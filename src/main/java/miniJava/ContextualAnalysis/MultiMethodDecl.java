package miniJava.ContextualAnalysis;

import java.util.ArrayList;

import miniJava.AbstractSyntaxTrees.Declaration;
import miniJava.AbstractSyntaxTrees.MethodDecl;
import miniJava.AbstractSyntaxTrees.Visitor;

public class MultiMethodDecl extends Declaration {
	ArrayList<MethodDecl> possibleDecls;
	
	public MultiMethodDecl(String name){
		super(name, null, null);
		possibleDecls = new ArrayList<>();
	}
	
	public MultiMethodDecl(MethodDecl possibleDecl){
		this(possibleDecl.name);
		possibleDecls.add(possibleDecl);
	}
	
	public MultiMethodDecl(MethodDecl possibleDecl, MethodDecl otherPossibleDecl){
		this(possibleDecl.name);
		possibleDecls.add(possibleDecl);
		possibleDecls.add(otherPossibleDecl);
	}

	@Override
	public <A, R> R visit(Visitor<A, R> v, A o){
		return null;
	}
}
