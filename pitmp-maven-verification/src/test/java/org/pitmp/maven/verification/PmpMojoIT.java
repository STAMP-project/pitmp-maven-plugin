package org.pitmp.maven.verification;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

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
import org.pitest.maven.PmpMojo;

@RunWith(Parameterized.class)
public class PmpMojoIT {

	private static final String PITMP_VERSION = getProperty("pitmp_version");
	private static final String PIT_VERSION = getProperty("pit_version");
	private static final String LOG_FILENAME = "log.out";

	@Parameter(0)
	public String projectPath;

	@Parameter(1)
	public String pomPath;

	private Verifier verifier;

	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Rule
	public TestName testName = new TestName();

	@Parameters(name = "{index}: Project: {0}, Pom Path: {1}")
	public static Collection<Object[]> configuration() {
		return Arrays.asList(new Object[][] { 
			    { "/dhell", "pom.xml.pitmp.conf1.xml" },
				{ "/dhell", "pom.xml.pitmp.noconf.xml" },

				{ "/dhell5", "pom.xml.pitmp.conf1.xml" },
				{ "/dhell5", "pom.xml.pitmp.noconf.xml" },

				{ "/dnoo", "pom.xml.pitmp.conf1.xml" },
				{ "/dnoo", "pom.xml.pitmp.noconf.xml" },

				{ "/dnoo5", "pom.xml.pitmp.conf1.xml" },
				{ "/dnoo5", "pom.xml.pitmp.noconf.xml" }, });
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
		cliOptions.add("-Dpitest-maven-version=" + PIT_VERSION);
		cliOptions.add("-Dpitmp-maven-plugin-version=" + PITMP_VERSION);
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

	private static String getProperty(String propertyName) {
		String path = "/version.prop";
		InputStream stream = PmpMojo.class.getResourceAsStream(path);
		Properties props = new Properties();
		try {
			props.load(stream);
			stream.close();
			return (String) props.get(propertyName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}