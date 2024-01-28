package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules281 { 
  public static IAST RULES = List( 
IIntegrate(5621,Integrate(Times(Exp(Times(ArcTan(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1))),n_DEFAULT)),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Exp(Times(n,ArcCot(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(c,CN1))))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5622,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),u_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(CN1,Times(CI,C1D2,n)),Integrate(Times(u,Power(Exp(Times(n,ArcTan(Times(a,x)))),CN1)),x)),x),And(FreeQ(a,x),IntegerQ(Times(CI,C1D2,n))))),
IIntegrate(5623,Integrate(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Sqr(x),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Subtract(Times(CI,n),C1))),Sqrt(Plus(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(a,x),IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1)))))),
IIntegrate(5624,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Subtract(Times(CI,n),C1))),Sqrt(Plus(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(a,x),IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))),IntegerQ(m)))),
IIntegrate(5625,Integrate(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n)),Power(Times(Sqr(x),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,n),x),Not(IntegerQ(Times(CI,n)))))),
IIntegrate(5626,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,n),x),Not(IntegerQ(Times(CI,n))),IntegerQ(m)))),
IIntegrate(5627,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_)),Power(x_,m_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(x,m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,Subtract(Times(CI,n),C1))),Sqrt(Plus(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(list(a,m),x),IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))),Not(IntegerQ(m))))),
IIntegrate(5628,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(list(a,m,n),x),Not(IntegerQ(Times(CI,C1D2,n))),Not(IntegerQ(m))))),
IIntegrate(5629,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,p),Integrate(Times(u,Power(x,p),Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(Times(CI,C1D2,n))),IntegerQ(p)))),
IIntegrate(5630,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(c,Times(d,x)),p),Power(Times(Power(x,p),Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p)),CN1)),Integrate(Times(u,Power(x,p),Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(Times(CI,C1D2,n))),Not(IntegerQ(p))))),
IIntegrate(5631,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(c,p)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n)),Power(Times(Sqr(x),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(CI,C1D2,n))),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5632,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(c,p)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(CI,C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),IntegerQ(m)))),
IIntegrate(5633,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),CN1)),Integrate(Times(Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(CI,C1D2,n))),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5634,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT),Power(x_,m_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,Power(c,p),Power(x,m),Power(Power(x,CN1),m)),Subst(Integrate(Times(Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Subtract(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Plus(C1,Times(CI,x,Power(a,CN1))),Times(CI,C1D2,n))),CN1)),x),x,Power(x,CN1))),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(CI,C1D2,n))),Or(IntegerQ(p),GtQ(c,C0)),Not(IntegerQ(m))))),
IIntegrate(5635,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),CN1)),Integrate(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Exp(Times(n,ArcCot(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(Times(CI,C1D2,n))),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5636,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(CN1,Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(a,c,n),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c))))),
IIntegrate(5637,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(CN1,Subtract(n,Times(a,x)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))))))),
IIntegrate(5638,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Plus(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Star(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x))))),x)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LtQ(p,CN1),NeQ(p,QQ(-3L,2L)),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Not(And(IntegerQ(p),IntegerQ(Times(CI,C1D2,n)))),Not(And(Not(IntegerQ(p)),IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1)))))))),
IIntegrate(5639,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(CN1,Plus(C1,Times(a,n,x)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(Sqr(a),c,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))))))),
IIntegrate(5640,Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Subtract(Times(C2,Plus(p,C1)),Times(a,n,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x)))),Power(Times(Sqr(a),c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Star(Times(n,Plus(Times(C2,p),C3),Power(Times(a,c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcCot(Times(a,x))))),x)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LeQ(p,CN1),NeQ(p,QQ(-3L,2L)),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),Not(And(IntegerQ(p),IntegerQ(Times(CI,C1D2,n)))),Not(And(Not(IntegerQ(p)),IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))))))))
  );
}
