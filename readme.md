#DemPred

##Create a data set
	Dataset<Datapoint> dataset = new Dataset<Datapoint>();

##Create data points
	Datapoint negDatapoint = new Datapoint();
	negDatapoint.setValue(-1.0);
	negDatapoint.setGroup(-1);

	Datapoint posDatapoint = new Datapoint();
	posDatapoint.setValue(1.0);
	posDatapoint.setGroup(1);

###you may weight reliable data points higher
	negDatapoint.setComment("measured via method a");
	negDatapoint.setWeight(1.0);

	
	posDatapoint.setComment("measured via method b (4.5 times more reliable than method a)");
	posDatapoint.setWeight(4.5);


	
##Create fature vectors
	negDatapoint.setFeatureVector(new DenseVector(new double[10]));
	posDatapoint.setFeatureVector(new DenseVector(new double[10]));

###Sparse vector representation for large sparse data set
	negDatapoint.setFeatureVector(new SparseVector(new double[10]));
	posDatapoint.setFeatureVector(new SparseVector(new double[10]));

##Adding Datapoints to a Dataset
	dataset.addDatapoint(negDatapoint);
	dataset.addDatapoint(posDatapoint);

##Here's an example how to read from a simple .csv file
	private static final Pattern splitRegex = Pattern.compile(",");

	public static Dataset<Datapoint> readFromCVS(File file) throws Exception {
		Dataset<Datapoint> dataset = new Dataset<Datapoint>();
		BufferedReader bufReader = new BufferedReader(new FileReader(file));
		String line;
		int linenumber = 0;
		while ((line = bufReader.readLine()) != null) {
			logger.fine("Reading line:" + (++linenumber));
			if (!line.startsWith("#") && !line.trim().isEmpty()) {
				int featureIndex = 0;
				String value;
				String[] splittet = splitRegex.split(line);
				Datapoint datapoint = new Datapoint();
				if (!(value = splittet[featureIndex++].trim()).isEmpty())
					datapoint.setComment(value);
				if (!(value = splittet[featureIndex++].trim()).isEmpty())
					datapoint.setGroup(Integer.parseInt(value));
				if (!(value = splittet[featureIndex++].trim()).isEmpty())
					datapoint.setValue(Double.parseDouble(value));
				if (!(value = splittet[featureIndex++].trim()).isEmpty())
					datapoint.setWeight(Double.parseDouble(value));
				double[] featureVector = new double[splittet.length - featureIndex];
				for (int i = featureIndex; i < splittet.length; ++i)
					featureVector[i - featureIndex] = Double.parseDouble(splittet[i]);
				datapoint.setFeatureVector(new DenseVector(featureVector));
				dataset.addDatapoint(datapoint);
			}
		}
		bufReader.close();
		return dataset;
	}

##Dataset Manipulations
### Split into train and test set
	ArrayList<Dataset<Datapoint>> splittedDataset = DatasetGenerator.split(dataset, 0.8);
	Dataset<Datapoint> trainset = splittedDataset.get(0);
	Dataset<Datapoint> testset = splittedDataset.get(1);

##Creating Features
	FeatureGeneratorInterface<Datapoint> featureGenerator = new CustomFeatureGenerator<Datapoint>()
	DatasetManipulator.generateFeatures(dataset, featureGenerator);

## Normalize test set based on data derived from train set
	DatasetNormalizer normalizer = new DatasetNormalizer();
	normalizer.train(trainset);
	normalizer.normalize(trainset);
	normalizer.normalize(testset);

## Instantiate Linear Classifier
	WrapperPrimal<Datapoint> classifier = new WrapperPrimal<Datapoint>();
	classifier.setGradLength(0.0001);
	classifier.setLambda2(0.0001);
	classifier.setLambda1(0.0);
	classifier.setW_plus(0.5);
	classifier.setLossFunction(new LogisticRegression<Datapoint>());
### set if datapoint weights should be used or not
	classifier.setUseDWeights(true);

### set if unbalanced datasets should be balanced
	classifier.setGroupAveraging(true);
	
	classifier.setSolver(0);
	classifier.setGrouper(new AboveThreshold(0.0));

### set logger of classifier class to see detailed training output
	Logger.getLogger(WrapperPrimal.class.getName()).setLevel(Level.FINEST);
	Logger.getLogger(WrapperPrimal.class.getName()).setParent(logger);
	
### extend to multiclass metaclassifier
	MultiClass<MultigroupDatapoint> multiClassifier = new
	MultiClass<MultigroupDatapoint>(classifier, trainset);
	Logger.getLogger(MultiClass.class.getName()).setParent(logger);
	
## Learn Classifier
	classifier.learn(trainset);

### predict train set (recall)
	classifier.predict(trainset);

### predict testset (prediction)
	classifier.predict(testset);

## Compute Prediction Statistics
	double predictionAUC = DatasetResult.auc(testset);
	double predictionMCC = DatasetResult.mcc(testset);
	double msePredictionLoss = DatasetResult.lossFunction(testset, new Mse<Datapoint>());
## Generate Charts
	JFreeChart chart = RocCurve.showRocCurve("AUC", trainset, testset);
	ChartTools.showChartAsFrame(chart);
	ChartTools.saveChartAsJPG("C:/path/results", chart, 500, 500);