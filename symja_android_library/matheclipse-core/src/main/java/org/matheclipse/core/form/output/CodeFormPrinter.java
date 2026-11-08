package org.matheclipse.core.form.output;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The text of an expression as C or Fortran source, for <code>CForm</code> and
 * <code>FortranForm</code>: <code>Power(x,2)</code> or <code>x**2</code>, <code>f(x,y)</code>,
 * rational numbers as reals (<code>x/25.</code>), <code>Complex(0,1)</code> or <code>(0,1)</code>,
 * <code>a == b</code> or <code>a.eq.b</code>.
 */
public final class CodeFormPrinter {

  private static final int OR = 1;
  private static final int AND = 2;
  private static final int NOT = 3;
  private static final int RELATION = 4;
  private static final int PLUS = 5;
  private static final int TIMES = 6;
  private static final int POWER = 8;
  private static final int ATOM = 10;

  /** A piece of text and the precedence of its outermost operator. */
  private static final class Code {
    final String text;
    final int precedence;

    Code(String text, int precedence) {
      this.text = text;
      this.precedence = precedence;
    }

    /** The text, in parentheses if its operator binds less tightly than <code>min</code>. */
    String at(int min) {
      return precedence < min ? "(" + text + ")" : text;
    }
  }

  private final boolean fortran;

  private CodeFormPrinter(boolean fortran) {
    this.fortran = fortran;
  }

  /** The C text of <code>expr</code>. */
  public static String cForm(IExpr expr) {
    return new CodeFormPrinter(false).convert(expr).text;
  }

  /** The Fortran text of <code>expr</code>. */
  public static String fortranForm(IExpr expr) {
    return new CodeFormPrinter(true).convert(expr).text;
  }

  /**
   * Break the text of a Fortran expression into lines of at most <code>pageWidth</code>
   * characters after a <code>+</code> or <code>-</code> of the outermost sum. Each continuation
   * line follows an empty line and starts with <code>&gt;</code>.
   */
  public static String breakLines(String text, int pageWidth) {
    List<String> tokens = new ArrayList<>();
    int depth = 0;
    int start = 0;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c == '(') {
        depth++;
      } else if (c == ')') {
        depth--;
      } else if (depth == 0 && (c == '+' || c == '-') && i > 0 && text.charAt(i - 1) == ' '
          && i + 1 < text.length() && text.charAt(i + 1) == ' ') {
        tokens.add(text.substring(start, i + 2));
        start = i + 2;
      }
    }
    tokens.add(text.substring(start));
    final String prefix = ">   ";
    StringBuilder result = new StringBuilder();
    StringBuilder line = new StringBuilder();
    boolean lineHasToken = false;
    for (String token : tokens) {
      if (lineHasToken && line.length() + token.length() > pageWidth) {
        result.append(line).append("\n \n");
        line.setLength(0);
        line.append(prefix);
        lineHasToken = false;
      }
      line.append(token);
      lineHasToken = true;
    }
    result.append(line);
    return result.toString();
  }

  private Code convert(IExpr expr) {
    if (expr instanceof IInteger) {
      IInteger n = (IInteger) expr;
      return new Code(n.toString(), n.isNegative() ? TIMES : ATOM);
    }
    if (expr instanceof IRational) {
      // a rational number is written as a real
      return real(((IRational) expr).doubleValue());
    }
    if (expr instanceof INum) {
      return real(((INum) expr).doubleValue());
    }
    if (expr instanceof IComplex || expr instanceof IComplexNum) {
      INumber z = (INumber) expr;
      String re = number(z.re());
      String im = number(z.im());
      return new Code(fortran ? "(" + re + "," + im + ")" : "Complex(" + re + "," + im + ")",
          ATOM);
    }
    if (expr instanceof IStringX) {
      String text = expr.toString().replace("\\", "\\\\").replace("\"", "\\\"");
      return new Code("\"" + text + "\"", ATOM);
    }
    if (expr.isSymbol()) {
      ISymbol symbol = (ISymbol) expr;
      return new Code(ISymbol.toString(symbol.getContext(), symbol.getSymbolName(),
          org.matheclipse.core.eval.EvalEngine.get()), ATOM);
    }
    if (!expr.isAST()) {
      return new Code(expr.toString(), ATOM);
    }
    final IAST ast = (IAST) expr;
    final IExpr head = ast.head();
    if (head instanceof IBuiltInSymbol) {
      switch (((IBuiltInSymbol) head).ordinal()) {
        case ID.Plus:
          return plus(ast);
        case ID.Times:
          return times(ast);
        case ID.Power:
          if (ast.isAST2()) {
            return power(ast);
          }
          break;
        case ID.Equal:
          return infix(ast, fortran ? ".eq." : " == ", RELATION);
        case ID.Unequal:
          return infix(ast, fortran ? ".ne." : " != ", RELATION);
        case ID.Less:
          return infix(ast, fortran ? ".lt." : " < ", RELATION);
        case ID.LessEqual:
          return infix(ast, fortran ? ".le." : " <= ", RELATION);
        case ID.Greater:
          return infix(ast, fortran ? ".gt." : " > ", RELATION);
        case ID.GreaterEqual:
          return infix(ast, fortran ? ".ge." : " >= ", RELATION);
        case ID.And:
          return infix(ast, fortran ? ".and." : " && ", AND);
        case ID.Or:
          return infix(ast, fortran ? ".or." : " || ", OR);
        case ID.Not:
          if (ast.isAST1()) {
            // C's ! binds tighter than every binary operator: !(a == b)
            return fortran ? new Code(".not." + convert(ast.arg1()).at(NOT + 1), NOT)
                : new Code("!" + convert(ast.arg1()).at(ATOM), NOT);
          }
          break;
        case ID.Mod:
          if (!fortran && ast.isAST2()) {
            return new Code(
                convert(ast.arg1()).at(TIMES) + " % " + convert(ast.arg2()).at(TIMES + 1), TIMES);
          }
          break;
        case ID.Part:
          if (!fortran && ast.size() > 2) {
            StringBuilder buf = new StringBuilder(convert(ast.arg1()).at(ATOM));
            for (int i = 2; i < ast.size(); i++) {
              buf.append('[').append(convert(ast.get(i)).text).append(']');
            }
            return new Code(buf.toString(), ATOM);
          }
          break;
        default:
          break;
      }
    }
    return function(ast);
  }

  /** <code>head(arg1,arg2,...)</code> */
  private Code function(IAST ast) {
    StringBuilder buf = new StringBuilder(convert(ast.head()).at(ATOM));
    buf.append('(');
    for (int i = 1; i < ast.size(); i++) {
      if (i > 1) {
        buf.append(',');
      }
      buf.append(convert(ast.get(i)).text);
    }
    buf.append(')');
    return new Code(buf.toString(), ATOM);
  }

  private Code infix(IAST ast, String operator, int precedence) {
    // a conjunction inside a disjunction keeps its parentheses: (p.and.q).or.r
    final int min = precedence <= AND ? NOT : precedence + 1;
    StringBuilder buf = new StringBuilder();
    for (int i = 1; i < ast.size(); i++) {
      if (i > 1) {
        buf.append(operator);
      }
      buf.append(convert(ast.get(i)).at(min));
    }
    return new Code(buf.toString(), precedence);
  }

  private Code plus(IAST plus) {
    StringBuilder buf = new StringBuilder();
    for (int i = 1; i < plus.size(); i++) {
      IExpr term = plus.get(i);
      if (i > 1) {
        if (isNegativeTerm(term)) {
          buf.append(" - ");
          buf.append(convert(term.negate()).at(PLUS + 1));
          continue;
        }
        buf.append(" + ");
      }
      buf.append(convert(term).at(PLUS));
    }
    return new Code(buf.toString(), PLUS);
  }

  /** A summand which is subtracted: a negative number or a product with a negative number. */
  private static boolean isNegativeTerm(IExpr term) {
    if (term.isReal()) {
      return ((IReal) term).isNegative();
    }
    return term.isTimes() && term.first().isReal() && ((IReal) term.first()).isNegative();
  }

  private Code times(IAST times) {
    IExpr coefficient = F.C1;
    List<IExpr> numerator = new ArrayList<>();
    List<IExpr> denominator = new ArrayList<>();
    for (IExpr factor : times) {
      if (factor.isNumber()) {
        coefficient = coefficient.times(factor);
      } else if (factor.isPower() && factor.exponent().isReal()
          && ((IReal) factor.exponent()).isNegative()) {
        IExpr exponent = factor.exponent().negate();
        denominator.add(exponent.isOne() ? factor.base() : F.Power(factor.base(), exponent));
      } else {
        numerator.add(factor);
      }
    }
    String sign = "";
    List<Code> numeratorCode = new ArrayList<>();
    List<Code> denominatorCode = new ArrayList<>();
    if (coefficient instanceof IRational) {
      IRational q = (IRational) coefficient;
      IInteger p = q.numerator();
      if (p.isMinusOne() && !numerator.isEmpty()) {
        sign = "-";
      } else if (!p.isOne() || numerator.isEmpty()) {
        numeratorCode.add(new Code(p.toString(), ATOM));
      }
      if (!q.denominator().isOne()) {
        // the denominator is written as a real
        denominatorCode.add(real(q.denominator().doubleValue()));
      }
    } else if (coefficient.isReal()) {
      double value = ((IReal) coefficient).evalf();
      if (value == -1.0 && !numerator.isEmpty()) {
        sign = "-";
      } else {
        numeratorCode.add(real(value));
      }
    } else {
      numeratorCode.add(convert(coefficient));
    }
    for (IExpr factor : numerator) {
      numeratorCode.add(convert(factor));
    }
    for (IExpr factor : denominator) {
      denominatorCode.add(convert(factor));
    }
    String numeratorText = join(numeratorCode);
    if (denominatorCode.isEmpty()) {
      return new Code(sign + numeratorText, TIMES);
    }
    if (numeratorCode.isEmpty()) {
      numeratorText = "1";
    } else if (numeratorCode.size() > 1) {
      numeratorText = "(" + numeratorText + ")";
    }
    String denominatorText = denominatorCode.size() > 1 ? "(" + join(denominatorCode) + ")"
        : denominatorCode.get(0).at(POWER);
    return new Code(sign + numeratorText + "/" + denominatorText, TIMES);
  }

  /**
   * The factors joined with <code>*</code>, each in parentheses if it is a sum. A later factor
   * which is itself a product, like C's <code>b % c</code>, is enclosed too, because
   * <code>a*b % c</code> groups as <code>(a*b) % c</code>.
   */
  private static String join(List<Code> factors) {
    StringBuilder buf = new StringBuilder();
    for (int i = 0; i < factors.size(); i++) {
      if (i > 0) {
        buf.append('*');
      }
      buf.append(factors.get(i).at(i == 0 ? TIMES : TIMES + 1));
    }
    return buf.toString();
  }

  private Code power(IAST power) {
    IExpr base = power.base();
    IExpr exponent = power.exponent();
    if (exponent.isReal() && ((IReal) exponent).isNegative()) {
      return times(F.Times(F.C1, power));
    }
    if (exponent.isNumEqualRational(F.C1D2)) {
      return new Code("Sqrt(" + convert(base).text + ")", ATOM);
    }
    // a rational exponent is written as a real
    Code exponentCode = exponent.isInteger() ? convert(exponent)
        : exponent.isRational() ? real(((IRational) exponent).doubleValue()) : convert(exponent);
    if (fortran) {
      return new Code(convert(base).at(POWER + 1) + "**" + exponentCode.at(POWER + 1), POWER);
    }
    return new Code("Power(" + convert(base).text + "," + exponentCode.text + ")", ATOM);
  }

  /** The text of the real or imaginary part of a complex number. */
  private String number(IExpr part) {
    if (part instanceof IInteger) {
      return part.toString();
    }
    return real(((IReal) part).evalf()).text;
  }

  /**
   * The shortest text which reads back as <code>value</code>: <code>2.5</code>,
   * <code>1000.</code>, from a million on and below <code>10^-5</code> with an exponent,
   * <code>1.e6</code>, <code>2.5e-8</code>.
   */
  private static Code real(double value) {
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      return new Code(Double.toString(value), ATOM);
    }
    if (value == 0.0) {
      return new Code("0.", ATOM);
    }
    BigDecimal decimal = new BigDecimal(Double.toString(Math.abs(value))).stripTrailingZeros();
    String digits = decimal.unscaledValue().toString();
    // value == 0.d1d2... * 10^(exponent + 1) == d1.d2... * 10^exponent
    int exponent = digits.length() - 1 - decimal.scale();
    String sign = value < 0 ? "-" : "";
    String text;
    if (exponent >= 6 || exponent < -5) {
      text = digits.charAt(0) + "." + digits.substring(1) + "e" + exponent;
    } else if (exponent < 0) {
      StringBuilder buf = new StringBuilder("0.");
      for (int i = -1; i > exponent; i--) {
        buf.append('0');
      }
      text = buf.append(digits).toString();
    } else if (digits.length() <= exponent + 1) {
      StringBuilder buf = new StringBuilder(digits);
      for (int i = digits.length(); i <= exponent; i++) {
        buf.append('0');
      }
      text = buf.append('.').toString();
    } else {
      text = digits.substring(0, exponent + 1) + "." + digits.substring(exponent + 1);
    }
    return new Code(sign + text, value < 0 ? TIMES : ATOM);
  }
}
