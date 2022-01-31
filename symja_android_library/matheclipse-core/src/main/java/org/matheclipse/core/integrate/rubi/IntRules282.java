package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCosh;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigReduce;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
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
class IntRules282 {
  public static IAST RULES =
      List(
          IIntegrate(5641,
              Integrate(
                  Times(
                      Power(Csch(
                          v_), m_DEFAULT),
                      Power(Plus(Times(Coth(v_), b_DEFAULT), a_), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Power(Plus(Times(b, Cosh(v)), Times(a, Sinh(v))), n), x), And(
                      FreeQ(List(a, b), x), IntegerQ(Times(C1D2, Subtract(m, C1))), EqQ(Plus(m, n),
                          C0)))),
          IIntegrate(5642,
              Integrate(
                  Times(
                      u_DEFAULT, Power(Sinh(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))), m_DEFAULT),
                      Power(Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(u,
                          Times(Power(Sinh(Plus(a, Times(b, x))), m),
                              Power(Sinh(Plus(c, Times(d, x))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5643,
              Integrate(
                  Times(
                      Power(Cosh(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))), m_DEFAULT),
                      Power(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT), u_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(u,
                          Times(
                              Power(Cosh(Plus(a, Times(b, x))),
                                  m),
                              Power(Cosh(Plus(c, Times(d, x))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5644,
              Integrate(
                  Times(Sech(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      Sech(Plus(c_, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Dist(Csch(Times(Subtract(Times(b, c), Times(a, d)), Power(d, CN1))),
                          Integrate(Tanh(Plus(a, Times(b, x))), x), x)),
                      Dist(
                          Csch(Times(Subtract(Times(b, c), Times(a, d)), Power(b, CN1))),
                          Integrate(Tanh(Plus(c, Times(d, x))), x), x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(b), Sqr(d)), C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(5645,
              Integrate(
                  Times(
                      Csch(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), Csch(
                          Plus(c_, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Csch(Times(Subtract(Times(b, c), Times(a, d)), Power(b, CN1))), Integrate(
                              Coth(Plus(a, Times(b, x))), x),
                          x),
                      Dist(
                          Csch(Times(Subtract(Times(b, c), Times(a, d)),
                              Power(d, CN1))),
                          Integrate(Coth(Plus(c, Times(d, x))), x), x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(b), Sqr(d)), C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(5646,
              Integrate(
                  Times(
                      Tanh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), Tanh(
                          Plus(c_, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(b, x,
                          Power(d, CN1)), x),
                      Dist(
                          Times(
                              b, Cosh(Times(Subtract(Times(b, c), Times(a, d)),
                                  Power(d, CN1))),
                              Power(d, CN1)),
                          Integrate(Times(Sech(Plus(a, Times(b, x))), Sech(Plus(c, Times(d, x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(b), Sqr(d)), C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(5647,
              Integrate(
                  Times(Coth(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      Coth(Plus(c_, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(b, x,
                          Power(d, CN1)), x),
                      Dist(Cosh(Times(Subtract(Times(b, c), Times(a, d)), Power(d, CN1))),
                          Integrate(Times(Csch(Plus(a, Times(b, x))), Csch(Plus(c, Times(d, x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(b), Sqr(d)), C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(5648,
              Integrate(
                  Times(u_DEFAULT,
                      Power(Plus(Times(Cosh(
                          v_), a_DEFAULT), Times(b_DEFAULT,
                              Sinh(v_))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(u, Power(Times(a,
                      Exp(Times(a, v, Power(b, CN1)))), n)), x),
                  And(FreeQ(List(a, b, n), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(5649,
              Integrate(
                  Sinh(
                      Times(
                          Sqr(Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT))),
                          d_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(C1D2,
                              Integrate(
                                  Exp(Times(CN1, d, Sqr(Plus(a,
                                      Times(b, Log(Times(c, Power(x, n)))))))),
                                  x),
                              x)),
                      Dist(C1D2,
                          Integrate(Exp(
                              Times(d, Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(5650,
              Integrate(
                  Cosh(
                      Times(
                          Sqr(Plus(
                              a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  b_DEFAULT))),
                          d_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(
                              Exp(Times(CN1, d, Sqr(
                                  Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Exp(Times(d, Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n)))))))), x),
                          x)),
                  FreeQ(List(a, b, c, d, n), x))),
          IIntegrate(5651,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_DEFAULT),
                      Sinh(
                          Times(
                              Sqr(Plus(
                                  a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                      b_DEFAULT))),
                              d_DEFAULT))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(C1D2,
                              Integrate(Times(Power(Times(e, x), m),
                                  Power(
                                      Exp(Times(d,
                                          Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                                      CN1)),
                                  x),
                              x)),
                      Dist(C1D2,
                          Integrate(
                              Times(Power(Times(e, x), m),
                                  Exp(Times(d,
                                      Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n))))))))),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(5652,
              Integrate(
                  Times(
                      Cosh(
                          Times(
                              Sqr(Plus(
                                  a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                      b_DEFAULT))),
                              d_DEFAULT)),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(
                              Times(
                                  Power(Times(e,
                                      x), m),
                                  Power(
                                      Exp(Times(d,
                                          Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n)))))))),
                                      CN1)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(Times(Power(Times(e, x), m),
                              Exp(Times(d, Sqr(Plus(a, Times(b, Log(Times(c, Power(x, n))))))))),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(5653,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      n_DEFAULT),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(x,
                          Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)), x),
                      Dist(Times(b, c, n),
                          Integrate(
                              Times(x,
                                  Power(Plus(a, Times(b,
                                      ArcSinh(Times(c, x)))), Subtract(n,
                                          C1)),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c), x), GtQ(n, C0)))),
          IIntegrate(5654,
              Integrate(Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  n_DEFAULT), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(x,
                          Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)), x),
                      Dist(Times(b, c, n),
                          Integrate(
                              Times(x,
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1)),
                                  Power(Times(Sqrt(Plus(CN1, Times(c, x))),
                                      Sqrt(Plus(C1, Times(c, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c), x), GtQ(n, C0)))),
          IIntegrate(5655,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      n_),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Sqrt(Plus(C1, Times(Sqr(c), Sqr(x)))),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n,
                                  C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(c, Power(Times(b, Plus(n, C1)),
                              CN1)),
                          Integrate(Times(x,
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n,
                                  C1)),
                              Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                          x)),
                  And(FreeQ(List(a, b, c), x), LtQ(n, CN1)))),
          IIntegrate(5656,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      n_),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Sqrt(Plus(CN1, Times(c, x))), Sqrt(Plus(C1, Times(c, x))),
                          Power(Plus(a, Times(b, ArcCosh(Times(c, x)))),
                              Plus(n, C1)),
                          Power(Times(b, c, Plus(n, C1)), CN1)), x),
                      Dist(
                          Times(c, Power(Times(b, Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  x, Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n,
                                      C1)),
                                  Power(
                                      Times(Sqrt(Plus(CN1, Times(c, x))), Sqrt(
                                          Plus(C1, Times(c, x)))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c), x), LtQ(n, CN1)))),
          IIntegrate(5657,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      n_),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(b,
                          c), CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, n), Cosh(Subtract(Times(a, Power(b,
                                      CN1)), Times(x,
                                          Power(b, CN1))))),
                              x),
                          x, Plus(a, Times(b, ArcSinh(Times(c, x))))),
                      x),
                  FreeQ(List(a, b, c, n), x))),
          IIntegrate(5658,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      n_),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(Times(b, c), CN1),
                          Subst(
                              Integrate(
                                  Times(Power(x, n),
                                      Sinh(Subtract(Times(a, Power(b, CN1)),
                                          Times(x, Power(b, CN1))))),
                                  x),
                              x, Plus(a, Times(b, ArcCosh(Times(c, x))))),
                          x)),
                  FreeQ(List(a, b, c, n), x))),
          IIntegrate(5659,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, CN1)),
                  x_Symbol),
              Condition(
                  Subst(
                      Integrate(Times(Power(Plus(a,
                          Times(b, x)), n), Power(Tanh(x),
                              CN1)),
                          x),
                      x, ArcSinh(Times(c, x))),
                  And(FreeQ(List(a, b, c), x), IGtQ(n, C0)))),
          IIntegrate(5660, Integrate(Times(
              Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
              Power(x_, CN1)), x_Symbol),
              Condition(
                  Subst(Integrate(Times(Power(Plus(a, Times(b, x)), n), Power(Coth(x), CN1)), x), x,
                      ArcCosh(Times(c, x))),
                  And(FreeQ(List(a, b, c), x), IGtQ(n, C0)))));
}
