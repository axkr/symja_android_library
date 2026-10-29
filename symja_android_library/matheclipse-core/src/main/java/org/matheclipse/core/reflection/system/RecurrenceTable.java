package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * <code>RecurrenceTable(eqns, a(n), {n, nmin, nmax})
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * generates a list of values of <code>a(n)</code> for successive <code>n</code> based on solving
 * the recurrence equations <code>eqns</code>.
 * </p>
 * </blockquote>
 *
 * <pre>
 * <code>RecurrenceTable(eqns, {a, b, ...}, {n, nmin, nmax})
 * </code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * generates a list of the values <code>{a(n), b(n), ...}</code> of a system of recurrences.
 * </p>
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; RecurrenceTable({a(n+1)==3*a(n), a(1)==7}, a(n), {n, 1, 5})
 * {7,21,63,189,567}
 * </code>
 * </pre>
 */
public class RecurrenceTable extends AbstractFunctionEvaluator {

  /** The maximum number of computed values */
  private static final int MAX_VALUES = 100000;

  /**
   * A term <code>a(n+k)</code> (<code>relative</code>) or <code>a(k)</code> of a recurrence, which
   * is replaced by the symbol <code>dummy</code>.
   */
  private static final class Term {
    final int head;
    final int k;
    final boolean relative;
    final ISymbol dummy;

    Term(int head, int k, boolean relative) {
      this.head = head;
      this.k = k;
      this.relative = relative;
      this.dummy = F.Dummy();
    }

    int index(int nValue) {
      return relative ? nValue + k : k;
    }
  }

  /**
   * A recurrence equation with its terms replaced by dummy symbols, which determines the head
   * <code>head</code> at the shift <code>top</code>.
   */
  private static final class Recurrence {
    final IExpr equation;
    final List<Term> terms;
    int head = -1;
    int top;
    /** the right-hand side if the equation is explicit in <code>head(n+top)</code> */
    IExpr explicit = F.NIL;
    Term unknown;

    Recurrence(IExpr equation, List<Term> terms) {
      this.equation = equation;
      this.terms = terms;
    }
  }

  public RecurrenceTable() {}

  private static int headIndex(IExpr expr, IAST heads) {
    if (expr.isAST1()) {
      for (int i = 1; i < heads.size(); i++) {
        if (expr.head().equals(heads.get(i))) {
          return i - 1;
        }
      }
    }
    return -1;
  }

  /**
   * Replace the terms <code>a(n+k)</code> and <code>a(k)</code> of the heads by dummy symbols.
   *
   * @return {@link F#NIL} if a term has an argument which is neither
   */
  private static IExpr replaceTerms(IExpr equation, IAST heads, IExpr n, List<Term> terms,
      EvalEngine engine) {
    Map<IExpr, Term> seen = new HashMap<IExpr, Term>();
    boolean[] valid = new boolean[] {true};
    IExpr result = F.subst(equation, x -> {
      int h = headIndex(x, heads);
      if (h < 0) {
        return F.NIL;
      }
      Term term = seen.get(x);
      if (term == null) {
        IExpr arg = x.first();
        boolean relative = !arg.isFree(n);
        int k = relative ? engine.evaluate(F.Subtract(arg, n)).toIntDefault() : arg.toIntDefault();
        if (k == Integer.MIN_VALUE) {
          valid[0] = false;
          return F.NIL;
        }
        term = new Term(h, k, relative);
        seen.put(x, term);
        terms.add(term);
      }
      return term.dummy;
    });
    return valid[0] ? result : F.NIL;
  }

  /**
   * Choose the head a recurrence determines: the term which stands alone on one side, or else the
   * term with the largest shift of a head no other recurrence determines.
   */
  private static boolean assignHead(Recurrence recurrence, boolean[] assigned) {
    IExpr lhs = recurrence.equation.first();
    IExpr rhs = recurrence.equation.second();
    for (int side = 0; side < 2; side++) {
      IExpr single = side == 0 ? lhs : rhs;
      IExpr other = side == 0 ? rhs : lhs;
      for (Term term : recurrence.terms) {
        if (term.relative && single.equals(term.dummy) && !assigned[term.head]
            && other.isFree(term.dummy) && isTopShift(recurrence, term)) {
          assign(recurrence, term, assigned);
          recurrence.explicit = other;
          return true;
        }
      }
    }
    Term best = null;
    for (Term term : recurrence.terms) {
      if (term.relative && !assigned[term.head] && (best == null || term.k > best.k)) {
        best = term;
      }
    }
    if (best == null || !isTopShift(recurrence, best)) {
      return false;
    }
    assign(recurrence, best, assigned);
    return true;
  }

  private static boolean isTopShift(Recurrence recurrence, Term term) {
    for (Term t : recurrence.terms) {
      if (t.relative && t.head == term.head && t.k > term.k) {
        return false;
      }
    }
    return true;
  }

  private static void assign(Recurrence recurrence, Term term, boolean[] assigned) {
    recurrence.head = term.head;
    recurrence.top = term.k;
    recurrence.unknown = term;
    assigned[term.head] = true;
  }

  /**
   * The values of the initial conditions. A condition whose one side is a term
   * <code>a(k)</code> is evaluated as soon as the terms of its other side have values, so
   * <code>a(2)==a(1)+1</code> may refer to other conditions; the rest are solved together.
   *
   * @return <code>false</code> if the conditions don't determine their terms
   */
  private static boolean initialValues(List<IExpr> conditions, IAST heads,
      List<Map<Integer, IExpr>> values, EvalEngine engine) {
    List<IExpr> open = new ArrayList<IExpr>(conditions);
    boolean progress = true;
    while (progress && !open.isEmpty()) {
      progress = false;
      for (int i = 0; i < open.size(); i++) {
        IExpr condition = open.get(i);
        for (int side = 1; side <= 2; side++) {
          IExpr single = condition.getAt(side);
          IExpr other = condition.getAt(3 - side);
          int h = headIndex(single, heads);
          int k = h < 0 ? Integer.MIN_VALUE : single.first().toIntDefault();
          if (k == Integer.MIN_VALUE) {
            continue;
          }
          IExpr value = knownTerms(other, heads, values);
          if (value.isPresent() && !values.get(h).containsKey(k)) {
            values.get(h).put(k, engine.evaluate(value));
            open.remove(i--);
            progress = true;
            break;
          }
        }
      }
    }
    if (open.isEmpty()) {
      return true;
    }
    // conditions which are no explicit assignments
    IASTAppendable equations = F.ListAlloc(open.size());
    for (IExpr condition : open) {
      equations.append(knownTerms(condition, heads, values));
    }
    IASTAppendable unknowns = F.ListAlloc();
    for (IExpr condition : open) {
      collectTerms(condition, heads, unknowns);
    }
    if (unknowns.isEmpty() || unknowns.argSize() != equations.argSize()) {
      return false;
    }
    IExpr solution = engine.evaluate(F.Solve(equations, unknowns));
    if (!solution.isListOfLists() || solution.argSize() < 1) {
      return false;
    }
    for (IExpr rule : (IAST) solution.first()) {
      IExpr term = rule.first();
      int h = headIndex(term, heads);
      if (h < 0 || !rule.second().isFree(x -> headIndex(x, heads) >= 0, true)) {
        return false;
      }
      values.get(h).put(term.first().toIntDefault(), rule.second());
    }
    return true;
  }

  /** Append the distinct terms <code>a(k)</code> of the heads in <code>expr</code>. */
  private static void collectTerms(IExpr expr, IAST heads, IASTAppendable terms) {
    if (headIndex(expr, heads) >= 0) {
      if (expr.first().isInteger() && !terms.contains(expr)) {
        terms.append(expr);
      }
    } else if (expr.isAST()) {
      for (IExpr arg : (IAST) expr) {
        collectTerms(arg, heads, terms);
      }
    }
  }

  /**
   * Replace the terms <code>a(k)</code> by their values.
   *
   * @return {@link F#NIL} if a term has no value yet
   */
  private static IExpr knownTerms(IExpr expr, IAST heads, List<Map<Integer, IExpr>> values) {
    boolean[] known = new boolean[] {true};
    IExpr result = F.subst(expr, x -> {
      int h = headIndex(x, heads);
      if (h < 0) {
        return F.NIL;
      }
      int k = x.first().toIntDefault();
      IExpr value = k == Integer.MIN_VALUE ? null : values.get(h).get(k);
      if (value == null) {
        known[0] = false;
        return F.NIL;
      }
      return value;
    });
    return known[0] ? result : F.NIL;
  }

  /**
   * The value of <code>recurrence.head(index)</code> from the recurrence: directly from the
   * explicit right-hand side, or else by solving the equation for it.
   *
   * @return {@link F#NIL} if a value it depends on isn't known yet, or the equation has no
   *         solution
   */
  private static IExpr step(Recurrence recurrence, int index, IExpr n,
      List<Map<Integer, IExpr>> values, EvalEngine engine) {
    final int nValue = index - recurrence.top;
    Map<IExpr, IExpr> substitution = new HashMap<IExpr, IExpr>();
    substitution.put(n, F.ZZ(nValue));
    for (Term term : recurrence.terms) {
      if (term == recurrence.unknown) {
        continue;
      }
      int j = term.index(nValue);
      if (term.head == recurrence.head && j == index) {
        return F.NIL;
      }
      IExpr value = values.get(term.head).get(j);
      if (value == null) {
        return F.NIL;
      }
      substitution.put(term.dummy, value);
    }
    if (recurrence.explicit.isPresent()) {
      return engine.evaluate(F.subst(recurrence.explicit, substitution));
    }
    IExpr equation = engine.evaluate(F.subst(recurrence.equation, substitution));
    IExpr solution = engine.evaluate(F.Solve(equation, recurrence.unknown.dummy));
    if (!solution.isListOfLists() || solution.argSize() < 1
        || !solution.first().first().isRuleAST()) {
      return F.NIL;
    }
    return solution.first().first().second();
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = engine.evaluate(ast.arg1());
    IAST equations;
    if (arg1.isList() || arg1.isAnd()) {
      equations = (IAST) arg1;
    } else {
      equations = F.List(arg1);
    }
    IExpr function = ast.arg2();
    if (!ast.arg3().isList()) {
      return F.NIL;
    }
    IAST iterator = (IAST) ast.arg3();
    if (iterator.argSize() < 2 || iterator.argSize() > 3) {
      return F.NIL;
    }
    final IExpr n = iterator.arg1();
    if (!n.isSymbol()) {
      return F.NIL;
    }
    // the heads a, or a(n) with the iterator variable n
    IAST functions = function.isList() ? (IAST) function : F.List(function);
    IASTAppendable heads = F.ListAlloc(functions.argSize());
    for (IExpr f : functions) {
      if (f.isSymbol()) {
        heads.append(f);
      } else if (f.isAST1() && f.head().isSymbol() && f.first().equals(n)) {
        heads.append(f.head());
      } else {
        return F.NIL;
      }
    }
    if (heads.isEmpty()) {
      return F.NIL;
    }
    final int headCount = heads.argSize();
    final int nMin = iterator.isAST2() ? 1 : engine.evaluate(iterator.arg2()).toIntDefault();
    final int nMax = engine.evaluate(iterator.last()).toIntDefault();
    if (nMin == Integer.MIN_VALUE || nMax == Integer.MIN_VALUE) {
      return F.NIL;
    }
    if (nMax < nMin) {
      return F.CEmptyList;
    }

    List<Recurrence> recurrences = new ArrayList<Recurrence>();
    List<IExpr> conditions = new ArrayList<IExpr>();
    for (int i = 1; i < equations.size(); i++) {
      IExpr equation = equations.get(i);
      if (!equation.isEqual() || !equation.isAST2()) {
        return F.NIL;
      }
      if (equation.isFree(n)) {
        conditions.add(equation);
      } else {
        List<Term> terms = new ArrayList<Term>();
        IExpr replaced = replaceTerms(equation, heads, n, terms, engine);
        if (replaced.isNIL() || terms.isEmpty()) {
          return F.NIL;
        }
        recurrences.add(new Recurrence(replaced, terms));
      }
    }
    if (recurrences.size() != headCount) {
      return F.NIL;
    }
    boolean[] assigned = new boolean[headCount];
    for (Recurrence recurrence : recurrences) {
      if (!assignHead(recurrence, assigned)) {
        return F.NIL;
      }
    }
    // the order of a head: its shift in its own recurrence minus its smallest shift anywhere
    Recurrence[] byHead = new Recurrence[headCount];
    int[] order = new int[headCount];
    int totalOrder = 0;
    for (Recurrence recurrence : recurrences) {
      byHead[recurrence.head] = recurrence;
    }
    for (int h = 0; h < headCount; h++) {
      int min = byHead[h].top;
      for (Recurrence recurrence : recurrences) {
        for (Term term : recurrence.terms) {
          if (term.relative && term.head == h) {
            min = Math.min(min, term.k);
          }
        }
      }
      order[h] = byHead[h].top - min;
      totalOrder += order[h];
    }
    if (conditions.size() != totalOrder) {
      // The number of constraints or initial conditions given, `1`, should be the same as the
      // total order of the system, `2`.
      return Errors.printMessage(S.RecurrenceTable, "rtnc",
          F.list(F.ZZ(conditions.size()), F.ZZ(totalOrder)), engine);
    }

    try {
      List<Map<Integer, IExpr>> values = new ArrayList<Map<Integer, IExpr>>(headCount);
      for (int h = 0; h < headCount; h++) {
        values.add(new HashMap<Integer, IExpr>());
      }
      if (!initialValues(conditions, heads, values, engine)) {
        return F.NIL;
      }
      int[] next = new int[headCount];
      for (int h = 0; h < headCount; h++) {
        int start = nMin;
        if (order[h] > 0) {
          start = Integer.MAX_VALUE;
          for (Integer index : values.get(h).keySet()) {
            start = Math.min(start, index);
          }
          for (int i = 0; i < order[h]; i++) {
            if (start == Integer.MAX_VALUE || !values.get(h).containsKey(start + i)) {
              // not enough initial conditions
              return F.NIL;
            }
          }
        }
        if (nMin < start || (long) nMax - start > MAX_VALUES) {
          return F.NIL;
        }
        next[h] = start + order[h];
      }
      // step the heads in turn as far as the values of the others reach
      boolean progress = true;
      while (progress) {
        progress = false;
        for (int h = 0; h < headCount; h++) {
          Map<Integer, IExpr> headValues = values.get(h);
          while (next[h] <= nMax) {
            engine.checkTimeLimit();
            if (!headValues.containsKey(next[h])) {
              IExpr value = step(byHead[h], next[h], n, values, engine);
              if (value.isNIL()) {
                break;
              }
              headValues.put(next[h], value);
            }
            next[h]++;
            progress = true;
          }
        }
      }
      IASTAppendable result = F.ListAlloc(nMax - nMin + 1);
      for (int index = nMin; index <= nMax; index++) {
        IASTAppendable row = F.ListAlloc(headCount);
        for (int h = 0; h < headCount; h++) {
          IExpr value = values.get(h).get(index);
          if (value == null) {
            return F.NIL;
          }
          row.append(value);
        }
        result.append(function.isList() ? row : row.arg1());
      }
      if (!arg1.isFree(x -> x.isInexactNumber(), false)) {
        // inexact coefficients or initial values give inexact table entries
        return engine.evalN(result);
      }
      return result;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_3_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDALL);
  }
}
