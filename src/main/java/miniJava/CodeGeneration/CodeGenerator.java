package miniJava.CodeGeneration;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.mJAM.Machine;

import static miniJava.mJAM.Machine.Op.*;
import static miniJava.mJAM.Machine.Reg.*;
import static miniJava.mJAM.Machine.Prim.*;

public class CodeGenerator implements Visitor<Object, Object> {
	@SuppressWarnings("unused")
	private ErrorReporter reporter;
	
	public CodeGenerator(Package ast, ErrorReporter reporter){
		this.reporter = reporter;
		Machine.initCodeGen(); // not sure if this is necessary or not.
		ast.visit(this, null);
	}

	@Override
	public Object visitPackage(Package prog, Object arg){
		// creates a new array of size 0 to pass to main
		Machine.emit(LOADL, 0);
		Machine.emit(newarr);
		
		// calling main
		int toPatch_mainCall = Machine.nextInstrAddr();
		Machine.emit(CALL, CB, -1);
		Machine.emit(HALT);
		
		prog.classDeclList.forEach(c -> c.visit(this, null));
		
		// TODO: patch main
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitBaseType(BaseType type, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitClassType(ClassType type, Object arg){
		// TODO Auto-generated method stub
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
		expr.expr.visit(this, null);
		
		switch(expr.operator.kind){
			case NEG:
				Machine.emit(not);
				break;
				
			case MINUS:
				Machine.emit(neg);
				break;
				
			default:
				// this shouldn't happen?
		}
		
		return null;
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, Object arg){
		expr.left.visit(this, null);
		expr.right.visit(this, null);
		
		switch(expr.operator.kind){
			case MORE_THAN:
				Machine.emit(gt);
				break;
				
			case LESS_THAN:
				Machine.emit(lt);
				break;
				
			case MORE_EQUAL:
				Machine.emit(ge);
				break;
				
			case LESS_EQUAL:
				Machine.emit(le);
				break;
				
			case NOT_EQUALS:
				Machine.emit(ne);
				break;
				
			case EQUALS_OP:
				Machine.emit(eq);
				break;
				
			case AND_LOG:
			case OR_LOG:
			
			case PLUS:
			case MINUS:
			case TIMES:
			case DIV:
			
			default:
				// should not happen
		}
		
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
		expr.lit.visit(this, null);
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
		return null;
	}

	@Override
	public Object visitIntLiteral(IntLiteral num, Object arg){
		Machine.emit(LOADL, Integer.parseInt(num.spelling));
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
