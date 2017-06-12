package com.creativewidgetworks.expressionparser;

import java.math.BigDecimal;
import java.util.Date;

public interface FieldInterface {
    public Value getField(String name, boolean caseSensitive);
}
