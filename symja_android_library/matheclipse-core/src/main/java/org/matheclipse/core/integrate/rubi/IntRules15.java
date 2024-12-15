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
class IntRules15 { 
  public static IAST RULES = List( 
IIntegrate(301,Integrate(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(With(list(Set(r,Numerator(Rt(Times(CN1,a,Power(b,CN1)),C2))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,CN1)),C2)))),Subtract(Dist(Times(s,Power(Times(C2,b),CN1)),Integrate(Times(Power(x,Subtract(m,Times(C1D2,n))),Power(Plus(r,Times(s,Power(x,Times(C1D2,n)))),CN1)),x),x),Dist(Times(s,Power(Times(C2,b),CN1)),Integrate(Times(Power(x,Subtract(m,Times(C1D2,n))),Power(Subtract(r,Times(s,Power(x,Times(C1D2,n)))),CN1)),x),x))),And(FreeQ(list(a,b),x),IGtQ(Times(C1D4,n),C0),IGtQ(m,C0),LeQ(Times(C1D2,n),m),LtQ(m,n),Not(GtQ(Times(a,Power(b,CN1)),C0))))),
IIntegrate(302,Integrate(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Integrate(PolynomialDivide(Power(x,m),Plus(a,Times(b,Power(x,n))),x),x),And(FreeQ(list(a,b),x),IGtQ(m,C0),IGtQ(n,C0),GtQ(m,Subtract(Times(C2,n),C1))))),
IIntegrate(303,Integrate(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),CN1D2)),x_Symbol),
    Condition(With(list(Set(r,$($s("§numer"),Rt(Times(b,Power(a,CN1)),C3))),Set(s,$($s("§denom"),Rt(Times(b,Power(a,CN1)),C3)))),Plus(Dist(Times(CSqrt2,s,Power(Times(Sqrt(Plus(C2,CSqrt3)),r),CN1)),Integrate(Power(Plus(a,Times(b,Power(x,C3))),CN1D2),x),x),Dist(Power(r,CN1),Integrate(Times(Plus(Times(Subtract(C1,CSqrt3),s),Times(r,x)),Power(Plus(a,Times(b,Power(x,C3))),CN1D2)),x),x))),And(FreeQ(list(a,b),x),PosQ(a)))),
IIntegrate(304,Integrate(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),CN1D2)),x_Symbol),
    Condition(With(list(Set(r,$($s("§numer"),Rt(Times(b,Power(a,CN1)),C3))),Set(s,$($s("§denom"),Rt(Times(b,Power(a,CN1)),C3)))),Plus(Negate(Dist(Times(CSqrt2,s,Power(Times(Sqrt(Subtract(C2,CSqrt3)),r),CN1)),Integrate(Power(Plus(a,Times(b,Power(x,C3))),CN1D2),x),x)),Dist(Power(r,CN1),Integrate(Times(Plus(Times(Plus(C1,CSqrt3),s),Times(r,x)),Power(Plus(a,Times(b,Power(x,C3))),CN1D2)),x),x))),And(FreeQ(list(a,b),x),NegQ(a)))),
IIntegrate(305,Integrate(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(b,Power(a,CN1)),C2))),Subtract(Dist(Power(q,CN1),Integrate(Power(Plus(a,Times(b,Power(x,C4))),CN1D2),x),x),Dist(Power(q,CN1),Integrate(Times(Subtract(C1,Times(q,Sqr(x))),Power(Plus(a,Times(b,Power(x,C4))),CN1D2)),x),x))),And(FreeQ(list(a,b),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(306,Integrate(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,b,Power(a,CN1)),C2))),Subtract(Dist(Power(q,CN1),Integrate(Power(Plus(a,Times(b,Power(x,C4))),CN1D2),x),x),Dist(Power(q,CN1),Integrate(Times(Subtract(C1,Times(q,Sqr(x))),Power(Plus(a,Times(b,Power(x,C4))),CN1D2)),x),x))),And(FreeQ(list(a,b),x),LtQ(a,C0),GtQ(b,C0)))),
IIntegrate(307,Integrate(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,b,Power(a,CN1)),C2))),Plus(Negate(Dist(Power(q,CN1),Integrate(Power(Plus(a,Times(b,Power(x,C4))),CN1D2),x),x)),Dist(Power(q,CN1),Integrate(Times(Plus(C1,Times(q,Sqr(x))),Power(Plus(a,Times(b,Power(x,C4))),CN1D2)),x),x))),And(FreeQ(list(a,b),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(308,Integrate(Times(Power(x_,C4),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C6))),CN1D2)),x_Symbol),
    Condition(With(list(Set(r,$($s("§numer"),Rt(Times(b,Power(a,CN1)),C3))),Set(s,$($s("§denom"),Rt(Times(b,Power(a,CN1)),C3)))),Subtract(Dist(Times(Subtract(CSqrt3,C1),Sqr(s),Power(Times(C2,Sqr(r)),CN1)),Integrate(Power(Plus(a,Times(b,Power(x,C6))),CN1D2),x),x),Dist(Power(Times(C2,Sqr(r)),CN1),Integrate(Times(Subtract(Times(Subtract(CSqrt3,C1),Sqr(s)),Times(C2,Sqr(r),Power(x,C4))),Power(Plus(a,Times(b,Power(x,C6))),CN1D2)),x),x))),FreeQ(list(a,b),x))),
IIntegrate(309,Integrate(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C8))),CN1D2)),x_Symbol),
    Condition(Subtract(Dist(Power(Times(C2,Rt(Times(b,Power(a,CN1)),C4)),CN1),Integrate(Times(Plus(C1,Times(Rt(Times(b,Power(a,CN1)),C4),Sqr(x))),Power(Plus(a,Times(b,Power(x,C8))),CN1D2)),x),x),Dist(Power(Times(C2,Rt(Times(b,Power(a,CN1)),C4)),CN1),Integrate(Times(Subtract(C1,Times(Rt(Times(b,Power(a,CN1)),C4),Sqr(x))),Power(Plus(a,Times(b,Power(x,C8))),CN1D2)),x),x)),FreeQ(list(a,b),x))),
IIntegrate(310,Integrate(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,C3),Power(Times(C2,Power(Plus(a,Times(b,Power(x,C4))),C1D4)),CN1)),x),Dist(Times(C1D2,a),Integrate(Times(Sqr(x),Power(Plus(a,Times(b,Power(x,C4))),QQ(-5L,4L))),x),x)),And(FreeQ(list(a,b),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(311,Integrate(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D4)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,Power(x,C4))),QQ(3L,4L)),Power(Times(C2,b,x),CN1)),x),Dist(Times(a,Power(Times(C2,b),CN1)),Integrate(Power(Times(Sqr(x),Power(Plus(a,Times(b,Power(x,C4))),C1D4)),CN1),x),x)),And(FreeQ(list(a,b),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(312,Integrate(Times(Power(x_,CN2),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D4)),x_Symbol),
    Condition(Subtract(Negate(Simp(Power(Times(x,Power(Plus(a,Times(b,Power(x,C4))),C1D4)),CN1),x)),Dist(b,Integrate(Times(Sqr(x),Power(Plus(a,Times(b,Power(x,C4))),QQ(-5L,4L))),x),x)),And(FreeQ(list(a,b),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(313,Integrate(Times(Power(x_,CN2),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D4)),x_Symbol),
    Condition(Dist(Times(x,Power(Plus(C1,Times(a,Power(Times(b,Power(x,C4)),CN1))),C1D4),Power(Plus(a,Times(b,Power(x,C4))),CN1D4)),Integrate(Power(Times(Power(x,C3),Power(Plus(C1,Times(a,Power(Times(b,Power(x,C4)),CN1))),C1D4)),CN1),x),x),And(FreeQ(list(a,b),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(314,Integrate(Times(Sqrt(Times(c_,x_)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),CN1D4)),x),Dist(Times(C1D2,a),Integrate(Times(Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),QQ(-5L,4L))),x),x)),And(FreeQ(list(a,b,c),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(315,Integrate(Times(Sqrt(Times(c_,x_)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Plus(Simp(Times(c,Power(Plus(a,Times(b,Sqr(x))),QQ(3L,4L)),Power(Times(b,Sqrt(Times(c,x))),CN1)),x),Dist(Times(a,Sqr(c),Power(Times(C2,b),CN1)),Integrate(Power(Times(Power(Times(c,x),QQ(3L,2L)),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1),x),x)),And(FreeQ(list(a,b,c),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(316,Integrate(Times(Power(Times(c_DEFAULT,x_),QQ(-3L,2L)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(CN2,Power(Times(c,Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1)),x),Dist(Times(b,Power(c,CN2)),Integrate(Times(Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),QQ(-5L,4L))),x),x)),And(FreeQ(list(a,b,c),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(317,Integrate(Times(Power(Times(c_DEFAULT,x_),QQ(-3L,2L)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(c,x)),Power(Plus(C1,Times(a,Power(Times(b,Sqr(x)),CN1))),C1D4),Power(Times(Sqr(c),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1)),Integrate(Power(Times(Sqr(x),Power(Plus(C1,Times(a,Power(Times(b,Sqr(x)),CN1))),C1D4)),CN1),x),x),And(FreeQ(list(a,b,c),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(318,Integrate(Times(Sqrt(x_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(CN2,Power(Times(Sqrt(a),Power(Times(CN1,b,Power(a,CN1)),QQ(3L,4L))),CN1)),Subst(Integrate(Times(Sqrt(Subtract(C1,Times(C2,Sqr(x)))),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Times(Sqrt(Subtract(C1,Times(Sqrt(Times(CN1,b,Power(a,CN1))),x))),C1DSqrt2)),x),And(FreeQ(list(a,b),x),GtQ(Times(CN1,b,Power(a,CN1)),C0),GtQ(a,C0)))),
IIntegrate(319,Integrate(Times(Sqrt(x_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1)))),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),Integrate(Times(Sqrt(x),Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),CN1D2)),x),x),And(FreeQ(list(a,b),x),GtQ(Times(CN1,b,Power(a,CN1)),C0),Not(GtQ(a,C0))))),
IIntegrate(320,Integrate(Times(Sqrt(Times(c_,x_)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(c,x)),Power(x,CN1D2)),Integrate(Times(Sqrt(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),And(FreeQ(list(a,b,c),x),GtQ(Times(CN1,b,Power(a,CN1)),C0))))
  );
}
