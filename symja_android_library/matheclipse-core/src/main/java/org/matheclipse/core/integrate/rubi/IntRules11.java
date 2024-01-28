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
class IntRules11 { 
  public static IAST RULES = List( 
IIntegrate(221,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1),x_Symbol),
    Condition(Simp(Times(Rt(Times(CN1,a,Power(b,CN1)),C2),Power(a,CN1),ArcTanh(Times(x,Power(Rt(Times(CN1,a,Power(b,CN1)),C2),CN1)))),x),And(FreeQ(list(a,b),x),NegQ(Times(a,Power(b,CN1)))))),
IIntegrate(222,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Simp(Times(ArcSinh(Times(Rt(b,C2),x,Power(a,CN1D2))),Power(Rt(b,C2),CN1)),x),And(FreeQ(list(a,b),x),GtQ(a,C0),PosQ(b)))),
IIntegrate(223,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Simp(Times(ArcSin(Times(Rt(Negate(b),C2),x,Power(a,CN1D2))),Power(Rt(Negate(b),C2),CN1)),x),And(FreeQ(list(a,b),x),GtQ(a,C0),NegQ(b)))),
IIntegrate(224,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Subst(Integrate(Power(Subtract(C1,Times(b,Sqr(x))),CN1),x),x,Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),And(FreeQ(list(a,b),x),Not(GtQ(a,C0))))),
IIntegrate(225,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4),x_Symbol),
    Condition(Subtract(Simp(Times(C2,x,Power(Plus(a,Times(b,Sqr(x))),CN1D4)),x),Simp(Star(a,Integrate(Power(Plus(a,Times(b,Sqr(x))),QQ(-5L,4L)),x)),x)),And(FreeQ(list(a,b),x),GtQ(a,C0),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(226,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4),x_Symbol),
    Condition(Simp(Times(C2,Power(Times(Power(a,C1D4),Rt(Times(CN1,b,Power(a,CN1)),C2)),CN1),EllipticE(Times(C1D2,ArcSin(Times(Rt(Times(CN1,b,Power(a,CN1)),C2),x))),C2)),x),And(FreeQ(list(a,b),x),GtQ(a,C0),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(227,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),C1D4),Power(Plus(a,Times(b,Sqr(x))),CN1D4)),Integrate(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),CN1D4),x)),x),And(FreeQ(list(a,b),x),PosQ(a)))),
IIntegrate(228,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4),x_Symbol),
    Condition(Simp(Star(Times(C2,Sqrt(Times(CN1,b,Sqr(x),Power(a,CN1))),Power(Times(b,x),CN1)),Subst(Integrate(Times(Sqr(x),Power(Subtract(C1,Times(Power(x,C4),Power(a,CN1))),CN1D2)),x),x,Power(Plus(a,Times(b,Sqr(x))),C1D4))),x),And(FreeQ(list(a,b),x),NegQ(a)))),
IIntegrate(229,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),x_Symbol),
    Condition(Simp(Times(C2,Power(Times(Power(a,QQ(3L,4L)),Rt(Times(b,Power(a,CN1)),C2)),CN1),EllipticF(Times(C1D2,ArcTan(Times(Rt(Times(b,Power(a,CN1)),C2),x))),C2)),x),And(FreeQ(list(a,b),x),GtQ(a,C0),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(230,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),x_Symbol),
    Condition(Simp(Times(C2,Power(Times(Power(a,QQ(3L,4L)),Rt(Times(CN1,b,Power(a,CN1)),C2)),CN1),EllipticF(Times(C1D2,ArcSin(Times(Rt(Times(CN1,b,Power(a,CN1)),C2),x))),C2)),x),And(FreeQ(list(a,b),x),GtQ(a,C0),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(231,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),QQ(3L,4L)),Power(Plus(a,Times(b,Sqr(x))),QQ(-3L,4L))),Integrate(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),QQ(-3L,4L)),x)),x),And(FreeQ(list(a,b),x),PosQ(a)))),
IIntegrate(232,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),x_Symbol),
    Condition(Simp(Star(Times(C2,Sqrt(Times(CN1,b,Sqr(x),Power(a,CN1))),Power(Times(b,x),CN1)),Subst(Integrate(Power(Subtract(C1,Times(Power(x,C4),Power(a,CN1))),CN1D2),x),x,Power(Plus(a,Times(b,Sqr(x))),C1D4))),x),And(FreeQ(list(a,b),x),NegQ(a)))),
IIntegrate(233,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D3),x_Symbol),
    Condition(Simp(Star(Times(C3,Sqrt(Times(b,Sqr(x))),Power(Times(C2,b,x),CN1)),Subst(Integrate(Times(x,Power(Plus(Negate(a),Power(x,C3)),CN1D2)),x),x,Power(Plus(a,Times(b,Sqr(x))),C1D3))),x),FreeQ(list(a,b),x))),
IIntegrate(234,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-2L,3L)),x_Symbol),
    Condition(Simp(Star(Times(C3,Sqrt(Times(b,Sqr(x))),Power(Times(C2,b,x),CN1)),Subst(Integrate(Power(Plus(Negate(a),Power(x,C3)),CN1D2),x),x,Power(Plus(a,Times(b,Sqr(x))),C1D3))),x),FreeQ(list(a,b),x))),
IIntegrate(235,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-1L,6L)),x_Symbol),
    Condition(Subtract(Simp(Times(C3,x,Power(Times(C2,Power(Plus(a,Times(b,Sqr(x))),QQ(1L,6L))),CN1)),x),Simp(Star(Times(C1D2,a),Integrate(Power(Plus(a,Times(b,Sqr(x))),QQ(-7L,6L)),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(236,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-5L,6L)),x_Symbol),
    Condition(Simp(Star(Power(Times(Power(Times(a,Power(Plus(a,Times(b,Sqr(x))),CN1)),C1D3),Power(Plus(a,Times(b,Sqr(x))),C1D3)),CN1),Subst(Integrate(Power(Subtract(C1,Times(b,Sqr(x))),QQ(-2L,3L)),x),x,Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),FreeQ(list(a,b),x))),
IIntegrate(237,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Simp(Times(Power(a,p),x,Hypergeometric2F1(Negate(p),C1D2,Plus(C1D2,C1),Times(CN1,b,Sqr(x),Power(a,CN1)))),x),And(FreeQ(list(a,b,p),x),Not(IntegerQ(Times(C2,p))),GtQ(a,C0)))),
IIntegrate(238,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(p)),Power(Plus(a,Times(b,Sqr(x))),FracPart(p)),Power(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),FracPart(p)),CN1)),Integrate(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),p),x)),x),And(FreeQ(list(a,b,p),x),Not(IntegerQ(Times(C2,p))),Not(GtQ(a,C0))))),
IIntegrate(239,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_))),p_),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(v,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Power(x,n))),p),x),x,v)),x),And(FreeQ(List(a,b,n,p),x),LinearQ(v,x),NeQ(v,x)))),
IIntegrate(240,Integrate(Times(x_,Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Log(RemoveContent(Plus(a,Times(b,Sqr(x))),x)),Power(Times(C2,b),CN1)),x),FreeQ(list(a,b),x)))
  );
}
