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
class IntRules26 {
  public static IAST RULES =
      List(
          IIntegrate(521,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT,
                          Power(x_, n_))), q_DEFAULT),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c,
                                  Times(d, Power(x, n))), q),
                              Power(Plus(e, Times(f, Power(x, n))), r)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), IGtQ(p, C0), IGtQ(q, C0), IGtQ(r, C0)))),
          IIntegrate(522,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), CN1),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), CN1),
                      Plus(e_, Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(
                              Subtract(Times(b, e), Times(a, f)), Power(
                                  Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(Power(Plus(a, Times(b, Power(x, n))), CN1), x), x),
                      Dist(
                          Times(
                              Subtract(Times(d, e), Times(c, f)), Power(
                                  Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(Power(Plus(c, Times(d, Power(x, n))), CN1), x), x)),
                  FreeQ(List(a, b, c, d, e, f, n), x))),
          IIntegrate(523,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, n_))), CN1),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), CN1D2),
                      Plus(e_, Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(f, Power(b, CN1)),
                          Integrate(Power(Plus(c, Times(d, Power(x, n))), CN1D2), x), x),
                      Dist(Times(Subtract(Times(b, e), Times(a, f)), Power(b, CN1)),
                          Integrate(Power(Times(Plus(a, Times(b, Power(x, n))),
                              Sqrt(Plus(c, Times(d, Power(x, n))))), CN1), x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f, n), x))),
          IIntegrate(524,
              Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), CN1D2),
                  Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), CN1D2),
                  Plus(e_, Times(f_DEFAULT, Power(x_, n_)))), x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(f, Power(b, CN1)),
                          Integrate(Times(Sqrt(Plus(a, Times(b, Power(x, n)))),
                              Power(Plus(c, Times(d, Power(x, n))), CN1D2)), x),
                          x),
                      Dist(Times(Subtract(Times(b, e), Times(a, f)), Power(b, CN1)),
                          Integrate(Power(Times(Sqrt(Plus(a, Times(b, Power(x, n)))),
                              Sqrt(Plus(c, Times(d, Power(x, n))))), CN1), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x),
                      Not(And(EqQ(n, C2),
                          Or(And(PosQ(Times(b, Power(a, CN1))), PosQ(Times(d, Power(c, CN1)))),
                              And(NegQ(Times(b, Power(a, CN1))),
                                  Or(PosQ(Times(d, Power(c, CN1))),
                                      And(GtQ(a, C0), Or(Not(GtQ(c, C0)),
                                          SimplerSqrtQ(Times(CN1, b, Power(a, CN1)),
                                              Times(CN1, d, Power(c, CN1))))))))))))),
          IIntegrate(525,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(
                          x_))), CN1D2),
                      Power(Plus(c_, Times(d_DEFAULT,
                          Sqr(x_))), QQ(-3L,
                              2L)),
                      Plus(e_, Times(f_DEFAULT, Sqr(x_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(
                              Subtract(Times(b, e), Times(a, f)), Power(
                                  Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Power(
                                  Times(Sqrt(Plus(a, Times(b, Sqr(x)))),
                                      Sqrt(Plus(c, Times(d, Sqr(x))))),
                                  CN1),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(d, e), Times(c, f)),
                              Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(Sqrt(Plus(a, Times(b, Sqr(x)))),
                                  Power(Plus(c, Times(d, Sqr(x))), QQ(-3L, 2L))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), PosQ(Times(b,
                      Power(a, CN1))), PosQ(
                          Times(d, Power(c, CN1)))))),
          IIntegrate(526,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Plus(e_, Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(Subtract(Times(b, e), Times(a, f)), x,
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(Plus(c, Times(d, Power(x, n))),
                                  q),
                              Power(Times(a, b, n, Plus(p, C1)), CN1)), x)),
                      Dist(
                          Power(Times(a, b, n,
                              Plus(p, C1)), CN1),
                          Integrate(Times(Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(Plus(c, Times(d, Power(x, n))), Subtract(q, C1)),
                              Simp(
                                  Plus(
                                      Times(c,
                                          Subtract(Plus(Times(b, e, n, Plus(p, C1)), Times(b, e)),
                                              Times(a, f))),
                                      Times(d,
                                          Plus(Times(b, e, n, Plus(p, C1)),
                                              Times(Subtract(Times(b, e), Times(a, f)),
                                                  Plus(Times(n, q), C1))),
                                          Power(x, n))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), LtQ(p, CN1), GtQ(q, C0)))),
          IIntegrate(527,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT), Plus(e_,
                          Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(b, e), Times(a, f)), x,
                                  Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                  Power(Plus(c, Times(d, Power(x, n))), Plus(q,
                                      C1)),
                                  Power(
                                      Times(a, n, Subtract(Times(b, c), Times(a,
                                          d)), Plus(p,
                                              C1)),
                                      CN1)),
                              x)),
                      Dist(
                          Power(Times(a, n, Subtract(Times(b, c), Times(a, d)),
                              Plus(p, C1)), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                  Power(Plus(c,
                                      Times(d, Power(x, n))), q),
                                  Simp(
                                      Plus(Times(c, Subtract(Times(b, e), Times(a, f))),
                                          Times(
                                              e, n, Subtract(Times(b, c),
                                                  Times(a, d)),
                                              Plus(p, C1)),
                                          Times(d, Subtract(Times(b, e), Times(a, f)),
                                              Plus(Times(n, Plus(p, q, C2)), C1), Power(x, n))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n, q), x), LtQ(p, CN1)))),
          IIntegrate(528,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Plus(e_, Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              f, x, Power(Plus(a, Times(b, Power(x,
                                  n))), Plus(p, C1)),
                              Power(Plus(c, Times(d, Power(x, n))), q), Power(
                                  Times(b, Plus(Times(n, Plus(p, q, C1)), C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(b,
                              Plus(Times(n, Plus(p, q, C1)), C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Power(x,
                                      n))), p),
                                  Power(Plus(c, Times(d, Power(x, n))), Subtract(q, C1)),
                                  Simp(
                                      Plus(
                                          Times(c,
                                              Plus(Times(b, e), Times(CN1, a, f),
                                                  Times(b, e, n, Plus(p, q, C1)))),
                                          Times(Plus(Times(d, Subtract(Times(b, e), Times(a, f))),
                                              Times(f, n, q, Subtract(Times(b, c), Times(a, d))),
                                              Times(b, d, e, n, Plus(p, q, C1))), Power(x, n))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n,
                      p), x), GtQ(q,
                          C0),
                      NeQ(Plus(Times(n, Plus(p, q, C1)), C1), C0)))),
          IIntegrate(529,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, C4))), QQ(-3L,
                              4L)),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, C4))), CN1), Plus(e_,
                          Times(f_DEFAULT, Power(x_, C4)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(Subtract(Times(b, e),
                          Times(a, f)), Power(Subtract(Times(b, c), Times(a, d)), CN1)), Integrate(
                              Power(Plus(a, Times(b, Power(x, C4))), QQ(-3L, 4L)), x),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(d, e), Times(c, f)), Power(
                                  Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a,
                                      Times(b, Power(x, C4))), C1D4),
                                  Power(Plus(c, Times(d, Power(x, C4))), CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f), x))),
          IIntegrate(530,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), CN1), Plus(e_,
                          Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(f, Power(d,
                              CN1)),
                          Integrate(Power(Plus(a, Times(b, Power(x, n))), p), x), x),
                      Dist(Times(Subtract(Times(d, e), Times(c, f)), Power(d, CN1)),
                          Integrate(Times(Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))), CN1)), x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f, p, n), x))),
          IIntegrate(531,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT), Plus(e_,
                          Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(e,
                          Integrate(Times(Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))), q)), x),
                          x),
                      Dist(f,
                          Integrate(
                              Times(
                                  Power(x, n), Power(Plus(a,
                                      Times(b, Power(x, n))), p),
                                  Power(Plus(c, Times(d, Power(x, n))), q)),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f, n, p, q), x))),
          IIntegrate(532,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(
                          x_))), CN1),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Sqr(x_))), CN1),
                      Power(Plus(e_, Times(f_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Power(
                                  Times(Plus(a, Times(b, Sqr(x))), Sqrt(Plus(e, Times(f, Sqr(x))))),
                                  CN1),
                              x),
                          x),
                      Dist(
                          Times(d, Power(Subtract(Times(b, c), Times(a, d)),
                              CN1)),
                          Integrate(
                              Power(
                                  Times(Plus(c, Times(d, Sqr(x))), Sqrt(Plus(e, Times(f, Sqr(x))))),
                                  CN1),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f), x))),
          IIntegrate(533,
              Integrate(
                  Times(
                      Power(x_, CN2), Power(Plus(c_,
                          Times(d_DEFAULT, Sqr(x_))), CN1),
                      Power(Plus(e_, Times(f_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(c, CN1),
                          Integrate(Power(Times(Sqr(x), Sqrt(Plus(e, Times(f, Sqr(x))))), CN1),
                              x),
                          x),
                      Dist(
                          Times(d, Power(c,
                              CN1)),
                          Integrate(
                              Power(
                                  Times(Plus(c, Times(d,
                                      Sqr(x))), Sqrt(
                                          Plus(e, Times(f, Sqr(x))))),
                                  CN1),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e, f), x), NeQ(Subtract(Times(d, e), Times(c, f)), C0)))),
          IIntegrate(534,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT,
                          Sqr(x_))), CN1),
                      Sqrt(Plus(c_, Times(d_DEFAULT, Sqr(
                          x_)))),
                      Sqrt(Plus(e_, Times(f_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(d, Power(b,
                              CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(e,
                                      Times(f, Sqr(x)))),
                                  Power(Plus(c, Times(d, Sqr(x))), CN1D2)),
                              x),
                          x),
                      Dist(Times(Subtract(Times(b, c), Times(a, d)), Power(b, CN1)),
                          Integrate(
                              Times(Sqrt(Plus(e, Times(f, Sqr(x)))),
                                  Power(Times(Plus(a, Times(b, Sqr(x))),
                                      Sqrt(Plus(c, Times(d, Sqr(x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), GtQ(Times(d, Power(c, CN1)), C0),
                      GtQ(Times(f, Power(e, CN1)), C0), Not(SimplerSqrtQ(Times(d, Power(c, CN1)),
                          Times(f, Power(e, CN1))))))),
          IIntegrate(535,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT,
                          Sqr(x_))), CN1),
                      Sqrt(Plus(c_,
                          Times(d_DEFAULT, Sqr(x_)))),
                      Sqrt(Plus(e_, Times(f_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(d, Power(b,
                              CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(e,
                                      Times(f, Sqr(x)))),
                                  Power(Plus(c, Times(d, Sqr(x))), CN1D2)),
                              x),
                          x),
                      Dist(Times(Subtract(Times(b, c), Times(a, d)), Power(b, CN1)),
                          Integrate(
                              Times(Sqrt(Plus(e, Times(f, Sqr(x)))),
                                  Power(Times(Plus(a, Times(b, Sqr(x))),
                                      Sqrt(Plus(c, Times(d, Sqr(x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), Not(
                          SimplerSqrtQ(Times(CN1, f, Power(e,
                              CN1)), Times(CN1, d,
                                  Power(c, CN1))))))),
          IIntegrate(536,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1D2), Power(
                          Plus(e_, Times(f_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(
                              Times(f, Power(Subtract(Times(b, e), Times(a, f)),
                                  CN1)),
                              Integrate(
                                  Power(
                                      Times(
                                          Sqrt(Plus(c,
                                              Times(d, Sqr(x)))),
                                          Sqrt(Plus(e, Times(f, Sqr(x))))),
                                      CN1),
                                  x),
                              x)),
                      Dist(Times(b, Power(Subtract(Times(b, e), Times(a, f)), CN1)),
                          Integrate(
                              Times(Sqrt(Plus(e, Times(f, Sqr(x)))),
                                  Power(Times(Plus(a, Times(b, Sqr(x))),
                                      Sqrt(Plus(c, Times(d, Sqr(x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), GtQ(Times(d, Power(c, CN1)), C0),
                      GtQ(Times(f, Power(e,
                          CN1)), C0),
                      Not(SimplerSqrtQ(Times(d, Power(c, CN1)), Times(f, Power(e, CN1))))))),
          IIntegrate(537,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Sqr(x_))), CN1D2),
                      Power(Plus(e_, Times(f_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(C1,
                          EllipticPi(Times(b, c, Power(Times(a, d), CN1)),
                              ArcSin(Times(Rt(Times(CN1, d, Power(c, CN1)), C2), x)), Times(c, f,
                                  Power(Times(d, e), CN1))),
                          Power(Times(a, Sqrt(c), Sqrt(e), Rt(Times(CN1, d, Power(c, CN1)), C2)),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), Not(GtQ(Times(d, Power(c, CN1)), C0)),
                      GtQ(c, C0), GtQ(e, C0), Not(
                          And(Not(GtQ(Times(f, Power(e, CN1)), C0)),
                              SimplerSqrtQ(Times(CN1, f, Power(e,
                                  CN1)), Times(CN1, d,
                                      Power(c, CN1)))))))),
          IIntegrate(538,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(
                          x_))), CN1),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1D2), Power(
                          Plus(e_, Times(f_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Plus(C1, Times(d, Sqr(x), Power(c, CN1)))), Power(
                              Plus(c, Times(d, Sqr(x))), CN1D2)),
                      Integrate(
                          Power(Times(Plus(a, Times(b, Sqr(x))),
                              Sqrt(Plus(C1, Times(d, Sqr(x), Power(c, CN1)))), Sqrt(
                                  Plus(e, Times(f, Sqr(x))))),
                              CN1),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), Not(GtQ(c, C0))))),
          IIntegrate(539, Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1),
              Sqrt(Plus(c_, Times(d_DEFAULT, Sqr(x_)))), Power(Plus(e_, Times(f_DEFAULT, Sqr(x_))),
                  CN1D2)),
              x_Symbol),
              Condition(
                  Simp(
                      Times(
                          c, Sqrt(Plus(e,
                              Times(f, Sqr(x)))),
                          EllipticPi(Subtract(C1, Times(b, c, Power(Times(a, d), CN1))),
                              ArcTan(Times(Rt(Times(d, Power(c, CN1)), C2), x)), Subtract(C1,
                                  Times(c, f, Power(Times(d, e), CN1)))),
                          Power(Times(a, e, Rt(Times(d, Power(c, CN1)), C2),
                              Sqrt(Plus(c, Times(d, Sqr(x)))), Sqrt(Times(c,
                                  Plus(e, Times(f, Sqr(x))), Power(
                                      Times(e, Plus(c, Times(d, Sqr(x)))), CN1)))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), PosQ(Times(d, Power(c, CN1)))))),
          IIntegrate(540,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(
                          x_))), CN1),
                      Sqrt(Plus(c_,
                          Times(d_DEFAULT, Sqr(x_)))),
                      Power(Plus(e_, Times(f_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(d, Power(b, CN1)), Integrate(Power(
                          Times(Sqrt(Plus(c, Times(d, Sqr(x)))), Sqrt(Plus(e, Times(f, Sqr(x))))),
                          CN1), x), x),
                      Dist(Times(Subtract(Times(b, c), Times(a, d)), Power(b, CN1)),
                          Integrate(Power(Times(Plus(a, Times(b, Sqr(x))),
                              Sqrt(Plus(c, Times(d, Sqr(x)))), Sqrt(Plus(e, Times(f, Sqr(x))))),
                              CN1), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NegQ(Times(d, Power(c, CN1)))))));
}
