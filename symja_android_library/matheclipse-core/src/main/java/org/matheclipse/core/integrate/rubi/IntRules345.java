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
class IntRules345 { 
  public static IAST RULES = List( 
IIntegrate(6901,Integrate(Times(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(ArcSech(Times(a,Power(x,p)))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(p,Power(Times(a,Plus(m,C1)),CN1)),Integrate(Power(x,Subtract(m,p)),x),x),x),Simp(Dist(Times(p,Sqrt(Plus(C1,Times(a,Power(x,p)))),Power(Times(a,Plus(m,C1)),CN1),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1))),Integrate(Times(Power(x,Subtract(m,p)),Power(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p))))),CN1)),x),x),x)),And(FreeQ(list(a,m,p),x),NeQ(m,CN1)))),
IIntegrate(6902,Integrate(Times(Exp(ArcCsch(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Dist(Power(a,CN1),Integrate(Power(x,Subtract(m,p)),x),x),x),Integrate(Times(Power(x,m),Sqrt(Plus(C1,Power(Times(Sqr(a),Power(x,Times(C2,p))),CN1)))),x)),FreeQ(list(a,m,p),x))),
IIntegrate(6903,Integrate(Times(Exp(Times(ArcSech(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Times(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))))),n)),x),And(FreeQ(m,x),IntegerQ(n)))),
IIntegrate(6904,Integrate(Times(Exp(Times(ArcCsch(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(Power(u,CN1),Sqrt(Plus(C1,Power(u,CN2)))),n)),x),And(FreeQ(m,x),IntegerQ(n)))),
IIntegrate(6905,Integrate(Times(Exp(ArcSech(Times(c_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(Power(Times(a,c),CN1),Integrate(Times(Sqrt(Power(Plus(C1,Times(c,x)),CN1)),Power(Times(x,Sqrt(Subtract(C1,Times(c,x)))),CN1)),x),x),x),Simp(Dist(Power(c,CN1),Integrate(Power(Times(x,Plus(a,Times(b,Sqr(x)))),CN1),x),x),x)),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6906,Integrate(Times(Exp(ArcCsch(Times(c_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(Power(Times(a,Sqr(c)),CN1),Integrate(Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x),x),x),Simp(Dist(Power(c,CN1),Integrate(Power(Times(x,Plus(a,Times(b,Sqr(x)))),CN1),x),x),x)),And(FreeQ(list(a,b,c),x),EqQ(Subtract(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6907,Integrate(Times(Exp(ArcSech(Times(c_DEFAULT,x_))),Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(Times(d,Power(Times(a,c),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,CN1)),Sqrt(Power(Plus(C1,Times(c,x)),CN1)),Power(Subtract(C1,Times(c,x)),CN1D2)),x),x),x),Simp(Dist(Times(d,Power(c,CN1)),Integrate(Times(Power(Times(d,x),Plus(m,CN1)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6908,Integrate(Times(Exp(ArcCsch(Times(c_DEFAULT,x_))),Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(Times(Sqr(d),Power(Times(a,Sqr(c)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,CN2)),Power(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x),x),x),Simp(Dist(Times(d,Power(c,CN1)),Integrate(Times(Power(Times(d,x),Plus(m,CN1)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Subtract(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6909,Integrate(ArcSech(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcSech(u)),x),Simp(Dist(Times(Sqrt(Subtract(C1,Sqr(u))),Power(Times(u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6910,Integrate(ArcCsch(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCsch(u)),x),Simp(Dist(Times(u,Power(Negate(Sqr(u)),CN1D2)),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6911,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSech(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Sqrt(Subtract(C1,Sqr(u))),Power(Times(d,Plus(m,C1),u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6912,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCsch(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,u,Power(Times(d,Plus(m,C1),Sqrt(Negate(Sqr(u)))),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6913,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Plus(Simp(Dist(Plus(a,Times(b,ArcSech(u))),w,x),x),Simp(Dist(Times(b,Sqrt(Subtract(C1,Sqr(u))),Power(Times(u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(6914,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Dist(Plus(a,Times(b,ArcCsch(u))),w,x),x),Simp(Dist(Times(b,u,Power(Negate(Sqr(u)),CN1D2)),Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(6915,Integrate(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Power(Times(b,CSqrtPi,Exp(Sqr(Plus(a,Times(b,x))))),CN1),x)),FreeQ(list(a,b),x))),
IIntegrate(6916,Integrate(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Power(Times(b,CSqrtPi,Exp(Sqr(Plus(a,Times(b,x))))),CN1),x)),FreeQ(list(a,b),x))),
IIntegrate(6917,Integrate(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Erfi(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Exp(Sqr(Plus(a,Times(b,x)))),Power(Times(b,CSqrtPi),CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6918,Integrate(Sqr(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(Erf(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Dist(Times(C4,Power(Pi,CN1D2)),Integrate(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x),x),x)),FreeQ(list(a,b),x))),
IIntegrate(6919,Integrate(Sqr(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Sqr(Erfc(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Dist(Times(C4,Power(Pi,CN1D2)),Integrate(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x),x),x)),FreeQ(list(a,b),x))),
IIntegrate(6920,Integrate(Sqr(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(Erfi(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Dist(Times(C4,Power(Pi,CN1D2)),Integrate(Times(Plus(a,Times(b,x)),Exp(Sqr(Plus(a,Times(b,x)))),Erfi(Plus(a,Times(b,x)))),x),x),x)),FreeQ(list(a,b),x)))
  );
}
