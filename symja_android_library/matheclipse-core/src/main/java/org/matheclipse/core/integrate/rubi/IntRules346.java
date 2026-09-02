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
class IntRules346 { 
  public static IAST RULES = List( 
IIntegrate(6921,Integrate(Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erf(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6922,Integrate(Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erfc(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6923,Integrate(Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erfi(Plus(a,Times(b,x))),n),x),And(FreeQ(list(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6924,Integrate(Times(Erf(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,x,Power(Pi,CN1D2),HypergeometricPFQ(list(C1D2,C1D2),list(QQ(3L,2L),QQ(3L,2L)),Times(CN1,Sqr(b),Sqr(x)))),x),FreeQ(b,x))),
IIntegrate(6925,Integrate(Times(Erfc(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Log(x),x),Integrate(Times(Erf(Times(b,x)),Power(x,CN1)),x)),FreeQ(b,x))),
IIntegrate(6926,Integrate(Times(Erfi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,x,Power(Pi,CN1D2),HypergeometricPFQ(list(C1D2,C1D2),list(QQ(3L,2L),QQ(3L,2L)),Times(Sqr(b),Sqr(x)))),x),FreeQ(b,x))),
IIntegrate(6927,Integrate(Times(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Erf(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(C2,b,Power(Times(CSqrtPi,d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6928,Integrate(Times(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Erfc(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(C2,b,Power(Times(CSqrtPi,d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6929,Integrate(Times(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Erfi(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(C2,b,Power(Times(CSqrtPi,d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Exp(Sqr(Plus(a,Times(b,x))))),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6930,Integrate(Times(Sqr(Erf(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(Erf(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(C4,b,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Erf(Times(b,x)),Power(Exp(Times(Sqr(b),Sqr(x))),CN1)),x),x),x)),And(FreeQ(b,x),Or(IGtQ(m,C0),ILtQ(Times(C1D2,Plus(m,C1)),C0))))),
IIntegrate(6931,Integrate(Times(Sqr(Erfc(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sqr(Erfc(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(C4,b,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Erfc(Times(b,x)),Power(Exp(Times(Sqr(b),Sqr(x))),CN1)),x),x),x)),And(FreeQ(b,x),Or(IGtQ(m,C0),ILtQ(Times(C1D2,Plus(m,C1)),C0))))),
IIntegrate(6932,Integrate(Times(Sqr(Erfi(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(Erfi(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(C4,b,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Times(Sqr(b),Sqr(x))),Erfi(Times(b,x))),x),x),x)),And(FreeQ(b,x),Or(IGtQ(m,C0),ILtQ(Times(C1D2,Plus(m,C1)),C0))))),
IIntegrate(6933,Integrate(Times(Sqr(Erf(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Sqr(Erf(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6934,Integrate(Times(Sqr(Erfc(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Sqr(Erfc(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6935,Integrate(Times(Sqr(Erfi(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Sqr(Erfi(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6936,Integrate(Times(Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Erf(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6937,Integrate(Times(Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Erfc(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6938,Integrate(Times(Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Erfi(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6939,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erf(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Exp(c),CSqrtPi,Power(Times(C2,b),CN1)),Subst(Integrate(Power(x,n),x),x,Erf(Times(b,x))),x),x),And(FreeQ(List(b,c,d,n),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6940,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfc(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Exp(c),CSqrtPi,Power(Times(C2,b),CN1)),Subst(Integrate(Power(x,n),x),x,Erfc(Times(b,x))),x),x),And(FreeQ(List(b,c,d,n),x),EqQ(d,Negate(Sqr(b))))))
  );
}
