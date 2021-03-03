package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.FailOnSystemExit;

import miniJava.AbstractSyntaxTrees.AST;
import miniJava.AbstractSyntaxTrees.ASTDisplay;
import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

public class ASTTest {
	// TODO: I really should do some kind of automatic text comparison but this is hard.

	@Test
	@FailOnSystemExit
	void test1(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test7.mjava");
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
	void test2(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test2.mjava");
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
	void test3(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test3.mjava");
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
