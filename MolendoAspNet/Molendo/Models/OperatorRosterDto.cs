using System;
using System.ComponentModel.DataAnnotations;
using System.Globalization;

namespace Molendo.Models
{
    public class OperatorRosterDto
    {
        [Key]
        public string fldOpRID { get; set; }
        [Required]
        public string fldOpServID { get; set; }
        [Required]
        public string fldOpRStartDate { get; set; }
        [Required]
        public string fldOpREndDate { get; set; }
        public bool? fldOpRDropIn { get; set; }
        public bool? fldOpRLogOn { get; set; }
        public bool? fldOpRLogOut { get; set; }
        public bool? fldOpROnHold { get; set; }
        public bool? fldOpROnPhone { get; set; }
        public string fldOpRComment { get; set; }
        public string fldOpRStartTime { get; set; }
        public string fldOpREndTime { get; set; }

        public DateTime ParseStartDate()
        {
            return DateTime.Parse(fldOpRStartDate);
        }

        public string GetStartDateString()
        {
            if (fldOpRStartDate == null) return null;
            return ParseStartDate().ToString("yyyy-MM-dd");
        }

        public string GetEndDateString()
        {
            if (fldOpREndDate == null) return null;
            return ParseEndDate().ToString("yyyy-MM-dd");
        }

        public DateTime ParseEndDate()
        {
            return DateTime.Parse(fldOpREndDate);
        }

        public DateTime ParseStartTime()
        {
            return DateTime.Parse(fldOpRStartTime);
        }

        public string GetStartTimeString()
        {
            if (fldOpRStartTime == null) return null;
            return ParseStartTime().ToString("HH:mm:ss");
        }

        public DateTime ParseEndTime()
        {
            return DateTime.Parse(fldOpREndTime);
        }

        public string GetEndTimeString()
        {
            if (fldOpREndTime == null) return null;
            return ParseEndTime().ToString("HH:mm:ss");
        }

        public bool IsStartDateToday()
        {
            DateTime now = DateTime.Now;
            DateTime startDate = ParseStartDate();
            return now.Year == startDate.Year && now.Month == startDate.Month
                && now.Day == startDate.Day;
        }

        public bool IsStartEndValid()
        {
            try
            {
                // null dates not acceptable
                DateTime startDate = ParseStartDate();
                DateTime endDate = ParseEndDate();
                // start date must be today if start time is null
                if (!IsStartDateToday() && fldOpRStartTime == null)
                    return false;
                // times can be null
                if (fldOpRStartTime != null) ParseStartTime();
                if (fldOpREndTime != null) ParseEndTime();
                return true;
            }
            catch
            {
                return false;
            }
        }

        // Start time set null if in the past
        public void CheckIfNowRoster()
        {
            if (fldOpRStartTime == null) return;
            DateTime now = DateTime.Now;
            DateTime startTime = ParseStartTime();
            if (IsStartDateToday() &&
                (now.TimeOfDay.CompareTo(startTime.TimeOfDay) >= 0))
                fldOpRStartTime = null;
        }

        public OperatorRosterDto Clone()
        {
            return new OperatorRosterDto
            {
                fldOpRID = this.fldOpRID,
                fldOpServID = this.fldOpServID,
                fldOpRStartDate = this.fldOpRStartDate,
                fldOpRStartTime = this.fldOpRStartTime,
                fldOpREndDate = this.fldOpREndDate,
                fldOpREndTime = this.fldOpREndTime,
                fldOpRDropIn = this.fldOpRDropIn,
                fldOpRComment = this.fldOpRComment,
                fldOpRLogOn = this.fldOpRLogOn,
                fldOpRLogOut = this.fldOpRLogOut,
                fldOpROnHold = this.fldOpROnHold,
                fldOpROnPhone = this.fldOpROnPhone,
            };
        }
    }
}
