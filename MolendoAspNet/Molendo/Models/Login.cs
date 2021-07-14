using System;
using Molendo.DAL;

namespace Molendo
{
    public class Login
    {
        public string opServId { get; set; }
        public Service service { get; set; }
        public int attempts { get; set; }
        public DateTime dateTime { get; set; }

        public Login(string opServId, Service service)
        {
            this.opServId = opServId;
            this.service = service;
            attempts = 0;
            dateTime = DateTime.Now;

        }

        public bool HasExpired()
        {
            return (DateTime.Now - dateTime).Minutes > 10;
        }

        public void IncrementAttempts()
        {
            attempts++;
        }
    }

}
