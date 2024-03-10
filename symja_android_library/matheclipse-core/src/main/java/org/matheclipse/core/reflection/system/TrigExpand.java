package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.Binomial;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.evalExpandAll;
import java.util.function.Function;
import org.matheclipse.core.builtin.Algebra;
import org.matheclipse.core.builtin.StructureFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.visit.VisitorPlusTimesPowerReplaceAll;

/**
 *
 *
 * <pre>
 * TrigExpand(expr)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * expands out trigonometric expressions in <code>expr</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; TrigExpand(Sin(x+y))
 * Cos(x)*Sin(y)+Cos(y)*Sin(x)
 * </pre>
 */
public class TrigExpand extends AbstractEvaluator {

  public static final Function<IExpr, IExpr> TRIG_EXPAND_FUNCTION = new TrigExpandFunction();

  private static final VisitorPlusTimesPowerReplaceAll TRIG_EXPAND_VISITOR =
      new VisitorPlusTimesPowerReplaceAll(TRIG_EXPAND_FUNCTION);

  private static final class TrigExpandFunction implements Function<IExpr, IExpr> {
    @Override
    public IExpr apply(IExpr ast) {
      if (ast.isAST1()) {
        IExpr first = ast.first();
        if (first.isPlus()) {
          IExpr temp = expandPlus((IAST) ast, (IAST) first);
          if (temp.isPresent()) {
            if (temp.isPlus()) {
              temp = ((IAST) temp).map(S.Plus, x -> distributeTimes(x));
            }
            return temp;
          }
        } else if (first.isTimes()) {
          return expandTimes((IAST) ast, (IAST) first);
        }
      }
      return F.NIL;
    }

    private static IExpr distributeTimes(IExpr expr) {
      return Algebra.distributeTimes(expr);
    }

    /**
     * Expand <code>f(a+b+c+...)</code> and create a trig function.
     *
     * @param ast
     * @param plusAST
     * @return
     */
    private IExpr expandPlus(IAST ast, IAST plusAST) {
      if (ast.isSin()) {
        return expandCosSinPlus(plusAST, false);
      } else if (ast.isCos()) {
        return expandCosSinPlus(plusAST, true);
        // return expandCosPlus(plusAST, 1);
      } else if (ast.isAST(S.Cot, 2)) {
        // Cos(x) / Sin(x)
        return F.Divide(expandCosSinPlus(plusAST, true), expandCosSinPlus(plusAST, false));
      } else if (ast.isTan()) {
        // Sin(x) / Cos(x)
        return F.Divide(expandCosSinPlus(plusAST, false), expandCosSinPlus(plusAST, true));
      } else if (ast.isAST(S.Csc, 2)) {
        // 1 / Sin(x)
        return expandCosSinPlus(plusAST, false).inverse();
      } else if (ast.isAST(S.Sec, 2)) {
        // 1 / Cos(x)
        return expandCosSinPlus(plusAST, true).inverse();
      } else if (ast.isAST(S.Sech, 2)) {
        return expandSechPlus(plusAST, 1);
      } else if (ast.isSinh()) {
        return expandCoshSinhPlus(plusAST, false);
      } else if (ast.isCosh()) {
        return expandCoshSinhPlus(plusAST, true);
      } else if (ast.isAST(S.Csch, 2)) {
        return expandCschPlus(plusAST, 1);
      } else if (ast.isAST(S.Coth, 2)) {
        // Cosh(x) / Sinh(x)
        return F.Divide(expandCoshSinhPlus(plusAST, true), expandCoshSinhPlus(plusAST, false));
      } else if (ast.isTanh()) {
        // Sinh(x) / Cosh(x)
        return F.Divide(expandCoshSinhPlus(plusAST, false), expandCoshSinhPlus(plusAST, true));
      }
      return F.NIL;
    }

    /**
     * Expand <code>f(n*theta)</code> and create a trig function.
     *
     * @param ast
     * @param timesAST
     * @return
     */
    private IExpr expandTimes(IAST ast, IAST timesAST) {
      if (timesAST.arg1().isInteger()) {
        IInteger n = (IInteger) timesAST.arg1();
        if (n.isPositive()) {
          try {
            IExpr theta = timesAST.rest().oneIdentity1();
            if (ast.isSin()) {
              return expandSinTimes(n, theta);
            } else if (ast.isCos()) {
              return expandCosTimes(n, theta);
            } else if (ast.isAST(S.Cot, 2)) {
              // Cos(x) / Sin(x)
              return F.Divide(expandCosTimes(n, theta), expandSinTimes(n, theta));
            } else if (ast.isTan()) {
              // Sin(x) / Cos(x)
              return F.Divide(expandSinTimes(n, theta), expandCosTimes(n, theta));
            } else if (ast.isAST(S.Csc, 2)) {
              // 1 / Sin(x)
              return expandSinTimes(n, theta).inverse();
            } else if (ast.isAST(S.Sec, 2)) {
              // 1 / Cos(x)
              return expandCosTimes(n, theta).inverse();
            } else if (ast.isSinh()) {
              int nInt = n.toInt();
              // return expandSinhPlus(F.constantArray(F.Plus, theta, nInt), 1);
              return expandCoshSinhPlus(theta.constantArray(S.Plus, 0, nInt), false);
            } else if (ast.isCosh()) {
              int nInt = n.toInt();
              // return expandCoshPlus(F.constantArray(F.Plus, theta, nInt), 1);
              return expandCoshSinhPlus(theta.constantArray(S.Plus, 0, nInt), true);
            } else if (ast.isAST(S.Csch, 2)) {
              // Csch(theta)/ChebyshevU(n - 1, Cosh(theta))
              return F.TrigExpand(F.Times(F.Csch(theta),
                  F.Power(F.ChebyshevU(F.Subtract(n, F.C1), F.Cosh(theta)), F.CN1)));
              // int nInt = n.toInt();
              // I^(n - 1)*2^(1 - n)* Product(Csch(theta + (I*k*Pi)/n], {k, 0, n - 1})
              // return F.Times(F.Power(F.C2, F.Plus(F.C1, F.Negate(n))), F.Power(F.CI,
              // F.Plus(F.CN1, n)),
              // F.Product(F.Csch(F.Plus(theta, F.Times(F.CI, F.k, F.Power(n, -1), F.Pi))),
              // F.List(F.k, F.C0, F.Plus(F.CN1, n))));
            } else if (ast.isAST(S.Sech, 2)) {
              int nInt = n.toInt();
              return expandSechPlus(theta.constantArray(S.Plus, 0, nInt), 1);
            }
          } catch (ArithmeticException ae) {

          }
        }
      }
      return F.NIL;
    }

    /**
     * <code>Cos(n*theta)</code>
     *
     * @param n
     * @param theta
     * @return
     */
    private static IExpr expandCosTimes(IInteger n, IExpr theta) {
      int ni = n.toIntDefault();
      if (ni > Integer.MIN_VALUE) {
        return F.sum(i -> Times(Times(Times(Power(CN1, Times(i, C1D2)), Binomial(n, i)),
            Power(Cos(theta), Plus(n, Times(CN1, i)))), Power(Sin(theta), i)), 0, ni, 2);
      }
      return F.NIL;
    }

    /**
     * <code>Sin(n*theta)</code>
     *
     * @param n
     * @param theta
     * @return
     */
    private static IExpr expandSinTimes(IInteger n, IExpr theta) {
      int ni = n.toIntDefault();
      if (ni > Integer.MIN_VALUE) {
        return F.sum(i -> Times(Times(Times(Power(CN1, Times(Plus(i, CN1), C1D2)), Binomial(n, i)),
            Power(Cos(theta), Plus(n, Times(CN1, i)))), Power(Sin(theta), i)), 1, ni, 2);
      }
      return F.NIL;
    }

    /**
     * <code>if (coshResult==true) then TrigExand(Cosh(a+b+c+...))  else TrigExand(Sinh(a+b+c+...))</code>
     * 
     * @param plusAST
     * @param coshResult
     * @return
     */
    private static IExpr expandCoshSinhPlus(IAST plusAST, boolean coshResult) {
      IExpr lhs = plusAST.arg1();
      IExpr rhs = plusAST.arg2();
      // Cosh(lhs)*Cosh(rhs) + Sinh(lhs)*Sinh(rhs)
      IExpr coshPlus = F.Plus(Times(F.Cosh(lhs), F.Cosh(rhs)), Times(F.Sinh(lhs), F.Sinh(rhs)));
      // Sinh(lhs)*Cosh(rhs) + Cosh(lhs)*Sinh(rhs)
      IExpr sinhPlus = F.Plus(Times(F.Sinh(lhs), F.Cosh(rhs)), Times(F.Cosh(lhs), F.Sinh(rhs)));
      for (int i = 3; i < plusAST.size(); i++) {
        lhs = plusAST.get(i);
        IExpr coshTemp = F.Plus(distributeTimes(Times(F.Cosh(lhs), coshPlus)),
            distributeTimes(Times(F.Sinh(lhs), sinhPlus)));
        IExpr sinhTemp = F.Plus(distributeTimes(Times(F.Sinh(lhs), coshPlus)),
            distributeTimes(Times(F.Cosh(lhs), sinhPlus)));
        coshPlus = coshTemp;
        sinhPlus = sinhTemp;
      }
      return coshResult ? coshPlus : sinhPlus;
    }

    /**
     * <code>if (cosResult==true) then TrigExand(Cos(a+b+c+...))  else TrigExand(Sin(a+b+c+...))</code>
     * 
     * @param plusAST
     * @param cosResult
     * @return
     */
    private static IExpr expandCosSinPlus(IAST plusAST, boolean cosResult) {
      IExpr lhs = plusAST.arg1();
      IExpr rhs = plusAST.arg2();
      IExpr cosPlus = F.Plus(Times(Cos(lhs), Cos(rhs)), Times(CN1, Sin(lhs), Sin(rhs)));
      IExpr sinPlus = F.Plus(Times(Sin(lhs), Cos(rhs)), Times(Cos(lhs), Sin(rhs)));
      for (int i = 3; i < plusAST.size(); i++) {
        lhs = plusAST.get(i);
        IExpr cosTemp = F.Plus(distributeTimes(Times(F.Cos(lhs), cosPlus)),
            distributeTimes(Times(CN1, Sin(lhs), sinPlus)));
        IExpr sinTemp = F.Plus(distributeTimes(Times(F.Sin(lhs), cosPlus)),
            distributeTimes(Times(F.Cos(lhs), sinPlus)));
        cosPlus = cosTemp;
        sinPlus = sinTemp;
      }
      return cosResult ? cosPlus : sinPlus;
    }

    /**
     * <code>Csch(a+b+c+...)</code>
     *
     * @param plusAST
     * @param startPosition
     * @return
     */
    private static IExpr expandCschPlus(IAST plusAST, int startPosition) {
      IExpr a = plusAST.get(startPosition);
      IExpr b;
      if (startPosition == plusAST.size() - 2) {
        // Csch[a + b] --> 1/(Cosh(b)*Sinh(a) + Cosh(a)*Sinh(b))
        b = plusAST.get(startPosition + 1);
      } else {
        // b = expandCschPlus(plusAST, startPosition + 1);
        return F.NIL;
      }
      return F.eval(F.Plus(
          F.Power(F.Plus(F.Times(F.Cosh(b), F.Sinh(a)), F.Times(F.Cosh(a), F.Sinh(b))), F.CN1)));
    }

    /**
     * <code>Sech(a+b+c+...)</code>
     *
     * @param plusAST
     * @param startPosition
     * @return
     */
    private static IExpr expandSechPlus(IAST plusAST, int startPosition) {
      IExpr a = plusAST.get(startPosition);
      IExpr b;
      if (startPosition == plusAST.size() - 2) {
        b = plusAST.get(startPosition + 1);
      } else {
        // b = expandSechPlus(plusAST, startPosition + 1);
        return F.NIL;
      }
      // Sech(a + b) --> 1/(Cosh(b)*Cosh(a) + Sinh(a)*Sinh(b))
      return F.eval(F.Plus(
          F.Power(F.Plus(F.Times(F.Cosh(b), F.Cosh(a)), F.Times(F.Sinh(a), F.Sinh(b))), F.CN1)));
    }

  }

  public TrigExpand() {}

  /**
   * Expands the argument of sine and cosine functions.
   *
   * <p>
   * <a href="http://en.wikipedia.org/wiki/List_of_trigonometric_identities" >List of trigonometric
   * identities</a>
   */
  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr temp = StructureFunctions.threadListLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }

    IExpr result = evalExpandAll(ast.arg1(), engine);
    temp = result.accept(TRIG_EXPAND_VISITOR);
    if (temp.isNIL()) {
      return ast.arg1();
    }
    do {
      result = evalExpandAll(temp, engine);
      temp = result.accept(TRIG_EXPAND_VISITOR);
    } while (temp.isPresent());
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
}
