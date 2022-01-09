package org.matheclipse.core.visit;

import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.FlowControlException;
import org.matheclipse.core.eval.exception.SymjaMathException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A level specification visitor for levels in abstract syntax trees (AST), which removes elements
 * from a (cloned) AST in the <code>visit(IAST clonedAST)</code> method.
 *
 * <p>Example: the nested list <code>{x,{y}}</code> has depth <code>3</code>
 */
public class VisitorRemoveLevelSpecification extends VisitorLevelSpecification {
  /** StopException will be thrown, if maximum number of Cases results are reached */
  public static class StopException extends FlowControlException {
    private static final long serialVersionUID = -8839477630696222675L;

    public StopException() {
      super("Stop VisitorDeleteLevelSpecification evaluation");
    }
  }

  final int maximumRemoved;
  private int removedCounter;

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
   * @param expr the given <i>level specification</i>
   * @param maximumRemoved maximum number of elements, which are allowed to remove
   * @param includeHeads set to <code>true</code>, if the header of an AST expression should be
   *     included
   * @throws SymjaMathException if the <code>expr</code> is not a <i>level specification</i>
   * @see
   */
  public VisitorRemoveLevelSpecification(
      final Function<IExpr, IExpr> function,
      final IExpr expr,
      int maximumRemoved,
      boolean includeHeads,
      final EvalEngine engine) {
    super(function, expr, includeHeads, engine);
    this.maximumRemoved = maximumRemoved;
    this.removedCounter = 0;
  }

  /**
   * Define a level specification for all elements on level <code>level</code> .
   *
   * @param level
   */
  public VisitorRemoveLevelSpecification(final Function<IExpr, IExpr> function, final int level) {
    this(function, level, true);
  }

  public VisitorRemoveLevelSpecification(
      final Function<IExpr, IExpr> function, final int level, final boolean includeHeads) {
    this(function, level, level, includeHeads);
  }

  public VisitorRemoveLevelSpecification(
      final Function<IExpr, IExpr> function, final int fromLevel, final int toLevel) {
    this(function, fromLevel, toLevel, true);
  }

  public VisitorRemoveLevelSpecification(
      final Function<IExpr, IExpr> function,
      final int fromLevel,
      final int toLevel,
      final boolean includeHeads) {
    this(function, fromLevel, toLevel, Integer.MIN_VALUE, -1, -1, includeHeads);
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
   * @param maximumRemoved maximum number of elements, which are allowed to remove
   * @param includeHeads
   */
  public VisitorRemoveLevelSpecification(
      final Function<IExpr, IExpr> function,
      final int fromLevel,
      final int toLevel,
      final int fromDepth,
      final int toDepth,
      int maximumRemoved,
      final boolean includeHeads) {
    super(function, fromLevel, toLevel, fromDepth, toDepth, includeHeads);
    this.maximumRemoved = maximumRemoved;
    this.removedCounter = 0;
  }

  /**
   * Get the number of remove operations.
   *
   * @return the removed counter
   */
  public int getRemovedCounter() {
    return removedCounter;
  }

  /**
   * <b>Note:</b> the given AST will be modified, i.e. some elements may be removed!
   *
   * @param astCopy a mutable copy of an AST where arguments could be removed.
   */
  @Override
  public IExpr visit(IASTMutable astCopy) {
    int minDepth = -1;
    IExpr arg;
    IExpr temp;
    IASTAppendable ast;
    try {
      if (!(astCopy instanceof IASTAppendable)) {
        ast = astCopy.copyAppendable();
      } else {
        ast = (IASTAppendable) astCopy;
      }
      fCurrentLevel++;
      if (fIncludeHeads) {
        arg = ast.get(0);
        if (arg.isAST()) {
          arg = ((IAST) arg).copyAppendable();
        }
        temp = arg.accept(this);
        if (temp.isPresent()) {
          ast.remove(0);
          removedCounter++;
          if (maximumRemoved >= 0) {
            if (removedCounter >= maximumRemoved) {
              throw new StopException();
            }
          }
        } else {
          ast.set(0, arg);
        }
        if (fCurrentDepth < minDepth) {
          minDepth = fCurrentDepth;
        }
      }
      int i = 1;
      while (i < ast.size()) {
        arg = ast.get(i);
        if (arg.isAST()) {
          arg = ((IAST) arg).copyAppendable();
        }
        temp = arg.accept(this);
        if (temp.isPresent()) {
          ast.remove(i);
          removedCounter++;
          if (maximumRemoved >= 0) {
            if (removedCounter >= maximumRemoved) {
              throw new StopException();
            }
          }
          continue;
        } else {
          ast.set(i, arg);
        }
        i++;
        if (fCurrentDepth < minDepth) {
          minDepth = fCurrentDepth;
        }
      }
    } finally {
      fCurrentLevel--;
    }
    fCurrentDepth = --minDepth;
    if (isInRange(fCurrentLevel, minDepth)) {
      return fFunction.apply(ast);
    }
    return F.NIL;
  }
}
