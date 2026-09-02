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
class IntRules294 { 
  public static IAST RULES = List( 
IIntegrate(5881,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),ILtQ(n,C0),IntegerQ(m)))),
IIntegrate(5882,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),ILtQ(n,C0),IntegerQ(m)))),
IIntegrate(5883,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Dist(Times(CN1,k,Power(e,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),ILtQ(n,C0),FractionQ(m)))),
IIntegrate(5884,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Dist(Times(CN1,k,Power(e,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),ILtQ(n,C0),FractionQ(m)))),
IIntegrate(5885,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),ILtQ(n,C0),Not(RationalQ(m))))),
IIntegrate(5886,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),ILtQ(n,C0),Not(RationalQ(m))))),
IIntegrate(5887,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5888,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5889,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5890,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5891,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Plus(m,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5892,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Plus(m,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5893,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5894,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5895,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Plus(c,Times(d,Power(x,n))))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Negate(c),Times(d,Power(x,n))))),x),x),x)),FreeQ(List(c,d,e,m,n),x))),
IIntegrate(5896,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Plus(c,Times(d,Power(x,n))))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Negate(c),Times(d,Power(x,n))))),x),x),x)),FreeQ(List(c,d,e,m,n),x))),
IIntegrate(5897,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(5898,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(5899,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(Coefficient(u,x,C1),Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Subtract(x,Coefficient(u,x,C0)),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x,u),x),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x),IntegerQ(m)))),
IIntegrate(5900,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(Coefficient(u,x,C1),Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Subtract(x,Coefficient(u,x,C0)),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x,u),x),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x),IntegerQ(m))))
  );
}
