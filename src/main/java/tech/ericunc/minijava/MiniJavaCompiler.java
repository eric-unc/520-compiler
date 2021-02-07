package tech.ericunc.minijava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import tech.ericunc.minijava.parser.Parser;
import tech.ericunc.minijava.scanner.Scanner;

public class MiniJavaCompiler {
	
	private static boolean debug;

	public static void main(String[] args){
		if(args.length == 0)
			throw new IllegalArgumentException("Needs file to compile with!");
		
		debug = args.length > 1 && args[1].contains("--debug");
		
		if(debugMode())
			System.out.println("Warning: the compiler is now in debug mode.");
		
		FileInputStream stream = null;
		
		try {
			stream = new FileInputStream(args[0]);
		}catch(FileNotFoundException e){
			System.err.println("File " + args[0] + " not found!");
			System.exit(1);
		}
		
		Scanner scanner = new Scanner(stream);
		Parser parser = new Parser(scanner);
		parser.parse();
	}
	
	public static boolean debugMode(){
		return debug;
	}
	
	public static void debug(Object o){
		System.out.println(o);
	}
}
