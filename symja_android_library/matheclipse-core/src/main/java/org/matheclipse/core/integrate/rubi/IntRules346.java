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
class IntRules346 { 
  public static IAST RULES = List( 
IIntegrate(6921,Integrate(Times(Sqr(Erf(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Sqr(Erf(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6922,Integrate(Times(Sqr(Erfc(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Sqr(Erfc(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6923,Integrate(Times(Sqr(Erfi(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Sqr(Erfi(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6924,Integrate(Times(Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Erf(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6925,Integrate(Times(Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Erfc(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6926,Integrate(Times(Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Erfi(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6927,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erf(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Exp(c),CSqrtPi,Power(Times(C2,b),CN1)),Subst(Integrate(Power(x,n),x),x,Erf(Times(b,x)))),x),And(FreeQ(List(b,c,d,n),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6928,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfc(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Exp(c),CSqrtPi,Power(Times(C2,b),CN1)),Subst(Integrate(Power(x,n),x),x,Erfc(Times(b,x)))),x),And(FreeQ(List(b,c,d,n),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6929,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfi(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Exp(c),CSqrtPi,Power(Times(C2,b),CN1)),Subst(Integrate(Power(x,n),x),x,Erfi(Times(b,x)))),x),And(FreeQ(List(b,c,d,n),x),EqQ(d,Sqr(b))))),
IIntegrate(6930,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(b,Exp(c),Sqr(x),Power(Pi,CN1D2),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(Sqr(b),Sqr(x)))),x),And(FreeQ(list(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6931,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Integrate(Exp(Plus(c,Times(d,Sqr(x)))),x),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x)),And(FreeQ(list(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6932,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(b,Exp(c),Sqr(x),Power(Pi,CN1D2),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(CN1,Sqr(b),Sqr(x)))),x),And(FreeQ(list(b,c,d),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6933,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Erf(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6934,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Erfc(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6935,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Erfi(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6936,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Simp(Star(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x)))),x)),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(6937,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_),x_Symbol),
    Condition(Plus(Simp(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Simp(Star(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x)))),x)),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(6938,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Simp(Star(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Exp(Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),d),Sqr(x)))),x)),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(6939,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Negate(Simp(Star(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x)))),x)),x)),Negate(Simp(Star(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x)),x))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C1)))),
IIntegrate(6940,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Negate(Simp(Star(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x)))),x)),x)),Simp(Star(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C1))))
  );
}
