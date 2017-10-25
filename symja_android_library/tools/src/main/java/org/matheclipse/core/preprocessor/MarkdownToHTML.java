package org.matheclipse.core.preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.Documentation;

import com.github.rjeschke.txtmark.BlockEmitter;
import com.github.rjeschke.txtmark.Configuration;
import com.github.rjeschke.txtmark.Configuration.Builder;
import com.github.rjeschke.txtmark.Processor;

/**
 * Generate HTML code from markdown files.
 */
public class MarkdownToHTML {

	public MarkdownToHTML() {
	}

	/**
	 * Generate markdown links for Symja function reference.
	 * 
	 * @param sourceLocation
	 *            source directory for funtions (*.md) files
	 */
	public static void generateHTMLString(final File sourceLocation, String function, boolean javadoc) {
		if (sourceLocation.exists()) {
			Builder builder = Configuration.builder();
			BlockEmitter emitter = new BlockEmitter() {
				@Override
				public void emitBlock(StringBuilder out, List<String> lines, String meta) {
					out.append("<pre>");
					for (final String s : lines) {
						for (int i = 0; i < s.length(); i++) {
							final char c = s.charAt(i);
							switch (c) {
							case '&':
								out.append("&amp;");
								break;
							case '<':
								out.append("&lt;");
								break;
							case '>':
								out.append("&gt;");
								break;
							default:
								out.append(c);
								break;
							}
						}
						out.append('\n');
					}
					out.append("</pre>\n");
				}

			};
			Configuration config = builder.setCodeBlockEmitter(emitter).enableSafeMode().forceExtentedProfile().build();
			// Get the list of the files contained in the package
			final String[] files = sourceLocation.list();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (files[i].endsWith(".md")) {

						String className = files[i].substring(0, files[i].length() - 3);
						if (className.equals(function)) {
							File file = new File(sourceLocation + "/" + files[i]);
							String html;
							try {
								html = Processor.process(file, config);
								if (javadoc) {
									String[] lines = html.split("\\n");
									System.out.println("/**");
									for (int j = 0; j < lines.length; j++) {
										if (!lines[j].startsWith("<h2>")) {
											System.out.println(" * " + lines[j]);
										}
									}
									System.out.println(" */");
								} else {
									System.out.println(html);
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}
			}
		}

	}

	public static String readString() {
		final StringBuilder input = new StringBuilder();
		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		boolean done = false;

		try { 
			while (!done) {
				System.out.print("▶ ");  
				final String s = in.readLine();
				if (s != null) {
					if ((s.length() > 0) && (s.charAt(s.length() - 1) != '\\')) {
						input.append(s);
						done = true;
					} else {
						if (s.length() > 1) {
							input.append(s.substring(0, s.length() - 1));
						} else {
							input.append(' ');
						}
					}
				}
			}
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		return input.toString();
	}

	public static void main(final String[] args) {
		F.initSymbols();
		File sourceLocation = new File(
				"..\\symja_android_library\\doc\\functions");
		String inputExpression;
		String trimmedInput;
		while (true) {
			try {
				inputExpression = readString();
				if (inputExpression != null) {
					trimmedInput = inputExpression.trim();
					if ((trimmedInput.length() >= 4)
							&& trimmedInput.toLowerCase(Locale.ENGLISH).substring(0, 4).equals("exit")) {
						System.out.println("Closing Symja console... bye.");
						System.exit(0);
					} else if (trimmedInput.length() > 1 && trimmedInput.charAt(0) == '?') {
						Documentation.findDocumentation(System.out, trimmedInput);
						continue;
					}
					System.out.println();
					generateHTMLString(sourceLocation, trimmedInput, true);
					System.out.println();
				}
			} catch (final Exception e) {
				System.out.println(e.getMessage());
			}
		}

	}

}