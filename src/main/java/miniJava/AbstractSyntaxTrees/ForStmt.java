package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class ForStmt extends Statement {
	public Statement initStmt;
	public Expression cond;
	public Statement increStmt;
	public Statement body;
	
	public ForStmt(Statement initStmt, Expression cond, Statement increStmt, Statement body, SourcePosition posn){
		super(posn);
		this.increStmt = initStmt;
		this.cond = cond;
		this.increStmt = increStmt;
		this.body = body;
	}

	@Override
	public <A, R> R visit(Visitor<A, R> v, A o){
		return v.visitForStmt(this, o);
	}

}
