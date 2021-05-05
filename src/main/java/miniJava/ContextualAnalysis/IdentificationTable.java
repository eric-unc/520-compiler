package miniJava.ContextualAnalysis;

import java.util.HashMap;
import java.util.Stack;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

public class IdentificationTable {
	//private Stack<HashMap<String, Declaration>> table;
	private HashMap<String, ClassDecl> classTable;
	private HashMap<String, MethodDecl> methodTable;
	private Stack<HashMap<String, Declaration>> varTable;
	private ErrorReporter reporter;
	
	
	public IdentificationTable(){
		this(new ErrorReporter());
	}

	public IdentificationTable(ErrorReporter reporter){
		this.reporter = reporter;
		//table = new Stack<>();
		classTable = new HashMap<>();
		methodTable = new HashMap<>();
		varTable = new Stack<>();
		openScope();
		
		/// Predefined names. Yes, this is hacky.
		// String
		enterClass(new ClassDecl(
				"String",
				new FieldDeclList(),
				new MethodDeclList(),
				null));
		
		// _PrintStream
		enterClass(new ClassDecl(
				"_PrintStream",
				new FieldDeclList(),
				new MethodDeclList(
						new MethodDecl(
								new FieldDecl(false, false, new BaseType(TypeKind.VOID, null), "println", null),
								new ParameterDeclList(new ParameterDecl(new BaseType(TypeKind.INT, null), "n", null)),
								new StatementList(),
								null)
				),
				null));
		
		// System
		Token pStreamToken = new Token(TokenType.IDEN, "_PrintStream", null);
		Identifier pStreamIden = new Identifier(pStreamToken);
		
		enterClass(new ClassDecl(
				"System",
				new FieldDeclList(
						new FieldDecl(
								false, 
								true, new ClassType(new Identifier(pStreamToken, retrieveClass(pStreamIden, null)), null),
								"out",
								null)
				),
				new MethodDeclList(
						new MethodDecl(
								new FieldDecl(false, true, new BaseType(TypeKind.VOID, null), "exit", null),
								new ParameterDeclList(new ParameterDecl(new BaseType(TypeKind.INT, null), "n", null)),
								new StatementList(),
								null)
				),
				null));
	}
	
	public ErrorReporter getReporter(){
		return reporter;
	}
	
	public void addError(String error){
		reporter.addError(error);
	}
	
	public void enterClass(ClassDecl dec){
		if(classTable.containsKey(dec.name))
			reporter.addError("*** line " + dec.posn.getStartLineNum() + ": attempts to re-declare class " + dec.name + " with conflicting declaration on line " + classTable.get(dec.name).posn.getStartLineNum() + "!");
		else
			classTable.put(dec.name, dec);
		
	}
	
	public ClassDecl retrieveClass(Identifier iden, MethodDecl fromMeth){
		ClassDecl ret = classTable.get(iden.spelling);
		
		if(ret == null)
			reporter.addError("*** line " + iden.posn.getStartLineNum() + ": attempts to reference class " + iden.spelling + " which was not found!");
		
		return ret;
	}
	
	public void enterMethod(MethodDecl dec){
		if(methodTable.containsKey(dec.name))
			reporter.addError("*** line " + dec.posn.getStartLineNum() + ": attempts to re-declare method " + dec.name + " with conflicting declaration on line " + methodTable.get(dec.name).posn.getStartLineNum() + "!");
		else
			methodTable.put(dec.name, dec);
	}
	
	public MethodDecl retrieveMethod(Identifier iden, MethodDecl fromMeth) {
		MethodDecl ret = methodTable.get(iden.spelling);
		
		if(ret == null)
			reporter.addError("*** line " + iden.posn.getStartLineNum() + ": attempts to reference method " + iden.spelling + " which was not found!");
		else if(fromMeth != null && fromMeth.isStatic && !((MemberDecl)ret).isStatic)
			reporter.addError("*** line " + iden.posn.getStartLineNum() + ": attempts to reference non-static method " + ret.name + " on line " + ret.posn.getStartLineNum() + "!");
		
		return ret;
	}
	
	public void enterVar(Declaration dec){
		// TODO: I think the condition isn't necessary, but will test later.
		if(getLevel() >= 4){
			for(int i = varTable.size() - 1; i >= 3; i--){
				if(varTable.get(i).containsKey(dec.name)){
					reporter.addError("*** line " + dec.posn.getStartLineNum() + ": attempts to declare " + dec.name + " with conflicting declaration on line " + varTable.get(i).get(dec.name).posn.getStartLineNum() + "!");
					return;
				}
			}
			
			varTable.peek().put(dec.name, dec);
		}else{
			if(varTable.peek().containsKey(dec.name))
				reporter.addError("*** line " + dec.posn.getStartLineNum() + ": attempts to declare " + ContextualAnalysis.localizeDeclName(dec) + " with conflicting declaration on line " + varTable.peek().get(dec.name).posn.getStartLineNum() + "!");
			else
				varTable.peek().put(dec.name, dec);
		}
	}
	
	public Declaration retrieveVar(Identifier iden, MethodDecl fromMeth){
		Declaration ret = null;
		
		for(int i = varTable.size() - 1; i >= 0; i--){
			Declaration possibleDecl = varTable.get(i).get(iden.spelling);
			
			if(possibleDecl != null){
				ret = possibleDecl;
				break;
			}
		}
		
		if(ret == null)
			reporter.addError("*** line " + iden.posn.getStartLineNum() + ": attempts to reference " + iden.spelling + " which was not found!");
		else if(ret instanceof MemberDecl && fromMeth != null && fromMeth.isStatic && !((MemberDecl)ret).isStatic)
			reporter.addError("*** line " + iden.posn.getStartLineNum() + ": attempts to reference non-static field " + ret.name + " on line " + ret.posn.getStartLineNum() + "!");
		else if(ret instanceof VarDecl && !((VarDecl)ret).isInitialized)
			reporter.addError("*** line " + iden.posn.getStartLineNum() + ": attempts to reference variable " + ret.name + " on line " + ret.posn.getStartLineNum() + " which is not initialized!");
		else
			iden.decl = ret;
		
		return ret;
	}
	
	public void openScope(){
		varTable.add(new HashMap<>());
	}
	
	public void closeScope(){
		varTable.pop();
	}
	
	public void clearMethods() {
		methodTable.clear();
	}
	
	public int getLevel(){
		return varTable.size() - 1;
	}
}
