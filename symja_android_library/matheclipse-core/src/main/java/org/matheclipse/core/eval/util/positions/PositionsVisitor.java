package org.matheclipse.core.eval.util.positions;

import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.EvalFlags.Flag;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Walks down one position <code>{p1, p2, ...}</code> of an expression and changes the part at its
 * end. The parts along the way are copied once (marked with {@link Flag#IS_COPIED}), so several
 * positions can be applied to the same copy. A position element is an index, which counts from the
 * end if negative, or for an association a key, given as a string or as <code>Key(k)</code>.
 */
abstract class PositionsVisitor {

  final IAST originalAST;

  int level;

  IAST positions;

  PositionsVisitor(IAST ast, IAST positions) {
    this.originalAST = ast;
    reset(positions);
  }

  protected void reset(IAST positions) {
    this.level = 1;
    this.positions = positions;
  }

  /**
   * The changed part for a position element which is no index or key, like <code>All</code> or a
   * <code>Span</code>.
   *
   * @return <code>null</code> if this visitor doesn't handle <code>position</code>
   */
  protected IAST visitOther(IAST ast, IExpr position) {
    return null;
  }

  /**
   * Change the value of the rule at <code>index</code> of the association <code>ast</code>, at the
   * end of the position.
   *
   * @return the changed association, or <code>null</code> if the part can't be changed
   */
  protected abstract IAST keyLeaf(IAST ast, int index, IAST rule);

  /**
   * Change the part <code>p</code> of <code>ast</code>, at the end of the position.
   *
   * @return the changed expression, or <code>null</code> if the part can't be changed
   */
  protected abstract IAST indexLeaf(IAST ast, int p);

  /**
   * Is the head of an expression with <code>n</code> arguments also at position
   * <code>-(n+1)</code>, and not only at position <code>0</code>?
   */
  protected boolean reachesHeadFromEnd() {
    return true;
  }

  protected IAST mapAtRecursive(IAST ast) {
    IExpr position = positions.get(level);
    IAST other = visitOther(ast, position);
    if (other != null) {
      return other;
    }
    if (position.isString() || position.isKey()) {
      if (ast.isAssociation()) {
        IExpr key = position.isString() ? position : position.first();
        int index = ((IAssociation) ast).getRulePosition(key);
        if (index > 0) {
          IAST rule = ((IAssociation) ast).getRule(index);
          if (level == positions.argSize()) {
            IAST result = keyLeaf(ast, index, rule);
            if (result != null) {
              return result;
            }
          } else {
            IExpr arg = rule.second();
            if (arg.isASTOrAssociation()) {
              level++;
              IExpr temp = mapAtRecursive(((IAST) arg));
              if (temp.isPresent()) {
                IASTAppendable association = getAppendableAST(ast);
                association.set(index, rule.setAtCopy(2, temp));
                level--;
                return association;
              }
              level--;
            }
          }
        }
      }
      // Part `1` of `2` does not exist.
      throw new ArgumentTypeException("partw", F.list(positions, originalAST));
    }

    int p = position.toIntDefault();
    if (F.isNotPresent(p)) {
      // Part `1` of `2` does not exist.
      throw new ArgumentTypeException("partw", F.list(positions, originalAST));
    }
    if (p < 0) {
      p = ast.size() + p;
      if (p == 0 && !reachesHeadFromEnd()) {
        p = -1;
      }
    }

    if (p >= 0 && p < ast.size()) {
      if (level == positions.argSize()) {
        IAST result = indexLeaf(ast, p);
        if (result != null) {
          return result;
        }
      } else {
        IExpr arg = ast.get(p);
        if (arg.isASTOrAssociation()) {
          IASTAppendable subResult = getAppendableAST(ast);
          level++;
          IExpr temp = mapAtRecursive((IAST) arg);
          if (temp.isPresent()) {
            level--;
            subResult.set(p, temp);
            return subResult;
          }
          level--;
        }
      }
    }
    // Part `1` of `2` does not exist.
    throw new ArgumentTypeException("partw", F.list(positions, originalAST));
  }

  /** <code>ast</code> itself if it is already a copy, otherwise a copy marked as one. */
  protected static IASTAppendable getAppendableAST(IAST ast) {
    if (ast.hasFlag(Flag.IS_COPIED)) {
      return (IASTAppendable) ast;
    }
    IASTAppendable appendable = ast.copyAppendable();
    appendable.addFlag(Flag.IS_COPIED);
    return appendable;
  }
}
