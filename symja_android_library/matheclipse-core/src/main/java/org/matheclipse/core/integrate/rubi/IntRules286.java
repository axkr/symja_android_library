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
class IntRules286 {
  public static IAST RULES =
      List(
          IIntegrate(5721,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(d, CN1),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(Plus(a, Times(b, x)), n), Power(Times(Cosh(x), Sinh(x)),
                                          CN1)),
                                  x),
                              x, ArcCosh(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(n, C0)))),
          IIntegrate(5722,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))),
                                  n),
                              Power(Times(d, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, c, n, Power(Negate(
                              d), p), Power(Times(f, Plus(m, C1)),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(C1, Times(c, x)), Plus(p, C1D2)),
                                  Power(Plus(CN1, Times(c, x)), Plus(p,
                                      C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), GtQ(n, C0), EqQ(Plus(m, Times(C2, p),
                          C3), C0),
                      NeQ(m, CN1), IntegerQ(p)))),
          IIntegrate(5723,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                              Power(Plus(a,
                                  Times(b, ArcSinh(Times(c, x)))), n),
                              Power(Times(d, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, c, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(Times(f, Plus(m, C1),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)), Power(Plus(
                                      a, Times(b, ArcSinh(Times(c, x)))),
                                      Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), EqQ(e, Times(Sqr(
                      c), d)), GtQ(n, C0), EqQ(Plus(m, Times(C2, p),
                          C3), C0),
                      NeQ(m, CN1)))),
          IIntegrate(5724,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus($p("d1"), Times($p("e1", true),
                          x_)), p_DEFAULT),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus($s("d1"), Times($s("e1"), x)), Plus(p, C1)),
                              Power(Plus($s("d2"), Times($s("e2"), x)), Plus(p, C1)),
                              Power(Plus(a,
                                  Times(b, ArcCosh(Times(c, x)))), n),
                              Power(Times($s("d1"), $s("d2"), f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, c, n, Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                              Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                              Power(Plus($s("d2"), Times($s("e2"), x)), FracPart(p)),
                              Power(Times(f, Plus(m, C1), Power(Plus(C1, Times(c, x)), FracPart(p)),
                                  Power(Plus(CN1, Times(c, x)), FracPart(p))), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(CN1, Times(Sqr(c), Sqr(x))), Plus(p,
                                      C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, m, p), x),
                      EqQ(Subtract($s("e1"), Times(c,
                          $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), GtQ(n, C0), EqQ(Plus(m,
                          Times(C2, p), C3), C0),
                      NeQ(m, CN1), IntegerQ(Plus(p, C1D2))))),
          IIntegrate(5725,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus($p("d1"), Times($p("e1", true),
                          x_)), p_DEFAULT),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus($s("d1"), Times($s("e1"), x)), Plus(p, C1)),
                              Power(Plus($s("d2"), Times($s("e2"), x)), Plus(p, C1)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n), Power(
                                  Times($s("d1"), $s("d2"), f, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, c, n, Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                          Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                          Power(Plus($s("d2"), Times($s("e2"), x)), FracPart(p)),
                          Power(Times(f, Plus(m, C1), Power(Plus(C1, Times(c, x)), FracPart(p)),
                              Power(Plus(CN1, Times(c, x)), FracPart(p))), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(C1, Times(c, x)), Plus(p, C1D2)),
                                  Power(Plus(CN1, Times(c, x)), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, m, p), x),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c,
                          $s("d2"))), C0),
                      GtQ(n, C0), EqQ(Plus(m, Times(C2, p), C3), C0), NeQ(m, CN1)))),
          IIntegrate(5726,
              Integrate(Times(
                  Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)), Power(x_, CN1),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)), x_Symbol),
              Condition(Plus(
                  Simp(Times(Power(Plus(d, Times(e, Sqr(x))), p),
                      Plus(a, Times(b, ArcSinh(Times(c, x)))), Power(Times(C2, p), CN1)), x),
                  Dist(d,
                      Integrate(Times(Power(Plus(d, Times(e, Sqr(x))), Subtract(p, C1)),
                          Plus(a, Times(b, ArcSinh(Times(c, x)))), Power(x, CN1)), x),
                      x),
                  Negate(
                      Dist(Times(b, c, Power(d, p), Power(Times(C2, p), CN1)),
                          Integrate(Power(Plus(C1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), IGtQ(p, C0)))),
          IIntegrate(5727,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Plus(d, Times(e, Sqr(x))), p), Plus(a, Times(b,
                                  ArcCosh(Times(c, x)))),
                              Power(Times(C2, p), CN1)),
                          x),
                      Dist(d,
                          Integrate(Times(Power(Plus(d, Times(e, Sqr(x))), Subtract(p, C1)),
                              Plus(a, Times(b, ArcCosh(Times(c, x)))), Power(x, CN1)), x),
                          x),
                      Negate(Dist(Times(b, c, Power(Negate(d), p), Power(Times(C2, p), CN1)),
                          Integrate(Times(Power(Plus(C1, Times(c, x)), Subtract(p, C1D2)),
                              Power(Plus(CN1, Times(c, x)), Subtract(p, C1D2))), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(p,
                          C0)))),
          IIntegrate(5728,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSinh(
                          Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), p),
                              Plus(a, Times(b,
                                  ArcSinh(Times(c, x)))),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(Times(b, c, Power(d, p), Power(Times(f, Plus(m, C1)), CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Plus(m, C1)),
                                      Power(Plus(C1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2))),
                                  x),
                              x)),
                      Negate(Dist(Times(C2, e, p, Power(Times(Sqr(f), Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Times(f, x), Plus(m, C2)),
                              Power(Plus(d, Times(e, Sqr(x))), Subtract(p, C1)),
                              Plus(a, Times(b, ArcSinh(Times(c, x))))), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(e, Times(Sqr(c),
                      d)), IGtQ(p,
                          C0),
                      ILtQ(Times(C1D2, Plus(m, C1)), C0)))),
          IIntegrate(5729,
              Integrate(Times(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  Power(Times(f_DEFAULT, x_), m_),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)), x_Symbol),
              Condition(Plus(
                  Simp(Times(Power(Times(f, x), Plus(m, C1)), Power(Plus(d, Times(e, Sqr(x))), p),
                      Plus(a, Times(b, ArcCosh(Times(c, x)))), Power(Times(f, Plus(m, C1)), CN1)),
                      x),
                  Negate(
                      Dist(Times(b, c, Power(Negate(d), p), Power(Times(f, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(C1, Times(c, x)), Subtract(p, C1D2)),
                                  Power(Plus(CN1, Times(c, x)), Subtract(p, C1D2))),
                              x),
                          x)),
                  Negate(
                      Dist(
                          Times(C2, e, p, Power(Times(Sqr(f), Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(p,
                                      C1)),
                                  Plus(a, Times(b, ArcCosh(Times(c, x))))),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(p,
                          C0),
                      ILtQ(Times(C1D2, Plus(m, C1)), C0)))),
          IIntegrate(5730,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set(u,
                      IntHide(Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), p)),
                          x))),
                      Subtract(Dist(Plus(a, Times(b, ArcSinh(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u, Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(c), d)), IGtQ(p, C0)))),
          IIntegrate(5731,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCosh(
                          Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), p)),
                              x))),
                      Subtract(
                          Dist(Plus(a,
                              Times(b, ArcCosh(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(
                                  SimplifyIntegrand(Times(u,
                                      Power(
                                          Times(Sqrt(Plus(C1, Times(c, x))),
                                              Sqrt(Plus(CN1, Times(c, x)))),
                                          CN1)),
                                      x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(p,
                          C0)))),
          IIntegrate(5732,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(x, m),
                                      Power(Plus(C1, Times(Sqr(c), Sqr(x))), p)),
                                  x))),
                      Subtract(
                          Dist(Times(Power(d, p), Plus(a, Times(b, ArcSinh(Times(c, x))))), u, x),
                          Dist(Times(b, c, Power(d, p)),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u, Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)),
                      IntegerQ(Subtract(p, C1D2)), Or(
                          IGtQ(Times(C1D2, Plus(m, C1)),
                              C0),
                          ILtQ(Times(C1D2, Plus(m, Times(C2, p), C3)), C0)),
                      NeQ(p, Negate(C1D2)), GtQ(d, C0)))),
          IIntegrate(5733,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(x_, m_), Power(Plus($p("d1"), Times($p("e1", true), x_)), p_), Power(
                          Plus($p("d2"), Times($p("e2", true), x_)), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(
                                      Power(x, m), Power(Plus(C1,
                                          Times(c, x)), p),
                                      Power(Plus(CN1, Times(c, x)), p)),
                                  x))),
                      Subtract(
                          Dist(
                              Times(Power(Times(CN1, $s("d1"), $s("d2")), p),
                                  Plus(a, Times(b, ArcCosh(Times(c, x))))),
                              u, x),
                          Dist(Times(b, c, Power(Times(CN1, $s("d1"), $s("d2")), p)),
                              Integrate(SimplifyIntegrand(Times(u,
                                  Power(Times(Sqrt(Plus(C1, Times(c, x))),
                                      Sqrt(Plus(CN1, Times(c, x)))), CN1)),
                                  x), x),
                              x))),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2")), x),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), IntegerQ(Subtract(p,
                          C1D2)),
                      Or(IGtQ(Times(C1D2,
                          Plus(m, C1)), C0), ILtQ(Times(C1D2, Plus(m, Times(C2, p), C3)),
                              C0)),
                      NeQ(p, Negate(C1D2)), GtQ($s("d1"), C0), LtQ($s("d2"), C0)))),
          IIntegrate(5734,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(x, m),
                                      Power(Plus(C1, Times(Sqr(c), Sqr(x))), p)),
                                  x))),
                      Subtract(Dist(Plus(a, Times(b, ArcSinh(Times(c, x)))),
                          Integrate(Times(Power(x, m), Power(Plus(d, Times(e, Sqr(x))), p)), x), x),
                          Dist(
                              Times(b, c, Power(d, Subtract(p, C1D2)),
                                  Sqrt(Plus(d, Times(e, Sqr(x)))),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u, Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)),
                      IGtQ(Plus(p, C1D2), C0),
                      Or(IGtQ(Times(C1D2, Plus(m, C1)), C0),
                          ILtQ(Times(C1D2, Plus(m, Times(C2, p), C3)), C0))))),
          IIntegrate(5735,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(x_, m_), Power(Plus($p("d1"),
                          Times($p("e1", true), x_)), p_),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(
                                      Power(x, m), Power(Plus(C1,
                                          Times(c, x)), p),
                                      Power(Plus(CN1, Times(c, x)), p)),
                                  x))),
                      Subtract(
                          Dist(
                              Plus(a, Times(b,
                                  ArcCosh(Times(c, x)))),
                              Integrate(Times(Power(x, m),
                                  Power(Plus($s("d1"),
                                      Times($s("e1"), x)), p),
                                  Power(Plus($s("d2"), Times($s("e2"), x)), p)), x),
                              x),
                          Dist(
                              Times(b, c, Power(Times(CN1, $s("d1"), $s("d2")), Subtract(p, C1D2)),
                                  Sqrt(Plus($s("d1"), Times($s("e1"), x))),
                                  Sqrt(Plus($s("d2"),
                                      Times($s("e2"), x))),
                                  Power(
                                      Times(
                                          Sqrt(Plus(C1, Times(c, x))), Sqrt(
                                              Plus(CN1, Times(c, x)))),
                                      CN1)),
                              Integrate(SimplifyIntegrand(Times(u,
                                  Power(Times(Sqrt(Plus(C1, Times(c, x))),
                                      Sqrt(Plus(CN1, Times(c, x)))), CN1)),
                                  x), x),
                              x))),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2")), x),
                      EqQ(Subtract($s("e1"), Times(c, $s(
                          "d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c,
                          $s("d2"))), C0),
                      IGtQ(Plus(p,
                          C1D2), C0),
                      Or(IGtQ(Times(C1D2, Plus(m, C1)), C0),
                          ILtQ(Times(C1D2, Plus(m, Times(C2, p), C3)), C0))))),
          IIntegrate(5736,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), p),
                              Power(Plus(a,
                                  Times(b, ArcCosh(Times(c, x)))), n),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(b, c, n, Power(Negate(
                                  d), p), Power(Times(f, Plus(m, C1)),
                                      CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Plus(m, C1)),
                                      Power(Plus(C1, Times(c, x)), Subtract(p, C1D2)),
                                      Power(Plus(CN1, Times(c, x)), Subtract(p,
                                          C1D2)),
                                      Power(
                                          Plus(a, Times(b,
                                              ArcCosh(Times(c, x)))),
                                          Subtract(n, C1))),
                                  x),
                              x)),
                      Negate(Dist(Times(C2, e, p, Power(Times(Sqr(f), Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Times(f, x), Plus(m, C2)),
                              Power(Plus(d, Times(e, Sqr(x))), Subtract(p, C1)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(n,
                          C0),
                      GtQ(p, C0), LtQ(m, CN1), IntegerQ(p)))),
          IIntegrate(5737,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Sqrt(Plus(d_, Times(e_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Times(f, x), Plus(m, C1)), Sqrt(Plus(d, Times(e,
                                  Sqr(x)))),
                              Power(Plus(a,
                                  Times(b, ArcSinh(Times(c, x)))), n),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(Times(b, c, n, Sqrt(Plus(d, Times(e, Sqr(x)))),
                              Power(Times(f, Plus(m, C1), Sqrt(Plus(C1, Times(Sqr(c), Sqr(x))))),
                                  CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Plus(m, C1)),
                                      Power(Plus(a, Times(b, ArcSinh(Times(c, x)))),
                                          Subtract(n, C1))),
                                  x),
                              x)),
                      Negate(Dist(Times(Sqr(c), Sqrt(Plus(d, Times(e, Sqr(x)))),
                          Power(Times(Sqr(f), Plus(m, C1), Sqrt(Plus(C1, Times(Sqr(c), Sqr(x))))),
                              CN1)),
                          Integrate(Times(Power(Times(f, x), Plus(m, C2)),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n),
                              Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(e,
                      Times(Sqr(c), d)), GtQ(n, C0), LtQ(m, CN1)))),
          IIntegrate(5738,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_),
                      Sqrt(Plus($p("d1"), Times($p("e1", true), x_))), Sqrt(
                          Plus($p("d2"), Times($p("e2", true), x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Sqrt(Plus($s("d1"), Times($s(
                                  "e1"), x))),
                              Sqrt(Plus($s("d2"), Times($s("e2"),
                                  x))),
                              Power(Plus(a,
                                  Times(b, ArcCosh(Times(c, x)))), n),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(b, c, n, Sqrt(Plus($s("d1"), Times($s("e1"), x))),
                                  Sqrt(Plus($s("d2"),
                                      Times($s("e2"), x))),
                                  Power(
                                      Times(f, Plus(m, C1), Sqrt(Plus(C1, Times(c, x))),
                                          Sqrt(Plus(CN1, Times(c, x)))),
                                      CN1)),
                              Integrate(
                                  Times(
                                      Power(Times(f, x), Plus(m,
                                          C1)),
                                      Power(
                                          Plus(a, Times(b,
                                              ArcCosh(Times(c, x)))),
                                          Subtract(n, C1))),
                                  x),
                              x)),
                      Negate(Dist(Times(Sqr(c), Sqrt(Plus($s("d1"), Times($s("e1"), x))),
                          Sqrt(Plus($s("d2"), Times($s("e2"), x))),
                          Power(Times(Sqr(f), Plus(m, C1), Sqrt(Plus(C1, Times(c, x))),
                              Sqrt(Plus(CN1, Times(c, x)))), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n),
                                  Power(Times(Sqrt(Plus(C1, Times(c, x))),
                                      Sqrt(Plus(CN1, Times(c, x)))), CN1)),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f), x),
                      EqQ(Subtract($s("e1"), Times(c,
                          $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), GtQ(n, C0), LtQ(m, CN1)))),
          IIntegrate(5739,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus(d, Times(e, Sqr(x))), p),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))),
                                  n),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(Times(C2, e, p, Power(Times(Sqr(f), Plus(m, C1)), CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Plus(m, C2)),
                                      Power(Plus(d, Times(e, Sqr(x))), Subtract(p,
                                          C1)),
                                      Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)),
                                  x),
                              x)),
                      Negate(
                          Dist(Times(b, c, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(f, Plus(m, C1),
                                      Power(Plus(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                              Integrate(Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Subtract(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(e, Times(Sqr(c),
                      d)), GtQ(n,
                          C0),
                      GtQ(p, C0), LtQ(m, CN1)))),
          IIntegrate(5740,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus($p("d1"), Times(
                          $p("e1", true), x_)), p_),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Power(Plus($s("d1"), Times($s("e1"), x)), p),
                              Power(Plus($s("d2"), Times($s("e2"), x)), p),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))),
                                  n),
                              Power(Times(f, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(C2, $s("e1"), $s("e2"), p, Power(Times(Sqr(f), Plus(m, C1)),
                                  CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Plus(m, C2)),
                                      Power(Plus($s("d1"), Times($s("e1"), x)), Subtract(p, C1)),
                                      Power(Plus($s("d2"), Times($s("e2"), x)), Subtract(p, C1)),
                                      Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)),
                                  x),
                              x)),
                      Negate(Dist(
                          Times(b, c, n, Power(Times(CN1, $s("d1"), $s("d2")), Subtract(p, C1D2)),
                              Sqrt(Plus($s("d1"), Times($s("e1"), x))),
                              Sqrt(Plus($s("d2"), Times($s("e2"), x))),
                              Power(Times(f, Plus(m, C1), Sqrt(Plus(C1, Times(c, x))),
                                  Sqrt(Plus(CN1, Times(c, x)))), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Plus(m, C1)),
                                  Power(Plus(CN1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f), x),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), GtQ(n, C0), GtQ(p, C0),
                      LtQ(m, CN1), IntegerQ(Subtract(p, C1D2))))));
}
