package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions37 { 
  public static IAST RULES = List( 
ISetDelayed(505,RectifyCotangent(u_,a_,b_,r_,x_Symbol),
    If(And(MatchQ(Together(a),Times(d_DEFAULT,Complex(C0,c_))),MatchQ(Together(b),Times(d_DEFAULT,Complex(C0,c_)))),Module(list(Set(c,Times(a,Power(CI,CN1))),Set(d,Times(b,Power(CI,CN1))),e),If(LtQ(d,C0),RectifyTangent(u,Negate(a),Negate(b),Negate(r),x),CompoundExpression(Set(e,SmartDenominator(Together(Plus(c,Times(d,x))))),Set(c,Times(c,e)),Set(d,Times(d,e)),If(EvenQ(Denominator(NumericFactor(Together(u)))),Subtract(Times(CI,r,C1D4,Log(RemoveContent(Plus(Simplify(Plus(Sqr(Plus(c,e)),Sqr(d))),Times(CN1,Simplify(Subtract(Sqr(Plus(c,e)),Sqr(d))),Cos(Times(C2,u))),Times(Simplify(Times(C2,Plus(c,e),d)),Sin(Times(C2,u)))),x))),Times(CI,r,C1D4,Log(RemoveContent(Plus(Simplify(Plus(Sqr(Subtract(c,e)),Sqr(d))),Times(CN1,Simplify(Subtract(Sqr(Subtract(c,e)),Sqr(d))),Cos(Times(C2,u))),Times(Simplify(Times(C2,Subtract(c,e),d)),Sin(Times(C2,u)))),x)))),Subtract(Times(CI,r,C1D4,Log(RemoveContent(Plus(Simplify(Sqr(Plus(c,e))),Times(CN1,Simplify(Subtract(Sqr(Plus(c,e)),Sqr(d))),Sqr(Cos(u))),Times(Simplify(Times(C2,Plus(c,e),d)),Cos(u),Sin(u))),x))),Times(CI,r,C1D4,Log(RemoveContent(Plus(Simplify(Sqr(Subtract(c,e))),Times(CN1,Simplify(Subtract(Sqr(Subtract(c,e)),Sqr(d))),Sqr(Cos(u))),Times(Simplify(Times(C2,Subtract(c,e),d)),Cos(u),Sin(u))),x)))))))),If(LtQ(b,C0),RectifyCotangent(u,Negate(a),Negate(b),Negate(r),x),If(EvenQ(Denominator(NumericFactor(Together(u)))),Subtract(Times(CN1,r,SimplifyAntiderivative(u,x)),Times(r,ArcTan(Simplify(Times(Plus(Times(C2,a,b,Cos(Times(C2,u))),Times(Subtract(Plus(C1,Sqr(a)),Sqr(b)),Sin(Times(C2,u)))),Power(Plus(Sqr(a),Sqr(Plus(C1,b)),Times(CN1,Subtract(Plus(C1,Sqr(a)),Sqr(b)),Cos(Times(C2,u))),Times(C2,a,b,Sin(Times(C2,u)))),CN1)))))),Subtract(Times(CN1,r,SimplifyAntiderivative(u,x)),Times(r,ArcTan(ActivateTrig(Simplify(Times(Plus(Times(a,b),Times(CN1,C2,a,b,Sqr($($s("§sin"),u))),Times(Subtract(Plus(C1,Sqr(a)),Sqr(b)),$($s("§cos"),u),$($s("§sin"),u))),Power(Plus(Times(b,Plus(C1,b)),Times(Subtract(Plus(C1,Sqr(a)),Sqr(b)),Sqr($($s("§sin"),u))),Times(C2,a,b,$($s("§cos"),u),$($s("§sin"),u))),CN1))))))))))),
ISetDelayed(506,SmartNumerator(Power(u_,n_)),
    Condition(SmartDenominator(Power(u,Negate(n))),And(RationalQ(n),Less(n,C0)))),
ISetDelayed(507,SmartNumerator(Times(u_,v_)),
    Times(SmartNumerator(u),SmartNumerator(v))),
ISetDelayed(508,SmartNumerator(u_),
    Numerator(u)),
ISetDelayed(509,SmartDenominator(Power(u_,n_)),
    Condition(SmartNumerator(Power(u,Negate(n))),And(RationalQ(n),Less(n,C0)))),
ISetDelayed(510,SmartDenominator(Times(u_,v_)),
    Times(SmartDenominator(u),SmartDenominator(v))),
ISetDelayed(511,SmartDenominator(u_),
    Denominator(u)),
ISetDelayed(512,SubstFor(w_,v_,u_,x_),
    SimplifyIntegrand(Times(w,SubstFor(v,u,x)),x)),
ISetDelayed(513,SubstFor(v_,u_,x_),
    If(AtomQ(v),Subst(u,v,x),If(Not(InertTrigFreeQ(u)),SubstFor(v,ActivateTrig(u),x),If(NeQ(FreeFactors(v,x),C1),SubstFor(NonfreeFactors(v,x),u,Times(x,Power(FreeFactors(v,x),CN1))),Switch(Head(v),Sin,SubstForTrig(u,x,Sqrt(Subtract(C1,Sqr(x))),Part(v,C1),x),Cos,SubstForTrig(u,Sqrt(Subtract(C1,Sqr(x))),x,Part(v,C1),x),Tan,SubstForTrig(u,Times(x,Power(Plus(C1,Sqr(x)),CN1D2)),Power(Plus(C1,Sqr(x)),CN1D2),Part(v,C1),x),Cot,SubstForTrig(u,Power(Plus(C1,Sqr(x)),CN1D2),Times(x,Power(Plus(C1,Sqr(x)),CN1D2)),Part(v,C1),x),Sec,SubstForTrig(u,Power(Subtract(C1,Sqr(x)),CN1D2),Power(x,CN1),Part(v,C1),x),Csc,SubstForTrig(u,Power(x,CN1),Power(Subtract(C1,Sqr(x)),CN1D2),Part(v,C1),x),Sinh,SubstForHyperbolic(u,x,Sqrt(Plus(C1,Sqr(x))),Part(v,C1),x),Cosh,SubstForHyperbolic(u,Sqrt(Plus(CN1,Sqr(x))),x,Part(v,C1),x),Tanh,SubstForHyperbolic(u,Times(x,Power(Subtract(C1,Sqr(x)),CN1D2)),Power(Subtract(C1,Sqr(x)),CN1D2),Part(v,C1),x),Coth,SubstForHyperbolic(u,Power(Plus(CN1,Sqr(x)),CN1D2),Times(x,Power(Plus(CN1,Sqr(x)),CN1D2)),Part(v,C1),x),Sech,SubstForHyperbolic(u,Power(Plus(CN1,Sqr(x)),CN1D2),Power(x,CN1),Part(v,C1),x),Csch,SubstForHyperbolic(u,Power(x,CN1),Power(Plus(C1,Sqr(x)),CN1D2),Part(v,C1),x),$b(),SubstForAux(u,v,x)))))),
ISetDelayed(514,SubstForAux(u_,v_,x_),
    If(SameQ(u,v),x,If(AtomQ(u),If(And(PowerQ(v),FreeQ(Part(v,C2),x),EqQ(u,Part(v,C1))),Power(x,Simplify(Power(Part(v,C2),CN1))),u),If(And(PowerQ(u),FreeQ(Part(u,C2),x)),If(EqQ(Part(u,C1),v),Power(x,Part(u,C2)),If(And(PowerQ(v),FreeQ(Part(v,C2),x),EqQ(Part(u,C1),Part(v,C1))),Power(x,Simplify(Times(Part(u,C2),Power(Part(v,C2),CN1)))),Power(SubstForAux(Part(u,C1),v,x),Part(u,C2)))),If(And(ProductQ(u),NeQ(FreeFactors(u,x),C1)),Times(FreeFactors(u,x),SubstForAux(NonfreeFactors(u,x),v,x)),If(And(ProductQ(u),ProductQ(v)),SubstForAux(First(u),First(v),x),Map(Function(SubstForAux(Slot1,v,x)),u))))))),
ISetDelayed(515,SubstForTrig(u_,$p("§sin"),$p("§cos"),v_,x_),
    If(AtomQ(u),u,If(And(TrigQ(u),IntegerQuotientQ(Part(u,C1),v)),If(Or(SameQ(Part(u,C1),v),EqQ(Part(u,C1),v)),Switch(Head(u),Sin,$s("§sin"),Cos,$s("§cos"),Tan,Times($s("§sin"),Power($s("§cos"),CN1)),Cot,Times($s("§cos"),Power($s("§sin"),CN1)),Sec,Power($s("§cos"),CN1),Csc,Power($s("§sin"),CN1)),Map(Function(SubstForTrig(Slot1,$s("§sin"),$s("§cos"),v,x)),ReplaceAll(TrigExpand($(Head(u),Times(Simplify(Times(Part(u,C1),Power(v,CN1))),x))),Rule(x,v)))),If(And(ProductQ(u),SameQ(Head(Part(u,C1)),Cos),SameQ(Head(Part(u,C2)),Sin),EqQ(Part(u,C1,C1),Times(C1D2,v)),EqQ(Part(u,C2,C1),Times(C1D2,v))),Times(C1D2,$s("§sin"),SubstForTrig(Drop(u,C2),$s("§sin"),$s("§cos"),v,x)),Map(Function(SubstForTrig(Slot1,$s("§sin"),$s("§cos"),v,x)),u))))),
ISetDelayed(516,SubstForHyperbolic(u_,$p("§sinh"),$p("§cosh"),v_,x_),
    If(AtomQ(u),u,If(And(HyperbolicQ(u),IntegerQuotientQ(Part(u,C1),v)),If(Or(SameQ(Part(u,C1),v),EqQ(Part(u,C1),v)),Switch(Head(u),Sinh,$s("§sinh"),Cosh,$s("§cosh"),Tanh,Times($s("§sinh"),Power($s("§cosh"),CN1)),Coth,Times($s("§cosh"),Power($s("§sinh"),CN1)),Sech,Power($s("§cosh"),CN1),Csch,Power($s("§sinh"),CN1)),Map(Function(SubstForHyperbolic(Slot1,$s("§sinh"),$s("§cosh"),v,x)),ReplaceAll(TrigExpand($(Head(u),Times(Simplify(Times(Part(u,C1),Power(v,CN1))),x))),Rule(x,v)))),If(And(ProductQ(u),SameQ(Head(Part(u,C1)),Cosh),SameQ(Head(Part(u,C2)),Sinh),EqQ(Part(u,C1,C1),Times(C1D2,v)),EqQ(Part(u,C2,C1),Times(C1D2,v))),Times(C1D2,$s("§sinh"),SubstForHyperbolic(Drop(u,C2),$s("§sinh"),$s("§cosh"),v,x)),Map(Function(SubstForHyperbolic(Slot1,$s("§sinh"),$s("§cosh"),v,x)),u))))),
ISetDelayed(517,SubstForFractionalPowerOfLinear(u_,x_Symbol),
    With(list(Set($s("lst"),FractionalPowerOfLinear(u,C1,False,x))),If(Or(AtomQ($s("lst")),FalseQ(Part($s("lst"),C2))),False,With(list(Set(n,Part($s("lst"),C1)),Set(a,Coefficient(Part($s("lst"),C2),x,C0)),Set(b,Coefficient(Part($s("lst"),C2),x,C1))),With(list(Set($s("tmp"),Simplify(Times(Power(x,Subtract(n,C1)),SubstForFractionalPower(u,Part($s("lst"),C2),n,Plus(Times(CN1,a,Power(b,CN1)),Times(Power(x,n),Power(b,CN1))),x))))),List(NonfreeFactors($s("tmp"),x),n,Part($s("lst"),C2),Times(FreeFactors($s("tmp"),x),Power(b,CN1)))))))),
ISetDelayed(518,FractionalPowerOfLinear(u_,n_,v_,x_),
    If(Or(AtomQ(u),FreeQ(u,x)),list(n,v),If(CalculusQ(u),False,If(And(FractionalPowerQ(u),LinearQ(Part(u,C1),x),Or(FalseQ(v),EqQ(Part(u,C1),v))),list(LCM(Denominator(Part(u,C2)),n),Part(u,C1)),Catch(Module(list(Set($s("lst"),list(n,v))),CompoundExpression(Scan(Function(If(AtomQ(Set($s("lst"),FractionalPowerOfLinear(Slot1,Part($s("lst"),C1),Part($s("lst"),C2),x))),Throw(False))),u),$s("lst"))))))))
  );
}
