package org.matheclipse.core.form.output;

import java.io.PrintWriter;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.Precedence;

/** ASCII pretty printer which tries to create a pretty printer ASCII form on 3 lines. */
public class ASCIIPrettyPrinter3 {
  /** The conversion wasn't called with an operator preceding the <code>IExpr</code> object. */
  public static final boolean NO_PLUS_CALL = false;

  /**
   * The conversion was called with a &quot;+&quot; operator preceding the <code>IExpr</code>
   * object.
   */
  public static final boolean PLUS_CALL = true;

  boolean fractionPrinted;
  StringBuilder line1;
  StringBuilder line2;
  StringBuilder line3;

  public ASCIIPrettyPrinter3() {
    line1 = new StringBuilder();
    line2 = new StringBuilder();
    line3 = new StringBuilder();
    fractionPrinted = false;
  }

  public void print(String numerator) {
    printFraction(numerator, null);
    fractionPrinted = false;
  }

  public void printFraction(String numerator, String denominator) {
    int len = numerator.length();
    if (denominator == null) {
      for (int i = 0; i < len; i++) {
        line1.append(' ');
      }
      line2.append(numerator);
      for (int i = 0; i < len; i++) {
        line3.append(' ');
      }
      fractionPrinted = false;
      return;
    }

    int diffN = 0;
    int diffD = 0;
    int rest = 0;
    if (len < denominator.length()) {
      diffN = denominator.length() - len;
      len = denominator.length();
    } else {
      diffD = len - denominator.length();
    }

    // print numerator row
    diffN >>= 1;
    rest = len - numerator.length() - diffN;
    for (int i = 0; i < diffN; i++) {
      line1.append(" ");
    }
    line1.append(numerator);
    for (int i = 0; i < rest; i++) {
      line1.append(" ");
    }

    // print fraction line
    for (int i = 0; i < len; i++) {
      line2.append("-");
    }

    // print denominator row
    diffD >>= 1;
    rest = len - denominator.length() - diffD;
    for (int i = 0; i < diffD; i++) {
      line3.append(" ");
    }
    line3.append(denominator);
    for (int i = 0; i < rest; i++) {
      line3.append(" ");
    }
    fractionPrinted = true;
  }

  @Override
  public String toString() {
    return line1.toString() + "\n" + line2.toString() + "\n" + line3.toString();
  }

  public String[] toStringBuilder() {
    String[] buf = new String[3];
    buf[0] = line1.toString();
    buf[1] = line2.toString();
    buf[2] = line3.toString();

    return buf;
  }

  public void convert(final IExpr expr) {
    fractionPrinted = false;
    convert(expr, 0, NO_PLUS_CALL);
  }

  private void convert(final IExpr expr, final int precedence, boolean caller) {
    if (expr.isPlus()) {
      if (Precedence.PLUS < precedence) {
        print(" ( ");
      }
      IAST plus = (IAST) expr;
      for (int i = 1; i < plus.size(); i++) {
        convert(plus.get(i), Precedence.PLUS, (i == 1) ? NO_PLUS_CALL : PLUS_CALL);
      }
      if (Precedence.PLUS < precedence) {
        print(" ) ");
      }
    } else if (expr.isTimes()) {
      convertTimes((IAST) expr, precedence, caller);
    } else if (expr.isPower()) {
      if (caller == PLUS_CALL) {
        print(" + ");
      }
      convertTimesPowerFraction((IAST) expr, precedence);
    } else if (expr.isAST() && expr.head().isSymbol()) {
      if (caller == PLUS_CALL) {
        print(" + ");
      }
      IAST ast = (IAST) expr;
      ISymbol head = (ISymbol) ast.head();
      if (head.equals(S.List)) {
        print("{");
      } else {
        print(head.toString() + "(");
      }
      for (int i = 1; i < ast.size(); i++) {
        fractionPrinted = false;
        if (i != 1) {
          print(", ");
        }
        convert(ast.get(i), 0, false);
      }
      if (head.equals(S.List)) {
        print("}");
      } else {
        print(")");
      }
    } else {
      if (expr.isReal()) {
        convertNumber((ISignedNumber) expr, caller);
      } else {
        if (caller == PLUS_CALL) {
          print(" + ");
        }
        print(expr.toString());
      }
    }
  }

  private void convertTimes(IAST times, final int precedence, boolean caller) {
    IExpr arg1 = times.arg1();

    if (arg1.isReal()) {
      if (Precedence.TIMES < precedence) {
        print(" ( ");
      }
      if (arg1.isMinusOne()) {
        print(" - ");
      } else {
        if (convertNumber((ISignedNumber) arg1, caller)) {
          print(" * ");
        }
      }

      IExpr timesExpr = times.rest().oneIdentity0();
      if (timesExpr.isTimes()) {
        times = (IAST) timesExpr;
      } else {
        convert(timesExpr, Precedence.TIMES, NO_PLUS_CALL);
        return;
      }
    } else {
      if (caller == PLUS_CALL) {
        print(" + ");
      }
      if (Precedence.TIMES < precedence) {
        print(" ( ");
      }
    }
    convertTimesPowerFraction(times, Precedence.TIMES);
    if (Precedence.TIMES < precedence) {
      print(" ) ");
    }
  }

  /**
   * Convert the Times() or Power() expression into a fraction if possible, otherwise print in one
   * line output form
   *
   * @param ast
   */
  private void convertTimesPowerFraction(IAST ast, final int precedence) {
    IExpr[] parts = Algebra.fractionalPartsTimesPower(ast, true, false, false, false, false, false);
    // IExpr[] parts = Algebra.getFractionalParts(ast, false);
    if (parts == null) {
      print(ast.toString());
      return;
    }
    final IExpr numerator = parts[0];
    final IExpr denominator = parts[1];
    if (!denominator.isOne()) {
      printFraction(numerator.toString(), denominator.toString());
    } else {
      print(ast.toString());
    }
  }

  /**
   * @param number
   * @param caller
   * @return <code>true</code> if the number is not equal <code>-1</code> or <code>1</code>
   */
  private boolean convertNumber(ISignedNumber number, boolean caller) {
    boolean negative = false;
    if (number.isNegative()) {
      number = number.negate();
      negative = true;
    }
    if (caller == PLUS_CALL) {
      if (negative) {
        print(" - ");
      } else {
        print(" + ");
      }
      if (!number.isOne()) {
        if (number.isFraction()) {
          IFraction frac = (IFraction) number;
          printFraction(frac.toBigNumerator().toString(), frac.toBigDenominator().toString());
        } else {
          convert(number, 0, NO_PLUS_CALL);
        }
        return true;
      }
    } else {

      if (number.isFraction()) {
        if (negative) {
          if (fractionPrinted) {
            print(" - ");
          } else {
            print("- ");
          }
        }
        IFraction frac = (IFraction) number;
        printFraction(frac.toBigNumerator().toString(), frac.toBigDenominator().toString());
      } else {
        if (negative) {
          print("-");
        }
        print(number.toString());
      }
      return true;
    }
    return false;
  }

  /**
   * Print the 3 lines <code>outputExpression</code> in the given print stream.
   *
   * @param out
   * @param outputExpression
   * @param prefix a prefix which should be placed before the <code>outputExpression</code>
   */
  public static void prettyPrinter(PrintWriter out, String[] outputExpression, String prefix) {
    if (outputExpression == null) {
      out.println(prefix);
    } else {
      if (outputExpression[0].trim().length() > 0) {
        for (int i = 0; i < prefix.length(); i++) {
          out.print(" ");
        }
        out.println(outputExpression[0]);

        for (int i = 0; i < prefix.length(); i++) {
          out.print(" ");
        }
        out.println(outputExpression[2]);
      } else {
        out.println(prefix + outputExpression[1]);
      }
    }
  }
}
