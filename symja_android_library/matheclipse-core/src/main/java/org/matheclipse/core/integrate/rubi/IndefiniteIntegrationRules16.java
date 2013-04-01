package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IndefiniteIntegrationRules16 { 
  public static IAST RULES = List( 
SetDelayed(Int(Times($p("u",true),Power(Times(Power($p("v"),$p("m",true)),Power($p("w"),$p("n",true))),$p("p"))),$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("r"),Simplify(Times(Power(Times(Power($s("v"),$s("m")),Power($s("w"),$s("n"))),$s("p")),Power(Times(Power($s("v"),Times($s("m"),$s("p"))),Power($s("w"),Times($s("n"),$s("p")))),CN1)))),$s("lst")),Condition(CompoundExpression(Set($s("lst"),SplitFreeFactors(Times(Power($s("v"),Times($s("m"),$s("p"))),Power($s("w"),Times($s("n"),$s("p")))),$s("x"))),Times(Times($s("r"),Part($s("lst"),C1)),Int(Regularize(Times($s("u"),Part($s("lst"),C2)),$s("x")),$s("x")))),NonzeroQ(Plus($s("r"),Times(CN1,C1))))),And(And(FreeQ($s("p"),$s("x")),Not(PowerQ($s("v")))),Not(PowerQ($s("w")))))),
SetDelayed(Int(Times(Times($p("u",true),Power($p("v"),$p("m"))),Power($p("w"),$p("n"))),$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("q"),Cancel(Times($s("v"),Power($s("w"),CN1))))),Condition(Times(Times(Times(Power($s("v"),$s("m")),Power($s("w"),$s("n"))),Power(Power($s("q"),$s("m")),CN1)),Int(Times($s("u"),Power($s("q"),$s("m"))),$s("x"))),PolynomialQ($s("q"),$s("x")))),And(And(FractionQ(List($s("m"),$s("n"))),Equal(Plus($s("m"),$s("n")),C0)),PolynomialQ(List($s("v"),$s("w")),$s("x"))))),
SetDelayed(Int($p("u"),$p("x",$s("Symbol"))),
    Module(List(Set($s("lst"),SubstForFractionalPowerOfLinear($s("u"),$s("x")))),Condition(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Int(Part($s("lst"),C1),$s("x")),$s("x"),Power(Part($s("lst"),C3),Times(C1,Power(Part($s("lst"),C2),CN1))))),NotFalseQ($s("lst"))))),
SetDelayed(Int(Times($p("u",true),Power(Plus($p("a"),Times($p("b",true),Power($p("v"),C2))),CN1)),$p("x",$s("Symbol"))),
    Condition(Plus(Dist(C1D2,Int(Times($s("u"),Power(Plus($s("a"),Times(Times($s("b"),Rt(Times(Times(CN1,$s("a")),Power($s("b"),CN1)),C2)),$s("v"))),CN1)),$s("x"))),Dist(C1D2,Int(Times($s("u"),Power(Plus($s("a"),Times(CN1,Times(Times($s("b"),Rt(Times(Times(CN1,$s("a")),Power($s("b"),CN1)),C2)),$s("v")))),CN1)),$s("x")))),FreeQ(List($s("a"),$s("b")),$s("x")))),
SetDelayed(Int(Times($p("u",true),Power(Plus($p("a"),Times($p("b",true),Power($p("v"),C2))),$p("m"))),$p("x",$s("Symbol"))),
    Condition(Dist(Power($s("a"),$s("m")),Int(Times(Times($s("u"),Power(Plus(C1,Times(Rt(Times(Times(CN1,$s("b")),Power($s("a"),CN1)),C2),$s("v"))),$s("m"))),Power(Plus(C1,Times(CN1,Times(Rt(Times(Times(CN1,$s("b")),Power($s("a"),CN1)),C2),$s("v")))),$s("m"))),$s("x"))),And(And(FreeQ(List($s("a"),$s("b")),$s("x")),IntegerQ($s("m"))),Or(Less($s("m"),CN1),And(Equal($s("m"),CN1),PositiveQ(Times(Times(CN1,$s("b")),Power($s("a"),CN1)))))))),
SetDelayed(Int(Times(Times($p("u",true),Power($p("f"),Plus($p("a"),$p("v")))),Power($p("g"),Plus($p("b"),$p("w")))),$p("x",$s("Symbol"))),
    Condition(Dist(Times(Power($s("f"),$s("a")),Power($s("g"),$s("b"))),Int(Times(Times($s("u"),Power($s("f"),$s("v"))),Power($s("g"),$s("w"))),$s("x"))),And(And(FreeQ(List($s("a"),$s("b"),$s("f"),$s("g")),$s("x")),Not(MatchQ($s("v"),Condition(Plus($p("c"),$p("t")),FreeQ($s("c"),$s("x")))))),Not(MatchQ($s("w"),Condition(Plus($p("c"),$p("t")),FreeQ($s("c"),$s("x")))))))),
SetDelayed(Int(Times($p("u",true),Power($p("f"),Plus($p("a"),$p("v")))),$p("x",$s("Symbol"))),
    Condition(Dist(Power($s("f"),$s("a")),Int(Times($s("u"),Power($s("f"),$s("v"))),$s("x"))),And(FreeQ(List($s("a"),$s("f")),$s("x")),Not(MatchQ($s("v"),Condition(Plus($p("b"),$p("w")),FreeQ($s("b"),$s("x")))))))),
SetDelayed(Int($p("u"),$p("x",$s("Symbol"))),
    Condition(Dist(C2,Subst(Int(Regularize(Times(SubstForTrig($s("u"),Times(C2,Times($s("x"),Power(Plus(C1,Power($s("x"),C2)),CN1))),Times(Plus(C1,Times(CN1,Power($s("x"),C2))),Power(Plus(C1,Power($s("x"),C2)),CN1)),$s("x"),$s("x")),Power(Plus(C1,Power($s("x"),C2)),CN1)),$s("x")),$s("x")),$s("x"),Tan(Times($s("x"),C1D2)))),FunctionOfTrigQ($s("u"),$s("x"),$s("x")))),
SetDelayed(Int($p("u"),$p("x",$s("Symbol"))),
    Condition(Dist(C2,Subst(Int(Regularize(Times(SubstForHyperbolic($s("u"),Times(C2,Times($s("x"),Power(Plus(C1,Times(CN1,Power($s("x"),C2))),CN1))),Times(Plus(C1,Power($s("x"),C2)),Power(Plus(C1,Times(CN1,Power($s("x"),C2))),CN1)),$s("x"),$s("x")),Power(Plus(C1,Times(CN1,Power($s("x"),C2))),CN1)),$s("x")),$s("x")),$s("x"),Tanh(Times($s("x"),C1D2)))),FunctionOfHyperbolicQ($s("u"),$s("x"),$s("x")))),
SetDelayed(Int($p("u"),$p("x",$s("Symbol"))),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic($s("u"),$s("x")))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),$s("x")),$s("x"),Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int($p("u"),$p("x",$s("Symbol"))),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic($s("u"),$s("x")))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),$s("x")),$s("x"),Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int($p("u"),$p("x",$s("Symbol"))),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic($s("u"),$s("x")))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),$s("x")),$s("x"),Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int(Times($p("u"),Power(Plus(C1,Times(CN1,Power(Plus($p("a",true),Times($p("b",true),$p("x"))),C2))),$p("n",true))),$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear($s("u"),$s("x")))),Condition(Dist(Times(C1,Power($s("b"),CN1)),Subst(Int(Regularize(Times(SubstForInverseFunction($s("u"),$s("tmp"),$s("x")),Power(Cos($s("x")),Plus(Times(C2,$s("n")),C1))),$s("x")),$s("x")),$s("x"),$s("tmp"))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcSin(Plus($s("a"),Times($s("b"),$s("x")))))))),And(FreeQ(List($s("a"),$s("b")),$s("x")),IntegerQ(Times(C2,$s("n")))))),
SetDelayed(Int(Times($p("u"),Power(Plus(C1,Times(CN1,Power(Plus($p("a",true),Times($p("b",true),$p("x"))),C2))),$p("n",true))),$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear($s("u"),$s("x")))),Condition(Times(CN1,Dist(Times(C1,Power($s("b"),CN1)),Subst(Int(Regularize(Times(SubstForInverseFunction($s("u"),$s("tmp"),$s("x")),Power(Sin($s("x")),Plus(Times(C2,$s("n")),C1))),$s("x")),$s("x")),$s("x"),$s("tmp")))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcCos(Plus($s("a"),Times($s("b"),$s("x")))))))),And(FreeQ(List($s("a"),$s("b")),$s("x")),IntegerQ(Times(C2,$s("n")))))),
SetDelayed(Int(Times($p("u"),Power(Plus(C1,Power(Plus($p("a",true),Times($p("b",true),$p("x"))),C2)),$p("n",true))),$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear($s("u"),$s("x")))),Condition(Dist(Times(C1,Power($s("b"),CN1)),Subst(Int(Regularize(Times(SubstForInverseFunction($s("u"),$s("tmp"),$s("x")),Power(Cosh($s("x")),Plus(Times(C2,$s("n")),C1))),$s("x")),$s("x")),$s("x"),$s("tmp"))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcSinh(Plus($s("a"),Times($s("b"),$s("x")))))))),And(FreeQ(List($s("a"),$s("b")),$s("x")),IntegerQ(Times(C2,$s("n")))))),
SetDelayed(Int($p("u"),$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("lst"),SubstForInverseFunctionOfLinear($s("u"),$s("x")))),Condition(Dist(Times(C1,Power(Part($s("lst"),C3),CN1)),Subst(Int(Part($s("lst"),C1),$s("x")),$s("x"),Part($s("lst"),C2))),NotFalseQ($s("lst")))),Not(NotIntegrableQ($s("u"),$s("x"))))),
SetDelayed(Int($p("u"),$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("lst"),SubstForInverseFunctionOfQuotientOfLinears($s("u"),$s("x")))),Condition(Dist(Part($s("lst"),C3),Subst(Int(Part($s("lst"),C1),$s("x")),$s("x"),Part($s("lst"),C2))),NotFalseQ($s("lst")))),Not(NotIntegrableQ($s("u"),$s("x")))))
  );
}
