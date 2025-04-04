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
class IntRules297 { 
  public static IAST RULES = List( 
IIntegrate(5941,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Times(Power(Times(f,x),m),Power(Plus(d,Times(e,x)),q)),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0),IntegerQ(q),Or(GtQ(q,C0),NeQ(a,C0),IntegerQ(m))))),
IIntegrate(5942,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(d,Times(e,Sqr(x))),q),Power(Times(C2,c,q,Plus(Times(C2,q),C1)),CN1)),x),Dist(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(q,C1)),Plus(a,Times(b,ArcTanh(Times(c,x))))),x),x),Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Plus(Times(C2,q),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(q,C0)))),
IIntegrate(5943,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(d,Times(e,Sqr(x))),q),Power(Times(C2,c,q,Plus(Times(C2,q),C1)),CN1)),x),Dist(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(q,C1)),Plus(a,Times(b,ArcCoth(Times(c,x))))),x),x),Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(Plus(Times(C2,q),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(q,C0)))),
IIntegrate(5944,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(b,p,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Subtract(p,C1)),Power(Times(C2,c,q,Plus(Times(C2,q),C1)),CN1)),x),Dist(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(q,C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p)),x),x),Negate(Dist(Times(Sqr(b),d,p,Subtract(p,C1),Power(Times(C2,q,Plus(Times(C2,q),C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(q,C1)),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Subtract(p,C2))),x),x)),Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(Plus(Times(C2,q),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(q,C0),GtQ(p,C1)))),
IIntegrate(5945,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(b,p,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Subtract(p,C1)),Power(Times(C2,c,q,Plus(Times(C2,q),C1)),CN1)),x),Dist(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(q,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p)),x),x),Negate(Dist(Times(Sqr(b),d,p,Subtract(p,C1),Power(Times(C2,q,Plus(Times(C2,q),C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(q,C1)),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Subtract(p,C2))),x),x)),Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Plus(Times(C2,q),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(q,C0),GtQ(p,C1)))),
IIntegrate(5946,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Log(RemoveContent(Plus(a,Times(b,ArcTanh(Times(c,x)))),x)),Power(Times(b,c,d),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0)))),
IIntegrate(5947,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Log(RemoveContent(Plus(a,Times(b,ArcCoth(Times(c,x)))),x)),Power(Times(b,c,d),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0)))),
IIntegrate(5948,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),NeQ(p,CN1)))),
IIntegrate(5949,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Plus(Times(Sqr(c),d),e),C0),NeQ(p,CN1)))),
IIntegrate(5950,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(CN2,Plus(a,Times(b,ArcTanh(Times(c,x)))),ArcTan(Times(Sqrt(Subtract(C1,Times(c,x))),Power(Plus(C1,Times(c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x),Negate(Simp(Times(CI,b,PolyLog(C2,Times(CN1,CI,Sqrt(Subtract(C1,Times(c,x))),Power(Plus(C1,Times(c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x)),Simp(Times(CI,b,PolyLog(C2,Times(CI,Sqrt(Subtract(C1,Times(c,x))),Power(Plus(C1,Times(c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(d,C0)))),
IIntegrate(5951,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(CN2,Plus(a,Times(b,ArcCoth(Times(c,x)))),ArcTan(Times(Sqrt(Subtract(C1,Times(c,x))),Power(Plus(C1,Times(c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x),Negate(Simp(Times(CI,b,PolyLog(C2,Times(CN1,CI,Sqrt(Subtract(C1,Times(c,x))),Power(Plus(C1,Times(c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x)),Simp(Times(CI,b,PolyLog(C2,Times(CI,Sqrt(Subtract(C1,Times(c,x))),Power(Plus(C1,Times(c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(d,C0)))),
IIntegrate(5952,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Power(Times(c,Sqrt(d)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Sech(x)),x),x,ArcTanh(Times(c,x))),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0),GtQ(d,C0)))),
IIntegrate(5953,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Negate(Dist(Times(x,Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Csch(x)),x),x,ArcCoth(Times(c,x))),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0),GtQ(d,C0)))),
IIntegrate(5954,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0),Not(GtQ(d,C0))))),
IIntegrate(5955,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),IGtQ(p,C0),Not(GtQ(d,C0))))),
IIntegrate(5956,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p),Power(Times(C2,d,Plus(d,Times(e,Sqr(x)))),CN1)),x),Negate(Dist(Times(C1D2,b,c,p),Integrate(Times(x,Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Subtract(p,C1)),Power(Plus(d,Times(e,Sqr(x))),CN2)),x),x)),Simp(Times(Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),Plus(p,C1)),Power(Times(C2,b,c,Sqr(d),Plus(p,C1)),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(p,C0)))),
IIntegrate(5957,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p),Power(Times(C2,d,Plus(d,Times(e,Sqr(x)))),CN1)),x),Negate(Dist(Times(C1D2,b,c,p),Integrate(Times(x,Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Subtract(p,C1)),Power(Plus(d,Times(e,Sqr(x))),CN2)),x),x)),Simp(Times(Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),Plus(p,C1)),Power(Times(C2,b,c,Sqr(d),Plus(p,C1)),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),GtQ(p,C0)))),
IIntegrate(5958,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Power(Times(c,d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),Simp(Times(x,Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0)))),
IIntegrate(5959,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Power(Times(c,d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),Simp(Times(x,Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0)))),
IIntegrate(5960,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Times(C4,c,d,Sqr(Plus(q,C1))),CN1)),x)),Dist(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,ArcTanh(Times(c,x))))),x),x),Negate(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Times(C2,d,Plus(q,C1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(Sqr(c),d),e),C0),LtQ(q,CN1),NeQ(q,QQ(-3L,2L)))))
  );
}
