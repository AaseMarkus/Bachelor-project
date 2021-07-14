using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace Molendo.DAL
{
    public class LoginService : ILoginService
    {
        // Key can for example be phone number, email address
        private IDictionary<string, (string code, Login login)> _logins =
            new Dictionary<string, (string code, Login login)>();

        public LoginService() {}

        public Task<bool> ValidateCode(string key,
            string code, out Login loginAttempt)
        {
            loginAttempt = null;
            if (_logins.ContainsKey(key))
            {
                // Return early if code has expired
                if (_logins[key].login.HasExpired())
                {
                    return Task.FromResult(false);
                }
                // Determine if code is correct
                else if (code == _logins[key].code)
                {
                    loginAttempt = _logins[key].login;
                    _logins.Remove(key);
                    return Task.FromResult(true);
                }
                // TODO find out how to control against brute force attacks
                else
                {
                    _logins[key].login.IncrementAttempts();
                }
            }
            return Task.FromResult(false);
        }

        public void AddLogin(string key, string code,
            Login login)
        {
            var exists = _logins.ContainsKey(key);
            if (exists) _logins[key] = (code, login);
            else _logins.Add(key, (code, login));
        }
    }
}
