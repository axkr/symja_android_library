package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules258 { 
  public static IAST RULES = List( 
IIntegrate(6451,Int(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(FresnelC(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6452,Int(Times(FresnelS(Times(b_DEFAULT,x_)),x_,Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Cos(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x)),Dist(Power(Times(C2,b,Pi),CN1),Int(Sin(Times(C2,d,Sqr(x))),x),x)),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6453,Int(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_)),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x),Dist(Times(b,Power(Times(C4,d),CN1)),Int(Sin(Times(C2,d,Sqr(x))),x),x)),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6454,Int(Times(FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,Subtract(m,C1)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x)),Dist(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Int(Times(Power(x,Subtract(m,C2)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),Dist(Power(Times(C2,b,Pi),CN1),Int(Times(Power(x,Subtract(m,C1)),Sin(Times(C2,d,Sqr(x)))),x),x)),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(6455,Int(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x),Negate(Dist(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Int(Times(Power(x,Subtract(m,C2)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x)),Negate(Dist(Times(b,Power(Times(C4,d),CN1)),Int(Times(Power(x,Subtract(m,C1)),Sin(Times(C2,d,Sqr(x)))),x),x))),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(6456,Int(Times(FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Plus(m,C1),CN1)),x),Negate(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C2)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x)),Dist(Times(d,Power(Times(Pi,b,Plus(m,C1)),CN1)),Int(Times(Power(x,Plus(m,C1)),Cos(Times(C2,d,Sqr(x)))),x),x),Negate(Simp(Times(d,Power(x,Plus(m,C2)),Power(Times(Pi,b,Plus(m,C1),Plus(m,C2)),CN1)),x))),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN2)))),
IIntegrate(6457,Int(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Plus(m,C1),CN1)),x),Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C2)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),Negate(Dist(Times(b,Power(Times(C2,Plus(m,C1)),CN1)),Int(Times(Power(x,Plus(m,C1)),Cos(Times(C2,d,Sqr(x)))),x),x)),Negate(Simp(Times(b,Power(x,Plus(m,C2)),Power(Times(C2,Plus(m,C1),Plus(m,C2)),CN1)),x))),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN2)))),
IIntegrate(6458,Int(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(FresnelS(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6459,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6460,Int(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Dist(Power(Times(Pi,b),CN1),Int(Sqr(Sin(Times(d,Sqr(x)))),x),x)),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6461,Int(Times(FresnelC(Times(b_DEFAULT,x_)),x_,Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x)),Dist(Times(b,Power(Times(C2,d),CN1)),Int(Sqr(Cos(Times(d,Sqr(x)))),x),x)),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6462,Int(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Negate(Dist(Power(Times(Pi,b),CN1),Int(Times(Power(x,Subtract(m,C1)),Sqr(Sin(Times(d,Sqr(x))))),x),x)),Negate(Dist(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Int(Times(Power(x,Subtract(m,C2)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x))),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(6463,Int(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,Subtract(m,C1)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x)),Dist(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Int(Times(Power(x,Subtract(m,C2)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),Dist(Times(b,Power(Times(C2,d),CN1)),Int(Times(Power(x,Subtract(m,C1)),Sqr(Cos(Times(d,Sqr(x))))),x),x)),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(6464,Int(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Plus(m,C1),CN1)),x),Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C2)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),Negate(Dist(Times(d,Power(Times(Pi,b,Plus(m,C1)),CN1)),Int(Times(Power(x,Plus(m,C1)),Sin(Times(C2,d,Sqr(x)))),x),x))),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN1)))),
IIntegrate(6465,Int(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Plus(m,C1),CN1)),x),Negate(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C2)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x)),Negate(Dist(Times(b,Power(Times(C2,Plus(m,C1)),CN1)),Int(Times(Power(x,Plus(m,C1)),Sin(Times(C2,d,Sqr(x)))),x),x))),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN1)))),
IIntegrate(6466,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6467,Int(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(FresnelC(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6468,Int(FresnelS(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,FresnelS(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(b,d,n),Int(Sin(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6469,Int(FresnelC(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,FresnelC(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(b,d,n),Int(Cos(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6470,Int(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(List(FresnelS,FresnelC),FSymbol)))),
IIntegrate(6471,Int(Times(FresnelS(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),FresnelS(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Int(Times(Power(Times(e,x),m),Sin(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))))),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6472,Int(Times(FresnelC(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),FresnelC(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Int(Times(Power(Times(e,x),m),Cos(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))))),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6473,Int(ExpIntegralE(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Negate(Simp(Times(ExpIntegralE(Plus(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(List(a,b,n),x))),
IIntegrate(6474,Int(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,m),ExpIntegralE(Plus(n,C1),Times(b,x)),Power(b,CN1)),x)),Dist(Times(m,Power(b,CN1)),Int(Times(Power(x,Subtract(m,C1)),ExpIntegralE(Plus(n,C1),Times(b,x))),x),x)),And(FreeQ(b,x),EqQ(Plus(m,n),C0),IGtQ(m,C0)))),
IIntegrate(6475,Int(Times(ExpIntegralE(C1,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(b,x,HypergeometricPFQ(List(C1,C1,C1),List(C2,C2,C2),Times(CN1,b,x))),x),Negate(Simp(Times(EulerGamma,Log(x)),x)),Negate(Simp(Times(C1D2,C1,Sqr(Log(Times(b,x)))),x))),FreeQ(b,x)))
  );
}
