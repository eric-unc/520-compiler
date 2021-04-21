package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.FailOnSystemExit;

class CodeGeneratorTest {
	@Test
	@FailOnSystemExit
	void test34(){
		fail(); // XXX currently only passing superficially
		System.out.println("Test 34");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test34.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		for(int i = 3; i <= 15; i++)
			assertTrue(tempOut.toString().contains("" + i));
		
		assertTrue(tempOut.toString().contains("999"));
	}
	
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
	
	@Test
	@FailOnSystemExit
	void test38(){
		System.out.println("Test 38");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test38.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("19"));
	}
	
	@Test
	@FailOnSystemExit
	void test39(){
		System.out.println("Test 39");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test39.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("222"));
		assertTrue(tempOut.toString().contains("444"));
	}
	
	@Test
	@FailOnSystemExit
	void test40(){
		System.out.println("Test 40");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test40.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("666"));
		assertTrue(tempOut.toString().contains("777"));
		assertTrue(tempOut.toString().contains("888"));
		assertTrue(tempOut.toString().contains("999"));
	}
	
	@Test
	@FailOnSystemExit
	void test41(){
		System.out.println("Test 41");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test41.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("Program has failed due to division by zero"));
	}
	
	@Test
	@FailOnSystemExit
	void test42(){
		System.out.println("Test 42");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test42.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("222"));
		assertTrue(tempOut.toString().contains("333"));
		assertTrue(tempOut.toString().contains("444"));
		assertTrue(tempOut.toString().contains("777"));
	}
	
	@Test
	@FailOnSystemExit
	void test43(){
		System.out.println("Test 43");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test43.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("42"));
		assertTrue(tempOut.toString().contains("22"));
	}
	
	@Test
	@FailOnSystemExit
	void test44(){
		System.out.println("Test 44");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test44.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("20"));
		assertTrue(tempOut.toString().contains("21"));
		assertTrue(tempOut.toString().contains("22"));
		assertTrue(tempOut.toString().contains("23"));
		assertTrue(tempOut.toString().contains("24"));
		assertTrue(tempOut.toString().contains("25"));
	}
	
	@Test
	@FailOnSystemExit
	void test45(){
		System.out.println("Test 45");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test45.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("555"));
		assertTrue(tempOut.toString().contains("666"));
		assertTrue(tempOut.toString().contains("777"));
		assertTrue(tempOut.toString().contains("888"));
	}
	
	@Test
	@FailOnSystemExit
	void test46(){
		System.out.println("Test 46");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test46.mjava", "--jit"});
		}catch(Exception e){
			e.printStackTrace();
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("1555"));
	}
}
