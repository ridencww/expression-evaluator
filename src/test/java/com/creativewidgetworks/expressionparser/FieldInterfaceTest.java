package com.creativewidgetworks.expressionparser;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FieldInterfaceTest extends Assert implements FieldInterface {

    private Parser parser;
    private String lastFieldRequested;

    @Before
    public void beforeEach() {
        parser = new Parser();
        parser.setFieldInterface(this);
        lastFieldRequested = null;
    }

    private String getLastFieldRequested() {
        String result = lastFieldRequested;
        lastFieldRequested = null;
        return result;
    }

    private void validateFieldParsed(String field) {
        parser.eval(field);
        assertEquals(field.substring(1), getLastFieldRequested());
    }

    /*----------------------------------------------------------------------------*/

    @Override
    public Value getField(String name, boolean caseSensitive) {
        lastFieldRequested = name;
        return name.equalsIgnoreCase("not_found") ? null : new Value(name, name + ":123");
    }

    /*----------------------------------------------------------------------------*/

    @Test
    public void testGetField_direct_no_handler() {
        Parser localParser = new Parser();
        assertNull(localParser.getFieldInterface());
        assertNull(localParser.getField("test"));
        assertNull(getLastFieldRequested());
    }

    @Test
    public void testGetField_direct_found() {
        assertEquals("test:123", parser.getField("test").asString());
        assertEquals("test", getLastFieldRequested());
    }

    @Test
    public void testGetField_direct_not_found() {
        assertNull(parser.getField("not_found"));
        assertEquals("not_found", getLastFieldRequested());
    }

    @Test
    public void testGetField_no_handler() {
        Parser localParser = new Parser();
        assertNull(localParser.getFieldInterface());
        assertEquals(ValueType.UNDEFINED, localParser.eval("@test").getType());
        assertNull(getLastFieldRequested());
    }

    @Test
    public void testGetField_found() {
        assertEquals("test:123", parser.eval("@test").asString());
        assertEquals("test", getLastFieldRequested());
    }

    @Test
    public void testGetField_not_found() {
        assertEquals(ValueType.UNDEFINED, parser.eval("@not_found").getType());
        assertEquals("not_found", getLastFieldRequested());
    }

    @Test
    public void testFieldExpressions() {
        validateFieldParsed("@/name");
        validateFieldParsed("@_name");
        validateFieldParsed("@@name");
        validateFieldParsed("@@@name");
        validateFieldParsed("@name_first");
        validateFieldParsed("@name:first");
        validateFieldParsed("@name/first");
    }
}
