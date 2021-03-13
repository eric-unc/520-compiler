package miniJava.SyntacticAnalyzer;

public class ScannerException extends SyntacticAnalyzerException {

	private static final long serialVersionUID = 1L;

	public ScannerException(Token t, int lineNum, int lineWidth){
		super("Unexpected token with value " + "\"" + t.getValue() + "\" on line " + lineNum + ":" + lineWidth + "!");
	}
	
	public ScannerException(String unsupportedFeature, int lineNum, int lineWidth){
		super("The scanner does not support " + unsupportedFeature + " on line " + lineNum + ":" + lineWidth + "!");
	}
}
