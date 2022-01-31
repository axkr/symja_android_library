package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Cancel;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.D;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Gamma;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.LogGamma;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FalseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfLog;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonsumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules127 {
  public static IAST RULES =
      List(
          IIntegrate(2541,
              Integrate(Times(Power(x_, CN1),
                  Power(
                      Plus(Times(Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), q_), b_DEFAULT),
                          Times(a_DEFAULT, Power(x_, m_DEFAULT))),
                      CN1),
                  Plus(
                      Times(Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), r_DEFAULT),
                          e_DEFAULT),
                      Times(d_DEFAULT, Power(x_, m_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          e, Log(
                              Plus(Times(a, Power(x, m)),
                                  Times(b, Power(Log(Times(c, Power(x, n))), q)))),
                          Power(Times(b, n, q), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, q, r), x), EqQ(r, Subtract(q, C1)),
                      EqQ(Subtract(Times(a, e, m), Times(b, d, n, q)), C0)))),
          IIntegrate(2542,
              Integrate(
                  Times(Power(x_, CN1),
                      Power(
                          Plus(
                              Times(Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  q_), b_DEFAULT),
                              Times(a_DEFAULT, Power(x_, m_DEFAULT))),
                          CN1),
                      Plus(
                          Times(Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                              r_DEFAULT), e_DEFAULT),
                          u_, Times(d_DEFAULT, Power(x_, m_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(e,
                              Log(Plus(Times(a, Power(x, m)),
                                  Times(b, Power(Log(Times(c, Power(x, n))), q)))),
                              Power(Times(b, n, q), CN1)),
                          x),
                      Integrate(
                          Times(u,
                              Power(
                                  Times(
                                      x,
                                      Plus(
                                          Times(a, Power(x, m)),
                                          Times(b, Power(Log(Times(c, Power(x, n))), q)))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n, q, r), x), EqQ(r, Subtract(q, C1)),
                      EqQ(Subtract(Times(a, e, m), Times(b, d, n, q)), C0)))),
          IIntegrate(2543,
              Integrate(
                  Times(Power(x_, CN1),
                      Power(
                          Plus(
                              Times(Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  q_), b_DEFAULT),
                              Times(a_DEFAULT, Power(x_, m_DEFAULT))),
                          CN1),
                      Plus(
                          Times(
                              Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  r_DEFAULT),
                              e_DEFAULT),
                          Times(d_DEFAULT, Power(x_, m_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              e, Log(
                                  Plus(
                                      Times(a, Power(x,
                                          m)),
                                      Times(b, Power(Log(Times(c, Power(x, n))), q)))),
                              Power(Times(b, n, q), CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(a, e, m), Times(b, d, n,
                                  q)),
                              Power(Times(b, n, q), CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, C1)),
                                  Power(
                                      Plus(Times(a, Power(x, m)),
                                          Times(b, Power(Log(Times(c, Power(x, n))), q))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n, q, r), x), EqQ(r, Subtract(q, C1)),
                      NeQ(Subtract(Times(a, e, m), Times(b, d, n, q)), C0)))),
          IIntegrate(2544,
              Integrate(
                  Times(Power(x_, CN1),
                      Power(
                          Plus(
                              Times(Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  q_), b_DEFAULT),
                              Times(a_DEFAULT, Power(x_, m_DEFAULT))),
                          p_DEFAULT),
                      Plus(
                          Times(
                              Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  r_DEFAULT),
                              e_DEFAULT),
                          Times(d_DEFAULT, Power(x_, m_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(e,
                          Power(
                              Plus(
                                  Times(a, Power(x, m)), Times(b,
                                      Power(Log(Times(c, Power(x, n))), q))),
                              Plus(p, C1)),
                          Power(Times(b, n, q, Plus(p, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p, q, r), x), EqQ(r, Subtract(q, C1)), NeQ(p,
                      CN1), EqQ(Subtract(Times(a, e, m), Times(b, d, n, q)), C0)))),
          IIntegrate(2545,
              Integrate(
                  Times(Power(x_, CN1),
                      Power(
                          Plus(
                              Times(Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  q_), b_DEFAULT),
                              Times(a_DEFAULT, Power(x_, m_DEFAULT))),
                          p_DEFAULT),
                      Plus(
                          Times(
                              Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                  r_DEFAULT),
                              e_DEFAULT),
                          Times(d_DEFAULT, Power(x_, m_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(e,
                              Power(
                                  Plus(
                                      Times(a, Power(x, m)), Times(b,
                                          Power(Log(Times(c, Power(x, n))), q))),
                                  Plus(p, C1)),
                              Power(Times(b, n, q, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Subtract(Times(a, e, m), Times(b, d, n,
                                  q)),
                              Power(Times(b, n, q), CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, C1)),
                                  Power(
                                      Plus(Times(a, Power(x, m)),
                                          Times(b, Power(Log(Times(c, Power(x, n))), q))),
                                      p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, n, p, q, r), x), EqQ(r, Subtract(q, C1)), NeQ(p,
                      CN1), NeQ(Subtract(Times(a, e, m), Times(b, d, n, q)), C0)))),
          IIntegrate(2546,
              Integrate(
                  Times(Power(x_, CN1),
                      Power(
                          Plus(
                              Times(Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), q_),
                                  b_DEFAULT),
                              Times(a_DEFAULT, Power(x_, m_DEFAULT))),
                          CN2),
                      Plus(
                          Times(Power(Log(Times(c_DEFAULT, Power(x_,
                              n_DEFAULT))), q_DEFAULT), f_DEFAULT),
                          Times(d_DEFAULT, Power(x_, m_DEFAULT)),
                          Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), e_DEFAULT,
                              Power(x_, m_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(d, Log(Times(c, Power(x, n))),
                          Power(Times(a, n,
                              Plus(Times(a, Power(x, m)),
                                  Times(b, Power(Log(Times(c, Power(x, n))), q)))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, q), x),
                      EqQ(Plus(Times(e, n),
                          Times(d, m)), C0),
                      EqQ(Plus(Times(a, f), Times(b, d, Subtract(q, C1))), C0)))),
          IIntegrate(2547,
              Integrate(
                  Times(
                      Plus(Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                          e_DEFAULT), d_),
                      Power(
                          Plus(
                              Times(
                                  Power(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                      q_),
                                  b_DEFAULT),
                              Times(a_DEFAULT, x_)),
                          CN2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(e, Log(Times(c, Power(x, n))),
                                  Power(
                                      Times(a,
                                          Plus(Times(a, x),
                                              Times(b, Power(Log(Times(c, Power(x, n))), q)))),
                                      CN1)),
                              x)),
                      Dist(
                          Times(Plus(d, Times(e, n)), Power(a,
                              CN1)),
                          Integrate(Power(Times(x,
                              Plus(Times(a, x), Times(b, Power(Log(Times(c, Power(x, n))), q)))),
                              CN1), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n, q), x), EqQ(Plus(d, Times(e, n, q)), C0)))),
          IIntegrate(
              2548, Integrate(Log(
                  u_), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(x, Log(
                          u)), x),
                      Integrate(SimplifyIntegrand(Times(x, D(u, x), Power(u, CN1)), x), x)),
                  InverseFunctionFreeQ(u, x))),
          IIntegrate(
              2549, Integrate(Log(
                  u_), x_Symbol),
              Condition(
                  Subtract(Simp(Times(x, Log(u)), x),
                      Integrate(
                          SimplifyIntegrand(Times(x, Simplify(Times(D(u, x), Power(u, CN1)))),
                              x),
                          x)),
                  ProductQ(u))),
          IIntegrate(2550,
              Integrate(Times(Log(u_),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1)), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Log(Plus(a, Times(b, x))), Log(u),
                          Power(b, CN1)), x),
                      Dist(Power(b, CN1),
                          Integrate(
                              SimplifyIntegrand(Times(Log(Plus(a, Times(b, x))), D(u, x),
                                  Power(u, CN1)), x),
                              x),
                          x)),
                  And(FreeQ(List(a, b), x), RationalFunctionQ(Times(D(u, x), Power(u, CN1)), x),
                      Or(NeQ(a, C0), Not(
                          And(BinomialQ(u, x), EqQ(Sqr(BinomialDegree(u, x)), C1))))))),
          IIntegrate(2551,
              Integrate(
                  Times(Log(u_), Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                      m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)), Log(u),
                          Power(Times(b, Plus(m, C1)), CN1)), x),
                      Dist(Power(Times(b, Plus(m, C1)), CN1),
                          Integrate(
                              SimplifyIntegrand(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                                  D(u, x), Power(u, CN1)), x),
                              x),
                          x)),
                  And(FreeQ(List(a, b, m), x), InverseFunctionFreeQ(u, x), NeQ(m, CN1)))),
          IIntegrate(2552, Integrate(Times(Log(u_), Power($p("§qx"), CN1)), x_Symbol),
              Condition(
                  With(List(Set(v, IntHide(Power($s("§qx"), CN1), x))),
                      Subtract(Simp(Times(v, Log(u)), x),
                          Integrate(SimplifyIntegrand(Times(v, D(u, x), Power(u, CN1)), x), x))),
                  And(QuadraticQ($s("§qx"), x), InverseFunctionFreeQ(u, x)))),
          IIntegrate(
              2553, Integrate(Times(Log(u_),
                  Power(u_, Times(a_DEFAULT, x_))), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(u, Times(a,
                          x)), Power(a,
                              CN1)),
                          x),
                      Integrate(
                          SimplifyIntegrand(Times(x, Power(u, Subtract(Times(a, x), C1)), D(u, x)),
                              x),
                          x)),
                  And(FreeQ(a, x), InverseFunctionFreeQ(u, x)))),
          IIntegrate(
              2554, Integrate(Times(Log(u_),
                  v_), x_Symbol),
              Condition(
                  With(
                      List(Set(w,
                          IntHide(v, x))),
                      Condition(
                          Subtract(
                              Dist(Log(u), w, x), Integrate(SimplifyIntegrand(
                                  Times(w, D(u, x), Power(u, CN1)), x), x)),
                          InverseFunctionFreeQ(w, x))),
                  InverseFunctionFreeQ(u, x))),
          IIntegrate(2555, Integrate(Times(Log(u_), v_), x_Symbol),
              Condition(
                  With(List(Set(w, IntHide(v, x))),
                      Condition(
                          Subtract(Dist(Log(u), w, x),
                              Integrate(SimplifyIntegrand(
                                  Times(w, Simplify(Times(D(u, x), Power(u, CN1)))), x), x)),
                          InverseFunctionFreeQ(w, x))),
                  ProductQ(u))),
          IIntegrate(
              2556, Integrate(Times(Log(v_),
                  Log(w_)), x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(x, Log(v),
                          Log(w)), x),
                      Negate(
                          Integrate(SimplifyIntegrand(Times(x, Log(w), D(v, x), Power(v, CN1)), x),
                              x)),
                      Negate(Integrate(
                          SimplifyIntegrand(Times(x, Log(v), D(w, x), Power(w, CN1)), x), x))),
                  And(InverseFunctionFreeQ(v, x), InverseFunctionFreeQ(w, x)))),
          IIntegrate(2557, Integrate(Times(Log(v_), Log(w_), u_), x_Symbol),
              Condition(With(
                  List(Set(z, IntHide(u, x))), Condition(
                      Plus(Dist(Times(Log(v), Log(w)), z, x),
                          Negate(Integrate(
                              SimplifyIntegrand(Times(z, Log(w), D(v, x), Power(v, CN1)), x), x)),
                          Negate(Integrate(
                              SimplifyIntegrand(Times(z, Log(v), D(w, x), Power(w, CN1)), x), x))),
                      InverseFunctionFreeQ(z, x))),
                  And(InverseFunctionFreeQ(v, x), InverseFunctionFreeQ(w, x)))),
          IIntegrate(
              2558, Integrate(Power(f_, Times(Log(u_), a_DEFAULT)), x_Symbol), Condition(
                  Integrate(Power(u, Times(a, Log(f))), x), FreeQ(List(a, f), x))),
          IIntegrate(2559, Integrate(u_, x_Symbol),
              Condition(
                  With(List(Set($s("lst"), FunctionOfLog(Cancel(Times(x, u)), x))),
                      Condition(Dist(Power(Part($s("lst"), C3), CN1),
                          Subst(Integrate(Part($s("lst"), C1), x), x, Log(Part($s("lst"), C2))), x),
                          Not(FalseQ($s("lst"))))),
                  NonsumQ(u))),
          IIntegrate(2560, Integrate(Times(Log(Gamma(v_)), u_DEFAULT), x_Symbol),
              Plus(Dist(Subtract(Log(Gamma(v)), LogGamma(v)), Integrate(u, x), x),
                  Integrate(Times(u, LogGamma(v)), x))));
}
