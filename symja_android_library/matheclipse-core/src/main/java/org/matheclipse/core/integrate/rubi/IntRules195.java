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
class IntRules195 { 
  public static IAST RULES = List( 
IIntegrate(3901,Integrate(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Sin(Plus(a,Times(b,Power(x,n)))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x)),Simp(Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Integrate(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),x),Simp(Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),Integrate(Times(Power(x,Subtract(m,Times(C2,n))),Power(Cos(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),x)),And(FreeQ(list(a,b),x),LtQ(p,CN1),NeQ(p,CN2),IGtQ(n,C0),IGtQ(m,Plus(Times(C2,n),CN1))))),
IIntegrate(3902,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0),ILtQ(n,C0),IntegerQ(m),EqQ(n,CN2)))),
IIntegrate(3903,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0),ILtQ(n,C0),IntegerQ(m),EqQ(n,CN2)))),
IIntegrate(3904,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Dist(Times(CN1,k,Power(e,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),ILtQ(n,C0),FractionQ(m)))),
IIntegrate(3905,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Dist(Times(CN1,k,Power(e,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e),x),IGtQ(p,C0),ILtQ(n,C0),FractionQ(m)))),
IIntegrate(3906,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C0),ILtQ(n,C0),Not(RationalQ(m))))),
IIntegrate(3907,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C0),ILtQ(n,C0),Not(RationalQ(m))))),
IIntegrate(3908,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(3909,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(3910,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(3911,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),FractionQ(n)))),
IIntegrate(3912,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Plus(m,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(3913,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Plus(m,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Simplify(Times(n,Power(Plus(m,C1),CN1))))))))),p),x),x,Power(x,Plus(m,C1))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(3914,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(3915,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(3916,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(x,n))))),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Power(Times(e,x),m),Exp(Plus(Times(c,CI),Times(d,CI,Power(x,n))))),x),x),x)),FreeQ(List(c,d,e,m,n),x))),
IIntegrate(3917,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(x,n))))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Plus(Times(c,CI),Times(d,CI,Power(x,n))))),x),x),x)),FreeQ(List(c,d,e,m,n),x))),
IIntegrate(3918,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(3919,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(3920,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,e,m,n,p),x)))
  );
}
