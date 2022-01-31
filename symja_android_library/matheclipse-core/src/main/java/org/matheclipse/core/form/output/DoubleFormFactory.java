package org.matheclipse.core.form.output;

import java.io.IOException;
import java.math.BigInteger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.DoubleToMMA;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.PostfixOperator;
import org.matheclipse.parser.client.operator.Precedence;
import org.matheclipse.parser.client.operator.PrefixOperator;

/** Converts an internal <code>IExpr</code> into a user readable string. */
public abstract class DoubleFormFactory {
  /** The conversion wasn't called with an operator preceding the <code>IExpr</code> object. */
  public static final boolean NO_PLUS_CALL = false;

  /**
   * The conversion was called with a &quot;+&quot; operator preceding the <code>IExpr</code>
   * object.
   */
  public static final boolean PLUS_CALL = true;

  protected final boolean fRelaxedSyntax;
  protected final boolean fPlusReversed;
  protected boolean fIgnoreNewLine = false;
  /** If <code>true</code> print leading and trailing quotes in Symja strings */
  private boolean fQuotes = false;

  private boolean fEmpty = true;
  // private int fColumnCounter;
  private int fExponentFigures;
  private int fSignificantFigures;

  public DoubleFormFactory(final boolean relaxedSyntax, final boolean reversed, int exponentFigures,
      int significantFigures) {
    fRelaxedSyntax = relaxedSyntax;
    fPlusReversed = reversed;
    fExponentFigures = exponentFigures;
    fSignificantFigures = significantFigures;
  }

  public void reset() {
    // fColumnCounter = 0;
  }

  public void convertDouble(final StringBuilder buf, final INum d, final int precedence,
      boolean caller) {
    final double doubleValue = d.doubleValue();
    convertDouble(buf, doubleValue, d, precedence, caller);
  }

  private void convertDouble(final StringBuilder buf, final double doubleValue, final INum d,
      final int precedence, boolean caller) {
    if (F.isZero(doubleValue, Config.ZERO_IN_OUTPUT_FORMAT)) {
      convertDoubleString(buf, convertDoubleToFormattedString(0.0), precedence, false);
      return;
    }
    final boolean isNegative = d.isNegative();
    if (!isNegative && caller == PLUS_CALL) {
      append(buf, "+");
    }
    if (d instanceof Num) {
      convertDoubleString(buf, convertDoubleToFormattedString(doubleValue), precedence, isNegative);
    } else {
      convertDoubleString(buf, convertApfloat(d.apfloatValue()), precedence, isNegative);
    }
  }

  private void convertDouble(final StringBuilder buf, final double doubleValue) {
    if (F.isZero(doubleValue, Config.ZERO_IN_OUTPUT_FORMAT)) {
      convertDoubleString(buf, convertDoubleToFormattedString(0.0), 0, false);
      return;
    }
    // final boolean isNegative = doubleValue < 0.0;
    // if (!isNegative && caller == PLUS_CALL) {
    // append(buf, "+");
    // }
    convertDoubleString(buf, convertDoubleToFormattedString(doubleValue), 0, false);
  }

  protected String convertDoubleToFormattedString(double dValue) {
    if (fSignificantFigures > 0) {
      StringBuilder buf = new StringBuilder();
      DoubleToMMA.doubleToMMA(buf, dValue, fExponentFigures, fSignificantFigures);
      return buf.toString();
    }
    return Double.toString(dValue);
  }

  protected void convertDoubleString(final StringBuilder buf, final String d, final int precedence,
      final boolean isNegative) {
    if (isNegative && (Precedence.PLUS < precedence)) {
      append(buf, "(");
    }
    append(buf, d);
    if (isNegative && (Precedence.PLUS < precedence)) {
      append(buf, ")");
    }
  }

  public void convertDoubleComplex(final StringBuilder buf, final IComplexNum dc,
      final int precedence, boolean caller) {
    if (dc instanceof ApcomplexNum) {
      convertApcomplex(buf, ((ApcomplexNum) dc).apcomplexValue(), precedence, caller);
      return;
    }
    if (Precedence.PLUS < precedence) {
      if (caller == PLUS_CALL) {
        append(buf, "+");
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
        append(buf, convertDoubleToFormattedString(realPart));
        if (!imaginaryZero) {
          append(buf, "+I*");
          final boolean isNegative = imaginaryPart < 0;
          convertDoubleString(buf, convertDoubleToFormattedString(imaginaryPart), Precedence.TIMES,
              isNegative);
        }
      } else {
        if (caller == PLUS_CALL) {
          append(buf, "+");
          caller = false;
        }
        append(buf, "I*");
        final boolean isNegative = imaginaryPart < 0;
        convertDoubleString(buf, convertDoubleToFormattedString(imaginaryPart), Precedence.TIMES,
            isNegative);
      }
    }
    if (Precedence.PLUS < precedence) {
      append(buf, ")");
    }
  }

  public void convertApcomplex(final StringBuilder buf, final Apcomplex dc, final int precedence,
      boolean caller) {
    if (Precedence.PLUS < precedence) {
      if (caller == PLUS_CALL) {
        append(buf, "+");
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
        append(buf, convertApfloat(realPart));
        if (!imaginaryZero) {
          append(buf, "+I*");
          final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
          convertDoubleString(buf, convertApfloat(imaginaryPart), Precedence.TIMES, isNegative);
        }
      } else {
        if (caller == PLUS_CALL) {
          append(buf, "+");
          caller = false;
        }
        append(buf, "I*");
        final boolean isNegative = imaginaryPart.compareTo(Apcomplex.ZERO) < 0;
        convertDoubleString(buf, convertApfloat(imaginaryPart), Precedence.TIMES, isNegative);
      }
    }
    if (Precedence.PLUS < precedence) {
      append(buf, ")");
    }
  }

  public static String convertApfloat(Apfloat num) {
    String str = num.toString();
    int index = str.indexOf('e');
    if (index > 0) {
      String exponentStr = str.substring(index + 1);
      String result = str.substring(0, index);
      return result + "*10^" + exponentStr;
    }
    return str;
  }

  public void convertInteger(final StringBuilder buf, final IInteger i, final int precedence,
      boolean caller) {
    final boolean isNegative = i.isNegative();
    if (!isNegative && caller == PLUS_CALL) {
      append(buf, "+");
    }
    if (isNegative && (Precedence.PLUS < precedence)) {
      append(buf, "(");
    }
    final String str = i.toBigNumerator().toString();
    // if ((str.length() + getColumnCounter() > 80)) {
    // if (getColumnCounter() > 40) {
    // newLine(buf);
    // }
    // final int len = str.length();
    // for (int j = 0; j < len; j += 79) {
    // if (j + 79 < len) {
    // append(buf, str.substring(j, j + 79));
    // append(buf, '\\');
    // newLine(buf);
    // } else {
    // append(buf, str.substring(j, len));
    // }
    // }
    // } else {
    append(buf, str);
    // }
    if (isNegative && (Precedence.PLUS < precedence)) {
      append(buf, ")");
    }
  }

  public void convertFraction(final StringBuilder buf, final IRational f, final int precedence,
      boolean caller) {
    convertFraction(buf, f.toBigNumerator(), f.toBigDenominator(), precedence, caller);
  }

  public void convertFraction(final StringBuilder buf, final BigInteger numerator,
      BigInteger denominator, final int precedence, boolean caller) {
    boolean isInteger = denominator.compareTo(BigInteger.ONE) == 0;
    final boolean isNegative = numerator.compareTo(BigInteger.ZERO) < 0;
    final int prec = isNegative ? Precedence.PLUS : Precedence.TIMES;
    if (!isNegative) {
      if (caller == PLUS_CALL) {
        append(buf, "+");
      }
    }

    if (prec < precedence) {
      append(buf, "(");
    }

    String str = numerator.toString();
    // if ((str.length() + getColumnCounter() > 80)) {
    // if (getColumnCounter() > 40) {
    // newLine(buf);
    // }
    // final int len = str.length();
    // for (int j = 0; j < len; j += 79) {
    // if (j + 79 < len) {
    // append(buf, str.substring(j, j + 79));
    // append(buf, '\\');
    // newLine(buf);
    // } else {
    // append(buf, str.substring(j, len));
    // }
    // }
    // } else {
    append(buf, str);
    // }
    if (!isInteger) {
      append(buf, "/");
      str = denominator.toString();
      // if ((str.length() + getColumnCounter() > 80)) {
      // if (getColumnCounter() > 40) {
      // newLine(buf);
      // }
      // final int len = str.length();
      // for (int j = 0; j < len; j += 79) {
      // if (j + 79 < len) {
      // append(buf, str.substring(j, j + 79));
      // append(buf, '\\');
      // newLine(buf);
      // } else {
      // append(buf, str.substring(j, len));
      // }
      // }
      // } else {
      append(buf, str);
      // }
    }
    if (prec < precedence) {
      append(buf, ")");
    }
  }

  public void convertComplex(final StringBuilder buf, final IComplex c, final int precedence,
      boolean caller) {
    boolean isReZero = c.getRealPart().isZero();
    final boolean isImOne = c.getImaginaryPart().isOne();
    final boolean isImMinusOne = c.getImaginaryPart().isMinusOne();
    if (!isReZero && (Precedence.PLUS < precedence)) {
      if (caller == PLUS_CALL) {
        append(buf, "+");
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
          append(buf, "+");
          caller = false;
        }
        append(buf, "I");
        return;
      } else {
        append(buf, "+I");
      }
    } else if (isImMinusOne) {
      append(buf, "-I");
    } else {
      final IRational im = c.getImaginaryPart();
      // int oldColumnCounter = fColumnCounter;
      StringBuilder imagBuf = new StringBuilder();
      try {
        if (im.isNegative()) {
          if (isReZero && (Precedence.TIMES < precedence)) {
            append(buf, "(");
          }
          append(buf, "-");
          // oldColumnCounter = fColumnCounter;
          // fColumnCounter = 0;
          append(imagBuf, "I*");
          convertFraction(imagBuf, im.negate(), Precedence.TIMES, NO_PLUS_CALL);
        } else {
          if (isReZero) {
            if (caller == PLUS_CALL) {
              append(buf, "+");
            }
            if (Precedence.TIMES < precedence) {
              append(buf, "(");
            }
            // oldColumnCounter = fColumnCounter;
            // fColumnCounter = 0;
            append(imagBuf, "I*");
          } else {
            append(buf, "+");
            // oldColumnCounter = fColumnCounter;
            // fColumnCounter = 0;
            append(imagBuf, "I*");
          }
          convertFraction(imagBuf, im, Precedence.TIMES, NO_PLUS_CALL);
        }

      } finally {
        // fColumnCounter = oldColumnCounter;
      }
      String str = imagBuf.toString();
      // if ((str.length() + getColumnCounter() > 80)) {
      // newLine(buf);
      // }
      append(buf, str);
      if (isReZero && (Precedence.TIMES < precedence)) {
        append(buf, ")");
      }
    }

    if (!isReZero && (Precedence.PLUS < precedence)) {
      append(buf, ")");
    }
  }

  public void convertString(final StringBuilder buf, final String str) {
    if (fQuotes) {
      append(buf, "\"");
      append(buf, str);
      append(buf, "\"");
    } else {
      append(buf, str);
    }
  }

  public void convertSymbol(final StringBuilder buf, final ISymbol symbol) {
    if (symbol == S.E) {
      buf.append("Math.E");
      return;
    } else if (symbol == S.Pi) {
      buf.append("Math.PI");
      return;
    } else if (symbol == S.False) {
      buf.append("false");
      return;
    } else if (symbol == S.True) {
      buf.append("true");
      return;
    } else if (symbol == S.Indeterminate) {
      buf.append("Double.NaN");
      return;
    }
    if (symbol.isConstantAttribute()) {
      try {
        double value = EvalEngine.get().evalDouble(symbol);
        buf.append("(" + value + ")");
        return;
      } catch (RuntimeException rex) {
        //
      }
    }
    Context context = symbol.getContext();
    if (context == Context.DUMMY) {
      append(buf, symbol.getSymbolName());
      return;
    }
    if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS && context.equals(Context.SYSTEM)) {
      String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(symbol.getSymbolName());
      if (str != null) {
        append(buf, str);
        return;
      }
    }
    if (EvalEngine.get().getContextPath().contains(context)) {
      append(buf, symbol.getSymbolName());
    } else {
      append(buf, context.completeContextName() + symbol.getSymbolName());
    }
  }

  public void convertPattern(final StringBuilder buf, final IPatternObject pattern) {
    append(buf, pattern.toString());
  }

  public void convertHead(final StringBuilder buf, final IExpr obj) {
    convertInternal(buf, obj);
  }

  private void convertPlusOperator(final StringBuilder buf, final IAST plusAST,
      final InfixOperator oper, final int precedence) {
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

  public void convertPlusArgument(final StringBuilder buf, IExpr plusArg, boolean caller) {
    if (plusArg.isTimes()) {
      final IAST timesAST = (IAST) plusArg;
      // IExpr arg1 = timesAST.arg1();
      final InfixOperator TIMES_OPERATOR =
          (InfixOperator) ASTNodeFactory.MMA_STYLE_FACTORY.get("Times");
      convertTimesFraction(buf, timesAST, TIMES_OPERATOR, Precedence.TIMES, caller);
    } else {
      if (plusArg.isNegativeSigned()) {
        // special case negative number or -Infinity...
        convertInternal(buf, plusArg);
      } else {
        if (caller == PLUS_CALL) {
          append(buf, "+");
        }
        convertInternal(buf, plusArg, Precedence.PLUS, false);
      }
    }
  }

  private void convertPlusOperatorReversed(final StringBuilder buf, final IAST plusAST,
      final InfixOperator oper, final int precedence) {
    int operPrecedence = oper.getPrecedence();
    if (operPrecedence < precedence) {
      append(buf, "(");
    }

    String operatorStr = oper.getOperatorString();

    IExpr plusArg;
    int size = plusAST.argSize();
    // print Plus[] in reverse order (i.e. numbers at last)
    for (int i = size; i > 0; i--) {
      plusArg = plusAST.get(i);

      if (plusArg.isTimes()) {
        final String multCh = ASTNodeFactory.MMA_STYLE_FACTORY.get("Times").getOperatorString();
        boolean showOperator = true;
        final IAST timesAST = (IAST) plusArg;
        IExpr arg1 = timesAST.arg1();

        if (arg1.isNumber() && (((INumber) arg1).complexSign() < 0)) {
          if (((INumber) arg1).isOne()) {
            showOperator = false;
          } else {
            if (arg1.isMinusOne()) {
              append(buf, "-");
              showOperator = false;
            } else {
              convertNumber(buf, (INumber) arg1, operPrecedence, NO_PLUS_CALL);
            }
          }
        } else {
          if (i < size) {
            append(buf, operatorStr);
          }
          convertInternal(buf, arg1, Precedence.TIMES, false);
        }

        IExpr timesArg;
        for (int j = 2; j < timesAST.size(); j++) {
          timesArg = timesAST.get(j);

          if (showOperator) {
            append(buf, multCh);
          } else {
            showOperator = true;
          }

          convertInternal(buf, timesArg, Precedence.TIMES, false);
        }
      } else {
        if (plusArg.isNumber() && (((INumber) plusArg).complexSign() < 0)) {
          // special case negative number:
          convertInternal(buf, plusArg);
        } else {
          if (i < size) {
            append(buf, operatorStr);
          }

          convertInternal(buf, plusArg, Precedence.PLUS, false);
        }
      }
    }

    if (operPrecedence < precedence) {
      append(buf, ")");
    }
  }

  private void convertTimesFraction(final StringBuilder buf, final IAST timesAST,
      final InfixOperator oper, final int precedence, boolean caller) {
    IExpr[] parts =
        Algebra.fractionalPartsTimesPower(timesAST, true, false, false, false, false, false);
    if (parts == null) {
      convertTimesOperator(buf, timesAST, oper, precedence, caller);
      return;
    }
    final IExpr numerator = parts[0];
    final IExpr denominator = parts[1];
    if (!denominator.isOne()) {
      int currPrecedence = oper.getPrecedence();
      if (currPrecedence < precedence) {
        append(buf, "(");
      }
      final IExpr fraction = parts[2];
      if (fraction != null) {
        convertNumber(buf, (ISignedNumber) fraction, Precedence.PLUS, caller);
        append(buf, "*");
        caller = NO_PLUS_CALL;
      }
      if (numerator.isReal()) {
        convertNumber(buf, (ISignedNumber) numerator, Precedence.PLUS, caller);
      } else if (numerator.isComplex() || numerator.isComplexNumeric()) {
        convertNumber(buf, (INumber) numerator, Precedence.DIVIDE, caller);
      } else {
        if (numerator.isTimes() && numerator.isAST2() && numerator.first().isMinusOne()) {
          append(buf, "-");
          convertInternal(buf, numerator.second(), Precedence.TIMES, false);
        } else {
          if (caller == PLUS_CALL) {
            append(buf, "+");
          }
          // insert numerator in buffer:
          if (numerator.isTimes()) {
            convertTimesOperator(buf, (IAST) numerator, oper, Precedence.DIVIDE, NO_PLUS_CALL);
          } else {
            convertInternal(buf, numerator, Precedence.DIVIDE, false);
          }
        }
      }
      append(buf, "/");
      // insert denominator in buffer:
      if (denominator.isTimes()) {
        convertTimesOperator(buf, (IAST) denominator, oper, Precedence.DIVIDE, NO_PLUS_CALL);
      } else {
        convertInternal(buf, denominator, Precedence.DIVIDE, false);
      }
      if (currPrecedence < precedence) {
        append(buf, ")");
      }
      return;
    }
    convertTimesOperator(buf, timesAST, oper, precedence, caller);
  }

  private void convertTimesOperator(final StringBuilder buf, final IAST timesAST,
      final InfixOperator oper, final int precedence, boolean caller) {
    boolean showOperator = true;
    int currPrecedence = oper.getPrecedence();
    if (currPrecedence < precedence) {
      append(buf, "(");
    }

    if (timesAST.size() > 1) {
      IExpr arg1 = timesAST.arg1();
      if (arg1.isReal() && timesAST.size() > 2 && !timesAST.arg2().isNumber()) {
        if (arg1.isMinusOne()) {
          append(buf, "-");
          showOperator = false;
        } else {
          convertNumber(buf, (ISignedNumber) arg1, Precedence.PLUS, caller);
        }
      } else if (arg1.isComplex() && timesAST.size() > 2) {
        convertComplex(buf, (IComplex) arg1, oper.getPrecedence(), caller);
      } else {
        if (caller == PLUS_CALL) {
          append(buf, "+");
        }
        convertInternal(buf, arg1, oper.getPrecedence(), false);
      }
    }
    for (int i = 2; i < timesAST.size(); i++) {
      if (showOperator) {
        append(buf, oper.getOperatorString());
      } else {
        showOperator = true;
      }
      convertInternal(buf, timesAST.get(i), oper.getPrecedence(), false);
    }
    if (currPrecedence < precedence) {
      append(buf, ")");
    }
  }

  // public void convertApplyOperator(final StringBuilder buf, final IAST list, final InfixOperator
  // oper,
  // final int precedence) {
  // IExpr arg2 = list.arg2();
  // if (arg2.isNumber()) {
  // INumber exp = (INumber) arg2;
  // if (exp.complexSign() < 0) {
  // if (ASTNodeFactory.APPLY_PRECEDENCE < precedence) {
  // append(buf, "(");
  // }
  // append(buf, "1/");
  // if (exp.isMinusOne()) {
  // convertInternal(buf, list.arg1(), ASTNodeFactory.DIVIDE_PRECEDENCE, false);
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

  public void convertPowerOperator(final StringBuilder buf, final IAST list,
      final InfixOperator oper, final int precedence) {
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
        convertInternal(buf, list.arg1(), 0, false);
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
          convertInternal(buf, list.arg1(), Precedence.DIVIDE, false);
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
    convertInfixOperator(S.Power, buf, list, oper, precedence);
  }

  public void convertInfixOperator(ISymbol head, final StringBuilder buf, final IAST list,
      final InfixOperator oper, final int precedence) {

    if (list.isAST2()) {
      if (oper.getPrecedence() < precedence) {
        append(buf, "(");
      }
      if (oper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE
          && list.arg1().head().equals(list.head())) {
        append(buf, "(");
      } else {
        if (oper.getOperatorString().equals("^")) {
          final Operator operator = getOperator(list.arg1().topHead());
          if (operator instanceof PostfixOperator) {
            append(buf, "(");
          }
        }
      }
      convertInternal(buf, list.arg1(), oper.getPrecedence(), false);
      if (oper.getGrouping() == InfixOperator.RIGHT_ASSOCIATIVE
          && list.arg1().head().equals(list.head())) {
        append(buf, ")");
      } else {
        if (oper.getOperatorString().equals("^")) {
          final Operator operator = getOperator(list.arg1().topHead());
          if (operator instanceof PostfixOperator) {
            append(buf, ")");
          }
        }
      }

      append(buf, oper.getOperatorString());

      if (oper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE
          && list.arg2().head().equals(list.head())) {
        append(buf, "(");
      }
      convertInternal(buf, list.arg2(), oper.getPrecedence(), false);
      if (oper.getGrouping() == InfixOperator.LEFT_ASSOCIATIVE
          && list.arg2().head().equals(list.head())) {
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
    if (list.size() > 3 && //
        (head.equals(S.Equal) || head.equals(S.Unequal) || head.equals(S.Greater)
            || head.equals(S.GreaterEqual) || head.equals(S.Less) || head.equals(S.LessEqual))) {
      convertInternal(buf, list.arg1(), oper.getPrecedence(), false);
      for (int i = 2; i < list.size(); i++) {
        append(buf, oper.getOperatorString());
        convertInternal(buf, list.get(i), oper.getPrecedence(), false);

        if (i < list.size() - 1) {
          buf.append(" && ");
          convertInternal(buf, list.get(i), oper.getPrecedence(), false);
        }
      }
    } else {
      if (list.size() > 1) {
        convertInternal(buf, list.arg1(), oper.getPrecedence(), false);
      }

      for (int i = 2; i < list.size(); i++) {
        append(buf, oper.getOperatorString());
        convertInternal(buf, list.get(i), oper.getPrecedence(), false);
      }
    }
    if (oper.getPrecedence() < precedence) {
      append(buf, ")");
    }
  }

  public void convertPrefixOperator(final StringBuilder buf, final IAST list,
      final PrefixOperator oper, final int precedence) {
    if (oper.getPrecedence() <= precedence) {
      append(buf, "(");
    }
    append(buf, oper.getOperatorString());
    convertInternal(buf, list.arg1(), oper.getPrecedence(), false);
    if (oper.getPrecedence() <= precedence) {
      append(buf, ")");
    }
  }

  public void convertPostfixOperator(final StringBuilder buf, final IAST list,
      final PostfixOperator oper, final int precedence) {
    if (oper.getPrecedence() <= precedence) {
      append(buf, "(");
    }
    convertInternal(buf, list.arg1(), oper.getPrecedence(), false);
    append(buf, oper.getOperatorString());
    if (oper.getPrecedence() <= precedence) {
      append(buf, ")");
    }
  }

  public String toString(final IExpr o) {
    reset();
    StringBuilder buf = new StringBuilder();
    convertInternal(buf, o, Integer.MIN_VALUE, false);
    return buf.toString();
  }

  public void convert(final StringBuilder buf, final IExpr o) {
    IExpr expr = EvalEngine.get().evaluate(F.PiecewiseExpand(o));
    convertInternal(buf, expr, Integer.MIN_VALUE, false);
  }

  protected void convertInternal(final StringBuilder buf, final IExpr o) {
    convertInternal(buf, o, Integer.MIN_VALUE, false);
  }

  private void convertNumber(final StringBuilder buf, final INumber o, final int precedence,
      boolean caller) {
    if (o instanceof INum) {
      convertDouble(buf, (INum) o, precedence, caller);
      return;
    }
    if (o instanceof IComplexNum) {
      convertDoubleComplex(buf, (IComplexNum) o, precedence, caller);
      return;
    }
    if (o instanceof IInteger) {
      convertInteger(buf, (IInteger) o, precedence, caller);
      return;
    }
    if (o instanceof IFraction) {
      convertFraction(buf, (IFraction) o, precedence, caller);
      return;
    }
    if (o instanceof IComplex) {
      convertComplex(buf, (IComplex) o, precedence, caller);
    }
  }

  private void convertInternal(final StringBuilder buf, final IExpr o, final int precedence,
      boolean isASTHead) {
    if (o instanceof IAST) {
      final IAST list = (IAST) o;
      // IExpr header = list.head();
      // if (!header.isSymbol()) {
      // // print expressions like: f(#1, y)& [x]
      //
      // IAST[] derivStruct = list.isDerivativeAST1();
      // if (derivStruct != null) {
      // IAST a1Head = derivStruct[0];
      // IAST headAST = derivStruct[1];
      // if (a1Head.isAST1() && a1Head.arg1().isInteger() && headAST.isAST1()
      // && (headAST.arg1().isSymbol() || headAST.arg1().isAST()) && derivStruct[2] != null) {
      // try {
      // int n = ((IInteger) a1Head.arg1()).toInt();
      // if (n == 1 || n == 2) {
      // IExpr symbolOrAST = headAST.arg1();
      // convertInternal(buf, symbolOrAST);
      // if (n == 1) {
      // append(buf, "'");
      // } else if (n == 2) {
      // append(buf, "''");
      // }
      // convertArgs(buf, symbolOrAST, list);
      // return;
      // }
      // } catch (ArithmeticException ae) {
      //
      // }
      // }
      // }
      //
      // convertInternal(buf, header, Integer.MIN_VALUE, true);
      // convertFunctionArgs(buf, list);
      // return;
      // }
      if (list.head().isSymbol()) {
        ISymbol head = (ISymbol) list.head();
        final Operator operator = getOperator(head);
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

        // int functionID = head.ordinal();
        // if (functionID > ID.UNKNOWN) {
        // switch (functionID) {
        // case ID.Quantity:
        // // if (head.equals(F.SeriesData) && (list.size() == 7)) {
        // if (list instanceof IQuantity) {
        // if (convertQuantityData(buf, (IQuantity) list, precedence)) {
        // return;
        // }
        // }
        // break;
        // case ID.SeriesData:
        // // if (head.equals(F.SeriesData) && (list.size() == 7)) {
        // if (list instanceof ASTSeriesData) {
        // if (convertSeriesData(buf, (ASTSeriesData) list, precedence)) {
        // return;
        // }
        // }
        // break;
        // case ID.List:
        // convertList(buf, list);
        // return;
        // case ID.Part:
        // if (list.size() >= 3) {
        // convertPart(buf, list);
        // return;
        // }
        // break;
        // case ID.Slot:
        // if (list.isAST1() && list.arg1().isInteger()) {
        // convertSlot(buf, list);
        // return;
        // }
        // break;
        // case ID.SlotSequence:
        // if (list.isAST1() && list.arg1().isInteger()) {
        // convertSlotSequence(buf, list);
        // return;
        // }
        // break;
        // case ID.Defer:
        // case ID.HoldForm:
        // if (list.isAST1()) {
        // convertInternal(buf, list.arg1());
        // return;
        // }
        // break;
        // case ID.DirectedInfinity:
        // if (list.isDirectedInfinity()) { // head.equals(F.DirectedInfinity))
        // if (list.isAST0()) {
        // append(buf, "ComplexInfinity");
        // return;
        // }
        // if (list.isAST1()) {
        // if (list.arg1().isOne()) {
        // append(buf, "Infinity");
        // return;
        // } else if (list.arg1().isMinusOne()) {
        // if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
        // append(buf, "(");
        // }
        // append(buf, "-Infinity");
        // if (ASTNodeFactory.PLUS_PRECEDENCE < precedence) {
        // append(buf, ")");
        // }
        // return;
        // } else if (list.arg1().isImaginaryUnit()) {
        // append(buf, "I*Infinity");
        // return;
        // } else if (list.arg1().isNegativeImaginaryUnit()) {
        // append(buf, "-I*Infinity");
        // return;
        // }
        // }
        // }
        // break;
        // case ID.Optional:
        // if (list.isAST2() && (list.arg1().isBlank() || list.arg1().isPattern())) {
        // convertInternal(buf, list.arg1());
        // buf.append(":");
        // convertInternal(buf, list.arg2());
        // return;
        // }
        // break;
        // }
        // } else {
        // if (list instanceof ASTRealVector || list instanceof ASTRealMatrix) {
        // convertList(buf, list);
        // return;
        // }
        // }
      }

      convertAST(buf, list);
      return;
    }
    if (o instanceof ISignedNumber) {
      convertNumber(buf, (ISignedNumber) o, precedence, NO_PLUS_CALL);
      return;
    }
    if (o instanceof IComplexNum) {
      convertDoubleComplex(buf, (IComplexNum) o, precedence, NO_PLUS_CALL);
      return;
    }
    if (o instanceof IComplex) {
      convertComplex(buf, (IComplex) o, precedence, NO_PLUS_CALL);
      return;
    }
    if (o instanceof ISymbol) {
      convertSymbol(buf, (ISymbol) o);
      return;
    }
    if (o instanceof IPatternObject) {
      convertPattern(buf, (IPatternObject) o);
      return;
    }
    convertString(buf, o.toString());
  }

  protected boolean convertOperator(final Operator operator, final IAST list,
      final StringBuilder buf, final int precedence, ISymbol head) {
    if ((operator instanceof PrefixOperator) && (list.isAST1())) {
      convertPrefixOperator(buf, list, (PrefixOperator) operator, precedence);
      return true;
    }
    if ((operator instanceof InfixOperator) && (list.size() > 2)) {
      InfixOperator infixOperator = (InfixOperator) operator;
      if (head.equals(S.Plus)) {
        if (fPlusReversed) {
          convertPlusOperatorReversed(buf, list, infixOperator, precedence);
        } else {
          convertPlusOperator(buf, list, infixOperator, precedence);
        }
        return true;
      } else if (head.equals(S.Times)) {
        convertTimesFraction(buf, list, infixOperator, precedence, NO_PLUS_CALL);
        return true;
      } else if (list.isPower()) {
        convertPowerOperator(buf, list, infixOperator, precedence);
        return true;
      } else if (list.isAST(S.Apply)) {
        if (list.size() == 3) {
          convertInfixOperator(head, buf, list, ASTNodeFactory.APPLY_OPERATOR, precedence);
          return true;
        }
        if (list.size() == 4 && list.arg2().equals(F.List(F.C1))) {
          convertInfixOperator(head, buf, list, ASTNodeFactory.APPLY_LEVEL_OPERATOR, precedence);
          return true;
        }
        return false;
      } else if (list.size() != 3 && infixOperator.getGrouping() != InfixOperator.NONE) {
        return false;
      }
      convertInfixOperator(head, buf, list, (InfixOperator) operator, precedence);
      return true;
    }
    if ((operator instanceof PostfixOperator) && (list.isAST1())) {
      convertPostfixOperator(buf, list, (PostfixOperator) operator, precedence);
      return true;
    }
    return false;
  }

  public Operator getOperator(ISymbol head) {
    // Operator operator = null;
    if (head == S.Plus || head == S.Times || head == S.Equal || head == S.Unequal || head == S.Less
        || head == S.LessEqual || head == S.Greater || head == S.GreaterEqual || head == S.And
        || head == S.Or || head == S.Not) {
      return OutputFormFactory.getOperator(head);
      // String str = head.toString();
      // operator = ASTNodeFactory.MMA_STYLE_FACTORY.get(str);
    }
    return null;
  }

  public void convertSlot(final StringBuilder buf, final IAST list) {
    try {
      final int slot = ((ISignedNumber) list.arg1()).toInt();
      append(buf, "#" + slot);
    } catch (final ArithmeticException e) {
      // add message to evaluation problemReporter
    }
  }

  public void convertSlotSequence(final StringBuilder buf, final IAST list) {
    try {
      final int slotSequenceStartPosition = ((ISignedNumber) list.arg1()).toInt();
      append(buf, "##" + slotSequenceStartPosition);
    } catch (final ArithmeticException e) {
      // add message to evaluation problemReporter
    }
  }

  public void convertList(final StringBuilder buf, final IAST list) {
    if (list instanceof ASTRealVector) {

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

      return;
    }
    if (list instanceof ASTRealMatrix) {

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

      return;
    }
    if (list.isEvalFlagOn(IAST.IS_MATRIX)) {
      if (!fEmpty) {
        newLine(buf);
      }
    }
    append(buf, "{");
    final int listSize = list.size();
    if (listSize > 1) {
      convertInternal(buf, list.arg1());
    }
    for (int i = 2; i < listSize; i++) {
      append(buf, ",");
      if (list.isEvalFlagOn(IAST.IS_MATRIX)) {
        newLine(buf);
        append(buf, ' ');
      }
      convertInternal(buf, list.get(i));
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
  public void convertPart(final StringBuilder buf, final IAST list) {
    IExpr arg1 = list.arg1();

    boolean parentheses = false;
    if (arg1.isAST()) {
      final Operator operator = getOperator(arg1.topHead());
      if (operator != null) {
        parentheses = true;
      }
    } else if (!arg1.isSymbol()) {
      parentheses = true;
    }
    if (parentheses) {
      append(buf, "(");
    }
    convertInternal(buf, arg1);
    if (parentheses) {
      append(buf, ")");
    }
    append(buf, "[[");
    for (int i = 2; i < list.size(); i++) {
      convertInternal(buf, list.get(i));
      if (i < list.argSize()) {
        append(buf, ",");
      }
    }
    append(buf, "]]");
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
  public boolean convertSeriesData(final StringBuilder buf, final ASTSeriesData seriesData,
      final int precedence) {
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
        INumber exp = F.fraction(nmin, den).normalize();
        IExpr pow = x.subtract(x0).power(exp);
        call = convertSeriesDataArg(tempBuffer, seriesData.coefficient(nmin), pow, call);
        for (int i = nmin + 1; i < nmax; i++) {
          exp = F.fraction(i, den).normalize();
          pow = x.subtract(x0).power(exp);
          call = convertSeriesDataArg(tempBuffer, seriesData.coefficient(i), pow, call);
        }
      }
      plusArg = F.Power(F.O(x.subtract(x0)), F.fraction(order, den).normalize());
      if (!plusArg.isZero()) {
        convertPlusArgument(tempBuffer, plusArg, call);
        call = PLUS_CALL;
      }
    } catch (Exception ex) {
      return false;
    }
    if (Precedence.PLUS < precedence) {
      append(tempBuffer, ")");
    }
    buf.append(tempBuffer);
    return true;
  }

  public boolean convertQuantityData(final StringBuilder buf, final IQuantity quantity,
      final int precedence) {
    StringBuilder tempBuffer = new StringBuilder();
    if (Precedence.PLUS < precedence) {
      append(tempBuffer, "(");
    }

    try {
      buf.append(quantity.toString());
    } catch (Exception ex) {
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
      boolean call) {
    IExpr plusArg;
    if (coefficient.isZero()) {
      return call;
    }
    if (coefficient.isOne()) {
      if (pow.isPlus()) {
        if (call == PLUS_CALL) {
          append(buf, "+");
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
        IASTAppendable times = F.TimesAlloc(3);
        if (coefficient.isTimes()) {
          times.appendArgs((IAST) coefficient);
        } else {
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

  public void convertFunctionArgs(final StringBuilder buf, final IAST list) {
    append(buf, "[");
    for (int i = 1; i < list.size(); i++) {
      convertInternal(buf, list.get(i));
      if (i < list.argSize()) {
        append(buf, ",");
      }
    }
    append(buf, "]");
  }

  public abstract String functionHead(ISymbol symbol);

  /**
   * Write a function into the given <code>StringBuilder</code>.
   *
   * @param buf
   * @param function
   * @throws IOException
   */
  public void convertAST(final StringBuilder buf, final IAST function) {
    if (function.isNumericFunction(true)) {
      try {
        double value = EvalEngine.get().evalDouble(function);
        buf.append("(" + value + ")");
        return;
      } catch (RuntimeException rex) {
        //
      }
    }
    IExpr head = function.head();
    if (head.isSymbol()) {
      String str = functionHead((ISymbol) head);
      if (str != null) {
        buf.append(str);
        if (function.isAST(S.ArcTan, 3)) {
          buf.append("2");
        }
        convertArgs(buf, head, function);
        return;
      }
    }
    convertInternal(buf, head);
    convertArgs(buf, head, function);
  }

  public void convertArgs(final StringBuilder buf, IExpr head, final IAST function) {
    if (head.isAST()) {
      append(buf, "[");
    } else if (fRelaxedSyntax) {
      append(buf, "(");
    } else {
      append(buf, "[");
    }
    final int functionSize = function.size();
    if (functionSize > 1) {
      convertInternal(buf, function.arg1());
    }
    for (int i = 2; i < functionSize; i++) {
      append(buf, ",");
      convertInternal(buf, function.get(i));
    }
    if (head.isAST()) {
      append(buf, "]");
    } else if (fRelaxedSyntax) {
      append(buf, ")");
    } else {
      append(buf, "]");
    }
  }

  /** this resets the columnCounter to offset 0 */
  private void newLine(StringBuilder buf) {
    // if (!fIgnoreNewLine) {
    append(buf, '\n');
    // }
    // fColumnCounter = 0;
    fEmpty = false;
  }

  private void append(StringBuilder buf, String str) {
    buf.append(str);
    // fColumnCounter += str.length();
    fEmpty = false;
  }

  private void append(StringBuilder buf, char c) {
    buf.append(c);
    // fColumnCounter += 1;
    fEmpty = false;
  }

  /** @param ignoreNewLine The ignoreNewLine to set. */
  public void setIgnoreNewLine(final boolean ignoreNewLine) {
    fIgnoreNewLine = ignoreNewLine;
  }

  /**
   * If <code>true</code> print leading and trailing quotes in Symja strings
   *
   * @param quotes
   */
  public void setQuotes(final boolean quotes) {
    fQuotes = quotes;
  }

  public void setEmpty(final boolean empty) {
    fEmpty = empty;
  }

  /** @return Returns the columnCounter. */
  // public int getColumnCounter() {
  // return fColumnCounter;
  // }

  /** @param columnCounter The columnCounter to set. */
  // public void setColumnCounter(final int columnCounter) {
  // fColumnCounter = columnCounter;
  // }
}
