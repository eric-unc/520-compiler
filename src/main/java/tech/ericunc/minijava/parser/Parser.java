package tech.ericunc.minijava.parser;

import tech.ericunc.minijava.exceptions.CompilerException;
import tech.ericunc.minijava.scanner.Scanner;
import tech.ericunc.minijava.scanner.Token;
import tech.ericunc.minijava.scanner.TokenType;

import static tech.ericunc.minijava.scanner.TokenType.*;

public class Parser {
	private Scanner scanner;
	
	private Token currToken;
	
	public Parser(Scanner scanner){
		this.scanner = scanner;
		
		currToken = scanner.scan();
	}
	
	public void parse(){
		try {
			parseProgram();
		}catch(CompilerException e){
			System.exit(4);
		}
	}
	
	public void parseProgram(){
		// TODO
		take(END);
	}
	
	private void take(TokenType type) throws CompilerException {
		if(currToken.getType() == type){
			currToken = scanner.scan();
		}else
			throw new CompilerException();
	}
}
