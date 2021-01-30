package tech.ericunc.minijava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import tech.ericunc.minijava.scanner.Scanner;

public class Compiler {

	public static void main(String[] args){
		if(args.length == 0)
			throw new IllegalArgumentException("Needs file to compile with!");
		
		FileInputStream stream = null;
		
		try {
			stream = new FileInputStream(args[0]);
		}catch(FileNotFoundException e){
			System.err.println("File " + args[0] + " not found!");
			System.exit(4);
		}
		
		Scanner scanner = new Scanner(stream);
	}
}
