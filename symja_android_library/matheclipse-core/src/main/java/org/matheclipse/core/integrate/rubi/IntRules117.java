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
class IntRules117 {
  public static IAST RULES =
      List(
          IIntegrate(2341,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                      b_DEFAULT)), Power(x_, m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                          q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(d, IntPart(q)), Power(Plus(d, Times(e, Sqr(x))), FracPart(q)),
                          Power(Power(Plus(C1, Times(e, Sqr(x), Power(d, CN1))), FracPart(q)),
                              CN1)),
                      Integrate(
                          Times(
                              Power(x, m), Power(Plus(C1,
                                  Times(e, Sqr(x), Power(d, CN1))), q),
                              Plus(a, Times(b, Log(Times(c, Power(x, n)))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), IntegerQ(Times(C1D2, m)),
                      IntegerQ(Subtract(q,
                          C1D2)),
                      Not(Or(LtQ(Plus(m, Times(C2, q)), CN2), GtQ(d, C0)))))),
          IIntegrate(2342,
              Integrate(
                  Times(
                      Plus(a_DEFAULT,
                          Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                      Power(x_, m_DEFAULT), Power(Plus($p("d1"),
                          Times($p("e1", true), x_)), q_),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus($s("d1"), Times($s("e1"), x)), q),
                          Power(Plus($s("d2"), Times($s("e2"), x)), q),
                          Power(
                              Power(Plus(C1,
                                  Times($s("e1"), $s("e2"), Sqr(x),
                                      Power(Times($s("d1"), $s("d2")), CN1))),
                                  q),
                              CN1)),
                      Integrate(
                          Times(Power(x, m),
                              Power(
                                  Plus(C1,
                                      Times($s("e1"), $s("e2"), Sqr(x),
                                          Power(Times($s("d1"), $s("d2")), CN1))),
                                  q),
                              Plus(a, Times(b, Log(Times(c, Power(x, n)))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), n), x),
                      EqQ(Plus(Times($s("d2"), $s("e1")),
                          Times($s("d1"), $s("e2"))), C0),
                      IntegerQ(m), IntegerQ(Subtract(q, C1D2))))),
          IIntegrate(2343,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(
                          Log(Times(c_DEFAULT, Power(x_, n_))), b_DEFAULT)),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Power(x_, r_DEFAULT))), CN1)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(Plus(a, Times(b, Log(Times(c, x)))),
                                  Power(
                                      Times(x,
                                          Plus(d, Times(e, Power(x, Times(r, Power(n, CN1)))))),
                                      CN1)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, r), x), IntegerQ(Times(r, Power(n, CN1)))))),
          IIntegrate(2344,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(d, CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                                  Power(x, CN1)),
                              x),
                          x),
                      Dist(
                          Times(e, Power(d,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a,
                                      Times(b, Log(Times(c, Power(x, n))))), p),
                                  Power(Plus(d, Times(e, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(p, C0)))),
          IIntegrate(2345,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Power(x_, r_DEFAULT))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Log(Plus(C1, Times(d,
                                      Power(Times(e, Power(x, r)), CN1)))),
                                  Power(Plus(a,
                                      Times(b, Log(Times(c, Power(x, n))))), p),
                                  Power(Times(d, r), CN1)),
                              x)),
                      Dist(
                          Times(b, n, p, Power(Times(d, r),
                              CN1)),
                          Integrate(
                              Times(Log(Plus(C1, Times(d, Power(Times(e, Power(x, r)), CN1)))),
                                  Power(
                                      Plus(a, Times(b, Log(Times(c, Power(x, n))))), Subtract(p,
                                          C1)),
                                  Power(x, CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n, r), x), IGtQ(p, C0)))),
          IIntegrate(2346,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(d,
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(q, C1)),
                                  Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p), Power(x,
                                      CN1)),
                              x),
                          x),
                      Dist(e,
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), Subtract(q,
                                      C1)),
                                  Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(p, C0), GtQ(q, C0),
                      IntegerQ(Times(C2, q))))),
          IIntegrate(2347,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, x_)), q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(d, CN1),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Plus(q, C1)),
                              Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p), Power(x,
                                  CN1)),
                              x),
                          x),
                      Dist(
                          Times(e, Power(d,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e,
                                      x)), q),
                                  Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e,
                      n), x), IGtQ(p,
                          C0),
                      LtQ(q, CN1), IntegerQ(Times(C2, q))))),
          IIntegrate(2348,
              Integrate(Times(
                  Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                  Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Power(x_, r_DEFAULT))),
                      q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(Plus(d, Times(e, Power(x, r))), q), Power(x, CN1)),
                                  x))),
                      Subtract(
                          Simp(Times(u,
                              Plus(a, Times(b, Log(Times(c, Power(x, n)))))), x),
                          Dist(Times(b, n), Integrate(Dist(Power(x, CN1), u, x), x), x))),
                  And(FreeQ(List(a, b, c, d, e, n, r), x), IntegerQ(Subtract(q, C1D2))))),
          IIntegrate(2349,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Power(x_, r_DEFAULT))), q_)),
                  x_Symbol),
              Condition(Subtract(Dist(Power(d, CN1),
                  Integrate(
                      Times(Power(Plus(d, Times(e, Power(x, r))), Plus(q, C1)),
                          Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p), Power(x, CN1)),
                      x),
                  x),
                  Dist(
                      Times(e, Power(d,
                          CN1)),
                      Integrate(Times(Power(x, Subtract(r, C1)),
                          Power(Plus(d,
                              Times(e, Power(x, r))), q),
                          Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, n, r), x), IGtQ(p, C0), ILtQ(q, CN1)))),
          IIntegrate(2350,
              Integrate(Times(
                  Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                  Power(Times(f_DEFAULT, x_), m_DEFAULT), Power(Plus(d_,
                      Times(e_DEFAULT, Power(x_, r_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(
                              Times(Power(Times(f, x), m),
                                  Power(Plus(d, Times(e, Power(x, r))), q)),
                              x))),
                      Condition(
                          Subtract(Dist(Plus(a, Times(b, Log(Times(c, Power(x, n))))), u, x),
                              Dist(
                                  Times(b, n), Integrate(
                                      SimplifyIntegrand(Times(u, Power(x, CN1)), x), x),
                                  x)),
                          Or(And(Or(EqQ(r, C1), EqQ(r,
                              C2)), IntegerQ(
                                  m),
                              IntegerQ(Subtract(q, C1D2))), InverseFunctionFreeQ(u, x)))),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, q, r), x), IntegerQ(Times(C2,
                      q)), Or(And(IntegerQ(m), IntegerQ(r)),
                          IGtQ(q, C0))))),
          IIntegrate(2351,
              Integrate(
                  Times(
                      Plus(a_DEFAULT,
                          Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, r_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(Plus(a, Times(b, Log(Times(c, Power(x, n))))),
                                  Times(Power(Times(f, x), m),
                                      Power(Plus(d, Times(e, Power(x, r))), q)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, q, r), x), IntegerQ(q),
                      Or(GtQ(q, C0), And(IntegerQ(m), IntegerQ(r)))))),
          IIntegrate(2352,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, r_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(Dist(Power(n, CN1),
                  Subst(Integrate(
                      Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                          Power(Plus(d, Times(e, Power(x, Times(r, Power(n, CN1))))),
                              q),
                          Power(Plus(a, Times(b, Log(Times(c, x)))), p)),
                      x), x, Power(x, n)),
                  x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p, q, r), x), IntegerQ(q),
                      IntegerQ(Times(r, Power(n, CN1))),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1)))), Or(
                          GtQ(Times(Plus(m, C1), Power(n, CN1)), C0), IGtQ(p, C0))))),
          IIntegrate(2353,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, r_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          ExpandIntegrand(Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                              Times(Power(Times(f, x), m),
                                  Power(Plus(d, Times(e, Power(x, r))), q)),
                              x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q,
                      r), x), IntegerQ(q), Or(GtQ(q, C0),
                          And(IGtQ(p, C0), IntegerQ(m), IntegerQ(r)))))),
          IIntegrate(2354,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, r_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Times(f, x), m), Power(Plus(d,
                              Times(e, Power(x, r))), q),
                          Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, m, n, p, q, r), x))),
          IIntegrate(2355,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(u_, q_DEFAULT), Power(Times(f_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Times(f, x), m), Power(ExpandToSum(u,
                              x), q),
                          Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)),
                      x),
                  And(FreeQ(List(a, b, c, f, m, n, p,
                      q), x), BinomialQ(u,
                          x),
                      Not(BinomialMatchQ(u, x))))),
          IIntegrate(2356,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      $p("§polyx")),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              $s("§polyx"), Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, n, p), x), PolynomialQ($s("§polyx"), x)))),
          IIntegrate(2357,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      $p("§rfx")),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(
                                  Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                                  $s("§rfx"), x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, n), x), RationalFunctionQ($s("§rfx"), x), IGtQ(p, C0)))),
          IIntegrate(2358,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      $p("§rfx")),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(Times(
                                  $s("§rfx"), Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))),
                                      p)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, n), x), RationalFunctionQ($s("§rfx"), x), IGtQ(p, C0)))),
          IIntegrate(2359,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                          p_DEFAULT),
                      $p("§afx")),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times($s("§afx"),
                          Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)),
                      x),
                  And(FreeQ(List(a, b, c, n, p), x), AlgebraicFunctionQ($s("§afx"), x, True)))),
          IIntegrate(2360,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), e_DEFAULT), d_),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times(Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p),
                          Power(Plus(d, Times(e, Log(Times(c, Power(x, n))))), q)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), IntegerQ(p), IntegerQ(q)))));
}
