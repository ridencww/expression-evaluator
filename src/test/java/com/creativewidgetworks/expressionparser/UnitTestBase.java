package com.creativewidgetworks.expressionparser;

import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Calendar;
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
            assertEquals(expression, ValueType.ARRAY, result.getType());
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

    private Value validateDateCommon(Parser parser, String expression, Object expected) {
        Value result = parser.eval(expression);
        validateNoParserException(result);
        if (expected == null) {
            assertNull(expression, result.asDate());
        } else {
            assertEquals(expression, ValueType.DATE, result.getType());
            if (result.asDate() == null) {
                fail("Expected date would be parsed from: " + expression);
            }
        }
        return result;
    }

    protected void validateDateResult(Parser parser, String expression, Date expected) {
        Value result = validateDateCommon(parser, expression, expected);
        assertTrue(expression + " -> " + result.asDate() + " = " + expected.toString(), Math.abs(expected.getTime() - result.asDate().getTime()) < 1000);
    }

    protected void validateDateResult(Parser parser, String expression, Calendar expected) {
        Value result = validateDateCommon(parser, expression, expected);

        int mon1 = expected.get(Calendar.MONTH);
        int day1 = expected.get(Calendar.DAY_OF_MONTH);
        int year1 = expected.get(Calendar.YEAR);
        int hour1 = expected.get(Calendar.HOUR_OF_DAY);
        int min1 = expected.get(Calendar.MINUTE);

        Calendar actual = Calendar.getInstance(parser.getTimeZone());
        actual.setTime(result.asDate());
        int mon2 = actual.get(Calendar.MONTH);
        int day2 = actual.get(Calendar.DAY_OF_MONTH);
        int year2 = actual.get(Calendar.YEAR);
        int hour2 = actual.get(Calendar.HOUR_OF_DAY);
        int min2 = actual.get(Calendar.MINUTE);

        assertEquals("month", mon1, mon2);
        assertEquals("day", day1, day2);
        assertEquals("year", year1, year2);
        assertEquals("hour", hour1, hour2);
        assertEquals("minute", min1, min2);
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

    protected void validateTokensTextOnly(List<Token> actual, String... expected) {
        assertEquals("token count", expected.length, actual.size());
        int row = 0;
        for (String tokenText : expected) {
            assertEquals("row " + row + " value", expected[row], actual.get(row).getText());
            row++;
        }
    }
}
