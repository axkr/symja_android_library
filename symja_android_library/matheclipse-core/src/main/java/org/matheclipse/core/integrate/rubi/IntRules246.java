package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IAST;
import com.google.common.base.Supplier;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules246 { 
  public static IAST RULES = List( 
IIntegrate(4921,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Sec(Plus(a,Times(b,x))),n)),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(p,C1),GtQ(m,C0)))),
IIntegrate(4922,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Csc(Plus(a,Times(b,x))),n)),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(p,C1),GtQ(m,C0)))),
IIntegrate(4923,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sqr(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Tan(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Tan(Plus(a,Times(b,x))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4924,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sqr(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Cot(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Cot(Plus(a,Times(b,x))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4925,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Sec(Plus(a,Times(b,x))),Power(Tan(Plus(a,Times(b,x))),Plus(p,CN2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),C3),Power(Tan(Plus(a,Times(b,x))),Plus(p,CN2))),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4926,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),n),Power(Tan(Plus(a,Times(b,x))),Plus(p,CN2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sec(Plus(a,Times(b,x))),Plus(n,C2)),Power(Tan(Plus(a,Times(b,x))),Plus(p,CN2))),x)),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4927,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_),Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Csc(Plus(a,Times(b,x))),Power(Cot(Plus(a,Times(b,x))),Plus(p,CN2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),C3),Power(Cot(Plus(a,Times(b,x))),Plus(p,CN2))),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4928,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),Plus(p,CN2))),x)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),Plus(n,C2)),Power(Cot(Plus(a,Times(b,x))),Plus(p,CN2))),x)),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(4929,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(Sec(Plus(a,Times(b,x))),n),Power(Tan(Plus(a,Times(b,x))),p)),x))),Subtract(Simp(Dist(Power(Plus(c,Times(d,x)),m),u,x),x),Simp(Dist(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),u),x),x),x))),And(FreeQ(List(a,b,c,d,n,p),x),IGtQ(m,C0),Or(IntegerQ(Times(C1D2,n)),IntegerQ(Times(C1D2,Plus(p,CN1))))))),
IIntegrate(4930,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(Csc(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),p)),x))),Subtract(Simp(Dist(Power(Plus(c,Times(d,x)),m),u,x),x),Simp(Dist(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),u),x),x),x))),And(FreeQ(List(a,b,c,d,n,p),x),IGtQ(m,C0),Or(IntegerQ(Times(C1D2,n)),IntegerQ(Times(C1D2,Plus(p,CN1))))))),
IIntegrate(4931,Integrate(Times(Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(C2,n),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(Times(C2,a),Times(C2,b,x))),n)),x),x),x),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(n),RationalQ(m)))),
IIntegrate(4932,Integrate(Times(Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(Csc(Plus(a,Times(b,x))),n),Power(Sec(Plus(a,Times(b,x))),p)),x))),Subtract(Simp(Dist(Power(Plus(c,Times(d,x)),m),u,x),x),Simp(Dist(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),u),x),x),x))),And(FreeQ(List(a,b,c,d),x),IntegersQ(n,p),GtQ(m,C0),NeQ(n,p)))),
IIntegrate(4933,Integrate(Times(Power(u_,m_DEFAULT),Power($(F_,v_),n_DEFAULT),Power($(G_,w_),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(F(ExpandToSum(v,x)),n),Power(G(ExpandToSum(v,x)),p)),x),And(FreeQ(list(m,n,p),x),TrigQ(FSymbol),TrigQ(GSymbol),EqQ(v,w),LinearQ(list(u,v,w),x),Not(LinearMatchQ(list(u,v,w),x))))),
IIntegrate(4934,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4935,Integrate(Times(Power(Plus(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4936,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sqr(Sec(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),Power(Plus(a_,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4937,Integrate(Times(Sqr(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),Power(Plus(Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4938,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sec(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Tan(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,x))))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4939,Integrate(Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Csc(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(4940,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(e,Times(f,x)),m),Times(Power(Sin(Plus(a,Times(b,x))),p),Power(Sin(Plus(c,Times(d,x))),q)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IGtQ(q,C0),IntegerQ(m))))
  );
}
