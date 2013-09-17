package com.demshape.dempred.examples;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.demshape.dempred.classifier.WrapperPrimal;
import com.demshape.dempred.datastructure.Datapoint;
import com.demshape.dempred.datastructure.Dataset;
import com.demshape.dempred.datastructure.DatasetGenerator;
import com.demshape.dempred.datastructure.DatasetNormalizer;
import com.demshape.dempred.datastructure.DatasetResult;
import com.demshape.dempred.grouper.AboveThreshold;
import com.demshape.dempred.losslunction.LogisticRegression;
import com.demshape.dempred.losslunction.Mse;
import com.demshape.dempred.math.DenseVector;
import com.demshape.dempred.math.SparseVector;

public class Example1 {

	public static final Logger logger = Logger.getLogger(Example1.class.getName());

	public static void main(String[] args) {

		try {
			// create a data set
			Dataset<Datapoint> dataset = new Dataset<Datapoint>();
			
			// create data points
			Datapoint negDatapoint = new Datapoint();
			negDatapoint.setValue(-1.0);
			negDatapoint.setGroup(-1);
			negDatapoint.setComment("measured via method a");
			// you may weight reliable data points higher
			negDatapoint.setWeight(1.0);
			negDatapoint.setFeatureVector(new DenseVector(new double[10]));
			
			
			Datapoint posDatapoint = new Datapoint();
			posDatapoint.setComment("measured via method b which is 4.5 times more reliable than method a");
			posDatapoint.setWeight(4.5);
			negDatapoint.setValue(1.0);
			negDatapoint.setGroup(1);
			negDatapoint.setFeatureVector(new DenseVector(new double[10]));
			
			// you may also create sparse representation
			negDatapoint.setFeatureVector(new SparseVector(new double[10]));

			// add data points to data set
			dataset.addDatapoint(posDatapoint);
			dataset.addDatapoint(negDatapoint);
			
			// split into train and test set
			ArrayList<Dataset<Datapoint>> splittedDataset = DatasetGenerator.split(dataset, 0.8);
			Dataset<Datapoint> trainset = splittedDataset.get(0);
			Dataset<Datapoint> testset = splittedDataset.get(1);

			// generate features
			// FeatureGeneratorInterface<Datapoint> featureGenerator = new
			// CustomFeatureGenerator()
			// DatasetManipulator.generateFeatures(dataset, featureGenerator);

			// normalize testset based on data derived from trainset
			DatasetNormalizer normalizer = new DatasetNormalizer();
			normalizer.train(trainset);
			normalizer.normalize(trainset);
			normalizer.normalize(testset);

			// instantiate linear classifier
			WrapperPrimal<Datapoint> classifier = new WrapperPrimal<Datapoint>();
			classifier.setGradLength(0.0001);
			classifier.setLambda2(0.0001);
			classifier.setLambda1(0.0);
			classifier.setW_plus(0.5);
			classifier.setLossFunction(new LogisticRegression<Datapoint>());
			// set if datapoint weights should beused or not
			classifier.setUseDWeights(true);
			
			classifier.setGroupAveraging(true);
			classifier.setSolver(0);
			classifier.setGrouper(new AboveThreshold(0.0));

			// set logger of classifier class to see detailed training output
			Logger.getLogger(WrapperPrimal.class.getName()).setLevel(Level.FINEST);
			Logger.getLogger(WrapperPrimal.class.getName()).setParent(logger);

			// extend to multiclass metaclassifier
			// MultiClass<MultigroupDatapoint> multiClassifier = new
			// MultiClass<MultigroupDatapoint>(classifier, trainset);
			// Logger.getLogger(MultiClass.class.getName()).setParent(logger);

			// learn classifier
			classifier.learn(trainset);

			// predict train set (recall)
			classifier.predict(trainset);

			// predict testset (prediction)
			classifier.predict(testset);

			// compute prediction statistics
			double predictionAUC = DatasetResult.auc(testset);
			double predictionMCC = DatasetResult.mcc(testset);
			double msePredictionLoss = DatasetResult.lossFunction(testset, new Mse<Datapoint>());
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logger.info("Programe finished.");
		}

	}

}
