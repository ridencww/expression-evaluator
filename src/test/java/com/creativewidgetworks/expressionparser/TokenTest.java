package com.creativewidgetworks.expressionparser;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class TokenTest extends UnitTestBase {

    @Test
    public void testAs() {
        Token token = new Token(TokenType.VALUE, "value", 1, 1);

        token.setValue(new Value("testAs", Boolean.TRUE));
        assertEquals(Boolean.TRUE, token.asBoolean());

        Date date = new Date();
        token.setValue(new Value("testAs", date));
        assertEquals(date, token.asDate());

        token.setValue(new Value("testAs", BigDecimal.TEN));
        assertEquals(BigDecimal.TEN, token.asNumber());

        token.setValue(new Value("testAs", this));
        assertTrue(this.equals(token.asObject()));

        token.setValue(new Value("testAs", "Hello, world"));
        assertEquals("Hello, world", token.asString());
    }

    @Test
    public void testEquals() {
        // Null
        Token token = new Token(TokenType.STRING, "and", 1, 1);
        assertFalse(token.equals(null));

        // Operator, but null text (internal state error, but we test anyway)
        token = new Token(TokenType.OPERATOR, (String)null, 1, 1);
        assertFalse(token.equals(Operator.AND));

        // Operators, but text must match
        token = new Token(TokenType.OPERATOR, "and", 1, 1);
        assertTrue(token.equals(Operator.AND));
        assertFalse(token.equals(Operator.OR));
    }

    @Test
    public void testIsOperator() {
        assertTrue("'and' is an operator",  new Token(TokenType.OPERATOR, "not", 1, 1 ).isOperator());
        assertTrue("'AND' is an operator",  new Token(TokenType.OPERATOR, "AND", 1, 1 ).isOperator());
        assertFalse("'(' is not an operator", new Token(TokenType.OPERATOR, "(", 1, 1 ).isOperator());
        assertFalse("')' is not an operator", new Token(TokenType.OPERATOR, ")", 1, 1 ).isOperator());
    }

}
