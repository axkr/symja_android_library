package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules86 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power($($p("F"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_))),p_),x_Symbol),
    Condition(Int(Expand(Power(Plus(a,Times(b,Power(F(Plus(c,Times(d,x))),n))),p),x),x),And(And(And(FreeQ(List(a,b,c,d),x),InertTrigQ($s("F"))),IntegerQ(n)),PositiveIntegerQ(p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power($($p("F"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_))),-1),x_Symbol),
    Condition(Dist(Times(C2,Power(Times(a,n),-1)),Sum(Int(Power(Plus(C1,Times(CN1,Sqr(F(Plus(c,Times(d,x)))),Power(Times(Power(CN1,Times(C4,k,Power(n,-1))),Rt(Times(CN1,a,Power(b,-1)),Times(C1D2,n))),-1))),-1),x),List(k,C1,Times(C1D2,n))),x),And(And(And(FreeQ(List(a,b,c,d),x),InertTrigQ($s("F"))),EvenQ(n)),Greater(n,C2)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power($($p("F"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_))),-1),x_Symbol),
    Condition(Int(ExpandTrig(Power(Plus(a,Times(b,Power(F(Plus(c,Times(d,x))),n))),-1),x),x),And(And(And(FreeQ(List(a,b,c,d),x),InertTrigQ($s("F"))),OddQ(n)),Greater(n,C2)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($p("F"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_))),-1),Power($($p("G"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrig(Power(G(Plus(c,Times(d,x))),m),Power(Plus(a,Times(b,Power(F(Plus(c,Times(d,x))),n))),-1),x),x),And(And(And(FreeQ(List(a,b,c,d,m),x),InertTrigQ($s("F"),$s("G"))),IntegerQ(n)),Greater(n,C2))))
  );
}
