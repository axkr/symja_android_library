package org.matheclipse.core.eval.util.positions;

import java.util.IdentityHashMap;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class FlattenPositions {
  final IdentityHashMap<IAST, IExpr> resultASTCache = new IdentityHashMap<IAST, IExpr>();
  final IdentityHashMap<IAST, IntArrayList> insertASTCache =
      new IdentityHashMap<IAST, IntArrayList>();

  final IAST originalAST;

  int level;
  IAST positions;

  public FlattenPositions(IAST ast) {
    this.originalAST = ast;
    reset(F.CEmptyList);
  }

  public FlattenPositions(IAST ast, IAST positions) {
    this.originalAST = ast;
    reset(positions);
  }

  protected void reset(IAST positions) {
    this.level = 1;
    this.positions = positions;
  }


  public static IExpr flattenListOfPositions(IAST ast, IAST listOfListsOfPositions) {
    FlattenPositions flattenPositions = new FlattenPositions(ast);
    for (int i = 1; i < listOfListsOfPositions.size(); i++) {
      IAST subList = (IAST) listOfListsOfPositions.get(i);
      if (subList.isEmpty()) {
        // Cannot insert at position `1` in `2`.
        return Errors.printMessage(S.FlattenAt, "ins", F.List(F.CEmptyList, ast));
      }

      flattenPositions.reset(subList);
      IExpr temp = flattenPositions.mapAtRecursive(ast);
      if (temp.isPresent()) {
        if (temp.isASTOrAssociation()) {
          ast = (IAST) temp;
        }
      }
    }
    MapPositions.removeIsCopiedRecursive(ast);
    return ast;
  }

  public static IExpr flattenPositions(IAST ast, IAST listOfPositions) {
    if (listOfPositions.isEmpty()) {
      return ast;
    }
    FlattenPositions flattenPositions = new FlattenPositions(ast, listOfPositions);
    IAST result = flattenPositions.mapAtRecursive(ast);
    MapPositions.removeIsCopiedRecursive(result);
    return result;
  }

  protected IAST mapAtRecursive(IAST ast) {
    IExpr position = positions.get(level);
    int p = position.toIntDefault();
    if (F.isNotPresent(p)) {
      // Part `1` of `2` does not exist.
      throw new ArgumentTypeException("partw", F.list(positions, originalAST));
    }
    Object[] pair = getAppendableIntPair(ast);
    IASTAppendable subResult = (IASTAppendable) pair[0];
    IntArrayList insertedPositions = (IntArrayList) pair[1];
    if (p < 0) {
      p = subResult.size() - getInsertPositionSize(insertedPositions) + p;
    }

    if (p >= 0 && p < ast.size() + 1) {
      if (level == positions.argSize()) {
        int newP = getInsertPosition(insertedPositions, p);
        if (newP == -1) {
          // position used a second time
          return subResult;
        }
        IExpr element = subResult.get(newP);
        if (!element.isAST()) {
          return subResult;
        }
        IAST elementAST = (IAST) element;
        int sizeOfFlattenedElement = elementAST.argSize();
        subResult.set(newP, F.Sequence());
        for (int i = elementAST.argSize(); i > 0; i--) {
          subResult.append(newP, elementAST.getRule(i));
        }
        insertedPositions.add(p);
        insertedPositions.add(sizeOfFlattenedElement);
        return subResult;
      } else {
        IExpr arg = ast.get(p);
        if (arg.isASTOrAssociation()) {
          // subResult = getAppendableAST(ast);
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

  /**
   * Get the new insert position.
   * 
   * @param insertedPositions list of already inserted positions
   * @param originalPosition the original position defined for the element
   * @return
   */
  protected int getInsertPosition(IntArrayList insertedPositions, int originalPosition) {
    int position = originalPosition;
    for (int i = 0; i < insertedPositions.size(); i += 2) {
      if (insertedPositions.getInt(i) < originalPosition) {
        position += insertedPositions.getInt(i + 1);
      } else if (insertedPositions.getInt(i) == originalPosition) {
        return -1;
      }
    }
    return position;
  }

  protected int getInsertPositionSize(IntArrayList insertedPositions) {
    int sum = 0;
    for (int i = 0; i < insertedPositions.size(); i += 2) {
      sum += insertedPositions.getInt(i + 1);
    }
    return sum;
  }

  /**
   * Get the pair of [{@link IASTAppendable}, {@link IntArrayList}] list of already inserted
   * original position numbers.
   * 
   * @param ast
   * @return
   */
  protected Object[] getAppendableIntPair(IAST ast) {
    if (ast.isEvalFlagOn(IAST.IS_COPIED)) {
      IntArrayList value = insertASTCache.get(ast);
      return new Object[] {ast, value};
    }
    IASTAppendable appendable = ast.copyAppendable();
    appendable.addEvalFlags(IAST.IS_COPIED);
    IntArrayList list = new IntArrayList();
    insertASTCache.put(appendable, list);
    return new Object[] {appendable, list};
  }

}
