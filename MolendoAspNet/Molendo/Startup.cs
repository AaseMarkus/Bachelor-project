using System;
using System.Text;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.Extensions.Logging;
using Microsoft.IdentityModel.Tokens;
using Molendo.DAL;
using Molendo.Auth;


namespace Molendo
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        // This method gets called by the runtime. Use this method to add services to the container.
        public void ConfigureServices(IServiceCollection services)
        {
            services.AddDbContext<MolendoContext>(opt =>
                opt.UseSqlServer(Configuration.GetConnectionString("SQLAuthProd")));
            services.AddScoped<IMolendoRepository, MolendoRepository>();
            services.AddDistributedMemoryCache();
            services.AddCors(options =>
            {
                options.AddDefaultPolicy(
                    builder =>
                    {
                        builder.WithOrigins("*")
                        .AllowAnyHeader()
                        .AllowAnyMethod();
                    });
            });
            services.AddControllers();

            // LoginService in-memory storage
            services.AddSingleton<ILoginService>(new LoginService());

            // JSON Web Tokens
            var key = Encoding.ASCII.GetBytes(TokenConfigs.Secret);
            services.AddAuthentication(auth =>
            {
                auth.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
                auth.DefaultSignInScheme = JwtBearerDefaults.AuthenticationScheme;
                auth.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
            })
                .AddJwtBearer(jwtBearer =>
                {
                    jwtBearer.RequireHttpsMetadata = false;
                    jwtBearer.SaveToken = true;

                    TokenValidationParameters tokenValidationParameters =
                        new TokenValidationParameters
                        {
                            ValidateIssuerSigningKey = true,
                            IssuerSigningKey = new SymmetricSecurityKey(key),
                            ValidateIssuer = false,
                            ValidateAudience = false,
                            ClockSkew = TimeSpan.Zero,
                        };

                    jwtBearer.TokenValidationParameters = tokenValidationParameters;
                });

        }

        // This method gets called by the runtime. Use this method to configure the HTTP request pipeline.
        public void Configure(IApplicationBuilder app, IWebHostEnvironment env,
            ILoggerFactory logger)
        {
            if (env.IsDevelopment())
            {
                app.UseDeveloperExceptionPage();
                logger.AddFile("Logs/MolendoLog.txt");
            }
            app.UseAuthentication();
            app.UseHttpsRedirection();
            app.UseRouting();
            app.UseCors();
            app.UseAuthorization();
            app.UseEndpoints(endpoints =>
            {
                endpoints.MapControllers();
            });
        }
    }
}
