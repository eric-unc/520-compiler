package tech.ericunc.minijava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import tech.ericunc.minijava.scanner.Scanner;
import tech.ericunc.minijava.scanner.Token;
import tech.ericunc.minijava.scanner.TokenType;

import static tech.ericunc.minijava.scanner.TokenType.*;

class ScannerTest {
	static final String RES = "src/test/resources/tech/ericunc/minijava/";

	static void assertScan(TokenType type, String value, Scanner scanner){
		Token token = scanner.scan();
		assertEquals(type, token.getType(), "Type mismatch");
		assertEquals(value, token.getValue(), "Value mismatch");
	}
	
	static void assertScanType(TokenType type, Scanner scanner){
		assertEquals(type, scanner.scan().getType(), "Type mismatch");
	}
	
	@Test
	void test1(){
		try {
			FileInputStream stream = new FileInputStream(RES + "Test1.mjava");
			
			Scanner scanner = new Scanner(stream);
			
			// Line 1
			assertScanType(CLASS, scanner);
			assertScan(IDEN, "Test1", scanner);
			assertScanType(L_BRACKET, scanner);
			
			// Line 2
			assertScanType(PUBLIC, scanner);
			assertScanType(STATIC, scanner);
			assertScanType(VOID, scanner);
			assertScan(IDEN, "main", scanner);
			assertScanType(L_PAREN, scanner);
			assertScan(IDEN, "String", scanner);
			assertScanType(L_SQ_BRACK, scanner);
			assertScanType(R_SQ_BRACK, scanner);
			assertScan(IDEN, "args", scanner);
			assertScanType(R_PAREN, scanner);
			assertScanType(L_BRACKET, scanner);
			
			// Line 3 TODO
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}
		
		assertEquals(true, true);
	}

}
