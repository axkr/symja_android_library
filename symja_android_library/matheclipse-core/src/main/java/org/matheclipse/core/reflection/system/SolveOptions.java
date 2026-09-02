package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The option values of one call of a function of the {@code Solve} family.
 *
 * <p>
 * {@link org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator} hands the option
 * values to the evaluator as an array which is indexed like the option keys the evaluator declared
 * in its {@code setUp}. Reading that array by a literal index couples every reader to the
 * declaration order of one particular caller, and the callers of the {@code Solve} family don't
 * agree on it: index <code>2</code> is the {@link S#Modulus} of {@link S#Solve} but the
 * {@link S#WorkingPrecision} of {@link S#NSolve}, and {@link S#FindInstance} declares one option
 * only, so the array is shorter than the readers expect.
 *
 * <p>
 * This class pairs the values with the keys the caller declared and exposes them by name. An option
 * which a caller doesn't declare reads as its documented default, so a reader doesn't have to know
 * which caller it serves.
 */
public final class SolveOptions {

  /** The option keys of {@link S#Solve} and {@link S#SolveValues}. */
  static final IBuiltInSymbol[] SOLVE_KEYS = new IBuiltInSymbol[] {S.Assumptions,
      S.GenerateConditions, S.InverseFunctions, S.MaxExtraConditions, S.MaxRoots, S.Modulus};

  /**
   * The option defaults of {@link S#Solve} and {@link S#SolveValues}. {@link S#Assumptions} default
   * to {@link S#Automatic}, i.e. to the assumptions the engine already carries - the global
   * <code>$Assumptions</code> and the ones an enclosing <code>Assuming(...)</code> established.
   */
  static final IExpr[] SOLVE_DEFAULTS =
      new IExpr[] {S.Automatic, S.True, S.Automatic, F.C0, F.C1000, F.C0};

  /** The option keys of {@link S#NSolve} and {@link S#NSolveValues}. */
  static final IBuiltInSymbol[] NSOLVE_KEYS =
      new IBuiltInSymbol[] {S.GenerateConditions, S.MaxRoots, S.WorkingPrecision};

  /** The option defaults of {@link S#NSolve} and {@link S#NSolveValues}. */
  static final IExpr[] NSOLVE_DEFAULTS = new IExpr[] {S.False, F.C1000, S.Automatic};

  /** The option keys of {@link S#SolveAlways}. */
  static final IBuiltInSymbol[] SOLVE_ALWAYS_KEYS =
      new IBuiltInSymbol[] {S.GenerateConditions, S.MaxRoots};

  /** The option defaults of {@link S#SolveAlways}. */
  static final IExpr[] SOLVE_ALWAYS_DEFAULTS = new IExpr[] {S.True, F.C1000};

  /** The option keys of {@link S#FindInstance}. */
  static final IBuiltInSymbol[] FIND_INSTANCE_KEYS =
      new IBuiltInSymbol[] {S.GenerateConditions};

  /** The option defaults of {@link S#FindInstance}. */
  static final IExpr[] FIND_INSTANCE_DEFAULTS = new IExpr[] {S.False};

  /** The option keys of {@link S#Reduce}. */
  static final IBuiltInSymbol[] REDUCE_KEYS =
      new IBuiltInSymbol[] {S.Backsubstitution, S.Cubics, S.GeneratedParameters, S.Method,
          S.Modulus, S.Quartics, S.WorkingPrecision};

  /**
   * The option defaults of {@link S#Reduce}. {@link S#Cubics} and {@link S#Quartics} default to
   * {@link S#False}: the roots of a general cubic or quartic are given as inert {@link S#Root}
   * objects rather than as the large explicit radicals of the Cardano and Ferrari formulas.
   * {@link S#Backsubstitution} defaults to {@link S#False}: a solved variable may stay expressed
   * through another one where substituting its value would blow the result up.
   */
  static final IExpr[] REDUCE_DEFAULTS =
      new IExpr[] {S.False, S.False, S.C, S.Automatic, F.C0, S.False, S.Automatic};

  /** The option keys of {@link S#Resolve}. */
  static final IBuiltInSymbol[] RESOLVE_KEYS =
      new IBuiltInSymbol[] {S.Method, S.WorkingPrecision};

  /** The option defaults of {@link S#Resolve}. */
  static final IExpr[] RESOLVE_DEFAULTS = new IExpr[] {S.Automatic, S.Automatic};

  /**
   * The values which an option reads as if the calling function doesn't declare it: no modulus, no
   * limit on the number of roots beyond the general one, and no requested precision.
   */
  private static final IExpr UNDECLARED_GENERATE_CONDITIONS = S.True;
  private static final IExpr UNDECLARED_MAX_ROOTS = F.C1000;
  private static final IExpr UNDECLARED_MODULUS = F.C0;
  private static final IExpr UNDECLARED_WORKING_PRECISION = S.Automatic;
  private static final IExpr UNDECLARED_GENERATED_PARAMETERS = S.C;
  private static final IExpr UNDECLARED_METHOD = S.Automatic;
  /** the radical solvers of the {@code Solve} family are used unless a caller opts out */
  private static final IExpr UNDECLARED_CUBICS = S.True;
  private static final IExpr UNDECLARED_QUARTICS = S.True;
  private static final IExpr UNDECLARED_ASSUMPTIONS = S.Automatic;
  /** an internal caller wants a value it can use, so it gets the fully substituted form */
  private static final IExpr UNDECLARED_BACKSUBSTITUTION = S.True;
  /** an internal caller keeps the inverse functions, warning about the branches they may lose */
  private static final IExpr UNDECLARED_INVERSE_FUNCTIONS = S.Automatic;
  /** an internal caller wants the generic solutions, without conditions on the parameters */
  private static final IExpr UNDECLARED_MAX_EXTRA_CONDITIONS = F.C0;

  private static final SolveOptions DEFAULTS = new SolveOptions(SOLVE_KEYS, SOLVE_DEFAULTS);

  private final IExpr generateConditions;
  private final IExpr maxRoots;
  private final IExpr modulus;
  private final IExpr workingPrecision;
  private final IExpr generatedParameters;
  private final IExpr method;
  private final IExpr cubics;
  private final IExpr quartics;
  private final IExpr assumptions;
  private final IExpr backsubstitution;
  private final IExpr inverseFunctions;
  private final IExpr maxExtraConditions;

  private SolveOptions(IBuiltInSymbol[] keys, IExpr[] values) {
    this.generateConditions =
        value(keys, values, S.GenerateConditions, UNDECLARED_GENERATE_CONDITIONS);
    this.maxRoots = value(keys, values, S.MaxRoots, UNDECLARED_MAX_ROOTS);
    this.modulus = value(keys, values, S.Modulus, UNDECLARED_MODULUS);
    this.workingPrecision = value(keys, values, S.WorkingPrecision, UNDECLARED_WORKING_PRECISION);
    this.generatedParameters =
        value(keys, values, S.GeneratedParameters, UNDECLARED_GENERATED_PARAMETERS);
    this.method = value(keys, values, S.Method, UNDECLARED_METHOD);
    this.cubics = value(keys, values, S.Cubics, UNDECLARED_CUBICS);
    this.quartics = value(keys, values, S.Quartics, UNDECLARED_QUARTICS);
    this.assumptions = value(keys, values, S.Assumptions, UNDECLARED_ASSUMPTIONS);
    this.backsubstitution =
        value(keys, values, S.Backsubstitution, UNDECLARED_BACKSUBSTITUTION);
    this.inverseFunctions =
        value(keys, values, S.InverseFunctions, UNDECLARED_INVERSE_FUNCTIONS);
    this.maxExtraConditions =
        value(keys, values, S.MaxExtraConditions, UNDECLARED_MAX_EXTRA_CONDITIONS);
  }

  /**
   * Pair the option values of one call with the option keys which the called function declared.
   *
   * @param keys the option keys the function declared in its {@code setUp}, one of the
   *        <code>..._KEYS</code> constants of this class
   * @param values the option values of this call, indexed like <code>keys</code>
   */
  public static SolveOptions of(IBuiltInSymbol[] keys, IExpr[] values) {
    return new SolveOptions(keys, values);
  }

  /**
   * The option values of an internal call which doesn't come from a user written option, for
   * example the solving steps of {@link S#Eliminate}.
   */
  public static SolveOptions defaults() {
    return DEFAULTS;
  }

  private static IExpr value(IBuiltInSymbol[] keys, IExpr[] values, IBuiltInSymbol key,
      IExpr undeclared) {
    for (int i = 0; i < keys.length; i++) {
      if (keys[i] == key) {
        return i < values.length && values[i].isPresent() ? values[i] : undeclared;
      }
    }
    return undeclared;
  }

  /** The value of the {@link S#GenerateConditions} option. */
  public IExpr generateConditions() {
    return generateConditions;
  }

  /** Test if the {@link S#GenerateConditions} option is set to {@link S#True}. */
  public boolean isGenerateConditions() {
    return generateConditions.isTrue();
  }

  /** The value of the {@link S#MaxRoots} option. */
  public IExpr maxRoots() {
    return maxRoots;
  }

  /**
   * The value of the {@link S#Modulus} option, or <code>0</code> ("no modulus") for a function which
   * doesn't declare it.
   */
  public IExpr modulus() {
    return modulus;
  }

  /**
   * The value of the {@link S#WorkingPrecision} option, or {@link S#Automatic} for a function which
   * doesn't declare it.
   */
  public IExpr workingPrecision() {
    return workingPrecision;
  }

  /**
   * The head which names the parameters that a solution generates, {@link S#C} by default, so that
   * they are named <code>C(1)</code>, <code>C(2)</code>, ...
   */
  public IExpr generatedParameters() {
    return generatedParameters;
  }

  /**
   * The value of the {@link S#Method} option. Only {@link S#Automatic} is implemented - there is one
   * algorithm to select - so a different value is accepted and ignored rather than rejected.
   */
  public IExpr method() {
    return method;
  }

  /**
   * The value of the {@link S#Cubics} option: whether a general cubic is solved by the explicit
   * radicals of the Cardano formula rather than by inert {@link S#Root} objects.
   */
  public IExpr cubics() {
    return cubics;
  }

  /** The value of the {@link S#Quartics} option, likewise for the Ferrari formula. */
  public IExpr quartics() {
    return quartics;
  }

  /**
   * The value of the {@link S#Assumptions} option, or {@link S#Automatic} if the caller doesn't
   * declare it or didn't write one down. {@link S#Automatic} means that only the assumptions which
   * the engine already carries apply.
   */
  public IExpr assumptions() {
    return assumptions;
  }

  /**
   * Test if the {@link S#Backsubstitution} option asks for every solved variable to be given
   * explicitly, even where substituting the value of another variable blows the result up.
   */
  public boolean isBacksubstitution() {
    return backsubstitution.isTrue();
  }

  /**
   * The value of the {@link S#InverseFunctions} option as one of the
   * <code>EvalEngine.INVERSE_FUNCTIONS_*</code> modes: {@link S#False} never applies a symbolic
   * inverse function, {@link S#True} applies it silently and {@link S#Automatic} applies it and
   * warns about the branches a multivalued inverse may lose.
   */
  /**
   * The number of equations on the parameters of a system which a solution may require, i.e. the
   * value of the {@link S#MaxExtraConditions} option. {@link S#All} allows every solution;
   * <code>0</code> - the default - returns only the generic solutions.
   */
  public int maxExtraConditions() {
    if (maxExtraConditions == S.All || maxExtraConditions.isInfinity()) {
      return Integer.MAX_VALUE;
    }
    int limit = maxExtraConditions.toIntDefault();
    return limit < 0 ? 0 : limit;
  }

  public int inverseFunctionsMode() {
    if (inverseFunctions.isFalse()) {
      return EvalEngine.INVERSE_FUNCTIONS_FALSE;
    }
    if (inverseFunctions.isTrue()) {
      return EvalEngine.INVERSE_FUNCTIONS_TRUE;
    }
    return EvalEngine.INVERSE_FUNCTIONS_AUTOMATIC;
  }

  /**
   * The {@link S#Cubics} and {@link S#Quartics} option rules, to be appended to a call of
   * {@link S#Roots}.
   */
  public IExpr[] rootsOptions() {
    return new IExpr[] {F.Rule(S.Cubics, cubics), F.Rule(S.Quartics, quartics)};
  }
}
