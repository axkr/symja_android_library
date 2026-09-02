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
class IntRules359 { 
  public static IAST RULES = List( 
IIntegrate(7181,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Plus(a_,Times(b_DEFAULT,x_)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Power(Times(c,ProductLog(x)),p),Power(Plus(Times(b,e),Times(CN1,a,f),Times(f,x)),m),x),x),x,Plus(a,Times(b,x))),x),x),And(FreeQ(List(a,b,c,e,f,p),x),IGtQ(m,C0)))),
IIntegrate(7182,Integrate(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_)))),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Times(c,ProductLog(Times(a,Power(x,n)))),p)),x),Simp(Dist(Times(n,p),Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Plus(C1,ProductLog(Times(a,Power(x,n)))),CN1)),x),x),x)),And(FreeQ(List(a,c,n,p),x),Or(EqQ(Times(n,Plus(p,CN1)),CN1),And(IntegerQ(Plus(p,CN1D2)),EqQ(Times(n,Plus(p,CN1D2)),CN1)))))),
IIntegrate(7183,Integrate(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_)))),p_DEFAULT),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Plus(Times(n,p),C1),CN1)),x),Simp(Dist(Times(n,p,Power(Times(c,Plus(Times(n,p),C1)),CN1)),Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,C1)),Power(Plus(C1,ProductLog(Times(a,Power(x,n)))),CN1)),x),x),x)),And(FreeQ(list(a,c,n),x),Or(And(IntegerQ(p),EqQ(Times(n,Plus(p,C1)),CN1)),And(IntegerQ(Plus(p,CN1D2)),EqQ(Times(n,Plus(p,C1D2)),CN1)))))),
IIntegrate(7184,Integrate(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_)))),p_DEFAULT),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(Power(x,n),CN1)))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(list(a,c,p),x),ILtQ(n,C0)))),
IIntegrate(7185,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Plus(C1,ProductLog(Times(a,Power(x,n)))),CN1)),x),x),x)),And(FreeQ(List(a,c,m,n,p),x),NeQ(m,CN1),Or(And(IntegerQ(Plus(p,CN1D2)),IGtQ(Times(C2,Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1))))),C0)),And(Not(IntegerQ(Plus(p,CN1D2))),IGtQ(Plus(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C1),C0)))))),
IIntegrate(7186,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Plus(m,Times(n,p),C1),CN1)),x),Simp(Dist(Times(n,p,Power(Times(c,Plus(m,Times(n,p),C1)),CN1)),Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,C1)),Power(Plus(C1,ProductLog(Times(a,Power(x,n)))),CN1)),x),x),x)),And(FreeQ(List(a,c,m,n,p),x),Or(EqQ(m,CN1),And(IntegerQ(Plus(p,CN1D2)),ILtQ(Plus(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),CN1D2),C0)),And(Not(IntegerQ(Plus(p,CN1D2))),ILtQ(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C0)))))),
IIntegrate(7187,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,x))),p),Power(Plus(C1,ProductLog(Times(a,x))),CN1)),x),Simp(Dist(Power(c,CN1),Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,x))),Plus(p,C1)),Power(Plus(C1,ProductLog(Times(a,x))),CN1)),x),x),x)),FreeQ(list(a,c,m),x))),
IIntegrate(7188,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_)))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(Power(x,n),CN1)))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,c,p),x),ILtQ(n,C0),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(7189,Integrate(Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1),x_Symbol),
    Condition(Simp(Times(Plus(a,Times(b,x)),Power(Times(b,d,ProductLog(Plus(a,Times(b,x)))),CN1)),x),FreeQ(list(a,b,d),x))),
IIntegrate(7190,Integrate(Times(ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(d,x),x),Integrate(Power(Plus(d,Times(d,ProductLog(Plus(a,Times(b,x))))),CN1),x)),FreeQ(list(a,b,d),x))),
IIntegrate(7191,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(c,Plus(a,Times(b,x)),Power(Times(c,ProductLog(Plus(a,Times(b,x)))),Plus(p,CN1)),Power(Times(b,d),CN1)),x),Simp(Dist(Times(c,p),Integrate(Times(Power(Times(c,ProductLog(Plus(a,Times(b,x)))),Plus(p,CN1)),Power(Plus(d,Times(d,ProductLog(Plus(a,Times(b,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),GtQ(p,C0)))),
IIntegrate(7192,Integrate(Times(Power(ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CN1),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Times(ExpIntegralEi(ProductLog(Plus(a,Times(b,x)))),Power(Times(b,d),CN1)),x),FreeQ(list(a,b,d),x))),
IIntegrate(7193,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN1D2),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Times(Rt(Times(Pi,c),C2),Erfi(Times(Sqrt(Times(c,ProductLog(Plus(a,Times(b,x))))),Power(Rt(c,C2),CN1))),Power(Times(b,c,d),CN1)),x),And(FreeQ(List(a,b,c,d),x),PosQ(c)))),
IIntegrate(7194,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN1D2),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Times(Rt(Times(CN1,Pi,c),C2),Erf(Times(Sqrt(Times(c,ProductLog(Plus(a,Times(b,x))))),Power(Rt(Negate(c),C2),CN1))),Power(Times(b,c,d),CN1)),x),And(FreeQ(List(a,b,c,d),x),NegQ(c)))),
IIntegrate(7195,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Power(Times(c,ProductLog(Plus(a,Times(b,x)))),p),Power(Times(b,d,Plus(p,C1)),CN1)),x),Simp(Dist(Power(Times(c,Plus(p,C1)),CN1),Integrate(Times(Power(Times(c,ProductLog(Plus(a,Times(b,x)))),Plus(p,C1)),Power(Plus(d,Times(d,ProductLog(Plus(a,Times(b,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),LtQ(p,CN1)))),
IIntegrate(7196,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Times(Gamma(Plus(p,C1),Negate(ProductLog(Plus(a,Times(b,x))))),Power(Times(c,ProductLog(Plus(a,Times(b,x)))),p),Power(Times(b,d,Power(Negate(ProductLog(Plus(a,Times(b,x)))),p)),CN1)),x),FreeQ(List(a,b,c,d,p),x))),
IIntegrate(7197,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Power(Plus(d,Times(d,ProductLog(x))),CN1),Power(Plus(Times(b,e),Times(CN1,a,f),Times(f,x)),m),x),x),x,Plus(a,Times(b,x))),x),x),And(FreeQ(List(a,b,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7198,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Plus(a_,Times(b_DEFAULT,x_)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Dist(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Times(Power(Times(c,ProductLog(x)),p),Power(Plus(d,Times(d,ProductLog(x))),CN1)),Power(Plus(Times(b,e),Times(CN1,a,f),Times(f,x)),m),x),x),x,Plus(a,Times(b,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,p),x),IGtQ(m,C0)))),
IIntegrate(7199,Integrate(Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_))))),CN1),x_Symbol),
    Condition(Negate(Subst(Integrate(Power(Times(Sqr(x),Plus(d,Times(d,ProductLog(Times(a,Power(Power(x,n),CN1)))))),CN1),x),x,Power(x,CN1))),And(FreeQ(list(a,d),x),ILtQ(n,C0)))),
IIntegrate(7200,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(c,x,Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,CN1)),Power(d,CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Times(n,Plus(p,CN1)),CN1))))
  );
}
