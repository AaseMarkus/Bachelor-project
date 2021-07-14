using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Molendo.Models;
namespace Molendo.DAL
{
    public interface IMolendoRepository
    {
        Task<ActionResult> DeleteRoster(string opServId);
        Task<List<OperatorRoster>> GetRoster(string opServId, DateTime from);
        Task<List<Service>> GetServices();
        Task<Login> LookupOpServSMS(PhoneIdentity phoneIdentity);
        Task<ActionResult> SaveRoster(List<OperatorRosterDto> rosterList); 
        public Task<string> SendSMSCodeSP(string phone, string code);
    }
}
