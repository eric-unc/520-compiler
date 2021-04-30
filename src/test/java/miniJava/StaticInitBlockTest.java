package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.FailOnSystemExit;

class StaticInitBlockTest {

	@Test
	@FailOnSystemExit
	void test57(){
		System.out.println("Test 57");
		
		PrintStream out = System.out;
		ByteArrayOutputStream tempOut = new ByteArrayOutputStream();
		System.setOut(new PrintStream(tempOut));
		
		try {
			Compiler.main(new String[]{MainTest.RES + "Test57.mjava", "--jit"});
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

}
