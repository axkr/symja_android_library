package org.matheclipse.ortools.sat.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.matheclipse.ortools.sat.ConstraintProto;
import org.matheclipse.ortools.sat.CpModelProto;

/**
 * Detects interchangeable columns of a matrix of Boolean variables (the typical symmetry of bin
 * packing and assignment models with identical bins) so that the branch and bound can break it
 * with lexicographic ordering constraints between consecutive columns. This replaces, for this
 * common case only, the symmetry detection of the C++ CP-SAT solver.
 *
 * <p>
 * The rows are disjoint at_most_one / exactly_one constraints of equal length over positive
 * literals; the columns are the classes of row variables linked by the linear constraints.
 * Columns are interchangeable if every variable domain is preserved and swapping two consecutive
 * columns maps every constraint (only Boolean and linear constraints are supported by the check)
 * and the objective onto themselves; then the whole symmetric group acts on the columns and
 * {@code col[j] >=lex col[j+1]} keeps at least one optimal solution. The engine only uses this for
 * models with an objective, never when solutions are enumerated.
 */
final class SymmetryDetector {
  private SymmetryDetector() {}

  /** Returns the columns (each listing its variables row by row) or null if none were found. */
  static int[][] detectColumns(CpModelProto model) {
    if (model.getAssumptionsCount() > 0) {
      return null;
    }
    // rows
    List<int[]> rows = new ArrayList<>();
    Map<Integer, Integer> rowOfVar = new HashMap<>();
    int k = -1;
    for (ConstraintProto ct : model.getConstraintsList()) {
      List<Integer> lits;
      if (ct.getConstraintCase() == ConstraintProto.ConstraintCase.AT_MOST_ONE) {
        lits = ct.getAtMostOne().getLiteralsList();
      } else if (ct.getConstraintCase() == ConstraintProto.ConstraintCase.EXACTLY_ONE) {
        lits = ct.getExactlyOne().getLiteralsList();
      } else {
        continue;
      }
      if (ct.getEnforcementLiteralCount() > 0 || lits.size() < 2) {
        continue;
      }
      if (k < 0) {
        k = lits.size();
      }
      if (lits.size() != k) {
        continue;
      }
      boolean ok = true;
      for (int l : lits) {
        if (l < 0 || rowOfVar.containsKey(l)) {
          ok = false;
          break;
        }
      }
      if (!ok) {
        continue;
      }
      int[] row = new int[k];
      for (int i = 0; i < k; i++) {
        row[i] = lits.get(i);
        rowOfVar.put(row[i], rows.size());
      }
      rows.add(row);
    }
    if (rows.size() < 2 || k < 2) {
      return null;
    }
    // columns from the linear constraints: union-find over row variables
    Map<Integer, Integer> parent = new HashMap<>();
    for (int v : rowOfVar.keySet()) {
      parent.put(v, v);
    }
    for (ConstraintProto ct : model.getConstraintsList()) {
      if (ct.getConstraintCase() != ConstraintProto.ConstraintCase.LINEAR) {
        continue;
      }
      int first = -1;
      for (int v : ct.getLinear().getVarsList()) {
        if (!rowOfVar.containsKey(v)) {
          continue;
        }
        if (first < 0) {
          first = v;
        } else {
          union(parent, first, v);
        }
      }
    }
    Map<Integer, List<Integer>> groups = new HashMap<>();
    for (int v : rowOfVar.keySet()) {
      groups.computeIfAbsent(find(parent, v), x -> new ArrayList<>()).add(v);
    }
    if (groups.size() != k) {
      return null;
    }
    // each group has exactly one variable per row; order the columns by their first row
    int numRows = rows.size();
    int[][] columns = new int[k][];
    int[] rowZero = rows.get(0);
    Map<Integer, Integer> columnOfRoot = new HashMap<>();
    for (int j = 0; j < k; j++) {
      columnOfRoot.put(find(parent, rowZero[j]), j);
      columns[j] = new int[numRows];
      Arrays.fill(columns[j], -1);
    }
    if (columnOfRoot.size() != k) {
      return null;
    }
    for (Map.Entry<Integer, Integer> e : rowOfVar.entrySet()) {
      Integer j = columnOfRoot.get(find(parent, e.getKey()));
      if (j == null || columns[j][e.getValue()] != -1) {
        return null;
      }
      columns[j][e.getValue()] = e.getKey();
    }
    // check that swapping consecutive columns is an automorphism of the model
    for (int j = 0; j + 1 < k; j++) {
      for (int i = 0; i < numRows; i++) {
        if (!model.getVariables(columns[j][i]).getDomainList()
            .equals(model.getVariables(columns[j + 1][i]).getDomainList())) {
          return null;
        }
      }
    }
    Set<String> normalized = normalizedConstraints(model, null);
    if (normalized == null) {
      return null;
    }
    String objective = normalizedObjective(model, null);
    for (int j = 0; j + 1 < k; j++) {
      Map<Integer, Integer> perm = new HashMap<>();
      for (int i = 0; i < numRows; i++) {
        perm.put(columns[j][i], columns[j + 1][i]);
        perm.put(columns[j + 1][i], columns[j][i]);
      }
      Set<String> permuted = normalizedConstraints(model, perm);
      if (!normalized.equals(permuted) || !objective.equals(normalizedObjective(model, perm))) {
        return null;
      }
    }
    return columns;
  }

  private static int find(Map<Integer, Integer> parent, int v) {
    int r = v;
    while (parent.get(r) != r) {
      r = parent.get(r);
    }
    int x = v;
    while (parent.get(x) != r) {
      int next = parent.get(x);
      parent.put(x, r);
      x = next;
    }
    return r;
  }

  private static void union(Map<Integer, Integer> parent, int a, int b) {
    int ra = find(parent, a);
    int rb = find(parent, b);
    if (ra != rb) {
      parent.put(ra, rb);
    }
  }

  private static int map(Map<Integer, Integer> perm, int ref) {
    if (perm == null) {
      return ref;
    }
    if (ref >= 0) {
      return perm.getOrDefault(ref, ref);
    }
    int v = -ref - 1;
    return -perm.getOrDefault(v, v) - 1;
  }

  // a canonical text of every constraint after renaming the variables; null if a constraint type
  // is not supported by the check
  private static Set<String> normalizedConstraints(CpModelProto model, Map<Integer, Integer> perm) {
    Set<String> result = new HashSet<>();
    Map<String, Integer> multiplicity = new HashMap<>();
    for (ConstraintProto ct : model.getConstraintsList()) {
      StringBuilder sb = new StringBuilder();
      int[] enf = new int[ct.getEnforcementLiteralCount()];
      for (int i = 0; i < enf.length; i++) {
        enf[i] = map(perm, ct.getEnforcementLiteral(i));
      }
      Arrays.sort(enf);
      sb.append(Arrays.toString(enf)).append(ct.getConstraintCase()).append(':');
      switch (ct.getConstraintCase()) {
        case AT_MOST_ONE:
        case EXACTLY_ONE:
        case BOOL_OR:
        case BOOL_AND:
        case BOOL_XOR: {
          List<Integer> lits = ct.getConstraintCase() == ConstraintProto.ConstraintCase.AT_MOST_ONE
              ? ct.getAtMostOne().getLiteralsList()
              : ct.getConstraintCase() == ConstraintProto.ConstraintCase.EXACTLY_ONE
                  ? ct.getExactlyOne().getLiteralsList()
                  : ct.getConstraintCase() == ConstraintProto.ConstraintCase.BOOL_OR
                      ? ct.getBoolOr().getLiteralsList()
                      : ct.getConstraintCase() == ConstraintProto.ConstraintCase.BOOL_AND
                          ? ct.getBoolAnd().getLiteralsList()
                          : ct.getBoolXor().getLiteralsList();
          int[] l = new int[lits.size()];
          for (int i = 0; i < l.length; i++) {
            l[i] = map(perm, lits.get(i));
          }
          Arrays.sort(l);
          sb.append(Arrays.toString(l));
          break;
        }
        case LINEAR: {
          int n = ct.getLinear().getVarsCount();
          long[][] terms = new long[n][2];
          for (int i = 0; i < n; i++) {
            terms[i][0] = map(perm, ct.getLinear().getVars(i));
            terms[i][1] = ct.getLinear().getCoeffs(i);
          }
          Arrays.sort(terms, (a, b) -> a[0] != b[0] ? Long.compare(a[0], b[0])
              : Long.compare(a[1], b[1]));
          for (long[] t : terms) {
            sb.append(t[0]).append('*').append(t[1]).append(',');
          }
          sb.append(ct.getLinear().getDomainList());
          break;
        }
        default:
          return null;
      }
      String key = sb.toString();
      int count = multiplicity.merge(key, 1, Integer::sum);
      result.add(key + "#" + count);
    }
    return result;
  }

  private static String normalizedObjective(CpModelProto model, Map<Integer, Integer> perm) {
    if (model.hasFloatingPointObjective()) {
      Map<Integer, Double> terms = new java.util.TreeMap<>();
      for (int i = 0; i < model.getFloatingPointObjective().getVarsCount(); i++) {
        terms.merge(map(perm, model.getFloatingPointObjective().getVars(i)),
            model.getFloatingPointObjective().getCoeffs(i), Double::sum);
      }
      return terms.toString();
    }
    Map<Integer, Long> terms = new java.util.TreeMap<>();
    for (int i = 0; i < model.getObjective().getVarsCount(); i++) {
      terms.merge(map(perm, model.getObjective().getVars(i)), model.getObjective().getCoeffs(i),
          Long::sum);
    }
    return terms.toString();
  }

  /** {@code a >=lex b} for two vectors of Boolean variables. */
  static final class LexGreaterEqual extends Propagator {
    final int[] a;
    final int[] b;
    private final int[] watched;

    LexGreaterEqual(int[] a, int[] b) {
      this.a = a;
      this.b = b;
      this.watched = concat(a, b);
    }

    @Override
    int[] watchedVars() {
      return watched;
    }

    @Override
    boolean propagate(DomainStore s) {
      for (int i = 0; i < a.length; i++) {
        int x = a[i];
        int y = b[i];
        if (s.isFixed(x) && s.isFixed(y)) {
          long vx = s.value(x);
          long vy = s.value(y);
          if (vx == vy) {
            continue;
          }
          return vx > vy;
        }
        // equal prefix: a[i] >= b[i]
        return s.setLb(x, s.lb[y]) && s.setUb(y, s.ub[x]);
      }
      return true;
    }
  }
}
