using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System.Linq;
using System;
using Molendo.DAL;
using Molendo.Models;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;
using Molendo.Auth;

namespace Molendo.Controllers
{
    // Methods are sorted first by type (public, private) and then alphabetically by method name
    // Controller is using attribute routing with token replacement
    [ApiController]
    [Authorize]
    public class MolendoController : ControllerBase
    {
        private readonly IMolendoRepository _db;
        private readonly ILogger<MolendoController> _logger;
        private readonly ILoginService _loginService;
        

        public MolendoController(IMolendoRepository db,
            ILogger<MolendoController> logger, ILoginService loginService)
        {
            _db = db;
            _logger = logger;
            _loginService = loginService;
        }

        [HttpPost]
        [AllowAnonymous]
        [Route("[controller]/auth/check-sms-code")]
        public async Task<ActionResult> CheckSMSCode(PhoneCode phoneCode)
        {
            Login loginAttempt;
            await _loginService.ValidateCode(phoneCode.phone, phoneCode.code,
                out loginAttempt);

            if (loginAttempt != null)
            {
                var operatorAuth = new OperatorAuth();
                operatorAuth.phone = phoneCode.phone;
                operatorAuth.opServId = loginAttempt.opServId;
                // These claims will be stored in the JWT and can be used
                // later by this server and by the client
                var claims = new List<Claim>
                {
                    new Claim("opservid", loginAttempt.opServId),
                    new Claim("service", loginAttempt.service.fldServID),
                    new Claim("theme", loginAttempt.service.fldServCssTheme),
                    new Claim("minhour", loginAttempt.service.fldServMinHour.ToString()),
                    new Claim("maxhour", loginAttempt.service.fldServMaxHour.ToString())

                };
                operatorAuth.jwt = TokenUtilities.GetToken(claims);

                return new JsonResult(operatorAuth);
            }
            return new Models.HttpResponse("Incorrect or expired code").
                    ToJsonResult(401);
        }

        
        [HttpDelete]
        [Route("[controller]/roster/{opservid}")]
        public async Task<ActionResult> DeleteRoster(string opServId)
        {
            var identity = User.Identity as ClaimsIdentity;
            if (IsAuthorized(identity, opServId))
                return await _db.DeleteRoster(opServId);

            return new UnauthorizedResult();
        }


        [HttpGet]
        [Route("[controller]/roster/{opservid}")]
        public async Task<ActionResult> GetRoster(string opServId)
        {
            var identity = User.Identity as ClaimsIdentity;
            if (IsAuthorized(identity, opServId))
            {
                var dateTime = DateTime.Now.AddDays(-7);
                List<OperatorRoster> roster = await _db.GetRoster(opServId, dateTime);
                if (roster != null) return new JsonResult(roster);

                return new BadRequestResult();
            }
            return new UnauthorizedResult();
        }

        
        [HttpGet]
        [AllowAnonymous]
        [Route("[controller]/servertime")]
        public string GetServerDatetime()
        {
            return DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss");
        }

        
        [HttpGet]
        [AllowAnonymous]
        [Route("[controller]/tables/service")]
        public async Task<ActionResult> GetServices()
        {
            List<Service> services = await _db.GetServices();
            if (services != null) return Ok(services);
            return new Models.HttpResponse(
                "Service table could not be retrieved").ToJsonResult(400);
        }

        
        [HttpPost]
        [Route("[controller]/roster")]
        public async Task<ActionResult> SaveRoster(List<OperatorRosterDto> rosterList)
        {
            // Return early if list does not have any future rosters
            rosterList = rosterList.Where(
                r => !r.fldOpRLogOut.GetValueOrDefault(false)).ToList();
            if (rosterList.Count == 0)
                return new Models.HttpResponse("Empty list. Nothing to save").
                    ToJsonResult(400);

            // Determining whether the operator is authorized
            var identity = User.Identity as ClaimsIdentity;
            if (identity != null)
            {
                // Confirming that fldOpServId's in list are identical
                var opServId = rosterList.ElementAt(0).fldOpServID;
                for (int i = 0; i < rosterList.Count; i++)
                {
                    if (!rosterList.ElementAt(i).fldOpServID.Equals(opServId))
                        return new UnauthorizedResult();
                }

                // Confirming that fldOpServId in claim and list are identical
                var claim = identity.FindFirst("opservid").Value;
                if (opServId.Equals(claim))
                {
                    ActionResult result = await _db.SaveRoster(rosterList);
                    return result;
                }
            }
            return new UnauthorizedResult();
        }

        [HttpPost]
        [AllowAnonymous]
        [Route("[controller]/auth/send-sms-code")]
        public async Task<JsonResult> SendSMSCode(PhoneIdentity phoneIdentity)
        {
            Login login = await _db.LookupOpServSMS(phoneIdentity);
            if (login != null)
            {
                
                var code = GenerateCode(4);
                var sendingResult = await _db.SendSMSCodeSP(phoneIdentity.phone, code);
                if (!sendingResult.ToLower().Contains("ok"))
                    return new Models.HttpResponse(sendingResult).ToJsonResult(400);
                _logger.LogInformation("Generated code: " + code);
                
                /*
                // Temporarily using last four digits for code
                var numberLength = phoneIdentity.phone.ToString().Length;
                var code = phoneIdentity.phone.ToString().Substring(numberLength - 4);
                */
                _loginService.AddLogin(phoneIdentity.phone, code, login);
                return new Models.HttpResponse("Psst! " + code).ToJsonResult(200);
            }
            else
            {
                return new Models.HttpResponse("Operator service not found").
                    ToJsonResult(404);
            }
        }

        private static string GenerateCode(int length)
        {
            Random random = new Random();
            var max = string.Concat(Enumerable.Repeat("9", length));
            var code = random.Next(int.Parse(max)).ToString().PadLeft(length, '0');
            return code;
        }

        // Helper method: controls given opServId vs. that stored in token claims
        private static bool IsAuthorized(ClaimsIdentity claimsIdentity,
            string opServId)
        {
            if (claimsIdentity != null)
            {
                var claim = claimsIdentity.FindFirst("opservid").Value;
                if (opServId.Equals(claim))
                    return true;
            }
            return false;
        }

    }
}
