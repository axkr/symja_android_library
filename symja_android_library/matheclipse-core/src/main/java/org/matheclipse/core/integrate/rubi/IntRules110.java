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
class IntRules110 { 
  public static IAST RULES = List( 
IIntegrate(2201,Integrate(Times(Power(Log(Times(d_DEFAULT,x_)),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(e_,Times(Log(Times(d_DEFAULT,x_)),h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(e,x,Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Log(Times(d,x)),Plus(n,C1)),Power(Plus(n,C1),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,n),x),EqQ(Subtract(e,Times(f,h,Plus(n,C1))),C0),EqQ(Subtract(Times(g,h,Plus(n,C1)),Times(b,c,e,Log(FSymbol))),C0),NeQ(n,CN1)))),
IIntegrate(2202,Integrate(Times(Power(Log(Times(d_DEFAULT,x_)),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(x_,m_DEFAULT),Plus(e_,Times(Log(Times(d_DEFAULT,x_)),h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(e,Power(x,Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Log(Times(d,x)),Plus(n,C1)),Power(Plus(n,C1),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,m,n),x),EqQ(Subtract(Times(e,Plus(m,C1)),Times(f,h,Plus(n,C1))),C0),EqQ(Subtract(Times(g,h,Plus(n,C1)),Times(b,c,e,Log(FSymbol))),C0),NeQ(n,CN1)))),
IIntegrate(2203,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(Power(FSymbol,Plus(a,Times(b,Plus(c,Times(d,x))))),Power(Times(b,d,Log(FSymbol)),CN1)),x),FreeQ(List(FSymbol,a,b,c,d),x))),
IIntegrate(2204,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(Power(FSymbol,a),CSqrtPi,Erfi(Times(Plus(c,Times(d,x)),Rt(Times(b,Log(FSymbol)),C2))),Power(Times(C2,d,Rt(Times(b,Log(FSymbol)),C2)),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d),x),PosQ(b)))),
IIntegrate(2205,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(Power(FSymbol,a),CSqrtPi,Erf(Times(Plus(c,Times(d,x)),Rt(Times(CN1,b,Log(FSymbol)),C2))),Power(Times(C2,d,Rt(Times(CN1,b,Log(FSymbol)),C2)),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d),x),NegQ(b)))),
IIntegrate(2206,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(c,Times(d,x)),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),Power(d,CN1)),x),Dist(Times(b,n,Log(FSymbol)),Integrate(Times(Power(Plus(c,Times(d,x)),n),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n))))),x),x)),And(FreeQ(List(FSymbol,a,b,c,d),x),IntegerQ(Times(C2,Power(n,CN1))),ILtQ(n,C0)))),
IIntegrate(2207,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Dist(Times(k,Power(d,CN1)),Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(FSymbol,Plus(a,Times(b,Power(x,Times(k,n)))))),x),x,Power(Plus(c,Times(d,x)),Power(k,CN1))),x)),And(FreeQ(List(FSymbol,a,b,c,d),x),IntegerQ(Times(C2,Power(n,CN1))),Not(IntegerQ(n))))),
IIntegrate(2208,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Negate(Simp(Times(Power(FSymbol,a),Plus(c,Times(d,x)),Gamma(Power(n,CN1),Times(CN1,b,Power(Plus(c,Times(d,x)),n),Log(FSymbol))),Power(Times(d,n,Power(Times(CN1,b,Power(Plus(c,Times(d,x)),n),Log(FSymbol)),Power(n,CN1))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,n),x),Not(IntegerQ(Times(C2,Power(n,CN1))))))),
IIntegrate(2209,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Plus(e,Times(f,x)),n),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),Power(Times(b,f,n,Power(Plus(c,Times(d,x)),n),Log(FSymbol)),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,n),x),EqQ(m,Subtract(n,C1)),EqQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(2210,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Simp(Times(Power(FSymbol,a),ExpIntegralEi(Times(b,Power(Plus(c,Times(d,x)),n),Log(FSymbol))),Power(Times(f,n),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,n),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(2211,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(d,Plus(m,C1)),CN1),Subst(Integrate(Power(FSymbol,Plus(a,Times(b,Sqr(x)))),x),x,Power(Plus(c,Times(d,x)),Plus(m,C1))),x),And(FreeQ(List(FSymbol,a,b,c,d,m,n),x),EqQ(n,Times(C2,Plus(m,C1)))))),
IIntegrate(2212,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,Negate(n),C1)),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),Power(Times(b,d,n,Log(FSymbol)),CN1)),x),Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Log(FSymbol)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,n)),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n))))),x),x)),And(FreeQ(List(FSymbol,a,b,c,d),x),IntegerQ(Times(C2,Plus(m,C1),Power(n,CN1))),LtQ(C0,Times(Plus(m,C1),Power(n,CN1)),C5),IntegerQ(n),Or(LtQ(C0,n,Plus(m,C1)),LtQ(m,n,C0))))),
IIntegrate(2213,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,Negate(n),C1)),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),Power(Times(b,d,n,Log(FSymbol)),CN1)),x),Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Log(FSymbol)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Simplify(Subtract(m,n))),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n))))),x),x)),And(FreeQ(List(FSymbol,a,b,c,d,m,n),x),IntegerQ(Times(C2,Simplify(Times(Plus(m,C1),Power(n,CN1))))),LtQ(C0,Simplify(Times(Plus(m,C1),Power(n,CN1))),C5),Not(RationalQ(m)),SumSimplerQ(m,Negate(n))))),
IIntegrate(2214,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,n,Log(FSymbol),Power(Plus(m,C1),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,n)),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n))))),x),x)),And(FreeQ(List(FSymbol,a,b,c,d),x),IntegerQ(Times(C2,Plus(m,C1),Power(n,CN1))),LtQ(CN4,Times(Plus(m,C1),Power(n,CN1)),C5),IntegerQ(n),Or(And(GtQ(n,C0),LtQ(m,CN1)),And(GtQ(Negate(n),C0),LeQ(Negate(n),Plus(m,C1))))))),
IIntegrate(2215,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,n,Log(FSymbol),Power(Plus(m,C1),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Simplify(Plus(m,n))),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n))))),x),x)),And(FreeQ(List(FSymbol,a,b,c,d,m,n),x),IntegerQ(Times(C2,Simplify(Times(Plus(m,C1),Power(n,CN1))))),LtQ(CN4,Simplify(Times(Plus(m,C1),Power(n,CN1))),C5),Not(RationalQ(m)),SumSimplerQ(m,n)))),
IIntegrate(2216,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Dist(Times(k,Power(d,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(FSymbol,Plus(a,Times(b,Power(x,Times(k,n)))))),x),x,Power(Plus(c,Times(d,x)),Power(k,CN1))),x)),And(FreeQ(List(FSymbol,a,b,c,d,m,n),x),IntegerQ(Times(C2,Plus(m,C1),Power(n,CN1))),LtQ(C0,Times(Plus(m,C1),Power(n,CN1)),C5),Not(IntegerQ(n))))),
IIntegrate(2217,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Plus(e,Times(f,x)),m),Power(Power(Plus(c,Times(d,x)),m),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n))))),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m,n),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IntegerQ(Times(C2,Simplify(Times(Plus(m,C1),Power(n,CN1))))),NeQ(f,d),Not(IntegerQ(m)),NeQ(Times(c,e),C0)))),
IIntegrate(2218,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Negate(Simp(Times(Power(FSymbol,a),Power(Plus(e,Times(f,x)),Plus(m,C1)),Gamma(Times(Plus(m,C1),Power(n,CN1)),Times(CN1,b,Power(Plus(c,Times(d,x)),n),Log(FSymbol))),Power(Times(f,n,Power(Times(CN1,b,Power(Plus(c,Times(d,x)),n),Log(FSymbol)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m,n),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(2219,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(f,Power(Plus(e,Times(f,x)),Subtract(m,C1)),Power(FSymbol,Plus(a,Times(b,Sqr(Plus(c,Times(d,x)))))),Power(Times(C2,b,Sqr(d),Log(FSymbol)),CN1)),x),Dist(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Subtract(m,C1)),Power(FSymbol,Plus(a,Times(b,Sqr(Plus(c,Times(d,x))))))),x),x),Negate(Dist(Times(Subtract(m,C1),Sqr(f),Power(Times(C2,b,Sqr(d),Log(FSymbol)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Subtract(m,C2)),Power(FSymbol,Plus(a,Times(b,Sqr(Plus(c,Times(d,x))))))),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),NeQ(Subtract(Times(d,e),Times(c,f)),C0),FractionQ(m),GtQ(m,C1)))),
IIntegrate(2220,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(f,Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(FSymbol,Plus(a,Times(b,Sqr(Plus(c,Times(d,x)))))),Power(Times(Plus(m,C1),Sqr(f)),CN1)),x),Negate(Dist(Times(C2,b,Sqr(d),Log(FSymbol),Power(Times(Sqr(f),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C2)),Power(FSymbol,Plus(a,Times(b,Sqr(Plus(c,Times(d,x))))))),x),x)),Dist(Times(C2,b,d,Subtract(Times(d,e),Times(c,f)),Log(FSymbol),Power(Times(Sqr(f),Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(FSymbol,Plus(a,Times(b,Sqr(Plus(c,Times(d,x))))))),x),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),NeQ(Subtract(Times(d,e),Times(c,f)),C0),LtQ(m,CN1))))
  );
}
