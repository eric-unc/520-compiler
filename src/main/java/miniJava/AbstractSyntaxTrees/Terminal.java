/**
 * miniJava Abstract Syntax Tree classes
 * @author prins
 * @version COMP 520 (v2.2)
 */
package miniJava.AbstractSyntaxTrees;

import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

abstract public class Terminal extends AST {

  public Terminal (Token t) {
	super(t.getPosition());
    spelling = t.getValue();
    kind = t.getType();
  }

  public TokenType kind;
  public String spelling;
}
