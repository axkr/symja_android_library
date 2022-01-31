package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C10;
import static org.matheclipse.core.expression.F.C1D3;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C6;
import static org.matheclipse.core.expression.F.C8;
import static org.matheclipse.core.expression.F.C9;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CSqrt2;
import static org.matheclipse.core.expression.F.CSqrt3;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.ZZ;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntBinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplerSqrtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules24 {
  public static IAST RULES =
      List(
          IIntegrate(481,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, n_))), CN1),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(
                              Times(a, Power(e, n), Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                              Integrate(
                                  Times(Power(Times(e, x), Subtract(m, n)),
                                      Power(Plus(a, Times(b, Power(x, n))), CN1)),
                                  x),
                              x)),
                      Dist(Times(c, Power(e, n), Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(Power(Times(e, x), Subtract(m, n)),
                                  Power(Plus(c, Times(d, Power(x, n))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), IGtQ(n,
                          C0),
                      LeQ(n, m, Subtract(Times(C2, n), C1))))),
          IIntegrate(482,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, n_))), CN1),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(
                              Times(Power(Times(e, x), m),
                                  Power(Plus(a, Times(b, Power(x, n))), CN1)),
                              x),
                          x),
                      Dist(
                          Times(d, Power(Subtract(Times(b, c), Times(a, d)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(e, x), m), Power(Plus(c, Times(d, Power(x, n))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), IGtQ(n,
                          C0)))),
          IIntegrate(483,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_),
                      Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, n_))), CN1),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(Power(e, n), Power(b, CN1)),
                          Integrate(
                              Times(Power(Times(e, x), Subtract(m, n)),
                                  Power(Plus(c, Times(d, Power(x, n))), q)),
                              x),
                          x),
                      Dist(
                          Times(a, Power(e, n), Power(b,
                              CN1)),
                          Integrate(Times(Power(Times(e, x), Subtract(m, n)),
                              Power(Plus(c, Times(d, Power(x, n))),
                                  q),
                              Power(Plus(a, Times(b, Power(x, n))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, q), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), IGtQ(n, C0),
                      LeQ(n, m, Subtract(Times(C2, n), C1)), IntBinomialQ(a, b, c, d, e, m, n, CN1,
                          q, x)))),
          IIntegrate(484,
              Integrate(
                  Times(
                      x_, Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, C3))), CN1),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, C3))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(List(Set(q, Rt(Times(d, Power(c, CN1)), C3))),
                      Plus(
                          Simp(
                              Times(q,
                                  ArcTanh(
                                      Times(
                                          Sqrt(Plus(c,
                                              Times(d, Power(x, C3)))),
                                          Power(Rt(c, C2), CN1))),
                                  Power(Times(C9, Power(C2, QQ(2L, 3L)), b, Rt(c, C2)), CN1)),
                              x),
                          Negate(
                              Simp(
                                  Times(q,
                                      ArcTanh(Times(Rt(c, C2),
                                          Subtract(C1, Times(Power(C2, C1D3), q, x)),
                                          Power(Plus(c, Times(d, Power(x, C3))), CN1D2))),
                                      Power(Times(C3, Power(C2, QQ(2L, 3L)), b, Rt(c, C2)), CN1)),
                                  x)),
                          Simp(
                              Times(q,
                                  ArcTan(
                                      Times(
                                          Sqrt(Plus(c, Times(d, Power(x, C3)))), Power(
                                              Times(CSqrt3, Rt(c, C2)), CN1))),
                                  Power(Times(C3, Power(C2, QQ(2L, 3L)), CSqrt3, b, Rt(c, C2)),
                                      CN1)),
                              x),
                          Negate(Simp(
                              Times(q,
                                  ArcTan(Times(
                                      CSqrt3, Rt(c, C2), Plus(C1, Times(Power(C2, C1D3), q, x)),
                                      Power(Plus(c, Times(d, Power(x, C3))), CN1D2))),
                                  Power(Times(C3, Power(C2, QQ(2L, 3L)), CSqrt3, b, Rt(c, C2)),
                                      CN1)),
                              x)))),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), EqQ(Subtract(Times(C4, b, c), Times(a, d)),
                          C0),
                      PosQ(c)))),
          IIntegrate(485,
              Integrate(
                  Times(
                      x_, Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1), Power(
                          Plus(c_, Times(d_DEFAULT, Power(x_, C3))), CN1D2)),
                  x_Symbol),
              Condition(With(List(Set(q, Rt(Times(d, Power(c, CN1)), C3))),
                  Plus(
                      Negate(
                          Simp(
                              Times(q,
                                  ArcTan(
                                      Times(
                                          Sqrt(Plus(c, Times(d, Power(x, C3)))), Power(
                                              Rt(Negate(c), C2), CN1))),
                                  Power(Times(C9, Power(C2, QQ(2L, 3L)), b, Rt(Negate(c), C2)),
                                      CN1)),
                              x)),
                      Negate(
                          Simp(
                              Times(q,
                                  ArcTan(
                                      Times(Rt(Negate(c), C2),
                                          Subtract(C1, Times(Power(C2, C1D3), q, x)), Power(
                                              Plus(c, Times(d, Power(x, C3))), CN1D2))),
                                  Power(Times(C3, Power(C2, QQ(2L, 3L)), b, Rt(Negate(c), C2)),
                                      CN1)),
                              x)),
                      Negate(
                          Simp(
                              Times(q,
                                  ArcTanh(
                                      Times(
                                          Sqrt(Plus(c,
                                              Times(d, Power(x, C3)))),
                                          Power(Times(CSqrt3, Rt(Negate(c), C2)), CN1))),
                                  Power(Times(C3, Power(C2, QQ(2L, 3L)), CSqrt3, b,
                                      Rt(Negate(c), C2)), CN1)),
                              x)),
                      Negate(
                          Simp(Times(q,
                              ArcTanh(Times(CSqrt3, Rt(Negate(c), C2),
                                  Plus(C1, Times(Power(C2, C1D3), q, x)), Power(
                                      Plus(c, Times(d, Power(x, C3))), CN1D2))),
                              Power(
                                  Times(C3, Power(C2, QQ(2L, 3L)), CSqrt3, b,
                                      Rt(Negate(c), C2)),
                                  CN1)),
                              x)))),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0), EqQ(Subtract(Times(C4, b, c),
                          Times(a, d)), C0),
                      NegQ(c)))),
          IIntegrate(486,
              Integrate(Times(x_, Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1),
                  Power(Plus(c_, Times(d_DEFAULT, Power(x_, C3))), CN1D2)), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(d, Power(c, CN1)), C3))),
                      Plus(
                          Dist(
                              Times(d, q, Power(Times(C4, b),
                                  CN1)),
                              Integrate(
                                  Times(Sqr(x),
                                      Power(
                                          Times(Subtract(Times(C8, c), Times(d, Power(x,
                                              C3))), Sqrt(Plus(c,
                                                  Times(d, Power(x, C3))))),
                                          CN1)),
                                  x),
                              x),
                          Negate(
                              Dist(
                                  Times(Sqr(q), Power(Times(ZZ(12L), b),
                                      CN1)),
                                  Integrate(
                                      Times(
                                          Plus(C1, Times(q,
                                              x)),
                                          Power(
                                              Times(Subtract(C2, Times(q, x)), Sqrt(Plus(c,
                                                  Times(d, Power(x, C3))))),
                                              CN1)),
                                      x),
                                  x)),
                          Dist(Power(Times(ZZ(12L), b, c), CN1),
                              Integrate(Times(
                                  Subtract(Subtract(Times(C2, c, Sqr(q)), Times(C2, d, x)),
                                      Times(d, q, Sqr(x))),
                                  Power(Times(Plus(C4, Times(C2, q, x), Times(Sqr(q), Sqr(x))),
                                      Sqrt(Plus(c, Times(d, Power(x, C3))))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Plus(Times(C8, b, c), Times(a, d)), C0)))),
          IIntegrate(487,
              Integrate(Times(
                  x_, Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1D2),
                  Power(Plus(c_, Times(d_DEFAULT, Power(x_, C3))), CN1)), x_Symbol),
              Condition(
                  With(List(Set(q, Rt(Times(b, Power(a, CN1)), C3)),
                      Set(r,
                          Simplify(Times(Subtract(Times(b, c), Times(C10, a, d)),
                              Power(Times(C6, a, d), CN1))))),
                      Plus(
                          Negate(
                              Simp(Times(q, Subtract(C2, r),
                                  ArcTan(Times(Subtract(C1, r),
                                      Sqrt(Plus(a, Times(b, Power(x, C3)))), Power(Times(CSqrt2,
                                          Rt(a, C2), Power(r, QQ(3L, 2L))), CN1))),
                                  Power(Times(C3, CSqrt2, Rt(a, C2), d, Power(r, QQ(3L, 2L))),
                                      CN1)),
                                  x)),
                          Negate(
                              Simp(
                                  Times(q, Subtract(C2, r),
                                      ArcTan(Times(Rt(a, C2), Sqrt(r), Plus(C1, r),
                                          Plus(C1, Times(q, x)),
                                          Power(
                                              Times(CSqrt2, Sqrt(Plus(a, Times(b, Power(x, C3))))),
                                              CN1))),
                                      Power(
                                          Times(C2, CSqrt2, Rt(a, C2), d,
                                              Power(r, QQ(3L, 2L))),
                                          CN1)),
                                  x)),
                          Negate(
                              Simp(
                                  Times(q, Subtract(C2, r),
                                      ArcTanh(
                                          Times(Rt(a, C2), Sqrt(r),
                                              Subtract(Plus(C1, r), Times(C2, q, x)),
                                              Power(
                                                  Times(CSqrt2,
                                                      Sqrt(Plus(a, Times(b, Power(x, C3))))),
                                                  CN1))),
                                      Power(Times(C3, CSqrt2, Rt(a, C2), d, Sqrt(r)), CN1)),
                                  x)),
                          Negate(
                              Simp(Times(q, Subtract(C2, r),
                                  ArcTanh(Times(Rt(a, C2), Subtract(C1, r), Sqrt(r),
                                      Plus(C1, Times(q, x)),
                                      Power(Times(CSqrt2, Sqrt(Plus(a, Times(b, Power(x, C3))))),
                                          CN1))),
                                  Power(Times(C6, CSqrt2, Rt(a, C2), d, Sqrt(r)), CN1)), x)))),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Subtract(Times(Sqr(b), Sqr(c)), Times(ZZ(20L), a, b, c, d)),
                          Times(C8, Sqr(a), Sqr(d))), C0),
                      PosQ(a)))),
          IIntegrate(488,
              Integrate(Times(
                  x_, Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1D2),
                  Power(Plus(c_, Times(d_DEFAULT, Power(x_, C3))), CN1)), x_Symbol),
              Condition(
                  With(List(Set(q, Rt(Times(b, Power(a, CN1)), C3)),
                      Set(r,
                          Simplify(Times(Subtract(Times(b, c), Times(C10, a, d)),
                              Power(Times(C6, a, d), CN1))))),
                      Plus(
                          Simp(
                              Times(q, Subtract(C2, r),
                                  ArcTanh(
                                      Times(Subtract(C1, r), Sqrt(Plus(a, Times(b, Power(x, C3)))),
                                          Power(Times(CSqrt2, Rt(Negate(a), C2),
                                              Power(r, QQ(3L, 2L))), CN1))),
                                  Power(
                                      Times(C3, CSqrt2, Rt(Negate(
                                          a), C2), d, Power(r,
                                              QQ(3L, 2L))),
                                      CN1)),
                              x),
                          Negate(
                              Simp(
                                  Times(q, Subtract(C2, r),
                                      ArcTanh(Times(Rt(Negate(a), C2), Sqrt(r), Plus(C1, r),
                                          Plus(C1, Times(q, x)),
                                          Power(
                                              Times(CSqrt2, Sqrt(Plus(a, Times(b, Power(x, C3))))),
                                              CN1))),
                                      Power(
                                          Times(
                                              C2, CSqrt2, Rt(Negate(
                                                  a), C2),
                                              d, Power(r, QQ(3L, 2L))),
                                          CN1)),
                                  x)),
                          Negate(
                              Simp(
                                  Times(q, Subtract(C2, r),
                                      ArcTan(Times(Rt(Negate(a), C2), Sqrt(r),
                                          Subtract(Plus(C1, r), Times(C2, q, x)),
                                          Power(
                                              Times(CSqrt2, Sqrt(Plus(a, Times(b, Power(x, C3))))),
                                              CN1))),
                                      Power(Times(C3, CSqrt2, Rt(Negate(a), C2), d, Sqrt(r)), CN1)),
                                  x)),
                          Negate(Simp(
                              Times(q, Subtract(C2, r),
                                  ArcTan(Times(Rt(Negate(a), C2), Subtract(C1, r), Sqrt(r),
                                      Plus(C1, Times(q, x)),
                                      Power(Times(CSqrt2, Sqrt(Plus(a, Times(b, Power(x, C3))))),
                                          CN1))),
                                  Power(Times(C6, CSqrt2, Rt(Negate(a), C2), d, Sqrt(r)), CN1)),
                              x)))),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Subtract(Subtract(Times(Sqr(b), Sqr(c)), Times(ZZ(20L), a, b, c, d)),
                          Times(C8, Sqr(a), Sqr(d))), C0),
                      NegQ(a)))),
          IIntegrate(489,
              Integrate(
                  Times(
                      x_, Sqrt(Plus(a_, Times(b_DEFAULT,
                          Power(x_, C3)))),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(b, Power(d, CN1)), Integrate(
                              Times(x, Power(Plus(a, Times(b, Power(x, C3))), CN1D2)), x),
                          x),
                      Dist(
                          Times(Subtract(Times(b, c), Times(a, d)), Power(d,
                              CN1)),
                          Integrate(Times(x,
                              Power(Times(Plus(c, Times(d, Power(x, C3))),
                                  Sqrt(Plus(a, Times(b, Power(x, C3))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(c, d, a, b), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), Or(
                          EqQ(Subtract(Times(b, c), Times(C4, a,
                              d)), C0),
                          EqQ(Plus(Times(b, c), Times(C8, a, d)), C0),
                          EqQ(Subtract(
                              Subtract(Times(Sqr(b), Sqr(c)), Times(ZZ(
                                  20L), a, b, c, d)),
                              Times(C8, Sqr(a), Sqr(d))), C0))))),
          IIntegrate(490,
              Integrate(
                  Times(
                      Sqr(x_), Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, C4))), CN1),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, C4))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, Numerator(
                              Rt(Times(CN1, a, Power(b, CN1)), C2))),
                          Set(s, Denominator(Rt(Times(CN1, a, Power(b, CN1)), C2)))),
                      Subtract(
                          Dist(Times(s, Power(Times(C2, b), CN1)),
                              Integrate(Power(Times(Plus(r, Times(s, Sqr(x))),
                                  Sqrt(Plus(c, Times(d, Power(x, C4))))), CN1), x),
                              x),
                          Dist(Times(s, Power(Times(C2, b), CN1)),
                              Integrate(Power(Times(Subtract(r, Times(s, Sqr(x))),
                                  Sqrt(Plus(c, Times(d, Power(x, C4))))), CN1), x),
                              x))),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(491,
              Integrate(
                  Times(
                      Sqr(x_), Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, C4))), CN1),
                      Sqrt(Plus(c_, Times(d_DEFAULT, Power(x_, C4))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(d, Power(b, CN1)),
                          Integrate(Times(Sqr(x), Power(Plus(c, Times(d, Power(x, C4))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(b, c), Times(a,
                              d)), Power(b,
                                  CN1)),
                          Integrate(
                              Times(Sqr(x),
                                  Power(
                                      Times(
                                          Plus(a, Times(b, Power(x, C4))), Sqrt(Plus(c,
                                              Times(d, Power(x, C4))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(492,
              Integrate(
                  Times(
                      Sqr(x_), Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1D2), Power(
                          Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(x, Sqrt(Plus(a, Times(b, Sqr(x)))),
                              Power(Times(b, Sqrt(Plus(c, Times(d, Sqr(x))))), CN1)),
                          x),
                      Dist(
                          Times(c, Power(b,
                              CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(a, Times(b, Sqr(x)))), Power(Plus(c, Times(d, Sqr(x))),
                                      QQ(-3L, 2L))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      PosQ(Times(b, Power(a,
                          CN1))),
                      PosQ(Times(d, Power(c, CN1))), Not(SimplerSqrtQ(Times(b, Power(a, CN1)),
                          Times(d, Power(c, CN1))))))),
          IIntegrate(493,
              Integrate(
                  Times(
                      Power(x_, n_), Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, n_))), CN1D2),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), CN1D2)),
                  x_Symbol),
              Condition(Subtract(
                  Dist(Power(b, CN1),
                      Integrate(Times(Sqrt(Plus(a, Times(b, Power(x, n)))),
                          Power(Plus(c, Times(d, Power(x, n))), CN1D2)), x),
                      x),
                  Dist(Times(a, Power(b, CN1)),
                      Integrate(Power(Times(Sqrt(Plus(a, Times(b, Power(x, n)))),
                          Sqrt(Plus(c, Times(d, Power(x, n))))), CN1), x),
                      x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      Or(EqQ(n, C2), EqQ(n, C4)), Not(
                          And(EqQ(n, C2),
                              SimplerSqrtQ(Times(CN1, b, Power(a, CN1)),
                                  Times(CN1, d, Power(c, CN1)))))))),
          IIntegrate(494,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set(k, Denominator(p))),
                      Dist(
                          Times(
                              k, Power(a, Plus(p,
                                  Times(Plus(m, C1), Power(n, CN1)))),
                              Power(n, CN1)),
                          Subst(Integrate(Times(
                              Power(x, Subtract(Times(k, Plus(m, C1), Power(n, CN1)), C1)),
                              Power(Subtract(c,
                                  Times(Subtract(Times(b, c), Times(a, d)), Power(x, k))), q),
                              Power(
                                  Power(Subtract(C1, Times(b, Power(x, k))),
                                      Plus(p, q, Times(Plus(m, C1), Power(n, CN1)), C1)),
                                  CN1)),
                              x), x,
                              Times(
                                  Power(x, Times(n,
                                      Power(k, CN1))),
                                  Power(Power(Plus(a, Times(b, Power(x, n))), Power(k, CN1)),
                                      CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(n, C0), RationalQ(m,
                      p), IntegersQ(Plus(p, Times(Plus(m, C1), Power(n, CN1))), q),
                      LtQ(CN1, p, C0)))),
          IIntegrate(495,
              Integrate(
                  Times(Power(x_, m_DEFAULT), Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, Power(Power(x, n), CN1))), p),
                              Power(Plus(c, Times(d, Power(Power(x, n), CN1))), q),
                              Power(Power(x, Plus(m, C2)), CN1)), x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, c, d, p, q), x), NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0), ILtQ(n,
                          C0),
                      IntegerQ(m)))),
          IIntegrate(496,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_),
                      Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(g,
                          Denominator(m))),
                      Negate(
                          Dist(
                              Times(g, Power(e,
                                  CN1)),
                              Subst(
                                  Integrate(
                                      Times(
                                          Power(
                                              Plus(a,
                                                  Times(b,
                                                      Power(
                                                          Times(Power(e, n), Power(x, Times(g, n))),
                                                          CN1))),
                                              p),
                                          Power(Plus(c,
                                              Times(d,
                                                  Power(Times(Power(e, n), Power(x, Times(g, n))),
                                                      CN1))),
                                              q),
                                          Power(Power(x, Plus(Times(g, Plus(m, C1)), C1)), CN1)),
                                      x),
                                  x, Power(Power(Times(e, x), Power(g, CN1)), CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, p, q), x), ILtQ(n, C0), FractionQ(m)))),
          IIntegrate(497,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_),
                      Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Times(Power(Times(e, x), m), Power(Power(x, CN1), m)),
                          Subst(Integrate(
                              Times(Power(Plus(a, Times(b, Power(Power(x, n), CN1))), p),
                                  Power(Plus(c, Times(d, Power(Power(x, n), CN1))), q),
                                  Power(Power(x, Plus(m, C2)), CN1)),
                              x), x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, p, q), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), ILtQ(n, C0),
                      Not(RationalQ(m))))),
          IIntegrate(498,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_)),
                  x_Symbol),
              Condition(With(List(Set(g, Denominator(n))),
                  Dist(g,
                      Subst(Integrate(Times(Power(x, Subtract(Times(g, Plus(m, C1)), C1)),
                          Power(Plus(a, Times(b, Power(x, Times(g, n)))),
                              p),
                          Power(Plus(c, Times(d, Power(x, Times(g, n)))), q)), x), x,
                          Power(x, Power(g, CN1))),
                      x)),
                  And(FreeQ(List(a, b, c, d, m, p, q), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), FractionQ(n)))),
          IIntegrate(499,
              Integrate(
                  Times(Power(Times(e_, x_), m_),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_), Power(
                          Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(
                              Power(x, m), Power(Plus(a,
                                  Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, p,
                      q), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0),
                      FractionQ(n)))),
          IIntegrate(500,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Plus(m,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a,
                                          Times(b,
                                              Power(x,
                                                  Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                      p),
                                  Power(
                                      Plus(c,
                                          Times(d,
                                              Power(x,
                                                  Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                      q)),
                              x),
                          x, Power(x, Plus(m, C1))),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n, p, q), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      IntegerQ(Simplify(Times(n, Power(Plus(m, C1), CN1)))), Not(IntegerQ(n))))));
}
