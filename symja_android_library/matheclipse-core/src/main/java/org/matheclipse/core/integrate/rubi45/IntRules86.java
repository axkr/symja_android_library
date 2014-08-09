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
public class IntRules86 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(Times(Power($(pf_,Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),x_Symbol),
    Condition(Int(Expand(Power(Plus(a,Times(b,Power($(pf,Plus(c,Times(pd,x))),pn))),p),x),x),And(And(And(FreeQ(List(a,b,c,pd),x),InertTrigQ(pf)),IntegerQ(pn)),PositiveIntegerQ(p)))),
ISetDelayed(Int(Power(Plus(Times(Power($(pf_,Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Dist(Times(C2,Power(Times(a,pn),CN1)),Sum(Int(Power(Plus(C1,Times(CN1,Sqr($(pf,Plus(c,Times(pd,x)))),Power(Times(Power(CN1,Times(C4,k,Power(pn,CN1))),Rt(Times(CN1,a,Power(b,CN1)),Times(C1D2,pn))),CN1))),CN1),x),List(k,C1,Times(C1D2,pn))),x),And(And(And(FreeQ(List(a,b,c,pd),x),InertTrigQ(pf)),EvenQ(pn)),Greater(pn,C2)))),
ISetDelayed(Int(Power(Plus(Times(Power($(pf_,Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Int(ExpandTrig(Power(Plus(a,Times(b,Power($(pf,Plus(c,Times(pd,x))),pn))),CN1),x),x),And(And(And(FreeQ(List(a,b,c,pd),x),InertTrigQ(pf)),OddQ(pn)),Greater(pn,C2)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($(pf_,Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),CN1),Power($(pg_,Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrig(Power($(pg,Plus(c,Times(pd,x))),m),Power(Plus(a,Times(b,Power($(pf,Plus(c,Times(pd,x))),pn))),CN1),x),x),And(And(And(FreeQ(List(a,b,c,pd,m),x),InertTrigQ(pf,pg)),IntegerQ(pn)),Greater(pn,C2))))
  );
}
