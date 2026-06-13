package org.matheclipse.core.integrate;

import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcCosh;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Erfi;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Unequal;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.x;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.Matcher;

/**
 * CRC-style integral table for fast lookup of common indefinite integrals before the Rubi rules are
 * tried.
 *
 * <p>
 * The table contains closed forms for elementary integrands with linear arguments
 * <code>a+b*x</code> and simple quadratic forms. All formulas are general antiderivatives which are
 * valid for symbolic parameters.
 */
public class IntegralTable {

  private static class LazyHolder {
    static final Matcher MATCHER = init();
  }

  private IntegralTable() {}

  /**
   * Try a fast table lookup for <code>Integrate(f, x)</code>.
   *
   * @param f the integrand
   * @param x the integration variable
   * @param engine the evaluation engine
   * @return the antiderivative or {@link F#NIL}
   */
  public static IExpr integrate(IExpr f, IExpr x, EvalEngine engine) {
    if (!Config.INTEGRATE_ALGORITHM_TABLE) {
      return F.NIL;
    }
    IExpr result = LazyHolder.MATCHER.apply(F.Integrate(f, x));
    if (result.isPresent()) {
      return engine.evaluate(result);
    }
    return F.NIL;
  }

  private static void rule(Matcher matcher, IExpr lhsIntegrand, IExpr rhs, IExpr condition) {
    matcher.caseOf(Integrate(lhsIntegrand, x_Symbol), Condition(rhs, condition));
  }

  private static Matcher init() {
    Matcher matcher = new Matcher();
    // linear argument u = a+b*x
    final IExpr u_ = Plus(a_DEFAULT, Times(b_DEFAULT, x_));
    final IExpr u = Plus(a, Times(b, x));
    final IExpr freeAB = FreeQ(list(a, b), x);

    // Sin(a+b*x) -> -Cos(a+b*x)/b
    rule(matcher, Sin(u_), Times(CN1, Power(b, CN1), Cos(u)), freeAB);
    // Cos(a+b*x) -> Sin(a+b*x)/b
    rule(matcher, Cos(u_), Times(Power(b, CN1), Sin(u)), freeAB);
    // Tan(a+b*x) -> -Log(Cos(a+b*x))/b
    rule(matcher, Tan(u_), Times(CN1, Power(b, CN1), Log(Cos(u))), freeAB);
    // Cot(a+b*x) -> Log(Sin(a+b*x))/b
    rule(matcher, Cot(u_), Times(Power(b, CN1), Log(Sin(u))), freeAB);
    // Sec(a+b*x) -> ArcTanh(Sin(a+b*x))/b
    rule(matcher, Sec(u_), Times(Power(b, CN1), ArcTanh(Sin(u))), freeAB);
    // Csc(a+b*x) -> -ArcTanh(Cos(a+b*x))/b
    rule(matcher, Csc(u_), Times(CN1, Power(b, CN1), ArcTanh(Cos(u))), freeAB);
    // Sec(a+b*x)^2 -> Tan(a+b*x)/b
    rule(matcher, Sqr(Sec(u_)), Times(Power(b, CN1), Tan(u)), freeAB);
    // Csc(a+b*x)^2 -> -Cot(a+b*x)/b
    rule(matcher, Sqr(Csc(u_)), Times(CN1, Power(b, CN1), Cot(u)), freeAB);
    // Sec(a+b*x)*Tan(a+b*x) -> Sec(a+b*x)/b
    rule(matcher, Times(Sec(u_), Tan(u_)), Times(Power(b, CN1), Sec(u)), freeAB);
    // Csc(a+b*x)*Cot(a+b*x) -> -Csc(a+b*x)/b
    rule(matcher, Times(Csc(u_), Cot(u_)), Times(CN1, Power(b, CN1), Csc(u)), freeAB);
    // Sin(a+b*x)^2 -> x/2 - Sin(2*(a+b*x))/(4*b)
    rule(matcher, Sqr(Sin(u_)),
        Plus(Times(C1D2, x), Times(CN1, Power(Times(C4, b), CN1), Sin(Times(C2, u)))), freeAB);
    // Cos(a+b*x)^2 -> x/2 + Sin(2*(a+b*x))/(4*b)
    rule(matcher, Sqr(Cos(u_)),
        Plus(Times(C1D2, x), Times(Power(Times(C4, b), CN1), Sin(Times(C2, u)))), freeAB);

    // Sinh(a+b*x) -> Cosh(a+b*x)/b
    rule(matcher, Sinh(u_), Times(Power(b, CN1), Cosh(u)), freeAB);
    // Cosh(a+b*x) -> Sinh(a+b*x)/b
    rule(matcher, Cosh(u_), Times(Power(b, CN1), Sinh(u)), freeAB);
    // Tanh(a+b*x) -> Log(Cosh(a+b*x))/b
    rule(matcher, Tanh(u_), Times(Power(b, CN1), Log(Cosh(u))), freeAB);
    // Coth(a+b*x) -> Log(Sinh(a+b*x))/b
    rule(matcher, Coth(u_), Times(Power(b, CN1), Log(Sinh(u))), freeAB);
    // Sech(a+b*x) -> ArcTan(Sinh(a+b*x))/b
    rule(matcher, Sech(u_), Times(Power(b, CN1), ArcTan(Sinh(u))), freeAB);
    // Csch(a+b*x) -> -ArcTanh(Cosh(a+b*x))/b
    rule(matcher, Csch(u_), Times(CN1, Power(b, CN1), ArcTanh(Cosh(u))), freeAB);
    // Sech(a+b*x)^2 -> Tanh(a+b*x)/b
    rule(matcher, Sqr(Sech(u_)), Times(Power(b, CN1), Tanh(u)), freeAB);
    // Csch(a+b*x)^2 -> -Coth(a+b*x)/b
    rule(matcher, Sqr(Csch(u_)), Times(CN1, Power(b, CN1), Coth(u)), freeAB);

    // E^(a+b*x) -> E^(a+b*x)/b
    rule(matcher, Power(S.E, u_), Times(Power(b, CN1), Exp(u)), freeAB);
    // c^(a+b*x) -> c^(a+b*x)/(b*Log(c)) ; c free
    rule(matcher, Power(c_, u_), Times(Power(Times(b, Log(c)), CN1), Power(c, u)),
        And(FreeQ(list(a, b, c), x), Unequal(c, S.E)));
    // E^(a+b*x^2) -> E^a*Sqrt(Pi)*Erfi(Sqrt(b)*x)/(2*Sqrt(b))
    rule(matcher, Power(S.E, Plus(a_DEFAULT, Times(b_DEFAULT, Sqr(x_)))),
        Times(Exp(a), Sqrt(S.Pi), Power(Times(C2, Sqrt(b)), CN1), Erfi(Times(Sqrt(b), x))), freeAB);

    // Log(a+b*x) -> (a+b*x)*(Log(a+b*x)-1)/b
    rule(matcher, Log(u_), Times(Power(b, CN1), u, Plus(Log(u), CN1)), freeAB);
    // Log(a+b*x)^2 -> u*(Log(u)^2 - 2*Log(u) + 2)/b
    rule(matcher, Sqr(Log(u_)), Times(Power(b, CN1), u, Plus(Sqr(Log(u)), Times(CN2, Log(u)), C2)),
        freeAB);

    // 1/(a+b*x) -> Log(a+b*x)/b
    rule(matcher, Power(Plus(a_, Times(b_DEFAULT, x_)), CN1), Times(Power(b, CN1), Log(u)), freeAB);
    // (a+b*x)^n -> (a+b*x)^(n+1)/(b*(n+1)) ; n != -1
    rule(matcher, Power(Plus(a_, Times(b_DEFAULT, x_)), n_),
        Times(Power(Times(b, Plus(n, C1)), CN1), Power(u, Plus(n, C1))),
        And(FreeQ(list(a, b, n), x), Unequal(n, CN1)));

    // ArcSin(a+b*x) -> (u*ArcSin(u)+Sqrt(1-u^2))/b
    rule(matcher, ArcSin(u_),
        Times(Power(b, CN1), Plus(Times(u, ArcSin(u)), Sqrt(Plus(C1, Times(CN1, Sqr(u)))))),
        freeAB);
    // ArcCos(a+b*x) -> (u*ArcCos(u)-Sqrt(1-u^2))/b
    rule(matcher, ArcCos(u_), Times(Power(b, CN1),
        Plus(Times(u, ArcCos(u)), Times(CN1, Sqrt(Plus(C1, Times(CN1, Sqr(u))))))), freeAB);
    // ArcTan(a+b*x) -> (u*ArcTan(u)-Log(1+u^2)/2)/b
    rule(matcher, ArcTan(u_),
        Times(Power(b, CN1), Plus(Times(u, ArcTan(u)), Times(CN1D2, Log(Plus(C1, Sqr(u)))))),
        freeAB);
    // ArcCot(a+b*x) -> (u*ArcCot(u)+Log(1+u^2)/2)/b
    rule(matcher, ArcCot(u_),
        Times(Power(b, CN1), Plus(Times(u, ArcCot(u)), Times(C1D2, Log(Plus(C1, Sqr(u)))))),
        freeAB);
    // ArcSinh(a+b*x) -> (u*ArcSinh(u)-Sqrt(u^2+1))/b
    rule(matcher, ArcSinh(u_),
        Times(Power(b, CN1), Plus(Times(u, ArcSinh(u)), Times(CN1, Sqrt(Plus(Sqr(u), C1))))),
        freeAB);
    // ArcCosh(a+b*x) -> (u*ArcCosh(u)-Sqrt(u-1)*Sqrt(u+1))/b
    rule(matcher, ArcCosh(u_), Times(Power(b, CN1),
        Plus(Times(u, ArcCosh(u)), Times(CN1, Sqrt(Plus(u, CN1)), Sqrt(Plus(u, C1))))), freeAB);
    // ArcTanh(a+b*x) -> (u*ArcTanh(u)+Log(1-u^2)/2)/b
    rule(matcher, ArcTanh(u_), Times(Power(b, CN1),
        Plus(Times(u, ArcTanh(u)), Times(C1D2, Log(Plus(C1, Times(CN1, Sqr(u))))))), freeAB);

    // E^(q*x)*Sin(r+c*x) -> E^(q*x)*(q*Sin(r+c*x)-c*Cos(r+c*x))/(q^2+c^2)
    rule(matcher,
        Times(Power(S.E, Times(q_DEFAULT, x_)), Sin(Plus(r_DEFAULT, Times(c_DEFAULT, x_)))),
        Times(Power(Plus(Sqr(q), Sqr(c)), CN1), Exp(Times(q, x)),
            Plus(Times(q, Sin(Plus(r, Times(c, x)))), Times(CN1, c, Cos(Plus(r, Times(c, x)))))),
        FreeQ(list(q, c, r), x));
    // E^(q*x)*Cos(r+c*x) -> E^(q*x)*(q*Cos(r+c*x)+c*Sin(r+c*x))/(q^2+c^2)
    rule(matcher,
        Times(Power(S.E, Times(q_DEFAULT, x_)), Cos(Plus(r_DEFAULT, Times(c_DEFAULT, x_)))),
        Times(Power(Plus(Sqr(q), Sqr(c)), CN1), Exp(Times(q, x)),
            Plus(Times(q, Cos(Plus(r, Times(c, x)))), Times(c, Sin(Plus(r, Times(c, x)))))),
        FreeQ(list(q, c, r), x));

    // 1/(a+b*x^2) -> ArcTan(Sqrt(b)*x/Sqrt(a))/(Sqrt(a)*Sqrt(b))
    rule(matcher, Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1),
        Times(Power(Times(Sqrt(a), Sqrt(b)), CN1), ArcTan(Times(Sqrt(b), x, Power(Sqrt(a), CN1)))),
        freeAB);
    // x/(a+b*x^2) -> Log(a+b*x^2)/(2*b)
    rule(matcher, Times(x_, Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1)),
        Times(Power(Times(C2, b), CN1), Log(Plus(a, Times(b, Sqr(x))))), freeAB);

    // 1/Sqrt(a+b*x^2) -> ArcSin(Sqrt(-b)*x/Sqrt(a))/Sqrt(-b) for b < 0
    rule(matcher, Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1D2),
        Times(Power(Sqrt(Times(CN1, b)), CN1),
            ArcSin(Times(Sqrt(Times(CN1, b)), x, Power(Sqrt(a), CN1)))),
        And(freeAB, F.Less(b, F.C0)));

    // 1/Sqrt(a+b*x^2) -> ArcTanh(Sqrt(b)*x/Sqrt(a+b*x^2))/Sqrt(b)
    rule(matcher, Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1D2), Times(Power(Sqrt(b), CN1),
        ArcTanh(Times(Sqrt(b), x, Power(Sqrt(Plus(a, Times(b, Sqr(x)))), CN1)))), freeAB);

    // Sqrt(a+b*x^2) -> x*Sqrt(a+b*x^2)/2 + a*ArcSin(Sqrt(-b)*x/Sqrt(a))/(2*Sqrt(-b)) for b < 0
    rule(matcher, Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), C1D2),
        Plus(Times(C1D2, x, Sqrt(Plus(a, Times(b, Sqr(x))))),
            Times(a, Power(Times(C2, Sqrt(Times(CN1, b))), CN1),
                ArcSin(Times(Sqrt(Times(CN1, b)), x, Power(Sqrt(a), CN1))))),
        And(freeAB, F.Less(b, F.C0)));

    // Sqrt(a+b*x^2) -> x*Sqrt(a+b*x^2)/2 + a*ArcTanh(Sqrt(b)*x/Sqrt(a+b*x^2))/(2*Sqrt(b))
    rule(matcher, Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), C1D2),
        Plus(Times(C1D2, x, Sqrt(Plus(a, Times(b, Sqr(x))))),
            Times(a, Power(Times(C2, Sqrt(b)), CN1),
                ArcTanh(Times(Sqrt(b), x, Power(Sqrt(Plus(a, Times(b, Sqr(x)))), CN1))))),
        freeAB);

    // 1/(a+b*x^2) -> ArcTanh(Sqrt(-b)*x/Sqrt(a))/(Sqrt(a)*Sqrt(-b)) for b < 0
    rule(matcher, Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1),
        Times(Power(Times(Sqrt(a), Sqrt(Times(CN1, b))), CN1),
            ArcTanh(Times(Sqrt(Times(CN1, b)), x, Power(Sqrt(a), CN1)))),
        And(freeAB, F.Less(b, F.C0)));

    // 1/(a+b*x^2) -> ArcTan(Sqrt(b)*x/Sqrt(a))/(Sqrt(a)*Sqrt(b))
    rule(matcher, Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1),
        Times(Power(Times(Sqrt(a), Sqrt(b)), CN1), ArcTan(Times(Sqrt(b), x, Power(Sqrt(a), CN1)))),
        freeAB);

    return matcher;
  }

  /** Force initialization of the rule table. */
  public static void initialize() {
    Matcher dummy = LazyHolder.MATCHER;
    if (dummy == null) {
      throw new IllegalStateException("IntegralTable initialization failed");
    }
  }
}
