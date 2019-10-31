package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules102 { 
  public static IAST RULES = List( 
IIntegrate(2551,Int(Times(Log(u_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Log(u),Power(Times(b,Plus(m,C1)),CN1)),x),Dist(Power(Times(b,Plus(m,C1)),CN1),Int(SimplifyIntegrand(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),D(u,x),Power(u,CN1)),x),x),x)),And(FreeQ(List(a,b,m),x),InverseFunctionFreeQ(u,x),NeQ(m,CN1)))),
IIntegrate(2552,Int(Times(Log(u_),Power($p("§qx"),CN1)),x_Symbol),
    Condition(With(List(Set(v,IntHide(Power($s("§qx"),CN1),x))),Subtract(Simp(Times(v,Log(u)),x),Int(SimplifyIntegrand(Times(v,D(u,x),Power(u,CN1)),x),x))),And(QuadraticQ($s("§qx"),x),InverseFunctionFreeQ(u,x)))),
IIntegrate(2553,Int(Times(Log(u_),Power(u_,Times(a_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(u,Times(a,x)),Power(a,CN1)),x),Int(SimplifyIntegrand(Times(x,Power(u,Subtract(Times(a,x),C1)),D(u,x)),x),x)),And(FreeQ(a,x),InverseFunctionFreeQ(u,x)))),
IIntegrate(2554,Int(Times(Log(u_),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Subtract(Dist(Log(u),w,x),Int(SimplifyIntegrand(Times(w,D(u,x),Power(u,CN1)),x),x)),InverseFunctionFreeQ(w,x))),InverseFunctionFreeQ(u,x))),
IIntegrate(2555,Int(Times(Log(u_),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Subtract(Dist(Log(u),w,x),Int(SimplifyIntegrand(Times(w,Simplify(Times(D(u,x),Power(u,CN1)))),x),x)),InverseFunctionFreeQ(w,x))),ProductQ(u))),
IIntegrate(2556,Int(Times(Log(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,Log(v),Log(w)),x),Negate(Int(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(v,CN1)),x),x)),Negate(Int(SimplifyIntegrand(Times(x,Log(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(2557,Int(Times(Log(v_),Log(w_),u_),x_Symbol),
    Condition(With(List(Set(z,IntHide(u,x))),Condition(Plus(Dist(Times(Log(v),Log(w)),z,x),Negate(Int(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(v,CN1)),x),x)),Negate(Int(SimplifyIntegrand(Times(z,Log(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(2558,Int(Power(f_,Times(Log(u_),a_DEFAULT)),x_Symbol),
    Condition(Int(Power(u,Times(a,Log(f))),x),FreeQ(List(a,f),x))),
IIntegrate(2559,Int(u_,x_Symbol),
    Condition(With(List(Set($s("lst"),FunctionOfLog(Cancel(Times(x,u)),x))),Condition(Dist(Power(Part($s("lst"),C3),CN1),Subst(Int(Part($s("lst"),C1),x),x,Log(Part($s("lst"),C2))),x),Not(FalseQ($s("lst"))))),NonsumQ(u))),
IIntegrate(2560,Int(Times(Log(Gamma(v_)),u_DEFAULT),x_Symbol),
    Plus(Dist(Subtract(Log(Gamma(v)),LogGamma(v)),Int(u,x),x),Int(Times(u,LogGamma(v)),x))),
IIntegrate(2561,Int(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,m_DEFAULT)),Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT),b_DEFAULT,Power(x_,r_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(x,Times(p,r)),Power(Plus(Times(a,Power(x,Subtract(m,r))),Times(b,Power(Log(Times(c,Power(x,n))),q))),p)),x),And(FreeQ(List(a,b,c,m,n,p,q,r),x),IntegerQ(p)))),
IIntegrate(2562,Int(u_,x_Symbol),
    Condition(Int(DeactivateTrig(u,x),x),FunctionOfTrigOfLinearQ(u,x))),
IIntegrate(2563,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_DEFAULT),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,b,f,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,e,f,m,n),x),EqQ(Plus(m,n,C2),C0),NeQ(m,CN1)))),
IIntegrate(2564,Int(Times(Power($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(a,f),CN1),Subst(Int(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,Subtract(n,C1)))),x),x,Times(a,Sin(Plus(e,Times(f,x))))),x),And(FreeQ(List(a,e,f,m),x),IntegerQ(Times(C1D2,Subtract(n,C1))),Not(And(IntegerQ(Times(C1D2,Subtract(m,C1))),LtQ(C0,m,n)))))),
IIntegrate(2565,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_DEFAULT),Power($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Times(a,f),CN1),Subst(Int(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(x),Power(a,CN2))),Times(C1D2,Subtract(n,C1)))),x),x,Times(a,Cos(Plus(e,Times(f,x))))),x)),And(FreeQ(List(a,e,f,m),x),IntegerQ(Times(C1D2,Subtract(n,C1))),Not(And(IntegerQ(Times(C1D2,Subtract(m,C1))),GtQ(m,C0),LeQ(m,n)))))),
IIntegrate(2566,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(a,Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(n,C1)),CN1)),x)),Dist(Times(Sqr(a),Subtract(m,C1),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Int(Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,e,f),x),GtQ(m,C1),LtQ(n,CN1),Or(IntegersQ(Times(C2,m),Times(C2,n)),EqQ(Plus(m,n),C0))))),
IIntegrate(2567,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(a,Power(Times(a,Cos(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(n,C1)),CN1)),x),Dist(Times(Sqr(a),Subtract(m,C1),Power(Times(Sqr(b),Plus(n,C1)),CN1)),Int(Times(Power(Times(a,Cos(Plus(e,Times(f,x)))),Subtract(m,C2)),Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C2))),x),x)),And(FreeQ(List(a,b,e,f),x),GtQ(m,C1),LtQ(n,CN1),Or(IntegersQ(Times(C2,m),Times(C2,n)),EqQ(Plus(m,n),C0))))),
IIntegrate(2568,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(a,Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,f,Plus(m,n)),CN1)),x)),Dist(Times(Sqr(a),Subtract(m,C1),Power(Plus(m,n),CN1)),Int(Times(Power(Times(b,Cos(Plus(e,Times(f,x)))),n),Power(Times(a,Sin(Plus(e,Times(f,x)))),Subtract(m,C2))),x),x)),And(FreeQ(List(a,b,e,f,n),x),GtQ(m,C1),NeQ(Plus(m,n),C0),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2569,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(a,Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Cos(Plus(e,Times(f,x)))),Subtract(m,C1)),Power(Times(b,f,Plus(m,n)),CN1)),x),Dist(Times(Sqr(a),Subtract(m,C1),Power(Plus(m,n),CN1)),Int(Times(Power(Times(b,Sin(Plus(e,Times(f,x)))),n),Power(Times(a,Cos(Plus(e,Times(f,x)))),Subtract(m,C2))),x),x)),And(FreeQ(List(a,b,e,f,n),x),GtQ(m,C1),NeQ(Plus(m,n),C0),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2570,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(b,Cos(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(a,b,f,Plus(m,C1)),CN1)),x),Dist(Times(Plus(m,n,C2),Power(Times(Sqr(a),Plus(m,C1)),CN1)),Int(Times(Power(Times(b,Cos(Plus(e,Times(f,x)))),n),Power(Times(a,Sin(Plus(e,Times(f,x)))),Plus(m,C2))),x),x)),And(FreeQ(List(a,b,e,f,n),x),LtQ(m,CN1),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2571,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Times(b,Sin(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(a,Cos(Plus(e,Times(f,x)))),Plus(m,C1)),Power(Times(a,b,f,Plus(m,C1)),CN1)),x)),Dist(Times(Plus(m,n,C2),Power(Times(Sqr(a),Plus(m,C1)),CN1)),Int(Times(Power(Times(b,Sin(Plus(e,Times(f,x)))),n),Power(Times(a,Cos(Plus(e,Times(f,x)))),Plus(m,C2))),x),x)),And(FreeQ(List(a,b,e,f,n),x),LtQ(m,CN1),IntegersQ(Times(C2,m),Times(C2,n))))),
IIntegrate(2572,Int(Times(Sqrt(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),Sqrt(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(a,Sin(Plus(e,Times(f,x))))),Sqrt(Times(b,Cos(Plus(e,Times(f,x))))),Power(Sin(Plus(Times(C2,e),Times(C2,f,x))),CN1D2)),Int(Sqrt(Sin(Plus(Times(C2,e),Times(C2,f,x)))),x),x),FreeQ(List(a,b,e,f),x))),
IIntegrate(2573,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),CN1D2),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Sin(Plus(Times(C2,e),Times(C2,f,x)))),Power(Times(Sqrt(Times(a,Sin(Plus(e,Times(f,x))))),Sqrt(Times(b,Cos(Plus(e,Times(f,x)))))),CN1)),Int(Power(Sin(Plus(Times(C2,e),Times(C2,f,x))),CN1D2),x),x),FreeQ(List(a,b,e,f),x))),
IIntegrate(2574,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),n_),Power(Times(a_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Dist(Times(k,a,b,Power(f,CN1)),Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(Sqr(a),Times(Sqr(b),Power(x,Times(C2,k)))),CN1)),x),x,Times(Power(Times(a,Sin(Plus(e,Times(f,x)))),Power(k,CN1)),Power(Power(Times(b,Cos(Plus(e,Times(f,x)))),Power(k,CN1)),CN1))),x)),And(FreeQ(List(a,b,e,f),x),EqQ(Plus(m,n),C0),GtQ(m,C0),LtQ(m,C1)))),
IIntegrate(2575,Int(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),a_DEFAULT),m_),Power(Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Negate(Dist(Times(k,a,b,Power(f,CN1)),Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(Sqr(a),Times(Sqr(b),Power(x,Times(C2,k)))),CN1)),x),x,Times(Power(Times(a,Cos(Plus(e,Times(f,x)))),Power(k,CN1)),Power(Power(Times(b,Sin(Plus(e,Times(f,x)))),Power(k,CN1)),CN1))),x))),And(FreeQ(List(a,b,e,f),x),EqQ(Plus(m,n),C0),GtQ(m,C0),LtQ(m,C1))))
  );
}
