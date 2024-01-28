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
class IntRules268 { 
  public static IAST RULES = List( 
IIntegrate(5361,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),p),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,c,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),And(EqQ(n,C1),IntegerQ(m))),NeQ(m,CN1)))),
IIntegrate(5362,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),p),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,c,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),And(EqQ(n,C1),IntegerQ(m))),NeQ(m,CN1)))),
IIntegrate(5363,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C1),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(5364,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C1),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(5365,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(x,m),Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,c,Power(x,n)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,c,Power(x,n)))))),p)),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(5366,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(x,m),Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),p)),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(5367,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),FractionQ(m)))),
IIntegrate(5368,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),FractionQ(m)))),
IIntegrate(5369,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,ArcCot(Power(Times(Power(x,n),c),CN1)))),p)),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(5370,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(a,Times(b,ArcTan(Power(Times(Power(x,n),c),CN1)))),p)),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(5371,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(5372,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(5373,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(d_,x_),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,c,n,Power(Times(Power(d,n),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,n)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(n),NeQ(m,CN1)))),
IIntegrate(5374,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(d_,x_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,c,n,Power(Times(Power(d,n),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,n)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(n),NeQ(m,CN1)))),
IIntegrate(5375,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(Power(d,IntPart(m)),Power(Times(d,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),RationalQ(m,n))))),
IIntegrate(5376,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(Power(d,IntPart(m)),Power(Times(d,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),RationalQ(m,n))))),
IIntegrate(5377,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(5378,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(5379,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Log(Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1))),Power(e,CN1)),x),Simp(Star(Times(b,c,p,Power(e,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Subtract(p,C1)),Log(Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)),C0)))),
IIntegrate(5380,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Log(Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1))),Power(e,CN1)),x),Simp(Star(Times(b,c,p,Power(e,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Subtract(p,C1)),Log(Times(C2,Power(Plus(C1,Times(e,x,Power(d,CN1))),CN1))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)),C0))))
  );
}
