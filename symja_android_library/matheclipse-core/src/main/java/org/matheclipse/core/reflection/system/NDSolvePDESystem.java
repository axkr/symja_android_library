package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.NDSolvePDE.Coefficient;

/**
 * <code>NDSolve</code> of a system of equations, first order in time, in one space variable:
 *
 * <pre>
 * NDSolve({D(u(x, t), t) - a*D(u(x, t), x, x) == NeumannValue(g0, x == x0) + NeumannValue(g1, x == x1),
 *          tau*D(v(x, t), t) == c*u(x, t) + e*v(x, t), u(x, 0) == f(x), v(x, 0) == h(x)},
 *         {u, v}, {t, t0, t1}, {x, x0, x1})
 * </pre>
 *
 * <p>
 * The unknowns take the time and the space variable in either order, as the equations write them.
 * An unknown with a second space derivative is a field which diffuses; one without any space
 * derivative is a quantity that only evolves in time at every point. Every equation has to be
 * linear in the unknowns and their space derivatives, with coefficients that may depend on
 * <code>t</code> and <code>x</code>.
 *
 * <p>
 * An end of a diffusing field either has a given value, <code>u(x0, t) == g(t)</code>, or a flux:
 * a term <code>NeumannValue(g, x == x0)</code> of the equation sets <code>a*du/dn = g</code> at
 * that end, with <code>n</code> the outward normal: a diffusive flux <code>g</code> into the
 * interval, which a drift term doesn't change. <code>g</code> may depend linearly on the values
 * of the unknowns there. An end without either is insulated when the equation has a
 * <code>NeumannValue</code> at all.
 *
 * <p>
 * The space interval is divided into {@link NDSolvePDE#SPACE_POINTS} points, a flux end by the
 * half cell next to it, which keeps the total of a conserved quantity exact, and the system is
 * stepped with the Crank-Nicolson scheme; each step solves one banded system.
 */
final class NDSolvePDESystem {

  private NDSolvePDESystem() {}

  /** The kinds of end of a diffusing field. */
  private static final int END_NONE = 0;
  private static final int END_VALUE = 1;
  private static final int END_FLUX = 2;

  /**
   * One unknown: its head, its placeholders and what the equations say about it.
   */
  private static final class Field {
    final IExpr head;
    final ISymbol u = F.Dummy("pdeU");
    final ISymbol ux = F.Dummy("pdeUx");
    final ISymbol uxx = F.Dummy("pdeUxx");
    final ISymbol ut = F.Dummy("pdeUt");
    /** the time derivative, solved for */
    IExpr rate = F.NIL;
    IExpr initial = F.NIL;
    /** the value or the flux terms at the two ends */
    final int[] endKind = {END_NONE, END_NONE};
    final IExpr[] endValue = {F.NIL, F.NIL};
    /** the NeumannValue terms: their factor in the rate and their value, per end */
    final List<IExpr[]>[] fluxes = newFluxLists();
    boolean diffusing;

    Field(IExpr head) {
      this.head = head;
    }

    @SuppressWarnings("unchecked")
    private static List<IExpr[]>[] newFluxLists() {
      return new List[] {new ArrayList<IExpr[]>(), new ArrayList<IExpr[]>()};
    }
  }

  /**
   * The solution of the problem in <code>ast</code>, or {@link F#NIL} where it is not one this
   * class solves.
   *
   * @param ast the whole <code>NDSolve(...)</code> or <code>NDSolveValue(...)</code> expression
   * @param engine the evaluation engine
   * @param ruleForm <code>true</code> for <code>{{u -&gt; f, ...}}</code>, <code>false</code> for
   *        the functions themselves
   */
  static IExpr solve(IAST ast, EvalEngine engine, boolean ruleForm) {
    NDSolvePDE.Ranges ranges = NDSolvePDE.parseRanges(ast);
    if (ranges == null) {
      return F.NIL;
    }
    ISymbol t = ranges.t;
    ISymbol x = ranges.x;
    double t0 = ranges.t0;
    double t1 = ranges.t1;
    double x0 = ranges.x0;
    double x1 = ranges.x1;

    // the unknowns, written as u, u(x, t) or a list of them
    IAST unknowns = ast.arg2().makeList();
    List<Field> fields = new ArrayList<Field>();
    for (IExpr unknown : unknowns) {
      IExpr head = unknown.isSymbol() ? unknown : unknown.isAST2() ? unknown.head() : F.NIL;
      if (!head.isSymbol()) {
        return F.NIL;
      }
      fields.add(new Field(head));
    }

    IAST equations = ast.arg1().makeList();
    // the position of the time variable in the arguments: 1 for u(t, x), 2 for u(x, t)
    int timeArgument = timeArgument(equations, fields, t, x);
    if (timeArgument < 0) {
      return F.NIL;
    }
    IASTAppendable rules = F.ListAlloc(5 * fields.size());
    for (Field field : fields) {
      rules.append(F.Rule(derivative(field.head, timeArgument, 1, 0, t, x), field.ut));
      rules.append(F.Rule(derivative(field.head, timeArgument, 0, 2, t, x), field.uxx));
      rules.append(F.Rule(derivative(field.head, timeArgument, 0, 1, t, x), field.ux));
      rules.append(F.Rule(application(field.head, timeArgument, t, x), field.u));
    }

    for (IExpr equation : equations) {
      if (equation.isAST(S.DirichletCondition, 3)) {
        // DirichletCondition(u(x, t) == g, x == x0)
        if (!boundaryValue(equation.first(), equation.second(), fields, timeArgument, t, x, x0,
            x1)) {
          return F.NIL;
        }
        continue;
      }
      if (!equation.isEqual()) {
        return F.NIL;
      }
      IExpr lhs = equation.first();
      IExpr rhs = equation.second();
      // the NeumannValue terms are replaced by markers, whose factors are taken below
      List<IExpr[]> neumann = new ArrayList<IExpr[]>();
      IExpr difference = F.Subtract(lhs, rhs).replaceAll(e -> {
        if (e.isAST(S.NeumannValue, 3)) {
          ISymbol marker = F.Dummy("pdeFlux");
          neumann.add(new IExpr[] {marker, e.first(), e.second()});
          return marker;
        }
        return F.NIL;
      }).orElse(F.Subtract(lhs, rhs));
      difference = engine.evaluate(F.subst(difference, rules));
      Field evolving = null;
      for (Field field : fields) {
        if (!difference.isFree(field.ut)) {
          if (evolving != null) {
            return F.NIL;
          }
          evolving = field;
        }
      }
      if (evolving == null) {
        // a condition: a value at an end or at the start
        if (!neumann.isEmpty()) {
          return F.NIL;
        }
        if (!boundaryValue(lhs, rhs, fields, timeArgument, t, x, x0, x1)
            && !initialValue(lhs, rhs, fields, timeArgument, t, x, t0)) {
          return F.NIL;
        }
        continue;
      }
      if (evolving.rate.isPresent()) {
        return F.NIL;
      }
      // the equation, linear in the time derivative with a numeric factor, solved for it
      IExpr tau = engine.evaluate(F.D(difference, evolving.ut));
      if (!tau.isNumber() || tau.isZero()) {
        return F.NIL;
      }
      IExpr rate = engine.evaluate(F.Expand(
          F.Times(F.CN1, F.Divide(F.subst(difference, F.Rule(evolving.ut, F.C0)), tau))));
      for (IExpr[] term : neumann) {
        IExpr factor = engine.evaluate(F.D(rate, term[0]));
        if (!factor.isNumber()) {
          return F.NIL;
        }
        int end = end(term[2], x, x0, x1);
        if (end < 0) {
          return F.NIL;
        }
        // the value of the flux at its end, in the values of the unknowns there
        IExpr value = engine.evaluate(F.subst(F.subst(term[1], rules), F.Rule(x, F.num(end == 0 ? x0 : x1))));
        evolving.fluxes[end].add(new IExpr[] {factor, value});
        rate = engine.evaluate(F.subst(rate, F.Rule(term[0], F.C0)));
      }
      evolving.rate = rate;
    }

    for (Field field : fields) {
      if (field.rate.isNIL() || field.initial.isNIL()) {
        return F.NIL;
      }
      for (Field other : fields) {
        if (other != field && (!field.rate.isFree(other.uxx) || !field.rate.isFree(other.ux))) {
          return F.NIL;
        }
      }
      field.diffusing = !field.rate.isFree(field.uxx);
      boolean hasFlux = !field.fluxes[0].isEmpty() || !field.fluxes[1].isEmpty();
      for (int end = 0; end < 2; end++) {
        if (!field.diffusing) {
          if (field.endKind[end] != END_NONE || !field.fluxes[end].isEmpty()) {
            return F.NIL;
          }
          continue;
        }
        if (!field.fluxes[end].isEmpty()) {
          if (field.endKind[end] != END_NONE) {
            return F.NIL;
          }
          field.endKind[end] = END_FLUX;
        } else if (field.endKind[end] == END_NONE) {
          if (!hasFlux) {
            // without any flux term an end needs a value
            return F.NIL;
          }
          // an end without a condition is insulated
          field.endKind[end] = END_FLUX;
        }
      }
      if (!field.diffusing && !field.rate.isFree(field.ux)) {
        // transport without diffusion is not stepped here
        return F.NIL;
      }
    }

    Discretisation system = Discretisation.create(fields, t, x, x0, x1, engine);
    if (system == null) {
      return F.NIL;
    }
    double[][][] values = system.solve(t0, t1);
    if (values == null) {
      return F.NIL;
    }

    IASTAppendable functions = F.ListAlloc(fields.size());
    for (int f = 0; f < fields.size(); f++) {
      double[][] fieldValues = values[f];
      IExpr function;
      if (timeArgument == 1) {
        function = ListInterpolation.grid(system.ts, system.xs, fieldValues, 3);
      } else {
        function = ListInterpolation.grid(system.xs, system.ts, transpose(fieldValues), 3);
      }
      if (function.isNIL()) {
        return F.NIL;
      }
      functions.append(function);
    }
    if (!ruleForm) {
      return ast.arg2().isList() ? functions : functions.arg1();
    }
    IASTAppendable solution = F.ListAlloc(fields.size());
    for (int f = 0; f < fields.size(); f++) {
      IExpr unknown = unknowns.get(f + 1);
      IExpr function = functions.get(f + 1);
      solution.append(unknown.isSymbol() ? F.Rule(unknown, function)
          : F.Rule(unknown, F.binaryAST2(function, unknown.first(), unknown.second())));
    }
    return F.list(solution);
  }

  /**
   * The position of the time variable in the arguments of the unknowns: <code>1</code> if they are
   * written <code>u(t, x)</code>, <code>2</code> for <code>u(x, t)</code>, <code>-1</code> if the
   * equations don't tell or don't agree.
   */
  private static int timeArgument(IAST equations, List<Field> fields, ISymbol t, ISymbol x) {
    int[] found = {0};
    for (IExpr equation : equations) {
      scan(equation, fields, t, x, found);
    }
    return found[0] > 0 ? found[0] : -1;
  }

  /** Records in <code>found[0]</code> where the applications of the unknowns have <code>t</code>. */
  private static void scan(IExpr expr, List<Field> fields, ISymbol t, ISymbol x, int[] found) {
    if (!expr.isAST()) {
      return;
    }
    if (expr.isAST2() && isFieldHead(expr.head(), fields)) {
      int position = 0;
      if (expr.first() == t || expr.second() == x) {
        position = 1;
      } else if (expr.first() == x || expr.second() == t) {
        position = 2;
      }
      if (position > 0) {
        if (found[0] == 0) {
          found[0] = position;
        } else if (found[0] != position) {
          found[0] = -1;
        }
      }
    }
    for (IExpr arg : (IAST) expr) {
      if (found[0] < 0) {
        return;
      }
      scan(arg, fields, t, x, found);
    }
  }

  /** Whether <code>head</code> is an unknown or a <code>Derivative(m, n)</code> of one. */
  private static boolean isFieldHead(IExpr head, List<Field> fields) {
    if (head.isAST1() && head.head().isAST(S.Derivative, 3)) {
      head = head.first();
    }
    for (Field field : fields) {
      if (field.head.equals(head)) {
        return true;
      }
    }
    return false;
  }

  private static Field field(IExpr head, List<Field> fields) {
    for (Field field : fields) {
      if (field.head.equals(head)) {
        return field;
      }
    }
    return null;
  }

  /** The time and the space argument of <code>u(s, y)</code>, in this order. */
  private static IExpr[] arguments(IExpr application, int timeArgument) {
    return timeArgument == 1 ? new IExpr[] {application.first(), application.second()}
        : new IExpr[] {application.second(), application.first()};
  }

  /** <code>u(t, x)</code> or <code>u(x, t)</code> */
  private static IAST application(IExpr head, int timeArgument, IExpr t, IExpr x) {
    return timeArgument == 1 ? F.binaryAST2(head, t, x) : F.binaryAST2(head, x, t);
  }

  /** The derivative of order <code>m</code> in time and <code>n</code> in space. */
  private static IAST derivative(IExpr head, int timeArgument, int m, int n, IExpr t, IExpr x) {
    IAST orders = timeArgument == 1 ? F.binaryAST2(S.Derivative, F.ZZ(m), F.ZZ(n))
        : F.binaryAST2(S.Derivative, F.ZZ(n), F.ZZ(m));
    return application(F.unaryAST1(orders, head), timeArgument, t, x);
  }

  /** <code>u(x, t0) == f(x)</code>, either way round. */
  private static boolean initialValue(IExpr lhs, IExpr rhs, List<Field> fields, int timeArgument,
      ISymbol t, ISymbol x, double t0) {
    IExpr application = lhs;
    IExpr value = rhs;
    if (!(application.isAST2() && field(application.head(), fields) != null)) {
      application = rhs;
      value = lhs;
    }
    Field field = application.isAST2() ? field(application.head(), fields) : null;
    if (field == null || field.initial.isPresent() || !isFree(value, fields)) {
      return false;
    }
    IExpr[] arguments = arguments(application, timeArgument);
    if (arguments[1] != x || !arguments[0].isNumber()
        || Math.abs(arguments[0].evalfNaN() - t0) > 1e-12) {
      return false;
    }
    field.initial = value;
    return true;
  }

  /** <code>u(x0, t) == g(t)</code> at one of the two ends, either way round. */
  private static boolean boundaryValue(IExpr lhs, IExpr rhs, List<Field> fields, int timeArgument,
      ISymbol t, ISymbol x, double x0, double x1) {
    IExpr condition = F.NIL;
    if (lhs.isEqual() && rhs.isEqual()) {
      // DirichletCondition(u(x, t) == g, x == a)
      condition = rhs;
      rhs = lhs.second();
      lhs = lhs.first();
    }
    IExpr application = lhs;
    IExpr value = rhs;
    if (!(application.isAST2() && field(application.head(), fields) != null)) {
      application = rhs;
      value = lhs;
    }
    Field field = application.isAST2() ? field(application.head(), fields) : null;
    if (field == null || !isFree(value, fields)) {
      return false;
    }
    IExpr[] arguments = arguments(application, timeArgument);
    if (arguments[0] != t) {
      return false;
    }
    int end;
    if (condition.isPresent()) {
      if (arguments[1] != x) {
        return false;
      }
      end = end(condition, x, x0, x1);
      value = F.subst(value, F.Rule(x, F.num(end == 0 ? x0 : x1)));
    } else {
      if (!arguments[1].isNumber()) {
        return false;
      }
      end = endAt(arguments[1].evalfNaN(), x0, x1);
    }
    if (end < 0 || field.endKind[end] != END_NONE) {
      return false;
    }
    field.endKind[end] = END_VALUE;
    field.endValue[end] = value;
    return true;
  }

  private static boolean isFree(IExpr expr, List<Field> fields) {
    for (Field field : fields) {
      if (!expr.isFree(field.head)) {
        return false;
      }
    }
    return true;
  }

  /** The end <code>0</code> or <code>1</code> a condition <code>x == a</code> names, or -1. */
  private static int end(IExpr condition, ISymbol x, double x0, double x1) {
    if (!condition.isEqual()) {
      return -1;
    }
    IExpr at = condition.first() == x ? condition.second()
        : condition.second() == x ? condition.first() : F.NIL;
    return at.isNumber() ? endAt(at.evalfNaN(), x0, x1) : -1;
  }

  private static int endAt(double at, double x0, double x1) {
    double tolerance = 1e-12 * Math.max(1.0, Math.abs(x1 - x0));
    if (Math.abs(at - x0) <= tolerance) {
      return 0;
    }
    if (Math.abs(at - x1) <= tolerance) {
      return 1;
    }
    return -1;
  }


  private static double[][] transpose(double[][] values) {
    double[][] result = new double[values[0].length][values.length];
    for (int i = 0; i < values.length; i++) {
      for (int j = 0; j < values[i].length; j++) {
        result[j][i] = values[i][j];
      }
    }
    return result;
  }

  /**
   * The discretised system <code>dU/dt = A(t) U + s(t)</code> over the grid points of all
   * unknowns, the unknowns of one point next to each other so that <code>A</code> is banded.
   */
  private static final class Discretisation {
    final int m;
    final int n = NDSolvePDE.SPACE_POINTS;
    final double h;
    final double[] xs;
    double[] ts;
    /** the half bandwidth: the neighbours of a point are m entries away */
    final int w;
    final Coefficient[] diffusion;
    final Coefficient[] drift;
    /** the coupling of unknown f to unknown g at the same point */
    final Coefficient[][] coupling;
    final Coefficient[] source;
    final Coefficient[] initial;
    final int[][] endKind;
    final Coefficient[][] endValue;
    /** per unknown and end: the factor of each flux term and its coefficients */
    final double[][][] fluxFactor;
    final Coefficient[][][][] fluxCoupling;
    final Coefficient[][][] fluxSource;

    private Discretisation(int m, double x0, double x1) {
      this.m = m;
      this.h = (x1 - x0) / (n - 1);
      this.xs = new double[n];
      for (int i = 0; i < n; i++) {
        xs[i] = x0 + i * h;
      }
      this.w = m;
      diffusion = new Coefficient[m];
      drift = new Coefficient[m];
      coupling = new Coefficient[m][m];
      source = new Coefficient[m];
      initial = new Coefficient[m];
      endKind = new int[m][];
      endValue = new Coefficient[m][2];
      fluxFactor = new double[m][2][];
      fluxCoupling = new Coefficient[m][2][][];
      fluxSource = new Coefficient[m][2][];
    }

    /**
     * The coefficients of the linear equations; <code>null</code> if an equation is not linear in
     * the unknowns.
     */
    static Discretisation create(List<Field> fields, ISymbol t, ISymbol x, double x0, double x1,
        EvalEngine engine) {
      int m = fields.size();
      Discretisation d = new Discretisation(m, x0, x1);
      IASTAppendable zeros = F.ListAlloc(4 * m);
      for (Field field : fields) {
        zeros.append(F.Rule(field.u, F.C0));
        zeros.append(F.Rule(field.ux, F.C0));
        zeros.append(F.Rule(field.uxx, F.C0));
      }
      for (int f = 0; f < m; f++) {
        Field field = fields.get(f);
        IExpr rate = field.rate;
        IExpr a = engine.evaluate(F.D(rate, field.uxx));
        IExpr b = engine.evaluate(F.D(rate, field.ux));
        if (!isConstant(a, fields) || !isConstant(b, fields)) {
          return null;
        }
        d.diffusion[f] = new Coefficient(a, t, x, engine);
        d.drift[f] = new Coefficient(b, t, x, engine);
        for (int g = 0; g < m; g++) {
          IExpr c = engine.evaluate(F.D(rate, fields.get(g).u));
          if (!isConstant(c, fields)) {
            return null;
          }
          d.coupling[f][g] = new Coefficient(c, t, x, engine);
        }
        IExpr rest = engine.evaluate(F.subst(rate, zeros));
        if (!isConstant(rest, fields)) {
          return null;
        }
        d.source[f] = new Coefficient(rest, t, x, engine);
        d.initial[f] = new Coefficient(field.initial, t, x, engine);
        d.endKind[f] = field.endKind.clone();
        for (int end = 0; end < 2; end++) {
          if (field.endKind[end] == END_VALUE) {
            d.endValue[f][end] = new Coefficient(field.endValue[end], t, x, engine);
          }
          List<IExpr[]> terms = field.fluxes[end];
          d.fluxFactor[f][end] = new double[terms.size()];
          d.fluxCoupling[f][end] = new Coefficient[terms.size()][m];
          d.fluxSource[f][end] = new Coefficient[terms.size()];
          for (int k = 0; k < terms.size(); k++) {
            d.fluxFactor[f][end][k] = terms.get(k)[0].evalfNaN();
            IExpr value = terms.get(k)[1];
            for (int g = 0; g < m; g++) {
              IExpr c = engine.evaluate(F.D(value, fields.get(g).u));
              if (!isConstant(c, fields)) {
                return null;
              }
              d.fluxCoupling[f][end][k][g] = new Coefficient(c, t, x, engine);
            }
            IExpr offset = engine.evaluate(F.subst(value, zeros));
            if (!isConstant(offset, fields)) {
              return null;
            }
            d.fluxSource[f][end][k] = new Coefficient(offset, t, x, engine);
          }
        }
      }
      return d;
    }

    /** Whether a coefficient is free of the unknowns and their derivatives. */
    private static boolean isConstant(IExpr expr, List<Field> fields) {
      for (Field field : fields) {
        if (!expr.isFree(field.u) || !expr.isFree(field.ux) || !expr.isFree(field.uxx)
            || !expr.isFree(field.ut) || !expr.isFree(field.head)) {
          return false;
        }
      }
      return true;
    }

    private int index(int f, int i) {
      return i * m + f;
    }

    /**
     * <code>A(tv)</code> in band form, <code>band[p][q - p + w]</code>, and <code>s(tv)</code>;
     * the rows of points with given values stay empty.
     *
     * @return <code>false</code> if a coefficient isn't a real number or the diffusion isn't
     *         positive
     */
    private boolean operator(double tv, double[][] band, double[] s) {
      for (double[] row : band) {
        java.util.Arrays.fill(row, 0.0);
      }
      java.util.Arrays.fill(s, 0.0);
      double h2 = h * h;
      for (int f = 0; f < m; f++) {
        boolean diffusing = endKind[f][0] != END_NONE;
        for (int i = 0; i < n; i++) {
          int p = index(f, i);
          double xi = xs[i];
          int end = i == 0 ? 0 : i == n - 1 ? 1 : -1;
          if (diffusing && end >= 0 && endKind[f][end] == END_VALUE) {
            continue;
          }
          for (int g = 0; g < m; g++) {
            double c = coupling[f][g].value(tv, xi);
            band[p][index(g, i) - p + w] += c;
          }
          s[p] += source[f].value(tv, xi);
          if (diffusing) {
            double a = diffusion[f].value(tv, xi);
            double b = drift[f].value(tv, xi);
            if (!(a > 0.0)) {
              return false;
            }
            if (end < 0) {
              band[p][index(f, i - 1) - p + w] += a / h2 - b / (2 * h);
              band[p][w] += -2 * a / h2;
              band[p][index(f, i + 1) - p + w] += a / h2 + b / (2 * h);
            } else {
              // a flux end: the half cell next to it, with no flux but the given one through
              // the end; the flux fixes the slope there, a*du/dn = flux, which the drift term
              // takes
              int inner = end == 0 ? 1 : n - 2;
              double normal = end == 0 ? -1.0 : 1.0;
              band[p][index(f, inner) - p + w] += 2 * a / h2;
              band[p][w] += -2 * a / h2;
              double[] factors = fluxFactor[f][end];
              for (int k = 0; k < factors.length; k++) {
                double weight = factors[k] * (2 / h + b * normal / a);
                for (int g = 0; g < m; g++) {
                  band[p][index(g, i) - p + w] += weight * fluxCoupling[f][end][k][g].value(tv, xi);
                }
                s[p] += weight * fluxSource[f][end][k].value(tv, xi);
              }
            }
          }
        }
      }
      for (double[] row : band) {
        for (double v : row) {
          if (!Double.isFinite(v)) {
            return false;
          }
        }
      }
      for (double v : s) {
        if (!Double.isFinite(v)) {
          return false;
        }
      }
      return true;
    }

    /**
     * The values of every unknown at every time step and grid point,
     * <code>values[f][step][i]</code>; <code>null</code> if the system can't be stepped.
     */
    double[][][] solve(double t0, double t1) {
      int steps = NDSolvePDE.TIME_STEPS;
      int size = m * n;
      double dt = (t1 - t0) / steps;
      ts = new double[steps + 1];
      double[][][] values = new double[m][steps + 1][n];
      double[] now = new double[size];
      for (int f = 0; f < m; f++) {
        for (int i = 0; i < n; i++) {
          now[index(f, i)] = initial[f].value(t0, xs[i]);
        }
        for (int end = 0; end < 2; end++) {
          if (endKind[f][end] == END_VALUE) {
            int i = end == 0 ? 0 : n - 1;
            now[index(f, i)] = endValue[f][end].value(t0, xs[i]);
          }
        }
      }
      ts[0] = t0;
      if (!store(now, values, 0)) {
        return null;
      }
      int width = 2 * w + 1;
      double[][] bandOld = new double[size][width];
      double[] sourceOld = new double[size];
      double[][] bandNew = new double[size][width];
      double[] sourceNew = new double[size];
      if (!operator(t0, bandOld, sourceOld)) {
        return null;
      }
      double[][] matrix = new double[size][width];
      double[] right = new double[size];
      for (int k = 0; k < steps; k++) {
        double tn1 = (k + 1 == steps) ? t1 : t0 + (k + 1) * dt;
        ts[k + 1] = tn1;
        if (!operator(tn1, bandNew, sourceNew)) {
          return null;
        }
        for (int p = 0; p < size; p++) {
          double sum = now[p] + 0.5 * dt * (sourceOld[p] + sourceNew[p]);
          for (int j = 0; j < width; j++) {
            int q = p + j - w;
            if (q >= 0 && q < size) {
              sum += 0.5 * dt * bandOld[p][j] * now[q];
            }
            matrix[p][j] = (j == w ? 1.0 : 0.0) - 0.5 * dt * bandNew[p][j];
          }
          right[p] = sum;
        }
        for (int f = 0; f < m; f++) {
          for (int end = 0; end < 2; end++) {
            if (endKind[f][end] == END_VALUE) {
              int p = index(f, end == 0 ? 0 : n - 1);
              java.util.Arrays.fill(matrix[p], 0.0);
              matrix[p][w] = 1.0;
              right[p] = endValue[f][end].value(tn1, xs[end == 0 ? 0 : n - 1]);
            }
          }
        }
        double[] next = solveBanded(matrix, right, w);
        if (next == null || !store(next, values, k + 1)) {
          return null;
        }
        now = next;
        double[][] swap = bandOld;
        bandOld = bandNew;
        bandNew = swap;
        double[] swapSource = sourceOld;
        sourceOld = sourceNew;
        sourceNew = swapSource;
      }
      return values;
    }

    private boolean store(double[] vector, double[][][] values, int step) {
      for (int f = 0; f < m; f++) {
        for (int i = 0; i < n; i++) {
          double v = vector[index(f, i)];
          if (!Double.isFinite(v)) {
            return false;
          }
          values[f][step][i] = v;
        }
      }
      return true;
    }
  }

  /**
   * Gaussian elimination of a banded system <code>matrix[p][q - p + w]</code> without pivoting,
   * which the diagonally dominant step matrix doesn't need. The matrix is overwritten.
   *
   * @return the solution, or <code>null</code> where a pivot vanishes
   */
  static double[] solveBanded(double[][] matrix, double[] right, int w) {
    int size = right.length;
    double[] b = right.clone();
    for (int k = 0; k < size; k++) {
      double pivot = matrix[k][w];
      if (pivot == 0.0 || !Double.isFinite(pivot)) {
        return null;
      }
      for (int r = k + 1; r <= Math.min(size - 1, k + w); r++) {
        // the entry of row r in column k
        double factor = matrix[r][k - r + w] / pivot;
        if (factor == 0.0) {
          continue;
        }
        for (int c = k; c <= Math.min(size - 1, k + w); c++) {
          matrix[r][c - r + w] -= factor * matrix[k][c - k + w];
        }
        b[r] -= factor * b[k];
      }
    }
    double[] result = new double[size];
    for (int k = size - 1; k >= 0; k--) {
      double sum = b[k];
      for (int c = k + 1; c <= Math.min(size - 1, k + w); c++) {
        sum -= matrix[k][c - k + w] * result[c];
      }
      result[k] = sum / matrix[k][w];
    }
    return result;
  }
}
