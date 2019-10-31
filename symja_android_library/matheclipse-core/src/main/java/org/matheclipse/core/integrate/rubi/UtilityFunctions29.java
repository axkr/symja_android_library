package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions29 { 
  public static IAST RULES = List( 
ISetDelayed(656,UnifyInertTrigFunction(Times(Power(Times(d_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(n,C2),EqQ(p,C1))),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(657,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(658,KnownSineIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(List($s("§sin"),$s("§cos")),u,x)),
ISetDelayed(659,KnownTangentIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(List($s("§tan")),u,x)),
ISetDelayed(660,KnownCotangentIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(List($s("§cot")),u,x)),
ISetDelayed(661,KnownSecantIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(List($s("§sec"),$s("§csc")),u,x)),
ISetDelayed(662,KnownTrigIntegrandQ($p("§list"),u_,x_Symbol),
    Or(SameQ(u,C1),MatchQ(u,Condition(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,m),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,BSymbol,m),x)))),MatchQ(u,Condition(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(e,f,ASymbol,C),x)))),MatchQ(u,Condition(Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))),Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(e,f,ASymbol,BSymbol,C),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,C,m),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))),Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,BSymbol,C,m),x)))))),
ISetDelayed(663,PiecewiseLinearQ(u_,v_,x_Symbol),
    And(PiecewiseLinearQ(u,x),PiecewiseLinearQ(v,x))),
ISetDelayed(664,PiecewiseLinearQ(u_,x_Symbol),
    Or(LinearQ(u,x),MatchQ(u,Condition(Log(Times(c_DEFAULT,Power(F_,v_))),And(FreeQ(List(FSymbol,c),x),LinearQ(v,x)))),MatchQ(u,Condition($(F_,$(G_,v_)),And(LinearQ(v,x),MemberQ(List(List(ArcTanh,Tanh),List(ArcTanh,Coth),List(ArcCoth,Coth),List(ArcCoth,Tanh),List(ArcTan,Tan),List(ArcTan,Cot),List(ArcCot,Cot),List(ArcCot,Tan)),List(FSymbol,GSymbol))))))),
ISetDelayed(665,Divides(y_,u_,x_Symbol),
    With(List(Set(v,Simplify(Times(u,Power(y,CN1))))),If(FreeQ(v,x),v,False))),
ISetDelayed(666,DerivativeDivides(y_,u_,x_Symbol),
    If(MatchQ(y,Condition(Times(a_DEFAULT,x),FreeQ(a,x))),False,If(If(PolynomialQ(y,x),And(PolynomialQ(u,x),Equal(Exponent(u,x),Subtract(Exponent(y,x),C1))),EasyDQ(y,x)),Module(List(Set(v,Block(List(Set($s("§$showsteps"),False)),ReplaceAll(D(y,x),Rule(Sinc(z_),Times(Sin(z),Power(z,CN1))))))),If(EqQ(v,C0),False,CompoundExpression(Set(v,Simplify(Times(u,Power(v,CN1)))),If(FreeQ(v,x),v,False)))),False))),
ISetDelayed(667,EasyDQ(Times(u_DEFAULT,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(EasyDQ(u,x),FreeQ(m,x))),
ISetDelayed(668,EasyDQ(u_,x_Symbol),
    If(Or(AtomQ(u),FreeQ(u,x),Equal(Length(u),C0)),True,If(CalculusQ(u),False,If(Equal(Length(u),C1),EasyDQ(Part(u,C1),x),If(Or(BinomialQ(u,x),ProductOfLinearPowersQ(u,x)),True,If(And(RationalFunctionQ(u,x),SameQ(RationalFunctionExponents(u,x),List(C1,C1))),True,If(ProductQ(u),If(FreeQ(First(u),x),EasyDQ(Rest(u),x),If(FreeQ(Rest(u),x),EasyDQ(First(u),x),False)),If(SumQ(u),And(EasyDQ(First(u),x),EasyDQ(Rest(u),x)),If(Equal(Length(u),C2),If(FreeQ(Part(u,C1),x),EasyDQ(Part(u,C2),x),If(FreeQ(Part(u,C2),x),EasyDQ(Part(u,C1),x),False)),False))))))))),
ISetDelayed(669,ProductOfLinearPowersQ(u_,x_Symbol),
    Or(FreeQ(u,x),MatchQ(u,Condition(Power(v_,n_DEFAULT),And(LinearQ(v,x),FreeQ(n,x)))),And(ProductQ(u),ProductOfLinearPowersQ(First(u),x),ProductOfLinearPowersQ(Rest(u),x)))),
ISetDelayed(670,Rt(u_,$p(n, Integer)),
    RtAux(TogetherSimplify(u),n)),
ISetDelayed(671,NthRoot(u_,n_),
    Power(u,Power(n,CN1))),
ISetDelayed(672,TrigSquare(u_),
    If(SumQ(u),With(List(Set($s("lst"),SplitSum(Function(SplitProduct($rubi("TrigSquareQ"),Slot1)),u))),If(And(Not(AtomQ($s("lst"))),EqQ(Plus(Part($s("lst"),C1,C2),Part($s("lst"),C2)),C0)),If(SameQ(Head(Part(Part($s("lst"),C1,C1),C1)),Sin),Times(Part($s("lst"),C2),Sqr(Cos(Part(Part($s("lst"),C1,C1),C1,C1)))),Times(Part($s("lst"),C2),Sqr(Sin(Part(Part($s("lst"),C1,C1),C1,C1))))),False)),False)),
ISetDelayed(673,RtAux(u_,n_),
    If(PowerQ(u),Power(Part(u,C1),Times(Part(u,C2),Power(n,CN1))),If(ProductQ(u),Module(List($s("lst")),CompoundExpression(Set($s("lst"),SplitProduct(Function(GtQ(Slot1,C0)),u)),If(ListQ($s("lst")),Times(RtAux(Part($s("lst"),C1),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct(Function(LtQ(Slot1,C0)),u)),If(ListQ($s("lst")),If(EqQ(Part($s("lst"),C1),CN1),With(List(Set(v,Part($s("lst"),C2))),If(And(PowerQ(v),LtQ(Part(v,C2),C0)),Power(RtAux(Negate(Power(Part(v,C1),Negate(Part(v,C2)))),n),CN1),If(ProductQ(v),If(ListQ(SplitProduct($rubi("SumBaseQ"),v)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("AllNegTermQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("NegSumBaseQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("SomeNegTermQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("SumBaseQ"),v)),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n))))))))),CompoundExpression(Set($s("lst"),SplitProduct($rubi("AtomBaseQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),Times(RtAux(Negate(First(v)),n),RtAux(Rest(v),n))))),If(OddQ(n),Negate(RtAux(v,n)),NthRoot(u,n))))),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Negate(Part($s("lst"),C2)),n))),CompoundExpression(Set($s("lst"),SplitProduct($rubi("AllNegTermQ"),u)),If(And(ListQ($s("lst")),ListQ(SplitProduct($rubi("SumBaseQ"),Part($s("lst"),C2)))),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Negate(Part($s("lst"),C2)),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("NegSumBaseQ"),u)),If(And(ListQ($s("lst")),ListQ(SplitProduct($rubi("NegSumBaseQ"),Part($s("lst"),C2)))),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Negate(Part($s("lst"),C2)),n)),Map(Function(RtAux(Slot1,n)),u)))))))))),With(List(Set(v,TrigSquare(u))),If(Not(AtomQ(v)),RtAux(v,n),If(And(OddQ(n),LtQ(u,C0)),Negate(RtAux(Negate(u),n)),If(ComplexNumberQ(u),With(List(Set(a,Re(u)),Set(b,Im(u))),If(And(Not(And(IntegerQ(a),IntegerQ(b))),IntegerQ(Times(a,Power(Plus(Sqr(a),Sqr(b)),CN1))),IntegerQ(Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1)))),Power(RtAux(Subtract(Times(a,Power(Plus(Sqr(a),Sqr(b)),CN1)),Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1),CI)),n),CN1),NthRoot(u,n))),If(And(OddQ(n),NegQ(u),PosQ(Negate(u))),Negate(RtAux(Negate(u),n)),NthRoot(u,n))))))))),
ISetDelayed(674,AtomBaseQ(u_),
    Or(AtomQ(u),And(PowerQ(u),OddQ(Part(u,C2)),AtomBaseQ(Part(u,C1))))),
ISetDelayed(675,SumBaseQ(u_),
    Or(SumQ(u),And(PowerQ(u),OddQ(Part(u,C2)),SumBaseQ(Part(u,C1))))),
ISetDelayed(676,NegSumBaseQ(u_),
    Or(And(SumQ(u),NegQ(First(u))),And(PowerQ(u),OddQ(Part(u,C2)),NegSumBaseQ(Part(u,C1))))),
ISetDelayed(677,AllNegTermQ(u_),
    If(And(PowerQ(u),OddQ(Part(u,C2))),AllNegTermQ(Part(u,C1)),If(SumQ(u),And(NegQ(First(u)),AllNegTermQ(Rest(u))),NegQ(u)))),
ISetDelayed(678,SomeNegTermQ(u_),
    If(And(PowerQ(u),OddQ(Part(u,C2))),SomeNegTermQ(Part(u,C1)),If(SumQ(u),Or(NegQ(First(u)),SomeNegTermQ(Rest(u))),NegQ(u)))),
ISetDelayed(679,TrigSquareQ(u_),
    And(PowerQ(u),EqQ(Part(u,C2),C2),MemberQ(List(Sin,Cos),Head(Part(u,C1))))),
ISetDelayed(680,SplitProduct($p("func"),u_),
    If(ProductQ(u),If($($s("func"),First(u)),List(First(u),Rest(u)),With(List(Set($s("lst"),SplitProduct($s("func"),Rest(u)))),If(AtomQ($s("lst")),False,List(Part($s("lst"),C1),Times(First(u),Part($s("lst"),C2)))))),If($($s("func"),u),List(u,C1),False)))
  );
}
