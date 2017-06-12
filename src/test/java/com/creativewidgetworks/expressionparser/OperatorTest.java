package com.creativewidgetworks.expressionparser;

import com.creativewidgetworks.expressionparser.Operator;
import com.creativewidgetworks.expressionparser.Token;
import com.creativewidgetworks.expressionparser.TokenType;
import org.junit.Assert;
import org.junit.Test;

public class OperatorTest extends Assert {

    @Test
    public void testFind_case_sensitive() {
        Token token = new Token(TokenType.OPERATOR, "AND");
        assertNotNull(Operator.find(token, true));
        token.setText("and");
        assertNull(Operator.find(token, true));
    }

    @Test
    public void testFind_case_insensitive() {
        Token token = new Token(TokenType.OPERATOR, "AND");
        assertNotNull(Operator.find(token, false));
        token.setText("and");
        assertNotNull(Operator.find(token, false));
    }

    @Test
    public void testOperatorRegex() {
        String regex = Operator.getOperatorRegex();
        assertEquals("or|not|mod|div|and|\\^|\\]|\\[|\\?|\\+|\\*|\\)|\\(|>>|>=|>|==|=|<=|<<|<|:|/|-|,|%|!=|!!|!", regex);
    }

}
