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
class IntRules22 {
  public static IAST RULES =
      List(
          IIntegrate(441,
              Integrate(
                  Times(
                      Sqr(x_), Power(Plus(a_, Times(b_DEFAULT,
                          Sqr(x_))), QQ(-3L,
                              4L)),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b,
                                  ArcTan(
                                      Times(
                                          Plus(b,
                                              Times(Sqr(Rt(Times(Sqr(b), Power(a, CN1)), C4)),
                                                  Sqrt(Plus(a, Times(b, Sqr(x)))))),
                                          Power(
                                              Times(Power(Rt(Times(Sqr(b), Power(a, CN1)), C4), C3),
                                                  x, Power(Plus(a, Times(b, Sqr(x))), C1D4)),
                                              CN1))),
                                  Power(
                                      Times(a, d, Power(Rt(Times(Sqr(b), Power(a, CN1)), C4),
                                          C3)),
                                      CN1)),
                              x)),
                      Simp(Times(b,
                          ArcTanh(Times(
                              Subtract(b,
                                  Times(Sqr(Rt(Times(Sqr(b), Power(a, CN1)), C4)),
                                      Sqrt(Plus(a, Times(b, Sqr(x)))))),
                              Power(Times(Power(Rt(Times(Sqr(b), Power(a, CN1)), C4), C3), x,
                                  Power(Plus(a, Times(b, Sqr(x))), C1D4)), CN1))),
                          Power(Times(a, d, Power(Rt(Times(Sqr(b), Power(a, CN1)), C4), C3)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c,
                      d), x), EqQ(Subtract(Times(b, c), Times(C2, a, d)),
                          C0),
                      PosQ(Times(Sqr(b), Power(a, CN1)))))),
          IIntegrate(442,
              Integrate(
                  Times(
                      Sqr(x_), Power(Plus(a_, Times(b_DEFAULT,
                          Sqr(x_))), QQ(-3L,
                              4L)),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b,
                                  ArcTan(Times(Rt(Times(CN1, Sqr(b), Power(a, CN1)), C4), x,
                                      Power(Times(CSqrt2, Power(Plus(a, Times(b, Sqr(x))), C1D4)),
                                          CN1))),
                                  Power(
                                      Times(CSqrt2, a, d,
                                          Power(Rt(Times(CN1, Sqr(b), Power(a, CN1)), C4), C3)),
                                      CN1)),
                              x)),
                      Simp(
                          Times(b,
                              ArcTanh(Times(Rt(Times(CN1, Sqr(b), Power(a, CN1)), C4), x,
                                  Power(Times(CSqrt2, Power(Plus(a, Times(b, Sqr(x))), C1D4)),
                                      CN1))),
                              Power(
                                  Times(
                                      CSqrt2, a, d, Power(Rt(Times(CN1, Sqr(b), Power(a, CN1)), C4),
                                          C3)),
                                  CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Times(b, c), Times(C2, a, d)), C0),
                      NegQ(Times(Sqr(b), Power(a, CN1)))))),
          IIntegrate(443,
              Integrate(
                  Times(
                      Power(x_, m_), Power(Plus(a_, Times(b_DEFAULT,
                          Sqr(x_))), QQ(-3L,
                              4L)),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(x, m),
                              Power(Times(Power(Plus(a, Times(b, Sqr(x))), QQ(3L, 4L)),
                                  Plus(c, Times(d, Sqr(x)))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Times(b, c), Times(C2, a, d)), C0),
                      IntegerQ(m), Or(PosQ(a), IntegerQ(Times(C1D2, m)))))),
          IIntegrate(444,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT), Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), p),
                                  Power(Plus(c, Times(d, x)), q)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n, p, q), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Plus(m, Negate(n), C1), C0)))),
          IIntegrate(445,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT), Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(x, Plus(m, Times(n, Plus(p, q)))),
                      Power(Plus(b, Times(a, Power(Power(x, n), CN1))),
                          p),
                      Power(Plus(d, Times(c, Power(Power(x, n), CN1))), q)), x),
                  And(FreeQ(List(a, b, c, d, m, n), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), IntegersQ(p,
                          q),
                      NegQ(n)))),
          IIntegrate(446,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x,
                                      Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                                  Power(Plus(a, Times(b, x)), p), Power(Plus(c, Times(d, x)), q)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n, p, q), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(447,
              Integrate(
                  Times(Power(Times(e_, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT), Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m), Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p, q), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(448,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT), Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Times(e, x), m), Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                      C0), IGtQ(p, C0), IGtQ(q, C0)))),
          IIntegrate(449,
              Integrate(Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT), Plus(c_,
                      Times(d_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(c, Power(Times(e, x), Plus(m, C1)),
                          Power(Plus(a, Times(b, Power(x, n))),
                              Plus(p, C1)),
                          Power(Times(a, e, Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(
                          Subtract(Times(a, d, Plus(m, C1)),
                              Times(b, c, Plus(m, Times(n, Plus(p, C1)), C1))),
                          C0),
                      NeQ(m, CN1)))),
          IIntegrate(450,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("a1"),
                          Times($p("b1", true), Power(x_, $p("non2", true)))), p_DEFAULT),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, $p("non2", true)))),
                          p_DEFAULT),
                      Plus(c_, Times(d_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Simp(Times(c, Power(Times(e, x), Plus(m, C1)),
                      Power(Plus($s("a1"), Times($s("b1"), Power(x, Times(C1D2, n)))), Plus(p, C1)),
                      Power(Plus($s("a2"), Times($s("b2"), Power(x, Times(C1D2, n)))), Plus(p,
                          C1)),
                      Power(Times($s("a1"), $s("a2"), e, Plus(m, C1)), CN1)), x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, d, e, m, n, p), x),
                      EqQ($s("non2"), Times(C1D2, n)),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0),
                      EqQ(Subtract(
                          Times($s("a1"), $s("a2"), d, Plus(m, C1)), Times($s("b1"), $s("b2"), c,
                              Plus(m, Times(n, Plus(p, C1)), C1))),
                          C0),
                      NeQ(m, CN1)))),
          IIntegrate(451,
              Integrate(Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                  Plus(c_, Times(d_DEFAULT, Power(x_, n_)))), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(c, Power(Times(e, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(Times(a, e, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(d, Power(Power(e, n), CN1)),
                          Integrate(Times(Power(Times(e, x), Plus(m, n)),
                              Power(Plus(a, Times(b, Power(x, n))), p)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Plus(m, Times(n, Plus(p, C1)), C1), C0), Or(IntegerQ(n), GtQ(e,
                          C0)),
                      Or(And(GtQ(n, C0), LtQ(m, CN1)), And(LtQ(n, C0), GtQ(Plus(m, n), CN1)))))),
          IIntegrate(452,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT), Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Subtract(Times(b, c), Times(a, d)), Power(Times(e, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, Power(x, n))),
                                  Plus(p, C1)),
                              Power(Times(a, b, e, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(d, Power(b,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(e,
                                      x), m),
                                  Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(
                          Plus(m, Times(n, Plus(p, C1)), C1), C0),
                      NeQ(m, CN1)))),
          IIntegrate(453,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Plus(c_, Times(d_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(c, Power(Times(e, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(Times(a, e, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(a, d, Plus(m, C1)),
                                  Times(b, c, Plus(m, Times(n, Plus(p, C1)), C1))),
                              Power(Times(a, Power(e, n), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(e, x), Plus(m, n)),
                                  Power(Plus(a, Times(b, Power(x, n))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      Or(IntegerQ(n), GtQ(e, C0)),
                      Or(And(GtQ(n, C0), LtQ(m, CN1)), And(LtQ(n, C0),
                          GtQ(Plus(m, n), CN1))),
                      Not(ILtQ(p, CN1))))),
          IIntegrate(454,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, $p("non2", true)))),
                          p_DEFAULT),
                      Power(
                          Plus($p("a2"),
                              Times($p("b2", true), Power(x_, $p("non2", true)))),
                          p_DEFAULT),
                      Plus(c_, Times(d_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              c, Power(Times(e, x), Plus(m,
                                  C1)),
                              Power(
                                  Plus($s("a1"), Times($s("b1"),
                                      Power(x, Times(C1D2, n)))),
                                  Plus(p, C1)),
                              Power(
                                  Plus($s("a2"), Times($s("b2"), Power(x, Times(C1D2, n)))), Plus(p,
                                      C1)),
                              Power(Times($s("a1"), $s("a2"), e, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(Times($s("a1"), $s("a2"), d, Plus(m, C1)),
                                  Times($s("b1"), $s("b2"), c, Plus(m, Times(n, Plus(p, C1)), C1))),
                              Power(Times($s("a1"), $s("a2"), Power(e, n), Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Times(e, x), Plus(m, n)),
                              Power(Plus($s("a1"), Times($s("b1"), Power(x, Times(C1D2, n)))), p),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, Times(C1D2, n)))), p)),
                              x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, d, e, p), x),
                      EqQ($s("non2"), Times(C1D2, n)),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"),
                          $s("b2"))), C0),
                      Or(IntegerQ(n), GtQ(e, C0)),
                      Or(And(GtQ(n, C0), LtQ(m,
                          CN1)), And(LtQ(n, C0),
                              GtQ(Plus(m, n), CN1))),
                      Not(ILtQ(p, CN1))))),
          IIntegrate(455,
              Integrate(
                  Times(
                      Power(x_, m_), Power(Plus(a_,
                          Times(b_DEFAULT, Sqr(x_))), p_),
                      Plus(c_, Times(d_DEFAULT, Sqr(x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Negate(a), Subtract(Times(C1D2, m), C1)),
                              Subtract(Times(b, c), Times(a, d)), x,
                              Power(Plus(a, Times(b,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(Times(C2, Power(b, Plus(Times(C1D2, m), C1)), Plus(p, C1)),
                                  CN1)),
                          x),
                      Dist(
                          Power(Times(C2, Power(b, Plus(Times(C1D2, m),
                              C1)), Plus(p,
                                  C1)),
                              CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b,
                                      Sqr(x))), Plus(p,
                                          C1)),
                                  ExpandToSum(
                                      Subtract(
                                          Times(C2, b, Plus(p, C1), Sqr(x),
                                              Together(Times(
                                                  Subtract(
                                                      Times(Power(b, Times(C1D2, m)),
                                                          Power(x, Subtract(m, C2)),
                                                          Plus(c, Times(d, Sqr(x)))),
                                                      Times(
                                                          Power(Negate(a),
                                                              Subtract(Times(C1D2, m), C1)),
                                                          Subtract(Times(b, c), Times(a, d)))),
                                                  Power(Plus(a, Times(b, Sqr(x))), CN1)))),
                                          Times(Power(Negate(a), Subtract(Times(C1D2, m), C1)),
                                              Subtract(Times(b, c), Times(a, d)))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      LtQ(p, CN1), IGtQ(Times(C1D2, m), C0), Or(IntegerQ(p), EqQ(
                          Plus(m, Times(C2, p), C1), C0))))),
          IIntegrate(456,
              Integrate(
                  Times(
                      Power(x_, m_), Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), p_), Plus(c_,
                          Times(d_DEFAULT, Sqr(x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Negate(a), Subtract(Times(C1D2, m), C1)),
                              Subtract(Times(b, c), Times(a, d)), x,
                              Power(Plus(a, Times(b,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(Times(C2, Power(b, Plus(Times(C1D2, m),
                                  C1)), Plus(p,
                                      C1)),
                                  CN1)),
                          x),
                      Dist(
                          Power(Times(C2, Power(b, Plus(Times(C1D2, m),
                              C1)), Plus(p,
                                  C1)),
                              CN1),
                          Integrate(
                              Times(
                                  Power(x, m), Power(Plus(a, Times(b,
                                      Sqr(x))), Plus(p,
                                          C1)),
                                  ExpandToSum(Subtract(
                                      Times(C2, b, Plus(p, C1), Together(Times(
                                          Subtract(
                                              Times(Power(b, Times(C1D2, m)),
                                                  Plus(c, Times(d, Sqr(x)))),
                                              Times(Power(Negate(a), Subtract(Times(C1D2, m), C1)),
                                                  Subtract(Times(b, c), Times(a, d)),
                                                  Power(x, Plus(Negate(m), C2)))),
                                          Power(Plus(a, Times(b, Sqr(x))), CN1)))),
                                      Times(Power(Negate(a), Subtract(Times(C1D2, m), C1)),
                                          Subtract(Times(b, c), Times(a, d)),
                                          Power(Power(x, m), CN1))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      LtQ(p, CN1), ILtQ(Times(C1D2,
                          m), C0),
                      Or(IntegerQ(p), EqQ(Plus(m, Times(C2, p), C1), C0))))),
          IIntegrate(457,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_,
                          n_))), p_DEFAULT),
                      Plus(c_, Times(d_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(Simp(Times(Subtract(Times(b, c), Times(a, d)),
                          Power(Times(e, x), Plus(m, C1)), Power(Plus(a, Times(b, Power(x, n))),
                              Plus(p, C1)),
                          Power(Times(a, b, e, n, Plus(p, C1)), CN1)), x)),
                      Dist(Times(Subtract(Times(a, d, Plus(m, C1)),
                          Times(b, c, Plus(m, Times(n, Plus(p, C1)), C1))),
                          Power(Times(a, b, n, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Times(e, x), m), Power(Plus(a, Times(b, Power(x, n))),
                                      Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), LtQ(p, CN1),
                      Or(And(Not(IntegerQ(Plus(p, C1D2))), NeQ(p, QQ(-5L, 4L))), Not(RationalQ(m)),
                          And(IGtQ(n, C0), ILtQ(Plus(p, C1D2), C0),
                              LeQ(CN1, m, Times(CN1, n, Plus(p, C1)))))))),
          IIntegrate(458,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus($p("a1"),
                          Times($p("b1", true), Power(x_, $p("non2", true)))), p_DEFAULT),
                      Power(
                          Plus($p("a2"), Times($p("b2", true), Power(x_, $p("non2", true)))),
                          p_DEFAULT),
                      Plus(c_, Times(d_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(
                              Times(
                                  Subtract(Times($s("b1"), $s("b2"), c), Times($s("a1"), $s("a2"),
                                      d)),
                                  Power(Times(e, x), Plus(m, C1)),
                                  Power(Plus($s("a1"), Times($s("b1"), Power(x, Times(C1D2, n)))),
                                      Plus(p, C1)),
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, Times(C1D2, n)))),
                                      Plus(p, C1)),
                                  Power(Times($s("a1"), $s("a2"), $s("b1"), $s("b2"), e, n,
                                      Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(
                              Subtract(
                                  Times($s("a1"), $s("a2"), d, Plus(m,
                                      C1)),
                                  Times($s("b1"), $s("b2"), c, Plus(m, Times(n, Plus(p, C1)), C1))),
                              Power(
                                  Times($s("a1"), $s("a2"), $s("b1"), $s("b2"), n, Plus(p, C1)),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(e, x), m),
                                  Power(Plus($s("a1"), Times($s("b1"), Power(x, Times(C1D2, n)))),
                                      Plus(p, C1)),
                                  Power(Plus($s("a2"), Times($s("b2"), Power(x, Times(C1D2, n)))),
                                      Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, d, e, m, n), x),
                      EqQ($s("non2"), Times(C1D2, n)),
                      EqQ(Plus(Times($s("a2"), $s("b1")),
                          Times($s("a1"), $s("b2"))), C0),
                      LtQ(p, CN1), Or(And(Not(IntegerQ(Plus(p, C1D2))), NeQ(p, QQ(-5L, 4L))),
                          Not(RationalQ(
                              m)),
                          And(IGtQ(n, C0), ILtQ(Plus(p, C1D2), C0),
                              LeQ(CN1, m, Times(CN1, n, Plus(p, C1)))))))),
          IIntegrate(459,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Plus(c_, Times(d_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(d, Power(Times(e, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)), Power(
                                  Times(b, e, Plus(m, Times(n, Plus(p, C1)), C1)), CN1)),
                          x),
                      Dist(
                          Times(Subtract(Times(a, d, Plus(m, C1)), Times(b, c,
                              Plus(m, Times(n, Plus(p, C1)), C1))), Power(
                                  Times(b, Plus(m, Times(n, Plus(p, C1)), C1)), CN1)),
                          Integrate(
                              Times(Power(Times(e,
                                  x), m), Power(Plus(a, Times(b, Power(x, n))),
                                      p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0),
                      NeQ(Plus(m, Times(n, Plus(p, C1)), C1), C0)))),
          IIntegrate(460,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, $p("non2", true)))),
                          p_DEFAULT),
                      Power(
                          Plus($p("a2"), Times($p("b2", true), Power(x_, $p("non2", true)))),
                          p_DEFAULT),
                      Plus(c_, Times(d_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(d, Power(Times(e, x), Plus(m, C1)),
                              Power(Plus($s("a1"), Times($s("b1"), Power(x, Times(C1D2, n)))),
                                  Plus(p, C1)),
                              Power(
                                  Plus($s("a2"), Times($s("b2"), Power(x, Times(C1D2, n)))), Plus(p,
                                      C1)),
                              Power(
                                  Times($s("b1"), $s("b2"), e,
                                      Plus(m, Times(n, Plus(p, C1)), C1)),
                                  CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(
                                  Times($s("a1"), $s("a2"), d, Plus(m, C1)), Times($s("b1"),
                                      $s("b2"), c, Plus(m, Times(n, Plus(p, C1)), C1))),
                              Power(
                                  Times($s("b1"), $s("b2"), Plus(m, Times(n, Plus(p, C1)), C1)),
                                  CN1)),
                          Integrate(Times(Power(Times(e, x), m),
                              Power(Plus($s("a1"), Times($s("b1"), Power(x, Times(C1D2, n)))), p),
                              Power(Plus($s("a2"), Times($s("b2"), Power(x, Times(C1D2, n)))), p)),
                              x),
                          x)),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, d, e, m, n, p), x),
                      EqQ($s("non2"), Times(C1D2, n)),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0),
                      NeQ(Plus(m, Times(n, Plus(p, C1)), C1), C0)))));
}
