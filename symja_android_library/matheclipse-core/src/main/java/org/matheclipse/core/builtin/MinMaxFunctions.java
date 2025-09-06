package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.S.Power;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import org.matheclipse.core.eval.exception.JASConversionException;
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
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.Matcher;
import org.matheclipse.core.polynomials.longexponent.ExprMonomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomial;
import org.matheclipse.core.polynomials.longexponent.ExprPolynomialRing;
import org.matheclipse.core.polynomials.longexponent.ExprRingFactory;
import org.matheclipse.core.reflection.system.rules.FunctionRangeRules;
import org.matheclipse.core.sympy.calculus.Util;
import org.matheclipse.core.visit.VisitorExpr;
import com.google.common.base.Suppliers;

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
        IExpr result = maximize(ast.topHead(), ast.arg1(), x, engine);
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
        IExpr result = minimize(ast.topHead(), ast.arg1(), x, engine);
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

  private static final class FunctionDomain extends AbstractFunctionEvaluator {
    private static final class ComplexesDomain {
      IAST variables;
      EvalEngine engine;

      public ComplexesDomain(IAST variables, EvalEngine engine) {
        this.variables = variables;
        this.engine = engine;
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
        }
      }

      private IExpr reduceRelation(IAST gt) {
        VariablesSet vset = new VariablesSet(gt);
        IAST reduceVariables = vset.reduceVariables(variables);
        if (reduceVariables.argSize() == 1) {
          final IExpr variable = reduceVariables.arg1();
          IExpr intervalData = IntervalDataSym.toIntervalData(gt, variable, engine);
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
            ComplexesDomain cd = new ComplexesDomain(variables, engine);
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


  private static final class FunctionRange extends AbstractFunctionEvaluator {
    private static final class FunctionRangeRealsVisitor extends VisitorExpr {
      final EvalEngine engine;

      public FunctionRangeRealsVisitor(EvalEngine engine) {
        super();
        this.engine = engine;
      }

      // @Override
      // public IExpr visit2(IExpr head, IExpr arg1) {
      // boolean evaled = false;
      // IExpr x = arg1;
      // IExpr result = arg1.accept(this);
      // if (result.isPresent()) {
      // evaled = true;
      // x = result;
      // }
      // if (evaled) {
      // return F.unaryAST1(head, x);
      // }
      // return F.NIL;
      // }

      /** {@inheritDoc} */
      @Override
      public IExpr visit3(IExpr head, IExpr arg1, IExpr arg2) {
        boolean evaled = false;
        IExpr x1 = arg1;
        IExpr result = arg1.accept(this);
        if (result.isPresent()) {
          evaled = true;
          x1 = result;
        }
        IExpr x2 = arg2;
        result = arg2.accept(this);
        if (result.isPresent()) {
          evaled = true;
          x2 = result;
        }
        if (head.equals(Power)) {
          if (x1.isInterval1()) {
            IAST interval = (IAST) x1;
            IExpr l = interval.lower();
            IExpr u = interval.upper();
            if (x2.isMinusOne()) {
              if (l.greaterEqual(F.C1).isTrue()) {
                // if (S.GreaterEqual.ofQ(engine, l, F.C1)) {
                // [>= 1, u]
                return F.Interval(F.Power(u, x2), F.Power(l, x2));
              }
            }
            if (l.isNegativeResult() && u.isPositiveResult()) {
              if (x2.isPositiveResult()) {
                return F.Interval(F.C0, F.Power(u, x2));
              }
              if (x2.isEvenResult()
                  || (x2.isFraction() && ((IFraction) x2).denominator().isEven())) {
                return F.Interval(F.C0, F.Power(u, x2));
              }
            }
          }
        }
        if (evaled) {
          return F.binaryAST2(head, x1, x2);
        }
        return F.NIL;
      }
    }

    private static class Initializer {

      private static Matcher init() {
        Matcher MATCHER = new Matcher();
        IAST list = FunctionRangeRules.RULES;

        for (int i = 1; i < list.size(); i++) {
          IExpr arg = list.get(i);
          if (arg.isAST(S.SetDelayed, 3)) {
            MATCHER.caseOf(arg.first(), arg.second());
          } else if (arg.isAST(S.Set, 3)) {
            MATCHER.caseOf(arg.first(), arg.second());
          }
        }
        return MATCHER;
      }
    }

    private static Supplier<Matcher> LAZY_MATCHER;


    public static IExpr callMatcher(final IAST ast, IExpr arg1, EvalEngine engine) {
      IExpr temp = getMatcher().replaceAll(ast);
      if (temp.isPresent()) {
        engine.putCache(ast, temp);
      }
      return temp;
    }

    private static Matcher getMatcher() {
      return LAZY_MATCHER.get();
    }

    private IExpr convertInterval(IExpr result, ISymbol y) {
      IAST list = (IAST) result.first();
      return convertMinMaxList(list, y);
    }

    private IExpr convertMinMaxList(IAST list, ISymbol y) {
      if (list.arg1().isRealResult()) {
        if (list.arg2().isInfinity()) {
          return F.GreaterEqual(y, list.arg1());
        } else if (list.arg2().isRealResult()) {
          return F.LessEqual(list.arg1(), y, list.arg2());
        }
      } else if (list.arg2().isRealResult()) {
        if (list.arg1().isNegativeInfinity()) {
          if (!list.arg2().isInfinity()) {
            return F.LessEqual(y, list.arg2());
          }
        }
      }
      return F.NIL;
    }

    // public IExpr evaluate(final IAST ast, EvalEngine engine) {
    // IExpr function = ast.arg1();
    // IExpr xExpr = ast.arg2();
    // IExpr yExpr = ast.arg3();
    // IBuiltInSymbol domain = S.Reals;
    // try {
    // if (xExpr.isSymbol() && yExpr.isSymbol()) {
    // IAST constrained_interval = IntervalDataSym.reals();
    // if (function.isAST()) {
    // IAST f = (IAST) function;
    // for (int i = 1; i < f.size(); i++) {
    // IExpr arg = f.get(i);
    // if (arg.isPower()) {
    //
    // } else if (arg.isLog()) {
    //
    // }
    // }
    // }
    // }
    // } catch (RuntimeException rex) {
    // rex.printStackTrace();
    // LOGGER.debug("FunctionRange.evaluate() failed", rex);
    // }
    // return F.NIL;
    // }
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr function = ast.arg1();
      IExpr xExpr = ast.arg2();
      IExpr yExpr = ast.arg3();
      IBuiltInSymbol domain = S.Reals;
      try {
        if (xExpr.isSymbol() && yExpr.isSymbol()) {
          IExpr match = callMatcher(ast, function, engine);
          if (match.isPresent()) {
            return match;
          }
          boolean evaled = true;
          ISymbol x = (ISymbol) xExpr;
          ISymbol y = (ISymbol) yExpr;
          IExpr min = engine.evalQuiet(F.Minimize(function, xExpr));
          IExpr max = engine.evalQuiet(F.Maximize(function, xExpr));
          IASTMutable minMaxList = F.binaryAST2(S.List, F.CNInfinity, F.CInfinity);
          if (min.isList2()) {
            minMaxList.set(1, min.first());
          } else {
            evaled = false;
          }
          if (max.isList2()) {
            minMaxList.set(2, max.first());
          } else {
            evaled = false;
          }
          if (evaled) {
            return convertMinMaxList(minMaxList, y);
          }
          IExpr f = function.replaceAll(F.Rule(x, F.Interval(F.CNInfinity, F.CInfinity)))
              .orElse(function);
          IExpr result = engine.evaluate(f);
          if (result.isInterval1()) {
            return convertInterval(result, y);
          } else if (domain.equals(S.Reals)) {
            IExpr temp = result;
            while (temp.isPresent()) {
              temp = temp.accept(new FunctionRangeRealsVisitor(engine));
              if (temp.isPresent()) {
                result = engine.evaluate(temp);
                temp = result;
              }
            }
            if (result.isInterval1()) {
              return convertInterval(result, y);
            }
          }
        }

      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        // rex.printStackTrace();
        LOGGER.debug("FunctionRange.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // Initializer.init();
      LAZY_MATCHER = Suppliers.memoize(Initializer::init);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
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
      S.FunctionDomain.setEvaluator(new FunctionDomain());
      S.FunctionPeriod.setEvaluator(new FunctionPeriod());
      S.FunctionRange.setEvaluator(new FunctionRange());
      S.Maximize.setEvaluator(new Maximize());
      S.Minimize.setEvaluator(new Minimize());
      S.NMaximize.setEvaluator(new NMaximize());
      S.NMinimize.setEvaluator(new NMinimize());

      S.NArgMax.setEvaluator(new NArgMax());
      S.NArgMin.setEvaluator(new NArgMin());
      S.NMaxValue.setEvaluator(new NMaxValue());
      S.NMinValue.setEvaluator(new NMinValue());
    }
  }


  /**
   *
   *
   * <pre>
   * <code>Maximize(unary-function, variable)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the maximum of the unary function for the given <code>variable</code>.
   *
   * </blockquote>
   *
   * <p>
   * See
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Derivative_test">Wikipedia - Derivative test</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Maximize(-x^4-7*x^3+2*x^2 - 42,x)
   * {-42-7*(-21/8-Sqrt(505)/8)^3+2*(21/8+Sqrt(505)/8)^2-(21/8+Sqrt(505)/8)^4,{x-&gt;-21/8-Sqrt(505)/8}}
   * </code>
   * </pre>
   *
   * <p>
   * Print a message if no maximum can be found
   *
   * <pre>
   * <code>&gt;&gt; Maximize(x^4+7*x^3-2*x^2 + 42, x)
   * {Infinity,{x-&gt;-Infinity}}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Minimize.md">Minimize</a>
   */
  private static final class Maximize extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr function = ast.arg1();
      IExpr x = ast.arg2();
      if (x.isList()) {
        if (x.isList1()) {
          x = x.first();
        } else {
          // `1` currently not supported in `2`.
          return Errors.printMessage(S.Maximize, "unsupported",
              F.List("Multiple variables", "Maximize"), engine);
        }
      }
      ISymbol head = ast.topHead();
      if (x.isSymbol() || (x.isAST() && !x.isList())) {
        return maximize(head, function, x, engine);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  /**
   *
   *
   * <pre>
   * <code>Minimize(unary-function, variable)
   * </code>
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the minimum of the unary function for the given <code>variable</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Derivative_test">Wikipedia - Derivative test</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * <code>&gt;&gt; Minimize(x^4+7*x^3-2*x^2 + 42, x)
   * {42+7*(-21/8-Sqrt(505)/8)^3-2*(21/8+Sqrt(505)/8)^2+(21/8+Sqrt(505)/8)^4,{x-&gt;-21/8-Sqrt(505)/8}}
   * </code>
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Maximize.md">Maximize</a>
   */
  private static final class Minimize extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr function = ast.arg1();
      IExpr x = ast.arg2();
      if (x.isList()) {
        if (x.isList1()) {
          x = x.first();
        } else {
          // `1` currently not supported in `2`.
          return Errors.printMessage(S.Minimize, "unsupported",
              F.List("Multiple variables", "Minimize"), engine);
        }
      }
      ISymbol head = ast.topHead();
      if (x.isSymbol() || (x.isAST() && !x.isList())) {
        return minimize(head, function, x, engine);
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
   *   x  + 2y &lt;=  6
   *   3x + 2y &lt;= 12
   *         x &gt;= 0
   *         y &gt;= 0
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
   *   x  + 2y &lt;=  6
   *   3x + 2y &lt;= 12
   *         x &gt;= 0
   *         y &gt;= 0
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


  private static final Logger LOGGER = LogManager.getLogger(MinMaxFunctions.class);

  private static double[][] boundaries(int dim, double lower, double upper) {
    double[][] boundaries = new double[2][dim];
    for (int i = 0; i < dim; i++)
      boundaries[0][i] = lower;
    for (int i = 0; i < dim; i++)
      boundaries[1][i] = upper;
    return boundaries;
  }

  public static void initialize() {
    Initializer.init();
  }

  private static IExpr maximize(ISymbol head, IExpr function, IExpr x, EvalEngine engine) {
    try {
      IExpr temp = maximizeExprPolynomial(function, F.list(x));
      if (temp.isPresent()) {
        return temp;
      }

      IExpr yNInf = S.Limit.of(function, F.Rule(x, F.CNInfinity));
      if (yNInf.isInfinity()) {
        LOGGER.log(engine.getLogLevel(), "{}: the maximum cannot be found.", head);
        return F.list(F.CInfinity, F.list(F.Rule(x, F.CNInfinity)));
      }
      IExpr yInf = S.Limit.of(function, F.Rule(x, F.CInfinity));
      if (yInf.isInfinity()) {
        LOGGER.log(engine.getLogLevel(), "{}: the maximum cannot be found.", head);
        return F.list(F.CInfinity, F.list(F.Rule(x, F.CInfinity)));
      }

      IExpr first_derivative = S.D.of(engine, function, x);
      IExpr second_derivative = S.D.of(engine, first_derivative, x);
      IExpr candidates = S.Solve.of(engine, F.Equal(first_derivative, F.C0), x, S.Reals);
      if (candidates.isFree(S.Solve)) {
        IExpr maxCandidate = F.NIL;
        IExpr maxValue = F.CNInfinity;
        if (candidates.isListOfLists()) {
          for (int i = 1; i < candidates.size(); i++) {
            IExpr candidate = ((IAST) candidates).get(i).first().second();
            IExpr value = engine.evaluate(F.xreplace(second_derivative, x, candidate));
            if (value.isNegative()) {
              IExpr functionValue = engine.evaluate(F.xreplace(function, x, candidate));
              if (functionValue.greater(maxValue).isTrue()) {
                // if (S.Greater.ofQ(functionValue, maxValue)) {
                maxValue = functionValue;
                maxCandidate = candidate;
              }
            }
          }
          if (maxCandidate.isPresent()) {
            return F.list(maxValue, F.list(F.Rule(x, maxCandidate)));
          }
        }
        return F.CEmptyList;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      LOGGER.log(engine.getLogLevel(), head, rex);
    }
    return F.NIL;
  }

  private static IAST maximizeCubicPolynomial(ExprPolynomial polynomial, IExpr x) {
    long varDegree = polynomial.degree(0);
    IExpr a;
    IExpr b;
    IExpr c;
    IExpr d;
    IExpr e;
    if (varDegree <= 3) {
      // solve cubic or quadratic maximize:
      a = F.C0;
      b = F.C0;
      c = F.C0;
      d = F.C0;
      e = F.C0;
      for (ExprMonomial monomial : polynomial) {
        IExpr coeff = monomial.coefficient();
        long lExp = monomial.exponent().getVal(0);
        if (lExp == 4) {
          a = coeff;
        } else if (lExp == 3) {
          b = coeff;
        } else if (lExp == 2) {
          c = coeff;
        } else if (lExp == 1) {
          d = coeff;
        } else if (lExp == 0) {
          e = coeff;
        } else {
          throw new ArithmeticException("Maximize::Unexpected exponent value: " + lExp);
        }
      }
      if (a.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        if (b.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          // quadratic
          if (c.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
            if (d.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
              // The `1` is not attained at any point satisfying the constraints.
              return Errors.printMessage(S.Maximize, "natt", F.List("maximum"));
            } else {
              // linear
              return F.list(F.Piecewise(F.list(F.list(e, F.Equal(d, F.C0))), F.CInfinity), F.list(
                  F.Rule(x, F.Piecewise(F.list(F.list(F.C0, F.Equal(d, F.C0))), S.Indeterminate))));
            }
          } else {
            return F
                .List(
                    F.Piecewise(
                        F.list(F.list(e, F.And(F.Equal(d, 0), F.LessEqual(c, 0))), F.list(
                                F.Times(F.C1D4, F.Power(c, -1),
                                    F.Plus(F.Times(-1, F.Power(d, 2)), F.Times(4, c, e))),
                                F.Or(F.And(F.Greater(d, 0), F.Less(c, 0)),
                                    F.And(F.Less(d, 0), F.Less(c, 0))))),
                        F.CInfinity),
                    F.list(F.Rule(x,
                        F.Piecewise(
                            F.list(F.list(F.Times(F.CN1D2, F.Power(c, -1), d),
                                    F.Or(F.And(F.Greater(d, 0), F.Less(c, 0)),
                                        F.And(F.Less(d, 0), F.Less(c, 0)))),
                                F.list(F.C0, F.And(F.Equal(d, 0), F.LessEqual(c, 0)))),
                            S.Indeterminate))));
          }
        } else {
          // cubic
          return F.list(
              F.Piecewise(
                  F.list(F.list(e,
                          F.Or(F.And(F.Equal(d, F.C0), F.Equal(c, F.C0), F.Equal(b, F.C0)),
                              F.And(F.Equal(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)))),
                      F.list(
                          F.Times(F.C1D4, F.Power(c, F.CN1),
                              F.Plus(F.Negate(F.Sqr(d)), F.Times(F.C4, c, e))),
                          F.Or(F.And(F.Greater(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)),
                              F.And(F.Less(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0))))),
                  F.oo),
              F.list(F.Rule(x,
                  F.Piecewise(
                      F.list(F.list(F.Times(F.CN1D2, F.Power(c, F.CN1), d),
                              F.Or(F.And(F.Greater(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)),
                                  F.And(F.Less(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0)))),
                          F.list(F.C0,
                              F.Or(F.And(F.Equal(d, F.C0), F.Equal(c, F.C0), F.Equal(b, F.C0)),
                                  F.And(F.Equal(d, F.C0), F.Less(c, F.C0), F.Equal(b, F.C0))))),
                      F.Indeterminate))));
        }
      }
    }

    return F.NIL;
  }

  private static IAST maximizeExprPolynomial(final IExpr expr, IAST varList) {
    IAST result = F.NIL;
    try {
      // try to generate a common expression polynomial
      ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
      ExprPolynomial ePoly = ring.create(expr, false, false, false);
      ePoly = ePoly.multiplyByMinimumNegativeExponents();
      result = maximizeCubicPolynomial(ePoly, varList.arg1());

      // result = QuarticSolver.sortASTArguments(result);
      return result;
    } catch (ArithmeticException | JASConversionException e2) {
      LOGGER.debug("MinMaxFunctions.maximizeExprPolynomial() failed", e2);
    }
    return result;
  }

  private static final IExpr minimize(ISymbol head, IExpr function, IExpr x, EvalEngine engine) {
    try {
      IExpr temp = minimizeExprPolynomial(function, F.list(x));
      if (temp.isPresent()) {
        return temp;
      }

      IExpr yNInf = S.Limit.of(function, F.Rule(x, F.CNInfinity));
      if (yNInf.isNegativeInfinity()) {
        LOGGER.log(engine.getLogLevel(), "{}: the maximum cannot be found.", head);
        return F.list(F.CNInfinity, F.list(F.Rule(x, F.CNInfinity)));
      }
      IExpr yInf = S.Limit.of(function, F.Rule(x, F.CInfinity));
      if (yInf.isNegativeInfinity()) {
        LOGGER.log(engine.getLogLevel(), "{}: the maximum cannot be found.", head);
        return F.list(F.CNInfinity, F.list(F.Rule(x, F.CInfinity)));
      }

      IExpr first_derivative = S.D.of(engine, function, x);
      IExpr second_derivative = S.D.of(engine, first_derivative, x);
      IExpr candidates = S.Solve.of(engine, F.Equal(first_derivative, F.C0), x, S.Reals);
      if (candidates.isFree(S.Solve)) {
        IExpr minCandidate = F.NIL;
        IExpr minValue = F.CInfinity;
        if (candidates.isListOfLists()) {
          for (int i = 1; i < candidates.size(); i++) {
            IExpr candidate = ((IAST) candidates).get(i).first().second();
            IExpr value = engine.evaluate(F.xreplace(second_derivative, x, candidate));
            if (value.isPositiveResult()) {
              IExpr functionValue = engine.evaluate(F.xreplace(function, x, candidate));
              if (S.Less.ofQ(functionValue, minValue)) {
                minValue = functionValue;
                minCandidate = candidate;
              }
            }
          }
          if (minCandidate.isPresent()) {
            return F.list(minValue, F.list(F.Rule(x, minCandidate)));
          }
        }
        return F.CEmptyList;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      LOGGER.log(engine.getLogLevel(), head, rex);
    }
    return F.NIL;
  }

  private static IAST minimizeCubicPolynomial(ExprPolynomial polynomial, IExpr x) {
    long varDegree = polynomial.degree(0);
    IExpr a;
    IExpr b;
    IExpr c;
    IExpr d;
    IExpr e;
    if (varDegree <= 3) {
      // solve cubic or quadratic minimize:
      a = F.C0;
      b = F.C0;
      c = F.C0;
      d = F.C0;
      e = F.C0;
      for (ExprMonomial monomial : polynomial) {
        IExpr coeff = monomial.coefficient();
        long lExp = monomial.exponent().getVal(0);
        if (lExp == 4) {
          a = coeff;
        } else if (lExp == 3) {
          b = coeff;
        } else if (lExp == 2) {
          c = coeff;
        } else if (lExp == 1) {
          d = coeff;
        } else if (lExp == 0) {
          e = coeff;
        } else {
          throw new ArithmeticException("Minimize::Unexpected exponent value: " + lExp);
        }
      }
      if (a.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
        if (b.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
          // quadratic
          if (c.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
            if (d.isPossibleZero(false, Config.SPECIAL_FUNCTIONS_TOLERANCE)) {
              // The `1` is not attained at any point satisfying the constraints.
              return Errors.printMessage(S.Minimize, "natt", F.List("minimum"));
            } else {
              // linear
              return F.list(F.Piecewise(F.list(F.list(e, F.Equal(d, F.C0))), F.CNInfinity), F.list(
                  F.Rule(x, F.Piecewise(F.list(F.list(F.C0, F.Equal(d, F.C0))), S.Indeterminate))));
            }
          } else {
            return F
                .List(
                    F.Piecewise(
                        F.list(F.list(e, F.And(F.Equal(d, 0),
                                F.GreaterEqual(c, 0))),
                            F.list(
                                F.Times(
                                    F.C1D4, F.Power(c, -1), F.Plus(F.Times(-1, F.Power(d, 2)),
                                        F.Times(4, c, e))),
                                F.Or(F.And(F.Greater(d, 0), F.Greater(c, 0)),
                                    F.And(F.Less(d, 0), F.Greater(c, 0))))),
                        F.CNInfinity),
                    F.list(F.Rule(x,
                        F.Piecewise(
                            F.list(F.list(F.Times(F.CN1D2, F.Power(c, -1), d),
                                    F.Or(F.And(F.Greater(d, 0), F.Greater(c, 0)),
                                        F.And(F.Less(d, 0), F.Greater(c, 0)))),
                                F.list(F.C0, F.And(F.Equal(d, 0), F.GreaterEqual(c, 0)))),
                            S.Indeterminate))));
          }
        } else {
          // cubic
          return F.list(
              F.Piecewise(
                  F.list(F.list(e,
                          F.Or(F.And(F.Equal(d, F.C0), F.Equal(c, F.C0), F.Equal(b, F.C0)),
                              F.And(F.Equal(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0)))),
                      F.list(
                          F.Times(F.C1D4, F.Power(c, F.CN1),
                              F.Plus(F.Negate(F.Sqr(d)), F.Times(F.C4, c, e))),
                          F.Or(F.And(F.Greater(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0)),
                              F.And(F.Less(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0))))),
                  F.Noo),
              F.list(F.Rule(x,
                  F.Piecewise(
                      F.list(F.list(F.Times(F.CN1D2, F.Power(c, F.CN1), d),
                              F.Or(F.And(F.Greater(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0)),
                                  F.And(F.Less(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0)))),
                          F.list(F.C0,
                              F.Or(F.And(F.Equal(d, F.C0), F.Equal(c, F.C0), F.Equal(b, F.C0)),
                                  F.And(F.Equal(d, F.C0), F.Greater(c, F.C0), F.Equal(b, F.C0))))),
                      F.Indeterminate))));
        }
      }
    }

    return F.NIL;
  }

  private static IAST minimizeExprPolynomial(final IExpr expr, IAST varList) {
    IAST result = F.NIL;
    try {
      // try to generate a common expression polynomial
      ExprPolynomialRing ring = new ExprPolynomialRing(ExprRingFactory.CONST, varList);
      ExprPolynomial ePoly = ring.create(expr, false, false, false);
      ePoly = ePoly.multiplyByMinimumNegativeExponents();
      result = minimizeCubicPolynomial(ePoly, varList.arg1());

      // result = QuarticSolver.sortASTArguments(result);
      return result;
    } catch (ArithmeticException | JASConversionException e2) {
      LOGGER.debug("MinMaxFunctions.minimizeExprPolynomial() failed", e2);
    }
    return result;
  }

  // static final int DIM = 2;
  // static final int LAMBDA = 4 + (int) (3. * Math.log(DIM));
  //
  // public static void main(String[] args) {
  //
  // final MultivariateFunction func = new MultiVariateNumerical(F.Plus(F.Sinc(F.x), F.Sinc(F.y)),
  // //
  // F.List(F.x, F.y));
  //
  // int dim = 2;
  // final double[] minPoint = new double[dim];
  // for (int i = 0; i < dim; i++) {
  // minPoint[i] = 0;
  // }
  //
  // double[] init = new double[dim];
  //
  // // Initial is minimum.
  // for (int i = 0; i < dim; i++) {
  // init[i] = minPoint[i];
  // }
  // optimizePowell(func, //
  // null,
  // // minPoint,
  // init, GoalType.MINIMIZE, 1e-9, 1e-9, 1e-9);
  // }

  private static double[] point(int n, double value) {
    double[] ds = new double[n];
    Arrays.fill(ds, value);
    return ds;
  }

  private MinMaxFunctions() {}
}
