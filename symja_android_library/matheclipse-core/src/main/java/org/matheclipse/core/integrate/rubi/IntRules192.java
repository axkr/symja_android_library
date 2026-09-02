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
class IntRules192 { 
  public static IAST RULES = List( 
IIntegrate(3841,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Integrate(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Cos(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,C0),ILtQ(n,C0)))),
IIntegrate(3842,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Sin(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(3843,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Cos(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(3844,Integrate(Sin(Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(Sqrt(CPiHalf),Power(Times(f,Rt(d,C2)),CN1),FresnelS(Times(Sqrt(Times(C2,Power(Pi,CN1))),Rt(d,C2),Plus(e,Times(f,x))))),x),FreeQ(list(d,e,f),x))),
IIntegrate(3845,Integrate(Cos(Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(Sqrt(CPiHalf),Power(Times(f,Rt(d,C2)),CN1),FresnelC(Times(Sqrt(Times(C2,Power(Pi,CN1))),Rt(d,C2),Plus(e,Times(f,x))))),x),FreeQ(list(d,e,f),x))),
IIntegrate(3846,Integrate(Sin(Plus(c_,Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Dist(Sin(c),Integrate(Cos(Times(d,Sqr(Plus(e,Times(f,x))))),x),x),x),Simp(Dist(Cos(c),Integrate(Sin(Times(d,Sqr(Plus(e,Times(f,x))))),x),x),x)),FreeQ(List(c,d,e,f),x))),
IIntegrate(3847,Integrate(Cos(Plus(c_,Times(d_DEFAULT,Sqr(Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Dist(Cos(c),Integrate(Cos(Times(d,Sqr(Plus(e,Times(f,x))))),x),x),x),Simp(Dist(Sin(c),Integrate(Sin(Times(d,Sqr(Plus(e,Times(f,x))))),x),x),x)),FreeQ(List(c,d,e,f),x))),
IIntegrate(3848,Integrate(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),x)),And(FreeQ(List(c,d,e,f),x),IGtQ(n,C2)))),
IIntegrate(3849,Integrate(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),x),Simp(Dist(C1D2,Integrate(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),x)),And(FreeQ(List(c,d,e,f),x),IGtQ(n,C2)))),
IIntegrate(3850,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C1),IGtQ(n,C1)))),
IIntegrate(3851,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C1),IGtQ(n,C1)))),
IIntegrate(3852,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Negate(Power(f,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(x,CN2)),x),x,Power(Plus(e,Times(f,x)),CN1)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(n,C0),EqQ(n,CN2)))),
IIntegrate(3853,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Negate(Power(f,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(x,CN2)),x),x,Power(Plus(e,Times(f,x)),CN1)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(n,C0),EqQ(n,CN2)))),
IIntegrate(3854,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(Times(n,f),CN1),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),p)),x),x,Power(Plus(e,Times(f,x)),n)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(Power(n,CN1))))),
IIntegrate(3855,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(Times(n,f),CN1),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),p)),x),x,Power(Plus(e,Times(f,x)),n)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(Power(n,CN1))))),
IIntegrate(3856,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Dist(Times(k,Power(f,CN1)),Subst(Integrate(Times(Power(x,Plus(k,CN1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),FractionQ(n)))),
IIntegrate(3857,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Dist(Times(k,Power(f,CN1)),Subst(Integrate(Times(Power(x,Plus(k,CN1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),FractionQ(n)))),
IIntegrate(3858,Integrate(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),x)),FreeQ(List(c,d,e,f,n),x))),
IIntegrate(3859,Integrate(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),x),Simp(Dist(C1D2,Integrate(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x),x),x)),FreeQ(List(c,d,e,f,n),x))),
IIntegrate(3860,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(p,C1))))
  );
}
