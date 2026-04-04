package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
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
import org.hipparchus.optim.nonlinear.scalar.GradientMultivariateOptimizer;
import org.hipparchus.optim.nonlinear.scalar.MultiStartMultivariateOptimizer;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunction;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunctionGradient;
import org.hipparchus.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer;
import org.hipparchus.optim.nonlinear.scalar.gradient.NonLinearConjugateGradientOptimizer.Formula;
import org.hipparchus.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.hipparchus.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.hipparchus.optim.nonlinear.scalar.noderiv.CMAESOptimizer.PopulationSize;
import org.hipparchus.optim.nonlinear.scalar.noderiv.CMAESOptimizer.Sigma;
import org.hipparchus.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.hipparchus.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.hipparchus.optim.nonlinear.vector.constrained.ConstraintOptimizer;
import org.hipparchus.optim.nonlinear.vector.constrained.LagrangeSolution;
import org.hipparchus.optim.nonlinear.vector.constrained.LinearEqualityConstraint;
import org.hipparchus.optim.nonlinear.vector.constrained.LinearInequalityConstraint;
import org.hipparchus.optim.nonlinear.vector.constrained.SQPOptimizerS2;
import org.hipparchus.random.GaussianRandomGenerator;
import org.hipparchus.random.JDKRandomGenerator;
import org.hipparchus.random.RandomDataGenerator;
import org.hipparchus.random.RandomVectorGenerator;
import org.hipparchus.random.UncorrelatedRandomVectorGenerator;
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
 * "https://github.com/Hipparchus-Math/hipparchus/blob/master/hipparchus-optim/src/main/java/org/hipparchus/optim/nonlinear/scalar/noderiv/BOBYQAOptimizer.java">Covariance
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
      relationList = F.List(function, relationList.subList(2).apply(S.And));
    } else if (relationList.argSize() > 1 && !relationList.arg2().isAnd()) {
      relationList = F.List(function, relationList.subList(2).apply(S.And));
    }
    IExpr arg2 = ast.arg2();
    if (!arg2.isList()) {
      arg2 = engine.evaluate(arg2);
    }
    VariablesSet vars = new VariablesSet(arg2);
    if (vars.size() == 0) {
      return F.NIL;
    }
    String method = POWELL_METHOD;
    int maxIterations = 100;
    if (options[0].isInteger()) {
      // determine option S.MaxIterations
      maxIterations = options[0].toIntDefault();
      if (F.isNotPresent(maxIterations)) {
        return F.NIL;
      }
      if (maxIterations < 0) {
        maxIterations = 100;
      }
    }
    if (!options[1].equals(S.Automatic)) {
      if (options[1].isSymbol() || options[1].isString()) {
        // determine option S.Method
        method = options[1].toString();
      }
    } else {
      if (ast.size() >= 4) {
        if (ast.arg3().isSymbol()) {
          method = ast.arg3().toString();
        }
      }
    }
    SimpleBounds simpleBounds = null;
    OptimizationData[] optimizationData = new OptimizationData[0];
    IASTAppendable varsList = vars.getVarList();
    if (relationList.argSize() > 1) {
      IASTAppendable reduceRelations = ((IAST) relationList.arg2()).copyAppendable();
      if (reduceRelations.argSize() > 0 && reduceRelations.isSameHeadSizeGE(S.And, 2)) {
        if (method.equalsIgnoreCase(SEQUENTIAL_QUADRATIC_METHOD)) {
          optimizationData = new OptimizationData[2];
          if (!createLinearConstraints(reduceRelations, varsList, engine, optimizationData)) {
            // Constraints in `1` are not all 'equality' or 'less equal' or 'greater equal'
            // constraints. Constraints with Unequal(!=) are not supported.
            return Errors.printMessage(ast.topHead(), "eqgele", F.List(reduceRelations), engine);
          }
        } else {
          simpleBounds = createSimpleBounds(reduceRelations, varsList, engine);
          if (simpleBounds != null //
              && !method.equalsIgnoreCase(BOBYQA_METHOD) //
              && !method.equalsIgnoreCase(CMAES_METHOD) //
              && !method.equalsIgnoreCase(CONJUGATEGRADIENT_METHOD)) {
            method = CMAES_METHOD;
          }
        }
      }
    }
    if (arg2.isList() && arg2.argSize() >= 2) {
      return optimizeGoal(goalType, function, (IAST) arg2, maxIterations, method, simpleBounds,
          optimizationData, engine);
    }
    return F.NIL;
  }


  private static boolean createLinearConstraints(IAST andAST, IASTAppendable varsList,
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
    double[] coefficients = new double[varsList.argSize()];
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
    double[] coefficients = new double[varsList.argSize()];
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
      IASTAppendable varsList, EvalEngine engine) {
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
          if (value != null) {
            int offset = getVariableOffset(varsList, variable);
            if (value[1] == S.Less) {
              upperBounds[offset] = value[0].evalf();
              double ulp = Math.ulp(upperBounds[offset]);
              if (ulp > 0.0) {
                upperBounds[offset] -= 2 * ulp;
              }
            } else if (value[1] == S.LessEqual) {
              upperBounds[offset] = value[0].evalf();
            } else if (value[1] == S.Greater) {
              lowerBounds[offset] = value[0].evalf();
              double ulp = Math.ulp(lowerBounds[offset]);
              if (ulp > 0.0) {
                lowerBounds[offset] += 2 * ulp;
              }
            } else if (value[1] == S.GreaterEqual) {
              lowerBounds[offset] = value[0].evalf();
            }
            reducedAndAST.remove(j);
            continue;
          }
        } else if (relationAST.argSize() == 3 && relationAST.arg2().equals(variable)) {
          int offset = getVariableOffset(varsList, variable);
          if (relationHead == S.Less) {
            lowerBounds[offset] = relationAST.arg1().evalf();
            upperBounds[offset] = relationAST.arg3().evalf();
            double ulp = Math.ulp(lowerBounds[offset]);
            if (ulp > 0.0) {
              lowerBounds[offset] += 2 * ulp;
            }
            ulp = Math.ulp(upperBounds[offset]);
            if (ulp > 0.0) {
              upperBounds[offset] -= 2 * ulp;
            }
          } else if (relationHead == S.LessEqual) {
            lowerBounds[offset] = relationAST.arg1().evalf();
            upperBounds[offset] = relationAST.arg3().evalf();

          } else if (relationHead == S.Greater) {
            lowerBounds[offset] = relationAST.arg3().evalf();
            upperBounds[offset] = relationAST.arg1().evalf();
            double ulp = Math.ulp(lowerBounds[offset]);
            if (ulp > 0.0) {
              lowerBounds[offset] += 2 * ulp;
            }
            ulp = Math.ulp(upperBounds[offset]);
            if (ulp > 0.0) {
              upperBounds[offset] -= 2 * ulp;
            }
          } else if (relationHead == S.GreaterEqual) {
            lowerBounds[offset] = relationAST.arg3().evalf();
            upperBounds[offset] = relationAST.arg1().evalf();
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
  private static int getVariableOffset(IASTAppendable varsList, IExpr variable) {
    for (int k = 1; k < varsList.size(); k++) {
      if (variable.equals(varsList.get(k))) {
        return k - 1;
      }
    }
    return -1;
  }

  private static IExpr optimizeGoal(GoalType goalType, IExpr function, IAST list, int maxIterations,
      String method, SimpleBounds simpleBounds, OptimizationData[] optimizationData,
      EvalEngine engine) {
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
        method = POWELL_METHOD;
      }
      OptimizeSupplier optimizeSupplier = new OptimizeSupplier(goalType, function, variableList,
          initialValues, maxIterations, method, simpleBounds, optimizationData, engine);
      return optimizeSupplier.get();
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
    final int maxIterations;
    final GoalType goalType;
    final IExpr originalFunction;
    final IAST variableList;
    final double[] initialValues;
    final SimpleBounds simpleBounds;
    OptimizationData[] optimizationData;
    String method;
    final EvalEngine engine;

    public OptimizeSupplier(GoalType goalType, IExpr function, IAST variableList,
        double[] initialValues, int maxIterations, String method, SimpleBounds simpleBounds,
        OptimizationData[] optimizationData, EvalEngine engine) {
      this.goalType = goalType;
      this.originalFunction = function;
      this.variableList = variableList;
      this.initialValues = initialValues;
      this.maxIterations = maxIterations;
      this.method = method;
      this.simpleBounds = simpleBounds;
      if (optimizationData == null || optimizationData.length < 2) {
        this.optimizationData = new OptimizationData[2];
      } else {
        this.optimizationData = optimizationData;
      }
      this.engine = engine;
    }

    @Override
    public IExpr get() {
      PointValuePair optimum = null;
      InitialGuess initialGuess = new InitialGuess(initialValues);
      IExpr function = engine.evaluate(originalFunction);
      if (method.equalsIgnoreCase(SEQUENTIAL_QUADRATIC_METHOD)) {
        try {
          // https://github.com/Hipparchus-Math/hipparchus/pull/404
          ConstraintOptimizer optim = new SQPOptimizerS2();
          TwiceDifferentiableMultiVariateNumerical twiceDifferentiableFunction =
              new TwiceDifferentiableMultiVariateNumerical(function, variableList, true);
          // x > 0, y > 0
          // LinearInequalityConstraint ineqc = new LinearInequalityConstraint(
          // new double[][] {{1.0, 0.0}, {0.0, 1.0}}, new double[] {0.0, 0.0});
          LagrangeSolution lagrangeSolution = optim.optimize( //
              new MaxEval(maxIterations), //
              new ObjectiveFunction(twiceDifferentiableFunction), //
              goalType, //
              initialGuess, //
              optimizationData[0], //
              optimizationData[1]);
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
          // rex.printStackTrace();
          method = POWELL_METHOD;
        }
      }
      MultiVariateNumerical multiVariateNumerical =
          new MultiVariateNumerical(function, variableList);
      if (method.equalsIgnoreCase(BOBYQA_METHOD) && initialValues.length < 2) {
        method = CMAES_METHOD;
      }
      if (method.equalsIgnoreCase(CMAES_METHOD)) {
        final CMAESOptimizer optim = new CMAESOptimizer(30000, // Max Evaluations
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
            new MaxEval(10000), //
            new ObjectiveFunction(multiVariateNumerical), //
            populationSize, //
            sigma, //
            goalType, //
            initialGuess, //
            simpleBounds, //
            new NelderMeadSimplex(initialValues.length));
      } else if (method.equalsIgnoreCase(BOBYQA_METHOD)) {
        BOBYQAOptimizer optim = new BOBYQAOptimizer(5);
        optimum = optim.optimize(//
            new MaxEval(10000), //
            new MaxIter(maxIterations), //
            new ObjectiveFunction(multiVariateNumerical), //
            goalType, //
            initialGuess, //
            simpleBounds, //
            new NelderMeadSimplex(initialValues.length));
      } else if (method.equalsIgnoreCase(POWELL_METHOD)) {
        final PowellOptimizer optim = new PowellOptimizer(1e-10, Math.ulp(1d), 1e-10, Math.ulp(1d));
        optimum = optim.optimize( //
            new MaxEval(maxIterations), //
            new ObjectiveFunction(multiVariateNumerical), //
            goalType, //
            initialGuess);
      } else if (method.equalsIgnoreCase(CONJUGATEGRADIENT_METHOD)) {

        MultiVariateVectorGradient multiVariateVectorGradient =
            new MultiVariateVectorGradient(function, variableList, true);
        try {
          Formula formula = NonLinearConjugateGradientOptimizer.Formula.POLAK_RIBIERE;
          optimum = conjugateGradient(multiVariateVectorGradient, multiVariateNumerical, formula,
              initialGuess);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          Formula formula = NonLinearConjugateGradientOptimizer.Formula.FLETCHER_REEVES;
          optimum = conjugateGradient(multiVariateVectorGradient, multiVariateNumerical, formula,
              initialGuess);
        }
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

    private PointValuePair conjugateGradient(MultiVariateVectorGradient multiVariateVectorGradient,
        MultiVariateNumerical multiVariateNumerical, Formula formula, InitialGuess initialGuess) {
      PointValuePair optimum;
      GradientMultivariateOptimizer underlying =
          new NonLinearConjugateGradientOptimizer(formula, new SimpleValueChecker(1e-10, 1e-10));
      JDKRandomGenerator g = new JDKRandomGenerator();
      g.setSeed(753289573253l);
      double[] mean = new double[initialValues.length];
      for (int i = 0; i < mean.length; i++) {
        mean[i] = 0.0;
      }
      double[] standardDeviation =
          calculateMultiStartStandardDeviations(initialValues.length, simpleBounds, initialValues);
      RandomVectorGenerator generator = new UncorrelatedRandomVectorGenerator(mean,
          standardDeviation, new GaussianRandomGenerator(g));
      int nbStarts = 10;
      MultiStartMultivariateOptimizer optimizer =
          new MultiStartMultivariateOptimizer(underlying, nbStarts, generator);

      optimum = optimizer.optimize(//
          new MaxEval(maxIterations), //
          new ObjectiveFunction(multiVariateNumerical), //
          new ObjectiveFunctionGradient(multiVariateVectorGradient), //
          goalType, //
          initialGuess);
      return optimum;
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

  /**
   * Calculates standard deviations for MultiStart RandomVectorGenerator based on bounds and an
   * initial guess point.
   *
   * @param dimension Problem dimension.
   * @param bounds SimpleBounds (can be null or have infinite values).
   * @param initialGuessPoint The central initial guess for the MultiStartOptimizer.
   * @param rangeFraction Fraction of finite bound range to use (e.g., 0.25 for 1/4).
   * @param guessFraction Fraction of initial guess magnitude to use (e.g., 1.0).
   * @param defaultStdDev Default value if bounds are infinite and guess is zero.
   * @param minStdDev Minimum allowed standard deviation.
   * @return Array of standard deviations.
   */
  private static double[] calculateMultiStartStandardDeviations(int dimension, SimpleBounds bounds,
      double[] initialGuessPoint, double rangeFraction, double guessFraction, double defaultStdDev,
      double minStdDev) {
    if (initialGuessPoint.length != dimension) {
      throw new MathIllegalArgumentException(
          org.hipparchus.exception.LocalizedCoreFormats.DIMENSIONS_MISMATCH,
          initialGuessPoint.length, dimension);
    }

    double[] stdDevs = new double[dimension];
    double[] lower = (bounds == null) ? null : bounds.getLower();
    double[] upper = (bounds == null) ? null : bounds.getUpper();

    for (int i = 0; i < dimension; i++) {
      double L = (lower == null || lower.length <= i) ? Double.NEGATIVE_INFINITY : lower[i];
      double U = (upper == null || upper.length <= i) ? Double.POSITIVE_INFINITY : upper[i];
      double guess = initialGuessPoint[i];

      boolean isLowerFinite = Double.isFinite(L);
      boolean isUpperFinite = Double.isFinite(U);

      if (isLowerFinite && isUpperFinite) {
        // Strategy 1: Use finite bounds range
        double range = U - L;
        if (range > minStdDev) { // Avoid issues if U == L
          stdDevs[i] = range * rangeFraction;
        } else {
          // If range is tiny or zero, maybe use guess or default?
          // Using guessFraction * abs(guess) or default might be better
          // Here, we just use minStdDev for simplicity if range is too small.
          stdDevs[i] = (guess == 0.0) ? Math.max(minStdDev, defaultStdDev)
              : Math.max(minStdDev, Math.abs(guess) * guessFraction);

          // If the variable is essentially fixed (L == U), a very small std dev is appropriate
          if (range == 0.0) {
            stdDevs[i] = minStdDev;
          }
        }

      } else {
        // Strategy 2/3: Bounds are infinite, use initial guess or default
        if (guess == 0.0) {
          // Strategy 3: Guess is zero, use default
          stdDevs[i] = defaultStdDev;
        } else {
          // Strategy 2: Use guess magnitude
          stdDevs[i] = Math.abs(guess) * guessFraction;
        }
      }

      // Ensure minimum standard deviation
      stdDevs[i] = Math.max(minStdDev, stdDevs[i]);
      // Ensure positivity just in case
      if (stdDevs[i] <= 0) {
        stdDevs[i] = minStdDev; // Should be covered by max, but belt-and-suspenders
      }
    }
    return stdDevs;
  }

  /**
   * Simplified version with default heuristic parameters. Uses range/4, abs(guess)*1.0, default
   * 1.0, min 1e-6.
   *
   * @param dimension Problem dimension.
   * @param bounds SimpleBounds (can be null or have infinite values).
   * @param initialGuessPoint The central initial guess for the MultiStartOptimizer.
   * @return Array of standard deviations.
   */
  private static double[] calculateMultiStartStandardDeviations(int dimension, SimpleBounds bounds,
      double[] initialGuessPoint) {
    return calculateMultiStartStandardDeviations(dimension, bounds, initialGuessPoint, 0.25, // rangeFraction
        // (1/4)
        1.0, // guessFraction (1.0)
        1.0, // defaultStdDev
        1e-6); // minStdDev
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
