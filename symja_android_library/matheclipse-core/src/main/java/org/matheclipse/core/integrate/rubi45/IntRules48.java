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
ISetDelayed(Int(Times(Power($($s("§cos"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times($p(d,true),x_))))),p_)),x_Symbol),
    Condition(Module(List(Set(v,ExpandTrig(Times(Power($($s("§cos"),Plus(c,Times(d,x))),m),Power($($s("§sin"),Plus(c,Times(d,x))),n),Power(Plus(a,Times(b,$($s("§sin"),Plus(c,Times(d,x))))),p)),x))),Condition(Int(v,x),SumQ(v))),FreeQ(List(a,b,c,d,m,n,p),x))),
ISetDelayed(Int(Times(Power($($s("§cos"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times($p(d,true),x_))),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times($p(d,true),x_))))),p_)),x_Symbol),
    Condition(Module(List(Set(v,ExpandTrig(Times(Power($($s("§cos"),Plus(c,Times(d,x))),m),Power($($s("§sin"),Plus(c,Times(d,x))),n),Power(Plus(a,Times(b,$($s("§cos"),Plus(c,Times(d,x))))),p)),x))),Condition(Int(v,x),SumQ(v))),FreeQ(List(a,b,c,d,m,n,p),x))),
ISetDelayed(Int(Times(Power(Times($p(e,true),$($s("§sin"),Plus(c_DEFAULT,Times($p(d,true),x_)))),n_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times($p(d,true),x_))))),p_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power($($s("§cos"),Plus(c,Times(d,x))),m),Power(Times(e,$($s("§sin"),Plus(c,Times(d,x)))),n),Power(Plus(a,Times(b,$($s("§sin"),Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Not(OddQ(m))))),
ISetDelayed(Int(Times(Power(Times($p(e,true),$($s("§sin"),Plus(c_DEFAULT,Times($p(d,true),x_)))),n_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times($p(d,true),x_))),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times($p(d,true),x_))))),p_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power($($s("§cos"),Plus(c,Times(d,x))),m),Power(Times(e,$($s("§sin"),Plus(c,Times(d,x)))),n),Power(Plus(a,Times(b,$($s("§cos"),Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Not(OddQ(m)))))
  );
}
