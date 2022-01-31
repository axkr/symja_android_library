package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
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
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
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
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumSimplerQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules142 {
  public static IAST RULES =
      List(
          IIntegrate(2841,
              Integrate(
                  Times(Power($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(Power(a, Times(C1D2, p)),
                          Power(c, Times(C1D2, p))), CN1),
                      Integrate(
                          Times(
                              Power(
                                  Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                      Times(C1D2, p))),
                              Power(
                                  Plus(c, Times(d,
                                      Sin(Plus(e, Times(f, x))))),
                                  Plus(n, Times(C1D2, p)))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      IntegerQ(Times(C1D2, p))))),
          IIntegrate(2842,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(g, Cos(Plus(e, Times(f, x))),
                          Power(Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                              Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))), CN1)),
                      Integrate(Power(Times(g, Cos(Plus(e, Times(f, x)))), Subtract(p, C1)), x), x),
                  And(FreeQ(List(a, b, c, d, e, f, g, p), x), EqQ(Plus(Times(b, c),
                      Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0)))),
          IIntegrate(2843,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(Plus(a_, Times(b_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))), m_),
                      Power(
                          Plus(c_, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, IntPart(m)), Power(c, IntPart(m)),
                          Power(Plus(a, Times(b, Sin(
                              Plus(e, Times(f, x))))), FracPart(m)),
                          Power(Plus(c, Times(d,
                              Sin(Plus(e, Times(f, x))))), FracPart(
                                  m)),
                          Power(
                              Times(Power(g, Times(C2, IntPart(m))),
                                  Power(Times(g, Cos(Plus(e, Times(f, x)))),
                                      Times(C2, FracPart(m)))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(Times(C2, m),
                                  p)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a,
                          d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      EqQ(Subtract(Plus(Times(C2, m), p), C1), C0),
                      EqQ(Subtract(Subtract(m, n), C1), C0)))),
          IIntegrate(2844,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(b, Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C1)),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m, C1)),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))),
                              n),
                          Power(Times(f, g, Subtract(Subtract(m, n), C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      EqQ(Subtract(Plus(Times(C2, m), p), C1), C0),
                      NeQ(Subtract(Subtract(m, n), C1), C0)))),
          IIntegrate(2845,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              CN2, b, Power(Times(g, Cos(
                                  Plus(e, Times(f, x)))), Plus(p,
                                      C1)),
                              Power(Plus(a, Times(b,
                                  Sin(Plus(e, Times(f, x))))), Subtract(m, C1)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n),
                              Power(Times(f, g, Plus(Times(C2, n), p, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, Subtract(Plus(Times(C2, m), p), C1),
                              Power(Times(d, Plus(Times(C2, n), p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      IGtQ(Simplify(Subtract(Plus(m, Times(C1D2, p)), C1D2)), C0), LtQ(n, CN1),
                      NeQ(Plus(Times(C2, n), p,
                          C1), C0),
                      Not(And(ILtQ(Simplify(Plus(m, n, p)), C0), GtQ(Simplify(
                          Plus(Times(C2, m), n, Times(C1D2, C3, p), C1)), C0)))))),
          IIntegrate(2846,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C1)),
                                  Power(
                                      Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Power(Plus(c,
                                      Times(d, Sin(Plus(e, Times(f, x))))), n),
                                  Power(Times(f, g, Plus(m, n, p)), CN1)),
                              x)),
                      Dist(Times(a, Subtract(Plus(Times(C2, m), p), C1), Power(Plus(m, n, p), CN1)),
                          Integrate(
                              Times(
                                  Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      IGtQ(Simplify(Subtract(Plus(m, Times(C1D2, p)),
                          C1D2)), C0),
                      Not(LtQ(n,
                          CN1)),
                      Not(And(
                          IGtQ(Simplify(Subtract(Plus(n, Times(C1D2, p)), C1D2)),
                              C0),
                          GtQ(Subtract(m, n), C0))),
                      Not(And(
                          ILtQ(Simplify(Plus(m, n, p)), C0), GtQ(Simplify(
                              Plus(Times(C2, m), n, Times(C1D2, C3, p), C1)), C0)))))),
          IIntegrate(2847,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, IntPart(m)), Power(c, IntPart(m)),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), FracPart(m)),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), FracPart(m)),
                          Power(Times(Power(g, Times(C2, IntPart(m))),
                              Power(Times(g, Cos(Plus(e, Times(f, x)))), Times(C2, FracPart(m)))),
                              CN1)),
                      Integrate(Power(Times(g, Cos(
                          Plus(e, Times(f, x)))), Plus(Times(C2, m),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      EqQ(Plus(Times(C2, m), p, C1), C0)))),
          IIntegrate(2848,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(b, Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C1)),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))),
                              n),
                          Power(Times(a, f, g, Subtract(m, n)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      EqQ(Plus(m, n, p, C1), C0), NeQ(m, n)))),
          IIntegrate(2849,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C1)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Plus(c,
                                  Times(d, Sin(Plus(e, Times(f, x))))), n),
                              Power(Times(a, f, g, Plus(Times(C2, m), p, C1)), CN1)),
                          x),
                      Dist(
                          Times(Plus(m, n, p, C1), Power(Times(a, Plus(Times(C2, m), p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      ILtQ(Simplify(Plus(m, n, p, C1)), C0), NeQ(Plus(Times(C2, m), p, C1),
                          C0),
                      Or(SumSimplerQ(m, C1), Not(SumSimplerQ(n, C1)))))),
          IIntegrate(2850,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              CN2, b, Power(Times(g, Cos(
                                  Plus(e, Times(f, x)))), Plus(p,
                                      C1)),
                              Power(Plus(a, Times(b,
                                  Sin(Plus(e, Times(f, x))))), Subtract(m, C1)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n),
                              Power(Times(f, g, Plus(Times(C2, n), p, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              b, Subtract(Plus(Times(C2, m), p),
                                  C1),
                              Power(Times(d, Plus(Times(C2, n), p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m,
                                      C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      GtQ(m, C0), LtQ(n, CN1), NeQ(Plus(Times(C2, n), p, C1), C0),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(2851,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  b, Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C1)),
                                  Power(
                                      Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))),
                                      n),
                                  Power(Times(f, g, Plus(m, n, p)), CN1)),
                              x)),
                      Dist(
                          Times(a, Subtract(Plus(Times(C2, m), p), C1), Power(Plus(m, n, p),
                              CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      GtQ(m, C0), NeQ(Plus(m, n, p), C0), Not(LtQ(C0, n, m)), IntegersQ(
                          Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(2852,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              b, Power(Times(g, Cos(Plus(e, Times(f,
                                  x)))), Plus(p, C1)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n), Power(
                                  Times(a, f, g, Plus(Times(C2, m), p, C1)), CN1)),
                          x),
                      Dist(
                          Times(Plus(m, n, p, C1), Power(Times(a, Plus(Times(C2, m), p, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      LtQ(m, CN1), NeQ(Plus(Times(C2, m), p, C1), C0), Not(LtQ(m, n, CN1)),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(2853,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, IntPart(m)), Power(c, IntPart(m)),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), FracPart(m)),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), FracPart(m)),
                          Power(Times(Power(g, Times(C2, IntPart(m))),
                              Power(Times(g, Cos(Plus(e, Times(f, x)))), Times(C2, FracPart(m)))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(Times(C2, m),
                                  p)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Subtract(n, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x),
                      EqQ(Plus(Times(b, c),
                          Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), Or(FractionQ(m), Not(FractionQ(n)))))),
          IIntegrate(2854,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(
                              d, Power(Times(g, Cos(
                                  Plus(e, Times(f, x)))), Plus(p,
                                      C1)),
                              Power(Plus(a,
                                  Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Times(f, g, Plus(m, p, C1)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, p), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      EqQ(Plus(Times(a, d, m), Times(b, c, Plus(m, p, C1))), C0)))),
          IIntegrate(2855,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_, Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Plus(Times(b, c), Times(a, d)),
                                  Power(Times(g, Cos(
                                      Plus(e, Times(f, x)))), Plus(p, C1)),
                                  Power(Plus(a,
                                      Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Power(Times(a, f, g, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(
                              b, Plus(Times(a, d, m), Times(b, c, Plus(m, p, C1))),
                              Power(Times(a, Sqr(g), Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C2)),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      GtQ(m, CN1), LtQ(p, CN1)))),
          IIntegrate(2856,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(d, Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C1)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Times(f, g, Plus(m, p, C1)), CN1)), x)),
                      Dist(
                          Times(
                              Plus(Times(a, d, m), Times(b, c, Plus(m, p, C1))), Power(
                                  Times(b, Plus(m, p, C1)), CN1)),
                          Integrate(Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, p), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      IGtQ(Simplify(
                          Times(C1D2, Plus(Times(C2, m), p, C1))), C0),
                      NeQ(Plus(m, p, C1), C0)))),
          IIntegrate(2857,
              Integrate(
                  Times(
                      Sqr($($s("§cos"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(Simp(Times(C2, Subtract(Times(b, c), Times(a, d)), Cos(Plus(e, Times(f, x))),
                      Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)), Power(
                          Times(Sqr(b), f, Plus(Times(C2, m), C3)), CN1)),
                      x),
                      Dist(Power(Times(Power(b, C3), Plus(Times(C2, m), C3)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C2)),
                                  Subtract(Plus(Times(b, c), Times(C2, a, d, Plus(m, C1))),
                                      Times(b, d, Plus(Times(C2, m), C3),
                                          Sin(Plus(e, Times(f, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      LtQ(m, QQ(-3L, 2L))))),
          IIntegrate(2858,
              Integrate(
                  Times(
                      Sqr($($s("§cos"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(Subtract(
                  Simp(Times(d, Cos(Plus(e, Times(f, x))),
                      Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C2)),
                      Power(Times(Sqr(b), f, Plus(m, C3)), CN1)), x),
                  Dist(Power(Times(Sqr(b), Plus(m, C3)), CN1), Integrate(
                      Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                          Plus(Times(b, d, Plus(m, C2)), Times(CN1, a, c, Plus(m, C3)),
                              Times(Subtract(Times(b, c, Plus(m, C3)), Times(a, d, Plus(m, C4))),
                                  Sin(Plus(e, Times(f, x)))))),
                      x), x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Subtract(Sqr(a), Sqr(
                      b)), C0), GeQ(m, QQ(-3L,
                          2L)),
                      LtQ(m, C0)))),
          IIntegrate(2859,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Subtract(Times(b, c), Times(a, d)),
                              Power(Times(g, Cos(Plus(e,
                                  Times(f, x)))), Plus(p, C1)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Times(a, f, g, Plus(Times(C2, m), p, C1)), CN1)),
                          x),
                      Dist(
                          Times(Plus(Times(a, d, m), Times(b, c, Plus(m, p, C1))), Power(
                              Times(a, b, Plus(Times(C2, m), p, C1)), CN1)),
                          Integrate(Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, p), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      Or(LtQ(m, CN1), ILtQ(Simplify(Plus(m, p)), C0)),
                      NeQ(Plus(Times(C2, m), p, C1), C0)))),
          IIntegrate(2860,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(Plus(a_, Times(
                          b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(c_DEFAULT,
                          Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  d, Power(Times(g, Cos(
                                      Plus(e, Times(f, x)))), Plus(p, C1)),
                                  Power(Plus(a,
                                      Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Power(Times(f, g, Plus(m, p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(Times(a, d, m), Times(b, c, Plus(m, p, C1))),
                              Power(Times(b, Plus(m, p, C1)), CN1)),
                          Integrate(Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, p), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      NeQ(Plus(m, p, C1), C0)))));
}
