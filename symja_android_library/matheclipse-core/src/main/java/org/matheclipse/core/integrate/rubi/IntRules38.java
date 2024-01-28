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
class IntRules38 { 
  public static IAST RULES = List( 
IIntegrate(761,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(b,Power(a,CN1)),C4))),Simp(Times(Plus(C1,Times(Sqr(q),Sqr(x))),Sqrt(Times(Plus(a,Times(b,Power(x,C4))),Power(Times(a,Sqr(Plus(C1,Times(Sqr(q),Sqr(x))))),CN1))),Power(Times(C2,q,Sqrt(Plus(a,Times(b,Power(x,C4))))),CN1),EllipticF(Times(C2,ArcTan(Times(q,x))),C1D2)),x)),And(FreeQ(list(a,b),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(762,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2),x_Symbol),
    Condition(Simp(Times(Power(Times(Sqrt(a),Rt(Times(CN1,b,Power(a,CN1)),C4)),CN1),EllipticF(ArcSin(Times(Rt(Times(CN1,b,Power(a,CN1)),C4),x)),CN1)),x),And(FreeQ(list(a,b),x),NegQ(Times(b,Power(a,CN1))),GtQ(a,C0)))),
IIntegrate(763,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,a,b),C2))),Condition(Simp(Times(Sqrt(Plus(Negate(a),Times(q,Sqr(x)))),Sqrt(Times(Plus(a,Times(q,Sqr(x))),Power(q,CN1))),Power(Times(CSqrt2,Sqrt(Negate(a)),Sqrt(Plus(a,Times(b,Power(x,C4))))),CN1),EllipticF(ArcSin(Times(x,Power(Times(Plus(a,Times(q,Sqr(x))),Power(Times(C2,q),CN1)),CN1D2))),C1D2)),x),IntegerQ(q))),And(FreeQ(list(a,b),x),LtQ(a,C0),GtQ(b,C0)))),
IIntegrate(764,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,a,b),C2))),Simp(Times(Sqrt(Times(Subtract(a,Times(q,Sqr(x))),Power(Plus(a,Times(q,Sqr(x))),CN1))),Sqrt(Times(Plus(a,Times(q,Sqr(x))),Power(q,CN1))),Power(Times(CSqrt2,Sqrt(Plus(a,Times(b,Power(x,C4)))),Sqrt(Times(a,Power(Plus(a,Times(q,Sqr(x))),CN1)))),CN1),EllipticF(ArcSin(Times(x,Power(Times(Plus(a,Times(q,Sqr(x))),Power(Times(C2,q),CN1)),CN1D2))),C1D2)),x)),And(FreeQ(list(a,b),x),LtQ(a,C0),GtQ(b,C0)))),
IIntegrate(765,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),CN1D2),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(C1,Times(b,Power(x,C4),Power(a,CN1)))),Power(Plus(a,Times(b,Power(x,C4))),CN1D2)),Integrate(Power(Plus(C1,Times(b,Power(x,C4),Power(a,CN1))),CN1D2),x)),x),And(FreeQ(list(a,b),x),NegQ(Times(b,Power(a,CN1))),Not(GtQ(a,C0))))),
IIntegrate(766,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C6))),CN1D2),x_Symbol),
    Condition(With(list(Set(r,$($s("§numer"),Rt(Times(b,Power(a,CN1)),C3))),Set(s,$($s("§denom"),Rt(Times(b,Power(a,CN1)),C3)))),Simp(Times(x,Plus(s,Times(r,Sqr(x))),Sqrt(Times(Plus(Sqr(s),Times(CN1,r,s,Sqr(x)),Times(Sqr(r),Power(x,C4))),Power(Plus(s,Times(Plus(C1,CSqrt3),r,Sqr(x))),CN2))),Power(Times(C2,Power(C3,C1D4),s,Sqrt(Plus(a,Times(b,Power(x,C6)))),Sqrt(Times(r,Sqr(x),Plus(s,Times(r,Sqr(x))),Power(Plus(s,Times(Plus(C1,CSqrt3),r,Sqr(x))),CN2)))),CN1),EllipticF(ArcCos(Times(Plus(s,Times(Subtract(C1,CSqrt3),r,Sqr(x))),Power(Plus(s,Times(Plus(C1,CSqrt3),r,Sqr(x))),CN1))),Times(C1D4,Plus(C2,CSqrt3)))),x)),FreeQ(list(a,b),x))),
IIntegrate(767,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C8))),CN1D2),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Subtract(C1,Times(Rt(Times(b,Power(a,CN1)),C4),Sqr(x))),Power(Plus(a,Times(b,Power(x,C8))),CN1D2)),x)),x),Simp(Star(C1D2,Integrate(Times(Plus(C1,Times(Rt(Times(b,Power(a,CN1)),C4),Sqr(x))),Power(Plus(a,Times(b,Power(x,C8))),CN1D2)),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(768,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C4))),QQ(-3L,4L)),x_Symbol),
    Condition(Simp(Star(Times(Power(x,C3),Power(Plus(C1,Times(a,Power(Times(b,Power(x,C4)),CN1))),QQ(3L,4L)),Power(Plus(a,Times(b,Power(x,C4))),QQ(-3L,4L))),Integrate(Power(Times(Power(x,C3),Power(Plus(C1,Times(a,Power(Times(b,Power(x,C4)),CN1))),QQ(3L,4L))),CN1),x)),x),FreeQ(list(a,b),x))),
IIntegrate(769,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),CN1D3),x_Symbol),
    Condition(Subtract(Simp(Times(ArcTan(Times(Plus(C1,Times(C2,Rt(b,C3),x,Power(Plus(a,Times(b,Power(x,C3))),CN1D3))),C1DSqrt3)),Power(Times(CSqrt3,Rt(b,C3)),CN1)),x),Simp(Times(Log(Subtract(Power(Plus(a,Times(b,Power(x,C3))),C1D3),Times(Rt(b,C3),x))),Power(Times(C2,Rt(b,C3)),CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(770,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Simp(Star(Power(a,Plus(p,Power(n,CN1))),Subst(Integrate(Power(Power(Subtract(C1,Times(b,Power(x,n))),Plus(p,Power(n,CN1),C1)),CN1),x),x,Times(x,Power(Power(Plus(a,Times(b,Power(x,n))),Power(n,CN1)),CN1)))),x),And(FreeQ(list(a,b),x),IGtQ(n,C0),LtQ(CN1,p,C0),NeQ(p,Negate(C1D2)),IntegerQ(Plus(p,Power(n,CN1)))))),
IIntegrate(771,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(a,Power(Plus(a,Times(b,Power(x,n))),CN1)),Plus(p,Power(n,CN1))),Power(Plus(a,Times(b,Power(x,n))),Plus(p,Power(n,CN1)))),Subst(Integrate(Power(Power(Subtract(C1,Times(b,Power(x,n))),Plus(p,Power(n,CN1),C1)),CN1),x),x,Times(x,Power(Power(Plus(a,Times(b,Power(x,n))),Power(n,CN1)),CN1)))),x),And(FreeQ(list(a,b),x),IGtQ(n,C0),LtQ(CN1,p,C0),NeQ(p,Negate(C1D2)),LtQ(Denominator(Plus(p,Power(n,CN1))),Denominator(p))))),
IIntegrate(772,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Integrate(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p)),x),And(FreeQ(list(a,b),x),ILtQ(n,C0),IntegerQ(p)))),
IIntegrate(773,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Power(Power(x,n),CN1))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(list(a,b,p),x),ILtQ(n,C0),Not(IntegerQ(p))))),
IIntegrate(774,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Power(x,Times(k,n)))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(list(a,b,p),x),FractionQ(n)))),
IIntegrate(775,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(list(a,b,n),x),IGtQ(p,C0)))),
IIntegrate(776,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Simp(Star(Times(x,Power(Plus(a,Times(b,Power(x,n))),p),Power(Times(Power(x,n),Power(Plus(a,Times(b,Power(x,n))),CN1)),p),Power(n,CN1)),Subst(Integrate(Power(Times(Power(x,Plus(p,C1)),Subtract(C1,Times(b,x))),CN1),x),x,Times(Power(x,n),Power(Plus(a,Times(b,Power(x,n))),CN1)))),x),And(FreeQ(List(a,b,n,p),x),EqQ(Plus(Power(n,CN1),p),C0)))),
IIntegrate(777,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(Times(n,Plus(p,C1)),C1),Power(Times(a,n,Plus(p,C1)),CN1)),Integrate(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),x)),x)),And(FreeQ(List(a,b,n,p),x),ILtQ(Simplify(Plus(Power(n,CN1),p,C1)),C0),NeQ(p,CN1)))),
IIntegrate(778,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Simp(Times(Power(a,p),x,Hypergeometric2F1(Negate(p),Power(n,CN1),Plus(Power(n,CN1),C1),Times(CN1,b,Power(x,n),Power(a,CN1)))),x),And(FreeQ(List(a,b,n,p),x),Not(IGtQ(p,C0)),Not(IntegerQ(Power(n,CN1))),Not(ILtQ(Simplify(Plus(Power(n,CN1),p)),C0)),Or(IntegerQ(p),GtQ(a,C0))))),
IIntegrate(779,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(p)),Power(Plus(a,Times(b,Power(x,n))),FracPart(p)),Power(Power(Plus(C1,Times(b,Power(x,n),Power(a,CN1))),FracPart(p)),CN1)),Integrate(Power(Plus(C1,Times(b,Power(x,n),Power(a,CN1))),p),x)),x),And(FreeQ(List(a,b,n,p),x),Not(IGtQ(p,C0)),Not(IntegerQ(Power(n,CN1))),Not(ILtQ(Simplify(Plus(Power(n,CN1),p)),C0)),Not(Or(IntegerQ(p),GtQ(a,C0)))))),
IIntegrate(780,Integrate(Times(Power(Plus($p("a1",true),Times($p("b1",true),Power(x_,n_))),p_DEFAULT),Power(Plus($p("a2",true),Times($p("b2",true),Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Times($s("a1"),$s("a2")),Times($s("b1"),$s("b2"),Power(x,Times(C2,n)))),p),x),And(FreeQ(List($s("a1"),$s("b1"),$s("a2"),$s("b2"),n,p),x),EqQ(Plus(Times($s("a2"),$s("b1")),Times($s("a1"),$s("b2"))),C0),Or(IntegerQ(p),And(GtQ($s("a1"),C0),GtQ($s("a2"),C0))))))
  );
}
