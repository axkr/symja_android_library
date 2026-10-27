package org.matheclipse.core.visit;

import java.util.function.ToIntFunction;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/** The level and depth bounds of a level visitor, parsed from a level specification. */
final class LevelBounds {

  int fromLevel = -1;

  int toLevel = -1;

  int fromDepth = 0;

  int toDepth = 0;

  /**
   * Parse a level specification.<br>
   * If <code>levelExpr</code> is a non-negative IInteger iValue set Level {1,iValue};<br>
   * If <code>levelExpr</code> is a negative IInteger iValue set Level {iValue, 0}; <br>
   * If <code>levelExpr</code> is a List {i0Value, i1Value} set Level {i0Value, i1Value};<br>
   *
   * @param levelExpr the evaluated level specification
   * @param startValueForAll the first level of <code>Infinity</code> and <code>All</code>
   * @param negativeToInfinity allow <code>{-n, Infinity}</code>
   * @param toInt the <code>int</code> value of a level number, which reports a number too large
   * @param engine
   * @throws ArgumentTypeException if <code>levelExpr</code> is no level specification
   */
  LevelBounds(final IExpr levelExpr, int startValueForAll, boolean negativeToInfinity,
      ToIntFunction<IInteger> toInt, final EvalEngine engine) {
    if (levelExpr instanceof IInteger) {
      final IInteger value = (IInteger) levelExpr;

      if (value.isNegative()) {
        fromDepth = Integer.MIN_VALUE;
        toDepth = toInt.applyAsInt(value);
        fromLevel = 1;
        toLevel = Integer.MAX_VALUE;
      } else {
        toLevel = toInt.applyAsInt(value);
        fromLevel = 1;
        fromDepth = Integer.MIN_VALUE;
        toDepth = -1;
      }
      return;
    }
    if (levelExpr.isList()) {
      final IAST lst = (IAST) levelExpr;

      if (lst.isAST1()) {
        if (lst.arg1() instanceof IInteger) {
          final IInteger i = (IInteger) lst.arg1();

          final int level = toInt.applyAsInt(i);
          if (i.isNegative()) {
            fromDepth = level;
            toDepth = level;
            fromLevel = 0;
            toLevel = Integer.MAX_VALUE;
          } else {
            toLevel = level;
            fromLevel = level;
            fromDepth = Integer.MIN_VALUE;
            toDepth = -1;
          }
          return;
        }
      } else {
        if ((lst.isAST2())) {
          if ((lst.arg1() instanceof IInteger) && (lst.arg2() instanceof IInteger)) {
            final IInteger i0 = (IInteger) lst.arg1();
            final IInteger i1 = (IInteger) lst.arg2();
            if (i0.isNegative() && i1.isNegative()) {
              fromDepth = toInt.applyAsInt(i0);
              toDepth = toInt.applyAsInt(i1);
              fromLevel = 0;
              toLevel = Integer.MAX_VALUE;
            } else if (i0.isNegative()) {
              // all subexpressions at levels i0 or above with a depth of -i1 or less.
              fromDepth = toInt.applyAsInt(i0);
              toDepth = -1;
              fromLevel = 0;
              toLevel = toInt.applyAsInt(i1);
            } else if (i1.isNegative()) {
              // all subexpressions at any level greater equal i0 that have a depth of -i1 or
              // greater.
              fromDepth = Integer.MIN_VALUE;
              toDepth = toInt.applyAsInt(i1);
              fromLevel = toInt.applyAsInt(i0);
              toLevel = Integer.MAX_VALUE;
            } else {
              fromDepth = Integer.MIN_VALUE;
              toDepth = -1;
              fromLevel = toInt.applyAsInt(i0);
              toLevel = toInt.applyAsInt(i1);
            }
            return;
          } else if ((lst.arg1() instanceof IInteger) && (lst.arg2().isInfinity())) {
            final IInteger i0 = (IInteger) lst.arg1();
            if (i0.isNegative()) {
              if (!negativeToInfinity) {
                // Level specification `1` is not of the form n, {n}, or {m, n}.
                String str = Errors.getMessage("level", F.list(levelExpr), engine);
                throw new ArgumentTypeException(str);
              }
              fromDepth = toInt.applyAsInt(i0);
              toDepth = -1;
              fromLevel = 0;
              toLevel = Integer.MAX_VALUE;
            } else {
              fromDepth = Integer.MIN_VALUE;
              toDepth = -1;
              fromLevel = toInt.applyAsInt(i0);
              toLevel = Integer.MAX_VALUE;
            }
            return;
          } else if ((lst.arg1().isNegativeInfinity()) && (lst.arg2().isInfinity())) {
            // level specification {-Infinity, Infinity} is effectively the same as {0,-1}
            fromDepth = Integer.MIN_VALUE;
            toDepth = -1;
            fromLevel = 0;
            toLevel = Integer.MAX_VALUE;
            return;
          }
        }
      }
    }
    if (levelExpr.isInfinity() || levelExpr == S.All) {
      // level specification Infinity and -1 are equivalent
      toLevel = Integer.MAX_VALUE;
      fromLevel = startValueForAll;
      fromDepth = Integer.MIN_VALUE;
      toDepth = -1;
      return;
    }
    // Level specification `1` is not of the form n, {n}, or {m, n}.
    String str = Errors.getMessage("level", F.list(levelExpr), engine);
    throw new ArgumentTypeException(str);
  }
}
