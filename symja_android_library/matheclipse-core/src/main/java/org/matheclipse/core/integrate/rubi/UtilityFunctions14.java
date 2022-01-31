package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$rubi;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Apply;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Complex;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.LessEqual;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.OrderedQ;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.UnsameQ;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.a_Symbol;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.b_Symbol;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.c_Symbol;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_Symbol;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.e_Symbol;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.r_;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.s_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.w_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.S.GCD;
import static org.matheclipse.core.expression.S.List;
import static org.matheclipse.core.expression.S.Plus;
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
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FactorNumericGcd;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FixSimplify;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimpFixFactor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions14 {
  public static IAST RULES =
      List(
          ISetDelayed(221,
              FixSimplify(
                  Times(Plus(u_DEFAULT, Times(b_DEFAULT, Sqrt(v_)), Times(c_DEFAULT, Sqrt(v_)),
                      Times(a_DEFAULT, Sqrt($p(v, Plus)))), w_DEFAULT)),
              FixSimplify(Times(w, Plus(u, Times(FixSimplify(Plus(a, b, c)), Sqrt(v)))))),
          ISetDelayed(222,
              FixSimplify(
                  Times(Plus(u_DEFAULT, Times(b_DEFAULT, Sqrt(v_)),
                      Times(a_DEFAULT, Sqrt($p(v, Plus)))), w_DEFAULT)),
              FixSimplify(Times(w, Plus(u, Times(FixSimplify(Plus(a, b)), Sqrt(v)))))),
          ISetDelayed(
              223, FixSimplify(Times(u_DEFAULT, Power(v_, m_),
                  Power(w_, n_))),
              Condition(
                  Negate(FixSimplify(
                      Times(u, Power(v, Subtract(m, C1))))),
                  And(RationalQ(m), Not(RationalQ(
                      w)), FractionQ(
                          n),
                      Less(n, C0), EqQ(Plus(v, Power(w, Negate(n))), C0)))),
          ISetDelayed(224, FixSimplify(Times(u_DEFAULT, Power(v_, m_), Power(w_, n_DEFAULT))),
              Condition(Times(Power(CN1, n), FixSimplify(Times(u, Power(v, Plus(m, n))))), And(
                  RationalQ(m), Not(RationalQ(w)), IntegerQ(n), EqQ(Plus(v, w), C0)))),
          ISetDelayed(225,
              FixSimplify(Times(u_DEFAULT, Power(Negate(
                  Power(v_, p_DEFAULT)), m_), Power(w_,
                      n_DEFAULT))),
              Condition(
                  Times(
                      Power(CN1, Times(n,
                          Power(p, CN1))),
                      FixSimplify(
                          Times(u, Power(Negate(Power(v, p)), Plus(m, Times(n, Power(p, CN1))))))),
                  And(RationalQ(m), Not(RationalQ(w)), IntegerQ(Times(n, Power(p, CN1))),
                      EqQ(Subtract(v, w), C0)))),
          ISetDelayed(226,
              FixSimplify(
                  Times(u_DEFAULT, Power(Negate(Power(v_, p_DEFAULT)), m_), Power(w_, n_DEFAULT))),
              Condition(
                  Times(Power(CN1, Plus(n, Times(n, Power(p, CN1)))),
                      FixSimplify(
                          Times(u, Power(Negate(Power(v, p)), Plus(m, Times(n, Power(p, CN1))))))),
                  And(RationalQ(m), Not(RationalQ(w)), IntegersQ(n, Times(n, Power(p, CN1))),
                      EqQ(Plus(v, w), C0)))),
          ISetDelayed(227,
              FixSimplify(Times(Power(Subtract(a_, b_), m_DEFAULT), Power(Plus(a_, b_), m_DEFAULT),
                  u_DEFAULT)),
              Condition(Times(u, Power(Subtract(Sqr(a), Sqr(b)), m)),
                  And(IntegerQ(m), AtomQ(a), AtomQ(b)))),
          ISetDelayed(228,
              FixSimplify(Times(Power(
                  Plus(Times(c_Symbol, Sqr(d_Symbol)),
                      Times(e_Symbol,
                          Plus(Times(CN1, b_Symbol, d_Symbol), Times(a_Symbol, e_Symbol)))),
                  m_DEFAULT), u_DEFAULT)),
              Condition(
                  Times(u, Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                      m)),
                  And(RationalQ(m), OrderedQ(List(a, b, c, d, e))))),
          ISetDelayed(229,
              FixSimplify(
                  Times(
                      Power(
                          Plus(
                              Times(c_Symbol, Sqr(d_Symbol)),
                              Times(e_Symbol,
                                  Plus(Times(CN1, b_Symbol, d_Symbol), Times(a_Symbol, e_Symbol)))),
                          m_DEFAULT),
                      u_DEFAULT)),
              Condition(
                  Times(u, Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), m)),
                  And(RationalQ(m), OrderedQ(List(a, b, c, d, e))))),
          ISetDelayed(230,
              FixSimplify(
                  Times(
                      Power(
                          Plus(Times(CN1, c_Symbol, Sqr(d_Symbol)),
                              Times(e_Symbol,
                                  Plus(Times(b_Symbol, d_Symbol), Times(CN1, a_Symbol, e_Symbol)))),
                          m_DEFAULT),
                      u_DEFAULT)),
              Condition(
                  Times(Power(CN1, m), u,
                      Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), m)),
                  And(IntegerQ(m), OrderedQ(List(a, b, c, d, e))))),
          ISetDelayed(231,
              FixSimplify(
                  Times(Power(
                      Plus(Times(CN1, c_Symbol, Sqr(d_Symbol)),
                          Times(e_Symbol,
                              Plus(Times(b_Symbol, d_Symbol), Times(CN1, a_Symbol, e_Symbol)))),
                      m_DEFAULT), u_DEFAULT)),
              Condition(
                  Times(
                      Power(CN1, m), u, Power(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                          Times(a, Sqr(e))), m)),
                  And(RationalQ(m), OrderedQ(List(a, b, c, d, e))))),
          ISetDelayed(232, FixSimplify(u_), u),
          ISetDelayed(233,
              SimpFixFactor(
                  Power(Plus(Times(Complex(C0, c_), a_DEFAULT), Times(Complex(C0, d_), b_DEFAULT)),
                      p_DEFAULT),
                  x_),
              Condition(
                  Times(Power(CI, p), SimpFixFactor(Power(Plus(Times(a, c), Times(b, d)), p),
                      x)),
                  IntegerQ(p))),
          ISetDelayed(234,
              SimpFixFactor(
                  Power(Plus(Times(Complex(C0, d_), a_DEFAULT), Times(Complex(C0, e_), b_DEFAULT),
                      Times(Complex(C0, f_), c_DEFAULT)), p_DEFAULT),
                  x_),
              Condition(
                  Times(
                      Power(CI, p), SimpFixFactor(
                          Power(Plus(Times(a, d), Times(b, e), Times(c, f)), p), x)),
                  IntegerQ(p))),
          ISetDelayed(235,
              SimpFixFactor(
                  Power(
                      Plus(Times(a_DEFAULT, Power(c_,
                          r_)), Times(b_DEFAULT,
                              Power(x_, n_DEFAULT))),
                      p_DEFAULT),
                  x_),
              Condition(Times(Power(c, Times(r, p)),
                  SimpFixFactor(Power(Plus(a, Times(b, Power(Power(c, r), CN1), Power(x, n))), p),
                      x)),
                  And(FreeQ(List(a, b, c), x), IntegersQ(n, p), AtomQ(c), RationalQ(r),
                      Less(r, C0)))),
          ISetDelayed(236,
              SimpFixFactor(
                  Power(
                      Plus(a_DEFAULT, Times(b_DEFAULT, Power(c_, r_),
                          Power(x_, n_DEFAULT))),
                      p_DEFAULT),
                  x_),
              Condition(Times(Power(c, Times(r, p)),
                  SimpFixFactor(Power(
                      Plus(Times(a, Power(Power(c, r), CN1)), Times(b, Power(x, n))), p), x)),
                  And(FreeQ(List(a, b,
                      c), x), IntegersQ(n,
                          p),
                      AtomQ(c), RationalQ(r), Less(r, C0)))),
          ISetDelayed(237,
              SimpFixFactor(
                  Power(
                      Plus(Times(a_DEFAULT, Power(c_, s_DEFAULT)),
                          Times(b_DEFAULT, Power(c_, r_DEFAULT), Power(x_, n_DEFAULT))),
                      p_DEFAULT),
                  x_),
              Condition(
                  Times(Power(c, Times(s, p)),
                      SimpFixFactor(
                          Power(Plus(a, Times(b, Power(c, Subtract(r, s)), Power(x, n))), p), x)),
                  And(FreeQ(List(a, b, c), x), IntegersQ(n, p), RationalQ(s, r), And(Less(C0, s),
                      LessEqual(s, r)), UnsameQ(Power(c, Times(s, p)), CN1)))),
          ISetDelayed(238,
              SimpFixFactor(
                  Power(
                      Plus(Times(a_DEFAULT, Power(c_, s_DEFAULT)),
                          Times(b_DEFAULT, Power(c_, r_DEFAULT), Power(x_, n_DEFAULT))),
                      p_DEFAULT),
                  x_),
              Condition(
                  Times(Power(c, Times(r, p)),
                      SimpFixFactor(
                          Power(Plus(Times(a, Power(c, Subtract(s, r))), Times(b, Power(x, n))),
                              p),
                          x)),
                  And(FreeQ(List(a, b, c), x), IntegersQ(n, p), RationalQ(s, r), Less(C0, r, s),
                      UnsameQ(Power(c, Times(r, p)), CN1)))),
          ISetDelayed(239, SimpFixFactor(u_,
              x_), u),
          ISetDelayed(240, FactorNumericGcd(u_),
              If(And(PowerQ(u), RationalQ(Part(u, C2))),
                  Power(FactorNumericGcd(Part(u,
                      C1)), Part(u,
                          C2)),
                  If(ProductQ(u), Map($rubi("FactorNumericGcd"), u),
                      If(SumQ(u),
                          With(
                              List(Set(g, Apply(GCD, Map($rubi("NumericFactor"), Apply(List, u))))),
                              Times(g, Map(Function(Times(Slot1, Power(g, CN1))), u))),
                          u)))));
}
