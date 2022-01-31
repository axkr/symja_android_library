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
class IntRules12 {
  public static IAST RULES =
      List(
          IIntegrate(
              241, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  p_), x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(a, Power(Plus(a, Times(b, Power(x, n))), CN1)), Plus(p,
                              Power(n, CN1))),
                          Power(Plus(a, Times(b, Power(x, n))), Plus(p, Power(n, CN1)))),
                      Subst(
                          Integrate(Power(Power(Subtract(C1, Times(b, Power(x, n))),
                              Plus(p, Power(n, CN1), C1)), CN1), x),
                          x,
                          Times(x, Power(Power(Plus(a, Times(b,
                              Power(x, n))), Power(n,
                                  CN1)),
                              CN1))),
                      x),
                  And(FreeQ(List(a, b), x), IGtQ(n, C0), LtQ(CN1, p, C0), NeQ(p, Negate(C1D2)),
                      LtQ(Denominator(Plus(p, Power(n, CN1))), Denominator(p))))),
          IIntegrate(
              242, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  p_), x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, Times(b, Power(Power(x, n), CN1))), p),
                                  Power(x, CN2)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, p), x), ILtQ(n, C0)))),
          IIntegrate(243, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_), x_Symbol),
              Condition(With(List(Set(k, Denominator(n))),
                  Dist(k,
                      Subst(
                          Integrate(Times(Power(x, Subtract(k, C1)),
                              Power(Plus(a, Times(b, Power(x, Times(k, n)))), p)), x),
                          x, Power(x, Power(k, CN1))),
                      x)),
                  And(FreeQ(List(a, b, p), x), FractionQ(n)))),
          IIntegrate(244, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_), x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Power(Plus(a, Times(b, Power(x, n))), p), x),
                      x),
                  And(FreeQ(List(a, b, n), x), IGtQ(p, C0)))),
          IIntegrate(
              245, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  p_), x_Symbol),
              Condition(
                  Simp(
                      Times(Power(a, p), x,
                          Hypergeometric2F1(
                              Negate(p), Power(n, CN1), Plus(Power(n,
                                  CN1), C1),
                              Times(CN1, b, Power(x, n), Power(a, CN1)))),
                      x),
                  And(FreeQ(List(a, b, n, p), x), Not(IGtQ(p, C0)), Not(IntegerQ(Power(n, CN1))),
                      Not(ILtQ(Simplify(Plus(Power(n, CN1), p)),
                          C0)),
                      Or(IntegerQ(p), GtQ(a, C0))))),
          IIntegrate(
              246, Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  p_), x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, IntPart(p)),
                          Power(Plus(a, Times(b, Power(x, n))), FracPart(p)),
                          Power(
                              Power(Plus(C1, Times(b, Power(x, n), Power(a, CN1))),
                                  FracPart(p)),
                              CN1)),
                      Integrate(Power(Plus(C1, Times(b, Power(x, n), Power(a, CN1))), p), x), x),
                  And(FreeQ(List(a, b, n, p), x), Not(IGtQ(p, C0)), Not(IntegerQ(Power(n, CN1))),
                      Not(ILtQ(Simplify(Plus(Power(n, CN1), p)),
                          C0)),
                      Not(Or(IntegerQ(p), GtQ(a, C0)))))),
          IIntegrate(247,
              Integrate(Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(v_, n_))), p_), x_Symbol),
              Condition(
                  Dist(Power(Coefficient(v, x, C1), CN1),
                      Subst(Integrate(Power(Plus(a, Times(b, Power(x, n))), p), x), x, v), x),
                  And(FreeQ(List(a, b, n, p), x), LinearQ(v, x), NeQ(v, x)))),
          IIntegrate(248,
              Integrate(
                  Times(
                      Power(Plus($p("a1", true),
                          Times($p("b1", true), Power(x_, n_))), p_DEFAULT),
                      Power(Plus($p("a2", true), Times($p("b2", true), Power(x_, n_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Power(
                          Plus(
                              Times($s("a1"), $s("a2")), Times($s("b1"), $s("b2"),
                                  Power(x, Times(C2, n)))),
                          p),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")),
                          Times($s("a1"), $s("b2"))), C0),
                      Or(IntegerQ(p), And(GtQ($s("a1"), C0), GtQ($s("a2"), C0)))))),
          IIntegrate(249,
              Integrate(
                  Times(
                      Power(Plus($p("a1"),
                          Times($p("b1", true), Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_DEFAULT))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(x, Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), p), Power(
                          Plus($s("a2"), Times($s("b2"), Power(x, n))), p),
                          Power(Plus(Times(C2, n, p), C1), CN1)), x),
                      Dist(
                          Times(C2, $s("a1"), $s("a2"), n, p,
                              Power(Plus(Times(C2, n, p), C1), CN1)),
                          Integrate(Times(
                              Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Subtract(p, C1)),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2")), x),
                      EqQ(Plus(Times($s("a2"), $s(
                          "b1")), Times($s("a1"),
                              $s("b2"))),
                          C0),
                      IGtQ(Times(C2,
                          n), C0),
                      GtQ(p, C0), Or(
                          IntegerQ(Times(C2,
                              p)),
                          Less(Denominator(Plus(p, Power(n, CN1))), Denominator(p)))))),
          IIntegrate(250,
              Integrate(
                  Times(
                      Power(Plus($p("a1"), Times($p("b1", true),
                          Power(x_, n_DEFAULT))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_DEFAULT))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(x,
                                  Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Plus(p,
                                      C1)),
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Plus(p,
                                      C1)),
                                  Power(Times(C2, $s("a1"), $s("a2"), n, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(Times(C2, n, Plus(p, C1)), C1),
                              Power(Times(C2, $s("a1"), $s("a2"), n, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Plus(p, C1)),
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2")), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0),
                      IGtQ(Times(C2, n), C0), LtQ(p, CN1), Or(
                          IntegerQ(Times(C2, p)), Less(Denominator(Plus(p, Power(n, CN1))),
                              Denominator(p)))))),
          IIntegrate(251,
              Integrate(
                  Times(
                      Power(Plus($p("a1"), Times($p("b1", true),
                          Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Negate(Subst(Integrate(
                      Times(Power(Plus($s("a1"), Times($s("b1"), Power(Power(x, n), CN1))), p),
                          Power(Plus($s("a2"), Times($s("b2"), Power(Power(x, n), CN1))),
                              p),
                          Power(x, CN2)),
                      x), x, Power(x, CN1))),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")),
                          Times($s("a1"), $s("b2"))), C0),
                      ILtQ(Times(C2, n), C0)))),
          IIntegrate(252,
              Integrate(
                  Times(
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(Times(C2, n)))),
                      Dist(k,
                          Subst(Integrate(
                              Times(Power(x, Subtract(k, C1)),
                                  Power(Plus($s("a1"), Times($s("b1"), Power(x, Times(k, n)))), p),
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, Times(k, n)))), p)),
                              x), x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")),
                          Times($s("a1"), $s("b2"))), C0),
                      FractionQ(Times(C2, n))))),
          IIntegrate(253,
              Integrate(
                  Times(
                      Power(Plus($p("a1", true), Times($p("b1", true),
                          Power(x_, n_))), p_),
                      Power(Plus($p("a2", true), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), FracPart(p)),
                          Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), FracPart(p)),
                          Power(Power(
                              Plus(Times($s("a1"), $s("a2")),
                                  Times($s("b1"), $s("b2"), Power(x, Times(C2, n)))),
                              FracPart(p)), CN1)),
                      Integrate(
                          Power(
                              Plus(
                                  Times($s("a1"), $s("a2")), Times($s("b1"), $s("b2"),
                                      Power(x, Times(C2, n)))),
                              p),
                          x),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0),
                      Not(IntegerQ(p))))),
          IIntegrate(254,
              Integrate(
                  Power(
                      Plus(a_, Times(b_DEFAULT, Power(Times(c_DEFAULT, Power(x_, q_DEFAULT)), n_))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Times(x, Power(Power(Times(c, Power(x, q)), Power(q, CN1)), CN1)),
                      Subst(
                          Integrate(Power(Plus(a, Times(b, Power(x, Times(n, q)))), p),
                              x),
                          x, Power(Times(c, Power(x, q)), Power(q, CN1))),
                      x),
                  And(FreeQ(List(a, b, c, n, p, q), x), IntegerQ(Times(n,
                      q)), NeQ(x,
                          Power(Times(c, Power(x, q)), Power(q, CN1)))))),
          IIntegrate(255,
              Integrate(
                  Power(
                      Plus(a_, Times(b_DEFAULT, Power(Times(c_DEFAULT, Power(x_, q_DEFAULT)), n_))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(n))),
                      Subst(
                          Integrate(Power(Plus(a,
                              Times(b, Power(c, n), Power(x, Times(n, q)))), p), x),
                          Power(x, Power(k,
                              CN1)),
                          Times(
                              Power(Times(c, Power(x, q)), Power(k,
                                  CN1)),
                              Power(
                                  Times(
                                      Power(c, Power(k, CN1)), Power(Power(x, Power(k, CN1)),
                                          Subtract(q, C1))),
                                  CN1)))),
                  And(FreeQ(List(a, b, c, p, q), x), FractionQ(n)))),
          IIntegrate(256,
              Integrate(
                  Power(
                      Plus(a_, Times(b_DEFAULT, Power(Times(c_DEFAULT, Power(x_, q_DEFAULT)), n_))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Subst(
                      Integrate(Power(Plus(a, Times(b, Power(c, n), Power(x, Times(n, q)))), p), x),
                      Power(x, Times(n, q)), Times(Power(Times(c, Power(x, q)), n),
                          Power(Power(c, n), CN1))),
                  And(FreeQ(List(a, b, c, n, p, q), x), Not(RationalQ(n))))),
          IIntegrate(257,
              Integrate(
                  Power(
                      Plus(a_, Times(b_DEFAULT, Power(Times(d_DEFAULT, Power(x_, q_DEFAULT)),
                          n_))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(Times(
                              Power(Plus(a, Times(b, Power(Times(d, Power(Power(x, q), CN1)), n))),
                                  p),
                              Power(x, CN2)), x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, d, n, p), x), ILtQ(q, C0)))),
          IIntegrate(258,
              Integrate(
                  Power(
                      Plus(a_, Times(b_DEFAULT, Power(Times(d_DEFAULT, Power(x_, q_DEFAULT)),
                          n_))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(s,
                          Denominator(q))),
                      Dist(s,
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(s, C1)),
                                      Power(
                                          Plus(a,
                                              Times(b, Power(Times(d, Power(x, Times(q, s))), n))),
                                          p)),
                                  x),
                              x, Power(x, Power(s, CN1))),
                          x)),
                  And(FreeQ(List(a, b, d, n, p), x), FractionQ(q)))),
          IIntegrate(259,
              Integrate(
                  Times(Power(Times(c_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Times(c, x), m),
                          Power(
                              Plus(Times($s("a1"), $s("a2")),
                                  Times($s("b1"), $s("b2"), Power(x, Times(C2, n)))),
                              p)),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0),
                      Or(IntegerQ(p), And(GtQ($s("a1"), C0), GtQ($s("a2"), C0)))))),
          IIntegrate(260,
              Integrate(Times(Power(x_, m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), CN1)), x_Symbol),
              Condition(
                  Simp(Times(Log(RemoveContent(Plus(a, Times(b, Power(x, n))), x)),
                      Power(Times(b, n), CN1)), x),
                  And(FreeQ(List(a, b, m, n), x), EqQ(m, Subtract(n, C1))))));
}
