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
class IntRules25 { 
  public static IAST RULES = List( 
IIntegrate(501,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),CN1),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D3)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(C6,Sqr(b),Sqr(d),Power(c,CN2)),C3))),Plus(Simp(Times(CN1,CSqrt3,b,d,ArcTan(Plus(C1DSqrt3,Times(C2,b,Subtract(c,Times(d,x)),Power(Times(CSqrt3,c,q,Power(Plus(a,Times(b,Sqr(x))),C1D3)),CN1)))),Power(Times(Sqr(c),Sqr(q)),CN1)),x),Negate(Simp(Times(C3,b,d,Log(Plus(c,Times(d,x))),Power(Times(C2,Sqr(c),Sqr(q)),CN1)),x)),Simp(Times(C3,b,d,Log(Subtract(Subtract(Times(b,c),Times(b,d,x)),Times(c,q,Power(Plus(a,Times(b,Sqr(x))),C1D3)))),Power(Times(C2,Sqr(c),Sqr(q)),CN1)),x))),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Times(b,Sqr(c)),Times(C3,a,Sqr(d))),C0)))),
IIntegrate(502,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),CN1),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D3)),x_Symbol),
    Condition(Simp(Star(Power(a,C1D3),Integrate(Power(Times(Plus(c,Times(d,x)),Power(Subtract(C1,Times(C3,d,x,Power(c,CN1))),C1D3),Power(Plus(C1,Times(C3,d,x,Power(c,CN1))),C1D3)),CN1),x)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,Sqr(c)),Times(C9,a,Sqr(d))),C0),GtQ(a,C0)))),
IIntegrate(503,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),CN1),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D3)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),C1D3),Power(Plus(a,Times(b,Sqr(x))),CN1D3)),Integrate(Power(Times(Plus(c,Times(d,x)),Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),C1D3)),CN1),x)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,Sqr(c)),Times(C9,a,Sqr(d))),C0),Not(GtQ(a,C0))))),
IIntegrate(504,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),CN1),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Star(c,Integrate(Times(Power(Plus(a,Times(b,Sqr(x))),p),Power(Subtract(Sqr(c),Times(Sqr(d),Sqr(x))),CN1)),x)),x),Simp(Star(d,Integrate(Times(x,Power(Plus(a,Times(b,Sqr(x))),p),Power(Subtract(Sqr(c),Times(Sqr(d),Sqr(x))),CN1)),x)),x)),FreeQ(List(a,b,c,d,p),x))),
IIntegrate(505,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Sqr(x))),p),Power(Subtract(Times(c,Power(Subtract(Sqr(c),Times(Sqr(d),Sqr(x))),CN1)),Times(d,x,Power(Subtract(Sqr(c),Times(Sqr(d),Sqr(x))),CN1))),Negate(n)),x),x),And(FreeQ(List(a,b,c,d,p),x),ILtQ(n,CN1),PosQ(Times(a,Power(b,CN1)))))),
IIntegrate(506,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,a,Power(b,CN1)),C2))),Simp(Star(Times(CN1,Power(Plus(a,Times(b,Sqr(x))),p),Power(Power(Plus(c,Times(d,x)),CN1),Times(C2,p)),Power(Times(d,Power(Subtract(C1,Times(Subtract(c,Times(d,q)),Power(Plus(c,Times(d,x)),CN1))),p),Power(Subtract(C1,Times(Plus(c,Times(d,q)),Power(Plus(c,Times(d,x)),CN1))),p)),CN1)),Subst(Integrate(Times(Power(Subtract(C1,Times(Subtract(c,Times(d,q)),x)),p),Power(Subtract(C1,Times(Plus(c,Times(d,q)),x)),p),Power(Power(x,Plus(n,Times(C2,p),C2)),CN1)),x),x,Power(Plus(c,Times(d,x)),CN1))),x)),And(FreeQ(List(a,b,c,d,p),x),ILtQ(n,CN1),NegQ(Times(a,Power(b,CN1)))))),
IIntegrate(507,Integrate(Times(Sqrt(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(d,CN1)),Subst(Integrate(Times(Sqr(x),Power(Plus(Times(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),Power(d,CN2)),Times(CN1,C2,b,c,Sqr(x),Power(d,CN2)),Times(b,Power(x,C4),Power(d,CN2))),CN1D2)),x),x,Sqrt(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(508,Integrate(Times(Sqrt(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,b,Power(a,CN1)),C2))),Simp(Star(Times(CN2,Sqrt(Plus(c,Times(d,x))),Power(Times(Sqrt(a),q,Sqrt(Times(q,Plus(c,Times(d,x)),Power(Plus(d,Times(c,q)),CN1)))),CN1)),Subst(Integrate(Times(Sqrt(Subtract(C1,Times(C2,d,Sqr(x),Power(Plus(d,Times(c,q)),CN1)))),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Sqrt(Times(C1D2,Subtract(C1,Times(q,x)))))),x)),And(FreeQ(List(a,b,c,d),x),NegQ(Times(b,Power(a,CN1))),GtQ(a,C0)))),
IIntegrate(509,Integrate(Times(Sqrt(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1)))),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),Integrate(Times(Sqrt(Plus(c,Times(d,x))),Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),CN1D2)),x)),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(b,Power(a,CN1))),Not(GtQ(a,C0))))),
IIntegrate(510,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(d,CN1)),Subst(Integrate(Power(Plus(Times(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),Power(d,CN2)),Times(CN1,C2,b,c,Sqr(x),Power(d,CN2)),Times(b,Power(x,C4),Power(d,CN2))),CN1D2),x),x,Sqrt(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d),x),PosQ(Times(b,Power(a,CN1)))))),
IIntegrate(511,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,b,Power(a,CN1)),C2))),Simp(Star(Times(CN2,Sqrt(Times(q,Plus(c,Times(d,x)),Power(Plus(d,Times(c,q)),CN1))),Power(Times(Sqrt(a),q,Sqrt(Plus(c,Times(d,x)))),CN1)),Subst(Integrate(Power(Times(Sqrt(Subtract(C1,Times(C2,d,Sqr(x),Power(Plus(d,Times(c,q)),CN1)))),Sqrt(Subtract(C1,Sqr(x)))),CN1),x),x,Sqrt(Times(C1D2,Subtract(C1,Times(q,x)))))),x)),And(FreeQ(List(a,b,c,d),x),NegQ(Times(b,Power(a,CN1))),GtQ(a,C0)))),
IIntegrate(512,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1)))),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),Integrate(Power(Times(Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1))))),CN1),x)),x),And(FreeQ(List(a,b,c,d),x),NegQ(Times(b,Power(a,CN1))),Not(GtQ(a,C0))))),
IIntegrate(513,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Power(a,p),Integrate(Times(Power(Plus(c,Times(d,x)),n),Power(Plus(C1,Times(Rt(Times(CN1,b,Power(a,CN1)),C2),x)),p),Power(Subtract(C1,Times(Rt(Times(CN1,b,Power(a,CN1)),C2),x)),p)),x)),x),And(FreeQ(List(a,b,c,d,n,p),x),GtQ(a,C0),NegQ(Times(b,Power(a,CN1)))))),
IIntegrate(514,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(q,Rt(Times(CN1,a,Power(b,CN1)),C2))),Simp(Star(Times(Power(Plus(a,Times(b,Sqr(x))),p),Power(Times(d,Power(Subtract(C1,Times(Plus(c,Times(d,x)),Power(Subtract(c,Times(d,q)),CN1))),p),Power(Subtract(C1,Times(Plus(c,Times(d,x)),Power(Plus(c,Times(d,q)),CN1))),p)),CN1)),Subst(Integrate(Times(Power(x,n),Power(Simp(Subtract(C1,Times(x,Power(Plus(c,Times(d,q)),CN1))),x),p),Power(Simp(Subtract(C1,Times(x,Power(Subtract(c,Times(d,q)),CN1))),x),p)),x),x,Plus(c,Times(d,x)))),x)),And(FreeQ(List(a,b,c,d,n,p),x),NeQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0)))),
IIntegrate(515,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,u_)),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(u_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Times(Power(Plus(c,Times(d,x)),n),Power(Plus(a,Times(b,Sqr(x))),p)),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(516,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(c,Times(d,x)),Plus(n,p)),Power(Plus(Times(a,Power(c,CN1)),Times(b,Power(d,CN1),x)),p)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),EqQ(Plus(Times(b,Sqr(c)),Times(a,Sqr(d))),C0),Or(IntegerQ(p),And(GtQ(a,C0),GtQ(c,C0),Not(IntegerQ(n))))))),
IIntegrate(517,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(e,m),Power(Power(d,Plus(m,Times(C2,p),C1)),CN1)),Subst(Integrate(Times(Power(x,Plus(Times(C2,n),C1)),Power(Plus(Negate(c),Sqr(x)),m),Power(Plus(Times(b,Sqr(c)),Times(a,Sqr(d)),Times(CN1,C2,b,c,Sqr(x)),Times(b,Power(x,C4))),p)),x),x,Sqrt(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),ILtQ(m,C0),IntegerQ(Plus(n,C1D2))))),
IIntegrate(518,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(Power(e,Plus(n,Times(C2,p),C1)),CN1)),Subst(Integrate(Times(Power(x,Plus(Times(C2,m),C1)),Power(Plus(Times(e,c),Times(d,Sqr(x))),n),Power(Plus(Times(a,Sqr(e)),Times(b,Power(x,C4))),p)),x),x,Sqrt(Times(e,x)))),x),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),ILtQ(n,C0),IntegerQ(Plus(m,C1D2))))),
IIntegrate(519,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("§qx"),PolynomialQuotient(Power(Plus(a,Times(b,Sqr(x))),p),Plus(c,Times(d,x)),x)),Set($s("R"),PolynomialRemainder(Power(Plus(a,Times(b,Sqr(x))),p),Plus(c,Times(d,x)),x))),Plus(Simp(Times(CN1,$s("R"),Power(Times(e,x),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(c,e,Plus(n,C1)),CN1)),x),Simp(Star(Power(Times(c,Plus(n,C1)),CN1),Integrate(Times(Power(Times(e,x),m),Power(Plus(c,Times(d,x)),Plus(n,C1)),ExpandToSum(Plus(Times(c,Plus(n,C1),$s("§qx")),Times($s("R"),Plus(m,n,C2))),x)),x)),x))),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C0),LtQ(n,CN1),Not(IntegerQ(m))))),
IIntegrate(520,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("§qx"),PolynomialQuotient(Power(Plus(a,Times(b,Sqr(x))),p),Times(e,x),x)),Set($s("R"),PolynomialRemainder(Power(Plus(a,Times(b,Sqr(x))),p),Times(e,x),x))),Plus(Simp(Times($s("R"),Power(Times(e,x),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Plus(m,C1),e,c),CN1)),x),Simp(Star(Power(Times(Plus(m,C1),e,c),CN1),Integrate(Times(Power(Times(e,x),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),ExpandToSum(Subtract(Times(Plus(m,C1),e,c,$s("§qx")),Times(d,$s("R"),Plus(m,n,C2))),x)),x)),x))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(p,C0),LtQ(m,CN1),Not(IntegerQ(n)))))
  );
}
