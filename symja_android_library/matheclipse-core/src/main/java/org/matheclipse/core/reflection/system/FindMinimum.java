package org.matheclipse.core.reflection.system;

import java.util.function.Supplier;
import org.hipparchus.exception.MathIllegalStateException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.optim.InitialGuess;
import org.hipparchus.optim.MaxEval;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.SimpleValueChecker;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.hipparchus.optim.nonlinear.scalar.GradientMultivariateOptimizer;
import org.hipparchus.optim.nonlinear.scalar.MultiStartMultivariateOptimizer;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunction;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.hipparchus.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.hipparchus.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.hipparchus.random.GaussianRandomGenerator;
import org.hipparchus.random.JDKRandomGenerator;
import org.hipparchus.random.RandomVectorGenerator;
import org.hipparchus.random.UncorrelatedRandomVectorGenerator;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.MultiVariateFunction;
import org.matheclipse.core.generic.MultiVariateVectorGradient;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
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
public class FindMinimum extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    GoalType goalType = GoalType.MINIMIZE;
    try {
      return findExtremum(ast, engine, goalType);
    } catch (MathIllegalStateException miae) {
      // `1`.
      return IOFunctions.printMessage(ast.topHead(), "error", F.List(F.$str(miae.getMessage())),
          engine);
    } catch (MathRuntimeException mre) {
      IOFunctions.printMessage(ast.topHead(), "error", F.List(F.$str(mre.getMessage())), engine);
      return F.CEmptyList;
    }
  }

  protected static IExpr findExtremum(IAST ast, EvalEngine engine, GoalType goalType) {
    IExpr function = ast.arg1();
    IExpr arg2 = ast.arg2();
    if (!arg2.isList()) {
      arg2 = engine.evaluate(arg2);
    }
    if (arg2.isList() && arg2.argSize() >= 2) {
      String method = "Powell";
      int maxIterations = 100;
      if (ast.size() >= 4) {
        final OptionArgs options = new OptionArgs(ast.topHead(), ast, 3, engine);
        maxIterations = options.getOptionMaxIterations(S.MaxIterations);
        if (maxIterations == Integer.MIN_VALUE) {
          return F.NIL;
        }
        if (maxIterations < 0) {
          maxIterations = 100;
        }

        IExpr optionMethod = options.getOption(S.Method);
        if (optionMethod.isSymbol() || optionMethod.isString()) {
          method = optionMethod.toString();
        } else {
          if (ast.arg3().isSymbol()) {
            method = ast.arg3().toString();
          }
        }
      }
      return optimizeGoal(method, maxIterations, goalType, function, (IAST) arg2, engine);
    }
    return F.NIL;
  }

  private static IExpr optimizeGoal(String method, int maxIterations, GoalType goalType,
      IExpr function, IAST list, EvalEngine engine) {
    double[] initialValues = null;
    IAST variableList = null;
    int[] dimension = list.isMatrix();
    if (dimension == null) {
      if (list.argSize() == 1) {
        initialValues = new double[1];
        initialValues[0] = 1.0;
        variableList = F.List(list.arg1());
      } else if (list.argSize() == 2 && !list.arg2().isSymbol()) {
        initialValues = new double[1];
        initialValues[0] = list.arg2().evalDouble();
        variableList = F.List(list.arg1());
      } else {
        initialValues = new double[list.argSize()];
        variableList = list;
        for (int i = 0; i < initialValues.length; i++) {
          initialValues[i] = 1.0;
        }
      }
    } else {
      if (dimension[0] == list.argSize()) {
        IASTAppendable vars = F.ListAlloc();
        if (dimension[1] == 1) {
          initialValues = new double[list.argSize()];
          for (int i = 0; i < list.argSize(); i++) {
            IAST row = (IAST) list.get(i + 1);
            initialValues[i] = list.getPart(i + 1, 2).evalDouble();
            vars.append(row.arg1());
          }
          variableList = vars;
        } else if (dimension[1] == 2) {
          initialValues = new double[list.argSize()];
          for (int i = 0; i < list.argSize(); i++) {
            IAST row = (IAST) list.get(i + 1);
            initialValues[i] = row.arg2().evalDouble();
            vars.append(row.arg1());
          }
          variableList = vars;
        }
      }
    }
    if (initialValues != null) {
      OptimizeSupplier optimizeSupplier =
          new OptimizeSupplier(goalType, function, variableList, initialValues, method, engine);
      return engine.evalBlock(optimizeSupplier, variableList);
    }
    return F.NIL;
  }

  private static class OptimizeSupplier implements Supplier<IExpr> {
    final GoalType goalType;
    final IExpr originalFunction;
    final IAST variableList;
    final double[] initialValues;
    final String method;
    final EvalEngine engine;

    public OptimizeSupplier(GoalType goalType, IExpr function, IAST variableList,
        double[] initialValues, String method, EvalEngine engine) {
      this.goalType = goalType;
      this.originalFunction = function;
      this.variableList = variableList;
      this.initialValues = initialValues;
      this.method = method;
      this.engine = engine;
    }

    @Override
    public IExpr get() {
      PointValuePair optimum = null;
      InitialGuess initialGuess = new InitialGuess(initialValues);
      IExpr function = engine.evaluate(originalFunction);
      if (method.equals("Powell")) {
        final PowellOptimizer optim = new PowellOptimizer(1e-10, Math.ulp(1d), 1e-10, Math.ulp(1d));
        optimum = optim.optimize( //
            new MaxEval(1000), //
            new ObjectiveFunction(new MultiVariateFunction(function, variableList)), //
            goalType, //
            initialGuess);
      } else if (method.equals("ConjugateGradient")) {
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
            new MaxEval(1000), //
            new ObjectiveFunction(new MultiVariateFunction(function, variableList)), //
            new ObjectiveFunctionGradient(new MultiVariateVectorGradient(function, variableList)), //
            goalType, //
            initialGuess);
      }

      if ((optimum != null)) {
        final double[] point = optimum.getPointRef();
        final double value = optimum.getValue();
        IASTAppendable ruleList = F.ListAlloc(variableList.size());
        for (int j = 1; j < variableList.size(); j++) {
          ruleList.append(F.Rule(variableList.get(j), F.num(point[j - 1])));
        }
        return F.List(F.num(value), ruleList);
      }
      return F.NIL;
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDALL);
  }
}
