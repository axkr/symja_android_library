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
public class IntRules86 { 
  public static IAST RULES = List( 
IIntegrate(2151,Int(Times(Power(Plus(c_,Times(d_DEFAULT,x_)),CN1),Plus(e_,Times(f_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),CN1D3)),x_Symbol),
    Condition(Plus(Simp(Times(CSqrt3,f,ArcTan(Times(Plus(C1,Times(C2,Rt(b,C3),Plus(Times(C2,c),Times(d,x)),Power(Times(d,Power(Plus(a,Times(b,Power(x,C3))),C1D3)),CN1))),C1DSqrt3)),Power(Times(Rt(b,C3),d),CN1)),x),Simp(Times(f,Log(Plus(c,Times(d,x))),Power(Times(Rt(b,C3),d),CN1)),x),Negate(Simp(Times(C3,f,Log(Subtract(Times(Rt(b,C3),Plus(Times(C2,c),Times(d,x))),Times(d,Power(Plus(a,Times(b,Power(x,C3))),C1D3)))),Power(Times(C2,Rt(b,C3),d),CN1)),x))),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Times(d,e),Times(c,f)),C0),EqQ(Subtract(Times(C2,b,Power(c,C3)),Times(a,Power(d,C3))),C0)))),
IIntegrate(2152,Int(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1),Plus(e_DEFAULT,Times(f_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,Power(x_,C3))),CN1D3)),x_Symbol),
    Condition(Plus(Dist(Times(f,Power(d,CN1)),Int(Power(Plus(a,Times(b,Power(x,C3))),CN1D3),x),x),Dist(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Int(Power(Times(Plus(c,Times(d,x)),Power(Plus(a,Times(b,Power(x,C3))),C1D3)),CN1),x),x)),FreeQ(List(a,b,c,d,e,f),x))),
IIntegrate(2153,Int(Times(Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,$p("nn",true)))),p_)),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(b,Power(x,$s("nn")))),p),Power(Subtract(Times(c,Power(Subtract(Sqr(c),Times(Sqr(d),Power(x,Times(C2,n)))),CN1)),Times(d,Power(x,n),Power(Subtract(Sqr(c),Times(Sqr(d),Power(x,Times(C2,n)))),CN1))),Negate(q)),x),x),And(FreeQ(List(a,b,c,d,n,$s("nn"),p),x),Not(IntegerQ(p)),ILtQ(q,C0),IGtQ(Log(C2,Times($s("nn"),Power(n,CN1))),C0)))),
IIntegrate(2154,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_),Power(Plus(a_,Times(b_DEFAULT,Power(x_,$p("nn",true)))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Times(e,x),m),Power(Power(x,m),CN1)),Int(ExpandIntegrand(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,$s("nn")))),p)),Power(Subtract(Times(c,Power(Subtract(Sqr(c),Times(Sqr(d),Power(x,Times(C2,n)))),CN1)),Times(d,Power(x,n),Power(Subtract(Sqr(c),Times(Sqr(d),Power(x,Times(C2,n)))),CN1))),Negate(q)),x),x),x),And(FreeQ(List(a,b,c,d,e,m,n,$s("nn"),p),x),Not(IntegerQ(p)),ILtQ(q,C0),IGtQ(Log(C2,Times($s("nn"),Power(n,CN1))),C0)))),
IIntegrate(2155,Int(Times(Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_)),Times(e_DEFAULT,Sqrt(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))))),CN1)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Plus(c,Times(d,x),Times(e,Sqrt(Plus(a,Times(b,x))))),CN1)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,e,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0),IntegerQ(Times(Plus(m,C1),Power(n,CN1)))))),
IIntegrate(2156,Int(Times(u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_)),Times(e_DEFAULT,Sqrt(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))))),CN1)),x_Symbol),
    Condition(Subtract(Dist(c,Int(Times(u,Power(Plus(Sqr(c),Times(CN1,a,Sqr(e)),Times(c,d,Power(x,n))),CN1)),x),x),Dist(Times(a,e),Int(Times(u,Power(Times(Plus(Sqr(c),Times(CN1,a,Sqr(e)),Times(c,d,Power(x,n))),Sqrt(Plus(a,Times(b,Power(x,n))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(2157,Int(Power(u_,m_DEFAULT),x_Symbol),
    Condition(With(List(Set(c,Simplify(D(u,x)))),Dist(Power(c,CN1),Subst(Int(Power(x,m),x),x,u),x)),And(FreeQ(m,x),PiecewiseLinearQ(u,x)))),
IIntegrate(2158,Int(Times(Power(u_,CN1),v_),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(b,x,Power(a,CN1)),x),Dist(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),Int(Power(u,CN1),x),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2159,Int(Times(Power(u_,CN1),Power(v_,n_)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(Power(v,n),Power(Times(a,n),CN1)),x),Dist(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),Int(Times(Power(v,Subtract(n,C1)),Power(u,CN1)),x),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),GtQ(n,C0),NeQ(n,C1)))),
IIntegrate(2160,Int(Times(Power(u_,CN1),Power(v_,CN1)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Dist(Times(b,Power(Subtract(Times(b,u),Times(a,v)),CN1)),Int(Power(v,CN1),x),x),Dist(Times(a,Power(Subtract(Times(b,u),Times(a,v)),CN1)),Int(Power(u,CN1),x),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2161,Int(Times(Power(u_,CN1),Power(v_,CN1D2)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(C2,ArcTan(Times(Sqrt(v),Power(Rt(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),C2),CN1))),Power(Times(a,Rt(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),C2)),CN1)),x),And(NeQ(Subtract(Times(b,u),Times(a,v)),C0),PosQ(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)))))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2162,Int(Times(Power(u_,CN1),Power(v_,CN1D2)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(CN2,ArcTanh(Times(Sqrt(v),Power(Rt(Times(CN1,Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),C2),CN1))),Power(Times(a,Rt(Times(CN1,Subtract(Times(b,u),Times(a,v)),Power(a,CN1)),C2)),CN1)),x),And(NeQ(Subtract(Times(b,u),Times(a,v)),C0),NegQ(Times(Subtract(Times(b,u),Times(a,v)),Power(a,CN1)))))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2163,Int(Times(Power(u_,CN1),Power(v_,n_)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(Power(v,Plus(n,C1)),Power(Times(Plus(n,C1),Subtract(Times(b,u),Times(a,v))),CN1)),x),Dist(Times(a,Plus(n,C1),Power(Times(Plus(n,C1),Subtract(Times(b,u),Times(a,v))),CN1)),Int(Times(Power(v,Plus(n,C1)),Power(u,CN1)),x),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),LtQ(n,CN1)))),
IIntegrate(2164,Int(Times(Power(u_,CN1),Power(v_,n_)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(Power(v,Plus(n,C1)),Hypergeometric2F1(C1,Plus(n,C1),Plus(n,C2),Times(CN1,a,v,Power(Subtract(Times(b,u),Times(a,v)),CN1))),Power(Times(Plus(n,C1),Subtract(Times(b,u),Times(a,v))),CN1)),x),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),Not(IntegerQ(n))))),
IIntegrate(2165,Int(Times(Power(u_,CN1D2),Power(v_,CN1D2)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(C2,ArcTanh(Times(Rt(Times(a,b),C2),Sqrt(u),Power(Times(a,Sqrt(v)),CN1))),Power(Rt(Times(a,b),C2),CN1)),x),And(NeQ(Subtract(Times(b,u),Times(a,v)),C0),PosQ(Times(a,b))))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2166,Int(Times(Power(u_,CN1D2),Power(v_,CN1D2)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(C2,ArcTan(Times(Rt(Times(CN1,a,b),C2),Sqrt(u),Power(Times(a,Sqrt(v)),CN1))),Power(Rt(Times(CN1,a,b),C2),CN1)),x),And(NeQ(Subtract(Times(b,u),Times(a,v)),C0),NegQ(Times(a,b))))),PiecewiseLinearQ(u,v,x))),
IIntegrate(2167,Int(Times(Power(u_,m_),Power(v_,n_)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Negate(Simp(Times(Power(u,Plus(m,C1)),Power(v,Plus(n,C1)),Power(Times(Plus(m,C1),Subtract(Times(b,u),Times(a,v))),CN1)),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(FreeQ(List(m,n),x),PiecewiseLinearQ(u,v,x),EqQ(Plus(m,n,C2),C0),NeQ(m,CN1)))),
IIntegrate(2168,Int(Times(Power(u_,m_),Power(v_,n_DEFAULT)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(Power(u,Plus(m,C1)),Power(v,n),Power(Times(a,Plus(m,C1)),CN1)),x),Dist(Times(b,n,Power(Times(a,Plus(m,C1)),CN1)),Int(Times(Power(u,Plus(m,C1)),Power(v,Subtract(n,C1))),x),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(FreeQ(List(m,n),x),PiecewiseLinearQ(u,v,x),NeQ(m,CN1),Or(And(LtQ(m,CN1),GtQ(n,C0),Not(And(ILtQ(Plus(m,n),CN2),Or(FractionQ(m),GeQ(Plus(Times(C2,n),m,C1),C0))))),And(IGtQ(n,C0),IGtQ(m,C0),LeQ(n,m)),And(IGtQ(n,C0),Not(IntegerQ(m))),And(ILtQ(m,C0),Not(IntegerQ(n))))))),
IIntegrate(2169,Int(Times(Power(u_,m_),Power(v_,n_DEFAULT)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(Power(u,Plus(m,C1)),Power(v,n),Power(Times(a,Plus(m,n,C1)),CN1)),x),Dist(Times(n,Subtract(Times(b,u),Times(a,v)),Power(Times(a,Plus(m,n,C1)),CN1)),Int(Times(Power(u,m),Power(v,Subtract(n,C1))),x),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),NeQ(Plus(m,n,C2),C0),GtQ(n,C0),NeQ(Plus(m,n,C1),C0),Not(And(IGtQ(m,C0),Or(Not(IntegerQ(n)),LtQ(C0,m,n)))),Not(ILtQ(Plus(m,n),CN2))))),
IIntegrate(2170,Int(Times(Power(u_,m_),Power(v_,n_)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(Power(u,Plus(m,C1)),Power(v,n),Power(Times(a,Plus(m,n,C1)),CN1)),x),Dist(Times(n,Subtract(Times(b,u),Times(a,v)),Power(Times(a,Plus(m,n,C1)),CN1)),Int(Times(Power(u,m),Power(v,Simplify(Subtract(n,C1)))),x),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),NeQ(Plus(m,n,C1),C0),Not(RationalQ(n)),SumSimplerQ(n,CN1)))),
IIntegrate(2171,Int(Times(Power(u_,m_),Power(v_,n_)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Negate(Simp(Times(Power(u,Plus(m,C1)),Power(v,Plus(n,C1)),Power(Times(Plus(m,C1),Subtract(Times(b,u),Times(a,v))),CN1)),x)),Dist(Times(b,Plus(m,n,C2),Power(Times(Plus(m,C1),Subtract(Times(b,u),Times(a,v))),CN1)),Int(Times(Power(u,Plus(m,C1)),Power(v,n)),x),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),NeQ(Plus(m,n,C2),C0),LtQ(m,CN1)))),
IIntegrate(2172,Int(Times(Power(u_,m_),Power(v_,n_)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Negate(Simp(Times(Power(u,Plus(m,C1)),Power(v,Plus(n,C1)),Power(Times(Plus(m,C1),Subtract(Times(b,u),Times(a,v))),CN1)),x)),Dist(Times(b,Plus(m,n,C2),Power(Times(Plus(m,C1),Subtract(Times(b,u),Times(a,v))),CN1)),Int(Times(Power(u,Simplify(Plus(m,C1))),Power(v,n)),x),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),Not(RationalQ(m)),SumSimplerQ(m,C1)))),
IIntegrate(2173,Int(Times(Power(u_,m_),Power(v_,n_)),x_Symbol),
    Condition(With(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(Power(u,m),Power(v,Plus(n,C1)),Hypergeometric2F1(Negate(m),Plus(n,C1),Plus(n,C2),Times(CN1,a,v,Power(Subtract(Times(b,u),Times(a,v)),CN1))),Power(Times(b,Plus(n,C1),Power(Times(b,u,Power(Subtract(Times(b,u),Times(a,v)),CN1)),m)),CN1)),x),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),Not(IntegerQ(m)),Not(IntegerQ(n))))),
IIntegrate(2174,Int(Times(Log(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(u_,n_DEFAULT)),x_Symbol),
    Condition(With(List(Set(c,Simplify(D(u,x)))),Plus(Simp(Times(Power(u,n),Plus(a,Times(b,x)),Log(Plus(a,Times(b,x))),Power(b,CN1)),x),Negate(Dist(Times(c,n,Power(b,CN1)),Int(Times(Power(u,Subtract(n,C1)),Plus(a,Times(b,x)),Log(Plus(a,Times(b,x)))),x),x)),Negate(Int(Power(u,n),x)))),And(FreeQ(List(a,b),x),PiecewiseLinearQ(u,x),Not(LinearQ(u,x)),GtQ(n,C0)))),
IIntegrate(2175,Int(Times(Log(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(u_,n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(c,Simplify(D(u,x)))),Plus(Simp(Times(Power(u,n),Power(Plus(a,Times(b,x)),Plus(m,C1)),Log(Plus(a,Times(b,x))),Power(Times(b,Plus(m,C1)),CN1)),x),Negate(Dist(Power(Plus(m,C1),CN1),Int(Times(Power(u,n),Power(Plus(a,Times(b,x)),m)),x),x)),Negate(Dist(Times(c,n,Power(Times(b,Plus(m,C1)),CN1)),Int(Times(Power(u,Subtract(n,C1)),Power(Plus(a,Times(b,x)),Plus(m,C1)),Log(Plus(a,Times(b,x)))),x),x)))),And(FreeQ(List(a,b,m),x),PiecewiseLinearQ(u,x),Not(LinearQ(u,x)),GtQ(n,C0),NeQ(m,CN1))))
  );
}
