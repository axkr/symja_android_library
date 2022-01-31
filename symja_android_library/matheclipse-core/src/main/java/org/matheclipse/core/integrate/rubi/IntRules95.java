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
class IntRules95 {
  public static IAST RULES = List(
      IIntegrate(1901,
          Integrate(
              Power(
                  Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_,
                          q_DEFAULT)),
                      Times(c_DEFAULT, Power(x_, r_DEFAULT))),
                  p_),
              x_Symbol),
          Condition(
              Integrate(Power(Times(Plus(a, b, c), Power(x, n)),
                  p), x),
              And(FreeQ(List(a, b, c, n, p), x), EqQ(n, q), EqQ(r, n)))),
      IIntegrate(1902,
          Integrate(
              Power(
                  Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                          Power(x_, r_DEFAULT))),
                  p_),
              x_Symbol),
          Condition(
              Integrate(
                  Times(
                      Power(x, Times(p,
                          q)),
                      Power(
                          Plus(
                              a, Times(b, Power(x, Subtract(n, q))), Times(c,
                                  Power(x, Times(C2, Subtract(n, q))))),
                          p)),
                  x),
              And(FreeQ(List(a, b, c, n, q), x), EqQ(r, Subtract(Times(C2,
                  n), q)), PosQ(Subtract(n,
                      q)),
                  IntegerQ(p)))),
      IIntegrate(1903,
          Integrate(
              Sqrt(
                  Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_,
                          q_DEFAULT)),
                      Times(c_DEFAULT, Power(x_, r_DEFAULT)))),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Sqrt(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                          Times(c, Power(x, Subtract(Times(C2, n), q))))),
                      Power(Times(Power(x, Times(C1D2, q)),
                          Sqrt(Plus(a, Times(b, Power(x, Subtract(n, q))),
                              Times(c, Power(x, Times(C2, Subtract(n, q))))))),
                          CN1)),
                  Integrate(Times(Power(x, Times(C1D2, q)),
                      Sqrt(Plus(a, Times(b, Power(x, Subtract(n, q))),
                          Times(c, Power(x, Times(C2, Subtract(n, q))))))),
                      x),
                  x),
              And(FreeQ(List(a, b, c, n, q), x), EqQ(r, Subtract(Times(C2, n), q)),
                  PosQ(Subtract(n, q))))),
      IIntegrate(1904,
          Integrate(
              Power(
                  Plus(
                      Times(a_DEFAULT, Sqr(x_)), Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(
                          c_DEFAULT, Power(x_, r_DEFAULT))),
                  CN1D2),
              x_Symbol),
          Condition(
              Dist(
                  Times(CN2, Power(Subtract(n, C2),
                      CN1)),
                  Subst(
                      Integrate(Power(Subtract(Times(C4, a), Sqr(x)),
                          CN1), x),
                      x,
                      Times(
                          x, Plus(Times(C2, a), Times(b,
                              Power(x, Subtract(n, C2)))),
                          Power(
                              Plus(Times(a, Sqr(x)), Times(b, Power(x, n)), Times(c, Power(x, r))),
                              CN1D2))),
                  x),
              And(FreeQ(List(a, b, c, n, r), x), EqQ(r, Subtract(Times(C2, n), C2)),
                  PosQ(Subtract(n, C2)), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
      IIntegrate(1905,
          Integrate(
              Power(
                  Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                          Power(x_, r_DEFAULT))),
                  CN1D2),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(x, Times(C1D2,
                          q)),
                      Sqrt(
                          Plus(a, Times(b, Power(x, Subtract(n, q))),
                              Times(c, Power(x, Times(C2, Subtract(n, q)))))),
                      Power(
                          Plus(
                              Times(a, Power(x, q)), Times(b, Power(x,
                                  n)),
                              Times(c, Power(x, Subtract(Times(C2, n), q)))),
                          CN1D2)),
                  Integrate(
                      Power(
                          Times(
                              Power(x, Times(C1D2,
                                  q)),
                              Sqrt(Plus(a, Times(b, Power(x, Subtract(n, q))),
                                  Times(c, Power(x, Times(C2, Subtract(n, q))))))),
                          CN1),
                      x),
                  x),
              And(FreeQ(List(a, b, c, n, q), x), EqQ(r, Subtract(Times(C2, n), q)),
                  PosQ(Subtract(n, q))))),
      IIntegrate(1906,
          Integrate(
              Power(
                  Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_,
                          q_DEFAULT)),
                      Times(c_DEFAULT, Power(x_, r_DEFAULT))),
                  p_),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(x,
                          Power(
                              Plus(Times(a, Power(x, q)), Times(b, Power(x, n)), Times(c,
                                  Power(x, Subtract(Times(C2, n), q)))),
                              p),
                          Power(Plus(Times(p, Subtract(Times(C2, n), q)), C1), CN1)),
                      x),
                  Dist(
                      Times(Subtract(n, q), p,
                          Power(Plus(Times(p, Subtract(Times(C2, n), q)), C1), CN1)),
                      Integrate(Times(Power(x, q),
                          Plus(Times(C2, a), Times(b, Power(x, Subtract(n, q)))),
                          Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                              Times(c, Power(x, Subtract(Times(C2, n), q)))), Subtract(p, C1))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, n, q), x), EqQ(r, Subtract(Times(C2, n), q)),
                  PosQ(Subtract(n, q)), Not(IntegerQ(p)),
                  NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), GtQ(p,
                      C0),
                  NeQ(Plus(Times(p, Subtract(Times(C2, n), q)), C1), C0)))),
      IIntegrate(1907,
          Integrate(
              Power(
                  Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                          Power(x_, r_DEFAULT))),
                  p_),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(Power(x, Plus(Negate(q), C1)),
                              Plus(
                                  Sqr(b), Times(CN1, C2, a, c), Times(b, c,
                                      Power(x, Subtract(n, q)))),
                              Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                  Times(c, Power(x, Subtract(Times(C2, n), q)))), Plus(p, C1)),
                              Power(
                                  Times(a, Subtract(n, q), Plus(p, C1),
                                      Subtract(Sqr(b), Times(C4, a, c))),
                                  CN1)),
                          x)),
                  Dist(
                      Power(
                          Times(a, Subtract(n, q), Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))),
                          CN1),
                      Integrate(Times(
                          Plus(Times(Plus(Times(p, q), C1), Subtract(Sqr(b), Times(C2, a, c))),
                              Times(Subtract(n, q), Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))),
                              Times(b, c,
                                  Plus(Times(p, q), Times(Subtract(n, q), Plus(Times(C2, p), C3)),
                                      C1),
                                  Power(x, Subtract(n, q)))),
                          Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                              Times(c, Power(x, Subtract(Times(C2, n), q)))),
                              Plus(p, C1)),
                          Power(Power(x, q), CN1)), x),
                      x)),
              And(FreeQ(List(a, b, c, n, q), x), EqQ(r, Subtract(Times(C2, n), q)),
                  PosQ(Subtract(n, q)), Not(IntegerQ(p)), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                      C0),
                  LtQ(p, CN1)))),
      IIntegrate(1908,
          Integrate(Power(Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
              Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT, Power(x_, r_DEFAULT))), p_),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(
                          Plus(
                              Times(a, Power(x, q)), Times(b, Power(x,
                                  n)),
                              Times(c, Power(x, Subtract(Times(C2, n), q)))),
                          p),
                      Power(
                          Times(Power(x, Times(p, q)),
                              Power(Plus(a, Times(b, Power(x, Subtract(n, q))),
                                  Times(c, Power(x, Times(C2, Subtract(n, q))))),
                                  p)),
                          CN1)),
                  Integrate(
                      Times(Power(x, Times(p, q)),
                          Power(
                              Plus(a, Times(b, Power(x, Subtract(n, q))),
                                  Times(c, Power(x, Times(C2, Subtract(n, q))))),
                              p)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, n, p, q), x), EqQ(r, Subtract(Times(C2, n), q)),
                  PosQ(Subtract(n, q)), Not(IntegerQ(p))))),
      IIntegrate(1909,
          Integrate(
              Power(
                  Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                          Power(x_, r_DEFAULT))),
                  p_),
              x_Symbol),
          Condition(
              Unintegrable(
                  Power(
                      Plus(
                          Times(b, Power(x, n)), Times(c, Power(x,
                              Subtract(Times(C2, n), q))),
                          Times(a, Power(x, q))),
                      p),
                  x),
              And(FreeQ(List(a, b, c, n, p, q), x), EqQ(r, Subtract(Times(C2, n), q))))),
      IIntegrate(1910,
          Integrate(
              Power(
                  Plus(Times(b_DEFAULT, Power(u_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(u_,
                          q_DEFAULT)),
                      Times(c_DEFAULT, Power(u_, r_DEFAULT))),
                  p_),
              x_Symbol),
          Condition(
              Dist(Power(Coefficient(u, x, C1), CN1),
                  Subst(Integrate(Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                      Times(c, Power(x, Subtract(Times(C2, n), q)))), p), x), x, u),
                  x),
              And(FreeQ(List(a, b, c, n, p, q), x), EqQ(r, Subtract(Times(C2, n), q)),
                  LinearQ(u, x), NeQ(u, x)))),
      IIntegrate(1911,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  Power(
                      Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                          Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                              Power(x_, r_DEFAULT))),
                      p_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(Times(Power(x, m),
                  Power(Times(Plus(a, b, c), Power(x, n)), p)), x),
              And(FreeQ(List(a, b, c, m, n, p), x), EqQ(q, n), EqQ(r, n)))),
      IIntegrate(1912, Integrate(Times(Power(x_, m_DEFAULT),
          Power(Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(a_DEFAULT, Power(x_, q_DEFAULT)),
              Times(c_DEFAULT, Power(x_, r_DEFAULT))), p_DEFAULT)),
          x_Symbol),
          Condition(
              Integrate(
                  Times(Power(x, Plus(m, Times(p, q))),
                      Power(
                          Plus(a, Times(b, Power(x, Subtract(n, q))),
                              Times(c, Power(x, Times(C2, Subtract(n, q))))),
                          p)),
                  x),
              And(FreeQ(List(a, b, c, m, n, q), x), EqQ(r, Subtract(Times(C2, n), q)), IntegerQ(p),
                  PosQ(Subtract(n, q))))),
      IIntegrate(1913,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  Power(Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                          Power(x_, r_DEFAULT))),
                      CN1D2)),
              x_Symbol),
          Condition(
              Dist(
                  Times(CN2, Power(Subtract(n, q),
                      CN1)),
                  Subst(
                      Integrate(Power(Subtract(Times(C4, a), Sqr(x)),
                          CN1), x),
                      x,
                      Times(Power(x, Plus(m, C1)),
                          Plus(Times(C2, a), Times(b, Power(x, Subtract(n, q)))),
                          Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                              Times(c, Power(x, r))), CN1D2))),
                  x),
              And(FreeQ(List(a, b, c, m, n, q, r), x), EqQ(r, Subtract(Times(C2, n), q)),
                  PosQ(Subtract(n, q)), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), EqQ(m,
                      Subtract(Times(C1D2, q), C1))))),
      IIntegrate(1914,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  Power(
                      Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                          Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                              Power(x_, r_DEFAULT))),
                      CN1D2)),
              x_Symbol),
          Condition(Dist(
              Times(Power(x, Times(C1D2, q)),
                  Sqrt(Plus(a, Times(b, Power(x, Subtract(n, q))),
                      Times(c, Power(x, Times(C2, Subtract(n, q)))))),
                  Power(
                      Plus(
                          Times(a, Power(x, q)), Times(b, Power(x, n)),
                          Times(c, Power(x, Subtract(Times(C2, n), q)))),
                      CN1D2)),
              Integrate(Times(Power(x, Subtract(m, Times(C1D2, q))),
                  Power(Plus(a, Times(b, Power(x, Subtract(n, q))),
                      Times(c, Power(x, Times(C2, Subtract(n, q))))), CN1D2)),
                  x),
              x),
              And(FreeQ(List(a, b, c, m, n, q), x), EqQ(r, Subtract(Times(C2, n), q)),
                  PosQ(Subtract(n,
                      q)),
                  Or(And(EqQ(m, C1), EqQ(n, C3), EqQ(q,
                      C2)), And(
                          Or(EqQ(m, QQ(3L, 2L)), EqQ(m, C1D2), EqQ(m,
                              QQ(5L, 2L))),
                          EqQ(n, C3), EqQ(q, C1)))))),
      IIntegrate(1915,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  Power(
                      Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                          Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                              Power(x_, r_DEFAULT))),
                      QQ(-3L, 2L))),
              x_Symbol),
          Condition(
              Simp(
                  Times(CN2, Power(x, Times(C1D2, Subtract(n, C1))), Plus(b, Times(C2, c, x)),
                      Power(Times(Subtract(Sqr(b), Times(C4, a, c)),
                          Sqrt(Plus(Times(a, Power(x, Subtract(n, C1))), Times(b, Power(x, n)),
                              Times(c, Power(x, Plus(n, C1)))))),
                          CN1)),
                  x),
              And(FreeQ(List(a, b, c, n), x), EqQ(m, Times(C1D2, C3, Subtract(n, C1))),
                  EqQ(q, Subtract(n, C1)), EqQ(r,
                      Plus(n, C1)),
                  NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
      IIntegrate(1916, Integrate(Times(Power(x_, m_DEFAULT),
          Power(Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(a_DEFAULT, Power(x_, q_DEFAULT)),
              Times(c_DEFAULT, Power(x_, r_DEFAULT))), QQ(-3L, 2L))),
          x_Symbol),
          Condition(
              Simp(
                  Times(Power(x, Times(C1D2, Subtract(n, C1))), Plus(Times(C4, a), Times(C2, b, x)),
                      Power(Times(Subtract(Sqr(b), Times(C4, a, c)),
                          Sqrt(Plus(Times(a, Power(x, Subtract(n, C1))), Times(b, Power(x, n)),
                              Times(c, Power(x, Plus(n, C1)))))),
                          CN1)),
                  x),
              And(FreeQ(List(a, b, c, n), x), EqQ(m, Times(C1D2, Subtract(Times(C3, n), C1))),
                  EqQ(q, Subtract(n, C1)), EqQ(r,
                      Plus(n, C1)),
                  NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
      IIntegrate(1917,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  Power(Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                          Power(x_, r_DEFAULT))),
                      p_)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(
                          Power(x, Subtract(m,
                              n)),
                          Power(
                              Plus(
                                  Times(a, Power(x, Subtract(n, C1))), Times(b,
                                      Power(x, n)),
                                  Times(c, Power(x, Plus(n, C1)))),
                              Plus(p, C1)),
                          Power(Times(C2, c, Plus(p, C1)), CN1)),
                      x),
                  Dist(
                      Times(b, Power(Times(C2, c),
                          CN1)),
                      Integrate(
                          Times(
                              Power(x, Subtract(m,
                                  C1)),
                              Power(Plus(Times(a, Power(x, Subtract(n, C1))), Times(b, Power(x, n)),
                                  Times(c, Power(x, Plus(n, C1)))), p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c), x), EqQ(r,
                  Subtract(Times(C2, n), q)), PosQ(Subtract(n, q)), Not(IntegerQ(p)),
                  NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(n, C0), RationalQ(m, p,
                      q),
                  EqQ(Subtract(Plus(m, Times(p, Subtract(n, C1))), C1), C0)))),
      IIntegrate(1918,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  Power(
                      Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                          Times(a_DEFAULT, Power(x_,
                              q_DEFAULT)),
                          Times(c_DEFAULT, Power(x_, r_DEFAULT))),
                      p_)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Power(x, Plus(m, Negate(n), q, C1)),
                          Plus(b, Times(C2, c,
                              Power(x, Subtract(n, q)))),
                          Power(
                              Plus(
                                  Times(a, Power(x, q)), Times(b, Power(x, n)), Times(c,
                                      Power(x, Subtract(Times(C2, n), q)))),
                              p),
                          Power(Times(C2, c, Subtract(n, q), Plus(Times(C2, p), C1)), CN1)),
                      x),
                  Dist(
                      Times(p, Subtract(Sqr(b), Times(C4, a, c)),
                          Power(Times(C2, c, Plus(Times(C2, p), C1)), CN1)),
                      Integrate(Times(Power(x, Plus(m, q)),
                          Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                              Times(c, Power(x, Subtract(Times(C2, n), q)))), Subtract(p, C1))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c), x), EqQ(r, Subtract(Times(C2, n), q)), PosQ(Subtract(n, q)),
                  Not(IntegerQ(p)), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(n, C0), GtQ(p,
                      C0),
                  RationalQ(m, q), EqQ(Plus(m, Times(p, q), C1), Subtract(n, q))))),
      IIntegrate(1919,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  Power(
                      Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                          Times(a_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                              Power(x_, r_DEFAULT))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(
                          Power(x, Plus(m, Negate(n), q,
                              C1)),
                          Plus(Times(b, Subtract(n, q), p),
                              Times(c,
                                  Plus(
                                      m, Times(p, q),
                                      Times(Subtract(n, q), Subtract(Times(C2, p), C1)), C1),
                                  Power(x, Subtract(n, q)))),
                          Power(
                              Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                  Times(c, Power(x, Subtract(Times(C2, n), q)))),
                              p),
                          Power(
                              Times(c, Plus(m, Times(p, Subtract(Times(C2, n), q)), C1),
                                  Plus(m, Times(p, q),
                                      Times(Subtract(n, q), Subtract(Times(C2, p), C1)), C1)),
                              CN1)),
                      x),
                  Dist(
                      Times(Subtract(n, q), p,
                          Power(Times(c, Plus(m, Times(p, Subtract(Times(C2, n), q)), C1),
                              Plus(
                                  m, Times(p, q), Times(Subtract(n, q), Subtract(Times(C2, p), C1)),
                                  C1)),
                              CN1)),
                      Integrate(Times(
                          Power(x, Subtract(m,
                              Subtract(n, Times(C2, q)))),
                          Simp(
                              Plus(
                                  Times(CN1, a, b, Plus(m, Times(p, q), Negate(n), q,
                                      C1)),
                                  Times(
                                      Subtract(Times(C2, a, c, Plus(m, Times(p, q),
                                          Times(Subtract(n, q), Subtract(Times(C2, p), C1)), C1)),
                                          Times(Sqr(b),
                                              Plus(m, Times(p, q),
                                                  Times(Subtract(n, q), Subtract(p, C1)), C1))),
                                      Power(x, Subtract(n, q)))),
                              x),
                          Power(
                              Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                  Times(c, Power(x, Subtract(Times(C2, n), q)))),
                              Subtract(p, C1))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c), x), EqQ(r, Subtract(Times(C2, n), q)), PosQ(Subtract(n, q)),
                  Not(IntegerQ(p)), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(n, C0),
                  GtQ(p, C0), RationalQ(m, q), GtQ(Plus(m, Times(p, q), C1), Subtract(n, q)),
                  NeQ(Plus(m, Times(p, Subtract(Times(C2, n), q)),
                      C1), C0),
                  NeQ(Plus(m, Times(p, q), Times(Subtract(n, q), Subtract(Times(C2, p), C1)), C1),
                      C0)))),
      IIntegrate(1920,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  Power(
                      Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                          Times(a_DEFAULT, Power(x_,
                              q_DEFAULT)),
                          Times(c_DEFAULT, Power(x_, r_DEFAULT))),
                      p_)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Power(x, Plus(m, C1)),
                          Power(
                              Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                  Times(c, Power(x, Subtract(Times(C2, n), q)))),
                              p),
                          Power(Plus(m, Times(p, q), C1), CN1)),
                      x),
                  Dist(Times(Subtract(n, q), p, Power(Plus(m, Times(p, q), C1), CN1)),
                      Integrate(Times(Power(x, Plus(m, n)),
                          Plus(b, Times(C2, c, Power(x, Subtract(n, q)))),
                          Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                              Times(c, Power(x, Subtract(Times(C2, n), q)))), Subtract(p, C1))),
                          x),
                      x)),
              And(FreeQ(List(a, b, c), x), EqQ(r, Subtract(Times(C2, n), q)), PosQ(Subtract(n, q)),
                  Not(IntegerQ(p)), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(n, C0),
                  GtQ(p, C0), RationalQ(m, q),
                  LeQ(Plus(m, Times(p, q), C1), Plus(Negate(Subtract(n, q)), C1)),
                  NeQ(Plus(m, Times(p, q), C1), C0)))));
}
