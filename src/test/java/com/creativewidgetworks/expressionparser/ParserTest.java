package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.creativewidgetworks.util.TestHelper;

public class ParserTest extends GrammarTest {

    @Test
    public void testResourcesAvailable() throws Exception {
        assertEquals("wrong msg", null, ParserException.formatMessage(null));
        assertEquals("wrong msg", null, ParserException.formatMessage((String)null, "Alpha"));
        assertEquals("wrong msg", "missing-resource", ParserException.formatMessage("missing-resource"));
        assertEquals("wrong msg", "missing-resource;Alpha;Beta", ParserException.formatMessage("missing-resource", "Alpha", "Beta"));
        assertEquals("wrong msg", "No errors", ParserException.formatMessage("success"));
        assertEquals("wrong msg", "Syntax error", ParserException.formatMessage("error.syntax"));
    }     
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testClearVariables() {
        Parser parser = new Parser();
        assertEquals("should be empty", 0, parser.getVariables().size());
        
        parser.getVariables().put("A", new Value());
        parser.getVariables().put("B", new Value());
        parser.getVariables().put("C", new Value());
        assertEquals("wrong count", 3, parser.getVariables().size());
        
        parser.clearVariables();
        assertEquals("should be empty", 0, parser.getVariables().size());
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testSetPrecision() {
        Parser parser = new Parser();
        assertEquals("default precision", Grammar.DEFAULT_PRECISION, parser.getGrammar().getPrecision());
        
        assertEquals("badly formatted", "0.33333", parser.eval("1/3").asString());
        parser.getGrammar().setPrecision(15);
        assertEquals("badly formatted", "0.333333333333333", parser.eval("1/3").asString());
    }

    /*----------------------------------------------------------------------------*/
   
    @Test
    public void testClearCache() throws Exception {
        String EXPRESSION1 = "(((1)-(2)))";
        String EXPRESSION2 = "(((1)+(2)))";
        
        Parser parser = new Parser();
        Map<String, List<Symbol>> cache = (Map<String, List<Symbol>>)TestHelper.getMember(parser, "tokenizedExpressions"); 
        assertEquals("should be empty", 0, cache.size());
        parser.eval(EXPRESSION1);
        parser.eval(EXPRESSION1);
        assertEquals("wrong size", 1, cache.size());
        parser.eval(EXPRESSION2);
        assertEquals("wrong size", 2, cache.size());
        parser.clearCache();
        assertEquals("should be empty", 0, cache.size());
    }

    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testMultipleExpressions() throws Exception {
        Value result = null;
        Parser parser = new Parser();

        result = parser.eval("A=3;B=7;A*B");
        assertEquals("wrong eval", new BigDecimal(21), result.asNumber());
        
        result = parser.eval("A='Test;';B=' me';A+B");
        assertEquals("wrong eval", "Test; me", result.asString());
    }
    
    /*----------------------------------------------------------------------------*/
    
    @Test
    public void testProperty() throws Exception {
        Parser parser = new Parser(new GrammarExtendedCalc());
        try {
            System.setProperty("MYTEST_PROPERTY", "Abc123");
            assertEquals("wrong value", "", parser.eval("${MYMISSING_PROPERTY}").asString());
            assertEquals("wrong value", System.getProperty("MYTEST_PROPERTY"), parser.eval("${MYTEST_PROPERTY}").asString());
            assertEquals("wrong value", System.getenv("username"), parser.eval("${username}").asString());
        } finally {
            System.clearProperty("MYTEST_PROPERTY");
        }
    }

}
