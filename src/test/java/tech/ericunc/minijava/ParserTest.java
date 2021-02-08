package tech.ericunc.minijava;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;

import tech.ericunc.minijava.parser.Parser;
import tech.ericunc.minijava.scanner.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static tech.ericunc.minijava.scanner.TokenType.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ParserTest {
	static final int EXPECTED_EXIT_CODE = -1;
	
	@Test
	@ExpectSystemExitWithStatus(EXPECTED_EXIT_CODE)
	void test1(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test1.mjava");
			Scanner scanner = new Scanner(stream);
			Parser parser = new Parser(scanner);
			parser.parse();
			System.exit(EXPECTED_EXIT_CODE);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	@ExpectSystemExitWithStatus(EXPECTED_EXIT_CODE)
	void test2(){
		System.exit(EXPECTED_EXIT_CODE);
	}
}
