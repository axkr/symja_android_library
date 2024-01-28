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
class IntRules350 { 
  public static IAST RULES = List( 
IIntegrate(7001,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(b,Pi,FresnelC(Times(b,x)),FresnelS(Times(b,x)),Power(Times(C4,d),CN1)),x),Simp(Times(QQ(1L,8L),CI,b,Sqr(x),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(CN1,CI,d,Sqr(x)))),x),Negate(Simp(Times(QQ(1L,8L),CI,b,Sqr(x),HypergeometricPFQ(list(C1,C1),list(QQ(3L,2L),C2),Times(CI,d,Sqr(x)))),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7002,Integrate(Times(Cos(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Star(Cos(c),Integrate(Times(Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x)),x),Simp(Star(Sin(c),Integrate(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7003,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Sin(Plus(c_,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Star(Sin(c),Integrate(Times(Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x)),x),Simp(Star(Cos(c),Integrate(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7004,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7005,Integrate(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(FresnelC(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7006,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),x_,Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Cos(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Star(Power(Times(C2,b,Pi),CN1),Integrate(Sin(Times(C2,d,Sqr(x))),x)),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7007,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_)),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Star(Times(b,Power(Times(C4,d),CN1)),Integrate(Sin(Times(C2,d,Sqr(x))),x)),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7008,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Subtract(m,C1)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Star(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C2)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x)),x),Simp(Star(Power(Times(C2,b,Pi),CN1),Integrate(Times(Power(x,Subtract(m,C1)),Sin(Times(C2,d,Sqr(x)))),x)),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(7009,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x),Negate(Simp(Star(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C2)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x)),x)),Negate(Simp(Star(Times(b,Power(Times(C4,d),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Sin(Times(C2,d,Sqr(x)))),x)),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(7010,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Plus(m,C1),CN1)),x),Negate(Simp(Times(d,Power(x,Plus(m,C2)),Power(Times(Pi,b,Plus(m,C1),Plus(m,C2)),CN1)),x)),Negate(Simp(Star(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x)),x)),Simp(Star(Times(d,Power(Times(Pi,b,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Cos(Times(C2,d,Sqr(x)))),x)),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN2)))),
IIntegrate(7011,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Plus(m,C1),CN1)),x),Negate(Simp(Times(b,Power(x,Plus(m,C2)),Power(Times(C2,Plus(m,C1),Plus(m,C2)),CN1)),x)),Simp(Star(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x)),x),Negate(Simp(Star(Times(b,Power(Times(C2,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Cos(Times(C2,d,Sqr(x)))),x)),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN2)))),
IIntegrate(7012,Integrate(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(FresnelS(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(7013,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(7014,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Star(Power(Times(Pi,b),CN1),Integrate(Sqr(Sin(Times(d,Sqr(x)))),x)),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7015,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),x_,Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Star(Times(b,Power(Times(C2,d),CN1)),Integrate(Sqr(Cos(Times(d,Sqr(x)))),x)),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7016,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Negate(Simp(Star(Power(Times(Pi,b),CN1),Integrate(Times(Power(x,Subtract(m,C1)),Sqr(Sin(Times(d,Sqr(x))))),x)),x)),Negate(Simp(Star(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C2)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x)),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(7017,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Subtract(m,C1)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Star(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C2)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x)),x),Simp(Star(Times(b,Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Sqr(Cos(Times(d,Sqr(x))))),x)),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(7018,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x)),x),Negate(Simp(Star(Times(d,Power(Times(Pi,b,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Sin(Times(C2,d,Sqr(x)))),x)),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN1)))),
IIntegrate(7019,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Plus(m,C1),CN1)),x),Negate(Simp(Star(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x)),x)),Negate(Simp(Star(Times(b,Power(Times(C2,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Sin(Times(C2,d,Sqr(x)))),x)),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN1)))),
IIntegrate(7020,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x)))
  );
}
