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
class IntRules70 {
  public static IAST RULES =
      List(
          IIntegrate(1401,
              Integrate(
                  Times(
                      Plus(d_, Times(e_DEFAULT,
                          Power(x_, n_))),
                      Power(
                          Plus(Times(b_DEFAULT, Power(x_, n_)),
                              Times(c_DEFAULT, Power(x_, $p("n2")))),
                          p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              e, Power(x, Plus(Negate(n),
                                  C1)),
                              Power(
                                  Plus(Times(b, Power(x,
                                      n)), Times(c,
                                          Power(x, Times(C2, n)))),
                                  Plus(p, C1)),
                              Power(Times(c, Plus(Times(n, Plus(Times(C2, p), C1)), C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(
                                  Times(b, e, Plus(Times(n, p), C1)), Times(c, d,
                                      Plus(Times(n, Plus(Times(C2, p), C1)), C1))),
                              Power(Times(c, Plus(Times(n, Plus(Times(C2, p), C1)), C1)), CN1)),
                          Integrate(
                              Power(Plus(Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                                  p),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d, e, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      Not(IntegerQ(p)), NeQ(Plus(Times(n, Plus(Times(C2, p), C1)),
                          C1), C0),
                      NeQ(Subtract(
                          Times(b, e, Plus(Times(n, p),
                              C1)),
                          Times(c, d, Plus(Times(n, Plus(Times(C2, p), C1)), C1))),
                          C0)))),
          IIntegrate(1402,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Power(Plus(Times(b_DEFAULT, Power(x_, n_)),
                          Times(c_DEFAULT, Power(x_, $p("n2")))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus(Times(b, Power(x, n)),
                                  Times(c, Power(x, Times(C2, n)))),
                              FracPart(p)),
                          Power(
                              Times(Power(x, Times(n, FracPart(p))), Power(
                                  Plus(b, Times(c, Power(x, n))), FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(x, Times(n, p)), Power(Plus(d,
                                  Times(e, Power(x, n))), q),
                              Power(Plus(b, Times(c, Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List(b, c, d, e, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      Not(IntegerQ(p))))),
          IIntegrate(1403,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, Power(x_, n_)), Times(c_DEFAULT,
                                  Power(x_, $p("n2")))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(d, Times(e, Power(x, n))), Plus(p,
                              q)),
                          Power(
                              Plus(Times(a, Power(d, CN1)),
                                  Times(c, Power(x, n), Power(e, CN1))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, q), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      IntegerQ(p)))),
          IIntegrate(1404,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, $p("n2")))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(d, Times(e, Power(x, n))), Plus(p,
                              q)),
                          Power(
                              Plus(Times(a, Power(d, CN1)),
                                  Times(c, Power(x, n), Power(e, CN1))),
                              p)),
                      x),
                  And(FreeQ(List(a, c, d, e, n, q), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(1405,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, Power(x_, n_))), q_),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, Power(x_,
                                  n_)),
                              Times(c_DEFAULT, Power(x_, $p("n2")))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                          FracPart(p)),
                          Power(
                              Times(Power(Plus(d, Times(e, Power(x, n))), FracPart(p)),
                                  Power(Plus(Times(a, Power(d, CN1)),
                                      Times(c, Power(x, n), Power(e, CN1))), FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, Power(x, n))), Plus(p, q)),
                              Power(
                                  Plus(Times(a, Power(d, CN1)),
                                      Times(c, Power(x, n), Power(e, CN1))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      EqQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      Not(IntegerQ(p))))),
          IIntegrate(1406,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, $p("n2")))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(c, Power(x, Times(C2, n)))), FracPart(p)),
                          Power(
                              Times(Power(Plus(d, Times(e, Power(x, n))), FracPart(p)),
                                  Power(
                                      Plus(Times(a, Power(d, CN1)),
                                          Times(c, Power(x, n), Power(e, CN1))),
                                      FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(d, Times(e, Power(x, n))), Plus(p,
                                  q)),
                              Power(
                                  Plus(Times(a, Power(d, CN1)),
                                      Times(c, Power(x, n), Power(e, CN1))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, n, p, q), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                          C0),
                      Not(IntegerQ(p))))),
          IIntegrate(1407,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Plus(a_, Times(b_DEFAULT, Power(x_, n_)),
                          Times(c_DEFAULT, Power(x_, $p("n2"))))),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(
                              Plus(d, Times(e, Power(x, n))), q),
                              Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                          C0),
                      IGtQ(q, C0)))),
          IIntegrate(1408,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT), Plus(a_,
                          Times(c_DEFAULT, Power(x_, $p("n2"))))),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(d, Times(e, Power(x, n))), q), Plus(a,
                                  Times(c, Power(x, Times(C2, n))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, n), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), IGtQ(q, C0)))),
          IIntegrate(1409,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, Power(x_, n_))), q_),
                      Plus(
                          a_, Times(b_DEFAULT, Power(x_, n_)), Times(c_DEFAULT,
                              Power(x_, $p("n2"))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                                  x, Power(Plus(d, Times(e,
                                      Power(x, n))), Plus(q,
                                          C1)),
                                  Power(Times(d, Sqr(e), n, Plus(q, C1)), CN1)),
                              x)),
                      Dist(Power(Times(n, Plus(q, C1), d, Sqr(e)), CN1),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Power(x, n))), Plus(q, C1)),
                                  Simp(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                                      Times(a, Sqr(e), Plus(Times(n, Plus(q, C1)), C1)), Times(c, d,
                                          e, n, Plus(q, C1), Power(x, n))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0), LtQ(q,
                          CN1)))),
          IIntegrate(1410,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, Power(x_, n_))), q_),
                      Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2"))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), x,
                                  Power(Plus(d, Times(e, Power(x, n))), Plus(q,
                                      C1)),
                                  Power(Times(d, Sqr(e), n, Plus(q, C1)), CN1)),
                              x)),
                      Dist(Power(Times(n, Plus(q, C1), d, Sqr(e)), CN1),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Power(x, n))), Plus(q, C1)),
                                  Simp(Plus(Times(c, Sqr(d)),
                                      Times(a, Sqr(e), Plus(Times(n, Plus(q, C1)), C1)),
                                      Times(c, d, e, n, Plus(q, C1), Power(x, n))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, n), x), EqQ($s("n2"), Times(C2,
                      n)), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                          C0),
                      LtQ(q, CN1)))),
          IIntegrate(1411,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, Power(x_, n_))), q_),
                      Plus(
                          a_, Times(b_DEFAULT, Power(x_,
                              n_)),
                          Times(c_DEFAULT, Power(x_, $p("n2"))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(c, Power(x, Plus(n, C1)),
                              Power(Plus(d, Times(e,
                                  Power(x, n))), Plus(q,
                                      C1)),
                              Power(Times(e, Plus(Times(n, Plus(q, C2)), C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(e,
                              Plus(Times(n, Plus(q, C2)), C1)), CN1),
                          Integrate(Times(Power(Plus(d, Times(e, Power(x, n))), q),
                              Subtract(Times(a, e, Plus(Times(n, Plus(q, C2)), C1)),
                                  Times(
                                      Subtract(Times(c, d, Plus(n, C1)),
                                          Times(b, e, Plus(Times(n, Plus(q, C2)), C1))),
                                      Power(x, n)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n, q), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0)))),
          IIntegrate(1412,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, Power(x_, n_))), q_),
                      Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2"))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(c, Power(x, Plus(n, C1)),
                              Power(Plus(d, Times(e, Power(x, n))), Plus(q, C1)), Power(
                                  Times(e, Plus(Times(n, Plus(q, C2)), C1)), CN1)),
                          x),
                      Dist(Power(Times(e, Plus(Times(n, Plus(q, C2)), C1)), CN1),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Power(x, n))), q),
                                  Subtract(Times(a, e, Plus(Times(n, Plus(q, C2)), C1)),
                                      Times(c, d, Plus(n, C1), Power(x, n)))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, n, q), x), EqQ($s("n2"), Times(C2,
                      n)), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                          C0)))),
          IIntegrate(1413,
              Integrate(
                  Times(
                      Plus(d_, Times(e_DEFAULT,
                          Power(x_, n_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2")))), CN1)),
                  x_Symbol),
              Condition(
                  With(List(Set(q, Rt(Times(C2, d, e), C2))),
                      Plus(
                          Dist(Times(Sqr(e), Power(Times(C2, c), CN1)),
                              Integrate(Power(Plus(d, Times(q, Power(x, Times(C1D2, n))),
                                  Times(e, Power(x, n))), CN1), x),
                              x),
                          Dist(Times(Sqr(e), Power(Times(C2, c), CN1)),
                              Integrate(
                                  Power(Plus(d, Times(CN1, q, Power(x, Times(C1D2, n))),
                                      Times(e, Power(x, n))), CN1),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Subtract(Times(c, Sqr(d)),
                          Times(a, Sqr(e))), C0),
                      IGtQ(Times(C1D2, n), C0), PosQ(Times(d, e))))),
          IIntegrate(1414,
              Integrate(
                  Times(
                      Plus(d_, Times(e_DEFAULT,
                          Power(x_, n_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2")))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(CN2, d, e), C2))),
                      Plus(
                          Dist(
                              Times(d, Power(Times(C2, a),
                                  CN1)),
                              Integrate(
                                  Times(
                                      Subtract(d, Times(q,
                                          Power(x, Times(C1D2, n)))),
                                      Power(
                                          Subtract(Subtract(d, Times(q,
                                              Power(x, Times(C1D2, n)))), Times(e,
                                                  Power(x, n))),
                                          CN1)),
                                  x),
                              x),
                          Dist(Times(d, Power(Times(C2, a), CN1)),
                              Integrate(Times(Plus(d, Times(q, Power(x, Times(C1D2, n)))),
                                  Power(Subtract(Plus(d, Times(q, Power(x, Times(C1D2, n)))),
                                      Times(e, Power(x, n))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Subtract(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      IGtQ(Times(C1D2, n), C0), NegQ(Times(d, e))))),
          IIntegrate(1415,
              Integrate(Times(
                  Plus(d_, Times(e_DEFAULT, Power(x_, n_))),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2")))), CN1)), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(a, Power(c, CN1)), C4))),
                      Plus(
                          Dist(
                              Power(Times(C2, CSqrt2, c,
                                  Power(q, C3)), CN1),
                              Integrate(
                                  Times(
                                      Subtract(Times(CSqrt2, d, q),
                                          Times(
                                              Subtract(d, Times(e, Sqr(q))), Power(x,
                                                  Times(C1D2, n)))),
                                      Power(
                                          Plus(Sqr(q),
                                              Times(CN1, CSqrt2, q, Power(x,
                                                  Times(C1D2, n))),
                                              Power(x, n)),
                                          CN1)),
                                  x),
                              x),
                          Dist(Power(Times(C2, CSqrt2, c, Power(q, C3)), CN1),
                              Integrate(Times(
                                  Plus(
                                      Times(CSqrt2, d, q),
                                      Times(Subtract(d, Times(e, Sqr(q))),
                                          Power(x, Times(C1D2, n)))),
                                  Power(
                                      Plus(
                                          Sqr(q), Times(CSqrt2, q, Power(x, Times(C1D2, n))),
                                          Power(x, n)),
                                      CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      IGtQ(Times(C1D2, n), C0), PosQ(Times(a, c))))),
          IIntegrate(1416,
              Integrate(Times(
                  Plus(d_, Times(e_DEFAULT, Power(x_, C3))),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C6))), CN1)), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(c, Power(a, CN1)), C6))),
                      Plus(
                          Dist(
                              Power(Times(C3, a,
                                  Sqr(q)), CN1),
                              Integrate(
                                  Times(
                                      Subtract(Times(Sqr(
                                          q), d), Times(e,
                                              x)),
                                      Power(Plus(C1, Times(Sqr(q), Sqr(x))), CN1)),
                                  x),
                              x),
                          Dist(
                              Power(Times(C6, a,
                                  Sqr(q)), CN1),
                              Integrate(
                                  Times(
                                      Subtract(
                                          Times(C2, Sqr(q), d),
                                          Times(Subtract(Times(CSqrt3, Power(q, C3), d), e), x)),
                                      Power(
                                          Plus(C1, Times(CN1, CSqrt3, q, x),
                                              Times(Sqr(q), Sqr(x))),
                                          CN1)),
                                  x),
                              x),
                          Dist(Power(Times(C6, a, Sqr(q)), CN1), Integrate(
                              Times(
                                  Plus(Times(C2, Sqr(q), d),
                                      Times(Plus(Times(CSqrt3, Power(q, C3), d), e), x)),
                                  Power(Plus(C1, Times(CSqrt3, q, x), Times(Sqr(q), Sqr(x))), CN1)),
                              x), x))),
                  And(FreeQ(List(a, c, d,
                      e), x), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                          C0),
                      PosQ(Times(c, Power(a, CN1)))))),
          IIntegrate(1417,
              Integrate(
                  Times(
                      Plus(d_, Times(e_DEFAULT,
                          Power(x_, n_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2")))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(CN1, a, Power(c, CN1)), C2))),
                      Plus(
                          Dist(Times(C1D2, Plus(d, Times(e, q))),
                              Integrate(Power(Plus(a, Times(c, q, Power(x, n))), CN1), x), x),
                          Dist(
                              Times(C1D2, Subtract(d, Times(e, q))), Integrate(
                                  Power(Subtract(a, Times(c, q, Power(x, n))), CN1), x),
                              x))),
                  And(FreeQ(List(a, c, d, e, n), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      NegQ(Times(a, c)), IntegerQ(n)))),
          IIntegrate(1418,
              Integrate(
                  Times(
                      Plus(d_, Times(e_DEFAULT,
                          Power(x_, n_))),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2")))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          d, Integrate(Power(Plus(a, Times(c, Power(x, Times(C2, n)))), CN1),
                              x),
                          x),
                      Dist(e,
                          Integrate(
                              Times(
                                  Power(x, n), Power(Plus(a, Times(c, Power(x, Times(C2, n)))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, n), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Plus(Times(c, Sqr(d)),
                          Times(a, Sqr(e))), C0),
                      Or(PosQ(Times(a, c)), Not(IntegerQ(n)))))),
          IIntegrate(1419,
              Integrate(
                  Times(
                      Plus(d_, Times(e_DEFAULT,
                          Power(x_, n_))),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, Power(x_,
                                  n_)),
                              Times(c_DEFAULT, Power(x_, $p("n2")))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(q,
                              Rt(Subtract(Times(C2, d, Power(e, CN1)), Times(b, Power(c, CN1))),
                                  C2))),
                      Plus(
                          Dist(Times(e, Power(Times(C2, c), CN1)),
                              Integrate(
                                  Power(
                                      Simp(
                                          Plus(Times(d, Power(e, CN1)),
                                              Times(q, Power(x, Times(C1D2, n))), Power(x, n)),
                                          x),
                                      CN1),
                                  x),
                              x),
                          Dist(Times(e, Power(Times(C2, c), CN1)),
                              Integrate(Power(
                                  Simp(Plus(Times(d, Power(e, CN1)),
                                      Times(CN1, q, Power(x, Times(C1D2, n))), Power(x, n)), x),
                                  CN1), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      IGtQ(Times(C1D2, n), C0),
                      Or(GtQ(Subtract(Times(C2, d, Power(e, CN1)), Times(b, Power(c, CN1))), C0),
                          And(Not(LtQ(
                              Subtract(Times(C2, d, Power(e, CN1)), Times(b, Power(c, CN1))), C0)),
                              EqQ(d, Times(e, Rt(Times(a, Power(c, CN1)), C2)))))))),
          IIntegrate(1420,
              Integrate(Times(Plus(d_, Times(e_DEFAULT, Power(x_, n_))),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_)),
                      Times(c_DEFAULT, Power(x_, $p("n2")))), CN1)),
                  x_Symbol),
              Condition(
                  With(List(Set(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))), Plus(
                      Dist(
                          Plus(Times(C1D2, e),
                              Times(Subtract(Times(C2, c, d), Times(b, e)),
                                  Power(Times(C2, q), CN1))),
                          Integrate(Power(
                              Plus(Times(C1D2, b), Times(CN1, C1D2, q), Times(c, Power(x, n))),
                              CN1), x),
                          x),
                      Dist(
                          Subtract(Times(C1D2, e),
                              Times(Subtract(Times(C2, c, d), Times(b, e)),
                                  Power(Times(C2, q), CN1))),
                          Integrate(Power(
                              Plus(Times(C1D2, b), Times(C1D2, q), Times(c, Power(x, n))), CN1), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      IGtQ(Times(C1D2, n), C0), GtQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))));
}
