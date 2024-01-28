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
class IntRules130 { 
  public static IAST RULES = List( 
IIntegrate(2601,Integrate(Times(Power(u_,m_),Power(v_,n_)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Subtract(Simp(Times(Power(u,Plus(m,C1)),Power(v,n),Power(Times(a,Plus(m,n,C1)),CN1)),x),Simp(Star(Times(n,Subtract(Times(b,u),Times(a,v)),Power(Times(a,Plus(m,n,C1)),CN1)),Integrate(Times(Power(u,m),Power(v,Simplify(Subtract(n,C1)))),x)),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),NeQ(Plus(m,n,C1),C0),Not(RationalQ(n)),SumSimplerQ(n,CN1)))),
IIntegrate(2602,Integrate(Times(Power(u_,m_),Power(v_,n_)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Simp(Times(CN1,Power(u,Plus(m,C1)),Power(v,Plus(n,C1)),Power(Times(Plus(m,C1),Subtract(Times(b,u),Times(a,v))),CN1)),x),Simp(Star(Times(b,Plus(m,n,C2),Power(Times(Plus(m,C1),Subtract(Times(b,u),Times(a,v))),CN1)),Integrate(Times(Power(u,Plus(m,C1)),Power(v,n)),x)),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),NeQ(Plus(m,n,C2),C0),LtQ(m,CN1)))),
IIntegrate(2603,Integrate(Times(Power(u_,m_),Power(v_,n_)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Simp(Times(CN1,Power(u,Plus(m,C1)),Power(v,Plus(n,C1)),Power(Times(Plus(m,C1),Subtract(Times(b,u),Times(a,v))),CN1)),x),Simp(Star(Times(b,Plus(m,n,C2),Power(Times(Plus(m,C1),Subtract(Times(b,u),Times(a,v))),CN1)),Integrate(Times(Power(u,Simplify(Plus(m,C1))),Power(v,n)),x)),x)),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),Not(RationalQ(m)),SumSimplerQ(m,C1)))),
IIntegrate(2604,Integrate(Times(Power(u_,m_),Power(v_,n_)),x_Symbol),
    Condition(With(list(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Simp(Times(Power(u,m),Power(v,Plus(n,C1)),Power(Times(b,Plus(n,C1),Power(Times(b,u,Power(Subtract(Times(b,u),Times(a,v)),CN1)),m)),CN1),Hypergeometric2F1(Negate(m),Plus(n,C1),Plus(n,C2),Times(CN1,a,v,Power(Subtract(Times(b,u),Times(a,v)),CN1)))),x),NeQ(Subtract(Times(b,u),Times(a,v)),C0))),And(PiecewiseLinearQ(u,v,x),Not(IntegerQ(m)),Not(IntegerQ(n))))),
IIntegrate(2605,Integrate(Times(Log(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(u_,n_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(D(u,x)))),Plus(Simp(Times(Power(u,n),Plus(a,Times(b,x)),Log(Plus(a,Times(b,x))),Power(b,CN1)),x),Negate(Integrate(Power(u,n),x)),Negate(Simp(Star(Times(c,n,Power(b,CN1)),Integrate(Times(Power(u,Subtract(n,C1)),Plus(a,Times(b,x)),Log(Plus(a,Times(b,x)))),x)),x)))),And(FreeQ(list(a,b),x),PiecewiseLinearQ(u,x),Not(LinearQ(u,x)),GtQ(n,C0)))),
IIntegrate(2606,Integrate(Times(Log(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(u_,n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(c,Simplify(D(u,x)))),Plus(Simp(Times(Power(u,n),Power(Plus(a,Times(b,x)),Plus(m,C1)),Log(Plus(a,Times(b,x))),Power(Times(b,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Power(Plus(m,C1),CN1),Integrate(Times(Power(u,n),Power(Plus(a,Times(b,x)),m)),x)),x)),Negate(Simp(Star(Times(c,n,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(u,Subtract(n,C1)),Power(Plus(a,Times(b,x)),Plus(m,C1)),Log(Plus(a,Times(b,x)))),x)),x)))),And(FreeQ(list(a,b,m),x),PiecewiseLinearQ(u,x),Not(LinearQ(u,x)),GtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(2607,Integrate(Times(Power(Times(b_DEFAULT,Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Times(b,Power(FSymbol,Times(g,Plus(e,Times(f,x))))),n),Power(Times(f,g,n,Log(FSymbol)),CN1)),x),Simp(Star(Times(d,m,Power(Times(f,g,n,Log(FSymbol)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Times(b,Power(FSymbol,Times(g,Plus(e,Times(f,x))))),n)),x)),x)),And(FreeQ(List(FSymbol,b,c,d,e,f,g,n),x),GtQ(m,C0),IntegerQ(Times(C2,m)),Not(TrueQ(False))))),
IIntegrate(2608,Integrate(Times(Power(Times(b_DEFAULT,Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(b,Power(FSymbol,Times(g,Plus(e,Times(f,x))))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(f,g,n,Log(FSymbol),Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(b,Power(FSymbol,Times(g,Plus(e,Times(f,x))))),n)),x)),x)),And(FreeQ(List(FSymbol,b,c,d,e,f,g,n),x),LtQ(m,CN1),IntegerQ(Times(C2,m)),Not(TrueQ(False))))),
IIntegrate(2609,Integrate(Times(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Simp(Times(Power(FSymbol,Times(g,Subtract(e,Times(c,f,Power(d,CN1))))),Power(d,CN1),ExpIntegralEi(Times(f,g,Plus(c,Times(d,x)),Log(FSymbol),Power(d,CN1)))),x),And(FreeQ(List(FSymbol,c,d,e,f,g),x),Not(TrueQ(False))))),
IIntegrate(2610,Integrate(Times(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Negate(d),m),Power(FSymbol,Times(g,Subtract(e,Times(c,f,Power(d,CN1))))),Power(Times(Power(f,Plus(m,C1)),Power(g,Plus(m,C1)),Power(Log(FSymbol),Plus(m,C1))),CN1),Gamma(Plus(m,C1),Times(CN1,f,g,Log(FSymbol),Power(d,CN1),Plus(c,Times(d,x))))),x),And(FreeQ(List(FSymbol,c,d,e,f,g),x),IntegerQ(m)))),
IIntegrate(2611,Integrate(Times(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(d,CN1)),Subst(Integrate(Power(FSymbol,Plus(Times(g,Subtract(e,Times(c,f,Power(d,CN1)))),Times(f,g,Sqr(x),Power(d,CN1)))),x),x,Sqrt(Plus(c,Times(d,x))))),x),And(FreeQ(List(FSymbol,c,d,e,f,g),x),Not(TrueQ(False))))),
IIntegrate(2612,Integrate(Times(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Simp(Times(CN1,Power(FSymbol,Times(g,Subtract(e,Times(c,f,Power(d,CN1))))),Power(Plus(c,Times(d,x)),FracPart(m)),Power(Times(d,Power(Times(CN1,f,g,Log(FSymbol),Power(d,CN1)),Plus(IntPart(m),C1)),Power(Times(CN1,f,g,Log(FSymbol),Plus(c,Times(d,x)),Power(d,CN1)),FracPart(m))),CN1),Gamma(Plus(m,C1),Times(CN1,f,g,Log(FSymbol),Power(d,CN1),Plus(c,Times(d,x))))),x),And(FreeQ(List(FSymbol,c,d,e,f,g,m),x),Not(IntegerQ(m))))),
IIntegrate(2613,Integrate(Times(Power(Times(b_DEFAULT,Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(b,Power(FSymbol,Times(g,Plus(e,Times(f,x))))),n),Power(Power(FSymbol,Times(g,n,Plus(e,Times(f,x)))),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(FSymbol,Times(g,n,Plus(e,Times(f,x))))),x)),x),FreeQ(List(FSymbol,b,c,d,e,f,g,m,n),x))),
IIntegrate(2614,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n))),p),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,m,n),x),IGtQ(p,C0)))),
IIntegrate(2615,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT))),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(a,d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(a,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n))),CN1)),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,n),x),IGtQ(m,C0)))),
IIntegrate(2616,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT))),p_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(a,CN1),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n))),Plus(p,C1))),x)),x),Simp(Star(Times(b,Power(a,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n))),p)),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,n),x),ILtQ(p,C0),IGtQ(m,C0)))),
IIntegrate(2617,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT))),p_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n))),p),x))),Subtract(Simp(Star(Power(Plus(c,Times(d,x)),m),u),x),Simp(Star(Times(d,m),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),u),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,n),x),IGtQ(m,C0),LtQ(p,CN1)))),
IIntegrate(2618,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Power(F_,Times(g_DEFAULT,v_)),n_DEFAULT))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,ExpandToSum(v,x))),n))),p)),x),And(FreeQ(List(FSymbol,a,b,c,d,g,n,p),x),LinearQ(v,x),Not(LinearMatchQ(v,x)),IntegerQ(m)))),
IIntegrate(2619,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n))),p),Power(Plus(c,Times(d,x)),m)),x),FreeQ(List(a,b,c,d,e,f,g,m,n,p),x))),
IIntegrate(2620,Integrate(Times(Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT))),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Times(b,f,g,n,Log(FSymbol)),CN1),Log(Plus(C1,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n),Power(a,CN1))))),x),Simp(Star(Times(d,m,Power(Times(b,f,g,n,Log(FSymbol)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Log(Plus(C1,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n),Power(a,CN1))))),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,n),x),IGtQ(m,C0))))
  );
}
