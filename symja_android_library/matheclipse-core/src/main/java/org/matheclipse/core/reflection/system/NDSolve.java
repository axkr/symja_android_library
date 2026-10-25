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
import org.hipparchus.ode.nonstiff.AdaptiveStepsizeIntegrator;
import org.hipparchus.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.hipparchus.ode.nonstiff.DormandPrince54Integrator;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.hipparchus.ode.nonstiff.EulerIntegrator;
import org.hipparchus.ode.nonstiff.GraggBulirschStoerIntegrator;
import org.hipparchus.ode.nonstiff.LutherIntegrator;
import org.hipparchus.ode.nonstiff.MidpointIntegrator;
import org.hipparchus.ode.sampling.ODEStateInterpolator;
import org.hipparchus.ode.sampling.ODEStepHandler;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
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

  /**
   * An <code>"Event"</code> function this close to zero where the integration starts, relative to
   * the size of the initial state, starts at zero: it has to move away from it before reaching
   * zero is an event.
   */
  private static final double START_ZERO_TOLERANCE = 1.0e-8;

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

    /**
     * The factored constant matrix <code>M</code> of equations <code>M.h == f</code> which are
     * linear in the highest derivatives <code>h</code>, or <code>null</code> if the highest
     * derivatives are given explicitly. Row <code>j</code> belongs to equation <code>j</code>,
     * column <code>k</code> to function <code>k</code>. When it is set, the right hand side at the
     * highest derivative of function <code>j</code> holds <code>f</code> of equation
     * <code>j</code>, and the derivatives are found by solving with the matrix.
     */
    org.hipparchus.linear.DecompositionSolver massMatrix = null;

    /** The state component of the highest derivative of every function. */
    int[] highestComponents = null;

    /**
     * The curve which algebraic equations leave, when the derivatives follow its tangent rather
     * than the right hand sides, or <code>null</code>.
     */
    ImplicitCurve curve = null;

    /**
     * This system started at <code>start</code> from <code>state</code>, with the right hand
     * sides <code>derivatives</code>.
     */
    FirstOrderSystem restart(IExpr[] derivatives, double start, double[] state) {
      FirstOrderSystem system = new FirstOrderSystem(timeVar, functions, appliedForm, offset,
          stateExpressions, derivatives, start, state);
      system.massMatrix = massMatrix;
      system.highestComponents = highestComponents;
      system.curve = curve;
      return system;
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

    /**
     * The rules of {@link #substitutions(double, double[])}, together with one for the highest
     * derivative of every function, <code>x'(t)</code> of a first order equation, which is not a
     * component of the state but its derivative.
     *
     * @param t the time to substitute
     * @param state the state vector to substitute
     * @param stateDot the derivative of the state vector at <code>t</code>
     */
    IAST substitutions(double t, double[] state, double[] stateDot) {
      IASTAppendable rules = F.ListAlloc(dimension() + functions.length + 1);
      for (int j = 0; j < functions.length; j++) {
        int blockEnd = (j + 1 < functions.length ? offset[j + 1] : dimension()) - 1;
        rules.append(F.Rule(ODEUtils.derivative(functions[j], blockEnd - offset[j] + 1, timeVar),
            F.num(stateDot[blockEnd])));
      }
      rules.appendArgs(substitutions(t, state));
      return rules;
    }

    /** The index of the state component <code>expr</code>, e.g. <code>y'(t)</code>, or -1. */
    int componentIndex(IExpr expr) {
      for (int i = 0; i < stateExpressions.length; i++) {
        if (stateExpressions[i].equals(expr)) {
          return i;
        }
      }
      return -1;
    }
  }

  private static class FirstODE implements OrdinaryDifferentialEquation {
    private final EvalEngine fEngine;
    private final FirstOrderSystem fSystem;

    /**
     * The speed along the tangent of {@link FirstOrderSystem#curve} at the last evaluation, where
     * the search for the next one starts.
     */
    private double fSpeed;

    public FirstODE(EvalEngine engine, FirstOrderSystem system) {
      this.fEngine = engine;
      this.fSystem = system;
      this.fSpeed = system.curve != null ? system.curve.initialSpeed : 0.0;
    }

    @Override
    public int getDimension() {
      return fSystem.dimension();
    }

    @Override
    public double[] computeDerivatives(double t, double[] state) {
      if (fSystem.curve != null) {
        double[] speed = {fSpeed};
        double[] stateDot = fSystem.curve.derivatives(t, state, speed, fEngine);
        fSpeed = speed[0];
        return stateDot;
      }
      final int dimension = fSystem.dimension();
      IAST rules = fSystem.substitutions(t, state);
      double[] stateDot = new double[dimension];
      for (int i = 0; i < dimension; i++) {
        stateDot[i] = fEngine.evalN(F.subst(fSystem.derivatives[i], rules)).evalfNaN();
      }
      if (fSystem.massMatrix != null) {
        final int[] highest = fSystem.highestComponents;
        double[] force = new double[highest.length];
        for (int j = 0; j < highest.length; j++) {
          force[j] = stateDot[highest[j]];
        }
        double[] solved = fSystem.massMatrix
            .solve(new org.hipparchus.linear.ArrayRealVector(force, false)).toArray();
        for (int j = 0; j < highest.length; j++) {
          stateDot[highest[j]] = solved[j];
        }
      }
      return stateDot;
    }
  }

  /**
   * <code>WhenEvent(cond, action)</code> stays as it is: it only has a meaning among the equations
   * of {@link NDSolve}, which reads it. Its arguments are held, so that the action runs at an event
   * rather than when the equations are evaluated.
   */
  public static final class WhenEvent extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, final EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
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
  /**
   * Whether <code>variable</code> is a call <code>x(...)</code> of one of the functions
   * <code>x</code> to solve for, given like <code>x</code>, <code>x(t)</code> or a list of them.
   */
  private static boolean isDependentFunctionCall(IExpr variable, IExpr dependentVariables) {
    IExpr head = variable.head();
    if (dependentVariables.isList()) {
      return ((IAST) dependentVariables).exists(v -> v.equals(head) || v.head().equals(head));
    }
    return dependentVariables.equals(head) || dependentVariables.head().equals(head);
  }

  /** A partial differential equation, the time range given third and the space range fourth. */
  private static IExpr solvePDE(IAST ast, EvalEngine engine, boolean ruleForm) {
    IExpr solution = NDSolvePDE.solve(ast, engine, ruleForm);
    if (solution.isPresent()) {
      return solution;
    }
    // a system, the unknowns written u(x, t), or ends with a flux
    return NDSolvePDESystem.solve(ast, engine, ruleForm);
  }

  /**
   * Take the <code>WhenEvent(cond, action)</code> entries out of the equations, and thread an
   * equation between two lists, <code>{x(0), y(0)} == {0, 1}</code>, into the equations of their
   * elements.
   *
   * @param ast the whole <code>NDSolve(...)</code> expression
   * @param whenEvents receives the <code>WhenEvent</code> entries
   * @return <code>ast</code> with the remaining equations, or <code>ast</code> itself if there was
   *         nothing to take out or to thread
   */
  private static IAST separateEquations(IAST ast, java.util.List<IAST> whenEvents) {
    IExpr equations = ast.arg1();
    if (!(equations.isList() || equations.isAnd() || equations.isAST(S.WhenEvent, 3)
        || equations.isEqual())) {
      return ast;
    }
    IASTAppendable result = F.ListAlloc();
    boolean[] changed = new boolean[1];
    separateEquations(equations, result, whenEvents, changed);
    return changed[0] ? ast.setAtCopy(1, result) : ast;
  }

  private static void separateEquations(IExpr expr, IASTAppendable result,
      java.util.List<IAST> whenEvents, boolean[] changed) {
    if (expr.isList() || expr.isAnd()) {
      for (IExpr arg : (IAST) expr) {
        separateEquations(arg, result, whenEvents, changed);
      }
      return;
    }
    if (expr.isAST(S.WhenEvent, 3)) {
      whenEvents.add((IAST) expr);
      changed[0] = true;
      return;
    }
    if (expr.isEqual() && expr.first().isList() && expr.second().isList()
        && expr.first().argSize() == expr.second().argSize()) {
      IAST lhs = (IAST) expr.first();
      IAST rhs = (IAST) expr.second();
      for (int i = 1; i < lhs.size(); i++) {
        separateEquations(F.Equal(lhs.get(i), rhs.get(i)), result, whenEvents, changed);
      }
      changed[0] = true;
      return;
    }
    result.append(expr);
  }

  static IExpr solve(IAST ast, final IExpr[] options, EvalEngine engine, boolean ruleForm) {
    if (!ast.arg3().isList()) {
      return F.NIL;
    }
    IAST indexed = indexedFunctions(ast);
    if (indexed.argSize() > 0) {
      // functions written a(1), u(i): solved for symbols in their place
      IASTAppendable toSymbols = F.ListAlloc(indexed.argSize());
      IASTAppendable fromSymbols = F.ListAlloc(indexed.argSize());
      for (int i = 1; i < indexed.size(); i++) {
        // distinct names, which the solver tells the unknowns apart by
        ISymbol dummy = F.Dummy("NDSolve$f" + i);
        toSymbols.append(F.Rule(indexed.get(i), dummy));
        fromSymbols.append(F.Rule(dummy, indexed.get(i)));
      }
      IExpr solution = solve((IAST) F.subst(ast, toSymbols), options, engine, ruleForm);
      return solution.isPresent() ? F.subst(solution, fromSymbols) : F.NIL;
    }
    if (ast.arg2().makeList().exists(NDSolve::isDerivativeCall)) {
      // solved for the functions, whose derivatives are asked for; not tried a second time
      return solveForDerivatives(ast, options, engine, ruleForm);
    }
    java.util.List<IAST> whenEvents = new java.util.ArrayList<IAST>();
    ast = separateEquations(ast, whenEvents);
    if (ast.argSize() >= 4 && ast.arg4().isList3()) {
      // a second range: a partial differential equation in one space variable
      IExpr solution = solvePDE(ast, engine, ruleForm);
      if (solution.isNIL() && ast.arg3().isList3()) {
        // the ranges may be given space first: {x, x0, x1}, {t, t0, t1}
        IASTAppendable swapped = ast.copyAppendable();
        swapped.set(3, ast.arg4());
        swapped.set(4, ast.arg3());
        solution = solvePDE(swapped, engine, ruleForm);
      }
      if (solution.isPresent()) {
        return solution;
      }
    }
    final IAST tRangeList = (IAST) ast.arg3();
    if (!(tRangeList.isAST2() || tRangeList.isAST3())) {
      return F.NIL;
    }
    try {
      if (tRangeList.arg1().isAST() && !tRangeList.arg1().isNumericFunction(true)
          && !isDependentFunctionCall(tRangeList.arg1(), ast.arg2())) {
        // an independent variable like Subscript(a, m): solve for a symbol in its place
        final IExpr variable = tRangeList.arg1();
        final ISymbol dummy = F.Dummy();
        IExpr solution =
            solve((IAST) F.subst(ast, F.Rule(variable, dummy)), options, engine, ruleForm);
        return solution.isPresent() ? F.subst(solution, F.Rule(dummy, variable)) : F.NIL;
      }
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
      final double tStart = tMinExpr.evalfNaN();
      final double tEnd = tMaxExpr.evalfNaN();
      if (Double.isNaN(tStart) || Double.isNaN(tEnd)) {
        return F.NIL;
      }
      // a range like {t, 0, -2} is the interval from -2 to 0
      final double tMin = Math.min(tStart, tEnd);
      final double tMax = Math.max(tStart, tEnd);

      // y(t /; t <= t0) == h(t): a delay differential equation with its history
      IExpr delayed = NDSolveDelay.solve(ast, timeVar, tMin, tMax, engine, ruleForm);
      if (delayed.isPresent()) {
        return delayed;
      }
      FirstOrderSystem system = reduceToFirstOrder(ast, timeVar, engine);
      if (system == null) {
        return F.NIL;
      }
      MethodSettings method = MethodSettings.parse(options, ast, tMax - tMin, engine);
      if (method == null) {
        return F.NIL;
      }
      method.whenEvents = whenEvents;
      return integrate(ast.topHead(), system, tMin, tMax, method, engine, ruleForm);
    } catch (LimitException le) {
      throw le;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return Errors.printMessage(ast.topHead(), rex, engine);
    }
  }

  /**
   * <code>NDSolve(eqns, {y''(t), y'(t), y(t)}, ...)</code>: the equations are solved for the
   * functions whose derivatives are asked for, and each of the requested expressions is the
   * derivative of the solution.
   *
   * @return {@link F#NIL} if the functions could not be solved for
   */
  private static IExpr solveForDerivatives(final IAST ast, final IExpr[] options,
      EvalEngine engine, boolean ruleForm) {
    IAST requested = ast.arg2().makeList();
    IASTAppendable functions = F.ListAlloc(requested.argSize());
    for (IExpr variable : requested) {
      IExpr function = isDerivativeCall(variable) ? variable.head().first()
          : variable.isAST1() ? variable.head() : variable;
      if (!function.isSymbol()) {
        return F.NIL;
      }
      if (!functions.contains(function)) {
        functions.append(function);
      }
    }
    IExpr solution = solve(ast.setAtCopy(2, functions), options, engine, true);
    if (!solution.isListOfLists() || solution.argSize() != 1) {
      return F.NIL;
    }
    IAST rules = (IAST) solution.first();
    IAST values = F.mapList(requested, variable -> engine.evaluate(F.subst(variable, rules)));
    if (ruleForm) {
      return F.list(F.mapRange(1, requested.size(),
          i -> F.Rule(requested.get(i), values.get(i))));
    }
    return values.argSize() == 1 ? values.arg1() : values;
  }

  /**
   * The functions to solve for which are not symbols but indexed, like <code>a(1)</code> given as
   * <code>a(1)</code>, <code>a(1)(t)</code> or <code>a(1)'(t)</code>.
   *
   * @return the distinct indexed functions, possibly none
   */
  private static IAST indexedFunctions(IAST ast) {
    IAST range = (IAST) ast.arg3();
    if (range.argSize() < 1) {
      return F.CEmptyList;
    }
    IExpr timeVar = range.arg1();
    IASTAppendable result = F.ListAlloc();
    for (IExpr variable : ast.arg2().makeList()) {
      IExpr function = variable;
      if (variable.isAST1() && variable.head().isAST1()
          && variable.head().head().isAST(S.Derivative, 2)) {
        // a(1)'(t)
        function = variable.head().first();
      } else if (variable.isAST1() && variable.head().isAST()
          && variable.first().equals(timeVar)) {
        // a(1)(t)
        function = variable.head();
      }
      if (function.isAST() && !function.isList() && function.head().isSymbol()
          && !function.isAST(S.Derivative) && function.isFree(timeVar)
          && !function.isNumericFunction(true) && !result.contains(function)) {
        result.append(function);
      }
    }
    return result;
  }

  /** <code>Derivative(k)[y][t]</code> */
  private static boolean isDerivativeCall(IExpr variable) {
    return variable.isAST1() && variable.head().isAST1()
        && variable.head().head().isAST(S.Derivative, 2) && variable.head().first().isSymbol();
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
    IExpr[] reduced = new IExpr[numberOfFunctions];
    int algebraic = 0;
    boolean firstOrder = true;
    for (int j = 0; j < numberOfFunctions; j++) {
      reduced[j] = F.subst(equations.get(j + 1), highestRules);
      if (isExplicit(reduced[j], highest)) {
        // an algebraic equation: free of every highest derivative
        algebraic++;
      }
      firstOrder &= order[j] == 1;
    }

    IExpr[] derivatives = new IExpr[dimension];
    org.hipparchus.linear.DecompositionSolver massMatrix = null;
    ImplicitCurve curve = null;
    if (algebraic > 0) {
      // Algebraic equations which leave a curve, and one differential equation which says how fast
      // it is followed - a unit speed x'(t)^2+y'(t)^2==1 on the circle x(t)^2+y(t)^2==1, say.
      if (firstOrder && algebraic == numberOfFunctions - 1) {
        curve = ImplicitCurve.create(reduced, functions, highest, timeVar, engine);
      }
      if (curve == null) {
        // The system is underdetermined.
        Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
        return null;
      }
      java.util.Arrays.fill(derivatives, F.C0);
    } else {
      IExpr[] rhs = new IExpr[numberOfFunctions];
      IExpr[][] coefficients = linearCoefficients(reduced, highest, engine);
      int[] pivots = coefficients == null ? null : pivots(coefficients);
      if (pivots != null) {
        // every equation names one highest derivative, linearly with a constant coefficient: it
        // is isolated directly
        for (int j = 0; j < numberOfFunctions; j++) {
          IExpr force = engine.evaluate(F.subst(reduced[j], zeroRules(highest)));
          rhs[pivots[j]] =
              engine.evaluate(F.Divide(F.Negate(force), coefficients[j][pivots[j]]));
        }
      } else {
        IAST solutionRules = F.NIL;
        if (coefficients == null || numberOfFunctions <= MAX_SOLVED_MASS_MATRIX) {
          IASTAppendable solveEquations = F.ListAlloc(numberOfFunctions);
          for (int j = 0; j < numberOfFunctions; j++) {
            solveEquations.append(F.Equal(reduced[j], F.C0));
          }
          IExpr solution = engine.evaluate(F.Solve(solveEquations, F.List(highest)));
          solutionRules = SolveUtils.firstSolutionRules(solution);
        }
        if (solutionRules.isPresent()) {
          for (int j = 0; j < numberOfFunctions; j++) {
            rhs[j] = F.subst(highest[j], solutionRules);
          }
        } else if (coefficients != null) {
          // M.h == f with a constant matrix M: factored once, and solved with at every step
          massMatrix = factorMassMatrix(coefficients);
          if (massMatrix != null) {
            for (int j = 0; j < numberOfFunctions; j++) {
              rhs[j] = engine.evaluate(F.Negate(F.subst(reduced[j], zeroRules(highest))));
            }
          }
        }
        if (solutionRules.isNIL() && massMatrix == null) {
          // The system is underdetermined.
          Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
          return null;
        }
      }
      for (int j = 0; j < numberOfFunctions; j++) {
        if (!isExplicit(rhs[j], highest)) {
          // the equations could not be solved for this highest derivative
          Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
          return null;
        }
        // y^(k)' is the next component of the state vector, for every k below the highest order
        for (int k = 0; k < order[j] - 1; k++) {
          derivatives[offset[j] + k] = stateExpressions[offset[j] + k + 1];
        }
        derivatives[offset[j] + order[j] - 1] = rhs[j];
      }
    }

    // --- the conditions: the value of a component of the state at a point ---
    initialConditions = decoupleConditions(initialConditions, functions, engine);
    int conditionCount = initialConditions.argSize();
    double[] points = new double[conditionCount];
    int[] components = new int[conditionCount];
    double[] values = new double[conditionCount];
    // the derivatives a curve is started with, at their points: they choose its direction
    double[] curvePoints = new double[numberOfFunctions];
    double[] curveDerivatives = new double[numberOfFunctions];
    java.util.Arrays.fill(curveDerivatives, Double.NaN);
    int skipped = 0;
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
      if (curve != null && derivativeOrder[0] == 1) {
        double point = ((IAST) application).arg1().evalfNaN();
        double value = conditionValue(condition, application, engine);
        if (Double.isNaN(point) || Double.isNaN(value)) {
          return null;
        }
        curvePoints[index] = point;
        curveDerivatives[index] = value;
        skipped++;
        continue;
      }
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
      double value = conditionValue(condition, application, engine);
      if (Double.isNaN(value)) {
        return null;
      }
      points[j - 1 - skipped] = point;
      components[j - 1 - skipped] = offset[index] + derivativeOrder[0];
      values[j - 1 - skipped] = value;
    }
    if (skipped > 0) {
      conditionCount -= skipped;
      points = java.util.Arrays.copyOf(points, conditionCount);
      components = java.util.Arrays.copyOf(components, conditionCount);
      values = java.util.Arrays.copyOf(values, conditionCount);
    }

    // --- the initial state at the point with the most conditions ---
    // conditions at one point are an initial value problem; at more points a boundary value
    // problem, whose missing initial values are found by shooting
    double t0 = Double.NaN;
    int mostConditions = 0;
    for (int j = 0; j < conditionCount; j++) {
      int count = 0;
      for (int k = 0; k < conditionCount; k++) {
        if (points[k] == points[j]) {
          count++;
        }
      }
      if (count > mostConditions || (count == mostConditions && points[j] < t0)) {
        mostConditions = count;
        t0 = points[j];
      }
    }
    double[] initialState = new double[dimension];
    boolean[] isGiven = new boolean[dimension];
    int boundaryConditions = 0;
    for (int j = 0; j < conditionCount; j++) {
      if (points[j] == t0) {
        initialState[components[j]] = values[j];
        isGiven[components[j]] = true;
      } else {
        boundaryConditions++;
      }
    }
    int unknowns = 0;
    for (int j = 0; j < dimension; j++) {
      if (!isGiven[j]) {
        unknowns++;
      }
    }
    if (unknowns != boundaryConditions || Double.isNaN(t0)) {
      // The system is underdetermined.
      Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
      return null;
    }
    FirstOrderSystem system = new FirstOrderSystem(timeVar, functions, appliedForm, offset,
        stateExpressions, derivatives, t0, initialState);
    if (massMatrix != null) {
      system.massMatrix = massMatrix;
      system.highestComponents = new int[numberOfFunctions];
      for (int j = 0; j < numberOfFunctions; j++) {
        system.highestComponents[j] = offset[j] + order[j] - 1;
      }
    }
    if (curve != null) {
      for (int j = 0; j < numberOfFunctions; j++) {
        if (curvePoints[j] != t0) {
          // only a derivative where the curve starts tells which way it is followed
          curveDerivatives[j] = Double.NaN;
        }
      }
      if (boundaryConditions > 0 || !curve.start(t0, initialState, curveDerivatives, engine)) {
        // The system is underdetermined.
        Errors.printMessage(S.NDSolve, "underdet", F.CEmptyList, engine);
        return null;
      }
      system.curve = curve;
    }
    if (boundaryConditions > 0 && !shoot(system, isGiven, points, components, values, engine)) {
      // The boundary value problem could not be solved by shooting from `1`.
      Errors.printMessage(S.NDSolve, "bvnc", F.list(F.num(t0)), engine);
      return null;
    }
    return system;
  }

  /** The most Newton steps of the shooting method. */
  private static final int MAX_SHOOTING_STEPS = 30;

  /** How often a Newton step which doesn't reduce the differences is halved. */
  private static final int MAX_STEP_HALVINGS = 10;

  /**
   * Find the initial values at <code>system.t0</code> which aren't given, so that the trajectory
   * from there meets the conditions at the other points: Newton's method on the differences at
   * those points, with a Jacobian of finite differences. A step which doesn't reduce the
   * differences is halved. A linear problem is solved by the first step.
   *
   * @param system the system whose initial state is completed in place
   * @param isGiven the components of the initial state which are given
   * @param points the points of all conditions
   * @param components the component of the state each condition is about
   * @param values the value of the component each condition requires
   * @return <code>false</code> if no solution was found
   */
  private static boolean shoot(FirstOrderSystem system, boolean[] isGiven, double[] points,
      int[] components, double[] values, EvalEngine engine) {
    final double[] state = system.initialState;
    final int dimension = state.length;
    int[] free = new int[dimension];
    int n = 0;
    for (int j = 0; j < dimension; j++) {
      if (!isGiven[j]) {
        free[n++] = j;
      }
    }
    double scale = 1.0;
    for (int j = 0; j < points.length; j++) {
      scale = Math.max(scale, Math.abs(values[j]));
    }
    double[] residual = boundaryResiduals(system, points, components, values, engine);
    if (residual == null) {
      return false;
    }
    double norm = maxNorm(residual);
    for (int step = 0; step < MAX_SHOOTING_STEPS; step++) {
      if (norm <= 1.0e-9 * scale) {
        return true;
      }
      double[][] jacobian = new double[n][n];
      for (int j = 0; j < n; j++) {
        final double saved = state[free[j]];
        final double h = 1.0e-6 * Math.max(1.0, Math.abs(saved));
        state[free[j]] = saved + h;
        double[] shifted = boundaryResiduals(system, points, components, values, engine);
        state[free[j]] = saved;
        if (shifted == null) {
          return false;
        }
        for (int i = 0; i < n; i++) {
          jacobian[i][j] = (shifted[i] - residual[i]) / h;
        }
      }
      double[] delta;
      try {
        org.hipparchus.linear.DecompositionSolver solver =
            new org.hipparchus.linear.LUDecomposition(
                new org.hipparchus.linear.Array2DRowRealMatrix(jacobian, false)).getSolver();
        delta = solver.solve(new org.hipparchus.linear.ArrayRealVector(residual, false))
            .toArray();
      } catch (MathRuntimeException mre) {
        // the conditions don't depend on the unknown initial values
        return false;
      }
      double[] start = state.clone();
      double factor = 1.0;
      double[] next = null;
      for (int halving = 0; halving <= MAX_STEP_HALVINGS; halving++) {
        for (int j = 0; j < n; j++) {
          state[free[j]] = start[free[j]] - factor * delta[j];
        }
        next = boundaryResiduals(system, points, components, values, engine);
        if (next != null && maxNorm(next) < norm) {
          break;
        }
        next = null;
        factor /= 2.0;
      }
      if (next == null) {
        // no step along the Newton direction gets closer to the conditions
        System.arraycopy(start, 0, state, 0, dimension);
        return false;
      }
      residual = next;
      norm = maxNorm(residual);
    }
    return norm <= 1.0e-9 * scale;
  }

  private static double maxNorm(double[] vector) {
    double norm = 0.0;
    for (double v : vector) {
      norm = Math.max(norm, Math.abs(v));
    }
    return norm;
  }

  /**
   * The differences between the components of the trajectory from the initial state of
   * <code>system</code> and the values the conditions at the points other than
   * <code>system.t0</code> require. The trajectory is integrated once to each such point.
   *
   * @return <code>null</code> if the integration failed
   */
  private static double[] boundaryResiduals(FirstOrderSystem system, double[] points,
      int[] components, double[] values, EvalEngine engine) {
    int count = 0;
    for (double point : points) {
      if (point != system.t0) {
        count++;
      }
    }
    double[] residual = new double[count];
    double[][] stateAt = new double[points.length][];
    FirstODE ode = new FirstODE(engine, system);
    int i = 0;
    for (int j = 0; j < points.length; j++) {
      if (points[j] == system.t0) {
        continue;
      }
      for (int k = 0; k < j && stateAt[j] == null; k++) {
        if (points[k] == points[j]) {
          stateAt[j] = stateAt[k];
        }
      }
      if (stateAt[j] == null) {
        engine.checkTimeLimit();
        double distance = Math.abs(points[j] - system.t0);
        try {
          DormandPrince853Integrator integrator =
              new DormandPrince853Integrator(MIN_STEP * distance, distance, 1.0e-12, 1.0e-12);
          integrator.setMaxEvaluations(MAX_EVALUATIONS);
          stateAt[j] = integrator
              .integrate(ode, new ODEState(system.t0, system.initialState.clone()), points[j])
              .getPrimaryState();
        } catch (MathRuntimeException mre) {
          return null;
        }
      }
      residual[i++] = stateAt[j][components[j]] - values[j];
    }
    return residual;
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
    if (forward == null && backward == null && (tMin != system.t0 || tMax != system.t0)) {
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
      // the range of the single point t0 has the initial value only
      // the components up to blockEnd are y, y', ..., the derivatives of the function
      int blockEnd =
          (j + 1 < numberOfFunctions ? system.offset[j + 1] : system.dimension()) - 1;
      IExpr interpolation = forward == null && backward == null
          ? InterpolatingFunctionExpr.newInstance(system.initialState, system.t0, system.offset[j])
          : InterpolatingFunctionExpr.newInstance(forward, backward, system.t0, system.offset[j],
              blockEnd, domainMin, domainMax);
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
    if (Double.isInfinite(requested)) {
      // a range like {t, 0, Infinity} ends where an event stopped it or the stepping gave up
      return actual;
    }
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
    java.util.List<IExpr> jumps = new java.util.ArrayList<>();
    double[] breaks = breakPoints(system, system.t0, target, jumps, engine);
    if (breaks.length == 0) {
      return propagateSmooth(head, system, target, method, engine, null);
    }
    // The right hand side jumps at the break points - HeavisideTheta(t-1) at t == 1 - where a step
    // size control would shrink its step for ever. Between two of them it is smooth: every
    // stretch is integrated on its own, with the jumping terms held at the value they have inside
    // it, and started from the state the stretch before ended in.
    DenseOutputModel whole = null;
    double from = system.t0;
    double[] state = system.initialState.clone();
    for (int k = 0; k <= breaks.length; k++) {
      double to = k < breaks.length ? breaks[k] : target;
      double middle = 0.5 * (from + to);
      IASTAppendable frozen = F.ListAlloc(jumps.size());
      for (IExpr jump : jumps) {
        frozen.append(F.Rule(jump,
            engine.evalN(F.subst(jump, F.Rule(system.timeVar, F.num(middle))))));
      }
      IExpr[] derivatives = new IExpr[system.derivatives.length];
      for (int i = 0; i < derivatives.length; i++) {
        derivatives[i] = F.subst(system.derivatives[i], frozen);
      }
      FirstOrderSystem stretch = system.restart(derivatives, from, state);
      double[][] end = new double[1][];
      DenseOutputModel model = propagateSmooth(head, stretch, to, method, engine, end);
      if (whole == null) {
        whole = model;
      } else {
        whole.append(model);
      }
      if (end[0] == null) {
        // the stepping gave up inside this stretch: the solution ends there
        break;
      }
      from = to;
      state = end[0];
    }
    return whole;
  }

  /**
   * The times strictly between <code>from</code> and <code>to</code> at which the right hand side
   * jumps: the zeros of the arguments of <code>HeavisideTheta</code>, <code>UnitStep</code> and
   * <code>Sign</code> which are linear in the independent variable and free of the unknown
   * functions. They are sorted in the direction of the integration.
   *
   * @param jumps receives the jumping terms
   */
  private static double[] breakPoints(FirstOrderSystem system, double from, double to,
      java.util.List<IExpr> jumps, EvalEngine engine) {
    java.util.TreeSet<Double> times = new java.util.TreeSet<>();
    for (IExpr derivative : system.derivatives) {
      collectJumps(derivative, system, from, to, jumps, times, engine);
    }
    double[] result = new double[times.size()];
    int i = 0;
    for (double t : (to >= from ? times : times.descendingSet())) {
      result[i++] = t;
    }
    return result;
  }

  private static void collectJumps(IExpr expr, FirstOrderSystem system, double from, double to,
      java.util.List<IExpr> jumps, java.util.Set<Double> times, EvalEngine engine) {
    if (!expr.isAST()) {
      return;
    }
    IAST ast = (IAST) expr;
    if ((ast.isAST(S.HeavisideTheta, 2) || ast.isAST(S.UnitStep, 2) || ast.isAST(S.Sign, 2))
        && !ast.arg1().isFree(system.timeVar)) {
      IExpr argument = ast.arg1();
      boolean stateFree = true;
      for (ISymbol function : system.functions) {
        stateFree &= argument.isFree(function);
      }
      if (stateFree && argument.isPolynomialOfMaxDegree(system.timeVar, 1)) {
        double a = engine.evalN(F.subst(argument, F.Rule(system.timeVar, F.C0))).evalfNaN();
        double b = engine.evalN(F.subst(argument, F.Rule(system.timeVar, F.C1))).evalfNaN() - a;
        double zero = -a / b;
        if (Double.isFinite(zero)) {
          if (!jumps.contains(ast)) {
            jumps.add(ast);
          }
          if (zero > Math.min(from, to) && zero < Math.max(from, to)) {
            times.add(zero);
          }
        }
        return;
      }
    }
    for (int i = 0; i < ast.size(); i++) {
      collectJumps(ast.get(i), system, from, to, jumps, times, engine);
    }
  }

  /**
   * @param end receives the state the integration ended in at index 0, or <code>null</code> there
   *        if the stepping gave up before <code>target</code>; may be <code>null</code>
   */
  private static DenseOutputModel propagateSmooth(ISymbol head, FirstOrderSystem system,
      double target, MethodSettings method, EvalEngine engine, double[][] end) {
    AbstractIntegrator integrator = method.createIntegrator();
    integrator.setMaxEvaluations(method.maxEvaluations);
    DenseOutputModel model = new DenseOutputModel();
    integrator.addStepHandler(model);
    LastStepRecorder recorder = new LastStepRecorder();
    integrator.addStepHandler(recorder);
    method.addEventDetectors(integrator, system, target >= system.t0, engine);
    ODEStateAndDerivative last;
    try {
      // clone, so that the two directions both start from the initial conditions
      last = integrator.integrate(new FirstODE(engine, system),
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
      return model;
    }
    if (end != null && recorder.lastState != null
        && Math.abs(last.getTime() - target) <= 1.0e-9 * Math.max(1.0, Math.abs(target))) {
      // an event which stopped the integration before the target ends the solution there
      end[0] = recorder.lastState.getPrimaryState().clone();
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

    /**
     * <code>MaxStepSize</code>, or {@link Double#NaN} for the default, a tenth of the requested
     * range.
     */
    double maxStepSize = Double.NaN;

    /**
     * <code>StartingStepSize</code>, or {@link Double#NaN} to let the integrator choose. For a fixed
     * step method it is the step.
     */
    double startingStepSize = Double.NaN;

    /** The most right hand side evaluations, from <code>MaxSteps</code>. */
    int maxEvaluations = MAX_EVALUATIONS;

    /** <code>true</code> if the steps have a fixed size, the <code>"FixedStep"</code> controller. */
    boolean fixedStep = false;

    /** The <code>"DifferenceOrder"</code> of a Runge-Kutta method, or 0 for the default. */
    int differenceOrder = 0;

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

    /** The <code>WhenEvent(cond, action)</code> entries of the equations. */
    java.util.List<IAST> whenEvents = java.util.Collections.emptyList();

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
        options = options.clone();
        for (int i = 4; i < ast.size(); i++) {
          // an option given before one of the step options, which the generic option reading
          // stops at
          IExpr entry = ast.get(i);
          if (entry.isRuleAST()) {
            if (entry.first() == S.Method) {
              options[METHOD] = entry.second();
            } else if (entry.first() == S.AccuracyGoal) {
              options[ACCURACY_GOAL] = entry.second();
            } else if (entry.first() == S.PrecisionGoal) {
              options[PRECISION_GOAL] = entry.second();
            }
          }
        }
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
      settings.parseStepOptions(ast);
      if (!Double.isNaN(settings.maxStepSize)) {
        settings.maxStep = settings.maxStepSize;
      } else if (range > 0.0) {
        settings.maxStep = Math.min(settings.maxStep, range / 10.0);
      }
      return settings;
    }

    /**
     * Read the options <code>StartingStepSize</code>, <code>MaxStepSize</code> and
     * <code>MaxSteps</code> from the trailing rules of the call. They are recognized by name, so
     * that they are read however the symbols were entered.
     */
    private void parseStepOptions(IAST ast) {
      for (int i = 4; i < ast.size(); i++) {
        IExpr entry = ast.get(i);
        if (!entry.isRuleAST() || !entry.first().isSymbol()) {
          continue;
        }
        String name = ((ISymbol) entry.first()).getSymbolName();
        IExpr value = entry.second();
        if ("StartingStepSize".equalsIgnoreCase(name)) {
          double step = value.evalfNaN();
          if (step > 0.0 && Double.isFinite(step)) {
            startingStepSize = step;
          }
        } else if ("MaxStepSize".equalsIgnoreCase(name)) {
          double step = value.evalfNaN();
          if (step > 0.0 && !Double.isNaN(step)) {
            maxStepSize = Double.isInfinite(step) ? Double.MAX_VALUE : step;
          }
        } else if ("MaxSteps".equalsIgnoreCase(name)) {
          if (value.isInfinity()) {
            maxEvaluations = Integer.MAX_VALUE;
          } else {
            int steps = value.toIntDefault();
            if (steps > 0) {
              // a step of the eighth order method takes a dozen evaluations, and some are rejected
              maxEvaluations = steps > Integer.MAX_VALUE / 16 ? Integer.MAX_VALUE : 16 * steps;
            }
          }
        }
      }
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
          if ("FixedStep".equals(entry.toString())) {
            // a controller: the method of its "Method" sub-option takes the fixed steps
            fixedStep = true;
          } else if (!"EventLocator".equals(entry.toString())
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
          case "DifferenceOrder":
            differenceOrder = rhs.isAutomatic() ? 0 : rhs.toIntDefault();
            if (differenceOrder < 0) {
              Errors.printMessage(ast.topHead(), "optx", F.List(entry, ast), engine);
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
          timeIntegration = name;
          return true;
        case "FixedStep":
          fixedStep = true;
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
      if (fixedStep) {
        double step = Double.isNaN(startingStepSize) ? maxStep / 100.0 : startingStepSize;
        // the Runge-Kutta formula of the requested order; the classical one of order four is the
        // default, and stands in for the orders which have no formula of their own
        switch (differenceOrder) {
          case 1:
            return new EulerIntegrator(step);
          case 2:
            return new MidpointIntegrator(step);
          case 6:
          case 7:
          case 8:
            return new LutherIntegrator(step);
          default:
            return new ClassicalRungeKuttaIntegrator(step);
        }
      }
      AdaptiveStepsizeIntegrator integrator;
      switch (timeIntegration) {
        case "Adams":
          integrator = new AdamsMoultonIntegrator(4, MIN_STEP, maxStep, absoluteTolerance,
              relativeTolerance);
          break;
        case "Extrapolation":
          integrator = new GraggBulirschStoerIntegrator(MIN_STEP, maxStep, absoluteTolerance,
              relativeTolerance);
          break;
        default:
          integrator = differenceOrder > 0 && differenceOrder <= 5
              ? new DormandPrince54Integrator(MIN_STEP, maxStep, absoluteTolerance,
                  relativeTolerance)
              : new DormandPrince853Integrator(MIN_STEP, maxStep, absoluteTolerance,
                  relativeTolerance);
      }
      if (!Double.isNaN(startingStepSize)) {
        integrator.setInitialStepSize(Math.min(startingStepSize, maxStep));
      }
      return integrator;
    }

    /**
     * Register one detector per <code>"Event"</code> expression, and the detectors of the
     * <code>WhenEvent</code> entries.
     *
     * @param forward <code>true</code> if the integration runs towards larger times
     */
    void addEventDetectors(AbstractIntegrator integrator, FirstOrderSystem system,
        boolean forward, EvalEngine engine) {
      for (IAST whenEvent : whenEvents) {
        // the condition is evaluated, which expands a form like Or @@ (x(t) > # &) /@ {1, 2};
        // the action stays held until an event
        IExpr condition = engine.evaluate(whenEvent.arg1());
        java.util.List<WhenEventDetector> detectors = new java.util.ArrayList<WhenEventDetector>();
        WhenEventDetector.collect(condition, F.NIL, whenEvent.arg2(), system, engine, forward,
            this, detectors);
        for (WhenEventDetector detector : detectors) {
          integrator.addEventDetector(detector);
        }
      }
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

    /**
     * The values of the event function closer to zero than this count as zero where the
     * integration starts: relative to the size of the initial state, the precision a value
     * computed from it has.
     */
    private final double fZeroThreshold;

    /**
     * The smallest distance from the start at which the event function was seen away from zero.
     * An event closer to the start than that is the function leaving the zero it started at,
     * rather than reaching one, and does not count.
     */
    private double fAwayFromZero = Double.POSITIVE_INFINITY;

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
      double scale = 1.0;
      for (double value : system.initialState) {
        scale = Math.max(scale, Math.abs(value));
      }
      this.fZeroThreshold = START_ZERO_TOLERANCE * scale;
    }

    @Override
    public double g(ODEStateAndDerivative state) {
      double value = substituted(fEvent, state).evalfNaN();
      if (Math.abs(value) > fZeroThreshold) {
        fAwayFromZero = Math.min(fAwayFromZero, Math.abs(state.getTime() - fSystem.t0));
      }
      return value;
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
        if (!(fAwayFromZero < Math.abs(state.getTime() - fSystem.t0))) {
          // the function started at zero and has not moved away from it yet
          return Action.CONTINUE;
        }
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
   * One detector of a <code>WhenEvent(cond, action)</code> entry of the equations.
   *
   * <p>
   * The condition is turned into a continuous function which crosses zero where the event is:
   * <code>f - g</code> for an equation <code>f == g</code>, which triggers in either direction, and
   * for an inequality <code>f &gt; g</code> the same difference, which triggers only when it
   * becomes positive, that is when the inequality becomes true. <code>Or</code> and
   * <code>And</code> of inequalities are the maximum and the minimum of the differences. An
   * <code>Or</code> of anything else is one detector per alternative, an <code>And</code> with
   * equations one detector per equation which acts only if the rest of the condition holds there.
   *
   * <p>
   * The action may be the string <code>"StopIntegration"</code>, <code>"RemoveEvent"</code>, rules
   * <code>y'(t) -&gt; -0.9*y'(t)</code> which restart the integration from the event with a new
   * state, or any other expression - <code>Sow(t)</code>, <code>tm = t</code> - which is evaluated
   * with the time and the state at the event substituted. <code>CompoundExpression</code> and lists
   * combine them.
   */
  private static final class WhenEventDetector implements ODEEventDetector {
    /**
     * Two events of one detector closer than this, relative to the time, are one: after a reset
     * the state starts on the far side of the zero it was found at, and the function can cross
     * back over it at once.
     */
    private static final double SAME_EVENT = 1.0e-10;

    private final FirstOrderSystem fSystem;
    private final EvalEngine fEngine;
    /** The function whose zero is the event. */
    private final IExpr fFunction;
    /** <code>true</code> if the event is only the zero where the function becomes positive. */
    private final boolean fBecomesTrue;
    /** The part of the condition which has to hold at the event, or {@link F#NIL}. */
    private final IExpr fGuard;
    private final IExpr fAction;
    private final boolean fForward;
    private final MethodSettings fSettings;
    private final BracketedUnivariateSolver<UnivariateFunction> fSolver;

    /** The time of the last event this detector acted on. */
    private double fLastEvent = Double.NaN;
    /** Set by the action <code>"RemoveEvent"</code>. */
    private boolean fRemoved = false;
    /** The state the integration restarts from after a reset. */
    private double[] fResetState = null;

    private WhenEventDetector(FirstOrderSystem system, EvalEngine engine, IExpr function,
        boolean becomesTrue, IExpr guard, IExpr action, boolean forward,
        MethodSettings settings) {
      this.fSystem = system;
      this.fEngine = engine;
      this.fFunction = function;
      this.fBecomesTrue = becomesTrue;
      this.fGuard = guard;
      this.fAction = action;
      this.fForward = forward;
      this.fSettings = settings;
      this.fSolver = settings.createSolver();
    }

    /**
     * Create the detectors of the condition <code>condition</code>.
     *
     * @param guard what else has to hold at the event, or {@link F#NIL}
     * @param detectors receives the detectors
     */
    static void collect(IExpr condition, IExpr guard, IExpr action, FirstOrderSystem system,
        EvalEngine engine, boolean forward, MethodSettings settings,
        java.util.List<WhenEventDetector> detectors) {
      if (condition.isEqual() && condition.argSize() == 2) {
        detectors.add(new WhenEventDetector(system, engine,
            F.Subtract(condition.first(), condition.second()), false, guard, action, forward,
            settings));
        return;
      }
      IExpr function = inequalityFunction(condition);
      if (function.isPresent()) {
        detectors.add(new WhenEventDetector(system, engine, function, true, guard, action, forward,
            settings));
        return;
      }
      if (condition.isOr()) {
        for (IExpr alternative : (IAST) condition) {
          collect(alternative, guard, action, system, engine, forward, settings, detectors);
        }
        return;
      }
      if (condition.isAnd() && ((IAST) condition).exists(IExpr::isEqual)) {
        IAST and = (IAST) condition;
        for (int i = 1; i < and.size(); i++) {
          if (and.get(i).isEqual()) {
            IAST rest = and.removeAtCopy(i);
            IExpr restGuard = guard.isPresent() ? F.And(guard, rest) : rest;
            collect(and.get(i), restGuard, action, system, engine, forward, settings, detectors);
          }
        }
        return;
      }
      // any other condition: the event is where its truth value turns from false to true
      detectors.add(new WhenEventDetector(system, engine, F.If(condition, F.C1, F.CN1), true,
          guard, action, forward, settings));
    }

    /**
     * A function which is positive exactly where the inequalities <code>condition</code> combines
     * hold, or {@link F#NIL} if it isn't made of inequalities.
     */
    private static IExpr inequalityFunction(IExpr condition) {
      if (condition.isAST() && condition.argSize() >= 2
          && (condition.isAST(S.Greater) || condition.isAST(S.GreaterEqual)
              || condition.isAST(S.Less) || condition.isAST(S.LessEqual))) {
        // a > b > c holds where both a - b and b - c are positive
        IAST inequality = (IAST) condition;
        boolean greater = condition.isAST(S.Greater) || condition.isAST(S.GreaterEqual);
        IASTAppendable min = F.ast(S.Min, inequality.argSize() - 1);
        for (int i = 1; i < inequality.argSize(); i++) {
          IExpr a = inequality.get(i);
          IExpr b = inequality.get(i + 1);
          min.append(greater ? F.Subtract(a, b) : F.Subtract(b, a));
        }
        return min.argSize() == 1 ? min.arg1() : min;
      }
      if (condition.isNot()) {
        IExpr function = inequalityFunction(condition.first());
        return function.isPresent() ? F.Negate(function) : F.NIL;
      }
      if (condition.isOr() || condition.isAnd()) {
        IAST ast = (IAST) condition;
        IASTAppendable result = F.ast(condition.isOr() ? S.Max : S.Min, ast.argSize());
        for (IExpr arg : ast) {
          IExpr function = inequalityFunction(arg);
          if (function.isNIL()) {
            return F.NIL;
          }
          result.append(function);
        }
        return result;
      }
      return F.NIL;
    }

    private IAST substitutions(ODEStateAndDerivative state) {
      return fSystem.substitutions(state.getTime(), state.getPrimaryState(),
          state.getPrimaryDerivative());
    }

    @Override
    public double g(ODEStateAndDerivative state) {
      return fEngine.evalN(F.subst(fFunction, substitutions(state))).evalfNaN();
    }

    @Override
    public AdaptableInterval getMaxCheckInterval() {
      return AdaptableInterval.of(fSettings.maxStep);
    }

    @Override
    public int getMaxIterationCount() {
      return fSettings.eventMaxIterations;
    }

    @Override
    public BracketedUnivariateSolver<UnivariateFunction> getSolver() {
      return fSolver;
    }

    @Override
    public ODEEventHandler getHandler() {
      return new ODEEventHandler() {
        @Override
        public Action eventOccurred(ODEStateAndDerivative state, ODEEventDetector detector,
            boolean increasing) {
          return act(state, increasing);
        }

        @Override
        public ODEState resetState(ODEEventDetector detector, ODEStateAndDerivative oldState) {
          return new ODEState(oldState.getTime(), fResetState);
        }
      };
    }

    /**
     * Run the action at an event.
     *
     * @param increasing <code>true</code> if the function increases at the event
     */
    private Action act(ODEStateAndDerivative state, boolean increasing) {
      final double t = state.getTime();
      if (fRemoved || (fBecomesTrue && increasing != fForward)
          || Math.abs(t - fLastEvent) <= SAME_EVENT * Math.max(1.0, Math.abs(t))) {
        return Action.CONTINUE;
      }
      IAST rules = substitutions(state);
      if (fGuard.isPresent() && !fEngine.evaluate(F.subst(fGuard, rules)).isTrue()) {
        return Action.CONTINUE;
      }
      fLastEvent = t;
      double[] newState = state.getPrimaryState().clone();
      boolean[] stop = new boolean[1];
      boolean reset = run(fAction, rules, state.getPrimaryState(), newState, stop);
      if (stop[0]) {
        return Action.STOP;
      }
      if (reset) {
        fResetState = newState;
        return Action.RESET_STATE;
      }
      return Action.CONTINUE;
    }

    /**
     * Run one part of an action.
     *
     * @param rules the time and the state at the event
     * @param state the state at the event, which the new values of a reset are computed from
     * @param newState receives the new values of a reset
     * @param stop receives <code>true</code> at index 0 if the integration should stop
     * @return <code>true</code> if <code>newState</code> was changed
     */
    private boolean run(IExpr action, IAST rules, double[] state, double[] newState,
        boolean[] stop) {
      if (action.isList() || action.isAST(S.CompoundExpression)) {
        boolean reset = false;
        for (IExpr part : (IAST) action) {
          reset |= run(part, rules, state, newState, stop);
        }
        return reset;
      }
      if (action.isString()) {
        String name = action.toString();
        if (STOP_INTEGRATION.equals(name)) {
          stop[0] = true;
        } else if ("RemoveEvent".equals(name)) {
          fRemoved = true;
        }
        return false;
      }
      if (action.isRuleAST()) {
        IExpr lhs = action.first();
        IExpr rhs = action.second();
        if (lhs.isList() && rhs.isList() && lhs.argSize() == rhs.argSize()) {
          boolean reset = false;
          for (int i = 1; i < lhs.size(); i++) {
            reset |= run(F.Rule(((IAST) lhs).get(i), ((IAST) rhs).get(i)), rules, state, newState,
                stop);
          }
          return reset;
        }
        int component = fSystem.componentIndex(lhs);
        if (component >= 0) {
          double value = fEngine.evalN(F.subst(rhs, rules)).evalfNaN();
          if (!Double.isNaN(value)) {
            newState[component] = value;
            return true;
          }
        }
        return false;
      }
      try {
        fEngine.evaluate(F.subst(action, rules));
      } catch (ThrowException tex) {
        if (F.$str(STOP_INTEGRATION).equals(tex.getTag())) {
          stop[0] = true;
        } else {
          throw tex;
        }
      }
      return false;
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
   * The largest number of equations, linear in the highest derivatives with a constant matrix,
   * which are still solved symbolically. The solution of a larger system grows with the square of
   * its size, so it is solved numerically with the factored matrix instead.
   */
  private static final int MAX_SOLVED_MASS_MATRIX = 4;

  /**
   * The constant coefficients <code>M(j,k)</code> of the highest derivative <code>k</code> in
   * equation <code>j</code>, if every equation is linear in the highest derivatives.
   *
   * @param reduced the equations, as expressions which should be zero, with the highest derivatives
   *        replaced by the symbols <code>highest</code>
   * @return <code>null</code> if an equation isn't linear in them, or a coefficient isn't a
   *         constant
   */
  private static IExpr[][] linearCoefficients(IExpr[] reduced, ISymbol[] highest,
      EvalEngine engine) {
    final int n = highest.length;
    IExpr[][] coefficients = new IExpr[n][n];
    for (int j = 0; j < n; j++) {
      for (int k = 0; k < n; k++) {
        if (reduced[j].isFree(highest[k])) {
          coefficients[j][k] = F.C0;
          continue;
        }
        IExpr coefficient = engine.evaluate(F.D(reduced[j], highest[k]));
        if (!coefficient.isNumericFunction(true)) {
          return null;
        }
        coefficients[j][k] = coefficient;
      }
    }
    return coefficients;
  }

  /**
   * The highest derivative each equation names, if every equation names exactly one and no two
   * the same one.
   *
   * @return the column of the non-zero coefficient of every row, or <code>null</code>
   */
  private static int[] pivots(IExpr[][] coefficients) {
    final int n = coefficients.length;
    int[] pivots = new int[n];
    boolean[] used = new boolean[n];
    for (int j = 0; j < n; j++) {
      pivots[j] = -1;
      for (int k = 0; k < n; k++) {
        if (!coefficients[j][k].isZero()) {
          if (pivots[j] >= 0 || used[k]) {
            return null;
          }
          pivots[j] = k;
        }
      }
      if (pivots[j] < 0) {
        return null;
      }
      used[pivots[j]] = true;
    }
    return pivots;
  }

  /** The rules which replace every one of <code>symbols</code> by zero. */
  private static IAST zeroRules(ISymbol[] symbols) {
    return F.mapRange(0, symbols.length, i -> F.Rule(symbols[i], F.C0));
  }

  /**
   * The LU decomposition of the matrix of numbers <code>coefficients</code>.
   *
   * @return <code>null</code> if it is singular, or not a matrix of real numbers
   */
  private static org.hipparchus.linear.DecompositionSolver factorMassMatrix(
      IExpr[][] coefficients) {
    final int n = coefficients.length;
    double[][] matrix = new double[n][n];
    for (int j = 0; j < n; j++) {
      for (int k = 0; k < n; k++) {
        matrix[j][k] = coefficients[j][k].evalfNaN();
        if (!Double.isFinite(matrix[j][k])) {
          return null;
        }
      }
    }
    org.hipparchus.linear.DecompositionSolver solver = new org.hipparchus.linear.LUDecomposition(
        new org.hipparchus.linear.Array2DRowRealMatrix(matrix, false)).getSolver();
    return solver.isNonSingular() ? solver : null;
  }

  /**
   * A curve which <code>n-1</code> algebraic equations <code>g(x)==0</code> leave in the space of
   * the <code>n</code> unknown functions <code>x</code>, together with one differential equation
   * <code>F(x, x')==0</code> which says how fast it is followed.
   *
   * <p>
   * The derivative is a multiple of the tangent <code>T(x)</code> of the curve, the generalized
   * cross product of the gradients of the algebraic equations, whose component <code>i</code> is
   * the determinant of the Jacobian of <code>g</code> with the unit row <code>e_i</code> appended.
   * Unlike a null space vector picked by a decomposition it is a smooth function of the point, so
   * that following <code>s*T(x)</code> with a fixed sign <code>s</code> never turns back. The sign
   * is the one of the derivatives the curve is started with, and the multiple is the root of the
   * differential equation along the tangent closest to the one at the last evaluation.
   */
  private static final class ImplicitCurve {
    /** The independent variable. */
    private final ISymbol fTimeVar;
    /** The symbols which stand for the values of the functions. */
    private final ISymbol[] fValues;
    /** The symbols which stand for the derivatives of the functions. */
    private final ISymbol[] fDerivatives;
    /** The algebraic equations, as expressions in {@link #fValues} which should be zero. */
    private final IExpr[] fConstraints;
    /** The partial derivatives of the constraints by the values. */
    private final IExpr[][] fGradients;
    /** The differential equation, as an expression which should be zero. */
    private final IExpr fSpeedEquation;

    /** The direction the tangent is followed in, <code>1</code> or <code>-1</code>. */
    private double fSign = 1.0;
    /** The multiple of the tangent at the start, where each integration begins its search. */
    double initialSpeed = 0.0;

    private ImplicitCurve(ISymbol timeVar, ISymbol[] values, ISymbol[] derivatives,
        IExpr[] constraints, IExpr[][] gradients, IExpr speedEquation) {
      this.fTimeVar = timeVar;
      this.fValues = values;
      this.fDerivatives = derivatives;
      this.fConstraints = constraints;
      this.fGradients = gradients;
      this.fSpeedEquation = speedEquation;
    }

    /**
     * @param reduced the first order equations, with the derivatives replaced by the symbols
     *        <code>highest</code>; all but one are algebraic
     * @return <code>null</code> if the algebraic equations depend on the independent variable
     */
    static ImplicitCurve create(IExpr[] reduced, ISymbol[] functions, ISymbol[] highest,
        ISymbol timeVar, EvalEngine engine) {
      final int n = functions.length;
      ISymbol[] values = new ISymbol[n];
      IASTAppendable valueRules = F.ListAlloc(n);
      for (int j = 0; j < n; j++) {
        values[j] = F.Dummy("NDSolve$" + functions[j].getSymbolName() + "$0");
        valueRules.append(F.Rule(F.unaryAST1(functions[j], timeVar), values[j]));
      }
      IExpr[] constraints = new IExpr[n - 1];
      IExpr speedEquation = F.NIL;
      int i = 0;
      for (IExpr equation : reduced) {
        IExpr expr = F.subst(equation, valueRules);
        if (isExplicit(expr, highest)) {
          if (!expr.isFree(timeVar)) {
            // a moving curve: its tangent alone doesn't give the derivative
            return null;
          }
          constraints[i++] = expr;
        } else {
          speedEquation = expr;
        }
      }
      for (IExpr expr : constraints) {
        for (ISymbol function : functions) {
          if (!expr.isFree(function)) {
            // a function which isn't evaluated at the independent variable
            return null;
          }
        }
      }
      IExpr[][] gradients = new IExpr[n - 1][n];
      for (int k = 0; k < n - 1; k++) {
        for (int j = 0; j < n; j++) {
          gradients[k][j] = engine.evaluate(F.D(constraints[k], values[j]));
        }
      }
      return new ImplicitCurve(timeVar, values, highest, constraints, gradients, speedEquation);
    }

    /**
     * Prepare the curve to be followed from <code>state</code>, which is moved onto it if it is
     * slightly off.
     *
     * @param derivatives the derivatives the curve starts with, {@link Double#NaN} where they are
     *        not given
     * @return <code>false</code> if the curve has no tangent at the start, or the differential
     *         equation no root along it
     */
    boolean start(double t, double[] state, double[] derivatives, EvalEngine engine) {
      if (!project(state, engine)) {
        return false;
      }
      double[] tangent = tangent(t, state, engine);
      double along = 0.0;
      double norm = 0.0;
      double givenNorm = 0.0;
      for (int j = 0; j < tangent.length; j++) {
        norm += tangent[j] * tangent[j];
        if (!Double.isNaN(derivatives[j])) {
          along += derivatives[j] * tangent[j];
          givenNorm += tangent[j] * tangent[j];
        }
      }
      if (!(norm > 0.0)) {
        return false;
      }
      fSign = along < 0.0 ? -1.0 : 1.0;
      double guess = (along != 0.0 && givenNorm > 0.0) ? Math.abs(along) / givenNorm
          : 1.0 / Math.sqrt(norm);
      try {
        initialSpeed = speed(t, state, tangent, guess, engine);
      } catch (MathRuntimeException mre) {
        return false;
      }
      return true;
    }

    /**
     * The derivative of the state at <code>t</code>.
     *
     * @param speed the multiple of the tangent at the last evaluation, where the search starts;
     *        receives the new one
     */
    double[] derivatives(double t, double[] state, double[] speed, EvalEngine engine) {
      double[] tangent = tangent(t, state, engine);
      speed[0] = speed(t, state, tangent, speed[0], engine);
      double[] result = new double[tangent.length];
      for (int j = 0; j < result.length; j++) {
        result[j] = speed[0] * fSign * tangent[j];
      }
      return result;
    }

    private IASTAppendable valueRules(double t, double[] state) {
      IASTAppendable rules = F.ListAlloc(2 * state.length + 1);
      for (int j = 0; j < state.length; j++) {
        rules.append(F.Rule(fValues[j], F.num(state[j])));
      }
      rules.append(F.Rule(fTimeVar, F.num(t)));
      return rules;
    }

    private double[][] jacobian(IAST rules, EvalEngine engine) {
      final int n = fValues.length;
      double[][] jacobian = new double[n - 1][n];
      for (int k = 0; k < n - 1; k++) {
        for (int j = 0; j < n; j++) {
          jacobian[k][j] = engine.evalN(F.subst(fGradients[k][j], rules)).evalfNaN();
        }
      }
      return jacobian;
    }

    /** The generalized cross product of the gradients of the constraints. */
    private double[] tangent(double t, double[] state, EvalEngine engine) {
      final int n = fValues.length;
      double[][] jacobian = jacobian(valueRules(t, state), engine);
      double[] tangent = new double[n];
      double[][] square = new double[n][];
      for (int k = 0; k < n - 1; k++) {
        square[k] = jacobian[k];
      }
      for (int i = 0; i < n; i++) {
        double[] unit = new double[n];
        unit[i] = 1.0;
        square[n - 1] = unit;
        tangent[i] = new org.hipparchus.linear.LUDecomposition(
            new org.hipparchus.linear.Array2DRowRealMatrix(square, true)).getDeterminant();
      }
      return tangent;
    }

    /**
     * The root <code>c</code> of the differential equation with the derivatives
     * <code>c*sign*tangent</code> closest to <code>guess</code>, by Newton's method.
     */
    private double speed(double t, double[] state, double[] tangent, double guess,
        EvalEngine engine) {
      double c = guess;
      double residual = speedResidual(t, state, tangent, c, engine);
      for (int iteration = 0; iteration < 50; iteration++) {
        if (residual == 0.0) {
          return c;
        }
        final double h = 1.0e-7 * Math.max(1.0, Math.abs(c));
        final double slope = (speedResidual(t, state, tangent, c + h, engine) - residual) / h;
        final double step = residual / slope;
        if (!Double.isFinite(step)) {
          break;
        }
        c -= step;
        residual = speedResidual(t, state, tangent, c, engine);
        if (Math.abs(step) <= 1.0e-14 * Math.max(1.0, Math.abs(c))) {
          return c;
        }
      }
      throw new org.hipparchus.exception.MathIllegalStateException(
          org.hipparchus.exception.LocalizedCoreFormats.CONVERGENCE_FAILED);
    }

    private double speedResidual(double t, double[] state, double[] tangent, double c,
        EvalEngine engine) {
      IASTAppendable rules = valueRules(t, state);
      for (int j = 0; j < tangent.length; j++) {
        rules.append(F.Rule(fDerivatives[j], F.num(c * fSign * tangent[j])));
      }
      return engine.evalN(F.subst(fSpeedEquation, rules)).evalfNaN();
    }

    /**
     * Move <code>state</code> onto the curve by Gauss-Newton steps, the shortest steps which make
     * the linearized constraints hold.
     *
     * @return <code>false</code> if it could not be moved onto it
     */
    private boolean project(double[] state, EvalEngine engine) {
      final int m = fConstraints.length;
      double scale = 1.0;
      for (double value : state) {
        scale = Math.max(scale, Math.abs(value));
      }
      for (int iteration = 0; iteration < 20; iteration++) {
        IAST rules = valueRules(0.0, state);
        double[] residual = new double[m];
        double norm = 0.0;
        for (int k = 0; k < m; k++) {
          residual[k] = engine.evalN(F.subst(fConstraints[k], rules)).evalfNaN();
          norm = Math.max(norm, Math.abs(residual[k]));
        }
        if (!Double.isFinite(norm)) {
          return false;
        }
        if (norm <= 1.0e-14 * scale) {
          return true;
        }
        org.hipparchus.linear.RealMatrix jacobian =
            new org.hipparchus.linear.Array2DRowRealMatrix(jacobian(rules, engine), false);
        try {
          double[] multipliers = new org.hipparchus.linear.LUDecomposition(
              jacobian.multiplyTransposed(jacobian)).getSolver()
                  .solve(new org.hipparchus.linear.ArrayRealVector(residual, false)).toArray();
          double[] step = jacobian.transpose().operate(multipliers);
          for (int j = 0; j < state.length; j++) {
            state[j] -= step[j];
          }
        } catch (MathRuntimeException mre) {
          return false;
        }
      }
      return false;
    }
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
   * Conditions which refer to more than one value, <code>PD(0) == 1 - PB(0)</code>, are solved
   * together with the others as one algebraic system for all the values they mention, and are
   * replaced by one condition per value.
   *
   * @param conditions the conditions, as expressions which should be zero
   * @param functions the dependent function symbols
   * @return <code>conditions</code> itself if every condition names a single value or the system
   *         has no numeric solution
   */
  private static IASTAppendable decoupleConditions(IASTAppendable conditions, ISymbol[] functions,
      EvalEngine engine) {
    java.util.Set<IExpr> applications = new java.util.LinkedHashSet<IExpr>();
    boolean coupled = false;
    for (IExpr condition : conditions) {
      java.util.Set<IExpr> found = new java.util.LinkedHashSet<IExpr>();
      collectApplications(condition, functions, found);
      coupled |= found.size() > 1;
      applications.addAll(found);
    }
    if (!coupled) {
      return conditions;
    }
    IASTAppendable unknowns = F.ListAlloc(applications.size());
    IASTAppendable toUnknowns = F.ListAlloc(applications.size());
    for (IExpr application : applications) {
      // distinct names: the solver tells the unknowns apart by them
      ISymbol unknown = F.Dummy("NDSolve$value" + unknowns.size());
      unknowns.append(unknown);
      toUnknowns.append(F.Rule(application, unknown));
    }
    IASTAppendable system = F.ListAlloc(conditions.argSize());
    for (IExpr condition : conditions) {
      system.append(F.Equal(F.subst(condition, toUnknowns), F.C0));
    }
    IAST rules = SolveUtils.firstSolutionRules(engine.evaluate(F.Solve(system, unknowns)));
    if (rules.isNIL()) {
      return conditions;
    }
    IASTAppendable result = F.ListAlloc(applications.size());
    int i = 1;
    for (IExpr application : applications) {
      IExpr value = engine.evalN(F.subst(unknowns.get(i++), rules));
      if (!value.isReal()) {
        // not determined by the conditions
        return conditions;
      }
      result.append(F.Subtract(application, value));
    }
    return result;
  }

  /** Collect the distinct applications of the dependent functions in <code>expr</code>. */
  private static void collectApplications(IExpr expr, ISymbol[] functions,
      java.util.Set<IExpr> applications) {
    if (!expr.isAST()) {
      return;
    }
    if (applicationIndex(expr, functions, new int[1]) >= 0) {
      applications.add(expr);
      return;
    }
    for (IExpr arg : (IAST) expr) {
      collectApplications(arg, functions, applications);
    }
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
