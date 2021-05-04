package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.FailOnSystemExit;

class ForTest {

	@Test
	@DisplayName("Simple for")
	@FailOnSystemExit
	void test73(){
		System.out.println("Test 73");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test73.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		for(int i = 0; i <= 5; i++)
			assertTrue(tempOut.toString().contains("" + i));
	}

	@Test
	@DisplayName("For with incrementer declared outside and non-linear increments")
	@FailOnSystemExit
	void test74(){
		System.out.println("Test 74");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test74.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		for(int i = 0; i <= 55555; i += 11111)
			assertTrue(tempOut.toString().contains("" + i));
	}
	
	@Test
	@DisplayName("For with incrementer initialized outside")
	@FailOnSystemExit
	void test75(){
		System.out.println("Test 75");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test75.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		for(int i = 0; i <= 55555; i += 11111)
			assertTrue(tempOut.toString().contains("" + i));
	}
	
	@Test
	@DisplayName("For with initialization/increment statements empty")
	@FailOnSystemExit
	void test78(){
		System.out.println("Test 78");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test78.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		for(int i = 0; i <= 55555; i += 11111)
			assertTrue(tempOut.toString().contains("" + i));
	}
	
	@Test
	@DisplayName("Infinite for loop with init/condition/increment empty")
	@FailOnSystemExit
	void test79(){
		System.out.println("Test 79");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test79.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		for(int i = 0; i <= 55555; i += 11111)
			assertTrue(tempOut.toString().contains("" + i));
	}
	
	@Test
	@DisplayName("For with arrays")
	@FailOnSystemExit
	void test80(){
		System.out.println("Test 80");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test80.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		for(int i = 5555; i <= 8888; i += 1111)
			assertTrue(tempOut.toString().contains("" + i));
	}
}
