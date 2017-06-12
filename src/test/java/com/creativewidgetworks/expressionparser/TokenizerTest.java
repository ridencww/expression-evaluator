package com.creativewidgetworks.expressionparser;

import org.junit.Test;

import java.util.List;

public class TokenizerTest extends UnitTestBase {

    @Test
    public void testRowColumn() {
        List<Token> tokens = Tokenizer.tokenize("1 2\n 3  4", false, false);
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
        List<Token> tokens = Tokenizer.tokenize("'hello", false, false);
        validateTokens(tokens,
                new Token(TokenType.NOMATCH, "", 1, 1),
                new Token(TokenType.IDENTIFIER, "hello", 1, 2));


        tokens = Tokenizer.tokenize("\"hello", false, false);
        validateTokens(tokens,
                new Token(TokenType.NOMATCH, "", 1, 1),
                new Token(TokenType.IDENTIFIER, "hello", 1, 2));

        // Empty strings
        tokens = Tokenizer.tokenize("\"\"", false, false);
        validateTokens(tokens, new Token(TokenType.STRING, "", 1, 1));
        // --
        tokens = Tokenizer.tokenize("''", false, false);
        validateTokens(tokens, new Token(TokenType.STRING, "", 1, 1));

        // Whitespace only
        tokens = Tokenizer.tokenize("' ' \" \"", false, false);
        validateTokens(tokens, new Token(TokenType.STRING, " ", 1, 1), new Token(TokenType.STRING, " ", 1, 5));

        tokens = Tokenizer.tokenize("'Hello' \"World\"", false, false);
        validateTokens(tokens, new Token(TokenType.STRING, "Hello", 1, 1), new Token(TokenType.STRING, "World", 1, 9));
    }

    @Test
    public void testWhitespace() {
        List<Token> tokens = Tokenizer.tokenize("1 2", false, false);
        validateTokens(tokens,
                new Token(TokenType.NUMBER, "1", 1, 1),
                new Token(TokenType.NUMBER, "2", 1, 3));

        tokens = Tokenizer.tokenize("1 2", false, true);
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
        validateTokens(Tokenizer.tokenize(EXPRESSION, false, false), expected);
    }
}
