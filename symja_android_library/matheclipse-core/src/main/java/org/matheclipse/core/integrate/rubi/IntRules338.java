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
class IntRules338 { 
  public static IAST RULES = List( 
IIntegrate(6761,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,p)),Subst(Integrate(Times(Power(Subtract(C1,Times(x,Power(a,CN1))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(x,Power(a,CN1))),Plus(p,Times(C1D2,n))),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(Times(C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(IntegersQ(Times(C2,p),Plus(p,Times(C1D2,n)))),IntegerQ(m)))),
IIntegrate(6762,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT),Power(x_,m_)),x_Symbol),
    Condition(Simp(Dist(Times(CN1,Power(c,p),Power(x,m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Subtract(C1,Times(x,Power(a,CN1))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(x,Power(a,CN1))),Plus(p,Times(C1D2,n))),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(Times(C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(IntegersQ(Times(C2,p),Plus(p,Times(C1D2,n)))),Not(IntegerQ(m))))),
IIntegrate(6763,Integrate(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Power(x,CN2))),FracPart(p)),Power(Power(Subtract(C1,Power(Times(Sqr(a),Sqr(x)),CN1)),FracPart(p)),CN1)),Integrate(Times(u,Power(Subtract(C1,Power(Times(Sqr(a),Sqr(x)),CN1)),p),Exp(Times(n,ArcCoth(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(Times(C1D2,n))),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(6764,Integrate(Times(Exp(Times(ArcCoth(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),u_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(-1,Times(C1D2,n)),Integrate(Times(u,Exp(Times(n,ArcTanh(Times(c,Plus(a,Times(b,x))))))),x),x),x),And(FreeQ(list(a,b,c),x),IntegerQ(Times(C1D2,n))))),
IIntegrate(6765,Integrate(Exp(Times(ArcCoth(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(c,Plus(a,Times(b,x))),Times(C1D2,n)),Power(Plus(C1,Power(Times(c,Plus(a,Times(b,x))),CN1)),Times(C1D2,n)),Power(Power(Plus(C1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),CN1)),Integrate(Times(Power(Plus(C1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),Power(Power(Plus(CN1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),CN1)),x),x),x),And(FreeQ(List(a,b,c,n),x),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6766,Integrate(Times(Exp(Times(ArcCoth(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),Power(x_,m_)),x_Symbol),
    Condition(Simp(Dist(Times(CN4,Power(Times(n,Power(b,Plus(m,C1)),Power(c,Plus(m,C1))),CN1)),Subst(Integrate(Times(Power(x,Times(C2,Power(n,CN1))),Power(Plus(C1,Times(a,c),Times(Subtract(C1,Times(a,c)),Power(x,Times(C2,Power(n,CN1))))),m),Power(Power(Plus(CN1,Power(x,Times(C2,Power(n,CN1)))),Plus(m,C2)),CN1)),x),x,Times(Power(Plus(C1,Power(Times(c,Plus(a,Times(b,x))),CN1)),Times(C1D2,n)),Power(Power(Subtract(C1,Power(Times(c,Plus(a,Times(b,x))),CN1)),Times(C1D2,n)),CN1))),x),x),And(FreeQ(list(a,b,c),x),ILtQ(m,C0),LtQ(CN1,n,C1)))),
IIntegrate(6767,Integrate(Times(Exp(Times(ArcCoth(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(c,Plus(a,Times(b,x))),Times(C1D2,n)),Power(Plus(C1,Power(Times(c,Plus(a,Times(b,x))),CN1)),Times(C1D2,n)),Power(Power(Plus(C1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(C1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),Power(Power(Plus(CN1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6768,Integrate(Times(Exp(Times(ArcCoth(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),p),Power(Times(Plus(a,Times(b,x)),Power(Plus(C1,a,Times(b,x)),CN1)),Times(C1D2,n)),Power(Times(Plus(C1,a,Times(b,x)),Power(Plus(a,Times(b,x)),CN1)),Times(C1D2,n)),Power(Subtract(Subtract(C1,a),Times(b,x)),Times(C1D2,n)),Power(Power(Plus(CN1,a,Times(b,x)),Times(C1D2,n)),CN1)),Integrate(Times(u,Power(Subtract(Subtract(C1,a),Times(b,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,a,Times(b,x)),Plus(p,Times(C1D2,n)))),x),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),Not(IntegerQ(Times(C1D2,n))),EqQ(Subtract(Times(b,d),Times(C2,a,e)),C0),EqQ(Plus(Times(Sqr(b),c),Times(e,Subtract(C1,Sqr(a)))),C0),Or(IntegerQ(p),GtQ(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),C0))))),
IIntegrate(6769,Integrate(Times(Exp(Times(ArcCoth(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(c,Times(d,x),Times(e,Sqr(x))),p),Power(Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,x)),Times(Sqr(b),Sqr(x))),p),CN1)),Integrate(Times(u,Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,x)),Times(Sqr(b),Sqr(x))),p),Exp(Times(n,ArcCoth(Times(a,x))))),x),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),Not(IntegerQ(Times(C1D2,n))),EqQ(Subtract(Times(b,d),Times(C2,a,e)),C0),EqQ(Plus(Times(Sqr(b),c),Times(e,Subtract(C1,Sqr(a)))),C0),Not(Or(IntegerQ(p),GtQ(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),C0)))))),
IIntegrate(6770,Integrate(Times(Exp(Times(ArcCoth(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1))),n_DEFAULT)),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Exp(Times(n,ArcTanh(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(c,CN1))))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6771,Integrate(ArcTanh(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTanh(Plus(a,Times(b,Power(x,n))))),x),Simp(Dist(Times(b,n),Integrate(Times(Power(x,n),Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,Power(x,n))),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6772,Integrate(ArcCoth(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCoth(Plus(a,Times(b,Power(x,n))))),x),Simp(Dist(Times(b,n),Integrate(Times(Power(x,n),Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,Power(x,n))),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6773,Integrate(Times(ArcTanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Log(Plus(C1,a,Times(b,Power(x,n)))),Power(x,CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Log(Subtract(Subtract(C1,a),Times(b,Power(x,n)))),Power(x,CN1)),x),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6774,Integrate(Times(ArcCoth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Log(Plus(C1,Power(Plus(a,Times(b,Power(x,n))),CN1))),Power(x,CN1)),x),x),x),Simp(Dist(C1D2,Integrate(Times(Log(Subtract(C1,Power(Plus(a,Times(b,Power(x,n))),CN1))),Power(x,CN1)),x),x),x)),FreeQ(list(a,b,n),x))),
IIntegrate(6775,Integrate(Times(ArcTanh(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),ArcTanh(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,Power(x,n))),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(list(a,b),x),RationalQ(m,n),NeQ(m,CN1),NeQ(Plus(m,C1),n)))),
IIntegrate(6776,Integrate(Times(ArcCoth(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),ArcCoth(Plus(a,Times(b,Power(x,n)))),Power(Plus(m,C1),CN1)),x),Simp(Dist(Times(b,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,n)),Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,Power(x,n))),Times(Sqr(b),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(list(a,b),x),RationalQ(m,n),NeQ(m,CN1),NeQ(Plus(m,C1),n)))),
IIntegrate(6777,Integrate(ArcTanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Log(Plus(C1,a,Times(b,Power(f,Plus(c,Times(d,x)))))),x),x),x),Simp(Dist(C1D2,Integrate(Log(Subtract(Subtract(C1,a),Times(b,Power(f,Plus(c,Times(d,x)))))),x),x),x)),FreeQ(List(a,b,c,d,f),x))),
IIntegrate(6778,Integrate(ArcCoth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Log(Plus(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1))),x),x),x),Simp(Dist(C1D2,Integrate(Log(Subtract(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1))),x),x),x)),FreeQ(List(a,b,c,d,f),x))),
IIntegrate(6779,Integrate(Times(ArcTanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Power(x,m),Log(Plus(C1,a,Times(b,Power(f,Plus(c,Times(d,x))))))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Power(x,m),Log(Subtract(Subtract(C1,a),Times(b,Power(f,Plus(c,Times(d,x))))))),x),x),x)),And(FreeQ(List(a,b,c,d,f),x),IGtQ(m,C0)))),
IIntegrate(6780,Integrate(Times(ArcCoth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(f_,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Dist(C1D2,Integrate(Times(Power(x,m),Log(Plus(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1)))),x),x),x),Simp(Dist(C1D2,Integrate(Times(Power(x,m),Log(Subtract(C1,Power(Plus(a,Times(b,Power(f,Plus(c,Times(d,x))))),CN1)))),x),x),x)),And(FreeQ(List(a,b,c,d,f),x),IGtQ(m,C0))))
  );
}
