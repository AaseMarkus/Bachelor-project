using System;
using System.ComponentModel.DataAnnotations;

namespace Molendo.Models
{
    public class PhoneIdentity
    {
        [Required]
        public string phone { get; set; }
        [Required]
        public string service { get; set; }
    }
}
