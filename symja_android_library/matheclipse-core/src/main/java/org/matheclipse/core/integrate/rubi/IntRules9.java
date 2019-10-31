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
public class IntRules9 { 
  public static IAST RULES = List( 
IIntegrate(226,Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C8))),CN1D2),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Subtract(C1,Times(Rt(Times(b,Power(a,CN1)),C4),Sqr(x))),Power(Plus(a,Times(b,Power(x,C8))),CN1D2)),x),x),Dist(C1D2,Int(Times(Plus(C1,Times(Rt(Times(b,Power(a,CN1)),C4),Sqr(x))),Power(Plus(a,Times(b,Power(x,C8))),CN1D2)),x),x)),FreeQ(List(a,b),x))),
IIntegrate(227,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4),x_Symbol),
    Condition(Subtract(Simp(Times(C2,x,Power(Plus(a,Times(b,Sqr(x))),CN1D4)),x),Dist(a,Int(Power(Plus(a,Times(b,Sqr(x))),QQ(-5L,4L)),x),x)),And(FreeQ(List(a,b),x),GtQ(a,C0),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(228,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4),x_Symbol),
    Condition(Simp(Times(C2,EllipticE(Times(C1D2,C1,ArcSin(Times(Rt(Times(CN1,b,Power(a,CN1)),C2),x))),C2),Power(Times(Power(a,C1D4),Rt(Times(CN1,b,Power(a,CN1)),C2)),CN1)),x),And(FreeQ(List(a,b),x),GtQ(a,C0),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(229,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4),x_Symbol),
    Condition(Dist(Times(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),C1D4),Power(Plus(a,Times(b,Sqr(x))),CN1D4)),Int(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),CN1D4),x),x),And(FreeQ(List(a,b),x),PosQ(a)))),
IIntegrate(230,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D4),x_Symbol),
    Condition(Dist(Times(C2,Sqrt(Times(CN1,b,Sqr(x),Power(a,CN1))),Power(Times(b,x),CN1)),Subst(Int(Times(Sqr(x),Power(Subtract(C1,Times(Power(x,C4),Power(a,CN1))),CN1D2)),x),x,Power(Plus(a,Times(b,Sqr(x))),C1D4)),x),And(FreeQ(List(a,b),x),NegQ(a)))),
IIntegrate(231,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),x_Symbol),
    Condition(Simp(Times(C2,EllipticF(Times(C1D2,C1,ArcTan(Times(Rt(Times(b,Power(a,CN1)),C2),x))),C2),Power(Times(Power(a,QQ(3L,4L)),Rt(Times(b,Power(a,CN1)),C2)),CN1)),x),And(FreeQ(List(a,b),x),GtQ(a,C0),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(232,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),x_Symbol),
    Condition(Simp(Times(C2,EllipticF(Times(C1D2,C1,ArcSin(Times(Rt(Times(CN1,b,Power(a,CN1)),C2),x))),C2),Power(Times(Power(a,QQ(3L,4L)),Rt(Times(CN1,b,Power(a,CN1)),C2)),CN1)),x),And(FreeQ(List(a,b),x),GtQ(a,C0),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(233,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),x_Symbol),
    Condition(Dist(Times(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),QQ(3L,4L)),Power(Plus(a,Times(b,Sqr(x))),QQ(-3L,4L))),Int(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),QQ(-3L,4L)),x),x),And(FreeQ(List(a,b),x),PosQ(a)))),
IIntegrate(234,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-3L,4L)),x_Symbol),
    Condition(Dist(Times(C2,Sqrt(Times(CN1,b,Sqr(x),Power(a,CN1))),Power(Times(b,x),CN1)),Subst(Int(Power(Subtract(C1,Times(Power(x,C4),Power(a,CN1))),CN1D2),x),x,Power(Plus(a,Times(b,Sqr(x))),C1D4)),x),And(FreeQ(List(a,b),x),NegQ(a)))),
IIntegrate(235,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D3),x_Symbol),
    Condition(Dist(Times(C3,Sqrt(Times(b,Sqr(x))),Power(Times(C2,b,x),CN1)),Subst(Int(Times(x,Power(Plus(Negate(a),Power(x,C3)),CN1D2)),x),x,Power(Plus(a,Times(b,Sqr(x))),C1D3)),x),FreeQ(List(a,b),x))),
IIntegrate(236,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-2L,3L)),x_Symbol),
    Condition(Dist(Times(C3,Sqrt(Times(b,Sqr(x))),Power(Times(C2,b,x),CN1)),Subst(Int(Power(Plus(Negate(a),Power(x,C3)),CN1D2),x),x,Power(Plus(a,Times(b,Sqr(x))),C1D3)),x),FreeQ(List(a,b),x))),
IIntegrate(237,Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),QQ(-3L,4L)),x_Symbol),
    Condition(Dist(Times(Power(x,C3),Power(Plus(C1,Times(a,Power(Times(b,Power(x,C4)),CN1))),QQ(3L,4L)),Power(Plus(a,Times(b,Power(x,C4))),QQ(-3L,4L))),Int(Power(Times(Power(x,C3),Power(Plus(C1,Times(a,Power(Times(b,Power(x,C4)),CN1))),QQ(3L,4L))),CN1),x),x),FreeQ(List(a,b),x))),
IIntegrate(238,Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),QQ(-1L,6L)),x_Symbol),
    Condition(Subtract(Simp(Times(C3,x,Power(Times(C2,Power(Plus(a,Times(b,Sqr(x))),QQ(1L,6L))),CN1)),x),Dist(Times(C1D2,a),Int(Power(Plus(a,Times(b,Sqr(x))),QQ(-7L,6L)),x),x)),FreeQ(List(a,b),x))),
IIntegrate(239,Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),CN1D3),x_Symbol),
    Condition(Subtract(Simp(Times(ArcTan(Times(Plus(C1,Times(C2,Rt(b,C3),x,Power(Plus(a,Times(b,Power(x,C3))),CN1D3))),C1DSqrt3)),Power(Times(CSqrt3,Rt(b,C3)),CN1)),x),Simp(Times(Log(Subtract(Power(Plus(a,Times(b,Power(x,C3))),C1D3),Times(Rt(b,C3),x))),Power(Times(C2,Rt(b,C3)),CN1)),x)),FreeQ(List(a,b),x))),
IIntegrate(240,Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Dist(Power(a,Plus(p,Power(n,CN1))),Subst(Int(Power(Power(Subtract(C1,Times(b,Power(x,n))),Plus(p,Power(n,CN1),C1)),CN1),x),x,Times(x,Power(Power(Plus(a,Times(b,Power(x,n))),Power(n,CN1)),CN1))),x),And(FreeQ(List(a,b),x),IGtQ(n,C0),LtQ(CN1,p,C0),NeQ(p,Negate(C1D2)),IntegerQ(Plus(p,Power(n,CN1)))))),
IIntegrate(241,Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Dist(Times(Power(Times(a,Power(Plus(a,Times(b,Power(x,n))),CN1)),Plus(p,Power(n,CN1))),Power(Plus(a,Times(b,Power(x,n))),Plus(p,Power(n,CN1)))),Subst(Int(Power(Power(Subtract(C1,Times(b,Power(x,n))),Plus(p,Power(n,CN1),C1)),CN1),x),x,Times(x,Power(Power(Plus(a,Times(b,Power(x,n))),Power(n,CN1)),CN1))),x),And(FreeQ(List(a,b),x),IGtQ(n,C0),LtQ(CN1,p,C0),NeQ(p,Negate(C1D2)),LtQ(Denominator(Plus(p,Power(n,CN1))),Denominator(p))))),
IIntegrate(242,Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,Power(Power(x,n),CN1))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,p),x),ILtQ(n,C0)))),
IIntegrate(243,Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(With(List(Set(k,Denominator(n))),Dist(k,Subst(Int(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Power(x,Times(k,n)))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,p),x),FractionQ(n)))),
IIntegrate(244,Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,n),x),IGtQ(p,C0)))),
IIntegrate(245,Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Simp(Times(Power(a,p),x,Hypergeometric2F1(Negate(p),Power(n,CN1),Plus(Power(n,CN1),C1),Times(CN1,b,Power(x,n),Power(a,CN1)))),x),And(FreeQ(List(a,b,n,p),x),Not(IGtQ(p,C0)),Not(IntegerQ(Power(n,CN1))),Not(ILtQ(Simplify(Plus(Power(n,CN1),p)),C0)),Or(IntegerQ(p),GtQ(a,C0))))),
IIntegrate(246,Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Dist(Times(Power(a,IntPart(p)),Power(Plus(a,Times(b,Power(x,n))),FracPart(p)),Power(Power(Plus(C1,Times(b,Power(x,n),Power(a,CN1))),FracPart(p)),CN1)),Int(Power(Plus(C1,Times(b,Power(x,n),Power(a,CN1))),p),x),x),And(FreeQ(List(a,b,n,p),x),Not(IGtQ(p,C0)),Not(IntegerQ(Power(n,CN1))),Not(ILtQ(Simplify(Plus(Power(n,CN1),p)),C0)),Not(Or(IntegerQ(p),GtQ(a,C0)))))),
IIntegrate(247,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_))),p_),x_Symbol),
    Condition(Dist(Power(Coefficient(v,x,C1),CN1),Subst(Int(Power(Plus(a,Times(b,Power(x,n))),p),x),x,v),x),And(FreeQ(List(a,b,n,p),x),LinearQ(v,x),NeQ(v,x)))),
IIntegrate(248,Int(Times(Power(Plus($p("a1",true),Times($p("b1",true),Power(x_,n_))),p_DEFAULT),Power(Plus($p("a2",true),Times($p("b2",true),Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,Times(C2,n)))),p),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),Or(IntegerQ(p),And(GtQ($s("a1"),C0),GtQ($s("a2"),C0)))))),
IIntegrate(249,Int(Times(Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_DEFAULT))),p_DEFAULT),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),p),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),p),Power(Plus(Times(C2,n,p),C1),CN1)),x),Dist(Times(C2,$s("a1"),$s("a2"),n,p,Power(Plus(Times(C2,n,p),C1),CN1)),Int(Times(Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),Subtract(p,C1)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),Subtract(p,C1))),x),x)),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2")),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),IGtQ(Times(C2,n),C0),GtQ(p,C0),Or(IntegerQ(Times(C2,p)),Less(Denominator(Plus(p,Power(n,CN1))),Denominator(p)))))),
IIntegrate(250,Int(Times(Power(Plus($p("a1"),Times($p("b1",true),Power(x_,n_DEFAULT))),p_),Power(Plus($p("a2"),Times($p("b2",true),Power(x_,n_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(x,Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),Plus(p,C1)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),Plus(p,C1)),Power(Times(C2,$s("a1"),$s("a2"),n,Plus(p,C1)),CN1)),x)),Dist(Times(Plus(Times(C2,n,Plus(p,C1)),C1),Power(Times(C2,$s("a1"),$s("a2"),n,Plus(p,C1)),CN1)),Int(Times(Power(Plus($s("a1"),Times($s("b1"),Power(x,n))),Plus(p,C1)),Power(Plus($s("a2"),Times($s("b2"),Power(x,n))),Plus(p,C1))),x),x)),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2")),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),IGtQ(Times(C2,n),C0),LtQ(p,CN1),Or(IntegerQ(Times(C2,p)),Less(Denominator(Plus(p,Power(n,CN1))),Denominator(p))))))
  );
}
