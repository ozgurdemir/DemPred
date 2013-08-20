package com.demshape.dempred.datastructure;

public class MultigroupDatapoint extends Datapoint {

	private static final long serialVersionUID = 1L;
	protected Integer multiGroup;
	protected MultigroupPrediction[] multigroupPredictions;

	public MultigroupDatapoint() {
		super();
	}

	public MultigroupDatapoint(int numGroups) {
		super();
		multigroupPredictions = new MultigroupPrediction[numGroups];
	}

	public Integer getMultiGroup() {
		return multiGroup;
	}

	public void setMultiGroup(Integer multiGroup) {
		this.multiGroup = multiGroup;
	}

	public MultigroupPrediction[] getMultigroupPredictions() {
		return multigroupPredictions;
	}

	public void setMultigroupPredictions(MultigroupPrediction[] multigroupPredictions) {
		this.multigroupPredictions = multigroupPredictions;
	}

}
