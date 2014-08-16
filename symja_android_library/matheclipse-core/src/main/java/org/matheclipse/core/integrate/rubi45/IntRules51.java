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
public class IntRules51 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_),x_Symbol),
    Condition(Times(Power(a,p),Int(Power(Cos(Plus(c,Times(pd,x))),Times(C2,p)),x)),And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(a,b))),IntegerQ(p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_),x_Symbol),
    Condition(Times(Power(a,p),Int(Power(Sin(Plus(c,Times(pd,x))),Times(C2,p)),x)),And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(a,b))),IntegerQ(p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_),x_Symbol),
    Condition(Int(Power(Times(a,Sqr(Cos(Plus(c,Times(pd,x))))),p),x),And(And(FreeQ(List(a,b,c,pd,p),x),ZeroQ(Plus(a,b))),Not(IntegerQ(p))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_),x_Symbol),
    Condition(Int(Power(Times(a,Sqr(Sin(Plus(c,Times(pd,x))))),p),x),And(And(FreeQ(List(a,b,c,pd,p),x),ZeroQ(Plus(a,b))),Not(IntegerQ(p))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_),x_Symbol),
    Condition(Module(List(Set(pe,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(pe,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,Times(Plus(a,b),Sqr(pe),Sqr(x))),p),Power(Power(Plus(C1,Times(Sqr(pe),Sqr(x))),Plus(p,C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(pe,CN1))))),And(FreeQ(List(a,b,c,pd),x),IntegerQ(p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_),x_Symbol),
    Condition(Module(List(Set(pe,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(pe,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,b,Times(a,Sqr(pe),Sqr(x))),p),Power(Power(Plus(C1,Times(Sqr(pe),Sqr(x))),Plus(p,C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(pe,CN1))))),And(FreeQ(List(a,b,c,pd),x),IntegerQ(p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_),x_Symbol),
    Condition(Times(Power(Power(C2,p),CN1),Int(Power(Plus(Times(C2,a),b,Times(CN1,b,Cos(Plus(Times(C2,c),Times(C2,pd,x))))),p),x)),And(And(FreeQ(List(a,b,c,pd,p),x),NonzeroQ(Plus(a,b))),Not(IntegerQ(p))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_),x_Symbol),
    Condition(Times(Power(Power(C2,p),CN1),Int(Power(Plus(Times(C2,a),b,Times(b,Cos(Plus(Times(C2,c),Times(C2,pd,x))))),p),x)),And(And(FreeQ(List(a,b,c,pd,p),x),NonzeroQ(Plus(a,b))),Not(IntegerQ(p))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),x_Symbol),
    Condition(Int(Expand(Power(Plus(a,Times(b,Power(Sin(Plus(c,Times(pd,x))),pn))),p),x),x),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(pn)),PositiveIntegerQ(p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),x_Symbol),
    Condition(Int(Expand(Power(Plus(a,Times(b,Power(Cos(Plus(c,Times(pd,x))),pn))),p),x),x),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(pn)),PositiveIntegerQ(p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(ExpandToSum(Plus(b,Times(a,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(pn,C1D2,p),C1)),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),EvenQ(pn)),IntegerQ(p)),Less(p,CN1)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(ExpandToSum(Plus(b,Times(a,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(pn,C1D2,p),C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),EvenQ(pn)),IntegerQ(p)),Less(p,CN1)))),
ISetDelayed(Int(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_)),x_Symbol),
    Condition(Times(Power(a,p),Int(Times(ActivateTrig(u),Power(Cos(Plus(c,Times(pd,x))),Times(C2,p))),x)),And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(a,b))),IntegerQ(p)))),
ISetDelayed(Int(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_)),x_Symbol),
    Condition(Times(Power(a,p),Int(Times(ActivateTrig(u),Power(Sin(Plus(c,Times(pd,x))),Times(C2,p))),x)),And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(a,b))),IntegerQ(p)))),
ISetDelayed(Int(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_)),x_Symbol),
    Condition(Times(Power(Times(a,Sqr(Cos(Plus(c,Times(pd,x))))),p),Power(Power(Cos(Plus(c,Times(pd,x))),Times(C2,p)),CN1),Int(Times(ActivateTrig(u),Power(Cos(Plus(c,Times(pd,x))),Times(C2,p))),x)),And(And(FreeQ(List(a,b,c,pd,p),x),ZeroQ(Plus(a,b))),Not(IntegerQ(p))))),
ISetDelayed(Int(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))))),p_)),x_Symbol),
    Condition(Times(Power(Times(a,Sqr(Sin(Plus(c,Times(pd,x))))),p),Power(Power(Sin(Plus(c,Times(pd,x))),Times(C2,p)),CN1),Int(Times(ActivateTrig(u),Power(Sin(Plus(c,Times(pd,x))),Times(C2,p))),x)),And(And(FreeQ(List(a,b,c,pd,p),x),ZeroQ(Plus(a,b))),Not(IntegerQ(p))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(ExpandToSum(Plus(b,Times(a,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),Times(pn,C1D2,p),C1)),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),EvenQ(pn)),EvenQ(m)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(ExpandToSum(Plus(b,Times(a,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),Times(pn,C1D2,p),C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),EvenQ(pn)),EvenQ(m)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p)),x),x,Times(Cos(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd,p),x),EvenQ(pn)),OddQ(m)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(ExpandToSum(Plus(a,Times(b,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p)),x),x,Times(Sin(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd,p),x),EvenQ(pn)),OddQ(m)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrig(Times(Power($($s("§sin"),Plus(c,Times(pd,x))),m),Power(Plus(a,Times(b,Power($($s("§sin"),Plus(c,Times(pd,x))),pn))),p)),x),x),And(FreeQ(List(a,b,c,pd),x),IntegersQ(m,pn,p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrig(Times(Power($($s("§cos"),Plus(c,Times(pd,x))),m),Power(Plus(a,Times(b,Power($($s("§cos"),Plus(c,Times(pd,x))),pn))),p)),x),x),And(FreeQ(List(a,b,c,pd),x),IntegersQ(m,pn,p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(ExpandToSum(Plus(Times(b,Power(f,pn),Power(x,pn)),Times(a,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),Times(pn,C1D2,p),C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),EvenQ(m)),EvenQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(ExpandToSum(Plus(Times(b,Power(f,pn),Power(x,pn)),Times(a,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),Times(pn,C1D2,p),C1)),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),EvenQ(m)),EvenQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Int(Expand(Times(Power(Plus(C1,Times(CN1,Sqr(Sin(Plus(c,Times(pd,x)))))),Times(C1D2,m)),Power(Plus(a,Times(b,Power(Sin(Plus(c,Times(pd,x))),pn))),p)),x),x),And(And(And(And(FreeQ(List(a,b,c,pd),x),EvenQ(m)),OddQ(pn)),IntegerQ(p)),Greater(m,C0)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Int(Expand(Times(Power(Plus(C1,Times(CN1,Sqr(Cos(Plus(c,Times(pd,x)))))),Times(C1D2,m)),Power(Plus(a,Times(b,Power(Cos(Plus(c,Times(pd,x))),pn))),p)),x),x),And(And(And(And(FreeQ(List(a,b,c,pd),x),EvenQ(m)),OddQ(pn)),IntegerQ(p)),Greater(m,C0)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Int(ExpandTrig(Times(Power(Plus(C1,Times(CN1,Sqr($($s("§sin"),Plus(c,Times(pd,x)))))),Times(C1D2,m)),Power(Plus(a,Times(b,Power($($s("§sin"),Plus(c,Times(pd,x))),pn))),p)),x),x),And(And(And(And(And(FreeQ(List(a,b,c,pd),x),EvenQ(m)),OddQ(pn)),IntegerQ(p)),Less(m,C0)),Less(p,CN1)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Int(ExpandTrig(Times(Power(Plus(C1,Times(CN1,Sqr($($s("§cos"),Plus(c,Times(pd,x)))))),Times(C1D2,m)),Power(Plus(a,Times(b,Power($($s("§cos"),Plus(c,Times(pd,x))),pn))),p)),x),x),And(And(And(And(And(FreeQ(List(a,b,c,pd),x),EvenQ(m)),OddQ(pn)),IntegerQ(p)),Less(m,C0)),Less(p,CN1)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(pe_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))),pn_))),p_DEFAULT),Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(Plus(a,Times(b,Power(Times(pe,f,x),pn))),p)),x),x,Times(Sin(Plus(c,Times(pd,x))),Power(f,CN1))))),And(FreeQ(List(a,b,c,pd,pe,pn,p),x),OddQ(m)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(pe_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))),pn_))),p_DEFAULT),Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(Plus(a,Times(b,Power(Times(pe,f,x),pn))),p)),x),x,Times(Cos(Plus(c,Times(pd,x))),Power(f,CN1))))),And(FreeQ(List(a,b,c,pd,pe,pn,p),x),OddQ(m)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(pe_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))),pn_))),p_DEFAULT),Power($($s("§tan"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(pd,x))),x))),Times(Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(Times(pe,f,x),pn))),p),Power(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,C1))),CN1)),x),x,Times(Sin(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd,pe,pn),x),OddQ(m)),IntegerQ(Times(C2,p))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Times(pe_DEFAULT,$($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT)))),pn_))),p_DEFAULT),Power($($s("§cot"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(pd,x))),x))),Times(CN1,Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(Times(pe,f,x),pn))),p),Power(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,C1))),CN1)),x),x,Times(Cos(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(FreeQ(List(a,b,c,pd,pe,pn),x),OddQ(m)),IntegerQ(Times(C2,p))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_DEFAULT),Power($($s("§tan"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(ExpandToSum(Plus(Times(b,Power(f,pn),Power(x,pn)),Times(a,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(pn,C1D2,p),C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd,m),x),Not(OddQ(m))),EvenQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_DEFAULT),Power($($s("§cot"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(ExpandToSum(Plus(Times(b,Power(f,pn),Power(x,pn)),Times(a,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(pn,C1D2,p),C1)),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd,m),x),Not(OddQ(m))),EvenQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§sin"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_DEFAULT),Power($($s("§tan"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(Times(ExpandToSum(Plus(Times(b,Power(f,pn),Power(x,pn)),Times(a,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)),CN1)),p),Power(Plus(C1,Times(Sqr(f),Sqr(x))),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd,m,p),x),Not(OddQ(m))),EvenQ(pn)),Not(IntegerQ(p))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power($($s("§cos"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),pn_))),p_DEFAULT),Power($($s("§cot"),Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(Times(ExpandToSum(Plus(Times(b,Power(f,pn),Power(x,pn)),Times(a,Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Times(C1D2,pn)),CN1)),p),Power(Plus(C1,Times(Sqr(f),Sqr(x))),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd,m,p),x),Not(OddQ(m))),EvenQ(pn)),Not(IntegerQ(p)))))
  );
}
