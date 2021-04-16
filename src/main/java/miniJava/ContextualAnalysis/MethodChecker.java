package miniJava.ContextualAnalysis;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.SyntacticAnalyzer.SourcePosition;

/**
 * Checks for:
 * <ul>
 * 	<li>A main method.</li>
 * 	<li>That each non-void method ends with a return.</li>
 * </ul>
 *
 */
public class MethodChecker implements Visitor<Object, Object> {
	private ErrorReporter reporter;
	
	public MethodChecker(Package ast, ErrorReporter reporter){
		this.reporter = reporter;
		ast.visit(this, null);
	}
	
	private void unexpectedMainMethod(SourcePosition other, SourcePosition first){
		reporter.addError("*** line " + other.getStartLineNum() + ": declared main method conflicts with first main method on line " + first.getStartLineNum() + "!");
	}
	
	private void emptyMethod(MethodDecl md){
		reporter.addError("*** line " + md.posn.getStartLineNum() + ": non-void method with empty body!");
	}
	
	private void expectedReturn(MethodDecl md, Statement s){
		reporter.addError("*** line " + s.posn.getStartLineNum() + ": last statement of method " + md.name + " (declared on line " + md.posn.getStartLineNum() + ") is not a return statement!");
	}
	
	@Override
	public Object visitPackage(Package prog, Object arg){
		MethodDecl firstMain = null;
		
		for(ClassDecl cd : prog.classDeclList){
			MethodDecl possibleMain = (MethodDecl)cd.visit(this, null);
			
			if(possibleMain != null){
				if(firstMain != null) // already found
					unexpectedMainMethod(possibleMain.posn, firstMain.posn);
				else
					firstMain = possibleMain;
			}
		}
		
		if(firstMain == null)
			reporter.addError("*** No main found!");
		else
			prog.main = firstMain;
		
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg){
		MethodDecl main = null;
		
		for(MethodDecl md : cd.methodDeclList) {
			md.visit(this, null);
			
			if(!md.isPrivate && md.isStatic && md.type.typeKind == TypeKind.VOID && md.name.equals("main") && md.parameterDeclList.size() == 1){
				ParameterDecl pd = md.parameterDeclList.get(0);
				
				if(pd.type.typeKind == TypeKind.ARRAY && pd.type instanceof ArrayType){
					TypeDenoter eltType = ((ArrayType)pd.type).eltType;
					
					if(eltType.typeKind == TypeKind.CLASS && eltType instanceof ClassType && ((ClassType)eltType).className.spelling.equals("String"))
						main = md;
				}
			}
		}
		
		return main;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg){
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg){
		if(md.type.typeKind != TypeKind.VOID){
			if(md.statementList.size() == 0)
				emptyMethod(md);
			else {
				Statement lastStatement = md.statementList.get(md.statementList.size() - 1);
				
				if(!(lastStatement instanceof ReturnStmt))
					expectedReturn(md, lastStatement);
			}
		}
		
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
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg){
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg){
		return null;
	}

	@Override
	public Object visitIxAssignStmt(IxAssignStmt stmt, Object arg){
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg){
		return null;
	}

	@Override
	public Object visitReturnStmt(ReturnStmt stmt, Object arg){
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg){
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg){
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, Object arg){
		return null;
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, Object arg){
		return null;
	}

	@Override
	public Object visitRefExpr(RefExpr expr, Object arg){
		return null;
	}

	@Override
	public Object visitIxExpr(IxExpr expr, Object arg){
		return null;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, Object arg){
		return null;
	}

	@Override
	public Object visitLiteralExpr(LiteralExpr expr, Object arg){
		return null;
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, Object arg){
		return null;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg){
		return null;
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg){
		return null;
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg){
		return null;
	}

	@Override
	public Object visitQRef(QualRef ref, Object arg){
		return null;
	}

	@Override
	public Object visitIdentifier(Identifier id, Object arg){
		return null;
	}

	@Override
	public Object visitOperator(Operator op, Object arg){
		return null;
	}

	@Override
	public Object visitIntLiteral(IntLiteral num, Object arg){
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, Object arg){
		return null;
	}

	@Override
	public Object visitNullLiteral(NullLiteral nil, Object arg){
		return null;
	}

}
