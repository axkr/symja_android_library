package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCosh;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN3;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Hypergeometric2F1;
import static org.matheclipse.core.expression.F.HypergeometricPFQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
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
class IntRules288 {
  public static IAST RULES =
      List(
          IIntegrate(5761,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, m_), Power(Plus($p("d1"), Times($p("e1", true), x_)), CN1D2), Power(
                          Plus($p("d2"), Times($p("e2", true), x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(Power(c, Plus(m,
                          C1)), Sqrt(
                              Times(CN1, $s("d1"), $s("d2")))),
                          CN1),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, x)), n),
                              Power(Cosh(x), m)), x),
                          x, ArcCosh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2")), x),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"),
                          Times(c, $s("d2"))), C0),
                      IGtQ(n, C0), GtQ($s("d1"), C0), LtQ($s("d2"), C0), IntegerQ(m)))),
          IIntegrate(5762,
              Integrate(Times(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                      CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Plus(a, Times(b, ArcSinh(Times(c, x)))), Hypergeometric2F1(C1D2,
                                  Times(C1D2, Plus(C1, m)), Times(C1D2, Plus(C3, m)), Times(CN1,
                                      Sqr(c), Sqr(x))),
                              Power(Times(Sqrt(d), f, Plus(m, C1)), CN1)),
                          x),
                      Simp(
                          Times(
                              b, c, Power(Times(f, x), Plus(m,
                                  C2)),
                              HypergeometricPFQ(
                                  List(C1, Plus(C1, Times(C1D2, m)), Plus(C1,
                                      Times(C1D2, m))),
                                  List(Plus(QQ(3L, 2L), Times(C1D2, m)), Plus(C2,
                                      Times(C1D2, m))),
                                  Times(CN1, Sqr(c), Sqr(x))),
                              Power(Times(Sqrt(d), Sqr(f), Plus(m, C1), Plus(m, C2)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(c),
                      d)), GtQ(d,
                          C0),
                      Not(IntegerQ(m))))),
          IIntegrate(5763,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT, x_), m_),
                      Power(
                          Plus($p("d1"), Times($p("e1", true), x_)), CN1D2),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)),
                              Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))),
                              Plus(a, Times(b, ArcCosh(Times(c, x)))),
                              Hypergeometric2F1(C1D2, Times(C1D2, Plus(C1, m)),
                                  Times(C1D2, Plus(C3, m)), Times(Sqr(c), Sqr(x))),
                              Power(
                                  Times(f, Plus(m, C1), Sqrt(Plus($s("d1"), Times($s("e1"), x))),
                                      Sqrt(Plus($s("d2"), Times($s("e2"), x)))),
                                  CN1)),
                          x),
                      Simp(
                          Times(b, c, Power(Times(f, x), Plus(m, C2)),
                              HypergeometricPFQ(
                                  List(C1, Plus(C1, Times(C1D2, m)), Plus(C1, Times(C1D2, m))),
                                  List(Plus(QQ(3L, 2L), Times(C1D2, m)),
                                      Plus(C2, Times(C1D2, m))),
                                  Times(Sqr(c), Sqr(x))),
                              Power(
                                  Times(Sqrt(Times(CN1, $s("d1"), $s("d2"))), Sqr(f), Plus(m, C1),
                                      Plus(m, C2)),
                                  CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, m), x),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c,
                          $s("d2"))), C0),
                      GtQ($s("d1"), C0), LtQ($s("d2"), C0), Not(IntegerQ(m))))),
          IIntegrate(5764,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Plus(C1,
                              Times(Sqr(c), Sqr(x)))),
                          Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(Power(Times(f, x), m),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))),
                                  n),
                              Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(c),
                      d)), GtQ(n,
                          C0),
                      Not(GtQ(d, C0)), Or(IntegerQ(m), EqQ(n, C1))))),
          IIntegrate(5765,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus($p("d1"), Times($p("e1", true),
                          x_)), CN1D2),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Sqrt(Plus(C1, Times(c, x))), Sqrt(Plus(CN1, Times(c, x))),
                          Power(Times(Sqrt(Plus($s("d1"), Times($s("e1"), x))),
                              Sqrt(Plus($s("d2"), Times($s("e2"), x)))), CN1)),
                      Integrate(Times(Power(Times(f, x), m),
                          Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n),
                          Power(Times(Sqrt(Plus(C1, Times(c, x))), Sqrt(Plus(CN1, Times(c, x)))),
                              CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, m), x),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), GtQ(n, C0),
                      Not(And(GtQ($s(
                          "d1"), C0), LtQ($s("d2"),
                              C0))),
                      Or(IntegerQ(m), EqQ(n, C1))))),
          IIntegrate(5766,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(d, Times(e,
                                  Sqr(x))), Plus(p, C1)),
                              Power(Plus(a,
                                  Times(b, ArcCosh(Times(c, x)))), n),
                              Power(Times(e, Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Negate(Dist(
                          Times(b, f, n, Power(Negate(d), p),
                              Power(Times(c, Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(C1, Times(c, x)), Plus(p, C1D2)),
                                  Power(Plus(CN1, Times(c, x)), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                      Dist(
                          Times(Sqr(f), Subtract(m, C1),
                              Power(Times(Sqr(c), Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C2)),
                              Power(Plus(d, Times(e, Sqr(x))), p),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0), GtQ(m, C1), NeQ(Plus(m, Times(C2, p), C1),
                          C0),
                      IntegerQ(p), IntegerQ(m)))),
          IIntegrate(5767,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(d, Times(e,
                                  Sqr(x))), Plus(p, C1)),
                              Power(Plus(a,
                                  Times(b, ArcSinh(Times(c, x)))), n),
                              Power(Times(e, Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(
                                  Sqr(f), Subtract(m, C1), Power(
                                      Times(Sqr(c), Plus(m, Times(C2, p), C1)), CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Subtract(m, C2)),
                                      Power(Plus(d, Times(e, Sqr(x))), p), Power(Plus(a,
                                          Times(b, ArcSinh(Times(c, x)))), n)),
                                  x),
                              x)),
                      Negate(Dist(
                          Times(b, f, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(Times(c, Plus(m, Times(C2, p), C1),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), FracPart(p))), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f, p), x), EqQ(e, Times(Sqr(c), d)), GtQ(n, C0),
                      GtQ(m, C1), NeQ(Plus(m, Times(C2, p), C1), C0), IntegerQ(m)))),
          IIntegrate(5768,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus($p("d1"),
                          Times($p("e1", true), x_)), p_),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus($s("d1"), Times($s(
                                  "e1"), x)), Plus(p, C1)),
                              Power(Plus($s("d2"), Times($s("e2"),
                                  x)), Plus(p, C1)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n), Power(Times($s(
                                  "e1"), $s("e2"), Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(Sqr(f), Subtract(m,
                              C1), Power(Times(Sqr(c), Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus($s("d1"), Times($s("e1"), x)), p),
                                  Power(Plus($s("d2"), Times($s("e2"), x)), p), Power(
                                      Plus(a, Times(b, ArcCosh(Times(c, x)))), n)),
                              x),
                          x),
                      Negate(Dist(
                          Times(b, f, n, Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                              Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                              Power(Plus($s("d2"), Times($s("e2"), x)), FracPart(p)),
                              Power(Times(c, Plus(m, Times(C2, p), C1),
                                  Power(Plus(C1, Times(c, x)), FracPart(p)),
                                  Power(Plus(CN1, Times(c, x)), FracPart(p))), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(CN1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, p), x),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), GtQ(n, C0), GtQ(m,
                          C1),
                      NeQ(Plus(m, Times(C2, p), C1), C0), IntegerQ(m), IntegerQ(Plus(p, C1D2))))),
          IIntegrate(5769,
              Integrate(
                  Times(Power(
                      Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_),
                      Power(Plus($p("d1"), Times($p("e1", true), x_)), p_), Power(
                          Plus($p("d2"), Times($p("e2", true), x_)), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(f, Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus($s("d1"), Times($s(
                                  "e1"), x)), Plus(p, C1)),
                              Power(Plus($s("d2"), Times($s("e2"),
                                  x)), Plus(p, C1)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n), Power(Times(
                                  $s("e1"), $s("e2"), Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(Sqr(f), Subtract(m,
                              C1), Power(Times(Sqr(c), Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C2)),
                                  Power(Plus($s("d1"), Times($s("e1"), x)), p),
                                  Power(Plus($s("d2"), Times($s("e2"), x)), p), Power(
                                      Plus(a, Times(b, ArcCosh(Times(c, x)))), n)),
                              x),
                          x),
                      Negate(Dist(Times(b, f, n, Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                          Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                          Power(Plus($s("d2"), Times($s("e2"), x)), FracPart(p)),
                          Power(Times(c, Plus(m, Times(C2, p), C1),
                              Power(Plus(C1, Times(c, x)), FracPart(p)),
                              Power(Plus(CN1, Times(c, x)), FracPart(p))), CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(C1, Times(c, x)), Plus(p, C1D2)),
                                  Power(Plus(CN1, Times(c, x)), Plus(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, p), x),
                      EqQ(Subtract($s("e1"), Times(c,
                          $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c,
                          $s("d2"))), C0),
                      GtQ(n, C0), GtQ(m, C1), NeQ(Plus(m, Times(C2, p), C1), C0), IntegerQ(m)))),
          IIntegrate(5770,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), m), Sqrt(Plus(C1, Times(c, x))),
                              Sqrt(Plus(CN1, Times(c, x))), Power(Plus(d, Times(e, Sqr(x))), p),
                              Power(Plus(a, Times(b,
                                  ArcCosh(Times(c, x)))), Plus(n,
                                      C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(f, m, Power(Negate(
                              d), p), Power(Times(b, c, Plus(n, C1)),
                                  CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(C1, Times(c, x)), Subtract(p, C1D2)),
                              Power(Plus(CN1, Times(c, x)), Subtract(p, C1D2)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), LtQ(n, CN1), EqQ(Plus(m, Times(C2, p),
                          C1), C0),
                      IntegerQ(p)))),
          IIntegrate(5771,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(f, x), m), Sqrt(Plus(C1, Times(Sqr(c), Sqr(x)))),
                              Power(Plus(d, Times(e, Sqr(x))), p),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n,
                                  C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(f, m, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(Times(b, c, Plus(n, C1),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), EqQ(e, Times(Sqr(c), d)), LtQ(n, CN1),
                      EqQ(Plus(m, Times(C2, p), C1), C0)))),
          IIntegrate(5772,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("d1"), Times($p("e1", true), x_)), p_DEFAULT),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), m), Sqrt(Plus(C1, Times(c, x))),
                              Sqrt(Plus(CN1, Times(c, x))),
                              Power(Plus($s("d1"), Times($s("e1"), x)), p),
                              Power(Plus($s("d2"), Times($s("e2"), x)), p),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n,
                                  C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Dist(Times(f, m, Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                          Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                          Power(Plus($s("d2"), Times($s("e2"), x)), FracPart(p)),
                          Power(
                              Times(b, c, Plus(n, C1), Power(Plus(C1, Times(c, x)), FracPart(p)),
                                  Power(Plus(CN1, Times(c, x)), FracPart(p))),
                              CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(CN1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, m, p), x),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), LtQ(n, CN1), EqQ(Plus(m,
                          Times(C2, p), C1), C0),
                      IntegerQ(Subtract(p, C1D2))))),
          IIntegrate(5773,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("d1"), Times($p("e1", true),
                          x_)), p_DEFAULT),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), m), Sqrt(Plus(C1, Times(c, x))),
                              Sqrt(Plus(CN1, Times(c, x))),
                              Power(Plus($s("d1"), Times($s("e1"), x)), p),
                              Power(Plus($s("d2"), Times($s("e2"), x)), p),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1)), Power(
                                  Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Dist(Times(f, m, Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                          Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                          Power(Plus($s("d2"), Times($s("e2"), x)), FracPart(p)),
                          Power(Times(b, c, Plus(n, C1), Power(Plus(C1, Times(c, x)), FracPart(p)),
                              Power(Plus(CN1, Times(c, x)), FracPart(p))), CN1)),
                          Integrate(
                              Times(
                                  Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(C1, Times(c, x)), Subtract(p, C1D2)),
                                  Power(Plus(CN1, Times(c, x)), Subtract(p, C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, m, p), x),
                      EqQ(Subtract($s("e1"), Times(c,
                          $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), LtQ(n,
                          CN1),
                      EqQ(Plus(m, Times(C2, p), C1), C0)))),
          IIntegrate(5774,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(f, x), m),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n, C1)), Power(
                                  Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          x),
                      Dist(Times(f, m, Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), EqQ(e, Times(Sqr(c), d)), LtQ(n, CN1),
                      GtQ(d, C0)))),
          IIntegrate(5775,
              Integrate(Times(
                  Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                  Power(Times(f_DEFAULT, x_), m_DEFAULT),
                  Power(Plus($p("d1"), Times($p("e1", true), x_)), CN1D2),
                  Power(Plus($p("d2"), Times($p("e2", true), x_)), CN1D2)), x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(f, x), m),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n,
                                  C1)),
                              Power(
                                  Times(b, c, Sqrt(Times(CN1, $s("d1"),
                                      $s("d2"))), Plus(n,
                                          C1)),
                                  CN1)),
                          x),
                      Dist(
                          Times(f, m,
                              Power(Times(b, c, Sqrt(Times(CN1, $s("d1"), $s("d2"))), Plus(n, C1)),
                                  CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C1)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, m), x),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), LtQ(n, CN1), GtQ($s("d1"),
                          C0),
                      LtQ($s("d2"), C0)))),
          IIntegrate(5776,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT,
                          x_)), b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(Times(f, x), m), Sqrt(Plus(C1, Times(c, x))),
                          Sqrt(Plus(CN1, Times(c, x))), Power(Plus(d, Times(e, Sqr(x))), p),
                          Power(Plus(a, Times(b, ArcCosh(Times(c, x)))),
                              Plus(n, C1)),
                          Power(Times(b, c, Plus(n, C1)), CN1)), x),
                      Dist(
                          Times(f, m, Power(Negate(
                              d), p), Power(Times(b, c, Plus(n, C1)),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(C1, Times(c, x)), Subtract(p, C1D2)),
                                  Power(Plus(CN1, Times(c,
                                      x)), Subtract(p,
                                          C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1))),
                              x),
                          x),
                      Negate(
                          Dist(
                              Times(
                                  c, Power(Negate(d), p), Plus(m, Times(C2, p),
                                      C1),
                                  Power(Times(b, f, Plus(n, C1)), CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Plus(m, C1)),
                                      Power(Plus(C1, Times(c, x)), Subtract(p, C1D2)),
                                      Power(Plus(CN1, Times(c, x)), Subtract(p, C1D2)),
                                      Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      LtQ(n, CN1), IGtQ(m, CN3), IGtQ(p, C0)))),
          IIntegrate(5777,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), m), Sqrt(Plus(C1, Times(Sqr(c), Sqr(x)))),
                              Power(Plus(d, Times(e, Sqr(x))), p),
                              Power(Plus(a, Times(b,
                                  ArcSinh(Times(c, x)))), Plus(n,
                                      C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(f, m, Power(d, IntPart(p)),
                                  Power(Plus(d, Times(e,
                                      Sqr(x))), FracPart(
                                          p)),
                                  Power(Times(b, c, Plus(n, C1),
                                      Power(Plus(C1, Times(Sqr(c), Sqr(x))), FracPart(p))), CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Subtract(m, C1)),
                                      Power(Plus(C1, Times(Sqr(c), Sqr(x))), Subtract(p,
                                          C1D2)),
                                      Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n, C1))),
                                  x),
                              x)),
                      Negate(
                          Dist(
                              Times(c, Plus(m, Times(C2, p), C1), Power(d, IntPart(p)),
                                  Power(Plus(d, Times(e,
                                      Sqr(x))), FracPart(
                                          p)),
                                  Power(
                                      Times(b, f, Plus(n, C1),
                                          Power(Plus(C1, Times(Sqr(c), Sqr(x))), FracPart(p))),
                                      CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Plus(m, C1)),
                                      Power(Plus(C1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                                      Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(e, Times(Sqr(c),
                      d)), LtQ(n, CN1), IGtQ(m, CN3), IGtQ(Times(C2, p), C0)))),
          IIntegrate(5778,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("d1"), Times($p("e1", true), x_)), p_DEFAULT),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), m), Sqrt(Plus(C1, Times(c, x))),
                              Sqrt(Plus(CN1, Times(c, x))),
                              Power(Plus($s("d1"), Times($s("e1"), x)), p),
                              Power(Plus($s("d2"), Times($s("e2"), x)), p),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n,
                                  C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(f, m, Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                              Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                              Power(Plus($s("d2"), Times($s("e2"),
                                  x)), FracPart(
                                      p)),
                              Power(
                                  Times(b, c, Plus(n, C1),
                                      Power(Plus(C1, Times(c, x)), FracPart(p)), Power(
                                          Plus(CN1, Times(c, x)), FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(f, x), Subtract(m, C1)),
                                  Power(Plus(CN1, Times(Sqr(c),
                                      Sqr(x))), Subtract(p,
                                          C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1))),
                              x),
                          x),
                      Negate(
                          Dist(
                              Times(c, Plus(m, Times(C2, p), C1),
                                  Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                                  Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                                  Power(Plus($s("d2"), Times($s("e2"),
                                      x)), FracPart(
                                          p)),
                                  Power(Times(b, f, Plus(n, C1),
                                      Power(Plus(C1, Times(c, x)), FracPart(p)),
                                      Power(Plus(CN1, Times(c, x)), FracPart(p))), CN1)),
                              Integrate(
                                  Times(Power(Times(f, x), Plus(m, C1)),
                                      Power(Plus(CN1, Times(Sqr(c), Sqr(x))), Subtract(p, C1D2)),
                                      Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f), x),
                      EqQ(Subtract($s("e1"), Times(c, $s("d1"))), C0),
                      EqQ(Plus($s("e2"),
                          Times(c, $s("d2"))), C0),
                      LtQ(n, CN1), IGtQ(m, CN3), IGtQ(Plus(p, C1D2), C0)))),
          IIntegrate(5779,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                      b_DEFAULT)), n_DEFAULT), Power(x_, m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(d, p), Power(Power(c, Plus(m, C1)),
                          CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x)), n), Power(Sinh(x), m), Power(Cosh(x),
                                      Plus(Times(C2, p), C1))),
                              x),
                          x, ArcSinh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ(e, Times(Sqr(c), d)),
                      IntegerQ(Times(C2,
                          p)),
                      GtQ(p, CN1), IGtQ(m, C0), Or(IntegerQ(p), GtQ(d, C0))))),
          IIntegrate(5780,
              Integrate(Times(
                  Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      n_DEFAULT),
                  Power(x_, m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(Negate(d), p), Power(Power(c, Plus(m, C1)), CN1)),
                      Subst(Integrate(Times(Power(Plus(a, Times(b, x)), n), Power(Cosh(x), m),
                          Power(Sinh(x), Plus(Times(C2, p), C1))), x), x, ArcCosh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(p, C0), IGtQ(m, C0)))));
}
