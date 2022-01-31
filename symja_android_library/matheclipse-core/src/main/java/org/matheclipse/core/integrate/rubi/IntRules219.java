package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.MatchQ;
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
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.v_DEFAULT;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.F.y_;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.y;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ActivateTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.DerivativeDivides;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FalseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InertTrigFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InertTrigQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemoveContent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstFor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigSimplify;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigSimplifyQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules219 {
  public static IAST RULES =
      List(
          IIntegrate(4381,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  c_DEFAULT, Sqr(
                                      $($s("§sec"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))))),
                              Times(b_DEFAULT, Sqr($($s("§tan"), Plus(d_DEFAULT,
                                  Times(e_DEFAULT, x_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Plus(a, c), p), Integrate(ActivateTrig(u),
                      x), x),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(b, c), C0)))),
          IIntegrate(4382,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Sqr($($s("§cot"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                                  b_DEFAULT),
                              Times(Sqr($($s("§csc"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                                  c_DEFAULT)),
                          p_DEFAULT),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(Plus(a, c), p), Integrate(ActivateTrig(u), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(b, c), C0)))),
          IIntegrate(
              4383, Integrate(Times(u_,
                  Power(y_, CN1)), x_Symbol),
              Condition(
                  With(List(Set(q, DerivativeDivides(ActivateTrig(y), ActivateTrig(u), x))),
                      Condition(Simp(Times(q, Log(RemoveContent(ActivateTrig(y), x))), x),
                          Not(FalseQ(q)))),
                  Not(InertTrigFreeQ(u)))),
          IIntegrate(4384, Integrate(Times(u_, Power(w_, CN1), Power(y_, CN1)), x_Symbol),
              Condition(With(
                  List(Set(q, DerivativeDivides(ActivateTrig(Times(y, w)), ActivateTrig(u), x))),
                  Condition(Simp(Times(q, Log(RemoveContent(ActivateTrig(Times(y, w)), x))), x),
                      Not(FalseQ(q)))),
                  Not(InertTrigFreeQ(u)))),
          IIntegrate(
              4385, Integrate(Times(u_,
                  Power(y_, m_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(ActivateTrig(y), ActivateTrig(u), x))),
                      Condition(
                          Simp(
                              Times(q, ActivateTrig(Power(y, Plus(m, C1))),
                                  Power(Plus(m, C1), CN1)),
                              x),
                          Not(FalseQ(q)))),
                  And(FreeQ(m, x), NeQ(m, CN1), Not(InertTrigFreeQ(u))))),
          IIntegrate(4386,
              Integrate(Times(u_, Power(y_, m_DEFAULT),
                  Power(z_, n_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(q,
                              DerivativeDivides(ActivateTrig(Times(y, z)),
                                  ActivateTrig(Times(u, Power(z, Subtract(n, m)))), x))),
                      Condition(
                          Simp(
                              Times(q,
                                  ActivateTrig(Times(Power(y, Plus(m, C1)),
                                      Power(z, Plus(m, C1)))),
                                  Power(Plus(m, C1), CN1)),
                              x),
                          Not(FalseQ(q)))),
                  And(FreeQ(List(m, n), x), NeQ(m, CN1), Not(InertTrigFreeQ(u))))),
          IIntegrate(4387,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Times(a_DEFAULT, Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_)),
                          n_)),
                  x_Symbol),
              Condition(
                  With(List(Set(v, ActivateTrig(F(Plus(c, Times(d, x)))))),
                      Dist(Times(Power(a, IntPart(n)),
                          Power(Times(v, Power(NonfreeFactors(v, x), CN1)), Times(p, IntPart(n))),
                          Power(Times(a, Power(v, p)), FracPart(
                              n)),
                          Power(Power(NonfreeFactors(v, x), Times(p, FracPart(n))), CN1)),
                          Integrate(Times(ActivateTrig(u),
                              Power(NonfreeFactors(v, x), Times(n, p))), x),
                          x)),
                  And(FreeQ(List(a, c, d, n,
                      p), x), InertTrigQ(
                          FSymbol),
                      Not(IntegerQ(n)), IntegerQ(p)))),
          IIntegrate(4388,
              Integrate(
                  Times(u_DEFAULT,
                      Power(Times(a_DEFAULT,
                          Power(Times(b_DEFAULT, $(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                              p_)),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(v,
                          ActivateTrig(F(Plus(c, Times(d, x)))))),
                      Dist(
                          Times(Power(a, IntPart(n)),
                              Power(Times(a,
                                  Power(Times(b, v), p)), FracPart(n)),
                              Power(Power(Times(b, v), Times(p, FracPart(n))), CN1)),
                          Integrate(Times(ActivateTrig(u), Power(Times(b, v), Times(n, p))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, n,
                      p), x), InertTrigQ(
                          FSymbol),
                      Not(IntegerQ(n)), Not(IntegerQ(p))))),
          IIntegrate(4389, Integrate(u_, x_Symbol),
              Condition(
                  With(
                      List(Set(v,
                          FunctionOfTrig(u, x))),
                      Condition(
                          With(List(Set(d, FreeFactors(Tan(v), x))),
                              Dist(Times(d, Power(Coefficient(v, x, C1), CN1)),
                                  Subst(
                                      Integrate(
                                          SubstFor(Power(Plus(C1, Times(Sqr(d), Sqr(x))), CN1),
                                              Times(Tan(v), Power(d, CN1)), u, x),
                                          x),
                                      x, Times(Tan(v), Power(d, CN1))),
                                  x)),
                          And(Not(FalseQ(v)), FunctionOfQ(NonfreeFactors(Tan(v), x), u, x)))),
                  And(InverseFunctionFreeQ(u, x),
                      Not(MatchQ(u,
                          Condition(
                              Times(v_DEFAULT,
                                  Power(
                                      Times(c_DEFAULT, Power($($s("§tan"), w_), n_DEFAULT),
                                          Power($($s("§tan"), z_), n_DEFAULT)),
                                      p_DEFAULT)),
                              And(FreeQ(List(c,
                                  p), x), IntegerQ(
                                      n),
                                  LinearQ(w, x), EqQ(z, Times(C2, w))))))))),
          IIntegrate(4390,
              Integrate(Times(u_,
                  Power(Times(c_DEFAULT, $($s("§sin"), v_)), m_)), x_Symbol),
              Condition(
                  With(
                      List(
                          Set(w,
                              FunctionOfTrig(
                                  Times(
                                      u, Power(Sin(Times(C1D2, v)), Times(C2, m)), Power(Power(
                                          Times(c, Tan(Times(C1D2, v))), m), CN1)),
                                  x))),
                      Condition(
                          Dist(
                              Times(Power(Times(c, Sin(v)), m),
                                  Power(Times(c, Tan(
                                      Times(C1D2, v))), m),
                                  Power(Power(Sin(Times(C1D2, v)), Times(C2, m)), CN1)),
                              Integrate(
                                  Times(
                                      u, Power(Sin(Times(C1D2,
                                          v)), Times(C2,
                                              m)),
                                      Power(Power(Times(c, Tan(Times(C1D2, v))), m), CN1)),
                                  x),
                              x),
                          And(Not(FalseQ(w)), FunctionOfQ(NonfreeFactors(Tan(w), x),
                              Times(u, Power(Sin(Times(C1D2, v)), Times(C2, m)),
                                  Power(Power(Times(c, Tan(Times(C1D2, v))), m), CN1)),
                              x)))),
                  And(FreeQ(c, x), LinearQ(v, x), IntegerQ(Plus(m,
                      C1D2)), Not(
                          SumQ(u)),
                      InverseFunctionFreeQ(u, x)))),
          IIntegrate(4391,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              Times(b_DEFAULT,
                                  Power($($s("§sec"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                      n_DEFAULT)),
                              Times(a_DEFAULT,
                                  Power($($s("§tan"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                      n_DEFAULT))),
                          p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          ActivateTrig(u), Power(Sec(Plus(c, Times(d, x))), Times(n,
                              p)),
                          Power(Plus(b, Times(a, Power(Sin(Plus(c, Times(d, x))), n))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IntegersQ(n, p)))),
          IIntegrate(4392,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Power($($s("§cot"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              n_DEFAULT), a_DEFAULT),
                          Times(
                              Power($($s("§csc"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  n_DEFAULT),
                              b_DEFAULT)),
                          p_),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(ActivateTrig(u), Power(Csc(Plus(c, Times(d, x))), Times(n, p)), Power(
                          Plus(b, Times(a, Power(Cos(Plus(c, Times(d, x))), n))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IntegersQ(n, p)))),
          IIntegrate(4393,
              Integrate(
                  Times(u_,
                      Power(
                          Plus(
                              Times(
                                  a_, Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                      p_DEFAULT)),
                              Times(b_DEFAULT,
                                  Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ActivateTrig(
                          Times(u, Power(F(Plus(c, Times(d, x))), Times(n, p)),
                              Power(
                                  Plus(a, Times(b, Power(F(Plus(c, Times(d, x))), Subtract(q, p)))),
                                  n))),
                      x),
                  And(FreeQ(List(a, b, c, d, p,
                      q), x), InertTrigQ(
                          FSymbol),
                      IntegerQ(n), PosQ(Subtract(q, p))))),
          IIntegrate(4394,
              Integrate(
                  Times(u_,
                      Power(
                          Plus(
                              Times(
                                  a_,
                                  Power($(F_, Plus(d_DEFAULT, Times(e_DEFAULT, x_))), p_DEFAULT)),
                              Times(b_DEFAULT, Power($(F_, Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  q_DEFAULT)),
                              Times(c_DEFAULT,
                                  Power($(F_, Plus(d_DEFAULT, Times(e_DEFAULT, x_))), r_DEFAULT))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ActivateTrig(
                          Times(
                              u, Power(F(Plus(d, Times(e, x))), Times(n, p)), Power(
                                  Plus(
                                      a, Times(b, Power(F(Plus(d, Times(e, x))), Subtract(q, p))),
                                      Times(c, Power(F(Plus(d, Times(e, x))), Subtract(r, p)))),
                                  n))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p, q, r), x), InertTrigQ(FSymbol), IntegerQ(n),
                      PosQ(Subtract(q, p)), PosQ(Subtract(r, p))))),
          IIntegrate(4395,
              Integrate(
                  Times(u_,
                      Power(
                          Plus(a_,
                              Times(
                                  b_DEFAULT, Power($(F_, Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                      p_DEFAULT)),
                              Times(
                                  c_DEFAULT, Power($(F_, Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                      q_DEFAULT))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(ActivateTrig(Times(u, Power(F(Plus(d, Times(e, x))), Times(n, p)),
                  Power(Plus(b, Times(a, Power(Power(F(Plus(d, Times(e, x))), p), CN1)),
                      Times(c, Power(F(Plus(d, Times(e, x))), Subtract(q, p)))), n))),
                  x),
                  And(FreeQ(List(a, b, c, d, e, p,
                      q), x), InertTrigQ(
                          FSymbol),
                      IntegerQ(n), NegQ(p)))),
          IIntegrate(4396,
              Integrate(Times(u_DEFAULT,
                  Power(
                      Plus(Times($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                          Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                      n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(ActivateTrig(u),
                          Power(
                              Times(a,
                                  Power(Exp(Times(a, Plus(c, Times(d, x)), Power(b, CN1))), CN1)),
                              n)),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), EqQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(
              4397, Integrate(u_, x_Symbol), Condition(Integrate(TrigSimplify(
                  u), x), TrigSimplifyQ(
                      u))),
          IIntegrate(
              4398, Integrate(Times(u_DEFAULT,
                  Power(Times(a_, v_), p_)), x_Symbol),
              Condition(
                  With(
                      List(Set($s("uu"), ActivateTrig(
                          u)), Set($s("vv"),
                              ActivateTrig(v))),
                      Dist(
                          Times(
                              Power(a, IntPart(p)), Power(Times(a, $s("vv")), FracPart(p)), Power(
                                  Power($s("vv"), FracPart(p)), CN1)),
                          Integrate(Times($s("uu"), Power($s("vv"), p)), x), x)),
                  And(FreeQ(List(a, p), x), Not(IntegerQ(p)), Not(InertTrigFreeQ(v))))),
          IIntegrate(
              4399, Integrate(Times(u_DEFAULT,
                  Power(Power(v_, m_), p_)), x_Symbol),
              Condition(
                  With(
                      List(Set($s("uu"), ActivateTrig(u)), Set($s("vv"),
                          ActivateTrig(v))),
                      Dist(
                          Times(
                              Power(Power($s(
                                  "vv"), m), FracPart(p)),
                              Power(Power($s("vv"), Times(m, FracPart(p))), CN1)),
                          Integrate(Times($s("uu"), Power($s("vv"), Times(m, p))), x), x)),
                  And(FreeQ(List(m, p), x), Not(IntegerQ(p)), Not(InertTrigFreeQ(v))))),
          IIntegrate(4400, Integrate(
              Times(u_DEFAULT, Power(Times(Power(v_, m_DEFAULT), Power(w_, n_DEFAULT)), p_)),
              x_Symbol),
              Condition(
                  With(
                      List(Set($s("uu"), ActivateTrig(u)), Set($s("vv"), ActivateTrig(v)),
                          Set($s("ww"), ActivateTrig(w))),
                      Dist(
                          Times(Power(Times(Power($s("vv"), m), Power($s("ww"), n)), FracPart(p)),
                              Power(Times(Power($s("vv"), Times(m, FracPart(p))),
                                  Power($s("ww"), Times(n, FracPart(p)))), CN1)),
                          Integrate(Times($s("uu"), Power($s("vv"), Times(m, p)),
                              Power($s("ww"), Times(n, p))), x),
                          x)),
                  And(FreeQ(List(m, n, p), x), Not(IntegerQ(p)),
                      Or(Not(InertTrigFreeQ(v)), Not(InertTrigFreeQ(w)))))));
}
