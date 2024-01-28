package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions12 { 
  public static IAST RULES = List( 
ISetDelayed(123,GeneralizedBinomialMatchQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(GeneralizedBinomialMatchQ(Slot1,x)),Throw(False))),u),True)),MatchQ(u,Condition(Plus(Times(a_DEFAULT,Power(x,q_DEFAULT)),Times(b_DEFAULT,Power(x,n_DEFAULT))),FreeQ(List(a,b,n,q),x))))),
ISetDelayed(124,TrinomialMatchQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(TrinomialMatchQ(Slot1,x)),Throw(False))),u),True)),MatchQ(u,Condition(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x,n_DEFAULT)),Times(c_DEFAULT,Power(x,j_DEFAULT))),And(FreeQ(List(a,b,c,n),x),EqQ(Subtract(j,Times(C2,n)),C0)))))),
ISetDelayed(125,GeneralizedTrinomialMatchQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(GeneralizedTrinomialMatchQ(Slot1,x)),Throw(False))),u),True)),MatchQ(u,Condition(Plus(Times(a_DEFAULT,Power(x,q_DEFAULT)),Times(b_DEFAULT,Power(x,n_DEFAULT)),Times(c_DEFAULT,Power(x,r_DEFAULT))),And(FreeQ(List(a,b,c,n,q),x),EqQ(Subtract(r,Subtract(Times(C2,n),q)),C0)))))),
ISetDelayed(126,QuotientOfLinearsMatchQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(QuotientOfLinearsMatchQ(Slot1,x)),Throw(False))),u),True)),MatchQ(u,Condition(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),CN1)),FreeQ(List(a,b,c,d,e),x))))),
ISetDelayed(127,PolynomialTermQ(u_,x_Symbol),
    Or(FreeQ(u,x),MatchQ(u,Condition(Times(a_DEFAULT,Power(x,n_DEFAULT)),And(FreeQ(a,x),IntegerQ(n),Greater(n,C0)))))),
ISetDelayed(128,PolynomialTerms(u_,x_Symbol),
    Map(Function(If(PolynomialTermQ(Slot1,x),Slot1,C0)),u)),
ISetDelayed(129,NonpolynomialTerms(u_,x_Symbol),
    Map(Function(If(PolynomialTermQ(Slot1,x),C0,Slot1)),u)),
ISetDelayed(130,PseudoBinomialQ(u_,x_Symbol),
    ListQ(PseudoBinomialParts(u,x))),
ISetDelayed(131,PseudoBinomialPairQ(u_,v_,x_Symbol),
    With(list(Set($s("lst1"),PseudoBinomialParts(u,x))),If(AtomQ($s("lst1")),False,With(list(Set($s("lst2"),PseudoBinomialParts(v,x))),If(AtomQ($s("lst2")),False,SameQ(Drop($s("lst1"),C2),Drop($s("lst2"),C2))))))),
ISetDelayed(132,NormalizePseudoBinomial(u_,x_Symbol),
    With(list(Set($s("lst"),PseudoBinomialParts(u,x))),Plus(Part($s("lst"),C1),Times(Part($s("lst"),C2),Power(Plus(Part($s("lst"),C3),Times(Part($s("lst"),C4),x)),Part($s("lst"),C5))))))
  );
}
