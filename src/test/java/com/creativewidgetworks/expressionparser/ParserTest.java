package com.creativewidgetworks.expressionparser;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParserTest extends UnitTestBase {

    private Parser parser;

    @Before
    public void beforeEach() {
        TestFunctions ourFunctions = new TestFunctions();
        parser = new Parser();
        parser.addFunction(new Function("abs", ourFunctions, "_ABS", 1, 1));
        parser.addFunction(new Function("sqrt", ourFunctions, "_SQRT", 1, 1));
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testEval_emptySource() {
       Value result = new Parser().eval(null);
       assertEquals("ERROR: EMPTY EXPRESSION", result.getName());
       assertEquals("", result.asString());

       result = new Parser().eval("");
       assertEquals("ERROR: EMPTY EXPRESSION", result.getName());
       assertEquals("", result.asString());
       assertEquals("name=ERROR: EMPTY EXPRESSION type=UNDEFINED str= num=0", result.toString());

       assertNull(parser.getLastExpression());
       assertNull( parser.getLastException());
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testBadExpressions() throws Exception {
        // mismatched parenthesis
        validateExceptionThrown(parser, "(1 + 2", "Syntax error, missing parenthesis. Expected )", 1, 6);
        validateExceptionThrown(parser, "1 + 2)", "Syntax error, missing parenthesis. Expected (", 1, 6);
        validateExceptionThrown(parser, "(((1) + (2))", "Syntax error, missing parenthesis. Expected )", 1, 12);
        validateExceptionThrown(parser, "((1) + (2)))", "Syntax error, missing parenthesis. Expected (", 1, 12);

        // missing terminals
        validateExceptionThrown(parser, "1 + * 2", "Syntax error", 1, 3);

        // dangling tokens
        validateExceptionThrown(parser, "1 + 2 +", "Syntax error", 1, 7);
        validateExceptionThrown(parser, "(1 == 1) ==", "Syntax error", 1, 10);
        validateExceptionThrown(parser, "1 + 3 4", "Syntax error", 1, 1);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testBasicEval() throws Exception {
        // expression with multiple parenthesis and whitespace
        String EXPRESSION = "( 1 -2) * (3/4)-(  5+6)";
        validateNumericResult(parser, EXPRESSION, "-11.75");

        // precedence
        validateNumericResult(parser, "10 + 20 * 30", "610");
        validateNumericResult(parser, "1 + 2^3", "9");
        validateNumericResult(parser, "6 / 2*3 + 4", "13");
        validateNumericResult(parser, "6^2 / 2*3 + 4", "58");
        validateBooleanResult(parser, "NOT 1 != 1", Boolean.TRUE);

        // parenthesis grouping
        validateNumericResult(parser, "12 / 3 + 1", "5");
        validateNumericResult(parser, "12 / (3 + 1)", "3");
        validateNumericResult(parser, "((15 / 2) + .5) / (1.5 + .5)", "4");
        validateNumericResult(parser, "6 / (2*3) + 4", "5");
        validateNumericResult(parser, "6^2 / (2*3) + 4", "10");
    }

   /*----------------------------------------------------------------------------*/

    @Test
    public void testTypeMismatch() {
        // String concat "+" where lhs and/or rhs is a STRING is acceptable

        validateExceptionThrown(parser, "'abc'*5", "Both values must be numeric: abc 5", 1, 7);
        validateExceptionThrown(parser, "5*'abc'", "Both values must be numeric: 5 abc", 1, 3);
        validateExceptionThrown(parser, "'abc'/5", "Both values must be numeric: abc 5", 1, 7);
        validateExceptionThrown(parser, "5/'abc'", "Both values must be numeric: 5 abc", 1, 3);
        validateExceptionThrown(parser, "'abc'-5", "Both values must be numeric: abc 5", 1, 7);
        validateExceptionThrown(parser, "5-'abc'", "Both values must be numeric: 5 abc", 1, 3);
        validateExceptionThrown(parser, "'abc' DIV 5", "Both values must be numeric: abc 5", 1, 11);
        validateExceptionThrown(parser, "5 DIV 'abc'", "Both values must be numeric: 5 abc", 1, 7);
        validateExceptionThrown(parser, "'abc' MOD 5", "Both values must be numeric: abc 5", 1, 11);
        validateExceptionThrown(parser, "5 MOD 'abc'", "Both values must be numeric: 5 abc", 1, 7);
        validateExceptionThrown(parser, "'abc' ^ 5", "Both values must be numeric: abc 5", 1, 9);
        validateExceptionThrown(parser, "5 ^ 'abc'", "Both values must be numeric: 5 abc", 1, 5);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testBuiltInConstants() {
        validateNumericResult(parser, "PI", "3.141592653589793");
        validateNumericResult(parser, "pi", "3.141592653589793");
        validateNumericResult(parser, "NULL", null);
        validateNumericResult(parser, "null", null);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testBuiltInFunction_CLEARGLOBAL() {
        parser.getGlobalVariables().put("NEWVAR1", new Value("NEWVAR1", "Hello, world"));
        validateBooleanResult(parser, "ClearGlobal('NEWVAR1')", Boolean.TRUE);
        assertNull(parser.getGlobalVariables().get("NEWVAR1"));
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testBuiltInFunction_CLEARGLOBALS() {
        parser.addGlobalVariable("TEMP", new Value("TEMP", "c:\\tmp"));
        validateBooleanResult(parser, "ClearGlobals()", Boolean.TRUE);
        assertEquals("globals cleared", 0, parser.getGlobalVariables().size());
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testBuiltInFunction_GETGLOBAL() {
        parser.getGlobalVariables().put("NEWVAR1", new Value("NEWVAR1", "Hello, world"));
        parser.getGlobalVariables().put("NEWVAR2", new Value("NEWVAR1", BigDecimal.TEN));
        validateStringResult(parser, "GetGlobal('NEWVAR1')", "Hello, world");
        validateNumericResult(parser, "GetGlobal('NEWVAR2')", "10");

        // No exceptions thrown on assignment when checking for null parameters
        validateBooleanResult(parser, "A=GetGlobal('NEWVAR2')", Boolean.TRUE);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testBuiltInFunction_SETGLOBAL() {
        validateExceptionThrown(parser, "SetGlobal()", "SetGlobal expected 2 parameter(s), but got 0", 1, 10 );
        validateExceptionThrown(parser, "SetGlobal(1, 'Hello')", "SetGlobal parameter 1 expected type STRING, but was NUMBER", 1, 10 );

        validateBooleanResult(parser, "SetGlobal('MODE', 'Append')", Boolean.TRUE);
        Value value = parser.getGlobalVariable("MODE");
        assertNotNull(value);
        assertEquals("value", "Append", value.asString());
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testBuiltInFunction_NOW() {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date bod = calendar.getTime();

        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        Date eod = calendar.getTime();

        validateDateResult(parser, "NOW()", now);
        validateDateResult(parser, "NOW(0)", now);
        validateDateResult(parser, "NOW(1)", bod);
        validateDateResult(parser, "NOW(2)", eod);

        validateExceptionThrown(parser, "NOW(A)", "The following parameter(s) cannot be null: 1", 1, 1);
        validateExceptionThrown(parser, "NOW(0,1)", "NOW expected 0..1 parameter(s), but got 2", 1, 4);
        validateExceptionThrown(parser, "NOW(3)", "NOW parameter 1 expected value to be in the range of 0..2, but was 3", 1, 4);

        // No exceptions thrown on assignment when checking for null parameters
        validateBooleanResult(parser, "A=NOW(0)", Boolean.TRUE);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testBuiltInFunction_NOW_timezone() {
        parser.setTimeZone(TimeZone.getTimeZone("UTC"));

        Calendar expected = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        validateDateResult(parser, "NOW()", expected);

        expected = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"));
        try {
            validateDateResult(parser, "NOW()", expected);
            fail("Expected AssertionError as expected timezone is not the same as the parser timezone");
        } catch (AssertionError ae) {
            // Okay to ignore, this was expected
        }
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testBuiltInFunction_PRECISION() {
        int oldPrecision = parser.getPrecision();

        // Default precision is 5 decimal places
        assertEquals("default precision", 5, oldPrecision);
        validateNumericResult(parser, "1/6", "0.16667");

        // Switch to two decimal places
        validateNumericResult(parser, "PRECISION(2)", "5");
        validateNumericResult(parser, "1/6", "0.17");

        // Initialized variable
        validateNumericResult(parser, "A=3;PRECISION(A);1/6", "0.167");

        // No exceptions thrown on assignment when checking for null parameters
        validateBooleanResult(parser, "A=PRECISION(4)", Boolean.TRUE);

        // Restore default
        parser.setPrecision(oldPrecision);
        assertEquals(oldPrecision, parser.getPrecision());
        assertEquals(oldPrecision, Parser.DEFAULT_PRECISION);

        validateExceptionThrown(parser, "PRECISION(B)", "The following parameter(s) cannot be null: 1", 1, 1);
        validateExceptionThrown(parser, "PRECISION()", "PRECISION expected 1 parameter(s), but got 0", 1, 10);
        validateExceptionThrown(parser, "PRECISION('Hello')", "PRECISION parameter 1 expected type NUMBER, but was STRING", 1, 10);
        validateExceptionThrown(parser, "PRECISION(0,1)", "PRECISION expected 1 parameter(s), but got 2", 1, 10);
        validateExceptionThrown(parser, "PRECISION(-1)", "PRECISION parameter 1 expected value to be in the range of 0..100, but was -1", 1, 10);
        validateExceptionThrown(parser, "PRECISION(125)", "PRECISION parameter 1 expected value to be in the range of 0..100, but was 125", 1, 10);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testAddInvalidFunction() throws Exception {
        parser = new Parser();
        try {
            parser.addFunction(new Function("bogus", this, "_BOGUS", 1, 1));
            fail("Expected IllegalStateException");
        } catch (IllegalStateException ex) {
            assertEquals("error message", "Init com.creativewidgetworks.expressionparser.ParserTest NoSuchMethodException _BOGUS", ex.getMessage());
        }
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testMultipleExpressions() throws Exception {
        validateNumericResult(parser, "PRECISION(2);1/6", "0.17");
        validateNumericResult(parser, "foo = PRECISION(2); 1.234", "1.234");
        validateNumericResult(parser, "A=3;B=7;A*B", "21");
        validateStringResult(parser, "A='Test;';B=' me';A+B", "Test; me");
        validateExceptionThrown(parser, "A=3;B=0;A/B", "/ by zero", 1, 2);
    }

   /*----------------------------------------------------------------------------*/

    @Test
    public void testAssignment() throws Exception {
        // number
        validateBooleanResult(parser, "V1=123.45", Boolean.TRUE);
        validateNumericResult(parser, "V1", "123.45");
        validateBooleanResult(parser, "V1=1+5", Boolean.TRUE);
        validateNumericResult(parser, "V1", "6");
        // string
        validateBooleanResult(parser, "V1=\"Ralph\"", Boolean.TRUE);
        validateStringResult(parser, "V1", "Ralph");
        validateBooleanResult(parser, "V1='Jeremy'", Boolean.TRUE);
        validateStringResult(parser, "V1", "Jeremy");
        // -- with embedded quotes
        validateBooleanResult(parser, "V1=\"Robert 'Bob'\"", Boolean.TRUE);
        validateStringResult(parser, "V1", "Robert 'Bob'");
        validateBooleanResult(parser, "V1='Robert \"Bob\"'", Boolean.TRUE);
        validateStringResult(parser, "V1", "Robert \"Bob\"");

        // boolean
        validateBooleanResult(parser, "V1=5>2", Boolean.TRUE);
        validateBooleanResult(parser, "V1", Boolean.TRUE);

        // date
        Date now = new Date();
        validateBooleanResult(parser, "V1=NOW()", Boolean.TRUE);
        validateDateResult(parser, "V1", now);

        // array - one dimension
        FunctionToolbox.register(parser);
        validateBooleanResult(parser, "V1=SPLIT('alpha,beta,gamma')", Boolean.TRUE);
        parser.eval("V1[1] = 'omega'");
        validateArray(parser, "V1","alpha", "alpha", "omega", "gamma");

        // array - two dimension
        parser.eval("DIM(V2,10,5)");
        parser.eval("V2[2,3] = 'epsilon'");
        parser.eval("V2[2,4] = 'omega'");
        validateStringResult(parser, "V2[2,3]", "epsilon");
        validateStringResult(parser, "V2[2,4]", "omega");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testArrayAccess() throws Exception {
        FunctionToolbox.register(parser);
        parser.eval("V1=SPLIT('alpha,beta,gamma')");

        // Bad array index
        validateExceptionThrown(parser, "V1[0-1]", "Index value of -1 is out of the range of 0..2", 1, 5);
        validateExceptionThrown(parser, "V1[3]", "Index value of 3 is out of the range of 0..2", 1, 4);

        validateStringResult(parser, "V1[]", "alpha");
        validateStringResult(parser, "V1[0]", "alpha");
        validateStringResult(parser, "V1[1]", "beta");
        validateStringResult(parser, "V1[2]", "gamma");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testArrayAccess_two_dimension() throws Exception {
        Value row1 = new Value("row-1");
        row1.addValueToArray(new Value("v1", "col-1"));
        row1.addValueToArray(new Value("v2", "col-2"));
        row1.addValueToArray(new Value("v3", "col-3"));

        Value rows = new Value("rows");
        rows.addValueToArray(row1);

        parser.getVariables().put("V1", rows);

        // Bad array index
        validateExceptionThrown(parser, "V1[0,0-1]", "Index value of -1 is out of the range of 0..2", 1, 7);
        validateExceptionThrown(parser, "V1[0,3]", "Index value of 3 is out of the range of 0..2", 1, 6);

        validateStringResult(parser, "V1[0,0]", "col-1");
        validateStringResult(parser, "V1[0,1]", "col-2");
        validateStringResult(parser, "V1[0,2]", "col-3");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testArrayAccess_set_value() throws Exception {
        FunctionToolbox.register(parser);
        parser.eval("V1=SPLIT('alpha,beta,gamma')");
        validateBooleanResult(parser, "V1[0]='omega'", Boolean.TRUE);
        validateStringResult(parser, "V1[0]", "omega");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testConcatenation() throws Exception {
        validateStringResult(parser, "'Hello' + ', ' + 'world'", "Hello, world");
        validateStringResult(parser, "((('Creative') + (' ' + 'Widgets')))", "Creative Widgets");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testAddition() throws Exception {
        parser.setPrecision(15);

        // unary plus for things like plot(+1,-1)
        validateNumericResult(parser, "+2", "2");

        // addition
        validateNumericResult(parser, "1+2", "3");
        validateNumericResult(parser, "1 + 2 + 3", "6");
        validateNumericResult(parser, "1+2+3+5", "11");
        validateNumericResult(parser, "1.23 + 4.56", "5.79");
        validateNumericResult(parser, "5.14159 + PI", "8.283182653589793");

        parser.eval("A=2");
        parser.eval("B=4");
        validateNumericResult(parser, "1 + A + 3 + B", "10");

        validateNumericResult(parser, "1 + ABS(-2) + 3 + SQRT(16)", "10");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testSubtraction() throws Exception {
        parser.setPrecision(15);

        // unary minus (negation)
        validateNumericResult(parser, "-2", "-2");
        validateNumericResult(parser, "--2", "2");
        validateNumericResult(parser, "0--2", "2");

        // subtraction
        validateNumericResult(parser, "1 - 2", "-1");
        validateNumericResult(parser, "10 - 2 - 3", "5");
        validateNumericResult(parser, "-1 - -2 - -3", "4");
        validateNumericResult(parser, "---1----2-----3" , "-2");
        validateNumericResult(parser, "10-1-1-1", "7");
        validateNumericResult(parser, "4.56 - 1.23", "3.33");
        validateNumericResult(parser, "5.14159 - PI", "1.999997346410207");

        parser.eval("A=2");
        parser.eval("B=4");
        validateNumericResult(parser, "1 - A - 3 - B", "-8");

        validateNumericResult(parser, "1 - ABS(-2) - 3 - SQRT(16)", "-8");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testMultiplication() throws Exception {
        validateNumericResult(parser, "5 * 6", "30");
        validateNumericResult(parser, "-5 * 6", "-30");
        validateNumericResult(parser, "5.123456789 * 6.98765432", "35.80094");
        validateNumericResult(parser, "2 * PI", "6.28319");
        validateNumericResult(parser, "1 * ABS(-2) * 3 * SQRT(16)", "24");
        validateNumericResult(parser, "1 * -ABS(-2) * 3 * SQRT(16)", "-24");

        parser.eval("A=ABS(-2)");
        parser.eval("B=SQRT(16)");
        validateNumericResult(parser, "1 * A - 3 * B", "-10");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testPercentage() throws Exception {
        validateNumericResult(parser, "100 * 80%", "80");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testDivision() throws Exception {
        validateExceptionThrown(parser, "23 / (1-1)", "/ by zero", 1, 4);

        // Percentage
        validateNumericResult(parser, "12.5%", ".125");

        // floating point division
        validateNumericResult(parser, "15 / 2", "7.5");
        validateNumericResult(parser, "4 / 5.5", "0.72727");
        validateNumericResult(parser, "4 / 54", "0.07407");

        // integer division
        validateNumericResult(parser, "15 DIV 2", "7");

        // modulus
        validateNumericResult(parser, "17 MOD 7", "3");
        validateNumericResult(parser, "15 MOD 2", "1");
        validateNumericResult(parser, "15 MOD 15", "0");

        validateNumericResult(parser, "10 / ABS(-2) + 3 + SQRT(16)", "12");

        parser.eval("A=ABS(-2)");
        parser.eval("B=SQRT(16)");
        validateNumericResult(parser, "10 / A + 3 + B", "12");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void TestExponentiation() {
        validateNumericResult(parser, "10^1", "10");
        validateNumericResult(parser, "10^2", "100");
        validateNumericResult(parser, "10^3", "1000");
        validateNumericResult(parser, "10^(1+3)", "10000");
        validateNumericResult(parser, "10^-3", "0.001");
        validateNumericResult(parser, "2^64", "18446744073709551616");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testCompareBooleans() throws Exception {
        // Valid operators
        validateBooleanResult(parser, "(1 == 1) == (2 == 2)", Boolean.TRUE);
        validateBooleanResult(parser, "(1 == 1) != (2 == 1)", Boolean.TRUE);
        validateBooleanResult(parser, "(1 == 1) AND (2 == 2)", Boolean.TRUE);
        validateBooleanResult(parser, "(1 == 0) OR (2 == 2)", Boolean.TRUE);

        // Invalid operators
        validateExceptionThrown(parser, "(1 == 0) < (2 == 2)", "Invalid operator for boolean operations: <", 1, 10);
        validateExceptionThrown(parser, "(1 == 0) <= (2 == 2)", "Invalid operator for boolean operations: <=", 1, 10);
        validateExceptionThrown(parser, "(1 == 0) > (2 == 2)", "Invalid operator for boolean operations: >", 1, 10);
        validateExceptionThrown(parser, "(1 == 0) >= (2 == 2)", "Invalid operator for boolean operations: >=", 1, 10);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testCompareNumbers() throws Exception {
        String[] numExpT = new String[]  {"1 < 3", "3 <= 3", "1.0 == 1", "1 != 2", "3 > 2", "3 >= 3"};
        for (String aNumExpT : numExpT) {
            validateBooleanResult(parser, aNumExpT, Boolean.TRUE);
        }

        String[] numExpF = new String[]  {"5 < 4", "4 <= 3", "1 == 2.0", "1 != 1.0", "1 > 2", "2 >= 3"};
        for (String aNumExpF : numExpF) {
            validateBooleanResult(parser, aNumExpF, Boolean.FALSE);
        }

        String[] numExpTA = new String[] {"1 < 10 and 5 > 3", "1 < 10 AND 5 > 2"};
        for (String aNumExpTA : numExpTA) {
            validateBooleanResult(parser, aNumExpTA, Boolean.TRUE);
        }

        String[] numExpTO = new String[] {"1 < 10 or 5 > 3", "1 < 10 OR 5 > 3", "1 < 10 OR 4 > 4", "1 > 4 OR 5 < 6"};
        for (String aNumExpTO : numExpTO) {
            validateBooleanResult(parser, aNumExpTO, Boolean.TRUE);
        }

        String[] numExpFA = new String[] {"1 < 10 AND 5 > 6", "1 < 0 AND 5 > 2"};
        for (String aNumExpFA : numExpFA) {
            validateBooleanResult(parser, aNumExpFA, Boolean.FALSE);
        }

        String[] numExpFO = new String[] {"1 < 0 OR 5 > 6"};
        for (String aNumExpFO : numExpFO) {
            validateBooleanResult(parser, aNumExpFO, Boolean.FALSE);
        }

        // Invalid operators
        validateExceptionThrown(parser, "1 AND 2", "Invalid operator: AND", 1, 7);
        validateExceptionThrown(parser, "1 OR 2", "Invalid operator: OR", 1, 6);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testCompareStrings() throws Exception {
        validateBooleanResult(parser, "\"ABC\" == \"Abc\"", Boolean.FALSE);
        validateBooleanResult(parser, "\"Abc\" == \"Abc\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"Abc\" == \"Abc\" AND \"x\" == \"x\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"Abc\" == \"Abc\" AND \"x\" == \"y\"", Boolean.FALSE);
        validateBooleanResult(parser, "\"ABC\" == \"abc\" OR \"x\" == \"x\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"ABC\" == \"Abc\" OR \"x\" == \"y\"", Boolean.FALSE);
        validateBooleanResult(parser, "\"ABC\" == \"abc\" OR \"x\" == \"x\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"ABC\" == \"Abc\" OR \"x\" == \"y\"", Boolean.FALSE);
        validateBooleanResult(parser, "\"ABC\" != \"Abc\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"ABC\" != \"ABC\"", Boolean.FALSE);
        validateBooleanResult(parser, "\"ABC\" < \"ABD\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"ABC\" < \"abc\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"ABC\" <= \"ABC\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"ABC\" > \"ABD\"", Boolean.FALSE);
        validateBooleanResult(parser, "\"ABD\" > \"ABC\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"abc\" > \"ABC\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"ABC\" >= \"ABC\"", Boolean.TRUE);
        validateBooleanResult(parser, "\"ABC\" > \"ABD\"", Boolean.FALSE);

        // Invalid operators
        validateExceptionThrown(parser, "\"ABC\" AND \"Abc\"", "Invalid operator: AND", 1, 11);
        validateExceptionThrown(parser, "\"ABC\" OR \"Abc\"", "Invalid operator: OR", 1, 10);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testCompareDates() throws Exception {
        // D1=D2 and D3=D4
        Date d1 = new Date(new Date().getTime() - 3600000);
        Date d2 = new Date(d1.getTime());
        Date d3 = new Date(new Date().getTime() + 3600000);
        Date d4 = new Date(d3.getTime());
        parser.getVariables().put("D1", new Value("D1", d1));
        parser.getVariables().put("D2", new Value("D2", d2));
        parser.getVariables().put("D3", new Value("D3", d3));
        parser.getVariables().put("D4", new Value("D4", d4));

        validateBooleanResult(parser, "D1 == D2", Boolean.TRUE);
        validateBooleanResult(parser, "D1 == D3", Boolean.FALSE);
        validateBooleanResult(parser, "D1 != D3", Boolean.TRUE);
        validateBooleanResult(parser, "D1 != D2", Boolean.FALSE);
        validateBooleanResult(parser, "D1 < D3", Boolean.TRUE);
        validateBooleanResult(parser, "D1 < D2", Boolean.FALSE);
        validateBooleanResult(parser, "D1 <= D1", Boolean.TRUE);
        validateBooleanResult(parser, "D3 <= D3", Boolean.TRUE);
        validateBooleanResult(parser, "D3 <= D1", Boolean.FALSE);
        validateBooleanResult(parser, "D3 > D1", Boolean.TRUE);
        validateBooleanResult(parser, "D1 > D3", Boolean.FALSE);
        validateBooleanResult(parser, "D1 >= D1", Boolean.TRUE);
        validateBooleanResult(parser, "D3 >= D3", Boolean.TRUE);
        validateBooleanResult(parser, "D1 >= D3", Boolean.FALSE);
        validateBooleanResult(parser, "D1 == D2 AND D3 == D4", Boolean.TRUE);
        validateBooleanResult(parser, "D1 == D2 AND D3 == D1", Boolean.FALSE);
        validateBooleanResult(parser, "D1 == D3 OR D3 == D4", Boolean.TRUE);
        validateBooleanResult(parser, "D1 == D3 OR D2 == D4", Boolean.FALSE);

        // NOT is a special case where the value is NUMERIC although asBoolean behaves as  expected, so
        // validateBooleanValue() cannot be used here.
        assertTrue("NOT (1!=1)", parser.eval("NOT (1!=1)").asBoolean());
        assertFalse("NOT (1==1)", parser.eval("NOT (1==1)").asBoolean());

        // Invalid operators
        validateExceptionThrown(parser, "D1 AND D4", "Invalid operator: AND", 1, 8);
        validateExceptionThrown(parser, "D1 OR D4", "Invalid operator: OR", 1, 7);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testTernary() throws Exception {
        validateExceptionThrown(parser, "(1==1) ? 'Y'", "Syntax error, ? without a matching :", 1, 8);
        validateExceptionThrown(parser, "(1==1) : 'N'", "Syntax error, : without preceding ?", 1, 8);
        validateExceptionThrown(parser, "1 ? 'Y' : 'N'", "Expected BOOLEAN value, but was NUMBER", 1, 1);
        validateExceptionThrown(parser, "1==1 ? 'Hi : 'Hello'", "Syntax error, bad token", 1, 20);
        validateExceptionThrown(parser, "1==0 ? Hi' : 'Hello'", "Syntax error, bad token", 1, 20);
        validateExceptionThrown(parser, "1==0 ? 'Hi' : 'Hello", "Syntax error, bad token", 1, 15);
        validateExceptionThrown(parser, "1==0 ? 'Hi' : Hello'", "Syntax error, bad token", 1, 20);

        validateStringResult(parser, "(1==1) ? 'Y' : 'N'", "Y");
        validateStringResult(parser, "(1==2) ? 'Y' : 'N'", "N");
        validateStringResult(parser, "(1==1) ? ((2==1) ? 'A' : 'B') : 'N'", "B");
        validateStringResult(parser, "(1==2) ? 'C' : ((2==1) ? 'C' : 'D')", "D");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testTokenCacheValueIsolation() throws Exception {
        parser.setPrecision(15);
        validateNumericResult(parser, "-2", "-2");
        validateNumericResult(parser, "-2", "-2");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testStringWithEscapedCharacters() throws Exception {
        // William 'Bill' Iden
        List<Token> tokens = parser.tokenize("\"William 'Bill' Iden\"", false);
        validateTokensTextOnly(tokens, "William 'Bill' Iden");

        // William "Bill" Iden
        tokens = parser.tokenize("\"William \\\"Bill\\\" Iden\"", false);
        validateTokensTextOnly(tokens, "William \"Bill\" Iden");

        // Clearance 13' 2"
        tokens = parser.tokenize("'Clearance 13\\' 2\\\"'", false);
        validateTokensTextOnly(tokens, "Clearance 13' 2\"");
    }

    /*----------------------------------------------------------------------------*/

    /*
     * It has been reported that Android 9.x/Java 1.7 throws a PatternSyntaxException. A
     * modified RegEx for PROPERTY was provided by samlu and this and the other tests assert
     * that the parser is still working as expected.
     *
     * This test passes without the RegEx under Java 7 on a Windows development machine, so the
     * issue may be related to Android. However, samlu's modified RegEx works on both platforms.
     */
    @Test
    public void testIssue15_PatternSyntaxException() throws Exception {
        validateNumericResult(parser, "4/67", "0.0597");
   }

    /*----------------------------------------------------------------------------*/

    /*
     * eval("SetGlobal('a', 123.45); aa=GetGlobal('a'); aa+1") does not work as
     * expected. Test change by samlu to Parser._GETGLOBAL.
     */
    @Test
    public void testIssue17_ExpressionUsingGlobalVariable() throws Exception {
        validateNumericResult(parser, "SetGlobal('a', 123.45); aa=GetGlobal('a'); aa+1", "124.45");
    }

    /*----------------------------------------------------------------------------*/

    /*
     * strange behavior when setCaseSensitive(true) - reported by samlu
     */
    @Test
    public void testIssue18_ExpressionUsingGlobalVariable() throws Exception {
        Parser parser = new Parser();
        parser.setCaseSensitive(true);
        parser.addVariable("c1", new Value().setValue(true));
        parser.addVariable("c2", new Value().setValue(true));

        validateBooleanResult(parser, "c1 and c2", Boolean.TRUE);
    }

    @Test
    public void testIssue18_AND_treated_like_identifer() throws Exception {
        Parser parser = new Parser();
        parser.setCaseSensitive(true);
        parser.addVariable("c1", new Value().setValue(true));
        parser.addVariable("c2", new Value().setValue(true));

        // "AND" is considered an identifer and not an operator (like A B C)
        validateExceptionThrown(parser, "c1 AND c2", "Syntax error",1, 1);
    }



}
