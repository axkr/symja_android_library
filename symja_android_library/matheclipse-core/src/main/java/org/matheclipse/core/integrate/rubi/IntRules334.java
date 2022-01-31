package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.F.y_;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.CSymbol;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.y;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.DerivativeDivides;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Divides;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FalseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemoveContent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplerIntegrandQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules334 {
  public static IAST RULES =
      List(
          IIntegrate(6680,
              Integrate(
                  Times(
                      Power(Plus(A_DEFAULT,
                          Times(C_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, $(
                                      F_, Times(c_DEFAULT,
                                          Sqrt(Plus(d_DEFAULT, Times(e_DEFAULT,
                                              x_))),
                                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), CN1D2))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(C2, e, g, Power(Times(CSymbol, Subtract(Times(e, f), Times(d, g))),
                          CN1)),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, F(Times(c, x)))), n),
                              Power(x, CN1)), x),
                          x, Times(Sqrt(Plus(d, Times(e, x))), Power(Plus(f, Times(g, x)), CN1D2))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, ASymbol, CSymbol, FSymbol), x),
                      EqQ(Subtract(Times(CSymbol, d, f), Times(ASymbol, e, g)),
                          C0),
                      EqQ(Plus(Times(e, f), Times(d, g)), C0), IGtQ(n, C0)))),
          IIntegrate(6681,
              Integrate(
                  Times(
                      Power(Plus(A_DEFAULT, Times(B_DEFAULT, x_),
                          Times(C_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, $(
                                      F_,
                                      Times(
                                          c_DEFAULT, Sqrt(Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), CN1D2))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(a,
                              Times(b,
                                  F(Times(c, Sqrt(Plus(d, Times(e, x))),
                                      Power(Plus(f, Times(g, x)), CN1D2))))),
                              n),
                          Power(Plus(ASymbol, Times(BSymbol, x), Times(CSymbol, Sqr(x))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, ASymbol, BSymbol, CSymbol, FSymbol, n), x),
                      EqQ(Subtract(Times(CSymbol, d, f),
                          Times(ASymbol, e, g)), C0),
                      EqQ(Subtract(Times(BSymbol, e, g),
                          Times(CSymbol, Plus(Times(e, f), Times(d, g)))), C0),
                      Not(IGtQ(n, C0))))),
          IIntegrate(6682,
              Integrate(
                  Times(
                      Power(Plus(A_,
                          Times(C_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $(F_,
                                  Times(c_DEFAULT, Sqrt(Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), CN1D2))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(
                      Power(Plus(a,
                          Times(b,
                              F(Times(c, Sqrt(Plus(d, Times(e, x))),
                                  Power(Plus(f, Times(g, x)), CN1D2))))),
                          n),
                      Power(Plus(ASymbol, Times(CSymbol, Sqr(x))), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e, f, g, ASymbol, CSymbol, FSymbol, n), x),
                      EqQ(Subtract(Times(CSymbol, d, f), Times(ASymbol, e, g)), C0), EqQ(
                          Plus(Times(e, f), Times(d, g)), C0),
                      Not(IGtQ(n, C0))))),
          IIntegrate(6683, Integrate(Times(u_, Power(y_, CN1)), x_Symbol), With(
              List(Set(q, DerivativeDivides(y, u, x))),
              Condition(Simp(Times(q, Log(RemoveContent(y, x))), x), Not(FalseQ(q))))),
          IIntegrate(6684, Integrate(Times(u_, Power(w_, CN1), Power(y_, CN1)), x_Symbol),
              With(List(Set(q, DerivativeDivides(Times(y, w), u, x))),
                  Condition(Simp(Times(q, Log(RemoveContent(Times(y, w), x))), x),
                      Not(FalseQ(q))))),
          IIntegrate(6685, Integrate(Times(u_, Power(y_, m_DEFAULT)), x_Symbol),
              Condition(
                  With(List(Set(q, DerivativeDivides(y, u, x))),
                      Condition(Simp(Times(q, Power(y, Plus(m, C1)), Power(Plus(m, C1), CN1)), x),
                          Not(FalseQ(q)))),
                  And(FreeQ(m, x), NeQ(m, CN1)))),
          IIntegrate(6686,
              Integrate(Times(u_, Power(y_, m_DEFAULT),
                  Power(z_, n_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(Times(y, z), Times(u, Power(z, Subtract(n, m))), x))),
                      Condition(
                          Simp(Times(q, Power(y, Plus(m, C1)), Power(z, Plus(m, C1)),
                              Power(Plus(m, C1), CN1)), x),
                          Not(FalseQ(q)))),
                  And(FreeQ(List(m, n), x), NeQ(m, CN1)))),
          IIntegrate(6687, Integrate(u_, x_Symbol),
              With(List(Set(v, SimplifyIntegrand(u, x))),
                  Condition(Integrate(v, x), SimplerIntegrandQ(v, u, x)))),
          IIntegrate(6688,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              Times(
                                  e_DEFAULT,
                                  Sqrt(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))))),
                              Times(
                                  f_DEFAULT, Sqrt(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_DEFAULT)))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(Power(Subtract(Times(a, Sqr(e)), Times(c, Sqr(f))), m),
                      Integrate(
                          ExpandIntegrand(
                              Times(u,
                                  Power(Power(
                                      Subtract(Times(e, Sqrt(Plus(a, Times(b, Power(x, n))))),
                                          Times(f, Sqrt(Plus(c, Times(d, Power(x, n)))))),
                                      m), CN1)),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f,
                      n), x), ILtQ(m,
                          C0),
                      EqQ(Subtract(Times(b, Sqr(e)), Times(d, Sqr(f))), C0)))),
          IIntegrate(6689,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              Times(
                                  e_DEFAULT, Sqrt(Plus(a_DEFAULT,
                                      Times(b_DEFAULT, Power(x_, n_DEFAULT))))),
                              Times(
                                  f_DEFAULT, Sqrt(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_DEFAULT)))))),
                          m_)),
                  x_Symbol),
              Condition(Dist(Power(Subtract(Times(b, Sqr(e)), Times(d, Sqr(f))), m), Integrate(
                  ExpandIntegrand(
                      Times(u, Power(x, Times(m, n)),
                          Power(Power(Subtract(Times(e, Sqrt(Plus(a, Times(b, Power(x, n))))),
                              Times(f, Sqrt(Plus(c, Times(d, Power(x, n)))))), m), CN1)),
                      x),
                  x), x), And(
                      FreeQ(List(a, b, c, d, e, f, n), x), ILtQ(m,
                          C0),
                      EqQ(Subtract(Times(a, Sqr(e)), Times(c, Sqr(f))), C0)))),
          IIntegrate(6690,
              Integrate(
                  Times(
                      Power(u_, m_DEFAULT), Power(Plus(Times(a_DEFAULT, Power(u_, n_)), v_),
                          p_DEFAULT),
                      w_),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(u, Plus(m,
                              Times(n, p))),
                          Power(Plus(a, Times(v, Power(Power(u, n), CN1))), p), w),
                      x),
                  And(FreeQ(List(a, m, n), x), IntegerQ(p), Not(GtQ(n, C0)), Not(FreeQ(v, x))))),
          IIntegrate(6691,
              Integrate(
                  Times(
                      u_, Power(Plus(c_DEFAULT, Times(d_DEFAULT, v_)), n_DEFAULT), Power(
                          Plus(a_DEFAULT, Times(b_DEFAULT, y_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(y, u, x))),
                      Condition(
                          Dist(q,
                              Subst(
                                  Integrate(Times(Power(Plus(a, Times(b, x)), m),
                                      Power(Plus(c, Times(d, x)), n)), x),
                                  x, y),
                              x),
                          Not(FalseQ(q)))),
                  And(FreeQ(List(a, b, c, d, m, n), x), EqQ(v, y)))),
          IIntegrate(6692,
              Integrate(
                  Times(u_, Power(Plus(c_DEFAULT, Times(d_DEFAULT, v_)), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, w_)), p_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, y_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set(q, DerivativeDivides(y, u, x))),
                      Condition(
                          Dist(q,
                              Subst(Integrate(Times(Power(Plus(a, Times(b, x)), m),
                                  Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p)),
                                  x), x, y),
                              x),
                          Not(FalseQ(q)))),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), EqQ(v, y), EqQ(w, y)))),
          IIntegrate(6693,
              Integrate(
                  Times(u_, Power(Plus(c_DEFAULT, Times(d_DEFAULT, v_)), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, w_)), p_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, y_)), m_DEFAULT), Power(
                          Plus(g_DEFAULT, Times(h_DEFAULT, z_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set(r, DerivativeDivides(y, u, x))),
                      Condition(
                          Dist(r,
                              Subst(Integrate(Times(Power(Plus(a, Times(b, x)), m),
                                  Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p),
                                  Power(Plus(g, Times(h, x)), q)), x), x, y),
                              x),
                          Not(FalseQ(r)))),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n, p, q), x), EqQ(v, y), EqQ(w, y),
                      EqQ(z, y)))),
          IIntegrate(6694,
              Integrate(Times(u_DEFAULT,
                  Plus(a_, Times(b_DEFAULT, Power(y_, n_)))), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(y, u, x))),
                      Condition(
                          Plus(Dist(a, Integrate(u, x), x), Dist(Times(b, q),
                              Subst(Integrate(Power(x, n), x), x, y), x)),
                          Not(FalseQ(q)))),
                  FreeQ(List(a, b, n), x))),
          IIntegrate(6695,
              Integrate(
                  Times(u_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(y_, n_))),
                      p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(y, u, x))),
                      Condition(
                          Dist(q,
                              Subst(Integrate(Power(Plus(a, Times(b, Power(x, n))), p), x), x,
                                  y),
                              x),
                          Not(FalseQ(q)))),
                  FreeQ(List(a, b, n, p), x))),
          IIntegrate(6696,
              Integrate(
                  Times(
                      u_DEFAULT, Power(v_, m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(y_, n_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Module(List(q, r),
                      Condition(Dist(Times(q, r),
                          Subst(
                              Integrate(
                                  Times(Power(x, m), Power(Plus(a, Times(b, Power(x, n))), p)), x),
                              x, y),
                          x),
                          And(Not(FalseQ(Set(r, Divides(Power(y, m), Power(v, m), x)))),
                              Not(FalseQ(Set(q, DerivativeDivides(y, u, x))))))),
                  FreeQ(List(a, b, m, n, p), x))),
          IIntegrate(6697,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              a_DEFAULT, Times(c_DEFAULT, Power(v_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(y_, n_))),
                          p_)),
                  x_Symbol),
              Condition(With(List(Set(q, DerivativeDivides(y, u, x))),
                  Condition(Dist(q,
                      Subst(Integrate(Power(
                          Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))), p), x),
                          x, y),
                      x), Not(FalseQ(q)))),
                  And(FreeQ(List(a, b, c, n, p), x), EqQ($s("n2"), Times(C2, n)), EqQ(v, y)))),
          IIntegrate(6698,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(v_,
                                  n_)),
                              Times(c_DEFAULT, Power(w_, $p("n2", true)))),
                          p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(y_, n_)))),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(y, u, x))),
                      Condition(
                          Dist(q,
                              Subst(
                                  Integrate(Times(Plus(ASymbol, Times(BSymbol, Power(x, n))),
                                      Power(Plus(a, Times(b, Power(x, n)),
                                          Times(c, Power(x, Times(C2, n)))), p)),
                                      x),
                                  x, y),
                              x),
                          Not(FalseQ(q)))),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol, n, p), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(v,
                          y),
                      EqQ(w, y)))),
          IIntegrate(6699,
              Integrate(
                  Times(u_DEFAULT,
                      Power(Plus(a_DEFAULT,
                          Times(c_DEFAULT, Power(w_, $p("n2", true)))), p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(y_, n_)))),
                  x_Symbol),
              Condition(
                  With(List(Set(q, DerivativeDivides(y, u, x))), Condition(
                      Dist(q,
                          Subst(Integrate(Times(Plus(ASymbol, Times(BSymbol, Power(x, n))),
                              Power(Plus(a, Times(c, Power(x, Times(C2, n)))), p)), x), x, y),
                          x),
                      Not(FalseQ(q)))),
                  And(FreeQ(List(a, c, ASymbol, BSymbol, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(w, y)))));
}
