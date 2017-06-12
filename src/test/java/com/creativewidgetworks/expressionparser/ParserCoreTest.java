package com.creativewidgetworks.expressionparser;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Stack;

public class ParserCoreTest extends UnitTestBase {
    private static final String EXPRESSION = "( 1 -2) * (3/4)-(  5+6)";

    private Parser parser;

    @Before
    public void beforeEach() {
        parser = new Parser();
        Constant.clearConstants();
        Function.clearFunctions();
    }

    /*---------------------------------------------------------------------------------*/

    @SuppressWarnings("unused")
    public Value _ALPHA(Token function, Stack<Token> stack) {
        return new Value();
    }

    @SuppressWarnings("unused")
    public Value _BETA(Token function, Stack<Token> stack) {
        return new Value();
    }

    @Test
    public void testResourcesAvailable() throws Exception {
        assertEquals("wrong msg", null, ParserException.formatMessage(null));
        assertEquals("wrong msg", null, ParserException.formatMessage(null, "Alpha"));
        assertEquals("wrong msg", "missing-resource", ParserException.formatMessage("missing-resource"));
        assertEquals("wrong msg", "missing-resource;Alpha;Beta", ParserException.formatMessage("missing-resource", "Alpha", "Beta"));
        assertEquals("wrong msg", "No errors", ParserException.formatMessage("success"));
        assertEquals("wrong msg", "Syntax error", ParserException.formatMessage("error.syntax"));
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testClearVariables() {
        assertEquals("should be empty", 0, parser.getVariables().size());

        parser.getVariables().put("A", new Value());
        parser.getVariables().put("B", new Value());
        parser.getVariables().put("C", new Value());
        assertEquals("wrong count", 3, parser.getVariables().size());

        parser.clearVariables();
        assertEquals("should be empty", 0, parser.getVariables().size());
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testSetPrecision() {
        assertEquals("default precision", Parser.DEFAULT_PRECISION, parser.getPrecision());

        assertEquals("badly formatted", "0.33333", parser.eval("1/3").asString());
        parser.setPrecision(15);
        assertEquals("badly formatted", "0.333333333333333", parser.eval("1/3").asString());
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testClearCache() throws Exception {
        String EXPRESSION1 = "(((1)-(2)))";
        String EXPRESSION2 = "(((1)+(2)))";

        assertEquals("should be empty", 0, parser.tokenizedExpressions.size());

        parser.eval(EXPRESSION1);
        parser.eval(EXPRESSION1);
        assertEquals("wrong size", 1, parser.tokenizedExpressions.size());

        parser.eval(EXPRESSION2);
        assertEquals("wrong size", 2, parser.tokenizedExpressions.size());

        parser.clearCache();
        assertEquals("should be empty", 0, parser.tokenizedExpressions.size());
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testProperty() throws Exception {
        try {
            System.setProperty("MYTEST_PROPERTY", "Abc123");
            validateStringResult(parser, "${MYMISSING_PROPERTY}", null);
            validateStringResult(parser, "${MYTEST_PROPERTY}", "Abc123");

            // This checks the ENV, but only works under Windows
            String os = System.getProperty("os.name");
            if (os != null && os.startsWith("Windows")) {
                validateStringResult(parser, "${username}", System.getenv("username"));
            }
        } finally {
            System.clearProperty("MYTEST_PROPERTY");
        }
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testInfixToRPN() throws Exception {
        Token[] expected = new Token[] {
                new Token(TokenType.NUMBER,    "1", 1, 3),
                new Token(TokenType.NUMBER,    "2", 1, 6),
                new Token(TokenType.OPERATOR,  "-", 1, 5),
                new Token(TokenType.NUMBER,    "3", 1, 12),
                new Token(TokenType.NUMBER,    "4", 1, 14),
                new Token(TokenType.OPERATOR,  "/", 1, 13),
                new Token(TokenType.OPERATOR,  "*", 1, 9),
                new Token(TokenType.NUMBER,    "5", 1, 20),
                new Token(TokenType.NUMBER,    "6", 1, 22),
                new Token(TokenType.OPERATOR,  "+", 1, 21),
                new Token(TokenType.OPERATOR,  "-", 1, 16),
        };

        List<Token> tokens = Tokenizer.tokenize(EXPRESSION, false, false);
        List<Token> actual = new Parser().infixToRPN(tokens);
        validateTokens(actual, expected);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testRPNtoValue() throws Exception {
        List<Token> tokens1 = Tokenizer.tokenize(EXPRESSION, false, false);
        List<Token> tokens2 = parser.infixToRPN(tokens1);
        Value result = parser.RPNtoValue(tokens2);
        assertEquals("wrong type", ValueType.NUMBER, result.getType());
        assertEquals("wrong value", "-11.75", result.asString());
        assertEquals("wrong value", new BigDecimal("-11.75"), result.asNumber());
    }

    /*----------------------------------------------------------------------------*/

    @Test
   public void testEval_emptySource() {
       Value result = parser.eval("");
       assertEquals("ERROR: EMPTY EXPRESSION", result.getName());
       assertEquals("", result.asString());
   }

   /*----------------------------------------------------------------------------*/

    @Test
    public void testBasicParse() {
        validateNumericResult(parser, "1 + 3", "4");
    }

   /*----------------------------------------------------------------------------*/

    @Test
    public void testMultipleExpressions() throws Exception {
        validateNumericResult(parser, "A=3;B=7;A*B", "21");
        validateStringResult(parser, "A='Test;';B=' me';A+B", "Test; me");
    }

}
