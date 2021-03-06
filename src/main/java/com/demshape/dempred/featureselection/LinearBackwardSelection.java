package com.demshape.dempred.featureselection;

import java.lang.reflect.Field;
import java.util.logging.Logger;

import com.demshape.dempred.classifier.ClassifierTools;
import com.demshape.dempred.classifier.WrapperPrimal;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.datastructure.DatasetResult;
import com.demshape.dempred.math.DenseVector;
import com.demshape.dempred.math.VectorInterface;
import com.demshape.dempred.resampling.CrossValidation;
import com.demshape.dempred.resampling.ResamplingErrorInterface;
import com.demshape.dempred.util.Lib;



public class LinearBackwardSelection {

	private static final Logger logger = Logger.getLogger(LinearBackwardSelection.class.getName());
	private double[] lambdaValues = { 0.001, 0.1, 0.3, 0.5, 0.7, 0.9, 0.95 };

	// backwart selection. Entweder training mit lambda werten und dann kleinste
	// gewichte entfernen, oder trainieren und features anhand der
	// auswirkung auf objective function ranken
	// die featureanzahl wird pro schritt geteilt, bis nur noch s features
	// vorhanden sind, danach immer eins loeschen
	public <T extends Datapoint> void select(WrapperPrimal<T> classifier, Dataset<T> trainset, Dataset<T> testset, int rankMethod, int speed, int numFeatures, ResamplingErrorInterface<T> resampler) throws Exception {
		VectorInterface rank;
		int[] indices;
		while (trainset.numFeatures() > numFeatures) {
			// Field field = Lib.getDeclaredField(classifier, "lambda");
			// Logger.getLogger(ClassifierTools.class.getName()).setParent(logger);
			// ClassifierTools.optimizeParameter(classifier, trainset, testset, resampler, field, lambdaValues);
			classifier.learn(trainset);
			classifier.predict(trainset);
			logger.fine(trainset.getFeatureIndex().toString());
			logger.fine(classifier.getWeight().toString());
			logger.fine(DatasetResult.toStringClassification(trainset));
			if (testset != null) {
				classifier.predict(testset);
				logger.fine(DatasetResult.toStringClassification(testset));
			}
			if (rankMethod == 0)
				rank = classifier.effectObjFunc(trainset);
			else if (rankMethod == 1) {
				rank = classifier.getWeight().clone();
				rank.reduceByOne();
				rank.abs();
			} else if (rankMethod == 2)
				rank = classifier.effectObjRetrain(trainset);
			else
				throw new IllegalArgumentException("The selected ranking method does not exist: " + rankMethod);
			int numDeletions = (int) (rank.size() / 2);
			if (numDeletions < numFeatures)
				numDeletions = rank.size() - numFeatures;
			if (trainset.numFeatures() < speed || numDeletions > trainset.numFeatures())
				indices = rank.minIndex(1);
			else
				indices = rank.minIndex(numDeletions);
			trainset.deleteFeatures(indices);
			if (testset != null)
				testset.deleteFeatures(indices);
			logger.fine(String.format("%d features left.%n", trainset.numFeatures()));
		}

	}

	// echtes backward training, bei der zusaetzlich anhand einer crossvalidation auf einem
	// Validationsset geranked wird, so dass selektierte features besser generalisieren
	public <T extends Datapoint> void selectCrossVal(WrapperPrimal<T> classifier, Dataset<T> trainset, Dataset<T> testset, int rankMethod, int speed, int numFeatures, double fraction, ResamplingErrorInterface<T> resampler) throws Exception {
		int numFolds = 10;
		// int numRounds = 1;
		int[] indices;
		VectorInterface rank;
		CrossValidation<T> crossVal = new CrossValidation<T>(trainset);
		while (trainset.numFeatures() > numFeatures) {
			// optimiere lambda wert
			Field field = Lib.getDeclaredField(classifier, "lambda");
			ClassifierTools.optimizeParameter(classifier, trainset, testset, resampler, field, lambdaValues);
			classifier.learn(trainset);
			classifier.predict(trainset);
			logger.fine(trainset.getFeatureIndex().toString());
			logger.fine(classifier.getWeight().toString());
			logger.fine(DatasetResult.toStringClassification(trainset));
			if (testset != null) {
				classifier.predict(testset);
				logger.fine(DatasetResult.toStringClassification(testset));
			}
			logger.fine(String.format("%n"));

			// berechne effect obj der features
			// rank = classifier.effectObjFunc(trainset).divScalar(trainset.size());
			rank = classifier.effectObjFunc(trainset);
			VectorInterface rankGen = new DenseVector(trainset.numFeatures(), 0.0);
			crossVal.generateFolds(numFolds);
			for (int foldNumber = 0; foldNumber < numFolds; ++foldNumber) {
				logger.fine(String.format("estimating generalization performance fold:%d of %d", foldNumber + 1, numFolds));
				Dataset<T> crossMain = crossVal.getFoldsExcept(foldNumber);
				Dataset<T> crossFold = crossVal.getFold(foldNumber);
				classifier.learn(crossMain);
				classifier.predict(crossMain);
				classifier.predict(crossFold);
				logger.fine(DatasetResult.toStringClassification(crossMain));
				logger.fine(DatasetResult.toStringClassification(crossFold));
				// rankGen.addVector(classifier.effectObjFunc(crossFold).divScalar(crossFold.size()));
				rankGen.addVector(classifier.effectObjFunc(crossFold));
			}
			rankGen.divScalar(numFolds);
			rank.mulScalar(1.0 - fraction).addVector(rankGen.mulScalar(fraction));

			int numDeletions = (int) (rank.size() / 2);
			if (numDeletions < numFeatures)
				numDeletions = rank.size() - numFeatures;
			if (trainset.numFeatures() < speed || numDeletions > trainset.numFeatures()) {
				indices = rank.minIndex(1);
				trainset.deleteFeatures(indices);
				if (testset != null)
					testset.deleteFeatures(indices);
			} else {
				indices = rank.minIndex(numDeletions);
				trainset.deleteFeatures(indices);
				if (testset != null)
					testset.deleteFeatures(indices);
			}
			logger.fine(String.format("%d features left.", trainset.numFeatures()));
		}
	}

	public <T extends Datapoint> void selectWithCompare(WrapperPrimal<T> classifier, Dataset<T> trainset, Dataset<T> testset, int rankMethod, int speed, double fraction) throws Exception {
		VectorInterface rank;
		int[] indices;
		Lib.deleteFile("/user/odemir/coepradatasets/programoutputs/rankGen.txt");
		VectorInterface rankGen = DatasetComparator.compare(testset, trainset);
		while (trainset.numFeatures() > 1) {

			classifier.learn(trainset);
			classifier.predict(trainset);
			classifier.predict(testset);
			logger.fine(DatasetResult.toStringClassification(trainset));
			logger.fine(DatasetResult.toStringClassification(testset));
			if (rankMethod == 0)
				rank = classifier.effectObjFunc(trainset);
			else if (rankMethod == 1) {
				rank = classifier.getWeight().clone();
				rank.reduceByOne();
				rank.abs();
			} else
				throw new IllegalArgumentException("The selected ranking method does not exist: " + rankMethod);
			rank.divScalar(rank.clone().abs().max());
			// rankGen.divScalar(rankGen.clone().abs().max());
			Lib.writeToFile(rank.toString() + System.getProperty("line.separator"), "/user/odemir/coepradatasets/programoutputs/rankGen.txt", true);
			rank.mulScalar(1.0 - fraction).addVector(rankGen.clone().mulScalar(fraction));
			int numDeletions = (int) (rank.size() / 2);
			if (trainset.numFeatures() < speed || numDeletions > trainset.numFeatures()) {
				indices = rank.minIndex(1);
				trainset.deleteFeatures(indices);
				testset.deleteFeatures(indices);
				rankGen.delete(indices);
			} else {
				indices = rank.minIndex(numDeletions);
				trainset.deleteFeatures(indices);
				testset.deleteFeatures(indices);
				rankGen.delete(indices);
			}
			double lambda_2 = classifier.getLambda2();
			classifier.setLambda2(0.00000000001);
			classifier.learn(trainset);
			classifier.predict(trainset);
			classifier.predict(testset);
			logger.fine(DatasetResult.toStringClassification(trainset));
			logger.fine(DatasetResult.toStringClassification(testset));
			classifier.setLambda2(lambda_2);
			System.out.println(trainset.getFeatureIndex());
		}

	}

	// **************************************************************************
	// ***************************************
	// **************************************************************************
	// ***************************************
	// //// old functions
	// **************************************************************************
	// ***************************************
	// **************************************************************************
	// ***************************************

	// backward selection, bei der nach jedem schritt nur die features entfernt
	// werden, die einen schlechten bzw.
	// gar keinen einfluss auf die Objective Function haben
	public <T extends Datapoint> void selectObjective(WrapperPrimal<T> classifier, Dataset<T> trainset, Dataset<T> testset) throws Exception {
		VectorInterface rank;
		boolean flag = true;
		int[] indices;
		while (flag) {
			classifier.learn(trainset);
			classifier.predict(trainset);
			classifier.predict(testset);
			logger.fine(DatasetResult.toStringClassification(trainset));
			logger.fine(DatasetResult.toStringClassification(testset));
			rank = classifier.effectObjFunc(trainset);
			indices = rank.findIndices("<=", 0.0);
			System.out.println("Max effect: " + rank.max());
			System.out.println("Number of negative and zero effects: " + indices.length);
			trainset.deleteFeatures(indices);
			testset.deleteFeatures(indices);
			double lambda_2 = classifier.getLambda2();
			classifier.setLambda2(0.00001);
			classifier.learn(trainset);
			classifier.predict(trainset);
			classifier.predict(testset);
			logger.fine(DatasetResult.toStringClassification(trainset));
			logger.fine(DatasetResult.toStringClassification(testset));
			classifier.setLambda2(lambda_2);
			if (indices.length == 0)
				flag = false;
			System.out.println(trainset.getFeatureIndex());
		}

	}

	public <T extends Datapoint> void selectCrossValFake(WrapperPrimal<T> classifier, Dataset<T> trainset, Dataset<T> testset, int rankMethod, int speed, double fraction) throws Exception {
		VectorInterface rank;
		VectorInterface rankTest;
		int[] indices;
		classifier.learn(trainset);
		classifier.predict(trainset);
		classifier.predict(testset);
		while (trainset.numFeatures() > 1) {
			classifier.learn(trainset);
			classifier.predict(trainset);
			classifier.predict(testset);
			logger.fine(DatasetResult.toStringClassification(trainset));
			logger.fine(DatasetResult.toStringClassification(testset));
			if (rankMethod == 0) {
				rank = classifier.effectObjFunc(trainset);
				// classifier.learn(testset);
				rankTest = classifier.effectObjFunc(testset);
				rank.divScalar(rank.clone().abs().max());
				rankTest.divScalar(rankTest.clone().abs().max());
				rank.mulScalar(1.0 - fraction).addVector(rankTest.mulScalar(fraction));
			} else if (rankMethod == 1) {
				rank = classifier.getWeight().clone();
				rank.reduceByOne();
				classifier.learn(testset);
				rankTest = classifier.getWeight().clone();
				rankTest.reduceByOne();
				rank.abs();
				rankTest.abs();
				rank.divScalar(rank.max());
				rankTest.divScalar(rankTest.max());
				rank.mulScalar(1.0 - fraction).addVector(rankTest.mulScalar(fraction));
			} else
				throw new IllegalArgumentException("The selected ranking method does not exist: " + rankMethod);
			int numDeletions = (int) (rank.size() / 2);
			if (trainset.numFeatures() < speed || numDeletions > trainset.numFeatures()) {
				indices = rank.minIndex(1);
				trainset.deleteFeatures(indices);
				testset.deleteFeatures(indices);
			} else {
				indices = rank.minIndex(numDeletions);
				trainset.deleteFeatures(indices);
				testset.deleteFeatures(indices);
			}
			logger.fine(String.format("%d features left.%n", trainset.numFeatures()));
		}
	}
}
