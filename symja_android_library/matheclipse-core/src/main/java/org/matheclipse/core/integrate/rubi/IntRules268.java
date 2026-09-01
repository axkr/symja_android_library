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
class IntRules268 { 
  public static IAST RULES = List( 
IIntegrate(5361,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,ArcCot(Power(Times(Power(x,n),c),CN1)))),p),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(5362,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,ArcTan(Power(Times(Power(x,n),c),CN1)))),p),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),ILtQ(n,C0)))),
IIntegrate(5363,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(k,CN1)),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(5364,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(k,CN1)),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),FractionQ(n)))),
IIntegrate(5365,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),p),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(5366,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),p),x),FreeQ(List(a,b,c,n,p),x))),
IIntegrate(5367,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(a,Log(x)),x),Simp(Dist(Times(CI,C1D2,b),Integrate(Times(Log(Subtract(C1,Times(CI,c,x))),Power(x,CN1)),x),x),x),Negate(Simp(Dist(Times(CI,C1D2,b),Integrate(Times(Log(Plus(C1,Times(CI,c,x))),Power(x,CN1)),x),x),x))),FreeQ(list(a,b,c),x))),
IIntegrate(5368,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(a,Log(x)),x),Negate(Simp(Dist(Times(CI,C1D2,b),Integrate(Times(Log(Plus(C1,Times(CI,Power(Times(c,x),CN1)))),Power(x,CN1)),x),x),x)),Simp(Dist(Times(CI,C1D2,b),Integrate(Times(Log(Subtract(C1,Times(CI,Power(Times(c,x),CN1)))),Power(x,CN1)),x),x),x)),FreeQ(list(a,b,c),x))),
IIntegrate(5369,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(C2,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),ArcTanh(Subtract(C1,Times(C2,Power(Plus(C1,Times(CI,c,x)),CN1))))),x),Simp(Dist(Times(C2,b,c,p),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN1)),ArcTanh(Subtract(C1,Times(C2,Power(Plus(C1,Times(CI,c,x)),CN1)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1)))),
IIntegrate(5370,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(C2,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),ArcCoth(Subtract(C1,Times(C2,Power(Plus(C1,Times(CI,c,x)),CN1))))),x),Simp(Dist(Times(C2,b,c,p),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN1)),ArcCoth(Subtract(C1,Times(C2,Power(Plus(C1,Times(CI,c,x)),CN1)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1)))),
IIntegrate(5371,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(x,CN1)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0)))),
IIntegrate(5372,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(x,CN1)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0)))),
IIntegrate(5373,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),p),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,c,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),Plus(p,CN1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),And(EqQ(n,C1),IntegerQ(m))),NeQ(m,CN1)))),
IIntegrate(5374,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),p),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,c,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),Plus(p,CN1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C0),Or(EqQ(p,C1),And(EqQ(n,C1),IntegerQ(m))),NeQ(m,CN1)))),
IIntegrate(5375,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C1),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(5376,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,m,n),x),IGtQ(p,C1),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(5377,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(x,m),Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,c,Power(x,n)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,c,Power(x,n)))))),p)),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(5378,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(x,m),Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),p)),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),IntegerQ(m)))),
IIntegrate(5379,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),FractionQ(m)))),
IIntegrate(5380,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,Times(k,n)))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0),FractionQ(m))))
  );
}
