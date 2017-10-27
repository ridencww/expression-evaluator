package com.follett.fsc.commons.evaluator.functions;

import java.sql.Timestamp;
import java.util.TimeZone;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.follett.fsc.commons.datetime.TimestampHelper;
import com.follett.fsc.commons.evaluator.Evaluator;
import com.follett.fsc.commons.evaluator.EvaluatorFunction;
import com.follett.fsc.commons.evaluator.EvaluatorResult;
import com.follett.fsc.commons.evaluator.Function;
import com.follett.fsc.commons.evaluator.Status;
import com.follett.fsc.commons.evaluator.Variable;
import com.follett.fsc.commons.evaluator.EvaluatorFunction.Precedence;
import com.follett.fsc.commons.evaluator.Variable.VariableType;

public class DateTimeTest extends TestCase {
    public static final String TEST_ALL_TEST_TYPE = "UNIT";

    public DateTimeTest(String name) {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }

    /*----------------------------------------------------------------------------*/
    // Test cases follow
    /*----------------------------------------------------------------------------*/

    public void testGetDescription() {
        Function function = new DateTime();
        assertEquals("wrong description",
            "Core date/time functions - Level 1",
            function.getDescription());
    }

    public void testGetPrecedence() {
        Function function = new DateTime();
        assertEquals("wrong precedence", Precedence.LEVEL_1, function.getPrecedence());
    }

    public void testTerms() {
        Function function = new DateTime();
        String[] expected = new String[] {
            "DATE", "MAKETIMESTAMP", "TIMESTAMPWITHIN", "TIMESTAMPBETWEEN"
        };
        String[] actual = function.getTerms();
        assertEquals("wrong size", expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals("wrong term", expected[i], actual[i]);
        }
    }

    public void testEvaluation_date() {
        Variable result;
        Evaluator e = new Evaluator();;
        EvaluatorResult er;
        Timestamp expected;

        // Verify that function is available
        assertTrue("\nfunction not registered", e.isTerm("date") != -1);
        Function.validateMinimumNumberOfArguments(e, "date", 0);
        Function.validateMaximumNumberOfArguments(e, "date", 1, "Ralph", "Iden", "Dev");

        expected = TimestampHelper.getNow();
        result = e.parse("DATE()");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.TIMESTAMP, result.getType());
        assertTrue("bad value", TimestampHelper.within(expected, result.getValueAsTimestamp(), 1000));

        expected = TimestampHelper.setToFirstSecondOfDay(TimestampHelper.getNow());
        result = e.parse("DATE(1)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.TIMESTAMP, result.getType());
        assertTrue("bad value", TimestampHelper.within(expected, result.getValueAsTimestamp(), 1000));

        expected = TimestampHelper.setToLastSecondOfDay(TimestampHelper.getNow());
        result = e.parse("DATE(2)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.TIMESTAMP, result.getType());
        assertTrue("bad value", TimestampHelper.within(expected, result.getValueAsTimestamp(), 1000));

        expected = TimestampHelper.setToLastSecondOfDay(TimestampHelper.getNow());
        result = e.parse("DATE(3)");
        er = e.getEvaluatorResult();
        assertTrue("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.ERROR_INVALID_ARGUMENT, er.getStatus());
        assertEquals("wrong message",
            "Invalid argument, expected \"0\", \"1\", or \"2\" but got 3",
            er.getMessages().get(0));

        result = e.parse("DATE(null)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.TIMESTAMP, result.getType());
        assertNull("bad value", result.getValueAsTimestamp());

    }

    /*----------------------------------------------------------------------------*/

    public void testEvaluation_maketimestamp() {
        Variable result;
        Evaluator e = new Evaluator();;
        EvaluatorResult er;
        Timestamp expected;

        // Verify that function is available
        assertTrue("\nfunction not registered", e.isTerm("maketimestamp") != -1);
        Function.validateMinimumNumberOfArguments(e, "maketimestamp", 1);
        Function.validateMaximumNumberOfArguments(e, "maketimestamp", 6,
            "1", "2", "3", "4", "5", "6", "7");

        expected = TimestampHelper.makeTimestamp(2009, 2, 4, 5, 20, 44);
        result = e.parse("MAKETIMESTAMP(3, 4, 2009, 05, 20, 44)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.TIMESTAMP, result.getType());
        assertTrue("bad value", expected.equals(result.getValueAsTimestamp()));

        expected = TimestampHelper.makeTimestamp(2009, 2, 4, 0, 0, 0);
        result = e.parse("MAKETIMESTAMP(3,4,2009)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.TIMESTAMP, result.getType());
        assertTrue("bad value", expected.equals(result.getValueAsTimestamp()));

        expected = TimestampHelper.makeTimestamp(2009, 2, 4, 7, 0, 0);
        result = e.parse("MAKETIMESTAMP(3,4,2009,7)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.TIMESTAMP, result.getType());
        assertTrue("bad value", expected.equals(result.getValueAsTimestamp()));

        expected = TimestampHelper.makeTimestamp(1960, 2, 4, 0, 0, 0);
        result = e.parse("MAKETIMESTAMP(3,4,60)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.TIMESTAMP, result.getType());
        assertTrue("bad value", expected.equals(result.getValueAsTimestamp()));

        expected = TimestampHelper.makeTimestamp(2009, 2, 4, 0, 0, 0);
        result = e.parse("MAKETIMESTAMP(3,4,09)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.TIMESTAMP, result.getType());
        assertTrue("bad value", expected.equals(result.getValueAsTimestamp()));
    }

    /*----------------------------------------------------------------------------*/

    public void testEvaluation_timestampbetween() {
        Variable result;
        Evaluator e = new Evaluator();;
        EvaluatorResult er;

        // Verify that function is available
        assertTrue("\nfunction not registered", e.isTerm("timestampbetween") != -1);
        Function.validateMinimumNumberOfArguments(e, "timestampbetween", 3);
        Function.validateMaximumNumberOfArguments(e, "timestampbetween", 3,
            "1", "2", "3", "4", "5", "6", "7");

        e.parse("TEST1 = MakeTimestamp(5,1,2009)");
        e.parse("TEST2 = MakeTimestamp(9,11,2009)");
        e.parse("FROM  = MakeTimestamp(9,1,2009)");
        e.parse("THRU  = MakeTimestamp(9,30,2009)");

        result = e.parse("TimestampBetween(TEST1,FROM,THRU)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampBetween(TEST2,FROM,THRU)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertTrue("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampBetween(null,FROM,THRU)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampBetween(null,null,THRU)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampBetween(null,null,null)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampBetween(null,FROM,null)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampBetween(TEST2,null,null)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampBetween(TEST2,null,THRU)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampBetween(TEST2,FROM,null)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());
    }

    /*----------------------------------------------------------------------------*/

    public void testEvaluation_timestampwithin() {
        Variable result;
        Evaluator e = new Evaluator();;
        EvaluatorResult er;

        // Verify that function is available
        assertTrue("\nfunction not registered", e.isTerm("timestampwithin") != -1);
        Function.validateMinimumNumberOfArguments(e, "timestampwithin", 3);
        Function.validateMaximumNumberOfArguments(e, "timestampwithin", 3,
            "1", "2", "3", "4", "5", "6", "7");

        e.parse("TEST1 = MakeTimestamp(5,1,2009,7,2,10)");
        e.parse("TEST2 = MakeTimestamp(5,1,2009,7,2,15)");

        result = e.parse("TimestampWithin(TEST1, TEST2, 1000)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampWithin(TEST1, TEST2, 10000)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertTrue("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampWithin(null, TEST2, 10000)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampWithin(null, null, 10000)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampWithin(null, TEST2, null)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampWithin(null,null,null)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());


        result = e.parse("TimestampWithin(TEST1, null, 10000)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampWithin(TEST1, TEST2, null)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());

        result = e.parse("TimestampWithin(TEST1, null, null)");
        er = e.getEvaluatorResult();
        assertFalse("wrong status", e.haveErrors());
        assertEquals("wrong status", Status.STATUS_OK, er.getStatus());
        assertEquals("wrong type", Variable.VariableType.BOOLEAN, result.getType());
        assertFalse("bad value", result.getValueAsBoolean().booleanValue());
    }

    public void testMakeTimestamp_parse_string_parameters() {
        Evaluator e = new Evaluator();
        EvaluatorResult er;

        // Simple conversion
        e.parse("MakeTimestamp(\"20090115\")");
        assertFalse(e.haveErrors());

        // String with conversion format supplied
        e.parse("MakeTimestamp(\"20090115\", \"yyyyMMdd\")");
        assertFalse(e.haveErrors());

        // Too many parameters (STRING, STRING is max)
        e.parse("MakeTimestamp(\"20090115\", \"yyyyMMdd\", 123)");
        er = e.getEvaluatorResult();
        assertTrue(e.haveErrors());
        assertEquals("wrong status", Status.ERROR_TOO_MANY_ARGUMENTS, er.getStatus());

        // Non-string for second parameter
        e.parse("MakeTimestamp(\"20090115\", 123)");
        er = e.getEvaluatorResult();
        assertTrue(e.haveErrors());
        assertEquals("wrong status", Status.ERROR_STRING_EXPECTED, er.getStatus());

        // Empty string to convert
        e.parse("MakeTimestamp(\"\")");
        er = e.getEvaluatorResult();
        assertTrue(e.haveErrors());
        assertEquals("wrong status", Status.ERROR_TIMESTAMP_CREATION, er.getStatus());

        // Empty format parameter
        e.parse("MakeTimestamp(\"20090115\", \"\")");
        er = e.getEvaluatorResult();
        assertTrue(e.haveErrors());
        assertEquals("wrong status", Status.ERROR_INVALID_ARGUMENT, er.getStatus());
        assertEquals("wrong msg", "Invalid argument, expected format string but got empty string", er.getMessages().get(0));

        // Make an invalid state by monkeying around with the SimpleDateFormat hashtable
        EvaluatorFunction f = e.getFunction("MakeTimestamp");
        ((DateTime)f).getDateFormaters().clear();
        e.parse("MakeTimestamp(\"20090115\")");
        er = e.getEvaluatorResult();
        assertTrue(e.haveErrors());
        assertEquals("wrong status", Status.ERROR, er.getStatus());
        assertEquals("wrong msg", "Error: Expected a SimpleDateFormat object for yyyy-MM-dd'T'HH:mm:ss.S", er.getMessages().get(0));
    }

    public void testMakeTimestamp_parse_string_default_list_valid_date() {
        Evaluator e = new Evaluator();

        // Build the timezone offset used for the test. Doing this will allow the test
        // to run across multiple timezones and handle DST.
        TimeZone tz = TimeZone.getDefault();
        int hoursOffset = Math.abs(tz.getRawOffset() / 3600000);
        String tzOffset = hoursOffset + "00";
        if (hoursOffset < 12) {
            tzOffset = "0" + tzOffset;
        }
        if (hoursOffset >= 0) {
            tzOffset = "-" + tzOffset;
        }

        // Valid format and valid date
        Timestamp tsDate = TimestampHelper.makeTimestamp(2009, 0, 15, 0, 0, 0);
        Timestamp tsDateTime = TimestampHelper.makeTimestamp(2009, 0, 15, 7, 32, 59);
        Timestamp tsDateTime24 = TimestampHelper.makeTimestamp(2009, 0, 15, 21, 32, 59);
        Timestamp tsDateTimeMilli = TimestampHelper.makeTimestamp(2009, 0, 15, 7, 32, 59);
        tsDateTimeMilli.setNanos(123 * 1000000);
        Timestamp tsDateTimeMilli24 = TimestampHelper.makeTimestamp(2009, 0, 15, 21, 32, 59);
        tsDateTimeMilli24.setNanos(123 * 1000000);
        Timestamp tsTime = TimestampHelper.makeTimestamp(1970, 0, 1, 7, 32, 59);
        Timestamp tsTime24 = TimestampHelper.makeTimestamp(1970, 0, 1, 21, 32, 59);

        Object[][] dateToTest = new Object[][] {
            {"20090115", tsDate},
            {"2009/01/15", tsDate},
            {"2009/1/15", tsDate},
            {"01/15/2009", tsDate},
            {"1/15/2009", tsDate},
            {"Jan 15 2009 07:32:59 AM", tsDateTime},
            {"Jan 15 2009 07:32:59.123 AM", tsDateTimeMilli},
            {"2009-01-15", tsDate},
            {"2009-01-15 07:32:59", tsDateTime},
            {"2009-01-15 21:32:59", tsDateTime24},
            {"2009-01-15 07:32:59.123", tsDateTimeMilli},
            {"2009-01-15 21:32:59.123", tsDateTimeMilli24},
            {"2009-01-15T07:32:59" + tzOffset, tsDateTime},
            {"2009-01-15T21:32:59" + tzOffset, tsDateTime24},
            {"2009-01-15T07:32:59.123", tsDateTimeMilli},
            {"2009-01-15T21:32:59.123", tsDateTimeMilli24},
            {"07:32:59", tsTime},
            {"21:32:59", tsTime24},
        };

        for (int i = 0; i < dateToTest.length; i++) {
            Variable var = e.parse("MakeTimestamp(\"" + dateToTest[i][0] + "\")");
            assertFalse("no errors expected for " + i + ":" + dateToTest[i][0], e.haveErrors());
            assertEquals("wrong type for " + i + ":" + dateToTest[i][0], VariableType.TIMESTAMP, var.getType());
            assertEquals("wrong value for " + i + ":" + dateToTest[i][0], dateToTest[i][1], var.getValueAsTimestamp());
        }
    }

    public void testMakeTimestamp_parse_string_default_list_valid_leapyeardate() {
        Evaluator e = new Evaluator();

        // Build the timezone offset used for the test. Doing this will allow the test
        // to run across multiple timezones and handle DST.
        TimeZone tz = TimeZone.getDefault();
        int hoursOffset = Math.abs(tz.getRawOffset() / 3600000);
        String tzOffset = hoursOffset + "00";
        if (hoursOffset < 12) {
            tzOffset = "0" + tzOffset;
        }
        if (hoursOffset >= 0) {
            tzOffset = "-" + tzOffset;
        }

        // Valid format and valid leap year
        Timestamp tsDate = TimestampHelper.makeTimestamp(2008, 1, 29, 0, 0, 0);
        Timestamp tsDateTime = TimestampHelper.makeTimestamp(2008, 1, 29, 7, 32, 59);
        Timestamp tsDateTime24 = TimestampHelper.makeTimestamp(2008, 1, 29, 21, 32, 59);
        Timestamp tsDateTimeMilli = TimestampHelper.makeTimestamp(2008, 1, 29, 7, 32, 59);
        tsDateTimeMilli.setNanos(123 * 1000000);
        Timestamp tsDateTimeMilli24 = TimestampHelper.makeTimestamp(2008, 1, 29, 21, 32, 59);
        tsDateTimeMilli24.setNanos(123 * 1000000);

        Object[][] dateToTest = new Object[][] {
            {"20080229", tsDate},
            {"2008/02/29", tsDate},
            {"2008/2/29", tsDate},
            {"2/29/2008", tsDate},
            {"02/29/2008", tsDate},
            {"Feb 29 2008 07:32:59 AM", tsDateTime},
            {"Feb 29 2008 07:32:59.123 AM", tsDateTimeMilli},
            {"2008-02-29", tsDate},
            {"2008-02-29 07:32:59", tsDateTime},
            {"2008-02-29 21:32:59", tsDateTime24},
            {"2008-02-29 07:32:59.123", tsDateTimeMilli},
            {"2008-02-29 21:32:59.123", tsDateTimeMilli24},
            {"2008-02-29T07:32:59" + tzOffset, tsDateTime},
            {"2008-02-29T21:32:59" + tzOffset, tsDateTime24},
            {"2008-02-29T07:32:59.123", tsDateTimeMilli},
            {"2008-02-29T21:32:59.123", tsDateTimeMilli24},
        };

        for (int i = 0; i < dateToTest.length; i++) {
            Variable var = e.parse("MakeTimestamp(\"" + dateToTest[i][0] + "\")");
            assertFalse("no errors expected for " + i + ":" + dateToTest[i][0], e.haveErrors());
            assertEquals("wrong type for " + i + ":" + dateToTest[i][0], VariableType.TIMESTAMP, var.getType());
            assertEquals("wrong value for " + i + ":" + dateToTest[i][0], dateToTest[i][1], var.getValueAsTimestamp());
        }
    }

    public void testMakeTimestamp_parse_string_default_list_invalid_leapyeardate() {
        Evaluator e = new Evaluator();

        // Build the timezone offset used for the test. Doing this will allow the test
        // to run across multiple timezones and handle DST.
        TimeZone tz = TimeZone.getDefault();
        int hoursOffset = Math.abs(tz.getRawOffset() / 3600000);
        String tzOffset = hoursOffset + "00";
        if (hoursOffset < 12) {
            tzOffset = "0" + tzOffset;
        }
        if (hoursOffset >= 0) {
            tzOffset = "-" + tzOffset;
        }

        // Valid format and valid leap year
        Timestamp tsDate = TimestampHelper.makeTimestamp(2009, 1, 29, 0, 0, 0);
        Timestamp tsDateTime = TimestampHelper.makeTimestamp(2009, 1, 29, 7, 32, 59);
        Timestamp tsDateTime24 = TimestampHelper.makeTimestamp(2009, 1, 29, 21, 32, 59);
        Timestamp tsDateTimeMilli = TimestampHelper.makeTimestamp(2009, 1, 29, 7, 32, 59);
        tsDateTimeMilli.setNanos(123 * 1000000);
        Timestamp tsDateTimeMilli24 = TimestampHelper.makeTimestamp(2009, 1, 29, 21, 32, 59);
        tsDateTimeMilli24.setNanos(123 * 1000000);

        Object[][] dateToTest = new Object[][] {
            {"20090229", tsDate},
            {"2009/02/29", tsDate},
            {"2009/2/29", tsDate},
            {"2/29/2009", tsDate},
            {"02/29/2009", tsDate},
            {"Feb 29 2009 07:32:59 AM", tsDateTime},
            {"Feb 29 2009 07:32:59.123 AM", tsDateTimeMilli},
            {"2009-02-29", tsDate},
            {"2009-02-29 07:32:59", tsDateTime},
            {"2009-02-29 21:32:59", tsDateTime24},
            {"2009-02-29 07:32:59.123", tsDateTimeMilli},
            {"2009-02-29 21:32:59.123", tsDateTimeMilli24},
            {"2009-02-29T07:32:59" + tzOffset, tsDateTime},
            {"2009-02-29T21:32:59" + tzOffset, tsDateTime24},
        };

        for (int i = 0; i < dateToTest.length; i++) {
            e.parse("MakeTimestamp(\"" + dateToTest[i][0] + "\")");
            EvaluatorResult er = e.getEvaluatorResult();
            assertTrue("errors expected for " + i + ":" + dateToTest[i][0], e.haveErrors());
            assertEquals("wrong status", Status.ERROR_TIMESTAMP_CREATION, er.getStatus());
            assertEquals("wrong msg",
                "Unable to create timestamp using expression \"" + dateToTest[i][0] + "\"",
                er.getMessages().get(0));
        }
    }

    public void testMakeTimestamp_parse_string_user_list() {
        Evaluator e = new Evaluator();
        EvaluatorResult er;

        Timestamp tsDate = TimestampHelper.makeTimestamp(2009, 0, 15, 0, 0, 0);

        // Confirm bad parse before switching out formats
        String testDate = "year:2009 month:01 day:15";
        e.parse("MakeTimestamp(\"" + testDate + "\")");
        er = e.getEvaluatorResult();
        assertTrue("errors expected for " + testDate, e.haveErrors());
        assertEquals("wrong status", Status.ERROR_TIMESTAMP_CREATION, er.getStatus());
        assertEquals("wrong msg",
            "Unable to create timestamp using expression \"" + testDate + "\"",
            er.getMessages().get(0));


        // Switch in the new formats
        String[] newFormats = new String[] {"'year:'yyyy 'month:'MM 'day:'dd"};
        EvaluatorFunction f = e.getFunction("maketimestamp");
        ((DateTime)f).setDateFormats(newFormats);

        Variable var = e.parse("MakeTimestamp(\"" + testDate + "\")");
        assertFalse("no errors expected for " + testDate, e.haveErrors());
        assertEquals("wrong type", VariableType.TIMESTAMP, var.getType());
        assertEquals("wrong value", tsDate, var.getValueAsTimestamp());
    }

    public void testMakeTimestamp_parse_string_user_format() {
        Evaluator e = new Evaluator();
        EvaluatorResult er;

        String testDate = "year:2009 month:01 day:15";
        String testDateFormat = "\"'year:'yyyy 'month:'MM 'day:'dd\"";
        Timestamp tsDate = TimestampHelper.makeTimestamp(2009, 0, 15, 0, 0, 0);

        // Confirm bad parse before specifying a custom format as a parameter
        e.parse("MakeTimestamp(\"" + testDate + "\")");
        er = e.getEvaluatorResult();
        assertTrue("errors expected for " + testDate, e.haveErrors());
        assertEquals("wrong status", Status.ERROR_TIMESTAMP_CREATION, er.getStatus());
        assertEquals("wrong msg",
            "Unable to create timestamp using expression \"" + testDate + "\"",
            er.getMessages().get(0));

        // Parse again, but specify special format
        Variable var = e.parse("MakeTimestamp(\"" + testDate + "\", " + testDateFormat + ")");
        assertFalse("no errors expected for " + testDate, e.haveErrors());
        assertEquals("wrong type", VariableType.TIMESTAMP, var.getType());
        assertEquals("wrong value", tsDate, var.getValueAsTimestamp());

        // Make sure original formats are still available
        e.parse("MakeTimestamp(\"2009-01-15\")");
        assertFalse("no errors expected for 2009-01-15", e.haveErrors());
        assertEquals("wrong type", VariableType.TIMESTAMP, var.getType());
        assertEquals("wrong value", tsDate, var.getValueAsTimestamp());
    }

    /*----------------------------------------------------------------------------*/
    public void testMakeTimestampNull() {
        int[] dateParts = {0, 0, 0, 15, 0, 2009};

        Evaluator e = new Evaluator();
        Variable result = null;

        for (int i = 1; i < 64; i++) {
            int k = i;
            StringBuilder sb = new StringBuilder("MakeTimestamp(");
            for (int j = 0; j < dateParts.length; j++) {
                if  (j > 0 ) {
                    sb.append(",");
                }
                if  (k % 2 == 1) {
                    sb.append("null");
                } else {
                    sb.append(dateParts[dateParts.length - j - 1]);
                }
                k = k / 2;
            }
            sb.append(")");
            result = e.parse(sb.toString());
            assertFalse("No Error expected for : " + sb.toString(), e.haveErrors());
            assertEquals("Wrong Type for " + sb.toString(), Variable.VariableType.TIMESTAMP, result.getType());
            assertNull("Should be null result for: " + sb.toString(), result.getValueAsTimestamp());
        }
    }


    /*----------------------------------------------------------------------------*/

    public static void main(String[] args) {
        String[] s = {"-noloading", DateTimeTest.class.getName()};
        junit.textui.TestRunner.main(s);
    }

    public static junit.framework.Test suite() {
        String testType = System.getProperty("test_type");
        TestSuite newSuite = new TestSuite();

        // Here is where you can specify a list of specific tests to run. If
        // none are specified, all tests will be run.
   //     newSuite.addTest(new DateTimeTest("testMakeTimestamp_parse_string_user_format"));

        // Here we test if we are running testunit or testacceptance (testType
        // will be set) or if no test cases were added to the test suite
        // above, then we run the full suite of tests.
        if (testType != null || newSuite.countTestCases() < 1)
            newSuite = new TestSuite(DateTimeTest.class);

        return (newSuite);
    }

}
