package miniJava.SyntacticAnalyzer;

public class ParserException extends SyntacticAnalyzerException {

	private static final long serialVersionUID = 1L;

	public ParserException(TokenType expected, TokenType got, Scanner scanner){
		super("Expected " + expected + " token on line " + scanner.getLineNum() + ":" + scanner.getLineWidth() 
		+ ", got " + got + "!");
	}
}
