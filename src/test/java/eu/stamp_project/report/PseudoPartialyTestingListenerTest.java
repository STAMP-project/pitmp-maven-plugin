package eu.stamp_project.report;

import static org.pitest.mutationtest.DetectionStatus.KILLED;
import static org.pitest.mutationtest.DetectionStatus.SURVIVED;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.DetectionStatus;
import org.pitest.mutationtest.MutationResult;
import org.pitest.mutationtest.MutationStatusTestPair;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

public class PseudoPartialyTestingListenerTest {

	private MutationResult mutation(String method, DetectionStatus status, String mutant) {
		return new MutationResult(new MutationDetails(
				new MutationIdentifier(
						new Location(ClassName.fromString("AClass"), MethodName.fromString(method), "()I"), 1, mutant),
				"path/to/file", "Mutation description", 1, 0), new MutationStatusTestPair(1, status));
	}

	private Collection<MutationResult> record(String method, DetectionStatus... statuses) {
		Collection<MutationResult> mutations = new ArrayList<>();
		for (int i = 0; i < statuses.length; i++) {
			mutations.add(mutation(method, statuses[i], Integer.toString(i)));
		}
		return mutations;
	}

	@Test(expected = RuntimeException.class)
	public void shouldBeAbovePseudoTestedThresold() {
		MethodThresholds.getInstance().setPseudoTestedThresold(1);
		Collection<MutationResult> c = new ArrayList<MutationResult>();
		// TESTED
		c.addAll(record("method1", KILLED, KILLED, KILLED));
		// PSEUDO_TESTED
		c.addAll(record("method2", SURVIVED, SURVIVED, SURVIVED));
		// PSEUDO_TESTED
		c.addAll(record("method3", SURVIVED, SURVIVED, SURVIVED));
		PseudoPartialyTestingListener l = new PseudoPartialyTestingListener(null);
		l.handleMutationResult(new ClassMutationResults(c));
		l.runEnd();
	}

	@Test(expected = RuntimeException.class)
	public void shouldBeAbovePartialyTestedThresold() {
		MethodThresholds.getInstance().setPartialyTestedThresold(1);
		Collection<MutationResult> c = new ArrayList<MutationResult>();
		// TESTED
		c.addAll(record("method1", KILLED, KILLED, KILLED));
		// PARTIALLY_TESTED
		c.addAll(record("method2", KILLED, SURVIVED, KILLED));
		// PARTIALLY_TESTED
		c.addAll(record("method3", KILLED, SURVIVED, KILLED));
		PseudoPartialyTestingListener l = new PseudoPartialyTestingListener(null);
		l.handleMutationResult(new ClassMutationResults(c));
		l.runEnd();
	}
}
