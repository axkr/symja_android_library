/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
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
import java.net.URL;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.LayoutComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

/**
 * Global configuration tasks.
 * 
 * @author Tilman Neumann
 */
public class ConfigUtil {

	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(ConfigUtil.class);
	
	private static boolean initialized = false;
	
	/** File separator used on this OS. */
	public static String FILE_SEPARATOR;
	
	/** Path separator used on this OS. */
	public static String PATH_SEPARATOR;
	
	/**
	 * The root directory of the current project, derived from the system property 'user.dir'. This is<br>
	 * a) the jml project root directory if jml is run as a project<br>
	 * b) the project root directory of a custom project using jml as a jar<br>
	 * c) the folder containing the jar file if jml is run as a runnable jar<br>
	 */
	public static String PROJECT_ROOT;
	
	/** The folder for configuration files in this project. */
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

	/** if true, then jml is deployed as a (possibly executable) jar file; otherwise as a Java project. */
	public static boolean IS_DEPLOYED_AS_JAR;
	
	
	private ConfigUtil() {
		// static class
	}

	/**
	 * Run project configuration being quiet about the initialization.
	 * @param verbose
	 */
	public static void initProject() {
		initProject(false);
	}

	/**
	 * Run project configuration, permitting to switch on verbose initialization.
	 * @param verbose
	 */
	public static void initProject(boolean verbose) {
		// avoid re-initialization from junit tests
		if (initialized) return;
		
		FILE_SEPARATOR = System.getProperty("file.separator");
		if (verbose) System.out.println("system file separator = " + FILE_SEPARATOR);
		PATH_SEPARATOR = System.getProperty("path.separator");
		if (verbose) System.out.println("system path separator = " + PATH_SEPARATOR);
		PROJECT_ROOT = System.getProperty("user.dir");
		if (verbose) System.out.println("project root directory (user.dir) = " + PROJECT_ROOT);
		CONF_ROOT = PROJECT_ROOT + FILE_SEPARATOR + "conf";
		if (verbose) System.out.println("conf root directory = " + CONF_ROOT);
		JAVA_CLASS_PATH = System.getProperty("java.class.path");
		if (verbose) System.out.println("java.class.path = " + JAVA_CLASS_PATH);
		JAVA_LIBRARY_PATH = System.getProperty("java.library.path");
		if (verbose) System.out.println("java.library.path = " + JAVA_LIBRARY_PATH);
		JAVA_TMP_DIR = System.getProperty("java.io.tmpdir");
		if (verbose) System.out.println("java.io.tmpdir = " + JAVA_TMP_DIR);
		USER_HOME = System.getProperty("user.home");
		if (verbose) System.out.println("user.home = " + USER_HOME);
		NUMBER_OF_PROCESSORS = Runtime.getRuntime().availableProcessors();
		if (verbose) System.out.println("number of processors = " + NUMBER_OF_PROCESSORS);
		IS_DEPLOYED_AS_JAR = "jar".equals(getClassResourceProtocol(verbose));
		if (verbose) System.out.println("jml deployed as jar = " + IS_DEPLOYED_AS_JAR);
		
		setupLog4j2(verbose);
	    
    	if (verbose) System.out.println("project initialization finished.");
    	initialized = true;
	}
	
	// derived from https://stackoverflow.com/questions/482560/can-you-tell-on-runtime-if-youre-running-java-from-within-a-jar
	private static String getClassResourceProtocol(boolean verbose) {
		//URL classResource = ConfigUtil.class.getResource(ConfigUtil.class.getName() + ".class"); // may be null
		URL classResource = ConfigUtil.class.getResource("");
		if (verbose) System.out.println("classResource = " + classResource);
		String protocol = classResource.getProtocol();
		if (verbose) System.out.println("protocol = " + protocol);
		return protocol;
	}
	
	private static void setupLog4j2(boolean verbose) {
		// Set up log4j2...
		// First choice is any custom configuration file defined by system property LOG4J2_CONFIGURATION_FILE
		String log4j2ConfigFile = System.getProperty("log4j2.configurationFile");
		if (log4j2ConfigFile != null) {
			if (new File(log4j2ConfigFile).exists()) {
				// the logger should already be initialized correctly, but here we write to stdout because we don't know the log level
				if (verbose) System.out.println("Using custom log4j2 configuration file " + log4j2ConfigFile + " defined by system property 'log4j2.configurationFile'");
				return;
			} else {
				if (verbose) System.out.println("The log4j2 configuration file " + log4j2ConfigFile + " defined by system property 'log4j2.configurationFile' does not exist");
			}
		} else {
			if (verbose) System.out.println("The system property 'log4j2.configurationFile' is not defined.");
		}
		
		if (IS_DEPLOYED_AS_JAR) {
			// So far I failed to find a way to change the log level in the case that jml is run from a jar.
			// Thus the only possible fallback is the default log4j2 console Logger with loglevel ERROR.
			// This will be disappointing if it is a runnable jar and we see no input prompt nor factoring outputs...
			if (verbose) {
				System.out.println("jml will use the default log4j2 console Logger with loglevel ERROR.");
				System.out.println("To take control of the logging behavior, define the system property 'log4j2.configurationFile' such that it points to a valid log4j2 configuration file.");
				System.out.println("For a runnable jar, use something like 'java -Dlog4j2.configurationFile=<some-file-path>/log4j2-test.xml -jar jml<version>.jar'");
			}
			return; // initialized, though not perfectly
		}

		// Now we know that jml is run as a Java project. In that case we have more options...
		// 2nd choice is the default log4j2-test.xml in the conf folder of the project.
		String defaultConfigFile = CONF_ROOT + FILE_SEPARATOR + "log4j2-test.xml";
		if (new File(defaultConfigFile).exists()) {
			// initialize Log4j2 from xml configuration
			System.setProperty("log4j2.configurationFile", defaultConfigFile);
			Configurator.reconfigure();
	    	if (verbose) System.out.println("log4j configuration successfully loaded from file " + defaultConfigFile + ".");
	    	return;
		} else {
			if (verbose) {
				System.out.println("The default configuration file " + defaultConfigFile + " does not exist");
				System.out.println("An emergency logger will be used that logs to console only.");
			}
		}
		
		// 3rd choice is to setup a simple emergencey logger.
		// For this we could use the log4j2 default logger
		//configureDefaultLogger_v1();
		// or create one programmatically on our own
		configureDefaultLogger_v2(false);
	}
	
	/**
	 * This version simply changes the log level of the log4j2 default logger.
	 * The default logger only logs to console and has pattern layout "%d [%t] %-5p %c - %m%n".
	 * 
	 * Works only when run as a project.
	 */
	@SuppressWarnings("unused")
	private static void configureDefaultLogger_v1() {
		Configurator.setRootLevel(Level.INFO);
	}

	/**
	 * Create an emergency logger on our own.
	 * 
	 * Works only when run as a project.
	 * 
	 * @param addFileLogger
	 */
	private static void configureDefaultLogger_v2(boolean addFileLogger) {
		// Create default logger, following https://www.baeldung.com/log4j2-programmatic-config.
		// This works only when running from a project.
		ConfigurationBuilder<BuiltConfiguration> builder = ConfigurationBuilderFactory.newConfigurationBuilder();
		// create console appenders
		AppenderComponentBuilder console = builder.newAppender("stdout", "Console"); 
		builder.add(console);
		// add layout
		LayoutComponentBuilder consoleLayout = builder.newLayout("PatternLayout");
		consoleLayout.addAttribute("pattern", "%d %-5p %c{1}(%L) [%t]: %m%n");
		console.add(consoleLayout);

		if (addFileLogger) {
			AppenderComponentBuilder file = builder.newAppender("log", "File"); 
			file.addAttribute("fileName", "log.txt");
			builder.add(file);
			LayoutComponentBuilder fileLayout = builder.newLayout("PatternLayout");
			fileLayout.addAttribute("pattern", "%m%n");
			file.add(fileLayout);
		}
		
		// set up root logger
 		RootLoggerComponentBuilder rootLogger = builder.newRootLogger(Level.DEBUG);
		rootLogger.add(builder.newAppenderRef("stdout"));
		builder.add(rootLogger);
		// let it be used
		Configurator.reconfigure(builder.build());		
	}
	
}
