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
class IntRules323 { 
  public static IAST RULES = List( 
IIntegrate(6461,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),x_,Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x)),Dist(Times(b,Power(Times(C2,d),CN1)),Integrate(Sqr(Cos(Times(d,Sqr(x)))),x),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6462,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Negate(Dist(Power(Times(Pi,b),CN1),Integrate(Times(Power(x,Subtract(m,C1)),Sqr(Sin(Times(d,Sqr(x))))),x),x)),Negate(Dist(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C2)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(6463,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,Subtract(m,C1)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x)),Dist(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C2)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),Dist(Times(b,Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Sqr(Cos(Times(d,Sqr(x))))),x),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(6464,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Plus(m,C1),CN1)),x),Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),Negate(Dist(Times(d,Power(Times(Pi,b,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Sin(Times(C2,d,Sqr(x)))),x),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN1)))),
IIntegrate(6465,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Plus(m,C1),CN1)),x),Negate(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x)),Negate(Dist(Times(b,Power(Times(C2,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Sin(Times(C2,d,Sqr(x)))),x),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN1)))),
IIntegrate(6466,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6467,Integrate(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(FresnelC(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6468,Integrate(FresnelS(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,FresnelS(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(b,d,n),Integrate(Sin(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6469,Integrate(FresnelC(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,FresnelC(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(b,d,n),Integrate(Cos(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6470,Integrate(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(list(FresnelS,FresnelC),FSymbol)))),
IIntegrate(6471,Integrate(Times(FresnelS(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),FresnelS(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Sin(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))))),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6472,Integrate(Times(FresnelC(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),FresnelC(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Cos(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))))),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6473,Integrate(ExpIntegralE(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Negate(Simp(Times(ExpIntegralE(Plus(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6474,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,m),ExpIntegralE(Plus(n,C1),Times(b,x)),Power(b,CN1)),x)),Dist(Times(m,Power(b,CN1)),Integrate(Times(Power(x,Subtract(m,C1)),ExpIntegralE(Plus(n,C1),Times(b,x))),x),x)),And(FreeQ(b,x),EqQ(Plus(m,n),C0),IGtQ(m,C0)))),
IIntegrate(6475,Integrate(Times(ExpIntegralE(C1,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x),Negate(Simp(Times(EulerGamma,Log(x)),x)),Negate(Simp(Times(C1D2,C1,Sqr(Log(Times(b,x)))),x))),FreeQ(b,x))),
IIntegrate(6476,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),ExpIntegralE(n,Times(b,x)),Power(Plus(m,C1),CN1)),x),Dist(Times(b,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),ExpIntegralE(Subtract(n,C1),Times(b,x))),x),x)),And(FreeQ(b,x),EqQ(Plus(m,n),C0),ILtQ(m,CN1)))),
IIntegrate(6477,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),m),Gamma(Plus(m,C1)),Log(x),Power(Times(b,Power(Times(b,x),m)),CN1)),x),Simp(Times(Power(Times(d,x),Plus(m,C1)),HypergeometricPFQ(list(Plus(m,C1),Plus(m,C1)),list(Plus(m,C2),Plus(m,C2)),Times(CN1,b,x)),Power(Times(d,Sqr(Plus(m,C1))),CN1)),x)),And(FreeQ(List(b,d,m,n),x),EqQ(Plus(m,n),C0),Not(IntegerQ(m))))),
IIntegrate(6478,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),ExpIntegralE(n,Times(b,x)),Power(Times(d,Plus(m,n)),CN1)),x),Simp(Times(Power(Times(d,x),Plus(m,C1)),ExpIntegralE(Negate(m),Times(b,x)),Power(Times(d,Plus(m,n)),CN1)),x)),And(FreeQ(List(b,d,m,n),x),NeQ(Plus(m,n),C0)))),
IIntegrate(6479,Integrate(Times(ExpIntegralE(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Plus(c,Times(d,x)),m),ExpIntegralE(Plus(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x)),Dist(Times(d,m,Power(b,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),ExpIntegralE(Plus(n,C1),Plus(a,Times(b,x)))),x),x)),And(FreeQ(List(a,b,c,d,m,n),x),Or(IGtQ(m,C0),ILtQ(n,C0),And(GtQ(m,C0),LtQ(n,CN1)))))),
IIntegrate(6480,Integrate(Times(ExpIntegralE(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),ExpIntegralE(n,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),ExpIntegralE(Subtract(n,C1),Plus(a,Times(b,x)))),x),x)),And(FreeQ(List(a,b,c,d,m,n),x),Or(IGtQ(n,C0),And(LtQ(m,CN1),GtQ(n,C0))),NeQ(m,CN1))))
  );
}
