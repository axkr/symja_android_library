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
class IntRules299 { 
  public static IAST RULES = List( 
IIntegrate(5981,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Sinh(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Sinh(Plus(a,Times(b,x))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(5982,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Cosh(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Cosh(Plus(a,Times(b,x))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(5983,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(c,Times(d,x)),m),Times(Power(Sinh(Plus(a,Times(b,x))),n),Power(Cosh(Plus(a,Times(b,x))),p)),x),x),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(5984,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sinh(Plus(a,Times(b,x))),n),Power(Tanh(Plus(a,Times(b,x))),Plus(p,CN2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sinh(Plus(a,Times(b,x))),Plus(n,CN2)),Power(Tanh(Plus(a,Times(b,x))),p)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(5985,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Cosh(Plus(a,Times(b,x))),n),Power(Coth(Plus(a,Times(b,x))),Plus(p,CN2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Cosh(Plus(a,Times(b,x))),Plus(n,CN2)),Power(Coth(Plus(a,Times(b,x))),p)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(5986,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Sech(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Sech(Plus(a,Times(b,x))),n)),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(p,C1),GtQ(m,C0)))),
IIntegrate(5987,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Csch(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Csch(Plus(a,Times(b,x))),n)),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(p,C1),GtQ(m,C0)))),
IIntegrate(5988,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sqr(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Tanh(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Tanh(Plus(a,Times(b,x))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(5989,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sqr(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Coth(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Power(Coth(Plus(a,Times(b,x))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(5990,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Subtract(Integrate(Times(Power(Plus(c,Times(d,x)),m),Sech(Plus(a,Times(b,x))),Power(Tanh(Plus(a,Times(b,x))),Plus(p,CN2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sech(Plus(a,Times(b,x))),C3),Power(Tanh(Plus(a,Times(b,x))),Plus(p,CN2))),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(5991,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Subtract(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sech(Plus(a,Times(b,x))),n),Power(Tanh(Plus(a,Times(b,x))),Plus(p,CN2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sech(Plus(a,Times(b,x))),Plus(n,C2)),Power(Tanh(Plus(a,Times(b,x))),Plus(p,CN2))),x)),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(5992,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_),Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(Power(Plus(c,Times(d,x)),m),Csch(Plus(a,Times(b,x))),Power(Coth(Plus(a,Times(b,x))),Plus(p,CN2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csch(Plus(a,Times(b,x))),C3),Power(Coth(Plus(a,Times(b,x))),Plus(p,CN2))),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(5993,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csch(Plus(a,Times(b,x))),n),Power(Coth(Plus(a,Times(b,x))),Plus(p,CN2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csch(Plus(a,Times(b,x))),Plus(n,C2)),Power(Coth(Plus(a,Times(b,x))),Plus(p,CN2))),x)),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(5994,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Sech(Plus(a,Times(b,x))),n),Power(Tanh(Plus(a,Times(b,x))),p)),x))),Subtract(Simp(Dist(Power(Plus(c,Times(d,x)),m),u,x),x),Simp(Dist(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),u),x),x),x))),And(FreeQ(List(a,b,c,d,n,p),x),IGtQ(m,C0),Or(IntegerQ(Times(C1D2,n)),IntegerQ(Times(C1D2,Plus(p,CN1))))))),
IIntegrate(5995,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Csch(Plus(a,Times(b,x))),n),Power(Coth(Plus(a,Times(b,x))),p)),x))),Subtract(Simp(Dist(Power(Plus(c,Times(d,x)),m),u,x),x),Simp(Dist(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),u),x),x),x))),And(FreeQ(List(a,b,c,d,n,p),x),IGtQ(m,C0),Or(IntegerQ(Times(C1D2,n)),IntegerQ(Times(C1D2,Plus(p,CN1))))))),
IIntegrate(5996,Integrate(Times(Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(C2,n),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csch(Plus(Times(C2,a),Times(C2,b,x))),n)),x),x),x),And(FreeQ(List(a,b,c,d),x),RationalQ(m),IntegerQ(n)))),
IIntegrate(5997,Integrate(Times(Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Csch(Plus(a,Times(b,x))),n),Power(Sech(Plus(a,Times(b,x))),p)),x))),Subtract(Simp(Dist(Power(Plus(c,Times(d,x)),m),u,x),x),Simp(Dist(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),u),x),x),x))),And(FreeQ(List(a,b,c,d),x),IntegersQ(n,p),GtQ(m,C0),NeQ(n,p)))),
IIntegrate(5998,Integrate(Times(Power(u_,m_DEFAULT),Power($(F_,v_),n_DEFAULT),Power($(G_,w_),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(F(ExpandToSum(v,x)),n),Power(G(ExpandToSum(v,x)),p)),x),And(FreeQ(list(m,n,p),x),HyperbolicQ(FSymbol),HyperbolicQ(GSymbol),EqQ(v,w),LinearQ(list(u,v,w),x),Not(LinearMatchQ(list(u,v,w),x))))),
IIntegrate(5999,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(6000,Integrate(Times(Power(Plus(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),Plus(n,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),NeQ(n,CN1))))
  );
}
