package dempred.classifier;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.datastructure.DatasetResult;
import dempred.loggingtools.LoggerTools;
import dempred.resampling.CrossValidation;
import dempred.resampling.ResamplingErrorInterface;

public class ClassifierTools {
	private static final Logger logger = Logger.getLogger(ClassifierTools.class.getName());

	public static <T extends Datapoint> double optimizeParameter(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset, ResamplingErrorInterface<T> errorInterface, Field field, double[] parameterList) throws Exception {
		logger.fine(String.format("Optimizing parameter: %s ", field.getName()));
		int bestIndex = 0;
		double smallestError = Double.POSITIVE_INFINITY;
		field.setAccessible(true);
		int logLevel = LoggerTools.getLevel(logger).intValue();
		for (int i = 0; i < parameterList.length; ++i) {
			field.set(classifier, parameterList[i]);
			double error = errorInterface.error(classifier, trainset);
			if (error < smallestError) {
				smallestError = error;
				bestIndex = i;
			}
			if (logLevel <= Level.FINE.intValue()) {
				logger.fine(String.format("Setting: %s to %e", field.getName(), parameterList[i]));
				classifier.learn(trainset);
				classifier.predict(trainset);
				logger.fine(DatasetResult.toStringClassification(trainset));
				if (testset != null) {
					classifier.predict(testset);
					logger.fine(DatasetResult.toStringClassification(testset));
				}
				logger.fine(String.format("Estimated error: %.4f %n", error));
			}
		}
		logger.fine(String.format("Finished optimizing classifier parameters. Best value is: %e", parameterList[bestIndex]));
		field.set(classifier, parameterList[bestIndex]);
		return parameterList[bestIndex];
		// return smallestError;
	}

	public static <T extends Datapoint> double[] optimize2Parameters(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset, ResamplingErrorInterface<T> errorInterface, Field field1, double[] parameterList1, Field field2, double[] parameterList2) throws Exception {
		int logLevel = LoggerTools.getLevel(logger).intValue();
		double bestParameter1 = 0;
		double bestParameter2 = 0;
		double bestError = Double.POSITIVE_INFINITY;
		field1.setAccessible(true);
		field2.setAccessible(true);
		for (int i = 0; i < parameterList1.length; ++i) {
			if (logLevel <= Level.FINE.intValue()) {
				logger.fine(String.format("################ %n Setting: %s to %e %n", field1.getName(), parameterList1[i]));
				for (int j = 0; j < parameterList2.length; ++j) {
					logger.fine(String.format("%n Setting: %s to %e %n", field2.getName(), parameterList2[j]));
					if (parameterList1[i] + parameterList2[j] > 0.9)
						continue;
					field1.set(classifier, parameterList1[i]);
					field2.set(classifier, parameterList2[j]);
					double error = errorInterface.error(classifier, trainset);
					if (error < bestError) {
						bestError = error;
						bestParameter1 = parameterList1[i];
						bestParameter2 = parameterList2[j];
					}
					if (logLevel <= Level.FINE.intValue()) {
						classifier.learn(trainset);
						classifier.predict(trainset);
						classifier.predict(testset);
						logger.fine(DatasetResult.toStringClassification(trainset));
						logger.fine(DatasetResult.toStringClassification(testset));
						logger.fine(String.format("Estimated error: %.4f %n", error));
					}
				}
			}
		}
		field1.set(classifier, bestParameter1);
		field1.set(classifier, bestParameter2);
		double[] result = { bestParameter1, bestParameter2 };
		return result;
	}

	public static <T extends Datapoint> double setBestParameterMCC(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset, Field field, double[] parameterList) throws Exception {
		int bestIndex = 0;
		double bestPerformance = Double.NEGATIVE_INFINITY;
		field.setAccessible(true);
		int logLevel = LoggerTools.getLevel(logger).intValue();
		for (int i = 0; i < parameterList.length; ++i) {
			field.set(classifier, parameterList[i]);
			classifier.learn(trainset);
			classifier.predict(trainset);
			classifier.predict(testset);
			double performance = DatasetResult.mcc(testset);
			if (performance > bestPerformance) {
				bestPerformance = performance;
				bestIndex = i;
			}
			if (logLevel <= Level.FINE.intValue()) {
				logger.fine(String.format("Setting: %s to %e", field.getName(), parameterList[i]));
				logger.fine(DatasetResult.toStringClassification(trainset));
				logger.fine(DatasetResult.toStringClassification(testset));
				logger.fine(String.format("%n"));
			}
		}
		logger.fine(String.format("Finished optimizing classifier parameters. Best value is: %e", parameterList[bestIndex]));
		field.set(classifier, parameterList[bestIndex]);
		return parameterList[bestIndex];
	}

	public static <T extends Datapoint> double setBestParameterAUC(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset, Field field, double[] parameterList) throws Exception {
		int bestIndex = 0;
		double bestPerformance = Double.NEGATIVE_INFINITY;
		field.setAccessible(true);
		int logLevel = LoggerTools.getLevel(logger).intValue();
		for (int i = 0; i < parameterList.length; ++i) {
			field.set(classifier, parameterList[i]);
			classifier.learn(trainset);
			classifier.predict(trainset);
			classifier.predict(testset);
			double performance = DatasetResult.auc(testset);
			if (performance > bestPerformance) {
				bestPerformance = performance;
				bestIndex = i;
			}
			if (logLevel <= Level.FINE.intValue()) {
				logger.fine(String.format("Setting: %s to %e", field.getName(), parameterList[i]));
				logger.fine(DatasetResult.toStringClassification(trainset));
				logger.fine(DatasetResult.toStringClassification(testset));
				logger.fine(String.format("%n"));
			}
		}
		logger.fine(String.format("Finished optimizing classifier parameters. Best value is: %e", parameterList[bestIndex]));
		field.set(classifier, parameterList[bestIndex]);
		return parameterList[bestIndex];
	}
	
	public static <T extends Datapoint> double setBestParameterRMSD(ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset, Field field, double[] parameterList) throws Exception {
		int bestIndex = 0;
		double bestPerformance = Double.POSITIVE_INFINITY;
		field.setAccessible(true);
		int logLevel = LoggerTools.getLevel(logger).intValue();
		for (int i = 0; i < parameterList.length; ++i) {
			field.set(classifier, parameterList[i]);
			classifier.learn(trainset);
			classifier.predict(trainset);
			classifier.predict(testset);
			double performance = DatasetResult.rmsd(testset);
			if (performance < bestPerformance) {
				bestPerformance = performance;
				bestIndex = i;
			}
			if (logLevel <= Level.FINE.intValue()) {
				logger.fine(String.format("Setting: %s to %e", field.getName(), parameterList[i]));
				logger.fine(DatasetResult.toStringClassification(trainset));
				logger.fine(DatasetResult.toStringClassification(testset));
				logger.fine(String.format("%n"));
			}
		}
		logger.fine(String.format("Finished optimizing classifier parameters. Best value is: %e", parameterList[bestIndex]));
		field.set(classifier, parameterList[bestIndex]);
		return parameterList[bestIndex];
	}

	public static <T extends Datapoint> void predictCrossval(ClassifierInterface<T> classifier, Dataset<T> dataset, int numFolds, int numRounds) throws Exception{
		CrossValidation<T> crossValidation = new CrossValidation<T>(dataset);
		for (int round = 0; round < numRounds; ++round) {
			crossValidation.generateFolds(numFolds);
			for (int fold = 0; fold < numFolds; ++fold) {
				Dataset<T> crossMain = crossValidation.getFoldsExcept(fold);
				Dataset<T> crossFold = crossValidation.getFold(fold);
				classifier.learn(crossMain);
				classifier.predict(crossFold);
			}
		}
	}
}
