package com.creativewidgetworks.expressionparser;

import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class UnitTestBase extends Assert {

    protected void validateArray(Parser parser, String expression, String baseValue, String... arrayValues) {
        Value result = parser.eval(expression);
        validateNoParserException(result);
        if (baseValue == null) {
            assertNull("expected null value", result.asString());
            assertNull("expected null list", result.getArray());
        } else {
            assertEquals("wrong value", baseValue, result.asString());
            if (arrayValues != null && arrayValues.length > 0) {
                assertNotNull("Not an array value", result.getArray());
                assertEquals("Not enough elements in array", arrayValues.length, result.getArray().size());
                for (int i = 0; i < arrayValues.length; i++) {
                    assertEquals("wrong array value at index " + i, arrayValues[i], result.getArray().get(i).asString());
                }
            }
        }
    }

    protected void validateBooleanResult(Parser parser, String expression, Boolean expected) {
        Value result = parser.eval(expression);
        validateNoParserException(result);
        if (expected == null) {
            assertNull(expression, result.asString());
        } else {
            assertEquals(expression, ValueType.BOOLEAN, result.getType());
            assertEquals(expression, expected, result.asBoolean());
        }
    }

    protected void validateDateNotParseable(Parser parser, String expression) {
        Value result = parser.eval(expression);
        validateNoParserException(result);
        assertEquals(expression, ValueType.DATE, result.getType());
        assertNull("Should not parse to date", result.asDate());
    }

    protected void validateDateResult(Parser parser, String expression, Date expected) {
        Value result = parser.eval(expression);
        validateNoParserException(result);
        if (expected == null) {
            assertNull(expression, result.asDate());
        } else {
            assertEquals(expression, ValueType.DATE, result.getType());
            if (result.asDate() == null) {
                fail("Expected date would be parsed from: " + expression);
            }
            assertTrue(expression + " = " + expected.toString(), Math.abs(expected.getTime() - result.asDate().getTime()) < 1000);
        }
    }

    protected void validateExceptionThrown(Parser parser, String expression, String expected, int row, int col) {
        try {
            Value result = parser.eval(expression);
            assertNotNull("should have value", result);
            assertEquals("wrong type", ValueType.OBJECT, result.getType());
            assertTrue("expected ParserException result", result.asObject() instanceof ParserException);
            assertNotNull("Should have exception", parser.getLastException());
            assertEquals("wrong msg", expected, parser.getLastException().getMessage());
            assertEquals("row", row, parser.getLastException().getErrorRow());
            assertEquals("col", col, parser.getLastException().getErrorColumn());
         } catch (Exception ex) {
            ex.printStackTrace();
            fail("Uncaught exception evaluation " + expression);
        }
    }

    protected void validateNoParserException(Value result) {
        if (result.getType() == ValueType.OBJECT && result.asObject() instanceof ParserException) {
            fail("Unexpected ParserException: " + ((ParserException)result.asObject()).getMessage());
        }
    }

    protected void validateNumericResult(Parser parser, String expression, String expected) {
        Value result = parser.eval(expression);
        validateNoParserException(result);
        if (expected == null) {
            assertNull(expression, result.asNumber());
        } else {
            assertEquals(expression, ValueType.NUMBER, result.getType());
            assertEquals(expression, new BigDecimal(expected), result.asNumber());
        }
    }

    protected void validatePattern(Parser parser, String functionName) {
        assertTrue("\"" + functionName + "\" should be part of the function matching pattern",
                parser.getFunctionRegex().indexOf(functionName.toUpperCase()) != -1);
    }

    protected void validateStringResult(Parser parser, String expression, String expected) {
        Value result = parser.eval(expression);
        validateNoParserException(result);
        if (expected == null) {
            assertTrue(expression, ValueType.UNDEFINED.equals(result.getType()) || result.asString() == null);
        } else {
            assertEquals(expression, ValueType.STRING, result.getType());
            assertEquals(expression, expected, result.asString());
        }
    }

    protected void validateTokens(List<Token> actual, Token... expected) {
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
