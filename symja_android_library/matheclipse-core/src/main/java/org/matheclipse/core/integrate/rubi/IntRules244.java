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
class IntRules244 { 
  public static IAST RULES = List( 
IIntegrate(4881,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(b,p,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Subtract(p,C1)),Power(Times(C2,c,q,Plus(Times(C2,q),C1)),CN1)),x),Dist(Times(C2,d,q,Power(Plus(Times(C2,q),C1),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x),Dist(Times(Sqr(b),d,p,Subtract(p,C1),Power(Times(C2,q,Plus(Times(C2,q),C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Subtract(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Subtract(p,C2))),x),x),Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(Times(C2,q),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(q,C0),GtQ(p,C1)))),
IIntegrate(4882,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Log(RemoveContent(Plus(a,Times(b,ArcTan(Times(c,x)))),x)),Power(Times(b,c,d),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d))))),
IIntegrate(4883,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),CN1),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Negate(Simp(Times(Log(RemoveContent(Plus(a,Times(b,ArcCot(Times(c,x)))),x)),Power(Times(b,c,d),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d))))),
IIntegrate(4884,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),NeQ(p,CN1)))),
IIntegrate(4885,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Negate(Simp(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x)),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),NeQ(p,CN1)))),
IIntegrate(4886,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(CN2,CI,Plus(a,Times(b,ArcTan(Times(c,x)))),ArcTan(Times(Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x),Simp(Times(CI,b,PolyLog(C2,Times(CN1,CI,Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x),Negate(Simp(Times(CI,b,PolyLog(C2,Times(CI,Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(d,C0)))),
IIntegrate(4887,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(CN2,CI,Plus(a,Times(b,ArcCot(Times(c,x)))),ArcTan(Times(Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x),Negate(Simp(Times(CI,b,PolyLog(C2,Times(CN1,CI,Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x)),Simp(Times(CI,b,PolyLog(C2,Times(CI,Sqrt(Plus(C1,Times(CI,c,x))),Power(Subtract(C1,Times(CI,c,x)),CN1D2))),Power(Times(c,Sqrt(d)),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(d,C0)))),
IIntegrate(4888,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Power(Times(c,Sqrt(d)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Sec(x)),x),x,ArcTan(Times(c,x))),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0),GtQ(d,C0)))),
IIntegrate(4889,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Negate(Dist(Times(x,Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Csc(x)),x),x,ArcCot(Times(c,x))),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0),GtQ(d,C0)))),
IIntegrate(4890,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0),Not(GtQ(d,C0))))),
IIntegrate(4891,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),IGtQ(p,C0),Not(GtQ(d,C0))))),
IIntegrate(4892,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Times(C2,d,Plus(d,Times(e,Sqr(x)))),CN1)),x),Negate(Dist(Times(C1D2,b,c,p),Integrate(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Subtract(p,C1)),Power(Plus(d,Times(e,Sqr(x))),CN2)),x),x)),Simp(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(C2,b,c,Sqr(d),Plus(p,C1)),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C0)))),
IIntegrate(4893,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Times(C2,d,Plus(d,Times(e,Sqr(x)))),CN1)),x),Dist(Times(C1D2,b,c,p),Integrate(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Subtract(p,C1)),Power(Plus(d,Times(e,Sqr(x))),CN2)),x),x),Negate(Simp(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(C2,b,c,Sqr(d),Plus(p,C1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C0)))),
IIntegrate(4894,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(c,d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Simp(Times(x,Plus(a,Times(b,ArcTan(Times(c,x)))),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d))))),
IIntegrate(4895,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Power(Times(c,d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),Simp(Times(x,Plus(a,Times(b,ArcCot(Times(c,x)))),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d))))),
IIntegrate(4896,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Times(C4,c,d,Sqr(Plus(q,C1))),CN1)),x),Dist(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,ArcTan(Times(c,x))))),x),x),Negate(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,ArcTan(Times(c,x)))),Power(Times(C2,d,Plus(q,C1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),NeQ(q,QQ(-3L,2L))))),
IIntegrate(4897,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Times(C4,c,d,Sqr(Plus(q,C1))),CN1)),x)),Dist(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,ArcCot(Times(c,x))))),x),x),Negate(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,ArcCot(Times(c,x)))),Power(Times(C2,d,Plus(q,C1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),NeQ(q,QQ(-3L,2L))))),
IIntegrate(4898,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Simp(Times(b,p,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Subtract(p,C1)),Power(Times(c,d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Negate(Dist(Times(Sqr(b),p,Subtract(p,C1)),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Subtract(p,C2)),Power(Plus(d,Times(e,Sqr(x))),QQ(-3L,2L))),x),x)),Simp(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C1)))),
IIntegrate(4899,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,p,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Subtract(p,C1)),Power(Times(c,d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),Negate(Dist(Times(Sqr(b),p,Subtract(p,C1)),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Subtract(p,C2)),Power(Plus(d,Times(e,Sqr(x))),QQ(-3L,2L))),x),x)),Simp(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C1)))),
IIntegrate(4900,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Simp(Times(b,p,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Subtract(p,C1)),Power(Times(C4,c,d,Sqr(Plus(q,C1))),CN1)),x),Dist(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),Negate(Dist(Times(Sqr(b),p,Subtract(p,C1),Power(Times(C4,Sqr(Plus(q,C1))),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Subtract(p,C2))),x),x)),Negate(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Times(C2,d,Plus(q,C1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),GtQ(p,C1),NeQ(q,QQ(-3L,2L)))))
  );
}
