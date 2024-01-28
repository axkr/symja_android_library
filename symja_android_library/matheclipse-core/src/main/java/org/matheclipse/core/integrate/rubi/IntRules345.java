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
class IntRules345 { 
  public static IAST RULES = List( 
IIntegrate(6901,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Plus(Simp(Star(Plus(a,Times(b,ArcSech(u))),w),x),Simp(Star(Times(b,Sqrt(Subtract(C1,Sqr(u))),Power(Times(u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x)),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(6902,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Star(Plus(a,Times(b,ArcCsch(u))),w),x),Simp(Star(Times(b,u,Power(Negate(Sqr(u)),CN1D2)),Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x)),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(6903,Integrate(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Power(Times(b,CSqrtPi,Exp(Sqr(Plus(a,Times(b,x))))),CN1),x)),FreeQ(list(a,b),x))),
IIntegrate(6904,Integrate(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Power(Times(b,CSqrtPi,Exp(Sqr(Plus(a,Times(b,x))))),CN1),x)),FreeQ(list(a,b),x))),
IIntegrate(6905,Integrate(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Erfi(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Exp(Sqr(Plus(a,Times(b,x)))),Power(Times(b,CSqrtPi),CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6906,Integrate(Sqr(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(Erf(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Star(Times(C4,Power(Pi,CN1D2)),Integrate(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(6907,Integrate(Sqr(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Sqr(Erfc(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Star(Times(C4,Power(Pi,CN1D2)),Integrate(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(6908,Integrate(Sqr(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(Erfi(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Star(Times(C4,Power(Pi,CN1D2)),Integrate(Times(Plus(a,Times(b,x)),Exp(Sqr(Plus(a,Times(b,x)))),Erfi(Plus(a,Times(b,x)))),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(6909,Integrate(Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erf(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6910,Integrate(Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erfc(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6911,Integrate(Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erfi(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6912,Integrate(Times(Erf(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,x,Power(Pi,CN1D2),HypergeometricPFQ(list(C1D2,C1D2),list(QQ(3L,2L),QQ(3L,2L)),Times(CN1,Sqr(b),Sqr(x)))),x),FreeQ(b,x))),
IIntegrate(6913,Integrate(Times(Erfc(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Log(x),x),Integrate(Times(Erf(Times(b,x)),Power(x,CN1)),x)),FreeQ(b,x))),
IIntegrate(6914,Integrate(Times(Erfi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,x,Power(Pi,CN1D2),HypergeometricPFQ(list(C1D2,C1D2),list(QQ(3L,2L),QQ(3L,2L)),Times(Sqr(b),Sqr(x)))),x),FreeQ(b,x))),
IIntegrate(6915,Integrate(Times(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Erf(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,b,Power(Times(CSqrtPi,d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6916,Integrate(Times(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Erfc(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,b,Power(Times(CSqrtPi,d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6917,Integrate(Times(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Erfi(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,b,Power(Times(CSqrtPi,d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Exp(Sqr(Plus(a,Times(b,x))))),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6918,Integrate(Times(Sqr(Erf(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(Erf(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(C4,b,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Erf(Times(b,x)),Power(Exp(Times(Sqr(b),Sqr(x))),CN1)),x)),x)),And(FreeQ(b,x),Or(IGtQ(m,C0),ILtQ(Times(C1D2,Plus(m,C1)),C0))))),
IIntegrate(6919,Integrate(Times(Sqr(Erfc(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sqr(Erfc(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(C4,b,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Erfc(Times(b,x)),Power(Exp(Times(Sqr(b),Sqr(x))),CN1)),x)),x)),And(FreeQ(b,x),Or(IGtQ(m,C0),ILtQ(Times(C1D2,Plus(m,C1)),C0))))),
IIntegrate(6920,Integrate(Times(Sqr(Erfi(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(Erfi(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(C4,b,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Times(Sqr(b),Sqr(x))),Erfi(Times(b,x))),x)),x)),And(FreeQ(b,x),Or(IGtQ(m,C0),ILtQ(Times(C1D2,Plus(m,C1)),C0)))))
  );
}
