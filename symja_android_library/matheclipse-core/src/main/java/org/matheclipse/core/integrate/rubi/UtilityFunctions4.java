package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions4 { 
  public static IAST RULES = List( 
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
    Not(Or(Quiet(PossibleZeroQ(Subtract(u,v))),SameQ(Refine(Equal(u,v)),True)))),
ISetDelayed(30,IGtQ(u_,n_),
    And(IntegerQ(u),Greater(u,n))),
ISetDelayed(31,ILtQ(u_,n_),
    And(IntegerQ(u),Less(u,n))),
ISetDelayed(32,IGeQ(u_,n_),
    And(IntegerQ(u),GreaterEqual(u,n))),
ISetDelayed(33,ILeQ(u_,n_),
    And(IntegerQ(u),LessEqual(u,n)))
  );
}
