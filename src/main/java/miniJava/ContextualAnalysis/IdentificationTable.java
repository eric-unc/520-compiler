package miniJava.ContextualAnalysis;

import java.util.HashMap;
import java.util.Stack;

import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.Identifier;

public class IdentificationTable {
	private Stack<HashMap<String, Declaration>> table;
	private ErrorReporter reporter;
	
	
	public IdentificationTable(){
		this(new ErrorReporter());
	}

	public IdentificationTable(ErrorReporter reporter){
		this.reporter = reporter;
		table = new Stack<>();
		table.push(new HashMap<>());
	}
	
	public ErrorReporter getReporter(){
		return reporter;
	}
	
	public void addError(String error){
		reporter.addError(error);
	}
	
	// TODO: make these methods more complicated
	
	public void enter(Declaration dec){
		if(table.peek().containsKey(dec.name))
			reporter.addError("*** line " + dec.posn.getStartLineNum() + ": attempts to declare" + dec.name + " with conflicting declaration on line " + table.peek().get(dec.name).posn.getStartLineNum() + "!");
		else
			table.peek().put(dec.name, dec);
	}
	
	/**
	 * 
	 * @param iden the identifier name
	 * @return either the declaration found or <code>null</code>
	 */
	public Declaration retrieve(Identifier iden){
		return table.peek().get(iden.spelling);
	}
	
	public void openScope(){
		table.add(new HashMap<>());
	}
	
	public void closeScope(){
		table.pop();
	}
	
	public int getLevel(){
		return table.size();
	}
}
