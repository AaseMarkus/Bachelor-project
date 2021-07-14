using System;
using System.Collections.Generic;
using System.Text.Json.Serialization;

namespace Molendo.Models
{
    public class OperatorAuth
    {
        [JsonPropertyName("id")]
        public string opServId { get; set; }
        [JsonPropertyName("phone")]
        public string phone { get; set; }
        [JsonPropertyName("token")]
        public string jwt { get; set; }
    }
}