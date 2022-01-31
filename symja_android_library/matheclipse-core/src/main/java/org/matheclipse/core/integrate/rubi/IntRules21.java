package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AppellF1;
import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C1D4;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN1D4;
import static org.matheclipse.core.expression.F.CSqrt2;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.EllipticE;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
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
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizePseudoBinomial;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PseudoBinomialPairQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules21 {
  public static IAST RULES =
      List(
          IIntegrate(421,
              Integrate(
                  Times(
                      Power(Plus(a_,
                          Times(b_DEFAULT, Sqr(x_))), CN1D2),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Plus(C1,
                              Times(d, Sqr(x), Power(c, CN1)))),
                          Power(Plus(c, Times(d, Sqr(x))), CN1D2)),
                      Integrate(
                          Power(
                              Times(
                                  Sqrt(Plus(a, Times(b, Sqr(x)))), Sqrt(Plus(C1, Times(d, Sqr(x),
                                      Power(c, CN1))))),
                              CN1),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), Not(GtQ(c, C0))))),
          IIntegrate(422,
              Integrate(
                  Times(
                      Sqrt(Plus(a_,
                          Times(b_DEFAULT, Sqr(x_)))),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(a,
                          Integrate(
                              Power(
                                  Times(Sqrt(Plus(a, Times(b, Sqr(x)))),
                                      Sqrt(Plus(c, Times(d, Sqr(x))))),
                                  CN1),
                              x),
                          x),
                      Dist(b,
                          Integrate(
                              Times(Sqr(x),
                                  Power(Times(Sqrt(Plus(a, Times(b, Sqr(x)))),
                                      Sqrt(Plus(c, Times(d, Sqr(x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), PosQ(Times(d,
                      Power(c, CN1))), PosQ(
                          Times(b, Power(a, CN1)))))),
          IIntegrate(423,
              Integrate(
                  Times(
                      Sqrt(Plus(a_,
                          Times(b_DEFAULT, Sqr(x_)))),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(b, Power(d, CN1)),
                          Integrate(
                              Times(Sqrt(Plus(c, Times(d, Sqr(x)))),
                                  Power(Plus(a, Times(b, Sqr(x))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(b, c), Times(a, d)), Power(d,
                              CN1)),
                          Integrate(
                              Power(
                                  Times(
                                      Sqrt(Plus(a, Times(b, Sqr(x)))), Sqrt(
                                          Plus(c, Times(d, Sqr(x))))),
                                  CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), PosQ(Times(d,
                      Power(c, CN1))), NegQ(
                          Times(b, Power(a, CN1)))))),
          IIntegrate(424,
              Integrate(
                  Times(
                      Sqrt(Plus(a_,
                          Times(b_DEFAULT, Sqr(x_)))),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Simp(Times(Sqrt(a),
                      EllipticE(ArcSin(Times(Rt(Times(CN1, d, Power(c, CN1)), C2), x)), Times(b, c,
                          Power(Times(a, d), CN1))),
                      Power(Times(Sqrt(c), Rt(Times(CN1, d, Power(c, CN1)), C2)), CN1)), x),
                  And(FreeQ(List(a, b, c, d), x), NegQ(Times(d, Power(c, CN1))), GtQ(c, C0),
                      GtQ(a, C0)))),
          IIntegrate(425,
              Integrate(
                  Times(Sqrt(Plus(a_, Times(b_DEFAULT, Sqr(x_)))),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(Sqrt(Subtract(a, Times(b, c, Power(d, CN1)))),
                              EllipticE(
                                  ArcCos(Times(Rt(Times(CN1, d, Power(c, CN1)), C2), x)), Times(b,
                                      c, Power(Subtract(Times(b, c), Times(a, d)), CN1))),
                              Power(Times(Sqrt(c), Rt(Times(CN1, d, Power(c, CN1)), C2)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NegQ(Times(d, Power(c, CN1))), GtQ(c, C0),
                      GtQ(Subtract(a, Times(b, c, Power(d, CN1))), C0)))),
          IIntegrate(426,
              Integrate(
                  Times(
                      Sqrt(Plus(a_,
                          Times(b_DEFAULT, Sqr(x_)))),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Plus(a,
                              Times(b, Sqr(x)))),
                          Power(Plus(C1, Times(b, Sqr(x), Power(a, CN1))), CN1D2)),
                      Integrate(
                          Times(
                              Sqrt(Plus(C1,
                                  Times(b, Sqr(x), Power(a, CN1)))),
                              Power(Plus(c, Times(d, Sqr(x))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NegQ(Times(d,
                      Power(c, CN1))), GtQ(c,
                          C0),
                      Not(GtQ(a, C0))))),
          IIntegrate(427,
              Integrate(
                  Times(
                      Sqrt(Plus(a_, Times(b_DEFAULT, Sqr(x_)))), Power(
                          Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Plus(C1,
                              Times(d, Sqr(x), Power(c, CN1)))),
                          Power(Plus(c, Times(d, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(Sqrt(Plus(a, Times(b, Sqr(x)))),
                              Power(Plus(C1, Times(d, Sqr(x), Power(c, CN1))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NegQ(Times(d, Power(c, CN1))), Not(GtQ(c, C0))))),
          IIntegrate(428,
              Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                  Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_)), x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times(Power(Plus(a, Times(b, Power(x, n))), p),
                          Power(Plus(c, Times(d, Power(x, n))), q)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, n, q), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      IGtQ(p, C0)))),
          IIntegrate(429,
              Integrate(
                  Times(
                      Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(a, p), Power(c, q), x,
                          AppellF1(Power(n, CN1), Negate(p), Negate(q), Plus(C1, Power(n, CN1)),
                              Times(CN1, b, Power(x, n), Power(a, CN1)), Times(CN1, d, Power(x, n),
                                  Power(c, CN1)))),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p, q), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(n, CN1), Or(IntegerQ(p), GtQ(
                          a, C0)),
                      Or(IntegerQ(q), GtQ(c, C0))))),
          IIntegrate(430,
              Integrate(
                  Times(
                      Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, IntPart(p)),
                          Power(Plus(a, Times(b, Power(x, n))), FracPart(p)),
                          Power(
                              Power(Plus(C1, Times(b, Power(x, n), Power(a, CN1))),
                                  FracPart(p)),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(C1, Times(b, Power(x, n), Power(a, CN1))), p),
                              Power(Plus(c, Times(d, Power(x, n))), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p, q), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      NeQ(n, CN1), Not(Or(IntegerQ(p), GtQ(a, C0)))))),
          IIntegrate(431,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(u_, n_))), p_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, Power(u_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Coefficient(u, x, C1), CN1),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))), q)), x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p, q), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(
              432, Integrate(Times(Power(u_, p_DEFAULT),
                  Power(v_, q_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(NormalizePseudoBinomial(u,
                              x), p),
                          Power(NormalizePseudoBinomial(v, x), q)),
                      x),
                  And(FreeQ(List(p, q), x), PseudoBinomialPairQ(u, v, x)))),
          IIntegrate(433,
              Integrate(
                  Times(Power(u_, p_DEFAULT), Power(v_, q_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(
                              NormalizePseudoBinomial(Times(Power(x, Times(m, Power(p, CN1))), u),
                                  x),
                              p),
                          Power(NormalizePseudoBinomial(v, x), q)),
                      x),
                  And(FreeQ(List(p, q), x), IntegersQ(p, Times(m,
                      Power(p, CN1))), PseudoBinomialPairQ(
                          Times(Power(x, Times(m, Power(p, CN1))), u), v, x)))),
          IIntegrate(434,
              Integrate(
                  Times(
                      Power(Plus(c_, Times(d_DEFAULT,
                          Power(x_, $p("mn", true)))), q_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(a, Times(b, Power(x, n))), p),
                          Power(Plus(d, Times(c, Power(x, n))), q), Power(Power(x, Times(n, q)),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, n,
                      p), x), EqQ($s("mn"),
                          Negate(n)),
                      IntegerQ(q), Or(PosQ(n), Not(IntegerQ(p)))))),
          IIntegrate(435,
              Integrate(
                  Times(
                      Power(Plus(c_, Times(d_DEFAULT,
                          Power(x_, $p("mn", true)))), q_),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(x, Times(n, FracPart(q))),
                          Power(Plus(c, Times(d, Power(Power(x, n), CN1))), FracPart(
                              q)),
                          Power(Power(Plus(d, Times(c, Power(x, n))), FracPart(q)), CN1)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(d,
                                  Times(c, Power(x, n))), q),
                              Power(Power(x, Times(n, q)), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p,
                      q), x), EqQ($s("mn"),
                          Negate(n)),
                      Not(IntegerQ(q)), Not(IntegerQ(p))))),
          IIntegrate(436,
              Integrate(Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                  Power(Times(b_DEFAULT, Power(x_,
                      n_)), p_),
                  Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)), x_Symbol),
              Condition(
                  Dist(
                      Times(Power(e, m),
                          Power(
                              Times(n,
                                  Power(
                                      b, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                                          C1))),
                              CN1)),
                      Subst(
                          Integrate(Times(Power(Times(b, x),
                              Subtract(Plus(p, Simplify(Times(Plus(m, C1), Power(n, CN1)))), C1)),
                              Power(Plus(c, Times(d, x)), q)), x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(b, c, d, e, m, n, p, q), x), Or(IntegerQ(m), GtQ(e, C0)),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(437,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Times(b_DEFAULT, Power(x_, n_DEFAULT)), p_), Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(e, m), Power(b, IntPart(p)),
                          Power(Times(b, Power(x, n)),
                              FracPart(p)),
                          Power(Power(x, Times(n, FracPart(p))), CN1)),
                      Integrate(
                          Times(
                              Power(x, Plus(m, Times(n, p))), Power(Plus(c, Times(d, Power(x, n))),
                                  q)),
                          x),
                      x),
                  And(FreeQ(List(b, c, d, e, m, n, p, q), x), Or(IntegerQ(m), GtQ(e,
                      C0)), Not(
                          IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1)))))))),
          IIntegrate(438,
              Integrate(Times(Power(Times(e_, x_), m_),
                  Power(Times(b_DEFAULT, Power(x_, n_DEFAULT)), p_), Power(
                      Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(
                              Power(x, m), Power(Times(b, Power(x, n)),
                                  p),
                              Power(Plus(c, Times(d, Power(x, n))), q)),
                          x),
                      x),
                  And(FreeQ(List(b, c, d, e, m, n, p, q), x), Not(IntegerQ(m))))),
          IIntegrate(439,
              Integrate(
                  Times(
                      x_, Power(Plus(a_,
                          Times(b_DEFAULT, Sqr(x_))), CN1D4),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(Times(ArcTan(
                              Times(Subtract(Sqr(Rt(a, C4)), Sqrt(Plus(a, Times(b, Sqr(x))))),
                                  Power(Times(CSqrt2, Rt(a, C4),
                                      Power(Plus(a, Times(b, Sqr(x))), C1D4)), CN1))),
                              Power(Times(CSqrt2, Rt(a, C4), d), CN1)), x)),
                      Simp(
                          Times(C1,
                              ArcTanh(Times(Plus(Sqr(Rt(a, C4)), Sqrt(Plus(a, Times(b, Sqr(x))))),
                                  Power(Times(CSqrt2, Rt(a, C4),
                                      Power(Plus(a, Times(b, Sqr(x))), C1D4)), CN1))),
                              Power(Times(CSqrt2, Rt(a, C4), d), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Times(b, c), Times(C2, a, d)), C0),
                      PosQ(a)))),
          IIntegrate(440,
              Integrate(
                  Times(
                      Power(x_, m_), Power(Plus(a_, Times(b_DEFAULT, Sqr(x_))), CN1D4),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Times(Power(x, m),
                          Power(Times(Power(Plus(a, Times(b, Sqr(x))), C1D4),
                              Plus(c, Times(d, Sqr(x)))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Subtract(Times(b, c), Times(C2, a, d)), C0),
                      IntegerQ(m), Or(PosQ(a), IntegerQ(Times(C1D2, m)))))));
}
