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
public class IntRules246 { 
  public static IAST RULES = List( 
IIntegrate(6151,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,Times(C1D2,n)),Int(Times(Power(x,m),Power(Plus(c,Times(d,Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),IGtQ(Times(C1D2,n),C0)))),
IIntegrate(6152,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(c,Times(C1D2,n)),CN1),Int(Times(Power(x,m),Power(Plus(c,Times(d,Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),ILtQ(Times(C1D2,n),C0)))),
IIntegrate(6153,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Int(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6154,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(u,Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0))))),
IIntegrate(6155,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Times(Power(Subtract(C1,Times(a,x)),FracPart(p)),Power(Plus(C1,Times(a,x)),FracPart(p))),CN1)),Int(Times(u,Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),IntegerQ(Times(C1D2,n))))),
IIntegrate(6156,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Int(Times(u,Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6157,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,p),Int(Times(u,Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Power(x,Times(C2,p)),CN1)),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),IntegerQ(p)))),
IIntegrate(6158,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(u,Power(Subtract(C1,Power(Times(a,x),CN1)),p),Power(Plus(C1,Power(Times(a,x),CN1)),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,n)),GtQ(c,C0)))),
IIntegrate(6159,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Dist(Times(Power(x,Times(C2,p)),Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Times(Power(Subtract(C1,Times(a,x)),p),Power(Plus(C1,Times(a,x)),p)),CN1)),Int(Times(u,Power(Subtract(C1,Times(a,x)),p),Power(Plus(C1,Times(a,x)),p),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Power(x,Times(C2,p)),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,n)),Not(GtQ(c,C0))))),
IIntegrate(6160,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN2))),p_)),x_Symbol),
    Condition(Dist(Times(Power(x,Times(C2,p)),Power(Plus(c,Times(d,Power(x,CN2))),p),Power(Power(Plus(C1,Times(c,Sqr(x),Power(d,CN1))),p),CN1)),Int(Times(u,Power(Plus(C1,Times(c,Sqr(x),Power(d,CN1))),p),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Power(x,Times(C2,p)),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(c,Times(Sqr(a),d)),C0),Not(IntegerQ(p)),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6161,Int(Exp(Times(ArcTanh(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(C1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),Power(Power(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)),Times(C1D2,n)),CN1)),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6162,Int(Times(Exp(Times(ArcTanh(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_)),Power(x_,m_)),x_Symbol),
    Condition(Dist(Times(C4,Power(Times(n,Power(b,Plus(m,C1)),Power(c,Plus(m,C1))),CN1)),Subst(Int(Times(Power(x,Times(C2,Power(n,CN1))),Power(Plus(CN1,Times(CN1,a,c),Times(Subtract(C1,Times(a,c)),Power(x,Times(C2,Power(n,CN1))))),m),Power(Power(Plus(C1,Power(x,Times(C2,Power(n,CN1)))),Plus(m,C2)),CN1)),x),x,Times(Power(Plus(C1,Times(c,Plus(a,Times(b,x)))),Times(C1D2,n)),Power(Power(Subtract(C1,Times(c,Plus(a,Times(b,x)))),Times(C1D2,n)),CN1))),x),And(FreeQ(List(a,b,c),x),ILtQ(m,C0),LtQ(CN1,n,C1)))),
IIntegrate(6163,Int(Times(Exp(Times(ArcTanh(Times(c_DEFAULT,Plus(a_,Times(b_DEFAULT,x_)))),n_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(d,Times(e,x)),m),Power(Plus(C1,Times(a,c),Times(b,c,x)),Times(C1D2,n)),Power(Power(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)),Times(C1D2,n)),CN1)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6164,Int(Times(Exp(Times(ArcTanh(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),p),Int(Times(u,Power(Subtract(Subtract(C1,a),Times(b,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,a,Times(b,x)),Plus(p,Times(C1D2,n)))),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Subtract(Times(b,d),Times(C2,a,e)),C0),EqQ(Plus(Times(Sqr(b),c),Times(e,Subtract(C1,Sqr(a)))),C0),Or(IntegerQ(p),GtQ(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),C0))))),
IIntegrate(6165,Int(Times(Exp(Times(ArcTanh(Plus(a_,Times(b_DEFAULT,x_))),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_),Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Plus(c,Times(d,x),Times(e,Sqr(x))),p),Power(Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,x)),Times(Sqr(b),Sqr(x))),p),CN1)),Int(Times(u,Power(Subtract(Subtract(Subtract(C1,Sqr(a)),Times(C2,a,b,x)),Times(Sqr(b),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Subtract(Times(b,d),Times(C2,a,e)),C0),EqQ(Plus(Times(Sqr(b),c),Times(e,Subtract(C1,Sqr(a)))),C0),Not(Or(IntegerQ(p),GtQ(Times(c,Power(Subtract(C1,Sqr(a)),CN1)),C0)))))),
IIntegrate(6166,Int(Times(Exp(Times(ArcTanh(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1))),n_DEFAULT)),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Exp(Times(n,ArcCoth(Plus(Times(a,Power(c,CN1)),Times(b,x,Power(c,CN1))))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(6167,Int(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),u_DEFAULT),x_Symbol),
    Condition(Dist(Power(CN1,Times(C1D2,n)),Int(Times(u,Exp(Times(n,ArcTanh(Times(a,x))))),x),x),And(FreeQ(a,x),IntegerQ(Times(C1D2,n))))),
IIntegrate(6168,Int(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,Plus(n,C1))),Power(Times(Sqr(x),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,Subtract(n,C1))),Sqrt(Subtract(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(a,x),IntegerQ(Times(C1D2,Subtract(n,C1)))))),
IIntegrate(6169,Int(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,Plus(n,C1))),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,Subtract(n,C1))),Sqrt(Subtract(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(a,x),IntegerQ(Times(C1D2,Subtract(n,C1))),IntegerQ(m)))),
IIntegrate(6170,Int(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Sqr(x),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,n),x),Not(IntegerQ(n))))),
IIntegrate(6171,Int(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,n),x),Not(IntegerQ(n)),IntegerQ(m)))),
IIntegrate(6172,Int(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(x,m),Power(Power(x,CN1),m)),Subst(Int(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,Plus(n,C1))),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,Subtract(n,C1))),Sqrt(Subtract(C1,Times(Sqr(x),Power(a,CN2))))),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,m),x),IntegerQ(Times(C1D2,Subtract(n,C1))),Not(IntegerQ(m))))),
IIntegrate(6173,Int(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_)),Power(x_,m_)),x_Symbol),
    Condition(Negate(Dist(Times(Power(x,m),Power(Power(x,CN1),m)),Subst(Int(Times(Power(Plus(C1,Times(x,Power(a,CN1))),Times(C1D2,n)),Power(Times(Power(x,Plus(m,C2)),Power(Subtract(C1,Times(x,Power(a,CN1))),Times(C1D2,n))),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,m,n),x),Not(IntegerQ(n)),Not(IntegerQ(m))))),
IIntegrate(6174,Int(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Plus(C1,Times(a,x)),Power(Plus(c,Times(d,x)),p),Exp(Times(n,ArcCoth(Times(a,x)))),Power(Times(a,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(a,c),d),C0),EqQ(p,Times(C1D2,n)),Not(IntegerQ(Times(C1D2,n)))))),
IIntegrate(6175,Int(Times(Exp(Times(ArcCoth(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,p),Int(Times(u,Power(x,p),Power(Plus(C1,Times(c,Power(Times(d,x),CN1))),p),Exp(Times(n,ArcCoth(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(IntegerQ(Times(C1D2,n))),IntegerQ(p))))
  );
}
