package com.demshape.dempred.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.thoughtworks.xstream.XStream;

public class Lib {

	public static void writeToFile(String message, String filename, boolean append) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(filename, append));
		out.write(message);
		out.flush();
		out.close();
	}

	public static void deleteFile(String filepath) {
		try {
			File myFile = new File(filepath);
			myFile.delete();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static <T> void printArrayOfObjects(T[] objects) {
		for (T x : objects) {
			System.out.println(x);
		}
	}

	public static <T> String Collection2String(Collection<T> objects, String delimiter) {
		StringBuffer strBuf = new StringBuffer(2000);
		for (T x : objects)
			strBuf.append(x + delimiter);
		return strBuf.toString();
	}

	public static int[] parseIntString(String input) {
		String[] splitted = input.split(",");
		int[] result = new int[splitted.length];
		for (int i = 0; i < splitted.length; ++i)
			result[i] = Integer.parseInt(splitted[i].trim());
		return result;
	}

	public static int[] deleteFromArray(int[] array, int index) {
		int[] result = new int[array.length - 1];
		int indexPointer = 0;
		for (int i = 0; i < array.length; ++i) {
			if (i != index)
				result[indexPointer++] = array[i];
		}
		return result;
	}

	public static Field getDeclaredField(Object o, String fieldName) throws NoSuchFieldException {
		try {
			return o.getClass().getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			return o.getClass().getSuperclass().getDeclaredField(fieldName);
		}
	}

	public static double ic50toPIC50(double x) {
		return -Math.log10(x * 1e-9);
	}

	public static double pic50toIC50(double x) {
		return (Math.pow(10, -x) / (1e-9));
	}

	public static Map<String, String> readFasta(File input) throws Exception {
		Map<String, String> resultHash = new HashMap<String, String>();
		BufferedReader bufReader = new BufferedReader(new FileReader(input));
		String line;
		String id = "";
		StringBuffer sequence = null;
		while ((line = bufReader.readLine()) != null) {
			if (line.startsWith(">")) {
				if (sequence != null)
					resultHash.put(id, sequence.toString());
				id = line.substring(1).trim();
				sequence = new StringBuffer();
			} else {
				sequence.append(line.trim());
			}
		}
		resultHash.put(id, sequence.toString());
		bufReader.close();
		return resultHash;
	}

	public static <T, E> String Map2String(Map<T, E> map, String keyValueDelimiter, String entryDelimiter) {
		StringBuffer output = new StringBuffer();
		for (Entry<T, E> entry : map.entrySet()) {
			output.append(entry.getKey());
			output.append(keyValueDelimiter);
			output.append(entry.getValue());
			output.append(entryDelimiter);
		}
		return output.toString();
	}

	public static void writeXml(File file, Object o) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		XStream xstream = new XStream();
		ObjectOutputStream xmlout = xstream.createObjectOutputStream(out);
		xmlout.writeObject(o);
		xmlout.flush();
		out.flush();
		xmlout.close();
		out.close();
	}

	public static <T> T readXml(File file, T object) throws IOException, ClassNotFoundException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		XStream xstream = new XStream();
		ObjectInputStream inXml = xstream.createObjectInputStream(in);
		return (T) inXml.readObject();
	}

	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

	public static Set<String> readConfFile(File file) throws Exception {
		String line;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		HashSet<String> configs = new HashSet<String>();
		while ((line = reader.readLine()) != null) {
			if (!line.startsWith("#") && !line.trim().isEmpty())
				configs.add(line.trim());
		}
		return configs;
	}
	
	public static void writeConfFile(File file, List<String> content) throws Exception {
		BufferedWriter out = new BufferedWriter(new FileWriter(file, false));
		for(String line: content){
			out.write(line);
			out.newLine();
		}
		out.flush();
		out.close();
	}

}
