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
class IntRules129 { 
  public static IAST RULES = List( 
IIntegrate(2581,Integrate(Times($p("§px",true),Power(Plus(c_,Times(d_DEFAULT,x_)),q_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(Power(c,C3),Times(Power(d,C3),Power(x,C3))),q),Power(Plus(a,Times(b,Power(x,C3))),p)),Times($s("§px"),Power(Power(Plus(Sqr(c),Times(CN1,c,d,x),Times(Sqr(d),Sqr(x))),q),CN1)),x),x),And(FreeQ(List(a,b,c,d,p),x),PolyQ($s("§px"),x),ILtQ(q,C0),RationalQ(p),EqQ(Denominator(p),C3)))),
IIntegrate(2582,Integrate(Times($p("§px"),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(c,q),CN1),Integrate(ExpandIntegrand(Times(Power(Subtract(Power(c,C3),Times(Power(d,C3),Power(x,C3))),q),Power(Plus(a,Times(b,Power(x,C3))),p)),Times(Power(x,m),$s("§px"),Power(Power(Subtract(c,Times(d,x)),q),CN1)),x),x)),x),And(FreeQ(List(a,b,c,d,e,m,p),x),PolyQ($s("§px"),x),EqQ(Subtract(Sqr(d),Times(c,e)),C0),ILtQ(q,C0),IntegerQ(m),RationalQ(p),EqQ(Denominator(p),C3)))),
IIntegrate(2583,Integrate(Times($p("§px",true),Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(c,q),CN1),Integrate(ExpandIntegrand(Times(Power(Subtract(Power(c,C3),Times(Power(d,C3),Power(x,C3))),q),Power(Plus(a,Times(b,Power(x,C3))),p)),Times($s("§px"),Power(Power(Subtract(c,Times(d,x)),q),CN1)),x),x)),x),And(FreeQ(List(a,b,c,d,e,p),x),PolyQ($s("§px"),x),EqQ(Subtract(Sqr(d),Times(c,e)),C0),ILtQ(q,C0),RationalQ(p),EqQ(Denominator(p),C3)))),
IIntegrate(2584,Integrate(Times(Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,$p("nn",true)))),p_)),x_Symbol),
    Condition(Integrate(Times(ExpandToSum(Power(Subtract(c,Times(d,Power(x,n))),Negate(q)),x),Power(Plus(a,Times(b,Power(x,$s("nn")))),p),Power(Power(Subtract(Sqr(c),Times(Sqr(d),Power(x,Times(C2,n)))),Negate(q)),CN1)),x),And(FreeQ(List(a,b,c,d,n,$s("nn"),p),x),Not(IntegerQ(p)),ILtQ(q,C0),IGtQ(Log(C2,Times($s("nn"),Power(n,CN1))),C0)))),
IIntegrate(2585,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,$p("nn",true)))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(e,x),m),Power(Power(x,m),CN1)),Integrate(Times(Power(x,m),ExpandToSum(Power(Subtract(c,Times(d,Power(x,n))),Negate(q)),x),Power(Plus(a,Times(b,Power(x,$s("nn")))),p),Power(Power(Subtract(Sqr(c),Times(Sqr(d),Power(x,Times(C2,n)))),Negate(q)),CN1)),x)),x),And(FreeQ(List(a,b,c,d,e,m,n,$s("nn"),p),x),Not(IntegerQ(p)),ILtQ(q,C0),IGtQ(Log(C2,Times($s("nn"),Power(n,CN1))),C0)))),
IIntegrate(2586,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_)),Times(e_DEFAULT,Sqrt(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))))),CN1)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Plus(c,Times(d,x),Times(e,Sqrt(Plus(a,Times(b,x))))),CN1)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,e,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),IntegerQ(Times(Plus(m,C1),Power(n,CN1)))))),
IIntegrate(2587,Integrate(Times(u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_)),Times(e_DEFAULT,Sqrt(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(c,Integrate(Times(u,Power(Plus(Sqr(c),Times(CN1,a,Sqr(e)),Times(c,d,Power(x,n))),CN1)),x)),x),Simp(Star(Times(a,e),Integrate(Times(u,Power(Times(Plus(Sqr(c),Times(CN1,a,Sqr(e)),Times(c,d,Power(x,n))),Sqrt(Plus(a,Times(b,Power(x,n))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(2588,Integrate(Power(u_,m_DEFAULT),x_Symbol),
    Condition(With(list(Set(c,Simplify(D(u,x)))),Simp(Star(Power(c,CN1),Subst(Integrate(Power(x,m),x),x,u)),x)),And(FreeQ(m,x),PiecewiseLinearQ(u,x)))),
IIntegrate(2589,Integrate(Times(Power(u_,CN1),v_),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(b,x,Power(a,CN1)),x),Simp(Star(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),Integrate(Power(u,CN1),x)),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2590,Integrate(Times(Power(u_,CN1),Power(v_,n_)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(Power(v,n),Power(Times(a,n),CN1)),x),Simp(Star(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),Integrate(Times(Power(v,Subtract(n,C1)),Power(u,CN1)),x)),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),GtQ(n,C0),NeQ(n,C1)))),
IIntegrate(2591,Integrate(Times(Power(u_,CN1),Power(v_,CN1)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Star(Times(b,Power(Subtract(Times(b,u),Times(a,v)),CN1)),Integrate(Power(v,CN1),x)),x),Simp(Star(Times(a,Power(Subtract(Times(b,u),Times(a,v)),CN1)),Integrate(Power(u,CN1),x)),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2592,Integrate(Times(Power(u_,CN1),Power(v_,CN1D2)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(C2,ArcTan(Times(Sqrt(v),Power(Rt(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),C2),CN1))),Power(Times(a,Rt(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),C2)),CN1)),x),And(NeQ(Subtract(Times(b,u),Times(a,v)),C0),PosQ(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)))))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2593,Integrate(Times(Power(u_,CN1),Power(v_,CN1D2)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(CN2,ArcTanh(Times(Sqrt(v),Power(Rt(Times(CN1,Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),C2),CN1))),Power(Times(a,Rt(Times(CN1,Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),C2)),CN1)),x),And(NeQ(Subtract(Times(b,u),Times(a,v)),C0),NegQ(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)))))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2594,Integrate(Times(Power(u_,CN1),Power(v_,n_)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(Power(v,Plus(n,C1)),Power(Times(Plus(n,C1),Subtract(Times(b,u),Times(a,v))),CN1)),x),Simp(Star(Times(a,Plus(n,C1),Power(Times(Plus(n,C1),Subtract(Times(b,u),Times(a,v))),CN1)),Integrate(Times(Power(v,Plus(n,C1)),Power(u,CN1)),x)),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),LtQ(n,CN1)))),
IIntegrate(2595,Integrate(Times(Power(u_,CN1),Power(v_,n_)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(Power(v,Plus(n,C1)),Power(Times(Plus(n,C1),Subtract(Times(b,u),Times(a,v))),CN1),Hypergeometric2F1(C1,Plus(n,C1),Plus(n,C2),Times(CN1,a,v,Power(Subtract(Times(b,u),Times(a,v)),CN1)))),x),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),Not(IntegerQ(n))))),
IIntegrate(2596,Integrate(Times(Power(u_,CN1D2),Power(v_,CN1D2)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(C2,Power(Rt(Times(a,b),C2),CN1),ArcTanh(Times(Rt(Times(a,b),C2),Sqrt(u),Power(Times(a,Sqrt(v)),CN1)))),x),And(NeQ(Subtract(Times(b,u),Times(a,v)),C0),PosQ(Times(a,b))))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2597,Integrate(Times(Power(u_,CN1D2),Power(v_,CN1D2)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(C2,Power(Rt(Times(CN1,a,b),C2),CN1),ArcTan(Times(Rt(Times(CN1,a,b),C2),Sqrt(u),Power(Times(a,Sqrt(v)),CN1)))),x),And(NeQ(Subtract(Times(b,u),Times(a,v)),C0),NegQ(Times(a,b))))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2598,Integrate(Times(Power(u_,m_),Power(v_,n_)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(CN1,Power(u,Plus(m,C1)),Power(v,Plus(n,C1)),Power(Times(Plus(m,C1),Subtract(Times(b,u),Times(a,v))),CN1)),x),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(FreeQ(list(m,n),x),PiecewiseLinearQ(u,v,x),EqQ(Plus(m,n,C2),C0),NeQ(m,CN1)))),
IIntegrate(2599,Integrate(Times(Power(u_,m_),Power(v_,n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(Power(u,Plus(m,C1)),Power(v,n),Power(Times(a,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,n,Power(Times(a,Plus(m,C1)),CN1)),Integrate(Times(Power(u,Plus(m,C1)),Power(v,Subtract(n,C1))),x)),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(FreeQ(list(m,n),x),PiecewiseLinearQ(u,v,x),NeQ(m,CN1),Or(And(LtQ(m,CN1),GtQ(n,C0),Not(And(ILtQ(Plus(m,n),CN2),Or(FractionQ(m),GeQ(Plus(Times(C2,n),m,C1),C0))))),And(IGtQ(n,C0),IGtQ(m,C0),LeQ(n,m)),And(IGtQ(n,C0),Not(IntegerQ(m))),And(ILtQ(m,C0),Not(IntegerQ(n))))))),
IIntegrate(2600,Integrate(Times(Power(u_,m_),Power(v_,n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(Power(u,Plus(m,C1)),Power(v,n),Power(Times(a,Plus(m,n,C1)),CN1)),x),Simp(Star(Times(n,Subtract(Times(b,u),Times(a,v)),Power(Times(a,Plus(m,n,C1)),CN1)),Integrate(Times(Power(u,m),Power(v,Subtract(n,C1))),x)),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),NeQ(Plus(m,n,C2),C0),GtQ(n,C0),NeQ(Plus(m,n,C1),C0),Not(And(IGtQ(m,C0),Or(Not(IntegerQ(n)),LtQ(C0,m,n)))),Not(ILtQ(Plus(m,n),CN2)))))
  );
}
