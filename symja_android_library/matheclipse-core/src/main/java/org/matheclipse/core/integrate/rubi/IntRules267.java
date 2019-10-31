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
public class IntRules267 { 
  public static IAST RULES = List( 
IIntegrate(6675,Int(Times(Power($(f_,x_),CN1),Power($(g_,x_),CN1),Plus(Times($(g_,x_),$($(Derivative(C1),f_),x_)),Times(CN1,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Log(Times($(f,x),Power($(g,x),CN1))),x),FreeQ(List(f,g),x))),
IIntegrate(6676,Int(Times(u_,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Times(c,Power(Plus(a,Times(b,x)),n)),FracPart(p)),Power(Power(Plus(a,Times(b,x)),Times(n,FracPart(p))),CN1)),Int(Times(u,Power(Plus(a,Times(b,x)),Times(n,p))),x),x),And(FreeQ(List(a,b,c,n,p),x),Not(IntegerQ(p)),Not(MatchQ(u,Condition(Times(Power(x,$p("n1",true)),v_DEFAULT),EqQ(n,Plus($s("n1"),C1)))))))),
IIntegrate(6677,Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(d_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),q_)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Power(Times(d,Plus(a,Times(b,x))),p)),q),Power(Power(Plus(a,Times(b,x)),Times(p,q)),CN1)),Int(Times(u,Power(Plus(a,Times(b,x)),Times(p,q))),x),x),And(FreeQ(List(a,b,c,d,p,q),x),Not(IntegerQ(p)),Not(IntegerQ(q))))),
IIntegrate(6678,Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),p_)),q_)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Power(Times(d,Power(Plus(a,Times(b,x)),n)),p)),q),Power(Power(Plus(a,Times(b,x)),Times(n,p,q)),CN1)),Int(Times(u,Power(Plus(a,Times(b,x)),Times(n,p,q))),x),x),And(FreeQ(List(a,b,c,d,n,p,q),x),Not(IntegerQ(p)),Not(IntegerQ(q))))),
IIntegrate(6679,Int(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(C2,e,g,Power(Times(C,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Int(Times(Power(Plus(a,Times(b,F(Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,C,FSymbol),x),EqQ(Subtract(Times(C,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(C,Plus(Times(e,f),Times(d,g)))),C0),IGtQ(n,C0)))),
IIntegrate(6680,Int(Times(Power(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(C2,e,g,Power(Times(C,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Int(Times(Power(Plus(a,Times(b,F(Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,C,FSymbol),x),EqQ(Subtract(Times(C,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),IGtQ(n,C0)))),
IIntegrate(6681,Int(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,F(Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(BSymbol,x),Times(C,Sqr(x))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,C,FSymbol,n),x),EqQ(Subtract(Times(C,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(C,Plus(Times(e,f),Times(d,g)))),C0),Not(IGtQ(n,C0))))),
IIntegrate(6682,Int(Times(Power(Plus(A_,Times(C_DEFAULT,Sqr(x_))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,F(Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(C,Sqr(x))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,C,FSymbol,n),x),EqQ(Subtract(Times(C,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),Not(IGtQ(n,C0))))),
IIntegrate(6683,Int(Times(u_,Power(y_,CN1)),x_Symbol),
    With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Times(q,Log(RemoveContent(y,x))),x),Not(FalseQ(q))))),
IIntegrate(6684,Int(Times(u_,Power(w_,CN1),Power(y_,CN1)),x_Symbol),
    With(List(Set(q,DerivativeDivides(Times(y,w),u,x))),Condition(Simp(Times(q,Log(RemoveContent(Times(y,w),x))),x),Not(FalseQ(q))))),
IIntegrate(6685,Int(Times(u_,Power(y_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Times(q,Power(y,Plus(m,C1)),Power(Plus(m,C1),CN1)),x),Not(FalseQ(q)))),And(FreeQ(m,x),NeQ(m,CN1)))),
IIntegrate(6686,Int(Times(u_,Power(y_,m_DEFAULT),Power(z_,n_DEFAULT)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(Times(y,z),Times(u,Power(z,Subtract(n,m))),x))),Condition(Simp(Times(q,Power(y,Plus(m,C1)),Power(z,Plus(m,C1)),Power(Plus(m,C1),CN1)),x),Not(FalseQ(q)))),And(FreeQ(List(m,n),x),NeQ(m,CN1)))),
IIntegrate(6687,Int(u_,x_Symbol),
    With(List(Set(v,SimplifyIntegrand(u,x))),Condition(Int(v,x),SimplerIntegrandQ(v,u,x)))),
IIntegrate(6688,Int(Times(u_DEFAULT,Power(Plus(Times(e_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),Times(f_DEFAULT,Sqrt(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT)))))),m_)),x_Symbol),
    Condition(Dist(Power(Subtract(Times(a,Sqr(e)),Times(c,Sqr(f))),m),Int(ExpandIntegrand(Times(u,Power(Power(Subtract(Times(e,Sqrt(Plus(a,Times(b,Power(x,n))))),Times(f,Sqrt(Plus(c,Times(d,Power(x,n)))))),m),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),ILtQ(m,C0),EqQ(Subtract(Times(b,Sqr(e)),Times(d,Sqr(f))),C0)))),
IIntegrate(6689,Int(Times(u_DEFAULT,Power(Plus(Times(e_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),Times(f_DEFAULT,Sqrt(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT)))))),m_)),x_Symbol),
    Condition(Dist(Power(Subtract(Times(b,Sqr(e)),Times(d,Sqr(f))),m),Int(ExpandIntegrand(Times(u,Power(x,Times(m,n)),Power(Power(Subtract(Times(e,Sqrt(Plus(a,Times(b,Power(x,n))))),Times(f,Sqrt(Plus(c,Times(d,Power(x,n)))))),m),CN1)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),ILtQ(m,C0),EqQ(Subtract(Times(a,Sqr(e)),Times(c,Sqr(f))),C0)))),
IIntegrate(6690,Int(Times(Power(u_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(u_,n_)),v_),p_DEFAULT),w_),x_Symbol),
    Condition(Int(Times(Power(u,Plus(m,Times(n,p))),Power(Plus(a,Times(v,Power(Power(u,n),CN1))),p),w),x),And(FreeQ(List(a,m,n),x),IntegerQ(p),Not(GtQ(n,C0)),Not(FreeQ(v,x))))),
IIntegrate(6691,Int(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(v,y)))),
IIntegrate(6692,Int(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,w_)),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Plus(e,Times(f,x)),p)),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),EqQ(v,y),EqQ(w,y)))),
IIntegrate(6693,Int(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,w_)),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,z_)),q_DEFAULT)),x_Symbol),
    Condition(With(List(Set(r,DerivativeDivides(y,u,x))),Condition(Dist(r,Subst(Int(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Plus(e,Times(f,x)),p),Power(Plus(g,Times(h,x)),q)),x),x,y),x),Not(FalseQ(r)))),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n,p,q),x),EqQ(v,y),EqQ(w,y),EqQ(z,y)))),
IIntegrate(6694,Int(Times(u_DEFAULT,Plus(a_,Times(b_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Plus(Dist(a,Int(u,x),x),Dist(Times(b,q),Subst(Int(Power(x,n),x),x,y),x)),Not(FalseQ(q)))),FreeQ(List(a,b,n),x))),
IIntegrate(6695,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),p_)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Power(Plus(a,Times(b,Power(x,n))),p),x),x,y),x),Not(FalseQ(q)))),FreeQ(List(a,b,n,p),x))),
IIntegrate(6696,Int(Times(u_DEFAULT,Power(v_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(q,r),Condition(Dist(Times(q,r),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x,y),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(v,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),FreeQ(List(a,b,m,n,p),x))),
IIntegrate(6697,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(v_,$p("n2",true))),Times(b_DEFAULT,Power(y_,n_))),p_)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y)))),
IIntegrate(6698,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_)),Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Times(Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y),EqQ(w,y)))),
IIntegrate(6699,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Times(Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,c,ASymbol,BSymbol,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(w,y))))
  );
}
