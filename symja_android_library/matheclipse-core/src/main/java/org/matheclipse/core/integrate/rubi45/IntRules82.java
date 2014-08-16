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
public class IntRules82 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Times(c_DEFAULT,Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),pn_),x_Symbol),
    Condition(Plus(Times(CN1,Cot(Plus(a,Times(b,x))),Power(Times(c,Sqr(Sin(Plus(a,Times(b,x))))),pn),Power(Times(C2,b,pn),CN1)),Times(c,Plus(Times(C2,pn),Times(CN1,C1)),Power(Times(C2,pn),CN1),Int(Power(Times(c,Sqr(Sin(Plus(a,Times(b,x))))),Plus(pn,Times(CN1,C1))),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),pn_),x_Symbol),
    Condition(Plus(Times(Tan(Plus(a,Times(b,x))),Power(Times(c,Sqr(Cos(Plus(a,Times(b,x))))),pn),Power(Times(C2,b,pn),CN1)),Times(c,Plus(Times(C2,pn),Times(CN1,C1)),Power(Times(C2,pn),CN1),Int(Power(Times(c,Sqr(Cos(Plus(a,Times(b,x))))),Plus(pn,Times(CN1,C1))),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),pn_),x_Symbol),
    Condition(Plus(Times(Cot(Plus(a,Times(b,x))),Power(Times(c,Sqr(Sin(Plus(a,Times(b,x))))),Plus(pn,C1)),Power(Times(b,c,Plus(Times(C2,pn),C1)),CN1)),Times(C2,Plus(pn,C1),Power(Times(c,Plus(Times(C2,pn),C1)),CN1),Int(Power(Times(c,Sqr(Sin(Plus(a,Times(b,x))))),Plus(pn,C1)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),pn_),x_Symbol),
    Condition(Plus(Times(CN1,Tan(Plus(a,Times(b,x))),Power(Times(c,Sqr(Cos(Plus(a,Times(b,x))))),Plus(pn,C1)),Power(Times(b,c,Plus(Times(C2,pn),C1)),CN1)),Times(C2,Plus(pn,C1),Power(Times(c,Plus(Times(C2,pn),C1)),CN1),Int(Power(Times(c,Sqr(Cos(Plus(a,Times(b,x))))),Plus(pn,C1)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Power(Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Power(Plus(C1,Sqr(x)),Plus(pn,Times(CN1,C1))),x),x,Tan(Plus(a,Times(b,x))))),FreeQ(List(a,b,pn),x))),
ISetDelayed(Int(Power(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Times(CN1,Power(b,CN1),Subst(Int(Power(Plus(C1,Sqr(x)),Plus(pn,Times(CN1,C1))),x),x,Cot(Plus(a,Times(b,x))))),FreeQ(List(a,b,pn),x))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Times(pd_DEFAULT,$(pf_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_)),pn_),x_Symbol),
    Condition(Module(List(Set(u,ActivateTrig($(pf,Plus(a,Times(b,x)))))),Times(Power(c,Plus(pn,Times(CN1,C1D2))),Power(Times(pd,FreeFactors(u,x)),Times(m,Plus(pn,Times(CN1,C1D2)))),Sqrt(Times(c,Power(Times(pd,u),m))),Power(Power(NonfreeFactors(u,x),Times(C1D2,m)),CN1),Int(Power(NonfreeFactors(u,x),Times(m,pn)),x))),And(And(FreeQ(List(a,b,c,pd,m),x),InertTrigQ(pf)),PositiveIntegerQ(Plus(pn,C1D2))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Times(pd_DEFAULT,$(pf_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_)),pn_),x_Symbol),
    Condition(Module(List(Set(u,ActivateTrig($(pf,Plus(a,Times(b,x)))))),Times(Power(c,Plus(pn,C1D2)),Power(Times(pd,FreeFactors(u,x)),Times(m,Plus(pn,C1D2))),Power(NonfreeFactors(u,x),Times(C1D2,m)),Power(Times(c,Power(Times(pd,u),m)),CN1D2),Int(Power(NonfreeFactors(u,x),Times(m,pn)),x))),And(And(FreeQ(List(a,b,c,pd,m),x),InertTrigQ(pf)),NegativeIntegerQ(Plus(pn,Times(CN1,C1D2)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Times(pd_DEFAULT,$(pf_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_)),pn_),x_Symbol),
    Condition(Module(List(Set(u,ActivateTrig($(pf,Plus(a,Times(b,x)))))),Times(Power(Times(c,Power(Times(pd,u),m)),pn),Power(Power(NonfreeFactors(u,x),Times(m,pn)),CN1),Int(Power(NonfreeFactors(u,x),Times(m,pn)),x))),And(And(FreeQ(List(a,b,c,pd,m,pn),x),InertTrigQ(pf)),Not(IntegerQ(Plus(pn,C1D2))))))
  );
}
