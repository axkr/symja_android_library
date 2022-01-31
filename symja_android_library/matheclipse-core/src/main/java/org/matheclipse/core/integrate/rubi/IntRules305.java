package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
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
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.CSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules305 {
  public static IAST RULES =
      List(
          IIntegrate(6101,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTanh(Times(c_DEFAULT, Power(x_, n_))), b_DEFAULT)),
                          p_DEFAULT),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(u,
                      Power(Plus(a, Times(b, ArcTanh(Times(c, Power(x, n))))), p)), x),
                  And(FreeQ(List(a, b, c, n, p), x),
                      Or(EqQ(u, C1),
                          MatchQ(u,
                              Condition(Power(Times(d_DEFAULT, x), m_DEFAULT),
                                  FreeQ(List(d, m), x))))))),
          IIntegrate(6102,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCoth(Times(c_DEFAULT, Power(x_, n_))), b_DEFAULT)),
                          p_DEFAULT),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(u,
                      Power(Plus(a, Times(b, ArcCoth(Times(c, Power(x, n))))), p)), x),
                  And(FreeQ(List(a, b, c, n, p), x),
                      Or(EqQ(u, C1),
                          MatchQ(u,
                              Condition(Power(Times(d_DEFAULT, x), m_DEFAULT),
                                  FreeQ(List(d, m), x))))))),
          IIntegrate(6103,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(ArcTanh(Plus(c_, Times(d_DEFAULT, x_))),
                          b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(Integrate(Power(Plus(a, Times(b, ArcTanh(x))), p), x), x,
                          Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(p, C0)))),
          IIntegrate(6104,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(ArcCoth(Plus(c_, Times(d_DEFAULT, x_))),
                          b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(Integrate(Power(Plus(a, Times(b, ArcCoth(x))), p), x), x,
                          Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(p, C0)))),
          IIntegrate(6105,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(ArcTanh(Plus(c_, Times(d_DEFAULT, x_))),
                          b_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b, ArcTanh(Plus(c, Times(d, x))))),
                      p), x),
                  And(FreeQ(List(a, b, c, d, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(6106,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(ArcCoth(Plus(c_, Times(d_DEFAULT, x_))),
                          b_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b, ArcCoth(Plus(c, Times(d, x))))),
                      p), x),
                  And(FreeQ(List(a, b, c, d, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(6107,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Times(f, x,
                                      Power(d, CN1)), m),
                                  Power(Plus(a, Times(b, ArcTanh(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Subtract(Times(d, e), Times(c, f)),
                      C0), IGtQ(p, C0)))),
          IIntegrate(6108,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(Power(Times(f, x, Power(d, CN1)), m),
                                  Power(Plus(a, Times(b, ArcCoth(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Subtract(Times(d, e),
                      Times(c, f)), C0), IGtQ(p,
                          C0)))),
          IIntegrate(6109,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(e, Times(f,
                                  x)), Plus(m,
                                      C1)),
                              Power(Plus(a, Times(b, ArcTanh(Plus(c, Times(d, x))))), p),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, d, p, Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                              Power(Plus(a, Times(b, ArcTanh(Plus(c, Times(d, x))))), Subtract(p,
                                  C1)),
                              Power(Subtract(C1, Sqr(Plus(c, Times(d, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), ILtQ(m, CN1)))),
          IIntegrate(6110,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCoth(Plus(c_, Times(d_DEFAULT, x_))),
                          b_DEFAULT)), p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(e, Times(f,
                                  x)), Plus(m,
                                      C1)),
                              Power(Plus(a,
                                  Times(b, ArcCoth(Plus(c, Times(d, x))))), p),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, d, p, Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcCoth(Plus(c, Times(d, x))))), Subtract(
                                      p, C1)),
                                  Power(Subtract(C1, Sqr(Plus(c, Times(d, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), ILtQ(m, CN1)))),
          IIntegrate(6111,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Plus(Times(Subtract(Times(d, e), Times(c, f)), Power(d, CN1)),
                                          Times(f, x, Power(d, CN1))),
                                      m),
                                  Power(Plus(a, Times(b, ArcTanh(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IGtQ(p, C0)))),
          IIntegrate(6112,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Plus(Times(Subtract(Times(d, e), Times(c, f)), Power(d, CN1)),
                                          Times(f, x, Power(d, CN1))),
                                      m),
                                  Power(Plus(a, Times(b, ArcCoth(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IGtQ(p, C0)))),
          IIntegrate(6113,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(e, Times(f, x)), m), Power(
                              Plus(a, Times(b, ArcTanh(Plus(c, Times(d, x))))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(6114,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(e, Times(f, x)), m), Power(Plus(a,
                              Times(b, ArcCoth(Plus(c, Times(d, x))))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(6115,
              Integrate(
                  Times(
                      ArcTanh(Plus(c_, Times(d_DEFAULT,
                          x_))),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_DEFAULT))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(
                              Times(
                                  Log(Plus(C1, c, Times(d, x))), Power(
                                      Plus(e, Times(f, Power(x, n))), CN1)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Log(Subtract(Subtract(C1, c), Times(d, x))),
                                  Power(Plus(e, Times(f, Power(x, n))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e, f), x), RationalQ(n)))),
          IIntegrate(6116,
              Integrate(
                  Times(
                      ArcCoth(Plus(c_, Times(d_DEFAULT,
                          x_))),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_DEFAULT))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(Dist(C1D2,
                      Integrate(Times(
                          Log(Times(Plus(C1, c, Times(d, x)), Power(Plus(c, Times(d, x)), CN1))),
                          Power(Plus(e, Times(f, Power(x, n))), CN1)), x),
                      x),
                      Dist(C1D2, Integrate(Times(
                          Log(Times(Plus(CN1, c, Times(d, x)), Power(Plus(c, Times(d, x)), CN1))),
                          Power(Plus(e, Times(f, Power(x, n))), CN1)), x), x)),
                  And(FreeQ(List(c, d, e, f), x), RationalQ(n)))),
          IIntegrate(6117,
              Integrate(
                  Times(
                      ArcTanh(Plus(c_,
                          Times(d_DEFAULT, x_))),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_))), CN1)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          ArcTanh(Plus(c, Times(d, x))), Power(Plus(e, Times(f, Power(x, n))),
                              CN1)),
                      x),
                  And(FreeQ(List(c, d, e, f, n), x), Not(RationalQ(n))))),
          IIntegrate(6118,
              Integrate(
                  Times(
                      ArcCoth(Plus(c_,
                          Times(d_DEFAULT, x_))),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_))), CN1)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(ArcCoth(Plus(c, Times(d, x))),
                          Power(Plus(e, Times(f, Power(x, n))), CN1)),
                      x),
                  And(FreeQ(List(c, d, e, f, n), x), Not(RationalQ(n))))),
          IIntegrate(6119,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(Times(CN1, CSymbol, Power(d, CN2)),
                                      Times(CSymbol, Sqr(x), Power(d, CN2))), q),
                                  Power(Plus(a, Times(b, ArcTanh(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, ASymbol, BSymbol, CSymbol, p, q), x),
                      EqQ(Plus(Times(BSymbol, Subtract(C1, Sqr(c))), Times(C2, ASymbol, c, d)), C0),
                      EqQ(Subtract(Times(C2, c, CSymbol), Times(BSymbol, d)), C0)))),
          IIntegrate(6120,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(Times(
                              Power(Plus(Times(CSymbol, Power(d, CN2)),
                                  Times(CSymbol, Sqr(x), Power(d, CN2))), q),
                              Power(Plus(a, Times(b, ArcCoth(x))), p)), x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, ASymbol, BSymbol, CSymbol, p, q), x),
                      EqQ(Plus(Times(BSymbol, Subtract(C1, Sqr(c))), Times(C2, ASymbol, c, d)), C0),
                      EqQ(Subtract(Times(C2, c, CSymbol), Times(BSymbol, d)), C0)))));
}
