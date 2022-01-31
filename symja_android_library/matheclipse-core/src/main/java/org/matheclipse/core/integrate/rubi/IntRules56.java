package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.C7;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
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
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolynomialDivide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplerSqrtQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules56 {
  public static IAST RULES = List(
      IIntegrate(1121, Integrate(Times(Power(Times(d_DEFAULT, x_), m_DEFAULT),
          Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), p_)),
          x_Symbol),
          Condition(
              Plus(Negate(Simp(
                  Times(Power(Times(d, x), Plus(m, C1)),
                      Plus(Sqr(b), Times(CN1, C2, a, c), Times(b, c, Sqr(x))),
                      Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), Plus(p, C1)),
                      Power(Times(C2, a, d, Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                  x)),
                  Dist(Power(Times(C2, a, Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1),
                      Integrate(
                          Times(Power(Times(d, x), m),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), Plus(p, C1)),
                              Simp(Plus(Times(Sqr(b), Plus(m, Times(C2, p), C3)),
                                  Times(CN1, C2, a, c, Plus(m, Times(C4, p), C5)),
                                  Times(b, c, Plus(m, Times(C4, p), C7), Sqr(x))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, m), x), NeQ(Subtract(Sqr(b), Times(C4, a,
                  c)), C0), LtQ(p, CN1), IntegerQ(Times(C2,
                      p)),
                  Or(IntegerQ(p), IntegerQ(m))))),
      IIntegrate(1122,
          Integrate(
              Times(
                  Power(Times(d_DEFAULT, x_), m_DEFAULT), Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                      Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Power(d, C3), Power(Times(d, x), Subtract(m, C3)),
                          Power(Plus(a, Times(b, Sqr(
                              x)), Times(c,
                                  Power(x, C4))),
                              Plus(p, C1)),
                          Power(Times(c, Plus(m, Times(C4, p), C1)), CN1)),
                      x),
                  Dist(Times(Power(d, C4), Power(Times(c, Plus(m, Times(C4, p), C1)), CN1)),
                      Integrate(
                          Times(Power(Times(d, x), Subtract(m, C4)),
                              Simp(Plus(Times(a, Subtract(m, C3)),
                                  Times(b, Subtract(Plus(m, Times(C2, p)), C1), Sqr(x))), x),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, p), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  GtQ(m, C3), NeQ(Plus(m, Times(C4, p), C1), C0), IntegerQ(Times(C2,
                      p)),
                  Or(IntegerQ(p), IntegerQ(m))))),
      IIntegrate(1123,
          Integrate(
              Times(
                  Power(Times(d_DEFAULT,
                      x_), m_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), p_)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(Power(Times(d, x), Plus(m, C1)),
                      Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                          Plus(p, C1)),
                      Power(Times(a, d, Plus(m, C1)), CN1)), x),
                  Dist(
                      Power(Times(a, Sqr(d),
                          Plus(m, C1)), CN1),
                      Integrate(
                          Times(
                              Power(Times(d, x), Plus(m, C2)),
                              Plus(
                                  Times(b, Plus(m, Times(C2, p), C3)),
                                  Times(c, Plus(m, Times(C4, p), C5), Sqr(x))),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, p), x), NeQ(Subtract(Sqr(b),
                  Times(C4, a, c)), C0), LtQ(m,
                      CN1),
                  IntegerQ(Times(C2, p)), Or(IntegerQ(p), IntegerQ(m))))),
      IIntegrate(1124,
          Integrate(
              Times(
                  Power(Times(d_DEFAULT,
                      x_), m_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), CN1)),
              x_Symbol),
          Condition(Subtract(
              Simp(Times(Power(Times(d, x), Plus(m, C1)), Power(Times(a, d, Plus(m, C1)), CN1)), x),
              Dist(Power(Times(a, Sqr(d)), CN1),
                  Integrate(Times(Power(Times(d, x), Plus(m, C2)), Plus(b, Times(c, Sqr(x))),
                      Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1)), x),
                  x)),
              And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  LtQ(m, CN1)))),
      IIntegrate(1125,
          Integrate(
              Times(
                  Power(x_, m_), Power(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4))),
                      CN1)),
              x_Symbol),
          Condition(
              Integrate(
                  PolynomialDivide(Power(x, m), Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                      x),
                  x),
              And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b),
                  Times(C4, a, c)), C0), IGtQ(m,
                      C5)))),
      IIntegrate(1126,
          Integrate(
              Times(
                  Power(Times(d_DEFAULT,
                      x_), m_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), CN1)),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(Power(d, C3), Power(Times(d, x), Subtract(m, C3)),
                      Power(Times(c, Subtract(m, C3)), CN1)), x),
                  Dist(Times(Power(d, C4), Power(c, CN1)),
                      Integrate(
                          Times(Power(Times(d, x), Subtract(m, C4)), Plus(a, Times(b, Sqr(x))),
                              Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b),
                  Times(C4, a, c)), C0), GtQ(m,
                      C3)))),
      IIntegrate(1127,
          Integrate(
              Times(
                  Sqr(x_), Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                      Times(c_DEFAULT, Power(x_, C4))), CN1)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Times(a, Power(c, CN1)), C2))),
                  Subtract(
                      Dist(C1D2,
                          Integrate(
                              Times(Plus(q, Sqr(x)),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(
                                  Subtract(q, Sqr(
                                      x)),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c), x), LtQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  PosQ(Times(a, c))))),
      IIntegrate(1128,
          Integrate(
              Times(
                  Power(x_, m_DEFAULT), Power(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4))),
                      CN1)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Times(a, Power(c, CN1)), C2))),
                  With(List(Set(r, Rt(Subtract(Times(C2, q), Times(b, Power(c, CN1))), C2))),
                      Plus(
                          Negate(Dist(Power(Times(C2, c, r), CN1),
                              Integrate(Times(Power(x, Subtract(m, C3)), Subtract(q, Times(r, x)),
                                  Power(Plus(q, Times(CN1, r, x), Sqr(x)), CN1)), x),
                              x)),
                          Dist(
                              Power(Times(C2, c,
                                  r), CN1),
                              Integrate(
                                  Times(
                                      Power(x, Subtract(m, C3)), Plus(q, Times(r, x)), Power(
                                          Plus(q, Times(r, x), Sqr(x)), CN1)),
                                  x),
                              x)))),
              And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b),
                  Times(C4, a, c)), C0), GeQ(m,
                      C3),
                  LtQ(m, C4), NegQ(Subtract(Sqr(b), Times(C4, a, c)))))),
      IIntegrate(1129,
          Integrate(
              Times(Power(x_, m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), CN1)),
              x_Symbol),
          Condition(With(List(Set(q, Rt(Times(a, Power(c, CN1)), C2))),
              With(List(Set(r, Rt(Subtract(Times(C2, q), Times(b, Power(c, CN1))), C2))),
                  Subtract(
                      Dist(Power(Times(C2, c, r), CN1),
                          Integrate(Times(Power(x, Subtract(m, C1)),
                              Power(Plus(q, Times(CN1, r, x), Sqr(x)), CN1)), x),
                          x),
                      Dist(Power(Times(C2, c, r), CN1),
                          Integrate(Times(Power(x, Subtract(m, C1)),
                              Power(Plus(q, Times(r, x), Sqr(x)), CN1)), x),
                          x)))),
              And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b),
                  Times(C4, a, c)), C0), GeQ(m,
                      C1),
                  LtQ(m, C3), NegQ(Subtract(Sqr(b), Times(C4, a, c)))))),
      IIntegrate(1130,
          Integrate(
              Times(
                  Power(Times(d_DEFAULT, x_), m_), Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                      Times(c_DEFAULT, Power(x_, C4))), CN1)),
              x_Symbol),
          Condition(
              With(List(Set(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Subtract(
                      Dist(Times(C1D2, Sqr(d), Plus(Times(b, Power(q, CN1)), C1)),
                          Integrate(Times(Power(Times(d, x), Subtract(m, C2)),
                              Power(Plus(Times(C1D2, b), Times(C1D2, q), Times(c, Sqr(x))), CN1)),
                              x),
                          x),
                      Dist(Times(C1D2, Sqr(d), Subtract(Times(b, Power(q, CN1)), C1)),
                          Integrate(Times(Power(Times(d, x), Subtract(m, C2)),
                              Power(Plus(Times(C1D2, b), Times(CN1, C1D2, q), Times(c, Sqr(x))),
                                  CN1)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  GeQ(m, C2)))),
      IIntegrate(1131,
          Integrate(
              Times(
                  Power(Times(d_DEFAULT,
                      x_), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), CN1)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Subtract(
                      Dist(Times(c, Power(q, CN1)),
                          Integrate(
                              Times(Power(Times(d, x), m),
                                  Power(Plus(Times(C1D2, b), Times(CN1, C1D2, q), Times(c, Sqr(x))),
                                      CN1)),
                              x),
                          x),
                      Dist(Times(c, Power(q, CN1)),
                          Integrate(Times(Power(Times(d, x), m),
                              Power(Plus(Times(C1D2, b), Times(C1D2, q), Times(c, Sqr(x))), CN1)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c, d, m), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
      IIntegrate(1132,
          Integrate(
              Times(Sqr(x_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Dist(Times(C2, Sqrt(Negate(c))),
                      Integrate(Times(Sqr(x),
                          Power(Times(Sqrt(Plus(b, q, Times(C2, c, Sqr(x)))),
                              Sqrt(Subtract(Plus(Negate(b), q), Times(C2, c, Sqr(x))))), CN1)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c), x), GtQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  LtQ(c, C0)))),
      IIntegrate(1133,
          Integrate(
              Times(Sqr(x_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Times(c, Power(a, CN1)), C2))),
                  Subtract(Dist(Power(q, CN1),
                      Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2),
                          x),
                      x),
                      Dist(Power(q, CN1),
                          Integrate(
                              Times(Subtract(C1, Times(q, Sqr(x))),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c), x), GtQ(Subtract(Sqr(b), Times(C4, a,
                  c)), C0), GtQ(Times(c,
                      Power(a, CN1)), C0),
                  LtQ(Times(b, Power(a, CN1)), C0)))),
      IIntegrate(1134,
          Integrate(
              Times(Sqr(x_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(List(Set(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Plus(
                      Negate(Dist(Times(Subtract(b, q), Power(Times(C2, c), CN1)),
                          Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2),
                              x),
                          x)),
                      Dist(
                          Power(Times(C2,
                              c), CN1),
                          Integrate(
                              Times(
                                  Plus(b, Negate(q), Times(C2, c, Sqr(x))), Power(Plus(a,
                                      Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                      CN1D2)),
                              x),
                          x))),
              And(FreeQ(List(a, b, c), x), GtQ(Subtract(Sqr(b),
                  Times(C4, a, c)), C0), LtQ(a,
                      C0),
                  GtQ(c, C0)))),
      IIntegrate(1135,
          Integrate(
              Times(Sqr(x_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Condition(
                      Subtract(
                          Simp(
                              Times(x, Plus(b, q, Times(C2, c, Sqr(x))),
                                  Power(
                                      Times(C2, c, Sqrt(Plus(
                                          a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                      CN1)),
                              x),
                          Simp(Times(Rt(Times(Plus(b, q), Power(Times(C2, a), CN1)), C2),
                              Plus(Times(C2, a), Times(Plus(b, q), Sqr(x))),
                              Sqrt(Times(Plus(Times(C2, a), Times(Subtract(b, q), Sqr(x))),
                                  Power(Plus(Times(C2, a), Times(Plus(b, q), Sqr(x))), CN1))),
                              EllipticE(ArcTan(
                                  Times(Rt(Times(Plus(b, q), Power(Times(C2, a), CN1)), C2), x)),
                                  Times(C2, q, Power(Plus(b, q), CN1))),
                              Power(
                                  Times(C2, c,
                                      Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                  CN1)),
                              x)),
                      And(PosQ(Times(Plus(b, q), Power(a, CN1))),
                          Not(And(PosQ(Times(Subtract(b, q), Power(a, CN1))),
                              SimplerSqrtQ(Times(Subtract(b, q), Power(Times(C2, a), CN1)),
                                  Times(Plus(b, q), Power(Times(C2, a), CN1)))))))),
              And(FreeQ(List(a, b, c), x), GtQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
      IIntegrate(1136,
          Integrate(Times(Sqr(x_),
              Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Condition(
                      Subtract(
                          Simp(
                              Times(
                                  x, Plus(b, Negate(q), Times(C2, c,
                                      Sqr(x))),
                                  Power(
                                      Times(
                                          C2, c, Sqrt(Plus(a, Times(b, Sqr(x)),
                                              Times(c, Power(x, C4))))),
                                      CN1)),
                              x),
                          Simp(Times(Rt(Times(Subtract(b, q), Power(Times(C2, a), CN1)), C2),
                              Plus(Times(C2, a), Times(Subtract(b, q), Sqr(x))),
                              Sqrt(Times(Plus(Times(C2, a), Times(Plus(b, q), Sqr(x))),
                                  Power(Plus(Times(C2, a), Times(Subtract(b, q), Sqr(x))), CN1))),
                              EllipticE(
                                  ArcTan(Times(
                                      Rt(Times(Subtract(b, q), Power(Times(C2, a), CN1)), C2), x)),
                                  Times(CN2, q, Power(Subtract(b, q), CN1))),
                              Power(
                                  Times(
                                      C2, c,
                                      Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                  CN1)),
                              x)),
                      PosQ(Times(Subtract(b, q), Power(a, CN1))))),
              And(FreeQ(List(a, b, c), x), GtQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
      IIntegrate(1137,
          Integrate(
              Times(Sqr(x_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(List(Set(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Condition(
                      Plus(
                          Negate(
                              Dist(Times(Plus(b, q), Power(Times(C2, c), CN1)),
                                  Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                      CN1D2), x),
                                  x)),
                          Dist(Power(Times(C2, c), CN1),
                              Integrate(Times(Plus(b, q, Times(C2, c, Sqr(x))),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2)),
                                  x),
                              x)),
                      And(NegQ(Times(Plus(b, q), Power(a, CN1))),
                          Not(And(NegQ(Times(Subtract(b, q), Power(a, CN1))),
                              SimplerSqrtQ(Times(CN1, Subtract(b, q), Power(Times(C2, a), CN1)),
                                  Times(CN1, Plus(b, q), Power(Times(C2, a), CN1)))))))),
              And(FreeQ(List(a, b, c), x), GtQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
      IIntegrate(1138,
          Integrate(
              Times(Sqr(x_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Condition(
                      Plus(
                          Negate(
                              Dist(Times(Subtract(b, q), Power(Times(C2, c), CN1)),
                                  Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                      CN1D2), x),
                                  x)),
                          Dist(Power(Times(C2, c), CN1),
                              Integrate(
                                  Times(Plus(b, Negate(q), Times(C2, c, Sqr(x))),
                                      Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                          CN1D2)),
                                  x),
                              x)),
                      NegQ(Times(Subtract(b, q), Power(a, CN1))))),
              And(FreeQ(List(a, b, c), x), GtQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
      IIntegrate(1139,
          Integrate(
              Times(Sqr(x_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(List(Set(q, Rt(Times(c, Power(a, CN1)), C2))),
                  Subtract(
                      Dist(Power(q, CN1),
                          Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2),
                              x),
                          x),
                      Dist(Power(q, CN1),
                          Integrate(
                              Times(
                                  Subtract(C1, Times(q,
                                      Sqr(x))),
                                  Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2)),
                              x),
                          x))),
              And(FreeQ(List(a, b,
                  c), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                      C0),
                  PosQ(Times(c, Power(a, CN1)))))),
      IIntegrate(1140,
          Integrate(
              Times(Sqr(x_),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4))),
                      CN1D2)),
              x_Symbol),
          Condition(
              With(List(Set(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))), Dist(
                  Times(Sqrt(Plus(C1, Times(C2, c, Sqr(x), Power(Subtract(b, q), CN1)))),
                      Sqrt(Plus(C1,
                          Times(C2, c, Sqr(x), Power(Plus(b, q), CN1)))),
                      Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2)),
                  Integrate(Times(Sqr(x),
                      Power(Times(Sqrt(Plus(C1, Times(C2, c, Sqr(x), Power(Subtract(b, q), CN1)))),
                          Sqrt(Plus(C1, Times(C2, c, Sqr(x), Power(Plus(b, q), CN1))))), CN1)),
                      x),
                  x)),
              And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                  NegQ(Times(c, Power(a, CN1)))))));
}
