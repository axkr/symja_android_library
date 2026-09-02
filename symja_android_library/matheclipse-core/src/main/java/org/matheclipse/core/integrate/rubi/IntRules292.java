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
class IntRules292 { 
  public static IAST RULES = List( 
IIntegrate(5841,Integrate(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Exp(Plus(c,Times(d,Power(x,n)))),x),x),x),Simp(Dist(C1D2,Integrate(Exp(Subtract(Negate(c),Times(d,Power(x,n)))),x),x),x)),FreeQ(list(c,d,n),x))),
IIntegrate(5842,Integrate(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Exp(Plus(c,Times(d,Power(x,n)))),x),x),x),Simp(Dist(C1D2,Integrate(Exp(Subtract(Negate(c),Times(d,Power(x,n)))),x),x),x)),FreeQ(list(c,d,n),x))),
IIntegrate(5843,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0)))),
IIntegrate(5844,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0)))),
IIntegrate(5845,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x,u),x),x),And(FreeQ(List(a,b,c,d,n),x),IntegerQ(p),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(5846,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x,u),x),x),And(FreeQ(List(a,b,c,d,n),x),IntegerQ(p),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(5847,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(u,n)))))),p),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x)))),
IIntegrate(5848,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(u,n)))))),p),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x)))),
IIntegrate(5849,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(u_))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Sinh(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5850,Integrate(Power(Plus(a_DEFAULT,Times(Cosh(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Cosh(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5851,Integrate(Times(Power(x_,CN1),Sinh(Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Simp(Times(SinhIntegral(Times(d,Power(x,n))),Power(n,CN1)),x),FreeQ(list(d,n),x))),
IIntegrate(5852,Integrate(Times(Cosh(Times(d_DEFAULT,Power(x_,n_))),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(CoshIntegral(Times(d,Power(x,n))),Power(n,CN1)),x),FreeQ(list(d,n),x))),
IIntegrate(5853,Integrate(Times(Power(x_,CN1),Sinh(Plus(c_,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Plus(Simp(Dist(Sinh(c),Integrate(Times(Cosh(Times(d,Power(x,n))),Power(x,CN1)),x),x),x),Simp(Dist(Cosh(c),Integrate(Times(Sinh(Times(d,Power(x,n))),Power(x,CN1)),x),x),x)),FreeQ(list(c,d,n),x))),
IIntegrate(5854,Integrate(Times(Cosh(Plus(c_,Times(d_DEFAULT,Power(x_,n_)))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Dist(Cosh(c),Integrate(Times(Cosh(Times(d,Power(x,n))),Power(x,CN1)),x),x),x),Simp(Dist(Sinh(c),Integrate(Times(Sinh(Times(d,Power(x,n))),Power(x,CN1)),x),x),x)),FreeQ(list(c,d,n),x))),
IIntegrate(5855,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(EqQ(p,C1),EqQ(m,Plus(n,CN1)),And(IntegerQ(p),GtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0)))))),
IIntegrate(5856,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(EqQ(p,C1),EqQ(m,Plus(n,CN1)),And(IntegerQ(p),GtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0)))))),
IIntegrate(5857,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(5858,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1))))))),
IIntegrate(5859,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,Plus(n,CN1)),Power(Times(e,x),Plus(m,Negate(n),C1)),Cosh(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Simp(Dist(Times(Power(e,n),Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Integrate(Times(Power(Times(e,x),Subtract(m,n)),Cosh(Plus(c,Times(d,Power(x,n))))),x),x),x)),And(FreeQ(list(c,d,e),x),IGtQ(n,C0),LtQ(C0,n,Plus(m,C1))))),
IIntegrate(5860,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,Plus(n,CN1)),Power(Times(e,x),Plus(m,Negate(n),C1)),Sinh(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Simp(Dist(Times(Power(e,n),Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Integrate(Times(Power(Times(e,x),Subtract(m,n)),Sinh(Plus(c,Times(d,Power(x,n))))),x),x),x)),And(FreeQ(list(c,d,e),x),IGtQ(n,C0),LtQ(C0,n,Plus(m,C1)))))
  );
}
