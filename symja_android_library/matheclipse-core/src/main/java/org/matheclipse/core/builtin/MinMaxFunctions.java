package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hipparchus.analysis.MultivariateFunction;
import org.hipparchus.optim.InitialGuess;
import org.hipparchus.optim.LocalizedOptimFormats;
import org.hipparchus.optim.MaxEval;
import org.hipparchus.optim.OptimizationData;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.SimpleBounds;
import org.hipparchus.optim.linear.LinearConstraint;
import org.hipparchus.optim.linear.LinearConstraintSet;
import org.hipparchus.optim.linear.LinearObjectiveFunction;
import org.hipparchus.optim.linear.NonNegativeConstraint;
import org.hipparchus.optim.linear.PivotSelectionRule;
import org.hipparchus.optim.linear.SimplexSolver;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.hipparchus.optim.nonlinear.scalar.MultivariateOptimizer;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunction;
import org.hipparchus.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer;
import org.hipparchus.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.hipparchus.optim.univariate.BrentOptimizer;
import org.hipparchus.optim.univariate.SearchInterval;
import org.hipparchus.optim.univariate.UnivariateObjectiveFunction;
import org.hipparchus.optim.univariate.UnivariatePointValuePair;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Expr2LP;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ArgumentTypeStopException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ExprAnalyzer;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalDataSym;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.MultiVariateNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.numerics.optim.DifferentialEvolution;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.Maximize;
import org.matheclipse.core.reflection.system.Minimize;
import org.matheclipse.core.sympy.calculus.Util;

/**
 * The MinMaxFunctions class is a part of the symbolic math library and is used for mathematical
 * optimization. It contains several nested classes, each representing a different mathematical
 * function or operation. The class contains a static initializer block that sets evaluators for
 * various mathematical functions.
 */
public class MinMaxFunctions {
  /**
   *
   *
   * <pre>
   * <code>ArgMax(function, variable)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a maximizer point for a univariate <code>function</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Arg_max">Wikipedia - Arg max</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; ArgMax(x*10-x^2, x)
   * 5
   * </code>
   * </pre>
   */
  private static class ArgMax extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr x = ast.arg2();
      if (x.isSymbol() || (x.isAST() && !x.isList())) {
        IExpr result = Maximize.maximize(ast.topHead(), ast.arg1(), x, engine);
        if (result.isList() && result.last().isList()) {
          IAST subList = (IAST) result.last();
          if (subList.last().isRule()) {
            return subList.last().second();
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }
  }

  /**
   *
   *
   * <pre>
   * <code>ArgMin(function, variable)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a minimizer point for a univariate <code>function</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Arg_max">Wikipedia - Arg max</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; ArgMin(x*10+x^2, x)
   * -5
   * </code>
   * </pre>
   */
  private static class ArgMin extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr x = ast.arg2();
      if (x.isSymbol() || (x.isAST() && !x.isList())) {
        IExpr result = Minimize.minimize(ast.topHead(), ast.arg1(), x, engine);
        if (result.isList() && result.last().isList()) {
          IAST subList = (IAST) result.last();
          if (subList.last().isRule()) {
            return subList.last().second();
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }
  }

  private static class FunctionContinuous extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      IExpr domain = ast.isAST3() ? ast.arg3() : S.Reals;

      IExpr function = arg1;
      IExpr constraints = S.True;

      // Handle the syntax: FunctionContinuous[{f, cons}, x]
      // We differentiate {f1, f2} (list of functions) from {f, cons} (function and constraint)
      // by checking if the second element looks like a constraint (inequality/logical).
      if (arg1.isList() && arg1.argSize() == 2) {
        IExpr maybeCons = arg1.second();
        if (isLikelyConstraint(maybeCons)) {
          function = arg1.first();
          constraints = maybeCons;
        }
      }

      IAST variables = arg2.isList() ? (IAST) arg2 : F.List(arg2);

      // 1. Calculate the set of discontinuities for the function(s)
      // We delegate this to the registered FunctionDiscontinuities evaluator
      IAST discontQuery = F.ternaryAST3(S.FunctionDiscontinuities, function, variables, domain);
      IExpr discontinuities = engine.evaluate(discontQuery);

      if (discontinuities.isFalse()) {
        // No discontinuities found -> Function is continuous
        return S.True;
      }
      if (discontinuities.isTrue()) {
        // Discontinuous everywhere or indeterminate
        return S.False;
      }

      // 2. Check if the discontinuities exist within the valid constraints
      // We form the logical expression: Discontinuities && Constraints
      IExpr problem = constraints.isTrue() ? discontinuities : F.And(discontinuities, constraints);

      // 3. Use Resolve to check satisfiability
      // If we find an instance, it means a discontinuity exists in the domain -> Continuous =
      // False.
      // If we find no instances ({}), it means no discontinuity in domain -> Continuous = True.
      IAST exists = F.Exists(variables, problem);
      IAST resolve = F.binaryAST2(S.Resolve, exists, domain);
      IExpr result = engine.evaluate(resolve);

      if (result.isTrue()) {
        return S.False;
      }
      if (result.isFalse()) {
        return S.True;
      }

      // If Resolve returns unevaluated, we cannot decide.
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    /**
     * Helper to determine if an expression is likely a constraint (logical or relational).
     */
    private boolean isLikelyConstraint(IExpr expr) {
      if (expr.isAST()) {
        IExpr head = expr.head();
        if (head.isBuiltInSymbol()) {
          switch (((IBuiltInSymbol) head).ordinal()) {
            case ID.Equal:
            case ID.Unequal:
            case ID.Less:
            case ID.LessEqual:
            case ID.Greater:
            case ID.GreaterEqual:
            case ID.And:
            case ID.Or:
            case ID.Not:
            case ID.Nand:
            case ID.Nor:
            case ID.Xor:
            case ID.Implies:
            case ID.Element:
              return true;
            default:
              return false;
          }
        }
      }
      return false;
    }
  }

  private static class FunctionDiscontinuities extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr function = ast.arg1();
      IAST variables = ast.arg2().makeList();
      IExpr domain = ast.isAST3() ? ast.arg3() : S.Reals;

      Set<IExpr> discontinuities = new LinkedHashSet<>();
      if (!collectDiscontinuities(function, variables, discontinuities, domain)) {
        // Warning: the set of discontinuities may be incomplete due to missing domain and
        // discontinuity information for some of the functions involved.
        Errors.printMessage(S.FunctionDiscontinuities, "unkds", F.CEmptyList, engine);
      }

      if (discontinuities.isEmpty()) {
        return S.False;
      }

      if (discontinuities.size() == 1) {
        return discontinuities.iterator().next();
      }

      IASTAppendable or = F.Or();
      or.appendAll(discontinuities);
      return or;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    /**
     * Recursively collects discontinuity conditions.
     *
     * @param expr The expression to analyze.
     * @param variables The variables of interest.
     * @param conditions The set to populate with conditions.
     * @param domain The domain (Reals or Complexes).
     */
    private boolean collectDiscontinuities(IExpr expr, IAST variables, Set<IExpr> conditions,
        IExpr domain) {
      // Check if expression depends on any variable
      if (expr.isFree(x -> variables.contains(x), false) || variables.contains(expr)) {
        return true;
      }

      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        IExpr head = ast.head();

        // Recurse check arguments first
        boolean noWarning = true;
        for (IExpr arg : ast) {
          if (!collectDiscontinuities(arg, variables, conditions, domain)) {
            noWarning = false;
          }
        }

        if (head.isBuiltInSymbol() && ast.argSize() >= 1) {
          IExpr arg1 = ast.arg1();
          switch (((IBuiltInSymbol) head).ordinal()) {
            case ID.Plus:
            case ID.Times:
            case ID.Abs:
            case ID.Min:
            case ID.Max:
            case ID.Sin:
            case ID.Cos:
            case ID.Sinh:
            case ID.Cosh:
            case ID.Tanh:
            case ID.Sinc:
            case ID.ArcSinh:
            case ID.Erf:
            case ID.Erfc:
              // continuous on the whole domain; the arguments were checked above
              return noWarning;
            case ID.Floor:
            case ID.Ceiling:
              conditions.add(F.Equal(F.Sin(F.Times(S.Pi, arg1)), F.C0));
              return noWarning;
            case ID.Round:
              IExpr arg2 = F.C1;
              if (ast.isAST2()) {
                arg2 = ast.arg2();
              }
              conditions
                  .add(F.Equal(F.Sin(F.Times(S.Pi, F.Plus(F.C1D2, F.Divide(arg1, arg2)))), F.C0));
              return noWarning;
            case ID.Sign:
            case ID.UnitStep:
              // Discontinuous at zero
              conditions.add(F.Equal(arg1, F.C0));
              return noWarning;
            case ID.Mod:
              // Mod(x, m) is discontinuous when x/m is an integer
              if (ast.argSize() == 2) {
                IExpr x = arg1;
                IExpr y = ast.arg2();
                conditions.add(F.Or(F.Equal(y, F.C0),
                    F.Equal(F.Sin(F.Times(F.Pi, x, F.Power(y, F.CN1))), F.C0)));
                return noWarning;
              }
              return false;
            case ID.Tan:
            case ID.Sec:
              // Poles when Cos(x) == 0
              conditions.add(F.Equal(F.Cos(arg1), F.C0));
              return noWarning;
            case ID.Cot:
            case ID.Csc:
              // Poles when Sin(x) == 0
              conditions.add(F.Equal(F.Sin(arg1), F.C0));
              return noWarning;
            case ID.Log:
              if (domain == S.Complexes) {
                // Branch cut (-Infinity, 0] => Im(arg) == 0 && Re(arg) <= 0
                IAST andCondition = F.And(F.Equal(F.Im(arg1), F.C0), F.LessEqual(F.Re(arg1), F.C0));
                IAST orCondition = F.Or(F.Equal(arg1, F.C0), andCondition);
                conditions.add(orCondition);
              } else {
                conditions.add(F.LessEqual(arg1, F.C0));
              }
              return noWarning;
            case ID.Power:
              IExpr base = ast.base();
              IExpr exponent = ast.exponent();
              if (exponent.isInteger()) {
                if (exponent.isNegative()) {
                  conditions.add(F.Equal(base, F.C0));
                }
              } else {
                // Non-integer exponent
                if (domain == S.Complexes) {
                  // Branch cut (-inf, 0]
                  conditions.add(F.And(F.Equal(F.Im(base), F.C0), F.LessEqual(F.Re(base), F.C0)));
                } else {
                  // For Reals x^y (y non-integer) -> x <= 0
                  conditions.add(F.LessEqual(base, F.C0));
                }
              }
              return noWarning;
            case ID.Beta:
              // Beta(x,y) = Gamma(x)Gamma(y)/Gamma(x+y)
              // Discontinuities at x <= 0 integers or y <= 0 integers
              for (IExpr arg : ast) {
                conditions.add(
                    F.And(F.Equal(F.Sin(F.Times(S.Pi, arg)), F.C0), F.LessEqual(F.Re(arg), F.C0)));
              }
              return noWarning;
            case ID.Gamma:
            case ID.LogGamma:
            case ID.Factorial:
              // Gamma is singular at non-positive integers.
              conditions
                  .add(F.And(F.Equal(F.Sin(F.Times(S.Pi, arg1)), F.C0), F.LessEqual(arg1, F.C0)));
              return noWarning;
            case ID.Piecewise:
              // Piecewise({{val, cond}, ...})
              // Discontinuities include the boundaries of the conditions
              if (arg1.isList()) {
                IAST list = (IAST) arg1;
                for (IExpr pArg : list) {
                  if (pArg.isList()) {
                    IExpr cond = ((IAST) pArg).second();
                    extractBoundaries(cond, variables, conditions);
                  }
                }
                return noWarning;
              }
              return false;
            default:
              break;
          }
        }
      }
      return false;
    }

    /**
     * Helper to extract equality boundaries from inequalities (e.g. x < 0 -> x == 0).
     */
    private void extractBoundaries(IExpr cond, IAST variables, Set<IExpr> conditions) {
      if (cond.isFree(variables)) {
        return;
      }

      if (cond.isAST()) {
        IAST ast = (IAST) cond;
        IExpr head = ast.head();
        if (head.isBuiltInSymbol()) {
          if (ast.isNot()) {
            extractBoundaries(ast.arg1(), variables, conditions);
            return;
          }
          if (ast.argSize() >= 2) {
            switch (((IBuiltInSymbol) head).ordinal()) {
              case ID.Less:
              case ID.LessEqual:
              case ID.Greater:
              case ID.GreaterEqual:
              case ID.Equal:
              case ID.Unequal:
                // Simple heuristic: LHS == RHS
                if (ast.argSize() == 2) {
                  conditions.add(F.Equal(ast.arg1(), ast.arg2()));
                }
                break;
              case ID.And:
              case ID.Or:
                for (IExpr arg : ast) {
                  extractBoundaries(arg, variables, conditions);
                }
                break;
              default:
                break;
            }
          }
        }
      }
    }
  }

  private static final class FunctionDomain extends AbstractFunctionEvaluator {
    private static final class ComplexesDomain {
      IAST variables;

      public ComplexesDomain(IAST variables) {
        this.variables = variables;
      }

      /**
       * Recursively determine the domain of a function over the complexes as a logical expression.
       *
       * @param expr the expression to find the domain for.
       * @param variables the variables in the expression.
       * @param engine the evaluation engine.
       * @return a logical expression representing the domain, or {@link F#NIL} if no constraints
       *         are found.
       */
      private IExpr complexesDomain(IExpr expr) {
        if (expr.isFree(x -> variables.contains(x), false)) {
          return F.NIL;
        }

        if (expr.isAST()) {
          IAST ast = (IAST) expr;
          IASTAppendable andConditions = F.And();

          // Recurse on arguments
          for (IExpr arg : ast) {
            IExpr argDomain = complexesDomain(arg);
            if (argDomain.isPresent()) {
              andConditions.append(argDomain);
            }
          }

          // Add constraints from the head of the expression
          if (ast.isPower()) {
            IExpr base = ast.arg1();
            IExpr exponent = ast.arg2();
            // base!=0||Re(exponent)>0
            andConditions.append(F.Or(F.Unequal(base, F.C0), F.Greater(F.Re(exponent), F.C0)));
          } else if (ast.isTimes()) {
            Optional<IExpr[]> parts = AlgebraUtil.fractionalParts(ast, false);
            if (parts.isPresent()) {
              IExpr denominator = parts.get()[1];
              if (!denominator.isOne()) {
                andConditions.append(F.Unequal(denominator, F.C0));
              }
            }
          } else {
            int headID = ast.headID();
            if (headID >= 0) {
              int argSize = ast.argSize();
              if (argSize == 1) {
                arg1ComplexesDomain(ast, andConditions, headID);
              }
            }
          }

          if (andConditions.isAST0()) {
            return S.True;
          }
          if (andConditions.isAST1()) {
            return andConditions.arg1();
          }
          return andConditions;
        }

        return F.NIL;
      }

      private void arg1ComplexesDomain(IAST ast, IASTAppendable andConditions, int headID) {
        IExpr z = ast.arg1();
        switch (headID) {

          case ID.Cos:
          case ID.Sin:
            break;
          case ID.Cot:
          case ID.Csc:
            // arg != k * Pi
            andConditions.append(F.NotElement(F.Times(z, F.Power(S.Pi, F.CN1)), S.Integers));
            break;
          case ID.Coth:
          case ID.Csch:
            // -I*z / Pi is not an integer
            andConditions.append(F.NotElement(F.Times(F.CNI, z, F.Power(S.Pi, F.CN1)), S.Integers));
            break;
          case ID.Tan:
          case ID.Sec:
            // arg != (k + 1/2) * Pi
            andConditions
                .append(F.NotElement(F.Plus(F.C1D2, F.Times(z, F.Power(S.Pi, F.CN1))), S.Integers));
            break;
          case ID.Tanh:
            // 1/2+(-I*z)/Pi∉Integers
            andConditions.append(
                F.NotElement(F.Plus(F.C1D2, F.Times(F.CNI, F.Power(F.Pi, F.CN1), z)), F.Integers));
            break;
          case ID.ArcTan:
          case ID.ArcCot:
            andConditions.append(F.Unequal(z, F.CI));
            andConditions.append(F.Unequal(z, F.CNI));
            break;
          case ID.ArcTanh:
          case ID.ArcCoth:
            andConditions.append(F.Unequal(z, F.C1));
            andConditions.append(F.Unequal(z, F.CN1));
            break;
          case ID.ArcCsch:
          case ID.ArcSech:
          case ID.Log:
            andConditions.append(F.Unequal(z, F.C0));
            break;
          case ID.Gamma:
            // z is not a non-positive integer
            andConditions.append(F.Or(F.Greater(F.Re(z), F.C0), F.NotElement(z, S.Integers)));
            break;
          default:
        }
      }
    }

    private static final class RealsDomain {
      IAST variables;
      EvalEngine engine;

      public RealsDomain(IAST variables, EvalEngine engine) {
        this.variables = variables;
        this.engine = engine;
      }

      /**
       * Recursively determine the domain of a function as a logical expression.
       *
       * @param expr the expression to find the domain for.
       * @param variables the variables in the expression.
       * @param engine the evaluation engine.
       * @return a logical expression representing the domain, or {@link F#NIL} if no constraints
       *         are found.
       */
      private IExpr realsDomain(IExpr expr) {
        if (expr.isFree(x -> variables.contains(x), false)) {
          return F.NIL;
        }

        if (expr.isAST()) {
          IAST ast = (IAST) expr;
          IASTAppendable andConditions = F.And();

          // Recurse on arguments
          for (IExpr arg : ast) {
            IExpr argDomain = realsDomain(arg);
            if (argDomain.isPresent()) {
              andConditions.append(argDomain);
            }
          }

          // Add constraints from the head of the expression
          if (ast.isPower()) {
            IExpr x = ast.arg1();
            IExpr n = ast.arg2();
            boolean evaled = false;
            if (!n.isFree(variables)) {
              // Case f(x)^g(x) -> requires f(x) > 0
              if (!x.isFree(variables)) {
                andConditions.append(F.Greater(x, F.C0));
                evaled = true;
              }
            } else {
              if (n.isIntegerResult()) {
                if (n.isNegativeResult()) {
                  IExpr denominator = x;
                  if (variables.argSize() == 1) {
                    IExpr rootsCondition = roots(denominator, variables.arg1(), engine);
                    if (rootsCondition.isPresent()) {
                      andConditions.append(rootsCondition);
                      evaled = true;
                    }
                  } else {
                    // Case f(x)^-n -> requires f(x) != 0
                    andConditions.append(F.Unequal(x, F.C0));
                    evaled = true;
                  }
                }
              } else if (n.isFraction()) {
                // Case f(x)^(p/q)
                IFraction frac = (IFraction) n;
                if (frac.denominator().isEven()) {
                  // requires f(x) >= 0
                  andConditions.append(F.GreaterEqual(x, F.C0));
                  evaled = true;
                }
              } else {

                // Case f(x)^y, y is non-integer constant -> requires f(x) >= 0
                andConditions.append(F.GreaterEqual(x, F.C0));
                evaled = true;
              }
            }
            if (!evaled) {
              // (n∈Integers&&x!=0)||(n∈Integers&&n>=1)||(x>=0&&n>0)||x>0
              andConditions.append(F.Or(F.And(F.Element(n, F.Integers), F.Unequal(x, F.C0)),
                  F.And(F.Element(n, F.Integers), F.GreaterEqual(n, F.C1)),
                  F.And(F.GreaterEqual(x, F.C0), F.Greater(n, F.C0)), F.Greater(x, F.C0)));
            }
          } else {
            int headID = ast.headID();
            if (headID >= 0) {
              int argSize = ast.argSize();
              if (argSize == 1) {
                arg1RealsDomain(ast, andConditions, headID);
              } else if (argSize == 2) {

              }
            }
          }

          if (andConditions.isAST0()) {
            return S.True;
          }
          if (andConditions.isAST1()) {
            return andConditions.arg1();
          }
          return andConditions;
        }

        return F.NIL;
      }

      private void arg1RealsDomain(IAST ast, IASTAppendable andConditions, int headID) {
        IExpr x = ast.arg1();
        IAST ineq;
        switch (headID) {
          case ID.ArcCot:
          case ID.ArcTan:
          case ID.ArcSinh:
          case ID.Cos:
          case ID.Cosh:
          case ID.Sin:
          case ID.Sinh:
          case ID.Sech:
          case ID.Tanh:
            break;
          case ID.ArcCos:
          case ID.ArcSin:
            // ineq = F.LessEqual(F.CN1, x, F.C1);
            ineq = F.And(F.GreaterEqual(x, F.CN1), F.LessEqual(x, F.C1));
            andConditions.append(reduceRelation(ineq).orElse(ineq));
            break;
          case ID.ArcCsc:
          case ID.ArcSec:
            ineq = F.Or(F.LessEqual(x, F.CN1), F.GreaterEqual(x, F.C1));
            andConditions.append(reduceRelation(ineq).orElse(ineq));
            break;
          case ID.ArcCosh:
            ineq = F.GreaterEqual(x, F.C1);
            andConditions.append(reduceRelation(ineq).orElse(ineq));
            break;
          case ID.ArcCoth:
            ineq = F.Or(F.Less(x, F.CN1), F.Greater(x, F.C1));
            andConditions.append(reduceRelation(ineq).orElse(ineq));
            break;
          case ID.ArcCsch:
            ineq = F.Or(F.Less(x, F.C0), F.Greater(x, F.C0));
            andConditions.append(reduceRelation(ineq).orElse(ineq));
            break;
          case ID.ArcSech:
            ineq = F.And(F.Greater(x, F.C0), F.LessEqual(x, F.C1));
            andConditions.append(reduceRelation(ineq).orElse(ineq));
            break;
          case ID.ArcTanh:
            ineq = F.And(F.Greater(x, F.CN1), F.Less(x, F.C1));
            andConditions.append(reduceRelation(ineq).orElse(ineq));
            break;
          case ID.Cot:
            // x != k * Pi
            andConditions.append(F.NotElement(F.Times(x, F.Power(S.Pi, F.CN1)), S.Integers));
            break;
          case ID.Coth:
          case ID.Csch:
            // x != 0
            ineq = F.Greater(F.Or(F.Greater(x, F.C0), F.Less(x, F.C0)), F.C0);
            andConditions.append(reduceRelation(ineq).orElse(ineq));
            break;
          case ID.Csc:
            // x != k * Pi
            andConditions.append(F.NotElement(F.Times(x, F.Power(S.Pi, F.CN1)), S.Integers));
            break;
          case ID.Gamma:
            // x>0||x∉Integers
            andConditions.append(F.Or(F.Greater(x, F.C0), F.NotElement(x, F.Integers)));
            break;
          case ID.Log:
            ineq = F.Greater(x, F.C0);
            andConditions.append(reduceRelation(ineq).orElse(ineq));
            break;
          case ID.Sec:
            // x != (k + 1/2) * Pi
            andConditions
                .append(F.NotElement(F.Plus(F.C1D2, F.Times(x, F.Power(S.Pi, F.CN1))), S.Integers));
            break;
          case ID.Tan:
            // x != (k + 1/2) * Pi
            andConditions
                .append(F.NotElement(F.Plus(F.C1D2, F.Times(x, F.Power(S.Pi, F.CN1))), S.Integers));
            break;
          default:
            break;
        }
      }

      private IExpr reduceRelation(IAST inequation) {
        VariablesSet vset = new VariablesSet(inequation);
        IAST reduceVariables = vset.reduceVariables(variables);
        if (reduceVariables.argSize() == 1) {
          final IExpr variable = reduceVariables.arg1();
          IExpr intervalData = IntervalDataSym.toIntervalData(inequation, variable, engine, false);
          if (intervalData.isPresent()) {
            IExpr interval = engine.evaluate(intervalData);
            if (interval.isIntervalData() && interval.argSize() > 0) {
              IExpr intervalToOr = IntervalDataSym.intervalToOr((IAST) interval, variable);
              if (intervalToOr.isPresent()) {
                return intervalToOr;
              }
            }
          }
        }
        return F.NIL;
      }

    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr function = ast.arg1();
      // IExpr vars = ast.arg2();

      final IAST variables =
          Validate.checkIsVariableOrVariableList(ast, 2, S.FunctionDomain, engine);
      if (variables.isNIL()) {
        return F.NIL;
      }
      //
      // if (!vars.isList()) {
      // if (!vars.isVariable()) {
      // return F.NIL;
      // }
      // variables = vars.makeList();
      // } else {
      // variables = (IAST) vars;
      // }

      IBuiltInSymbol domain = S.Reals;
      try {
        VariablesSet vset = new VariablesSet(variables);
        if (vset.isEmpty()) {
          return S.True;
        }
        if (function.isFree(v -> variables.contains(v), false)) {
          // constant w.r.t. the variables -> defined everywhere
          return S.True;
        }

        if (ast.isAST3()) {
          if (ast.arg3() == S.Complexes) {
            domain = S.Complexes;
          } else if (ast.arg3() == S.Complexes) {
            domain = S.Reals;
          } else {
            domain = null;
          }
        }

        if (domain != null && function.isNumericFunction(vset)) {
          IExpr result = F.NIL;
          if (domain == S.Complexes) {
            ComplexesDomain cd = new ComplexesDomain(variables);
            result = cd.complexesDomain(function);
            if (result.isPresent()) {
              return engine.evaluate(result);
            }
          } else {
            RealsDomain rd = new RealsDomain(variables, engine);
            result = rd.realsDomain(function);
            if (result.isPresent()) {
              result = engine.evaluate(result);
              if (variables.argSize() == 1) {
                result = IntervalDataSym
                    .normalizeExpr(result.makeAST(S.And), variables.arg1(), engine).orElse(result);
              }
              return chainBoundedIntervals(result);
            }
          }

        }

      } catch (ArgumentTypeStopException atse) {
        if (Config.SHOW_STACKTRACE) {
          atse.printStackTrace();
        }
        // Unable to find the domain with the available methods.
        return Errors.printMessage(S.FunctionDomain, "nmet", F.CEmptyList, engine);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    private static IExpr roots(IExpr denominator, IExpr x, EvalEngine engine)
        throws ArgumentTypeStopException {
      IExpr roots = RootsFunctions.roots(denominator, false, x.makeList(), engine);
      if (roots.isNonEmptyList()) {
        IAST list = (IAST) roots;
        IASTAppendable condition = F.ast(S.And, list.argSize());
        for (int i = 1; i < list.size(); i++) {
          IExpr arg = list.get(i);
          if (arg.isRealResult()) {
            // x<7||x>7
            condition.append(F.Or(F.Less(x, arg), F.Greater(x, arg)));
          }
        }
        if (condition.size() > 1) {
          if (condition.argSize() == 1) {
            return condition.arg1();
          }
          return condition;
        }
        return S.True;
      }

      throw new ArgumentTypeStopException("Roots failed");
    }

    /**
     * Rewrite each two-condition bounded interval {@code x>a && x<b} (and its closed / mixed
     * variants) into the chained inequality {@code a<x<b}. Recurses into the arguments of a
     * top-level {@code Or}; {@code And}s with more than two conditions (e.g. a {@code NotElement}
     * constraint combined with bounds) are left untouched.
     *
     * @param expr the (already normalized) domain expression
     * @return {@code expr} with bounded pairs chained
     */
    private static IExpr chainBoundedIntervals(IExpr expr) {
      // Use isAST(S.Or) rather than isOr(): normalizeExpr can hand back a single-argument
      // Or(And(...)) (which isOr() rejects) that would otherwise flatten back to And-form.
      if (expr.isAST(S.Or)) {
        IAST or = (IAST) expr;
        IASTMutable result = or.copy();
        boolean changed = false;
        for (int i = 1; i < or.size(); i++) {
          IExpr chained = chainBoundedAnd(or.get(i));
          if (chained.isPresent()) {
            result.set(i, chained);
            changed = true;
          }
        }
        return changed ? result : expr;
      }
      return chainBoundedAnd(expr).orElse(expr);
    }

    /**
     * If {@code expr} is a two-argument {@code And} of a lower and an upper bound on the same
     * variable, return the equivalent chained inequality; otherwise {@link F#NIL}.
     */
    private static IExpr chainBoundedAnd(IExpr expr) {
      if (!expr.isAnd() || expr.size() != 3) {
        return F.NIL;
      }
      IAST and = (IAST) expr;
      // And is Orderless, so the lower/upper condition may appear in either position.
      IExpr chained = chainBoundedPair(and.arg1(), and.arg2());
      if (chained.isNIL()) {
        chained = chainBoundedPair(and.arg2(), and.arg1());
      }
      return chained;
    }

    /**
     * Combine a lower bound ({@code Greater}/{@code GreaterEqual(x, a)}) and an upper bound
     * ({@code Less}/{@code LessEqual(x, b)}) on the same symbol {@code x} into the chained form
     * {@code a<x<b} (homogeneous) or an {@code Inequality[...]} (mixed open/closed). Returns
     * {@link F#NIL} when the pair is not a valid lower/upper bound on a single variable.
     */
    private static IExpr chainBoundedPair(IExpr lower, IExpr upper) {
      if (!lower.isAST2() || !upper.isAST2()) {
        return F.NIL;
      }
      boolean lowerStrict;
      if (lower.head() == S.Greater) {
        lowerStrict = true;
      } else if (lower.head() == S.GreaterEqual) {
        lowerStrict = false;
      } else {
        return F.NIL;
      }
      boolean upperStrict;
      if (upper.head() == S.Less) {
        upperStrict = true;
      } else if (upper.head() == S.LessEqual) {
        upperStrict = false;
      } else {
        return F.NIL;
      }
      IExpr variable = lower.first();
      if (!variable.isSymbol() || !variable.equals(upper.first())) {
        return F.NIL;
      }
      IExpr lo = lower.second();
      IExpr hi = upper.second();
      if (!lo.isFree(variable) || !hi.isFree(variable)) {
        return F.NIL;
      }
      if (lowerStrict == upperStrict) {
        return lowerStrict ? F.Less(lo, variable, hi) : F.LessEqual(lo, variable, hi);
      }
      return F.Inequality(lo, lowerStrict ? S.Less : S.LessEqual, variable,
          upperStrict ? S.Less : S.LessEqual, hi);
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }


  }


  private static final class FunctionPeriod extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr function = ast.arg1();
      IExpr arg2 = ast.arg2();
      // TODO implement different domains
      // ISymbol domain = S.Reals;
      // if (ast.argSize() >= 3) {
      // if (!domain.equals(ast.arg3())) {
      //
      // }
      // }
      IAST variables = arg2.makeList();
      if (variables.argSize() != 1 || !variables.arg1().isSymbol()) {
        return F.NIL;
      }
      return Util.periodicity(function, (ISymbol) variables.arg1());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

  }


  private static class FunctionSingularities extends AbstractFunctionEvaluator {

    public FunctionSingularities() {}

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr function = ast.arg1();
      IExpr vars = ast.arg2();
      IExpr domain = S.Reals; // Default domain

      if (ast.isAST3()) {
        domain = ast.arg3();
      }

      IAST variables;
      if (vars.isList()) {
        variables = (IAST) vars;
      } else if (vars.isSymbol()) {
        variables = F.List(vars);
      } else {
        return F.NIL;
      }

      Set<IExpr> singularities = new LinkedHashSet<>();
      collectSingularities(function, variables, singularities, domain);

      if (singularities.isEmpty()) {
        return S.False;
      }

      if (singularities.size() == 1) {
        return singularities.iterator().next();
      }

      IASTAppendable or = F.Or();
      or.appendAll(singularities);
      return or;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    /**
     * Recursively collects singularity conditions.
     *
     * @param expr The expression to analyze.
     * @param variables The variables of interest.
     * @param conditions The set to populate with conditions (e.g., x == 0).
     */
    private void collectSingularities(IExpr expr, IAST variables, Set<IExpr> conditions,
        IExpr domain) {
      // Check if expression depends on any variable
      if (expr.isFree(x -> variables.contains(x), false)) {
        return;
      }

      if (expr.isAST()) {
        IAST ast = (IAST) expr;
        ISymbol head = ast.topHead();

        // Recurse check arguments first
        for (IExpr arg : ast) {
          collectSingularities(arg, variables, conditions, domain);
        }

        if (domain == S.Complexes) {
          if (ast.isPower()) {
            // Base^Exponent
            IExpr base = ast.base();
            IExpr exponent = ast.exponent();
            if (exponent.isInteger()) {
              if (exponent.isNegative()) {
                // Pole at base == 0
                conditions.add(F.Equal(base, F.C0));
              }
            } else {
              // Branch point at base == 0
              conditions.add(F.Equal(base, F.C0));
              // Branch cut (-inf, 0] => Im(base) == 0 && Re(base) <= 0
              conditions.add(F.And(F.Equal(F.Im(base), F.C0), F.LessEqual(F.Re(base), F.C0)));
            }
          }
          if (ast.argSize() == 1) {
            if (head == S.Log) {
              // Branch point at arg == 0
              conditions.add(F.Equal(ast.arg1(), F.C0));
              // Branch cut (-inf, 0] => Im(arg) == 0 && Re(arg) <= 0
              conditions
                  .add(F.And(F.Equal(F.Im(ast.arg1()), F.C0), F.LessEqual(F.Re(ast.arg1()), F.C0)));
            } else if (head == S.Tan || head == S.Sec) {
              // Poles when Cos(x) == 0
              conditions.add(F.Equal(F.Cos(ast.arg1()), F.C0));
            } else if (head == S.Cot || head == S.Csc) {
              // Poles when Sin(x) == 0
              conditions.add(F.Equal(F.Sin(ast.arg1()), F.C0));
            } else if (head == S.ArcSin || head == S.ArcCos) {
              // Branch points at z == 1, z == -1
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Branch cuts: (-inf, -1] U [1, inf)
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.LessEqual(F.Re(z), F.CN1)));
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.GreaterEqual(F.Re(z), F.C1)));
            } else if (head == S.ArcTan) {
              // Branch points at z == I, z == -I
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Plus(F.CI, z), F.C0));
              conditions.add(F.Equal(F.Subtract(F.CI, z), F.C0)); // -I + z == 0
              // Branch cuts: [I, I*inf) U (-I*inf, -I]
              conditions.add(F.And(F.Equal(F.Re(z), F.C0), F.GreaterEqual(F.Im(z), F.C1)));
              conditions.add(F.And(F.Equal(F.Re(z), F.C0), F.LessEqual(F.Im(z), F.CN1)));
            } else if (head == S.ArcCot) {
              // Branch points at z == I, z == -I
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Plus(F.CI, z), F.C0));
              conditions.add(F.Equal(F.Subtract(F.CI, z), F.C0));
              // Branch cut: [-I, I]
              conditions.add(F.And(F.Equal(F.Re(z), F.C0), F.GreaterEqual(F.Im(z), F.CN1),
                  F.LessEqual(F.Im(z), F.C1)));
            } else if (head == S.ArcSec || head == S.ArcCsc) {
              // Branch points z == +/- 1 and z == 0
              IExpr z = ast.arg1();
              conditions.add(F.Equal(z, F.C0));
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Branch cut: [-1, 1]
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.GreaterEqual(F.Re(z), F.CN1),
                  F.LessEqual(F.Re(z), F.C1)));
            } else if (head == S.ArcCosh) {
              // Branch points z == 1, z == -1
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Branch cut: (-inf, 1]
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.LessEqual(F.Re(z), F.C1)));
            } else if (head == S.ArcTanh) {
              // Branch points z == 1, z == -1
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Branch cuts: (-inf, -1] U [1, inf)
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.LessEqual(F.Re(z), F.CN1)));
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.GreaterEqual(F.Re(z), F.C1)));
            } else if (head == S.ArcCoth) {
              // Branch points z == 1, z == -1
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Branch cut: [-1, 1]
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.GreaterEqual(F.Re(z), F.CN1),
                  F.LessEqual(F.Re(z), F.C1)));
            } else if (head == S.ArcSech) {
              // ArcSech(z) = ArcCosh(1/z).
              IExpr z = ast.arg1();
              conditions.add(F.Equal(z, F.C0));
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Cuts: (-inf, 0] U [1, inf)
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.LessEqual(F.Re(z), F.C0)));
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.GreaterEqual(F.Re(z), F.C1)));
            } else if (head == S.ArcCsch) {
              // ArcCsch(z) = ArcSinh(1/z).
              IExpr z = ast.arg1();
              conditions.add(F.Equal(z, F.C0));
              conditions.add(F.Equal(F.Plus(F.CI, z), F.C0));
              conditions.add(F.Equal(F.Subtract(F.CI, z), F.C0));
              // Cut: [-I, I]
              conditions.add(F.And(F.Equal(F.Re(z), F.C0), F.GreaterEqual(F.Im(z), F.CN1),
                  F.LessEqual(F.Im(z), F.C1)));
            }
          }
        } else {
          // Logic for Reals (simplified)
          if (ast.isPower()) {
            IExpr base = ast.base();
            IExpr exponent = ast.exponent();
            if (exponent.isNegative()) {
              conditions.add(F.Equal(base, F.C0));
            } else if (!exponent.isInteger()) {
              conditions.add(F.Equal(base, F.C0));
            }
          }

          if (ast.argSize() == 1) {
            if (head == S.Log) {
              conditions.add(F.Equal(ast.arg1(), F.C0));
            } else if (head == S.Tan || head == S.Sec) {
              conditions.add(F.Equal(F.Cos(ast.arg1()), F.C0));
            } else if (head == S.Cot || head == S.Csc) {
              conditions.add(F.Equal(F.Sin(ast.arg1()), F.C0));
            } else if (head == S.ArcSec || head == S.ArcCsc || head == S.ArcSech
                || head == S.ArcCsch) {
              conditions.add(F.Equal(ast.arg1(), F.C0));
            }
          }
        }

        if (ast.argSize() == 1) {
          if (head == S.Gamma || head == S.LogGamma || head == S.Factorial) {
            // Gamma is singular at non-positive integers.
            IExpr arg = ast.arg1();
            conditions.add(F.And(F.Equal(F.Sin(F.Times(S.Pi, arg)), F.C0), F.LessEqual(arg, F.C0)));
          }
        }
        int[] piecewise = ast.isPiecewise();
        if (piecewise != null) {
          // Piecewise({{val, cond}, ...})
          // Singularities include the boundaries of the conditions
          IAST list = (IAST) ast.arg1();
          for (IExpr arg : list) {
            if (arg.isList()) {
              IExpr cond = ((IAST) arg).second();
              extractBoundaries(cond, variables, conditions);
            }
          }
        }
      }
    }

    /**
     * Helper to extract equality boundaries from inequalities (e.g. x < 0 -> x == 0).
     */
    private void extractBoundaries(IExpr cond, IAST variables, Set<IExpr> conditions) {
      if (cond.isFree(variables)) {
        return;
      }

      if (cond.isAST()) {
        IAST ast = (IAST) cond;
        ISymbol head = ast.topHead();
        if (head == S.Less || head == S.LessEqual || head == S.Greater || head == S.GreaterEqual
            || head == S.Equal || head == S.Unequal) {

          // Simple heuristic: LHS == RHS
          if (ast.size() == 3) {
            conditions.add(F.Equal(ast.arg1(), ast.arg2()));
          }
        } else if (head == S.And || head == S.Or || head == S.Not) {
          for (IExpr arg : ast) {
            extractBoundaries(arg, variables, conditions);
          }
        }
      }
    }
  }

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    /**
     * The init method sets the evaluators for various mathematical functions.
     */
    private static void init() {
      S.ArgMax.setEvaluator(new ArgMax());
      S.ArgMin.setEvaluator(new ArgMin());
      S.FunctionContinuous.setEvaluator(new FunctionContinuous());
      S.FunctionDiscontinuities.setEvaluator(new FunctionDiscontinuities());
      S.FunctionDomain.setEvaluator(new FunctionDomain());
      S.FunctionPeriod.setEvaluator(new FunctionPeriod());
      S.FunctionSingularities.setEvaluator(new FunctionSingularities());
      S.NMaximize.setEvaluator(new NMaximize());
      S.NMinimize.setEvaluator(new NMinimize());

      S.NArgMax.setEvaluator(new NArgMax());
      S.NArgMin.setEvaluator(new NArgMin());
      S.NMaxValue.setEvaluator(new NMaxValue());
      S.NMinValue.setEvaluator(new NMinValue());
    }
  }


  /**
   * The NArgMax function is used to find the values of the variables that maximize the given
   * function.
   */
  private static class NArgMax extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr maximize = engine.evaluate(F.NMaximize(ast.arg1(), ast.arg2()));
      if (maximize.isList2() && maximize.second().isListOfRules()) {
        IAST listOfRules = (IAST) maximize.second();
        return listOfRules.map(x -> x.second());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }


  /**
   * The NArgMin function is used to find the values of the variables that minimize the given
   * function.
   */
  private static class NArgMin extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr minimize = engine.evaluate(F.NMinimize(ast.arg1(), ast.arg2()));
      if (minimize.isList2() && minimize.second().isListOfRules()) {
        IAST listOfRules = (IAST) minimize.second();
        return listOfRules.map(x -> x.second());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }


  /**
   *
   *
   * <pre>
   * NMaximize(maximize_function, constraints, variables_list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * the <code>NMaximize</code> function provides an implementation of
   * <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's simplex algorithm</a>
   * for solving linear optimization problems with linear equality and inequality constraints. The
   * variables are free; a non-negative variable needs its constraint.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Linear_programming">Wikipedia - Linear
   * programming</a>
   * </ul>
   *
   * <p>
   * See also: <a href="LinearProgramming.md">LinearProgramming</a>,
   * <a href="NMinimize.md">NMinimize</a>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; NMaximize({-2*x+y-5, x+2*y&lt;=6 &amp;&amp; 3*x + 2*y &lt;= 12 }, {x, y})
   * {-2.0,{x-&gt;0.0,y-&gt;3.0}}
   * </pre>
   *
   * <p>
   * solves the linear problem:
   *
   * <pre>
   * Maximize -2x + y - 5
   * </pre>
   *
   * <p>
   * with the constraints:
   *
   * <pre>
   * x  + 2y &lt;=  6
   * 3x + 2y &lt;= 12
   * x &gt;= 0
   * y &gt;= 0
   * </pre>
   */
  private static final class NMaximize extends NMinimize {

    @Override
    protected GoalType getGoalType() {
      // override the MINIMIZE goal type from super class NMinimize
      return GoalType.MAXIMIZE;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }


  /**
   * The NMaxValue function is used to find the maximum value for the given function.
   */
  private static class NMaxValue extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr maximize = engine.evaluate(F.NMaximize(ast.arg1(), ast.arg2()));
      if (maximize.isList2() && maximize.second().isListOfRules()) {
        return maximize.first();
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }


  /**
   *
   *
   * <pre>
   * NMinimize(coefficientsOfLinearObjectiveFunction, constraintList, constraintRelationList)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * the <code>NMinimize</code> function provides an implementation of
   * <a href="http://en.wikipedia.org/wiki/Simplex_algorithm">George Dantzig's simplex algorithm</a>
   * for solving linear optimization problems with linear equality and inequality constraints. The
   * variables are free; a non-negative variable needs its constraint.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Linear_programming">Wikipedia - Linear
   * programming</a>
   * </ul>
   *
   * <p>
   * See also: <a href="LinearProgramming.md">LinearProgramming</a>,
   * <a href="NMaximize.md">NMaximize</a>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; NMinimize({-2*x+y-5, x+2*y&lt;=6 &amp;&amp; 3*x + 2*y &lt;= 12}, {x, y})
   * {-13.0,{x-&gt;4.0,y-&gt;0.0}
   * </pre>
   *
   * <p>
   * solves the linear problem:
   *
   * <pre>
   * Minimize -2x + y - 5
   * </pre>
   *
   * <p>
   * with the constraints:
   *
   * <pre>
   * x  + 2y &lt;=  6
   * 3x + 2y &lt;= 12
   * x &gt;= 0
   * y &gt;= 0
   * </pre>
   */
  private static class NMinimize extends AbstractFunctionEvaluator {

    protected static List<LinearConstraint> getConstraints(VariablesSet vars,
        IAST listOfconstraints) {
      List<LinearConstraint> constraints =
          new ArrayList<LinearConstraint>(listOfconstraints.size());
      listOfconstraints.forEach(x -> {
        Expr2LP x2LP = new Expr2LP(x, vars);
        constraints.add(x2LP.expr2Constraint());
      });
      return constraints;
    }

    protected static LinearObjectiveFunction getObjectiveFunction(VariablesSet vars,
        IExpr objectiveFunction) {
      Expr2LP x2LP = new Expr2LP(objectiveFunction, vars);
      return x2LP.expr2ObjectiveFunction();
    }

    /**
     * @param func function to optimize.
     * @param variables
     * @param init Starting point.
     * @param goal minimization or maximization.
     * @param tolerance tolerance (relative error on the objective function) for "Powell" algorithm.
     * @param lineTolerance tolerance (relative error on the objective function) for the internal
     *        line search algorithm.
     * @param pointTolerance Tolerance for checking that the optimum is correct.
     */
    private static IExpr optimizePowell( //
        MultivariateFunction func, //
        VariablesSet variables, //
        double[] init, //
        GoalType goal, //
        double tolerance, //
        double lineTolerance, //
        double pointTolerance) { //
      final MultivariateOptimizer optim =
          new PowellOptimizer(tolerance, Math.ulp(1d), lineTolerance, Math.ulp(1d));

      final PointValuePair solution = optim.optimize(//
          new MaxEval(100000), //
          new ObjectiveFunction(func), //
          goal, //
          new InitialGuess(init) //
      );
      // final double[] point = solution.getPoint();
      // System.out.println("sol=" + Arrays.toString(solution.getPoint()));

      double[] values = solution.getPointRef();
      List<IExpr> varList = variables.getArrayList();
      IASTAppendable list =
          F.mapRange(0, varList.size(), i -> F.Rule(varList.get(i), F.num(values[i])));
      IAST result = F.list(F.num(func.value(values)), list);
      return result;
    }

    protected static IAST simplexSolver(VariablesSet variables,
        LinearObjectiveFunction objectiveFunction, OptimizationData... optimizationData)
        throws org.hipparchus.exception.MathRuntimeException {
      SimplexSolver solver = new SimplexSolver();
      PointValuePair solution = solver.optimize(optimizationData);
      double[] values = solution.getPointRef();
      List<IExpr> varList = variables.getArrayList();
      IASTAppendable list =
          F.mapRange(0, values.length, i -> F.Rule(varList.get(i), F.num(values[i])));
      return F.list(F.num(objectiveFunction.value(values)), list);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // switch to numeric calculation
      return numericEval(ast, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }

    protected GoalType getGoalType() {
      return GoalType.MINIMIZE;
    }

    /** The number of relaxations a search for integer values may solve. */
    private static final int MAX_BRANCH_AND_BOUND_NODES = 500;

    /** The number of local searches which are started from the best points of a scanned box. */
    private static final int MAX_BOX_STARTS = 8;

    /** How far a constraint may be violated by a numerical solution. */
    private static final double FEASIBILITY_TOLERANCE = 1e-6;

    /** The state of one <code>NMinimize</code> call. */
    private static final class Search {
      final IAST listOfVariables;
      final VariablesSet vars;
      final EvalEngine engine;
      /** a problem was found to have no point which satisfies its constraints */
      boolean infeasible = false;
      int nodes = 0;

      Search(IAST listOfVariables, VariablesSet vars, EvalEngine engine) {
        this.listOfVariables = listOfVariables;
        this.vars = vars;
        this.engine = engine;
      }
    }

    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      for (int i = 3; i < ast.size(); i++) {
        // NMinimize(f, vars, Method -> ..., MaxIterations -> ...): the options are accepted, the
        // method is chosen from the problem
        if (!ast.get(i).isRuleAST()) {
          // Options expected (instead of `1`) beyond position `2` in `3`. An option must be a rule
          // or a list of rules.
          return Errors.printMessage(ast.topHead(), "nonopt", F.List(ast.get(i), F.C2, ast),
              engine);
        }
      }
      try {
        IAST list1 = ast.arg1().makeList();
        IAST listOfVariables = ast.arg2().makeList();
        VariablesSet vars = new VariablesSet(listOfVariables);
        if (list1.argSize() > 0 && vars.size() > 0) {
          IExpr function = list1.first();
          // {f, c1, c2, ...} and {f, c1 && c2 && ...} are the same problem
          IExpr constraints = S.True;
          if (list1.argSize() == 2) {
            constraints = list1.arg2();
          } else if (list1.argSize() > 2) {
            constraints = list1.rest().setAtCopy(0, S.And);
          }
          Search search = new Search(listOfVariables, vars, engine);
          List<IExpr> integerVariables = new ArrayList<IExpr>();
          constraints = withoutDomains(constraints, integerVariables, engine);

          // c1 || c2: the best of the optima of the alternatives
          // an Or at the top keeps its order: of two equal optima the first alternative's is the
          // answer, NMinimize({x^2, x<=-2 || x>=2}, x) is {4.,{x->-2.}}
          IExpr alternatives = constraints.isFree(S.Or) || constraints.isOr() ? constraints
              : engine.evaluate(F.unaryAST1(S.LogicalExpand, constraints));
          IAST branches = alternatives.isOr() ? (IAST) alternatives : F.List(alternatives);
          IExpr best = F.NIL;
          for (int i = 1; i < branches.size(); i++) {
            IExpr branch = branches.get(i);
            if (branch.isFalse()) {
              search.infeasible = true;
              continue;
            }
            IExpr result = integerVariables.isEmpty() //
                ? optimum(function, branch, search)
                : branchAndBound(function, branch, integerVariables, search);
            best = better(best, result);
          }
          if (best.isNIL() && search.infeasible) {
            // There are no points that satisfy the constraints `1`.
            Errors.printMessage(ast.topHead(), "nsol",
                F.List(constraints.isAnd() ? ((IAST) constraints).setAtCopy(0, S.List)
                    : F.List(constraints)),
                engine);
            IASTAppendable rules = F.mapRange(1, listOfVariables.size(),
                i -> F.Rule(listOfVariables.get(i), S.Indeterminate));
            return F.list(getGoalType() == GoalType.MINIMIZE ? F.CInfinity : F.CNInfinity, rules);
          }
          return best;
        }
      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (org.hipparchus.exception.MathRuntimeException e) {
        return Errors.printMessage(ast.topHead(), e, engine);
      }
      return F.NIL;
    }

    /** The better one of two results <code>{value, rules}</code>; {@link F#NIL} is no result. */
    private IExpr better(IExpr first, IExpr second) {
      if (first.isNIL()) {
        return second;
      }
      if (second.isNIL()) {
        return first;
      }
      double a = first.first().evalf();
      double b = second.first().evalf();
      return (getGoalType() == GoalType.MINIMIZE ? b < a : b > a) ? second : first;
    }

    /**
     * Take the domain conditions <code>Element(x, Integers)</code> and
     * <code>Element({x,y}, Integers)</code> out of the constraints; <code>Element(x, Reals)</code>
     * says nothing new.
     *
     * @param integerVariables gets the variables which have to be integers
     * @return the remaining constraints, <code>True</code> if there are none
     */
    private static IExpr withoutDomains(IExpr constraints, List<IExpr> integerVariables,
        EvalEngine engine) {
      if (constraints.isFree(S.Element)) {
        return constraints;
      }
      IAST conjuncts = constraints.isAnd() ? (IAST) constraints : F.And(constraints);
      IASTAppendable rest = F.ast(S.And, conjuncts.argSize());
      for (int i = 1; i < conjuncts.size(); i++) {
        IExpr conjunct = conjuncts.get(i);
        if (conjunct.isAST(S.Element, 3)
            && (conjunct.second() == S.Integers || conjunct.second() == S.Reals)) {
          if (conjunct.second() == S.Integers) {
            IAST variables = conjunct.first().makeList();
            for (int j = 1; j < variables.size(); j++) {
              if (!integerVariables.contains(variables.get(j))) {
                integerVariables.add(variables.get(j));
              }
            }
          }
          continue;
        }
        rest.append(conjunct);
      }
      return rest.argSize() == 0 ? S.True : rest.oneIdentity1();
    }

    /**
     * The optimum of <code>function</code> under the conjunction <code>constraints</code>:
     * <ul>
     * <li>no constraints: a Powell search from the origin,
     * <li>a linear objective and linear constraints: the simplex algorithm,
     * <li>every variable in a finite interval: local searches from the best points of a scan of the
     * box, so that the answer is the global optimum and not the local one next to the origin,
     * <li>else the local constrained search from a few start points.
     * </ul>
     *
     * @return {@link F#NIL} if no point which satisfies the constraints was found
     */
    private IExpr optimum(IExpr function, IExpr constraints, Search search) {
      if (constraints.isTrue()) {
        return unconstrainedOptimum(function, search);
      }
      ExprAnalyzer exprAnalyzer = new ExprAnalyzer(function, search.listOfVariables, search.engine);
      if (exprAnalyzer.simplifyAndAnalyze() == ExprAnalyzer.LINEAR) {
        try {
          IExpr linear = optimizeSimplexSolver(constraints, search.vars, function);
          if (linear.isPresent()) {
            return linear;
          }
        } catch (org.hipparchus.exception.MathIllegalStateException mise) {
          if (mise.getSpecifier() == LocalizedOptimFormats.NO_FEASIBLE_SOLUTION) {
            search.infeasible = true;
            return F.NIL;
          }
          throw mise;
        }
      }
      return constrainedOptimum(function, constraints, search);
    }

    /**
     * The optimum without constraints. The Powell search from the origin finds the local optimum
     * next to the origin: <code>NMinimize(x^4-3*x^2+x, x)</code> stopped at <code>-1.07</code>,
     * where the minimum is <code>-3.51</code> at <code>x == -1.3</code>. So a population search is
     * started around the origin as well, and a Powell search from its best point. Its result is
     * the answer only if it is better than the one from the origin: a problem which has one
     * optimum gets the result it always had.
     */
    private IExpr unconstrainedOptimum(IExpr function, Search search) {
      final MultivariateFunction func = new MultiVariateNumerical(function, search.listOfVariables);
      final int dimension = search.vars.size();
      final boolean minimize = getGoalType() == GoalType.MINIMIZE;
      final MultivariateFunction cost = minimize ? func : point -> -func.value(point);
      double[] init = new double[dimension];
      double[] best = null;
      double bestCost = Double.POSITIVE_INFINITY;
      org.hipparchus.exception.MathRuntimeException failure = null;
      try {
        best = powell(func, init);
        bestCost = cost.value(best);
        if (Double.isNaN(bestCost)) {
          best = null;
          bestCost = Double.POSITIVE_INFINITY;
        }
      } catch (org.hipparchus.exception.MathRuntimeException mre) {
        failure = mre;
      }
      final double[] local = best;
      final double localCost = bestCost;
      double[] lower = new double[dimension];
      double[] upper = new double[dimension];
      for (int k = 0; k < GLOBAL_START_REGIONS.length; k++) {
        final double region = GLOBAL_START_REGIONS[k];
        java.util.Arrays.fill(lower, -region);
        java.util.Arrays.fill(upper, region);
        // The population stays in the region; the Powell search from its best point is free to
        // leave it. Of equal optima the one next to the origin is wanted, as for the periodic
        // Sin(x)+Sin(10*x/3): a bias below the noise of the search tells them apart.
        final MultivariateFunction biased = point -> cost.value(point) + 1e-9 * norm(point) / region;
        DifferentialEvolution.Result global = DifferentialEvolution.minimize(biased, lower, upper,
            true, null, populationSize(dimension), GLOBAL_GENERATIONS, GLOBAL_SEED + k);
        if (global == null) {
          continue;
        }
        double[] candidate = global.point;
        double candidateCost = cost.value(candidate);
        try {
          double[] polished = powell(func, candidate);
          double polishedCost = cost.value(polished);
          if (polishedCost <= candidateCost) {
            candidate = polished;
            candidateCost = polishedCost;
          }
        } catch (org.hipparchus.exception.MathRuntimeException mre) {
          // the point of the population search is the candidate
        }
        if (isBetter(candidateCost, bestCost)
            // of the equal optima of a periodic function the one next to the origin
            || (isBetter(bestCost, localCost) && !isBetter(bestCost, candidateCost)
                && 2.0 * norm(candidate) < norm(best))) {
          best = candidate;
          bestCost = candidateCost;
        }
      }
      if (best == null) {
        if (failure != null) {
          throw failure;
        }
        return F.NIL;
      }
      if (isUnbounded(cost, best, bestCost, cost.value(init))) {
        // The problem is unbounded.
        Errors.printMessage(minimize ? S.NMinimize : S.NMaximize, "ubnd", F.CEmptyList,
            search.engine);
        IAST variables = search.listOfVariables;
        return F.list(minimize ? F.CNInfinity : F.CInfinity,
            F.mapRange(1, variables.size(), i -> F.Rule(variables.get(i), S.Indeterminate)));
      }
      return F.list(F.num(func.value(best)), rules(search.vars.getArrayList(), best));
    }

    /** The half widths of the boxes in which the population searches without constraints start. */
    private static final double[] GLOBAL_START_REGIONS = {3.0, 30.0};

    private static final int GLOBAL_GENERATIONS = 300;

    /** The seed of the population searches: the same problem gets the same answer every time. */
    private static final long GLOBAL_SEED = 0x5DEECE66DL;

    private static int populationSize(int dimension) {
      return Math.min(Math.max(15 * dimension, 20), 150);
    }

    /**
     * <code>true</code> if the cost <code>candidate</code> is lower than <code>incumbent</code> by
     * more than the noise of two searches which end in the same optimum.
     */
    private static boolean isBetter(double candidate, double incumbent) {
      if (Double.isNaN(candidate)) {
        return false;
      }
      if (Double.isInfinite(incumbent)) {
        return candidate < incumbent;
      }
      return candidate < incumbent - 1e-8 * (1.0 + Math.abs(incumbent));
    }

    /**
     * <code>true</code> if the search ran away: its end point is far from the origin, and further
     * out on the ray through it the cost does not rise - as for <code>x^3-x</code>, which has no
     * lower bound, and for <code>Exp(x)</code>, which does not take its infimum.
     */
    private static boolean isUnbounded(MultivariateFunction cost, double[] point,
        double pointCost, double originCost) {
      if (pointCost == Double.NEGATIVE_INFINITY) {
        return true;
      }
      double norm = norm(point);
      if (norm < 0.99 * GLOBAL_START_REGIONS[GLOBAL_START_REGIONS.length - 1]) {
        // inside of the region of the population search
        return false;
      }
      if (norm > 1e8
          && pointCost < -1e15 * (1.0 + (Double.isFinite(originCost) ? Math.abs(originCost) : 0.0))) {
        // an oscillating objective whose amplitude grows without bound
        return true;
      }
      double previous = pointCost;
      double[] further = new double[point.length];
      for (double scale = 2.0; scale <= 1e7; scale *= scale < 8.0 ? 2.0 : 100.0) {
        for (int d = 0; d < point.length; d++) {
          further[d] = scale * point[d];
        }
        double value = cost.value(further);
        if (Double.isNaN(value) || value > previous) {
          return false;
        }
        previous = value;
      }
      return true;
    }

    private static double norm(double[] point) {
      double norm = 0.0;
      for (double x : point) {
        norm = Math.max(norm, Math.abs(x));
      }
      return norm;
    }

    /** The end point of a Powell search for the optimum of <code>func</code> from <code>init</code>. */
    private double[] powell(MultivariateFunction func, double[] init) {
      final MultivariateOptimizer optim =
          new PowellOptimizer(1e-9, Math.ulp(1d), 1e-9, Math.ulp(1d));
      return optim.optimize(new MaxEval(100000), new ObjectiveFunction(func), getGoalType(),
          new InitialGuess(init)).getPoint();
    }

    /**
     * The linear program, or {@link F#NIL} if a constraint isn't linear. The variables are free:
     * <code>NMinimize({x+y, x>=-1 && y>=-2}, {x,y})</code> is <code>-3</code>, not the
     * <code>0</code> a non-negativity constraint gave.
     */
    private IExpr optimizeSimplexSolver(IExpr constraintExpr, VariablesSet variables,
        IExpr function) {
      IExpr listOfconstraints = constraintExpr.makeAST(S.And);
      try {
        // lc1 && lc2 && lc3...
        LinearObjectiveFunction objectiveFunction = getObjectiveFunction(variables, function);
        List<LinearConstraint> constraints = getConstraints(variables, (IAST) listOfconstraints);
        return simplexSolver(variables, objectiveFunction, objectiveFunction,
            new LinearConstraintSet(constraints), getGoalType(), new NonNegativeConstraint(false),
            PivotSelectionRule.BLAND);
      } catch (ArgumentTypeException | ClassCastException | ArithmeticException ex) {
        // a constraint which isn't linear
        return F.NIL;
      }
    }

    /**
     * The constrained optimum found by the local constrained solver of <code>FindMinimum</code> /
     * <code>FindMaximum</code>. A result which violates the constraints is no answer: for
     * contradictory constraints the local solver stops at a point which satisfies none of them.
     */
    private IExpr constrainedOptimum(IExpr function, IExpr constraints, Search search) {
      final EvalEngine engine = search.engine;
      final List<IExpr> variables = search.vars.getArrayList();
      boolean[] onlyBounds = new boolean[1];
      double[][] box = box(constraints, variables, onlyBounds, engine);
      if (box != null) {
        return boxOptimum(function, constraints, box, onlyBounds[0], search);
      }
      boolean violated = false;
      final IExpr[] starts = {F.C0, F.C1, F.CN1};
      for (int i = 0; i < starts.length; i++) {
        IASTAppendable specs = F.ListAlloc(variables.size());
        for (IExpr variable : variables) {
          specs.append(F.list(variable, starts[i]));
        }
        // the other starts are fallbacks: their failures are quiet, the last one says why
        IExpr result = localSearch(function, constraints, specs, i < starts.length - 1, engine);
        if (result.isPresent()) {
          if (isFeasible(constraints, (IAST) result.second(), engine)) {
            return result;
          }
          violated = true;
        }
      }
      if (violated) {
        search.infeasible = true;
      }
      return F.NIL;
    }

    private IExpr localSearch(IExpr function, IExpr constraints, IAST specs, boolean quiet,
        EvalEngine engine) {
      final IAST problem = F.list(function, constraints);
      IAST search = F.binaryAST2(getGoalType() == GoalType.MINIMIZE ? S.FindMinimum : S.FindMaximum,
          problem, specs);
      IExpr result = quiet ? engine.evalQuiet(search) : engine.evaluate(search);
      if (result.isList2() && result.first().isReal() && result.second().isListOfRules()) {
        return result;
      }
      return F.NIL;
    }

    /**
     * The global optimum on a box: scan the box, and start the local constrained search from the
     * best points of the scan which satisfy the constraints.
     * <code>NMaximize({x*Cos(x), 0<=x<=16}, x)</code> is <code>12.6</code> at
     * <code>x == 12.6</code>, where the search from the origin stopped at the first local maximum
     * <code>0.56</code>.
     */
    private IExpr boxOptimum(IExpr function, IExpr constraints, double[][] box, boolean onlyBounds,
        Search search) {
      final EvalEngine engine = search.engine;
      final List<IExpr> variables = search.vars.getArrayList();
      final int dimension = variables.size();
      final boolean minimize = getGoalType() == GoalType.MINIMIZE;
      final MultivariateFunction func = new MultiVariateNumerical(function, search.listOfVariables);
      final int perDimension =
          dimension == 1 ? 1000 : dimension == 2 ? 61 : dimension == 3 ? 15 : 0;
      final int samples = perDimension == 0 ? 4000 : (int) Math.pow(perDimension, dimension);
      double[][] points = new double[samples][];
      double[] values = new double[samples];
      int count = 0;
      double[] point = new double[dimension];
      for (int k = 0; k < samples; k++) {
        int index = k;
        for (int d = 0; d < dimension; d++) {
          double fraction;
          if (perDimension == 0) {
            fraction = halton(k + 1, PRIMES[d % PRIMES.length]);
          } else {
            fraction = (index % perDimension + 0.5) / perDimension;
            index /= perDimension;
          }
          point[d] = box[d][0] + fraction * (box[d][1] - box[d][0]);
        }
        double value;
        try {
          value = func.value(point);
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          continue;
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
          continue;
        }
        points[count] = point.clone();
        values[count++] = minimize ? value : -value;
      }
      // the best points of the scan first; the starts are taken apart from each other, so that
      // they are not all in the one basin which has the best samples
      Integer[] order = new Integer[count];
      for (int i = 0; i < count; i++) {
        order[i] = i;
      }
      final double[] sortValues = values;
      java.util.Arrays.sort(order, (a, b) -> Double.compare(sortValues[a], sortValues[b]));
      final double separation = perDimension == 0 ? 0.1 : 2.5 / perDimension;
      double[][] bestPoints = new double[MAX_BOX_STARTS][];
      int found = 0;
      int tested = 0;
      for (int i = 0; i < count && found < MAX_BOX_STARTS && tested < 40 * MAX_BOX_STARTS; i++) {
        double[] candidate = points[order[i]];
        boolean apart = true;
        for (int j = 0; j < found && apart; j++) {
          double distance = 0.0;
          for (int d = 0; d < dimension; d++) {
            double width = box[d][1] - box[d][0];
            distance = Math.max(distance,
                width == 0.0 ? 0.0 : Math.abs(candidate[d] - bestPoints[j][d]) / width);
          }
          apart = distance > separation;
        }
        if (!apart) {
          continue;
        }
        tested++;
        if (isFeasible(constraints, rules(variables, candidate), engine)) {
          bestPoints[found++] = candidate;
        }
      }
      if (found == 0) {
        // no point of the scan satisfies the constraints: the middle of the box is the start
        bestPoints[0] = new double[dimension];
        for (int d = 0; d < dimension; d++) {
          bestPoints[0][d] = 0.5 * (box[d][0] + box[d][1]);
        }
        found = 1;
      }
      IExpr best = F.NIL;
      boolean violated = false;
      for (int k = 0; k < found; k++) {
        if (onlyBounds) {
          // the box is all there is: a deterministic local search inside of it, where the bounded
          // method of FindMinimum is a randomized one
          double[] local = boxLocalSearch(func, box, bestPoints[k], minimize,
              perDimension == 0 ? 0.05 : 1.0 / perDimension);
          if (local != null) {
            best = better(best, F.list(F.num(func.value(local)), rules(variables, local)));
            continue;
          }
        }
        IASTAppendable specs = F.ListAlloc(dimension);
        for (int d = 0; d < dimension; d++) {
          specs.append(F.list(variables.get(d), F.num(bestPoints[k][d])));
        }
        IExpr result = localSearch(function, constraints, specs, true, engine);
        if (result.isPresent()) {
          if (isFeasible(constraints, (IAST) result.second(), engine)) {
            best = better(best, result);
          } else {
            violated = true;
          }
        }
      }
      if (onlyBounds && dimension >= 2) {
        // The scan is thin in a box of many variables, and its local searches start in the best
        // cells only: of the 5 variables of a Schwefel function one stayed in the wrong basin. A
        // population search finds the basin; its result is the answer only if it is better.
        best = boxGlobalSearch(func, box, best, search);
      }
      if (best.isNIL() && violated) {
        search.infeasible = true;
      }
      return best;
    }

    private IExpr boxGlobalSearch(MultivariateFunction func, double[][] box, IExpr best,
        Search search) {
      final List<IExpr> variables = search.vars.getArrayList();
      final int dimension = box.length;
      final boolean minimize = getGoalType() == GoalType.MINIMIZE;
      final MultivariateFunction cost = minimize ? func : point -> -func.value(point);
      double[] lower = new double[dimension];
      double[] upper = new double[dimension];
      for (int d = 0; d < dimension; d++) {
        lower[d] = box[d][0];
        upper[d] = box[d][1];
        if (!(upper[d] > lower[d])) {
          return best;
        }
      }
      double bestCost = Double.POSITIVE_INFINITY;
      if (best.isPresent()) {
        double value = best.first().evalf();
        bestCost = minimize ? value : -value;
      }
      for (int k = 0; k < BOX_GLOBAL_RUNS; k++) {
        DifferentialEvolution.Result global = DifferentialEvolution.minimize(cost, lower, upper,
            true, null, populationSize(dimension), GLOBAL_GENERATIONS, GLOBAL_SEED + 16 + k);
        if (global == null) {
          continue;
        }
        double[] candidate = global.point;
        double candidateCost = global.value;
        double[] polished = boxLocalSearch(func, box, candidate, minimize, 0.01);
        if (polished != null) {
          double polishedCost = cost.value(polished);
          if (polishedCost <= candidateCost) {
            candidate = polished;
            candidateCost = polishedCost;
          }
        }
        if (isBetter(candidateCost, bestCost)) {
          bestCost = candidateCost;
          best = F.list(F.num(func.value(candidate)), rules(variables, candidate));
        }
      }
      return best;
    }

    /** The number of population searches in a box, each with random numbers of its own. */
    private static final int BOX_GLOBAL_RUNS = 2;

    /**
     * The local optimum of <code>func</code> in the box next to <code>start</code>: Brent's method
     * on the neighbouring cells of the scan for one variable, Powell's BOBYQA method for more.
     *
     * @param cell the size of a cell of the scan, as a fraction of the width of the box
     * @return <code>null</code> if the search fails
     */
    private static double[] boxLocalSearch(MultivariateFunction func, double[][] box,
        double[] start, boolean minimize, double cell) {
      final int dimension = start.length;
      final double[] width = new double[dimension];
      for (int d = 0; d < dimension; d++) {
        width[d] = box[d][1] - box[d][0];
        if (!(width[d] > 0.0)) {
          return null;
        }
      }
      try {
        if (dimension == 1) {
          double left = Math.max(box[0][0], start[0] - cell * width[0]);
          double right = Math.min(box[0][1], start[0] + cell * width[0]);
          UnivariatePointValuePair result =
              new BrentOptimizer(1e-12, 1e-14).optimize(new MaxEval(10000),
                  new UnivariateObjectiveFunction(t -> func.value(new double[] {t})),
                  minimize ? GoalType.MINIMIZE : GoalType.MAXIMIZE,
                  new SearchInterval(left, right, Math.min(right, Math.max(left, start[0]))));
          return new double[] {result.getPoint()};
        }
        // in the coordinates u of the unit cube, so that one trust region radius fits every
        // variable
        final MultivariateFunction scaled = u -> {
          double[] x = new double[dimension];
          for (int d = 0; d < dimension; d++) {
            x[d] = box[d][0] + Math.min(1.0, Math.max(0.0, u[d])) * width[d];
          }
          return func.value(x);
        };
        double[] u0 = new double[dimension];
        double[] zeros = new double[dimension];
        double[] ones = new double[dimension];
        for (int d = 0; d < dimension; d++) {
          u0[d] = (start[d] - box[d][0]) / width[d];
          ones[d] = 1.0;
        }
        PointValuePair result = new BOBYQAOptimizer(2 * dimension + 1, Math.min(0.25, cell), 1e-12)
            .optimize(new MaxEval(20000), new ObjectiveFunction(scaled),
                minimize ? GoalType.MINIMIZE : GoalType.MAXIMIZE, new InitialGuess(u0),
                new SimpleBounds(zeros, ones));
        double[] u = result.getPointRef();
        double[] x = new double[dimension];
        for (int d = 0; d < dimension; d++) {
          x[d] = box[d][0] + Math.min(1.0, Math.max(0.0, u[d])) * width[d];
        }
        return x;
      } catch (org.hipparchus.exception.MathRuntimeException mre) {
        return null;
      }
    }

    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

    /** The <code>index</code>-th member of the Halton sequence to the given base. */
    private static double halton(int index, int base) {
      double result = 0.0;
      double fraction = 1.0 / base;
      for (int i = index; i > 0; i /= base) {
        result += fraction * (i % base);
        fraction /= base;
      }
      return result;
    }

    private static IAST rules(List<IExpr> variables, double[] point) {
      return F.mapRange(0, variables.size(), i -> F.Rule(variables.get(i), F.num(point[i])));
    }

    /**
     * The finite interval of every variable which the constraints state as bounds of the single
     * variable, like <code>0 <= x <= 16</code> or <code>x >= -3 && x < 5</code>.
     *
     * @return <code>{{lower, upper}, ...}</code> in the order of the variables, or
     *         <code>null</code> if a variable has no finite interval
     */
    private static double[][] box(IExpr constraints, List<IExpr> variables, boolean[] onlyBounds,
        EvalEngine engine) {
      onlyBounds[0] = true;
      final int n = variables.size();
      double[][] box = new double[n][];
      for (int i = 0; i < n; i++) {
        box[i] = new double[] {Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY};
      }
      IAST conjuncts = constraints.isAnd() ? (IAST) constraints : F.And(constraints);
      for (int i = 1; i < conjuncts.size(); i++) {
        IExpr conjunct = conjuncts.get(i);
        if (conjunct.isTrue()) {
          continue;
        }
        if (!conjunct.isAST() || conjunct.argSize() < 2) {
          onlyBounds[0] = false;
          continue;
        }
        final boolean less = conjunct.isAST(S.Less) || conjunct.isAST(S.LessEqual);
        if (!less && !conjunct.isAST(S.Greater) && !conjunct.isAST(S.GreaterEqual)) {
          onlyBounds[0] = false;
          continue;
        }
        IAST relation = (IAST) conjunct;
        for (int j = 1; j < relation.argSize(); j++) {
          // smaller <= larger
          IExpr smaller = less ? relation.get(j) : relation.get(j + 1);
          IExpr larger = less ? relation.get(j + 1) : relation.get(j);
          boolean bound = false;
          int variable = variables.indexOf(larger);
          if (variable >= 0) {
            double lower = engine.evalN(smaller).evalfNaN();
            if (!Double.isNaN(lower)) {
              box[variable][0] = Math.max(box[variable][0], lower);
              bound = true;
            }
          }
          variable = variables.indexOf(smaller);
          if (variable >= 0) {
            double upper = engine.evalN(larger).evalfNaN();
            if (!Double.isNaN(upper)) {
              box[variable][1] = Math.min(box[variable][1], upper);
              bound = true;
            }
          }
          if (!bound) {
            onlyBounds[0] = false;
          }
        }
      }
      for (int i = 0; i < n; i++) {
        if (Double.isInfinite(box[i][0]) || Double.isInfinite(box[i][1]) || box[i][0] > box[i][1]) {
          return null;
        }
      }
      return box;
    }

    /**
     * Test a point against the conjunction <code>constraints</code>, with the tolerance a numerical
     * solution on the boundary needs.
     */
    private static boolean isFeasible(IExpr constraints, IAST rules, EvalEngine engine) {
      IAST conjuncts = constraints.isAnd() ? (IAST) constraints : F.And(constraints);
      for (int i = 1; i < conjuncts.size(); i++) {
        IExpr conjunct = conjuncts.get(i);
        if (!conjunct.isAST() || conjunct.argSize() < 2) {
          continue;
        }
        final boolean less = conjunct.isAST(S.Less) || conjunct.isAST(S.LessEqual);
        final boolean equal = conjunct.isAST(S.Equal);
        if (!less && !equal && !conjunct.isAST(S.Greater) && !conjunct.isAST(S.GreaterEqual)) {
          continue;
        }
        IAST relation = (IAST) conjunct;
        double previous = engine.evalN(F.subst(relation.arg1(), rules)).evalfNaN();
        for (int j = 2; j < relation.size(); j++) {
          double next = engine.evalN(F.subst(relation.get(j), rules)).evalfNaN();
          if (Double.isNaN(previous) || Double.isNaN(next)) {
            return false;
          }
          double tolerance =
              FEASIBILITY_TOLERANCE * Math.max(1.0, Math.max(Math.abs(previous), Math.abs(next)));
          double difference = next - previous;
          if (equal ? Math.abs(difference) > tolerance
              : less ? difference < -tolerance : difference > tolerance) {
            return false;
          }
          previous = next;
        }
      }
      return true;
    }

    /** <code>constraints && relation</code> as one flat conjunction */
    private static IExpr and(IExpr constraints, IExpr relation) {
      if (constraints.isTrue()) {
        return relation;
      }
      if (constraints.isAnd()) {
        return ((IAST) constraints).appendClone(relation);
      }
      return F.And(constraints, relation);
    }

    /**
     * The optimum with integer values for the <code>integerVariables</code> by branch and bound:
     * solve the problem without the integer conditions, and where an integer variable comes out as
     * <code>v</code> with a fractional part, solve the two problems with <code>x <= Floor(v)</code>
     * and with <code>x >= Ceiling(v)</code>.
     */
    private IExpr branchAndBound(IExpr function, IExpr constraints, List<IExpr> integerVariables,
        Search search) {
      final boolean minimize = getGoalType() == GoalType.MINIMIZE;
      IExpr incumbent = F.NIL;
      java.util.ArrayDeque<IExpr> open = new java.util.ArrayDeque<IExpr>();
      open.push(constraints);
      while (!open.isEmpty() && search.nodes < MAX_BRANCH_AND_BOUND_NODES) {
        search.nodes++;
        IExpr node = open.pop();
        IExpr relaxed = optimum(function, node, search);
        if (relaxed.isNIL()) {
          continue;
        }
        if (incumbent.isPresent()) {
          double bound = relaxed.first().evalf();
          double value = incumbent.first().evalf();
          if (minimize ? bound >= value : bound <= value) {
            // no integer point of this node is better than the one which is known
            continue;
          }
        }
        IAST rules = (IAST) relaxed.second();
        IExpr fractional = F.NIL;
        double fractionalValue = 0.0;
        for (int i = 1; i < rules.size(); i++) {
          IExpr variable = rules.get(i).first();
          if (integerVariables.contains(variable)) {
            double v = rules.get(i).second().evalf();
            if (Math.abs(v - Math.rint(v)) > FEASIBILITY_TOLERANCE) {
              fractional = variable;
              fractionalValue = v;
              break;
            }
          }
        }
        if (fractional.isNIL()) {
          // an integer point: write the integer variables as integers
          IASTAppendable integerRules = F.ListAlloc(rules.argSize());
          for (int i = 1; i < rules.size(); i++) {
            IExpr variable = rules.get(i).first();
            integerRules.append(integerVariables.contains(variable)
                ? F.Rule(variable, F.ZZ(Math.round(rules.get(i).second().evalf())))
                : rules.get(i));
          }
          IExpr value = search.engine.evalN(F.subst(function, integerRules));
          if (value.isReal()) {
            incumbent = better(incumbent, F.list(value, integerRules));
          }
          continue;
        }
        IExpr upper = F.LessEqual(fractional, F.ZZ((long) Math.floor(fractionalValue)));
        IExpr lower = F.GreaterEqual(fractional, F.ZZ((long) Math.ceil(fractionalValue)));
        open.push(and(node, upper));
        open.push(and(node, lower));
      }
      if (incumbent.isPresent()) {
        // a branch without points is no failure once an integer point is known
        search.infeasible = false;
      }
      return incumbent;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }


  /**
   * The NMinValue function is used to find the minimum value for the given function.
   */
  private static class NMinValue extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr minimize = engine.evaluate(F.NMinimize(ast.arg1(), ast.arg2()));
      if (minimize.isList2() && minimize.second().isListOfRules()) {
        return minimize.first();
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }
  }


  public static void initialize() {
    Initializer.init();
  }

  private MinMaxFunctions() {}
}
