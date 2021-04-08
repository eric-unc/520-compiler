package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.ContextualAnalysis.ErrorReporter;
import miniJava.ContextualAnalysis.Identification;
import miniJava.ContextualAnalysis.MethodChecker;
import miniJava.ContextualAnalysis.TypeChecking;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

public class Compiler {
	
	private static boolean isSpecialMode;

	public static void main(String[] args){
		if(args.length == 0)
			throw new IllegalArgumentException("Needs file to compile with!");
		
		isSpecialMode = args.length > 1;
		
		if(isSpecialMode)
			System.out.println("Warning: the compiler is now in sepcial mode \"" + args[1] + "\".");
		
		FileInputStream stream = null;
		
		try {
			stream = new FileInputStream(args[0]);
		}catch(FileNotFoundException e){
			System.err.println("File " + args[0] + " not found!");
			System.exit(1);
		}
		
		Scanner scanner = new Scanner(stream);
		Parser parser = new Parser(scanner);
		AST ast = parser.parse();
		
		ASTDisplay display = new ASTDisplay();
		display.showTree(ast);
		
		ErrorReporter e = new ErrorReporter();
		@SuppressWarnings("unused")
		Identification id = new Identification((Package)ast, e);
		
		if(e.hasErrors()){
			e.printErrors();
			System.exit(4);
		}
		
		@SuppressWarnings("unused")
		TypeChecking tc = new TypeChecking((Package)ast, e);
		
		if(e.hasErrors()){
			e.printErrors();
			System.exit(4);
		}
		
		if(isSpecialMode && args[1].contains("contextual-analysis-only"))
			System.exit(0);
		
		@SuppressWarnings("unused")
		MethodChecker mc = new MethodChecker((Package)ast, e);
		
		if(e.hasErrors()){
			e.printErrors();
			System.exit(4);
		}
		
		System.exit(0);
	}
}
