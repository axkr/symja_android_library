package org.matheclipse.core.eval.util;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.parser.client.math.MathException;

/**
 * Specification for number of <a href="https://en.wikipedia.org/wiki/Set_(mathematics)">set
 * elements</a>.
 */
public class SetSpecification extends SetSpec {

  /** Create a SetSpecification with level from 0 to Integer.MAX_VALUE */
  public SetSpecification() {
    super();
    fMinCount = 0;
    fMaxCount = Integer.MAX_VALUE;
  }

  /**
   * Create a SetSpecification from an IInteger or IAST list-object.<br>
   * <br>
   * If <code>expr</code> is a non-negative IInteger iValue set Level {1,iValue};<br>
   * If <code>expr</code> is a negative IInteger iValue set Level {iValue, 0};<br>
   * If <code>expr</code> is a List {i0Value, i1Value} set Level {i0Value, i1Value};<br>
   *
   * @param expr
   * @throws MathException if the expr is not a <i>level specification</i>
   * @see
   */
  public SetSpecification(final IExpr expr) {
    super(0);
    fMinCount = fMaxCount = -1;
    if (expr instanceof IInteger) {
      final IInteger value = (IInteger) expr;

      if (value.isNegative()) {
        fMinCount = 1;
        fMaxCount = Integer.MAX_VALUE;
      } else {
        fMaxCount = value.toBigNumerator().intValue();
        fMinCount = 1;
      }
      return;
    }
    if (expr.isList()) {
      final IAST lst = (IAST) expr;

      if (lst.isAST1()) {
        if (lst.arg1() instanceof IInteger) {
          final IInteger i = (IInteger) lst.arg1();

          if (i.isNegative()) {
            fMinCount = 0;
            fMaxCount = Integer.MAX_VALUE;
          } else {
            fMaxCount = i.toBigNumerator().intValue();
            fMinCount = i.toBigNumerator().intValue();
            if (fMaxCount < fMinCount) {
              String str = Errors.getMessage("level", F.list(expr), EvalEngine.get());
              throw new ArgumentTypeException(str);
              // throw new MathException("Invalid Level specification: " + expr.toString());
            }
          }
          return;
        }
      } else {
        if ((lst.isAST2())) {
          if ((lst.arg1() instanceof IInteger) && (lst.arg2() instanceof IInteger)) {
            final IInteger i0 = (IInteger) lst.arg1();
            final IInteger i1 = (IInteger) lst.arg2();
            if (i0.isNegative() && i1.isNegative()) {
              fMinCount = 0;
              fMaxCount = Integer.MAX_VALUE;
            } else if (i0.isNegative()) {
              String str = Errors.getMessage("level", F.list(expr), EvalEngine.get());
              throw new ArgumentTypeException(str);
              // throw new MathException("Invalid Level specification: " + expr.toString());
            } else if (i1.isNegative()) {
              fMinCount = i0.toBigNumerator().intValue();
              fMaxCount = Integer.MAX_VALUE;
            } else {
              fMinCount = i0.toBigNumerator().intValue();
              fMaxCount = i1.toBigNumerator().intValue();
            }
            return;
          } else if ((lst.arg1() instanceof IInteger) && (lst.arg2().isInfinity())) {
            final IInteger i0 = (IInteger) lst.arg1();
            if (i0.isNegative()) {
              String str = Errors.getMessage("level", F.list(expr), EvalEngine.get());
              throw new ArgumentTypeException(str);
              // throw new MathException("Invalid Level specification: " + expr.toString());
            } else {
              fMinCount = i0.toBigNumerator().intValue();
              fMaxCount = Integer.MAX_VALUE;
            }
            return;
          }
        }
      }
    }
    if (expr.isInfinity()) {
      fMaxCount = Integer.MAX_VALUE;
      fMinCount = 1;
      return;
    }
    String str = Errors.getMessage("level", F.list(expr), EvalEngine.get());
    throw new ArgumentTypeException(str);
    // throw new MathException("Invalid Level specification: " + expr.toString());
  }

  /** Create a SetSpecification with only the given level */
  public SetSpecification(final int level) {
    this(level, level);
  }

  /** Create a SetSpecification with the given level range */
  public SetSpecification(final int levelFrom, final int levelTo) {
    super();
    fMinCount = levelFrom;
    fMaxCount = levelTo;
  }
}
