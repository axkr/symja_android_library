package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
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
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules64 {
  public static IAST RULES =
      List(
          IIntegrate(1281,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(
                              x_)), Times(c_DEFAULT,
                                  Power(x_, C4))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              d, Power(Times(f, x), Plus(m,
                                  C1)),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), Plus(p,
                                  C1)),
                              Power(Times(a, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(a, Sqr(f),
                              Plus(m, C1)), CN1),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C2)),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p),
                                  Simp(
                                      Subtract(
                                          Subtract(Times(a, e, Plus(m, C1)),
                                              Times(b, d, Plus(m, Times(C2, p), C3))),
                                          Times(c, d, Plus(m, Times(C4, p), C5), Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, p), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), LtQ(m, CN1),
                      IntegerQ(Times(C2, p)), Or(IntegerQ(p), IntegerQ(m))))),
          IIntegrate(1282,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(d, Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(a, Times(c, Power(x, C4))),
                                  Plus(p, C1)),
                              Power(Times(a, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(Power(Times(a, Sqr(f), Plus(m, C1)), CN1),
                          Integrate(Times(Power(Times(f, x), Plus(m, C2)),
                              Power(Plus(a, Times(c, Power(x, C4))), p),
                              Subtract(Times(a, e, Plus(m, C1)),
                                  Times(c, d, Plus(m, Times(C4, p), C5), Sqr(x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, p), x), LtQ(m, CN1), IntegerQ(Times(C2, p)),
                      Or(IntegerQ(p), IntegerQ(m))))),
          IIntegrate(1283,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r,
                              Rt(Times(c, Subtract(Times(C2, c, d), Times(b,
                                  e)), Power(e,
                                      CN1)),
                                  C2))),
                      Plus(
                          Dist(Times(C1D2, e),
                              Integrate(Times(Power(Times(f, x), m),
                                  Power(Plus(Times(c, d, Power(e, CN1)), Times(CN1, r, x),
                                      Times(c, Sqr(x))), CN1)),
                                  x),
                              x),
                          Dist(Times(C1D2, e),
                              Integrate(Times(Power(Times(f, x), m),
                                  Power(Plus(Times(c, d, Power(e, CN1)), Times(r, x),
                                      Times(c, Sqr(x))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, m), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Subtract(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      GtQ(Times(d, Power(e,
                          CN1)), C0),
                      PosQ(Times(c, Subtract(Times(C2, c, d), Times(b, e)), Power(e, CN1)))))),
          IIntegrate(1284,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1)),
                  x_Symbol),
              Condition(With(List(Set(r, Rt(Times(C2, Sqr(c), d, Power(e, CN1)), C2))), Plus(Dist(
                  Times(C1D2, e),
                  Integrate(Times(Power(Times(f, x), m),
                      Power(Plus(Times(c, d, Power(e, CN1)), Times(CN1, r, x), Times(c, Sqr(x))),
                          CN1)),
                      x),
                  x),
                  Dist(Times(C1D2, e),
                      Integrate(
                          Times(Power(Times(f, x), m),
                              Power(Plus(Times(c, d, Power(e, CN1)), Times(r, x), Times(c, Sqr(x))),
                                  CN1)),
                          x),
                      x))),
                  And(FreeQ(List(a, c, d, e, f, m), x),
                      EqQ(Subtract(Times(c, Sqr(d)),
                          Times(a, Sqr(e))), C0),
                      GtQ(Times(d, Power(e, CN1)), C0)))),
          IIntegrate(1285,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                      Plus(
                          Dist(
                              Plus(Times(C1D2, e),
                                  Times(Subtract(Times(C2, c, d), Times(b, e)),
                                      Power(Times(C2, q), CN1))),
                              Integrate(Times(Power(Times(f, x), m),
                                  Power(Plus(Times(C1D2, b), Times(CN1, C1D2, q), Times(c, Sqr(x))),
                                      CN1)),
                                  x),
                              x),
                          Dist(
                              Subtract(Times(C1D2, e),
                                  Times(Subtract(Times(C2, c, d), Times(b, e)),
                                      Power(Times(C2, q), CN1))),
                              Integrate(Times(Power(Times(f, x), m),
                                  Power(Plus(Times(C1D2, b), Times(C1D2, q), Times(c, Sqr(x))),
                                      CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f,
                      m), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0)))),
          IIntegrate(1286,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(CN1, a, c), C2))),
                      Plus(
                          Negate(
                              Dist(Plus(Times(C1D2, e), Times(c, d, Power(Times(C2, q), CN1))),
                                  Integrate(
                                      Times(Power(Times(f, x), m),
                                          Power(Subtract(q, Times(c, Sqr(x))), CN1)),
                                      x),
                                  x)),
                          Dist(Subtract(Times(C1D2, e), Times(c, d, Power(Times(C2, q), CN1))),
                              Integrate(Times(Power(Times(f, x), m),
                                  Power(Plus(q, Times(c, Sqr(x))), CN1)), x),
                              x))),
                  FreeQ(List(a, c, d, e, f, m), x))),
          IIntegrate(1287,
              Integrate(Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT), Power(Plus(a_,
                      Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                      C0), IntegerQ(q), IntegerQ(m)))),
          IIntegrate(1288,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT), Power(
                          Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q), Power(
                                  Plus(a, Times(c, Power(x, C4))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, m), x), IntegerQ(q), IntegerQ(m)))),
          IIntegrate(1289,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Power(Times(f, x), m),
                          Times(Power(Plus(d, Times(e, Sqr(x))), q), Power(
                              Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                      C0), IntegerQ(q), Not(IntegerQ(m))))),
          IIntegrate(1290,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT), Power(
                          Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Power(Times(f, x), m),
                          Times(Power(Plus(d, Times(e, Sqr(x))), q),
                              Power(Plus(a, Times(c, Power(x, C4))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, m), x), IntegerQ(q), Not(IntegerQ(m))))),
          IIntegrate(1291,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, Sqr(x_))), q_),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(
                              x_)), Times(c_DEFAULT,
                                  Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(Power(f, C4), Power(c,
                              CN2)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C4)),
                              Plus(Times(c, d), Times(CN1, b, e), Times(c, e, Sqr(x))), Power(
                                  Plus(d, Times(e, Sqr(x))), Subtract(q, C1))),
                              x),
                          x),
                      Dist(Times(Power(f, C4), Power(c, CN2)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C4)),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                                  Simp(Plus(Times(a, Subtract(Times(c, d), Times(b, e))),
                                      Times(Plus(Times(b, c, d), Times(CN1, Sqr(b), e),
                                          Times(a, c, e)), Sqr(x))),
                                      x),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      Not(IntegerQ(q)), GtQ(q, C0), GtQ(m, C3)))),
          IIntegrate(1292,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                          Sqr(x_))), q_),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(Power(f, C4), Power(c, CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C4)),
                                  Power(Plus(d, Times(e, Sqr(x))), q)),
                              x),
                          x),
                      Dist(
                          Times(a, Power(f, C4), Power(c,
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C4)),
                                  Power(Plus(d,
                                      Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(c, Power(x, C4))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, q), x), Not(IntegerQ(q)), GtQ(m, C3)))),
          IIntegrate(1293,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, Sqr(x_))), q_),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(e, Sqr(f), Power(c, CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1))),
                              x),
                          x),
                      Dist(
                          Times(Sqr(f), Power(c,
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                                  Simp(
                                      Subtract(Times(a, e), Times(
                                          Subtract(Times(c, d), Times(b, e)), Sqr(x))),
                                      x),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      Not(IntegerQ(q)), GtQ(q, C0), GtQ(m, C1), LeQ(m, C3)))),
          IIntegrate(1294,
              Integrate(Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), q_), Power(
                      Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(e, Sqr(f), Power(c, CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1))),
                              x),
                          x),
                      Dist(Times(Sqr(f), Power(c, CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                                  Simp(Subtract(Times(a, e), Times(c, d, Sqr(x))), x), Power(
                                      Plus(a, Times(c, Power(x, C4))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e,
                      f), x), Not(
                          IntegerQ(q)),
                      GtQ(q, C0), GtQ(m, C1), LeQ(m, C3)))),
          IIntegrate(1295,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, Sqr(x_))), q_),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(d, Power(a, CN1)),
                          Integrate(Times(Power(Times(f, x), m),
                              Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1))), x),
                          x),
                      Dist(Power(Times(a, Sqr(f)), CN1),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                                  Simp(Plus(Times(b, d), Times(CN1, a, e), Times(c, d, Sqr(x))), x),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Sqr(b), Times(C4, a,
                      c)), C0), Not(IntegerQ(
                          q)),
                      GtQ(q, C0), LtQ(m, C0)))),
          IIntegrate(1296,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT,
                          Sqr(x_))), q_),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(Dist(Times(d, Power(a, CN1)),
                      Integrate(
                          Times(Power(Times(f, x), m),
                              Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1))),
                          x),
                      x),
                      Dist(Power(Times(a, Sqr(f)), CN1),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(q, C1)),
                                  Simp(Subtract(Times(a, e), Times(c, d, Sqr(x))), x), Power(
                                      Plus(a, Times(c, Power(x, C4))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f), x), Not(IntegerQ(q)), GtQ(q, C0), LtQ(m, C0)))),
          IIntegrate(1297,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, Sqr(x_))), q_),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(Sqr(d), Power(f, C4),
                              Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C4)),
                                  Power(Plus(d, Times(e, Sqr(x))), q)),
                              x),
                          x),
                      Dist(Times(Power(f,
                          C4),
                          Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                              CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C4)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Simp(Plus(Times(a, d),
                                  Times(Subtract(Times(b, d), Times(a, e)), Sqr(x))), x),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      Not(IntegerQ(q)), LtQ(q, CN1), GtQ(m, C3)))),
          IIntegrate(1298,
              Integrate(Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1)), x_Symbol),
              Condition(Subtract(Dist(
                  Times(Sqr(d), Power(f, C4), Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                  Integrate(Times(
                      Power(Times(f, x), Subtract(m, C4)), Power(Plus(d, Times(e, Sqr(x))), q)), x),
                  x),
                  Dist(Times(a, Power(f, C4), Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                      Integrate(Times(Power(Times(f, x), Subtract(m, C4)),
                          Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                          Subtract(d, Times(e, Sqr(x))),
                          Power(Plus(a, Times(c, Power(x, C4))), CN1)), x),
                      x)),
                  And(FreeQ(List(a, c, d, e, f), x), Not(IntegerQ(q)), LtQ(q, CN1), GtQ(m, C3)))),
          IIntegrate(1299,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), q_),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Dist(
                          Times(d, e, Sqr(f),
                              Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                                  CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C2)),
                              Power(Plus(d, Times(e, Sqr(x))), q)), x),
                          x)),
                      Dist(
                          Times(Sqr(f),
                              Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Simp(Plus(Times(a, e), Times(c, d, Sqr(x))), x), Power(Plus(a,
                                      Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Sqr(b),
                      Times(C4, a, c)), C0), Not(
                          IntegerQ(q)),
                      LtQ(q, CN1), GtQ(m, C1), LeQ(m, C3)))),
          IIntegrate(1300,
              Integrate(Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1)), x_Symbol),
              Condition(
                  Plus(
                      Negate(Dist(
                          Times(d, e, Sqr(f), Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C2)),
                              Power(Plus(d, Times(e, Sqr(x))), q)), x),
                          x)),
                      Dist(Times(Sqr(f), Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C2)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Simp(Plus(Times(a, e), Times(c, d, Sqr(x))), x),
                              Power(Plus(a, Times(c, Power(x, C4))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f), x), Not(IntegerQ(q)), LtQ(q, CN1), GtQ(m, C1),
                      LeQ(m, C3)))));
}
