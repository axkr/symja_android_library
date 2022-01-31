package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CPiHalf;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.CSymbol;
import static org.matheclipse.core.expression.S.Pi;
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
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifyInertTrigFunction;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions35 {
  public static IAST RULES = List(
      ISetDelayed(631,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power(Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  c_DEFAULT), n_))),
                      p_DEFAULT),
                  Power(Times($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(CN1, d, $($s("§cos"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(
                      Plus(a,
                          Times(b,
                              Power(Times(CN1, c, $($s("§tan"), Plus(e, CPiHalf, Times(f, x)))),
                                  n))),
                      p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(632,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power(Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  c_DEFAULT), n_))),
                      p_DEFAULT),
                  Power(
                      Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(CN1, d, $($s("§tan"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(
                      Plus(a,
                          Times(b,
                              Power(Times(CN1, c, $($s("§tan"), Plus(e, CPiHalf, Times(f, x)))),
                                  n))),
                      p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(633,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power(Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  c_DEFAULT), n_))),
                      p_DEFAULT),
                  Power(Times($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(CN1, d, $($s("§cot"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(
                      Plus(a,
                          Times(b,
                              Power(Times(CN1, c, $($s("§tan"), Plus(e, CPiHalf, Times(f, x)))),
                                  n))),
                      p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(634,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power(Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  c_DEFAULT), n_))),
                      p_DEFAULT),
                  Power(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(CN1, d, $($s("§sec"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(
                      Plus(a,
                          Times(b,
                              Power(Times(CN1, c, $($s("§tan"), Plus(e, CPiHalf, Times(f, x)))),
                                  n))),
                      p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(635,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power(Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  c_DEFAULT), n_))),
                      p_DEFAULT),
                  Power(Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(Times(Power(Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x)))), m),
              Power(
                  Plus(a,
                      Times(b,
                          Power(Times(CN1, c, $($s("§tan"), Plus(e, CPiHalf, Times(f, x)))), n))),
                  p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(636,
          UnifyInertTrigFunction(
              Power(
                  Plus(
                      a_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                  n_DEFAULT),
              x_),
          Condition(
              Power(Plus(a, Times(b, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))),
                  n),
              FreeQ(List(a, b, e, f, n), x))),
      ISetDelayed(637,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(g, $($s("§csc"),
                      Plus(e, CPiHalf, Times(f, x)))), p),
                  Power(Plus(a, Times(b, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m)),
              FreeQ(List(a, b, e, f, g, m, p), x))),
      ISetDelayed(638,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Times($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(g,
                      $($s("§cos"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x)))), p),
                  Power(
                      Subtract(a, Times(b, $($s("§csc"),
                          Plus(e, Times(CN1, C1D2, Pi), Times(f, x))))),
                      m)),
              FreeQ(List(a, b, e, f, g, m, p), x))),
      ISetDelayed(639,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      m_DEFAULT),
                  Power(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(g,
                      $($s("§sec"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x)))), p),
                  Power(
                      Subtract(a, Times(b,
                          $($s("§csc"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x))))),
                      m)),
              FreeQ(List(a, b, e, f, g, m, p), x))),
      ISetDelayed(640,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Times($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(CN1, g, $($s("§cot"), Plus(e, CPiHalf, Times(f, x)))), p), Power(Plus(
                      a, Times(b, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m)),
              FreeQ(List(a, b, e, f, g, m, p), x))),
      ISetDelayed(641,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT)),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Plus(a, Times(b,
                      $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c, Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), n)),
              FreeQ(List(a, b, c, d, e, f, m, n), x))),
      ISetDelayed(642,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Power(Plus(c_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      d_DEFAULT)), n_DEFAULT),
                  Power(
                      Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(g, $($s("§csc"), Plus(e, CPiHalf, Times(f, x)))), p),
                  Power(Plus(a, Times(b,
                      $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c, Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), n)),
              FreeQ(List(a, b, c, d, e, f, g, m, n, p), x))),
      ISetDelayed(643,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT)),
                      n_DEFAULT),
                  Power(
                      Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(g, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))), p),
                  Power(Plus(a, Times(b,
                      $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c, Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), n)),
              FreeQ(List(a, b, c, d, e, f, g, m, n, p), x))),
      ISetDelayed(644,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      m_DEFAULT),
                  Plus(A_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      B_DEFAULT)),
                  Power(
                      Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Plus(a, Times(b,
                      $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x)))), n), Plus(ASymbol,
                      Times(BSymbol, $($s("§csc"), Plus(e, CPiHalf, Times(f, x)))))),
              FreeQ(List(a, b, d, e, f, ASymbol, BSymbol, m, n), x))),
      ISetDelayed(645,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Plus(
                          A_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              B_DEFAULT)),
                      p_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT)),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Plus(a, Times(b,
                      $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c,
                      Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), n),
                  Power(
                      Plus(ASymbol,
                          Times(BSymbol, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))),
                      p)),
              FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m, n, p), x))),
      ISetDelayed(646,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Plus(A_DEFAULT,
                      Times($($s("§sec"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                      Times(Sqr($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), C_DEFAULT))),
              x_),
          Condition(
              Times(
                  Power(Plus(a,
                      Times(b, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Plus(
                      ASymbol, Times(BSymbol, $($s("§csc"),
                          Plus(e, CPiHalf, Times(f, x)))),
                      Times(CSymbol, Sqr($($s("§csc"), Plus(e, CPiHalf, Times(f, x))))))),
              FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol, m), x))),
      ISetDelayed(647,
          UnifyInertTrigFunction(Times(
              Power(
                  Plus(a_DEFAULT,
                      Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT)),
                  m_DEFAULT),
              Plus(
                  A_DEFAULT, Times(Sqr($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      C_DEFAULT))),
              x_),
          Condition(
              Times(
                  Power(Plus(a,
                      Times(b, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Plus(ASymbol, Times(CSymbol, Sqr($($s("§csc"), Plus(e, CPiHalf, Times(f, x))))))),
              FreeQ(List(a, b, e, f, ASymbol, CSymbol, m), x))),
      ISetDelayed(648,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Plus(A_DEFAULT,
                      Times($($s("§sec"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                      Times(Sqr($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), C_DEFAULT)),
                  Power(
                      Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Plus(a, Times(b,
                      $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Times(d,
                      $($s("§csc"), Plus(e, CPiHalf, Times(f, x)))), n),
                  Plus(
                      ASymbol, Times(BSymbol, $($s("§csc"),
                          Plus(e, CPiHalf, Times(f, x)))),
                      Times(CSymbol, Sqr($($s("§csc"), Plus(e, CPiHalf, Times(f, x))))))),
              FreeQ(List(a, b, d, e, f, ASymbol, BSymbol, CSymbol, m, n), x))),
      ISetDelayed(649,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      m_DEFAULT),
                  Plus(A_DEFAULT,
                      Times(Sqr($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), C_DEFAULT)),
                  Power(Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(Power(Plus(a, Times(b, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x)))), n),
                  Plus(ASymbol, Times(CSymbol, Sqr($($s("§csc"), Plus(e, CPiHalf, Times(f, x))))))),
              FreeQ(List(a, b, d, e, f, ASymbol, CSymbol, m, n), x))),
      ISetDelayed(650, UnifyInertTrigFunction(u_, x_), u));
}
