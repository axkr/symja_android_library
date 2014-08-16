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
public class IntRules135 { 
  public static IAST RULES = List( 
ISetDelayed(Int(PolyLog(pn_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),x_Symbol),
    Condition(Plus(Times(x,PolyLog(pn,Times(a,Power(Times(b,Power(x,p)),q)))),Times(CN1,p,q,Int(PolyLog(Plus(pn,Times(CN1,C1)),Times(a,Power(Times(b,Power(x,p)),q))),x))),And(And(FreeQ(List(a,b,p,q),x),RationalQ(pn)),Greater(pn,C0)))),
ISetDelayed(Int(PolyLog(pn_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),x_Symbol),
    Condition(Plus(Times(x,PolyLog(Plus(pn,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),CN1)),Times(CN1,Power(Times(p,q),CN1),Int(PolyLog(Plus(pn,C1),Times(a,Power(Times(b,Power(x,p)),q))),x))),And(And(FreeQ(List(a,b,p,q),x),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Times(PolyLog(pn_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT))),Power(Plus(pd_DEFAULT,Times(x_,pe_DEFAULT)),CN1)),x_Symbol),
    Condition(Times(PolyLog(Plus(pn,C1),Times(c,Power(Plus(a,Times(b,x)),p))),Power(Times(pe,p),CN1)),And(FreeQ(List(a,b,c,pd,pe,pn,p),x),ZeroQ(Plus(Times(b,pd),Times(CN1,a,pe)))))),
ISetDelayed(Int(Times(PolyLog(pn_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),Power(x_,CN1)),x_Symbol),
    Condition(Times(PolyLog(Plus(pn,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),CN1)),FreeQ(List(a,b,pn,p,q),x))),
ISetDelayed(Int(Times(PolyLog(pn_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),PolyLog(pn,Times(a,Power(Times(b,Power(x,p)),q))),Power(Plus(m,C1),CN1)),Times(CN1,p,q,Power(Plus(m,C1),CN1),Int(Times(Power(x,m),PolyLog(Plus(pn,Times(CN1,C1)),Times(a,Power(Times(b,Power(x,p)),q)))),x))),And(And(And(FreeQ(List(a,b,m,p,q),x),NonzeroQ(Plus(m,C1))),RationalQ(pn)),Greater(pn,C0)))),
ISetDelayed(Int(Times(PolyLog(pn_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),PolyLog(Plus(pn,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),CN1)),Times(CN1,Plus(m,C1),Power(Times(p,q),CN1),Int(Times(Power(x,m),PolyLog(Plus(pn,C1),Times(a,Power(Times(b,Power(x,p)),q)))),x))),And(And(And(FreeQ(List(a,b,m,p,q),x),NonzeroQ(Plus(m,C1))),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Times(PolyLog(pn_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),Power(x_,CN1),Power(Log(Times(c_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Log(Times(c,Power(x,m))),r),PolyLog(Plus(pn,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),CN1)),Times(CN1,m,r,Power(Times(p,q),CN1),Int(Times(Power(Log(Times(c,Power(x,m))),Plus(r,Times(CN1,C1))),PolyLog(Plus(pn,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(x,CN1)),x))),And(And(FreeQ(List(a,b,c,m,pn,q,r),x),RationalQ(r)),Greater(r,C0)))),
ISetDelayed(Int(PolyLog(pn_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT))),x_Symbol),
    Condition(Plus(Times(x,PolyLog(pn,Times(c,Power(Plus(a,Times(b,x)),p)))),Times(CN1,p,Int(PolyLog(Plus(pn,Times(CN1,C1)),Times(c,Power(Plus(a,Times(b,x)),p))),x)),Times(a,p,Int(Times(PolyLog(Plus(pn,Times(CN1,C1)),Times(c,Power(Plus(a,Times(b,x)),p))),Power(Plus(a,Times(b,x)),CN1)),x))),And(And(FreeQ(List(a,b,c,p),x),RationalQ(pn)),Greater(pn,C0)))),
ISetDelayed(Int(Times(PolyLog(pn_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),PolyLog(pn,Times(c,Power(Plus(a,Times(b,x)),p))),Power(Plus(m,C1),CN1)),Times(CN1,b,p,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),PolyLog(Plus(pn,Times(CN1,C1)),Times(c,Power(Plus(a,Times(b,x)),p))),Power(Plus(a,Times(b,x)),CN1)),x))),And(And(And(FreeQ(List(a,b,c,m,p),x),RationalQ(pn)),Greater(pn,C0)),PositiveIntegerQ(m)))),
ISetDelayed(Int(PolyLog(pn_,Times(pd_DEFAULT,Power(Power(pf_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT))),x_Symbol),
    Condition(Times(PolyLog(Plus(pn,C1),Times(pd,Power(Power(pf,Times(c,Plus(a,Times(b,x)))),p))),Power(Times(b,c,p,Log(pf)),CN1)),FreeQ(List(pf,a,b,c,pd,pn,p),x))),
ISetDelayed(Int(Times(PolyLog(pn_,Times(pd_DEFAULT,Power(Power(pf_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT))),Power(Plus(pe_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Plus(pe,Times(f,x)),m),PolyLog(Plus(pn,C1),Times(pd,Power(Power(pf,Times(c,Plus(a,Times(b,x)))),p))),Power(Times(b,c,p,Log(pf)),CN1)),Times(CN1,f,m,Power(Times(b,c,p,Log(pf)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),PolyLog(Plus(pn,C1),Times(pd,Power(Power(pf,Times(c,Plus(a,Times(b,x)))),p)))),x))),And(And(FreeQ(List(pf,a,b,c,pd,pe,f,pn,p),x),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(u_,PolyLog(pn_,v_)),x_Symbol),
    Condition(Module(List(Set(w,DerivativeDivides(v,Times(u,v),x))),Condition(Times(w,PolyLog(Plus(pn,C1),v)),Not(FalseQ(w)))),FreeQ(pn,x))),
ISetDelayed(Int(Times(u_,Log(w_),PolyLog(pn_,v_)),x_Symbol),
    Condition(Module(List(Set(z,DerivativeDivides(v,Times(u,v),x))),Condition(Plus(Times(z,Log(w),PolyLog(Plus(pn,C1),v)),Times(CN1,Int(SimplifyIntegrand(Times(z,D(w,x),PolyLog(Plus(pn,C1),v),Power(w,CN1)),x),x))),Not(FalseQ(z)))),And(FreeQ(pn,x),InverseFunctionFreeQ(w,x))))
  );
}
