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
class IntRules267 { 
  public static IAST RULES = List( 
IIntegrate(5341,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C1),IGtQ(n,C0)))),
IIntegrate(5342,Integrate(Times(Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,n),Cosh(Plus(a,Times(b,Power(x,n)))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Integrate(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x)),Negate(Simp(Times(n,Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x))),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),LtQ(p,CN1),NeQ(p,CN2)))),
IIntegrate(5343,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,n),Sinh(Plus(a,Times(b,Power(x,n)))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x)),Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Integrate(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Simp(Times(n,Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x)),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),LtQ(p,CN1),NeQ(p,CN2)))),
IIntegrate(5344,Integrate(Times(Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Cosh(Plus(a,Times(b,Power(x,n)))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Integrate(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x)),Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),Integrate(Times(Power(x,Subtract(m,Times(C2,n))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Negate(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x))),And(FreeQ(List(a,b),x),IntegersQ(m,n),LtQ(p,CN1),NeQ(p,CN2),LtQ(C0,Times(C2,n),Plus(m,C1))))),
IIntegrate(5345,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Sinh(Plus(a,Times(b,Power(x,n)))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x)),Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Integrate(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Negate(Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),Integrate(Times(Power(x,Subtract(m,Times(C2,n))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x)),Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x)),And(FreeQ(List(a,b),x),IntegersQ(m,n),LtQ(p,CN1),NeQ(p,CN2),LtQ(C0,Times(C2,n),Plus(m,C1))))),
IIntegrate(5346,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),ILtQ(n,C0),IntegerQ(m)))),
IIntegrate(5347,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),ILtQ(n,C0),IntegerQ(m)))),
IIntegrate(5348,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Negate(Dist(Times(k,Power(e,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),ILtQ(n,C0),FractionQ(m)))),
IIntegrate(5349,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Negate(Dist(Times(k,Power(e,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),ILtQ(n,C0),FractionQ(m)))),
IIntegrate(5350,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Times(Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),ILtQ(n,C0),Not(RationalQ(m))))),
IIntegrate(5351,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),ILtQ(n,C0),Not(RationalQ(m))))),
IIntegrate(5352,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Dist(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5353,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Dist(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5354,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5355,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(5356,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Plus(m,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1))),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5357,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Plus(m,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1))),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5358,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5359,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5360,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Plus(c,Times(d,Power(x,n))))),x),x),Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Negate(c),Times(d,Power(x,n))))),x),x)),FreeQ(List(c,d,e,m,n),x)))
  );
}
