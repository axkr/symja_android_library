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
import org.matheclipse.core.builtin.SimplifyFunctions;
import org.matheclipse.core.builtin.StructureFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.eval.util.IAssumptions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
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
public class ComplexExpand extends AbstractFunctionOptionEvaluator {


  public ComplexExpand() {}

  static class ComplexExpandVisitor extends VisitorExpr {
    final EvalEngine fEngine;

    public ComplexExpandVisitor(EvalEngine engine) {
      super();
      fEngine = engine;
    }

    @Override
    public IExpr visit(ISymbol element) {
      if (element.isVariable() && !element.isRealResult()) {
        return F.Plus(F.Re(element), F.Times(F.CI, F.Im(element)));
      }
      return F.NIL;
    }

    @Override
    public IExpr visit(IASTMutable ast) {
      if ((ast.isAST(S.Re, 2) || ast.isAST(S.Im, 2) || ast.isAST(S.Arg, 2))//
          && ast.arg1().isSymbol()) {
        return F.NIL;
      }
      IExpr result = F.NIL;
      IExpr expr = super.visit(ast);
      if (expr.isNIL()) {
        expr = ast;
      } else {
        result = expr;
      }

      IExpr temp = fEngine.evaluateNIL(F.Expand(expr));
      if (temp.isPresent() && !temp.equals(expr)) {
        expr = temp;
        result = temp;
      }

      if (expr.isPower()) {
        IExpr base = expr.base();
        IExpr exp = expr.exponent();
        if (base.isE()) {
          IExpr x = exp.re();
          IExpr y = exp.im();
          IExpr v1 = F.Exp(x);
          return F.Plus(F.Times(v1, F.Cos(y)), F.Times(F.CI, v1, F.Sin(y)));
        }
        if (base.isRealResult()) {
          IExpr x = exp.re();
          IExpr y = exp.im();
          if (base.isPositive()) {
            // base^x*Cos(y*Log(base))+I*base^x*Sin(y*Log(base))
            IExpr v1 = F.Power(base, x);
            IExpr v2 = F.Times(y, F.Log(base));
            return F.Plus(F.Times(v1, F.Cos(v2)), F.Times(F.CI, v1, F.Sin(v2)));
          } else if (base.isNegative()) {
            base = base.negate();
            // (base^x*Cos(Pi*x+y*Log(base)))/E^(Pi*y)+(I*base^x*Sin(Pi*x+y*Log(base)))/E^(Pi*y)
            IExpr v1 = F.Power(base, x);
            IExpr v2 = F.Power(F.Exp(F.Times(F.Pi, y)), F.CN1);
            IExpr v3 = F.Plus(F.Times(F.Pi, x), F.Times(y, F.Log(base)));
            return F.Plus(F.Times(v1, v2, F.Cos(v3)), F.Times(F.CI, v1, v2, F.Sin(v3)));
          }
          if (exp.isReal()) {
            return result;
          }
        }

        IExpr x = base.re();
        IExpr y = base.im();
        IExpr arg = SimplifyFunctions.argReXImY(x, y, fEngine);
        if (exp.isRealResult()) {
          IExpr n = exp;
          // (x^2+y^2)^(n/2)*Cos(n*arg)+I*(x^2+y^2)^(n/2)*Sin(n*arg)
          IExpr v1 = F.Power(F.Plus(F.Sqr(x), F.Sqr(y)), F.Times(F.C1D2, n));
          IExpr v2 = F.Times(n, arg);
          return F.Plus(F.Times(v1, F.Cos(v2)), F.Times(F.CI, v1, F.Sin(v2)));
        }
        IExpr a = exp.re();
        IExpr b = exp.im();
        // ((x^2+y^2)^(a/2)*Cos(a*arg+1/2*b*Log(x^2+y^2)))/E^(b*arg)+(I*(x^2+y^2)^(a/2)*Sin(a*arg+1/2*b*Log(x^2+y^2)))/E^(b*arg)
        IExpr v1 = F.Power(F.Exp(F.Times(b, arg)), F.CN1);
        IExpr v2 = F.Plus(F.Sqr(x), F.Sqr(y));
        IExpr v3 = F.Power(v2, F.Times(F.C1D2, a));
        IExpr v4 = arg;
        IExpr v5 = F.Plus(F.Times(a, v4), F.Times(F.C1D2, b, F.Log(v2)));
        return F.Plus(F.Times(v1, v3, F.Cos(v5)), F.Times(F.CI, v1, v3, F.Sin(v5)));
      }
      return result;
    }

    @Override
    public IExpr visit2(IExpr head, IExpr a1) {
      int headID = -1;
      IExpr arg1 = a1;
      if (head.isSymbol()) {
        headID = ((ISymbol) head).ordinal();
        if (headID == ID.Re || headID == ID.Im) {
          if (a1.isAST1() && a1.head().isSymbol()) {
            int reHeadID = ((ISymbol) a1.head()).ordinal();
            if (reHeadID > ID.UNKNOWN) {
              IExpr subArg1 = a1.first();
              IExpr result = subArg1.accept(this);
              if (result.isPresent()) {
                result = fEngine.evaluate(result);
                subArg1 = result;
              }
              IExpr x = S.Re.of(fEngine, subArg1);
              IExpr y = S.Im.of(fEngine, subArg1);
              final IExpr temp;
              if (headID == ID.Re) {
                temp = reComplexExpand(reHeadID, subArg1, x, y);
              } else {
                temp = imComplexExpand(reHeadID, subArg1, x, y);
              }
              if (temp.isPresent()) {
                return temp;
              }
            }
          }
        }
      }

      IExpr result = a1.accept(this);
      if (result.isPresent()) {
        result = fEngine.evaluate(result);
        arg1 = result;
      }

      if (headID > ID.UNKNOWN) {
        // IExpr z = a1;
        IExpr x = S.Re.of(fEngine, arg1);
        IExpr y = S.Im.of(fEngine, arg1);
        switch (headID) {
          case ID.Abs:
            // Sqrt(x^2 + y^2)
            return F.Sqrt(Plus(Power(x, C2), Power(y, C2)));
          case ID.ArcCos: {
            // Pi/2-Arg(Sqrt(1-(x+I*y)^2)+I*(x+I*y))+I*Log(Sqrt((-y+(4*x^2*y^2+(1-x^2+y^2)^2)^(1/4)*Cos(Arg(1-(x+I*y)^2)/2))^2+(x+(4*x^2*y^2+(1-x^2+y^2)^2)^(1/4)*Sin(Arg(1-(x+I*y)^2)/2))^2))
            IExpr v1 = F.Sqr(x);
            IExpr v2 = F.Subtract(F.C1, F.Sqr(F.Plus(x, F.Times(F.CI, y))));
            IExpr v3 = F.Plus(x, F.Times(F.CI, y));
            IExpr v4 = F.Sqr(y);
            IExpr v5 = F.Power(F.Plus(F.Times(F.C4, v1, v4), F.Sqr(F.Plus(F.C1, F.Negate(v1), v4))),
                F.C1D4);
            IExpr v6 = F.Times(F.C1D2, F.Arg(v2));
            return F.Plus(F.CPiHalf, F.Negate(F.Arg(F.Plus(F.Sqrt(v2), F.Times(F.CI, v3)))),
                F.Times(F.CC(0L, 1L, 1L, 2L),
                    F.Log(F.Plus(F.Sqr(F.Plus(y, F.Times(F.CN1, v5, F.Cos(v6)))),
                        F.Sqr(F.Plus(x, F.Times(v5, F.Sin(v6))))))));
          }
          case ID.ArcCot: {
            // -Arg(1-I/(x+I*y))/2+Arg(1+I/(x+I*y))/2+I*(-Log(x^2/(x^2+y^2)^2+(1+y/(x^2+y^2))^2)/4+Log(x^2/(x^2+y^2)^2+(1-y/(x^2+y^2))^2)/4)
            // -Arg(1-I/(x+I*y))/2+Arg(1+I/(x+I*y))/2+I*(-Log(x^2/(x^2+y^2)^2+(1+y/(x^2+y^2))^2)/4+Log(x^2/(x^2+y^2)^2+(1-y/(x^2+y^2))^2)/4)
            IExpr v1 = F.Sqr(x);
            IExpr v2 = F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN1);
            IExpr v3 = F.Plus(v1, F.Sqr(y));
            IExpr v4 = F.Times(v1, F.Power(v3, F.CN2));
            IExpr v5 = F.Power(v3, F.CN1);
            return F.Plus(F.Times(F.CN1D2, F.Arg(F.Plus(F.C1, F.Times(F.CNI, v2)))),
                F.Times(F.C1D2, F.Arg(F.Plus(F.C1, F.Times(F.CI, v2)))),
                F.Times(F.CI,
                    F.Plus(F.Times(F.CN1D4, F.Log(F.Plus(v4, F.Sqr(F.Plus(F.C1, F.Times(v5, y)))))),
                        F.Times(F.C1D4,
                            F.Log(F.Plus(v4, F.Sqr(F.Plus(F.C1, F.Times(F.CN1, v5, y)))))))));
          }
          case ID.ArcCsc: {
            // Arg(Sqrt(1-1/(x+I*y)^2)+I/(x+I*y))-I*Log(Sqrt((y/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1-(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Cos(Arg(1-1/(x+I*y)^2)/2))^2+(x/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1-(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Sin(Arg(1-1/(x+I*y)^2)/2))^2))
            IExpr v1 = F.Sqr(x);
            IExpr v2 = F.Sqr(y);
            IExpr v3 = F.Plus(v1, v2);
            IExpr v4 = F.Subtract(F.C1, F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN2));
            IExpr v5 = F.Plus(x, F.Times(F.CI, y));
            IExpr v6 = F.Power(v3, F.CN1);
            IExpr v7 =
                F.Power(
                    F.Plus(F.Times(F.C4, v1, v2, F.Power(v3, F.CN4)),
                        F.Sqr(
                            F.Plus(F.C1, F.Times(F.CN1, F.Subtract(v1, v2), F.Power(v3, F.CN2))))),
                    F.C1D4);
            IExpr v8 = F.Times(F.C1D2, F.Arg(v4));
            return F.Plus(F.Arg(F.Plus(F.Sqrt(v4), F.Times(F.CI, F.Power(v5, F.CN1)))),
                F.Times(F.CC(0L, 1L, -1L, 2L),
                    F.Log(F.Plus(F.Sqr(F.Plus(F.Times(v6, y), F.Times(v7, F.Cos(v8)))),
                        F.Sqr(F.Plus(F.Times(v6, x), F.Times(v7, F.Sin(v8))))))));

          }
          case ID.ArcSec: {
            // Pi/2-Arg(Sqrt(1-1/(x+I*y)^2)+I/(x+I*y))+I*Log(Sqrt((y/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1-(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Cos(Arg(1-1/(x+I*y)^2)/2))^2+(x/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1-(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Sin(Arg(1-1/(x+I*y)^2)/2))^2))
            IExpr v1 = F.Sqr(x);
            IExpr v2 = F.Sqr(y);
            IExpr v3 = F.Plus(v1, v2);
            IExpr v4 = F.Subtract(F.C1, F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN2));
            IExpr v5 = F.Plus(x, F.Times(F.CI, y));
            IExpr v6 = F.Power(v3, F.CN1);
            IExpr v7 =
                F.Power(
                    F.Plus(F.Times(F.C4, v1, v2, F.Power(v3, F.CN4)),
                        F.Sqr(
                            F.Plus(F.C1, F.Times(F.CN1, F.Subtract(v1, v2), F.Power(v3, F.CN2))))),
                    F.C1D4);
            IExpr v8 = F.Times(F.C1D2, F.Arg(v4));
            return F.Plus(F.CPiHalf,
                F.Negate(F.Arg(F.Plus(F.Sqrt(v4), F.Times(F.CI, F.Power(v5, F.CN1))))),
                F.Times(F.CC(0L, 1L, 1L, 2L),
                    F.Log(F.Plus(F.Sqr(F.Plus(F.Times(v6, y), F.Times(v7, F.Cos(v8)))),
                        F.Sqr(F.Plus(F.Times(v6, x), F.Times(v7, F.Sin(v8))))))));

          }
          case ID.ArcSin: {
            // Arg(Sqrt(1-(x+I*y)^2)+I*(x+I*y))-I*Log(Sqrt((-y+(4*x^2*y^2+(1-x^2+y^2)^2)^(1/4)*Cos(Arg(1-(x+I*y)^2)/2))^2+(x+(4*x^2*y^2+(1-x^2+y^2)^2)^(1/4)*Sin(Arg(1-(x+I*y)^2)/2))^2))
            IExpr v1 = F.Sqr(x);
            IExpr v2 = F.Subtract(F.C1, F.Sqr(F.Plus(x, F.Times(F.CI, y))));
            IExpr v3 = F.Plus(x, F.Times(F.CI, y));
            IExpr v4 = F.Sqr(y);
            IExpr v5 = F.Power(F.Plus(F.Times(F.C4, v1, v4), F.Sqr(F.Plus(F.C1, F.Negate(v1), v4))),
                F.C1D4);
            IExpr v6 = F.Times(F.C1D2, F.Arg(v2));
            return F.Plus(F.Arg(F.Plus(F.Sqrt(v2), F.Times(F.CI, v3))),
                F.Times(F.CC(0L, 1L, -1L, 2L),
                    F.Log(F.Plus(F.Sqr(F.Plus(y, F.Times(F.CN1, v5, F.Cos(v6)))),
                        F.Sqr(F.Plus(x, F.Times(v5, F.Sin(v6))))))));
          }
          case ID.ArcTan: {
            // -Arg(1-I*x-y)/2+Arg(1+I*x-y)/2+I*(-Log(x^2+(1-y)^2)/4+Log(x^2+(1+y)^2)/4)
            IExpr v1 = F.Negate(y);
            IExpr v2 = F.Sqr(x);
            return F.Plus(F.Times(F.CN1D2, F.Arg(F.Plus(F.C1, v1, F.Times(F.CNI, x)))),
                F.Times(F.C1D2, F.Arg(F.Plus(F.C1, v1, F.Times(F.CI, x)))),
                F.Times(F.CI, F.Plus(F.Times(F.CN1D4, F.Log(F.Plus(F.Sqr(F.Plus(F.C1, v1)), v2))),
                    F.Times(F.C1D4, F.Log(F.Plus(v2, F.Sqr(F.Plus(F.C1, y))))))));
          }
          case ID.ArcCosh: {
            // I*Arg(x+Sqrt(-1+x+I*y)*Sqrt(1+x+I*y)+I*y)+Log(Sqrt((y+((-1+x)^2+y^2)^(1/4)*((1+x)^2+y^2)^(1/4)*Cos(Arg(1+x+I*y)/2)*Sin(Arg(-1+x+I*y)/2)+((-1+x)^2+y^2)^(1/4)*((1+x)^2+y^2)^(1/4)*Cos(Arg(-1+x+I*y)/2)*Sin(Arg(1+x+I*y)/2))^2+(x+((-1+x)^2+y^2)^(1/4)*((1+x)^2+y^2)^(1/4)*Cos(Arg(-1+x+I*y)/2)*Cos(Arg(1+x+I*y)/2)-((-1+x)^2+y^2)^(1/4)*((1+x)^2+y^2)^(1/4)*Sin(Arg(-1+x+I*y)/2)*Sin(Arg(1+x+I*y)/2))^2))
            IExpr v0 = F.Sqr(y);
            IExpr v1 = F.Power(F.Plus(F.Sqr(F.Plus(F.CN1, x)), v0), F.C1D4);
            IExpr v2 = F.Power(F.Plus(F.Sqr(F.Plus(F.C1, x)), v0), F.C1D4);
            IExpr v3 = F.Times(F.CI, y);
            IExpr v4 = F.Plus(F.CN1, x, v3);
            IExpr v5 = F.Plus(F.C1, x, v3);
            IExpr v7 = F.Times(F.C1D2, F.Arg(v4));
            IExpr v8 = F.Times(F.C1D2, F.Arg(v5));
            IExpr v9 = F.Cos(v7);
            IExpr v10 = F.Cos(v8);
            IExpr v11 = F.Sin(v7);
            IExpr v12 = F.Sin(v8);
            return F.Plus(F.Times(F.CI, F.Arg(F.Plus(v3, F.Times(F.Sqrt(v4), F.Sqrt(v5)), x))),
                F.Times(F.C1D2,
                    F.Log(F.Plus(
                        F.Sqr(
                            F.Plus(F.Times(F.CN1, v1, v11, v12, v2), F.Times(v1, v10, v2, v9), x)),
                        F.Sqr(F.Plus(F.Times(v1, v10, v11, v2), F.Times(v1, v12, v2, v9), y))))));
          }
          case ID.ArcCoth: {
            // I*(-Arg(1-1/(x+I*y))/2+Arg(1+1/(x+I*y))/2)-Log(y^2/(x^2+y^2)^2+(1-x/(x^2+y^2))^2)/4+Log(y^2/(x^2+y^2)^2+(1+x/(x^2+y^2))^2)/4
            IExpr v1 = F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN1);
            IExpr v2 = F.Sqr(y);
            IExpr v3 = F.Plus(F.Sqr(x), v2);
            IExpr v4 = F.Times(v2, F.Power(v3, F.CN2));
            IExpr v5 = F.Power(v3, F.CN1);
            return F.Plus(
                F.Times(F.CI,
                    F.Plus(F.Times(F.CN1D2, F.Arg(F.Subtract(F.C1, v1))),
                        F.Times(F.C1D2, F.Arg(F.Plus(F.C1, v1))))),
                F.Times(F.C1D4, F.Log(F.Plus(v4, F.Sqr(F.Plus(F.C1, F.Times(v5, x)))))),
                F.Times(F.CN1D4, F.Log(F.Plus(v4, F.Sqr(F.Plus(F.C1, F.Times(F.CN1, v5, x)))))));
          }
          case ID.ArcCsch: {
            // I*Arg(Sqrt(1+1/(x+I*y)^2)+1/(x+I*y))+Log(Sqrt((x/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1+(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Cos(Arg(1+1/(x+I*y)^2)/2))^2+(-y/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1+(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Sin(Arg(1+1/(x+I*y)^2)/2))^2))
            IExpr v1 = F.Sqr(x);
            IExpr v2 = F.Sqr(y);
            IExpr v3 = F.Plus(v1, v2);
            IExpr v4 = F.Plus(F.C1, F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN2));
            IExpr v5 = F.Plus(x, F.Times(F.CI, y));
            IExpr v6 = F.Power(v3, F.CN1);
            IExpr v7 = F.Power(F.Plus(F.Times(F.C4, v1, v2, F.Power(v3, F.CN4)),
                F.Sqr(F.Plus(F.C1, F.Times(F.Subtract(v1, v2), F.Power(v3, F.CN2))))), F.C1D4);
            IExpr v8 = F.Times(F.C1D2, F.Arg(v4));
            return F.Plus(F.Times(F.CI, F.Arg(F.Plus(F.Sqrt(v4), F.Power(v5, F.CN1)))),
                F.Times(F.C1D2, F.Log(F.Plus(F.Sqr(F.Plus(F.Times(v6, x), F.Times(v7, F.Cos(v8)))),
                    F.Sqr(F.Plus(F.Times(v6, y), F.Times(F.CN1, v7, F.Sin(v8))))))));
          }
          case ID.ArcSech: {
            // I*Arg(1/(x+I*y)+(1+1/(x+I*y))*Sqrt((1-x-I*y)/(1+x+I*y)))+Log(Sqrt((x/(x^2+y^2)+Sqrt(Sqrt((1-x)^2+y^2)/Sqrt((1+x)^2+y^2))*(1+x/(x^2+y^2))*Cos(Arg((1-x-I*y)/(1+x+I*y))/2)+(y*Sqrt(Sqrt((1-x)^2+y^2)/Sqrt((1+x)^2+y^2))*Sin(Arg((1-x-I*y)/(1+x+I*y))/2))/(x^2+y^2))^2+(-y/(x^2+y^2)+(-y*Sqrt(Sqrt((1-x)^2+y^2)/Sqrt((1+x)^2+y^2))*Cos(Arg((1-x-I*y)/(1+x+I*y))/2))/(x^2+y^2)+Sqrt(Sqrt((1-x)^2+y^2)/Sqrt((1+x)^2+y^2))*(1+x/(x^2+y^2))*Sin(Arg((1-x-I*y)/(1+x+I*y))/2))^2))
            IExpr v1 = F.Power(F.Plus(F.Sqr(x), F.Sqr(y)), F.CN1);
            IExpr v2 = F.Sqrt(F.Times(F.Sqrt(F.Plus(F.Sqr(F.Subtract(F.C1, x)), F.Sqr(y))),
                F.Power(F.Plus(F.Sqr(F.Plus(F.C1, x)), F.Sqr(y)), F.CN1D2)));
            IExpr v4 = F.Negate(x);
            IExpr v5 = F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN1);
            IExpr v6 = F.Times(F.Power(F.Plus(F.C1, x, F.Times(F.CI, y)), F.CN1),
                F.Plus(F.C1, v4, F.Times(F.CNI, y)));
            IExpr v8 = F.Times(x, v1);
            IExpr v9 = F.Plus(F.C1, v8);
            IExpr v10 = F.Times(F.C1D2, F.Arg(v6));
            IExpr v11 = F.Cos(v10);
            IExpr v12 = F.Sin(v10);
            return F.Plus(F.Times(F.CI, F.Arg(F.Plus(v5, F.Times(F.Plus(F.C1, v5), F.Sqrt(v6))))),
                F.Times(F.C1D2,
                    F.Log(F.Plus(
                        F.Sqr(F.Plus(F.Times(v12, v2, v9), F.Times(F.CN1, v1, y),
                            F.Times(F.CN1, v1, v11, v2, y))),
                        F.Sqr(F.Plus(v8, F.Times(v11, v2, v9), F.Times(v1, v12, v2, y)))))));
          }
          case ID.ArcSinh: {
            // I*Arg(x+Sqrt(1+(x+I*y)^2)+I*y)+Log(Sqrt((x+(4*x^2*y^2+(1+x^2-y^2)^2)^(1/4)*Cos(Arg(1+(x+I*y)^2)/2))^2+(y+(4*x^2*y^2+(1+x^2-y^2)^2)^(1/4)*Sin(Arg(1+(x+I*y)^2)/2))^2))
            IExpr v1 = F.Sqr(x);
            IExpr v2 = F.Plus(F.C1, F.Sqr(F.Plus(x, F.Times(F.CI, y))));
            IExpr v3 = F.Times(F.CI, y);
            IExpr v4 = F.Sqr(y);
            IExpr v5 = F.Power(F.Plus(F.Times(F.C4, v1, v4), F.Sqr(F.Plus(F.C1, v1, F.Negate(v4)))),
                F.C1D4);
            IExpr v6 = F.Times(F.C1D2, F.Arg(v2));
            return F.Plus(F.Times(F.CI, F.Arg(F.Plus(F.Sqrt(v2), v3, x))),
                F.Times(F.C1D2, F.Log(F.Plus(F.Sqr(F.Plus(x, F.Times(v5, F.Cos(v6)))),
                    F.Sqr(F.Plus(y, F.Times(v5, F.Sin(v6))))))));
          }
          case ID.ArcTanh: {
            // I*(-Arg(1-x-I*y)/2+Arg(1+x+I*y)/2)-Log((1-x)^2+y^2)/4+Log((1+x)^2+y^2)/4
            IExpr v1 = F.Negate(x);
            IExpr v2 = F.Sqr(y);
            return F.Plus(
                F.Times(F.CI,
                    F.Plus(F.Times(F.CN1D2, F.Arg(F.Plus(F.C1, v1, F.Times(F.CNI, y)))),
                        F.Times(F.C1D2, F.Arg(F.Plus(F.C1, x, F.Times(F.CI, y)))))),
                F.Times(F.CN1D4, F.Log(F.Plus(F.Sqr(F.Plus(F.C1, v1)), v2))),
                F.Times(F.C1D4, F.Log(F.Plus(v2, F.Sqr(F.Plus(F.C1, x))))));
          }
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
            final IExpr logPart;
            if (x.isPossibleZero(false) && y.isPositiveResult()) {
              logPart = F.Log(y);
            } else if (y.isPossibleZero(false) && x.isPositiveResult()) {
              logPart = F.Log(x);
            } else {
              IExpr logPlusReImSquared =
                  F.PowerExpand(F.Log(F.FactorTerms(F.Plus(F.Sqr(x), F.Sqr(y)))));
              logPart = fEngine.evaluate(F.Times(F.C1D2, logPlusReImSquared));
            }
            IExpr arg = SimplifyFunctions.argReXImY(x, y, fEngine);
            return F.Plus(F.Times(F.CI, arg), logPart);
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

    /**
     * Complex expand expressions <code>Re( head(z) )</code>, where <code>x</code> is the real part
     * of <code>z</code> and <code>y</code> is the imaginary part of <code>z</code>.
     * 
     * @param head
     * @param z
     * @param x
     * @param y
     * @return
     */
    private IExpr reComplexExpand(int head, IExpr z, IExpr x, IExpr y) {
      switch (head) {
        case ID.ArcCos: {
          // Pi/2-Arg(Sqrt(1-(x+I*y)^2)+I*(x+I*y))
          IExpr v1 = F.Plus(x, F.Times(F.CI, y));
          return F.Subtract(F.CPiHalf,
              F.Arg(F.Plus(F.Times(F.CI, v1), F.Sqrt(F.Subtract(F.C1, F.Sqr(v1))))));
        }
        case ID.ArcCot: {
          // -Arg(1-I/(x+I*y))/2+Arg(1+I/(x+I*y))/2
          IExpr v1 = F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN1);
          return F.Plus(F.Times(F.CN1D2, F.Arg(F.Plus(F.C1, F.Times(F.CNI, v1)))),
              F.Times(F.C1D2, F.Arg(F.Plus(F.C1, F.Times(F.CI, v1)))));
        }
        case ID.ArcCsc: {
          // Arg(Sqrt(1-1/(x+I*y)^2)+I/(x+I*y))
          IExpr v1 = F.Plus(x, F.Times(F.CI, y));
          return F.Arg(F.Plus(F.Sqrt(F.Subtract(F.C1, F.Power(v1, F.CN2))),
              F.Times(F.CI, F.Power(v1, F.CN1))));
        }
        case ID.ArcSec: {
          // Pi/2-Arg(Sqrt(1-1/(x+I*y)^2)+I/(x+I*y))
          IExpr v1 = F.Plus(x, F.Times(F.CI, y));
          return F.Subtract(F.CPiHalf, F.Arg(F.Plus(F.Sqrt(F.Subtract(F.C1, F.Power(v1, F.CN2))),
              F.Times(F.CI, F.Power(v1, F.CN1)))));
        }
        case ID.ArcSin: {
          // Arg(Sqrt(1-(x+I*y)^2)+I*(x+I*y))
          IExpr v1 = F.Plus(x, F.Times(F.CI, y));
          return F.Arg(F.Plus(F.Times(F.CI, v1), F.Sqrt(F.Subtract(F.C1, F.Sqr(v1)))));
        }
        case ID.ArcTan: {
          // -Arg(1-I*(x+I*y))/2+Arg(1+I*(x+I*y))/2
          IExpr v1 = F.Plus(x, F.Times(F.CI, y));
          return F.Plus(F.Times(F.CN1D2, F.Arg(F.Plus(F.C1, F.Times(F.CNI, v1)))),
              F.Times(F.C1D2, F.Arg(F.Plus(F.C1, F.Times(F.CI, v1)))));
        }
        case ID.ArcCosh: {
          // Log(Sqrt((y+((-1+x)^2+y^2)^(1/4)*((1+x)^2+y^2)^(1/4)*Cos(Arg(1+x+I*y)/2)*Sin(Arg(-1+x+I*y)/2)+((-1+x)^2+y^2)^(1/4)*((1+x)^2+y^2)^(1/4)*Cos(Arg(-1+x+I*y)/2)*Sin(Arg(1+x+I*y)/2))^2+(x+((-1+x)^2+y^2)^(1/4)*((1+x)^2+y^2)^(1/4)*Cos(Arg(-1+x+I*y)/2)*Cos(Arg(1+x+I*y)/2)-((-1+x)^2+y^2)^(1/4)*((1+x)^2+y^2)^(1/4)*Sin(Arg(-1+x+I*y)/2)*Sin(Arg(1+x+I*y)/2))^2))
          IExpr v0 = F.Sqr(y);
          IExpr v1 = F.Power(F.Plus(F.Sqr(F.Plus(F.CN1, x)), v0), F.C1D4);
          IExpr v2 = F.Power(F.Plus(F.Sqr(F.Plus(F.C1, x)), v0), F.C1D4);
          IExpr v3 = F.Times(F.CI, y);
          IExpr v5 = F.Times(F.C1D2, F.Arg(F.Plus(F.CN1, x, v3)));
          IExpr v6 = F.Times(F.C1D2, F.Arg(F.Plus(F.C1, x, v3)));
          IExpr v7 = F.Cos(v5);
          IExpr v8 = F.Cos(v6);
          IExpr v9 = F.Sin(v5);
          IExpr v10 = F.Sin(v6);
          return F.Times(F.C1D2,
              F.Log(
                  F.Plus(F.Sqr(F.Plus(F.Times(v1, v2, v7, v8), F.Times(F.CN1, v1, v10, v2, v9), x)),
                      F.Sqr(F.Plus(F.Times(v1, v10, v2, v7), F.Times(v1, v2, v8, v9), y)))));

        }
        case ID.ArcCoth: {
          // -Log(y^2/(x^2+y^2)^2+(1-x/(x^2+y^2))^2)/4+Log(y^2/(x^2+y^2)^2+(1+x/(x^2+y^2))^2)/4
          IExpr v1 = F.Sqr(y);
          IExpr v2 = F.Plus(F.Sqr(x), v1);
          IExpr v3 = F.Times(v1, F.Power(v2, F.CN2));
          IExpr v4 = F.Power(v2, F.CN1);
          return F.Plus(F.Times(F.C1D4, F.Log(F.Plus(v3, F.Sqr(F.Plus(F.C1, F.Times(v4, x)))))),
              F.Times(F.CN1D4, F.Log(F.Plus(v3, F.Sqr(F.Plus(F.C1, F.Times(F.CN1, v4, x)))))));
        }
        case ID.ArcCsch: {
          // Log(Sqrt((x/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1+(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Cos(Arg(1+1/(x+I*y)^2)/2))^2+(-y/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1+(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Sin(Arg(1+1/(x+I*y)^2)/2))^2))
          IExpr v1 = F.Sqr(x);
          IExpr v2 = F.Sqr(y);
          IExpr v3 = F.Plus(v1, v2);
          IExpr v4 = F.Power(v3, F.CN1);
          IExpr v5 = F.Power(F.Plus(F.Times(F.C4, v1, v2, F.Power(v3, F.CN4)),
              F.Sqr(F.Plus(F.C1, F.Times(F.Subtract(v1, v2), F.Power(v3, F.CN2))))), F.C1D4);
          IExpr v6 =
              F.Times(F.C1D2, F.Arg(F.Plus(F.C1, F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN2))));
          return F.Times(F.C1D2, F.Log(F.Plus(F.Sqr(F.Plus(F.Times(v4, x), F.Times(v5, F.Cos(v6)))),
              F.Sqr(F.Plus(F.Times(v4, y), F.Times(F.CN1, v5, F.Sin(v6)))))));
        }
        case ID.ArcSech: {
          // Log(Sqrt((x/(x^2+y^2)+Sqrt(Sqrt((1-x)^2+y^2)/Sqrt((1+x)^2+y^2))*(1+x/(x^2+y^2))*Cos(Arg((1-x-I*y)/(1+x+I*y))/2)+(y*Sqrt(Sqrt((1-x)^2+y^2)/Sqrt((1+x)^2+y^2))*Sin(Arg((1-x-I*y)/(1+x+I*y))/2))/(x^2+y^2))^2+(-y/(x^2+y^2)+(-y*Sqrt(Sqrt((1-x)^2+y^2)/Sqrt((1+x)^2+y^2))*Cos(Arg((1-x-I*y)/(1+x+I*y))/2))/(x^2+y^2)+Sqrt(Sqrt((1-x)^2+y^2)/Sqrt((1+x)^2+y^2))*(1+x/(x^2+y^2))*Sin(Arg((1-x-I*y)/(1+x+I*y))/2))^2))
          IExpr v1 = F.Power(F.Plus(F.Sqr(x), F.Sqr(y)), F.CN1);
          IExpr v2 = F.Sqrt(F.Times(F.Sqrt(F.Plus(F.Sqr(F.Subtract(F.C1, x)), F.Sqr(y))),
              F.Power(F.Plus(F.Sqr(F.Plus(F.C1, x)), F.Sqr(y)), F.CN1D2)));
          IExpr v3 = F.Sqr(y);
          IExpr v4 = F.Negate(x);
          IExpr v5 = F.Times(x, v1);
          IExpr v6 = F.Plus(F.C1, v5);
          IExpr v7 =
              F.Times(F.C1D2, F.Arg(F.Times(F.Power(F.Plus(F.C1, x, F.Times(F.CI, y)), F.CN1),
                  F.Plus(F.C1, v4, F.Times(F.CNI, y)))));
          IExpr v8 = F.Cos(v7);
          IExpr v9 = F.Sin(v7);
          return F.Times(F.C1D2,
              F.Log(F.Plus(
                  F.Sqr(F.Plus(F.Times(v2, v6, v9), F.Times(F.CN1, v1, y),
                      F.Times(F.CN1, v1, v2, v8, y))),
                  F.Sqr(F.Plus(v5, F.Times(v2, v6, v8), F.Times(v1, v2, v9, y))))));
        }
        case ID.ArcSinh: {
          // Log(Sqrt((x+(4*x^2*y^2+(1+x^2-y^2)^2)^(1/4)*Cos(Arg(1+(x+I*y)^2)/2))^2+(y+(4*x^2*y^2+(1+x^2-y^2)^2)^(1/4)*Sin(Arg(1+(x+I*y)^2)/2))^2))
          IExpr v1 = F.Sqr(x);
          IExpr v2 = F.Sqr(y);
          IExpr v3 =
              F.Power(F.Plus(F.Times(F.C4, v1, v2), F.Sqr(F.Plus(F.C1, v1, F.Negate(v2)))), F.C1D4);
          IExpr v4 = F.Times(F.C1D2, F.Arg(F.Plus(F.C1, F.Sqr(F.Plus(x, F.Times(F.CI, y))))));
          return F.Times(F.C1D2, F.Log(F.Plus(F.Sqr(F.Plus(x, F.Times(v3, F.Cos(v4)))),
              F.Sqr(F.Plus(y, F.Times(v3, F.Sin(v4)))))));
        }
        case ID.ArcTanh: {
          // -Log((1-x)^2+y^2)/4+Log((1+x)^2+y^2)/4
          IExpr v1 = F.Sqr(y);
          return F.Plus(F.Times(F.CN1D4, F.Log(F.Plus(v1, F.Sqr(F.Subtract(F.C1, x))))),
              F.Times(F.C1D4, F.Log(F.Plus(v1, F.Sqr(F.Plus(F.C1, x))))));
        }
        default:
          break;
      }
      return F.NIL;
    }

    /**
     * Complex expand expressions <code>Im( head(z) )</code>, where <code>x</code> is the real part
     * of <code>z</code> and <code>y</code> is the imaginary part of <code>z</code>.
     * 
     * @param head
     * @param z
     * @param x
     * @param y
     * @return
     */
    private IExpr imComplexExpand(int head, IExpr z, IExpr x, IExpr y) {
      switch (head) {
        case ID.ArcCos: {
          // Log(Sqrt((-y+(4*x^2*y^2+(1-x^2+y^2)^2)^(1/4)*Cos(Arg(1-(x+I*y)^2)/2))^2+(x+(4*x^2*y^2+(1-x^2+y^2)^2)^(1/4)*Sin(Arg(1-(x+I*y)^2)/2))^2))
          IExpr v1 = F.Sqr(x);
          IExpr v2 = F.Sqr(y);
          IExpr v3 =
              F.Power(F.Plus(F.Times(F.C4, v1, v2), F.Sqr(F.Plus(F.C1, F.Negate(v1), v2))), F.C1D4);
          IExpr v4 = F.Times(F.C1D2, F.Arg(F.Subtract(F.C1, F.Sqr(F.Plus(x, F.Times(F.CI, y))))));
          return F.Times(F.C1D2, F.Log(F.Plus(F.Sqr(F.Plus(y, F.Times(F.CN1, v3, F.Cos(v4)))),
              F.Sqr(F.Plus(x, F.Times(v3, F.Sin(v4)))))));
        }
        case ID.ArcCot: {
          // -Log(x^2/(x^2+y^2)^2+(1+y/(x^2+y^2))^2)/4+Log(x^2/(x^2+y^2)^2+(1-y/(x^2+y^2))^2)/4
          IExpr v1 = F.Sqr(x);
          IExpr v2 = F.Plus(v1, F.Sqr(y));
          IExpr v3 = F.Times(v1, F.Power(v2, F.CN2));
          IExpr v4 = F.Power(v2, F.CN1);
          return F.Plus(F.Times(F.CN1D4, F.Log(F.Plus(v3, F.Sqr(F.Plus(F.C1, F.Times(v4, y)))))),
              F.Times(F.C1D4, F.Log(F.Plus(v3, F.Sqr(F.Plus(F.C1, F.Times(F.CN1, v4, y)))))));
        }
        case ID.ArcCsc: {
          // -Log(Sqrt((y/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1-(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Cos(Arg(1-1/(x+I*y)^2)/2))^2+(x/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1-(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Sin(Arg(1-1/(x+I*y)^2)/2))^2))
          IExpr v1 = F.Sqr(x);
          IExpr v2 = F.Sqr(y);
          IExpr v3 = F.Plus(v1, v2);
          IExpr v4 = F.Power(v3, F.CN1);
          IExpr v5 = F.Power(
              F.Plus(F.Times(F.C4, v1, v2, F.Power(v3, F.CN4)),
                  F.Sqr(F.Plus(F.C1, F.Times(F.CN1, F.Subtract(v1, v2), F.Power(v3, F.CN2))))),
              F.C1D4);
          IExpr v6 =
              F.Times(F.C1D2, F.Arg(F.Subtract(F.C1, F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN2))));
          return F.Times(F.CN1D2,
              F.Log(F.Plus(F.Sqr(F.Plus(F.Times(v4, y), F.Times(v5, F.Cos(v6)))),
                  F.Sqr(F.Plus(F.Times(v4, x), F.Times(v5, F.Sin(v6)))))));
        }
        case ID.ArcSec: {
          // Log(Sqrt((y/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1-(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Cos(Arg(1-1/(x+I*y)^2)/2))^2+(x/(x^2+y^2)+((4*x^2*y^2)/(x^2+y^2)^4+(1-(x^2-y^2)/(x^2+y^2)^2)^2)^(1/4)*Sin(Arg(1-1/(x+I*y)^2)/2))^2))
          IExpr v1 = F.Sqr(x);
          IExpr v2 = F.Sqr(y);
          IExpr v3 = F.Plus(v1, v2);
          IExpr v4 = F.Power(v3, F.CN1);
          IExpr v5 = F.Power(
              F.Plus(F.Times(F.C4, v1, v2, F.Power(v3, F.CN4)),
                  F.Sqr(F.Plus(F.C1, F.Times(F.CN1, F.Subtract(v1, v2), F.Power(v3, F.CN2))))),
              F.C1D4);
          IExpr v6 =
              F.Times(F.C1D2, F.Arg(F.Subtract(F.C1, F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN2))));
          return F.Times(F.C1D2, F.Log(F.Plus(F.Sqr(F.Plus(F.Times(v4, y), F.Times(v5, F.Cos(v6)))),
              F.Sqr(F.Plus(F.Times(v4, x), F.Times(v5, F.Sin(v6)))))));
        }
        case ID.ArcSin: {
          // -Log(Sqrt((-y+(4*x^2*y^2+(1-x^2+y^2)^2)^(1/4)*Cos(Arg(1-(x+I*y)^2)/2))^2+(x+(4*x^2*y^2+(1-x^2+y^2)^2)^(1/4)*Sin(Arg(1-(x+I*y)^2)/2))^2))
          IExpr v1 = F.Sqr(x);
          IExpr v2 = F.Sqr(y);
          IExpr v3 =
              F.Power(F.Plus(F.Times(F.C4, v1, v2), F.Sqr(F.Plus(F.C1, F.Negate(v1), v2))), F.C1D4);
          IExpr v4 = F.Times(F.C1D2, F.Arg(F.Subtract(F.C1, F.Sqr(F.Plus(x, F.Times(F.CI, y))))));
          return F.Times(F.CN1D2, F.Log(F.Plus(F.Sqr(F.Plus(y, F.Times(F.CN1, v3, F.Cos(v4)))),
              F.Sqr(F.Plus(x, F.Times(v3, F.Sin(v4)))))));

        }
        case ID.ArcTan: {
          // -Log(x^2+(1-y)^2)/4+Log(x^2+(1+y)^2)/4
          IExpr v1 = F.Sqr(x);
          return F.Plus(F.Times(F.CN1D4, F.Log(F.Plus(v1, F.Sqr(F.Subtract(F.C1, y))))),
              F.Times(F.C1D4, F.Log(F.Plus(v1, F.Sqr(F.Plus(F.C1, y))))));
        }
        case ID.ArcCosh: {
          // Arg(x+Sqrt(-1+x+I*y)*Sqrt(1+x+I*y)+I*y)
          IExpr v1 = F.Times(F.CI, y);
          return F.Arg(
              F.Plus(v1, x, F.Times(F.Sqrt(F.Plus(F.CN1, v1, x)), F.Sqrt(F.Plus(F.C1, v1, x)))));
        }
        case ID.ArcCoth: {
          // -Arg(1-1/(x+I*y))/2+Arg(1+1/(x+I*y))/2
          IExpr v1 = F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN1);
          return F.Plus(F.Times(F.CN1D2, F.Arg(F.Subtract(F.C1, v1))),
              F.Times(F.C1D2, F.Arg(F.Plus(F.C1, v1))));
        }
        case ID.ArcCsch: {
          // Arg(Sqrt(1+1/(x+I*y)^2)+1/(x+I*y))
          IExpr v1 = F.Plus(x, F.Times(F.CI, y));
          return F.Arg(F.Plus(F.Sqrt(F.Plus(F.C1, F.Power(v1, F.CN2))), F.Power(v1, F.CN1)));
        }
        case ID.ArcSech: {
          // Arg(1/(x+I*y)+(1+1/(x+I*y))*Sqrt((1-x-I*y)/(1+x+I*y)))
          IExpr v1 = F.Power(F.Plus(x, F.Times(F.CI, y)), F.CN1);
          IExpr v2 = F.Times(F.CI, y);
          return F.Arg(F.Plus(v1,
              F.Times(F.Plus(F.C1, v1), F.Sqrt(F.Times(F.Power(F.Plus(F.C1, v2, x), F.CN1),
                  F.Plus(F.C1, F.Negate(x), F.Times(F.CNI, y)))))));
        }
        case ID.ArcSinh: {
          // Arg(x+Sqrt(1+(x+I*y)^2)+I*y)
          IExpr v1 = F.Times(F.CI, y);
          return F.Arg(F.Plus(v1, x, F.Sqrt(F.Plus(F.C1, F.Sqr(F.Plus(v1, x))))));
        }
        case ID.ArcTanh: {
          // -Arg(1-x-I*y)/2+Arg(1+x+I*y)/2
          return F.Plus(F.Times(F.CN1D2, F.Arg(F.Plus(F.C1, F.Negate(x), F.Times(F.CNI, y)))),
              F.Times(F.C1D2, F.Arg(F.Plus(F.C1, x, F.Times(F.CI, y)))));
        }
        default:
          break;
      }
      return F.NIL;
    }
  }

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] option,
      final EvalEngine engine) {
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
      // TODO implement for other TargetFunctions than Im,Re
      IAST targetFunctions = option[0].makeList();
      setAssumptions(arg1, arg2, oldAssumptions, engine);

      IExpr result = arg1;
      temp = result.accept(new ComplexExpandVisitor(engine));
      if (temp.isPresent()) {
        result = engine.evaluate(temp);
      }
      return result;
    } finally {
      engine.setAssumptions(oldAssumptions);
    }
  }

  private static void setAssumptions(IExpr arg1, final IAST arg2, IAssumptions oldAssumptions,
      final EvalEngine engine) {
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
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, S.TargetFunctions, F.List(S.Im, S.Re));
  }
}
