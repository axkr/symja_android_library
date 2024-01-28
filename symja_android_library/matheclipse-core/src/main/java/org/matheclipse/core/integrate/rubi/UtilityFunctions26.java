package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions26 { 
  public static IAST RULES = List( 
ISetDelayed(342,ExpandIntegrand(Times(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,m_DEFAULT)),Times(e_DEFAULT,Power(u_,p_)),Times(f_DEFAULT,Power(u_,q_))),Power(Plus(a_,Times(b_DEFAULT,Power(u_,n_))),CN1)),x_Symbol),
    Condition(With(list(Set(r,Numerator(Rt(Times(CN1,a,Power(b,CN1)),n))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,CN1)),n)))),Module(list(k),Sum(Times(Plus(Times(r,c),Times(r,d,Power(Times(r,Power(s,CN1)),m),Power(CN1,Times(CN2,k,m,Power(n,CN1)))),Times(r,e,Power(Times(r,Power(s,CN1)),p),Power(CN1,Times(CN2,k,p,Power(n,CN1)))),Times(r,f,Power(Times(r,Power(s,CN1)),q),Power(CN1,Times(CN2,k,q,Power(n,CN1))))),Power(Times(a,n,Subtract(r,Times(Power(CN1,Times(C2,k,Power(n,CN1))),s,u))),CN1)),list(k,C1,n)))),And(FreeQ(List(a,b,c,d,e,f),x),IntegersQ(m,n,p,q),Less(C0,m,p,q,n)))),
ISetDelayed(343,ExpandIntegrand(Power(Plus(a_,Times(c_DEFAULT,Power(u_,n_))),p_),x_Symbol),
    Condition(Module(list(q),ReplaceAll(ExpandIntegrand(Power(Power(c,p),CN1),Times(Power(Plus(Negate(q),Times(c,x)),p),Power(Plus(q,Times(c,x)),p)),x),list(Rule(q,Rt(Times(CN1,a,c),C2)),Rule(x,Power(u,Times(C1D2,n)))))),And(FreeQ(list(a,c),x),EvenQ(n),ILtQ(p,C0)))),
ISetDelayed(344,ExpandIntegrand(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(u_,n_))),p_)),x_Symbol),
    Condition(Module(list(q),ReplaceAll(ExpandIntegrand(Power(Power(c,p),CN1),Times(Power(x,m),Power(Plus(Negate(q),Times(c,Power(x,Times(C1D2,n)))),p),Power(Plus(q,Times(c,Power(x,Times(C1D2,n)))),p)),x),list(Rule(q,Rt(Times(CN1,a,c),C2)),Rule(x,u)))),And(FreeQ(list(a,c),x),IntegersQ(m,Times(C1D2,n)),ILtQ(p,C0),Less(C0,m,n),Unequal(m,Times(C1D2,n))))),
ISetDelayed(345,ExpandIntegrand(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,a,Power(b,CN1)),n))),Module(list($s("ii")),ExpandIntegrand(Power(Negate(b),p),Product(Power(Subtract(q,Times(Power(CN1,Times(C2,$s("ii"),Power(n,CN1))),x)),p),list($s("ii"),C1,n)),x))),And(FreeQ(list(a,b),x),IGtQ(n,C1),ILtQ(p,CN1)))),
ISetDelayed(346,ExpandIntegrand(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(u_,n_DEFAULT)),Times(c_DEFAULT,Power(u_,$p("n2",true)))),p_),x_Symbol),
    Condition(Module(list(q),ReplaceAll(ExpandIntegrand(Power(Times(Power(C4,p),Power(c,p)),CN1),Times(Power(Plus(b,Negate(q),Times(C2,c,x)),p),Power(Plus(b,q,Times(C2,c,x)),p)),x),list(Rule(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2)),Rule(x,Power(u,n))))),And(FreeQ(list(a,b,c),x),IntegerQ(n),EqQ($s("n2"),Times(C2,n)),ILtQ(p,C0),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
ISetDelayed(347,ExpandIntegrand(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(u_,n_DEFAULT)),Times(c_DEFAULT,Power(u_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Module(list(q),ReplaceAll(ExpandIntegrand(Power(Times(Power(C4,p),Power(c,p)),CN1),Times(Power(x,m),Power(Plus(b,Negate(q),Times(C2,c,Power(x,n))),p),Power(Plus(b,q,Times(C2,c,Power(x,n))),p)),x),list(Rule(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2)),Rule(x,u)))),And(FreeQ(list(a,b,c),x),IntegersQ(m,n,$s("n2")),EqQ($s("n2"),Times(C2,n)),ILtQ(p,C0),Less(C0,m,Times(C2,n)),Not(And(Equal(m,n),Equal(p,CN1))),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
ISetDelayed(348,ExpandIntegrand(Times(Plus(c_,Times(d_DEFAULT,Power(u_,n_DEFAULT))),Power(Plus(a_,Times(b_DEFAULT,Power(u_,$p("n2",true)))),CN1)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,a,Power(b,CN1)),C2))),Subtract(Times(CN1,Subtract(c,Times(d,q)),Power(Times(C2,b,q,Plus(q,Power(u,n))),CN1)),Times(Plus(c,Times(d,q)),Power(Times(C2,b,q,Subtract(q,Power(u,n))),CN1)))),And(FreeQ(List(a,b,c,d,n),x),EqQ($s("n2"),Times(C2,n))))),
ISetDelayed(349,ExpandIntegrand(Times(Plus(d_DEFAULT,Times(e_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,Power(u_,n_DEFAULT))))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(u_,n_DEFAULT)),Times(c_DEFAULT,Power(u_,$p("n2",true)))),CN1)),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),With(list(Set(r,TogetherSimplify(Times(Subtract(Times(C2,c,Plus(d,Times(e,f))),Times(b,e,g)),Power(q,CN1))))),Plus(Times(Plus(Times(e,g),r),Power(Plus(b,Negate(q),Times(C2,c,Power(u,n))),CN1)),Times(Subtract(Times(e,g),r),Power(Plus(b,q,Times(C2,c,Power(u,n))),CN1))))),And(FreeQ(List(a,b,c,d,e,f,g,n),x),EqQ($s("n2"),Times(C2,n)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
ISetDelayed(350,ExpandIntegrand(Times(u_,Power(v_,CN1)),x_Symbol),
    Condition(PolynomialDivide(u,v,x),And(PolynomialQ(u,x),PolynomialQ(v,x),GreaterEqual(Exponent(u,x),Exponent(v,x))))),
ISetDelayed(351,ExpandIntegrand(Times(u_,Power(Times(a_DEFAULT,x_),p_)),x_Symbol),
    Condition(ExpandToSum(Power(Times(a,x),p),u,x),And(Not(IntegerQ(p)),PolynomialQ(u,x)))),
ISetDelayed(352,ExpandIntegrand(Times(u_DEFAULT,Power(v_,p_)),x_Symbol),
    Condition(ExpandIntegrand(NormalizeIntegrand(Power(v,p),x),u,x),Not(IntegerQ(p)))),
ISetDelayed(353,ExpandIntegrand(u_,x_Symbol),
    With(list(Set(v,ExpandExpression(u,x))),Condition(v,SumQ(v)))),
ISetDelayed(354,ExpandIntegrand(Times(Power(u_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(u_,n_))),CN1)),x_Symbol),
    Condition(ExpandBinomial(a,b,m,n,u,x),And(FreeQ(list(a,b),x),IntegersQ(m,n),Less(C0,m,n)))),
ISetDelayed(355,ExpandIntegrand(u_,x_Symbol),
    u),
ISetDelayed(356,ExpandExpression(u_,x_Symbol),
    Module(list(v,w),CompoundExpression(Set(v,If(And(AlgebraicFunctionQ(u,x),Not(RationalFunctionQ(u,x))),ExpandAlgebraicFunction(u,x),C0)),If(SumQ(v),ExpandCleanup(v,x),CompoundExpression(Set(v,SmartApart(u,x)),If(SumQ(v),ExpandCleanup(v,x),CompoundExpression(Set(v,SmartApart(RationalFunctionFactors(u,x),x,x)),If(SumQ(v),CompoundExpression(Set(w,NonrationalFunctionFactors(u,x)),ExpandCleanup(Map(Function(Times(Slot1,w)),v),x)),CompoundExpression(Set(v,Expand(u,x)),If(SumQ(v),ExpandCleanup(v,x),CompoundExpression(Set(v,Expand(u)),If(SumQ(v),ExpandCleanup(v,x),SimplifyTerm(u,x))))))))))))),
ISetDelayed(357,ExpandCleanup(Plus(u_,Times(v_,Power(Plus(a_,Times(b_DEFAULT,x_)),CN1)),Times(w_,Power(Plus(c_,Times(d_DEFAULT,x_)),CN1))),x_Symbol),
    Condition(ExpandCleanup(Plus(u,Times(Plus(Times(c,v),Times(a,w)),Power(Plus(Times(a,c),Times(b,d,Sqr(x))),CN1))),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(d,v),Times(b,w)),C0)))),
ISetDelayed(358,ExpandCleanup(u_,x_Symbol),
    Module(list(Set(v,CollectReciprocals(u,x))),If(SumQ(v),CompoundExpression(Set(v,Map(Function(SimplifyTerm(Slot1,x)),v)),If(SumQ(v),UnifySum(v,x),v)),v))),
ISetDelayed(359,CollectReciprocals(Plus(u_,Times(e_,Power(Plus(a_,Times(b_DEFAULT,x_)),CN1)),Times(f_,Power(Plus(c_,Times(d_DEFAULT,x_)),CN1))),x_Symbol),
    Condition(CollectReciprocals(Plus(u,Times(Plus(Times(c,e),Times(a,f)),Power(Plus(Times(a,c),Times(b,d,Sqr(x))),CN1))),x),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(d,e),Times(b,f)),C0)))),
ISetDelayed(360,CollectReciprocals(Plus(u_,Times(e_,Power(Plus(a_,Times(b_DEFAULT,x_)),CN1)),Times(f_,Power(Plus(c_,Times(d_DEFAULT,x_)),CN1))),x_Symbol),
    Condition(CollectReciprocals(Plus(u,Times(Plus(Times(d,e),Times(b,f)),x,Power(Plus(Times(a,c),Times(b,d,Sqr(x))),CN1))),x),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),EqQ(Plus(Times(c,e),Times(a,f)),C0)))),
ISetDelayed(361,CollectReciprocals(u_,x_Symbol),
    u)
  );
}
