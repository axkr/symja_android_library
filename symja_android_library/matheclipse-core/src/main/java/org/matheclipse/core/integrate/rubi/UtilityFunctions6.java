package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions6 { 
  public static IAST RULES = List( 
ISetDelayed(92,RemoveContentAux(Plus(Times(Power(a_,m_),u_DEFAULT),Times(b_,v_DEFAULT)),x_Symbol),
    Condition(If(Greater(m,C1),RemoveContentAux(Subtract(Times(Power(a,Subtract(m,C1)),u),v),x),RemoveContentAux(Subtract(u,Times(Power(a,Subtract(C1,m)),v)),x)),And(IntegersQ(a,b),Equal(Plus(a,b),C0),RationalQ(m)))),
ISetDelayed(93,RemoveContentAux(Plus(Times(Power(a_,m_DEFAULT),u_DEFAULT),Times(Power(a_,n_DEFAULT),v_DEFAULT)),x_Symbol),
    Condition(RemoveContentAux(Plus(u,Times(Power(a,Subtract(n,m)),v)),x),And(FreeQ(a,x),RationalQ(m,n),GreaterEqual(Subtract(n,m),C0)))),
ISetDelayed(94,RemoveContentAux(Plus(Times(Power(a_,m_DEFAULT),u_DEFAULT),Times(Power(a_,n_DEFAULT),v_DEFAULT),Times(Power(a_,p_DEFAULT),w_DEFAULT)),x_Symbol),
    Condition(RemoveContentAux(Plus(u,Times(Power(a,Subtract(n,m)),v),Times(Power(a,Subtract(p,m)),w)),x),And(FreeQ(a,x),RationalQ(m,n,p),GreaterEqual(Subtract(n,m),C0),GreaterEqual(Subtract(p,m),C0)))),
ISetDelayed(95,RemoveContentAux(u_,x_Symbol),
    If(And(SumQ(u),NegQ(First(u))),Negate(u),u)),
ISetDelayed(96,FreeFactors(u_,x_),
    If(ProductQ(u),Map(Function(If(FreeQ(Slot1,x),Slot1,C1)),u),If(FreeQ(u,x),u,C1))),
ISetDelayed(97,NonfreeFactors(u_,x_),
    If(ProductQ(u),Map(Function(If(FreeQ(Slot1,x),C1,Slot1)),u),If(FreeQ(u,x),C1,u))),
ISetDelayed(98,FreeTerms(u_,x_),
    If(SumQ(u),Map(Function(If(FreeQ(Slot1,x),Slot1,C0)),u),If(FreeQ(u,x),u,C0))),
ISetDelayed(99,NonfreeTerms(u_,x_),
    If(SumQ(u),Map(Function(If(FreeQ(Slot1,x),C0,Slot1)),u),If(FreeQ(u,x),C0,u))),
ISetDelayed(100,Expon($p("expr"),$p("form")),
    Exponent(Together($s("expr")),$s("form"))),
ISetDelayed(101,Expon($p("expr"),$p("form"),h_),
    Exponent(Together($s("expr")),$s("form"),h)),
ISetDelayed(102,Coeff($p("expr"),$p("form")),
    Coefficient(Together($s("expr")),$s("form"))),
ISetDelayed(103,Coeff($p("expr"),$p("form"),n_),
    With(List(Set($s("coef1"),Coefficient($s("expr"),$s("form"),n)),Set($s("coef2"),Coefficient(Together($s("expr")),$s("form"),n))),If(SameQ(Simplify(Subtract($s("coef1"),$s("coef2"))),C0),$s("coef1"),$s("coef2")))),
ISetDelayed(104,LeadTerm(u_),
    If(SumQ(u),First(u),u)),
ISetDelayed(105,RemainingTerms(u_),
    If(SumQ(u),Rest(u),C0)),
ISetDelayed(106,LeadFactor(u_),
    If(ProductQ(u),LeadFactor(First(u)),If(And(ComplexNumberQ(u),SameQ(Re(u),C0)),If(SameQ(Im(u),C1),u,LeadFactor(Im(u))),u))),
ISetDelayed(107,RemainingFactors(u_),
    If(ProductQ(u),Times(RemainingFactors(First(u)),Rest(u)),If(And(ComplexNumberQ(u),SameQ(Re(u),C0)),If(SameQ(Im(u),C1),C1,Times(CI,RemainingFactors(Im(u)))),C1))),
ISetDelayed(108,LeadBase(u_),
    With(List(Set(v,LeadFactor(u))),If(PowerQ(v),Part(v,C1),v))),
ISetDelayed(109,LeadDegree(u_),
    With(List(Set(v,LeadFactor(u))),If(PowerQ(v),Part(v,C2),C1))),
ISetDelayed(110,$($s("§numer"),Power($p(m, Integer),$p(n,Rational))),
    Condition(C1,Less(n,C0))),
ISetDelayed(111,$($s("§numer"),Times(u_,v_)),
    Times($($s("§numer"),u),$($s("§numer"),v))),
ISetDelayed(112,$($s("§numer"),u_),
    Numerator(u)),
ISetDelayed(113,$($s("§denom"),Power($p(m, Integer),$p(n,Rational))),
    Condition(Power(m,Negate(n)),Less(n,C0))),
ISetDelayed(114,$($s("§denom"),Times(u_,v_)),
    Times($($s("§denom"),u),$($s("§denom"),v))),
ISetDelayed(115,$($s("§denom"),u_),
    Denominator(u)),
ISetDelayed(116,LinearQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(PolyQ(Slot1,x,C1),Null,Throw(False))),u),True)),PolyQ(u,x,C1)))
  );
}
