package miniJava.SyntacticAnalyzer;

public enum TokenType {
	///// Main/misc types
	IDEN,		// identifier
	END,		// end
	ERROR,		// unknown/error token
	
	
	
	///// Keywords
	
	// Main/misc
	CLASS,		// class keyword
	THIS,		// this keyword
	RETURN,		// return keyword
	NEW,		// new keyword
	
	// Primitive types
	INT,		// integer (32-bit) keyword
	BOOLEAN,	// boolean keyword
	
	// Control
	IF,			// if keyword
	ELSE,		// else keyword
	WHILE,		// while keyword
	FOR,		// for keyword

	// Modifiers
	PUBLIC,		// public keyword
	PRIVATE,	// private keyword
	STATIC,		// static keyword
	VOID,		// void keyword
	
	
	
	///// Literals
	NUM,		// (int) number
	TRUE,		// true boolean value
	FALSE,		// false boolean value
	NULL,		// null
	
	///// Symbols
	
	// Main/misc
	EQUALS,		// =
	SEMI,		// ;
	DOT,		// .
	COMMA,		// ,
	
	// Paired
	L_PAREN,	// (
	R_PAREN,	// )
	L_BRACKET,	// {
	R_BRACKET,	// }
	L_SQ_BRACK,	// [
	R_SQ_BRACK,	// ]
	
	// Relational operators
	MORE_THAN,	// >
	LESS_THAN,	// <
	MORE_EQUAL,	// >=
	LESS_EQUAL,	// <=
	EQUALS_OP,	// ==
	NOT_EQUALS,	// !=
	
	// Logical operators
	AND_LOG,	// &&
	OR_LOG,		// ||
	NEG,		// !
	
	// Arithmetric operators
	PLUS,		// +
	MINUS,		// -
	TIMES,		// *
	DIV,		// /
	MOD,		// %
}
