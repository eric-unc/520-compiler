package miniJava.SyntacticAnalyzer;

import static miniJava.SyntacticAnalyzer.TokenType.*;

import miniJava.CompilerException;

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
		while(currToken.getType() != END){
			parseClass();
		}
		
		takeIt();
	}
	
	/** Class ::= <strong>class</strong> <em>id</em> <strong>{</strong> ClassMember* <strong>}</strong> */
	private void parseClass(){
		take(CLASS);
		take(IDEN);
		take(L_BRACKET);
		
		while(currToken.getType() != R_BRACKET){
			parseClassMember();
		}
		
		takeIt();
	}
	
	/** ClassMember ::= Modifiers (Type Field|(<strong>void</strong>|Type) Method) */
	private void parseClassMember(){
		parseModifiers();
		
		if(currToken.getType() == VOID){
			takeIt();
			take(IDEN);
			take(L_PAREN);
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
	
	/** Type ::= Type ::= <strong>boolean</strong>|((<strong>int</strong>|Id)(<strong>[]</strong>)?) */
	private void parseType(){
		if(currToken.getType() == BOOLEAN) {
			takeIt();
		}else{
			if(currToken.getType() == INT)
				takeIt();
			else
				take(IDEN);
			
			if(currToken.getType() == L_SQ_BRACK){
				takeIt();
				take(R_SQ_BRACK);
			}
		}
	}
	
	/** Field ::= <em>id</em><strong>;</strong> */
	private void parseField(){
		takeIt();
	}
	
	/** Method ::= <em>id</em><strong>(</strong>ParamList*<strong>){</strong>Statement*<strong>}</strong>*/
	private void parseMethod(){
		if(currToken.getType() != R_PAREN){
			parseParamList();
			take(R_PAREN);
		}else
			takeIt();
		
		take(L_BRACKET);
		
		while(currToken.getType() != R_BRACKET)
			parseStatement();
		
		takeIt();
	}
	
	/** ParamList ::= Type <em>id</em>(, Type <em>id</em>)* */
	private void parseParamList(){
		parseType();
		take(IDEN);
		
		while(currToken.getType() == COMMA){
			takeIt();
			parseType();
			take(IDEN);
		}
	}
	
	/** ArgList ::= Expression(, Expression)* **/
	private void parseArgList(){
		parseExpression();
		
		while(currToken.getType() == COMMA){
			takeIt();
			parseExpression();
		}
	}
	
	/** Reference ::= (<em>id</em>|<strong>this</strong>)(<strong>.</strong><em>id</em>)* **/
	private void parseReference(){
		if(currToken.getType() == THIS)
			takeIt();
		else
			take(IDEN);
		
		while(currToken.getType() == DOT){
			takeIt();
			take(IDEN);
		}
	}
	
	/** Statement ::= <strong>{</strong> Statement* <strong>}</strong><br />
	 		| <strong>return</strong> Expression<strong>;</strong><br />
			| <strong>if(</strong>Expression<strong>)</strong> Statement (<strong>else</strong> Statement)?<br />
			| <strong>while(</strong>Expression<strong>)</strong> Statement<br />
			| Type <em>id</em> <strong>=</strong> Expression<strong>;</strong><br />
			| Reference(<strong>[</strong>Expression<strong>]</strong>)? <strong>=</strong> Expression<strong>;</strong><br />
			| Reference<strong>(</strong>ArgList?<strong>);</strong><br />
	*/
	private void parseStatement(){
		switch(currToken.getType()){
			case L_BRACKET:
				takeIt();
				
				while(currToken.getType() != R_BRACKET)
					parseStatement();
				
				takeIt();
				break;
				
			case RETURN:
				takeIt();
				
				if(currToken.getType() != SEMI)
					parseExpression();
				
				take(SEMI);
				break;
				
			case IF:
				takeIt();
				take(L_PAREN);
				parseExpression();
				take(R_PAREN);
				parseStatement();
				
				if(currToken.getType() == ELSE){
					takeIt();
					parseStatement();
				}
				
				break;
				
			case WHILE:
				takeIt();
				take(L_PAREN);
				parseExpression();
				take(R_PAREN);
				parseStatement();
				break;
			
			// The following very messy parts are for the last three rules.
			// int/bool part of first statement (missing iden)
			case INT:
			case BOOLEAN:
				parseType();
				take(IDEN);
				take(EQUALS);
				parseExpression();
				take(SEMI);
				break;
			
			// this part of second/third (missing iden)
			case THIS:
				parseReference();
				
				if(currToken.getType() == L_SQ_BRACK){ // second rule with [] ::= Reference[Expression] = Expression;
					takeIt();
					parseExpression();
					take(R_SQ_BRACK);
					take(EQUALS);
					parseExpression();
					take(SEMI);
				}else if(currToken.getType() == L_PAREN){ // third rule ::= Reference(ArgList?);
					takeIt();
					
					if(currToken.getType() == IDEN)
						parseArgList();
					
					take(R_PAREN);
					take(SEMI);
				}else{ // second rule without [] ::= Reference = Expression;
					take(EQUALS);
					parseExpression();
					take(SEMI);
				}
				
				break;
			
			case IDEN: // assuming iden
			default:
				take(IDEN);
				
				switch(currToken.getType()){
					case IDEN: // first rule only
						takeIt();
						take(EQUALS);
						parseExpression();
						take(SEMI);
						break;
						
					case L_SQ_BRACK: // first or second rule with []
						takeIt();
						
						if(currToken.getType() != R_SQ_BRACK){ // second rule
							parseExpression();
							take(R_SQ_BRACK);
							
						}else{
							takeIt();
							take(IDEN);
						}
						
						take(EQUALS);
						parseExpression();
						take(SEMI);
						
						break;
					
					case DOT: // second or third rule
						takeIt();
						take(IDEN);
						
						while(currToken.getType() == DOT){
							takeIt();
							take(IDEN);
						}
						
						if(currToken.getType() == L_SQ_BRACK){ // second rule with [
							takeIt();
							parseExpression();
							take(R_SQ_BRACK);
							take(EQUALS);
							parseExpression();
							take(SEMI);
						}else if(currToken.getType() == EQUALS){ // second without [
							takeIt();
							parseExpression();
							take(SEMI);
						}else{ // third
							take(L_PAREN);
							parseArgList();
							take(R_PAREN);
							take(SEMI);
						}
						
						break;
					
					case EQUALS:
						takeIt();
						parseExpression();
						take(SEMI);
						
						break;
					
					// assume ( for final case)
					case L_PAREN:
					default:
						take(L_PAREN);
						parseArgList();
						take(R_PAREN);
						take(SEMI);
				}
		}
	}
	
	/** Expression ::= OrExpression */
	private void parseExpression(){
		parseOrExpression();
	}
	
	/** OrExpression ::= AndExpression (<strong>||</strong> AndExpression)* */
	private void parseOrExpression(){
		parseAndExpression();

		while(currToken.getType() == OR_LOG){
			takeIt();
			parseAndExpression();
		}
	}
	
	/** AndExpression ::= EqualityExpression (<strong>&&</strong> EqualityExpression)* */
	private void parseAndExpression(){
		parseEqualityExpression();
		
		while(currToken.getType() == AND_LOG){
			takeIt();
			parseEqualityExpression();
		}
	}
	
	/** EqualityExpression ::= RelationalExpression ((<strong>==</strong>|<strong>!=</strong>) RelationalExpression)* */
	private void parseEqualityExpression(){
		parseRelationalExpression();
		
		while(currToken.getType() == EQUALS_OP || currToken.getType() == NOT_EQUALS){
			takeIt();
			parseRelationalExpression();
		}
	}
	
	/** RelationalExpression ::= AdditiveExpression ((<strong>==</strong>|<strong>!=</strong>) AdditiveExpression)? */
	private void parseRelationalExpression(){
		parseAdditiveExpression();
		
		if(currToken.getType() == MORE_THAN || currToken.getType() == LESS_THAN || currToken.getType() == MORE_EQUAL || currToken.getType() == LESS_EQUAL){
			takeIt();
			parseAdditiveExpression();
		}
	}
	
	/** AdditiveExpression ::= MultiplicativeExpression ((<strong>+</strong>|<strong>-</strong>) MultiplicativeExpression)* */
	private void parseAdditiveExpression(){
		parseMultiplicativeExpression();
		
		while(currToken.getType() == PLUS || currToken.getType() == MINUS){
			takeIt();
			parseMultiplicativeExpression();
		}
	}
	
	/** MultiplicativeExpression ::= UnaryExpression ((<strong>*</strong>|<strong>/</strong>) UnaryExpression)* */
	private void parseMultiplicativeExpression(){
		parseUnaryExpression();
		
		while(currToken.getType() == TIMES || currToken.getType() == DIV){
			takeIt();
			parseUnaryExpression();
		}
	}
	
	/** UnaryExpression ::= ((<strong>-</strong>|<strong>!</strong>) UnaryExpression) | PureExpression */
	private void parseUnaryExpression(){
		if(currToken.getType() == MINUS || currToken.getType() == NEG){
			takeIt();
			parseUnaryExpression();
		}else
			parsePureExpression();
	}
	
	/** PureExpression ::= <strong>(</strong>Expression<strong>)</strong><br />
			| <em>literal</em><br />
			| <strong>new</strong> (<strong>int[</strong>Expression<strong>]</strong>|<em>id</em>(<strong>()</strong>|<strong>[</strong>Expression<strong>]</strong>))<br />
			| Reference(<strong>[</strong>Expression<strong>]</strong>|<strong>(</strong>ArgList?<strong>)</strong>)?
	*/
	private void parsePureExpression(){
		switch(currToken.getType()){
			case L_PAREN:
				takeIt();
				parseExpression();
				take(R_PAREN);
				break;
			
			case NUM:
			case TRUE:
			case FALSE:
				takeIt();
				break;
				
			case NEW:
				takeIt();
				
				if(currToken.getType() == INT){
					takeIt();
					take(L_SQ_BRACK);
					parseExpression();
					take(R_SQ_BRACK);
				}else{
					take(IDEN);
					
					if(currToken.getType() == L_PAREN){
						takeIt();
						take(R_PAREN);
					}else{
						take(L_SQ_BRACK);
						parseExpression();
						take(R_SQ_BRACK);
					}
				}
				
				break;
				
			case IDEN:
			case THIS:
			default:
				parseReference();
				
				if(currToken.getType() == L_SQ_BRACK){
					takeIt();
					parseExpression();
					take(R_SQ_BRACK);
				}else if(currToken.getType() == L_PAREN){
					takeIt();
					
					if(currToken.getType() == IDEN)
						parseArgList();
					
					take(R_PAREN);
				}
				
				break;
		}
	}
	
	private void take(TokenType expected) throws CompilerException {
		if(currToken.getType() == expected){
			currToken = scanner.scan();
		}else
			throw new CompilerException(expected, currToken.getType(), scanner);
	}
	
	private void takeIt(){
		currToken = scanner.scan();
	}
}
