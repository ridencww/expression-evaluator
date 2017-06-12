package com.creativewidgetworks.expressionparser;

import com.creativewidgetworks.expressionparser.ParserException;
import com.creativewidgetworks.expressionparser.Token;
import com.creativewidgetworks.expressionparser.Value;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

@SuppressWarnings("unused")
public class TestFunctions {
    /*
     * Returns the absolute value of a number
     * abs(0) -> 0
     * abs(123.45) -> 123.45
     * abs(-123.45) -> 123.45
     * abs("kdkdkd") -> expected number exception
     */
    public Value _ABS(Token function, Stack<Token> stack) {
        BigDecimal bd = stack.pop().asNumber();
        return new Value(function.getText()).setValue(bd == null ? null : bd.abs());
    }

    /*
     * Returns the current date and time
     * parameter_1: (no parameter = actual time)
     *              0 = actual time
     *              1 = beginning of today
     *              2 = end of today
     * date() ->   2009-08-31 13:32:02
     * date(1) ->  2009-08-31 00:00:00
     * date(2) ->  2009-08-31 23:59:59
     * date("kdkdkd") -> expected number exception
     * 
     * This function exists in the basic calc grammar because the ParserTest needs 
     * to be sure that it can test Values that are dates
     * 
     */
    public Value _NOW(Token function, Stack<Token> stack) throws ParserException {
        Calendar calendar = Calendar.getInstance();
        if (function.getArgc() > 0) {
            int mode = stack.pop().asNumber().intValue();
            switch (mode) {
                case 0:
                    break; // current time

                case 1:    // beginning of day
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                    break;

                case 2:    // end of day
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 0);
                    break;

                default:
                    String msg = ParserException.formatMessage("error.function_value_out_of_range",
                            function.getText(), "1", "0", "2", String.valueOf(mode));
                    throw new ParserException(msg);
            }
        }

        return new Value(function.getText()).setValue(new Date(calendar.getTimeInMillis()));
    }

    /*
     * Returns the square root of a number
     * sqrt(25) -> 5
     * sqrt(-2) -> Infinity or NAN exception
     * sqrt("dd") -> expected number exception
     */
    public Value _SQRT(Token function, Stack<Token> stack) {
        BigDecimal bd = stack.pop().asNumber();
        bd = bd == null ? null : new BigDecimal(Math.sqrt(bd.doubleValue()));
        return new Value(function.getText()).setValue(bd);
    }
}
