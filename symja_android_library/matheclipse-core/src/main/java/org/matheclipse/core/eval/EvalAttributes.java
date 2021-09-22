package org.matheclipse.core.eval;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Static methods for evaluating <code>ISymbol.FLAT, ISymbol.LISTABLE</code> and <code>
 * ISymbol.ORDERLESS</code> attributes.
 *
 * @see org.matheclipse.core.interfaces.ISymbol#FLAT
 * @see org.matheclipse.core.interfaces.ISymbol#LISTABLE
 * @see org.matheclipse.core.interfaces.ISymbol#ORDERLESS
 */
public class EvalAttributes {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * Check the cached hashcode with the current one. Only necessary in DEBUG mode.
   *
   * @param ast
   */
  private static void checkCachedHashcode(final IAST ast) {

    final int hash = ast.getHashCache();
    if (hash != 0) {
      ast.clearHashCache();
      if (hash != ast.hashCode()) {
        throw new UnsupportedOperationException("Different hash codes for:" + ast.toString());
      }
    }
  }

  /**
   * Flatten the list (i.e. typically the ASTs head has the attribute ISymbol.FLAT) example: suppose
   * the head f should be flattened out: <code>f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]</code>
   *
   * @param ast the <code>AST</code> whose elements should be flattened.
   * @return returns the flattened list or <code>F.NIL</code>
   */
  public static IASTAppendable flattenDeep(final IAST ast) {
    if ((ast.getEvalFlags() & IAST.IS_FLATTENED) == IAST.IS_FLATTENED) {
      // already flattened
      return F.NIL;
    }
    final IExpr sym = ast.head();
    if (sym.isSymbol() && ast.isAST(sym)) {
      IASTAppendable result = flattenDeep((ISymbol) sym, ast);
      if (result.isPresent()) {
        result.addEvalFlags(IAST.IS_FLATTENED);
        return result;
      }
    }
    ast.addEvalFlags(IAST.IS_FLATTENED);
    return F.NIL;
  }

  public static IExpr simpleEval(IASTMutable result) {
    IASTMutable temp = result;
    if (temp.isFlatAST()) {
      IASTMutable t = EvalAttributes.flatten(temp);
      if (t.isPresent()) {
        temp = t;
      }
    }
    if (temp.isOrderlessAST()) {
      EvalAttributes.sort(temp);

      if (temp.isPlus()) {
        if (temp.first().isZero()) {
          IAST rest = temp.rest();
          rest.isFreeOfPatterns();
          return rest.oneIdentity0();
        }
      } else if (temp.isTimes()) {
        if (temp.first().isOne()) {
          IAST rest = temp.rest();
          rest.isFreeOfPatterns();
          return rest.oneIdentity1();
        }
        // else if (temp.first().isZero()) {
        // return temp.first();
        // }
      }
    }
    if (temp.isOneIdentityAST1()) {
      return temp.first();
    }

    temp.isFreeOfPatterns();
    return temp;
  }

  /**
   * Flatten only the first level in the list (i.e. typically the ASTs head has the attribute
   * ISymbol.FLAT) example: suppose the head f should be flattened out: <code>
   * f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,f[u,v],z]</code>
   *
   * @param ast the <code>AST</code> whose elements should be flattened.
   * @return returns the flattened list or <code>F.NIL</code>
   * @see #flattenDeep(IAST)
   */
  public static IASTAppendable flatten(final IAST ast) {
    if (ast.isEvalFlagOn(IAST.IS_FLATTENED)) {
      // already flattened
      return F.NIL;
    }

    final IExpr sym = ast.head();
    if (sym.isSymbol() && ast.isAST(sym)) {
      IASTAppendable result = flatten((ISymbol) sym, ast);
      if (result.isPresent()) {
        result.addEvalFlags(IAST.IS_FLATTENED);
        return result;
      }
    }
    ast.addEvalFlags(IAST.IS_FLATTENED);
    return F.NIL;
  }

  /**
   * Flatten the list (i.e. the ASTs head element has the same head) example: suppose the head f
   * should be flattened out:<br>
   * <code>f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]</code>
   *
   * @param head the head of the expression, which should be flattened.
   * @param ast the <code>sublist</code> which should be added to the <code>result</code> list.
   * @return the flattened ast expression if a sublist was flattened out, otherwise return <code>
   *     F#NIL</code>..
   */
  public static IASTAppendable flattenDeep(final ISymbol head, final IAST ast) {
    int[] newSize = new int[1];
    newSize[0] = 0;
    boolean[] flattened = new boolean[] {false};

    ast.forEach(
        expr -> {
          if (expr.isAST(head)) {
            flattened[0] = true;
            int temp = flattenAlloc(head, (IAST) expr);
            newSize[0] += temp;
          } else {
            newSize[0]++;
          }
        });

    if (flattened[0]) {
      IASTAppendable result = F.ast(ast.head(), newSize[0]);
      ast.forEach(
          expr -> {
            if (expr.isAST(head)) {
              result.appendArgs(flattenDeep(head, (IAST) expr).orElse((IAST) expr));
            } else {
              result.append(expr);
            }
          });
      return result;
    }
    return F.NIL;
  }

  /**
   * Flatten only the first level in the list (i.e. the ASTs head element has the same head)
   * example: suppose the head f should be flattened out:<br>
   * <code>f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,f[u,v],z]</code>
   *
   * @param head the head of the expression, which should be flattened.
   * @param ast the <code>sublist</code> which should be added to the <code>result</code> list.
   * @return the flattened ast expression if a sublist was flattened out, otherwise return <code>
   *     F#NIL</code>.
   * @see #flattenDeep(ISymbol, IAST)
   */
  public static IASTAppendable flatten(final ISymbol head, final IAST ast) {
    int[] newSize = new int[1];
    newSize[0] = 0;
    boolean[] flattened = new boolean[] {false};

    ast.forEach(
        expr -> {
          if (expr.isAST(head)
              || (expr.isUnevaluated()
                  && expr.first().head().equals(head)
                  && expr.first().isAST())) {
            flattened[0] = true;
            int temp = ((IAST) expr).argSize(); // flattenAlloc(head, (IAST) expr);
            newSize[0] += temp;
          } else {
            newSize[0]++;
          }
        });

    if (flattened[0]) {
      IASTAppendable result = F.ast(ast.head(), newSize[0]);
      ast.forEach(
          expr -> {
            if (expr.isAST(head)) {
              result.appendArgs((IAST) expr); // flatten(head, (IAST) expr).orElse((IAST) expr));
            } else if (expr.isUnevaluated()
                && expr.first().head().equals(head)
                && expr.first().isAST()) {
              IAST unevaluated = (IAST) expr.first();
              result.appendArgs(unevaluated.map(head, x -> F.Unevaluated(x)));
            } else {
              result.append(expr);
            }
          });
      return result;
    }
    return F.NIL;
  }

  /**
   * Flatten all positions in the given list (i.e. the ASTs head element has the same head) example:
   * suppose the head f should be flattened out:<br>
   * <code>f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]</code>
   *
   * @param head the head of the expression, which should be flattened.
   * @param ast the <code>sublist</code> which should be added to the <code>result</code> list.
   * @param positions the positions which should be flattened
   * @return the flattened ast expression if a sublist was flattened out, otherwise return <code>
   *     F#NIL</code>..
   */
  public static IASTAppendable flattenAt(final ISymbol head, final IAST ast, int[] positions) {
    IExpr expr;
    final int astSize = ast.size();
    int newSize = 0;
    boolean flattened = false;
    for (int i = 1; i < astSize; i++) {
      expr = ast.get(i);
      if (expr.isAST() && containsPosition(i, positions)) {
        flattened = true;
        int temp = flattenAlloc(head, (IAST) expr);
        newSize += temp;
      } else {
        newSize++;
      }
    }
    if (flattened) {
      IASTAppendable result = F.ast(ast.head(), newSize);
      for (int i = 1; i < astSize; i++) {
        expr = ast.get(i);
        if (expr.isAST() && containsPosition(i, positions)) {
          result.appendArgs(flattenAt(head, (IAST) expr, positions).orElse((IAST) expr));
        } else {
          result.append(expr);
        }
      }
      return result;
    }
    return F.NIL;
  }

  private static boolean containsPosition(int position, int[] positions) {
    for (int i = 0; i < positions.length; i++) {
      if (positions[i] == position) {
        return true;
      }
    }
    return false;
  }

  public static int flattenAlloc(final ISymbol head, final IAST ast) {
    int[] newSize = new int[1];
    ast.forEach(
        expr -> {
          if (expr.isAST(head)) {
            newSize[0] += flattenAlloc(head, (IAST) expr);
          } else {
            newSize[0]++;
          }
        });
    return newSize[0];
  }

  /**
   * Flatten the list (i.e. the ASTs head element has the same head) element has the same head)
   * example: suppose the head f should be flattened out: <br>
   * <code>f[a,b,f[x,y,f[u,v]],z] ==> f[a,b,x,y,u,v,z]</code>
   *
   * @param head the head of the expression, which should be flattened.
   * @param sublist the <code>sublist</code> which should be added to the <code>result</code> list.
   * @param result the <code>result</code> list, where all sublist elements with the same <code>head
   *     </code> should be appended.
   * @param recursionCounter
   * @param level the recursion level up to which the list should be flattened
   * @return <code>true</code> if a sublist was flattened out into the <code>result</code> list.
   */
  public static boolean flatten(
      final ISymbol head,
      final IAST sublist,
      final IASTAppendable result,
      final int recursionCounter,
      final int level) {
    boolean[] flattened = new boolean[1];
    flattened[0] = false;
    sublist.forEach(
        1,
        sublist.size(),
        expr -> {
          if (expr.isAST(head) && recursionCounter < level) {
            flattened[0] = true;
            flatten(head, (IAST) expr, result, recursionCounter + 1, level);
          } else {
            result.append(expr);
          }
        });
    return flattened[0];
  }

  /**
   * Copy the <code>ast</code> and return the sorted copy using function <code>Order(a, b)</code>.
   *
   * @param ast
   * @return return the sorted copy
   */
  public static final IAST copySort(final IAST ast) {
    final IASTMutable sortedList = ast.copy();
    sort(sortedList);
    return sortedList;
  }

  /**
   * Copy the <code>ast</code> and return the sorted copy using function <code>Less(a, b)</code>.
   *
   * @param ast
   * @return return the sorted copy
   */
  public static final IAST copySortLess(final IAST ast) {
    final IASTMutable sortedList = ast.copy();
    sortLess(sortedList);
    return sortedList;
  }

  /**
   * Sort the <code>ast</code> in place using function <code>Less(a, b)</code>.
   *
   * @param ast the AST will be sorted in place.
   * @return <code>true</code> if the sort algorithm was used; <code>false</code> otherwise
   */
  public static final boolean sortLess(IASTMutable ast) {
    return sort(ast, new Predicates.IsBinaryFalse(S.Less));
  }

  /**
   * Sort the <code>ast</code> in place using function <code>Order</code>. <b>Example:</b> suppose
   * the Symbol f has the attribute ISymbol.ORDERLESS <code>f(z,d,a,b) ==> f(a,b,d,z)</code>
   *
   * @param ast the AST will be sorted in place.
   * @return <code>true</code> if the sort algorithm was used; <code>false</code> otherwise
   */
  public static final boolean sort(IASTMutable ast) {
    final int astSize = ast.size();
    if (astSize > 2) {
      switch (astSize) {
        case 3:
          return sort2Args(ast, false);
        case 4:
          return sort3Args(ast, false);
        default:
          if (sort(ast, Comparators.CANONICAL_COMPARATOR)) {
            if (LOGGER.isDebugEnabled()) {
              checkCachedHashcode(ast);
            }
            return true;
          }
      }
    }
    return false;
  }

  /**
   * Sort the <code>ast</code> in place using function <code>Order</code>. <b>Example:</b> suppose
   * the Symbol f has the attribute ISymbol.ORDERLESS <code>f(z,d,a,b) ==> f(a,b,d,z)</code>
   *
   * @param ast the AST will be sorted in place.
   * @return <code>true</code> if the sort algorithm was used; <code>false</code> otherwise
   */
  public static final boolean sortWithFlags(IASTMutable ast) {
    if (ast.isEvalFlagOn(IAST.IS_SORTED)) {
      return false;
    }

    final int astSize = ast.size();
    if (astSize > 2) {
      switch (astSize) {
        case 3:
          return sort2Args(ast, true);
        case 4:
          return sort3Args(ast, true);
        default:
          if (sort(ast, Comparators.CANONICAL_COMPARATOR)) {
            ast.addEvalFlags(IAST.IS_SORTED);
            if (LOGGER.isDebugEnabled()) {
              checkCachedHashcode(ast);
            }
            return true;
          }
      }
    }
    ast.addEvalFlags(IAST.IS_SORTED);
    return false;
  }

  /**
   * Check if all arguments of the <code>ast</code> are sorted for the used <code>comparator</code>.
   *
   * @param ast
   * @param comparator a comparison function, which imposes a total ordering
   * @return
   */
  public static final boolean isSorted(final IAST ast, Comparator<IExpr> comparator) {
    return isSorted(ast, 1, comparator);
  }

  /**
   * Check if the arguments of <code>ast</code> starting from <code>fromPosition</code> are sorted
   * for the used <code>comparator</code>.
   *
   * @param ast
   * @param fromPosition start comparing the arguments from this position
   * @param comparator a comparison function, which imposes a total ordering
   * @return
   */
  public static final boolean isSorted(
      final IAST ast, int fromPosition, Comparator<IExpr> comparator) {
    if (ast.size() < fromPosition + 2) {
      return true;
    }
    IExpr previous = ast.get(fromPosition);
    for (int i = fromPosition + 1; i < ast.size(); i++) {
      final IExpr current = ast.get(i);
      if (comparator.compare(previous, current) > 0) {
        return false;
      }
      previous = current;
    }
    return true;
  }

  /**
   * Sort the <code>ast</code> in place using function <code>comparator#compare(a, b)</code>.
   * <b>Example:</b> suppose the Symbol f has the attribute ISymbol.ORDERLESS <code>
   * f(z,d,a,b) ==> f(a,b,d,z)</code>
   *
   * @param ast the AST will be sorted in place.
   * @return <code>true</code> if the sort algorithm was used; <code>false</code> otherwise
   */
  public static final boolean sort(final IASTMutable ast, Comparator<IExpr> comparator) {
    if (ast.isAssociation()) {
      throw new UnsupportedOperationException(
          "Sort(list, comparator) not implemented for associations.");
    }
    if (ast.size() > 2) {
      if (!isSorted(ast, comparator)) {
        final IExpr[] a = ast.toArray();
        int end = a.length;
        if (Config.FUZZ_TESTING) {
          try {
            Arrays.sort(a, 1, ast.size(), comparator);
            for (int j = 1; j < end; j++) {
              ast.set(j, a[j]);
            }
          } catch (java.lang.IllegalArgumentException iae) {
            // java.util.TimSort.mergeHi(TimSort.java:899) - Comparison method violates its general
            // contract!
            LOGGER.error(ast, iae);
            throw iae;
          }
        } else {
          Arrays.sort(a, 1, ast.size(), comparator);
          for (int j = 1; j < end; j++) {
            ast.set(j, a[j]);
          }
        }
        return true;
      }
    }
    return false;
  }

  /**
   * Sort an AST with 2 arguments using function <code>Order</code>.
   *
   * @param ast an ast with 2 arguments
   * @return
   */
  private static boolean sort2Args(final IASTMutable ast, final boolean setFlag) {
    IExpr temp;
    if (ast.arg1().compareTo(ast.arg2()) > 0) {
      // swap arguments
      temp = ast.arg2();
      ast.set(2, ast.arg1());
      ast.set(1, temp);
      if (setFlag) {
        ast.addEvalFlags(IAST.IS_SORTED);
      }
      if (LOGGER.isDebugEnabled()) {
        checkCachedHashcode(ast);
      }
      return true;
    }
    if (setFlag) {
      ast.addEvalFlags(IAST.IS_SORTED);
    }
    return false;
  }

  /**
   * Sort an AST with 3 arguments using function <code>Order</code>.
   *
   * @param ast an ast with 3 arguments
   * @return
   */
  private static boolean sort3Args(final IASTMutable ast, final boolean setFlag) {
    IExpr temp;
    // http://stackoverflow.com/questions/4793251/sorting-int-array-with-only-3-elements
    boolean evaled = false;
    if (ast.arg1().compareTo(ast.arg2()) > 0) {
      // swap arguments
      temp = ast.arg2();
      ast.set(2, ast.arg1());
      ast.set(1, temp);
      evaled = true;
    }
    if (ast.arg2().compareTo(ast.arg3()) > 0) {
      // swap arguments
      temp = ast.arg3();
      ast.set(3, ast.arg2());
      ast.set(2, temp);
      evaled = true;
      if (ast.arg1().compareTo(ast.arg2()) > 0) {
        // swap arguments
        temp = ast.arg2();
        ast.set(2, ast.arg1());
        ast.set(1, temp);
      }
    }
    if (setFlag) {
      ast.addEvalFlags(IAST.IS_SORTED);
    }
    if (evaled) {
      if (LOGGER.isDebugEnabled()) {
        checkCachedHashcode(ast);
      }
    }
    return evaled;
  }

  /**
   * Thread through all (sub-)lists in the arguments of the IAST (i.e. typically the ASTs head has
   * the attribute ISymbol.LISTABLE) example: <code>Sin[{2,x,Pi}] ==> {Sin[2],Sin[x],Sin[Pi]}</code>
   *
   * @param ast
   * @param listHead the lists head (typically <code>F.List</code>)
   * @param argHead the arguments head (typically <code>ast.head()</code>)
   * @param listLength the length of the list
   * @return the resulting ast with the <code>argHead</code> threaded into each ast argument.
   */
  public static IASTMutable threadList(
      final IAST ast, final IExpr listHead, final IExpr argHead, final int listLength) {
    if (listLength == 0) {
      return F.headAST0(listHead);
    }
    IASTMutable result = F.NIL;
    final int listSize = ast.size();
    for (int j = 1; j < listLength + 1; j++) {
      final IASTMutable subResult = F.astMutable(argHead, listSize - 1);
      for (int i = 1; i < listSize; i++) {
        if (listHead == S.List
            && //
            (ast.get(i).isList() || ast.get(i).isSparseArray())) {
          if (ast.get(i).isList()) {
            final IAST arg = (IAST) ast.get(i);
            subResult.set(i, arg.get(j));
          } else if (ast.get(i).isSparseArray()) {
            final ISparseArray arg = (ISparseArray) ast.get(i);
            subResult.set(i, arg.get(j));
            // subResult.set(i, Programming.sparsePart(arg, F.Part(arg, F.ZZ(j)), 2,
            // EvalEngine.get()));
          }
        } else if (listHead == S.SparseArray) {
          if (ast.get(i).isList() || ast.get(i).isSparseArray()) {
            if (ast.get(i).isList()) {
              final IAST arg = (IAST) ast.get(i);
              subResult.set(i, arg.get(j));
            } else if (ast.get(i).isSparseArray()) {
              final ISparseArray arg = (ISparseArray) ast.get(i);
              subResult.set(i, arg.get(j));
              // subResult.set(i, Programming.sparsePart(arg, F.Part(arg, F.ZZ(j)), 2,
              // EvalEngine.get()));
            }
          } else {
            subResult.set(i, ast.get(i));
          }
        } else if (ast.get(i).isAST(listHead)) {
          final IAST arg = (IAST) ast.get(i);
          subResult.set(i, arg.get(j));
        } else {
          subResult.set(i, ast.get(i));
        }
      }
      if (!result.isPresent()) {
        IExpr head = listHead == S.SparseArray ? S.List : listHead;
        switch (listLength) {
          case 1:
            result = F.unaryAST1(head, F.Slot1);
            break;
          case 2:
            result = F.binaryAST2(head, F.Slot1, F.Slot2);
            break;
          case 3:
            result = F.ternaryAST3(head, F.Slot1, F.Slot2, F.Slot3);
            break;
          default:
            result = F.astMutable(head, listLength);
        }
      }
      result.set(j, subResult);
    }
    if (listHead == S.SparseArray) {
      return F.unaryAST1(S.SparseArray, result);
    }
    return result;
  }

  private EvalAttributes() {}
}
