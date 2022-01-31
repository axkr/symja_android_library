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
class IntRules134 {
  public static IAST RULES =
      List(
          IIntegrate(2681,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C1)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m), Power(
                                  Times(a, f, g, Plus(Times(C2, m), p, C1)), CN1)),
                          x),
                      Dist(
                          Times(Plus(m, p, C1), Power(Times(a, Plus(Times(C2, m), p, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(g, Cos(Plus(e, Times(f, x)))), p), Power(Plus(a,
                                      Times(b, Sin(Plus(e, Times(f, x))))),
                                      Plus(m, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g, m, p), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      LtQ(m, CN1), NeQ(Plus(Times(C2, m), p, C1), C0),
                      IntegersQ(Times(C2, m), Times(C2, p))))),
          IIntegrate(2682,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(g, Power(Times(g, Cos(Plus(e, Times(f, x)))), Subtract(p, C1)),
                              Power(Times(b, f, Subtract(p, C1)), CN1)),
                          x),
                      Dist(Times(Sqr(g), Power(a, CN1)),
                          Integrate(Power(Times(g, Cos(Plus(e, Times(f, x)))), Subtract(p, C2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g), x), EqQ(Subtract(Sqr(a),
                      Sqr(b)), C0), GtQ(p,
                          C1),
                      IntegerQ(Times(C2, p))))),
          IIntegrate(2683,
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
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C1)),
                              Power(
                                  Times(
                                      a, f, g, Subtract(p, C1), Plus(a,
                                          Times(b, Sin(Plus(e, Times(f, x)))))),
                                  CN1)),
                          x),
                      Dist(
                          Times(p, Power(Times(a, Subtract(p, C1)),
                              CN1)),
                          Integrate(Power(Times(g, Cos(Plus(e, Times(f, x)))), p), x), x)),
                  And(FreeQ(List(a, b, e, f, g,
                      p), x), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      Not(GeQ(p, C1)), IntegerQ(Times(C2, p))))),
          IIntegrate(2684,
              Integrate(
                  Times(
                      Sqrt(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT)),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(g, Sqrt(Plus(C1, Cos(Plus(e, Times(f, x))))),
                              Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                              Power(Plus(a, Times(a, Cos(Plus(e, Times(f, x)))),
                                  Times(b, Sin(Plus(e, Times(f, x))))), CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(C1, Cos(Plus(e, Times(f, x))))), Power(
                                      Times(g, Cos(Plus(e, Times(f, x)))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(
                              g, Sqrt(Plus(C1, Cos(
                                  Plus(e, Times(f, x))))),
                              Sqrt(Plus(a,
                                  Times(b, Sin(Plus(e, Times(f, x)))))),
                              Power(
                                  Plus(b, Times(b, Cos(Plus(e, Times(f, x)))),
                                      Times(a, Sin(Plus(e, Times(f, x))))),
                                  CN1)),
                          Integrate(
                              Times(Sin(Plus(e, Times(f, x))),
                                  Power(Times(Sqrt(Times(g, Cos(Plus(e, Times(f, x))))),
                                      Sqrt(Plus(C1, Cos(Plus(e, Times(f, x)))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2685,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          QQ(3L, 2L)),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(g, Sqrt(Times(g, Cos(Plus(e, Times(f, x))))),
                              Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))), Power(Times(b, f),
                                  CN1)),
                          x),
                      Dist(Times(Sqr(g), Power(Times(C2, a), CN1)),
                          Integrate(
                              Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                                  Power(Times(g, Cos(Plus(e, Times(f, x)))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2686,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              CN2, b, Power(Times(g, Cos(
                                  Plus(e, Times(f, x)))), Plus(p,
                                      C1)),
                              Power(
                                  Times(
                                      f, g, Subtract(Times(C2,
                                          p), C1),
                                      Power(
                                          Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), QQ(3L,
                                              2L))),
                                  CN1)),
                          x),
                      Dist(
                          Times(C2, a, Subtract(p, C2), Power(Subtract(Times(C2, p), C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(g,
                                      Cos(Plus(e, Times(f, x)))), p),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), QQ(-3L, 2L))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0), GtQ(p, C2),
                      IntegerQ(Times(C2, p))))),
          IIntegrate(2687,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  b, Power(Times(g, Cos(
                                      Plus(e, Times(f, x)))), Plus(p,
                                          C1)),
                                  Power(
                                      Times(a, f, g, Plus(p, C1), Sqrt(Plus(a,
                                          Times(b, Sin(Plus(e, Times(f, x))))))),
                                      CN1)),
                              x)),
                      Dist(
                          Times(
                              a, Plus(Times(C2, p), C1),
                              Power(Times(C2, Sqr(g), Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C2)),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), QQ(-3L, 2L))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g), x), EqQ(Subtract(Sqr(a),
                      Sqr(b)), C0), LtQ(p,
                          CN1),
                      IntegerQ(Times(C2, p))))),
          IIntegrate(2688,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(Plus(
                          a_,
                          Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(a, m), Power(Times(g, Cos(
                              Plus(e, Times(f, x)))), Plus(p,
                                  C1)),
                          Power(
                              Times(f, g,
                                  Power(Plus(C1, Sin(Plus(e, Times(f, x)))), Times(C1D2,
                                      Plus(p, C1))),
                                  Power(Subtract(C1, Sin(Plus(e, Times(f, x)))),
                                      Times(C1D2, Plus(p, C1)))),
                              CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Plus(C1, Times(b, x, Power(a, CN1))), Plus(m,
                                          Times(C1D2, Subtract(p, C1)))),
                                  Power(Subtract(C1, Times(b, x, Power(a, CN1))),
                                      Times(C1D2, Subtract(p, C1)))),
                              x),
                          x, Sin(Plus(e, Times(f, x)))),
                      x),
                  And(FreeQ(List(a, b, e, f, g,
                      p), x), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      IntegerQ(m)))),
          IIntegrate(2689,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqr(a), Power(Times(g, Cos(
                              Plus(e, Times(f, x)))), Plus(p,
                                  C1)),
                          Power(
                              Times(f, g,
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                      Times(C1D2, Plus(p, C1))),
                                  Power(Subtract(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                      Times(C1D2, Plus(p, C1)))),
                              CN1)),
                      Subst(Integrate(Times(
                          Power(Plus(a, Times(b, x)), Plus(m, Times(C1D2, Subtract(p, C1)))), Power(
                              Subtract(a, Times(b, x)), Times(C1D2, Subtract(p, C1)))),
                          x), x, Sin(Plus(e, Times(f, x)))),
                      x),
                  And(FreeQ(List(a, b, e, f, g, m,
                      p), x), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      Not(IntegerQ(m))))),
          IIntegrate(2690,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C1)),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Sin(Plus(e, Times(f, x))), Power(Times(f, g, Plus(p, C1)), CN1)),
                              x)),
                      Dist(Power(Times(Sqr(g), Plus(p, C1)), CN1),
                          Integrate(Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C2)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m, C1)),
                              Plus(
                                  Times(a, Plus(p, C2)), Times(b, Plus(m, p, C2),
                                      Sin(Plus(e, Times(f, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      LtQ(C0, m, C1), LtQ(p, CN1), Or(IntegersQ(Times(C2, m), Times(C2,
                          p)), IntegerQ(
                              m))))),
          IIntegrate(2691,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p,
                                      C1)),
                                  Power(
                                      Plus(a, Times(b, Sin(
                                          Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Plus(b, Times(a, Sin(Plus(e, Times(f, x))))),
                                  Power(Times(f, g, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Power(Times(Sqr(g),
                              Plus(p, C1)), CN1),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C2)),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Subtract(m,
                                      C2)),
                                  Plus(Times(Sqr(b), Subtract(m, C1)), Times(Sqr(a), Plus(p,
                                      C2)), Times(a, b, Plus(m, p, C1),
                                          Sin(Plus(e, Times(f, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0), GtQ(m,
                      C1), LtQ(p, CN1),
                      Or(IntegersQ(Times(C2, m), Times(C2, p)), IntegerQ(m))))),
          IIntegrate(2692,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  b, Power(Times(g, Cos(
                                      Plus(e, Times(f, x)))), Plus(p,
                                          C1)),
                                  Power(
                                      Plus(a, Times(b,
                                          Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Power(Times(f, g, Plus(m, p)), CN1)),
                              x)),
                      Dist(Power(Plus(m, p), CN1),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                      Subtract(m, C2)),
                                  Plus(Times(Sqr(b), Subtract(m, C1)), Times(Sqr(a), Plus(m, p)),
                                      Times(a, b, Subtract(Plus(Times(C2, m), p), C1),
                                          Sin(Plus(e, Times(f, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g, p), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      GtQ(m, C1), NeQ(Plus(m,
                          p), C0),
                      Or(IntegersQ(Times(C2, m), Times(C2, p)), IntegerQ(m))))),
          IIntegrate(2693,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              g, Power(Times(g, Cos(
                                  Plus(e, Times(f, x)))), Subtract(p,
                                      C1)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Times(b, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(Sqr(g), Subtract(p, C1), Power(Times(b, Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Subtract(p, C2)),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                                  Sin(Plus(e, Times(f, x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), LtQ(m,
                          CN1),
                      GtQ(p, C1), IntegersQ(Times(C2, m), Times(C2, p))))),
          IIntegrate(2694,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_, Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  b, Power(Times(g, Cos(
                                      Plus(e, Times(f, x)))), Plus(p,
                                          C1)),
                                  Power(Plus(a, Times(b,
                                      Sin(Plus(e, Times(f, x))))), Plus(m,
                                          C1)),
                                  Power(Times(f, g, Subtract(Sqr(a), Sqr(b)), Plus(m, C1)), CN1)),
                              x)),
                      Dist(Power(Times(Subtract(Sqr(a), Sqr(b)), Plus(m, C1)), CN1),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), p),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Subtract(Times(a, Plus(m, C1)), Times(b, Plus(m, p, C2),
                                      Sin(Plus(e, Times(f, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g, p), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      LtQ(m, CN1), IntegersQ(Times(C2, m), Times(C2, p))))),
          IIntegrate(2695,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              g, Power(Times(g, Cos(
                                  Plus(e, Times(f, x)))), Subtract(p,
                                      C1)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Times(b, f, Plus(m, p)), CN1)),
                          x),
                      Dist(Times(Sqr(g), Subtract(p, C1), Power(Times(b, Plus(m, p)), CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Subtract(p, C2)),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m), Plus(b,
                                      Times(a, Sin(Plus(e, Times(f, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g, m), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), GtQ(p, C1), NeQ(Plus(m, p),
                          C0),
                      IntegersQ(Times(C2, m), Times(C2, p))))),
          IIntegrate(2696,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Times(g, Cos(
                                  Plus(e, Times(f, x)))), Plus(p,
                                      C1)),
                              Power(Plus(a, Times(b, Sin(
                                  Plus(e, Times(f, x))))), Plus(m, C1)),
                              Subtract(b, Times(a, Sin(Plus(e, Times(f,
                                  x))))),
                              Power(Times(f, g, Subtract(Sqr(a), Sqr(b)), Plus(p, C1)), CN1)),
                          x),
                      Dist(Power(Times(Sqr(g), Subtract(Sqr(a), Sqr(b)), Plus(p, C1)), CN1),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C2)),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                                  Plus(Times(Sqr(a), Plus(p, C2)),
                                      Times(CN1, Sqr(b), Plus(m, p, C2)), Times(a, b, Plus(m, p,
                                          C3), Sin(Plus(e, Times(f, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g, m), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), LtQ(p,
                          CN1),
                      IntegersQ(Times(C2, m), Times(C2, p))))),
          IIntegrate(2697, Integrate(Times(Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(
              f_DEFAULT, x_))), g_DEFAULT), CN1D2), Power(Plus(a_,
                  Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))), CN1D2)),
              x_Symbol),
              Condition(
                  Dist(
                      Times(
                          C2, CSqrt2, Sqrt(Times(g,
                              Cos(Plus(e, Times(f, x))))),
                          Sqrt(
                              Times(
                                  Plus(a, Times(b,
                                      Sin(Plus(e, Times(f, x))))),
                                  Power(
                                      Times(Subtract(a, b),
                                          Subtract(C1, Sin(Plus(e, Times(f, x))))),
                                      CN1))),
                          Power(
                              Times(
                                  f, g, Sqrt(Plus(a,
                                      Times(b, Sin(Plus(e, Times(f, x)))))),
                                  Sqrt(
                                      Times(
                                          Plus(C1, Cos(Plus(e, Times(f, x))),
                                              Sin(Plus(e, Times(f, x)))),
                                          Power(
                                              Subtract(Plus(C1, Cos(Plus(e, Times(f, x)))),
                                                  Sin(Plus(e, Times(f, x)))),
                                              CN1)))),
                              CN1)),
                      Subst(Integrate(Power(
                          Plus(C1, Times(Plus(a, b), Power(x, C4), Power(Subtract(a, b), CN1))),
                          CN1D2), x), x, Sqrt(
                              Times(Plus(C1, Cos(Plus(e, Times(f, x))), Sin(Plus(e, Times(f, x)))),
                                  Power(
                                      Subtract(Plus(C1, Cos(Plus(e, Times(f, x)))),
                                          Sin(Plus(e, Times(f, x)))),
                                      CN1)))),
                      x),
                  And(FreeQ(List(a, b, e, f, g), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(2698,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_),
                      Power(Plus(
                          a_,
                          Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(g, Power(Times(g, Cos(Plus(e, Times(f, x)))), Subtract(p, C1)),
                          Subtract(C1, Sin(Plus(e, Times(f,
                              x)))),
                          Power(Plus(a, Times(b,
                              Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                          Power(Times(CN1, Subtract(a, b), Subtract(C1, Sin(Plus(e, Times(f, x)))),
                              Power(Times(Plus(a, b), Plus(C1, Sin(Plus(e, Times(f, x))))), CN1)),
                              Times(C1D2, m)),
                          Hypergeometric2F1(Plus(m, C1), Plus(Times(C1D2, m), C1), Plus(m, C2),
                              Times(C2, Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                  Power(Times(Plus(a, b), Plus(C1, Sin(Plus(e, Times(f, x))))),
                                      CN1))),
                          Power(Times(f, Plus(a, b), Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, e, f, g, m, p), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), EqQ(Plus(m, p, C1),
                          C0)))),
          IIntegrate(2699,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Times(g, Cos(
                                  Plus(e, Times(f, x)))), Plus(p,
                                      C1)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Power(Times(f, g, Subtract(a, b), Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(a, Power(Times(Sqr(g), Subtract(a, b)),
                              CN1)),
                          Integrate(
                              Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C2)),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m), Power(
                                      Subtract(C1, Sin(Plus(e, Times(f, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g, m, p), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), EqQ(Plus(m, p, C2),
                          C0)))),
          IIntegrate(2700,
              Integrate(
                  Times(
                      Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          p_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Times(g, Cos(Plus(e, Times(f,
                                  x)))), Plus(p, C1)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Power(Times(f, g, Subtract(a, b), Plus(p, C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(
                                  b, Plus(m, p, C2), Power(
                                      Times(Sqr(g), Subtract(a, b), Plus(p, C1)), CN1)),
                              Integrate(
                                  Times(
                                      Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C2)),
                                      Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m)),
                                  x),
                              x)),
                      Dist(Times(a, Power(Times(Sqr(g), Subtract(a, b)), CN1)),
                          Integrate(Times(Power(Times(g, Cos(Plus(e, Times(f, x)))), Plus(p, C2)),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Subtract(C1, Sin(Plus(e, Times(f, x)))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, e, f, g, m, p), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      ILtQ(Plus(m, p, C2), C0)))));
}
