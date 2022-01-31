package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.CN1;
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
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
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
class IntRules141 {
  public static IAST RULES =
      List(
          IIntegrate(2821,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, Cos(Plus(e, Times(f, x))),
                                  Power(
                                      Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Power(Plus(c,
                                      Times(d, Sin(Plus(e, Times(f, x))))), n),
                                  Power(Times(f, Plus(m, n)), CN1)),
                              x)),
                      Dist(
                          Power(Times(d,
                              Plus(m, n)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m,
                                      C2)),
                                  Power(
                                      Plus(c, Times(d, Sin(Plus(e, Times(f,
                                          x))))),
                                      Subtract(n, C1)),
                                  Simp(
                                      Plus(Times(Sqr(a), c, d, Plus(m, n)),
                                          Times(b, d,
                                              Plus(Times(b, c, Subtract(m, C1)), Times(a, d, n))),
                                          Times(
                                              Subtract(
                                                  Times(a, d, Plus(Times(C2, b, c), Times(a, d)),
                                                      Plus(m, n)),
                                                  Times(b, d,
                                                      Subtract(Times(a, c),
                                                          Times(b, d, Subtract(Plus(m, n), C1))))),
                                              Sin(Plus(e, Times(f, x)))),
                                          Times(b, d,
                                              Plus(Times(b, c, n),
                                                  Times(a, d, Subtract(Plus(Times(C2, m), n), C1))),
                                              Sqr(Sin(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)), C0),
                      LtQ(C0, m, C2), LtQ(CN1, n, C2), NeQ(Plus(m, n), C0), Or(IntegerQ(m),
                          IntegersQ(Times(C2, m), Times(C2, n)))))),
          IIntegrate(2822,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(b, Power(d,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m,
                                          C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n, C1))),
                              x),
                          x),
                      Dist(Times(Subtract(Times(b, c), Times(a, d)), Power(d, CN1)),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m,
                                          C1)),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), IGtQ(m, C0)))),
          IIntegrate(2823,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(a,
                          Integrate(
                              Times(
                                  Power(Times(d,
                                      Sin(Plus(e, Times(f, x)))), n),
                                  Power(
                                      Subtract(Sqr(a),
                                          Times(Sqr(b), Sqr(Sin(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(d,
                              CN1)),
                          Integrate(
                              Times(Power(Times(d, Sin(Plus(e, Times(f, x)))), Plus(n, C1)),
                                  Power(
                                      Subtract(Sqr(a), Times(Sqr(b),
                                          Sqr(Sin(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, n), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2824,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrig(
                          Times(Power(Times(d, $($s("§sin"), Plus(e, Times(f, x)))), n),
                              Power(
                                  Times(
                                      Power(Subtract(a,
                                          Times(b, $($s("§sin"), Plus(e, Times(f, x))))), m),
                                      Power(
                                          Power(
                                              Subtract(Sqr(a),
                                                  Times(Sqr(b),
                                                      Sqr($($s("§sin"), Plus(e, Times(f, x)))))),
                                              m),
                                          CN1)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, n), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      ILtQ(m, CN1)))),
          IIntegrate(2825,
              Integrate(Times(
                  Power(
                      Plus(
                          a_DEFAULT,
                          Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_),
                  Power(
                      Plus(c_DEFAULT,
                          Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      n_)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(a,
                              Times(b, Sin(Plus(e, Times(f, x))))), m),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a,
                          d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2826,
              Integrate(
                  Times(
                      Power(
                          Times(c_DEFAULT,
                              Power(
                                  Times(
                                      d_DEFAULT,
                                      $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                  p_)),
                          n_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(
                              n)),
                          Power(Times(c,
                              Power(Times(d, Sin(Plus(e, Times(f, x)))), p)), FracPart(n)),
                          Power(
                              Power(Times(d, Sin(Plus(e, Times(f, x)))),
                                  Times(p, FracPart(n))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m), Power(
                                  Times(d, Sin(Plus(e, Times(f, x)))), Times(n, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(n))))),
          IIntegrate(2827,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT)),
                          m_DEFAULT),
                      Power(
                          Times(c_DEFAULT,
                              Power(
                                  Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                      d_DEFAULT),
                                  p_)),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(
                              n)),
                          Power(Times(c,
                              Power(Times(d, Cos(Plus(e, Times(f, x)))), p)), FracPart(n)),
                          Power(
                              Power(Times(d, Cos(Plus(e, Times(f, x)))),
                                  Times(p, FracPart(n))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b,
                                  Cos(Plus(e, Times(f, x))))), m),
                              Power(Times(d, Cos(Plus(e, Times(f, x)))), Times(n, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(n))))),
          IIntegrate(2828,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                          Power(Plus(d,
                              Times(c, Sin(Plus(e, Times(f, x))))), n),
                          Power(Power(Sin(Plus(e, Times(f, x))), n), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IntegerQ(n)))),
          IIntegrate(2829,
              Integrate(
                  Times(
                      Power(Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT), c_), n_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(b, Times(a,
                              Csc(Plus(e, Times(f, x))))), m),
                          Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))),
                              n),
                          Power(Power(Csc(Plus(e, Times(f, x))), m), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), Not(IntegerQ(n)), IntegerQ(m)))),
          IIntegrate(2830,
              Integrate(
                  Times(Power(
                      Plus(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                          a_),
                      m_DEFAULT),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(b, Times(a,
                              Sec(Plus(e, Times(f, x))))), m),
                          Power(Plus(c,
                              Times(d, Sec(Plus(e, Times(f, x))))), n),
                          Power(Power(Sec(Plus(e, Times(f, x))), m), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), Not(IntegerQ(n)), IntegerQ(m)))),
          IIntegrate(2831,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Sin(Plus(e, Times(f, x))), n),
                          Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), n), Power(Power(
                              Plus(d, Times(c, Sin(Plus(e, Times(f, x))))), n), CN1)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Plus(d,
                                  Times(c, Sin(Plus(e, Times(f, x))))), n),
                              Power(Power(Sin(Plus(e, Times(f, x))), n), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), Not(IntegerQ(n)), Not(IntegerQ(m))))),
          IIntegrate(2832,
              Integrate(
                  Times(
                      Power(
                          Plus(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sec"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Cos(Plus(e, Times(f, x))), n),
                          Power(Plus(c, Times(d, Sec(Plus(e, Times(f, x))))), n),
                          Power(Power(Plus(d, Times(c, Cos(Plus(e, Times(f, x))))), n), CN1)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Cos(Plus(e, Times(f, x))))), m),
                              Power(Plus(d, Times(c, Cos(Plus(e, Times(f, x))))),
                                  n),
                              Power(Power(Cos(Plus(e, Times(f, x))), n), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), Not(IntegerQ(n)), Not(IntegerQ(m))))),
          IIntegrate(2833,
              Integrate(
                  Times(
                      $($s("§cos"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(b,
                          f), CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(a,
                                      x), m),
                                  Power(Plus(c, Times(d, x, Power(b, CN1))), n)),
                              x),
                          x, Times(b, Sin(Plus(e, Times(f, x))))),
                      x),
                  FreeQ(List(a, b, c, d, e, f, m, n), x))),
          IIntegrate(2834,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), p_),
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Plus(
                          a_, Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(a,
                          Integrate(
                              Times(Power(Cos(Plus(e, Times(f, x))), p),
                                  Power(Times(d, Sin(Plus(e, Times(f, x)))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(d,
                              CN1)),
                          Integrate(
                              Times(Power(Cos(Plus(e, Times(f, x))), p), Power(
                                  Times(d, Sin(Plus(e, Times(f, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, n, p), x), IntegerQ(Times(C1D2, Subtract(p, C1))),
                      IntegerQ(n),
                      Or(And(LtQ(p, C0), NeQ(Subtract(Sqr(a), Sqr(b)), C0)),
                          LtQ(C0, n, Subtract(p, C1)), LtQ(Plus(p, C1), Negate(n),
                              Plus(Times(C2, p), C1)))))),
          IIntegrate(2835,
              Integrate(
                  Times(Power($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), p_),
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(Dist(Power(a, CN1),
                      Integrate(Times(Power(Cos(Plus(e, Times(f, x))), Subtract(p, C2)),
                          Power(Times(d, Sin(Plus(e, Times(f, x)))), n)), x),
                      x),
                      Dist(Power(Times(b, d), CN1),
                          Integrate(
                              Times(Power(Cos(Plus(e, Times(f, x))), Subtract(p, C2)),
                                  Power(Times(d, Sin(Plus(e, Times(f, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, n, p), x), IntegerQ(Times(C1D2, Subtract(p, C1))),
                      EqQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      IntegerQ(n),
                      Or(LtQ(C0, n, Times(C1D2, Plus(p, C1))),
                          And(LeQ(p, Negate(n)), LtQ(Negate(n), Subtract(Times(C2, p), C3))), And(
                              GtQ(n, C0), LeQ(n, Negate(p))))))),
          IIntegrate(2836,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(Power(b, p),
                          f), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, x), Plus(m, Times(C1D2, Subtract(p, C1)))),
                                  Power(Subtract(a, x), Times(C1D2, Subtract(p, C1))), Power(
                                      Plus(c, Times(d, x, Power(b, CN1))), n)),
                              x),
                          x, Times(b, Sin(Plus(e, Times(f, x))))),
                      x),
                  And(FreeQ(List(a, b, e, f, c, d, m, n),
                      x), IntegerQ(Times(C1D2, Subtract(p, C1))),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2837,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(Power(b, p),
                          f), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, x), m),
                                  Power(Plus(c, Times(d, x,
                                      Power(b, CN1))), n),
                                  Power(Subtract(Sqr(b), Sqr(x)), Times(C1D2, Subtract(p, C1)))),
                              x),
                          x, Times(b, Sin(Plus(e, Times(f, x))))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n),
                      x), IntegerQ(Times(C1D2, Subtract(p, C1))),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2838,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Plus(a_, Times(b_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(a,
                          Integrate(Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                              Power(Times(d, Sin(Plus(e, Times(f, x)))), n)), x),
                          x),
                      Dist(
                          Times(b, Power(d,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                                  Power(Times(d, Sin(Plus(e, Times(f, x)))), Plus(n, C1))),
                              x),
                          x)),
                  FreeQ(List(a, b, d, e, f, g, n, p), x))),
          IIntegrate(2839,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(Sqr(g), Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Subtract(p, C2)),
                                  Power(Times(d, Sin(Plus(e, Times(f, x)))), n)),
                              x),
                          x),
                      Dist(Times(Sqr(g), Power(Times(b, d), CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Subtract(p,
                                  C2)), Power(Times(d, Sin(Plus(e, Times(f, x)))),
                                      Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, g, n, p), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2840,
              Integrate(
                  Times(
                      Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(a, m), Power(c, m), Power(Power(g, Times(C2, m)), CN1)),
                      Integrate(
                          Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(Times(C2, m), p)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Subtract(n, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      IntegerQ(m), Not(And(IntegerQ(n), LtQ(Sqr(n), Sqr(m))))))));
}
