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
class IntRules251 { 
  public static IAST RULES = List( 
IIntegrate(5021,Integrate(Times(Power(Cot(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Plus(Times(Plus(m,C1),Power(n,CN1)),CN1)),Power(Cot(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5022,Integrate(Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(Power(C2,p),Exp(Times(CI,a,d,p))),Integrate(Times(Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),x),And(FreeQ(list(a,b,d),x),IntegerQ(p)))),
IIntegrate(5023,Integrate(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(Power(CC(0L,1L,-2L,1L),p),Exp(Times(CI,a,d,p))),Integrate(Times(Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),x),And(FreeQ(list(a,b,d),x),IntegerQ(p)))),
IIntegrate(5024,Integrate(Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(5025,Integrate(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(5026,Integrate(Power(Sec(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5027,Integrate(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5028,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(C2,p),Exp(Times(CI,a,d,p))),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),x),And(FreeQ(List(a,b,d,e,m),x),IntegerQ(p)))),
IIntegrate(5029,Integrate(Times(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(CC(0L,1L,-2L,1L),p),Exp(Times(CI,a,d,p))),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),x),And(FreeQ(List(a,b,d,e,m),x),IntegerQ(p)))),
IIntegrate(5030,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),x),And(FreeQ(List(a,b,d,e,m,p),x),Not(IntegerQ(p))))),
IIntegrate(5031,Integrate(Times(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),x),And(FreeQ(List(a,b,d,e,m,p),x),Not(IntegerQ(p))))),
IIntegrate(5032,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sec(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Plus(Times(Plus(m,C1),Power(n,CN1)),CN1)),Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5033,Integrate(Times(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Plus(Times(Plus(m,C1),Power(n,CN1)),CN1)),Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5034,Integrate(Times(Log(Times(b_DEFAULT,x_)),Sin(Times(Log(Times(b_DEFAULT,x_)),a_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Cos(Times(a,x,Log(Times(b,x)))),Power(a,CN1)),x),Integrate(Sin(Times(a,x,Log(Times(b,x)))),x)),FreeQ(list(a,b),x))),
IIntegrate(5035,Integrate(Times(Cos(Times(Log(Times(b_DEFAULT,x_)),a_DEFAULT,x_)),Log(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Times(a,x,Log(Times(b,x)))),Power(a,CN1)),x),Integrate(Cos(Times(a,x,Log(Times(b,x)))),x)),FreeQ(list(a,b),x))),
IIntegrate(5036,Integrate(Times(Log(Times(b_DEFAULT,x_)),Power(x_,m_DEFAULT),Sin(Times(Log(Times(b_DEFAULT,x_)),a_DEFAULT,Power(x_,n_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Cos(Times(a,Power(x,n),Log(Times(b,x)))),Power(Times(a,n),CN1)),x),Simp(Dist(Power(n,CN1),Integrate(Times(Power(x,m),Sin(Times(a,Power(x,n),Log(Times(b,x))))),x),x),x)),And(FreeQ(List(a,b,m,n),x),EqQ(m,Plus(n,CN1))))),
IIntegrate(5037,Integrate(Times(Cos(Times(Log(Times(b_DEFAULT,x_)),a_DEFAULT,Power(x_,n_DEFAULT))),Log(Times(b_DEFAULT,x_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Times(a,Power(x,n),Log(Times(b,x)))),Power(Times(a,n),CN1)),x),Simp(Dist(Power(n,CN1),Integrate(Times(Power(x,m),Cos(Times(a,Power(x,n),Log(Times(b,x))))),x),x),x)),And(FreeQ(List(a,b,m,n),x),EqQ(m,Plus(n,CN1))))),
IIntegrate(5038,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(Power(b,CN1),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Sin(Plus(c,Times(d,x))),Plus(n,CN1))),x),x),x),Simp(Dist(Times(a,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Sin(Plus(c,Times(d,x))),Plus(n,CN1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(5039,Integrate(Times(Power(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power(Plus(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Dist(Power(b,CN1),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Cos(Plus(c,Times(d,x))),Plus(n,CN1))),x),x),x),Simp(Dist(Times(a,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Cos(Plus(c,Times(d,x))),Plus(n,CN1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(5040,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CNI,Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Times(b,f,Plus(m,C1)),CN1)),x),Simp(Dist(C2,Integrate(Times(Power(Plus(e,Times(f,x)),m),Exp(Times(CI,Plus(c,Times(d,x)))),Power(Subtract(a,Times(CI,b,Exp(Times(CI,Plus(c,Times(d,x)))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Subtract(Sqr(a),Sqr(b)),C0))))
  );
}
