package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.FailOnSystemExit;

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

}
