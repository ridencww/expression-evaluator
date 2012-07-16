package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;

import org.junit.Test;

public class GrammarExtendedCalcTest extends GrammarTest {

    @Test
    public void testARCCOS() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "ARCCOS");

        validateException(parser.eval("ARCCOS()"), "ARCCOS expected 1 parameter(s), but got 0", 1, 6);
        validateException(parser.eval("ARCCOS(0.01, 2)"), "ARCCOS expected 1 parameter(s), but got 2", 1, 6);
        validateException(parser.eval("ARCCOS('1.23')"), "ARCCOS parameter 1 expected type NUMBER, but was STRING", 1, 6);

        validateNumber(parser.eval("ARCCOS(null)"), null);
        validateNumber(parser.eval("ARCCOS(0.9271838546)"), "22");
        validateNumber(parser.eval("ARCCOS(0.7071068)"), "45");
    }      

    @Test
    public void testARCSIN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "ARCSIN");
        
        validateException(parser.eval("ARCSIN()"), "ARCSIN expected 1 parameter(s), but got 0", 1, 6);
        validateException(parser.eval("ARCSIN(0.01, 2)"), "ARCSIN expected 1 parameter(s), but got 2", 1, 6);
        validateException(parser.eval("ARCSIN('1.23')"), "ARCSIN parameter 1 expected type NUMBER, but was STRING", 1, 6);
        
        validateNumber(parser.eval("ARCSIN(null)"), null);
        validateNumber(parser.eval("ARCSIN(0.3746065934)"), "22");
        validateNumber(parser.eval("ARCSIN(0.7071068)"), "45");
    }      

    @Test
    public void testARCTAN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "ARCTAN");
        
        validateException(parser.eval("ARCTAN()"), "ARCTAN expected 1 parameter(s), but got 0", 1, 6);
        validateException(parser.eval("ARCTAN(0.01, 2)"), "ARCTAN expected 1 parameter(s), but got 2", 1, 6);
        validateException(parser.eval("ARCTAN('1.23')"), "ARCTAN parameter 1 expected type NUMBER, but was STRING", 1, 6);
        
        validateNumber(parser.eval("ARCTAN(null)"), null);
        validateNumber(parser.eval("ARCTAN(0)"), "0");
        validateNumber(parser.eval("ARCTAN(0.4040262258)"), "22");
        validateNumber(parser.eval("ARCTAN(1)"), "45");
    }      
    
    @Test
    public void testARRAYLEN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "ARRAYLEN");
        
        parser.eval("V1=1");
        parser.eval("V2=SPLIT('00,10,11')");
        
        validateException(parser.eval("ARRAYLEN()"), "ARRAYLEN expected 1 parameter(s), but got 0", 1, 8);
        validateException(parser.eval("ARRAYLEN(V1,V2)"), "ARRAYLEN expected 1 parameter(s), but got 2", 1, 8);
        validateException(parser.eval("ARRAYLEN('test')"), "Expected 'test' to be an array value", 1, 8);
        validateException(parser.eval("ARRAYLEN(V1)"), "Expected V1 to be an array value", 1, 8);
        
        validateNumber(parser.eval("ARRAYLEN(null)"), null);
        validateNumber(parser.eval("ARRAYLEN(V2)"), "3");
        validateNumber(parser.eval("ARRAYLEN(SPLIT('00,10,11,100'))"), "4");
    } 
    
    @Test
    public void testAVERAGE() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "AVERAGE");

        validateException(parser.eval("AVERAGE()"), "AVERAGE expected 1..n parameter(s), but got 0", 1, 7);
        validateException(parser.eval("AVERAGE('1.23')"), "AVERAGE parameter 1 expected type NUMBER, but was STRING", 1, 7);
        validateException(parser.eval("AVERAGE(1,2,'3')"), "Expected NUMBER value, but was STRING", 1, 7);

        validateNumber(parser.eval("AVERAGE(null)"), null);
        validateNumber(parser.eval("AVERAGE(0)"), "0");
        validateNumber(parser.eval("AVERAGE(2)"), "2");
        validateNumber(parser.eval("AVERAGE(2, 4, 6, 8)"), "5");
    }      
    
    @Test
    public void testCEILING() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "CEILING");

        validateException(parser.eval("CEILING()"), "CEILING expected 1 parameter(s), but got 0", 1, 7);
        validateException(parser.eval("CEILING(0.01, 2)"), "CEILING expected 1 parameter(s), but got 2", 1, 7);
        validateException(parser.eval("CEILING('1.23')"), "CEILING parameter 1 expected type NUMBER, but was STRING", 1, 7);

        validateNumber(parser.eval("CEILING(null)"), null);
        validateNumber(parser.eval("CEILING(0.01)"), "1.0");
        validateNumber(parser.eval("CEILING(2.022)"), "3.0");
    }     
    
    @Test
    public void testCONTAINS() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "CONTAINS");

        validateException(parser.eval("CONTAINS()"), "CONTAINS expected 2 parameter(s), but got 0", 1, 8);
        validateException(parser.eval("CONTAINS('A', 'B', 2)"), "CONTAINS expected 2 parameter(s), but got 3", 1, 8);
        validateException(parser.eval("CONTAINS(1, 'B')"), "CONTAINS parameter 1 expected type STRING, but was NUMBER", 1, 8);
        validateException(parser.eval("CONTAINS('A', 2)"), "CONTAINS parameter 2 expected type STRING, but was NUMBER", 1, 8);

        validateBoolean(parser.eval("CONTAINS(null, null)"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINS('A', null)"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINS(null, 'B')"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINS('Ralph', 'I')"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINS('Ralph', 'Ph')"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINS('Ralph', 'ph')"), Boolean.TRUE);
        validateBoolean(parser.eval("CONTAINS('Ralph', Upper('ph'))"), Boolean.FALSE);
    }    

    @Test
    public void testCONTAINSALL() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "CONTAINSALL");
        
        validateException(parser.eval("CONTAINSALL()"), "CONTAINSALL expected 2 parameter(s), but got 0", 1, 11);
        validateException(parser.eval("CONTAINSALL('A', 'B', 2)"), "CONTAINSALL expected 2 parameter(s), but got 3", 1, 11);
        validateException(parser.eval("CONTAINSALL(1, 'B')"), "CONTAINSALL parameter 1 expected type STRING, but was NUMBER", 1, 11);
        validateException(parser.eval("CONTAINSALL('A', 2)"), "CONTAINSALL parameter 2 expected type STRING, but was NUMBER", 1, 11);
        
        validateBoolean(parser.eval("CONTAINSALL(null, null)"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINSALL('A', null)"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINSALL(null, 'B')"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINSALL('Ralph', 'I')"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINSALL('Ralph', 'hR')"), Boolean.TRUE);
        validateBoolean(parser.eval("CONTAINSALL('Ralph', 'ph')"), Boolean.TRUE);
        validateBoolean(parser.eval("CONTAINSALL('Ralph', 'pH')"), Boolean.FALSE);
    }    

    @Test
    public void testCONTAINSANY() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "CONTAINSANY");
        
        validateException(parser.eval("CONTAINSANY()"), "CONTAINSANY expected 2 parameter(s), but got 0", 1, 11);
        validateException(parser.eval("CONTAINSANY('A', 'B', 2)"), "CONTAINSANY expected 2 parameter(s), but got 3", 1, 11);
        validateException(parser.eval("CONTAINSANY(1, 'B')"), "CONTAINSANY parameter 1 expected type STRING, but was NUMBER", 1, 11);
        validateException(parser.eval("CONTAINSANY('A', 2)"), "CONTAINSANY parameter 2 expected type STRING, but was NUMBER", 1, 11);
        
        validateBoolean(parser.eval("CONTAINSANY(null, null)"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINSANY('A', null)"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINSANY(null, 'B')"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINSANY('Ralph', 'I')"), Boolean.FALSE);
        validateBoolean(parser.eval("CONTAINSANY('Ralph', 'XhR')"), Boolean.TRUE);
        validateBoolean(parser.eval("CONTAINSANY('Ralph', 'Xph')"), Boolean.TRUE);
        validateBoolean(parser.eval("CONTAINSANY('Ralph', 'XpH')"), Boolean.TRUE);
    }    
    
    @Test
    public void testCOS() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "COS");

        validateException(parser.eval("COS()"), "COS expected 1 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("COS(0.01, 2)"), "COS expected 1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("COS('1.23')"), "COS parameter 1 expected type NUMBER, but was STRING", 1, 3);

        validateNumber(parser.eval("COS(null)"), null);
        validateNumber(parser.eval("COS(0)"), "1");
        validateNumber(parser.eval("COS(1)"), "0.99985");
        validateNumber(parser.eval("COS(22)"), "0.92718");
        validateNumber(parser.eval("COS(45)"), "0.70711");
    }       

    @Test
    public void testENDSWITH() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "ENDSWITH");
        
        validateException(parser.eval("ENDSWITH()"), "ENDSWITH expected 2 parameter(s), but got 0", 1, 8);
        validateException(parser.eval("ENDSWITH('A', 'B', 2)"), "ENDSWITH expected 2 parameter(s), but got 3", 1, 8);
        validateException(parser.eval("ENDSWITH(1, 'B')"), "ENDSWITH parameter 1 expected type STRING, but was NUMBER", 1, 8);
        validateException(parser.eval("ENDSWITH('A', 2)"), "ENDSWITH parameter 2 expected type STRING, but was NUMBER", 1, 8);
        
        validateBoolean(parser.eval("ENDSWITH(null, null)"), Boolean.FALSE);
        validateBoolean(parser.eval("ENDSWITH('A', null)"), Boolean.FALSE);
        validateBoolean(parser.eval("ENDSWITH(null, 'B')"), Boolean.FALSE);
        validateBoolean(parser.eval("ENDSWITH('Ralph', 'I')"), Boolean.FALSE);
        validateBoolean(parser.eval("ENDSWITH('Ralph', 'Ph')"), Boolean.FALSE);
        validateBoolean(parser.eval("ENDSWITH('Ralph', 'ph')"), Boolean.TRUE);
        validateBoolean(parser.eval("ENDSWITH('Ralph', Upper('ph'))"), Boolean.FALSE);
    }    
    
    @Test
    public void testEXP() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "EXP");

        validateException(parser.eval("EXP()"), "EXP expected 1 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("EXP(0.01, 2)"), "EXP expected 1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("EXP('1.23')"), "EXP parameter 1 expected type NUMBER, but was STRING", 1, 3);

        validateNumber(parser.eval("EXP(null)"), null);
        validateNumber(parser.eval("EXP(0)"), "1.0");
        validateNumber(parser.eval("EXP(1)"), "2.7182818284590455");
        validateNumber(parser.eval("EXP(2)"), "7.38905609893065");
    }     
    
    @Test
    public void testFACTORIAL() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "FACTORIAL");
        
        validateException(parser.eval("FACTORIAL()"), "FACTORIAL expected 1 parameter(s), but got 0", 1, 9);
        validateException(parser.eval("FACTORIAL(1,2)"), "FACTORIAL expected 1 parameter(s), but got 2", 1, 9);
        validateException(parser.eval("FACTORIAL('A')"), "FACTORIAL parameter 1 expected type NUMBER, but was STRING", 1, 9);
        validateException(parser.eval("FACTORIAL(-1)')"), "Value cannot be less than zero, but was -1", 1, 9);
        
        validateNumber(parser.eval("FACTORIAL(null)"), null);
        validateNumber(parser.eval("FACTORIAL(0)"), "1");
        validateNumber(parser.eval("FACTORIAL(1)"), "1");
        validateNumber(parser.eval("FACTORIAL(2)"), "2");
        validateNumber(parser.eval("FACTORIAL(5)"), "120");
        validateNumber(parser.eval("FACTORIAL(6)"), "720");
        validateNumber(parser.eval("FACTORIAL(30)"), "265252859812191058636308480000000");
    }      

    @Test
    public void testFIND() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "FIND");
        
        validateException(parser.eval("FIND()"), "FIND expected 2..3 parameter(s), but got 0", 1, 4);
        validateException(parser.eval("FIND('A','B',2,2)"), "FIND expected 2..3 parameter(s), but got 4", 1, 4);
        validateException(parser.eval("FIND(1,'B')"), "FIND parameter 1 expected type STRING, but was NUMBER", 1, 4);
        validateException(parser.eval("FIND('A',2)"), "FIND parameter 2 expected type STRING, but was NUMBER", 1, 4);
        validateException(parser.eval("FIND('A','B','C')"), "FIND parameter 3 expected type NUMBER, but was STRING", 1, 4);
        
        validateNumber(parser.eval("FIND(null,'abc')"), "0");
        validateNumber(parser.eval("FIND('AbCdEfG', null)"), "0");
        validateNumber(parser.eval("FIND('', '')"), "0");
        validateNumber(parser.eval("FIND('AbCdEfG', '')"), "0");
        validateNumber(parser.eval("FIND('AbCdEfG', 'cd')"), "0");
        validateNumber(parser.eval("FIND('AbCdEfG', 'CD')"), "0");
        validateNumber(parser.eval("FIND('AbCdEfG', 'Cd')"), "3");
        validateNumber(parser.eval("FIND('AbCdEfGCd', 'Cd', 5)"), "8");
        validateNumber(parser.eval("FIND('AbCdEfGCd', 'Cd', -15)"), "3");
        validateNumber(parser.eval("FIND('AbCdEfGCd', 'Cd', 15)"), "0");
    }     
    
    @Test
    public void testFLOOR() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "FLOOR");

        validateException(parser.eval("FLOOR()"), "FLOOR expected 1 parameter(s), but got 0", 1, 5);
        validateException(parser.eval("FLOOR(0.01, 2)"), "FLOOR expected 1 parameter(s), but got 2", 1, 5);
        validateException(parser.eval("FLOOR('A')"), "FLOOR parameter 1 expected type NUMBER, but was STRING", 1, 5);
        
        validateNumber(parser.eval("FLOOR(null)"), null);
        validateNumber(parser.eval("FLOOR(0.01)"), "0.0");
        validateNumber(parser.eval("FLOOR(2.022)"), "2.0");
    }       
    
    @Test
    public void testHEX() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "HEX");
        
        validateException(parser.eval("HEX()"), "HEX expected 1 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("HEX(123, 'test')"), "HEX expected 1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("HEX('123')"), "HEX parameter 1 expected type NUMBER, but was STRING", 1, 3);

        validateString(parser.eval("HEX(null)"), null);
        validateString(parser.eval("HEX(0)"), "00");
        validateString(parser.eval("HEX(10)"), "0A");
        validateString(parser.eval("HEX(-10)"), "F6");
        validateString(parser.eval("HEX(-1000)"), "FC18");
        validateString(parser.eval("HEX(-125000)"), "FFFE17B8");
    }

    @Test
    public void testISBLANK() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "ISBLANK");
        
        validateException(parser.eval("ISBLANK()"), "ISBLANK expected 1 parameter(s), but got 0", 1, 7);
        validateException(parser.eval("ISBLANK(123, 'test')"), "ISBLANK expected 1 parameter(s), but got 2", 1, 7);
        
        validateBoolean(parser.eval("ISBLANK(null)"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBLANK(NULL)"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBLANK('')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBLANK('  ')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBLANK('  \t  ')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBLANK(' test ')"), Boolean.FALSE);
    }

    @Test
    public void testISBOOLEAN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "ISBOOLEAN");
        
        validateException(parser.eval("ISBOOLEAN()"), "ISBOOLEAN expected 1 parameter(s), but got 0", 1, 9);
        validateException(parser.eval("ISBOOLEAN(123, 'test')"), "ISBOOLEAN expected 1 parameter(s), but got 2", 1, 9);
        
        validateBoolean(parser.eval("ISBOOLEAN(NOW())"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN(null)"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN('')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN('Noway')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN(1.000000000000001)"),Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN(0)"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN('0')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN('0.0')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN('off')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN('OFF')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN('false')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN('False')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN(1==0)"), Boolean.FALSE);
        validateBoolean(parser.eval("ISBOOLEAN(1)"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBOOLEAN(2-1)"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBOOLEAN('1.000')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBOOLEAN('on')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBOOLEAN('ON')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBOOLEAN('yes')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBOOLEAN('TRUE')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBOOLEAN('trUe')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISBOOLEAN(1==1)"), Boolean.TRUE);        
    }

    @Test
    public void testISNULL() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "ISNULL");
        
        validateException(parser.eval("ISNULL()"), "ISNULL expected 1 parameter(s), but got 0", 1, 6);
        validateException(parser.eval("ISNULL(123, 'test')"), "ISNULL expected 1 parameter(s), but got 2", 1, 6);
        
        validateBoolean(parser.eval("ISNULL(null)"), Boolean.TRUE);
        validateBoolean(parser.eval("ISNULL(NULL)"), Boolean.TRUE);
        validateBoolean(parser.eval("ISNULL('')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISNULL('  ')"), Boolean.FALSE);
    }

    @Test
    public void testISNUMBER() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "ISNUMBER");
        
        validateException(parser.eval("ISNUMBER()"), "ISNUMBER expected 1 parameter(s), but got 0", 1, 8);
        validateException(parser.eval("ISNUMBER(123, 'test')"), "ISNUMBER expected 1 parameter(s), but got 2", 1, 8);
        
        // This will create a NUMBER token, which will be examined by its string value later
        parser.eval("N=123.45/1.0");
        
        validateBoolean(parser.eval("ISNUMBER(null)"), Boolean.FALSE);
        validateBoolean(parser.eval("ISNUMBER(NULL)"), Boolean.FALSE);
        validateBoolean(parser.eval("ISNUMBER('')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISNUMBER('  ')"), Boolean.FALSE);
        validateBoolean(parser.eval("ISNUMBER('123')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISNUMBER('123.45')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISNUMBER('-123')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISNUMBER('-123.45')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISNUMBER('1.4E12')"), Boolean.TRUE);
        validateBoolean(parser.eval("ISNUMBER(PI)"), Boolean.TRUE);
        validateBoolean(parser.eval("ISNUMBER(N)"), Boolean.TRUE);
    }
    
    @Test
    public void testLEFT() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "LEFT");

        validateException(parser.eval("LEFT()"), "LEFT expected 2 parameter(s), but got 0", 1, 4);
        validateException(parser.eval("LEFT('test')"), "LEFT expected 2 parameter(s), but got 1", 1, 4);
        validateException(parser.eval("LEFT('test', 2, 2)"), "LEFT expected 2 parameter(s), but got 3", 1, 4);
        validateException(parser.eval("LEFT(123, 2)"), "LEFT parameter 1 expected type STRING, but was NUMBER", 1, 4);
        
        validateString(parser.eval("LEFT(null, 2)"), null);
        validateString(parser.eval("LEFT('', 3)"), "");
        validateString(parser.eval("LEFT('12345', null)"), "12345");
        validateString(parser.eval("LEFT('12345', 0)"), "");
        validateString(parser.eval("LEFT('12345', -10)"), "");
        validateString(parser.eval("LEFT('12345', 3)"), "123");
        validateString(parser.eval("LEFT('12', 5)"), "12");
        validateString(parser.eval("LEFT(RIGHT('12', 2), 5)"), "12");
    }    
    
    @Test
    public void testLEN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "LEN");
        
        validateException(parser.eval("LEN()"), "LEN expected 1 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("LEN('test', 'test')"), "LEN expected 1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("LEN(123)"), "LEN parameter 1 expected type STRING, but was NUMBER", 1, 3);
        
        validateNumber(parser.eval("LEN('')"), "0");
        validateNumber(parser.eval("LEN(null)"), "0");
        validateNumber(parser.eval("LEN('Hello')"), "5");
    }

    @Test
    public void testLOG() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "LOG");

        validateException(parser.eval("LOG()"), "LOG expected 1 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("LOG(0.01, 2)"), "LOG expected 1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("LOG('1.23')"), "LOG parameter 1 expected type NUMBER, but was STRING", 1, 3);
        
        validateNumber(parser.eval("LOG(null)"), null);
        validateNumber(parser.eval("LOG(1)"), "0.0");
        validateNumber(parser.eval("LOG(2)"), "0.6931471805599453");
    } 

    @Test
    public void testLOG10() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "LOG10");
        
        validateException(parser.eval("LOG10()"), "LOG10 expected 1 parameter(s), but got 0", 1, 5);
        validateException(parser.eval("LOG10(0.01, 2)"), "LOG10 expected 1 parameter(s), but got 2", 1, 5);
        validateException(parser.eval("LOG10('1.23')"), "LOG10 parameter 1 expected type NUMBER, but was STRING", 1, 5);
        
        validateNumber(parser.eval("LOG10(null)"), null);
        validateNumber(parser.eval("LOG10(1)"), "0.0");
        validateNumber(parser.eval("LOG10(2)"), "0.3010299956639812");
    } 
    
    @Test
    public void testLOWER() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "LOWER");

        validateException(parser.eval("LOWER()"), "LOWER expected 1 parameter(s), but got 0", 1, 5);
        validateException(parser.eval("LOWER('test', 'test')"), "LOWER expected 1 parameter(s), but got 2", 1, 5);
        validateException(parser.eval("LOWER(123)"), "LOWER parameter 1 expected type STRING, but was NUMBER", 1, 5);
        
        validateString(parser.eval("LOWER(null)"), null);
        validateString(parser.eval("LOWER('')"), "");
        validateString(parser.eval("LOWER('HellO, WORLD')"), "hello, world");
    }

    @Test
    public void testMAKEBOOLEAN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "MAKEBOOLEAN");
        
        validateException(parser.eval("MAKEBOOLEAN()"), "MAKEBOOLEAN expected 1 parameter(s), but got 0", 1, 11);
        validateException(parser.eval("MAKEBOOLEAN('test', 'test')"), "MAKEBOOLEAN expected 1 parameter(s), but got 2", 1, 11);
        
        validateBoolean(parser.eval("MAKEBOOLEAN(NOW())"), null);
        validateBoolean(parser.eval("MAKEBOOLEAN(null)"), null);
        validateBoolean(parser.eval("MAKEBOOLEAN('')"), null);
        validateBoolean(parser.eval("MAKEBOOLEAN('Noway')"), null);
        validateBoolean(parser.eval("MAKEBOOLEAN(1.000000000000001)"), null);
        validateBoolean(parser.eval("MAKEBOOLEAN(0)"), Boolean.FALSE);
        validateBoolean(parser.eval("MAKEBOOLEAN('0')"), Boolean.FALSE);
        validateBoolean(parser.eval("MAKEBOOLEAN('0.0')"), Boolean.FALSE);
        validateBoolean(parser.eval("MAKEBOOLEAN('off')"), Boolean.FALSE);
        validateBoolean(parser.eval("MAKEBOOLEAN('OFF')"), Boolean.FALSE);
        validateBoolean(parser.eval("MAKEBOOLEAN('false')"), Boolean.FALSE);
        validateBoolean(parser.eval("MAKEBOOLEAN('False')"), Boolean.FALSE);
        validateBoolean(parser.eval("MAKEBOOLEAN(1==0)"), Boolean.FALSE);
        validateBoolean(parser.eval("MAKEBOOLEAN(1)"), Boolean.TRUE);
        validateBoolean(parser.eval("MAKEBOOLEAN(2-1)"), Boolean.TRUE);
        validateBoolean(parser.eval("MAKEBOOLEAN('1.000')"), Boolean.TRUE);
        validateBoolean(parser.eval("MAKEBOOLEAN('on')"), Boolean.TRUE);
        validateBoolean(parser.eval("MAKEBOOLEAN('ON')"), Boolean.TRUE);
        validateBoolean(parser.eval("MAKEBOOLEAN('yes')"), Boolean.TRUE);
        validateBoolean(parser.eval("MAKEBOOLEAN('TRUE')"), Boolean.TRUE);
        validateBoolean(parser.eval("MAKEBOOLEAN('trUe')"), Boolean.TRUE);
        validateBoolean(parser.eval("MAKEBOOLEAN(1==1)"), Boolean.TRUE);
    }

    @Test
    public void testMATCH() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "MATCH");

        validateException(parser.eval("MATCH()"), "MATCH expected 2 parameter(s), but got 0", 1, 5);
        validateException(parser.eval("MATCH('A','B', 1)"), "MATCH expected 2 parameter(s), but got 3", 1, 5);
        validateException(parser.eval("MATCH(1, '1.23')"), "MATCH parameter 1 expected type STRING, but was NUMBER", 1, 5);
        validateException(parser.eval("MATCH('1.23', 1)"), "MATCH parameter 2 expected type STRING, but was NUMBER", 1, 5);
        validateException(parser.eval("MATCH('1.23', '[')"), "Invalid regex pattern: [", 1, 5);

        validateArray(parser.eval("MATCH(null, null)"), null);
        validateArray(parser.eval("MATCH('a', null)"), null);
        validateArray(parser.eval("MATCH(null, 'b')"), null);
        validateArray(parser.eval("MATCH(null, 'b')"), null);
        validateArray(parser.eval("MATCH('Phone:', '[\\(](\\d{3})\\D*(\\d{3})\\D*(\\d{4})\\D*(\\d*)')"), ""); // no match
        validateArray(parser.eval("MATCH('Phone: (815) 555-1212 x100 (Work)', '[\\(](\\d{3})\\D*(\\d{3})\\D*(\\d{4})\\D*(\\d*)')"), 
            "(815) 555-1212 x100",  // asString 
            "(815) 555-1212 x100", "815", "555", "1212", "100");  // group 0..n
    }      

    @Test
    public void testMATCHBYLEN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "MATCHBYLEN");

        validateException(parser.eval("MATCHBYLEN()"), "MATCHBYLEN expected 3 parameter(s), but got 0", 1, 10);
        validateException(parser.eval("MATCHBYLEN('A','B', 'C', 'D')"), "MATCHBYLEN expected 3 parameter(s), but got 4", 1, 10);
        validateException(parser.eval("MATCHBYLEN(1, 'B', 'C')"), "MATCHBYLEN parameter 1 expected type STRING, but was NUMBER", 1, 10);
        validateException(parser.eval("MATCHBYLEN('A', 1, 'C')"), "MATCHBYLEN parameter 2 expected type STRING, but was NUMBER", 1, 10);
        validateException(parser.eval("MATCHBYLEN('A', 'B', 1)"), "MATCHBYLEN parameter 3 expected type STRING, but was NUMBER", 1, 10);
        validateException(parser.eval("MATCHBYLEN('A', '[', 'C')"), "Invalid regex pattern: [", 1, 10);

        validateString(parser.eval("MATCHBYLEN(null, null, null)"), null);
        validateString(parser.eval("MATCHBYLEN('a', null, null)"), null);
        validateString(parser.eval("MATCHBYLEN(null, 'b', null)"), null);
        validateString(parser.eval("MATCHBYLEN(null, null, 'c')"), null);

        // No match -> "no value"
        String variations = "0='no value':7=      ###-####:10=(###) ###-####:?='Ralph' + ' ' + 'Iden'";
        validateString(parser.eval("MATCHBYLEN('', '[0-9]*', \"" + variations + "\")"), "no value");
        
        // No match - > empty
        variations = "0=:7=      ###-####:10=(###) ###-####:?='Ralph' + ' ' + 'Iden'";
        validateString(parser.eval("MATCHBYLEN('', '[0-9]*', \"" + variations + "\")"), ""); 
        
        // Match pattern of length 7
        validateString(parser.eval("MATCHBYLEN('5551212', '[0-9]*', \"" + variations + "\")"), "      555-1212");
        
        // Match pattern of length 10
        validateString(parser.eval("MATCHBYLEN('8155551212', '[0-9]*', \"" + variations + "\")"), "(815) 555-1212");
        
        // Matched, but not one of the specified lengths uses default variation
        validateString(parser.eval("MATCHBYLEN('81555512121234', '[0-9]*', \"" + variations + "\")"), "Ralph Iden");

        // Matched, but not one of the specified lengths and not default returns empty string
        variations = "0=:7=      ###-####:10=(###) ###-####";
        validateString(parser.eval("MATCHBYLEN('81555512121234', '[0-9]*', \"" + variations + "\")"), "");
        
        // Poorly formed variation patterns return empty strings -----------------------------------------------
        // Match where variation is empty and at the end of the variations
        variations = "7=";
        validateString(parser.eval("MATCHBYLEN('5551212', '[0-9]*', \"" + variations + "\")"), "");
        
        // Match with a trailing colon in variation string
        variations = "0=:7=      ###-####:10=(###) ###-####:";
        validateString(parser.eval("MATCHBYLEN('81555512121234', '[0-9]*', \"" + variations + "\")"), "");
        
        // No matches with a trailing colon in variation string
        variations = "0=:7=      ###-####:10=(###) ###-####:";
        validateString(parser.eval("MATCHBYLEN('A', 'B', \"" + variations + "\")"), "");        
    }     
    
    @Test
    public void testMAX() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "MAX");
        
        validateException(parser.eval("MAX()"), "MAX expected 2 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("MAX(0.01, 2, 1)"), "MAX expected 2 parameter(s), but got 3", 1, 3);
        validateException(parser.eval("MAX('1.23', 1)"), "MAX parameter 1 expected type NUMBER, but was STRING", 1, 3);
        validateException(parser.eval("MAX(1, '1.23')"), "MAX parameter 2 expected type NUMBER, but was STRING", 1, 3);
        
        validateNumber(parser.eval("MAX(null, null)"), null);
        validateNumber(parser.eval("MAX(1, null)"), null);
        validateNumber(parser.eval("MAX(null, 2)"), null);
        validateNumber(parser.eval("MAX(-1, 4)"), "4");
        validateNumber(parser.eval("MAX(4, -1)"), "4");
        validateNumber(parser.eval("MAX(-4, -1)"), "-1");
        validateNumber(parser.eval("MAX(4, 4)"), "4");
        validateNumber(parser.eval("MAX(-1, -1)"), "-1");
    }      
    
    @Test
    public void testMID() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "MID");
    
        validateException(parser.eval("MID()"), "MID expected 2..3 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("MID('test')"), "MID expected 2..3 parameter(s), but got 1", 1, 3);
        validateException(parser.eval("MID('test', 1, 2, 3)"), "MID expected 2..3 parameter(s), but got 4", 1, 3);
        validateException(parser.eval("MID(123, 1)"), "MID parameter 1 expected type STRING, but was NUMBER", 1, 3);
        validateException(parser.eval("MID('123', '1')"), "MID parameter 2 expected type NUMBER, but was STRING", 1, 3);
        validateException(parser.eval("MID('123', 1, '2')"), "MID parameter 3 expected type NUMBER, but was STRING", 1, 3);
        
        validateString(parser.eval("MID(null, null)"), null);
        validateString(parser.eval("MID(null, null, null)"), null);
        validateString(parser.eval("MID('test', null)"), null);
        validateString(parser.eval("MID(null, 2)"), null);
        validateString(parser.eval("MID('test', 2, null)"), null);
        validateString(parser.eval("MID('', 3)"), "");
        validateString(parser.eval("MID('12345', 0)"), "");
        validateString(parser.eval("MID('12345', -10)"), "");
        validateString(parser.eval("MID('12', 5)"), "");
        validateString(parser.eval("MID('12345', 3)"), "345");
        validateString(parser.eval("MID('12345', 3, 1)"), "3");
        validateString(parser.eval("MID('12345', 3, 2)"), "34");
        validateString(parser.eval("MID('12345', 3, 3)"), "345");
        validateString(parser.eval("MID('12345', 3, 4)"), "345");
    }
    
    @Test
    public void testMIN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "MIN");

        validateException(parser.eval("MIN()"), "MIN expected 2 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("MIN(0.01, 2, 1)"), "MIN expected 2 parameter(s), but got 3", 1, 3);
        validateException(parser.eval("MIN('1.23', 1)"), "MIN parameter 1 expected type NUMBER, but was STRING", 1, 3);
        validateException(parser.eval("MIN(1, '1.23')"), "MIN parameter 2 expected type NUMBER, but was STRING", 1, 3);

        validateNumber(parser.eval("MIN(null, null)"), null);
        validateNumber(parser.eval("MIN(1, null)"), null);
        validateNumber(parser.eval("MIN(null, 2)"), null);
        validateNumber(parser.eval("MIN(-1, 4)"), "-1");
        validateNumber(parser.eval("MIN(4, -1)"), "-1");
        validateNumber(parser.eval("MIN(-4, -1)"), "-4");
        validateNumber(parser.eval("MIN(4, 4)"), "4");
        validateNumber(parser.eval("MIN(-1, -1)"), "-1");
    }     

    @Test
    public void testNAMECASE() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "NAMECASE");

        validateException(parser.eval("NAMECASE()"), "NAMECASE expected 1 parameter(s), but got 0", 1, 8);
        validateException(parser.eval("NAMECASE('test', 'test')"), "NAMECASE expected 1 parameter(s), but got 2", 1, 8);
        validateException(parser.eval("NAMECASE(123)"), "NAMECASE parameter 1 expected type STRING, but was NUMBER", 1, 8);

        validateString(parser.eval("NAMECASE(null)"), null);
        validateString(parser.eval("NAMECASE('')"), "");
        validateString(parser.eval("NAMECASE('ralph iden')"), "Ralph Iden");
        validateString(parser.eval("NAMECASE('RALPH IDEN')"), "Ralph Iden");
        validateString(parser.eval("NAMECASE('Ralph Iden')"), "Ralph Iden");
        validateString(parser.eval("NAMECASE('  RALPH\t\tW. IDEN  ')"), "  Ralph\t\tW. Iden  ");
    }    

    @Test
    public void testRANDOM() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "RANDOM");

        validateException(parser.eval("RANDOM(1,2,3)"), "RANDOM expected 0..2 parameter(s), but got 3", 1, 6);
        validateException(parser.eval("RANDOM('123', 2)"), "RANDOM parameter 1 expected type NUMBER, but was STRING", 1, 6);
        validateException(parser.eval("RANDOM(1, '123')"), "RANDOM parameter 2 expected type NUMBER, but was STRING", 1, 6);
       
        assertTrue(parser.eval("RANDOM(0)").asString().startsWith("0"));

        Value result;
        BigDecimal bdMax = new BigDecimal("100");
        for (int i = 0; i < 1000; i++) {
            result = parser.eval("RANDOM(100)");
            if (result.asNumber().compareTo(BigDecimal.ZERO) < 0 ||
                result.asNumber().compareTo(bdMax) > 0) {
                fail("Random number outside of range (0..100)");
            }
        }

        BigDecimal bdMin1 = new BigDecimal("50");
        BigDecimal bdMax1 = new BigDecimal("99");
        for (int i = 0; i < 1000; i++) {
            result = parser.eval("RANDOM(50, 99)");
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
       
       validateException(parser.eval("REPLACE()"), "REPLACE expected 3 parameter(s), but got 0", 1, 7);
       validateException(parser.eval("REPLACE('test', 'me')"), "REPLACE expected 3 parameter(s), but got 2", 1, 7);
       validateException(parser.eval("REPLACE(123, '2', '3')"), "REPLACE parameter 1 expected type STRING, but was NUMBER", 1, 7);
       validateException(parser.eval("REPLACE('1', 2, '3')"), "REPLACE parameter 2 expected type STRING, but was NUMBER", 1, 7);
       validateException(parser.eval("REPLACE('1', '2', 3)"), "REPLACE parameter 3 expected type STRING, but was NUMBER", 1, 7);

       validateString(parser.eval("REPLACE(null, null, null)"), null);
       validateString(parser.eval("REPLACE('Ralph', null, null)"), "Ralph");
       validateString(parser.eval("REPLACE('Mr. Ralph', null, 'Jeremy')"), "Mr. Ralph");
       validateString(parser.eval("REPLACE('Mr. Ralph', 'Ralph', null)"), "Mr. Ralph");
       validateString(parser.eval("REPLACE('Mr. Ralph', 'Ralph', 'Jeremy')"), "Mr. Jeremy");
   }

   @Test
   public void testREPLACEALL() throws Exception {
       Parser parser = new Parser(new GrammarExtendedCalc());
       
       validatePattern(parser, "REPLACEALL");
       
       validateException(parser.eval("REPLACEALL()"), "REPLACEALL expected 3 parameter(s), but got 0", 1, 10);
       validateException(parser.eval("REPLACEALL('test', 'me')"), "REPLACEALL expected 3 parameter(s), but got 2", 1, 10);
       validateException(parser.eval("REPLACEALL(123, '2', '3')"), "REPLACEALL parameter 1 expected type STRING, but was NUMBER", 1, 10);
       validateException(parser.eval("REPLACEALL('1', 2, '3')"), "REPLACEALL parameter 2 expected type STRING, but was NUMBER", 1, 10);
       validateException(parser.eval("REPLACEALL('1', '2', 3)"), "REPLACEALL parameter 3 expected type STRING, but was NUMBER", 1, 10);
       
       validateString(parser.eval("REPLACEALL(null, null, null)"), null);
       validateString(parser.eval("REPLACEALL('Ralph', null, null)"), "Ralph");
       validateString(parser.eval("REPLACEALL('Ralph', 'al', null)"), "Ralph");
       validateString(parser.eval("REPLACEALL('Ralph', null, 'A')"), "Ralph");
       validateString(parser.eval("REPLACEALL('boo:and:foo', ':', '-')"), "boo-and-foo");
       validateString(parser.eval("REPLACEALL('abbbcabc 000 abc', 'abc', '123')"), "abbbc123 000 123");
       validateString(parser.eval("REPLACEALL('acabc 000 abc', 'ab*c', '123')"), "123123 000 123");
       validateString(parser.eval("REPLACEALL('acabc 000 abc', 'ab+c', '123')"), "ac123 000 123");
       validateString(parser.eval("REPLACEALL('abbbcabc 000 abc', 'ab+c', '123')"), "123123 000 123");
   }

   @Test
   public void testREPLACEFIRST() throws Exception {
       Parser parser = new Parser(new GrammarExtendedCalc());
       
       validatePattern(parser, "REPLACEFIRST");
       
       validateException(parser.eval("REPLACEFIRST()"), "REPLACEFIRST expected 3 parameter(s), but got 0", 1, 12);
       validateException(parser.eval("REPLACEFIRST('test', 'me')"), "REPLACEFIRST expected 3 parameter(s), but got 2", 1, 12);
       validateException(parser.eval("REPLACEFIRST(123, '2', '3')"), "REPLACEFIRST parameter 1 expected type STRING, but was NUMBER", 1, 12);
       validateException(parser.eval("REPLACEFIRST('1', 2, '3')"), "REPLACEFIRST parameter 2 expected type STRING, but was NUMBER", 1, 12);
       validateException(parser.eval("REPLACEFIRST('1', '2', 3)"), "REPLACEFIRST parameter 3 expected type STRING, but was NUMBER", 1, 12);
       
       validateString(parser.eval("REPLACEFIRST(null, null, null)"), null);
       validateString(parser.eval("REPLACEFIRST('Ralph', null, null)"), "Ralph");
       validateString(parser.eval("REPLACEFIRST('Ralph', 'al', null)"), "Ralph");
       validateString(parser.eval("REPLACEFIRST('Ralph', null, 'A')"), "Ralph");
       validateString(parser.eval("REPLACEFIRST('boo:and:foo', ':', '-')"), "boo-and:foo");
       validateString(parser.eval("REPLACEFIRST('abbbcabc 000 abc', 'abc', '123')"), "abbbc123 000 abc");
       validateString(parser.eval("REPLACEFIRST('acabc 000 abc', 'ab*c', '123')"), "123abc 000 abc");
       validateString(parser.eval("REPLACEFIRST('acabc 000 abc', 'ab+c', '123')"), "ac123 000 abc");
       validateString(parser.eval("REPLACEFIRST('abbbcabc 000 abc', 'ab+c', '123')"), "123abc 000 abc");
   }
    
    
    @Test
    public void testRIGHT() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "RIGHT");
        
        validateException(parser.eval("RIGHT()"), "RIGHT expected 2 parameter(s), but got 0", 1, 5);
        validateException(parser.eval("RIGHT('test')"), "RIGHT expected 2 parameter(s), but got 1", 1, 5);
        validateException(parser.eval("RIGHT('test', 2, 2)"), "RIGHT expected 2 parameter(s), but got 3", 1, 5);
        validateException(parser.eval("RIGHT(123, 2)"), "RIGHT parameter 1 expected type STRING, but was NUMBER", 1, 5);
        
        validateString(parser.eval("RIGHT(null, 2)"), null);
        validateString(parser.eval("RIGHT('', 3)"), "");
        validateString(parser.eval("RIGHT('12345', null)"), "12345");
        validateString(parser.eval("RIGHT('12345', 0)"), "");
        validateString(parser.eval("RIGHT('12345', -10)"), "");
        validateString(parser.eval("RIGHT('12345', 3)"), "345");
        validateString(parser.eval("RIGHT('12', 5)"), "12");
        validateString(parser.eval("RIGHT(LEFT('12345', 3), 2)"), "23");
    }     
    
    @Test
    public void testSIN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "SIN");

        validateException(parser.eval("SIN()"), "SIN expected 1 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("SIN(0.01, 2)"), "SIN expected 1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("SIN('1.23')"), "SIN parameter 1 expected type NUMBER, but was STRING", 1, 3);

        validateNumber(parser.eval("SIN(null)"), null);
        validateNumber(parser.eval("SIN(0)"), "0");
        validateNumber(parser.eval("SIN(1)"), "0.01745");
        validateNumber(parser.eval("SIN(22)"), "0.37461");
        validateNumber(parser.eval("SIN(45)"), "0.70711");
    }     

    @Test
    public void testSPLIT() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "SPLIT");
        
        validateException(parser.eval("SPLIT()"), "SPLIT expected 1..3 parameter(s), but got 0", 1, 5);
        validateException(parser.eval("SPLIT('a,b,c', ',', 2, 3)"), "SPLIT expected 1..3 parameter(s), but got 4", 1, 5);
        validateException(parser.eval("SPLIT(1.23)"), "SPLIT parameter 1 expected type STRING, but was NUMBER", 1, 5);
        validateException(parser.eval("SPLIT('a,b,c', 2)"), "SPLIT parameter 2 expected type STRING, but was NUMBER", 1, 5);
        validateException(parser.eval("SPLIT('a,b,c', ',', 'a')"), "SPLIT parameter 3 expected type NUMBER, but was STRING", 1, 5);
        
        validateArray(parser.eval("SPLIT(null)"), null);
        validateArray(parser.eval("SPLIT(null, null)"), null);
        validateArray(parser.eval("SPLIT('')"), "", "");
        validateArray(parser.eval("SPLIT('Ralph')"), "Ralph", "Ralph");
        validateArray(parser.eval("SPLIT('Ralph', null)"), "Ralph", "Ralph");
        validateArray(parser.eval("SPLIT('Ralph', null, null)"), "Ralph", "Ralph");

        validateArray(parser.eval("SPLIT('Ralph,Iden')"), "Ralph", "Ralph", "Iden");
        validateArray(parser.eval("SPLIT('boo:and:foo', ':', 2)"), "boo", "boo", "and:foo");
        validateArray(parser.eval("SPLIT('boo:and:foo', ':', 5)"), "boo", "boo", "and", "foo");
        validateArray(parser.eval("SPLIT('boo:and:foo', ':', -2)"), "boo", "boo", "and", "foo");
        validateArray(parser.eval("SPLIT('boo:and:foo', 'o', 5)"), "b", "b", "", ":and:f", "", "");
        validateArray(parser.eval("SPLIT('boo:and:foo', 'o', -2)"), "b", "b", "", ":and:f", "", "");
        validateArray(parser.eval("SPLIT('boo:and:foo', 'o', 0)"), "b", "b", "", ":and:f");
    }     

    @Test
    public void testSTARTSWITH() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "STARTSWITH");

        validateException(parser.eval("STARTSWITH()"), "STARTSWITH expected 2 parameter(s), but got 0", 1, 10);
        validateException(parser.eval("STARTSWITH('A', 'B', 2)"), "STARTSWITH expected 2 parameter(s), but got 3", 1, 10);
        validateException(parser.eval("STARTSWITH(1, 'B')"), "STARTSWITH parameter 1 expected type STRING, but was NUMBER", 1, 10);
        validateException(parser.eval("STARTSWITH('A', 2)"), "STARTSWITH parameter 2 expected type STRING, but was NUMBER", 1, 10);

        validateBoolean(parser.eval("STARTSWITH(null, null)"), Boolean.FALSE);
        validateBoolean(parser.eval("STARTSWITH('A', null)"), Boolean.FALSE);
        validateBoolean(parser.eval("STARTSWITH(null, 'B')"), Boolean.FALSE);
        validateBoolean(parser.eval("STARTSWITH('Ralph', 'I')"), Boolean.FALSE);
        validateBoolean(parser.eval("STARTSWITH('Ralph', 'ra')"), Boolean.FALSE);
        validateBoolean(parser.eval("STARTSWITH('Ralph', 'Ra')"), Boolean.TRUE);
        validateBoolean(parser.eval("STARTSWITH('Ralph', Upper('ra'))"), Boolean.FALSE);
    }    

    @Test
    public void testSTR() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "STR");

        validateException(parser.eval("STR()"), "STR expected 1..3 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("STR(123.45, 6, 2, 'test')"), "STR expected 1..3 parameter(s), but got 4", 1, 3);
        validateException(parser.eval("STR('123')"), "STR parameter 1 expected type NUMBER, but was STRING", 1, 3);
        validateException(parser.eval("STR(123, '1')"), "STR parameter 2 expected type NUMBER, but was STRING", 1, 3);
        validateException(parser.eval("STR(123, 1, '1')"), "STR parameter 3 expected type NUMBER, but was STRING", 1, 3);

        // Null arguments return null
        validateString(parser.eval("STR(null)"), null);
        validateString(parser.eval("STR(null, null)"), null);
        validateString(parser.eval("STR(null, null, null)"), null);
        validateString(parser.eval("STR(1, null)"), null);
        validateString(parser.eval("STR(1, 2, null)"), null);
        validateString(parser.eval("STR(null, 2, null)"), null);
        validateString(parser.eval("STR(null, null, 3)"), null);   
        
        // Simple conversion, no width or precision values
        validateString(parser.eval("STR(null)"), null);
        validateString(parser.eval("STR(0)"), "0");
        validateString(parser.eval("STR(123.45)"), "123.45");
        validateString(parser.eval("STR(-123.45)"), "-123.45");
        
        // Conversion with width parameter
        validateString(parser.eval("STR(0, 3)"), "0");
        validateString(parser.eval("STR(123.45, 3)"), "123");
        validateString(parser.eval("STR(123.4548, 3)"), "123");
        validateString(parser.eval("STR(1234.4548, 3)"), "1234");
        validateString(parser.eval("STR(-123.4548, 3)"), "-123");
        validateString(parser.eval("STR(-1234.4548, 3)"), "-1234");
        
        // Conversion with width and precision parameters
        validateString(parser.eval("STR(0, 3, 1)"), "0.0");
        validateString(parser.eval("STR(123.45, 3, 1)"), "123.5");
        validateString(parser.eval("STR(123.454, 6, 2)"), "123.45");
        validateString(parser.eval("STR(123.455, 6, 2)"), "123.46");
        validateString(parser.eval("STR(1234.454, 7, 3)"), "1234.454");
        validateString(parser.eval("STR(-123.45, 7, 3)"), "-123.450");
        validateString(parser.eval("STR(-1234.45, 3, 3)"), "-1234.450");
    }    
    
    @Test
    public void testSTRING() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "STRING");

        validateException(parser.eval("STRING()"), "STRING expected 2 parameter(s), but got 0", 1, 6);
        validateException(parser.eval("STRING('test')"), "STRING expected 2 parameter(s), but got 1", 1, 6);
        validateException(parser.eval("STRING('test', 2, 2)"), "STRING expected 2 parameter(s), but got 3", 1, 6);
        validateException(parser.eval("STRING(123, 2)"), "STRING parameter 1 expected type STRING, but was NUMBER", 1, 6);
        validateException(parser.eval("STRING('123', '2')"), "STRING parameter 2 expected type NUMBER, but was STRING", 1, 6);

        validateString(parser.eval("STRING(null, null)"), null);
        validateString(parser.eval("STRING(null, 3)"), null);
        validateString(parser.eval("STRING('*', null)"), null);
        validateString(parser.eval("STRING('', 3)"), "");
        validateString(parser.eval("STRING('*', 3)"), "***");
        validateString(parser.eval("STRING('R', 0)"), "");
        validateString(parser.eval("STRING('R', -10)"), "");
        validateString(parser.eval("STRING('RI', 3)"), "RIRIRI");
    }     
    
    @Test
    public void testTAN() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "TAN");

        validateException(parser.eval("TAN()"), "TAN expected 1 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("TAN(0.01, 2)"), "TAN expected 1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("TAN('1.23')"), "TAN parameter 1 expected type NUMBER, but was STRING", 1, 3);

        validateNumber(parser.eval("TAN(null)"), null);
        validateNumber(parser.eval("TAN(0)"), "0");
        validateNumber(parser.eval("TAN(1)"), "0.01746");
        validateNumber(parser.eval("TAN(22)"), "0.40403");
        validateNumber(parser.eval("TAN(45)"), "1");
    }      
    
    @Test
    public void testTRIM() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "TRIM");

        validateException(parser.eval("TRIM()"), "TRIM expected 1..2 parameter(s), but got 0", 1, 4);
        validateException(parser.eval("TRIM('test', 'test', 1)"), "TRIM expected 1..2 parameter(s), but got 3", 1, 4);
        validateException(parser.eval("TRIM(123)"), "TRIM parameter 1 expected type STRING, but was NUMBER", 1, 4);
        validateException(parser.eval("TRIM('123', 2)"), "TRIM parameter 2 expected type STRING, but was NUMBER", 1, 4);
        
        validateString(parser.eval("TRIM(null)"), null);
        validateString(parser.eval("TRIM('')"), "");
        validateString(parser.eval("TRIM('  \t  Hello World  \t ')"), "Hello World");
        validateString(parser.eval("TRIM('*****Hello World*****', '*')"), "Hello World");
    }

    @Test
    public void testTRIMLEFT() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "TRIMLEFT");
        
        validateException(parser.eval("TRIMLEFT()"), "TRIMLEFT expected 1..2 parameter(s), but got 0", 1, 8);
        validateException(parser.eval("TRIMLEFT('test', 'test', 1)"), "TRIMLEFT expected 1..2 parameter(s), but got 3", 1, 8);
        validateException(parser.eval("TRIMLEFT(123)"), "TRIMLEFT parameter 1 expected type STRING, but was NUMBER", 1, 8);
        validateException(parser.eval("TRIMLEFT('123', 2)"), "TRIMLEFT parameter 2 expected type STRING, but was NUMBER", 1, 8);
        
        validateString(parser.eval("TRIMLEFT(null)"), null);
        validateString(parser.eval("TRIMLEFT('')"), "");
        validateString(parser.eval("TRIMLEFT('  \t  Hello World  \t ')"), "Hello World  \t ");
        validateString(parser.eval("TRIMLEFT('*****Hello World*****', '*')"), "Hello World*****");
    }

    @Test
    public void testTRIMRIGHT() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "TRIMRIGHT");
        
        validateException(parser.eval("TRIMRIGHT()"), "TRIMRIGHT expected 1..2 parameter(s), but got 0", 1, 9);
        validateException(parser.eval("TRIMRIGHT('test', 'test', 1)"), "TRIMRIGHT expected 1..2 parameter(s), but got 3", 1, 9);
        validateException(parser.eval("TRIMRIGHT(123)"), "TRIMRIGHT parameter 1 expected type STRING, but was NUMBER", 1, 9);
        validateException(parser.eval("TRIMRIGHT('123', 2)"), "TRIMRIGHT parameter 2 expected type STRING, but was NUMBER", 1, 9);
        
        validateString(parser.eval("TRIMRIGHT(null)"), null);
        validateString(parser.eval("TRIMRIGHT('')"), "");
        validateString(parser.eval("TRIMRIGHT('  \t  Hello World  \t ')"), "  \t  Hello World");
        validateString(parser.eval("TRIMRIGHT('*****Hello World*****', '*')"), "*****Hello World");
    }

    @Test
    public void testUPPER() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "UPPER");

        validateException(parser.eval("UPPER()"), "UPPER expected 1 parameter(s), but got 0", 1, 5);
        validateException(parser.eval("UPPER('test', 'test')"), "UPPER expected 1 parameter(s), but got 2", 1, 5);
        validateException(parser.eval("UPPER(123)"), "UPPER parameter 1 expected type STRING, but was NUMBER", 1, 5);
        
        validateString(parser.eval("UPPER(null)"), null);
        validateString(parser.eval("UPPER('')"), "");
        validateString(parser.eval("UPPER('HellO, WORLD')"), "HELLO, WORLD");
    }

    @Test
    public void testVAL() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "VAL");

        validateException(parser.eval("VAL()"), "VAL expected 1 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("VAL('test', 'test')"), "VAL expected 1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("VAL(123)"), "VAL parameter 1 expected type STRING, but was NUMBER", 1, 3);
        validateException(parser.eval("VAL('123E')"), "java.lang.NumberFormatException", 1, 3);
        
        validateNumber(parser.eval("VAL(null)"), null);
        validateNumber(parser.eval("VAL('')"), "0");
        validateNumber(parser.eval("VAL('123.45')"), "123.45");
    }
    
}
