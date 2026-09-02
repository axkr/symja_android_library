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
class IntRules290 { 
  public static IAST RULES = List( 
IIntegrate(5801,Integrate(Times(u_DEFAULT,Power(f_,Times(Power(ArcSec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Simp(Dist(Power(b,CN1),Subst(Integrate(Times(ReplaceAll(u,Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Sec(x),Power(b,CN1))))),Power(f,Times(c,Power(x,n))),Sec(x),Tan(x)),x),x,ArcSec(Plus(a,Times(b,x)))),x),x),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0)))),
IIntegrate(5802,Integrate(Times(u_DEFAULT,Power(f_,Times(Power(ArcCsc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Simp(Dist(Negate(Power(b,CN1)),Subst(Integrate(Times(ReplaceAll(u,Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Csc(x),Power(b,CN1))))),Power(f,Times(c,Power(x,n))),Csc(x),Cot(x)),x),x,ArcCsc(Plus(a,Times(b,x)))),x),x),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0)))),
IIntegrate(5803,Integrate(ArcSec(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSec(u)),x),Simp(Dist(Times(u,Power(Sqr(u),CN1D2)),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Plus(Sqr(u),CN1))),CN1)),x),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(5804,Integrate(ArcCsc(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCsc(u)),x),Simp(Dist(Times(u,Power(Sqr(u),CN1D2)),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Plus(Sqr(u),CN1))),CN1)),x),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(5805,Integrate(Times(Plus(a_DEFAULT,Times(ArcSec(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSec(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,u,Power(Times(d,Plus(m,C1),Sqrt(Sqr(u))),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Plus(Sqr(u),CN1))),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(5806,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsc(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCsc(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,u,Power(Times(d,Plus(m,C1),Sqrt(Sqr(u))),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Plus(Sqr(u),CN1))),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(5807,Integrate(Times(Plus(a_DEFAULT,Times(ArcSec(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Dist(Plus(a,Times(b,ArcSec(u))),w,x),x),Simp(Dist(Times(b,u,Power(Sqr(u),CN1D2)),Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Plus(Sqr(u),CN1))),CN1)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(5808,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsc(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Plus(Simp(Dist(Plus(a,Times(b,ArcCsc(u))),w,x),x),Simp(Dist(Times(b,u,Power(Sqr(u),CN1D2)),Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Plus(Sqr(u),CN1))),CN1)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(5809,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Sinh(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(5810,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(v_),b_DEFAULT)),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Cosh(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(5811,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Sinh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0)))),
IIntegrate(5812,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Cosh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0)))),
IIntegrate(5813,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Dist(Times(Plus(Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x))),Power(Power(x,n),CN1)),x),x),x)),Negate(Simp(Dist(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x)))),x),x),x))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),IGtQ(n,C0),LtQ(p,CN1),GtQ(n,C2)))),
IIntegrate(5814,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Dist(Times(Plus(Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x))),Power(Power(x,n),CN1)),x),x),x)),Negate(Simp(Dist(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x)))),x),x),x))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),IGtQ(n,C0),LtQ(p,CN1),GtQ(n,C2)))),
IIntegrate(5815,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Sinh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1))))),
IIntegrate(5816,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Cosh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1))))),
IIntegrate(5817,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Sinh(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(5818,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Cosh(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(5819,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(x,n))),p),Sinh(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(5820,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(x,n))),p),Cosh(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,n,p),x)))
  );
}
