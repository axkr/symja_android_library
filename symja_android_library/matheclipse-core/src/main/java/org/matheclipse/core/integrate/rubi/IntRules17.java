package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules17 {
  public static IAST RULES =
      List(
          IIntegrate(341,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(n))),
                      Dist(k,
                          Subst(
                              Integrate(Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                                  Power(Plus(a, Times(b, Power(x, Times(k, n)))), p)), x),
                              x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, m, p), x), FractionQ(n)))),
          IIntegrate(342, Integrate(Times(Power(x_, m_DEFAULT),
              Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_), Power(
                  Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
              x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(Times(C2, n)))),
                      Dist(k,
                          Subst(Integrate(
                              Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                                  Power(Plus($s("a1"), Times($s("b1"), Power(x, Times(k, n)))), p),
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, Times(k, n)))), p)),
                              x), x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), m, p), x),
                      EqQ(Plus(Times($s("a2"), $s(
                          "b1")), Times($s("a1"),
                              $s("b2"))),
                          C0),
                      FractionQ(Times(C2, n))))),
          IIntegrate(343,
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
                  And(FreeQ(List(a, b, c, m, p), x), FractionQ(n)))),
          IIntegrate(344,
              Integrate(
                  Times(Power(Times(c_, x_), m_),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_), Power(Plus(
                          $p("a2"), Times($p("b2", true), Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(m)), Power(Times(c, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(Times(Power(x, m),
                          Power(Plus($s("a1"),
                              Times($s("b1"), Power(x, n))), p),
                          Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), p)), x),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")),
                          Times($s("a1"), $s("b2"))), C0),
                      FractionQ(Times(C2, n))))),
          IIntegrate(345,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Plus(m,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Power(
                                  Plus(a,
                                      Times(b,
                                          Power(x, Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                  p),
                              x),
                          x, Power(x, Plus(m, C1))),
                      x),
                  And(FreeQ(List(a, b, m, n, p), x), IntegerQ(Simplify(
                      Times(n, Power(Plus(m, C1), CN1)))), Not(
                          IntegerQ(n))))),
          IIntegrate(346,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true),
                          Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(Dist(
                  Power(Plus(m,
                      C1), CN1),
                  Subst(
                      Integrate(
                          Times(
                              Power(
                                  Plus($s("a1"),
                                      Times($s("b1"),
                                          Power(x, Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                  p),
                              Power(
                                  Plus($s("a2"),
                                      Times($s("b2"),
                                          Power(x, Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                  p)),
                          x),
                      x, Power(x, Plus(m, C1))),
                  x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), m, n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"),
                          $s("b2"))), C0),
                      IntegerQ(Simplify(Times(C2, n, Power(Plus(m, C1), CN1)))),
                      Not(IntegerQ(Times(C2, n)))))),
          IIntegrate(347,
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
                  And(FreeQ(List(a, b, c, m, n, p), x), IntegerQ(Simplify(
                      Times(n, Power(Plus(m, C1), CN1)))), Not(
                          IntegerQ(n))))),
          IIntegrate(348,
              Integrate(
                  Times(Power(Times(c_, x_), m_),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_), Power(Plus(
                          $p("a2"), Times($p("b2", true), Power(x_, n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(m)), Power(Times(c, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(Times(Power(x, m),
                          Power(Plus($s("a1"),
                              Times($s("b1"), Power(x, n))), p),
                          Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), p)), x),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0),
                      IntegerQ(Simplify(
                          Times(C2, n, Power(Plus(m, C1), CN1)))),
                      Not(IntegerQ(Times(C2, n)))))),
          IIntegrate(349,
              Integrate(Times(Power(x_, m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(x, Plus(m, C1)), Power(Plus(a, Times(b, Power(x, n))), p),
                          Power(Plus(m, C1), CN1)), x),
                      Dist(Times(b, n, p, Power(Plus(m, C1), CN1)),
                          Integrate(
                              Times(Power(x, Plus(m, n)),
                                  Power(Plus(a, Times(b, Power(x, n))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, m, n), x), EqQ(Plus(Times(Plus(m, C1), Power(n, CN1)),
                      p), C0), GtQ(p,
                          C0)))),
          IIntegrate(350,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true),
                          Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(x, Plus(m, C1)),
                              Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), p),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), p),
                              Power(Plus(m, C1), CN1)),
                          x),
                      Dist(Times(C2, $s("b1"), $s("b2"), n, p, Power(Plus(m, C1), CN1)),
                          Integrate(Times(Power(x, Plus(m, Times(C2, n))),
                              Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Subtract(p, C1)),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), m, n), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"),
                          $s("b2"))), C0),
                      EqQ(Plus(Times(Plus(m, C1), Power(Times(C2, n), CN1)), p), C0), GtQ(p, C0)))),
          IIntegrate(351,
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
                  And(FreeQ(List(a, b, c, m, n), x), EqQ(Plus(Times(Plus(m, C1), Power(n, CN1)),
                      p), C0), GtQ(p,
                          C0)))),
          IIntegrate(352,
              Integrate(
                  Times(Power(Times(c_, x_), m_),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(c, IntPart(m)), Power(Times(c, x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m), Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), p),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n), x),
                      EqQ(Plus(Times($s("a2"), $s(
                          "b1")), Times($s("a1"),
                              $s("b2"))),
                          C0),
                      EqQ(Plus(Times(Plus(m, C1), Power(Times(C2, n), CN1)), p), C0), GtQ(p, C0)))),
          IIntegrate(353,
              Integrate(
                  Times(Power(Times(c_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(c, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, Power(x, n))),
                                  p),
                              Power(Times(c, Plus(m, Times(n, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(a, n, p, Power(Plus(m, Times(n, p), C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(c,
                                      x), m),
                                  Power(Plus(a, Times(b, Power(x, n))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, m, n), x),
                      IntegerQ(Plus(p,
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))),
                      GtQ(p, C0), NeQ(Plus(m, Times(n, p), C1), C0)))),
          IIntegrate(354,
              Integrate(Times(Power(Times(c_DEFAULT, x_), m_DEFAULT),
                  Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_),
                  Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)), x_Symbol),
              Condition(Plus(
                  Simp(Times(Power(Times(c, x), Plus(m, C1)),
                      Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), p),
                      Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), p),
                      Power(Times(c, Plus(m, Times(C2, n, p), C1)), CN1)), x),
                  Dist(
                      Times(C2, $s("a1"), $s("a2"), n, p, Power(Plus(m, Times(C2, n, p), C1), CN1)),
                      Integrate(
                          Times(Power(Times(c, x), m),
                              Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Subtract(p, C1)),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Subtract(p, C1))),
                          x),
                      x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n), x),
                      EqQ(Plus(Times($s("a2"), $s(
                          "b1")), Times($s("a1"),
                              $s("b2"))),
                          C0),
                      IntegerQ(Plus(p, Simplify(Times(Plus(m, C1), Power(Times(C2, n), CN1))))),
                      GtQ(p, C0), NeQ(Plus(m, Times(C2, n, p), C1), C0)))),
          IIntegrate(355,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(p))),
                      Dist(
                          Times(k, Power(a, Plus(p,
                              Simplify(Times(Plus(m, C1), Power(n, CN1))))), Power(n,
                                  CN1)),
                          Subst(Integrate(Times(
                              Power(x,
                                  Subtract(Times(k, Simplify(Times(Plus(m, C1), Power(n, CN1)))),
                                      C1)),
                              Power(
                                  Power(Subtract(C1, Times(b, Power(x, k))),
                                      Plus(p, Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                                  CN1)),
                              x), x,
                              Times(Power(x, Times(n, Power(k, CN1))),
                                  Power(Power(Plus(a, Times(b, Power(x, n))), Power(k, CN1)),
                                      CN1))),
                          x)),
                  And(FreeQ(List(a, b, m, n), x),
                      IntegerQ(Plus(p,
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))),
                      LtQ(CN1, p, C0)))),
          IIntegrate(356,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(p))),
                      Dist(
                          Times(k,
                              Power(
                                  Times($s("a1"), $s("a2")), Plus(p, Simplify(
                                      Times(Plus(m, C1), Power(Times(C2, n), CN1))))),
                              Power(Times(C2, n), CN1)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(x,
                                          Subtract(
                                              Times(k,
                                                  Simplify(Times(Plus(m, C1),
                                                      Power(Times(C2, n), CN1)))),
                                              C1)),
                                      Power(
                                          Power(
                                              Subtract(C1, Times($s("b1"), $s("b2"), Power(x, k))),
                                              Plus(p,
                                                  Simplify(
                                                      Times(Plus(m, C1), Power(Times(C2, n), CN1))),
                                                  C1)),
                                          CN1)),
                                  x),
                              x,
                              Times(Power(x, Times(C2, n, Power(k, CN1))),
                                  Power(Times(
                                      Power(Plus($s("a1"), Times($s("b1"), Power(x, n))),
                                          Power(k, CN1)),
                                      Power(Plus($s("a2"), Times($s("b2"), Power(x, n))),
                                          Power(k, CN1))),
                                      CN1))),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), m, n), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"),
                          $s("b2"))), C0),
                      IntegerQ(Plus(p, Simplify(Times(Plus(m, C1), Power(Times(C2, n), CN1))))),
                      LtQ(CN1, p, C0)))),
          IIntegrate(357,
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
                  And(FreeQ(List(a, b, c, m, n), x),
                      IntegerQ(Plus(p,
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))),
                      LtQ(CN1, p, C0)))),
          IIntegrate(358,
              Integrate(
                  Times(Power(Times(c_, x_), m_),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(c, IntPart(m)), Power(Times(c, x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m), Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), p),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"),
                          $s("b2"))), C0),
                      IntegerQ(Plus(p,
                          Simplify(Times(Plus(m, C1), Power(Times(C2, n), CN1))))),
                      LtQ(CN1, p, C0)))),
          IIntegrate(359,
              Integrate(
                  Times(
                      Power(Times(c_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(Power(Times(c, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(Times(a, c, n, Plus(p, C1)), CN1)), x)),
                      Dist(
                          Times(Plus(m, Times(n, Plus(p, C1)), C1),
                              Power(Times(a, n, Plus(p, C1)), CN1)),
                          Integrate(Times(Power(Times(c, x), m),
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, m, n), x),
                      IntegerQ(Plus(p,
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))),
                      LtQ(p, CN1)))),
          IIntegrate(360,
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
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Plus(p,
                                      C1)),
                                  Power(Times(C2, $s("a1"), $s("a2"), c, n, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(m, Times(C2, n, Plus(p, C1)), C1),
                              Power(Times(C2, $s("a1"), $s("a2"), n, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(c, x), m),
                                  Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Plus(p, C1)),
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0),
                      IntegerQ(Plus(p, Simplify(Times(Plus(m, C1), Power(Times(C2, n), CN1))))),
                      LtQ(p, CN1)))));
}
