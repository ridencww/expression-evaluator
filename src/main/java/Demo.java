import java.util.List;

import com.creativewidgetworks.expressionparser.Grammar;
import com.creativewidgetworks.expressionparser.GrammarExtendedCalc;
import com.creativewidgetworks.expressionparser.Parser;
import com.creativewidgetworks.expressionparser.ParserException;
import com.creativewidgetworks.expressionparser.Symbol;
import com.creativewidgetworks.expressionparser.Value;

public class Demo {

    /* The only reason that this program is using DemoParser is to expose the tokenize
     * and RPN methods which normally aren't called directly outside of a demo like this.
     */
    public class DemoParser extends Parser {
        public DemoParser(Grammar grammar) {
            super(grammar);
        }

        @Override
        public List<Symbol> tokenize(String source) {
            return super.tokenize(source);
        }

        @Override
        public List<Symbol> infixToRPN(List<Symbol> inputTokens) throws ParserException {
            return super.infixToRPN(inputTokens);
        }

        @Override
        public Value RPNtoValue(List<Symbol> tokens) throws ParserException {
            return super.RPNtoValue(tokens);
        }
    }
    
    private static void usage() {
        System.out.println("usage: Eval expression [-verbose]");
        System.exit(0);
    }
    
    public static void main(String[] args) throws ParserException {
        boolean verbose = false;
        String expression = null;
        if (args.length == 0) {
            usage();
        } else {
            expression = args[0];
            verbose = args.length > 1 && args[1].toLowerCase().startsWith("-v");
        }
        
        Value value;
        DemoParser parser = new Demo().new DemoParser(new GrammarExtendedCalc());
        
        if (verbose) {
            // Tokenize
            List<Symbol> tokens = parser.tokenize(expression);
            for (Symbol token : tokens) {
                System.out.println(token);
            }
            System.out.println();
            
            // Convert infix (left to right) to RPN
            List<Symbol> rpn = parser.infixToRPN(tokens);
            for (Symbol token : rpn) {
                System.out.println(token);
            }
            System.out.println();        
            
            // Execute the grammar
            value = parser.RPNtoValue(rpn);
        } else {
            value = parser.eval(expression);
        }
        
        if (value != null) {
            switch (value.getType()) {
                case BOOLEAN:
                    System.out.println(value.asBoolean() + " [boolean]");
                    break;
                    
                case DATE:
                    System.out.println(value.asDate() + " [date]");
                    break;
                    
                case STRING:
                    System.out.println(value.asString() + " [string]");
                    break;
                    
                case NUMBER:
                    System.out.println(value.asNumber() + " [number]");
                    break;
                    
                case OBJECT:
                    System.out.println(value.asObject() + " [object]");
                    break;
                    
                case UNDEFINED:
                    System.out.println("undefined");
                    break;
            } 
        } else {
            System.out.println("null");
        }
    }

}
