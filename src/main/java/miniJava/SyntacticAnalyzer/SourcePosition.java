package miniJava.SyntacticAnalyzer;

public class SourcePosition {
	private HalfPosition start;
	private HalfPosition end;
	
	public SourcePosition(HalfPosition pos){
		this(pos, pos);
	}
	
	public SourcePosition(HalfPosition start, HalfPosition end){
		this.start = start;
		this.end = end;
	}
	
	public HalfPosition getStart(){
		return start;
	}
	
	public HalfPosition getEnd(){
		return end;
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
	
	public void adjustToStart(SourcePosition other){
		this.start = other.start;
	}
	
	public void adjustToEnd(SourcePosition other){
		this.end = other.end;
	}
	
	@Override
	public String toString(){
		return "(Start: " + start + ", End: " + end + ")";
	}
}
