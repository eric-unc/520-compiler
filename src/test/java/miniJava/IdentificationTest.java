package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.FailOnSystemExit;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.Package;
import miniJava.ContextualAnalysis.Identification;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

class IdentificationTest {

	@Test
	@FailOnSystemExit
	void test01(){
		try {
			System.out.println("Test 1");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test01.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(0, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test02(){
		try {
			System.out.println("Test 2");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test02.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
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
			System.out.println("Test 3");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test03.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(0, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test04(){
		try {
			System.out.println("Test 4");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test04.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(4, e.numErrors()); // this is fine as either 2 or 4
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test07(){
		try {
			System.out.println("Test 7");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test07.mjava");
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
	
	@Test
	@FailOnSystemExit
	void test08(){
		try {
			System.out.println("Test 8");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test08.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(0, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test13(){
		try {
			System.out.println("Test 13");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test13.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(4, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test14(){
		try {
			System.out.println("Test 14");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test14.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(0, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test15(){
		try {
			System.out.println("Test 15");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test15.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(2, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test16(){
		try {
			System.out.println("Test 16");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test16.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(0, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test17(){
		try {
			System.out.println("Test 17");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test17.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(0, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test18(){
		try {
			System.out.println("Test 18");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test18.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(5, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test19(){
		try {
			System.out.println("Test 19");
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test19.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ErrorReporter e = new ErrorReporter();
			@SuppressWarnings("unused")
			Identification id = new Identification((Package)ast, e);
			
			e.printErrors();
			assertEquals(1, e.numErrors());
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
}
