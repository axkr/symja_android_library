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
class IntRules97 {
  public static IAST RULES =
      List(
          IIntegrate(1941,
              Integrate(Times(Power(x_, m_DEFAULT),
                  Power(Plus(Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                      Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(a_DEFAULT, Power(x_, q_DEFAULT))), p_DEFAULT),
                  Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, Plus(m, C1)),
                              Plus(
                                  Times(ASymbol,
                                      Plus(m, Times(p, q),
                                          Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                                  Times(BSymbol, Plus(m, Times(p, q), C1),
                                      Power(x, Subtract(n, q)))),
                              Power(
                                  Plus(
                                      Times(a, Power(x, q)), Times(b, Power(x, n)), Times(c,
                                          Power(x, Subtract(Times(C2, n), q)))),
                                  p),
                              Power(
                                  Times(
                                      Plus(m, Times(p, q), C1),
                                      Plus(
                                          m, Times(p, q),
                                          Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                                  CN1)),
                          x),
                      Dist(
                          Times(Subtract(n, q), p,
                              Power(
                                  Times(Plus(m, Times(p, q), C1),
                                      Plus(
                                          m, Times(p, q), Times(Subtract(n, q),
                                              Plus(Times(C2, p), C1)),
                                          C1)),
                                  CN1)),
                          Integrate(
                              Times(Power(x, Plus(n, m)),
                                  Simp(
                                      Plus(Times(C2, a, BSymbol, Plus(m, Times(p, q), C1)),
                                          Times(CN1, ASymbol, b,
                                              Plus(m, Times(p, q), Times(Subtract(n, q),
                                                  Plus(Times(C2, p), C1)), C1)),
                                          Times(
                                              Subtract(Times(b, BSymbol, Plus(m, Times(p, q), C1)),
                                                  Times(C2, ASymbol, c,
                                                      Plus(m, Times(p, q),
                                                          Times(Subtract(n, q),
                                                              Plus(Times(C2, p), C1)),
                                                          C1))),
                                              Power(x, Subtract(n, q)))),
                                      x),
                                  Power(
                                      Plus(Times(a, Power(x, q)), Times(b,
                                          Power(x, n)),
                                          Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                      Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol), x), EqQ(r, Subtract(n, q)),
                      EqQ(j, Subtract(Times(C2, n), q)), Not(IntegerQ(p)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(n, C0), GtQ(p, C0),
                      RationalQ(m, q), LeQ(Plus(m, Times(p, q)), Negate(Subtract(n, q))),
                      NeQ(Plus(m, Times(p, q), C1), C0),
                      NeQ(Plus(m, Times(p, q), Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1),
                          C0)))),
          IIntegrate(1942,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                          Times(a_DEFAULT, Power(x_, q_DEFAULT))), p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(n, Plus(q, r))), Condition(
                          Plus(
                              Simp(
                                  Times(
                                      Power(x, Plus(m, C1)),
                                      Plus(
                                          Times(ASymbol, Plus(m, Times(p, q),
                                              Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                                          Times(
                                              BSymbol, Plus(m, Times(p, q), C1), Power(x,
                                                  Subtract(n, q)))),
                                      Power(
                                          Plus(Times(a, Power(x, q)), Times(c,
                                              Power(x, Subtract(Times(C2, n), q)))),
                                          p),
                                      Power(
                                          Times(Plus(m, Times(p, q), C1), Plus(m, Times(p, q),
                                              Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                                          CN1)),
                                  x),
                              Dist(
                                  Times(C2, Subtract(n, q), p,
                                      Power(
                                          Times(Plus(m, Times(p, q), C1),
                                              Plus(m, Times(p, q),
                                                  Times(Subtract(n, q), Plus(Times(C2, p),
                                                      C1)),
                                                  C1)),
                                          CN1)),
                                  Integrate(Times(Power(x, Plus(n, m)), Simp(Subtract(
                                      Times(a, BSymbol, Plus(m, Times(p, q), C1)),
                                      Times(ASymbol, c,
                                          Plus(m, Times(p, q),
                                              Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1),
                                          Power(x, Subtract(n, q)))),
                                      x),
                                      Power(
                                          Plus(Times(a, Power(x, q)),
                                              Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                          Subtract(p, C1))),
                                      x),
                                  x)),
                          And(EqQ(j, Subtract(Times(C2, n), q)), IGtQ(n, C0), LeQ(Plus(m,
                              Times(p, q)),
                              Negate(Subtract(n, q))), NeQ(Plus(m, Times(p, q), C1), C0),
                              NeQ(Plus(m, Times(p, q),
                                  Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1), C0)))),
                  And(FreeQ(List(a, c, ASymbol,
                      BSymbol), x), Not(
                          IntegerQ(p)),
                      RationalQ(m, p, q), GtQ(p, C0)))),
          IIntegrate(1943,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(Times(c_DEFAULT, Power(x_, j_DEFAULT)), Times(b_DEFAULT,
                          Power(x_, n_DEFAULT)), Times(a_DEFAULT, Power(x_, q_DEFAULT))),
                          p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, Plus(m, Negate(n), C1)),
                              Subtract(Subtract(Times(ASymbol, b), Times(C2, a, BSymbol)),
                                  Times(Subtract(Times(b, BSymbol), Times(C2, ASymbol, c)), Power(x,
                                      Subtract(n, q)))),
                              Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                  Times(c, Power(x, Subtract(Times(C2, n), q)))), Plus(p, C1)),
                              Power(Times(Subtract(n, q), Plus(p, C1),
                                  Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          x),
                      Dist(
                          Power(
                              Times(Subtract(n, q), Plus(p, C1),
                                  Subtract(Sqr(b), Times(C4, a, c))),
                              CN1),
                          Integrate(Times(Power(x, Subtract(m, n)),
                              Simp(Plus(
                                  Times(Plus(m, Times(p, q), Negate(n), q, C1),
                                      Subtract(Times(C2, a, BSymbol), Times(ASymbol, b))),
                                  Times(
                                      Plus(m, Times(p, q), Times(C2, Subtract(n, q), Plus(p, C1)),
                                          C1),
                                      Subtract(Times(b, BSymbol), Times(C2, ASymbol, c)),
                                      Power(x, Subtract(n, q)))),
                                  x),
                              Power(
                                  Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                      Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                  Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol),
                      x), EqQ(r, Subtract(n, q)), EqQ(j, Subtract(Times(C2, n), q)),
                      Not(IntegerQ(p)), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(n, C0),
                      LtQ(p, CN1), RationalQ(m, q), GtQ(Plus(m, Times(p, q)), Subtract(Subtract(n,
                          q), C1))))),
          IIntegrate(1944,
              Integrate(Times(Power(x_, m_DEFAULT),
                  Power(Plus(Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                      Times(a_DEFAULT, Power(x_, q_DEFAULT))), p_DEFAULT),
                  Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))), x_Symbol),
              Condition(With(List(Set(n, Plus(q, r))),
                  Condition(
                      Subtract(
                          Simp(
                              Times(Power(x, Plus(m, Negate(n), C1)),
                                  Subtract(
                                      Times(a, BSymbol),
                                      Times(ASymbol, c, Power(x, Subtract(n, q)))),
                                  Power(
                                      Plus(Times(a, Power(x, q)), Times(c,
                                          Power(x, Subtract(Times(C2, n), q)))),
                                      Plus(p, C1)),
                                  Power(Times(C2, a, c, Subtract(n, q), Plus(p, C1)), CN1)),
                              x),
                          Dist(Power(Times(C2, a, c, Subtract(n, q), Plus(p, C1)), CN1),
                              Integrate(Times(Power(x, Subtract(m, n)), Simp(Subtract(
                                  Times(a, BSymbol, Plus(m, Times(p, q), Negate(n), q, C1)),
                                  Times(ASymbol, c,
                                      Plus(m, Times(p, q), Times(Subtract(n, q), C2, Plus(p, C1)),
                                          C1),
                                      Power(x, Subtract(n, q)))),
                                  x),
                                  Power(
                                      Plus(Times(a, Power(x, q)),
                                          Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                      Plus(p, C1))),
                                  x),
                              x)),
                      And(EqQ(j,
                          Subtract(Times(C2, n), q)), IGtQ(n, C0),
                          Greater(Plus(m, Times(p, q)), Subtract(Subtract(n, q), C1))))),
                  And(FreeQ(List(a, c, ASymbol, BSymbol), x), Not(
                      IntegerQ(p)), RationalQ(m, q), LtQ(p, CN1)))),
          IIntegrate(1945,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                              Times(a_DEFAULT, Power(x_, q_DEFAULT))),
                          p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))),
                  x_Symbol),
              Condition(Plus(
                  Simp(Times(Power(x, Plus(m, C1)), Plus(Times(b, BSymbol, Subtract(n, q), p),
                      Times(ASymbol, c,
                          Plus(m, Times(p, q), Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                      Times(BSymbol, c, Plus(m, Times(p, q), Times(C2, Subtract(n, q), p), C1),
                          Power(x, Subtract(n, q)))),
                      Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                          Times(c, Power(x, Subtract(Times(C2, n), q)))), p),
                      Power(Times(c, Plus(m, Times(p, Subtract(Times(C2, n), q)), C1),
                          Plus(m, Times(p, q), Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                          CN1)),
                      x),
                  Dist(Times(Subtract(n, q), p,
                      Power(
                          Times(c, Plus(m, Times(p, Subtract(Times(C2, n), q)), C1), Plus(m,
                              Times(p, q), Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                          CN1)),
                      Integrate(
                          Times(Power(x, Plus(m, q)),
                              Simp(Plus(
                                  Times(C2, a, ASymbol, c,
                                      Plus(m, Times(p, q),
                                          Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                                  Times(CN1, a, b, BSymbol, Plus(m, Times(p, q), C1)),
                                  Times(
                                      Subtract(
                                          Plus(
                                              Times(C2, a, BSymbol, c,
                                                  Plus(m, Times(p, q), Times(C2, Subtract(n, q), p),
                                                      C1)),
                                              Times(
                                                  ASymbol, b, c,
                                                  Plus(m, Times(p, q),
                                                      Times(Subtract(n, q), Plus(Times(C2, p), C1)),
                                                      C1))),
                                          Times(Sqr(b), BSymbol,
                                              Plus(m, Times(p, q), Times(Subtract(n, q), p), C1))),
                                      Power(x, Subtract(n, q)))),
                                  x),
                              Power(
                                  Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                      Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                  Subtract(p, C1))),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol), x), EqQ(r, Subtract(n, q)),
                      EqQ(j, Subtract(Times(C2, n), q)), Not(IntegerQ(p)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(n, C0), GtQ(p, C0),
                      RationalQ(m, q),
                      GtQ(Plus(m, Times(p,
                          q)), Subtract(Negate(Subtract(n, q)),
                              C1)),
                      NeQ(Plus(m, Times(p, Subtract(Times(C2, n), q)),
                          C1), C0),
                      NeQ(Plus(m, Times(p, q), Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1),
                          C0)))),
          IIntegrate(1946,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              Times(c_DEFAULT, Power(x_, j_DEFAULT)), Times(a_DEFAULT,
                                  Power(x_, q_DEFAULT))),
                          p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))),
                  x_Symbol),
              Condition(
                  With(List(Set(n, Plus(q, r))),
                      Condition(
                          Plus(
                              Simp(Times(Power(x, Plus(m, C1)),
                                  Plus(
                                      Times(ASymbol,
                                          Plus(m, Times(p, q),
                                              Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                                      Times(BSymbol,
                                          Plus(m, Times(p, q), Times(C2, Subtract(n, q), p),
                                              C1),
                                          Power(x, Subtract(n, q)))),
                                  Power(
                                      Plus(
                                          Times(a, Power(x, q)), Times(c,
                                              Power(x, Subtract(Times(C2, n), q)))),
                                      p),
                                  Power(
                                      Times(Plus(m, Times(p, Subtract(Times(C2, n), q)), C1),
                                          Plus(m, Times(p, q),
                                              Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                                      CN1)),
                                  x),
                              Dist(
                                  Times(Subtract(n, q), p,
                                      Power(
                                          Times(Plus(m, Times(p, Subtract(Times(C2, n), q)), C1),
                                              Plus(m, Times(p, q),
                                                  Times(Subtract(n, q), Plus(Times(C2, p), C1)),
                                                  C1)),
                                          CN1)),
                                  Integrate(
                                      Times(
                                          Power(x, Plus(m,
                                              q)),
                                          Simp(
                                              Plus(
                                                  Times(C2, a, ASymbol,
                                                      Plus(m, Times(p, q),
                                                          Times(Subtract(n, q),
                                                              Plus(Times(C2, p), C1)),
                                                          C1)),
                                                  Times(C2, a, BSymbol,
                                                      Plus(m, Times(p, q),
                                                          Times(C2, Subtract(n, q), p), C1),
                                                      Power(x, Subtract(n, q)))),
                                              x),
                                          Power(
                                              Plus(Times(a, Power(x, q)),
                                                  Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                              Subtract(p, C1))),
                                      x),
                                  x)),
                          And(EqQ(j, Subtract(Times(C2, n), q)), IGtQ(n, C0),
                              GtQ(Plus(m, Times(p, q)), Negate(Subtract(n, q))),
                              NeQ(Plus(m, Times(p, q), Times(C2, Subtract(n, q), p), C1), C0),
                              NeQ(Plus(m, Times(p, q),
                                  Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1), C0),
                              NeQ(Plus(m, C1), n)))),
                  And(FreeQ(List(a, c, ASymbol,
                      BSymbol), x), Not(
                          IntegerQ(p)),
                      RationalQ(m, q), GtQ(p, C0)))),
          IIntegrate(1947,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_,
                                  n_DEFAULT)),
                              Times(a_DEFAULT, Power(x_, q_DEFAULT))),
                          p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(x, Plus(m, Negate(q), C1)),
                                  Plus(Times(ASymbol, Sqr(b)), Times(CN1, a, b, BSymbol),
                                      Times(CN1, C2, a, ASymbol, c),
                                      Times(Subtract(Times(ASymbol, b), Times(C2, a, BSymbol)), c,
                                          Power(x, Subtract(n, q)))),
                                  Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                      Times(c, Power(x, Subtract(Times(C2, n), q)))), Plus(p, C1)),
                                  Power(Times(a, Subtract(n, q), Plus(p, C1),
                                      Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                              x)),
                      Dist(
                          Power(
                              Times(
                                  a, Subtract(n, q), Plus(p, C1), Subtract(Sqr(b),
                                      Times(C4, a, c))),
                              CN1),
                          Integrate(
                              Times(Power(x, Subtract(m, q)),
                                  Simp(Plus(
                                      Times(ASymbol, Sqr(b),
                                          Plus(m, Times(p, q), Times(Subtract(n, q), Plus(p, C1)),
                                              C1)),
                                      Times(CN1, a, b, BSymbol, Plus(m, Times(p, q), C1)),
                                      Times(CN1, C2, a, ASymbol, c,
                                          Plus(m, Times(p, q),
                                              Times(C2, Subtract(n, q), Plus(p, C1)), C1)),
                                      Times(
                                          Plus(m, Times(p, q),
                                              Times(Subtract(n, q), Plus(Times(C2, p), C3)), C1),
                                          Subtract(Times(ASymbol, b), Times(C2, a, BSymbol)), c,
                                          Power(x, Subtract(n, q)))),
                                      x),
                                  Power(
                                      Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                          Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                      Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol),
                      x), EqQ(r, Subtract(n, q)), EqQ(j, Subtract(Times(C2, n), q)),
                      Not(IntegerQ(p)), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(n, C0),
                      LtQ(p, CN1), RationalQ(m, q), Less(Plus(m, Times(p, q)),
                          Subtract(Subtract(n, q), C1))))),
          IIntegrate(1948,
              Integrate(Times(Power(x_, m_DEFAULT),
                  Power(Plus(Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                      Times(a_DEFAULT, Power(x_, q_DEFAULT))), p_DEFAULT),
                  Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))), x_Symbol),
              Condition(With(
                  List(Set(n, Plus(q, r))),
                  Condition(
                      Plus(
                          Negate(
                              Simp(
                                  Times(
                                      Power(x, Plus(m, Negate(q),
                                          C1)),
                                      Plus(
                                          Times(ASymbol, c),
                                          Times(BSymbol, c, Power(x, Subtract(n, q)))),
                                      Power(
                                          Plus(Times(a, Power(x, q)), Times(c,
                                              Power(x, Subtract(Times(C2, n), q)))),
                                          Plus(p, C1)),
                                      Power(Times(C2, a, c, Subtract(n, q), Plus(p, C1)), CN1)),
                                  x)),
                          Dist(
                              Power(Times(C2, a, c, Subtract(n, q),
                                  Plus(p, C1)), CN1),
                              Integrate(Times(Power(x, Subtract(m, q)),
                                  Simp(Plus(
                                      Times(ASymbol, c,
                                          Plus(m, Times(p, q),
                                              Times(C2, Subtract(n, q), Plus(p, C1)), C1)),
                                      Times(BSymbol,
                                          Plus(m, Times(p, q),
                                              Times(Subtract(n, q), Plus(Times(C2, p), C3)), C1),
                                          c, Power(x, Subtract(n, q)))),
                                      x),
                                  Power(
                                      Plus(Times(a, Power(x, q)),
                                          Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                      Plus(p, C1))),
                                  x),
                              x)),
                      And(EqQ(j, Subtract(Times(C2, n),
                          q)), IGtQ(n,
                              C0),
                          LtQ(Plus(m, Times(p, q)), Subtract(Subtract(n, q), C1))))),
                  And(FreeQ(List(a, c, ASymbol, BSymbol), x), Not(
                      IntegerQ(p)), RationalQ(m, q), LtQ(p, CN1)))),
          IIntegrate(1949,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                              Times(a_DEFAULT, Power(x_, q_DEFAULT))),
                          p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(BSymbol, Power(x, Plus(m, Negate(n), C1)),
                              Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                  Times(c, Power(x, Subtract(Times(C2, n), q)))), Plus(p, C1)),
                              Power(
                                  Times(c,
                                      Plus(m, Times(p, q),
                                          Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1)),
                                  CN1)),
                          x),
                      Dist(
                          Power(
                              Times(c,
                                  Plus(
                                      m, Times(p, q), Times(Subtract(n, q),
                                          Plus(Times(C2, p), C1)),
                                      C1)),
                              CN1),
                          Integrate(Times(
                              Power(x, Plus(m, Negate(n),
                                  q)),
                              Simp(Plus(Times(a, BSymbol, Plus(m, Times(p, q), Negate(n), q, C1)),
                                  Times(
                                      Subtract(
                                          Times(b, BSymbol,
                                              Plus(m, Times(p, q), Times(Subtract(n, q), p), C1)),
                                          Times(ASymbol, c, Plus(m, Times(p, q),
                                              Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1))),
                                      Power(x, Subtract(n, q)))),
                                  x),
                              Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                  Times(c, Power(x, Subtract(Times(C2, n), q)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol), x), EqQ(r, Subtract(n, q)),
                      EqQ(j, Subtract(Times(C2, n), q)), Not(IntegerQ(p)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IGtQ(n, C0), GeQ(p, CN1),
                      LtQ(p, C0), RationalQ(m, q),
                      GeQ(Plus(m, Times(p,
                          q)), Subtract(Subtract(n, q),
                              C1)),
                      NeQ(Plus(m, Times(p, q), Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1),
                          C0)))),
          IIntegrate(1950,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(a_DEFAULT, Power(x_, q_DEFAULT))),
                          p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(n,
                          Plus(q, r))),
                      Condition(
                          Subtract(
                              Simp(
                                  Times(BSymbol, Power(x, Plus(m, Negate(n), C1)),
                                      Power(Plus(Times(a, Power(x, q)),
                                          Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                          Plus(p, C1)),
                                      Power(
                                          Times(c,
                                              Plus(m, Times(p, q),
                                                  Times(Subtract(n, q), Plus(Times(C2, p), C1)),
                                                  C1)),
                                          CN1)),
                                  x),
                              Dist(
                                  Power(
                                      Times(c,
                                          Plus(
                                              m, Times(p, q), Times(Subtract(n, q),
                                                  Plus(Times(C2, p), C1)),
                                              C1)),
                                      CN1),
                                  Integrate(Times(Power(x, Plus(m, Negate(n), q)),
                                      Simp(Subtract(
                                          Times(a, BSymbol, Plus(m, Times(p, q), Negate(n), q, C1)),
                                          Times(ASymbol, c, Plus(m, Times(p, q),
                                              Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1),
                                              Power(x, Subtract(n, q)))),
                                          x),
                                      Power(Plus(Times(a, Power(x, q)),
                                          Times(c, Power(x, Subtract(Times(C2, n), q)))), p)),
                                      x),
                                  x)),
                          And(EqQ(j, Subtract(Times(C2, n), q)), IGtQ(n, C0),
                              GeQ(Plus(m, Times(p, q)), Subtract(Subtract(n, q), C1)),
                              NeQ(Plus(m, Times(p, q),
                                  Times(Subtract(n, q), Plus(Times(C2, p), C1)), C1), C0)))),
                  And(FreeQ(List(a, c, ASymbol,
                      BSymbol), x), Not(
                          IntegerQ(p)),
                      RationalQ(m, p, q), GeQ(p, CN1), LtQ(p, C0)))),
          IIntegrate(1951, Integrate(Times(Power(x_, m_DEFAULT),
              Power(Plus(Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                  Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(a_DEFAULT, Power(x_, q_DEFAULT))),
                  p_DEFAULT),
              Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))), x_Symbol), Condition(
                  Plus(Simp(Times(ASymbol, Power(x, Plus(m, Negate(q), C1)),
                      Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                          Times(c, Power(x, Subtract(Times(C2, n), q)))), Plus(p, C1)),
                      Power(Times(a, Plus(m, Times(p, q), C1)), CN1)), x),
                      Dist(Power(Times(a, Plus(m, Times(p, q), C1)), CN1),
                          Integrate(Times(Power(x, Subtract(Plus(m, n), q)),
                              Simp(Subtract(
                                  Subtract(Times(a, BSymbol, Plus(m, Times(p, q), C1)),
                                      Times(ASymbol, b,
                                          Plus(m, Times(p, q), Times(Subtract(n, q), Plus(p, C1)),
                                              C1))),
                                  Times(ASymbol, c,
                                      Plus(m, Times(p, q), Times(C2, Subtract(n, q), Plus(p, C1)),
                                          C1),
                                      Power(x, Subtract(n, q)))),
                                  x),
                              Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                  Times(c, Power(x, Subtract(Times(C2, n), q)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol), x), EqQ(r, Subtract(n, q)),
                      EqQ(j, Subtract(Times(C2, n), q)), Not(IntegerQ(p)),
                      NeQ(Subtract(Sqr(b),
                          Times(C4, a, c)), C0),
                      IGtQ(n, C0), RationalQ(m, p, q), Or(
                          And(GeQ(p, CN1), LtQ(p,
                              C0)),
                          EqQ(Plus(m, Times(p, q), Times(Subtract(n, q), Plus(Times(C2, p), C1)),
                              C1), C0)),
                      LeQ(Plus(m, Times(p,
                          q)), Negate(
                              Subtract(n, q))),
                      NeQ(Plus(m, Times(p, q), C1), C0)))),
          IIntegrate(1952,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              Times(c_DEFAULT, Power(x_,
                                  j_DEFAULT)),
                              Times(a_DEFAULT, Power(x_, q_DEFAULT))),
                          p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(x_, r_DEFAULT)))),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(n,
                          Plus(q, r))),
                      Condition(
                          Plus(
                              Simp(
                                  Times(ASymbol, Power(x, Plus(m, Negate(q), C1)),
                                      Power(Plus(Times(a, Power(x, q)),
                                          Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                          Plus(p, C1)),
                                      Power(Times(a, Plus(m, Times(p, q), C1)), CN1)),
                                  x),
                              Dist(Power(Times(a, Plus(m, Times(p, q), C1)), CN1),
                                  Integrate(
                                      Times(
                                          Power(x, Subtract(Plus(m, n), q)),
                                          Simp(Subtract(Times(a, BSymbol, Plus(m, Times(p, q), C1)),
                                              Times(ASymbol, c,
                                                  Plus(m, Times(p, q),
                                                      Times(C2, Subtract(n, q), Plus(p, C1)), C1),
                                                  Power(x, Subtract(n, q)))),
                                              x),
                                          Power(
                                              Plus(Times(a, Power(x, q)),
                                                  Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                              p)),
                                      x),
                                  x)),
                          And(EqQ(j, Subtract(Times(C2, n), q)), IGtQ(n, C0),
                              Or(And(GeQ(p, CN1), LtQ(p, C0)),
                                  EqQ(Plus(
                                      m, Times(p, q), Times(Subtract(n, q), Plus(Times(C2, p), C1)),
                                      C1), C0)),
                              LeQ(Plus(m, Times(p, q)), Negate(Subtract(n, q))), NeQ(
                                  Plus(m, Times(p, q), C1), C0)))),
                  And(FreeQ(List(a, c, ASymbol, BSymbol), x), Not(IntegerQ(p)),
                      RationalQ(m, p, q)))),
          IIntegrate(1953,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Plus(A_, Times(B_DEFAULT,
                          Power(x_, j_DEFAULT))),
                      Power(
                          Plus(
                              Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                              Times(a_DEFAULT, Power(x_, q_DEFAULT)),
                              Times(c_DEFAULT, Power(x_, r_DEFAULT))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(x, Times(C1D2,
                              q)),
                          Sqrt(
                              Plus(a, Times(b, Power(x, Subtract(n, q))),
                                  Times(c, Power(x, Times(C2, Subtract(n, q)))))),
                          Power(Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                              Times(c, Power(x, Subtract(Times(C2, n), q)))), CN1D2)),
                      Integrate(
                          Times(Power(x, Subtract(m, Times(C1D2, q))),
                              Plus(ASymbol, Times(BSymbol, Power(x, Subtract(n, q)))),
                              Power(Plus(a, Times(b, Power(x, Subtract(n, q))),
                                  Times(c, Power(x, Times(C2, Subtract(n, q))))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol, m, n, q), x), EqQ(j, Subtract(n, q)),
                      EqQ(r, Subtract(Times(C2,
                          n), q)),
                      PosQ(Subtract(n, q)), Or(EqQ(m, C1D2), EqQ(m, Negate(C1D2))), EqQ(n,
                          C3),
                      EqQ(q, C1)))),
          IIntegrate(1954,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(Times(a_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, k_DEFAULT)), Times(c_DEFAULT,
                                  Power(x_, n_DEFAULT))),
                          p_),
                      Plus(A_, Times(B_DEFAULT, Power(x_, q_)))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus(
                                  Times(a, Power(x, j)), Times(b, Power(x,
                                      k)),
                                  Times(c, Power(x, n))),
                              p),
                          Power(
                              Times(Power(x, Times(j, p)),
                                  Power(Plus(a, Times(b, Power(x, Subtract(k, j))),
                                      Times(c, Power(x, Times(C2, Subtract(k, j))))), p)),
                              CN1)),
                      Integrate(Times(Power(x, Plus(m, Times(j, p))),
                          Plus(ASymbol, Times(BSymbol, Power(x, Subtract(k, j)))),
                          Power(Plus(a, Times(b, Power(x, Subtract(k, j))),
                              Times(c, Power(x, Times(C2, Subtract(k, j))))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol, j, k, m, p), x), EqQ(q, Subtract(k,
                      j)), EqQ(n, Subtract(Times(C2, k), j)), Not(
                          IntegerQ(p)),
                      PosQ(Subtract(k, j))))),
          IIntegrate(1955,
              Integrate(
                  Times(Power(u_, m_DEFAULT), Plus(A_, Times(B_DEFAULT, Power(u_, j_DEFAULT))),
                      Power(
                          Plus(Times(b_DEFAULT, Power(u_, n_DEFAULT)),
                              Times(a_DEFAULT, Power(u_, q_DEFAULT)), Times(c_DEFAULT,
                                  Power(u_, r_DEFAULT))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coefficient(u, x,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Times(Power(x, m),
                                  Plus(ASymbol, Times(BSymbol, Power(x, Subtract(n, q)))),
                                  Power(
                                      Plus(Times(a, Power(x, q)), Times(b, Power(x, n)),
                                          Times(c, Power(x, Subtract(Times(C2, n), q)))),
                                      p)),
                              x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol, m, n, p, q), x), EqQ(j, Subtract(n,
                      q)), EqQ(r,
                          Subtract(Times(C2, n), q)),
                      LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(1956,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Times(
                              e_DEFAULT, Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                                  r_DEFAULT)),
                          p_),
                      Power(
                          Times(
                              f_DEFAULT, Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_DEFAULT))),
                                  s_)),
                          q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(e, Power(Plus(a, Times(b, Power(x, n))), r)), p),
                          Power(Times(f,
                              Power(Plus(c, Times(d, Power(x, n))), s)), q),
                          Power(
                              Times(Power(Plus(a, Times(b, Power(x, n))), Times(p, r)), Power(
                                  Plus(c, Times(d, Power(x, n))), Times(q, s))),
                              CN1)),
                      Integrate(
                          Times(Power(x, m), Power(Plus(a, Times(b, Power(x, n))), Times(p, r)),
                              Power(Plus(c, Times(d, Power(x, n))), Times(q, s))),
                          x),
                      x),
                  FreeQ(List(a, b, c, d, e, f, m, n, p, q, r, s), x))),
          IIntegrate(1957,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Times(e_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                              Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_DEFAULT))), CN1)),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(b, e, Power(d, CN1)), p), Integrate(u, x), x), And(
                      FreeQ(List(a, b, c, d, e, n, p), x), EqQ(Subtract(Times(b, c), Times(a, d)),
                          C0)))),
          IIntegrate(1958,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Times(
                              e_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                              Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_DEFAULT))), CN1)),
                          p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          u, Power(Times(e, Plus(a, Times(b, Power(x, n)))),
                              p),
                          Power(Power(Plus(c, Times(d, Power(x, n))), p), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), GtQ(Times(b, d,
                      e), C0), GtQ(Subtract(c, Times(a, d, Power(b, CN1))),
                          C0)))),
          IIntegrate(1959,
              Integrate(
                  Power(
                      Times(
                          e_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_,
                              n_DEFAULT))),
                          Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_DEFAULT))), CN1)),
                      p_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Denominator(p))),
                      Dist(
                          Times(q, e, Subtract(Times(b, c), Times(a, d)), Power(n,
                              CN1)),
                          Subst(
                              Integrate(Times(Power(x, Subtract(Times(q, Plus(p, C1)), C1)),
                                  Power(Plus(Times(CN1, a, e), Times(c, Power(x, q))),
                                      Subtract(Power(n, CN1), C1)),
                                  Power(Power(Subtract(Times(b, e), Times(d, Power(x, q))),
                                      Plus(Power(n, CN1), C1)), CN1)),
                                  x),
                              x,
                              Power(
                                  Times(e, Plus(a, Times(b, Power(x, n))), Power(
                                      Plus(c, Times(d, Power(x, n))), CN1)),
                                  Power(q, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), FractionQ(p), IntegerQ(Power(n, CN1))))),
          IIntegrate(1960,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Times(
                              e_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_,
                                  n_DEFAULT))),
                              Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_DEFAULT))), CN1)),
                          p_)),
                  x_Symbol),
              Condition(
                  With(List(Set(q, Denominator(p))),
                      Dist(Times(q, e, Subtract(Times(b, c), Times(a, d)), Power(n, CN1)),
                          Subst(
                              Integrate(Times(Power(x, Subtract(Times(q, Plus(p, C1)), C1)),
                                  Power(Plus(Times(CN1, a, e), Times(c, Power(x, q))),
                                      Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                                  Power(
                                      Power(Subtract(Times(b, e), Times(d, Power(x, q))),
                                          Plus(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                                      CN1)),
                                  x),
                              x,
                              Power(Times(e, Plus(a, Times(b, Power(x, n))),
                                  Power(Plus(c, Times(d, Power(x, n))), CN1)), Power(q, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), FractionQ(p),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))));
}
