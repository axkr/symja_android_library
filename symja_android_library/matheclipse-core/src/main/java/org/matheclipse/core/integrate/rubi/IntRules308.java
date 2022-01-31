package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CN5;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules308 {
  public static IAST RULES =
      List(
          IIntegrate(6161,
              Integrate(
                  Exp(Times(ArcTanh(Times(c_DEFAULT, Plus(a_, Times(b_DEFAULT, x_)))),
                      n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(C1, Times(a, c), Times(b, c,
                              x)), Times(C1D2,
                                  n)),
                          Power(
                              Power(
                                  Subtract(Subtract(C1, Times(a, c)),
                                      Times(b, c, x)),
                                  Times(C1D2, n)),
                              CN1)),
                      x),
                  FreeQ(List(a, b, c, n), x))),
          IIntegrate(6162, Integrate(
              Times(Exp(
                  Times(ArcTanh(Times(c_DEFAULT, Plus(a_, Times(b_DEFAULT, x_)))), n_)),
                  Power(x_, m_)),
              x_Symbol),
              Condition(
                  Dist(
                      Times(C4, Power(Times(n, Power(b, Plus(m,
                          C1)), Power(c,
                              Plus(m, C1))),
                          CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, Times(C2,
                                      Power(n, CN1))),
                                  Power(Plus(CN1, Times(CN1, a, c),
                                      Times(Subtract(C1, Times(a, c)),
                                          Power(x, Times(C2, Power(n, CN1))))),
                                      m),
                                  Power(Power(Plus(C1, Power(x, Times(C2, Power(n, CN1)))),
                                      Plus(m, C2)), CN1)),
                              x),
                          x,
                          Times(
                              Power(Plus(C1, Times(c, Plus(a, Times(b, x)))), Times(C1D2,
                                  n)),
                              Power(Power(Subtract(C1, Times(c, Plus(a, Times(b, x)))),
                                  Times(C1D2, n)), CN1))),
                      x),
                  And(FreeQ(List(a, b, c), x), ILtQ(m, C0), LtQ(CN1, n, C1)))),
          IIntegrate(6163,
              Integrate(
                  Times(Exp(
                      Times(ArcTanh(Times(c_DEFAULT, Plus(a_, Times(b_DEFAULT, x_)))), n_DEFAULT)),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(d, Times(e, x)), m),
                          Power(Plus(C1, Times(a, c), Times(b, c, x)), Times(C1D2, n)),
                          Power(Power(Subtract(Subtract(C1, Times(a, c)), Times(b, c, x)),
                              Times(C1D2, n)), CN1)),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(6164,
              Integrate(Times(Exp(Times(ArcTanh(Plus(a_, Times(b_DEFAULT, x_))), n_DEFAULT)),
                  u_DEFAULT, Power(Plus(c_, Times(d_DEFAULT, x_), Times(e_DEFAULT, Sqr(x_))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(c,
                          Power(Subtract(C1, Sqr(a)), CN1)), p),
                      Integrate(
                          Times(u,
                              Power(Subtract(Subtract(C1, a), Times(b, x)), Subtract(p,
                                  Times(C1D2, n))),
                              Power(Plus(C1, a, Times(b, x)), Plus(p, Times(C1D2, n)))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x),
                      EqQ(Subtract(Times(b, d), Times(C2, a, e)), C0),
                      EqQ(Plus(Times(Sqr(b), c),
                          Times(e, Subtract(C1, Sqr(a)))), C0),
                      Or(IntegerQ(p), GtQ(Times(c, Power(Subtract(C1, Sqr(a)), CN1)), C0))))),
          IIntegrate(6165,
              Integrate(
                  Times(
                      Exp(Times(ArcTanh(Plus(a_, Times(b_DEFAULT, x_))),
                          n_DEFAULT)),
                      u_DEFAULT, Power(Plus(c_, Times(d_DEFAULT, x_), Times(e_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(Plus(c, Times(d, x), Times(e, Sqr(x))), p),
                      Power(Power(Subtract(Subtract(Subtract(C1, Sqr(a)), Times(C2, a, b, x)),
                          Times(Sqr(b), Sqr(x))), p), CN1)),
                      Integrate(
                          Times(u,
                              Power(Subtract(Subtract(Subtract(C1, Sqr(a)), Times(C2, a, b, x)),
                                  Times(Sqr(b), Sqr(x))), p),
                              Exp(Times(n, ArcTanh(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x),
                      EqQ(Subtract(Times(b, d), Times(C2, a,
                          e)), C0),
                      EqQ(Plus(Times(Sqr(
                          b), c), Times(e,
                              Subtract(C1, Sqr(a)))),
                          C0),
                      Not(Or(IntegerQ(p), GtQ(Times(c, Power(Subtract(C1, Sqr(a)), CN1)), C0)))))),
          IIntegrate(6166,
              Integrate(
                  Times(
                      Exp(Times(
                          ArcTanh(
                              Times(c_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1))),
                          n_DEFAULT)),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          u, Exp(
                              Times(n,
                                  ArcCoth(Plus(Times(a, Power(c, CN1)),
                                      Times(b, x, Power(c, CN1))))))),
                      x),
                  FreeQ(List(a, b, c, n), x))),
          IIntegrate(6167,
              Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_)),
                  u_DEFAULT), x_Symbol),
              Condition(
                  Dist(
                      Power(CN1, Times(C1D2,
                          n)),
                      Integrate(Times(u, Exp(Times(n, ArcTanh(Times(a, x))))), x), x),
                  And(FreeQ(a, x), IntegerQ(Times(C1D2, n))))),
          IIntegrate(6168,
              Integrate(Exp(
                  Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)), x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(C1, Times(x, Power(a, CN1))),
                                      Times(C1D2, Plus(n, C1))),
                                  Power(
                                      Times(Sqr(x),
                                          Power(
                                              Subtract(C1, Times(x, Power(a, CN1))),
                                              Times(C1D2, Subtract(n, C1))),
                                          Sqrt(Subtract(C1, Times(Sqr(x), Power(a, CN2))))),
                                      CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(a, x), IntegerQ(Times(C1D2, Subtract(n, C1)))))),
          IIntegrate(6169,
              Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)),
                  Power(x_, m_DEFAULT)), x_Symbol),
              Condition(
                  Negate(
                      Subst(Integrate(
                          Times(Power(Plus(C1, Times(x, Power(a, CN1))), Times(C1D2, Plus(n, C1))),
                              Power(
                                  Times(Power(x, Plus(m, C2)),
                                      Power(Subtract(C1, Times(x, Power(a, CN1))),
                                          Times(C1D2, Subtract(n, C1))),
                                      Sqrt(Subtract(C1, Times(Sqr(x), Power(a, CN2))))),
                                  CN1)),
                          x), x, Power(x, CN1))),
                  And(FreeQ(a, x), IntegerQ(Times(C1D2, Subtract(n, C1))), IntegerQ(m)))),
          IIntegrate(
              6170, Integrate(Exp(
                  Times(ArcCoth(Times(a_DEFAULT, x_)), n_)), x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(Power(Plus(C1, Times(x, Power(a, CN1))), Times(C1D2, n)),
                                  Power(
                                      Times(Sqr(x),
                                          Power(Subtract(C1, Times(x, Power(a, CN1))),
                                              Times(C1D2, n))),
                                      CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, n), x), Not(IntegerQ(n))))),
          IIntegrate(6171,
              Integrate(
                  Times(Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                      n_)), Power(x_,
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(Power(Plus(C1, Times(x, Power(a, CN1))), Times(C1D2, n)),
                                  Power(Times(Power(x, Plus(m, C2)),
                                      Power(Subtract(C1, Times(x, Power(a, CN1))), Times(C1D2, n))),
                                      CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, n), x), Not(IntegerQ(n)), IntegerQ(m)))),
          IIntegrate(6172,
              Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)), Power(x_, m_)),
                  x_Symbol),
              Condition(Negate(
                  Dist(
                      Times(Power(x,
                          m), Power(Power(x, CN1), m)),
                      Subst(
                          Integrate(Times(
                              Power(Plus(C1, Times(x, Power(a, CN1))), Times(C1D2, Plus(n, C1))),
                              Power(Times(Power(x, Plus(m, C2)),
                                  Power(Subtract(C1, Times(x, Power(a, CN1))),
                                      Times(C1D2, Subtract(n, C1))),
                                  Sqrt(Subtract(C1, Times(Sqr(x), Power(a, CN2))))), CN1)),
                              x),
                          x, Power(x, CN1)),
                      x)),
                  And(FreeQ(List(a, m), x), IntegerQ(Times(C1D2, Subtract(n, C1))),
                      Not(IntegerQ(m))))),
          IIntegrate(6173,
              Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_)),
                  Power(x_, m_)), x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(Power(x, m), Power(Power(x, CN1), m)), Subst(
                              Integrate(
                                  Times(Power(Plus(C1, Times(x, Power(a, CN1))), Times(C1D2, n)),
                                      Power(Times(Power(x, Plus(m, C2)),
                                          Power(Subtract(C1, Times(x, Power(a, CN1))),
                                              Times(C1D2, n))),
                                          CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, m, n), x), Not(IntegerQ(n)), Not(IntegerQ(m))))),
          IIntegrate(6174,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Plus(C1, Times(a, x)), Power(Plus(c, Times(d,
                              x)), p),
                          Exp(Times(n, ArcCoth(Times(a, x)))), Power(Times(a, Plus(p, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Plus(Times(a,
                      c), d), C0), EqQ(p, Times(C1D2,
                          n)),
                      Not(IntegerQ(Times(C1D2, n)))))),
          IIntegrate(6175,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)), u_DEFAULT, Power(
                          Plus(c_, Times(d_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, p),
                      Integrate(
                          Times(u, Power(x, p),
                              Power(Plus(C1, Times(c, Power(Times(d, x), CN1))),
                                  p),
                              Exp(Times(n, ArcCoth(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n), x), EqQ(Subtract(Times(Sqr(a), Sqr(
                      c)), Sqr(
                          d)),
                      C0), Not(IntegerQ(Times(C1D2, n))), IntegerQ(p)))),
          IIntegrate(6176,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      u_DEFAULT, Power(Plus(c_, Times(d_DEFAULT, x_)), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(c, Times(d, x)), p),
                          Power(Times(Power(x, p),
                              Power(Plus(C1, Times(c, Power(Times(d, x), CN1))), p)), CN1)),
                      Integrate(
                          Times(u, Power(x, p),
                              Power(Plus(C1, Times(c, Power(Times(d, x), CN1))), p),
                              Exp(Times(n, ArcCoth(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x),
                      EqQ(Subtract(Times(Sqr(a), Sqr(c)),
                          Sqr(d)), C0),
                      Not(IntegerQ(Times(C1D2, n))), Not(IntegerQ(p))))),
          IIntegrate(6177,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)), Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, CN1))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(c, n),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(c, Times(d, x)), Subtract(p, n)),
                                      Power(Subtract(C1, Times(Sqr(x), Power(a, CN2))),
                                          Times(C1D2, n)),
                                      Power(x, CN2)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, p), x), EqQ(Plus(c, Times(a, d)), C0),
                      IntegerQ(Times(C1D2,
                          Subtract(n, C1))),
                      Or(IntegerQ(p), EqQ(p, Times(C1D2, n)), EqQ(p,
                          Plus(Times(C1D2, n), C1))),
                      IntegerQ(Times(C2, p))))),
          IIntegrate(6178,
              Integrate(
                  Times(
                      Exp(Times(
                          ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN1))), p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(c, n),
                          Subst(
                              Integrate(Times(Power(Plus(c, Times(d, x)), Subtract(p, n)),
                                  Power(Subtract(C1, Times(Sqr(x), Power(a, CN2))), Times(C1D2, n)),
                                  Power(Power(x, Plus(m, C2)), CN1)), x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, p), x), EqQ(Plus(c, Times(a, d)), C0),
                      IntegerQ(Times(C1D2, Subtract(n, C1))), IntegerQ(m), Or(IntegerQ(p),
                          EqQ(p, Times(C1D2, n)), EqQ(p, Plus(Times(C1D2, n),
                              C1)),
                          LtQ(CN5, m, CN1)),
                      IntegerQ(Times(C2, p))))),
          IIntegrate(6179,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN1))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(c, p),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(C1, Times(d, x, Power(c, CN1))), p),
                                      Power(Plus(C1, Times(x, Power(a, CN1))), Times(C1D2, n)),
                                      Power(Times(Sqr(x),
                                          Power(Subtract(C1, Times(x, Power(a, CN1))),
                                              Times(C1D2, n))),
                                          CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, n, p), x),
                      EqQ(Subtract(Sqr(c), Times(Sqr(a), Sqr(d))), C0), Not(
                          IntegerQ(Times(C1D2, n))),
                      Or(IntegerQ(p), GtQ(c, C0))))),
          IIntegrate(6180,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(
                          Times(a_DEFAULT, x_)), n_DEFAULT)),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, CN1))), p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(Negate(Dist(Power(c, p), Subst(
                  Integrate(Times(Power(Plus(C1, Times(d, x, Power(c, CN1))), p),
                      Power(Plus(C1, Times(x, Power(a, CN1))), Times(C1D2, n)),
                      Power(Times(Power(x, Plus(m, C2)),
                          Power(Subtract(C1, Times(x, Power(a, CN1))), Times(C1D2, n))), CN1)),
                      x),
                  x, Power(x, CN1)), x)),
                  And(FreeQ(List(a, c, d, n, p), x),
                      EqQ(Subtract(Sqr(c), Times(Sqr(a), Sqr(d))), C0),
                      Not(IntegerQ(Times(C1D2, n))), Or(IntegerQ(p), GtQ(c, C0)), IntegerQ(m)))));
}
