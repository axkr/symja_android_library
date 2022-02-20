package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions3 { 
  public static IAST RULES = List( 
ISetDelayed(18,InverseTrigQ(u_),
    MemberQ($s("§$inversetrigfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(19,InverseHyperbolicQ(u_),
    MemberQ($s("§$inversehyperbolicfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(20,CalculusQ(u_),
    MemberQ($s("§$calculusfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(21,StopFunctionQ(u_),
    If(AtomQ(Head(u)),MemberQ($s("§$stopfunctions"),Head(u)),StopFunctionQ(Head(u)))),
ISetDelayed(22,HeldFormQ(u_),
    If(AtomQ(Head(u)),MemberQ($s("§$heldfunctions"),Head(u)),HeldFormQ(Head(u)))),
ISetDelayed(23,InverseFunctionQ(u_),
    Or(LogQ(u),And(InverseTrigQ(u),LessEqual(Length(u),C1)),InverseHyperbolicQ(u),SameQ(Head(u),$s("§mods")),SameQ(Head(u),PolyLog))),
ISetDelayed(24,TrigHyperbolicFreeQ(u_,x_Symbol),
    If(AtomQ(u),True,If(Or(TrigQ(u),HyperbolicQ(u),CalculusQ(u)),FreeQ(u,x),Catch(CompoundExpression(Scan(Function(If(TrigHyperbolicFreeQ(Slot1,x),Null,Throw(False))),u),True))))),
ISetDelayed(25,InverseFunctionFreeQ(u_,x_Symbol),
    If(AtomQ(u),True,If(Or(InverseFunctionQ(u),CalculusQ(u),SameQ(Head(u),Hypergeometric2F1),SameQ(Head(u),AppellF1)),FreeQ(u,x),Catch(CompoundExpression(Scan(Function(If(InverseFunctionFreeQ(Slot1,x),Null,Throw(False))),u),True))))),
ISetDelayed(26,CalculusFreeQ(u_,x_),
    If(AtomQ(u),True,If(Or(And(CalculusQ(u),SameQ(Part(u,C2),x)),HeldFormQ(u)),False,Catch(CompoundExpression(Scan(Function(If(CalculusFreeQ(Slot1,x),Null,Throw(False))),u),True))))),
ISetDelayed(27,IntegralFreeQ(u_),
    And(FreeQ(u,Integrate),FreeQ(u,$rubi("Integral")),FreeQ(u,$rubi("Unintegrable")),FreeQ(u,$rubi("CannotIntegrate")))),
ISetDelayed(28,EqQ(u_,v_),
    Or(Quiet(PossibleZeroQ(Subtract(u,v))),SameQ(Refine(Equal(u,v)),True))),
ISetDelayed(29,NeQ(u_,v_),
    Not(Or(Quiet(PossibleZeroQ(Subtract(u,v))),SameQ(Refine(Equal(u,v)),True))))
  );
}
