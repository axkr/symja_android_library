package org.matheclipse.core.eval;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.form.output.ASCIIPrettyPrinter3;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.Names;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * 
 */
public class Console {

	private ExprEvaluator fEvaluator;

	private OutputFormFactory fOutputFactory;

	/**
	 * Use pretty printer for expression output n print stream
	 */
	private boolean fPrettyPrinter;

	/**
	 * 60 seconds timeout limit as the default value for Symja expression evaluation.
	 */
	private long fSeconds = 60;

	private File fFile;

	private String fDefaultSystemRulesFilename;

	private static int COUNTER = 1;

	public static void main(final String args[]) {
		F.initSymbols(null, null, true); // console.getDefaultSystemRulesFilename(),
											// null, false);

		Console console;
		try {
			console = new Console();
		} catch (final SyntaxError e1) {
			e1.printStackTrace();
			return;
		}
		String inputExpression = null;
		String trimmedInput = null;
		console.setArgs(args);

		final File file = console.getFile();
		if (file != null) {
			try {
				final BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				final StringBuffer buff = new StringBuffer(1024);
				String line;
				while ((line = f.readLine()) != null) {
					buff.append(line);
					buff.append('\n');
				}
				f.close();
				inputExpression = buff.toString();
				System.out.println("In [" + COUNTER + "]: " + inputExpression);
				console.resultPrinter(inputExpression);
				COUNTER++;
			} catch (final IOException ioe) {
				final String msg = "Cannot read from the specified file. "
						+ "Make sure the path exists and you have read permission.";
				System.out.println(msg);
				return;
			}
		}

		while (true) {
			try {
				inputExpression = console.readString(System.out, ">> ");
				if (inputExpression != null) {
					trimmedInput = inputExpression.trim();
					if ((inputExpression.length() >= 4)
							&& inputExpression.toLowerCase(Locale.ENGLISH).substring(0, 4).equals("exit")) {
						System.out.println("Closing Symja console... bye.");
						System.exit(0);
					} else if ((inputExpression.length() >= 10)
							&& inputExpression.toLowerCase(Locale.ENGLISH).substring(0, 10).equals("timeoutoff")) {
						System.out.println("Disabling timeout for evaluation");
						console.fSeconds = -1;
						continue;
					} else if ((inputExpression.length() >= 9)
							&& inputExpression.toLowerCase(Locale.ENGLISH).substring(0, 9).equals("timeouton")) {
						System.out.println("Enabling timeout for evaluation to 60 seconds.");
						console.fSeconds = 60;
						continue;
					} else if (trimmedInput.length() > 1 && trimmedInput.charAt(0) == '?') {
						String name = trimmedInput.substring(1);
						IAST list = Names.getNamesByPrefix(name);
						for (int i = 1; i < list.size(); i++) {
							System.out.print(list.get(i).toString());
							if (i != list.size() - 1) {
								System.out.print(", ");
							}
						}
						System.out.println();
						if (list.size() == 2) {
							printDocumentation(list.get(1).toString());
						} else if (list.size() == 1
								&& (name.equals("D") || name.equals("E") || name.equals("I") || name.equals("N"))) {
							printDocumentation(name);
						}
						continue;
					}
					String postfix = Scanner.balanceCode(inputExpression);
					if (postfix != null && postfix.length() > 0) {
						System.err.println("Automatically closing brackets: " + postfix);
						inputExpression = inputExpression + postfix;
					}
					System.out.println("In [" + COUNTER + "]: " + inputExpression);
					if (console.fPrettyPrinter) {
						console.prettyPrinter(inputExpression);
					} else {
						console.resultPrinter(inputExpression);
					}
					COUNTER++;
				}
				// } catch (final MathRuntimeException mre) {
				// Throwable me = mre.getCause();
				// System.out.println(me.getMessage());
			} catch (final Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private String resultPrinter(String inputExpression) {
		String outputExpression = interpreter(inputExpression);
		if (outputExpression.length() > 0) {
			System.out.println("Out[" + COUNTER + "]: " + outputExpression);
		}
		return outputExpression;
	}

	private void prettyPrinter(String inputExpression) {
		System.out.println();
		String[] outputExpression = prettyPrinter3Lines(inputExpression);
		ASCIIPrettyPrinter3.prettyPrinter(System.out, outputExpression, "Out[" + COUNTER + "]: ");
	}

	/**
	 * Load the documentation fro ressources folder if available ad print to output.
	 * 
	 * @param symbolName
	 */
	private static void printDocumentation(String symbolName) {
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
					System.out.println(line);
				}
				f.close();
				is.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prints the usage of how to use this class to System.out
	 */
	private static void printUsage() {
		final String lineSeparator = System.getProperty("line.separator");
		final StringBuffer msg = new StringBuffer();
		msg.append("org.matheclipse.core.eval.Console [options]" + lineSeparator);
		msg.append(lineSeparator);
		msg.append("Program arguments: " + lineSeparator);
		msg.append("  -h or -help                print usage messages" + lineSeparator);

		msg.append("To stop the program type: exit<RETURN>" + lineSeparator);
		msg.append("To continue an input line type: \\<RETURN>" + lineSeparator);
		msg.append("at the end of the line." + lineSeparator);
		msg.append("To disable the evaluation timeout type: timeoutoff<RETURN>" + lineSeparator);
		msg.append("To enable the evaluation timeout type: timeouton<RETURN>" + lineSeparator);
		msg.append("****+****+****+****+****+****+****+****+****+****+****+****+");

		System.out.println(msg.toString());
	}

	/**
	 * Prints the usage of how to use this class to System.out
	 */
	private static void printUsageCompletely() {
		final String lineSeparator = System.getProperty("line.separator");
		final StringBuffer msg = new StringBuffer();
		msg.append("org.matheclipse.core.eval.Console [options]" + lineSeparator);
		msg.append(lineSeparator);
		msg.append("Program arguments: " + lineSeparator);
		msg.append("  -h or -help                                 print usage messages" + lineSeparator);
		msg.append("  -c or -code <command>                       run the command" + lineSeparator);
		msg.append("  -f or -function <function> -args arg1 arg2  run the function" + lineSeparator);
		msg.append("        -file <filename>                      use given file as input script" + lineSeparator);
		msg.append("  -d or -default <filename>                   use given textfile for system rules" + lineSeparator);
		msg.append("  -pp                                         enable pretty printer" + lineSeparator);

		msg.append("To stop the program type: exit<RETURN>" + lineSeparator);
		msg.append("To continue an input line type: \\<RETURN>" + lineSeparator);
		msg.append("at the end of the line." + lineSeparator);
		msg.append("To disable the evaluation timeout type: timeoutoff<RETURN>" + lineSeparator);
		msg.append("To enable the evaluation timeout type: timeouton<RETURN>" + lineSeparator);
		msg.append("****+****+****+****+****+****+****+****+****+****+****+****+");

		System.out.println(msg.toString());
	}

	/**
	 * Create a console which appends each evaluation output in a history list.
	 */
	public Console() {
		fEvaluator = new ExprEvaluator(false, 100);
		DecimalFormatSymbols usSymbols = new DecimalFormatSymbols(Locale.US);
		DecimalFormat decimalFormat = new DecimalFormat("0.0####", usSymbols);
		fOutputFactory = OutputFormFactory.get(true, false, decimalFormat);
	}

	/**
	 * Sets the arguments for the <code>main</code> method
	 * 
	 * @param args
	 *            the aruments of the program
	 */
	private void setArgs(final String args[]) {
		String function = null;
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];

			if (arg.equals("-code") || arg.equals("-c")) {
				try {
					String outputExpression = interpreter(args[i + 1]);
					if (outputExpression.length() > 0) {
						System.out.println(outputExpression);
					}
					System.exit(1);
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a command when " + "using the -code argument";
					System.out.println(msg);
					System.exit(-1);
					return;
				}
			} else if (arg.equals("-function") || arg.equals("-f")) {
				try {
					function = args[i + 1];
					i++;
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a function when " + "using the -function argument";
					System.out.println(msg);
					System.exit(-1);
					return;
				}
			} else if (arg.equals("-args") || arg.equals("-a")) {
				try {
					if (function != null) {
						StringBuilder inputExpression = new StringBuilder(1024);
						inputExpression.append(function);
						inputExpression.append("(");
						for (int j = i + 1; j < args.length; j++) {
							if (j != i + 1) {
								inputExpression.append(", ");
							}
							inputExpression.append(args[j]);
						}
						inputExpression.append(")");
						String outputExpression = interpreter(inputExpression.toString());
						if (outputExpression.length() > 0) {
							System.out.println(outputExpression);
						}
						System.exit(1);
					}
					return;
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a function when " + "using the -function argument";
					System.out.println(msg);
					System.exit(-1);
					return;
				}
			} else if (arg.equals("-help") || arg.equals("-h")) {
				printUsageCompletely();
				return;
				// } else if (arg.equals("-debug")) {
				// Config.DEBUG = true;
			} else if (arg.equals("-file")) {
				try {
					fFile = new File(args[i + 1]);
					i++;
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a file when " + "using the -file argument";
					System.out.println(msg);
					return;
				}
			} else if (arg.equals("-default") || arg.equals("-d")) {
				try {
					fDefaultSystemRulesFilename = args[i + 1];
					i++;
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a file when " + "using the -file argument";
					System.out.println(msg);
					return;
				}
			} else if (arg.equals("-pp")) {
				fPrettyPrinter = true;
			} else if (arg.charAt(0) == '-') {
				// we don't have any more args to recognize!
				final String msg = "Unknown arg: " + arg;
				System.out.println(msg);
				printUsage();
				return;
			}

		}
		printUsage();
	}

	/**
	 * Evaluates the given string-expression and returns the result in <code>OutputForm</code>
	 * 
	 * @param inputExpression
	 * @return
	 */
	public String interpreter(final String inputExpression) {
		IExpr result;
		final StringWriter buf = new StringWriter();
		try {
			if (fSeconds <= 0) {
				result = fEvaluator.evaluate(inputExpression);
			} else {
				result = fEvaluator.evaluateWithTimeout(inputExpression, fSeconds, TimeUnit.SECONDS, true);
			}
			if (result != null) {
				if (result.equals(F.Null)) {
					return "";
				}
				StringBuilder strBuffer = new StringBuilder();
				fOutputFactory.convert(strBuffer, result);
				return strBuffer.toString();
			}
		} catch (final SyntaxError se) {
			String msg = se.getMessage();
			System.err.println();
			System.err.println(msg);
			return "";
		} catch (final RuntimeException re) {
			Throwable me = re.getCause();
			if (me instanceof MathException) {
				Validate.printException(buf, me);
			} else {
				Validate.printException(buf, re);
			}
			return "";
		} catch (final Exception e) {
			Validate.printException(buf, e);
			return "";
		} catch (final OutOfMemoryError e) {
			Validate.printException(buf, e);
			return "";
		} catch (final StackOverflowError e) {
			Validate.printException(buf, e);
			return "";
		}
		return buf.toString();
	}

	private String[] prettyPrinter3Lines(final String inputExpression) {
		IExpr result;

		final StringWriter buf = new StringWriter();
		try {
			if (fSeconds <= 0) {
				result = fEvaluator.evaluate(inputExpression);
			} else {
				result = fEvaluator.evaluateWithTimeout(inputExpression, fSeconds, TimeUnit.SECONDS, true);
			}
			if (result != null) {
				if (result.equals(F.Null)) {
					return null;
				}
				ASCIIPrettyPrinter3 strBuffer = new ASCIIPrettyPrinter3();
				strBuffer.convert(result);
				return strBuffer.toStringBuilder();
			}
		} catch (final SyntaxError se) {
			String msg = se.getMessage();
			System.err.println();
			System.err.println(msg);
			return null;
		} catch (final RuntimeException re) {
			Throwable me = re.getCause();
			if (me instanceof MathException) {
				Validate.printException(buf, me);
			} else {
				Validate.printException(buf, re);
			}
			return null;
		} catch (final Exception e) {
			Validate.printException(buf, e);
			return null;
		} catch (final OutOfMemoryError e) {
			Validate.printException(buf, e);
			return null;
		} catch (final StackOverflowError e) {
			Validate.printException(buf, e);
			return null;
		}
		String[] strArray = new String[3];
		strArray[0] = "";
		strArray[1] = buf.toString();
		strArray[2] = "";
		return strArray;
	}

	/**
	 * prints a prompt on the console but doesn't print a newline
	 * 
	 * @param out
	 * @param prompt
	 *            the prompt string to display
	 * 
	 */

	public void printPrompt(final PrintStream out, final String prompt) {
		out.print(prompt);
		out.flush();
	}

	/**
	 * read a string from the console. The string is terminated by a newline
	 * 
	 * @param out
	 *            Description of Parameter
	 * @return the input string (without the newline)
	 */

	public String readString(final PrintStream out) {
		final StringBuffer input = new StringBuffer();
		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		boolean done = false;

		try {
			while (!done) {
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

	/**
	 * read a string from the console. The string is terminated by a newline
	 * 
	 * @param prompt
	 *            the prompt string to display
	 * @param out
	 *            Description of Parameter
	 * @return the input string (without the newline)
	 */

	public String readString(final PrintStream out, final String prompt) {
		printPrompt(out, prompt);
		return readString(out);
	}

	/**
	 * @param file
	 */
	public void setFile(final File file) {
		fFile = file;
	}

	/**
	 * @return the file with which the program was started or <code>null</code>
	 */
	public File getFile() {
		return fFile;
	}

	/**
	 * Get the default rules textfile name, which should be loaded at startup. This file replaces the default built-in
	 * System.mep resource stream.
	 * 
	 * @return default rules textfile name
	 */
	public String getDefaultSystemRulesFilename() {
		return fDefaultSystemRulesFilename;
	}

	/**
	 * 
	 * @param fileContent
	 * @param extension
	 *            the file extension i.e. *.svg *.html
	 */
	private static void openInBrowser(String fileContent, String extension) {
		File temp;
		try {
			temp = File.createTempFile("document", ".htm");
			BufferedWriter out = new BufferedWriter(new FileWriter(temp));
			out.write(fileContent);
			out.close();

			System.out.println(temp.toURI().toString());

			java.awt.Desktop.getDesktop().browse(temp.toURI());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// private static void openURL(String url) {
	// if (Desktop.isDesktopSupported()) {
	// Desktop desktop = Desktop.getDesktop();
	// try {
	// desktop.browse(new URI(url));
	// } catch (IOException | URISyntaxException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// } else {
	// Runtime runtime = Runtime.getRuntime();
	// try {
	// runtime.exec("xdg-open " + url);
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }
}