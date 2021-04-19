package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.FailOnSystemExit;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

//@Disabled
public class ASTTest {
	@Test
	@FailOnSystemExit
	void test01(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test01.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test02(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test02.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test03(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test03.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test07(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test07.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test08(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test08.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test09(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test09.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test10(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test10.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test12(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test12.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test17(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test17.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test19(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test19.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	void test35(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test35.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); 
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
}
