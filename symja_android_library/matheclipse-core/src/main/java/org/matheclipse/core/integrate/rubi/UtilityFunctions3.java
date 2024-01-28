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
ISetDelayed(17,HyperbolicQ(u_),
    MemberQ($s("§$hyperbolicfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(18,InverseTrigQ(u_),
    MemberQ($s("§$inversetrigfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(19,InverseHyperbolicQ(u_),
    MemberQ($s("§$inversehyperbolicfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(20,CalculusQ(u_),
    MemberQ($s("§$calculusfunctions"),If(AtomQ(u),u,Head(u)))),
ISetDelayed(21,HeldFormQ(u_),
    If(AtomQ(Head(u)),MemberQ($s("§$heldfunctions"),Head(u)),HeldFormQ(Head(u)))),
ISetDelayed(22,StopFunctionQ(u_),
    If(AtomQ(Head(u)),MemberQ($s("§$stopfunctions"),Head(u)),StopFunctionQ(Head(u)))),
ISetDelayed(23,InverseFunctionQ(u_),
    Or(LogQ(u),And(InverseTrigQ(u),LessEqual(Length(u),C1)),InverseHyperbolicQ(u),SameQ(Head(u),$s("§mods")),SameQ(Head(u),PolyLog)))
  );
}
