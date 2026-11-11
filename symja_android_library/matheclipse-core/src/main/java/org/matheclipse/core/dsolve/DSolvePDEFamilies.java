package org.matheclipse.core.dsolve;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Initial value problems of an evolution equation in two variables solved by a family of functions
 * which contains the initial data, the two classical boundary problems on a half line, and
 * Liouville's equation.
 *
 * <p>
 * For data <code>u(x, t0) = u0(x)</code> (and <code>u_t(x, t0) = u1(x)</code> for an equation of
 * second order in <code>t</code>) the solution is looked for in one of three families:
 * <ul>
 * <li><code>a(t)*u0(x)</code>, which turns <code>u_t + u*u_x == 0</code> with <code>u0 = x</code>
 * into an equation for <code>a(t)</code>;</li>
 * <li><code>u0(x - c*(t - t0))</code>, the travelling waves of the KdV, Fisher-KPP, Burgers and
 * sine-Gordon equations;</li>
 * <li><code>c0*E^(a0(t) + a1(t)*m1(x) + ...)</code> over the monomials <code>m</code> of the
 * exponent of the data, the Gaussians of the heat and Schrodinger equations.</li>
 * </ul>
 * In each the space dependence has to cancel, which leaves ordinary differential equations in
 * <code>t</code> with initial values read off the data. Every answer is checked by substitution
 * into the equation and into each condition.
 */
final class DSolvePDEFamilies {

  private DSolvePDEFamilies() {}

  /** The most monomials of the exponent of the data. */
  private static final int MAX_MONOMIALS = 6;

  /**
   * The solution of the problem with the conditions of the context, or {@link F#NIL}.
   *
   * @param lhs the residual of the equation, which is zero on a solution
   * @param uApplied the unknown applied to both variables
   */
  static IExpr solve(IExpr lhs, IExpr uApplied, IExpr x, IExpr y, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    int count = ctx.conditions.argSize();
    if (count < 1 || count > 2) {
      return F.NIL;
    }
    DSolvePDEInitialValue.Prescribed[] conditions = new DSolvePDEInitialValue.Prescribed[count];
    for (int i = 0; i < count; i++) {
      conditions[i] =
          DSolvePDEInitialValue.Prescribed.of(ctx.conditions.get(i + 1), uApplied, x, y, engine);
      if (conditions[i] == null) {
        return F.NIL;
      }
    }
    Problem p = new Problem(lhs, uApplied, x, y, ctx);
    try {
      IExpr body = F.NIL;
      Initial initial = Initial.of(p, conditions);
      if (initial != null) {
        p.onWholeLine(initial.space);
        for (int family = 0; family < 4 && body.isNIL(); family++) {
          IExpr candidate = family == 0 ? exponentialPolynomial(p, initial)
              : family == 1 ? amplitude(p, initial)
                  : family == 2 ? shift(p, initial) : decayingShift(p, initial);
          if (candidate.isPresent()) {
            candidate = p.simplify(candidate);
            if (p.verify(candidate)) {
              body = candidate;
            }
          }
        }
        return body;
      }
      IExpr candidate = similarity(p, conditions);
      if (candidate.isNIL()) {
        candidate = timeHarmonic(p, conditions);
      }
      if (candidate.isPresent()) {
        candidate = p.simplify(candidate);
        if (p.verify(candidate)) {
          return candidate;
        }
      }
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
    }
    return F.NIL;
  }

  /**
   * The general solution of Liouville's equation <code>a*u_xy + b*E^(m*u) == 0</code>:
   * <code>Log(2*f'(x)*g'(y)/(k*m*(f(x)+g(y))^2))/m</code> with <code>k = -b/a</code> and arbitrary
   * functions <code>f, g</code>.
   *
   * @return {@link F#NIL} if the equation isn't of this kind
   */
  static IExpr liouville(IExpr lhs, IExpr uApplied, IExpr x, IExpr y, DSolveContext ctx) {
    EvalEngine engine = ctx.engine;
    try {
      IExpr uxy = engine.evaluate(F.D(F.D(uApplied, x), y));
      ISymbol marker = F.Dummy("pvT");
      IExpr withMarker = F.subst(lhs, uxy, marker);
      IExpr a = engine.evaluate(F.D(withMarker, marker));
      IExpr rest = engine.evaluate(F.subst(withMarker, marker, F.C0));
      ISymbol v = F.Dummy("pvU");
      IExpr rv = engine.evaluate(F.subst(rest, uApplied, v));
      if (a.isZero() || !a.isFree(x) || !a.isFree(y) || !a.isFree(uApplied.head(), true)
          || !rv.isFree(x) || !rv.isFree(y) || !rv.isFree(uApplied.head(), true)
          || rv.isFree(v)) {
        return F.NIL;
      }
      // rest = b*E^(m*v)
      IExpr m = engine.evaluate(F.Simplify(F.Divide(F.D(rv, v), rv)));
      if (!m.isFree(v) || m.isZero()) {
        return F.NIL;
      }
      IExpr b = engine.evaluate(F.Simplify(F.Times(rv, F.Exp(F.Times(F.CN1, m, v)))));
      if (!b.isFree(v)) {
        return F.NIL;
      }
      IExpr k = engine.evaluate(F.Negate(F.Divide(b, a)));
      IExpr f = F.unaryAST1(F.C(engine.incConstantCounter()), x);
      IExpr g = F.unaryAST1(F.C(engine.incConstantCounter()), y);
      IExpr argument = F.Divide(F.Times(F.C2, F.D(f, x), F.D(g, y)),
          F.Times(k, m, F.Sqr(F.Plus(f, g))));
      return engine.evaluate(F.Divide(F.Log(argument), m));
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * An evolution equation of first order in time in more than two variables, with the data
   * <code>u(x, y, ..., t0) = u0(x, y, ...)</code>, solved by the family <code>a(t)*u0</code>: the
   * vorticity of the Taylor-Green vortex decays as <code>E^(-2*nu*t)</code>.
   *
   * @param equations the equation and the condition
   * @param uApplied the unknown applied to all variables
   * @return {@link F#NIL} if the problem isn't of this kind or the family doesn't close
   */
  static IExpr solveMany(IAST equations, IExpr uApplied, IAST vars, EvalEngine engine) {
    if (equations.argSize() != 2 || !uApplied.isAST() || uApplied.argSize() != vars.argSize()) {
      return F.NIL;
    }
    try {
      IExpr head = uApplied.head();
      IExpr equation = F.NIL;
      IExpr condition = F.NIL;
      int fixed = -1;
      for (IExpr e : equations) {
        int index = fixedIndex(e, head, vars);
        if (index > 0) {
          condition = e;
          fixed = index;
        } else {
          equation = e;
        }
      }
      if (equation.isNIL() || condition.isNIL() || !equation.isEqual()) {
        return F.NIL;
      }
      IExpr t = vars.get(fixed);
      IExpr t0 = condition.first().getAt(fixed);
      IExpr u0 = condition.second();
      IExpr lhs = engine.evaluate(F.ExpandAll(F.Subtract(equation.first(), equation.second())));
      ISymbol a = F.Dummy("pvA");
      IExpr body = F.Times(F.unaryAST1(a, t), u0);
      java.util.function.Function<IExpr, IExpr> apply = b -> engine
          .evaluate(F.ReplaceAll(lhs, F.Rule(head, F.Function(vars, b))));
      IExpr residual = apply.apply(body);
      IExpr top = engine.evaluate(F.D(F.unaryAST1(a, t), t));
      ISymbol marker = F.Dummy("pvD");
      IExpr solution = engine.evalQuiet(
          F.Solve(F.Equal(F.subst(residual, top, marker), F.C0), marker));
      if (!solution.isListOfLists() || solution.argSize() != 1) {
        return F.NIL;
      }
      IExpr rhs = engine.evaluate(F.Simplify(solution.first().first().second()));
      for (IExpr v : vars) {
        if (!v.equals(t) && !rhs.isFree(v)) {
          return F.NIL;
        }
      }
      IASTAppendable ivp = F.ListAlloc();
      ivp.append(F.Equal(top, rhs));
      ivp.append(F.Equal(F.unaryAST1(a, t0), F.C1));
      int counter = engine.getConstantCounter();
      IExpr sol;
      try {
        sol = engine.evalQuiet(F.DSolve(ivp, F.unaryAST1(a, t), t));
      } finally {
        engine.setConstantCounter(counter);
      }
      if (!sol.isListOfLists()) {
        return F.NIL;
      }
      for (IExpr branch : (IAST) sol) {
        if (branch.argSize() != 1 || !branch.first().isRule()) {
          continue;
        }
        IExpr amplitude = branch.first().second();
        if (!amplitude.isFree(c -> c.isAST(S.C, 2), true) || !amplitude.isFree(S.DSolve, true)) {
          // a general solution, which doesn't know the data
          continue;
        }
        IExpr result = engine.evaluate(F.Times(amplitude, u0));
        IExpr atStart = F.Subtract(F.subst(result, t, t0), u0);
        if (engine.evaluate(F.Simplify(atStart)).isZero()
            && engine.evaluate(F.Simplify(apply.apply(result))).isZero()) {
          return result;
        }
      }
      return F.NIL;
    } catch (RuntimeException rex) {
      org.matheclipse.core.eval.Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
  }

  /**
   * The position of the one argument of <code>u(...) == data</code> which is held at a point
   * while the others are the variables, or <code>-1</code>.
   */
  private static int fixedIndex(IExpr e, IExpr head, IAST vars) {
    if (!e.isEqual() || !e.first().isAST(head, vars.size()) || !e.second().isFree(head, true)) {
      return -1;
    }
    IAST call = (IAST) e.first();
    int fixed = -1;
    for (int i = 1; i < call.size(); i++) {
      if (call.get(i).equals(vars.get(i))) {
        continue;
      }
      if (fixed > 0 || !call.get(i).isFree(v -> vars.contains(v), true)) {
        return -1;
      }
      fixed = i;
    }
    if (fixed < 0 || !e.second().isFree(vars.get(fixed))) {
      return -1;
    }
    return fixed;
  }

  /** The equation, the unknown and its two variables. */
  private static final class Problem {
    final IExpr lhs;
    final IExpr uApplied;
    final IExpr x;
    final IExpr y;
    final DSolveContext ctx;
    final EvalEngine engine;
    /** What the check of an answer assumes, see {@link #genericAssumptions()}. */
    IExpr assumptions;
    /** The space variable of data on the whole line, or {@link F#NIL} on a half line. */
    IExpr wholeLine = F.NIL;

    Problem(IExpr lhs, IExpr uApplied, IExpr x, IExpr y, DSolveContext ctx) {
      this.lhs = lhs;
      this.uApplied = uApplied;
      this.x = x;
      this.y = y;
      this.ctx = ctx;
      this.engine = ctx.engine;
      this.assumptions = genericAssumptions();
    }

    /**
     * The data is given for every value of <code>space</code>: an answer is found without an
     * assumption on a sign, so that <code>Sqrt(x^2)</code> in the data stays what it is, and it is
     * checked at negative values of <code>space</code> as well.
     */
    void onWholeLine(IExpr space) {
      this.wholeLine = space;
      this.assumptions = genericAssumptions();
    }

    /**
     * The variables on the half line and every parameter positive: the generic case of a problem
     * without assumptions, in which a diffusion runs forwards and a frequency is positive. The
     * space variable of data on the whole line has no sign.
     */
    private IExpr genericAssumptions() {
      IASTAppendable and = F.ast(S.And);
      if (!x.equals(wholeLine)) {
        and.append(F.Greater(x, F.C0));
      }
      if (!y.equals(wholeLine)) {
        and.append(F.Greater(y, F.C0));
      }
      IASTAppendable all = F.ListAlloc();
      all.append(lhs);
      all.appendArgs(ctx.conditions);
      for (IExpr symbol : new VariablesSet(all).getArrayList()) {
        if (symbol.isSymbol() && !symbol.equals(x) && !symbol.equals(y)
            && !symbol.equals(uApplied.head())) {
          and.append(F.Greater(symbol, F.C0));
        }
      }
      return and;
    }

    /** The residual <code>r</code> with the unknown replaced by <code>Function({x, y}, body)</code>. */
    IExpr apply(IExpr r, IExpr body) {
      return engine.evaluate(
          F.ReplaceAll(r, F.Rule(uApplied.head(), F.Function(F.List(x, y), body))));
    }

    /**
     * <code>expr</code> simplified, on a half line with the assumptions of the generic case. With
     * data on the whole line nothing is assumed, and the assumptions are left to the check of the
     * answer: what is returned holds for the other signs as well.
     */
    IExpr simplify(IExpr expr) {
      return engine
          .evaluate(wholeLine.isPresent() ? F.Simplify(expr) : F.Simplify(expr, assumptions));
    }

    boolean isZero(IExpr expr) {
      if (expr.isZero()) {
        return true;
      }
      IExpr together = engine.evaluate(F.Together(expr));
      if (together.isZero()) {
        return true;
      }
      if (engine.evaluate(F.Simplify(together, assumptions)).isZero()) {
        return true;
      }
      // the variables and parameters are positive: (nu*t)^(3/2) is nu*t*Sqrt(nu*t)
      if (wholeLine.isNIL() && engine.evaluate(F.Together(F.PowerExpand(together))).isZero()) {
        return true;
      }
      // before the exponential form, which grows to minutes for the sine-Gordon kink
      Boolean sampled = isZeroAtSamplePoints(together);
      if (sampled != null) {
        return sampled;
      }
      return engine.evaluate(F.Simplify(F.Together(F.TrigToExp(together)), assumptions))
          .isZero();
    }

    /**
     * Whether <code>expr</code> is 0 at three points with positive values of its symbols, or null
     * if a value isn't a number. The space variable of data on the whole line is negative at two
     * of the points. The sum is compared with its largest term: computed with 30 digits a sum
     * which is 0 comes out 25 digits below it, however small or large the terms are.
     */
    private Boolean isZeroAtSamplePoints(IExpr expr) {
      IAST symbols = new VariablesSet(expr).getVarList();
      if (symbols.argSize() == 0) {
        return null;
      }
      IExpr expanded = engine.evaluate(F.Expand(expr));
      IAST terms = expanded.isPlus() ? (IAST) expanded : F.List(expanded);
      // N(expr, 30) sets the significant figures of the output, which are the caller's
      final int significantFigures = engine.getSignificantFigures();
      try {
        for (int point = 0; point < 3; point++) {
          IASTAppendable rules = F.ListAlloc(symbols.argSize());
          for (int i = 1; i < symbols.size(); i++) {
            IExpr symbol = symbols.get(i);
            int numerator = 31 + 17 * i + 23 * point;
            rules.append(F.Rule(symbol,
                F.QQ(point > 0 && symbol.equals(wholeLine) ? -numerator : numerator, 40)));
          }
          IASTAppendable values = F.PlusAlloc(terms.argSize());
          IExpr scale = F.C0;
          for (IExpr term : terms) {
            IExpr value = engine.evalQuiet(F.N(F.ReplaceAll(term, rules), F.ZZ(30)));
            if (!value.isNumber()) {
              return null;
            }
            values.append(value);
            scale = engine.evalQuiet(F.Max(scale, F.Abs(value)));
          }
          IExpr sum = engine.evalQuiet(F.Abs(values));
          if (!sum.isReal() || !scale.isReal()) {
            return null;
          }
          if (engine.evalTrue(F.Greater(sum, F.Times(F.num(1.0e-25), scale)))) {
            return Boolean.FALSE;
          }
        }
        return Boolean.TRUE;
      } finally {
        engine.setSignificantFigures(significantFigures);
      }
    }

    /** Whether <code>body</code> solves the equation and meets every condition. */
    boolean verify(IExpr body) {
      if (!body.isFree(S.Integrate, true) || !body.isFree(S.DSolve, true)) {
        return false;
      }
      if (!isZero(apply(lhs, body))) {
        return false;
      }
      for (IExpr condition : ctx.conditions) {
        IExpr residual = F.Subtract(condition.first(), condition.second());
        IExpr value = apply(residual, body);
        if (isZero(value)) {
          continue;
        }
        if (value.isFree(y -> y.isDirectedInfinity() || y.isIndeterminate(), true)
            || !limitAtCondition(residual, body)) {
          return false;
        }
      }
      return true;
    }

    /**
     * Whether a condition at a point where <code>body</code> isn't finite holds as a one-sided
     * limit, like the start of Stokes' first problem at <code>t == 0</code>, where the similarity
     * variable <code>y/Sqrt(t)</code> is singular.
     */
    private boolean limitAtCondition(IExpr residual, IExpr body) {
      DSolvePDEInitialValue.Prescribed condition = null;
      for (IExpr c : ctx.conditions) {
        if (F.Subtract(c.first(), c.second()).equals(residual)) {
          condition = DSolvePDEInitialValue.Prescribed.of(c, uApplied, x, y, engine);
        }
      }
      if (condition == null) {
        return false;
      }
      IExpr fixed = condition.freeIsX ? y : x;
      ISymbol eps = F.Dummy("pvEps");
      IExpr shifted = F.subst(body, fixed, F.Plus(fixed, eps));
      IExpr value = apply(residual, shifted);
      IExpr limit = engine.evaluate(F.function(S.Limit, value, F.Rule(eps, F.C0),
          F.Rule(S.Direction, F.CN1), F.Rule(S.Assumptions, assumptions)));
      return limit.isFree(S.Limit, true) && isZero(limit);
    }

    /**
     * The value of <code>a(t)</code> solving <code>a^(n)(t) == rhs</code> with
     * <code>a(t0) == a0</code> (and <code>a'(t0) == a1</code>), or {@link F#NIL}.
     */
    IExpr initialValueProblem(ISymbol a, IExpr t, int n, IExpr rhs, IExpr t0, IExpr a0,
        IExpr a1) {
      IExpr derivative = engine.evaluate(F.D(F.unaryAST1(a, t), F.List(t, F.ZZ(n))));
      IASTAppendable equations = F.ListAlloc();
      equations.append(F.Equal(derivative, rhs));
      equations.append(F.Equal(F.unaryAST1(a, t0), a0));
      if (a1.isPresent()) {
        equations.append(F.Equal(F.unaryAST1(F.unaryAST1(F.Derivative(F.C1), a), t0), a1));
      }
      int counter = engine.getConstantCounter();
      try {
        IExpr solution = engine.evalQuiet(F.DSolve(equations, F.unaryAST1(a, t), t));
        if (solution.isListOfLists() && solution.argSize() >= 1 && solution.first().argSize() == 1
            && solution.first().first().isRule()) {
          IExpr value = solution.first().first().second();
          if (value.isFree(y -> y.isAST(S.C, 2), true) && value.isFree(S.DSolve, true)) {
            return value;
          }
        }
        return F.NIL;
      } finally {
        engine.setConstantCounter(counter);
      }
    }

    /** The general solution of an ordinary differential equation for <code>f(s)</code>. */
    IExpr general(IExpr equation, ISymbol f, ISymbol s) {
      IExpr solution = engine.evalQuiet(F.DSolve(equation, F.unaryAST1(f, s), s));
      if (solution.isListOfLists() && solution.argSize() == 1 && solution.first().argSize() == 1
          && solution.first().first().isRule()) {
        return solution.first().first().second();
      }
      return F.NIL;
    }

    /**
     * <code>expr</code> with its constants <code>C(k)</code> fitted to the equations, or
     * {@link F#NIL} if there isn't exactly one solution. The constants are replaced by symbols
     * first: <code>C</code> is protected, and Solve doesn't take <code>C(1)</code> as an unknown.
     */
    IExpr fitConstants(IExpr expr, IAST equations) {
      IASTAppendable unknowns = F.ListAlloc();
      IASTAppendable toSymbols = F.ListAlloc();
      for (int k = 1; k <= 4; k++) {
        if (!expr.isFree(F.C(k))) {
          ISymbol s = F.Dummy("pvK" + k);
          unknowns.append(s);
          toSymbols.append(F.Rule(F.C(k), s));
        }
      }
      if (unknowns.argSize() == 0) {
        return F.NIL;
      }
      IExpr withSymbols = engine.evaluate(F.ReplaceAll(equations, toSymbols));
      IExpr solution = engine.evalQuiet(F.Solve(withSymbols, unknowns));
      if (!solution.isListOfLists() || solution.argSize() != 1) {
        return F.NIL;
      }
      IExpr fitted =
          engine.evaluate(F.ReplaceAll(F.ReplaceAll(expr, toSymbols), solution.first()));
      for (IExpr s : unknowns) {
        if (!fitted.isFree(s)) {
          return F.NIL;
        }
      }
      return fitted;
    }

    /** The solutions of <code>expr == 0</code> for <code>v</code>. */
    List<IExpr> solveFor(IExpr expr, IExpr v) {
      List<IExpr> result = new ArrayList<IExpr>();
      IExpr solution = engine.evalQuiet(F.Solve(F.Equal(expr, F.C0), v));
      if (solution.isListOfLists()) {
        for (IExpr rules : (IAST) solution) {
          if (rules.argSize() == 1 && rules.first().isRule() && rules.first().first().equals(v)) {
            result.add(rules.first().second());
          }
        }
      }
      return result;
    }
  }

  /** The data at a fixed time: <code>u(x, t0) = u0(x)</code> and, if needed, <code>u_t</code>. */
  private static final class Initial {
    final IExpr space;
    final IExpr time;
    final IExpr t0;
    final IExpr u0;
    final IExpr u1;
    final int order;

    private Initial(IExpr space, IExpr time, IExpr t0, IExpr u0, IExpr u1, int order) {
      this.space = space;
      this.time = time;
      this.t0 = t0;
      this.u0 = u0;
      this.u1 = u1;
      this.order = order;
    }

    /** The data, or null if the conditions don't all fix the time at one point. */
    static Initial of(Problem p, DSolvePDEInitialValue.Prescribed[] conditions) {
      DSolvePDEInitialValue.Prescribed first = conditions[0];
      IExpr u0 = F.NIL;
      IExpr u1 = F.NIL;
      for (DSolvePDEInitialValue.Prescribed c : conditions) {
        if (c.freeIsX != first.freeIsX || !c.at.equals(first.at)) {
          return null;
        }
        int inSpace = c.freeIsX ? c.orderX : c.orderY;
        int inTime = c.freeIsX ? c.orderY : c.orderX;
        if (inSpace != 0) {
          return null;
        }
        if (inTime == 0 && u0.isNIL()) {
          u0 = c.data;
        } else if (inTime == 1 && u1.isNIL()) {
          u1 = c.data;
        } else {
          return null;
        }
      }
      IExpr space = first.freeIsX ? p.x : p.y;
      IExpr time = first.freeIsX ? p.y : p.x;
      int order = timeOrder(p.lhs, p.uApplied.head(), first.freeIsX ? 2 : 1);
      if (u0.isNIL() || u0.isFree(space) || order < 1 || order > 2
          || conditions.length != order || (order == 2 && u1.isNIL())) {
        return null;
      }
      return new Initial(space, time, first.at, u0, u1, order);
    }

    /** The highest order of a derivative of the unknown in the variable at <code>index</code>. */
    private static int timeOrder(IExpr expr, IExpr head, int index) {
      int[] max = new int[1];
      expr.has(e -> {
        if (e.isAST() && e.head().isAST() && ((IAST) e.head()).arg1().equals(head)
            && ((IAST) e.head()).head().isAST(S.Derivative, 3)) {
          IExpr orders = ((IAST) e.head()).head();
          max[0] = Math.max(max[0], ((IAST) orders).get(index).toIntDefault(0));
        }
        return false;
      }, true);
      return max[0];
    }

    /** <code>t - t0</code>. */
    IExpr elapsed() {
      return t0.isZero() ? time : F.Subtract(time, t0);
    }

    /**
     * <code>c*(t - t0)</code> expanded: in the moving coordinate <code>x - c*t + c*t0</code> the
     * substitution <code>x -> xi + c*t - c*t0</code> cancels term by term.
     */
    IExpr travelled(IExpr c, EvalEngine engine) {
      return engine.evaluate(F.Expand(F.Times(c, elapsed())));
    }

    /** Whether <code>expr</code> doesn't depend on the space variable. */
    boolean isSpaceFree(IExpr expr) {
      return expr.isFree(space);
    }
  }

  /** The family <code>a(t)*u0(x)</code>. */
  private static IExpr amplitude(Problem p, Initial initial) {
    EvalEngine engine = p.engine;
    ISymbol a = F.Dummy("pvA");
    IExpr body = F.Times(F.unaryAST1(a, initial.time), initial.u0);
    IExpr residual = p.apply(p.lhs, body);
    IExpr top = engine
        .evaluate(F.D(F.unaryAST1(a, initial.time), F.List(initial.time, F.ZZ(initial.order))));
    ISymbol marker = F.Dummy("pvD");
    IExpr withMarker = F.subst(residual, top, marker);
    IExpr a1 = F.NIL;
    if (initial.order == 2) {
      // u1 = a'(t0)*u0
      a1 = p.simplify(F.Divide(initial.u1, initial.u0));
      if (!initial.isSpaceFree(a1)) {
        return F.NIL;
      }
    }
    for (IExpr rhs : p.solveFor(withMarker, marker)) {
      rhs = p.simplify(rhs);
      if (!initial.isSpaceFree(rhs)) {
        continue;
      }
      IExpr value = p.initialValueProblem(a, initial.time, initial.order, rhs, initial.t0,
          F.C1, a1);
      if (value.isPresent()) {
        return engine.evaluate(F.Times(value, initial.u0));
      }
    }
    return F.NIL;
  }

  /** The family <code>u0(x - c*(t - t0))</code> with a constant speed <code>c</code>. */
  private static IExpr shift(Problem p, Initial initial) {
    EvalEngine engine = p.engine;
    ISymbol c = F.Dummy("pvC");
    IExpr travelled = initial.travelled(c, engine);
    IExpr body = F.subst(initial.u0, initial.space, F.Subtract(initial.space, travelled));
    IExpr speed = F.NIL;
    if (initial.order == 2) {
      // u_t(x, t0) = -c*u0'(x)
      speed = spaceFree(p, initial,
          F.Negate(F.Divide(initial.u1, F.D(initial.u0, initial.space))));
    } else {
      // in the moving coordinate the profile doesn't depend on c, and c is left as the factor
      // the time derivative brings
      ISymbol xi = F.Dummy("pvXi");
      IExpr residual = p.apply(p.lhs, body);
      residual = engine.evaluate(F.subst(residual, initial.space, F.Plus(xi, travelled)));
      IExpr a = engine.evaluate(F.subst(residual, c, F.C0));
      IExpr b = engine.evaluate(F.D(residual, c));
      if (!b.isZero() && engine.evaluate(F.D(b, c)).isZero()) {
        speed = spaceFree(p, initial, F.subst(F.Negate(F.Divide(a, b)), xi, initial.space));
      }
    }
    if (speed.isNIL() || !speed.isFree(initial.time)) {
      return F.NIL;
    }
    return engine.evaluate(F.subst(body, c, speed));
  }

  /**
   * The family <code>E^(k*(t - t0))*u0(x - c*(t - t0))</code> of a first order equation in time
   * with constants <code>k</code> and <code>c</code>, like a drift with diffusion of a periodic
   * profile. In the moving coordinate the residual divided by the exponential is
   * <code>A + k*B + c*C</code>, which vanishes with its derivative in the space variable: a linear
   * system for <code>k</code> and <code>c</code>.
   */
  private static IExpr decayingShift(Problem p, Initial initial) {
    if (initial.order != 1) {
      return F.NIL;
    }
    EvalEngine engine = p.engine;
    ISymbol k = F.Dummy("pvK");
    ISymbol c = F.Dummy("pvC");
    ISymbol xi = F.Dummy("pvXi");
    // both expanded, so that E^(k*t - k)*E^(k - k*t) cancels
    IExpr growth = initial.travelled(k, engine);
    IExpr travelled = initial.travelled(c, engine);
    IExpr body = F.Times(F.Exp(growth),
        F.subst(initial.u0, initial.space, F.Subtract(initial.space, travelled)));
    IExpr residual = p.apply(p.lhs, body);
    residual = engine.evaluate(F.Expand(F.Times(F.Exp(initial.travelled(F.Negate(k), engine)),
        F.subst(residual, initial.space, F.Plus(xi, travelled)))));
    IExpr b = engine.evaluate(F.D(residual, k));
    IExpr cc = engine.evaluate(F.D(residual, c));
    if (!b.isFree(k) || !b.isFree(c) || !cc.isFree(k) || !cc.isFree(c)) {
      return F.NIL;
    }
    IExpr a = engine.evaluate(F.subst(residual, F.List(F.Rule(k, F.C0), F.Rule(c, F.C0))));
    IExpr a1 = engine.evaluate(F.D(a, xi));
    IExpr b1 = engine.evaluate(F.D(b, xi));
    IExpr c1 = engine.evaluate(F.D(cc, xi));
    IExpr determinant = p.simplify(F.Subtract(F.Times(b, c1), F.Times(b1, cc)));
    if (determinant.isZero()) {
      return F.NIL;
    }
    IExpr rate = spaceFree(p, initial, F.subst(F.Divide(
        F.Subtract(F.Times(a1, cc), F.Times(a, c1)), determinant), xi, initial.space));
    IExpr speed = spaceFree(p, initial, F.subst(F.Divide(
        F.Subtract(F.Times(a, b1), F.Times(a1, b)), determinant), xi, initial.space));
    if (rate.isNIL() || speed.isNIL() || !rate.isFree(initial.time)
        || !speed.isFree(initial.time) || (rate.isZero() && speed.isZero())) {
      return F.NIL;
    }
    return engine.evaluate(F.subst(body, F.List(F.Rule(k, rate), F.Rule(c, speed))));
  }

  /**
   * <code>expr</code>, which is constant if the family closes, free of the space variable: simplified,
   * or else its value at a point. The value at a point is only a candidate, which the check of the
   * answer accepts or not: <code>Together</code> of the quotient of the Fisher-KPP wave, with
   * <code>E^(x/Sqrt(6))</code> and <code>E^(Sqrt(2/3)*x)</code> in it, has hundreds of terms.
   *
   * @return {@link F#NIL} if neither is free of the space variable
   */
  private static IExpr spaceFree(Problem p, Initial initial, IExpr expr) {
    // evaluated before a point is put in: D(u0, x) at x == 0 is no derivative
    expr = p.engine.evaluate(expr);
    IExpr simplified = p.simplify(expr);
    if (initial.isSpaceFree(simplified)) {
      return simplified;
    }
    for (IExpr point : new IExpr[] {F.C0, F.C1}) {
      IExpr value = p.simplify(F.subst(expr, initial.space, point));
      if (value.isFree(y -> y.isDirectedInfinity() || y.isIndeterminate(), true)
          && initial.isSpaceFree(value)) {
        return value;
      }
    }
    return F.NIL;
  }

  /**
   * The family <code>c0*E^(a0(t) + a1(t)*m1 + ...)</code> over the monomials <code>m</code> of the
   * exponent of the data, solved monomial by monomial.
   */
  private static IExpr exponentialPolynomial(Problem p, Initial initial) {
    if (initial.order != 1) {
      return F.NIL;
    }
    EvalEngine engine = p.engine;
    IExpr[] split = exponentialFactor(initial.u0);
    if (split == null || !initial.isSpaceFree(split[0])) {
      return F.NIL;
    }
    IExpr c0 = split[0];
    IExpr exponent = engine.evaluate(F.Expand(split[1]));
    List<IExpr> monomials = new ArrayList<IExpr>();
    List<IExpr> initialValues = new ArrayList<IExpr>();
    // the constant monomial first, which carries the constant terms of the exponent
    monomials.add(F.C1);
    initialValues.add(F.C0);
    for (IExpr term : exponent.isPlus() ? (IAST) exponent : F.List(exponent)) {
      IExpr coefficient = engine.evaluate(F.subst(term, initial.space, F.C1));
      if (initial.isSpaceFree(term)) {
        initialValues.set(0, engine.evaluate(F.Plus(initialValues.get(0), term)));
        continue;
      }
      IExpr monomial = engine.evaluate(F.Divide(term, coefficient));
      if (!monomial.isFree(initial.time) || monomials.size() >= MAX_MONOMIALS) {
        return F.NIL;
      }
      monomials.add(monomial);
      initialValues.add(coefficient);
    }
    int n = monomials.size();
    ISymbol[] a = new ISymbol[n];
    IASTAppendable sum = F.PlusAlloc(n);
    for (int m = 0; m < n; m++) {
      a[m] = F.Dummy("pvE" + m);
      sum.append(F.Times(F.unaryAST1(a[m], initial.time), monomials.get(m)));
    }
    IExpr phi = engine.evaluate(F.Times(c0, F.Exp(sum)));
    IExpr quotient = engine.evaluate(F.Expand(F.Divide(p.apply(p.lhs, phi), phi)));
    // the terms of the quotient grouped by monomial: each group is an equation for one a(t)
    IExpr[] groups = new IExpr[n];
    for (IExpr term : quotient.isPlus() ? (IAST) quotient : F.List(quotient)) {
      IExpr coefficient = engine.evaluate(F.subst(term, initial.space, F.C1));
      if (coefficient.isZero()) {
        continue;
      }
      if (!initial.isSpaceFree(coefficient) || !term.isFree(S.E, true)) {
        return F.NIL;
      }
      IExpr monomial = engine.evaluate(F.Divide(term, coefficient));
      int hit = monomials.indexOf(monomial);
      if (hit < 0) {
        // a monomial outside the family has to cancel, which the check of the answer decides
        continue;
      }
      groups[hit] = groups[hit] == null ? coefficient
          : engine.evaluate(F.Plus(groups[hit], coefficient));
    }
    IExpr[] solved = new IExpr[n];
    int count = 0;
    for (int pass = 0; pass < n && count < n; pass++) {
      for (int m = 0; m < n; m++) {
        if (solved[m] != null) {
          continue;
        }
        if (groups[m] == null) {
          return F.NIL;
        }
        IExpr group = groups[m];
        for (int k = 0; k < n; k++) {
          if (solved[k] != null) {
            group = engine.evaluate(F.ReplaceAll(group,
                F.Rule(a[k], F.Function(F.List(initial.time), solved[k]))));
          }
        }
        boolean own = true;
        for (int k = 0; k < n && own; k++) {
          if (k != m && solved[k] == null && !group.isFree(a[k], true)) {
            own = false;
          }
        }
        if (!own) {
          continue;
        }
        ISymbol marker = F.Dummy("pvD");
        IExpr top = engine.evaluate(F.D(F.unaryAST1(a[m], initial.time), initial.time));
        List<IExpr> rhs = p.solveFor(F.subst(group, top, marker), marker);
        if (rhs.size() != 1) {
          return F.NIL;
        }
        IExpr value = p.initialValueProblem(a[m], initial.time, 1, rhs.get(0), initial.t0,
            initialValues.get(m), F.NIL);
        if (value.isNIL()) {
          return F.NIL;
        }
        solved[m] = value;
        count++;
      }
    }
    if (count < n) {
      return F.NIL;
    }
    IASTAppendable solvedSum = F.PlusAlloc(n);
    for (int m = 0; m < n; m++) {
      solvedSum.append(F.Times(solved[m], monomials.get(m)));
    }
    return engine.evaluate(F.Times(c0, F.Exp(solvedSum)));
  }

  /** <code>{c0, P}</code> with <code>u0 == c0*E^P</code>, or null. */
  private static IExpr[] exponentialFactor(IExpr u0) {
    if (u0.isExp()) {
      return new IExpr[] {F.C1, u0.exponent()};
    }
    if (!u0.isTimes()) {
      return null;
    }
    IASTAppendable rest = F.TimesAlloc(u0.argSize());
    IExpr exponent = F.NIL;
    for (IExpr factor : (IAST) u0) {
      if (exponent.isNIL() && factor.isExp()) {
        exponent = factor.exponent();
      } else {
        rest.append(factor);
      }
    }
    if (exponent.isNIL()) {
      return null;
    }
    return new IExpr[] {EvalEngine.get().evaluate(rest.oneIdentity1()), exponent};
  }

  /**
   * A diffusion which is invariant under <code>(y, t) -> (k*y, k^2*t)</code>, with a constant
   * initial value <code>u(y, t0) = a</code> and a constant boundary value <code>u(y0, t) = b</code>:
   * <code>u = F((y-y0)/Sqrt(t-t0))</code> with <code>F(0) == b</code> and
   * <code>F(Infinity) == a</code>.
   */
  private static IExpr similarity(Problem p, DSolvePDEInitialValue.Prescribed[] conditions) {
    if (conditions.length != 2) {
      return F.NIL;
    }
    EvalEngine engine = p.engine;
    DSolvePDEInitialValue.Prescribed initial = null;
    DSolvePDEInitialValue.Prescribed boundary = null;
    for (DSolvePDEInitialValue.Prescribed c : conditions) {
      if (c.orderX != 0 || c.orderY != 0) {
        return F.NIL;
      }
      int index = c.freeIsX ? 2 : 1;
      if (initial == null && Initial.timeOrder(p.lhs, p.uApplied.head(), index) == 1) {
        initial = c;
      } else {
        boundary = c;
      }
    }
    if (initial == null || boundary == null || initial.freeIsX == boundary.freeIsX) {
      return F.NIL;
    }
    IExpr t = initial.freeIsX ? p.y : p.x;
    IExpr y = initial.freeIsX ? p.x : p.y;
    if (!initial.data.isFree(y) || !initial.data.isFree(t) || !boundary.data.isFree(t)) {
      return F.NIL;
    }
    ISymbol f = F.Dummy("pvF");
    ISymbol eta = F.Dummy("pvEta");
    IExpr tau = engine.evaluate(F.Subtract(t, initial.at));
    IExpr similar = F.Divide(F.Subtract(y, boundary.at), F.Sqrt(tau));
    IExpr residual = p.apply(p.lhs, F.unaryAST1(f, similar));
    residual = engine.evaluate(
        F.subst(residual, y, F.Plus(boundary.at, F.Times(eta, F.Sqrt(tau)))));
    IExpr ode = F.NIL;
    for (IExpr k : new IExpr[] {F.C1, F.C1D2, F.QQ(3, 2), F.C2}) {
      IExpr scaled = engine.evaluate(F.Together(F.Times(residual, F.Power(tau, k))));
      if (scaled.isFree(t) && !scaled.isFree(f, true)) {
        ode = scaled;
        break;
      }
    }
    if (ode.isNIL()) {
      return F.NIL;
    }
    IExpr general = p.general(F.Equal(ode, F.C0), f, eta);
    if (general.isNIL() || general.isFree(y1 -> y1.isAST(S.C, 2), true)) {
      return F.NIL;
    }
    IExpr atZero = engine.evaluate(F.subst(general, eta, F.C0));
    IExpr atInfinity = engine.evaluate(F.Limit(general, F.Rule(eta, F.CInfinity),
        F.Rule(S.Assumptions, p.assumptions)));
    if (!atInfinity.isFree(S.Limit, true)) {
      return F.NIL;
    }
    IExpr fitted = p.fitConstants(general,
        F.List(F.Equal(atZero, boundary.data), F.Equal(atInfinity, initial.data)));
    if (fitted.isNIL()) {
      return F.NIL;
    }
    return engine.evaluate(F.subst(fitted, eta, similar));
  }

  /**
   * A boundary value <code>u(y0, t) = Re(A*E^(I*w*t))</code> and no initial value: the periodic
   * steady state <code>Re(E^(I*w*t)*F(y))</code> with the solution <code>F</code> of the reduced
   * equation which stays bounded for large <code>y</code>, and <code>F(y0) == A</code>.
   */
  private static IExpr timeHarmonic(Problem p, DSolvePDEInitialValue.Prescribed[] conditions) {
    if (conditions.length != 1 || conditions[0].orderX != 0 || conditions[0].orderY != 0) {
      return F.NIL;
    }
    EvalEngine engine = p.engine;
    DSolvePDEInitialValue.Prescribed c = conditions[0];
    IExpr y = c.freeIsX ? p.y : p.x;
    IExpr t = c.freeIsX ? p.x : p.y;
    IExpr g = c.data;
    if (!g.isFree(y) || g.isFree(t)) {
      return F.NIL;
    }
    // w^2 == -g''/g
    IExpr w2 = p.simplify(F.Negate(F.Divide(F.D(g, F.List(t, F.C2)), g)));
    if (!w2.isFree(t) || w2.isZero()) {
      return F.NIL;
    }
    IExpr w = p.simplify(F.Sqrt(w2));
    // A == g(0) - I*g'(0)/w
    IExpr g0 = engine.evaluate(F.subst(g, t, F.C0));
    IExpr g1 = engine.evaluate(F.subst(engine.evaluate(F.D(g, t)), t, F.C0));
    IExpr amplitude = engine.evaluate(F.Subtract(g0, F.Times(F.CI, F.Divide(g1, w))));
    ISymbol h = F.Dummy("pvH");
    IExpr wave = F.Exp(F.Times(F.CI, w, t));
    IExpr residual = p.apply(p.lhs, F.Times(wave, F.unaryAST1(h, y)));
    IExpr ode = engine.evaluate(F.Together(F.Divide(residual, wave)));
    if (!ode.isFree(t)) {
      return F.NIL;
    }
    IExpr general = p.general(F.Equal(ode, F.C0), h, (ISymbol) y);
    if (general.isNIL()) {
      return F.NIL;
    }
    // keep the members E^(k*y) of the basis which decay, Re(k) < 0
    IExpr kept = general;
    for (int k = 1; k <= 2; k++) {
      IExpr only = engine.evaluate(F.ReplaceAll(general,
          F.List(F.Rule(F.C(k), F.C1), F.Rule(F.C(3 - k), F.C0))));
      IExpr rate = p.simplify(F.Divide(F.D(only, y), only));
      if (!rate.isFree(y) || !decays(p, rate)) {
        kept = engine.evaluate(F.ReplaceAll(kept, F.Rule(F.C(k), F.C0)));
      }
    }
    IExpr atBoundary = engine.evaluate(F.subst(kept, y, c.at));
    IExpr fitted = p.fitConstants(kept, F.List(F.Equal(atBoundary, amplitude)));
    if (fitted.isNIL()) {
      return F.NIL;
    }
    IExpr complex = engine.evaluate(F.PowerExpand(F.Times(wave, fitted)));
    return engine.evaluate(F.ComplexExpand(F.Re(complex)));
  }

  /** Whether the real part of the constant rate is negative, with sample values for parameters. */
  private static boolean decays(Problem p, IExpr rate) {
    EvalEngine engine = p.engine;
    IExpr value = engine.evaluate(F.PowerExpand(rate));
    int i = 0;
    for (IExpr symbol : new VariablesSet(value).getArrayList()) {
      value = F.subst(value, symbol, F.QQ(13 + 4 * i, 10));
      i++;
    }
    IExpr re = engine.evaluate(F.N(F.Re(value)));
    return re.isReal() && re.evalDouble() < -1.0e-12;
  }
}
