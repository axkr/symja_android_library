package org.matheclipse.core.visit;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public abstract class AbstractLevelVisitor extends AbstractVisitor {

  protected int fFromLevel;

  protected int fToLevel;

  protected int fFromDepth;

  protected int fToDepth;

  protected final boolean fIncludeHeads;

  protected int fCurrentLevel;

  protected int fCurrentDepth;

  /**
   * Create a LevelSpecification from an IInteger or IAST list-object.<br>
   * <br>
   * An <code>unevaledLevelExpr</code> is interpreted as a <i>level specification</i> for the
   * allowed levels in an AST.<br>
   * If <code>unevaledLevelExpr</code> is a non-negative IInteger iValue set Level {1,iValue};<br>
   * If <code>unevaledLevelExpr</code> is a negative IInteger iValue set Level {iValue, 0}; <br>
   * If <code>unevaledLevelExpr</code> is a List {i0Value, i1Value} set Level {i0Value,
   * i1Value};<br>
   * 
   * @param unevaledLevelExpr
   * @param includeHeads
   * @param startValueForAll
   * @param engine
   */
  public AbstractLevelVisitor(final IExpr unevaledLevelExpr, boolean includeHeads,
      int startValueForAll, final EvalEngine engine) {
    this.fIncludeHeads = includeHeads;
    IExpr levelExpr = engine.evaluate(unevaledLevelExpr);
    fFromLevel = fToLevel = -1;
    fFromDepth = fToDepth = 0;
    if (levelExpr instanceof IInteger) {
      final IInteger value = (IInteger) levelExpr;

      if (value.isNegative()) {
        fFromDepth = Integer.MIN_VALUE;
        fToDepth = Validate.throwIntType(value, Integer.MIN_VALUE, engine);
        fFromLevel = 1;
        fToLevel = Integer.MAX_VALUE;
      } else {
        fToLevel = Validate.throwIntType(value, Integer.MIN_VALUE, engine);
        fFromLevel = 1;
        fFromDepth = Integer.MIN_VALUE;
        fToDepth = -1;
      }
      return;
    }
    if (levelExpr.isList()) {
      final IAST lst = (IAST) levelExpr;

      if (lst.isAST1()) {
        if (lst.arg1() instanceof IInteger) {
          final IInteger i = (IInteger) lst.arg1();

          final int level = Validate.throwIntType(i, Integer.MIN_VALUE, engine);
          if (i.isNegative()) {
            fFromDepth = level;
            fToDepth = level;
            fFromLevel = 0;
            fToLevel = Integer.MAX_VALUE;
          } else {
            fToLevel = level;
            fFromLevel = level;
            fFromDepth = Integer.MIN_VALUE;
            fToDepth = -1;
          }
          return;
        }
      } else {
        if ((lst.isAST2())) {
          if ((lst.arg1() instanceof IInteger) && (lst.arg2() instanceof IInteger)) {
            final IInteger i0 = (IInteger) lst.arg1();
            final IInteger i1 = (IInteger) lst.arg2();
            if (i0.isNegative() && i1.isNegative()) {
              fFromDepth = Validate.throwIntType(i0, Integer.MIN_VALUE, engine);
              fToDepth = Validate.throwIntType(i1, Integer.MIN_VALUE, engine);
              fFromLevel = 0;
              fToLevel = Integer.MAX_VALUE;
            } else if (i0.isNegative()) {
              // all subexpressions at levels i0 or above with a depth of -i1 or less.
              fFromDepth = Validate.throwIntType(i0, Integer.MIN_VALUE, engine);
              fToDepth = -1;
              fFromLevel = 0;
              fToLevel = Validate.throwIntType(i1, Integer.MIN_VALUE, engine);
            } else if (i1.isNegative()) {
              // all subexpressions at any level greater equal i0 that have a depth of -i1 or
              // greater.
              fFromDepth = Integer.MIN_VALUE;
              fToDepth = Validate.throwIntType(i1, Integer.MIN_VALUE, engine);
              fFromLevel = Validate.throwIntType(i0, Integer.MIN_VALUE, engine);
              fToLevel = Integer.MAX_VALUE;
            } else {
              fFromDepth = Integer.MIN_VALUE;
              fToDepth = -1;
              fFromLevel = Validate.throwIntType(i0, Integer.MIN_VALUE, engine);
              fToLevel = Validate.throwIntType(i1, Integer.MIN_VALUE, engine);
            }
            return;
          } else if ((lst.arg1() instanceof IInteger) && (lst.arg2().isInfinity())) {
            final IInteger i0 = (IInteger) lst.arg1();
            if (i0.isNegative()) {
              fFromDepth = Validate.throwIntType(i0, Integer.MIN_VALUE, engine);
              fToDepth = -1;
              fFromLevel = 0;
              fToLevel = Integer.MAX_VALUE;
            } else {
              fFromDepth = Integer.MIN_VALUE;
              fToDepth = -1;
              fFromLevel = Validate.throwIntType(i0, Integer.MIN_VALUE, engine);
              fToLevel = Integer.MAX_VALUE;
            }
            return;
          } else if ((lst.arg1().isNegativeInfinity()) && (lst.arg2().isInfinity())) {
            // level specification {-Infinity, Infinity} is effectively the same as {0,-1}
            fFromDepth = Integer.MIN_VALUE;
            fToDepth = -1;
            fFromLevel = 0;
            fToLevel = Integer.MAX_VALUE;
            return;
          }
        }
      }
    }
    if (levelExpr.isInfinity() || levelExpr.equals(S.All)) {
      // level specification Infinity and -1 are equivalent
      fToLevel = Integer.MAX_VALUE;
      fFromLevel = startValueForAll;
      fFromDepth = Integer.MIN_VALUE;
      fToDepth = -1;
      return;
    }
    // Level specification `1` is not of the form n, {n}, or {m, n}.
    String str = Errors.getMessage("level", F.list(levelExpr), EvalEngine.get());
    throw new ArgumentTypeException(str);
    // throw new MathException("Invalid Level specification: " + levelExpr.toString());
  }

  /**
   * Example value set for including all levels:<br>
   * <code>fromLevel = 0;</code><br>
   * <code>toLevel = Integer.MAX_VALUE;</code><br>
   * <code>fromDepth = Integer.MIN_VALUE;</code><br>
   * <code>toDepth = -1;</code><br>
   *
   * @param fromLevel
   * @param toLevel
   * @param fromDepth
   * @param toDepth
   * @param includeHeads
   */
  public AbstractLevelVisitor(final int fromLevel,
      final int toLevel, final int fromDepth, final int toDepth, final boolean includeHeads) {
    fIncludeHeads = includeHeads;
    fFromLevel = fromLevel;
    fToLevel = toLevel;
    fCurrentLevel = 0;
    fFromDepth = fromDepth;
    fCurrentDepth = -1;
    fToDepth = toDepth;
  }

  public void incCurrentLevel() {
    fCurrentLevel++;
  }

  public void decCurrentLevel() {
    fCurrentLevel--;
  }

  public boolean isInRange(int level, int depth) {
    return (level >= fFromLevel) && (level <= fToLevel) && (depth >= fFromDepth)
        && (depth <= fToDepth);
  }

  /**
   * Return a shallow copy of the <code>ast</code>.
   *
   * @param ast
   * @param x
   * @return
   */
  public IASTMutable createResult(IAST ast, final IExpr x) {
    // can't use copy() here, because index 0 will be set in some scenarios
    return ast.copyAppendable();
  }
}
