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
ISetDelayed(Int(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),x_Symbol),
    Condition(Plus(Times(x,PolyLog(n,Times(a,Power(Times(b,Power(x,p)),q)))),Times(CN1,p,q,Int(PolyLog(Plus(n,Negate(C1)),Times(a,Power(Times(b,Power(x,p)),q))),x))),And(And(FreeQ(List(a,b,p,q),x),RationalQ(n)),Greater(n,C0)))),
ISetDelayed(Int(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),x_Symbol),
    Condition(Plus(Times(x,PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),-1)),Times(CN1,Power(Times(p,q),-1),Int(PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),x))),And(And(FreeQ(List(a,b,p,q),x),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Times(PolyLog(n_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),-1)),x_Symbol),
    Condition(Times(PolyLog(Plus(n,C1),Times(c,Power(Plus(a,Times(b,x)),p))),Power(Times(e,p),-1)),And(FreeQ(List(a,b,c,d,e,n,p),x),ZeroQ(Plus(Times(b,d),Times(CN1,a,e)))))),
ISetDelayed(Int(Times(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),Power(x_,-1)),x_Symbol),
    Condition(Times(PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),-1)),FreeQ(List(a,b,n,p,q),x))),
ISetDelayed(Int(Times(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),PolyLog(n,Times(a,Power(Times(b,Power(x,p)),q))),Power(Plus(m,C1),-1)),Times(CN1,p,q,Power(Plus(m,C1),-1),Int(Times(Power(x,m),PolyLog(Plus(n,Negate(C1)),Times(a,Power(Times(b,Power(x,p)),q)))),x))),And(And(And(FreeQ(List(a,b,m,p,q),x),NonzeroQ(Plus(m,C1))),RationalQ(n)),Greater(n,C0)))),
ISetDelayed(Int(Times(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),-1)),Times(CN1,Plus(m,C1),Power(Times(p,q),-1),Int(Times(Power(x,m),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q)))),x))),And(And(And(FreeQ(List(a,b,m,p,q),x),NonzeroQ(Plus(m,C1))),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Times(Power(Log(Times(c_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),Power(x_,-1)),x_Symbol),
    Condition(Plus(Times(Power(Log(Times(c,Power(x,m))),r),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),-1)),Times(CN1,m,r,Power(Times(p,q),-1),Int(Times(Power(Log(Times(c,Power(x,m))),Plus(r,Negate(C1))),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(x,-1)),x))),And(And(FreeQ(List(a,b,c,m,n,q,r),x),RationalQ(r)),Greater(r,C0)))),
ISetDelayed(Int(PolyLog(n_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT))),x_Symbol),
    Condition(Plus(Times(x,PolyLog(n,Times(c,Power(Plus(a,Times(b,x)),p)))),Times(CN1,p,Int(PolyLog(Plus(n,Negate(C1)),Times(c,Power(Plus(a,Times(b,x)),p))),x)),Times(a,p,Int(Times(PolyLog(Plus(n,Negate(C1)),Times(c,Power(Plus(a,Times(b,x)),p))),Power(Plus(a,Times(b,x)),-1)),x))),And(And(FreeQ(List(a,b,c,p),x),RationalQ(n)),Greater(n,C0)))),
ISetDelayed(Int(Times(PolyLog(n_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),PolyLog(n,Times(c,Power(Plus(a,Times(b,x)),p))),Power(Plus(m,C1),-1)),Times(CN1,b,p,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),PolyLog(Plus(n,Negate(C1)),Times(c,Power(Plus(a,Times(b,x)),p))),Power(Plus(a,Times(b,x)),-1)),x))),And(And(And(FreeQ(List(a,b,c,m,p),x),RationalQ(n)),Greater(n,C0)),PositiveIntegerQ(m)))),
ISetDelayed(Int(PolyLog(n_,Times(d_DEFAULT,Power(Power($p("F"),Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT))),x_Symbol),
    Condition(Times(PolyLog(Plus(n,C1),Times(d,Power(Power($s("F"),Times(c,Plus(a,Times(b,x)))),p))),Power(Times(b,c,p,Log($s("F"))),-1)),FreeQ(List($s("F"),a,b,c,d,n,p),x))),
ISetDelayed(Int(Times(PolyLog(n_,Times(d_DEFAULT,Power(Power($p("F"),Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Plus(e,Times(f,x)),m),PolyLog(Plus(n,C1),Times(d,Power(Power($s("F"),Times(c,Plus(a,Times(b,x)))),p))),Power(Times(b,c,p,Log($s("F"))),-1)),Times(CN1,f,m,Power(Times(b,c,p,Log($s("F"))),-1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Negate(C1))),PolyLog(Plus(n,C1),Times(d,Power(Power($s("F"),Times(c,Plus(a,Times(b,x)))),p)))),x))),And(And(FreeQ(List($s("F"),a,b,c,d,e,f,n,p),x),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(u_,PolyLog(n_,v_)),x_Symbol),
    Condition(Module(List(Set(w,DerivativeDivides(v,Times(u,v),x))),Condition(Times(w,PolyLog(Plus(n,C1),v)),Not(FalseQ(w)))),FreeQ(n,x))),
ISetDelayed(Int(Times(u_,Log(w_),PolyLog(n_,v_)),x_Symbol),
    Condition(Module(List(Set(z,DerivativeDivides(v,Times(u,v),x))),Condition(Plus(Times(z,Log(w),PolyLog(Plus(n,C1),v)),Negate(Int(SimplifyIntegrand(Times(z,D(w,x),PolyLog(Plus(n,C1),v),Power(w,-1)),x),x))),Not(FalseQ(z)))),And(FreeQ(n,x),InverseFunctionFreeQ(w,x))))
  );
}
