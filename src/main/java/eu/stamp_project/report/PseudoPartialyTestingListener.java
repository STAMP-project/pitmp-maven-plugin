package eu.stamp_project.report;

import org.pitest.mutationtest.ClassMutationResults;
import org.pitest.mutationtest.ListenerArguments;
import org.pitest.mutationtest.MutationResultListener;

import eu.stamp_project.mutationtest.descartes.reporting.models.MethodRecord;

public class PseudoPartialyTestingListener implements MutationResultListener {
	private Integer tested = 0;
	private Integer notCovered = 0;
	private Integer pseudoTested = 0;
	private Integer partiallyTested = 0;

	private ListenerArguments args;

	public PseudoPartialyTestingListener(final ListenerArguments args) {
		this.args = args;
	}

	public ListenerArguments getArguments() {
		return args;
	}

	@Override
	public void runStart() {
	}

	@Override
	public void handleMutationResult(ClassMutationResults results) {
		MethodRecord.getRecords(results).forEach(this::checkMethod);
	}

	@Override
	public void runEnd() {
		throwErrorIfPseudoTestedAboveThreshold(pseudoTested);
		throwErrorIfPartiallyTestedAboveThreshold(partiallyTested);
	}

	private void throwErrorIfPartiallyTestedAboveThreshold(Integer partiallyTested) {
		if ((MethodThresholds.getInstance().getPartialyTestedThresold() != 0)
				&& (partiallyTested > MethodThresholds.getInstance().getPartialyTestedThresold())) {
			throw new RuntimeException("Partially Tested score of " + partiallyTested + " is above threshold of "
					+ MethodThresholds.getInstance().getPartialyTestedThresold());
		}
	}

	private void throwErrorIfPseudoTestedAboveThreshold(Integer psedudoTested) {
		if ((MethodThresholds.getInstance().getPseudoTestedThresold() != 0)
				&& (psedudoTested > MethodThresholds.getInstance().getPseudoTestedThresold())) {
			throw new RuntimeException("Pseudo Tested score of " + psedudoTested + " is above threshold of "
					+ MethodThresholds.getInstance().getPseudoTestedThresold());
		}
	}

	private void checkMethod(MethodRecord method) {
		switch (method.getClassification()) {
		case TESTED:
			tested++;
			break;
		case NOT_COVERED:
			notCovered++;
			break;
		case PSEUDO_TESTED:
			pseudoTested++;
			break;
		case PARTIALLY_TESTED:
			partiallyTested++;
			break;
		}
	}
}
