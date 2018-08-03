package eu.stamp_project.report;

public class MethodThresholds {
	private static MethodThresholds instance;

	private int pseudoTestedThresold;

	private int partialyTestedThresold;

	private MethodThresholds() {
	}

	public static MethodThresholds getInstance() {
		if (instance == null) {
			instance = new MethodThresholds();
		}
		return instance;
	}

	public int getPseudoTestedThresold() {
		return pseudoTestedThresold;
	}

	public void setPseudoTestedThresold(int pseudoTestedThresold) {
		this.pseudoTestedThresold = pseudoTestedThresold;
	}

	public int getPartialyTestedThresold() {
		return partialyTestedThresold;
	}

	public void setPartialyTestedThresold(int partialyTestedThresold) {
		this.partialyTestedThresold = partialyTestedThresold;
	}

}
