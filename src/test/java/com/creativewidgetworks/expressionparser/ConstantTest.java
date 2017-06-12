package com.creativewidgetworks.expressionparser;

import com.creativewidgetworks.expressionparser.Constant;
import com.creativewidgetworks.expressionparser.TokenType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class ConstantTest extends Assert {

    @Before
    public void beforeEach() {
        Constant.clearConstants();
    }

    @Test
    public void testGetConstantRegex_no_constants() {
        assertEquals("~~no-constants-defined~~", Constant.getConstantRegex());
    }

    @Test
    public void test_getConstantRegex_case_insensitive() {
        Constant.addConstant("e", BigDecimal.valueOf(Math.E), false);
        Constant.addConstant("pi", BigDecimal.valueOf(Math.PI), false);
        assertEquals("PI|E", Constant.getConstantRegex());
    }

    @Test
    public void test_getConstantRegex_case_sensitive() {
        Constant.addConstant("e", BigDecimal.valueOf(Math.E), true);
        Constant.addConstant("pi", BigDecimal.valueOf(Math.PI), true);
        assertEquals("pi|e", Constant.getConstantRegex());
    }

    @Test
    public void test_add_constant_case_insensitive() {
        Constant.addConstant("pi", BigDecimal.valueOf(Math.PI), false);
        BigDecimal pi = Constant.getConstants().get("PI");
        assertNotNull(pi);
        assertEquals(Math.PI, pi.doubleValue(), 0.001);
        assertNull(Constant.getConstants().get("pi"));
    }

    @Test
    public void test_add_constant_case_sensitive() {
        Constant.addConstant("pi", BigDecimal.valueOf(Math.PI), true);
        BigDecimal pi = Constant.getConstants().get("pi");
        assertNotNull(pi);
        assertEquals(Math.PI, pi.doubleValue(), 0.001);
        assertNull(Constant.getConstants().get("PI"));
    }

    @Test
    public void test_add_invalidatss_tokenType_pattern() {
        assertFalse("should not have constant", TokenType.getPattern(false).pattern().contains("PI"));
        Constant.addConstant("pi", BigDecimal.valueOf(Math.PI), false);
        assertTrue("should have constant regex", TokenType.getPattern(false).pattern().contains("PI"));
    }

    @Test
    public void testClearConstants() {
        assertEquals(0, Constant.getConstants().size());
        Constant.addConstant("e", BigDecimal.valueOf(Math.E), false);
        Constant.addConstant("pi", BigDecimal.valueOf(Math.PI), false);
        assertEquals(2, Constant.getConstants().size());
        Constant.clearConstants();
        assertEquals(0, Constant.getConstants().size());
    }

    @Test
    public void test_clear_invalidatss_tokenType_pattern() {
        Constant.addConstant("pi", BigDecimal.valueOf(Math.PI), false);
        assertTrue("should have constant regex", TokenType.getPattern(false).pattern().contains("PI"));
        Constant.clearConstants();
        assertFalse("should not have constant regex", TokenType.getPattern(false).pattern().contains("PI"));
    }

    @Test
    public void testGetConstant_case_insensitive() {
        Constant.addConstant("pi", BigDecimal.valueOf(Math.PI), false);
        assertNotNull(Constant.get("pi", false));
        assertNotNull(Constant.get("PI", false));
    }

    @Test
    public void testGetConstant_case_sensitive() {
        Constant.addConstant("pi", BigDecimal.valueOf(Math.PI), true);
        assertNotNull(Constant.get("pi", true));
        assertNull(Constant.get("PI", true));
    }
}
