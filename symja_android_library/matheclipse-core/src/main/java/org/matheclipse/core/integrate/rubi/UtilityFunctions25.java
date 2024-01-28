package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions25 { 
  public static IAST RULES = List( 
ISetDelayed(322,ExpandIntegrand(Times(Power(Plus(a_,Times(b_DEFAULT,Power(F_,v_))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(F_,v_))),n_),u_DEFAULT),x_Symbol),
    Condition(With(list(Set(w,ReplaceAll(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),Rule(x,Power(FSymbol,v))))),Condition(Map(Function(Times(u,Slot1)),w),SumQ(w))),And(FreeQ(List(FSymbol,a,b,c,d),x),IntegersQ(m,n),Less(n,C0)))),
ISetDelayed(323,ExpandIntegrand(Times(Power(F_,Times(e_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT))),u_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(v,ExpandIntegrand(Times(u,Power(Plus(a,Times(b,x)),m)),x))),Condition(Distribute(Times(Power(FSymbol,Times(e,Power(Plus(c,Times(d,x)),n))),v),Plus,Times),SumQ(v))),And(FreeQ(List(FSymbol,a,b,c,d,e,m,n),x),PolynomialQ(u,x)))),
ISetDelayed(324,ExpandIntegrand(Times(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT))),u_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(ExpandIntegrand(Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))),Times(u,Power(Plus(a,Times(b,x)),m)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),PolynomialQ(u,x)))),
ISetDelayed(325,ExpandIntegrand(Times(Power(F_,Times(e_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT))),u_),x_Symbol),
    Condition(If(EqQ(n,C1),ExpandIntegrand(Power(FSymbol,Times(e,Power(Plus(c,Times(d,x)),n))),u,x),ExpandLinearProduct(Power(FSymbol,Times(e,Power(Plus(c,Times(d,x)),n))),u,c,d,x)),And(FreeQ(List(FSymbol,c,d,e,n),x),PolynomialQ(u,x)))),
ISetDelayed(326,ExpandIntegrand(Times(Power($(F_,u_),m_DEFAULT),Power(Plus(a_,Times($(G_,u_),b_DEFAULT)),n_DEFAULT)),x_Symbol),
    Condition(ReplaceAll(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),n),Power(Power(x,m),CN1)),x),Rule(x,G(u))),And(FreeQ(list(a,b),x),IntegersQ(m,n),SameQ(Times(F(u),G(u)),C1)))),
ISetDelayed(327,ExpandIntegrand(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),p_DEFAULT)),q_DEFAULT))),b_DEFAULT)),n_),u_),x_Symbol),
    Condition(ExpandLinearProduct(Power(Plus(a,Times(b,Log(Times(c,Power(Times(d,Power(Plus(e,Times(f,x)),p)),q))))),n),u,e,f,x),And(FreeQ(List(a,b,c,d,e,f,n,p,q),x),PolynomialQ(u,x)))),
ISetDelayed(328,ExpandIntegrand(Times(Power(Plus(a_DEFAULT,Times($(F_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT)),n_),u_),x_Symbol),
    Condition(ExpandLinearProduct(Power(Plus(a,Times(b,F(Plus(c,Times(d,x))))),n),u,c,d,x),And(FreeQ(List(a,b,c,d,n),x),PolynomialQ(u,x),MemberQ(List(ArcSin,ArcCos,ArcSinh,ArcCosh),FSymbol)))),
ISetDelayed(329,ExpandIntegrand(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,n_)),Times(b_DEFAULT,Sqrt(Plus(c_,Times(d_DEFAULT,Power(x_,j_)))))),CN1)),x_Symbol),
    Condition(ExpandIntegrand(Times(u,Subtract(Times(a,Power(x,n)),Times(b,Sqrt(Plus(c,Times(d,Power(x,Times(C2,n))))))),Power(Plus(Times(CN1,Sqr(b),c),Times(Subtract(Sqr(a),Times(Sqr(b),d)),Power(x,Times(C2,n)))),CN1)),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(j,Times(C2,n))))),
ISetDelayed(330,ExpandIntegrand(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(If(RationalQ(a,b,c,d),ExpandExpression(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),CN1)),x),With(list(Set($s("tmp"),Subtract(Times(a,d),Times(b,c)))),Module(list(k),Plus(Times(SimplifyTerm(Times(Power($s("tmp"),m),Power(Power(d,m),CN1)),x),Power(Plus(c,Times(d,x)),CN1)),Sum(Times(SimplifyTerm(Times(b,Power($s("tmp"),Subtract(k,C1)),Power(Power(d,k),CN1)),x),Power(Plus(a,Times(b,x)),Subtract(m,k))),list(k,C1,m)))))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
ISetDelayed(331,ExpandIntegrand(Times(Plus(A_,Times(B_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(If(RationalQ(a,b,c,d,ASymbol,BSymbol),ExpandExpression(Times(Power(Plus(a,Times(b,x)),m),Plus(ASymbol,Times(BSymbol,x)),Power(Plus(c,Times(d,x)),CN1)),x),Module(list($s("tmp1"),$s("tmp2")),CompoundExpression(Set($s("tmp1"),Times(Subtract(Times(ASymbol,d),Times(BSymbol,c)),Power(d,CN1))),Set($s("tmp2"),ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),CN1)),x)),Set($s("tmp2"),If(SumQ($s("tmp2")),Map(Function(SimplifyTerm(Times($s("tmp1"),Slot1),x)),$s("tmp2")),SimplifyTerm(Times($s("tmp1"),$s("tmp2")),x))),Plus(Times(SimplifyTerm(Times(BSymbol,Power(d,CN1)),x),Power(Plus(a,Times(b,x)),m)),$s("tmp2"))))),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol),x),IGtQ(m,C0)))),
ISetDelayed(332,ExpandIntegrand(Times(u_,Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(ExpandIntegrand(Power(Plus(c,Times(d,x)),n),Times(u,Power(Plus(a,Times(b,x)),m)),x),And(FreeQ(List(a,b,c,d,m,n),x),PolynomialQ(u,x),Not(IntegerQ(m)),IGtQ(Subtract(n,m),C0)))),
ISetDelayed(333,ExpandIntegrand(Times(u_,Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("sum1"),ExpandLinearProduct(Power(Plus(a,Times(b,x)),m),u,a,b,x))),If(Or(Not(IntegerQ(m)),And(Greater(m,C2),LinearQ(u,x))),$s("sum1"),With(list(Set($s("sum2"),ExpandExpression(Times(u,Power(Plus(a,Times(b,x)),m)),x))),If(SumQ($s("sum2")),If(Greater(m,C0),If(Or(LessEqual(Length($s("sum2")),Plus(Exponent(u,x),C2)),LessEqual(LeafCount($s("sum2")),Times(QQ(2L,3L),LeafCount($s("sum1"))))),$s("sum2"),$s("sum1")),If(LessEqual(LeafCount($s("sum2")),Plus(LeafCount($s("sum1")),C2)),$s("sum2"),$s("sum1"))),$s("sum1"))))),And(FreeQ(list(a,b,m),x),PolynomialQ(u,x),Not(And(IGtQ(m,C0),MatchQ(u,Condition(Times(w_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x)),p_)),And(FreeQ(list(c,d),x),IntegerQ(p),Greater(p,m))))))))),
ISetDelayed(334,ExpandIntegrand(Times(u_,Power(v_,n_),Power(Plus(a_,Times(b_DEFAULT,x_)),m_)),x_Symbol),
    Condition($(Function(Plus(ExpandIntegrand(Times(Part(Slot1,C1),Power(Plus(a,Times(b,x)),FractionalPart(m))),x),ExpandIntegrand(Times(Part(Slot1,C2),Power(v,n),Power(Plus(a,Times(b,x)),m)),x))),PolynomialQuotientRemainder(u,Times(Power(v,Negate(n)),Power(Plus(a,Times(b,x)),Negate(IntegerPart(m)))),x)),And(FreeQ(list(a,b,m),x),ILtQ(n,C0),Not(IntegerQ(m)),PolynomialQ(u,x),PolynomialQ(v,x),RationalQ(m),Less(m,CN1),GreaterEqual(Exponent(u,x),Times(CN1,Plus(n,IntegerPart(m)),Exponent(v,x)))))),
ISetDelayed(335,ExpandIntegrand(Times(u_,Power(v_,n_),Power(Plus(a_,Times(b_DEFAULT,x_)),m_)),x_Symbol),
    Condition($(Function(Plus(ExpandIntegrand(Times(Part(Slot1,C1),Power(Plus(a,Times(b,x)),m)),x),ExpandIntegrand(Times(Part(Slot1,C2),Power(v,n),Power(Plus(a,Times(b,x)),m)),x))),PolynomialQuotientRemainder(u,Power(v,Negate(n)),x)),And(FreeQ(list(a,b,m),x),ILtQ(n,C0),Not(IntegerQ(m)),PolynomialQ(u,x),PolynomialQ(v,x),GreaterEqual(Exponent(u,x),Times(CN1,n,Exponent(v,x)))))),
ISetDelayed(336,ExpandIntegrand(Power(Plus(a_,Times(b_DEFAULT,Power(u_,n_))),CN1),x_Symbol),
    Condition(With(list(Set(r,Numerator(Rt(Times(CN1,a,Power(b,CN1)),C2))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,CN1)),C2)))),Plus(Times(r,Power(Times(C2,a,Subtract(r,Times(s,Power(u,Times(C1D2,n))))),CN1)),Times(r,Power(Times(C2,a,Plus(r,Times(s,Power(u,Times(C1D2,n))))),CN1)))),And(FreeQ(list(a,b),x),IGtQ(Times(C1D2,n),C0)))),
ISetDelayed(337,ExpandIntegrand(Times(Plus(c_,Times(d_DEFAULT,Power(u_,n_))),Power(Plus(a_,Times(b_DEFAULT,Power(u_,$p("n2")))),CN1)),x_Symbol),
    Condition(With(list(Set(r,Numerator(Rt(Times(CN1,a,Power(b,CN1)),C2))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,CN1)),C2)))),Plus(Times(CN1,s,Plus(Times(d,r),Times(c,s)),Power(Times(C2,b,r,Subtract(r,Times(s,Power(u,n)))),CN1)),Times(s,Subtract(Times(d,r),Times(c,s)),Power(Times(C2,b,r,Plus(r,Times(s,Power(u,n)))),CN1)))),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C0),EqQ($s("n2"),Times(C2,n))))),
ISetDelayed(338,ExpandIntegrand(Times(Plus(c_DEFAULT,Times(d_DEFAULT,u_)),Power(Plus(a_,Times(b_DEFAULT,u_)),m_)),x_Symbol),
    Condition(Plus(Times(d,Power(b,CN1),Power(Plus(a,Times(b,u)),Plus(m,C1))),Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1),Power(Plus(a,Times(b,u)),m))),And(FreeQ(List(a,b,c,d),x),ILtQ(m,C0)))),
ISetDelayed(339,ExpandIntegrand(Power(Plus(a_,Times(b_DEFAULT,Power(u_,n_))),CN1),x_Symbol),
    Condition(With(list(Set(r,Numerator(Rt(Times(CN1,a,Power(b,CN1)),n))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,CN1)),n)))),Module(list(k),Sum(Times(r,Power(Times(a,n,Subtract(r,Times(Power(CN1,Times(C2,k,Power(n,CN1))),s,u))),CN1)),list(k,C1,n)))),And(FreeQ(list(a,b),x),IGtQ(n,C1)))),
ISetDelayed(340,ExpandIntegrand(Times(Plus(c_,Times(d_DEFAULT,Power(u_,m_DEFAULT))),Power(Plus(a_,Times(b_DEFAULT,Power(u_,n_))),CN1)),x_Symbol),
    Condition(With(list(Set(r,Numerator(Rt(Times(CN1,a,Power(b,CN1)),n))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,CN1)),n)))),Module(list(k),Sum(Times(Plus(Times(r,c),Times(r,d,Power(Times(r,Power(s,CN1)),m),Power(CN1,Times(CN2,k,m,Power(n,CN1))))),Power(Times(a,n,Subtract(r,Times(Power(CN1,Times(C2,k,Power(n,CN1))),s,u))),CN1)),list(k,C1,n)))),And(FreeQ(List(a,b,c,d),x),IntegersQ(m,n),Less(C0,m,n)))),
ISetDelayed(341,ExpandIntegrand(Times(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,m_DEFAULT)),Times(e_DEFAULT,Power(u_,p_))),Power(Plus(a_,Times(b_DEFAULT,Power(u_,n_))),CN1)),x_Symbol),
    Condition(With(list(Set(r,Numerator(Rt(Times(CN1,a,Power(b,CN1)),n))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,CN1)),n)))),Module(list(k),Sum(Times(Plus(Times(r,c),Times(r,d,Power(Times(r,Power(s,CN1)),m),Power(CN1,Times(CN2,k,m,Power(n,CN1)))),Times(r,e,Power(Times(r,Power(s,CN1)),p),Power(CN1,Times(CN2,k,p,Power(n,CN1))))),Power(Times(a,n,Subtract(r,Times(Power(CN1,Times(C2,k,Power(n,CN1))),s,u))),CN1)),list(k,C1,n)))),And(FreeQ(List(a,b,c,d,e),x),IntegersQ(m,n,p),Less(C0,m,p,n))))
  );
}
