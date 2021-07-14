using System;
using System.ComponentModel.DataAnnotations;

namespace Molendo.Models
{
    public class PhoneCode
    {
        [Required]
        public string phone { get; set; }
        [Required]
        public string code { get; set; }
    }
}
