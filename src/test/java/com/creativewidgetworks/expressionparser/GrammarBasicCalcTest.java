package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.creativewidgetworks.expressionparser.enums.SymbolType;
import com.creativewidgetworks.expressionparser.enums.ValueType;
import com.creativewidgetworks.util.TestHelper;

public class GrammarBasicCalcTest extends GrammarTest {

    private static String EXPRESSION = "( 1 -2) * (3/4)-(  5+6)";
    
    @Test
    public void testTokenize() throws Exception {
        // Quick smoke test to tokenize "( 1 -2) * (3/4)-(  5+6)"                
        Symbol[] expected = new Symbol[] {
            new Symbol(SymbolType.SEPARATOR, "(", 1, 1),
            new Symbol(SymbolType.NUMBER,    "1", 1, 3),
            new Symbol(SymbolType.OPERATOR,  "-", 1, 5),
            new Symbol(SymbolType.NUMBER,    "2", 1, 6),
            new Symbol(SymbolType.SEPARATOR, ")", 1, 7),
            new Symbol(SymbolType.OPERATOR,  "*", 1, 9),
            new Symbol(SymbolType.SEPARATOR, "(", 1, 11),
            new Symbol(SymbolType.NUMBER,    "3", 1, 12),
            new Symbol(SymbolType.OPERATOR,  "/", 1, 13),
            new Symbol(SymbolType.NUMBER,    "4", 1, 14),
            new Symbol(SymbolType.SEPARATOR, ")", 1, 15),
            new Symbol(SymbolType.OPERATOR,  "-", 1, 16),
            new Symbol(SymbolType.SEPARATOR, "(", 1, 17),
            new Symbol(SymbolType.NUMBER,    "5", 1, 20),
            new Symbol(SymbolType.OPERATOR,  "+", 1, 21),
            new Symbol(SymbolType.NUMBER,    "6", 1, 22),
            new Symbol(SymbolType.SEPARATOR, ")", 1, 23),
        };
        
        Parser parser = new Parser();
        List<Symbol> actual = (List<Symbol>) TestHelper.callMethod(parser, "tokenize", new Class<?>[] {String.class}, EXPRESSION);
        assertEquals("wrong token count", expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals("row " + i + ": type",expected[i].getType(), actual.get(i).getType());
            assertEquals("row " + i + ": text",expected[i].getText(), actual.get(i).getText());
            assertEquals("row " + i + ": row",expected[i].getRow(), actual.get(i).getRow());
            assertEquals("row " + i + ": col",expected[i].getColumn(), actual.get(i).getColumn());
        }
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testInfixToRPN() throws Exception {
        // Quick smoke test converting infix expression (1-2)*(3/4)-(5+6) into RPN                
        Symbol[] expected = new Symbol[] {
            new Symbol(SymbolType.NUMBER,    "1", 1, 3),
            new Symbol(SymbolType.NUMBER,    "2", 1, 6),
            new Symbol(SymbolType.OPERATOR,  "-", 1, 5),
            new Symbol(SymbolType.NUMBER,    "3", 1, 12),
            new Symbol(SymbolType.NUMBER,    "4", 1, 14),
            new Symbol(SymbolType.OPERATOR,  "/", 1, 13),
            new Symbol(SymbolType.OPERATOR,  "*", 1, 9),
            new Symbol(SymbolType.NUMBER,    "5", 1, 20),
            new Symbol(SymbolType.NUMBER,    "6", 1, 22),
            new Symbol(SymbolType.OPERATOR,  "+", 1, 21),
            new Symbol(SymbolType.OPERATOR,  "-", 1, 16),
        };

        Parser parser = new Parser();
        List<Symbol> tokens = (List<Symbol>) TestHelper.callMethod(parser, "tokenize", new Class<?>[] {String.class}, EXPRESSION);
        List<Symbol> actual = (List<Symbol>) TestHelper.callMethod(parser, "infixToRPN", new Class[] {List.class}, tokens);
        assertEquals("wrong token count", expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals("row " + i + ": type",expected[i].getType(), actual.get(i).getType());
            assertEquals("row " + i + ": text",expected[i].getText(), actual.get(i).getText());
            assertEquals("row " + i + ": row",expected[i].getRow(), actual.get(i).getRow());
            assertEquals("row " + i + ": col",expected[i].getColumn(), actual.get(i).getColumn());            
        }
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testRPNtoValue() throws Exception {
        // Quick smoke test converting an expression in RPN to a value                
        Parser parser = new Parser();
        List<Symbol> tokens1 = (List<Symbol>) TestHelper.callMethod(parser, "tokenize", new Class<?>[] {String.class}, EXPRESSION);
        List<Symbol> tokens2 = (List<Symbol>) TestHelper.callMethod(parser, "infixToRPN", new Class[] {List.class}, tokens1);
        Value result = (Value) TestHelper.callMethod(parser, "RPNtoValue", new Class[] {List.class}, tokens2);
        assertEquals("wrong type", ValueType.NUMBER, result.getType());
        assertEquals("wrong value", "-11.75", result.asString());
        assertEquals("wrong value", new BigDecimal("-11.75"), result.asNumber());
    }
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testBasicEval() throws Exception {
        Parser parser = new Parser();
        
        Value result = parser.eval(null);
        assertEquals("wrong type", ValueType.UNDEFINED, result.getType());
        assertEquals("wrong name", "ERROR: EMPTY EXPRESSION", result.getName());

        result = parser.eval("");
        assertEquals("wrong type", ValueType.UNDEFINED, result.getType());
        assertEquals("wrong name", "ERROR: EMPTY EXPRESSION", result.getName());
        
        result = parser.eval(EXPRESSION);
        assertEquals("wrong type", ValueType.NUMBER, result.getType());
        assertEquals("wrong value", "-11.75", result.asString());
        assertEquals("wrong value", new BigDecimal("-11.75"), result.asNumber());        
        
        // parenthesis grouping
        result = parser.eval("12 / 3 + 1");
        assertEquals("bad eval", new BigDecimal(5), result.asNumber());
        result = parser.eval("12 / (3 + 1)");
        assertEquals("bad eval", new BigDecimal(3), result.asNumber());
        result = parser.eval("((15 / 2) + .5) / (1.5 + .5)");
        assertEquals("bad eval", new BigDecimal(4), result.asNumber());
        
    }
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testBadExpressions() throws Exception {
        Parser parser = new Parser();
         
        // mismatched parenthesis
        validateExceptionThrown(parser, "(1 + 2", "Syntax error, missing parenthesis. Expected )");
        validateExceptionThrown(parser, "1 + 2)", "Syntax error, missing parenthesis. Expected (");
        validateExceptionThrown(parser, "(((1) + (2))", "Syntax error, missing parenthesis. Expected )");
        validateExceptionThrown(parser, "((1) + (2)))", "Syntax error, missing parenthesis. Expected (");
        
        // missing terminals
        validateExceptionThrown(parser, "1 + * 2", "Syntax error");
        
        // dangling tokens
        validateExceptionThrown(parser, "1 + 2 +", "Syntax error");
        validateExceptionThrown(parser, "(1 == 1) ==", "Syntax error");
    }    
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testConstants() {
        Value result;
        Parser parser = new Parser();
        
        result = parser.eval("PI");
        assertEquals("bad eval", new BigDecimal("3.141592653589793"), result.asNumber());        

        result = parser.eval("pi");
        assertEquals("bad eval", new BigDecimal("3.141592653589793"), result.asNumber());        

        result = parser.eval("NULL");
        assertEquals("bad eval", null, result.asNumber());        

        result = parser.eval("null");
        assertEquals("bad eval", null, result.asNumber());        
    }
        
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testAssignment() throws Exception {
        Value result;
        Parser parser = new Parser();
        
        // number
        result = parser.eval("V1=123.45");
        assertEquals("Wrong type", ValueType.NUMBER, result.getType());
        assertEquals("Wrong value", new BigDecimal("123.45"), result.asNumber());
        result = parser.eval("V1");
        assertEquals("Wrong type", ValueType.NUMBER, result.getType());
        assertEquals("Wrong value", new BigDecimal("123.45"), result.asNumber());
        
        // string
        result = parser.eval("V1=\"Ralph\"");
        assertEquals("Wrong type", ValueType.STRING, result.getType());
        assertEquals("Wrong value", "Ralph", result.asString());
        result = parser.eval("V1");
        assertEquals("Wrong type", ValueType.STRING, result.getType());
        assertEquals("Wrong value", "Ralph", result.asString());
        // -- single quote variation
        result = parser.eval("V1='Jeremy'");
        assertEquals("Wrong type", ValueType.STRING, result.getType());
        assertEquals("Wrong value", "Jeremy", result.asString());
        result = parser.eval("V1");
        assertEquals("Wrong type", ValueType.STRING, result.getType());
        assertEquals("Wrong value", "Jeremy", result.asString());
        // -- with embedded quotes 
        result = parser.eval("V1=\"Robert 'Bob'\"");
        assertEquals("Wrong type", ValueType.STRING, result.getType());
        assertEquals("Wrong value", "Robert 'Bob'", result.asString());
        result = parser.eval("V1");
        assertEquals("Wrong type", ValueType.STRING, result.getType());
        assertEquals("Wrong value", "Robert 'Bob'", result.asString());        
        result = parser.eval("V1='Robert \"Bob\"'");
        assertEquals("Wrong type", ValueType.STRING, result.getType());
        assertEquals("Wrong value", "Robert \"Bob\"", result.asString());
        result = parser.eval("V1");
        assertEquals("Wrong type", ValueType.STRING, result.getType());
        assertEquals("Wrong value", "Robert \"Bob\"", result.asString());    
        
        // boolean
        result = parser.eval("V1=5>2");
        assertEquals("Wrong type", ValueType.BOOLEAN, result.getType());
        assertTrue("Wrong value", result.asBoolean().booleanValue());        
        result = parser.eval("V1");
        assertEquals("Wrong type", ValueType.BOOLEAN, result.getType());
        assertTrue("Wrong value", result.asBoolean().booleanValue());        
        
        // date
        Date expected = new Date();
        result = parser.eval("V1=NOW()");
        assertEquals("Wrong type", ValueType.DATE, result.getType());
        assertTrue("bad value", Math.abs(expected.getTime() - result.asDate().getTime()) < 1000);
        result = parser.eval("V1");
        assertEquals("Wrong type", ValueType.DATE, result.getType());
        assertTrue("bad value", Math.abs(expected.getTime() - result.asDate().getTime()) < 1000);
    }
    
    /*----------------------------------------------------------------------------*/

    @Test
    public void testConcatenation() throws Exception {
        Value result;
        Parser parser = new Parser();
        
        result = parser.eval("'Hello' + ', ' + 'world'");
        assertEquals("bad eval", "Hello, world", result.asString());     
        
        result = parser.eval("((('Creative') + (' ' + 'Widgets')))");
        assertEquals("bad eval", "Creative Widgets", result.asString());     
    }
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testAddition() throws Exception {
        Value result;
        Parser parser = new Parser();

        // unary plus for things like plot(+1,-1)
        result = parser.eval("+2");
        assertEquals("bad eval", new BigDecimal("2"), result.asNumber());
        
        // addition
        result = parser.eval("1 + 2");
        assertEquals("bad eval", new BigDecimal(3), result.asNumber());
        
        result = parser.eval("1 + 2 + 3");
        assertEquals("bad eval", new BigDecimal(6), result.asNumber());

        result = parser.eval("1+2+3+5");
        assertEquals("bad eval", new BigDecimal(11), result.asNumber());

        result = parser.eval("1.23 + 4.56");
        assertEquals("bad eval", new BigDecimal("5.79"), result.asNumber());

        result = parser.eval("5.14159 + PI");
        assertEquals("bad eval", new BigDecimal("8.283182653589793"), result.asNumber());
        
        parser.eval("A=2");
        parser.eval("B=4");
        result = parser.eval("1 + A + 3 + B");
        assertEquals("bad eval", new BigDecimal(10), result.asNumber());        
        
        result = parser.eval("1 + ABS(-2) + 3 + SQRT(16)");
        assertEquals("bad eval", new BigDecimal(10), result.asNumber());
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testSubtraction() throws Exception {
        Value result;
        Parser parser = new Parser();
        
        // unary minus (negation)
        result = parser.eval("-2");
        assertEquals("bad eval", new BigDecimal("-2"), result.asNumber());
        
        result = parser.eval("--2");
        assertEquals("bad eval", new BigDecimal("2"), result.asNumber());
        
        result = parser.eval("0--2");
        assertEquals("bad eval", new BigDecimal("2"), result.asNumber());
        
        // subtraction
        result = parser.eval("1 - 2");
        assertEquals("bad eval", new BigDecimal(-1), result.asNumber());
        
        result = parser.eval("10 - 2 - 3");
        assertEquals("bad eval", new BigDecimal(5), result.asNumber());
        
        result = parser.eval("-1 - -2 - -3");
        assertEquals("bad eval", new BigDecimal(4), result.asNumber());
        
        result = parser.eval("---1----2-----3");
        assertEquals("bad eval", new BigDecimal(-2), result.asNumber());
        
        result = parser.eval("10-1-1-1");
        assertEquals("bad eval", new BigDecimal(7), result.asNumber());
        
        result = parser.eval("4.56 - 1.23");
        assertEquals("bad eval", new BigDecimal("3.33"), result.asNumber());
        
        result = parser.eval("5.14159 - PI");
        assertEquals("bad eval", new BigDecimal("1.999997346410207"), result.asNumber());
        
        parser.eval("A=2");
        parser.eval("B=4");
        result = parser.eval("1 - A - 3 - B");
        assertEquals("bad eval", new BigDecimal(-8), result.asNumber());        
        
        result = parser.eval("1 - ABS(-2) - 3 - SQRT(16)");
        assertEquals("bad eval", new BigDecimal(-8), result.asNumber());
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testMultiplication() throws Exception {
        Value result;
        Parser parser = new Parser();

        result = parser.eval("5 * 6");
        assertEquals("bad eval", new BigDecimal(30), result.asNumber());

        result = parser.eval("-5 * 6");
        assertEquals("bad eval", new BigDecimal(-30), result.asNumber());
        
        result = parser.eval("5.123456789 * 6.98765432");
        assertEquals("bad eval", new BigDecimal("35.80094"), result.asNumber());

        result = parser.eval("2 * PI");
        assertEquals("bad eval", new BigDecimal("6.28319"), result.asNumber());

        result = parser.eval("1 * ABS(-2) * 3 * SQRT(16)");
        assertEquals("bad eval", new BigDecimal(24), result.asNumber());

        result = parser.eval("1 * -ABS(-2) * 3 * SQRT(16)");
        assertEquals("bad eval", new BigDecimal(-24), result.asNumber());
        
        parser.eval("A=ABS(-2)");
        parser.eval("B=SQRT(16)");
        result = parser.eval("1 * A - 3 * B");
        assertEquals("bad eval", new BigDecimal(-10), result.asNumber());
    }
    
    /*----------------------------------------------------------------------------*/

    @Test
    public void testPercentage() throws Exception {
        Value result;
        Parser parser = new Parser();
        
        result = parser.eval("100 * 80%");
        assertEquals("bad eval", new BigDecimal("80"), result.asNumber());
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testDivision() throws Exception {
        Value result;
        Parser parser = new Parser();

        validateExceptionThrown(parser, "23 / (1-1)", "/ by zero");

        // floating point division
        result = parser.eval("15 / 2");
        assertEquals("bad eval", new BigDecimal("7.5"), result.asNumber());

        result = parser.eval("4 / 5.5");
        assertEquals("bad eval", new BigDecimal("0.72727"), result.asNumber());

        result = parser.eval("4 / 54");
        assertEquals("bad eval", new BigDecimal("0.07407"), result.asNumber());

        // integer division
        result = parser.eval("15 \\ 2");
        assertEquals("bad eval", new BigDecimal("7"), result.asNumber());
        result = parser.eval("15 DIV 2");
        assertEquals("bad eval", new BigDecimal("7"), result.asNumber());

        // modulus
        result = parser.eval("17 MOD 7");
        assertEquals("bad eval", new BigDecimal("3"), result.asNumber());
        result = parser.eval("15 MOD 2");
        assertEquals("bad eval", new BigDecimal("1"), result.asNumber());
        result = parser.eval("15 MOD 15");
        assertEquals("bad eval", new BigDecimal("0"), result.asNumber());     
        
        result = parser.eval("10 / ABS(-2) + 3 + SQRT(16)");
        assertEquals("bad eval", new BigDecimal("12"), result.asNumber());

        parser.eval("A=ABS(-2)");
        parser.eval("B=SQRT(16)");
        result = parser.eval("10 / A + 3 + B");
        assertEquals("bad eval", new BigDecimal("12"), result.asNumber());        
    }    
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void TestExponentiation() throws Exception {
        Value result;
        Parser parser = new Parser();
        
        result = parser.eval("10^1");
        assertEquals("bad eval", new BigDecimal("10"), result.asNumber());

        result = parser.eval("10^3");
        assertEquals("bad eval", new BigDecimal("1000"), result.asNumber());
        
        result = parser.eval("10 ^ 3");
        assertEquals("bad eval", new BigDecimal("1000"), result.asNumber());
        
        result = parser.eval("10^(1+2)");
        assertEquals("bad eval", new BigDecimal("1000"), result.asNumber());
        
        result = parser.eval("10^-3");
        assertEquals("bad eval", new BigDecimal("0.001"), result.asNumber());
        
        result = parser.eval("2^64");
        assertEquals("bad eval", new BigDecimal("18446744073709551616"), result.asNumber());
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testCompareBooleans() throws Exception {
        Parser parser = new Parser();
    
        // Valid operators
        assertTrue(parser.eval("(1 == 1) == (2 == 2)").asBoolean().booleanValue());
        assertTrue(parser.eval("(1 == 1) != (2 == 1)").asBoolean().booleanValue());
        assertTrue(parser.eval("(1 == 1) AND (2 == 2)").asBoolean().booleanValue());
        assertTrue(parser.eval("(1 == 0) OR (2 == 2)").asBoolean().booleanValue());

        // Invalid operators
        validateExceptionThrown(parser, "(1 == 0) < (2 == 2)", "Invalid operator: <");
        validateExceptionThrown(parser, "(1 == 0) <= (2 == 2)", "Invalid operator: <=");
        validateExceptionThrown(parser, "(1 == 0) > (2 == 2)", "Invalid operator: >");
        validateExceptionThrown(parser, "(1 == 0) >= (2 == 2)", "Invalid operator: >=");
    }
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testCompareNumbers() throws Exception {
        Parser parser = new Parser();
        
        String[] numExpT = new String[]  {"1 < 3", "3 <= 3", "1.0 == 1", "1 != 2", "3 > 2", "3 >= 3"};
        String[] numExpF = new String[]  {"5 < 4", "4 <= 3", "1 == 2.0", "1 != 1.0", "1 > 2", "2 >= 3"};
        String[] numExpTA = new String[] {"1 < 10 and 5 > 3", "1 < 10 AND 5 > 2"};
        String[] numExpTO = new String[] {"1 < 10 or 5 > 3", "1 < 10 OR 5 > 3", "1 < 10 OR 4 > 4", "1 > 4 OR 5 < 6"};
        String[] numExpFA = new String[] {"1 < 10 AND 5 > 6", "1 < 0 AND 5 > 2"};
        String[] numExpFO = new String[] {"1 < 0 OR 5 > 6"};

        for (int i = 0; i < numExpT.length; i++) {
            assertTrue(numExpT[i] + " failed", parser.eval(numExpT[i]).asBoolean().booleanValue());
        }
        for (int i = 0; i < numExpF.length; i++) {
            assertFalse(numExpF[i] + " failed", parser.eval(numExpF[i]).asBoolean().booleanValue());
        }
        for (int i = 0; i < numExpTA.length; i++) {
            assertTrue(numExpTA[i] + " failed", parser.eval(numExpTA[i]).asBoolean().booleanValue());
        }
        for (int i = 0; i < numExpTO.length; i++) {
            assertTrue(numExpTO[i] + " failed", parser.eval(numExpTO[i]).asBoolean().booleanValue());
        }
        for (int i = 0; i < numExpFA.length; i++) {
            assertFalse(numExpFA[i] + " failed", parser.eval(numExpFA[i]).asBoolean().booleanValue());
        }
        for (int i = 0; i < numExpFO.length; i++) {
            assertFalse(numExpFO[i] + " failed", parser.eval(numExpFO[i]).asBoolean().booleanValue());
        }

        // Invalid operators
        validateExceptionThrown(parser, "1 AND 2", "Invalid operator: AND");
        validateExceptionThrown(parser, "1 OR 2", "Invalid operator: OR");
    }
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testCompareStrings() throws Exception {
        Parser parser = new Parser();
    
        assertFalse(parser.eval("\"ABC\" == \"Abc\"").asBoolean().booleanValue());
        assertTrue(parser.eval("\"Abc\" == \"Abc\"").asBoolean().booleanValue());
        assertTrue(parser.eval("\"Abc\" == \"Abc\" AND \"x\" == \"x\"").asBoolean().booleanValue());
        assertFalse(parser.eval("\"Abc\" == \"Abc\" AND \"x\" == \"y\"").asBoolean().booleanValue());
        assertTrue(parser.eval("\"ABC\" == \"abc\" OR \"x\" == \"x\"").asBoolean().booleanValue());
        assertFalse(parser.eval("\"ABC\" == \"Abc\" OR \"x\" == \"y\"").asBoolean().booleanValue());

        assertTrue(parser.eval("\"ABC\" != \"Abc\"").asBoolean().booleanValue());
        assertFalse(parser.eval("\"ABC\" != \"ABC\"").asBoolean().booleanValue());

        assertTrue(parser.eval("\"ABC\" < \"ABD\"").asBoolean().booleanValue());
        assertTrue(parser.eval("\"ABC\" < \"abc\"").asBoolean().booleanValue());
        assertTrue(parser.eval("\"ABC\" <= \"ABC\"").asBoolean().booleanValue());
        assertFalse(parser.eval("\"ABC\" > \"ABD\"").asBoolean().booleanValue());

        assertTrue(parser.eval("\"ABD\" > \"ABC\"").asBoolean().booleanValue());
        assertTrue(parser.eval("\"abc\" > \"ABC\"").asBoolean().booleanValue());
        assertTrue(parser.eval("\"ABC\" >= \"ABC\"").asBoolean().booleanValue());
        assertFalse(parser.eval("\"ABC\" > \"ABD\"").asBoolean().booleanValue());

        // Invalid operators
        validateExceptionThrown(parser, "\"ABC\" AND \"Abc\"", "Invalid operator: AND");
        validateExceptionThrown(parser, "\"ABC\" OR \"Abc\"", "Invalid operator: OR");
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testCompareDates() throws Exception {
        Parser parser = new Parser();
  
        // D1=D2 and D3=D4
        Date d1 = new Date(new Date().getTime() - 3600000);
        Date d2 = new Date(d1.getTime());
        Date d3 = new Date(new Date().getTime() + 3600000);
        Date d4 = new Date(d3.getTime());
        
        Value D1 = new Value("D1");
        Value D2 = new Value("D3");
        Value D3 = new Value("D4");
        Value D4 = new Value("D5");
        D1.setValue(d1);
        D2.setValue(d2);
        D3.setValue(d3);
        D4.setValue(d4);
        parser.getVariables().put("D1", D1); 
        parser.getVariables().put("D2", D2); 
        parser.getVariables().put("D3", D3); 
        parser.getVariables().put("D4", D4); 
        
        assertTrue(parser.eval("D1 == D2").asBoolean().booleanValue());
        assertFalse(parser.eval("D1 == D3").asBoolean().booleanValue());

        assertTrue(parser.eval("D1 != D3").asBoolean().booleanValue());
        assertFalse(parser.eval("D1 != D2").asBoolean().booleanValue());

        assertTrue(parser.eval("D1 < D3").asBoolean().booleanValue());
        assertFalse(parser.eval("D1 < D2").asBoolean().booleanValue());
        assertTrue(parser.eval("D1 <= D1").asBoolean().booleanValue());
        assertTrue(parser.eval("D3 <= D3").asBoolean().booleanValue());
        assertFalse(parser.eval("D3 <= D1").asBoolean().booleanValue());

        assertTrue(parser.eval("D3 > D1").asBoolean().booleanValue());
        assertFalse(parser.eval("D1 > D3").asBoolean().booleanValue());
        assertTrue(parser.eval("D1 >= D1").asBoolean().booleanValue());
        assertTrue(parser.eval("D3 >= D3").asBoolean().booleanValue());
        assertFalse(parser.eval("D1 >= D3").asBoolean().booleanValue());

        assertTrue(parser.eval("D1 == D2 AND D3 == D4").asBoolean().booleanValue());
        assertFalse(parser.eval("D1 == D2 AND D3 == D1").asBoolean().booleanValue());

        assertTrue(parser.eval("D1 == D3 OR D3 == D4").asBoolean().booleanValue());
        assertFalse(parser.eval("D1 == D3 OR D2 == D4").asBoolean().booleanValue());

        Value v = parser.eval("NOT (1!=1)");
        assertTrue("NOT (1!=1)", v.asBoolean().booleanValue());

        v = parser.eval("NOT (1==1)");
        assertFalse("NOT (1==1)", v.asBoolean().booleanValue());

        // Invalid operators
        validateExceptionThrown(parser, "D1 AND D4", "Invalid operator: AND");
        validateExceptionThrown(parser, "D1 OR D4", "Invalid operator: OR");
    }
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testTernary() throws Exception {
        Parser parser = new Parser();
        
        validateExceptionThrown(parser, "(1==1) ? 'Y'", "Syntax error, ? without a matching :");
        validateExceptionThrown(parser, "(1==1) : 'N'", "Syntax error, : without preceding ?");
        validateExceptionThrown(parser, "1 ? 'Y' : 'N'", "Expected boolean value");
        
        assertEquals("wrong value", "Y", parser.eval("(1==1) ? 'Y' : 'N'").asString());
        assertEquals("wrong value", "N", parser.eval("(1==2) ? 'Y' : 'N'").asString());
        assertEquals("wrong value", "B", parser.eval("(1==1) ? ((2==1) ? 'A' : 'B') : 'N'").asString());
        assertEquals("wrong value", "D", parser.eval("(1==2) ? 'C' : ((2==1) ? 'C' : 'D')").asString());
    }
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testABS() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());

        validatePattern(parser, "ABS");
        
        parser.eval("A='1.01'");
        
        validateException(parser.eval("ABS()"), "ABS expected 1 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("ABS(1, 2)"), "ABS expected 1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("ABS('123')"), "ABS parameter 1 expected type NUMBER, but was STRING", 1, 3);
        validateException(parser.eval("ABS(A)"), "ABS parameter 1 expected type NUMBER, but was STRING", 1, 3);
        
        validateNumber(parser.eval("ABS(null)"), null);
        validateNumber(parser.eval("ABS(0)"), "0");
        validateNumber(parser.eval("ABS(1.2345)"), "1.2345");
        validateNumber(parser.eval("ABS(-123.45)"), "123.45");
    }

    @Test
    public void testNOW() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "NOW");

        validateException(parser.eval("NOW('test', 'test')"), "NOW expected 0..1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("NOW('test')"), "NOW parameter 1 expected type NUMBER, but was STRING", 1, 3);
        validateException(parser.eval("NOW(5)"), "NOW parameter 1 expected value to be in the range of 0..2, but was 5", 1, 3);
        
        // current time
        Date now = new Date();
        validateDate(parser.eval("NOW()"), now, 100);
        validateDate(parser.eval("NOW(0)"), now, 100);
        
        // beginning of day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        validateDate(parser.eval("NOW(1)"), calendar.getTime(), 0);

        // end of day
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        validateDate(parser.eval("NOW(2)"), calendar.getTime(), 0);
    }
    
    @Test
    public void testSQR() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "SQR");

        validateException(parser.eval("SQR()"), "SQR expected 1 parameter(s), but got 0", 1, 3);
        validateException(parser.eval("SQR(1, 2)"), "SQR expected 1 parameter(s), but got 2", 1, 3);
        validateException(parser.eval("SQR('123')"), "SQR parameter 1 expected type NUMBER, but was STRING", 1, 3);
        
        validateNumber(parser.eval("SQR(null)"), null);
        validateNumber(parser.eval("SQR(0)"), "0");
        validateNumber(parser.eval("SQR(9)"), "81");
        validateNumber(parser.eval("SQR(-2)"), "4");
    }

    @Test
    public void testSQRT() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        
        validatePattern(parser, "SQRT");
        
        validateException(parser.eval("SQRT()"), "SQRT expected 1 parameter(s), but got 0", 1, 4);
        validateException(parser.eval("SQRT(1, 2)"), "SQRT expected 1 parameter(s), but got 2", 1, 4);
        validateException(parser.eval("SQRT('123')"), "SQRT parameter 1 expected type NUMBER, but was STRING", 1, 4);
        validateException(parser.eval("SQRT(-2)"), "Infinite or NaN", 1, 4);
        
        validateNumber(parser.eval("SQRT(null)"), null);
        validateNumber(parser.eval("SQRT(0)"), "0");
        validateNumber(parser.eval("SQRT(81)"), "9");
    }
    
    
}
