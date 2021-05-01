package miniJava;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


/** Automated regression tester for Checkpoint 4 tests
 * Created by Max Beckman-Harned and Jan Prins 
 * Modified by Eric Schneider
 * Put your tests in "tests/pa4_tests" folder in your Eclipse workspace directory
 * If you preface your compiler error messages with *** then they will be displayed 
 */
@Disabled
public class Checkpoint4 {
	
	//private static String projDir;
	private static File classPath;
	//private static File testDir;
	static final String PA4_TESTS = "src/test/resources/miniJava/pa4_tests";
	static final String BIN = "build/classes/java/main/";
	
	@Test
    void checkpoint4() throws IOException, InterruptedException {
    	/*
		// project directory for miniJava and tester
		projDir = System.getProperty("user.dir");
		System.out.println("Run pa4_tests on miniJava compiler in " + projDir);
		
		// compensate for project organization 
		classPath = new File(projDir + "/bin");
		if (!classPath.isDirectory()) {
			// no bin directory in project, assume projDir is root for class files
			classPath = new File(projDir);
		}*/

		// miniJava compiler mainclass present ?
		if (! new File(BIN + "/miniJava/Compiler.class").exists()) {
			System.out.println("No miniJava Compiler.class found (has it been compiled?) - exiting!");
			fail();
			return;
		}

		// test directory present ?
		File testDir = new File(PA4_TESTS);
		if (! testDir.isDirectory()) {
			System.out.println("pa4_tests directory not found - exiting!");
			return;
		}
   
		System.out.println("Running tests from directory " + testDir);
		int failures = 0;
		for (File x : testDir.listFiles()) {
			if  (x.getName().startsWith(".") || x.getName().endsWith("mJAM") || x.getName().endsWith("asm"))
				continue;
			
			int returnCode = runTest(x);
			
			if (returnCode == 1) {
				System.err.println("### miniJava Compiler fails while processing test " + x.getName());
				fail();
				failures++;
				continue;
			}
			if (returnCode == 130) {
				System.err.println("### miniJava Compiler hangs on test " + x.getName());
				fail();
				failures++;
				continue;
			}
			if (x.getName().indexOf("pass") != -1) {
				if (returnCode == 0) {
					try {
						int val = executeTest(x);
						int expected = Integer.parseInt(x.getName().substring(5,7));
						if (val == expected) 
							System.out.println(x.getName() + " ran successfully!");
						else if (expected == 30 && val == 10) {
							// pass430
							System.out.println(x.getName() + " appears to have run successfully -- check output is 1 through 10 in order");
						}
						else {
							failures++;
							System.err.println(x.getName() + " compiled but did not run successfully--got output " + val);
							fail();
						}
					}
					catch(Exception ex) {
						failures++;
						System.err.println(x.getName() + " did not output correctly.");
						fail();
					}
				}
				else {
					failures++;
					System.err.println(x.getName() + " failed to be processed!");
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
	
    private static int runTest(File x) throws IOException, InterruptedException {
    	//String testPath = x.getPath();
    	//ProcessBuilder pb = new ProcessBuilder("java", "miniJava.Compiler", testPath);
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", BIN, "miniJava.Compiler", x.getPath());
    	pb.directory(classPath);
    	pb.redirectErrorStream(true);
    	Process p = pb.start();

    	processStream(p.getInputStream());
    	if (!p.waitFor(5, TimeUnit.SECONDS)) {
			// hung test
			p.destroy();
			return 130;  // interrupted
		}
        return p.exitValue();
    }
	
    private static int executeTest(File x) throws IOException, InterruptedException {
    	String testPath = x.getPath().replace(".java", ".mJAM");
    	//ProcessBuilder pb = new ProcessBuilder("java", "mJAM.Interpreter", testPath);
    	ProcessBuilder pb = new ProcessBuilder("java", "-cp", BIN, "miniJava.Compiler", x.getPath(), "--run");
    	// ^ yes this means it will be compiled again, but i'm lazy lol
    	pb.directory(classPath);
    	Process process = pb.start();

    	Scanner scan = new Scanner(process.getInputStream());
    	int num = -1;
    	while (scan.hasNextLine()) {
    		String line = scan.nextLine();
    		if (line.startsWith(">>> ")) {
    			num = Integer.parseInt(line.substring(4));
    			System.out.println("Result = " + num);
    		}
    		if (line.startsWith("*** ")) {
    			System.out.println(line);
    		}
    	}
    	scan.close();

    	return num;
    }
	
	
    public static void processStream(InputStream stream) {
    	Scanner scan = new Scanner(stream);
    	while (scan.hasNextLine()) {
    		String line = scan.nextLine();
    		if (line.startsWith("*** "))
    			System.out.println(line);
    	}
    	scan.close();
    }
}
