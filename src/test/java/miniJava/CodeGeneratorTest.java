package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import com.ginsberg.junit.exit.FailOnSystemExit;

class CodeGeneratorTest {

	@Test
	@ExpectSystemExitWithStatus(0)
	void test35(){
		System.out.println("Test 35");
		Compiler.main(new String[]{MainTest.RES + "Test35.mjava", "--asm-too"});
	}
}
