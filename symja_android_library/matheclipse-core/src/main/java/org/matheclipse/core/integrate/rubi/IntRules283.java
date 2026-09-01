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
class IntRules283 { 
  public static IAST RULES = List( 
IIntegrate(5661,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT),Power(x_,m_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(c,p),Power(x,m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Subtract(p,Times(CI,C1D2,n))),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(c,Times(Sqr(a),d)),Not(IntegerQ(Times(CI,C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(And(IntegerQ(Times(C2,p)),IntegerQ(Plus(p,Times(CI,C1D2,n))))),Not(IntegerQ(m))))),
IIntegrate(5662,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Power(Plus(C1,Power(Times(Sqr(a),Sqr(x)),CN1)),p),CN1)),Integrate(Times(u,Power(Plus(C1,Power(Times(Sqr(a),Sqr(x)),CN1)),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(c,Times(Sqr(a),d)),Not(IntegerQ(Times(CI,C1D2,n))),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5663,Integrate(Times(Exp(Times(ArcCot(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),u_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(-1,Times(CI,C1D2,n)),Integrate(Times(u,Power(Exp(Times(n,ArcTan(Times(c,Plus(a,Times(b,x)))))),CN1)),x),x),x),And(FreeQ(list(a,b,c),x),IntegerQ(Times(CI,C1D2,n))))),
IIntegrate(5664,Integrate(Exp(Times(ArcCot(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(CI,c,Plus(a,Times(b,x))),Times(CI,C1D2,n)),Power(Plus(C1,Power(Times(CI,c,Plus(a,Times(b,x))),CN1)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(CI,C1D2,n)),CN1)),Integrate(Times(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(CI,C1D2,n)),Power(Power(Plus(CN1,Times(CI,a,c),Times(CI,b,c,x)),Times(CI,C1D2,n)),CN1)),x),x),x),And(FreeQ(List(a,b,c,n),x),Not(IntegerQ(Times(CI,C1D2,n)))))),
IIntegrate(5665,Integrate(Times(Exp(Times(ArcCoth(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),Power(x_,m_)),x_Symbol),
    Condition(Simp(Dist(Times(C4,Power(Times(Power(CI,m),n,Power(b,Plus(m,C1)),Power(c,Plus(m,C1))),CN1)),Subst(Integrate(Times(Power(x,Times(C2,Power(Times(CI,n),CN1))),Power(Plus(C1,Times(CI,a,c),Times(Subtract(C1,Times(CI,a,c)),Power(x,Times(C2,Power(Times(CI,n),CN1))))),m),Power(Power(Plus(CN1,Power(x,Times(C2,Power(Times(CI,n),CN1)))),Plus(m,C2)),CN1)),x),x,Times(Power(Plus(C1,Power(Times(CI,c,Plus(a,Times(b,x))),CN1)),Times(CI,C1D2,n)),Power(Power(Subtract(C1,Power(Times(CI,c,Plus(a,Times(b,x))),CN1)),Times(CI,C1D2,n)),CN1))),x),x),And(FreeQ(list(a,b,c),x),ILtQ(m,C0),LtQ(CN1,Times(CI,n),C1)))),
IIntegrate(5666,Integrate(Times(Exp(Times(ArcCoth(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(CI,c,Plus(a,Times(b,x))),Times(CI,C1D2,n)),Power(Plus(C1,Power(Times(CI,c,Plus(a,Times(b,x))),CN1)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(CI,C1D2,n)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(C1,Times(CI,a,c),Times(CI,b,c,x)),Times(CI,C1D2,n)),Power(Power(Plus(CN1,Times(CI,a,c),Times(CI,b,c,x)),Times(CI,C1D2,n)),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),Not(IntegerQ(Times(CI,C1D2,n)))))),
IIntegrate(5667,Integrate(Times(Exp(Times(ArcCot(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(c,Power(Plus(C1,Sqr(a)),CN1)),p),Power(Times(Plus(Times(CI,a),Times(CI,b,x)),Power(Plus(C1,Times(CI,a),Times(CI,b,x)),CN1)),Times(CI,C1D2,n)),Power(Times(Plus(C1,Times(CI,a),Times(CI,b,x)),Power(Plus(Times(CI,a),Times(CI,b,x)),CN1)),Times(CI,C1D2,n)),Power(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,x)),Times(CI,C1D2,n)),Power(Power(Plus(CN1,Times(CI,a),Times(CI,b,x)),Times(CI,C1D2,n)),CN1)),Integrate(Times(u,Power(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,x)),Subtract(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,a),Times(CI,b,x)),Plus(p,Times(CI,C1D2,n)))),x),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),Not(IntegerQ(Times(CI,C1D2,n))),EqQ(Subtract(Times(b,d),Times(C2,a,e)),C0),EqQ(Subtract(Times(Sqr(b),c),Times(e,Plus(C1,Sqr(a)))),C0),Or(IntegerQ(p),GtQ(Times(c,Power(Plus(C1,Sqr(a)),CN1)),C0))))),
IIntegrate(5668,Integrate(Times(Exp(Times(ArcCot(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,x),Times(e,Sqr(x))),p),Power(Power(Plus(C1,Sqr(a),Times(C2,a,b,x),Times(Sqr(b),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Sqr(a),Times(C2,a,b,x),Times(Sqr(b),Sqr(x))),p),Exp(Times(n,ArcCot(Times(a,x))))),x),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),Not(IntegerQ(Times(CI,C1D2,n))),EqQ(Subtract(Times(b,d),Times(C2,a,e)),C0),EqQ(Subtract(Times(Sqr(b),c),Times(e,Plus(C1,Sqr(a)))),C0),Not(Or(IntegerQ(p),GtQ(Times(c,Power(Plus(C1,Sqr(a)),CN1)),C0)))))),
IIntegrate(5669,Integrate(Times(Exp(Times(ArcCot(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1))),n_DEFAULT)),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Exp(Times(n,ArcTan(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(c,CN1))))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5670,Integrate(ArcTan(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Plus(a,Times(b,Power(x,n))))),x),Simp(Dist(Times(b,n),Integrate(Times(Power(x,n),Power(Plus(C1,Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(5671,Integrate(ArcCot(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Plus(a,Times(b,Power(x,n))))),x),Simp(Dist(Times(b,n),Integrate(Times(Power(x,n),Power(Plus(C1,Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(5672,Integrate(Times(ArcTan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,Power(x,n)))),Power(x,CN1)),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Plus(C1,Times(CI,a),Times(CI,b,Power(x,n)))),Power(x,CN1)),x),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(5673,Integrate(Times(ArcCot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Subtract(C1,Times(CI,Power(Plus(a,Times(b,Power(x,n))),CN1)))),Power(x,CN1)),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Plus(C1,Times(CI,Power(Plus(a,Times(b,Power(x,n))),CN1)))),Power(x,CN1)),x),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(5674,Integrate(Times(ArcTan(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),ArcTan(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(C1,Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(list(a,b),x),RationalQ(m,n),Unequal(Plus(m,C1),C0),Unequal(Plus(m,C1),n)))),
IIntegrate(5675,Integrate(Times(ArcCot(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),ArcCot(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Plus(C1,Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(list(a,b),x),RationalQ(m,n),Unequal(Plus(m,C1),C0),Unequal(Plus(m,C1),n)))),
IIntegrate(5676,Integrate(ArcTan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Log(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,Power(f,Plus(c,Times(d,x)))))),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Log(Plus(C1,Times(CI,a),Times(CI,b,Power(f,Plus(c,Times(d,x)))))),x),x),x)),FreeQ(List(a,b,c,d,f),x))),
IIntegrate(5677,Integrate(ArcCot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Log(Subtract(C1,Times(CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1)))),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Log(Plus(C1,Times(CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1)))),x),x),x)),FreeQ(List(a,b,c,d,f),x))),
IIntegrate(5678,Integrate(Times(ArcTan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Power(x,m),Log(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,Power(f,Plus(c,Times(d,x))))))),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Power(x,m),Log(Plus(C1,Times(CI,a),Times(CI,b,Power(f,Plus(c,Times(d,x))))))),x),x),x)),And(FreeQ(List(a,b,c,d,f),x),IntegerQ(m),Greater(m,C0)))),
IIntegrate(5679,Integrate(Times(ArcCot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Power(x,m),Log(Subtract(C1,Times(CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1))))),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Power(x,m),Log(Plus(C1,Times(CI,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1))))),x),x),x)),And(FreeQ(List(a,b,c,d,f),x),IntegerQ(m),Greater(m,C0)))),
IIntegrate(5680,Integrate(Times(Power(ArcTan(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCot(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x)))
  );
}
