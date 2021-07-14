using System;
using System.Threading.Tasks;
using Molendo.Models;

namespace Molendo.DAL

{
    public interface ILoginService
    {
        Task<bool> ValidateCode(string key,
            string code, out Login loginAttempt);
        void AddLogin(string key, string code,
            Login login);
    }
}
