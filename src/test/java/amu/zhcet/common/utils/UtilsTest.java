package amu.zhcet.common.utils;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsTest {

    @Test
    public void testAutomaticSessionCodeGeneration() {
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear() % 100;
        int month = localDate.getMonthValue();

        String actualCode = Utils.getDefaultSessionCode();
        if (month < 6)
            assertEquals("W"+year, actualCode);
        else
            assertEquals("A"+year, actualCode);
    }

    @Test
    public void testSessionNameGeneration() {
        String codeAutumn = "A17";
        String codeWinter = "W18";

        assertEquals("Autumn '17", Utils.getSessionName(codeAutumn));
        assertEquals("Winter '18", Utils.getSessionName(codeWinter));
    }

    @Test
    public void testValidEmail() {
        assertEquals(true, Utils.isValidEmail("jamal.areeb@gmail.com"));
        assertEquals(false, Utils.isValidEmail("jamal.areeb"));
    }

    @Test
    public void testGetClientIPForwarded() {
        String headerValue = "MyIP,YourIP";

        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(eq("X-Forwarded-For"))).thenReturn(headerValue);

        assertEquals("MyIP", Utils.getClientIP(request));
    }

    @Test
    public void testGetClientIPOriginal() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader(eq("X-Forwarded-For"))).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("RemoteIP");

        assertEquals("RemoteIP", Utils.getClientIP(request));
    }

    @Test
    public void testHumanReadableByteCountSIByte() {
        assertEquals("234 B", Utils.humanReadableByteCount(234, true));
    }

    @Test
    public void testHumanReadableByteCountSIKiloByte() {
        assertEquals("29.6 kB", Utils.humanReadableByteCount(29567, true));
    }

    @Test
    public void testHumanReadableByteCountSIMegaByte() {
        assertEquals("7.6 MB", Utils.humanReadableByteCount(7648321, true));
    }

    @Test
    public void testHumanReadableByteCountSIGigaByte() {
        assertEquals("9.3 GB", Utils.humanReadableByteCount(9277648321L, true));
    }

    @Test
    public void testHumanReadableByteCountByte() {
        assertEquals("234 B", Utils.humanReadableByteCount(234, false));
    }

    @Test
    public void testHumanReadableByteCountKiloByte() {
        assertEquals("28.9 KiB", Utils.humanReadableByteCount(29567, false));
    }

    @Test
    public void testHumanReadableByteCountMegaByte() {
        assertEquals("7.3 MiB", Utils.humanReadableByteCount(7648321, false));
    }

    @Test
    public void testHumanReadableByteCountGigaByte() {
        assertEquals("8.6 GiB", Utils.humanReadableByteCount(9277648321L, false));
    }

}
