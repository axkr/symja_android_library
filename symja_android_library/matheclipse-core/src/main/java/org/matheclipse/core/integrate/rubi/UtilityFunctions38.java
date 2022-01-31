package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.$str;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.G_;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Print;
import static org.matheclipse.core.expression.F.ReplacePart;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.RuleDelayed;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.S.Block;
import static org.matheclipse.core.expression.S.Condition;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.GSymbol;
import static org.matheclipse.core.expression.S.Hold;
import static org.matheclipse.core.expression.S.Module;
import static org.matheclipse.core.expression.S.Pattern;
import static org.matheclipse.core.expression.S.With;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FixIntRule;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FixRhsIntRule;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.G;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.H;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.J;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions38 {
  public static IAST RULES =
      List(
          ISetDelayed(
              689, FixIntRule($p(
                  "§rule")),
              If(AtomQ(Part($s("§rule"), C1, C1, CN1)),
                  FixIntRule($s("§rule"), Part($s("§rule"), C1, C1,
                      CN1)),
                  If(And(SameQ(Head(Part($s("§rule"), C1, C1, CN1)), Pattern),
                      AtomQ(Part($s("§rule"), C1, C1, CN1, C1))),
                      FixIntRule($s("§rule"), Part($s("§rule"), C1, C1, CN1,
                          C1)),
                      Print($str("Invalid integration rule: "), Part($s("§rule"), C1, C1, CN1))))),
          ISetDelayed(690,
              FixIntRule(
                  RuleDelayed(
                      $p("lhs"),
                      $(F_, $(G_, $p("§list"), $(F_, Plus(u_, v_), $p("test2"))), $p("test1"))),
                  x_),
              Condition(ReplacePart(
                  RuleDelayed($s("lhs"),
                      F(G($s("§list"), F(Plus(u, v), $s("test2"))), $s("test1"))),
                  List(Rule(List(C2, C1, C2, C1, C1), FixRhsIntRule(u, x)),
                      Rule(List(C2, C1, C2, C1, C2), FixRhsIntRule(v, x)))),
                  And(SameQ(FSymbol, Condition),
                      Or(SameQ(GSymbol, With), SameQ(GSymbol, Module), SameQ(GSymbol, Block))))),
          ISetDelayed(691,
              FixIntRule(
                  RuleDelayed($p("lhs"), $(G_, $p("§list"), $(F_, Plus(u_, v_), $p("test2")))), x_),
              Condition(
                  ReplacePart(RuleDelayed($s("lhs"), G($s("§list"), F(Plus(u, v), $s("test2")))),
                      List(Rule(List(C2, C2, C1, C1), FixRhsIntRule(u, x)),
                          Rule(List(C2, C2, C1, C2), FixRhsIntRule(v, x)))),
                  And(SameQ(FSymbol, Condition),
                      Or(SameQ(GSymbol, With), SameQ(GSymbol, Module), SameQ(GSymbol, Block))))),
          ISetDelayed(692,
              FixIntRule(
                  RuleDelayed($p("lhs"), $(F_, $(G_, $p("§list"), Plus(u_, v_)), $p("test"))), x_),
              Condition(
                  ReplacePart(RuleDelayed($s("lhs"), F(G($s("§list"), Plus(u, v)), $s("test"))),
                      List(Rule(List(C2, C1, C2, C1), FixRhsIntRule(u, x)),
                          Rule(List(C2, C1, C2, C2), FixRhsIntRule(v, x)))),
                  And(SameQ(FSymbol, Condition), Or(SameQ(GSymbol, With), SameQ(GSymbol, Module),
                      SameQ(GSymbol, Block))))),
          ISetDelayed(693, FixIntRule(RuleDelayed($p("lhs"), $(G_, $p("§list"), Plus(u_, v_))), x_),
              Condition(
                  ReplacePart(RuleDelayed($s("lhs"), G($s("§list"), Plus(u, v))),
                      List(Rule(List(C2, C2, C1), FixRhsIntRule(u, x)),
                          Rule(List(C2, C2, C2), FixRhsIntRule(v, x)))),
                  Or(SameQ(GSymbol, With), SameQ(GSymbol, Module), SameQ(GSymbol, Block)))),
          ISetDelayed(
              694, FixIntRule(RuleDelayed($p("lhs"),
                  $(F_, Plus(u_, v_), $p("test"))), x_),
              Condition(
                  ReplacePart(RuleDelayed($s("lhs"), F(Plus(u, v), $s("test"))),
                      List(Rule(List(C2, C1, C1), FixRhsIntRule(u, x)),
                          Rule(List(C2, C1, C2), FixRhsIntRule(v, x)))),
                  SameQ(FSymbol, Condition))),
          ISetDelayed(
              695, FixIntRule(RuleDelayed($p("lhs"),
                  Plus(u_, v_)), x_),
              ReplacePart(
                  RuleDelayed($s("lhs"), Plus(u,
                      v)),
                  List(Rule(List(C2, C1), FixRhsIntRule(u, x)),
                      Rule(List(C2, C2), FixRhsIntRule(v, x))))),
          ISetDelayed(696,
              FixIntRule(
                  RuleDelayed($p("lhs"),
                      $(F_, $(G_, $p("list1"), $(F_, $($p("H"), $p("list2"), u_), $p("test2"))),
                          $p("test1"))),
                  x_),
              Condition(
                  ReplacePart(
                      RuleDelayed(
                          $s("lhs"), F(G($s("list1"), F(H($s("list2"), u), $s("test2"))),
                              $s("test1"))),
                      Rule(List(C2, C1, C2, C1, C2), FixRhsIntRule(u, x))),
                  And(SameQ(FSymbol, Condition), Or(SameQ(GSymbol, With), SameQ(GSymbol,
                      Module), SameQ(GSymbol, Block)), Or(SameQ($s("H"), With),
                          SameQ($s("H"), Module), SameQ($s("H"), Block))))),
          ISetDelayed(697,
              FixIntRule(
                  RuleDelayed(
                      $p("lhs"), $(F_, $(G_, $p("list1"), $($p("H"), $p("list2"), u_)),
                          $p("test1"))),
                  x_),
              Condition(
                  ReplacePart(
                      RuleDelayed($s("lhs"), F(G($s("list1"), H($s("list2"), u)),
                          $s("test1"))),
                      Rule(List(C2, C1, C2, C2), FixRhsIntRule(u, x))),
                  And(SameQ(FSymbol, Condition),
                      Or(SameQ(GSymbol, With), SameQ(GSymbol, Module), SameQ(GSymbol,
                          Block)),
                      Or(SameQ($s("H"), With), SameQ($s("H"), Module), SameQ($s("H"), Block))))),
          ISetDelayed(698,
              FixIntRule(
                  RuleDelayed(
                      $p("lhs"), $(
                          F_, $(
                              G_, $p("§list"), $(F_,
                                  $($p("H"), $p("str1"), $p("str2"), $p("str3"),
                                      $($p("J"), u_)),
                                  $p("test2"))),
                          $p("test1"))),
                  x_),
              Condition(
                  ReplacePart(RuleDelayed($s("lhs"),
                      F(G($s("§list"), F(H($s("str1"), $s("str2"), $s("str3"), J(u)), $s("test2"))),
                          $s("test1"))),
                      Rule(List(C2, C1, C2, C1, C4, C1), FixRhsIntRule(u, x))),
                  And(SameQ(FSymbol, Condition),
                      Or(SameQ(GSymbol, With), SameQ(GSymbol, Module), SameQ(GSymbol,
                          Block)),
                      SameQ($s("H"), $s("§showstep")), SameQ($s("J"), Hold)))),
          ISetDelayed(699,
              FixIntRule(
                  RuleDelayed($p("lhs"), $(F_, $(G_, $p("§list"), $(F_, u_, $p("test2"))),
                      $p("test1"))),
                  x_),
              Condition(
                  ReplacePart(
                      RuleDelayed($s("lhs"), F(G($s("§list"), F(u, $s("test2"))), $s("test1"))),
                      Rule(List(C2, C1, C2, C1), FixRhsIntRule(u, x))),
                  And(SameQ(FSymbol, Condition),
                      Or(SameQ(GSymbol, With), SameQ(GSymbol, Module), SameQ(GSymbol, Block))))),
          ISetDelayed(700,
              FixIntRule(RuleDelayed($p("lhs"), $(G_, $p("§list"), $(F_, u_, $p("test2")))), x_),
              Condition(
                  ReplacePart(RuleDelayed($s("lhs"), G($s("§list"), F(u, $s("test2")))),
                      Rule(List(C2, C2, C1), FixRhsIntRule(u, x))),
                  And(SameQ(FSymbol, Condition),
                      Or(SameQ(GSymbol, With), SameQ(GSymbol, Module), SameQ(GSymbol, Block))))),
          ISetDelayed(701,
              FixIntRule(RuleDelayed($p("lhs"), $(F_, $(G_, $p("§list"), u_), $p("test"))), x_),
              Condition(
                  ReplacePart(RuleDelayed($s("lhs"), F(G($s("§list"), u), $s("test"))),
                      Rule(List(C2, C1, C2), FixRhsIntRule(u, x))),
                  And(SameQ(FSymbol, Condition),
                      Or(SameQ(GSymbol, With), SameQ(GSymbol, Module), SameQ(GSymbol, Block))))),
          ISetDelayed(702, FixIntRule(RuleDelayed($p("lhs"), $(G_, $p("§list"), u_)), x_),
              Condition(
                  ReplacePart(RuleDelayed($s("lhs"), G($s("§list"), u)),
                      Rule(List(C2, C2), FixRhsIntRule(u, x))),
                  Or(SameQ(GSymbol, With), SameQ(GSymbol, Module), SameQ(GSymbol, Block)))),
          ISetDelayed(703, FixIntRule(RuleDelayed($p("lhs"), $(F_, u_, $p("test"))), x_),
              Condition(ReplacePart(RuleDelayed($s("lhs"), F(u, $s("test"))),
                  Rule(List(C2, C1), FixRhsIntRule(u, x))), SameQ(FSymbol, Condition))),
          ISetDelayed(704, FixIntRule(RuleDelayed($p("lhs"), u_), x_),
              ReplacePart(RuleDelayed($s("lhs"), u), Rule(List(C2), FixRhsIntRule(u, x)))),
          ISetDelayed(705, FixRhsIntRule(Plus(u_, v_), x_),
              Plus(FixRhsIntRule(u, x), FixRhsIntRule(v, x))),
          ISetDelayed(706, FixRhsIntRule(Subtract(u_, v_), x_),
              Subtract(FixRhsIntRule(u, x), FixRhsIntRule(v, x))),
          ISetDelayed(707, FixRhsIntRule(Negate(u_), x_), Negate(FixRhsIntRule(u, x))));
}
