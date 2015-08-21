package gov.gtas.parsers.util;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import gov.gtas.parsers.exception.ParseException;

public class ParseUtilsTest {
    @Test
    public void testSplitHappyPath() {
        String segmentText = "DTM*36:10109$LOC*31*USA$NAD*FL***ANDREWS:TIFFANY:PAGE$ATT*2**F$";
        String segs[] = ParseUtils.splitWithEscapeChar(segmentText, '$', '?');
        assertEquals(4, segs.length);
        assertEquals("DTM*36:10109", segs[0]);
        assertEquals("LOC*31*USA", segs[1]);
        assertEquals("NAD*FL***ANDREWS:TIFFANY:PAGE", segs[2]);
        assertEquals("ATT*2**F", segs[3]);
    }

    @Test
    public void testSplitWithEscaped() {
        String segmentText = "DTM*36:10109'LOC*31*USA'NAD*FL***MC?'ANDREWS:TIFFANY:PAGE'ATT*2**F'";
        String segs[] = ParseUtils.splitWithEscapeChar(segmentText, '\'', '?');
        assertEquals(4, segs.length);
        assertEquals("NAD*FL***MC'ANDREWS:TIFFANY:PAGE", segs[2]);
    }
    
    @Test
    public void testEscapedEscaped() {
        String segmentText = "DTM*36:10109'LOC*31*USA'NAD*FL***MC?'ANDREWS?:TIFF??ANY:PAGE'ATT*2**F'";
        String segs[] = ParseUtils.splitWithEscapeChar(segmentText, '\'', '?');
        assertEquals(4, segs.length);
        assertEquals("NAD*FL***MC'ANDREWS?:TIFF??ANY:PAGE", segs[2]);
    }

    @Test
    public void testSplitSegmentsWithExtraneousWhitespace() {
        String segmentText = "DTM*36:10109  $   LOC*31*USA  $NAD*FL***ANDREWS:TIFFANY:PAGE\r\n $\n\n\n\n ATT*2**F$";
        String segs[] = ParseUtils.splitWithEscapeChar(segmentText, '$', '?');
        assertEquals(4, segs.length, 4);
        assertEquals("DTM*36:10109", segs[0]);
        assertEquals("LOC*31*USA", segs[1]);
        assertEquals("NAD*FL***ANDREWS:TIFFANY:PAGE", segs[2]);
        assertEquals("ATT*2**F", segs[3]);
    }

    @Test
    public void testSplitElementsWithExtraneousWhitespace() {
        String segmentText = " ANDREWS:    TIFFANY : PAGE ";
        String elements[] = ParseUtils.splitWithEscapeChar(segmentText, ':', '?');
        assertEquals("ANDREWS", elements[0]);
        assertEquals("TIFFANY", elements[1]);
        assertEquals("PAGE", elements[2]);
    }
    
    @Test
    public void testMd5() {
        String str1 = "unoshriram";
        String expected = "95E5F0B6988EC703E832172F70CE7DC7";
        String actual = ParseUtils.getMd5Hash(str1, StandardCharsets.US_ASCII);
        assertEquals(expected, actual);
    }
    
    @Test
    public void testConvertToSingleLine() {
        String input = "   hello    \r\n\r\n  there\r   gtas team\n";
        String actual = ParseUtils.convertToSingleLine(input);
        assertEquals("hellotheregtas team", actual);
    }
    
    @Test
    public void testSeparateCarrierAndFlightNumber() {
        String[] tests = {
                "UA0341",
                "UA123",
                "Q545",
                "BA1"
        };
        for (String s : tests) {
            String[] r = ParseUtils.separateCarrierAndFlightNumber(s);
            System.out.println(Arrays.toString(r));            
        }
    }
    
    @Test
    public void testCalculateAge() throws ParseException {
        Date d = ParseUtils.parseDateTime("03051980", "MMddyyyy");
        int age = ParseUtils.calculateAge(d);
        System.out.println(d + " " + age);
    }
}
