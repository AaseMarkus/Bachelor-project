using System;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace Molendo.DAL
{
    [Table("tblOPERATOR")]
    public class Operator
    {
        #nullable enable
        [Key]
        public int fldOpID { get; set; }
        public string? fldOpNick { get; set; }
        public string? fldOpFirstName { get; set; }
        public string? fldOpLastName { get; set; }
        public string? fldOpAddress { get; set; }
        public string? fldOpPostCode { get; set; }
        public string? fldOpPostOffice { get; set; }
        public string? fldCountryID { get; set; }
        public string? fldOpStatus { get; set; }
        public DateTime? fldOpStartDate { get; set; }
        public DateTime? fldOpEndDate { get; set; }
        public string? fldOpEMail { get; set; }
        public string? fldOpAccount { get; set; }
    }

    [Table("tblOPERATORPHONES")]
    public class OperatorPhones
    {
        #nullable enable        
        [Key]
        public Guid fldOpPID { get; set; }
        public Guid fldOpServID { get; set; }
        public string? fldOpPNo { get; set; }
        public string? fldOpPStatus { get; set; }
        public int? fldOpPSortOrder { get; set; }
        #nullable disable
        public virtual OperatorService fldOpServ { get; set; }
    }

    [Table("tblOPERATORSERVICE")]
    public class OperatorService
    {
        #nullable enable
        [Key]
        public Guid fldOpServID { get; set; }
        public int fldOpID { get; set; }
        public string? fldServID { get; set; }
        public string? fldOpServHtmlTitle { get; set; }
        public string? fldOpServHtmlIngress { get; set; }
        public string? fldOpServHtmlShortText { get; set; }
        public string? fldOpServHtmlLongText { get; set; }
        #nullable disable
        public virtual Operator fldOp { get; set; }
        public virtual Service fldServ { get; set; }
    }

    
    [Table("tblOPERATORROSTER")]
    public class OperatorRoster
    {
        #nullable enable
        [Key]
        public Guid fldOpRID { get; set; }
        public Guid fldOpServID { get; set; }
        public DateTime? fldOpRStartDate { get; set; }
        public DateTime? fldOpREndDate { get; set; }
        public DateTime? fldOpRStartTime { get; set; }
        public DateTime? fldOpREndTime { get; set; }
        public bool? fldOpRDropIn { get; set; }
        public bool? fldOpRLogOn { get; set; }
        public bool? fldOpRLogOut { get; set; }
        public bool? fldOpROnHold { get; set; }
        public bool? fldOpROnPhone { get; set; }
        public string? fldOpRComment { get; set; }
        public DateTime? fldOpRStart { get; set; }
        public DateTime? fldOpREnd { get; set; }
        #nullable disable
        public virtual OperatorService fldOpServ { get; set; }

    }

    [Table("tblSERVICE")]
    public class Service
    {
        [Key]
        public string fldServID { get; set; }
        public string fldServCssTheme { get; set; }
        public int fldServMinHour { get; set; }
        public int fldServMaxHour { get; set; }
    }

    public class MolendoContext : DbContext
    {
        public MolendoContext(DbContextOptions<MolendoContext> options) :
            base(options)
        { }

        public virtual DbSet<Operator> Operator { get; set; }
        public virtual DbSet<OperatorPhones> OperatorPhones { get; set; }
        public virtual DbSet<OperatorService> OperatorService { get; set; }
        public virtual DbSet<OperatorRoster> OperatorRoster { get; set; }
        public virtual DbSet<Service> Service { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseLazyLoadingProxies();
        }
    }

}
