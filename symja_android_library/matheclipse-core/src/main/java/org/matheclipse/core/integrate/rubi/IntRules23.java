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
class IntRules23 { 
  public static IAST RULES = List( 
IIntegrate(461,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,d,Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,b,c,Plus(n,p,C1)),CN1)),x),Simp(Star(Times(Simplify(Plus(n,Times(C2,p),C2)),Power(Times(C2,c,Plus(n,p,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,c,d,n,p),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),ILtQ(Simplify(Plus(n,Times(C2,p),C2)),C0),Or(LtQ(n,CN1),GtQ(Plus(n,p),C0))))),
IIntegrate(462,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(C2,Subtract(n,C1)),d,Power(c,Subtract(n,C2)),Plus(c,Times(d,x)),Power(Times(b,Sqrt(Plus(a,Times(b,Sqr(x))))),CN1)),x),Simp(Star(Times(Sqr(d),Power(b,CN1)),Integrate(Times(Power(Plus(a,Times(b,Sqr(x))),CN1D2),ExpandToSum(Times(Subtract(Times(Power(C2,Subtract(n,C1)),Power(c,Subtract(n,C1))),Power(Plus(c,Times(d,x)),Subtract(n,C1))),Power(Subtract(c,Times(d,x)),CN1)),x)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),IGtQ(n,C2)))),
IIntegrate(463,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Power(Negate(c),Subtract(Negate(n),C2)),Power(d,Plus(Times(C2,n),C3)),Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Times(Power(C2,Plus(n,C1)),Power(b,Plus(n,C2)),Plus(c,Times(d,x))),CN1)),x),Simp(Star(Times(Power(d,Plus(Times(C2,n),C2)),Power(Power(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,Sqr(x))),CN1D2),ExpandToSum(Times(Subtract(Times(Power(C2,Subtract(Negate(n),C1)),Power(Negate(c),Subtract(Negate(n),C1))),Power(Plus(Negate(c),Times(d,x)),Subtract(Negate(n),C1))),Power(Plus(c,Times(d,x)),CN1)),x)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),ILtQ(n,C0),EqQ(Plus(n,p),QQ(-3L,2L))))),
IIntegrate(464,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(a,Times(b,Sqr(x))),Plus(n,p)),Power(Power(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(d,CN1))),n),CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),IntegerQ(n),RationalQ(p),Or(LtQ(C0,Negate(n),p),LtQ(p,Negate(n),C0)),NeQ(n,C2),NeQ(n,CN1)))),
IIntegrate(465,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Plus(a,Times(b,Sqr(x))),p),Power(Times(d,Plus(n,p,C1)),CN1)),x),Simp(Star(Times(b,p,Power(Times(Sqr(d),Plus(n,p,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(n,C2)),Power(Plus(a,Times(b,Sqr(x))),Subtract(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),GtQ(p,C0),Or(LtQ(n,CN2),EqQ(Plus(n,Times(C2,p),C1),C0)),NeQ(Plus(n,p,C1),C0),IntegerQ(Times(C2,p))))),
IIntegrate(466,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Plus(a,Times(b,Sqr(x))),p),Power(Times(d,Plus(n,Times(C2,p),C1)),CN1)),x),Simp(Star(Times(C2,b,c,p,Power(Times(Sqr(d),Plus(n,Times(C2,p),C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Plus(a,Times(b,Sqr(x))),Subtract(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),GtQ(p,C0),Or(LeQ(CN2,n,C0),EqQ(Plus(n,p,C1),C0)),NeQ(Plus(n,Times(C2,p),C1),C0),IntegerQ(Times(C2,p))))),
IIntegrate(467,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,c,Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,a,d,Plus(p,C1)),CN1)),x),Simp(Star(Times(c,Plus(n,Times(C2,p),C2),Power(Times(C2,a,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(n,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),LtQ(p,CN1),LtQ(C0,n,C1),IntegerQ(Times(C2,p))))),
IIntegrate(468,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(d,Power(Plus(c,Times(d,x)),Subtract(n,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(b,Plus(p,C1)),CN1)),x),Simp(Star(Times(Sqr(d),Plus(n,p),Power(Times(b,Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(n,C2)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),LtQ(p,CN1),GtQ(n,C1),IntegerQ(Times(C2,p))))),
IIntegrate(469,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(d,Power(Plus(c,Times(d,x)),Subtract(n,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(b,Plus(n,Times(C2,p),C1)),CN1)),x),Simp(Star(Times(C2,c,Plus(n,p),Power(Plus(n,Times(C2,p),C1),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(n,C1)),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,c,d,p),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),GtQ(n,C0),NeQ(Plus(n,Times(C2,p),C1),C0),IntegerQ(Times(C2,p))))),
IIntegrate(470,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,d,Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,b,c,Plus(n,p,C1)),CN1)),x),Simp(Star(Times(Plus(n,Times(C2,p),C2),Power(Times(C2,c,Plus(n,p,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,c,d,p),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),LtQ(n,C0),NeQ(Plus(n,p,C1),C0),IntegerQ(Times(C2,p))))),
IIntegrate(471,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(C2,d),Subst(Integrate(Power(Plus(Times(C2,b,c),Times(Sqr(d),Sqr(x))),CN1),x),x,Times(Sqrt(Plus(a,Times(b,Sqr(x)))),Power(Plus(c,Times(d,x)),CN1D2)))),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0)))),
IIntegrate(472,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,Plus(p,C1)),Power(c,Subtract(n,C1)),Power(Times(Subtract(c,Times(d,x)),Power(c,CN1)),Plus(p,C1)),Power(Power(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(d,CN1))),Plus(p,C1)),CN1)),Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),Plus(n,p)),Power(Plus(Times(a,Power(c,CN1)),Times(b,Power(d,CN1),x)),p)),x)),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),Or(IntegerQ(n),GtQ(c,C0)),GtQ(a,C0),Not(And(IntegerQ(n),Or(IntegerQ(Times(C3,p)),IntegerQ(Times(C4,p)))))))),
IIntegrate(473,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,Subtract(n,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),Plus(p,C1)),Power(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(d,CN1))),Plus(p,C1))),CN1)),Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),Plus(n,p)),Power(Plus(Times(a,Power(c,CN1)),Times(b,Power(d,CN1),x)),p)),x)),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),Or(IntegerQ(n),GtQ(c,C0)),Not(GtQ(a,C0)),Not(And(IntegerQ(n),Or(IntegerQ(Times(C3,p)),IntegerQ(Times(C4,p)))))))),
IIntegrate(474,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,IntPart(n)),Power(Plus(c,Times(d,x)),FracPart(n)),Power(Power(Plus(C1,Times(d,x,Power(c,CN1))),FracPart(n)),CN1)),Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),n),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),Not(Or(IntegerQ(n),GtQ(c,C0)))))),
IIntegrate(475,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(d,n,Power(c,Subtract(n,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(C2,b,Plus(p,C1)),CN1)),x),Integrate(ExpandIntegrand(Times(Subtract(Power(Plus(c,Times(d,x)),n),Times(d,n,Power(c,Subtract(n,C1)),x)),Power(Plus(a,Times(b,Sqr(x))),p)),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0),IGtQ(n,C0),LeQ(n,p)))),
IIntegrate(476,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0)))),
IIntegrate(477,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Power(a,p),Integrate(ExpandIntegrand(Times(Power(Plus(c,Times(d,x)),n),Power(Subtract(C1,Times(Rt(Times(CN1,b,Power(a,CN1)),C2),x)),p),Power(Plus(C1,Times(Rt(Times(CN1,b,Power(a,CN1)),C2),x)),p)),x),x)),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(n),NiceSqrtQ(Times(CN1,b,Power(a,CN1))),Not($($s("§fractionalpowerfactorq"),Rt(Times(CN1,b,Power(a,CN1)),C2)))))),
IIntegrate(478,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),CN1)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C1)))),
IIntegrate(479,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),CN1),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(d,Log(RemoveContent(Plus(c,Times(d,x)),x)),Power(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),CN1)),x),Simp(Star(Times(b,Power(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),CN1)),Integrate(Times(Subtract(c,Times(d,x)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x)),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(480,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(d,Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Plus(n,C1),Plus(Times(b,Sqr(c)),Times(a,Sqr(d)))),CN1)),x),Simp(Star(Times(b,Power(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(n,C1)),Subtract(c,Times(d,x)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),ILtQ(n,CN1))))
  );
}
