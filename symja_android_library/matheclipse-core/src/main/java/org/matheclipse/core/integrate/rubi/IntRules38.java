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
class IntRules38 {
  public static IAST RULES =
      List(
          IIntegrate(761,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, u_)), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, u_), Times(c_DEFAULT, Sqr(u_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coefficient(u, x,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), m), Power(
                                  Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                              x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, p), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(762,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, u_)), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(u_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Coefficient(u, x, C1), CN1),
                      Subst(
                          Integrate(Times(Power(Plus(d, Times(e, x)), m),
                              Power(Plus(a, Times(c, Sqr(x))), p)), x),
                          x, u),
                      x),
                  And(FreeQ(List(a, c, d, e, m, p), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(763,
              Integrate(
                  Times(Power(
                      Times(e_DEFAULT, x_), m_DEFAULT), Plus(f_, Times(g_DEFAULT, x_)),
                      Power(Plus(Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(g, Power(Times(e, x), m),
                          Power(Plus(Times(b, x), Times(c, Sqr(x))), Plus(p, C1)), Power(
                              Times(c, Plus(m, Times(C2, p), C2)), CN1)),
                      x),
                  And(FreeQ(List(b, c, e, f, g, m,
                      p), x), EqQ(
                          Subtract(Times(b, g, Plus(m, p, C1)),
                              Times(c, f, Plus(m, Times(C2, p), C2))),
                          C0),
                      NeQ(Plus(m, Times(C2, p), C2), C0)))),
          IIntegrate(764,
              Integrate(
                  Times(Power(x_, m_DEFAULT), Plus(f_, Times(g_DEFAULT, x_)),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          f, Integrate(Times(Power(x, m), Power(Plus(a, Times(c, Sqr(x))), p)),
                              x),
                          x),
                      Dist(g,
                          Integrate(
                              Times(Power(x, Plus(m, C1)), Power(Plus(a, Times(c, Sqr(x))), p)), x),
                          x)),
                  And(FreeQ(List(a, c, f, g, p), x), IntegerQ(m), Not(IntegerQ(Times(C2, p)))))),
          IIntegrate(765,
              Integrate(Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                  Plus(f_DEFAULT, Times(g_DEFAULT, x_)),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(ExpandIntegrand(Times(Power(Times(e, x), m), Plus(f, Times(g, x)),
                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)), x), x),
                  And(FreeQ(List(a, b, c, e, f, g, m), x), IntegerQ(p),
                      Or(GtQ(p, C0), And(EqQ(a, C0), IntegerQ(m)))))),
          IIntegrate(766,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Plus(f_DEFAULT, Times(g_DEFAULT,
                          x_)),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Times(e, x), m), Plus(f, Times(g, x)), Power(
                                  Plus(a, Times(c, Sqr(x))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, e, f, g, m), x), IGtQ(p, C0)))),
          IIntegrate(767,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Plus(f_, Times(g_DEFAULT, x_)),
                      Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(f, g, Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p,
                                  C1)),
                              Power(Times(b, Plus(p, C1), Subtract(Times(e, f), Times(d, g))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, p), x),
                      EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(m, Times(C2, p),
                          C3), C0),
                      EqQ(Subtract(Times(C2, c, f), Times(b, g)), C0)))),
          IIntegrate(768,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Plus(f_DEFAULT, Times(g_DEFAULT,
                          x_)),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(g, Power(Plus(d, Times(e, x)), m),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))),
                                  Plus(p, C1)),
                              Power(Times(C2, c, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(e, g, m, Power(Times(C2, c, Plus(p, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g),
                      x), EqQ(Subtract(Times(C2, c, f), Times(b, g)), C0), LtQ(p, CN1),
                      GtQ(m, C0)))),
          IIntegrate(769,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Plus(f_DEFAULT, Times(g_DEFAULT, x_)),
                      Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(Plus(Simp(Times(
                  CN2, c, Subtract(Times(e, f), Times(d, g)), Power(Plus(d, Times(e, x)),
                      Plus(m, C1)),
                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                  Power(Times(Plus(p, C1), Sqr(Subtract(Times(C2, c, d), Times(b, e)))), CN1)), x),
                  Dist(
                      Times(
                          Subtract(Times(C2, c, f), Times(b, g)),
                          Power(Subtract(Times(C2, c, d), Times(b, e)), CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, p), x),
                      EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(m, Times(C2,
                          p), C3), C0),
                      NeQ(Subtract(Times(C2, c, f),
                          Times(b, g)), C0),
                      NeQ(Subtract(Times(C2, c, d), Times(b, e)), C0)))),
          IIntegrate(770,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Plus(f_DEFAULT, Times(g_DEFAULT,
                          x_)),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(a, Times(b, x),
                              Times(c, Sqr(x))), FracPart(p)),
                          Power(
                              Times(
                                  Power(c, IntPart(p)), Power(Plus(Times(C1D2, b), Times(c, x)),
                                      Times(C2, FracPart(p)))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(d, Times(e, x)), m), Plus(f, Times(g, x)), Power(
                                  Plus(Times(C1D2, b), Times(c, x)), Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g,
                      m), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0)))),
          IIntegrate(771,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Plus(f_DEFAULT, Times(g_DEFAULT, x_)),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times(Power(Plus(d, Times(e, x)), m), Plus(f, Times(g, x)),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IntegerQ(p),
                      Or(GtQ(p, C0), And(EqQ(a, C0), IntegerQ(m)))))),
          IIntegrate(772,
              Integrate(Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                  Plus(f_DEFAULT, Times(g_DEFAULT, x_)), Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(d, Times(e, x)), m), Plus(f, Times(g, x)), Power(
                                  Plus(a, Times(c, Sqr(x))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, g, m), x), IGtQ(p, C0)))),
          IIntegrate(773,
              Integrate(
                  Times(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), Plus(f_, Times(g_DEFAULT, x_)),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(Simp(Times(e, g, x, Power(c, CN1)), x),
                      Dist(Power(c, CN1),
                          Integrate(Times(
                              Plus(Times(c, d, f), Times(CN1, a, e, g),
                                  Times(Subtract(Plus(Times(c, e, f), Times(c, d, g)),
                                      Times(b, e, g)), x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          IIntegrate(774,
              Integrate(
                  Times(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), Plus(f_, Times(g_DEFAULT, x_)),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(e, g, x,
                          Power(c, CN1)), x),
                      Dist(Power(c, CN1),
                          Integrate(
                              Times(
                                  Plus(Times(c, d, f), Times(CN1, a, e, g),
                                      Times(c, Plus(Times(e, f), Times(d, g)), x)),
                                  Power(Plus(a, Times(c, Sqr(x))), CN1)),
                              x),
                          x)),
                  FreeQ(List(a, c, d, e, f, g), x))),
          IIntegrate(775,
              Integrate(
                  Times(
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_)), Plus(f_DEFAULT, Times(g_DEFAULT, x_)),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(
                              Subtract(
                                  Subtract(Times(b, e, g, Plus(p, C2)),
                                      Times(c, Plus(Times(e, f), Times(d, g)),
                                          Plus(Times(C2, p), C3))),
                                  Times(C2, c, e, g, Plus(p, C1), x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(C2, Sqr(c), Plus(p, C1), Plus(Times(C2, p), C3)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, p), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(Sqr(b), e, g, Plus(p, C2)), Times(CN1, C2, a, c, e, g),
                          Times(c,
                              Subtract(Times(C2, c, d, f),
                                  Times(b, Plus(Times(e, f), Times(d, g)))),
                              Plus(Times(C2, p), C3))),
                          C0),
                      NeQ(p, CN1)))),
          IIntegrate(776,
              Integrate(
                  Times(Plus(d_DEFAULT, Times(e_DEFAULT, x_)),
                      Plus(f_DEFAULT, Times(g_DEFAULT,
                          x_)),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Plus(Times(
                              Plus(Times(e, f), Times(d, g)), Plus(Times(C2, p), C3)),
                              Times(C2, e, g, Plus(p, C1), x)),
                          Power(Plus(a, Times(c,
                              Sqr(x))), Plus(p,
                                  C1)),
                          Power(Times(C2, c, Plus(p, C1), Plus(Times(C2, p), C3)), CN1)),
                      x),
                  And(FreeQ(List(a, c, d, e, f, g, p), x),
                      EqQ(Subtract(Times(a, e, g), Times(c, d, f, Plus(Times(C2, p), C3))),
                          C0),
                      NeQ(p, CN1)))),
          IIntegrate(777,
              Integrate(
                  Times(Plus(d_DEFAULT, Times(e_DEFAULT, x_)),
                      Plus(f_DEFAULT, Times(g_DEFAULT,
                          x_)),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(Subtract(
                  Negate(
                      Simp(
                          Times(
                              Subtract(Subtract(Times(C2, a, c, Plus(Times(e, f), Times(d, g))),
                                  Times(b, Plus(Times(c, d, f), Times(a, e, g)))),
                                  Times(
                                      Plus(Times(Sqr(b), e, g),
                                          Times(CN1, b, c, Plus(Times(e, f), Times(d, g))),
                                          Times(C2, c, Subtract(Times(c, d, f), Times(a, e, g)))),
                                      x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(c, Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          x)),
                  Dist(
                      Times(Plus(Times(Sqr(b), e, g, Plus(p, C2)), Times(CN1, C2, a, c, e, g),
                          Times(c,
                              Subtract(Times(C2, c, d, f),
                                  Times(b, Plus(Times(e, f), Times(d, g)))),
                              Plus(Times(C2, p), C3))),
                          Power(Times(c, Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                      Integrate(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)), x), x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), LtQ(p, CN1)))),
          IIntegrate(778,
              Integrate(
                  Times(Plus(d_DEFAULT, Times(e_DEFAULT, x_)),
                      Plus(f_DEFAULT, Times(g_DEFAULT,
                          x_)),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Subtract(Times(a, Plus(Times(e, f), Times(d, g))),
                                  Times(Subtract(Times(c, d, f), Times(a, e, g)), x)),
                              Power(Plus(a, Times(c, Sqr(x))),
                                  Plus(p, C1)),
                              Power(Times(C2, a, c, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(a, e, g), Times(c, d, f, Plus(Times(C2,
                                  p), C3))),
                              Power(Times(C2, a, c, Plus(p, C1)), CN1)),
                          Integrate(Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)), x), x)),
                  And(FreeQ(List(a, c, d, e, f, g), x), LtQ(p, CN1)))),
          IIntegrate(779,
              Integrate(
                  Times(Plus(d_DEFAULT, Times(e_DEFAULT, x_)),
                      Plus(f_DEFAULT, Times(g_DEFAULT,
                          x_)),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Subtract(
                                      Subtract(Times(b, e, g, Plus(p, C2)),
                                          Times(c, Plus(Times(e, f), Times(d, g)),
                                              Plus(Times(C2, p), C3))),
                                      Times(C2, c, e, g, Plus(p, C1), x)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                  Power(Times(C2, Sqr(c), Plus(p, C1), Plus(Times(C2, p), C3)),
                                      CN1)),
                              x)),
                      Dist(
                          Times(Plus(Times(Sqr(b), e, g, Plus(p, C2)), Times(CN1, C2, a, c, e, g),
                              Times(c,
                                  Subtract(Times(C2, c, d, f),
                                      Times(b, Plus(Times(e, f), Times(d, g)))),
                                  Plus(Times(C2, p), C3))),
                              Power(Times(C2, Sqr(c), Plus(Times(C2, p), C3)), CN1)),
                          Integrate(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p), x), x)),
                  And(FreeQ(List(a, b, c, d, e, f, g,
                      p), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      Not(LeQ(p, CN1))))),
          IIntegrate(780,
              Integrate(
                  Times(
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_)), Plus(f_DEFAULT, Times(g_DEFAULT, x_)),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(
                          Plus(Times(Plus(Times(e, f), Times(d, g)), Plus(Times(C2, p), C3)),
                              Times(C2, e, g, Plus(p, C1), x)),
                          Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                          Power(Times(C2, c, Plus(p, C1), Plus(Times(C2, p), C3)), CN1)), x),
                      Dist(
                          Times(Subtract(Times(a, e, g), Times(c, d, f, Plus(Times(C2, p), C3))),
                              Power(Times(c, Plus(Times(C2, p), C3)), CN1)),
                          Integrate(Power(Plus(a, Times(c, Sqr(x))), p), x), x)),
                  And(FreeQ(List(a, c, d, e, f, g, p), x), Not(LeQ(p, CN1))))));
}
