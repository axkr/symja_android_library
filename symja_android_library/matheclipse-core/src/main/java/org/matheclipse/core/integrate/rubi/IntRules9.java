package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN1D3;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.EllipticE;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.i_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.i;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CannotIntegrate;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumSimplerQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules9 {
  public static IAST RULES =
      List(
          IIntegrate(181,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(h, Power(b, CN1)),
                          Integrate(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p),
                              Power(Plus(g, Times(h, x)), Subtract(q, C1))), x),
                          x),
                      Dist(Times(Subtract(Times(b, g), Times(a, h)), Power(b, CN1)),
                          Integrate(Times(Power(Plus(a, Times(b, x)), m),
                              Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p),
                              Power(Plus(g, Times(h, x)), Subtract(q, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n, p), x), IGtQ(q, C0),
                      Or(SumSimplerQ(m, C1),
                          And(Not(SumSimplerQ(n, C1)), Not(SumSimplerQ(p, C1))))))),
          IIntegrate(182,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), p_DEFAULT),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  CannotIntegrate(
                      Times(
                          Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)), n), Power(
                              Plus(e, Times(f, x)), p),
                          Power(Plus(g, Times(h, x)), q)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, g, h, m, n, p, q), x))),
          IIntegrate(183,
              Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, u_)), m_DEFAULT),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, u_)), n_DEFAULT),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, u_)), p_DEFAULT),
                  Power(Plus(g_DEFAULT, Times(h_DEFAULT, u_)), q_DEFAULT)), x_Symbol),
              Condition(
                  Dist(Power(Coefficient(u, x, C1), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)), n),
                                  Power(Plus(e, Times(f, x)), p), Power(Plus(g, Times(h, x)), q)),
                              x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n, p,
                      q), x), LinearQ(u,
                          x),
                      NeQ(u, x)))),
          IIntegrate(184,
              Integrate(Power(Times(i_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_),
                  Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), q_)), r_), x_Symbol),
              Condition(
                  Dist(Times(
                      Power(Times(i, Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)), n),
                          Power(Plus(e, Times(f, x)), p), Power(Plus(g, Times(h, x)), q)), r),
                      Power(Times(Power(Plus(a, Times(b, x)), Times(m, r)),
                          Power(Plus(c, Times(d, x)), Times(n, r)),
                          Power(Plus(e, Times(f, x)), Times(p, r)),
                          Power(Plus(g, Times(h, x)), Times(q, r))), CN1)),
                      Integrate(Times(Power(Plus(a, Times(b, x)), Times(m, r)),
                          Power(Plus(c, Times(d, x)), Times(n, r)),
                          Power(Plus(e, Times(f, x)), Times(p, r)),
                          Power(Plus(g, Times(h, x)), Times(q, r))), x),
                      x),
                  FreeQ(List(a, b, c, d, e, f, g, h, i, m, n, p, q, r), x))),
          IIntegrate(185, Integrate(Power(u_, m_), x_Symbol), Condition(
              Integrate(Power(ExpandToSum(u, x), m), x),
              And(FreeQ(m, x), LinearQ(u, x), Not(LinearMatchQ(u, x))))),
          IIntegrate(186, Integrate(Times(Power(u_, m_DEFAULT), Power(v_, n_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), m), Power(ExpandToSum(v, x), n)), x),
                  And(FreeQ(List(m, n), x), LinearQ(List(u, v), x),
                      Not(LinearMatchQ(List(u, v), x))))),
          IIntegrate(187,
              Integrate(
                  Times(Power(u_, m_DEFAULT), Power(v_, n_DEFAULT),
                      Power(w_, p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(ExpandToSum(u, x), m), Power(ExpandToSum(v, x), n),
                          Power(ExpandToSum(w, x), p)),
                      x),
                  And(FreeQ(List(m, n,
                      p), x), LinearQ(List(u, v, w),
                          x),
                      Not(LinearMatchQ(List(u, v, w), x))))),
          IIntegrate(188,
              Integrate(
                  Times(Power(u_, m_DEFAULT), Power(v_, n_DEFAULT), Power(w_, p_DEFAULT),
                      Power(z_, q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(ExpandToSum(u, x), m), Power(ExpandToSum(v, x), n),
                          Power(ExpandToSum(w, x), p), Power(ExpandToSum(z, x), q)),
                      x),
                  And(FreeQ(List(m, n, p, q), x), LinearQ(List(u, v, w, z), x),
                      Not(LinearMatchQ(List(u, v, w, z), x))))),
          IIntegrate(189, Integrate(Power(Times(b_DEFAULT, Power(x_, n_)), p_), x_Symbol),
              Condition(
                  Dist(
                      Times(Power(b, IntPart(p)), Power(Times(b,
                          Power(x, n)), FracPart(p)), Power(Power(x, Times(n, FracPart(p))),
                              CN1)),
                      Integrate(Power(x, Times(n, p)), x), x),
                  FreeQ(List(b, n, p), x))),
          IIntegrate(190, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_), x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(Times(Power(x, Subtract(Power(n, CN1), C1)),
                              Power(Plus(a, Times(b, x)), p)), x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, p), x), FractionQ(n), IntegerQ(Power(n, CN1))))),
          IIntegrate(
              191, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  p_), x_Symbol),
              Condition(
                  Simp(
                      Times(x, Power(Plus(a, Times(b, Power(x, n))), Plus(p,
                          C1)), Power(a,
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, n, p), x), EqQ(Plus(Power(n, CN1), p, C1), C0)))),
          IIntegrate(
              192, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  p_), x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(x, Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(Times(a, n, Plus(p, C1)), CN1)), x)),
                      Dist(
                          Times(Plus(Times(n, Plus(p, C1)), C1), Power(Times(a, n, Plus(p, C1)),
                              CN1)),
                          Integrate(Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)), x), x)),
                  And(FreeQ(List(a, b, n,
                      p), x), ILtQ(Simplify(Plus(Power(n, CN1), p, C1)),
                          C0),
                      NeQ(p, CN1)))),
          IIntegrate(193, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_), x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(x, Times(n, p)),
                          Power(Plus(b, Times(a, Power(Power(x, n), CN1))), p)),
                      x),
                  And(FreeQ(List(a, b), x), LtQ(n, C0), IntegerQ(p)))),
          IIntegrate(194, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_), x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Power(Plus(a, Times(b, Power(x, n))), p), x),
                      x),
                  And(FreeQ(List(a, b), x), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(
              195, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  p_), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(x, Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(Times(n, p), C1), CN1)),
                          x),
                      Dist(
                          Times(a, n, p, Power(Plus(Times(n, p), C1), CN1)), Integrate(
                              Power(Plus(a, Times(b, Power(x, n))), Subtract(p, C1)), x),
                          x)),
                  And(FreeQ(List(a, b), x), IGtQ(n, C0), GtQ(p, C0),
                      Or(IntegerQ(Times(C2, p)), And(EqQ(n, C2), IntegerQ(Times(C4, p))),
                          And(EqQ(n, C2), IntegerQ(Times(C3, p))),
                          LtQ(Denominator(Plus(p, Power(n, CN1))), Denominator(p)))))),
          IIntegrate(196,
              Integrate(Power(Plus(a_, Times(b_DEFAULT,
                  Sqr(x_))), QQ(-5L,
                      4L)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(C2,
                          EllipticE(
                              Times(C1D2, C1,
                                  ArcTan(Times(Rt(Times(b, Power(a, CN1)), C2), x))),
                              C2),
                          Power(Times(Power(a, QQ(5L, 4L)), Rt(Times(b, Power(a, CN1)), C2)), CN1)),
                      x),
                  And(FreeQ(List(a, b), x), GtQ(a, C0), PosQ(Times(b, Power(a, CN1)))))),
          IIntegrate(197,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))),
                  QQ(-5L, 4L)), x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(C1, Times(b, Sqr(x), Power(a, CN1))), C1D4), Power(Times(a,
                              Power(Plus(a, Times(b, Sqr(x))), C1D4)), CN1)),
                      Integrate(Power(Plus(C1, Times(b, Sqr(x),
                          Power(a, CN1))), QQ(-5L,
                              4L)),
                          x),
                      x),
                  And(FreeQ(List(a, b), x), PosQ(a), PosQ(Times(b, Power(a, CN1)))))),
          IIntegrate(198,
              Integrate(Power(Plus(a_, Times(b_DEFAULT,
                  Sqr(x_))), QQ(-7L,
                      6L)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(
                          Times(
                              Power(Plus(a, Times(b, Sqr(x))), QQ(2L,
                                  3L)),
                              Power(Times(a, Power(Plus(a, Times(b, Sqr(x))), CN1)), QQ(2L, 3L))),
                          CN1),
                      Subst(
                          Integrate(Power(Subtract(C1, Times(b, Sqr(x))),
                              CN1D3), x),
                          x, Times(x, Power(Plus(a, Times(b, Sqr(x))), CN1D2))),
                      x),
                  FreeQ(List(a, b), x))),
          IIntegrate(
              199, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  p_), x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(x, Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                  Power(Times(a, n, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(Times(n, Plus(p, C1)), C1), Power(Times(a, n, Plus(p, C1)),
                              CN1)),
                          Integrate(Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)), x), x)),
                  And(FreeQ(List(a, b), x), IGtQ(n, C0), LtQ(p, CN1),
                      Or(IntegerQ(Times(C2, p)), And(Equal(n, C2), IntegerQ(Times(C4, p))),
                          And(Equal(n, C2), IntegerQ(Times(C3, p))), Less(
                              Denominator(Plus(p, Power(n, CN1))), Denominator(p)))))),
          IIntegrate(200,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1), x_Symbol),
              Condition(
                  Plus(
                      Dist(Power(Times(C3, Sqr(Rt(a, C3))), CN1),
                          Integrate(Power(Plus(Rt(a, C3), Times(Rt(b, C3), x)), CN1), x), x),
                      Dist(Power(Times(C3, Sqr(Rt(a, C3))), CN1),
                          Integrate(Times(Subtract(Times(C2, Rt(a, C3)), Times(Rt(b, C3), x)),
                              Power(Plus(Sqr(Rt(a, C3)), Times(CN1, Rt(a, C3), Rt(b, C3), x),
                                  Times(Sqr(Rt(b, C3)), Sqr(x))), CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b), x))));
}
