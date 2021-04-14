/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class BaseType extends TypeDenoter
{
    public BaseType(TypeKind t, SourcePosition posn){
        super(t, posn);
    }
    
    public <A,R> R visit(Visitor<A,R> v, A o) {
        return v.visitBaseType(this, o);
    }

	@Override
	public boolean equals(TypeDenoter other){
		return other != null && ((this.typeKind == other.typeKind && other instanceof BaseType && this.typeKind != TypeKind.UNSUPPORTED)
				|| (this.typeKind == TypeKind.CLASS && other.typeKind == TypeKind.CLASS && other instanceof ClassType && other.equals(this)));
	}

	@Override
	public String toPrettyString(){
		return "BaseType (" + this.typeKind + ")";
	}
}
