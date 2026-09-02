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
class IntRules347 { 
  public static IAST RULES = List( 
IIntegrate(6941,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfi(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Exp(c),CSqrtPi,Power(Times(C2,b),CN1)),Subst(Integrate(Power(x,n),x),x,Erfi(Times(b,x))),x),x),And(FreeQ(List(b,c,d,n),x),EqQ(d,Sqr(b))))),
IIntegrate(6942,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(b,Exp(c),Sqr(x),Power(Pi,CN1D2),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(Sqr(b),Sqr(x)))),x),And(FreeQ(list(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6943,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Integrate(Exp(Plus(c,Times(d,Sqr(x)))),x),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x)),And(FreeQ(list(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6944,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(b,Exp(c),Sqr(x),Power(Pi,CN1D2),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(CN1,Sqr(b),Sqr(x)))),x),And(FreeQ(list(b,c,d),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6945,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Erf(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6946,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Erfc(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6947,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Erfi(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6948,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Simp(Dist(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x)))),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(6949,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_),x_Symbol),
    Condition(Plus(Simp(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Simp(Dist(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x)))),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(6950,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Simp(Dist(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Exp(Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),d),Sqr(x)))),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(6951,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,CN1)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Negate(Simp(Dist(Times(Plus(m,CN1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Plus(m,CN2)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x)))),x),x),x)),Negate(Simp(Dist(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x),x),x))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C1)))),
IIntegrate(6952,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,CN1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Negate(Simp(Dist(Times(Plus(m,CN1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Plus(m,CN2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x)))),x),x),x)),Simp(Dist(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C1)))),
IIntegrate(6953,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,CN1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Negate(Simp(Dist(Times(Plus(m,CN1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Plus(m,CN2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x)))),x),x),x)),Negate(Simp(Dist(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Exp(Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),d),Sqr(x))))),x),x),x))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C1)))),
IIntegrate(6954,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,Exp(c),x,Power(Pi,CN1D2),HypergeometricPFQ(list(C1D2,C1),list(QQ(3L,2L),QQ(3L,2L)),Times(Sqr(b),Sqr(x)))),x),And(FreeQ(list(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6955,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(x,CN1)),x),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x)),Power(x,CN1)),x)),And(FreeQ(list(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6956,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,Exp(c),x,Power(Pi,CN1D2),HypergeometricPFQ(list(C1D2,C1),list(QQ(3L,2L),QQ(3L,2L)),Times(CN1,Sqr(b),Sqr(x)))),x),And(FreeQ(list(b,c,d),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6957,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x)))),x),x),x)),Negate(Simp(Dist(Times(C2,b,Power(Times(Plus(m,C1),CSqrtPi),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x),x),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(6958,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x)))),x),x),x)),Simp(Dist(Times(C2,b,Power(Times(Plus(m,C1),CSqrtPi),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x),x),x)),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(6959,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x)))),x),x),x)),Negate(Simp(Dist(Times(C2,b,Power(Times(Plus(m,C1),CSqrtPi),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),d),Sqr(x))))),x),x),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(6960,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Times(e,x),m),Power(Erf(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x)))
  );
}
