package org.matheclipse.core.reflection.system;

import org.hipparchus.analysis.UnivariateFunction;
import org.hipparchus.analysis.solvers.BracketedUnivariateSolver;
import org.hipparchus.analysis.solvers.BracketingNthOrderBrentSolver;
import org.hipparchus.analysis.solvers.RegulaFalsiSolver;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.ode.AbstractIntegrator;
import org.hipparchus.ode.DenseOutputModel;
import org.hipparchus.ode.ODEState;
import org.hipparchus.ode.ODEStateAndDerivative;
import org.hipparchus.ode.OrdinaryDifferentialEquation;
import org.hipparchus.ode.events.Action;
import org.hipparchus.ode.events.AdaptableInterval;
import org.hipparchus.ode.events.EventSlopeFilter;
import org.hipparchus.ode.events.FilterType;
import org.hipparchus.ode.events.ODEEventDetector;
import org.hipparchus.ode.events.ODEEventHandler;
import org.hipparchus.ode.nonstiff.AdamsMoultonIntegrator;
import org.hipparchus.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.hipparchus.ode.nonstiff.GraggBulirschStoerIntegrator;
import org.hipparchus.ode.sampling.ODEStateInterpolator;
import org.hipparchus.ode.sampling.ODEStepHandler;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.ODEUtils;
import org.matheclipse.core.eval.util.SolveUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * See: <a href="https://en.wikipedia.org/wiki/Ordinary_differential_equation">Wikipedia:Ordinary
 * differential equation</a>
 */
public class NDSolve extends AbstractFunctionOptionEvaluator {

  /**
   * The highest derivative order which is searched for when the equations are reduced to an
   * explicit first order system. Mirrors the bound {@link DSolve} places on itself and keeps the
   * search for the order of each dependent function finite.
   */
  private static final int MAX_DERIVATIVE_ORDER = 10;

  /**
   * The relative and absolute tolerance the integrator is asked for.
   *
   * <p>
   * Tighter than {@link Config#SPECIAL_FUNCTIONS_TOLERANCE}, which the shared special function code
   * uses. The accuracy of the solution - and of any point located on it, such as the time an event
   * is reached - is bounded by this value, and <code>10^-10</code> leaves a located event roughly
   * <code>10^-10</code> away from where it really is. Tightening the shared constant instead would
   * slow unrelated code for no benefit.
   */
  private static final double TOLERANCE = 1.0e-12;

  /**
   * The largest number of right hand side evaluations a single integration may use.
   *
   * <p>
   * Hipparchus defaults to {@link Integer#MAX_VALUE} and its guide recommends choosing a value
   * suited to the problem. The recommendation binds harder here than for a typical caller: every
   * evaluation substitutes into a symbolic expression and evaluates it numerically, so an equation
   * whose error control collapses into ever smaller steps - approaching a singularity, say - would
   * otherwise run until the engine's own timeout with nothing to show for it.
   */
  private static final int MAX_EVALUATIONS = 100_000;

  /** The smallest step the adaptive integrators may take before they give up. */
  private static final double MIN_STEP = 1.0e-10;

  /** The accuracy an event time is located to, once its step has been bracketed. */
  private static final double EVENT_THRESHOLD = 1.0e-14;

  /** The <code>Throw</code> tag which stops the integration at an event. */
  private static final String STOP_INTEGRATION = "StopIntegration";

  /**
   * An explicit first order system <code>s'(t) = f(t, s(t))</code>, reduced from the equations the
   * user wrote.
   *
   * <p>
   * A dependent function <code>y</code> of order <code>n</code> contributes <code>n</code>
   * components to the state vector <code>s</code>, holding <code>y, y', ..., y^(n-1)</code>. The
   * first <code>n-1</code> rows of <code>f</code> are then simply the next component, and only the
   * last row is the equation solved for <code>y^(n)</code>.
   *
   * <p>
   * Every component belongs to the primary state, never to a Hipparchus
   * {@link org.hipparchus.ode.SecondaryODE}: an adaptive integrator controls its step size on the
   * primary state alone, so moving the derivative components into a secondary equation would look
   * tidier while quietly removing them from the error control.
   */
  private static final class FirstOrderSystem {
    /** The independent variable. */
    final ISymbol timeVar;

    /** The dependent function symbols, e.g. <code>{u, v}</code>. */
    final ISymbol[] functions;

    /** <code>true</code> where the user wrote <code>y(x)</code> rather than <code>y</code>. */
    final boolean[] appliedForm;

    /** The index in the state vector at which the components of each function start. */
    final int[] offset;

    /** Component <code>i</code> of the state vector, as the expression <code>y^(k)(t)</code>. */
    final IExpr[] stateExpressions;

    /** The right hand side of component <code>i</code> of the state vector. */
    final IExpr[] derivatives;

    /** The point at which the initial conditions are given. */
    final double t0;

    /** The state vector at {@link #t0}. */
    final double[] initialState;

    FirstOrderSystem(ISymbol timeVar, ISymbol[] functions, boolean[] appliedForm, int[] offset,
        IExpr[] stateExpressions, IExpr[] derivatives, double t0, double[] initialState) {
      this.timeVar = timeVar;
      this.functions = functions;
      this.appliedForm = appliedForm;
      this.offset = offset;
      this.stateExpressions = stateExpressions;
      this.derivatives = derivatives;
      this.t0 = t0;
      this.initialState = initialState;
    }

    int dimension() {
      return derivatives.length;
    }

    /**
     * The rules which turn an expression written in terms of the dependent functions into one in
     * numbers, e.g. <code>y'(t)-1</code> into <code>3.5-1</code>.
     *
     * <p>
     * The rule for the time variable comes last on purpose: the replacement rewrites the outermost
     * match, so <code>y'(t)</code> is replaced as a whole and the <code>t</code> it contains is
     * never seen on its own.
     *
     * @param t the time to substitute
     * @param state the state vector to substitute
     */
    IAST substitutions(double t, double[] state) {
      IASTAppendable rules = F.ListAlloc(dimension() + 1);
      for (int i = 0; i < dimension(); i++) {
        rules.append(F.Rule(stateExpressions[i], F.num(state[i])));
      }
      rules.append(F.Rule(timeVar, F.num(t)));
      return rules;
    }
  }

  private static class FirstODE implements OrdinaryDifferentialEquation {
    private final EvalEngine fEngine;
    private final FirstOrderSystem fSystem;

    public FirstODE(EvalEngine engine, FirstOrderSystem system) {
      this.fEngine = engine;
      this.fSystem = system;
    }

    @Override
    public int getDimension() {
      return fSystem.dimension();
    }

    @Override
    public double[] computeDerivatives(double t, double[] state) {
      final int dimension = fSystem.dimension();
      IAST rules = fSystem.substitutions(t, state);
      double[] stateDot = new double[dimension];
      for (int i = 0; i < dimension; i++) {
        stateDot[i] = fEngine.evalN(F.subst(fSystem.derivatives[i], rules)).evalfNaN();
      }
      return stateDot;
    }
  }

  public NDSolve() {}

  /** The index of each option of {@link #setUp(ISymbol)} in the option array. */
  static final int METHOD = 0, ACCURACY_GOAL = 1, PRECISION_GOAL = 2;

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    return solve(ast, options, engine, true);
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.Method, S.AccuracyGoal, S.PrecisionGoal}, //
        new IExpr[] {S.Automatic, S.Automatic, S.Automatic});
  }

  /**
   * Solve the system numerically.
   *
   * <p>
   * Shared by {@link NDSolve} and {@link NDSolveValue}, which differ only in how they hand the
   * solution back: as rules for the dependent functions, or as the functions themselves.
   *
   * @param ast the whole <code>NDSolve(...)</code> or <code>NDSolveValue(...)</code> expression
   * @param options the evaluated values of {@link S#Method}, {@link S#AccuracyGoal} and
   *        {@link S#PrecisionGoal}
   * @param engine the evaluation engine
   * @param ruleForm <code>true</code> to return <code>{{y->InterpolatingFunction(...)}}</code>,
   *        <code>false</code> to return the interpolating function itself
   * @return the solution, or {@link F#NIL} if the equations could not be solved
   */
  static IExpr solve(final IAST ast, final IExpr[] options, EvalEngine engine, boolean ruleForm) {
    if (!ast.arg3().isList()) {
      return F.NIL;
    }
    final IAST tRangeList = (IAST) ast.arg3();
    if (!(tRangeList.isAST2() || tRangeList.isAST3())) {
      return F.NIL;
    }
    try {
      if (!tRangeList.arg1().isSymbol()) {
        // `1` is not a valid variable.
        return Errors.printMessage(ast.topHead(), "ivar", F.list(tRangeList.arg1()), engine);
      }
      final ISymbol timeVar = (ISymbol) tRangeList.arg1();
      IExpr tMinExpr = F.C0;
      IExpr tMaxExpr = tRangeList.arg2();
      if (tRangeList.isAST3()) {
        tMinExpr = tRangeList.arg2();
        tMaxExpr = tRangeList.arg3();
      }
      final double tMin = tMinExpr.evalfNaN();
      final double tMax = tMaxExpr.evalfNaN();
      if (Double.isNaN(tMin) || Double.isNaN(tMax)) {
        return F.NIL;
      }

      FirstOrderSystem system = reduceToFirstOrder(ast, timeVar, engine);
      if (system == null) {
        return F.NIL;
      }
      MethodSettings method = MethodSettings.parse(options, ast, tMax - tMin, engine);
      if (method == null) {
        return F.NIL;
      }
      return integrate(ast.topHead(), system, tMin, tMax, method, engine, ruleForm);
    } catch (LimitException le) {
      throw le;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(ast.topHead(), rex, engine);
    }
  }

  /**
   * Reduce the equations of <code>ast</code> to an explicit first order system.
   *
   * @param ast the whole <code>NDSolve(...)</code> expression
   * @param timeVar the independent variable
   * @param engine the evaluation engine
   * @return the reduced system, or <code>null</code> if the equations could not be reduced, in
   *         which case a message has been printed
   */
  private static FirstOrderSystem reduceToFirstOrder(IAST ast, ISymbol timeVar, EvalEngine engine) {
    // --- the dependent functions, written either as y or as y(t) ---
    final IAST functionList = ast.arg2().makeList();
    final int numberOfFunctions = functionList.argSize();
    if (numberOfFunctions == 0) {
      return null;
    }
    ISymbol[] functions = new ISymbol[numberOfFunctions];
    boolean[] appliedForm = new boolean[numberOfFunctions];
    for (int i = 0; i < numberOfFunctions; i++) {
      IExpr function = functionList.get(i + 1);
      if (function.isSymbol()) {
        functions[i] = (ISymbol) function;
      } else if (function.isAST1() && function.head().isSymbol()
          && function.first().equals(timeVar)) {
        functions[i] = (ISymbol) function.head();
        appliedForm[i] = true;
      } else {
        // `1` is not a valid variable.
        Errors.printMessage(S.NDSolve, "ivar", F.list(function), engine);
        return null;
      }
    }

    // --- the equations, rewritten as expressions which should be zero ---
    // an `And(...)` of equations is equivalent to a `List(...)` of equations
    IASTAppendable equations =
        DSolve.checkDSolveEquations(SolveUtils.toEquationList(ast.arg1()).makeList());
    if (equations.isNIL()) {
      return null;
    }
    // An initial condition names the independent variable only through a fixed point - y(0), y'(0)
    // - so the conditions are exactly the equations which are free of it.
    IASTAppendable initialConditions = F.ListAlloc(equations.argSize());
    int i = 1;
    while (i < equations.size()) {
      IExpr equation = equations.get(i);
      if (equation.isFree(timeVar)) {
        initialConditions.append(equation);
        equations.remove(i);
      } else {
        i++;
      }
    }
    if (equations.argSize() != numberOfFunctions) {
      // The system is underdetermined.
      Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
      return null;
    }

    // --- the order of each function, and with it the layout of the state vector ---
    int[] order = new int[numberOfFunctions];
    int[] offset = new int[numberOfFunctions];
    int dimension = 0;
    for (int j = 0; j < numberOfFunctions; j++) {
      offset[j] = dimension;
      for (int k = MAX_DERIVATIVE_ORDER; k >= 1; k--) {
        if (!equations.isFree(ODEUtils.derivative(functions[j], k, timeVar))) {
          order[j] = k;
          break;
        }
      }
      if (order[j] == 0) {
        // the function is never differentiated, so the equations do not determine it
        Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
        return null;
      }
      dimension += order[j];
    }

    IExpr[] stateExpressions = new IExpr[dimension];
    for (int j = 0; j < numberOfFunctions; j++) {
      for (int k = 0; k < order[j]; k++) {
        stateExpressions[offset[j] + k] = ODEUtils.derivative(functions[j], k, timeVar);
      }
    }

    // --- solve the equations for the highest derivatives ---
    // Every y^(n)(t) is replaced by a dummy symbol and the resulting algebraic system is handed to
    // Solve. The one mechanism covers y'(x)==y(x) and y''(x)+y(x)==0, which isolate directly, as
    // well as a pair such as u''+v''==-(u+v), u''-v''==-(u-v), which is only solvable for u'' and
    // v'' by treating the two equations as a system.
    ISymbol[] highest = new ISymbol[numberOfFunctions];
    IASTAppendable highestRules = F.ListAlloc(numberOfFunctions);
    for (int j = 0; j < numberOfFunctions; j++) {
      highest[j] = F.Dummy("NDSolve$" + functions[j].getSymbolName() + "$" + order[j]);
      highestRules.append(F.Rule(ODEUtils.derivative(functions[j], order[j], timeVar), highest[j]));
    }
    IASTAppendable solveEquations = F.ListAlloc(numberOfFunctions);
    for (int j = 1; j < equations.size(); j++) {
      solveEquations.append(F.Equal(F.subst(equations.get(j), highestRules), F.C0));
    }
    IExpr solution = engine.evaluate(F.Solve(solveEquations, F.List(highest)));
    IAST solutionRules = SolveUtils.firstSolutionRules(solution);
    if (solutionRules.isNIL()) {
      // The system is underdetermined.
      Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
      return null;
    }

    IExpr[] derivatives = new IExpr[dimension];
    for (int j = 0; j < numberOfFunctions; j++) {
      IExpr rhs = F.subst(highest[j], solutionRules);
      if (!isExplicit(rhs, highest)) {
        // the equations could not be solved for this highest derivative
        Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
        return null;
      }
      // y^(k)' is the next component of the state vector, for every k below the highest order
      for (int k = 0; k < order[j] - 1; k++) {
        derivatives[offset[j] + k] = stateExpressions[offset[j] + k + 1];
      }
      derivatives[offset[j] + order[j] - 1] = rhs;
    }

    // --- the initial conditions ---
    double[] initialState = new double[dimension];
    boolean[] isGiven = new boolean[dimension];
    double t0 = Double.NaN;
    for (int j = 1; j < initialConditions.size(); j++) {
      IExpr condition = initialConditions.get(j);
      IExpr application = findApplication(condition, functions);
      if (application.isNIL()) {
        // Equation or list of equations expected instead of `1` in the first argument `2`.
        Errors.printMessage(S.NDSolve, "deqn", F.List(condition, ast.arg1()), engine);
        return null;
      }
      int[] derivativeOrder = new int[1];
      int index = applicationIndex(application, functions, derivativeOrder);
      if (derivativeOrder[0] >= order[index]) {
        // a condition on a derivative the reduced system does not carry, e.g. y''(0) for a first
        // order equation
        Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
        return null;
      }
      double point = ((IAST) application).arg1().evalfNaN();
      if (Double.isNaN(point)) {
        return null;
      }
      if (Double.isNaN(t0)) {
        t0 = point;
      } else if (t0 != point) {
        // conditions spread over more than one point are a boundary value problem, which this
        // initial value solver cannot integrate
        Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
        return null;
      }
      double value = conditionValue(condition, application, engine);
      if (Double.isNaN(value)) {
        return null;
      }
      initialState[offset[index] + derivativeOrder[0]] = value;
      isGiven[offset[index] + derivativeOrder[0]] = true;
    }
    for (int j = 0; j < dimension; j++) {
      if (!isGiven[j]) {
        // The system is underdetermined.
        Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
        return null;
      }
    }

    return new FirstOrderSystem(timeVar, functions, appliedForm, offset, stateExpressions,
        derivatives, t0, initialState);
  }

  /**
   * Integrate the system and assemble the solution.
   *
   * @param head <code>NDSolve</code> or <code>NDSolveValue</code>, for the messages
   * @param system the reduced first order system
   * @param tMin the start of the requested range
   * @param tMax the end of the requested range
   * @param engine the evaluation engine
   * @param ruleForm <code>true</code> for <code>{{y->InterpolatingFunction(...)}}</code>,
   *        <code>false</code> for the interpolating function itself
   * @return the solution
   */
  private static IExpr integrate(ISymbol head, FirstOrderSystem system, double tMin, double tMax,
      MethodSettings method, EvalEngine engine, boolean ruleForm) {
    // The initial conditions are given at t0, which need not be the start of the requested range.
    // An interior t0 is propagated twice, once in each direction, because a dense output model runs
    // in one direction only - DenseOutputModel.append rejects a model of the opposite direction
    // with a propagation direction mismatch.
    DenseOutputModel forward =
        (tMax > system.t0) ? propagate(head, system, tMax, method, engine) : null;
    DenseOutputModel backward =
        (tMin < system.t0) ? propagate(head, system, tMin, method, engine) : null;
    if (forward == null && backward == null) {
      // the range is the single point t0, which is not something to integrate over
      return F.NIL;
    }
    // Read the domain back from the models rather than from the requested range: an event which
    // stopped the integration leaves the model ending where the event was found, and that shorter
    // interval is the one the solution is actually defined on.
    double domainMin = (backward != null) ? reachedEnd(backward, tMin) : system.t0;
    double domainMax = (forward != null) ? reachedEnd(forward, tMax) : system.t0;

    final int numberOfFunctions = system.functions.length;
    IASTAppendable solutions = F.ListAlloc(numberOfFunctions);
    for (int j = 0; j < numberOfFunctions; j++) {
      // the solution of function j is state component offset[j], the one holding y rather than one
      // of the derivatives the reduction introduced
      IExpr interpolation = InterpolatingFunctionExpr.newInstance(forward, backward, system.t0,
          system.offset[j], domainMin, domainMax);
      // y(x) as the second argument asks for the solution in the same applied form
      IExpr solution = system.appliedForm[j] //
          ? F.unaryAST1(interpolation, system.timeVar)
          : interpolation;
      solutions.append(ruleForm //
          ? F.Rule(system.appliedForm[j] //
              ? F.unaryAST1(system.functions[j], system.timeVar)
              : system.functions[j], solution)
          : solution);
    }
    // NDSolve wraps the rules in the outer list a Solve style result has, so that Part(s,1) picks
    // one solution. NDSolveValue hands back the function itself, and only makes a list of them
    // when there is more than one dependent function to hand back.
    if (ruleForm) {
      return F.list(solutions);
    }
    return numberOfFunctions == 1 ? solutions.arg1() : solutions;
  }

  /**
   * Where a propagation ended: the point an event stopped it at, or the requested bound when it ran
   * the whole way.
   *
   * <p>
   * The model's own final time is not used as-is for the second case. Accumulating steps can leave
   * it an ulp short of the target - <code>0.9999999999999999</code> for a range ending at
   * <code>1</code> - which would put the endpoint the caller asked about just outside the domain
   * and make sampling it report an extrapolation. Anything that close means the whole range was
   * covered, and the requested bound is the exact one to report.
   *
   * @param model the model a propagation produced
   * @param requested the time the propagation was asked to reach
   */
  private static double reachedEnd(DenseOutputModel model, double requested) {
    double actual = model.getFinalTime();
    double slack = 1.0e-9 * Math.max(1.0, Math.abs(requested));
    return (Math.abs(actual - requested) <= slack) ? requested : actual;
  }

  /**
   * Integrate the system from its initial point to <code>target</code>, keeping the continuous
   * extension the integrator builds as it steps.
   *
   * @param head <code>NDSolve</code> or <code>NDSolveValue</code>, for the messages
   * @param system the reduced first order system
   * @param target the time to integrate to, which may lie below <code>system.t0</code>
   * @param engine the evaluation engine
   * @return the dense output model covering the interval between the two times, which is the
   *         stretch which could be integrated when the stepping gave up before
   *         <code>target</code>
   */
  private static DenseOutputModel propagate(ISymbol head, FirstOrderSystem system, double target,
      MethodSettings method, EvalEngine engine) {
    AbstractIntegrator integrator = method.createIntegrator();
    integrator.setMaxEvaluations(MAX_EVALUATIONS);
    DenseOutputModel model = new DenseOutputModel();
    integrator.addStepHandler(model);
    LastStepRecorder recorder = new LastStepRecorder();
    integrator.addStepHandler(recorder);
    method.addEventDetectors(integrator, system, engine);
    try {
      // clone, so that the two directions both start from the initial conditions
      integrator.integrate(new FirstODE(engine, system),
          new ODEState(system.t0, system.initialState.clone()), target);
    } catch (MathRuntimeException mre) {
      // The stepping gave up before the requested time was reached - the step size collapsed at a
      // singularity, the state went to NaN, or the evaluation budget ran out. That is the ordinary
      // outcome of a shooting method whose current guess sends the trajectory to infinity well
      // short of the endpoint, so the stretch which was integrated is thrown away only if there is
      // none: `integrate` reads the domain of the solution back off the model rather than from the
      // requested range, so a model which stops early becomes a solution defined on the shorter
      // interval instead of no solution at all.
      if (recorder.lastState == null) {
        // not one step was taken, so there is nothing to hand back
        throw mre;
      }
      // `DenseOutputModel` learns its final time and the index it interpolates from in `finish`,
      // which only a completed integration calls - `init` left the final time at the target which
      // was never reached. Close the model here with the last state which was actually stepped to.
      model.finish(recorder.lastState);
      // At `1` == `2`, step size is effectively zero; singularity or stiff system suspected.
      Errors.printMessage(head, "ndsz",
          F.list(system.timeVar, F.num(recorder.lastState.getTime())), engine);
    }
    return model;
  }

  /**
   * Remembers the last step an integrator completed.
   *
   * <p>
   * A {@link DenseOutputModel} keeps every step, but does not say where the stepping stopped until
   * it is closed, and it is only closed by an integration which reached its target. This records
   * the state to close it with when one does not.
   */
  private static final class LastStepRecorder implements ODEStepHandler {
    /** The state at the end of the last completed step, or <code>null</code> if there was none. */
    private ODEStateAndDerivative lastState = null;

    @Override
    public void handleStep(ODEStateInterpolator interpolator) {
      lastState = interpolator.getCurrentState();
    }
  }

  /**
   * The settings of the <code>Method</code>, <code>AccuracyGoal</code> and
   * <code>PrecisionGoal</code> options.
   *
   * <pre>
   * Method -&gt; m                              (* a time integration method *)
   * Method -&gt; {"name", "sub" -&gt; value, ...}  (* a method with sub-options *)
   * Method -&gt; {"TimeIntegration" -&gt; m}       (* a named solution stage *)
   * Method -&gt; {"controller", Method -&gt; m}    (* a controller wrapping a submethod *)
   * </pre>
   */
  private static final class MethodSettings {
    /** The time integration method, one of the names {@link #createIntegrator()} knows. */
    String timeIntegration = "ExplicitRungeKutta";

    /** The absolute local error the integrator is asked for. */
    double absoluteTolerance = TOLERANCE;

    /** The relative local error the integrator is asked for. */
    double relativeTolerance = TOLERANCE;

    /** The largest step the integrator may take, and the interval events are checked over. */
    double maxStep = 100.0;

    /** One entry per <code>"Event"</code> expression; empty when no events were asked for. */
    IExpr[] events = new IExpr[0];

    /** The held right hand side of <code>"EventAction"</code>. */
    IExpr eventAction = F.binaryAST2(S.Throw, S.Null, F.$str(STOP_INTEGRATION));

    /** <code>"Direction"</code>: <code>1</code>, <code>-1</code> or <code>0</code> for All. */
    int direction = 0;

    /** <code>"EventCondition"</code>, or {@link F#NIL} when the event is unconditional. */
    IExpr eventCondition = F.NIL;

    /**
     * <code>"EventLocationMethod"</code>, one of <code>Brent</code>,
     * <code>LinearInterpolation</code>.
     */
    String eventLocationMethod = "Brent";

    /** The <code>"MaxIterations"</code> sub-option of the event location method. */
    int eventMaxIterations = 100;

    /**
     * Read the options.
     *
     * @param options the evaluated option values, indexed by {@link NDSolve#METHOD} and friends
     * @param ast the whole call, for messages
     * @param range the width of the requested range, used to bound the event check interval
     * @param engine the evaluation engine
     * @return the settings, or <code>null</code> if an option was not understood
     */
    static MethodSettings parse(IExpr[] options, IAST ast, double range, EvalEngine engine) {
      MethodSettings settings = new MethodSettings();
      if (options != null && options.length > PRECISION_GOAL) {
        // AccuracyGoal is the absolute local error, PrecisionGoal the relative one, both given as
        // a number of digits. Automatic keeps the default, which is tighter than the roughly eight
        // digits which are used at machine precision, because the accuracy of a located event is
        // bounded by it.
        double accuracy = digitsToTolerance(options[ACCURACY_GOAL]);
        if (!Double.isNaN(accuracy)) {
          settings.absoluteTolerance = accuracy;
        }
        double precision = digitsToTolerance(options[PRECISION_GOAL]);
        if (!Double.isNaN(precision)) {
          settings.relativeTolerance = precision;
        }
        if (!settings.parseMethod(options[METHOD], ast, engine)) {
          return null;
        }
      }
      if (range > 0.0) {
        settings.maxStep = Math.min(settings.maxStep, range / 10.0);
      }
      return settings;
    }

    /** Turn a number of digits into a tolerance, e.g. <code>8</code> into <code>10^-8</code>. */
    private static double digitsToTolerance(IExpr goal) {
      if (goal == null || !goal.isReal()) {
        return Double.NaN;
      }
      double digits = goal.evalfNaN();
      return Double.isNaN(digits) ? Double.NaN : Math.pow(10.0, -digits);
    }

    /** Read one <code>Method</code> value, which may nest. */
    private boolean parseMethod(IExpr method, IAST ast, EvalEngine engine) {
      if (method == null || method.isAutomatic()) {
        return true;
      }
      if (method.isString()) {
        return setTimeIntegration(method.toString(), ast, engine);
      }
      if (!method.isList()) {
        // Unknown option `1` in `2`.
        Errors.printMessage(ast.topHead(), "optx", F.List(F.Rule(S.Method, method), ast), engine);
        return false;
      }
      IAST list = (IAST) method;
      for (int i = 1; i < list.size(); i++) {
        IExpr entry = list.get(i);
        if (entry.isString()) {
          // the leading name of a method or of a controller such as "EventLocator"
          if (!"EventLocator".equals(entry.toString())
              && !setTimeIntegration(entry.toString(), ast, engine)) {
            return false;
          }
          continue;
        }
        if (!entry.isRuleAST()) {
          Errors.printMessage(ast.topHead(), "optx", F.List(entry, ast), engine);
          return false;
        }
        IExpr lhs = entry.first();
        IExpr rhs = entry.second();
        String name = lhs.isString() ? lhs.toString() : lhs.toString();
        switch (name) {
          case "TimeIntegration":
          case "Method":
            if (!parseMethod(rhs, ast, engine)) {
              return false;
            }
            break;
          case "Event":
            events = rhs.isList() ? ((IAST) rhs).argSize() == 0 ? new IExpr[0] : toArray((IAST) rhs)
                : new IExpr[] {rhs};
            break;
          case "EventAction":
            eventAction = rhs;
            break;
          case "Direction":
            direction = rhs == S.All ? 0 : rhs.toIntDefault();
            if (direction != 0 && direction != 1 && direction != -1) {
              Errors.printMessage(ast.topHead(), "optx", F.List(entry, ast), engine);
              return false;
            }
            break;
          case "EventCondition":
            eventCondition = rhs;
            break;
          case "EventLocationMethod":
            if (!setEventLocationMethod(rhs, ast, engine)) {
              return false;
            }
            break;
          default:
            // Unknown option `1` in `2`.
            Errors.printMessage(ast.topHead(), "optx", F.List(entry, ast), engine);
            return false;
        }
      }
      return true;
    }

    private static IExpr[] toArray(IAST list) {
      IExpr[] result = new IExpr[list.argSize()];
      for (int i = 0; i < result.length; i++) {
        result[i] = list.get(i + 1);
      }
      return result;
    }

    private boolean setEventLocationMethod(IExpr value, IAST ast, EvalEngine engine) {
      IExpr name = value;
      if (value.isList() && value.size() > 1) {
        name = value.first();
        for (int i = 2; i < value.size(); i++) {
          IExpr entry = ((IAST) value).get(i);
          if (entry.isRuleAST() && "MaxIterations".equals(entry.first().toString())) {
            int iterations = entry.second().toIntDefault();
            if (iterations > 0) {
              eventMaxIterations = iterations;
            }
          }
        }
      }
      if (name.isAutomatic()) {
        return true;
      }
      String method = name.toString();
      if ("Brent".equals(method) || "LinearInterpolation".equals(method)) {
        eventLocationMethod = method;
        return true;
      }
      // "StepBegin" and "StepEnd" report the event at a step boundary rather than solving for it,
      // which the integrator does not offer a hook for.
      Errors.printMessage(ast.topHead(), "optx",
          F.List(F.Rule(F.$str("EventLocationMethod"), value), ast), engine);
      return false;
    }

    private boolean setTimeIntegration(String name, IAST ast, EvalEngine engine) {
      switch (name) {
        case "ExplicitRungeKutta":
        case "Adams":
        case "Extrapolation":
        case "FixedStep":
          timeIntegration = name;
          return true;
        default:
          // The stiff solvers - "BDF", "StiffnessSwitching", "IDA", "ImplicitRungeKutta" and the
          // "LinearlyImplicit..." submethods - have no counterpart: the integrator library ships
          // explicit methods only.
          Errors.printMessage(ast.topHead(), "optx", F.List(F.Rule(S.Method, F.$str(name)), ast),
              engine);
          return false;
      }
    }

    /** Build the integrator the time integration method asks for. */
    AbstractIntegrator createIntegrator() {
      switch (timeIntegration) {
        case "Adams":
          return new AdamsMoultonIntegrator(4, MIN_STEP, maxStep, absoluteTolerance,
              relativeTolerance);
        case "Extrapolation":
          return new GraggBulirschStoerIntegrator(MIN_STEP, maxStep, absoluteTolerance,
              relativeTolerance);
        case "FixedStep":
          return new ClassicalRungeKuttaIntegrator(maxStep / 100.0);
        default:
          return new DormandPrince853Integrator(MIN_STEP, maxStep, absoluteTolerance,
              relativeTolerance);
      }
    }

    /** Register one detector per <code>"Event"</code> expression. */
    void addEventDetectors(AbstractIntegrator integrator, FirstOrderSystem system,
        EvalEngine engine) {
      for (int i = 0; i < events.length; i++) {
        ODEEventDetector detector = new EventLocator(system, engine, events[i], eventCondition,
            eventAction, createSolver(), maxStep, eventMaxIterations);
        if (direction != 0) {
          detector = new EventSlopeFilter<>(detector, direction > 0 //
              ? FilterType.TRIGGER_ONLY_INCREASING_EVENTS
              : FilterType.TRIGGER_ONLY_DECREASING_EVENTS);
        }
        integrator.addEventDetector(detector);
      }
    }

    private BracketedUnivariateSolver<UnivariateFunction> createSolver() {
      if ("LinearInterpolation".equals(eventLocationMethod)) {
        return new RegulaFalsiSolver(0, EVENT_THRESHOLD, 0);
      }
      return new BracketingNthOrderBrentSolver(0, EVENT_THRESHOLD, 0, 5);
    }
  }

  /**
   * Locates the points where an <code>"Event"</code> expression crosses zero and runs the
   * <code>"EventAction"</code> there.
   */
  private static final class EventLocator implements ODEEventDetector {
    private final FirstOrderSystem fSystem;
    private final EvalEngine fEngine;
    private final IExpr fEvent;
    private final IExpr fCondition;
    private final IExpr fAction;
    private final BracketedUnivariateSolver<UnivariateFunction> fSolver;
    private final double fMaxCheck;
    private final int fMaxIterations;

    EventLocator(FirstOrderSystem system, EvalEngine engine, IExpr event, IExpr condition,
        IExpr action, BracketedUnivariateSolver<UnivariateFunction> solver, double maxCheck,
        int maxIterations) {
      this.fSystem = system;
      this.fEngine = engine;
      this.fEvent = event;
      this.fCondition = condition;
      this.fAction = action;
      this.fSolver = solver;
      this.fMaxCheck = maxCheck;
      this.fMaxIterations = maxIterations;
    }

    @Override
    public double g(ODEStateAndDerivative state) {
      return substituted(fEvent, state).evalfNaN();
    }

    @Override
    public AdaptableInterval getMaxCheckInterval() {
      return AdaptableInterval.of(fMaxCheck);
    }

    @Override
    public int getMaxIterationCount() {
      return fMaxIterations;
    }

    @Override
    public BracketedUnivariateSolver<UnivariateFunction> getSolver() {
      return fSolver;
    }

    @Override
    public ODEEventHandler getHandler() {
      return (state, detector, increasing) -> {
        if (fCondition.isPresent() && !substituted(fCondition, state).isTrue()) {
          return Action.CONTINUE;
        }
        try {
          // the action is held, so that t and the dependent functions in it are the numbers of the
          // event rather than whatever they happened to be when NDSolve was called
          fEngine.evaluate(substituted(fAction, state));
        } catch (ThrowException tex) {
          if (F.$str(STOP_INTEGRATION).equals(tex.getTag())) {
            return Action.STOP;
          }
          throw tex;
        }
        return Action.CONTINUE;
      };
    }

    /** Evaluate an expression with the numbers of this event substituted into it. */
    private IExpr substituted(IExpr expr, ODEStateAndDerivative state) {
      return fEngine
          .evaluate(F.subst(expr, fSystem.substitutions(state.getTime(), state.getPrimaryState())));
    }
  }

  /**
   * <code>true</code> if <code>rhs</code> is an explicit expression for a highest derivative, that
   * is if the solver returned something which no longer mentions any of the unknowns.
   */
  private static boolean isExplicit(IExpr rhs, ISymbol[] highest) {
    for (int i = 0; i < highest.length; i++) {
      if (!rhs.isFree(highest[i])) {
        return false;
      }
    }
    return true;
  }

  /**
   * Locate the application of a dependent function in an initial condition, e.g. <code>y'(0)</code>
   * in <code>-1+y'(0)</code>.
   *
   * @param expr the initial condition
   * @param functions the dependent function symbols
   * @return the application, or {@link F#NIL} if the condition does not contain exactly one
   */
  private static IExpr findApplication(IExpr expr, ISymbol[] functions) {
    if (!expr.isAST()) {
      return F.NIL;
    }
    if (applicationIndex(expr, functions, new int[1]) >= 0) {
      return expr;
    }
    IAST ast = (IAST) expr;
    IExpr found = F.NIL;
    for (int i = 0; i < ast.size(); i++) {
      IExpr temp = findApplication(ast.get(i), functions);
      if (temp.isPresent()) {
        if (found.isPresent() && !found.equals(temp)) {
          // more than one unknown - not an initial condition this solver can read
          return F.NIL;
        }
        found = temp;
      }
    }
    return found;
  }

  /**
   * The index of the dependent function applied in <code>expr</code>.
   *
   * @param expr the candidate expression, e.g. <code>y'(0)</code>
   * @param functions the dependent function symbols
   * @param derivativeOrder receives the derivative order of the application
   * @return the index in <code>functions</code>, or <code>-1</code> if this is not an application
   *         of one of them
   */
  private static int applicationIndex(IExpr expr, ISymbol[] functions, int[] derivativeOrder) {
    if (!expr.isAST1()) {
      return -1;
    }
    IAST[] deriveExpr = expr.isDerivativeAST1();
    if (deriveExpr != null) {
      int order = ODEUtils.derivativeOrder(deriveExpr);
      if (order < 0) {
        return -1;
      }
      IExpr function = deriveExpr[1].arg1();
      for (int i = 0; i < functions.length; i++) {
        if (functions[i].equals(function)) {
          derivativeOrder[0] = order;
          return i;
        }
      }
      return -1;
    }
    for (int i = 0; i < functions.length; i++) {
      if (functions[i].equals(expr.head())) {
        derivativeOrder[0] = 0;
        return i;
      }
    }
    return -1;
  }

  /**
   * Solve an initial condition for the value of the function application it constrains. Going
   * through the solver rather than reading off a term keeps conditions such as
   * <code>y(0)==-ArcCos(31/40)</code>, whose value is only a number after a numeric evaluation, on
   * the same path as <code>y(0)==1</code>.
   *
   * @param condition the initial condition, as an expression which should be zero
   * @param application the function application it constrains, e.g. <code>y(0)</code>
   * @param engine the evaluation engine
   * @return the value, or {@link Double#NaN} if it is not a number
   */
  private static double conditionValue(IExpr condition, IExpr application, EvalEngine engine) {
    ISymbol unknown = F.Dummy("NDSolve$value");
    IExpr solution = engine.evaluate(
        F.Solve(F.List(F.Equal(F.subst(condition, F.List(F.Rule(application, unknown))), F.C0)),
            F.List(unknown)));
    IAST rules = SolveUtils.firstSolutionRules(solution);
    if (rules.isNIL()) {
      return Double.NaN;
    }
    return engine.evalN(F.subst(unknown, rules)).evalfNaN();
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    // three arguments plus any trailing options
    return IFunctionEvaluator.ARGS_3_INFINITY;
  }
}
