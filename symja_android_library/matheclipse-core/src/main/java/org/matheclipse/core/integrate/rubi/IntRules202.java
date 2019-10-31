package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules202 { 
  public static IAST RULES = List( 
IIntegrate(5051,Int(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Int(Times(Log(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),Dist(Times(C1D2,CI),Int(Times(Log(Plus(C1,Times(CI,a),Times(CI,b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),RationalQ(n)))),
IIntegrate(5052,Int(Times(ArcCot(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Int(Times(Log(Times(Plus(CNI,a,Times(b,x)),Power(Plus(a,Times(b,x)),CN1))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x),Dist(Times(C1D2,CI),Int(Times(Log(Times(Plus(CI,a,Times(b,x)),Power(Plus(a,Times(b,x)),CN1))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),RationalQ(n)))),
IIntegrate(5053,Int(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcTan(Plus(a,Times(b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),And(FreeQ(List(a,b,c,d,n),x),Not(RationalQ(n))))),
IIntegrate(5054,Int(Times(ArcCot(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcCot(Plus(a,Times(b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),And(FreeQ(List(a,b,c,d,n),x),Not(RationalQ(n))))),
IIntegrate(5055,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(C,Power(d,CN2)),Times(C,Sqr(x),Power(d,CN2))),q),Power(Plus(a,Times(b,ArcTan(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,C,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,C),Times(BSymbol,d)),C0)))),
IIntegrate(5056,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(C,Power(d,CN2)),Times(C,Sqr(x),Power(d,CN2))),q),Power(Plus(a,Times(b,ArcCot(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,C,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,C),Times(BSymbol,d)),C0)))),
IIntegrate(5057,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(C,Power(d,CN2)),Times(C,Sqr(x),Power(d,CN2))),q),Power(Plus(a,Times(b,ArcTan(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,C,m,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,C),Times(BSymbol,d)),C0)))),
IIntegrate(5058,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(C,Power(d,CN2)),Times(C,Sqr(x),Power(d,CN2))),q),Power(Plus(a,Times(b,ArcCot(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,C,m,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,C),Times(BSymbol,d)),C0)))),
IIntegrate(5059,Int(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Int(Times(Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,Subtract(Times(CI,n),C1))),Sqrt(Plus(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(a,x),IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1)))))),
IIntegrate(5060,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,Plus(Times(CI,n),C1))),Power(Times(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,Subtract(Times(CI,n),C1))),Sqrt(Plus(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(List(a,m),x),IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1)))))),
IIntegrate(5061,Int(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,CI,n)),CN1)),x),And(FreeQ(List(a,n),x),Not(IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))))))),
IIntegrate(5062,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,CI,n)),CN1)),x),And(FreeQ(List(a,m,n),x),Not(IntegerQ(Times(C1D2,Subtract(Times(CI,n),C1))))))),
IIntegrate(5063,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(u,Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,CI,n)),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5064,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(c,Times(d,x)),p),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,CI,n)),CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(Or(IntegerQ(p),GtQ(c,C0)))))),
IIntegrate(5065,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,p),Int(Times(u,Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTan(Times(a,x)))),Power(Power(x,p),CN1)),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),IntegerQ(p)))),
IIntegrate(5066,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Dist(Times(Power(CN1,Times(C1D2,n)),Power(c,p)),Int(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Power(Subtract(C1,Power(Times(CI,a,x),CN1)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Power(Times(CI,a,x),CN1)),Times(C1D2,CI,n)),CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,CI,n)),GtQ(c,C0)))),
IIntegrate(5067,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Subtract(C1,Times(CI,a,x)),Times(C1D2,CI,n)),Power(Power(Plus(C1,Times(CI,a,x)),Times(C1D2,CI,n)),CN1)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,CI,n)),Not(GtQ(c,C0))))),
IIntegrate(5068,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Dist(Times(Power(x,p),Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),CN1)),Int(Times(u,Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTan(Times(a,x)))),Power(Power(x,p),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p))))),
IIntegrate(5069,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Plus(n,Times(a,x)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),Not(IntegerQ(Times(CI,n)))))),
IIntegrate(5070,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Subtract(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Dist(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Int(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTan(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c)),LtQ(p,CN1),Not(IntegerQ(Times(CI,n))),NeQ(Plus(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p))))),
IIntegrate(5071,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Exp(Times(n,ArcTan(Times(a,x)))),Power(Times(a,c,n),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(d,Times(Sqr(a),c))))),
IIntegrate(5072,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(Power(Plus(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(C1D2,CI,n))),Power(Subtract(C1,Times(CI,a,x)),Times(CI,n))),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(d,Times(Sqr(a),c)),IntegerQ(p),IntegerQ(Times(C1D2,Plus(Times(CI,n),C1))),Not(IntegerQ(Subtract(p,Times(C1D2,CI,n))))))),
IIntegrate(5073,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(Power(Subtract(C1,Times(CI,a,x)),Plus(p,Times(C1D2,CI,n))),Power(Plus(C1,Times(CI,a,x)),Subtract(p,Times(C1D2,CI,n)))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(d,Times(Sqr(a),c)),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(5074,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Power(c,Times(C1D2,CI,n)),Int(Times(Power(Plus(c,Times(d,Sqr(x))),Subtract(p,Times(C1D2,CI,n))),Power(Subtract(C1,Times(CI,a,x)),Times(CI,n))),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),IGtQ(Times(C1D2,CI,n),C0)))),
IIntegrate(5075,Int(Times(Exp(Times(ArcTan(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Power(Power(c,Times(C1D2,CI,n)),CN1),Int(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,Times(C1D2,CI,n))),Power(Power(Plus(C1,Times(CI,a,x)),Times(CI,n)),CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(d,Times(Sqr(a),c)),Not(Or(IntegerQ(p),GtQ(c,C0))),ILtQ(Times(C1D2,CI,n),C0))))
  );
}
