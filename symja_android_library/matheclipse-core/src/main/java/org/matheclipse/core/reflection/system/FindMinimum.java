package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.function.Supplier;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.RealVector;
import org.hipparchus.optim.InitialGuess;
import org.hipparchus.optim.MaxEval;
import org.hipparchus.optim.OptimizationData;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.SimpleValueChecker;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.hipparchus.optim.nonlinear.scalar.GradientMultivariateOptimizer;
import org.hipparchus.optim.nonlinear.scalar.MultiStartMultivariateOptimizer;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunction;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.hipparchus.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.hipparchus.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.hipparchus.optim.nonlinear.vector.constrained.ConstraintOptimizer;
import org.hipparchus.optim.nonlinear.vector.constrained.LagrangeSolution;
import org.hipparchus.optim.nonlinear.vector.constrained.LinearEqualityConstraint;
import org.hipparchus.optim.nonlinear.vector.constrained.LinearInequalityConstraint;
import org.hipparchus.optim.nonlinear.vector.constrained.SQPOptimizerS;
import org.hipparchus.random.GaussianRandomGenerator;
import org.hipparchus.random.JDKRandomGenerator;
import org.hipparchus.random.RandomVectorGenerator;
import org.hipparchus.random.UncorrelatedRandomVectorGenerator;
import org.matheclipse.core.basic.OperationSystem;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.MultiVariateNumerical;
import org.matheclipse.core.generic.MultiVariateVectorGradient;
import org.matheclipse.core.generic.TwiceDifferentiableMultiVariateNumerical;
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
 * <code>FindMinimum(f, {x, xstart}, Method-&gt;method_name)
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
 * <p>
 * See
 * </p>
 * <ul>
 * <li><a href="https://en.wikipedia.org/wiki/Mathematical_optimization">Wikipedia - Mathematical
 * optimization</a></li>
 * </ul>
 * <h4>Powell</h4>
 * <p>
 * Implements the Powell optimizer.
 * </p>
 * <p>
 * This is the default method, if no <code>method_name</code> is given.
 * </p>
 * <h4>ConjugateGradient</h4>
 * <p>
 * Implements the ConjugateGradient optimizer.<br />
 * This is a derivative based method and the functions must be symbolically differentiatable.
 * </p>
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; FindMinimum(Sin(x), {x, 0.5})
 * {-1.0,{x-&gt;-1.5708}}
 * </code>
 * </pre>
 */
public class FindMinimum extends AbstractFunctionOptionEvaluator {


  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    GoalType goalType = GoalType.MINIMIZE;
    try {
      return findExtremum(ast, goalType, engine, options);
    } catch (MathIllegalArgumentException miae) {
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(miae.getMessage())), engine);
    } catch (MathIllegalStateException mise) {
      if (mise.getSpecifier().equals(LocalizedCoreFormats.MAX_COUNT_EXCEEDED)) {
        // Failed to converge to the requested accuracy or precision within `1` iterations.
        return Errors.printMessage(ast.topHead(), "cvmit", F.list(F.$str("?")), engine);
      }
      // `1`.
      return Errors.printMessage(ast.topHead(), "error", F.list(F.$str(mise.getMessage())), engine);
    } catch (MathRuntimeException mre) {
      mre.printStackTrace();
      Errors.printMessage(ast.topHead(), "error", F.list(F.$str(mre.getMessage())), engine);
      return F.CEmptyList;
    }
  }


  protected static IExpr findExtremum(IAST ast, GoalType goalType, EvalEngine engine,
      IExpr[] options) {
    IAST relationList = ast.arg1().makeList();
    if (relationList.argSize() == 0) {
      return F.NIL;
    }
    IExpr function = relationList.arg1();
    if (relationList.argSize() > 2 && !relationList.arg2().isAnd()) {
      relationList = F.List(function, relationList.copyFrom(2).apply(S.And));
    }
    IExpr arg2 = ast.arg2();
    if (!arg2.isList()) {
      arg2 = engine.evaluate(arg2);
    }
    VariablesSet vars = new VariablesSet(arg2);
    if (vars.size() == 0) {
      return F.NIL;
    }
    OptimizationData[] optimizationData = new OptimizationData[0];
    for (int i = 2; i < relationList.size(); i++) {
      IExpr expr = relationList.get(i);
      if (expr.isAnd()) {
        IAST andAST = (IAST) expr;
        optimizationData = new OptimizationData[2];
        if (!createLinearConstraints(andAST, vars, engine, optimizationData)) {
          // Constraints in `1` are not all 'equality' or 'less equal' or 'greater equal'
          // constraints. Constraints with Unequal(!=) are not supported.
          return Errors.printMessage(ast.topHead(), "eqgele", F.List(andAST), engine);
        }
        continue;
      }
      if (expr.isRelationalBinary()) {

      } else {
        if (function.isPresent()) {
          return F.NIL;
        }
        function = expr;
      }
    }
    if (arg2.isList() && arg2.argSize() >= 2) {
      String method = "Powell";
      int maxIterations = 100;

      if (options[0].isInteger()) {
        // S.MaxIterations
        maxIterations = options[0].toIntDefault();
        if (maxIterations == Integer.MIN_VALUE) {
          return F.NIL;
        }
        if (maxIterations < 0) {
          maxIterations = 100;
        }
      }
      if (!options[1].equals(S.Automatic)) {
        if (options[1].isSymbol() || options[1].isString()) {
          // S.Method
          method = options[1].toString();
        }
      } else {
        if (ast.size() >= 4) {
          if (ast.arg3().isSymbol()) {
            method = ast.arg3().toString();
          }
        }
      }

      // if (ast.size() >= 4) {
      // final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
      // maxIterations = options.getOptionMaxIterations(S.MaxIterations);
      // if (maxIterations == Integer.MIN_VALUE) {
      // return F.NIL;
      // }
      // if (maxIterations < 0) {
      // maxIterations = 100;
      // }
      //
      // IExpr optionMethod = options.getOption(S.Method);
      // if (optionMethod.isSymbol() || optionMethod.isString()) {
      // method = optionMethod.toString();
      // } else {
      // if (ast.arg3().isSymbol()) {
      // method = ast.arg3().toString();
      // }
      // }
      // }
      return optimizeGoal(goalType, function, (IAST) arg2, maxIterations, method, optimizationData,
          engine);
    }
    return F.NIL;
  }


  private static boolean createLinearConstraints(IAST andAST, VariablesSet vars, EvalEngine engine,
      OptimizationData[] optimizationData) {
    if (andAST.size() > 1) {
      int varsSize = vars.size();
      IASTAppendable varsList = vars.getVarList();
      double[] inequalitiesConstants = new double[andAST.size() - 1];
      ArrayList<double[]> inequalitiesList = new ArrayList<double[]>();
      int[] inequalitiesConstantsIndex = new int[] {0};
      double[] equalitiesConstants = new double[andAST.size() - 1];
      ArrayList<double[]> equalitiesList = new ArrayList<double[]>();
      int[] equalitiesConstantsIndex = new int[] {0};
      for (int i = 1; i < andAST.size(); i++) {
        IExpr temp = andAST.get(i);
        if (temp.isRelationalBinary()) {
          if (temp.isAST(S.Equal, 3)) {
            if (!createEqualityRelation((IAST) temp, equalitiesList, equalitiesConstants,
                equalitiesConstantsIndex, varsList, engine)) {
              return false;
            }
          } else if (temp.isAST(S.LessEqual, 3)) {
            // see https://github.com/Hipparchus-Math/hipparchus/discussions/334
            if (!createInequalityRelation((IAST) temp, inequalitiesList, inequalitiesConstants,
                inequalitiesConstantsIndex, varsList, true, engine)) {
              return false;
            }
          } else if (temp.isAST(S.GreaterEqual, 3)) {
            // https://github.com/Hipparchus-Math/hipparchus/discussions/334
            if (!createInequalityRelation((IAST) temp, inequalitiesList, inequalitiesConstants,
                inequalitiesConstantsIndex, varsList, false, engine)) {
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

  private static boolean createEqualityRelation(IAST relation, ArrayList<double[]> equalitiesList,
      double[] equalitiesConstants, int[] equalitiesConstantsIndex, IASTAppendable varsList,
      EvalEngine engine) {
    double[] coefficients = new double[varsList.size() - 1];
    equalitiesList.add(coefficients);
    IASTAppendable rhs = F.PlusAlloc(4);
    IExpr lhs;
    lhs = engine.evaluate(F.Subtract(relation.first(), relation.second()));
    IAST plus = lhs.makeAST(S.Plus);
    for (int j = 1; j < plus.size(); j++) {
      IExpr addend = plus.get(j);
      if (addend.isFree(x -> varsList.contains(x), false)) {
        rhs.append(addend.negate());
        continue;
      }
      if (addend.isTimes()) {
        IAST times = (IAST) addend;
        for (int k = 1; k < times.size(); k++) {
          IExpr factor = times.get(k);
          int offset = getVariableOffset(varsList, factor);
          if (offset >= 0) {
            IASTMutable coefficient = times.removeAtCopy(k);
            if (!coefficient.isFree(x -> varsList.contains(x), false)) {
              return false;
            }
            coefficients[offset] = coefficient.evalf();
          }
        }
        continue;
      }
      int offset = getVariableOffset(varsList, addend);
      if (offset < 0) {
        return false;
      }
      coefficients[offset] = 1.0;
    }
    equalitiesConstants[equalitiesConstantsIndex[0]++] = rhs.evalf();
    return true;
  }

  private static boolean createInequalityRelation(IAST relation,
      ArrayList<double[]> inequalitiesList, double[] inequalitiesConstants,
      int[] inequalitiesConstantsIndex, IASTAppendable varsList, boolean lessOperator,
      EvalEngine engine) {
    double[] coefficients = new double[varsList.size() - 1];
    inequalitiesList.add(coefficients);
    IASTAppendable rhs = F.PlusAlloc(4);
    IExpr lhs;
    if (lessOperator) {
      // negate both sides to get a "greater" relation
      lhs = engine.evaluate(F.Subtract(F.Negate(relation.first()), F.Negate(relation.second())));
    } else {
      lhs = engine.evaluate(F.Subtract(relation.first(), relation.second()));
    }
    IAST plus = lhs.makeAST(S.Plus);
    for (int j = 1; j < plus.size(); j++) {
      IExpr addend = plus.get(j);
      if (addend.isFree(x -> varsList.contains(x), false)) {
        rhs.append(addend.negate());
        continue;
      }
      if (addend.isTimes()) {
        IAST times = (IAST) addend;
        for (int k = 1; k < times.size(); k++) {
          IExpr factor = times.get(k);
          int offset = getVariableOffset(varsList, factor);
          if (offset >= 0) {
            IASTMutable coefficient = times.removeAtCopy(k);
            if (!coefficient.isFree(x -> varsList.contains(x), false)) {
              return false;
            }
            coefficients[offset] = coefficient.evalf();
          }
        }
        continue;
      }
      int offset = getVariableOffset(varsList, addend);
      if (offset < 0) {
        return false;
      }
      coefficients[offset] = 1.0;
    }
    inequalitiesConstants[inequalitiesConstantsIndex[0]++] = rhs.evalf();
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


  private static int getVariableOffset(IASTAppendable varsList, IExpr addend) {
    for (int k = 1; k < varsList.size(); k++) {
      if (addend.equals(varsList.get(k))) {
        return k - 1;
      }
    }
    return -1;
  }

  private static IExpr optimizeGoal(GoalType goalType, IExpr function, IAST list,
      int maxIterations, String method, OptimizationData[] optimizationData, EvalEngine engine) {
    double[] initialValues = null;
    IAST variableList = null;
    int[] dimension = list.isMatrix();
    if (dimension == null) {
      if (list.argSize() == 1) {
        initialValues = new double[1];
        initialValues[0] = 1.999999999999999;
        variableList = F.list(list.arg1());
      } else if (list.argSize() == 2 && !list.arg2().isSymbol()) {
        initialValues = new double[1];
        initialValues[0] = list.arg2().evalf();
        variableList = F.list(list.arg1());
      } else {
        initialValues = new double[list.argSize()];
        variableList = list;
        for (int i = 0; i < initialValues.length; i++) {
          initialValues[i] = 1.999999999999999;
        }
      }
    } else {
      if (dimension[0] == list.argSize()) {
        IASTAppendable vars = F.ListAlloc();
        if (dimension[1] == 1) {
          initialValues = new double[list.argSize()];
          for (int i = 0; i < list.argSize(); i++) {
            IAST row = (IAST) list.get(i + 1);
            initialValues[i] = list.getPart(i + 1, 2).evalf();
            vars.append(row.arg1());
          }
          variableList = vars;
        } else if (dimension[1] == 2) {
          initialValues = new double[list.argSize()];
          for (int i = 0; i < list.argSize(); i++) {
            IAST row = (IAST) list.get(i + 1);
            initialValues[i] = row.arg2().evalf();
            vars.append(row.arg1());
          }
          variableList = vars;
        }
      }
    }
    if (initialValues != null) {
      IExpr initialValue =
          testInitialValue(function, variableList, initialValues, goalType, engine);
      if (initialValue.isNIL()) {
        return F.NIL;
      }

      if (variableList.argSize() == 1 && method.equalsIgnoreCase("sequentialquadratic")) {
        method = "Powell";
      }
      OptimizeSupplier optimizeSupplier = new OptimizeSupplier(goalType, function, variableList,
          initialValues, maxIterations, method, optimizationData, engine);
      return engine.evalBlock(optimizeSupplier, variableList);
    }
    return F.NIL;
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
      initialResult = engine.evaluate(function.replaceAll(rules));
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
    final int maxIterations;
    final GoalType goalType;
    final IExpr originalFunction;
    final IAST variableList;
    final double[] initialValues;
    OptimizationData[] optimizationData;
    String method;
    final EvalEngine engine;

    public OptimizeSupplier(GoalType goalType, IExpr function, IAST variableList,
        double[] initialValues, int maxIterations, String method,
        OptimizationData[] optimizationData, EvalEngine engine) {
      this.goalType = goalType;
      this.originalFunction = function;
      this.variableList = variableList;
      this.initialValues = initialValues;
      this.maxIterations = maxIterations;
      this.method = method;
      this.optimizationData = optimizationData;
      this.engine = engine;
    }

    @Override
    public IExpr get() {
      PointValuePair optimum = null;
      InitialGuess initialGuess = new InitialGuess(initialValues);
      IExpr function = engine.evaluate(originalFunction);
      if (method.equalsIgnoreCase("sequentialquadratic")) {
        try {
          ConstraintOptimizer optim = new SQPOptimizerS();
          TwiceDifferentiableMultiVariateNumerical twiceDifferentiableFunction =
              new TwiceDifferentiableMultiVariateNumerical(function, variableList, true);
          // x > 0, y > 0
          // LinearInequalityConstraint ineqc = new LinearInequalityConstraint(
          // new double[][] {{1.0, 0.0}, {0.0, 1.0}}, new double[] {0.0, 0.0});
          LagrangeSolution lagrangeSolution = optim.optimize( //
              new MaxEval(maxIterations), //
              new ObjectiveFunction(twiceDifferentiableFunction), //
              goalType, //
              initialGuess, optimizationData[0], optimizationData[1]);
          if ((lagrangeSolution != null)) {
            RealVector solutionVector = lagrangeSolution.getX();
            IASTAppendable ruleList = F.mapRange(1, variableList.size(),
                j -> F.Rule(variableList.get(j), F.num(solutionVector.getEntry(j - 1))));
            final double value = lagrangeSolution.getValue();
            return F.list(F.num(value), ruleList);
          }
          return F.NIL;
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          rex.printStackTrace();
          method = "Powell";
        }
      }
      MultiVariateNumerical multivariateVariateNumerical =
          new MultiVariateNumerical(function, variableList);
      if (method.equalsIgnoreCase("powell")) {
        final PowellOptimizer optim = new PowellOptimizer(1e-10, Math.ulp(1d), 1e-10, Math.ulp(1d));
        optimum = optim.optimize( //
            new MaxEval(maxIterations), //
            new ObjectiveFunction(multivariateVariateNumerical), //
            goalType, //
            initialGuess);
      } else if (method.equalsIgnoreCase("conjugategradient")) {
        GradientMultivariateOptimizer underlying = new NonLinearConjugateGradientOptimizer(
            NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE,
            new SimpleValueChecker(1e-10, 1e-10));
        JDKRandomGenerator g = new JDKRandomGenerator();
        g.setSeed(753289573253l);
        double[] mean = new double[initialValues.length];
        for (int i = 0; i < mean.length; i++) {
          mean[i] = 0.0;
        }
        double[] standardDeviation = new double[initialValues.length];
        for (int i = 0; i < mean.length; i++) {
          standardDeviation[i] = 1.0;
        }
        RandomVectorGenerator generator = new UncorrelatedRandomVectorGenerator(mean,
            standardDeviation, new GaussianRandomGenerator(g));
        int nbStarts = 10;
        MultiStartMultivariateOptimizer optimizer =
            new MultiStartMultivariateOptimizer(underlying, nbStarts, generator);

        optimum = optimizer.optimize(//
            new MaxEval(maxIterations), //
            new ObjectiveFunction(multivariateVariateNumerical), //
            new ObjectiveFunctionGradient(
                new MultiVariateVectorGradient(function, variableList, true)), //
            goalType, //
            initialGuess);
      }


      if ((optimum != null)) {
        final double[] point = optimum.getPointRef();
        IASTAppendable ruleList = F.mapRange(1, variableList.size(),
            j -> F.Rule(variableList.get(j), F.num(point[j - 1])));
        final double value = optimum.getValue();
        return F.list(F.num(value), ruleList);
      }
      return F.NIL;
    }
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {//
            S.MaxIterations, S.Method}, //
        new IExpr[] {//
            F.C100, S.Automatic});
  }

}
