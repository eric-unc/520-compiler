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
import miniJava.ContextualAnalysis.TypeChecking;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

/**
 * I wasn't sure if these should be put into identification or something else so yeah.
 * This is for testing the length property of arrays.
 *
 */
class LengthTest {

	@Test
	@FailOnSystemExit
	void test29(){
		try {
			System.out.println("Test 29");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test29.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			new Identification((Package)ast, e);
			assertEquals(0, e.numErrors());
			
			new TypeChecking((Package)ast, e);
			e.printErrors();
			assertEquals(0, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}

	@Test
	@FailOnSystemExit
	void test30(){
		try {
			System.out.println("Test 30");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test30.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			new Identification((Package)ast, e);
			assertEquals(0, e.numErrors());
			
			new TypeChecking((Package)ast, e);
			e.printErrors();
			assertEquals(1, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
}
