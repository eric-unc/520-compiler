package miniJava.SyntacticAnalyzer;

import static miniJava.SyntacticAnalyzer.TokenType.*;

import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;

public class Parser {
	private Scanner scanner;
	
	private Token currToken;
	
	public Parser(Scanner scanner){
		this.scanner = scanner;
		
		currToken = scanner.scan();
	}
	
	public AST parse(){
		try {
			return parseProgram();
		}catch(SyntacticAnalyzerException e){
			System.err.println(e.getMessage());
			System.exit(4);
		}
		
		return null;
	}
	
	/** Program ::= Class* <em>end</em> */
	public Package parseProgram(){
		HalfPosition start = scanner.getHalfPosition();
		
		ClassDeclList cdl = new ClassDeclList();
		while(currToken.getType() != END){
			cdl.add(parseClass());
		}
		
		takeIt();
		
		return new Package(cdl, new SourcePosition(start, scanner.getHalfPosition()));
	}
	
	/** Class ::= <strong>class</strong> <em>id</em> <strong>{</strong> ClassMember* <strong>}</strong> */
	private ClassDecl parseClass(){
		HalfPosition start = scanner.getHalfPosition();
		
		take(CLASS);
		String cn = take(IDEN).getValue();
		take(L_BRACKET);
		
		FieldDeclList fdl = new FieldDeclList();
		MethodDeclList mdl = new MethodDeclList();
		
		while(currToken.getType() != R_BRACKET){
			parseClassMember(fdl, mdl);
		}
		
		takeIt();
		return new ClassDecl(cn, fdl, mdl, new SourcePosition(start, scanner.getHalfPosition()));
	}
	
	/** ClassMember ::= FieldDeclaration (FieldTail|Method) | <strong>static</strong> StaticBlock | Constructor */
	private void parseClassMember(FieldDeclList fdl, MethodDeclList mdl){
		FieldDecl fd;
		HalfPosition start = scanner.getHalfPosition();
		
		if(currToken.getType() == STATIC){
			Token stat = takeIt();
			
			if(currToken.getType() == L_BRACKET){
				parseStaticBlock(mdl, stat);
				return;
			}else{
				fd = parseFieldDeclaration();
				fd.isStatic = true;
				fd.posn.adjustToStart(stat.getPosition());
			}
		}else if(currToken.getType() == IDEN){
			Token iden = takeIt();
			
			if(currToken.getType() == L_PAREN){ // assume constructor
				parseConstructor(mdl, false, iden);
				return;
			}else{ // whoops, that's just a type then
				TypeDenoter type = new ClassType(new Identifier(iden), new SourcePosition(start, scanner.getHalfPosition()));
				
				if(currToken.getType() == L_SQ_BRACK){
					takeIt();
					take(R_SQ_BRACK);
					type = new ArrayType(type, new SourcePosition(start, scanner.getHalfPosition()));
				}
				
				Identifier name = new Identifier(take(IDEN));
				
				fd = new FieldDecl(false, false, type, name.spelling, new SourcePosition(start, scanner.getHalfPosition()));
			}
		}else if(currToken.getType() == PUBLIC){
			takeIt();
			
			if(currToken.getType() == STATIC || currToken.getType() == VOID || currToken.getType() == INT || currToken.getType() == BOOLEAN){
				fd = parseFieldDeclaration();
			}else{
				Token iden = take(IDEN);
				
				if(currToken.getType() == L_PAREN){ // assume constructor
					parseConstructor(mdl, false, iden);
					return;
				}else{ // that's a type then
					TypeDenoter type = new ClassType(new Identifier(iden), new SourcePosition(start, scanner.getHalfPosition()));
					
					if(currToken.getType() == L_SQ_BRACK){
						takeIt();
						take(R_SQ_BRACK);
						type = new ArrayType(type, new SourcePosition(start, scanner.getHalfPosition()));
					}
					
					Identifier name = new Identifier(take(IDEN));
					
					fd = new FieldDecl(false, false, type, name.spelling, new SourcePosition(start, scanner.getHalfPosition()));
				}
			}
		}else if(currToken.getType() == PRIVATE){
			takeIt();
			
			if(currToken.getType() == STATIC || currToken.getType() == VOID || currToken.getType() == INT || currToken.getType() == BOOLEAN){
				fd = parseFieldDeclaration();
				fd.isPrivate = true;
			}else{
				Token iden = take(IDEN);
				
				if(currToken.getType() == L_PAREN){ // assume constructor
					parseConstructor(mdl, true, iden);
					return;
				}else{ // that's a type then
					TypeDenoter type = new ClassType(new Identifier(iden), new SourcePosition(start, scanner.getHalfPosition()));
					
					if(currToken.getType() == L_SQ_BRACK){
						takeIt();
						take(R_SQ_BRACK);
						type = new ArrayType(type, new SourcePosition(start, scanner.getHalfPosition()));
					}
					
					Identifier name = new Identifier(take(IDEN));
					
					fd = new FieldDecl(true, false, type, name.spelling, new SourcePosition(start, scanner.getHalfPosition()));
				}
			}
		}else{
			fd = parseFieldDeclaration();
		}
		
		if(currToken.getType() == SEMI){
			 parseFieldTail(fd, fdl);
		}else{
			parseMethod(fd, mdl);
		}
	}
	
	/** FieldDeclaration ::= (<strong>public</strong>|<strong>private</strong>)? <strong>static</strong>? (Type|<strong>void</strong>) Id */
	private FieldDecl parseFieldDeclaration(){
		HalfPosition start = scanner.getHalfPosition();
		boolean isPrivate = false;
		boolean isStatic = false;
		
		if(currToken.getType() == PRIVATE) {
			isPrivate = true;
			takeIt();
		}else if(currToken.getType() == PUBLIC)
			takeIt();
		
		if(currToken.getType() == STATIC){
			isStatic = true;
			takeIt();
		}
		
		TypeDenoter td;
		
		if(currToken.getType() == VOID)
			td = new BaseType(TypeKind.VOID, takeIt().getPosition());
		else
			td = parseType();
		
		String name = take(IDEN).getValue();
		
		return new FieldDecl(isPrivate, isStatic, td, name, new SourcePosition(start, scanner.getHalfPosition()));
	}
	
	private void parseFieldTail(FieldDecl fd, FieldDeclList fdl) throws SyntacticAnalyzerException {
		if(fd.type.typeKind == TypeKind.VOID)
			throw new ParserException(VOID, IDEN, scanner); // TODO: the position is technically off
		
		fdl.add(fd);
		
		take(SEMI);
	}
	
	/** Method ::= **(**ParamList\* **){**Statement\* **}** */
	private void parseMethod(FieldDecl fd, MethodDeclList mdl){
		HalfPosition start = fd.posn.getStart();
		
		take(L_PAREN);
		
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
		
		mdl.add(new MethodDecl(fd, p1, sl, new SourcePosition(start, scanner.getHalfPosition())));
	}
	
	/** StaticBlock ::= <strong>{</strong> Statement* <strong>}</strong> */
	private void parseStaticBlock(MethodDeclList mdl, Token stat){
		HalfPosition start = stat.getPosition().getStart();
		
		take(L_BRACKET);
		
		StatementList sl = new StatementList();
		while(currToken.getType() != R_BRACKET){
			sl.add(parseStatement());
		}
		
		takeIt();
		
		mdl.add(new StaticBlockDecl(sl, new SourcePosition(start, scanner.getHalfPosition())));
	}
	
	/** Id **(**ParamList\* **){**Statement\* **}** */
	private void parseConstructor(MethodDeclList mdl, boolean isPrivate, Token id){
		HalfPosition start = id.getPosition().getStart();
		
		take(L_PAREN);
		
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
		
		mdl.add(new ConstructorDecl(isPrivate, new Identifier(id), p1, sl, new SourcePosition(start, scanner.getHalfPosition())));
	}
	
	/** Type ::= Type ::= <strong>boolean</strong>|((<strong>int</strong>|Id)(<strong>[]</strong>)?) */
	private TypeDenoter parseType(){
		HalfPosition start = scanner.getHalfPosition();
		
		if(currToken.getType() == BOOLEAN) {
			takeIt();
			return new BaseType(TypeKind.BOOLEAN, new SourcePosition(start, scanner.getHalfPosition()));
		}else{
			TypeDenoter ret;
			
			if(currToken.getType() == INT) {
				takeIt();
				ret = new BaseType(TypeKind.INT, new SourcePosition(start, scanner.getHalfPosition()));
			}else{
				Identifier i = new Identifier(take(IDEN));
				ret = new ClassType(i, new SourcePosition(start, scanner.getHalfPosition()));
			}
			
			if(currToken.getType() == L_SQ_BRACK){
				takeIt();
				take(R_SQ_BRACK);
				ret = new ArrayType(ret, new SourcePosition(start, scanner.getHalfPosition()));
			}
			
			return ret;
		}
	}
	
	/** ParamList ::= Type <em>id</em>(, Type <em>id</em>)* */
	private ParameterDeclList parseParamList(){
		HalfPosition start = scanner.getHalfPosition();
		
		ParameterDeclList ret = new ParameterDeclList();
		
		TypeDenoter td = parseType();
		String name = take(IDEN).getValue();
		ret.add(new ParameterDecl(td, name, new SourcePosition(start, scanner.getHalfPosition())));
		
		while(currToken.getType() == COMMA){
			takeIt();
			start = scanner.getHalfPosition();
			TypeDenoter td2 = parseType();
			String name2 = take(IDEN).getValue();
			ret.add(new ParameterDecl(td2, name2, new SourcePosition(start, scanner.getHalfPosition())));
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
		HalfPosition start = scanner.getHalfPosition();
		
		Reference r;
		
		if(currToken.getType() == THIS){
			takeIt();
			r = new ThisRef(new SourcePosition(start, scanner.getHalfPosition()));
			
		}else{
			Identifier id = new Identifier(take(IDEN));
			r = new IdRef(id, new SourcePosition(start, scanner.getHalfPosition()));
		}
		
		while(currToken.getType() == DOT){
			takeIt();
			Identifier id = new Identifier(take(IDEN));
			r = new QualRef(r, id, new SourcePosition(start, scanner.getHalfPosition()));
		}
		
		return r;
	}
	
	/** Statement ::= <strong>{</strong> Statement* <strong>}</strong><br />
	 		| <strong>return</strong> (Expression)?<strong>;</strong><br />
			| <strong>if(</strong>Expression<strong>)</strong> Statement (<strong>else</strong> Statement)?<br />
			| <strong>while(</strong>Expression<strong>)</strong> Statement<br />
			| Type <em>id</em> <strong>=</strong> Expression<strong>;</strong><br />
			| Reference(<strong>[</strong>Expression<strong>]</strong>)? <strong>=</strong> Expression<strong>;</strong><br />
			| Reference<strong>(</strong>ArgList?<strong>);</strong><br />
	*/
	private Statement parseStatement(){
		HalfPosition start = scanner.getHalfPosition();
		
		switch(currToken.getType()){
			case L_BRACKET:
				takeIt();
				
				StatementList sl = new StatementList();
				
				while(currToken.getType() != R_BRACKET)
					sl.add(parseStatement());
				
				takeIt();
				return new BlockStmt(sl, new SourcePosition(start, scanner.getHalfPosition()));
				
			case RETURN:
				takeIt();
				
				Expression retE = null;
				
				if(currToken.getType() != SEMI)
					retE = parseExpression();
				
				take(SEMI);
				return new ReturnStmt(retE, new SourcePosition(start, scanner.getHalfPosition()));
				
			case IF:
				takeIt();
				take(L_PAREN);
				Expression ifE = parseExpression();
				take(R_PAREN);
				Statement ifTS = parseStatement();
				
				if(currToken.getType() == ELSE){
					takeIt();
					Statement ifFS = parseStatement();
					return new IfStmt(ifE, ifTS, ifFS, new SourcePosition(start, scanner.getHalfPosition()));
				}else{
					return new IfStmt(ifE, ifTS, new SourcePosition(start, scanner.getHalfPosition()));
				}
				
			case WHILE:
				takeIt();
				take(L_PAREN);
				Expression whileE = parseExpression();
				take(R_PAREN);
				Statement whileS = parseStatement();
				return new WhileStmt(whileE, whileS, new SourcePosition(start, scanner.getHalfPosition()));
			
			// The following very messy parts are for the last three rules.
			// int/bool part of first statement (missing iden):
			// (int|boolean) id = Expression;
			case INT:
			case BOOLEAN:
				TypeDenoter td = parseType();
				String name = take(IDEN).getValue();
				VarDecl vd = new VarDecl(td, name, new SourcePosition(start, scanner.getHalfPosition()));
				
				take(EQUALS);
				Expression val = parseExpression();
				take(SEMI);
				return new VarDeclStmt(vd, val, new SourcePosition(start, scanner.getHalfPosition()));
			
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
					
					return new IxAssignStmt(r, arrLoc, ixVal, new SourcePosition(start, scanner.getHalfPosition()));
				}else if(currToken.getType() == L_PAREN){ // third rule ::= Reference(ArgList?);
					takeIt();
					
					ExprList el = new ExprList();
					
					if(currToken.getType() != R_PAREN)
						el = parseArgList();
					
					take(R_PAREN);
					take(SEMI);
					return new CallStmt(r, el, new SourcePosition(start, scanner.getHalfPosition()));
				}else{ // second rule without [] ::= Reference = Expression;
					take(EQUALS);
					Expression asVal = parseExpression();
					take(SEMI);
					return new AssignStmt(r, asVal, new SourcePosition(start, scanner.getHalfPosition()));
				}
			
			case IDEN: // assuming iden
			default:
				Identifier id = new Identifier(take(IDEN));
				
				switch(currToken.getType()){
					case IDEN: // Type id = Expression;
						TypeDenoter td2 = new ClassType(id, new SourcePosition(start, scanner.getHalfPosition()));
						String name2 = takeIt().getValue();
						VarDecl varD = new VarDecl(td2, name2, new SourcePosition(start, scanner.getHalfPosition()));
						
						take(EQUALS);
						Expression e = parseExpression();
						take(SEMI);
						return new VarDeclStmt(varD, e, new SourcePosition(start, scanner.getHalfPosition()));
						
					case L_SQ_BRACK: // first or second rule with []
						takeIt();
						
						if(currToken.getType() != R_SQ_BRACK){ // second rule; Ref[Expr] = Expr;
							Reference idR = new IdRef(id, new SourcePosition(start, scanner.getHalfPosition()));
							Expression ix = parseExpression();
							take(R_SQ_BRACK);
							take(EQUALS);
							Expression newVal = parseExpression();
							take(SEMI);
							return new IxAssignStmt(idR, ix, newVal, new SourcePosition(start, scanner.getHalfPosition()));
						}else{ // first rule; Ref[] id = Expr;
							takeIt();
							// TODO: lmao the first SourcePosition is BS
							TypeDenoter td3 = new ClassType(id, new SourcePosition(start, scanner.getHalfPosition()));
							TypeDenoter td4 = new ArrayType(td3, new SourcePosition(start, scanner.getHalfPosition()));
							String name3 = take(IDEN).getValue();
							VarDecl vDecl = new VarDecl(td4, name3, new SourcePosition(start, scanner.getHalfPosition()));
							take(EQUALS);
							Expression newVal = parseExpression();
							take(SEMI);
							return new VarDeclStmt(vDecl, newVal, new SourcePosition(start, scanner.getHalfPosition()));
						}
					
					case DOT: // second or third rule
						Reference r2 = new IdRef(id, new SourcePosition(start, scanner.getHalfPosition()));
						takeIt();
						Identifier id2 = new Identifier(take(IDEN));
						r2 = new QualRef(r2, id2, new SourcePosition(start, scanner.getHalfPosition()));
						
						while(currToken.getType() == DOT){
							takeIt();
							Identifier id3 = new Identifier(take(IDEN));
							r2 = new QualRef(r2, id3, new SourcePosition(start, scanner.getHalfPosition()));
						}
						
						if(currToken.getType() == L_SQ_BRACK){ // second rule with [
							// like thing.q[5] = 5
							takeIt();
							Expression e2 = parseExpression();
							take(R_SQ_BRACK);
							
							
							take(EQUALS);
							Expression e3 = parseExpression();
							take(SEMI);
							return new IxAssignStmt(r2, e2, e3, new SourcePosition(start, scanner.getHalfPosition()));
						}else if(currToken.getType() == EQUALS){ // second without [
							// like thing.q = 5
							takeIt();
							Expression newVal = parseExpression();
							take(SEMI);
							return new AssignStmt(r2, newVal, new SourcePosition(start, scanner.getHalfPosition()));
						}else{ // third, like thing.q()
							take(L_PAREN);
							
							ExprList el = new ExprList();
							
							if(currToken.getType() != R_PAREN)
								el = parseArgList();
							
							take(R_PAREN);
							take(SEMI);
							return new CallStmt(r2, el, new SourcePosition(start, scanner.getHalfPosition()));
						}
					
					case EQUALS: // id = Exp;
						Reference ref = new IdRef(id, new SourcePosition(start, scanner.getHalfPosition()));
						takeIt();
						Expression e2 = parseExpression();
						take(SEMI);
						return new AssignStmt(ref, e2, new SourcePosition(start, scanner.getHalfPosition()));
					
					// assume ( for final case)
					case L_PAREN: // id(ArgList?)
					default:
						Reference ref2 = new IdRef(id, new SourcePosition(start, scanner.getHalfPosition()));
						take(L_PAREN);

						ExprList el = new ExprList();
						
						if(currToken.getType() != R_PAREN)
							el = parseArgList();

						takeIt();
						take(SEMI);
						return new CallStmt(ref2, el, new SourcePosition(start, scanner.getHalfPosition()));
				}
		}
	}
	
	/** Expression ::= OrExpression */
	private Expression parseExpression(){
		return parseOrExpression();
	}
	
	/** OrExpression ::= AndExpression (<strong>||</strong> AndExpression)* */
	private Expression parseOrExpression(){
		HalfPosition start = scanner.getHalfPosition();
		
		Expression ret = parseAndExpression();

		while(currToken.getType() == OR_LOG){
			Expression e1 = ret;
			Operator op = new Operator(currToken);
			
			takeIt();
			
			Expression e2 = parseAndExpression();
			SourcePosition pos = new SourcePosition(start, scanner.getHalfPosition());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** AndExpression ::= EqualityExpression (<strong>&&</strong> EqualityExpression)* */
	private Expression parseAndExpression(){
		HalfPosition start = scanner.getHalfPosition();
		
		Expression ret = parseEqualityExpression();

		while(currToken.getType() == AND_LOG){
			Expression e1 = ret;
			Operator op = new Operator(currToken);
			
			takeIt();
			
			Expression e2 = parseEqualityExpression();
			SourcePosition pos = new SourcePosition(start, scanner.getHalfPosition());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** EqualityExpression ::= RelationalExpression ((<strong>==</strong>|<strong>!=</strong>) RelationalExpression)* */
	private Expression parseEqualityExpression(){
		HalfPosition start = scanner.getHalfPosition();
		
		Expression ret = parseRelationalExpression();

		while(currToken.getType() == EQUALS_OP || currToken.getType() == NOT_EQUALS){
			Expression e1 = ret;
			Operator op = new Operator(currToken);
			
			takeIt();
			
			Expression e2 = parseRelationalExpression();
			SourcePosition pos = new SourcePosition(start, scanner.getHalfPosition());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** RelationalExpression ::= AdditiveExpression ((<strong>==</strong>|<strong>!=</strong>) AdditiveExpression)? */
	private Expression parseRelationalExpression(){
		HalfPosition start = scanner.getHalfPosition();
		
		Expression ret = parseAdditiveExpression();

		while(currToken.getType() == MORE_THAN || currToken.getType() == LESS_THAN || currToken.getType() == MORE_EQUAL || currToken.getType() == LESS_EQUAL){
			Expression e1 = ret;
			Operator op = new Operator(currToken);
			
			takeIt();
			
			Expression e2 = parseAdditiveExpression();
			SourcePosition pos = new SourcePosition(start, scanner.getHalfPosition());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** AdditiveExpression ::= MultiplicativeExpression ((<strong>+</strong>|<strong>-</strong>) MultiplicativeExpression)* */
	private Expression parseAdditiveExpression(){
		HalfPosition start = scanner.getHalfPosition();
		
		Expression ret = parseMultiplicativeExpression();

		while(currToken.getType() == PLUS || currToken.getType() == MINUS){
			Expression e1 = ret;
			Operator op = new Operator(currToken);
			
			takeIt();
			
			Expression e2 = parseMultiplicativeExpression();
			SourcePosition pos = new SourcePosition(start, scanner.getHalfPosition());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** MultiplicativeExpression ::= UnaryExpression ((<strong>*</strong>|<strong>/</strong>|<strong>%</strong>) UnaryExpression)* */
	private Expression parseMultiplicativeExpression(){
		HalfPosition start = scanner.getHalfPosition();
		
		Expression ret = parseUnaryExpression();

		while(currToken.getType() == TIMES || currToken.getType() == DIV || currToken.getType() == MOD){
			Expression e1 = ret;
			
			Operator op = new Operator(currToken);
			takeIt();
			
			Expression e2 = parseUnaryExpression();
			SourcePosition pos = new SourcePosition(start, scanner.getHalfPosition());
			
			ret = new BinaryExpr(op, e1, e2, pos);
		}
		
		return ret;
	}
	
	/** UnaryExpression ::= ((<strong>-</strong>|<strong>!</strong>) UnaryExpression) | PureExpression */
	private Expression parseUnaryExpression(){
		if(currToken.getType() == MINUS || currToken.getType() == NEG){
			HalfPosition start = scanner.getHalfPosition();
			
			Operator op = new Operator(currToken);
			takeIt();
			
			Expression e = parseUnaryExpression();
			SourcePosition pos = new SourcePosition(start, scanner.getHalfPosition());
			return new UnaryExpr(op, e, pos);
		}else
			return parsePureExpression();
	}
	
	/** PureExpression ::= <strong>(</strong>Expression<strong>)</strong><br />
			| <em>literal</em><br />
			| <strong>new</strong> (<strong>int[</strong>Expression<strong>]</strong>|<em>id</em>(<strong>(</strong>ArgList?</strong>)</strong>|<strong>[</strong>Expression<strong>]</strong>))<br />
			| Reference(<strong>[</strong>Expression<strong>]</strong>|<strong>(</strong>ArgList?<strong>)</strong>)?
	*/
	private Expression parsePureExpression(){
		HalfPosition start = scanner.getHalfPosition();
		
		switch(currToken.getType()){
			case L_PAREN:
				takeIt();
				Expression ret = parseExpression();
				take(R_PAREN);
				ret.posn = new SourcePosition(start, scanner.getHalfPosition());
				return ret;
			
			case NUM:
				Terminal il = new IntLiteral(currToken);
				takeIt();
				return new LiteralExpr(il, new SourcePosition(start, scanner.getHalfPosition()));
				
			case TRUE:
			case FALSE:
				Terminal bl = new BooleanLiteral(currToken);
				takeIt();
				return new LiteralExpr(bl, new SourcePosition(start, scanner.getHalfPosition()));
			
			case NULL:
				Terminal nl = new NullLiteral(currToken);
				takeIt();
				return new LiteralExpr(nl, new SourcePosition(start, scanner.getHalfPosition()));
				
			case NEW:
				takeIt();
				
				if(currToken.getType() == INT){
					TypeDenoter td = new BaseType(TypeKind.INT, new SourcePosition(start, scanner.getHalfPosition()));
					takeIt();
					take(L_SQ_BRACK);
					Expression e = parseExpression();
					take(R_SQ_BRACK);
					
					return new NewArrayExpr(td, e, new SourcePosition(start, scanner.getHalfPosition()));
				}else{
					Identifier i = new Identifier(take(IDEN));
					ClassType ct = new ClassType(i, new SourcePosition(start, scanner.getHalfPosition()));
					
					if(currToken.getType() == L_PAREN){ // new id()
						takeIt();
						
						ExprList el = new ExprList();
						
						if(currToken.getType() != R_PAREN)
							el = parseArgList();
						
						take(R_PAREN);
						return new NewObjectExpr(ct, el, new SourcePosition(start, scanner.getHalfPosition()));
					}else{ // new id[expr]
						take(L_SQ_BRACK);
						Expression e = parseExpression();
						take(R_SQ_BRACK);
						return new NewArrayExpr(ct, e, new SourcePosition(start, scanner.getHalfPosition()));
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
					return new IxExpr(r, e, new SourcePosition(start, scanner.getHalfPosition()));
				}else if(currToken.getType() == L_PAREN){
					takeIt();
					
					ExprList el = new ExprList();
					
					if(currToken.getType() != R_PAREN)
						el = parseArgList();
					
					take(R_PAREN);
					return new CallExpr(r, el, new SourcePosition(start, scanner.getHalfPosition()));
				}else
					return new RefExpr(r, new SourcePosition(start, scanner.getHalfPosition()));
		}
	}
	
	private Token take(TokenType expected) throws SyntacticAnalyzerException {
		Token ret = currToken;
		
		if(currToken.getType() == expected)
			currToken = scanner.scan();
		else
			throw new ParserException(expected, currToken.getType(), scanner);
		
		return ret;
	}
	
	private Token takeIt(){
		Token ret = currToken;
		currToken = scanner.scan();
		return ret;
	}
}
