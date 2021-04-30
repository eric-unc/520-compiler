package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class StaticBlockDecl extends MethodDecl {

	public StaticBlockDecl(StatementList sl, SourcePosition posn){
		super(new FieldDecl(true, true, new BaseType(TypeKind.VOID, null), "_static", null), new ParameterDeclList(), sl, posn);
	}
}
