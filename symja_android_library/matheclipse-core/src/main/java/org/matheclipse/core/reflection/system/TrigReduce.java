package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.y_;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.y;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcher;
import org.matheclipse.core.patternmatching.hash.HashedOrderlessMatcherPlus;
import org.matheclipse.core.visit.VisitorExpr;

/**
 *
 *
 * <pre>
 * TrigReduce(expr)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * rewrites products and powers of trigonometric functions in <code>expr</code> in terms of
 * trigonometric functions with combined arguments.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; TrigReduce(2*Sin(x)*Cos(y))
 * Sin(-y+x)+Sin(y+x)
 * </pre>
 */
public class TrigReduce extends AbstractEvaluator {
  // don't use HashedOrderlessMatcherTimes - powers of trig functions are handled separately
  private static HashedOrderlessMatcher TIMES_MATCHER = new HashedOrderlessMatcher();

  private static HashedOrderlessMatcher PLUS_MATCHER = new HashedOrderlessMatcherPlus();

  public TrigReduce() {}

  static class TrigReduceVisitor extends VisitorExpr {
    final EvalEngine fEngine;

    public TrigReduceVisitor(EvalEngine engine) {
      super();
      fEngine = engine;
    }

    @Override
    public IExpr visit(IASTMutable ast) {
      if (ast.isPlus()) {
        IAST result = PLUS_MATCHER.evaluate(ast, fEngine);
        if (result.isPresent()) {
          return result;
        }
      } else if (ast.isTimes()) {
        IExpr temp = visitTimes(ast);
        if (temp.isPresent()) {
          return temp;
        }
      } else if (ast.isPower()) {
        if (ast.base().isAST()) {
          IExpr temp = visitPower(ast);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
      return visitAST(ast);
    }

    private IExpr visitPower(IASTMutable ast) {
      int n = ast.exponent().toIntDefault();
      if (n > 1) {
        IAST base = (IAST) ast.base();
        if (base.isCos()) {
          return powerCos(base.arg1(), n);
        } else if (base.isCosh()) {
          return powerCosh(base.arg1(), n);
        } else if (base.isSinh()) {
          return powerSinh(base.arg1(), n);
        } else if (base.isSin()) {
          return powerSin(base.arg1(), n);
        } else if (base.isAST(S.Cot, 2)) {
          IExpr trigReduceSinPower = powerSin(base.arg1(), n);
          IExpr trigReduceCosPower = powerCos(base.arg1(), n);
          return F.Times(trigReduceCosPower, F.Power(trigReduceSinPower, F.CN1));
        } else if (base.isTan()) {
          IExpr trigReduceSinPower = powerSin(base.arg1(), n);
          IExpr trigReduceCosPower = powerCos(base.arg1(), n);
          return F.Times(trigReduceSinPower, F.Power(trigReduceCosPower, F.CN1));
        } else if (base.isAST(S.Csc, 2)) {
          IExpr trigReduceSinPower = powerSin(base.arg1(), n);
          return F.Power(trigReduceSinPower, F.CN1);
        } else if (base.isAST(S.Sec, 2)) {
          IExpr trigReduceCosPower = powerCos(base.arg1(), n);
          return F.Power(trigReduceCosPower, F.CN1);
        } else if (base.isAST(S.Coth, 2)) {
          IExpr trigReduceSinhPower = powerSinh(base.arg1(), n);
          IExpr trigReduceCoshPower = powerCosh(base.arg1(), n);
          return F.Times(trigReduceCoshPower, F.Power(trigReduceSinhPower, F.CN1));
        } else if (base.isAST(S.Csch, 2)) {
          IExpr trigReduceSinhPower = powerSinh(base.arg1(), n);
          return F.Power(trigReduceSinhPower, F.CN1);
        } else if (base.isAST(S.Sech, 2)) {
          IExpr trigReduceCoshPower = powerCosh(base.arg1(), n);
          return F.Power(trigReduceCoshPower, F.CN1);
        } else if (base.isTanh()) {
          IExpr trigReduceSinhPower = powerSinh(base.arg1(), n);
          IExpr trigReduceCoshPower = powerCosh(base.arg1(), n);
          return F.Times(trigReduceSinhPower, F.Power(trigReduceCoshPower, F.CN1));
        }
      }
      return F.NIL;
    }

    private IExpr visitTimes(IASTMutable ast) {
      IBuiltInSymbol head = S.Times;
      IAST flattened = TrigExpand.rewriteCircularHyperbolicOrderless(ast, head);
      if (flattened.isTimes()) {
        IExpr[] fractionParts = AlgebraUtil.numeratorDenominator(flattened, false, fEngine);
        if (fractionParts != null) {
          boolean evaled = false;
          IExpr p0 = fractionParts[0];
          if (fractionParts[0].isTimes()) {
            IExpr temp = timesMatcherEvalLoop((IAST) fractionParts[0]);
            if (temp.isPresent()) {
              p0 = temp;
              evaled = true;
            }
          }
          IExpr p1 = fractionParts[1];
          if (fractionParts[1].isTimes()) {
            IExpr temp = timesMatcherEvalLoop((IAST) fractionParts[1]);
            if (temp.isPresent()) {
              p1 = temp;
              evaled = true;
            }
          }
          if (evaled) {
            return F.Divide(p0, p1);
          }
        }
      }
      return F.NIL;
    }

    private IExpr timesMatcherEvalLoop(IAST ast) {
      IExpr temp = TIMES_MATCHER.evaluate(ast, fEngine);
      if (temp.isNIL()) {
        return F.NIL;
      }
      if (!temp.isTimes()) {
        return temp;
      }

      IAST result = (IAST) temp;
      while (result.isPresent()) {
        temp = TIMES_MATCHER.evaluate(result, fEngine);
        if (temp.isNIL()) {
          return result;
        }
        if (temp.isTimes()) {
          result = (IAST) temp;
        } else {
          return temp;
        }
      }
      return F.NIL;
    }

    private IExpr powerCos(IExpr x, int i) {
      IInteger n = F.ZZ(i);
      IExpr result = F.NIL;
      if (n.isEven()) {
        int mi = i / 2;
        IInteger m = F.ZZ(mi);
        // Sum(Binomial(n,k)*Cos((-2*k+n)*x),{k,0,-1+m})+Binomial(n,m)/2
        IExpr sum =
            F.sum(k -> F.Times(F.Binomial(n, k), F.Cos(F.Times(F.Plus(F.Times(F.CN2, k), n), x))),
                0, mi - 1);
        result = F.Plus(sum, F.Times(F.C1D2, F.Binomial(n, m)));
      } else {
        // odd case
        int mi = (i - 1) / 2;
        // Sum(Binomial(n,k)*Cos((-2*k+n)*x),{k,0,m})
        result = F.sum(
            k -> F.Times(F.Binomial(n, k), F.Cos(F.Times(F.Plus(F.Times(F.CN2, k), n), x))), 0, mi);

      }
      // result/2^(-1+n)
      IRational inverse = F.C2.powerRational(i - 1).inverse();
      return fEngine.evaluate(F.Times(inverse, result));
    }

    private IExpr powerCosh(IExpr x, int i) {
      IInteger n = F.ZZ(i);
      IExpr result = F.NIL;
      if (n.isEven()) {
        int mi = i / 2;
        IInteger m = F.ZZ(mi);
        // Sum(Binomial(n,k)*Cosh((-2*k+n)*x),{k,0,-1+m})+Binomial(n,m)/2
        IExpr sum =
            F.sum(k -> F.Times(F.Binomial(n, k), F.Cosh(F.Times(F.Plus(F.Times(F.CN2, k), n), x))),
                0, mi - 1);
        result = F.Plus(sum, F.Times(F.C1D2, F.Binomial(n, m)));


      } else {
        // odd case
        int mi = (i - 1) / 2;
        // Sum(Binomial(n,k)*Cosh((-2*k+n)*x),{k,0,m})
        result =
            F.sum(k -> F.Times(F.Binomial(n, k), F.Cosh(F.Times(F.Plus(F.Times(F.CN2, k), n), x))),
                0, mi);
      }
      // result/2^(-1+n)
      IRational inverse = F.C2.powerRational(i - 1).inverse();
      return fEngine.evaluate(F.Times(inverse, result));
    }

    private IExpr powerSin(IExpr x, int i) {
      IInteger n = F.ZZ(i);
      IExpr result = F.NIL;
      if (n.isEven()) {
        int mi = i / 2;
        IInteger m = F.ZZ(mi);
        // Sum(Binomial(n,k)/(-1)^(k-m)*Cos((-2*k+n)*x),{k,0,-1+m})+(-1)^m/2*Binomial(n,m)
        IExpr sum = F.sum(k -> F.Times(F.Power(-1, F.Plus(F.Negate(k), m)), F.Binomial(n, k),
            F.Cos(F.Times(F.Plus(F.Times(F.CN2, k), n), x))), 0, mi - 1);
        result = F.Plus(sum, F.Times(F.C1D2, F.Binomial(n, m)));
      } else {
        // odd case
        int mi = (i - 1) / 2;
        IInteger m = F.ZZ(mi);
        // Sum(Binomial(n,k)/(-1)^(k-m)*Sin((-2*k+n)*x),{k,0,m})
        result = F.sum(k -> F.Times(F.Power(-1, F.Plus(F.Negate(k), m)), F.Binomial(n, k),
            F.Sin(F.Times(F.Plus(F.Times(F.CN2, k), n), x))), 0, mi);
      }
      // result/2^(-1+n)
      IRational inverse = F.C2.powerRational(i - 1).inverse();
      return fEngine.evaluate(F.Times(inverse, result));
    }

    private IExpr powerSinh(IExpr x, int i) {
      IInteger n = F.ZZ(i);
      IExpr result = F.NIL;
      if (n.isEven()) {
        int mi = i / 2;
        IInteger m = F.ZZ(mi);
        // Sum((-1)^k*Binomial(n,k)*Cosh((-2*k+n)*x),{k,0,-1+m})+(-1)^m/2*Binomial(n,m)
        IExpr sum = F.sum(k -> F.Times(F.Power(-1, k), F.Binomial(n, k),
            F.Cosh(F.Times(F.Plus(F.Times(F.CN2, k), n), x))), 0, mi - 1);
        result = F.Plus(sum, F.Times(F.C1D2, F.Power(-1, m), F.Binomial(n, m)));
      } else {
        // odd case
        int mi = (i - 1) / 2;
        IInteger m = F.ZZ(mi);
        // Sum((-1)^k*Binomial(n,k)*Sinh((-2*k+n)*x),{k,0,m})
        result = F.sum(k -> F.Times(F.Power(-1, k), F.Binomial(n, k),
            F.Sinh(F.Times(F.Plus(F.Times(F.CN2, k), n), x))), 0, mi);
      }
      // result/2^(-1+n)
      IRational inverse = F.C2.powerRational(i - 1).inverse();
      return fEngine.evaluate(F.Times(inverse, result));
    }

  }

  /**
   * Transform products of trigonometric functions into &quot;linear form&quot;.
   *
   * <p>
   * <a href=
   * "http://en.wikipedia.org/wiki/List_of_trigonometric_identities#Product-to-sum_and_sum-to-product_identities"
   * >List of trigonometric identities - Product-to-sum and sum-to-product identities</a>
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr temp = CompareUtil.threadListLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }

    return trigReduce(ast.arg1(), engine);
  }

  public static IExpr trigReduce(IExpr temp, EvalEngine engine) {
    IExpr result = temp;
    if (result.isAST()) {
      TrigReduceVisitor trigReduceVisitor = new TrigReduceVisitor(engine);
      IExpr lastResult = result;
      while (temp.isPresent()) {
        result = temp;
        if (temp.isPower()) {
          result = F.evalExpand(temp);
        } else if (temp.isPlus()) {
          result = F.evalExpand(temp);
          lastResult = result;
        } else if (temp.isTimes()) {
          if (temp.first().isNumber()) {
            result = F.Times(temp.first(), F.evalExpand(temp.rest()));
          } else {
            result = F.evalExpand(temp);
          }
        }

        temp = result.accept(trigReduceVisitor);
        if (temp.isPresent()) {
          lastResult = temp;
          result = temp;
        }
      }
      return engine.evaluate(lastResult);
    }
    return result;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    PLUS_MATCHER.defineHashRule(F.Cot(x_), F.Cot(y_),
        // Csc(x)*Csc(y)*Sin(x+y)
        F.Times(F.Csc(x), F.Csc(y), F.Sin(F.Plus(x, y))));
    PLUS_MATCHER.defineHashRule(F.Cot(x_), F.Tan(y_),
        // Cos(x-y)*Csc(x)*Sec(y)
        F.Times(F.Cos(F.Subtract(x, y)), F.Csc(x), F.Sec(y)));
    PLUS_MATCHER.defineHashRule(F.Tan(x_), F.Tan(y_),
        // Sec(x)*Sec(y)*Sin(x+y)
        F.Times(F.Sec(x), F.Sec(y), F.Sin(F.Plus(x, y))));

    PLUS_MATCHER.defineHashRule(F.Coth(x_), F.Coth(y_),
        // Csch(x)*Csch(y)*Sinh(x+y)
        F.Times(F.Csch(x), F.Csch(y), F.Sinh(F.Plus(x, y))));
    PLUS_MATCHER.defineHashRule(F.Coth(x_), F.Tanh(y_),
        // Cosh(x+y)*Csch(x)*Sech(y)
        F.Times(F.Cosh(F.Plus(x, y)), F.Csch(x), F.Sech(y)));
    PLUS_MATCHER.defineHashRule(F.Tanh(x_), F.Tanh(y_),
        // Sech(x)*Sech(y)*Sinh(x+y)
        F.Times(F.Sech(x), F.Sech(y), F.Sinh(F.Plus(x, y))));

    TIMES_MATCHER.defineHashRule(Sin(x_), Cos(y_),
        // 1/2 * (Sin(x+y)+Sin(x-y))
        F.Times(F.C1D2, F.Plus(F.Sin(F.Plus(x, y)), F.Sin(F.Subtract(x, y)))));
    TIMES_MATCHER.defineHashRule(Cos(x_), Cos(y_),
        // 1/2 * (Cos(x+y)+Cos(x-y))
        F.Times(F.C1D2, F.Plus(F.Cos(F.Plus(x, y)), F.Cos(F.Subtract(x, y)))));
    TIMES_MATCHER.defineHashRule(Sin(x_), Sin(y_),
        // 1/2 * (Cos(x-y)-Cos(x+y))
        F.Times(F.C1D2, F.Subtract(F.Cos(F.Subtract(x, y)), F.Cos(F.Plus(x, y)))));

    TIMES_MATCHER.defineHashRule(Sinh(x_), Cosh(y_),
        // 1/2*(Sinh(x-y)+Sinh(x+y))
        F.Times(F.C1D2, F.Plus(F.Sinh(F.Subtract(x, y)), F.Sinh(F.Plus(x, y)))));
    TIMES_MATCHER.defineHashRule(Cosh(x_), Cosh(y_),
        // 1/2*(Cosh(x-y)+Cosh(x+y))
        F.Times(F.C1D2, F.Plus(F.Cosh(F.Subtract(x, y)), F.Cosh(F.Plus(x, y)))));
    TIMES_MATCHER.defineHashRule(Sinh(x_), Sinh(y_),
        // 1/2*(-Cosh(x-y)+Cosh(x+y))
        F.Times(F.C1D2, F.Plus(F.Negate(F.Cosh(F.Subtract(x, y))), F.Cosh(F.Plus(x, y)))));

    TIMES_MATCHER.defineHashRule(Sin(x_), Cosh(y_),
        // 1/2*(Sin(x-I*y)+Sin(x+I*y))
        F.Times(F.C1D2,
            F.Plus(F.Sin(F.Plus(x, F.Times(F.CNI, y))), F.Sin(F.Plus(x, F.Times(F.CI, y))))));
    TIMES_MATCHER.defineHashRule(Cos(x_), Cosh(y_),
        // 1/2*(Cos(x-I*y)+Cos(x+I*y))
        F.Times(F.C1D2,
            F.Plus(F.Cos(F.Plus(x, F.Times(F.CNI, y))), F.Cos(F.Plus(x, F.Times(F.CI, y))))));

    TIMES_MATCHER.defineHashRule(Sin(x_), Sinh(y_),
        // (-1/2)*I*(Cos(x-I*y)-Cos(x+I*y))
        F.Times(F.CN1D2, F.CI,
            F.Subtract(F.Cos(F.Plus(x, F.Times(F.CNI, y))), F.Cos(F.Plus(x, F.Times(F.CI, y))))));
    TIMES_MATCHER.defineHashRule(Cos(x_), Sinh(y_),
        // 1/2*I*(Sin(x-I*y)-Sin(x+I*y))
        F.Times(F.C1D2, F.CI,
            F.Subtract(F.Sin(F.Plus(x, F.Times(F.CNI, y))), F.Sin(F.Plus(x, F.Times(F.CI, y))))));
  }
}
