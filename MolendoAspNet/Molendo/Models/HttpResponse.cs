using System.Text.Json;
using System.Text.Json.Serialization;
using Microsoft.AspNetCore.Mvc;

namespace Molendo.Models
{

    public class HttpResponse
    {
        public string Message { get; set; }


        public HttpResponse() { }

        public HttpResponse(string message)
        {
            Message = message;
        }

        public JsonResult ToJsonResult(int code)
        {
            return new JsonResult(this)
            {
                StatusCode = code
            };
        }

        public ContentResult ToContentResult(int code)
        {
            return new ContentResult
            {
                Content = Message,
                StatusCode = code,
                ContentType = "application/json"
            };
        }
        
    }
}

