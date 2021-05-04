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
	
	private int staticSize = 0;
	
	public CodeGenerator(Package ast, ErrorReporter reporter){
		this.reporter = reporter;
		Machine.initCodeGen(); // not sure if this is necessary or not.
		ast.visit(this, null);
		
		methodRefsToPatch.forEach((toPatchCBOffset, md) -> 
			Machine.patch(toPatchCBOffset, ((MethodDescriptor)md.runtimeDescriptor).cbOffset));
	}
	
	private static enum ClassDeclVisitStage {
		STATIC_BLOCK,
		FIELDS,
		FIELDS_INIT,
		METHODS,
	}

	@Override
	public Object visitPackage(Package prog, Object arg){
		prog.classDeclList.forEach(c -> c.visit(this, ClassDeclVisitStage.FIELDS));
		prog.classDeclList.forEach(c -> c.visit(this, ClassDeclVisitStage.FIELDS_INIT));
		
		// generate calls to static blocks
		prog.classDeclList.forEach(c -> c.visit(this, ClassDeclVisitStage.STATIC_BLOCK));
		
		// creates a new array of size 0 to pass to main
		Machine.emit(LOADL, 0);
		Machine.emit(newarr);
		
		// calling main
		int toPatch_mainCall = Machine.nextInstrAddr();
		Machine.emit(CALL, CB, -1);
		Machine.emit(HALT);
		
		prog.classDeclList.forEach(c -> c.visit(this, ClassDeclVisitStage.METHODS));
		
		methodRefsToPatch.put(toPatch_mainCall, prog.main);
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg){
		ClassDeclVisitStage stage = (ClassDeclVisitStage)arg;
		
		switch(stage) {
			case STATIC_BLOCK:
				if(cd.staticBlockDecl != null){
					int toPatch_staticBlockCall = Machine.nextInstrAddr();
					Machine.emit(CALL, CB, -1);
					
					methodRefsToPatch.put(toPatch_staticBlockCall, cd.staticBlockDecl);
				}
				
				break;
				
			case FIELDS:
				cd.runtimeDescriptor = new ClassDescriptor();
				
				cd.fieldDeclList.forEach(fd ->  fd.visit(this, cd.runtimeDescriptor));
				break;
			
			case FIELDS_INIT:
				cd.fieldDeclList.forEach(fd ->  fd.visit(this, null));
				break;
				
			case METHODS:
				// creates a new constructor if there is none
				if(cd.constructorDecl == null){
					cd.constructorDecl = new ConstructorDecl(false, cd.name, new ParameterDeclList(), new StatementList(), null);
					//cd.constructorDecl.runtimeDescriptor = new MethodDescriptor(0);
					cd.methodDeclList.add(cd.constructorDecl);
					cd.constructorDecl.inClass = cd;
				}
				
				cd.methodDeclList.forEach(md -> md.visit(this, null));
				break;
		}
		
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg){
		if(arg instanceof ClassDescriptor) {
			ClassDescriptor rd = (ClassDescriptor)arg;
			
			VarDescriptor newVD = new VarDescriptor();
			newVD.size = 1;
			
			if(fd.isStatic){
				newVD.offset = staticSize;
				staticSize += newVD.size;
			}else{
				newVD.offset = rd.objectSize;
				rd.objectSize += newVD.size;
			}
			
			fd.runtimeDescriptor = newVD;
		}else{
			if(fd.isStatic){
				if(fd.initExpression != null) {
					fd.initExpression.visit(this, null);
				}else
					Machine.emit(PUSH, 1); // an "empty" stack value
			}
		}
		
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg){
		md.runtimeDescriptor = new MethodDescriptor(Machine.nextInstrAddr());
		
		md.parameterDeclList.forEach(pd -> pd.visit(this, md));
		
		if(md.name.equals("_constructor") && md.inClass.toInitialize != null){ // yes, this is hacky, 
			md.inClass.toInitialize.forEach(fd -> {
				fd.initExpression.visit(this, null);
				Machine.emit(STORE, OB, ((VarDescriptor)fd.runtimeDescriptor).offset);
			});
			
			if(md.posn == null){ // then it's a virtual method
				Machine.emit(RETURN, 0, 0, 0);
			}
		}
		
		md.statementList.forEach(s -> s.visit(this, md));
		return null;
	}

	@Override
	public Object visitParameterDecl(ParameterDecl pd, Object arg){
		MethodDecl decl = ((MethodDecl)arg);
		MethodDescriptor md = (MethodDescriptor)decl.runtimeDescriptor;
		
		VarDescriptor newVD = new VarDescriptor();
		newVD.size = 1;
		newVD.offset = (-1 * decl.parameterDeclList.size()) +  md.argsize;
		md.argsize += newVD.size;
		
		pd.runtimeDescriptor = newVD;
		
		return null;
	}

	@Override
	public Object visitVarDecl(VarDecl decl, Object arg){
		MethodDescriptor md = (MethodDescriptor)((MethodDecl)arg).runtimeDescriptor;
		
		VarDescriptor newVD = new VarDescriptor();
		newVD.size = 1;
		newVD.offset = md.size;
		md.size += newVD.size;
		
		decl.runtimeDescriptor = newVD;
		
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
		
		int originalSize = ((MethodDescriptor)md.runtimeDescriptor).size;
		
		stmt.sl.forEach(s -> s.visit(this, md));
		
		int added = ((MethodDescriptor)md.runtimeDescriptor).size - originalSize;
		
		if(added != 0){
			Machine.emit(POP, added);
			((MethodDescriptor)md.runtimeDescriptor).size = originalSize;
		}
		
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		stmt.varDecl.visit(this, md);
		
		if(stmt.initExp != null)
			stmt.initExp.visit(this, md);
		else
			Machine.emit(PUSH, 1); // an "empty" value
		
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		VarDescriptor vd = (VarDescriptor)stmt.ref.decl.runtimeDescriptor;
		
		if(stmt.ref instanceof IdRef){
			stmt.val.visit(this, md);
			
			if(stmt.ref.decl instanceof LocalDecl)
				Machine.emit(STORE, LB, vd.offset);
			else if(stmt.ref.decl instanceof FieldDecl){
				FieldDecl fd = (FieldDecl)stmt.ref.decl;
				Machine.emit(STORE, fd.isStatic ? SB : OB, vd.offset);
			}
		}else if(stmt.ref instanceof QualRef){
			FieldDecl fd = (FieldDecl)stmt.ref.decl;
			
			if(fd.isStatic){
				stmt.val.visit(this, md);
				Machine.emit(STORE, SB, vd.offset);
			}else{
				stmt.ref.visit(this, true); // get address of ref, field index
				stmt.val.visit(this, md);
				Machine.emit(fieldupd);
			}
		} // other option is ThisRef which won't get past contextual analysis
		
		return null;
	}

	@Override
	public Object visitIxAssignStmt(IxAssignStmt stmt, Object arg){
		stmt.ref.visit(this, null); // put array onto stack
		stmt.ix.visit(this, null); // put desired index onto stack
		stmt.exp.visit(this, null); // put new value onto stack
		
		Machine.emit(arrayupd);
		
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		// each argument should be put onto the stack
		stmt.argList.forEach(_arg -> _arg.visit(this, null));
		
		MethodDecl calledMethod = (MethodDecl)stmt.methodRef.decl;
		
		if(!calledMethod.isStatic){ // but we may need to put the object on stack first
			if(stmt.methodRef instanceof QualRef && !(((QualRef)stmt.methodRef).ref instanceof ThisRef)) 
				stmt.methodRef.visit(this, null);
			else
				Machine.emit(LOADA, OB, 0);
		}
		
		if(calledMethod.inClass != null) {
			int toPatch_methodCall = Machine.nextInstrAddr();
			Machine.emit(calledMethod.isStatic ? CALL : CALLI, CB, -1);
			methodRefsToPatch.put(toPatch_methodCall, calledMethod);
			
			if(md.type.typeKind != TypeKind.VOID)
				Machine.emit(POP, 1);
		}else{ // assume built-in
			if(calledMethod.name.equals("println"))
				Machine.emit(putintnl);
			else if(calledMethod.name.equals("exit"))
				Machine.emit(HALT);
		}
		
		// if we aren't doing anything with the return value, we need to yeet it
		if(calledMethod.type.typeKind != TypeKind.VOID)
			Machine.emit(POP, 1);
		
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
	public Object visitIfStmt(IfStmt stmt, Object arg){
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
	public Object visitForStmt(ForStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		// first stmt can add stuff
		int originalSize = ((MethodDescriptor)md.runtimeDescriptor).size;
		
		if(stmt.initStmt != null)
			stmt.initStmt.visit(this, md);
		
		// jump to eval
		int toPatch_forEval = Machine.nextInstrAddr();
		Machine.emit(JUMP, CB, -1);
		
		// body
		int bodyAddr = Machine.nextInstrAddr();
		stmt.body.visit(this, md);
		
		// increment
		if(stmt.increStmt != null)
			stmt.increStmt.visit(this, null);
		
		// eval conditional, and jump if true
		if(stmt.cond != null) {
			int forEvalAddr = Machine.nextInstrAddr();
			stmt.cond.visit(this, md);
			Machine.emit(JUMPIF, 1, CB, bodyAddr);
			Machine.patch(toPatch_forEval, forEvalAddr);
		}else{ // or just loop infinitely like a moron
			int forFakeEvalAddr = Machine.nextInstrAddr();
			Machine.emit(JUMP, CB, bodyAddr);
			Machine.patch(toPatch_forEval, forFakeEvalAddr); // yes some of this is not very necessary.
		}
		
		int added = ((MethodDescriptor)md.runtimeDescriptor).size - originalSize;
		
		if(added != 0){
			Machine.emit(POP, added);
			((MethodDescriptor)md.runtimeDescriptor).size = originalSize;
		}
		
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
		
		// Bitwise special cases.
		switch(expr.operator.kind){
			case AND_LOG:
				int toPatch_and = Machine.nextInstrAddr();
				Machine.emit(JUMPIF, Machine.falseRep, CB, -1);
				
				expr.right.visit(this, null);
				
				int toPatch_orAnd = Machine.nextInstrAddr();
				Machine.emit(JUMP, CB, -1);
				
				int retFalseAddr = Machine.nextInstrAddr();
				Machine.emit(LOADL, Machine.falseRep);
				
				Machine.patch(toPatch_and, retFalseAddr);
				Machine.patch(toPatch_orAnd, Machine.nextInstrAddr());
				return null;
				
			case OR_LOG:
				int toPatch_or = Machine.nextInstrAddr();
				Machine.emit(JUMPIF, Machine.trueRep, CB, -1);
				
				expr.right.visit(this, null);
				
				int toPatch_andOr = Machine.nextInstrAddr();
				Machine.emit(JUMP, CB, -1);
				
				int retTrueAddr = Machine.nextInstrAddr();
				Machine.emit(LOADL, Machine.trueRep);
				
				Machine.patch(toPatch_or, retTrueAddr);
				Machine.patch(toPatch_andOr, Machine.nextInstrAddr());
				return null;
				
			default: // continue
		}
		
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
			
			case MOD:
				Machine.emit(mod);
				break;
			
			default:
				// should not happen
		}
		
		return null;
	}

	@Override
	public Object visitRefExpr(RefExpr expr, Object arg){
		expr.ref.visit(this, null);
		return null;
	}

	@Override
	public Object visitIxExpr(IxExpr expr, Object arg){
		expr.ref.visit(this, null); // put array
		expr.ixExpr.visit(this, null); // put ix on stack
		Machine.emit(arrayref);
		return null;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, Object arg){
		// each argument should be put onto the stack
		expr.argList.forEach(_arg -> _arg.visit(this, null));
		
		// then, we perform that beautiful call
		MethodDecl calledMethod = (MethodDecl)expr.functionRef.decl;
		
		if(!calledMethod.isStatic){ // but we may need to put the object on stack first
			// wow this is hacky lol
			if(expr.functionRef instanceof QualRef && !(((QualRef)expr.functionRef).ref instanceof ThisRef)) 
				expr.functionRef.visit(this, null);
			else
				Machine.emit(LOADA, OB, 0);
		}
		
		int toPatch_methodCall = Machine.nextInstrAddr();
		
		// there's also a CALLD, but that has to do with inheritance, which is not supported (yet)
		Machine.emit(calledMethod.isStatic ? CALL : CALLI, CB, -1);
		
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
		
		// TODO: field initializations
		/*cd.toInitialize.forEach(fd -> {
			fd.initExpression.visit(this, null);
		});*/
		
		if(cd.constructorDecl != null){
			expr.argList.forEach(_arg -> _arg.visit(this, null));
			
			// This will duplicate the address of newly created object on the stack
			Machine.emit(LOAD, ST, -1 - expr.argList.size());
			
			int toPatch_constructorCall = Machine.nextInstrAddr();
			Machine.emit(CALLI, CB, -1);
			methodRefsToPatch.put(toPatch_constructorCall, cd.constructorDecl);
		}
		
		return null;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg){
		expr.sizeExpr.visit(this, null);
		Machine.emit(newarr);
		return null;
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg){
		Machine.emit(LOADA, OB, 0);
		return null;
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg){
		if(ref.decl instanceof LocalDecl){
			LocalDecl ld = (LocalDecl)ref.decl;
			
			Machine.emit(LOAD, LB, ((VarDescriptor)ld.runtimeDescriptor).offset);
		}else if(ref.decl instanceof FieldDecl){
			FieldDecl fd = (FieldDecl)ref.decl;
			
			Machine.emit(LOAD, fd.isStatic ? SB : OB, ((VarDescriptor)fd.runtimeDescriptor).offset);
		}
		
		return null;
	}

	@Override
	public Object visitQRef(QualRef ref, Object arg){
		// arg: true if want address
		Boolean b = arg != null ? (Boolean)arg : false;
		
		if(b){
			MemberDecl md = (MemberDecl)ref.id.decl;
			ref.ref.visit(this, null);
			Machine.emit(LOADL, ((VarDescriptor)md.runtimeDescriptor).offset);
		}else{
			if(ref.ref.decl instanceof LocalDecl || ref.ref.decl instanceof MemberDecl){
				ref.ref.visit(this, null);
				
				if(ref.id.decl instanceof MethodDecl){
					// call will take care of it
				}else if(ref.id.decl instanceof MemberDecl){
					MemberDecl md = (MemberDecl)ref.id.decl;
					
					if(md.runtimeDescriptor != null){
						Machine.emit(LOADL, ((VarDescriptor)md.runtimeDescriptor).offset);
						Machine.emit(fieldref);
					}else{
						if(ref.ref.decl.type.typeKind == TypeKind.ARRAY && ref.id.spelling.equals("length"))
							Machine.emit(arraylen);
					}
				}
			}else if(ref.ref.decl instanceof ClassDecl){ // attempted static access
				if(ref.id.decl instanceof FieldDecl && !ref.id.spelling.equals("out")){
					FieldDecl fd = (FieldDecl)ref.id.decl;
					Machine.emit(LOAD, fd.isStatic ? SB : OB, ((VarDescriptor)fd.runtimeDescriptor).offset);
				}
			}
		}
		
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
