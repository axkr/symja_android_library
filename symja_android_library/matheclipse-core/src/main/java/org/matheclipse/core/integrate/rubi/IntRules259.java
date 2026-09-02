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
class IntRules259 { 
  public static IAST RULES = List( 
IIntegrate(5181,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Times(b,c),CN1),Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),p),CN1)),x)),Subst(Integrate(Times(Power(x,n),Power(Sin(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1)))),Plus(Times(C2,p),C1))),x),x,Plus(a,Times(b,ArcCos(Times(c,x))))),x),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(Times(C2,p),C0)))),
IIntegrate(5182,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcSin(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Plus(Times(Sqr(c),d),e),C0),Or(IGtQ(p,C0),ILtQ(Plus(p,C1D2),C0))))),
IIntegrate(5183,Integrate(Times(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Plus(Simp(Dist(Plus(a,Times(b,ArcCos(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Plus(Times(Sqr(c),d),e),C0),Or(IGtQ(p,C0),ILtQ(Plus(p,C1D2),C0))))),
IIntegrate(5184,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Plus(d,Times(e,Sqr(x))),p),x),x),And(FreeQ(List(a,b,c,d,e,n),x),NeQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(p),Or(GtQ(p,C0),IGtQ(n,C0))))),
IIntegrate(5185,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Plus(d,Times(e,Sqr(x))),p),x),x),And(FreeQ(List(a,b,c,d,e,n),x),NeQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(p),Or(GtQ(p,C0),IGtQ(n,C0))))),
IIntegrate(5186,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,n,p),x))),
IIntegrate(5187,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,n,p),x))),
IIntegrate(5188,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Simp(Dist(Power(Times(CN1,Sqr(d),g,Power(e,CN1)),q),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0),GtQ(d,C0),LtQ(Times(g,Power(e,CN1)),C0)))),
IIntegrate(5189,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Simp(Dist(Power(Times(CN1,Sqr(d),g,Power(e,CN1)),q),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0),GtQ(d,C0),LtQ(Times(g,Power(e,CN1)),C0)))),
IIntegrate(5190,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(d,Times(e,x)),q),Power(Plus(f,Times(g,x)),q),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0)))),
IIntegrate(5191,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(d,Times(e,x)),q),Power(Plus(f,Times(g,x)),q),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0)))),
IIntegrate(5192,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(e,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Tan(x)),x),x,ArcSin(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0)))),
IIntegrate(5193,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Dist(Power(e,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Cot(x)),x),x,ArcCos(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0)))),
IIntegrate(5194,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Times(C2,e,Plus(p,C1)),CN1)),x),Simp(Dist(Times(b,n,Power(Times(C2,c,Plus(p,C1)),CN1),Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),p),CN1)),x)),Integrate(Times(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),Plus(p,C1D2)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,CN1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(n,C0),NeQ(p,CN1)))),
IIntegrate(5195,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Times(C2,e,Plus(p,C1)),CN1)),x),Simp(Dist(Times(b,n,Power(Times(C2,c,Plus(p,C1)),CN1),Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),p),CN1)),x)),Integrate(Times(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),Plus(p,C1D2)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,CN1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(n,C0),NeQ(p,CN1)))),
IIntegrate(5196,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Times(Cos(x),Sin(x)),CN1)),x),x,ArcSin(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0)))),
IIntegrate(5197,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Times(Cos(x),Sin(x)),CN1)),x),x,ArcCos(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0)))),
IIntegrate(5198,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Times(d,f,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(f,Plus(m,C1)),CN1),Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),p),CN1)),x)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),Plus(p,C1D2)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,CN1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(n,C0),EqQ(Plus(m,Times(C2,p),C3),C0),NeQ(m,CN1)))),
IIntegrate(5199,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(f_DEFAULT,x_),m_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Times(d,f,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,c,n,Power(Times(f,Plus(m,C1)),CN1),Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),p),CN1)),x)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),Plus(p,C1D2)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,CN1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(n,C0),EqQ(Plus(m,Times(C2,p),C3),C0),NeQ(m,CN1)))),
IIntegrate(5200,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),p),Plus(a,Times(b,ArcSin(Times(c,x)))),Power(Times(C2,p),CN1)),x),Simp(Dist(d,Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,CN1)),Plus(a,Times(b,ArcSin(Times(c,x)))),Power(x,CN1)),x),x),x),Negate(Simp(Dist(Times(b,c,Power(d,p),Power(Times(C2,p),CN1)),Integrate(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),Plus(p,CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0))))
  );
}
