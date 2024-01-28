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
class IntRules322 { 
  public static IAST RULES = List( 
IIntegrate(6441,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,ArcTanh(Power(Times(Power(x,n),c),CN1)))),p),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(6442,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(6443,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(6444,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(6445,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),p),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(6446,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(a,Log(x)),x),Negate(Simp(Times(C1D2,b,PolyLog(C2,Times(CN1,c,x))),x)),Simp(Times(C1D2,b,PolyLog(C2,Times(c,x))),x)),FreeQ(list(a,b,c),x))),
IIntegrate(6447,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(a,Log(x)),x),Simp(Times(C1D2,b,PolyLog(C2,Negate(Power(Times(c,x),CN1)))),x),Negate(Simp(Times(C1D2,b,PolyLog(C2,Power(Times(c,x),CN1))),x))),FreeQ(list(a,b,c),x))),
IIntegrate(6448,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(C2,Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),ArcTanh(Subtract(C1,Times(C2,Power(Subtract(C1,Times(c,x)),CN1))))),x),Simp(Star(Times(C2,b,c,p),Integrate(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Subtract(p,C1)),ArcTanh(Subtract(C1,Times(C2,Power(Subtract(C1,Times(c,x)),CN1)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1)))),
IIntegrate(6449,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(C2,Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),ArcCoth(Subtract(C1,Times(C2,Power(Subtract(C1,Times(c,x)),CN1))))),x),Simp(Star(Times(C2,b,c,p),Integrate(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Subtract(p,C1)),ArcCoth(Subtract(C1,Times(C2,Power(Subtract(C1,Times(c,x)),CN1)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1)))),
IIntegrate(6450,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(x,CN1)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0)))),
IIntegrate(6451,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(x,CN1)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0)))),
IIntegrate(6452,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,c,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),And(EqQ(n,C1),IntegerQ(m))),NeQ(m,CN1)))),
IIntegrate(6453,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),p),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,c,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),And(EqQ(n,C1),IntegerQ(m))),NeQ(m,CN1)))),
IIntegrate(6454,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C1),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(6455,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C1),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(6456,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(x,m),Power(Subtract(Plus(a,Times(b,C1D2,Log(Plus(C1,Times(c,Power(x,n)))))),Times(b,C1D2,Log(Subtract(C1,Times(c,Power(x,n)))))),p)),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(6457,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(x,m),Power(Subtract(Plus(a,Times(b,C1D2,Log(Plus(C1,Power(Times(Power(x,n),c),CN1))))),Times(b,C1D2,Log(Subtract(C1,Power(Times(Power(x,n),c),CN1))))),p)),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(6458,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),FractionQ(m)))),
IIntegrate(6459,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),FractionQ(m)))),
IIntegrate(6460,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,ArcCoth(Power(Times(Power(x,n),c),CN1)))),p)),x),And(FreeQ(List(a,b,c,m),x),IGtQ(p,C1),ILtQ(n,C0))))
  );
}
