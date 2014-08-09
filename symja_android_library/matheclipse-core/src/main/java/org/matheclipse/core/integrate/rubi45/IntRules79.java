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
public class IntRules79 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),p_),x_Symbol),
    Condition(Int(Power(Times(CN1,a,Sqr(Tan(Plus(c,Times(pd,x))))),p),x),And(FreeQ(List(a,b,c,pd,p),x),ZeroQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),p_),x_Symbol),
    Condition(Int(Power(Times(CN1,a,Sqr(Cot(Plus(c,Times(pd,x))))),p),x),And(FreeQ(List(a,b,c,pd,p),x),ZeroQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Plus(Times(x,Power(a,CN1)),Times(CN1,b,Power(a,CN1),Int(Power(Plus(b,Times(a,Sqr(Cos(Plus(c,Times(pd,x)))))),CN1),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Plus(Times(x,Power(a,CN1)),Times(CN1,b,Power(a,CN1),Int(Power(Plus(b,Times(a,Sqr(Sin(Plus(c,Times(pd,x)))))),CN1),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(a,b))))),
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),p_),x_Symbol),
    Condition(Times(Power(pd,CN1),Subst(Int(Times(Power(Plus(a,b,Times(b,Sqr(x))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Tan(Plus(c,Times(pd,x))))),And(And(FreeQ(List(a,b,c,pd,p),x),NonzeroQ(Plus(a,b))),NonzeroQ(Plus(p,C1))))),
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),p_),x_Symbol),
    Condition(Times(CN1,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,b,Times(b,Sqr(x))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Cot(Plus(c,Times(pd,x))))),And(And(FreeQ(List(a,b,c,pd,p),x),NonzeroQ(Plus(a,b))),NonzeroQ(Plus(p,C1))))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),Power($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd,p),x),EvenQ(m)),EvenQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),Power($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd,p),x),EvenQ(m)),EvenQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(Plus(b,Times(a,Power(Times(f,x),pn))),p),Power(Power(Times(f,x),Times(pn,p)),CN1)),x),x,Times(Cos(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd),x),OddQ(m)),IntegersQ(pn,p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(Plus(b,Times(a,Power(Times(f,x),pn))),p),Power(Power(Times(f,x),Times(pn,p)),CN1)),x),x,Times(Sin(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd),x),OddQ(m)),IntegersQ(pn,p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),Power($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),Times(CN1,C1))),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd,p),x),EvenQ(m)),EvenQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),Power($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),Times(CN1,C1))),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd,p),x),EvenQ(m)),EvenQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),Power($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(ExpandToSum(Plus(b,Times(a,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(pn,p),C1))),CN1)),x),x,Times(Sin(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),OddQ(m)),EvenQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),Power($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(ExpandToSum(Plus(b,Times(a,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(pn,p),C1))),CN1)),x),x,Times(Cos(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),OddQ(m)),EvenQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),Power($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrig(Times(Power($($s("§sec"),Plus(c,Times(pd,x))),m),Power(Plus(a,Times(b,Power($($s("§sec"),Plus(c,Times(pd,x))),pn))),p)),x),x),And(FreeQ(List(a,b,c,pd),x),IntegersQ(m,pn,p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),Power($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrig(Times(Power($($s("§csc"),Plus(c,Times(pd,x))),m),Power(Plus(a,Times(b,Power($($s("§csc"),Plus(c,Times(pd,x))),pn))),p)),x),x),And(FreeQ(List(a,b,c,pd),x),IntegersQ(m,pn,p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(pd,x))),x))),Times(CN1,Power(Times(pd,Power(f,Plus(m,Times(pn,p),Times(CN1,C1)))),CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(Plus(b,Times(a,Power(Times(f,x),pn))),p),Power(Power(x,Plus(m,Times(pn,p))),CN1)),x),x,Times(Cos(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd,pn),x),OddQ(m)),IntegerQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(pd,x))),x))),Times(Power(Times(pd,Power(f,Plus(m,Times(pn,p),Times(CN1,C1)))),CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(Plus(b,Times(a,Power(Times(f,x),pn))),p),Power(Power(x,Plus(m,Times(pn,p))),CN1)),x),x,Times(Sin(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd,pn),x),OddQ(m)),IntegerQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§sec"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Plus(C1,Times(Sqr(f),Sqr(x))),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd),x),EvenQ(m)),EvenQ(pn)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§csc"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Plus(C1,Times(Sqr(f),Sqr(x))),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd),x),EvenQ(m)),EvenQ(pn))))
  );
}
