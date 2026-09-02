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
class IntRules157 { 
  public static IAST RULES = List( 
IIntegrate(3141,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),n),Power(Times(a,d,Plus(Times(C2,n),C1)),CN1)),x),Simp(Dist(Times(Plus(n,C1),Power(Times(a,Plus(Times(C2,n),C1)),CN1)),Integrate(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(3142,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Simp(Times(CN1,Power(C2,Plus(n,C1D2)),Power(a,Plus(n,CN1D2)),b,Cos(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))))),CN1),Hypergeometric2F1(C1D2,Subtract(C1D2,n),QQ(3L,2L),Times(C1D2,Subtract(C1,Times(b,Sin(Plus(c,Times(d,x))),Power(a,CN1)))))),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),GtQ(a,C0)))),
IIntegrate(3143,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Simp(Dist(Times(Power(a,IntPart(n)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),FracPart(n)),Power(Power(Plus(C1,Times(b,Power(a,CN1),Sin(Plus(c,Times(d,x))))),FracPart(n)),CN1)),Integrate(Power(Plus(C1,Times(b,Power(a,CN1),Sin(Plus(c,Times(d,x))))),n),x),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),Not(GtQ(a,C0))))),
IIntegrate(3144,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(C2,Sqrt(Plus(a,b)),Power(d,CN1),EllipticE(Times(C1D2,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),Times(C2,b,Power(Plus(a,b),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Plus(a,b),C0)))),
IIntegrate(3145,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(C2,Sqrt(Subtract(a,b)),Power(d,CN1),EllipticE(Times(C1D2,Plus(c,CPiHalf,Times(d,x))),Times(CN2,b,Power(Subtract(a,b),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Subtract(a,b),C0)))),
IIntegrate(3146,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Plus(a,Times(b,Sin(Plus(c,Times(d,x)))))),Power(Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1)),CN1D2)),Integrate(Sqrt(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Sin(Plus(c,Times(d,x)))))),x),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),Not(GtQ(Plus(a,b),C0))))),
IIntegrate(3147,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,CN1)),Power(Times(d,n),CN1)),x),Simp(Dist(Power(n,CN1),Integrate(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,CN2)),Simp(Plus(Times(Sqr(a),n),Times(Sqr(b),Plus(n,CN1)),Times(a,b,Plus(Times(C2,n),CN1),Sin(Plus(c,Times(d,x))))),x)),x),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(3148,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(a),Sqr(b)),C2))),Plus(Simp(Times(x,Power(q,CN1)),x),Simp(Times(C2,Power(Times(d,q),CN1),ArcTan(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,q,Times(b,Sin(Plus(c,Times(d,x))))),CN1)))),x))),And(FreeQ(List(a,b,c,d),x),GtQ(Subtract(Sqr(a),Sqr(b)),C0),PosQ(a)))),
IIntegrate(3149,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(a),Sqr(b)),C2))),Subtract(Simp(Times(CN1,x,Power(q,CN1)),x),Simp(Times(C2,Power(Times(d,q),CN1),ArcTan(Times(b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Negate(q),Times(b,Sin(Plus(c,Times(d,x))))),CN1)))),x))),And(FreeQ(List(a,b,c,d),x),GtQ(Subtract(Sqr(a),Sqr(b)),C0),NegQ(a)))),
IIntegrate(3150,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(CPiHalf,c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(e,FreeFactors(Tan(Times(C1D2,Plus(c,Times(d,x)))),x))),Simp(Dist(Times(C2,e,Power(d,CN1)),Subst(Integrate(Power(Plus(a,b,Times(Subtract(a,b),Sqr(e),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(d,x)))),Power(e,CN1))),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3151,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(With(list(Set(e,FreeFactors(Tan(Times(C1D2,Plus(c,Times(d,x)))),x))),Simp(Dist(Times(C2,e,Power(d,CN1)),Subst(Integrate(Power(Plus(a,Times(C2,b,e,x),Times(a,Sqr(e),Sqr(x))),CN1),x),x,Times(Tan(Times(C1D2,Plus(c,Times(d,x)))),Power(e,CN1))),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3152,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Simp(Times(C2,Power(Times(d,Sqrt(Plus(a,b))),CN1),EllipticF(Times(C1D2,Plus(c,Times(CN1,C1D2,Pi),Times(d,x))),Times(C2,b,Power(Plus(a,b),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Plus(a,b),C0)))),
IIntegrate(3153,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Simp(Times(C2,Power(Times(d,Sqrt(Subtract(a,b))),CN1),EllipticF(Times(C1D2,Plus(c,CPiHalf,Times(d,x))),Times(CN2,b,Power(Subtract(a,b),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(Subtract(a,b),C0)))),
IIntegrate(3154,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1D2),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Times(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Power(Plus(a,b),CN1))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1D2)),Integrate(Power(Plus(Times(a,Power(Plus(a,b),CN1)),Times(b,Power(Plus(a,b),CN1),Sin(Plus(c,Times(d,x))))),CN1D2),x),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),Not(GtQ(Plus(a,b),C0))))),
IIntegrate(3155,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(d,Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1)),x),Simp(Dist(Power(Times(Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1),Integrate(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),Simp(Subtract(Times(a,Plus(n,C1)),Times(b,Plus(n,C2),Sin(Plus(c,Times(d,x))))),x)),x),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(3156,Integrate(Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Simp(Dist(Times(Cos(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(C1,Sin(Plus(c,Times(d,x))))),Sqrt(Subtract(C1,Sin(Plus(c,Times(d,x)))))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Times(Sqrt(Plus(C1,x)),Sqrt(Subtract(C1,x))),CN1)),x),x,Sin(Plus(c,Times(d,x)))),x),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n)))))),
IIntegrate(3157,Integrate(Power(Plus(a_,Times($($s("§cos"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT,$($s("§sin"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,C1D2,Sin(Plus(Times(C2,c),Times(C2,d,x))))),n),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(3158,Integrate(Times(Power($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),p_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(Power(b,p),f),CN1),Subst(Integrate(Times(Power(Plus(a,x),Plus(m,Times(C1D2,Plus(p,CN1)))),Power(Subtract(a,x),Times(C1D2,Plus(p,CN1)))),x),x,Times(b,Sin(Plus(e,Times(f,x))))),x),x),And(FreeQ(List(a,b,e,f,m),x),IntegerQ(Times(C1D2,Plus(p,CN1))),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Or(GeQ(p,CN1),Not(IntegerQ(Plus(m,C1D2))))))),
IIntegrate(3159,Integrate(Times(Power($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),p_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(Power(b,p),f),CN1),Subst(Integrate(Times(Power(Plus(a,x),m),Power(Subtract(Sqr(b),Sqr(x)),Times(C1D2,Plus(p,CN1)))),x),x,Times(b,Sin(Plus(e,Times(f,x))))),x),x),And(FreeQ(List(a,b,e,f,m),x),IntegerQ(Times(C1D2,Plus(p,CN1))),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3160,Integrate(Times(Power(Times($($s("§cos"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),g_DEFAULT),p_),Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Power(Times(g,Cos(Plus(e,Times(f,x)))),Plus(p,C1)),Power(Times(f,g,Plus(p,C1)),CN1)),x),Simp(Dist(a,Integrate(Power(Times(g,Cos(Plus(e,Times(f,x)))),p),x),x),x)),And(FreeQ(List(a,b,e,f,g,p),x),Or(IntegerQ(Times(C2,p)),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))))
  );
}
