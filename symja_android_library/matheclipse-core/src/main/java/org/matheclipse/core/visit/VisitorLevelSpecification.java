package org.matheclipse.core.visit;

import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
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
public class VisitorLevelSpecification extends AbstractLevelVisitor {
  protected final Function<IExpr, IExpr> fFunction;

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
   * @param function the function which should be applied for an element
   * @param unevaledLevelExpr the given <i>level specification</i>
   * @param includeHeads set to <code>true</code>, if the header of an AST expression should be
   *        included
   * @param engine
   * @throws SymjaMathException if the <code>expr</code> is not a <i>level specification</i>
   */
  public VisitorLevelSpecification(final Function<IExpr, IExpr> function,
      final IExpr unevaledLevelExpr, boolean includeHeads, final EvalEngine engine) {
    this(function, unevaledLevelExpr, includeHeads, 1, engine);
  }

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
   * @param function
   * @param unevaledLevelExpr
   * @param includeHeads
   * @param startValueForAll
   * @param engine
   */
  public VisitorLevelSpecification(final Function<IExpr, IExpr> function,
      final IExpr unevaledLevelExpr, boolean includeHeads, int startValueForAll,
      final EvalEngine engine) {
    super(unevaledLevelExpr, includeHeads, startValueForAll, engine);
    fFunction = function;
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
    super(fromLevel, toLevel, fromDepth, toDepth, includeHeads);
    fFunction = function;
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
        checkRecursionLimit(assoc);
        if (fIncludeHeads) {
          // no include head for associations
        }
        assoc.forEach((x, i) -> {
          final IExpr temp = x.accept(this);
          if (temp.isPresent()) {
            if (result[0].isNIL()) {
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
        if (result[0].isNIL()) {
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
        checkRecursionLimit(ast);
        if (fIncludeHeads) {
          final IExpr temp = ast.get(0).accept(this);
          if (temp.isPresent()) {
            if (result[0].isNIL()) {
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
            if (result[0].isNIL()) {
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
        if (result[0].isNIL()) {
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

  private void checkRecursionLimit(IExpr expr) {
    fCurrentLevel++;
    int currentDepth = fEngine.getRecursionCounter() + fCurrentLevel;
    if (currentDepth > fEngine.getRecursionLimit()) {
      RecursionLimitExceeded.throwIt(currentDepth, expr);
    }
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

}
