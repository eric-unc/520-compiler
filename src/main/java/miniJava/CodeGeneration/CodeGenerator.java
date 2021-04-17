package miniJava.CodeGeneration;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.mJAM.Machine;

import java.util.HashMap;

import static miniJava.mJAM.Machine.Op.*;
import static miniJava.mJAM.Machine.Reg.*;
import static miniJava.mJAM.Machine.Prim.*;

public class CodeGenerator implements Visitor<Object, Object> {
	@SuppressWarnings("unused")
	private ErrorReporter reporter;
	// the line to patch -> the method it should have
	private HashMap<Integer, MethodDecl> methodRefsToPatch = new HashMap<>();
	
	public CodeGenerator(Package ast, ErrorReporter reporter){
		this.reporter = reporter;
		Machine.initCodeGen(); // not sure if this is necessary or not.
		ast.visit(this, null);
		
		methodRefsToPatch.forEach((toPatchCBOffset, md) -> {
			System.out.println(toPatchCBOffset + " " + md + " " + md.runtimeDescriptor);
			
			if(md.runtimeDescriptor != null)
				Machine.patch(toPatchCBOffset, ((MethodDescriptor)md.runtimeDescriptor).cbOffset);
		});
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
		
		methodRefsToPatch.put(toPatch_mainCall, prog.main);
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg){
		cd.runtimeDescriptor = new ClassDescriptor();
		
		cd.fieldDeclList.forEach(fd -> {
			fd.visit(this, cd.runtimeDescriptor);
			
			if(fd.isStatic)
				((ClassDescriptor)cd.runtimeDescriptor).staticSize += ((VarDescriptor)fd.runtimeDescriptor).size;
			else
				((ClassDescriptor)cd.runtimeDescriptor).objectSize += ((VarDescriptor)fd.runtimeDescriptor).size;
		});
		
		cd.methodDeclList.forEach(md -> md.visit(this, null));
		
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg){
		ClassDescriptor rd = (ClassDescriptor)arg;
		
		VarDescriptor newVD = new VarDescriptor();
		newVD.size = 1; // TODO: see Machine.linkDataSize (which is 3)
		
		if(fd.isStatic){ 
			newVD.offset = rd.staticSize;
			rd.staticSize += newVD.size;
		}else{
			newVD.offset = rd.objectSize;
			rd.objectSize += newVD.size;
		}
		
		fd.runtimeDescriptor = newVD;
		
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg){
		md.runtimeDescriptor = new MethodDescriptor(Machine.nextInstrAddr());
		
		md.parameterDeclList.forEach(pd -> pd.visit(this, md));
		md.statementList.forEach(s -> s.visit(this, md));
		return null;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg){
		MethodDescriptor md = (MethodDescriptor)((MethodDecl)arg).runtimeDescriptor;
		
		VarDescriptor newVD = new VarDescriptor();
		newVD.size = 1; // TODO: see Machine.linkDataSize (which is 3)
		newVD.offset = md.size;
		md.size += newVD.size;
		
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg){
		// No real reason to visit this I think???
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
		MethodDecl md = (MethodDecl)arg;
		stmt.sl.forEach(s -> s.visit(this, md));
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg){
		// TODO: really not sure what to do here
		MethodDescriptor md = (MethodDescriptor)((MethodDecl)arg).runtimeDescriptor;
		
		VarDescriptor newVD = new VarDescriptor();
		newVD.size = 1; // TODO: see Machine.linkDataSize (which is 3)
		newVD.offset = md.size;
		md.size += newVD.size;
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		// TODO lol
		stmt.val.visit(this, md);
		VarDescriptor vd = (VarDescriptor)stmt.ref.decl.runtimeDescriptor;
		Machine.emit(STORE, OB, vd.offset);
		return null;
	}

	@Override
	public Object visitIxAssignStmt(IxAssignStmt stmt, Object arg){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		// each argument should be put onto the stack
		stmt.argList.forEach(_arg -> _arg.visit(this, md));
		
		// then, we perform that beautiful call
		MethodDecl calledMethod = (MethodDecl)stmt.methodRef.decl;
		int toPatch_methodCall = Machine.nextInstrAddr();
		
		// TODO: there's also a CALLD, no idea what it's for.
		if(calledMethod.isStatic)
			Machine.emit(CALL, CB, -1);
		else
			Machine.emit(CALLI, CB, -1);
		
		methodRefsToPatch.put(toPatch_methodCall, calledMethod);
		return null;
	}

	@Override
	public Object visitReturnStmt(ReturnStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		if(stmt.returnExpr != null){
			stmt.returnExpr.visit(this, md);
			Machine.emit(RETURN, 1, 0, md.parameterDeclList.size());
		}else
			Machine.emit(RETURN, 0, 0, md.parameterDeclList.size());
		
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg){ // dunno if this works or not
		MethodDecl md = (MethodDecl)arg;
		
		stmt.cond.visit(this, md);
		int toPatch_elseOrPastElse = Machine.nextInstrAddr();
		Machine.emit(JUMPIF, 0, CB, -1);
		stmt.thenStmt.visit(this, md);
		int toPatch_end = Machine.nextInstrAddr();
		Machine.emit(JUMP, CB, -1);
		
		if(stmt.elseStmt != null) {
			int _else = Machine.nextInstrAddr();
			stmt.elseStmt.visit(this, md);
			Machine.patch(toPatch_elseOrPastElse, _else);
		}else
			Machine.patch(toPatch_elseOrPastElse, Machine.nextInstrAddr());
		
		Machine.patch(toPatch_end, Machine.nextInstrAddr());
		
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		// jump to eval
		int toPatch_whileEval = Machine.nextInstrAddr();
		Machine.emit(JUMP, CB, -1);
		
		// body
		int bodyAddr = Machine.nextInstrAddr();
		stmt.body.visit(this, md);
		
		// eval conditional, and jump if true
		int whileEvalAddr = Machine.nextInstrAddr();
		stmt.cond.visit(this, md);
		Machine.emit(JUMPIF, 1, CB, bodyAddr);
		
		Machine.patch(toPatch_whileEval, whileEvalAddr);
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		expr.expr.visit(this, md);
		
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
		MethodDecl md = (MethodDecl)arg;
		
		expr.left.visit(this, md);
		expr.right.visit(this, md);
		
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
				Machine.emit(and);
				break;
				
			case OR_LOG:
				Machine.emit(or);
				break;
			
			case PLUS:
				Machine.emit(add);
				break;
				
			case MINUS:
				Machine.emit(sub);
				break;
				
			case TIMES:
				Machine.emit(mult);
				break;
				
			case DIV:
				Machine.emit(div);
				break;
			
			default:
				// should not happen
		}
		
		return null;
	}

	@Override
	public Object visitRefExpr(RefExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		expr.ref.visit(this, md);
		
		return null;
	}

	@Override
	public Object visitIxExpr(IxExpr expr, Object arg){
		expr.ixExpr.visit(this, null); // put ix on stack
		//expr.ref.decl
		// TODO not sure how to access array???
		return null;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		// each argument should be put onto the stack
		expr.argList.forEach(_arg -> _arg.visit(this, md));
		
		// then, we perform that beautiful call
		MethodDecl calledMethod = (MethodDecl)expr.functionRef.decl;
		int toPatch_methodCall = Machine.nextInstrAddr();
		
		// TODO: there's also a CALLD, no idea what it's for.
		if(calledMethod.isStatic)
			Machine.emit(CALL, CB, -1);
		else
			Machine.emit(CALLI, CB, -1);
		
		methodRefsToPatch.put(toPatch_methodCall, calledMethod);
		
		return null;
	}

	@Override
	public Object visitLiteralExpr(LiteralExpr expr, Object arg){
		expr.lit.visit(this, null);
		return null;
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, Object arg){
		ClassDecl cd = (ClassDecl)expr.classtype.classDecl;
		
		Machine.emit(LOADL, -1);
		Machine.emit(LOADL, ((ClassDescriptor)cd.runtimeDescriptor).objectSize);
		Machine.emit(newobj);
		
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
		// TODO WIP
		if(ref.decl instanceof LocalDecl){
			LocalDecl ld = (LocalDecl)ref.decl;
			//Machine.emit(LOAD, LB, ((VarDescriptor)ld.runtimeDescriptor).offset);
			Machine.emit(LOAD, LB, 0);
		}else if(ref.decl instanceof MemberDecl){
			MemberDecl md = (MemberDecl)ref.decl;
			
			if(md.isStatic)
				Machine.emit(LOAD, SB, ((VarDescriptor)md.runtimeDescriptor).offset);
			else
				Machine.emit(LOAD, OB, ((VarDescriptor)md.runtimeDescriptor).offset);
		}
		
		return null;
	}

	@Override
	public Object visitQRef(QualRef ref, Object arg){
		//ref.decl.visit(this, null); // TODO: this definitely isn't right
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
		switch(bool.spelling){
			case "true":
				Machine.emit(LOADL, Machine.trueRep);
				break;
				
			case "false":
				Machine.emit(LOADL, Machine.falseRep);
				break;
				
			default:
				// Should not happen
		}
		
		return null;
	}

	@Override
	public Object visitNullLiteral(NullLiteral nil, Object arg){
		Machine.emit(LOADL, Machine.nullRep);
		return null;
	}

}
