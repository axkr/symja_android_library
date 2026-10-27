package org.matheclipse.core.visit;

import java.util.function.Predicate;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
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
 * <p>
 * Example: the nested list <code>{x,{y}}</code> has depth <code>3</code>
 */
public final class VisitorBooleanLevelSpecification extends AbstractVisitorBoolean {
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
   *        included
   */
  public VisitorBooleanLevelSpecification(final Predicate<IExpr> function,
      final IExpr unevaledLevelExpr, boolean includeHeads, final EvalEngine engine) {
    this.fIncludeHeads = includeHeads;
    this.fFunction = function;
    LevelBounds bounds = new LevelBounds(unevaledLevelExpr.eval(engine), 1, false,
        i -> Validate.checkIntType(S.MemberQ, i, Integer.MIN_VALUE, engine), engine);
    fFromLevel = bounds.fromLevel;
    fToLevel = bounds.toLevel;
    fFromDepth = bounds.fromDepth;
    fToDepth = bounds.toDepth;
  }

  /**
   * Define a level specification for all elements on level <code>level</code> .
   *
   * @param level
   */
  public VisitorBooleanLevelSpecification(final Predicate<IExpr> predicate, final int level) {
    this(predicate, level, true);
  }

  public VisitorBooleanLevelSpecification(final Predicate<IExpr> predicate, final int level,
      final boolean includeHeads) {
    this(predicate, level, level, includeHeads);
  }

  public VisitorBooleanLevelSpecification(final Predicate<IExpr> predicate, final int fromLevel,
      final int toLevel) {
    this(predicate, fromLevel, toLevel, true);
  }

  public VisitorBooleanLevelSpecification(final Predicate<IExpr> predicate, final int fromLevel,
      final int toLevel, final boolean includeHeads) {
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
  public VisitorBooleanLevelSpecification(final Predicate<IExpr> function, final int fromLevel,
      final int toLevel, final int fromDepth, final int toDepth, final boolean includeHeads) {
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
    return (level >= fFromLevel) && (level <= fToLevel) && (depth >= fFromDepth)
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
    // an expression without arguments has depth 2, like one of atoms
    int[] minDepth = new int[] {-1};
    try {
      fCurrentLevel++;
      if (fIncludeHeads) {
        final boolean temp = ast.get(0).accept(this);
        // the head is visited to test its parts, but its depth must not contribute to this
        // expression's depth: negative levels ignore the head chain (like `Depth`).
        if (temp) {
          return true;
        }
      }
      final boolean exists = ast.exists((x, i) -> {
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
