package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.sympy.combinatorics.NamedGroups;
import org.matheclipse.core.sympy.combinatorics.PermGroups;
import org.matheclipse.core.sympy.combinatorics.Permutations;
import org.matheclipse.core.sympy.exception.ValueError;

/**
 * Functions for permutations and permutation groups. The algorithms are implemented in the
 * package <code>org.matheclipse.core.sympy.combinatorics</code>.
 */
public class PermutationGroupFunctions {

  /** The maximum number of elements in <code>GroupElements, GroupMultiplicationTable</code> */
  private static final int MAX_ELEMENTS = 50000;

  /** The maximum number of elements in <code>GroupMultiplicationTable</code> */
  private static final int MAX_TABLE_SIZE = 2000;

  /** The maximum degree of a named group */
  private static final int MAX_DEGREE = 4096;

  private static class Initializer {

    private static void init() {
      S.InversePermutation.setEvaluator(new PermutationFunction(PermutationFunction.INVERSE));
      S.PermutationPower.setEvaluator(new PermutationFunction(PermutationFunction.POWER));
      S.PermutationOrder.setEvaluator(new PermutationFunction(PermutationFunction.ORDER));
      S.PermutationSupport.setEvaluator(new PermutationFunction(PermutationFunction.SUPPORT));
      S.PermutationLength.setEvaluator(new PermutationFunction(PermutationFunction.LENGTH));
      S.PermutationMax.setEvaluator(new PermutationFunction(PermutationFunction.MAX));
      S.PermutationMin.setEvaluator(new PermutationFunction(PermutationFunction.MIN));

      S.GroupOrder.setEvaluator(new GroupFunction(GroupFunction.ORDER));
      S.GroupElements.setEvaluator(new GroupFunction(GroupFunction.ELEMENTS));
      S.GroupGenerators.setEvaluator(new GroupFunction(GroupFunction.GENERATORS));
      S.GroupOrbits.setEvaluator(new GroupFunction(GroupFunction.ORBITS));
      S.GroupStabilizer.setEvaluator(new GroupFunction(GroupFunction.STABILIZER));
      S.GroupElementQ.setEvaluator(new GroupFunction(GroupFunction.ELEMENTQ));
      S.GroupMultiplicationTable.setEvaluator(new GroupFunction(GroupFunction.TABLE));
    }
  }

  /**
   * Convert a permutation list or a <code>Cycles({...})</code> expression into the 0-based array
   * form.
   *
   * @return <code>null</code> if the expression isn't a valid permutation
   */
  public static int[] toPermutation(IExpr expr, EvalEngine engine) {
    if (expr.isAST(S.Cycles, 2)) {
      if (expr.first().isEmptyList()) {
        return new int[0];
      }
      expr = engine.evaluate(F.unaryAST1(S.PermutationList, expr));
    }
    if (expr.isList()) {
      return Permutations.fromList((IAST) expr);
    }
    return null;
  }

  /**
   * Convert the 0-based array form into a <code>Cycles({...})</code> expression.
   */
  public static IExpr toCycles(int[] permutation, EvalEngine engine) {
    if (Permutations.isIdentity(permutation)) {
      return F.Cycles(F.CEmptyList);
    }
    return engine.evaluate(F.unaryAST1(S.PermutationCycles, Permutations.toList(permutation)));
  }

  private static IExpr toExpr(int[] permutation, boolean cycles, EvalEngine engine) {
    return cycles ? toCycles(permutation, engine) : Permutations.toList(permutation);
  }

  /**
   * Convert a group expression like <code>PermutationGroup({...}), SymmetricGroup(n),...</code>
   * into a permutation group.
   *
   * @return <code>null</code> if the expression isn't a valid group
   */
  public static PermGroups toGroup(IExpr expr, EvalEngine engine) {
    if (!expr.isAST()) {
      return null;
    }
    IAST group = (IAST) expr;
    try {
      if (group.isAST(S.PermutationGroup, 2) && group.arg1().isList()) {
        IAST gens = (IAST) group.arg1();
        List<int[]> list = new ArrayList<int[]>(gens.argSize());
        for (int i = 1; i < gens.size(); i++) {
          int[] permutation = toPermutation(gens.get(i), engine);
          if (permutation == null) {
            return null;
          }
          list.add(permutation);
        }
        if (list.isEmpty()) {
          list.add(new int[0]);
        }
        return new PermGroups(list);
      }
      if (group.isAST(S.AbelianGroup, 2) && group.arg1().isList()) {
        IAST orders = (IAST) group.arg1();
        int[] cyclicOrders = new int[orders.argSize()];
        int degree = 0;
        for (int i = 1; i < orders.size(); i++) {
          cyclicOrders[i - 1] = orders.get(i).toIntDefault();
          if (cyclicOrders[i - 1] < 1) {
            return null;
          }
          degree += cyclicOrders[i - 1];
          if (degree > MAX_DEGREE) {
            return null;
          }
        }
        return NamedGroups.abelianGroup(cyclicOrders);
      }
      if (group.isAST1()) {
        int n = group.arg1().toIntDefault();
        if (n < 1 || n > MAX_DEGREE) {
          return null;
        }
        if (group.head() == S.SymmetricGroup) {
          return NamedGroups.symmetricGroup(n);
        }
        if (group.head() == S.AlternatingGroup) {
          return NamedGroups.alternatingGroup(n);
        }
        if (group.head() == S.CyclicGroup) {
          return NamedGroups.cyclicGroup(n);
        }
        if (group.head() == S.DihedralGroup) {
          if (n == 2) {
            // WMA uses the generators Cycles({{1,2}}) and Cycles({{3,4}})
            List<int[]> list = new ArrayList<int[]>(2);
            list.add(new int[] {1, 0, 2, 3});
            list.add(new int[] {0, 1, 3, 2});
            return new PermGroups(list, 4);
          }
          return NamedGroups.dihedralGroup(n);
        }
      }
    } catch (ValueError ve) {
      //
    }
    return null;
  }

  private static IExpr toGroupExpr(List<int[]> generators, EvalEngine engine) {
    IASTAppendable gens = F.ListAlloc(generators.size());
    for (int[] generator : generators) {
      if (!Permutations.isIdentity(generator)) {
        gens.append(toCycles(generator, engine));
      }
    }
    return F.unaryAST1(S.PermutationGroup, gens);
  }

  private static final class PermutationFunction extends AbstractFunctionEvaluator {
    static final int INVERSE = 0;
    static final int POWER = 1;
    static final int ORDER = 2;
    static final int SUPPORT = 3;
    static final int LENGTH = 4;
    static final int MAX = 5;
    static final int MIN = 6;

    private final int kind;

    PermutationFunction(int kind) {
      this.kind = kind;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      final boolean cycles = arg1.isAST(S.Cycles, 2);
      if (!cycles && !arg1.isList()) {
        return F.NIL;
      }
      int[] permutation = toPermutation(arg1, engine);
      if (permutation == null) {
        // `1` is not a valid permutation.
        return Errors.printMessage(ast.topHead(), "perm", F.List(arg1), engine);
      }
      switch (kind) {
        case INVERSE:
          return toExpr(Permutations.inverse(permutation), cycles, engine);
        case POWER:
          if (ast.arg2().isInteger()) {
            return toExpr(
                Permutations.power(permutation, ((org.matheclipse.core.interfaces.IInteger) ast
                    .arg2()).toBigNumerator()),
                cycles, engine);
          }
          return F.NIL;
        case ORDER:
          return Permutations.order(permutation);
        case SUPPORT: {
          int[] support = Permutations.support(permutation);
          IASTAppendable list = F.ListAlloc(support.length);
          for (int i = 0; i < support.length; i++) {
            list.append(F.ZZ(support[i] + 1));
          }
          return list;
        }
        case LENGTH:
          return F.ZZ(Permutations.length(permutation));
        case MAX: {
          int max = Permutations.max(permutation);
          return max < 0 ? F.C0 : F.ZZ(max + 1);
        }
        case MIN: {
          int min = Permutations.min(permutation);
          return min < 0 ? F.CInfinity : F.ZZ(min + 1);
        }
        default:
          return F.NIL;
      }
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return kind == POWER ? ARGS_2_2 : ARGS_1_1;
    }
  }

  private static final class GroupFunction extends AbstractFunctionEvaluator {
    static final int ORDER = 0;
    static final int ELEMENTS = 1;
    static final int GENERATORS = 2;
    static final int ORBITS = 3;
    static final int STABILIZER = 4;
    static final int ELEMENTQ = 5;
    static final int TABLE = 6;

    private final int kind;

    GroupFunction(int kind) {
      this.kind = kind;
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      PermGroups group = toGroup(ast.arg1(), engine);
      if (group == null) {
        return kind == ELEMENTQ && ast.arg1().isAST(S.PermutationGroup) ? S.False : F.NIL;
      }
      try {
        switch (kind) {
          case ORDER:
            return group.order();
          case GENERATORS: {
            List<int[]> generators = group.generators();
            if (ast.arg1().isAST(S.SymmetricGroup, 2) && generators.size() == 2) {
              // WMA lists the transposition first
              generators = Arrays.asList(generators.get(1), generators.get(0));
            } else if (ast.arg1().isAST(S.DihedralGroup, 2) && generators.size() == 2
                && ast.arg1().first().toIntDefault() >= 3) {
              // WMA lists the reflection, which fixes the point 1, first
              final int n = group.degree();
              int[] reflection = new int[n];
              for (int i = 0; i < n; i++) {
                reflection[i] = (n - i) % n;
              }
              generators = Arrays.asList(reflection, generators.get(0));
            }
            IASTAppendable list = F.ListAlloc(generators.size());
            for (int[] generator : generators) {
              if (!Permutations.isIdentity(generator)) {
                list.append(toCycles(generator, engine));
              }
            }
            return list;
          }
          case ELEMENTS: {
            List<int[]> elements = group.elements(MAX_ELEMENTS);
            if (elements == null) {
              return F.NIL;
            }
            if (ast.isAST2()) {
              // only the elements at the given positions
              IExpr positions = ast.arg2();
              if (positions.isList()) {
                IAST list = (IAST) positions;
                IASTAppendable result = F.ListAlloc(list.argSize());
                for (int i = 1; i < list.size(); i++) {
                  int position = list.get(i).toIntDefault();
                  if (position < 1 || position > elements.size()) {
                    return F.NIL;
                  }
                  result.append(toCycles(elements.get(position - 1), engine));
                }
                return result;
              }
              return F.NIL;
            }
            IASTAppendable result = F.ListAlloc(elements.size());
            for (int[] element : elements) {
              result.append(toCycles(element, engine));
            }
            return result;
          }
          case ORBITS:
            return orbits(ast, group);
          case STABILIZER:
            return stabilizer(ast, group, engine);
          case ELEMENTQ: {
            int[] permutation = toPermutation(ast.arg2(), engine);
            if (permutation == null) {
              return S.False;
            }
            return F.booleSymbol(group.contains(permutation));
          }
          case TABLE: {
            List<int[]> elements = group.elements(MAX_TABLE_SIZE);
            if (elements == null) {
              return F.NIL;
            }
            final int n = elements.size();
            IASTAppendable table = F.ListAlloc(n);
            for (int i = 0; i < n; i++) {
              IASTAppendable row = F.ListAlloc(n);
              for (int j = 0; j < n; j++) {
                int[] product = Permutations.mul(elements.get(i), elements.get(j));
                int index = java.util.Collections.binarySearch(elements, product, Arrays::compare);
                row.append(F.ZZ(index + 1));
              }
              table.append(row);
            }
            return table;
          }
          default:
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(ast.topHead(), rex);
      }
      return F.NIL;
    }

    private static IAST toOrbit(int[] orbit) {
      IASTAppendable list = F.ListAlloc(orbit.length);
      for (int i = 0; i < orbit.length; i++) {
        list.append(F.ZZ(orbit[i] + 1));
      }
      return list;
    }

    private static IExpr orbits(final IAST ast, PermGroups group) {
      if (ast.isAST2()) {
        if (!ast.arg2().isList()) {
          return F.NIL;
        }
        IAST points = (IAST) ast.arg2();
        IASTAppendable result = F.ListAlloc(points.argSize());
        boolean[] seen = new boolean[group.degree()];
        for (int i = 1; i < points.size(); i++) {
          int point = points.get(i).toIntDefault();
          if (point < 1) {
            return F.NIL;
          }
          if (point > group.degree()) {
            // a fixed point of the group
            IAST orbit = F.List(points.get(i));
            if (result.indexOf(orbit) < 0) {
              result.append(orbit);
            }
            continue;
          }
          if (!seen[point - 1]) {
            int[] orbit = group.orbit(point - 1);
            for (int j = 0; j < orbit.length; j++) {
              seen[orbit[j]] = true;
            }
            result.append(toOrbit(orbit));
          }
        }
        return result;
      }
      List<int[]> orbits = group.orbits();
      IASTAppendable result = F.ListAlloc(orbits.size());
      for (int[] orbit : orbits) {
        // WMA also lists the fixed points up to the largest moved point
        result.append(toOrbit(orbit));
      }
      return result;
    }

    private static IExpr stabilizer(final IAST ast, PermGroups group, EvalEngine engine) {
      IAST points = ast.arg2().isList() ? (IAST) ast.arg2() : F.List(ast.arg2());
      for (int i = 1; i < points.size(); i++) {
        int point = points.get(i).toIntDefault();
        if (point < 1) {
          return F.NIL;
        }
        if (point <= group.degree()) {
          group = group.stabilizer(point - 1);
        }
      }
      return toGroupExpr(group.generators(), engine);
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      switch (kind) {
        case STABILIZER:
        case ELEMENTQ:
          return ARGS_2_2;
        case ORBITS:
        case ELEMENTS:
          return ARGS_1_2;
        default:
          return ARGS_1_1;
      }
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private PermutationGroupFunctions() {}
}
