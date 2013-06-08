package dempred.util;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Serializer {

	public static void saveToFile(File file, Object... objects) throws IOException {
		OutputStream fos = new FileOutputStream(file);
		ObjectOutputStream o = new ObjectOutputStream(fos);
		for (Object object : objects)
			o.writeObject(object);
		fos.close();
	}

	public static ArrayList<Object> readFromFile(File file) throws FileNotFoundException, IOException, ClassNotFoundException {
		ArrayList<Object> result = new ArrayList<Object>();
		InputStream fis = new FileInputStream(file);
		ObjectInputStream o = new ObjectInputStream(fis);
		try {
			while (true)
				result.add(o.readObject());

		} catch (EOFException e) {
			fis.close();
		}
		return result;
	}
}
