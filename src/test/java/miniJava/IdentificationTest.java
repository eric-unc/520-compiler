package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.FailOnSystemExit;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.ContextualAnalysis.ErrorReporter;
import miniJava.ContextualAnalysis.Identification;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

class IdentificationTest {

	@Test
	@FailOnSystemExit
	void test1(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test01.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(e.numErrors(), 0);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test13(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test13.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(3, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
}
