package dempred.util;

public class ProgressHelper {
	private double total;
	private double status;
	private double precision;
	private int currentStatus;

	public ProgressHelper(double total, double precision) {
		super();
		this.total = total;
		this.precision = precision;
		this.status = 0;
		this.currentStatus = 0;
	}

	public boolean statusChanged() {
		++currentStatus;
		return statusChanged(currentStatus);
	}

	public boolean statusChanged(int status) {
		if ((status - this.status) / total > precision || status >= total) {
			this.status = status;
			return true;
		}
		return false;
	}

	public int getStatusAsPercent() {
		return (int) Math.round((status / total) * 100.0);
	}

}
