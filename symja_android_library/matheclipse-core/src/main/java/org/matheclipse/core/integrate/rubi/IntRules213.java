package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.CSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ActivateTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.KnownSecantIntegrandQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules213 {
  public static IAST RULES =
      List(
          IIntegrate(4261,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              d_DEFAULT),
                          n_DEFAULT),
                      u_,
                      Power(
                          Times(c_DEFAULT, $($s("§tan"), Plus(a_DEFAULT,
                              Times(b_DEFAULT, x_)))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c, Tan(Plus(a, Times(b, x)))), m),
                          Power(Times(d,
                              Csc(Plus(a, Times(b, x)))), m),
                          Power(Power(Times(d, Sec(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(
                          Times(
                              ActivateTrig(u), Power(Times(d,
                                  Sec(Plus(a, Times(b, x)))), m),
                              Power(Power(Times(d, Csc(Plus(a, Times(b, x)))), Subtract(m, n)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m,
                      n), x), KnownSecantIntegrandQ(u,
                          x),
                      Not(IntegerQ(m))))),
          IIntegrate(4262,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              c_DEFAULT),
                          m_DEFAULT),
                      u_,
                      Power(
                          Times(d_DEFAULT, $($s("§sec"), Plus(a_DEFAULT,
                              Times(b_DEFAULT, x_)))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c, Cot(Plus(a, Times(b, x)))), m),
                          Power(Times(d,
                              Sec(Plus(a, Times(b, x)))), m),
                          Power(Power(Times(d, Csc(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(
                          Times(
                              ActivateTrig(u), Power(Times(d,
                                  Csc(Plus(a, Times(b, x)))), m),
                              Power(Power(Times(d, Sec(Plus(a, Times(b, x)))), Subtract(m, n)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n), x), KnownSecantIntegrandQ(u, x),
                      Not(IntegerQ(m))))),
          IIntegrate(4263,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              c_DEFAULT),
                          m_DEFAULT),
                      Power(
                          Times($($s("§csc"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              d_DEFAULT),
                          n_DEFAULT),
                      u_),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c, Cot(Plus(a, Times(b, x)))), m),
                          Power(Times(d, Sec(Plus(a, Times(b, x)))), m),
                          Power(Power(Times(d, Csc(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(Times(ActivateTrig(u),
                          Power(Times(d, Csc(Plus(a, Times(b, x)))), Plus(m, n)), Power(
                              Power(Times(d, Sec(Plus(a, Times(b, x)))), m), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n), x), KnownSecantIntegrandQ(u, x),
                      Not(IntegerQ(m))))),
          IIntegrate(4264,
              Integrate(
                  Times(u_,
                      Power(Times(c_DEFAULT, $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(c,
                              Csc(Plus(a, Times(b, x)))), m),
                          Power(Times(c, Sin(Plus(a, Times(b, x)))), m)),
                      Integrate(
                          Times(ActivateTrig(u),
                              Power(Power(Times(c, Csc(Plus(a, Times(b, x)))), m), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, m), x), Not(IntegerQ(m)), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4265,
              Integrate(
                  Times(
                      Power(Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))), c_DEFAULT),
                          m_DEFAULT),
                      u_),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(c,
                              Cos(Plus(a, Times(b, x)))), m),
                          Power(Times(c, Sec(Plus(a, Times(b, x)))), m)),
                      Integrate(
                          Times(
                              ActivateTrig(u), Power(Power(Times(c, Sec(Plus(a, Times(b, x)))), m),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, m), x), Not(IntegerQ(m)), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4266,
              Integrate(
                  Times(u_,
                      Power(
                          Times(c_DEFAULT, $($s("§tan"), Plus(a_DEFAULT,
                              Times(b_DEFAULT, x_)))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c, Tan(Plus(a, Times(b, x)))), m),
                          Power(Times(c,
                              Csc(Plus(a, Times(b, x)))), m),
                          Power(Power(Times(c, Sec(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(
                          Times(
                              ActivateTrig(u), Power(Times(c, Sec(Plus(a, Times(b, x)))), m), Power(
                                  Power(Times(c, Csc(Plus(a, Times(b, x)))), m), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, m), x), Not(IntegerQ(m)), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4267,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              c_DEFAULT),
                          m_DEFAULT),
                      u_),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(c, Cot(Plus(a, Times(b, x)))), m),
                          Power(Times(c, Sec(Plus(a, Times(b, x)))), m), Power(
                              Power(Times(c, Csc(Plus(a, Times(b, x)))), m), CN1)),
                      Integrate(Times(ActivateTrig(u),
                          Power(Times(c, Csc(Plus(a, Times(b, x)))), m), Power(
                              Power(Times(c, Sec(Plus(a, Times(b, x)))), m), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, m), x), Not(IntegerQ(m)), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4268,
              Integrate(
                  Times(
                      Plus(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              B_DEFAULT),
                          A_),
                      u_,
                      Power(Times(c_DEFAULT, $($s("§sec"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(c,
                      Integrate(
                          Times(ActivateTrig(u),
                              Power(Times(c, Sec(
                                  Plus(a, Times(b, x)))), Subtract(n,
                                      C1)),
                              Plus(BSymbol, Times(ASymbol, Sec(Plus(a, Times(b, x)))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol, n), x), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4269,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              c_DEFAULT),
                          n_DEFAULT),
                      u_,
                      Plus(
                          A_, Times(B_DEFAULT, $($s("§sin"),
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Dist(c,
                      Integrate(
                          Times(ActivateTrig(u),
                              Power(Times(c, Csc(Plus(a, Times(b, x)))), Subtract(n, C1)), Plus(
                                  BSymbol, Times(ASymbol, Csc(Plus(a, Times(b, x)))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol, n), x), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4270,
              Integrate(
                  Times(
                      Plus(Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))), B_DEFAULT),
                          A_),
                      u_),
                  x_Symbol),
              Condition(
                  Integrate(Times(ActivateTrig(u),
                      Plus(BSymbol, Times(ASymbol, Sec(Plus(a, Times(b, x))))), Power(
                          Sec(Plus(a, Times(b, x))), CN1)),
                      x),
                  And(FreeQ(List(a, b, ASymbol, BSymbol), x), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4271,
              Integrate(
                  Times(u_,
                      Plus(
                          A_, Times(B_DEFAULT, $($s("§sin"),
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(ActivateTrig(u),
                          Plus(BSymbol, Times(ASymbol,
                              Csc(Plus(a, Times(b, x))))),
                          Power(Csc(Plus(a, Times(b, x))), CN1)),
                      x),
                  And(FreeQ(List(a, b, ASymbol, BSymbol), x), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4272,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))), B_DEFAULT),
                          Times(Sqr($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                              C_DEFAULT)),
                      u_DEFAULT,
                      Power(Times(c_DEFAULT, $($s("§sec"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Sqr(c),
                      Integrate(
                          Times(ActivateTrig(u),
                              Power(Times(c,
                                  Sec(Plus(a, Times(b, x)))), Subtract(n, C2)),
                              Plus(
                                  CSymbol, Times(BSymbol, Sec(
                                      Plus(a, Times(b, x)))),
                                  Times(ASymbol, Sqr(Sec(Plus(a, Times(b, x))))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol, CSymbol, n), x),
                      KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4273,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              c_DEFAULT),
                          n_DEFAULT),
                      u_DEFAULT,
                      Plus(A_DEFAULT,
                          Times(B_DEFAULT, $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          Times(C_DEFAULT, Sqr($($s("§sin"),
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Dist(Sqr(c),
                      Integrate(
                          Times(ActivateTrig(u),
                              Power(Times(c,
                                  Csc(Plus(a, Times(b, x)))), Subtract(n, C2)),
                              Plus(
                                  CSymbol, Times(BSymbol, Csc(
                                      Plus(a, Times(b, x)))),
                                  Times(ASymbol, Sqr(Csc(Plus(a, Times(b, x))))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol, CSymbol,
                      n), x), KnownSecantIntegrandQ(u,
                          x)))),
          IIntegrate(4274,
              Integrate(
                  Times(u_DEFAULT,
                      Plus(
                          Times(Sqr($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                              C_DEFAULT),
                          A_),
                      Power(Times(c_DEFAULT, $($s("§sec"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Sqr(c),
                      Integrate(
                          Times(ActivateTrig(u),
                              Power(Times(c, Sec(Plus(a, Times(b, x)))), Subtract(n,
                                  C2)),
                              Plus(CSymbol, Times(ASymbol, Sqr(Sec(Plus(a, Times(b, x))))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, ASymbol, CSymbol, n), x), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4275,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))), c_DEFAULT),
                          n_DEFAULT),
                      u_DEFAULT,
                      Plus(A_,
                          Times(
                              C_DEFAULT, Sqr($($s("§sin"),
                                  Plus(a_DEFAULT, Times(b_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Dist(Sqr(c),
                      Integrate(
                          Times(ActivateTrig(u),
                              Power(Times(c, Csc(Plus(a, Times(b, x)))), Subtract(n,
                                  C2)),
                              Plus(CSymbol, Times(ASymbol, Sqr(Csc(Plus(a, Times(b, x))))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, ASymbol, CSymbol, n), x), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4276,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))), B_DEFAULT),
                          Times(Sqr($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                              C_DEFAULT)),
                      u_),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(ActivateTrig(u),
                          Plus(
                              CSymbol, Times(BSymbol, Sec(
                                  Plus(a, Times(b, x)))),
                              Times(ASymbol, Sqr(Sec(Plus(a, Times(b, x)))))),
                          Power(Sec(Plus(a, Times(b, x))), CN2)),
                      x),
                  And(FreeQ(List(a, b, ASymbol, BSymbol,
                      CSymbol), x), KnownSecantIntegrandQ(u,
                          x)))),
          IIntegrate(4277,
              Integrate(
                  Times(u_,
                      Plus(A_DEFAULT,
                          Times(B_DEFAULT, $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          Times(C_DEFAULT, Sqr($($s("§sin"),
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(ActivateTrig(u),
                          Plus(
                              CSymbol, Times(BSymbol, Csc(
                                  Plus(a, Times(b, x)))),
                              Times(ASymbol, Sqr(Csc(Plus(a, Times(b, x)))))),
                          Power(Csc(Plus(a, Times(b, x))), CN2)),
                      x),
                  And(FreeQ(List(a, b, ASymbol, BSymbol, CSymbol), x),
                      KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4278,
              Integrate(
                  Times(
                      Plus(
                          Times(Sqr($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                              C_DEFAULT),
                          A_),
                      u_),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(ActivateTrig(u),
                          Plus(CSymbol, Times(ASymbol,
                              Sqr(Sec(Plus(a, Times(b, x)))))),
                          Power(Sec(Plus(a, Times(b, x))), CN2)),
                      x),
                  And(FreeQ(List(a, b, ASymbol, CSymbol), x), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4279,
              Integrate(
                  Times(u_,
                      Plus(A_,
                          Times(C_DEFAULT, Sqr($($s("§sin"),
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(ActivateTrig(u),
                          Plus(CSymbol, Times(ASymbol, Sqr(Csc(Plus(a, Times(b, x)))))), Power(
                              Csc(Plus(a, Times(b, x))), CN2)),
                      x),
                  And(FreeQ(List(a, b, ASymbol, CSymbol), x), KnownSecantIntegrandQ(u, x)))),
          IIntegrate(4280,
              Integrate(
                  Times(u_,
                      Plus(
                          Times(A_DEFAULT,
                              Power($($s("§sec"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                                  n_DEFAULT)),
                          Times(B_DEFAULT,
                              Power($($s("§sec"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                                  $p("n1"))),
                          Times(C_DEFAULT,
                              Power($($s("§sec"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                                  $p("n2"))))),
                  x_Symbol),
              Condition(
                  Integrate(Times(ActivateTrig(u), Power(Sec(Plus(a, Times(b, x))), n),
                      Plus(ASymbol, Times(BSymbol, Sec(Plus(a, Times(b, x)))),
                          Times(CSymbol, Sqr(Sec(Plus(a, Times(b, x))))))),
                      x),
                  And(FreeQ(List(a, b, ASymbol, BSymbol, CSymbol, n), x),
                      EqQ($s("n1"), Plus(n, C1)), EqQ($s("n2"), Plus(n, C2))))));
}
