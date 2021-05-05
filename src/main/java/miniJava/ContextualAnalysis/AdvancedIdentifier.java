package miniJava.ContextualAnalysis;

import miniJava.AbstractSyntaxTrees.Identifier;

public class AdvancedIdentifier {
	enum Type {
		CLASS,
		METHOD,
		VAR
	}
	
	String spelling;
	Type type;
	
	public AdvancedIdentifier(String spelling, Type type){
		this.spelling = spelling;
		this.type = type;
	}
	
	public AdvancedIdentifier(Identifier iden, Type type){
		this.spelling = iden.spelling;
		this.type = type;
	}
	
	public boolean equals(AdvancedIdentifier other){
		if(type != other.type)
			return false;
		
		if(type == Type.METHOD)
			return ((MethodIdentifier)this).equals((MethodIdentifier)other);
		
		return spelling.equals(other.spelling); // lol dunno if this what we want
	}
}
