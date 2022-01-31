package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.EvenQ;
import static org.matheclipse.core.expression.F.Exponent;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FractionalPart;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.GreaterEqual;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerPart;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.LeafCount;
import static org.matheclipse.core.expression.F.Length;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.LessEqual;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.PolynomialQuotientRemainder;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Product;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Unequal;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.j_;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.ArcCos;
import static org.matheclipse.core.expression.S.ArcCosh;
import static org.matheclipse.core.expression.S.ArcSin;
import static org.matheclipse.core.expression.S.ArcSinh;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.j;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandExpression;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandLinearProduct;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyTerm;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions19 {
  public static IAST RULES =
      List(
          ISetDelayed(319,
              ExpandIntegrand(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT,
                                      Power(Times(d_DEFAULT,
                                          Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), p_DEFAULT)),
                                          q_DEFAULT))),
                                  b_DEFAULT)),
                          n_),
                      u_),
                  x_Symbol),
              Condition(
                  ExpandLinearProduct(
                      Power(
                          Plus(a,
                              Times(b,
                                  Log(Times(c, Power(Times(d, Power(Plus(e, Times(f, x)), p)),
                                      q))))),
                          n),
                      u, e, f, x),
                  And(FreeQ(List(a, b, c, d, e, f, n, p, q), x), PolynomialQ(u, x)))),
          ISetDelayed(320,
              ExpandIntegrand(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  b_DEFAULT)),
                          n_),
                      u_),
                  x_Symbol),
              Condition(
                  ExpandLinearProduct(Power(Plus(a, Times(b, F(Plus(c, Times(d, x))))),
                      n), u, c, d, x),
                  And(FreeQ(List(a, b, c, d,
                      n), x), PolynomialQ(u, x), MemberQ(List(ArcSin, ArcCos, ArcSinh, ArcCosh),
                          FSymbol)))),
          ISetDelayed(321,
              ExpandIntegrand(
                  Times(u_DEFAULT,
                      Power(
                          Plus(Times(a_DEFAULT, Power(x_, n_)),
                              Times(b_DEFAULT, Sqrt(Plus(c_, Times(d_DEFAULT, Power(x_, j_)))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  ExpandIntegrand(
                      Times(u,
                          Subtract(Times(a, Power(x, n)),
                              Times(b, Sqrt(Plus(c, Times(d, Power(x, Times(C2, n))))))),
                          Power(
                              Plus(Times(CN1, Sqr(b), c),
                                  Times(Subtract(Sqr(a), Times(Sqr(b), d)),
                                      Power(x, Times(C2, n)))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), EqQ(j, Times(C2, n))))),
          ISetDelayed(322,
              ExpandIntegrand(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT, x_)),
                          m_),
                      Power(Plus(c_, Times(d_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  If(RationalQ(a, b, c, d),
                      ExpandExpression(
                          Times(Power(Plus(a, Times(b, x)), m),
                              Power(Plus(c, Times(d, x)), CN1)),
                          x),
                      With(List(Set($s("tmp"), Subtract(Times(a, d), Times(b, c)))),
                          Module(List(k),
                              Plus(
                                  Times(
                                      SimplifyTerm(
                                          Times(Power($s("tmp"), m), Power(Power(d, m), CN1)), x),
                                      Power(Plus(c, Times(d, x)), CN1)),
                                  Sum(Times(
                                      SimplifyTerm(Times(b, Power($s("tmp"), Subtract(k, C1)),
                                          Power(Power(d, k), CN1)), x),
                                      Power(Plus(a, Times(b, x)), Subtract(m, k))),
                                      List(k, C1, m)))))),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(m, C0)))),
          ISetDelayed(323, ExpandIntegrand(Times(Plus(A_, Times(B_DEFAULT, x_)), Power(
              Plus(a_, Times(b_DEFAULT, x_)), m_DEFAULT),
              Power(Plus(c_, Times(d_DEFAULT, x_)), CN1)), x_Symbol),
              Condition(
                  If(RationalQ(a, b, c, d, ASymbol, BSymbol),
                      ExpandExpression(
                          Times(
                              Power(Plus(a, Times(b,
                                  x)), m),
                              Plus(ASymbol, Times(BSymbol, x)), Power(Plus(c, Times(d, x)), CN1)),
                          x),
                      Module(List($s("tmp1"), $s("tmp2")),
                          CompoundExpression(
                              Set($s("tmp1"),
                                  Times(Subtract(Times(ASymbol, d), Times(BSymbol, c)),
                                      Power(d, CN1))),
                              Set($s("tmp2"),
                                  ExpandIntegrand(Times(Power(Plus(a, Times(b, x)), m),
                                      Power(Plus(c, Times(d, x)), CN1)), x)),
                              Set($s("tmp2"), If(SumQ($s("tmp2")),
                                  Map(Function(SimplifyTerm(Times($s("tmp1"), Slot1), x)),
                                      $s("tmp2")),
                                  SimplifyTerm(Times($s("tmp1"), $s("tmp2")), x))),
                              Plus(Times(SimplifyTerm(Times(BSymbol, Power(d, CN1)), x),
                                  Power(Plus(a, Times(b, x)), m)), $s("tmp2"))))),
                  And(FreeQ(List(a, b, c, d, ASymbol, BSymbol), x), IGtQ(m, C0)))),
          ISetDelayed(324,
              ExpandIntegrand(Times(u_, Power(Plus(a_, Times(b_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(c_, Times(d_DEFAULT, x_)), n_DEFAULT)), x_Symbol),
              Condition(
                  ExpandIntegrand(Power(Plus(c, Times(d, x)), n),
                      Times(u, Power(Plus(a, Times(b, x)), m)), x),
                  And(FreeQ(List(a, b, c, d, m, n), x), PolynomialQ(u, x), Not(IntegerQ(m)),
                      IGtQ(Subtract(n, m), C0)))),
          ISetDelayed(325,
              ExpandIntegrand(Times(u_,
                  Power(Plus(a_, Times(b_DEFAULT, x_)), m_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(
                          Set($s("sum1"),
                              ExpandLinearProduct(Power(Plus(a, Times(b, x)), m), u, a, b, x))),
                      If(Or(Not(IntegerQ(m)), And(Greater(m, C2), LinearQ(u, x))), $s("sum1"),
                          With(
                              List(
                                  Set($s("sum2"),
                                      ExpandExpression(Times(u, Power(Plus(a, Times(b, x)), m)),
                                          x))),
                              If(SumQ($s("sum2")),
                                  If(Greater(m, C0),
                                      If(Or(LessEqual(Length($s("sum2")), Plus(Exponent(u, x), C2)),
                                          LessEqual(LeafCount($s("sum2")),
                                              Times(QQ(2L, 3L), LeafCount($s("sum1"))))),
                                          $s("sum2"), $s("sum1")),
                                      If(LessEqual(LeafCount($s("sum2")),
                                          Plus(LeafCount($s("sum1")), C2)), $s("sum2"),
                                          $s("sum1"))),
                                  $s("sum1"))))),
                  And(FreeQ(List(a, b, m), x), PolynomialQ(u, x), Not(And(IGtQ(m, C0), MatchQ(u,
                      Condition(Times(w_DEFAULT, Power(Plus(c_, Times(d_DEFAULT, x)), p_)),
                          And(FreeQ(List(c, d), x), IntegerQ(p), Greater(p, m))))))))),
          ISetDelayed(326,
              ExpandIntegrand(Times(u_, Power(v_, n_),
                  Power(Plus(a_, Times(b_DEFAULT, x_)), m_)), x_Symbol),
              Condition(
                  $(Function(
                      Plus(
                          ExpandIntegrand(Times(
                              Part(Slot1, C1), Power(Plus(a, Times(b, x)), FractionalPart(m))), x),
                          ExpandIntegrand(
                              Times(Part(Slot1, C2), Power(v, n), Power(Plus(a, Times(b, x)), m)),
                              x))),
                      PolynomialQuotientRemainder(u,
                          Times(
                              Power(v, Negate(n)), Power(Plus(a, Times(b, x)),
                                  Negate(IntegerPart(m)))),
                          x)),
                  And(FreeQ(List(a, b, m), x), ILtQ(n, C0), Not(IntegerQ(m)), PolynomialQ(u, x),
                      PolynomialQ(v, x), RationalQ(m), Less(m, CN1),
                      GreaterEqual(Exponent(u, x), Times(CN1, Plus(n, IntegerPart(m)),
                          Exponent(v, x)))))),
          ISetDelayed(327,
              ExpandIntegrand(Times(u_, Power(v_, n_),
                  Power(Plus(a_, Times(b_DEFAULT, x_)), m_)), x_Symbol),
              Condition($(Function(
                  Plus(ExpandIntegrand(Times(Part(Slot1, C1), Power(Plus(a, Times(b, x)), m)), x),
                      ExpandIntegrand(Times(Part(Slot1, C2), Power(v, n),
                          Power(Plus(a, Times(b, x)), m)), x))),
                  PolynomialQuotientRemainder(u, Power(v, Negate(n)), x)),
                  And(FreeQ(List(a, b, m), x), ILtQ(n, C0), Not(IntegerQ(m)), PolynomialQ(u, x),
                      PolynomialQ(v, x), GreaterEqual(Exponent(u, x),
                          Times(CN1, n, Exponent(v, x)))))),
          ISetDelayed(328,
              ExpandIntegrand(Power(Plus(a_, Times(b_DEFAULT, Power(u_, n_))),
                  CN1), x_Symbol),
              Condition(
                  With(
                      List(Set(r, Numerator(Rt(Times(CN1, a, Power(b, CN1)), C2))),
                          Set(s, Denominator(Rt(Times(CN1, a, Power(b, CN1)), C2)))),
                      Plus(Times(r,
                          Power(Times(C2, a, Subtract(r, Times(s, Power(u, Times(C1D2, n))))),
                              CN1)),
                          Times(r,
                              Power(Times(C2, a, Plus(r, Times(s, Power(u, Times(C1D2, n))))),
                                  CN1)))),
                  And(FreeQ(List(a, b), x), IGtQ(Times(C1D2, n), C0)))),
          ISetDelayed(329,
              ExpandIntegrand(
                  Times(
                      Plus(c_, Times(d_DEFAULT,
                          Power(u_, n_))),
                      Power(Plus(a_, Times(b_DEFAULT, Power(u_, $p("n2")))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(r, Numerator(Rt(Times(CN1, a, Power(b, CN1)), C2))),
                          Set(s, Denominator(Rt(Times(CN1, a, Power(b, CN1)), C2)))),
                      Plus(
                          Times(CN1, s, Plus(Times(d, r), Times(c, s)),
                              Power(Times(C2, b, r, Subtract(r, Times(s, Power(u, n)))), CN1)),
                          Times(s, Subtract(Times(d, r), Times(c, s)),
                              Power(Times(C2, b, r, Plus(r, Times(s, Power(u, n)))), CN1)))),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(n, C0), EqQ($s("n2"), Times(C2, n))))),
          ISetDelayed(330,
              ExpandIntegrand(
                  Times(
                      Plus(c_DEFAULT, Times(d_DEFAULT,
                          u_)),
                      Power(Plus(a_, Times(b_DEFAULT, u_)), m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Times(d, Power(b, CN1), Power(Plus(a, Times(b,
                          u)), Plus(m,
                              C1))),
                      Times(
                          Subtract(Times(b, c), Times(a,
                              d)),
                          Power(b, CN1), Power(Plus(a, Times(b, u)), m))),
                  And(FreeQ(List(a, b, c, d), x), ILtQ(m, C0)))),
          ISetDelayed(331,
              ExpandIntegrand(Power(Plus(a_, Times(b_DEFAULT, Power(u_, n_))),
                  CN1), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, Numerator(Rt(Times(CN1, a, Power(b, CN1)), n))), Set(s,
                              Denominator(Rt(Times(CN1, a, Power(b, CN1)), n)))),
                      Module(
                          List(k), Sum(
                              Times(r,
                                  Power(
                                      Times(a, n,
                                          Subtract(r,
                                              Times(Power(CN1, Times(C2, k, Power(n, CN1))), s,
                                                  u))),
                                      CN1)),
                              List(k, C1, n)))),
                  And(FreeQ(List(a, b), x), IGtQ(n, C1)))),
          ISetDelayed(332,
              ExpandIntegrand(
                  Times(
                      Plus(c_, Times(d_DEFAULT,
                          Power(u_, m_DEFAULT))),
                      Power(Plus(a_, Times(b_DEFAULT, Power(u_, n_))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, Numerator(
                              Rt(Times(CN1, a, Power(b, CN1)), n))),
                          Set(s, Denominator(Rt(Times(CN1, a, Power(b, CN1)), n)))),
                      Module(
                          List(k), Sum(
                              Times(
                                  Plus(Times(r, c),
                                      Times(r, d, Power(Times(r, Power(s, CN1)), m),
                                          Power(CN1, Times(CN2, k, m, Power(n, CN1))))),
                                  Power(Times(a, n,
                                      Subtract(r, Times(Power(CN1, Times(C2, k, Power(n, CN1))), s,
                                          u))),
                                      CN1)),
                              List(k, C1, n)))),
                  And(FreeQ(List(a, b, c, d), x), IntegersQ(m, n), Less(C0, m, n)))),
          ISetDelayed(333,
              ExpandIntegrand(
                  Times(
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, Power(u_,
                              m_DEFAULT)),
                          Times(e_DEFAULT, Power(u_, p_))),
                      Power(Plus(a_, Times(b_DEFAULT, Power(u_, n_))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, Numerator(
                              Rt(Times(CN1, a, Power(b, CN1)), n))),
                          Set(s, Denominator(Rt(Times(CN1, a, Power(b, CN1)), n)))),
                      Module(
                          List(k), Sum(
                              Times(
                                  Plus(Times(r, c),
                                      Times(
                                          r, d, Power(Times(r,
                                              Power(s, CN1)), m),
                                          Power(CN1, Times(CN2, k, m, Power(n, CN1)))),
                                      Times(r, e, Power(Times(r, Power(s, CN1)), p),
                                          Power(CN1, Times(CN2, k, p, Power(n, CN1))))),
                                  Power(
                                      Times(a, n,
                                          Subtract(r,
                                              Times(Power(CN1, Times(C2, k, Power(n, CN1))), s,
                                                  u))),
                                      CN1)),
                              List(k, C1, n)))),
                  And(FreeQ(List(a, b, c, d, e), x), IntegersQ(m, n, p), Less(C0, m, p, n)))),
          ISetDelayed(334,
              ExpandIntegrand(
                  Times(
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, Power(u_, m_DEFAULT)), Times(e_DEFAULT, Power(
                              u_, p_)),
                          Times(f_DEFAULT, Power(u_, q_))),
                      Power(Plus(a_, Times(b_DEFAULT, Power(u_, n_))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, Numerator(Rt(Times(CN1, a, Power(b, CN1)), n))), Set(s,
                              Denominator(Rt(Times(CN1, a, Power(b, CN1)), n)))),
                      Module(
                          List(k), Sum(
                              Times(
                                  Plus(Times(r, c),
                                      Times(
                                          r, d, Power(Times(r, Power(s, CN1)), m), Power(CN1,
                                              Times(CN2, k, m, Power(n, CN1)))),
                                      Times(
                                          r, e, Power(Times(r,
                                              Power(s, CN1)), p),
                                          Power(CN1, Times(CN2, k, p, Power(n, CN1)))),
                                      Times(
                                          r, f, Power(Times(r, Power(s, CN1)), q),
                                          Power(CN1, Times(CN2, k, q, Power(n, CN1))))),
                                  Power(Times(a, n,
                                      Subtract(r,
                                          Times(Power(CN1, Times(C2, k, Power(n, CN1))), s, u))),
                                      CN1)),
                              List(k, C1, n)))),
                  And(FreeQ(List(a, b, c, d, e, f), x), IntegersQ(m, n, p, q),
                      Less(C0, m, p, q, n)))),
          ISetDelayed(335,
              ExpandIntegrand(Power(Plus(a_, Times(c_DEFAULT, Power(u_, n_))),
                  p_), x_Symbol),
              Condition(
                  Module(List(q),
                      ReplaceAll(
                          ExpandIntegrand(Power(Power(c, p), CN1),
                              Times(
                                  Power(Plus(Negate(q), Times(c, x)),
                                      p),
                                  Power(Plus(q, Times(c, x)), p)),
                              x),
                          List(
                              Rule(q, Rt(Times(CN1, a, c), C2)), Rule(
                                  x, Power(u, Times(C1D2, n)))))),
                  And(FreeQ(List(a, c), x), EvenQ(n), ILtQ(p, C0)))),
          ISetDelayed(336,
              ExpandIntegrand(
                  Times(Power(u_, m_DEFAULT), Power(
                      Plus(a_DEFAULT, Times(c_DEFAULT, Power(u_, n_))), p_)),
                  x_Symbol),
              Condition(Module(List(q), ReplaceAll(ExpandIntegrand(Power(Power(c, p), CN1),
                  Times(Power(x, m), Power(Plus(Negate(q), Times(c, Power(x, Times(C1D2, n)))), p),
                      Power(Plus(q, Times(c, Power(x, Times(C1D2, n)))), p)),
                  x), List(Rule(q, Rt(Times(CN1, a, c), C2)), Rule(x, u)))), And(
                      FreeQ(List(a, c), x), IntegersQ(m, Times(C1D2,
                          n)),
                      ILtQ(p, C0), Less(C0, m, n), Unequal(m, Times(C1D2, n))))),
          ISetDelayed(337,
              ExpandIntegrand(Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))),
                  p_), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(CN1, a, Power(b, CN1)), n))),
                      Module(List($s("ii")),
                          ExpandIntegrand(Power(Negate(b), p),
                              Product(Power(
                                  Subtract(q,
                                      Times(Power(CN1, Times(C2, $s("ii"), Power(n, CN1))), x)),
                                  p), List($s("ii"), C1, n)),
                              x))),
                  And(FreeQ(List(a, b), x), IGtQ(n, C1), ILtQ(p, CN1)))),
          ISetDelayed(338,
              ExpandIntegrand(Power(Plus(
                  a_DEFAULT, Times(b_DEFAULT, Power(u_, n_DEFAULT)),
                  Times(c_DEFAULT, Power(u_, $p("n2", true)))), p_), x_Symbol),
              Condition(
                  Module(List(q),
                      ReplaceAll(
                          ExpandIntegrand(Power(Times(Power(C4, p), Power(c, p)), CN1),
                              Times(Power(Plus(b, Negate(q), Times(C2, c, x)), p),
                                  Power(Plus(b, q, Times(C2, c, x)), p)),
                              x),
                          List(Rule(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2)),
                              Rule(x, Power(u, n))))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(n), EqQ($s("n2"), Times(C2, n)),
                      ILtQ(p, C0), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))));
}
