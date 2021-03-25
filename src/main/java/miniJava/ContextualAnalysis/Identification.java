package miniJava.ContextualAnalysis;

import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;

public class Identification implements Visitor<Object, Object> {
	private IdentificationTable table;
	//private ErrorReporter reporter;
	
	public Identification(Package ast, ErrorReporter reporter){
		//this.reporter = reporter;
		this.table = new IdentificationTable(reporter);
		ast.visit(this, null);
	}

	@Override
	public Object visitPackage(Package prog, Object arg){
		prog.classDeclList.forEach(table::enter);
		
		prog.classDeclList.forEach(cd -> cd.visit(this, null));
		
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg){
		table.openScope();
		
		cd.fieldDeclList.forEach(table::enter);
		cd.methodDeclList.forEach(table::enter);
		
		cd.fieldDeclList.forEach(fd -> fd.visit(this, null));
		cd.methodDeclList.forEach(md -> md.visit(this, null));
		
		table.closeScope();
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg){
		fd.type.visit(this, null);
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg){
		md.type.visit(this, null);
		
		table.openScope();
		md.parameterDeclList.forEach(pd -> pd.visit(this, null));
		
		table.openScope();
		md.statementList.forEach(sl -> sl.visit(this, null));
		
		table.closeScope();
		table.closeScope();
		return null;
	}

	// This is the start of stuff purely implemented by me
	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg){
		pd.type.visit(this, null);
		table.enter(pd);
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg){
		decl.type.visit(this, null);
		table.enter(decl);
		return null;
	}

	@Override
	public Object visitBaseType(BaseType type, Object arg){
		// I think we don't do anything here?
		return null;
	}

	@Override
	public Object visitClassType(ClassType type, Object arg){
		table.retrieve(type.className);
		return null;
	}

	@Override
	public Object visitArrayType(ArrayType type, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBlockStmt(BlockStmt stmt, Object arg){
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg){
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitLiteralExpr(LiteralExpr expr, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitQRef(QualRef ref, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIdentifier(Identifier id, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitOperator(Operator op, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitIntLiteral(IntLiteral num, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBooleanLiteral(BooleanLiteral bool, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitNullLiteral(NullLiteral nil, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

}
