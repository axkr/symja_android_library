package org.matheclipse.core.integrate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;

/**
 * A system of linear equations whose coefficients are exact numbers (rational or complex rational),
 * solved by Gauss-Jordan elimination over sparse rows. The undetermined coefficients of an
 * integration ansatz give such systems with hundreds of equations, and most entries are zero; the
 * general <code>Solve</code> takes minutes for them.
 */
final class ExactLinearSystem {

  private ExactLinearSystem() {}

  /**
   * One solution of the linear <code>equations</code> in the <code>unknowns</code>, the unknowns
   * which the system leaves free set to zero.
   *
   * @param equations a list of <code>lhs == rhs</code> or of expressions equal to zero
   * @param unknowns the list of unknowns
   * @param engine the evaluation engine
   * @return a list of rules for the unknowns which are not free, the empty list if the system has
   *         no solution, or <code>null</code> if an equation is not linear in the unknowns or has a
   *         coefficient which is not an exact number
   */
  static IAST solve(IAST equations, IAST unknowns, EvalEngine engine) {
    final Map<IExpr, Integer> column = new HashMap<>();
    for (int j = 1; j < unknowns.size(); j++) {
      column.put(unknowns.get(j), j - 1);
    }
    final List<Map<Integer, INumber>> rows = new ArrayList<>(equations.argSize());
    final List<INumber> rhs = new ArrayList<>(equations.argSize());
    for (IExpr equation : equations) {
      final IExpr lhs =
          equation.isEqual() ? F.Subtract(equation.first(), equation.second()) : equation;
      final IExpr expanded = engine.evaluate(F.Expand(lhs));
      final Map<Integer, INumber> row = new HashMap<>();
      INumber constant = F.C0;
      for (IExpr term : expanded.isPlus() ? (IAST) expanded : F.list(expanded)) {
        if (term.isNumber()) {
          if (term.isInexactNumber()) {
            return null;
          }
          constant = constant.plus((INumber) term);
          continue;
        }
        INumber coefficient = F.C1;
        IExpr unknown = term;
        if (term.isTimes() && term.size() == 3 && term.first().isNumber()) {
          coefficient = (INumber) term.first();
          unknown = term.second();
        }
        final Integer j = column.get(unknown);
        if (j == null || coefficient.isInexactNumber()) {
          return null;
        }
        final INumber sum = row.containsKey(j) ? row.get(j).plus(coefficient) : coefficient;
        if (sum.isZero()) {
          row.remove(j);
        } else {
          row.put(j, sum);
        }
      }
      rows.add(row);
      rhs.add(constant.negate());
    }
    final int[] pivotRowOfColumn = new int[unknowns.argSize()];
    java.util.Arrays.fill(pivotRowOfColumn, -1);
    final boolean[] used = new boolean[rows.size()];
    for (int j = 0; j < unknowns.argSize(); j++) {
      int pivot = -1;
      for (int i = 0; i < rows.size(); i++) {
        if (!used[i] && rows.get(i).containsKey(j)) {
          // the shortest row keeps the fill-in small
          if (pivot < 0 || rows.get(i).size() < rows.get(pivot).size()) {
            pivot = i;
          }
        }
      }
      if (pivot < 0) {
        continue;
      }
      engine.checkTimeLimit();
      used[pivot] = true;
      pivotRowOfColumn[j] = pivot;
      final Map<Integer, INumber> pivotRow = rows.get(pivot);
      final INumber inverse = pivotRow.get(j).inverse();
      for (Map.Entry<Integer, INumber> entry : pivotRow.entrySet()) {
        entry.setValue(entry.getValue().times(inverse));
      }
      rhs.set(pivot, rhs.get(pivot).times(inverse));
      for (int i = 0; i < rows.size(); i++) {
        final Map<Integer, INumber> row = rows.get(i);
        final INumber factor = i == pivot ? null : row.get(j);
        if (factor == null) {
          continue;
        }
        final INumber minusFactor = factor.negate();
        for (Map.Entry<Integer, INumber> entry : pivotRow.entrySet()) {
          final int k = entry.getKey();
          final INumber delta = entry.getValue().times(minusFactor);
          final INumber old = row.get(k);
          final INumber sum = old == null ? delta : old.plus(delta);
          if (sum.isZero()) {
            row.remove(k);
          } else {
            row.put(k, sum);
          }
        }
        rhs.set(i, rhs.get(i).plus(rhs.get(pivot).times(minusFactor)));
      }
    }
    for (int i = 0; i < rows.size(); i++) {
      if (rows.get(i).isEmpty() && !rhs.get(i).isZero()) {
        // 0 == nonzero
        return F.CEmptyList;
      }
    }
    final IASTAppendable rules = F.ListAlloc(unknowns.argSize());
    for (int j = 0; j < unknowns.argSize(); j++) {
      final int i = pivotRowOfColumn[j];
      if (i >= 0) {
        // the free unknowns of the pivot row are zero
        rules.append(F.Rule(unknowns.get(j + 1), rhs.get(i)));
      }
    }
    return rules;
  }
}
