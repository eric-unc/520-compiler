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
	
	private boolean checkTypeDenoter(int lineNum, TypeDenoter expected, TypeDenoter given){
		if(expected instanceof ArrayType){
			if(expected.typeKind != given.typeKind || !(given instanceof ArrayType) || ((ArrayType)expected).eltType != ((ArrayType)given).eltType) {
				reporter.addError("*** line " + lineNum + ": expected " + expected + ", got " + given + "!");
				return false;
			}
		}else if(expected instanceof BaseType){
			if(expected.typeKind != given.typeKind || !(given instanceof BaseType)) {
				reporter.addError("*** line " + lineNum + ": expected " + expected + ", got " + given + "!");
				return false;
			}
		}else if(expected instanceof ClassType){
			if(expected.typeKind != given.typeKind || !(given instanceof ClassType) || ((ClassType)expected).className.spelling != ((ClassType)given).className.spelling) {
				reporter.addError("*** line " + lineNum + ": expected " + expected + ", got " + given + "!");
				return false;
			}	
		}
		
		return true;
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
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg){
		md.statementList.forEach(sl -> sl.visit(this, md));
		return null;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg){
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg){
		return null;
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
		stmt.sl.forEach(s -> s.visit(this, stmt));
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIxAssignStmt(IxAssignStmt stmt, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitReturnStmt(ReturnStmt stmt, Object arg){
		return stmt.returnExpr.visit(this, null);
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg){
		TypeDenoter condTD = (TypeDenoter)stmt.cond.visit(this, null);
		checkTypeKind(stmt.cond.posn, condTD.typeKind, TypeKind.BOOLEAN);
		
		stmt.thenStmt.visit(this, null);
		
		if(stmt.elseStmt != null)
			stmt.elseStmt.visit(this, null);
		
		return null;
	}

	

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg){
		TypeDenoter condTD = (TypeDenoter)stmt.cond.visit(this, null);
		checkTypeKind(stmt.cond.posn, condTD.typeKind, TypeKind.BOOLEAN);

		stmt.body.visit(this, null);
		
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitRefExpr(RefExpr expr, Object arg){
		// TODO Auto-generated method stub
		return null;
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
					
					checkTypeDenoter(passedArg.posn.getStartLineNum(), (TypeDenoter)param.visit(this, null), (TypeDenoter)passedArg.visit(this, null));
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
		return expr.classtype;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg){
		return new ArrayType(expr.eltType, expr.posn);
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg){
		return ref.decl.visit(this, null);
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg){
		return ref.decl.visit(this, null);
	}

	@Override
	public Object visitQRef(QualRef ref, Object arg){
		return ref.decl.visit(this, null);
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
		// TODO unsure
		return new BaseType(TypeKind.CLASS, nil.posn);
	}

}
