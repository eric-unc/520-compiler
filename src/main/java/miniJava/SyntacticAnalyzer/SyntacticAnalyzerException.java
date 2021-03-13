package miniJava.SyntacticAnalyzer;

public class SyntacticAnalyzerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SyntacticAnalyzerException(){
		
	}
	
	public SyntacticAnalyzerException(String msg){
		super(msg);
	}
	
	public SyntacticAnalyzerException(TokenType expected, TokenType got, Scanner scanner){
		super("Expected " + expected + " token on line " + scanner.getLineNum() + ":" + scanner.getLineWidth() 
		+ ", got " + got + "!");
	}
}
