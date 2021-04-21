package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.FailOnSystemExit;

class CodeGeneratorTest {

	@Test
	@FailOnSystemExit
	void test35(){
		System.out.println("Test 35");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test35.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("3"));
	}
	
	@Test
	@FailOnSystemExit
	void test36(){
		System.out.println("Test 36");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test36.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("10"));
	}
	
	@Test
	@FailOnSystemExit
	void test37(){
		System.out.println("Test 37");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test37.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("100"));
		assertTrue(tempOut.toString().contains("101"));
		assertTrue(tempOut.toString().contains("102"));
		assertTrue(tempOut.toString().contains("103"));
		assertTrue(tempOut.toString().contains("104"));
	}
}
