package miniJava.ContextualAnalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import miniJava.ErrorReporter;
import miniJava.AbstractSyntaxTrees.*;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

public class IdentificationTable {
	private Stack<HashMap<String, Declaration>> table;
	private ErrorReporter reporter;
	
	
	public IdentificationTable(){
		this(new ErrorReporter());
	}

	public IdentificationTable(ErrorReporter reporter){
		this.reporter = reporter;
		table = new Stack<>();
		openScope();
		
		/// Predefined names. Yes, this is hacky.
		// String
		enter(new ClassDecl(
				"String",
				new FieldDeclList(),
				new MethodDeclList(),
				null));
		
		// _PrintStream
		enter(new ClassDecl(
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
		
		enter(new ClassDecl(
				"System",
				new FieldDeclList(
						new FieldDecl(
								false, 
								true, new ClassType(new Identifier(pStreamToken, retrieve(pStreamIden, null)), null),
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
	
	public void enter(Declaration dec){
		// TODO: I think the condition isn't necessary, but will test later.
		if(getLevel() >= 4){
			for(int i = table.size() - 1; i >= 3; i--){
				if(table.get(i).containsKey(dec.name)){
					reporter.addError("*** line " + dec.posn.getStartLineNum() + ": attempts to declare " + ContextualAnalysis.localizeDeclName(dec) + " with conflicting declaration on line " + table.get(i).get(dec.name).posn.getStartLineNum() + "!");
					return;
				}
			}
			
			table.peek().put(dec.name, dec);
		}else{
			if(table.peek().containsKey(dec.name)){
				if(dec instanceof MethodDecl){
					MethodDecl entering = (MethodDecl)dec;
					Declaration conflicting = table.peek().get(dec.name);
					
					if(conflicting instanceof MultiMethodDecl) {
						ArrayList<MethodDecl> possibleDecls = ((MultiMethodDecl)conflicting).possibleDecls;
						
						boolean foundGenuineConflict = false;
						
						for(MethodDecl pd : possibleDecls){
							if(pd.parameterDeclList.size() == entering.parameterDeclList.size()){ // continue
								boolean isSame = true;
								
								for(int i = 0; i < pd.parameterDeclList.size(); i++)
									if(!pd.parameterDeclList.get(i).type.equals(entering.parameterDeclList.get(i).type)) {
										isSame = false;
										break;
									}
								
								if(isSame){
									foundGenuineConflict = true;
									reporter.addError("*** line " + dec.posn.getStartLineNum() + ": attempts to declare method " + ContextualAnalysis.localizeDeclName(dec) + " with conflicting declaration on line " + conflicting.posn.getStartLineNum() + "!");
									break;
								}
							}
						}
						
						if(!foundGenuineConflict)
							((MultiMethodDecl)conflicting).possibleDecls.add(entering);
					}else if(conflicting instanceof MethodDecl){
						MethodDecl conflictingMD = (MethodDecl)conflicting;
						boolean foundGenuineConflict = false;
						
						if(conflictingMD.parameterDeclList.size() == entering.parameterDeclList.size()){
							boolean isSame = true;
							
							for(int i = 0; i < conflictingMD.parameterDeclList.size(); i++)
								if(!conflictingMD.parameterDeclList.get(i).type.equals(entering.parameterDeclList.get(i).type)){
									isSame = false;
									break;
								}
							
							if(isSame){
								foundGenuineConflict = true;
								reporter.addError("*** line " + dec.posn.getStartLineNum() + ": attempts to declare method " + ContextualAnalysis.localizeDeclName(dec) + " with conflicting declaration on line " + conflicting.posn.getStartLineNum() + "!");
							}
						}
						
						if(!foundGenuineConflict)
							table.peek().put(dec.name, new MultiMethodDecl(conflictingMD, entering));
					}else
						reporter.addError("*** line " + dec.posn.getStartLineNum() + ": attempts to declare " + ContextualAnalysis.localizeDeclName(dec) + " with conflicting declaration on line " + conflicting.posn.getStartLineNum() + "!");
				}else
					reporter.addError("*** line " + dec.posn.getStartLineNum() + ": attempts to declare " + ContextualAnalysis.localizeDeclName(dec) + " with conflicting declaration on line " + table.peek().get(dec.name).posn.getStartLineNum() + "!");
			}else
				table.peek().put(dec.name, dec);
		}
	}
	
	/**
	 * 
	 * @param iden the identifier, which will be modified to have a declared attached to it.
	 * @return either the declaration found or <code>null</code>
	 */
	public Declaration retrieve(Identifier iden, MethodDecl fromMeth){
		Declaration ret = null;
		
		for(int i = table.size() - 1; i >= 0; i--){
			Declaration possibleDecl = table.get(i).get(iden.spelling);
			
			if(possibleDecl != null){
				ret = possibleDecl;
				break;
			}
		}
		
		if(ret == null)
			reporter.addError("*** line " + iden.posn.getStartLineNum() + ": attempts to reference " + iden.spelling + " which was not found!");
		else if(ret instanceof MemberDecl && fromMeth != null && fromMeth.isStatic && !((MemberDecl)ret).isStatic)
			reporter.addError("*** line " + iden.posn.getStartLineNum() + ": attempts to reference non-static " + ret.name + " on line " + ret.posn.getStartLineNum() + "!");
		else if(ret instanceof VarDecl && !((VarDecl)ret).isInitialized)
			reporter.addError("*** line " + iden.posn.getStartLineNum() + ": attempts to reference " + ret.name + " on line " + ret.posn.getStartLineNum() + " which is not initialized!");
		else
			iden.decl = ret;
		
		return ret;
	}
	
	public void openScope(){
		table.add(new HashMap<>());
	}
	
	public void closeScope(){
		table.pop();
	}
	
	public int getLevel(){
		return table.size() - 1;
	}
}
