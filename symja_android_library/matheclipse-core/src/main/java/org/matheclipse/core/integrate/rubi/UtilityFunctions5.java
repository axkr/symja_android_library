package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions5 { 
  public static IAST RULES = List( 
ISetDelayed(42,PolyQ(u_,x_Symbol,n_),
    If(PolynomialQ(u,x),And(EqQ(Exponent(u,x),n),NeQ(Coefficient(u,x,n),C0)),With(list(Set(v,Together(u))),And(PolynomialQ(v,x),EqQ(Exponent(v,x),n),NeQ(Coefficient(v,x,n),C0))))),
ISetDelayed(43,PolyQ(u_,Power(x_Symbol,v_),n_),
    And(PolyQ(u,Power(x,v)),EqQ(Expon(u,Power(x,v)),n),NeQ(Coeff(u,Power(x,v),n),C0))),
ISetDelayed(44,PolyQ(u_,x_Symbol),
    Or(PolynomialQ(u,x),PolynomialQ(Together(u),x))),
ISetDelayed(45,PolyQ(u_,Power(x_Symbol,$p(n, Integer))),
    Condition(If(PolynomialQ(u,x),PolynomialQ(u,Power(x,n)),With(list(Set(v,Together(u))),And(PolynomialQ(v,x),PolynomialQ(v,Power(x,n))))),Greater(n,C0))),
ISetDelayed(46,PolyQ(u_,Power(x_Symbol,v_)),
    Condition(If(SameQ(Quiet(PolynomialQ(u,Power(x,v))),True),FreeQ(CoefficientList(u,Power(x,v)),x),With(list(Set(w,Together(u))),And(SameQ(Quiet(PolynomialQ(w,Power(x,v))),True),FreeQ(CoefficientList(w,Power(x,v)),x)))),And(NonsumQ(v),FreeQ(v,x)))),
ISetDelayed(47,PolyQ(u_,v_),
    False),
ISetDelayed(48,ProperPolyQ(u_,x_Symbol),
    And(PolyQ(u,x),NeQ(Coeff(u,x,C0),C0))),
ISetDelayed(49,BinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(BinomialQ(Slot1,x)),Throw(False))),u),True)),ListQ(BinomialParts(u,x)))),
ISetDelayed(50,BinomialQ(u_,x_Symbol,n_),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(BinomialQ(Slot1,x,n)),Throw(False))),u),True)),$(Function(And(ListQ(Slot1),SameQ(Part(Slot1,C3),n))),BinomialParts(u,x)))),
ISetDelayed(51,TrinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(TrinomialQ(Slot1,x)),Throw(False))),u),True)),And(ListQ(TrinomialParts(u,x)),Not(QuadraticQ(u,x)),Not(MatchQ(u,Condition(Sqr(w_),BinomialQ(w,x))))))),
ISetDelayed(52,GeneralizedBinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(GeneralizedBinomialQ(Slot1,x)),Throw(False))),u),True)),ListQ(GeneralizedBinomialParts(u,x)))),
ISetDelayed(53,GeneralizedTrinomialQ(u_,x_Symbol),
    If(ListQ(u),Catch(CompoundExpression(Scan(Function(If(Not(GeneralizedTrinomialQ(Slot1,x)),Throw(False))),u),True)),ListQ(GeneralizedTrinomialParts(u,x)))),
ISetDelayed(54,PosQ(u_),
    PosAux(TogetherSimplify(u))),
ISetDelayed(55,PosAux(u_),
    If(NumberQ(u),If(SameQ(Head(u),Complex),If(EqQ(Re(u),C0),PosAux(Im(u)),PosAux(Re(u))),Greater(u,C0)),If(NumericQ(u),With(list(Set(v,Simplify(Re(u)))),If(NumberQ(v),If(EqQ(v,C0),PosAux(Simplify(Im(u))),Greater(v,C0)),With(list(Set(w,N(u))),And(NumberQ(w),PosAux(w))))),With(list(Set(v,Refine(Greater(u,C0)))),If(Or(SameQ(v,True),SameQ(v,False)),v,If(PowerQ(u),If(IntegerQ(Part(u,C2)),Or(EvenQ(Part(u,C2)),PosAux(Part(u,C1))),True),If(ProductQ(u),If(PosAux(First(u)),PosAux(Rest(u)),Not(PosAux(Rest(u)))),If(SumQ(u),PosAux(First(u)),True)))))))),
ISetDelayed(56,NegQ(u_),
    And(Not(PosQ(u)),NeQ(u,C0))),
ISetDelayed(57,NiceSqrtQ(u_),
    And(Not(LtQ(u,C0)),NiceSqrtAuxQ(u))),
ISetDelayed(58,NiceSqrtAuxQ(u_),
    If(RationalQ(u),Greater(u,C0),If(PowerQ(u),EvenQ(Part(u,C2)),If(ProductQ(u),And(NiceSqrtAuxQ(First(u)),NiceSqrtAuxQ(Rest(u))),If(SumQ(u),$(Function(And(NonsumQ(Slot1),NiceSqrtAuxQ(Slot1))),Simplify(u)),False))))),
ISetDelayed(59,PerfectSquareQ(u_),
    If(RationalQ(u),And(Greater(u,C0),RationalQ(Sqrt(u))),If(PowerQ(u),EvenQ(Part(u,C2)),If(ProductQ(u),And(PerfectSquareQ(First(u)),PerfectSquareQ(Rest(u))),If(SumQ(u),$(Function(And(NonsumQ(Slot1),PerfectSquareQ(Slot1))),Simplify(u)),False))))),
ISetDelayed(60,SimplerQ(u_,v_),
    If(IntegerQ(u),If(IntegerQ(v),If(Equal(u,v),False,If(Equal(u,Negate(v)),Less(v,C0),Less(Abs(u),Abs(v)))),True),If(IntegerQ(v),False,If(FractionQ(u),If(FractionQ(v),If(Equal(Denominator(u),Denominator(v)),SimplerQ(Numerator(u),Numerator(v)),Less(Denominator(u),Denominator(v))),True),If(FractionQ(v),False,If(And(Or(SameQ(Re(u),C0),SameQ(Re(u),CD0)),Or(SameQ(Re(v),C0),SameQ(Re(v),CD0))),SimplerQ(Im(u),Im(v)),If(ComplexNumberQ(u),If(ComplexNumberQ(v),If(Equal(Re(u),Re(v)),SimplerQ(Im(u),Im(v)),SimplerQ(Re(u),Re(v))),False),If(NumberQ(u),If(NumberQ(v),OrderedQ(list(u,v)),True),If(NumberQ(v),False,If(AtomQ(u),If(AtomQ(v),OrderedQ(list(u,v)),True),If(AtomQ(v),False,If(SameQ(Head(u),Head(v)),If(Equal(Length(u),Length(v)),If(Equal(LeafCount(u),LeafCount(v)),Catch(CompoundExpression(Do(If(SameQ(Part(u,$s("ii")),Part(v,$s("ii"))),Null,Throw(SimplerQ(Part(u,$s("ii")),Part(v,$s("ii"))))),list($s("ii"),Length(u))),False)),Less(LeafCount(u),LeafCount(v))),Less(Length(u),Length(v))),If(Equal(LeafCount(u),LeafCount(v)),Not(OrderedQ(list(v,u))),Less(LeafCount(u),LeafCount(v))))))))))))))),
ISetDelayed(61,SimplerSqrtQ(u_,v_),
    If(And(LtQ(v,C0),Not(LtQ(u,C0))),True,If(And(LtQ(u,C0),Not(LtQ(v,C0))),False,With(list(Set($s("sqrtu"),Rt(u,C2)),Set($s("sqrtv"),Rt(v,C2))),If(IntegerQ($s("sqrtu")),If(IntegerQ($s("sqrtv")),Less($s("sqrtu"),$s("sqrtv")),True),If(IntegerQ($s("sqrtv")),False,If(RationalQ($s("sqrtu")),If(RationalQ($s("sqrtv")),Less($s("sqrtu"),$s("sqrtv")),True),If(RationalQ($s("sqrtv")),False,If(PosQ(u),If(PosQ(v),Less(LeafCount($s("sqrtu")),LeafCount($s("sqrtv"))),True),If(PosQ(v),False,If(Less(LeafCount($s("sqrtu")),LeafCount($s("sqrtv"))),True,If(Less(LeafCount($s("sqrtv")),LeafCount($s("sqrtu"))),False,Not(OrderedQ(list(v,u)))))))))))))))
  );
}
