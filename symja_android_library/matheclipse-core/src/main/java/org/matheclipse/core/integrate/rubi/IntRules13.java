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
class IntRules13 {
  public static IAST RULES =
      List(
          IIntegrate(261,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Power(Plus(a, Times(b, Power(x, n))),
                              Plus(p, C1)),
                          Power(Times(b, n, Plus(p, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, m, n, p), x), EqQ(m, Subtract(n, C1)), NeQ(p, CN1)))),
          IIntegrate(262,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_DEFAULT))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_DEFAULT))), p_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Plus(p, C1)),
                          Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Plus(p,
                              C1)),
                          Power(Times(C2, $s("b1"), $s("b2"), n, Plus(p, C1)), CN1)),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), m, n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")),
                          Times($s("a1"), $s("b2"))), C0),
                      EqQ(m, Subtract(Times(C2, n), C1)), NeQ(p, CN1)))),
          IIntegrate(263,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(Integrate(Times(Power(x, Plus(m, Times(n, p))),
                  Power(Plus(b, Times(a, Power(Power(x, n), CN1))), p)), x), And(
                      FreeQ(List(a, b, m, n), x), IntegerQ(p), NegQ(n)))),
          IIntegrate(264,
              Integrate(
                  Times(
                      Power(Times(c_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Times(c, x), Plus(m, C1)),
                          Power(Plus(a, Times(b, Power(x, n))),
                              Plus(p, C1)),
                          Power(Times(a, c, Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, m, n, p),
                      x), EqQ(Plus(Times(Plus(m, C1), Power(n, CN1)), p, C1), C0),
                      NeQ(m, CN1)))),
          IIntegrate(265,
              Integrate(
                  Times(Power(Times(c_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Times(c, x), Plus(m, C1)),
                          Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Plus(p, C1)),
                          Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Plus(p,
                              C1)),
                          Power(Times($s("a1"), $s("a2"), c, Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"),
                          $s("b2"))), C0),
                      EqQ(Plus(Times(Plus(m, C1), Power(Times(C2, n), CN1)), p, C1), C0), NeQ(m,
                          CN1)))),
          IIntegrate(266,
              Integrate(Times(Power(x_, m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)), x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(Integrate(
                          Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                              Power(Plus(a, Times(b, x)), p)),
                          x), x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, m, n,
                      p), x), IntegerQ(
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(267,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true),
                          Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(Dist(Power(n, CN1),
                  Subst(
                      Integrate(
                          Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                              Power(Plus($s("a1"), Times($s("b1"), x)), p),
                              Power(Plus($s("a2"), Times($s("b2"), x)), p)),
                          x),
                      x, Power(x, n)),
                  x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), m, n, p), x),
                      EqQ(Plus(Times($s("a2"), $s(
                          "b1")), Times($s("a1"),
                              $s("b2"))),
                          C0),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(Times(C2, n), CN1))))))),
          IIntegrate(268,
              Integrate(
                  Times(
                      Power(Times(c_, x_), m_), Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(m)), Power(Times(c,
                              x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(Times(Power(x, m), Power(Plus(a, Times(b, Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, m, n,
                      p), x), IntegerQ(
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(269,
              Integrate(
                  Times(Power(Times(c_, x_), m_),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(Dist(
                  Times(Power(c, IntPart(m)), Power(Times(c, x), FracPart(m)),
                      Power(Power(x, FracPart(m)), CN1)),
                  Integrate(Times(Power(x, m),
                      Power(Plus($s("a1"), Times($s("b1"), Power(x, n))),
                          p),
                      Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), p)), x),
                  x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0), IntegerQ(
                          Simplify(Times(Plus(m, C1), Power(Times(C2, n), CN1))))))),
          IIntegrate(270,
              Integrate(
                  Times(
                      Power(Times(c_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Times(c, x), m), Power(Plus(a, Times(b, Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, m, n), x), IGtQ(p, C0)))),
          IIntegrate(271,
              Integrate(
                  Times(Power(x_, m_),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(x, Plus(m, C1)),
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(Times(a, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, Plus(m, Times(n, Plus(p, C1)), C1),
                          Power(Times(a, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(x, Plus(m, n)), Power(Plus(a, Times(b, Power(x, n))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, m, n, p), x),
                      ILtQ(Simplify(
                          Plus(Times(Plus(m, C1), Power(n, CN1)), p, C1)), C0),
                      NeQ(m, CN1)))),
          IIntegrate(272,
              Integrate(
                  Times(Power(x_, m_),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_), Power(Plus(
                          $p("a2"), Times($p("b2", true), Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Subtract(Simp(Times(Power(x, Plus(m, C1)),
                      Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Plus(p, C1)),
                      Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Plus(p, C1)), Power(
                          Times($s("a1"), $s("a2"), Plus(m, C1)), CN1)),
                      x),
                      Dist(
                          Times(
                              $s("b1"), $s("b2"), Plus(m, Times(C2, n, Plus(p, C1)), C1), Power(
                                  Times($s("a1"), $s("a2"), Plus(m, C1)), CN1)),
                          Integrate(Times(Power(x, Plus(m, Times(C2, n))),
                              Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), p),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), p)), x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), m, n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")),
                          Times($s("a1"), $s("b2"))), C0),
                      ILtQ(Simplify(
                          Plus(Times(Plus(m, C1), Power(Times(C2, n), CN1)), p, C1)), C0),
                      NeQ(m, CN1)))),
          IIntegrate(273,
              Integrate(
                  Times(
                      Power(Times(c_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(Times(Power(Times(c, x), Plus(m, C1)),
                          Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                          Power(Times(a, c, n, Plus(p, C1)), CN1)), x)),
                      Dist(
                          Times(
                              Plus(m, Times(n, Plus(p, C1)), C1),
                              Power(Times(a, n, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(c, x), m),
                                  Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, m, n, p), x),
                      ILtQ(Simplify(
                          Plus(Times(Plus(m, C1), Power(n, CN1)), p, C1)), C0),
                      NeQ(p, CN1)))),
          IIntegrate(274,
              Integrate(
                  Times(Power(Times(c_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Times(c, x), Plus(m, C1)),
                                  Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Plus(p, C1)),
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Plus(p, C1)),
                                  Power(Times(C2, $s("a1"), $s("a2"), c, n, Plus(p, C1)), CN1)),
                              x)),
                      Dist(Times(Plus(m, Times(C2, n, Plus(p, C1)), C1),
                          Power(Times(C2, $s("a1"), $s("a2"), n, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(c, x), m),
                                  Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Plus(p, C1)),
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")),
                          Times($s("a1"), $s("b2"))), C0),
                      ILtQ(Simplify(
                          Plus(Times(Plus(m, C1), Power(Times(C2, n), CN1)), p, C1)), C0),
                      NeQ(p, CN1)))),
          IIntegrate(275,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  With(List(Set(k, GCD(Plus(m, C1), n))),
                      Condition(Dist(Power(k, CN1),
                          Subst(Integrate(
                              Times(Power(x, Subtract(Times(Plus(m, C1), Power(k, CN1)), C1)),
                                  Power(Plus(a, Times(b, Power(x, Times(n, Power(k, CN1))))), p)),
                              x), x, Power(x, k)),
                          x), Unequal(k, C1))),
                  And(FreeQ(List(a, b, p), x), IGtQ(n, C0), IntegerQ(m)))),
          IIntegrate(276,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true),
                          Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(With(List(Set(k, GCD(Plus(m, C1), Times(C2, n)))), Condition(Dist(
                  Power(k, CN1),
                  Subst(Integrate(Times(Power(x, Subtract(Times(Plus(m, C1), Power(k, CN1)), C1)),
                      Power(Plus($s("a1"), Times($s("b1"), Power(x, Times(n, Power(k, CN1))))), p),
                      Power(Plus($s("a2"), Times($s("b2"), Power(x, Times(n, Power(k, CN1))))), p)),
                      x), x, Power(x, k)),
                  x), Unequal(k, C1))),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), p), x),
                      EqQ(Plus(Times($s("a2"), $s(
                          "b1")), Times($s("a1"),
                              $s("b2"))),
                          C0),
                      IGtQ(Times(C2, n), C0), IntegerQ(m)))),
          IIntegrate(277,
              Integrate(
                  Times(
                      Power(Times(c_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(Subtract(
                  Simp(Times(Power(Times(c, x), Plus(m, C1)),
                      Power(Plus(a, Times(b, Power(x, n))), p), Power(Times(c, Plus(m, C1)), CN1)),
                      x),
                  Dist(Times(b, n, p, Power(Times(Power(c, n), Plus(m, C1)), CN1)),
                      Integrate(Times(Power(Times(c, x), Plus(m, n)),
                          Power(Plus(a, Times(b, Power(x, n))), Subtract(p, C1))), x),
                      x)),
                  And(FreeQ(List(a, b, c), x), IGtQ(n, C0), GtQ(p, C0), LtQ(m, CN1),
                      Not(ILtQ(Times(Plus(m, Times(n, p), n, C1), Power(n, CN1)),
                          C0)),
                      IntBinomialQ(a, b, c, n, m, p, x)))),
          IIntegrate(278,
              Integrate(
                  Times(Power(Times(c_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true),
                          Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(c, x), Plus(m, C1)),
                              Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), p),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, n))),
                                  p),
                              Power(Times(c, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, $s("b1"), $s("b2"), n, p,
                              Power(Times(Power(c, Times(C2, n)), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(c, x), Plus(m, Times(C2, n))),
                                  Power(Plus($s("a1"), Times($s("b1"), Power(x, n))),
                                      Subtract(p, C1)),
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, n))),
                                      Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s(
                          "b2"))), C0),
                      IGtQ(Times(C2, n), C0), GtQ(p, C0), LtQ(m, CN1),
                      NeQ(Plus(m, Times(C2, n, p),
                          C1), C0),
                      IntBinomialQ(
                          Times($s("a1"), $s(
                              "a2")),
                          Times($s("b1"), $s("b2")), c, Times(C2, n), m, p, x)))),
          IIntegrate(279, Integrate(Times(Power(Times(c_DEFAULT, x_), m_DEFAULT),
              Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)), x_Symbol), Condition(
                  Plus(
                      Simp(Times(Power(Times(c, x), Plus(m, C1)),
                          Power(Plus(a, Times(b, Power(x, n))), p),
                          Power(Times(c, Plus(m, Times(n, p), C1)), CN1)), x),
                      Dist(Times(a, n, p, Power(Plus(m, Times(n, p), C1), CN1)),
                          Integrate(Times(Power(Times(c, x), m),
                              Power(Plus(a, Times(b, Power(x, n))), Subtract(p, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, m), x), IGtQ(n, C0), GtQ(p,
                      C0), NeQ(Plus(m, Times(n, p), C1), C0),
                      IntBinomialQ(a, b, c, n, m, p, x)))),
          IIntegrate(280,
              Integrate(
                  Times(Power(Times(c_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true),
                          Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(Times(c, x), Plus(m, C1)),
                          Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), p),
                          Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), p),
                          Power(Times(c, Plus(m, Times(C2, n, p), C1)), CN1)), x),
                      Dist(
                          Times(C2, $s("a1"), $s("a2"), n, p,
                              Power(Plus(m, Times(C2, n, p), C1), CN1)),
                          Integrate(Times(Power(Times(c, x), m),
                              Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Subtract(p, C1)),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0),
                      IGtQ(Times(C2, n), C0), GtQ(p, C0), NeQ(Plus(m, Times(C2, n, p), C1), C0),
                      IntBinomialQ(Times($s("a1"), $s("a2")), Times($s("b1"), $s("b2")), c,
                          Times(C2, n), m, p, x)))));
}
