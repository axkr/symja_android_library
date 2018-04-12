package org.matheclipse.core.preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;

/**
 * Convert Symja expressions line comments opened by <code>// [$ ... $]</code> and closed by <code>// $$ </code> into
 * Java source code.
 *
 * Example from <code>TrigToExp</code>
 * 
 * <pre>
 * MATCHER.caseOf(Csch(x_), //
 * 		x -> // [$ 2/(E^x-E^(-x)) $]
 * 		F.Times(F.C2, F.Power(F.Plus(F.Negate(F.Power(F.E, F.Negate(x))), F.Power(F.E, x)), -1))); // $$);
 * </pre>
 */
public class ExprPreprocessor {

	private final static String TEST = "DFKSKFJDSFDJSALKDFJ\n"//
			+ "DJLSKFALDJLKSAJDFLKJSDF\n" //
			+ " //[$ 2+2 $] fff \n" //
			+ "sdfkas13421\n" //
			+ "asdfls43642356lfdk\n" //
			+ " //$$\n" //
			+ "dfjsadkfjsakdf\n" //
			+ "dlfkjdsalkdfjslkfdj\n" //
			+ "DJLSKFALDJLKSAJDFLKJSDF\n" //
			+ " //[$ ax^by $] fff \n" //
			+ "sdfkas546slkf\n" //
			+ "asdfls879798lfdk\n" //
			+ " sss //$$\n" //
			+ "sdlfkjsalkdfjaslkdf\n"//
			+ "dfsadkfjslfk\n";

	private final static String START_BLOCK = "//[$";
	private final static String START_BLOCK2 = "// [$";
	private final static String END_COMMAND = "$]";
	private final static String END_BLOCK = "//$$";
	private final static String END_BLOCK2 = "// $$";

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

	public static void main(String[] args) {
		Config.EXPLICIT_TIMES_OPERATOR = true;
		F.initSymbols();

		// File sourceLocation = new File("..\\symja_android_library\\rules");

		String inputExpression;
		String trimmedInput;
		System.out.println("Input qualified Java file for converting Symja expressions to Java source");
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
							String str = convertSource(inputString);
							if (str != null) {
								System.out.println(str);
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

	private static String convertSource(String str) {
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

	private static int generateCommand(String str, StringBuilder buf, int position, int startCommand, int length) {
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

	private static int insertJavaCode(String command, StringBuilder buf, String postFix, int endBlock) {
		int position;
		position = endBlock;
		ExprParser p = new ExprParser(EvalEngine.get(), true);
		IExpr expr = p.parse(command);
		buf.append(expr.internalJavaString(false, 1, false, true, true));
		buf.append(postFix);
		buf.append(" ");
		return position;
	}
}
