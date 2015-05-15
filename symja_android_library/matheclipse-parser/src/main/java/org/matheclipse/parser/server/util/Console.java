/*
 * Copyright 2005-2008 Axel Kramer (axelclk@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.matheclipse.parser.server.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;
import org.matheclipse.parser.client.ast.FunctionNode;
import org.matheclipse.parser.client.eval.ComplexEvaluator;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.math.Complex;
import org.matheclipse.parser.client.math.MathException;

/**
 * A java console program to run the parsers numerical evaluators interactivly
 */
public class Console {

	private File fFile;

	private boolean fComplexEvaluatorMode = false;

	// private Parser fParser;

	public static void main(final String args[]) {
		printUsage();
		Console console = new Console();

		String expr = null;
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
				System.out.println(console.interpreter(buff.toString()));
			} catch (final IOException ioe) {
				final String msg = "Cannot read from the specified file. " + "Make sure the path exists and you have read permission.";
				System.out.println(msg);
				return;
			}
		}

		while (true) {
			try {
				expr = console.readString(System.out, ">>> ");
				if (expr != null) {
					if ((expr.length() >= 4) && expr.toLowerCase().substring(0, 4).equals("exit")) {
						break;
					}
					if ((expr.length() >= 6) && expr.toLowerCase().substring(0, 6).equals("double")) {
						console.fComplexEvaluatorMode = false;
						System.out.println("Double evaluation mode (switch to other mode with keyword 'complex')");
						continue;
					}
					if ((expr.length() >= 7) && expr.toLowerCase().substring(0, 7).equals("complex")) {
						console.fComplexEvaluatorMode = true;
						System.out.println("Complex evaluation mode (switch to other mode with keyword 'double')");
						continue;
					}
					System.out.println(console.interpreter(expr));
				}
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
		msg.append(Console.class.getCanonicalName() + " [options]" + lineSeparator);
		msg.append(lineSeparator);
		msg.append("Options: " + lineSeparator);
		msg.append("  -d or -double                use Double evaluation mode" + lineSeparator);
		msg.append("  -c or -complex               use Complex evaluation mode" + lineSeparator);
		msg.append("  -h or -help                  print this message" + lineSeparator);
		msg.append("  -f or -file <filename>       use given file as input" + lineSeparator);
		msg.append("To stop the program type: " + lineSeparator);
		msg.append("exit<RETURN-KEY>" + lineSeparator);
		msg.append("To switch between the evaluation modes type: " + lineSeparator);
		msg.append("complex<RETURN-KEY> or" + lineSeparator);
		msg.append("double<RETURN-KEY>" + lineSeparator);
		msg.append("To continue an input line type '\\' at the end of the line." + lineSeparator);
		msg.append("****+****+****+****+****+****+****+****+****+****+****+****+");

		System.out.println(msg.toString());
	}

	public Console() {
		super();
	}

	/**
	 * Sets the arguments for the <code>main</code> method
	 * 
	 * @param args
	 *          the aruments of the program
	 */
	private void setArgs(final String args[]) {
		for (int i = 0; i < args.length; i++) {
			final String arg = args[i];
			if (arg.equals("-double") || arg.equals("-d")) {
				fComplexEvaluatorMode = false;
				return;
			} else if (arg.equals("-complex") || arg.equals("-c")) {
				fComplexEvaluatorMode = true;
				return;
			} else if (arg.equals("-help") || arg.equals("-h")) {
				printUsage();
				return;
			} else if (arg.equals("-file") || arg.equals("-f")) {
				try {
					fFile = new File(args[i + 1]);
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
	 * Evaluates the given string-expression and returns the result in
	 * <code>OutputForm</code>
	 * 
	 * @param strEval
	 * 
	 */
	public String interpreter(final String strEval) {
		try {
			if (fComplexEvaluatorMode) {
				Parser p = new Parser();
				ASTNode obj = p.parse(strEval);
				ComplexEvaluator engine = new ComplexEvaluator();
				if (obj instanceof FunctionNode) {
					obj = engine.optimizeFunction((FunctionNode) obj);
				}
				Complex c = engine.evaluateNode(obj);
				return ComplexEvaluator.toString(c);
			} else {
				DoubleEvaluator engine = new DoubleEvaluator();
				double d = engine.evaluate(strEval);
				return Double.toString(d);
			}
		} catch (MathException e) {
			System.err.println();
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * prints a prompt on the console but doesn't print a newline
	 * 
	 * @param out
	 * @param prompt
	 *          the prompt string to display
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
	 *          Description of Parameter
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
	 *          the prompt string to display
	 * @param out
	 *          Description of Parameter
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

}