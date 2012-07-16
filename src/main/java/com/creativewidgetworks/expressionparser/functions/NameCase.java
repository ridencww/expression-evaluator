package com.creativewidgetworks.expressionparser.functions;

import java.util.Stack;

import com.creativewidgetworks.expressionparser.Symbol;
import com.creativewidgetworks.expressionparser.Value;

public class NameCase {

    
    public Value _NAMECASE(Symbol function, Stack<Symbol> stack) {
        String str = stack.pop().asString();
        if (str != null) {
            StringBuilder sb = new StringBuilder();
            boolean uc = true;
            for (int i = 0; i < str.length(); i++) {
                if (uc && !Character.isWhitespace(str.charAt(i))) {
                    sb.append(Character.toUpperCase(str.charAt(i)));
                    uc = false;
                } else {
                    sb.append(Character.toLowerCase(str.charAt(i)));
                    if (Character.isWhitespace(str.charAt(i))) {
                        uc = true;
                    }
                }
            }
            str = sb.toString();
        }
        
        return new Value(function.getText()).setValue(str);
    }
    
}
