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
class IntRules272 { 
  public static IAST RULES = List( 
IIntegrate(5441,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(d,q),Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Sin(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcCot(Times(c,x)))),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),ILtQ(Times(C2,Plus(q,C1)),C0),IntegerQ(q)))),
IIntegrate(5442,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(d,Plus(q,C1D2)),x,Sqrt(Times(Plus(C1,Times(Sqr(c),Sqr(x))),Power(Times(Sqr(c),Sqr(x)),CN1))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Sin(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcCot(Times(c,x)))),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),ILtQ(Times(C2,Plus(q,C1)),C0),Not(IntegerQ(q))))),
IIntegrate(5443,Integrate(Times(ArcTan(Times(c_DEFAULT,x_)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Subtract(C1,Times(CI,c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Plus(C1,Times(CI,c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x)),x)),FreeQ(list(c,d,e),x))),
IIntegrate(5444,Integrate(Times(ArcCot(Times(c_DEFAULT,x_)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Subtract(C1,Times(CI,Power(Times(c,x),CN1)))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Plus(C1,Times(CI,Power(Times(c,x),CN1)))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x)),x)),FreeQ(list(c,d,e),x))),
IIntegrate(5445,Integrate(Times(Plus(Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Star(a,Integrate(Power(Plus(d,Times(e,Sqr(x))),CN1),x)),x),Simp(Star(b,Integrate(Times(ArcTan(Times(c,x)),Power(Plus(d,Times(e,Sqr(x))),CN1)),x)),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(5446,Integrate(Times(Plus(Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Star(a,Integrate(Power(Plus(d,Times(e,Sqr(x))),CN1),x)),x),Simp(Star(b,Integrate(Times(ArcCot(Times(c,x)),Power(Plus(d,Times(e,Sqr(x))),CN1)),x)),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(5447,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),q),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcTan(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IntegerQ(q),ILtQ(Plus(q,C1D2),C0))))),
IIntegrate(5448,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),q),x))),Plus(Simp(Star(Plus(a,Times(b,ArcCot(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IntegerQ(q),ILtQ(Plus(q,C1D2),C0))))),
IIntegrate(5449,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),q),x),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(q),IGtQ(p,C0)))),
IIntegrate(5450,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),q),x),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(q),IGtQ(p,C0)))),
IIntegrate(5451,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C2)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x)),x),Simp(Star(Times(d,Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C2)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),GtQ(m,C1)))),
IIntegrate(5452,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C2)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x)),x),Simp(Star(Times(d,Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Subtract(m,C2)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),GtQ(m,C1)))),
IIntegrate(5453,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x)),x),Simp(Star(Times(e,Power(Times(d,Sqr(f)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C2)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),LtQ(m,CN1)))),
IIntegrate(5454,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x)),x),Simp(Star(Times(e,Power(Times(d,Sqr(f)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C2)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),LtQ(m,CN1)))),
IIntegrate(5455,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,CI,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,e,Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(c,d),CN1),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Subtract(CI,Times(c,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0)))),
IIntegrate(5456,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(CI,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,e,Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(c,d),CN1),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Subtract(CI,Times(c,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0)))),
IIntegrate(5457,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(b,c,d,Plus(p,C1)),CN1),Integrate(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),Not(IGtQ(p,C0)),NeQ(p,CN1)))),
IIntegrate(5458,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(b,c,d,Plus(p,C1)),CN1),Integrate(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),Not(IGtQ(p,C0)),NeQ(p,CN1)))),
IIntegrate(5459,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,CI,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,d,Plus(p,C1)),CN1)),x),Simp(Star(Times(CI,Power(d,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Times(x,Plus(CI,Times(c,x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C0)))),
IIntegrate(5460,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CI,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,d,Plus(p,C1)),CN1)),x),Simp(Star(Times(CI,Power(d,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Times(x,Plus(CI,Times(c,x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C0))))
  );
}
