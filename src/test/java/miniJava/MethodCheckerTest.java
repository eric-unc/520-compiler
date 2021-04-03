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
import miniJava.ContextualAnalysis.MethodChecker;
import miniJava.ContextualAnalysis.TypeChecking;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

class MethodCheckerTest {

	@Test
	@FailOnSystemExit
	void test01(){
		try {
			System.out.println("Test 01");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test01.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			new Identification((Package)ast, e);
			assertEquals(0, e.numErrors());
			
			new TypeChecking((Package)ast, e);
			e.printErrors();
			assertEquals(0, e.numErrors());
			
			new MethodChecker((Package)ast, e);
			e.printErrors();
			assertEquals(0, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test03(){
		try {
			System.out.println("Test 03");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test03.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			new Identification((Package)ast, e);
			assertEquals(0, e.numErrors());
			
			new TypeChecking((Package)ast, e);
			e.printErrors();
			assertEquals(0, e.numErrors());
			
			new MethodChecker((Package)ast, e);
			e.printErrors();
			assertEquals(1, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test08(){
		try {
			System.out.println("Test 08");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test08.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			new Identification((Package)ast, e);
			assertEquals(0, e.numErrors());
			
			new TypeChecking((Package)ast, e);
			e.printErrors();
			assertEquals(0, e.numErrors());
			
			new MethodChecker((Package)ast, e);
			e.printErrors();
			assertEquals(1, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test31(){
		try {
			System.out.println("Test 31");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test31.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			new Identification((Package)ast, e);
			assertEquals(0, e.numErrors());
			
			new TypeChecking((Package)ast, e);
			e.printErrors();
			assertEquals(0, e.numErrors());
			
			new MethodChecker((Package)ast, e);
			e.printErrors();
			assertEquals(1, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test32(){
		try {
			System.out.println("Test 32");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test32.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			new Identification((Package)ast, e);
			assertEquals(0, e.numErrors());
			
			new TypeChecking((Package)ast, e);
			e.printErrors();
			assertEquals(0, e.numErrors());
			
			new MethodChecker((Package)ast, e);
			e.printErrors();
			assertEquals(0, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test33(){
		try {
			System.out.println("Test 33");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test33.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			new Identification((Package)ast, e);
			assertEquals(0, e.numErrors());
			
			new TypeChecking((Package)ast, e);
			e.printErrors();
			assertEquals(0, e.numErrors());
			
			new MethodChecker((Package)ast, e);
			e.printErrors();
			assertEquals(3, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
}
