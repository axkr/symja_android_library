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
class IntRules156 { 
  public static IAST RULES = List( 
IIntegrate(3121,Integrate(Power(Times(b_,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(b,Sin(Plus(c,Times(d,x)))),n),Power(Power(Sin(Plus(c,Times(d,x))),n),CN1)),Integrate(Power(Sin(Plus(c,Times(d,x))),n),x)),x),And(FreeQ(list(b,c,d),x),LtQ(CN1,n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(3122,Integrate(Power(Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1),Sqrt(Sqr(Cos(Plus(c,Times(d,x)))))),CN1),Hypergeometric2F1(C1D2,Times(C1D2,Plus(n,C1)),Times(C1D2,Plus(n,C3)),Sqr(Sin(Plus(c,Times(d,x)))))),x),And(FreeQ(List(b,c,d,n),x),Not(IntegerQ(Times(C2,n)))))),
IIntegrate(3123,Integrate(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(Times(C2,Sqr(a)),Sqr(b)),C1D2,x),x),Negate(Simp(Times(C2,a,b,Cos(Plus(c,Times(d,x))),Power(d,CN1)),x)),Negate(Simp(Times(Sqr(b),Cos(Plus(c,Times(d,x))),Sin(Plus(c,Times(d,x))),Power(Times(C2,d),CN1)),x))),FreeQ(List(a,b,c,d),x))),
IIntegrate(3124,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Integrate(ExpandTrig(Power(Plus(a,Times(b,$($s("§sin"),Plus(c,Times(d,x))))),n),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(n,C0)))),
IIntegrate(3125,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(CN2,b,Cos(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))))),CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3126,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C1)),Power(Times(d,n),CN1)),x),Simp(Star(Times(a,Subtract(Times(C2,n),C1),Power(n,CN1)),Integrate(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(Subtract(n,C1D2),C0)))),
IIntegrate(3127,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Simp(Times(CN1,Cos(Plus(c,Times(d,x))),Power(Times(d,Plus(b,Times(a,Sin(Plus(c,Times(d,x)))))),CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3128,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Simp(Star(Times(CN2,Power(d,CN1)),Subst(Integrate(Power(Subtract(Times(C2,a),Sqr(x)),CN1),x),x,Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1D2)))),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3129,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),n),Power(Times(a,d,Plus(Times(C2,n),C1)),CN1)),x),Simp(Star(Times(Plus(n,C1),Power(Times(a,Plus(Times(C2,n),C1)),CN1)),Integrate(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(3130,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Simp(Times(CN1,Power(C2,Plus(n,C1D2)),Power(a,Subtract(n,C1D2)),b,Cos(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))))),CN1),Hypergeometric2F1(C1D2,Subtract(C1D2,n),QQ(3L,2L),Times(C1D2,Subtract(C1,Times(b,Sin(Plus(c,Times(d,x))),Power(a,CN1)))))),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),GtQ(a,C0)))),
IIntegrate(3131,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(n)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),FracPart(n)),Power(Power(Plus(C1,Times(b,Power(a,CN1),Sin(Plus(c,Times(d,x))))),FracPart(n)),CN1)),Integrate(Power(Plus(C1,Times(b,Power(a,CN1),Sin(Plus(c,Times(d,x))))),n),x)),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),Not(GtQ(a,C0))))),
IIntegrate(3132,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(C2,Sqrt(Plus(a,b)),Power(d,CN1),EllipticE(Times(C1D2,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),Times(C2,b,Power(Plus(a,b),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Plus(a,b),C0)))),
IIntegrate(3133,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(C2,Sqrt(Subtract(a,b)),Power(d,CN1),EllipticE(Times(C1D2,Plus(c,CPiHalf,Times(d,x))),Times(CN2,b,Power(Subtract(a,b),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Subtract(a,b),C0)))),
IIntegrate(3134,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x)))))),Power(Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1)),CN1D2)),Integrate(Sqrt(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Sin(Plus(c,Times(d,x)))))),x)),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),Not(GtQ(Plus(a,b),C0))))),
IIntegrate(3135,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C1)),Power(Times(d,n),CN1)),x),Simp(Star(Power(n,CN1),Integrate(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C2)),Simp(Plus(Times(Sqr(a),n),Times(Sqr(b),Subtract(n,C1)),Times(a,b,Subtract(Times(C2,n),C1),Sin(Plus(c,Times(d,x))))),x)),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(3136,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(a),Sqr(b)),C2))),Plus(Simp(Times(x,Power(q,CN1)),x),Simp(Times(C2,Power(Times(d,q),CN1),ArcTan(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,q,Times(b,Sin(Plus(c,Times(d,x))))),CN1)))),x))),And(FreeQ(List(a,b,c,d),x),GtQ(Subtract(Sqr(a),Sqr(b)),C0),PosQ(a)))),
IIntegrate(3137,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(a),Sqr(b)),C2))),Subtract(Simp(Times(CN1,x,Power(q,CN1)),x),Simp(Times(C2,Power(Times(d,q),CN1),ArcTan(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Negate(q),Times(b,Sin(Plus(c,Times(d,x))))),CN1)))),x))),And(FreeQ(List(a,b,c,d),x),GtQ(Subtract(Sqr(a),Sqr(b)),C0),NegQ(a)))),
IIntegrate(3138,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(CPiHalf,c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(e,FreeFactors(Tan(Times(C1D2,Plus(c,Times(d,x)))),x))),Simp(Star(Times(C2,e,Power(d,CN1)),Subst(Integrate(Power(Plus(a,b,Times(Subtract(a,b),Sqr(e),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(d,x)))),Power(e,CN1)))),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3139,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(e,FreeFactors(Tan(Times(C1D2,Plus(c,Times(d,x)))),x))),Simp(Star(Times(C2,e,Power(d,CN1)),Subst(Integrate(Power(Plus(a,Times(C2,b,e,x),Times(a,Sqr(e),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(d,x)))),Power(e,CN1)))),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3140,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Simp(Times(C2,Power(Times(d,Sqrt(Plus(a,b))),CN1),EllipticF(Times(C1D2,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),Times(C2,b,Power(Plus(a,b),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Plus(a,b),C0))))
  );
}
