package miniJava.ContextualAnalysis;

import java.util.ArrayList;

import miniJava.AbstractSyntaxTrees.BaseType;
import miniJava.AbstractSyntaxTrees.Identifier;
import miniJava.AbstractSyntaxTrees.TypeDenoter;
import miniJava.AbstractSyntaxTrees.TypeKind;

public class MethodIdentifier extends AdvancedIdentifier {

	public ArrayList<TypeDenoter> params;
	
	public MethodIdentifier(Identifier id, ArrayList<TypeDenoter> params){
		super(id, AdvancedIdentifier.Type.METHOD);
		this.params = params;
	}

	/** for unknown types */
	public MethodIdentifier(Identifier id, int numParams){
		super(id, AdvancedIdentifier.Type.METHOD);
		
		for(int i = 0 ; i <= numParams; i++)
			params.add(new BaseType(TypeKind.UNKNOWN, null));
	}
	
	public int paramCount() {
		return params.size();
	}
	
	public boolean equals(MethodIdentifier other){
		if(paramCount() != other.paramCount())
			return false;
		
		for(int i = 0; i < params.size(); i++)
			if(!params.get(i).equals(other.params.get(i)))
				return false;
		
		return true;
	}
}
