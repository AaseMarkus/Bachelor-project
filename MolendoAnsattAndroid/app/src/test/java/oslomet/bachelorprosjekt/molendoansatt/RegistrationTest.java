package oslomet.bachelorprosjekt.molendoansatt;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;

import static org.junit.Assert.*;

public class RegistrationTest {
    @Test
    public void getIntervalString_Null() {
        Registration reg = new Registration("1", null, null,
                null, null, false, false, false);
        assertEquals(reg.getIntervalString(), "");
    }

    @Test
    public void getIntervalString_Valid() {
        Registration reg1 = new Registration("1", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(10,0), LocalTime.of(15, 0),
                false, false, false);

        assertEquals(reg1.getIntervalString(), "10:00 - 15:00");

        // Minutes
        Registration reg2 = new Registration("1", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 9),
                LocalTime.of(10,34), LocalTime.of(15, 14),
                false, false, false);

        assertEquals(reg2.getIntervalString(), "10:34 - 15:14");

        // New date
        Registration reg3 = new Registration("1", LocalDate.of(2021,
                3, 9), LocalDate.of(2021, 3, 10),
                LocalTime.of(10,34), LocalTime.of(1, 14),
                false, false, false);

        assertEquals(reg3.getIntervalString(), "10:34 - 01:14");
    }
}
