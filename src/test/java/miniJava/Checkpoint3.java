package miniJava;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/** Automated regression tester for Checkpoint 3 tests. 
 * Created by Max Beckman-Harned and Jan Prins
 * Modified by Eric Schneider
 * Put your tests in "tests/pa3_tests" folder in your Eclipse workspace directory
 * If you preface your error messages / exceptions with *** then they will 
 * be displayed in the regression tester output when they appear during processing
 */
public class Checkpoint3 {
        
	//private static String projDir;
	private static File classPath;
	//private static File testDir;
	static final String PA3_TESTS = "src/test/resources/miniJava/pa3_tests";
	static final String BIN = "build/classes/java/main/";
        
	@Test
	@Disabled
	void checkpoint3() throws IOException, InterruptedException {

		// project directory for miniJava and tester
		//projDir = System.getProperty("user.dir");
		//System.out.println("Run pa3_tests on miniJava compiler in " + projDir);
		
		// compensate for project organization 
		//classPath = new File(BIN);
		/*if (!classPath.isDirectory()) {
			// no bin directory in project, assume projDir is root for class files
			classPath = new File(projDir);
		}*/

		// miniJava compiler mainclass present ?
		if (! new File(BIN + "/miniJava/Compiler.class").exists()) {
			System.out.println("No miniJava Compiler.class found (has it been compiled?) - exiting");
			fail();
			return;
		}

		// test directory present ?
		File testDir = new File(PA3_TESTS);
		if (!testDir.isDirectory()) {
			System.out.println("pa3_tests directory not found - exiting!");
			fail();
			return;
		}

		System.out.println("Running tests from directory " + testDir);
        int failures = 0;
        for (File x : testDir.listFiles()) {
            if (x.getName().endsWith("out") || x.getName().startsWith(".") 
                || x.getName().endsWith("mJAM") || x.getName().endsWith("asm"))
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
                    System.out.println(x.getName() + " passed successfully!");
                }
                else {
                    failures++;
                    System.err.println(x.getName()  + " did not pass!");
                    fail();
                }
            } else {
                if (returnCode == 4)
                    System.out.println(x.getName() + " failed successfully!");
                else {
                    System.err.println(x.getName() + " failed to detect the error!");
                    failures++;
                    fail();
                }
            }
        }
        System.out.println(failures + " incorrect results in all.");     
    }
        
    private static int runTest(File x) throws IOException, InterruptedException {
    	//System.out.println(x.getPath());
        //String testPath = x.getPath();
        //ProcessBuilder pb = new ProcessBuilder("java", "miniJava.Compiler", testPath, "contextual-analysis-only");
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", BIN, "miniJava.Compiler", x.getPath(), "--contextual-analysis-only");
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
        
        
    public static void processStream(InputStream stream) {
        Scanner scan = new Scanner(stream);
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            //if (line.startsWith("*** "))
                System.out.println(line);
            //if (line.startsWith("ERROR")) {
                //System.out.println(line);
            //}
        }
        scan.close();
    }
}
