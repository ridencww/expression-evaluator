package com.creativewidgetworks.expressionparser;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

public class ParserExceptionTest extends UnitTestBase {

    @Test
    public void testMessageTranslation() {
        IllegalStateException ex = new IllegalStateException("invalid state");

        String msg = ParserException.formatMessage("Test message");
        ParserException pex = new ParserException(msg);
        assertEquals("Test message", pex.getMessage());

        msg = ParserException.formatMessage("Test message");
        pex = new ParserException(msg, ex);
        assertEquals("Test message", pex.getMessage());

        msg = ParserException.formatMessage("error", "Something expected just happened");
        pex = new ParserException(msg);
        assertEquals("Error: Something expected just happened", pex.getMessage());

        msg = ParserException.formatMessage("error", "Something expected just happened");
        pex = new ParserException(msg, ex);
        assertEquals("Error: Something expected just happened", pex.getMessage());
    }

}
