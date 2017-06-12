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
    }

    /*---------------------------------------------------------------------------------*/

    // These are required to exercise the function tests. Do not remove.

    @SuppressWarnings("unused")
    public Value _ALPHA(Token function, Stack<Token> stack) {
        return new Value();
    }

    @SuppressWarnings("unused")
    public Value _BETA(Token function, Stack<Token> stack) {
        return new Value();
    }

    /*---------------------------------------------------------------------------------*/

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

        int oldPrecision = parser.setPrecision(15);
        assertEquals("old precision", Parser.DEFAULT_PRECISION, oldPrecision);
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
    public void testRowColumn() {
        List<Token> tokens = parser.tokenize("1 2\n 3  4", false);
        validateTokens(tokens,
                new Token(TokenType.NUMBER, "1", 1, 1),
                new Token(TokenType.NUMBER, "2", 1, 3),
                new Token(TokenType.NEWLINE, "\n", 1, 4),
                new Token(TokenType.NUMBER, "3", 2, 2),
                new Token(TokenType.NUMBER, "4", 2, 5));
    }

    @Test
    public void testString() {
        // Unclosed quotes
        List<Token> tokens = parser.tokenize("'hello", false);
        validateTokens(tokens,
                new Token(TokenType.NOMATCH, "", 1, 1),
                new Token(TokenType.IDENTIFIER, "hello", 1, 2));


        tokens = parser.tokenize("\"hello", false);
        validateTokens(tokens,
                new Token(TokenType.NOMATCH, "", 1, 1),
                new Token(TokenType.IDENTIFIER, "hello", 1, 2));

        // Empty strings
        tokens = parser.tokenize("\"\"", false);
        validateTokens(tokens, new Token(TokenType.STRING, "", 1, 1));
        // --
        tokens = parser.tokenize("''", false);
        validateTokens(tokens, new Token(TokenType.STRING, "", 1, 1));

        // Whitespace only
        tokens = parser.tokenize("' ' \" \"", false);
        validateTokens(tokens, new Token(TokenType.STRING, " ", 1, 1), new Token(TokenType.STRING, " ", 1, 5));

        tokens = parser.tokenize("'Hello' \"World\"", false);
        validateTokens(tokens, new Token(TokenType.STRING, "Hello", 1, 1), new Token(TokenType.STRING, "World", 1, 9));
    }

    @Test
    public void testWhitespace() {
        List<Token> tokens = parser.tokenize("1 2", false);
        validateTokens(tokens,
                new Token(TokenType.NUMBER, "1", 1, 1),
                new Token(TokenType.NUMBER, "2", 1, 3));

        tokens = parser.tokenize("1 2", true);
        validateTokens(tokens,
                new Token(TokenType.NUMBER, "1", 1, 1),
                new Token(TokenType.WHITESPACE, " ", 1, 2),
                new Token(TokenType.NUMBER, "2", 1, 3));
    }

    @Test
    public void testTokenize() throws Exception {
        Token[] expected = new Token[] {
                new Token(TokenType.OPERATOR, "(", 1, 1),
                new Token(TokenType.NUMBER,    "1", 1, 3),
                new Token(TokenType.OPERATOR,  "-", 1, 5),
                new Token(TokenType.NUMBER,    "2", 1, 6),
                new Token(TokenType.OPERATOR, ")", 1, 7),
                new Token(TokenType.OPERATOR,  "*", 1, 9),
                new Token(TokenType.OPERATOR, "(", 1, 11),
                new Token(TokenType.NUMBER,    "3", 1, 12),
                new Token(TokenType.OPERATOR,  "/", 1, 13),
                new Token(TokenType.NUMBER,    "4", 1, 14),
                new Token(TokenType.OPERATOR, ")", 1, 15),
                new Token(TokenType.OPERATOR,  "-", 1, 16),
                new Token(TokenType.OPERATOR, "(", 1, 17),
                new Token(TokenType.NUMBER,    "5", 1, 20),
                new Token(TokenType.OPERATOR,  "+", 1, 21),
                new Token(TokenType.NUMBER,    "6", 1, 22),
                new Token(TokenType.OPERATOR, ")", 1, 23),
        };

        String EXPRESSION = "( 1 -2) * (3/4)-(  5+6)";
        validateTokens(parser.tokenize(EXPRESSION, false), expected);
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

        List<Token> tokens = parser.tokenize(EXPRESSION, false);
        List<Token> actual = new Parser().infixToRPN(tokens);
        validateTokens(actual, expected);
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testRPNtoValue() throws Exception {
        List<Token> tokens1 = parser.tokenize(EXPRESSION, false);
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
