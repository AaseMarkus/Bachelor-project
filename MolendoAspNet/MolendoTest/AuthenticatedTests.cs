using System;
using System.Net.Http;
using System.Threading.Tasks;
using NUnit.Framework;
using Newtonsoft.Json;
using System.Collections.Generic;
using System.Text;
using System.Net.Http.Headers;

namespace MolendoTests
{
    // Integration tests ie. involving all layers
    // Login happens on initation. If run in production, the SendSMSCode line
    // has to be commented out and the received code inserted beforehand in phoneId
    [TestFixture]
    public class AuthenticatedTests
    {
        string opServId;
        string jwt;
        Uri baseAddress = new Uri("http://18.191.107.144:88/molendo/");
        HttpClient client = new HttpClient();
        Dictionary<string, string> phoneId = new Dictionary<string, string>()
        {
            { "service", "Magic Circle" },
            { "phone", "12345678" },
            { "code", "5678" },
            //{ "service", "Data Svar" },
            //{ "phonenumber", "87654321" },
            //{ "code", "4321" }
        };

        // Used for all tests in class: HttpClient setup and authentication
        [OneTimeSetUp]
        public async Task Init()
        {
            // Update baseAddress to the current asp.net backend IP and port
            client.BaseAddress = baseAddress;
            var statusCode = await SendSMSCode(phoneId);
            var auth = await CheckSMSCode(phoneId);
            opServId = auth["id"];
            jwt = auth["token"];
            client.DefaultRequestHeaders.Authorization =
                new AuthenticationHeaderValue("Bearer", jwt);
            var response = await DeleteRoster();
        }

        [SetUp]
        public async Task ClearRoster()
        {
            await DeleteRoster();
        }

        // Called via SaveRoster
        // Code 400 when roster with null comment field given
        [Test]
        public async Task ControlRoster_CommentNull()
        {
            // arrange
            var url = "roster";
            var roster = CreateRoster_Valid();
            roster["fldOpRComment"] = null;
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster);

            // act
            var response = await PostRequest(url, rosters);
            var responseStr = await response.Content.ReadAsStringAsync();
            HttpResponse httpResponse = JsonConvert.DeserializeObject<HttpResponse>(responseStr);

            // assert
            Assert.AreEqual(400, (int)response.StatusCode);
            Assert.AreEqual("Roster with null comment field given", httpResponse.Message);
        }

        // Called via SaveRoster
        // Roster given with OpRId not found for opServId
        [Test]
        public async Task ControlRoster_UnknownOpRId()
        {
            // arrange
            var url = "roster";
            var roster = CreateRoster_Valid();
            roster.Add("fldOpRId", "id");
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster);

            // act
            var response = await PostRequest(url, rosters);
            var responseStr = await response.Content.ReadAsStringAsync();
            HttpResponse httpResponse = JsonConvert.DeserializeObject<HttpResponse>(responseStr);

            // assert
            Assert.AreEqual(400, (int) response.StatusCode);
            Assert.AreEqual("Roster with RID 'id' not found in DB", httpResponse.Message);
        }

        // Called via SaveRoster
        // Same start day and both have null end time
        [Test]
        public async Task ControlRoster_DuplicateEndTimes()
        {
            // arrange
            var url = "roster";
            var roster1 = CreateRoster_Valid();
            var roster2 = CreateRoster_Valid();
            roster1["fldOpREndTime"] = null;
            roster2["fldOpREndTime"] = null;
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster1);
            rosters.Add(roster2);

            // act
            var response = await PostRequest(url, rosters);
            var responseStr = await response.Content.ReadAsStringAsync();
            HttpResponse httpResponse = JsonConvert.DeserializeObject<HttpResponse>(responseStr);

            // assert
            Assert.AreEqual(400, (int)response.StatusCode);
            Assert.AreEqual("Two or more ongoing rosters given with " +
                        "identical dates and null end times", httpResponse.Message);
        }

        // Called via SaveRoster
        // DropIn roster when not today should result in Bad Request
        [Test]
        public async Task ControlRoster_InvalidStartEnd()
        {
            // arrange
            var url = "roster";
            var roster = CreateRoster_Valid();
            roster["fldOpRStartDate"] = GetDateString(1);
            roster["fldOpRStartTime"] = null;
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster);

            // act
            var response = await PostRequest(url, rosters);
            var responseStr = await response.Content.ReadAsStringAsync();
            HttpResponse httpResponse = JsonConvert.DeserializeObject<HttpResponse>(responseStr);

            // assert
            Assert.AreEqual(400, (int)response.StatusCode);
            Assert.AreEqual("Roster with invalid start/end datetime given",
                httpResponse.Message);
        }


        // Adds two valid rosters and deletes them, which should result in two 100 operations
        [Test]
        public async Task DeleteRoster_CorrectSPCounts()
        {
            // arrange
            var roster1 = CreateRoster_Valid();
            var roster2 = CreateRoster_Valid();
            roster2["fldOpRStartDate"] = GetDateString(2);
            roster2["fldOpREndDate"] = GetDateString(2);
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster1);
            rosters.Add(roster2);

            // act
            // Step 1: saving
            var url = "roster";
            var createResponse = await PostRequest(url, rosters);
            var createResponseStr = await createResponse.Content.ReadAsStringAsync();
            var inserts = JsonToDict(createResponseStr, "insert");
            // Step 2: deleting
            var deleteResponse = await DeleteRoster();
            var deleteResponseStr = await deleteResponse.Content.ReadAsStringAsync();
            var deletes = JsonToDict(deleteResponseStr);

            // assert
            Assert.AreEqual(201, (int) createResponse.StatusCode);
            Assert.AreEqual(2, inserts["100"]);
            Assert.AreEqual(2, deletes["100"]);
        }


        // opServId is slightly changed, which should trigger Unauthorized
        [Test]
        public async Task DeleteRoster_opServIdInvalid()
        {
            // arrange

            // act
            var response = await DeleteRoster(opServId + "a");

            // assert
            Assert.AreEqual(401, (int)response.StatusCode);

        }

        // opServId is slightly changed, which should trigger Unauthorized
        [Test]
        public async Task GetRoster_opServIdInvalid()
        {
            // arrange

            // act
            var response = await GetRoster(opServId + "a");

            // assert
            Assert.AreEqual(401, (int)response.StatusCode);
        }


        // Empty roster list, which should return Bad Request
        [Test]
        public async Task SaveRoster_EmptyRosterList()
        {
            // arrange
            var url = "roster";
            var rosters = new List<Dictionary<string, object>>();

            // act
            var response = await PostRequest(url, rosters);
            var responseStr = await response.Content.ReadAsStringAsync();
            HttpResponse httpResponse =
                JsonConvert.DeserializeObject<HttpResponse>(responseStr);

            // assert
            Assert.AreEqual(400, (int)response.StatusCode);
            Assert.AreEqual("Empty list. Nothing to save", httpResponse.Message);
        }


        // Roster list with one past and one future roster results in one successful insert
        [Test]
        public async Task SaveRoster_OneFutureRoster()
        {
            // arrange
            var roster1 = CreateRoster_Valid();
            var roster2 = CreateRoster_Valid();
            roster2["fldOpRStartDate"] = GetDateString(-1);
            roster2["fldOpREndDate"] = GetDateString(-1);
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster1);
            rosters.Add(roster2);

            // act
            var url = "roster";
            var response = await PostRequest(url, rosters);
            var responseStr = await response.Content.ReadAsStringAsync();
            var inserts = JsonToDict(responseStr, "insert");

            // assert
            Assert.AreEqual(201, (int) response.StatusCode);
            Assert.AreEqual(inserts["100"], 1);
        }

        // Simultaneous rosters result in one insert (100) and one "guard exists" (400)
        [Test]
        public async Task SaveRoster_SimultaneousRosters()
        {
            // arrange
            var roster1 = CreateRoster_Valid();
            var roster2 = CreateRoster_Valid();
            roster1["fldOpRStartTime"] = "06:15:00";
            roster1["fldOpREndTime"] = "09:15:00";
            roster2["fldOpRStartTime"] = "09:14:59";
            roster2["fldOpREndTime"] = "14:55:00";
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster1);
            rosters.Add(roster2);

            // act
            var url = "roster";
            var response = await PostRequest(url, rosters);
            var responseStr = await response.Content.ReadAsStringAsync();
            var inserts = JsonToDict(responseStr, "insert");

            // assert
            Assert.AreEqual(201, (int)response.StatusCode);
            Assert.AreEqual(inserts["100"], 1);
            Assert.AreEqual(inserts["400"], 1);
        }

        // opServIds in roster list are not identical, which should trigger Unauthorized
        [Test]
        public async Task SaveRoster_OpServIdsNotIdentical()
        {
            // arrange
            var url = "roster";
            var roster1 = CreateRoster_Valid();
            var roster2 = CreateRoster_Valid();
            roster2["fldOpServId"] = "not a valid opServId";
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster1);
            rosters.Add(roster2);

            // act
            var response = await PostRequest(url, rosters);
            var responseStr = await response.Content.ReadAsStringAsync();
            HttpResponse httpResponse =
                JsonConvert.DeserializeObject<HttpResponse>(responseStr);

            // assert
            Assert.AreEqual(401, (int)response.StatusCode);
        }


        // opServId is slightly changed, which should trigger Unauthorized
        [Test]
        public async Task SaveRoster_OpServIdInvalid()
        {
            // arrange
            var url = "roster";
            var roster = CreateRoster_Valid();
            roster["fldOpServId"] = opServId + "a";
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster);

            // act
            var response = await PostRequest(url, rosters);

            // assert
            Assert.AreEqual(401, (int)response.StatusCode);
        }


        private async Task<Dictionary<string, string>> CheckSMSCode(Dictionary<string, string> identity)
        {
            var url = "auth/check-sms-code/";
            var response = await PostRequest(url, identity);
            var responseStr = await response.Content.ReadAsStringAsync();
            var auth = JsonConvert.DeserializeObject<Dictionary<string, string>>(responseStr);
            return auth;
        }

        // Reusable roster
        private Dictionary<string, object> CreateRoster_Valid()
        {
            var roster = new Dictionary<string, object>();
            roster.Add("fldOpServID", opServId);
            roster.Add("fldOpRStartDate", GetDateString(1));
            roster.Add("fldOpRStartTime", "06:50:10");
            roster.Add("fldOpREndDate", GetDateString(1));
            roster.Add("fldOpREndTime", "07:30:10");
            roster.Add("fldOpRDropIn", false);
            roster.Add("fldOpRComment", "This is a comment");
            return roster;
        }



        private async Task<HttpResponseMessage> DeleteRoster()
        {
            var url = "roster/" + opServId;
            return await client.DeleteAsync(url);
        }

        private async Task<HttpResponseMessage> DeleteRoster(string opServId)
        {
            var url = "roster/" + opServId;
            return await client.DeleteAsync(url);
        }

        private async Task<HttpResponseMessage> GetRoster()
        {
            var url = "roster/" + opServId;
            return await client.GetAsync(url);
        }


        private async Task<HttpResponseMessage> GetRoster(string opServId)
        {
            var url = "roster/" + opServId;
            return await client.GetAsync(url);
        }


        // Helper method for deserializing DeleteRoster JSON message
        private Dictionary<string, int> JsonToDict(string json)
        {
                return JsonConvert.DeserializeObject<Dictionary<string, int>>(json);
        }


        // DRY helper method
        private string GetDateString(int addDays)
        {
            return DateTime.Now.AddDays(addDays).
                ToString("yyyy-MM-dd HH:mm:ss");
        }


        // Helper method for deserializing SaveRoster JSON message
        private Dictionary<string, int> JsonToDict(string json, string subDict)
        {
            return JsonConvert.DeserializeObject
                <Dictionary<string, Dictionary<string, int>>>(json)[subDict];
        }


        // DRY helper method
        private async Task<HttpResponseMessage> PostRequest(string url, Object obj)
        {
            var json = JsonConvert.SerializeObject(obj);
            var data = new StringContent(json, Encoding.UTF8, "application/json");
            return await client.PostAsync(url, data);
        }


        private async Task<int> SendSMSCode(Dictionary<string, string> identity)
        {
            var url = "auth/send-sms-code/";
            var request = await PostRequest(url, identity);
            return (int) request.StatusCode;
        }
    }
}
