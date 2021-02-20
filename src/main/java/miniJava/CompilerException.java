package miniJava;

import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.TokenType;

public class CompilerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CompilerException(){
		
	}
	
	public CompilerException(TokenType expected, TokenType got, Scanner scanner){
		super("Expected " + expected + " token on line " + scanner.getLineNum() + ":" + scanner.getLineWidth() + ", got " + got + "!");
	}
}
