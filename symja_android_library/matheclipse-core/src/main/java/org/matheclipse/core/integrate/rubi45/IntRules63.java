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
public class IntRules63 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(pe,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(Power(pe,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,pe,x)),pn),Power(Power(Plus(C1,Times(Sqr(pe),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(pe,CN1))))),And(FreeQ(List(a,b,c,pd,pn),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(pe,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,Power(pe,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,pe,x)),pn),Power(Power(Plus(C1,Times(Sqr(pe),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(pe,CN1))))),And(FreeQ(List(a,b,c,pd,pn),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Expand(Times(Power(Sin(Plus(c,Times(pd,x))),m),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),pn)),x),x),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(Times(C1D2,Plus(m,Times(CN1,C1))))),PositiveIntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Expand(Times(Power(Cos(Plus(c,Times(pd,x))),m),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),pn)),x),x),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(Times(C1D2,Plus(m,Times(CN1,C1))))),PositiveIntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Sin(Plus(c,Times(pd,x))),m),Power(Plus(Times(a,Cos(Plus(c,Times(pd,x)))),Times(b,Sin(Plus(c,Times(pd,x))))),pn),Power(Power(Cos(Plus(c,Times(pd,x))),pn),CN1)),x),And(And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(Times(C1D2,Plus(m,Times(CN1,C1))))),NegativeIntegerQ(pn)),Or(And(Less(m,C5),Greater(pn,CN4)),And(Equal(m,C5),Equal(pn,CN1)))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Cos(Plus(c,Times(pd,x))),m),Power(Plus(Times(a,Sin(Plus(c,Times(pd,x)))),Times(b,Cos(Plus(c,Times(pd,x))))),pn),Power(Power(Sin(Plus(c,Times(pd,x))),pn),CN1)),x),And(And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(Times(C1D2,Plus(m,Times(CN1,C1))))),NegativeIntegerQ(pn)),Or(And(Less(m,C5),Greater(pn,CN4)),And(Equal(m,C5),Equal(pn,CN1)))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(pe,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(pe,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,Times(b,pe,x)),pn),Power(Power(Plus(C1,Times(Sqr(pe),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(pe,CN1))))),And(FreeQ(List(a,b,c,pd,pn),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(pe,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,pe,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,Times(b,pe,x)),pn),Power(Power(Plus(C1,Times(Sqr(pe),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(pe,CN1))))),And(FreeQ(List(a,b,c,pd,pn),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Expand(Times(Power(Cos(Plus(c,Times(pd,x))),m),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),pn)),x),x),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(Times(C1D2,Plus(m,Times(CN1,C1))))),PositiveIntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Expand(Times(Power(Sin(Plus(c,Times(pd,x))),m),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),pn)),x),x),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(Times(C1D2,Plus(m,Times(CN1,C1))))),PositiveIntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Cos(Plus(c,Times(pd,x))),Plus(m,Times(CN1,pn))),Power(Plus(Times(a,Cos(Plus(c,Times(pd,x)))),Times(b,Sin(Plus(c,Times(pd,x))))),pn)),x),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(Times(C1D2,Plus(m,Times(CN1,C1))))),NegativeIntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Sin(Plus(c,Times(pd,x))),Plus(m,Times(CN1,pn))),Power(Plus(Times(a,Sin(Plus(c,Times(pd,x)))),Times(b,Cos(Plus(c,Times(pd,x))))),pn)),x),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(Times(C1D2,Plus(m,Times(CN1,C1))))),NegativeIntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Cos(Plus(c,Times(pd,x))),Plus(m,Times(CN1,pn))),Power(Sin(Plus(c,Times(pd,x))),p),Power(Plus(Times(a,Cos(Plus(c,Times(pd,x)))),Times(b,Sin(Plus(c,Times(pd,x))))),pn)),x),And(FreeQ(List(a,b,c,pd,m,p),x),IntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))))),pn_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),p_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Sin(Plus(c,Times(pd,x))),Plus(m,Times(CN1,pn))),Power(Cos(Plus(c,Times(pd,x))),p),Power(Plus(Times(a,Sin(Plus(c,Times(pd,x)))),Times(b,Cos(Plus(c,Times(pd,x))))),pn)),x),And(FreeQ(List(a,b,c,pd,m,p),x),IntegerQ(pn))))
  );
}
