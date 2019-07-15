package org.matheclipse.core.eval;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.ASCIIPrettyPrinter3;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.graphics.Show2SVG;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

/**
 * A read-eval-print loop console for Symja syntax math expressions.
 * 
 * See {@link MMAConsole}
 */
public class Console {

	protected final static String MATHCELL_PAGE = //
			"<html>\n" + //
					"<head>\n" + //
					"<meta charset=\"utf-8\">\n" + //
					"<title>MathCell</title>\n" + //
					"<link rel=\"stylesheet\" type=\"text/css\" href=\"style.css\">\n" + //
					"<style></style>\n" + //
					"</head>\n" + //
					"\n" + //
					"<body>\n" + //
					"\n" + //
					"<script src=\"https://cdn.jsdelivr.net/gh/paulmasson/mathcell@1.7.0/build/mathcell.js\"></script>\n"
					+ //
					"<script src=\"https://cdn.jsdelivr.net/gh/mathjax/MathJax@2.7.5/MathJax.js?config=TeX-AMS_HTML\"></script>"
					+ //
					"\n" + //
					"<p style=\"text-align: center; line-height: 2\"><span style=\"font-size: 20pt\">MathCell</span></p>\n"
					+ //
					"\n" + //
					"<div class=\"mathcell\" style=\"height: 4in\">\n" + //
					"<script>\n" + //
					"\n" + //
					"var parent = document.scripts[ document.scripts.length - 1 ].parentNode;\n" + //
					"\n" + //
					"var id = generateId();\n" + //
					"parent.id = id;\n" + //
					"\n" + //
					"`1`\n" + //
					"\n" + //
					"parent.update( id );\n" + //
					"\n" + //
					"</script>\n" + //
					"</div>\n" + //
					"\n" + //
					"</body>\n" + //
					"</html>";//

	/**
	 * 60 seconds timeout limit as the default value for Symja expression evaluation.
	 */
	private long fSeconds = 60;

	private final static int OUTPUTFORM = 0;

	private final static int JAVAFORM = 1;

	private final static int TRADITIONALFORM = 2;

	private final static int PRETTYFORM = 3;

	private final static int INPUTFORM = 4;

	private int fUsedForm = OUTPUTFORM;

	private ExprEvaluator fEvaluator;

	private OutputFormFactory fOutputFactory;

	private OutputFormFactory fOutputTraditionalFactory;

	private OutputFormFactory fInputFactory;
	/**
	 * Use pretty printer for expression output n print stream
	 */
	// private boolean fPrettyPrinter;

	// private File fFile;

	private String fDefaultSystemRulesFilename;

	private static int COUNTER = 1;

	/**
	 * See: <a href="https://stackoverflow.com/a/20387039/24819">Printing out unicode from Java code issue in windows
	 * console</a>
	 */
	private static PrintWriter stdout = new PrintWriter(new OutputStreamWriter(System.out, StandardCharsets.UTF_8),
			true);
	/**
	 * See: <a href="https://stackoverflow.com/a/20387039/24819">Printing out unicode from Java code issue in windows
	 * console</a>
	 */
	private static PrintWriter stderr = new PrintWriter(new OutputStreamWriter(System.err, StandardCharsets.UTF_8),
			true);

	public static void runConsole(final String args[], PrintWriter out, PrintWriter err) {
		stdout = out;
		stderr = err;
		main(args);
	}

	public static void main(final String args[]) {
		F.initSymbols(null, null, true);
		Console console;
		try {
			console = new Console();
		} catch (final SyntaxError e1) {
			e1.printStackTrace();
			return;
		}
		String inputExpression = null;
		String trimmedInput = null;
		try {
			console.setArgs(args);
		} catch (ReturnException re) {
			return;
		}

		// final File file = console.getFile();
		// if (file != null) {
		// try {
		// final BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		// final StringBuilder buff = new StringBuilder(1024);
		// String line;
		// while ((line = f.readLine()) != null) {
		// buff.append(line);
		// buff.append('\n');
		// }
		// f.close();
		// inputExpression = buff.toString();
		// stdout.println("In [" + COUNTER + "]: " + inputExpression);
		// console.resultPrinter(inputExpression);
		// COUNTER++;
		// } catch (final IOException ioe) {
		// final String msg = "Cannot read from the specified file. "
		// + "Make sure the path exists and you have read permission.";
		// stdout.println(msg);
		// return;
		// }
		// }

		while (true) {
			try {
				inputExpression = console.readString(stdout, ">> ");
				if (inputExpression != null) {
					trimmedInput = inputExpression.trim();
					if (trimmedInput.length() >= 4 && trimmedInput.charAt(0) == '/') {
						String command = trimmedInput.substring(1).toLowerCase(Locale.ENGLISH);
						if (command.equals("exit")) {
							stdout.println("Closing Symja console... bye.");
							System.exit(0);
						} else if (command.equals("java")) {
							stdout.println("Enabling output for JavaForm");
							console.fUsedForm = JAVAFORM;
							continue;
						} else if (command.equals("traditional")) {
							stdout.println("Enabling output for TraditionalForm");
							console.fUsedForm = TRADITIONALFORM;
							continue;
						} else if (command.equals("output")) {
							stdout.println("Enabling output for OutputForm");
							console.fUsedForm = OUTPUTFORM;
							continue;
						} else if (command.equals("pretty")) {
							stdout.println("Enabling output for PrettyPrinterForm");
							console.fUsedForm = PRETTYFORM;
							continue;
						} else if (command.equals("input")) {
							stdout.println("Enabling output for InputForm");
							console.fUsedForm = INPUTFORM;
							continue;
						} else if (command.equals("timeoutoff")) {
							stdout.println("Disabling timeout for evaluation");
							console.fSeconds = -1;
							continue;
						} else if (command.equals("timeouton")) {
							stdout.println("Enabling timeout for evaluation to 60 seconds.");
							console.fSeconds = 60;
							continue;
						}
					}
					// if ((trimmedInput.length() >= 4)
					// && trimmedInput.toLowerCase(Locale.ENGLISH).substring(0, 4).equals("exit")) {
					// stdout.println("Closing Symja console... bye.");
					// System.exit(0);
					// } else if ((trimmedInput.length() >= 7)
					// && trimmedInput.toLowerCase(Locale.ENGLISH).substring(0, 7).equals("javaoff")) {
					// stdout.println("Disabling output for JavaForm");
					// console.fUseJavaForm = false;
					// continue;
					// } else if ((trimmedInput.length() >= 6)
					// && trimmedInput.toLowerCase(Locale.ENGLISH).substring(0, 6).equals("javaon")) {
					// stdout.println("Enabling output for JavaForm");
					// console.fUseJavaForm = true;
					// continue;
					// } else if ((trimmedInput.length() >= 10)
					// && trimmedInput.toLowerCase(Locale.ENGLISH).substring(0, 10).equals("timeoutoff")) {
					// stdout.println("Disabling timeout for evaluation");
					// console.fSeconds = -1;
					// continue;
					// } else if ((trimmedInput.length() >= 9)
					// && trimmedInput.toLowerCase(Locale.ENGLISH).substring(0, 9).equals("timeouton")) {
					// stdout.println("Enabling timeout for evaluation to 60 seconds.");
					// console.fSeconds = 60;
					// continue;
					// } else
					// if (trimmedInput.length() > 1 && trimmedInput.charAt(0) == '?') {
					// Documentation.findDocumentation(stdout, trimmedInput);
					// continue;
					// }
					String postfix = Scanner.balanceCode(inputExpression);
					if (postfix != null && postfix.length() > 0) {
						stderr.println("Automatically closing brackets: " + postfix);
						inputExpression = inputExpression + postfix;
					}
					stdout.println("In [" + COUNTER + "]: " + inputExpression);
					stdout.flush();
					// if (console.fPrettyPrinter) {
					// console.prettyPrinter(inputExpression);
					// } else {
					console.resultPrinter(inputExpression);
					// }
					COUNTER++;
				}
				// } catch (final MathRuntimeException mre) {
				// Throwable me = mre.getCause();
				// stdout.println(me.getMessage());
			} catch (final Exception e) {
				stderr.println(e.getMessage());
				stderr.flush();
			}
		}
	}

	private String resultPrinter(String inputExpression) {
		String outputExpression = interpreter(inputExpression);
		if (outputExpression.length() > 0) {
			stdout.println("Out[" + COUNTER + "]: " + outputExpression);
			stdout.flush();
		}
		return outputExpression;
	}

	// private void prettyPrinter(String inputExpression) {
	// stdout.println();
	// String[] outputExpression = prettyPrinter3Lines(inputExpression);
	// ASCIIPrettyPrinter3.prettyPrinter(stdout, outputExpression, "Out[" + COUNTER + "]: ");
	// }

	/**
	 * Prints the usage of how to use this class to stdout
	 */
	private static void printUsage() {
		final String lineSeparator = System.getProperty("line.separator");
		final StringBuilder msg = new StringBuilder();
		msg.append(Config.SYMJA);
		msg.append("org.matheclipse.core.eval.Console [options]" + lineSeparator);
		msg.append(lineSeparator);
		msg.append("Program arguments: " + lineSeparator);
		msg.append("  -h or -help                                 print usage messages" + lineSeparator);
		msg.append("  -c or -code <command>                       run the command" + lineSeparator);
		msg.append("  -f or -function <function> -args arg1 arg2  run the function" + lineSeparator);
		// msg.append(" -file <filename> use given file as input script" + lineSeparator);
		msg.append("  -d or -default <filename>                   use given textfile for an initial package script"
				+ lineSeparator);
		// msg.append(" -pp enable pretty printer" + lineSeparator);
		msg.append("To stop the program type: /exit<RETURN>" + lineSeparator);
		msg.append("To continue an input line type: \\<RETURN>" + lineSeparator);
		msg.append("at the end of the line." + lineSeparator);
		msg.append("To disable the evaluation timeout type: /timeoutoff<RETURN>" + lineSeparator);
		msg.append("To enable the evaluation timeout type: /timeouton<RETURN>" + lineSeparator);
		msg.append("To enable the output in Java form: /java<RETURN>" + lineSeparator);
		msg.append("To enable the output in standard form: /output<RETURN>" + lineSeparator);
		msg.append("To enable the output in standard form: /traditional<RETURN>" + lineSeparator);
		msg.append("****+****+****+****+****+****+****+****+****+****+****+****+");

		stdout.println(msg.toString());
		stdout.flush();
	}

	/**
	 * Prints the usage of how to use this class to stdout
	 */
	// private static void printUsageCompletely() {
	// final String lineSeparator = System.getProperty("line.separator");
	// final StringBuilder msg = new StringBuilder();
	// msg.append("org.matheclipse.core.eval.Console [options]" + lineSeparator);
	// msg.append(lineSeparator);
	// msg.append("Program arguments: " + lineSeparator);
	// msg.append(" -h or -help print usage messages" + lineSeparator);
	// msg.append(" -c or -code <command> run the command" + lineSeparator);
	// msg.append(" -f or -function <function> -args arg1 arg2 run the function" + lineSeparator);
	// // msg.append(" -file <filename> use given file as input script" + lineSeparator);
	// msg.append(" -d or -default <filename> use given textfile for an initial package script"
	// + lineSeparator);
	// msg.append(" -pp enable pretty printer" + lineSeparator);
	//
	// msg.append("To stop the program type: exit<RETURN>" + lineSeparator);
	// msg.append("To continue an input line type: \\<RETURN>" + lineSeparator);
	// msg.append("at the end of the line." + lineSeparator);
	// msg.append("To disable the evaluation timeout type: timeoutoff<RETURN>" + lineSeparator);
	// msg.append("To enable the evaluation timeout type: timeouton<RETURN>" + lineSeparator);
	// msg.append("To enable the output in Java form: javaon<RETURN>" + lineSeparator);
	// msg.append("To disable the output in Java form: javaoff<RETURN>" + lineSeparator);
	// msg.append("****+****+****+****+****+****+****+****+****+****+****+****+");
	//
	// stdout.println(msg.toString());
	// }

	/**
	 * Create a console which appends each evaluation output in a history list.
	 */
	public Console() {
		// activate MathCell JavaScript output for Plot, Plot3D
		Config.USE_MATHCELL = true;
		fEvaluator = new ExprEvaluator(false, 100);
		fOutputFactory = OutputFormFactory.get(true, false, 5, 7);
		fEvaluator.getEvalEngine().setFileSystemEnabled(true);
		fOutputTraditionalFactory = OutputFormFactory.get(true, false, 5, 7);
		fInputFactory = OutputFormFactory.get(true, false, 5, 7);
		fInputFactory.setQuotes(true);
		// F.$PreRead.assign(//
		// F.Function(F.ReplaceAll(F.Unevaluated(F.Slot1), //
		// F.List(F.RuleDelayed(F.binaryAST2(F.Plot, F.x_, F.y_), F.Manipulate(F.binaryAST2(F.Plot, F.x, F.y))), //
		// F.RuleDelayed(F.ternaryAST3(F.Plot3D, F.x_, F.y_, F.z_), F.Manipulate(F.ternaryAST3(F.Plot3D, F.x, F.y,
		// F.z))//
		// )))));
	}

	/**
	 * Sets the arguments for the <code>main</code> method
	 * 
	 * @param args
	 *            the arguments of the program
	 */
	private void setArgs(final String args[]) {
		String function = null;
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];

			if (arg.equals("-code") || arg.equals("-c")) {
				try {
					String outputExpression = interpreter(args[i + 1]);
					if (outputExpression.length() > 0) {
						stdout.print(outputExpression);
					}
					throw ReturnException.RETURN_TRUE;
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a command when " + "using the -code argument";
					stdout.println(msg);
					throw ReturnException.RETURN_FALSE;
				}
			} else if (arg.equals("-function") || arg.equals("-f")) {
				try {
					function = args[i + 1];
					i++;
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a function when " + "using the -function argument";
					stdout.println(msg);
					throw ReturnException.RETURN_FALSE;
				}
			} else if (arg.equals("-args") || arg.equals("-a")) {
				try {
					if (function != null) {
						StringBuilder inputExpression = new StringBuilder(1024);
						inputExpression.append(function);
						inputExpression.append('(');
						for (int j = i + 1; j < args.length; j++) {
							if (j != i + 1) {
								inputExpression.append(", ");
							}
							inputExpression.append(args[j]);
						}
						inputExpression.append(')');
						String outputExpression = interpreter(inputExpression.toString());
						if (outputExpression.length() > 0) {
							stdout.print(outputExpression);
						}
						throw ReturnException.RETURN_TRUE;
					}
					return;
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a function when " + "using the -function argument";
					stdout.println(msg);
					throw ReturnException.RETURN_FALSE;
				}
			} else if (arg.equals("-help") || arg.equals("-h")) {
				printUsage();
				return;
				// } else if (arg.equals("-debug")) {
				// Config.DEBUG = true;
			} else if (arg.equals("-file")) {

				try {
					// fFile = new File(args[i + 1]);
					fEvaluator.eval(F.Get(args[i + 1]));

					// if (fFile.isFile()) {
					//
					// }
					i++;
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a file when " + "using the -file argument";
					stdout.println(msg);
					return;
				}
			} else if (arg.equals("-default") || arg.equals("-d")) {
				try {
					fDefaultSystemRulesFilename = args[i + 1];
					fEvaluator.eval(F.Get(args[i + 1]));
					i++;
				} catch (final ArrayIndexOutOfBoundsException aioobe) {
					final String msg = "You must specify a file when " + "using the -d argument";
					stdout.println(msg);
					return;
				}
				// } else if (arg.equals("-pp")) {
				// fPrettyPrinter = true;
			} else if (arg.charAt(0) == '-') {
				// we don't have any more args to recognize!
				final String msg = "Unknown arg: " + arg;
				stdout.println(msg);
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
				result = fEvaluator.eval(inputExpression);
			} else {
				result = fEvaluator.evaluateWithTimeout(inputExpression, fSeconds, TimeUnit.SECONDS, true,
						new EvalControlledCallable(fEvaluator.getEvalEngine()));
			}
			if (result != null) {
				return printResult(result);
			}
		} catch (final AbortException re) {
			try {
				return printResult(F.$Aborted);
			} catch (IOException e) {
				Validate.printException(buf, e);
				stderr.println(buf.toString());
				stderr.flush();
				return "";
			}
		} catch (final SyntaxError se) {
			String msg = se.getMessage();
			stderr.println(msg);
			stderr.println();
			stderr.flush();
			return "";
		} catch (final RuntimeException re) {
			Throwable me = re.getCause();
			if (me instanceof MathException) {
				Validate.printException(buf, me);
			} else {
				Validate.printException(buf, re);
			}
			stderr.println(buf.toString());
			stderr.flush();
			return "";
		} catch (final Exception e) {
			Validate.printException(buf, e);
			stderr.println(buf.toString());
			stderr.flush();
			return "";
		} catch (final OutOfMemoryError e) {
			Validate.printException(buf, e);
			stderr.println(buf.toString());
			stderr.flush();
			return "";
		} catch (final StackOverflowError e) {
			Validate.printException(buf, e);
			stderr.println(buf.toString());
			stderr.flush();
			return "";
		}
		return buf.toString();
	}

	private String printResult(IExpr result) throws IOException {
		if (result.equals(F.Null)) {
			return "";
		}
		switch (fUsedForm) {
		case JAVAFORM:
			return result.internalJavaString(false, -1, false, true, false);
		case TRADITIONALFORM:
			StringBuilder traditionalBuffer = new StringBuilder();
			fOutputTraditionalFactory.reset();
			fOutputTraditionalFactory.convert(traditionalBuffer, result);
			return traditionalBuffer.toString();
		case PRETTYFORM:
			ASCIIPrettyPrinter3 prettyBuffer = new ASCIIPrettyPrinter3();
			prettyBuffer.convert(result);
			stdout.println();
			String[] outputExpression = prettyBuffer.toStringBuilder();
			ASCIIPrettyPrinter3.prettyPrinter(stdout, outputExpression, "Out[" + COUNTER + "]: ");
			return "";
		case INPUTFORM:
			StringBuilder inputBuffer = new StringBuilder();
			fInputFactory.reset();
			fInputFactory.convert(inputBuffer, result);
			return inputBuffer.toString();
		default:
			if (Desktop.isDesktopSupported()) {
				IExpr outExpr = result;
				if (result.isAST(F.Graphics)) {// || result.isAST(F.Graphics3D)) {
					outExpr = F.Show(outExpr);
				}
				if (outExpr.isSameHeadSizeGE(F.Show, 2)) {
					try {
						IAST show = (IAST) outExpr;
						if (show.size() > 1 && show.arg1().isSameHeadSizeGE(F.Graphics, 2)) {
							return openSVGOnDesktop(show);
						}
					} catch (Exception ex) {
						if (Config.SHOW_STACKTRACE) {
							ex.printStackTrace();
						}
					}
				} else if (result.isAST(F.JSFormData, 3) && result.second().toString().equals("mathcell")) {
					try {
						String manipulateStr = ((IAST) result).arg1().toString();
						String html = MATHCELL_PAGE;
						html = html.replaceAll("`1`", manipulateStr);
						return Console.openHTMLOnDesktop(html);
					} catch (Exception ex) {
						if (Config.SHOW_STACKTRACE) {
							ex.printStackTrace();
						}
					}
				}
			}
			StringBuilder strBuffer = new StringBuilder();
			fOutputFactory.reset();
			fOutputFactory.convert(strBuffer, result);
			return strBuffer.toString();
		}
	}

	public static String openSVGOnDesktop(IAST show) throws IOException {
		StringBuilder stw = new StringBuilder();
		Show2SVG.graphicsToSVG(show.getAST(1), stw);
		File temp = File.createTempFile("tempfile", ".svg");
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
		bw.write(stw.toString());
		bw.close();
		Desktop.getDesktop().open(temp);
		return temp.toString();
	}

	public static String openHTMLOnDesktop(String html) throws IOException {
		File temp = File.createTempFile("tempfile", ".html");
		BufferedWriter bw = new BufferedWriter(new FileWriter(temp));
		bw.write(html);
		bw.close();
		Desktop.getDesktop().open(temp);
		return temp.toString();
	}

	// private String[] prettyPrinter3Lines(final String inputExpression) {
	// IExpr result;
	//
	// final StringWriter buf = new StringWriter();
	// try {
	// if (fSeconds <= 0) {
	// result = fEvaluator.eval(inputExpression);
	// } else {
	// result = fEvaluator.evaluateWithTimeout(inputExpression, fSeconds, TimeUnit.SECONDS, true,
	// new EvalCallable(fEvaluator.getEvalEngine()));
	// }
	// if (result != null) {
	// if (result.equals(F.Null)) {
	// return null;
	// }
	// ASCIIPrettyPrinter3 strBuffer = new ASCIIPrettyPrinter3();
	// strBuffer.convert(result);
	// return strBuffer.toStringBuilder();
	// }
	// } catch (final SyntaxError se) {
	// String msg = se.getMessage();
	// stderr.println();
	// stderr.println(msg);
	// return null;
	// } catch (final RuntimeException re) {
	// Throwable me = re.getCause();
	// if (me instanceof MathException) {
	// Validate.printException(buf, me);
	// } else {
	// Validate.printException(buf, re);
	// }
	// return null;
	// } catch (final Exception e) {
	// Validate.printException(buf, e);
	// return null;
	// } catch (final OutOfMemoryError e) {
	// Validate.printException(buf, e);
	// return null;
	// } catch (final StackOverflowError e) {
	// Validate.printException(buf, e);
	// return null;
	// }
	// String[] strArray = new String[3];
	// strArray[0] = "";
	// strArray[1] = buf.toString();
	// strArray[2] = "";
	// return strArray;
	// }

	/**
	 * prints a prompt on the console but doesn't print a newline
	 * 
	 * @param out
	 * @param prompt
	 *            the prompt string to display
	 * 
	 */

	public void printPrompt(final PrintWriter out, final String prompt) {
		out.print(prompt);
		out.flush();
	}

	/**
	 * read a string from the console. The string is terminated by a newline
	 * 
	 * @return the input string (without the newline)
	 */

	public String readString() {
		final StringBuilder input = new StringBuilder();
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

	public String readString(final PrintWriter out, final String prompt) {
		printPrompt(out, prompt);
		return readString();
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

			stdout.println(temp.toURI().toString());

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
