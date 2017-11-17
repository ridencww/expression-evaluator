package com.creativewidgetworks.expressionparser;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FunctionToolboxTest extends UnitTestBase {

    private Parser parser;
    private FunctionToolbox toolbox;

    @Before
    public void beforeEach() {
        parser = new Parser();
        toolbox = FunctionToolbox.register(parser);
    }

    private Date makeDate(int mon, int day, int year, int hr, int min, int sec) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, mon - 1, day, hr, min, sec);
        return cal.getTime();
    }

    private void validateGUID(Parser parser, String expression, boolean upperCase, boolean dashes, boolean braces) {
        String validSet = "0123456789abcdef";

        if (upperCase) {
            validSet = validSet.toUpperCase();
        }

        if (dashes) {
            validSet += "-";
        }

        if (braces) {
            validSet += "{}";
        }

        int len = dashes ? 36 : 32;
        if (braces) {
            len += 2;
        }

        String guid = parser.eval(expression).asString();

        assertEquals("GUID length: " + guid, len, guid.length());

        for (int i = 0; i < guid.length(); i++) {
            if (validSet.indexOf(guid.charAt(i)) == -1) {
                fail("Invalid character found in GUID: " + guid.charAt(i));
            }
        }
    }

    @Test
    public void testABS() throws Exception {
        validatePattern(parser, "ABS");

        parser.eval("A='1.01'");

        validateExceptionThrown(parser, "ABS()", "ABS expected 1 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "ABS(1, 2)", "ABS expected 1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "ABS('123')", "ABS parameter 1 expected type NUMBER, but was STRING", 1, 4);
        validateExceptionThrown(parser, "ABS(A)", "ABS parameter 1 expected type NUMBER, but was STRING", 1, 4);

        validateNumericResult(parser, "ABS(null)", null);
        validateNumericResult(parser, "ABS(0)", "0");
        validateNumericResult(parser, "ABS(1.2345)", "1.2345");
        validateNumericResult(parser, "ABS(-123.45)", "123.45");
    }

    @Test
    public void testARCCOS() throws Exception {
        validatePattern(parser, "ARCCOS");

        validateExceptionThrown(parser, "ARCCOS()", "ARCCOS expected 1 parameter(s), but got 0", 1, 7);
        validateExceptionThrown(parser, "ARCCOS(0.01, 2)", "ARCCOS expected 1 parameter(s), but got 2", 1, 7);
        validateExceptionThrown(parser, "ARCCOS('1.23')", "ARCCOS parameter 1 expected type NUMBER, but was STRING", 1, 7);

        validateNumericResult(parser, "ARCCOS(null)", null);
        validateNumericResult(parser, "ARCCOS(0.9271838546)", "22");
        validateNumericResult(parser, "ARCCOS(0.7071068)", "45");
    }

    @Test
    public void testARCSIN() throws Exception {
        validatePattern(parser, "ARCSIN");

        validateExceptionThrown(parser, "ARCSIN()", "ARCSIN expected 1 parameter(s), but got 0", 1, 7);
        validateExceptionThrown(parser, "ARCSIN(0.01, 2)", "ARCSIN expected 1 parameter(s), but got 2", 1, 7);
        validateExceptionThrown(parser, "ARCSIN('1.23')", "ARCSIN parameter 1 expected type NUMBER, but was STRING", 1, 7);

        validateNumericResult(parser, "ARCSIN(null)", null);
        validateNumericResult(parser, "ARCSIN(0.3746065934)", "22");
        validateNumericResult(parser, "ARCSIN(0.7071068)", "45");
    }

    @Test
    public void testARCTAN() throws Exception {
        validatePattern(parser, "ARCTAN");

        validateExceptionThrown(parser, "ARCTAN()", "ARCTAN expected 1 parameter(s), but got 0", 1, 7);
        validateExceptionThrown(parser, "ARCTAN(0.01, 2)", "ARCTAN expected 1 parameter(s), but got 2", 1, 7);
        validateExceptionThrown(parser, "ARCTAN('1.23')", "ARCTAN parameter 1 expected type NUMBER, but was STRING", 1, 7);

        validateNumericResult(parser, "ARCTAN(null)", null);
        validateNumericResult(parser, "ARCTAN(0)", "0");
        validateNumericResult(parser, "ARCTAN(0.4040262258)", "22");
        validateNumericResult(parser, "ARCTAN(1)", "45");
    }

    @Test
    public void testARRAYLEN() throws Exception {
        validatePattern(parser, "ARRAYLEN");

        parser.eval("V1=1");
        parser.eval("V2=SPLIT('00,10,11')");

        validateExceptionThrown(parser, "ARRAYLEN()", "ARRAYLEN expected 1 parameter(s), but got 0", 1, 9);
        validateExceptionThrown(parser, "ARRAYLEN(V1,V2)", "ARRAYLEN expected 1 parameter(s), but got 2", 1, 9);
        validateExceptionThrown(parser, "ARRAYLEN('test')", "Expected ARRAY type, but was STRING", 1, 10);
        validateExceptionThrown(parser, "ARRAYLEN(V1)", "Expected ARRAY type, but was NUMBER", 1, 10);

        validateNumericResult(parser, "ARRAYLEN(null)", null);
        validateNumericResult(parser, "ARRAYLEN(V2)", "3");
        validateNumericResult(parser, "ARRAYLEN(SPLIT('00,10,11,100'))", "4");
    }

    @Test
    public void testAVERAGE() throws Exception {
        validatePattern(parser, "AVERAGE");

        validateExceptionThrown(parser, "AVERAGE()", "AVERAGE expected 1..n parameter(s), but got 0", 1, 8);
        validateExceptionThrown(parser, "AVERAGE('1.23')", "AVERAGE parameter 1 expected type NUMBER, but was STRING", 1, 8);
        validateExceptionThrown(parser, "AVERAGE(1,2,'3')", "Expected NUMBER value, but was STRING", 1, 13);
        validateExceptionThrown(parser, "AVERAGE(null, null)", "The following parameter(s) cannot be null: 0, 1", 1, 1);

        validateNumericResult(parser, "AVERAGE(0)", "0");
        validateNumericResult(parser, "AVERAGE(2)", "2");
        validateNumericResult(parser, "AVERAGE(2, 4, 6, 8)", "5");
    }

    @Test
    public void testCEILING() throws Exception {
        validatePattern(parser, "CEILING");

        validateExceptionThrown(parser, "CEILING()", "CEILING expected 1 parameter(s), but got 0", 1, 8);
        validateExceptionThrown(parser, "CEILING(0.01, 2)", "CEILING expected 1 parameter(s), but got 2", 1, 8);
        validateExceptionThrown(parser, "CEILING('1.23')", "CEILING parameter 1 expected type NUMBER, but was STRING", 1, 8);

        validateNumericResult(parser, "CEILING(null)", null);
        validateNumericResult(parser, "CEILING(0.01)", "1.0");
        validateNumericResult(parser, "CEILING(2.022)", "3.0");
    }

    @Test
    public void testCONTAINS() throws Exception {
        validatePattern(parser, "CONTAINS");

        validateExceptionThrown(parser, "CONTAINS()", "CONTAINS expected 2 parameter(s), but got 0", 1, 9);
        validateExceptionThrown(parser, "CONTAINS('A', 'B', 2)", "CONTAINS expected 2 parameter(s), but got 3", 1, 9);
        validateExceptionThrown(parser, "CONTAINS(1, 'B')", "CONTAINS parameter 1 expected type STRING, but was NUMBER", 1, 9);
        validateExceptionThrown(parser, "CONTAINS('A', 2)", "CONTAINS parameter 2 expected type STRING, but was NUMBER", 1, 9);

        validateBooleanResult(parser, "CONTAINS(null, null)", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINS('A', null)", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINS(null, 'B')", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINS('Ralph', 'I')", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINS('Ralph', 'Ph')", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINS('Ralph', 'ph')", Boolean.TRUE);
        validateBooleanResult(parser, "CONTAINS('Ralph', Upper('ph'))", Boolean.FALSE);
    }

    @Test
    public void testCONTAINSALL() throws Exception {
        validatePattern(parser, "CONTAINSALL");

        validateExceptionThrown(parser, "CONTAINSALL()", "CONTAINSALL expected 2 parameter(s), but got 0", 1, 12);
        validateExceptionThrown(parser, "CONTAINSALL('A', 'B', 2)", "CONTAINSALL expected 2 parameter(s), but got 3", 1, 12);
        validateExceptionThrown(parser, "CONTAINSALL(1, 'B')", "CONTAINSALL parameter 1 expected type STRING, but was NUMBER", 1, 12);
        validateExceptionThrown(parser, "CONTAINSALL('A', 2)", "CONTAINSALL parameter 2 expected type STRING, but was NUMBER", 1, 12);

        validateBooleanResult(parser, "CONTAINSALL(null, null)", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINSALL('A', null)", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINSALL(null, 'B')", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINSALL('Ralph', 'I')", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINSALL('Ralph', 'hR')", Boolean.TRUE);
        validateBooleanResult(parser, "CONTAINSALL('Ralph', 'ph')", Boolean.TRUE);
        validateBooleanResult(parser, "CONTAINSALL('Ralph', 'pH')", Boolean.FALSE);
    }

    @Test
    public void testCONTAINSANY() throws Exception {
        validatePattern(parser, "CONTAINSANY");

        validateExceptionThrown(parser, "CONTAINSANY()", "CONTAINSANY expected 2 parameter(s), but got 0", 1, 12);
        validateExceptionThrown(parser, "CONTAINSANY('A', 'B', 2)", "CONTAINSANY expected 2 parameter(s), but got 3", 1, 12);
        validateExceptionThrown(parser, "CONTAINSANY(1, 'B')", "CONTAINSANY parameter 1 expected type STRING, but was NUMBER", 1, 12);
        validateExceptionThrown(parser, "CONTAINSANY('A', 2)", "CONTAINSANY parameter 2 expected type STRING, but was NUMBER", 1, 12);

        validateBooleanResult(parser, "CONTAINSANY(null, null)", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINSANY('A', null)", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINSANY(null, 'B')", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINSANY('Ralph', 'I')", Boolean.FALSE);
        validateBooleanResult(parser, "CONTAINSANY('Ralph', 'XhR')", Boolean.TRUE);
        validateBooleanResult(parser, "CONTAINSANY('Ralph', 'Xph')", Boolean.TRUE);
        validateBooleanResult(parser, "CONTAINSANY('Ralph', 'XpH')", Boolean.TRUE);
    }

    @Test
    public void testCOS() throws Exception {
        validatePattern(parser, "COS");

        validateExceptionThrown(parser, "COS()", "COS expected 1 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "COS(0.01, 2)", "COS expected 1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "COS('1.23')", "COS parameter 1 expected type NUMBER, but was STRING", 1, 4);

        validateNumericResult(parser, "COS(null)", null);
        validateNumericResult(parser, "COS(0)", "1");
        validateNumericResult(parser, "COS(1)", "0.99985");
        validateNumericResult(parser, "COS(22)", "0.92718");
        validateNumericResult(parser, "COS(45)", "0.70711");
    }

    @Test
    public void testDATEADD() throws Exception {
        validatePattern(parser, "DATEADD");

        validateExceptionThrown(parser, "DATEADD(NULL, NULL)", "The following parameter(s) cannot be null: 0, 1", 1, 1);
        validateExceptionThrown(parser, "DATEADD()", "DATEADD expected 2..3 parameter(s), but got 0", 1, 8);
        validateExceptionThrown(parser, "DATEADD(1, 2)", "DATEADD parameter 1 expected type DATE, but was NUMBER", 1, 8);
        validateExceptionThrown(parser, "DATEADD(NOW(), '2')", "DATEADD parameter 2 expected type NUMBER, but was STRING", 1, 8);
        validateExceptionThrown(parser, "DATEADD(NOW(), 2, 1)", "DATEADD parameter 3 expected type STRING, but was NUMBER", 1, 8);
        validateExceptionThrown(parser, "DATEADD(NOW(), 2, 'x')", "Expected period to be one of 'm', 'd', 'y', 'hr', 'mi', or 'se'", 1, 1);

        // Add or subtract days
        validateDateResult(parser, "DATEADD(MAKEDATE(3, 14, 2007), 1)", makeDate(3, 15, 2007, 0, 0, 0));
        validateDateResult(parser, "DATEADD(MAKEDATE(3, 14, 2007), -1)", makeDate(3, 13, 2007, 0, 0, 0));

        validateDateResult(parser, "DATEADD(MAKEDATE(3, 14, 2007), 1, 'm')", makeDate(4, 14, 2007, 0, 0, 0));
        validateDateResult(parser, "DATEADD(MAKEDATE(3, 14, 2007), 1, 'd')", makeDate(3, 15, 2007, 0, 0, 0));
        validateDateResult(parser, "DATEADD(MAKEDATE(3, 14, 2007), 1, 'y')", makeDate(3, 14, 2008, 0, 0, 0));
        validateDateResult(parser, "DATEADD(MAKEDATE(3, 14, 2007), 1, 'hr')", makeDate(3, 14, 2007, 1, 0, 0));
        validateDateResult(parser, "DATEADD(MAKEDATE(3, 14, 2007), 1, 'mi')", makeDate(3, 14, 2007, 0, 1, 0));
        validateDateResult(parser, "DATEADD(MAKEDATE(3, 14, 2007), 1, 'se')", makeDate(3, 14, 2007, 0, 0, 1));

        // Rollover
        validateDateResult(parser, "DATEADD(MAKEDATE(3, 14, 2007), 10, 'm')", makeDate(1, 14, 2008, 0, 0, 0));
    }

    @Test
    public void testDATEBETWEEN() throws Exception {
        parser.eval("DATE1=MakeDate(5,1,2009)");
        parser.eval("DATE2=MakeDate(9,11,2009)");
        parser.eval("FROMDATE=MakeDate(9,1,2009)");
        parser.eval("THRUDATE=MakeDate(9,30,2009)");

        validatePattern(parser, "DATEBETWEEN");

        validateExceptionThrown(parser, "DATEBETWEEN()", "DATEBETWEEN expected 3 parameter(s), but got 0", 1, 12);
        validateExceptionThrown(parser, "DATEBETWEEN(1, DATE1, DATE2)", "DATEBETWEEN parameter 1 expected type DATE, but was NUMBER", 1, 12);
        validateExceptionThrown(parser, "DATEBETWEEN(DATE1, 2, DATE2)", "DATEBETWEEN parameter 2 expected type DATE, but was NUMBER", 1, 12);
        validateExceptionThrown(parser, "DATEBETWEEN(DATE1, DATE2, 3)", "DATEBETWEEN parameter 3 expected type DATE, but was NUMBER", 1, 12);

        validateBooleanResult(parser, "DATEBETWEEN(DATE2, FROMDATE, THRUDATE)", Boolean.TRUE);
        validateBooleanResult(parser, "DATEBETWEEN(DATE1, FROMDATE, THRUDATE)", Boolean.FALSE);
        validateBooleanResult(parser, "DATEBETWEEN(null, FROMDATE, THRUDATE)", Boolean.FALSE);
        validateBooleanResult(parser, "DATEBETWEEN(null, null, THRUDATE)", Boolean.FALSE);
        validateBooleanResult(parser, "DATEBETWEEN(null, null, null)", Boolean.FALSE);
        validateBooleanResult(parser, "DATEBETWEEN(DATE2, FROMDATE, null)", Boolean.FALSE);
        validateBooleanResult(parser, "DATEBETWEEN(DATE2, null, THRUDATE)", Boolean.FALSE);
        validateBooleanResult(parser, "DATEBETWEEN(DATE2, null, null)", Boolean.FALSE);
    }

    @Test
    public void testDATEBOD() throws Exception {
        validatePattern(parser, "DATEBOD");
        validateExceptionThrown(parser, "DATEBOD()", "DATEBOD expected 1 parameter(s), but got 0", 1, 8);
        validateExceptionThrown(parser, "DATEBOD(1)", "DATEBOD parameter 1 expected type DATE, but was NUMBER", 1, 8);
        validateDateResult(parser, "DATEBOD(MAKEDATE(3,14,2007,14,15,40))", makeDate(3, 14, 2007, 0, 0, 0));
    }

    @Test
    public void testDATEEOD() throws Exception {
        validatePattern(parser, "DATEEOD");
        validateExceptionThrown(parser, "DATEEOD()", "DATEEOD expected 1 parameter(s), but got 0", 1, 8);
        validateExceptionThrown(parser, "DATEEOD(1)", "DATEEOD parameter 1 expected type DATE, but was NUMBER", 1, 8);
        validateDateResult(parser, "DATEEOD(MAKEDATE(3,14,2007,14,15,40))", makeDate(3, 14, 2007, 23, 59, 59));
    }

    @Test
    public void testDATEFORMAT() throws Exception {
        validatePattern(parser, "DATEFORMAT");

        validateExceptionThrown(parser, "DATEFORMAT(NULL, NULL)", "The following parameter(s) cannot be null: 0, 1", 1, 1);
        validateExceptionThrown(parser, "DATEFORMAT()", "DATEFORMAT expected 2..7 parameter(s), but got 0", 1, 11);
        validateExceptionThrown(parser, "DATEFORMAT(1, 2)", "DATEFORMAT parameter 1 expected type STRING, but was NUMBER", 1, 11);
        validateExceptionThrown(parser, "DATEFORMAT('MM/dd/yyyy', MAKEBOOLEAN('1'))", "DATEFORMAT parameter 1 expected type NUMBER, but was BOOLEAN", 1, 26);
        validateExceptionThrown(parser, "DATEFORMAT('MM/dd/yyyy', '4', 1, 1997, 14, 10, 44)", "Too many parameters", 1, 31);
        validateExceptionThrown(parser, "DATEFORMAT('MM/dd/yyyy', 4, '1', 1997, 14, 10, 44)", "DATEFORMAT parameter 3 expected type NUMBER, but was STRING", 1, 11);
        validateExceptionThrown(parser, "DATEFORMAT('MM/dd/yyyy', 4, 1, '1997', 14, 10, 44)", "DATEFORMAT parameter 4 expected type NUMBER, but was STRING", 1, 11);
        validateExceptionThrown(parser, "DATEFORMAT('MM/dd/yyyy', 4, 1, 1997, '14', 10, 44)", "DATEFORMAT parameter 5 expected type NUMBER, but was STRING", 1, 11);
        validateExceptionThrown(parser, "DATEFORMAT('MM/dd/yyyy', 4, 1, 1997, 14, '10', 44)", "DATEFORMAT parameter 6 expected type NUMBER, but was STRING", 1, 11);
        validateExceptionThrown(parser, "DATEFORMAT('MM/dd/yyyy', 4, 1, 1997, 14, 10, '44')", "DATEFORMAT parameter 7 expected type NUMBER, but was STRING", 1, 11);
        validateExceptionThrown(parser, "DATEFORMAT('MM/dd/yyyy', MAKEDATE(3, 14, 2007), 1)", "Too many parameters", 1, 49);

        validateStringResult(parser, "DATEFORMAT('MM/dd/yyyy', MAKEDATE(3, 14, 2007))", "03/14/2007");
        validateStringResult(parser, "DATEFORMAT('MM/dd/yyyy', '2007-03-14')", "03/14/2007");
        validateStringResult(parser, "DATEFORMAT('MM/dd/yyyy', 3, 14, 2007, 0, 0, 00)", "03/14/2007");
    }

    @Test
    public void testDATEWITHIN() throws Exception {
        parser.eval("DATE1=MakeDate(6,15,2008,8,50,10)");
        parser.eval("DATE2=MakeDate(6,15,2008,8,50,15)");

        validatePattern(parser, "DATEWITHIN");

        validateExceptionThrown(parser, "DATEWITHIN()", "DATEWITHIN expected 3 parameter(s), but got 0", 1, 11);
        validateExceptionThrown(parser, "DATEWITHIN(1, DATE1, 1000)", "DATEWITHIN parameter 1 expected type DATE, but was NUMBER", 1, 11);
        validateExceptionThrown(parser, "DATEWITHIN(DATE1, 2, 1000)", "DATEWITHIN parameter 2 expected type DATE, but was NUMBER", 1, 11);
        validateExceptionThrown(parser, "DATEWITHIN(DATE1, DATE2, 'Hello')", "DATEWITHIN parameter 3 expected type NUMBER, but was STRING", 1, 11);

        validateBooleanResult(parser, "DATEWITHIN(DATE1, DATE2, 10000)", Boolean.TRUE);
        validateBooleanResult(parser, "DATEWITHIN(DATE1, DATE2, 1000)", Boolean.FALSE);

        validateBooleanResult(parser, "DATEWITHIN(DATE1, null, 1000)", Boolean.FALSE);
        validateBooleanResult(parser, "DATEWITHIN(null, DATE2, 1000)", Boolean.FALSE);
        validateBooleanResult(parser, "DATEWITHIN(null, null, 1000)", Boolean.FALSE);
        validateBooleanResult(parser, "DATEWITHIN(DATE1, DATE2, null)", Boolean.FALSE);
        validateBooleanResult(parser, "DATEWITHIN(null, null, null)", Boolean.FALSE);
    }

    @Test
    public void testENDSWITH() throws Exception {
        validatePattern(parser, "ENDSWITH");

        validateExceptionThrown(parser, "ENDSWITH()", "ENDSWITH expected 2 parameter(s), but got 0", 1, 9);
        validateExceptionThrown(parser, "ENDSWITH('A', 'B', 2)", "ENDSWITH expected 2 parameter(s), but got 3", 1, 9);
        validateExceptionThrown(parser, "ENDSWITH(1, 'B')", "ENDSWITH parameter 1 expected type STRING, but was NUMBER", 1, 9);
        validateExceptionThrown(parser, "ENDSWITH('A', 2)", "ENDSWITH parameter 2 expected type STRING, but was NUMBER", 1, 9);

        validateBooleanResult(parser, "ENDSWITH(null, null)", Boolean.FALSE);
        validateBooleanResult(parser, "ENDSWITH('A', null)", Boolean.FALSE);
        validateBooleanResult(parser, "ENDSWITH(null, 'B')", Boolean.FALSE);
        validateBooleanResult(parser, "ENDSWITH('Ralph', 'I')", Boolean.FALSE);
        validateBooleanResult(parser, "ENDSWITH('Ralph', 'Ph')", Boolean.FALSE);
        validateBooleanResult(parser, "ENDSWITH('Ralph', 'ph')", Boolean.TRUE);
        validateBooleanResult(parser, "ENDSWITH('Ralph', Upper('ph'))", Boolean.FALSE);
    }

    @Test
    public void testEXP() throws Exception {
        validatePattern(parser, "EXP");

        validateExceptionThrown(parser, "EXP()", "EXP expected 1 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "EXP(0.01, 2)", "EXP expected 1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "EXP('1.23')", "EXP parameter 1 expected type NUMBER, but was STRING", 1, 4);

        validateNumericResult(parser, "EXP(null)", null);
        validateNumericResult(parser, "EXP(0)", "1.00000");
        validateNumericResult(parser, "EXP(1)", "2.71828");
        validateNumericResult(parser, "EXP(2)", "7.38906");
    }

    @Test
    public void testFACTORIAL() throws Exception {
        validatePattern(parser, "FACTORIAL");

        validateExceptionThrown(parser, "FACTORIAL()", "FACTORIAL expected 1 parameter(s), but got 0", 1, 10);
        validateExceptionThrown(parser, "FACTORIAL(1,2)", "FACTORIAL expected 1 parameter(s), but got 2", 1, 10);
        validateExceptionThrown(parser, "FACTORIAL('A')", "FACTORIAL parameter 1 expected type NUMBER, but was STRING", 1, 10);
        validateExceptionThrown(parser, "FACTORIAL(-1)", "Value cannot be less than zero, but was -1", 1, 11);

        validateNumericResult(parser, "FACTORIAL(null)", null);
        validateNumericResult(parser, "FACTORIAL(0)", "1");
        validateNumericResult(parser, "FACTORIAL(1)", "1");
        validateNumericResult(parser, "FACTORIAL(2)", "2");
        validateNumericResult(parser, "FACTORIAL(5)", "120");
        validateNumericResult(parser, "FACTORIAL(6)", "720");
        validateNumericResult(parser, "FACTORIAL(30)", "265252859812191058636308480000000");
    }

    @Test
    public void testFIND() throws Exception {
        validatePattern(parser, "FIND");

        validateExceptionThrown(parser, "FIND()", "FIND expected 2..3 parameter(s), but got 0", 1, 5);
        validateExceptionThrown(parser, "FIND('A','B',2,2)", "FIND expected 2..3 parameter(s), but got 4", 1, 5);
        validateExceptionThrown(parser, "FIND(1,'B')", "FIND parameter 1 expected type STRING, but was NUMBER", 1, 5);
        validateExceptionThrown(parser, "FIND('A',2)", "FIND parameter 2 expected type STRING, but was NUMBER", 1, 5);
        validateExceptionThrown(parser, "FIND('A','B','C')", "FIND parameter 3 expected type NUMBER, but was STRING", 1, 5);

        validateNumericResult(parser, "FIND(null,'abc')", "0");
        validateNumericResult(parser, "FIND('AbCdEfG', null)", "0");
        validateNumericResult(parser, "FIND('', '')", "0");
        validateNumericResult(parser, "FIND('AbCdEfG', '')", "0");
        validateNumericResult(parser, "FIND('AbCdEfG', 'cd')", "0");
        validateNumericResult(parser, "FIND('AbCdEfG', 'CD')", "0");
        validateNumericResult(parser, "FIND('AbCdEfG', 'Cd')", "3");
        validateNumericResult(parser, "FIND('AbCdEfGCd', 'Cd', 5)", "8");
        validateNumericResult(parser, "FIND('AbCdEfGCd', 'Cd', -15)", "3");
        validateNumericResult(parser, "FIND('AbCdEfGCd', 'Cd', 15)", "0");
    }

    @Test
    public void testFLOOR() throws Exception {
        validatePattern(parser, "FLOOR");

        validateExceptionThrown(parser, "FLOOR()", "FLOOR expected 1 parameter(s), but got 0", 1, 6);
        validateExceptionThrown(parser, "FLOOR(0.01, 2)", "FLOOR expected 1 parameter(s), but got 2", 1, 6);
        validateExceptionThrown(parser, "FLOOR('A')", "FLOOR parameter 1 expected type NUMBER, but was STRING", 1, 6);

        validateNumericResult(parser, "FLOOR(null)", null);
        validateNumericResult(parser, "FLOOR(0.01)", "0.0");
        validateNumericResult(parser, "FLOOR(2.022)", "2.0");
    }

    @Test
    public void testFORMAT() throws Exception {
        validatePattern(parser, "FORMAT");

        validateExceptionThrown(parser, "FORMAT(NULL, NULL)", "The following parameter(s) cannot be null: 0, 1", 1, 1);
        validateExceptionThrown(parser, "FORMAT()", "FORMAT expected 2 parameter(s), but got 0", 1, 7);
        validateExceptionThrown(parser, "FORMAT(1, 'b')", "FORMAT parameter 1 expected type STRING, but was NUMBER", 1, 7);
        validateExceptionThrown(parser, "FORMAT('a', 2)", "FORMAT parameter 2 expected type STRING, but was NUMBER", 1, 7);

        validateStringResult(parser, "FORMAT('(###) ###-#### HOME', '8155551212')", "(815) 555-1212 HOME");
    }

    @Test
    public void testFORMATBYLEN() throws Exception {
        validatePattern(parser, "FORMATBYLEN");

        validateExceptionThrown(parser, "FORMATBYLEN()", "FORMATBYLEN expected 3 parameter(s), but got 0", 1, 12);
        validateExceptionThrown(parser, "FORMATBYLEN('A','B', 'C', 'D')", "FORMATBYLEN expected 3 parameter(s), but got 4", 1, 12);
        validateExceptionThrown(parser, "FORMATBYLEN(1, 'B', 'C')", "FORMATBYLEN parameter 1 expected type STRING, but was NUMBER", 1, 12);
        validateExceptionThrown(parser, "FORMATBYLEN('A', 1, 'C')", "FORMATBYLEN parameter 2 expected type STRING, but was NUMBER", 1, 12);
        validateExceptionThrown(parser, "FORMATBYLEN('A', 'B', 1)", "FORMATBYLEN parameter 3 expected type STRING, but was NUMBER", 1, 12);
        validateExceptionThrown(parser, "FORMATBYLEN('A', '[', 'C')", "Syntax error, missing bracket. Expected ]", 1, 26);
        validateExceptionThrown(parser, "FORMATBYLEN('8155551212', '[0-9*', '?=0=')", "Invalid regex pattern: [0-9*", 1, 27);

        validateStringResult(parser, "FORMATBYLEN(null, null, null)", null);
        validateStringResult(parser, "FORMATBYLEN('a', null, null)", null);
        validateStringResult(parser, "FORMATBYLEN(null, 'b', null)", null);
        validateStringResult(parser, "FORMATBYLEN(null, null, 'c')", null);

        // No match -> "no value"
        String variations = "0='no value':7=      ###-####:10=(###) ###-####:?='Ralph' + ' ' + 'Iden'";
        validateStringResult(parser, "FORMATBYLEN('', '[0-9]*', \"" + variations + "\")", "no value");

        // No match - > empty
        variations = "0=:7=      ###-####:10=(###) ###-####:?='Ralph' + ' ' + 'Iden'";
        validateStringResult(parser, "FORMATBYLEN('', '[0-9]*', \"" + variations + "\")", "");

        // Match pattern of length 7
        validateStringResult(parser, "FORMATBYLEN('5551212', '[0-9]*', \"" + variations + "\")", "      555-1212");

        // Match pattern of length 10
        validateStringResult(parser, "FORMATBYLEN('8155551212', '[0-9]*', \"" + variations + "\")", "(815) 555-1212");

        // Matched, but not one of the specified lengths uses default variation
        validateStringResult(parser, "FORMATBYLEN('81555512121234', '[0-9]*', \"" + variations + "\")", "Ralph Iden");

        // Matched, but not one of the specified lengths and not default returns empty string
        variations = "0=:7=      ###-####:10=(###) ###-####";
        validateStringResult(parser, "FORMATBYLEN('81555512121234', '[0-9]*', \"" + variations + "\")", "");

        // Poorly formed variation patterns return empty strings -----------------------------------------------
        // Match where variation is empty and at the end of the variations
        variations = "7=";
        validateStringResult(parser, "FORMATBYLEN('5551212', '[0-9]*', \"" + variations + "\")", "");

        // Match with a trailing colon in variation string
        variations = "0=:7=      ###-####:10=(###) ###-####:";
        validateStringResult(parser, "FORMATBYLEN('81555512121234', '[0-9]*', \"" + variations + "\")", "");

        // No matches with a trailing colon in variation string
        variations = "0=:7=      ###-####:10=(###) ###-####:";
        validateStringResult(parser, "FORMATBYLEN('A', 'B', \"" + variations + "\")", "");
    }

    @Test
    public void testGUID() throws Exception {
        validatePattern(parser, "GUID");

        validateExceptionThrown(parser, "GUID(1, 2)", "GUID expected 0..1 parameter(s), but got 2", 1, 5);
        validateExceptionThrown(parser, "GUID('A')", "GUID parameter 1 expected type NUMBER, but was STRING", 1, 5);

        validateGUID(parser, "GUID()", false, false, false);
        validateGUID(parser, "GUID(0)", false, false, false);
        validateGUID(parser, "GUID(1)", true, false, false);
        validateGUID(parser, "GUID(2)", false, true, false);
        validateGUID(parser, "GUID(3)", true, true, false);
        validateGUID(parser, "GUID(4)", false, true, true);
        validateGUID(parser, "GUID(5)", true, true, true);
    }

    @Test
    public void testHEX() throws Exception {
        validatePattern(parser, "HEX");

        validateExceptionThrown(parser, "HEX()", "HEX expected 1 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "HEX(123, 'test')", "HEX expected 1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "HEX('123')", "HEX parameter 1 expected type NUMBER, but was STRING", 1, 4);

        validateStringResult(parser, "HEX(null)", null);
        validateStringResult(parser, "HEX(0)", "00");
        validateStringResult(parser, "HEX(10)", "0A");
        validateStringResult(parser, "HEX(-10)", "F6");
        validateStringResult(parser, "HEX(-1000)", "FC18");
        validateStringResult(parser, "HEX(-125000)", "FFFE17B8");
    }

    @Test
    public void testISANYOF() throws Exception {
        validatePattern(parser, "ISANYOF");

        validateExceptionThrown(parser, "ISANYOF()", "ISANYOF expected 1..n parameter(s), but got 0", 1, 8);
        validateExceptionThrown(parser, "ISANYOF(123, '123')", "ISANYOF parameter 1 expected type STRING, but was NUMBER", 1, 8);

        validateBooleanResult(parser, "ISANYOF('alpha', 'alpha', 'beta', 'gamma')", Boolean.TRUE);
        validateBooleanResult(parser, "ISANYOF('beta', 'alpha', 'beta', 'gamma')", Boolean.TRUE);
        validateBooleanResult(parser, "ISANYOF('gamma', 'alpha', 'beta', 'gamma')", Boolean.TRUE);
        validateBooleanResult(parser, "ISANYOF('omega', 'alpha', 'beta', 'gamma')", Boolean.FALSE);

        // Comparison values are coerced to strings
        validateBooleanResult(parser, "ISANYOF('123', 123)", Boolean.TRUE);

        // Case sensitive comparisons
        validateBooleanResult(parser, "ISANYOF('BETA', 'alpha', 'beta', 'gamma')", Boolean.FALSE);
    }

    @Test
    public void testISBLANK() throws Exception {
        validatePattern(parser, "ISBLANK");

        validateExceptionThrown(parser, "ISBLANK()", "ISBLANK expected 1 parameter(s), but got 0", 1, 8);
        validateExceptionThrown(parser, "ISBLANK(123, 'test')", "ISBLANK expected 1 parameter(s), but got 2", 1, 8);

        validateBooleanResult(parser, "ISBLANK(null)", Boolean.TRUE);
        validateBooleanResult(parser, "ISBLANK(NULL)", Boolean.TRUE);
        validateBooleanResult(parser, "ISBLANK('')", Boolean.TRUE);
        validateBooleanResult(parser, "ISBLANK('  ')", Boolean.TRUE);
        validateBooleanResult(parser, "ISBLANK('  \t  ')", Boolean.TRUE);
        validateBooleanResult(parser, "ISBLANK(' test ')", Boolean.FALSE);
    }

    @Test
    public void testISBOOLEAN() throws Exception {
        validatePattern(parser, "ISBOOLEAN");

        validateExceptionThrown(parser, "ISBOOLEAN()", "ISBOOLEAN expected 1 parameter(s), but got 0", 1, 10);
        validateExceptionThrown(parser, "ISBOOLEAN(123, 'test')", "ISBOOLEAN expected 1 parameter(s), but got 2", 1, 10);

        validateBooleanResult(parser, "ISBOOLEAN(NOW())", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN(null)", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN('')", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN('Noway')", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN(1.000000000000001)", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN(0)", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN('0')", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN('0.0')", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN('off')", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN('OFF')", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN('false')", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN('False')", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN(1==0)", Boolean.FALSE);
        validateBooleanResult(parser, "ISBOOLEAN(1)", Boolean.TRUE);
        validateBooleanResult(parser, "ISBOOLEAN(2-1)", Boolean.TRUE);
        validateBooleanResult(parser, "ISBOOLEAN('1.000')", Boolean.TRUE);
        validateBooleanResult(parser, "ISBOOLEAN('on')", Boolean.TRUE);
        validateBooleanResult(parser, "ISBOOLEAN('ON')", Boolean.TRUE);
        validateBooleanResult(parser, "ISBOOLEAN('yes')", Boolean.TRUE);
        validateBooleanResult(parser, "ISBOOLEAN('TRUE')", Boolean.TRUE);
        validateBooleanResult(parser, "ISBOOLEAN('trUe')", Boolean.TRUE);
        validateBooleanResult(parser, "ISBOOLEAN(1==1)", Boolean.TRUE);
    }

    @Test
    public void testISDATE() throws Exception {
        validatePattern(parser, "ISDATE");

        validateExceptionThrown(parser, "ISDATE()", "ISDATE expected 1..2 parameter(s), but got 0", 1, 7);
        validateExceptionThrown(parser, "ISDATE(123)", "ISDATE parameter 1 expected type STRING, but was NUMBER", 1, 7);
        validateExceptionThrown(parser, "ISDATE('2009', 123)", "ISDATE parameter 2 expected type STRING, but was NUMBER", 1, 7);

        validateBooleanResult(parser, "ISDATE('2/29/76')", Boolean.TRUE);
        validateBooleanResult(parser, "ISDATE('2/29/77')", Boolean.FALSE);
        validateBooleanResult(parser, "ISDATE('1/15/2009')", Boolean.TRUE);
        validateBooleanResult(parser, "ISDATE('99/15/2009')", Boolean.FALSE);
        validateBooleanResult(parser, "ISDATE('not-a-date')", Boolean.FALSE);

        validateBooleanResult(parser, "ISDATE(null)", Boolean.FALSE);
        validateBooleanResult(parser, "ISDATE('')", Boolean.FALSE);
        validateBooleanResult(parser, "ISDATE('2009')", Boolean.FALSE);
        validateBooleanResult(parser, "ISDATE('2009','')", Boolean.FALSE);
        validateBooleanResult(parser, "ISDATE('2009','yyyy')", Boolean.TRUE);
    }

    @Test
    public void testISNONEOF() throws Exception {
        validatePattern(parser, "ISNONEOF");

        validateExceptionThrown(parser, "ISNONEOF()", "ISNONEOF expected 1..n parameter(s), but got 0", 1, 9);
        validateExceptionThrown(parser, "ISNONEOF(123, '123')", "ISNONEOF parameter 1 expected type STRING, but was NUMBER", 1, 9);

        validateBooleanResult(parser, "ISNONEOF('alpha', 'alpha', 'beta', 'gamma')", Boolean.FALSE);
        validateBooleanResult(parser, "ISNONEOF('beta', 'alpha', 'beta', 'gamma')", Boolean.FALSE);
        validateBooleanResult(parser, "ISNONEOF('gamma', 'alpha', 'beta', 'gamma')", Boolean.FALSE);
        validateBooleanResult(parser, "ISNONEOF('omega', 'alpha', 'beta', 'gamma')", Boolean.TRUE);

        // Comparison values are coerced to strings
        validateBooleanResult(parser, "ISNONEOF('123', 123)", Boolean.FALSE);

        // Case sensitive comparisons
        validateBooleanResult(parser, "ISNONEOF('BETA', 'alpha', 'beta', 'gamma')", Boolean.TRUE);
    }

    @Test
    public void testISNULL() throws Exception {
        validatePattern(parser, "ISNULL");

        validateExceptionThrown(parser, "ISNULL()", "ISNULL expected 1 parameter(s), but got 0", 1, 7);
        validateExceptionThrown(parser, "ISNULL(123, 'test')", "ISNULL expected 1 parameter(s), but got 2", 1, 7);

        validateBooleanResult(parser, "ISNULL(null)", Boolean.TRUE);
        validateBooleanResult(parser, "ISNULL(NULL)", Boolean.TRUE);
        validateBooleanResult(parser, "ISNULL('')", Boolean.FALSE);
        validateBooleanResult(parser, "ISNULL('  ')", Boolean.FALSE);
    }

    @Test
    public void testISNUMBER() throws Exception {
        validatePattern(parser, "ISNUMBER");

        validateExceptionThrown(parser, "ISNUMBER()", "ISNUMBER expected 1 parameter(s), but got 0", 1, 9);
        validateExceptionThrown(parser, "ISNUMBER(123, 'test')", "ISNUMBER expected 1 parameter(s), but got 2", 1, 9);

        // This will create a NUMBER token, which will be examined by its string value later
        parser.eval("N=123.45/1.0");

        validateBooleanResult(parser, "ISNUMBER(null)", Boolean.FALSE);
        validateBooleanResult(parser, "ISNUMBER(NULL)", Boolean.FALSE);
        validateBooleanResult(parser, "ISNUMBER('')", Boolean.FALSE);
        validateBooleanResult(parser, "ISNUMBER('  ')", Boolean.FALSE);
        validateBooleanResult(parser, "ISNUMBER('123')", Boolean.TRUE);
        validateBooleanResult(parser, "ISNUMBER('123.45')", Boolean.TRUE);
        validateBooleanResult(parser, "ISNUMBER('-123')", Boolean.TRUE);
        validateBooleanResult(parser, "ISNUMBER('-123.45')", Boolean.TRUE);
        validateBooleanResult(parser, "ISNUMBER('1.4E12')", Boolean.TRUE);
        validateBooleanResult(parser, "ISNUMBER(PI)", Boolean.TRUE);
        validateBooleanResult(parser, "ISNUMBER(N)", Boolean.TRUE);
    }

    @Test
    public void testLEFT() throws Exception {
        validatePattern(parser, "LEFT");

        validateExceptionThrown(parser, "LEFT()", "LEFT expected 2 parameter(s), but got 0", 1, 5);
        validateExceptionThrown(parser, "LEFT('test')", "LEFT expected 2 parameter(s), but got 1", 1, 5);
        validateExceptionThrown(parser, "LEFT('test', 2, 2)", "LEFT expected 2 parameter(s), but got 3", 1, 5);
        validateExceptionThrown(parser, "LEFT(123, 2)", "LEFT parameter 1 expected type STRING, but was NUMBER", 1, 5);

        validateStringResult(parser, "LEFT(null, 2)", null);
        validateStringResult(parser, "LEFT('', 3)", "");
        validateStringResult(parser, "LEFT('12345', null)", "12345");
        validateStringResult(parser, "LEFT('12345', 0)", "");
        validateStringResult(parser, "LEFT('12345', -10)", "");
        validateStringResult(parser, "LEFT('12345', 3)", "123");
        validateStringResult(parser, "LEFT('12', 5)", "12");
        validateStringResult(parser, "LEFT(RIGHT('12', 2), 5)", "12");
    }

    @Test
    public void testLEFTOF() throws Exception {
        validatePattern(parser, "LEFTOF");

        validateExceptionThrown(parser, "LEFTOF()", "LEFTOF expected 2 parameter(s), but got 0", 1, 7);
        validateExceptionThrown(parser, "LEFTOF('test')", "LEFTOF expected 2 parameter(s), but got 1", 1, 7);
        validateExceptionThrown(parser, "LEFTOF('riden@myemail.org', '@', 2)", "LEFTOF expected 2 parameter(s), but got 3", 1, 7);
        validateExceptionThrown(parser, "LEFTOF(123, '@')", "LEFTOF parameter 1 expected type STRING, but was NUMBER", 1, 7);
        validateExceptionThrown(parser, "LEFTOF('123', 123)", "LEFTOF parameter 2 expected type STRING, but was NUMBER", 1, 7);

        validateStringResult(parser, "LEFTOF(null, '@')", null);
        validateStringResult(parser, "LEFTOF('riden@myemail.org', null)", "riden@myemail.org");
        validateStringResult(parser, "LEFTOF('riden@myemail.org', '@')", "riden");
        validateStringResult(parser, "LEFTOF('riden@myemail.org', '<->')", "riden@myemail.org");
        validateStringResult(parser, "LEFTOF('left<->right', '<->')", "left");
    }

    @Test
    public void testLEN() throws Exception {
        validatePattern(parser, "LEN");

        validateExceptionThrown(parser, "LEN()", "LEN expected 1 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "LEN('test', 'test')", "LEN expected 1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "LEN(123)", "LEN parameter 1 expected type STRING, but was NUMBER", 1, 4);

        validateNumericResult(parser, "LEN('')", "0");
        validateNumericResult(parser, "LEN(null)", "0");
        validateNumericResult(parser, "LEN('Hello')", "5");
    }

    @Test
    public void testLOG() throws Exception {
        validatePattern(parser, "LOG");

        validateExceptionThrown(parser, "LOG()", "LOG expected 1 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "LOG(0.01, 2)", "LOG expected 1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "LOG('1.23')", "LOG parameter 1 expected type NUMBER, but was STRING", 1, 4);

        validateNumericResult(parser, "LOG(null)", null);
        validateNumericResult(parser, "LOG(1)", "0.0");
        validateNumericResult(parser, "LOG(2)", "0.6931471805599453");
    }

    @Test
    public void testLOG10() throws Exception {
        validatePattern(parser, "LOG10");

        validateExceptionThrown(parser, "LOG10()", "LOG10 expected 1 parameter(s), but got 0", 1, 6);
        validateExceptionThrown(parser, "LOG10(0.01, 2)", "LOG10 expected 1 parameter(s), but got 2", 1, 6);
        validateExceptionThrown(parser, "LOG10('1.23')", "LOG10 parameter 1 expected type NUMBER, but was STRING", 1, 6);

        validateNumericResult(parser, "LOG10(null)", null);
        validateNumericResult(parser, "LOG10(1)", "0.0");
        validateNumericResult(parser, "LOG10(2)", "0.3010299956639812");
    }

    @Test
    public void testLOWER() throws Exception {
        validatePattern(parser, "LOWER");

        validateExceptionThrown(parser, "LOWER()", "LOWER expected 1 parameter(s), but got 0", 1, 6);
        validateExceptionThrown(parser, "LOWER('test', 'test')", "LOWER expected 1 parameter(s), but got 2", 1, 6);
        validateExceptionThrown(parser, "LOWER(123)", "LOWER parameter 1 expected type STRING, but was NUMBER", 1, 6);

        validateStringResult(parser, "LOWER(null)", null);
        validateStringResult(parser, "LOWER('')", "");
        validateStringResult(parser, "LOWER('HellO, WORLD')", "hello, world");
    }

    @Test
    public void testMAKEBOOLEAN() throws Exception {
        validatePattern(parser, "MAKEBOOLEAN");

        validateExceptionThrown(parser, "MAKEBOOLEAN()", "MAKEBOOLEAN expected 1 parameter(s), but got 0", 1, 12);
        validateExceptionThrown(parser, "MAKEBOOLEAN('test', 'test')", "MAKEBOOLEAN expected 1 parameter(s), but got 2", 1, 12);

        validateBooleanResult(parser, "MAKEBOOLEAN(NOW())", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN(null)", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN('')", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN('Noway')", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN(1.000000000000001)", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN(0)", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN('0')", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN('0.0')", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN('off')", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN('OFF')", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN('false')", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN('False')", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN(1==0)", Boolean.FALSE);
        validateBooleanResult(parser, "MAKEBOOLEAN(1)", Boolean.TRUE);
        validateBooleanResult(parser, "MAKEBOOLEAN(2-1)", Boolean.TRUE);
        validateBooleanResult(parser, "MAKEBOOLEAN('1.000')", Boolean.TRUE);
        validateBooleanResult(parser, "MAKEBOOLEAN('on')", Boolean.TRUE);
        validateBooleanResult(parser, "MAKEBOOLEAN('ON')", Boolean.TRUE);
        validateBooleanResult(parser, "MAKEBOOLEAN('yes')", Boolean.TRUE);
        validateBooleanResult(parser, "MAKEBOOLEAN('TRUE')", Boolean.TRUE);
        validateBooleanResult(parser, "MAKEBOOLEAN('trUe')", Boolean.TRUE);
        validateBooleanResult(parser, "MAKEBOOLEAN(1==1)", Boolean.TRUE);
    }

    @Test
    public void testMAKEDATE_mdyhms() throws Exception {
        validatePattern(parser, "MAKEDATE");
        validateExceptionThrown(parser, "MAKEDATE()", "MAKEDATE expected 1..6 parameter(s), but got 0", 1, 9);
        validateExceptionThrown(parser, "MAKEDATE(1,2,3,4,5,6,7)", "MAKEDATE expected 1..6 parameter(s), but got 7", 1, 9);

        validateExceptionThrown(parser, "MAKEDATE(0, 3, 2009)", "Error: Invalid value MONTH", 1, 10);
        validateExceptionThrown(parser, "MAKEDATE(4, 53, 2009)", "Error: Invalid value DAY_OF_MONTH", 1, 13);
        validateExceptionThrown(parser, "MAKEDATE(4, 3, -2009)", "Error: Invalid value YEAR", 1, 16);
        validateExceptionThrown(parser, "MAKEDATE(4, 3, 2009, 99)", "Error: Invalid value HOUR_OF_DAY", 1, 22);
        validateExceptionThrown(parser, "MAKEDATE(4, 3, 2009, 13, 99)", "Error: Invalid value MINUTE", 1, 26);
        validateExceptionThrown(parser, "MAKEDATE(4, 3, 2009, 13, 14, 99)", "Error: Invalid value SECOND", 1, 30);

        validateDateResult(parser, "MAKEDATE(2, 29, 76)", makeDate(2, 29, 1976, 0, 0, 0));
        validateDateResult(parser, "MAKEDATE(4, 3, 2009)", makeDate(4, 3, 2009, 0, 0,0));
        validateDateResult(parser, "MAKEDATE(4, 3, 2009, 13)", makeDate(4, 3, 2009, 13, 0,0));
        validateDateResult(parser, "MAKEDATE(4, 3, 2009, 13, 14)", makeDate(4, 3, 2009, 13, 14,0));
        validateDateResult(parser, "MAKEDATE(4, 3, 2009, 13, 14, 15)", makeDate(4, 3, 2009, 13, 14,15));
    }

    @Test
    public void testMAKEDATE_parse() throws Exception {
        validateExceptionThrown(parser, "MAKEDATE('2009/04/03', 'mm-dd-yy', 123)", "MAKEDATE expected 1..2 parameter(s), but got 3", 1, 24);
        validateExceptionThrown(parser, "MAKEDATE('2009/04/03', '')", "Expected a non-empty value for format string", 1, 24);
        validateExceptionThrown(parser, "MAKEDATE('2009/04/03', 123)", "Type mismatch error. Expected STRING, but was NUMBER", 1, 24);

        // Build the timezone offset used for the test. Doing this will allow the test
        // to run across multiple timezones and handle Daylight Saving or Summer Time.
        TimeZone tz = TimeZone.getDefault();
        int hoursOffset = Math.abs(tz.getRawOffset() / 3600000);
        String tzOffset = hoursOffset + "00";
        if (hoursOffset < 12) {
            tzOffset = "0" + tzOffset;
        }
        if (hoursOffset >= 0) {
            tzOffset = "-" + tzOffset;
        }

        String timeZoneShortForm = tz.getDisplayName(false, TimeZone.SHORT);

        Date dtDate = makeDate(1, 15, 2009,0, 0, 0);
        Date dtDateLastCentury = makeDate(1, 15, 60,0, 0, 0);
        Date dtDateTime = makeDate( 1, 15, 2009, 7, 32, 59);
        Date dtDateTime24 = makeDate(1, 15, 2009, 21, 32, 59);
        Date dtDateTimeMilli = makeDate(1, 15, 2009,7, 32, 59);
        Date dtDateTimeMilli24 = makeDate(1, 15, 2009,21, 32, 59);
        Date dtTime = makeDate(1, 1, 1970, 7, 32, 59);
        Date dtTime24 = makeDate(1, 1, 1970, 21, 32, 59);

        Object[][] testData = new Object[][] {
            {"20090115", dtDate},
            {"2009/01/15", dtDate},
            {"2009/1/15", dtDate},
            {"01/15/2009", dtDate},
            {"1/15/2009", dtDate},
            {"1/15/60", dtDateLastCentury},
            {"Jan 15 2009 07:32:59 AM", dtDateTime},
            {"Jan 15 2009 07:32:59.123 AM", dtDateTimeMilli},
            {"2009-01-15", dtDate},
            {"2009-01-15 07:32:59", dtDateTime},
            {"2009-01-15 21:32:59", dtDateTime24},
            {"2009-01-15 07:32:59.123", dtDateTimeMilli},
            {"2009-01-15 21:32:59.123", dtDateTimeMilli24},
            {"2009-01-15T07:32:59" + tzOffset, dtDateTime},
            {"2009-01-15T07:32:59" + timeZoneShortForm, dtDateTime},
            {"2009-01-15T21:32:59" + tzOffset, dtDateTime24},
            {"2009-01-15T07:32:59.123", dtDateTimeMilli},
            {"2009-01-15T21:32:59.123", dtDateTimeMilli24},
            {"07:32:59", dtTime},
            {"21:32:59", dtTime24},
        };

        for (int i = 0; i < testData.length; i++) {
            String testDate = "MAKEDATE('" + testData[i][0].toString() + "')";
            validateDateResult(parser, testDate, (Date)testData[i][1]);
        }

        // Custom format: 'TODAY: 'MM-dd-yyyy
        validateDateNotParseable(parser, "MAKEDATE('TODAY: 01-15-2009')");
        validateDateResult(parser, "MAKEDATE('TODAY: 01-15-2009', \"'TODAY: 'MM-dd-yyyy\")", dtDate);

        // Switch out patterns so the custom pattern is the only one accepted
        String[] orgPatterns = toolbox.getDatePatterns();
        try {
            validateDateNotParseable(parser, "MAKEDATE('TODAY: 01-15-2009')");
            toolbox.setDatePatterns(new String[] {"'TODAY: 'MM-dd-yyyy"});
            validateDateNotParseable(parser, "MAKEDATE('01-15-2009')");
            validateDateResult(parser, "MAKEDATE('TODAY: 01-15-2009')", dtDate);
        } finally {
            toolbox.setDatePatterns(orgPatterns);
        }
    }

    @Test
    public void testMATCH() throws Exception {
        validatePattern(parser, "MATCH");

        validateExceptionThrown(parser, "MATCH()", "MATCH expected 2 parameter(s), but got 0", 1, 6);
        validateExceptionThrown(parser, "MATCH('A','B', 1)", "MATCH expected 2 parameter(s), but got 3", 1, 6);
        validateExceptionThrown(parser, "MATCH(1, '1.23')", "MATCH parameter 1 expected type STRING, but was NUMBER", 1, 6);
        validateExceptionThrown(parser, "MATCH('1.23', 1)", "MATCH parameter 2 expected type STRING, but was NUMBER", 1, 6);
        validateExceptionThrown(parser, "MATCH('1.23', '[')", "Syntax error, missing bracket. Expected ]", 1, 18);
        validateExceptionThrown(parser, "MATCH('1.23', '+')", "Invalid regex pattern: +", 1, 15);

        validateArray(parser, "MATCH(null, null)", null);
        validateArray(parser, "MATCH('a', null)", null);
        validateArray(parser, "MATCH(null, 'b')", null);
        validateArray(parser, "MATCH(null, 'b')", null);
        validateArray(parser, "MATCH('Phone:', '[\\(](\\d{3})\\D*(\\d{3})\\D*(\\d{4})\\D*(\\d*)')", ""); // no match
        validateArray(parser, "MATCH('Phone: (815) 555-1212 x100 (Work)', '[\\(](\\d{3})\\D*(\\d{3})\\D*(\\d{4})\\D*(\\d*)')",
                "(815) 555-1212 x100",  // asString
                "(815) 555-1212 x100", "815", "555", "1212", "100");  // group 0..n
    }

    @Test
    public void testMAX() throws Exception {
        validatePattern(parser, "MAX");

        validateExceptionThrown(parser, "MAX()","MAX expected 2 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "MAX(0.01, 2, 1)","MAX expected 2 parameter(s), but got 3", 1, 4);
        validateExceptionThrown(parser, "MAX('1.23', 1)","MAX parameter 1 expected type NUMBER, but was STRING", 1, 4);
        validateExceptionThrown(parser, "MAX(1, '1.23')","MAX parameter 2 expected type NUMBER, but was STRING", 1, 4);

        validateNumericResult(parser, "MAX(null, null)",null);
        validateNumericResult(parser, "MAX(1, null)",null);
        validateNumericResult(parser, "MAX(null, 2)",null);
        validateNumericResult(parser, "MAX(-1, 4)","4");
        validateNumericResult(parser, "MAX(4, -1)","4");
        validateNumericResult(parser, "MAX(-4, -1)","-1");
        validateNumericResult(parser, "MAX(4, 4)","4");
        validateNumericResult(parser, "MAX(-1, -1)","-1");
    }

    @Test
    public void testMID() throws Exception {
        validatePattern(parser, "MID");

        validateExceptionThrown(parser, "MID()","MID expected 2..3 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "MID('test')","MID expected 2..3 parameter(s), but got 1", 1, 4);
        validateExceptionThrown(parser, "MID('test', 1, 2, 3)","MID expected 2..3 parameter(s), but got 4", 1, 4);
        validateExceptionThrown(parser, "MID(123, 1)","MID parameter 1 expected type STRING, but was NUMBER", 1, 4);
        validateExceptionThrown(parser, "MID('123', '1')","MID parameter 2 expected type NUMBER, but was STRING", 1, 4);
        validateExceptionThrown(parser, "MID('123', 1, '2')","MID parameter 3 expected type NUMBER, but was STRING", 1, 4);

        validateStringResult(parser, "MID(null, null)",null);
        validateStringResult(parser, "MID(null, null, null)",null);
        validateStringResult(parser, "MID('test', null)",null);
        validateStringResult(parser, "MID(null, 2)",null);
        validateStringResult(parser, "MID('test', 2, null)",null);
        validateStringResult(parser, "MID('', 3)","");
        validateStringResult(parser, "MID('12345', 0)","");
        validateStringResult(parser, "MID('12345', -10)","");
        validateStringResult(parser, "MID('12', 5)","");
        validateStringResult(parser, "MID('12345', 3)","345");
        validateStringResult(parser, "MID('12345', 3, 1)","3");
        validateStringResult(parser, "MID('12345', 3, 2)","34");
        validateStringResult(parser, "MID('12345', 3, 3)","345");
        validateStringResult(parser, "MID('12345', 3, 4)","345");
    }

    @Test
    public void testMIN() throws Exception {
        validatePattern(parser, "MIN");

        validateExceptionThrown(parser, "MIN()","MIN expected 2 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "MIN(0.01, 2, 1)","MIN expected 2 parameter(s), but got 3", 1, 4);
        validateExceptionThrown(parser, "MIN('1.23', 1)","MIN parameter 1 expected type NUMBER, but was STRING", 1, 4);
        validateExceptionThrown(parser, "MIN(1, '1.23')","MIN parameter 2 expected type NUMBER, but was STRING", 1, 4);

        validateNumericResult(parser, "MIN(null, null)",null);
        validateNumericResult(parser, "MIN(1, null)",null);
        validateNumericResult(parser, "MIN(null, 2)",null);
        validateNumericResult(parser, "MIN(-1, 4)","-1");
        validateNumericResult(parser, "MIN(4, -1)","-1");
        validateNumericResult(parser, "MIN(-4, -1)","-4");
        validateNumericResult(parser, "MIN(4, 4)","4");
        validateNumericResult(parser, "MIN(-1, -1)","-1");
    }

    @Test
    public void testNAMECASE() throws Exception {
        validatePattern(parser, "NAMECASE");

        validateExceptionThrown(parser, "NAMECASE()","NAMECASE expected 1 parameter(s), but got 0", 1, 9);
        validateExceptionThrown(parser, "NAMECASE('test', 'test')","NAMECASE expected 1 parameter(s), but got 2", 1, 9);
        validateExceptionThrown(parser, "NAMECASE(123)","NAMECASE parameter 1 expected type STRING, but was NUMBER", 1, 9);

        validateStringResult(parser, "NAMECASE(null)",null);
        validateStringResult(parser, "NAMECASE('')","");
        validateStringResult(parser, "NAMECASE('ralph iden')","Ralph Iden");
        validateStringResult(parser, "NAMECASE('RALPH IDEN')","Ralph Iden");
        validateStringResult(parser, "NAMECASE('Ralph Iden')","Ralph Iden");
        validateStringResult(parser, "NAMECASE('  RALPH\t\tW. IDEN  ')","  Ralph\t\tW. Iden  ");
    }

    @Test
    public void testRANDOM() throws Exception {
        validatePattern(parser, "RANDOM");

        validateExceptionThrown(parser, "RANDOM(NULL)","The following parameter(s) cannot be null: 0", 1, 1);
        validateExceptionThrown(parser, "RANDOM(1,2,3)","RANDOM expected 0..2 parameter(s), but got 3", 1, 7);
        validateExceptionThrown(parser, "RANDOM('123', 2)","RANDOM parameter 1 expected type NUMBER, but was STRING", 1, 7);
        validateExceptionThrown(parser, "RANDOM(1, '123')","RANDOM parameter 2 expected type NUMBER, but was STRING", 1, 7);

        assertTrue(parser.eval("RANDOM()").asString().startsWith("0"));
        assertTrue(parser.eval("RANDOM(0)").asString().startsWith("0"));

        Value result;
        BigDecimal bdMax = new BigDecimal("100");
        for (int i = 0; i < 1000; i++) {
            result = parser.eval( "RANDOM(100)");
            if (result.asNumber().compareTo(BigDecimal.ZERO) < 0 ||
                    result.asNumber().compareTo(bdMax) > 0) {
                fail("Random number outside of range (0..100)");
            }
        }

        BigDecimal bdMin1 = new BigDecimal("50");
        BigDecimal bdMax1 = new BigDecimal("99");
        for (int i = 0; i < 1000; i++) {
            // Reverse low/high periodically to verify function swaps them back
            String exp = (i % 15 == 0) ? "RANDOM(99, 50)" : "RANDOM(50, 99)";
            result = parser.eval(exp);
            if (result.asNumber().compareTo(bdMin1) < 0 ||
                    result.asNumber().compareTo(bdMax1) > 0) {
                fail("Random number outside of range (50..99)");
            }
        }
    }

    @Test
    public void testREPLACE() throws Exception {
        validatePattern(parser, "REPLACE");

        validateExceptionThrown(parser, "REPLACE()","REPLACE expected 3 parameter(s), but got 0", 1, 8);
        validateExceptionThrown(parser, "REPLACE('test', 'me')","REPLACE expected 3 parameter(s), but got 2", 1, 8);
        validateExceptionThrown(parser, "REPLACE(123, '2', '3')","REPLACE parameter 1 expected type STRING, but was NUMBER", 1, 8);
        validateExceptionThrown(parser, "REPLACE('1', 2, '3')","REPLACE parameter 2 expected type STRING, but was NUMBER", 1, 8);
        validateExceptionThrown(parser, "REPLACE('1', '2', 3)","REPLACE parameter 3 expected type STRING, but was NUMBER", 1, 8);

        validateStringResult(parser, "REPLACE(null, null, null)",null);
        validateStringResult(parser, "REPLACE('Ralph', null, null)","Ralph");
        validateStringResult(parser, "REPLACE('Mr. Ralph', null, 'Jeremy')","Mr. Ralph");
        validateStringResult(parser, "REPLACE('Mr. Ralph', 'Ralph', null)","Mr. Ralph");
        validateStringResult(parser, "REPLACE('Mr. Ralph', 'Ralph', 'Jeremy')","Mr. Jeremy");
    }

    @Test
    public void testREPLACEALL() throws Exception {
        validatePattern(parser, "REPLACEALL");

        validateExceptionThrown(parser, "REPLACEALL()","REPLACEALL expected 3 parameter(s), but got 0", 1, 11);
        validateExceptionThrown(parser, "REPLACEALL('test', 'me')","REPLACEALL expected 3 parameter(s), but got 2", 1, 11);
        validateExceptionThrown(parser, "REPLACEALL(123, '2', '3')","REPLACEALL parameter 1 expected type STRING, but was NUMBER", 1, 11);
        validateExceptionThrown(parser, "REPLACEALL('1', 2, '3')","REPLACEALL parameter 2 expected type STRING, but was NUMBER", 1, 11);
        validateExceptionThrown(parser, "REPLACEALL('1', '2', 3)","REPLACEALL parameter 3 expected type STRING, but was NUMBER", 1, 11);

        validateStringResult(parser, "REPLACEALL(null, null, null)",null);
        validateStringResult(parser, "REPLACEALL('Ralph', null, null)","Ralph");
        validateStringResult(parser, "REPLACEALL('Ralph', 'al', null)","Ralph");
        validateStringResult(parser, "REPLACEALL('Ralph', null, 'A')","Ralph");
        validateStringResult(parser, "REPLACEALL('boo:and:foo', ':', '-')","boo-and-foo");
        validateStringResult(parser, "REPLACEALL('abbbcabc 000 abc', 'abc', '123')","abbbc123 000 123");
        validateStringResult(parser, "REPLACEALL('acabc 000 abc', 'ab*c', '123')","123123 000 123");
        validateStringResult(parser, "REPLACEALL('acabc 000 abc', 'ab+c', '123')","ac123 000 123");
        validateStringResult(parser, "REPLACEALL('abbbcabc 000 abc', 'ab+c', '123')","123123 000 123");
    }

    @Test
    public void testREPLACEFIRST() throws Exception {
        validatePattern(parser, "REPLACEFIRST");

        validateExceptionThrown(parser, "REPLACEFIRST()","REPLACEFIRST expected 3 parameter(s), but got 0", 1, 13);
        validateExceptionThrown(parser, "REPLACEFIRST('test', 'me')","REPLACEFIRST expected 3 parameter(s), but got 2", 1, 13);
        validateExceptionThrown(parser, "REPLACEFIRST(123, '2', '3')","REPLACEFIRST parameter 1 expected type STRING, but was NUMBER", 1, 13);
        validateExceptionThrown(parser, "REPLACEFIRST('1', 2, '3')","REPLACEFIRST parameter 2 expected type STRING, but was NUMBER", 1, 13);
        validateExceptionThrown(parser, "REPLACEFIRST('1', '2', 3)","REPLACEFIRST parameter 3 expected type STRING, but was NUMBER", 1, 13);

        validateStringResult(parser, "REPLACEFIRST(null, null, null)",null);
        validateStringResult(parser, "REPLACEFIRST('Ralph', null, null)","Ralph");
        validateStringResult(parser, "REPLACEFIRST('Ralph', 'al', null)","Ralph");
        validateStringResult(parser, "REPLACEFIRST('Ralph', null, 'A')","Ralph");
        validateStringResult(parser, "REPLACEFIRST('boo:and:foo', ':', '-')","boo-and:foo");
        validateStringResult(parser, "REPLACEFIRST('abbbcabc 000 abc', 'abc', '123')","abbbc123 000 abc");
        validateStringResult(parser, "REPLACEFIRST('acabc 000 abc', 'ab*c', '123')","123abc 000 abc");
        validateStringResult(parser, "REPLACEFIRST('acabc 000 abc', 'ab+c', '123')","ac123 000 abc");
        validateStringResult(parser, "REPLACEFIRST('abbbcabc 000 abc', 'ab+c', '123')","123abc 000 abc");
    }

    @Test
    public void testRIGHT() throws Exception {
        validatePattern(parser, "RIGHT");

        validateExceptionThrown(parser, "RIGHT()","RIGHT expected 2 parameter(s), but got 0", 1, 6);
        validateExceptionThrown(parser, "RIGHT('test')","RIGHT expected 2 parameter(s), but got 1", 1, 6);
        validateExceptionThrown(parser, "RIGHT('test', 2, 2)","RIGHT expected 2 parameter(s), but got 3", 1, 6);
        validateExceptionThrown(parser, "RIGHT(123, 2)","RIGHT parameter 1 expected type STRING, but was NUMBER", 1, 6);

        validateStringResult(parser, "RIGHT(null, 2)",null);
        validateStringResult(parser, "RIGHT('', 3)","");
        validateStringResult(parser, "RIGHT('12345', null)","12345");
        validateStringResult(parser, "RIGHT('12345', 0)","");
        validateStringResult(parser, "RIGHT('12345', -10)","");
        validateStringResult(parser, "RIGHT('12345', 3)","345");
        validateStringResult(parser, "RIGHT('12', 5)","12");
        validateStringResult(parser, "RIGHT(LEFT('12345', 3), 2)","23");
    }

    @Test
    public void testRIGHTOF() throws Exception {
        validatePattern(parser, "RIGHTOF");

        validateExceptionThrown(parser, "RIGHTOF()", "RIGHTOF expected 2 parameter(s), but got 0", 1, 8);
        validateExceptionThrown(parser, "RIGHTOF('test')", "RIGHTOF expected 2 parameter(s), but got 1", 1, 8);
        validateExceptionThrown(parser, "RIGHTOF('riden@myemail.org', '@', 2)", "RIGHTOF expected 2 parameter(s), but got 3", 1, 8);
        validateExceptionThrown(parser, "RIGHTOF(123, '@')", "RIGHTOF parameter 1 expected type STRING, but was NUMBER", 1, 8);
        validateExceptionThrown(parser, "RIGHTOF('123', 123)", "RIGHTOF parameter 2 expected type STRING, but was NUMBER", 1, 8);

        validateStringResult(parser, "RIGHTOF(null, '@')", null);
        validateStringResult(parser, "RIGHTOF('riden@myemail.org', null)", "");
        validateStringResult(parser, "RIGHTOF('riden@myemail.org', '@')", "myemail.org");
        validateStringResult(parser, "RIGHTOF('riden@myemail.org', '<->')", "");
        validateStringResult(parser, "RIGHTOF('left<->right', '<->')", "right");
    }

    @Test
    public void testSIN() throws Exception {
        validatePattern(parser, "SIN");

        validateExceptionThrown(parser, "SIN()","SIN expected 1 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "SIN(0.01, 2)","SIN expected 1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "SIN('1.23')","SIN parameter 1 expected type NUMBER, but was STRING", 1, 4);

        validateNumericResult(parser, "SIN(null)",null);
        validateNumericResult(parser, "SIN(0)","0");
        validateNumericResult(parser, "SIN(1)","0.01745");
        validateNumericResult(parser, "SIN(22)","0.37461");
        validateNumericResult(parser, "SIN(45)","0.70711");
    }

    @Test
    public void testSPLIT() throws Exception {
        validatePattern(parser, "SPLIT");

        validateExceptionThrown(parser, "SPLIT()","SPLIT expected 1..3 parameter(s), but got 0", 1, 6);
        validateExceptionThrown(parser, "SPLIT('a,b,c', ',', 2, 3)","SPLIT expected 1..3 parameter(s), but got 4", 1, 6);
        validateExceptionThrown(parser, "SPLIT(1.23)","SPLIT parameter 1 expected type STRING, but was NUMBER", 1, 6);
        validateExceptionThrown(parser, "SPLIT('a,b,c', 2)","SPLIT parameter 2 expected type STRING, but was NUMBER", 1, 6);
        validateExceptionThrown(parser, "SPLIT('a,b,c', ',', 'a')","SPLIT parameter 3 expected type NUMBER, but was STRING", 1, 6);

        validateArray(parser, "SPLIT(null)",null);
        validateArray(parser, "SPLIT(null, null)",null);
        validateArray(parser, "SPLIT('')","", "");
        validateArray(parser, "SPLIT('Ralph')","Ralph", "Ralph");
        validateArray(parser, "SPLIT('Ralph', null)","Ralph", "Ralph");
        validateArray(parser, "SPLIT('Ralph', null, null)","Ralph", "Ralph");

        validateArray(parser, "SPLIT('Ralph,Iden')","Ralph", "Ralph", "Iden");
        validateArray(parser, "SPLIT('boo:and:foo', ':', 2)","boo", "boo", "and:foo");
        validateArray(parser, "SPLIT('boo:and:foo', ':', 5)","boo", "boo", "and", "foo");
        validateArray(parser, "SPLIT('boo:and:foo', ':', -2)","boo", "boo", "and", "foo");
        validateArray(parser, "SPLIT('boo:and:foo', 'o', 5)","b", "b", "", ":and:f", "", "");
        validateArray(parser, "SPLIT('boo:and:foo', 'o', -2)","b", "b", "", ":and:f", "", "");
        validateArray(parser, "SPLIT('boo:and:foo', 'o', 0)","b", "b", "", ":and:f");
    }

    @Test
    public void testSQR() throws Exception {
        validatePattern(parser, "SQR");

        validateExceptionThrown(parser, "SQR()", "SQR expected 1 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "SQR(1, 2)", "SQR expected 1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "SQR('123')", "SQR parameter 1 expected type NUMBER, but was STRING", 1, 4);

        validateNumericResult(parser, "SQR(null)", null);
        validateNumericResult(parser, "SQR(0)", "0");
        validateNumericResult(parser, "SQR(9)", "81");
        validateNumericResult(parser, "SQR(-2)", "4");
    }

    @Test
    public void testSQRT() throws Exception {
        validatePattern(parser, "SQRT");

        validateExceptionThrown(parser, "SQRT()", "SQRT expected 1 parameter(s), but got 0", 1, 5);
        validateExceptionThrown(parser, "SQRT(1, 2)", "SQRT expected 1 parameter(s), but got 2", 1, 5);
        validateExceptionThrown(parser, "SQRT('123')", "SQRT parameter 1 expected type NUMBER, but was STRING", 1, 5);
        validateExceptionThrown(parser, "SQRT(-2)", "Infinite or NaN", 1, 6);

        validateNumericResult(parser, "SQRT(null)", null);
        validateNumericResult(parser, "SQRT(0)", "0");
        validateNumericResult(parser, "SQRT(81)", "9");
   }

    @Test
    public void testSTARTSWITH() throws Exception {
        validatePattern(parser, "STARTSWITH");

        validateExceptionThrown(parser, "STARTSWITH()", "STARTSWITH expected 2 parameter(s), but got 0", 1, 11);
        validateExceptionThrown(parser, "STARTSWITH('A', 'B', 2)", "STARTSWITH expected 2 parameter(s), but got 3", 1, 11);
        validateExceptionThrown(parser, "STARTSWITH(1, 'B')", "STARTSWITH parameter 1 expected type STRING, but was NUMBER", 1, 11);
        validateExceptionThrown(parser, "STARTSWITH('A', 2)", "STARTSWITH parameter 2 expected type STRING, but was NUMBER", 1, 11);

        validateBooleanResult(parser, "STARTSWITH(null, null)", Boolean.FALSE);
        validateBooleanResult(parser, "STARTSWITH('A', null)", Boolean.FALSE);
        validateBooleanResult(parser, "STARTSWITH(null, 'B')", Boolean.FALSE);
        validateBooleanResult(parser, "STARTSWITH('Ralph', 'I')", Boolean.FALSE);
        validateBooleanResult(parser, "STARTSWITH('Ralph', 'ra')", Boolean.FALSE);
        validateBooleanResult(parser, "STARTSWITH('Ralph', 'Ra')", Boolean.TRUE);
        validateBooleanResult(parser, "STARTSWITH('Ralph', Upper('ra'))", Boolean.FALSE);
    }

    @Test
    public void testSTR() throws Exception {
        validatePattern(parser, "STR");

        validateExceptionThrown(parser, "STR()", "STR expected 1..3 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "STR(123.45, 6, 2, 'test')", "STR expected 1..3 parameter(s), but got 4", 1, 4);
        validateExceptionThrown(parser, "STR('123')", "STR parameter 1 expected type NUMBER, but was STRING", 1, 4);
        validateExceptionThrown(parser, "STR(123, '1')", "STR parameter 2 expected type NUMBER, but was STRING", 1, 4);
        validateExceptionThrown(parser, "STR(123, 1, '1')", "STR parameter 3 expected type NUMBER, but was STRING", 1, 4);

        // Null arguments return null
        validateStringResult(parser, "STR(null)", null);
        validateStringResult(parser, "STR(null, null)", null);
        validateStringResult(parser, "STR(null, null, null)", null);
        validateStringResult(parser, "STR(1, null)", null);
        validateStringResult(parser, "STR(1, 2, null)", null);
        validateStringResult(parser, "STR(null, 2, null)", null);
        validateStringResult(parser, "STR(null, null, 3)", null);

        // Simple conversion, no width or precision values
        validateStringResult(parser, "STR(null)", null);
        validateStringResult(parser, "STR(0)", "0");
        validateStringResult(parser, "STR(123.45)", "123.45");
        validateStringResult(parser, "STR(-123.45)", "-123.45");

        // Conversion with width parameter
        validateStringResult(parser, "STR(0, 3)", "0");
        validateStringResult(parser, "STR(123.45, 3)", "123");
        validateStringResult(parser, "STR(123.4548, 3)", "123");
        validateStringResult(parser, "STR(1234.4548, 3)", "1234");
        validateStringResult(parser, "STR(-123.4548, 3)", "-123");
        validateStringResult(parser, "STR(-1234.4548, 3)", "-1234");

        // Conversion with width and precision parameters
        validateStringResult(parser, "STR(0, 3, 1)", "0.0");
        validateStringResult(parser, "STR(123.45, 3, 1)", "123.5");
        validateStringResult(parser, "STR(123.454, 6, 2)", "123.45");
        validateStringResult(parser, "STR(123.455, 6, 2)", "123.46");
        validateStringResult(parser, "STR(1234.454, 7, 3)", "1234.454");
        validateStringResult(parser, "STR(-123.45, 7, 3)", "-123.450");
        validateStringResult(parser, "STR(-1234.45, 3, 3)", "-1234.450");
    }

    @Test
    public void testSTRING() throws Exception {
        validatePattern(parser, "STRING");

        validateExceptionThrown(parser, "STRING()", "STRING expected 2 parameter(s), but got 0", 1, 7);
        validateExceptionThrown(parser, "STRING('test')", "STRING expected 2 parameter(s), but got 1", 1, 7);
        validateExceptionThrown(parser, "STRING('test', 2, 2)", "STRING expected 2 parameter(s), but got 3", 1, 7);
        validateExceptionThrown(parser, "STRING(123, 2)", "STRING parameter 1 expected type STRING, but was NUMBER", 1, 7);
        validateExceptionThrown(parser, "STRING('123', '2')", "STRING parameter 2 expected type NUMBER, but was STRING", 1, 7);

        validateStringResult(parser, "STRING(null, null)", null);
        validateStringResult(parser, "STRING(null, 3)", null);
        validateStringResult(parser, "STRING('*', null)", null);
        validateStringResult(parser, "STRING('', 3)", "");
        validateStringResult(parser, "STRING('*', 3)", "***");
        validateStringResult(parser, "STRING('R', 0)", "");
        validateStringResult(parser, "STRING('R', -10)", "");
        validateStringResult(parser, "STRING('RI', 3)", "RIRIRI");
    }

    @Test
    public void testTAN() throws Exception {
        validatePattern(parser, "TAN");

        validateExceptionThrown(parser, "TAN()", "TAN expected 1 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "TAN(0.01, 2)", "TAN expected 1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "TAN('1.23')", "TAN parameter 1 expected type NUMBER, but was STRING", 1, 4);

        validateNumericResult(parser, "TAN(null)", null);
        validateNumericResult(parser, "TAN(0)", "0");
        validateNumericResult(parser, "TAN(1)", "0.01746");
        validateNumericResult(parser, "TAN(22)", "0.40403");
        validateNumericResult(parser, "TAN(45)", "1");
    }

    @Test
    public void testTRIM() throws Exception {
        validatePattern(parser, "TRIM");

        validateExceptionThrown(parser, "TRIM()", "TRIM expected 1..2 parameter(s), but got 0", 1, 5);
        validateExceptionThrown(parser, "TRIM('test', 'test', 1)", "TRIM expected 1..2 parameter(s), but got 3", 1, 5);
        validateExceptionThrown(parser, "TRIM(123)", "TRIM parameter 1 expected type STRING, but was NUMBER", 1, 5);
        validateExceptionThrown(parser, "TRIM('123', 2)", "TRIM parameter 2 expected type STRING, but was NUMBER", 1, 5);

        validateStringResult(parser, "TRIM(null)", null);
        validateStringResult(parser, "TRIM('')", "");
        validateStringResult(parser, "TRIM('  \t  Hello World  \t ')", "Hello World");
        validateStringResult(parser, "TRIM('*****Hello World*****', '*')", "Hello World");
    }

    @Test
    public void testTRIMLEFT() throws Exception {
        validatePattern(parser, "TRIMLEFT");

        validateExceptionThrown(parser, "TRIMLEFT()", "TRIMLEFT expected 1..2 parameter(s), but got 0", 1, 9);
        validateExceptionThrown(parser, "TRIMLEFT('test', 'test', 1)", "TRIMLEFT expected 1..2 parameter(s), but got 3", 1, 9);
        validateExceptionThrown(parser, "TRIMLEFT(123)", "TRIMLEFT parameter 1 expected type STRING, but was NUMBER", 1, 9);
        validateExceptionThrown(parser, "TRIMLEFT('123', 2)", "TRIMLEFT parameter 2 expected type STRING, but was NUMBER", 1, 9);

        validateStringResult(parser, "TRIMLEFT(null)", null);
        validateStringResult(parser, "TRIMLEFT('')", "");
        validateStringResult(parser, "TRIMLEFT('  \t  Hello World  \t ')", "Hello World  \t ");
        validateStringResult(parser, "TRIMLEFT('*****Hello World*****', '*')", "Hello World*****");
    }

    @Test
    public void testTRIMRIGHT() throws Exception {
        validatePattern(parser, "TRIMRIGHT");

        validateExceptionThrown(parser, "TRIMRIGHT()", "TRIMRIGHT expected 1..2 parameter(s), but got 0", 1, 10);
        validateExceptionThrown(parser, "TRIMRIGHT('test', 'test', 1)", "TRIMRIGHT expected 1..2 parameter(s), but got 3", 1, 10);
        validateExceptionThrown(parser, "TRIMRIGHT(123)", "TRIMRIGHT parameter 1 expected type STRING, but was NUMBER", 1, 10);
        validateExceptionThrown(parser, "TRIMRIGHT('123', 2)", "TRIMRIGHT parameter 2 expected type STRING, but was NUMBER", 1, 10);

        validateStringResult(parser, "TRIMRIGHT(null)", null);
        validateStringResult(parser, "TRIMRIGHT('')", "");
        validateStringResult(parser, "TRIMRIGHT('  \t  Hello World  \t ')", "  \t  Hello World");
        validateStringResult(parser, "TRIMRIGHT('*****Hello World*****', '*')", "*****Hello World");
    }

    @Test
    public void testUPPER() throws Exception {
        validatePattern(parser, "UPPER");

        validateExceptionThrown(parser, "UPPER()", "UPPER expected 1 parameter(s), but got 0", 1, 6);
        validateExceptionThrown(parser, "UPPER('test', 'test')", "UPPER expected 1 parameter(s), but got 2", 1, 6);
        validateExceptionThrown(parser, "UPPER(123)", "UPPER parameter 1 expected type STRING, but was NUMBER", 1, 6);

        validateStringResult(parser, "UPPER(null)", null);
        validateStringResult(parser, "UPPER('')", "");
        validateStringResult(parser, "UPPER('HellO, WORLD')", "HELLO, WORLD");
    }

    @Test
    public void testVAL() throws Exception {
        validatePattern(parser, "VAL");

        validateExceptionThrown(parser, "VAL()", "VAL expected 1 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "VAL('test', 'test')", "VAL expected 1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "VAL(123)", "VAL parameter 1 expected type STRING, but was NUMBER", 1, 4);
        validateExceptionThrown(parser, "VAL('123E')", "Expected STRING value that could be parsed to a NUMBER, but was 123E", 1, 5);

        validateNumericResult(parser, "VAL(null)", null);
        validateNumericResult(parser, "VAL('')", "0");
        validateNumericResult(parser, "VAL('123.45')", "123.45");
    }
}
