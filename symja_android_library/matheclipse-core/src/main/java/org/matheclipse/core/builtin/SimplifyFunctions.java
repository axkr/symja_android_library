package org.matheclipse.core.builtin;

import java.util.function.Function;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.SimplifyUtil;
import org.matheclipse.core.eval.exception.LimitException;
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
import org.matheclipse.core.reflection.system.TrigSimplifyFu;

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
        }
      }

      // Note: this should also cache FullSimplify calls
      IExpr defaultResult = engine.getCache(ast);
      if (defaultResult != null) {
        return defaultResult;
      }

      IExpr complexityFunctionHead = F.NIL;
      OptionArgs options = null;
      if (ast.argSize() > 1) {
        options = new OptionArgs(ast.topHead(), ast, ast.argSize(), engine);
        complexityFunctionHead = options.getOptionAutomatic(S.ComplexityFunction);
      }
      IExpr assumptionExpr = OptionArgs.determineAssumptions(ast, 2, options);

      IAssumptions oldAssumptions = engine.getAssumptions();
      try {
        Function<IExpr, Long> complexityFunction =
            SimplifyUtil.createComplexityFunction(complexityFunctionHead, engine);

        if (arg1.isAST()) {
          IExpr relational = simplifyRelational((IAST) arg1, ast, complexityFunction, engine);
          if (relational.isPresent()) {
            engine.putCache(ast, relational);
            return relational;
          }
        }

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

        // Apply initial standard substitutions
        IExpr temp = F.subst(arg1, F.list( //
            F.Rule(S.GoldenAngle, F.Times(F.Subtract(F.C3, F.CSqrt5), S.Pi)), //
            F.Rule(S.GoldenRatio, F.Times(F.C1D2, F.Plus(F.C1, F.CSqrt5))), //
            F.Rule(S.Degree, F.Divide(S.Pi, F.ZZ(180))) //
        ));

        if (temp.isPresent()) {
          arg1 = temp;
        }

        if (arg1.isDirectedInfinity() && ((IAST) arg1).isAST1()) {
          IExpr normalized =
              normalizeDirectedInfinity((IAST) arg1, complexityFunction, minCounter, engine);
          if (normalized.isPresent()) {
            engine.putCache(ast, normalized);
            return normalized;
          }
        }

        // run the standard algebraic simplify steps first
        temp = SimplifyUtil.simplifyStep(arg1, defaultResult, complexityFunction, minCounter,
            isFullSimplifyMode() || isVariableFreeNumber(arg1), false, engine);

        IExpr currentResult = temp.isPresent() ? temp : defaultResult;

        // The Fu trigonometric search factors and expands the whole expression several times over,
        // which is prohibitive for a large one: 800ms for a third derivative of
        // E^Sin(x)*Log(1+x^2)/Cosh(x) before it ran into the AST size limit. Its wins are the
        // contractions of small sums, so it gets the same bound as the factorization step.
        if (currentResult.isAST()
            && currentResult.leafCount() < Config.MAX_SIMPLIFY_FACTOR_LEAFCOUNT) {
          try {
            IExpr trigTemp =
                TrigSimplifyFu.simplify((IAST) currentResult, engine, complexityFunction);
            if (trigTemp.isPresent() && !trigTemp.equals(currentResult)) {
              long trigCount = complexityFunction.apply(trigTemp);
              if (trigCount < minCounter) {
                minCounter = trigCount;
                temp = trigTemp;
              }
            }
          } catch (LimitException le) {
            // the trigonometric candidate hit an evaluation limit; keep what the pipeline found
          }
        }

        if (temp.isPresent()) {
          engine.putCache(ast, temp);
          return temp;
        }

      } catch (ArithmeticException e) {
        // Fall back gracefully
      } finally {
        engine.setAssumptions(oldAssumptions);
      }

      return F.NIL;
    }

    /**
     * Decide a relation by moving everything to the left-hand-side and testing <code>lhs-rhs</code>
     * against <code>0</code>. That is how an identity such as <code>x^2-y^2 == (x+y)*(x-y)</code>
     * is proved.
     *
     * <p>
     * The rewritten form is only used when it actually decides the relation, or when it is simpler
     * than what we started with. Returning it unconditionally — as this did before — turns
     * <code>Simplify(x==y)</code> into the heavier <code>x-y==0</code>.
     *
     * @param relation the relation to simplify
     * @param ast the surrounding <code>Simplify(...)</code> call, reused so that its options carry
     *        over to the subtraction
     * @param complexityFunction weighs the rewritten form against the original
     * @param engine the evaluation engine
     * @return the decided or simpler relation, or {@link F#NIL} to leave it to the normal pipeline
     */
    private static IExpr simplifyRelational(IAST relation, IAST ast,
        Function<IExpr, Long> complexityFunction, EvalEngine engine) {
      switch (relation.headID()) {
        case ID.Equal:
        case ID.Unequal:
        case ID.Greater:
        case ID.GreaterEqual:
        case ID.Less:
        case ID.LessEqual:
          break;
        default:
          return F.NIL;
      }
      if (relation.size() != 3 || relation.arg2().isZero()) {
        return F.NIL;
      }
      IExpr difference =
          engine.evaluate(ast.setAtClone(1, F.Subtract(relation.arg1(), relation.arg2())));
      IExpr rewritten = engine.evaluate(F.binaryAST2(relation.head(), difference, F.C0));
      if (rewritten.isTrue() || rewritten.isFalse()) {
        return rewritten;
      }
      return complexityFunction.apply(rewritten) < complexityFunction.apply(relation) //
          ? rewritten
          : F.NIL;
    }

    /**
     * A variable-free expression is a number waiting to be recognized, and the rewrites that
     * recognize it — <code>FunctionExpand</code>, <code>TrigToExp</code>, <code>ExpToTrig</code>,
     * &hellip; — are the ones only <code>FullSimplify</code> runs. Plain <code>Simplify</code>
     * already got at them, but by accident and at a ruinous price: it offers <code>Apart</code> as
     * a rewrite candidate, and <code>Apart</code> of a variable-free product used to start a whole
     * nested <code>FullSimplify</code> — once per node and per fixpoint pass. Doing it here instead
     * is the same reach for a fraction of the work:
     * <code>Simplify(2*Cos(Pi/180*(60+3*Tan(Pi/180*(45-2*Sin(Pi/60))))))</code> went from 391ms to
     * 213ms.
     *
     * @param expr the expression to be simplified
     * @return <code>true</code> if <code>expr</code> is a variable-free number small enough to be
     *         worth the extra rewrites
     */
    private static boolean isVariableFreeNumber(IExpr expr) {
      return expr.leafCount() < Config.MAX_SIMPLIFY_APART_LEAFCOUNT && expr.isNumericFunction();
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

    /**
     * Normalize the direction of a <code>DirectedInfinity[dir]</code> expression by rewriting it as
     * <code>DirectedInfinity[Sign[Factor[dir]]]</code>. <code>Factor</code> turns a symbolic
     * <code>Plus</code> direction into a product over which <code>Sign</code> distributes, e.g.
     * <code>DirectedInfinity((-I*3/2*y)/E + I*3/2*E*y)</code> becomes
     * <code>DirectedInfinity(I*Sign(y))</code>.
     *
     * @param directedInfinity a <code>DirectedInfinity[dir]</code> AST (must satisfy
     *        {@code isAST1()})
     * @param complexityFunction the &ldquo;weight&rdquo; function used to gate the rewrite
     * @param minCounter the complexity of the original expression
     * @param engine the evaluation engine
     * @return the simpler normalized <code>DirectedInfinity</code> or {@link F#NIL}
     */
    private static IExpr normalizeDirectedInfinity(IAST directedInfinity,
        Function<IExpr, Long> complexityFunction, long minCounter, EvalEngine engine) {
      IExpr dir = directedInfinity.arg1();
      try {
        IExpr normalizedDir = engine.evaluate(F.Sign(F.Factor(dir)));
        if (normalizedDir.isPresent() && !normalizedDir.isAST(S.Sign)) {
          IExpr candidate = engine.evaluate(F.DirectedInfinity(normalizedDir));
          if (candidate.isDirectedInfinity() && complexityFunction.apply(candidate) < minCounter) {
            return candidate;
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
      }
      return F.NIL;
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
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private SimplifyFunctions() {}
}
