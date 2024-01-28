package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions11 { 
  public static IAST RULES = List( 
ISetDelayed(112,LinearQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(PolyQ(Slot1,x,C1),Null,Throw(False))),u),True)),PolyQ(u,x,C1))),
ISetDelayed(113,QuadraticQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(QuadraticQ(Slot1,x)),Throw(False))),u),True)),And(PolyQ(u,x,C2),Not(And(SameQ(Coefficient(u,x,C0),C0),SameQ(Coefficient(u,x,C1),C0)))))),
ISetDelayed(114,LinearPairQ(u_,v_,x_Symbol),
    And(LinearQ(u,x),LinearQ(v,x),NeQ(u,x),EqQ(Subtract(Times(Coefficient(u,x,C0),Coefficient(v,x,C1)),Times(Coefficient(u,x,C1),Coefficient(v,x,C0))),C0))),
ISetDelayed(115,MonomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(MonomialQ(Slot1,x),Null,Throw(False))),u),True)),MatchQ(u,Condition(Times(a_DEFAULT,Power(x,n_DEFAULT)),FreeQ(list(a,n),x))))),
ISetDelayed(116,MinimumMonomialExponent(u_,x_Symbol),
    Module(list(Set(n,MonomialExponent(First(u),x))),CompoundExpression(Scan(Function(If(PosQ(Subtract(n,MonomialExponent(Slot1,x))),Set(n,MonomialExponent(Slot1,x)))),u),n))),
ISetDelayed(117,MonomialExponent(a_,x_Symbol),
    Condition(C0,FreeQ(a,x))),
ISetDelayed(118,MonomialExponent(Times(a_DEFAULT,Power(x_,n_DEFAULT)),x_Symbol),
    Condition(n,FreeQ(list(a,n),x))),
ISetDelayed(119,LinearMatchQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(LinearMatchQ(Slot1,x)),Throw(False))),u),True)),MatchQ(u,Condition(Plus(a_DEFAULT,Times(b_DEFAULT,x)),FreeQ(list(a,b),x))))),
ISetDelayed(120,QuadraticMatchQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(QuadraticMatchQ(Slot1,x)),Throw(False))),u),True)),Or(MatchQ(u,Condition(Plus(a_DEFAULT,Times(b_DEFAULT,x),Times(c_DEFAULT,Sqr(x))),FreeQ(list(a,b,c),x))),MatchQ(u,Condition(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(x))),FreeQ(list(a,c),x)))))),
ISetDelayed(121,CubicMatchQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(CubicMatchQ(Slot1,x)),Throw(False))),u),True)),Or(MatchQ(u,Condition(Plus(a_DEFAULT,Times(b_DEFAULT,x),Times(c_DEFAULT,Sqr(x)),Times(d_DEFAULT,Power(x,C3))),FreeQ(List(a,b,c,d),x))),MatchQ(u,Condition(Plus(a_DEFAULT,Times(b_DEFAULT,x),Times(d_DEFAULT,Power(x,C3))),FreeQ(list(a,b,d),x))),MatchQ(u,Condition(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(x)),Times(d_DEFAULT,Power(x,C3))),FreeQ(list(a,c,d),x))),MatchQ(u,Condition(Plus(a_DEFAULT,Times(d_DEFAULT,Power(x,C3))),FreeQ(list(a,d),x)))))),
ISetDelayed(122,BinomialMatchQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(BinomialMatchQ(Slot1,x)),Throw(False))),u),True)),MatchQ(u,Condition(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x,n_DEFAULT))),FreeQ(list(a,b,n),x)))))
  );
}
