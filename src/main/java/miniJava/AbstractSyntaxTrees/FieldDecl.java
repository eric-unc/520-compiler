/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class FieldDecl extends MemberDecl {

	public FieldDecl(boolean isPrivate, boolean isStatic, TypeDenoter t, String name, SourcePosition posn){
		super(isPrivate, isStatic, t, name, posn);
	}
	
	public FieldDecl(boolean isPrivate, boolean isStatic, Expression initExpression, TypeDenoter t, String name, SourcePosition posn){
		super(isPrivate, isStatic, t, name, posn);
		this.initExpression = initExpression;
	}

	public FieldDecl(MemberDecl md, SourcePosition posn){
		super(md, posn);
	}

	public <A, R> R visit(Visitor<A, R> v, A o){
		return v.visitFieldDecl(this, o);
	}

	public ClassDecl inClass;
	
	/** only for ACTUAL field declarations */
	public Expression initExpression;
}
