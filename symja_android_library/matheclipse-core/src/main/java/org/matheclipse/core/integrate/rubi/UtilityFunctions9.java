package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions9 { 
  public static IAST RULES = List( 
ISetDelayed(85,NumericFactor(u_),
    If(NumberQ(u),If(EqQ(Im(u),C0),u,If(EqQ(Re(u),C0),Im(u),C1)),If(PowerQ(u),If(And(RationalQ(Part(u,C1)),FractionQ(Part(u,C2))),If(Greater(Part(u,C2),C0),Power(Denominator(Part(u,C1)),CN1),Power(Denominator(Power(Part(u,C1),CN1)),CN1)),C1),If(ProductQ(u),Map($rubi("NumericFactor"),u),If(SumQ(u),If(Less(LeafCount(u),ZZ(50L)),$(Function(If(SumQ(Slot1),C1,NumericFactor(Slot1))),ContentFactor(u)),With(list(Set(m,NumericFactor(First(u))),Set(n,NumericFactor(Rest(u)))),If(And(Less(m,C0),Less(n,C0)),Negate(GCD(Negate(m),Negate(n))),GCD(m,n)))),C1))))),
ISetDelayed(86,NonnumericFactors(u_),
    If(NumberQ(u),If(EqQ(Im(u),C0),C1,If(EqQ(Re(u),C0),CI,u)),If(PowerQ(u),If(And(RationalQ(Part(u,C1)),FractionQ(Part(u,C2))),Times(u,Power(NumericFactor(u),CN1)),u),If(ProductQ(u),Map($rubi("NonnumericFactors"),u),If(SumQ(u),If(Less(LeafCount(u),ZZ(50L)),$(Function(If(SumQ(Slot1),u,NonnumericFactors(Slot1))),ContentFactor(u)),With(list(Set(n,NumericFactor(u))),Map(Function(Times(Slot1,Power(n,CN1))),u))),u))))),
ISetDelayed(87,RemoveContent(u_,x_Symbol),
    With(list(Set(v,NonfreeFactors(u,x))),With(list(Set(w,Together(v))),If(EqQ(FreeFactors(w,x),C1),RemoveContentAux(v,x),RemoveContentAux(NonfreeFactors(w,x),x))))),
ISetDelayed(88,RemoveContentAux(Plus(Times(Power(a_,m_),u_DEFAULT),Times(b_,v_DEFAULT)),x_Symbol),
    Condition(If(Greater(m,C1),RemoveContentAux(Subtract(Times(Power(a,Subtract(m,C1)),u),v),x),RemoveContentAux(Subtract(u,Times(Power(a,Subtract(C1,m)),v)),x)),And(IntegersQ(a,b),Equal(Plus(a,b),C0),RationalQ(m)))),
ISetDelayed(89,RemoveContentAux(Plus(Times(Power(a_,m_DEFAULT),u_DEFAULT),Times(Power(a_,n_DEFAULT),v_DEFAULT)),x_Symbol),
    Condition(RemoveContentAux(Plus(u,Times(Power(a,Subtract(n,m)),v)),x),And(FreeQ(a,x),RationalQ(m,n),GreaterEqual(Subtract(n,m),C0)))),
ISetDelayed(90,RemoveContentAux(Plus(Times(Power(a_,m_DEFAULT),u_DEFAULT),Times(Power(a_,n_DEFAULT),v_DEFAULT),Times(Power(a_,p_DEFAULT),w_DEFAULT)),x_Symbol),
    Condition(RemoveContentAux(Plus(u,Times(Power(a,Subtract(n,m)),v),Times(Power(a,Subtract(p,m)),w)),x),And(FreeQ(a,x),RationalQ(m,n,p),GreaterEqual(Subtract(n,m),C0),GreaterEqual(Subtract(p,m),C0)))),
ISetDelayed(91,RemoveContentAux(u_,x_Symbol),
    If(And(SumQ(u),NegQ(First(u))),Negate(u),u)),
ISetDelayed(92,FreeFactors(u_,x_),
    If(ProductQ(u),Map(Function(If(FreeQ(Slot1,x),Slot1,C1)),u),If(FreeQ(u,x),u,C1))),
ISetDelayed(93,NonfreeFactors(u_,x_),
    If(ProductQ(u),Map(Function(If(FreeQ(Slot1,x),C1,Slot1)),u),If(FreeQ(u,x),C1,u))),
ISetDelayed(94,FreeTerms(u_,x_),
    If(SumQ(u),Map(Function(If(FreeQ(Slot1,x),Slot1,C0)),u),If(FreeQ(u,x),u,C0))),
ISetDelayed(95,NonfreeTerms(u_,x_),
    If(SumQ(u),Map(Function(If(FreeQ(Slot1,x),C0,Slot1)),u),If(FreeQ(u,x),C0,u))),
ISetDelayed(96,Expon($p("expr"),$p("form")),
    Exponent(Together($s("expr")),$s("form"))),
ISetDelayed(97,Expon($p("expr"),$p("form"),h_),
    Exponent(Together($s("expr")),$s("form"),h)),
ISetDelayed(98,Coeff($p("expr"),$p("form")),
    Coefficient(Together($s("expr")),$s("form")))
  );
}
