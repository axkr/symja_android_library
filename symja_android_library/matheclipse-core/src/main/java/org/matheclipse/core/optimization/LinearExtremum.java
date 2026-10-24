package org.matheclipse.core.optimization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The extremum of a linear objective under linear constraints, solved by the installed
 * {@link ILinearOptimizer}: the <code>Method -&gt; "ORTools"</code> path of <code>Minimize</code>,
 * <code>Maximize</code>, <code>NMinimize</code> and <code>NMaximize</code>.
 *
 * <p>
 * The constraints are a conjunction of <code>Equal, Less, LessEqual, Greater, GreaterEqual</code>
 * relations (also chained ones like <code>0 &lt;= x &lt;= 3</code>) and domain conditions
 * <code>Element(x, Integers)</code>. A strict inequality is accepted only between integer valued
 * sides, where <code>a &lt; b</code> is <code>a &lt;= b - 1</code>; on the reals it has no
 * attained optimum, and the caller's own method has to decide it.
 */
public final class LinearExtremum {

  private LinearExtremum() {}

  /** One linear row <code>lower &lt;= coefficients . x &lt;= upper</code>. */
  private static final class Row {
    final double[] coefficients;
    final double lower;
    final double upper;

    Row(double[] coefficients, double lower, double upper) {
      this.coefficients = coefficients;
      this.lower = lower;
      this.upper = upper;
    }
  }

  /** An affine expression <code>coefficients . x + constant</code> with exact coefficients. */
  private static final class Affine {
    final IExpr[] coefficients;
    final IExpr constant;

    Affine(IExpr[] coefficients, IExpr constant) {
      this.coefficients = coefficients;
      this.constant = constant;
    }

    double[] doubles() {
      double[] d = new double[coefficients.length];
      for (int i = 0; i < d.length; i++) {
        d[i] = coefficients[i].evalf();
      }
      return d;
    }

    boolean isIntegral() {
      for (IExpr c : coefficients) {
        if (!c.isInteger()) {
          return false;
        }
      }
      return true;
    }
  }

  /**
   * The extremum <code>{value, {x1 -&gt; v1, ...}}</code>.
   *
   * @param head the calling function, for messages
   * @param objective the objective function
   * @param constraints the constraints: <code>True</code>, one relation, an <code>And</code> or a
   *        list of them
   * @param varList the variables
   * @param allInteger whether every variable is an integer (the domain argument
   *        <code>Integers</code>)
   * @param isMax <code>true</code> for a maximum
   * @param numeric <code>true</code> for <code>NMinimize/NMaximize</code>: the value and real
   *        variables as machine numbers; else the value is computed exactly from the solution
   * @return {@link F#NIL} if the problem is not linear in this sense, so that the caller uses its
   *         own method
   */
  public static IExpr extremum(ISymbol head, IExpr objective, IExpr constraints, IAST varList,
      boolean allInteger, boolean isMax, boolean numeric, EvalEngine engine) {
    final int n = varList.argSize();
    if (n == 0) {
      return F.NIL;
    }
    final IExpr[] vars = new IExpr[n];
    for (int i = 0; i < n; i++) {
      vars[i] = varList.get(i + 1);
      if (!vars[i].isVariable()) {
        return F.NIL;
      }
    }
    final boolean[] integer = new boolean[n];
    Arrays.fill(integer, allInteger);

    Affine obj = affine(objective, vars, engine);
    if (obj == null || !isNumeric(obj)) {
      return F.NIL;
    }

    List<IExpr> conjuncts = new ArrayList<>();
    flatten(constraints, conjuncts);
    // the domain conditions first, so that strict inequalities know the integer variables
    List<IExpr> relations = new ArrayList<>();
    for (IExpr c : conjuncts) {
      if (c.isAST(S.Element, 3)) {
        IExpr domain = c.second();
        if (domain != S.Integers && domain != S.Reals) {
          return F.NIL;
        }
        IAST elements = c.first().makeList();
        for (int j = 1; j < elements.size(); j++) {
          int k = indexOf(vars, elements.get(j));
          if (k < 0) {
            return F.NIL;
          }
          if (domain == S.Integers) {
            integer[k] = true;
          }
        }
      } else {
        relations.add(c);
      }
    }

    List<Row> rows = new ArrayList<>();
    for (IExpr c : relations) {
      if (!addRows(c, vars, integer, rows, engine)) {
        return F.NIL;
      }
    }

    final int m = rows.size();
    double[][] matrix = new double[m][];
    double[] rowLower = new double[m];
    double[] rowUpper = new double[m];
    for (int i = 0; i < m; i++) {
      Row r = rows.get(i);
      matrix[i] = r.coefficients;
      rowLower[i] = r.lower;
      rowUpper[i] = r.upper;
    }
    double[] varLower = new double[n];
    double[] varUpper = new double[n];
    Arrays.fill(varLower, Double.NEGATIVE_INFINITY);
    Arrays.fill(varUpper, Double.POSITIVE_INFINITY);
    ILinearOptimizer.Problem problem = new ILinearOptimizer.Problem(obj.doubles(),
        obj.constant.evalf(), isMax, matrix, rowLower, rowUpper, varLower, varUpper, integer,
        OptimizationMethod.DEFAULT_TIME_LIMIT);
    ILinearOptimizer.Result result = ILinearOptimizer.get().solve(problem);

    switch (result.status) {
      case OPTIMAL:
      case FEASIBLE: {
        IASTAppendable rules = F.ListAlloc(n);
        for (int i = 0; i < n; i++) {
          double v = result.values[i];
          rules.append(F.Rule(vars[i], integer[i] ? F.ZZ(Math.round(v)) : F.num(v)));
        }
        IExpr value = engine.evaluate(F.ReplaceAll(objective, rules));
        if (numeric) {
          value = engine.evalN(value);
        }
        return F.list(value, rules);
      }
      case INFEASIBLE: {
        // There are no values of `1` for which the constraints `2` are satisfied and the
        // objective function `3` is real valued.
        Errors.printMessage(head, "infeas", F.list(varList, constraints, objective), engine);
        return F.list(isMax ? F.CNInfinity : F.CInfinity, indeterminateRules(vars));
      }
      case UNBOUNDED:
        return F.list(isMax ? F.CInfinity : F.CNInfinity, indeterminateRules(vars));
      default:
        return F.NIL;
    }
  }

  private static IAST indeterminateRules(IExpr[] vars) {
    IASTAppendable rules = F.ListAlloc(vars.length);
    for (IExpr v : vars) {
      rules.append(F.Rule(v, S.Indeterminate));
    }
    return rules;
  }

  private static int indexOf(IExpr[] vars, IExpr v) {
    for (int i = 0; i < vars.length; i++) {
      if (vars[i].equals(v)) {
        return i;
      }
    }
    return -1;
  }

  private static void flatten(IExpr constraints, List<IExpr> conjuncts) {
    if (constraints.isTrue()) {
      return;
    }
    if (constraints.isAnd() || constraints.isList()) {
      for (IExpr c : (IAST) constraints) {
        flatten(c, conjuncts);
      }
      return;
    }
    conjuncts.add(constraints);
  }

  private static boolean isNumeric(Affine a) {
    for (IExpr c : a.coefficients) {
      if (!c.isReal()) {
        return false;
      }
    }
    return a.constant.isReal();
  }

  /**
   * Add the rows of the relation <code>c</code>; a chained relation like
   * <code>LessEqual(a, b, c)</code> gives one row per adjacent pair.
   *
   * @return <code>false</code> if <code>c</code> is not a linear relation with numeric
   *         coefficients, or a strict inequality between sides which aren't integer valued
   */
  private static boolean addRows(IExpr c, IExpr[] vars, boolean[] integer, List<Row> rows,
      EvalEngine engine) {
    if (!c.isAST() || c.argSize() < 2) {
      return false;
    }
    final IAST rel = (IAST) c;
    final ISymbol op = rel.topHead();
    final boolean less = op == S.Less || op == S.LessEqual;
    final boolean greater = op == S.Greater || op == S.GreaterEqual;
    final boolean strict = op == S.Less || op == S.Greater;
    if (op != S.Equal && !less && !greater) {
      return false;
    }
    for (int k = 1; k < rel.argSize(); k++) {
      // lhs - rhs  op  0
      Affine d = affine(engine.evaluate(F.Subtract(rel.get(k), rel.get(k + 1))), vars, engine);
      if (d == null || !isNumeric(d)) {
        return false;
      }
      double bound = -d.constant.evalf();
      if (op == S.Equal) {
        rows.add(new Row(d.doubles(), bound, bound));
        continue;
      }
      if (strict) {
        // only between integer valued sides: coefficients . x  <  -constant
        if (!d.isIntegral() || !d.constant.isRational() || !onlyIntegerVariables(d, integer)) {
          return false;
        }
        IExpr negConstant = engine.evaluate(F.Negate(d.constant));
        bound = less ? engine.evaluate(F.Subtract(F.Ceiling(negConstant), F.C1)).evalf()
            : engine.evaluate(F.Plus(F.Floor(negConstant), F.C1)).evalf();
      }
      if (less) {
        rows.add(new Row(d.doubles(), Double.NEGATIVE_INFINITY, bound));
      } else {
        rows.add(new Row(d.doubles(), bound, Double.POSITIVE_INFINITY));
      }
    }
    return true;
  }

  private static boolean onlyIntegerVariables(Affine d, boolean[] integer) {
    for (int i = 0; i < integer.length; i++) {
      if (!integer[i] && !d.coefficients[i].isZero()) {
        return false;
      }
    }
    return true;
  }

  /**
   * The exact affine coefficients of <code>expr</code> in <code>vars</code>, or <code>null</code>
   * if <code>expr</code> isn't affine in them.
   */
  private static Affine affine(IExpr expr, IExpr[] vars, EvalEngine engine) {
    final int n = vars.length;
    IExpr expanded = engine.evaluate(F.Expand(expr));
    IExpr[] coefficients = new IExpr[n];
    IASTAppendable linearPart = F.PlusAlloc(n + 1);
    for (int i = 0; i < n; i++) {
      IExpr ci = engine.evaluate(F.Coefficient(expanded, vars[i]));
      if (!isFreeOf(ci, vars)) {
        return null;
      }
      coefficients[i] = ci;
      linearPart.append(F.Times(ci, vars[i]));
    }
    IASTAppendable zeroRules = F.ListAlloc(n);
    for (IExpr v : vars) {
      zeroRules.append(F.Rule(v, F.C0));
    }
    IExpr constant = engine.evaluate(F.ReplaceAll(expanded, zeroRules));
    if (!isFreeOf(constant, vars)) {
      return null;
    }
    linearPart.append(constant);
    IExpr remainder = engine.evaluate(F.Expand(F.Subtract(expanded, linearPart)));
    if (!remainder.isZero()) {
      return null;
    }
    return new Affine(coefficients, constant);
  }

  private static boolean isFreeOf(IExpr e, IExpr[] vars) {
    for (IExpr v : vars) {
      if (!e.isFree(v)) {
        return false;
      }
    }
    return true;
  }
}
