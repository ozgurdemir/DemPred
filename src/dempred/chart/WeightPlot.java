package dempred.chart;

import java.awt.Color;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import dempred.math.VectorInterface;

public class WeightPlot {

	private String title;
	private VectorInterface weights;
	private List<String> featureNames;
	private Double offset;

	public WeightPlot(String title, VectorInterface weights) {
		super();
		this.title = title;
		this.weights = weights;
	}

	public WeightPlot(String title, VectorInterface weights, List<String> featureNames) {
		super();
		this.title = title;
		this.weights = weights;
		this.featureNames = featureNames;
	}

	public WeightPlot(String title, VectorInterface weights, List<String> featureNames, double offset) {
		super();
		this.title = title;
		this.weights = weights;
		this.featureNames = featureNames;
		this.offset = offset;
	}

	public JFreeChart generateChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < weights.size(); ++i) {
			String column = Integer.toString(i);
			if (this.featureNames != null)
				column = featureNames.get(i);
			dataset.addValue(weights.get(i), "1", column);
		}
		if (this.offset != null)
			dataset.addValue(this.offset, "1", "offset b");

		JFreeChart chart = ChartFactory.createBarChart(title, "feature", "coefficient", dataset, PlotOrientation.VERTICAL, false, false, false);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setDomainGridlinePaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.lightGray);

		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));

		BarRenderer renderer = (BarRenderer) plot.getRenderer();
		renderer.setDrawBarOutline(false);
		renderer.setShadowVisible(false);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesFillPaint(0, Color.red);
		return chart;
	}
}
