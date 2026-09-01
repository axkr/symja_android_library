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
class IntRules309 { 
  public static IAST RULES = List( 
IIntegrate(6181,Integrate(Times(Sqr(Csch(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Power(Plus(Times(Sqr(Csch(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),a_DEFAULT),Times(Sqr(Coth(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),b_DEFAULT),c_DEFAULT),CN1),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(C2,Integrate(Times(Power(Plus(f,Times(g,x)),m),Power(Plus(Times(C2,a),b,Negate(c),Times(Plus(b,c),Cosh(Plus(Times(C2,d),Times(C2,e,x))))),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(m,C0),NeQ(Plus(a,b),C0),NeQ(Plus(a,c),C0)))),
IIntegrate(6182,Integrate(Times(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),CN2),Plus(A_,Times(B_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Times(BSymbol,Plus(e,Times(f,x)),Cosh(Plus(c,Times(d,x))),Power(Times(a,d,Plus(a,Times(b,Sinh(Plus(c,Times(d,x)))))),CN1)),x),Simp(Dist(Times(BSymbol,f,Power(Times(a,d),CN1)),Integrate(Times(Cosh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol),x),EqQ(Plus(Times(a,ASymbol),Times(b,BSymbol)),C0)))),
IIntegrate(6183,Integrate(Times(Power(Plus(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN2),Plus(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),B_DEFAULT),A_),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(BSymbol,Plus(e,Times(f,x)),Sinh(Plus(c,Times(d,x))),Power(Times(a,d,Plus(a,Times(b,Cosh(Plus(c,Times(d,x)))))),CN1)),x),Simp(Dist(Times(BSymbol,f,Power(Times(a,d),CN1)),Integrate(Times(Sinh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol),x),EqQ(Subtract(Times(a,ASymbol),Times(b,BSymbol)),C0)))),
IIntegrate(6184,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),n_)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,x)),m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),RationalQ(p)))),
IIntegrate(6185,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),n_)))),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,x)),m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),IGtQ(m,C0),RationalQ(p)))),
IIntegrate(6186,Integrate(Times(Power(Sech(v_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Tanh(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Times(a,Cosh(v)),Times(b,Sinh(v))),n),x),And(FreeQ(list(a,b),x),IntegerQ(Times(C1D2,Plus(m,CN1))),EqQ(Plus(m,n),C0)))),
IIntegrate(6187,Integrate(Times(Power(Csch(v_),m_DEFAULT),Power(Plus(Times(Coth(v_),b_DEFAULT),a_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Times(b,Cosh(v)),Times(a,Sinh(v))),n),x),And(FreeQ(list(a,b),x),IntegerQ(Times(C1D2,Plus(m,CN1))),EqQ(Plus(m,n),C0)))),
IIntegrate(6188,Integrate(Times(u_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),m_DEFAULT),Power(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(u,Times(Power(Sinh(Plus(a,Times(b,x))),m),Power(Sinh(Plus(c,Times(d,x))),n)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(6189,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),m_DEFAULT),Power(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(u,Times(Power(Cosh(Plus(a,Times(b,x))),m),Power(Cosh(Plus(c,Times(d,x))),n)),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(6190,Integrate(Times(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Sech(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Dist(Negate(Csch(Times(Subtract(Times(b,c),Times(a,d)),Power(d,CN1)))),Integrate(Tanh(Plus(a,Times(b,x))),x),x),x),Simp(Dist(Csch(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1))),Integrate(Tanh(Plus(c,Times(d,x))),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(b),Sqr(d)),C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(6191,Integrate(Times(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Csch(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Dist(Csch(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1))),Integrate(Coth(Plus(a,Times(b,x))),x),x),x),Simp(Dist(Csch(Times(Subtract(Times(b,c),Times(a,d)),Power(d,CN1))),Integrate(Coth(Plus(c,Times(d,x))),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(b),Sqr(d)),C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(6192,Integrate(Times(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tanh(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(b,x,Power(d,CN1)),x),Simp(Dist(Times(b,Power(d,CN1),Cosh(Times(Subtract(Times(b,c),Times(a,d)),Power(d,CN1)))),Integrate(Times(Sech(Plus(a,Times(b,x))),Sech(Plus(c,Times(d,x)))),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(b),Sqr(d)),C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(6193,Integrate(Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Coth(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(b,x,Power(d,CN1)),x),Simp(Dist(Cosh(Times(Subtract(Times(b,c),Times(a,d)),Power(d,CN1))),Integrate(Times(Csch(Plus(a,Times(b,x))),Csch(Plus(c,Times(d,x)))),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(b),Sqr(d)),C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(6194,Integrate(Times(u_DEFAULT,Power(Plus(Times(Cosh(v_),a_DEFAULT),Times(b_DEFAULT,Sinh(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Times(a,Exp(Times(a,Power(b,CN1),v))),n)),x),And(FreeQ(list(a,b,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(6195,Integrate(Sinh(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Dist(CN1D2,Integrate(Exp(Times(CN1,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x),x),Simp(Dist(C1D2,Integrate(Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6196,Integrate(Cosh(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Exp(Times(CN1,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x),x),Simp(Dist(C1D2,Integrate(Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6197,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sinh(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Dist(CN1D2,Integrate(Times(Power(Times(e,x),m),Power(Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x),x)),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6198,Integrate(Times(Cosh(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Power(Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Times(d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x),x),x)),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6199,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),n)),x),Simp(Dist(Times(b,c,n),Integrate(Times(x,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,CN1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),GtQ(n,C0)))),
IIntegrate(6200,Integrate(Power(Plus(a_DEFAULT,Times(ArcSinh(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Subtract(Simp(Times(Sqrt(Plus(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Dist(Times(c,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcSinh(Times(c,x)))),Plus(n,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x),x),x)),And(FreeQ(list(a,b,c),x),LtQ(n,CN1))))
  );
}
