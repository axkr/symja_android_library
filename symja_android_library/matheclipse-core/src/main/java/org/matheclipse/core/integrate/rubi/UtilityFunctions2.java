package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions2 { 
  public static IAST RULES = List( 
ISetDelayed(13,FractionalPowerFreeQ(u_),
    If(AtomQ(u),True,If(And(FractionalPowerQ(u),Not(AtomQ(Part(u,C1)))),False,Catch(CompoundExpression(Scan(Function(If(FractionalPowerFreeQ(Slot1),Null,Throw(False))),u),True))))),
ISetDelayed(14,ComplexFreeQ(u_),
    If(AtomQ(u),Not(ComplexNumberQ(u)),SameQ(Scan(Function(If(ComplexFreeQ(Slot1),Null,Return(False))),u),Null))),
ISetDelayed(15,LogQ(u_),
    SameQ(Head(u),Log)),
ISetDelayed(16,TrigQ(u_),
    MemberQ($s("§$trigfunctions"),If(AtomQ(u),u,Head(u))))
  );
}
