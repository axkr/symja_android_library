/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.util;

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Global configuration tasks.
 * 
 * @author Tilman Neumann
 */
public class ConfigUtil {

	private static final Logger LOG = Logger.getLogger(ConfigUtil.class);
	
	private static boolean alreadyInitialized = false;
	public static boolean verbose = false;
	
	/** File separator used on this OS. */
	public static String FILE_SEPARATOR;
	/** Path separator used on this OS. */
	public static String PATH_SEPARATOR;
	/** The root directory of the current project. */
	public static String PROJECT_ROOT;
	/** The base folder for all configuration files in this project. */
	public static String CONF_ROOT;
	/** Java class path */
	public static String JAVA_CLASS_PATH;
	/** Java library path */
	public static String JAVA_LIBRARY_PATH;
	/** Java temp directory */
	public static String JAVA_TMP_DIR;
	/** user home directory */
	public static String USER_HOME;

	/** number of processors to use for parallel implementations */
	public static int NUMBER_OF_PROCESSORS;
	
	private ConfigUtil() {
		// static class
	}

	/**
	 * Project configuration.
	 */
	public static void initProject() {
		// avoid re-initialization from junit tests
		if (alreadyInitialized) return;
		
		FILE_SEPARATOR = System.getProperty("file.separator");
		if (verbose) System.out.println("system file separator = " + FILE_SEPARATOR);
		PATH_SEPARATOR = System.getProperty("path.separator");
		if (verbose) System.out.println("system path separator = " + PATH_SEPARATOR);
		PROJECT_ROOT = System.getProperty("user.dir");
		// The project root is something like D:\Projects\math_java when run from a Java project;
		// but it is different when run from an executable jar!
		// TODO: get example for executable jar
		if (verbose) System.out.println("project root directory = " + PROJECT_ROOT);
		CONF_ROOT = PROJECT_ROOT + FILE_SEPARATOR + "conf";
		if (verbose) System.out.println("conf root directory = " + CONF_ROOT);
		JAVA_CLASS_PATH = System.getProperty("java.class.path");
		if (verbose) System.out.println("java.class.path = " + JAVA_CLASS_PATH);
		JAVA_LIBRARY_PATH = System.getProperty("java.library.path");
		if (verbose) System.out.println("java.library.path = " + JAVA_LIBRARY_PATH);
		JAVA_TMP_DIR = System.getProperty("java.io.tmpdir");
		if (verbose) System.out.println("java.io.tmpdir = " + JAVA_TMP_DIR);
		USER_HOME = System.getProperty("user.home");
		if (verbose) System.out.println("conf root directory = " + USER_HOME);
		NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
		if (verbose) System.out.println("number of processors = " + NUMBER_OF_PROCESSORS);
		
		String confFileStr = CONF_ROOT + FILE_SEPARATOR + "log4jconf.xml";
		File confFile = new File(confFileStr);
		if (confFile.exists()) {
			// initialize XML-style Log4j-configuration
	    	DOMConfigurator.configure(CONF_ROOT + FILE_SEPARATOR + "log4jconf.xml");
	    	if (verbose) LOG.info("log4j configuration loaded.");
	    	if (verbose) LOG.info("project initialization finished.\n");
	    	alreadyInitialized = true;
//		} else {
//			// emergency initialization that logs into console
//			PatternLayout layout = new PatternLayout();
//			ConsoleAppender appender = new ConsoleAppender(layout);
//			appender.setThreshold(Level.DEBUG); // TODO: Set to ERROR before creating jar file, to DEBUG before creating src distribution
//			BasicConfigurator.configure(appender);
	    	alreadyInitialized = true;
		}
	}
}
