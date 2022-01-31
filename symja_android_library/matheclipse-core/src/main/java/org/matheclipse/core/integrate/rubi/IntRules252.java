package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CNI;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Not;
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
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
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
class IntRules252 {
  public static IAST RULES =
      List(
          IIntegrate(5041,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(ArcTan(Plus(c_, Times(d_DEFAULT, x_))),
                          b_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b, ArcTan(Plus(c, Times(d, x))))),
                      p), x),
                  And(FreeQ(List(a, b, c, d, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(5042,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(ArcCot(Plus(c_, Times(d_DEFAULT, x_))), b_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b,
                      ArcCot(Plus(c, Times(d, x))))), p), x),
                  And(FreeQ(List(a, b, c, d, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(5043,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Plus(c_, Times(d_DEFAULT, x_))),
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
                                  Power(Plus(a, Times(b, ArcTan(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Subtract(Times(d, e), Times(c, f)),
                      C0), IGtQ(p, C0)))),
          IIntegrate(5044,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(Power(Times(f, x, Power(d, CN1)), m),
                                  Power(Plus(a, Times(b, ArcCot(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Subtract(Times(d, e),
                      Times(c, f)), C0), IGtQ(p,
                          C0)))),
          IIntegrate(5045,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcTan(Plus(c_, Times(d_DEFAULT, x_))),
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
                              Power(Plus(a, Times(b, ArcTan(Plus(c, Times(d, x))))),
                                  p),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, d, p, Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcTan(Plus(c, Times(d, x))))), Subtract(p,
                                      C1)),
                                  Power(Plus(C1, Sqr(Plus(c, Times(d, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), ILtQ(m, CN1)))),
          IIntegrate(5046,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCot(Plus(c_, Times(d_DEFAULT, x_))),
                          b_DEFAULT)), p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                              Power(Plus(a,
                                  Times(b, ArcCot(Plus(c, Times(d, x))))), p),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, d, p, Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcCot(Plus(c, Times(d, x))))), Subtract(p,
                                      C1)),
                                  Power(Plus(C1, Sqr(Plus(c, Times(d, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), ILtQ(m, CN1)))),
          IIntegrate(5047,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Plus(c_, Times(d_DEFAULT, x_))),
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
                                  Power(Plus(a, Times(b, ArcTan(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), IGtQ(p, C0)))),
          IIntegrate(5048,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(Power(
                                  Plus(Times(Subtract(Times(d, e), Times(c, f)), Power(d, CN1)),
                                      Times(f, x, Power(d, CN1))),
                                  m), Power(Plus(a, Times(b, ArcCot(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), IGtQ(p, C0)))),
          IIntegrate(5049,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(e, Times(f, x)), m), Power(
                              Plus(a, Times(b, ArcTan(Plus(c, Times(d, x))))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(5050,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(e, Times(f, x)), m), Power(Plus(a,
                              Times(b, ArcCot(Plus(c, Times(d, x))))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), Not(IGtQ(p, C0))))),
          IIntegrate(5051,
              Integrate(
                  Times(
                      ArcTan(Plus(a_, Times(b_DEFAULT, x_))), Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_DEFAULT))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(Log(Subtract(Subtract(C1, Times(CI, a)), Times(CI, b, x))),
                                  Power(Plus(c, Times(d, Power(x, n))), CN1)),
                              x),
                          x),
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(
                                  Log(Plus(C1, Times(CI, a), Times(CI, b, x))), Power(Plus(c,
                                      Times(d, Power(x, n))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), RationalQ(n)))),
          IIntegrate(5052,
              Integrate(
                  Times(
                      ArcCot(Plus(a_, Times(b_DEFAULT,
                          x_))),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_DEFAULT))), CN1)),
                  x_Symbol),
              Condition(Subtract(
                  Dist(Times(C1D2, CI),
                      Integrate(Times(
                          Log(Times(Plus(CNI, a, Times(b, x)), Power(Plus(a, Times(b, x)), CN1))),
                          Power(Plus(c, Times(d, Power(x, n))), CN1)), x),
                      x),
                  Dist(Times(C1D2, CI),
                      Integrate(Times(
                          Log(Times(Plus(CI, a, Times(b, x)), Power(Plus(a, Times(b, x)), CN1))),
                          Power(Plus(c, Times(d, Power(x, n))), CN1)), x),
                      x)),
                  And(FreeQ(List(a, b, c, d), x), RationalQ(n)))),
          IIntegrate(5053,
              Integrate(
                  Times(ArcTan(Plus(a_, Times(b_DEFAULT, x_))),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), CN1)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(ArcTan(Plus(a, Times(b, x))),
                          Power(Plus(c, Times(d, Power(x, n))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), Not(RationalQ(n))))),
          IIntegrate(5054,
              Integrate(
                  Times(
                      ArcCot(Plus(a_,
                          Times(b_DEFAULT, x_))),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), CN1)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(ArcCot(Plus(a, Times(b, x))),
                          Power(Plus(c, Times(d, Power(x, n))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), Not(RationalQ(n))))),
          IIntegrate(5055,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Plus(c_, Times(d_DEFAULT, x_))),
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
                                  Power(Plus(Times(CSymbol, Power(d, CN2)),
                                      Times(CSymbol, Sqr(x), Power(d, CN2))), q),
                                  Power(Plus(a, Times(b, ArcTan(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, ASymbol, BSymbol, CSymbol, p, q), x),
                      EqQ(Subtract(Times(BSymbol, Plus(C1,
                          Sqr(c))), Times(C2, ASymbol, c,
                              d)),
                          C0),
                      EqQ(Subtract(Times(C2, c, CSymbol), Times(BSymbol, d)), C0)))),
          IIntegrate(5056,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(
                          Plus(A_DEFAULT, Times(B_DEFAULT, x_),
                              Times(C_DEFAULT, Sqr(x_))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(Times(CSymbol, Power(d, CN2)),
                                      Times(CSymbol, Sqr(x), Power(d, CN2))), q),
                                  Power(Plus(a, Times(b, ArcCot(x))), p)),
                              x),
                          x, Plus(c, Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, ASymbol, BSymbol, CSymbol, p, q), x),
                      EqQ(Subtract(Times(BSymbol, Plus(C1, Sqr(c))), Times(C2, ASymbol, c,
                          d)), C0),
                      EqQ(Subtract(Times(C2, c, CSymbol), Times(BSymbol, d)), C0)))),
          IIntegrate(5057,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTan(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(A_DEFAULT, Times(B_DEFAULT, x_),
                              Times(C_DEFAULT, Sqr(x_))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(Dist(Power(d, CN1),
                  Subst(Integrate(Times(
                      Power(Plus(Times(Subtract(Times(d, e), Times(c, f)), Power(d, CN1)),
                          Times(f, x, Power(d, CN1))), m),
                      Power(
                          Plus(Times(CSymbol, Power(d, CN2)),
                              Times(CSymbol, Sqr(x), Power(d, CN2))),
                          q),
                      Power(Plus(a, Times(b, ArcTan(x))), p)), x), x, Plus(c,
                          Times(d, x))),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, m, p, q), x),
                      EqQ(Subtract(Times(BSymbol, Plus(C1, Sqr(c))), Times(C2, ASymbol, c, d)), C0),
                      EqQ(Subtract(Times(C2, c, CSymbol), Times(BSymbol, d)), C0)))),
          IIntegrate(5058,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCot(Plus(c_, Times(d_DEFAULT, x_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(A_DEFAULT, Times(B_DEFAULT, x_),
                              Times(C_DEFAULT, Sqr(x_))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(Integrate(Times(
                          Power(Plus(Times(Subtract(Times(d, e), Times(c, f)), Power(d, CN1)),
                              Times(f, x, Power(d, CN1))), m),
                          Power(
                              Plus(Times(CSymbol, Power(d, CN2)),
                                  Times(CSymbol, Sqr(x), Power(d, CN2))),
                              q),
                          Power(Plus(a, Times(b, ArcCot(x))), p)), x), x, Plus(c,
                              Times(d, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, m, p, q), x),
                      EqQ(Subtract(Times(BSymbol, Plus(C1, Sqr(c))), Times(C2, ASymbol, c,
                          d)), C0),
                      EqQ(Subtract(Times(C2, c, CSymbol), Times(BSymbol, d)), C0)))),
          IIntegrate(
              5059, Integrate(Exp(
                  Times(ArcTan(Times(a_DEFAULT, x_)), n_)), x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Subtract(C1, Times(CI, a, x)), Times(C1D2, Plus(Times(CI, n), C1))),
                          Power(
                              Times(
                                  Power(Plus(C1, Times(CI, a, x)),
                                      Times(C1D2, Subtract(Times(CI, n), C1))),
                                  Sqrt(Plus(C1, Times(Sqr(a), Sqr(x))))),
                              CN1)),
                      x),
                  And(FreeQ(a, x), IntegerQ(Times(C1D2, Subtract(Times(CI, n), C1)))))),
          IIntegrate(5060,
              Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT, x_)), n_)), Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(x, m),
                      Power(Subtract(C1, Times(CI, a, x)), Times(C1D2, Plus(Times(CI, n), C1))),
                      Power(Times(
                          Power(Plus(C1, Times(CI, a, x)), Times(C1D2, Subtract(Times(CI, n), C1))),
                          Sqrt(Plus(C1, Times(Sqr(a), Sqr(x))))), CN1)),
                      x),
                  And(FreeQ(List(a, m), x), IntegerQ(Times(C1D2, Subtract(Times(CI, n), C1)))))));
}
