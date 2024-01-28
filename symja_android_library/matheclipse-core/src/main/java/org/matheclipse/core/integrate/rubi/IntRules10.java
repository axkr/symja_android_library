package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules10 { 
  public static IAST RULES = List( 
IIntegrate(201,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,u_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,u_)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,u_)),p_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,u_)),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Plus(e,Times(f,x)),p),Power(Plus(g,Times(h,x)),q)),x),x,u)),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n,p,q),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(202,Integrate(Power(Times(i_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),p_),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),q_)),r_),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(i,Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Plus(e,Times(f,x)),p),Power(Plus(g,Times(h,x)),q)),r),Power(Times(Power(Plus(a,Times(b,x)),Times(m,r)),Power(Plus(c,Times(d,x)),Times(n,r)),Power(Plus(e,Times(f,x)),Times(p,r)),Power(Plus(g,Times(h,x)),Times(q,r))),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Times(m,r)),Power(Plus(c,Times(d,x)),Times(n,r)),Power(Plus(e,Times(f,x)),Times(p,r)),Power(Plus(g,Times(h,x)),Times(q,r))),x)),x),FreeQ(List(a,b,c,d,e,f,g,h,i,m,n,p,q,r),x))),
IIntegrate(203,Integrate(Power(u_,m_),x_Symbol),
    Condition(Integrate(Power(ExpandToSum(u,x),m),x),And(FreeQ(m,x),LinearQ(u,x),Not(LinearMatchQ(u,x))))),
IIntegrate(204,Integrate(Times(Power(u_,m_DEFAULT),Power(v_,n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),n)),x),And(FreeQ(list(m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(205,Integrate(Times(Power(u_,m_DEFAULT),Power(v_,n_DEFAULT),Power(w_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),n),Power(ExpandToSum(w,x),p)),x),And(FreeQ(list(m,n,p),x),LinearQ(list(u,v,w),x),Not(LinearMatchQ(list(u,v,w),x))))),
IIntegrate(206,Integrate(Times(Power(u_,m_DEFAULT),Power(v_,n_DEFAULT),Power(w_,p_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),n),Power(ExpandToSum(w,x),p),Power(ExpandToSum(z,x),q)),x),And(FreeQ(List(m,n,p,q),x),LinearQ(List(u,v,w,z),x),Not(LinearMatchQ(List(u,v,w,z),x))))),
IIntegrate(207,Integrate(Power(Times(b_DEFAULT,Sqr(x_)),p_),x_Symbol),
    Condition(Simp(Star(Times(Power(b,IntPart(p)),Power(Times(b,Sqr(x)),FracPart(p)),Power(Power(x,Times(C2,FracPart(p))),CN1)),Integrate(Power(x,Times(C2,p)),x)),x),FreeQ(list(b,p),x))),
IIntegrate(208,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,2L)),x_Symbol),
    Condition(Simp(Times(x,Power(Times(a,Sqrt(Plus(a,Times(b,Sqr(x))))),CN1)),x),FreeQ(list(a,b),x))),
IIntegrate(209,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,a,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(Times(C2,p),C3),Power(Times(C2,a,Plus(p,C1)),CN1)),Integrate(Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),x)),x)),And(FreeQ(list(a,b),x),ILtQ(Plus(p,QQ(3L,2L)),C0)))),
IIntegrate(210,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Sqr(x))),p),x),x),And(FreeQ(list(a,b),x),IGtQ(p,C0)))),
IIntegrate(211,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,Sqr(x))),p),Power(Plus(Times(C2,p),C1),CN1)),x),Simp(Star(Times(C2,a,p,Power(Plus(Times(C2,p),C1),CN1)),Integrate(Power(Plus(a,Times(b,Sqr(x))),Subtract(p,C1)),x)),x)),And(FreeQ(list(a,b),x),GtQ(p,C0),Or(IntegerQ(Times(C4,p)),IntegerQ(Times(C6,p)))))),
IIntegrate(212,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-5L,4L)),x_Symbol),
    Condition(Simp(Times(C2,Power(Times(Power(a,QQ(5L,4L)),Rt(Times(b,Power(a,CN1)),C2)),CN1),EllipticE(Times(C1D2,ArcTan(Times(Rt(Times(b,Power(a,CN1)),C2),x))),C2)),x),And(FreeQ(list(a,b),x),GtQ(a,C0),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(213,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-5L,4L)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),C1D4),Power(Times(a,Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1)),Integrate(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),QQ(-5L,4L)),x)),x),And(FreeQ(list(a,b),x),PosQ(a),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(214,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-7L,6L)),x_Symbol),
    Condition(Simp(Star(Power(Times(Power(Plus(a,Times(b,Sqr(x))),QQ(2L,3L)),Power(Times(a,Power(Plus(a,Times(b,Sqr(x))),CN1)),QQ(2L,3L))),CN1),Subst(Integrate(Power(Subtract(C1,Times(b,Sqr(x))),CN1D3),x),x,Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),FreeQ(list(a,b),x))),
IIntegrate(215,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,a,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(Times(C2,p),C3),Power(Times(C2,a,Plus(p,C1)),CN1)),Integrate(Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),x)),x)),And(FreeQ(list(a,b),x),LtQ(p,CN1),Or(IntegerQ(Times(C4,p)),IntegerQ(Times(C6,p)))))),
IIntegrate(216,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(Simp(Times(Power(Times(Rt(a,C2),Rt(b,C2)),CN1),ArcTan(Times(Rt(b,C2),x,Power(Rt(a,C2),CN1)))),x),And(FreeQ(list(a,b),x),PosQ(Times(a,Power(b,CN1))),Or(GtQ(a,C0),GtQ(b,C0))))),
IIntegrate(217,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(Simp(Times(CN1,Power(Times(Rt(Negate(a),C2),Rt(Negate(b),C2)),CN1),ArcTan(Times(Rt(Negate(b),C2),x,Power(Rt(Negate(a),C2),CN1)))),x),And(FreeQ(list(a,b),x),PosQ(Times(a,Power(b,CN1))),Or(LtQ(a,C0),LtQ(b,C0))))),
IIntegrate(218,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(Simp(Times(Rt(Times(a,Power(b,CN1)),C2),Power(a,CN1),ArcTan(Times(x,Power(Rt(Times(a,Power(b,CN1)),C2),CN1)))),x),And(FreeQ(list(a,b),x),PosQ(Times(a,Power(b,CN1)))))),
IIntegrate(219,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(Simp(Times(Power(Times(Rt(a,C2),Rt(Negate(b),C2)),CN1),ArcTanh(Times(Rt(Negate(b),C2),x,Power(Rt(a,C2),CN1)))),x),And(FreeQ(list(a,b),x),NegQ(Times(a,Power(b,CN1))),Or(GtQ(a,C0),LtQ(b,C0))))),
IIntegrate(220,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(Simp(Times(CN1,Power(Times(Rt(Negate(a),C2),Rt(b,C2)),CN1),ArcTanh(Times(Rt(b,C2),x,Power(Rt(Negate(a),C2),CN1)))),x),And(FreeQ(list(a,b),x),NegQ(Times(a,Power(b,CN1))),Or(LtQ(a,C0),GtQ(b,C0)))))
  );
}
