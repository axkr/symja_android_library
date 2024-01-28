package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions23 { 
  public static IAST RULES = List( 
ISetDelayed(288,FactorOrder(u_,v_),
    If(SameQ(u,C1),If(SameQ(v,C1),C0,CN1),If(SameQ(v,C1),C1,Order(u,v)))),
ISetDelayed(289,Smallest($p("num1"),$p("num2")),
    If(Greater($s("num1"),C0),If(Greater($s("num2"),C0),Min($s("num1"),$s("num2")),C0),If(Greater($s("num2"),C0),C0,Max($s("num1"),$s("num2"))))),
ISetDelayed(290,Smallest($p("lst",List)),
    Module(list(Set($s("num"),Part($s("lst"),C1))),CompoundExpression(Scan(Function(Set($s("num"),Smallest($s("num"),Slot1))),Rest($s("lst"))),$s("num")))),
ISetDelayed(291,MonomialFactor(u_,x_Symbol),
    If(AtomQ(u),If(SameQ(u,x),list(C1,C1),list(C0,u)),If(PowerQ(u),If(IntegerQ(Part(u,C2)),With(list(Set($s("lst"),MonomialFactor(Part(u,C1),x))),list(Times(Part($s("lst"),C1),Part(u,C2)),Power(Part($s("lst"),C2),Part(u,C2)))),If(And(SameQ(Part(u,C1),x),FreeQ(Part(u,C2),x)),list(Part(u,C2),C1),list(C0,u))),If(ProductQ(u),With(list(Set($s("lst1"),MonomialFactor(First(u),x)),Set($s("lst2"),MonomialFactor(Rest(u),x))),list(Plus(Part($s("lst1"),C1),Part($s("lst2"),C1)),Times(Part($s("lst1"),C2),Part($s("lst2"),C2)))),If(SumQ(u),Module(list($s("lst"),$s("deg")),CompoundExpression(Set($s("lst"),Map(Function(MonomialFactor(Slot1,x)),Apply(List,u))),Set($s("deg"),Part($s("lst"),C1,C1)),Scan(Function(Set($s("deg"),MinimumDegree($s("deg"),Part(Slot1,C1)))),Rest($s("lst"))),If(Or(EqQ($s("deg"),C0),And(RationalQ($s("deg")),Less($s("deg"),C0))),list(C0,u),list($s("deg"),Apply(Plus,Map(Function(Times(Power(x,Subtract(Part(Slot1,C1),$s("deg"))),Part(Slot1,C2))),$s("lst"))))))),list(C0,u)))))),
ISetDelayed(292,MinimumDegree($p("deg1"),$p("deg2")),
    If(RationalQ($s("deg1")),If(RationalQ($s("deg2")),Min($s("deg1"),$s("deg2")),$s("deg1")),If(RationalQ($s("deg2")),$s("deg2"),With(list(Set($s("deg"),Simplify(Subtract($s("deg1"),$s("deg2"))))),If(RationalQ($s("deg")),If(Greater($s("deg"),C0),$s("deg2"),$s("deg1")),If(OrderedQ(list($s("deg1"),$s("deg2"))),$s("deg1"),$s("deg2"))))))),
ISetDelayed(293,ConstantFactor(u_,x_Symbol),
    If(FreeQ(u,x),list(u,C1),If(AtomQ(u),list(C1,u),If(And(PowerQ(u),FreeQ(Part(u,C2),x)),Module(list(Set($s("lst"),ConstantFactor(Part(u,C1),x)),$s("tmp")),If(IntegerQ(Part(u,C2)),list(Power(Part($s("lst"),C1),Part(u,C2)),Power(Part($s("lst"),C2),Part(u,C2))),CompoundExpression(Set($s("tmp"),PositiveFactors(Part($s("lst"),C1))),If(SameQ($s("tmp"),C1),list(C1,u),list(Power($s("tmp"),Part(u,C2)),Power(Times(NonpositiveFactors(Part($s("lst"),C1)),Part($s("lst"),C2)),Part(u,C2))))))),If(ProductQ(u),With(list(Set($s("lst"),Map(Function(ConstantFactor(Slot1,x)),Apply(List,u)))),list(Apply(Times,Map(First,$s("lst"))),Apply(Times,Map(Function(Part(Slot1,C2)),$s("lst"))))),If(SumQ(u),With(list(Set($s("lst1"),Map(Function(ConstantFactor(Slot1,x)),Apply(List,u)))),If(Apply(SameQ,Map(Function(Part(Slot1,C2)),$s("lst1"))),list(Apply(Plus,Map(First,$s("lst1"))),Part($s("lst1"),C1,C2)),With(list(Set($s("lst2"),CommonFactors(Map(First,$s("lst1"))))),list(First($s("lst2")),Apply(Plus,Map2(Times,Rest($s("lst2")),Map(Function(Part(Slot1,C2)),$s("lst1")))))))),list(C1,u))))))),
ISetDelayed(294,PositiveFactors(u_),
    If(EqQ(u,C0),C1,If(RationalQ(u),Abs(u),If(GtQ(u,C0),u,If(ProductQ(u),Map($rubi("PositiveFactors"),u),C1))))),
ISetDelayed(295,NonpositiveFactors(u_),
    If(EqQ(u,C0),u,If(RationalQ(u),Sign(u),If(GtQ(u,C0),C1,If(ProductQ(u),Map($rubi("NonpositiveFactors"),u),u))))),
ISetDelayed(296,PolynomialInQ(u_,v_,x_Symbol),
    PolynomialInAuxQ(u,NonfreeFactors(NonfreeTerms(v,x),x),x)),
ISetDelayed(297,PolynomialInAuxQ(u_,v_,x_),
    If(SameQ(u,v),True,If(AtomQ(u),UnsameQ(u,x),If(PowerQ(u),If(And(PowerQ(v),SameQ(Part(u,C1),Part(v,C1))),IGtQ(Times(Part(u,C2),Power(Part(v,C2),CN1)),C0),And(IGtQ(Part(u,C2),C0),PolynomialInAuxQ(Part(u,C1),v,x))),If(Or(SumQ(u),ProductQ(u)),Catch(CompoundExpression(Scan(Function(If(Not(PolynomialInAuxQ(Slot1,v,x)),Throw(False))),u),True)),False))))),
ISetDelayed(298,ExponentIn(u_,v_,x_Symbol),
    ExponentInAux(u,NonfreeFactors(NonfreeTerms(v,x),x),x)),
ISetDelayed(299,ExponentInAux(u_,v_,x_),
    If(SameQ(u,v),C1,If(AtomQ(u),C0,If(PowerQ(u),If(And(PowerQ(v),SameQ(Part(u,C1),Part(v,C1))),Times(Part(u,C2),Power(Part(v,C2),CN1)),Times(Part(u,C2),ExponentInAux(Part(u,C1),v,x))),If(ProductQ(u),Apply(Plus,Map(Function(ExponentInAux(Slot1,v,x)),Apply(List,u))),Apply(Max,Map(Function(ExponentInAux(Slot1,v,x)),Apply(List,u)))))))),
ISetDelayed(300,PolynomialInSubst(u_,v_,x_Symbol),
    With(list(Set(w,NonfreeTerms(v,x))),ReplaceAll(PolynomialInSubstAux(u,NonfreeFactors(w,x),x),list(Rule(x,Times(Subtract(x,FreeTerms(v,x)),Power(FreeFactors(w,x),CN1))))))),
ISetDelayed(301,PolynomialInSubstAux(u_,v_,x_),
    If(SameQ(u,v),x,If(AtomQ(u),u,If(PowerQ(u),If(And(PowerQ(v),SameQ(Part(u,C1),Part(v,C1))),Power(x,Times(Part(u,C2),Power(Part(v,C2),CN1))),Power(PolynomialInSubstAux(Part(u,C1),v,x),Part(u,C2))),Map(Function(PolynomialInSubstAux(Slot1,v,x)),u))))),
ISetDelayed(302,PolynomialDivide(u_,v_,x_Symbol),
    Module(List(Set($s("quo"),PolynomialQuotient(u,v,x)),Set($s("rem"),PolynomialRemainder(u,v,x)),$s("free"),$s("monomial")),CompoundExpression(Set($s("quo"),Apply(Plus,Map(Function(Simp(Together(Times(Coefficient($s("quo"),x,Slot1),Power(x,Slot1))),x)),Exponent($s("quo"),x,List)))),Set($s("rem"),Together($s("rem"))),Set($s("free"),FreeFactors($s("rem"),x)),Set($s("rem"),NonfreeFactors($s("rem"),x)),Set($s("monomial"),Power(x,Exponent($s("rem"),x,Min))),If(NegQ(Coefficient($s("rem"),x,C0)),Set($s("monomial"),Negate($s("monomial")))),Set($s("rem"),Apply(Plus,Map(Function(Simp(Together(Times(Coefficient($s("rem"),x,Slot1),Power(x,Slot1),Power($s("monomial"),CN1))),x)),Exponent($s("rem"),x,List)))),If(BinomialQ(v,x),Plus($s("quo"),Times($s("free"),$s("monomial"),$s("rem"),Power(ExpandToSum(v,x),CN1))),Plus($s("quo"),Times($s("free"),$s("monomial"),$s("rem"),Power(v,CN1)))))))
  );
}
