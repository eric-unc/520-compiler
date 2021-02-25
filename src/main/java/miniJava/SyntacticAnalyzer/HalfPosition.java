package miniJava.SyntacticAnalyzer;

public class HalfPosition {
	public final int lineNum;
	public final int lineWidth;
	
	public HalfPosition(int lineNum, int lineWidth){
		this.lineNum = lineNum;
		this.lineWidth = lineWidth;
	}

	public int getLineNum(){
		return lineNum;
	}

	public int getLineWidth(){
		return lineWidth;
	}
}
