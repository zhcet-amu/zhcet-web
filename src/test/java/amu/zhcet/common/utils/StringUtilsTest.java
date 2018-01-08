package amu.zhcet.common.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class StringUtilsTest {

    @Test
    public void testCapitalizeFirst() {
        String allLower = "areeb jamal";
        String allUpper = "AREEB JAMAL";
        String mixed = "aReeB jAMal";

        String expected = "Areeb Jamal";
        assertNull(StringUtils.capitalizeFirst(null));
        assertEquals(expected, StringUtils.capitalizeFirst(allLower));
        assertEquals(expected, StringUtils.capitalizeFirst(allUpper));
        assertEquals(expected, StringUtils.capitalizeFirst(mixed));
    }

    @Test
    public void testCapitalizeAll() {
        String allLower = "areeb jamal";
        String allUpper = "AREEB JAMAL";
        String mixed = "aReeB jAMal";

        String expected = "AREEB JAMAL";
        assertNull(StringUtils.capitalizeAll(null));
        assertEquals(expected, StringUtils.capitalizeAll(allLower));
        assertEquals(expected, StringUtils.capitalizeAll(allUpper));
        assertEquals(expected, StringUtils.capitalizeAll(mixed));
    }

    @Test
    public void testDefaultString() {
        String expected = "Test String";

        assertEquals(expected, StringUtils.defaultString(null, expected));
        assertEquals(expected, StringUtils.defaultString("", expected));
        assertEquals("Done", StringUtils.defaultString("Done", expected));
    }

}
