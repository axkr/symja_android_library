package org.matheclipse.core.visit;

import java.util.function.Function;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IAssociation;
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
public class VisitorLevelSpecification extends AbstractVisitor {
  protected final Function<IExpr, IExpr> fFunction;

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
   * @throws SymjaMathException if the <code>expr</code> is not a <i>level specification</i>
   */
  public VisitorLevelSpecification(final Function<IExpr, IExpr> function,
      final IExpr unevaledLevelExpr, boolean includeHeads, final EvalEngine engine) {
    IExpr levelExpr = engine.evaluate(unevaledLevelExpr);
    fFromLevel = fToLevel = -1;
    fFromDepth = fToDepth = 0;
    this.fIncludeHeads = includeHeads;
    this.fFunction = function;
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
              // Level specification `1` is not of the form n, {n}, or {m, n}.
              String str = IOFunctions.getMessage("level", F.list(levelExpr), EvalEngine.get());
              throw new ArgumentTypeException(str);
              // throw new MathException("Invalid Level specification: " + levelExpr.toString());
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
      fFromLevel = 1;
      fFromDepth = Integer.MIN_VALUE;
      fToDepth = -1;
      return;
    }
    // Level specification `1` is not of the form n, {n}, or {m, n}.
    String str = IOFunctions.getMessage("level", F.list(levelExpr), EvalEngine.get());
    throw new ArgumentTypeException(str);
    // throw new MathException("Invalid Level specification: " + levelExpr.toString());
  }

  /**
   * Define a level specification for all elements on level <code>level</code> .
   *
   * @param level
   */
  public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final int level) {
    this(function, level, true);
  }

  public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final int level,
      final boolean includeHeads) {
    this(function, level, level, includeHeads);
  }

  public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final int fromLevel,
      final int toLevel) {
    this(function, fromLevel, toLevel, true);
  }

  public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final int fromLevel,
      final int toLevel, final boolean includeHeads) {
    this(function, fromLevel, toLevel, Integer.MIN_VALUE, -1, includeHeads);
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
  public VisitorLevelSpecification(final Function<IExpr, IExpr> function, final int fromLevel,
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
  public IExpr visit(IDataExpr<?> element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public IExpr visit(IInteger element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public IExpr visit(IFraction element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public IExpr visit(IComplex element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public IExpr visit(INum element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public IExpr visit(IComplexNum element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public IExpr visit(ISymbol element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public IExpr visit(IPattern element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public IExpr visit(IPatternSequence element) {
    return visitAtom(element);
  }

  /** */
  @Override
  public IExpr visit(IStringX element) {
    return visitAtom(element);
  }

  protected final IExpr visitAtom(IExpr element) {
    fCurrentDepth = -1;
    return isInRange(fCurrentLevel, -1) ? fFunction.apply(element) : F.NIL;
  }

  @Override
  public IExpr visit(IAssociation assoc) {
    IAssociation[] result = new IAssociation[] {F.NIL};
    if (assoc.isPresent()) {
      int[] minDepth = new int[] {0};
      try {
        fCurrentLevel++;
        if (fIncludeHeads) {
          // no include head for associations
        }
        assoc.forEach((x, i) -> {
          final IExpr temp = x.accept(this);
          if (temp.isPresent()) {
            if (!result[0].isPresent()) {
              result[0] = createResult(assoc, temp);
            }
            result[0].set(i, assoc.getRule(i).setAtCopy(2, temp));
          }
          if (fCurrentDepth < minDepth[0]) {
            minDepth[0] = fCurrentDepth;
          }
        });
      } finally {
        fCurrentLevel--;
      }
      fCurrentDepth = --minDepth[0];
      if (isInRange(fCurrentLevel, minDepth[0])) {
        if (!result[0].isPresent()) {
          return fFunction.apply(assoc);
        } else {
          IExpr temp = fFunction.apply(result[0]);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
    }
    return result[0];
  }

  @Override
  public IExpr visit(IASTMutable ast) {
    IASTMutable[] result = new IASTMutable[] {F.NIL};
    if (ast.isPresent()) {
      int[] minDepth = new int[] {0};
      try {
        fCurrentLevel++;
        if (fIncludeHeads) {
          final IExpr temp = ast.get(0).accept(this);
          if (temp.isPresent()) {
            if (!result[0].isPresent()) {
              result[0] = createResult(ast, temp);
            }
            result[0].set(0, temp);
          }
          if (fCurrentDepth < minDepth[0]) {
            minDepth[0] = fCurrentDepth;
          }
        }
        ast.forEach((x, i) -> {
          final IExpr temp = x.accept(this);
          if (temp.isPresent()) {
            if (!result[0].isPresent()) {
              result[0] = createResult(ast, temp);
            }
            result[0].set(i, temp);
          }
          if (fCurrentDepth < minDepth[0]) {
            minDepth[0] = fCurrentDepth;
          }
        });
      } finally {
        fCurrentLevel--;
      }
      fCurrentDepth = --minDepth[0];
      if (isInRange(fCurrentLevel, minDepth[0])) {
        if (!result[0].isPresent()) {
          return fFunction.apply(ast);
        } else {
          IExpr temp = fFunction.apply(result[0]);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
    }
    return result[0];
  }

  /**
   * Can be overridden in derived visitors.
   *
   * @param assoc an association
   * @param x current rule of the association
   * @return
   */
  public IAssociation createResult(IAssociation assoc, final IExpr x) {
    return assoc.copy();
  }

  /**
   * Can be overridden in derived visitors.
   *
   * @param ast
   * @param x
   * @return
   */
  public IASTMutable createResult(IASTMutable ast, final IExpr x) {
    return ast.copyAppendable();
  }
}
