package org.pitest.maven;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.maven.it.VerificationException;
import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.apache.maven.shared.utils.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class PmpMojoIT {

	private static final String LOG_FILENAME = "log.out";

	@Parameter(0)
	public String pitTestMavenVersion;

	@Parameter(1)
	public String pitmpMavenPluginVersion;

	@Parameter(2)
	public String projectPath;

	@Parameter(3)
	public String pomPath;

	private Verifier verifier;

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Rule
	public TestName testName = new TestName();

	@Parameters(name = "{index}: Project: {2}, Pom Path: {3}, Pit Test Version: {0}, Pitmp Version: {1} ")
	public static Collection<Object[]> configuration() {
		return Arrays.asList(new Object[][] {
				{ "1.4.2", "1.3.7-SNAPSHOT", "/dhell", "pom.xml.pitmp.conf1.xml" },
				{ "1.4.2", "1.3.7-SNAPSHOT", "/dhell", "pom.xml.pitmp.noconf.xml" },

				{ "1.4.2", "1.3.7-SNAPSHOT", "/dhell5", "pom.xml.pitmp.conf1.xml" },
				{ "1.4.2", "1.3.7-SNAPSHOT", "/dhell5", "pom.xml.pitmp.noconf.xml" },

				{ "1.4.2", "1.3.7-SNAPSHOT", "/dnoo", "pom.xml.pitmp.conf1.xml" },
				{ "1.4.2", "1.3.7-SNAPSHOT", "/dnoo", "pom.xml.pitmp.noconf.xml" },

				{ "1.4.2", "1.3.7-SNAPSHOT", "/dnoo5", "pom.xml.pitmp.conf1.xml" },
				{ "1.4.2", "1.3.7-SNAPSHOT", "/dnoo5", "pom.xml.pitmp.noconf.xml" }, 
				});
	}

	@Test
	public void testDefaultConfiguration() throws Exception {
		prepare(projectPath);
		// goals
		List<String> goals = new ArrayList<String>();
		goals.add("clean");
		goals.add("install");
		goals.add("pitmp:run");
		// client options
		List<String> cliOptions = new ArrayList<String>();
		cliOptions.add("-Dpitest-maven-version=" + pitTestMavenVersion);
		cliOptions.add("-Dpitmp-maven-plugin-version=" + pitmpMavenPluginVersion);
		verifier.setCliOptions(cliOptions);
		verifier.executeGoals(goals);
		verifier.verifyErrorFreeLog();
	}

	private File prepare(String testPath) throws IOException, VerificationException {
		String path = ResourceExtractor.extractResourcePath(getClass(), testPath, testFolder.getRoot(), true)
				.getAbsolutePath();
		verifier = new Verifier(path);
		verifier.setAutoclean(true);
		verifier.setDebug(true);
		verifier.setLogFileName(LOG_FILENAME);

		FileUtils.rename(new File(path + File.separator + pomPath), new File(path + File.separator + "pom.xml"));
		return new File(testFolder.getRoot().getAbsolutePath() + testPath);
	}

}