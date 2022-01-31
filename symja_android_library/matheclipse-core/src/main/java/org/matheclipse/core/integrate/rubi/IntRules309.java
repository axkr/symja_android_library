package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CN4;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Coth;
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
import static org.matheclipse.core.expression.F.QQ;
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
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules309 {
  public static IAST RULES =
      List(
          IIntegrate(6181,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)), Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, CN1))), p_DEFAULT),
                      Power(x_, m_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Times(Power(c, p), Power(x, m), Power(Power(x, CN1), m)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(C1, Times(d, x, Power(c, CN1))), p),
                                      Power(Plus(C1, Times(x, Power(a, CN1))), Times(C1D2, n)),
                                      Power(Times(Power(x, Plus(m, C2)),
                                          Power(Subtract(C1, Times(x, Power(a, CN1))),
                                              Times(C1D2, n))),
                                          CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, m, n, p), x),
                      EqQ(Subtract(Sqr(c), Times(Sqr(a), Sqr(
                          d))), C0),
                      Not(IntegerQ(Times(C1D2,
                          n))),
                      Or(IntegerQ(p), GtQ(c, C0)), Not(IntegerQ(m))))),
          IIntegrate(6182,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)), u_DEFAULT, Power(
                          Plus(c_, Times(d_DEFAULT, Power(x_, CN1))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(c, Times(d, Power(x, CN1))), p),
                          Power(Power(Plus(C1, Times(d, Power(Times(c, x), CN1))), p), CN1)),
                      Integrate(
                          Times(u, Power(Plus(C1, Times(d, Power(Times(c, x), CN1))), p),
                              Exp(Times(n, ArcCoth(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x),
                      EqQ(Subtract(Sqr(c), Times(Sqr(a), Sqr(d))), C0), Not(IntegerQ(
                          Times(C1D2, n))),
                      Not(Or(IntegerQ(p), GtQ(c, C0)))))),
          IIntegrate(6183,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Simp(Times(Exp(Times(n, ArcCoth(Times(a, x)))),
                      Power(Times(a, c, n), CN1)), x),
                  And(FreeQ(List(a, c, d, n), x), EqQ(Plus(Times(Sqr(a), c), d), C0),
                      Not(IntegerQ(Times(C1D2, n)))))),
          IIntegrate(6184,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_)),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Subtract(n, Times(a, x)), Exp(Times(n,
                              ArcCoth(Times(a, x)))),
                          Power(
                              Times(a, c, Subtract(Sqr(n), C1),
                                  Sqrt(Plus(c, Times(d, Sqr(x))))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, c, d,
                      n), x), EqQ(Plus(Times(Sqr(a), c), d),
                          C0),
                      Not(IntegerQ(n))))),
          IIntegrate(6185,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(Simp(Times(Plus(n, Times(C2, a, Plus(p, C1), x)),
                      Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                      Exp(Times(n, ArcCoth(Times(a, x)))), Power(Times(a, c,
                          Subtract(Sqr(n), Times(C4, Sqr(Plus(p, C1))))), CN1)),
                      x),
                      Dist(
                          Times(
                              C2, Plus(p, C1), Plus(Times(C2, p), C3), Power(Times(c,
                                  Subtract(Sqr(n), Times(C4, Sqr(Plus(p, C1))))), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)), Exp(
                                      Times(n, ArcCoth(Times(a, x))))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(Plus(Times(Sqr(a), c), d), C0),
                      Not(IntegerQ(Times(C1D2,
                          n))),
                      LtQ(p, CN1), NeQ(p, QQ(-3L, 2L)),
                      NeQ(Subtract(Sqr(n),
                          Times(C4, Sqr(Plus(p, C1)))), C0),
                      Or(IntegerQ(p), Not(IntegerQ(n)))))),
          IIntegrate(6186,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_)), x_, Power(
                          Plus(c_, Times(d_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(Subtract(C1, Times(a, n, x)), Exp(Times(n, ArcCoth(Times(a, x)))),
                              Power(Times(Sqr(a), c, Subtract(Sqr(n), C1),
                                  Sqrt(Plus(c, Times(d, Sqr(x))))), CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(Plus(Times(Sqr(a), c), d), C0),
                      Not(IntegerQ(n))))),
          IIntegrate(6187,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      x_, Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Plus(Times(C2, Plus(p, C1)), Times(a, n, x)),
                              Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                              Exp(Times(n, ArcCoth(Times(a, x)))),
                              Power(Times(Sqr(a), c, Subtract(Sqr(n), Times(C4, Sqr(Plus(p, C1))))),
                                  CN1)),
                          x),
                      Dist(Times(n, Plus(Times(C2, p), C3),
                          Power(Times(a, c, Subtract(Sqr(n), Times(C4, Sqr(Plus(p, C1))))), CN1)),
                          Integrate(Times(Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                              Exp(Times(n, ArcCoth(Times(a, x))))), x),
                          x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(Plus(Times(Sqr(a), c), d), C0),
                      Not(IntegerQ(Times(C1D2, n))), LeQ(p, CN1), NeQ(p, QQ(-3L, 2L)),
                      NeQ(Subtract(Sqr(n), Times(C4, Sqr(Plus(p, C1)))), C0),
                      Or(IntegerQ(p), Not(IntegerQ(n)))))),
          IIntegrate(6188,
              Integrate(
                  Times(Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)), Sqr(x_),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(Plus(n, Times(C2, Plus(p, C1), a, x)),
                              Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                              Exp(Times(n, ArcCoth(Times(a, x)))), Power(Times(Power(a, C3), c,
                                  Sqr(n), Subtract(Sqr(n), C1)), CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(Plus(Times(Sqr(a), c), d), C0),
                      Not(IntegerQ(Times(C1D2,
                          n))),
                      EqQ(Plus(Sqr(n), Times(C2, Plus(p, C1))), C0), NeQ(Sqr(n), C1)))),
          IIntegrate(6189,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      Sqr(x_), Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(Subtract(Simp(Times(Plus(n, Times(C2, Plus(p, C1), a, x)),
                  Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                  Exp(Times(n, ArcCoth(Times(a, x)))),
                  Power(Times(Power(a, C3), c, Subtract(Sqr(n), Times(C4, Sqr(Plus(p, C1))))),
                      CN1)),
                  x),
                  Dist(
                      Times(Plus(Sqr(n), Times(C2, Plus(p, C1))),
                          Power(Times(Sqr(a), c, Subtract(Sqr(n), Times(C4, Sqr(Plus(p, C1))))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                              Exp(Times(n, ArcCoth(Times(a, x))))),
                          x),
                      x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(Plus(Times(Sqr(a), c), d), C0),
                      Not(IntegerQ(Times(C1D2, n))), LeQ(p, CN1),
                      NeQ(Plus(Sqr(n), Times(C2,
                          Plus(p, C1))), C0),
                      NeQ(Subtract(Sqr(n),
                          Times(C4, Sqr(Plus(p, C1)))), C0),
                      Or(IntegerQ(p), Not(IntegerQ(n)))))),
          IIntegrate(6190,
              Integrate(
                  Times(Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                      n_DEFAULT)), Power(x_, m_DEFAULT), Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Times(Power(Negate(c), p), Power(Power(a, Plus(m, C1)), CN1)),
                          Subst(
                              Integrate(Times(Exp(Times(n, x)),
                                  Power(Coth(x), Plus(m, Times(C2, Plus(p, C1)))), Power(
                                      Power(Cosh(x), Times(C2, Plus(p, C1))), CN1)),
                                  x),
                              x, ArcCoth(Times(a, x))),
                          x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(Plus(Times(Sqr(a), c), d), C0),
                      Not(IntegerQ(Times(C1D2,
                          n))),
                      IntegerQ(m), LeQ(C3, m, Times(CN2, Plus(p, C1))), IntegerQ(p)))),
          IIntegrate(6191,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)), u_DEFAULT, Power(
                          Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, p),
                      Integrate(
                          Times(u, Power(x, Times(C2, p)),
                              Power(Subtract(C1, Power(Times(Sqr(a), Sqr(x)), CN1)),
                                  p),
                              Exp(Times(n, ArcCoth(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n), x), EqQ(Plus(Times(Sqr(a),
                      c), d), C0), Not(IntegerQ(
                          Times(C1D2, n))),
                      IntegerQ(p)))),
          IIntegrate(6192,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      u_DEFAULT, Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(c, Times(d, Sqr(x))), p),
                          Power(Times(Power(x, Times(C2, p)),
                              Power(Subtract(C1, Power(Times(Sqr(a), Sqr(x)), CN1)), p)), CN1)),
                      Integrate(Times(u, Power(x, Times(C2, p)),
                          Power(Subtract(C1, Power(Times(Sqr(a), Sqr(x)), CN1)), p),
                          Exp(Times(n, ArcCoth(Times(a, x))))), x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Plus(Times(Sqr(a), c), d), C0),
                      Not(IntegerQ(Times(C1D2, n))), Not(IntegerQ(p))))),
          IIntegrate(6193,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      u_DEFAULT, Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN2))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(c, p), Power(Power(a, Times(C2, p)), CN1)),
                      Integrate(
                          Times(u, Power(Plus(CN1, Times(a, x)), Subtract(p, Times(C1D2, n))),
                              Power(Plus(C1, Times(a, x)), Plus(p, Times(C1D2, n))), Power(
                                  Power(x, Times(C2, p)), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Plus(c, Times(Sqr(a), d)), C0),
                      Not(IntegerQ(Times(C1D2, n))), Or(IntegerQ(p), GtQ(c,
                          C0)),
                      IntegersQ(Times(C2, p), Plus(p, Times(C1D2, n)))))),
          IIntegrate(6194,
              Integrate(
                  Times(Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)), Power(
                      Plus(c_, Times(d_DEFAULT, Power(x_, CN2))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(Dist(Power(c, p),
                      Subst(Integrate(Times(
                          Power(Subtract(C1, Times(x, Power(a, CN1))), Subtract(p, Times(C1D2, n))),
                          Power(Plus(C1, Times(x, Power(a, CN1))), Plus(p, Times(C1D2, n))),
                          Power(x, CN2)), x), x, Power(x, CN1)),
                      x)),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Plus(c, Times(Sqr(a), d)), C0),
                      Not(IntegerQ(Times(C1D2, n))), Or(IntegerQ(p), GtQ(c,
                          C0)),
                      Not(IntegersQ(Times(C2, p), Plus(p, Times(C1D2, n))))))),
          IIntegrate(6195,
              Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT, x_)), n_DEFAULT)),
                  Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN2))), p_DEFAULT),
                  Power(x_, m_DEFAULT)), x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(c, p),
                          Subst(
                              Integrate(Times(
                                  Power(Subtract(C1, Times(x, Power(a, CN1))),
                                      Subtract(p, Times(C1D2, n))),
                                  Power(Plus(C1, Times(x, Power(a, CN1))), Plus(p, Times(C1D2, n))),
                                  Power(Power(x, Plus(m, C2)), CN1)), x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Plus(c, Times(Sqr(a), d)), C0),
                      Not(IntegerQ(Times(C1D2, n))), Or(IntegerQ(p), GtQ(c, C0)), Not(IntegersQ(
                          Times(C2, p), Plus(p, Times(C1D2, n)))),
                      IntegerQ(m)))),
          IIntegrate(6196,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(
                          Times(a_DEFAULT, x_)), n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN2))), p_DEFAULT), Power(x_, m_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Times(Power(c, p), Power(x, m), Power(Power(x, CN1), m)),
                          Subst(
                              Integrate(Times(
                                  Power(Subtract(C1, Times(x, Power(a, CN1))),
                                      Subtract(p, Times(C1D2, n))),
                                  Power(Plus(C1, Times(x, Power(a, CN1))), Plus(p, Times(C1D2, n))),
                                  Power(Power(x, Plus(m, C2)), CN1)), x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, m, n, p), x), EqQ(Plus(c, Times(Sqr(a), d)), C0),
                      Not(IntegerQ(Times(C1D2,
                          n))),
                      Or(IntegerQ(p), GtQ(c, C0)), Not(IntegersQ(Times(C2, p),
                          Plus(p, Times(C1D2, n)))),
                      Not(IntegerQ(m))))),
          IIntegrate(6197,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      u_DEFAULT, Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN2))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(c, IntPart(p)),
                          Power(Plus(c, Times(d, Power(x, CN2))), FracPart(p)),
                          Power(
                              Power(Subtract(C1, Power(Times(Sqr(a), Sqr(x)), CN1)),
                                  FracPart(p)),
                              CN1)),
                      Integrate(
                          Times(
                              u, Power(Subtract(C1, Power(Times(Sqr(a), Sqr(x)), CN1)),
                                  p),
                              Exp(Times(n, ArcCoth(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Plus(c,
                      Times(Sqr(a), d)), C0), Not(IntegerQ(Times(C1D2, n))), Not(
                          Or(IntegerQ(p), GtQ(c, C0)))))),
          IIntegrate(6198,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(c_DEFAULT, Plus(a_, Times(b_DEFAULT, x_)))),
                          n_)),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(CN1, Times(C1D2, n)),
                      Integrate(Times(u, Exp(Times(n,
                          ArcTanh(Times(c, Plus(a, Times(b, x))))))), x),
                      x),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2, n))))),
          IIntegrate(6199,
              Integrate(
                  Exp(Times(ArcCoth(Times(c_DEFAULT, Plus(a_, Times(b_DEFAULT, x_)))),
                      n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c, Plus(a, Times(b, x))), Times(C1D2, n)),
                          Power(Plus(C1, Power(Times(c, Plus(a, Times(b, x))), CN1)),
                              Times(C1D2, n)),
                          Power(Power(Plus(C1, Times(a, c), Times(b, c, x)), Times(C1D2, n)), CN1)),
                      Integrate(Times(Power(Plus(C1, Times(a, c), Times(b, c, x)), Times(C1D2, n)),
                          Power(Power(Plus(CN1, Times(a, c), Times(b, c, x)), Times(C1D2, n)),
                              CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, n), x), Not(IntegerQ(Times(C1D2, n)))))),
          IIntegrate(6200,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(c_DEFAULT, Plus(a_, Times(b_DEFAULT, x_)))),
                          n_)),
                      Power(x_, m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(CN4, Power(Times(n, Power(b, Plus(m,
                          C1)), Power(c,
                              Plus(m, C1))),
                          CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, Times(C2,
                                      Power(n, CN1))),
                                  Power(
                                      Plus(C1, Times(a, c),
                                          Times(
                                              Subtract(C1, Times(a, c)), Power(x,
                                                  Times(C2, Power(n, CN1))))),
                                      m),
                                  Power(
                                      Power(
                                          Plus(CN1, Power(x, Times(C2, Power(n, CN1)))), Plus(m,
                                              C2)),
                                      CN1)),
                              x),
                          x,
                          Times(
                              Power(Plus(C1, Power(Times(c, Plus(a, Times(b, x))), CN1)),
                                  Times(C1D2, n)),
                              Power(Power(Subtract(C1, Power(Times(c, Plus(a, Times(b, x))), CN1)),
                                  Times(C1D2, n)), CN1))),
                      x),
                  And(FreeQ(List(a, b, c), x), ILtQ(m, C0), LtQ(CN1, n, C1)))));
}
