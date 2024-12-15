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
class IntRules2 { 
  public static IAST RULES = List( 
IIntegrate(41,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),m),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Or(IntegerQ(m),And(GtQ(a,C0),GtQ(c,C0)))))),
IIntegrate(42,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,x)),FracPart(m)),Power(Plus(c,Times(d,x)),FracPart(m)),Power(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),FracPart(m)),CN1)),Integrate(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),m),x),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Not(IntegerQ(Times(C2,m)))))),
IIntegrate(43,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IGtQ(m,C0),Or(Not(IntegerQ(n)),And(EqQ(c,C0),LeQ(Plus(Times(C7,m),Times(C4,n),C4),C0)),LtQ(Plus(Times(C9,m),Times(C5,Plus(n,C1))),C0),GtQ(Plus(m,n,C2),C0))))),
IIntegrate(44,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),ILtQ(m,C0),IntegerQ(n),Not(And(IGtQ(n,C0),LtQ(Plus(m,n,C2),C0)))))),
IIntegrate(45,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),x),Dist(Times(d,Simplify(Plus(m,n,C2)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Simplify(Plus(m,C1))),Power(Plus(c,Times(d,x)),n)),x),x)),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),ILtQ(Simplify(Plus(m,n,C2)),C0),NeQ(m,CN1),Not(And(LtQ(m,CN1),LtQ(n,CN1),Or(EqQ(a,C0),And(NeQ(c,C0),LtQ(Subtract(m,n),C0),IntegerQ(n))))),Or(SumSimplerQ(m,C1),Not(SumSimplerQ(n,C1)))))),
IIntegrate(46,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-9L,4L)),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(CN4,Power(Times(C5,b,Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),C1D4)),CN1)),x),Dist(Times(d,Power(Times(C5,b),CN1)),Integrate(Power(Times(Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),QQ(5L,4L))),CN1),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),NegQ(Times(Sqr(a),Sqr(b)))))),
IIntegrate(47,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,C1)),CN1)),x),Dist(Times(d,n,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),GtQ(n,C0),LtQ(m,CN1),Not(And(IntegerQ(n),Not(IntegerQ(m)))),Not(And(ILeQ(Plus(m,n,C2),C0),Or(FractionQ(m),GeQ(Plus(Times(C2,n),m,C1),C0)))),IntLinearQ(a,b,c,d,m,n,x)))),
IIntegrate(48,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-5L,4L)),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D4)),x_Symbol),
    Condition(Plus(Simp(Times(CN2,Power(Times(b,Power(Plus(a,Times(b,x)),C1D4),Power(Plus(c,Times(d,x)),C1D4)),CN1)),x),Dist(c,Integrate(Power(Times(Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),QQ(5L,4L))),CN1),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),NegQ(Times(Sqr(a),Sqr(b)))))),
IIntegrate(49,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,n,C1)),CN1)),x),Dist(Times(C2,c,n,Power(Plus(m,n,C1),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),IGtQ(Plus(m,C1D2),C0),IGtQ(Plus(n,C1D2),C0),LtQ(m,n)))),
IIntegrate(50,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,n,C1)),CN1)),x),Dist(Times(n,Subtract(Times(b,c),Times(a,d)),Power(Times(b,Plus(m,n,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),GtQ(n,C0),NeQ(Plus(m,n,C1),C0),Not(And(IGtQ(m,C0),Or(Not(IntegerQ(n)),And(GtQ(m,C0),LtQ(Subtract(m,n),C0))))),Not(ILtQ(Plus(m,n,C2),C0)),IntLinearQ(a,b,c,d,m,n,x)))),
IIntegrate(51,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),x),Dist(Times(d,Plus(m,n,C2),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n)),x),x)),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),LtQ(m,CN1),Not(And(LtQ(n,CN1),Or(EqQ(a,C0),And(NeQ(c,C0),LtQ(Subtract(m,n),C0),IntegerQ(n))))),IntLinearQ(a,b,c,d,m,n,x)))),
IIntegrate(52,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),CN1D2),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Simp(Times(ArcCosh(Times(b,x,Power(a,CN1))),Power(b,CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(a,c),C0),EqQ(Subtract(b,d),C0),GtQ(a,C0)))),
IIntegrate(53,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),CN1D2),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Integrate(Power(Subtract(Subtract(Times(a,c),Times(b,Subtract(a,c),x)),Times(Sqr(b),Sqr(x))),CN1D2),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(b,d),C0),GtQ(Plus(a,c),C0)))),
IIntegrate(54,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1D2),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Dist(Times(C2,Power(b,CN1D2)),Subst(Integrate(Power(Plus(Times(b,c),Times(CN1,a,d),Times(d,Sqr(x))),CN1D2),x),x,Sqrt(Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,d),x),GtQ(Subtract(Times(b,c),Times(a,d)),C0),GtQ(b,C0)))),
IIntegrate(55,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D3)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)),C3))),Plus(Negate(Simp(Times(Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(Times(C2,b,q),CN1)),x)),Dist(Times(C3,Power(Times(C2,b),CN1)),Subst(Integrate(Power(Plus(Sqr(q),Times(q,x),Sqr(x)),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3)),x),Negate(Dist(Times(C3,Power(Times(C2,b,q),CN1)),Subst(Integrate(Power(Subtract(q,x),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3)),x)))),And(FreeQ(List(a,b,c,d),x),PosQ(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)))))),
IIntegrate(56,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D3)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,Subtract(Times(b,c),Times(a,d)),Power(b,CN1)),C3))),Plus(Simp(Times(Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(Times(C2,b,q),CN1)),x),Dist(Times(C3,Power(Times(C2,b),CN1)),Subst(Integrate(Power(Plus(Sqr(q),Times(CN1,q,x),Sqr(x)),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3)),x),Negate(Dist(Times(C3,Power(Times(C2,b,q),CN1)),Subst(Integrate(Power(Plus(q,x),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3)),x)))),And(FreeQ(List(a,b,c,d),x),NegQ(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)))))),
IIntegrate(57,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),QQ(-2L,3L))),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)),C3))),Plus(Negate(Simp(Times(Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(Times(C2,b,Sqr(q)),CN1)),x)),Negate(Dist(Times(C3,Power(Times(C2,b,q),CN1)),Subst(Integrate(Power(Plus(Sqr(q),Times(q,x),Sqr(x)),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3)),x)),Negate(Dist(Times(C3,Power(Times(C2,b,Sqr(q)),CN1)),Subst(Integrate(Power(Subtract(q,x),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3)),x)))),And(FreeQ(List(a,b,c,d),x),PosQ(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)))))),
IIntegrate(58,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),QQ(-2L,3L))),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,Subtract(Times(b,c),Times(a,d)),Power(b,CN1)),C3))),Plus(Negate(Simp(Times(Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(Times(C2,b,Sqr(q)),CN1)),x)),Dist(Times(C3,Power(Times(C2,b,q),CN1)),Subst(Integrate(Power(Plus(Sqr(q),Times(CN1,q,x),Sqr(x)),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3)),x),Dist(Times(C3,Power(Times(C2,b,Sqr(q)),CN1)),Subst(Integrate(Power(Plus(q,x),CN1),x),x,Power(Plus(c,Times(d,x)),C1D3)),x))),And(FreeQ(List(a,b,c,d),x),NegQ(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1)))))),
IIntegrate(59,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1D3),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),QQ(-2L,3L))),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(d,Power(b,CN1)),C3))),Plus(Negate(Simp(Times(CSqrt3,q,ArcTan(Plus(Times(C2,q,Power(Plus(a,Times(b,x)),C1D3),Power(Times(CSqrt3,Power(Plus(c,Times(d,x)),C1D3)),CN1)),C1DSqrt3)),Power(d,CN1)),x)),Negate(Simp(Times(C3,q,Log(Subtract(Times(q,Power(Plus(a,Times(b,x)),C1D3),Power(Plus(c,Times(d,x)),CN1D3)),C1)),Power(Times(C2,d),CN1)),x)),Negate(Simp(Times(q,Log(Plus(c,Times(d,x))),Power(Times(C2,d),CN1)),x)))),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),PosQ(Times(d,Power(b,CN1)))))),
IIntegrate(60,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1D3),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),QQ(-2L,3L))),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,d,Power(b,CN1)),C3))),Plus(Simp(Times(CSqrt3,q,ArcTan(Subtract(C1DSqrt3,Times(C2,q,Power(Plus(a,Times(b,x)),C1D3),Power(Times(CSqrt3,Power(Plus(c,Times(d,x)),C1D3)),CN1)))),Power(d,CN1)),x),Simp(Times(C3,q,Log(Plus(Times(q,Power(Plus(a,Times(b,x)),C1D3),Power(Plus(c,Times(d,x)),CN1D3)),C1)),Power(Times(C2,d),CN1)),x),Simp(Times(q,Log(Plus(c,Times(d,x))),Power(Times(C2,d),CN1)),x))),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),NegQ(Times(d,Power(b,CN1))))))
  );
}
