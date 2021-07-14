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
    // These tests should confirm that an unauthenticated user is unable to
    // modify rosters belonging to a valid opServId. A valid opServId and JWT
    // is obtained in the set-up phase and then the JWT value is edited slightly.
    // If run in production, the SendSMSCode line has to be commented out
    // and the received code inserted beforehand in phoneId
    [TestFixture]
    public class UnauthenticatedTests
    {
        string opServId;
        string invalidJwt;
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

        // HttpClient setup
        [OneTimeSetUp]
        public async Task Init()
        {
            // Update baseAddress to the current asp.net backend IP and port
            client.BaseAddress = baseAddress;
            var statusCode = await SendSMSCode(phoneId);
            var auth = await CheckSMSCode(phoneId);
            opServId = auth["id"];
            var jwt = auth["token"];
            invalidJwt = jwt.Substring(0, 50) + "Q" + jwt.Substring(51);
            client.DefaultRequestHeaders.Authorization =
                new AuthenticationHeaderValue("Bearer", invalidJwt);
            var response = await DeleteRoster();
        }

        // Correct service name and number with incorrect code should give Unauthorized
        [Test]
        public async Task CheckSMSCode_WrongCode()
        {
            // arrange
            phoneId["code"] = "4567";

            // act
            int statusCode = await SendSMSCode(phoneId);
            var url = "auth/check-sms-code";
            var response = await PostRequest(url, phoneId);

            // assert
            Assert.AreEqual(200, statusCode);
            Assert.AreEqual(401, (int) response.StatusCode);
        }


        // Valid opServId with invalid JWT should result in Unauthorized
        [Test]
        public async Task DeleteRoster_InvalidToken()
        {
            // arrange

            // act
            var response = await DeleteRoster();

            // assert
            Assert.AreEqual(401, (int)response.StatusCode);
        }


        // Valid opServId but header has no JWT, which should result in Unauthorized
        [Test]
        public async Task DeleteRoster_MissingToken()
        {
            // arrange
            var url = "roster/" + opServId;
            var client = new HttpClient();
            client.BaseAddress = baseAddress;

            // act
            var response = await client.DeleteAsync(url);

            // assert
            Assert.AreEqual(401, (int)response.StatusCode);
        }


        // Valid opServId with invalid JWT should result in Unauthorized
        [Test]
        public async Task GetRoster_InvalidToken()
        {
            // arrange

            // act
            var response = await GetRoster();

            // assert
            Assert.AreEqual(401, (int)response.StatusCode);
        }

        // Valid opServId but header has no JWT, which should result in Unauthorized
        [Test]
        public async Task GetRoster_MissingToken()
        {
            // arrange
            var url = "roster/" + opServId;
            var client = new HttpClient();
            client.BaseAddress = baseAddress;

            // act
            var response = await client.GetAsync(url);

            // assert
            Assert.AreEqual(401, (int)response.StatusCode);
        }


        // Valid opServId with invalid JWT should result in Unauthorized
        [Test]
        public async Task SaveRoster_InvalidToken()
        {
            // arrange
            var roster = CreateRoster_Valid();
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster);

            // act
            var url = "roster";
            var response = await PostRequest(url, rosters);
           
            // assert
            Assert.AreEqual(401, (int)response.StatusCode);
        }

        // Valid opServId but header has no JWT, which should result in Unauthorized
        [Test]
        public async Task SaveRoster_MissingToken()
        {
            // arrange
            var roster = CreateRoster_Valid();
            var rosters = new List<Dictionary<string, object>>();
            rosters.Add(roster);
            var url = "roster";
            var client = new HttpClient();
            client.BaseAddress = baseAddress;
            var json = JsonConvert.SerializeObject(rosters);
            var data = new StringContent(json, Encoding.UTF8, "application/json");

            // act
            var response = await client.PostAsync(url, data);

            // assert
            Assert.AreEqual(401, (int)response.StatusCode);
        }

        [Test]
        public async Task SendSMSCode_NotFound()
        {
            // arrange
            phoneId["service"] = "Data Svar";

            // act
            var url = "auth/send-sms-code/";
            var response = await PostRequest(url, phoneId);
            var responseStr = await response.Content.ReadAsStringAsync();
            HttpResponse httpResponse =
                JsonConvert.DeserializeObject<HttpResponse>(responseStr);

            // assert
            Assert.AreEqual(404, (int)response.StatusCode);
            Assert.AreEqual("Operator service not found", httpResponse.Message);
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
