package org.matheclipse.core.form.output;

import java.io.IOException;
import java.util.Optional;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
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
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.operator.ASTNodeFactory;
import org.matheclipse.parser.client.operator.InfixOperator;
import org.matheclipse.parser.client.operator.Operator;
import org.matheclipse.parser.client.operator.PostfixOperator;
import org.matheclipse.parser.client.operator.Precedence;
import org.matheclipse.parser.client.operator.PrefixOperator;

/** Converts an internal <code>IExpr</code> into a user readable string. */
public abstract class DoubleFormFactory extends AbstractCodeFormFactory {
  public DoubleFormFactory(final boolean relaxedSyntax, final boolean reversed, int exponentFigures,
      int significantFigures) {
    super(relaxedSyntax, reversed, exponentFigures, significantFigures);
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
        convertInternal(buf, plusArg, Precedence.PLUS, false, true);
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
          if (arg1.isOne()) {
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
          convertInternal(buf, arg1, Precedence.TIMES, false, true);
        }

        IExpr timesArg;
        for (int j = 2; j < timesAST.size(); j++) {
          timesArg = timesAST.get(j);

          if (showOperator) {
            append(buf, multCh);
          } else {
            showOperator = true;
          }

          convertInternal(buf, timesArg, Precedence.TIMES, false, true);
        }
      } else {
        if (plusArg.isNumber() && (((INumber) plusArg).complexSign() < 0)) {
          // special case negative number:
          convertInternal(buf, plusArg);
        } else {
          if (i < size) {
            append(buf, operatorStr);
          }

          convertInternal(buf, plusArg, Precedence.PLUS, false, true);
        }
      }
    }

    if (operPrecedence < precedence) {
      append(buf, ")");
    }
  }

  private void convertTimesFraction(final StringBuilder buf, final IAST timesAST,
      final InfixOperator oper, final int precedence, boolean caller) {
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
          append(buf, "-");
          convertInternal(buf, numerator.second(), Precedence.TIMES, false, true);
        } else {
          if (caller == PLUS_CALL) {
            append(buf, "+");
          }
          // insert numerator in buffer:
          if (numerator.isTimes()) {
            convertTimesOperator(buf, (IAST) numerator, oper, Precedence.DIVIDE, NO_PLUS_CALL);
          } else {
            convertInternal(buf, numerator, Precedence.DIVIDE, false, true);
          }
        }
      }
      append(buf, "/");
      // insert denominator in buffer:
      if (denominator.isTimes()) {
        convertTimesOperator(buf, (IAST) denominator, oper, Precedence.DIVIDE, NO_PLUS_CALL);
      } else {
        convertInternal(buf, denominator, Precedence.DIVIDE, false, true);
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
          convertNumber(buf, (IReal) arg1, Precedence.PLUS, caller);
        }
      } else if (arg1.isComplex() && timesAST.size() > 2) {
        convertComplex(buf, (IComplex) arg1, oper.getPrecedence(), caller);
      } else {
        if (caller == PLUS_CALL) {
          append(buf, "+");
        }
        convertInternal(buf, arg1, oper.getPrecedence(), false, true);
      }
    }
    for (int i = 2; i < timesAST.size(); i++) {
      if (showOperator) {
        append(buf, oper.getOperatorString());
      } else {
        showOperator = true;
      }
      convertInternal(buf, timesAST.get(i), oper.getPrecedence(), false, true);
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

  public void convert(final StringBuilder buf, final IExpr o) {
    convertInternal(buf, o, Integer.MIN_VALUE, false, true);
  }

  public void convertExpr(final StringBuilder buf, final IExpr expr) {
    convertInternal(buf, expr, Integer.MIN_VALUE, false, true);
  }

  @Override
  protected void convertInternal(final StringBuilder buf, final IExpr o, final int precedence,
      boolean isASTHead) {
    convertInternal(buf, o, precedence, isASTHead, true);
  }

  protected void convertNumber(final StringBuilder buf, final INumber o, final int precedence,
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
      double doubleValue = ((IFraction) o).evalfNaN();
      convertDouble(buf, F.num(doubleValue), precedence, caller);
      return;
    }
    if (o instanceof IComplex) {
      convertComplex(buf, (IComplex) o, precedence, caller);
    }
  }

  protected void convertInternal(final StringBuilder buf, final IExpr o, final int precedence,
      boolean isASTHead, boolean eval) {
    if (o instanceof IAST) {
      final IAST list = (IAST) o;
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
      }
      convertAST(buf, list, precedence, eval);
      return;
    }
    if (o instanceof IReal) {
      convertNumber(buf, (IReal) o, precedence, NO_PLUS_CALL);
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
      if (head == S.Plus) {
        if (fPlusReversed) {
          convertPlusOperatorReversed(buf, list, infixOperator, precedence);
        } else {
          convertPlusOperator(buf, list, infixOperator, precedence);
        }
        return true;
      } else if (head == S.Times) {
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
        if (list.size() == 4 && list.arg2().equals(F.CListC1)) {
          convertInfixOperator(head, buf, list, ASTNodeFactory.MAPAPPLY_OPERATOR, precedence);
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
      IExpr x = seriesData.expansionVariable();
      IExpr x0 = seriesData.expansionPoint();
      int nmin = seriesData.minExponent();
      int nmax = seriesData.exponentBound();
      int order = seriesData.truncateOrder();
      long den = seriesData.puiseuxDenominator();
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

  /**
   * Write a function into the given <code>StringBuilder</code>.
   *
   * @param buf
   * @param function
   * @param eval TODO
   * @throws IOException
   */
  public void convertAST(final StringBuilder buf, final IAST function, final int precedence,
      boolean eval) {
    if (function.isNumericFunction(true)) {
      try {
        INumber value = function.evalNumber();
        if (value != null) {
          convertNumber(buf, value, precedence, NO_PLUS_CALL);
          return;
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
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
    IExpr expr = S.PiecewiseExpand.of(function);
    if (function != expr) {
      convertInternal(buf, expr, Integer.MIN_VALUE, false, true);
    } else {
      convertInternal(buf, head);
      convertArgs(buf, head, function);
    }
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

  /** @return Returns the columnCounter. */
  // public int getColumnCounter() {
  // return fColumnCounter;
  // }

  /** @param columnCounter The columnCounter to set. */
  // public void setColumnCounter(final int columnCounter) {
  // fColumnCounter = columnCounter;
  // }
}
