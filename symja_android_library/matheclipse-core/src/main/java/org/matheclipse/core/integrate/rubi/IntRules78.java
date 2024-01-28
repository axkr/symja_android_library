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
class IntRules78 { 
  public static IAST RULES = List( 
IIntegrate(1561,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Power(x,C3),Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(c,Times(b,Power(x,CN2)),Times(a,Power(x,CN4)))),Power(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))))),CN1)),Integrate(Power(Times(Power(x,C3),Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(c,Times(b,Power(x,CN2)),Times(a,Power(x,CN4))))),CN1),x)),x),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0)))),
IIntegrate(1562,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Power(x,C3),Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(c,Times(a,Power(x,CN4)))),Power(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Plus(a,Times(c,Power(x,C4))))),CN1)),Integrate(Power(Times(Power(x,C3),Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(c,Times(a,Power(x,CN4))))),CN1),x)),x),And(FreeQ(List(a,c,d,e),x),NeQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0)))),
IIntegrate(1563,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Simp(Times(Sqrt(a),Power(Times(C2,Sqrt(d),Rt(Times(CN1,e,Power(d,CN1)),C2)),CN1),EllipticE(Times(C2,ArcSin(Times(Rt(Times(CN1,e,Power(d,CN1)),C2),x))),Times(b,d,Power(Times(C4,a,e),CN1)))),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(c,d),Times(b,e)),C0),GtQ(a,C0),GtQ(d,C0)))),
IIntegrate(1564,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4)))),Sqrt(Times(Plus(d,Times(e,Sqr(x))),Power(d,CN1))),Power(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Times(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),Power(a,CN1)))),CN1)),Integrate(Times(Sqrt(Plus(C1,Times(b,Power(a,CN1),Sqr(x)),Times(c,Power(a,CN1),Power(x,C4)))),Power(Plus(C1,Times(e,Power(d,CN1),Sqr(x))),CN1D2)),x)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(c,d),Times(b,e)),C0),Not(And(GtQ(a,C0),GtQ(d,C0)))))),
IIntegrate(1565,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4)))),Power(Times(x,Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Plus(c,Times(b,Power(x,CN2)),Times(a,Power(x,CN4))))),CN1)),Integrate(Times(x,Sqrt(Plus(c,Times(b,Power(x,CN2)),Times(a,Power(x,CN4)))),Power(Plus(e,Times(d,Power(x,CN2))),CN1D2)),x)),x),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0)))),
IIntegrate(1566,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Sqrt(Plus(a_,Times(c_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(a,Times(c,Power(x,C4)))),Power(Times(x,Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Plus(c,Times(a,Power(x,CN4))))),CN1)),Integrate(Times(x,Sqrt(Plus(c,Times(a,Power(x,CN4)))),Power(Plus(e,Times(d,Power(x,CN2))),CN1D2)),x)),x),And(FreeQ(List(a,c,d,e),x),NeQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0)))),
IIntegrate(1567,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),p)),x),x),And(FreeQ(List(a,b,c,d,e,p,q),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Or(And(IntegerQ(p),IntegerQ(q)),IGtQ(p,C0),IGtQ(q,C0))))),
IIntegrate(1568,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(c,Power(x,C4))),p)),x),x),And(FreeQ(List(a,c,d,e,p,q),x),Or(And(IntegerQ(p),IntegerQ(q)),IGtQ(p,C0))))),
IIntegrate(1569,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(c,Power(x,C4))),p),Power(Subtract(Times(d,Power(Subtract(Sqr(d),Times(Sqr(e),Power(x,C4))),CN1)),Times(e,Sqr(x),Power(Subtract(Sqr(d),Times(Sqr(e),Power(x,C4))),CN1))),Negate(q)),x),x),And(FreeQ(List(a,c,d,e,p),x),NeQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),ILtQ(q,C0)))),
IIntegrate(1570,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),p)),x),FreeQ(List(a,b,c,d,e,p,q),x))),
IIntegrate(1571,Integrate(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(c,Power(x,C4))),p)),x),FreeQ(List(a,c,d,e,p,q),x))),
IIntegrate(1572,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(e_DEFAULT,Sqr(x_)),q_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(C2,Power(e,Times(C1D2,Subtract(m,C1)))),CN1),Subst(Integrate(Times(Power(Times(e,x),Plus(q,Times(C1D2,Subtract(m,C1)))),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x,Sqr(x))),x),And(FreeQ(List(a,b,c,e,p,q),x),Not(IntegerQ(q)),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(1573,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(e_DEFAULT,Sqr(x_)),q_),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(C2,Power(e,Times(C1D2,Subtract(m,C1)))),CN1),Subst(Integrate(Times(Power(Times(e,x),Plus(q,Times(C1D2,Subtract(m,C1)))),Power(Plus(a,Times(c,Sqr(x))),p)),x),x,Sqr(x))),x),And(FreeQ(List(a,c,e,p,q),x),Not(IntegerQ(q)),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(1574,Integrate(Times(Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Times(e_DEFAULT,Sqr(x_)),q_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(q)),Power(Times(e,Sqr(x)),FracPart(q)),Power(Times(Power(f,Times(C2,IntPart(q))),Power(Times(f,x),Times(C2,FracPart(q)))),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,Times(C2,q))),Power(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),p)),x)),x),And(FreeQ(List(a,b,c,e,f,m,p,q),x),Not(IntegerQ(q))))),
IIntegrate(1575,Integrate(Times(Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Times(e_DEFAULT,Sqr(x_)),q_),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(q)),Power(Times(e,Sqr(x)),FracPart(q)),Power(Times(Power(f,Times(C2,IntPart(q))),Power(Times(f,x),Times(C2,FracPart(q)))),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,Times(C2,q))),Power(Plus(a,Times(c,Power(x,C4))),p)),x)),x),And(FreeQ(List(a,c,e,f,m,p,q),x),Not(IntegerQ(q))))),
IIntegrate(1576,Integrate(Times(x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(C1D2,Subst(Integrate(Times(Power(Plus(d,Times(e,x)),q),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x,Sqr(x))),x),FreeQ(List(a,b,c,d,e,p,q),x))),
IIntegrate(1577,Integrate(Times(x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(C1D2,Subst(Integrate(Times(Power(Plus(d,Times(e,x)),q),Power(Plus(a,Times(c,Sqr(x))),p)),x),x,Sqr(x))),x),FreeQ(List(a,c,d,e,p,q),x))),
IIntegrate(1578,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(C1D2,Subst(Integrate(Times(Power(x,Times(C1D2,Subtract(m,C1))),Power(Plus(d,Times(e,x)),q),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x,Sqr(x))),x),And(FreeQ(List(a,b,c,d,e,p,q),x),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(1579,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(C1D2,Subst(Integrate(Times(Power(x,Times(C1D2,Subtract(m,C1))),Power(Plus(d,Times(e,x)),q),Power(Plus(a,Times(c,Sqr(x))),p)),x),x,Sqr(x))),x),And(FreeQ(List(a,c,d,e,p,q),x),IntegerQ(Times(C1D2,Plus(m,C1)))))),
IIntegrate(1580,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Negate(d),Subtract(Times(C1D2,m),C1)),Power(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),p),x,Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),Power(Times(C2,Power(e,Plus(Times(C2,p),Times(C1D2,m))),Plus(q,C1)),CN1)),x),Simp(Star(Power(Times(C2,Power(e,Plus(Times(C2,p),Times(C1D2,m))),Plus(q,C1)),CN1),Integrate(Times(Power(Plus(d,Times(e,Sqr(x))),Plus(q,C1)),ExpandToSum(Together(Times(Power(Plus(d,Times(e,Sqr(x))),CN1),Subtract(Times(C2,Power(e,Plus(Times(C2,p),Times(C1D2,m))),Plus(q,C1),Power(x,m),Power(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),p)),Times(Power(Negate(d),Subtract(Times(C1D2,m),C1)),Power(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),p),Plus(d,Times(e,Plus(Times(C2,q),C3),Sqr(x))))))),x)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(p,C0),ILtQ(q,CN1),IGtQ(Times(C1D2,m),C0))))
  );
}
