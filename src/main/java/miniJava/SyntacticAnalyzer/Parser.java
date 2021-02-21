package miniJava.SyntacticAnalyzer;

import static miniJava.SyntacticAnalyzer.TokenType.AND_LOG;
import static miniJava.SyntacticAnalyzer.TokenType.BOOLEAN;
import static miniJava.SyntacticAnalyzer.TokenType.CLASS;
import static miniJava.SyntacticAnalyzer.TokenType.COMMA;
import static miniJava.SyntacticAnalyzer.TokenType.DIV;
import static miniJava.SyntacticAnalyzer.TokenType.DOT;
import static miniJava.SyntacticAnalyzer.TokenType.ELSE;
import static miniJava.SyntacticAnalyzer.TokenType.END;
import static miniJava.SyntacticAnalyzer.TokenType.EQUALS;
import static miniJava.SyntacticAnalyzer.TokenType.EQUALS_OP;
import static miniJava.SyntacticAnalyzer.TokenType.IDEN;
import static miniJava.SyntacticAnalyzer.TokenType.INT;
import static miniJava.SyntacticAnalyzer.TokenType.LESS_EQUAL;
import static miniJava.SyntacticAnalyzer.TokenType.LESS_THAN;
import static miniJava.SyntacticAnalyzer.TokenType.L_BRACKET;
import static miniJava.SyntacticAnalyzer.TokenType.L_PAREN;
import static miniJava.SyntacticAnalyzer.TokenType.L_SQ_BRACK;
import static miniJava.SyntacticAnalyzer.TokenType.MINUS;
import static miniJava.SyntacticAnalyzer.TokenType.MORE_EQUAL;
import static miniJava.SyntacticAnalyzer.TokenType.MORE_THAN;
import static miniJava.SyntacticAnalyzer.TokenType.NEG;
import static miniJava.SyntacticAnalyzer.TokenType.NOT_EQUALS;
import static miniJava.SyntacticAnalyzer.TokenType.OR_LOG;
import static miniJava.SyntacticAnalyzer.TokenType.PLUS;
import static miniJava.SyntacticAnalyzer.TokenType.PRIVATE;
import static miniJava.SyntacticAnalyzer.TokenType.PUBLIC;
import static miniJava.SyntacticAnalyzer.TokenType.R_BRACKET;
import static miniJava.SyntacticAnalyzer.TokenType.R_PAREN;
import static miniJava.SyntacticAnalyzer.TokenType.R_SQ_BRACK;
import static miniJava.SyntacticAnalyzer.TokenType.SEMI;
import static miniJava.SyntacticAnalyzer.TokenType.STATIC;
import static miniJava.SyntacticAnalyzer.TokenType.THIS;
import static miniJava.SyntacticAnalyzer.TokenType.TIMES;
import static miniJava.SyntacticAnalyzer.TokenType.VOID;

import miniJava.CompilerException;
import miniJava.AbstractSyntaxTrees.ArrayType;
import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.BinaryExpr;
import miniJava.AbstractSyntaxTrees.BooleanLiteral;
import miniJava.AbstractSyntaxTrees.CallExpr;
import miniJava.AbstractSyntaxTrees.ClassType;
import miniJava.AbstractSyntaxTrees.ExprList;
import miniJava.AbstractSyntaxTrees.Expression;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.IntLiteral;
import miniJava.AbstractSyntaxTrees.IxExpr;
import miniJava.AbstractSyntaxTrees.LiteralExpr;
import miniJava.AbstractSyntaxTrees.NewArrayExpr;
import miniJava.AbstractSyntaxTrees.NewObjectExpr;
import miniJava.AbstractSyntaxTrees.Operator;
import miniJava.AbstractSyntaxTrees.RefExpr;
import miniJava.AbstractSyntaxTrees.Reference;
import miniJava.AbstractSyntaxTrees.Terminal;
import miniJava.AbstractSyntaxTrees.TypeDenoter;
import miniJava.AbstractSyntaxTrees.TypeKind;
import miniJava.AbstractSyntaxTrees.UnaryExpr;
import miniJava.AbstractSyntaxTrees.Visitor;

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
	// TODO: AST
	public void parseProgram(){
		while(currToken.getType() != END){
			parseClass();
		}
		
		takeIt();
	}
	
	/** Class ::= <strong>class</strong> <em>id</em> <strong>{</strong> ClassMember* <strong>}</strong> */
	// TODO: AST
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
	// TODO: AST
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
	// TODO: AST
	private void parseModifiers(){
		if(currToken.getType() == PUBLIC || currToken.getType() == PRIVATE)
			takeIt();
		
		if(currToken.getType() == STATIC)
			takeIt();
	}
	
	/** Type ::= Type ::= <strong>boolean</strong>|((<strong>int</strong>|Id)(<strong>[]</strong>)?) */
	// TODO: AST
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
	// TODO: AST
	private void parseField(){
		takeIt();
	}
	
	/** Method ::= <em>id</em><strong>(</strong>ParamList*<strong>){</strong>Statement*<strong>}</strong>*/
	// TODO: AST
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
	// TODO: AST
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
	private ExprList parseArgList(){
		Expression e = parseExpression();
		ExprList ret = new ExprList();
		ret.add(e);
		
		while(currToken.getType() == COMMA){
			takeIt();
			ret.add(parseExpression());
		}
		
		return ret;
	}
	
	/** Reference ::= (<em>id</em>|<strong>this</strong>)(<strong>.</strong><em>id</em>)* **/
	// TODO: AST
	private Reference parseReference(){
		if(currToken.getType() == THIS)
			takeIt();
		else
			take(IDEN);
		
		while(currToken.getType() == DOT){
			takeIt();
			take(IDEN);
		}
		
		return null; // TODO
	}
	
	/** Statement ::= <strong>{</strong> Statement* <strong>}</strong><br />
	 		| <strong>return</strong> Expression<strong>;</strong><br />
			| <strong>if(</strong>Expression<strong>)</strong> Statement (<strong>else</strong> Statement)?<br />
			| <strong>while(</strong>Expression<strong>)</strong> Statement<br />
			| Type <em>id</em> <strong>=</strong> Expression<strong>;</strong><br />
			| Reference(<strong>[</strong>Expression<strong>]</strong>)? <strong>=</strong> Expression<strong>;</strong><br />
			| Reference<strong>(</strong>ArgList?<strong>);</strong><br />
	*/
	// TODO: AST
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
					
					if(currToken.getType() != R_PAREN)
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
							
							if(currToken.getType() != R_PAREN)
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

						if(currToken.getType() == IDEN)
							parseArgList();

						take(R_PAREN);
						take(SEMI);
				}
		}
	}
	
	/** Expression ::= OrExpression */
	private Expression parseExpression(){
		return parseOrExpression();
	}
	
	/** OrExpression ::= AndExpression (<strong>||</strong> AndExpression)* */
	private Expression parseOrExpression(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		Expression ret = parseAndExpression();

		while(currToken.getType() == OR_LOG){
			Expression e1 = ret;
			Operator op = new Operator(currToken);
			
			takeIt();
			
			Expression e2 = parseAndExpression();
			SourcePosition pos = new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** AndExpression ::= EqualityExpression (<strong>&&</strong> EqualityExpression)* */
	private Expression parseAndExpression(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		Expression ret = parseEqualityExpression();

		while(currToken.getType() == AND_LOG){
			Expression e1 = ret;
			Operator op = new Operator(currToken);
			
			takeIt();
			
			Expression e2 = parseEqualityExpression();
			SourcePosition pos = new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** EqualityExpression ::= RelationalExpression ((<strong>==</strong>|<strong>!=</strong>) RelationalExpression)* */
	private Expression parseEqualityExpression(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		Expression ret = parseRelationalExpression();

		while(currToken.getType() == EQUALS_OP || currToken.getType() == NOT_EQUALS){
			Expression e1 = ret;
			Operator op = new Operator(currToken);
			
			takeIt();
			
			Expression e2 = parseRelationalExpression();
			SourcePosition pos = new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** RelationalExpression ::= AdditiveExpression ((<strong>==</strong>|<strong>!=</strong>) AdditiveExpression)? */
	private Expression parseRelationalExpression(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		Expression ret = parseAdditiveExpression();

		while(currToken.getType() == MORE_THAN || currToken.getType() == LESS_THAN || currToken.getType() == MORE_EQUAL || currToken.getType() == LESS_EQUAL){
			Expression e1 = ret;
			Operator op = new Operator(currToken);
			
			takeIt();
			
			Expression e2 = parseAdditiveExpression();
			SourcePosition pos = new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** AdditiveExpression ::= MultiplicativeExpression ((<strong>+</strong>|<strong>-</strong>) MultiplicativeExpression)* */
	private Expression parseAdditiveExpression(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		Expression ret = parseMultiplicativeExpression();

		while(currToken.getType() == PLUS || currToken.getType() == MINUS){
			Expression e1 = ret;
			Operator op = new Operator(currToken);
			
			takeIt();
			
			Expression e2 = parseMultiplicativeExpression();
			SourcePosition pos = new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** MultiplicativeExpression ::= UnaryExpression ((<strong>*</strong>|<strong>/</strong>) UnaryExpression)* */
	private Expression parseMultiplicativeExpression(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		Expression ret = parseUnaryExpression();

		while(currToken.getType() == TIMES || currToken.getType() == DIV){
			Expression e1 = ret;
			
			Operator op = new Operator(currToken);
			takeIt();
			
			Expression e2 = parseUnaryExpression();
			SourcePosition pos = new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** UnaryExpression ::= ((<strong>-</strong>|<strong>!</strong>) UnaryExpression) | PureExpression */
	private Expression parseUnaryExpression(){
		if(currToken.getType() == MINUS || currToken.getType() == NEG){
			int startLineNum = scanner.getLineNum();
			int startLineWidth = scanner.getLineWidth();
			
			Operator op = new Operator(currToken);
			takeIt();
			
			Expression e = parseUnaryExpression();
			SourcePosition pos = new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth());
			return new UnaryExpr(op, e, pos);
		}else
			return parsePureExpression();
	}
	
	/** PureExpression ::= <strong>(</strong>Expression<strong>)</strong><br />
			| <em>literal</em><br />
			| <strong>new</strong> (<strong>int[</strong>Expression<strong>]</strong>|<em>id</em>(<strong>()</strong>|<strong>[</strong>Expression<strong>]</strong>))<br />
			| Reference(<strong>[</strong>Expression<strong>]</strong>|<strong>(</strong>ArgList?<strong>)</strong>)?
	*/
	private Expression parsePureExpression(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		switch(currToken.getType()){
			case L_PAREN: // TODO: potentially () should be included in the POS
				takeIt();
				Expression ret = parseExpression();
				take(R_PAREN);
				return ret;
			
			case NUM:
				Terminal il = new IntLiteral(currToken);
				takeIt();
				return new LiteralExpr(il, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				
			case TRUE:
			case FALSE:
				Terminal bl = new BooleanLiteral(currToken);
				takeIt();
				return new LiteralExpr(bl, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				
			case NEW:
				takeIt();
				
				if(currToken.getType() == INT){
					TypeDenoter td = new BaseType(TypeKind.INT, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
					takeIt();
					take(L_SQ_BRACK);
					Expression e = parseExpression();
					take(R_SQ_BRACK);
					
					return new NewArrayExpr(td, e, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				}else{
					Identifier i = new Identifier(take(IDEN));
					ClassType ct = new ClassType(i, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
					
					if(currToken.getType() == L_PAREN){ // new id()
						takeIt();
						take(R_PAREN);
						return new NewObjectExpr(ct, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
					}else{ // new id[expr]
						take(L_SQ_BRACK);
						Expression e = parseExpression();
						take(R_SQ_BRACK);
						return new NewArrayExpr(ct, e, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
					}
				}
				
			case IDEN:
			case THIS:
			default:
				Reference r = parseReference();
				
				if(currToken.getType() == L_SQ_BRACK){
					takeIt();
					Expression e = parseExpression();
					take(R_SQ_BRACK);
					return new IxExpr(r, e, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				}else if(currToken.getType() == L_PAREN){
					takeIt();
					
					ExprList el = new ExprList();
					
					if(currToken.getType() != R_PAREN)
						el = parseArgList();
					
					take(R_PAREN);
					return new CallExpr(r, el, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				}else
					return new RefExpr(r, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
		}
	}
	
	private Token take(TokenType expected) throws CompilerException {
		Token ret = currToken;
		
		if(currToken.getType() == expected)
			currToken = scanner.scan();
		else
			throw new CompilerException(expected, currToken.getType(), scanner);
		
		return ret;
	}
	
	private Token takeIt(){
		Token ret = currToken;
		currToken = scanner.scan();
		return ret;
	}
}
