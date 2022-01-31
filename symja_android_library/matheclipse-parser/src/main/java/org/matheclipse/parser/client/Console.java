package org.matheclipse.parser.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.matheclipse.parser.client.eval.DoubleEvaluator;
import org.matheclipse.parser.client.math.MathException;

/**
 * A java console program to run the DoubleEvaluator and ComplexEvalVisitor numerical evaluators
 * interactivly.
 *
 * @see DoubleEvaluator
 */
public class Console {

  private File fFile;

  public static void main(final String args[]) {
    Console console = new Console();

    String expr = null;

    while (true) {
      try {
        expr = console.readString(System.out, ">> ");
        if (expr != null) {
          if ((expr.length() >= 4) && expr.toLowerCase().substring(0, 4).equals("exit")) {
            break;
          }
          if ((expr.length() >= 6) && expr.toLowerCase().substring(0, 6).equals("double")) {
            // console.fComplexEvaluatorMode = false;
            System.out
                .println("Double evaluation mode (switch to other mode with keyword 'complex')");
            continue;
          }
          // if ((expr.length() >= 7) && expr.toLowerCase().substring(0, 7).equals("complex")) {
          // console.fComplexEvaluatorMode = true;
          // System.out.println("Complex evaluation mode (switch to other mode with keyword
          // 'double')");
          // continue;
          // }
          System.out.println(console.interpreter(expr));
        }
      } catch (final Exception e) {
        System.out.println(e.getMessage());
      }
    }
  }

  public Console() {
    super();
  }

  /**
   * Evaluates the given string-expression and returns the result in string form.
   *
   * @param strEval
   * @return the result in string for
   */
  public String interpreter(final String strEval) {
    try {
      DoubleEvaluator engine = new DoubleEvaluator(true);
      double d = engine.evaluate(strEval);
      return Double.toString(d);

    } catch (MathException e) {
      System.err.println();
      System.err.println(e.getMessage());
    } catch (RuntimeException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * prints a prompt on the console but doesn't print a newline
   *
   * @param out
   * @param prompt the prompt string to display
   */
  public void printPrompt(final PrintStream out, final String prompt) {
    out.print(prompt);
    out.flush();
  }

  /**
   * read a string from the console. The string is terminated by a newline
   *
   * @param out Description of Parameter
   * @return the input string (without the newline)
   */
  public String readString(final PrintStream out) {
    final StringBuffer input = new StringBuffer();
    final BufferedReader in =
        new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
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
   * @param prompt the prompt string to display
   * @param out Description of Parameter
   * @return the input string (without the newline)
   */
  public String readString(final PrintStream out, final String prompt) {
    printPrompt(out, prompt);
    return readString(out);
  }

  /** @param file */
  public void setFile(final File file) {
    fFile = file;
  }

  /** @return the file with which the program was started or <code>null</code> */
  public File getFile() {
    return fFile;
  }
}
