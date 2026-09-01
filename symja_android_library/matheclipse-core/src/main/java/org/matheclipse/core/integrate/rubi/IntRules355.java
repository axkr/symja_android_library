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
class IntRules355 { 
  public static IAST RULES = List( 
IIntegrate(7101,Integrate(Sqr(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(CoshIntegral(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Dist(C2,Integrate(Times(Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(a,Times(b,x)))),x),x),x)),FreeQ(list(a,b),x))),
IIntegrate(7102,Integrate(Times(Power(x_,m_DEFAULT),Sqr(SinhIntegral(Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(SinhIntegral(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Sinh(Times(b,x)),SinhIntegral(Times(b,x))),x),x),x)),And(FreeQ(b,x),IGtQ(m,C0)))),
IIntegrate(7103,Integrate(Times(Sqr(CoshIntegral(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(CoshIntegral(Times(b,x))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Cosh(Times(b,x)),CoshIntegral(Times(b,x))),x),x),x)),And(FreeQ(b,x),IGtQ(m,C0)))),
IIntegrate(7104,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sqr(SinhIntegral(Plus(a_,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),m),Sqr(SinhIntegral(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),CN1)),x),Negate(Simp(Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(a,Times(b,x)))),x),x),x)),Simp(Dist(Times(Subtract(Times(b,c),Times(a,d)),m,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Sqr(SinhIntegral(Plus(a,Times(b,x))))),x),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(7105,Integrate(Times(Sqr(CoshIntegral(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),m),Sqr(CoshIntegral(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),CN1)),x),Negate(Simp(Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(a,Times(b,x)))),x),x),x)),Simp(Dist(Times(Subtract(Times(b,c),Times(a,d)),m,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Sqr(CoshIntegral(Plus(a,Times(b,x))))),x),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(7106,Integrate(Times(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Cosh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(7107,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Sinh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(7108,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),Negate(Simp(Dist(Times(f,m,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7109,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),Negate(Simp(Dist(Times(f,m,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7110,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x),x),x)),Negate(Simp(Dist(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7111,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x),x),x)),Negate(Simp(Dist(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7112,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Sinh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(7113,Integrate(Times(CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Cosh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(7114,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Sinh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),Negate(Simp(Dist(Times(f,m,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7115,Integrate(Times(CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Cosh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),Negate(Simp(Dist(Times(f,m,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7116,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x),x),x)),Negate(Simp(Dist(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7117,Integrate(Times(CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x),x),x)),Negate(Simp(Dist(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7118,Integrate(SinhIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,SinhIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(b,d,n),Integrate(Times(Sinh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7119,Integrate(CoshIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,CoshIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(b,d,n),Integrate(Times(Cosh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7120,Integrate(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(list(SinhIntegral,CoshIntegral),x))))
  );
}
