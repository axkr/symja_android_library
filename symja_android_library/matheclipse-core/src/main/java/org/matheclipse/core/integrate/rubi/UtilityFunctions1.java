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
  public static IAST RULES = List( 
ISetDelayed(7,FractionOrNegativeQ($ps("u")),
    SameQ(Scan(Function(If(Or(FractionQ(Slot1),And(IntegerQ(Slot1),Less(Slot1,C0))),Null,Return(False))),List(u)),Null)),
ISetDelayed(8,SqrtNumberQ(Power(m_,n_)),
    Or(And(IntegerQ(n),SqrtNumberQ(m)),And(IntegerQ(Subtract(n,C1D2)),RationalQ(m)))),
ISetDelayed(9,SqrtNumberQ(Times(u_,v_)),
    And(SqrtNumberQ(u),SqrtNumberQ(v))),
ISetDelayed(10,SqrtNumberQ(u_),
    Or(RationalQ(u),SameQ(u,CI))),
ISetDelayed(11,SqrtNumberSumQ(u_),
    Or(And(SumQ(u),SqrtNumberQ(First(u)),SqrtNumberQ(Rest(u))),And(ProductQ(u),SqrtNumberQ(First(u)),SqrtNumberSumQ(Rest(u))))),
ISetDelayed(12,IndependentQ(u_,x_),
    FreeQ(u,x))
  );
}
