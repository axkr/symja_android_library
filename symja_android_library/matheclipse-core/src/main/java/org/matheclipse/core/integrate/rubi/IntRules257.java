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
public class IntRules257 { 
  public static IAST RULES = List( 
IIntegrate(6426,Int(Times(FresnelS(Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),FresnelS(Times(b,x)),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Int(Times(Power(Times(d,x),Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x)))),x),x)),And(FreeQ(List(b,d,m),x),NeQ(m,CN1)))),
IIntegrate(6427,Int(Times(FresnelC(Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),FresnelC(Times(b,x)),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Int(Times(Power(Times(d,x),Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x)))),x),x)),And(FreeQ(List(b,d,m),x),NeQ(m,CN1)))),
IIntegrate(6428,Int(Times(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),FresnelS(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Int(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x)))))),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6429,Int(Times(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),FresnelC(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Int(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x)))))),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6430,Int(Times(Sqr(FresnelS(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(FresnelS(Times(b,x))),Power(Plus(m,C1),CN1)),x),Dist(Times(C2,b,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x))),x),x)),And(FreeQ(b,x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(6431,Int(Times(Sqr(FresnelC(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(FresnelC(Times(b,x))),Power(Plus(m,C1),CN1)),x),Dist(Times(C2,b,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x))),x),x)),And(FreeQ(b,x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(6432,Int(Times(Sqr(FresnelS(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Int(ExpandIntegrand(Sqr(FresnelS(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6433,Int(Times(Sqr(FresnelC(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Int(ExpandIntegrand(Sqr(FresnelC(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6434,Int(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6435,Int(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6436,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(Times(C1D4,Plus(C1,CI)),Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,Sqrt(Pi),Plus(C1,CI),b,x))),x),x),Dist(Times(C1D4,Subtract(C1,CI)),Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,Sqrt(Pi),Subtract(C1,CI),b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(CN1,C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6437,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Dist(Times(C1D4,Subtract(C1,CI)),Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,Sqrt(Pi),Plus(C1,CI),b,x))),x),x),Dist(Times(C1D4,Plus(C1,CI)),Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(C1D2,Sqrt(Pi),Subtract(C1,CI),b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(CN1,C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6438,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6439,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6440,Int(Times(Power(FresnelS(Times(b_DEFAULT,x_)),n_DEFAULT),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Dist(Times(Pi,b,Power(Times(C2,d),CN1)),Subst(Int(Power(x,n),x),x,FresnelS(Times(b,x))),x),And(FreeQ(List(b,d,n),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6441,Int(Times(Cos(Times(d_DEFAULT,Sqr(x_))),Power(FresnelC(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Pi,b,Power(Times(C2,d),CN1)),Subst(Int(Power(x,n),x),x,FresnelC(Times(b,x))),x),And(FreeQ(List(b,d,n),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6442,Int(Times(FresnelS(Times(b_DEFAULT,x_)),Sin(Plus(c_,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Dist(Sin(c),Int(Times(Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),Dist(Cos(c),Int(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6443,Int(Times(Cos(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Dist(Cos(c),Int(Times(Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),Dist(Sin(c),Int(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6444,Int(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(FresnelS(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6445,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6446,Int(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(FresnelC(Times(b,x)),FresnelS(Times(b,x)),Power(Times(C2,b),CN1)),x),Negate(Simp(Times(QQ(1L,8L),C1,CI,b,Sqr(x),HypergeometricPFQ(List(C1,C1),List(QQ(3L,2L),C2),Times(CN1,C1D2,CI,Sqr(b),Pi,Sqr(x)))),x)),Simp(Times(QQ(1L,8L),C1,CI,b,Sqr(x),HypergeometricPFQ(List(C1,C1),List(QQ(3L,2L),C2),Times(C1D2,C1,CI,Sqr(b),Pi,Sqr(x)))),x)),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6447,Int(Times(FresnelC(Times(b_DEFAULT,x_)),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(b,Pi,FresnelC(Times(b,x)),FresnelS(Times(b,x)),Power(Times(C4,d),CN1)),x),Simp(Times(QQ(1L,8L),C1,CI,b,Sqr(x),HypergeometricPFQ(List(C1,C1),List(QQ(3L,2L),C2),Times(CN1,CI,d,Sqr(x)))),x),Negate(Simp(Times(QQ(1L,8L),C1,CI,b,Sqr(x),HypergeometricPFQ(List(C1,C1),List(QQ(3L,2L),C2),Times(CI,d,Sqr(x)))),x))),And(FreeQ(List(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6448,Int(Times(Cos(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Dist(Cos(c),Int(Times(Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),Dist(Sin(c),Int(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6449,Int(Times(FresnelC(Times(b_DEFAULT,x_)),Sin(Plus(c_,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Dist(Sin(c),Int(Times(Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),Dist(Cos(c),Int(Times(Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x)),And(FreeQ(List(b,c,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(6450,Int(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x)))
  );
}
