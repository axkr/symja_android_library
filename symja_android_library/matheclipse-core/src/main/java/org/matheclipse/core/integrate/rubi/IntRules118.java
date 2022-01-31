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
class IntRules118 {
  public static IAST RULES =
      List(
          IIntegrate(2361,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Plus(
                          d_DEFAULT, Times(Log(Times(f_DEFAULT, Power(x_, r_DEFAULT))),
                              e_DEFAULT))),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u, IntHide(Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                              x))),
                      Subtract(Dist(Plus(d, Times(e, Log(Times(f, Power(x, r))))), u, x),
                          Dist(
                              Times(e, r), Integrate(SimplifyIntegrand(Times(u, Power(x, CN1)), x),
                                  x),
                              x))),
                  FreeQ(List(a, b, c, d, e, f, n, p, r), x))),
          IIntegrate(2362,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(
                          Plus(
                              d_DEFAULT, Times(Log(Times(f_DEFAULT, Power(x_, r_DEFAULT))),
                                  e_DEFAULT)),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              x, Power(Plus(a, Times(b,
                                  Log(Times(c, Power(x, n))))), p),
                              Power(Plus(d, Times(e, Log(Times(f, Power(x, r))))), q)),
                          x),
                      Negate(
                          Dist(Times(b, n, p),
                              Integrate(
                                  Times(
                                      Power(
                                          Plus(a, Times(b,
                                              Log(Times(c, Power(x, n))))),
                                          Subtract(p, C1)),
                                      Power(Plus(d, Times(e, Log(Times(f, Power(x, r))))), q)),
                                  x),
                              x)),
                      Negate(
                          Dist(Times(e, q, r),
                              Integrate(
                                  Times(Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                                      Power(Plus(d, Times(e, Log(Times(f, Power(x, r))))),
                                          Subtract(q, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, n, r), x), IGtQ(p, C0), IGtQ(q, C0)))),
          IIntegrate(2363,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_DEFAULT,
                          Times(Log(Times(f_DEFAULT, Power(x_, r_DEFAULT))), e_DEFAULT)),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                          Power(Plus(d, Times(e, Log(Times(f, Power(x, r))))), q)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, n, p, q, r), x))),
          IIntegrate(2364,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(Log(v_), b_DEFAULT)), p_DEFAULT), Power(Plus(
                          c_DEFAULT, Times(Log(v_), d_DEFAULT)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coeff(v, x,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(a,
                                      Times(b, Log(x))), p),
                                  Power(Plus(c, Times(d, Log(x))), q)),
                              x),
                          x, v),
                      x),
                  And(FreeQ(List(a, b, c, d, p, q), x), LinearQ(v, x), NeQ(Coeff(v, x, C0), C0)))),
          IIntegrate(2365,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                          p_DEFAULT),
                      Power(
                          Plus(
                              d_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  e_DEFAULT)),
                          q_DEFAULT),
                      Power(x_, CN1)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a,
                                  Times(b, x)), p), Power(Plus(d, Times(e, x)),
                                      q)),
                              x),
                          x, Log(Times(c, Power(x, n)))),
                      x),
                  FreeQ(List(a, b, c, d, e, n, p, q), x))),
          IIntegrate(2366,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Plus(d_DEFAULT, Times(Log(Times(f_DEFAULT, Power(x_, r_DEFAULT))),
                          e_DEFAULT)),
                      Power(Times(g_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(Times(Power(Times(g, x), m),
                                  Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)), x))),
                      Subtract(Dist(Plus(d, Times(e, Log(Times(f, Power(x, r))))), u, x),
                          Dist(Times(e, r), Integrate(SimplifyIntegrand(Times(u, Power(x, CN1)), x),
                              x), x))),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p, r), x),
                      Not(And(EqQ(p, C1), EqQ(a, C0), NeQ(d, C0)))))),
          IIntegrate(2367,
              Integrate(Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                      p_DEFAULT),
                  Power(
                      Plus(d_DEFAULT, Times(Log(Times(f_DEFAULT, Power(x_, r_DEFAULT))),
                          e_DEFAULT)),
                      q_DEFAULT),
                  Power(Times(g_DEFAULT, x_), m_DEFAULT)), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(g, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                              Power(Plus(d,
                                  Times(e, Log(Times(f, Power(x, r))))), q),
                              Power(Times(g, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(b, n, p, Power(Plus(m, C1),
                                  CN1)),
                              Integrate(
                                  Times(Power(Times(g, x), m),
                                      Power(
                                          Plus(a, Times(b, Log(Times(c, Power(x, n))))), Subtract(p,
                                              C1)),
                                      Power(Plus(d, Times(e, Log(Times(f, Power(x, r))))), q)),
                                  x),
                              x)),
                      Negate(
                          Dist(
                              Times(e, q, r, Power(Plus(m, C1),
                                  CN1)),
                              Integrate(
                                  Times(Power(Times(g, x), m),
                                      Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                                      Power(Plus(d, Times(e, Log(Times(f, Power(x, r))))),
                                          Subtract(q, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, r), x), IGtQ(p, C0), IGtQ(q, C0),
                      NeQ(m, CN1)))),
          IIntegrate(2368,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(
                          Plus(
                              d_DEFAULT, Times(Log(Times(f_DEFAULT, Power(x_, r_DEFAULT))),
                                  e_DEFAULT)),
                          q_DEFAULT),
                      Power(Times(g_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(Times(g, x), m),
                      Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))),
                          p),
                      Power(Plus(d, Times(e, Log(Times(f, Power(x, r))))), q)), x),
                  FreeQ(List(a, b, c, d, e, f, g, m, n, p, q, r), x))),
          IIntegrate(2369,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(Log(v_), b_DEFAULT)), p_DEFAULT),
                      Power(Plus(c_DEFAULT,
                          Times(Log(v_), d_DEFAULT)), q_DEFAULT),
                      Power(u_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(e, Coeff(u, x, C0)), Set(f, Coeff(u, x, C1)), Set(g,
                              Coeff(v, x, C0)),
                          Set(h, Coeff(v, x, C1))),
                      Condition(
                          Dist(Power(h, CN1),
                              Subst(Integrate(Times(Power(Times(f, x, Power(h, CN1)), m),
                                  Power(Plus(a, Times(b, Log(x))), p),
                                  Power(Plus(c, Times(d, Log(x))), q)), x), x, v),
                              x),
                          And(EqQ(Subtract(Times(f, g), Times(e, h)), C0), NeQ(g, C0)))),
                  And(FreeQ(List(a, b, c, d, m, p, q), x), LinearQ(List(u, v), x)))),
          IIntegrate(2370,
              Integrate(
                  Times(
                      Log(Times(
                          d_DEFAULT, Power(Plus(e_, Times(f_DEFAULT, Power(x_, m_DEFAULT))),
                              r_DEFAULT))),
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u, IntHide(Log(Times(d, Power(Plus(e, Times(f, Power(x, m))), r))),
                              x))),
                      Subtract(
                          Dist(Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))),
                              p), u, x),
                          Dist(Times(b, n, p),
                              Integrate(
                                  Dist(Times(Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))),
                                      Subtract(p, C1)), Power(x, CN1)), u, x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, r, m,
                      n), x), IGtQ(p, C0), RationalQ(m), Or(EqQ(p, C1),
                          And(FractionQ(m), IntegerQ(
                              Power(m, CN1))),
                          And(EqQ(r, C1), EqQ(m, C1), EqQ(Times(d, e), C1)))))),
          IIntegrate(2371,
              Integrate(
                  Times(
                      Log(Times(
                          d_DEFAULT, Power(Plus(e_, Times(f_DEFAULT, Power(x_, m_DEFAULT))),
                              r_DEFAULT))),
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u, IntHide(Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                          x))),
                      Subtract(
                          Dist(Log(
                              Times(d, Power(Plus(e, Times(f, Power(x, m))), r))), u, x),
                          Dist(Times(f, m, r),
                              Integrate(
                                  Dist(
                                      Times(
                                          Power(x, Subtract(m, C1)),
                                          Power(Plus(e, Times(f, Power(x, m))), CN1)),
                                      u, x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, r, m, n), x), IGtQ(p, C0), IntegerQ(m)))),
          IIntegrate(2372,
              Integrate(
                  Times(
                      Log(Times(
                          d_DEFAULT, Power(Plus(e_, Times(f_DEFAULT, Power(x_, m_DEFAULT))),
                              r_DEFAULT))),
                      Power(
                          Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                              b_DEFAULT)),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p), Log(
                              Times(d, Power(Plus(e, Times(f, Power(x, m))), r)))),
                      x),
                  FreeQ(List(a, b, c, d, e, f, r, m, n, p), x))),
          IIntegrate(2373,
              Integrate(
                  Times(
                      Log(Times(d_DEFAULT,
                          Power(u_, r_DEFAULT))),
                      Power(
                          Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                              b_DEFAULT)),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Log(Times(d, Power(ExpandToSum(u, x), r))), Power(
                              Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, r, n,
                      p), x), BinomialQ(u,
                          x),
                      Not(BinomialMatchQ(u, x))))),
          IIntegrate(2374,
              Integrate(
                  Times(Log(Times(d_DEFAULT, Plus(e_, Times(f_DEFAULT, Power(x_, m_DEFAULT))))),
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(PolyLog(C2, Times(CN1, d, f, Power(x, m))),
                                  Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                                  Power(m, CN1)),
                              x)),
                      Dist(
                          Times(b, n, p, Power(m,
                              CN1)),
                          Integrate(
                              Times(PolyLog(C2, Times(CN1, d, f, Power(x, m))),
                                  Power(
                                      Plus(a, Times(b, Log(Times(c, Power(x, n))))), Subtract(p,
                                          C1)),
                                  Power(x, CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), IGtQ(p, C0), EqQ(Times(d, e), C1)))),
          IIntegrate(2375,
              Integrate(
                  Times(
                      Log(Times(
                          d_DEFAULT, Power(Plus(e_, Times(f_DEFAULT, Power(x_, m_DEFAULT))),
                              r_DEFAULT))),
                      Power(
                          Plus(a_DEFAULT,
                              Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Log(Times(d, Power(Plus(e,
                                  Times(f, Power(x, m))), r))),
                              Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))),
                                  Plus(p, C1)),
                              Power(Times(b, n, Plus(p, C1)), CN1)),
                          x),
                      Dist(Times(f, m, r, Power(Times(b, n, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, C1)),
                                  Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), Plus(p,
                                      C1)),
                                  Power(Plus(e, Times(f, Power(x, m))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, r, m,
                      n), x), IGtQ(p,
                          C0),
                      NeQ(Times(d, e), C1)))),
          IIntegrate(2376,
              Integrate(
                  Times(
                      Log(Times(
                          d_DEFAULT, Power(Plus(e_, Times(f_DEFAULT, Power(x_, m_DEFAULT))),
                              r_DEFAULT))),
                      Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                          b_DEFAULT)),
                      Power(Times(g_DEFAULT, x_), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times(Power(Times(g, x), q),
                              Log(Times(d, Power(Plus(e, Times(f, Power(x, m))), r)))), x))),
                      Subtract(Dist(Plus(a, Times(b, Log(Times(c, Power(x, n))))), u, x),
                          Dist(Times(b, n), Integrate(Dist(Power(x, CN1), u, x), x), x))),
                  And(FreeQ(List(a, b, c, d, e, f, g, r, m, n,
                      q), x), Or(
                          IntegerQ(Times(Plus(q, C1),
                              Power(m, CN1))),
                          And(RationalQ(m), RationalQ(q))),
                      NeQ(q, CN1)))),
          IIntegrate(2377,
              Integrate(
                  Times(
                      Log(Times(d_DEFAULT,
                          Plus(e_, Times(f_DEFAULT, Power(x_, m_DEFAULT))))),
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(g_DEFAULT, x_), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(
                                      Power(Times(g, x), q), Log(Times(d,
                                          Plus(e, Times(f, Power(x, m)))))),
                                  x))),
                      Subtract(
                          Dist(Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))),
                              p), u, x),
                          Dist(Times(b, n, p), Integrate(Dist(Times(
                              Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), Subtract(p, C1)),
                              Power(x, CN1)), u, x), x), x))),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, q), x), IGtQ(p, C0), RationalQ(m),
                      RationalQ(q), NeQ(q, CN1),
                      Or(EqQ(p, C1), And(FractionQ(m), IntegerQ(Times(Plus(q, C1), Power(m, CN1)))),
                          And(IGtQ(q, C0), IntegerQ(Times(Plus(q, C1), Power(m, CN1))),
                              EqQ(Times(d, e), C1)))))),
          IIntegrate(2378,
              Integrate(
                  Times(
                      Log(Times(
                          d_DEFAULT, Power(Plus(e_, Times(f_DEFAULT, Power(x_, m_DEFAULT))),
                              r_DEFAULT))),
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(g_DEFAULT, x_), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(Times(g, x), q),
                                      Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)),
                                  x))),
                      Subtract(
                          Dist(Log(
                              Times(d, Power(Plus(e, Times(f, Power(x, m))), r))), u, x),
                          Dist(Times(f, m, r),
                              Integrate(
                                  Dist(
                                      Times(Power(x, Subtract(m, C1)),
                                          Power(Plus(e, Times(f, Power(x, m))), CN1)),
                                      u, x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g, r, m, n, q), x), IGtQ(p, C0), RationalQ(m),
                      RationalQ(q)))),
          IIntegrate(2379,
              Integrate(
                  Times(
                      Log(Times(
                          d_DEFAULT, Power(Plus(e_, Times(f_DEFAULT, Power(x_, m_DEFAULT))),
                              r_DEFAULT))),
                      Power(
                          Plus(a_DEFAULT,
                              Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(g_DEFAULT, x_), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Times(g, x), q),
                          Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p), Log(
                              Times(d, Power(Plus(e, Times(f, Power(x, m))), r)))),
                      x),
                  FreeQ(List(a, b, c, d, e, f, g, r, m, n, p, q), x))),
          IIntegrate(2380, Integrate(Times(Log(Times(d_DEFAULT, Power(u_, r_DEFAULT))),
              Power(Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                  p_DEFAULT),
              Power(Times(g_DEFAULT, x_), q_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(Times(Power(Times(g, x), q), Log(Times(d, Power(ExpandToSum(u, x), r))),
                      Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)), x),
                  And(FreeQ(List(a, b, c, d, g, r, n, p, q), x), BinomialQ(u, x),
                      Not(BinomialMatchQ(u, x))))));
}
