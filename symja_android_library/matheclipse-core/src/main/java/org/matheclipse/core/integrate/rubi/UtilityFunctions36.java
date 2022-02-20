package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions36 { 
  public static IAST RULES = List( 
ISetDelayed(651,UnifyInertTrigFunction(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_),x_),
    Condition(Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p),And(FreeQ(List(a,b,c,e,f,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(652,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§sin"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(653,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cos"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(654,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§tan"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(655,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cot"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(656,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(n,C2),EqQ(p,C1))),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(657,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(658,KnownSineIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(list($s("§sin"),$s("§cos")),u,x)),
ISetDelayed(659,KnownTangentIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(list($s("§tan")),u,x)),
ISetDelayed(660,KnownCotangentIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(list($s("§cot")),u,x)),
ISetDelayed(661,KnownSecantIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(list($s("§sec"),$s("§csc")),u,x)),
ISetDelayed(662,KnownTrigIntegrandQ($p("§list"),u_,x_Symbol),
    Or(SameQ(u,C1),MatchQ(u,Condition(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,m),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,BSymbol,m),x)))),MatchQ(u,Condition(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(e,f,ASymbol,CSymbol),x)))),MatchQ(u,Condition(Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))),Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(e,f,ASymbol,BSymbol,CSymbol),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,CSymbol,m),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))),Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,BSymbol,CSymbol,m),x)))))),
ISetDelayed(663,PiecewiseLinearQ(u_,v_,x_Symbol),
    And(PiecewiseLinearQ(u,x),PiecewiseLinearQ(v,x))),
ISetDelayed(664,PiecewiseLinearQ(u_,x_Symbol),
    Or(LinearQ(u,x),MatchQ(u,Condition(Log(Times(c_DEFAULT,Power(F_,v_))),And(FreeQ(list(FSymbol,c),x),LinearQ(v,x)))),MatchQ(u,Condition($(F_,$(G_,v_)),And(LinearQ(v,x),MemberQ(List(list(ArcTanh,Tanh),list(ArcTanh,Coth),list(ArcCoth,Coth),list(ArcCoth,Tanh),list(ArcTan,Tan),list(ArcTan,Cot),list(ArcCot,Cot),list(ArcCot,Tan)),list(FSymbol,GSymbol))))))),
ISetDelayed(665,Divides(y_,u_,x_Symbol),
    With(list(Set(v,Simplify(Times(u,Power(y,CN1))))),If(FreeQ(v,x),v,False))),
ISetDelayed(666,DerivativeDivides(y_,u_,x_Symbol),
    If(MatchQ(y,Condition(Times(a_DEFAULT,x),FreeQ(a,x))),False,If(If(PolynomialQ(y,x),And(PolynomialQ(u,x),Equal(Exponent(u,x),Subtract(Exponent(y,x),C1))),EasyDQ(y,x)),Module(list(Set(v,Block(list(Set($s("§$showsteps"),False)),ReplaceAll(D(y,x),Rule(Sinc(z_),Times(Sin(z),Power(z,CN1))))))),If(EqQ(v,C0),False,CompoundExpression(Set(v,Simplify(Times(u,Power(v,CN1)))),If(FreeQ(v,x),v,False)))),False))),
ISetDelayed(667,EasyDQ(Times(u_DEFAULT,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(EasyDQ(u,x),FreeQ(m,x))),
ISetDelayed(668,EasyDQ(u_,x_Symbol),
    If(Or(AtomQ(u),FreeQ(u,x),Equal(Length(u),C0)),True,If(CalculusQ(u),False,If(Equal(Length(u),C1),EasyDQ(Part(u,C1),x),If(Or(BinomialQ(u,x),ProductOfLinearPowersQ(u,x)),True,If(And(RationalFunctionQ(u,x),SameQ(RationalFunctionExponents(u,x),list(C1,C1))),True,If(ProductQ(u),If(FreeQ(First(u),x),EasyDQ(Rest(u),x),If(FreeQ(Rest(u),x),EasyDQ(First(u),x),False)),If(SumQ(u),And(EasyDQ(First(u),x),EasyDQ(Rest(u),x)),If(Equal(Length(u),C2),If(FreeQ(Part(u,C1),x),EasyDQ(Part(u,C2),x),If(FreeQ(Part(u,C2),x),EasyDQ(Part(u,C1),x),False)),False))))))))),
ISetDelayed(669,ProductOfLinearPowersQ(u_,x_Symbol),
    Or(FreeQ(u,x),MatchQ(u,Condition(Power(v_,n_DEFAULT),And(LinearQ(v,x),FreeQ(n,x)))),And(ProductQ(u),ProductOfLinearPowersQ(First(u),x),ProductOfLinearPowersQ(Rest(u),x)))),
ISetDelayed(670,Rt(u_,$p(n, Integer)),
    RtAux(TogetherSimplify(u),n))
  );
}
