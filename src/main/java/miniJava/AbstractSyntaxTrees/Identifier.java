/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.Token;

public class Identifier extends Terminal {
	public Declaration decl;

	public Identifier(Token t){
		super(t);
	}

	public Identifier(Token t, Declaration decl){
		this(t);
		this.decl = decl;
	}

	public <A, R> R visit(Visitor<A, R> v, A o){
		return v.visitIdentifier(this, o);
	}

	public boolean equals(Identifier other){
		return this.decl.equals(other.decl);
	}
}
