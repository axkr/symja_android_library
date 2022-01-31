package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Together;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
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
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules62 {
  public static IAST RULES =
      List(
          IIntegrate(1241,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(d, Times(e, Sqr(x))), q),
                          Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p)),
                      x),
                  FreeQ(List(a, b, c, d, e, p, q), x))),
          IIntegrate(1242,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))), q_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(d, Times(e, Sqr(x))),
                              q),
                          Power(Plus(a, Times(c, Power(x, C4))), p)),
                      x),
                  FreeQ(List(a, c, d, e, p, q), x))),
          IIntegrate(1243,
              Integrate(
                  Times(Power(x_, m_DEFAULT), Power(Times(e_DEFAULT, Sqr(x_)), q_),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(C2, Power(e, Times(C1D2, Subtract(m, C1)))), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Times(e, x), Plus(q, Times(C1D2, Subtract(m, C1)))),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                              x),
                          x, Sqr(x)),
                      x),
                  And(FreeQ(List(a, b, c, e, p,
                      q), x), Not(
                          IntegerQ(q)),
                      IntegerQ(Times(C1D2, Subtract(m, C1)))))),
          IIntegrate(1244,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Times(e_DEFAULT,
                          Sqr(x_)), q_),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(C2,
                          Power(e, Times(C1D2, Subtract(m, C1)))), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Times(e, x), Plus(q, Times(C1D2, Subtract(m, C1)))),
                                  Power(Plus(a, Times(c, Sqr(x))), p)),
                              x),
                          x, Sqr(x)),
                      x),
                  And(FreeQ(List(a, c, e, p,
                      q), x), Not(
                          IntegerQ(q)),
                      IntegerQ(Times(C1D2, Subtract(m, C1)))))),
          IIntegrate(1245,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Times(e_DEFAULT,
                          Sqr(x_)), q_),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(
                              x_)), Times(c_DEFAULT,
                                  Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(e, IntPart(q)), Power(Times(e, Sqr(x)), FracPart(q)),
                          Power(
                              Times(Power(f, Times(C2, IntPart(q))),
                                  Power(Times(f, x), Times(C2, FracPart(q)))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Times(f, x), Plus(m, Times(C2, q))), Power(Plus(a,
                                  Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, e, f, m, p, q), x), Not(IntegerQ(q))))),
          IIntegrate(1246,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Times(e_DEFAULT,
                          Sqr(x_)), q_),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(q)), Power(Times(e,
                              Sqr(x)), FracPart(q)),
                          Power(
                              Times(
                                  Power(f, Times(C2, IntPart(q))), Power(Times(f, x),
                                      Times(C2, FracPart(q)))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Times(f, x), Plus(m, Times(C2, q))), Power(
                                  Plus(a, Times(c, Power(x, C4))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, e, f, m, p, q), x), Not(IntegerQ(q))))),
          IIntegrate(1247,
              Integrate(
                  Times(
                      x_, Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(C1D2,
                      Subst(
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), q),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                              x),
                          x, Sqr(x)),
                      x),
                  FreeQ(List(a, b, c, d, e, p, q), x))),
          IIntegrate(1248,
              Integrate(
                  Times(
                      x_, Power(Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))), q_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(C1D2,
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), q), Power(Plus(a, Times(c, Sqr(x))),
                                      p)),
                              x),
                          x, Sqr(x)),
                      x),
                  FreeQ(List(a, c, d, e, p, q), x))),
          IIntegrate(1249,
              Integrate(
                  Times(Power(x_, m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(C1D2,
                      Subst(
                          Integrate(
                              Times(Power(x, Times(C1D2, Subtract(m, C1))),
                                  Power(Plus(d, Times(e, x)), q), Power(
                                      Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                              x),
                          x, Sqr(x)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p, q), x),
                      EqQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      Not(IntegerQ(p)), IGtQ(Times(C1D2, Plus(m, C1)), C0)))),
          IIntegrate(1250,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), FracPart(p)),
                          Power(Times(Power(c, IntPart(p)),
                              Power(Plus(Times(C1D2, b), Times(c, Sqr(x))),
                                  Times(C2, FracPart(p)))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q),
                              Power(Plus(Times(C1D2, b), Times(c, Sqr(x))), Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p,
                      q), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      Not(IntegerQ(p))))),
          IIntegrate(1251,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(C1D2,
                      Subst(
                          Integrate(
                              Times(Power(x, Times(C1D2, Subtract(m, C1))),
                                  Power(Plus(d, Times(e, x)), q),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                              x),
                          x, Sqr(x)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p, q), x),
                      IntegerQ(Times(C1D2, Subtract(m, C1)))))),
          IIntegrate(1252,
              Integrate(Times(Power(x_, m_DEFAULT),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT), Power(
                      Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(Dist(C1D2,
                  Subst(
                      Integrate(
                          Times(Power(x, Times(C1D2, Subtract(m, C1))),
                              Power(Plus(d, Times(e, x)), q), Power(Plus(a, Times(c, Sqr(x))), p)),
                          x),
                      x, Sqr(x)),
                  x), And(FreeQ(List(a, c, d, e, p, q), x), IntegerQ(Times(C1D2, Plus(m, C1)))))),
          IIntegrate(1253,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Times(f, x), m), Power(Plus(d, Times(e,
                              Sqr(x))), Plus(q,
                                  p)),
                          Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, q), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(1254,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Times(f, x), m), Power(Plus(d, Times(e,
                              Sqr(x))), Plus(q,
                                  p)),
                          Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))), p)),
                      x),
                  And(FreeQ(List(a, c, d, e, f, q, m,
                      q), x), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(1255,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          p_)),
                  x_Symbol),
              Condition(Dist(Times(
                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), FracPart(p)),
                  Power(
                      Times(Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                          Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))),
                              FracPart(p))),
                      CN1)),
                  Integrate(
                      Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), Plus(q, p)),
                          Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))), p)),
                      x),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p, q), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      EqQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      Not(IntegerQ(p))))),
          IIntegrate(1256,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_), Power(
                          Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(c, Power(x, C4))), FracPart(p)),
                          Power(Times(Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))),
                                  FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), Plus(q,
                                  p)),
                              Power(Plus(Times(a, Power(d, CN1)), Times(c, Sqr(x), Power(e, CN1))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, m, p, q),
                      x), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      Not(IntegerQ(p))))),
          IIntegrate(1257,
              Integrate(Times(Power(x_, m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Negate(d), Subtract(Times(C1D2, m), C1)),
                              Power(
                                  Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), p),
                              x, Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Times(C2, Power(e, Plus(Times(C2, p), Times(C1D2, m))),
                                  Plus(q, C1)), CN1)),
                          x),
                      Dist(
                          Power(
                              Times(C2, Power(e, Plus(Times(C2, p),
                                  Times(C1D2, m))), Plus(q,
                                      C1)),
                              CN1),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  ExpandToSum(Together(Times(C1, Subtract(
                                      Times(C2, Power(e, Plus(Times(C2, p), Times(C1D2, m))),
                                          Plus(q, C1), Power(x, m),
                                          Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                              p)),
                                      Times(Power(Negate(d), Subtract(Times(C1D2, m), C1)),
                                          Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                              Times(a, Sqr(e))), p),
                                          Plus(d, Times(e, Plus(Times(C2, q), C3), Sqr(x))))),
                                      Power(Plus(d, Times(e, Sqr(x))), CN1))), x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      IGtQ(p, C0), ILtQ(q, CN1), IGtQ(Times(C1D2, m), C0)))),
          IIntegrate(
              1258, Integrate(Times(Power(x_, m_DEFAULT),
                  Power(Plus(d_, Times(e_DEFAULT,
                      Sqr(x_))), q_),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Negate(d), Subtract(Times(C1D2, m), C1)),
                              Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), p), x,
                              Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Times(C2, Power(e, Plus(Times(C2, p), Times(C1D2, m))),
                                  Plus(q, C1)), CN1)),
                          x),
                      Dist(
                          Power(
                              Times(C2, Power(e, Plus(Times(C2, p),
                                  Times(C1D2, m))), Plus(q,
                                      C1)),
                              CN1),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  ExpandToSum(Together(Times(C1,
                                      Subtract(
                                          Times(C2, Power(e, Plus(Times(C2, p), Times(C1D2, m))),
                                              Plus(q, C1), Power(x, m),
                                              Power(Plus(a, Times(c, Power(x, C4))), p)),
                                          Times(Power(Negate(d), Subtract(Times(C1D2, m), C1)),
                                              Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), p),
                                              Plus(d, Times(e, Plus(Times(C2, q), C3), Sqr(x))))),
                                      Power(Plus(d, Times(e, Sqr(x))), CN1))), x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d,
                      e), x), IGtQ(p,
                          C0),
                      ILtQ(q, CN1), IGtQ(Times(C1D2, m), C0)))),
          IIntegrate(1259,
              Integrate(
                  Times(
                      Power(x_, m_), Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), q_),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(
                              x_)), Times(c_DEFAULT,
                                  Power(x_, C4))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Negate(d), Subtract(Times(C1D2, m),
                                  C1)),
                              Power(
                                  Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), p),
                              x, Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)), Power(
                                  Times(C2, Power(e, Plus(Times(C2, p), Times(C1D2, m))), Plus(q,
                                      C1)),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              Power(Negate(d), Subtract(Times(C1D2, m),
                                  C1)),
                              Power(Times(C2, Power(e, Times(C2, p)), Plus(q, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(x, m), Power(Plus(d, Times(e,
                                      Sqr(x))), Plus(q,
                                          C1)),
                                  ExpandToSum(Together(Times(C1, Subtract(
                                      Times(
                                          C2, Power(Negate(d), Plus(Times(CN1, C1D2, m), C1)),
                                          Power(e, Times(C2, p)), Plus(q, C1),
                                          Power(
                                              Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                              p)),
                                      Times(
                                          Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                              Times(a, Sqr(e))), p),
                                          Power(Times(Power(e, Times(C1D2, m)), Power(x, m)), CN1),
                                          Plus(d, Times(e, Plus(Times(C2, q), C3), Sqr(x))))),
                                      Power(Plus(d, Times(e, Sqr(x))), CN1))), x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      IGtQ(p, C0), ILtQ(q, CN1), ILtQ(Times(C1D2, m), C0)))),
          IIntegrate(1260,
              Integrate(Times(Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_DEFAULT)), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Negate(d), Subtract(Times(C1D2, m), C1)),
                              Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), p), x,
                              Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Times(C2, Power(e, Plus(Times(C2, p), Times(C1D2, m))),
                                  Plus(q, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Power(Negate(d), Subtract(Times(C1D2, m),
                                  C1)),
                              Power(Times(C2, Power(e, Times(C2, p)), Plus(q, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(x, m), Power(Plus(d, Times(e,
                                      Sqr(x))), Plus(q,
                                          C1)),
                                  ExpandToSum(Together(Times(C1, Subtract(
                                      Times(C2, Power(Negate(d), Plus(Times(CN1, C1D2, m), C1)),
                                          Power(e, Times(C2, p)), Plus(q, C1),
                                          Power(Plus(a, Times(c, Power(x, C4))), p)),
                                      Times(Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), p),
                                          Power(Times(Power(e, Times(C1D2, m)), Power(x, m)), CN1),
                                          Plus(d, Times(e, Plus(Times(C2, q), C3), Sqr(x))))),
                                      Power(Plus(d, Times(e, Sqr(x))), CN1))), x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e), x), IGtQ(p, C0), ILtQ(q, CN1),
                      ILtQ(Times(C1D2, m), C0)))));
}
