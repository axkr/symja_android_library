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
class IntRules73 {
  public static IAST RULES =
      List(
          IIntegrate(1461,
              Integrate(
                  Times(Plus(A_, Times(B_DEFAULT, Power(x_, m_DEFAULT))),
                      Power(Plus(d_, Times(e_DEFAULT,
                          Power(x_, n_))), q_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2")))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(Dist(ASymbol,
                      Integrate(
                          Times(Power(Plus(d, Times(e, Power(x, n))), q),
                              Power(Plus(a, Times(c, Power(x, Times(C2, n)))), p)),
                          x),
                      x),
                      Dist(
                          BSymbol, Integrate(Times(Power(x, m),
                              Power(Plus(d, Times(e, Power(x, n))), q), Power(
                                  Plus(a, Times(c, Power(x, Times(C2, n)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, ASymbol, BSymbol, m, n, p, q), x),
                      EqQ($s("n2"), Times(C2, n)), EqQ(Plus(m, Negate(n), C1), C0)))),
          IIntegrate(1462,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Times(e_DEFAULT,
                          Power(x_, n_)), q_),
                      Power(
                          Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(f, m),
                          Power(Times(n, Power(e, Subtract(Times(Plus(m, C1), Power(n, CN1)), C1))),
                              CN1)),
                      Subst(
                          Integrate(Times(
                              Power(Times(e, x),
                                  Subtract(Plus(q, Times(Plus(m, C1), Power(n, CN1))), C1)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)), x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      Or(IntegerQ(m), GtQ(f,
                          C0)),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(1463,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Times(e_DEFAULT, Power(x_,
                          n_)), q_),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true)))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(f, m),
                          Power(Times(n, Power(e, Subtract(Times(Plus(m, C1), Power(n, CN1)), C1))),
                              CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Times(e, x),
                                      Subtract(Plus(q, Times(Plus(m, C1), Power(n, CN1))), C1)),
                                  Power(Plus(a, Times(c, Sqr(x))), p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, c, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      Or(IntegerQ(m), GtQ(f, C0)), IntegerQ(
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(1464,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Times(e_DEFAULT,
                          Power(x_, n_)), q_),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(f, m), Power(e, IntPart(q)),
                          Power(Times(e, Power(x, n)),
                              FracPart(q)),
                          Power(Power(x, Times(n, FracPart(q))), CN1)),
                      Integrate(
                          Times(Power(x, Plus(m, Times(n, q))),
                              Power(
                                  Plus(a, Times(b, Power(x, n)),
                                      Times(c, Power(x, Times(C2, n)))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      Or(IntegerQ(m), GtQ(f,
                          C0)),
                      Not(IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1)))))))),
          IIntegrate(1465,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Times(e_DEFAULT, Power(x_, n_)), q_), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(f, m), Power(e, IntPart(q)),
                          Power(Times(e, Power(x, n)),
                              FracPart(q)),
                          Power(Power(x, Times(n, FracPart(q))), CN1)),
                      Integrate(
                          Times(
                              Power(x, Plus(m,
                                  Times(n, q))),
                              Power(Plus(a, Times(c, Power(x, Times(C2, n)))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      Or(IntegerQ(m), GtQ(f,
                          C0)),
                      Not(IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1)))))))),
          IIntegrate(1466,
              Integrate(
                  Times(
                      Power(Times(f_, x_), m_DEFAULT), Power(Times(e_DEFAULT,
                          Power(x_, n_)), q_),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(f, IntPart(m)), Power(Times(f, x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(Times(Power(x, m), Power(Times(e, Power(x, n)), q),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, e, f, m, n, p,
                      q), x), EqQ($s("n2"),
                          Times(C2, n)),
                      Not(IntegerQ(m))))),
          IIntegrate(1467,
              Integrate(Times(Power(Times(f_, x_), m_DEFAULT),
                  Power(Times(e_DEFAULT, Power(x_,
                      n_)), q_),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true)))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(f, IntPart(m)), Power(Times(f, x),
                          FracPart(m)), Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(
                              Power(x, m), Power(Times(e, Power(x, n)), q), Power(
                                  Plus(a, Times(c, Power(x, Times(C2, n)))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      Not(IntegerQ(m))))),
          IIntegrate(1468,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_, $p("n2", true))), Times(b_DEFAULT,
                                  Power(x_, n_))),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(d,
                                      Times(e, x)), q),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Simplify(Plus(m, Negate(n), C1)), C0)))),
          IIntegrate(1469,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT,
                          Power(x_, $p("n2", true)))), p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(d,
                                      Times(e, x)), q),
                                  Power(Plus(a, Times(c, Sqr(x))), p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, c, d, e, m, n, p, q), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Simplify(Plus(m, Negate(n), C1)),
                          C0)))),
          IIntegrate(1470,
              Integrate(Times(Power(x_, m_DEFAULT),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true))), Times(b_DEFAULT,
                      Power(x_, n_))), p_DEFAULT),
                  Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(x, Plus(m, Times(n, Plus(Times(C2, p), q)))),
                          Power(Plus(e, Times(d, Power(Power(x, n), CN1))), q),
                          Power(
                              Plus(c, Times(b, Power(Power(x, n), CN1)),
                                  Times(a, Power(Power(x, Times(C2, n)), CN1))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), EqQ($s("n2"), Times(C2,
                      n)), IntegersQ(p,
                          q),
                      NegQ(n)))),
          IIntegrate(1471,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT,
                          Power(x_, $p("n2", true)))), p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(x, Plus(m, Times(n, Plus(Times(C2, p),
                              q)))),
                          Power(Plus(e, Times(d, Power(Power(x, n), CN1))), q),
                          Power(Plus(c, Times(a, Power(Power(x, Times(C2, n)), CN1))), p)),
                      x),
                  And(FreeQ(List(a, c, d, e, m, n), x), EqQ($s("n2"), Times(C2,
                      n)), IntegersQ(p,
                          q),
                      NegQ(n)))),
          IIntegrate(1472,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(Times(Power(x, Subtract(Times(Plus(m, C1), Power(n, CN1)), C1)),
                              Power(Plus(d, Times(e, x)), q),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)), x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      Not(IntegerQ(
                          p)),
                      IGtQ(m, C0), IGtQ(n, C0), IGtQ(Times(Plus(m, C1), Power(n, CN1)), C0)))),
          IIntegrate(1473,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(
                          Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true))), Times(b_DEFAULT,
                              Power(x_, n_))),
                          p_),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              FracPart(p)),
                          Power(
                              Times(Power(c, IntPart(p)),
                                  Power(Plus(Times(C1D2, b), Times(c, Power(x, n))),
                                      Times(C2, FracPart(p)))),
                              CN1)),
                      Integrate(Times(Power(Times(f, x), m),
                          Power(Plus(d, Times(e, Power(x, n))), q), Power(Plus(Times(C1D2, b),
                              Times(c, Power(x, n))), Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      Not(IntegerQ(p))))),
          IIntegrate(1474,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, $p("n2", true))), Times(b_DEFAULT,
                              Power(x_, n_))),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(Dist(Power(n, CN1),
                  Subst(
                      Integrate(
                          Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                              Power(Plus(d, Times(e, x)), q),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                          x),
                      x, Power(x, n)),
                  x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p, q), x), EqQ($s("n2"), Times(C2,
                      n)), IntegerQ(
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(1475,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT,
                          Power(x_, $p("n2", true)))), p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                                      C1)),
                                  Power(Plus(d, Times(e, x)), q), Power(Plus(a, Times(c, Sqr(x))),
                                      p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, c, d, e, m, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(1476,
              Integrate(
                  Times(
                      Power(Times(f_,
                          x_), m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(x_, n_))),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(f, IntPart(m)), Power(Times(f, x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m), Power(Plus(d, Times(e, Power(x, n))), q),
                              Power(
                                  Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2,
                      n)), IntegerQ(
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(1477,
              Integrate(
                  Times(Power(Times(f_, x_), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT,
                          Power(x_, $p("n2", true)))), p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(f, IntPart(m)), Power(Times(f, x),
                          FracPart(m)), Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m), Power(Plus(d, Times(e, Power(x, n))), q),
                              Power(Plus(a, Times(c, Power(x, Times(C2, n)))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(1478,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Power(x_, n_)),
                              Times(c_DEFAULT, Power(x_, $p("n2")))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Times(f, x), m),
                          Power(Plus(d, Times(e, Power(x, n))), Plus(q,
                              p)),
                          Power(
                              Plus(Times(a, Power(d, CN1)),
                                  Times(c, Power(x, n), Power(e, CN1))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, q), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(1479,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, $p("n2")))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Times(f, x), m),
                          Power(Plus(d, Times(e, Power(x, n))), Plus(q, p)),
                          Power(Plus(Times(a, Power(d, CN1)), Times(c, Power(x, n), Power(e, CN1))),
                              p)),
                      x),
                  And(FreeQ(List(a, c, d, e, f, q, m, n, q), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), IntegerQ(p)))),
          IIntegrate(1480,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_,
                          Times(e_DEFAULT, Power(x_, n_))), q_),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, Power(x_, n_)), Times(c_DEFAULT,
                                  Power(x_, $p("n2")))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              FracPart(p)),
                          Power(Times(Power(Plus(d, Times(e, Power(x, n))), FracPart(p)),
                              Power(Plus(Times(a, Power(d, CN1)),
                                  Times(c, Power(x, n), Power(e, CN1))), FracPart(p))),
                              CN1)),
                      Integrate(Times(Power(Times(f, x), m),
                          Power(Plus(d, Times(e, Power(x, n))), Plus(q, p)),
                          Power(Plus(Times(a, Power(d, CN1)), Times(c, Power(x, n), Power(e, CN1))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      Not(IntegerQ(p))))));
}
