package miniJava.ContextualAnalysis;

import miniJava.AbstractSyntaxTrees.Declaration;

public class ContextualAnalysis {
	/**
	 * This is just here to context to certain internalized names.
	 */
	static String localizeDeclName(Declaration d){
		switch(d.name){
			case "_static":
				return "a static initialization block";
			case "_constructor":
				return "a constructor";
			default:
				return d.name;
		}
	}
}
