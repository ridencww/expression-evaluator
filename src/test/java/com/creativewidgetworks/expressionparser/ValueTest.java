package com.creativewidgetworks.expressionparser;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

public class ValueTest extends UnitTestBase {

    @Test
    public void testArray() {
        Value value = new Value("a");
        value.addValueToArray(new Value("b1", Boolean.TRUE));
        value.addValueToArray(new Value("b2", Boolean.FALSE));
        assertEquals("size", 2, value.getArray().size());
        assertEquals("name=b1 type=BOOLEAN (TRUE) str=1 num=1", value.getArray().get(0).toString());
        value.clear();
        assertNull("array discarded", value.getArray());

        value = new Value("a");
        value.addValueToArray(new Value("b1", Boolean.TRUE));
        value.addValueToArray(new Value("b2", Boolean.FALSE));
        assertEquals("size", 2, value.getArray().size());
        assertEquals("name=b2 type=BOOLEAN (FALSE) str=0 num=0", value.getArray().get(1).toString());
        value.unsetArray();
        assertNull("array discarded", value.getArray());
    }

    @Test
    public void testClear() {
        Value value = new Value();
        value.setName("a");
        value.setValue(BigDecimal.TEN);
        assertEquals("name=a type=NUMBER str=10 num=10", value.toString());
        value.clear();
        assertEquals("name=a type=UNDEFINED str= num=0", value.toString());
    }



}
