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
class IntRules279 { 
  public static IAST RULES = List( 
IIntegrate(5581,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCot(Plus(c,Times(d,x))))),p),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,p,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCot(Plus(c,Times(d,x))))),Plus(p,CN1)),Power(Plus(C1,Sqr(Plus(c,Times(d,x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(m,CN1)))),
IIntegrate(5582,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcTan(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),IGtQ(p,C0)))),
IIntegrate(5583,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCot(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),IGtQ(p,C0)))),
IIntegrate(5584,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcTan(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5585,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCot(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5586,Integrate(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Plus(C1,Times(CI,a),Times(CI,b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),RationalQ(n)))),
IIntegrate(5587,Integrate(Times(ArcCot(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Times(Plus(CNI,a,Times(b,x)),Power(Plus(a,Times(b,x)),CN1))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Times(Plus(CI,a,Times(b,x)),Power(Plus(a,Times(b,x)),CN1))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),RationalQ(n)))),
IIntegrate(5588,Integrate(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcTan(Plus(a,Times(b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),And(FreeQ(List(a,b,c,d,n),x),Not(RationalQ(n))))),
IIntegrate(5589,Integrate(Times(ArcCot(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcCot(Plus(a,Times(b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),And(FreeQ(List(a,b,c,d,n),x),Not(RationalQ(n))))),
IIntegrate(5590,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(5591,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(5592,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(5593,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(5594,Integrate(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Integrate(Times(Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,Plus(Times(CI,n),CN1))),Sqrt(Plus(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(a,x),IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1)))))),
IIntegrate(5595,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,Plus(Times(CI,n),CN1))),Sqrt(Plus(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(list(a,m),x),IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1)))))),
IIntegrate(5596,Integrate(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Subtract(C1,Times(CI,a,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,C1D2,n)),CN1)),x),And(FreeQ(list(a,n),x),Not(IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1))))))),
IIntegrate(5597,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Subtract(C1,Times(CI,a,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,C1D2,n)),CN1)),x),And(FreeQ(list(a,m,n),x),Not(IntegerQ(Times(C1D2,Plus(Times(CI,n),CN1))))))),
IIntegrate(5598,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(c,p),Integrate(Times(u,Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Subtract(C1,Times(CI,a,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,C1D2,n)),CN1)),x),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5599,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Plus(c,Times(d,x)),p),Power(Subtract(C1,Times(CI,a,x)),Times(CI,C1D2,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,C1D2,n)),CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5600,Integrate(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,p),Integrate(Times(u,Power(Power(x,p),CN1),Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTan(Times(a,x))))),x),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),IntegerQ(p))))
  );
}
