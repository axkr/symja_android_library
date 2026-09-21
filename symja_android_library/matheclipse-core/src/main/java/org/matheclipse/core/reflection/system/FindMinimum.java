package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.RealVector;
import org.hipparchus.optim.InitialGuess;
import org.hipparchus.optim.MaxEval;
import org.hipparchus.optim.MaxIter;
import org.hipparchus.optim.OptimizationData;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.SimpleBounds;
import org.hipparchus.optim.SimpleValueChecker;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunction;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.hipparchus.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.hipparchus.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer.Formula;
import org.hipparchus.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.hipparchus.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.hipparchus.optim.nonlinear.scalar.noderiv.CMAESOptimizer.PopulationSize;
import org.hipparchus.optim.nonlinear.scalar.noderiv.CMAESOptimizer.Sigma;
import org.hipparchus.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.hipparchus.optim.nonlinear.vector.constrained.ConstraintOptimizer;
import org.hipparchus.optim.nonlinear.vector.constrained.LagrangeSolution;
import org.hipparchus.optim.nonlinear.vector.constrained.LinearEqualityConstraint;
import org.hipparchus.optim.nonlinear.vector.constrained.LinearInequalityConstraint;
import org.hipparchus.optim.nonlinear.vector.constrained.SQPOptimizerS2;
import org.hipparchus.random.RandomDataGenerator;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.MultiVariateNumerical;
import org.matheclipse.core.generic.MultiVariateVectorGradient;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.generic.TwiceDifferentiableMultiVariateNumerical;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <pre>
 * <code>FindMinimum(f, {x, xstart})
 * </code>
 * </pre>
 * 
 * <p>
 * searches for a local numerical minimum of <code>f</code> for the variable <code>x</code> and the
 * start value <code>xstart</code>.
 * </p>
 * 
 * <pre>
 * <code>FindMinimum(f, {x, xstart}, Method-&gt;methodName)
 * </code>
 * </pre>
 * 
 * <p>
 * searches for a local numerical minimum of <code>f</code> for the variable <code>x</code> and the
 * start value <code>xstart</code>, with one of the following method names:
 * </p>
 * 
 * <pre>
 * <code>FindMinimum(f, {{x, xstart},{y, ystart},...})
 * </code>
 * </pre>
 * 
 * <p>
 * searches for a local numerical minimum of the multivariate function <code>f</code> for the
 * variables <code>x, y,...</code> and the corresponding start values
 * <code>xstart, ystart,...</code>.
 * </p>
 *
 * <pre>
 * <code>FindMinimum(f, {x, xstart}, {y, ystart}, ...)
 * </code>
 * </pre>
 *
 * <p>
 * is the same search with one search specification per argument.
 * </p>
 *
 * <pre>
 * <code>FindMinimum({f, constraints}, {{x, xstart},{y, ystart},...})
 * </code>
 * </pre>
 *
 * <p>
 * searches for a local numerical minimum subject to the <code>constraints</code>. Bounds of a
 * single variable like <code>x&gt;=1</code> are taken by the methods &quot;CMAES&quot; and
 * &quot;BOBYQA&quot;; linear equations and inequalities like <code>x+y&gt;=4</code> select the
 * &quot;SequentialQuadratic&quot; method. Other constraints are not supported.
 * </p>
 * <p>
 * A search specification can be <code>x</code> or <code>{x}</code> (start value chosen
 * automatically), <code>{x, xstart}</code>, <code>{x, xstart, xstart2}</code> or
 * <code>{x, xstart, xmin, xmax}</code> (the search stays in <code>xmin&lt;=x&lt;=xmax</code>). The
 * variables are localized like in <code>Block</code>.
 * </p>
 * <p>
 * The option <code>MaxIterations</code> (default <code>100</code>) limits the iterations of a
 * method; <code>Automatic</code> and <code>Infinity</code> are possible values.
 * </p>
 * 
 * <p>
 * See
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Mathematical_optimization">Wikipedia - Mathematical
 * optimization</a></li>
 * <li><a href="https://en.wikipedia.org/wiki/Rosenbrock_function">Wikipedia - Rosenbrock
 * function</a></li>
 * </ul>
 * <h4>&quot;Powell&quot;</h4>
 * <p>
 * Implements the <a href=
 * "https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/PowellOptimizer.java">Powell</a>
 * optimizer.
 * </p>
 * <p>
 * This is the default method, if no <code>Method</code> is set.
 * </p>
 * <h4>&quot;ConjugateGradient&quot;</h4>
 * <p>
 * Implements the <a href=
 * "https://github.com/Hipparchus-Math/hipparchus/blob/main/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/gradient/NonLinearConjugateGradientOptimizer.java">Non-linear
 * conjugate gradient</a> optimizer.<br />
 * This is a derivative based method and the functions must be symbolically differentiable.
 * </p>
 * <h4>&quot;SequentialQuadratic&quot;</h4>
 * <p>
 * Implements the <a href=
 * "https://github.com/Hipparchus-Math/hipparchus/blob/main/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/vector/constrained/SQPOptimizerS2.java">Sequential
 * Quadratic Programming</a> optimizer.
 * </p>
 * <p>
 * This is a derivative, multivariate based method and the functions must be symbolically
 * differentiable.
 * </p>
 * <h4>&quot;BOBYQA&quot;</h4>
 * <p>
 * Implements <a href=
 * "https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/BOBYQAOptimizer.java">Powell's
 * BOBYQA</a> optimizer (Bound Optimization BY Quadratic Approximation).
 * </p>
 * <p>
 * The &quot;BOBYQA&quot; method falls back to &quot;CMAES&quot; if the objective function has
 * dimension 1.
 * </p>
 * <h4>&quot;CMAES&quot;</h4>
 * <p>
 * Implements the <a href=
 * "https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/CMAESOptimizer.java">Covariance
 * Matrix Adaptation Evolution Strategy (CMA-ES)</a> optimizer.
 * </p>
 * <h3>Examples</h3>
 * 
 * <pre>
 * <code>&gt;&gt; FindMinimum(Sin(x), {x, 0.5}) 
 * {-1.0,{x-&gt;-1.5708}}
 * 
 * &gt;&gt; FindMinimum(Sin(x)*Sin(2*y), {{x, 2}, {y, 2}}, Method -&gt; &quot;ConjugateGradient&quot;) 
 * {-1.0,{x-&gt;1.5708,y-&gt;2.35619}}        
 * </code>
 * </pre>
 */
public class FindMinimum extends AbstractFunctionOptionEvaluator {

  public static final String BOBYQA_METHOD = "BOBYQA";
  public static final String CMAES_METHOD = "CMAES";
  public static final String CONJUGATEGRADIENT_METHOD = "ConjugateGradient";
  public static final String POWELL_METHOD = "Powell";
  public static final String SEQUENTIAL_QUADRATIC_METHOD = "SequentialQuadratic";

  private static final String[] METHODS = {POWELL_METHOD, CONJUGATEGRADIENT_METHOD,
      SEQUENTIAL_QUADRATIC_METHOD, BOBYQA_METHOD, CMAES_METHOD};

  /** Start value of a variable which has none in its search specification. */
  private static final double DEFAULT_START_VALUE = 1.999999999999999;

  /** Lower limit of the ceiling for the number of function evaluations. */
  private static final int MIN_EVALUATIONS = 10000;

  private static final int EVALUATIONS_PER_ITERATION = 100;

  /** A search which reaches a function value of this size ran away on an unbounded function. */
  private static final double DIVERGED = 1e300;

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    if (argSize > 0 && argSize < ast.size()) {
      ast = ast.copyUntil(argSize + 1);
    }
    try {
      return findExtremum(ast, goalType(), engine, options);
    } catch (MathIllegalStateException mise) {
      if (mise.getSpecifier().equals(LocalizedCoreFormats.MAX_COUNT_EXCEEDED)) {
        Object[] parts = mise.getParts();
        if (parts != null && parts.length >= 1) {
          // Failed to converge to the requested accuracy or precision within `1` iterations.
          return Errors.printMessage(ast.topHead(), "cvmit", F.list(F.$str(parts[0].toString())),
              engine);
        }
      }
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(mise.getMessage())), engine);
    } catch (MathRuntimeException mre) {
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(mre.getMessage())), engine);
    }
  }

  /**
   * The direction of the search; {@link FindMaximum} overrides this method.
   */
  protected GoalType goalType() {
    return GoalType.MINIMIZE;
  }

  /**
   * One search specification <code>x</code>, <code>{x}</code>, <code>{x, x0}</code>,
   * <code>{x, x0, x1}</code> or <code>{x, x0, xmin, xmax}</code>.
   */
  private static final class VariableSpec {
    final IExpr variable;
    final double start;
    final double lower;
    final double upper;

    VariableSpec(IExpr variable, double start, double lower, double upper) {
      this.variable = variable;
      this.start = start;
      this.lower = lower;
      this.upper = upper;
    }
  }

  protected static IExpr findExtremum(IAST ast, GoalType goalType, EvalEngine engine,
      IExpr[] options) {
    final ISymbol head = goalType == GoalType.MINIMIZE ? S.FindMinimum : S.FindMaximum;
    IAST relationList = ast.arg1().makeList();
    if (relationList.argSize() == 0) {
      return F.NIL;
    }
    IExpr function = relationList.arg1();

    // The search specifications are the leading run of list arguments behind the function. Several
    // of them are the form FindMinimum(f, {x,x0}, {y,y0}), which is the same as the nested
    // FindMinimum(f, {{x,x0},{y,y0}}).
    int lastSpecPosition = 1;
    for (int i = 2; i < ast.size(); i++) {
      if (!ast.get(i).isList()) {
        break;
      }
      lastSpecPosition = i;
    }
    if (lastSpecPosition < 2) {
      // FindMinimum(f, x) or a symbol which holds the specifications
      lastSpecPosition = 2;
    }
    List<VariableSpec> specs = variableSpecs(ast, lastSpecPosition, head, engine);
    if (specs == null) {
      return F.NIL;
    }

    int maxIterations = FindRoot.optionMaxIterations(options[0], head, engine);
    if (maxIterations < 0) {
      return F.NIL;
    }
    String method = POWELL_METHOD;
    boolean automaticMethod = true;
    if (options[1] != S.Automatic) {
      if (options[1].isSymbol() || options[1].isString()) {
        // determine option S.Method
        method = options[1].toString();
        automaticMethod = false;
      }
    } else if (lastSpecPosition < ast.argSize() && ast.last().isSymbol()) {
      // FindMinimum(f, {x, x0}, methodName) - the bare method name behind the specifications
      method = ast.last().toString();
      automaticMethod = false;
    }
    final String methodName = method;
    method = canonicalMethod(methodName);
    if (method == null) {
      // `1`.
      return Errors.printMessage(head, "error", F.list(
          F.$str("Method " + methodName + " is not one of " + String.join(", ", METHODS))), engine);
    }

    // FindMinimum has attribute HoldAll and localizes its variables like Block does
    final int n = specs.size();
    ISymbol[] blockedSymbols = new ISymbol[n];
    IExpr[] blockedValues = new IExpr[n];
    boolean[] blockedDelayed = new boolean[n];
    for (int i = 0; i < n; i++) {
      IExpr variable = specs.get(i).variable;
      if (variable.isSymbol() && ((ISymbol) variable).hasAssignedSymbolValue()) {
        ISymbol symbol = (ISymbol) variable;
        blockedSymbols[i] = symbol;
        blockedValues[i] = symbol.assignedValue();
        blockedDelayed[i] = symbol.isEvalFlagOn(ISymbol.SETDELAYED_FLAG_ASSIGNED_VALUE);
        symbol.clearValue();
      }
    }
    try {
      return findExtremum(head, function, relationList, specs, goalType, maxIterations, method,
          automaticMethod, engine);
    } finally {
      for (int i = 0; i < n; i++) {
        if (blockedSymbols[i] != null) {
          blockedSymbols[i].assignValue(blockedValues[i], blockedDelayed[i]);
        }
      }
    }
  }

  private static IExpr findExtremum(ISymbol head, IExpr function, IAST relationList,
      List<VariableSpec> specs, GoalType goalType, int maxIterations, String method,
      boolean automaticMethod, EvalEngine engine) {
    final int n = specs.size();
    // the one and only order of the variables: the order of the search specifications
    IASTAppendable varsList = F.ListAlloc(n);
    double[] initialValues = new double[n];
    double[] lowerBounds = new double[n];
    double[] upperBounds = new double[n];
    boolean specBounds = false;
    for (int i = 0; i < n; i++) {
      VariableSpec spec = specs.get(i);
      varsList.append(spec.variable);
      initialValues[i] = spec.start;
      lowerBounds[i] = spec.lower;
      upperBounds[i] = spec.upper;
      specBounds |= !Double.isInfinite(spec.lower) || !Double.isInfinite(spec.upper);
    }

    SimpleBounds simpleBounds = specBounds ? new SimpleBounds(lowerBounds, upperBounds) : null;
    OptimizationData[] optimizationData = new OptimizationData[2];
    boolean constrained = false;
    if (relationList.argSize() > 1) {
      IASTAppendable constraints = F.ast(S.And, relationList.argSize());
      for (int i = 2; i < relationList.size(); i++) {
        IExpr relation = relationList.get(i);
        if (relation.isAnd()) {
          constraints.appendArgs((IAST) relation);
        } else {
          constraints.append(relation);
        }
      }
      constrained = constraints.argSize() > 0;
      if (constrained && !method.equals(SEQUENTIAL_QUADRATIC_METHOD)) {
        IASTAppendable remaining = constraints.copyAppendable();
        SimpleBounds relationBounds = createSimpleBounds(remaining, varsList, engine);
        if (relationBounds != null && remaining.argSize() == 0) {
          simpleBounds = intersect(simpleBounds, relationBounds);
          if (method.equals(POWELL_METHOD)) {
            // Powell is unbounded. Hipparchus PR #455 lets SQPOptimizerS2 take SimpleBounds.
            method = CMAES_METHOD;
          }
        } else if (automaticMethod) {
          // constraints which are no bounds of a single variable need a constrained optimizer
          method = SEQUENTIAL_QUADRATIC_METHOD;
        } else {
          // `1`.
          return Errors.printMessage(head, "error",
              F.list(F.$str("Method " + method + " only takes bounds of a single variable, not "
                  + remaining + "; use Method -> \"" + SEQUENTIAL_QUADRATIC_METHOD + "\"")),
              engine);
        }
      }
      if (constrained && method.equals(SEQUENTIAL_QUADRATIC_METHOD)) {
        if (!createLinearConstraints(constraints, varsList, engine, optimizationData)) {
          // Constraints in `1` are not all 'equality' or 'less equal' or 'greater equal'
          // constraints. Constraints with Unequal(!=) are not supported.
          return Errors.printMessage(head, "eqgele", F.List(constraints), engine);
        }
      }
    } else if (simpleBounds != null && method.equals(POWELL_METHOD)) {
      method = CMAES_METHOD;
    }

    IExpr initialValue = testInitialValue(function, varsList, initialValues, goalType, engine);
    if (initialValue.isNIL()) {
      return F.NIL;
    }
    if (n == 1 && method.equals(SEQUENTIAL_QUADRATIC_METHOD) && !constrained) {
      method = POWELL_METHOD;
    }
    OptimizeSupplier optimizeSupplier = new OptimizeSupplier(head, goalType, function, varsList,
        initialValues, maxIterations, method, simpleBounds, optimizationData, constrained, engine);
    return optimizeSupplier.get();
  }

  /**
   * The method name in the spelling of the <code>*_METHOD</code> constants or <code>null</code> if
   * there is no such method.
   */
  private static String canonicalMethod(String method) {
    for (int i = 0; i < METHODS.length; i++) {
      if (METHODS[i].equalsIgnoreCase(method)) {
        return METHODS[i];
      }
    }
    return null;
  }

  private static SimpleBounds intersect(SimpleBounds first, SimpleBounds second) {
    if (first == null) {
      return second;
    }
    double[] lower = first.getLower();
    double[] upper = first.getUpper();
    double[] lower2 = second.getLower();
    double[] upper2 = second.getUpper();
    for (int i = 0; i < lower.length; i++) {
      lower[i] = Math.max(lower[i], lower2[i]);
      upper[i] = Math.min(upper[i], upper2[i]);
    }
    return new SimpleBounds(lower, upper);
  }

  /**
   * Read the search specifications <code>ast.get(2) ... ast.get(lastSpecPosition)</code>.
   *
   * @return <code>null</code> if a specification isn't valid - the message was printed in that case
   */
  private static List<VariableSpec> variableSpecs(final IAST ast, int lastSpecPosition,
      ISymbol head, EvalEngine engine) {
    IAST entries;
    if (lastSpecPosition > 2) {
      entries = F.mapRange(2, lastSpecPosition + 1, i -> ast.get(i));
    } else {
      IExpr arg2 = ast.arg2();
      if (!arg2.isList()) {
        IExpr evaluated = engine.evaluate(arg2);
        if (evaluated.isList()) {
          arg2 = evaluated;
        }
      }
      if (!arg2.isList()) {
        entries = F.List(arg2);
      } else {
        IAST list = (IAST) arg2;
        if (list.argSize() >= 2 && list.argSize() <= 4 && !list.exists(x -> x.isList())
            && engine.evaluate(list.arg2()).isNumericFunction(true)) {
          // {x, x0}, {x, x0, x1}, {x, x0, xmin, xmax}
          entries = F.List(list);
        } else {
          // {{x, x0}, {y, y0}, ...} or the variables {x, y, ...}
          entries = list;
        }
      }
    }
    if (entries.argSize() == 0) {
      // Search specification `1` should be a list with 1 to 3 elements.
      Errors.printMessage(head, "fdss", F.List(entries), engine);
      return null;
    }
    List<VariableSpec> specs = new ArrayList<VariableSpec>(entries.argSize());
    for (int i = 1; i < entries.size(); i++) {
      IExpr entry = entries.get(i);
      IExpr variable = entry.isList() ? entry.first() : entry;
      if (entry.isList() && (entry.argSize() < 1 || entry.argSize() > 4)) {
        // Search specification `1` should be a list with 1 to 3 elements.
        Errors.printMessage(head, "fdss", F.List(entry), engine);
        return null;
      }
      if (!variable.isVariable() || variable.isBuiltInSymbol()) {
        // `1` is not a valid variable.
        Errors.printMessage(head, "ivar", F.List(variable), engine);
        return null;
      }
      double start = DEFAULT_START_VALUE;
      double lower = Double.NEGATIVE_INFINITY;
      double upper = Double.POSITIVE_INFINITY;
      if (entry.isList()) {
        IAST spec = (IAST) entry;
        if (spec.argSize() >= 2) {
          // {x, x0, x1} - the second start value x1 is of no use for the optimizers
          start = engine.evaluate(spec.arg2()).evalfNaN();
        }
        if (spec.argSize() == 4) {
          lower = engine.evaluate(spec.arg3()).evalfNaN();
          upper = engine.evaluate(spec.arg4()).evalfNaN();
        }
        if (Double.isNaN(start) || Double.isNaN(lower) || Double.isNaN(upper) || lower > upper) {
          // Search specification `1` should be a list with 1 to 3 elements.
          Errors.printMessage(head, "fdss", F.List(entry), engine);
          return null;
        }
      }
      specs.add(new VariableSpec(variable, start, lower, upper));
    }
    return specs;
  }

  private static boolean createLinearConstraints(IAST andAST, IAST varsList,
      EvalEngine engine, OptimizationData[] optimizationData) {
    if (andAST.size() > 1) {
      int varsSize = varsList.argSize();
      double[] inequalitiesConstants = new double[andAST.argSize()];
      ArrayList<double[]> inequalitiesList = new ArrayList<double[]>();
      int[] inequalitiesConstantsIndex = new int[] {0};
      double[] equalitiesConstants = new double[andAST.argSize()];
      ArrayList<double[]> equalitiesList = new ArrayList<double[]>();
      int[] equalitiesConstantsIndex = new int[] {0};
      for (int i = 1; i < andAST.size(); i++) {
        IExpr temp = andAST.get(i);
        if (temp.isRelationalBinary()) {
          if (temp.isEqual()) {
            if (!createLinearRelation(F.Subtract(temp.first(), temp.second()), equalitiesList,
                equalitiesConstants, equalitiesConstantsIndex, varsList, engine)) {
              return false;
            }
          } else if (temp.isAST(S.LessEqual, 3)) {
            // see https://github.com/Hipparchus-Math/hipparchus/discussions/334
            if (!createLinearRelation(F.Subtract(temp.second(), temp.first()), inequalitiesList,
                inequalitiesConstants, inequalitiesConstantsIndex, varsList, engine)) {
              return false;
            }
          } else if (temp.isAST(S.GreaterEqual, 3)) {
            // https://github.com/Hipparchus-Math/hipparchus/discussions/334
            if (!createLinearRelation(F.Subtract(temp.first(), temp.second()), inequalitiesList,
                inequalitiesConstants, inequalitiesConstantsIndex, varsList, engine)) {
              return false;
            }
          } else {
            return false;
          }
        } else {
          return false;
        }
      }
      if (inequalitiesList.size() > 0) {
        LinearInequalityConstraint ineqc =
            createLinearInequalitiyConstraints(inequalitiesConstants, inequalitiesList, varsSize);
        optimizationData[0] = ineqc;
      }
      if (equalitiesList.size() > 0) {
        LinearEqualityConstraint eqc =
            createLinearEqualityConstraints(varsSize, equalitiesConstants, equalitiesList);
        optimizationData[1] = eqc;
      }
      return true;
    }
    return false;
  }

  /**
   * Read the linear relation <code>difference &gt;= 0</code> or <code>difference == 0</code> as a
   * row of coefficients and a constant.
   *
   * @return <code>false</code> if <code>difference</code> is not linear in the variables
   */
  private static boolean createLinearRelation(IExpr difference, ArrayList<double[]> rowList,
      double[] constants, int[] constantsIndex, IAST varsList, EvalEngine engine) {
    double[] coefficients = new double[varsList.argSize()];
    IASTAppendable rhs = F.PlusAlloc(4);
    IAST plus = engine.evaluate(F.Expand(difference)).makeAST(S.Plus);
    for (int j = 1; j < plus.size(); j++) {
      IExpr addend = plus.get(j);
      if (addend.isFree(x -> varsList.contains(x), false)) {
        rhs.append(addend.negate());
        continue;
      }
      int offset = getVariableOffset(varsList, addend);
      double coefficient = 1.0;
      if (offset < 0 && addend.isTimes()) {
        IAST times = (IAST) addend;
        for (int k = 1; k < times.size(); k++) {
          offset = getVariableOffset(varsList, times.get(k));
          if (offset >= 0) {
            IASTMutable rest = times.removeAtCopy(k);
            if (!rest.isFree(x -> varsList.contains(x), false)) {
              return false;
            }
            coefficient = rest.evalfNaN();
            break;
          }
        }
      }
      if (offset < 0 || Double.isNaN(coefficient)) {
        return false;
      }
      coefficients[offset] += coefficient;
    }
    double constant = rhs.evalfNaN();
    if (Double.isNaN(constant)) {
      return false;
    }
    rowList.add(coefficients);
    constants[constantsIndex[0]++] = constant;
    return true;
  }


  private static LinearEqualityConstraint createLinearEqualityConstraints(int varsSize,
      double[] equalitiesConstants, ArrayList<double[]> equalitiesList) {
    double[][] coefficientMatrix = new double[equalitiesList.size()][varsSize];
    double[] constantVector = new double[equalitiesList.size()];
    System.arraycopy(equalitiesConstants, 0, constantVector, 0, equalitiesList.size());
    for (int i = 0; i < equalitiesList.size(); i++) {
      double[] ds = equalitiesList.get(i);
      System.arraycopy(ds, 0, coefficientMatrix[i], 0, ds.length);
    }
    LinearEqualityConstraint eqc = new LinearEqualityConstraint(coefficientMatrix, constantVector);
    return eqc;
  }

  /**
   * Creates the linear inequality constraints from the given list of inequalities.
   * 
   * @param inequalitiesConstants
   * @param inequalitiesList
   * @param varsSize
   * @return
   */
  private static LinearInequalityConstraint createLinearInequalitiyConstraints(
      double[] inequalitiesConstants, ArrayList<double[]> inequalitiesList, int varsSize) {
    double[][] coefficientMatrix = new double[inequalitiesList.size()][varsSize];
    double[] constantVector = new double[inequalitiesList.size()];
    System.arraycopy(inequalitiesConstants, 0, constantVector, 0, inequalitiesList.size());
    for (int i = 0; i < inequalitiesList.size(); i++) {
      double[] ds = inequalitiesList.get(i);
      System.arraycopy(ds, 0, coefficientMatrix[i], 0, ds.length);
    }
    LinearInequalityConstraint ineqc =
        new LinearInequalityConstraint(coefficientMatrix, constantVector);
    return ineqc;
  }

  /**
   * Creates the simple bounds for the variables. After processing, we have a reduced
   * <code>reducedAndAST</code> list containing only the entries which are not used to determine the
   * bounds.
   * 
   * @param reducedAndAST reduced list containing only the entries which are not used to determine
   *        the bounds
   * @param varsList
   * @param engine
   * @return the simple bounds for the variables extracted from the given <code>reducedAndAST</code>
   */
  private static SimpleBounds createSimpleBounds(IASTAppendable reducedAndAST,
      IAST varsList, EvalEngine engine) {
    int varsSize = varsList.argSize();
    if (varsSize <= 0) {
      return null;
    }
    double[] lowerBounds = new double[varsSize];
    double[] upperBounds = new double[varsSize];
    for (int i = 0; i < varsSize; i++) {
      lowerBounds[i] = Double.NEGATIVE_INFINITY;
      upperBounds[i] = Double.POSITIVE_INFINITY;
    }

    int j = 1;
    while (j < reducedAndAST.size()) {
      IExpr temp = reducedAndAST.get(j);
      VariablesSet vars = new VariablesSet(temp);
      if (vars.size() == 1
          && temp.isFunctionID(ID.Greater, ID.GreaterEqual, ID.Less, ID.LessEqual)) {
        final IAST relationAST = (IAST) temp;
        final IBuiltInSymbol relationHead = (IBuiltInSymbol) relationAST.head();
        IExpr variable = vars.firstVariable();
        if (relationAST.argSize() == 2) {
          IExpr[] value = extractVariable(relationHead, relationAST.arg1(), relationAST.arg2(),
              variable, engine);
          int offset = getVariableOffset(varsList, variable);
          if (value != null && offset >= 0) {
            double bound = value[0].evalfNaN();
            if (Double.isNaN(bound)) {
              return null;
            }
            if (value[1] == S.Less) {
              upperBounds[offset] = bound;
              double ulp = Math.ulp(upperBounds[offset]);
              if (ulp > 0.0) {
                upperBounds[offset] -= 2 * ulp;
              }
            } else if (value[1] == S.LessEqual) {
              upperBounds[offset] = bound;
            } else if (value[1] == S.Greater) {
              lowerBounds[offset] = bound;
              double ulp = Math.ulp(lowerBounds[offset]);
              if (ulp > 0.0) {
                lowerBounds[offset] += 2 * ulp;
              }
            } else if (value[1] == S.GreaterEqual) {
              lowerBounds[offset] = bound;
            }
            reducedAndAST.remove(j);
            continue;
          }
        } else if (relationAST.argSize() == 3 && relationAST.arg2().equals(variable)
            && getVariableOffset(varsList, variable) >= 0) {
          int offset = getVariableOffset(varsList, variable);
          double lowerBound = relationAST.arg1().evalfNaN();
          double upperBound = relationAST.arg3().evalfNaN();
          if (Double.isNaN(lowerBound) || Double.isNaN(upperBound)) {
            return null;
          }
          if (relationHead == S.Less) {
            lowerBounds[offset] = lowerBound;
            upperBounds[offset] = upperBound;
            double ulp = Math.ulp(lowerBounds[offset]);
            if (ulp > 0.0) {
              lowerBounds[offset] += 2 * ulp;
            }
            ulp = Math.ulp(upperBounds[offset]);
            if (ulp > 0.0) {
              upperBounds[offset] -= 2 * ulp;
            }
          } else if (relationHead == S.LessEqual) {
            lowerBounds[offset] = lowerBound;
            upperBounds[offset] = upperBound;

          } else if (relationHead == S.Greater) {
            lowerBounds[offset] = upperBound;
            upperBounds[offset] = lowerBound;
            double ulp = Math.ulp(lowerBounds[offset]);
            if (ulp > 0.0) {
              lowerBounds[offset] += 2 * ulp;
            }
            ulp = Math.ulp(upperBounds[offset]);
            if (ulp > 0.0) {
              upperBounds[offset] -= 2 * ulp;
            }
          } else if (relationHead == S.GreaterEqual) {
            lowerBounds[offset] = upperBound;
            upperBounds[offset] = lowerBound;
          }
          reducedAndAST.remove(j);
          continue;

        }
      }

      j++;
    }
    return new SimpleBounds(lowerBounds, upperBounds);
  }

  /**
   * Extracts the variable from the relation and returns the value of the variable and the relation.
   * 
   * @param relation {@link S#LessEqual} or {@link S#GreaterEqual}
   * @param lhs left hand side of the relation
   * @param rhs right hand side of the relation
   * @param variable the variable to be extracted
   * @param engine the evaluation engine
   * @return an array containing the value of the variable at index 0 and the relation
   *         {@link S#GreaterEqual} or {@link S#LessEqual} at index 1 or <code>null</code> if the
   *         variable bounds could not be found
   */
  private static IExpr[] extractVariable(IBuiltInSymbol relation, IExpr lhs, IExpr rhs,
      IExpr variable, EvalEngine engine) {
    Predicate<IExpr> predicate = Predicates.in(variable);
    boolean boolArg1 = lhs.isFree(predicate, true);
    boolean boolArg2 = rhs.isFree(predicate, true);
    if (!boolArg1 && boolArg2) {
      if (lhs.isVariable()) {
        return new IExpr[] {rhs, relation};
      }
    } else if (boolArg1 && !boolArg2) {
      if (rhs.isVariable()) {
        IBuiltInSymbol newRelation = relation;
        if (relation == S.GreaterEqual) {
          newRelation = S.LessEqual;
        } else if (relation == S.Greater) {
          newRelation = S.Less;
        } else if (relation == S.LessEqual) {
          newRelation = S.GreaterEqual;
        } else if (relation == S.Less) {
          newRelation = S.Greater;
        }
        return new IExpr[] {lhs, newRelation};
      }
    }
    return null;
  }

  /**
   * Returns the offset of the variable in the list of variables.
   * 
   * @param varsList list of variables
   * @param variable the variable to be searched
   * @return <code>-1</code> if the variable is not found in the list of variables
   */
  private static int getVariableOffset(IAST varsList, IExpr variable) {
    for (int k = 1; k < varsList.size(); k++) {
      if (variable.equals(varsList.get(k))) {
        return k - 1;
      }
    }
    return -1;
  }

  /**
   * Print message &quot;nrnum&quot; if the function doesn't evaluate to a real number for the
   * initial values.
   * 
   * @param function
   * @param variableList
   * @param initialStartValues
   * @param rules
   * @param goalType
   * @param engine
   * 
   * @return
   */
  private static IExpr testInitialValue(IExpr function, IAST variableList, double[] initialValues,
      GoalType goalType, EvalEngine engine) {
    IAST initialStartValues = Convert.toVector(initialValues);
    IASTAppendable rules = F.ListAlloc();
    for (int i = 1; i < initialStartValues.size(); i++) {
      rules.append(F.Rule(variableList.get(i), initialStartValues.get(i)));
    }
    IExpr initialResult = F.NIL;
    try {
      initialResult = engine.evaluate(F.subst(function, rules));
      if (!initialResult.isNumericFunction(true)) {
        // The Function value `1` is not a real number at `2`=`3`.
        return Errors.printMessage(goalType == GoalType.MINIMIZE ? S.FindMinimum : S.FindMaximum,
            "nrnum", F.List(initialResult, variableList, initialStartValues), engine);
      }
      IReal realNumber = initialResult.evalReal();
      if (realNumber == null) {
        // The Function value `1` is not a real number at `2`=`3`.
        return Errors.printMessage(goalType == GoalType.MINIMIZE ? S.FindMinimum : S.FindMaximum,
            "nrnum", F.List(initialResult, variableList, initialStartValues), engine);
      }
      return realNumber;
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    // The Function value `1` is not a real number at `2`=`3`.
    return Errors.printMessage(goalType == GoalType.MINIMIZE ? S.FindMinimum : S.FindMaximum,
        "nrnum", F.List(function, variableList, initialStartValues), engine);
  }

  private static class OptimizeSupplier implements Supplier<IExpr> {
    final ISymbol head;
    final int maxIterations;
    final GoalType goalType;
    final IExpr originalFunction;
    final IAST variableList;
    final double[] initialValues;
    final SimpleBounds simpleBounds;
    final OptimizationData[] optimizationData;
    final boolean constrained;
    String method;
    final EvalEngine engine;

    public OptimizeSupplier(ISymbol head, GoalType goalType, IExpr function, IAST variableList,
        double[] initialValues, int maxIterations, String method, SimpleBounds simpleBounds,
        OptimizationData[] optimizationData, boolean constrained, EvalEngine engine) {
      this.head = head;
      this.goalType = goalType;
      this.originalFunction = function;
      this.variableList = variableList;
      this.initialValues = initialValues;
      this.maxIterations = maxIterations;
      this.method = method;
      this.simpleBounds = simpleBounds;
      this.optimizationData = optimizationData;
      this.constrained = constrained;
      this.engine = engine;
    }

    @Override
    public IExpr get() {
      PointValuePair optimum = null;
      InitialGuess initialGuess = new InitialGuess(initialValues);
      // MaxIterations counts the iterations of a method; the function evaluations only get a
      // ceiling which keeps a diverging search from running forever
      final MaxIter maxIter = new MaxIter(maxIterations);
      final int maxEvaluations = (int) Math.min(Integer.MAX_VALUE,
          Math.max(MIN_EVALUATIONS, EVALUATIONS_PER_ITERATION * (long) maxIterations));
      final MaxEval maxEval = new MaxEval(maxEvaluations);
      IExpr function = engine.evaluate(originalFunction);
      if (method.equals(SEQUENTIAL_QUADRATIC_METHOD)) {
        try {
          // https://github.com/Hipparchus-Math/hipparchus/pull/404
          // SQPOptimizerS2 doesn't read the GoalType and always minimizes
          final boolean maximize = goalType == GoalType.MAXIMIZE;
          ConstraintOptimizer optim = new SQPOptimizerS2();
          TwiceDifferentiableMultiVariateNumerical twiceDifferentiableFunction =
              new TwiceDifferentiableMultiVariateNumerical(
                  maximize ? engine.evaluate(F.Negate(function)) : function, variableList, true);
          twiceDifferentiableFunction.setMaxEval(maxEvaluations);
          LagrangeSolution lagrangeSolution = optim.optimize( //
              maxEval, //
              maxIter, //
              new ObjectiveFunction(twiceDifferentiableFunction), //
              GoalType.MINIMIZE, //
              initialGuess, //
              optimizationData[0], //
              optimizationData[1]);
          if ((lagrangeSolution != null)) {
            RealVector solutionVector = lagrangeSolution.getX();
            IASTAppendable ruleList = F.mapRange(1, variableList.size(),
                j -> F.Rule(variableList.get(j), F.num(solutionVector.getEntry(j - 1))));
            final double value = lagrangeSolution.getValue();
            return F.list(F.num(maximize ? -value : value), ruleList);
          }
          return F.NIL;
        } catch (MathIllegalStateException mise) {
          if (mise.getSpecifier().equals(LocalizedCoreFormats.MAX_COUNT_EXCEEDED)) {
            throw mise;
          }
          if (constrained) {
            // the other methods can't take the constraints
            throw mise;
          }
          method = POWELL_METHOD;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          if (constrained) {
            // `1`.
            return Errors.printMessage(head, "error", F.list(F.$str(String.valueOf(rex.getMessage()))),
                engine);
          }
          method = POWELL_METHOD;
        }
      }
      MultiVariateNumerical multiVariateNumerical =
          new MultiVariateNumerical(function, variableList);
      if (method.equals(CONJUGATEGRADIENT_METHOD)) {
        MultiVariateVectorGradient multiVariateVectorGradient =
            new MultiVariateVectorGradient(function, variableList, true);
        if (isNumeric(multiVariateVectorGradient.value(initialValues))) {
          try {
            Formula formula = NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE;
            optimum = conjugateGradient(multiVariateVectorGradient, multiVariateNumerical, formula,
                initialGuess, maxEval, maxIter);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            Formula formula = NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES;
            optimum = conjugateGradient(multiVariateVectorGradient, multiVariateNumerical, formula,
                initialGuess, maxEval, maxIter);
          }
          if (optimum != null && simpleBounds != null && !inBounds(optimum.getPointRef())) {
            // the conjugate gradient method takes no bounds and left them
            optimum = null;
            method = CMAES_METHOD;
          }
        } else {
          // no symbolic gradient, for example f(x_?NumericQ):=... - use a derivative free method
          method = simpleBounds == null ? POWELL_METHOD : CMAES_METHOD;
        }
      }
      if (method.equals(BOBYQA_METHOD) && initialValues.length < 2) {
        method = CMAES_METHOD;
      }
      if (method.equals(CMAES_METHOD)) {
        final CMAESOptimizer optim = new CMAESOptimizer(Math.max(30000, maxIterations), // Max Iterations
            0.0, // Stop fitness
            true, // Is active CMA?
            10, //
            0, //
            new RandomDataGenerator(), // Random generator
            true, // Use Boundaries?
            null);
        PopulationSize populationSize = new PopulationSize(5);
        Sigma sigma = calculateCMAESSigma(initialValues.length, simpleBounds, initialValues);

        optimum = optim.optimize(//
            maxEval, //
            new ObjectiveFunction(multiVariateNumerical), //
            populationSize, //
            sigma, //
            goalType, //
            initialGuess, //
            simpleBounds == null ? SimpleBounds.unbounded(initialValues.length) : simpleBounds);
      } else if (method.equals(BOBYQA_METHOD)) {
        BOBYQAOptimizer optim = new BOBYQAOptimizer(2 * initialValues.length + 1);
        optimum = optim.optimize(//
            maxEval, //
            maxIter, //
            new ObjectiveFunction(multiVariateNumerical), //
            goalType, //
            initialGuess, //
            simpleBounds == null ? SimpleBounds.unbounded(initialValues.length) : simpleBounds);
      } else if (method.equals(POWELL_METHOD)) {
        // The default convergence check of the PowellOptimizer, 2*(fX-fVal) <= threshold, is true
        // after the first sweep of every maximization, so the negated function is minimized.
        final boolean maximize = goalType == GoalType.MAXIMIZE;
        final PowellOptimizer optim = new PowellOptimizer(1e-10, Math.ulp(1d), 1e-10, Math.ulp(1d));
        optimum = optim.optimize( //
            maxEval, //
            maxIter, //
            new ObjectiveFunction(
                maximize ? point -> -multiVariateNumerical.value(point) : multiVariateNumerical), //
            GoalType.MINIMIZE, //
            initialGuess);
        if (maximize) {
          optimum = new PointValuePair(optimum.getPointRef(), -optimum.getValue(), false);
        }
      }

      if ((optimum != null)) {
        final double[] point = optimum.getPointRef();
        if (!isNumeric(point) || !(Math.abs(optimum.getValue()) < DIVERGED)) {
          // the function is unbounded in the direction of the search
          // Failed to converge to the requested accuracy or precision within `1` iterations.
          return Errors.printMessage(head, "cvmit", F.list(F.ZZ(maxIterations)), engine);
        }
        IASTAppendable ruleList = F.mapRange(1, variableList.size(),
            j -> F.Rule(variableList.get(j), F.num(point[j - 1])));
        final double value = optimum.getValue();
        return F.list(F.num(value), ruleList);
      }
      return F.NIL;
    }

    private boolean inBounds(double[] point) {
      double[] lower = simpleBounds.getLower();
      double[] upper = simpleBounds.getUpper();
      for (int i = 0; i < point.length; i++) {
        if (point[i] < lower[i] || point[i] > upper[i]) {
          return false;
        }
      }
      return true;
    }

    private static boolean isNumeric(double[] values) {
      for (int i = 0; i < values.length; i++) {
        if (Double.isNaN(values[i])) {
          return false;
        }
      }
      return true;
    }

    private PointValuePair conjugateGradient(MultiVariateVectorGradient multiVariateVectorGradient,
        MultiVariateNumerical multiVariateNumerical, Formula formula, InitialGuess initialGuess,
        MaxEval maxEval, MaxIter maxIter) {
      // a local search from the start value: no MultiStartMultivariateOptimizer, which returns the
      // best of some random starts and with it a minimum far away from the start value
      NonLinearConjugateGradientOptimizer optimizer =
          new NonLinearConjugateGradientOptimizer(formula, new SimpleValueChecker(1e-10, 1e-10));
      return optimizer.optimize(//
          maxEval, //
          maxIter, //
          new ObjectiveFunction(multiVariateNumerical), //
          new ObjectiveFunctionGradient(multiVariateVectorGradient), //
          goalType, //
          initialGuess);
    }
  }

  /**
   * Calculates initial sigma values for the CMAES method based on bounds and start point.
   *
   * @param dimension The problem dimension.
   * @param bounds SimpleBounds object (can be null or have null/infinite entries).
   * @param startPoint The initial guess array.
   * @param defaultSigmaForInfinite Bounds A default sigma if both bounds are infinite and start
   *        point is 0.
   * @param rangeFraction The fraction of the finite range to use (e.g., 0.25 for 1/4).
   * @param startPointFraction The fraction of the start point magnitude to use when bounds are
   *        infinite.
   * @param distanceToBoundFraction The fraction of the distance to a finite bound when one bound is
   *        infinite.
   * @param minSigma A small minimum value for sigma to prevent zero sigma.
   * @return A double array containing the calculated initial sigma for each dimension.
   * @throws MathIllegalArgumentException if startPoint length doesn't match dimension.
   */
  private static double[] calculateCMAESSigma(int dimension, SimpleBounds bounds,
      double[] startPoint, double defaultSigmaForInfiniteBounds, double rangeFraction,
      double startPointFraction, double distanceToBoundFraction, double minSigma) {
    if (startPoint.length != dimension) {
      throw new MathIllegalArgumentException(
          org.hipparchus.exception.LocalizedCoreFormats.DIMENSIONS_MISMATCH, startPoint.length,
          dimension);
    }

    double[] sigma = new double[dimension];
    double[] lower = (bounds == null) ? null : bounds.getLower();
    double[] upper = (bounds == null) ? null : bounds.getUpper();

    for (int i = 0; i < dimension; i++) {
      double lowerValue =
          (lower == null || lower.length <= i) ? Double.NEGATIVE_INFINITY : lower[i];
      double upperValue =
          (upper == null || upper.length <= i) ? Double.POSITIVE_INFINITY : upper[i];
      double start = startPoint[i];

      boolean isLowerFinite = Double.isFinite(lowerValue);
      boolean isUpperFinite = Double.isFinite(upperValue);

      if (isLowerFinite && isUpperFinite) {
        // Case 1: Both bounds are finite
        double range = upperValue - lowerValue;
        sigma[i] = Math.max(minSigma, range * rangeFraction);
        // Handle potential zero range if L == U (though unlikely for optimization)
        if (sigma[i] <= minSigma && range == 0.0) {
          // If bounds are equal, maybe this variable is fixed?
          // Set a very small sigma or handle as per problem definition.
          // Using minSigma is usually safe enough.
          sigma[i] = minSigma;
        }

      } else if (isLowerFinite && !isUpperFinite) {
        // Case 2: Lower finite, Upper infinite
        double distToBounds = Math.abs(start - lowerValue);
        sigma[i] = Math.max(minSigma, distToBounds * distanceToBoundFraction);
        // If start is exactly at the bound, dist is 0. Rely on minSigma.

      } else if (!isLowerFinite && isUpperFinite) {
        // Case 3: Lower infinite, Upper finite
        double distToBounds = Math.abs(upperValue - start);
        sigma[i] = Math.max(minSigma, distToBounds * distanceToBoundFraction);
        // If start is exactly at the bound, dist is 0. Rely on minSigma.

      } else {
        // Case 4: Both bounds infinite
        if (start == 0.0) {
          sigma[i] = Math.max(minSigma, defaultSigmaForInfiniteBounds);
        } else {
          sigma[i] = Math.max(minSigma, Math.abs(start) * startPointFraction);
        }
      }
      // Final check to ensure sigma is positive
      if (sigma[i] <= 0) {
        sigma[i] = minSigma;
      }
    }
    return sigma;
  }

  /**
   * Simplified version with default heuristic parameters. Calculates initial sigma values CMAES
   * method based on bounds and start point. Uses range/4, dist/2, abs(start)/4, default 1.0, min
   * 1e-6.
   *
   * @param dimension The problem dimension.
   * @param bounds SimpleBounds object (can be null or have null/infinite entries).
   * @param startPoint The initial guess array.
   * @return a double array containing the calculated initial sigma for each dimension.
   */
  private static Sigma calculateCMAESSigma(int dimension, SimpleBounds bounds,
      double[] startPoint) {
    double[] calculatedSigma = calculateCMAESSigma(dimension, bounds, startPoint, 1.0, // defaultSigmaForInfiniteBounds
        0.25, // rangeFraction (1/4)
        0.25, // startPointFraction (1/4)
        0.50, // distanceToBoundFraction (1/2)
        1e-6); // minSigma
    return new Sigma(calculatedSigma);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.HOLDALL);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {//
            S.MaxIterations, S.Method}, //
        new IExpr[] {//
            S.Automatic, S.Automatic});
  }

}
