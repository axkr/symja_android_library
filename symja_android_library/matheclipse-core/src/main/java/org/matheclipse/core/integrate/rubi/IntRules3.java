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
class IntRules3 { 
  public static IAST RULES = List( 
IIntegrate(61,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),x),Simp(Star(Times(d,Plus(m,n,C2),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n)),x)),x)),And(FreeQ(List(a,b,c,d,n),x),LtQ(m,CN1),Not(And(LtQ(n,CN1),Or(EqQ(a,C0),And(NeQ(c,C0),LtQ(Subtract(m,n),C0),IntegerQ(n))))),IntLinearQ(a,b,c,d,m,n,x)))),
IIntegrate(62,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1D2),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Integrate(Power(Subtract(Subtract(Times(a,c),Times(b,Subtract(a,c),x)),Times(Sqr(b),Sqr(x))),CN1D2),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(b,d),C0),GtQ(Plus(a,c),C0)))),
IIntegrate(63,Integrate(Times(Power(Times(b_DEFAULT,x_),CN1D2),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(b,CN1)),Subst(Integrate(Power(Plus(c,Times(d,Sqr(x),Power(b,CN1))),CN1D2),x),x,Sqrt(Times(b,x)))),x),And(FreeQ(list(b,c,d),x),GtQ(c,C0)))),
IIntegrate(64,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),CN1D2),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(b,CN1)),Subst(Integrate(Power(Plus(c,Times(CN1,a,d,Power(b,CN1)),Times(d,Sqr(x),Power(b,CN1))),CN1D2),x),x,Sqrt(Plus(a,Times(b,x))))),x),And(FreeQ(List(a,b,c,d),x),GtQ(Subtract(c,Times(a,d,Power(b,CN1))),C0),Or(Not(GtQ(Subtract(a,Times(c,b,Power(d,CN1))),C0)),PosQ(b))))),
IIntegrate(65,Integrate(Times(Power(Times(b_DEFAULT,x_),CN1D2),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Simp(Star(C2,Subst(Integrate(Power(Subtract(b,Times(d,Sqr(x))),CN1),x),x,Times(Sqrt(Times(b,x)),Power(Plus(c,Times(d,x)),CN1D2)))),x),And(FreeQ(list(b,c,d),x),Not(GtQ(c,C0))))),
IIntegrate(66,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),CN1D2),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Simp(Star(C2,Subst(Integrate(Power(Subtract(b,Times(d,Sqr(x))),CN1),x),x,Times(Sqrt(Plus(a,Times(b,x))),Power(Plus(c,Times(d,x)),CN1D2)))),x),And(FreeQ(List(a,b,c,d),x),Not(GtQ(Subtract(c,Times(a,d,Power(b,CN1))),C0))))),
IIntegrate(67,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D3)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)),C3))),Plus(Simp(Times(CN1,Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(Times(C2,b,q),CN1)),x),Simp(Star(Times(C3,Power(Times(C2,b),CN1)),Subst(Integrate(Power(Plus(Sqr(q),Times(q,x),Sqr(x)),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3))),x),Negate(Simp(Star(Times(C3,Power(Times(C2,b,q),CN1)),Subst(Integrate(Power(Subtract(q,x),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3))),x)))),And(FreeQ(List(a,b,c,d),x),PosQ(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)))))),
IIntegrate(68,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D3)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,Subtract(Times(b,c),Times(a,d)),Power(b,CN1)),C3))),Plus(Simp(Times(Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(Times(C2,b,q),CN1)),x),Simp(Star(Times(C3,Power(Times(C2,b),CN1)),Subst(Integrate(Power(Plus(Sqr(q),Times(CN1,q,x),Sqr(x)),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3))),x),Negate(Simp(Star(Times(C3,Power(Times(C2,b,q),CN1)),Subst(Integrate(Power(Plus(q,x),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3))),x)))),And(FreeQ(List(a,b,c,d),x),NegQ(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)))))),
IIntegrate(69,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),QQ(-2L,3L))),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)),C3))),Plus(Simp(Times(CN1,Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(Times(C2,b,Sqr(q)),CN1)),x),Negate(Simp(Star(Times(C3,Power(Times(C2,b,q),CN1)),Subst(Integrate(Power(Plus(Sqr(q),Times(q,x),Sqr(x)),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3))),x)),Negate(Simp(Star(Times(C3,Power(Times(C2,b,Sqr(q)),CN1)),Subst(Integrate(Power(Subtract(q,x),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3))),x)))),And(FreeQ(List(a,b,c,d),x),PosQ(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)))))),
IIntegrate(70,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),QQ(-2L,3L))),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,Subtract(Times(b,c),Times(a,d)),Power(b,CN1)),C3))),Plus(Simp(Times(CN1,Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(Times(C2,b,Sqr(q)),CN1)),x),Simp(Star(Times(C3,Power(Times(C2,b,q),CN1)),Subst(Integrate(Power(Plus(Sqr(q),Times(CN1,q,x),Sqr(x)),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3))),x),Simp(Star(Times(C3,Power(Times(C2,b,Sqr(q)),CN1)),Subst(Integrate(Power(Plus(q,x),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3))),x))),And(FreeQ(List(a,b,c,d),x),NegQ(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)))))),
IIntegrate(71,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1D3),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),QQ(-2L,3L))),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(d,Power(b,CN1)),C3))),Plus(Simp(Times(CN1,CSqrt3,q,Power(d,CN1),ArcTan(Plus(Times(C2,q,Power(Plus(a,Times(b,x)),C1D3),Power(Times(CSqrt3,Power(Plus(c,Times(d,x)),C1D3)),CN1)),C1DSqrt3))),x),Negate(Simp(Times(C3,q,Power(Times(C2,d),CN1),Log(Subtract(Times(q,Power(Plus(a,Times(b,x)),C1D3),Power(Plus(c,Times(d,x)),CN1D3)),C1))),x)),Negate(Simp(Times(q,Power(Times(C2,d),CN1),Log(Plus(c,Times(d,x)))),x)))),And(FreeQ(List(a,b,c,d),x),PosQ(Times(d,Power(b,CN1)))))),
IIntegrate(72,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1D3),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),QQ(-2L,3L))),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,d,Power(b,CN1)),C3))),Plus(Simp(Times(CSqrt3,q,Power(d,CN1),ArcTan(Subtract(C1DSqrt3,Times(C2,q,Power(Plus(a,Times(b,x)),C1D3),Power(Times(CSqrt3,Power(Plus(c,Times(d,x)),C1D3)),CN1))))),x),Simp(Times(C3,q,Power(Times(C2,d),CN1),Log(Plus(Times(q,Power(Plus(a,Times(b,x)),C1D3),Power(Plus(c,Times(d,x)),CN1D3)),C1))),x),Simp(Times(q,Power(Times(C2,d),CN1),Log(Plus(c,Times(d,x)))),x))),And(FreeQ(List(a,b,c,d),x),NegQ(Times(d,Power(b,CN1)))))),
IIntegrate(73,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(With(list(Set(p,Denominator(m))),Simp(Star(Times(p,Power(b,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(p,Plus(m,C1)),C1)),Power(Plus(c,Times(CN1,a,d,Power(b,CN1)),Times(d,Power(x,p),Power(b,CN1))),n)),x),x,Power(Plus(a,Times(b,x)),Power(p,CN1)))),x)),And(FreeQ(List(a,b,c,d),x),LtQ(CN1,m,C0),LeQ(CN1,n,C0),LeQ(Denominator(n),Denominator(m)),IntLinearQ(a,b,c,d,m,n,x)))),
IIntegrate(74,Integrate(Times(Power(Times(b_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Times(Power(c,n),Power(Times(b,x),Plus(m,C1)),Power(Times(b,Plus(m,C1)),CN1),Hypergeometric2F1(Negate(n),Plus(m,C1),Plus(m,C2),Times(CN1,d,x,Power(c,CN1)))),x),And(FreeQ(List(b,c,d,m,n),x),Not(IntegerQ(m)),Or(IntegerQ(n),And(GtQ(c,C0),Not(And(EqQ(n,Negate(C1D2)),EqQ(Subtract(Sqr(c),Sqr(d)),C0),GtQ(Times(CN1,d,Power(Times(b,c),CN1)),C0)))))))),
IIntegrate(75,Integrate(Times(Power(Times(b_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Times(Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(d,Plus(n,C1),Power(Times(CN1,d,Power(Times(b,c),CN1)),m)),CN1),Hypergeometric2F1(Negate(m),Plus(n,C1),Plus(n,C2),Plus(C1,Times(d,x,Power(c,CN1))))),x),And(FreeQ(List(b,c,d,m,n),x),Not(IntegerQ(n)),Or(IntegerQ(m),GtQ(Times(CN1,d,Power(Times(b,c),CN1)),C0))))),
IIntegrate(76,Integrate(Times(Power(Times(b_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,IntPart(n)),Power(Plus(c,Times(d,x)),FracPart(n)),Power(Power(Plus(C1,Times(d,x,Power(c,CN1))),FracPart(n)),CN1)),Integrate(Times(Power(Times(b,x),m),Power(Plus(C1,Times(d,x,Power(c,CN1))),n)),x)),x),And(FreeQ(List(b,c,d,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n)),Not(GtQ(c,C0)),Not(GtQ(Times(CN1,d,Power(Times(b,c),CN1)),C0)),Or(And(RationalQ(m),Not(And(EqQ(n,Negate(C1D2)),EqQ(Subtract(Sqr(c),Sqr(d)),C0)))),Not(RationalQ(n)))))),
IIntegrate(77,Integrate(Times(Power(Times(b_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(CN1,b,c,Power(d,CN1)),IntPart(m)),Power(Times(b,x),FracPart(m)),Power(Power(Times(CN1,d,x,Power(c,CN1)),FracPart(m)),CN1)),Integrate(Times(Power(Times(CN1,d,x,Power(c,CN1)),m),Power(Plus(c,Times(d,x)),n)),x)),x),And(FreeQ(List(b,c,d,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n)),Not(GtQ(c,C0)),Not(GtQ(Times(CN1,d,Power(Times(b,c),CN1)),C0))))),
IIntegrate(78,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Times(Power(Subtract(Times(b,c),Times(a,d)),n),Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Times(Power(b,Plus(n,C1)),Plus(m,C1)),CN1),Hypergeometric2F1(Negate(n),Plus(m,C1),Plus(m,C2),Times(CN1,d,Plus(a,Times(b,x)),Power(Subtract(Times(b,c),Times(a,d)),CN1)))),x),And(FreeQ(List(a,b,c,d,m),x),Not(IntegerQ(m)),IntegerQ(n)))),
IIntegrate(79,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Times(b,Plus(m,C1),Power(Times(b,Power(Subtract(Times(b,c),Times(a,d)),CN1)),n)),CN1),Hypergeometric2F1(Negate(n),Plus(m,C1),Plus(m,C2),Times(CN1,d,Plus(a,Times(b,x)),Power(Subtract(Times(b,c),Times(a,d)),CN1)))),x),And(FreeQ(List(a,b,c,d,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n)),GtQ(Times(b,Power(Subtract(Times(b,c),Times(a,d)),CN1)),C0),Or(RationalQ(m),Not(And(RationalQ(n),GtQ(Times(CN1,d,Power(Subtract(Times(b,c),Times(a,d)),CN1)),C0))))))),
IIntegrate(80,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(c,Times(d,x)),FracPart(n)),Power(Times(Power(Times(b,Power(Subtract(Times(b,c),Times(a,d)),CN1)),IntPart(n)),Power(Times(b,Plus(c,Times(d,x)),Power(Subtract(Times(b,c),Times(a,d)),CN1)),FracPart(n))),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Simp(Plus(Times(b,c,Power(Subtract(Times(b,c),Times(a,d)),CN1)),Times(b,d,x,Power(Subtract(Times(b,c),Times(a,d)),CN1))),x),n)),x)),x),And(FreeQ(List(a,b,c,d,m,n),x),Not(IntegerQ(m)),Not(IntegerQ(n)),Or(RationalQ(m),Not(SimplerQ(Plus(n,C1),Plus(m,C1)))))))
  );
}
