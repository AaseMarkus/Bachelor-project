using System;
using Xunit;
using Molendo.Models;

namespace MolendoTests
{
    public class OperatorRosterDtoTest
    {

        OperatorRosterDto rosterAllFields_Valid = new OperatorRosterDto
        {
            fldOpServID = "76c0b9fd-8145-4a41-94da-8e3b27451133",
            fldOpRStartDate = "2021-03-01 20:10:00",
            fldOpRStartTime = "06:50:10",
            fldOpREndDate = "2021-03-01 20:10:00",
            fldOpREndTime = "07:30:10",
            fldOpRDropIn = false,
            fldOpRComment = "This is a comment",
        };

        // All fields have valid values
        [Fact]
        public void IsStartEndValid_Valid()
        {
            // arrange
            var roster = rosterAllFields_Valid;

            // act
            bool valid = roster.IsStartEndValid();

            // assert
            Assert.True(valid);
        }

        [Fact]
        public void IsStartEndValid_StartEndTimeNull()
        {
            // arrange
            var roster = rosterAllFields_Valid.Clone();
            roster.fldOpRStartDate = DateTime.Now.ToString();
            roster.fldOpRStartTime = null;
            roster.fldOpREndTime = null;

            // act
            bool valid = roster.IsStartEndValid();

            // assert
            Assert.True(valid);
        }

        [Fact]
        public void IsStartDateToday_Valid()
        {
            // arrange
            var roster = rosterAllFields_Valid.Clone();
            roster.fldOpRStartDate = DateTime.Now.ToString();
            roster.fldOpRStartTime = null;

            // act
            bool valid = roster.IsStartDateToday();

            // assert
            Assert.True(valid);
        }

        [Fact]
        public void IsStartDateToday_False()
        {
            // arrange
            var roster = rosterAllFields_Valid.Clone();
            roster.fldOpRStartDate = DateTime.Now.AddDays(1).ToString();
            roster.fldOpRStartTime = null;

            // act
            bool today = roster.IsStartDateToday();

            // assert
            Assert.False(today);
        }

        [Fact]
        public void IsStartEndValid_NullDates_Invalid()
        {
            // arrange
            var roster = rosterAllFields_Valid.Clone();
            roster.fldOpRStartDate = null;
            roster.fldOpREndDate = null;

            // act
            bool valid = roster.IsStartEndValid();

            // assert
            Assert.False(valid);
        }


        [Fact]
        public void ParseStartDate_Exception()
        {
            // arrange
            var roster = rosterAllFields_Valid.Clone();
            roster.fldOpRStartDate = "2021-02-31";

            // act

            // assert
            var exception = Assert.Throws<FormatException>(
                () => roster.ParseStartDate());   
        }


        [Fact]
        public void ParseStartDate_Correct()
        {
            // arrange
            var roster = rosterAllFields_Valid.Clone();
            roster.fldOpRStartDate = "2021-03-31 10:10:20";

            // act
            var startDate = roster.GetStartDateString();

            // assert
            Assert.Equal("2021-03-31", startDate);
        }

    }
}
