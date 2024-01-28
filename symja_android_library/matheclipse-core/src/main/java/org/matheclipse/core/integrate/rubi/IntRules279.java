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
class IntRules279 { 
  public static IAST RULES = List( 
IIntegrate(5581,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(5582,Integrate(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Integrate(Times(Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,Subtract(Times(CI,n),C1))),Sqrt(Plus(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(a,x),IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1)))))),
IIntegrate(5583,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,Subtract(Times(CI,n),C1))),Sqrt(Plus(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(list(a,m),x),IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1)))))),
IIntegrate(5584,Integrate(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Subtract(C1,Times(CI,a,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,C1D2,n)),CN1)),x),And(FreeQ(list(a,n),x),Not(IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))))))),
IIntegrate(5585,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Subtract(C1,Times(CI,a,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,C1D2,n)),CN1)),x),And(FreeQ(list(a,m,n),x),Not(IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))))))),
IIntegrate(5586,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(u,Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Subtract(C1,Times(CI,a,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,C1D2,n)),CN1)),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5587,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(c,Times(d,x)),p),Power(Subtract(C1,Times(CI,a,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,C1D2,n)),CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5588,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,p),Integrate(Times(u,Power(Power(x,p),CN1),Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTan(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),IntegerQ(p)))),
IIntegrate(5589,Integrate(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(CN1,Times(C1D2,n)),Power(c,p)),Integrate(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Power(Subtract(C1,Power(Times(CI,a,x),CN1)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Power(Times(CI,a,x),CN1)),Times(CI,C1D2,n)),CN1)),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(CI,C1D2,n)),GtQ(c,C0)))),
IIntegrate(5590,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Subtract(C1,Times(CI,a,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,C1D2,n)),CN1)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(CI,C1D2,n)),Not(GtQ(c,C0))))),
IIntegrate(5591,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(x,p),Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),CN1)),Integrate(Times(u,Power(Power(x,p),CN1),Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTan(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p))))),
IIntegrate(5592,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Plus(n,Times(a,x)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(CI,n)))))),
IIntegrate(5593,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Subtract(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Simp(Star(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x))))),x)),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LtQ(p,CN1),Not(IntegerQ(Times(CI,n))),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p))))),
IIntegrate(5594,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,c,n),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c))))),
IIntegrate(5595,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(Power(Plus(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(CI,C1D2,n))),Power(Subtract(C1,Times(CI,a,x)),Times(CI,n))),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(d,Times(Sqr(a),c)),IntegerQ(p),IntegerQ(Times(C1D2,Plus(Times(CI,n),C1))),Not(IntegerQ(Subtract(p,Times(CI,C1D2,n))))))),
IIntegrate(5596,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,p),Integrate(Times(Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(CI,C1D2,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(CI,C1D2,n)))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5597,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Power(c,Times(CI,C1D2,n)),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Subtract(p,Times(CI,C1D2,n))),Power(Subtract(C1,Times(CI,a,x)),Times(CI,n))),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),IGtQ(Times(CI,C1D2,n),C0)))),
IIntegrate(5598,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Power(Power(c,Times(CI,C1D2,n)),CN1),Integrate(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,Times(CI,C1D2,n))),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,n)),CN1)),x)),x),And(FreeQ(List(a,c,d,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),ILtQ(Times(CI,C1D2,n),C0)))),
IIntegrate(5599,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Plus(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Integrate(Times(Power(Plus(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTan(Times(a,x))))),x)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5600,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(CN1,Subtract(C1,Times(a,n,x)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(d,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(CI,n))))))
  );
}
