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

	@Test
	@FailOnSystemExit
	void test1(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test7.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			AST ast = parser.parse();
			
			ASTDisplay display = new ASTDisplay(); // TODO: maybe try to do some kind of text comparison
			display.showTree(ast);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}

}
