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
class IntRules347 { 
  public static IAST RULES = List( 
IIntegrate(6941,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Negate(Simp(Star(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Integrate(Times(Power(x,Subtract(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x)))),x)),x)),Negate(Simp(Star(Times(b,Power(Times(d,CSqrtPi),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Exp(Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),d),Sqr(x))))),x)),x))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C1)))),
IIntegrate(6942,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,Exp(c),x,Power(Pi,CN1D2),HypergeometricPFQ(list(C1D2,C1),list(QQ(3L,2L),QQ(3L,2L)),Times(Sqr(b),Sqr(x)))),x),And(FreeQ(list(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6943,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(x,CN1)),x),Integrate(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x)),Power(x,CN1)),x)),And(FreeQ(list(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6944,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,Exp(c),x,Power(Pi,CN1D2),HypergeometricPFQ(list(C1D2,C1),list(QQ(3L,2L),QQ(3L,2L)),Times(CN1,Sqr(b),Sqr(x)))),x),And(FreeQ(list(b,c,d),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6945,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Star(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x)))),x)),x)),Negate(Simp(Star(Times(C2,b,Power(Times(Plus(m,C1),CSqrtPi),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x)),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(6946,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Star(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x)))),x)),x)),Simp(Star(Times(C2,b,Power(Times(Plus(m,C1),CSqrtPi),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x)),x)),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(6947,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Star(Times(C2,d,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x)))),x)),x)),Negate(Simp(Star(Times(C2,b,Power(Times(Plus(m,C1),CSqrtPi),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Exp(Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),d),Sqr(x))))),x)),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(6948,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Times(e,x),m),Power(Erf(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6949,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Times(e,x),m),Power(Erfc(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6950,Integrate(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Times(e,x),m),Power(Erfi(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6951,Integrate(Erf(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Erf(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Star(Times(C2,b,d,n,Power(Pi,CN1D2)),Integrate(Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6952,Integrate(Erfc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Erfc(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Star(Times(C2,b,d,n,Power(Pi,CN1D2)),Integrate(Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6953,Integrate(Erfi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Erfi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Star(Times(C2,b,d,n,Power(Pi,CN1D2)),Integrate(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6954,Integrate(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n))))),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(list(Erf,Erfc,Erfi),FSymbol)))),
IIntegrate(6955,Integrate(Times(Erf(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erf(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,b,d,n,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6956,Integrate(Times(Erfc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erfc(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,b,d,n,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6957,Integrate(Times(Erfi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Erfi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(C2,b,d,n,Power(Times(CSqrtPi,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(e,x),m),Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6958,Integrate(Times(Erf(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erf(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6959,Integrate(Times(Erfc(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfc(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4)))))),
IIntegrate(6960,Integrate(Times(Erfi(Times(b_DEFAULT,x_)),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Exp(Subtract(Times(CN1,CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Exp(Plus(Times(CI,c),Times(CI,d,Sqr(x)))),Erfi(Times(b,x))),x)),x)),And(FreeQ(list(b,c,d),x),EqQ(Sqr(d),Negate(Power(b,C4))))))
  );
}
