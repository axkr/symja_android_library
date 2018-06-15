package org.matheclipse.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * <p>
 * Convert expressions in line comments opened by <code>// [$ ... $]</code> and closed by <code>// $$</code> into Java
 * source code. The closing tag can be followed by postfix characters which are appended after the genreated source
 * code.
 * </p>
 * <p>
 * See github project <a href="https://github.com/axkr/java_codegen">java_codegen</a>
 * </p>
 */
public abstract class AbstractCodeGenerator {

	private final static String START_BLOCK = "//[$";
	private final static String START_BLOCK2 = "// [$";
	private final static String END_COMMAND = "$]";
	private final static String END_BLOCK = "//$$";
	private final static String END_BLOCK2 = "// $$";

	private static String readString() {
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

	public static void runConsole(AbstractCodeGenerator epp) {
		// File sourceLocation = new File("..\\symja_android_library\\rules");
		String inputExpression;
		String trimmedInput;
		while (true) {
			try {
				inputExpression = readString();
				if (inputExpression != null) {
					trimmedInput = inputExpression.trim();
					if ((trimmedInput.length() >= 4)
							&& trimmedInput.toLowerCase(Locale.ENGLISH).substring(0, 4).equals("exit")) {
						System.out.println("Closing ExprPreprocessor console... bye.");
						System.exit(0);
					}
					System.out.println();

					File sourceFile = new File(".." + trimmedInput);
					if (sourceFile.toString().endsWith(".java")) {
						try {
							final BufferedReader f = new BufferedReader(new FileReader(sourceFile));
							final StringBuilder buff = new StringBuilder(1024);
							String line;
							while ((line = f.readLine()) != null) {
								buff.append(line);
								buff.append('\n');
							}
							f.close();
							String inputString = buff.toString();
							String str = epp.convertSource(inputString);
							if (str != null) {
								System.out.println(str);
								try {
									BufferedWriter out = new BufferedWriter(new FileWriter(sourceFile));
									out.write(str);
									out.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					System.out.println();
				}
			} catch (final Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public abstract boolean apply(String command, StringBuilder buf);

	private String convertSource(String str) {
		StringBuilder buf = new StringBuilder(str.length() + 100);
		int position = 0;
		while (true) {
			int startCommand = str.indexOf(START_BLOCK, position);
			if (startCommand >= 0) {
				position = generateCommand(str, buf, position, startCommand, START_BLOCK.length());
				if (position < 0) {
					return null;
				}
			} else {
				startCommand = str.indexOf(START_BLOCK2, position);
				if (startCommand >= 0) {
					position = generateCommand(str, buf, position, startCommand, START_BLOCK2.length());
					if (position < 0) {
						return null;
					}
				} else {
					if (position < str.length()) {
						buf.append(str.substring(position, str.length()));
					}
					return buf.toString();
				}
			}
		}
	}

	private int generateCommand(String str, StringBuilder buf, int position, int startCommand, int length) {
		int endCommand = str.indexOf(END_COMMAND, startCommand);
		if (endCommand >= 0) {
			String command = str.substring(startCommand + length, endCommand);
			// System.out.println(command);
			int endBlock = str.indexOf(END_BLOCK, endCommand);
			if (endBlock >= 0) {
				String postFix = "";
				int newLineCommand = str.indexOf("\n", endCommand);
				if (newLineCommand >= 0 && newLineCommand < endBlock) {
					buf.append(str.substring(position, newLineCommand + 1));
				} else {
					buf.append(str.substring(position, endCommand + END_COMMAND.length()));
				}

				int newLineEndBlock = str.indexOf("\n", endBlock);
				if (newLineEndBlock >= 0) {
					postFix = str.substring(endBlock + END_BLOCK.length(), newLineEndBlock);
				}

				position = insertJavaCode(command, buf, postFix, endBlock);
			} else {
				endBlock = str.indexOf(END_BLOCK2, endCommand);
				if (endBlock >= 0) {
					String postFix = "";
					int newLineCommand = str.indexOf("\n", endCommand);
					if (newLineCommand >= 0 && newLineCommand < endBlock) {
						buf.append(str.substring(position, newLineCommand + 1));
					} else {
						buf.append(str.substring(position, endCommand + END_COMMAND.length()));
					}

					int newLineEndBlock = str.indexOf("\n", endBlock);
					if (newLineEndBlock >= 0) {
						postFix = str.substring(endBlock + END_BLOCK2.length(), newLineEndBlock);
					}

					position = insertJavaCode(command, buf, postFix, endBlock);
				} else {
					System.out.println("Error: no end clock of //$$ comment found after position " + endCommand);
					return -1;
				}
			}
		} else {
			System.out.println("Error: no end of //[$ comment found after position " + startCommand);
			return -1;
		}
		return position;
	}

	private int insertJavaCode(String command, StringBuilder buf, String postFix, int endBlock) {
		int position;
		command = command.replaceAll("\\s// ", "   ");
		position = endBlock;
		apply(command, buf);
		buf.append(postFix);
		buf.append(" ");
		return position;
	}

}
