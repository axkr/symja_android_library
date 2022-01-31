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
class IntRules336 {
  public static IAST RULES =
      List(
          IIntegrate(6720,
              Integrate(
                  Times(u_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_))),
                      p_)),
                  x_Symbol),
              Condition(Dist(
                  Times(Power(b, IntPart(p)), Power(Plus(a, Times(b, Power(x, n))), FracPart(p)),
                      Power(Times(Power(x, Times(n, FracPart(p))),
                          Power(Plus(C1, Times(a, Power(Times(Power(x, n), b), CN1))),
                              FracPart(p))),
                          CN1)),
                  Integrate(
                      Times(
                          u, Power(x, Times(n, p)), Power(Plus(C1,
                              Times(a, Power(Times(Power(x, n), b), CN1))), p)),
                      x),
                  x),
                  And(FreeQ(List(a, b, p), x), Not(IntegerQ(
                      p)), ILtQ(n,
                          C0),
                      Not(RationalFunctionQ(u, x)), IntegerQ(Plus(p, C1D2))))),
          IIntegrate(6721,
              Integrate(Times(u_DEFAULT,
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(v_, n_))), p_)), x_Symbol),
              Condition(Dist(Times(Power(Plus(a, Times(b, Power(v, n))), FracPart(p)),
                  Power(Times(Power(v, Times(n, FracPart(p))),
                      Power(Plus(b, Times(a, Power(Power(v, n), CN1))), FracPart(p))), CN1)),
                  Integrate(
                      Times(u, Power(v, Times(n, p)),
                          Power(Plus(b, Times(a, Power(Power(v, n), CN1))), p)),
                      x),
                  x),
                  And(FreeQ(List(a, b, p), x), Not(IntegerQ(
                      p)), ILtQ(n,
                          C0),
                      BinomialQ(v, x), Not(LinearQ(v, x))))),
          IIntegrate(6722,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(v_, n_),
                              Power(x_, m_DEFAULT))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, Power(x, m), Power(v, n))), FracPart(p)),
                          Power(
                              Times(Power(v, Times(n, FracPart(p))),
                                  Power(Plus(Times(b, Power(x, m)),
                                      Times(a, Power(Power(v, n), CN1))), FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(u, Power(v, Times(n, p)),
                              Power(Plus(Times(b, Power(x, m)), Times(a, Power(Power(v, n), CN1))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, m, p), x), Not(IntegerQ(p)), ILtQ(n, C0), BinomialQ(v, x)))),
          IIntegrate(6723,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              Times(a_DEFAULT, Power(x_, r_DEFAULT)), Times(b_DEFAULT,
                                  Power(x_, s_DEFAULT))),
                          m_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(v,
                              Times(
                                  Power(
                                      Plus(Times(a, Power(x, r)), Times(b,
                                          Power(x, s))),
                                      FracPart(m)),
                                  Power(
                                      Times(Power(x, Times(r, FracPart(m))),
                                          Power(Plus(a, Times(b, Power(x, Subtract(s, r)))),
                                              FracPart(m))),
                                      CN1)))),
                      Condition(
                          Dist(v,
                              Integrate(
                                  Times(
                                      u, Power(x, Times(m, r)), Power(Plus(a,
                                          Times(b, Power(x, Subtract(s, r)))), m)),
                                  x),
                              x),
                          NeQ(Simplify(v), C1))),
                  And(FreeQ(List(a, b, m, r, s), x), Not(IntegerQ(m)), PosQ(Subtract(s, r))))),
          IIntegrate(6724,
              Integrate(Times(u_,
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), CN1)), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(v,
                              RationalFunctionExpand(Times(u,
                                  Power(Plus(a, Times(b, Power(x, n))), CN1)), x))),
                      Condition(Integrate(v, x), SumQ(v))),
                  And(FreeQ(List(a, b), x), IGtQ(n, C0)))),
          IIntegrate(6725,
              Integrate(
                  Times(u_,
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(c_DEFAULT,
                                  Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(Power(C4, p), Power(c, p)), CN1),
                      Integrate(Times(u, Power(Plus(b, Times(C2, c, Power(x, n))), Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, n), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      IntegerQ(p), Not(AlgebraicFunctionQ(u, x))))),
          IIntegrate(6726,
              Integrate(Times(u_,
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(c_DEFAULT, Power(x_, $p("n2", true)))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(a, Times(b, Power(x, n)), Times(c,
                              Power(x, Times(C2, n)))), p),
                          Power(Power(Plus(b, Times(C2, c, Power(x, n))), Times(C2, p)), CN1)),
                      Integrate(Times(u, Power(Plus(b, Times(C2, c, Power(x, n))), Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      Not(IntegerQ(p)), Not(AlgebraicFunctionQ(u, x))))),
          IIntegrate(6727,
              Integrate(
                  Times(u_,
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(x_,
                                  n_DEFAULT)),
                              Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(v,
                              RationalFunctionExpand(Times(u,
                                  Power(Plus(a, Times(b, Power(x, n)),
                                      Times(c, Power(x, Times(C2, n)))), CN1)),
                                  x))),
                      Condition(Integrate(v, x), SumQ(v))),
                  And(FreeQ(List(a, b, c), x), EqQ($s("n2"), Times(C2, n)), IGtQ(n, C0)))),
          IIntegrate(6728,
              Integrate(
                  Times(u_DEFAULT,
                      Power(Plus(Times(a_DEFAULT, Power(x_, m_DEFAULT)),
                          Times(b_DEFAULT, Sqrt(Times(c_DEFAULT, Power(x_, n_))))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(Times(u,
                      Subtract(Times(a, Power(x, m)), Times(b, Sqrt(Times(c, Power(x, n))))),
                      Power(Subtract(Times(Sqr(a), Power(x, Times(C2, m))),
                          Times(Sqr(b), c, Power(x, n))), CN1)),
                      x),
                  FreeQ(List(a, b, c, m, n), x))),
          IIntegrate(6729, Integrate(u_, x_Symbol),
              With(List(Set($s("lst"), FunctionOfLinear(u, x))),
                  Condition(
                      Dist(
                          Power(Part($s("lst"), C3), CN1), Subst(Integrate(Part($s("lst"), C1), x),
                              x, Plus(Part($s("lst"), C2), Times(Part($s("lst"), C3), x))),
                          x),
                      Not(FalseQ($s("lst")))))),
          IIntegrate(6730, Integrate(Times(u_, Power(x_, CN1)), x_Symbol),
              Condition(
                  With(List(Set($s("lst"), PowerVariableExpn(u, C0, x))),
                      Condition(Dist(Power(Part($s("lst"), C2), CN1),
                          Subst(Integrate(NormalizeIntegrand(
                              Simplify(Times(Part($s("lst"), C1), Power(x, CN1))), x), x), x, Power(
                                  Times(Part($s("lst"), C3), x), Part($s("lst"), C2))),
                          x), And(Not(FalseQ($s("lst"))), NeQ(Part($s("lst"), C2), C0)))),
                  And(NonsumQ(u), Not(RationalFunctionQ(u, x))))),
          IIntegrate(6731, Integrate(Times(u_, Power(x_, m_DEFAULT)), x_Symbol),
              Condition(
                  With(List(Set($s("lst"), PowerVariableExpn(u, Plus(m, C1), x))),
                      Condition(Dist(Power(Part($s("lst"), C2), CN1),
                          Subst(Integrate(NormalizeIntegrand(
                              Simplify(Times(Part($s("lst"), C1), Power(x, CN1))), x), x), x, Power(
                                  Times(Part($s("lst"), C3), x), Part($s("lst"), C2))),
                          x),
                          And(Not(FalseQ($s("lst"))), NeQ(Part($s("lst"), C2), Plus(m, C1))))),
                  And(IntegerQ(m), NeQ(m, CN1), NonsumQ(u),
                      Or(GtQ(m, C0), Not(AlgebraicFunctionQ(u, x)))))),
          IIntegrate(6732, Integrate(Times(u_, Power(x_, m_)), x_Symbol),
              Condition(
                  With(List(Set(k, Denominator(m))), Dist(k,
                      Subst(
                          Integrate(Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                              ReplaceAll(u, Rule(x, Power(x, k)))), x),
                          x, Power(x, Power(k, CN1))),
                      x)),
                  FractionQ(m))),
          IIntegrate(6733, Integrate(u_, x_Symbol),
              Condition(With(List(Set($s("lst"), FunctionOfSquareRootOfQuadratic(u, x))),
                  Condition(
                      Dist(C2, Subst(Integrate(Part($s("lst"), C1), x), x, Part($s("lst"), C2)), x),
                      And(Not(FalseQ($s("lst"))), EqQ(Part($s("lst"), C3), C1)))),
                  EulerIntegrandQ(u, x))),
          IIntegrate(6734, Integrate(u_, x_Symbol),
              Condition(
                  With(List(Set($s("lst"), FunctionOfSquareRootOfQuadratic(u, x))),
                      Condition(Dist(C2, Subst(Integrate(Part($s("lst"), C1), x), x,
                          Part($s("lst"), C2)), x), And(Not(FalseQ($s("lst"))),
                              EqQ(Part($s("lst"), C3), C2)))),
                  EulerIntegrandQ(u, x))),
          IIntegrate(6735, Integrate(u_, x_Symbol),
              Condition(
                  With(
                      List(Set($s("lst"),
                          FunctionOfSquareRootOfQuadratic(u, x))),
                      Condition(
                          Dist(
                              C2, Subst(Integrate(Part($s("lst"), C1), x), x,
                                  Part($s("lst"), C2)),
                              x),
                          And(Not(FalseQ($s("lst"))), EqQ(Part($s("lst"), C3), C3)))),
                  EulerIntegrandQ(u, x))),
          IIntegrate(
              6736, Integrate(Power(Plus(a_, Times(b_DEFAULT, Sqr(v_))),
                  CN1), x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Power(Times(C2,
                              a), CN1),
                          Integrate(
                              Together(
                                  Power(
                                      Subtract(C1,
                                          Times(v,
                                              Power(Rt(Times(CN1, a, Power(b, CN1)), C2), CN1))),
                                      CN1)),
                              x),
                          x),
                      Dist(Power(Times(C2, a), CN1),
                          Integrate(
                              Together(
                                  Power(
                                      Plus(C1,
                                          Times(v,
                                              Power(Rt(Times(CN1, a, Power(b, CN1)), C2), CN1))),
                                      CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b), x))),
          IIntegrate(6737,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(v_, n_))),
                  CN1), x_Symbol),
              Condition(
                  Dist(
                      Times(C2, Power(Times(a, n),
                          CN1)),
                      Sum(Integrate(
                          Together(
                              Power(
                                  Subtract(C1,
                                      Times(Sqr(v),
                                          Power(
                                              Times(
                                                  Power(CN1, Times(C4, k, Power(n, CN1))), Rt(
                                                      Times(CN1, a, Power(b, CN1)),
                                                      Times(C1D2, n))),
                                              CN1))),
                                  CN1)),
                          x), List(k, C1, Times(C1D2, n))),
                      x),
                  And(FreeQ(List(a, b), x), IGtQ(Times(C1D2, n), C1)))),
          IIntegrate(6738,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, Power(v_, n_))),
                  CN1), x_Symbol),
              Condition(
                  Dist(
                      Power(Times(a,
                          n), CN1),
                      Sum(Integrate(
                          Together(Power(
                              Subtract(C1,
                                  Times(v,
                                      Power(Times(Power(CN1, Times(C2, k, Power(n, CN1))),
                                          Rt(Times(CN1, a, Power(b, CN1)), n)), CN1))),
                              CN1)),
                          x), List(k, C1, n)),
                      x),
                  And(FreeQ(List(a, b), x), IGtQ(Times(C1D2, Subtract(n, C1)), C0)))),
          IIntegrate(6739,
              Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, Power(u_, n_DEFAULT))), CN1), v_),
                  x_Symbol),
              Condition(
                  Integrate(ReplaceAll(ExpandIntegrand(
                      Times(PolynomialInSubst(v, u, x), Power(Plus(a, Times(b, Power(x, n))), CN1)),
                      x), Rule(x, u)), x),
                  And(FreeQ(List(a, b), x), IGtQ(n, C0), PolynomialInQ(v, u, x)))));
}
