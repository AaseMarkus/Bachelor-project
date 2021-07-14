package oslomet.bachelorprosjekt.molendoansatt;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;
import oslomet.bachelorprosjekt.molendoansatt.objects.RegistrationHandler;

public class RegistrationHandlerTest {
    RegistrationHandler registrationHandler;
    Registration reg1, reg2;

    @Before
    public void setup() {
        registrationHandler = new RegistrationHandler();

        LocalDate date = LocalDate.of(2021, 3, 8);
        LocalTime startTime = LocalTime.of(10, 0);
        LocalTime endTime = LocalTime.of(15, 0);

        reg1 = new Registration("fldOpServId1", date, date, startTime,
                endTime, false, false, false);

        reg2 = new Registration("fldOpServId2", date.plusDays(2), date.plusDays(2), startTime,
                endTime, false, false, false);
    }

    @Test(expected = NullPointerException.class)
    public void setCurrentDate_Null() {
        registrationHandler.setCurrent_date(null);
    }

    @Test
    public void setCurrentDate_ValidTuesday() {
        LocalDateTime tuesday = LocalDateTime.of(2021, 3, 9, 0, 0);
        registrationHandler.setCurrent_date(tuesday);
        assertEquals(tuesday, registrationHandler.getCurrent_date());

        LocalDate monday = tuesday.minusDays(1).toLocalDate();
        assertEquals(monday, registrationHandler.getMin_date());
        assertEquals(registrationHandler.getMin_date().getDayOfWeek(), DayOfWeek.MONDAY);
    }

    @Test
    public void setCurrentDate_ValidMonday() {
        LocalDateTime monday = LocalDateTime.of(2021, 3, 8, 0, 0);
        registrationHandler.setCurrent_date(monday);
        assertEquals(monday, registrationHandler.getCurrent_date());
        assertEquals(monday.toLocalDate(), registrationHandler.getMin_date());
        assertEquals(registrationHandler.getMin_date().getDayOfWeek(), DayOfWeek.MONDAY);
    }

    @Test
    public void addRegistration_Null() {
        Registration reg = null;
        boolean result = registrationHandler.add(reg);
        assertFalse(result);
    }

    @Test
    public void addRegistration_InvalidFields() {
        Registration reg = new Registration("fldOpServId",
                null, null, null, null,
                false, false, false);
        assertFalse(registrationHandler.add(reg));
    }

    @Test
    public void addRegistration_InvalidInterval() {
        LocalDate startDate = LocalDate.of(2021, 3, 8);
        LocalTime time = LocalTime.of(10, 0);
        LocalDate endDate = startDate.plusDays(2);
        Registration reg = new Registration("fldOpServId", startDate, endDate, time, time,
                false, false, false);
        assertFalse(registrationHandler.add(reg));
    }

    @Test
    public void addRegistration_Valid() {
        boolean result = registrationHandler.add(reg1);
        assertTrue(result);
        assertEquals(registrationHandler.getRegistrations().size(), 1);
        assertEquals(registrationHandler.getRegistrations().get(0), reg1);
    }

    @Test
    public void addRegistration_Multiple() {
        boolean result1 = registrationHandler.add(reg1);
        assertTrue(result1);
        assertEquals(registrationHandler.getRegistrations().size(), 1);

        boolean result2 = registrationHandler.add(reg2);
        assertTrue(result2);
        assertEquals(registrationHandler.getRegistrations().size(), 2);
    }

    @Test
    public void deleteRegistration_Empty() {
        assertFalse(registrationHandler.delete(reg1));
    }

    @Test
    public void deleteRegistration_NullEmpty() {
        assertFalse(registrationHandler.delete(null));
    }

    @Test
    public void deleteRegistration_NullNotEmpty() {
        registrationHandler.add(reg1);
        assertFalse(registrationHandler.delete(null));
        assertEquals(registrationHandler.getRegistrations().size(), 1);
    }

    @Test
    public void deleteRegistration_Invalid() {
        registrationHandler.add(reg1);
        assertFalse(registrationHandler.delete(reg2));
        assertEquals(registrationHandler.getRegistrations().size(), 1);
    }

    @Test
    public void deleteRegistration_ValidSingle() {
        registrationHandler.add(reg1);
        assertTrue(registrationHandler.delete(reg1));
        assertEquals(registrationHandler.getRegistrations().size(), 0);
    }

    @Test
    public void deleteRegistration_ValidMultiple() {
        registrationHandler.add(reg1);
        registrationHandler.add(reg2);
        assertTrue(registrationHandler.delete(reg1));
        assertEquals(registrationHandler.getRegistrations().size(), 1);
        assertEquals(registrationHandler.getRegistrations().get(0), reg2);
    }



    @Test
    public void getRegistrationsOnDate_Empty() {
        LocalDate date = LocalDate.of(2021, 9, 3);
        ArrayList<Registration> registrations = registrationHandler.getRegistrations(date);
        assertEquals(registrations.size(), 0);
    }

    @Test
    public void getRegistrationsOnDate_Null() {
        ArrayList<Registration> registrations = registrationHandler.getRegistrations(null);
        assertEquals(registrations.size(), 0);
    }

    @Test
    public void getRegistrationsOnDate_EmptyDate() {
        Registration registration1 = new Registration("1", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(10, 0), LocalTime.of(15, 0),
                false, false, false);

        Registration registration2 = new Registration("2", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(17, 0), LocalTime.of(20, 0),
                false, false, false);

        Registration registration3 = new Registration("3", LocalDate.of(2021,
                3, 11), LocalDate.of(2021, 3, 11),
                LocalTime.of(10, 0), LocalTime.of(15, 0),
                false, false, false);

        Registration registration4 = new Registration("4", LocalDate.of(2021,
                3, 11), LocalDate.of(2021, 3, 11),
                LocalTime.of(17, 0), LocalTime.of(20, 0),
                false, false, false);

        registrationHandler.add(registration1);
        registrationHandler.add(registration2);
        registrationHandler.add(registration3);
        registrationHandler.add(registration4);

        LocalDate date = LocalDate.of(2021, 3, 10);

        ArrayList<Registration> registrations = registrationHandler.getRegistrations(date);
        assertEquals(registrations.size(), 0);
    }

    @Test
    public void getRegistrationsOnDate_Valid() {
        Registration registration1 = new Registration("1", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(10, 0), LocalTime.of(15, 0),
                false, false, false);

        Registration registration2 = new Registration("2", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(17, 0), LocalTime.of(20, 0),
                false, false, false);

        Registration registration3 = new Registration("3", LocalDate.of(2021,
                3, 11), LocalDate.of(2021, 3, 11),
                LocalTime.of(10, 0), LocalTime.of(15, 0),
                false, false, false);

        Registration registration4 = new Registration("4", LocalDate.of(2021,
                3, 11), LocalDate.of(2021, 3, 11),
                LocalTime.of(17, 0), LocalTime.of(20, 0),
                false, false, false);

        registrationHandler.add(registration1);
        registrationHandler.add(registration2);
        registrationHandler.add(registration3);
        registrationHandler.add(registration4);

        LocalDate date = LocalDate.of(2021, 3, 11);

        ArrayList<Registration> registrations = registrationHandler.getRegistrations(date);
        assertEquals(registrations.size(), 2);
        assertEquals(registrations.get(0).getFldOpRStartDate(), date);
        assertEquals(registrations.get(1).getFldOpRStartDate(), date);
    }

    @Test(expected = NullPointerException.class)
    public void WeeklyRegistrationsNull() {
        registrationHandler.getWeeklyRegistrationArray(null);
    }

    @Test
    public void WeeklyRegistrations_Empty() {
        LocalDate date = LocalDate.of(2021, 3, 8);
        ArrayList<ArrayList<Registration>> weeklyList =
                registrationHandler.getWeeklyRegistrationArray(date);

        assertEquals(weeklyList.size(), 7);

        weeklyList.forEach(l -> {
            assertEquals(l.size(), 0);
        });
    }

    @Test
    public void WeeklyRegistrations_NotEmpty() {
        LocalDate date = LocalDate.of(2021, 3, 8); // Monday
        Registration mon1 = new Registration("1", LocalDate.of(2021, 3, 8),
                LocalDate.of(2021, 3, 8), LocalTime.of(10, 0),
                LocalTime.of(15, 0),
                false, false, false);

        Registration mon2 = new Registration("2", LocalDate.of(2021, 3, 8),
                LocalDate.of(2021, 3, 8), LocalTime.of(17, 0),
                LocalTime.of(20, 0),false,false,false);

        Registration wed1 = new Registration("3", LocalDate.of(2021, 3, 10),
                LocalDate.of(2021, 3, 10), LocalTime.of(10, 0),
                LocalTime.of(15, 0),false, false, false);

        Registration nextWeek = new Registration("4", LocalDate.of(2021, 3, 15),
                LocalDate.of(2021, 3, 15), LocalTime.of(10, 0),
                LocalTime.of(15, 0), false, false, false);

        registrationHandler.add(mon1);
        registrationHandler.add(mon2);
        registrationHandler.add(wed1);
        registrationHandler.add(nextWeek);

        ArrayList<ArrayList<Registration>> weeklyRegistrations = registrationHandler.getWeeklyRegistrationArray(date);

        assertEquals(weeklyRegistrations.size(), 7);

        for(int i = 0; i < weeklyRegistrations.size(); i++) {
            if(i == 0) {
                // Monday
                assertEquals(weeklyRegistrations.get(i).size(), 2);
                assertEquals(weeklyRegistrations.get(i).get(0).getFldOpRStartDate(),
                        LocalDate.of(2021, 3, 8));
                assertEquals(weeklyRegistrations.get(i).get(1).getFldOpRStartDate(),
                        LocalDate.of(2021, 3, 8));
            }
            else if(i == 2) {
                // Tuesday
                assertEquals(weeklyRegistrations.get(i).size(), 1);
                assertEquals(weeklyRegistrations.get(i).get(0).getFldOpRStartDate(),
                        LocalDate.of(2021, 3, 10));
            }
            else {
                assertEquals(weeklyRegistrations.get(i).size(), 0);
            }
        }
    }

    @Test
    public void timeIsInRange_Null() {
        boolean result = registrationHandler.timeIsInValidRange(null);
        assertFalse(result);
    }

    @Test
    public void timeIsInRange_Invalid() {
        LocalTime time = registrationHandler.getMin_time().minusHours(1);
        boolean result = registrationHandler.timeIsInValidRange(time);
        assertFalse(result);
    }

    @Test
    public void timeIsInRange_Valid() {
        LocalTime start_time = registrationHandler.getMin_time();
        LocalTime end_time = registrationHandler.getMax_time();
        assertTrue(registrationHandler.timeIsInValidRange(start_time.plusHours(1)));
        assertTrue(registrationHandler.timeIsInValidRange(start_time));
        assertTrue(registrationHandler.timeIsInValidRange(end_time));
    }

    @Test
    public void isSameDay_Null() {
        LocalDateTime date = LocalDateTime.of(2021, 9, 3, 10, 0 );
        assertFalse(registrationHandler.isSameDay(date, null));
        assertFalse(registrationHandler.isSameDay(null, date));
        assertFalse(registrationHandler.isSameDay(null, null));
    }

    @Test
    public void isSameDay_OutsideRange() {
        LocalDateTime date = LocalDateTime.of(2021, 9, 3,
                registrationHandler.getMin_time().getHour(), 0 );
        // Both outside range
        assertFalse(registrationHandler.isSameDay(date.minusHours(1), date.minusHours(2)));

        // First outside range
        assertFalse(registrationHandler.isSameDay(date.minusHours(1), date));

        // Second outside range
        assertFalse(registrationHandler.isSameDay(date, date.minusHours(1)));
    }

    @Test
    public void isSameDay_DifferentDay() {
        LocalDateTime date1 = LocalDateTime.of(2021, 9, 3, 10, 0);
        assertFalse(registrationHandler.isSameDay(date1, date1.plusDays(1)));

        // Same date, different interval
        LocalDateTime date2 = LocalDateTime.of(2021, 10, 3, 1, 0);
        LocalDateTime date3 = LocalDateTime.of(2021, 10, 3, 10, 0);
        assertFalse(registrationHandler.isSameDay(date2, date3));
    }

    @Test
    public void isSameDay_Valid() {
        LocalDateTime date1 = LocalDateTime.of(2021, 9, 3, 10, 0);
        LocalDateTime date2 = LocalDateTime.of(2021, 9, 3, 15, 0);

        assertTrue(registrationHandler.isSameDay(date1, date2));

        //Different date, same interval
        LocalDateTime date3 = LocalDateTime.of(2021, 9, 4, 1, 0);

        assertTrue(registrationHandler.isSameDay(date2, date3));
    }

    @Test
    public void deleteRegistrationsOnDate_Null() {
        assertFalse(registrationHandler.deleteRegistrationsOnDate(null));
    }

    @Test
    public void deleteRegistrationsOnDate_Empty() {
        assertFalse(registrationHandler.deleteRegistrationsOnDate(LocalDate.of(2021,3,9)));
    }

    @Test
    public void deleteRegistrationsOnDate_NoRegistrations() {
        registrationHandler.add(reg1);
        assertFalse(registrationHandler.deleteRegistrationsOnDate(LocalDate.of(2030, 3, 9)));
        assertEquals(registrationHandler.getRegistrations().size(), 1);
    }

    @Test
    public void deleteRegistrationsOnDate_Valid() {
        Registration registration1 = new Registration("1", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(10, 0), LocalTime.of(15, 0),
                false, false, false);

        Registration registration2 = new Registration("2", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(17, 0), LocalTime.of(20, 0),
                false, false, false);

        Registration registration3 = new Registration("3", LocalDate.of(2021,
                3, 11), LocalDate.of(2021, 3, 11),
                LocalTime.of(10, 0), LocalTime.of(15, 0),
                false, false, false);

        Registration registration4 = new Registration("4", LocalDate.of(2021,
                3, 11), LocalDate.of(2021, 3, 11),
                LocalTime.of(17, 0), LocalTime.of(20, 0),
                false, false, false);

        registrationHandler.add(registration1);
        registrationHandler.add(registration2);
        registrationHandler.add(registration3);
        registrationHandler.add(registration4);

        assertTrue(registrationHandler.deleteRegistrationsOnDate(LocalDate.of(2021, 3, 11)));
        assertEquals(registrationHandler.getRegistrations().size(), 2);

        registrationHandler.getRegistrations().forEach(r -> {
            assertEquals(r.getFldOpRStartDate(), LocalDate.of(2021, 3, 9));
        });
    }

    @Test
    public void deleteRegistrationsOnDate_ValidInterval() {
        Registration registration1 = new Registration("1", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(0, 0), LocalTime.of(1, 0),
                false, false, false);

        registrationHandler.add(registration1);

        assertTrue(registrationHandler.deleteRegistrationsOnDate(LocalDate.of(2021,3,8)));
        assertEquals(registrationHandler.getRegistrations().size(), 0);
    }

    @Test
    public void addNewDailyPlans_Null() {
        assertFalse(registrationHandler.addNewDailyPlans(null, null));
    }

    @Test
    public void addNewDailyPlans_Empty() {
        ArrayList<ArrayList<LocalDateTime>> list = new ArrayList<>();
        LocalDate date = LocalDate.of(2021, 3, 9);

        assertFalse(registrationHandler.addNewDailyPlans(list, date));
        assertEquals(registrationHandler.getRegistrations().size(), 0);
    }

    @Test
    public void getRegistrationsAfterTime_Null() {
        ArrayList<Registration> registrations = new ArrayList<>();
        assertEquals(registrations, registrationHandler.getRegistrationsAfterTime(null));
    }

    @Test
    public void getRegistrationsAfterTime_Empty() {
        ArrayList<Registration> registrations = new ArrayList<>();
        assertEquals(registrations, registrationHandler
                .getRegistrationsAfterTime(LocalDateTime.of(2021,3,9,
                        10,0)));
    }

    @Test
    public void getRegistrationsAfterTime_EmptyDateTime() {
        ArrayList<Registration> registrations = new ArrayList<>();

        Registration registration1 = new Registration("1", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(10, 0), LocalTime.of(15, 0),
                false, false, false);

        Registration registration2 = new Registration("2", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(17, 0), LocalTime.of(20, 0),
                false, false, false);

        registrationHandler.add(registration1);
        registrationHandler.add(registration2);

        assertEquals(registrations, registrationHandler.getRegistrationsAfterTime
                (LocalDateTime.of(2021,3,10,10,0)));
    }

    @Test
    public void getRegistrationsAfterTime_Valid() {
        Registration registration1 = new Registration("1", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(10, 0), LocalTime.of(15, 0),
                false, false, false);

        Registration registration2 = new Registration("2", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(17, 0), LocalTime.of(20, 0),
                false, false, false);

        Registration registration3 = new Registration("3", LocalDate.of(2021,
                3, 11), LocalDate.of(2021, 3, 11),
                LocalTime.of(10, 0), LocalTime.of(15, 0),
                false, false, false);

        Registration registration4 = new Registration("4", LocalDate.of(2021,
                3, 11), LocalDate.of(2021, 3, 11),
                LocalTime.of(17, 0), LocalTime.of(20, 0),
                false, false, false);

        registrationHandler.add(registration1);
        registrationHandler.add(registration2);
        registrationHandler.add(registration3);
        registrationHandler.add(registration4);

        ArrayList<Registration> test1 = registrationHandler.getRegistrationsAfterTime(LocalDateTime.of
                (2021,3,10,10,0));

        assertEquals(test1.size(), 2);

        test1.forEach(registration -> assertTrue(
                LocalDateTime.of(registration.getFldOpRStartDate(), registration.getFldOpRStartTime())
                .isAfter(LocalDateTime.of(2021,3,10,10,0))
        ));

        ArrayList<Registration> test2 = registrationHandler.getRegistrationsAfterTime(LocalDateTime.of
                (2021, 3, 9,15, 0));

        assertEquals(test2.size(), 3);

        test2.forEach(registration -> assertTrue(
                LocalDateTime.of(registration.getFldOpRStartDate(), registration.getFldOpRStartTime())
                        .isAfter(LocalDateTime.of(2021,3,9,15,0))
        ));
    }

    @Test
    public void removeOpenEndedConflicts_LogOutTrue() {
        // arrange
        LocalDate date = LocalDate.of(2021, 1, 1);
        Registration reg1 = new Registration("invalid opServId", date, date,
                LocalTime.of(10, 0), LocalTime.of(11, 15),
                false, false, false);
        Registration reg2 = new Registration("invalid opServId", date, date,
                LocalTime.of(11, 16), null,
                false, false, true);
        Registration reg3 = new Registration("invalid opServId", date, date,
                LocalTime.of(11, 45), LocalTime.of(12, 15),
                false, false, false);

        // act
        registrationHandler.add(reg1);
        registrationHandler.add(reg2);
        registrationHandler.add(reg3);
        List<Registration> regs = registrationHandler.getFutureRegistrations();

        // assert
        assertEquals(regs.size(), 3);
        assertTrue(regs.contains(reg1));
        assertTrue(regs.contains(reg2));
        assertTrue(regs.contains(reg3));
    }

    @Test
    public void removeOpenEndedConflicts_LogOutFalse() {
        // arrange
        LocalDate date = LocalDate.of(2021, 1, 1);
        Registration reg1 = new Registration("invalid opServId", date, date,
                LocalTime.of(10, 0), LocalTime.of(11, 15),
                false, false, false);
        Registration reg2 = new Registration("invalid opServId", date, date,
                LocalTime.of(11, 16), null,
                false, false, false);
        Registration reg3 = new Registration("invalid opServId", date, date,
                LocalTime.of(11, 45), LocalTime.of(12, 15),
                false, false, false);

        // act
        registrationHandler.add(reg1);
        registrationHandler.add(reg2);
        registrationHandler.add(reg3);
        List<Registration> regs = registrationHandler.getFutureRegistrations();

        // assert
        assertEquals(regs.size(), 2);
        assertTrue(regs.contains(reg1));
        assertTrue(regs.contains(reg2));
    }

    @Test
    public void removeOpenEndedConflicts_TwoActive() {
        // arrange
        LocalDate date = LocalDate.of(2021, 1, 1);
        Registration reg1 = new Registration("invalid opServId", date, date,
                LocalTime.of(10, 0), LocalTime.of(11, 15),
                false, false, false);
        Registration reg2 = new Registration("invalid opServId", date, date,
                LocalTime.of(11, 16), null,
                false, false, false);
        Registration reg3 = new Registration("invalid opServId", date, date,
                LocalTime.of(11, 45), null,
                false, false, false);

        // act
        registrationHandler.add(reg1);
        registrationHandler.add(reg2);
        registrationHandler.add(reg3);
        List<Registration> regs = registrationHandler.getFutureRegistrations();

        // assert
        assertEquals(regs.size(), 2);
        assertTrue(regs.contains(reg1));
        assertTrue(regs.contains(reg2));
    }

    @Test
    public void removeOpenEndedConflicts_NearbyDatesNotAffected() {
        // arrange
        LocalDate date1 = LocalDate.of(2021, 1, 1);
        LocalDate date2 = LocalDate.of(2021, 1, 2);
        LocalDate date3 = LocalDate.of(2021, 1, 3);
        Registration reg1 = new Registration("invalid opServId", date1, date1,
                LocalTime.of(12, 15), LocalTime.of(12, 30),
                false, false, false);
        Registration reg2 = new Registration("invalid opServId", date2, date2,
                LocalTime.of(11, 15), null,
                false, false, false);
        Registration reg3 = new Registration("invalid opServId", date3, date3,
                LocalTime.of(13, 45), LocalTime.of(14, 15),
                false, false, false);

        // act
        registrationHandler.add(reg1);
        registrationHandler.add(reg2);
        registrationHandler.add(reg3);
        List<Registration> regs = registrationHandler.getFutureRegistrations();

        // assert
        assertEquals(regs.size(), 3);
        assertTrue(regs.contains(reg1));
        assertTrue(regs.contains(reg2));
        assertTrue(regs.contains(reg3));
    }


}
