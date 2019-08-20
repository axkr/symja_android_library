package org.matheclipse.core.form;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.interfaces.IAST;

public class Documentation {

	public static void findDocumentation(Appendable out, String trimmedInput) {
		String name = trimmedInput.substring(1);
		usageDocumentation(out, name);
	}

	public static void usageDocumentation(Appendable out, String name) {
		IAST list = IOFunctions.getNamesByPrefix(name);
		try {
			if (list.size() != 2) { 
				for (int i = 1; i < list.size(); i++) {
					out.append(list.get(i).toString());
					if (i != list.argSize()) {
						out.append(", ");
					}
				}
			}
			out.append("\n");
			if (list.size() == 2) {
				Documentation.printDocumentation(out, list.arg1().toString());
			} else if (list.size() == 1
					&& (name.equals("D") || name.equals("E") || name.equals("I") || name.equals("N"))) {
				Documentation.printDocumentation(out, name);
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Load the documentation from ressource folder if available and print to output.
	 * 
	 * @param symbolName
	 */
	public static boolean printDocumentation(Appendable out, String symbolName) {
		// read markdown file
		String fileName = symbolName + ".md";

		// Get file from resources folder
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();

		try {
			InputStream is = classloader.getResourceAsStream(fileName);
			if (is != null) {
				final BufferedReader f = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String line;
				boolean emptyLine = false;
				while ((line = f.readLine()) != null) {
					if (line.startsWith("```")) {
						continue;
					}
					if (line.trim().length() == 0) {
						if (emptyLine) {
							continue;
						}
						emptyLine = true;
					} else {
						emptyLine = false;
					}
					out.append(line);
					out.append("\n");
				}
				f.close();
				is.close();
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean extraxtDocumentation(Appendable out, String symbolName) {
		// read markdown file
		String fileName = symbolName + ".md";

		// Get file from resources folder
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();

		try {
			InputStream is = classloader.getResourceAsStream(fileName);
			if (is != null) {
				final BufferedReader f = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				String line;
				String shortdesc = null;
				while ((line = f.readLine()) != null) {
					if (line.startsWith("```")) {
						if (shortdesc == null && (line = f.readLine()) != null) {
							shortdesc = line.trim();
						}
						continue;
					}
					if (line.startsWith("### ")) {
						return false;
					}

					if (line.startsWith("> ")) {
						out.append(" ");
						if (shortdesc != null) {
							out.append(shortdesc);
							out.append(" - ");
						}
						out.append(line.substring(2));
						return true;
					}
				}
				f.close();
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
