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
class IntRules108 {
  public static IAST RULES =
      List(
          IIntegrate(2161, Integrate(Times(Power(u_, CN1), Power(v_, CN1D2)), x_Symbol),
              Condition(
                  With(List(Set(a, Simplify(D(u, x))), Set(b, Simplify(D(v, x)))),
                      Condition(Simp(Times(C2, ArcTan(Times(Sqrt(v),
                          Power(Rt(Times(Subtract(Times(b, u), Times(a, v)), Power(a, CN1)), C2),
                              CN1))),
                          Power(
                              Times(a,
                                  Rt(Times(Subtract(Times(b, u), Times(a, v)), Power(a, CN1)), C2)),
                              CN1)),
                          x),
                          And(NeQ(Subtract(Times(b, u), Times(a, v)), C0), PosQ(Times(Subtract(
                              Times(b, u), Times(a, v)), Power(a, CN1)))))),
                  PiecewiseLinearQ(u, v, x))),
          IIntegrate(2162, Integrate(Times(Power(u_, CN1), Power(v_, CN1D2)), x_Symbol),
              Condition(With(List(Set(a, Simplify(D(u, x))), Set(b, Simplify(D(v, x)))),
                  Condition(Simp(Times(CN2, ArcTanh(Times(Sqrt(v),
                      Power(Rt(Times(CN1, Subtract(Times(b, u), Times(a, v)), Power(a, CN1)), C2),
                          CN1))),
                      Power(
                          Times(a,
                              Rt(Times(CN1, Subtract(Times(b, u), Times(a, v)), Power(a, CN1)),
                                  C2)),
                          CN1)),
                      x),
                      And(NeQ(Subtract(Times(b, u), Times(a,
                          v)), C0), NegQ(Times(Subtract(Times(b, u), Times(a, v)),
                              Power(a, CN1)))))),
                  PiecewiseLinearQ(u, v, x))),
          IIntegrate(
              2163, Integrate(Times(Power(u_, CN1),
                  Power(v_, n_)), x_Symbol),
              Condition(
                  With(
                      List(Set(a, Simplify(
                          D(u, x))), Set(b,
                              Simplify(D(v, x)))),
                      Condition(
                          Subtract(
                              Simp(Times(
                                  Power(v, Plus(n, C1)),
                                  Power(Times(Plus(n, C1), Subtract(Times(b, u), Times(a, v))),
                                      CN1)),
                                  x),
                              Dist(
                                  Times(a, Plus(n, C1),
                                      Power(Times(Plus(n, C1), Subtract(Times(b, u), Times(a, v))),
                                          CN1)),
                                  Integrate(Times(Power(v, Plus(n, C1)), Power(u, CN1)), x), x)),
                          NeQ(Subtract(Times(b, u), Times(a, v)), C0))),
                  And(PiecewiseLinearQ(u, v, x), LtQ(n, CN1)))),
          IIntegrate(2164, Integrate(Times(Power(u_, CN1), Power(v_, n_)), x_Symbol),
              Condition(With(List(Set(a, Simplify(D(u, x))), Set(b, Simplify(D(v, x)))),
                  Condition(
                      Simp(
                          Times(
                              Power(v, Plus(n, C1)), Hypergeometric2F1(C1, Plus(n, C1), Plus(n, C2),
                                  Times(CN1, a, v, Power(Subtract(Times(b, u), Times(a, v)), CN1))),
                              Power(Times(Plus(n, C1), Subtract(Times(b, u), Times(a, v))), CN1)),
                          x),
                      NeQ(Subtract(Times(b, u), Times(a, v)), C0))),
                  And(PiecewiseLinearQ(u, v, x), Not(IntegerQ(n))))),
          IIntegrate(
              2165, Integrate(Times(Power(u_, CN1D2),
                  Power(v_, CN1D2)), x_Symbol),
              Condition(
                  With(
                      List(Set(a, Simplify(D(u, x))), Set(b,
                          Simplify(D(v, x)))),
                      Condition(
                          Simp(
                              Times(C2,
                                  ArcTanh(
                                      Times(Rt(Times(a, b), C2), Sqrt(u),
                                          Power(Times(a, Sqrt(v)), CN1))),
                                  Power(Rt(Times(a, b), C2), CN1)),
                              x),
                          And(NeQ(Subtract(Times(b, u), Times(a, v)), C0), PosQ(Times(a, b))))),
                  PiecewiseLinearQ(u, v, x))),
          IIntegrate(
              2166, Integrate(Times(Power(u_, CN1D2),
                  Power(v_, CN1D2)), x_Symbol),
              Condition(
                  With(
                      List(Set(a, Simplify(D(u, x))), Set(b,
                          Simplify(D(v, x)))),
                      Condition(
                          Simp(
                              Times(C2,
                                  ArcTan(
                                      Times(Rt(Times(CN1, a, b), C2), Sqrt(u),
                                          Power(Times(a, Sqrt(v)), CN1))),
                                  Power(Rt(Times(CN1, a, b), C2), CN1)),
                              x),
                          And(NeQ(Subtract(Times(b, u), Times(a, v)), C0), NegQ(Times(a, b))))),
                  PiecewiseLinearQ(u, v, x))),
          IIntegrate(
              2167, Integrate(Times(Power(u_, m_),
                  Power(v_, n_)), x_Symbol),
              Condition(
                  With(
                      List(Set(a, Simplify(
                          D(u, x))), Set(b,
                              Simplify(D(v, x)))),
                      Condition(
                          Negate(
                              Simp(Times(Power(u, Plus(m, C1)), Power(v, Plus(n, C1)),
                                  Power(Times(Plus(m, C1), Subtract(Times(b, u), Times(a, v))),
                                      CN1)),
                                  x)),
                          NeQ(Subtract(Times(b, u), Times(a, v)), C0))),
                  And(FreeQ(List(m, n), x), PiecewiseLinearQ(u, v, x), EqQ(Plus(m, n, C2), C0),
                      NeQ(m, CN1)))),
          IIntegrate(
              2168, Integrate(Times(Power(u_, m_),
                  Power(v_, n_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(Set(a, Simplify(
                          D(u, x))), Set(b,
                              Simplify(D(v, x)))),
                      Condition(
                          Subtract(
                              Simp(
                                  Times(Power(u, Plus(m, C1)), Power(v, n),
                                      Power(Times(a, Plus(m, C1)), CN1)),
                                  x),
                              Dist(Times(b, n, Power(Times(a, Plus(m, C1)), CN1)),
                                  Integrate(Times(Power(u, Plus(m, C1)), Power(v, Subtract(n, C1))),
                                      x),
                                  x)),
                          NeQ(Subtract(Times(b, u), Times(a, v)), C0))),
                  And(FreeQ(List(m,
                      n), x), PiecewiseLinearQ(u, v, x), NeQ(m, CN1), Or(
                          And(LtQ(m, CN1), GtQ(n, C0),
                              Not(And(
                                  ILtQ(Plus(m, n), CN2),
                                  Or(FractionQ(m), GeQ(Plus(Times(C2, n), m, C1), C0))))),
                          And(IGtQ(n, C0), IGtQ(m, C0), LeQ(n, m)),
                          And(IGtQ(n,
                              C0), Not(IntegerQ(m))),
                          And(ILtQ(m, C0), Not(IntegerQ(n))))))),
          IIntegrate(2169, Integrate(Times(Power(u_, m_), Power(v_, n_DEFAULT)), x_Symbol),
              Condition(
                  With(List(Set(a, Simplify(D(u, x))), Set(b, Simplify(D(v, x)))),
                      Condition(
                          Subtract(
                              Simp(Times(Power(u, Plus(m, C1)), Power(v, n),
                                  Power(Times(a, Plus(m, n, C1)), CN1)), x),
                              Dist(
                                  Times(n, Subtract(Times(b, u), Times(a, v)),
                                      Power(Times(a, Plus(m, n, C1)), CN1)),
                                  Integrate(Times(Power(u, m), Power(v, Subtract(n, C1))), x), x)),
                          NeQ(Subtract(Times(b, u), Times(a, v)), C0))),
                  And(PiecewiseLinearQ(u, v, x), NeQ(Plus(m, n, C2), C0), GtQ(n, C0),
                      NeQ(Plus(m, n, C1), C0),
                      Not(And(IGtQ(m, C0), Or(Not(IntegerQ(
                          n)), LtQ(C0, m,
                              n)))),
                      Not(ILtQ(Plus(m, n), CN2))))),
          IIntegrate(
              2170, Integrate(Times(Power(u_, m_),
                  Power(v_, n_)), x_Symbol),
              Condition(
                  With(
                      List(Set(a, Simplify(D(u, x))), Set(b,
                          Simplify(D(v, x)))),
                      Condition(
                          Subtract(
                              Simp(Times(Power(u, Plus(m, C1)), Power(v, n),
                                  Power(Times(a, Plus(m, n, C1)), CN1)), x),
                              Dist(
                                  Times(n, Subtract(Times(b, u), Times(a, v)), Power(
                                      Times(a, Plus(m, n, C1)), CN1)),
                                  Integrate(Times(Power(u, m), Power(v, Simplify(Subtract(n, C1)))),
                                      x),
                                  x)),
                          NeQ(Subtract(Times(b, u), Times(a, v)), C0))),
                  And(PiecewiseLinearQ(u, v, x), NeQ(Plus(m, n,
                      C1), C0), Not(
                          RationalQ(n)),
                      SumSimplerQ(n, CN1)))),
          IIntegrate(
              2171, Integrate(Times(Power(u_, m_),
                  Power(v_, n_)), x_Symbol),
              Condition(
                  With(
                      List(Set(a, Simplify(
                          D(u, x))), Set(b,
                              Simplify(D(v, x)))),
                      Condition(Plus(Negate(Simp(
                          Times(Power(u, Plus(m, C1)), Power(v, Plus(n, C1)),
                              Power(Times(Plus(m, C1), Subtract(Times(b, u), Times(a, v))), CN1)),
                          x)), Dist(
                              Times(b, Plus(m, n, C2),
                                  Power(Times(Plus(m, C1), Subtract(Times(b, u), Times(a, v))),
                                      CN1)),
                              Integrate(Times(Power(u, Plus(m, C1)), Power(v, n)), x), x)),
                          NeQ(Subtract(Times(b, u), Times(a, v)), C0))),
                  And(PiecewiseLinearQ(u, v, x), NeQ(Plus(m, n, C2), C0), LtQ(m, CN1)))),
          IIntegrate(
              2172, Integrate(Times(Power(u_, m_),
                  Power(v_, n_)), x_Symbol),
              Condition(
                  With(
                      List(Set(a, Simplify(
                          D(u, x))), Set(b,
                              Simplify(D(v, x)))),
                      Condition(
                          Plus(
                              Negate(Simp(Times(Power(u, Plus(m, C1)), Power(v, Plus(n, C1)),
                                  Power(Times(Plus(m, C1), Subtract(Times(b, u), Times(a, v))),
                                      CN1)),
                                  x)),
                              Dist(
                                  Times(b, Plus(m, n, C2),
                                      Power(Times(Plus(m, C1), Subtract(Times(b, u), Times(a, v))),
                                          CN1)),
                                  Integrate(Times(Power(u, Simplify(Plus(m, C1))), Power(v, n)), x),
                                  x)),
                          NeQ(Subtract(Times(b, u), Times(a, v)), C0))),
                  And(PiecewiseLinearQ(u, v, x), Not(RationalQ(m)), SumSimplerQ(m, C1)))),
          IIntegrate(
              2173, Integrate(Times(Power(u_, m_),
                  Power(v_, n_)), x_Symbol),
              Condition(
                  With(
                      List(Set(a, Simplify(D(u,
                          x))), Set(b, Simplify(
                              D(v, x)))),
                      Condition(
                          Simp(Times(Power(u, m), Power(v, Plus(n, C1)),
                              Hypergeometric2F1(Negate(m), Plus(n, C1), Plus(n, C2),
                                  Times(CN1, a, v, Power(Subtract(Times(b, u), Times(a, v)), CN1))),
                              Power(
                                  Times(b, Plus(n, C1),
                                      Power(Times(b, u,
                                          Power(Subtract(Times(b, u), Times(a, v)), CN1)), m)),
                                  CN1)),
                              x),
                          NeQ(Subtract(Times(b, u), Times(a, v)), C0))),
                  And(PiecewiseLinearQ(u, v, x), Not(IntegerQ(m)), Not(IntegerQ(n))))),
          IIntegrate(2174,
              Integrate(Times(Log(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                  Power(u_, n_DEFAULT)), x_Symbol),
              Condition(
                  With(List(Set(c, Simplify(D(u, x)))),
                      Plus(
                          Simp(Times(Power(u, n), Plus(a, Times(b, x)), Log(Plus(a, Times(b, x))),
                              Power(b, CN1)), x),
                          Negate(Dist(Times(c, n, Power(b, CN1)),
                              Integrate(Times(Power(u, Subtract(n, C1)), Plus(a, Times(b, x)),
                                  Log(Plus(a, Times(b, x)))), x),
                              x)),
                          Negate(Integrate(Power(u, n), x)))),
                  And(FreeQ(List(a, b), x), PiecewiseLinearQ(u, x), Not(LinearQ(u,
                      x)), GtQ(n,
                          C0)))),
          IIntegrate(2175,
              Integrate(
                  Times(
                      Log(Plus(a_DEFAULT, Times(b_DEFAULT,
                          x_))),
                      Power(u_, n_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set(c, Simplify(D(u, x)))),
                      Plus(
                          Simp(Times(Power(u, n), Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Log(Plus(a, Times(b, x))), Power(Times(b, Plus(m, C1)), CN1)), x),
                          Negate(Dist(Power(Plus(m, C1), CN1),
                              Integrate(Times(Power(u, n), Power(Plus(a, Times(b, x)), m)), x), x)),
                          Negate(
                              Dist(Times(c, n, Power(Times(b, Plus(m, C1)), CN1)),
                                  Integrate(Times(Power(u, Subtract(n, C1)),
                                      Power(Plus(a, Times(b, x)),
                                          Plus(m, C1)),
                                      Log(Plus(a, Times(b, x)))), x),
                                  x)))),
                  And(FreeQ(List(a, b, m), x), PiecewiseLinearQ(u, x), Not(LinearQ(u,
                      x)), GtQ(n,
                          C0),
                      NeQ(m, CN1)))),
          IIntegrate(2176,
              Integrate(
                  Times(
                      Power(
                          Times(
                              b_DEFAULT, Power(F_, Times(g_DEFAULT,
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Times(b,
                                  Power(FSymbol, Times(g, Plus(e, Times(f, x))))), n),
                              Power(Times(f, g, n, Log(FSymbol)), CN1)),
                          x),
                      Dist(
                          Times(d, m, Power(Times(f, g, n, Log(FSymbol)),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                                  Power(Times(b, Power(FSymbol, Times(g, Plus(e, Times(f, x))))),
                                      n)),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, b, c, d, e, f, g,
                      n), x), GtQ(m,
                          C0),
                      IntegerQ(Times(C2, m)), Not(SameQ(False, True))))),
          IIntegrate(2177,
              Integrate(
                  Times(
                      Power(
                          Times(
                              b_DEFAULT, Power(F_, Times(g_DEFAULT,
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(c, Times(d,
                                  x)), Plus(m,
                                      C1)),
                              Power(Times(b,
                                  Power(FSymbol, Times(g, Plus(e, Times(f, x))))), n),
                              Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(f, g, n, Log(FSymbol), Power(Times(d, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), Plus(m, C1)),
                                  Power(Times(b, Power(FSymbol, Times(g, Plus(e, Times(f, x))))),
                                      n)),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, b, c, d, e, f, g, n), x), LtQ(m, CN1),
                      IntegerQ(Times(C2, m)), Not(SameQ(False, True))))),
          IIntegrate(2178,
              Integrate(Times(Power(F_, Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1)), x_Symbol),
              Condition(
                  Simp(
                      Times(Power(FSymbol, Times(g, Subtract(e, Times(c, f, Power(d, CN1))))),
                          ExpIntegralEi(Times(f, g, Plus(c, Times(d, x)), Log(FSymbol),
                              Power(d, CN1))),
                          Power(d, CN1)),
                      x),
                  And(FreeQ(List(FSymbol, c, d, e, f, g), x), Not(SameQ(False, True))))),
          IIntegrate(2179,
              Integrate(
                  Times(Power(F_, Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), Power(
                      Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Negate(d), m),
                          Power(FSymbol, Times(g, Subtract(e, Times(c, f, Power(d, CN1))))),
                          Gamma(
                              Plus(m, C1), Times(CN1, f, g, Log(FSymbol), Plus(c, Times(d, x)),
                                  Power(d, CN1))),
                          Power(
                              Times(
                                  Power(f, Plus(m, C1)), Power(g, Plus(m, C1)), Power(Log(FSymbol),
                                      Plus(m, C1))),
                              CN1)),
                      x),
                  And(FreeQ(List(FSymbol, c, d, e, f, g), x), IntegerQ(m)))),
          IIntegrate(2180,
              Integrate(Times(Power(F_, Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D2)), x_Symbol),
              Condition(
                  Dist(Times(C2, Power(d, CN1)),
                      Subst(
                          Integrate(Power(FSymbol,
                              Plus(Times(g, Subtract(e, Times(c, f, Power(d, CN1)))),
                                  Times(f, g, Sqr(x), Power(d, CN1)))),
                              x),
                          x, Sqrt(Plus(c, Times(d, x)))),
                      x),
                  And(FreeQ(List(FSymbol, c, d, e, f, g), x), Not(SameQ(False, True))))));
}
