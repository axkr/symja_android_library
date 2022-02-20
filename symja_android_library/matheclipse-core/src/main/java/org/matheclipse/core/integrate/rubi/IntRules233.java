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
class IntRules233 { 
  public static IAST RULES = List( 
IIntegrate(4661,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(d,p),Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Cos(x),Plus(Times(C2,p),C1))),x),x,ArcSin(Times(c,x))),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(Times(C2,p),C0),Or(IntegerQ(p),GtQ(d,C0))))),
IIntegrate(4662,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Times(Power(d,p),Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Sin(x),Plus(Times(C2,p),C1))),x),x,ArcCos(Times(c,x))),x)),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(Times(C2,p),C0),Or(IntegerQ(p),GtQ(d,C0))))),
IIntegrate(4663,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(d,Subtract(p,C1D2)),Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),Integrate(Times(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(Times(C2,p),C0),Not(Or(IntegerQ(p),GtQ(d,C0)))))),
IIntegrate(4664,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(d,Subtract(p,C1D2)),Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),Integrate(Times(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(Times(C2,p),C0),Not(Or(IntegerQ(p),GtQ(d,C0)))))),
IIntegrate(4665,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Subtract(Dist(Plus(a,Times(b,ArcSin(Times(c,x)))),u,x),Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Plus(Times(Sqr(c),d),e),C0),Or(IGtQ(p,C0),ILtQ(Plus(p,C1D2),C0))))),
IIntegrate(4666,Integrate(Times(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),p),x))),Plus(Dist(Plus(a,Times(b,ArcCos(Times(c,x)))),u,x),Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Plus(Times(Sqr(c),d),e),C0),Or(IGtQ(p,C0),ILtQ(Plus(p,C1D2),C0))))),
IIntegrate(4667,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Plus(d,Times(e,Sqr(x))),p),x),x),And(FreeQ(List(a,b,c,d,e,n),x),NeQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(p),Or(GtQ(p,C0),IGtQ(n,C0))))),
IIntegrate(4668,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Plus(d,Times(e,Sqr(x))),p),x),x),And(FreeQ(List(a,b,c,d,e,n),x),NeQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(p),Or(GtQ(p,C0),IGtQ(n,C0))))),
IIntegrate(4669,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,n,p),x))),
IIntegrate(4670,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,Sqr(x))),p),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),FreeQ(List(a,b,c,d,e,n,p),x))),
IIntegrate(4671,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Dist(Power(Times(CN1,Sqr(d),g,Power(e,CN1)),q),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0),GtQ(d,C0),LtQ(Times(g,Power(e,CN1)),C0)))),
IIntegrate(4672,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Dist(Power(Times(CN1,Sqr(d),g,Power(e,CN1)),q),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0),GtQ(d,C0),LtQ(Times(g,Power(e,CN1)),C0)))),
IIntegrate(4673,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(d,Times(e,x)),q),Power(Plus(f,Times(g,x)),q),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0)))),
IIntegrate(4674,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),p_),Power(Plus(f_,Times(g_DEFAULT,x_)),q_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(d,Times(e,x)),q),Power(Plus(f,Times(g,x)),q),Power(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Subtract(p,q)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n),x),EqQ(Plus(Times(e,f),Times(d,g)),C0),EqQ(Subtract(Times(Sqr(c),Sqr(d)),Sqr(e)),C0),HalfIntegerQ(p,q),GeQ(Subtract(p,q),C0)))),
IIntegrate(4675,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Negate(Dist(Power(e,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Tan(x)),x),x,ArcSin(Times(c,x))),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0)))),
IIntegrate(4676,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Dist(Power(e,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Cot(x)),x),x,ArcCos(Times(c,x))),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0)))),
IIntegrate(4677,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Times(C2,e,Plus(p,C1)),CN1)),x),Dist(Times(b,n,Power(d,IntPart(p)),Power(Plus(d,Times(e,Sqr(x))),FracPart(p)),Power(Times(C2,c,Plus(p,C1),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),FracPart(p))),CN1)),Integrate(Times(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),Plus(p,C1D2)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(n,C0),NeQ(p,CN1)))),
IIntegrate(4678,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(p,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Times(C2,e,Plus(p,C1)),CN1)),x),Dist(Times(b,n,Power(d,IntPart(p)),Power(Plus(d,Times(e,Sqr(x))),FracPart(p)),Power(Times(C2,c,Plus(p,C1),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),FracPart(p))),CN1)),Integrate(Times(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),Plus(p,C1D2)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(n,C0),NeQ(p,CN1)))),
IIntegrate(4679,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Times(Cos(x),Sin(x)),CN1)),x),x,ArcSin(Times(c,x))),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0)))),
IIntegrate(4680,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Negate(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Times(Cos(x),Sin(x)),CN1)),x),x,ArcCos(Times(c,x))),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(n,C0))))
  );
}
