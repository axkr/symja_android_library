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
class IntRules33 {
  public static IAST RULES =
      List(
          IIntegrate(661,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT, x_)), CN1D2), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(Times(C2, e),
                      Subst(
                          Integrate(Power(Plus(Times(C2, c, d), Times(Sqr(e), Sqr(x))),
                              CN1), x),
                          x,
                          Times(Sqrt(Plus(a, Times(c, Sqr(x)))),
                              Power(Plus(d, Times(e, x)), CN1D2))),
                      x),
                  And(FreeQ(List(a, c, d, e), x),
                      EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0)))),
          IIntegrate(662,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))),
                              p),
                          Power(Times(e, Plus(m, p, C1)), CN1)), x),
                      Dist(
                          Times(c, p, Power(Times(Sqr(e), Plus(m, p, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C2)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      GtQ(p, C0), Or(LtQ(m, CN2), EqQ(Plus(m, Times(C2, p), C1),
                          C0)),
                      NeQ(Plus(m, p, C1), C0), IntegerQ(Times(C2, p))))),
          IIntegrate(663,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          Power(Plus(a, Times(c, Sqr(x))), p), Power(Times(e, Plus(m, p, C1)),
                              CN1)),
                          x),
                      Dist(Times(c, p, Power(Times(Sqr(e), Plus(m, p, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C2)),
                                  Power(Plus(a, Times(c, Sqr(x))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e), x), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      GtQ(p, C0), Or(LtQ(m, CN2), EqQ(Plus(m, Times(C2, p),
                          C1), C0)),
                      NeQ(Plus(m, p, C1), C0), IntegerQ(Times(C2, p))))),
          IIntegrate(664,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))),
                                  p),
                              Power(Times(e, Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              p, Subtract(Times(C2, c, d), Times(b,
                                  e)),
                              Power(Times(Sqr(e), Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      GtQ(p, C0), Or(LeQ(CN2, m, C0), EqQ(Plus(m, p, C1),
                          C0)),
                      NeQ(Plus(m, Times(C2, p), C1), C0), IntegerQ(Times(C2, p))))),
          IIntegrate(665,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(c, Sqr(x))),
                                  p),
                              Power(Times(e, Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, c, d, p, Power(Times(Sqr(e), Plus(m, Times(C2, p), C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), Plus(m, C1)), Power(
                                      Plus(a, Times(c, Sqr(x))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e), x), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      GtQ(p, C0), Or(LeQ(CN2, m, C0), EqQ(Plus(m, p, C1),
                          C0)),
                      NeQ(Plus(m, Times(C2, p), C1), C0), IntegerQ(Times(C2, p))))),
          IIntegrate(666,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Subtract(Times(C2, c, d), Times(b, e)),
                              Power(Plus(d, Times(e, x)), m),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(e, Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          x),
                      Dist(
                          Times(Subtract(Times(C2, c, d), Times(b, e)), Plus(m, Times(C2, p), C2),
                              Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0), LtQ(p,
                          CN1),
                      LtQ(C0, m, C1), IntegerQ(Times(C2, p))))),
          IIntegrate(667,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(d, Power(Plus(d, Times(e, x)), m),
                              Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(C2, a, e, Plus(p, C1)), CN1)), x)),
                      Dist(
                          Times(d, Plus(m, Times(C2, p), C2),
                              Power(Times(C2, a, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e), x), EqQ(Plus(Times(c, Sqr(d)),
                      Times(a, Sqr(e))), C0), LtQ(p,
                          CN1),
                      LtQ(C0, m, C1), IntegerQ(Times(C2, p))))),
          IIntegrate(668,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))),
                                  Plus(p, C1)),
                              Power(Times(c, Plus(p, C1)), CN1)),
                          x),
                      Dist(Times(Sqr(e), Plus(m, p), Power(Times(c, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                          Times(a, Sqr(e))), C0),
                      LtQ(p, CN1), GtQ(m, C1), IntegerQ(Times(C2, p))))),
          IIntegrate(669,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Power(Plus(a, Times(c, Sqr(x))),
                                  Plus(p, C1)),
                              Power(Times(c, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(Sqr(e), Plus(m, p), Power(Times(c, Plus(p, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                                  Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e), x), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      LtQ(p, CN1), GtQ(m, C1), IntegerQ(Times(C2, p))))),
          IIntegrate(670,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(c, Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(Plus(m, p), Subtract(Times(C2, c, d), Times(b, e)),
                              Power(Times(c, Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a,
                          Sqr(e))), C0),
                      GtQ(m, C1), NeQ(Plus(m, Times(C2, p), C1), C0), IntegerQ(Times(C2, p))))),
          IIntegrate(671,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(c, Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, c, d, Plus(m, p),
                              Power(Times(c, Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(c, Sqr(x))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, p), x),
                      EqQ(Plus(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      GtQ(m, C1), NeQ(Plus(m, Times(C2, p), C1), C0), IntegerQ(Times(C2, p))))),
          IIntegrate(672,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(e, Power(Plus(d, Times(e, x)), m),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(Plus(m, p, C1), Subtract(Times(C2, c, d), Times(b, e))),
                                  CN1)),
                              x)),
                      Dist(
                          Times(c, Plus(m, Times(C2, p), C2),
                              Power(Times(Plus(m, p, C1), Subtract(Times(C2, c, d), Times(b, e))),
                                  CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      LtQ(m, C0), NeQ(Plus(m, p, C1), C0), IntegerQ(Times(C2, p))))),
          IIntegrate(673,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(e, Power(Plus(d, Times(e, x)), m),
                                  Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                                  Power(Times(C2, c, d, Plus(m, p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(m, Times(C2, p), C2), Power(Times(C2, d, Plus(m, p, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), Plus(m, C1)), Power(
                                      Plus(a, Times(c, Sqr(x))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, p), x),
                      EqQ(Plus(Times(c, Sqr(d)), Times(a,
                          Sqr(e))), C0),
                      LtQ(m, C0), NeQ(Plus(m, p, C1), C0), IntegerQ(Times(C2, p))))),
          IIntegrate(674,
              Integrate(Times(Power(Times(e_DEFAULT, x_), m_), Power(
                  Plus(Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)), x_Symbol),
              Condition(Dist(
                  Times(Power(Times(e, x), m), Power(Plus(Times(b, x), Times(c, Sqr(x))), p),
                      Power(Times(Power(x, Plus(m, p)), Power(Plus(b, Times(c, x)), p)), CN1)),
                  Integrate(Times(Power(x, Plus(m, p)), Power(Plus(b, Times(c, x)), p)), x), x),
                  And(FreeQ(List(b, c, e, m), x), Not(IntegerQ(p))))),
          IIntegrate(675,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(d, Times(e, x)), Plus(m,
                              p)),
                          Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))), p)),
                      x),
                  And(FreeQ(List(a, c, d, e, m, p), x),
                      EqQ(Plus(Times(c, Sqr(d)),
                          Times(a, Sqr(e))), C0),
                      Not(IntegerQ(p)), GtQ(a, C0), GtQ(d, C0), Not(IGtQ(m, C0))))),
          IIntegrate(676,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(a, Plus(p, C1)), Power(d, Subtract(m, C1)),
                      Power(Times(Subtract(d, Times(e, x)), Power(d, CN1)), Plus(p, C1)),
                      Power(Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))),
                          Plus(p, C1)), CN1)),
                      Integrate(
                          Times(Power(Plus(C1, Times(e, x, Power(d, CN1))),
                              Plus(m, p)),
                              Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, m), x),
                      EqQ(Plus(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      Not(IntegerQ(p)), Or(IntegerQ(m), GtQ(d,
                          C0)),
                      GtQ(a, C0), Not(And(IGtQ(m, C0),
                          Or(IntegerQ(Times(C3, p)), IntegerQ(Times(C4, p)))))))),
          IIntegrate(677,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(d, m), Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                          Power(Times(Power(Plus(C1, Times(e, x, Power(d, CN1))), FracPart(p)),
                              Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))),
                                  FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(C1, Times(e, x, Power(d, CN1))), Plus(m, p)),
                              Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      Not(IntegerQ(p)), Or(IntegerQ(m), GtQ(d,
                          C0)),
                      Not(And(IGtQ(m, C0), Or(IntegerQ(Times(C3, p)), IntegerQ(Times(C4, p)))))))),
          IIntegrate(678,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(d, Subtract(m, C1)),
                          Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                          Power(Times(Power(Plus(C1, Times(e, x, Power(d, CN1))), Plus(p, C1)),
                              Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))),
                                  Plus(p, C1))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(C1, Times(e, x, Power(d, CN1))),
                              Plus(m, p)),
                              Power(Plus(Times(a, Power(d, CN1)), Times(c, x, Power(e, CN1))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, m), x),
                      EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), Not(IntegerQ(p)),
                      Or(IntegerQ(m), GtQ(d,
                          C0)),
                      Not(And(IGtQ(m, C0), Or(IntegerQ(Times(C3, p)), IntegerQ(Times(C4, p)))))))),
          IIntegrate(679,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(d, IntPart(m)), Power(Plus(d, Times(e,
                              x)), FracPart(
                                  m)),
                          Power(Power(Plus(C1, Times(e, x, Power(d, CN1))), FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(Plus(C1, Times(e, x, Power(d, CN1))), m),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a,
                          Sqr(e))), C0),
                      Not(IntegerQ(p)), Not(Or(IntegerQ(m), GtQ(d, C0)))))),
          IIntegrate(680,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(d, IntPart(m)), Power(Plus(d, Times(e, x)), FracPart(m)),
                          Power(Power(Plus(C1, Times(e, x, Power(d, CN1))), FracPart(m)), CN1)),
                      Integrate(Times(Power(Plus(C1, Times(e, x, Power(d, CN1))), m),
                          Power(Plus(a, Times(c, Sqr(x))), p)), x),
                      x),
                  And(FreeQ(List(a, c, d, e, m), x),
                      EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), Not(IntegerQ(p)),
                      Not(Or(IntegerQ(m), GtQ(d, C0)))))));
}
