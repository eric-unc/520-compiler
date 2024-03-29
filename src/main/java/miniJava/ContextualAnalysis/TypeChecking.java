package miniJava.ContextualAnalysis;

import java.util.ArrayList;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.SyntacticAnalyzer.SourcePosition;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

public class TypeChecking implements Visitor<Object, Object> {
	private ErrorReporter reporter;
	
	public TypeChecking(Package ast, ErrorReporter reporter){
		this.reporter = reporter;
		ast.visit(this, null);
	}
	
	private boolean checkTypeKind(SourcePosition posn, TypeKind expected, TypeKind given){
		if(expected != given){
			reporter.addError("*** line " + posn.getStartLineNum() + ": expected " + expected + ", got " + given + "!");
			return false;
		}
		
		return true;
	}
	
	private void checkTypeDenoter(SourcePosition posn, TypeDenoter expected, TypeDenoter given){
		if(!expected.equals(given))
			reporter.addError("*** line " + posn.getStartLineNum() + ": expected " + expected.toPrettyString() + ", got " + given.toPrettyString() + "!");
	}
	
	private void expectedNonNullTD(SourcePosition posn) {
		reporter.addError("*** line " + posn.getStartLineNum() + ": expected a non-null type!");
	}
	
	private void checkClass(ClassType ct){
		if(!(ct.classDecl instanceof ClassDecl))
			reporter.addError("*** line " + ct.posn.getStartLineNum() + ": expected a class declaration!");
	}
	
	private void checkNotClassOrVarDecl(Declaration decl){
		checkNotClassDecl(decl);
		checkNotMethodDecl(decl);
	}
	
	private void checkNotClassDecl(Declaration decl){
		if(decl instanceof ClassDecl)
			reporter.addError("*** line " + decl.posn.getStartLineNum() + ": is a class declaration, not a variable!");
	}
	
	private void checkNotMethodDecl(Declaration decl){
		if(decl instanceof MethodDecl)
			reporter.addError("*** line " + decl.posn.getStartLineNum() + ": is a method declaration, not a variable!");
	}
	
	private void checkNotSolitaryDeclaration(Statement s){
		if(s instanceof VarDeclStmt)
			reporter.addError("*** line " + s.posn.getStartLineNum() + ": is an unpermitted solitary variable declaration!");
	}
	
	private void expectedMethod(int lineNum){
		reporter.addError("*** line " + lineNum + ": expected a method!");
	}
	
	private void expectedMatchingMethod(int lineNum) {
		reporter.addError("*** line " + lineNum + ": expected a method match for the number of parameters and types given!");
	}
	
	private void expectedArgs(int lineNum, MethodDecl md, int attemptedNumOfArgs){
		reporter.addError("*** line " + lineNum + ": tried to call " + ContextualAnalysis.localizeDeclName(md) + " with " + attemptedNumOfArgs + " args, but it needed " + md.parameterDeclList.size() + "!");
	}
	
	private void expectedArrayType(Reference ref){
		reporter.addError("*** line " + ref.posn.getStartLineNum() + ": references " + ref.decl.name + " which is not an array!");
	}
	
	private void expectedNonFinalVariable(QualRef ref){
		reporter.addError("*** line " + ref.posn.getStartLineNum() + ": references " + ref.decl.name + " which is a private field!");
	}
	
	private void expectedValidOperator(Operator operator){
		reporter.addError("*** line " + operator.posn.getStartLineNum() + ": uses unexpected operator " + operator.spelling + "!");
	}

	@Override
	public Object visitPackage(Package prog, Object arg){
		prog.classDeclList.forEach(cd -> cd.visit(this, null));
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg){
		cd.fieldDeclList.forEach(fd -> fd.visit(this, null));
		cd.methodDeclList.forEach(md -> md.visit(this, null));
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg){
		if(fd.initExpression != null){
			TypeDenoter initExprTD = (TypeDenoter)fd.initExpression.visit(this, null);
			checkTypeDenoter(fd.initExpression.posn, fd.type, initExprTD);
		}
		
		return fd.type;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg){
		md.parameterDeclList.forEach(pd -> pd.visit(this, md));
		md.statementList.forEach(s -> s.visit(this, md));
		return md.type;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg){
		pd.type.visit(this, null);
		return pd.type;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg){
		if(decl.type instanceof ClassType && ((ClassType)decl.type).className.spelling.equals("String"))
			return new BaseType(TypeKind.UNSUPPORTED, decl.posn);
		else
			return decl.type;
	}

	@Override
	public Object visitBaseType(BaseType type, Object arg){
		return null;
	}

	@Override
	public Object visitClassType(ClassType type, Object arg){
		checkClass(type);
		return null;
	}

	@Override
	public Object visitArrayType(ArrayType type, Object arg){
		type.eltType.visit(this, null);
		return null;
	}

	@Override
	public Object visitBlockStmt(BlockStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		stmt.sl.forEach(s -> s.visit(this, md));
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		TypeDenoter declTD = (TypeDenoter)stmt.varDecl.visit(this, md);
		TypeDenoter	expTD = (TypeDenoter)stmt.initExp.visit(this, md);
		
		if(expTD != null)
			checkTypeDenoter(expTD.posn, declTD, expTD);
		else
			expectedNonNullTD(stmt.posn);
		
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		if(stmt.ref instanceof QualRef && stmt.ref.decl.name.equals("length") && ((QualRef)stmt.ref).ref.decl.type.typeKind == TypeKind.ARRAY){
			expectedNonFinalVariable((QualRef)stmt.ref);
			return null;
		}
			
		TypeDenoter refTD = (TypeDenoter)stmt.ref.visit(this, md);
		TypeDenoter expTD = (TypeDenoter)stmt.val.visit(this, md);
			
		checkTypeDenoter(expTD.posn, refTD, expTD);
		
		return null;
	}

	@Override
	public Object visitIxAssignStmt(IxAssignStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		if(!(stmt.ref.decl.type instanceof ArrayType)){
			expectedArrayType(stmt.ref);
			return null;
		}
		
		TypeDenoter ixTD = (TypeDenoter)stmt.ix.visit(this, md);
		checkTypeKind(stmt.ix.posn, ixTD.typeKind, TypeKind.INT);
		
		TypeDenoter expTD = (TypeDenoter)stmt.exp.visit(this, md);
		checkTypeDenoter(expTD.posn, ((ArrayType)stmt.ref.decl.type).eltType, expTD);
		
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg){
		MethodDecl callingFrom = (MethodDecl)arg;
		
		if(stmt.methodRef.decl instanceof MethodDecl){
			MethodDecl md = (MethodDecl)stmt.methodRef.decl;
			
			if(md.parameterDeclList.size() != stmt.argList.size()){
				expectedArgs(stmt.posn.getStartLineNum(), md, stmt.argList.size());
			}else{				
				for(int i = 0; i < md.parameterDeclList.size(); i++){
					Expression passedArg = stmt.argList.get(i);
					ParameterDecl param = md.parameterDeclList.get(i);
					
					checkTypeDenoter(passedArg.posn, (TypeDenoter)param.visit(this, null), (TypeDenoter)passedArg.visit(this, callingFrom));
				}
			}
		}else if(stmt.methodRef.decl instanceof MultiMethodDecl){
			MultiMethodDecl mmd = (MultiMethodDecl)stmt.methodRef.decl;
			MethodDecl found = null;
			
			// I only want to visit each expression once
			ArrayList<TypeDenoter> argListTypes = new ArrayList<>();
			stmt.argList.forEach(e -> argListTypes.add((TypeDenoter)e.visit(this, callingFrom)));
			
			
			for(MethodDecl toTry : mmd.possibleDecls)
				if(toTry.parameterDeclList.size() == argListTypes.size()){
					boolean matches = true;
					
					for(int i = 0; i < toTry.parameterDeclList.size(); i++)
						if(!toTry.parameterDeclList.get(i).type.equals(argListTypes.get(i))) {
							matches = false;
							break;
						}
					
					if(matches){
						found = toTry;
						break;
					}
				}
			
			if(found == null){
				expectedMatchingMethod(stmt.posn.getStartLineNum());
				return null;
			}
			
			stmt.methodRef.decl = found;
			
			if(callingFrom.isStatic && !found.isStatic)
				reporter.addError("*** line " + stmt.methodRef.posn.getStartLineNum() + ": attempts to reference non-static " + found.name + " on line " + found.posn.getStartLineNum() + "!");
			
			if(callingFrom.isPrivate && callingFrom.inClass != found.inClass)
				reporter.addError("*** line " + stmt.methodRef.posn.getStartLineNum() + ": attempts to reference private " + found.name + " on line " + found.posn.getStartLineNum() + "!");
		}else
			expectedMethod(stmt.posn.getStartLineNum());
		
		return null;
	}

	@Override
	public Object visitReturnStmt(ReturnStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		if(stmt.returnExpr != null) {
			TypeDenoter rTD = (TypeDenoter)stmt.returnExpr.visit(this, md);
			checkTypeDenoter(stmt.returnExpr.posn, md.type, rTD);
		}else
			checkTypeKind(stmt.posn, md.type.typeKind, TypeKind.VOID);
		
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		TypeDenoter condTD = (TypeDenoter)stmt.cond.visit(this, md);
		checkTypeKind(stmt.cond.posn, TypeKind.BOOLEAN, condTD.typeKind);
		
		checkNotSolitaryDeclaration(stmt.thenStmt);
		stmt.thenStmt.visit(this, md);
		
		if(stmt.elseStmt != null){
			checkNotSolitaryDeclaration(stmt.elseStmt);
			stmt.elseStmt.visit(this, md);
		}
		
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		TypeDenoter condTD = (TypeDenoter)stmt.cond.visit(this, md);
		checkTypeKind(stmt.cond.posn, TypeKind.BOOLEAN, condTD.typeKind);

		checkNotSolitaryDeclaration(stmt.body);
		stmt.body.visit(this, md);
		
		return null;
	}
	
	@Override
	public Object visitForStmt(ForStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		if(stmt.initStmt != null)
			stmt.initStmt.visit(this, md);
		
		if(stmt.cond != null){
			TypeDenoter condTD = (TypeDenoter)stmt.cond.visit(this, md);
			checkTypeKind(stmt.cond.posn, TypeKind.BOOLEAN, condTD.typeKind);
		}
		
		if(stmt.increStmt != null)
			stmt.increStmt.visit(this, md);
		
		checkNotSolitaryDeclaration(stmt.body);
		stmt.body.visit(this, md);
		
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		TypeDenoter e = (TypeDenoter)expr.expr.visit(this, md);
		
		switch(expr.operator.kind){
			case NEG:
				checkTypeKind(e.posn, TypeKind.BOOLEAN, e.typeKind);
				return new BaseType(TypeKind.BOOLEAN, expr.posn);
			case MINUS:
				checkTypeKind(e.posn, TypeKind.INT, e.typeKind);
				return new BaseType(TypeKind.INT, expr.posn);
				
			default:
				expectedValidOperator(expr.operator);
				return new BaseType(TypeKind.ERROR, expr.posn);
		}
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		TypeDenoter left = (TypeDenoter)expr.left.visit(this, md);
		TypeDenoter right = (TypeDenoter)expr.right.visit(this, md);
		
		switch(expr.operator.kind){
			case MORE_THAN:
			case LESS_THAN:
			case MORE_EQUAL:
			case LESS_EQUAL:
				checkTypeKind(expr.left.posn, TypeKind.INT, left.typeKind);
				checkTypeKind(expr.right.posn, TypeKind.INT, right.typeKind);
				return new BaseType(TypeKind.BOOLEAN, expr.posn);
			
			case EQUALS_OP:
			case NOT_EQUALS:
				checkTypeDenoter(expr.posn, left, right);
				return new BaseType(TypeKind.BOOLEAN, expr.posn);
				
			case AND_LOG:
			case OR_LOG:
				checkTypeKind(expr.left.posn, TypeKind.BOOLEAN, left.typeKind);
				checkTypeKind(expr.right.posn, TypeKind.BOOLEAN, right.typeKind);
				return new BaseType(TypeKind.BOOLEAN, expr.posn);
			
			case PLUS:
			case MINUS:
			case TIMES:
			case DIV:
			case MOD:
				checkTypeKind(expr.left.posn, TypeKind.INT, left.typeKind);
				checkTypeKind(expr.right.posn, TypeKind.INT, right.typeKind);
				return new BaseType(TypeKind.INT, expr.posn);
			
			default:
				expectedValidOperator(expr.operator);
				return new BaseType(TypeKind.ERROR, expr.posn);
		}
	}

	@Override
	public Object visitRefExpr(RefExpr expr, Object arg){
		return expr.ref.visit(this, null);
	}

	@Override
	public Object visitIxExpr(IxExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		TypeDenoter refType = (TypeDenoter)expr.ref.visit(this, md);
		
		if(!(refType instanceof ArrayType)){
			expectedArrayType(expr.ref);
			return new BaseType(TypeKind.ERROR, expr.posn);
		}
		
		ArrayType at = (ArrayType)refType;
		
		checkTypeKind(expr.ixExpr.posn, ((TypeDenoter)expr.ixExpr.visit(this, null)).typeKind, TypeKind.INT);
		
		return at.eltType;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, Object arg){
		MethodDecl callingFrom = (MethodDecl)arg;
		
		if(expr.functionRef.decl instanceof MethodDecl){
			MethodDecl md = (MethodDecl)expr.functionRef.decl;
			
			if(md.parameterDeclList.size() != expr.argList.size()){
				expectedArgs(expr.posn.getStartLineNum(), md, expr.argList.size());
			}else{				
				for(int i = 0; i < md.parameterDeclList.size(); i++){
					Expression passedArg = expr.argList.get(i);
					ParameterDecl param = md.parameterDeclList.get(i);
					
					checkTypeDenoter(passedArg.posn, (TypeDenoter)param.visit(this, null), (TypeDenoter)passedArg.visit(this, callingFrom));
				}
				
				return md.type;
			}
		}else if(expr.functionRef.decl instanceof MultiMethodDecl){
			MultiMethodDecl mmd = (MultiMethodDecl)expr.functionRef.decl;
			MethodDecl found = null;
				
			// I only want to visit each expression once
			ArrayList<TypeDenoter> argListTypes = new ArrayList<>();
			expr.argList.forEach(e -> argListTypes.add((TypeDenoter)e.visit(this, callingFrom)));

			for(MethodDecl toTry : mmd.possibleDecls)
				if(toTry.parameterDeclList.size() == argListTypes.size()){
					boolean matches = true;
						
					for(int i = 0; i < toTry.parameterDeclList.size(); i++)
						if(!toTry.parameterDeclList.get(i).type.equals(argListTypes.get(i))) {
							matches = false;
							break;
						}
						
					if(matches){
						found = toTry;
						break;
					}
				}
				
			if(found == null) {
				expectedMatchingMethod(expr.posn.getStartLineNum());
				return null;
			}
			
			expr.functionRef.decl = found;
			
			if(callingFrom.isStatic && !found.isStatic)
				reporter.addError("*** line " + expr.functionRef.posn.getStartLineNum() + ": attempts to reference non-static " + found.name + " on line " + found.posn.getStartLineNum() + "!");
			
			if(callingFrom.isPrivate && callingFrom.inClass != found.inClass)
				reporter.addError("*** line " + expr.functionRef.posn.getStartLineNum() + ": attempts to reference private " + found.name + " on line " + found.posn.getStartLineNum() + "!");
			
			
			return found.type;
			
		}else{
			expectedMethod(expr.posn.getStartLineNum());
		}
		
		return new BaseType(TypeKind.ERROR, expr.posn);
	}

	@Override
	public Object visitLiteralExpr(LiteralExpr expr, Object arg){
		return expr.lit.visit(this, null);
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, Object arg){
		if(expr.classtype.className.spelling.equals("String"))
			return new BaseType(TypeKind.UNSUPPORTED, expr.posn);
		
		ClassDecl cd = (ClassDecl)expr.classtype.classDecl;
		
		// actual identification happens in MethodChecker which maybe isn't a great idea but oh well.
		for(MethodDecl md : cd.methodDeclList)
			if(md.name.equals("_constructor")){
				if(md.parameterDeclList.size() != expr.argList.size()){
					expectedArgs(expr.posn.getStartLineNum(), md, expr.argList.size());
				}else{
					for(int i = 0; i < md.parameterDeclList.size(); i++){
						Expression passedArg = expr.argList.get(i);
						ParameterDecl param = md.parameterDeclList.get(i);
						
						checkTypeDenoter(passedArg.posn, (TypeDenoter)param.visit(this, null), (TypeDenoter)passedArg.visit(this, null));
					}
				}
				
				break;
			}
		
		return expr.classtype;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg){
		checkTypeDenoter(expr.sizeExpr.posn, new BaseType(TypeKind.INT, null), (TypeDenoter)expr.sizeExpr.visit(this, null));
		return new ArrayType(expr.eltType, expr.posn);
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg){
		// yes, this is very hacky, i am sorry
		ClassDecl cd = (ClassDecl)ref.decl;
		return new ClassType(new Identifier(new Token(TokenType.IDEN, cd.name, cd.posn), cd), cd.posn);
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg){
		checkNotClassOrVarDecl(ref.decl);
		return ref.decl.type != null ? ref.decl.type : new BaseType(TypeKind.ERROR, ref.posn);
	}

	@Override
	public Object visitQRef(QualRef ref, Object arg){
		checkNotMethodDecl(ref.decl);
		checkNotMethodDecl(ref.ref.decl);
		return ref.decl.type;
	}

	@Override
	public Object visitIdentifier(Identifier id, Object arg){
		return id.decl.visit(this, null);
	}

	@Override
	public Object visitOperator(Operator op, Object arg){
		return null;
	}

	@Override
	public Object visitIntLiteral(IntLiteral num, Object arg){
		return new BaseType(TypeKind.INT, num.posn);
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, Object arg){
		return new BaseType(TypeKind.BOOLEAN, bool.posn);
	}

	@Override
	public Object visitNullLiteral(NullLiteral nil, Object arg){
		return new BaseType(TypeKind.CLASS, nil.posn);
	}
}
