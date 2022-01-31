package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcSin;
import static org.matheclipse.core.expression.F.B_DEFAULT;
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
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.EllipticPi;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.CSymbol;
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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules150 {
  public static IAST RULES =
      List(
          IIntegrate(3001,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(
                              Subtract(Times(ASymbol, b), Times(a,
                                  BSymbol)),
                              Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), CN1),
                              x),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(BSymbol, c), Times(ASymbol,
                                  d)),
                              Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                          Integrate(Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3002,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(BSymbol, Power(d, CN1)),
                          Integrate(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m), x), x),
                      Dist(Times(Subtract(Times(BSymbol, c), Times(ASymbol, d)), Power(d, CN1)),
                          Integrate(Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m), x),
                      NeQ(Subtract(Times(b, c), Times(a,
                          d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3003,
              Integrate(
                  Times(
                      Sqrt(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, $($s("§sin"),
                                      Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(CN2, BSymbol, Cos(Plus(e, Times(f, x))),
                              Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                              Power(Plus(c,
                                  Times(d, Sin(Plus(e, Times(f, x))))), n),
                              Power(Times(f, Plus(Times(C2, n), C3)), CN1)),
                          x),
                      Dist(
                          Power(Plus(Times(C2,
                              n), C3), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Subtract(n,
                                      C1)),
                                  Simp(
                                      Plus(Times(a, ASymbol, c, Plus(Times(C2, n), C3)),
                                          Times(BSymbol,
                                              Plus(Times(b, c), Times(C2, a, d, n))),
                                          Times(
                                              Plus(
                                                  Times(BSymbol, Plus(Times(a, c), Times(b, d)),
                                                      Plus(Times(C2, n), C1)),
                                                  Times(ASymbol, Plus(Times(b, c), Times(a, d)),
                                                      Plus(Times(C2, n), C3))),
                                              Sin(Plus(e, Times(f, x)))),
                                          Times(
                                              Plus(Times(ASymbol, b, d, Plus(Times(C2, n), C3)),
                                                  Times(BSymbol,
                                                      Plus(Times(a, d), Times(C2, b, c, n)))),
                                              Sqr(Sin(Plus(e, Times(f, x)))))),
                                      x),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      NeQ(Subtract(Sqr(a), Sqr(
                          b)), C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0), EqQ(Sqr(n), C1D4)))),
          IIntegrate(3004,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), CN1D2),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Plus(A_,
                          Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(C4, ASymbol,
                          EllipticPi(CN1,
                              Negate(ArcSin(Times(Cos(Plus(e, Times(f, x))),
                                  Power(Plus(C1, Sin(Plus(e, Times(f, x)))), CN1)))),
                              Times(CN1, Subtract(a, b), Power(Plus(a, b), CN1))),
                          Power(Times(f, Sqrt(Plus(a, b))), CN1)),
                      x),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol), x), GtQ(b, C0), GtQ(Subtract(Sqr(b),
                      Sqr(a)), C0), EqQ(ASymbol,
                          BSymbol)))),
          IIntegrate(3005,
              Integrate(
                  Times(
                      Power(Times(d_,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), CN1D2),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Plus(A_, Times(B_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Sin(
                              Plus(e, Times(f, x)))),
                          Power(Times(d, Sin(Plus(e, Times(f, x)))), CN1D2)),
                      Integrate(Times(Plus(ASymbol, Times(BSymbol, Sin(Plus(e, Times(f, x))))),
                          Power(Times(Sqrt(Sin(Plus(e, Times(f, x)))),
                              Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, e, f, d, ASymbol, BSymbol), x), GtQ(b, C0),
                      GtQ(Subtract(Sqr(b), Sqr(a)), C0), EqQ(ASymbol, BSymbol)))),
          IIntegrate(3006,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(BSymbol, Power(d,
                              CN1)),
                          Integrate(
                              Times(
                                  Sqrt(Plus(c, Times(d,
                                      Sin(Plus(e, Times(f, x)))))),
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Times(BSymbol, c), Times(ASymbol, d)), Power(d,
                              CN1)),
                          Integrate(
                              Power(Times(
                                  Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))), Sqrt(Plus(c,
                                      Times(d, Sin(Plus(e, Times(f, x))))))),
                                  CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3007,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(A_DEFAULT,
                          Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                          Plus(ASymbol, Times(BSymbol,
                              Sin(Plus(e, Times(f, x))))),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)),
                          C0),
                      NeQ(Subtract(Sqr(c), Sqr(d)), C0)))),
          IIntegrate(3008,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(Plus(A_DEFAULT,
                          Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          p_),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(Dist(
                  Times(Sqrt(Plus(a, Times(b, Sin(Plus(e, Times(f, x)))))),
                      Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x)))))),
                      Power(Times(f, Cos(Plus(e, Times(f, x)))), CN1)),
                  Subst(
                      Integrate(Times(Power(Plus(a, Times(b, x)), Subtract(m, C1D2)),
                          Power(Plus(c, Times(d, x)), Subtract(n, C1D2)),
                          Power(Plus(ASymbol, Times(BSymbol, x)), p)), x),
                      x, Sin(Plus(e, Times(f, x)))),
                  x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m, n,
                      p), x), EqQ(Plus(Times(b, c), Times(a, d)),
                          C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3009,
              Integrate(
                  Times(
                      Power(
                          Plus(A_DEFAULT,
                              Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  B_DEFAULT)),
                          p_),
                      Power(
                          Plus(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(Dist(
                      Times(Sqrt(Plus(a, Times(b, Cos(Plus(e, Times(f, x)))))),
                          Sqrt(Plus(c, Times(d, Cos(Plus(e, Times(f, x)))))),
                          Power(Times(f, Sin(Plus(e, Times(f, x)))), CN1)),
                      Subst(Integrate(
                          Times(Power(Plus(a, Times(b, x)), Subtract(m, C1D2)),
                              Power(Plus(c, Times(d, x)), Subtract(n, C1D2)),
                              Power(Plus(ASymbol, Times(BSymbol, x)), p)),
                          x), x, Cos(Plus(e, Times(f, x)))),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m, n, p), x), EqQ(Plus(
                      Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)),
                          C0)))),
          IIntegrate(3010,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Plus(Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          Times(C_DEFAULT, Sqr($($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Dist(Power(b, CN1),
                      Integrate(
                          Times(
                              Power(Times(b, Sin(Plus(e, Times(f, x)))), Plus(m, C1)), Plus(BSymbol,
                                  Times(CSymbol, Sin(Plus(e, Times(f, x)))))),
                          x),
                      x),
                  FreeQ(List(b, e, f, BSymbol, CSymbol, m), x))),
          IIntegrate(3011,
              Integrate(
                  Times(
                      Power(Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Plus(A_,
                          Times(C_DEFAULT, Sqr($($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(ASymbol, Cos(Plus(e, Times(f, x))),
                          Power(Times(b, Sin(Plus(e, Times(f, x)))), Plus(m,
                              C1)),
                          Power(Times(b, f, Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List(b, e, f, ASymbol, CSymbol,
                      m), x), EqQ(Plus(Times(ASymbol, Plus(m, C2)), Times(CSymbol, Plus(m, C1))),
                          C0)))),
          IIntegrate(3012,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_),
                      Plus(A_,
                          Times(
                              C_DEFAULT, Sqr($($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(ASymbol, Cos(Plus(e, Times(f, x))),
                              Power(Times(b, Sin(Plus(e, Times(f, x)))),
                                  Plus(m, C1)),
                              Power(Times(b, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Plus(Times(ASymbol, Plus(m, C2)), Times(CSymbol,
                                  Plus(m, C1))),
                              Power(Times(Sqr(b), Plus(m, C1)), CN1)),
                          Integrate(Power(Times(b, Sin(Plus(e, Times(f, x)))), Plus(m, C2)),
                              x),
                          x)),
                  And(FreeQ(List(b, e, f, ASymbol, CSymbol), x), LtQ(m, CN1)))),
          IIntegrate(3013,
              Integrate(
                  Times(Power($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_DEFAULT),
                      Plus(A_, Times(C_DEFAULT, Sqr($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Negate(Dist(Power(f, CN1),
                      Subst(Integrate(
                          Times(Power(Subtract(C1, Sqr(x)), Times(C1D2, Subtract(m, C1))),
                              Subtract(Plus(ASymbol, CSymbol), Times(CSymbol, Sqr(x)))),
                          x), x, Cos(Plus(e, Times(f, x)))),
                      x)),
                  And(FreeQ(List(e, f, ASymbol, CSymbol), x), IGtQ(Times(C1D2, Plus(m, C1)), C0)))),
          IIntegrate(3014,
              Integrate(
                  Times(
                      Power(Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Plus(A_,
                          Times(C_DEFAULT, Sqr($($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(Plus(
                  Negate(
                      Simp(
                          Times(CSymbol, Cos(Plus(e, Times(f, x))),
                              Power(Times(b, Sin(Plus(e, Times(f, x)))), Plus(m, C1)),
                              Power(Times(b, f, Plus(m, C2)), CN1)),
                          x)),
                  Dist(
                      Times(
                          Plus(Times(ASymbol, Plus(m, C2)), Times(CSymbol,
                              Plus(m, C1))),
                          Power(Plus(m, C2), CN1)),
                      Integrate(Power(Times(b, Sin(Plus(e, Times(f, x)))), m), x), x)),
                  And(FreeQ(List(b, e, f, ASymbol, CSymbol, m), x), Not(LtQ(m, CN1))))),
          IIntegrate(3015,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(A_DEFAULT,
                          Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          Times(C_DEFAULT, Sqr($($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Dist(Power(b, CN2),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Simp(
                                  Plus(Times(b, BSymbol), Times(CN1, a, CSymbol),
                                      Times(b, CSymbol, Sin(Plus(e, Times(f, x))))),
                                  x)),
                          x),
                      x),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol,
                      m), x), EqQ(
                          Plus(
                              Times(ASymbol, Sqr(
                                  b)),
                              Times(CN1, a, b, BSymbol), Times(Sqr(a), CSymbol)),
                          C0)))),
          IIntegrate(3016,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(A_DEFAULT,
                          Times(C_DEFAULT, Sqr($($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Dist(Times(CSymbol, Power(b, CN2)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Simp(Plus(Negate(a), Times(b, Sin(Plus(e, Times(f, x))))), x)),
                          x),
                      x),
                  And(FreeQ(List(a, b, e, f, ASymbol, CSymbol,
                      m), x), EqQ(Plus(Times(ASymbol, Sqr(b)), Times(Sqr(a), CSymbol)),
                          C0)))),
          IIntegrate(3017,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(A_DEFAULT,
                          Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          Times(
                              C_DEFAULT, Sqr($($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Subtract(ASymbol, CSymbol),
                          Integrate(Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Plus(C1, Sin(Plus(e, Times(f, x))))), x),
                          x),
                      Dist(CSymbol,
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                      m),
                                  Sqr(Plus(C1, Sin(Plus(e, Times(f, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol, m), x),
                      EqQ(Plus(ASymbol, Negate(BSymbol), CSymbol),
                          C0),
                      Not(IntegerQ(Times(C2, m)))))),
          IIntegrate(3018,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(A_DEFAULT,
                          Times(
                              C_DEFAULT, Sqr($($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Subtract(ASymbol, CSymbol),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m), Plus(C1,
                                      Sin(Plus(e, Times(f, x))))),
                              x),
                          x),
                      Dist(CSymbol, Integrate(Times(Power(Plus(a,
                          Times(b, Sin(Plus(e, Times(f, x))))),
                          m), Sqr(Plus(C1, Sin(Plus(e, Times(f, x)))))), x), x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, CSymbol,
                      m), x), EqQ(Plus(ASymbol, CSymbol),
                          C0),
                      Not(IntegerQ(Times(C2, m)))))),
          IIntegrate(3019,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(A_DEFAULT,
                          Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          Times(
                              C_DEFAULT, Sqr($($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Plus(Times(ASymbol, b), Times(CN1, a, BSymbol), Times(b, CSymbol)),
                              Cos(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))),
                                  m),
                              Power(Times(a, f, Plus(Times(C2, m), C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(Sqr(a),
                              Plus(Times(C2, m), C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Simp(Plus(Times(a, ASymbol, Plus(m, C1)),
                                      Times(m, Subtract(Times(b, BSymbol), Times(a, CSymbol))),
                                      Times(b, CSymbol, Plus(Times(C2, m), C1),
                                          Sin(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol),
                      x), LtQ(m, CN1), EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3020,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(A_DEFAULT,
                          Times(
                              C_DEFAULT, Sqr($($s("§sin"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(b, Plus(ASymbol, CSymbol), Cos(Plus(e, Times(f, x))),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                          Power(Times(a, f, Plus(Times(C2, m), C1)), CN1)), x),
                      Dist(Power(Times(Sqr(a), Plus(Times(C2, m), C1)), CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Simp(Plus(Times(a, ASymbol, Plus(m, C1)), Times(CN1, a, CSymbol, m),
                                  Times(b, CSymbol, Plus(Times(C2, m), C1),
                                      Sin(Plus(e, Times(f, x))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, CSymbol), x), LtQ(m, CN1),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))));
}
