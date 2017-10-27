import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class DemoTest extends Assert {

    public final String EOLN = System.getProperty("line.separator");

    private void validateOutput(String[] args, String expected) throws Exception {
        PrintStream saved = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PrintStream ps = new PrintStream(baos);
            System.setOut(ps);
            Demo.main(args);
            assertEquals(EOLN + expected + EOLN, baos.toString());
        } finally {
            System.setOut(saved);
        }
    }

    @Test
    public void testUsage() throws Exception {
        validateOutput(new String[]{}, "usage: Demo \"expression\" [-verbose]");
    }

    @Test
    public void testEval() throws Exception {
        // object and undefined types are not exercised by DemoTest test
        // NULL is defined as a numeric constant with the value of null/nil
        String[][] data = new String[][] {
            {"1==1", "EVALUATING 1==1" + EOLN + "RESULT: true [boolean]"},
            {"MAKEDATE(1,15,2009)", "EVALUATING MAKEDATE(1,15,2009)" + EOLN + "RESULT: Thu Jan 15 00:00:00 CST 2009 [date]"},
            {"1+1", "EVALUATING 1+1" + EOLN + "RESULT: 2 [number]"},
            {"'A'+'B'", "EVALUATING 'A'+'B'" + EOLN + "RESULT: AB [string]"},
            {"NULL", "EVALUATING NULL" + EOLN + "RESULT: null [number]"},
            {"MYNAME", "EVALUATING MYNAME" + EOLN + "RESULT: undefined"},
        };

        for (int i = 0; i < data.length; i++) {
            validateOutput(new String[] {data[i][0]}, data[i][1]);
        }
   }

    @Test
    public void testEval_verbose() throws Exception {
        String expected =
            "EVALUATING 1+2" + EOLN +
            EOLN +
            "INPUT (INFIX)" + EOLN +
            "1,1    NUMBER     1 " + EOLN +
            "1,2    OPERATOR   + " + EOLN +
            "1,3    NUMBER     2 " + EOLN +
            EOLN +
            "RPN (POSTFIX)" + EOLN +
            "1,1    NUMBER     1 " + EOLN +
            "1,3    NUMBER     2 " + EOLN +
            "1,2    OPERATOR   + " + EOLN +
            EOLN +
            "RESULT: 3 [number]";

        validateOutput(new String[] {"1+2", "-verbose"}, expected);
    }
}
