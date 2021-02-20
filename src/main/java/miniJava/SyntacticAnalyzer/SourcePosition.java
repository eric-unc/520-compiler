package miniJava.SyntacticAnalyzer;

public class SourcePosition {
	public final int startLineNum;
	public final int endLineNum;
	public final int startLineWidth;
	public final int endLineWidth;
	
	public SourcePosition(int lineNum, int lineWidth){
		this(lineNum, lineNum, lineWidth, lineWidth);
	}
	
	public SourcePosition(int startLineNum, int endLineNum, int startLineWidth, int endLineWidth){
		this.startLineNum = startLineNum;
		this.endLineNum = endLineNum;
		this.startLineWidth = startLineWidth;
		this.endLineWidth = endLineWidth;
	}

	public int getStartLineNum(){
		return startLineNum;
	}

	public int getEndLineNum(){
		return endLineNum;
	}

	public int getStartLineWidth(){
		return startLineWidth;
	}

	public int getEndLineWidth(){
		return endLineWidth;
	}
}
