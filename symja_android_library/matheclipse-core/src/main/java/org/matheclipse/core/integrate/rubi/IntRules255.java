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
public class IntRules255 { 
  public static IAST RULES = List( 
IIntegrate(6376,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(b,Exp(c),Sqr(x),HypergeometricPFQ(List(C1,C1),List(QQ(3L,2L),C2),Times(Sqr(b),Sqr(x))),Power(Pi,CN1D2)),x),And(FreeQ(List(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6377,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Int(Exp(Plus(c,Times(d,Sqr(x)))),x),Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x))),x)),And(FreeQ(List(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6378,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(b,Exp(c),Sqr(x),HypergeometricPFQ(List(C1,C1),List(QQ(3L,2L),C2),Times(CN1,Sqr(b),Sqr(x))),Power(Pi,CN1D2)),x),And(FreeQ(List(b,c,d),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6379,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Erf(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6380,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Erfc(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6381,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Erfi(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6382,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Dist(Times(b,Power(Times(d,Sqrt(Pi)),CN1)),Int(Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x)))),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(6383,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_),x_Symbol),
    Condition(Plus(Simp(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Dist(Times(b,Power(Times(d,Sqrt(Pi)),CN1)),Int(Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x)))),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(6384,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_),x_Symbol),
    Condition(Subtract(Simp(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Dist(Times(b,Power(Times(d,Sqrt(Pi)),CN1)),Int(Exp(Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),d),Sqr(x)))),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(6385,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Negate(Dist(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Int(Times(Power(x,Subtract(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x)))),x),x)),Negate(Dist(Times(b,Power(Times(d,Sqrt(Pi)),CN1)),Int(Times(Power(x,Subtract(m,C1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C1)))),
IIntegrate(6386,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Negate(Dist(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Int(Times(Power(x,Subtract(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x)))),x),x)),Dist(Times(b,Power(Times(d,Sqrt(Pi)),CN1)),Int(Times(Power(x,Subtract(m,C1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C1)))),
IIntegrate(6387,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Subtract(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Times(C2,d),CN1)),x),Negate(Dist(Times(Subtract(m,C1),Power(Times(C2,d),CN1)),Int(Times(Power(x,Subtract(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x)))),x),x)),Negate(Dist(Times(b,Power(Times(d,Sqrt(Pi)),CN1)),Int(Times(Power(x,Subtract(m,C1)),Exp(Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),d),Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C1)))),
IIntegrate(6388,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,Exp(c),x,HypergeometricPFQ(List(C1D2,C1),List(QQ(3L,2L),QQ(3L,2L)),Times(Sqr(b),Sqr(x))),Power(Pi,CN1D2)),x),And(FreeQ(List(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6389,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(x,CN1)),x),Int(Times(Exp(Plus(c,Times(d,Sqr(x)))),Erf(Times(b,x)),Power(x,CN1)),x)),And(FreeQ(List(b,c,d),x),EqQ(d,Sqr(b))))),
IIntegrate(6390,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(C2,b,Exp(c),x,HypergeometricPFQ(List(C1D2,C1),List(QQ(3L,2L),QQ(3L,2L)),Times(CN1,Sqr(b),Sqr(x))),Power(Pi,CN1D2)),x),And(FreeQ(List(b,c,d),x),EqQ(d,Negate(Sqr(b)))))),
IIntegrate(6391,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),x),Negate(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erf(Plus(a,Times(b,x)))),x),x)),Negate(Dist(Times(C2,b,Power(Times(Plus(m,C1),Sqrt(Pi)),CN1)),Int(Times(Power(x,Plus(m,C1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(6392,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),x),Negate(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfc(Plus(a,Times(b,x)))),x),x)),Dist(Times(C2,b,Power(Times(Plus(m,C1),Sqrt(Pi)),CN1)),Int(Times(Power(x,Plus(m,C1)),Exp(Subtract(Subtract(Plus(Negate(Sqr(a)),c),Times(C2,a,b,x)),Times(Subtract(Sqr(b),d),Sqr(x))))),x),x)),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(6393,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),x),Negate(Dist(Times(C2,d,Power(Plus(m,C1),CN1)),Int(Times(Power(x,Plus(m,C2)),Exp(Plus(c,Times(d,Sqr(x)))),Erfi(Plus(a,Times(b,x)))),x),x)),Negate(Dist(Times(C2,b,Power(Times(Plus(m,C1),Sqrt(Pi)),CN1)),Int(Times(Power(x,Plus(m,C1)),Exp(Plus(Sqr(a),c,Times(C2,a,b,x),Times(Plus(Sqr(b),d),Sqr(x))))),x),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(m,CN1)))),
IIntegrate(6394,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Times(e,x),m),Power(Erf(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6395,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Times(e,x),m),Power(Erfc(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6396,Int(Times(Exp(Plus(c_DEFAULT,Times(d_DEFAULT,Sqr(x_)))),Power(Erfi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Exp(Plus(c,Times(d,Sqr(x)))),Power(Times(e,x),m),Power(Erfi(Plus(a,Times(b,x))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(6397,Int(Erf(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Erf(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(C2,b,d,n,Power(Pi,CN1D2)),Int(Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6398,Int(Erfc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Erfc(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(C2,b,d,n,Power(Pi,CN1D2)),Int(Power(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6399,Int(Erfi(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Erfi(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(C2,b,d,n,Power(Pi,CN1D2)),Int(Exp(Sqr(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(6400,Int(Times(Power(x_,CN1),$(F_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(F(Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n),x),MemberQ(List(Erf,Erfc,Erfi),FSymbol))))
  );
}
