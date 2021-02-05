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
	
	/** Program ::= Class* <em>end</em> */
	public void parseProgram(){
		parseClass();
		take(END);
	}
	
	/** Class ::= <strong>class</strong> id <strong>{</strong> (Modifiers (Type Field|(<strong>void</strong>|Type) Method))* <strong>}</strong> */
	private void parseClass(){
		take(CLASS);
		take(IDEN);
		take(L_BRACKET);
		
		while(currToken.getType() != R_BRACKET){
			parseModifiers();
			
			if(currToken.getType() == VOID){
				takeIt();
				
			}else{
				parseType();
			}
		}
		
		takeIt();
	}
	
	/** Modifiers ::= (<strong>public</strong>|<strong>private</strong>)? <strong>static</strong>? */
	private void parseModifiers(){
		if(currToken.getType() == PUBLIC || currToken.getType() == PRIVATE)
			takeIt();
		
		if(currToken.getType() == STATIC)
			takeIt();
	}
	
	private void parseType(){
		// TODO
	}
	
	/** Field ::= Type <em>id</em><strong>;</strong>*/
	@SuppressWarnings("unused")
	private void parseField(){
		// TODO
	}
	
	/** Method ::= (Type|<strong>void</strong>) <em>id</em><strong>(</strong>Parameters?<strong>){</strong>Statement*<strong>}</strong>*/
	@SuppressWarnings("unused")
	private void parseMethod(){
		// TODO
	}
	
	private void take(TokenType type) throws CompilerException {
		if(currToken.getType() == type){
			currToken = scanner.scan();
		}else
			throw new CompilerException();
	}
	
	private void takeIt(){
		currToken = scanner.scan();
	}
}
