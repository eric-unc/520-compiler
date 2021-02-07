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
			System.err.println(e.getMessage());
			System.exit(4);
		}
	}
	
	/** Program ::= Class* <em>end</em> */
	public void parseProgram(){
		parseClass();
		take(END);
	}
	
	/** Class ::= <strong>class</strong> id <strong>{</strong> ClassItem* <strong>}</strong> */
	private void parseClass(){
		take(CLASS);
		take(IDEN);
		take(L_BRACKET);
		
		while(currToken.getType() != R_BRACKET){
			parseClassItem();
		}
		
		takeIt();
	}
	
	/** ClassItem ::= Modifiers (Type Field|(<strong>void</strong>|Type) Method) */
	private void parseClassItem(){
		parseModifiers();
		
		if(currToken.getType() == VOID){
			takeIt();
			parseMethod();
		}else{
			parseType();
			// TODO: this should be part of parseField or parseMethod but that's too hard
			take(IDEN);
			
			if(currToken.getType() == SEMI)
				parseField();
			else{
				take(L_PAREN); // This too
				parseMethod();
			}
		}
	}
	
	/** Modifiers ::= (<strong>public</strong>|<strong>private</strong>)? <strong>static</strong>? */
	private void parseModifiers(){
		if(currToken.getType() == PUBLIC || currToken.getType() == PRIVATE)
			takeIt();
		
		if(currToken.getType() == STATIC)
			takeIt();
	}
	
	/** Type ::= (<strong>int</strong>|<strong>boolean</strong>|Id)(<strong>[]</strong>)? */
	private void parseType(){
		if(currToken.getType() != INT && currToken.getType() != BOOLEAN)
			take(IDEN);
		else
			takeIt();
		
		if(currToken.getType() == L_SQ_BRACK){
			takeIt();
			take(R_SQ_BRACK);
		}
	}
	
	/** Field ::= Id<strong>;</strong> */
	private void parseField(){
		takeIt();
	}
	
	/** Method ::= Id<strong>(</strong>ParamList*<strong>){</strong>Statement*<strong>}</strong>*/
	private void parseMethod(){
		if(currToken.getType() != R_PAREN){
			parseParamList();
		}else
			takeIt();
		
		take(L_BRACKET);
		
		while(currToken.getType() != R_BRACKET)
			parseStatement();
		
		takeIt();
	}
	
	/** ParamList ::= Type Id(, Type Id)* */
	private void parseParamList(){
		// TODO
	}
	
	/** ArgList ::= Id(, Id)* **/
	private void parseArgList(){
		take(IDEN);
		
		while(currToken.getType() == COMMA){
			takeIt();
			take(IDEN);
		}
	}
	
	/** Reference ::= (Id|**this**)(**.**Id)* **/
	private void parseReference(){
		// TODO
	}
	
	/** Statement ::= TODO */
	private void parseStatement(){
		// TODO
	}
	
	/** Expression ::= TODO */
	private void parseExpression(){
		// TODO
	}
	
	private void take(TokenType type) throws CompilerException {
		if(currToken.getType() == type){
			currToken = scanner.scan();
		}else
			throw new CompilerException(type, scanner);
	}
	
	private void takeIt(){
		currToken = scanner.scan();
	}
}
