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
class IntRules267 { 
  public static IAST RULES = List( 
IIntegrate(5341,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSin(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(5342,Integrate(Times(Plus(a_DEFAULT,Times(ArcCos(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCos(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(5343,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Star(Plus(a,Times(b,ArcSin(u))),w),x),Simp(Star(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x)),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(5344,Integrate(Times(Plus(a_DEFAULT,Times(ArcCos(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Plus(Simp(Star(Plus(a,Times(b,ArcCos(u))),w),x),Simp(Star(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x)),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(5345,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),p)),x),Simp(Star(Times(b,c,n,p),Integrate(Times(Power(x,n),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0),Or(EqQ(n,C1),EqQ(p,C1))))),
IIntegrate(5346,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),p)),x),Simp(Star(Times(b,c,n,p),Integrate(Times(Power(x,n),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0),Or(EqQ(n,C1),EqQ(p,C1))))),
IIntegrate(5347,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,c,Power(x,n)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,c,Power(x,n)))))),p),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0)))),
IIntegrate(5348,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),p),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0)))),
IIntegrate(5349,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,ArcCot(Power(Times(Power(x,n),c),CN1)))),p),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(5350,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,ArcTan(Power(Times(Power(x,n),c),CN1)))),p),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(5351,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(5352,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(5353,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),p),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(5354,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),p),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(5355,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(a,Log(x)),x),Simp(Star(Times(CI,C1D2,b),Integrate(Times(Log(Subtract(C1,Times(CI,c,x))),Power(x,CN1)),x)),x),Negate(Simp(Star(Times(CI,C1D2,b),Integrate(Times(Log(Plus(C1,Times(CI,c,x))),Power(x,CN1)),x)),x))),FreeQ(list(a,b,c),x))),
IIntegrate(5356,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(a,Log(x)),x),Negate(Simp(Star(Times(CI,C1D2,b),Integrate(Times(Log(Plus(C1,Times(CI,Power(Times(c,x),CN1)))),Power(x,CN1)),x)),x)),Simp(Star(Times(CI,C1D2,b),Integrate(Times(Log(Subtract(C1,Times(CI,Power(Times(c,x),CN1)))),Power(x,CN1)),x)),x)),FreeQ(list(a,b,c),x))),
IIntegrate(5357,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(C2,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),ArcTanh(Subtract(C1,Times(C2,Power(Plus(C1,Times(CI,c,x)),CN1))))),x),Simp(Star(Times(C2,b,c,p),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Subtract(p,C1)),ArcTanh(Subtract(C1,Times(C2,Power(Plus(C1,Times(CI,c,x)),CN1)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1)))),
IIntegrate(5358,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(C2,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),ArcCoth(Subtract(C1,Times(C2,Power(Plus(C1,Times(CI,c,x)),CN1))))),x),Simp(Star(Times(C2,b,c,p),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Subtract(p,C1)),ArcCoth(Subtract(C1,Times(C2,Power(Plus(C1,Times(CI,c,x)),CN1)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1)))),
IIntegrate(5359,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(x,CN1)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0)))),
IIntegrate(5360,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(x,CN1)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0))))
  );
}
