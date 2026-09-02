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
class IntRules354 { 
  public static IAST RULES = List( 
IIntegrate(7081,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x)))),x),x),x)),Negate(Simp(Dist(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7082,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x)))),x),x),x),Negate(Simp(Dist(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7083,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Sin(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(7084,Integrate(Times(CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Cos(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(7085,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),m),Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Negate(Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Sin(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),Negate(Simp(Dist(Times(f,m,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x)))),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7086,Integrate(Times(CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(e,Times(f,x)),m),Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(b,CN1)),x),Simp(Dist(Times(d,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),m),Cos(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x),Simp(Dist(Times(f,m,Power(b,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,CN1)),Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x)))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7087,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x)))),x),x),x),Negate(Simp(Dist(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7088,Integrate(Times(CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Dist(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x)))),x),x),x)),Negate(Simp(Dist(Times(d,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),ILtQ(m,CN1)))),
IIntegrate(7089,Integrate(SinIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,SinIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(b,d,n),Integrate(Times(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7090,Integrate(CosIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,CosIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(b,d,n),Integrate(Times(Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(7091,Integrate(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(list(SinIntegral,CosIntegral),x)))),
IIntegrate(7092,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),SinIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),SinIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7093,Integrate(Times(CosIntegral(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),CosIntegral(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),NeQ(m,CN1)))),
IIntegrate(7094,Integrate(SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),SinhIntegral(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Cosh(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(7095,Integrate(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),CoshIntegral(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Times(Sinh(Plus(a,Times(b,x))),Power(b,CN1)),x)),FreeQ(list(a,b),x))),
IIntegrate(7096,Integrate(Times(Power(x_,CN1),SinhIntegral(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(C1D2,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x),Simp(Times(C1D2,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(b,x))),x)),FreeQ(b,x))),
IIntegrate(7097,Integrate(Times(CoshIntegral(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CN1D2,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(CN1,b,x))),x),Simp(Times(C1D2,b,x,HypergeometricPFQ(list(C1,C1,C1),list(C2,C2,C2),Times(b,x))),x),Simp(Times(EulerGamma,Log(x)),x),Simp(Times(C1D2,Sqr(Log(Times(b,x)))),x)),FreeQ(b,x))),
IIntegrate(7098,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),SinhIntegral(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(7099,Integrate(Times(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),CoshIntegral(Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(7100,Integrate(Sqr(SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Sqr(SinhIntegral(Plus(a,Times(b,x)))),Power(b,CN1)),x),Simp(Dist(C2,Integrate(Times(Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(a,Times(b,x)))),x),x),x)),FreeQ(list(a,b),x)))
  );
}
