package com.creativewidgetworks.expressionparser;

import com.creativewidgetworks.expressionparser.Function;
import com.creativewidgetworks.expressionparser.Token;
import com.creativewidgetworks.expressionparser.TokenType;
import com.creativewidgetworks.expressionparser.Value;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Stack;

public class FunctionTest extends Assert {

    @Before
    public void beforeEach() {
        Function.clearFunctions();
    }

    /*---------------------------------------------------------------------------------*/

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
        assertEquals("~~no-functions-defined~~", Function.getFunctionRegex());
    }

    @Test
    public void testGetFunctionRegEx_case_insensitive() {
        Function.addFunction(new Function("alpha", this, "_ALPHA", 0, 0), false);
        Function.addFunction(new Function("beta", this, "_BETA", 0, 0), false);
        assertEquals("beta|alpha", Function.getFunctionRegex());
    }

    @Test
    public void testGetFunctionRegEx_case_sensitive() {
        Function.addFunction(new Function("alpha", this, "_ALPHA", 0, 0), true);
        Function.addFunction(new Function("beta", this, "_BETA", 0, 0), true);
        assertEquals("beta|alpha", Function.getFunctionRegex());
    }

    @Test
    public void test_add_function_case_sensitive() {
        Function.addFunction(new Function("alpha", this, "_ALPHA", 0, 0), true);
        assertNull(Function.getFunctions().get("ALPHA"));
        assertNotNull(Function.getFunctions().get("alpha"));
    }

   @Test
   public void test_add_function_case_insensitive() {
       Function.addFunction(new Function("alpha", this, "_ALPHA", 0, 0), false);
       assertNotNull(Function.getFunctions().get("ALPHA"));
       assertNull(Function.getFunctions().get("alpha"));
   }

    @Test
    public void test_add_invalidatss_tokenType_pattern() {
        assertFalse("should not have function regex", TokenType.getPattern(false).pattern().contains("testme"));
        Function.addFunction(new Function("testme", this, "_ALPHA", 0, 0), false);
        assertTrue("should have function regex", TokenType.getPattern(false).pattern().contains("testme"));
    }

    @Test
    public void testClearFunctions() {
        assertEquals(0, Function.getFunctions().size());
        Function.addFunction(new Function("alpha", this, "_ALPHA", 0, 0), false);
        Function.addFunction(new Function("beta", this, "_BETA", 0, 0), false);
        assertEquals(2, Function.getFunctions().size());
        Function.clearFunctions();
        assertEquals(0, Function.getFunctions().size());
    }

    @Test
    public void test_clear_invalidatss_tokenType_pattern() {
        Function.addFunction(new Function("testme", this, "_ALPHA", 0, 0), false);
        assertTrue("should have function regex", TokenType.getPattern(false).pattern().contains("testme"));
        Function.clearFunctions();
        assertFalse("should not have function regex", TokenType.getPattern(false).pattern().contains("testme"));
    }

    @Test
    public void testGetFunction_case_sensitive() {
        Function.addFunction(new Function("alpha", this, "_ALPHA", 0, 0), true);
        assertNotNull(Function.get("alpha", true));
        assertNull(Function.get("ALPHA", true));
    }

    @Test
    public void testGetFunction_case_insensitive() {
        Function.addFunction(new Function("alpha", this, "_ALPHA", 0, 0), false);
        assertNotNull(Function.get("alpha", false));
        assertNotNull(Function.get("ALPHA", false));
    }
}
