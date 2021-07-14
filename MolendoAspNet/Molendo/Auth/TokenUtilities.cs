using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;
using Molendo.Models;

namespace Molendo.Auth
{
    public class TokenUtilities
    {
        public static string GetToken(List<Claim> claims)
        {
            var signingKey = new SymmetricSecurityKey(Encoding.
                UTF8.GetBytes(TokenConfigs.Secret));
            var credentials = new SigningCredentials(signingKey,
                SecurityAlgorithms.HmacSha256Signature);
            var token = new JwtSecurityToken(TokenConfigs.Issuer,
                TokenConfigs.Audience, claims, expires: DateTime.Now.AddDays(7),
                signingCredentials: credentials);
            var tokenValue = new JwtSecurityTokenHandler().WriteToken(token);

            return tokenValue;
        }
    }
}
