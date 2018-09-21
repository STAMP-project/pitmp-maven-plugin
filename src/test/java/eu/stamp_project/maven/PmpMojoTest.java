package eu.stamp_project.maven;

import java.io.File;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.pitest.maven.PmpMojo;

public class PmpMojoTest extends AbstractMojoTestCase {

	protected void setUp() throws Exception {
		// required for mojo lookups to work
		super.setUp();
	}

	public void testMojoParameters() throws Exception {
		File testPom = new File(getBasedir(), "/src/test/resources/dhell/pom.xml");
		PmpMojo mojo = (PmpMojo) lookupMojo("run", testPom);
		assertNotNull(mojo);
		assertNotNull(mojo.getTargetClasses());
		assertEquals(1, mojo.getTargetClasses().size());

	}
}
