package org.matheclipse.core.eval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.Names;
import org.matheclipse.parser.client.SyntaxError;

/**
 * 
 */
public class MMAConsole {

	private ExprEvaluator fEvaluator;

	private File fFile;

	private String fDefaultSystemRulesFilename;

	private static int COUNTER = 1;

	public static void main(final String args[]) {
		// distinguish between lower- and uppercase identifiers
		Config.PARSER_USE_LOWERCASE_SYMBOLS = false;
		F.initSymbols(null, null, true); // console.getDefaultSystemRulesFilename(), null, false);
		printUsage();
		MMAConsole console;
		try {
			console = new MMAConsole();
		} catch (final SyntaxError e1) {
			e1.printStackTrace();
			return;
		}
		String inputExpression = null;
		String trimmedInput = null;
		String outputExpression = null;
		console.setArgs(args);
		final File file = console.getFile();
		if (file != null) {
			try {
				final BufferedReader f = new BufferedReader(new FileReader(file));
				final StringBuffer buff = new StringBuffer(1024);
				String line;
				while ((line = f.readLine()) != null) {
					buff.append(line);
					buff.append('\n');
				}
				f.close();
				inputExpression = buff.toString();
				outputExpression = console.interpreter(inputExpression);
				System.out.println("In [" + COUNTER + "]: " + inputExpression);
				if (outputExpression.length() > 0) {
					System.out.println("Out[" + COUNTER + "]: " + outputExpression);
				}
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
				inputExpression = console.readString(System.out, ">>> ");
				if (inputExpression != null) {
					trimmedInput = inputExpression.trim();
					if ((inputExpression.length() >= 4)
							&& inputExpression.toLowerCase(Locale.ENGLISH).substring(0, 4).equals("exit")) {
						System.out.println("Closing Symja console... bye.");
						System.exit(0);
					} else if (trimmedInput.charAt(0) == '?') {
						IAST list = Names.getNamesByPrefix(trimmedInput.substring(1));
						for (int i = 1; i < list.size(); i++) {
							System.out.print(list.get(i).toString());
							if (i != list.size()-1){
								System.out.print(", ");
							}
						}
						System.out.println();
						continue;
					}
					outputExpression = console.interpreter(inputExpression);
					System.out.println("In [" + COUNTER + "]: " + inputExpression);
					if (outputExpression.length() > 0) {
						System.out.println("Out[" + COUNTER + "]: " + outputExpression);
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

	/**
	 * Prints the usage of how to use this class to System.out
	 */
	private static void printUsage() {
		final String lineSeparator = System.getProperty("line.separator");
		final StringBuffer msg = new StringBuffer();
		msg.append("org.matheclipse.Console [options]" + lineSeparator);
		msg.append(lineSeparator);
		msg.append("Program arguments: " + lineSeparator);
		msg.append("  -h or -help                print this message" + lineSeparator);
		// msg.append(" -debug print debugging information" + lSep);
		msg.append("  -f or -file <filename>     use given file as input script" + lineSeparator);
		msg.append("  -d or -default <filename>  use given textfile for system rules" + lineSeparator);
		msg.append("To stop the program type: exit<RETURN>" + lineSeparator);
		msg.append("To continue an input line type: \\<RETURN>" + lineSeparator);
		msg.append("at the end of the line." + lineSeparator);
		msg.append("****+****+****+****+****+****+****+****+****+****+****+****+");

		System.out.println(msg.toString());
	}

	/**
	 * Create a console which appends each evaluation output in a history list.
	 */
	public MMAConsole() {
		EvalEngine engine = new EvalEngine(false);
		fEvaluator = new ExprEvaluator(engine, false, 100);
	}

	/**
	 * Sets the arguments for the <code>main</code> method
	 * 
	 * @param args
	 *            the aruments of the program
	 */
	private void setArgs(final String args[]) {
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];

			if (arg.equals("-help") || arg.equals("-h")) {
				printUsage();
				return;
				// } else if (arg.equals("-debug")) {
				// Config.DEBUG = true;
			} else if (arg.equals("-file") || arg.equals("-f")) {
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
			} else if (arg.charAt(0) == '-') {
				// we don't have any more args to recognize!
				final String msg = "Unknown arg: " + arg;
				System.out.println(msg);
				printUsage();
				return;
			}

		}

	}

	/**
	 * Evaluates the given string-expression and returns the result in <code>OutputForm</code>
	 * 
	 * @param inputExpression
	 * 
	 */
	public String interpreter(final String inputExpression) {
		IExpr result;
		final StringWriter buf = new StringWriter();
		try {
			result = (IExpr) fEvaluator.evaluate(inputExpression);
			if (result.equals(F.Null)) {
				return "";
			}
			return result.toString();
		} catch (final RuntimeException re) {
			printException(buf, re);
		} catch (final Exception e) {
			printException(buf, e);
		}
		return buf.toString();
	}

	private void printException(final Writer buf, final Throwable e) {
		String msg = e.getMessage();
		try {
			if (msg != null) {
				buf.write("\nError: " + msg);

			} else {
				buf.write("\nError: " + e.getClass().getSimpleName());
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
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
		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
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
	 * Get the default rules textfile name, which should be loaded at startup. This file replaces the default built-in System.mep
	 * resource stream.
	 * 
	 * @return default rules textfile name
	 */
	public String getDefaultSystemRulesFilename() {
		return fDefaultSystemRulesFilename;
	}
}