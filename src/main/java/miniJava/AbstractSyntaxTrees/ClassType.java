/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class ClassType extends TypeDenoter
{
    public ClassType(Identifier cn, SourcePosition posn){
        super(TypeKind.CLASS, posn);
        className = cn;
    }
            
    public <A,R> R visit(Visitor<A,R> v, A o) {
        return v.visitClassType(this, o);
    }

    public Identifier className;

	@Override
	public boolean equals(TypeDenoter other){
		return (typeKind == other.typeKind && other instanceof ClassType && className.equals(((ClassType)other).className)
				|| (other.typeKind == TypeKind.CLASS && other instanceof BaseType)); // null is fine
	}
	
	@Override
	public String toString(){
		return "ClassType (" + this.className.spelling + ")";
	}
}
