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
class IntRules322 { 
  public static IAST RULES = List( 
IIntegrate(6441,Integrate(Power(f_,Times(Power(ArcCosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(b,CN1),Subst(Integrate(Times(Power(f,Times(c,Power(x,n))),Sinh(x)),x),x,ArcCosh(Plus(a,Times(b,x)))),x),x),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0)))),
IIntegrate(6442,Integrate(Times(Power(f_,Times(Power(ArcCosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(b,CN1),Subst(Integrate(Times(Power(Plus(Times(CN1,a,Power(b,CN1)),Times(Cosh(x),Power(b,CN1))),m),Power(f,Times(c,Power(x,n))),Sinh(x)),x),x,ArcCosh(Plus(a,Times(b,x)))),x),x),And(FreeQ(List(a,b,c,f),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(6443,Integrate(ArcCosh(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCosh(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u))),CN1)),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6444,Integrate(Times(Plus(a_DEFAULT,Times(ArcCosh(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCosh(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u))),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6445,Integrate(Times(Plus(a_DEFAULT,Times(ArcCosh(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Dist(Plus(a,Times(b,ArcCosh(u))),w,x),x),Simp(Dist(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u))),CN1)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(6446,Integrate(Exp(Times(ArcCosh(u_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(u,Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u)))),n),x),And(IntegerQ(n),PolyQ(u,x)))),
IIntegrate(6447,Integrate(Times(Exp(Times(ArcCosh(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(u,Times(Sqrt(Plus(CN1,u)),Sqrt(Plus(C1,u)))),n)),x),And(RationalQ(m),IntegerQ(n),PolyQ(u,x)))),
IIntegrate(6448,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p)),x),Simp(Dist(Times(b,c,n,p),Integrate(Times(Power(x,n),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),Plus(p,CN1)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0),Or(EqQ(n,C1),EqQ(p,C1))))),
IIntegrate(6449,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),p)),x),Simp(Dist(Times(b,c,n,p),Integrate(Times(Power(x,n),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),Plus(p,CN1)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0),Or(EqQ(n,C1),EqQ(p,C1))))),
IIntegrate(6450,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Subtract(Plus(a,Times(b,C1D2,Log(Plus(C1,Times(c,Power(x,n)))))),Times(b,C1D2,Log(Subtract(C1,Times(c,Power(x,n)))))),p),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0)))),
IIntegrate(6451,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Subtract(Plus(a,Times(b,C1D2,Log(Plus(C1,Power(Times(Power(x,n),c),CN1))))),Times(b,C1D2,Log(Subtract(C1,Power(Times(Power(x,n),c),CN1))))),p),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0)))),
IIntegrate(6452,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,ArcCoth(Power(Times(Power(x,n),c),CN1)))),p),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(6453,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,ArcTanh(Power(Times(Power(x,n),c),CN1)))),p),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(6454,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(k,CN1)),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(6455,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(k,CN1)),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(6456,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(6457,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),p),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(6458,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(a,Log(x)),x),Negate(Simp(Times(C1D2,b,PolyLog(C2,Times(CN1,c,x))),x)),Simp(Times(C1D2,b,PolyLog(C2,Times(c,x))),x)),FreeQ(list(a,b,c),x))),
IIntegrate(6459,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(a,Log(x)),x),Simp(Times(C1D2,b,PolyLog(C2,Negate(Power(Times(c,x),CN1)))),x),Negate(Simp(Times(C1D2,b,PolyLog(C2,Power(Times(c,x),CN1))),x))),FreeQ(list(a,b,c),x))),
IIntegrate(6460,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(C2,Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),ArcTanh(Subtract(C1,Times(C2,Power(Subtract(C1,Times(c,x)),CN1))))),x),Simp(Dist(Times(C2,b,c,p),Integrate(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Plus(p,CN1)),ArcTanh(Subtract(C1,Times(C2,Power(Subtract(C1,Times(c,x)),CN1)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1))))
  );
}
