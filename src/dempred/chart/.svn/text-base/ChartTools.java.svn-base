package dempred.chart;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

public class ChartTools {
	private static final Logger logger = Logger.getLogger(ChartTools.class.getName());

	public static JFreeChart getSimpleChart(String name, XYSeries series) {
		XYSeriesCollection data = new XYSeriesCollection();
		data.addSeries(series);
		JFreeChart chart = ChartFactory.createScatterPlot("Test", "x", "y", data, PlotOrientation.VERTICAL, true, true, false);
		return chart;
	}

	public static JFreeChart simpleLineChart(double[] values) {
		double[] x = new double[values.length];
		for (int i = 0; i < values.length; ++i)
			x[i] = i;
		SimpleLineChart lineChart = new SimpleLineChart("simple line chart", "x", "y", x, values, false);
		return lineChart.generateChart();
	}

	public static void showChartAsFrame(JFreeChart chart) {
		ChartFrame frame = new ChartFrame("Chart", chart);
		frame.pack();
		frame.setSize(500, 500);
		frame.setVisible(true);
	}

	public static void saveChartAsJPG(String chartPath, JFreeChart chart, int width, int height) {
		try {
			ChartUtilities.saveChartAsJPEG(new File(chartPath + ".jpg"), chart, width, height);
		} catch (Exception e) {
			System.err.println(e.getStackTrace());
		}
	}

	public static void writeChartAsPDF(OutputStream out, JFreeChart chart, int width, int height, FontMapper mapper) throws IOException {
		Rectangle pagesize = new Rectangle(width, height);
		Document document = new Document(pagesize, 50, 50, 50, 50);
		try {
			PdfWriter writer = PdfWriter.getInstance(document, out);
			document.addAuthor("JFreeChart");
			document.addSubject("Demonstration");
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			PdfTemplate tp = cb.createTemplate(width, height);
			Graphics2D g2 = tp.createGraphics(width, height, mapper);
			Rectangle2D r2D = new Rectangle2D.Double(0, 0, width, height);
			chart.draw(g2, r2D, null);
			g2.dispose();
			cb.addTemplate(tp, 0, 0);
		} catch (DocumentException de) {
			logger.warning(de.getMessage());
		}
		document.close();
	}

	public static void saveChartAsPDF(String chartPath, JFreeChart chart, int width, int height, FontMapper mapper) {
		File file = new File(chartPath + ".pdf");
		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			writeChartAsPDF(out, chart, width, height, mapper);
			out.close();
		} catch (Exception de) {
			logger.warning(de.getMessage());
		}
	}
}
