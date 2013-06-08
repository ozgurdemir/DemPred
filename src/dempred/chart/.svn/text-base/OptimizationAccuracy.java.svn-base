package dempred.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;


import com.lowagie.text.pdf.DefaultFontMapper;

import dempred.classifier.ClassifierInterface;
import dempred.datastructure.Datapoint;
import dempred.datastructure.Dataset;
import dempred.datastructure.DatasetResult;
import dempred.resampling.ResamplingErrorInterface;

public class OptimizationAccuracy {
	private static final Logger logger = Logger.getLogger(OptimizationAccuracy.class.getName());
	public static int width = 600;
	public static int height = 400;

	public static <T extends Datapoint> void optimizationPlot(String title, ClassifierInterface<T> classifier, Dataset<T> trainset, Dataset<T> testset, ResamplingErrorInterface<T> errorInterface, Field field, double[] parameterList, String chartPath) throws Exception {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		DefaultBoxAndWhiskerCategoryDataset bwdataset = new DefaultBoxAndWhiskerCategoryDataset();
		String[] labelList = { "1e-10", "1e-9", "1e-8", "1e-7", "1e-6", "1e-5", "1e-4", "1e-3", "1e-2", "1e-1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", "0.93" };
		double[] errors = new double[parameterList.length];
		int bestIndex = 0;
		double smallestError = Double.POSITIVE_INFINITY;
		field.setAccessible(true);
		for (int i = 0; i < parameterList.length; ++i) {
			logger.fine(String.format("Setting: %s to %e", field.getName(), parameterList[i]));
			field.set(classifier, parameterList[i]);
			double error = errorInterface.error(classifier, trainset);
			errors[i] = error;
			if (error < smallestError) {
				smallestError = error;
				bestIndex = i;
			}
			ArrayList<Double> reversedErrors = new ArrayList<Double>(errors.length);
			for (Double value : errors)
				reversedErrors.add(1 - value);
			bwdataset.add(reversedErrors, "errors", String.format("%s", labelList[i]));
			dataset.addValue(1 - error, "estimated weighted accuracy", String.format("%s", labelList[i]));
			classifier.learn(trainset);
			classifier.predict(trainset);
			classifier.predict(testset);
			logger.fine(DatasetResult.toStringClassification(trainset));
			logger.fine(DatasetResult.toStringClassification(testset));
			logger.fine(String.format("Estimated weighted accuracy: %.4f %n", 1 - error));
			// double trueErr = ((double) testresult.getCorrectPositives() + testresult.getCorrectNegatives()) / (testresult.getNumNegatives() + testresult.getNumPositives());
			double trueErr = DatasetResult.mcc(testset);
			dataset.addValue(trueErr, "true weighted accuracy", String.format("%s", labelList[i]));
		}
		logger.fine(String.format("Best value for: %s is %e", field.getName(), parameterList[bestIndex]));

		// Formatierungen
		// chart.getLegend().setItemFont(new Font("SansSerif",10,20));
		// chart.getTitle().setFont(new Font("SansSerif",10,40));
		// CategoryAxis domainAxis = plot.getDomainAxis();
		// domainAxis.setLabelFont(new Font("SansSerif",10,30));
		// domainAxis.setTickLabelFont(new Font("SansSerif",10,20));
		// ValueAxis rangeAxis = plot.getRangeAxis();
		// rangeAxis.setLabelFont(new Font("SansSerif",10,30));
		// rangeAxis.setTickLabelFont(new Font("SansSerif",10,20));
		// rangeAxis.setRange(0.0, 1.0);
		// domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

		// overlayed chart
		CategoryPlot overlaidplot = new CategoryPlot();
		overlaidplot.setBackgroundPaint(Color.white);
		overlaidplot.setDomainGridlinePaint(Color.lightGray);
		overlaidplot.setRangeGridlinePaint(Color.lightGray);
		overlaidplot.setDomainGridlinesVisible(true);
		overlaidplot.setDataset(dataset);

		CategoryItemRenderer linerenderer = new LineAndShapeRenderer();
		linerenderer.setSeriesPaint(0, Color.red);
		linerenderer.setSeriesPaint(1, Color.black);
		linerenderer.setSeriesStroke(0, new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 6.0f, 6.0f }, 0.0f));
		linerenderer.setSeriesStroke(1, new BasicStroke(4.0f));

		overlaidplot.setRenderer(linerenderer);
		overlaidplot.setDomainAxis(new CategoryAxis("lambda"));
		overlaidplot.setRangeAxis(new NumberAxis("weighted accuracy"));
		overlaidplot.setOrientation(PlotOrientation.VERTICAL);
		overlaidplot.setRangeGridlinesVisible(true);
		overlaidplot.setDomainGridlinesVisible(true);

		BoxAndWhiskerRenderer bwrenderer = new BoxAndWhiskerRenderer();
		bwrenderer.setFillBox(false);
		bwrenderer.setBasePaint(Color.yellow);
		bwrenderer.setArtifactPaint(Color.red);
		bwrenderer.setBaseFillPaint(Color.yellow);
		overlaidplot.setDataset(1, bwdataset);
		overlaidplot.setRenderer(1, bwrenderer);
		overlaidplot.mapDatasetToRangeAxis(1, 0);

		overlaidplot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
		overlaidplot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		JFreeChart overlaidchart = new JFreeChart(overlaidplot);
		overlaidchart.setTitle(title);

		ChartTools.saveChartAsJPG(chartPath, overlaidchart, width, height);
		ChartTools.saveChartAsPDF(chartPath, overlaidchart, width, height, new DefaultFontMapper());

		ChartFrame frame = new ChartFrame("First", overlaidchart);
		frame.pack();
		frame.setVisible(true);
	}

}
