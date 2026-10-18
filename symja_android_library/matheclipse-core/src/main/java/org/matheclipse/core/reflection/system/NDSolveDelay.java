package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.ode.DenseOutputModel;
import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.OrdinaryDifferentialEquation;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>NDSolve</code> of delay differential equations with constant delays,
 * <code>{y'(t) == f(t, y(t), y(t - tau1), ...), y(t /; t &lt;= t0) == h(t)}</code>: the history
 * <code>h</code> gives the values before <code>t0</code>, and the equations are integrated by the
 * method of steps. The interval is cut into pieces no longer than the smallest delay, so that
 * every delayed value in a piece lies in the history or in a piece already integrated, which makes
 * each piece an ordinary differential equation.
 */
final class NDSolveDelay {

  /** The tolerances of the integration. */
  private static final double TOLERANCE = 1.0e-11;

  /** The functions, their right hand sides and histories, and the start of the integration. */
  private final ISymbol timeVar;
  private final ISymbol[] functions;
  private final boolean[] appliedForm;
  private final IExpr[] derivatives;
  private final IExpr[] histories;
  private final double t0;
  /** The delayed applications <code>y(t - tau)</code> and their delays and function indices. */
  private final List<IExpr> delayed = new ArrayList<IExpr>();
  private final List<Double> delays = new ArrayList<Double>();
  private final List<Integer> delayedFunction = new ArrayList<Integer>();
  private final EvalEngine engine;
  /** The values of the histories which are constants, <code>NaN</code> for the others. */
  private double[] constantHistories;
  /** The pieces integrated so far, joined in one model. */
  private DenseOutputModel model = null;

  private NDSolveDelay(ISymbol timeVar, ISymbol[] functions, boolean[] appliedForm,
      IExpr[] derivatives, IExpr[] histories, double t0, EvalEngine engine) {
    this.timeVar = timeVar;
    this.functions = functions;
    this.appliedForm = appliedForm;
    this.derivatives = derivatives;
    this.histories = histories;
    this.t0 = t0;
    this.engine = engine;
  }

  /**
   * The solution of a delay differential equation system.
   *
   * @return <code>F.NIL</code> if the equations are no delay system of this form
   */
  static IExpr solve(IAST ast, ISymbol timeVar, double tMin, double tMax, EvalEngine engine,
      boolean ruleForm) {
    IAST equations = ast.arg1().makeList();
    if (!equations.exists(NDSolveDelay::isHistoryCondition)) {
      return F.NIL;
    }
    IAST functionList = ast.arg2().makeList();
    final int n = functionList.argSize();
    ISymbol[] functions = new ISymbol[n];
    boolean[] appliedForm = new boolean[n];
    for (int i = 0; i < n; i++) {
      IExpr function = functionList.get(i + 1);
      if (function.isSymbol()) {
        functions[i] = (ISymbol) function;
      } else if (function.isAST1() && function.head().isSymbol() && function.first() == timeVar) {
        functions[i] = (ISymbol) function.head();
        appliedForm[i] = true;
      } else {
        return F.NIL;
      }
    }
    IExpr[] derivatives = new IExpr[n];
    IExpr[] histories = new IExpr[n];
    double t0 = Double.NaN;
    for (IExpr equation : equations) {
      if (!equation.isEqual()) {
        return F.NIL;
      }
      IExpr lhs = equation.first();
      IExpr rhs = equation.second();
      if (isHistoryCondition(equation)) {
        // y(t /; t <= t0) == h(t)
        IExpr condition = lhs.first();
        if (condition.first() != timeVar || !condition.second().isAST(S.LessEqual, 3)
            || condition.second().first() != timeVar) {
          return F.NIL;
        }
        int index = indexOf(functions, lhs.head());
        double start = condition.second().second().evalfNaN();
        if (index < 0 || Double.isNaN(start) || (!Double.isNaN(t0) && start != t0)) {
          return F.NIL;
        }
        t0 = start;
        histories[index] = rhs;
        continue;
      }
      // y'(t) == f(...)
      if (!lhs.isAST1() || lhs.first() != timeVar || !lhs.head().isAST1()
          || !lhs.head().head().isAST(S.Derivative, 2) || !lhs.head().head().first().isOne()) {
        return F.NIL;
      }
      int index = indexOf(functions, lhs.head().first());
      if (index < 0 || derivatives[index] != null) {
        return F.NIL;
      }
      derivatives[index] = rhs;
    }
    for (int i = 0; i < n; i++) {
      if (derivatives[i] == null || histories[i] == null) {
        return F.NIL;
      }
    }
    if (Double.isNaN(t0) || !(tMax > t0)) {
      return F.NIL;
    }
    final double tolerance = 1.0e-12 * Math.max(1.0, Math.abs(t0));
    if (tMin < t0 - tolerance) {
      // the history gives the values before t0; integrating backwards from it isn't done here
      return F.NIL;
    }
    // a range which starts after t0 is integrated from t0, and the solution is given on the range
    final double domainMin = Math.max(t0, Math.min(tMin, tMax));
    NDSolveDelay system =
        new NDSolveDelay(timeVar, functions, appliedForm, derivatives, histories, t0, engine);
    if (!system.collectDelays() || system.delays.isEmpty()) {
      return F.NIL;
    }
    if (!system.integrate(tMax)) {
      return F.NIL;
    }
    IASTAppendable solutions = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      IExpr interpolation =
          InterpolatingFunctionExpr.newInstance(system.model, null, t0, i, domainMin, tMax);
      IExpr solution = appliedForm[i] ? F.unaryAST1(interpolation, timeVar) : interpolation;
      solutions.append(ruleForm
          ? F.Rule(appliedForm[i] ? F.unaryAST1(functions[i], timeVar) : functions[i], solution)
          : solution);
    }
    if (ruleForm) {
      return F.list(solutions);
    }
    return n == 1 ? solutions.arg1() : solutions;
  }

  /** Whether the equation is a history <code>y(t /; t &lt;= t0) == h(t)</code>. */
  private static boolean isHistoryCondition(IExpr equation) {
    return equation.isEqual() && equation.first().isAST1()
        && equation.first().first().isAST(S.Condition, 3);
  }

  private static int indexOf(ISymbol[] functions, IExpr head) {
    for (int i = 0; i < functions.length; i++) {
      if (functions[i] == head) {
        return i;
      }
    }
    return -1;
  }

  /**
   * The applications <code>y(t - tau)</code> with a positive constant delay in the right hand
   * sides.
   *
   * @return <code>false</code> if a function is applied to something else than <code>t</code> or
   *         <code>t - tau</code>
   */
  private boolean collectDelays() {
    for (IExpr derivative : derivatives) {
      boolean[] valid = {true};
      collect(derivative, valid);
      if (!valid[0]) {
        return false;
      }
    }
    return true;
  }

  private void collect(IExpr expr, boolean[] valid) {
    if (!expr.isAST()) {
      return;
    }
    int index = expr.isAST1() ? indexOf(functions, expr.head()) : -1;
    if (index >= 0) {
      IExpr argument = expr.first();
      if (argument == timeVar) {
        return;
      }
      double shift = engine.evalN(F.Subtract(argument, timeVar)).evalfNaN();
      if (Double.isNaN(shift) || !(shift < 0.0)) {
        valid[0] = false;
        return;
      }
      if (!delayed.contains(expr)) {
        delayed.add(expr);
        delays.add(-shift);
        delayedFunction.add(index);
      }
      return;
    }
    for (IExpr arg : (IAST) expr) {
      collect(arg, valid);
    }
  }

  /** The value of function <code>i</code> at <code>s</code>, from the history or the pieces. */
  private double valueAt(int i, double s) {
    if ((s <= t0 || model == null) && !Double.isNaN(constantHistories[i])) {
      return constantHistories[i];
    }
    if (s <= t0 || model == null) {
      return engine.evalN(F.subst(histories[i], F.Rule(timeVar, F.num(Math.min(s, t0)))))
          .evalfNaN();
    }
    return model.getInterpolatedState(Math.min(s, model.getFinalTime())).getPrimaryState()[i];
  }

  private double[] derivativesAt(double t, double[] y) {
    IASTAppendable rules = F.ListAlloc(functions.length + delayed.size() + 1);
    for (int k = 0; k < delayed.size(); k++) {
      rules.append(F.Rule(delayed.get(k), F.num(valueAt(delayedFunction.get(k), t - delays.get(k)))));
    }
    for (int i = 0; i < functions.length; i++) {
      rules.append(F.Rule(F.unaryAST1(functions[i], timeVar), F.num(y[i])));
    }
    rules.append(F.Rule(timeVar, F.num(t)));
    double[] result = new double[functions.length];
    for (int i = 0; i < functions.length; i++) {
      result[i] = engine.evalN(F.subst(derivatives[i], rules)).evalfNaN();
      if (Double.isNaN(result[i])) {
        throw new ArithmeticException("NDSolve: the right hand side is not a number");
      }
    }
    return result;
  }

  /** Integrate piece by piece up to <code>tMax</code>. */
  private boolean integrate(double tMax) {
    constantHistories = new double[functions.length];
    for (int i = 0; i < functions.length; i++) {
      // a constant history is evaluated once, not at every delayed value
      constantHistories[i] =
          histories[i].isFree(timeVar) ? engine.evalN(histories[i]).evalfNaN() : Double.NaN;
    }
    double piece = Double.MAX_VALUE;
    for (double delay : delays) {
      piece = Math.min(piece, delay);
    }
    double[] state = new double[functions.length];
    for (int i = 0; i < functions.length; i++) {
      state[i] = valueAt(i, t0);
      if (Double.isNaN(state[i])) {
        return false;
      }
    }
    double from = t0;
    try {
      while (from < tMax) {
        double to = Math.min(tMax, from + piece);
        if (tMax - to < 1.0e-12 * Math.max(1.0, Math.abs(tMax))) {
          to = tMax;
        }
        DormandPrince853Integrator integrator =
            new DormandPrince853Integrator(1.0e-12, to - from, TOLERANCE, TOLERANCE);
        DenseOutputModel pieceModel = new DenseOutputModel();
        integrator.addStepHandler(pieceModel);
        OrdinaryDifferentialEquation ode = new OrdinaryDifferentialEquation() {
          @Override
          public int getDimension() {
            return functions.length;
          }

          @Override
          public double[] computeDerivatives(double t, double[] y) {
            return derivativesAt(t, y);
          }
        };
        state = integrator.integrate(ode, new ODEState(from, state), to).getPrimaryState();
        if (model == null) {
          model = pieceModel;
        } else {
          // append() measures the gap from the step a lookup last went to, so the lookups of the
          // delayed values are taken back to the end of the model first
          model.getInterpolatedState(model.getFinalTime());
          model.append(pieceModel);
        }
        from = to;
      }
    } catch (MathRuntimeException | ArithmeticException ex) {
      return false;
    }
    return model != null;
  }
}
