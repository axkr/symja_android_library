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
IIntegrate(6461,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(C2,Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),ArcCoth(Subtract(C1,Times(C2,Power(Subtract(C1,Times(c,x)),CN1))))),x),Simp(Dist(Times(C2,b,c,p),Integrate(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Plus(p,CN1)),ArcCoth(Subtract(C1,Times(C2,Power(Subtract(C1,Times(c,x)),CN1)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1)))),
IIntegrate(6462,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(x,CN1)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0)))),
IIntegrate(6463,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(x,CN1)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0)))),
IIntegrate(6464,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,c,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),Plus(p,CN1)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),And(EqQ(n,C1),IntegerQ(m))),NeQ(m,CN1)))),
IIntegrate(6465,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),p),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,c,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),Plus(p,CN1)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),And(EqQ(n,C1),IntegerQ(m))),NeQ(m,CN1)))),
IIntegrate(6466,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C1),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(6467,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C1),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(6468,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(x,m),Power(Subtract(Plus(a,Times(b,C1D2,Log(Plus(C1,Times(c,Power(x,n)))))),Times(b,C1D2,Log(Subtract(C1,Times(c,Power(x,n)))))),p)),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(6469,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(x,m),Power(Subtract(Plus(a,Times(b,C1D2,Log(Plus(C1,Power(Times(Power(x,n),c),CN1))))),Times(b,C1D2,Log(Subtract(C1,Power(Times(Power(x,n),c),CN1))))),p)),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(6470,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),FractionQ(m)))),
IIntegrate(6471,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),FractionQ(m)))),
IIntegrate(6472,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,ArcCoth(Power(Times(Power(x,n),c),CN1)))),p)),x),And(FreeQ(List(a,b,c,m),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(6473,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,ArcTanh(Power(Times(Power(x,n),c),CN1)))),p)),x),And(FreeQ(List(a,b,c,m),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(6474,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,m),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(6475,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,m),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(6476,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(d_,x_),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(Power(d,n),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,n)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(n),NeQ(m,CN1)))),
IIntegrate(6477,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(d_,x_),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(Power(d,n),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,n)),Power(Subtract(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(n),NeQ(m,CN1)))),
IIntegrate(6478,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(d,IntPart(m)),Power(Times(d,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),RationalQ(m,n))))),
IIntegrate(6479,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(d,IntPart(m)),Power(Times(d,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),RationalQ(m,n))))),
IIntegrate(6480,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x)))
  );
}
