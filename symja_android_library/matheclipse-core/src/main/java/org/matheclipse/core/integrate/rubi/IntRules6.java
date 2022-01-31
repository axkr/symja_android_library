package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AppellF1;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C1DSqrt3;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.C6;
import static org.matheclipse.core.expression.F.C7;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN1D3;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CSqrt3;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Hypergeometric2F1;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumSimplerQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules6 {
  public static IAST RULES =
      List(
          IIntegrate(121,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT,
                          x_)), CN1D2),
                      Power(Plus(c_,
                          Times(d_DEFAULT, x_)), CN1D2),
                      Power(Plus(e_, Times(f_DEFAULT, x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Times(b, Plus(c, Times(d, x)),
                              Power(Subtract(Times(b, c), Times(a, d)), CN1))),
                          Power(Plus(c, Times(d, x)), CN1D2)),
                      Integrate(
                          Power(
                              Times(Sqrt(Plus(a, Times(b, x))),
                                  Sqrt(Plus(
                                      Times(b, c, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                      Times(b, d, x,
                                          Power(Subtract(Times(b, c), Times(a, d)), CN1)))),
                                  Sqrt(Plus(e, Times(f, x)))),
                              CN1),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x),
                      Not(GtQ(Times(Subtract(Times(b, c), Times(a, d)), Power(b,
                          CN1)), C0)),
                      SimplerQ(Plus(a, Times(b, x)), Plus(c, Times(d, x))),
                      SimplerQ(Plus(a, Times(b, x)), Plus(e, Times(f, x)))))),
          IIntegrate(122,
              Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, x_)), CN1D2), Power(Plus(c_,
                  Times(d_DEFAULT, x_)), CN1D2),
                  Power(Plus(e_, Times(f_DEFAULT, x_)), CN1D2)), x_Symbol),
              Condition(Dist(
                  Times(
                      Sqrt(Times(b, Plus(e, Times(f, x)),
                          Power(Subtract(Times(b, e), Times(a, f)), CN1))),
                      Power(Plus(e, Times(f, x)), CN1D2)),
                  Integrate(
                      Power(
                          Times(Sqrt(Plus(a, Times(b, x))), Sqrt(Plus(c, Times(d, x))),
                              Sqrt(Plus(Times(b, e, Power(Subtract(Times(b, e), Times(a, f)), CN1)),
                                  Times(b, f, x, Power(Subtract(Times(b, e), Times(a, f)), CN1))))),
                          CN1),
                      x),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f), x), Not(GtQ(
                      Times(Subtract(Times(b, e), Times(a, f)), Power(b, CN1)), C0))))),
          IIntegrate(123,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D3), Power(
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1D3)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(q,
                              Rt(Times(b, Subtract(Times(b, e), Times(a, f)),
                                  Power(Subtract(Times(b, c), Times(a, d)), CN2)), C3))),
                      Plus(
                          Negate(
                              Simp(
                                  Times(
                                      Log(Plus(a,
                                          Times(b, x))),
                                      Power(Times(C2, q, Subtract(Times(b, c), Times(a, d))), CN1)),
                                  x)),
                          Negate(
                              Simp(
                                  Times(CSqrt3,
                                      ArcTan(Plus(C1DSqrt3,
                                          Times(C2, q, Power(Plus(c, Times(d, x)), QQ(2L, 3L)),
                                              Power(
                                                  Times(CSqrt3, Power(Plus(e, Times(f, x)), C1D3)),
                                                  CN1)))),
                                      Power(Times(C2, q, Subtract(Times(b, c), Times(a, d))), CN1)),
                                  x)),
                          Simp(
                              Times(C3,
                                  Log(Subtract(Times(q, Power(Plus(c, Times(d, x)), QQ(2L, 3L))),
                                      Power(Plus(e, Times(f, x)), C1D3))),
                                  Power(Times(C4, q, Subtract(Times(b, c), Times(a, d))), CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), EqQ(
                          Subtract(Subtract(Times(C2, b, d, e),
                              Times(b, c, f)), Times(a, d, f)),
                          C0)))),
          IIntegrate(124,
              Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D3),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1D3)), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), QQ(2L, 3L)),
                              Power(Plus(e, Times(f, x)), QQ(2L,
                                  3L)),
                              Power(
                                  Times(
                                      Plus(m, C1), Subtract(Times(b, c), Times(a,
                                          d)),
                                      Subtract(Times(b, e), Times(a, f))),
                                  CN1)),
                          x),
                      Dist(
                          Times(f,
                              Power(
                                  Times(
                                      C6, Plus(m, C1), Subtract(Times(b, c), Times(a, d)), Subtract(
                                          Times(b, e), Times(a, f))),
                                  CN1)),
                          Integrate(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Subtract(
                                  Subtract(Times(a, d, Plus(Times(C3, m), C1)),
                                      Times(C3, b, c, Plus(Times(C3, m), C5))),
                                  Times(C2, b, d, Plus(Times(C3, m), C7), x)),
                              Power(Times(Power(Plus(c, Times(d, x)), C1D3),
                                  Power(Plus(e, Times(f, x)), C1D3)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x),
                      EqQ(Subtract(Subtract(Times(C2, b, d, e), Times(b, c, f)), Times(a, d, f)),
                          C0),
                      ILtQ(m, CN1)))),
          IIntegrate(125,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), p_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(Times(a, c), Times(b, d, Sqr(x))), m),
                          Power(Times(f, x), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, f, m, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a,
                          d)), C0),
                      EqQ(Subtract(m, n), C0), GtQ(a, C0), GtQ(c, C0)))),
          IIntegrate(126,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), p_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, x)), FracPart(m)),
                          Power(Plus(c, Times(d, x)), FracPart(
                              m)),
                          Power(Power(Plus(Times(a, c), Times(b, d, Sqr(x))), FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(Plus(Times(a, c), Times(b, d, Sqr(x))), m),
                              Power(Times(f, x), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, f, m, n, p), x), EqQ(Plus(Times(b, c), Times(a, d)),
                      C0), EqQ(Subtract(m, n), C0)))),
          IIntegrate(127,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), p_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Plus(a, Times(b, x)), n), Power(Plus(c, Times(d, x)), n),
                              Power(Times(f, x), p)),
                          Power(Plus(a, Times(b, x)), Subtract(m, n)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, f, m, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), IGtQ(Subtract(m,
                          n), C0),
                      NeQ(Plus(m, n, p, C2), C0)))),
          IIntegrate(128,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT), Power(
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)),
                                  n),
                              Power(Plus(e, Times(f, x)), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n,
                      p), x), Or(IGtQ(m, C0),
                          And(ILtQ(m, C0), ILtQ(n, C0)))))),
          IIntegrate(129,
              Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT), Power(
                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Plus(n, C1)),
                              Power(Plus(e, Times(f, x)), Plus(p,
                                  C1)),
                              Power(
                                  Times(Plus(m, C1), Subtract(Times(b, c), Times(a, d)),
                                      Subtract(Times(b, e), Times(a, f))),
                                  CN1)),
                          x),
                      Dist(
                          Power(
                              Times(
                                  Plus(m, C1), Subtract(Times(b, c), Times(a,
                                      d)),
                                  Subtract(Times(b, e), Times(a, f))),
                              CN1),
                          Integrate(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p),
                              Simp(Subtract(
                                  Subtract(Times(a, d, f, Plus(m, C1)),
                                      Times(b,
                                          Plus(Times(d, e, Plus(m, n, C2)),
                                              Times(c, f, Plus(m, p, C2))))),
                                  Times(b, d, f, Plus(m, n, p, C3), x)), x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), ILtQ(Plus(m, n, p, C2), C0),
                      NeQ(m, CN1),
                      Or(SumSimplerQ(m, C1),
                          And(Not(And(NeQ(n, CN1), SumSimplerQ(n, C1))),
                              Not(And(NeQ(p, CN1), SumSimplerQ(p, C1)))))))),
          IIntegrate(130,
              Integrate(Times(Power(Times(e_DEFAULT, x_), p_),
                  Power(Plus(a_, Times(b_DEFAULT, x_)), m_), Power(Plus(c_, Times(d_DEFAULT, x_)),
                      n_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(p))),
                      Dist(Times(k, Power(e, CN1)),
                          Subst(
                              Integrate(Times(Power(x, Subtract(Times(k, Plus(p, C1)), C1)),
                                  Power(Plus(a, Times(b, Power(x, k), Power(e, CN1))), m),
                                  Power(Plus(c, Times(d, Power(x, k), Power(e, CN1))), n)), x),
                              x, Power(Times(e, x), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), FractionQ(p), IntegerQ(m)))),
          IIntegrate(131,
              Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_DEFAULT),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_)), x_Symbol),
              Condition(Simp(Times(Power(Subtract(Times(b, c), Times(a, d)), n),
                  Power(Plus(a, Times(b, x)), Plus(m, C1)),
                  Hypergeometric2F1(Plus(m, C1), Negate(n), Plus(m, C2),
                      Times(CN1, Subtract(Times(d, e), Times(c, f)), Plus(a, Times(b, x)),
                          Power(Times(Subtract(Times(b, c), Times(a, d)), Plus(e, Times(f, x))),
                              CN1))),
                  Power(
                      Times(Plus(m, C1), Power(Subtract(Times(b, e), Times(a, f)), Plus(n, C1)),
                          Power(Plus(e, Times(f, x)), Plus(m, C1))),
                      CN1)),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p),
                      x), EqQ(Plus(m, n, p, C2), C0), ILtQ(n, C0)))),
          IIntegrate(132,
              Integrate(Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)),
                      n_),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_)), x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                          Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f,
                              x)), Plus(p,
                                  C1)),
                          Hypergeometric2F1(
                              Plus(m, C1), Negate(n), Plus(m, C2),
                              Times(
                                  CN1, Subtract(Times(d, e), Times(c, f)), Plus(a, Times(b, x)),
                                  Power(
                                      Times(Subtract(Times(b, c), Times(a, d)),
                                          Plus(e, Times(f, x))),
                                      CN1))),
                          Power(Times(Subtract(Times(b, e), Times(a, f)), Plus(m, C1),
                              Power(Times(Subtract(Times(b, e), Times(a, f)), Plus(c, Times(d, x)),
                                  Power(Times(Subtract(Times(b, c), Times(a, d)),
                                      Plus(e, Times(f, x))), CN1)),
                                  n)),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), EqQ(Plus(m, n, p, C2), C0),
                      Not(IntegerQ(n))))),
          IIntegrate(133,
              Integrate(
                  Times(
                      Power(Times(b_DEFAULT, x_), m_), Power(Plus(c_,
                          Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_, Times(f_DEFAULT, x_)), p_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(c, n), Power(e, p), Power(Times(b, x), Plus(m, C1)),
                          AppellF1(
                              Plus(m, C1), Negate(n), Negate(p), Plus(m, C2), Times(CN1, d, x,
                                  Power(c, CN1)),
                              Times(CN1, f, x, Power(e, CN1))),
                          Power(Times(b, Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List(b, c, d, e, f, m, n, p), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      GtQ(c, C0), Or(IntegerQ(p), GtQ(e, C0))))),
          IIntegrate(134,
              Integrate(
                  Times(
                      Power(Times(b_DEFAULT, x_), m_), Power(Plus(c_,
                          Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_, Times(f_DEFAULT, x_)), p_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Plus(c, Times(d, x)), Plus(n, C1)),
                          AppellF1(Plus(n, C1), Negate(m), Negate(p), Plus(n, C2),
                              Plus(C1, Times(d, x, Power(c, CN1))), Times(CN1, f,
                                  Plus(c, Times(d, x)), Power(Subtract(Times(d, e), Times(c, f)),
                                      CN1))),
                          Power(
                              Times(d, Plus(n, C1),
                                  Power(Times(CN1, d, Power(Times(b, c), CN1)), m),
                                  Power(Times(d, Power(Subtract(Times(d, e), Times(c, f)), CN1)),
                                      p)),
                              CN1)),
                      x),
                  And(FreeQ(List(b, c, d, e, f, m, n, p), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      GtQ(Times(CN1, d, Power(Times(b, c), CN1)), C0), Or(
                          IntegerQ(p), GtQ(Times(d, Power(Subtract(Times(d, e), Times(c, f)), CN1)),
                              C0))))),
          IIntegrate(135,
              Integrate(
                  Times(
                      Power(Times(b_DEFAULT, x_), m_), Power(Plus(c_,
                          Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_, Times(f_DEFAULT, x_)), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(n)), Power(Plus(c, Times(d,
                              x)), FracPart(
                                  n)),
                          Power(Power(Plus(C1, Times(d, x, Power(c, CN1))), FracPart(n)), CN1)),
                      Integrate(Times(Power(Times(b, x), m),
                          Power(Plus(C1, Times(d, x, Power(c, CN1))),
                              n),
                          Power(Plus(e, Times(f, x)), p)), x),
                      x),
                  And(FreeQ(List(b, c, d, e, f, m, n, p), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      Not(GtQ(c, C0))))),
          IIntegrate(136,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_)),
                  x_Symbol),
              Condition(
                  Simp(Times(Power(Subtract(Times(b, e), Times(a, f)), p),
                      Power(Plus(a, Times(b, x)), Plus(m, C1)),
                      AppellF1(Plus(m, C1), Negate(n), Negate(p), Plus(m, C2), Times(CN1, d,
                          Plus(a, Times(b, x)), Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Times(CN1, f, Plus(a, Times(b, x)),
                              Power(Subtract(Times(b, e), Times(a, f)), CN1))),
                      Power(
                          Times(Power(b, Plus(p, C1)), Plus(m, C1),
                              Power(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)), n)),
                          CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      IntegerQ(p),
                      GtQ(Times(b,
                          Power(Subtract(Times(b, c), Times(a, d)), CN1)), C0),
                      Not(And(
                          GtQ(Times(d,
                              Power(Subtract(Times(d, a), Times(c, b)), CN1)), C0),
                          SimplerQ(Plus(c, Times(d, x)), Plus(a, Times(b, x)))))))),
          IIntegrate(137,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(c, Times(d, x)), FracPart(n)),
                          Power(
                              Times(
                                  Power(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                      IntPart(n)),
                                  Power(
                                      Times(b, Plus(c, Times(d, x)),
                                          Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                      FracPart(n))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, x)), m),
                              Power(
                                  Plus(Times(b, c, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                      Times(b, d, x,
                                          Power(Subtract(Times(b, c), Times(a, d)), CN1))),
                                  n),
                              Power(Plus(e, Times(f, x)), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      IntegerQ(p),
                      Not(GtQ(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)), C0)),
                      Not(SimplerQ(Plus(c, Times(d, x)), Plus(a, Times(b, x))))))),
          IIntegrate(138,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_)),
                  x_Symbol),
              Condition(Simp(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                  AppellF1(Plus(m, C1), Negate(n), Negate(p), Plus(m, C2),
                      Times(CN1, d, Plus(a, Times(b, x)),
                          Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                      Times(CN1, f, Plus(a, Times(b, x)),
                          Power(Subtract(Times(b, e), Times(a, f)), CN1))),
                  Power(
                      Times(b, Plus(m, C1),
                          Power(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)), n),
                          Power(Times(b, Power(Subtract(Times(b, e), Times(a, f)), CN1)), p)),
                      CN1)),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      Not(IntegerQ(p)),
                      GtQ(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)), C0),
                      GtQ(Times(b, Power(Subtract(Times(b, e), Times(a, f)), CN1)), C0),
                      Not(And(GtQ(Times(d, Power(Subtract(Times(d, a), Times(c, b)), CN1)), C0),
                          GtQ(Times(d, Power(Subtract(Times(d, e), Times(c, f)), CN1)), C0),
                          SimplerQ(Plus(c, Times(d, x)), Plus(a, Times(b, x))))),
                      Not(And(
                          GtQ(Times(f, Power(Subtract(Times(f, a), Times(e,
                              b)), CN1)), C0),
                          GtQ(Times(f, Power(Subtract(Times(f, c), Times(e, d)), CN1)), C0),
                          SimplerQ(Plus(e, Times(f, x)), Plus(a, Times(b, x)))))))),
          IIntegrate(139,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT,
                          x_)), m_),
                      Power(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(e,
                              Times(f, x)), FracPart(p)),
                          Power(
                              Times(Power(
                                  Times(b, Power(Subtract(Times(b, e), Times(a, f)), CN1)), IntPart(
                                      p)),
                                  Power(Times(b, Plus(e, Times(f, x)), Power(
                                      Subtract(Times(b, e), Times(a, f)), CN1)), FracPart(
                                          p))),
                              CN1)),
                      Integrate(Times(Power(Plus(a, Times(b, x)), m),
                          Power(Plus(c, Times(d, x)), n),
                          Power(
                              Plus(Times(b, e, Power(Subtract(Times(b, e), Times(a, f)), CN1)),
                                  Times(b, f, x, Power(Subtract(Times(b, e), Times(a, f)), CN1))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      Not(IntegerQ(p)),
                      GtQ(Times(b, Power(Subtract(Times(b, c),
                          Times(a, d)), CN1)), C0),
                      Not(GtQ(Times(b, Power(Subtract(Times(b, e), Times(a, f)), CN1)), C0))))),
          IIntegrate(140,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT,
                          x_)), m_),
                      Power(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_)), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(c, Times(d, x)), FracPart(n)),
                          Power(
                              Times(
                                  Power(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                      IntPart(n)),
                                  Power(
                                      Times(b, Plus(c, Times(d, x)),
                                          Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                      FracPart(n))),
                              CN1)),
                      Integrate(Times(Power(Plus(a, Times(b, x)), m),
                          Power(
                              Plus(Times(b, c, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                                  Times(b, d, x, Power(Subtract(Times(b, c), Times(a, d)), CN1))),
                              n),
                          Power(Plus(e, Times(f, x)), p)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      Not(IntegerQ(p)),
                      Not(GtQ(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)), C0)),
                      Not(SimplerQ(Plus(c, Times(d, x)), Plus(a, Times(b, x)))),
                      Not(SimplerQ(Plus(e, Times(f, x)), Plus(a, Times(b, x))))))));
}
