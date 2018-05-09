package com.creativewidgetworks.expressionparser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Stack;

public class FunctionTest extends UnitTestBase {

    private Parser parser;

    @Before
    public void beforeEach() {
        parser = new Parser();
    }

    /*---------------------------------------------------------------------------------*/

    // These are required to exercise the function tests. Do not remove.

    @SuppressWarnings("unused")
    public Value _ALPHA(Token function, Stack<Token> stack) {
        return new Value();
    }

    @SuppressWarnings("unused")
    public Value _BETA(Token function, Stack<Token> stack) {
        return new Value();
    }

   /*---------------------------------------------------------------------------------*/

    @Test
    public void testGetFunctionRegex_no_functions() {
        assertEquals("setGlobal|precision|now|getGlobal|dim|clearGlobals|clearGlobal", parser.getFunctionRegex());
    }

    @Test
    public void testGetFunctionRegEx_case_insensitive() {
        parser.addFunction(new Function("alpha", this, "_ALPHA", 0, 0));
        parser.addFunction(new Function("beta", this, "_BETA", 0, 0));
        assertEquals("setGlobal|precision|now|getGlobal|dim|clearGlobals|clearGlobal|beta|alpha", parser.getFunctionRegex());
    }

    @Test
    public void testGetFunctionRegEx_case_sensitive() {
        parser.addFunction(new Function("alpha", this, "_ALPHA", 0, 0));
        parser.addFunction(new Function("beta", this, "_BETA", 0, 0));
        assertEquals("setGlobal|precision|now|getGlobal|dim|clearGlobals|clearGlobal|beta|alpha", parser.getFunctionRegex());
    }

    @Test
    public void test_add_function_case_sensitive() {
        parser.setCaseSensitive(true);
        parser.addFunction(new Function("alpha", this, "_ALPHA", 0, 0));
        assertNull(parser.getFunctions().get("ALPHA"));
        assertNotNull(parser.getFunctions().get("alpha"));
    }

   @Test
   public void test_add_function_case_insensitive() {
       parser.addFunction(new Function("alpha", this, "_ALPHA", 0, 0));
       assertNotNull(parser.getFunctions().get("ALPHA"));
       assertNull(parser.getFunctions().get("alpha"));
   }

    @Test
    public void test_add_invalidatss_tokenType_pattern() {
        assertFalse("should not have function regex", TokenType.getPattern(parser).pattern().contains("testme"));
        parser.addFunction(new Function("testme", this, "_ALPHA", 0, 0));
        assertTrue("should have function regex", TokenType.getPattern(parser).pattern().contains("testme"));
    }

    @Test
    public void testClearFunctions() {
        assertEquals(7, parser.getFunctions().size());
        parser.addFunction(new Function("alpha", this, "_ALPHA", 0, 0));
        parser.addFunction(new Function("beta", this, "_BETA", 0, 0));
        assertEquals(9, parser.getFunctions().size());
        parser.clearFunctions();
        assertEquals(7, parser.getFunctions().size());
    }

    @Test
    public void test_clear_invalidatss_tokenType_pattern() {
        parser.addFunction(new Function("testme", this, "_ALPHA", 0, 0));
        assertTrue("should have function regex", TokenType.getPattern(parser).pattern().contains("testme"));
        parser.clearFunctions();
        assertFalse("should not have function regex", TokenType.getPattern(parser).pattern().contains("testme"));
    }

    @Test
    public void testGetFunction_case_sensitive() {
        parser.setCaseSensitive(true);
        parser.addFunction(new Function("alpha", this, "_ALPHA", 0, 0));
        assertNotNull(parser.getFunction("alpha"));
        assertNull(parser.getFunction("ALPHA"));
    }

    @Test
    public void testGetFunction_case_insensitive() {
        parser.addFunction(new Function("alpha", this, "_ALPHA", 0, 0));
        assertNotNull(parser.getFunction("alpha"));
        assertNotNull(parser.getFunction("ALPHA"));
    }

    @Test
    public void testUnusedTenaryPathParametersNotChecked() throws Exception {
        FunctionToolbox.register(parser);
        validateStringResult(parser, "ISBLANK(null) ? 'Okay' : DATEFORMAT('yyyyMMdd', null)", "Okay");
        validateStringResult(parser, "ISBLANK('X') ? DATEFORMAT('yyyyMMdd', null) : 'Okay'", "Okay");
        validateExceptionThrown(parser, "ISBLANK(null) ? DATEFORMAT('yyyyMMdd', null) : 'Okay'",
            "The following parameter(s) cannot be null: 2", 1, 17);
        validateExceptionThrown(parser, "ISBLANK('X') ? 'Okay' : DATEFORMAT('yyyyMMdd', null)",
            "The following parameter(s) cannot be null: 2", 1, 25);
    }
}
