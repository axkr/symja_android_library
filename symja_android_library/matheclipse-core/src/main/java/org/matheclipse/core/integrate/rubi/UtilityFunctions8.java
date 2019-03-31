package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions8 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
ISetDelayed(651,UnifyInertTrigFunction(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_),x_),
    Condition(Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,Times(C1D2,Pi),Times(f,x)))),n))),p),And(FreeQ(List(a,b,c,e,f,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p))))));
ISetDelayed(652,UnifyInertTrigFunction(Times(Power(Times(d_DEFAULT,$($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§sin"),Plus(e,Times(C1D2,Pi),Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,Times(C1D2,Pi),Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p))))));
ISetDelayed(653,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cos"),Plus(e,Times(C1D2,Pi),Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,Times(C1D2,Pi),Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p))))));
ISetDelayed(654,UnifyInertTrigFunction(Times(Power(Times(d_DEFAULT,$($s("§cot"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§tan"),Plus(e,Times(C1D2,Pi),Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,Times(C1D2,Pi),Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p))))));
ISetDelayed(655,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§cot"),Plus(e,Times(C1D2,Pi),Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,Times(C1D2,Pi),Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p))))));
ISetDelayed(656,UnifyInertTrigFunction(Times(Power(Times(d_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§sec"),Plus(e,Times(C1D2,Pi),Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,Times(C1D2,Pi),Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(n,C2),EqQ(p,C1))),Not(And(EqQ(a,C0),IntegerQ(p))))));
ISetDelayed(657,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,$($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_))),p_DEFAULT),Power(Times(d_DEFAULT,$($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§csc"),Plus(e,Times(C1D2,Pi),Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,Times(C1D2,Pi),Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p))))));
ISetDelayed(658,KnownSineIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(List($s("§sin"),$s("§cos")),u,x));
ISetDelayed(659,KnownTangentIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(List($s("§tan")),u,x));
ISetDelayed(660,KnownCotangentIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(List($s("§cot")),u,x));
ISetDelayed(661,KnownSecantIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(List($s("§sec"),$s("§csc")),u,x));
ISetDelayed(662,KnownTrigIntegrandQ($p("§list"),u_,x_Symbol),
    Or(SameQ(u,C1),MatchQ(u,Condition(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,m),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,BSymbol,m),x)))),MatchQ(u,Condition(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(e,f,ASymbol,CSymbol),x)))),MatchQ(u,Condition(Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))),Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(e,f,ASymbol,BSymbol,CSymbol),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,CSymbol,m),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))),Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,BSymbol,CSymbol,m),x))))));
ISetDelayed(663,PiecewiseLinearQ(u_,v_,x_Symbol),
    And(PiecewiseLinearQ(u,x),PiecewiseLinearQ(v,x)));
ISetDelayed(664,PiecewiseLinearQ(u_,x_Symbol),
    Or(LinearQ(u,x),MatchQ(u,Condition(Log(Times(c_DEFAULT,Power(F_,v_))),And(FreeQ(List(FSymbol,c),x),LinearQ(v,x)))),MatchQ(u,Condition($(F_,$(G_,v_)),And(LinearQ(v,x),MemberQ(List(List(ArcTanh,Tanh),List(ArcTanh,Coth),List(ArcCoth,Coth),List(ArcCoth,Tanh),List(ArcTan,Tan),List(ArcTan,Cot),List(ArcCot,Cot),List(ArcCot,Tan)),List(FSymbol,GSymbol)))))));
ISetDelayed(665,Divides(y_,u_,x_Symbol),
    With(List(Set(v,Simplify(Times(u,Power(y,CN1))))),If(FreeQ(v,x),v,False)));
ISetDelayed(666,DerivativeDivides(y_,u_,x_Symbol),
    If(MatchQ(y,Condition(Times(a_DEFAULT,x),FreeQ(a,x))),False,If(If(PolynomialQ(y,x),And(PolynomialQ(u,x),Equal(Exponent(u,x),Subtract(Exponent(y,x),C1))),EasyDQ(y,x)),Module(List(Set(v,Block(List(Set($s("§$showsteps"),False)),ReplaceAll(D(y,x),Rule(Sinc(z_),Times(Sin(z),Power(z,CN1))))))),If(EqQ(v,C0),False,CompoundExpression(Set(v,Simplify(Times(u,Power(v,CN1)))),If(FreeQ(v,x),v,False)))),False)));
ISetDelayed(667,EasyDQ(Times(u_DEFAULT,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(EasyDQ(u,x),FreeQ(m,x)));
ISetDelayed(668,EasyDQ(u_,x_Symbol),
    If(Or(AtomQ(u),FreeQ(u,x),Equal(Length(u),C0)),True,If(CalculusQ(u),False,If(Equal(Length(u),C1),EasyDQ(Part(u,C1),x),If(Or(BinomialQ(u,x),ProductOfLinearPowersQ(u,x)),True,If(And(RationalFunctionQ(u,x),SameQ(RationalFunctionExponents(u,x),List(C1,C1))),True,If(ProductQ(u),If(FreeQ(First(u),x),EasyDQ(Rest(u),x),If(FreeQ(Rest(u),x),EasyDQ(First(u),x),False)),If(SumQ(u),And(EasyDQ(First(u),x),EasyDQ(Rest(u),x)),If(Equal(Length(u),C2),If(FreeQ(Part(u,C1),x),EasyDQ(Part(u,C2),x),If(FreeQ(Part(u,C2),x),EasyDQ(Part(u,C1),x),False)),False)))))))));
ISetDelayed(669,ProductOfLinearPowersQ(u_,x_Symbol),
    Or(FreeQ(u,x),MatchQ(u,Condition(Power(v_,n_DEFAULT),And(LinearQ(v,x),FreeQ(n,x)))),And(ProductQ(u),ProductOfLinearPowersQ(First(u),x),ProductOfLinearPowersQ(Rest(u),x))));
ISetDelayed(670,Rt(u_,$p(n, Integer)),
    RtAux(TogetherSimplify(u),n));
ISetDelayed(671,NthRoot(u_,n_),
    Power(u,Power(n,CN1)));
ISetDelayed(672,TrigSquare(u_),
    If(SumQ(u),With(List(Set($s("lst"),SplitSum(Function(SplitProduct($rubi("TrigSquareQ"),Slot1)),u))),If(And(Not(AtomQ($s("lst"))),EqQ(Plus(Part($s("lst"),C1,C2),Part($s("lst"),C2)),C0)),If(SameQ(Head(Part(Part($s("lst"),C1,C1),C1)),Sin),Times(Part($s("lst"),C2),Sqr(Cos(Part(Part($s("lst"),C1,C1),C1,C1)))),Times(Part($s("lst"),C2),Sqr(Sin(Part(Part($s("lst"),C1,C1),C1,C1))))),False)),False));
ISetDelayed(673,RtAux(u_,n_),
    If(PowerQ(u),Power(Part(u,C1),Times(Part(u,C2),Power(n,CN1))),If(ProductQ(u),Module(List($s("lst")),CompoundExpression(Set($s("lst"),SplitProduct(Function(GtQ(Slot1,C0)),u)),If(ListQ($s("lst")),Times(RtAux(Part($s("lst"),C1),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct(Function(LtQ(Slot1,C0)),u)),If(ListQ($s("lst")),If(EqQ(Part($s("lst"),C1),CN1),With(List(Set(v,Part($s("lst"),C2))),If(And(PowerQ(v),LtQ(Part(v,C2),C0)),Power(RtAux(Negate(Power(Part(v,C1),Negate(Part(v,C2)))),n),CN1),If(ProductQ(v),If(ListQ(SplitProduct($rubi("SumBaseQ"),v)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("AllNegTermQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("NegSumBaseQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("SomeNegTermQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("SumBaseQ"),v)),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n))))))))),CompoundExpression(Set($s("lst"),SplitProduct($rubi("AtomBaseQ"),v)),If(ListQ($s("lst")),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Part($s("lst"),C2),n)),Times(RtAux(Negate(First(v)),n),RtAux(Rest(v),n))))),If(OddQ(n),Negate(RtAux(v,n)),NthRoot(u,n))))),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Negate(Part($s("lst"),C2)),n))),CompoundExpression(Set($s("lst"),SplitProduct($rubi("AllNegTermQ"),u)),If(And(ListQ($s("lst")),ListQ(SplitProduct($rubi("SumBaseQ"),Part($s("lst"),C2)))),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Negate(Part($s("lst"),C2)),n)),CompoundExpression(Set($s("lst"),SplitProduct($rubi("NegSumBaseQ"),u)),If(And(ListQ($s("lst")),ListQ(SplitProduct($rubi("NegSumBaseQ"),Part($s("lst"),C2)))),Times(RtAux(Negate(Part($s("lst"),C1)),n),RtAux(Negate(Part($s("lst"),C2)),n)),Map(Function(RtAux(Slot1,n)),u)))))))))),With(List(Set(v,TrigSquare(u))),If(Not(AtomQ(v)),RtAux(v,n),If(And(OddQ(n),LtQ(u,C0)),Negate(RtAux(Negate(u),n)),If(ComplexNumberQ(u),With(List(Set(a,Re(u)),Set(b,Im(u))),If(And(Not(And(IntegerQ(a),IntegerQ(b))),IntegerQ(Times(a,Power(Plus(Sqr(a),Sqr(b)),CN1))),IntegerQ(Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1)))),Power(RtAux(Subtract(Times(a,Power(Plus(Sqr(a),Sqr(b)),CN1)),Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1),CI)),n),CN1),NthRoot(u,n))),If(And(OddQ(n),NegQ(u),PosQ(Negate(u))),Negate(RtAux(Negate(u),n)),NthRoot(u,n)))))))));
ISetDelayed(674,AtomBaseQ(u_),
    Or(AtomQ(u),And(PowerQ(u),OddQ(Part(u,C2)),AtomBaseQ(Part(u,C1)))));
ISetDelayed(675,SumBaseQ(u_),
    Or(SumQ(u),And(PowerQ(u),OddQ(Part(u,C2)),SumBaseQ(Part(u,C1)))));
ISetDelayed(676,NegSumBaseQ(u_),
    Or(And(SumQ(u),NegQ(First(u))),And(PowerQ(u),OddQ(Part(u,C2)),NegSumBaseQ(Part(u,C1)))));
ISetDelayed(677,AllNegTermQ(u_),
    If(And(PowerQ(u),OddQ(Part(u,C2))),AllNegTermQ(Part(u,C1)),If(SumQ(u),And(NegQ(First(u)),AllNegTermQ(Rest(u))),NegQ(u))));
ISetDelayed(678,SomeNegTermQ(u_),
    If(And(PowerQ(u),OddQ(Part(u,C2))),SomeNegTermQ(Part(u,C1)),If(SumQ(u),Or(NegQ(First(u)),SomeNegTermQ(Rest(u))),NegQ(u))));
ISetDelayed(679,TrigSquareQ(u_),
    And(PowerQ(u),EqQ(Part(u,C2),C2),MemberQ(List(Sin,Cos),Head(Part(u,C1)))));
ISetDelayed(680,SplitProduct($p("func"),u_),
    If(ProductQ(u),If($($s("func"),First(u)),List(First(u),Rest(u)),With(List(Set($s("lst"),SplitProduct($s("func"),Rest(u)))),If(AtomQ($s("lst")),False,List(Part($s("lst"),C1),Times(First(u),Part($s("lst"),C2)))))),If($($s("func"),u),List(u,C1),False)));
ISetDelayed(681,SplitSum($p("func"),u_),
    If(SumQ(u),If(Not(AtomQ($($s("func"),First(u)))),List($($s("func"),First(u)),Rest(u)),With(List(Set($s("lst"),SplitSum($s("func"),Rest(u)))),If(AtomQ($s("lst")),False,List(Part($s("lst"),C1),Plus(First(u),Part($s("lst"),C2)))))),If(Not(AtomQ($($s("func"),u))),List($($s("func"),u),C0),False)));
ISetDelayed(682,IntSum(u_,x_Symbol),
    Plus(Simp(Times(FreeTerms(u,x),x),x),IntTerm(NonfreeTerms(u,x),x)));
ISetDelayed(683,IntTerm(u_,x_Symbol),
    Condition(Map(Function(IntTerm(Slot1,x)),u),SumQ(u)));
ISetDelayed(684,IntTerm(Times(c_DEFAULT,Power(v_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,Cancel(v))),If(EqQ(m,CN1),Simp(Times(c,Log(RemoveContent(u,x)),Power(Coeff(u,x,C1),CN1)),x),If(And(EqQ(m,C1),EqQ(c,C1),SumQ(u)),IntSum(u,x),Simp(Times(c,Power(u,Plus(m,C1)),Power(Times(Coeff(u,x,C1),Plus(m,C1)),CN1)),x)))),And(FreeQ(List(c,m),x),LinearQ(v,x))));
ISetDelayed(685,IntTerm(u_,x_Symbol),
    Dist(FreeFactors(u,x),Int(NonfreeFactors(u,x),x),x));
ISetDelayed(686,RuleName($p("name")),
    CompoundExpression(AppendTo($s("§$rulenamelist"),$s("name")),Null));
ISetDelayed(687,FixIntRules(),
    CompoundExpression(Set($($s("§downvalues"),Integrate),FixIntRules($($s("§downvalues"),Integrate))),Null));
ISetDelayed(688,FixIntRules($p("rulelist")),
    Block(List(Integrate,$rubi("Subst"),$rubi("Simp"),$rubi("Dist")),CompoundExpression(SetAttributes(List($rubi("Simp"),$rubi("Dist"),Integrate,$rubi("Subst")),HoldAll),Map(Function(FixIntRule(Slot1,Part(Slot1,C1,C1,C2,C1))),$s("rulelist")))));
ISetDelayed(689,FixIntRule($p("§rule")),
    If(AtomQ(Part($s("§rule"),C1,C1,CN1)),FixIntRule($s("§rule"),Part($s("§rule"),C1,C1,CN1)),If(And(SameQ(Head(Part($s("§rule"),C1,C1,CN1)),Pattern),AtomQ(Part($s("§rule"),C1,C1,CN1,C1))),FixIntRule($s("§rule"),Part($s("§rule"),C1,C1,CN1,C1)),Print($str("Invalid integration rule: "),Part($s("§rule"),C1,C1,CN1)))));
ISetDelayed(690,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),$(F_,Plus(u_,v_),$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),F(Plus(u,v),$s("test2"))),$s("test1"))),List(Rule(List(C2,C1,C2,C1,C1),FixRhsIntRule(u,x)),Rule(List(C2,C1,C2,C1,C2),FixRhsIntRule(v,x)))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)))));
ISetDelayed(691,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),$(F_,Plus(u_,v_),$p("test2")))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),F(Plus(u,v),$s("test2")))),List(Rule(List(C2,C2,C1,C1),FixRhsIntRule(u,x)),Rule(List(C2,C2,C1,C2),FixRhsIntRule(v,x)))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)))));
ISetDelayed(692,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),Plus(u_,v_)),$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),Plus(u,v)),$s("test"))),List(Rule(List(C2,C1,C2,C1),FixRhsIntRule(u,x)),Rule(List(C2,C1,C2,C2),FixRhsIntRule(v,x)))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)))));
ISetDelayed(693,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),Plus(u_,v_))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),Plus(u,v))),List(Rule(List(C2,C2,C1),FixRhsIntRule(u,x)),Rule(List(C2,C2,C2),FixRhsIntRule(v,x)))),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))));
ISetDelayed(694,FixIntRule(RuleDelayed($p("lhs"),$(F_,Plus(u_,v_),$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(Plus(u,v),$s("test"))),List(Rule(List(C2,C1,C1),FixRhsIntRule(u,x)),Rule(List(C2,C1,C2),FixRhsIntRule(v,x)))),SameQ(FSymbol,Condition)));
ISetDelayed(695,FixIntRule(RuleDelayed($p("lhs"),Plus(u_,v_)),x_),
    ReplacePart(RuleDelayed($s("lhs"),Plus(u,v)),List(Rule(List(C2,C1),FixRhsIntRule(u,x)),Rule(List(C2,C2),FixRhsIntRule(v,x)))));
ISetDelayed(696,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("list1"),$(F_,$($p("H"),$p("list2"),u_),$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("list1"),F(H($s("list2"),u),$s("test2"))),$s("test1"))),Rule(List(C2,C1,C2,C1,C2),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)),Or(SameQ($s("H"),With),SameQ($s("H"),Module),SameQ($s("H"),Block)))));
ISetDelayed(697,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("list1"),$($p("H"),$p("list2"),u_)),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("list1"),H($s("list2"),u)),$s("test1"))),Rule(List(C2,C1,C2,C2),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)),Or(SameQ($s("H"),With),SameQ($s("H"),Module),SameQ($s("H"),Block)))));
ISetDelayed(698,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),$(F_,$($p("H"),$p("str1"),$p("str2"),$p("str3"),$($p("J"),u_)),$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),F(H($s("str1"),$s("str2"),$s("str3"),J(u)),$s("test2"))),$s("test1"))),Rule(List(C2,C1,C2,C1,C4,C1),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)),SameQ($s("H"),$s("§showstep")),SameQ($s("J"),Hold))));
ISetDelayed(699,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),$(F_,u_,$p("test2"))),$p("test1"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),F(u,$s("test2"))),$s("test1"))),Rule(List(C2,C1,C2,C1),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)))));
ISetDelayed(700,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),$(F_,u_,$p("test2")))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),F(u,$s("test2")))),Rule(List(C2,C2,C1),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)))));
ISetDelayed(701,FixIntRule(RuleDelayed($p("lhs"),$(F_,$(G_,$p("§list"),u_),$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(G($s("§list"),u),$s("test"))),Rule(List(C2,C1,C2),FixRhsIntRule(u,x))),And(SameQ(FSymbol,Condition),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block)))));
ISetDelayed(702,FixIntRule(RuleDelayed($p("lhs"),$(G_,$p("§list"),u_)),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),G($s("§list"),u)),Rule(List(C2,C2),FixRhsIntRule(u,x))),Or(SameQ(GSymbol,With),SameQ(GSymbol,Module),SameQ(GSymbol,Block))));
ISetDelayed(703,FixIntRule(RuleDelayed($p("lhs"),$(F_,u_,$p("test"))),x_),
    Condition(ReplacePart(RuleDelayed($s("lhs"),F(u,$s("test"))),Rule(List(C2,C1),FixRhsIntRule(u,x))),SameQ(FSymbol,Condition)));
ISetDelayed(704,FixIntRule(RuleDelayed($p("lhs"),u_),x_),
    ReplacePart(RuleDelayed($s("lhs"),u),Rule(List(C2),FixRhsIntRule(u,x))));
ISetDelayed(705,FixRhsIntRule(Plus(u_,v_),x_),
    Plus(FixRhsIntRule(u,x),FixRhsIntRule(v,x)));
ISetDelayed(706,FixRhsIntRule(Subtract(u_,v_),x_),
    Subtract(FixRhsIntRule(u,x),FixRhsIntRule(v,x)));
ISetDelayed(707,FixRhsIntRule(Negate(u_),x_),
    Negate(FixRhsIntRule(u,x)));
ISetDelayed(708,FixRhsIntRule(Times(a_,u_),x_),
    Condition(Dist(a,u,x),MemberQ(List(Integrate,$rubi("Subst")),Head(Unevaluated(u)))));
ISetDelayed(709,FixRhsIntRule(u_,x_),
    If(And(SameQ(Head(Unevaluated(u)),$rubi("Dist")),Equal(Length(Unevaluated(u)),C2)),Insert(Unevaluated(u),x,C3),If(MemberQ(List(Integrate,$rubi("Unintegrable"),$rubi("CannotIntegrate"),$rubi("Subst"),$rubi("Simp"),$rubi("Dist")),Head(Unevaluated(u))),u,Simp(u,x))));
  };
}
}
