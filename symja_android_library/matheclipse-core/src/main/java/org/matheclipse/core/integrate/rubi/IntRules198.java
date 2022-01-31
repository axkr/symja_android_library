package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1DSqrt2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.EllipticE;
import static org.matheclipse.core.expression.F.EllipticPi;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules198 {
  public static IAST RULES =
      List(
          IIntegrate(3961,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_DEFAULT),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          c_), n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(a, c, Cot(Plus(e, Times(f, x))),
                          Power(Times(f, Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                              Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))), CN1)),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), Subtract(m, C1D2)),
                                  Power(Plus(c, Times(d, x)), Subtract(n, C1D2))),
                              x),
                          x, Csc(Plus(e, Times(f, x)))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), EqQ(Plus(Times(b, c), Times(a, d)),
                      C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3962,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(CN1, a, c), m),
                      Integrate(
                          ExpandTrig(
                              Times(Power(Times(g, $($s("§csc"), Plus(e, Times(f, x)))), p),
                                  Power($($s("§cot"), Plus(e, Times(f, x))), Times(C2, m))),
                              Power(Plus(c, Times(d, $($s("§csc"), Plus(e, Times(f, x))))),
                                  Subtract(n, m)),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x),
                      EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      IntegersQ(m, n), GeQ(Subtract(n, m), C0), GtQ(Times(m, n), C0)))),
          IIntegrate(3963,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(CN1, a, c), Plus(m, C1D2)), Cot(Plus(e, Times(f, x))),
                          Power(
                              Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Times(g,
                                  Csc(Plus(e, Times(f, x)))), p),
                              Power(Cot(Plus(e, Times(f, x))), Times(C2, m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, p), x),
                      EqQ(Plus(Times(b, c),
                          Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), IntegerQ(Plus(m, C1D2))))),
          IIntegrate(3964,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          p_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          c_), n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(a, c, g, Cot(Plus(e, Times(f, x))),
                          Power(
                              Times(f, Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))),
                              CN1)),
                      Subst(
                          Integrate(Times(Power(Times(g, x), Subtract(p, C1)),
                              Power(Plus(a, Times(b, x)), Subtract(m, C1D2)), Power(
                                  Plus(c, Times(d, x)), Subtract(n, C1D2))),
                              x),
                          x, Csc(Plus(e, Times(f, x)))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x), EqQ(Plus(Times(b, c),
                      Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0)))),
          IIntegrate(3965,
              Integrate(
                  Times(
                      Sqrt(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT)),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(CN2, b, g, Power(f,
                          CN1)),
                      Subst(
                          Integrate(
                              Power(
                                  Subtract(Plus(Times(b, c), Times(a, d)),
                                      Times(c, g, Sqr(x))),
                                  CN1),
                              x),
                          x,
                          Times(b, Cot(Plus(e, Times(f, x))),
                              Power(
                                  Times(
                                      Sqrt(Times(g, Csc(Plus(e, Times(f, x))))), Sqrt(Plus(a,
                                          Times(b, Csc(Plus(e, Times(f, x))))))),
                                  CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0)))),
          IIntegrate(3966,
              Integrate(
                  Times(
                      Sqrt(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT)),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(a, Power(c,
                              CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Times(g, Csc(
                                      Plus(e, Times(f, x))))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(Times(Subtract(Times(b, c), Times(a, d)), Power(Times(c, g), CN1)),
                          Integrate(
                              Times(Power(Times(g, Csc(Plus(e, Times(f, x)))), QQ(3L, 2L)),
                                  Power(Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                      Plus(c, Times(d, Csc(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3967,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT), c_),
                          CN1)),
                  x_Symbol),
              Condition(Dist(Times(CN2, b, Power(f, CN1)), Subst(
                  Integrate(Power(Plus(Times(b, c), Times(a, d), Times(d, Sqr(x))), CN1), x), x,
                  Times(b, Cot(Plus(e, Times(f, x))),
                      Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2))),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3968,
              Integrate(
                  Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                              a_)),
                      Power(Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT), c_), CN1)),
                  x_Symbol),
              Condition(Negate(
                  Simp(
                      Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                          Sqrt(Times(c,
                              Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1))),
                          EllipticE(
                              ArcSin(
                                  Times(c, Cot(Plus(e, Times(f, x))), Power(
                                      Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1))),
                              Times(
                                  CN1, Subtract(Times(b, c), Times(a, d)), Power(
                                      Plus(Times(b, c), Times(a, d)), CN1))),
                          Power(Times(d, f,
                              Sqrt(Times(c, d, Plus(a, Times(b, Csc(Plus(e, Times(f, x))))),
                                  Power(Times(Plus(Times(b, c), Times(a, d)),
                                      Plus(c, Times(d, Csc(Plus(e, Times(f, x)))))), CN1)))),
                              CN1)),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      EqQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3969,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(b, Power(d,
                              CN1)),
                          Integrate(
                              Times(
                                  Csc(Plus(e, Times(f,
                                      x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(b, c), Times(a,
                              d)), Power(d,
                                  CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Power(
                                      Times(
                                          Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                          Plus(c, Times(d, Csc(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), NeQ(Subtract(Sqr(a),
                          Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3970,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          QQ(3L, 2L)),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          c_), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(g, Power(d, CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Times(g, Csc(Plus(e, Times(f, x))))), Sqrt(
                                      Plus(a, Times(b, Csc(Plus(e, Times(f, x))))))),
                              x),
                          x),
                      Dist(Times(c, g, Power(d, CN1)),
                          Integrate(Times(Sqrt(Times(g, Csc(Plus(e, Times(f, x))))),
                              Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                              Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3971,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          QQ(3L, 2L)),
                      Sqrt(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(b, Power(d,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(g, Csc(
                                      Plus(e, Times(f, x)))), QQ(3L,
                                          2L)),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(b, c), Times(a,
                              d)), Power(d,
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(Times(g, Csc(Plus(e, Times(f, x)))), QQ(3L,
                                      2L)),
                                  Power(
                                      Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                          Plus(c, Times(d, Csc(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)),
                          C0)))),
          IIntegrate(3972,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(b, Power(Subtract(Times(b, c), Times(a, d)),
                              CN1)),
                          Integrate(
                              Times(
                                  Csc(Plus(e, Times(f,
                                      x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(Times(d, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), Or(EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                          EqQ(Subtract(Sqr(c), Sqr(d)), C0))))),
          IIntegrate(3973,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(CN2, Cot(Plus(e, Times(f, x))),
                          Sqrt(
                              Times(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))),
                                  Power(Plus(a, b), CN1))),
                          EllipticPi(
                              Times(C2, d, Power(Plus(c, d),
                                  CN1)),
                              ArcSin(Times(Sqrt(Subtract(C1, Csc(Plus(e, Times(f, x))))),
                                  C1DSqrt2)),
                              Times(C2, b, Power(Plus(a, b), CN1))),
                          Power(
                              Times(f, Plus(c, d),
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))), Sqrt(
                                      Negate(Sqr(Cot(Plus(e, Times(f, x))))))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3974,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          QQ(3L, 2L)),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          CN1D2),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(Times(a, g, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                              Integrate(
                                  Times(Sqrt(
                                      Times(g, Csc(Plus(e, Times(f, x))))),
                                      Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                                  x),
                              x)),
                      Dist(Times(c, g, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(Sqrt(Times(g, Csc(Plus(e, Times(f, x))))),
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3975,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          QQ(3L, 2L)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          g, Sqrt(Times(g, Csc(Plus(e,
                              Times(f, x))))),
                          Sqrt(Plus(b, Times(a, Sin(Plus(e, Times(f, x)))))), Power(
                              Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                      Integrate(
                          Power(
                              Times(Sqrt(Plus(b, Times(a, Sin(Plus(e, Times(f, x)))))),
                                  Plus(d, Times(c, Sin(Plus(e, Times(f, x)))))),
                              CN1),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3976,
              Integrate(
                  Times(Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(
                              Times(a, Power(Subtract(Times(b, c), Times(a, d)),
                                  CN1)),
                              Integrate(
                                  Times(
                                      Csc(Plus(e, Times(f,
                                          x))),
                                      Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                                  x),
                              x)),
                      Dist(Times(c, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(Csc(Plus(e, Times(f, x))),
                                  Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), Or(EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                          EqQ(Subtract(Sqr(c), Sqr(d)), C0))))),
          IIntegrate(3977,
              Integrate(
                  Times(
                      Sqr($($s("§csc"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(d, CN1),
                          Integrate(
                              Times(
                                  Csc(Plus(e, Times(f,
                                      x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(c, Power(d,
                              CN1)),
                          Integrate(
                              Times(
                                  Csc(Plus(e, Times(f, x))), Power(
                                      Times(
                                          Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                          Plus(c, Times(d, Csc(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                      C0), NeQ(Subtract(Sqr(a), Sqr(b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3978,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              g_DEFAULT),
                          QQ(5L, 2L)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2),
                      Power(Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          c_), CN1)),
                  x_Symbol),
              Condition(Plus(Negate(Dist(
                  Times(Sqr(c), Sqr(g), Power(Times(d, Subtract(Times(b, c), Times(a, d))), CN1)),
                  Integrate(Times(Sqrt(Times(g, Csc(Plus(e, Times(f, x))))),
                      Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                      Power(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))), CN1)), x),
                  x)), Dist(
                      Times(Sqr(g), Power(Times(d, Subtract(Times(b, c), Times(a, d))), CN1)),
                      Integrate(
                          Times(Sqrt(Times(g, Csc(Plus(e, Times(f, x))))),
                              Plus(Times(a, c),
                                  Times(Subtract(Times(b, c), Times(a, d)),
                                      Csc(Plus(e, Times(f, x))))),
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3979,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                          QQ(5L, 2L)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(g, Power(d,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(g, Csc(Plus(e, Times(f, x)))), QQ(3L,
                                      2L)),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(Times(c, g, Power(d, CN1)),
                          Integrate(
                              Times(Power(Times(g, Csc(Plus(e, Times(f, x)))), QQ(3L, 2L)),
                                  Power(
                                      Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                          Plus(c, Times(d, Csc(Plus(e, Times(f, x)))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)),
                          C0)))),
          IIntegrate(3980,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))),
                      Sqrt(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(CN2, b, Power(f, CN1)),
                      Subst(Integrate(Power(Subtract(C1, Times(b, d, Sqr(x))), CN1), x), x,
                          Times(Cot(Plus(e, Times(f, x))),
                              Power(Times(Sqrt(Plus(a, Times(b, Csc(Plus(e, Times(f, x)))))),
                                  Sqrt(Plus(c, Times(d, Csc(Plus(e, Times(f, x))))))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)), C0)))));
}
