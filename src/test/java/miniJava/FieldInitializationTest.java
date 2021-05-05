package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import com.ginsberg.junit.exit.FailOnSystemExit;
import com.ginsberg.junit.exit.SystemExitPreventedException;

class FieldInitializationTest {

	@Test
	@DisplayName("Simple static initialization")
	@FailOnSystemExit
	void test81(){
		System.out.println("Test 81");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test81.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("11111"));
	}

	@Test
	@DisplayName("Static initialization using method")
	@FailOnSystemExit
	void test82(){
		System.out.println("Test 82");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test82.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("11111"));
	}
	
	@Test
	@DisplayName("Complex Static initialization")
	@FailOnSystemExit
	void test83(){
		System.out.println("Test 83");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test83.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("11125"));
	}
	
	@Test
	@DisplayName("Static initializer trying to access non-static field correctly erroring")
	@ExpectSystemExitWithStatus(4)
	void test84(){
		System.out.println("Test 84");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test84.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
	
	@Test
	@DisplayName("Static initializer trying to access non-static method correctly erroring")
	@ExpectSystemExitWithStatus(4)
	void test85(){
		System.out.println("Test 85");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test85.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
	
	@Test
	@DisplayName("Simple non-static initialization")
	@FailOnSystemExit
	void test86(){
		System.out.println("Test 86");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test86.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("11111"));
	}
	
	@Test
	@DisplayName("Non-static initialization using method")
	@FailOnSystemExit
	void test87(){
		System.out.println("Test 87");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test87.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("11111"));
	}
	
	@Test
	@DisplayName("Complex non-static initialization")
	@FailOnSystemExit
	void test88(){
		System.out.println("Test 88");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test88.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("11125"));
	}
}
