package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class UtilityFunctions0 { 
  public static IAST RULES = List( 
ISetDelayed(4097,IntHide(u_,x_Symbol),
    Block(List(Set($s("§$showsteps"),False),Set($s("§$stepcounter"),Null)),Int(u,x))),
ISetDelayed(4098,EveryQ($p("func"),$p("lst")),
    Catch(CompoundExpression(Scan(Function(If($($s("func"),Slot1),Null,Throw(False))),$s("lst")),True))),
ISetDelayed(4099,Map2($p("func"),$p("lst1"),$p("lst2")),
    Module(List($s("ii")),ReapList(Do(Sow($($s("func"),Part($s("lst1"),$s("ii")),Part($s("lst2"),$s("ii")))),List($s("ii"),Length($s("lst1"))))))),
ISetDelayed(4100,ReapList(u_),
    With(List(Set($s("lst"),Part(Reap(u),C2))),If(SameQ($s("lst"),List()),$s("lst"),Part($s("lst"),C1)))),
ISetDelayed(4101,HalfIntegerQ($ps("u")),
    SameQ(Scan(Function(If(And(SameQ(Head(Slot1),$s("Rational")),Equal(Denominator(Slot1),C2)),Null,Return(False))),List(u)),Null)),
ISetDelayed(4102,RationalQ($ps("u")),
    SameQ(Scan(Function(If(Or(IntegerQ(Slot1),SameQ(Head(Slot1),$s("Rational"))),Null,Return(False))),List(u)),Null)),
ISetDelayed(4103,FractionOrNegativeQ($ps("u")),
    SameQ(Scan(Function(If(Or(FractionQ(Slot1),And(IntegerQ(Slot1),Less(Slot1,C0))),Null,Return(False))),List(u)),Null)),
ISetDelayed(4104,SqrtNumberQ(Power(m_,n_)),
    Or(And(IntegerQ(n),SqrtNumberQ(m)),And(IntegerQ(Plus(n,Negate(C1D2))),RationalQ(m)))),
ISetDelayed(4105,SqrtNumberQ(Times(u_,v_)),
    And(SqrtNumberQ(u),SqrtNumberQ(v))),
ISetDelayed(4106,SqrtNumberQ(u_),
    Or(RationalQ(u),SameQ(u,CI))),
ISetDelayed(4107,SqrtNumberSumQ(u_),
    Or(And(SumQ(u),SqrtNumberQ(First(u)),SqrtNumberQ(Rest(u))),And(ProductQ(u),SqrtNumberQ(First(u)),SqrtNumberSumQ(Rest(u))))),
ISetDelayed(4108,IndependentQ(u_,x_),
    FreeQ(u,x)),
ISetDelayed(4109,FractionalPowerFreeQ(u_),
    If(AtomQ(u),True,If(And(FractionalPowerQ(u),Not(AtomQ(Part(u,C1)))),False,Catch(CompoundExpression(Scan(Function(If(FractionalPowerFreeQ(Slot1),Null,Throw(False))),u),True))))),
ISetDelayed(4110,ComplexFreeQ(u_),
    If(AtomQ(u),Not(ComplexNumberQ(u)),SameQ(Scan(Function(If(ComplexFreeQ(Slot1),Null,Return(False))),u),Null))),
ISetDelayed(4111,LogQ(u_),
    SameQ(Head(u),$s("Log"))),
ISetDelayed(4112,TrigQ(u_),
    MemberQ($s("§$trigfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(4113,HyperbolicQ(u_),
    MemberQ($s("§$hyperbolicfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(4114,InverseTrigQ(u_),
    MemberQ($s("§$inversetrigfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(4115,InverseHyperbolicQ(u_),
    MemberQ($s("§$inversehyperbolicfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(4116,CalculusQ(u_),
    MemberQ($s("§$calculusfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(4117,StopFunctionQ(u_),
    If(AtomQ(Head(u)),MemberQ($s("§$stopfunctions"),Head(u)),StopFunctionQ(Head(u)))),
ISetDelayed(4118,HeldFormQ(u_),
    If(AtomQ(Head(u)),MemberQ($s("§$heldfunctions"),Head(u)),HeldFormQ(Head(u)))),
ISetDelayed(4119,InverseFunctionQ(u_),
    Or(LogQ(u),And(InverseTrigQ(u),LessEqual(Length(u),C1)),InverseHyperbolicQ(u),SameQ(Head(u),$s("§mods")),SameQ(Head(u),$s("PolyLog")))),
ISetDelayed(4120,TrigHyperbolicFreeQ(u_,x_Symbol),
    If(AtomQ(u),True,If(Or(TrigQ(u),HyperbolicQ(u),CalculusQ(u)),FreeQ(u,x),Catch(CompoundExpression(Scan(Function(If(TrigHyperbolicFreeQ(Slot1,x),Null,Throw(False))),u),True))))),
ISetDelayed(4121,InverseFunctionFreeQ(u_,x_Symbol),
    If(AtomQ(u),True,If(Or(InverseFunctionQ(u),CalculusQ(u),SameQ(Head(u),$s("Hypergeometric2F1")),SameQ(Head(u),$s("AppellF1"))),FreeQ(u,x),Catch(CompoundExpression(Scan(Function(If(InverseFunctionFreeQ(Slot1,x),Null,Throw(False))),u),True))))),
ISetDelayed(4122,CalculusFreeQ(u_,x_),
    If(AtomQ(u),True,If(Or(And(CalculusQ(u),SameQ(Part(u,C2),x)),HeldFormQ(u)),False,Catch(CompoundExpression(Scan(Function(If(CalculusFreeQ(Slot1,x),Null,Throw(False))),u),True))))),
ISetDelayed(4123,IntegralFreeQ(u_),
    And(FreeQ(u,Integrate),FreeQ(u,$rubi("Integrate::Integral")),FreeQ(u,$rubi("Integrate::Unintegrable")),FreeQ(u,$rubi("Integrate::CannotIntegrate")))),
ISetDelayed(4124,EqQ(u_,v_),
    Or(Quiet(PossibleZeroQ(Plus(u,Negate(v)))),SameQ(Refine(Equal(u,v)),True))),
ISetDelayed(4125,NeQ(u_,v_),
    Not(Or(Quiet(PossibleZeroQ(Plus(u,Negate(v)))),SameQ(Refine(Equal(u,v)),True)))),
ISetDelayed(4126,IGtQ(u_,n_),
    And(IntegerQ(u),Greater(u,n))),
ISetDelayed(4127,ILtQ(u_,n_),
    And(IntegerQ(u),Less(u,n))),
ISetDelayed(4128,IGeQ(u_,n_),
    And(IntegerQ(u),GreaterEqual(u,n))),
ISetDelayed(4129,ILeQ(u_,n_),
    And(IntegerQ(u),LessEqual(u,n))),
ISetDelayed(4130,GtQ(u_,v_),
    If(RealNumberQ(u),If(RealNumberQ(v),Greater(u,v),With(List(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),$s("Real")),Greater(u,$s("vn"))))),With(List(Set($s("un"),N(Together(u)))),If(SameQ(Head($s("un")),$s("Real")),If(RealNumberQ(v),Greater($s("un"),v),With(List(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),$s("Real")),Greater($s("un"),$s("vn"))))),False))))
  );
}
