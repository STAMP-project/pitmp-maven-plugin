package eu.stamp_project.maven;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.pitest.maven.PmpMojo;

public class PmpMojoTest extends AbstractMojoTestCase {

	protected void setUp() throws Exception {
		// required for mojo lookups to work
		super.setUp();
	}

	/**
	 * @throws Exception
	 */
	public void testMojoGoal() throws Exception {
		File testPom = new File(getBasedir(), "/src/test/resources/dhell/pom.xml");
		PmpMojo mojo = (PmpMojo) lookupMojo("run", testPom);
		assertNotNull(mojo);
//		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//		PrintStream testConsole = new PrintStream(outStream);
//		PrintStream trueConsole = System.out;
//
//		System.setOut(testConsole);
//		mojo.execute();
//		System.setOut(trueConsole);
//		
//		 outStream.toString().trim().toLowerCase();
		
	}
}
