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
class IntRules317 { 
  public static IAST RULES = List( 
IIntegrate(6341,Integrate(Times(Exp(ArcSech(Times(c_DEFAULT,x_))),Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(Times(d,Power(Times(a,c),CN1)),Integrate(Times(Power(Times(d,x),Subtract(m,C1)),Sqrt(Power(Plus(C1,Times(c,x)),CN1)),Power(Subtract(C1,Times(c,x)),CN1D2)),x),x),Dist(Times(d,Power(c,CN1)),Integrate(Times(Power(Times(d,x),Subtract(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6342,Integrate(Times(Exp(ArcCsch(Times(c_DEFAULT,x_))),Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(Times(Sqr(d),Power(Times(a,Sqr(c)),CN1)),Integrate(Times(Power(Times(d,x),Subtract(m,C2)),Power(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x),x),Dist(Times(d,Power(c,CN1)),Integrate(Times(Power(Times(d,x),Subtract(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Subtract(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6343,Integrate(ArcSech(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcSech(u)),x),Dist(Times(Sqrt(Subtract(C1,Sqr(u))),Power(Times(u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6344,Integrate(ArcCsch(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCsch(u)),x),Dist(Times(u,Power(Negate(Sqr(u)),CN1D2)),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6345,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSech(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Sqrt(Subtract(C1,Sqr(u))),Power(Times(d,Plus(m,C1),u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6346,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCsch(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,u,Power(Times(d,Plus(m,C1),Sqrt(Negate(Sqr(u)))),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6347,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcSech(u))),w,x),Dist(Times(b,Sqrt(Subtract(C1,Sqr(u))),Power(Times(u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(6348,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Dist(Plus(a,Times(b,ArcCsch(u))),w,x),Dist(Times(b,u,Power(Negate(Sqr(u)),CN1D2)),Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(6349,Integrate(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Power(Times(b,Sqrt(Pi),Exp(Sqr(Plus(a,Times(b,x))))),CN1),x)),FreeQ(list(a,b),x))),
IIntegrate(6350,Integrate(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Power(Times(b,Sqrt(Pi),Exp(Sqr(Plus(a,Times(b,x))))),CN1),x)),FreeQ(list(a,b),x))),
IIntegrate(6351,Integrate(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Erfi(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Exp(Sqr(Plus(a,Times(b,x)))),Power(Times(b,Sqrt(Pi)),CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6352,Integrate(Sqr(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(Erf(Plus(a,Times(b,x)))),Power(b,CN1)),x),Dist(Times(C4,Power(Pi,CN1D2)),Integrate(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x),x)),FreeQ(list(a,b),x))),
IIntegrate(6353,Integrate(Sqr(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Sqr(Erfc(Plus(a,Times(b,x)))),Power(b,CN1)),x),Dist(Times(C4,Power(Pi,CN1D2)),Integrate(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x),x)),FreeQ(list(a,b),x))),
IIntegrate(6354,Integrate(Sqr(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(Erfi(Plus(a,Times(b,x)))),Power(b,CN1)),x),Dist(Times(C4,Power(Pi,CN1D2)),Integrate(Times(Plus(a,Times(b,x)),Exp(Sqr(Plus(a,Times(b,x)))),Erfi(Plus(a,Times(b,x)))),x),x)),FreeQ(list(a,b),x))),
IIntegrate(6355,Integrate(Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erf(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6356,Integrate(Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erfc(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6357,Integrate(Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erfi(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6358,Integrate(Times(Erf(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,x,HypergeometricPFQ(list(C1D2,C1D2),list(QQ(3L,2L),QQ(3L,2L)),Times(CN1,Sqr(b),Sqr(x))),Power(Pi,CN1D2)),x),FreeQ(b,x))),
IIntegrate(6359,Integrate(Times(Erfc(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Log(x),x),Integrate(Times(Erf(Times(b,x)),Power(x,CN1)),x)),FreeQ(b,x))),
IIntegrate(6360,Integrate(Times(Erfi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,x,HypergeometricPFQ(list(C1D2,C1D2),list(QQ(3L,2L),QQ(3L,2L)),Times(Sqr(b),Sqr(x))),Power(Pi,CN1D2)),x),FreeQ(b,x)))
  );
}
