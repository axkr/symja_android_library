package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions1 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
ISetDelayed(35,GtQ(u_,v_,w_),
    And(GtQ(u,v),GtQ(v,w)));
ISetDelayed(36,LtQ(u_,v_),
    If(RealNumberQ(u),If(RealNumberQ(v),Less(u,v),With(List(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),Less(u,$s("vn"))))),With(List(Set($s("un"),N(Together(u)))),If(SameQ(Head($s("un")),Real),If(RealNumberQ(v),Less($s("un"),v),With(List(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),Less($s("un"),$s("vn"))))),False))));
ISetDelayed(37,LtQ(u_,v_,w_),
    And(LtQ(u,v),LtQ(v,w)));
ISetDelayed(38,GeQ(u_,v_),
    If(RealNumberQ(u),If(RealNumberQ(v),GreaterEqual(u,v),With(List(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),GreaterEqual(u,$s("vn"))))),With(List(Set($s("un"),N(Together(u)))),If(SameQ(Head($s("un")),Real),If(RealNumberQ(v),GreaterEqual($s("un"),v),With(List(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),GreaterEqual($s("un"),$s("vn"))))),False))));
ISetDelayed(39,GeQ(u_,v_,w_),
    And(GeQ(u,v),GeQ(v,w)));
ISetDelayed(40,LeQ(u_,v_),
    If(RealNumberQ(u),If(RealNumberQ(v),LessEqual(u,v),With(List(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),LessEqual(u,$s("vn"))))),With(List(Set($s("un"),N(Together(u)))),If(SameQ(Head($s("un")),Real),If(RealNumberQ(v),LessEqual($s("un"),v),With(List(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),LessEqual($s("un"),$s("vn"))))),False))));
ISetDelayed(41,LeQ(u_,v_,w_),
    And(LeQ(u,v),LeQ(v,w)));
ISetDelayed(42,PolyQ(u_,x_Symbol,n_),
    If(PolynomialQ(u,x),And(EqQ(Exponent(u,x),n),NeQ(Coefficient(u,x,n),C0)),With(List(Set(v,Together(u))),And(PolynomialQ(v,x),EqQ(Exponent(v,x),n),NeQ(Coefficient(v,x,n),C0)))));
ISetDelayed(43,PolyQ(u_,Power(x_Symbol,v_),n_),
    And(PolyQ(u,Power(x,v)),EqQ(Expon(u,Power(x,v)),n),NeQ(Coeff(u,Power(x,v),n),C0)));
ISetDelayed(44,PolyQ(u_,x_Symbol),
    Or(PolynomialQ(u,x),PolynomialQ(Together(u),x)));
ISetDelayed(45,PolyQ(u_,Power(x_Symbol,$p(n, Integer))),
    Condition(If(PolynomialQ(u,x),PolynomialQ(u,Power(x,n)),With(List(Set(v,Together(u))),And(PolynomialQ(v,x),PolynomialQ(v,Power(x,n))))),Greater(n,C0)));
ISetDelayed(46,PolyQ(u_,Power(x_Symbol,v_)),
    Condition(If(SameQ(Quiet(PolynomialQ(u,Power(x,v))),True),FreeQ(CoefficientList(u,Power(x,v)),x),With(List(Set(w,Together(u))),And(SameQ(Quiet(PolynomialQ(w,Power(x,v))),True),FreeQ(CoefficientList(w,Power(x,v)),x)))),And(NonsumQ(v),FreeQ(v,x))));
ISetDelayed(47,PolyQ(u_,v_),
    False);
ISetDelayed(48,ProperPolyQ(u_,x_Symbol),
    And(PolyQ(u,x),NeQ(Coeff(u,x,C0),C0)));
ISetDelayed(49,BinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(BinomialQ(Slot1,x)),Throw(False))),u),True)),ListQ(BinomialParts(u,x))));
ISetDelayed(50,BinomialQ(u_,x_Symbol,n_),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(BinomialQ(Slot1,x,n)),Throw(False))),u),True)),$(Function(And(ListQ(Slot1),SameQ(Part(Slot1,C3),n))),BinomialParts(u,x))));
ISetDelayed(51,TrinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(TrinomialQ(Slot1,x)),Throw(False))),u),True)),And(ListQ(TrinomialParts(u,x)),Not(QuadraticQ(u,x)),Not(MatchQ(u,Condition(Sqr(w_),BinomialQ(w,x)))))));
ISetDelayed(52,GeneralizedBinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(GeneralizedBinomialQ(Slot1,x)),Throw(False))),u),True)),ListQ(GeneralizedBinomialParts(u,x))));
ISetDelayed(53,GeneralizedTrinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(GeneralizedTrinomialQ(Slot1,x)),Throw(False))),u),True)),ListQ(GeneralizedTrinomialParts(u,x))));
ISetDelayed(54,PosQ(u_),
    PosAux(TogetherSimplify(u)));
ISetDelayed(55,PosAux(u_),
    If(NumberQ(u),If(SameQ(Head(u),Complex),If(EqQ(Re(u),C0),PosAux(Im(u)),PosAux(Re(u))),Greater(u,C0)),If(NumericQ(u),With(List(Set(v,Simplify(Re(u)))),If(NumberQ(v),If(EqQ(v,C0),PosAux(Simplify(Im(u))),Greater(v,C0)),With(List(Set(w,N(u))),And(NumberQ(w),PosAux(w))))),With(List(Set(v,Refine(Greater(u,C0)))),If(Or(SameQ(v,True),SameQ(v,False)),v,If(PowerQ(u),If(IntegerQ(Part(u,C2)),Or(EvenQ(Part(u,C2)),PosAux(Part(u,C1))),True),If(ProductQ(u),If(PosAux(First(u)),PosAux(Rest(u)),Not(PosAux(Rest(u)))),If(SumQ(u),PosAux(First(u)),True))))))));
ISetDelayed(56,NegQ(u_),
    And(Not(PosQ(u)),NeQ(u,C0)));
ISetDelayed(57,NiceSqrtQ(u_),
    And(Not(LtQ(u,C0)),NiceSqrtAuxQ(u)));
ISetDelayed(58,NiceSqrtAuxQ(u_),
    If(RationalQ(u),Greater(u,C0),If(PowerQ(u),EvenQ(Part(u,C2)),If(ProductQ(u),And(NiceSqrtAuxQ(First(u)),NiceSqrtAuxQ(Rest(u))),If(SumQ(u),$(Function(And(NonsumQ(Slot1),NiceSqrtAuxQ(Slot1))),Simplify(u)),False)))));
ISetDelayed(59,PerfectSquareQ(u_),
    If(RationalQ(u),And(Greater(u,C0),RationalQ(Sqrt(u))),If(PowerQ(u),EvenQ(Part(u,C2)),If(ProductQ(u),And(PerfectSquareQ(First(u)),PerfectSquareQ(Rest(u))),If(SumQ(u),$(Function(And(NonsumQ(Slot1),PerfectSquareQ(Slot1))),Simplify(u)),False)))));
ISetDelayed(60,SimplerQ(u_,v_),
    If(IntegerQ(u),If(IntegerQ(v),If(Equal(u,v),False,If(Equal(u,Negate(v)),Less(v,C0),Less(Abs(u),Abs(v)))),True),If(IntegerQ(v),False,If(FractionQ(u),If(FractionQ(v),If(Equal(Denominator(u),Denominator(v)),SimplerQ(Numerator(u),Numerator(v)),Less(Denominator(u),Denominator(v))),True),If(FractionQ(v),False,If(And(Or(SameQ(Re(u),C0),SameQ(Re(u),num(0.0))),Or(SameQ(Re(v),C0),SameQ(Re(v),num(0.0)))),SimplerQ(Im(u),Im(v)),If(ComplexNumberQ(u),If(ComplexNumberQ(v),If(Equal(Re(u),Re(v)),SimplerQ(Im(u),Im(v)),SimplerQ(Re(u),Re(v))),False),If(NumberQ(u),If(NumberQ(v),OrderedQ(List(u,v)),True),If(NumberQ(v),False,If(AtomQ(u),If(AtomQ(v),OrderedQ(List(u,v)),True),If(AtomQ(v),False,If(SameQ(Head(u),Head(v)),If(Equal(Length(u),Length(v)),If(Equal(LeafCount(u),LeafCount(v)),Catch(CompoundExpression(Do(If(SameQ(Part(u,$s("ii")),Part(v,$s("ii"))),Null,Throw(SimplerQ(Part(u,$s("ii")),Part(v,$s("ii"))))),List($s("ii"),Length(u))),False)),Less(LeafCount(u),LeafCount(v))),Less(Length(u),Length(v))),If(Equal(LeafCount(u),LeafCount(v)),Not(OrderedQ(List(v,u))),Less(LeafCount(u),LeafCount(v)))))))))))))));
ISetDelayed(61,SimplerSqrtQ(u_,v_),
    If(And(LtQ(v,C0),Not(LtQ(u,C0))),True,If(And(LtQ(u,C0),Not(LtQ(v,C0))),False,With(List(Set($s("sqrtu"),Rt(u,C2)),Set($s("sqrtv"),Rt(v,C2))),If(IntegerQ($s("sqrtu")),If(IntegerQ($s("sqrtv")),Less($s("sqrtu"),$s("sqrtv")),True),If(IntegerQ($s("sqrtv")),False,If(RationalQ($s("sqrtu")),If(RationalQ($s("sqrtv")),Less($s("sqrtu"),$s("sqrtv")),True),If(RationalQ($s("sqrtv")),False,If(PosQ(u),If(PosQ(v),Less(LeafCount($s("sqrtu")),LeafCount($s("sqrtv"))),True),If(PosQ(v),False,If(Less(LeafCount($s("sqrtu")),LeafCount($s("sqrtv"))),True,If(Less(LeafCount($s("sqrtv")),LeafCount($s("sqrtu"))),False,Not(OrderedQ(List(v,u)))))))))))))));
ISetDelayed(62,SumSimplerQ(u_,v_),
    If(RationalQ(u,v),If(Equal(v,C0),False,If(Greater(v,C0),Less(u,CN1),GreaterEqual(u,Negate(v)))),SumSimplerAuxQ(Expand(u),Expand(v))));
ISetDelayed(63,SumSimplerAuxQ(u_,v_),
    Condition(And(Or(RationalQ(First(v)),SumSimplerAuxQ(u,First(v))),Or(RationalQ(Rest(v)),SumSimplerAuxQ(u,Rest(v)))),SumQ(v)));
ISetDelayed(64,SumSimplerAuxQ(u_,v_),
    Condition(Or(SumSimplerAuxQ(First(u),v),SumSimplerAuxQ(Rest(u),v)),SumQ(u)));
ISetDelayed(65,SumSimplerAuxQ(u_,v_),
    And(UnsameQ(v,C0),SameQ(NonnumericFactors(u),NonnumericFactors(v)),Or(Less(Times(NumericFactor(u),Power(NumericFactor(v),-1)),CN1D2),And(Equal(Times(NumericFactor(u),Power(NumericFactor(v),-1)),CN1D2),Less(NumericFactor(u),C0)))));
ISetDelayed(66,SimplerIntegrandQ(u_,v_,x_Symbol),
    Module(List(Set($s("lst"),CancelCommonFactors(u,v)),$s("u1"),$s("v1")),CompoundExpression(Set($s("u1"),Part($s("lst"),C1)),Set($s("v1"),Part($s("lst"),C2)),If(Less(LeafCount($s("u1")),Times(QQ(3L,4L),LeafCount($s("v1")))),True,If(RationalFunctionQ($s("u1"),x),If(RationalFunctionQ($s("v1"),x),Less(Apply(Plus,RationalFunctionExponents($s("u1"),x)),Apply(Plus,RationalFunctionExponents($s("v1"),x))),True),False)))));
ISetDelayed(67,CancelCommonFactors(u_,v_),
    If(ProductQ(u),If(ProductQ(v),If(MemberQ(v,First(u)),CancelCommonFactors(Rest(u),DeleteCases(v,First(u),C1,C1)),$(Function(List(Times(First(u),Part(Slot1,C1)),Part(Slot1,C2))),CancelCommonFactors(Rest(u),v))),If(MemberQ(u,v),List(DeleteCases(u,v,C1,C1),C1),List(u,v))),If(ProductQ(v),If(MemberQ(v,u),List(C1,DeleteCases(v,u,C1,C1)),List(u,v)),List(u,v))));
ISetDelayed(68,BinomialDegree(u_,x_Symbol),
    Part(BinomialParts(u,x),C3));
ISetDelayed(69,BinomialParts(u_,x_Symbol),
    If(PolynomialQ(u,x),If(Greater(Exponent(u,x),C0),With(List(Set($s("lst"),Exponent(u,x,List))),If(Equal(Length($s("lst")),C1),List(C0,Coefficient(u,x,Exponent(u,x)),Exponent(u,x)),If(And(Equal(Length($s("lst")),C2),Equal(Part($s("lst"),C1),C0)),List(Coefficient(u,x,C0),Coefficient(u,x,Exponent(u,x)),Exponent(u,x)),False))),False),If(PowerQ(u),If(And(SameQ(Part(u,C1),x),FreeQ(Part(u,C2),x)),List(C0,C1,Part(u,C2)),False),If(ProductQ(u),If(FreeQ(First(u),x),With(List(Set($s("lst2"),BinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),False,List(Times(First(u),Part($s("lst2"),C1)),Times(First(u),Part($s("lst2"),C2)),Part($s("lst2"),C3)))),If(FreeQ(Rest(u),x),With(List(Set($s("lst1"),BinomialParts(First(u),x))),If(AtomQ($s("lst1")),False,List(Times(Rest(u),Part($s("lst1"),C1)),Times(Rest(u),Part($s("lst1"),C2)),Part($s("lst1"),C3)))),With(List(Set($s("lst1"),BinomialParts(First(u),x))),If(AtomQ($s("lst1")),False,With(List(Set($s("lst2"),BinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),False,With(List(Set(a,Part($s("lst1"),C1)),Set(b,Part($s("lst1"),C2)),Set(m,Part($s("lst1"),C3)),Set(c,Part($s("lst2"),C1)),Set(d,Part($s("lst2"),C2)),Set(n,Part($s("lst2"),C3))),If(EqQ(a,C0),If(EqQ(c,C0),List(C0,Times(b,d),Plus(m,n)),If(EqQ(Plus(m,n),C0),List(Times(b,d),Times(b,c),m),False)),If(EqQ(c,C0),If(EqQ(Plus(m,n),C0),List(Times(b,d),Times(a,d),n),False),If(And(EqQ(m,n),EqQ(Plus(Times(a,d),Times(b,c)),C0)),List(Times(a,c),Times(b,d),Times(C2,m)),False)))))))))),If(SumQ(u),If(FreeQ(First(u),x),With(List(Set($s("lst2"),BinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),False,List(Plus(First(u),Part($s("lst2"),C1)),Part($s("lst2"),C2),Part($s("lst2"),C3)))),If(FreeQ(Rest(u),x),With(List(Set($s("lst1"),BinomialParts(First(u),x))),If(AtomQ($s("lst1")),False,List(Plus(Rest(u),Part($s("lst1"),C1)),Part($s("lst1"),C2),Part($s("lst1"),C3)))),With(List(Set($s("lst1"),BinomialParts(First(u),x))),If(AtomQ($s("lst1")),False,With(List(Set($s("lst2"),BinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),False,If(EqQ(Part($s("lst1"),C3),Part($s("lst2"),C3)),List(Plus(Part($s("lst1"),C1),Part($s("lst2"),C1)),Plus(Part($s("lst1"),C2),Part($s("lst2"),C2)),Part($s("lst1"),C3)),False))))))),False)))));
ISetDelayed(70,TrinomialDegree(u_,x_Symbol),
    Part(TrinomialParts(u,x),C4));
ISetDelayed(71,TrinomialParts(u_,x_Symbol),
    If(PolynomialQ(u,x),With(List(Set($s("lst"),CoefficientList(u,x))),If(Or(Less(Length($s("lst")),C3),EvenQ(Length($s("lst"))),EqQ(Part($s("lst"),Times(C1D2,Plus(Length($s("lst")),C1))),C0)),False,Catch(CompoundExpression(Scan(Function(If(EqQ(Slot1,C0),Null,Throw(False))),Drop(Drop(Drop($s("lst"),List(Times(C1D2,Plus(Length($s("lst")),C1)))),C1),CN1)),List(First($s("lst")),Part($s("lst"),Times(C1D2,Plus(Length($s("lst")),C1))),Last($s("lst")),Times(C1D2,Plus(Length($s("lst")),Negate(C1)))))))),If(PowerQ(u),If(EqQ(Part(u,C2),C2),With(List(Set($s("lst"),BinomialParts(Part(u,C1),x))),If(Or(AtomQ($s("lst")),EqQ(Part($s("lst"),C1),C0)),False,List(Sqr(Part($s("lst"),C1)),Times(C2,Part($s("lst"),C1),Part($s("lst"),C2)),Sqr(Part($s("lst"),C2)),Part($s("lst"),C3)))),False),If(ProductQ(u),If(FreeQ(First(u),x),With(List(Set($s("lst2"),TrinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),False,List(Times(First(u),Part($s("lst2"),C1)),Times(First(u),Part($s("lst2"),C2)),Times(First(u),Part($s("lst2"),C3)),Part($s("lst2"),C4)))),If(FreeQ(Rest(u),x),With(List(Set($s("lst1"),TrinomialParts(First(u),x))),If(AtomQ($s("lst1")),False,List(Times(Rest(u),Part($s("lst1"),C1)),Times(Rest(u),Part($s("lst1"),C2)),Times(Rest(u),Part($s("lst1"),C3)),Part($s("lst1"),C4)))),With(List(Set($s("lst1"),BinomialParts(First(u),x))),If(AtomQ($s("lst1")),False,With(List(Set($s("lst2"),BinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),False,With(List(Set(a,Part($s("lst1"),C1)),Set(b,Part($s("lst1"),C2)),Set(m,Part($s("lst1"),C3)),Set(c,Part($s("lst2"),C1)),Set(d,Part($s("lst2"),C2)),Set(n,Part($s("lst2"),C3))),If(And(EqQ(m,n),NeQ(Plus(Times(a,d),Times(b,c)),C0)),List(Times(a,c),Plus(Times(a,d),Times(b,c)),Times(b,d),m),False)))))))),If(SumQ(u),If(FreeQ(First(u),x),With(List(Set($s("lst2"),TrinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),False,List(Plus(First(u),Part($s("lst2"),C1)),Part($s("lst2"),C2),Part($s("lst2"),C3),Part($s("lst2"),C4)))),If(FreeQ(Rest(u),x),With(List(Set($s("lst1"),TrinomialParts(First(u),x))),If(AtomQ($s("lst1")),False,List(Plus(Rest(u),Part($s("lst1"),C1)),Part($s("lst1"),C2),Part($s("lst1"),C3),Part($s("lst1"),C4)))),With(List(Set($s("lst1"),TrinomialParts(First(u),x))),If(AtomQ($s("lst1")),With(List(Set($s("lst3"),BinomialParts(First(u),x))),If(AtomQ($s("lst3")),False,With(List(Set($s("lst2"),TrinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),With(List(Set($s("lst4"),BinomialParts(Rest(u),x))),If(AtomQ($s("lst4")),False,If(EqQ(Part($s("lst3"),C3),Times(C2,Part($s("lst4"),C3))),List(Plus(Part($s("lst3"),C1),Part($s("lst4"),C1)),Part($s("lst4"),C2),Part($s("lst3"),C2),Part($s("lst4"),C3)),If(EqQ(Part($s("lst4"),C3),Times(C2,Part($s("lst3"),C3))),List(Plus(Part($s("lst3"),C1),Part($s("lst4"),C1)),Part($s("lst3"),C2),Part($s("lst4"),C2),Part($s("lst3"),C3)),False)))),If(And(EqQ(Part($s("lst3"),C3),Part($s("lst2"),C4)),NeQ(Plus(Part($s("lst3"),C2),Part($s("lst2"),C2)),C0)),List(Plus(Part($s("lst3"),C1),Part($s("lst2"),C1)),Plus(Part($s("lst3"),C2),Part($s("lst2"),C2)),Part($s("lst2"),C3),Part($s("lst2"),C4)),If(And(EqQ(Part($s("lst3"),C3),Times(C2,Part($s("lst2"),C4))),NeQ(Plus(Part($s("lst3"),C2),Part($s("lst2"),C3)),C0)),List(Plus(Part($s("lst3"),C1),Part($s("lst2"),C1)),Part($s("lst2"),C2),Plus(Part($s("lst3"),C2),Part($s("lst2"),C3)),Part($s("lst2"),C4)),False)))))),With(List(Set($s("lst2"),TrinomialParts(Rest(u),x))),If(AtomQ($s("lst2")),With(List(Set($s("lst4"),BinomialParts(Rest(u),x))),If(AtomQ($s("lst4")),False,If(And(EqQ(Part($s("lst4"),C3),Part($s("lst1"),C4)),NeQ(Plus(Part($s("lst1"),C2),Part($s("lst4"),C2)),C0)),List(Plus(Part($s("lst1"),C1),Part($s("lst4"),C1)),Plus(Part($s("lst1"),C2),Part($s("lst4"),C2)),Part($s("lst1"),C3),Part($s("lst1"),C4)),If(And(EqQ(Part($s("lst4"),C3),Times(C2,Part($s("lst1"),C4))),NeQ(Plus(Part($s("lst1"),C3),Part($s("lst4"),C2)),C0)),List(Plus(Part($s("lst1"),C1),Part($s("lst4"),C1)),Part($s("lst1"),C2),Plus(Part($s("lst1"),C3),Part($s("lst4"),C2)),Part($s("lst1"),C4)),False)))),If(And(EqQ(Part($s("lst1"),C4),Part($s("lst2"),C4)),NeQ(Plus(Part($s("lst1"),C2),Part($s("lst2"),C2)),C0),NeQ(Plus(Part($s("lst1"),C3),Part($s("lst2"),C3)),C0)),List(Plus(Part($s("lst1"),C1),Part($s("lst2"),C1)),Plus(Part($s("lst1"),C2),Part($s("lst2"),C2)),Plus(Part($s("lst1"),C3),Part($s("lst2"),C3)),Part($s("lst1"),C4)),False))))))),False)))));
ISetDelayed(72,GeneralizedBinomialDegree(u_,x_Symbol),
    $(Function(Plus(Part(Slot1,C3),Negate(Part(Slot1,C4)))),GeneralizedBinomialParts(u,x)));
ISetDelayed(73,GeneralizedBinomialParts(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),x_Symbol),
    Condition(List(a,b,n,q),And(FreeQ(List(a,b,n,q),x),PosQ(Plus(n,Negate(q))))));
ISetDelayed(74,GeneralizedBinomialParts(Times(a_,u_),x_Symbol),
    Condition(With(List(Set($s("lst"),GeneralizedBinomialParts(u,x))),Condition(List(Times(a,Part($s("lst"),C1)),Times(a,Part($s("lst"),C2)),Part($s("lst"),C3),Part($s("lst"),C4)),ListQ($s("lst")))),FreeQ(a,x)));
ISetDelayed(75,GeneralizedBinomialParts(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set($s("lst"),GeneralizedBinomialParts(u,x))),Condition(List(Part($s("lst"),C1),Part($s("lst"),C2),Plus(m,Part($s("lst"),C3)),Plus(m,Part($s("lst"),C4))),And(ListQ($s("lst")),NeQ(Plus(m,Part($s("lst"),C3)),C0),NeQ(Plus(m,Part($s("lst"),C4)),C0)))),FreeQ(m,x)));
ISetDelayed(76,GeneralizedBinomialParts(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set($s("lst"),BinomialParts(u,x))),Condition(List(Part($s("lst"),C1),Part($s("lst"),C2),Plus(m,Part($s("lst"),C3)),m),And(ListQ($s("lst")),NeQ(Plus(m,Part($s("lst"),C3)),C0)))),FreeQ(m,x)));
ISetDelayed(77,GeneralizedBinomialParts(u_,x_Symbol),
    False);
ISetDelayed(78,GeneralizedTrinomialDegree(u_,x_Symbol),
    $(Function(Plus(Part(Slot1,C4),Negate(Part(Slot1,C5)))),GeneralizedTrinomialParts(u,x)));
ISetDelayed(79,GeneralizedTrinomialParts(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),x_Symbol),
    Condition(List(a,b,c,n,q),And(FreeQ(List(a,b,c,n,q),x),EqQ(r,Plus(Times(C2,n),Negate(q))))));
ISetDelayed(80,GeneralizedTrinomialParts(Times(a_,u_),x_Symbol),
    Condition(With(List(Set($s("lst"),GeneralizedTrinomialParts(u,x))),Condition(List(Times(a,Part($s("lst"),C1)),Times(a,Part($s("lst"),C2)),Times(a,Part($s("lst"),C3)),Part($s("lst"),C4),Part($s("lst"),C5)),ListQ($s("lst")))),FreeQ(a,x)));
ISetDelayed(81,GeneralizedTrinomialParts(u_,x_Symbol),
    Condition(With(List(Set($s("lst"),Expon(u,x,List))),If(And(Equal(Length($s("lst")),C3),NeQ(Part($s("lst"),C0),C0),EqQ(Part($s("lst"),C3),Plus(Times(C2,Part($s("lst"),C2)),Negate(Part($s("lst"),C1))))),List(Coeff(u,x,Part($s("lst"),C1)),Coeff(u,x,Part($s("lst"),C2)),Coeff(u,x,Part($s("lst"),C3)),Part($s("lst"),C2),Part($s("lst"),C1)),False)),PolyQ(u,x)));
ISetDelayed(82,GeneralizedTrinomialParts(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set($s("lst"),GeneralizedTrinomialParts(u,x))),Condition(List(Part($s("lst"),C1),Part($s("lst"),C2),Part($s("lst"),C3),Plus(m,Part($s("lst"),C4)),Plus(m,Part($s("lst"),C5))),And(ListQ($s("lst")),NeQ(Plus(m,Part($s("lst"),C4)),C0),NeQ(Plus(m,Part($s("lst"),C5)),C0)))),FreeQ(m,x)));
ISetDelayed(83,GeneralizedTrinomialParts(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set($s("lst"),TrinomialParts(u,x))),Condition(List(Part($s("lst"),C1),Part($s("lst"),C2),Part($s("lst"),C3),Plus(m,Part($s("lst"),C4)),m),And(ListQ($s("lst")),NeQ(Plus(m,Part($s("lst"),C4)),C0)))),FreeQ(m,x)));
ISetDelayed(84,GeneralizedTrinomialParts(u_,x_Symbol),
    False);
ISetDelayed(85,IntPart(Times(m_,u_),Optional(n_,C1)),
    Condition(IntPart(u,Times(m,n)),RationalQ(m)));
ISetDelayed(86,IntPart(u_,Optional(n_,C1)),
    If(RationalQ(u),IntegerPart(Times(n,u)),If(SumQ(u),Map(Function(IntPart(Slot1,n)),u),C0)));
ISetDelayed(87,FracPart(Times(m_,u_),Optional(n_,C1)),
    Condition(FracPart(u,Times(m,n)),RationalQ(m)));
ISetDelayed(88,FracPart(u_,Optional(n_,C1)),
    If(RationalQ(u),FractionalPart(Times(n,u)),If(SumQ(u),Map(Function(FracPart(Slot1,n)),u),Times(n,u))));
ISetDelayed(89,NumericFactor(u_),
    If(NumberQ(u),If(EqQ(Im(u),C0),u,If(EqQ(Re(u),C0),Im(u),C1)),If(PowerQ(u),If(And(RationalQ(Part(u,C1)),FractionQ(Part(u,C2))),If(Greater(Part(u,C2),C0),Power(Denominator(Part(u,C1)),-1),Power(Denominator(Power(Part(u,C1),-1)),-1)),C1),If(ProductQ(u),Map($rubi("NumericFactor"),u),If(SumQ(u),If(Less(LeafCount(u),ZZ(50L)),$(Function(If(SumQ(Slot1),C1,NumericFactor(Slot1))),ContentFactor(u)),With(List(Set(m,NumericFactor(First(u))),Set(n,NumericFactor(Rest(u)))),If(And(Less(m,C0),Less(n,C0)),Negate(GCD(Negate(m),Negate(n))),GCD(m,n)))),C1)))));
ISetDelayed(90,NonnumericFactors(u_),
    If(NumberQ(u),If(EqQ(Im(u),C0),C1,If(EqQ(Re(u),C0),CI,u)),If(PowerQ(u),If(And(RationalQ(Part(u,C1)),FractionQ(Part(u,C2))),Times(u,Power(NumericFactor(u),-1)),u),If(ProductQ(u),Map($rubi("NonnumericFactors"),u),If(SumQ(u),If(Less(LeafCount(u),ZZ(50L)),$(Function(If(SumQ(Slot1),u,NonnumericFactors(Slot1))),ContentFactor(u)),With(List(Set(n,NumericFactor(u))),Map(Function(Times(Slot1,Power(n,-1))),u))),u)))));
ISetDelayed(91,RemoveContent(u_,x_Symbol),
    With(List(Set(v,NonfreeFactors(u,x))),With(List(Set(w,Together(v))),If(EqQ(FreeFactors(w,x),C1),RemoveContentAux(v,x),RemoveContentAux(NonfreeFactors(w,x),x)))));
ISetDelayed(92,RemoveContentAux(Plus(Times(Power(a_,m_),u_DEFAULT),Times(b_,v_DEFAULT)),x_Symbol),
    Condition(If(Greater(m,C1),RemoveContentAux(Plus(Times(Power(a,Plus(m,Negate(C1))),u),Negate(v)),x),RemoveContentAux(Plus(u,Times(CN1,Power(a,Plus(C1,Negate(m))),v)),x)),And(IntegersQ(a,b),Equal(Plus(a,b),C0),RationalQ(m))));
ISetDelayed(93,RemoveContentAux(Plus(Times(Power(a_,m_DEFAULT),u_DEFAULT),Times(Power(a_,n_DEFAULT),v_DEFAULT)),x_Symbol),
    Condition(RemoveContentAux(Plus(u,Times(Power(a,Plus(n,Negate(m))),v)),x),And(FreeQ(a,x),RationalQ(m,n),GreaterEqual(Plus(n,Negate(m)),C0))));
ISetDelayed(94,RemoveContentAux(Plus(Times(Power(a_,m_DEFAULT),u_DEFAULT),Times(Power(a_,n_DEFAULT),v_DEFAULT),Times(Power(a_,p_DEFAULT),w_DEFAULT)),x_Symbol),
    Condition(RemoveContentAux(Plus(u,Times(Power(a,Plus(n,Negate(m))),v),Times(Power(a,Plus(p,Negate(m))),w)),x),And(FreeQ(a,x),RationalQ(m,n,p),GreaterEqual(Plus(n,Negate(m)),C0),GreaterEqual(Plus(p,Negate(m)),C0))));
ISetDelayed(95,RemoveContentAux(u_,x_Symbol),
    If(And(SumQ(u),NegQ(First(u))),Negate(u),u));
ISetDelayed(96,FreeFactors(u_,x_),
    If(ProductQ(u),Map(Function(If(FreeQ(Slot1,x),Slot1,C1)),u),If(FreeQ(u,x),u,C1)));
ISetDelayed(97,NonfreeFactors(u_,x_),
    If(ProductQ(u),Map(Function(If(FreeQ(Slot1,x),C1,Slot1)),u),If(FreeQ(u,x),C1,u)));
ISetDelayed(98,FreeTerms(u_,x_),
    If(SumQ(u),Map(Function(If(FreeQ(Slot1,x),Slot1,C0)),u),If(FreeQ(u,x),u,C0)));
ISetDelayed(99,NonfreeTerms(u_,x_),
    If(SumQ(u),Map(Function(If(FreeQ(Slot1,x),C0,Slot1)),u),If(FreeQ(u,x),C0,u)));
ISetDelayed(100,Expon($p("expr"),$p("form")),
    Exponent(Together($s("expr")),$s("form")));
ISetDelayed(101,Expon($p("expr"),$p("form"),h_),
    Exponent(Together($s("expr")),$s("form"),h));
ISetDelayed(102,Coeff($p("expr"),$p("form")),
    Coefficient(Together($s("expr")),$s("form")));
ISetDelayed(103,Coeff($p("expr"),$p("form"),n_),
    With(List(Set($s("coef1"),Coefficient($s("expr"),$s("form"),n)),Set($s("coef2"),Coefficient(Together($s("expr")),$s("form"),n))),If(SameQ(Simplify(Plus($s("coef1"),Negate($s("coef2")))),C0),$s("coef1"),$s("coef2"))));
ISetDelayed(104,LeadTerm(u_),
    If(SumQ(u),First(u),u));
ISetDelayed(105,RemainingTerms(u_),
    If(SumQ(u),Rest(u),C0));
ISetDelayed(106,LeadFactor(u_),
    If(ProductQ(u),LeadFactor(First(u)),If(And(ComplexNumberQ(u),SameQ(Re(u),C0)),If(SameQ(Im(u),C1),u,LeadFactor(Im(u))),u)));
ISetDelayed(107,RemainingFactors(u_),
    If(ProductQ(u),Times(RemainingFactors(First(u)),Rest(u)),If(And(ComplexNumberQ(u),SameQ(Re(u),C0)),If(SameQ(Im(u),C1),C1,Times(CI,RemainingFactors(Im(u)))),C1)));
ISetDelayed(108,LeadBase(u_),
    With(List(Set(v,LeadFactor(u))),If(PowerQ(v),Part(v,C1),v)));
ISetDelayed(109,LeadDegree(u_),
    With(List(Set(v,LeadFactor(u))),If(PowerQ(v),Part(v,C2),C1)));
ISetDelayed(110,$($s("§numer"),Power($p(m, Integer),$p(n,Rational))),
    Condition(C1,Less(n,C0)));
ISetDelayed(111,$($s("§numer"),Times(u_,v_)),
    Times($($s("§numer"),u),$($s("§numer"),v)));
ISetDelayed(112,$($s("§numer"),u_),
    Numerator(u));
ISetDelayed(113,$($s("§denom"),Power($p(m, Integer),$p(n,Rational))),
    Condition(Power(m,Negate(n)),Less(n,C0)));
ISetDelayed(114,$($s("§denom"),Times(u_,v_)),
    Times($($s("§denom"),u),$($s("§denom"),v)));
ISetDelayed(115,$($s("§denom"),u_),
    Denominator(u));
ISetDelayed(116,LinearQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(PolyQ(Slot1,x,C1),Null,Throw(False))),u),True)),PolyQ(u,x,C1)));
ISetDelayed(117,QuadraticProductQ(u_,x_Symbol),
    And(ProductQ(NonfreeFactors(u,x)),Catch(CompoundExpression(Scan(Function(If(MatchQ(Slot1,Condition(Power($p("§pm"),m_DEFAULT),And(PolyQ($s("§pm"),x),LessEqual(Expon($s("§pm"),x),C2),IntegerQ(m)))),Null,Throw(False))),NonfreeFactors(u,x)),True))));
ISetDelayed(118,PowerOfLinearQ(Power(u_,m_DEFAULT),x_Symbol),
    And(FreeQ(m,x),PolynomialQ(u,x),If(IntegerQ(m),MatchQ(FactorSquareFree(u),Condition(Power(w_,n_DEFAULT),And(FreeQ(n,x),LinearQ(w,x)))),LinearQ(u,x))));
ISetDelayed(119,QuadraticQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(QuadraticQ(Slot1,x)),Throw(False))),u),True)),And(PolyQ(u,x,C2),Not(And(SameQ(Coefficient(u,x,C0),C0),SameQ(Coefficient(u,x,C1),C0))))));
ISetDelayed(120,LinearPairQ(u_,v_,x_Symbol),
    And(LinearQ(u,x),LinearQ(v,x),NeQ(u,x),EqQ(Plus(Times(Coefficient(u,x,C0),Coefficient(v,x,C1)),Times(CN1,Coefficient(u,x,C1),Coefficient(v,x,C0))),C0)));
ISetDelayed(121,MonomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(MonomialQ(Slot1,x),Null,Throw(False))),u),True)),MatchQ(u,Condition(Times(a_DEFAULT,Power(x,n_DEFAULT)),FreeQ(List(a,n),x)))));
  }
}
}
