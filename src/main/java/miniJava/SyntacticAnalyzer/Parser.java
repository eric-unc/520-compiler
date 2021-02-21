package miniJava.SyntacticAnalyzer;

import static miniJava.SyntacticAnalyzer.TokenType.*;

import miniJava.CompilerException;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;

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
	public Package parseProgram(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		ClassDeclList cdl = new ClassDeclList();
		while(currToken.getType() != END){
			cdl.add(parseClass());
		}
		
		takeIt();
		
		return new Package(cdl, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
	}
	
	/** Class ::= <strong>class</strong> <em>id</em> <strong>{</strong> ClassMember* <strong>}</strong> */
	private ClassDecl parseClass(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		take(CLASS);
		String cn = take(IDEN).getValue();
		take(L_BRACKET);
		
		FieldDeclList fdl = new FieldDeclList();
		MethodDeclList mdl = new MethodDeclList();
		
		while(currToken.getType() != R_BRACKET){
			parseClassMember(fdl, mdl);
		}
		
		takeIt();
		return new ClassDecl(cn, fdl, mdl, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
	}
	
	/** ClassMember ::= Modifiers (Type Field|(<strong>void</strong>|Type) Method) */
	private void parseClassMember(FieldDeclList fdl, MethodDeclList mdl){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		boolean[] mods = parseModifiers();
		
		if(currToken.getType() == VOID){
			takeIt();
			SourcePosition sp = new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth());
			String name = take(IDEN).getValue();
			take(L_PAREN);
			parseMethod(new FieldDecl(mods[0], mods[1], new BaseType(TypeKind.VOID, sp), name, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth())), mdl);
		}else{
			// TODO: should make a FieldDecl parser first, and then the method is based off it
			
			TypeDenoter td = parseType();
			
			String name = take(IDEN).getValue();
			
			if(currToken.getType() == SEMI){
				takeIt();
				fdl.add(new FieldDecl(mods[0], mods[1], td, name, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth())));
			}else{
				take(L_PAREN); // This too
				parseMethod(new FieldDecl(mods[0], mods[1], td, name, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth())), mdl);
			}
		}
	}
	
	/** Modifiers ::= (<strong>public</strong>|<strong>private</strong>)? <strong>static</strong>? */
	private boolean[] parseModifiers(){
		boolean[] ret = new boolean[]{false, false};
		
		if(currToken.getType() == PUBLIC)
			takeIt();
		else if(currToken.getType() == PRIVATE){
			ret[0] = true;
			takeIt();
		}
		
		if(currToken.getType() == STATIC){
			ret[1] = true;
			takeIt();
		}
		
		return ret;
	}
	
	/** Type ::= Type ::= <strong>boolean</strong>|((<strong>int</strong>|Id)(<strong>[]</strong>)?) */
	private TypeDenoter parseType(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		if(currToken.getType() == BOOLEAN) {
			takeIt();
			return new BaseType(TypeKind.BOOLEAN, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
		}else{
			TypeDenoter ret;
			
			if(currToken.getType() == INT) {
				takeIt();
				ret = new BaseType(TypeKind.INT, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
			}else{
				Identifier i = new Identifier(take(IDEN));
				ret = new ClassType(i, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
			}
			
			if(currToken.getType() == L_SQ_BRACK){
				takeIt();
				take(R_SQ_BRACK);
				ret = new ArrayType(ret, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
			}
			
			return ret;
		}
	}
	
	/** Field ::= <em>id</em><strong>;</strong> */
	/*private void parseField(FieldDeclList fdl){
		takeIt();
	}*/
	
	/** Method ::= <em>id</em><strong>(</strong>ParamList*<strong>){</strong>Statement*<strong>}</strong>*/
	private void parseMethod(MemberDecl md, MethodDeclList mdl){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		ParameterDeclList p1 = new ParameterDeclList();
		
		if(currToken.getType() != R_PAREN){
			p1 = parseParamList();
			take(R_PAREN);
		}else
			takeIt();
		
		take(L_BRACKET);
		
		StatementList sl = new StatementList();
		while(currToken.getType() != R_BRACKET){
			sl.add(parseStatement());
		}
		
		takeIt();
		
		mdl.add(new MethodDecl(md, p1, sl, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth())));
	}
	
	/** ParamList ::= Type <em>id</em>(, Type <em>id</em>)* */
	private ParameterDeclList parseParamList(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		ParameterDeclList ret = new ParameterDeclList();
		
		TypeDenoter td = parseType();
		String name = take(IDEN).getValue();
		ret.add(new ParameterDecl(td, name, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth())));
		
		while(currToken.getType() == COMMA){
			takeIt();
			int startLineNum2 = scanner.getLineNum();
			int startLineWidth2 = scanner.getLineWidth();
			TypeDenoter td2 = parseType();
			String name2 = take(IDEN).getValue();
			ret.add(new ParameterDecl(td2, name2, new SourcePosition(startLineNum2, scanner.getLineNum(), startLineWidth2, scanner.getLineWidth())));
		}
		
		return ret;
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
	private Reference parseReference(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		Reference r;
		
		if(currToken.getType() == THIS){
			takeIt();
			r = new ThisRef(new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
			
		}else{
			Identifier id = new Identifier(take(IDEN));
			r = new IdRef(id, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
		}
		
		while(currToken.getType() == DOT){
			takeIt();
			Identifier id = new Identifier(take(IDEN));
			r = new QualRef(r, id, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
		}
		
		return r;
	}
	
	/** Statement ::= <strong>{</strong> Statement* <strong>}</strong><br />
	 		| <strong>return</strong> Expression<strong>;</strong><br />
			| <strong>if(</strong>Expression<strong>)</strong> Statement (<strong>else</strong> Statement)?<br />
			| <strong>while(</strong>Expression<strong>)</strong> Statement<br />
			| Type <em>id</em> <strong>=</strong> Expression<strong>;</strong><br />
			| Reference(<strong>[</strong>Expression<strong>]</strong>)? <strong>=</strong> Expression<strong>;</strong><br />
			| Reference<strong>(</strong>ArgList?<strong>);</strong><br />
	*/
	private Statement parseStatement(){
		int startLineNum = scanner.getLineNum();
		int startLineWidth = scanner.getLineWidth();
		
		switch(currToken.getType()){
			case L_BRACKET:
				takeIt();
				
				StatementList sl = new StatementList();
				
				while(currToken.getType() != R_BRACKET)
					sl.add(parseStatement());
				
				takeIt();
				return new BlockStmt(sl, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				
			case RETURN:
				takeIt();
				
				Expression retE = null;
				
				if(currToken.getType() != SEMI)
					retE = parseExpression();
				
				take(SEMI);
				return new ReturnStmt(retE, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				
			case IF:
				takeIt();
				take(L_PAREN);
				Expression ifE = parseExpression();
				take(R_PAREN);
				Statement ifTS = parseStatement();
				
				if(currToken.getType() == ELSE){
					takeIt();
					Statement ifFS = parseStatement();
					return new IfStmt(ifE, ifTS, ifFS, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				}else{
					return new IfStmt(ifE, ifTS, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				}
				
			case WHILE:
				takeIt();
				take(L_PAREN);
				Expression whileE = parseExpression();
				take(R_PAREN);
				Statement whileS = parseStatement();
				return new WhileStmt(whileE, whileS, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
			
			// The following very messy parts are for the last three rules.
			// int/bool part of first statement (missing iden):
			// (int|boolean) id = Expression;
			case INT:
			case BOOLEAN:
				TypeDenoter td = parseType();
				String name = take(IDEN).getValue();
				VarDecl vd = new VarDecl(td, name, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				
				take(EQUALS);
				Expression val = parseExpression();
				take(SEMI);
				return new VarDeclStmt(vd, val, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
			
			// this part of second/third (missing iden)
			case THIS:
				Reference r = parseReference();
				
				if(currToken.getType() == L_SQ_BRACK){ // second rule with [] ::= Reference[Expression] = Expression;
					takeIt();
					Expression arrLoc = parseExpression();
					take(R_SQ_BRACK);
					
					take(EQUALS);
					Expression ixVal = parseExpression();
					take(SEMI);
					
					return new IxAssignStmt(r, arrLoc, ixVal, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				}else if(currToken.getType() == L_PAREN){ // third rule ::= Reference(ArgList?);
					takeIt();
					
					ExprList el = new ExprList();
					
					if(currToken.getType() != R_PAREN)
						el = parseArgList();
					
					take(R_PAREN);
					take(SEMI);
					return new CallStmt(r, el, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				}else{ // second rule without [] ::= Reference = Expression;
					take(EQUALS);
					Expression asVal = parseExpression();
					take(SEMI);
					return new AssignStmt(r, asVal, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
				}
			
			case IDEN: // assuming iden
			default:
				Identifier id = new Identifier(take(IDEN));
				
				switch(currToken.getType()){
					case IDEN: // Type id = Expression;
						TypeDenoter td2 = new ClassType(id, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						String name2 = takeIt().getValue();
						VarDecl varD = new VarDecl(td2, name2, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						
						take(EQUALS);
						Expression e = parseExpression();
						take(SEMI);
						return new VarDeclStmt(varD, e, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						
					case L_SQ_BRACK: // first or second rule with []
						takeIt();
						
						if(currToken.getType() != R_SQ_BRACK){ // second rule; Ref[Expr] = Expr;
							Reference idR = new IdRef(id, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
							Expression ix = parseExpression();
							take(R_SQ_BRACK);
							take(EQUALS);
							Expression newVal = parseExpression();
							take(SEMI);
							return new IxAssignStmt(idR, ix, newVal, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						}else{ // first rule; Ref[] id = Expr;
							takeIt();
							// TODO: lmao the first SourcePosition is BS
							TypeDenoter td3 = new ClassType(id, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
							TypeDenoter td4 = new ArrayType(td3, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
							String name3 = take(IDEN).getValue();
							VarDecl vDecl = new VarDecl(td4, name3, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
							take(EQUALS);
							Expression newVal = parseExpression();
							take(SEMI);
							return new VarDeclStmt(vDecl, newVal, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						}
					
					case DOT: // second or third rule
						Reference r2 = new IdRef(id, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						takeIt();
						Identifier id2 = new Identifier(take(IDEN));
						r2 = new QualRef(r2, id2, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						
						while(currToken.getType() == DOT){
							takeIt();
							Identifier id3 = new Identifier(take(IDEN));
							r2 = new QualRef(r2, id3, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						}
						
						if(currToken.getType() == L_SQ_BRACK){ // second rule with [
							// like thing.q[5] = 5
							takeIt();
							Expression e2 = parseExpression();
							take(R_SQ_BRACK);
							
							
							take(EQUALS);
							Expression e3 = parseExpression();
							take(SEMI);
							return new IxAssignStmt(r2, e2, e3, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						}else if(currToken.getType() == EQUALS){ // second without [
							// like thing.q = 5
							takeIt();
							Expression newVal = parseExpression();
							take(SEMI);
							return new AssignStmt(r2, newVal, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						}else{ // third, like thing.q()
							take(L_PAREN);
							
							ExprList el = new ExprList();
							
							if(currToken.getType() != R_PAREN)
								el = parseArgList();
							
							take(R_PAREN);
							take(SEMI);
							return new CallStmt(r2, el, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						}
					
					case EQUALS: // id = Exp;
						Reference ref = new IdRef(id, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						takeIt();
						Expression e2 = parseExpression();
						take(SEMI);
						return new AssignStmt(ref, e2, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
					
					// assume ( for final case)
					case L_PAREN: // id(ArgList?)
					default:
						Reference ref2 = new IdRef(id, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
						take(L_PAREN);

						ExprList el = new ExprList();
						
						if(currToken.getType() == IDEN)
							el = parseArgList();

						take(R_PAREN);
						take(SEMI);
						return new CallStmt(ref2, el, new SourcePosition(startLineNum, scanner.getLineNum(), startLineWidth, scanner.getLineWidth()));
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