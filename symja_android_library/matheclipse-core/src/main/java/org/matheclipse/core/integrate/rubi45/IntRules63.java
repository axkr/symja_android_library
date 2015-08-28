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
public class IntRules63 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_)),x_Symbol),
    Condition(Module(List(Set(e,FreeFactors(Tan(Plus(c,Times(d,x))),x))),Times(Power(e,Plus(m,C1)),Power(d,-1),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,e,x)),n),Power(Power(Plus(C1,Times(Sqr(e),Sqr(x))),Plus(Times(C1D2,m),C1)),-1)),x),x,Times(Tan(Plus(c,Times(d,x))),Power(e,-1))))),And(FreeQ(List(a,b,c,d,n),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_)),x_Symbol),
    Condition(Module(List(Set(e,FreeFactors(Cot(Plus(c,Times(d,x))),x))),Times(CN1,Power(e,Plus(m,C1)),Power(d,-1),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,e,x)),n),Power(Power(Plus(C1,Times(Sqr(e),Sqr(x))),Plus(Times(C1D2,m),C1)),-1)),x),x,Times(Cot(Plus(c,Times(d,x))),Power(e,-1))))),And(FreeQ(List(a,b,c,d,n),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(Expand(Times(Power(Sin(Plus(c,Times(d,x))),m),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),n)),x),x),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(Times(C1D2,Plus(m,Negate(C1))))),PositiveIntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(Expand(Times(Power(Cos(Plus(c,Times(d,x))),m),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),n)),x),x),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(Times(C1D2,Plus(m,Negate(C1))))),PositiveIntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Sin(Plus(c,Times(d,x))),m),Power(Plus(Times(a,Cos(Plus(c,Times(d,x)))),Times(b,Sin(Plus(c,Times(d,x))))),n),Power(Power(Cos(Plus(c,Times(d,x))),n),-1)),x),And(And(And(FreeQ(List(a,b,c,d),x),IntegerQ(Times(C1D2,Plus(m,Negate(C1))))),NegativeIntegerQ(n)),Or(And(Less(m,C5),Greater(n,CN4)),And(Equal(m,C5),Equal(n,CN1)))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Cos(Plus(c,Times(d,x))),m),Power(Plus(Times(a,Sin(Plus(c,Times(d,x)))),Times(b,Cos(Plus(c,Times(d,x))))),n),Power(Power(Sin(Plus(c,Times(d,x))),n),-1)),x),And(And(And(FreeQ(List(a,b,c,d),x),IntegerQ(Times(C1D2,Plus(m,Negate(C1))))),NegativeIntegerQ(n)),Or(And(Less(m,C5),Greater(n,CN4)),And(Equal(m,C5),Equal(n,CN1)))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_)),x_Symbol),
    Condition(Module(List(Set(e,FreeFactors(Tan(Plus(c,Times(d,x))),x))),Times(e,Power(d,-1),Subst(Int(Times(Power(Plus(a,Times(b,e,x)),n),Power(Power(Plus(C1,Times(Sqr(e),Sqr(x))),Plus(Times(C1D2,m),C1)),-1)),x),x,Times(Tan(Plus(c,Times(d,x))),Power(e,-1))))),And(FreeQ(List(a,b,c,d,n),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_)),x_Symbol),
    Condition(Module(List(Set(e,FreeFactors(Cot(Plus(c,Times(d,x))),x))),Times(CN1,e,Power(d,-1),Subst(Int(Times(Power(Plus(a,Times(b,e,x)),n),Power(Power(Plus(C1,Times(Sqr(e),Sqr(x))),Plus(Times(C1D2,m),C1)),-1)),x),x,Times(Cot(Plus(c,Times(d,x))),Power(e,-1))))),And(FreeQ(List(a,b,c,d,n),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(Expand(Times(Power(Cos(Plus(c,Times(d,x))),m),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),n)),x),x),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(Times(C1D2,Plus(m,Negate(C1))))),PositiveIntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(Expand(Times(Power(Sin(Plus(c,Times(d,x))),m),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),n)),x),x),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(Times(C1D2,Plus(m,Negate(C1))))),PositiveIntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Cos(Plus(c,Times(d,x))),Plus(m,Negate(n))),Power(Plus(Times(a,Cos(Plus(c,Times(d,x)))),Times(b,Sin(Plus(c,Times(d,x))))),n)),x),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(Times(C1D2,Plus(m,Negate(C1))))),NegativeIntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Sin(Plus(c,Times(d,x))),Plus(m,Negate(n))),Power(Plus(Times(a,Sin(Plus(c,Times(d,x)))),Times(b,Cos(Plus(c,Times(d,x))))),n)),x),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(Times(C1D2,Plus(m,Negate(C1))))),NegativeIntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Cos(Plus(c,Times(d,x))),Plus(m,Negate(n))),Power(Sin(Plus(c,Times(d,x))),p),Power(Plus(Times(a,Cos(Plus(c,Times(d,x)))),Times(b,Sin(Plus(c,Times(d,x))))),n)),x),And(FreeQ(List(a,b,c,d,m,p),x),IntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),p_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Sin(Plus(c,Times(d,x))),Plus(m,Negate(n))),Power(Cos(Plus(c,Times(d,x))),p),Power(Plus(Times(a,Sin(Plus(c,Times(d,x)))),Times(b,Cos(Plus(c,Times(d,x))))),n)),x),And(FreeQ(List(a,b,c,d,m,p),x),IntegerQ(n))))
  );
}
