package miniJava.ContextualAnalysis;

import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.SyntacticAnalyzer.SourcePosition;

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
	
	private void checkTypeDenoter(SourcePosition posn, TypeDenoter expected, TypeDenoter given){if(!expected.equals(given))
			reporter.addError("*** line " + posn.getStartLineNum() + ": expected " + expected + ", got " + given + "!");
	}
	
	private void expectedMethod(int lineNum){
		reporter.addError("*** line " + lineNum + ": expected a method!");
	}
	
	private void expectedArgs(int lineNum, MethodDecl md, int attemptedNumOfArgs){
		reporter.addError("*** line " + lineNum + ": tried to call " + md.name + " with " + attemptedNumOfArgs + " args, but it needed " + md.parameterDeclList.size() + "!");
	}
	
	private void expectedArrayType(Reference ref){
		reporter.addError("*** line " + ref.posn.getStartLineNum() + ": references " + ref.decl.name + " which is not an array!");
	}
	
	private void expectedBaseOrClassType(Reference ref){
		reporter.addError("*** line " + ref.posn.getStartLineNum() + ": references " + ref.decl.name + " is not a base type or class type!");
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
		return fd.type;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg){
		md.statementList.forEach(sl -> sl.visit(this, md));
		return md.type;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg){
		return pd.type;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg){
		return decl.type;
	}

	@Override
	public Object visitBaseType(BaseType type, Object arg){
		return null;
	}

	@Override
	public Object visitClassType(ClassType type, Object arg){
		return null;
	}

	@Override
	public Object visitArrayType(ArrayType type, Object arg){
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
		TypeDenoter declTD = (TypeDenoter)stmt.varDecl.visit(this, null);
		TypeDenoter	expTD = (TypeDenoter)stmt.initExp.visit(this, null);
		checkTypeDenoter(expTD.posn, declTD, expTD);
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg){
		if(stmt.ref.decl.type instanceof BaseType || stmt.ref.decl.type instanceof ClassType){
			TypeDenoter refTD = (TypeDenoter)stmt.ref.visit(this, null);
			TypeDenoter expTD = (TypeDenoter)stmt.val.visit(this, null);
			
			checkTypeDenoter(expTD.posn, refTD, expTD);
		}else{
			expectedBaseOrClassType(stmt.ref);
		}
		
		return null;
	}

	@Override
	public Object visitIxAssignStmt(IxAssignStmt stmt, Object arg){
		if(!(stmt.ref.decl.type instanceof ArrayType)){
			expectedArrayType(stmt.ref);
			return null;
		}
		
		TypeDenoter ixTD = (TypeDenoter)stmt.ix.visit(this, null);
		checkTypeKind(stmt.ix.posn, ixTD.typeKind, TypeKind.INT);
		
		TypeDenoter expTD = (TypeDenoter)stmt.exp.visit(this, null);
		checkTypeDenoter(expTD.posn, ((ArrayType)stmt.ref.decl.type).eltType, expTD);
		
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg){
		if(stmt.methodRef.decl instanceof MethodDecl){
			MethodDecl md = (MethodDecl)stmt.methodRef.decl;
			
			if(md.parameterDeclList.size() != stmt.argList.size()){
				expectedArgs(stmt.posn.getStartLineNum(), md, stmt.argList.size());
			}else{				
				for(int i = 0; i < md.parameterDeclList.size(); i++){
					Expression passedArg = stmt.argList.get(i);
					ParameterDecl param = md.parameterDeclList.get(i);
					
					checkTypeDenoter(passedArg.posn, (TypeDenoter)param.visit(this, null), (TypeDenoter)passedArg.visit(this, null));
				}
			}
		}else{
			expectedMethod(stmt.posn.getStartLineNum());
		}
		
		return null;
	}

	@Override
	public Object visitReturnStmt(ReturnStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		if(stmt.returnExpr != null) {
			TypeDenoter rTD = (TypeDenoter)stmt.returnExpr.visit(this, null);
			checkTypeDenoter(stmt.returnExpr.posn, md.type, rTD);
		}else
			checkTypeKind(stmt.posn, md.type.typeKind, TypeKind.VOID);
		
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		TypeDenoter condTD = (TypeDenoter)stmt.cond.visit(this, null);
		checkTypeKind(stmt.cond.posn, TypeKind.BOOLEAN, condTD.typeKind);
		
		stmt.thenStmt.visit(this, md);
		
		if(stmt.elseStmt != null)
			stmt.elseStmt.visit(this, md);
		
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		TypeDenoter condTD = (TypeDenoter)stmt.cond.visit(this, null);
		checkTypeKind(stmt.cond.posn, TypeKind.BOOLEAN, condTD.typeKind);

		stmt.body.visit(this, md);
		
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, Object arg){
		TypeDenoter e = (TypeDenoter)expr.expr.visit(this, null);
		
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
		TypeDenoter left = (TypeDenoter)expr.left.visit(this, null);
		TypeDenoter right = (TypeDenoter)expr.right.visit(this, null);
		
		switch(expr.operator.kind){
			case MORE_THAN:
			case LESS_THAN:
			case MORE_EQUAL:
			case LESS_EQUAL:
			case NOT_EQUALS:
				checkTypeKind(expr.left.posn, TypeKind.INT, left.typeKind);
				checkTypeKind(expr.right.posn, TypeKind.INT, right.typeKind);
				return new BaseType(TypeKind.BOOLEAN, expr.posn);
				
			case EQUALS_OP:
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
		TypeDenoter refType = (TypeDenoter)expr.ref.visit(this, null);
		
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
		if(expr.functionRef.decl instanceof MethodDecl){
			MethodDecl md = (MethodDecl)expr.functionRef.decl;
			
			if(md.parameterDeclList.size() != expr.argList.size()){
				expectedArgs(expr.posn.getStartLineNum(), md, expr.argList.size());
			}else{				
				for(int i = 0; i < md.parameterDeclList.size(); i++){
					Expression passedArg = expr.argList.get(i);
					ParameterDecl param = md.parameterDeclList.get(i);
					
					checkTypeDenoter(passedArg.posn, (TypeDenoter)param.visit(this, null), (TypeDenoter)passedArg.visit(this, null));
				}
				
				return md.type;
			}
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
		if(!expr.classtype.className.spelling.equals("String"))
			return expr.classtype;
		else
			return new BaseType(TypeKind.UNSUPPORTED, expr.posn);
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg){
		checkTypeDenoter(expr.sizeExpr.posn, new BaseType(TypeKind.INT, null), (TypeDenoter)expr.sizeExpr.visit(this, null));
		return new ArrayType(expr.eltType, expr.posn);
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg){
		return ref.decl.type;
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg){
		return ref.decl.type;
	}

	@Override
	public Object visitQRef(QualRef ref, Object arg){
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
