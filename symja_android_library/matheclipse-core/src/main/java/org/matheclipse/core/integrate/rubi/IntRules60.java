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
class IntRules60 {
  public static IAST RULES = List(
      IIntegrate(1201,
          Integrate(
              Times(
                  Plus(d_, Times(e_DEFAULT,
                      Sqr(x_))),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Times(CN1, c, Power(a, CN1)), C2))),
                  Plus(
                      Dist(
                          Times(Subtract(Times(d,
                              q), e), Power(q,
                                  CN1)),
                          Integrate(Power(Plus(a, Times(c, Power(x, C4))), CN1D2), x), x),
                      Dist(Times(e, Power(q, CN1)),
                          Integrate(Times(Plus(C1, Times(q, Sqr(x))),
                              Power(Plus(a, Times(c, Power(x, C4))), CN1D2)), x),
                          x))),
              And(FreeQ(List(a, c, d, e), x), NegQ(Times(c,
                  Power(a, CN1))), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                      C0)))),
      IIntegrate(1202,
          Integrate(
              Times(
                  Plus(d_, Times(e_DEFAULT,
                      Sqr(x_))),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Dist(
                      Times(Sqrt(Plus(C1, Times(C2, c, Sqr(x), Power(Subtract(b, q), CN1)))),
                          Sqrt(Plus(C1, Times(C2, c, Sqr(x), Power(Plus(b, q), CN1)))), Power(
                              Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2)),
                      Integrate(
                          Times(Plus(d, Times(e, Sqr(x))),
                              Power(
                                  Times(
                                      Sqrt(Plus(C1,
                                          Times(C2, c, Sqr(x), Power(Subtract(b, q), CN1)))),
                                      Sqrt(Plus(C1, Times(C2, c, Sqr(x), Power(Plus(b, q), CN1))))),
                                  CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d,
                  e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                      C0),
                  NegQ(Times(c, Power(a, CN1)))))),
      IIntegrate(1203,
          Integrate(
              Times(
                  Plus(d_, Times(e_DEFAULT,
                      Sqr(x_))),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandIntegrand(
                      Times(Plus(d, Times(e, Sqr(x))),
                          Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p)),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), NeQ(
                  Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0)))),
      IIntegrate(1204,
          Integrate(
              Times(
                  Plus(d_, Times(e_DEFAULT, Sqr(x_))), Power(
                      Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandIntegrand(
                      Times(Plus(d, Times(e,
                          Sqr(x))), Power(Plus(a, Times(c, Power(x, C4))),
                              p)),
                      x),
                  x),
              And(FreeQ(List(a, c, d, e), x), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0)))),
      IIntegrate(1205,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(f,
                          Coeff(
                              PolynomialRemainder(
                                  Power(Plus(d, Times(e, Sqr(x))), q), Plus(a, Times(b, Sqr(x)),
                                      Times(c, Power(x, C4))),
                                  x),
                              x, C0)),
                      Set(g,
                          Coeff(
                              PolynomialRemainder(Power(Plus(d, Times(e, Sqr(x))), q),
                                  Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), x),
                              x, C2))),
                  Plus(
                      Simp(
                          Times(x,
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), Plus(p, C1)),
                              Subtract(Subtract(Times(a, b, g), Times(f,
                                  Subtract(Sqr(b), Times(C2, a, c)))), Times(c,
                                      Subtract(Times(b, f), Times(C2, a, g)), Sqr(x))),
                              Power(
                                  Times(C2, a, Plus(p, C1),
                                      Subtract(Sqr(b), Times(C4, a, c))),
                                  CN1)),
                          x),
                      Dist(
                          Power(Times(C2, a, Plus(p, C1),
                              Subtract(Sqr(b), Times(C4, a, c))), CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), Plus(p, C1)),
                              ExpandToSum(Plus(
                                  Times(C2, a, Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c)),
                                      PolynomialQuotient(Power(Plus(d, Times(e, Sqr(x))), q),
                                          Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), x)),
                                  Times(Sqr(b), f, Plus(Times(C2, p), C3)),
                                  Times(CN1, C2, a, c, f, Plus(Times(C4, p), C5)),
                                  Times(CN1, a, b, g),
                                  Times(c, Plus(Times(C4, p), C7),
                                      Subtract(Times(b, f), Times(C2, a, g)), Sqr(x))),
                                  x)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                      Times(a, Sqr(e))), C0),
                  IGtQ(q, C1), LtQ(p, CN1)))),
      IIntegrate(1206,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(Power(e, q), Power(x, Subtract(Times(C2, q), C3)),
                          Power(Plus(a, Times(b, Sqr(
                              x)), Times(c,
                                  Power(x, C4))),
                              Plus(p, C1)),
                          Power(Times(c, Plus(Times(C4, p), Times(C2, q), C1)), CN1)),
                      x),
                  Dist(
                      Power(Times(c,
                          Plus(Times(C4, p), Times(C2, q), C1)), CN1),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p),
                              ExpandToSum(Subtract(
                                  Subtract(
                                      Subtract(
                                          Times(c, Plus(Times(C4, p), Times(C2, q), C1),
                                              Power(Plus(d, Times(e, Sqr(x))), q)),
                                          Times(a, Subtract(Times(C2, q), C3), Power(e, q),
                                              Power(x, Subtract(Times(C2, q), C4)))),
                                      Times(b, Subtract(Plus(Times(C2, p), Times(C2, q)), C1),
                                          Power(e, q), Power(x, Subtract(Times(C2, q), C2)))),
                                  Times(c, Plus(Times(C4, p), Times(C2, q), C1), Power(e, q),
                                      Power(x, Times(C2, q)))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, p), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0), IGtQ(q,
                      C1)))),
      IIntegrate(1207,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), q_),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(Power(e, q), Power(x, Subtract(Times(C2, q), C3)),
                          Power(Plus(a, Times(c, Power(x,
                              C4))), Plus(p,
                                  C1)),
                          Power(Times(c, Plus(Times(C4, p), Times(C2, q), C1)), CN1)),
                      x),
                  Dist(
                      Power(Times(c,
                          Plus(Times(C4, p), Times(C2, q), C1)), CN1),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(c, Power(x, C4))), p), ExpandToSum(Subtract(
                                  Subtract(
                                      Times(c, Plus(Times(C4, p), Times(C2, q), C1),
                                          Power(Plus(d, Times(e, Sqr(x))), q)),
                                      Times(a, Subtract(Times(C2, q), C3), Power(e, q),
                                          Power(x, Subtract(Times(C2, q), C4)))),
                                  Times(c, Plus(Times(C4, p), Times(C2, q), C1), Power(e, q),
                                      Power(x, Times(C2, q)))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, c, d, e, p), x), NeQ(Plus(Times(c, Sqr(d)),
                  Times(a, Sqr(e))), C0), IGtQ(q,
                      C1)))),
      IIntegrate(1208,
          Integrate(
              Times(
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1), Power(Plus(a_,
                      Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      p_)),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Dist(Power(e, CN2),
                          Integrate(
                              Times(
                                  Subtract(Subtract(Times(c, d), Times(b, e)), Times(c, e, Sqr(x))),
                                  Power(
                                      Plus(a, Times(b, Sqr(x)),
                                          Times(c, Power(x, C4))),
                                      Subtract(p, C1))),
                              x),
                          x)),
                  Dist(
                      Times(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                          Power(e, CN2)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                              Subtract(p, C1)), Power(Plus(d, Times(e, Sqr(x))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NeQ(Plus(Times(c, Sqr(
                      d)), Times(CN1, b, d,
                          e),
                      Times(a, Sqr(e))), C0),
                  IGtQ(Plus(p, C1D2), C0)))),
      IIntegrate(1209,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(
              Plus(
                  Negate(Dist(Power(e, CN2),
                      Integrate(Times(Subtract(Times(c, d), Times(c, e, Sqr(x))),
                          Power(Plus(a, Times(c, Power(x, C4))), Subtract(p, C1))), x),
                      x)),
                  Dist(
                      Times(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), Power(e,
                          CN2)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(c, Power(x, C4))), Subtract(p, C1)), Power(
                                  Plus(d, Times(e, Sqr(x))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, c, d, e), x), NeQ(Plus(Times(c, Sqr(d)),
                  Times(a, Sqr(e))), C0), IGtQ(Plus(p, C1D2),
                      C0)))),
      IIntegrate(1210,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              Plus(Dist(Power(Times(C2, d), CN1),
                  Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2), x), x),
                  Dist(
                      Power(Times(C2, d), CN1), Integrate(Times(Subtract(d, Times(e, Sqr(x))),
                          Power(Times(Plus(d, Times(e, Sqr(x))),
                              Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                      Times(a, Sqr(e))), C0),
                  EqQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0)))),
      IIntegrate(1211,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              Plus(
                  Dist(
                      Power(Times(C2,
                          d), CN1),
                      Integrate(Power(Plus(a, Times(c, Power(x, C4))), CN1D2), x), x),
                  Dist(
                      Power(Times(C2,
                          d), CN1),
                      Integrate(
                          Times(Subtract(d, Times(e, Sqr(x))),
                              Power(
                                  Times(Plus(d, Times(e, Sqr(x))),
                                      Sqrt(Plus(a, Times(c, Power(x, C4))))),
                                  CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, c, d, e), x), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                  EqQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0)))),
      IIntegrate(1212,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Dist(Times(C2, Sqrt(Negate(c))),
                      Integrate(
                          Power(
                              Times(
                                  Plus(d, Times(e, Sqr(x))), Sqrt(Plus(b, q, Times(C2, c, Sqr(x)))),
                                  Sqrt(Subtract(Plus(Negate(b), q), Times(C2, c, Sqr(x))))),
                              CN1),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e), x), GtQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  LtQ(c, C0)))),
      IIntegrate(1213,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Times(CN1, a, c), C2))),
                  Dist(
                      Sqrt(Negate(
                          c)),
                      Integrate(
                          Power(Times(
                              Plus(d, Times(e, Sqr(x))), Sqrt(Plus(q, Times(c, Sqr(x)))), Sqrt(
                                  Subtract(q, Times(c, Sqr(x))))),
                              CN1),
                          x),
                      x)),
              And(FreeQ(List(a, c, d, e), x), GtQ(a, C0), LtQ(c, C0)))),
      IIntegrate(1214,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Subtract(
                      Dist(
                          Times(
                              C2, c, Power(Subtract(Times(C2, c, d), Times(e, Subtract(b, q))),
                                  CN1)),
                          Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2),
                              x),
                          x),
                      Dist(
                          Times(e, Power(Subtract(Times(C2, c, d), Times(e, Subtract(b, q))), CN1)),
                          Integrate(Times(Plus(b, Negate(q), Times(C2, c, Sqr(x))),
                              Power(
                                  Times(Plus(d, Times(e, Sqr(x))),
                                      Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                  CN1)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d, e), x), GtQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  Not(LtQ(c, C0))))),
      IIntegrate(1215,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Times(CN1, a, c), C2))),
                  Plus(
                      Dist(
                          Times(c, Power(Plus(Times(c, d), Times(e, q)),
                              CN1)),
                          Integrate(Power(Plus(a, Times(c, Power(x, C4))), CN1D2), x), x),
                      Dist(
                          Times(e, Power(Plus(Times(c, d), Times(e, q)),
                              CN1)),
                          Integrate(
                              Times(Subtract(q, Times(c, Sqr(x))),
                                  Power(Times(Plus(d, Times(e, Sqr(x))),
                                      Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1)),
                              x),
                          x))),
              And(FreeQ(List(a, c, d, e), x), GtQ(Times(CN1, a, c), C0), Not(LtQ(c, C0))))),
      IIntegrate(1216,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Times(c, Power(a, CN1)), C2))),
                  Subtract(
                      Dist(
                          Times(
                              Plus(Times(c, d), Times(a, e,
                                  q)),
                              Power(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                          Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2),
                              x),
                          x),
                      Dist(
                          Times(a, e, Plus(e, Times(d, q)),
                              Power(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                          Integrate(Times(Plus(C1, Times(q, Sqr(x))),
                              Power(
                                  Times(Plus(d, Times(e, Sqr(x))),
                                      Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                  CN1)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NeQ(Plus(Times(c, Sqr(
                      d)), Times(CN1, b, d,
                          e),
                      Times(a, Sqr(e))), C0),
                  NeQ(Subtract(Times(c, Sqr(
                      d)), Times(a,
                          Sqr(e))),
                      C0),
                  PosQ(Times(c, Power(a, CN1)))))),
      IIntegrate(1217,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Times(c, Power(a, CN1)), C2))),
                  Subtract(
                      Dist(
                          Times(
                              Plus(Times(c, d), Times(a, e, q)), Power(
                                  Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                          Integrate(Power(Plus(a, Times(c, Power(x, C4))), CN1D2), x), x),
                      Dist(
                          Times(a, e, Plus(e, Times(d, q)),
                              Power(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                          Integrate(Times(Plus(C1, Times(q, Sqr(x))),
                              Power(Times(Plus(d, Times(e, Sqr(x))),
                                  Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1)),
                              x),
                          x))),
              And(FreeQ(List(a, c, d, e), x), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                  NeQ(Subtract(Times(c, Sqr(d)),
                      Times(a, Sqr(e))), C0),
                  PosQ(Times(c, Power(a, CN1)))))),
      IIntegrate(1218,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Times(CN1, c, Power(a, CN1)), C4))),
                  Simp(
                      Times(C1,
                          EllipticPi(
                              Times(CN1, e, Power(Times(d, Sqr(q)), CN1)), ArcSin(
                                  Times(q, x)),
                              CN1),
                          Power(Times(d, Sqrt(a), q), CN1)),
                      x)),
              And(FreeQ(List(a, c, d, e), x), NegQ(Times(c, Power(a, CN1))), GtQ(a, C0)))),
      IIntegrate(1219,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              Dist(
                  Times(Sqrt(Plus(C1, Times(c, Power(x, C4), Power(a, CN1)))), Power(
                      Plus(a, Times(c, Power(x, C4))), CN1D2)),
                  Integrate(
                      Power(
                          Times(Plus(d, Times(e, Sqr(x))),
                              Sqrt(Plus(C1, Times(c, Power(x, C4), Power(a, CN1))))),
                          CN1),
                      x),
                  x),
              And(FreeQ(List(a, c, d, e), x), NegQ(Times(c, Power(a, CN1))), Not(GtQ(a, C0))))),
      IIntegrate(1220,
          Integrate(
              Times(
                  Power(Plus(d_,
                      Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(List(Set(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Dist(Times(Sqrt(Plus(C1, Times(C2, c, Sqr(x), Power(Subtract(b, q), CN1)))),
                      Sqrt(Plus(C1, Times(C2, c, Sqr(x), Power(Plus(b, q), CN1)))), Power(
                          Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2)),
                      Integrate(
                          Power(
                              Times(Plus(d, Times(e, Sqr(x))),
                                  Sqrt(Plus(C1, Times(C2, c, Sqr(x), Power(Subtract(b, q), CN1)))),
                                  Sqrt(Plus(C1, Times(C2, c, Sqr(x), Power(Plus(b, q), CN1))))),
                              CN1),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NegQ(Times(c, Power(a, CN1)))))));
}
