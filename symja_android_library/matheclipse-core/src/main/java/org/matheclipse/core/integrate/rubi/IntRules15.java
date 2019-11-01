package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules15 { 
  public static IAST RULES = List( 
IIntegrate(301,Int(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(With(List(Set(r,Numerator(Rt(Times(CN1,a,Power(b,CN1)),C2))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,CN1)),C2)))),Subtract(Dist(Times(s,Power(Times(C2,b),CN1)),Int(Times(Power(x,Subtract(m,Times(C1D2,n))),Power(Plus(r,Times(s,Power(x,Times(C1D2,n)))),CN1)),x),x),Dist(Times(s,Power(Times(C2,b),CN1)),Int(Times(Power(x,Subtract(m,Times(C1D2,n))),Power(Subtract(r,Times(s,Power(x,Times(C1D2,n)))),CN1)),x),x))),And(FreeQ(List(a,b),x),IGtQ(Times(C1D4,n),C0),IGtQ(m,C0),LeQ(Times(C1D2,n),m),LtQ(m,n),Not(GtQ(Times(a,Power(b,CN1)),C0))))),
IIntegrate(302,Int(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Int(PolynomialDivide(Power(x,m),Plus(a,Times(b,Power(x,n))),x),x),And(FreeQ(List(a,b),x),IGtQ(m,C0),IGtQ(n,C0),GtQ(m,Subtract(Times(C2,n),C1))))),
IIntegrate(303,Int(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),CN1D2)),x_Symbol),
    Condition(With(List(Set(r,$($s("§numer"),Rt(Times(b,Power(a,CN1)),C3))),Set(s,$($s("§denom"),Rt(Times(b,Power(a,CN1)),C3)))),Plus(Dist(Times(CSqrt2,s,Power(Times(Sqrt(Plus(C2,CSqrt3)),r),CN1)),Int(Power(Plus(a,Times(b,Power(x,C3))),CN1D2),x),x),Dist(Power(r,CN1),Int(Times(Plus(Times(Subtract(C1,CSqrt3),s),Times(r,x)),Power(Plus(a,Times(b,Power(x,C3))),CN1D2)),x),x))),And(FreeQ(List(a,b),x),PosQ(a)))),
IIntegrate(304,Int(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),CN1D2)),x_Symbol),
    Condition(With(List(Set(r,$($s("§numer"),Rt(Times(b,Power(a,CN1)),C3))),Set(s,$($s("§denom"),Rt(Times(b,Power(a,CN1)),C3)))),Plus(Negate(Dist(Times(CSqrt2,s,Power(Times(Sqrt(Subtract(C2,CSqrt3)),r),CN1)),Int(Power(Plus(a,Times(b,Power(x,C3))),CN1D2),x),x)),Dist(Power(r,CN1),Int(Times(Plus(Times(Plus(C1,CSqrt3),s),Times(r,x)),Power(Plus(a,Times(b,Power(x,C3))),CN1D2)),x),x))),And(FreeQ(List(a,b),x),NegQ(a)))),
IIntegrate(305,Int(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(With(List(Set(q,Rt(Times(b,Power(a,CN1)),C2))),Subtract(Dist(Power(q,CN1),Int(Power(Plus(a,Times(b,Power(x,C4))),CN1D2),x),x),Dist(Power(q,CN1),Int(Times(Subtract(C1,Times(q,Sqr(x))),Power(Plus(a,Times(b,Power(x,C4))),CN1D2)),x),x))),And(FreeQ(List(a,b),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(306,Int(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(With(List(Set(q,Rt(Times(CN1,b,Power(a,CN1)),C2))),Subtract(Dist(Power(q,CN1),Int(Power(Plus(a,Times(b,Power(x,C4))),CN1D2),x),x),Dist(Power(q,CN1),Int(Times(Subtract(C1,Times(q,Sqr(x))),Power(Plus(a,Times(b,Power(x,C4))),CN1D2)),x),x))),And(FreeQ(List(a,b),x),LtQ(a,C0),GtQ(b,C0)))),
IIntegrate(307,Int(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(With(List(Set(q,Rt(Times(CN1,b,Power(a,CN1)),C2))),Plus(Negate(Dist(Power(q,CN1),Int(Power(Plus(a,Times(b,Power(x,C4))),CN1D2),x),x)),Dist(Power(q,CN1),Int(Times(Plus(C1,Times(q,Sqr(x))),Power(Plus(a,Times(b,Power(x,C4))),CN1D2)),x),x))),And(FreeQ(List(a,b),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(308,Int(Times(Power(x_,C4),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C6))),CN1D2)),x_Symbol),
    Condition(With(List(Set(r,$($s("§numer"),Rt(Times(b,Power(a,CN1)),C3))),Set(s,$($s("§denom"),Rt(Times(b,Power(a,CN1)),C3)))),Subtract(Dist(Times(Subtract(CSqrt3,C1),Sqr(s),Power(Times(C2,Sqr(r)),CN1)),Int(Power(Plus(a,Times(b,Power(x,C6))),CN1D2),x),x),Dist(Power(Times(C2,Sqr(r)),CN1),Int(Times(Subtract(Times(Subtract(CSqrt3,C1),Sqr(s)),Times(C2,Sqr(r),Power(x,C4))),Power(Plus(a,Times(b,Power(x,C6))),CN1D2)),x),x))),FreeQ(List(a,b),x))),
IIntegrate(309,Int(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C8))),CN1D2)),x_Symbol),
    Condition(Subtract(Dist(Power(Times(C2,Rt(Times(b,Power(a,CN1)),C4)),CN1),Int(Times(Plus(C1,Times(Rt(Times(b,Power(a,CN1)),C4),Sqr(x))),Power(Plus(a,Times(b,Power(x,C8))),CN1D2)),x),x),Dist(Power(Times(C2,Rt(Times(b,Power(a,CN1)),C4)),CN1),Int(Times(Subtract(C1,Times(Rt(Times(b,Power(a,CN1)),C4),Sqr(x))),Power(Plus(a,Times(b,Power(x,C8))),CN1D2)),x),x)),FreeQ(List(a,b),x))),
IIntegrate(310,Int(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,C3),Power(Times(C2,Power(Plus(a,Times(b,Power(x,C4))),C1D4)),CN1)),x),Dist(Times(C1D2,a),Int(Times(Sqr(x),Power(Plus(a,Times(b,Power(x,C4))),QQ(-5L,4L))),x),x)),And(FreeQ(List(a,b),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(311,Int(Times(Sqr(x_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D4)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,Power(x,C4))),QQ(3L,4L)),Power(Times(C2,b,x),CN1)),x),Dist(Times(a,Power(Times(C2,b),CN1)),Int(Power(Times(Sqr(x),Power(Plus(a,Times(b,Power(x,C4))),C1D4)),CN1),x),x)),And(FreeQ(List(a,b),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(312,Int(Times(Power(x_,CN2),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D4)),x_Symbol),
    Condition(Subtract(Negate(Simp(Power(Times(x,Power(Plus(a,Times(b,Power(x,C4))),C1D4)),CN1),x)),Dist(b,Int(Times(Sqr(x),Power(Plus(a,Times(b,Power(x,C4))),QQ(-5L,4L))),x),x)),And(FreeQ(List(a,b),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(313,Int(Times(Power(x_,CN2),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D4)),x_Symbol),
    Condition(Dist(Times(x,Power(Plus(C1,Times(a,Power(Times(b,Power(x,C4)),CN1))),C1D4),Power(Plus(a,Times(b,Power(x,C4))),CN1D4)),Int(Power(Times(Power(x,C3),Power(Plus(C1,Times(a,Power(Times(b,Power(x,C4)),CN1))),C1D4)),CN1),x),x),And(FreeQ(List(a,b),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(314,Int(Times(Sqrt(Times(c_,x_)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),CN1D4)),x),Dist(Times(C1D2,a),Int(Times(Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),QQ(-5L,4L))),x),x)),And(FreeQ(List(a,b,c),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(315,Int(Times(Sqrt(Times(c_,x_)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Plus(Simp(Times(c,Power(Plus(a,Times(b,Sqr(x))),QQ(3L,4L)),Power(Times(b,Sqrt(Times(c,x))),CN1)),x),Dist(Times(a,Sqr(c),Power(Times(C2,b),CN1)),Int(Power(Times(Power(Times(c,x),QQ(3L,2L)),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1),x),x)),And(FreeQ(List(a,b,c),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(316,Int(Times(Power(Times(c_DEFAULT,x_),QQ(-3L,2L)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(CN2,Power(Times(c,Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1)),x),Dist(Times(b,Power(c,CN2)),Int(Times(Sqrt(Times(c,x)),Power(Plus(a,Times(b,Sqr(x))),QQ(-5L,4L))),x),x)),And(FreeQ(List(a,b,c),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(317,Int(Times(Power(Times(c_DEFAULT,x_),QQ(-3L,2L)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4)),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(c,x)),Power(Plus(C1,Times(a,Power(Times(b,Sqr(x)),CN1))),C1D4),Power(Times(Sqr(c),Power(Plus(a,Times(b,Sqr(x))),C1D4)),CN1)),Int(Power(Times(Sqr(x),Power(Plus(C1,Times(a,Power(Times(b,Sqr(x)),CN1))),C1D4)),CN1),x),x),And(FreeQ(List(a,b,c),x),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(318,Int(Times(Sqrt(x_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(CN2,Power(Times(Sqrt(a),Power(Times(CN1,b,Power(a,CN1)),QQ(3L,4L))),CN1)),Subst(Int(Times(Sqrt(Subtract(C1,Times(C2,Sqr(x)))),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Times(Sqrt(Subtract(C1,Times(Sqrt(Times(CN1,b,Power(a,CN1))),x))),C1DSqrt2)),x),And(FreeQ(List(a,b),x),GtQ(Times(CN1,b,Power(a,CN1)),C0),GtQ(a,C0)))),
IIntegrate(319,Int(Times(Sqrt(x_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1)))),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),Int(Times(Sqrt(x),Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),CN1D2)),x),x),And(FreeQ(List(a,b),x),GtQ(Times(CN1,b,Power(a,CN1)),C0),Not(GtQ(a,C0))))),
IIntegrate(320,Int(Times(Sqrt(Times(c_,x_)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(c,x)),Power(x,CN1D2)),Int(Times(Sqrt(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x),x),And(FreeQ(List(a,b,c),x),GtQ(Times(CN1,b,Power(a,CN1)),C0))))
  );
}
