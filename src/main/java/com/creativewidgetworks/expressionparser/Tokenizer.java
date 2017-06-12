package com.creativewidgetworks.expressionparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Tokenizer {

    public static List<Token> tokenize(String input, boolean caseSensitive, boolean wantWhitespace) {
        int offset = 0;
        int row = 1;

        List<Token> tokens = new ArrayList<>();

        Matcher matcher = TokenType.getPattern(caseSensitive).matcher(input);
        while (matcher.find()) {
            if (wantWhitespace || matcher.group(TokenType.WHITESPACE.name()) == null) {
                for (TokenType tokenType : TokenType.values()) {
                    if (matcher.group(tokenType.name()) != null) {
                        String text = tokenType.resolve(matcher.group(tokenType.name()));
                        tokens.add(new Token(tokenType, text, row, matcher.start() + 1 - offset));
                        break;
                    }
                }
            }

            if (matcher.group(TokenType.NEWLINE.name()) != null) {
                offset = matcher.start() + 1;
                row++;
            }
        }

        // Remove the NOMATCH signifying end-of-expression
        if (tokens.size() > 1) {
            int last = tokens.size() - 1;
            Token token = tokens.get(last);
            if (TokenType.NOMATCH.equals(token.getType())) {
                tokens.remove(last);
            }
        }

        return tokens;
    }
}
