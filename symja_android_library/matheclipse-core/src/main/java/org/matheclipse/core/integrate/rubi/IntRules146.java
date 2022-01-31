package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules146 {
  public static IAST RULES =
      List(
          IIntegrate(2921,
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
                  Dist(Times(g, Power(Times(g, Cos(Plus(e, Times(f, x)))), Subtract(p, C1)),
                      Power(Times(f,
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                              Times(C1D2, Subtract(p, C1))),
                          Power(Subtract(a, Times(b, Sin(Plus(e, Times(f, x))))),
                              Times(C1D2, Subtract(p, C1)))),
                          CN1)),
                      Subst(Integrate(
                          Times(Power(Plus(a, Times(b, x)), Plus(m, Times(C1D2, Subtract(p, C1)))),
                              Power(Subtract(a, Times(b, x)), Times(C1D2,
                                  Subtract(p, C1))),
                              Power(Plus(c, Times(d, x)), n)),
                          x), x, Sin(Plus(e, Times(f, x)))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      Not(IntegerQ(m))))),
          IIntegrate(2922,
              Integrate(
                  Times(
                      Sqr($($s("§cos"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                          Power(Plus(c,
                              Times(d, Sin(Plus(e, Times(f, x))))), n),
                          Subtract(C1, Sqr(Sin(Plus(e, Times(f, x)))))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      Or(IGtQ(m, C0), IntegersQ(Times(C2, m), Times(C2, n)))))),
          IIntegrate(2923,
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
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrig(
                          Times(Power(Plus(a, Times(b, $($s("§sin"), Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, $($s("§sin"), Plus(e, Times(f, x))))), n),
                              Power(
                                  Subtract(C1, Sqr($($s("§sin"), Plus(e, Times(f, x))))), Times(
                                      C1D2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      IGtQ(Times(C1D2, p), C0),
                      Or(IGtQ(m, C0), IntegersQ(Times(C2, m), Times(C2, n)))))),
          IIntegrate(2924,
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
                  Integrate(
                      ExpandTrig(
                          Times(Power(Times(g, $($s("§cos"), Plus(e, Times(f, x)))), p),
                              Power(Plus(a, Times(b, $($s("§sin"), Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, $($s("§sin"), Plus(e, Times(f, x))))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, p), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), IntegersQ(Times(C2, m),
                          Times(C2, n))))),
          IIntegrate(2925,
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
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                              m),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2926,
              Integrate(
                  Times(
                      Power(Times(g_DEFAULT, $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          p_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(g, Times(C2, IntPart(p))),
                          Power(Times(g,
                              Cos(Plus(e, Times(f, x)))), FracPart(p)),
                          Power(Times(g, Sec(Plus(e, Times(f, x)))), FracPart(p))),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n), Power(
                                  Power(Times(g, Cos(Plus(e, Times(f, x)))), p), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x), Not(IntegerQ(p))))),
          IIntegrate(2927,
              Integrate(
                  Times(
                      Power(Plus(
                          a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                          m_DEFAULT),
                      Power(Plus(
                          c_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT)),
                          n_DEFAULT),
                      Power(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(g, Times(C2, IntPart(p))),
                          Power(Times(g, Sin(Plus(e, Times(f, x)))), FracPart(p)),
                          Power(Times(g, Csc(Plus(e, Times(f, x)))), FracPart(p))),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Cos(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Cos(Plus(e, Times(f, x))))), n), Power(
                                  Power(Times(g, Sin(Plus(e, Times(f, x)))), p), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x), Not(IntegerQ(p))))),
          IIntegrate(2928,
              Integrate(
                  Times(
                      Sqrt(Times(g_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Sqrt(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(g, Power(d, CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))), Power(
                                      Times(g, Sin(Plus(e, Times(f, x)))), CN1D2)),
                              x),
                          x),
                      Dist(Times(c, g, Power(d, CN1)),
                          Integrate(
                              Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                  Power(
                                      Times(Sqrt(Times(g, Sin(Plus(e, Times(f, x))))),
                                          Plus(c, Times(d, Sin(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      Or(EqQ(Subtract(Sqr(a), Sqr(b)), C0), EqQ(Subtract(Sqr(c), Sqr(d)), C0))))),
          IIntegrate(2929,
              Integrate(
                  Times(
                      Sqrt(Times(g_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Sqrt(
                          Plus(a_,
                              Times(
                                  b_DEFAULT, $($s("§sin"),
                                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(Subtract(
                  Dist(
                      Times(b, Power(d,
                          CN1)),
                      Integrate(
                          Times(
                              Sqrt(Times(g, Sin(
                                  Plus(e, Times(f, x))))),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), CN1D2)),
                          x),
                      x),
                  Dist(
                      Times(Subtract(Times(b, c), Times(a, d)), Power(d,
                          CN1)),
                      Integrate(
                          Times(Sqrt(Times(g, Sin(Plus(e, Times(f, x))))),
                              Power(
                                  Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))), Plus(c,
                                      Times(d, Sin(Plus(e, Times(f, x)))))),
                                  CN1)),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                      C0), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2930,
              Integrate(
                  Times(
                      Power(
                          Times(g_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          CN1D2),
                      Sqrt(
                          Plus(a_,
                              Times(
                                  b_DEFAULT, $($s("§sin"),
                                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(Dist(Times(CN2, b, Power(f, CN1)), Subst(Integrate(Power(Plus(Times(b, c),
                  Times(a, d), Times(c, g, Sqr(x))), CN1), x), x, Times(b, Cos(
                      Plus(e, Times(f, x))),
                      Power(
                          Times(Sqrt(Times(g, Sin(Plus(e, Times(f, x))))), Sqrt(Plus(a, Times(
                              b, Sin(Plus(e, Times(f, x))))))),
                          CN1))),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2931,
              Integrate(
                  Times(Power($($s("§sin"),
                      Plus(e_DEFAULT, Times(f_DEFAULT, x_))), CN1D2), Sqrt(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Power(Plus(c_,
                          Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(Negate(
                  Simp(
                      Times(Sqrt(Plus(a, b)),
                          EllipticE(ArcSin(Times(Cos(Plus(e, Times(f, x))), Power(
                              Plus(C1, Sin(Plus(e, Times(f, x)))), CN1))), Times(CN1,
                                  Subtract(a, b), Power(Plus(a, b), CN1))),
                          Power(Times(c, f), CN1)),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(d, c), GtQ(Subtract(Sqr(b),
                      Sqr(a)), C0), GtQ(b,
                          C0)))),
          IIntegrate(2932,
              Integrate(
                  Times(Power(Times(
                      g_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), CN1D2),
                      Sqrt(Plus(a_, Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)))))),
                      Power(Plus(c_, Times(d_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))), CN1)),
                  x_Symbol),
              Condition(
                  Negate(Simp(
                      Times(
                          Sqrt(Plus(a,
                              Times(b, Sin(Plus(e, Times(f, x)))))),
                          Sqrt(
                              Times(
                                  d, Sin(Plus(e, Times(f,
                                      x))),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1))),
                          EllipticE(
                              ArcSin(
                                  Times(c, Cos(Plus(e, Times(f, x))), Power(
                                      Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1))),
                              Times(
                                  Subtract(Times(b, c), Times(a,
                                      d)),
                                  Power(Plus(Times(b, c), Times(a, d)), CN1))),
                          Power(Times(d, f, Sqrt(Times(g, Sin(Plus(e, Times(f, x))))),
                              Sqrt(Times(Sqr(c), Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                  Power(Times(Plus(Times(a, c), Times(b, d)),
                                      Plus(c, Times(d, Sin(Plus(e, Times(f, x)))))), CN1)))),
                              CN1)),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      EqQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2933,
              Integrate(
                  Times(
                      Power(
                          Times(g_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          CN1D2),
                      Sqrt(
                          Plus(a_,
                              Times(
                                  b_DEFAULT, $($s("§sin"),
                                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(a, Power(c,
                              CN1)),
                          Integrate(
                              Power(
                                  Times(
                                      Sqrt(Times(g, Sin(Plus(e,
                                          Times(f, x))))),
                                      Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))))),
                                  CN1),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(b, c), Times(a,
                              d)), Power(Times(c, g),
                                  CN1)),
                          Integrate(
                              Times(Sqrt(Times(g, Sin(Plus(e, Times(f, x))))),
                                  Power(Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                      Plus(c, Times(d, Sin(Plus(e, Times(f, x)))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2934,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), CN1),
                      Sqrt(
                          Plus(a_,
                              Times(
                                  b_DEFAULT, $($s("§sin"),
                                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(c, CN1),
                          Integrate(Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                              Power(Sin(Plus(e, Times(f, x))), CN1)), x),
                          x),
                      Dist(Times(d, Power(c, CN1)),
                          Integrate(Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2935,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), CN1),
                      Sqrt(
                          Plus(a_,
                              Times(
                                  b_DEFAULT, $($s("§sin"),
                                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(a, Power(c,
                              CN1)),
                          Integrate(
                              Power(
                                  Times(
                                      Sin(Plus(e, Times(f, x))), Sqrt(Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x))))))),
                                  CN1),
                              x),
                          x),
                      Dist(Times(Subtract(Times(b, c), Times(a, d)), Power(c, CN1)), Integrate(
                          Power(Times(Sqrt(Plus(a,
                              Times(b, Sin(Plus(e, Times(f, x)))))), Plus(c,
                                  Times(d, Sin(Plus(e, Times(f, x)))))),
                              CN1),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2936,
              Integrate(
                  Times(
                      Sqrt(Times(g_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(
                              Times(a, g, Power(Subtract(Times(b, c), Times(a, d)),
                                  CN1)),
                              Integrate(
                                  Power(Times(
                                      Sqrt(Times(g, Sin(Plus(e, Times(f, x))))), Sqrt(Plus(a, Times(
                                          b, Sin(Plus(e, Times(f, x))))))),
                                      CN1),
                                  x),
                              x)),
                      Dist(Times(c, g, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                  Power(
                                      Times(Sqrt(Times(g, Sin(Plus(e, Times(f, x))))),
                                          Plus(c, Times(d, Sin(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      Or(EqQ(Subtract(Sqr(a), Sqr(b)), C0), EqQ(Subtract(Sqr(c), Sqr(d)), C0))))),
          IIntegrate(2937,
              Integrate(
                  Times(
                      Sqrt(Times(g_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(C2, Sqrt(Negate(Sqr(Cot(Plus(e, Times(f, x)))))),
                          Sqrt(Times(g, Sin(Plus(e, Times(f, x))))),
                          Sqrt(
                              Times(Plus(b, Times(a, Csc(Plus(e, Times(f, x))))),
                                  Power(Plus(a, b), CN1))),
                          EllipticPi(
                              Times(C2, c, Power(Plus(c, d),
                                  CN1)),
                              ArcSin(Times(Sqrt(Subtract(C1, Csc(Plus(e, Times(f, x))))),
                                  C1DSqrt2)),
                              Times(C2, a, Power(Plus(a, b), CN1))),
                          Power(
                              Times(
                                  f, Plus(c, d), Cot(Plus(e, Times(f, x))), Sqrt(Plus(a,
                                      Times(b, Sin(Plus(e, Times(f, x))))))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2938,
              Integrate(
                  Times(
                      Power(
                          Times(g_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          CN1D2),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Power(
                                  Times(
                                      Sqrt(Times(g, Sin(Plus(e, Times(f, x))))), Sqrt(Plus(a, Times(
                                          b, Sin(Plus(e, Times(f, x))))))),
                                  CN1),
                              x),
                          x),
                      Dist(
                          Times(d, Power(Subtract(Times(b, c), Times(a, d)), CN1)), Integrate(
                              Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                  Power(Times(Sqrt(Times(g, Sin(Plus(e, Times(f, x))))),
                                      Plus(c, Times(d, Sin(Plus(e, Times(f, x)))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      Or(EqQ(Subtract(Sqr(a), Sqr(b)), C0), EqQ(Subtract(Sqr(c), Sqr(d)), C0))))),
          IIntegrate(2939,
              Integrate(
                  Times(
                      Power(
                          Times(g_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          CN1D2),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(c, CN1),
                          Integrate(
                              Power(
                                  Times(
                                      Sqrt(Times(g, Sin(Plus(e,
                                          Times(f, x))))),
                                      Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))))),
                                  CN1),
                              x),
                          x),
                      Dist(
                          Times(d, Power(Times(c, g),
                              CN1)),
                          Integrate(
                              Times(Sqrt(Times(g, Sin(Plus(e, Times(f, x))))),
                                  Power(
                                      Times(
                                          Sqrt(Plus(a, Times(b,
                                              Sin(Plus(e, Times(f, x)))))),
                                          Plus(c, Times(d, Sin(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c), Times(a,
                          d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(2940,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), CN1),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Power(Plus(c_, Times(d_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(Sqr(d), Power(Times(c, Subtract(Times(b, c), Times(a, d))),
                              CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b,
                                      Sin(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x),
                      Dist(Power(Times(c, Subtract(Times(b, c), Times(a, d))), CN1),
                          Integrate(
                              Times(
                                  Subtract(Subtract(Times(b, c), Times(a, d)),
                                      Times(b, d, Sin(Plus(e, Times(f, x))))),
                                  Power(
                                      Times(Sin(Plus(e, Times(f, x))),
                                          Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))));
}
