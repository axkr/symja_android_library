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
class IntRules272 { 
  public static IAST RULES = List( 
IIntegrate(5441,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Times(c,d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Simp(Times(x,Plus(a,Times(b,ArcTan(Times(c,x)))),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d))))),
IIntegrate(5442,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Power(Times(c,d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Simp(Times(x,Plus(a,Times(b,ArcCot(Times(c,x)))),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d))))),
IIntegrate(5443,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Simp(Times(b,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Times(C4,c,d,Sqr(Plus(q,C1))),CN1)),x),Negate(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,ArcTan(Times(c,x)))),Power(Times(C2,d,Plus(q,C1)),CN1)),x)),Simp(Dist(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,ArcTan(Times(c,x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),NeQ(q,QQ(-3L,2L))))),
IIntegrate(5444,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Times(C4,c,d,Sqr(Plus(q,C1))),CN1)),x),Negate(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,ArcCot(Times(c,x)))),Power(Times(C2,d,Plus(q,C1)),CN1)),x)),Simp(Dist(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Plus(a,Times(b,ArcCot(Times(c,x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),NeQ(q,QQ(-3L,2L))))),
IIntegrate(5445,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Simp(Times(b,p,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN1)),Power(Times(c,d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Simp(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Negate(Simp(Dist(Times(Sqr(b),p,Plus(p,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN2)),Power(Plus(d,Times(e,Sqr(x))),QQ(-3L,2L))),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C1)))),
IIntegrate(5446,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,p,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN1)),Power(Times(c,d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Simp(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Times(d,Sqrt(Plus(d,Times(e,Sqr(x))))),CN1)),x),Negate(Simp(Dist(Times(Sqr(b),p,Plus(p,CN1)),Integrate(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN2)),Power(Plus(d,Times(e,Sqr(x))),QQ(-3L,2L))),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),GtQ(p,C1)))),
IIntegrate(5447,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Simp(Times(b,p,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN1)),Power(Times(C4,c,d,Sqr(Plus(q,C1))),CN1)),x),Negate(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(Times(C2,d,Plus(q,C1)),CN1)),x)),Simp(Dist(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),x),Negate(Simp(Dist(Times(Sqr(b),p,Plus(p,CN1),Power(Times(C4,Sqr(Plus(q,C1))),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,CN2))),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),GtQ(p,C1),NeQ(q,QQ(-3L,2L))))),
IIntegrate(5448,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,p,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN1)),Power(Times(C4,c,d,Sqr(Plus(q,C1))),CN1)),x),Negate(Simp(Times(x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(Times(C2,d,Plus(q,C1)),CN1)),x)),Simp(Dist(Times(Plus(Times(C2,q),C3),Power(Times(C2,d,Plus(q,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),x),x),Negate(Simp(Dist(Times(Sqr(b),p,Plus(p,CN1),Power(Times(C4,Sqr(Plus(q,C1))),CN1)),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,CN2))),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),GtQ(p,C1),NeQ(q,QQ(-3L,2L))))),
IIntegrate(5449,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Simp(Dist(Times(C2,c,Plus(q,C1),Power(Times(b,Plus(p,C1)),CN1)),Integrate(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Plus(p,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),LtQ(p,CN1)))),
IIntegrate(5450,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1)),Power(Times(b,c,d,Plus(p,C1)),CN1)),x),Simp(Dist(Times(C2,c,Plus(q,C1),Power(Times(b,Plus(p,C1)),CN1)),Integrate(Times(x,Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Plus(p,C1))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(e,Times(Sqr(c),d)),LtQ(q,CN1),LtQ(p,CN1)))),
IIntegrate(5451,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(d,q),Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Cos(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcTan(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),ILtQ(Times(C2,Plus(q,C1)),C0),Or(IntegerQ(q),GtQ(d,C0))))),
IIntegrate(5452,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(d,Plus(q,C1D2)),Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Integrate(Times(Power(Plus(C1,Times(Sqr(c),Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),ILtQ(Times(C2,Plus(q,C1)),C0),Not(Or(IntegerQ(q),GtQ(d,C0)))))),
IIntegrate(5453,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(d,q),Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Sin(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcCot(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),ILtQ(Times(C2,Plus(q,C1)),C0),IntegerQ(q)))),
IIntegrate(5454,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(d,Plus(q,C1D2)),x,Sqrt(Times(Plus(C1,Times(Sqr(c),Sqr(x))),Power(Times(Sqr(c),Sqr(x)),CN1))),Power(Plus(d,Times(e,Sqr(x))),CN1D2)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Power(Sin(x),Times(C2,Plus(q,C1))),CN1)),x),x,ArcCot(Times(c,x))),x),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(e,Times(Sqr(c),d)),ILtQ(Times(C2,Plus(q,C1)),C0),Not(IntegerQ(q))))),
IIntegrate(5455,Integrate(Times(ArcTan(Times(c_DEFAULT,x_)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Subtract(C1,Times(CI,c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Plus(C1,Times(CI,c,x))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),FreeQ(list(c,d,e),x))),
IIntegrate(5456,Integrate(Times(ArcCot(Times(c_DEFAULT,x_)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Subtract(C1,Times(CI,Power(Times(c,x),CN1)))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Plus(C1,Times(CI,Power(Times(c,x),CN1)))),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),FreeQ(list(c,d,e),x))),
IIntegrate(5457,Integrate(Times(Plus(Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(a,Integrate(Power(Plus(d,Times(e,Sqr(x))),CN1),x),x),x),Simp(Dist(b,Integrate(Times(ArcTan(Times(c,x)),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(5458,Integrate(Times(Plus(Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT),a_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(a,Integrate(Power(Plus(d,Times(e,Sqr(x))),CN1),x),x),x),Simp(Dist(b,Integrate(Times(ArcCot(Times(c,x)),Power(Plus(d,Times(e,Sqr(x))),CN1)),x),x),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(5459,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),q),x))),Subtract(Simp(Dist(Plus(a,Times(b,ArcTan(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IntegerQ(q),ILtQ(Plus(q,C1D2),C0))))),
IIntegrate(5460,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),q),x))),Plus(Simp(Dist(Plus(a,Times(b,ArcCot(Times(c,x)))),u,x),x),Simp(Dist(Times(b,c),Integrate(SimplifyIntegrand(Times(u,Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),x),x))),And(FreeQ(List(a,b,c,d,e),x),Or(IntegerQ(q),ILtQ(Plus(q,C1D2),C0)))))
  );
}
