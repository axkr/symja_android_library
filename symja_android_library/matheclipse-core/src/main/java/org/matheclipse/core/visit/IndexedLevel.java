package org.matheclipse.core.visit;

import java.util.function.BiFunction;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A level specification for levels used in the function <code>MapIndexed</code> Example: the nested
 * list <code>{x,{y}}</code> has depth <code>3</code>
 */
public class IndexedLevel extends AbstractLevelVisitor {
  protected final BiFunction<IExpr, IExpr, IExpr> fFunction;

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
  public IndexedLevel(final BiFunction<IExpr, IExpr, IExpr> function, final IExpr unevaledLevelExpr,
      boolean includeHeads, final EvalEngine engine) {
    super(unevaledLevelExpr, includeHeads, 1, engine);
    this.fFunction = function;
  }

  public IndexedLevel(final BiFunction<IExpr, IExpr, IExpr> function, final int level,
      final boolean includeHeads) {
    this(function, level, level, includeHeads);
  }

  public IndexedLevel(final BiFunction<IExpr, IExpr, IExpr> function, final int fromLevel,
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
  public IndexedLevel(final BiFunction<IExpr, IExpr, IExpr> function, final int fromLevel,
      final int toLevel, final int fromDepth, final int toDepth, final boolean includeHeads) {
    super(fromLevel, toLevel, fromDepth, toDepth, includeHeads);
    fFunction = function;
  }

  /**
   * 
   * @param element
   * @param indx
   * @return
   * @deprecated use {@link #visitAtom(IExpr, int[])}
   */
  @Deprecated
  public IExpr visitExpr(IExpr element, IExpr[] indx) {
    return visitAtom(element, indx);
  }

  protected final IExpr visitAtom(IExpr element, IExpr[] indx) {
    if (element.isASTOrAssociation()) {
      return F.NIL;
    }
    fCurrentDepth = -1;
    IASTAppendable indxList = F.mapRange(0, indx.length, i -> indx[i]);
    return isInRange(fCurrentLevel, -1) ? fFunction.apply(element, indxList) : F.NIL;
  }

  public IExpr visitAST(IAST ast, IExpr[] indx) {
    IASTMutable[] result = new IASTMutable[] {F.NIL};
    int[] minDepth = new int[] {0};
    try {
      int size = indx.length;
      final IExpr[] newIndx = new IExpr[size + 1];
      System.arraycopy(indx, 0, newIndx, 0, size);
      fCurrentLevel++;
      if (fIncludeHeads) {
        newIndx[size] = F.C0;
        IExpr element = ast.get(0);
        if (element.isAST()) {
          IExpr temp = visitAST((IAST) element, newIndx);
          if (temp.isPresent()) {
            element = temp;
          }
        }
        final IExpr temp = visitAtom(element, newIndx);
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
      ast.forEachRule((x, i) -> {
        newIndx[size] = F.ZZ(i);
        IExpr element = x;
        boolean evaled = false;
        if (ast.isAssociation() && element.isRuleAST()) {
          // IAssociation assoc = (IAssociation) ast;
          IAST rule = (IAST) element;
          IExpr key = rule.arg1();
          newIndx[size] = F.Key(key);
          element = rule.arg2();
        }
        if (element.isASTOrAssociation()) {
          IExpr temp = visitAST((IAST) element, newIndx);
          if (temp.isPresent()) {
            evaled = true;
            element = temp;
          }
        }
        IExpr value = F.NIL;
        final IExpr temp = visitAtom(element, newIndx);
        if (temp.isPresent()) {
          value = temp;
        } else if (evaled) {
          value = element;
        }
        if (value.isPresent()) {
          if (result[0].isNIL()) {
            result[0] = ast.copyAppendable();
          }
          result[0].setValue(i, value);
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
      IASTAppendable indxList = F.mapRange(0, indx.length, i -> indx[i]);
      if (result[0].isNIL()) {
        return fFunction.apply(ast, indxList);
      } else {
        IExpr temp = fFunction.apply(result[0], indxList);
        if (temp.isPresent()) {
          return temp;
        }
      }
    }

    return result[0];
  }

}
