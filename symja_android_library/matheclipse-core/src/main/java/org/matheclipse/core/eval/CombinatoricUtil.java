package org.matheclipse.core.eval;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.matheclipse.core.builtin.Combinatoric;
import org.matheclipse.core.combinatoric.KSubsetsIterable;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class CombinatoricUtil {

  /**
   * Iterate over the lists of all k-combinations from a given list
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Combination">Combination</a>
   */
  public static final class KSubsetsList implements Iterable<IAST> {
  
    private class KSubsetsIterator implements Iterator<IAST> {
  
      private final Iterator<int[]> fIterable;
  
      private KSubsetsIterator() {
        this.fIterable = new KSubsetsIterable(fList.size() - fOffset, fK).iterator();
      }
  
      @Override
      public boolean hasNext() {
        return fIterable.hasNext();
      }
  
      /**
       * Get the index array for the next partition.
       *
       * @return <code>null</code> if no further index array could be generated
       */
      @Override
      public IAST next() {
        int j[] = fIterable.next();
        if (j == null) {
          return null;
        }
  
        IASTAppendable temp = fResultList.copyAppendable();
        return temp.appendArgs(0, fK, i -> {
          if (j.length > i && fList.size() > (j[i] + fOffset)) {
            return fList.get(j[i] + fOffset);
          }
          return F.NIL;
        });
      }
    }
    private final IAST fList;
    private final IAST fResultList;
  
    private final int fOffset;
  
    private final int fK;
  
    private KSubsetsList(final IAST list, final int k, IAST resultList, final int offset) {
      fList = list;
      fK = k;
      fResultList = resultList;
      fOffset = offset;
    }
  
    @Override
    public Iterator<IAST> iterator() {
      return new KSubsetsIterator();
    }
  }

  /**
   * The <code>cycles</code> expression is canonicalized by dropping empty and singleton cycles,
   * rotating each cycle so that the smallest position is at index 1.
   *
   * @param cycles <code>S.Cycles({{...}, ...})</code> expression
   * @param quiet if <code>true</code> suppress the output of error messages
   * @param engine
   * @return the canonicalized expression or otherwise <code>F.NIL</code>, if the <code>cycles
   *     </code> is no valid <code>Cycles</code> expression.
   */
  public static IAST canonicalizeCycles(final IAST cycles, boolean quiet, EvalEngine engine) {
    if (cycles.arg1().isList()) {
      IAST mainList = (IAST) cycles.arg1();
      if (mainList.isEmptyList()) {
        cycles.setEvalFlags(IAST.BUILT_IN_EVALED);
        // cycles.builtinEvaled();
        return F.NIL;
      }
      if (!mainList.isListOfLists()) {
        // normalize for possible SparseArray expressions
        IExpr temp = mainList.normal(false);
        if (!temp.isListOfLists()) {
          if (!quiet && temp.isAST()) {
            IAST list = (IAST) temp;
            for (int i = 0; i < list.size(); i++) {
              if (list.get(i).isNumber()) {
                // `1` is expected to contain a list of lists of integers.
                return Errors.printMessage(S.Cycles, "intpoint", F.list(cycles), engine);
              }
            }
          }
          return F.NIL;
        }
        mainList = (IAST) temp;
      }
  
      IASTAppendable result = F.ListAlloc(mainList.argSize());
      Set<IExpr> set = new HashSet<IExpr>();
      for (int j = 1; j < mainList.size(); j++) {
        IAST list = (IAST) mainList.get(j);
        for (int i = 1; i < list.size(); i++) {
          IExpr arg = list.get(i);
          if (arg.isInteger()) {
            if (!arg.isPositive()) {
              if (!quiet) {
                // `1` contains integers that are not positive.
                Errors.printMessage(S.Cycles, "pospoint", F.list(cycles), engine);
              }
              return F.NIL;
            }
            if (set.contains(arg)) {
              if (!quiet) {
                // `1` contains repeated integers.
                Errors.printMessage(S.Cycles, "reppoint", F.list(cycles), engine);
              }
              return F.NIL;
            }
            set.add(arg);
          } else {
            if (arg.isNumber()) {
              if (!quiet) {
                // `1` is expected to contain a list of lists of integers.
                Errors.printMessage(S.Cycles, "intpoint", F.list(cycles), engine);
              }
              return F.NIL;
            }
  
            // symbolic args => return unevaluated
            return F.NIL;
          }
        }
  
        if (list.size() > 2) {
          // drop empty and singleton cycles
  
          // rotate cycle by rotateLeftPositions so that the smallest position is at index 1
          int rotateLeftPositions = 0;
          IInteger value = (IInteger) list.get(1);
          for (int i = 1; i < list.size(); i++) {
            IInteger arg = (IInteger) list.get(i);
            if (value.isGT(arg)) {
              value = arg;
              rotateLeftPositions = i - 1;
            }
          }
          if (rotateLeftPositions > 0) {
            IASTAppendable newList = F.ListAlloc(list.size());
            result.append(list.rotateLeft(newList, rotateLeftPositions));
          } else {
            result.append(list);
          }
        }
      }
  
      EvalAttributes.sort(result, Comparators.LEXICAL_COMPARATOR);
      IAST resultCycles = F.Cycles(result);
      resultCycles.setEvalFlags(IAST.BUILT_IN_EVALED);
      // resultCycles.builtinEvaled();
      return resultCycles;
    }
    if (!quiet) {
      // `1` is expected to contain a list of lists of integers.
      Errors.printMessage(S.Cycles, "intpoint", F.list(cycles), engine);
    }
    return F.NIL;
  }

  /**
   * Check if cycles is a valid <code>Cycles({{...},{...},...})</code> , which contains disjoint
   * permutation cycles represented by integer lists.
   *
   * @param cycles
   * @param quiet if <code>true</code> suppress the output of error messages
   * @param engine
   * @return <code>F.NIL</code> if cycles is not a valid <code>Cycles({{...},{...},...})</code>
   *         expression
   */
  public static IAST checkCycles(final IAST cycles, boolean quiet, EvalEngine engine) {
    if (cycles.isAST(S.Cycles, 2)) {
      return CombinatoricUtil.canonicalizeCycles(cycles, quiet, engine);
    }
    return F.NIL;
  }

  /**
   * Determine the permutations list length from the Cycles first argument.
   *
   * @param cyclesMainList first arg of Cycles
   */
  public static int determineLengthFromCycles(IAST cyclesMainList) {
    int permutationsListLength = -1;
    for (int j = 1; j < cyclesMainList.size(); j++) {
      IAST list = (IAST) cyclesMainList.get(j);
      for (int i = 1; i < list.size(); i++) {
        int position = list.get(i).toIntDefault();
        if (position > permutationsListLength) {
          permutationsListLength = position;
        }
      }
    }
    return permutationsListLength;
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

  public static IAST permutationReplace(IAST list1, IAST mainList) {
    IASTMutable result = list1.copy();
  
    boolean changed = false;
    for (int i = 1; i < list1.size(); i++) {
      IExpr arg = list1.get(i);
      if (arg.isInteger()) {
        IInteger element = Combinatoric.PermutationReplace.replaceSingleElement(mainList, (IInteger) arg);
        if (!element.equals(arg)) {
          result.set(i, element);
          changed = true;
        }
      }
    }
    return changed ? result : list1;
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

  public static CombinatoricUtil.KSubsetsList subsets(final IAST list, final int k, IAST resultList,
      final int offset) {
    return new CombinatoricUtil.KSubsetsList(list, k, resultList, offset);
  }

  private CombinatoricUtil() {
    // private constructor to avoid instantiation
  }
}
