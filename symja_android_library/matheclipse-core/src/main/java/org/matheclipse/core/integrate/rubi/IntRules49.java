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
public class IntRules49 { 
  public static IAST RULES = List( 
IIntegrate(1226,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN2),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Plus(Simp(Times(x,Sqrt(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4)))),Power(Times(C2,d,Plus(d,Times(e,Sqr(x)))),CN1)),x),Dist(Times(c,Power(Times(C2,d,Sqr(e)),CN1)),Int(Times(Subtract(d,Times(e,Sqr(x))),Power(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),CN1D2)),x),x),Negate(Dist(Times(Subtract(Times(c,Sqr(d)),Times(a,Sqr(e))),Power(Times(C2,d,Sqr(e)),CN1)),Int(Power(Times(Plus(d,Times(e,Sqr(x))),Sqrt(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))))),CN1),x),x))),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0)))),
IIntegrate(1227,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN2),Sqrt(Plus(a_,Times(c_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Plus(Simp(Times(x,Sqrt(Plus(a,Times(c,Power(x,C4)))),Power(Times(C2,d,Plus(d,Times(e,Sqr(x)))),CN1)),x),Dist(Times(c,Power(Times(C2,d,Sqr(e)),CN1)),Int(Times(Subtract(d,Times(e,Sqr(x))),Power(Plus(a,Times(c,Power(x,C4))),CN1D2)),x),x),Negate(Dist(Times(Subtract(Times(c,Sqr(d)),Times(a,Sqr(e))),Power(Times(C2,d,Sqr(e)),CN1)),Int(Power(Times(Plus(d,Times(e,Sqr(x))),Sqrt(Plus(a,Times(c,Power(x,C4))))),CN1),x),x))),And(FreeQ(List(a,c,d,e),x),NeQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0)))),
IIntegrate(1228,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_)),x_Symbol),
    Condition(Module(List($s("aa"),$s("bb"),$s("cc")),Int(ReplaceAll(ExpandIntegrand(Power(Plus($s("aa"),Times($s("bb"),Sqr(x)),Times($s("cc"),Power(x,C4))),CN1D2),Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus($s("aa"),Times($s("bb"),Sqr(x)),Times($s("cc"),Power(x,C4))),Plus(p,C1D2))),x),List(Rule($s("aa"),a),Rule($s("bb"),b),Rule($s("cc"),c))),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0),ILtQ(q,C0),IntegerQ(Plus(p,C1D2))))),
IIntegrate(1229,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_)),x_Symbol),
    Condition(Module(List($s("aa"),$s("cc")),Int(ReplaceAll(ExpandIntegrand(Power(Plus($s("aa"),Times($s("cc"),Power(x,C4))),CN1D2),Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus($s("aa"),Times($s("cc"),Power(x,C4))),Plus(p,C1D2))),x),List(Rule($s("aa"),a),Rule($s("cc"),c))),x)),And(FreeQ(List(a,c,d,e),x),NeQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),ILtQ(q,C0),IntegerQ(Plus(p,C1D2))))),
IIntegrate(1230,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(Simp(Times(C1,EllipticF(Times(C2,ArcSin(Times(Rt(Times(CN1,e,Power(d,CN1)),C2),x))),Times(b,d,Power(Times(C4,a,e),CN1))),Power(Times(C2,Sqrt(a),Sqrt(d),Rt(Times(CN1,e,Power(d,CN1)),C2)),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(c,d),Times(b,e)),C0),GtQ(a,C0),GtQ(d,C0)))),
IIntegrate(1231,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(Plus(d,Times(e,Sqr(x))),Power(d,CN1))),Sqrt(Times(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),Power(a,CN1))),Power(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))))),CN1)),Int(Power(Times(Sqrt(Plus(C1,Times(e,Sqr(x),Power(d,CN1)))),Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1)),Times(c,Power(x,C4),Power(a,CN1))))),CN1),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(c,d),Times(b,e)),C0),Not(And(GtQ(a,C0),GtQ(d,C0)))))),
IIntegrate(1232,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Power(x,C3),Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(c,Times(b,Power(x,CN2)),Times(a,Power(x,CN4)))),Power(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))))),CN1)),Int(Power(Times(Power(x,C3),Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(c,Times(b,Power(x,CN2)),Times(a,Power(x,CN4))))),CN1),x),x),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0)))),
IIntegrate(1233,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Power(x,C3),Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(c,Times(a,Power(x,CN4)))),Power(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Plus(a,Times(c,Power(x,C4))))),CN1)),Int(Power(Times(Power(x,C3),Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(c,Times(a,Power(x,CN4))))),CN1),x),x),And(FreeQ(List(a,c,d,e),x),NeQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0)))),
IIntegrate(1234,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Simp(Times(Sqrt(a),EllipticE(Times(C2,ArcSin(Times(Rt(Times(CN1,e,Power(d,CN1)),C2),x))),Times(b,d,Power(Times(C4,a,e),CN1))),Power(Times(C2,Sqrt(d),Rt(Times(CN1,e,Power(d,CN1)),C2)),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(c,d),Times(b,e)),C0),GtQ(a,C0),GtQ(d,C0)))),
IIntegrate(1235,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4)))),Sqrt(Times(Plus(d,Times(e,Sqr(x))),Power(d,CN1))),Power(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Times(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),Power(a,CN1)))),CN1)),Int(Times(Sqrt(Plus(C1,Times(b,Sqr(x),Power(a,CN1)),Times(c,Power(x,C4),Power(a,CN1)))),Power(Plus(C1,Times(e,Sqr(x),Power(d,CN1))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(c,d),Times(b,e)),C0),Not(And(GtQ(a,C0),GtQ(d,C0)))))),
IIntegrate(1236,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Sqrt(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4)))),Power(Times(x,Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Plus(c,Times(b,Power(x,CN2)),Times(a,Power(x,CN4))))),CN1)),Int(Times(x,Sqrt(Plus(c,Times(b,Power(x,CN2)),Times(a,Power(x,CN4)))),Power(Plus(e,Times(d,Power(x,CN2))),CN1D2)),x),x),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Plus(Times(c,Sqr(d)),Times(CN1,b,d,e),Times(a,Sqr(e))),C0)))),
IIntegrate(1237,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1D2),Sqrt(Plus(a_,Times(c_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(e,Times(d,Power(x,CN2)))),Sqrt(Plus(a,Times(c,Power(x,C4)))),Power(Times(x,Sqrt(Plus(d,Times(e,Sqr(x)))),Sqrt(Plus(c,Times(a,Power(x,CN4))))),CN1)),Int(Times(x,Sqrt(Plus(c,Times(a,Power(x,CN4)))),Power(Plus(e,Times(d,Power(x,CN2))),CN1D2)),x),x),And(FreeQ(List(a,c,d,e),x),NeQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0)))),
IIntegrate(1238,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),p)),x),x),And(FreeQ(List(a,b,c,d,e,p,q),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Or(And(IntegerQ(p),IntegerQ(q)),IGtQ(p,C0),IGtQ(q,C0))))),
IIntegrate(1239,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(c,Power(x,C4))),p)),x),x),And(FreeQ(List(a,c,d,e,p,q),x),Or(And(IntegerQ(p),IntegerQ(q)),IGtQ(p,C0))))),
IIntegrate(1240,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_)),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(c,Power(x,C4))),p),Power(Subtract(Times(d,Power(Subtract(Sqr(d),Times(Sqr(e),Power(x,C4))),CN1)),Times(e,Sqr(x),Power(Subtract(Sqr(d),Times(Sqr(e),Power(x,C4))),CN1))),Negate(q)),x),x),And(FreeQ(List(a,c,d,e,p),x),NeQ(Plus(Times(c,Sqr(d)),Times(a,Sqr(e))),C0),Not(IntegerQ(p)),ILtQ(q,C0)))),
IIntegrate(1241,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),p)),x),FreeQ(List(a,b,c,d,e,p,q),x))),
IIntegrate(1242,Int(Times(Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(a,Times(c,Power(x,C4))),p)),x),FreeQ(List(a,c,d,e,p,q),x))),
IIntegrate(1243,Int(Times(Power(x_,m_DEFAULT),Power(Times(e_DEFAULT,Sqr(x_)),q_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(C2,Power(e,Times(C1D2,Subtract(m,C1)))),CN1),Subst(Int(Times(Power(Times(e,x),Plus(q,Times(C1D2,Subtract(m,C1)))),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x,Sqr(x)),x),And(FreeQ(List(a,b,c,e,p,q),x),Not(IntegerQ(q)),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(1244,Int(Times(Power(x_,m_DEFAULT),Power(Times(e_DEFAULT,Sqr(x_)),q_),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(C2,Power(e,Times(C1D2,Subtract(m,C1)))),CN1),Subst(Int(Times(Power(Times(e,x),Plus(q,Times(C1D2,Subtract(m,C1)))),Power(Plus(a,Times(c,Sqr(x))),p)),x),x,Sqr(x)),x),And(FreeQ(List(a,c,e,p,q),x),Not(IntegerQ(q)),IntegerQ(Times(C1D2,Subtract(m,C1)))))),
IIntegrate(1245,Int(Times(Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Times(e_DEFAULT,Sqr(x_)),q_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(q)),Power(Times(e,Sqr(x)),FracPart(q)),Power(Times(Power(f,Times(C2,IntPart(q))),Power(Times(f,x),Times(C2,FracPart(q)))),CN1)),Int(Times(Power(Times(f,x),Plus(m,Times(C2,q))),Power(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),p)),x),x),And(FreeQ(List(a,b,c,e,f,m,p,q),x),Not(IntegerQ(q))))),
IIntegrate(1246,Int(Times(Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Times(e_DEFAULT,Sqr(x_)),q_),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(q)),Power(Times(e,Sqr(x)),FracPart(q)),Power(Times(Power(f,Times(C2,IntPart(q))),Power(Times(f,x),Times(C2,FracPart(q)))),CN1)),Int(Times(Power(Times(f,x),Plus(m,Times(C2,q))),Power(Plus(a,Times(c,Power(x,C4))),p)),x),x),And(FreeQ(List(a,c,e,f,m,p,q),x),Not(IntegerQ(q))))),
IIntegrate(1247,Int(Times(x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Dist(C1D2,Subst(Int(Times(Power(Plus(d,Times(e,x)),q),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x,Sqr(x)),x),FreeQ(List(a,b,c,d,e,p,q),x))),
IIntegrate(1248,Int(Times(x_,Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(c_DEFAULT,Power(x_,C4))),p_DEFAULT)),x_Symbol),
    Condition(Dist(C1D2,Subst(Int(Times(Power(Plus(d,Times(e,x)),q),Power(Plus(a,Times(c,Sqr(x))),p)),x),x,Sqr(x)),x),FreeQ(List(a,c,d,e,p,q),x))),
IIntegrate(1249,Int(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_)),x_Symbol),
    Condition(Dist(C1D2,Subst(Int(Times(Power(x,Times(C1D2,Subtract(m,C1))),Power(Plus(d,Times(e,x)),q),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p)),x),x,Sqr(x)),x),And(FreeQ(List(a,b,c,d,e,p,q),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),IGtQ(Times(C1D2,Plus(m,C1)),C0)))),
IIntegrate(1250,Int(Times(Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),q_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_)),Times(c_DEFAULT,Power(x_,C4))),p_)),x_Symbol),
    Condition(Dist(Times(Power(Plus(a,Times(b,Sqr(x)),Times(c,Power(x,C4))),FracPart(p)),Power(Times(Power(c,IntPart(p)),Power(Plus(Times(C1D2,b),Times(c,Sqr(x))),Times(C2,FracPart(p)))),CN1)),Int(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),q),Power(Plus(Times(C1D2,b),Times(c,Sqr(x))),Times(C2,p))),x),x),And(FreeQ(List(a,b,c,d,e,f,m,p,q),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)))))
  );
}
