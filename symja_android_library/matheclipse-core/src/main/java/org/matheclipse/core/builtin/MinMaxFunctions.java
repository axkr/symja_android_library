package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.hipparchus.analysis.MultivariateFunction;
import org.hipparchus.optim.InitialGuess;
import org.hipparchus.optim.MaxEval;
import org.hipparchus.optim.OptimizationData;
import org.hipparchus.optim.PointValuePair;
import org.hipparchus.optim.linear.LinearConstraint;
import org.hipparchus.optim.linear.LinearConstraintSet;
import org.hipparchus.optim.linear.LinearObjectiveFunction;
import org.hipparchus.optim.linear.NonNegativeConstraint;
import org.hipparchus.optim.linear.PivotSelectionRule;
import org.hipparchus.optim.linear.SimplexSolver;
import org.hipparchus.optim.nonlinear.scalar.GoalType;
import org.hipparchus.optim.nonlinear.scalar.MultivariateOptimizer;
import org.hipparchus.optim.nonlinear.scalar.ObjectiveFunction;
import org.hipparchus.optim.nonlinear.scalar.noderiv.PowellOptimizer;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Expr2LP;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
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
import org.matheclipse.core.interfaces.IASTAppendable;
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
              if (domain.equals(S.Complexes)) {
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
                if (domain.equals(S.Complexes)) {
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
          if (domain.equals(S.Complexes)) {
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
              return result;
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

        if (domain.equals(S.Complexes)) {
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
            if (head.equals(S.Log)) {
              // Branch point at arg == 0
              conditions.add(F.Equal(ast.arg1(), F.C0));
              // Branch cut (-inf, 0] => Im(arg) == 0 && Re(arg) <= 0
              conditions
                  .add(F.And(F.Equal(F.Im(ast.arg1()), F.C0), F.LessEqual(F.Re(ast.arg1()), F.C0)));
            } else if (head.equals(S.Tan) || head.equals(S.Sec)) {
              // Poles when Cos(x) == 0
              conditions.add(F.Equal(F.Cos(ast.arg1()), F.C0));
            } else if (head.equals(S.Cot) || head.equals(S.Csc)) {
              // Poles when Sin(x) == 0
              conditions.add(F.Equal(F.Sin(ast.arg1()), F.C0));
            } else if (head.equals(S.ArcSin) || head.equals(S.ArcCos)) {
              // Branch points at z == 1, z == -1
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Branch cuts: (-inf, -1] U [1, inf)
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.LessEqual(F.Re(z), F.CN1)));
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.GreaterEqual(F.Re(z), F.C1)));
            } else if (head.equals(S.ArcTan)) {
              // Branch points at z == I, z == -I
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Plus(F.CI, z), F.C0));
              conditions.add(F.Equal(F.Subtract(F.CI, z), F.C0)); // -I + z == 0
              // Branch cuts: [I, I*inf) U (-I*inf, -I]
              conditions.add(F.And(F.Equal(F.Re(z), F.C0), F.GreaterEqual(F.Im(z), F.C1)));
              conditions.add(F.And(F.Equal(F.Re(z), F.C0), F.LessEqual(F.Im(z), F.CN1)));
            } else if (head.equals(S.ArcCot)) {
              // Branch points at z == I, z == -I
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Plus(F.CI, z), F.C0));
              conditions.add(F.Equal(F.Subtract(F.CI, z), F.C0));
              // Branch cut: [-I, I]
              conditions.add(F.And(F.Equal(F.Re(z), F.C0), F.GreaterEqual(F.Im(z), F.CN1),
                  F.LessEqual(F.Im(z), F.C1)));
            } else if (head.equals(S.ArcSec) || head.equals(S.ArcCsc)) {
              // Branch points z == +/- 1 and z == 0
              IExpr z = ast.arg1();
              conditions.add(F.Equal(z, F.C0));
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Branch cut: [-1, 1]
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.GreaterEqual(F.Re(z), F.CN1),
                  F.LessEqual(F.Re(z), F.C1)));
            } else if (head.equals(S.ArcCosh)) {
              // Branch points z == 1, z == -1
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Branch cut: (-inf, 1]
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.LessEqual(F.Re(z), F.C1)));
            } else if (head.equals(S.ArcTanh)) {
              // Branch points z == 1, z == -1
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Branch cuts: (-inf, -1] U [1, inf)
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.LessEqual(F.Re(z), F.CN1)));
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.GreaterEqual(F.Re(z), F.C1)));
            } else if (head.equals(S.ArcCoth)) {
              // Branch points z == 1, z == -1
              IExpr z = ast.arg1();
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Branch cut: [-1, 1]
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.GreaterEqual(F.Re(z), F.CN1),
                  F.LessEqual(F.Re(z), F.C1)));
            } else if (head.equals(S.ArcSech)) {
              // ArcSech(z) = ArcCosh(1/z).
              IExpr z = ast.arg1();
              conditions.add(F.Equal(z, F.C0));
              conditions.add(F.Equal(F.Subtract(F.C1, z), F.C0));
              conditions.add(F.Equal(F.Plus(F.C1, z), F.C0));
              // Cuts: (-inf, 0] U [1, inf)
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.LessEqual(F.Re(z), F.C0)));
              conditions.add(F.And(F.Equal(F.Im(z), F.C0), F.GreaterEqual(F.Re(z), F.C1)));
            } else if (head.equals(S.ArcCsch)) {
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
            if (head.equals(S.Log)) {
              conditions.add(F.Equal(ast.arg1(), F.C0));
            } else if (head.equals(S.Tan) || head.equals(S.Sec)) {
              conditions.add(F.Equal(F.Cos(ast.arg1()), F.C0));
            } else if (head.equals(S.Cot) || head.equals(S.Csc)) {
              conditions.add(F.Equal(F.Sin(ast.arg1()), F.C0));
            } else if (head.equals(S.ArcSec) || head.equals(S.ArcCsc) || head.equals(S.ArcSech)
                || head.equals(S.ArcCsch)) {
              conditions.add(F.Equal(ast.arg1(), F.C0));
            }
          }
        }

        if (ast.argSize() == 1) {
          if (head.equals(S.Gamma) || head.equals(S.LogGamma) || head.equals(S.Factorial)) {
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
        if (head.equals(S.Less) || head.equals(S.LessEqual) || head.equals(S.Greater)
            || head.equals(S.GreaterEqual) || head.equals(S.Equal) || head.equals(S.Unequal)) {

          // Simple heuristic: LHS == RHS
          if (ast.size() == 3) {
            conditions.add(F.Equal(ast.arg1(), ast.arg2()));
          }
        } else if (head.equals(S.And) || head.equals(S.Or) || head.equals(S.Not)) {
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
      // S.FunctionContinuous.setEvaluator(new FunctionContinuous());
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
   * for solving linear optimization problems with linear equality and inequality constraints and
   * implicit non-negative variables.
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
   * for solving linear optimization problems with linear equality and inequality constraints and
   * implicit non-negative variables.
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
          new MaxEval(1000), //
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
      return ARGS_2_2;
    }

    protected GoalType getGoalType() {
      return GoalType.MINIMIZE;
    }

    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      try {
        IAST list1 = ast.arg1().makeList();
        IAST listOfVariables = ast.arg2().makeList();
        VariablesSet vars = new VariablesSet(listOfVariables);
        if (list1.argSize() > 0 && vars.size() > 0) {
          IExpr function = list1.first();
          ExprAnalyzer exprAnalyzer = new ExprAnalyzer(function, listOfVariables, engine);
          int typeOfExpression = exprAnalyzer.simplifyAndAnalyze();
          if (typeOfExpression == ExprAnalyzer.LINEAR) {
            if (list1.isAST2()) {
              return optimizeSimplexSolver(list1, vars, function);
            }
          }
          final MultivariateFunction func = new MultiVariateNumerical(function, listOfVariables);
          int dimension = vars.size();
          final double[] minPoint = new double[dimension];
          for (int i = 0; i < dimension; i++) {
            minPoint[i] = 0;
          }
          double[] init = new double[dimension];
          // Initial is minimum.
          for (int i = 0; i < dimension; i++) {
            init[i] = minPoint[i];
          }
          return optimizePowell(func, //
              vars,
              // minPoint,
              init, getGoalType(), 1e-9, 1e-9, 1e-9);
        }

      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (org.hipparchus.exception.MathRuntimeException e) {
        return Errors.printMessage(ast.topHead(), e, engine);
      }
      return F.NIL;
    }

    private IAST optimizeSimplexSolver(IAST list1, VariablesSet variables, IExpr function) {
      IExpr listOfconstraints = list1.arg2().makeAST(S.And);

      // lc1 && lc2 && lc3...
      LinearObjectiveFunction objectiveFunction = getObjectiveFunction(variables, function);
      List<LinearConstraint> constraints = getConstraints(variables, (IAST) listOfconstraints);
      return simplexSolver(variables, objectiveFunction, objectiveFunction,
          new LinearConstraintSet(constraints), getGoalType(), new NonNegativeConstraint(true),
          PivotSelectionRule.BLAND);
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
