package miniJava;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import com.ginsberg.junit.exit.FailOnSystemExit;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ParserTest {
	
	@Test
	@FailOnSystemExit
	static void test1(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test1.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			parser.parse();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	static void test2(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test2.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			parser.parse();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	static void test3(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test3.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			parser.parse();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	static void test4(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test4.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			parser.parse();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@FailOnSystemExit
	static void test5(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test5.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			parser.parse();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	@ExpectSystemExitWithStatus(4)
	static void test6(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test6.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			parser.parse();
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
	}
}
