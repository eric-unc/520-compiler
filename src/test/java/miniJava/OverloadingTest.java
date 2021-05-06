package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import com.ginsberg.junit.exit.FailOnSystemExit;
import com.ginsberg.junit.exit.SystemExitPreventedException;

class OverloadingTest {

	@Test
	@DisplayName("Basic overloading")
	@FailOnSystemExit
	void test89(){
		System.out.println("Test 89");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test89.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("11111"));
		assertTrue(tempOut.toString().contains("22222"));
	}

	@Test
	@DisplayName("More complicated overloading")
	@FailOnSystemExit
	void test90(){
		System.out.println("Test 90");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test90.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("11111"));
		assertTrue(tempOut.toString().contains("22222"));
		assertTrue(tempOut.toString().contains("33333"));
		assertTrue(tempOut.toString().contains("44444"));
		assertTrue(tempOut.toString().contains("55555"));
		assertTrue(tempOut.toString().contains("77777"));
		assertTrue(tempOut.toString().contains("999"));
	}
	
	@Test
	@DisplayName("Genuine conflicts correctly erroring")
	@ExpectSystemExitWithStatus(4)
	void test91(){
		System.out.println("Test 91");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test91.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
	
	@Test
	@DisplayName("Genuine conflicts with alternating visibilities correctly erroring")
	@ExpectSystemExitWithStatus(4)
	void test92(){
		System.out.println("Test 92");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test92.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
	
	@Test
	@DisplayName("Genuine conflicts with alternating staticity correctly erroring")
	@ExpectSystemExitWithStatus(4)
	void test93(){
		System.out.println("Test 93");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test93.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
	
	/**
	 * Some identification is shifted to the type checker for overloaded methods, so yes, this is worth checking.
	 */
	@Test
	@DisplayName("Attempted access to private overloaded method correctly erroring")
	@ExpectSystemExitWithStatus(4)
	void test94(){
		System.out.println("Test 94");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test94.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
}
