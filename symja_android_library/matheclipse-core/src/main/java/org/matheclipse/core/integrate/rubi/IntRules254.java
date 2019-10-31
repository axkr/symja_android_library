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
public class IntRules254 { 
  public static IAST RULES = List( 
IIntegrate(6351,Int(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Erfi(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Exp(Sqr(Plus(a,Times(b,x)))),Power(Times(b,Sqrt(Pi)),CN1)),x)),FreeQ(List(a,b),x))),
IIntegrate(6352,Int(Sqr(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(Erf(Plus(a,Times(b,x)))),Power(b,CN1)),x),Dist(Times(C4,Power(Pi,CN1D2)),Int(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x),x)),FreeQ(List(a,b),x))),
IIntegrate(6353,Int(Sqr(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Sqr(Erfc(Plus(a,Times(b,x)))),Power(b,CN1)),x),Dist(Times(C4,Power(Pi,CN1D2)),Int(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x),x)),FreeQ(List(a,b),x))),
IIntegrate(6354,Int(Sqr(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(Erfi(Plus(a,Times(b,x)))),Power(b,CN1)),x),Dist(Times(C4,Power(Pi,CN1D2)),Int(Times(Plus(a,Times(b,x)),Exp(Sqr(Plus(a,Times(b,x)))),Erfi(Plus(a,Times(b,x)))),x),x)),FreeQ(List(a,b),x))),
IIntegrate(6355,Int(Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erf(Plus(a,Times(b,x))),n),x),And(FreeQ(List(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6356,Int(Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erfc(Plus(a,Times(b,x))),n),x),And(FreeQ(List(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6357,Int(Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Unintegrable(Power(Erfi(Plus(a,Times(b,x))),n),x),And(FreeQ(List(a,b,n),x),NeQ(n,C1),NeQ(n,C2)))),
IIntegrate(6358,Int(Times(Erf(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,x,HypergeometricPFQ(List(C1D2,C1D2),List(QQ(3L,2L),QQ(3L,2L)),Times(CN1,Sqr(b),Sqr(x))),Power(Pi,CN1D2)),x),FreeQ(b,x))),
IIntegrate(6359,Int(Times(Erfc(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Log(x),x),Int(Times(Erf(Times(b,x)),Power(x,CN1)),x)),FreeQ(b,x))),
IIntegrate(6360,Int(Times(Erfi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,x,HypergeometricPFQ(List(C1D2,C1D2),List(QQ(3L,2L),QQ(3L,2L)),Times(Sqr(b),Sqr(x))),Power(Pi,CN1D2)),x),FreeQ(b,x))),
IIntegrate(6361,Int(Times(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Erf(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(C2,b,Power(Times(Sqrt(Pi),d,Plus(m,C1)),CN1)),Int(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6362,Int(Times(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Erfc(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(C2,b,Power(Times(Sqrt(Pi),d,Plus(m,C1)),CN1)),Int(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Exp(Sqr(Plus(a,Times(b,x)))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6363,Int(Times(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Erfi(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(C2,b,Power(Times(Sqrt(Pi),d,Plus(m,C1)),CN1)),Int(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Exp(Sqr(Plus(a,Times(b,x))))),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6364,Int(Times(Sqr(Erf(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(Erf(Times(b,x))),Power(Plus(m,C1),CN1)),x),Dist(Times(C4,b,Power(Times(Sqrt(Pi),Plus(m,C1)),CN1)),Int(Times(Power(x,Plus(m,C1)),Erf(Times(b,x)),Power(Exp(Times(Sqr(b),Sqr(x))),CN1)),x),x)),And(FreeQ(b,x),Or(IGtQ(m,C0),ILtQ(Times(C1D2,Plus(m,C1)),C0))))),
IIntegrate(6365,Int(Times(Sqr(Erfc(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sqr(Erfc(Times(b,x))),Power(Plus(m,C1),CN1)),x),Dist(Times(C4,b,Power(Times(Sqrt(Pi),Plus(m,C1)),CN1)),Int(Times(Power(x,Plus(m,C1)),Erfc(Times(b,x)),Power(Exp(Times(Sqr(b),Sqr(x))),CN1)),x),x)),And(FreeQ(b,x),Or(IGtQ(m,C0),ILtQ(Times(C1D2,Plus(m,C1)),C0))))),
IIntegrate(6366,Int(Times(Sqr(Erfi(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(Erfi(Times(b,x))),Power(Plus(m,C1),CN1)),x),Dist(Times(C4,b,Power(Times(Sqrt(Pi),Plus(m,C1)),CN1)),Int(Times(Power(x,Plus(m,C1)),Exp(Times(Sqr(b),Sqr(x))),Erfi(Times(b,x))),x),x)),And(FreeQ(b,x),Or(IGtQ(m,C0),ILtQ(Times(C1D2,Plus(m,C1)),C0))))),
IIntegrate(6367,Int(Times(Sqr(Erf(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Int(ExpandIntegrand(Sqr(Erf(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6368,Int(Times(Sqr(Erfc(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Int(ExpandIntegrand(Sqr(Erfc(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6369,Int(Times(Sqr(Erfi(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Int(ExpandIntegrand(Sqr(Erfi(x)),Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,x)),m),x),x),x,Plus(a,Times(b,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6370,Int(Times(Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Erf(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6371,Int(Times(Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Erfc(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6372,Int(Times(Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Erfi(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6373,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erf(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Exp(c),Sqrt(Pi),Power(Times(C2,b),CN1)),Subst(Int(Power(x,n),x),x,Erf(Times(b,x))),x),And(FreeQ(List(b,c,d,n),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6374,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfc(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Times(Exp(c),Sqrt(Pi),Power(Times(C2,b),CN1)),Subst(Int(Power(x,n),x),x,Erfc(Times(b,x))),x)),And(FreeQ(List(b,c,d,n),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6375,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfi(Times(b_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Exp(c),Sqrt(Pi),Power(Times(C2,b),CN1)),Subst(Int(Power(x,n),x),x,Erfi(Times(b,x))),x),And(FreeQ(List(b,c,d,n),x),EqQ(d,Sqr(b)))))
  );
}
