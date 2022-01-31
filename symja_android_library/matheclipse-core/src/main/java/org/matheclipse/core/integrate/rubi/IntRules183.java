package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
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
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.ZZ;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
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
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules183 {
  public static IAST RULES =
      List(
          IIntegrate(3661,
              Integrate(
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT,
                              Power(
                                  Times(
                                      c_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                          Times(f_DEFAULT, x_)))),
                                  n_))),
                      p_),
                  x_Symbol),
              Condition(
                  With(List(Set($s("ff"), FreeFactors(Tan(Plus(e, Times(f, x))), x))),
                      Dist(Times(c, $s("ff"), Power(f, CN1)),
                          Subst(Integrate(Times(
                              Power(Plus(a, Times(b, Power(Times($s("ff"), x), n))), p),
                              Power(Plus(Sqr(c), Times(Sqr($s("ff")), Sqr(x))), CN1)), x), x, Times(
                                  c, Tan(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, e, f, n, p), x), Or(IntegersQ(n, p), IGtQ(p, C0),
                      EqQ(Sqr(n), C4), EqQ(Sqr(n), ZZ(16L)))))),
          IIntegrate(3662,
              Integrate(
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT,
                              Power(
                                  Times(c_DEFAULT,
                                      $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                  n_))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Power(Plus(a, Times(b, Power(Times(c, Tan(Plus(e, Times(f, x)))), n))),
                          p),
                      x),
                  FreeQ(List(a, b, c, e, f, n, p), x))),
          IIntegrate(3663,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(c_DEFAULT,
                                          $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(With(List(Set($s("ff"), FreeFactors(Tan(Plus(e, Times(f, x))), x))),
                  Dist(Times(c, Power($s("ff"), Plus(m, C1)), Power(f, CN1)),
                      Subst(Integrate(Times(Power(x, m),
                          Power(Plus(a, Times(b, Power(Times($s("ff"), x), n))), p),
                          Power(Power(Plus(Sqr(c), Times(Sqr($s("ff")), Sqr(x))),
                              Plus(Times(C1D2, m), C1)), CN1)),
                          x), x, Times(c, Tan(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                      x)),
                  And(FreeQ(List(a, b, c, e, f, n, p), x), IntegerQ(Times(C1D2, m))))),
          IIntegrate(3664,
              Integrate(
                  Times(Power($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, Sqr($($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("ff"),
                          FreeFactors(Sec(Plus(e, Times(f, x))), x))),
                      Dist(
                          Power(Times(f,
                              Power($s("ff"), m)), CN1),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(Plus(CN1, Times(Sqr($s("ff")), Sqr(x))), Times(C1D2,
                                          Subtract(m, C1))),
                                      Power(Plus(a, Negate(b), Times(b, Sqr($s(
                                          "ff")), Sqr(
                                              x))),
                                          p),
                                      Power(Power(x, Plus(m, C1)), CN1)),
                                  x),
                              x, Times(Sec(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, b, e, f, p), x), IntegerQ(Times(C1D2, Subtract(m, C1)))))),
          IIntegrate(3665,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_DEFAULT),
                      Power(Plus(a_,
                          Times(
                              b_DEFAULT, Power($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("ff"),
                          FreeFactors(Sec(Plus(e, Times(f, x))), x))),
                      Dist(
                          Power(Times(f,
                              Power($s("ff"), m)), CN1),
                          Subst(Integrate(Times(
                              Power(Plus(CN1, Times(Sqr($s("ff")), Sqr(x))),
                                  Times(C1D2, Subtract(m, C1))),
                              Power(Plus(a,
                                  Times(b,
                                      Power(Plus(CN1, Times(Sqr($s("ff")), Sqr(x))),
                                          Times(C1D2, n)))),
                                  p),
                              Power(Power(x, Plus(m, C1)), CN1)), x), x,
                              Times(Sec(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, b, e, f, p), x), IntegerQ(Times(C1D2,
                      Subtract(m, C1))), IntegerQ(
                          Times(C1D2, n))))),
          IIntegrate(3666,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(c_DEFAULT,
                                          $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrig(
                          Times(Power(Times(d, $($s("§sin"), Plus(e, Times(f, x)))), m),
                              Power(
                                  Plus(a,
                                      Times(b, Power(Times(c, $($s("§tan"), Plus(e, Times(f, x)))),
                                          n))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), IGtQ(p, C0)))),
          IIntegrate(3667,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_),
                      Power(Plus(a_,
                          Times(b_DEFAULT, Sqr($($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))))),
                          p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("ff"),
                          FreeFactors(Tan(Plus(e, Times(f, x))), x))),
                      Dist(
                          Times($s("ff"), Power(Times(d, Sin(Plus(e, Times(f, x)))), m),
                              Power(Sqr(Sec(Plus(e, Times(f, x)))), Times(C1D2, m)), Power(
                                  Times(f, Power(Tan(Plus(e, Times(f, x))), m)), CN1)),
                          Subst(Integrate(Times(Power(Times($s("ff"), x), m),
                              Power(Plus(a, Times(b, Sqr($s("ff")), Sqr(x))), p),
                              Power(Power(Plus(C1, Times(Sqr($s("ff")), Sqr(x))),
                                  Plus(Times(C1D2, m), C1)), CN1)),
                              x), x, Times(Tan(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, m, p), x), Not(IntegerQ(m))))),
          IIntegrate(3668,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(c_DEFAULT,
                                          $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Times(d,
                          Sin(Plus(e, Times(f, x)))), m), Power(
                              Plus(a, Times(b, Power(Times(c, Tan(Plus(e, Times(f, x)))), n))), p)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, m, n, p), x))),
          IIntegrate(3669,
              Integrate(Times(
                  Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      d_DEFAULT), m_),
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT,
                              Power(
                                  Times(
                                      c_DEFAULT,
                                      $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                  n_))),
                      p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(d, Cos(Plus(e, Times(f,
                              x)))), FracPart(
                                  m)),
                          Power(Times(Sec(Plus(e, Times(f, x))), Power(d, CN1)), FracPart(m))),
                      Integrate(
                          Times(Power(
                              Plus(a, Times(b, Power(Times(c, Tan(Plus(e, Times(f, x)))), n))), p),
                              Power(Power(Times(Sec(Plus(e, Times(f, x))), Power(d, CN1)), m),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(m))))),
          IIntegrate(3670,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(
                                          c_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT,
                                              x_)))),
                                      n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("ff"),
                          FreeFactors(Tan(Plus(e, Times(f, x))), x))),
                      Dist(Times(c, $s("ff"), Power(f, CN1)),
                          Subst(
                              Integrate(Times(Power(Times(d, $s("ff"), x, Power(c, CN1)), m),
                                  Power(Plus(a, Times(b,
                                      Power(Times($s("ff"), x), n))), p),
                                  Power(Plus(Sqr(c), Times(Sqr($s("ff")), Sqr(x))), CN1)), x),
                              x, Times(c, Tan(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m, n,
                      p), x), Or(IGtQ(p, C0), EqQ(n, C2), EqQ(n, C4),
                          And(IntegerQ(p), RationalQ(n)))))),
          IIntegrate(3671,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(c_DEFAULT,
                                          $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrig(
                          Times(
                              Power(Times(d,
                                  $($s("§tan"), Plus(e, Times(f, x)))), m),
                              Power(
                                  Plus(a,
                                      Times(b,
                                          Power(Times(c, $($s("§tan"), Plus(e, Times(f, x)))), n))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), IGtQ(p, C0)))),
          IIntegrate(3672,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(
                                          c_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT,
                                              x_)))),
                                      n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Times(d,
                              Tan(Plus(e, Times(f, x)))), m),
                          Power(Plus(a, Times(b, Power(Times(c, Tan(Plus(e, Times(f, x)))), n))),
                              p)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, m, n, p), x))),
          IIntegrate(3673,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          m_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT,
                                          x_))),
                                      n_DEFAULT))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(d, Times(n,
                          p)),
                      Integrate(
                          Times(
                              Power(Times(d, Cot(
                                  Plus(e, Times(f, x)))), Subtract(m,
                                      Times(n, p))),
                              Power(Plus(b, Times(a, Power(Cot(Plus(e, Times(f, x))), n))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, m, n, p), x), Not(IntegerQ(m)), IntegersQ(n, p)))),
          IIntegrate(3674,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          m_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(
                                          c_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT,
                                              x_)))),
                                      n_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(d, Cot(
                              Plus(e, Times(f, x)))), FracPart(
                                  m)),
                          Power(Times(Tan(Plus(e, Times(f, x))), Power(d, CN1)), FracPart(m))),
                      Integrate(
                          Times(
                              Power(
                                  Plus(a, Times(b,
                                      Power(Times(c, Tan(Plus(e, Times(f, x)))), n))),
                                  p),
                              Power(Power(Times(Tan(Plus(e, Times(f, x))), Power(d, CN1)), m),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(IntegerQ(m))))),
          IIntegrate(3675,
              Integrate(
                  Times(Power($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(c_DEFAULT,
                                          $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("ff"),
                          FreeFactors(Tan(Plus(e, Times(f, x))), x))),
                      Dist(
                          Times($s("ff"), Power(Times(Power(c, Subtract(m, C1)), f),
                              CN1)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(
                                          Plus(Sqr(c), Times(Sqr($s("ff")), Sqr(x))), Subtract(
                                              Times(C1D2, m), C1)),
                                      Power(Plus(a, Times(b, Power(Times($s("ff"), x), n))), p)),
                                  x),
                              x, Times(c, Tan(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, e, f, n, p), x), IntegerQ(Times(C1D2, m)),
                      Or(IntegersQ(n, p), IGtQ(m, C0), IGtQ(p, C0), EqQ(Sqr(
                          n), C4), EqQ(Sqr(n),
                              ZZ(16L)))))),
          IIntegrate(3676,
              Integrate(
                  Times(
                      Power($($s("§sec"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_DEFAULT),
                      Power(Plus(a_,
                          Times(
                              b_DEFAULT, Power($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("ff"),
                          FreeFactors(Sin(Plus(e, Times(f, x))), x))),
                      Dist(Times($s("ff"), Power(f, CN1)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(ExpandToSum(Plus(Times(b, Power(Times($s("ff"), x), n)),
                                          Times(a,
                                              Power(Subtract(C1, Times(Sqr($s("ff")), Sqr(x))),
                                                  Times(C1D2, n)))),
                                          x), p),
                                      Power(Power(Subtract(C1, Times(Sqr($s("ff")), Sqr(x))),
                                          Times(C1D2, Plus(m, Times(n, p), C1))), CN1)),
                                  x),
                              x, Times(Sin(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, b, e, f), x), IntegerQ(Times(C1D2,
                      Subtract(m, C1))), IntegerQ(
                          Times(C1D2, n)),
                      IntegerQ(p)))),
          IIntegrate(3677,
              Integrate(
                  Times(
                      Power($($s("§sec"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_DEFAULT),
                      Power(Plus(a_,
                          Times(
                              b_DEFAULT, Power($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("ff"),
                          FreeFactors(Sin(Plus(e, Times(f, x))), x))),
                      Dist(Times($s("ff"), Power(f, CN1)), Subst(Integrate(Times(C1, Power(Times(
                          Plus(Times(b, Power(Times($s("ff"), x), n)),
                              Times(a,
                                  Power(Subtract(C1, Times(Sqr($s("ff")), Sqr(x))),
                                      Times(C1D2, n)))),
                          Power(Power(Subtract(C1, Times(Sqr($s("ff")), Sqr(x))), Times(C1D2, n)),
                              CN1)),
                          p),
                          Power(
                              Power(Subtract(C1, Times(Sqr($s("ff")), Sqr(x))),
                                  Times(C1D2, Plus(m, C1))),
                              CN1)),
                          x), x, Times(Sin(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, b, e, f, p), x), IntegerQ(Times(C1D2, Subtract(m,
                      C1))), IntegerQ(Times(C1D2,
                          n)),
                      Not(IntegerQ(p))))),
          IIntegrate(3678,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sec"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(c_DEFAULT,
                                          $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrig(
                          Times(
                              Power(Times(d,
                                  $($s("§sec"), Plus(e, Times(f, x)))), m),
                              Power(
                                  Plus(a,
                                      Times(b, Power(Times(c, $($s("§tan"), Plus(e, Times(f, x)))),
                                          n))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), IGtQ(p, C0)))),
          IIntegrate(3679,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT,
                              $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_),
                      Power(
                          Plus(a_,
                              Times(
                                  b_DEFAULT, Sqr($($s("§tan"), Plus(e_DEFAULT,
                                      Times(f_DEFAULT, x_)))))),
                          p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("ff"),
                          FreeFactors(Tan(Plus(e, Times(f, x))), x))),
                      Dist(
                          Times($s("ff"), Power(Times(d, Sec(Plus(e, Times(f, x)))), m),
                              Power(
                                  Times(f, Power(Sqr(Sec(Plus(e, Times(f, x)))),
                                      Times(C1D2, m))),
                                  CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(
                                      Plus(C1, Times(Sqr($s("ff")), Sqr(x))), Subtract(Times(C1D2,
                                          m), C1)),
                                      Power(Plus(a, Times(b, Sqr($s("ff")), Sqr(x))), p)),
                                  x),
                              x, Times(Tan(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, m, p), x), Not(IntegerQ(m))))),
          IIntegrate(3680,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sec"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(
                                          c_DEFAULT, $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT,
                                              x_)))),
                                      n_))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(Unintegrable(
                  Times(Power(Times(d, Sec(Plus(e, Times(f, x)))), m),
                      Power(Plus(a, Times(b, Power(Times(c, Tan(Plus(e, Times(f, x)))), n))), p)),
                  x), FreeQ(List(a, b, c, d, e, f, m, n, p), x))));
}
