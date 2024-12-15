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
class IntRules326 { 
  public static IAST RULES = List( 
IIntegrate(6521,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x)))),x),x),Negate(Dist(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(6522,Integrate(Times(CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x)))),x),x)),Negate(Dist(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(6523,Integrate(SinIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,SinIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(b,d,n),Integrate(Times(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6524,Integrate(CosIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,CosIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(b,d,n),Integrate(Times(Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6525,Integrate(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(list(SinIntegral,CosIntegral),x)))),
IIntegrate(6526,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),SinIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),SinIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6527,Integrate(Times(CosIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),CosIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(6528,Integrate(SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),SinhIntegral(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Cosh(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6529,Integrate(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),CoshIntegral(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Sinh(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(6530,Integrate(Times(Power(x_,CN1),SinhIntegral(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(C1D2,C1,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x),Simp(Times(C1D2,C1,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(b,x))),x)),FreeQ(b,x))),
IIntegrate(6531,Integrate(Times(CoshIntegral(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(C1D2,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x)),Simp(Times(C1D2,C1,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(b,x))),x),Simp(Times(EulerGamma,Log(x)),x),Simp(Times(C1D2,C1,Sqr(Log(Times(b,x)))),x)),FreeQ(b,x))),
IIntegrate(6532,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),SinhIntegral(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6533,Integrate(Times(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),CoshIntegral(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6534,Integrate(Sqr(SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(SinhIntegral(Plus(a,Times(b,x)))),Power(b,CN1)),x),Dist(C2,Integrate(Times(Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(a,Times(b,x)))),x),x)),FreeQ(list(a,b),x))),
IIntegrate(6535,Integrate(Sqr(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(CoshIntegral(Plus(a,Times(b,x)))),Power(b,CN1)),x),Dist(C2,Integrate(Times(Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(a,Times(b,x)))),x),x)),FreeQ(list(a,b),x))),
IIntegrate(6536,Integrate(Times(Power(x_,m_DEFAULT),Sqr(SinhIntegral(Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(SinhIntegral(Times(b,x))),Power(Plus(m,C1),CN1)),x),Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Sinh(Times(b,x)),SinhIntegral(Times(b,x))),x),x)),And(FreeQ(b,x),IGtQ(m,C0)))),
IIntegrate(6537,Integrate(Times(Sqr(CoshIntegral(Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Sqr(CoshIntegral(Times(b,x))),Power(Plus(m,C1),CN1)),x),Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Cosh(Times(b,x)),CoshIntegral(Times(b,x))),x),x)),And(FreeQ(b,x),IGtQ(m,C0)))),
IIntegrate(6538,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sqr(SinhIntegral(Plus(a_,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),m),Sqr(SinhIntegral(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),CN1)),x),Negate(Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(a,Times(b,x)))),x),x)),Dist(Times(Subtract(Times(b,c),Times(a,d)),m,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Sqr(SinhIntegral(Plus(a,Times(b,x))))),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6539,Integrate(Times(Sqr(CoshIntegral(Plus(a_,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),m),Sqr(CoshIntegral(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),CN1)),x),Negate(Dist(Times(C2,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(a,Times(b,x)))),x),x)),Dist(Times(Subtract(Times(b,c),Times(a,d)),m,Power(Times(b,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Sqr(CoshIntegral(Plus(a,Times(b,x))))),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6540,Integrate(Times(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Dist(Times(d,Power(b,CN1)),Integrate(Times(Cosh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x)),FreeQ(List(a,b,c,d),x)))
  );
}
