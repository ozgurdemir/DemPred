package dempred.datastructure;

import dempred.math.DenseVector;

public class MultigroupDatapoint extends Datapoint {

	private static final long serialVersionUID = 1L;
	protected Integer multiGroup;
	protected DenseVector predictedValues;

	public Integer getMultiGroup() {
		return multiGroup;
	}

	public void setMultiGroup(Integer multiGroup) {
		this.multiGroup = multiGroup;
	}

	public DenseVector getPredictedValues() {
		return predictedValues;
	}

	public void setPredictedValues(DenseVector predictions) {
		this.predictedValues = predictions;
	}

}
