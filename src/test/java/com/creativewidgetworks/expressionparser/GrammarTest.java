package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;

import com.creativewidgetworks.expressionparser.enums.ValueType;

public class GrammarTest extends Assert {
    
    protected void validateArray(Value result, String baseValue, String... arrayValues) {
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

    protected void validateBoolean(Value result, Boolean boolValue) {
        validateNoParserException(result);
        assertEquals("wrong type", ValueType.BOOLEAN, result.getType());
        if (boolValue == null) {
            assertNull("valueNotSet", result.asObject());
        } else {
            assertEquals("wrong value", boolValue, result.asBoolean());
        }
    }    
    
    protected void validateDate(Value result, Date dateValue, int withinMs) {
        validateNoParserException(result);
        assertEquals("wrong type", ValueType.DATE, result.getType());
        if (dateValue == null) {
            assertNull("should be null", result.asDate());
        } else {
            long diff = result.asDate() == null ? Integer.MIN_VALUE : result.asDate().getTime() - dateValue.getTime(); 
            assertTrue("dates should be within " + withinMs + " ms: " + dateValue + ", " + result.asDate(), Math.abs(diff) <= withinMs);    
        }
    }

    protected void validateException(Value result, String msg, int errorRow, int errorCol) {
        assertTrue("expected ParserException", result.asObject() instanceof ParserException);
        ParserException pe = (ParserException)result.asObject();
        assertEquals("wrong msg", msg, pe.getMessage());
        if (errorRow != -1) {
            assertEquals("wrong row", errorRow, pe.getErrorRow());
        }
        if (errorCol != -1) {
            assertEquals("wrong column", errorCol, pe.getErrorColumn());
        }
    }

    protected void validateExceptionThrown(Parser parser, String expression, String expected) {
        try {
            Value result = parser.eval(expression);
            assertNotNull("should have value", result);
            assertEquals("wrong type", ValueType.OBJECT, result.getType());
            assertTrue("wrong type", result.asObject() instanceof ParserException);
            assertNotNull("Should have exception", parser.getLastException());
            assertEquals("wrong msg", expected, parser.getLastException().getMessage());
            
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Uncaught exception");
        }    
    }    

    protected void validateNoParserException(Value result) {
        if (result.getType() == ValueType.OBJECT && result.asObject() instanceof ParserException) {
            fail("Unexpected ParserException: " + ((ParserException)result.asObject()).getMessage());
        }
    }   
    
    protected void validateNumber(Value result, String numericValue) {
        validateNoParserException(result);
        assertEquals("wrong type", ValueType.NUMBER, result.getType());
        assertEquals("wrong value", numericValue == null ? null : new BigDecimal(numericValue), result.asNumber());    
    }

    protected void validateString(Value result, String stringValue) {
        assertEquals("wrong type", ValueType.STRING, result.getType());
        assertEquals("wrong value", stringValue, result.asString());    
    }
    
    protected void validatePattern(Parser parser, String functionName) {
        assertTrue("\"" + functionName.toLowerCase() + "\" should be part of the function matching pattern",
            parser.getGrammar().getPattern_FUNCTION().indexOf(functionName.toLowerCase()) != -1);
    }
    
}
