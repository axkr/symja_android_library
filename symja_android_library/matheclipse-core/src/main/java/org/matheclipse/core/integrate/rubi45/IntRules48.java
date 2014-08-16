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
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),p_),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(v,ExpandTrig(Times(Power($($s("§cos"),Plus(c,Times(pd,x))),m),Power($($s("§sin"),Plus(c,Times(pd,x))),pn),Power(Plus(a,Times(b,$($s("§sin"),Plus(c,Times(pd,x))))),p)),x))),Condition(Int(v,x),SumQ(v))),FreeQ(List(a,b,c,pd,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),p_),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(v,ExpandTrig(Times(Power($($s("§cos"),Plus(c,Times(pd,x))),m),Power($($s("§sin"),Plus(c,Times(pd,x))),pn),Power(Plus(a,Times(b,$($s("§cos"),Plus(c,Times(pd,x))))),p)),x))),Condition(Int(v,x),SumQ(v))),FreeQ(List(a,b,c,pd,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),p_DEFAULT),Power(Times(pe_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))),pn_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power($($s("§cos"),Plus(c,Times(pd,x))),m),Power(Times(pe,$($s("§sin"),Plus(c,Times(pd,x)))),pn),Power(Plus(a,Times(b,$($s("§sin"),Plus(c,Times(pd,x))))),p)),x),And(FreeQ(List(a,b,c,pd,pe,m,pn,p),x),Not(OddQ(m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),p_DEFAULT),Power(Times(pe_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))),pn_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power($($s("§cos"),Plus(c,Times(pd,x))),m),Power(Times(pe,$($s("§sin"),Plus(c,Times(pd,x)))),pn),Power(Plus(a,Times(b,$($s("§cos"),Plus(c,Times(pd,x))))),p)),x),And(FreeQ(List(a,b,c,pd,pe,m,pn,p),x),Not(OddQ(m)))))
  );
}
