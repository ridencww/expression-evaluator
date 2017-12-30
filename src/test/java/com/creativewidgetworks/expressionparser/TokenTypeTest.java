package com.creativewidgetworks.expressionparser;

import org.junit.Test;

public class TokenTypeTest extends UnitTestBase {

    @Test
    public void testUnescapeString() {
        String[][] toTest = {
            {null, null}, // null
            {"", ""}, // empty string
            {"\\", "\\\\"}, // backslash
            {"\'", "\\\'"}, // single quote
            {"\"", "\\\""}, // double quote
            {"\b", "\\b"}, // backspace quote
            {"\f", "\\f"}, // formfeed
            {"\n", "\\n"}, // newline
            {"\r", "\\r"}, // carriage return
            {"\t", "\\t"}, // tab
            {"A", "\\101"}, // octal
            {"B", "\\u0042"}, // Unicode
            {"u", "\\u"}, // Unicode
            {"u004", "\\u004"}, // Unicode - invalid length, too short
            {"", "\\u0HI0"}, // Unicode - invalid characters
        };

        for (String[] row : toTest) {
            assertEquals(row[0], TokenType.unescapeString(row[1]));
        }
    }

}
