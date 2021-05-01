package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class ConstructorDecl extends MethodDecl {

	public ConstructorDecl(boolean isPrivate, Identifier givenName, ParameterDeclList pl, StatementList sl, SourcePosition posn){
		super(new FieldDecl(isPrivate, false, new BaseType(TypeKind.VOID, null), "_constructor", null), pl, sl, posn);
		this.givenName = givenName;
	}
	
	public Identifier givenName;
}
