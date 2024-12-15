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
class IntRules132 { 
  public static IAST RULES = List( 
IIntegrate(2641,Integrate(Power($($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),CN1D2),x_Symbol),
    Condition(Simp(Times(C2,EllipticF(Times(C1D2,C1,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),C2),Power(d,CN1)),x),FreeQ(list(c,d),x))),
IIntegrate(2642,Integrate(Power(Times(b_,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),CN1D2),x_Symbol),
    Condition(Dist(Times(Sqrt(Sin(Plus(c,Times(d,x)))),Power(Times(b,Sin(Plus(c,Times(d,x)))),CN1D2)),Integrate(Power(Sin(Plus(c,Times(d,x))),CN1D2),x),x),FreeQ(list(b,c,d),x))),
IIntegrate(2643,Integrate(Power(Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(b,Sin(Plus(c,Times(d,x)))),Plus(n,C1)),Hypergeometric2F1(C1D2,Times(C1D2,Plus(n,C1)),Times(C1D2,Plus(n,C3)),Sqr(Sin(Plus(c,Times(d,x))))),Power(Times(b,d,Plus(n,C1),Sqrt(Sqr(Cos(Plus(c,Times(d,x)))))),CN1)),x),And(FreeQ(List(b,c,d,n),x),Not(IntegerQ(Times(C2,n)))))),
IIntegrate(2644,Integrate(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(C1D2,Plus(Times(C2,Sqr(a)),Sqr(b)),x),x),Negate(Simp(Times(C2,a,b,Cos(Plus(c,Times(d,x))),Power(d,CN1)),x)),Negate(Simp(Times(Sqr(b),Cos(Plus(c,Times(d,x))),Sin(Plus(c,Times(d,x))),Power(Times(C2,d),CN1)),x))),FreeQ(List(a,b,c,d),x))),
IIntegrate(2645,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Integrate(ExpandTrig(Power(Plus(a,Times(b,$($s("§sin"),Plus(c,Times(d,x))))),n),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(n,C0)))),
IIntegrate(2646,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(CN2,b,Cos(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))))),CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(2647,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C1)),Power(Times(d,n),CN1)),x)),Dist(Times(a,Subtract(Times(C2,n),C1),Power(n,CN1)),Integrate(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(Subtract(n,C1D2),C0)))),
IIntegrate(2648,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Negate(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(d,Plus(b,Times(a,Sin(Plus(c,Times(d,x)))))),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(2649,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Dist(Times(CN2,Power(d,CN1)),Subst(Integrate(Power(Subtract(Times(C2,a),Sqr(x)),CN1),x),x,Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1D2))),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(2650,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),n),Power(Times(a,d,Plus(Times(C2,n),C1)),CN1)),x),Dist(Times(Plus(n,C1),Power(Times(a,Plus(Times(C2,n),C1)),CN1)),Integrate(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(2651,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Negate(Simp(Times(Power(C2,Plus(n,C1D2)),Power(a,Subtract(n,C1D2)),b,Cos(Plus(c,Times(d,x))),Hypergeometric2F1(C1D2,Subtract(C1D2,n),QQ(3L,2L),Times(C1D2,C1,Subtract(C1,Times(b,Sin(Plus(c,Times(d,x))),Power(a,CN1))))),Power(Times(d,Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))))),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),GtQ(a,C0)))),
IIntegrate(2652,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(n)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),FracPart(n)),Power(Power(Plus(C1,Times(b,Sin(Plus(c,Times(d,x))),Power(a,CN1))),FracPart(n)),CN1)),Integrate(Power(Plus(C1,Times(b,Sin(Plus(c,Times(d,x))),Power(a,CN1))),n),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),Not(GtQ(a,C0))))),
IIntegrate(2653,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(C2,Sqrt(Plus(a,b)),EllipticE(Times(C1D2,C1,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),Times(C2,b,Power(Plus(a,b),CN1))),Power(d,CN1)),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Plus(a,b),C0)))),
IIntegrate(2654,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(C2,Sqrt(Subtract(a,b)),EllipticE(Times(C1D2,C1,Plus(c,CPiHalf,Times(d,x))),Times(CN2,b,Power(Subtract(a,b),CN1))),Power(d,CN1)),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Subtract(a,b),C0)))),
IIntegrate(2655,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x)))))),Power(Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1)),CN1D2)),Integrate(Sqrt(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Sin(Plus(c,Times(d,x))),Power(Plus(a,b),CN1)))),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),Not(GtQ(Plus(a,b),C0))))),
IIntegrate(2656,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C1)),Power(Times(d,n),CN1)),x)),Dist(Power(n,CN1),Integrate(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Subtract(n,C2)),Simp(Plus(Times(Sqr(a),n),Times(Sqr(b),Subtract(n,C1)),Times(a,b,Subtract(Times(C2,n),C1),Sin(Plus(c,Times(d,x))))),x)),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(2657,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(a),Sqr(b)),C2))),Plus(Simp(Times(x,Power(q,CN1)),x),Simp(Times(C2,ArcTan(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,q,Times(b,Sin(Plus(c,Times(d,x))))),CN1))),Power(Times(d,q),CN1)),x))),And(FreeQ(List(a,b,c,d),x),GtQ(Subtract(Sqr(a),Sqr(b)),C0),PosQ(a)))),
IIntegrate(2658,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(a),Sqr(b)),C2))),Subtract(Negate(Simp(Times(x,Power(q,CN1)),x)),Simp(Times(C2,ArcTan(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Negate(q),Times(b,Sin(Plus(c,Times(d,x))))),CN1))),Power(Times(d,q),CN1)),x))),And(FreeQ(List(a,b,c,d),x),GtQ(Subtract(Sqr(a),Sqr(b)),C0),NegQ(a)))),
IIntegrate(2659,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(CPiHalf,c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(e,FreeFactors(Tan(Times(C1D2,Plus(c,Times(d,x)))),x))),Dist(Times(C2,e,Power(d,CN1)),Subst(Integrate(Power(Plus(a,b,Times(Subtract(a,b),Sqr(e),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(d,x)))),Power(e,CN1))),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(2660,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(e,FreeFactors(Tan(Times(C1D2,Plus(c,Times(d,x)))),x))),Dist(Times(C2,e,Power(d,CN1)),Subst(Integrate(Power(Plus(a,Times(C2,b,e,x),Times(a,Sqr(e),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(d,x)))),Power(e,CN1))),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0))))
  );
}
