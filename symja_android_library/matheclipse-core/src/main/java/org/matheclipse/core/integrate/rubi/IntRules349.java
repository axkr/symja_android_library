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
class IntRules349 { 
  public static IAST RULES = List( 
IIntegrate(6981,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),FresnelC(Times(b,x)),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x)))),x)),x)),And(FreeQ(list(b,d,m),x),NeQ(m,CN1)))),
IIntegrate(6982,Integrate(Times(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),FresnelS(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x)))))),x)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6983,Integrate(Times(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),FresnelC(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x)))))),x)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6984,Integrate(Times(Sqr(FresnelS(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(FresnelS(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(C2,b,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x))),x)),x)),And(FreeQ(b,x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(6985,Integrate(Times(Sqr(FresnelC(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(FresnelC(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(C2,b,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x))),x)),x)),And(FreeQ(b,x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(6986,Integrate(Times(Sqr(FresnelS(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Sqr(FresnelS(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6987,Integrate(Times(Sqr(FresnelC(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Sqr(FresnelC(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6988,Integrate(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6989,Integrate(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6990,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Star(Times(C1D4,Plus(C1,CI)),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,CSqrtPi,Plus(C1,CI),b,x))),x)),x),Simp(Star(Times(C1D4,Subtract(C1,CI)),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,CSqrtPi,Subtract(C1,CI),b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,CN1,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6991,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Star(Times(C1D4,Subtract(C1,CI)),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,CSqrtPi,Plus(C1,CI),b,x))),x)),x),Simp(Star(Times(C1D4,Plus(C1,CI)),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,CSqrtPi,Subtract(C1,CI),b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,CN1,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6992,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6993,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6994,Integrate(Times(Power(FresnelS(Times(b_DEFAULT,x_)),n_DEFAULT),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Simp(Star(Times(Pi,b,Power(Times(C2,d),CN1)),Subst(Integrate(Power(x,n),x),x,FresnelS(Times(b,x)))),x),And(FreeQ(list(b,d,n),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6995,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),Power(FresnelC(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Pi,b,Power(Times(C2,d),CN1)),Subst(Integrate(Power(x,n),x),x,FresnelC(Times(b,x)))),x),And(FreeQ(list(b,d,n),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6996,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),Sin(Plus(c_,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Star(Sin(c),Integrate(Times(Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x)),x),Simp(Star(Cos(c),Integrate(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6997,Integrate(Times(Cos(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Star(Cos(c),Integrate(Times(Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x)),x),Simp(Star(Sin(c),Integrate(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6998,Integrate(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(FresnelS(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6999,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7000,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(FresnelC(Times(b,x)),FresnelS(Times(b,x)),Power(Times(C2,b),CN1)),x),Negate(Simp(Times(QQ(1L,8L),CI,b,Sqr(x),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(CN1,C1D2,CI,Sqr(b),Pi,Sqr(x)))),x)),Simp(Times(QQ(1L,8L),CI,b,Sqr(x),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(C1D2,CI,Sqr(b),Pi,Sqr(x)))),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))))))
  );
}
