package com.demshape.dempred.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.logging.Logger;

import org.kohsuke.args4j.CmdLineParser;

public class ParameterParser extends CmdLineParser {
	private Object bean;
	private static final Logger logger = Logger.getLogger(ParameterParser.class.getName());

	public ParameterParser(Object bean) {
		super(bean);
		this.bean = bean;
		ParameterParser.registerHandler(Boolean.class, WorkingBooleanOptionHandler.class);
	}

	public void parseArgumentFromFile(File file) throws Exception {
		BufferedReader bufReader = new BufferedReader(new FileReader(file));
		String line, key = "", value;
		int index;
		while ((line = bufReader.readLine()) != null) {
			if (!line.startsWith("#") && line.trim().length() > 0) {
				index = line.indexOf(":");
				key = line.substring(0, index).trim();
				value = line.substring(index + 1).trim();
				String[] parameters = { "-" + key, value };
				parseArgument(parameters);
			}
		}
		bufReader.close();
	}

	public String toString() {
		StringBuffer strBuffer = new StringBuffer(2000);
		String lineSeperator = System.getProperty("line.separator");
		for (Field x : bean.getClass().getFields())
			try {
				strBuffer.append(x.getName() + ": " + x.get(bean) + lineSeperator);
			} catch (IllegalAccessException e) {
				logger.fine(Lib.getStackTrace(e));
			}
		return strBuffer.toString();
	}
}
