package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.D;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQuotient;
import static org.matheclipse.core.expression.F.PolynomialRemainder;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
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
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemoveContent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules79 {
  public static IAST RULES =
      List(
          IIntegrate(1581,
              Integrate(
                  Times(
                      Power(Times(f_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus($p("d1"), Times($p("e1", true), Power(x_, $p("non2", true)))),
                          q_DEFAULT),
                      Power(Plus($p("d2"),
                          Times($p("e2", true), Power(x_, $p("non2", true)))), q_DEFAULT),
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)), Times(c_DEFAULT,
                                  Power(x_, $p("n2")))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(
                      Power(Plus($s("d1"), Times($s("e1"), Power(x, Times(C1D2, n)))), FracPart(q)),
                      Power(Plus($s("d2"), Times($s("e2"), Power(x, Times(C1D2, n)))), FracPart(q)),
                      Power(Power(
                          Plus(Times($s("d1"), $s("d2")), Times($s("e1"), $s("e2"), Power(x, n))),
                          FracPart(q)), CN1)),
                      Integrate(Times(Power(Times(f, x), m),
                          Power(Plus(Times($s("d1"), $s("d2")),
                              Times($s("e1"), $s("e2"), Power(x, n))), q),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, n, p, q), x),
                      EqQ($s("n2"), Times(C2, n)), EqQ($s("non2"), Times(C1D2, n)), EqQ(Plus(
                          Times($s("d2"), $s("e1")), Times($s("d1"), $s("e2"))), C0)))),
          IIntegrate(1582,
              Integrate(Times($p("§px"),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Coeff($s("§px"), x, Subtract(n, C1)),
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)), Power(
                                  Times(b, n, Plus(p, C1)), CN1)),
                          x),
                      Integrate(
                          Times(
                              Subtract($s("§px"),
                                  Times(Coeff($s("§px"), x, Subtract(n, C1)),
                                      Power(x, Subtract(n, C1)))),
                              Power(Plus(a, Times(b, Power(x, n))), p)),
                          x)),
                  And(FreeQ(List(a, b), x), PolyQ($s("§px"), x), IGtQ(p, C1), IGtQ(n, C1),
                      NeQ(Coeff($s("§px"), x,
                          Subtract(n, C1)), C0),
                      NeQ($s("§px"), Times(Coeff($s("§px"), x, Subtract(n,
                          C1)), Power(x,
                              Subtract(n, C1)))),
                      Not(MatchQ($s("§px"), Condition(
                          Times($p("§qx", true),
                              Power(Plus(c_, Times(d_DEFAULT, Power(x, m_))), q_)),
                          And(FreeQ(List(c, d), x), PolyQ($s("§qx"), x), IGtQ(q, C1), IGtQ(m, C1),
                              NeQ(Coeff(Times($s("§qx"), Power(Plus(a, Times(b, Power(x, n))), p)),
                                  x, Subtract(m, C1)), C0),
                              GtQ(Times(m, q), Times(n, p))))))))),
          IIntegrate(1583,
              Integrate(
                  Times(
                      $p("§px"), Power(x_, m_DEFAULT), Power(Plus(a_,
                          Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Coeff($s("§px"), x, Subtract(Subtract(n, m), C1)),
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)), Power(
                                  Times(b, n, Plus(p, C1)), CN1)),
                          x),
                      Integrate(
                          Times(
                              Subtract($s("§px"),
                                  Times(Coeff($s("§px"), x, Subtract(Subtract(n, m), C1)),
                                      Power(x, Subtract(Subtract(n, m), C1)))),
                              Power(x, m), Power(Plus(a, Times(b, Power(x, n))), p)),
                          x)),
                  And(FreeQ(List(a, b, m, n), x), PolyQ($s("§px"), x), IGtQ(p, C1),
                      IGtQ(Subtract(n, m), C0),
                      NeQ(Coeff($s("§px"), x, Subtract(Subtract(n, m), C1)), C0)))),
          IIntegrate(1584,
              Integrate(
                  Times(u_DEFAULT, Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              Times(a_DEFAULT, Power(x_, p_DEFAULT)), Times(b_DEFAULT,
                                  Power(x_, q_DEFAULT))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          u, Power(x, Plus(m,
                              Times(n, p))),
                          Power(Plus(a, Times(b, Power(x, Subtract(q, p)))), n)),
                      x),
                  And(FreeQ(List(a, b, m, p, q), x), IntegerQ(n), PosQ(Subtract(q, p))))),
          IIntegrate(1585, Integrate(Times(u_DEFAULT, Power(x_, m_DEFAULT),
              Power(Plus(Times(a_DEFAULT, Power(x_, p_DEFAULT)),
                  Times(b_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT, Power(x_, r_DEFAULT))),
                  n_DEFAULT)),
              x_Symbol),
              Condition(
                  Integrate(Times(u, Power(x, Plus(m, Times(n, p))),
                      Power(Plus(a, Times(b, Power(x, Subtract(q, p))),
                          Times(c, Power(x, Subtract(r, p)))), n)),
                      x),
                  And(FreeQ(List(a, b, c, m, p, q, r), x), IntegerQ(n), PosQ(Subtract(q, p)),
                      PosQ(Subtract(r, p))))),
          IIntegrate(1586,
              Integrate(
                  Times(u_DEFAULT, Power($p("§px"), p_DEFAULT),
                      Power($p("§qx"), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(u, Power(PolynomialQuotient($s("§px"), $s("§qx"), x), p),
                          Power($s("§qx"), Plus(p, q))),
                      x),
                  And(LtQ(Times(p, q), C0), FreeQ(q, x), PolyQ($s("§px"), x), PolyQ($s(
                      "§qx"), x), EqQ(PolynomialRemainder($s("§px"), $s("§qx"), x),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(
              1587, Integrate(Times($p("§pp"),
                  Power($p("§qq"), CN1)), x_Symbol),
              Condition(
                  With(List(Set(p, Expon($s("§pp"), x)), Set(q, Expon($s("§qq"), x))),
                      Condition(
                          Simp(Times(Coeff($s("§pp"), x, p), Log(RemoveContent($s("§qq"), x)),
                              Power(Times(q, Coeff($s("§qq"), x, q)), CN1)), x),
                          And(EqQ(p, Subtract(q, C1)),
                              EqQ($s("§pp"),
                                  Simplify(Times(Coeff($s("§pp"), x, p), D($s("§qq"), x), Power(
                                      Times(q, Coeff($s("§qq"), x, q)), CN1))))))),
                  And(PolyQ($s("§pp"), x), PolyQ($s("§qq"), x)))),
          IIntegrate(
              1588, Integrate(Times($p("§pp"),
                  Power($p("§qq"), m_DEFAULT)), x_Symbol),
              Condition(
                  With(List(Set(p, Expon($s("§pp"), x)), Set(q, Expon($s("§qq"), x))), Condition(
                      Simp(
                          Times(Coeff($s("§pp"), x, p), Power(x, Plus(p, Negate(q), C1)),
                              Power($s("§qq"), Plus(m, C1)),
                              Power(Times(Plus(p, Times(m, q), C1), Coeff($s("§qq"), x, q)), CN1)),
                          x),
                      And(NeQ(Plus(p, Times(m, q), C1), C0),
                          EqQ(Times(Plus(p, Times(m, q), C1), Coeff($s("§qq"), x, q), $s("§pp")),
                              Times(Coeff($s("§pp"), x, p), Power(x, Subtract(p, q)),
                                  Plus(Times(Plus(p, Negate(q), C1), $s("§qq")),
                                      Times(Plus(m, C1), x, D($s("§qq"), x)))))))),
                  And(FreeQ(m, x), PolyQ($s("§pp"), x), PolyQ($s("§qq"), x), NeQ(m, CN1)))),
          IIntegrate(1589,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_DEFAULT))), p_),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_DEFAULT))), p_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), Plus(p, C1)),
                          Power(Plus($s("a2"), Times($s("b2"), Power(x, n))), Plus(p, C1)), Power(
                              Times(C2, $s("b1"), $s("b2"), n, Plus(p, C1)), CN1)),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), m, n, p), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0),
                      EqQ(Plus(m, Times(CN1, C2, n), C1), C0), NeQ(p, CN1)))),
          IIntegrate(1590,
              Integrate(Times($p("§pp"), Power($p("§qq"), m_DEFAULT), Power($p("§rr"), n_DEFAULT)),
                  x_Symbol),
              Condition(With(List(Set(
                  p, Expon($s("§pp"), x)), Set(q, Expon($s("§qq"), x)),
                  Set(r, Expon($s("§rr"), x))),
                  Condition(
                      Simp(Times(
                          Coeff($s("§pp"), x, p), Power(x, Plus(p, Negate(q), Negate(r), C1)),
                          Power($s("§qq"), Plus(m, C1)), Power($s("§rr"), Plus(n, C1)),
                          Power(Times(Plus(p, Times(m, q), Times(n, r), C1), Coeff($s("§qq"), x, q),
                              Coeff($s("§rr"), x, r)), CN1)),
                          x),
                      And(NeQ(Plus(p, Times(m, q), Times(n, r), C1), C0), EqQ(
                          Times(Plus(p, Times(m, q), Times(n, r), C1), Coeff($s("§qq"), x, q),
                              Coeff($s("§rr"), x, r), $s("§pp")),
                          Times(Coeff($s("§pp"), x, p), Power(x, Subtract(Subtract(p, q), r)),
                              Plus(Times(Plus(p, Negate(q), Negate(r), C1), $s("§qq"), $s("§rr")),
                                  Times(Plus(m, C1), x, $s("§rr"), D($s("§qq"), x)),
                                  Times(Plus(n, C1), x, $s("§qq"), D($s("§rr"), x)))))))),
                  And(FreeQ(List(m, n), x), NeQ(m, CN1), NeQ(n, CN1), PolyQ($s("§pp"), x), PolyQ($s(
                      "§qq"), x), PolyQ($s("§rr"),
                          x)))),
          IIntegrate(1591,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, Power($p("§pq"), n_DEFAULT))),
                          p_DEFAULT),
                      $p("§qr")),
                  x_Symbol),
              Condition(With(List(Set(q, Expon($s("§pq"), x)), Set(r, Expon($s("§qr"), x))),
                  Condition(Dist(
                      Times(Coeff($s("§qr"), x, r), Power(Times(q, Coeff($s("§pq"), x, q)), CN1)),
                      Subst(Integrate(Power(Plus(a, Times(b, Power(x, n))), p), x), x, $s("§pq")),
                      x),
                      And(EqQ(r, Subtract(q, C1)),
                          EqQ(Times(Coeff($s("§qr"), x, r), D($s("§pq"), x)),
                              Times(q, Coeff($s("§pq"), x, q), $s("§qr")))))),
                  And(FreeQ(List(a, b, n, p), x), PolyQ($s("§pq"), x), PolyQ($s("§qr"), x)))),
          IIntegrate(1592,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power($p("§pq"),
                                  n_DEFAULT)),
                              Times(c_DEFAULT, Power($p("§pq"), $p("n2", true)))),
                          p_DEFAULT),
                      $p("§qr")),
                  x_Symbol),
              Condition(
                  Module(List(Set(q, Expon($s("§pq"), x)), Set(r, Expon($s("§qr"), x))),
                      Condition(
                          Dist(Times(Coeff($s("§qr"), x, r),
                              Power(Times(q, Coeff($s("§pq"), x, q)), CN1)),
                              Subst(Integrate(Power(
                                  Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                                  p), x), x, $s("§pq")),
                              x),
                          And(EqQ(r, Subtract(q, C1)),
                              EqQ(Times(Coeff($s("§qr"), x, r), D($s("§pq"), x)),
                                  Times(q, Coeff($s("§pq"), x, q), $s("§qr")))))),
                  And(FreeQ(List(a, b, c, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      PolyQ($s("§pq"), x), PolyQ($s("§qr"), x)))),
          IIntegrate(1593,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              Times(a_DEFAULT, Power(x_, p_DEFAULT)), Times(b_DEFAULT,
                                  Power(x_, q_DEFAULT))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          u, Power(x, Times(n,
                              p)),
                          Power(Plus(a, Times(b, Power(x, Subtract(q, p)))), n)),
                      x),
                  And(FreeQ(List(a, b, p, q), x), IntegerQ(n), PosQ(Subtract(q, p))))),
          IIntegrate(1594,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(Times(a_DEFAULT, Power(x_, p_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, q_DEFAULT)), Times(c_DEFAULT,
                                  Power(x_, r_DEFAULT))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(u, Power(x, Times(n, p)),
                          Power(Plus(a, Times(b, Power(x, Subtract(q, p))),
                              Times(c, Power(x, Subtract(r, p)))), n)),
                      x),
                  And(FreeQ(List(a, b, c, p, q, r), x), IntegerQ(n), PosQ(Subtract(q, p)), PosQ(
                      Subtract(r, p))))),
          IIntegrate(1595,
              Integrate(
                  Times(Sqrt(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_)),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D2),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), CN1D2),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(b, BSymbol, Sqrt(Plus(c, Times(d, x))), Sqrt(Plus(e, Times(f, x))),
                          Sqrt(Plus(g, Times(h, x))), Power(
                              Times(d, f, h, Sqrt(Plus(a, Times(b, x)))), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(
                                  BSymbol, Subtract(Times(b, g), Times(a, h)),
                                  Power(Times(C2, f, h), CN1)),
                              Integrate(
                                  Times(Sqrt(Plus(e, Times(f, x))),
                                      Power(
                                          Times(Sqrt(Plus(a, Times(b, x))),
                                              Sqrt(Plus(c, Times(d, x))), Sqrt(
                                                  Plus(g, Times(h, x)))),
                                          CN1)),
                                  x),
                              x)),
                      Dist(Times(
                          BSymbol, Subtract(Times(b, e), Times(a, f)),
                          Subtract(Times(b, g), Times(a, h)), Power(Times(C2, d, f, h), CN1)),
                          Integrate(Times(Sqrt(Plus(c, Times(d, x))),
                              Power(
                                  Times(Power(Plus(a, Times(b, x)), QQ(3L, 2L)),
                                      Sqrt(Plus(e, Times(f, x))), Sqrt(Plus(g, Times(h, x)))),
                                  CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, ASymbol, BSymbol), x),
                      EqQ(Subtract(Times(C2, ASymbol, d, f),
                          Times(BSymbol, Plus(Times(d, e), Times(c, f)))), C0)))),
          IIntegrate(1596,
              Integrate(Times(Sqrt(Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                  Plus(A_DEFAULT, Times(B_DEFAULT, x_)),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D2),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1D2),
                  Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), CN1D2)), x_Symbol),
              Condition(Plus(Simp(Times(BSymbol, Sqrt(Plus(a, Times(b, x))), Sqrt(Plus(e, Times(f,
                  x))), Sqrt(Plus(g, Times(h, x))), Power(Times(f, h, Sqrt(Plus(c, Times(d, x)))),
                      CN1)),
                  x),
                  Negate(Dist(Times(BSymbol, Subtract(Times(b, e), Times(a, f)), Subtract(Times(b,
                      g), Times(a, h)), Power(Times(C2, b, f, h),
                          CN1)),
                      Integrate(
                          Power(
                              Times(Sqrt(Plus(a, Times(b, x))), Sqrt(Plus(c, Times(d, x))),
                                  Sqrt(Plus(e, Times(f, x))), Sqrt(Plus(g, Times(h, x)))),
                              CN1),
                          x),
                      x)),
                  Dist(
                      Times(BSymbol, Subtract(Times(d, e), Times(
                          c, f)), Subtract(Times(d, g),
                              Times(c, h)),
                          Power(Times(C2, d, f, h), CN1)),
                      Integrate(Times(Sqrt(Plus(a, Times(b, x))),
                          Power(Times(Power(Plus(c, Times(d, x)), QQ(3L, 2L)),
                              Sqrt(Plus(e, Times(f, x))), Sqrt(Plus(g, Times(h, x)))),
                              CN1)),
                          x),
                      x),
                  Dist(
                      Times(
                          Plus(Times(C2, ASymbol, b, d, f, h), Times(BSymbol,
                              Subtract(Times(a, d, f, h),
                                  Times(b, Plus(Times(d, f, g), Times(d, e, h), Times(c, f, h)))))),
                          Power(Times(C2, b, d, f, h), CN1)),
                      Integrate(
                          Times(Sqrt(Plus(a, Times(b, x))),
                              Power(Times(Sqrt(Plus(c, Times(d, x))), Sqrt(Plus(e, Times(f, x))),
                                  Sqrt(Plus(g, Times(h, x)))), CN1)),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, ASymbol,
                      BSymbol), x), NeQ(
                          Subtract(
                              Times(C2, ASymbol, d, f), Times(BSymbol,
                                  Plus(Times(d, e), Times(c, f)))),
                          C0)))),
          IIntegrate(1597,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_)),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D2),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1D2), Power(
                          Plus(g_DEFAULT, Times(h_DEFAULT, x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(d, f, h, Plus(Times(C2, m), C3)), CN1),
                      Integrate(Times(Power(Plus(a, Times(b, x)), Subtract(m, C1)),
                          Power(Times(Sqrt(Plus(c, Times(d, x))), Sqrt(Plus(e, Times(f, x))),
                              Sqrt(Plus(g, Times(h, x)))), CN1),
                          Simp(
                              Plus(Times(a, ASymbol, d, f, h, Plus(Times(C2, m), C3)),
                                  Times(
                                      Plus(Times(ASymbol, b), Times(a, BSymbol)), d, f, h, Plus(
                                          Times(C2, m), C3),
                                      x),
                                  Times(b, BSymbol, d, f, h, Plus(Times(C2, m), C3), Sqr(x))),
                              x)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, ASymbol, BSymbol), x),
                      IntegerQ(Times(C2, m)), GtQ(m, C0)))),
          IIntegrate(1598,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1D2),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_)),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D2),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1D2),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(Dist(Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)), Power(b, CN1)),
                      Integrate(
                          Power(Times(Sqrt(Plus(a, Times(b, x))), Sqrt(Plus(c, Times(d, x))),
                              Sqrt(Plus(e, Times(f, x))), Sqrt(Plus(g, Times(h, x)))), CN1),
                          x),
                      x),
                      Dist(Times(BSymbol, Power(b, CN1)),
                          Integrate(Times(Sqrt(Plus(a, Times(b, x))),
                              Power(Times(Sqrt(Plus(c, Times(d, x))), Sqrt(Plus(e, Times(f, x))),
                                  Sqrt(Plus(g, Times(h, x)))), CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e, f, g, h, ASymbol, BSymbol), x))),
          IIntegrate(1599,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_)),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D2),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1D2), Power(
                          Plus(g_DEFAULT, Times(h_DEFAULT, x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(Simp(Times(Subtract(Times(ASymbol, Sqr(b)), Times(a, b, BSymbol)), Power(
                      Plus(a, Times(b, x)), Plus(m, C1)), Sqrt(Plus(c, Times(d, x))),
                      Sqrt(Plus(e, Times(f, x))), Sqrt(Plus(g, Times(h, x))),
                      Power(Times(Plus(m, C1), Subtract(Times(b, c), Times(a, d)),
                          Subtract(Times(b, e), Times(a, f)), Subtract(Times(b, g), Times(a, h))),
                          CN1)),
                      x),
                      Dist(Power(
                          Times(C2, Plus(m, C1), Subtract(Times(b, c), Times(a, d)), Subtract(
                              Times(b, e), Times(a, f)), Subtract(Times(b, g), Times(a, h))),
                          CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Times(Sqrt(Plus(c, Times(d, x))), Sqrt(Plus(e, Times(f, x))),
                                  Sqrt(Plus(g, Times(h, x)))), CN1),
                              Simp(
                                  Plus(
                                      Times(ASymbol, Plus(Times(C2, Sqr(a), d, f, h, Plus(m, C1)),
                                          Times(CN1, C2, a, b, Plus(m, C1),
                                              Plus(Times(d, f, g), Times(d, e, h), Times(c, f, h))),
                                          Times(Sqr(b), Plus(Times(C2, m), C3),
                                              Plus(Times(d, e, g), Times(c, f, g),
                                                  Times(c, e, h))))),
                                      Times(CN1, b, BSymbol,
                                          Plus(
                                              Times(a,
                                                  Plus(Times(d, e, g), Times(c, f, g),
                                                      Times(c, e, h))),
                                              Times(C2, b, c, e, g, Plus(m, C1)))),
                                      Times(CN1, C2, Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                          Subtract(Times(a, d, f, h, Plus(m, C1)),
                                              Times(b, Plus(m, C2),
                                                  Plus(Times(d, f, g), Times(d, e, h),
                                                      Times(c, f, h)))),
                                          x),
                                      Times(d, f, h, Plus(Times(C2, m), C5),
                                          Subtract(Times(ASymbol, Sqr(b)), Times(a, b, BSymbol)),
                                          Sqr(x))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, ASymbol, BSymbol), x),
                      IntegerQ(Times(C2, m)), LtQ(m, CN1)))),
          IIntegrate(1600,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1D2),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), CN1D2),
                      Power(
                          Plus(g_DEFAULT, Times(h_DEFAULT, x_)), CN1D2),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_)))),
                  x_Symbol),
              Condition(
                  Plus(Simp(Times(C2, CSymbol, Power(Plus(a, Times(b, x)), m), Sqrt(Plus(
                      c, Times(d, x))), Sqrt(Plus(e, Times(f, x))), Sqrt(Plus(g, Times(h, x))),
                      Power(Times(d, f, h, Plus(Times(C2, m), C3)), CN1)), x),
                      Dist(
                          Power(Times(d, f, h, Plus(Times(C2, m), C3)), CN1), Integrate(
                              Times(Power(Plus(a, Times(b, x)), Subtract(m, C1)),
                                  Power(Times(Sqrt(Plus(c, Times(d, x))),
                                      Sqrt(Plus(e, Times(f, x))), Sqrt(Plus(g, Times(h, x)))), CN1),
                                  Simp(Plus(
                                      Times(a, ASymbol, d, f, h, Plus(
                                          Times(C2, m), C3)),
                                      Times(CN1, CSymbol,
                                          Plus(Times(a,
                                              Plus(Times(d, e, g), Times(c, f, g), Times(c, e, h))),
                                              Times(C2, b, c, e, g, m))),
                                      Times(Subtract(
                                          Times(Plus(Times(ASymbol, b), Times(a, BSymbol)), d, f, h,
                                              Plus(Times(C2, m), C3)),
                                          Times(CSymbol,
                                              Plus(
                                                  Times(C2, a,
                                                      Plus(Times(d, f, g), Times(d, e, h),
                                                          Times(c, f, h))),
                                                  Times(b, Plus(Times(C2, m), C1),
                                                      Plus(Times(d, e, g), Times(c, f, g),
                                                          Times(c, e, h)))))),
                                          x),
                                      Times(Plus(Times(b, BSymbol, d, f, h, Plus(Times(C2, m), C3)),
                                          Times(C2, CSymbol,
                                              Subtract(Times(a, d, f, h, m),
                                                  Times(b, Plus(m, C1),
                                                      Plus(Times(d, f, g), Times(d, e, h),
                                                          Times(c, f, h)))))),
                                          Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, ASymbol, BSymbol, CSymbol), x),
                      IntegerQ(Times(C2, m)), GtQ(m, C0)))));
}
