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
class IntRules54 { 
  public static IAST RULES = List( 
IIntegrate(1081,Integrate(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Simp(Star(c,Integrate(ExpandIntegrand(Power(Times(Plus(Times(C1D2,b),Times(CN1,C1D2,q),Times(c,x)),Plus(Times(C1D2,b),Times(C1D2,q),Times(c,x))),CN1),x),x)),x)),And(FreeQ(list(a,b,c),x),NiceSqrtQ(Subtract(Sqr(b),Times(C4,a,c)))))),
IIntegrate(1082,Integrate(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(With(list(Set(q,Subtract(C1,Times(C4,Simplify(Times(a,c,Power(b,CN2))))))),Condition(Simp(Star(Times(CN2,Power(b,CN1)),Subst(Integrate(Power(Subtract(q,Sqr(x)),CN1),x),x,Plus(C1,Times(C2,c,x,Power(b,CN1))))),x),And(RationalQ(q),Or(EqQ(Sqr(q),C1),Not(RationalQ(Subtract(Sqr(b),Times(C4,a,c)))))))),FreeQ(list(a,b,c),x))),
IIntegrate(1083,Integrate(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(Simp(Star(CN2,Subst(Integrate(Power(Simp(Subtract(Subtract(Sqr(b),Times(C4,a,c)),Sqr(x)),x),CN1),x),x,Plus(b,Times(C2,c,x)))),x),FreeQ(list(a,b,c),x))),
IIntegrate(1084,Integrate(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Condition(Simp(Star(Power(Power(c,p),CN1),Integrate(ExpandIntegrand(Times(Power(Plus(Times(C1D2,b),Times(CN1,C1D2,q),Times(c,x)),p),Power(Plus(Times(C1D2,b),Times(C1D2,q),Times(c,x)),p)),x),x)),x),Not($($s("§fractionalpowerfactorq"),q)))),And(FreeQ(list(a,b,c),x),IntegerQ(p),NiceSqrtQ(Subtract(Sqr(b),Times(C4,a,c)))))),
IIntegrate(1085,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x),And(FreeQ(list(a,b,c),x),IntegerQ(p),Or(GtQ(p,C0),EqQ(a,C0))))),
IIntegrate(1086,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),x),Simp(Star(Times(C2,c,Plus(Times(C2,p),C3),Power(Times(Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),Integrate(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),x)),x)),And(FreeQ(list(a,b,c),x),ILtQ(p,CN1)))),
IIntegrate(1087,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Times(C2,c,Plus(Times(C2,p),C1)),CN1)),x),Simp(Star(Times(p,Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C2,c,Plus(Times(C2,p),C1)),CN1)),Integrate(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Subtract(p,C1)),x)),x)),And(FreeQ(list(a,b,c),x),GtQ(p,C0),Or(IntegerQ(Times(C4,p)),IntegerQ(Times(C3,p)))))),
IIntegrate(1088,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),QQ(-3L,2L)),x_Symbol),
    Condition(Simp(Times(CN2,Plus(b,Times(C2,c,x)),Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x))))),CN1)),x),And(FreeQ(list(a,b,c),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(1089,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),x),Simp(Star(Times(C2,c,Plus(Times(C2,p),C3),Power(Times(Plus(p,C1),Subtract(Sqr(b),Times(C4,a,c))),CN1)),Integrate(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),x)),x)),And(FreeQ(list(a,b,c),x),LtQ(p,CN1),Or(IntegerQ(Times(C4,p)),IntegerQ(Times(C3,p)))))),
IIntegrate(1090,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Simp(Star(Power(Times(C2,c,Power(Times(CN4,c,Power(Subtract(Sqr(b),Times(C4,a,c)),CN1)),p)),CN1),Subst(Integrate(Power(Simp(Subtract(C1,Times(Sqr(x),Power(Subtract(Sqr(b),Times(C4,a,c)),CN1))),x),p),x),x,Plus(b,Times(C2,c,x)))),x),And(FreeQ(List(a,b,c,p),x),GtQ(Subtract(Times(C4,a),Times(Sqr(b),Power(c,CN1))),C0)))),
IIntegrate(1091,Integrate(Power(Plus(Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Simp(Star(C2,Subst(Integrate(Power(Subtract(C1,Times(c,Sqr(x))),CN1),x),x,Times(x,Power(Plus(Times(b,x),Times(c,Sqr(x))),CN1D2)))),x),FreeQ(list(b,c),x))),
IIntegrate(1092,Integrate(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Simp(Star(C2,Subst(Integrate(Power(Subtract(Times(C4,c),Sqr(x)),CN1),x),x,Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2)))),x),FreeQ(list(a,b,c),x))),
IIntegrate(1093,Integrate(Power(Plus(Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(Times(b,x),Times(c,Sqr(x))),p),Power(Power(Times(CN1,c,Plus(Times(b,x),Times(c,Sqr(x))),Power(b,CN2)),p),CN1)),Integrate(Power(Subtract(Times(CN1,c,x,Power(b,CN1)),Times(Sqr(c),Sqr(x),Power(b,CN2))),p),x)),x),And(FreeQ(list(b,c),x),Or(IntegerQ(Times(C4,p)),IntegerQ(Times(C3,p)))))),
IIntegrate(1094,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Simp(Star(Times(C4,Sqrt(Sqr(Plus(b,Times(C2,c,x)))),Power(Plus(b,Times(C2,c,x)),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(C4,Plus(p,C1)),C1)),Power(Plus(Sqr(b),Times(CN1,C4,a,c),Times(C4,c,Power(x,C4))),CN1D2)),x),x,Power(Plus(a,Times(b,x),Times(c,Sqr(x))),C1D4))),x),And(FreeQ(list(a,b,c),x),IntegerQ(Times(C4,p))))),
IIntegrate(1095,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Simp(Star(Times(C3,Sqrt(Sqr(Plus(b,Times(C2,c,x)))),Power(Plus(b,Times(C2,c,x)),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(C3,Plus(p,C1)),C1)),Power(Plus(Sqr(b),Times(CN1,C4,a,c),Times(C4,c,Power(x,C3))),CN1D2)),x),x,Power(Plus(a,Times(b,x),Times(c,Sqr(x))),C1D3))),x),And(FreeQ(list(a,b,c),x),IntegerQ(Times(C3,p))))),
IIntegrate(1096,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Simp(Times(CN1,Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(q,Plus(p,C1),Power(Times(Subtract(Subtract(q,b),Times(C2,c,x)),Power(Times(C2,q),CN1)),Plus(p,C1))),CN1),Hypergeometric2F1(Negate(p),Plus(p,C1),Plus(p,C2),Times(Plus(b,q,Times(C2,c,x)),Power(Times(C2,q),CN1)))),x)),And(FreeQ(List(a,b,c,p),x),Not(IntegerQ(Times(C4,p))),Not(IntegerQ(Times(C3,p)))))),
IIntegrate(1097,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,u_),Times(c_DEFAULT,Sqr(u_))),p_),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x,u)),x),And(FreeQ(List(a,b,c,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(1098,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(c,p),CN1),Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p))),x)),x),And(FreeQ(List(a,b,c,d,e,m),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(p)))),
IIntegrate(1099,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(Power(e,m),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,Times(C1D2,Plus(m,C1)))),Power(Times(Power(c,Times(C1D2,Plus(m,C1))),Plus(m,Times(C2,p),C1)),CN1)),x),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(1100,Integrate(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(C2,c,Plus(p,C1)),CN1)),x),Simp(Star(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x)),x)),And(FreeQ(List(a,b,c,d,e,p),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0))))
  );
}
