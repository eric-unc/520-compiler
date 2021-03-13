package miniJava.SyntacticAnalyzer;

public abstract class SyntacticAnalyzerException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SyntacticAnalyzerException(String msg){
		super(msg);
	}
}
