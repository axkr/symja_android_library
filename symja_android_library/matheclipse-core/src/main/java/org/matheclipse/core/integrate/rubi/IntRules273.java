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
class IntRules273 { 
  public static IAST RULES = List( 
IIntegrate(5461,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),q),x),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(q),IGtQ(p,C0)))),
IIntegrate(5462,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),q),x),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(q),IGtQ(p,C0)))),
IIntegrate(5463,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Times(Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN2)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(d,Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN2)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),GtQ(m,C1)))),
IIntegrate(5464,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Times(Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN2)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(d,Sqr(f),Power(e,CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN2)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),GtQ(m,C1)))),
IIntegrate(5465,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(e,Power(Times(d,Sqr(f)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C2)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),LtQ(m,CN1)))),
IIntegrate(5466,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Power(d,CN1),Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(e,Power(Times(d,Sqr(f)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C2)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),GtQ(p,C0),LtQ(m,CN1)))),
IIntegrate(5467,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(CNI,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,e,Plus(p,C1)),CN1)),x),Simp(Dist(Power(Times(c,d),CN1),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Subtract(CI,Times(c,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0)))),
IIntegrate(5468,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(CI,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,e,Plus(p,C1)),CN1)),x),Simp(Dist(Power(Times(c,d),CN1),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Subtract(CI,Times(c,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0)))),
IIntegrate(5469,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Simp(Dist(Power(Times(b,c,d,Plus(p,C1)),CN1),Integrate(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),Not(IGtQ(p,C0)),NeQ(p,CN1)))),
IIntegrate(5470,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Simp(Dist(Power(Times(b,c,d,Plus(p,C1)),CN1),Integrate(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),Not(IGtQ(p,C0)),NeQ(p,CN1)))),
IIntegrate(5471,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CNI,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,d,Plus(p,C1)),CN1)),x),Simp(Dist(Times(CI,Power(d,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Times(x,Plus(CI,Times(c,x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C0)))),
IIntegrate(5472,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CI,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,d,Plus(p,C1)),CN1)),x),Simp(Dist(Times(CI,Power(d,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Times(x,Plus(CI,Times(c,x))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C0)))),
IIntegrate(5473,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,c,d,Plus(p,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(e,Times(Sqr(c),d)),LtQ(p,CN1)))),
IIntegrate(5474,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Times(f,x),m),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Simp(Dist(Times(f,m,Power(Times(b,c,d,Plus(p,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,CN1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(e,Times(Sqr(c),d)),LtQ(p,CN1)))),
IIntegrate(5475,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Plus(a,Times(b,ArcTan(Times(c,x)))),Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(m),Not(And(EqQ(m,C1),NeQ(a,C0)))))),
IIntegrate(5476,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Plus(a,Times(b,ArcCot(Times(c,x)))),Times(Power(x,m),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(m),Not(And(EqQ(m,C1),NeQ(a,C0)))))),
IIntegrate(5477,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Times(C2,e,Plus(q,C1)),CN1)),x),Simp(Dist(Times(b,p,Power(Times(C2,c,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,q),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C0),NeQ(q,CN1)))),
IIntegrate(5478,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Times(C2,e,Plus(q,C1)),CN1)),x),Simp(Dist(Times(b,p,Power(Times(C2,c,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,q),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C0),NeQ(q,CN1)))),
IIntegrate(5479,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1),Plus(d,Times(e,Sqr(x)))),CN1)),x),Negate(Simp(Times(Subtract(C1,Times(Sqr(c),Sqr(x))),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C2)),Power(Times(Sqr(b),e,Plus(p,C1),Plus(p,C2),Plus(d,Times(e,Sqr(x)))),CN1)),x)),Negate(Simp(Dist(Times(C4,Power(Times(Sqr(b),Plus(p,C1),Plus(p,C2)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C2)),Power(Plus(d,Times(e,Sqr(x))),CN2)),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(p,CN1),NeQ(p,CN2)))),
IIntegrate(5480,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1),Plus(d,Times(e,Sqr(x)))),CN1)),x),Negate(Simp(Times(Subtract(C1,Times(Sqr(c),Sqr(x))),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C2)),Power(Times(Sqr(b),e,Plus(p,C1),Plus(p,C2),Plus(d,Times(e,Sqr(x)))),CN1)),x)),Negate(Simp(Dist(Times(C4,Power(Times(Sqr(b),Plus(p,C1),Plus(p,C2)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C2)),Power(Plus(d,Times(e,Sqr(x))),CN2)),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(p,CN1),NeQ(p,CN2))))
  );
}
