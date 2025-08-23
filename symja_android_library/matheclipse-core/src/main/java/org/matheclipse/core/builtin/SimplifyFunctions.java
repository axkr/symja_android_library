package org.matheclipse.core.builtin;

import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.SimplifyUtil;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class SimplifyFunctions {
  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.FullSimplify.setEvaluator(new FullSimplify());
      S.Simplify.setEvaluator(new Simplify());
    }
  }

  /**
   *
   *
   * <pre>
   * Simplify(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * simplifies <code>expr</code>
   *
   * </blockquote>
   *
   * <pre>
   * Simplify(expr, option1, option2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * simplify <code>expr</code> with some additional options set
   *
   * </blockquote>
   *
   * <ul>
   * <li>Assumptions - use assumptions to simplify the expression
   * <li>ComplexFunction - use this function to determine the &ldquo;weight&rdquo; of an expression.
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Simplify(1/2*(2*x+2))
   * x+1
   *
   * &gt;&gt; Simplify(2*Sin(x)^2 + 2*Cos(x)^2)
   * 2
   *
   * &gt;&gt; Simplify(x)
   * x
   *
   * &gt;&gt; Simplify(f(x))
   * f(x)
   *
   * &gt;&gt; Simplify(a*x^2+b*x^2)
   * (a+b)*x^2
   * </pre>
   *
   * <p>
   * Simplify with an assumption:
   *
   * <pre>
   * &gt;&gt; Simplify(Sqrt(x^2), Assumptions -&gt; x&gt;0)
   * x
   * </pre>
   *
   * <p>
   * For <code>Assumptions</code> you can define the assumption directly as second argument:
   *
   * <pre>
   * &gt;&gt; Simplify(Sqrt(x^2), x&gt;0)
   * x
   * </pre>
   *
   * <pre>
   * ```
   * &gt;&gt; Simplify(Abs(x), x&lt;0)
   * Abs(x)
   * </pre>
   *
   * <p>
   * With this &ldquo;complexity function&rdquo; the <code>Abs</code> expression gets a
   * &ldquo;heavier weight&rdquo;.
   *
   * <pre>
   * &gt;&gt; complexity(x_) := 2*Count(x, _Abs, {0, 10}) + LeafCount(x)
   *
   * &gt;&gt; Simplify(Abs(x), x&lt;0, ComplexityFunction-&gt;complexity)
   * -x
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="FullSimplify.md">FullSimplify</a>
   */
  static class Simplify extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAtom() && ast.isAST1()) {
        return arg1;
      }
      if (arg1.isAST()) {
        IAST list1 = (IAST) arg1;
        int headID = list1.headID();
        switch (headID) {
          case ID.List:
            return list1.mapThread(ast, 1);
          case ID.Rule:
            if (list1.size() == 3) {
              return F.Rule(ast.setAtClone(1, list1.arg1()), ast.setAtClone(1, list1.arg2()));
            }
            break;
          case ID.Equal:
          case ID.Unequal:
          case ID.Greater:
          case ID.GreaterEqual:
          case ID.Less:
          case ID.LessEqual:
            if (list1.size() == 3 && !list1.arg2().isZero()) {
              IExpr sub = ast.setAtClone(1, F.Subtract(list1.arg1(), list1.arg2()));
              return F.binaryAST2(list1.head(), sub, F.C0);
            }
            break;
        }
      }

      // note: this should also cache FullSimplify calls
      IExpr defaultResult = engine.getCache(ast);
      if (defaultResult != null) {
        return defaultResult;
      }

      IExpr complexityFunctionHead = F.NIL;
      OptionArgs options = null;
      if (ast.size() > 2) {
        options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
        complexityFunctionHead = options.getOptionAutomatic(S.ComplexityFunction);
      }
      IExpr assumptionExpr = OptionArgs.determineAssumptions(ast, 2, options);

      IAssumptions oldAssumptions = engine.getAssumptions();
      try {
        Function<IExpr, Long> complexityFunction =
            SimplifyUtil.createComplexityFunction(complexityFunctionHead, engine);
        long minCounter = complexityFunction.apply(arg1);
        defaultResult = arg1;
        long count = 0L;
        if (assumptionExpr.isPresent() && assumptionExpr.isAST()) {
          IAssumptions assumptions =
              org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
          if (assumptions != null) {
            engine.setAssumptions(assumptions);
            arg1 = AssumptionFunctions.refineAssumptions(arg1, assumptions, engine);
            count = complexityFunction.apply(arg1);
            if (count <= minCounter) {
              minCounter = count;
              defaultResult = arg1;
            }
          }
        }

        IExpr temp = arg1.replaceAll(F.list( //
            F.Rule(S.GoldenAngle, //
                F.Times(F.Subtract(F.C3, F.CSqrt5), S.Pi)), //
            F.Rule(S.GoldenRatio, //
                F.Times(F.C1D2, F.Plus(F.C1, F.CSqrt5))), //
            F.Rule(S.Degree, //
                F.Divide(S.Pi, F.ZZ(180))) //
        ));
        if (temp.isPresent()) {
          arg1 = temp;
        }

        temp = SimplifyUtil.simplifyStep(arg1, defaultResult, complexityFunction, minCounter,
            isFullSimplifyMode(), false, engine);
        engine.putCache(ast, temp);
        return temp;

      } catch (ArithmeticException e) {
        //
      } finally {
        engine.setAssumptions(oldAssumptions);
      }

      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.Assumptions, S.$Assumptions), //
              F.Rule(S.ComplexityFunction, S.Automatic)));
    }

    public boolean isFullSimplifyMode() {
      return false;
    }

  }

  /**
   *
   *
   * <pre>
   * FullSimplify(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * works like <code>Simplify</code> but additionally tries some <code>FunctionExpand</code> rule
   * transformations to simplify <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * FullSimplify(expr, option1, option2, ...)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * full simplifies <code>expr</code> with some additional options set
   *
   * </blockquote>
   *
   * <ul>
   * <li>Assumptions - use assumptions to simplify the expression
   * <li>ComplexFunction - use this function to determine the &ldquo;weight&rdquo; of an expression.
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FullSimplify(Cos(n*ArcCos(x)) == ChebyshevT(n, x))
   * True
   * </pre>
   *
   * <h3>Related terms</h3>
   *
   * <p>
   * <a href="Simplify.md">Simplify</a>
   */
  private static class FullSimplify extends Simplify {

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public boolean isFullSimplifyMode() {
      return true;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.Assumptions, S.$Assumptions), //
              F.Rule(S.ComplexityFunction, S.Automatic)));
      SimplifyUtil.TIMES_ORDERLESS_MATCHER = SimplifyUtil.initTimesHashMatcher();
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private SimplifyFunctions() {}
}
