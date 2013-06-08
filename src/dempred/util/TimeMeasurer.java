package dempred.util;

public class TimeMeasurer {

	public static final int info = 0;
	public static final int warning = 1;
	private int level;
	private long startTime;
	private long breakTime;

	public TimeMeasurer() {
		startTime = System.currentTimeMillis();
		breakTime = startTime;
	}

	public long getTotalTime() {
		return (System.currentTimeMillis() - startTime);
	}

	public void getTotalTime(String message) {
			System.out.println(message + " " + msec2String(getTotalTime()));
	}

	public long setBreak() {
		long tmp = System.currentTimeMillis();
		long diff = tmp - breakTime;
		breakTime = tmp;
		return diff;
	}

	public void setBreak(String message) {
			System.out.println(message + " " + msec2String(setBreak()));
	}
	
	public void message(String message) {
			System.out.println(message);
	}

	private String msec2String(long msec) {
		long seconds = msec / 1000;
		int sec = (int) seconds % 60;
		int min = (int) (seconds / 60) % 60;
		int hour = (int) (seconds / 60 / 60) % 24;
		return String.format("%02d:%02d:%02d", hour, min, sec);
	}

	public final int getLevel() {
		return level;
	}

	public final void setLevel(int level) {
		this.level = level;
	}

}
