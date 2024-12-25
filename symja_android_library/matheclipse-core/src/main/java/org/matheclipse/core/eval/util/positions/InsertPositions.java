package org.matheclipse.core.eval.util.positions;

import java.util.IdentityHashMap;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ArgumentTypeStopException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import it.unimi.dsi.fastutil.ints.IntArrayList;

public class InsertPositions {
  final IdentityHashMap<IAST, IExpr> resultASTCache = new IdentityHashMap<IAST, IExpr>();
  final IdentityHashMap<IAST, IntArrayList> insertASTCache =
      new IdentityHashMap<IAST, IntArrayList>();

  int index;
  IAST positions;
  final IExpr element;

  public InsertPositions(IExpr element) {
    this.element = element;
    reset(F.CEmptyList);
  }

  public InsertPositions(IExpr element, IAST positions) {
    this.element = element;
    reset(positions);
  }

  protected void reset(IAST positions) {
    this.index = 1;
    this.positions = positions;
  }

  /**
   * Map a function `f` over the elements of a list of positions
   * <code>{{p11, p12, ...},{p21, p22, ...}}</code>. The function `f` is applied to the part of the
   * expression at each position.
   * 
   * @param f function to apply
   * @param ast
   * @param listOfListsOfPositions
   */
  public static IExpr insertListOfPositions(IAST ast, IExpr element, IAST listOfListsOfPositions) {
    InsertPositions mapPositions = new InsertPositions(element);
    for (int i = 1; i < listOfListsOfPositions.size(); i++) {
      IAST subList = (IAST) listOfListsOfPositions.get(i);
      if (subList.isEmpty()) {
        // Cannot insert at position `1` in `2`.
        return Errors.printMessage(S.Insert, "ins", F.List(F.CEmptyList, ast));
      }

      mapPositions.reset(subList);
      IExpr temp = mapPositions.mapAtRecursive(ast);
      if (temp.isPresent()) {
        if (temp.isASTOrAssociation()) {
          ast = (IAST) temp;
        }
      }
    }
    MapPositions.removeIsCopiedRecursive(ast);
    return ast;
  }

  /**
   * Map a function `f` over the elements of a list of positions <code>{p1, p1, ...}</code>. The
   * function `f` is applied to the part of the expression at each position.
   * 
   * @param f function to apply
   * @param ast
   * @param listOfPositions list of positions
   * @return
   */
  public static IExpr insertPositions(IAST ast, IExpr element, IAST listOfPositions) {
    if (listOfPositions.isEmpty()) {
      return ast;
    }
    InsertPositions mapPositions = new InsertPositions(element, listOfPositions);
    IAST result = mapPositions.mapAtRecursive(ast);
    MapPositions.removeIsCopiedRecursive(result);
    return result;
  }

  protected IAST mapAtRecursive(IAST ast) {
    IExpr pos = positions.get(index);
    if (pos.equals(S.All)) {
      Object[] pair = getAppendableIntPair(ast);
      final IASTAppendable subResult = (IASTAppendable) pair[0];
      final IntArrayList insertedPositions = (IntArrayList) pair[1];
      if (index == positions.size() - 1) {
        int position = 1;
        for (int i = 1; i < ast.size(); i++) {
          insertedPositions.add(i);
          subResult.set(position++, element);
          position++;
        }
      } else {
        index++;
        for (int i = 1; i < ast.size(); i++) {
          IExpr temp = mapAtRecursive(subResult.getAST(i));
          if (temp.isPresent()) {
            subResult.set(i, temp);
          }
        }
        index--;
      }
      return subResult;
    }
    if (pos.isString() || pos.isKey()) {
      if (ast.isAssociation()) {
        if (!element.isRuleAST() && !element.isListOfRules()) {
          // The argument `1` is not a rule or a list of rules.
          throw new ArgumentTypeStopException("invdt2", F.list(element, ast));
        }
        IExpr key = pos.isString() ? pos : pos.first();
        final Object[] pair = getAppendableIntPair(ast);
        final IAssociation association = (IAssociation) pair[0];
        // IntArrayList insertedPositions = (IntArrayList) pair[1];
        if (index == positions.size() - 1) {
          int keyPosition = ((IAssociation) ast).getRulePosition(key);
          if (keyPosition > 0) {
            return appendElement(association, keyPosition);
          }
        } else {
          IAST rule = ((IAssociation) ast).getRule(key);
          if (rule.isPresent()) {
            IExpr arg = rule.second();
            if (arg.isASTOrAssociation()) {
              index++;
              IExpr temp = mapAtRecursive(((IAST) arg));
              if (temp.isPresent()) {
                rule = rule.setAtCopy(2, temp);
                association.appendRule(rule);
                index--;
                return association;
              }
              index--;
            }
          }
        }

        // Part `1` of `2` does not exist.
        throw new ArgumentTypeException("partw", F.list(F.list(pos), ast));
      }
    }

    int p = pos.toIntDefault();
    if (p == Integer.MIN_VALUE) {
      // Part `1` of `2` does not exist.
      throw new ArgumentTypeException("partw", F.list(F.list(pos), ast));
    }
    Object[] pair = getAppendableIntPair(ast);
    IASTAppendable subResult = (IASTAppendable) pair[0];
    IntArrayList insertedPositions = (IntArrayList) pair[1];
    if (p < 0) {
      p = subResult.size() - insertedPositions.size() + p + 1;
    }

    if (p >= 0 && p < ast.size() + 1) {
      if (index == positions.size() - 1) {
        if (ast.isAssociation()) {
          if (!element.isRuleAST() && !element.isListOfRules()) {
            // The argument `1` is not a rule or a list of rules.
            throw new ArgumentTypeStopException("invdt2", F.list(element, ast));
          }
          if (ast.size() > p) {
            IExpr temp = ast.getRule(p);
            if (temp.isPresent()) {
              return appendElement((IAssociation) subResult, p);
            } else {
              throw new ArgumentTypeException("ins", F.list(F.ZZ(p), ast));
            }
          } else if (p == 1) {
            if (element.isListOfRules()) {
              IAST list = (IAST) element;
              for (int i = 1; i < list.size(); i++) {
                IExpr rule = list.getRule(i);
                subResult.appendRule(rule);
              }
            } else {
              subResult.appendRule(element);
            }
            return subResult;
          }
          throw new ArgumentTypeException("ins", F.list(F.ZZ(p), ast));
        }
        int newP = getInsertPosition(insertedPositions, p);
        subResult.append(newP, element);
        insertedPositions.add(p);
        return subResult;
      } else {
        IExpr arg = ast.get(p);
        if (arg.isASTOrAssociation()) {
          // subResult = getAppendableAST(ast);
          index++;
          IExpr temp = mapAtRecursive((IAST) arg);
          if (temp.isPresent()) {
            index--;
            subResult.set(p, temp);
            return subResult;
          }
          index--;
        }
      }
    }
    // Part `1` of `2` does not exist.
    throw new ArgumentTypeException("partw", F.list(F.list(pos), ast));
  }

  /**
   * Append the element to the result at position `p`. If element is a {@link S#List} expression,
   * the list elements are appended.
   * 
   * @param result
   * @param p
   * @return
   */
  protected IAST appendElement(IAssociation result, int p) {
    if (element.isListOfRules()) {
      IAST list = (IAST) element;
      for (int i = list.argSize(); i > 0; i--) {
        IExpr rule = list.getRule(i);
        result.append(p, rule);
      }
      return result;
    }
    result.append(p, element);
    return result;
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
    for (int i = 0; i < insertedPositions.size(); i++) {
      if (insertedPositions.getInt(i) <= originalPosition) {
        position++;
      }
    }
    return position;
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
