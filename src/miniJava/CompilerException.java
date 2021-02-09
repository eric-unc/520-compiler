package miniJava;

import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.TokenType;

public class CompilerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CompilerException(){
		
	}
	
	public CompilerException(TokenType expectedType, Scanner scanner){
		super("Expected " + expectedType + " token on line " + scanner.getLineNum() + "!");
	}
}
