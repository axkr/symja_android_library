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
public class IntRules1 { 
  public static IAST RULES = List( 
IIntegrate(26,Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,j_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(CN1,Sqr(b),Power(d,CN1)),m),Int(Times(u,Power(Power(Subtract(a,Times(b,Power(x,n))),m),CN1)),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),EqQ(j,Times(C2,n)),EqQ(p,Negate(m)),EqQ(Plus(Times(Sqr(b),c),Times(Sqr(a),d)),C0),GtQ(a,C0),LtQ(d,C0)))),
IIntegrate(27,Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Cancel(Times(Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p)),Power(Power(c,p),CN1)))),x),And(FreeQ(List(a,b,c),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(p)))),
IIntegrate(28,Int(Times(u_DEFAULT,Power(Plus(a_,Times(c_DEFAULT,Power(x_,$p("n2",true))),Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(c,p),CN1),Int(Times(u,Power(Plus(Times(C1D2,b),Times(c,Power(x,n))),Times(C2,p))),x),x),And(FreeQ(List(a,b,c,n),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(p)))),
IIntegrate(29,Int(Power(x_,CN1),x_Symbol),
    Simp(Log(x),x)),
IIntegrate(30,Int(Power(x_,m_DEFAULT),x_Symbol),
    Condition(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(m,C1),CN1)),x),And(FreeQ(m,x),NeQ(m,CN1)))),
IIntegrate(31,Int(Power(Plus(a_,Times(b_DEFAULT,x_)),CN1),x_Symbol),
    Condition(Simp(Times(Log(RemoveContent(Plus(a,Times(b,x)),x)),Power(b,CN1)),x),FreeQ(List(a,b),x))),
IIntegrate(32,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Times(b,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,m),x),NeQ(m,CN1)))),
IIntegrate(33,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,u_)),m_),x_Symbol),
    Condition(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Int(Power(Plus(a,Times(b,x)),m),x),x,u),x),And(FreeQ(List(a,b,m),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(34,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(d,x,Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Times(b,Plus(m,C2)),CN1)),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Subtract(Times(a,d),Times(b,c,Plus(m,C2))),C0)))),
IIntegrate(35,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Int(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),CN1),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0)))),
IIntegrate(36,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Dist(Times(b,Power(Subtract(Times(b,c),Times(a,d)),CN1)),Int(Power(Plus(a,Times(b,x)),CN1),x),x),Dist(Times(d,Power(Subtract(Times(b,c),Times(a,d)),CN1)),Int(Power(Plus(c,Times(d,x)),CN1),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(37,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),x),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(m,n,C2),C0),NeQ(m,CN1)))),
IIntegrate(38,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),m),Power(Plus(Times(C2,m),C1),CN1)),x),Dist(Times(C2,a,c,m,Power(Plus(Times(C2,m),C1),CN1)),Int(Times(Power(Plus(a,Times(b,x)),Subtract(m,C1)),Power(Plus(c,Times(d,x)),Subtract(m,C1))),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),IGtQ(Plus(m,C1D2),C0)))),
IIntegrate(39,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-3L,2L)),Power(Plus(c_,Times(d_DEFAULT,x_)),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(x,Power(Times(a,c,Sqrt(Plus(a,Times(b,x))),Sqrt(Plus(c,Times(d,x)))),CN1)),x),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0)))),
IIntegrate(40,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(x,Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(C2,a,c,Plus(m,C1)),CN1)),x)),Dist(Times(Plus(Times(C2,m),C3),Power(Times(C2,a,c,Plus(m,C1)),CN1)),Int(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(m,C1))),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),ILtQ(Plus(m,QQ(3L,2L)),C0)))),
IIntegrate(41,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),m),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Or(IntegerQ(m),And(GtQ(a,C0),GtQ(c,C0)))))),
IIntegrate(42,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,x)),FracPart(m)),Power(Plus(c,Times(d,x)),FracPart(m)),Power(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),FracPart(m)),CN1)),Int(Power(Plus(Times(a,c),Times(b,d,Sqr(x))),m),x),x),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),Not(IntegerQ(Times(C2,m)))))),
IIntegrate(43,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IGtQ(m,C0),Or(Not(IntegerQ(n)),And(EqQ(c,C0),LeQ(Plus(Times(C7,m),Times(C4,n),C4),C0)),LtQ(Plus(Times(C9,m),Times(C5,Plus(n,C1))),C0),GtQ(Plus(m,n,C2),C0))))),
IIntegrate(44,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),ILtQ(m,C0),IntegerQ(n),Not(And(IGtQ(n,C0),LtQ(Plus(m,n,C2),C0)))))),
IIntegrate(45,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),x),Dist(Times(d,Simplify(Plus(m,n,C2)),Power(Times(Subtract(Times(b,c),Times(a,d)),Plus(m,C1)),CN1)),Int(Times(Power(Plus(a,Times(b,x)),Simplify(Plus(m,C1))),Power(Plus(c,Times(d,x)),n)),x),x)),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),ILtQ(Simplify(Plus(m,n,C2)),C0),NeQ(m,CN1),Not(And(LtQ(m,CN1),LtQ(n,CN1),Or(EqQ(a,C0),And(NeQ(c,C0),LtQ(Subtract(m,n),C0),IntegerQ(n))))),Or(SumSimplerQ(m,C1),Not(SumSimplerQ(n,C1)))))),
IIntegrate(46,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-9L,4L)),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D4)),x_Symbol),
    Condition(Subtract(Simp(Times(CN4,Power(Times(C5,b,Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),C1D4)),CN1)),x),Dist(Times(d,Power(Times(C5,b),CN1)),Int(Power(Times(Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),QQ(5L,4L))),CN1),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),NegQ(Times(Sqr(a),Sqr(b)))))),
IIntegrate(47,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,C1)),CN1)),x),Dist(Times(d,n,Power(Times(b,Plus(m,C1)),CN1)),Int(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),GtQ(n,C0),LtQ(m,CN1),Not(And(IntegerQ(n),Not(IntegerQ(m)))),Not(And(ILeQ(Plus(m,n,C2),C0),Or(FractionQ(m),GeQ(Plus(Times(C2,n),m,C1),C0)))),IntLinearQ(a,b,c,d,m,n,x)))),
IIntegrate(48,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),QQ(-5L,4L)),Power(Plus(c_,Times(d_DEFAULT,x_)),CN1D4)),x_Symbol),
    Condition(Plus(Simp(Times(CN2,Power(Times(b,Power(Plus(a,Times(b,x)),C1D4),Power(Plus(c,Times(d,x)),C1D4)),CN1)),x),Dist(c,Int(Power(Times(Power(Plus(a,Times(b,x)),QQ(5L,4L)),Power(Plus(c,Times(d,x)),QQ(5L,4L))),CN1),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),NegQ(Times(Sqr(a),Sqr(b)))))),
IIntegrate(49,Int(Times(Power(Plus(a_,Times(b_DEFAULT,x_)),m_),Power(Plus(c_,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,n,C1)),CN1)),x),Dist(Times(C2,c,n,Power(Plus(m,n,C1),CN1)),Int(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Plus(Times(b,c),Times(a,d)),C0),IGtQ(Plus(m,C1D2),C0),IGtQ(Plus(n,C1D2),C0),LtQ(m,n)))),
IIntegrate(50,Int(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),n),Power(Times(b,Plus(m,n,C1)),CN1)),x),Dist(Times(n,Subtract(Times(b,c),Times(a,d)),Power(Times(b,Plus(m,n,C1)),CN1)),Int(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),Subtract(n,C1))),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),GtQ(n,C0),NeQ(Plus(m,n,C1),C0),Not(And(IGtQ(m,C0),Or(Not(IntegerQ(n)),And(GtQ(m,C0),LtQ(Subtract(m,n),C0))))),Not(ILtQ(Plus(m,n,C2),C0)),IntLinearQ(a,b,c,d,m,n,x))))
  );
}
