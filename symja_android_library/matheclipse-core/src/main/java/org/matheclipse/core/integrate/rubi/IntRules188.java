package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.EllipticPi;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules188 {
  public static IAST RULES =
      List(
          IIntegrate(3761,
              Integrate(
                  Times(
                      Plus(d_, Times(e_DEFAULT, x_)), Tan(Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(e, Log(Cos(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                              Power(Times(C2, c), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x),
                      EqQ(Subtract(Times(C2, c, d), Times(b, e)), C0)))),
          IIntegrate(3762,
              Integrate(
                  Times(
                      Cot(Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_)))),
                      Plus(d_, Times(e_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          e, Log(Sin(
                              Plus(a, Times(b, x), Times(c, Sqr(x))))),
                          Power(Times(C2, c), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x),
                      EqQ(Subtract(Times(C2, c, d), Times(b, e)), C0)))),
          IIntegrate(3763,
              Integrate(
                  Times(
                      Plus(d_DEFAULT, Times(e_DEFAULT, x_)), Tan(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(e, Log(Cos(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                                  Power(Times(C2, c), CN1)),
                              x)),
                      Dist(
                          Times(Subtract(Times(C2, c, d), Times(b,
                              e)), Power(Times(C2, c),
                                  CN1)),
                          Integrate(Tan(Plus(a, Times(b, x), Times(c, Sqr(x)))), x), x)),
                  And(FreeQ(List(a, b, c, d,
                      e), x), NeQ(Subtract(Times(C2, c, d), Times(b, e)),
                          C0)))),
          IIntegrate(3764,
              Integrate(Times(Cot(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))), x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(e, Log(Sin(Plus(a, Times(b, x), Times(c, Sqr(x))))),
                          Power(Times(C2, c), CN1)), x),
                      Dist(Times(Subtract(Times(C2, c, d), Times(b, e)), Power(Times(C2, c), CN1)),
                          Integrate(Cot(Plus(a, Times(b, x), Times(c, Sqr(x)))), x), x)),
                  And(FreeQ(List(a, b, c, d, e), x),
                      NeQ(Subtract(Times(C2, c, d), Times(b, e)), C0)))),
          IIntegrate(3765,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT),
                      Power(Tan(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(d, Times(e, x)), m),
                          Power(Tan(Plus(a, Times(b, x), Times(c, Sqr(x)))), n)),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(3766,
              Integrate(
                  Times(
                      Power(
                          Cot(Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                              Times(c_DEFAULT, Sqr(x_)))),
                          n_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(d, Times(e, x)),
                              m),
                          Power(Cot(Plus(a, Times(b, x), Times(c, Sqr(x)))), n)),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n), x))),
          IIntegrate(3767,
              Integrate(Power($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                  n_), x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(d, CN1),
                          Subst(
                              Integrate(
                                  ExpandIntegrand(
                                      Power(Plus(C1, Sqr(x)), Subtract(Times(C1D2, n), C1)), x),
                                  x),
                              x, Cot(Plus(c, Times(d, x)))),
                          x)),
                  And(FreeQ(List(c, d), x), IGtQ(Times(C1D2, n), C0)))),
          IIntegrate(3768,
              Integrate(
                  Power(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                      n_),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(b, Cos(Plus(c, Times(d, x))),
                              Power(Times(b, Csc(Plus(c, Times(d, x)))), Subtract(n, C1)), Power(
                                  Times(d, Subtract(n, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Sqr(b), Subtract(n, C2), Power(Subtract(n,
                              C1), CN1)),
                          Integrate(Power(Times(b, Csc(
                              Plus(c, Times(d, x)))), Subtract(n,
                                  C2)),
                              x),
                          x)),
                  And(FreeQ(List(b, c, d), x), GtQ(n, C1), IntegerQ(Times(C2, n))))),
          IIntegrate(3769,
              Integrate(
                  Power(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                      n_),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Cos(Plus(c, Times(d, x))),
                              Power(Times(b, Csc(Plus(c, Times(d, x)))),
                                  Plus(n, C1)),
                              Power(Times(b, d, n), CN1)),
                          x),
                      Dist(Times(Plus(n, C1), Power(Times(Sqr(b), n), CN1)),
                          Integrate(Power(Times(b, Csc(Plus(c, Times(d, x)))), Plus(n, C2)), x),
                          x)),
                  And(FreeQ(List(b, c, d), x), LtQ(n, CN1), IntegerQ(Times(C2, n))))),
          IIntegrate(3770,
              Integrate($($s("§csc"),
                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), x_Symbol),
              Condition(
                  Negate(Simp(Times(ArcTanh(Cos(Plus(c, Times(d, x)))), Power(d, CN1)),
                      x)),
                  FreeQ(List(c, d), x))),
          IIntegrate(3771,
              Integrate(
                  Power(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                      n_),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(Times(b, Csc(Plus(c, Times(d, x)))),
                      n), Power(Sin(Plus(c, Times(d, x))), n)), Integrate(
                          Power(Power(Sin(Plus(c, Times(d, x))), n), CN1), x),
                      x),
                  And(FreeQ(List(b, c, d), x), EqQ(Sqr(n), C1D4)))),
          IIntegrate(3772,
              Integrate(Power(
                  Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), n_),
                  x_Symbol),
              Condition(Simp(Times(Power(Times(b, Csc(Plus(c, Times(d, x)))), Subtract(n, C1)),
                  Power(Times(Sin(Plus(c, Times(d, x))), Power(b, CN1)), Subtract(n, C1)),
                  Integrate(Power(Power(Times(Sin(Plus(c, Times(d, x))), Power(b, CN1)), n), CN1),
                      x)),
                  x), And(FreeQ(List(b, c, d, n), x), Not(IntegerQ(n))))),
          IIntegrate(3773,
              Integrate(
                  Sqr(Plus(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                      a_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Sqr(
                          a), x), x),
                      Dist(Times(C2, a, b), Integrate(Csc(Plus(c, Times(d, x))),
                          x), x),
                      Dist(Sqr(b), Integrate(Sqr(Csc(Plus(c, Times(d, x)))), x), x)),
                  FreeQ(List(a, b, c, d), x))),
          IIntegrate(3774,
              Integrate(
                  Sqrt(
                      Plus(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                          a_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(CN2, b, Power(d,
                          CN1)),
                      Subst(
                          Integrate(Power(Plus(a, Sqr(x)), CN1), x), x, Times(b,
                              Cot(Plus(c,
                                  Times(d, x))),
                              Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), CN1D2))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3775,
              Integrate(
                  Power(
                      Plus(
                          Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT),
                          a_),
                      n_),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Sqr(b), Cot(Plus(c,
                                      Times(d, x))),
                                  Power(
                                      Plus(a, Times(b,
                                          Csc(Plus(c, Times(d, x))))),
                                      Subtract(n, C2)),
                                  Power(Times(d, Subtract(n, C1)), CN1)),
                              x)),
                      Dist(
                          Times(a, Power(Subtract(n, C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b, Csc(Plus(c, Times(d, x))))),
                                      Subtract(n, C2)),
                                  Plus(Times(a, Subtract(n, C1)),
                                      Times(b, Subtract(Times(C3, n), C4),
                                          Csc(Plus(c, Times(d, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(a),
                      Sqr(b)), C0), GtQ(n,
                          C1),
                      IntegerQ(Times(C2, n))))),
          IIntegrate(3776,
              Integrate(
                  Power(
                      Plus(
                          Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT),
                          a_),
                      CN1D2),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Power(a, CN1), Integrate(
                              Sqrt(Plus(a, Times(b, Csc(Plus(c, Times(d, x)))))), x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Csc(Plus(c, Times(d, x))), Power(Plus(a,
                                  Times(b, Csc(Plus(c, Times(d, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3777,
              Integrate(
                  Power(
                      Plus(
                          Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT),
                          a_),
                      n_),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Cot(Plus(c, Times(d, x))),
                                  Power(Plus(a,
                                      Times(b, Csc(Plus(c, Times(d, x))))), n),
                                  Power(Times(d, Plus(Times(C2, n), C1)), CN1)),
                              x)),
                      Dist(Power(Times(Sqr(a), Plus(Times(C2, n), C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), Plus(n, C1)),
                                  Subtract(
                                      Times(a, Plus(Times(C2, n), C1)), Times(b, Plus(n, C1),
                                          Csc(Plus(c, Times(d, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0), LeQ(n, CN1),
                      IntegerQ(Times(C2, n))))),
          IIntegrate(3778,
              Integrate(
                  Power(
                      Plus(Times(
                          $($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_),
                      n_),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(a, n), Cot(Plus(c,
                              Times(d, x))),
                          Power(
                              Times(
                                  d, Sqrt(Plus(C1,
                                      Csc(Plus(c, Times(d, x))))),
                                  Sqrt(Subtract(C1, Csc(Plus(c, Times(d, x)))))),
                              CN1)),
                      Subst(
                          Integrate(
                              Times(Power(Plus(C1, Times(b, x, Power(a, CN1))), Subtract(n, C1D2)),
                                  Power(Times(x, Sqrt(Subtract(C1, Times(b, x, Power(a, CN1))))),
                                      CN1)),
                              x),
                          x, Csc(Plus(c, Times(d, x)))),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), EqQ(Subtract(Sqr(a), Sqr(
                      b)), C0), Not(IntegerQ(
                          Times(C2, n))),
                      GtQ(a, C0)))),
          IIntegrate(3779,
              Integrate(
                  Power(Plus(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                      a_), n_),
                  x_Symbol),
              Condition(Dist(Times(Power(a, IntPart(n)),
                  Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), FracPart(n)), Power(Power(
                      Plus(C1, Times(b, Csc(Plus(c, Times(d, x))), Power(a, CN1))), FracPart(n)),
                      CN1)),
                  Integrate(Power(Plus(C1, Times(b, Csc(Plus(c, Times(d, x))), Power(a, CN1))), n),
                      x),
                  x),
                  And(FreeQ(List(a, b, c, d, n), x), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                      Not(IntegerQ(Times(C2, n))), Not(GtQ(a, C0))))),
          IIntegrate(3780,
              Integrate(Sqrt(
                  Plus(Times($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_)),
                  x_Symbol),
              Condition(
                  Simp(Times(
                      C2, Plus(a, Times(b,
                          Csc(Plus(c, Times(d, x))))),
                      Sqrt(
                          Times(
                              b, Plus(C1, Csc(
                                  Plus(c, Times(d, x)))),
                              Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), CN1))),
                      Sqrt(Times(CN1, b, Subtract(C1, Csc(Plus(c, Times(d, x)))),
                          Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), CN1))),
                      EllipticPi(Times(a, Power(Plus(a, b), CN1)),
                          ArcSin(Times(Rt(Plus(a, b), C2),
                              Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), CN1D2))),
                          Times(Subtract(a, b), Power(Plus(a, b), CN1))),
                      Power(Times(d, Rt(Plus(a, b), C2), Cot(Plus(c, Times(d, x)))), CN1)), x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(a), Sqr(b)), C0)))));
}
