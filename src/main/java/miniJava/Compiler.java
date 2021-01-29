package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import miniJava.SyntacticAnalyzer.Scanner;

public class Compiler {

	public static void main(String[] args){
		if(args.length == 0)
			throw new IllegalArgumentException("Needs file to compile with!");
		
		FileInputStream input = null;
		
		try {
			input = new FileInputStream(args[0]);
		}catch(FileNotFoundException e){
			System.err.println("File " + args[0] + " not found!");
			System.exit(4);
		}
		
		Scanner scanner = new Scanner(input);
	}
}
