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
public class IntRules64 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Times(Power(a,CN1),Int(Sqr(Cos(Plus(c,Times(pd,x)))),x)),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Times(Power(a,CN1),Int(Sqr(Sin(Plus(c,Times(pd,x)))),x)),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Plus(Times(x,Power(Plus(a,Times(CN1,b)),CN1)),Times(CN1,b,Power(Plus(a,Times(CN1,b)),CN1),Int(Times(Sqr(Sec(Plus(c,Times(pd,x)))),Power(Plus(a,Times(b,Sqr(Tan(Plus(c,Times(pd,x)))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Plus(Times(x,Power(Plus(a,Times(CN1,b)),CN1)),Times(CN1,b,Power(Plus(a,Times(CN1,b)),CN1),Int(Times(Sqr(Csc(Plus(c,Times(pd,x)))),Power(Plus(a,Times(b,Sqr(Cot(Plus(c,Times(pd,x)))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(a,Times(CN1,b)))))),
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),p_),x_Symbol),
    Condition(Module(List(Set(pe,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(pe,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,Times(b,Sqr(pe),Sqr(x))),p),Power(Plus(C1,Times(Sqr(pe),Sqr(x))),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(pe,CN1))))),And(FreeQ(List(a,b,c,pd,p),x),NonzeroQ(Plus(p,C1))))),
ISetDelayed(Int(Power(Plus(Times(Sqr($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),b_DEFAULT),a_),p_),x_Symbol),
    Condition(Module(List(Set(pe,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,pe,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,Times(b,Sqr(pe),Sqr(x))),p),Power(Plus(C1,Times(Sqr(pe),Sqr(x))),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(pe,CN1))))),And(FreeQ(List(a,b,c,pd,p),x),NonzeroQ(Plus(p,C1))))),
ISetDelayed(Int(Power(Plus(Times(Power($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),x_Symbol),
    Condition(Times(Power(pd,CN1),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,pn))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Tan(Plus(c,Times(pd,x))))),FreeQ(List(a,b,c,pd,pn,p),x))),
ISetDelayed(Int(Power(Plus(Times(Power($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_),x_Symbol),
    Condition(Times(CN1,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,pn))),p),Power(Plus(C1,Sqr(x)),CN1)),x),x,Cot(Plus(c,Times(pd,x))))),FreeQ(List(a,b,c,pd,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(Power(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pe_DEFAULT),pn_),b_DEFAULT),a_),p_),Power($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(Times(pe,f,x),pn))),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),And(FreeQ(List(a,b,c,pd,pe,pn,p),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(Times(Power(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pe_DEFAULT),pn_),b_DEFAULT),a_),p_),Power($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,Power(f,Plus(m,C1)),Power(pd,CN1),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(Times(pe,f,x),pn))),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),And(FreeQ(List(a,b,c,pd,pe,pn,p),x),IntegerQ(Times(C1D2,m))))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(ExpandToSum(Plus(Times(a,Power(Times(f,x),pn)),Times(b,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Times(f,x),Times(pn,p)),CN1)),x),x,Times(Cos(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),OddQ(m)),EvenQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,C1)))),Power(ExpandToSum(Plus(Times(a,Power(Times(f,x),pn)),Times(b,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p),Power(Power(Times(f,x),Times(pn,p)),CN1)),x),x,Times(Sin(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),OddQ(m)),EvenQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pe_DEFAULT),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,Times(b,Power(Times(pe,f,x),pn))),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),And(FreeQ(List(a,b,c,pd,pe,pn,p),x),EvenQ(m)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pe_DEFAULT),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,Times(b,Power(Times(pe,f,x),pn))),p),Power(Power(Plus(C1,Times(Sqr(f),Sqr(x))),Plus(Times(C1D2,m),C1)),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),And(FreeQ(List(a,b,c,pd,pe,pn,p),x),EvenQ(m)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§cos"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Sin(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,pn,p),Times(CN1,C1)))),Power(ExpandToSum(Plus(Times(b,Power(Times(f,x),pn)),Times(a,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p)),x),x,Times(Sin(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),OddQ(m)),EvenQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pn_),b_DEFAULT),a_),p_DEFAULT),Power($($s("§sin"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cos(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,Plus(m,Times(CN1,pn,p),Times(CN1,C1)))),Power(ExpandToSum(Plus(Times(b,Power(Times(f,x),pn)),Times(a,Power(Plus(C1,Times(CN1,Sqr(f),Sqr(x))),Times(C1D2,pn)))),x),p)),x),x,Times(Cos(Plus(c,Times(pd,x))),Power(f,CN1))))),And(And(And(FreeQ(List(a,b,c,pd),x),OddQ(m)),EvenQ(pn)),IntegerQ(p)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pe_DEFAULT),pn_),b_DEFAULT),a_),p_),Power($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Tan(Plus(c,Times(pd,x))),x))),Times(f,Power(pd,CN1),Subst(Int(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,Power(Times(pe,f,x),pn))),p),Power(Plus(C1,Times(Sqr(f),Sqr(x))),CN1)),x),x,Times(Tan(Plus(c,Times(pd,x))),Power(f,CN1))))),FreeQ(List(a,b,c,pd,pe,m,pn,p),x))),
ISetDelayed(Int(Times(Power(Plus(Times(Power(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),pe_DEFAULT),pn_),b_DEFAULT),a_),p_),Power($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(f,FreeFactors(Cot(Plus(c,Times(pd,x))),x))),Times(CN1,f,Power(pd,CN1),Subst(Int(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,Power(Times(pe,f,x),pn))),p),Power(Plus(C1,Times(Sqr(f),Sqr(x))),CN1)),x),x,Times(Cot(Plus(c,Times(pd,x))),Power(f,CN1))))),FreeQ(List(a,b,c,pd,pe,m,pn,p),x)))
  );
}
