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
class IntRules256 { 
  public static IAST RULES = List( 
IIntegrate(5121,Integrate(Times(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Sec(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Star(Negate(Csc(Times(Subtract(Times(b,c),Times(a,d)),Power(d,CN1)))),Integrate(Tan(Plus(a,Times(b,x))),x)),x),Simp(Star(Csc(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1))),Integrate(Tan(Plus(c,Times(d,x))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(b),Sqr(d)),C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(5122,Integrate(Times(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Csc(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Star(Csc(Times(Subtract(Times(b,c),Times(a,d)),Power(b,CN1))),Integrate(Cot(Plus(a,Times(b,x))),x)),x),Simp(Star(Csc(Times(Subtract(Times(b,c),Times(a,d)),Power(d,CN1))),Integrate(Cot(Plus(c,Times(d,x))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(b),Sqr(d)),C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(5123,Integrate(Times(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Tan(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,x,Power(d,CN1)),x),Simp(Star(Times(b,Power(d,CN1),Cos(Times(Subtract(Times(b,c),Times(a,d)),Power(d,CN1)))),Integrate(Times(Sec(Plus(a,Times(b,x))),Sec(Plus(c,Times(d,x)))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(b),Sqr(d)),C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(5124,Integrate(Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Cot(Plus(c_,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,x,Power(d,CN1)),x),Simp(Star(Cos(Times(Subtract(Times(b,c),Times(a,d)),Power(d,CN1))),Integrate(Times(Csc(Plus(a,Times(b,x))),Csc(Plus(c,Times(d,x)))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(b),Sqr(d)),C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(5125,Integrate(Times(u_DEFAULT,Power(Plus(Times(Cos(v_),a_DEFAULT),Times(b_DEFAULT,Sin(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(u,Power(Times(a,Power(Exp(Times(a,Power(b,CN1),v)),CN1)),n)),x),And(FreeQ(list(a,b,n),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(5126,Integrate(Sin(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Exp(Times(CN1,CI,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Exp(Times(CI,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(5127,Integrate(Cos(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Exp(Times(CN1,CI,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x)),x),Simp(Star(C1D2,Integrate(Exp(Times(CI,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x)),x)),FreeQ(List(a,b,c,d,n),x))),
IIntegrate(5128,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sin(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Power(Times(e,x),m),Power(Exp(Times(CI,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Power(Times(e,x),m),Exp(Times(CI,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x)),x)),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(5129,Integrate(Times(Cos(Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT))),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Power(Exp(Times(CI,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),CN1)),x)),x),Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Times(CI,d,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n))))))))),x)),x)),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(5130,Integrate(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n)),x),Simp(Star(Times(b,c,n),Integrate(Times(x,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),GtQ(n,C0)))),
IIntegrate(5131,Integrate(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n)),x),Simp(Star(Times(b,c,n),Integrate(Times(x,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),GtQ(n,C0)))),
IIntegrate(5132,Integrate(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Plus(Simp(Times(Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Star(Times(c,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),LtQ(n,CN1)))),
IIntegrate(5133,Integrate(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Sqrt(Subtract(C1,Times(Sqr(c),Sqr(x)))),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),CN1)),x),Simp(Star(Times(c,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(x,Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Plus(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),LtQ(n,CN1)))),
IIntegrate(5134,Integrate(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Star(Power(Times(b,c),CN1),Subst(Integrate(Times(Power(x,n),Cos(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcSin(Times(c,x)))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5135,Integrate(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Star(Negate(Power(Times(b,c),CN1)),Subst(Integrate(Times(Power(x,n),Sin(Plus(Times(CN1,a,Power(b,CN1)),Times(x,Power(b,CN1))))),x),x,Plus(a,Times(b,ArcCos(Times(c,x)))))),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5136,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Cot(x)),x),x,ArcSin(Times(c,x))),And(FreeQ(list(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(5137,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Tan(x)),x),x,ArcCos(Times(c,x)))),And(FreeQ(list(a,b,c),x),IGtQ(n,C0)))),
IIntegrate(5138,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(5139,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCos(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),n),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,C1)),Power(Plus(a,Times(b,ArcCos(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(5140,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSin(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),n),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,c,n,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,ArcSin(Times(c,x)))),Subtract(n,C1)),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2)),x)),x)),And(FreeQ(list(a,b,c),x),IGtQ(m,C0),GtQ(n,C0))))
  );
}
