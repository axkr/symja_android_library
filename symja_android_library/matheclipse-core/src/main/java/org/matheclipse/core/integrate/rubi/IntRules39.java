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
public class IntRules39 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(1951,Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(x_,r_DEFAULT)))),x_Symbol),
    Condition(Plus(Simp(Times(ASymbol,Power(x,Plus(m,Negate(q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1)),Power(Times(a,Plus(m,Times(p,q),C1)),CN1)),x),Dist(Power(Times(a,Plus(m,Times(p,q),C1)),CN1),Int(Times(Power(x,Subtract(Plus(m,n),q)),Simp(Subtract(Subtract(Times(a,BSymbol,Plus(m,Times(p,q),C1)),Times(ASymbol,b,Plus(m,Times(p,q),Times(Subtract(n,q),Plus(p,C1)),C1))),Times(ASymbol,c,Plus(m,Times(p,q),Times(C2,Subtract(n,q),Plus(p,C1)),C1),Power(x,Subtract(n,q)))),x),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p)),x),x)),And(FreeQ(List(a,b,c,ASymbol,BSymbol),x),EqQ(r,Subtract(n,q)),EqQ(j,Subtract(Times(C2,n),q)),Not(IntegerQ(p)),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IGtQ(n,C0),RationalQ(m,p,q),Or(And(GeQ(p,CN1),LtQ(p,C0)),EqQ(Plus(m,Times(p,q),Times(Subtract(n,q),Plus(Times(C2,p),C1)),C1),C0)),LeQ(Plus(m,Times(p,q)),Negate(Subtract(n,q))),NeQ(Plus(m,Times(p,q),C1),C0))));
IIntegrate(1952,Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(x_,r_DEFAULT)))),x_Symbol),
    Condition(With(List(Set(n,Plus(q,r))),Condition(Plus(Simp(Times(ASymbol,Power(x,Plus(m,Negate(q),C1)),Power(Plus(Times(a,Power(x,q)),Times(c,Power(x,Subtract(Times(C2,n),q)))),Plus(p,C1)),Power(Times(a,Plus(m,Times(p,q),C1)),CN1)),x),Dist(Power(Times(a,Plus(m,Times(p,q),C1)),CN1),Int(Times(Power(x,Subtract(Plus(m,n),q)),Simp(Subtract(Times(a,BSymbol,Plus(m,Times(p,q),C1)),Times(ASymbol,c,Plus(m,Times(p,q),Times(C2,Subtract(n,q),Plus(p,C1)),C1),Power(x,Subtract(n,q)))),x),Power(Plus(Times(a,Power(x,q)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p)),x),x)),And(EqQ(j,Subtract(Times(C2,n),q)),IGtQ(n,C0),Or(And(GeQ(p,CN1),LtQ(p,C0)),EqQ(Plus(m,Times(p,q),Times(Subtract(n,q),Plus(Times(C2,p),C1)),C1),C0)),LeQ(Plus(m,Times(p,q)),Negate(Subtract(n,q))),NeQ(Plus(m,Times(p,q),C1),C0)))),And(FreeQ(List(a,c,ASymbol,BSymbol),x),Not(IntegerQ(p)),RationalQ(m,p,q))));
IIntegrate(1953,Int(Times(Power(x_,m_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(x_,j_DEFAULT))),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Power(x,Times(C1D2,q)),Sqrt(Plus(a,Times(b,Power(x,Subtract(n,q))),Times(c,Power(x,Times(C2,Subtract(n,q)))))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),CN1D2)),Int(Times(Power(x,Subtract(m,Times(C1D2,q))),Plus(ASymbol,Times(BSymbol,Power(x,Subtract(n,q)))),Power(Plus(a,Times(b,Power(x,Subtract(n,q))),Times(c,Power(x,Times(C2,Subtract(n,q))))),CN1D2)),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,m,n,q),x),EqQ(j,Subtract(n,q)),EqQ(r,Subtract(Times(C2,n),q)),PosQ(Subtract(n,q)),Or(EqQ(m,C1D2),EqQ(m,Negate(C1D2))),EqQ(n,C3),EqQ(q,C1))));
IIntegrate(1954,Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,k_DEFAULT)),Times(c_DEFAULT,Power(x_,n_DEFAULT))),p_),Plus(A_,Times(B_DEFAULT,Power(x_,q_)))),x_Symbol),
    Condition(Dist(Times(Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,k)),Times(c,Power(x,n))),p),Power(Times(Power(x,Times(j,p)),Power(Plus(a,Times(b,Power(x,Subtract(k,j))),Times(c,Power(x,Times(C2,Subtract(k,j))))),p)),CN1)),Int(Times(Power(x,Plus(m,Times(j,p))),Plus(ASymbol,Times(BSymbol,Power(x,Subtract(k,j)))),Power(Plus(a,Times(b,Power(x,Subtract(k,j))),Times(c,Power(x,Times(C2,Subtract(k,j))))),p)),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,j,k,m,p),x),EqQ(q,Subtract(k,j)),EqQ(n,Subtract(Times(C2,k),j)),Not(IntegerQ(p)),PosQ(Subtract(k,j)))));
IIntegrate(1955,Int(Times(Power(u_,m_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(u_,j_DEFAULT))),Power(Plus(Times(b_DEFAULT,Power(u_,n_DEFAULT)),Times(a_DEFAULT,Power(u_,q_DEFAULT)),Times(c_DEFAULT,Power(u_,r_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Int(Times(Power(x,m),Plus(ASymbol,Times(BSymbol,Power(x,Subtract(n,q)))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Subtract(Times(C2,n),q)))),p)),x),x,u),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,m,n,p,q),x),EqQ(j,Subtract(n,q)),EqQ(r,Subtract(Times(C2,n),q)),LinearQ(u,x),NeQ(u,x))));
IIntegrate(1956,Int(Times(Power(x_,m_DEFAULT),Power(Times(e_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT)),p_),Power(Times(f_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),s_)),q_)),x_Symbol),
    Condition(Dist(Times(Power(Times(e,Power(Plus(a,Times(b,Power(x,n))),r)),p),Power(Times(f,Power(Plus(c,Times(d,Power(x,n))),s)),q),Power(Times(Power(Plus(a,Times(b,Power(x,n))),Times(p,r)),Power(Plus(c,Times(d,Power(x,n))),Times(q,s))),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),Times(p,r)),Power(Plus(c,Times(d,Power(x,n))),Times(q,s))),x),x),FreeQ(List(a,b,c,d,e,f,m,n,p,q,r,s),x)));
IIntegrate(1957,Int(Times(u_DEFAULT,Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(Dist(Power(Times(b,e,Power(d,CN1)),p),Int(u,x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0))));
IIntegrate(1958,Int(Times(u_DEFAULT,Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(Int(Times(u,Power(Times(e,Plus(a,Times(b,Power(x,n)))),p),Power(Power(Plus(c,Times(d,Power(x,n))),p),CN1)),x),And(FreeQ(List(a,b,c,d,e,n,p),x),GtQ(Times(b,d,e),C0),GtQ(Subtract(c,Times(a,d,Power(b,CN1))),C0))));
IIntegrate(1959,Int(Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_),x_Symbol),
    Condition(With(List(Set(q,Denominator(p))),Dist(Times(q,e,Subtract(Times(b,c),Times(a,d)),Power(n,CN1)),Subst(Int(Times(Power(x,Subtract(Times(q,Plus(p,C1)),C1)),Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Subtract(Power(n,CN1),C1)),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Plus(Power(n,CN1),C1)),CN1)),x),x,Power(Times(e,Plus(a,Times(b,Power(x,n))),Power(Plus(c,Times(d,Power(x,n))),CN1)),Power(q,CN1))),x)),And(FreeQ(List(a,b,c,d,e),x),FractionQ(p),IntegerQ(Power(n,CN1)))));
IIntegrate(1960,Int(Times(Power(x_,m_DEFAULT),Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(With(List(Set(q,Denominator(p))),Dist(Times(q,e,Subtract(Times(b,c),Times(a,d)),Power(n,CN1)),Subst(Int(Times(Power(x,Subtract(Times(q,Plus(p,C1)),C1)),Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),CN1)),x),x,Power(Times(e,Plus(a,Times(b,Power(x,n))),Power(Plus(c,Times(d,Power(x,n))),CN1)),Power(q,CN1))),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),FractionQ(p),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))))));
IIntegrate(1961,Int(Times(Power(u_,r_DEFAULT),Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(With(List(Set(q,Denominator(p))),Dist(Times(q,e,Subtract(Times(b,c),Times(a,d)),Power(n,CN1)),Subst(Int(SimplifyIntegrand(Times(Power(x,Subtract(Times(q,Plus(p,C1)),C1)),Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Subtract(Power(n,CN1),C1)),Power(ReplaceAll(u,Rule(x,Times(Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Power(n,CN1)),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Power(n,CN1)),CN1)))),r),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Plus(Power(n,CN1),C1)),CN1)),x),x),x,Power(Times(e,Plus(a,Times(b,Power(x,n))),Power(Plus(c,Times(d,Power(x,n))),CN1)),Power(q,CN1))),x)),And(FreeQ(List(a,b,c,d,e),x),PolynomialQ(u,x),FractionQ(p),IntegerQ(Power(n,CN1)),IntegerQ(r))));
IIntegrate(1962,Int(Times(Power(u_,r_DEFAULT),Power(x_,m_DEFAULT),Power(Times(e_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),p_)),x_Symbol),
    Condition(With(List(Set(q,Denominator(p))),Dist(Times(q,e,Subtract(Times(b,c),Times(a,d)),Power(n,CN1)),Subst(Int(SimplifyIntegrand(Times(Power(x,Subtract(Times(q,Plus(p,C1)),C1)),Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(ReplaceAll(u,Rule(x,Times(Power(Plus(Times(CN1,a,e),Times(c,Power(x,q))),Power(n,CN1)),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Power(n,CN1)),CN1)))),r),Power(Power(Subtract(Times(b,e),Times(d,Power(x,q))),Plus(Times(Plus(m,C1),Power(n,CN1)),C1)),CN1)),x),x),x,Power(Times(e,Plus(a,Times(b,Power(x,n))),Power(Plus(c,Times(d,Power(x,n))),CN1)),Power(q,CN1))),x)),And(FreeQ(List(a,b,c,d,e),x),PolynomialQ(u,x),FractionQ(p),IntegerQ(Power(n,CN1)),IntegersQ(m,r))));
IIntegrate(1963,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,CN1)),n_))),p_),x_Symbol),
    Condition(Negate(Dist(c,Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(x,CN2)),x),x,Times(c,Power(x,CN1))),x)),FreeQ(List(a,b,c,n,p),x)));
IIntegrate(1964,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,CN1)),n_))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(c,Plus(m,C1)),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(c,Power(x,CN1))),x)),And(FreeQ(List(a,b,c,n,p),x),IntegerQ(m))));
IIntegrate(1965,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,CN1)),n_))),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_)),x_Symbol),
    Condition(Negate(Dist(Times(c,Power(Times(d,x),m),Power(Times(c,Power(x,CN1)),m)),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(c,Power(x,CN1))),x)),And(FreeQ(List(a,b,c,d,m,n,p),x),Not(IntegerQ(m)))));
IIntegrate(1966,Int(Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),$p("n2",true))),Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_))),p_DEFAULT),x_Symbol),
    Condition(Negate(Dist(d,Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(x,CN2)),x),x,Times(d,Power(x,CN1))),x)),And(FreeQ(List(a,b,c,d,n,p),x),EqQ($s("n2"),Times(C2,n)))));
IIntegrate(1967,Int(Times(Power(Plus(a_,Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),$p("n2",true))),Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(d,Plus(m,C1)),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(d,Power(x,CN1))),x)),And(FreeQ(List(a,b,c,d,n,p),x),EqQ($s("n2"),Times(C2,n)),IntegerQ(m))));
IIntegrate(1968,Int(Times(Power(Plus(a_,Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),$p("n2",true))),Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_))),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Negate(Dist(Times(d,Power(Times(e,x),m),Power(Times(d,Power(x,CN1)),m)),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(d,Power(x,CN1))),x)),And(FreeQ(List(a,b,c,d,e,m,n,p),x),EqQ($s("n2"),Times(C2,n)),Not(IntegerQ(m)))));
IIntegrate(1969,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_DEFAULT),x_Symbol),
    Condition(Negate(Dist(d,Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)),Power(Power(d,Times(C2,n)),CN1))),p),Power(x,CN2)),x),x,Times(d,Power(x,CN1))),x)),And(FreeQ(List(a,b,c,d,n,p),x),EqQ($s("n2"),Times(CN2,n)),IntegerQ(Times(C2,n)))));
IIntegrate(1970,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(d,Plus(m,C1)),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)),Power(Power(d,Times(C2,n)),CN1))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(d,Power(x,CN1))),x)),And(FreeQ(List(a,b,c,d,n,p),x),EqQ($s("n2"),Times(CN2,n)),IntegerQ(Times(C2,n)),IntegerQ(m))));
IIntegrate(1971,Int(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Times(d,Power(Times(e,x),m),Power(Times(d,Power(x,CN1)),m)),Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)),Power(Power(d,Times(C2,n)),CN1))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(d,Power(x,CN1))),x)),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ($s("n2"),Times(CN2,n)),Not(IntegerQ(m)),IntegerQ(Times(C2,n)))));
IIntegrate(1972,Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(1973,Int(Times(Power(u_,p_DEFAULT),Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(c,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(c,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(1974,Int(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(List(p,q),x),BinomialQ(List(u,v),x),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(v,x)),C0),Not(BinomialMatchQ(List(u,v),x)))));
IIntegrate(1975,Int(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(e,x),m),Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(List(e,m,p,q),x),BinomialQ(List(u,v),x),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(v,x)),C0),Not(BinomialMatchQ(List(u,v),x)))));
IIntegrate(1976,Int(Times(Power(u_,m_DEFAULT),Power(v_,p_DEFAULT),Power(w_,q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),p),Power(ExpandToSum(w,x),q)),x),And(FreeQ(List(m,p,q),x),BinomialQ(List(u,v,w),x),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(v,x)),C0),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(w,x)),C0),Not(BinomialMatchQ(List(u,v,w),x)))));
IIntegrate(1977,Int(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT),Power(Times(g_DEFAULT,x_),m_DEFAULT),Power(z_,r_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(g,x),m),Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q),Power(ExpandToSum(z,x),r)),x),And(FreeQ(List(g,m,p,q,r),x),BinomialQ(List(u,v,z),x),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(v,x)),C0),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(z,x)),C0),Not(BinomialMatchQ(List(u,v,z),x)))));
IIntegrate(1978,Int(Times($p("§pq"),Power(u_,p_DEFAULT),Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(c,x),m),$s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(c,m,p),x),PolyQ($s("§pq"),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(1979,Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),GeneralizedBinomialQ(u,x),Not(GeneralizedBinomialMatchQ(u,x)))));
IIntegrate(1980,Int(Times(Power(u_,p_DEFAULT),Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(c,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(c,m,p),x),GeneralizedBinomialQ(u,x),Not(GeneralizedBinomialMatchQ(u,x)))));
IIntegrate(1981,Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),QuadraticQ(u,x),Not(QuadraticMatchQ(u,x)))));
IIntegrate(1982,Int(Times(Power(u_,m_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),p)),x),And(FreeQ(List(m,p),x),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x))))));
IIntegrate(1983,Int(Times(Power(u_,m_DEFAULT),Power(v_,n_DEFAULT),Power(w_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),n),Power(ExpandToSum(w,x),p)),x),And(FreeQ(List(m,n,p),x),LinearQ(List(u,v),x),QuadraticQ(w,x),Not(And(LinearMatchQ(List(u,v),x),QuadraticMatchQ(w,x))))));
IIntegrate(1984,Int(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(List(p,q),x),QuadraticQ(List(u,v),x),Not(QuadraticMatchQ(List(u,v),x)))));
IIntegrate(1985,Int(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT),Power(z_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(z,x),m),Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(List(m,p,q),x),LinearQ(z,x),QuadraticQ(List(u,v),x),Not(And(LinearMatchQ(z,x),QuadraticMatchQ(List(u,v),x))))));
IIntegrate(1986,Int(Times($p("§pq"),Power(u_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times($s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),PolyQ($s("§pq"),x),QuadraticQ(u,x),Not(QuadraticMatchQ(u,x)))));
IIntegrate(1987,Int(Times($p("§pq"),Power(u_,m_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),$s("§pq"),Power(ExpandToSum(v,x),p)),x),And(FreeQ(List(m,p),x),PolyQ($s("§pq"),x),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x))))));
IIntegrate(1988,Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x)))));
IIntegrate(1989,Int(Times(Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(d,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(d,m,p),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x)))));
IIntegrate(1990,Int(Times(Power(u_,q_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),q),Power(ExpandToSum(v,x),p)),x),And(FreeQ(List(p,q),x),BinomialQ(u,x),TrinomialQ(v,x),Not(And(BinomialMatchQ(u,x),TrinomialMatchQ(v,x))))));
IIntegrate(1991,Int(Times(Power(u_,q_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),q),Power(ExpandToSum(v,x),p)),x),And(FreeQ(List(p,q),x),BinomialQ(u,x),BinomialQ(v,x),Not(And(BinomialMatchQ(u,x),BinomialMatchQ(v,x))))));
IIntegrate(1992,Int(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(f,x),m),Power(ExpandToSum(z,x),q),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(f,m,p,q),x),BinomialQ(z,x),TrinomialQ(u,x),Not(And(BinomialMatchQ(z,x),TrinomialMatchQ(u,x))))));
IIntegrate(1993,Int(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(f,x),m),Power(ExpandToSum(z,x),q),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(f,m,p,q),x),BinomialQ(z,x),BinomialQ(u,x),Not(And(BinomialMatchQ(z,x),BinomialMatchQ(u,x))))));
IIntegrate(1994,Int(Times($p("§pq"),Power(u_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times($s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),PolyQ($s("§pq"),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x)))));
IIntegrate(1995,Int(Times($p("§pq"),Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(d,x),m),$s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(d,m,p),x),PolyQ($s("§pq"),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x)))));
IIntegrate(1996,Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),GeneralizedTrinomialQ(u,x),Not(GeneralizedTrinomialMatchQ(u,x)))));
IIntegrate(1997,Int(Times(Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(d,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(d,m,p),x),GeneralizedTrinomialQ(u,x),Not(GeneralizedTrinomialMatchQ(u,x)))));
IIntegrate(1998,Int(Times(Power(u_,p_DEFAULT),z_),x_Symbol),
    Condition(Int(Times(ExpandToSum(z,x),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),BinomialQ(z,x),GeneralizedTrinomialQ(u,x),EqQ(Subtract(BinomialDegree(z,x),GeneralizedTrinomialDegree(u,x)),C0),Not(And(BinomialMatchQ(z,x),GeneralizedTrinomialMatchQ(u,x))))));
IIntegrate(1999,Int(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),z_),x_Symbol),
    Condition(Int(Times(Power(Times(f,x),m),ExpandToSum(z,x),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(f,m,p),x),BinomialQ(z,x),GeneralizedTrinomialQ(u,x),EqQ(Subtract(BinomialDegree(z,x),GeneralizedTrinomialDegree(u,x)),C0),Not(And(BinomialMatchQ(z,x),GeneralizedTrinomialMatchQ(u,x))))));
IIntegrate(2000,Int(Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_),x_Symbol),
    Condition(Simp(Times(Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Subtract(n,j),Plus(p,C1),Power(x,Subtract(n,C1))),CN1)),x),And(FreeQ(List(a,b,j,n,p),x),Not(IntegerQ(p)),NeQ(n,j),EqQ(Plus(Times(j,p),Negate(n),j,C1),C0))));
  }
}
}
