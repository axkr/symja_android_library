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
class IntRules192 { 
  public static IAST RULES = List( 
IIntegrate(3841,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Negate(Power(f,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(x,CN2)),x),x,Power(Plus(e,Times(f,x)),CN1))),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(n,C0),EqQ(n,CN2)))),
IIntegrate(3842,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Times(n,f),CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),p)),x),x,Power(Plus(e,Times(f,x)),n))),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(Power(n,CN1))))),
IIntegrate(3843,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Times(n,f),CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),p)),x),x,Power(Plus(e,Times(f,x)),n))),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(Power(n,CN1))))),
IIntegrate(3844,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_DEFAULT),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Star(Times(k,Power(f,CN1)),Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),FractionQ(n)))),
IIntegrate(3845,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Module(list(Set(k,Denominator(n))),Simp(Star(Times(k,Power(f,CN1)),Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(Plus(e,Times(f,x)),Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),FractionQ(n)))),
IIntegrate(3846,Integrate(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x)),x)),FreeQ(List(c,d,e,f,n),x))),
IIntegrate(3847,Integrate(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Exp(Subtract(Times(CN1,c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x)),x),Simp(Star(C1D2,Integrate(Exp(Plus(Times(c,CI),Times(d,CI,Power(Plus(e,Times(f,x)),n)))),x)),x)),FreeQ(List(c,d,e,f,n),x))),
IIntegrate(3848,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(p,C1)))),
IIntegrate(3849,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(p,C1)))),
IIntegrate(3850,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))))),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),FreeQ(List(a,b,c,d,e,f,n,p),x))),
IIntegrate(3851,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(Plus(e,Times(f,x)),n)))))),p),x),FreeQ(List(a,b,c,d,e,f,n,p),x))),
IIntegrate(3852,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Sin(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),Not(LinearMatchQ(u,x))))),
IIntegrate(3853,Integrate(Power(Plus(a_DEFAULT,Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Cos(Plus(c,Times(d,Power(ExpandToSum(u,x),n)))))),p),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),Not(LinearMatchQ(u,x))))),
IIntegrate(3854,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(u_))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Sin(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(3855,Integrate(Power(Plus(a_DEFAULT,Times(Cos(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Cos(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(3856,Integrate(Times(Power(x_,CN1),Sin(Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Simp(Times(SinIntegral(Times(d,Power(x,n))),Power(n,CN1)),x),FreeQ(list(d,n),x))),
IIntegrate(3857,Integrate(Times(Cos(Times(d_DEFAULT,Power(x_,n_))),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(CosIntegral(Times(d,Power(x,n))),Power(n,CN1)),x),FreeQ(list(d,n),x))),
IIntegrate(3858,Integrate(Times(Power(x_,CN1),Sin(Plus(c_,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Plus(Simp(Star(Sin(c),Integrate(Times(Cos(Times(d,Power(x,n))),Power(x,CN1)),x)),x),Simp(Star(Cos(c),Integrate(Times(Sin(Times(d,Power(x,n))),Power(x,CN1)),x)),x)),FreeQ(list(c,d,n),x))),
IIntegrate(3859,Integrate(Times(Cos(Plus(c_,Times(d_DEFAULT,Power(x_,n_)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Cos(c),Integrate(Times(Cos(Times(d,Power(x,n))),Power(x,CN1)),x)),x),Simp(Star(Sin(c),Integrate(Times(Sin(Times(d,Power(x,n))),Power(x,CN1)),x)),x)),FreeQ(list(c,d,n),x))),
IIntegrate(3860,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(EqQ(p,C1),EqQ(m,Subtract(n,C1)),And(IntegerQ(p),GtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0))))))
  );
}
