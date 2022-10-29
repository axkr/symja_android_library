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
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
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
      if (expr.isTimes()) {
        return Algebra.distributeTimes(expr);
      }
      return expr;
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
        return expandSinPlus(plusAST, 1);
      } else if (ast.isCos()) {
        return expandCosPlus(plusAST, 1);
      } else if (ast.isAST(S.Cot, 2)) {
        // Cos(x) / Sin(x)
        return F.Divide(expandCosPlus(plusAST, 1), expandSinPlus(plusAST, 1));
      } else if (ast.isTan()) {
        // Sin(x) / Cos(x)
        return F.Divide(expandSinPlus(plusAST, 1), expandCosPlus(plusAST, 1));
      } else if (ast.isAST(S.Csc, 2)) {
        // 1 / Sin(x)
        return F.Divide(F.C1, expandSinPlus(plusAST, 1));
      } else if (ast.isAST(S.Sec, 2)) {
        // 1 / Cos(x)
        return F.Divide(F.C1, expandCosPlus(plusAST, 1));
      } else if (ast.isAST(S.Sech, 2)) {
        return expandSechPlus(plusAST, 1);
      } else if (ast.isSinh()) {
        return expandSinhPlus(plusAST, 1);
      } else if (ast.isCosh()) {
        return expandCoshPlus(plusAST, 1);
      } else if (ast.isAST(S.Csch, 2)) {
        return expandCschPlus(plusAST, 1);
      } else if (ast.isTanh()) {
        return expandTanhPlus(plusAST, 1);
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
        if (n.compareInt(0) > 0) {
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
              return F.Divide(F.C1, expandSinTimes(n, theta));
            } else if (ast.isAST(S.Sec, 2)) {
              // 1 / Cos(x)
              return F.Divide(F.C1, expandCosTimes(n, theta));
            } else if (ast.isSinh()) {
              int nInt = n.toInt();
              // return expandSinhPlus(F.constantArray(F.Plus, theta, nInt), 1);
              return expandSinhPlus(theta.constantArray(S.Plus, 0, nInt), 1);
            } else if (ast.isCosh()) {
              int nInt = n.toInt();
              // return expandCoshPlus(F.constantArray(F.Plus, theta, nInt), 1);
              return expandCoshPlus(theta.constantArray(S.Plus, 0, nInt), 1);
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
     * <code>Sin(a+b+c+...)</code>
     *
     * @param plusAST
     * @param startPosition
     * @return
     */
    private static IExpr expandSinPlus(IAST plusAST, int startPosition) {
      IASTAppendable result = F.PlusAlloc(2);
      IExpr lhs = plusAST.get(startPosition);
      if (startPosition == plusAST.size() - 2) {
        IExpr rhs = plusAST.get(startPosition + 1);
        result.append(Times(Sin(lhs), Cos(rhs)));
        result.append(Times(Cos(lhs), Sin(rhs)));
      } else {
        result.append(Times(Sin(lhs), expandCosPlus(plusAST, startPosition + 1)));
        result.append(Times(Cos(lhs), expandSinPlus(plusAST, startPosition + 1)));
      }
      return result;
    }

    /**
     * <code>Sinh(a+b+c+...)</code>
     *
     * @param plusAST
     * @param startPosition
     * @return
     */
    private static IExpr expandSinhPlus(IAST plusAST, int startPosition) {
      IASTAppendable result = F.PlusAlloc(2);
      IExpr lhs = plusAST.get(startPosition);
      if (startPosition == plusAST.size() - 2) {
        // Sinh(x)*Cosh(y) + Cosh(x)*Sinh(y)
        IExpr rhs = plusAST.get(startPosition + 1);
        result.append(Times(F.Sinh(lhs), F.Cosh(rhs)));
        result.append(Times(F.Cosh(lhs), F.Sinh(rhs)));
      } else {
        result.append(Times(F.Sinh(lhs), expandCoshPlus(plusAST, startPosition + 1)));
        result.append(Times(F.Cosh(lhs), expandSinhPlus(plusAST, startPosition + 1)));
      }
      return result;
    }

    /**
     * <code>Sin(a+b+c+...)</code>
     *
     * @param plusAST
     * @param startPosition
     * @return
     */
    private static IExpr expandCosPlus(IAST plusAST, int startPosition) {
      IASTAppendable result = F.PlusAlloc(2);
      IExpr lhs = plusAST.get(startPosition);
      if (startPosition == plusAST.size() - 2) {
        IExpr rhs = plusAST.get(startPosition + 1);
        result.append(Times(Cos(lhs), Cos(rhs)));
        result.append(Times(CN1, Sin(lhs), Sin(rhs)));
      } else {
        result.append(Times(Cos(lhs), expandCosPlus(plusAST, startPosition + 1)));
        result.append(Times(CN1, Sin(lhs), expandSinPlus(plusAST, startPosition + 1)));
      }
      return result;
    }

    /**
     * <code>Cosh(a+b+c+...)</code>
     *
     * @param plusAST
     * @param startPosition
     * @return
     */
    private static IExpr expandCoshPlus(IAST plusAST, int startPosition) {
      IASTAppendable result = F.PlusAlloc(2);
      IExpr lhs = plusAST.get(startPosition);
      if (startPosition == plusAST.size() - 2) {
        // Cosh(x)*Cosh(y) + Sinh(x)*Sinh(y)
        IExpr rhs = plusAST.get(startPosition + 1);
        result.append(Times(F.Cosh(lhs), F.Cosh(rhs)));
        result.append(Times(F.Sinh(lhs), F.Sinh(rhs)));
      } else {
        result.append(Times(F.Cosh(lhs), expandCoshPlus(plusAST, startPosition + 1)));
        result.append(Times(F.Sinh(lhs), expandSinhPlus(plusAST, startPosition + 1)));
      }
      return result;
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

    /**
     * <code>Tanh(a+b+c+...)</code>
     *
     * @param plusAST
     * @param startPosition
     * @return
     */
    private static IExpr expandTanhPlus(IAST plusAST, int startPosition) {
      IASTAppendable result = F.TimesAlloc(2);
      IExpr lhs = plusAST.get(startPosition);
      if (startPosition == plusAST.size() - 2) {
        // (Tanh(x)+Tanh(y)) / (1+Tanh(x)*Tanh(y))
        IExpr rhs = plusAST.get(startPosition + 1);
        result.append(Plus(F.Tanh(lhs), F.Tanh(rhs)));
        result.append(F.Power(Plus(F.C1, Times(F.Tanh(lhs), F.Tanh(rhs))), F.CN1));
      } else {
        result.append(Plus(F.Tanh(lhs), expandTanhPlus(plusAST, startPosition + 1)));
        result.append(F.Power(
            Plus(F.C1, Times(F.Tanh(lhs), expandTanhPlus(plusAST, startPosition + 1))), F.CN1));
      }
      return result;
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
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }
}
