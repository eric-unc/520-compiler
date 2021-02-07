package tech.ericunc.minijava.exceptions;

import tech.ericunc.minijava.scanner.Scanner;
import tech.ericunc.minijava.scanner.TokenType;

public class CompilerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CompilerException(){
		
	}
	
	public CompilerException(TokenType expectedType, Scanner scanner){
		super("Expected " + expectedType + " token on line " + scanner.getLineNum() + "!");
	}
}
