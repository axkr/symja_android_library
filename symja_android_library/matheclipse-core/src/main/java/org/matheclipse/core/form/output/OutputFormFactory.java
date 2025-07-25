package org.matheclipse.core.form.output;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.ApfloatToMMA;
import org.matheclipse.core.form.DoubleToMMA;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.parser.client.Characters;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.PostfixOperator;
import org.matheclipse.parser.client.operator.Precedence;
import org.matheclipse.parser.client.operator.PrefixOperator;
import it.unimi.dsi.fastutil.ints.IntList;

/** Converts an internal <code>IExpr</code> into a user readable string. */
public class OutputFormFactory {
  private static final Logger LOGGER = LogManager.getLogger(OutputFormFactory.class);

  /** The conversion wasn't called with an operator preceding the <code>IExpr</code> object. */
  public static final boolean NO_PLUS_CALL = false;

  /**
   * The conversion was called with a &quot;+&quot; operator preceding the <code>IExpr</code>
   * object.
   */
  public static final boolean PLUS_CALL = true;

  /**
   * If <code>true</code> use &quot;(&quot; and &quot;)&quot; as parenthesis for function arguments;
   * otherwise use &quot;[&quot; and &quot;]&quot;
   */
  private final boolean fRelaxedSyntax;

  /**
   * Write the arguments of a {@link S#Plus} expression in reversed order.
   */
  private final boolean fPlusReversed;

  /**
   * Write complex numbers as <code>re + im*I</code> instead of <code>re + I*im</code>
   */
  private final boolean fComplexReImI;

  private boolean fIgnoreNewLine = Config.DEFAULT_OUTPUT_FORM_IGNORE_NEW_LINE;

  /** If <code>true</code> print leading and trailing quotes in Symja strings */
  protected boolean fInputForm = false;

  private boolean fEmpty = true;

  private boolean fUseSignificantFiguresInApfloat = false;

  private int fColumnCounter = 0;

  /**
   * Use scientific notation for all numbers with exponents outside the range
   * <code>-exponentFigures</code> to <code>exponentFigures</code>
   */
  private int fExponentFigures;

  /**
   * The number of significant figures which should be printed for <code>double</code> floating
   * point numbers
   */
  private int fSignificantFigures;

  /**
   * 
   * @param relaxedSyntax if <code>true</code> use &quot;(&quot; and &quot;)&quot; as parenthesis
   *        for function arguments; otherwise use &quot;[&quot; and &quot;]&quot;
   * @param plusReversed write the arguments of a {@link S#Plus} expression in reversed order
   * @param exponentFigures use scientific notation for all numbers with exponents outside the range
   *        <code>-exponentFigures</code> to <code>exponentFigures</code>
   * @param significantFigures the number of significant figures which should be printed for
   *        <code>double</code> floating point numbers
   */
  protected OutputFormFactory(final boolean relaxedSyntax, final boolean plusReversed,
      int exponentFigures, int significantFigures) {
    this(relaxedSyntax, plusReversed, false, exponentFigures, significantFigures);
  }

  /**
   * 
   * @param relaxedSyntax if <code>true</code> use &quot;(&quot; and &quot;)&quot; as parenthesis
   *        for function arguments; otherwise use &quot;[&quot; and &quot;]&quot;
   * @param plusReversed write the arguments of a {@link S#Plus} expression in reversed order.
   * @param complexReImI write complex numbers as <code>re + im*I</code> instead of
   *        <code>re + I*im</code>
   * @param exponentFigures use scientific notation for all numbers with exponents outside the range
   *        <code>-exponentFigures</code> to <code>exponentFigures</code>.
   * @param significantFigures the number of significant figures which should be printed for
   *        <code>double</code> floating point numbers
   */
  protected OutputFormFactory(final boolean relaxedSyntax, final boolean plusReversed,
      final boolean complexReImI, int exponentFigures, int significantFigures) {
    this.fRelaxedSyntax = relaxedSyntax;
    this.fPlusReversed = plusReversed;
    this.fExponentFigures = exponentFigures;
    this.fSignificantFigures = significantFigures;
    this.fComplexReImI = complexReImI;
  }

  /**
   * Reset the column counter.
   *
   * @param useSignificantFiguresInApfloat shorten an Apfloat to the number of significantFigures if
   *        possible
   */
  public void reset(boolean useSignificantFiguresInApfloat) {
    fColumnCounter = 0;
    fUseSignificantFiguresInApfloat = useSignificantFiguresInApfloat;
  }

  /**
   * Get an <code>OutputFormFactory</code> for converting an internal expression to a user readable
   * string.
   *
   * @param relaxedSyntax If <code>true</code> use paranthesis instead of square brackets and ignore
   *        case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *        brackets instead of double square brackets for extracting parts of an expression, i.e.
   *        {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @return
   */
  public static OutputFormFactory get(final boolean relaxedSyntax) {
    return get(relaxedSyntax, false, false, -1, -1);
  }

  /**
   * Get an <code>OutputFormFactory</code> for converting an internal expression to a user readable
   * string.
   *
   * @param relaxedSyntax if <code>true</code> use paranthesis instead of square brackets and ignore
   *        case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *        brackets instead of double square brackets for extracting parts of an expression, i.e.
   *        {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @param plusReversed if <code>true</code> the arguments of the <code>Plus()</code> function will
   *        be printed in reversed order
   * @return
   */
  public static OutputFormFactory get(final boolean relaxedSyntax, final boolean plusReversed) {
    return get(relaxedSyntax, plusReversed, false, -1, -1);
  }

  /**
   * Get an <code>OutputFormFactory</code> for converting an internal expression to a user readable
   * string.
   *
   * @param relaxedSyntax if <code>true</code> use parenthesis instead of square brackets and ignore
   *        case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *        brackets instead of double square brackets for extracting parts of an expression, i.e.
   *        {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @param plusReversed if <code>true</code> the arguments of the <code>Plus()</code> function will
   *        be printed in reversed order
   * @param exponentFigures
   * @param significantFigures
   * @return
   */
  public static OutputFormFactory get(final boolean relaxedSyntax, final boolean plusReversed,
      int exponentFigures, int significantFigures) {
    return new OutputFormFactory(relaxedSyntax, plusReversed, false, exponentFigures,
        significantFigures);
  }

  /**
   * Get an <code>OutputFormFactory</code> for converting an internal expression to a user readable
   * string.
   *
   * @param relaxedSyntax if <code>true</code> use parenthesis instead of square brackets and ignore
   *        case for functions, i.e. sin() instead of Sin[]. If <code>true</code> use single square
   *        brackets instead of double square brackets for extracting parts of an expression, i.e.
   *        {a,b,c,d}[1] instead of {a,b,c,d}[[1]].
   * @param plusReversed if <code>true</code> the arguments of the <code>Plus()</code> function will
   *        be printed in reversed order
   * @param complexReImI write complex numbers as <code>re + im*I</code> instead of
   *        <code>re + I*im</code>
   * @param exponentFigures use scientific notation for all numbers with exponents outside the range
   *        <code>-exponentFigures</code> to <code>exponentFigures</code>.
   * @param significantFigures the number of significant figures which should be printed for
   *        <code>double</code> floating point numbers
   * @return
   */
  public static OutputFormFactory get(final boolean relaxedSyntax, final boolean plusReversed,
      final boolean complexReImI, int exponentFigures, int significantFigures) {
    return new OutputFormFactory(relaxedSyntax, plusReversed, complexReImI, exponentFigures,
        significantFigures);
  }

  /**
   * Get an <code>OutputFormFactory</code> for converting an internal expression to a user readable
   * string, with <code>relaxedSyntax</code> set to false.
   *
   * @return
   * @see #get(boolean)
   */
  public static OutputFormFactory get() {
    return get(false);
  }

  public void convertDouble(final Appendable buf, final INum d, final int precedence,
      boolean caller) throws IOException {
    final double doubleValue = d.doubleValue();
    convertDouble(buf, doubleValue, d, precedence, caller);
  }

  private void convertDouble(final Appendable buf, final double doubleValue, final INum d,
      final int precedence, boolean caller) throws IOException {
    final boolean isNegative = d.isNegative();
    if (d instanceof ApfloatNum) {
      Apfloat apfloat = ((ApfloatNum) d).apfloatValue();
      if (!isNegative && caller == PLUS_CALL) {
        append(buf, fInputForm ? " + " : "+");
      }
      String str = fInputForm ? ApfloatNum.fullFormString(apfloat)
          : convertApfloatToFormattedString(apfloat);
      convertDoubleString(buf, str, precedence, isNegative);
      return;
    }
    if (F.isZero(doubleValue, Config.ZERO_IN_OUTPUT_FORMAT)) {
      if (fInputForm) {
        convertDoubleString(buf, d.fullFormString(), precedence, false);
      } else {
        convertDoubleString(buf, convertDoubleToFormattedString(0.0), precedence, false);
      }
      return;
    }

    if (!isNegative && caller == PLUS_CALL) {
      append(buf, fInputForm ? " + " : "+");
    }
    if (d instanceof Num) {
      if (fInputForm) {
        convertDoubleString(buf, d.fullFormString(), precedence, isNegative);
      } else {
        convertDoubleString(buf, convertDoubleToFormattedString(doubleValue), precedence,
            isNegative);
      }
    }
  }

  private void convertDouble(final Appendable buf, final double doubleValue) throws IOException {
    if (F.isZero(doubleValue, Config.ZERO_IN_OUTPUT_FORMAT)) {
      convertDoubleString(buf, convertDoubleToFormattedString(0.0), Precedence.NO_PRECEDENCE,
          false);
      return;
    }
    convertDoubleString(buf, convertDoubleToFormattedString(doubleValue), Precedence.NO_PRECEDENCE,
        false);
  }

  private String convertApfloatToFormattedString(Apfloat value) {
    StringBuilder buf = new StringBuilder();
    int numericPrecision = (int) EvalEngine.get().getNumericPrecision();
    if (fUseSignificantFiguresInApfloat) {
      ApfloatToMMA.apfloatToMMA(buf, value, numericPrecision, fSignificantFigures,
          fUseSignificantFiguresInApfloat);
    } else {
      ApfloatToMMA.apfloatToMMA(buf, value, numericPrecision, numericPrecision,
          fUseSignificantFiguresInApfloat);
    }
    return buf.toString();
  }

  private String convertDoubleToFormattedString(double dValue) {
    if (fSignificantFigures > 0) {
      StringBuilder buf = new StringBuilder();
      DoubleToMMA.doubleToMMA(buf, dValue, fExponentFigures, fSignificantFigures, fRelaxedSyntax);
      return buf.toString();
    }
    return Double.toString(dValue);
  }

  private void convertDoubleString(final Appendable buf, final String d, final int precedence,
      final boolean isNegative) throws IOException {
    if (isNegative && (Precedence.PLUS < precedence)) {
      append(buf, "(");
    }
    append(buf, d);
    if (isNegative && (Precedence.PLUS < precedence)) {
      append(buf, ")");
    }
  }

  public void convertDoubleComplex(final Appendable buf, final IComplexNum dc, final int precedence,
      boolean caller) throws IOException {
    if (fComplexReImI) {
      convertDoubleComplexReImI(buf, dc, precedence, caller);
      return;
    }
    if (dc instanceof ApcomplexNum) {
      convertApcomplex(buf, ((ApcomplexNum) dc).apcomplexValue(), precedence, caller);
      return;
    }
    if (Precedence.PLUS < precedence) {
      if (caller == PLUS_CALL) {
        append(buf, fInputForm ? " + " : "+");
        caller = false;
      }
      append(buf, "(");
    }

    double realPart = dc.getRealPart();
    double imaginaryPart = dc.getImaginaryPart();
    boolean realZero = F.isZero(realPart);
    boolean imaginaryZero = F.isZero(imaginaryPart);
    if (realZero && imaginaryZero) {
      convertDoubleString(buf, convertDoubleToFormattedString(0.0), Precedence.PLUS, false);
    } else {
      if (!realZero) {
        String str =
            fInputForm ? Num.fullFormString(realPart) : convertDoubleToFormattedString(realPart);
        append(buf, str);
        if (!imaginaryZero) {
          append(buf, "+I*");
          final boolean isNegative = imaginaryPart < 0;
          str = fInputForm ? Num.fullFormString(imaginaryPart)
              : convertDoubleToFormattedString(imaginaryPart);
          convertDoubleString(buf, str, Precedence.TIMES, isNegative);
        }
      } else {
        if (caller == PLUS_CALL) {
          append(buf, fInputForm ? " + " : "+");
          caller = false;
        }
        append(buf, "I*");
        final boolean isNegative = imaginaryPart < 0;
        String str = fInputForm ? Num.fullFormString(imaginaryPart)
            : convertDoubleToFormattedString(imaginaryPart);
        convertDoubleString(buf, str, Precedence.TIMES, isNegative);
      }
    }

    if (Precedence.PLUS < precedence) {
      append(buf, ")");
    }
  }

  public void convertDoubleComplexReImI(final Appendable buf, final IComplexNum dc,
      final int precedence, boolean caller) throws IOException {
    if (dc instanceof ApcomplexNum) {
      convertApcomplex(buf, ((ApcomplexNum) dc).apcomplexValue(), precedence, caller);
      return;
    }
    if (Precedence.PLUS < precedence) {
      if (caller == PLUS_CALL) {
        append(buf, fInputForm ? " + " : "+");
        caller = false;
      }
      append(buf, "(");
    }

    double realPart = dc.getRealPart();
    double imaginaryPart = dc.getImaginaryPart();
    boolean realZero = F.isZero(realPart);
    boolean imaginaryZero = F.isZero(imaginaryPart);
    if (realZero && imaginaryZero) {
      convertDoubleString(buf, convertDoubleToFormattedString(0.0), Precedence.PLUS, false);
    } else {
      if (!realZero) {
        String str =
            fInputForm ? Num.fullFormString(realPart) : convertDoubleToFormattedString(realPart);
        append(buf, str);
        if (!imaginaryZero) {
          final boolean isNegative = imaginaryPart < 0;
          if (!isNegative) {
            append(buf, "+");
          }
          str = fInputForm ? Num.fullFormString(imaginaryPart)
              : convertDoubleToFormattedString(imaginaryPart);
          convertDoubleString(buf, str, Precedence.PLUS, isNegative);
          append(buf, "*I");
        }
      } else {
        final boolean isNegative = imaginaryPart < 0;
        if (!isNegative && caller == PLUS_CALL) {
          append(buf, "+");
          caller = false;
        }

        String str = fInputForm ? Num.fullFormString(imaginaryPart)
            : convertDoubleToFormattedString(imaginaryPart);
        convertDoubleString(buf, str, precedence, isNegative);
        append(buf, "*I");
      }
    }

    if (Precedence.PLUS < precedence) {
      append(buf, ")");
    }
  }

  public void convertApcomplex(final Appendable buf, final Apcomplex dc, final int precedence,
      boolean caller) throws IOException {
    if (fComplexReImI) {
      convertApcomplexReImI(buf, dc, precedence, caller);
      return;
    }
    if (Precedence.PLUS < precedence) {
      if (caller == PLUS_CALL) {
        append(buf, fInputForm ? " + " : "+");
        caller = false;
      }
      append(buf, "(");
    }
    Apfloat realPart = dc.real();
    Apfloat imaginaryPart = dc.imag();
    boolean realZero = realPart.equals(Apcomplex.ZERO);
    boolean imaginaryZero = imaginaryPart.equals(Apcomplex.ZERO);
    if (realZero && imaginaryZero) {
      convertDoubleString(buf, "0.0", Precedence.PLUS, false);
    } else {
      if (!realZero) {
        String str = fInputForm ? ApfloatNum.fullFormString(realPart)
            : convertApfloatToFormattedString(realPart);
        append(buf, str);
        if (!imaginaryZero) {
          append(buf, "+I*");
          final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
          str = fInputForm ? ApfloatNum.fullFormString(imaginaryPart)
              : convertApfloatToFormattedString(imaginaryPart);
          convertDoubleString(buf, str, Precedence.TIMES, isNegative);
        }
      } else {
        if (caller == PLUS_CALL) {
          append(buf, fInputForm ? " + " : "+");
          caller = false;
        }
        append(buf, "I*");
        final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
        String str = fInputForm ? ApfloatNum.fullFormString(imaginaryPart)
            : convertApfloatToFormattedString(imaginaryPart);
        convertDoubleString(buf, str, Precedence.TIMES, isNegative);
      }
    }
    if (Precedence.PLUS < precedence) {
      append(buf, ")");
    }
  }

  public void convertApcomplexReImI(final Appendable buf, final Apcomplex dc, final int precedence,
      boolean caller) throws IOException {
    if (Precedence.PLUS < precedence) {
      if (caller == PLUS_CALL) {
        append(buf, fInputForm ? " + " : "+");
        caller = false;
      }
      append(buf, "(");
    }
    Apfloat realPart = dc.real();
    Apfloat imaginaryPart = dc.imag();
    boolean realZero = realPart.equals(Apcomplex.ZERO);
    boolean imaginaryZero = imaginaryPart.equals(Apcomplex.ZERO);
    if (realZero && imaginaryZero) {
      convertDoubleString(buf, "0.0", Precedence.PLUS, false);
    } else {
      if (!realZero) {
        String str = fInputForm ? ApfloatNum.fullFormString(realPart)
            : convertApfloatToFormattedString(realPart);
        append(buf, str);
        if (!imaginaryZero) {
          final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
          if (!isNegative) {
            append(buf, fInputForm ? " + " : "+");
          }
          str = fInputForm ? ApfloatNum.fullFormString(imaginaryPart)
              : convertApfloatToFormattedString(imaginaryPart);
          convertDoubleString(buf, str, Precedence.PLUS, isNegative);
          append(buf, "*I");
        }
      } else {
        final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
        if (!isNegative && caller == PLUS_CALL) {
          append(buf, fInputForm ? " + " : "+");
          caller = false;
        }
        String str = fInputForm ? ApfloatNum.fullFormString(imaginaryPart)
            : convertApfloatToFormattedString(imaginaryPart);
        convertDoubleString(buf, str, precedence, isNegative);
        append(buf, "*I");
      }
    }
    if (Precedence.PLUS < precedence) {
      append(buf, ")");
    }
  }

  // public static String convertApfloat(Apfloat num) {
  // final long precision = EvalEngine.get().getNumericPrecision();
  // final long scale = num.scale();
  // if (scale >= -precision && scale <= precision) {
  // return num.toString(true);
  // }
  // String str = num.toString();
  // int index = str.indexOf('e');
  // if (index > 0) {
  // StringBuilderWriter stw = new StringBuilderWriter();
  // try {
  // FormattingWriter.toMMA(num, stw, 15, false);
  // return stw.toString();
  // } catch (IOException e) {
  // LOGGER.error("OutputFormFactory.convertApfloat() failed", e);
  // }
  // // String exponentStr = str.substring(index + 1);
  // // String result = str.substring(0, index);
  // // return result + "*10^" + exponentStr;
  // }
  // return str;
  // }

  public void convertInteger(final Appendable buf, IInteger i, final int precedence, boolean caller)
      throws IOException {
    final boolean isNegative = i.isNegative();
    if (!isNegative && caller == PLUS_CALL) {
      append(buf, fInputForm ? " + " : "+");
    }
    if (isNegative && (Precedence.PLUS < precedence)) {
      append(buf, "(");
    }
    final String str;
    if (isNegative) {
      str = i.toString().substring(1);
      append(buf, fInputForm && (caller == PLUS_CALL) ? " - " : "-");
    } else {
      str = i.toString();
    }

    if ((str.length() + getColumnCounter() > Config.MAX_OUTPUT_LINE)) {
      if (getColumnCounter() > 40) {
        newLine(buf);
      }
      final int len = str.length();
      for (int j = 0; j < len; j += 79) {
        if (j + 79 < len) {
          append(buf, str.substring(j, j + 79));
          append(buf, '\\');
          newLine(buf);
        } else {
          append(buf, str.substring(j, len));
        }
      }
    } else {
      append(buf, str);
    }
    if (isNegative && (Precedence.PLUS < precedence)) {
      append(buf, ")");
    }
  }

  public void convertFraction(final Appendable buf, final IRational f, final int precedence,
      boolean caller) throws IOException {
    convertFraction(buf, f.toBigNumerator(), f.toBigDenominator(), precedence, caller);
  }

  public void convertFraction(final Appendable buf, BigInteger numerator, BigInteger denominator,
      final int precedence, boolean caller) throws IOException {
    boolean isInteger = denominator.compareTo(BigInteger.ONE) == 0;
    final boolean isNegative = numerator.signum() < 0;
    final int prec = isNegative ? Precedence.PLUS : Precedence.TIMES;
    if (!isNegative) {
      if (caller == PLUS_CALL) {
        append(buf, fInputForm ? " + " : "+");
      }
    }

    if (prec < precedence) {
      append(buf, "(");
    }
    if (isNegative) {
      numerator = numerator.negate();
      append(buf, fInputForm && (caller == PLUS_CALL) ? " - " : "-");
    }
    String str = numerator.toString();
    if ((str.length() + getColumnCounter() > Config.MAX_OUTPUT_LINE)) {
      if (getColumnCounter() > 40) {
        newLine(buf);
      }
      final int len = str.length();
      for (int j = 0; j < len; j += 79) {
        if (j + 79 < len) {
          append(buf, str.substring(j, j + 79));
          append(buf, '\\');
          newLine(buf);
        } else {
          append(buf, str.substring(j, len));
        }
      }
    } else {
      append(buf, str);
    }
    if (!isInteger) {
      append(buf, "/");
      str = denominator.toString();
      if ((str.length() + getColumnCounter() > Config.MAX_OUTPUT_LINE)) {
        if (getColumnCounter() > 40) {
          newLine(buf);
        }
        final int len = str.length();
        for (int j = 0; j < len; j += 79) {
          if (j + 79 < len) {
            append(buf, str.substring(j, j + 79));
            append(buf, '\\');
            newLine(buf);
          } else {
            append(buf, str.substring(j, len));
          }
        }
      } else {
        append(buf, str);
      }
    }
    if (prec < precedence) {
      append(buf, ")");
    }
  }

  public void convertComplex(final Appendable buf, final IComplex c, final int precedence,
      boolean caller) throws IOException {
    if (fComplexReImI) {
      convertComplexReImI(buf, c, precedence, caller);
      return;
    }
    boolean isReZero = c.getRealPart().isZero();
    final boolean isImOne = c.getImaginaryPart().isOne();
    final boolean isImMinusOne = c.getImaginaryPart().isMinusOne();
    if (!isReZero && (Precedence.PLUS < precedence)) {
      if (caller == PLUS_CALL) {
        append(buf, fInputForm ? " + " : "+");
        caller = false;
      }
      append(buf, "(");
    }
    if (!isReZero) {
      convertFraction(buf, c.getRealPart(), Precedence.PLUS, caller);
    }
    if (isImOne) {
      if (isReZero) {
        if (caller == PLUS_CALL) {
          append(buf, fInputForm ? " + " : "+");
          caller = false;
        }
        append(buf, "I");
        return;
      } else {
        append(buf, fInputForm ? " + I" : "+I");
      }
    } else if (isImMinusOne) {
      append(buf, fInputForm ? " - I" : "-I");
    } else {
      final IRational im = c.getImaginaryPart();
      int oldColumnCounter = fColumnCounter;
      StringBuilder imagBuf = new StringBuilder();
      try {
        if (im.isNegative()) {
          if (isReZero && (Precedence.TIMES < precedence)) {
            if (caller == PLUS_CALL) {
              append(buf, fInputForm ? " + " : "+");
            }
            append(buf, "(");
          }
          append(buf, fInputForm ? " - " : "-");
          oldColumnCounter = fColumnCounter;
          fColumnCounter = 0;
          append(imagBuf, "I*");
          convertFraction(imagBuf, im.negate(), Precedence.TIMES, NO_PLUS_CALL);
        } else {
          if (isReZero) {
            if (caller == PLUS_CALL) {
              append(buf, fInputForm ? " + " : "+");
            }
            if (Precedence.TIMES < precedence) {
              append(buf, "(");
            }
            oldColumnCounter = fColumnCounter;
            fColumnCounter = 0;
            append(imagBuf, "I*");
          } else {
            append(buf, fInputForm ? " + " : "+");
            oldColumnCounter = fColumnCounter;
            fColumnCounter = 0;
            append(imagBuf, "I*");
          }
          convertFraction(imagBuf, im, Precedence.TIMES, NO_PLUS_CALL);
        }

      } finally {
        fColumnCounter = oldColumnCounter;
      }
      String str = imagBuf.toString();
      if ((str.length() + getColumnCounter() > Config.MAX_OUTPUT_LINE)) {
        newLine(buf);
      }
      append(buf, str);
      if (isReZero && (Precedence.TIMES < precedence)) {
        append(buf, ")");
      }
    }

    if (!isReZero && (Precedence.PLUS < precedence)) {
      append(buf, ")");
    }
  }

  public void convertComplexReImI(final Appendable buf, final IComplex c, final int precedence,
      boolean caller) throws IOException {
    boolean isReZero = c.getRealPart().isZero();
    final boolean isImOne = c.getImaginaryPart().isOne();
    final boolean isImMinusOne = c.getImaginaryPart().isMinusOne();
    if (!isReZero && (Precedence.PLUS < precedence)) {
      if (caller == PLUS_CALL) {
        append(buf, fInputForm ? " + " : "+");
        caller = false;
      }
      append(buf, "(");
    }
    if (!isReZero) {
      convertFraction(buf, c.getRealPart(), Precedence.PLUS, caller);
    }
    if (isImOne) {
      if (isReZero) {
        if (caller == PLUS_CALL) {
          append(buf, fInputForm ? " + " : "+");
          caller = false;
        }
        append(buf, "I");
        return;
      } else {
        append(buf, fInputForm ? " + I" : "+I");
      }
    } else if (isImMinusOne) {
      append(buf, fInputForm ? " - I" : "-I");
    } else {
      final IRational im = c.getImaginaryPart();
      int oldColumnCounter = fColumnCounter;
      StringBuilder imagBuf = new StringBuilder();
      try {
        if (im.isNegative()) {
          if (isReZero && (Precedence.TIMES < precedence)) {
            if (caller == PLUS_CALL) {
              append(buf, fInputForm ? " + " : "+");
            }
            append(buf, "(");
          }
          append(buf, fInputForm ? " - " : "-");
          oldColumnCounter = fColumnCounter;
          fColumnCounter = 0;
          convertFraction(imagBuf, im.negate(), Precedence.TIMES, NO_PLUS_CALL);
        } else {
          if (isReZero) {
            if (caller == PLUS_CALL) {
              append(buf, fInputForm ? " + " : "+");
            }
            if (Precedence.TIMES < precedence) {
              append(buf, "(");
            }
            oldColumnCounter = fColumnCounter;
            fColumnCounter = 0;
          } else {
            append(buf, fInputForm ? " + " : "+");
            oldColumnCounter = fColumnCounter;
            fColumnCounter = 0;
          }
          convertFraction(imagBuf, im, Precedence.TIMES, NO_PLUS_CALL);
        }
        append(imagBuf, "*I");

      } finally {
        fColumnCounter = oldColumnCounter;
      }
      String str = imagBuf.toString();
      if ((str.length() + getColumnCounter() > Config.MAX_OUTPUT_LINE)) {
        newLine(buf);
      }
      append(buf, str);
      if (isReZero && (Precedence.TIMES < precedence)) {
        append(buf, ")");
      }
    }

    if (!isReZero && (Precedence.PLUS < precedence)) {
      append(buf, ")");
    }
  }

  public void convertString(final Appendable buf, final String str) throws IOException {
    if (fInputForm) {
      append(buf, "\"");
      append(buf, str);
      append(buf, "\"");
    } else {
      appendUnicodeMapped(buf, str);
    }
  }

  public void convertSymbol(final Appendable buf, final ISymbol symbol) throws IOException {
    append(buf, ISymbol.toString(symbol.getContext(), symbol.getSymbolName(), EvalEngine.get()));
    // Context context = symbol.getContext();
    // if (context == Context.DUMMY) {
    // append(buf, symbol.getSymbolName());
    // return;
    // }
    // if (context.equals(Context.SYSTEM)) {
    // String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(symbol.getSymbolName());
    // if (str != null) {
    // append(buf, str);
    // return;
    // }
    // }
    // if (EvalEngine.get().getContextPath().contains(context)) {
    // append(buf, symbol.getSymbolName());
    // } else {
    // append(buf, context.completeContextName() + symbol.getSymbolName());
    // }
  }

  public void convertPattern(final Appendable buf, final IPatternObject pattern)
      throws IOException {
    append(buf, pattern.toString());
  }

  public void convertHead(final Appendable buf, final IExpr obj) throws IOException {
    convert(buf, obj, Integer.MIN_VALUE, false);
  }

  private void convertPlusOperator(final Appendable buf, final IAST plusAST,
      final InfixOperator oper, final int precedence) throws IOException {
    int operPrecedence = oper.getPrecedence();
    if (operPrecedence < precedence) {
      append(buf, "(");
    }

    IExpr plusArg;
    int size = plusAST.size();
    if (size > 0) {
      convertPlusArgument(buf, plusAST.arg1(), NO_PLUS_CALL);
      for (int i = 2; i < size; i++) {
        plusArg = plusAST.get(i);
        convertPlusArgument(buf, plusArg, PLUS_CALL);
      }
    }
    if (operPrecedence < precedence) {
      append(buf, ")");
    }
  }

  public void convertPlusArgument(final Appendable buf, IExpr plusArg, boolean caller)
      throws IOException {
    if (plusArg.isTimes()) {
      final IAST timesAST = (IAST) plusArg;
      // IExpr arg1 = timesAST.arg1();
      final InfixOperator TIMES_OPERATOR =
          (InfixOperator) ASTNodeFactory.MMA_STYLE_FACTORY.get("Times");
      convertTimesFraction(buf, timesAST, TIMES_OPERATOR, Precedence.TIMES, caller);
    } else {
      if (plusArg.isNegativeSigned()) {
        // special case negative number or -Infinity...
        convert(buf, plusArg, Integer.MIN_VALUE, false);
      } else {
        if (caller == PLUS_CALL) {
          append(buf, fInputForm ? " + " : "+");
        }
        convert(buf, plusArg, Precedence.PLUS, false);
      }
    }
  }

  private void convertTimesFraction(final Appendable buf, final IAST timesAST,
      final InfixOperator oper, final int precedence, boolean caller) throws IOException {
    Optional<IExpr[]> parts =
        AlgebraUtil.fractionalPartsTimesPower(timesAST, true, false, false, false, false, false);
    if (parts.isEmpty()) {
      convertTimesOperator(buf, timesAST, oper, precedence, caller);
      return;
    }
    final IExpr numerator = parts.get()[0];
    final IExpr denominator = parts.get()[1];
    if (!denominator.isOne()) {
      int currPrecedence = oper.getPrecedence();
      if (currPrecedence < precedence) {
        append(buf, "(");
      }
      final IExpr fraction = parts.get()[2];
      if (fraction != null) {
        convertNumber(buf, (IReal) fraction, Precedence.PLUS, caller);
        append(buf, "*");
        caller = NO_PLUS_CALL;
      }
      if (numerator.isReal()) {
        convertNumber(buf, (IReal) numerator, Precedence.PLUS, caller);
      } else if (numerator.isComplex() || numerator.isComplexNumeric()) {
        convertNumber(buf, (INumber) numerator, Precedence.DIVIDE, caller);
      } else {
        if (numerator.isTimes() && numerator.isAST2() && numerator.first().isMinusOne()) {
          append(buf, fInputForm ? " - " : "-");
          convert(buf, numerator.second(), Precedence.TIMES, false);
        } else {
          if (caller == PLUS_CALL) {
            append(buf, fInputForm ? " + " : "+");
          }
          // insert numerator in buffer:
          if (numerator.isTimes()) {
            convertTimesOperator(buf, (IAST) numerator, oper, Precedence.DIVIDE, NO_PLUS_CALL);
          } else {
            convert(buf, numerator, Precedence.DIVIDE, false);
          }
        }
      }
      append(buf, "/");
      // insert denominator in buffer:
      if (denominator.isTimes()) {
        convertTimesOperator(buf, (IAST) denominator, oper, Precedence.DIVIDE, NO_PLUS_CALL);
      } else {
        convert(buf, denominator, Precedence.DIVIDE, false);
      }
      if (currPrecedence < precedence) {
        append(buf, ")");
      }
      return;
    }
    convertTimesOperator(buf, timesAST, oper, precedence, caller);
  }

  private void convertTimesOperator(final Appendable buf, final IAST timesAST,
      final InfixOperator oper, final int precedence, boolean caller) throws IOException {
    int size = timesAST.size();
    boolean showOperator = true;
    int currPrecedence = oper.getPrecedence();
    if (currPrecedence < precedence) {
      append(buf, "(");
    }

    if (size > 1) {
      IExpr arg1 = timesAST.arg1();
      if (arg1.isReal() && size > 2 && !timesAST.arg2().isNumber()) {
        if (arg1.isMinusOne()) {
          append(buf, fInputForm && (caller == PLUS_CALL) ? " - " : "-");
          showOperator = false;
        } else {
          convertNumber(buf, (IReal) arg1, Precedence.PLUS, caller);
        }
      } else if (arg1.isComplex() && size > 2) {
        convertComplex(buf, (IComplex) arg1, oper.getPrecedence(), caller);
      } else {
        if (caller == PLUS_CALL) {
          append(buf, fInputForm ? " + " : "+");
        }
        convert(buf, arg1, oper.getPrecedence(), false);
      }
    }
    for (int i = 2; i < size; i++) {
      if (showOperator) {
        append(buf, oper.getOperatorString());
      } else {
        showOperator = true;
      }
      convert(buf, timesAST.get(i), oper.getPrecedence(), false);
    }
    if (currPrecedence < precedence) {
      append(buf, ")");
    }
  }

  // public void convertApplyOperator(final Appendable buf, final IAST list, final InfixOperator
  // oper,
  // final int precedence) throws IOException {
  // IExpr arg2 = list.arg2();
  // if (arg2.isNumber()) {
  // INumber exp = (INumber) arg2;
  // if (exp.complexSign() < 0) {
  // if (ASTNodeFactory.APPLY_PRECEDENCE < precedence) {
  // append(buf, "(");
  // }
  // append(buf, "1/");
  // if (exp.isMinusOne()) {
  // convert(buf, list.arg1(), ASTNodeFactory.DIVIDE_PRECEDENCE, false);
  // if (ASTNodeFactory.DIVIDE_PRECEDENCE < precedence) {
  // append(buf, ")");
  // }
  // return;
  // }
  // // flip presign of the exponent
  // IAST pow = list.setAtCopy(2, exp.opposite());
  // convertPowerOperator(buf, pow, oper, ASTNodeFactory.DIVIDE_PRECEDENCE);
  // if (ASTNodeFactory.APPLY_PRECEDENCE < precedence) {
  // append(buf, ")");
  // }
  // return;
  // }
  // }
  // convertInfixOperator(buf, list, oper, precedence);
  // }

  public void convertPowerOperator(final Appendable buf, final IAST list, final InfixOperator oper,
      final int precedence) throws IOException {
    IExpr arg2 = list.arg2();
    if (arg2.isNumber()) {
      INumber exp = (INumber) arg2;
      if (exp.isNumEqualRational(F.C1D2)) {
        append(buf, "Sqrt");
        if (fRelaxedSyntax) {
          append(buf, "(");
        } else {
          append(buf, "[");
        }
        convert(buf, list.arg1(), Precedence.NO_PRECEDENCE, false);
        if (fRelaxedSyntax) {
          append(buf, ")");
        } else {
          append(buf, "]");
        }
        return;
      }
      if (exp.complexSign() < 0) {
        if (Precedence.DIVIDE < precedence) {
          append(buf, "(");
        }
        append(buf, "1/");
        if (exp.isMinusOne()) {
          convert(buf, list.arg1(), Precedence.DIVIDE, false);
          if (Precedence.DIVIDE < precedence) {
            append(buf, ")");
          }
          return;
        }
        // flip presign of the exponent
        IAST pow = list.setAtCopy(2, exp.opposite());
        convertPowerOperator(buf, pow, oper, Precedence.DIVIDE);
        if (Precedence.DIVIDE < precedence) {
          append(buf, ")");
        }
        return;
      }
    }
    convertInfixOperator(buf, list, oper, precedence);
  }

  public void convertInfixOperator(final Appendable buf, final IAST list, final InfixOperator oper,
      final int precedence) throws IOException {
    final boolean isOr = list.isOr();
    String operatorString = oper.getOperatorString();
    if (list.isAST2()) {
      IExpr arg1 = list.arg1();
      IExpr arg2 = list.arg2();
      if (oper.getPrecedence() < precedence) {
        append(buf, "(");
      }
      if (oper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE
          && arg1.head().equals(list.head())) {
        append(buf, "(");
      } else {
        if (operatorString.equals("^")) {
          final Operator operator = getOperator(arg1.topHead(), arg1.argSize());
          if (operator instanceof PostfixOperator) {
            append(buf, "(");
          }
        }
      }
      if (isOr && arg1.isAnd()) {
        append(buf, "(");
      }
      convert(buf, arg1, oper.getPrecedence(), false);
      if (isOr && arg1.isAnd()) {
        append(buf, ")");
      }
      if (oper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE
          && arg1.head().equals(list.head())) {
        append(buf, ")");
      } else {
        if (operatorString.equals("^")) {
          final Operator operator = getOperator(arg1.topHead(), arg1.argSize());
          if (operator instanceof PostfixOperator) {
            append(buf, ")");
          }
        }
      }

      appendUnicodeMapped(buf, operatorString);

      if (oper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE && arg2.head().equals(list.head())) {
        append(buf, "(");
      }
      if (isOr && arg2.isAnd()) {
        append(buf, "(");
      }
      convert(buf, arg2, oper.getPrecedence(), false);
      if (isOr && arg2.isAnd()) {
        append(buf, ")");
      }
      if (oper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE && arg2.head().equals(list.head())) {
        append(buf, ")");
      }

      if (oper.getPrecedence() < precedence) {
        append(buf, ")");
      }
      return;
    }

    if (oper.getPrecedence() < precedence) {
      append(buf, "(");
    }
    if (list.size() > 1) {
      if (isOr && list.arg1().isAnd()) {
        append(buf, "(");
      }
      convert(buf, list.arg1(), oper.getPrecedence(), false);
      if (isOr && list.arg1().isAnd()) {
        append(buf, ")");
      }
    }

    for (int i = 2; i < list.size(); i++) {
      appendUnicodeMapped(buf, operatorString);
      if (isOr && list.get(i).isAnd()) {
        append(buf, "(");
      }
      convert(buf, list.get(i), oper.getPrecedence(), false);
      if (isOr && list.get(i).isAnd()) {
        append(buf, ")");
      }
    }
    if (oper.getPrecedence() < precedence) {
      append(buf, ")");
    }
  }

  private void appendUnicodeMapped(final Appendable buf, String operatorString) throws IOException {
    operatorString = Characters.mapWLUnicodeToEquivalent(operatorString);
    append(buf, operatorString);
  }

  public void convertPrefixOperator(final Appendable buf, final IAST list,
      final PrefixOperator oper, final int precedence) throws IOException {
    if (oper.getPrecedence() <= precedence) {
      append(buf, "(");
    }
    append(buf, oper.getOperatorString());
    convert(buf, list.arg1(), oper.getPrecedence(), false);
    if (oper.getPrecedence() <= precedence) {
      append(buf, ")");
    }
  }

  public void convertPostfixOperator(final Appendable buf, final IAST list,
      final PostfixOperator oper, final int precedence) throws IOException {
    if (oper.getPrecedence() <= precedence) {
      append(buf, "(");
    }
    convert(buf, list.arg1(), oper.getPrecedence(), false);
    append(buf, oper.getOperatorString());
    if (oper.getPrecedence() <= precedence) {
      append(buf, ")");
    }
  }

  public String toString(final IExpr o, boolean useSignificantFigures) {
    reset(useSignificantFigures);
    StringBuilder buf = new StringBuilder();
    try {
      convert(buf, o, Integer.MIN_VALUE, false);
    } catch (IOException e) {
      LOGGER.debug("OutputFormFactory.toString() failed", e);
    }
    return buf.toString();
  }

  public String toString(final IExpr o) {
    reset(false);
    StringBuilder buf = new StringBuilder();
    try {
      convert(buf, o, Integer.MIN_VALUE, false);
    } catch (IOException e) {
      LOGGER.debug("OutputFormFactory.toString() failed", e);
    }
    return buf.toString();
  }

  public boolean convert(final Appendable buf, final IExpr o) {
    try {
      convert(buf, o, Integer.MIN_VALUE, false);
      if (buf instanceof CharSequence) {
        if (((CharSequence) buf).length() >= Config.MAX_OUTPUT_SIZE) {
          return false;
        }
      }
      return true;
    } catch (IOException | RuntimeException | OutOfMemoryError rex) {
      // rex.printStackTrace();
      LOGGER.debug("OutputFormFactory.convert() failed", rex);
    }
    return false;
  }

  private void convertNumber(final Appendable buf, final INumber o, final int precedence,
      boolean caller) throws IOException {
    if (o instanceof INum) {
      convertDouble(buf, (INum) o, precedence, caller);
    } else if (o instanceof IComplexNum) {
      convertDoubleComplex(buf, (IComplexNum) o, precedence, caller);
    } else if (o instanceof IInteger) {
      convertInteger(buf, (IInteger) o, precedence, caller);
    } else if (o instanceof IFraction) {
      convertFraction(buf, (IFraction) o, precedence, caller);
    } else if (o instanceof IComplex) {
      convertComplex(buf, (IComplex) o, precedence, caller);
    } else {
      LOGGER.error("OutputFormFactory.convertNumber() failed");
    }
  }

  private void convert(final Appendable buf, final IExpr o, final int precedence, boolean isASTHead)
      throws IOException {
    if (o instanceof IAST) {
      final IAST list = (IAST) o;
      if (list.isNIL()) {
        append(buf, "NIL");
        return;
      }
      if (o.isDataset()) {
        // TODO improve output
        buf.append(o.toString());
        return;
      } else if (o.isAssociation()) {
        convertAssociation(buf, (IAssociation) o);
        return;
      } else if (o.isAST(S.Association, 1)) {
        buf.append("<||>");
        return;
      }

      IExpr header = list.head();
      if (!header.isSymbol()) {
        // print expressions like: f(#1, y)& [x]

        IAST[] derivStruct = list.isDerivativeAST1();
        if (derivStruct != null) {
          IAST a1Head = derivStruct[0];
          IAST headAST = derivStruct[1];
          if (a1Head.isAST1() && a1Head.arg1().isInteger() && headAST.isAST1()
              && (headAST.arg1().isSymbol() || headAST.arg1().isAST()) && derivStruct[2] != null) {
            try {
              int n = ((IInteger) a1Head.arg1()).toInt();
              if (n == 1 || n == 2) {
                IExpr symbolOrAST = headAST.arg1();
                convert(buf, symbolOrAST, Integer.MIN_VALUE, false);
                if (n == 1) {
                  append(buf, "'");
                } else if (n == 2) {
                  append(buf, "''");
                }
                convertArgs(buf, symbolOrAST, list);
                return;
              }
            } catch (ArithmeticException ae) {

            }
          }
        }

        convert(buf, header, Integer.MIN_VALUE, true);
        // avoid fast StackOverflow
        append(buf, "[");
        for (int i = 1; i < list.size(); i++) {
          convert(buf, list.get(i), Integer.MIN_VALUE, false);
          if (i < list.argSize()) {
            append(buf, ",");
          }
        }
        append(buf, "]");
        return;
      }
      if (header.isSymbol()) {
        ISymbol head = (ISymbol) header;
        int functionID = head.ordinal();
        if (functionID > ID.UNKNOWN) {
          switch (functionID) {
            case ID.TwoWayRule:
            case ID.UndirectedEdge:
              if (list.isAST2()) {
                convert(buf, list.arg1(), Integer.MIN_VALUE, false);
                buf.append("<->");
                convert(buf, list.arg2(), Integer.MIN_VALUE, false);
                return;
              }
              break;
            case ID.DirectedEdge:
              if (list.isAST2()) {
                convert(buf, list.arg1(), Integer.MIN_VALUE, false);
                buf.append("->");
                convert(buf, list.arg2(), Integer.MIN_VALUE, false);
                return;
              }
              break;
          }
        }

        final Operator operator = getOperator(head, list.argSize());
        if (operator != null) {
          if (operator instanceof PostfixOperator) {
            if (list.isAST1()) {
              convertPostfixOperator(buf, list, (PostfixOperator) operator, precedence);
              return;
            }
          } else {
            if (convertOperator(operator, list, buf, isASTHead ? Integer.MAX_VALUE : precedence,
                head)) {
              return;
            }
          }
        }

        if (functionID > ID.UNKNOWN) {
          switch (functionID) {
            case ID.Inequality:
              if (list.size() > 3 && convertInequality(buf, list, precedence)) {
                return;
              }
              break;
            case ID.Quantity:
              // if (head.equals(F.SeriesData) && (list.size() == 7)) {
              if (list instanceof IQuantity) {
                if (convertQuantityData(buf, (IQuantity) list, precedence)) {
                  return;
                }
              }
              break;
            case ID.SeriesData:
              // if (head.equals(F.SeriesData) && (list.size() == 7)) {
              if (list instanceof ASTSeriesData) {
                if (convertSeriesData(buf, (ASTSeriesData) list, precedence)) {
                  return;
                }
              }
              break;
            case ID.SparseArray:
              if (list.isSparseArray()) {
                buf.append(list.toString());
                return;
              }
              break;
            case ID.Parenthesis:
              convertArgs(buf, S.Parenthesis, list);
              return;
            case ID.List:
              convertList(buf, list, false);
              return;
            case ID.MatrixForm:
              if (list.isASTOrAssociation() && list.size() > 1) {
                // see also MatrixForm in MathML or TeX format for "graphical representation".
                IExpr normal = list.arg1().normal(false);
                if (normal.isList()) { // && normal.isMatrix() != null) {
                  IntList dims = LinearAlgebra.dimensions((IAST) normal, S.List);
                  convertList(buf, (IAST) normal, dims.size() >= 2);
                  return;
                }
                convert(buf, normal, Integer.MIN_VALUE, false);
                return;
              }
              break;
            case ID.Out:
              if (list.isAST1() && list.arg1().isInteger()) {
                int lineNumber = list.arg1().toIntDefault();
                if (lineNumber == -1) {
                  buf.append("%");
                  return;
                } else if (lineNumber == -2) {
                  buf.append("%%");
                  return;
                }
              }
              break;
            case ID.Part:
              if (list.size() >= 3) {
                convertPart(buf, list);
                return;
              }
              break;
            case ID.PrecedenceForm:
              if (list.size() == 3) {
                convertPrecedenceForm(buf, list, precedence);
                return;
              }
              break;
            case ID.Slot:
              if (list.isAST1() && list.arg1().isInteger()) {
                convertSlot(buf, list);
                return;
              }
              break;
            case ID.SlotSequence:
              if (list.isAST1() && list.arg1().isInteger()) {
                convertSlotSequence(buf, list);
                return;
              }
              break;
            case ID.Defer:
            case ID.HoldForm:
              if (list.isAST1()) {
                convert(buf, list.arg1(), Integer.MIN_VALUE, false);
                return;
              }
              break;
            case ID.DirectedInfinity:
              if (list.isDirectedInfinity()) { // head.equals(F.DirectedInfinity))
                if (list.isAST0()) {
                  append(buf, "ComplexInfinity");
                  return;
                }
                if (list.isAST1()) {
                  IExpr arg1 = list.arg1();
                  if (arg1.isOne()) {
                    append(buf, "Infinity");
                    return;
                  } else if (arg1.isMinusOne()) {
                    if (Precedence.PLUS < precedence) {
                      append(buf, "(");
                    }
                    append(buf, "-Infinity");
                    if (Precedence.PLUS < precedence) {
                      append(buf, ")");
                    }
                    return;
                  } else if (arg1.isImaginaryUnit()) {
                    append(buf, "I*Infinity");
                    return;
                  } else if (arg1.isNegativeImaginaryUnit()) {
                    append(buf, "-I*Infinity");
                    return;
                  }
                }
              }
              break;
            case ID.Optional:
              if (list.isAST2() && (list.arg1().isBlank() || list.arg1().isPattern())) {
                convert(buf, list.arg1(), Integer.MIN_VALUE, false);
                buf.append(":");
                convert(buf, list.arg2(), Integer.MIN_VALUE, false);
                return;
              }
              break;
            case ID.Complex:
              if (list.isAST2()) {
                // used for visual comparison of steps
                boolean isZeroRealPart = list.arg1().isZero();
                final int prec = isZeroRealPart ? Precedence.TIMES : Precedence.PLUS;
                if (prec < precedence) {
                  append(buf, "(");
                }
                if (isZeroRealPart) {
                  buf.append("I*");
                  convert(buf, list.arg2(), Precedence.TIMES, false);
                } else {
                  convert(buf, list.arg1(), Precedence.PLUS, false);
                  buf.append("+I*");
                  convert(buf, list.arg2(), Precedence.TIMES, false);
                }
                if (prec < precedence) {
                  append(buf, ")");
                }
                return;
              }
              break;
            case ID.Rational:
              if (list.isAST2()) {
                // used for visual comparison of steps
                IExpr numerator = list.arg1();
                final boolean isNegative = numerator.isNegative();
                final int prec = isNegative ? Precedence.PLUS : Precedence.TIMES;
                if (prec < precedence) {
                  append(buf, "(");
                }
                convert(buf, list.arg1(), Precedence.DIVIDE, false);
                buf.append("/");
                convert(buf, list.arg2(), Precedence.DIVIDE, false);
                if (prec < precedence) {
                  append(buf, ")");
                }
                return;
              }
              break;
          }
        } else {
          if (list instanceof ASTRealVector || list instanceof ASTRealMatrix) {
            convertList(buf, list, false);
            return;
          }
        }
      }

      convertAST(buf, list);
    } else if (o instanceof IReal) {
      convertNumber(buf, (IReal) o, precedence, NO_PLUS_CALL);
    } else if (o instanceof IComplexNum) {
      convertDoubleComplex(buf, (IComplexNum) o, precedence, NO_PLUS_CALL);
    } else if (o instanceof IComplex) {
      convertComplex(buf, (IComplex) o, precedence, NO_PLUS_CALL);
    } else if (o instanceof ISymbol) {
      convertSymbol(buf, (ISymbol) o);
    } else if (o instanceof IPatternObject) {
      convertPattern(buf, (IPatternObject) o);
    } else {
      // includes (o instanceof IStringX)
      convertString(buf, o.toString());
    }
  }

  private void convertAssociation(final Appendable buf, final IAssociation association)
      throws IOException {
    append(buf, "<|");
    final int size = association.size();
    if (size > 1) {
      convert(buf, association.getRule(1), Integer.MIN_VALUE, false);
    }
    for (int i = 2; i < size; i++) {
      append(buf, ",");
      convert(buf, association.getRule(i), Integer.MIN_VALUE, false);
    }
    append(buf, "|>");
  }

  private boolean convertInequality(final Appendable buf, final IAST inequality,
      final int precedence) throws IOException {
    StringBuilder tempBuffer = new StringBuilder();
    if (Precedence.EQUAL < precedence) {
      append(tempBuffer, "(");
    }
    final int listSize = inequality.size();
    int i = 1;
    while (i < listSize) {
      convert(tempBuffer, inequality.get(i++), Integer.MIN_VALUE, false);
      if (i == listSize) {
        if (Precedence.EQUAL < precedence) {
          append(tempBuffer, ")");
        }
        buf.append(tempBuffer);
        return true;
      }
      IExpr head = inequality.get(i++);
      if (head.isBuiltInSymbol()) {
        int id = ((IBuiltInSymbol) head).ordinal();
        switch (id) {
          case ID.Equal:
            tempBuffer.append("==");
            break;
          case ID.Greater:
            tempBuffer.append(">");
            break;
          case ID.GreaterEqual:
            tempBuffer.append(">=");
            break;
          case ID.Less:
            tempBuffer.append("<");
            break;
          case ID.LessEqual:
            tempBuffer.append("<=");
            break;
          case ID.Unequal:
            tempBuffer.append("!=");
            break;
          default:
            return false;
        }
      } else {
        return false;
      }
    }
    if (Precedence.EQUAL < precedence) {
      append(tempBuffer, ")");
    }
    buf.append(tempBuffer);
    return true;
  }

  private boolean convertOperator(final Operator operator, final IAST list, final Appendable buf,
      final int precedence, ISymbol head) throws IOException {
    if ((operator instanceof PrefixOperator) && (list.isAST1())) {
      convertPrefixOperator(buf, list, (PrefixOperator) operator, precedence);
      return true;
    }
    if ((operator instanceof InfixOperator) && (list.size() > 2)) {
      InfixOperator infixOperator = (InfixOperator) operator;
      if (head.equals(S.Plus)) {
        IAST plusAST = list;
        if (fPlusReversed) {
          plusAST = plusAST.reverse(F.NIL);
        }
        convertPlusOperator(buf, plusAST, infixOperator, precedence);
        return true;
      } else if (head.equals(S.Times)) {
        convertTimesFraction(buf, list, infixOperator, precedence, NO_PLUS_CALL);
        return true;
      } else if (list.isPower()) {
        convertPowerOperator(buf, list, infixOperator, precedence);
        return true;
      } else if (list.isAST(S.Apply)) {
        if (list.size() == 3) {
          convertInfixOperator(buf, list, ASTNodeFactory.APPLY_OPERATOR, precedence);
          return true;
        }
        if (list.size() == 4 && list.arg2().equals(F.CListC1)) {
          convertInfixOperator(buf, list, ASTNodeFactory.MAPAPPLY_OPERATOR, precedence);
          return true;
        }
        return false;
      } else if (list.size() != 3 && infixOperator.getGrouping() != InfixOperator.NONE) {
        return false;
      }
      convertInfixOperator(buf, list, (InfixOperator) operator, precedence);
      return true;
    }
    if ((operator instanceof PostfixOperator) && (list.isAST1())) {
      convertPostfixOperator(buf, list, (PostfixOperator) operator, precedence);
      return true;
    }
    return false;
  }

  public static Operator getOperator(ISymbol head, int argSize) {
    String headerStr = head.getSymbolName();
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS && head.getContext().equals(Context.SYSTEM)) {
      String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(headerStr);
      if (str != null) {
        headerStr = str;
      }
    }
    Operator op = ASTNodeFactory.MMA_STYLE_FACTORY.get(headerStr);
    if (op != null && argSize > 1 && !(op instanceof InfixOperator)) {
      List<Operator> operatorList =
          ASTNodeFactory.MMA_STYLE_FACTORY.getOperatorList(op.getOperatorString());
      if (operatorList != null) {
        for (int i = 0; i < operatorList.size(); i++) {
          final Operator operator = operatorList.get(i);
          if (operator instanceof InfixOperator) {
            return operator;
          }
        }
      }
    }
    return op;
  }

  public void convertSlot(final Appendable buf, final IAST list) throws IOException {
    try {
      final int slot = ((IReal) list.arg1()).toInt();
      append(buf, "#" + slot);
    } catch (final ArithmeticException e) {
      // add message to evaluation problemReporter
    }
  }

  public void convertSlotSequence(final Appendable buf, final IAST list) throws IOException {
    try {
      final int slotSequenceStartPosition = ((IReal) list.arg1()).toInt();
      append(buf, "##" + slotSequenceStartPosition);
    } catch (final ArithmeticException e) {
      // add message to evaluation problemReporter
    }
  }

  public void convertList(final Appendable buf, final IAST list, boolean isMatrix)
      throws IOException {
    if (list instanceof ASTRealVector) {
      try {
        RealVector vector = ((ASTRealVector) list).getRealVector();
        buf.append('{');
        int size = vector.getDimension();
        for (int i = 0; i < size; i++) {
          convertDouble(buf, vector.getEntry(i));
          if (i < size - 1) {
            buf.append(",");
          }
        }
        buf.append('}');
      } catch (IOException e) {
        LOGGER.debug("OutputFormFactory.convertList() failed", e);
      }
      return;
    }
    if (list instanceof ASTRealMatrix) {
      try {
        RealMatrix matrix = ((ASTRealMatrix) list).getRealMatrix();
        buf.append('{');

        int rows = matrix.getRowDimension();
        int cols = matrix.getColumnDimension();
        for (int i = 0; i < rows; i++) {
          if (i != 0) {
            buf.append(" ");
          }
          buf.append("{");
          for (int j = 0; j < cols; j++) {
            convertDouble(buf, matrix.getEntry(i, j));
            if (j < cols - 1) {
              buf.append(",");
            }
          }
          buf.append('}');
          if (i < rows - 1) {
            buf.append(",");
            buf.append('\n');
          }
        }
        buf.append('}');
      } catch (IOException e) {
        LOGGER.debug("OutputFormFactory.convertList() failed", e);
      }
      return;
    }
    if (list.isEvalFlagOn(IAST.IS_MATRIX) || isMatrix) {
      if (!fEmpty) {
        newLine(buf);
      }
    }
    append(buf, "{");
    final int listSize = list.size();
    if (listSize > 1) {
      convert(buf, list.arg1(), Integer.MIN_VALUE, false);
    }
    for (int i = 2; i < listSize; i++) {
      append(buf, ",");
      if (list.isEvalFlagOn(IAST.IS_MATRIX) || isMatrix) {
        newLine(buf);
        append(buf, ' ');
      }
      convert(buf, list.get(i), Integer.MIN_VALUE, false);
    }
    append(buf, "}");
  }

  /**
   * This method will only be called if <code>list.isAST2()==true</code> and the head equals "Part".
   *
   * @param buf
   * @param list
   * @throws IOException
   */
  public void convertPart(final Appendable buf, final IAST list) throws IOException {
    IExpr arg1 = list.arg1();

    boolean parentheses = false;
    if (arg1.isASTOrAssociation()) {
      final Operator operator = getOperator(arg1.topHead(), arg1.argSize());
      if (operator != null) {
        parentheses = true;
      }
    } else if (!arg1.isSymbol()) {
      parentheses = true;
    }
    if (parentheses) {
      append(buf, "(");
    }
    convert(buf, arg1, Integer.MIN_VALUE, false);
    if (parentheses) {
      append(buf, ")");
    }
    append(buf, "[[");
    for (int i = 2; i < list.size(); i++) {
      convert(buf, list.get(i), Integer.MIN_VALUE, false);
      if (i < list.argSize()) {
        append(buf, ",");
      }
    }
    append(buf, "]]");
  }

  public void convertPrecedenceForm(final Appendable buf, final IAST list, int precedence)
      throws IOException {
    final IExpr arg1 = list.arg1();
    final int formPrecedence = list.arg2().toIntDefault();
    if (formPrecedence > 0 && precedence >= formPrecedence) {
      append(buf, "(");
      convert(buf, arg1, Integer.MIN_VALUE, false);
      append(buf, ")");
    } else {
      convert(buf, arg1, precedence, false);
    }
  }

  /**
   * Convert a <code>SeriesData(...)</code> expression.
   *
   * @param buf
   * @param seriesData <code>SeriesData[x, x0, list, nmin, nmax, den]</code> expression
   * @param precedence the precedence of the parent expression
   * @return <code>true</code> if the conversion was successful
   * @throws IOException
   */
  public boolean convertSeriesData(final Appendable buf, final ASTSeriesData seriesData,
      final int precedence) throws IOException {
    if (fInputForm) {
      IAST series = seriesData.toSeriesData();
      append(buf, series.toString());
      return true;
    }
    StringBuilder tempBuffer = new StringBuilder();
    if (Precedence.PLUS < precedence) {
      append(tempBuffer, "(");
    }

    try {
      IExpr plusArg;
      // SeriesData[x, x0, list, nmin, nmax, den]
      IExpr x = seriesData.getX();
      IExpr x0 = seriesData.getX0();
      int nmin = seriesData.getNMin();
      int nmax = seriesData.getNMax();
      int order = seriesData.order();
      long den = seriesData.getDenominator();
      boolean call = NO_PLUS_CALL;
      if (nmax > nmin) {
        INumber exp = F.QQ(nmin, den).normalize();
        IExpr pow = x.subtract(x0).power(exp);
        call = convertSeriesDataArg(tempBuffer, seriesData.coefficient(nmin), pow, call);
        for (int i = nmin + 1; i < nmax; i++) {
          exp = F.QQ(i, den).normalize();
          pow = x.subtract(x0).power(exp);
          call = convertSeriesDataArg(tempBuffer, seriesData.coefficient(i), pow, call);
        }
      }
      plusArg = F.Power(F.O(x.subtract(x0)), F.QQ(order, den).normalize());
      if (!plusArg.isZero()) {
        convertPlusArgument(tempBuffer, plusArg, call);
        call = PLUS_CALL;
      }
    } catch (Exception ex) {
      Errors.rethrowsInterruptException(ex);
      return false;
    }
    if (Precedence.PLUS < precedence) {
      append(tempBuffer, ")");
    }
    buf.append(tempBuffer);
    return true;
  }

  public boolean convertQuantityData(final Appendable buf, final IQuantity quantity,
      final int precedence) throws IOException {
    StringBuilder tempBuffer = new StringBuilder();
    if (Precedence.PLUS < precedence) {
      append(tempBuffer, "(");
    }

    try {
      buf.append(quantity.toString());
    } catch (Exception ex) {
      Errors.rethrowsInterruptException(ex);
      return false;
    }
    if (Precedence.PLUS < precedence) {
      append(tempBuffer, ")");
    }
    buf.append(tempBuffer);
    return true;
  }

  /**
   * Convert a factor of a <code>SeriesData</code> object.
   *
   * @param buf
   * @param coefficient the coefficient expression of the factor
   * @param pow the power expression of the factor
   * @param call
   * @return the current call status
   * @throws IOException
   */
  private boolean convertSeriesDataArg(StringBuilder buf, IExpr coefficient, IExpr pow,
      boolean call) throws IOException {
    IExpr plusArg;
    if (coefficient.isZero()) {
      return call;
    }
    if (coefficient.isOne()) {
      if (pow.isPlus()) {
        if (call == PLUS_CALL) {
          append(buf, fInputForm ? " + " : "+");
        }
        append(buf, "(");
        convertPlusArgument(buf, pow, call);
        append(buf, ")");
        call = PLUS_CALL;
        return call;
      }
      plusArg = pow;
    } else {
      if (pow.isOne()) {
        plusArg = coefficient;
      } else {
        IASTAppendable times;
        if (coefficient.isTimes()) {
          IAST timesCoefficients = (IAST) coefficient;
          times = timesCoefficients.copyAppendable(1);
        } else {
          times = F.TimesAlloc(2);
          times.append(coefficient);
        }
        times.append(pow);
        plusArg = times;
      }
    }
    if (!plusArg.isZero()) {
      convertPlusArgument(buf, plusArg, call);
      call = PLUS_CALL;
    }
    return call;
  }

  /**
   * Write a function into the given <code>Appendable</code>.
   *
   * @param buf
   * @param function
   * @throws IOException
   */
  public void convertAST(final Appendable buf, final IAST function) throws IOException {
    IExpr head = function.head();
    convert(buf, head, Integer.MIN_VALUE, false);
    convertArgs(buf, head, function);
  }

  public void convertArgs(final Appendable buf, IExpr head, final IAST function)
      throws IOException {
    if (head.isAST()) {
      append(buf, "[");
    } else {
      append(buf, fRelaxedSyntax ? "(" : "[");
    }
    final int functionSize = function.size();
    if (functionSize > 1) {
      convert(buf, function.arg1(), Integer.MIN_VALUE, false);
    }
    for (int i = 2; i < functionSize; i++) {
      append(buf, ",");
      convert(buf, function.get(i), Integer.MIN_VALUE, false);
    }
    if (head.isAST()) {
      append(buf, "]");
    } else {
      append(buf, fRelaxedSyntax ? ")" : "]");
    }
  }

  /** this resets the columnCounter to offset 0 */
  private void newLine(Appendable buf) throws IOException {
    if (!fIgnoreNewLine) {
      append(buf, '\n');
    }
    fColumnCounter = 0;
    fEmpty = false;
  }

  protected void append(Appendable buf, String str) throws IOException {
    buf.append(str);
    fColumnCounter += str.length();
    fEmpty = false;
  }

  private void append(Appendable buf, char c) throws IOException {
    buf.append(c);
    fColumnCounter += 1;
    fEmpty = false;
  }

  /** @param ignoreNewLine The ignoreNewLine to set. */
  public void setIgnoreNewLine(final boolean ignoreNewLine) {
    fIgnoreNewLine = ignoreNewLine;
  }

  /**
   * If <code>true</code> print leading and trailing quotes in Symja strings
   *
   * @param inputForm
   */
  public void setInputForm(final boolean inputForm) {
    fInputForm = inputForm;
  }

  public void setSignificantFigures(int significantFigures) {
    fSignificantFigures = significantFigures;
  }

  public void setEmpty(final boolean empty) {
    fEmpty = empty;
  }

  /** @return Returns the columnCounter. */
  public int getColumnCounter() {
    return fColumnCounter;
  }

  public int getSignificantFigures() {
    return fSignificantFigures;
  }

  /** @param columnCounter The columnCounter to set. */
  public void setColumnCounter(final int columnCounter) {
    fColumnCounter = columnCounter;
  }
}
