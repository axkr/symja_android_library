package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.Block;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CPiHalf;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.D;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Exponent;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.First;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.G_;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Length;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.MemberQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.Rest;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sinc;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.F.y_;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.ArcCot;
import static org.matheclipse.core.expression.S.ArcCoth;
import static org.matheclipse.core.expression.S.ArcTan;
import static org.matheclipse.core.expression.S.ArcTanh;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.CSymbol;
import static org.matheclipse.core.expression.S.Cot;
import static org.matheclipse.core.expression.S.Coth;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.GSymbol;
import static org.matheclipse.core.expression.S.Tan;
import static org.matheclipse.core.expression.S.Tanh;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.y;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CalculusQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.DerivativeDivides;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Divides;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EasyDQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.KnownCotangentIntegrandQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.KnownSecantIntegrandQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.KnownSineIntegrandQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.KnownTangentIntegrandQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.KnownTrigIntegrandQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PiecewiseLinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductOfLinearPowersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionExponents;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifyInertTrigFunction;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions46 { 
  public static IAST RULES = List( 
ISetDelayed(672,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(CN1,d,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(n,C2),EqQ(p,C1))),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(673,UnifyInertTrigFunction(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),c_DEFAULT),n_))),p_DEFAULT),Power(Times($($s("§sec"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),m_DEFAULT)),x_),
    Condition(Times(Power(Times(d,$($s("§csc"),Plus(e,CPiHalf,Times(f,x)))),m),Power(Plus(a,Times(b,Power(Times(CN1,c,$($s("§sec"),Plus(e,CPiHalf,Times(f,x)))),n))),p)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),Not(And(EqQ(a,C0),IntegerQ(p)))))),
ISetDelayed(674,KnownSineIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(list($s("§sin"),$s("§cos")),u,x)),
ISetDelayed(675,KnownTangentIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(list($s("§tan")),u,x)),
ISetDelayed(676,KnownCotangentIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(list($s("§cot")),u,x)),
ISetDelayed(677,KnownSecantIntegrandQ(u_,x_Symbol),
    KnownTrigIntegrandQ(list($s("§sec"),$s("§csc")),u,x)),
ISetDelayed(678,KnownTrigIntegrandQ($p("§list"),u_,x_Symbol),
    Or(SameQ(u,C1),MatchQ(u,Condition(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,m),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,BSymbol,m),x)))),MatchQ(u,Condition(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(e,f,ASymbol,CSymbol),x)))),MatchQ(u,Condition(Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))),Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(e,f,ASymbol,BSymbol,CSymbol),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,CSymbol,m),x)))),MatchQ(u,Condition(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))),m_DEFAULT),Plus(A_DEFAULT,Times(B_DEFAULT,$($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x)))),Times(C_DEFAULT,Sqr($($p("func"),Plus(e_DEFAULT,Times(f_DEFAULT,x))))))),And(MemberQ($s("§list"),$s("func")),FreeQ(List(a,b,e,f,ASymbol,BSymbol,CSymbol,m),x)))))),
ISetDelayed(679,PiecewiseLinearQ(u_,v_,x_Symbol),
    And(PiecewiseLinearQ(u,x),PiecewiseLinearQ(v,x))),
ISetDelayed(680,PiecewiseLinearQ(u_,x_Symbol),
    Or(LinearQ(u,x),MatchQ(u,Condition(Log(Times(c_DEFAULT,Power(F_,v_))),And(FreeQ(list(FSymbol,c),x),LinearQ(v,x)))),MatchQ(u,Condition($(F_,$(G_,v_)),And(LinearQ(v,x),MemberQ(List(list(ArcTanh,Tanh),list(ArcTanh,Coth),list(ArcCoth,Coth),list(ArcCoth,Tanh),list(ArcTan,Tan),list(ArcTan,Cot),list(ArcCot,Cot),list(ArcCot,Tan)),list(FSymbol,GSymbol))))))),
ISetDelayed(681,Divides(y_,u_,x_Symbol),
    With(list(Set(v,Simplify(Times(u,Power(y,CN1))))),If(FreeQ(v,x),v,False))),
ISetDelayed(682,DerivativeDivides(y_,u_,x_Symbol),
    If(MatchQ(y,Condition(Times(a_DEFAULT,x),FreeQ(a,x))),False,If(If(PolynomialQ(y,x),And(PolynomialQ(u,x),Equal(Exponent(u,x),Subtract(Exponent(y,x),C1))),EasyDQ(y,x)),Module(list(Set(v,Block(list(Set($s("§$showsteps"),False)),ReplaceAll(D(y,x),Rule(Sinc(z_),Times(Sin(z),Power(z,CN1))))))),If(EqQ(v,C0),False,CompoundExpression(Set(v,Simplify(Times(u,Power(v,CN1)))),If(FreeQ(v,x),v,False)))),False))),
ISetDelayed(683,EasyDQ(Times(u_DEFAULT,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(EasyDQ(u,x),FreeQ(m,x))),
ISetDelayed(684,EasyDQ(u_,x_Symbol),
    If(Or(AtomQ(u),FreeQ(u,x),Equal(Length(u),C0)),True,If(CalculusQ(u),False,If(Equal(Length(u),C1),EasyDQ(Part(u,C1),x),If(Or(BinomialQ(u,x),ProductOfLinearPowersQ(u,x)),True,If(And(RationalFunctionQ(u,x),SameQ(RationalFunctionExponents(u,x),list(C1,C1))),True,If(ProductQ(u),If(FreeQ(First(u),x),EasyDQ(Rest(u),x),If(FreeQ(Rest(u),x),EasyDQ(First(u),x),False)),If(SumQ(u),And(EasyDQ(First(u),x),EasyDQ(Rest(u),x)),If(Equal(Length(u),C2),If(FreeQ(Part(u,C1),x),EasyDQ(Part(u,C2),x),If(FreeQ(Part(u,C2),x),EasyDQ(Part(u,C1),x),False)),False))))))))),
ISetDelayed(685,ProductOfLinearPowersQ(u_,x_Symbol),
          Or(FreeQ(u, x),
              MatchQ(u, Condition(Power(v_, n_DEFAULT), And(LinearQ(v, x), FreeQ(n, x)))),
              And(ProductQ(u), ProductOfLinearPowersQ(First(u), x),
                  ProductOfLinearPowersQ(Rest(u), x))))
  );
}
