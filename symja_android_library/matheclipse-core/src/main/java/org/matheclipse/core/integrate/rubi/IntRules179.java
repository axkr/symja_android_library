package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
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
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LeQ;
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
class IntRules179 {
  public static IAST RULES =
      List(
          IIntegrate(3581,
              Integrate(
                  Times(
                      Power(Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT), p_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(g, Plus(m, n)),
                      Integrate(
                          Times(
                              Power(Times(g, Cot(Plus(e, Times(f, x)))),
                                  Subtract(Subtract(p, m), n)),
                              Power(Plus(b, Times(a,
                                  Cot(Plus(e, Times(f, x))))), m),
                              Power(Plus(d, Times(c, Cot(Plus(e, Times(f, x))))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g,
                      p), x), Not(
                          IntegerQ(p)),
                      IntegerQ(m), IntegerQ(n)))),
          IIntegrate(3582,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT)),
                          m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT),
                      Power(Times(g_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(g, Plus(m,
                          n)),
                      Integrate(
                          Times(
                              Power(Times(g, Tan(Plus(e, Times(f, x)))),
                                  Subtract(Subtract(p, m), n)),
                              Power(Plus(b, Times(a, Tan(Plus(e, Times(f, x))))), m),
                              Power(Plus(d, Times(c, Tan(Plus(e, Times(f, x))))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, p), x), Not(IntegerQ(p)), IntegerQ(m),
                      IntegerQ(n)))),
          IIntegrate(3583,
              Integrate(
                  Times(
                      Power(
                          Times(
                              g_DEFAULT, Power($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  q_)),
                          p_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(g, Power(Tan(Plus(e, Times(f, x))), q)), p), Power(Power(
                              Times(g, Tan(Plus(e, Times(f, x)))), Times(p, q)), CN1)),
                      Integrate(
                          Times(Power(Times(g, Tan(Plus(e, Times(f, x)))), Times(p, q)),
                              Power(Plus(a, Times(b,
                                  Tan(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p, q), x), Not(IntegerQ(p)),
                      Not(And(IntegerQ(m), IntegerQ(n)))))),
          IIntegrate(3584,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_DEFAULT),
                      Power(Times(g_DEFAULT,
                          $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), p_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(g, n),
                      Integrate(
                          Times(Power(Times(g, Tan(Plus(e, Times(f, x)))), Subtract(p, n)),
                              Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m), Power(Plus(d,
                                  Times(c, Tan(Plus(e, Times(f, x))))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, p), x), IntegerQ(n)))),
          IIntegrate(3585,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_),
                      Power($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), p_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(b, Times(a,
                              Cot(Plus(e, Times(f, x))))), m),
                          Power(Plus(c,
                              Times(d, Cot(Plus(e, Times(f, x))))), n),
                          Power(Power(Cot(Plus(e, Times(f, x))), Plus(m, p)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f,
                      n), x), Not(
                          IntegerQ(n)),
                      IntegerQ(m), IntegerQ(p)))),
          IIntegrate(3586,
              Integrate(
                  Times(Power(
                      Plus(Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          c_),
                      n_),
                      Power(
                          Times(g_DEFAULT,
                              $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          p_),
                      Power(
                          Plus(
                              a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Cot(
                              Plus(e, Times(f, x))), p),
                          Power(Times(g, Tan(Plus(e, Times(f, x)))), p)),
                      Integrate(
                          Times(Power(Plus(b, Times(a, Cot(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Cot(Plus(e, Times(f, x))))), n), Power(
                                  Power(Cot(Plus(e, Times(f, x))), Plus(m, p)), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n,
                      p), x), Not(
                          IntegerQ(n)),
                      IntegerQ(m), Not(IntegerQ(p))))),
          IIntegrate(3587,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  d_DEFAULT),
                              c_),
                          n_),
                      Power(
                          Times(g_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          p_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(g, Tan(Plus(e, Times(f, x)))), n),
                          Power(Plus(c, Times(d,
                              Cot(Plus(e, Times(f, x))))), n),
                          Power(Power(Plus(d, Times(c, Tan(Plus(e, Times(f, x))))), n), CN1)),
                      Integrate(
                          Times(Power(Times(g, Tan(Plus(e, Times(f, x)))), Subtract(p, n)),
                              Power(Plus(a, Times(b,
                                  Tan(Plus(e, Times(f, x))))), m),
                              Power(Plus(d, Times(c, Tan(Plus(e, Times(f, x))))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x), Not(IntegerQ(n)),
                      Not(IntegerQ(m))))),
          IIntegrate(3588,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(A_DEFAULT,
                          Times(B_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(a, c, Power(f, CN1)),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), Subtract(m, C1)),
                                  Power(Plus(c, Times(d, x)), Subtract(n, C1)), Plus(ASymbol,
                                      Times(BSymbol, x))),
                              x),
                          x, Tan(Plus(e, Times(f, x)))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m, n), x), EqQ(Plus(Times(b,
                      c), Times(a, d)), C0), EqQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3589,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(BSymbol, d, Power(b,
                              CN1)),
                          Integrate(Tan(Plus(e, Times(f, x))), x), x),
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(
                                  Simp(
                                      Plus(Times(ASymbol, b, c),
                                          Times(
                                              Plus(Times(ASymbol, b, d),
                                                  Times(BSymbol,
                                                      Subtract(Times(b, c), Times(a, d)))),
                                              Tan(Plus(e, Times(f, x))))),
                                      x),
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol,
                      BSymbol), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0)))),
          IIntegrate(3590,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                              Plus(Times(a, c), Times(b, d)),
                              Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))),
                                  m),
                              Power(Times(C2, Sqr(a), f, m), CN1)), x)),
                      Dist(Power(Times(C2, a, b), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Simp(
                                      Plus(Times(ASymbol, b, c), Times(a, BSymbol, c),
                                          Times(a, ASymbol, d), Times(b, BSymbol, d),
                                          Times(C2, a, BSymbol, d, Tan(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a,
                          d)), C0),
                      LtQ(m, CN1), EqQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3591,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Subtract(Times(b, c), Times(a, d)),
                          Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                          Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(m, C1)),
                          Power(Times(b, f, Plus(m, C1), Plus(Sqr(a), Sqr(b))), CN1)), x),
                      Dist(
                          Power(Plus(Sqr(a),
                              Sqr(b)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                                  Simp(
                                      Subtract(
                                          Subtract(Plus(
                                              Times(a, ASymbol, c), Times(b, BSymbol, c),
                                              Times(ASymbol, b, d)), Times(a, BSymbol, d)),
                                          Times(
                                              Subtract(
                                                  Subtract(
                                                      Subtract(Times(ASymbol, b, c),
                                                          Times(a, BSymbol, c)),
                                                      Times(a, ASymbol, d)),
                                                  Times(b, BSymbol, d)),
                                              Tan(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c),
                          Times(a, d)), C0),
                      LtQ(m, CN1), NeQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3592,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_DEFAULT),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(BSymbol, d,
                          Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))),
                              Plus(m, C1)),
                          Power(Times(b, f, Plus(m, C1)), CN1)), x),
                      Integrate(
                          Times(
                              Power(Plus(a,
                                  Times(b, Tan(Plus(e, Times(f, x))))), m),
                              Simp(Plus(Times(ASymbol, c), Times(CN1, BSymbol, d),
                                  Times(Plus(Times(BSymbol, c), Times(ASymbol, d)),
                                      Tan(Plus(e, Times(f, x))))),
                                  x)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol,
                      m), x), NeQ(Subtract(Times(b, c), Times(a, d)),
                          C0),
                      Not(LeQ(m, CN1))))),
          IIntegrate(3593,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(
                              Times(Sqr(a), Subtract(Times(BSymbol, c), Times(ASymbol, d)),
                                  Power(
                                      Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Subtract(m,
                                          C1)),
                                  Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Plus(n, C1)),
                                  Power(Times(d, f, Plus(Times(b, c), Times(a, d)), Plus(n, C1)),
                                      CN1)),
                              x)),
                      Dist(
                          Times(a, Power(Times(d, Plus(Times(b, c), Times(a,
                              d)), Plus(n,
                                  C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Subtract(m,
                                          C1)),
                                  Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Plus(n, C1)),
                                  Simp(Plus(Times(ASymbol, b, d, Subtract(Subtract(m, n), C2)),
                                      Times(CN1, BSymbol,
                                          Plus(Times(b, c, Subtract(m, C1)),
                                              Times(a, d, Plus(n, C1)))),
                                      Times(
                                          Subtract(Times(a, ASymbol, d, Plus(m, n)),
                                              Times(BSymbol,
                                                  Plus(Times(a, c, Subtract(m, C1)),
                                                      Times(b, d, Plus(n, C1))))),
                                          Tan(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(Sqr(a), Sqr(b)), C0),
                      GtQ(m, C1), LtQ(n, CN1)))),
          IIntegrate(3594,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, BSymbol,
                              Power(Plus(a, Times(b,
                                  Tan(Plus(e, Times(f, x))))), Subtract(m,
                                      C1)),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Plus(n,
                                  C1)),
                              Power(Times(d, f, Plus(m, n)), CN1)),
                          x),
                      Dist(Power(Times(d, Plus(m, n)), CN1), Integrate(Times(
                          Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Subtract(m, C1)),
                          Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), n),
                          Simp(Subtract(
                              Plus(Times(a, ASymbol, d, Plus(m, n)),
                                  Times(BSymbol,
                                      Subtract(Times(a, c, Subtract(m, C1)),
                                          Times(b, d, Plus(n, C1))))),
                              Times(Subtract(
                                  Times(BSymbol, Subtract(Times(b, c), Times(a, d)),
                                      Subtract(m, C1)),
                                  Times(d, Plus(Times(ASymbol, b), Times(a, BSymbol)), Plus(m, n))),
                                  Tan(Plus(e, Times(f, x))))),
                              x)),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(Sqr(a), Sqr(b)),
                          C0),
                      GtQ(m, C1), Not(LtQ(n, CN1))))),
          IIntegrate(3595,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m),
                                  Power(Plus(c,
                                      Times(d, Tan(Plus(e, Times(f, x))))), n),
                                  Power(Times(C2, a, f, m), CN1)),
                              x)),
                      Dist(Power(Times(C2, Sqr(a), m), CN1), Integrate(Times(
                          Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(m, C1)),
                          Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Subtract(n, C1)),
                          Simp(Subtract(
                              Subtract(Times(ASymbol, Plus(Times(a, c, m), Times(b, d, n))),
                                  Times(BSymbol, Plus(Times(b, c, m), Times(a, d, n)))),
                              Times(d,
                                  Subtract(Times(b, BSymbol, Subtract(m, n)),
                                      Times(a, ASymbol, Plus(m, n))),
                                  Tan(Plus(e, Times(f, x))))),
                              x)),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(b, c), Times(a,
                          d)), C0),
                      EqQ(Plus(Sqr(a), Sqr(b)), C0), LtQ(m, C0), GtQ(n, C0)))),
          IIntegrate(3596,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Plus(Times(a, ASymbol), Times(b, BSymbol)),
                              Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d,
                                  Tan(Plus(e, Times(f, x))))), Plus(n,
                                      C1)),
                              Power(Times(C2, f, m, Subtract(Times(b, c), Times(a, d))), CN1)),
                          x),
                      Dist(
                          Power(Times(C2, a, m,
                              Subtract(Times(b, c), Times(a, d))), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), n),
                                  Simp(Plus(
                                      Times(ASymbol,
                                          Subtract(Times(b, c, m),
                                              Times(a, d, Plus(Times(C2, m), n, C1)))),
                                      Times(BSymbol,
                                          Subtract(Times(a, c, m), Times(b, d, Plus(n, C1)))),
                                      Times(d, Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                          Plus(m, n, C1), Tan(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, n), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(Sqr(a), Sqr(b)), C0),
                      LtQ(m, C0), Not(GtQ(n, C0))))),
          IIntegrate(3597,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(A_DEFAULT, Times(B_DEFAULT,
                          $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(BSymbol, Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m),
                          Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))),
                              n),
                          Power(Times(f, Plus(m, n)), CN1)), x),
                      Dist(
                          Power(Times(a,
                              Plus(m, n)), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m),
                                  Power(
                                      Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Subtract(n,
                                          C1)),
                                  Simp(Plus(Times(a, ASymbol, c, Plus(m, n)),
                                      Times(CN1, BSymbol, Plus(Times(b, c, m), Times(a, d, n))),
                                      Times(
                                          Subtract(Times(a, ASymbol, d, Plus(m, n)),
                                              Times(BSymbol,
                                                  Subtract(Times(b, d, m), Times(a, c, n)))),
                                          Tan(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(Sqr(a), Sqr(b)),
                          C0),
                      GtQ(n, C0)))),
          IIntegrate(3598,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(A_DEFAULT,
                          Times(B_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Subtract(Times(ASymbol, d), Times(BSymbol, c)),
                              Power(Plus(a, Times(b, Tan(
                                  Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Plus(n,
                                  C1)),
                              Power(Times(f, Plus(n, C1), Plus(Sqr(c), Sqr(d))), CN1)),
                          x),
                      Dist(
                          Power(Times(a, Plus(n, C1),
                              Plus(Sqr(c), Sqr(d))), CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m),
                                  Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), Plus(n, C1)),
                                  Simp(
                                      Subtract(
                                          Subtract(
                                              Times(ASymbol,
                                                  Subtract(Times(b, d, m),
                                                      Times(a, c, Plus(n, C1)))),
                                              Times(BSymbol,
                                                  Plus(Times(b, c, m), Times(a, d, Plus(n, C1))))),
                                          Times(a, Subtract(Times(BSymbol, c), Times(ASymbol, d)),
                                              Plus(m, n, C1), Tan(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(Sqr(a), Sqr(b)),
                          C0),
                      LtQ(n, CN1)))),
          IIntegrate(3599,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(A_DEFAULT,
                          Times(B_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(Times(b, BSymbol, Power(f, CN1)),
                      Subst(Integrate(
                          Times(Power(Plus(a, Times(b, x)), Subtract(m, C1)),
                              Power(Plus(c, Times(d, x)), n)),
                          x), x, Tan(Plus(e, Times(f, x)))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m, n), x),
                      NeQ(Subtract(Times(b, c), Times(a,
                          d)), C0),
                      EqQ(Plus(Sqr(a),
                          Sqr(b)), C0),
                      EqQ(Plus(Times(ASymbol, b), Times(a, BSymbol)), C0)))),
          IIntegrate(3600,
              Integrate(
                  Times(
                      Power(
                          Plus(a_, Times(
                              b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          m_),
                      Plus(
                          A_DEFAULT, Times(B_DEFAULT,
                              $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Plus(c_DEFAULT,
                              Times(d_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(
                              Plus(Times(ASymbol, b), Times(a,
                                  BSymbol)),
                              Power(Plus(Times(b, c), Times(a, d)), CN1)),
                          Integrate(Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m), x), x),
                      Dist(
                          Times(Subtract(Times(BSymbol, c), Times(ASymbol, d)),
                              Power(Plus(Times(b, c), Times(a, d)), CN1)),
                          Integrate(Times(Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), m),
                              Subtract(a, Times(b, Tan(Plus(e, Times(f, x))))),
                              Power(Plus(c, Times(d, Tan(Plus(e, Times(f, x))))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m), x),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Plus(Sqr(a), Sqr(b)), C0),
                      NeQ(Plus(Times(ASymbol, b), Times(a, BSymbol)), C0)))));
}
