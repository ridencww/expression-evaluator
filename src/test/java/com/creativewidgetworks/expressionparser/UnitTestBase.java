package com.creativewidgetworks.expressionparser;

import com.creativewidgetworks.expressionparser.*;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.List;

public class UnitTestBase extends Assert {

    void validateExceptionThrown(Parser parser, String expression, String expected) {
        try {
            Value result = parser.eval(expression);
            assertNotNull("should have value", result);
            assertEquals("wrong type", ValueType.OBJECT, result.getType());
            assertTrue("expected ParserException result", result.asObject() instanceof ParserException);
            assertNotNull("Should have exception", parser.getLastException());
            assertEquals("wrong msg", expected, parser.getLastException().getMessage());
         } catch (Exception ex) {
            ex.printStackTrace();
            fail("Uncaught exception evaluation " + expression);
        }
    }

    void validateNumericResult(Parser parser, String expression, String expected) {
        Value result = parser.eval(expression);
        if (expected == null) {
            assertNull(expression, result.asNumber());
        } else {
            assertEquals(expression, ValueType.NUMBER, result.getType());
            assertEquals(expression, new BigDecimal(expected), result.asNumber());
        }
    }

    void validateStringResult(Parser parser, String expression, String expected) {
        Value result = parser.eval(expression);
        if (expected == null) {
            assertEquals(expression, "", result.asString());
        } else {
            assertEquals(expression, ValueType.STRING, result.getType());
            assertEquals(expression, expected, result.asString());
        }
    }

    void validateBooleanResult(Parser parser, String expression, Boolean expected) {
        Value result = parser.eval(expression);
        if (expected == null) {
            assertNull(expression, result.asString());
        } else {
            assertEquals(expression, ValueType.BOOLEAN, result.getType());
            assertEquals(expression, expected, result.asBoolean());
        }
    }

    void validateDateResult(Parser parser, String expression, String expected) {
        Value result = parser.eval(expression);
        if (expected == null) {
            assertNull(expression, result.asDate());
        } else {
            long time = Long.valueOf(expected);
            assertEquals(expression, ValueType.DATE, result.getType());
            assertTrue(expression, Math.abs(time - result.asDate().getTime()) < 1000);
        }
    }

    void validateTokens(List<Token> actual, Token... expected) {
        assertEquals("token count", expected.length, actual.size());
        int row = 0;
        for (Token token : expected) {
            assertEquals("row " + row + " type", expected[row].getType(), actual.get(row).getType());
            assertEquals("row " + row + " value", expected[row].getText(), actual.get(row).getText());
            assertEquals("row " + row + " row", expected[row].getRow(), actual.get(row).getRow());
            assertEquals("row " + row + " col", expected[row].getColumn(), actual.get(row).getColumn());
            row++;
        }
    }

}
