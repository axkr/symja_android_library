package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Csc;
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
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
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
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ActivateTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules214 {
  public static IAST RULES = List(
      IIntegrate(4281,
          Integrate(
              Times(
                  Plus(
                      Times(Power($($s("§csc"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                          A_DEFAULT),
                      Times(Power($($s("§csc"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))), $p("n1")),
                          B_DEFAULT),
                      Times(
                          Power($($s("§csc"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              $p("n2")),
                          C_DEFAULT)),
                  u_),
              x_Symbol),
          Condition(
              Integrate(
                  Times(
                      ActivateTrig(u), Power(Csc(Plus(a, Times(b, x))), n), Plus(ASymbol,
                          Times(BSymbol, Csc(Plus(a, Times(b, x)))), Times(CSymbol,
                              Sqr(Csc(Plus(a, Times(b, x))))))),
                  x),
              And(FreeQ(List(a, b, ASymbol, BSymbol, CSymbol, n), x), EqQ($s("n1"), Plus(n,
                  C1)), EqQ($s("n2"),
                      Plus(n, C2))))),
      IIntegrate(4282,
          Integrate(
              Times(
                  $($s("§sin"), Plus(a_DEFAULT,
                      Times(b_DEFAULT, x_))),
                  $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(
                          Sin(Plus(a, Negate(c), Times(Subtract(b, d), x))), Power(
                              Times(C2, Subtract(b, d)), CN1)),
                      x),
                  Simp(
                      Times(Sin(Plus(a, c, Times(Plus(b, d), x))), Power(Times(C2, Plus(b, d)),
                          CN1)),
                      x)),
              And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))),
      IIntegrate(4283,
          Integrate(
              Times(
                  $($s("§cos"), Plus(a_DEFAULT,
                      Times(b_DEFAULT, x_))),
                  $($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(
                          Sin(Plus(a, Negate(c),
                              Times(Subtract(b, d), x))),
                          Power(Times(C2, Subtract(b, d)), CN1)),
                      x),
                  Simp(
                      Times(Sin(Plus(a, c, Times(Plus(b, d), x))), Power(Times(C2, Plus(b, d)),
                          CN1)),
                      x)),
              And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))),
      IIntegrate(4284,
          Integrate(
              Times(
                  $($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), $($s("§sin"),
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Subtract(
                  Negate(
                      Simp(
                          Times(
                              Cos(Plus(a, Negate(c), Times(Subtract(b, d), x))), Power(
                                  Times(C2, Subtract(b, d)), CN1)),
                          x)),
                  Simp(
                      Times(Cos(Plus(a, c, Times(Plus(b, d), x))),
                          Power(Times(C2, Plus(b, d)), CN1)),
                      x)),
              And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))),
      IIntegrate(4285,
          Integrate(
              Times(Sqr($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(
                  Dist(C1D2, Integrate(Power(Times(g, Sin(Plus(c, Times(d, x)))), p),
                      x), x),
                  Dist(C1D2,
                      Integrate(
                          Times(Cos(Plus(c, Times(d, x))),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))), p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, g), x), EqQ(Subtract(Times(b, c), Times(a,
                  d)), C0), EqQ(Times(d,
                      Power(b, CN1)), C2),
                  IGtQ(Times(C1D2, p), C0)))),
      IIntegrate(4286,
          Integrate(
              Times(
                  Sqr($($s("§sin"),
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Subtract(
                  Dist(C1D2, Integrate(Power(Times(g, Sin(Plus(c, Times(d, x)))), p),
                      x), x),
                  Dist(C1D2,
                      Integrate(
                          Times(Cos(Plus(c, Times(d, x))),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))), p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, g), x), EqQ(Subtract(Times(b, c), Times(a,
                  d)), C0), EqQ(Times(d,
                      Power(b, CN1)), C2),
                  IGtQ(Times(C1D2, p), C0)))),
      IIntegrate(4287,
          Integrate(
              Times(
                  Power(Times($($s("§cos"),
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_))), e_DEFAULT), m_DEFAULT),
                  Power($($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
              x_Symbol),
          Condition(
              Dist(
                  Times(Power(C2, p), Power(Power(e, p),
                      CN1)),
                  Integrate(
                      Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), Plus(m, p)),
                          Power(Sin(Plus(a, Times(b, x))), p)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, m), x), EqQ(Subtract(Times(b, c), Times(a,
                  d)), C0), EqQ(Times(d,
                      Power(b, CN1)), C2),
                  IntegerQ(p)))),
      IIntegrate(4288,
          Integrate(
              Times(
                  Power(
                      Times(f_DEFAULT, $($s("§sin"),
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      n_DEFAULT),
                  Power($($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
              x_Symbol),
          Condition(
              Dist(
                  Times(Power(C2, p), Power(Power(f, p),
                      CN1)),
                  Integrate(
                      Times(
                          Power(Cos(
                              Plus(a, Times(b, x))), p),
                          Power(Times(f, Sin(Plus(a, Times(b, x)))), Plus(n, p))),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, f, n), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                  EqQ(Times(d, Power(b, CN1)), C2), IntegerQ(p)))),
      IIntegrate(4289,
          Integrate(
              Times(
                  Power(Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      e_DEFAULT), m_),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Simp(
                  Times(Sqr(e), Power(Times(e, Cos(Plus(a, Times(b, x)))), Subtract(m, C2)),
                      Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p,
                          C1)),
                      Power(Times(C2, b, g, Plus(p, C1)), CN1)),
                  x),
              And(FreeQ(List(a, b, c, d, e, g, m, p), x),
                  EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)),
                      C2),
                  Not(IntegerQ(p)), EqQ(Subtract(Plus(m, p), C1), C0)))),
      IIntegrate(4290,
          Integrate(
              Times(
                  Power(Times(e_DEFAULT,
                      $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), m_),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Negate(
                  Simp(
                      Times(Sqr(e), Power(Times(e, Sin(Plus(a, Times(b, x)))), Subtract(m, C2)),
                          Power(Times(g, Sin(Plus(c, Times(d, x)))),
                              Plus(p, C1)),
                          Power(Times(C2, b, g, Plus(p, C1)), CN1)),
                      x)),
              And(FreeQ(List(a, b, c, d, e, g, m, p), x),
                  EqQ(Subtract(Times(b, c),
                      Times(a, d)), C0),
                  EqQ(Times(d,
                      Power(b, CN1)), C2),
                  Not(IntegerQ(p)), EqQ(Subtract(Plus(m, p), C1), C0)))),
      IIntegrate(4291, Integrate(
          Times(
              Power(Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))), e_DEFAULT),
                  m_DEFAULT),
              Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))), p_)),
          x_Symbol),
          Condition(
              Negate(
                  Simp(Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), m),
                      Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C1)),
                      Power(Times(b, g, m), CN1)), x)),
              And(FreeQ(List(a, b, c, d, e, g, m, p), x),
                  EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)), C2),
                  Not(IntegerQ(p)), EqQ(Plus(m, Times(C2, p), C2), C0)))),
      IIntegrate(4292,
          Integrate(
              Times(
                  Power(
                      Times(e_DEFAULT,
                          $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      m_DEFAULT),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Simp(
                  Times(Power(Times(e, Sin(Plus(a, Times(b, x)))), m),
                      Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C1)), Power(Times(b, g, m),
                          CN1)),
                  x),
              And(FreeQ(List(a, b, c, d, e, g, m, p), x),
                  EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)),
                      C2),
                  Not(IntegerQ(p)), EqQ(Plus(m, Times(C2, p), C2), C0)))),
      IIntegrate(4293,
          Integrate(
              Times(
                  Power(Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))), e_DEFAULT), m_),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(
                          Sqr(e), Power(Times(e, Cos(Plus(a,
                              Times(b, x)))), Subtract(m, C2)),
                          Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C1)),
                          Power(Times(C2, b, g, Plus(p, C1)), CN1)),
                      x),
                  Dist(
                      Times(Power(e, C4), Subtract(Plus(m, p), C1),
                          Power(Times(C4, Sqr(g), Plus(p, C1)), CN1)),
                      Integrate(Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), Subtract(m, C4)),
                          Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C2))), x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, g), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                  EqQ(Times(d, Power(b,
                      CN1)), C2),
                  Not(IntegerQ(p)), GtQ(m, C2), LtQ(p, CN1), Or(GtQ(m, C3), EqQ(p,
                      QQ(-3L, 2L))),
                  IntegersQ(Times(C2, m), Times(C2, p))))),
      IIntegrate(4294,
          Integrate(
              Times(
                  Power(Times(e_DEFAULT,
                      $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), m_),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(Sqr(e), Power(Times(e, Sin(Plus(a, Times(b, x)))), Subtract(m, C2)),
                              Power(Times(g, Sin(
                                  Plus(c, Times(d, x)))), Plus(p,
                                      C1)),
                              Power(Times(C2, b, g, Plus(p, C1)), CN1)),
                          x)),
                  Dist(
                      Times(
                          Power(e, C4), Subtract(Plus(m, p),
                              C1),
                          Power(Times(C4, Sqr(g), Plus(p, C1)), CN1)),
                      Integrate(Times(Power(Times(e, Sin(Plus(a, Times(b, x)))), Subtract(m, C4)),
                          Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C2))), x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, g), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                  EqQ(Times(d, Power(b, CN1)), C2), Not(IntegerQ(
                      p)),
                  GtQ(m, C2), LtQ(p, CN1), Or(GtQ(m, C3), EqQ(p,
                      QQ(-3L, 2L))),
                  IntegersQ(Times(C2, m), Times(C2, p))))),
      IIntegrate(4295,
          Integrate(
              Times(
                  Power(Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      e_DEFAULT), m_),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), m),
                      Power(Times(g, Sin(Plus(c, Times(d, x)))),
                          Plus(p, C1)),
                      Power(Times(C2, b, g, Plus(p, C1)), CN1)), x),
                  Dist(
                      Times(
                          Sqr(e), Plus(m, Times(C2, p), C2), Power(Times(C4, Sqr(g), Plus(p, C1)),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Times(e, Cos(Plus(a, Times(b, x)))), Subtract(m, C2)), Power(
                                  Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C2))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, g), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                  EqQ(Times(d, Power(b, CN1)), C2), Not(IntegerQ(p)), GtQ(m, C1), LtQ(p, CN1),
                  NeQ(Plus(m, Times(C2,
                      p), C2), C0),
                  Or(LtQ(p, CN2), EqQ(m, C2)), IntegersQ(Times(C2, m), Times(C2, p))))),
      IIntegrate(4296,
          Integrate(
              Times(
                  Power(Times(e_DEFAULT,
                      $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), m_),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(Power(Times(e, Sin(Plus(a, Times(b, x)))), m),
                              Power(Times(g, Sin(
                                  Plus(c, Times(d, x)))), Plus(p,
                                      C1)),
                              Power(Times(C2, b, g, Plus(p, C1)), CN1)),
                          x)),
                  Dist(
                      Times(Sqr(e), Plus(m, Times(C2, p), C2),
                          Power(Times(C4, Sqr(g), Plus(p, C1)), CN1)),
                      Integrate(
                          Times(
                              Power(Times(e, Sin(Plus(a, Times(b, x)))), Subtract(m, C2)),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C2))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, g), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                  EqQ(Times(d, Power(b, CN1)), C2), Not(IntegerQ(p)), GtQ(m, C1), LtQ(p, CN1),
                  NeQ(Plus(m, Times(C2, p), C2), C0), Or(LtQ(p, CN2), EqQ(m,
                      C2)),
                  IntegersQ(Times(C2, m), Times(C2, p))))),
      IIntegrate(4297,
          Integrate(
              Times(
                  Power(Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      e_DEFAULT), m_),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(Simp(Times(Sqr(e), Power(Times(e, Cos(Plus(a, Times(b, x)))), Subtract(m, C2)),
                  Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C1)),
                  Power(Times(C2, b, g, Plus(m, Times(C2, p))), CN1)), x), Dist(
                      Times(Sqr(e), Subtract(Plus(m, p), C1), Power(Plus(m, Times(C2, p)), CN1)),
                      Integrate(
                          Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), Subtract(m, C2)),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))), p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, g, p), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                  EqQ(Times(d, Power(b,
                      CN1)), C2),
                  Not(IntegerQ(p)), GtQ(m, C1), NeQ(Plus(m,
                      Times(C2, p)), C0),
                  IntegersQ(Times(C2, m), Times(C2, p))))),
      IIntegrate(4298,
          Integrate(
              Times(
                  Power(Times(e_DEFAULT,
                      $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), m_),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(Sqr(e), Power(Times(e, Sin(Plus(a, Times(b, x)))), Subtract(m, C2)),
                              Power(Times(g, Sin(
                                  Plus(c, Times(d, x)))), Plus(p,
                                      C1)),
                              Power(Times(C2, b, g, Plus(m, Times(C2, p))), CN1)),
                          x)),
                  Dist(
                      Times(Sqr(e), Subtract(Plus(m,
                          p), C1), Power(Plus(m, Times(C2, p)),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Times(e, Sin(Plus(a, Times(b, x)))), Subtract(m,
                                  C2)),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))), p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, g, p), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                  EqQ(Times(d, Power(b, CN1)), C2), Not(IntegerQ(p)), GtQ(m, C1), NeQ(Plus(m,
                      Times(C2, p)), C0),
                  IntegersQ(Times(C2, m), Times(C2, p))))),
      IIntegrate(4299,
          Integrate(
              Times(
                  Power(Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      e_DEFAULT), m_),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), m),
                              Power(Times(g, Sin(
                                  Plus(c, Times(d, x)))), Plus(p,
                                      C1)),
                              Power(Times(C2, b, g, Plus(m, p, C1)), CN1)),
                          x)),
                  Dist(Times(Plus(m, Times(C2, p), C2), Power(Times(Sqr(e), Plus(m, p, C1)), CN1)),
                      Integrate(
                          Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), Plus(m, C2)),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))), p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, g, p), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                  EqQ(Times(d, Power(b, CN1)), C2), Not(IntegerQ(p)), LtQ(m, CN1),
                  NeQ(Plus(m, Times(C2,
                      p), C2), C0),
                  NeQ(Plus(m, p, C1), C0), IntegersQ(Times(C2, m), Times(C2, p))))),
      IIntegrate(4300,
          Integrate(
              Times(
                  Power(Times(e_DEFAULT,
                      $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), m_),
                  Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(
                          Power(Times(e, Sin(Plus(a,
                              Times(b, x)))), m),
                          Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C1)),
                          Power(Times(C2, b, g, Plus(m, p, C1)), CN1)),
                      x),
                  Dist(Times(Plus(m, Times(C2, p), C2), Power(Times(Sqr(e), Plus(m, p, C1)), CN1)),
                      Integrate(Times(Power(Times(e, Sin(Plus(a, Times(b, x)))), Plus(m, C2)),
                          Power(Times(g, Sin(Plus(c, Times(d, x)))), p)), x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, g, p), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                  EqQ(Times(d, Power(b, CN1)), C2), Not(IntegerQ(p)), LtQ(m, CN1),
                  NeQ(Plus(m, Times(C2, p), C2), C0), NeQ(Plus(m, p, C1), C0),
                  IntegersQ(Times(C2, m), Times(C2, p))))));
}
