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
class IntRules350 { 
  public static IAST RULES = List( 
IIntegrate(7001,Integrate(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(7002,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Dist(CC(1L,4L,1L,4L),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,CSqrtPi,CC(1L,1L,1L,1L),b,x))),x),x),x),Simp(Dist(CC(1L,4L,-1L,4L),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,CSqrtPi,CC(1L,1L,-1L,1L),b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,CN1,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7003,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Dist(CC(1L,4L,-1L,4L),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,CSqrtPi,CC(1L,1L,1L,1L),b,x))),x),x),x),Simp(Dist(CC(1L,4L,1L,4L),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,CSqrtPi,CC(1L,1L,-1L,1L),b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,CN1,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7004,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7005,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7006,Integrate(Times(Power(FresnelS(Times(b_DEFAULT,x_)),n_DEFAULT),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Simp(Dist(Times(Pi,b,Power(Times(C2,d),CN1)),Subst(Integrate(Power(x,n),x),x,FresnelS(Times(b,x))),x),x),And(FreeQ(list(b,d,n),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7007,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),Power(FresnelC(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Pi,b,Power(Times(C2,d),CN1)),Subst(Integrate(Power(x,n),x),x,FresnelC(Times(b,x))),x),x),And(FreeQ(list(b,d,n),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7008,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),Sin(Plus(c_,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Dist(Sin(c),Integrate(Times(Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),x),Simp(Dist(Cos(c),Integrate(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7009,Integrate(Times(Cos(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Dist(Cos(c),Integrate(Times(Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),x),Simp(Dist(Sin(c),Integrate(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7010,Integrate(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(FresnelS(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7011,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7012,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(FresnelC(Times(b,x)),FresnelS(Times(b,x)),Power(Times(C2,b),CN1)),x),Negate(Simp(Times(QQ(1L,8L),CI,b,Sqr(x),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(CN1D2,CI,Sqr(b),Pi,Sqr(x)))),x)),Simp(Times(QQ(1L,8L),CI,b,Sqr(x),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(C1D2,CI,Sqr(b),Pi,Sqr(x)))),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7013,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(b,Pi,FresnelC(Times(b,x)),FresnelS(Times(b,x)),Power(Times(C4,d),CN1)),x),Simp(Times(QQ(1L,8L),CI,b,Sqr(x),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(CNI,d,Sqr(x)))),x),Negate(Simp(Times(QQ(1L,8L),CI,b,Sqr(x),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(CI,d,Sqr(x)))),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7014,Integrate(Times(Cos(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Dist(Cos(c),Integrate(Times(Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),x),Simp(Dist(Sin(c),Integrate(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7015,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Sin(Plus(c_,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Dist(Sin(c),Integrate(Times(Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),x),Simp(Dist(Cos(c),Integrate(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7016,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7017,Integrate(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(FresnelC(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7018,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),x_,Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Cos(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Dist(Power(Times(C2,b,Pi),CN1),Integrate(Sin(Times(C2,d,Sqr(x))),x),x),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7019,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_)),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Dist(Times(b,Power(Times(C4,d),CN1)),Integrate(Sin(Times(C2,d,Sqr(x))),x),x),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7020,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,CN1)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Dist(Times(Plus(m,CN1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Plus(m,CN2)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),x),Simp(Dist(Power(Times(C2,b,Pi),CN1),Integrate(Times(Power(x,Plus(m,CN1)),Sin(Times(C2,d,Sqr(x)))),x),x),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1))))
  );
}
