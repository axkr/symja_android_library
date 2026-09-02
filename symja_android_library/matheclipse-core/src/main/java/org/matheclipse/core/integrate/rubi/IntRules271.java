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
class IntRules271 { 
  public static IAST RULES = List( 
IIntegrate(5421,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),q_)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,x)),q)),x))),Subtract(Simp(Dist(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),u,x),x),Simp(Dist(Times(b,c,p),Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN1)),Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,q),x),IGtQ(p,C1),EqQ(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),IntegersQ(m,q),NeQ(m,CN1),NeQ(q,CN1),ILtQ(Plus(m,q,C1),C0),LtQ(Times(m,q),C0)))),
IIntegrate(5422,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),q_)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,x)),q)),x))),Plus(Simp(Dist(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),u,x),x),Simp(Dist(Times(b,c,p),Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN1)),Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,q),x),IGtQ(p,C1),EqQ(Plus(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),IntegersQ(m,q),NeQ(m,CN1),NeQ(q,CN1),ILtQ(Plus(m,q,C1),C0),LtQ(Times(m,q),C0)))),
IIntegrate(5423,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Times(Power(Times(f,x),m),Power(Plus(d,Times(e,x)),q)),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0),IntegerQ(q),Or(GtQ(q,C0),NeQ(a,C0),IntegerQ(m))))),
IIntegrate(5424,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Times(Power(Times(f,x),m),Power(Plus(d,Times(e,x)),q)),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0),IntegerQ(q),Or(GtQ(q,C0),NeQ(a,C0),IntegerQ(m))))),
IIntegrate(5425,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Power(Plus(d,Times(e,Sqr(x))),q),Power(Times(C2,c,q,Plus(Times(C2,q),C1)),CN1)),x),Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Plus(a,Times(b,ArcTan(Times(c,x)))),Power(Plus(Times(C2,q),C1),CN1)),x),Simp(Dist(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,CN1)),Plus(a,Times(b,ArcTan(Times(c,x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(q,C0)))),
IIntegrate(5426,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(d,Times(e,Sqr(x))),q),Power(Times(C2,c,q,Plus(Times(C2,q),C1)),CN1)),x),Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Plus(a,Times(b,ArcCot(Times(c,x)))),Power(Plus(Times(C2,q),C1),CN1)),x),Simp(Dist(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,CN1)),Plus(a,Times(b,ArcCot(Times(c,x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(q,C0)))),
IIntegrate(5427,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,p,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN1)),Power(Times(C2,c,q,Plus(Times(C2,q),C1)),CN1)),x),Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(Times(C2,q),C1),CN1)),x),Simp(Dist(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,CN1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(Sqr(b),d,p,Plus(p,CN1),Power(Times(C2,q,Plus(Times(C2,q),C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,CN1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN2))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(q,C0),GtQ(p,C1)))),
IIntegrate(5428,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(b,p,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN1)),Power(Times(C2,c,q,Plus(Times(C2,q),C1)),CN1)),x),Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(Times(C2,q),C1),CN1)),x),Simp(Dist(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,CN1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x),x),Simp(Dist(Times(Sqr(b),d,p,Plus(p,CN1),Power(Times(C2,q,Plus(Times(C2,q),C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,CN1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN2))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(q,C0),GtQ(p,C1)))),
IIntegrate(5429,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Log(RemoveContent(Plus(a,Times(b,ArcTan(Times(c,x)))),x)),Power(Times(b,c,d),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d))))),
IIntegrate(5430,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(CN1,Log(RemoveContent(Plus(a,Times(b,ArcCot(Times(c,x)))),x)),Power(Times(b,c,d),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d))))),
IIntegrate(5431,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),NeQ(p,CN1)))),
IIntegrate(5432,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(CN1,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),NeQ(p,CN1)))),
IIntegrate(5433,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(CN2,CI,Plus(a,Times(b,ArcTan(Times(c,x)))),ArcTan(Times(Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x),Simp(Times(CI,b,PolyLog(C2,Times(CNI,Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x),Negate(Simp(Times(CI,b,PolyLog(C2,Times(CI,Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(d,C0)))),
IIntegrate(5434,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(CN2,CI,Plus(a,Times(b,ArcCot(Times(c,x)))),ArcTan(Times(Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x),Negate(Simp(Times(CI,b,PolyLog(C2,Times(CNI,Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x)),Simp(Times(CI,b,PolyLog(C2,Times(CI,Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(d,C0)))),
IIntegrate(5435,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Dist(Power(Times(c,Sqrt(d)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Sec(x)),x),x,ArcTan(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0),GtQ(d,C0)))),
IIntegrate(5436,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,x,Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Csc(x)),x),x,ArcCot(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0),GtQ(d,C0)))),
IIntegrate(5437,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0),Not(GtQ(d,C0))))),
IIntegrate(5438,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0),Not(GtQ(d,C0))))),
IIntegrate(5439,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Times(C2,d,Plus(d,Times(e,Sqr(x)))),CN1)),x),Simp(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(C2,b,c,Sqr(d),Plus(p,C1)),CN1)),x),Negate(Simp(Dist(Times(b,c,C1D2,p),Integrate(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN1)),Power(Plus(d,Times(e,Sqr(x))),CN2)),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C0)))),
IIntegrate(5440,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Times(C2,d,Plus(d,Times(e,Sqr(x)))),CN1)),x),Negate(Simp(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(C2,b,c,Sqr(d),Plus(p,C1)),CN1)),x)),Simp(Dist(Times(b,c,C1D2,p),Integrate(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN1)),Power(Plus(d,Times(e,Sqr(x))),CN2)),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C0))))
  );
}
