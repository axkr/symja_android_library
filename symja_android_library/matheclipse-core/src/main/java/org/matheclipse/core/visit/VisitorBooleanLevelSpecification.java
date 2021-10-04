package org.matheclipse.core.visit;

import java.util.function.Predicate;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IDataExpr;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * A level specification visitor for levels in abstract syntax trees (AST).
 *
 * <p>Example: the nested list <code>{x,{y}}</code> has depth <code>3</code>
 */
public class VisitorBooleanLevelSpecification extends AbstractVisitorBoolean {
  protected final Predicate<IExpr> fFunction;

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
   * An <code>expr</code> is interpreted as a <i>level specification</i> for the allowed levels in
   * an AST.<br>
   * If <code>expr</code> is a non-negative IInteger iValue set Level {1,iValue};<br>
   * If <code>expr</code> is a negative IInteger iValue set Level {iValue, 0}; <br>
   * If <code>expr</code> is a List {i0Value, i1Value} set Level {i0Value, i1Value};<br>
   *
   * @param function the function which should be applied for an element
   * @param unevaledLevelExpr the given <i>level specification</i>
   * @param includeHeads set to <code>true</code>, if the header of an AST expression should be
   *     included
   */
  public VisitorBooleanLevelSpecification(
      final Predicate<IExpr> function,
      final IExpr unevaledLevelExpr,
      boolean includeHeads,
      final EvalEngine engine) {
    IExpr levelExpr = engine.evaluate(unevaledLevelExpr);
    fFromLevel = fToLevel = -1;
    fFromDepth = fToDepth = 0;
    this.fIncludeHeads = includeHeads;
    this.fFunction = function;
    if (levelExpr instanceof IInteger) {
      final IInteger value = (IInteger) levelExpr;

      if (value.isNegative()) {
        fFromDepth = Integer.MIN_VALUE;
        fToDepth = Validate.checkIntType(S.MemberQ, value, Integer.MIN_VALUE, engine);
        fFromLevel = 1;
        fToLevel = Integer.MAX_VALUE;
      } else {
        fToLevel = Validate.checkIntType(S.MemberQ, value, Integer.MIN_VALUE, engine);
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

          final int level = Validate.checkIntType(S.MemberQ, i, Integer.MIN_VALUE, engine);
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
              fFromDepth = Validate.checkIntType(S.MemberQ, i0, Integer.MIN_VALUE, engine);
              fToDepth = Validate.checkIntType(S.MemberQ, i1, Integer.MIN_VALUE, engine);
              fFromLevel = 0;
              fToLevel = Integer.MAX_VALUE;
            } else if (i0.isNegative()) {
              // all subexpressions at levels i0 or above with a depth of -i1 or less.
              fFromDepth = Validate.checkIntType(S.MemberQ, i0, Integer.MIN_VALUE, engine);
              fToDepth = -1;
              fFromLevel = 0;
              fToLevel = Validate.checkIntType(S.MemberQ, i1, Integer.MIN_VALUE, engine);
            } else if (i1.isNegative()) {
              // all subexpressions at any level greater equal i0 that have a depth of -i1 or
              // greater.
              fFromDepth = Integer.MIN_VALUE;
              fToDepth = Validate.checkIntType(S.MemberQ, i1, Integer.MIN_VALUE, engine);
              fFromLevel = Validate.checkIntType(S.MemberQ, i0, Integer.MIN_VALUE, engine);
              fToLevel = Integer.MAX_VALUE;
            } else {
              fFromDepth = Integer.MIN_VALUE;
              fToDepth = -1;
              fFromLevel = Validate.checkIntType(S.MemberQ, i0, Integer.MIN_VALUE, engine);
              fToLevel = Validate.checkIntType(S.MemberQ, i1, Integer.MIN_VALUE, engine);
            }
            return;
          } else if ((lst.arg1() instanceof IInteger) && (lst.arg2().isInfinity())) {
            final IInteger i0 = (IInteger) lst.arg1();
            if (i0.isNegative()) {
              String str = IOFunctions.getMessage("level", F.List(levelExpr), EvalEngine.get());
              throw new ArgumentTypeException(str);
              //							throw new MathException("Invalid Level specification: " +
              // levelExpr.toString());
            } else {
              fFromDepth = Integer.MIN_VALUE;
              fToDepth = -1;
              fFromLevel = Validate.checkIntType(S.MemberQ, i0, Integer.MIN_VALUE, engine);
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
      fFromLevel = 1;
      fFromDepth = Integer.MIN_VALUE;
      fToDepth = -1;
      return;
    }
    String str = IOFunctions.getMessage("level", F.List(levelExpr), EvalEngine.get());
    throw new ArgumentTypeException(str);
    // throw new MathException("Invalid Level specification: " + levelExpr.toString());
  }

  /**
   * Define a level specification for all elements on level <code>level</code> .
   *
   * @param level
   */
  public VisitorBooleanLevelSpecification(final Predicate<IExpr> predicate, final int level) {
    this(predicate, level, true);
  }

  public VisitorBooleanLevelSpecification(
      final Predicate<IExpr> predicate, final int level, final boolean includeHeads) {
    this(predicate, level, level, includeHeads);
  }

  public VisitorBooleanLevelSpecification(
      final Predicate<IExpr> predicate, final int fromLevel, final int toLevel) {
    this(predicate, fromLevel, toLevel, true);
  }

  public VisitorBooleanLevelSpecification(
      final Predicate<IExpr> predicate,
      final int fromLevel,
      final int toLevel,
      final boolean includeHeads) {
    this(predicate, fromLevel, toLevel, Integer.MIN_VALUE, -1, includeHeads);
  }

  /**
   * Example value set for including all levels:<br>
   * <code>fromLevel = 0;</code><br>
   * <code>toLevel = Integer.MAX_VALUE;</code><br>
   * <code>fromDepth = Integer.MIN_VALUE;</code><br>
   * <code>toDepth = -1;</code><br>
   *
   * @param function the function which should be applied for an element
   * @param fromLevel
   * @param toLevel
   * @param fromDepth
   * @param toDepth
   * @param includeHeads
   */
  public VisitorBooleanLevelSpecification(
      final Predicate<IExpr> function,
      final int fromLevel,
      final int toLevel,
      final int fromDepth,
      final int toDepth,
      final boolean includeHeads) {
    fFunction = function;
    fFromLevel = fromLevel;
    fToLevel = toLevel;
    fCurrentLevel = 0;
    fIncludeHeads = includeHeads;
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
    return (level >= fFromLevel)
        && (level <= fToLevel)
        && (depth >= fFromDepth)
        && (depth <= fToDepth);
  }

  /** */
  @Override
  public boolean visit(IInteger element) {
    return visitAtom(element);
  }

  @Override
  public boolean visit(IDataExpr element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public boolean visit(IFraction element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public boolean visit(IComplex element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public boolean visit(INum element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public boolean visit(IComplexNum element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public boolean visit(ISymbol element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public boolean visit(IPattern element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public boolean visit(IPatternSequence element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public boolean visit(IStringX element) {
    return visitAtom(element);
  }

  protected final boolean visitAtom(IExpr element) {
    fCurrentDepth = -1;
    return isInRange(fCurrentLevel, -1) ? fFunction.test(element) : false;
  }

  @Override
  public boolean visit(IAST ast) {
    int[] minDepth = new int[] {0};
    try {
      fCurrentLevel++;
      if (fIncludeHeads) {
        final boolean temp = ast.get(0).accept(this);
        if (fCurrentDepth < minDepth[0]) {
          minDepth[0] = fCurrentDepth;
        }
        if (temp) {
          return true;
        }
      }
      final boolean exists =
          ast.exists(
              (x, i) -> {
                final boolean temp = x.accept(this);
                if (fCurrentDepth < minDepth[0]) {
                  minDepth[0] = fCurrentDepth;
                }
                return temp;
              });
      if (exists) {
        return true;
      }
    } finally {
      fCurrentLevel--;
    }
    fCurrentDepth = --minDepth[0];
    if (isInRange(fCurrentLevel, minDepth[0])) {
      return fFunction.test(ast);
    }
    return false;
  }
}
