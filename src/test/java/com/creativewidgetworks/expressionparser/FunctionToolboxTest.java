package com.creativewidgetworks.expressionparser;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;

public class FunctionToolboxTest extends UnitTestBase {

    private Parser parser;

    @Before
    public void beforeEach() {
        parser = new Parser();
        FunctionToolbox.register(parser);
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
        validateExceptionThrown(parser, "ARRAYLEN('test')", "Expected 'test' to be an array value", 1, 10);
        validateExceptionThrown(parser, "ARRAYLEN(V1)", "Expected 'V1' to be an array value", 1, 10);

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

        validateNumericResult(parser, "AVERAGE(null)", null);
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

    /*

        @Test
        public void testCONTAINS() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "CONTAINS");

            validateExceptionThrown(parser, "CONTAINS()"), "CONTAINS expected 2 parameter(s), but got 0", 1, 8);
            validateExceptionThrown(parser, "CONTAINS('A', 'B', 2)"), "CONTAINS expected 2 parameter(s), but got 3", 1, 8);
            validateExceptionThrown(parser, "CONTAINS(1, 'B')"), "CONTAINS parameter 1 expected type STRING, but was NUMBER", 1, 8);
            validateExceptionThrown(parser, "CONTAINS('A', 2)"), "CONTAINS parameter 2 expected type STRING, but was NUMBER", 1, 8);

            validateBoolean(parser, "CONTAINS(null, null)"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINS('A', null)"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINS(null, 'B')"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINS('Ralph', 'I')"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINS('Ralph', 'Ph')"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINS('Ralph', 'ph')"), Boolean.TRUE);
            validateBoolean(parser, "CONTAINS('Ralph', Upper('ph'))"), Boolean.FALSE);
        }

        @Test
        public void testCONTAINSALL() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "CONTAINSALL");

            validateExceptionThrown(parser, "CONTAINSALL()"), "CONTAINSALL expected 2 parameter(s), but got 0", 1, 11);
            validateExceptionThrown(parser, "CONTAINSALL('A', 'B', 2)"), "CONTAINSALL expected 2 parameter(s), but got 3", 1, 11);
            validateExceptionThrown(parser, "CONTAINSALL(1, 'B')"), "CONTAINSALL parameter 1 expected type STRING, but was NUMBER", 1, 11);
            validateExceptionThrown(parser, "CONTAINSALL('A', 2)"), "CONTAINSALL parameter 2 expected type STRING, but was NUMBER", 1, 11);

            validateBoolean(parser, "CONTAINSALL(null, null)"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINSALL('A', null)"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINSALL(null, 'B')"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINSALL('Ralph', 'I')"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINSALL('Ralph', 'hR')"), Boolean.TRUE);
            validateBoolean(parser, "CONTAINSALL('Ralph', 'ph')"), Boolean.TRUE);
            validateBoolean(parser, "CONTAINSALL('Ralph', 'pH')"), Boolean.FALSE);
        }

        @Test
        public void testCONTAINSANY() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "CONTAINSANY");

            validateExceptionThrown(parser, "CONTAINSANY()"), "CONTAINSANY expected 2 parameter(s), but got 0", 1, 11);
            validateExceptionThrown(parser, "CONTAINSANY('A', 'B', 2)"), "CONTAINSANY expected 2 parameter(s), but got 3", 1, 11);
            validateExceptionThrown(parser, "CONTAINSANY(1, 'B')"), "CONTAINSANY parameter 1 expected type STRING, but was NUMBER", 1, 11);
            validateExceptionThrown(parser, "CONTAINSANY('A', 2)"), "CONTAINSANY parameter 2 expected type STRING, but was NUMBER", 1, 11);

            validateBoolean(parser, "CONTAINSANY(null, null)"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINSANY('A', null)"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINSANY(null, 'B')"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINSANY('Ralph', 'I')"), Boolean.FALSE);
            validateBoolean(parser, "CONTAINSANY('Ralph', 'XhR')"), Boolean.TRUE);
            validateBoolean(parser, "CONTAINSANY('Ralph', 'Xph')"), Boolean.TRUE);
            validateBoolean(parser, "CONTAINSANY('Ralph', 'XpH')"), Boolean.TRUE);
        }

        @Test
        public void testCOS() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "COS");

            validateExceptionThrown(parser, "COS()"), "COS expected 1 parameter(s), but got 0", 1, 3);
            validateExceptionThrown(parser, "COS(0.01, 2)"), "COS expected 1 parameter(s), but got 2", 1, 3);
            validateExceptionThrown(parser, "COS('1.23')"), "COS parameter 1 expected type NUMBER, but was STRING", 1, 3);

            validateNumericResult(parser, "COS(null)"), null);
            validateNumericResult(parser, "COS(0)"), "1");
            validateNumericResult(parser, "COS(1)"), "0.99985");
            validateNumericResult(parser, "COS(22)"), "0.92718");
            validateNumericResult(parser, "COS(45)"), "0.70711");
        }

        @Test
        public void testENDSWITH() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "ENDSWITH");

            validateExceptionThrown(parser, "ENDSWITH()"), "ENDSWITH expected 2 parameter(s), but got 0", 1, 8);
            validateExceptionThrown(parser, "ENDSWITH('A', 'B', 2)"), "ENDSWITH expected 2 parameter(s), but got 3", 1, 8);
            validateExceptionThrown(parser, "ENDSWITH(1, 'B')"), "ENDSWITH parameter 1 expected type STRING, but was NUMBER", 1, 8);
            validateExceptionThrown(parser, "ENDSWITH('A', 2)"), "ENDSWITH parameter 2 expected type STRING, but was NUMBER", 1, 8);

            validateBoolean(parser, "ENDSWITH(null, null)"), Boolean.FALSE);
            validateBoolean(parser, "ENDSWITH('A', null)"), Boolean.FALSE);
            validateBoolean(parser, "ENDSWITH(null, 'B')"), Boolean.FALSE);
            validateBoolean(parser, "ENDSWITH('Ralph', 'I')"), Boolean.FALSE);
            validateBoolean(parser, "ENDSWITH('Ralph', 'Ph')"), Boolean.FALSE);
            validateBoolean(parser, "ENDSWITH('Ralph', 'ph')"), Boolean.TRUE);
            validateBoolean(parser, "ENDSWITH('Ralph', Upper('ph'))"), Boolean.FALSE);
        }

        @Test
        public void testEXP() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "EXP");

            validateExceptionThrown(parser, "EXP()"), "EXP expected 1 parameter(s), but got 0", 1, 3);
            validateExceptionThrown(parser, "EXP(0.01, 2)"), "EXP expected 1 parameter(s), but got 2", 1, 3);
            validateExceptionThrown(parser, "EXP('1.23')"), "EXP parameter 1 expected type NUMBER, but was STRING", 1, 3);

            validateNumericResult(parser, "EXP(null)"), null);
            validateNumericResult(parser, "EXP(0)"), "1.0");
            validateNumericResult(parser, "EXP(1)"), "2.7182818284590455");
            validateNumericResult(parser, "EXP(2)"), "7.38905609893065");
        }

        @Test
        public void testFACTORIAL() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "FACTORIAL");

            validateExceptionThrown(parser, "FACTORIAL()"), "FACTORIAL expected 1 parameter(s), but got 0", 1, 9);
            validateExceptionThrown(parser, "FACTORIAL(1,2)"), "FACTORIAL expected 1 parameter(s), but got 2", 1, 9);
            validateExceptionThrown(parser, "FACTORIAL('A')"), "FACTORIAL parameter 1 expected type NUMBER, but was STRING", 1, 9);
            validateExceptionThrown(parser, "FACTORIAL(-1)')"), "Value cannot be less than zero, but was -1", 1, 9);

            validateNumericResult(parser, "FACTORIAL(null)"), null);
            validateNumericResult(parser, "FACTORIAL(0)"), "1");
            validateNumericResult(parser, "FACTORIAL(1)"), "1");
            validateNumericResult(parser, "FACTORIAL(2)"), "2");
            validateNumericResult(parser, "FACTORIAL(5)"), "120");
            validateNumericResult(parser, "FACTORIAL(6)"), "720");
            validateNumericResult(parser, "FACTORIAL(30)"), "265252859812191058636308480000000");
        }

        @Test
        public void testFIND() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "FIND");

            validateExceptionThrown(parser, "FIND()"), "FIND expected 2..3 parameter(s), but got 0", 1, 4);
            validateExceptionThrown(parser, "FIND('A','B',2,2)"), "FIND expected 2..3 parameter(s), but got 4", 1, 4);
            validateExceptionThrown(parser, "FIND(1,'B')"), "FIND parameter 1 expected type STRING, but was NUMBER", 1, 4);
            validateExceptionThrown(parser, "FIND('A',2)"), "FIND parameter 2 expected type STRING, but was NUMBER", 1, 4);
            validateExceptionThrown(parser, "FIND('A','B','C')"), "FIND parameter 3 expected type NUMBER, but was STRING", 1, 4);

            validateNumericResult(parser, "FIND(null,'abc')"), "0");
            validateNumericResult(parser, "FIND('AbCdEfG', null)"), "0");
            validateNumericResult(parser, "FIND('', '')"), "0");
            validateNumericResult(parser, "FIND('AbCdEfG', '')"), "0");
            validateNumericResult(parser, "FIND('AbCdEfG', 'cd')"), "0");
            validateNumericResult(parser, "FIND('AbCdEfG', 'CD')"), "0");
            validateNumericResult(parser, "FIND('AbCdEfG', 'Cd')"), "3");
            validateNumericResult(parser, "FIND('AbCdEfGCd', 'Cd', 5)"), "8");
            validateNumericResult(parser, "FIND('AbCdEfGCd', 'Cd', -15)"), "3");
            validateNumericResult(parser, "FIND('AbCdEfGCd', 'Cd', 15)"), "0");
        }

        @Test
        public void testFLOOR() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "FLOOR");

            validateExceptionThrown(parser, "FLOOR()"), "FLOOR expected 1 parameter(s), but got 0", 1, 5);
            validateExceptionThrown(parser, "FLOOR(0.01, 2)"), "FLOOR expected 1 parameter(s), but got 2", 1, 5);
            validateExceptionThrown(parser, "FLOOR('A')"), "FLOOR parameter 1 expected type NUMBER, but was STRING", 1, 5);

            validateNumericResult(parser, "FLOOR(null)"), null);
            validateNumericResult(parser, "FLOOR(0.01)"), "0.0");
            validateNumericResult(parser, "FLOOR(2.022)"), "2.0");
        }

        @Test
        public void testHEX() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "HEX");

            validateExceptionThrown(parser, "HEX()"), "HEX expected 1 parameter(s), but got 0", 1, 3);
            validateExceptionThrown(parser, "HEX(123, 'test')"), "HEX expected 1 parameter(s), but got 2", 1, 3);
            validateExceptionThrown(parser, "HEX('123')"), "HEX parameter 1 expected type NUMBER, but was STRING", 1, 3);

            validateString(parser, "HEX(null)"), null);
            validateString(parser, "HEX(0)"), "00");
            validateString(parser, "HEX(10)"), "0A");
            validateString(parser, "HEX(-10)"), "F6");
            validateString(parser, "HEX(-1000)"), "FC18");
            validateString(parser, "HEX(-125000)"), "FFFE17B8");
        }

        @Test
        public void testISBLANK() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "ISBLANK");

            validateExceptionThrown(parser, "ISBLANK()"), "ISBLANK expected 1 parameter(s), but got 0", 1, 7);
            validateExceptionThrown(parser, "ISBLANK(123, 'test')"), "ISBLANK expected 1 parameter(s), but got 2", 1, 7);

            validateBoolean(parser, "ISBLANK(null)"), Boolean.TRUE);
            validateBoolean(parser, "ISBLANK(NULL)"), Boolean.TRUE);
            validateBoolean(parser, "ISBLANK('')"), Boolean.TRUE);
            validateBoolean(parser, "ISBLANK('  ')"), Boolean.TRUE);
            validateBoolean(parser, "ISBLANK('  \t  ')"), Boolean.TRUE);
            validateBoolean(parser, "ISBLANK(' test ')"), Boolean.FALSE);
        }

        @Test
        public void testISBOOLEAN() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "ISBOOLEAN");

            validateExceptionThrown(parser, "ISBOOLEAN()"), "ISBOOLEAN expected 1 parameter(s), but got 0", 1, 9);
            validateExceptionThrown(parser, "ISBOOLEAN(123, 'test')"), "ISBOOLEAN expected 1 parameter(s), but got 2", 1, 9);

            validateBoolean(parser, "ISBOOLEAN(NOW())"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN(null)"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN('')"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN('Noway')"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN(1.000000000000001)"),Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN(0)"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN('0')"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN('0.0')"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN('off')"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN('OFF')"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN('false')"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN('False')"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN(1==0)"), Boolean.FALSE);
            validateBoolean(parser, "ISBOOLEAN(1)"), Boolean.TRUE);
            validateBoolean(parser, "ISBOOLEAN(2-1)"), Boolean.TRUE);
            validateBoolean(parser, "ISBOOLEAN('1.000')"), Boolean.TRUE);
            validateBoolean(parser, "ISBOOLEAN('on')"), Boolean.TRUE);
            validateBoolean(parser, "ISBOOLEAN('ON')"), Boolean.TRUE);
            validateBoolean(parser, "ISBOOLEAN('yes')"), Boolean.TRUE);
            validateBoolean(parser, "ISBOOLEAN('TRUE')"), Boolean.TRUE);
            validateBoolean(parser, "ISBOOLEAN('trUe')"), Boolean.TRUE);
            validateBoolean(parser, "ISBOOLEAN(1==1)"), Boolean.TRUE);
        }

        @Test
        public void testISNULL() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "ISNULL");

            validateExceptionThrown(parser, "ISNULL()"), "ISNULL expected 1 parameter(s), but got 0", 1, 6);
            validateExceptionThrown(parser, "ISNULL(123, 'test')"), "ISNULL expected 1 parameter(s), but got 2", 1, 6);

            validateBoolean(parser, "ISNULL(null)"), Boolean.TRUE);
            validateBoolean(parser, "ISNULL(NULL)"), Boolean.TRUE);
            validateBoolean(parser, "ISNULL('')"), Boolean.FALSE);
            validateBoolean(parser, "ISNULL('  ')"), Boolean.FALSE);
        }

        @Test
        public void testISNUMBER() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "ISNUMBER");

            validateExceptionThrown(parser, "ISNUMBER()"), "ISNUMBER expected 1 parameter(s), but got 0", 1, 8);
            validateExceptionThrown(parser, "ISNUMBER(123, 'test')"), "ISNUMBER expected 1 parameter(s), but got 2", 1, 8);

            // This will create a NUMBER token, which will be examined by its string value later
            parser, "N=123.45/1.0");

            validateBoolean(parser, "ISNUMBER(null)"), Boolean.FALSE);
            validateBoolean(parser, "ISNUMBER(NULL)"), Boolean.FALSE);
            validateBoolean(parser, "ISNUMBER('')"), Boolean.FALSE);
            validateBoolean(parser, "ISNUMBER('  ')"), Boolean.FALSE);
            validateBoolean(parser, "ISNUMBER('123')"), Boolean.TRUE);
            validateBoolean(parser, "ISNUMBER('123.45')"), Boolean.TRUE);
            validateBoolean(parser, "ISNUMBER('-123')"), Boolean.TRUE);
            validateBoolean(parser, "ISNUMBER('-123.45')"), Boolean.TRUE);
            validateBoolean(parser, "ISNUMBER('1.4E12')"), Boolean.TRUE);
            validateBoolean(parser, "ISNUMBER(PI)"), Boolean.TRUE);
            validateBoolean(parser, "ISNUMBER(N)"), Boolean.TRUE);
        }

        @Test
        public void testLEFT() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "LEFT");

            validateExceptionThrown(parser, "LEFT()"), "LEFT expected 2 parameter(s), but got 0", 1, 4);
            validateExceptionThrown(parser, "LEFT('test')"), "LEFT expected 2 parameter(s), but got 1", 1, 4);
            validateExceptionThrown(parser, "LEFT('test', 2, 2)"), "LEFT expected 2 parameter(s), but got 3", 1, 4);
            validateExceptionThrown(parser, "LEFT(123, 2)"), "LEFT parameter 1 expected type STRING, but was NUMBER", 1, 4);

            validateString(parser, "LEFT(null, 2)"), null);
            validateString(parser, "LEFT('', 3)"), "");
            validateString(parser, "LEFT('12345', null)"), "12345");
            validateString(parser, "LEFT('12345', 0)"), "");
            validateString(parser, "LEFT('12345', -10)"), "");
            validateString(parser, "LEFT('12345', 3)"), "123");
            validateString(parser, "LEFT('12', 5)"), "12");
            validateString(parser, "LEFT(RIGHT('12', 2), 5)"), "12");
        }

        @Test
        public void testLEN() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "LEN");

            validateExceptionThrown(parser, "LEN()"), "LEN expected 1 parameter(s), but got 0", 1, 3);
            validateExceptionThrown(parser, "LEN('test', 'test')"), "LEN expected 1 parameter(s), but got 2", 1, 3);
            validateExceptionThrown(parser, "LEN(123)"), "LEN parameter 1 expected type STRING, but was NUMBER", 1, 3);

            validateNumericResult(parser, "LEN('')"), "0");
            validateNumericResult(parser, "LEN(null)"), "0");
            validateNumericResult(parser, "LEN('Hello')"), "5");
        }

        @Test
        public void testLOG() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "LOG");

            validateExceptionThrown(parser, "LOG()"), "LOG expected 1 parameter(s), but got 0", 1, 3);
            validateExceptionThrown(parser, "LOG(0.01, 2)"), "LOG expected 1 parameter(s), but got 2", 1, 3);
            validateExceptionThrown(parser, "LOG('1.23')"), "LOG parameter 1 expected type NUMBER, but was STRING", 1, 3);

            validateNumericResult(parser, "LOG(null)"), null);
            validateNumericResult(parser, "LOG(1)"), "0.0");
            validateNumericResult(parser, "LOG(2)"), "0.6931471805599453");
        }

        @Test
        public void testLOG10() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "LOG10");

            validateExceptionThrown(parser, "LOG10()"), "LOG10 expected 1 parameter(s), but got 0", 1, 5);
            validateExceptionThrown(parser, "LOG10(0.01, 2)"), "LOG10 expected 1 parameter(s), but got 2", 1, 5);
            validateExceptionThrown(parser, "LOG10('1.23')"), "LOG10 parameter 1 expected type NUMBER, but was STRING", 1, 5);

            validateNumericResult(parser, "LOG10(null)"), null);
            validateNumericResult(parser, "LOG10(1)"), "0.0");
            validateNumericResult(parser, "LOG10(2)"), "0.3010299956639812");
        }

        @Test
        public void testLOWER() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "LOWER");

            validateExceptionThrown(parser, "LOWER()"), "LOWER expected 1 parameter(s), but got 0", 1, 5);
            validateExceptionThrown(parser, "LOWER('test', 'test')"), "LOWER expected 1 parameter(s), but got 2", 1, 5);
            validateExceptionThrown(parser, "LOWER(123)"), "LOWER parameter 1 expected type STRING, but was NUMBER", 1, 5);

            validateString(parser, "LOWER(null)"), null);
            validateString(parser, "LOWER('')"), "");
            validateString(parser, "LOWER('HellO, WORLD')"), "hello, world");
        }

        @Test
        public void testMAKEBOOLEAN() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "MAKEBOOLEAN");

            validateExceptionThrown(parser, "MAKEBOOLEAN()"), "MAKEBOOLEAN expected 1 parameter(s), but got 0", 1, 11);
            validateExceptionThrown(parser, "MAKEBOOLEAN('test', 'test')"), "MAKEBOOLEAN expected 1 parameter(s), but got 2", 1, 11);

            validateBoolean(parser, "MAKEBOOLEAN(NOW())"), null);
            validateBoolean(parser, "MAKEBOOLEAN(null)"), null);
            validateBoolean(parser, "MAKEBOOLEAN('')"), null);
            validateBoolean(parser, "MAKEBOOLEAN('Noway')"), null);
            validateBoolean(parser, "MAKEBOOLEAN(1.000000000000001)"), null);
            validateBoolean(parser, "MAKEBOOLEAN(0)"), Boolean.FALSE);
            validateBoolean(parser, "MAKEBOOLEAN('0')"), Boolean.FALSE);
            validateBoolean(parser, "MAKEBOOLEAN('0.0')"), Boolean.FALSE);
            validateBoolean(parser, "MAKEBOOLEAN('off')"), Boolean.FALSE);
            validateBoolean(parser, "MAKEBOOLEAN('OFF')"), Boolean.FALSE);
            validateBoolean(parser, "MAKEBOOLEAN('false')"), Boolean.FALSE);
            validateBoolean(parser, "MAKEBOOLEAN('False')"), Boolean.FALSE);
            validateBoolean(parser, "MAKEBOOLEAN(1==0)"), Boolean.FALSE);
            validateBoolean(parser, "MAKEBOOLEAN(1)"), Boolean.TRUE);
            validateBoolean(parser, "MAKEBOOLEAN(2-1)"), Boolean.TRUE);
            validateBoolean(parser, "MAKEBOOLEAN('1.000')"), Boolean.TRUE);
            validateBoolean(parser, "MAKEBOOLEAN('on')"), Boolean.TRUE);
            validateBoolean(parser, "MAKEBOOLEAN('ON')"), Boolean.TRUE);
            validateBoolean(parser, "MAKEBOOLEAN('yes')"), Boolean.TRUE);
            validateBoolean(parser, "MAKEBOOLEAN('TRUE')"), Boolean.TRUE);
            validateBoolean(parser, "MAKEBOOLEAN('trUe')"), Boolean.TRUE);
            validateBoolean(parser, "MAKEBOOLEAN(1==1)"), Boolean.TRUE);
        }

        @Test
        public void testMATCH() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "MATCH");

            validateExceptionThrown(parser, "MATCH()"), "MATCH expected 2 parameter(s), but got 0", 1, 5);
            validateExceptionThrown(parser, "MATCH('A','B', 1)"), "MATCH expected 2 parameter(s), but got 3", 1, 5);
            validateExceptionThrown(parser, "MATCH(1, '1.23')"), "MATCH parameter 1 expected type STRING, but was NUMBER", 1, 5);
            validateExceptionThrown(parser, "MATCH('1.23', 1)"), "MATCH parameter 2 expected type STRING, but was NUMBER", 1, 5);
            validateExceptionThrown(parser, "MATCH('1.23', '[')"), "Invalid regex pattern: [", 1, 5);

            validateArray(parser, "MATCH(null, null)"), null);
            validateArray(parser, "MATCH('a', null)"), null);
            validateArray(parser, "MATCH(null, 'b')"), null);
            validateArray(parser, "MATCH(null, 'b')"), null);
            validateArray(parser, "MATCH('Phone:', '[\\(](\\d{3})\\D*(\\d{3})\\D*(\\d{4})\\D*(\\d*)')"), ""); // no match
            validateArray(parser, "MATCH('Phone: (815) 555-1212 x100 (Work)', '[\\(](\\d{3})\\D*(\\d{3})\\D*(\\d{4})\\D*(\\d*)')"),
                    "(815) 555-1212 x100",  // asString
                    "(815) 555-1212 x100", "815", "555", "1212", "100");  // group 0..n
        }

        @Test
        public void testMATCHBYLEN() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "MATCHBYLEN");

            validateExceptionThrown(parser, "MATCHBYLEN()"), "MATCHBYLEN expected 3 parameter(s), but got 0", 1, 10);
            validateExceptionThrown(parser, "MATCHBYLEN('A','B', 'C', 'D')"), "MATCHBYLEN expected 3 parameter(s), but got 4", 1, 10);
            validateExceptionThrown(parser, "MATCHBYLEN(1, 'B', 'C')"), "MATCHBYLEN parameter 1 expected type STRING, but was NUMBER", 1, 10);
            validateExceptionThrown(parser, "MATCHBYLEN('A', 1, 'C')"), "MATCHBYLEN parameter 2 expected type STRING, but was NUMBER", 1, 10);
            validateExceptionThrown(parser, "MATCHBYLEN('A', 'B', 1)"), "MATCHBYLEN parameter 3 expected type STRING, but was NUMBER", 1, 10);
            validateExceptionThrown(parser, "MATCHBYLEN('A', '[', 'C')"), "Invalid regex pattern: [", 1, 10);

            validateString(parser, "MATCHBYLEN(null, null, null)"), null);
            validateString(parser, "MATCHBYLEN('a', null, null)"), null);
            validateString(parser, "MATCHBYLEN(null, 'b', null)"), null);
            validateString(parser, "MATCHBYLEN(null, null, 'c')"), null);

            // No match -> "no value"
            String variations = "0='no value':7=      ###-####:10=(###) ###-####:?='Ralph' + ' ' + 'Iden'";
            validateString(parser, "MATCHBYLEN('', '[0-9]*', \"" + variations + "\")"), "no value");

            // No match - > empty
            variations = "0=:7=      ###-####:10=(###) ###-####:?='Ralph' + ' ' + 'Iden'";
            validateString(parser, "MATCHBYLEN('', '[0-9]*', \"" + variations + "\")"), "");

            // Match pattern of length 7
            validateString(parser, "MATCHBYLEN('5551212', '[0-9]*', \"" + variations + "\")"), "      555-1212");

            // Match pattern of length 10
            validateString(parser, "MATCHBYLEN('8155551212', '[0-9]*', \"" + variations + "\")"), "(815) 555-1212");

            // Matched, but not one of the specified lengths uses default variation
            validateString(parser, "MATCHBYLEN('81555512121234', '[0-9]*', \"" + variations + "\")"), "Ralph Iden");

            // Matched, but not one of the specified lengths and not default returns empty string
            variations = "0=:7=      ###-####:10=(###) ###-####";
            validateString(parser, "MATCHBYLEN('81555512121234', '[0-9]*', \"" + variations + "\")"), "");

            // Poorly formed variation patterns return empty strings -----------------------------------------------
            // Match where variation is empty and at the end of the variations
            variations = "7=";
            validateString(parser, "MATCHBYLEN('5551212', '[0-9]*', \"" + variations + "\")"), "");

            // Match with a trailing colon in variation string
            variations = "0=:7=      ###-####:10=(###) ###-####:";
            validateString(parser, "MATCHBYLEN('81555512121234', '[0-9]*', \"" + variations + "\")"), "");

            // No matches with a trailing colon in variation string
            variations = "0=:7=      ###-####:10=(###) ###-####:";
            validateString(parser, "MATCHBYLEN('A', 'B', \"" + variations + "\")"), "");
        }

        @Test
        public void testMAX() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "MAX");

            validateExceptionThrown(parser, "MAX()"), "MAX expected 2 parameter(s), but got 0", 1, 3);
            validateExceptionThrown(parser, "MAX(0.01, 2, 1)"), "MAX expected 2 parameter(s), but got 3", 1, 3);
            validateExceptionThrown(parser, "MAX('1.23', 1)"), "MAX parameter 1 expected type NUMBER, but was STRING", 1, 3);
            validateExceptionThrown(parser, "MAX(1, '1.23')"), "MAX parameter 2 expected type NUMBER, but was STRING", 1, 3);

            validateNumericResult(parser, "MAX(null, null)"), null);
            validateNumericResult(parser, "MAX(1, null)"), null);
            validateNumericResult(parser, "MAX(null, 2)"), null);
            validateNumericResult(parser, "MAX(-1, 4)"), "4");
            validateNumericResult(parser, "MAX(4, -1)"), "4");
            validateNumericResult(parser, "MAX(-4, -1)"), "-1");
            validateNumericResult(parser, "MAX(4, 4)"), "4");
            validateNumericResult(parser, "MAX(-1, -1)"), "-1");
        }

        @Test
        public void testMID() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "MID");

            validateExceptionThrown(parser, "MID()"), "MID expected 2..3 parameter(s), but got 0", 1, 3);
            validateExceptionThrown(parser, "MID('test')"), "MID expected 2..3 parameter(s), but got 1", 1, 3);
            validateExceptionThrown(parser, "MID('test', 1, 2, 3)"), "MID expected 2..3 parameter(s), but got 4", 1, 3);
            validateExceptionThrown(parser, "MID(123, 1)"), "MID parameter 1 expected type STRING, but was NUMBER", 1, 3);
            validateExceptionThrown(parser, "MID('123', '1')"), "MID parameter 2 expected type NUMBER, but was STRING", 1, 3);
            validateExceptionThrown(parser, "MID('123', 1, '2')"), "MID parameter 3 expected type NUMBER, but was STRING", 1, 3);

            validateString(parser, "MID(null, null)"), null);
            validateString(parser, "MID(null, null, null)"), null);
            validateString(parser, "MID('test', null)"), null);
            validateString(parser, "MID(null, 2)"), null);
            validateString(parser, "MID('test', 2, null)"), null);
            validateString(parser, "MID('', 3)"), "");
            validateString(parser, "MID('12345', 0)"), "");
            validateString(parser, "MID('12345', -10)"), "");
            validateString(parser, "MID('12', 5)"), "");
            validateString(parser, "MID('12345', 3)"), "345");
            validateString(parser, "MID('12345', 3, 1)"), "3");
            validateString(parser, "MID('12345', 3, 2)"), "34");
            validateString(parser, "MID('12345', 3, 3)"), "345");
            validateString(parser, "MID('12345', 3, 4)"), "345");
        }

        @Test
        public void testMIN() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "MIN");

            validateExceptionThrown(parser, "MIN()"), "MIN expected 2 parameter(s), but got 0", 1, 3);
            validateExceptionThrown(parser, "MIN(0.01, 2, 1)"), "MIN expected 2 parameter(s), but got 3", 1, 3);
            validateExceptionThrown(parser, "MIN('1.23', 1)"), "MIN parameter 1 expected type NUMBER, but was STRING", 1, 3);
            validateExceptionThrown(parser, "MIN(1, '1.23')"), "MIN parameter 2 expected type NUMBER, but was STRING", 1, 3);

            validateNumericResult(parser, "MIN(null, null)"), null);
            validateNumericResult(parser, "MIN(1, null)"), null);
            validateNumericResult(parser, "MIN(null, 2)"), null);
            validateNumericResult(parser, "MIN(-1, 4)"), "-1");
            validateNumericResult(parser, "MIN(4, -1)"), "-1");
            validateNumericResult(parser, "MIN(-4, -1)"), "-4");
            validateNumericResult(parser, "MIN(4, 4)"), "4");
            validateNumericResult(parser, "MIN(-1, -1)"), "-1");
        }

        @Test
        public void testNAMECASE() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "NAMECASE");

            validateExceptionThrown(parser, "NAMECASE()"), "NAMECASE expected 1 parameter(s), but got 0", 1, 8);
            validateExceptionThrown(parser, "NAMECASE('test', 'test')"), "NAMECASE expected 1 parameter(s), but got 2", 1, 8);
            validateExceptionThrown(parser, "NAMECASE(123)"), "NAMECASE parameter 1 expected type STRING, but was NUMBER", 1, 8);

            validateString(parser, "NAMECASE(null)"), null);
            validateString(parser, "NAMECASE('')"), "");
            validateString(parser, "NAMECASE('ralph iden')"), "Ralph Iden");
            validateString(parser, "NAMECASE('RALPH IDEN')"), "Ralph Iden");
            validateString(parser, "NAMECASE('Ralph Iden')"), "Ralph Iden");
            validateString(parser, "NAMECASE('  RALPH\t\tW. IDEN  ')"), "  Ralph\t\tW. Iden  ");
        }

        @Test
        public void testRANDOM() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "RANDOM");

            validateExceptionThrown(parser, "RANDOM(1,2,3)"), "RANDOM expected 0..2 parameter(s), but got 3", 1, 6);
            validateExceptionThrown(parser, "RANDOM('123', 2)"), "RANDOM parameter 1 expected type NUMBER, but was STRING", 1, 6);
            validateExceptionThrown(parser, "RANDOM(1, '123')"), "RANDOM parameter 2 expected type NUMBER, but was STRING", 1, 6);

            assertTrue(parser, "RANDOM(0)").asString().startsWith("0"));

            Value result;
            BigDecimal bdMax = new BigDecimal("100");
            for (int i = 0; i < 1000; i++) {
                result = parser, "RANDOM(100)");
                if (result.asNumber().compareTo(BigDecimal.ZERO) < 0 ||
                        result.asNumber().compareTo(bdMax) > 0) {
                    fail("Random number outside of range (0..100)");
                }
            }

            BigDecimal bdMin1 = new BigDecimal("50");
            BigDecimal bdMax1 = new BigDecimal("99");
            for (int i = 0; i < 1000; i++) {
                result = parser, "RANDOM(50, 99)");
                if (result.asNumber().compareTo(bdMin1) < 0 ||
                        result.asNumber().compareTo(bdMax1) > 0) {
                    fail("Random number outside of range (50..99)");
                }
            }
        }

        @Test
        public void testREPLACE() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "REPLACE");

            validateExceptionThrown(parser, "REPLACE()"), "REPLACE expected 3 parameter(s), but got 0", 1, 7);
            validateExceptionThrown(parser, "REPLACE('test', 'me')"), "REPLACE expected 3 parameter(s), but got 2", 1, 7);
            validateExceptionThrown(parser, "REPLACE(123, '2', '3')"), "REPLACE parameter 1 expected type STRING, but was NUMBER", 1, 7);
            validateExceptionThrown(parser, "REPLACE('1', 2, '3')"), "REPLACE parameter 2 expected type STRING, but was NUMBER", 1, 7);
            validateExceptionThrown(parser, "REPLACE('1', '2', 3)"), "REPLACE parameter 3 expected type STRING, but was NUMBER", 1, 7);

            validateString(parser, "REPLACE(null, null, null)"), null);
            validateString(parser, "REPLACE('Ralph', null, null)"), "Ralph");
            validateString(parser, "REPLACE('Mr. Ralph', null, 'Jeremy')"), "Mr. Ralph");
            validateString(parser, "REPLACE('Mr. Ralph', 'Ralph', null)"), "Mr. Ralph");
            validateString(parser, "REPLACE('Mr. Ralph', 'Ralph', 'Jeremy')"), "Mr. Jeremy");
        }

        @Test
        public void testREPLACEALL() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "REPLACEALL");

            validateExceptionThrown(parser, "REPLACEALL()"), "REPLACEALL expected 3 parameter(s), but got 0", 1, 10);
            validateExceptionThrown(parser, "REPLACEALL('test', 'me')"), "REPLACEALL expected 3 parameter(s), but got 2", 1, 10);
            validateExceptionThrown(parser, "REPLACEALL(123, '2', '3')"), "REPLACEALL parameter 1 expected type STRING, but was NUMBER", 1, 10);
            validateExceptionThrown(parser, "REPLACEALL('1', 2, '3')"), "REPLACEALL parameter 2 expected type STRING, but was NUMBER", 1, 10);
            validateExceptionThrown(parser, "REPLACEALL('1', '2', 3)"), "REPLACEALL parameter 3 expected type STRING, but was NUMBER", 1, 10);

            validateString(parser, "REPLACEALL(null, null, null)"), null);
            validateString(parser, "REPLACEALL('Ralph', null, null)"), "Ralph");
            validateString(parser, "REPLACEALL('Ralph', 'al', null)"), "Ralph");
            validateString(parser, "REPLACEALL('Ralph', null, 'A')"), "Ralph");
            validateString(parser, "REPLACEALL('boo:and:foo', ':', '-')"), "boo-and-foo");
            validateString(parser, "REPLACEALL('abbbcabc 000 abc', 'abc', '123')"), "abbbc123 000 123");
            validateString(parser, "REPLACEALL('acabc 000 abc', 'ab*c', '123')"), "123123 000 123");
            validateString(parser, "REPLACEALL('acabc 000 abc', 'ab+c', '123')"), "ac123 000 123");
            validateString(parser, "REPLACEALL('abbbcabc 000 abc', 'ab+c', '123')"), "123123 000 123");
        }

        @Test
        public void testREPLACEFIRST() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "REPLACEFIRST");

            validateExceptionThrown(parser, "REPLACEFIRST()"), "REPLACEFIRST expected 3 parameter(s), but got 0", 1, 12);
            validateExceptionThrown(parser, "REPLACEFIRST('test', 'me')"), "REPLACEFIRST expected 3 parameter(s), but got 2", 1, 12);
            validateExceptionThrown(parser, "REPLACEFIRST(123, '2', '3')"), "REPLACEFIRST parameter 1 expected type STRING, but was NUMBER", 1, 12);
            validateExceptionThrown(parser, "REPLACEFIRST('1', 2, '3')"), "REPLACEFIRST parameter 2 expected type STRING, but was NUMBER", 1, 12);
            validateExceptionThrown(parser, "REPLACEFIRST('1', '2', 3)"), "REPLACEFIRST parameter 3 expected type STRING, but was NUMBER", 1, 12);

            validateString(parser, "REPLACEFIRST(null, null, null)"), null);
            validateString(parser, "REPLACEFIRST('Ralph', null, null)"), "Ralph");
            validateString(parser, "REPLACEFIRST('Ralph', 'al', null)"), "Ralph");
            validateString(parser, "REPLACEFIRST('Ralph', null, 'A')"), "Ralph");
            validateString(parser, "REPLACEFIRST('boo:and:foo', ':', '-')"), "boo-and:foo");
            validateString(parser, "REPLACEFIRST('abbbcabc 000 abc', 'abc', '123')"), "abbbc123 000 abc");
            validateString(parser, "REPLACEFIRST('acabc 000 abc', 'ab*c', '123')"), "123abc 000 abc");
            validateString(parser, "REPLACEFIRST('acabc 000 abc', 'ab+c', '123')"), "ac123 000 abc");
            validateString(parser, "REPLACEFIRST('abbbcabc 000 abc', 'ab+c', '123')"), "123abc 000 abc");
        }


        @Test
        public void testRIGHT() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "RIGHT");

            validateExceptionThrown(parser, "RIGHT()"), "RIGHT expected 2 parameter(s), but got 0", 1, 5);
            validateExceptionThrown(parser, "RIGHT('test')"), "RIGHT expected 2 parameter(s), but got 1", 1, 5);
            validateExceptionThrown(parser, "RIGHT('test', 2, 2)"), "RIGHT expected 2 parameter(s), but got 3", 1, 5);
            validateExceptionThrown(parser, "RIGHT(123, 2)"), "RIGHT parameter 1 expected type STRING, but was NUMBER", 1, 5);

            validateString(parser, "RIGHT(null, 2)"), null);
            validateString(parser, "RIGHT('', 3)"), "");
            validateString(parser, "RIGHT('12345', null)"), "12345");
            validateString(parser, "RIGHT('12345', 0)"), "");
            validateString(parser, "RIGHT('12345', -10)"), "");
            validateString(parser, "RIGHT('12345', 3)"), "345");
            validateString(parser, "RIGHT('12', 5)"), "12");
            validateString(parser, "RIGHT(LEFT('12345', 3), 2)"), "23");
        }

        @Test
        public void testSIN() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "SIN");

            validateExceptionThrown(parser, "SIN()"), "SIN expected 1 parameter(s), but got 0", 1, 3);
            validateExceptionThrown(parser, "SIN(0.01, 2)"), "SIN expected 1 parameter(s), but got 2", 1, 3);
            validateExceptionThrown(parser, "SIN('1.23')"), "SIN parameter 1 expected type NUMBER, but was STRING", 1, 3);

            validateNumericResult(parser, "SIN(null)"), null);
            validateNumericResult(parser, "SIN(0)"), "0");
            validateNumericResult(parser, "SIN(1)"), "0.01745");
            validateNumericResult(parser, "SIN(22)"), "0.37461");
            validateNumericResult(parser, "SIN(45)"), "0.70711");
        }

        @Test
        public void testSPLIT() throws Exception {
            Parser parser = new Parser(new GrammarExtendedCalc());

            validatePattern(parser, "SPLIT");

            validateExceptionThrown(parser, "SPLIT()"), "SPLIT expected 1..3 parameter(s), but got 0", 1, 5);
            validateExceptionThrown(parser, "SPLIT('a,b,c', ',', 2, 3)"), "SPLIT expected 1..3 parameter(s), but got 4", 1, 5);
            validateExceptionThrown(parser, "SPLIT(1.23)"), "SPLIT parameter 1 expected type STRING, but was NUMBER", 1, 5);
            validateExceptionThrown(parser, "SPLIT('a,b,c', 2)"), "SPLIT parameter 2 expected type STRING, but was NUMBER", 1, 5);
            validateExceptionThrown(parser, "SPLIT('a,b,c', ',', 'a')"), "SPLIT parameter 3 expected type NUMBER, but was STRING", 1, 5);

            validateArray(parser, "SPLIT(null)"), null);
            validateArray(parser, "SPLIT(null, null)"), null);
            validateArray(parser, "SPLIT('')"), "", "");
            validateArray(parser, "SPLIT('Ralph')"), "Ralph", "Ralph");
            validateArray(parser, "SPLIT('Ralph', null)"), "Ralph", "Ralph");
            validateArray(parser, "SPLIT('Ralph', null, null)"), "Ralph", "Ralph");

            validateArray(parser, "SPLIT('Ralph,Iden')"), "Ralph", "Ralph", "Iden");
            validateArray(parser, "SPLIT('boo:and:foo', ':', 2)"), "boo", "boo", "and:foo");
            validateArray(parser, "SPLIT('boo:and:foo', ':', 5)"), "boo", "boo", "and", "foo");
            validateArray(parser, "SPLIT('boo:and:foo', ':', -2)"), "boo", "boo", "and", "foo");
            validateArray(parser, "SPLIT('boo:and:foo', 'o', 5)"), "b", "b", "", ":and:f", "", "");
            validateArray(parser, "SPLIT('boo:and:foo', 'o', -2)"), "b", "b", "", ":and:f", "", "");
            validateArray(parser, "SPLIT('boo:and:foo', 'o', 0)"), "b", "b", "", ":and:f");
        }
    */
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
/*
    @Test
    public void testSTARTSWITH() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "STARTSWITH");

        validateExceptionThrown(parser, "STARTSWITH()"), "STARTSWITH expected 2 parameter(s), but got 0", 1, 10);
        validateExceptionThrown(parser, "STARTSWITH('A', 'B', 2)"), "STARTSWITH expected 2 parameter(s), but got 3", 1, 10);
        validateExceptionThrown(parser, "STARTSWITH(1, 'B')"), "STARTSWITH parameter 1 expected type STRING, but was NUMBER", 1, 10);
        validateExceptionThrown(parser, "STARTSWITH('A', 2)"), "STARTSWITH parameter 2 expected type STRING, but was NUMBER", 1, 10);

        validateBoolean(parser, "STARTSWITH(null, null)"), Boolean.FALSE);
        validateBoolean(parser, "STARTSWITH('A', null)"), Boolean.FALSE);
        validateBoolean(parser, "STARTSWITH(null, 'B')"), Boolean.FALSE);
        validateBoolean(parser, "STARTSWITH('Ralph', 'I')"), Boolean.FALSE);
        validateBoolean(parser, "STARTSWITH('Ralph', 'ra')"), Boolean.FALSE);
        validateBoolean(parser, "STARTSWITH('Ralph', 'Ra')"), Boolean.TRUE);
        validateBoolean(parser, "STARTSWITH('Ralph', Upper('ra'))"), Boolean.FALSE);
    }

    @Test
    public void testSTR() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "STR");

        validateExceptionThrown(parser, "STR()"), "STR expected 1..3 parameter(s), but got 0", 1, 3);
        validateExceptionThrown(parser, "STR(123.45, 6, 2, 'test')"), "STR expected 1..3 parameter(s), but got 4", 1, 3);
        validateExceptionThrown(parser, "STR('123')"), "STR parameter 1 expected type NUMBER, but was STRING", 1, 3);
        validateExceptionThrown(parser, "STR(123, '1')"), "STR parameter 2 expected type NUMBER, but was STRING", 1, 3);
        validateExceptionThrown(parser, "STR(123, 1, '1')"), "STR parameter 3 expected type NUMBER, but was STRING", 1, 3);

        // Null arguments return null
        validateString(parser, "STR(null)"), null);
        validateString(parser, "STR(null, null)"), null);
        validateString(parser, "STR(null, null, null)"), null);
        validateString(parser, "STR(1, null)"), null);
        validateString(parser, "STR(1, 2, null)"), null);
        validateString(parser, "STR(null, 2, null)"), null);
        validateString(parser, "STR(null, null, 3)"), null);

        // Simple conversion, no width or precision values
        validateString(parser, "STR(null)"), null);
        validateString(parser, "STR(0)"), "0");
        validateString(parser, "STR(123.45)"), "123.45");
        validateString(parser, "STR(-123.45)"), "-123.45");

        // Conversion with width parameter
        validateString(parser, "STR(0, 3)"), "0");
        validateString(parser, "STR(123.45, 3)"), "123");
        validateString(parser, "STR(123.4548, 3)"), "123");
        validateString(parser, "STR(1234.4548, 3)"), "1234");
        validateString(parser, "STR(-123.4548, 3)"), "-123");
        validateString(parser, "STR(-1234.4548, 3)"), "-1234");

        // Conversion with width and precision parameters
        validateString(parser, "STR(0, 3, 1)"), "0.0");
        validateString(parser, "STR(123.45, 3, 1)"), "123.5");
        validateString(parser, "STR(123.454, 6, 2)"), "123.45");
        validateString(parser, "STR(123.455, 6, 2)"), "123.46");
        validateString(parser, "STR(1234.454, 7, 3)"), "1234.454");
        validateString(parser, "STR(-123.45, 7, 3)"), "-123.450");
        validateString(parser, "STR(-1234.45, 3, 3)"), "-1234.450");
    }

    @Test
    public void testSTRING() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "STRING");

        validateExceptionThrown(parser, "STRING()"), "STRING expected 2 parameter(s), but got 0", 1, 6);
        validateExceptionThrown(parser, "STRING('test')"), "STRING expected 2 parameter(s), but got 1", 1, 6);
        validateExceptionThrown(parser, "STRING('test', 2, 2)"), "STRING expected 2 parameter(s), but got 3", 1, 6);
        validateExceptionThrown(parser, "STRING(123, 2)"), "STRING parameter 1 expected type STRING, but was NUMBER", 1, 6);
        validateExceptionThrown(parser, "STRING('123', '2')"), "STRING parameter 2 expected type NUMBER, but was STRING", 1, 6);

        validateString(parser, "STRING(null, null)"), null);
        validateString(parser, "STRING(null, 3)"), null);
        validateString(parser, "STRING('*', null)"), null);
        validateString(parser, "STRING('', 3)"), "");
        validateString(parser, "STRING('*', 3)"), "***");
        validateString(parser, "STRING('R', 0)"), "");
        validateString(parser, "STRING('R', -10)"), "");
        validateString(parser, "STRING('RI', 3)"), "RIRIRI");
    }

    @Test
    public void testTAN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "TAN");

        validateExceptionThrown(parser, "TAN()"), "TAN expected 1 parameter(s), but got 0", 1, 3);
        validateExceptionThrown(parser, "TAN(0.01, 2)"), "TAN expected 1 parameter(s), but got 2", 1, 3);
        validateExceptionThrown(parser, "TAN('1.23')"), "TAN parameter 1 expected type NUMBER, but was STRING", 1, 3);

        validateNumericResult(parser, "TAN(null)"), null);
        validateNumericResult(parser, "TAN(0)"), "0");
        validateNumericResult(parser, "TAN(1)"), "0.01746");
        validateNumericResult(parser, "TAN(22)"), "0.40403");
        validateNumericResult(parser, "TAN(45)"), "1");
    }

    @Test
    public void testTRIM() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "TRIM");

        validateExceptionThrown(parser, "TRIM()"), "TRIM expected 1..2 parameter(s), but got 0", 1, 4);
        validateExceptionThrown(parser, "TRIM('test', 'test', 1)"), "TRIM expected 1..2 parameter(s), but got 3", 1, 4);
        validateExceptionThrown(parser, "TRIM(123)"), "TRIM parameter 1 expected type STRING, but was NUMBER", 1, 4);
        validateExceptionThrown(parser, "TRIM('123', 2)"), "TRIM parameter 2 expected type STRING, but was NUMBER", 1, 4);

        validateString(parser, "TRIM(null)"), null);
        validateString(parser, "TRIM('')"), "");
        validateString(parser, "TRIM('  \t  Hello World  \t ')"), "Hello World");
        validateString(parser, "TRIM('*****Hello World*****', '*')"), "Hello World");
    }

    @Test
    public void testTRIMLEFT() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "TRIMLEFT");

        validateExceptionThrown(parser, "TRIMLEFT()"), "TRIMLEFT expected 1..2 parameter(s), but got 0", 1, 8);
        validateExceptionThrown(parser, "TRIMLEFT('test', 'test', 1)"), "TRIMLEFT expected 1..2 parameter(s), but got 3", 1, 8);
        validateExceptionThrown(parser, "TRIMLEFT(123)"), "TRIMLEFT parameter 1 expected type STRING, but was NUMBER", 1, 8);
        validateExceptionThrown(parser, "TRIMLEFT('123', 2)"), "TRIMLEFT parameter 2 expected type STRING, but was NUMBER", 1, 8);

        validateString(parser, "TRIMLEFT(null)"), null);
        validateString(parser, "TRIMLEFT('')"), "");
        validateString(parser, "TRIMLEFT('  \t  Hello World  \t ')"), "Hello World  \t ");
        validateString(parser, "TRIMLEFT('*****Hello World*****', '*')"), "Hello World*****");
    }

    @Test
    public void testTRIMRIGHT() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "TRIMRIGHT");

        validateExceptionThrown(parser, "TRIMRIGHT()"), "TRIMRIGHT expected 1..2 parameter(s), but got 0", 1, 9);
        validateExceptionThrown(parser, "TRIMRIGHT('test', 'test', 1)"), "TRIMRIGHT expected 1..2 parameter(s), but got 3", 1, 9);
        validateExceptionThrown(parser, "TRIMRIGHT(123)"), "TRIMRIGHT parameter 1 expected type STRING, but was NUMBER", 1, 9);
        validateExceptionThrown(parser, "TRIMRIGHT('123', 2)"), "TRIMRIGHT parameter 2 expected type STRING, but was NUMBER", 1, 9);

        validateString(parser, "TRIMRIGHT(null)"), null);
        validateString(parser, "TRIMRIGHT('')"), "");
        validateString(parser, "TRIMRIGHT('  \t  Hello World  \t ')"), "  \t  Hello World");
        validateString(parser, "TRIMRIGHT('*****Hello World*****', '*')"), "*****Hello World");
    }

    @Test
    public void testUPPER() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "UPPER");

        validateExceptionThrown(parser, "UPPER()"), "UPPER expected 1 parameter(s), but got 0", 1, 5);
        validateExceptionThrown(parser, "UPPER('test', 'test')"), "UPPER expected 1 parameter(s), but got 2", 1, 5);
        validateExceptionThrown(parser, "UPPER(123)"), "UPPER parameter 1 expected type STRING, but was NUMBER", 1, 5);

        validateString(parser, "UPPER(null)"), null);
        validateString(parser, "UPPER('')"), "");
        validateString(parser, "UPPER('HellO, WORLD')"), "HELLO, WORLD");
    }

    @Test
    public void testVAL() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "VAL");

        validateExceptionThrown(parser, "VAL()"), "VAL expected 1 parameter(s), but got 0", 1, 3);
        validateExceptionThrown(parser, "VAL('test', 'test')"), "VAL expected 1 parameter(s), but got 2", 1, 3);
        validateExceptionThrown(parser, "VAL(123)"), "VAL parameter 1 expected type STRING, but was NUMBER", 1, 3);
        validateExceptionThrown(parser, "VAL('123E')"), "java.lang.NumberFormatException", 1, 3);

        validateNumericResult(parser, "VAL(null)"), null);
        validateNumericResult(parser, "VAL('')"), "0");
        validateNumericResult(parser, "VAL('123.45')"), "123.45");
    }

    */
}
