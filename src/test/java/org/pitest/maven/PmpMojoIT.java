package org.pitest.maven;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PmpMojoIT {

	private static Logger LOGGER = LoggerFactory.getLogger(PmpMojoIT.class);

	private Verifier verifier;
	private long startTime;

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Rule
	public TestName testName = new TestName();

	@Before
	public void beforeEachTest() {
		LOGGER.info("running test '{}'", testName.getMethodName());
		startTime = System.currentTimeMillis();
	}

	@After
	public void afterEachTest() {
		LOGGER.info("duration of test '{}' {}ms", testName.getMethodName(), System.currentTimeMillis() - startTime);
	}

	@Test
	public void testDefaultConfiguration() throws Exception {
		prepare("/dhell");
		List<String> goals = new ArrayList<String>();
		goals.add("clean");
		goals.add("install");
		goals.add("pitmp:run");
		verifier.executeGoals(goals);
		verifier.verifyErrorFreeLog();
	}

	private void prepare(String testPath) throws IOException, VerificationException {
		String path = ResourceExtractor.extractResourcePath(getClass(), testPath, testFolder.getRoot(), true)
				.getAbsolutePath();
		verifier = new Verifier(path);
		verifier.setAutoclean(true);
		verifier.setDebug(true);
	}

}