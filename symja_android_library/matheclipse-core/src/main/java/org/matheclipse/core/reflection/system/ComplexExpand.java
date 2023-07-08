package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Times;
import java.util.List;
import org.matheclipse.core.builtin.StructureFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorExpr;

/**
 *
 *
 * <pre>
 * ComplexExpand(expr)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * get the expanded <code>expr</code>. All variable symbols in <code>expr</code> are assumed to be
 * non complex numbers.
 *
 * </blockquote>
 *
 * <p>
 * See:<br>
 *
 * <ul>
 * <li><a href="http://en.wikipedia.org/wiki/List_of_trigonometric_identities">Wikipedia - List of
 * trigonometric identities</a>
 * </ul>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; ComplexExpand(Sin(x+I*y))
 * Cosh(y)*Sin(x)+I*Cos(x)*Sinh(y)
 * </pre>
 */
public class ComplexExpand extends AbstractEvaluator {

  public ComplexExpand() {}

  static class ComplexExpandVisitor extends VisitorExpr {
    final EvalEngine fEngine;

    public ComplexExpandVisitor(EvalEngine engine) {
      super();
      fEngine = engine;
    }

    @Override
    public IExpr visit(IASTMutable ast) {
      if (ast.isTimes()) {
        IExpr expanded = F.evalExpand(ast);
        if (expanded.isPlus()) {
          return S.ComplexExpand.of(fEngine, expanded);
        }
      }

      if (ast.isPower()) {
        IExpr base = ast.base();
        IExpr exp = ast.exponent();
        IExpr baseRe = base.re();
        IExpr baseIm = base.im();

        if (exp.isRational()) {
          if (base.isInteger() && base.isNegative()) {
            IExpr exponent = ast.exponent();
            // ((base^2)^(exponent/2))
            IExpr coeff = F.Power(F.Power(base, F.C2), F.C1D2.times(exponent));
            // exponent*Arg(base)
            IExpr inner = exponent.times(F.Arg(base));
            // coeff*Cos(inner) + I*coeff*Sin(inner);
            IExpr temp = S.Expand.of(fEngine,
                F.Plus(F.Times(coeff, F.Cos(inner)), F.Times(F.CI, coeff, F.Sin(inner))));
            return temp;
          } else if (exp.isFraction()) {
            IInteger n = ((IFraction) exp).numerator();
            IInteger d = ((IFraction) exp).denominator();
            IFraction expHalf = F.QQ(n, d.multiply(2));
            // complex expand base^(n/d) with {base} in Complexes and {n,d} in Integers
            IAST baseSqrSum = F.Plus(F.Sqr(baseIm), F.Sqr(baseRe));
            return F.Plus(F.Times(F.Cos(F.Times(exp, F.Arg(base))), F.Power(baseSqrSum, expHalf)),
                F.Times(F.CI, F.Power(baseSqrSum, expHalf), F.Sin(F.Times(exp, F.Arg(base)))));
          }
        }

        IExpr expRe = exp.re();
        IExpr expIm = exp.im();
        if (baseRe.isNegative()) {
          if (baseIm.isZero()) {
            baseRe = baseRe.negate();
            return F.Plus(//
                F.Times(F.Power(baseRe, exp), F.Cos(F.Times(exp, F.Arg(base)))), //
                F.Times(F.CI, F.Power(baseRe, exp), F.Sin(F.Times(exp, F.Arg(base)))));
          }
          return F.NIL;
        }

        if (!exp.isNumber()) {
          // complex expand base^(exp) with {base,exp} in Complexes
          final IAST baseSqrSum2 = F.Plus(F.Sqr(baseIm), F.Sqr(baseRe));
          final IAST eInversed = F.Power(F.Exp(F.Times(F.Arg(base), expIm)), F.CN1);
          final IAST argBaseTimesexpRe = F.Times(F.Arg(base), expRe);
          return F.Plus(
              F.Times(eInversed,
                  F.Cos(F.Plus(F.Times(F.C1D2, expIm, F.Log(baseSqrSum2)), argBaseTimesexpRe)),
                  F.Power(baseSqrSum2, F.Times(F.C1D2, expRe))),
              F.Times(F.CI, eInversed, F.Power(baseSqrSum2, F.Times(F.C1D2, expRe)),
                  F.Sin(F.Plus(F.Times(F.C1D2, expIm, F.Log(baseSqrSum2)), argBaseTimesexpRe))));
        }
      }
      return super.visit(ast);
    }

    @Override
    public IExpr visit2(IExpr head, IExpr a1) {
      IExpr arg1 = a1;
      IExpr result = a1.accept(this);
      if (result.isPresent()) {
        result = fEngine.evaluate(result);
        arg1 = result;
      }
      IExpr x = S.Re.of(fEngine, arg1);
      IExpr y = S.Im.of(fEngine, arg1);
      if (head.isSymbol()) {
        int headID = ((ISymbol) head).ordinal();
        switch (headID) {
          case ID.Abs:
            // Sqrt(x^2 + y^2)
            return F.Sqrt(Plus(Power(x, C2), Power(y, C2)));
          case ID.Cos:
            // Cos(x)*Cosh(y)-I*Sin(x)*Sinh(y)
            return Plus(Times(Cos(x), Cosh(y)), Times(F.CNI, Sin(x), Sinh(y)));
          case ID.Cosh:
            // Cos(y)*Cosh(x)+I*Sin(y)*Sinh(x)
            F.Plus(F.Times(F.Cos(y), F.Cosh(x)), F.Times(F.CI, F.Sin(y), F.Sinh(x)));
          case ID.Cot: {
            // -Sin(2*x)/(Cos(2*x)-Cosh(2*y))+(I*Sinh(2*y))/(Cos(2*x)-Cosh(2*y))
            IExpr v1 = F.Times(F.C2, x);
            IExpr v2 = F.Times(F.C2, y);
            IExpr v3 = F.Power(F.Subtract(F.Cos(v1), F.Cosh(v2)), F.CN1);
            return F.Plus(F.Times(F.CN1, v3, F.Sin(v1)), F.Times(F.CI, v3, F.Sinh(v2)));
          }
          case ID.Coth: {
            // (I*Sin(2*y))/(Cos(2*y)-Cosh(2*x))-Sinh(2*x)/(Cos(2*y)-Cosh(2*x))
            IExpr v1 = F.Times(F.C2, x);
            IExpr v2 = F.Times(F.C2, y);
            IExpr v3 = F.Power(F.Subtract(F.Cos(v2), F.Cosh(v1)), F.CN1);
            return F.Plus(F.Times(F.CI, v3, F.Sin(v2)), F.Times(F.CN1, v3, F.Sinh(v1)));
          }
          case ID.Csc: {
            // ((-1)*2*Cosh(y)*Sin(x))/(Cos(2*x)-Cosh(2*y))+(I*2*Cos(x)*Sinh(y))/(Cos(2*x)-Cosh(2*y))
            IExpr v1 =
                F.Power(F.Subtract(F.Cos(F.Times(F.C2, x)), F.Cosh(F.Times(F.C2, y))), F.CN1);
            return F.Plus(F.Times(F.CN2, v1, F.Cosh(y), F.Sin(x)),
                F.Times(F.CC(0L, 1L, 2L, 1L), v1, F.Cos(x), F.Sinh(y)));
          }
          case ID.Csch: {
            // (2*I*Cosh(x)*Sin(y))/(Cos(2*y)-Cosh(2*x))+(-2*Cos(y)*Sinh(x))/(Cos(2*y)-Cosh(2*x))
            IExpr v1 =
                F.Power(F.Subtract(F.Cos(F.Times(F.C2, y)), F.Cosh(F.Times(F.C2, x))), F.CN1);
            return F.Plus(F.Times(F.CC(0L, 1L, 2L, 1L), v1, F.Cosh(x), F.Sin(y)),
                F.Times(F.CN2, v1, F.Cos(y), F.Sinh(x)));
          }
          case ID.Log:
            // I*Arg(x + I*y) + (1/2)*Log(x^2 + y^2)
            final IAST logPart;
            if (x.isPossibleZero(false) && y.isPositiveResult()) {
              logPart = F.Log(y);
            } else if (y.isPossibleZero(false) && x.isPositiveResult()) {
              logPart = F.Log(x);
            } else {
              logPart = F.Times(F.C1D2, F.Log(F.Plus(F.Sqr(x), F.Sqr(y))));
            }
            return F.Plus(F.Times(F.CI, F.Arg(F.Plus(x, F.Times(F.CI, y)))), logPart);
          case ID.Sec: {
            // (2*Cos(x)*Cosh(y))/(Cos(2*x)+Cosh(2*y))+(I*2*Sin(x)*Sinh(y))/(Cos(2*x)+Cosh(2*y))
            IExpr v1 = F.Power(F.Plus(F.Cos(F.Times(F.C2, x)), F.Cosh(F.Times(F.C2, y))), F.CN1);
            return F.Plus(F.Times(F.C2, v1, F.Cos(x), F.Cosh(y)),
                F.Times(F.CC(0L, 1L, 2L, 1L), v1, F.Sin(x), F.Sinh(y)));
          }
          case ID.Sech: {
            // (2*Cos(y)*Cosh(x))/(Cos(2*y)+Cosh(2*x))+(-I*2*Sin(y)*Sinh(x))/(Cos(2*y)+Cosh(2*x))
            IExpr v1 = F.Power(F.Plus(F.Cos(F.Times(F.C2, y)), F.Cosh(F.Times(F.C2, x))), F.CN1);
            return F.Plus(F.Times(F.C2, v1, F.Cos(y), F.Cosh(x)),
                F.Times(F.CC(0L, 1L, -2L, 1L), v1, F.Sin(y), F.Sinh(x)));
          }
          case ID.ProductLog:
            // I*Im(ProductLog(x + I*y)) + Re(ProductLog(x + I*y))
            IExpr productLog = F.ProductLog(F.Plus(x, F.Times(F.CI, y)));
            return Plus(Times(F.CI, F.Im(productLog)), F.Re(productLog));
          case ID.Sin:
            // Cosh(y)*Sin(x)+I*Sinh(y)*Cos(x)
            return Plus(Times(Cosh(y), Sin(x)), Times(CI, Sinh(y), Cos(x)));
          case ID.Sinh:
            // I*Cosh(x)*Sin(y)+Cos(y)*Sinh(x)
            return F.Plus(F.Times(F.CI, F.Cosh(x), F.Sin(y)), F.Times(F.Cos(y), F.Sinh(x)));
          case ID.ArcTan: {
            // -Arg(1-I*x-y)/2+Arg(1+I*x-y)/2+I*(-Log(x^2+(1-y)^2)/4+Log(x^2+(1+y)^2)/4)
            IExpr v1 = F.Negate(y);
            IExpr v2 = F.Sqr(x);
            return F.Plus(F.Times(F.CN1D2, F.Arg(F.Plus(F.C1, v1, F.Times(F.CNI, x)))),
                F.Times(F.C1D2, F.Arg(F.Plus(F.C1, v1, F.Times(F.CI, x)))),
                F.Times(F.CI, F.Plus(F.Times(F.CN1D4, F.Log(F.Plus(F.Sqr(F.Plus(F.C1, v1)), v2))),
                    F.Times(F.C1D4, F.Log(F.Plus(v2, F.Sqr(F.Plus(F.C1, y))))))));
          }
          case ID.Tan: {
            // Sin(2*x)/(Cos(2*x)+Cosh(2*y))+(I*Sinh(2*y))/(Cos(2*x)+Cosh(2*y))
            IExpr v1 = F.Times(F.C2, x);
            IExpr v2 = F.Times(F.C2, y);
            IExpr v3 = F.Power(F.Plus(F.Cos(v1), F.Cosh(v2)), F.CN1);
            return F.Plus(F.Times(v3, F.Sin(v1)), F.Times(F.CI, v3, F.Sinh(v2)));
          }
          case ID.Tanh:
            // (I*Sin(2*y))/(Cos(2*y) + Cosh(2*x)) ) + Sinh(2*x) /(Cos(2*y) + Cosh(2*x)) )
            IExpr v1 = F.Times(F.C2, x);
            IExpr v2 = F.Times(F.C2, y);
            IExpr v3 = F.Power(F.Plus(F.Cos(v2), F.Cosh(v1)), F.CN1);
            return F.Plus(F.Times(F.CI, v3, F.Sin(v2)), F.Times(v3, F.Sinh(v1)));
          default:
        }
      }
      if (result.isPresent()) {
        return F.unaryAST1(head, result);
      }
      return F.NIL;
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr temp = StructureFunctions.threadListLogicEquationOperators(ast.arg1(), ast, 1);
    if (temp.isPresent()) {
      return temp;
    }
    IAssumptions oldAssumptions = engine.getAssumptions();
    try {
      IExpr arg1 = ast.arg1();
      final IAST arg2;
      if (ast.isAST2()) {
        arg2 = ast.arg2().makeList();
      } else {
        arg2 = F.NIL;
      }
      VariablesSet eVar = new VariablesSet(arg1);
      List<IExpr> varList = eVar.getVarList().copyTo();
      IASTAppendable assumptionExpr = F.mapList(varList, variable -> {
        if (arg2.isPresent()) {
          for (int j = 1; j < arg2.size(); j++) {
            if (S.MatchQ.ofQ(variable, arg2.get(j))) {
              return F.NIL;
            }
          }
        }
        return F.Element(variable, S.Reals);
      });
      IAssumptions assumptions;
      if (oldAssumptions == null) {
        assumptions = org.matheclipse.core.eval.util.Assumptions.getInstance(assumptionExpr);
      } else {
        assumptions = oldAssumptions.copy();
        assumptions = assumptions.addAssumption(assumptionExpr);
      }
      engine.setAssumptions(assumptions);

      IExpr result = arg1;
      ComplexExpandVisitor tteVisitor = new ComplexExpandVisitor(engine);
      // while (true) {
      temp = result.accept(tteVisitor);
      if (temp.isPresent()) {
        result = engine.evaluate(temp);
        // } else {
        // break;
      }
      // }
      return result;
    } finally {
      engine.setAssumptions(oldAssumptions);
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }
}
