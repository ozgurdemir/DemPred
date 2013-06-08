package dempred.util;
import java.io.File;
import java.io.FilenameFilter;

public class FileNameFilter implements FilenameFilter {
	private String fileExtension;

	public FileNameFilter(String fileExtension) {
		super();
		this.fileExtension = fileExtension;
	}

	@Override
	public boolean accept(File file, String name) {
		return (name.endsWith(this.fileExtension));
	}

}
