package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.evalExpandAll;
import java.util.function.Function;
import org.matheclipse.core.eval.AlgebraUtil;
import org.matheclipse.core.eval.CompareUtil;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
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

  private static final class TrigExpandFunction implements Function<IExpr, IExpr> {
    /**
     * <code>if (coshResult==true) then TrigExand(Cosh(a+b+c+...))  else TrigExand(Sinh(a+b+c+...))</code>
     * 
     * @param plusAST
     * @param coshResult
     * @return
     */
    private static IExpr coshSinhPlus(IAST plusAST, boolean coshResult) {
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
    private static IExpr cosSinPlus(IAST plusAST, boolean cosResult) {
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
     * <code>Cos(n*x)</code>
     *
     * @param n positive integer
     * @param x the rest of the {@link S#Times} expression
     * @return summation formula derive from de Moivre's theorem
     */
    private static IExpr cosTimes(IInteger n, IExpr x) {
      int ni = n.toIntDefault();
      if (ni > Integer.MIN_VALUE) {
        // Sum(((-1)^m*Binomial(n,2*m)*Sin(x)^(2*m))/Cos(x)^(2*m-n),{m,0,Floor(n/2)})
        return F.sum(m -> F.Times(F.Power(-1, m), F.Binomial(n, F.Times(F.C2, m)),
            F.Power(F.Cos(x), F.Plus(F.Times(F.CN2, m), n)), F.Power(F.Sin(x), F.Times(F.C2, m))),
            0, ni / 2);
      }
      return F.NIL;
    }

    /**
     * <code>Cosh(n*x)</code>
     *
     * @param n positive integer
     * @param x the rest of the {@link S#Times} expression
     * @return summation formula derive from de Moivre's theorem
     */
    private static IExpr coshTimes(IInteger n, IExpr x) {
      int ni = n.toIntDefault();
      if (ni > Integer.MIN_VALUE) {
        // Sum((Binomial(n,2*m)*Sinh(x)^(2*m))/Cosh(x)^(2*m-n),{m,0,Floor(n/2)})
        return F.sum(m -> F.Times(F.Binomial(n, F.Times(F.C2, m)),
            F.Power(F.Cosh(x), F.Plus(F.Times(F.CN2, m), n)), F.Power(F.Sinh(x), F.Times(F.C2, m))),
            0, ni / 2);

      }
      return F.NIL;
    }

    private static IExpr distributeTimes(IExpr expr) {
      return AlgebraUtil.distributeTimes(expr);
    }

    /**
     * <code>Sin(n*x)</code>
     *
     * @param n positive integer
     * @param x the rest of the {@link S#Times} expression
     * @return summation formula derive from de Moivre's theorem
     */
    private static IExpr sinTimes(IInteger n, IExpr x) {
      int ni = n.toIntDefault();
      if (ni > Integer.MIN_VALUE) {
        // Sum(((-1)^m*Binomial(n,2*m+1)*Sin(x)^(2*m+1))/Cos(x)^(2*m+1-n),{m,0,Floor(1/2*(-1+n))})
        return F.sum(m -> F.Times(F.Power(-1, m), F.Binomial(n, F.Plus(F.Times(F.C2, m), F.C1)),
            F.Power(F.Cos(x), F.Plus(F.Negate(F.Plus(F.Times(F.C2, m), F.C1)), n)),
            F.Power(F.Sin(x), F.Plus(F.Times(F.C2, m), F.C1))), 0, (ni - 1) / 2);
      }
      return F.NIL;
    }

    /**
     * <code>Sinh(n*x)</code>
     *
     * @param n positive integer
     * @param x the rest of the {@link S#Times} expression
     * @return summation formula derive from de Moivre's theorem
     */
    private static IExpr sinhTimes(IInteger n, IExpr x) {
      int ni = n.toIntDefault();
      if (ni > Integer.MIN_VALUE) {
        // Sum((Binomial(n,2*m+1)*Sinh(x)^(2*m+1))/Cosh(x)^(2*m+1-n),{m,0,Floor(1/2*(-1+n))})
        return F.sum(m -> F.Times(F.Binomial(n, F.Plus(F.Times(F.C2, m), F.C1)),
            F.Power(F.Cosh(x), F.Plus(F.Negate(F.Plus(F.Times(F.C2, m), F.C1)), n)),
            F.Power(F.Sinh(x), F.Plus(F.Times(F.C2, m), F.C1))), 0, (ni - 1) / 2);
      }
      return F.NIL;
    }

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

    /**
     * Expand <code>f(a+b+c+...)</code> and create a trig function.
     *
     * @param ast
     * @param plusAST
     * @return
     */
    private IExpr expandPlus(IAST ast, IAST plusAST) {
      if (ast.isSin()) {
        return cosSinPlus(plusAST, false);
      } else if (ast.isCos()) {
        return cosSinPlus(plusAST, true);
      } else if (ast.isSinh()) {
        return coshSinhPlus(plusAST, false);
      } else if (ast.isCosh()) {
        return coshSinhPlus(plusAST, true);
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
        final IExpr theta;
        if (n.isNegative()) {
          n = n.negate();
          theta = timesAST.setAtCopy(1, F.CN1);
        } else {
          theta = timesAST.rest().oneIdentity1();
        }
        try {
          if (ast.isSin()) {
            return sinTimes(n, theta);
          } else if (ast.isCos()) {
            return cosTimes(n, theta);
          } else if (ast.isSinh()) {
            return sinhTimes(n, theta);
          } else if (ast.isCosh()) {
            return coshTimes(n, theta);
          }
        } catch (ArithmeticException ae) {

        }
      }
      return F.NIL;
    }

  }

  public static final Function<IExpr, IExpr> TRIG_EXPAND_FUNCTION = new TrigExpandFunction();

  private static final VisitorPlusTimesPowerReplaceAll TRIG_EXPAND_VISITOR =
      new VisitorPlusTimesPowerReplaceAll(TRIG_EXPAND_FUNCTION);

  /**
   * Rewrite to simpler circular or hyperbolic functions for {@link S#Cot}, {@link S#Coth},
   * {@link S#Csc}, {@link S#Csch}, {@link S#Sec}, {@link S#Sech}, {@link S#Tan}, {@link S#Tanh}.
   * 
   * @param expr
   * @return
   */
  private static IExpr rewriteCircularHyperbolic(IExpr expr) {
    if (expr.isAST1()) {
      int headID = expr.headID();
      if (headID > -1) {
        IExpr arg1 = expr.first();
        switch (headID) {
          case ID.Cot:
            return F.Divide(F.Cos(arg1), F.Sin(arg1));
          case ID.Tan:
            return F.Divide(F.Sin(arg1), F.Cos(arg1));
          case ID.Csc:
            return F.Power(F.Sin(arg1), F.CN1);
          case ID.Sec:
            return F.Power(F.Cos(arg1), F.CN1);
          case ID.Coth:
            return F.Divide(F.Cosh(arg1), F.Sinh(arg1));
          case ID.Tanh:
            return F.Divide(F.Sinh(arg1), F.Cosh(arg1));
          case ID.Csch:
            return F.Power(F.Sinh(arg1), F.CN1);
          case ID.Sech:
            return F.Power(F.Cosh(arg1), F.CN1);
        }
      }
    }
    return F.NIL;
  }

  public static IAST rewriteCircularHyperbolicOrderless(IASTMutable orderlessAST,
      IBuiltInSymbol orderlessHead) {
    IASTMutable mutableOrderless = F.NIL;
    for (int i = 1; i < orderlessAST.size(); i++) {
      IExpr expr = orderlessAST.get(i);
      IExpr temp = rewriteCircularHyperbolic(expr);
      if (temp.isPresent()) {
        if (mutableOrderless.isNIL()) {
          mutableOrderless = orderlessAST.copy();
        }
        mutableOrderless.set(i, temp);
      }
    }
    if (mutableOrderless.isNIL()) {
      return orderlessAST;
    }
    IAST flattened = EvalAttributes.flatten(orderlessHead, mutableOrderless);
    if (!flattened.isTimes()) {
      flattened = mutableOrderless;
    }
    return flattened;
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
    IExpr temp = CompareUtil.threadListLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }

    IExpr arg1 = ast.arg1();
    IExpr result = evalExpandAll(arg1, engine);
    result = F.subst(result, x -> rewriteCircularHyperbolic(x));
    temp = result.accept(TRIG_EXPAND_VISITOR);
    if (temp.isNIL()) {
      return arg1;
    }
    do {
      result = evalExpandAll(temp, engine);
      result = F.subst(result, x -> rewriteCircularHyperbolic(x));
      temp = result.accept(TRIG_EXPAND_VISITOR);
    } while (temp.isPresent());
    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }
}
