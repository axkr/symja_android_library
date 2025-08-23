package org.matheclipse.core.eval;

import java.util.HashSet;
import java.util.Set;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

public class CombinatoricUtil {

  private CombinatoricUtil() {
    // private constructor to avoid instantiation
  }

  public static int[] permute(IAST list, IAST permutationList) {
    IExpr temp = CombinatoricUtil.permutationCycles(permutationList);
    if (temp.isAST(S.Cycles, 2)) {
      IAST permutationMainList = (IAST) temp.first();
      IExpr permute =
          permute(list, permutationMainList, F.Permute(list, permutationList));
      return permute.toIntVector();
    }
    return null;
  }

  /**
   * Create a <code>Cycles({...})</code> expression from the permutation list.
   *
   * @param permList the permutation list
   * @param engine
   */
  public static IExpr permutationCycles(IAST permList) {
    return permutationCycles(permList, S.Cycles);
  }

  /**
   * Create a <code>Cycles({...})</code> expression from the permutation list.
   *
   * @param permList the permutation list
   * @param head define the head of the resulting expression (can be different from {@link S#Cycles}
   * @param engine
   * @return
   */
  public static IExpr permutationCycles(IAST permList, IExpr head) {
    if (permList.isEmptyList()) {
      return F.Cycles(F.CEmptyList);
    }

    int[] positions = CombinatoricUtil.isPermutationList(permList, false);
    if (positions == null) {
      // not a valid permutation list
      return F.NIL;
    }
    //
    // permList should be valid here
    //
    IASTAppendable mainList = F.ListAlloc(F.allocMin16(permList));
    IASTAppendable cycleList = F.NIL;
    int oldPosition = -1;
    int newPosition = -1;
    for (int i = 0; i < positions.length; i++) {
      newPosition = positions[i];
      if (newPosition < 0) {
        cycleList = F.NIL;
      } else {
        if (newPosition == i + 1) {
          positions[i] = -1;
          cycleList = F.NIL;
          if (head != S.Cycles) {
            // ignore singletons for S.Cycles; otherwise keep singletons:
            IAST singleton = F.list(F.ZZ(newPosition));
            mainList.append(singleton);
          }
        } else {
          positions[i] = -1;
          if (cycleList.isNIL()) {
            cycleList = F.ListAlloc(permList.argSize() < 16 ? permList.size() : 16);
            mainList.append(cycleList);
          }
          cycleList.append(newPosition);
          oldPosition = newPosition;
          while (positions[oldPosition - 1] > 0) {
            newPosition = positions[oldPosition - 1];
            if (newPosition < 0) {
              break;
            }

            positions[oldPosition - 1] = -1;
            if (positions[newPosition - 1] < 0) {
              cycleList.append(1, newPosition);
            } else {
              cycleList.append(newPosition);
            }
            oldPosition = newPosition;
          }
          cycleList = F.NIL;
        }
      }
    }

    return F.unaryAST1(head, mainList);
  }

  public static IExpr permute(IAST list1, IAST cyclesMainList, final IAST ast) {
    IASTMutable result = list1.copy();
    boolean changed = false;
  
    for (int j = 1; j < cyclesMainList.size(); j++) {
      IAST list = (IAST) cyclesMainList.get(j);
      for (int i = 1; i < list.size(); i++) {
        int fromPosition = list.get(i).toIntDefault();
        if (F.isNotPresent(fromPosition)) {
          return F.NIL;
        }
        if (fromPosition > list1.argSize()) {
          // Required length `1` is smaller than maximum `2` of support of `3`
          return Errors.printMessage(S.Permute, "lowlen",
              F.list(F.ZZ(list1.argSize()), list.get(i), ast));
        }
  
        int toPosition;
        if (i < list.size() - 1) {
          toPosition = list.get(i + 1).toIntDefault();
        } else {
          toPosition = list.arg1().toIntDefault();
        }
        if (F.isNotPresent(toPosition)) {
          return F.NIL;
        }
        if (toPosition > list1.argSize()) {
          // Required length `1` is smaller than maximum `2` of support of `3`
          return Errors.printMessage(S.Permute, "lowlen",
              F.list(F.ZZ(list1.argSize()), F.ZZ(toPosition), ast));
        }
        changed = true;
        IExpr from = list1.get(fromPosition);
        result.set(toPosition, from);
      }
    }
    return changed ? result : list1;
  }

  /**
   * Return the permutation list array if <code>permList</code> is a valid permutation list (i.e. a
   * rearrangement of the integers <code>{1..permList.argSize()}</code> ).
   *
   * @param permList a list which should be checked if it is a valid permutation list
   * @param quiet if <code>true</code> suppress the output of error messages
   * @param engine
   * @return <code>null</code> if <code>permList</code> is not a valid permutation list.
   */
  public static int[] isPermutationList(IAST permList, boolean quiet) {
    Set<IExpr> set = new HashSet<IExpr>();
    int[] positions = new int[permList.argSize()];
    for (int i = 1; i < permList.size(); i++) {
      IExpr arg = permList.get(i);
      if (arg.isInteger()) {
        int position = arg.toIntDefault();
        if (position < 1 || position > permList.argSize()) {
          if (!quiet) {
            // Invalid permutation list `1`.
            Errors.printMessage(S.Cycles, "permlist", F.list(permList));
          }
          return null;
        }
        if (set.contains(arg)) {
          // contains repeated integers.
          if (!quiet) {
            // Invalid permutation list `1`.
            Errors.printMessage(S.Cycles, "permlist", F.list(permList));
          }
          return null;
        }
        set.add(arg);
        positions[i - 1] = position;
      } else {
        if (!quiet) {
          // `1` is not a valid permutation.
          Errors.printMessage(S.Cycles, "perm", F.list(permList));
        }
        return null;
      }
    }
    return positions;
  }
}
