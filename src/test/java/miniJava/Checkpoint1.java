package miniJava;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/* Automated regression tester for Checkpoint 1 tests
 * Created by Max Beckman-Harned and Jan Prins 
 * Modified by Eric Schneider
 */
public class Checkpoint1 {
	
	static ExecutorService threadPool = Executors.newCachedThreadPool();
	
	static final String PA1_TESTS = "src/test/resources/miniJava/pa1_tests";
	static final String BIN = "build/classes/java/main/";

	@Test
	@Disabled
	void checkpoint1() throws IOException, InterruptedException {
		System.out.println("Run pa1_tests on miniJava compiler!");
		
		// compensate for project organization 
		File classPath = new File(BIN);
		
		// miniJava compiler mainclass present ?
		if (! new File(classPath + "/miniJava/Compiler.class").exists()) {
			System.err.println("No miniJava compiler found - exiting!");
			fail();
		}
		
		// test directory present ?
		File testDir = new File(PA1_TESTS);
		if (!testDir.isDirectory()) {
			System.err.println("pa1_tests directory not found - exiting!");
			fail();
		}
		
		// run tests
		int failures = 0;
		for (File x : testDir.listFiles()) {
			int returnCode = runTest(x, classPath);
			if (returnCode == 1) {
				System.out.println("### miniJava Compiler failed while processing test " + x.getName());
				failures++;
				fail();
			}
			if (returnCode == 130) {
				System.err.println("### miniJava Compiler hangs on test " + x.getName());
				failures++;
				continue;
			}
			if (x.getName().indexOf("pass") != -1) {
				if (returnCode == 0)
					System.out.println(x.getName() + " passed successfully!");
				else if (returnCode == 4) {
					failures++;	
					System.err.println(x.getName()	+ " failed but should have passed!");
					fail();
				}
			} else {
				if (returnCode == 4)
					System.out.println(x.getName() + " failed successfully!");
				else {
					System.err.println(x.getName() + " did not fail properly!");
					failures++;
					fail();
				}
			}
		}
		
		System.out.println(failures + " failures in all.");
	}
	
	private static int runTest(File x, File cp) throws IOException, InterruptedException {
		String testPath = x.getPath();
		System.out.println(testPath);
		ProcessBuilder pb = new ProcessBuilder("java", "-cp", BIN, "miniJava.Compiler", testPath, "--ast-only");
		Process p = pb.start();
		threadPool.execute(new ProcessOutputter(p.getInputStream(), false));
		
		if (!p.waitFor(4, TimeUnit.SECONDS)) {
			// hung test
			p.destroy();
			return 130;  // interrupted
		}
		
		return p.exitValue();
	}
	
	static class ProcessOutputter implements Runnable {
		private Scanner processOutput;
		private boolean output;
		
		public ProcessOutputter(InputStream _processStream, boolean _output) {
			processOutput = new Scanner(_processStream);
			output = _output;
		}
		
		@Override
		public void run() {
			while(processOutput.hasNextLine()) {
				String line = processOutput.nextLine();
				if (output)
					System.out.println(line);
			}
		}
	}
}

