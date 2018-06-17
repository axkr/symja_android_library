package org.matheclipse.core.form;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.reflection.system.Names;

public class Documentation {

	public static void findDocumentation(Appendable out, String trimmedInput) {
		String name = trimmedInput.substring(1);
		usageDocumentation(out, name);
	}

	public static void usageDocumentation(Appendable out, String name) {
		IAST list = Names.getNamesByPrefix(name);
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
				Documentation.printDocumentation(out, list.get(1).toString());
			} else if (list.size() == 1
					&& (name.equals("D") || name.equals("E") || name.equals("I") || name.equals("N"))) {
				Documentation.printDocumentation(out, name);
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Load the documentation fro ressources folder if available ad print to output.
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

}
