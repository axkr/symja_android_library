package org.matheclipse.core.preprocessor;

import java.io.File;

import org.matheclipse.core.expression.F;

/**
 * Generate markdown links for Symja function reference.
 */
public class MarkdownPreprocessor {

	public MarkdownPreprocessor() {
	}

	/**
	 * Generate markdown links for Symja function reference.
	 * 
	 * @param sourceLocation
	 *            source directory for funtions (*.md) files
	 */
	public static void generateFunctionStrings(final File sourceLocation) {
		if (sourceLocation.exists()) {
			// Get the list of the files contained in the package
			final String[] files = sourceLocation.list();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					// we are only interested in .m files
					if (files[i].endsWith(".md")) {
						String className = files[i].substring(0, files[i].length() - 3);
						// [Integrate](functions/Integrate.md)
						System.out.print("* [");
						System.out.print(className);
						System.out.print("](functions/");
						System.out.print(className);
						System.out.println(".md)");
					}
				}
			}
		}

	}

	public static void main(final String[] args) {
		F.initSymbols();
		File sourceLocation = new File(
				//C:\\Users\\dev\\git\\symja_android_library\\
				"../symja_android_library/doc/functions");

		generateFunctionStrings(sourceLocation);
	}

}