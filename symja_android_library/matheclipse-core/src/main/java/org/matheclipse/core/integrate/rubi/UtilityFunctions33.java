package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.EvenQ;
import static org.matheclipse.core.expression.F.Exponent;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.FullSimplify;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.GCD;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Length;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.ListQ;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.List;
import static org.matheclipse.core.expression.S.Null;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CalculusQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EulerIntegrandQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EvenQuotientQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FalseQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfDensePolynomialsQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfLog;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FunctionOfSquareRootOfQuadratic;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LogQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerVariableDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerVariableExpn;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerVariableSubst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ProductQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SquareRootOfQuadraticSubst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions33 { 
  public static IAST RULES = List( 
ISetDelayed(436,EvenQuotientQ(u_,v_),
    EvenQ(Simplify(Times(u,Power(v,CN1))))),
ISetDelayed(437,FunctionOfDensePolynomialsQ(u_,x_Symbol),
    If(FreeQ(u,x),True,If(PolynomialQ(u,x),Greater(Length(Exponent(u,x,List)),C1),Catch(CompoundExpression(Scan(Function(If(FunctionOfDensePolynomialsQ(Slot1,x),Null,Throw(False))),u),True))))),
ISetDelayed(438,FunctionOfLog(u_,x_Symbol),
    With(list(Set($s("lst"),FunctionOfLog(u,False,False,x))),If(Or(AtomQ($s("lst")),FalseQ(Part($s("lst"),C2))),False,$s("lst")))),
ISetDelayed(439,FunctionOfLog(u_,v_,n_,x_),
    If(AtomQ(u),If(SameQ(u,x),False,list(u,v,n)),If(CalculusQ(u),False,Module(list($s("lst")),If(And(LogQ(u),ListQ(Set($s("lst"),BinomialParts(Part(u,C1),x))),EqQ(Part($s("lst"),C1),C0)),If(Or(FalseQ(v),SameQ(Part(u,C1),v)),list(x,Part(u,C1),Part($s("lst"),C3)),False),CompoundExpression(Set($s("lst"),list(C0,v,n)),Catch(list(Map(Function(CompoundExpression(Set($s("lst"),FunctionOfLog(Slot1,Part($s("lst"),C2),Part($s("lst"),C3),x)),If(AtomQ($s("lst")),Throw(False),Part($s("lst"),C1)))),u),Part($s("lst"),C2),Part($s("lst"),C3))))))))),
ISetDelayed(440,PowerVariableExpn(u_,m_,x_Symbol),
    If(IntegerQ(m),With(list(Set($s("lst"),PowerVariableDegree(u,m,C1,x))),If(AtomQ($s("lst")),False,list(Times(Power(x,Times(m,Power(Part($s("lst"),C1),CN1))),PowerVariableSubst(u,Part($s("lst"),C1),x)),Part($s("lst"),C1),Part($s("lst"),C2)))),False)),
ISetDelayed(441,PowerVariableDegree(u_,m_,c_,x_Symbol),
    If(FreeQ(u,x),list(m,c),If(Or(AtomQ(u),CalculusQ(u)),False,If(And(PowerQ(u),FreeQ(Times(Part(u,C1),Power(x,CN1)),x)),If(Or(EqQ(m,C0),And(SameQ(m,Part(u,C2)),SameQ(c,Times(Part(u,C1),Power(x,CN1))))),list(Part(u,C2),Times(Part(u,C1),Power(x,CN1))),If(And(IntegerQ(Part(u,C2)),IntegerQ(m),Greater(GCD(m,Part(u,C2)),C1),SameQ(c,Times(Part(u,C1),Power(x,CN1)))),list(GCD(m,Part(u,C2)),c),False)),Catch(Module(list(Set($s("lst"),list(m,c))),CompoundExpression(Scan(Function(CompoundExpression(Set($s("lst"),PowerVariableDegree(Slot1,Part($s("lst"),C1),Part($s("lst"),C2),x)),If(AtomQ($s("lst")),Throw(False)))),u),$s("lst")))))))),
ISetDelayed(442,PowerVariableSubst(u_,m_,x_Symbol),
    If(Or(FreeQ(u,x),AtomQ(u),CalculusQ(u)),u,If(And(PowerQ(u),FreeQ(Times(Part(u,C1),Power(x,CN1)),x)),Power(x,Times(Part(u,C2),Power(m,CN1))),Map(Function(PowerVariableSubst(Slot1,m,x)),u)))),
ISetDelayed(443,EulerIntegrandQ(Power(Plus(Times(b_DEFAULT,Power(u_,n_)),Times(a_DEFAULT,x_)),p_),x_Symbol),
    Condition(True,And(FreeQ(list(a,b),x),IntegerQ(Plus(n,C1D2)),QuadraticQ(u,x),Or(Not(RationalQ(p)),And(ILtQ(p,C0),Not(BinomialQ(u,x))))))),
ISetDelayed(444,EulerIntegrandQ(Times(Power(v_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(u_,n_)),Times(a_DEFAULT,x_)),p_)),x_Symbol),
    Condition(True,And(FreeQ(list(a,b),x),EqQ(u,v),IntegersQ(Times(C2,m),Plus(n,C1D2)),QuadraticQ(u,x),Or(Not(RationalQ(p)),And(ILtQ(p,C0),Not(BinomialQ(u,x))))))),
ISetDelayed(445,EulerIntegrandQ(Times(Power(v_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(u_,n_)),Times(a_DEFAULT,x_)),p_)),x_Symbol),
    Condition(True,And(FreeQ(list(a,b),x),EqQ(u,v),IntegersQ(Times(C2,m),Plus(n,C1D2)),QuadraticQ(u,x),Or(Not(RationalQ(p)),And(ILtQ(p,C0),Not(BinomialQ(u,x))))))),
ISetDelayed(446,EulerIntegrandQ(Times(Power(u_,n_),Power(v_,p_)),x_Symbol),
    Condition(True,And(ILtQ(p,C0),IntegerQ(Plus(n,C1D2)),QuadraticQ(u,x),QuadraticQ(v,x),Not(BinomialQ(v,x))))),
ISetDelayed(447,EulerIntegrandQ(u_,x_Symbol),
    False),
ISetDelayed(448,FunctionOfSquareRootOfQuadratic(u_,x_Symbol),
    If(MatchQ(u,Condition(Times(Power(x,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x,n_DEFAULT))),p_)),FreeQ(List(a,b,m,n,p),x))),False,Module(list(Set($s("tmp"),FunctionOfSquareRootOfQuadratic(u,False,x))),If(Or(AtomQ($s("tmp")),FalseQ(Part($s("tmp"),C1))),False,CompoundExpression(Set($s("tmp"),Part($s("tmp"),C1)),Module(List(Set(a,Coefficient($s("tmp"),x,C0)),Set(b,Coefficient($s("tmp"),x,C1)),Set(c,Coefficient($s("tmp"),x,C2)),$s("§sqrt"),q,r),If(Or(And(EqQ(a,C0),EqQ(b,C0)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0)),False,If(PosQ(c),CompoundExpression(Set($s("§sqrt"),Rt(c,C2)),Set(q,Plus(Times(a,$s("§sqrt")),Times(b,x),Times($s("§sqrt"),Sqr(x)))),Set(r,Plus(b,Times(C2,$s("§sqrt"),x))),list(Simplify(Times(SquareRootOfQuadraticSubst(u,Times(q,Power(r,CN1)),Times(Plus(Negate(a),Sqr(x)),Power(r,CN1)),x),q,Power(r,CN2))),Simplify(Plus(Times($s("§sqrt"),x),Sqrt($s("tmp")))),C2)),If(PosQ(a),CompoundExpression(Set($s("§sqrt"),Rt(a,C2)),Set(q,Plus(Times(c,$s("§sqrt")),Times(CN1,b,x),Times($s("§sqrt"),Sqr(x)))),Set(r,Subtract(c,Sqr(x))),list(Simplify(Times(SquareRootOfQuadraticSubst(u,Times(q,Power(r,CN1)),Times(Plus(Negate(b),Times(C2,$s("§sqrt"),x)),Power(r,CN1)),x),q,Power(r,CN2))),Simplify(Times(Plus(Negate($s("§sqrt")),Sqrt($s("tmp"))),Power(x,CN1))),C1)),CompoundExpression(Set($s("§sqrt"),Rt(Subtract(Sqr(b),Times(C4,a,c)),C2)),Set(r,Subtract(c,Sqr(x))),list(Simplify(Times(CN1,$s("§sqrt"),SquareRootOfQuadraticSubst(u,Times(CN1,$s("§sqrt"),x,Power(r,CN1)),Times(CN1,Plus(Times(b,c),Times(c,$s("§sqrt")),Times(Plus(Negate(b),$s("§sqrt")),Sqr(x))),Power(Times(C2,c,r),CN1)),x),x,Power(r,CN2))),FullSimplify(Times(C2,c,Sqrt($s("tmp")),Power(Plus(b,Negate($s("§sqrt")),Times(C2,c,x)),CN1))),C3))))))))))),
ISetDelayed(449,FunctionOfSquareRootOfQuadratic(u_,v_,x_Symbol),
    If(Or(AtomQ(u),FreeQ(u,x)),list(v),If(And(PowerQ(u),FreeQ(Part(u,C2),x)),If(And(FractionQ(Part(u,C2)),Equal(Denominator(Part(u,C2)),C2),PolynomialQ(Part(u,C1),x),Equal(Exponent(Part(u,C1),x),C2)),If(Or(FalseQ(v),SameQ(Part(u,C1),v)),list(Part(u,C1)),False),FunctionOfSquareRootOfQuadratic(Part(u,C1),v,x)),If(Or(ProductQ(u),SumQ(u)),Catch(Module(list(Set($s("lst"),list(v))),CompoundExpression(Scan(Function(CompoundExpression(Set($s("lst"),FunctionOfSquareRootOfQuadratic(Slot1,Part($s("lst"),C1),x)),If(AtomQ($s("lst")),Throw(False)))),u),$s("lst")))),False)))),
ISetDelayed(450,SquareRootOfQuadraticSubst(u_,$p("vv"),$p("xx"),x_Symbol),
          If(Or(AtomQ(u), FreeQ(u, x)), If(SameQ(u, x), $s("xx"), u),
              If(And(PowerQ(u), FreeQ(Part(u, C2), x)),
                  If(And(FractionQ(Part(u, C2)), Equal(Denominator(Part(u, C2)), C2),
                      PolynomialQ(Part(u, C1), x), Equal(Exponent(Part(u, C1), x), C2)),
                      Power($s("vv"), Numerator(Part(u, C2))),
                      Power(SquareRootOfQuadraticSubst(Part(u, C1), $s("vv"), $s("xx"), x),
                          Part(u, C2))),
                  Map(Function(SquareRootOfQuadraticSubst(Slot1, $s("vv"), $s("xx"), x)), u))))
  );
}
