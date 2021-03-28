package miniJava;

import static miniJava.SyntacticAnalyzer.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import miniJava.SyntacticAnalyzer.Scanner;
import miniJava.SyntacticAnalyzer.Token;
import miniJava.SyntacticAnalyzer.TokenType;

class ScannerTest {
	void assertScan(TokenType type, String value, Scanner scanner){
		Token token = scanner.scan();
		assertEquals(type, token.getType(), "Type mismatch");
		assertEquals(value, token.getValue(), "Value mismatch");
	}

	static void assertScanType(TokenType type, Scanner scanner){
		assertEquals(type, scanner.scan().getType(), "Type mismatch");
	}

	@Test
	@Timeout(5)
	void test01(){
		try {
			FileInputStream stream = new FileInputStream(MainTest.RES + "Test01.mjava");

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

			// Line 3
			assertScanType(INT, scanner);
			assertScan(IDEN, "x", scanner);
			assertScanType(EQUALS, scanner);
			assertScan(NUM, "5", scanner);
			assertScanType(SEMI, scanner);
			
			System.out.println("made it to l3");

			// Line 4
			assertScanType(INT, scanner);
			assertScan(IDEN, "y", scanner);
			assertScanType(EQUALS, scanner);
			assertScan(NUM, "2", scanner);
			assertScanType(SEMI, scanner);

			// Line 5
			assertScanType(INT, scanner);
			assertScan(IDEN, "z", scanner);
			assertScanType(EQUALS, scanner);
			assertScan(IDEN, "x", scanner);
			assertScanType(MINUS, scanner);
			assertScan(IDEN, "y", scanner);
			assertScanType(SEMI, scanner);

			// Line 11
			assertScanType(BOOLEAN, scanner);
			assertScan(IDEN, "a", scanner);
			assertScanType(EQUALS, scanner);
			assertScan(IDEN, "x", scanner);
			assertScanType(MORE_THAN, scanner);
			assertScan(IDEN, "z", scanner);
			assertScanType(SEMI, scanner);

			// Line 12
			assertScanType(BOOLEAN, scanner);
			assertScan(IDEN, "b", scanner);
			assertScanType(EQUALS, scanner);
			assertScanType(FALSE, scanner);
			assertScanType(SEMI, scanner);
			
			// Line 13/14
			assertScanType(R_BRACKET, scanner);
			assertScanType(R_BRACKET, scanner);
			
			assertScanType(END, scanner);
		}catch(FileNotFoundException e){
			e.printStackTrace();
			fail();
		}

		assertEquals(true, true);
	}
}
