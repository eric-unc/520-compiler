package miniJava.SyntacticAnalyzer;

public class SourcePosition {
	private final HalfPosition start;
	private final HalfPosition end;
	
	public SourcePosition(HalfPosition pos){
		this(pos, pos);
	}
	
	public SourcePosition(HalfPosition start, HalfPosition end){
		this.start = start;
		this.end = end;
	}

	public int getStartLineNum(){
		return start.getLineNum();
	}

	public int getEndLineNum(){
		return end.getLineNum();
	}

	public int getStartLineWidth(){
		return start.getLineWidth();
	}

	public int getEndLineWidth(){
		return end.getLineWidth();
	}
	
	@Override
	public String toString(){
		return "(Start: " + start + ", End: " + end + ")";
	}
}
