package miniJava.ContextualAnalysis;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.AbstractSyntaxTrees.Package;

public class Identification implements Visitor<Object, Object> {
	private IdentificationTable table;
	private ErrorReporter reporter;
	
	public Identification(Package ast, ErrorReporter reporter){
		this.reporter = reporter;
		this.table = new IdentificationTable(reporter);
		ast.visit(this, null);
	}

	@Override
	public Object visitPackage(Package prog, Object arg){
		table.openScope();
		
		prog.classDeclList.forEach(table::enter);
		
		prog.classDeclList.forEach(cd -> cd.visit(this, null));
		
		table.closeScope();
		return null;
	}

	@Override
	public Object visitClassDecl(ClassDecl cd, Object arg){
		// holy shit this code is straight garbo
		if(arg == null){
			table.openScope();
			
			cd.fieldDeclList.forEach(table::enter);
			cd.methodDeclList.forEach(table::enter);
			
			cd.fieldDeclList.forEach(fd -> fd.visit(this, cd));
			cd.methodDeclList.forEach(md -> md.visit(this, cd));
			
			table.closeScope();
		}else{
			Identifier searchingFor = (Identifier)arg;
			
			for(FieldDecl fd : cd.fieldDeclList)
				if(fd.name.equals(searchingFor.spelling))
					return fd;
			
			for(MethodDecl md : cd.methodDeclList)
				if(md.name.equals(searchingFor.spelling))
					return md;
			
		}
		
		return null;
	}

	@Override
	public Object visitFieldDecl(FieldDecl fd, Object arg){
		fd.type.visit(this, null);
		
		ClassDecl context = (ClassDecl)arg;
		fd.inClass = context;
		
		return null;
	}

	@Override
	public Object visitMethodDecl(MethodDecl md, Object arg){
		md.type.visit(this, null);
		
		ClassDecl context = (ClassDecl)arg;
		md.inClass = context;
		
		table.openScope();
		md.parameterDeclList.forEach(pd -> pd.visit(this, null));
		
		table.openScope();
		md.statementList.forEach(sl -> sl.visit(this, md));
		
		table.closeScope();
		table.closeScope();
		return null;
	}

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
		return null;
	}

	@Override
	public Object visitClassType(ClassType type, Object arg){
		type.classDecl = table.retrieve(type.className, null);
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
		table.openScope();
		stmt.sl.forEach(s -> s.visit(this, md));
		table.closeScope();
		return null;
	}

	@Override
	public Object visitVardeclStmt(VarDeclStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		stmt.varDecl.visit(this, null);
		stmt.initExp.visit(this, md);
		stmt.varDecl.isInitialized = true;
		return null;
	}

	@Override
	public Object visitAssignStmt(AssignStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		stmt.ref.visit(this, md);
		stmt.val.visit(this, md);
		return null;
	}

	@Override
	public Object visitIxAssignStmt(IxAssignStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		stmt.ref.visit(this, md);
		stmt.ix.visit(this, md);
		stmt.exp.visit(this, md);
		return null;
	}

	@Override
	public Object visitCallStmt(CallStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		stmt.methodRef.visit(this, md);
		stmt.argList.forEach(e -> e.visit(this, md));
		return null;
	}

	@Override
	public Object visitReturnStmt(ReturnStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		if(stmt.returnExpr != null)
			stmt.returnExpr.visit(this, md);
		
		return null;
	}

	@Override
	public Object visitIfStmt(IfStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		stmt.cond.visit(this, md);
		stmt.thenStmt.visit(this, md);
		
		if(stmt.elseStmt != null)
			stmt.elseStmt.visit(this, md);
		
		return null;
	}

	@Override
	public Object visitWhileStmt(WhileStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		stmt.cond.visit(this, md);
		stmt.body.visit(this, md);
		
		return null;
	}
	
	@Override
	public Object visitForStmt(ForStmt stmt, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		table.openScope();
		stmt.initStmt.visit(this, md);
		stmt.cond.visit(this, md);
		stmt.increStmt.visit(this, md);
		stmt.body.visit(this, md);
		table.closeScope();
		
		return null;
	}

	@Override
	public Object visitUnaryExpr(UnaryExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		expr.operator.visit(this, null);
		expr.expr.visit(this, md);
		return null;
	}

	@Override
	public Object visitBinaryExpr(BinaryExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		expr.operator.visit(this, null);
		expr.left.visit(this, md);
		expr.right.visit(this, md);
		
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
		MethodDecl md = (MethodDecl)arg;
		
		expr.ref.visit(this, md);
		expr.ixExpr.visit(this, md);
		
		return null;
	}

	@Override
	public Object visitCallExpr(CallExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		expr.functionRef.visit(this, md);
		expr.argList.forEach(al -> al.visit(this, md));
		
		return null;
	}

	@Override
	public Object visitLiteralExpr(LiteralExpr expr, Object arg){
		return null;
	}

	@Override
	public Object visitNewObjectExpr(NewObjectExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		expr.classtype.visit(this, null);
		expr.argList.forEach(e -> e.visit(this, md));
		
		for(MethodDecl _md : ((ClassDecl)expr.classtype.classDecl).methodDeclList)
			if(_md.name.equals("_constructor"))
				if(_md.isPrivate && _md.inClass != md.inClass)
					reporter.addError("*** line " + expr.posn.getStartLineNum() + ": attempts to reference private constructor on line " + _md.posn.getStartLineNum() + "!");
		
		return null;
	}

	@Override
	public Object visitNewArrayExpr(NewArrayExpr expr, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		expr.eltType.visit(this, null);
		expr.sizeExpr.visit(this, md);
		
		return null;
	}

	@Override
	public Object visitThisRef(ThisRef ref, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		if(md.isStatic)
			reporter.addError("*** line " + ref.posn.getStartLineNum() + ": attempts to reference non-static `this` on line " + ref.posn.getStartLineNum() + "!");

		ref.decl = md.inClass;
		return null;
	}

	@Override
	public Object visitIdRef(IdRef ref, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		Declaration d = (Declaration)ref.id.visit(this, md);
		ref.decl = d;
		
		return null;
	}

	@Override
	public Object visitQRef(QualRef ref, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		ref.ref.visit(this, md);
		Declaration context = ref.ref.decl;
		
		if(context == null){
			reporter.addError("*** line " + ref.ref.posn.getStartLineNum() + ": attempts to reference " + ref.id.spelling + " and no context was found!");
			return null;
		}
		
		if(context instanceof ClassDecl){
			ClassDecl cd = (ClassDecl)context;
			MemberDecl d = (MemberDecl)cd.visit(this, ref.id);
			
			if(d == null){
				reporter.addError("*** line " + ref.id.posn.getStartLineNum() + ": attempts to reference " + ref.id.spelling + " which was not found in class " + cd.name + "!");
				return null;
			}
			
			if(md.isStatic && !d.isStatic){
				reporter.addError("*** line " + ref.id.posn.getStartLineNum() + ": attempts to reference non-static " + d.name + " on line " + d.posn.getStartLineNum() + "!");
				return null;
			}
			
			if(d.isPrivate && cd != md.inClass)
				reporter.addError("*** line " + ref.id.posn.getStartLineNum() + ": attempts to reference private " + d.name + " on line " + d.posn.getStartLineNum() + "!");
			
			ref.id.decl = d;
			ref.decl = ref.id.decl;
		}else if(context instanceof LocalDecl){
			LocalDecl ld = (LocalDecl)context;
			
			switch(ld.type.typeKind){
				case CLASS:
					ClassType ct = (ClassType)ld.type;
					ClassDecl cd = (ClassDecl)table.retrieve(ct.className, md);
					MemberDecl d = (MemberDecl)cd.visit(this, ref.id);
					
					if(d == null){
						reporter.addError("*** line " + ref.id.posn.getStartLineNum() + ": attempts to reference " + ref.id.spelling + " which was not found in class " + cd.name + "!");
						return null;
					}
					
					if(d.isPrivate && cd != md.inClass)
						reporter.addError("*** line " + ref.id.posn.getStartLineNum() + ": attempts to reference private " + d.name + " on line " + d.posn.getStartLineNum() + "!");
					
					ref.id.decl = d;
					ref.decl = ref.id.decl;
					break;
					
				case ARRAY:
					if(ref.id.spelling.equals("length")){
						ref.id.decl = new FieldDecl(false, false, new BaseType(TypeKind.INT, null), "length", null);
						ref.decl = ref.id.decl;
						break;
					}
					
				default:
					reporter.addError("*** line " + ref.id.posn.getStartLineNum() + ": attempts to reference " + ref.id.spelling + " for type" + ld.type.typeKind + "!");
			}
		}else if(context instanceof MemberDecl){
			MemberDecl memd = (MemberDecl)context;
			
			switch(memd.type.typeKind){
				case CLASS:
					ClassType ct = (ClassType)memd.type;
					ClassDecl cd = (ClassDecl)table.retrieve(ct.className, md);
					MemberDecl d = (MemberDecl)cd.visit(this, ref.id);
					
					if(d == null){
						reporter.addError("*** line " + ref.id.posn.getStartLineNum() + ": attempts to reference " + ref.id.spelling + " which was not found in class " + cd.name + "!");
						return null;
					}
					
					if(d.isPrivate && cd != md.inClass)
						reporter.addError("*** line " + ref.id.posn.getStartLineNum() + ": attempts to reference private " + d.name + " on line " + d.posn.getStartLineNum() + "!");
					
					ref.id.decl = d;
					ref.decl = ref.id.decl;
					break;
					
				case ARRAY:
					if(ref.id.spelling.equals("length")){
						ref.id.decl = new FieldDecl(false, false, new BaseType(TypeKind.INT, null), "length", null);
						ref.decl = ref.id.decl;
						break;
					}
					
				default:
					reporter.addError("*** line " + ref.id.posn.getStartLineNum() + ": attempts to reference " + ref.id.spelling + " for type" + memd.type.typeKind + "!");
			}
		}
		
		return null;
	}

	@Override
	public Object visitIdentifier(Identifier id, Object arg){
		MethodDecl md = (MethodDecl)arg;
		
		return table.retrieve(id, md);
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
