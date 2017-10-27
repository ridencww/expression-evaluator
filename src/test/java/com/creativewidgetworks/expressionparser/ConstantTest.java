package com.creativewidgetworks.expressionparser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

public class ConstantTest extends Assert {

    private Parser parser;

    @Before
    public void beforeEach() {
        parser = new Parser();
    }

    @Test
    public void testGetConstantRegex_default_constants() {
        assertEquals("PI|NULL", parser.getConstantRegex());
    }

    @Test
    public void test_getConstantRegex_case_insensitive() {
        parser.addConstant("e", BigDecimal.valueOf(Math.E));
        assertEquals("PI|NULL|E", parser.getConstantRegex());
    }

    @Test
    public void test_getConstantRegex_duplicated() {
        parser.addConstant("e", BigDecimal.valueOf(Math.E));
        parser.addConstant("pi", BigDecimal.valueOf(Math.E));
        parser.addConstant("e", BigDecimal.valueOf(Math.E));
        assertEquals("PI|NULL|E", parser.getConstantRegex());
    }

    @Test
    public void test_getConstantRegex_case_sensitive() {
        parser.setCaseSensitive(true);
        parser.addConstant("e", BigDecimal.valueOf(Math.E));
        assertEquals("e|PI|NULL", parser.getConstantRegex());
    }

    @Test
    public void test_add_constant_case_insensitive() {
        parser.addConstant("e", BigDecimal.valueOf(Math.E));
        BigDecimal e = parser.getConstants().get("E");
        assertNotNull(e);
        assertEquals(Math.E, e.doubleValue(), 0.001);
        assertNull(parser.getConstants().get("e"));
    }

    @Test
    public void test_add_constant_case_sensitive() {
        parser.setCaseSensitive(true);
        parser.addConstant("e", BigDecimal.valueOf(Math.E));
        BigDecimal e = parser.getConstants().get("e");
        assertNotNull(e);
        assertEquals(Math.E, e.doubleValue(), 0.001);
        assertNull(parser.getConstants().get("E"));
    }

    @Test
    public void test_add_invalidates_tokenType_pattern() {
        String pattern = TokenType.getPattern(parser).pattern();
        parser.addConstant("my-constant", BigDecimal.valueOf(Math.E));
        assertTrue("should have constant regex", TokenType.getPattern(parser).pattern().contains("MY-CONSTANT"));
    }

    @Test
    public void testClearConstants() {
        assertEquals(2, parser.getConstants().size());
        parser.addConstant("e", BigDecimal.valueOf(Math.E));
        assertEquals(3, parser.getConstants().size());
        parser.clearConstants();
        assertEquals(2, parser.getConstants().size());
        assertNotNull(parser.getConstant("PI"));
    }

    @Test
    public void test_clear_invalidates_tokenType_pattern() {
        parser.addConstant("my-constant", BigDecimal.valueOf(Math.E));
        assertTrue("should have constant regex", TokenType.getPattern(parser).pattern().contains("MY-CONSTANT"));
        parser.clearConstants();
        assertFalse("should not have constant regex", TokenType.getPattern(parser).pattern().contains("MY-CONSTANT"));
    }

    @Test
    public void testGetConstant_case_insensitive() {
        parser.addConstant("e", BigDecimal.valueOf(Math.E));
        assertNotNull(parser.getConstant("e"));
        assertNotNull(parser.getConstant("E"));
    }

    @Test
    public void testGetConstant_case_sensitive() {
        parser.setCaseSensitive(true);
        parser.addConstant("e", BigDecimal.valueOf(Math.E));
        assertNotNull(parser.getConstant("e"));
        assertNull(parser.getConstant("E"));
    }

    @Test
    public void testGetDefaultConstants() {
        BigDecimal nul = parser.getConstants().get("NULL");
        assertNull(nul);

        BigDecimal pi = parser.getConstants().get("PI");
        assertNotNull(pi);
        assertEquals(Math.PI, pi.doubleValue(), 0.001);
    }
}
