# expression-evaluator

An extensible general purpose expression evaluator in Java.

## Features

- Lightweight and fast expression parser
- Designed around an abstract grammar object that can be expanded as desired
- Symbols defining grammar are easily customizable
- New functions can easily be added
- Supports result types of STRING, NUMBER, DATE, and BOOLEAN
- Parameter type validation supported
- Supports user properties, enviornment variables, and data sets
- Two grammars supplied, a 4 function calculator grammar and an extended grammar with string and math functions

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
      <version>1.0.0-SNAPSHOT</version>
    </dependency>

## Functions supported by basic calcuator

Coming soon. For now, examine GrammarBasicCalc.java

## Functions supported by extended calcuator

Coming soon. For now, examine GrammarExtendedCalc.java 

##    
    
## Demo

The jar contains a small console program that exercises the extended grammar parser as well as displaying the tokens and RPN stream.

Examples:

    java -cp expression-parser "(1+4)/3"
    java -cp expression-parser "(1+4)/3" -verbose
    java -cp expression-parser "upper('AbC' + 'def')"

    
## Version History
 
 - 1.0.0-SNAPSHOT Initial pre-release

    
## License

expression-parser is licensed under the [Modified BSD][1] license. Permission is granted to anyone to use this software for any purpose, including commercial applications.

Enjoy.


  [1]: http://www.opensource.org/licenses/BSD-3-Clause
