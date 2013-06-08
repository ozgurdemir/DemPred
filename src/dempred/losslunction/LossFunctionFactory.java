package dempred.losslunction;

import dempred.datastructure.Datapoint;


public class LossFunctionFactory<T extends Datapoint> {

	public LossFunctionInterface<T> getFunction(int type) throws IllegalArgumentException {
		switch (type) {
		case 0:
			return new HardStepFunction<T>(0.0);
		case 1:
			return new Bnll<T>();
		case 2:
			return new Sigmodial<T>(1.0);
		case 3:
			return new SmoothHinge<T>();
		case 4:
			return new Mse<T>();
		case 5:
			return new Lorentzian<T>();
		case 6:
			return new SidedQuad<T>();
		case 7:
			return new SidedLorentzian<T>();
		case 8:
			return new LogisticRegression<T>();
		default:
			throw new IllegalArgumentException("The selected LossFunction does not exist!: " + type);
		}
	}

}
