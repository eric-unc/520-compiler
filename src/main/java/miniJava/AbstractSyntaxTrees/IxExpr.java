/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.SourcePosition;

public class IxExpr extends Expression {

public IxExpr(Reference r, Expression e, SourcePosition posn){
    super(posn);
    ref = r;
    ixExpr = e;
}
    
public <A,R> R visit(Visitor<A,R> v, A o) {
    return v.visitIxExpr(this, o);
}

public Reference ref;
public Expression ixExpr;

}
