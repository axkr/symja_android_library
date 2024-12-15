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
class IntRules225 { 
  public static IAST RULES = List( 
IIntegrate(4501,Integrate(Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Times(Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(4502,Integrate(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Times(Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(4503,Integrate(Power(Sec(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(4504,Integrate(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(4505,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(C2,p),Exp(Times(CI,a,d,p))),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),And(FreeQ(List(a,b,d,e,m),x),IntegerQ(p)))),
IIntegrate(4506,Integrate(Times(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(CN2,CI),p),Exp(Times(CI,a,d,p))),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),And(FreeQ(List(a,b,d,e,m),x),IntegerQ(p)))),
IIntegrate(4507,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),And(FreeQ(List(a,b,d,e,m,p),x),Not(IntegerQ(p))))),
IIntegrate(4508,Integrate(Times(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x),x),And(FreeQ(List(a,b,d,e,m,p),x),Not(IntegerQ(p))))),
IIntegrate(4509,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sec(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(4510,Integrate(Times(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(4511,Integrate(Times(Log(Times(b_DEFAULT,x_)),Sin(Times(Log(Times(b_DEFAULT,x_)),a_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Negate(Simp(Times(Cos(Times(a,x,Log(Times(b,x)))),Power(a,CN1)),x)),Integrate(Sin(Times(a,x,Log(Times(b,x)))),x)),FreeQ(list(a,b),x))),
IIntegrate(4512,Integrate(Times(Cos(Times(Log(Times(b_DEFAULT,x_)),a_DEFAULT,x_)),Log(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Times(a,x,Log(Times(b,x)))),Power(a,CN1)),x),Integrate(Cos(Times(a,x,Log(Times(b,x)))),x)),FreeQ(list(a,b),x))),
IIntegrate(4513,Integrate(Times(Log(Times(b_DEFAULT,x_)),Power(x_,m_DEFAULT),Sin(Times(Log(Times(b_DEFAULT,x_)),a_DEFAULT,Power(x_,n_DEFAULT)))),x_Symbol),
    Condition(Subtract(Negate(Simp(Times(Cos(Times(a,Power(x,n),Log(Times(b,x)))),Power(Times(a,n),CN1)),x)),Dist(Power(n,CN1),Integrate(Times(Power(x,m),Sin(Times(a,Power(x,n),Log(Times(b,x))))),x),x)),And(FreeQ(List(a,b,m,n),x),EqQ(m,Subtract(n,C1))))),
IIntegrate(4514,Integrate(Times(Cos(Times(Log(Times(b_DEFAULT,x_)),a_DEFAULT,Power(x_,n_DEFAULT))),Log(Times(b_DEFAULT,x_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Times(a,Power(x,n),Log(Times(b,x)))),Power(Times(a,n),CN1)),x),Dist(Power(n,CN1),Integrate(Times(Power(x,m),Cos(Times(a,Power(x,n),Log(Times(b,x))))),x),x)),And(FreeQ(List(a,b,m,n),x),EqQ(m,Subtract(n,C1))))),
IIntegrate(4515,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Subtract(Dist(Power(b,CN1),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Sin(Plus(c,Times(d,x))),Subtract(n,C1))),x),x),Dist(Times(a,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Sin(Plus(c,Times(d,x))),Subtract(n,C1)),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(4516,Integrate(Times(Power(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),Power(Plus(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Dist(Power(b,CN1),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Cos(Plus(c,Times(d,x))),Subtract(n,C1))),x),x),Dist(Times(a,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Power(Cos(Plus(c,Times(d,x))),Subtract(n,C1)),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(4517,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(CI,Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Times(b,f,Plus(m,C1)),CN1)),x)),Dist(C2,Integrate(Times(Power(Plus(e,Times(f,x)),m),Exp(Times(CI,Plus(c,Times(d,x)))),Power(Subtract(a,Times(CI,b,Exp(Times(CI,Plus(c,Times(d,x)))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4518,Integrate(Times(Power(Plus(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(CI,Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Times(b,f,Plus(m,C1)),CN1)),x),Dist(Times(C2,CI),Integrate(Times(Power(Plus(e,Times(f,x)),m),Exp(Times(CI,Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Exp(Times(CI,Plus(c,Times(d,x)))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4519,Integrate(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(CI,Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Times(b,f,Plus(m,C1)),CN1)),x)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Exp(Times(CI,Plus(c,Times(d,x)))),Power(Subtract(Subtract(a,Rt(Subtract(Sqr(a),Sqr(b)),C2)),Times(CI,b,Exp(Times(CI,Plus(c,Times(d,x)))))),CN1)),x),Integrate(Times(Power(Plus(e,Times(f,x)),m),Exp(Times(CI,Plus(c,Times(d,x)))),Power(Subtract(Plus(a,Rt(Subtract(Sqr(a),Sqr(b)),C2)),Times(CI,b,Exp(Times(CI,Plus(c,Times(d,x)))))),CN1)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),PosQ(Subtract(Sqr(a),Sqr(b)))))),
IIntegrate(4520,Integrate(Times(Power(Plus(Times(Cos(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sin(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CI,Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Times(b,f,Plus(m,C1)),CN1)),x),Negate(Dist(CI,Integrate(Times(Power(Plus(e,Times(f,x)),m),Exp(Times(CI,Plus(c,Times(d,x)))),Power(Plus(a,Negate(Rt(Subtract(Sqr(a),Sqr(b)),C2)),Times(b,Exp(Times(CI,Plus(c,Times(d,x)))))),CN1)),x),x)),Negate(Dist(CI,Integrate(Times(Power(Plus(e,Times(f,x)),m),Exp(Times(CI,Plus(c,Times(d,x)))),Power(Plus(a,Rt(Subtract(Sqr(a),Sqr(b)),C2),Times(b,Exp(Times(CI,Plus(c,Times(d,x)))))),CN1)),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),PosQ(Subtract(Sqr(a),Sqr(b))))))
  );
}
