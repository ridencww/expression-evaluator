# expression-evaluator

An extensible general purpose expression evaluator in Java.

## Features

 - Lightweight and fast expression parser
 - Designed around an abstract grammar object that can be expanded as desired
 - Symbols defining grammar are easily customizable
 - New functions can easily be added
 - Compiled expressions can be cached for performance
 - Result types of STRING, NUMBER, DATE, and BOOLEAN
 - Parameter type validation 
 - Variables
 - Single call to parse() can contain multiple expressions
 - User properties, environment variables, and data sets
 - Two grammars supplied, a simple 4 function calculator grammar with a few functions and an extended grammar with many string and math functions

## Requirements

This project uses Java 1.5+ and requires no additional libraries other than the ones provided by the JDK/JRE.

## Installation

### From source

Clone the repository from Github:

    git://github.com/ridencww/expression-evaluator.git

Build the project and create the JAR:

##### Using Maven
    mvn clean install

### Maven

If you're using Maven for your project, add the following to your project's pom.xml:

    <dependency>
      <groupId>com.creativewidgetworks</groupId>
      <artifactId>expression-evaluator</artifactId>
      <version>1.0.0</version>
    </dependency>

## Basic evaluator (GrammarBasicCalc.java)

    Assignment  =
    Operators   + - * / DIV MOD % ^ 
    Logical     < <= == != >= > AND OR NOT XOR
    Ternary     ? :  
    Shift       << >>
    DataSource  ${<id>}
    Constants   NULL PI
    Functions   ABS NOW SQR SQRT


## Extended evaluator (GrammarExtendedCalc.java)

    Assignment  =
    Operators   + - * / DIV MOD % ^ 
    Logical     < <= == != >= > AND OR NOT XOR
    Ternary     ? :  
    Shift       << >>
    DataSource  ${<id>}
    Constants   NULL PI
    Functions   ABS ARCCOS ARCSIN ARCTAN ARRAYLEN AVERAGE CEILING CONTAINS CONTAINSALL CONTAINSANY
                COS ENDSWITH EXP FACTORIAL FIND FLOOR HEX ISBLANK ISBOOLEAN ISDATE ISNULL LEFT LEN
                LOG LOG10 LOWER MAKEBOOLEAN MATCH MATCHBYLEN MAX MID MIN NAMECASE NOW RANDOM REPLACE
                REPLACEFIRST RIGHT SIN SPLIT SQR SQRT STARTSWITH STR STRING TAN TRIM TRIMLEFT TRIMRIGHT
                UPPER VAL

##    
    
## Usage

The jar contains a small console program that exercises the extended grammar parser as well as displaying the tokens and RPN stream.

Examples:

    java -cp expression-parser.jar Eval "(1+4)/3"
    java -cp expression-parser.jar Eval "(1+4)/3" -verbose
    java -cp expression-parser.jar Eval "upper('AbC' + 'def')"

    
## Version History
 
 - 1.0.0 Initial release to GitHub
    
## License

expression-parser is licensed under the [Modified BSD][1] license. Permission is granted to anyone to use this software for any purpose, including commercial applications.


  [1]: http://www.opensource.org/licenses/BSD-3-Clause

