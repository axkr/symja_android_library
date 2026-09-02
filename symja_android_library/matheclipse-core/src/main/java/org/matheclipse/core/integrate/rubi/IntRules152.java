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
class IntRules152 { 
  public static IAST RULES = List( 
IIntegrate(3041,Integrate(Log(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,Log(u)),x),Integrate(SimplifyIntegrand(Times(x,Simplify(Times(D(u,x),Power(u,CN1)))),x),x)),ProductQ(u))),
IIntegrate(3042,Integrate(Times(Log(u_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(a,Times(b,x))),Log(u),Power(b,CN1)),x),Simp(Dist(Power(b,CN1),Integrate(SimplifyIntegrand(Times(Log(Plus(a,Times(b,x))),D(u,x),Power(u,CN1)),x),x),x),x)),And(FreeQ(list(a,b),x),RationalFunctionQ(Times(D(u,x),Power(u,CN1)),x),Or(NeQ(a,C0),Not(And(BinomialQ(u,x),EqQ(Sqr(BinomialDegree(u,x)),C1))))))),
IIntegrate(3043,Integrate(Times(Log(u_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Log(u),Power(Times(b,Plus(m,C1)),CN1)),x),Simp(Dist(Power(Times(b,Plus(m,C1)),CN1),Integrate(SimplifyIntegrand(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),D(u,x),Power(u,CN1)),x),x),x),x)),And(FreeQ(list(a,b,m),x),InverseFunctionFreeQ(u,x),NeQ(m,CN1)))),
IIntegrate(3044,Integrate(Times(Log(u_),Power($p("§qx"),CN1)),x_Symbol),
    Condition(With(list(Set(v,IntHide(Power($s("§qx"),CN1),x))),Subtract(Simp(Times(v,Log(u)),x),Integrate(SimplifyIntegrand(Times(v,D(u,x),Power(u,CN1)),x),x))),And(QuadraticQ($s("§qx"),x),InverseFunctionFreeQ(u,x)))),
IIntegrate(3045,Integrate(Times(Log(u_),Power(u_,Times(a_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(u,Times(a,x)),Power(a,CN1)),x),Integrate(SimplifyIntegrand(Times(x,Power(u,Plus(Times(a,x),CN1)),D(u,x)),x),x)),And(FreeQ(a,x),InverseFunctionFreeQ(u,x)))),
IIntegrate(3046,Integrate(Times(Log(u_),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Dist(Log(u),w,x),x),Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(u,CN1)),x),x)),InverseFunctionFreeQ(w,x))),InverseFunctionFreeQ(u,x))),
IIntegrate(3047,Integrate(Times(Log(u_),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Dist(Log(u),w,x),x),Integrate(SimplifyIntegrand(Times(w,Simplify(Times(D(u,x),Power(u,CN1)))),x),x)),InverseFunctionFreeQ(w,x))),ProductQ(u))),
IIntegrate(3048,Integrate(Times(Log(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,Log(v),Log(w)),x),Negate(Integrate(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(v,CN1)),x),x)),Negate(Integrate(SimplifyIntegrand(Times(x,Log(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(3049,Integrate(Times(Log(v_),Log(w_),u_),x_Symbol),
    Condition(With(list(Set(z,IntHide(u,x))),Condition(Plus(Simp(Dist(Times(Log(v),Log(w)),z,x),x),Negate(Integrate(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(v,CN1)),x),x)),Negate(Integrate(SimplifyIntegrand(Times(z,Log(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(3050,Integrate(Power(f_,Times(Log(u_),a_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(u,Times(a,Log(f))),x),FreeQ(list(a,f),x))),
IIntegrate(3051,Integrate(u_,x_Symbol),
    Condition(With(list(Set($s("lst"),FunctionOfLog(Cancel(Times(x,u)),x))),Condition(Simp(Dist(Power(Part($s("lst"),C3),CN1),Subst(Integrate(Part($s("lst"),C1),x),x,Log(Part($s("lst"),C2))),x),x),Not(FalseQ($s("lst"))))),NonsumQ(u))),
IIntegrate(3052,Integrate(Times(Log(Gamma(v_)),u_DEFAULT),x_Symbol),
    Plus(Simp(Dist(Subtract(Log(Gamma(v)),LogGamma(v)),Integrate(u,x),x),x),Integrate(Times(u,LogGamma(v)),x))),
IIntegrate(3053,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,m_DEFAULT)),Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT),b_DEFAULT,Power(x_,r_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(x,Times(p,r)),Power(Plus(Times(a,Power(x,Subtract(m,r))),Times(b,Power(Log(Times(c,Power(x,n))),q))),p)),x),And(FreeQ(List(a,b,c,m,n,p,q,r),x),IntegerQ(p)))),
IIntegrate(3054,Integrate(u_,x_Symbol),
    Condition(Integrate(DeactivateTrig(u,x),x),FunctionOfTrigOfLinearQ(u,x))),
IIntegrate(3055,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_DEFAULT),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,b,f,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,e,f,m,n),x),EqQ(Plus(m,n,C2),C0),NeQ(m,CN1)))),
IIntegrate(3056,Integrate(Times(Power($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(a,f),CN1),Subst(Integrate(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,Plus(n,CN1)))),x),x,Times(a,Sin(Plus(e,Times(f,x))))),x),x),And(FreeQ(List(a,e,f,m),x),IntegerQ(Times(C1D2,Plus(n,CN1))),Not(And(IntegerQ(Times(C1D2,Plus(m,CN1))),LtQ(C0,m,n)))))),
IIntegrate(3057,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_DEFAULT),Power($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(Times(a,f),CN1)),Subst(Integrate(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,Plus(n,CN1)))),x),x,Times(a,Cos(Plus(e,Times(f,x))))),x),x),And(FreeQ(List(a,e,f,m),x),IntegerQ(Times(C1D2,Plus(n,CN1))),Not(And(IntegerQ(Times(C1D2,Plus(m,CN1))),GtQ(m,C0),LeQ(m,n)))))),
IIntegrate(3058,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,a,Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,CN1)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(n,C1)),CN1)),x),Simp(Dist(Times(Sqr(a),Plus(m,CN1),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Integrate(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,CN2)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C2))),x),x),x)),And(FreeQ(List(a,b,e,f),x),GtQ(m,C1),LtQ(n,CN1),Or(IntegersQ(Times(C2,m),Times(C2,n)),EqQ(Plus(m,n),C0))))),
IIntegrate(3059,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(a,Power(Times(a,Cos(Plus(e,Times(f,x)))),Plus(m,CN1)),Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(n,C1)),CN1)),x),Simp(Dist(Times(Sqr(a),Plus(m,CN1),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Integrate(Times(Power(Times(a,Cos(Plus(e,Times(f,x)))),Plus(m,CN2)),Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C2))),x),x),x)),And(FreeQ(List(a,b,e,f),x),GtQ(m,C1),LtQ(n,CN1),Or(IntegersQ(Times(C2,m),Times(C2,n)),EqQ(Plus(m,n),C0))))),
IIntegrate(3060,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,a,Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,CN1)),Power(Times(b,f,Plus(m,n)),CN1)),x),Simp(Dist(Times(Sqr(a),Plus(m,CN1),Power(Plus(m,n),CN1)),Integrate(Times(Power(Times(b,Cos(Plus(e,Times(f,x)))),n),Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,CN2))),x),x),x)),And(FreeQ(List(a,b,e,f,n),x),GtQ(m,C1),NeQ(Plus(m,n),C0),IntegersQ(Times(C2,m),Times(C2,n)))))
  );
}
