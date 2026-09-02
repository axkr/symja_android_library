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
class IntRules351 { 
  public static IAST RULES = List( 
IIntegrate(7021,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,CN1)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x),Negate(Simp(Dist(Times(Plus(m,CN1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Plus(m,CN2)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),x)),Negate(Simp(Dist(Times(b,Power(Times(C4,d),CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Sin(Times(C2,d,Sqr(x)))),x),x),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(7022,Integrate(Times(FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Plus(m,C1),CN1)),x),Negate(Simp(Times(d,Power(x,Plus(m,C2)),Power(Times(Pi,b,Plus(m,C1),Plus(m,C2)),CN1)),x)),Negate(Simp(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),x)),Simp(Dist(Times(d,Power(Times(Pi,b,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Cos(Times(C2,d,Sqr(x)))),x),x),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN2)))),
IIntegrate(7023,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Plus(m,C1),CN1)),x),Negate(Simp(Times(b,Power(x,Plus(m,C2)),Power(Times(C2,Plus(m,C1),Plus(m,C2)),CN1)),x)),Simp(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),x),Negate(Simp(Dist(Times(b,Power(Times(C2,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Cos(Times(C2,d,Sqr(x)))),x),x),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN2)))),
IIntegrate(7024,Integrate(Times(Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(FresnelS(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(7025,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelC(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(7026,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Dist(Power(Times(Pi,b),CN1),Integrate(Sqr(Sin(Times(d,Sqr(x)))),x),x),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7027,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),x_,Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Dist(Times(b,Power(Times(C2,d),CN1)),Integrate(Sqr(Cos(Times(d,Sqr(x)))),x),x),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4)))))),
IIntegrate(7028,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,CN1)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Times(C2,d),CN1)),x),Negate(Simp(Dist(Power(Times(Pi,b),CN1),Integrate(Times(Power(x,Plus(m,CN1)),Sqr(Sin(Times(d,Sqr(x))))),x),x),x)),Negate(Simp(Dist(Times(Plus(m,CN1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Plus(m,CN2)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(7029,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,CN1)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Times(C2,d),CN1)),x),Simp(Dist(Times(Plus(m,CN1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Plus(m,CN2)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),x),Simp(Dist(Times(b,Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Plus(m,CN1)),Sqr(Cos(Times(d,Sqr(x))))),x),x),x)),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),IGtQ(m,C1)))),
IIntegrate(7030,Integrate(Times(Cos(Times(d_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Cos(Times(d,Sqr(x))),FresnelS(Times(b,x)),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Sin(Times(d,Sqr(x))),FresnelS(Times(b,x))),x),x),x),Negate(Simp(Dist(Times(d,Power(Times(Pi,b,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Sin(Times(C2,d,Sqr(x)))),x),x),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN1)))),
IIntegrate(7031,Integrate(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,m_),Sin(Times(d_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Sin(Times(d,Sqr(x))),FresnelC(Times(b,x)),Power(Plus(m,C1),CN1)),x),Negate(Simp(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Cos(Times(d,Sqr(x))),FresnelC(Times(b,x))),x),x),x)),Negate(Simp(Dist(Times(b,Power(Times(C2,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Sin(Times(C2,d,Sqr(x)))),x),x),x))),And(FreeQ(list(b,d),x),EqQ(Sqr(d),Times(C1D4,Sqr(Pi),Power(b,C4))),ILtQ(m,CN1)))),
IIntegrate(7032,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Cos(Plus(c,Times(d,Sqr(x)))),Power(FresnelS(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(7033,Integrate(Times(Power(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(FresnelC(Plus(a,Times(b,x))),n),Sin(Plus(c,Times(d,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(7034,Integrate(FresnelS(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,FresnelS(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(b,d,n),Integrate(Sin(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7035,Integrate(FresnelC(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,FresnelC(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(b,d,n),Integrate(Cos(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7036,Integrate(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(list(FresnelS,FresnelC),FSymbol)))),
IIntegrate(7037,Integrate(Times(FresnelS(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),FresnelS(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Sin(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))))),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7038,Integrate(Times(FresnelC(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),FresnelC(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Cos(Times(C1D2,Pi,Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))))),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7039,Integrate(ExpIntegralE(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(CN1,ExpIntegralE(Plus(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x),FreeQ(list(a,b,n),x))),
IIntegrate(7040,Integrate(Times(ExpIntegralE(n_,Times(b_DEFAULT,x_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,m),ExpIntegralE(Plus(n,C1),Times(b,x)),Power(b,CN1)),x),Simp(Dist(Times(m,Power(b,CN1)),Integrate(Times(Power(x,Plus(m,CN1)),ExpIntegralE(Plus(n,C1),Times(b,x))),x),x),x)),And(FreeQ(b,x),EqQ(Plus(m,n),C0),IGtQ(m,C0))))
  );
}
