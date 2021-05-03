package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import com.ginsberg.junit.exit.FailOnSystemExit;
import com.ginsberg.junit.exit.SystemExitPreventedException;

class ConstructorTest {

	@Test
	@DisplayName("Simple constructors")
	@FailOnSystemExit
	void test63(){
		System.out.println("Test 63");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test63.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("55555"));
		assertTrue(tempOut.toString().contains("66666"));
		assertTrue(tempOut.toString().contains("77777"));
	}
	
	@Test
	@DisplayName("Two constructors in the same class correctly erroring")
	@ExpectSystemExitWithStatus(4)
	void test64(){
		System.out.println("Test 64");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test64.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
	
	@Test
	@DisplayName("Constructor with three arguments")
	@FailOnSystemExit
	void test65(){
		System.out.println("Test 65");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test65.mjava", "--run"});
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
		assertTrue(tempOut.toString().contains("3333"));
	}
	
	@Test
	@DisplayName("Attempted construction without parameters correctly failing")
	@ExpectSystemExitWithStatus(4)
	void test66(){
		System.out.println("Test 66");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test66.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
	
	@Test
	@DisplayName("Attempted construction with incorrect type of parameters correctly failing")
	@ExpectSystemExitWithStatus(4)
	void test67(){
		System.out.println("Test 67");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test67.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
	
	@Test
	@DisplayName("Attempted construction with too many parameters correctly failing")
	@ExpectSystemExitWithStatus(4)
	void test68(){
		System.out.println("Test 68");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test68.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
	
	@Test
	@DisplayName("Constructor with public modifier")
	@FailOnSystemExit
	void test69(){
		System.out.println("Test 69");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test69.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("555"));
	}
	
	@Test
	@DisplayName("Constructors of all modifiers")
	@FailOnSystemExit
	void test70(){
		System.out.println("Test 70");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test70.mjava", "--run"});
		}catch(Exception e){
			e.printStackTrace();
			out.println(tempOut.toString());
			System.setOut(out);
			fail();
		}
		
		out.println(tempOut.toString());
		System.setOut(out);
		
		assertTrue(tempOut.toString().contains("555"));
		assertTrue(tempOut.toString().contains("666"));
		assertTrue(tempOut.toString().contains("777"));
	}
	
	@Test
	@DisplayName("Construction with private constructor outside of class correctly failing")
	@ExpectSystemExitWithStatus(4)
	void test71(){
		System.out.println("Test 71");
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test71.mjava", "--run"});
		}catch(Exception e){
			if(!(e instanceof SystemExitPreventedException)){
				e.printStackTrace();
				fail();
			}
		}
	}
}
