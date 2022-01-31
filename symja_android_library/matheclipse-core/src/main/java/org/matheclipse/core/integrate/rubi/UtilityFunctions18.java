package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.$str;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.Apply;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Distribute;
import static org.matheclipse.core.expression.F.Expand;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.G_;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.LessEqual;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Print;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.List;
import static org.matheclipse.core.expression.S.Plus;
import static org.matheclipse.core.expression.S.Times;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.k;
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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ActivateTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandCleanup;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandLinearProduct;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.G;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedTrinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.MergeMonomials;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeTerms;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyTerm;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrinomialQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions18 {
  public static IAST RULES =
      List(
          ISetDelayed(300, ExpandToSum(u_, v_, x_Symbol),
              Module(
                  List(Set(w,
                      ExpandToSum(v, x)), r),
                  CompoundExpression(Set(r, NonfreeTerms(w, x)),
                      If(SumQ(r),
                          Plus(
                              Times(u, FreeTerms(w, x)), Map(
                                  Function(MergeMonomials(Times(u, Slot1), x)), r)),
                          Plus(Times(u, FreeTerms(w, x)), MergeMonomials(Times(u, r), x)))))),
          ISetDelayed(301, ExpandToSum(u_, x_Symbol),
              If(PolyQ(u, x),
                  Simp(
                      Apply(
                          Plus, Map(
                              Function(Times(Coeff(u, x, Slot1),
                                  Power(x, Slot1))),
                              Expon(u, x, List))),
                      x),
                  If(BinomialQ(u, x),
                      $(Function(Plus(Part(Slot1, C1),
                          Times(Part(Slot1, C2), Power(x, Part(Slot1, C3))))), BinomialParts(u,
                              x)),
                      If(TrinomialQ(u, x),
                          $(Function(Plus(Part(Slot1, C1),
                              Times(Part(Slot1, C2), Power(x, Part(Slot1, C4))), Times(Part(Slot1,
                                  C3), Power(x, Times(C2, Part(Slot1, C4)))))),
                              TrinomialParts(u, x)),
                          If(GeneralizedBinomialQ(u, x),
                              $(Function(Plus(Times(Part(Slot1, C1), Power(x, Part(Slot1, C4))),
                                  Times(Part(Slot1, C2), Power(x, Part(Slot1, C3))))),
                                  GeneralizedBinomialParts(u, x)),
                              If(GeneralizedTrinomialQ(u, x), $(
                                  Function(
                                      Plus(Times(Part(Slot1, C1), Power(x, Part(Slot1, C5))),
                                          Times(Part(Slot1, C2), Power(x, Part(Slot1, C4))),
                                          Times(Part(Slot1, C3),
                                              Power(x,
                                                  Subtract(Times(C2, Part(Slot1, C4)),
                                                      Part(Slot1, C5)))))),
                                  GeneralizedTrinomialParts(u, x)),
                                  CompoundExpression(
                                      Print($str("Warning: Unrecognized expression for expansion "),
                                          u),
                                      Expand(u, x)))))))),
          ISetDelayed(302, ExpandTrig(u_,
              x_Symbol), ActivateTrig(ExpandIntegrand(u, x))),
          ISetDelayed(303, ExpandTrig(u_, v_, x_Symbol),
              With(
                  List(Set(w, ExpandTrig(v, x)), Set(z, ActivateTrig(u))), If(SumQ(w),
                      Map(Function(Times(z, Slot1)), w), Times(z, w)))),
          ISetDelayed(304, ExpandIntegrand(u_, v_, x_Symbol),
              Module(
                  List(Set(w,
                      ExpandIntegrand(v, x)), r),
                  CompoundExpression(
                      Set(r, NonfreeTerms(w,
                          x)),
                      If(SumQ(r),
                          Plus(
                              Times(u, FreeTerms(w, x)), Map(
                                  Function(MergeMonomials(Times(u, Slot1), x)), r)),
                          Plus(Times(u, FreeTerms(w, x)), MergeMonomials(Times(u, r), x)))))),
          ISetDelayed(
              305, ExpandIntegrand(Power(u_,
                  p_DEFAULT), x_Symbol),
              Condition(
                  If(EqQ(p, C1), ExpandCleanup(u, x), ExpandCleanup(Expand(Power(u, p), x),
                      x)),
                  And(SumQ(u), IGtQ(p, C0)))),
          ISetDelayed(306,
              ExpandIntegrand(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  p_DEFAULT), x_Symbol),
              Condition(
                  ExpandIntegrand(
                      Times(Power(x, Times(n, p)),
                          Power(Plus(b, Times(a, Power(x, Negate(n)))), p)),
                      x),
                  And(IntegerQ(p), ILtQ(n, C0)))),
          ISetDelayed(307,
              ExpandIntegrand(
                  Times(
                      Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  ExpandIntegrand(
                      Times(Power(x, Plus(m, Times(n, p))),
                          Power(Plus(b, Times(a, Power(x, Negate(n)))), p)),
                      x),
                  And(IntegerQ(p), ILtQ(n, C0)))),
          ISetDelayed(308,
              ExpandIntegrand(Times($p("§px", true), Power(x_, m_),
                  Power(Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                      Times(c_DEFAULT, Power(x_, $p("n1")))), p_DEFAULT)),
                  x_Symbol),
              Condition(ExpandIntegrand(Times($s("§px"), Power(x, Plus(m, Times(n, p))),
                  Power(Plus(b, Times(c, x)), p)), x),
                  And(FreeQ(List(b, c, m), x), PolyQ($s("§px"), x), IGtQ(n, C0),
                      EqQ($s("n1"), Plus(n, C1)), IntegerQ(p)))),
          ISetDelayed(309,
              ExpandIntegrand(
                  Times($p("§px", true),
                      Power(
                          Plus(Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                              Times(c_DEFAULT, Power(x_, $p("n1")))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  ExpandIntegrand(Times($s("§px"), Power(x, Times(n, p)),
                      Power(Plus(b, Times(c, x)), p)), x),
                  And(FreeQ(List(b,
                      c), x), PolyQ($s("§px"),
                          x),
                      IGtQ(n, C0), EqQ($s("n1"), Plus(n, C1)), IntegerQ(p)))),
          ISetDelayed(310,
              ExpandIntegrand(
                  Times(
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT,
                          Power(F_, u_))), p_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, Power(F_, v_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Simplify(Times(u, Power(v, CN1))))),
                      Condition(
                          ReplaceAll(
                              ExpandIntegrand(
                                  Times(
                                      Power(Plus(a, Times(b,
                                          Power(x, Numerator(k)))), p),
                                      Power(Plus(c, Times(d, Power(x, Denominator(k)))), q)),
                                  x),
                              Rule(x, Power(FSymbol, Times(v, Power(Denominator(k), CN1))))),
                          RationalQ(k))),
                  And(FreeQ(List(FSymbol, a, b, c, d), x), IntegersQ(p, q)))),
          ISetDelayed(311,
              ExpandIntegrand(
                  Times(
                      Power(F_,
                          Times(e_DEFAULT, Power(Plus(c_DEFAULT,
                              Times(d_DEFAULT, x_)), n_DEFAULT))),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("tmp"),
                          Subtract(Times(a, h), Times(b, g)))),
                      Module(List(k),
                          Plus(
                              Times(
                                  SimplifyTerm(
                                      Times(Power($s("tmp"), m), Power(Power(h, m), CN1)), x),
                                  Power(FSymbol, Times(e, Power(Plus(c, Times(d, x)), n))), Power(
                                      Plus(g, Times(h, x)), CN1)),
                              Sum(Times(
                                  SimplifyTerm(Times(b, Power($s("tmp"), Subtract(k, C1)),
                                      Power(Power(h, k), CN1)), x),
                                  Power(FSymbol, Times(e,
                                      Power(Plus(c, Times(d, x)), n))),
                                  Power(Plus(a, Times(b, x)), Subtract(m, k))),
                                  List(k, C1, m))))),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, g, h), x), IGtQ(m, C0),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          ISetDelayed(312,
              ExpandIntegrand(
                  Times(
                      Power(F_,
                          Times(b_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)),
                              n_DEFAULT))),
                      Power(x_, m_DEFAULT), Power(Plus(e_, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  If(And(IGtQ(m, C0), IGtQ(p, C0), LessEqual(m, p),
                      Or(EqQ(n, C1), EqQ(Subtract(Times(d, e), Times(c, f)), C0))),
                      ExpandLinearProduct(
                          Times(
                              Power(Plus(e, Times(f, x)), p), Power(FSymbol,
                                  Times(b, Power(Plus(c, Times(d, x)), n)))),
                          Power(x, m), e, f, x),
                      If(IGtQ(p, C0),
                          Distribute(
                              Times(Power(x, m),
                                  Power(FSymbol, Times(b, Power(Plus(c, Times(d, x)), n))),
                                  Expand(Power(Plus(e, Times(f, x)), p), x)),
                              Plus, Times),
                          ExpandIntegrand(
                              Power(FSymbol, Times(b,
                                  Power(Plus(c, Times(d, x)), n))),
                              Times(Power(x, m), Power(Plus(e, Times(f, x)), p)), x))),
                  FreeQ(List(FSymbol, b, c, d, e, f, m, n, p), x))),
          ISetDelayed(313,
              ExpandIntegrand(
                  Times(
                      Power(F_,
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)),
                                      n_DEFAULT)))),
                      Power(x_, m_DEFAULT), Power(Plus(e_, Times(f_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  If(And(
                      IGtQ(m, C0), IGtQ(p, C0), LessEqual(m, p), Or(EqQ(n, C1),
                          EqQ(Subtract(Times(d, e), Times(c, f)), C0))),
                      ExpandLinearProduct(
                          Times(
                              Power(Plus(e,
                                  Times(f, x)), p),
                              Power(FSymbol, Plus(a, Times(b, Power(Plus(c, Times(d, x)), n))))),
                          Power(x, m), e, f, x),
                      If(IGtQ(p, C0),
                          Distribute(
                              Times(Power(x, m),
                                  Power(FSymbol, Plus(a, Times(b,
                                      Power(Plus(c, Times(d, x)), n)))),
                                  Expand(Power(Plus(e, Times(f, x)), p), x)),
                              Plus, Times),
                          ExpandIntegrand(
                              Power(FSymbol, Plus(a, Times(b, Power(Plus(c, Times(d, x)), n)))),
                              Times(Power(x, m), Power(Plus(e, Times(f, x)), p)), x))),
                  FreeQ(List(FSymbol, a, b, c, d, e, f, m, n, p), x))),
          ISetDelayed(314,
              ExpandIntegrand(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT, Power(F_, v_))),
                          m_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(F_, v_))), n_), u_DEFAULT),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(w,
                              ReplaceAll(
                                  ExpandIntegrand(
                                      Times(Power(Plus(a, Times(b, x)), m),
                                          Power(Plus(c, Times(d, x)), n)),
                                      x),
                                  Rule(x, Power(FSymbol, v))))),
                      Condition(Map(Function(Times(u, Slot1)), w), SumQ(w))),
                  And(FreeQ(List(FSymbol, a, b, c, d), x), IntegersQ(m, n), Less(n, C0)))),
          ISetDelayed(315,
              ExpandIntegrand(
                  Times(
                      Power(F_,
                          Times(e_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)),
                              n_DEFAULT))),
                      u_, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(v,
                          ExpandIntegrand(Times(u, Power(Plus(a, Times(b, x)), m)), x))),
                      Condition(
                          Distribute(
                              Times(Power(FSymbol, Times(e, Power(Plus(c, Times(d, x)), n))),
                                  v),
                              Plus, Times),
                          SumQ(v))),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, m, n), x), PolynomialQ(u, x)))),
          ISetDelayed(316,
              ExpandIntegrand(
                  Times(
                      Log(Times(c_DEFAULT,
                          Power(Plus(d_DEFAULT, Times(e_DEFAULT, Power(x_, n_DEFAULT))),
                              p_DEFAULT))),
                      u_, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  ExpandIntegrand(Log(Times(c, Power(Plus(d, Times(e, Power(x, n))), p))), Times(u,
                      Power(Plus(a, Times(b, x)), m)), x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x), PolynomialQ(u, x)))),
          ISetDelayed(317,
              ExpandIntegrand(
                  Times(
                      Power(F_,
                          Times(e_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)),
                              n_DEFAULT))),
                      u_),
                  x_Symbol),
              Condition(If(EqQ(n, C1),
                  ExpandIntegrand(Power(FSymbol, Times(e, Power(Plus(c, Times(d, x)), n))), u, x),
                  ExpandLinearProduct(Power(FSymbol, Times(e, Power(Plus(c, Times(d, x)), n))), u,
                      c, d, x)),
                  And(FreeQ(List(FSymbol, c, d, e, n), x), PolynomialQ(u, x)))),
          ISetDelayed(318,
              ExpandIntegrand(Times(Power($(F_, u_), m_DEFAULT),
                  Power(Plus(a_, Times($(G_, u_), b_DEFAULT)), n_DEFAULT)), x_Symbol),
              Condition(
                  ReplaceAll(
                      ExpandIntegrand(
                          Times(Power(Plus(a, Times(b, x)), n), Power(Power(x, m), CN1)), x),
                      Rule(x, G(u))),
                  And(FreeQ(List(a, b), x), IntegersQ(m, n), SameQ(Times(F(u), G(u)), C1)))));
}
