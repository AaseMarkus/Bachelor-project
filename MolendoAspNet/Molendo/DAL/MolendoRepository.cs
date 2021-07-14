using System;
using System.Data;
using System.Collections.Generic;
using System.Data.Common;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Data.SqlClient;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;
using Molendo.Models;

namespace Molendo.DAL
{
    // Methods are sorted first by type (public, private) and then alphabetically by method name
    public class MolendoRepository : IMolendoRepository
    {
        private readonly MolendoContext _db;
        private ILogger<MolendoRepository> _logger;

        public MolendoRepository(MolendoContext db,
            ILogger<MolendoRepository> logger)
        {
            _db = db;
            _logger = logger;
        }


        private async Task<JsonResult> ControlRoster(
            List<OperatorRosterDto> rosters)
        {
            // Checking formats
            for (int i = 0; i < rosters.Count; i++)
            {
                OperatorRosterDto r = rosters.ElementAt(i);
                // a. Are all datetimes valid ?
                if (!r.IsStartEndValid())
                {
                    var msg = "Roster with invalid start/end datetime given";
                    return new Models.HttpResponse(msg).ToJsonResult(400);
                }

                // b. Is Comment-field defined?
                if (r.fldOpRComment == null)
                {
                    var msg = "Roster with null comment field given";
                    return new Models.HttpResponse(msg).ToJsonResult(400);
                }
            }
            // 2. Checking for same-day null end-time duplicates
            List<OperatorRosterDto> rostersWithNull = rosters.Where(
                r => r.fldOpREndTime == null && !r.fldOpRLogOut.GetValueOrDefault(false)).ToList();
            if (rostersWithNull.Count > 0)
            {
                var duplicates = rostersWithNull.GroupBy(r => r.ParseStartDate()).
                    Where(r => r.Count() > 1).ToList();
                if (duplicates.Count > 0)
                {
                    var msg = "Two or more ongoing rosters given with " +
                        "identical dates and null end times";
                    return new Models.HttpResponse(msg).ToJsonResult(400);
                }
            }

            // 3. Checking ID validity
            var opServId = rosters.ElementAt(0).fldOpServID;
            // a. Does the operator service exist?
            // That they are identical has already been confirmed
            try
            {
                OperatorService opService = await _db.OperatorService.
                    SingleAsync(s => s.fldOpServID.ToString() == opServId);
            }
            catch
            {
                return new Models.HttpResponse("Invalid operator service given").
                    ToJsonResult(400);
            }

            // b. Does the old roster exist in the database?
            for (int i = 0; i < rosters.Count; i++)
            {
                var r = rosters.ElementAt(i);
                if (r.fldOpRID == null) continue; // check unecessary
                try
                {
                    await _db.OperatorRoster.SingleAsync(
                        r2 => r2.fldOpRID.ToString() == r.fldOpRID);
                }
                catch
                {
                    return new Models.HttpResponse(
                        "Roster with RID '" + r.fldOpRID + "' not found in DB").
                        ToJsonResult(400);
                }
            }
            // Everything checks out
            return null;

        }


        public async Task<ActionResult> DeleteRoster(string opServId)
        {
            try
            {
                OperatorService opService = await _db.OperatorService.
                    SingleAsync(s => s.fldOpServID.ToString().Equals(opServId));
            }
            catch (Exception e)
            {
                // add logger call here
                return new Models.HttpResponse("Invalid operator service given").ToJsonResult(400);
            }
            List<OperatorRoster> roster = await GetRoster(opServId);
            if (roster.Count == 0)
            {
                var msg = "Operator service ID has no future rosters";
                return new Models.HttpResponse(msg).ToJsonResult(400);
            }
            var results = new Dictionary<int, int>();
            int code = -1;
            roster.ForEach(r =>
            {
                code = DeleteRosterSP(r.fldOpRID.ToString());
                if (!results.ContainsKey(code)) results.Add(code, 0);
                results[code]++;
            });
            var json = Newtonsoft.Json.JsonConvert.SerializeObject(results);
            return new Models.HttpResponse(json).ToContentResult(200);
        }


        public int DeleteRosterSP(string opRId)
        {
            List<SqlParameter> parameters = new List<SqlParameter>
            { 
                // Create parameters    
                new SqlParameter { ParameterName = "@OpRId", Value = opRId, Direction = ParameterDirection.Input },
                new SqlParameter { ParameterName = "@RemoveStatus", SqlDbType = SqlDbType.Int, Direction = ParameterDirection.ReturnValue }
            };
            DbCommand cmd = _db.Database.GetDbConnection().CreateCommand();
            cmd.CommandType = CommandType.StoredProcedure;
            cmd.Parameters.AddRange(parameters.ToArray());
            cmd.CommandText = "dbo.spRemoveOperatorROSTER";
            var removeStatus = -1;
            try
            {
                cmd.Connection.Open();
                removeStatus = (int) cmd.ExecuteScalar();
            }
            catch (Exception e)
            {
                // add logger call here
            }
            finally
            {
                cmd.Connection.Close();
            }
            return removeStatus;
        }


        // Get future rosters from database
        private async Task<List<OperatorRoster>> GetRoster(string opServId)
        {
            var rosterList = await _db.OperatorRoster.ToListAsync();
            return rosterList.Where(r => opServId.Equals(r.fldOpServID.ToString()) &&
                !r.fldOpRLogOut.GetValueOrDefault(false)).ToList();
        }


        public async Task<List<OperatorRoster>> GetRoster(string opServId, DateTime from)
        {
            ;
            var roster = await _db.OperatorRoster.Where(r =>
                r.fldOpServID.ToString().Equals(opServId) &&
                r.fldOpRStart >= from).ToListAsync();
            return roster;
        }

        public async Task<List<Service>> GetServices()
        {
            List<Service> services = await _db.Service.ToListAsync();
            return services;
        }


        public int InsertRosterSP(OperatorRosterDto roster)
        {
            List<SqlParameter> parameters = new List<SqlParameter>
            { 
                // Create parameters    
                new SqlParameter { ParameterName = "@OpServId", Value = roster.fldOpServID, Direction = ParameterDirection.Input },
                new SqlParameter { ParameterName = "@OpRStartDate", Value = roster.GetStartDateString(), Direction = ParameterDirection.Input },
                new SqlParameter { ParameterName = "@OpRStartTime", Value = (object) roster.GetStartTimeString() ?? DBNull.Value, Direction = ParameterDirection.Input },
                new SqlParameter { ParameterName = "@OpREndDate", Value = roster.GetEndDateString(), Direction = ParameterDirection.Input },
                new SqlParameter { ParameterName = "@OpREndTime", Value = (object) roster.GetEndTimeString() ?? DBNull.Value, Direction = ParameterDirection.Input },
                new SqlParameter { ParameterName = "@OpRDropIn", Value = Convert.ToInt32(roster.fldOpRDropIn), Direction = ParameterDirection.Input },
                new SqlParameter { ParameterName = "@OpRComment", Value = roster.fldOpRComment, Direction = ParameterDirection.Input },
                new SqlParameter { ParameterName = "@InsertStatus", SqlDbType = SqlDbType.Int, Direction = ParameterDirection.ReturnValue }
            };
            DbCommand cmd = _db.Database.GetDbConnection().CreateCommand();
            cmd.CommandType = CommandType.StoredProcedure;
            cmd.Parameters.AddRange(parameters.ToArray());
            cmd.CommandText = "dbo.spAddOperatorROSTER";
            var insertStatus = -1;
            try
            {
                cmd.Connection.Open();
                cmd.ExecuteNonQuery();
                insertStatus = (int)cmd.Parameters["@InsertStatus"].Value;
            }
            catch (Exception e)
            {
                // add logger call here
            }
            finally
            {
                cmd.Connection.Close();
            }
            return insertStatus;
        }


        public async Task<Login> LookupOpServSMS(PhoneIdentity phoneIdentity)
        {
            List<OperatorPhones> phones = await _db.OperatorPhones.Where(p =>
            p.fldOpPNo.Equals(phoneIdentity.phone)).ToListAsync();
            foreach (OperatorPhones p in phones)
            {
                if (p.fldOpServ.fldServID.Equals(phoneIdentity.service))
                {
                    var login = new Login(p.fldOpServID.ToString(),
                        p.fldOpServ.fldServ);
                    return login;
                }
            };
            // add logger call here

            return null;
        }


        public async Task<ActionResult> SaveRoster(List<OperatorRosterDto> rosters)
        {
            // Control phase: checking if data is valid
            var jsonResult = await ControlRoster(rosters);
            if (jsonResult != null)
            {
                // add logger call here
                return jsonResult;
            }
            var results = new Dictionary<string, Dictionary<int, int>>();

            // Delete phase: removing DB rosters not found in new list
            var del_results = new Dictionary<int, int>();
            // DB rosters to keep
            var rids = new List<string>();
            var opServId = rosters.ElementAt(0).fldOpServID;
            // Rosters with LogOut == true are not made available for deletion
            var RostersOld = await GetRoster(opServId);

            rosters.ForEach(r => { if (r.fldOpRID != null) rids.Add(r.fldOpRID); });
            int code;
            RostersOld.ForEach(r =>
            {
                if (!rids.Contains(r.fldOpRID.ToString()))
                // DB roster not found in incoming list, so it is deleted
                {
                    code = DeleteRosterSP(r.fldOpRID.ToString());
                    if (!del_results.ContainsKey(code)) del_results.Add(code, 0);
                    del_results[code]++;
                }
            });
            results.Add("delete", del_results);

            // Insert phase: adding rosters that don't have RIDs
            var ins_results = new Dictionary<int, int>();
            rosters.ForEach(r =>
            {
                if (r.fldOpRID == null)
                {
                    r.CheckIfNowRoster();  // Start time set to null if passed
                    code = InsertRosterSP(r);
                    if (!ins_results.ContainsKey(code)) ins_results.Add(code, 0);
                    ins_results[code]++;
                }
            });
            results.Add("insert", ins_results);
            // Save operation done
            var json = Newtonsoft.Json.JsonConvert.SerializeObject(results);
            return new Models.HttpResponse(json).ToContentResult(201);
        }

        
        public async Task<string> SendSMSCodeSP(string phone, string code)
        {
            var msg = "Your single use code is "+code;
            List<SqlParameter> parameters = new List<SqlParameter>
            { 
                // Create parameters    
                new SqlParameter { ParameterName = "@ANumber", Value = phone, Direction = ParameterDirection.Input },
                new SqlParameter { ParameterName = "@Text", Value = msg, Direction = ParameterDirection.Input },
                new SqlParameter { ParameterName = "@InsertStatus", SqlDbType = SqlDbType.Text, Direction = ParameterDirection.ReturnValue }
            };
            DbCommand cmd = _db.Database.GetDbConnection().CreateCommand();
            cmd.CommandType = CommandType.StoredProcedure;
            cmd.Parameters.AddRange(parameters.ToArray());
            cmd.CommandText = "dbo.spSendSMS";
            var sendingResult = "";
            try
            {
                cmd.Connection.Open();
                cmd.ExecuteNonQuery();
                sendingResult = (string) cmd.ExecuteScalar();
            }
            catch (Exception e)
            {
                // add logger call here
            }
            finally
            {
                cmd.Connection.Close();
            }
            return sendingResult;
        }


        // Temporary helper method that does the same as Molendo's 1-minute SP routine
        public async Task<bool> UpdateBooleanVars()
        {
            var allRosters = await _db.OperatorRoster.ToListAsync();
            var now = DateTime.Now;
            allRosters.ForEach(r =>
            {
                r.fldOpRLogOn = r.fldOpRStart < DateTime.Now;
                if (r.fldOpRLogOut == null)
                {
                    r.fldOpRLogOut = false;
                }
                else if (r.fldOpREndTime != null && r.fldOpRLogOut != true)
                {
                    r.fldOpRLogOut = r.fldOpREnd < now;
                }
                r.fldOpROnHold = false;
                r.fldOpROnPhone = false;
            });
            await _db.SaveChangesAsync();
            return true;
        }
    }
}