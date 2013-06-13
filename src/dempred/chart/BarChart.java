package dempred.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

public class BarChart {
	private String title;
	private String xAxisTitle;
	private String yAxisTitle;
	private String[] names;
	private double[] values;
	private boolean legend;
	private PlotOrientation orientation;

	public BarChart(String title, String xAxisTitle, String yAxisTitle, String[] names, double[] values, boolean legend, PlotOrientation orientation) {
		super();
		this.names = names;
		this.values = values;
		this.title = title;
		this.legend = legend;
		this.xAxisTitle = xAxisTitle;
		this.yAxisTitle = yAxisTitle;
		this.orientation = orientation;
	}

	public JFreeChart generateChart() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < names.length; ++i)
			dataset.addValue(values[i], "Series 1", names[i]);

		JFreeChart chart = ChartFactory.createBarChart(title, xAxisTitle, yAxisTitle, dataset, orientation, legend, false, false);
		CategoryPlot plot = chart.getCategoryPlot();
		plot.setForegroundAlpha(1.0f);
		// left align the category labels...
		CategoryAxis axis = plot.getDomainAxis();
		CategoryLabelPositions p = axis.getCategoryLabelPositions();
		CategoryLabelPosition left = new CategoryLabelPosition(RectangleAnchor.LEFT, TextBlockAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, 0.0, CategoryLabelWidthType.RANGE, 0.30f);
		axis.setCategoryLabelPositions(CategoryLabelPositions.replaceLeftPosition(p, left));

		return chart;

	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final String getXAxisTitle() {
		return xAxisTitle;
	}

	public final void setXAxisTitle(String axisTitle) {
		xAxisTitle = axisTitle;
	}

	public final String getYAxisTitle() {
		return yAxisTitle;
	}

	public final void setYAxisTitle(String axisTitle) {
		yAxisTitle = axisTitle;
	}

	public final String[] getNames() {
		return names;
	}

	public final void setNames(String[] names) {
		this.names = names;
	}

	public final double[] getValues() {
		return values;
	}

	public final void setValues(double[] values) {
		this.values = values;
	}

	public final boolean isLegend() {
		return legend;
	}

	public final void setLegend(boolean legend) {
		this.legend = legend;
	}

	public final PlotOrientation getOrientation() {
		return orientation;
	}

	public final void setOrientation(PlotOrientation orientation) {
		this.orientation = orientation;
	}

}
