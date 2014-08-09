package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules48 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),p_),Power($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT),Power($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(v,ExpandTrig(Times(Power($($s("§cos"),Plus(c,Times(pd,x))),m),Power($($s("§sin"),Plus(c,Times(pd,x))),pn),Power(Plus(a,Times(b,$($s("§sin"),Plus(c,Times(pd,x))))),p)),x))),Condition(Int(v,x),SumQ(v))),FreeQ(List(a,b,c,pd,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),p_),Power($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT),Power($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(v,ExpandTrig(Times(Power($($s("§cos"),Plus(c,Times(pd,x))),m),Power($($s("§sin"),Plus(c,Times(pd,x))),pn),Power(Plus(a,Times(b,$($s("§cos"),Plus(c,Times(pd,x))))),p)),x))),Condition(Int(v,x),SumQ(v))),FreeQ(List(a,b,c,pd,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),p_DEFAULT),Power(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pe_DEFAULT),pn_DEFAULT),Power($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power($($s("§cos"),Plus(c,Times(pd,x))),m),Power(Times(pe,$($s("§sin"),Plus(c,Times(pd,x)))),pn),Power(Plus(a,Times(b,$($s("§sin"),Plus(c,Times(pd,x))))),p)),x),And(FreeQ(List(a,b,c,pd,pe,m,pn,p),x),Not(OddQ(m))))),
ISetDelayed(Int(Times(Power(Plus(Times($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),p_DEFAULT),Power(Times($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pe_DEFAULT),pn_DEFAULT),Power($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power($($s("§cos"),Plus(c,Times(pd,x))),m),Power(Times(pe,$($s("§sin"),Plus(c,Times(pd,x)))),pn),Power(Plus(a,Times(b,$($s("§cos"),Plus(c,Times(pd,x))))),p)),x),And(FreeQ(List(a,b,c,pd,pe,m,pn,p),x),Not(OddQ(m)))))
  );
}
