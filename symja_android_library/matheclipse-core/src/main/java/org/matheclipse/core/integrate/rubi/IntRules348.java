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
class IntRules348 { 
  public static IAST RULES = List( 
IIntegrate(6961,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Times(e,x),m),Power(Erfc(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6962,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Times(e,x),m),Power(Erfi(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6963,Integrate(Erf(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Erf(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(C2,b,d,n,Power(Pi,CN1D2)),Integrate(Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6964,Integrate(Erfc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Erfc(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(C2,b,d,n,Power(Pi,CN1D2)),Integrate(Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6965,Integrate(Erfi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Erfi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(C2,b,d,n,Power(Pi,CN1D2)),Integrate(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6966,Integrate(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(list(Erf,Erfc,Erfi),FSymbol)))),
IIntegrate(6967,Integrate(Times(Erf(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erf(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(C2,b,d,n,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6968,Integrate(Times(Erfc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erfc(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(C2,b,d,n,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6969,Integrate(Times(Erfi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erfi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(C2,b,d,n,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),m),Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6970,Integrate(Times(Erf(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Exp(Subtract(Times(CNI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6971,Integrate(Times(Erfc(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Exp(Subtract(Times(CNI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6972,Integrate(Times(Erfi(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Exp(Subtract(Times(CNI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6973,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Times(Exp(Subtract(Times(CNI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6974,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Times(Exp(Subtract(Times(CNI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6975,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Times(Exp(Subtract(Times(CNI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6976,Integrate(Times(Erf(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erf(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6977,Integrate(Times(Erfc(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Times(b,x))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfc(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6978,Integrate(Times(Erfi(Times(b_DEFAULT,x_)),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Times(b,x))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfi(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6979,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erf(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4))))),
IIntegrate(6980,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Times(b,x))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Exp(Subtract(Negate(c),Times(d,Sqr(x)))),Erfc(Times(b,x))),x),x),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Power(b,C4)))))
  );
}
