package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import com.ginsberg.junit.exit.FailOnSystemExit;
import com.ginsberg.junit.exit.SystemExitPreventedException;

class StaticInitBlockTest {

	@Test
	@FailOnSystemExit
	void test57(){
		System.out.println("Test 57");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test57.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("111"));
		assertTrue(tempOut.toString().contains("222"));
		assertTrue(tempOut.toString().contains("333"));
		assertTrue(tempOut.toString().contains("444"));
	}

	@Test
	@ExpectSystemExitWithStatus(4)
	void test58(){
		System.out.println("Test 58");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test58.mjava"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
	
	@Test
	@FailOnSystemExit
	void test59(){
		System.out.println("Test 59");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test59.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("1111"));
		assertTrue(tempOut.toString().contains("2222"));
	}
	
	@Test
	@FailOnSystemExit
	void test60(){
		System.out.println("Test 60");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test60.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("5555"));
		assertTrue(tempOut.toString().contains("1111"));
		assertTrue(tempOut.toString().contains("2222"));
		assertTrue(tempOut.toString().contains("3333"));
	}
	
	@Test
	@FailOnSystemExit
	void test61(){
		System.out.println("Test 61");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test61.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("2222"));
		assertTrue(tempOut.toString().contains("3333"));
		assertTrue(tempOut.toString().contains("4444"));
		assertTrue(tempOut.toString().contains("5555"));
	}
}
