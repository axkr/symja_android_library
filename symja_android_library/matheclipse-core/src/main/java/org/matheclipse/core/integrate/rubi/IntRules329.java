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
class IntRules329 {
  public static IAST RULES = List(
      IIntegrate(
          6581, Integrate(Zeta(C2, Plus(a_DEFAULT, Times(b_DEFAULT, x_))), x_Symbol), Condition(
              Integrate(PolyGamma(C1, Plus(a, Times(b, x))), x), FreeQ(List(a, b), x))),
      IIntegrate(
          6582, Integrate(Zeta(s_,
              Plus(a_DEFAULT, Times(b_DEFAULT, x_))), x_Symbol),
          Condition(
              Negate(Simp(Times(Zeta(Subtract(s, C1), Plus(a, Times(b, x))),
                  Power(Times(b, Subtract(s, C1)), CN1)), x)),
              And(FreeQ(List(a, b, s), x), NeQ(s, C1), NeQ(s, C2)))),
      IIntegrate(6583,
          Integrate(
              Times(
                  Power(Plus(c_DEFAULT,
                      Times(d_DEFAULT, x_)), m_DEFAULT),
                  Zeta(C2, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Integrate(Times(Power(Plus(c,
                  Times(d, x)), m), PolyGamma(C1,
                      Plus(a, Times(b, x)))),
                  x),
              And(FreeQ(List(a, b, c, d), x), RationalQ(m)))),
      IIntegrate(6584,
          Integrate(
              Times(
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT), Zeta(s_,
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Zeta(Subtract(s, C1), Plus(a, Times(b, x))),
                              Power(Times(b, Subtract(s, C1)), CN1)),
                          x)),
                  Dist(
                      Times(d, m, Power(Times(b, Subtract(s, C1)),
                          CN1)),
                      Integrate(
                          Times(
                              Power(Plus(c, Times(d, x)), Subtract(m, C1)), Zeta(Subtract(s, C1),
                                  Plus(a, Times(b, x)))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, s), x), NeQ(s, C1), NeQ(s, C2), GtQ(m, C0)))),
      IIntegrate(6585,
          Integrate(
              Times(
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT), Zeta(s_,
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(
                          Power(Plus(c, Times(d, x)), Plus(m, C1)), Zeta(s,
                              Plus(a, Times(b, x))),
                          Power(Times(d, Plus(m, C1)), CN1)),
                      x),
                  Dist(
                      Times(b, s, Power(Times(d, Plus(m, C1)),
                          CN1)),
                      Integrate(
                          Times(Power(Plus(c, Times(d, x)), Plus(m, C1)),
                              Zeta(Plus(s, C1), Plus(a, Times(b, x)))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, s), x), NeQ(s, C1), NeQ(s, C2), LtQ(m, CN1)))),
      IIntegrate(6586,
          Integrate(
              PolyLog(n_, Times(a_DEFAULT,
                  Power(Times(b_DEFAULT, Power(x_, p_DEFAULT)), q_DEFAULT))),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(x,
                      PolyLog(n, Times(a, Power(Times(b, Power(x, p)), q)))), x),
                  Dist(Times(p, q),
                      Integrate(PolyLog(Subtract(n, C1), Times(a, Power(Times(b, Power(x, p)), q))),
                          x),
                      x)),
              And(FreeQ(List(a, b, p, q), x), GtQ(n, C0)))),
      IIntegrate(6587,
          Integrate(PolyLog(n_, Times(a_DEFAULT,
              Power(Times(b_DEFAULT, Power(x_, p_DEFAULT)), q_DEFAULT))), x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(x, PolyLog(Plus(n, C1), Times(a, Power(Times(b, Power(x, p)), q))),
                      Power(Times(p, q), CN1)), x),
                  Dist(Power(Times(p, q), CN1),
                      Integrate(PolyLog(Plus(n, C1), Times(a, Power(Times(b, Power(x, p)), q))),
                          x),
                      x)),
              And(FreeQ(List(a, b, p, q), x), LtQ(n, CN1)))),
      IIntegrate(6588,
          Integrate(
              PolyLog(n_, Times(a_DEFAULT,
                  Power(Times(b_DEFAULT, Power(x_, p_DEFAULT)), q_DEFAULT))),
              x_Symbol),
          Condition(
              Unintegrable(PolyLog(n,
                  Times(a, Power(Times(b, Power(x, p)), q))), x),
              FreeQ(List(a, b, n, p, q), x))),
      IIntegrate(6589,
          Integrate(
              Times(
                  Power(Plus(d_DEFAULT,
                      Times(e_DEFAULT, x_)), CN1),
                  PolyLog(
                      n_, Times(c_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                          p_DEFAULT)))),
              x_Symbol),
          Condition(
              Simp(
                  Times(PolyLog(Plus(n, C1), Times(c, Power(Plus(a, Times(b, x)), p))),
                      Power(Times(e, p), CN1)),
                  x),
              And(FreeQ(List(a, b, c, d, e, n, p), x), EqQ(Times(b, d), Times(a, e))))),
      IIntegrate(6590,
          Integrate(
              Times(Power(x_, CN1),
                  PolyLog(
                      n_, Times(a_DEFAULT, Power(Times(b_DEFAULT, Power(x_, p_DEFAULT)),
                          q_DEFAULT)))),
              x_Symbol),
          Condition(
              Simp(
                  Times(
                      PolyLog(Plus(n, C1), Times(a, Power(Times(b, Power(x, p)), q))), Power(
                          Times(p, q), CN1)),
                  x),
              FreeQ(List(a, b, n, p, q), x))),
      IIntegrate(6591,
          Integrate(
              Times(Power(Times(d_DEFAULT, x_), m_DEFAULT),
                  PolyLog(n_, Times(a_DEFAULT, Power(Times(b_DEFAULT, Power(x_, p_DEFAULT)),
                      q_DEFAULT)))),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Power(Times(d, x), Plus(m, C1)),
                          PolyLog(n, Times(a, Power(Times(b, Power(x, p)), q))), Power(
                              Times(d, Plus(m, C1)), CN1)),
                      x),
                  Dist(
                      Times(p, q, Power(Plus(m, C1),
                          CN1)),
                      Integrate(
                          Times(Power(Times(d, x), m), PolyLog(Subtract(n, C1),
                              Times(a, Power(Times(b, Power(x, p)), q)))),
                          x),
                      x)),
              And(FreeQ(List(a, b, d, m, p, q), x), NeQ(m, CN1), GtQ(n, C0)))),
      IIntegrate(6592,
          Integrate(
              Times(
                  Power(Times(d_DEFAULT,
                      x_), m_DEFAULT),
                  PolyLog(
                      n_, Times(a_DEFAULT, Power(Times(b_DEFAULT, Power(x_, p_DEFAULT)),
                          q_DEFAULT)))),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Power(Times(d, x), Plus(m, C1)),
                          PolyLog(Plus(n, C1), Times(a, Power(Times(b, Power(x, p)), q))), Power(
                              Times(d, p, q), CN1)),
                      x),
                  Dist(Times(Plus(m, C1), Power(Times(p, q), CN1)),
                      Integrate(Times(Power(Times(d, x), m),
                          PolyLog(Plus(n, C1), Times(a, Power(Times(b, Power(x, p)), q)))), x),
                      x)),
              And(FreeQ(List(a, b, d, m, p, q), x), NeQ(m, CN1), LtQ(n, CN1)))),
      IIntegrate(6593,
          Integrate(
              Times(
                  Power(Times(d_DEFAULT,
                      x_), m_DEFAULT),
                  PolyLog(
                      n_, Times(a_DEFAULT, Power(Times(b_DEFAULT, Power(x_, p_DEFAULT)),
                          q_DEFAULT)))),
              x_Symbol),
          Condition(
              Unintegrable(
                  Times(Power(Times(d,
                      x), m), PolyLog(n,
                          Times(a, Power(Times(b, Power(x, p)), q)))),
                  x),
              FreeQ(List(a, b, d, m, n, p, q), x))),
      IIntegrate(6594,
          Integrate(
              Times(
                  Power(Log(
                      Times(c_DEFAULT, Power(x_, m_DEFAULT))), r_DEFAULT),
                  Power(x_, CN1),
                  PolyLog(
                      n_, Times(a_DEFAULT, Power(Times(b_DEFAULT, Power(x_, p_DEFAULT)),
                          q_DEFAULT)))),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(Power(Log(Times(c, Power(x, m))), r),
                      PolyLog(Plus(n, C1), Times(a, Power(Times(b, Power(x, p)), q))), Power(
                          Times(p, q), CN1)),
                      x),
                  Dist(Times(m, r, Power(Times(p, q), CN1)),
                      Integrate(Times(Power(Log(Times(c, Power(x, m))), Subtract(r, C1)),
                          PolyLog(Plus(n, C1), Times(a, Power(Times(b, Power(x, p)), q))), Power(x,
                              CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, m, n, q, r), x), GtQ(r, C0)))),
      IIntegrate(6595,
          Integrate(
              PolyLog(n_, Times(c_DEFAULT,
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), p_DEFAULT))),
              x_Symbol),
          Condition(
              Plus(
                  Simp(Times(x,
                      PolyLog(n, Times(c, Power(Plus(a, Times(b, x)), p)))), x),
                  Negate(
                      Dist(p,
                          Integrate(
                              PolyLog(Subtract(n, C1),
                                  Times(c, Power(Plus(a, Times(b, x)), p))),
                              x),
                          x)),
                  Dist(Times(a, p),
                      Integrate(
                          Times(PolyLog(Subtract(n, C1), Times(c, Power(Plus(a, Times(b, x)), p))),
                              Power(Plus(a, Times(b, x)), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, p), x), GtQ(n, C0)))),
      IIntegrate(6596,
          Integrate(
              Times(
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1), PolyLog(C2, Times(c_DEFAULT,
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Plus(
                  Simp(Times(Log(Subtract(Subtract(C1, Times(a, c)), Times(b, c, x))),
                      PolyLog(C2, Times(c, Plus(a, Times(b, x)))), Power(e, CN1)), x),
                  Dist(
                      Times(b, Power(e,
                          CN1)),
                      Integrate(
                          Times(Sqr(Log(Subtract(Subtract(C1, Times(a, c)), Times(b, c, x)))),
                              Power(Plus(a, Times(b, x)), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d,
                  e), x), EqQ(Plus(Times(c, Subtract(Times(b, d), Times(a, e))), e),
                      C0)))),
      IIntegrate(6597,
          Integrate(
              Times(
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1), PolyLog(C2, Times(c_DEFAULT,
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(
                          Log(Plus(d, Times(e, x))), PolyLog(C2,
                              Times(c, Plus(a, Times(b, x)))),
                          Power(e, CN1)),
                      x),
                  Dist(
                      Times(b, Power(e,
                          CN1)),
                      Integrate(Times(Log(Plus(d, Times(e, x))),
                          Log(Subtract(Subtract(C1, Times(a, c)), Times(b, c, x))),
                          Power(Plus(a, Times(b, x)), CN1)), x),
                      x)),
              And(FreeQ(List(a, b, c, d, e), x),
                  NeQ(Plus(Times(c, Subtract(Times(b, d), Times(a, e))), e), C0)))),
      IIntegrate(6598,
          Integrate(
              Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT), PolyLog(C2,
                  Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          PolyLog(C2, Times(c, Plus(a, Times(b, x)))), Power(Times(e, Plus(m, C1)),
                              CN1)),
                      x),
                  Dist(Times(b, Power(Times(e, Plus(m, C1)), CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Log(Subtract(Subtract(C1, Times(a, c)), Times(b, c, x))), Power(
                                  Plus(a, Times(b, x)), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, m), x), NeQ(m, CN1)))),
      IIntegrate(6599,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  PolyLog(
                      n_, Times(c_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                          p_DEFAULT)))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(
                              Subtract(Power(a, Plus(m, C1)),
                                  Times(Power(b, Plus(m, C1)), Power(x, Plus(m, C1)))),
                              PolyLog(n, Times(c, Power(Plus(a, Times(b, x)), p))),
                              Power(Times(Plus(m, C1), Power(b, Plus(m, C1))), CN1)),
                          x)),
                  Dist(Times(p, Power(Times(Plus(m, C1), Power(b, m)), CN1)),
                      Integrate(ExpandIntegrand(
                          PolyLog(Subtract(n, C1), Times(c, Power(Plus(a, Times(b, x)), p))),
                          Times(
                              Subtract(Power(a, Plus(m, C1)),
                                  Times(Power(b, Plus(m, C1)), Power(x, Plus(m, C1)))),
                              Power(Plus(a, Times(b, x)), CN1)),
                          x), x),
                      x)),
              And(FreeQ(List(a, b, c, p), x), GtQ(n, C0), IntegerQ(m), NeQ(m, CN1)))),
      IIntegrate(6600,
          Integrate(
              Times(
                  Plus(g_DEFAULT,
                      Times(Log(Times(f_DEFAULT, Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)),
                          n_DEFAULT))), h_DEFAULT)),
                  PolyLog(C2, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(
                          x, Plus(g, Times(h,
                              Log(Times(f, Power(Plus(d, Times(e, x)), n))))),
                          PolyLog(C2, Times(c, Plus(a, Times(b, x))))),
                      x),
                  Dist(b,
                      Integrate(Times(
                          Plus(g, Times(h, Log(Times(f, Power(Plus(d, Times(e, x)), n))))),
                          Log(Subtract(Subtract(C1, Times(a, c)), Times(b, c, x))), ExpandIntegrand(
                              Times(x, Power(Plus(a, Times(b, x)), CN1)), x)),
                          x),
                      x),
                  Negate(Dist(Times(e, h, n),
                      Integrate(Times(PolyLog(C2, Times(c, Plus(a, Times(b, x)))),
                          ExpandIntegrand(Times(x, Power(Plus(d, Times(e, x)), CN1)), x)), x),
                      x))),
              FreeQ(List(a, b, c, d, e, f, g, h, n), x))));
}
