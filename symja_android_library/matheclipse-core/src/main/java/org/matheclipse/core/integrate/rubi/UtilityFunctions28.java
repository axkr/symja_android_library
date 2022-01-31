package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$b;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Drop;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.G_;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.LCM;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Switch;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.TrigExpand;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.g_;
import static org.matheclipse.core.expression.F.h_;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.s_;
import static org.matheclipse.core.expression.F.t_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ArcCot;
import static org.matheclipse.core.expression.S.ArcCoth;
import static org.matheclipse.core.expression.S.ArcTan;
import static org.matheclipse.core.expression.S.ArcTanh;
import static org.matheclipse.core.expression.S.Cos;
import static org.matheclipse.core.expression.S.Cosh;
import static org.matheclipse.core.expression.S.Cot;
import static org.matheclipse.core.expression.S.Coth;
import static org.matheclipse.core.expression.S.Csc;
import static org.matheclipse.core.expression.S.Csch;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.GSymbol;
import static org.matheclipse.core.expression.S.Sec;
import static org.matheclipse.core.expression.S.Sech;
import static org.matheclipse.core.expression.S.Sin;
import static org.matheclipse.core.expression.S.Sinh;
import static org.matheclipse.core.expression.S.Tan;
import static org.matheclipse.core.expression.S.Tanh;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ActivateTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CalculusQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FalseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionalPowerOfLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionalPowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HyperbolicQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InertReciprocalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InertTrigFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InertTrigQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegerQuotientQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionOfLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NonfreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SmartDenominator;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SmartNumerator;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstFor;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstForAux;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstForFractionalPower;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstForFractionalPowerOfLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstForHyperbolic;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstForTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TryPureTanSubst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TryPureTanhSubst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TryTanhSubst;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions28 {
  public static IAST RULES = List(
      ISetDelayed(494, SmartDenominator(Power(u_, n_)),
          Condition(SmartNumerator(Power(u, Negate(n))), And(RationalQ(n), Less(n, C0)))),
      ISetDelayed(495, SmartDenominator(Times(u_, v_)),
          Times(SmartDenominator(u), SmartDenominator(v))),
      ISetDelayed(496, SmartDenominator(u_), Denominator(u)),
      ISetDelayed(497, SubstFor(w_, v_, u_, x_), SimplifyIntegrand(Times(w, SubstFor(v, u, x)), x)),
      ISetDelayed(498, SubstFor(v_, u_, x_),
          If(AtomQ(v), Subst(u, v, x),
              If(Not(InertTrigFreeQ(u)), SubstFor(v, ActivateTrig(u), x),
                  If(NeQ(FreeFactors(v, x), C1),
                      SubstFor(NonfreeFactors(v, x), u, Times(x, Power(FreeFactors(v, x), CN1))),
                      Switch(Head(v), Sin,
                          SubstForTrig(u, x, Sqrt(Subtract(C1, Sqr(x))), Part(v, C1), x), Cos,
                          SubstForTrig(u, Sqrt(Subtract(C1, Sqr(x))), x, Part(v, C1), x), Tan,
                          SubstForTrig(u, Times(x, Power(Plus(C1, Sqr(x)), CN1D2)),
                              Power(Plus(C1, Sqr(x)), CN1D2), Part(v, C1), x),
                          Cot,
                          SubstForTrig(u, Power(Plus(C1, Sqr(x)), CN1D2),
                              Times(x, Power(Plus(C1, Sqr(x)), CN1D2)), Part(v, C1), x),
                          Sec,
                          SubstForTrig(
                              u, Power(Subtract(C1, Sqr(x)), CN1D2), Power(x, CN1), Part(v, C1), x),
                          Csc,
                          SubstForTrig(u, Power(x, CN1), Power(Subtract(C1, Sqr(x)), CN1D2),
                              Part(v, C1), x),
                          Sinh, SubstForHyperbolic(u, x, Sqrt(Plus(C1, Sqr(x))), Part(v, C1), x),
                          Cosh, SubstForHyperbolic(u, Sqrt(Plus(CN1, Sqr(x))), x, Part(v, C1), x),
                          Tanh,
                          SubstForHyperbolic(u, Times(x, Power(Subtract(C1, Sqr(x)), CN1D2)),
                              Power(Subtract(C1, Sqr(x)), CN1D2), Part(v, C1), x),
                          Coth,
                          SubstForHyperbolic(u, Power(Plus(CN1, Sqr(x)), CN1D2),
                              Times(x, Power(Plus(CN1, Sqr(x)), CN1D2)), Part(v, C1), x),
                          Sech,
                          SubstForHyperbolic(
                              u, Power(Plus(CN1, Sqr(x)), CN1D2), Power(x, CN1), Part(v, C1), x),
                          Csch,
                          SubstForHyperbolic(u, Power(x, CN1), Power(Plus(C1, Sqr(x)), CN1D2),
                              Part(v, C1), x),
                          $b(), SubstForAux(u, v, x)))))),
      ISetDelayed(499, SubstForAux(u_, v_, x_),
          If(SameQ(u, v), x,
              If(AtomQ(u),
                  If(And(PowerQ(v), FreeQ(Part(v, C2), x), EqQ(u, Part(v, C1))),
                      Power(x, Simplify(Power(Part(v, C2), CN1))), u),
                  If(And(PowerQ(u), FreeQ(Part(u, C2), x)),
                      If(EqQ(Part(u, C1), v), Power(x, Part(u, C2)),
                          If(And(PowerQ(v), FreeQ(Part(v, C2), x), EqQ(Part(u, C1), Part(v, C1))),
                              Power(x, Simplify(Times(Part(u, C2), Power(Part(v, C2), CN1)))),
                              Power(SubstForAux(Part(u, C1), v, x), Part(u, C2)))),
                      If(And(ProductQ(u), NeQ(FreeFactors(u, x), C1)),
                          Times(FreeFactors(u, x), SubstForAux(NonfreeFactors(u, x), v, x)),
                          If(And(ProductQ(u), ProductQ(v)), SubstForAux(First(u), First(v), x),
                              Map(Function(SubstForAux(Slot1, v, x)), u))))))),
      ISetDelayed(
          500, SubstForTrig(u_, $p("§sin"), $p(
              "§cos"), v_, x_),
          If(AtomQ(u), u, If(
              And(TrigQ(u), IntegerQuotientQ(Part(u, C1),
                  v)),
              If(Or(SameQ(Part(u, C1), v), EqQ(Part(u, C1), v)),
                  Switch(Head(u), Sin, $s("§sin"), Cos, $s("§cos"), Tan,
                      Times($s("§sin"), Power($s("§cos"), CN1)), Cot,
                      Times($s("§cos"), Power($s("§sin"), CN1)), Sec, Power($s("§cos"), CN1), Csc,
                      Power($s("§sin"), CN1)),
                  Map(Function(SubstForTrig(Slot1, $s("§sin"), $s("§cos"), v, x)),
                      ReplaceAll(
                          TrigExpand(
                              $(Head(u), Times(Simplify(Times(Part(u, C1), Power(v, CN1))), x))),
                          Rule(x, v)))),
              If(And(ProductQ(u), SameQ(Head(Part(u, C1)), Cos), SameQ(Head(Part(u, C2)), Sin),
                  EqQ(Part(u, C1, C1), Times(C1D2, v)), EqQ(Part(u, C2, C1), Times(C1D2, v))),
                  Times(C1D2, $s("§sin"), SubstForTrig(Drop(u, C2), $s("§sin"), $s("§cos"), v,
                      x)),
                  Map(Function(SubstForTrig(Slot1, $s("§sin"), $s("§cos"), v, x)), u))))),
      ISetDelayed(
          501, SubstForHyperbolic(u_, $p("§sinh"), $p(
              "§cosh"), v_, x_),
          If(AtomQ(u), u,
              If(And(HyperbolicQ(u), IntegerQuotientQ(Part(u, C1),
                  v)), If(
                      Or(SameQ(Part(u,
                          C1), v), EqQ(Part(u, C1),
                              v)),
                      Switch(
                          Head(u), Sinh, $s("§sinh"), Cosh, $s("§cosh"), Tanh, Times($s("§sinh"),
                              Power($s("§cosh"), CN1)),
                          Coth, Times($s("§cosh"), Power($s("§sinh"), CN1)), Sech,
                          Power($s("§cosh"), CN1), Csch, Power($s("§sinh"), CN1)),
                      Map(Function(SubstForHyperbolic(Slot1, $s("§sinh"), $s("§cosh"), v, x)),
                          ReplaceAll(
                              TrigExpand($(Head(u),
                                  Times(Simplify(Times(Part(u, C1), Power(v, CN1))), x))),
                              Rule(x, v)))),
                  If(And(ProductQ(u), SameQ(Head(Part(u, C1)), Cosh),
                      SameQ(Head(Part(u, C2)), Sinh), EqQ(Part(u, C1, C1), Times(C1D2, v)),
                      EqQ(Part(u, C2, C1), Times(C1D2, v))),
                      Times(
                          C1D2, $s("§sinh"), SubstForHyperbolic(Drop(u, C2), $s("§sinh"),
                              $s("§cosh"), v, x)),
                      Map(Function(SubstForHyperbolic(Slot1, $s("§sinh"), $s("§cosh"), v, x)),
                          u))))),
      ISetDelayed(502, SubstForFractionalPowerOfLinear(u_, x_Symbol),
          With(List(Set($s("lst"), FractionalPowerOfLinear(u, C1, False, x))),
              If(Or(AtomQ($s("lst")), FalseQ(Part($s("lst"), C2))), False, With(
                  List(Set(n, Part($s("lst"), C1)), Set(a, Coefficient(Part($s("lst"), C2), x, C0)),
                      Set(b, Coefficient(Part($s("lst"), C2), x, C1))),
                  With(
                      List(Set($s("tmp"),
                          Simplify(Times(Power(x, Subtract(n, C1)),
                              SubstForFractionalPower(u, Part($s("lst"), C2), n,
                                  Plus(Times(CN1, a, Power(b, CN1)),
                                      Times(Power(x, n), Power(b, CN1))),
                                  x))))),
                      List(NonfreeFactors($s("tmp"), x), n, Part($s("lst"), C2),
                          Times(FreeFactors($s("tmp"), x), Power(b, CN1)))))))),
      ISetDelayed(
          503, FractionalPowerOfLinear(u_, n_, v_, x_), If(
              Or(AtomQ(u), FreeQ(u,
                  x)),
              List(n, v),
              If(CalculusQ(u), False,
                  If(And(FractionalPowerQ(u), LinearQ(Part(u, C1), x),
                      Or(FalseQ(v), EqQ(Part(u, C1), v))),
                      List(LCM(Denominator(Part(u, C2)), n), Part(u, C1)),
                      Catch(
                          Module(List(Set($s("lst"), List(n, v))),
                              CompoundExpression(
                                  Scan(Function(If(AtomQ(Set($s("lst"),
                                      FractionalPowerOfLinear(Slot1, Part($s("lst"), C1),
                                          Part($s("lst"), C2), x))),
                                      Throw(False))), u),
                                  $s("lst")))))))),
      ISetDelayed(504, InverseFunctionOfLinear(u_, x_Symbol), If(
          Or(AtomQ(u), CalculusQ(u), FreeQ(u, x)), False,
          If(And(InverseFunctionQ(u), LinearQ(Part(u, C1), x)), u, Module(List($s("tmp")),
              Catch(CompoundExpression(
                  Scan(Function(If(Not(AtomQ(Set($s("tmp"), InverseFunctionOfLinear(Slot1, x)))),
                      Throw($s("tmp")))), u),
                  False)))))),
      ISetDelayed(505, TryPureTanSubst(u_, x_Symbol),
          Not(MatchQ(u,
              Condition($(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, $(G_, v_))))),
                  And(FreeQ(List(a, b, c), x),
                      MemberQ(List(ArcTan, ArcCot, ArcTanh, ArcCoth), FSymbol),
                      MemberQ(List(Tan, Cot, Tanh, Coth), GSymbol), LinearQ(v, x)))))),
      ISetDelayed(506, TryTanhSubst(u_, x_Symbol),
          And(FalseQ(FunctionOfLinear(u, x)),
              Not(MatchQ(u,
                  Condition(Times(r_DEFAULT, Power(Plus(s_, t_), n_DEFAULT)),
                      And(IntegerQ(n), Greater(n, C0))))),
              Not(MatchQ(u, Log(v_))),
              Not(MatchQ(u,
                  Condition(Power(Plus(a_, Times(b_DEFAULT, Power($(f_, x), n_))), CN1),
                      And(MemberQ(List(Sinh, Cosh, Sech, Csch), f), IntegerQ(n), Greater(n, C2))))),
              Not(MatchQ(u,
                  Condition(Times($(f_, Times(m_DEFAULT, x)), $(g_, Times(n_DEFAULT, x))),
                      And(IntegersQ(m, n), MemberQ(List(Sinh, Cosh, Sech, Csch), f),
                          MemberQ(List(Sinh, Cosh, Sech, Csch), g))))),
              Not(MatchQ(u,
                  Condition(Times(r_DEFAULT, Power(Times(a_DEFAULT, Power(s_, m_)), p_)),
                      And(FreeQ(List(a, m, p), x),
                          Not(And(SameQ(m, C2), Or(SameQ(s, Sech(x)), SameQ(s, Csch(x))))))))),
              SameQ(u, ExpandIntegrand(u, x)))),
      ISetDelayed(507, TryPureTanhSubst(u_, x_Symbol),
          And(Not(MatchQ(u, Log(v_))),
              Not(MatchQ(u, Condition(ArcTanh(Times(a_DEFAULT, Tanh(v_))), FreeQ(a, x)))),
              Not(MatchQ(u, Condition(ArcTanh(Times(a_DEFAULT, Coth(v_))), FreeQ(a, x)))),
              Not(MatchQ(u, Condition(ArcCoth(Times(a_DEFAULT, Tanh(v_))), FreeQ(a, x)))),
              Not(MatchQ(u, Condition(ArcCoth(Times(a_DEFAULT, Coth(v_))), FreeQ(a, x)))),
              SameQ(u, ExpandIntegrand(u, x)))),
      ISetDelayed(508, InertTrigQ(f_),
          MemberQ(List($s("§sin"), $s("§cos"), $s("§tan"), $s("§cot"), $s("§sec"), $s("§csc")), f)),
      ISetDelayed(509, InertTrigQ(f_, g_),
          If(SameQ(f, g), InertTrigQ(f), Or(InertReciprocalQ(f, g), InertReciprocalQ(g, f)))),
      ISetDelayed(510, InertTrigQ(f_, g_, h_), And(InertTrigQ(f, g), InertTrigQ(g, h))),
      ISetDelayed(511, InertReciprocalQ(f_, g_),
          Or(And(SameQ(f, $s("§sin")), SameQ(g, $s("§csc"))),
              And(SameQ(f, $s("§cos")), SameQ(g, $s("§sec"))),
              And(SameQ(f, $s("§tan")), SameQ(g, $s("§cot"))))),
      ISetDelayed(512, InertTrigFreeQ(u_), And(FreeQ(u, $s("§sin")), FreeQ(u, $s("§cos")),
          FreeQ(u, $s("§tan")), FreeQ(u, $s("§cot")), FreeQ(u, $s("§sec")), FreeQ(u, $s("§csc")))));
}
