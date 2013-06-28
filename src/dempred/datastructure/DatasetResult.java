package dempred.datastructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import dempred.losslunction.LossFunctionInterface;
import dempred.math.DenseVector;
import dempred.math.VectorInterface;
import dempred.math.VectorMetric;
import dempred.transformer.NoTransformation;
import dempred.transformer.TransformationFunctionInterface;

/**
 * The Class DatasetResult contains various functions to measure the prediction quality of a predicted dataset.
 */
public class DatasetResult {

	/** The notransform. */
	private static TransformationFunctionInterface notransform = new NoTransformation();

	/**
	 * Given a lossfunction this function evaluates the error. sum(loss(measuredVaue, predictedValue))
	 *
	 * @param <T> the generic type
	 * @param dataset the dataset
	 * @param lossFunction the loss function
	 * @return the evaluated loss function
	 */
	public static <T extends Datapoint> double lossFunction(Dataset<T> dataset, LossFunctionInterface<T> lossFunction) {
		double result = 0.0;
		for (T datapoint : dataset.getDatapoints())
			result += lossFunction.g(datapoint.getValue(), datapoint.getPredictedValue(), datapoint);
		return result;
	}

	/**
	 * Calculates the Mathew's Correlation Coefficient (MCC) of a predicted dataset.
	 *
	 * @param dataset the dataset
	 * @return the mcc
	 */
	public static double mcc(Dataset<?> dataset) {
		int numPositives = dataset.groupQuantity(1);
		int numNegatives = dataset.groupQuantity(-1);
		int correctPositives = dataset.numCorrectPredictions(1);
		int correctNegatives = dataset.numCorrectPredictions(-1);
		return mcc(correctPositives, correctNegatives, numNegatives - correctNegatives, numPositives - correctPositives);
	}

	/**
	 * Calculates the Mathew's Correlation Coefficient (MCC) given the number of true and false negatives and positives respectively.
	 *
	 * @param truepos the truepos
	 * @param trueneg the trueneg
	 * @param falsepos the falsepos
	 * @param falseneg the falseneg
	 * @return the double
	 */
	public static double mcc(double truepos, double trueneg, double falsepos, double falseneg) {
		return ((truepos * trueneg - falsepos * falseneg) / Math.sqrt(((truepos + falsepos) * (truepos + falseneg) * (trueneg + falsepos) * (trueneg + falseneg))));
	}

	/**
	 * Calculates the accuracy of a predicted dataset: true positives / (true positives + false positives)
	 *
	 * @param dataset the dataset
	 * @return the double
	 */
	public static double accuracy(Dataset<?> dataset) {
		int numNegatives = dataset.groupQuantity(-1);
		int correctNegatives = dataset.numCorrectPredictions(-1);
		double truepos = dataset.numCorrectPredictions(1);
		double falsepos = numNegatives - correctNegatives;
		return truepos / (truepos + falsepos);
	}

	/**
	 * Calculates the R2q2 of a dataset.
	 *
	 * @param dataset the dataset
	 * @return the r2 or q2 error
	 */
	public static double r2q2(Dataset<?> dataset) {
		return r2q2(dataset, notransform);
	}

	 
	/**
	 * Calculates the R2q2 value. the transformation is accomplished before the calculation. <br>
	 * r^2 = coefficient of determination <br>
	 * Only in case of a linear regression r^2 = pcc^2 <br>
	 * q^2 = same as r2 but computed on loo trainset. r2 is normally on recall. "predictive r2" on test set <br>
	 *
	 * @param dataset the dataset
	 * @param transformationFunction the transformation function
	 * @return the R2 or Q2 value
	 */
	public static double r2q2(Dataset<?> dataset, TransformationFunctionInterface transformationFunction) {
		double mean = computeValueMean(dataset);
		double sum1 = 0.0;
		double sum2 = 0.0;
		for (Datapoint datapoint : dataset.getDatapoints()) {
			sum1 += Math.pow(transformationFunction.transform(datapoint.getValue()) - transformationFunction.transform(datapoint.getPredictedValue()), 2);
			sum2 += Math.pow(transformationFunction.transform(datapoint.getValue()) - mean, 2);
		}
		return 1 - (sum1 / sum2);
	}

	/**
	 * Computes the mean of all values of a dataset.
	 *
	 * @param dataset the dataset
	 * @return the double
	 */
	public static double computeValueMean(Dataset<?> dataset) {
		double mean = 0.0;
		for (Datapoint datapoint : dataset.getDatapoints())
			mean += datapoint.getValue();
		mean /= dataset.size();
		return mean;
	}

	/**
	 * called "linear correlation coefficient" (r) or "Pearson product moment correlation coefficient" (pcc)
	 * in case of linear regression: can be squared to get r^2 but not same as "coefficient of determination" in all cases!
	 *
	 * @param dataset the dataset
	 * @return the pcc value
	 */
	public static double pcc(Dataset<?> dataset) {
		return pcc(dataset, notransform);
	}

	/**
     * called "linear correlation coefficient" (r) or "Pearson product moment correlation coefficient" (pcc)
	 * in case of linear regression: can be squared to get r^2 but not same as "coefficient of determination" in all cases!
	 * the transformation is accomplished before the calculation.
	 * 
	 * @param dataset the dataset
	 * @param transformationFunction the transformation function
	 * @return the double
	 */
	public static double pcc(Dataset<?> dataset, TransformationFunctionInterface transformationFunction) {
		VectorInterface measuredValues = new DenseVector(dataset.size());
		VectorInterface predictedValues = new DenseVector(dataset.size());
		for (int i = 0; i < dataset.size(); ++i) {
			Datapoint datapoint = dataset.getDatapoint(i);
			measuredValues.set(i, transformationFunction.transform(datapoint.getValue()));
			predictedValues.set(i, transformationFunction.transform(datapoint.getPredictedValue()));
		}
		return VectorMetric.pcc(measuredValues, predictedValues);
	}

	/**
	 * Calculates the Residual Mean Square Deviation (RMSD).
	 *
	 * @param dataset the dataset
	 * @return the double
	 */
	public static double rmsd(Dataset<?> dataset) {
		return rmsd(dataset, notransform);
	}

	/**
	 * Calculates the Residual Mean Square Deviation (RMSD).
	 * the transformation is accomplished before the calculation.
	 *
	 * @param dataset the dataset
	 * @param transformationFunction the transformation function
	 * @return the double
	 */
	public static double rmsd(Dataset<?> dataset, TransformationFunctionInterface transformationFunction) {
		double result = 0.0;
		for (Datapoint datapoint : dataset.getDatapoints())
			result += Math.pow(transformationFunction.transform(datapoint.getValue()) - transformationFunction.transform(datapoint.getPredictedValue()), 2.0);
		return Math.sqrt(result / dataset.size());
	}

	 
	/**
	 * Calculates the Average fold error.
	 * also called geometric mean fold error which is the geometric mean of the error quotient.
	 * geometric mean is defined as: n-th root of the products of the errors. 
	 *
	 * @param dataset the dataset
	 * @return the double
	 */
	public static double averageFoldError(Dataset<?> dataset) {
		return averageFoldError(dataset, notransform);
	}

	/**
	 * Calculates the Average fold error.
	 * also called geometric mean fold error which is the geometric mean of the error quotient.
	 * geometric mean is defined as: n-th root of the products of the errors.
	 * the transformation is accomplished before the calculation.
	 *
	 * @param dataset the dataset
	 * @param transformationFunction the transformation function
	 * @return the double
	 */
	public static double averageFoldError(Dataset<?> dataset, TransformationFunctionInterface transformationFunction) {
		double foldError = 0.0;
		for (Datapoint datapoint : dataset.getDatapoints())
			foldError += Math.abs(Math.log10(transformationFunction.transform(datapoint.getPredictedValue()) / transformationFunction.transform(datapoint.getValue())));
		foldError /= dataset.size();
		foldError = Math.pow(10, foldError);
		return foldError;
	}

	/**
	 * calculates the sum of fold errors: sum(predicted valuw/measured value)
	 *
	 * @param dataset the dataset
	 * @return the fold error
	 */
	public static double foldError(Dataset<?> dataset) {
		double foldError = 0.0;
		for (Datapoint datapoint : dataset.getDatapoints()) {
			if (datapoint.getValue() > datapoint.getPredictedValue())
				foldError += datapoint.getValue() / datapoint.getPredictedValue();
			else
				foldError += datapoint.getPredictedValue() / datapoint.getValue();
		}
		return foldError;
	}

	/**
	 * Calculates the Area Under The Receiver Operator Caracteristic Curve.
	 *
	 * @param dataset the dataset
	 * @return the auc value
	 */
	public static double auc(Dataset<?> dataset) {
		int p = dataset.groupQuantity(1);
		int n = dataset.groupQuantity(-1);
		int fp = 0;
		int tp = 0;
		int fpPrev = 0;
		int tpPrev = 0;
		double a = 0.0;
		double fprev = Double.NEGATIVE_INFINITY;
		List<Datapoint> tempSet = new ArrayList<Datapoint>(dataset.getDatapoints());
		Collections.sort(tempSet, new Datapoint.PredictedValueComparatorDesc());
		for (Datapoint datapoint : tempSet) {
			if (datapoint.getPredictedValue() != fprev) {
				a += trapezoid_area(fp, fpPrev, tp, tpPrev);
				fprev = datapoint.getPredictedValue();
				fpPrev = fp;
				tpPrev = tp;
			}
			if (datapoint.getGroup() != null && datapoint.getGroup() == 1)
				tp++;
			else
				fp++;
		}
		a += trapezoid_area(fp, fpPrev, tp, tpPrev);
		a /= p * n;
		return a;
	}

	/**
	 * Trapezoid_area.
	 *
	 * @param x1 the x1
	 * @param x2 the x2
	 * @param y1 the y1
	 * @param y2 the y2
	 * @return the double
	 */
	private static double trapezoid_area(double x1, double x2, double y1, double y2) {
		double base = Math.abs(x1 - x2);
		double height = (y1 + y2) / 2.0;
		return base * height;
	}

	/**
	 * Outputs a single line of quality measurements specially for classification tasks.
	 *
	 * @param dataset the dataset
	 * @return string of classification measurements
	 */
	public static String toStringClassification(Dataset<?> dataset) {
		int numPositives = dataset.groupQuantity(1);
		int numNegatives = dataset.groupQuantity(-1);
		int correctPositives = dataset.numCorrectPredictions(1);
		int correctNegatives = dataset.numCorrectPredictions(-1);
		int total = numPositives + numNegatives;
		int TotalRight = correctPositives + correctNegatives;
		double fractPosRight = ((double) 100.0 * correctPositives / numPositives);
		double fractNegRight = ((double) 100.0 * correctNegatives / numNegatives);
		double mcc = mcc(correctPositives, correctNegatives, numNegatives - correctNegatives, numPositives - correctPositives);
		double auc = auc(dataset);
		return String.format("PosRight: %d of %d(%.2f%%) | NegRight %d of %d(%.2f%%) | total %d of %d(%.2f%%) | MCC: %.4f | AUC:%.4f", correctPositives, numPositives, fractPosRight, correctNegatives, numNegatives, fractNegRight, TotalRight, total, ((fractPosRight + fractNegRight) / 2), mcc, auc);
	}
	
	/**
	 * Outputs a single line of quality measurements specially for regression tasks.
	 *
	 * @param dataset the dataset
	 * @return the string of regression measurements
	 */
	public static String toStringRegression(Dataset<?> dataset) {
		return String.format("RMSD:%.4f | Q2:%.4f | GMFE:%.4f | PCC(r):%.4f", rmsd(dataset), r2q2(dataset), averageFoldError(dataset), pcc(dataset));
	}

	/**
	 * Returns a String of various quality tasks in cvs format.
	 *
	 * @param dataset the dataset
	 * @return the string
	 */
	public static String toCVS(Dataset<?> dataset) {
		int numPositives = dataset.groupQuantity(1);
		int numNegatives = dataset.groupQuantity(-1);
		int correctPositives = dataset.numCorrectPredictions(1);
		int correctNegatives = dataset.numCorrectPredictions(-1);
		int total = numPositives + numNegatives;
		int TotalRight = correctPositives + correctNegatives;
		double fractPosRight = ((double) 100.0 * correctPositives / numPositives);
		double fractNegRight = ((double) 100.0 * correctNegatives / numNegatives);
		double mcc = mcc(correctPositives, correctNegatives, numNegatives - correctNegatives, numPositives - correctPositives);
		return String.format("%d,%d,%.2f,%d,%d,%.2f,%d,%d,%.2f,%.4f", correctPositives, numPositives, fractPosRight, correctNegatives, numNegatives, fractNegRight, TotalRight, total, ((fractPosRight + fractNegRight) / 2), mcc);
	}

	/**
	 * Computes the Log loss of a dataset.
	 *
	 * @param dataset the dataset
	 * @param transformationFunction the transformation function
	 * @return the Log Loss value
	 */
	public static double logLoss(Dataset<?> dataset, TransformationFunctionInterface transformationFunction) {
		double eps = 0.00001;
		double logLoss = 0.0;
		for (Datapoint datapoint : dataset.getDatapoints()) {
			double measuredValue = 0.0;
			if (datapoint.getValue() > 0)
				measuredValue = 1.0;
			double predictedValue = transformationFunction.transform(datapoint.getPredictedValue());
			predictedValue = Math.max(predictedValue, eps);
			predictedValue = Math.min(predictedValue, 1.0 - eps);
			logLoss += measuredValue * Math.log(predictedValue) + (1.0 - measuredValue) * Math.log(1.0 - predictedValue);
		}
		return (-1.0 / dataset.size()) * logLoss;
	}

	// **************************************************************

}
