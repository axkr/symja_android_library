package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.ast;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.x;
import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.RulesData;

public class PatternsTest extends ExprEvaluatorTestCase {

  @Override
  public void check(String evalString, String expectedResult) {
    check(evaluator, evalString, expectedResult, -1);
  }

  @Test
  public void testPriority001() {

    IASTAppendable ast1 = ast(f);
    ast1.append(a_);
    IASTAppendable ast2 = ast(f);
    ast2.append(Times(a_, x_));
    PatternMatcher pm1 = new PatternMatcher(ast1);
    PatternMatcher pm2 = new PatternMatcher(ast2);
    int cpr = pm1.equivalentTo(pm2);
    assertEquals(cpr, 1);
  }

  @Test
  public void testPriority002() {

    IASTAppendable ast1 = ast(f);
    ast1.append(Times(a, x));
    IASTAppendable ast2 = ast(f);
    ast2.append(Times(a_, x_));
    PatternMatcher pm1 = new PatternMatcher(ast1);
    PatternMatcher pm2 = new PatternMatcher(ast2);
    int cpr = pm1.equivalentTo(pm2);
    assertEquals(cpr, -1);
  }

  @Test
  public void testComplicatedPatternRule() {
    IExpr expr = F.Integrate(F.unaryAST1(F.unaryAST1(F.Derivative(F.n_), F.f_), F.x_), F.x_Symbol);
    assertEquals("Integrate(Derivative(n_)[f_][x_],x_Symbol)", expr.toString());
    boolean isComplicated = RulesData.isComplicatedPatternRule(expr);
    assertTrue(isComplicated);
  }

  @Test
  public void testEvaluatedPatternRule() {
    check("f(a_+b_,a_,b_) := {a,b}", //
        "");
    check("Definition(f)", //
        "f(a_ + b_,a_,b_):={a,b}");
    check("f(5,3,2)", //
        "f(5,3,2)");

    check("g(a_,b_,a_+b_) := {a,b}", //
        "");
    check("g(3,2,5)", //
        "g(3,2,5)");
  }

  @Test
  public void testDocOptions() {
    // TODO doc/Options
    check("Options(f) = {n -> 2}", //
        "{n->2}");
    check("Options(f)", //
        "{n->2}");
    check("f(x_, OptionsPattern(f)) := x ^ OptionValue(n)", //
        "");
    check("f(x)", //
        "x^2");
    check("f(x, n -> 3)", //
        "x^3");
  }

  @Test
  public void testMatchQ() {
    check("MatchQ(a* I*3, (f1_.)* Complex(0, 3))", //
        "True");
    check("MatchQ(a* I*3, (f1_.)* Complex(0, j_))", //
        "True");
    check("MatchQ(a*(1+I*3), (f1_.)* Complex(0, j_))", //
        "False");
  }

  @Test
  public void testOptions() {
    // TODO define options for Plot and other built-ins
    // check("Options(Plot)", //
    // "");
    check("Options(f) = {a -> 1, b -> 2};", //
        "");
    check("Options(f)", //
        "{a->1,b->2}");
    check("f(x_, OptionsPattern()) := {x, OptionValue(a)}", //
        "");
    check("f(7, a -> uuu)", //
        "{7,uuu}");
    check("Options(Plus)", //
        "{}");
    check("Options(Factor)", //
        "{Extension->None,GaussianIntegers->False,Modulus->0}");
  }

  @Test
  public void testOptional() {
    check("ReplaceList({a,b,c},{a_:5,b__}->{{a},{b}})", //
        "{{{a},{b,c}},{{5},{a,b,c}}}");

    check("g(b_.?IntegerQ) :={b}", //
        "");

    check("f(a)/.f(a,b_.)->{a,b}", //
        "f(a)");
    check("f(a,b)/.f(a,b_.)->{a,b}", //
        "{a,b}");
    check("f(a)/.f(a,b_:c)->{a,b}", //
        "{a,c}");
    check("f(a,b)/.f(a,b_:c)->{a,b}", //
        "{a,b}");

    check("f(x,y)/.f(a__,b_.)->{{a},{b}}", //
        "{{x},{y}}");
    check("f(x,y)/.f(a___,b_.)->{{a},{b}}", //
        "{{x},{y}}");
    check("f(x,y)/.f(a__,b_:c)->{{a},{b}}", //
        "{{x},{y}}");
    check("f(x,y)/.f(a___,b_:c)->{{a},{b}}", //
        "{{x},{y}}");

    check("f(x,y)/.f(a___,b_:c,d_:e)->{{a},{b},{d}}", //
        "{{},{x},{y}}");
    check("f(x)/.f(a_,b_:y,c_:z)->{{a},{b},{c}}", //
        "{{x},{y},{z}}");
    check("f( )/.f(a_,b_:y,c_:z)->{{a},{b},{c}}", //
        "f()");
    check("f(x,i,j)/.f(a_,b_:y,c_:z)->{{a},{b},{c}}", //
        "{{x},{i},{j}}");
    check("a /.f(a,c_.)->{{c}}", //
        "a");

    check("a /. a + c_.->{{c}}", //
        "{{0}}");

    check("MatchQ(a,f(a,c_.))", //
        "False");
    check("MatchQ(a,a+c_.)", //
        "True");

    check("a/.a+c_.+d_.->{{c},{d}}", //
        "{{0},{0}}");
    check("Cos(x)/.(_+c_.+d_.)->{{c},{d}}", //
        "{{0},{0}}");

    check("5*a/.Optional(c1_?NumberQ)*a_->{{c1},{a}}", //
        "{{5},{a}}");
    check("a/.Optional(c1_?NumberQ)*a_->{{c1},{a}}", //
        "{{1},{a}}");

    check("MatchQ(f(a,b),f(c1__?NumberQ))", //
        "False");
    check("MatchQ(f(1,2),f(c1__?NumberQ))", //
        "True");
    check("MatchQ(f(1,2),f(Optional(c1__?NumberQ)))", //
        "False");
    check("MatchQ(f(1),f(Optional(c1__?NumberQ)))", //
        "True");

    check("ReplaceList({a,b,c},{a_:5,b__}->{{a},{b}})", //
        "{{{a},{b,c}},{{5},{a,b,c}}}");
    // TODO add missing combinations
    check("ReplaceList({a,b,c},{a_:5,b_:6,c___}->{{a},{b},{c}})", //
        "{{{a},{b},{c}},{{5},{6},{a,b,c}}}");

    check("MatchQ(-x,p_.)", //
        "True");
    check("MatchQ(-x*a,p_.*a)", //
        "True");
    check("MatchQ(__, Optional(1)*a_)", //
        "True");
    check("MatchQ(x^x, x^Optional(exp_))", //
        "True");

    check("f(a) /. f(x_, y_:3) -> {x, y}", "{a,3}");

    check("f(x_, Optional(y_,1)) := {x, y}", "");
    check("f(1, 2)", "{1,2}");
    check("f(a)", "{a,1}");

    check("g(x_, y_:1) := {x, y}", "");
    check("g(1, 2)", "{1,2}");
    check("g(a)", "{a,1}");

    // check("Default(h)=0", "0");
    // check("h(a) /. h(x_, y_.) -> {x, y}", "");

  }

  @Test
  public void testDocOptionValue() {
    // TODO doc/OptionValue
    check("f(a->3) /. f(OptionsPattern({})) -> {OptionValue(a)}", //
        "{3}");
    check("f(a->3) /. f(OptionsPattern({})) -> {OptionValue(b)}", //
        "{b}");
    check("f(a->3) /. f(OptionsPattern({})) -> {OptionValue(a+b)}", //
        "{a+b}");
    check("f(a->5) /. f(OptionsPattern({})) -> {OptionValue(Symbol(\"a\"))}", //
        "{5}");
  }

  @Test
  public void testOptionValue() {
    // TODO
    check("OptionValue({foo`a -> 1}, bar`a)", //
        "1");

    check("Options(tt) = {\"aa\" -> a1, \"bb\" -> b1}", //
        "{aa->a1,bb->b1}");
    check("tt(x_, opts : OptionsPattern()) := {x, opts}", //
        "");
    check("tt(test, \"aa\"->r1)", //
        "{test,aa->r1}");
    check("tt(test,  aa ->r1)", //
        "{test,aa->r1}");
    check("tt(test, \"aa\"->r1, \"a2\"->r2 )", //
        "{test,aa->r1,a2->r2}");
    check("ClearAll(tt)", //
        "");
    check("Options(tt) = {\"aa\" -> a1, \"bb\" -> b1}", //
        "{aa->a1,bb->b1}");
    check("tt(x_,  OptionsPattern()) := {x, OptionValue(aa)}", //
        "");
    check("tt(test,  \"aa\" ->r1)", //
        "{test,r1}");
    check("tt(test,  aa ->r1)", //
        "{test,r1}");
    check("tt(test)", //
        "{test,a1}");

    check(
        "Options(f) = {a -> 1}; " + "Options(g) = {a -> 2};"
            + "f(g(OptionsPattern()), OptionsPattern()) := "
            + "{OptionValue(a), OptionValue(f,a), OptionValue(g,a)}", //
        "");

    check("f(g(a->10),a->11)", //
        "{11,11,10}");
    check("f(g( ),a->11)", //
        "{11,11,2}");
    check("f(g())", //
        "{1,1,2}");

    check("Options(f) = {a -> a0, b -> b0}", //
        "{a->a0,b->b0}");
    check("f(x_, OptionsPattern()) := {x, OptionValue(a)}", //
        "");
    check("f(7, a -> test)", //
        "{7,test}");
    check("f(7)", //
        "{7,a0}");

    check("Options(f) = {a -> x, b -> y}", //
        "{a->x,b->y}");
    check("OptionValue(f, {b -> 5}, {a, b})", //
        "{x,5}");
    check("{a, b} /. {b -> 5} /. Options(f)", //
        "{x,5}");
    check("OptionValue(f, {a -> 7, b -> a}, {a, b})", //
        "{7,a}");
    check("{a, b} /. {a -> 7, b -> a} /. Options(f)", //
        "{7,x}");

    check("OptionValue({a -> 1}, {a, b})", //
        "{1,b}");
    check("OptionValue({opt -> 1}, opt)", //
        "1");
    check("OptionValue({\"opt\" -> 1}, \"opt\")", //
        "1");
    check("OptionValue({opt -> 1}, \"opt\")", //
        "1");
    check("OptionValue({\"opt\" -> 1}, opt)", //
        "1");

    check("ClearAll(f,g)", //
        "");
    check(
        "Options(f) = {a -> 1}; " + "Options(g) = {a -> 2};" + "f(g(OptionsPattern())) := "
            + "{OptionValue(a), OptionValue(f,a), OptionValue(g,a)}", //
        "");
    check("f(g())", //
        "{OptionValue(a),1,2}");

    check("Options(f3):={a->12}", //
        "");
    check("f3(x_,OptionsPattern()):=x^OptionValue(a)", //
        "");
    check("f3(y)", //
        "y^12");

    check("Options(f4):={a->12}", //
        "");
    check("f4(x_,OptionsPattern({a:>4})):=x^OptionValue(a)", //
        "");
    check("f4(y)", //
        "y^4");
  }

  @Test
  public void testOptionsPattern() {
    check("Options(f)={a->a0, b->b0};", //
        "");
    check("f(x_,OptionsPattern()):={x, OptionValue(a)}", //
        "");
    check("f(11, a->iam)", //
        "{11,iam}");
    check("f(22)", //
        "{22,a0}");
    check("f(x_,OptionsPattern({a -> a0, b -> b0})):={x, OptionValue(a)}", //
        "");
    check("f(11)", //
        "{11,a0}");
    check("opts:OptionsPattern() // FullForm", //
        "Pattern(opts,OptionsPattern())");
    check("OptionsPattern() // FullForm", //
        "OptionsPattern()");
  }


  @Test
  public void testReplace() {
    check("Replace(<| key -> <|a -> 1, b -> 2|>|>, <|k_ -> v_, y___|> -> {k, v,  y}, {1})", //
        "<|Key->{a,1,b->2}|>");
    check("f( <|key_ -> val_|> ) := <|val -> key|>", //
        "");
    check("f( <|a -> 1|> )", //
        "<|1->a|>");
    check("Replace(<|a -> 1|>, <|key_ -> val_|> :> <|val -> key|>)", //
        "<|1->a|>");
    check("Replace({1, 2}, Dispatch[{1 -> a, 3 -> b}], 1)", //
        "{a,2}");
    check("Replace({a, b}, <|a -> 1, c -> 2|>, 1)", //
        "{1,b}");
    check("Replace(f(f(f(f(x)))), f(x_) :> g(x), All)", //
        "g(g(g(g(x))))");
    check("Replace(f(f(f(f(x)))), f(x_) :> g(x),{0,2})", //
        "g(g(g(f(x))))");

    check("Replace({1, 3, 2, x, 6, Pi}, _?PrimeQ -> \"prim\", {1})", //
        "{1,prim,prim,x,6,Pi}");
    check("Replace({1, 3, 2, x, 6, Pi}, t_ /; Mod[t, 3] == 0 -> \"3*n\", {1})", //
        "{1,3*n,2,x,3*n,Pi}");

    check("Replace(Hold(x + x), x -> 7, {-1})", //
        "Hold(7+7)");
    check("Replace(Hold(7+7), x_ -> x+x, {-1})", //
        "Hold(2*7+2*7)");
    check("Replace(Hold(7+7), x_ :> x+x, {-1})", //
        "Hold(7+7+7+7)");
    check("Replace(x,x -> 1)", //
        "1");
    check("Replace({x,y},x -> 1)", //
        "{x,y}");
    check("Replace({x,y},{_,_} -> 1)", //
        "1");
    check("Replace({x,y,z},x -> 1,1)", //
        "{1,y,z}");
    check("Replace({{x},x,{{x}}},x -> 1,2)", //
        "{{1},1,{{x}}}");
    check("Replace({x,{x}},x -> 1,{2})", //
        "{x,{1}}");
    check("Replace({x,x(x)},x -> 1,2)", //
        "{1,x(1)}");

    // By default, only the top level is searched for matches
    check("Replace(1 + x, {x -> 2})", //
        "1+x");
    // use Replace() as an operator
    check("Replace({x_ -> x + 1})[10]", //
        "11");
    // Replace replaces the deepest levels first
    check("Replace(x(1), {x(1) -> y, 1 -> 2}, All)", //
        "x(2)");
    // Replace stops after the first replacement
    check("Replace(x, {x -> {}, _List -> y})", //
        "{}");
    check("Replace(x^2, x^2 -> a + b)", //
        "a+b");
    check("Replace(1+x^2, x^2 -> a + b)", //
        "1+x^2");

    check("Replace(x, {x -> a, x -> b})", //
        "a");
    check("Replace(x, {y -> a, x -> b, x->c})", //
        "b");

    check("Replace(x, {{x -> a}, {x -> b}})", //
        "{a,b}");
    check("Replace(x, {{x -> a}, {d -> b}})", //
        "{a,x}");
    check("Replace(x, {{e->q, x -> a}, {x -> b}})", //
        "{a,b}");

    // Test with level specification
    check("Replace(f(1, x^2,x^2), x^2 -> a + b, {1})", //
        "f(1,a+b,a+b)");
    check("Replace(f(1, x^2,x^2), z -> a + b, {1})", //
        "f(1,x^2,x^2)");
    check("Replace(f(1, x^2,x^2), {{1 -> a + b},{x^2 -> a + b}}, {1})", //
        "{f(a+b,x^2,x^2),f(1,a+b,a+b)}");
    check("Replace(f(1, x^2,x^2), {{z -> a + b},{w -> a + b}}, {1})", //
        "{f(1,x^2,x^2),f(1,x^2,x^2)}");
    check("Replace(f(1, x, x), {y -> a, x -> b, x->c}, {1})", //
        "f(1,b,b)");
    check("Replace(f(1, x, x), {y -> a, z -> b, w->c}, {1})", //
        "f(1,x,x)");
    // check("Replace({x, x, x}, x :> RandomReal(), {1})",
    // "{0.20251412388709988,0.7585256738344558,0.0882472501351631}");
  }

  @Test
  public void testReplaceAll() {
    // example from https://en.wikipedia.org/wiki/Wolfram_Language
    // check(
    // "sortRule := {x___,y_,z_,k___} /; y>z -> {x,z,y,k}", //
    // "");
    // check(
    // "{ 9, 5, 3, 1, 2, 4 } /. sortRule", //
    // "{5,9,3,1,2,4}");
    //
    // check(
    // "1/.(2)", //
    // "1/.2");
    // // parse as floating point number
    // check(
    // "1/.2", //
    // "5.0");

    // https://mathematica.stackexchange.com/a/9234/21734
    // print depth-first preorder
    check("{{1, {2, 3}}, {4, 5}} /. _?Print -> Null;", //
        "");

    check("<|a -> b|> /. b :> x", //
        "<|a->x|>");
    check("<|a -> 1|> /. <|a -> x_|> :> x", //
        "1");
    check("{1, 2} /. Dispatch({1 -> a, 3 -> b})", //
        "{a,2}");
    check("{1, 2} /. <|4 -> a, 2 -> b|>", //
        "{1,b}");

    check("{{}, {a, a}, {a, b}, {a, a, a}, {a}} /. {a ..} -> x", //
        "{{},x,{a,b},x,x}");
    check("{{}, {f(a), f(b)}, {f(a)}, {f(a, b)}, {f(a), g(b)}} /. {f(_) ..} -> x", //
        "{{},x,x,{f(a,b)},{f(a),g(b)}}");
    check("{f( ), f(a,a), f(a,b), f(a,a,a)} /.  f(a..) -> x", //
        "{f(),x,f(a,b),x}");

    check("f(a,b,23,4,5,6)/. x_Integer->test", //
        "f(a,b,test,test,test,test)");
    check("Indeterminate/.x->3", //
        "Indeterminate");
    check("{x,y,z}/.x -> 1", //
        "{1,y,z}");
    check("{x(x),y}/.x -> 1", //
        "{1[1],y}");
    check("{{x,y}}/.x:>Sequence[2,3]", //
        "{{2,3,y}}");
    check("{{x,y},y}/.{_,_} -> {1,1}", //
        "{1,1}");

    check("{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z)} /. f_Power :> (f /. x->10)", //
        "{x,Sin(x),100,x*y,x+y,g(y,x),h(x,y,z)}");
    check("{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z)} /. Sin(x_) -> Sin(10)", //
        "{x,Sin(10),x^2,x*y,x+y,g(y,x),h(x,y,z)}");
    check(
        "{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z),Cos(y)} /. HoldPattern(Plus(t__)) :> (Plus(t) /. x->10)", //
        "{x,Sin(x),x^2,x*y,10+y,g(y,x),h(x,y,z),Cos(y)}");
    check("{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z),Cos(y)} /. Plus(t__) :> (Plus(t) /. x->10)", //
        "{10,Sin(10),100,10*y,10+y,g(y,10),h(10,y,z),Cos(y)}");

    check("{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z),Cos(y)} /. Power(t__) :> (Power(t) /. x->10)", //
        "{10,Sin(10),100,10*y,10+y,g(y,10),h(10,y,z),Cos(y)}");
    check(
        "{x,Sin(x),x^2,x*y,x+y,g(y,x),h(x,y,z),Cos(y)} /. HoldPattern(Power(t__)) :> (Power(t) /. x->10)", //
        "{x,Sin(x),100,x*y,x+y,g(y,x),h(x,y,z),Cos(y)}");

    check("{f(x),g(x),f(x,y),Sin(x+y),f(),f(x,y,z),Cos(y)} /. f(t__) :> a*f(t)", //
        "{a*f(x),g(x),a*f(x,y),Sin(x+y),f(),a*f(x,y,z),Cos(y)}");
    check("{f(x),g(x),f(x,y),Sin(x+y),f(),f(x,y,z),Cos(y)} /. x_f :> a*x", //
        "{a*f(x),g(x),a*f(x,y),Sin(x+y),a*f(),a*f(x,y,z),Cos(y)}");
    check("{f(x),g(x),f(x,y),Sin(x+y),f(),f(x,y,z),Cos(y)} /. f(t___) :> a*f(t)", //
        "{a*f(x),g(x),a*f(x,y),Sin(x+y),a*f(),a*f(x,y,z),Cos(y)}");

    // prints
    // {{1,{2,3}},{4,5}}
    // {1,{2,3}}
    // {2,3}
    // {4,5}
    check("{{1, {2, 3}}, {4, 5}} /. {_, _} ? Print -> Null;", //
        "");
    // prints:
    // {{1,{2,3}},{4,5}}
    // List
    // {1,{2,3}}
    // List
    // 1
    // {2,3}
    // List
    // 2
    // 3
    // {4,5}
    // List
    // 4
    // 5
    check("{{1, {2, 3}}, {4, 5}} /. _?Print -> Null;", //
        "");
    check("g(a + b + c + d, b + d) /. g(x_ + y_, x_) -> p(x, y)", //
        "p(b+d,a+c)");

    check("2*x*y /. {x -> a, y -> b}", //
        "2*a*b");
    check("2*x*y /. {2*x -> a, 2*x*y -> b}", //
        "a*y");
    check("2*x*y /. {x -> a, 2*x*y -> b}", //
        "b");
    check("ReplaceAll(x^2+3*x+12, x->10)", //
        "142");
    check("x_Integer /. x->xvar", //
        "xvar_Integer");
    check("x__ /. x->xvar", //
        "xvar__");
    check("x___ /. x->xvar", //
        "xvar___");
    check("a + b + c /. a + c -> p", //
        "b+p");
    // check("g(a + b + c + d, b + d) /. g(x_ + y_, x_) -> p(x, y)", "p(b+d,a+c)");

    // TODO
    // check("ReplaceAll({a, b, c}, {___, x__, ___} -> {x})", "{a}");
    check("{g(1), Hold(g(1))} /. g(n_) -> n + 1", //
        "{2,Hold(1+1)}");

    check("x /. { }", //
        "x");
    check("x /. {{x -> 1}, {y -> 2}}", //
        "{1,x}");
    check("{a, b, c} /. {a -> b, b -> d}", //
        "{b,d,c}");
    check("{a, b, c} /. a -> b /. b -> d", //
        "{d,d,c}");
    check("{g(1), Hold(g(1))} /. g(n_) -> n + 1", //
        "{2,Hold(1+1)}");

    check("u(v(w,x,y) /. { {}, {w->y}})", //
        "u({v(w,x,y),v(y,x,y)})");
    check("u(v(w,x,y) /. { {}, w->y})", //
        "u(v(w,x,y)/.{{},w->y})");
    check("ReplaceAll(x -> a)[{x, x^2, y, z}]", //
        "{a,a^2,y,z}");
    check("x /. {y -> 2, z -> 3}", //
        "x");
    check("x /. {x -> 1, x -> 3, x -> 7}", //
        "1");
    check("x /. {{x -> 1}, {x -> 3}, {x -> 7}}", //
        "{1,3,7}");
    check("a == b /. _Equal -> 2", //
        "2");
    check("If(1 == k, itstrue, itsfalse) /. _If -> 99", //
        "99");

    check("ReplaceAll({a -> 1})[{a, b}]", //
        "{1,b}");
    check("{x, x^2, y, z} /. x -> a", //
        "{a,a^2,y,z}");
    check("{x, x^2, y, z} /. x -> {a, b}", //
        "{{a,b},{a^2,b^2},y,z}");
    check("Sin(x) /. Sin -> Cos", //
        "Cos(x)");
    check("1 + x^2 + x^4 /. x^p_ -> f(p)", //
        "1+f(2)+f(4)");
    check("x /. {x -> 1, x -> 3, x -> 7}", //
        "1");
    check("x /. {{x -> 1}, {x -> 3}, {x -> 7}}", //
        "{1,3,7}");
    check("x /. {{a->z, x -> 1}, {x -> 3}, {x -> 7}}", //
        "{1,3,7}");
    check("{a, b, c} /. List -> f", //
        "f(a,b,c)");
    check("SetAttributes($r, {OneIdentity, Flat});$r(a, b, b, c) /. $r(x_, x_) -> rp(x)", //
        "$r(a,rp(b),c)");

    check("f(a) + f(b) /. f(x_) -> x^2", //
        "a^2+b^2");
    check("(x_ /; x < 0)", //
        "x_/;x<0");
    check("{1 + a, 2 + a, -3 + a} /. (x_ /; x < 0) + a -> p(x)", //
        "{1+a,2+a,p(-3)}");
    check("$fac(x_ /; x > 0) := x!;$fac(6) + $fac(-4)", //
        "720+$fac(-4)");

    check("f(a + b) + f(a + c) /. f(a + x_) + f(c + y_) -> p(x, y)", //
        "p(b,a)");
    // wrong result
    check("f(a + b) + f(a + c) + f(b + d) /. f(a + x_) + f(c + y_) -> p(x, y)", //
        "f(b+d)+p(b,a)");

    check("g(a + b, a) /. g(x_ + y_, x_) -> p(x, y)", //
        "p(a,b)");
    check("g(a + b, b) /. g(x_ + y_, x_) -> p(x, y)", //
        "p(b,a)");
    check("h(a + b, a + b) /. h(x_ + y_, x_ + z_) -> p(x, y, z)", //
        "p(a,b,b)");
    check(
        "SetAttributes($q, Orderless);f($q(a, b), $q(b, c)) /. f($q(x_, y_), $q(x_, z_)) -> p(x, y, z)", //
        "p(b,a,c)");
    check("g(a + b + c) /. g(x_ + y_) -> p(x, y)", //
        "p(a,b+c)");
    check("g(a + b + c + d) /. g(x_ + y_) -> p(x, y)", //
        "p(a,b+c+d)");
    check("g(a + b + c + d, b + d) /. g(x_ + y_, x_) -> p(x, y)", //
        "p(b+d,a+c)");
    check("a + b + c /. a + c -> p", //
        "b+p");
    check("u(a) + u(b) + v(c) + v(d) /. u(x_) + u(y_) -> u(x + y)", //
        "u(a+b)+v(c)+v(d)");
    check("SetAttributes($r, Flat);$r(a, b, a, b) /. $r(x_, x_) -> rp(x)", //
        "rp($r(a,b))");

    // correct because OneIdentity is set:
    check("SetAttributes($r, {OneIdentity, Flat});$r(a, b, b, c) /. $r(x_, x_) -> rp(x)", //
        "$r(a,rp(b),c)");
    check("SetAttributes($r, {OneIdentity, Flat});$r(a, b, b, c) /. $r(b, b) -> rp(b)", //
        "$r(a,rp(b),c)");

    // wrong because OneIdentity is not set:
    check("SetAttributes($r, Flat);$r(a, b, b, c) /. $r(x_, x_) -> rp(x)", //
        "$r(a,rp(b),c)");
    // wrong because OneIdentity is not set:
    check("SetAttributes($r, Flat);$r(a, b, b, c) /. $r(b, b) -> rp(b)", //
        "$r(a,rp(b),c)");
    // check("","");

    check("{c+d+e} /. x_+y_->{x,y}", //
        "{{c,d+e}}");
    check("{a+b,x,c+d+e} /. x_+y_->{x,y}", //
        "{{a,b},x,{c,d+e}}");
  }

  @Test
  public void testReplaceList() {
    check("ReplaceList(a+b+c+d+e+f,(x_+y_+z_) :> {{x},{y},{z}}, 3)", //
        "{{{a},{b},{c+d+e+f}},"//
            + "{{a},{c},{b+d+e+f}},"//
            + "{{a},{d},{b+c+e+f}}}");

    check("ReplaceList({b*x, a*b*x*z, x}, {a_*x_,a_.*x_^y_.*z_, x_ } -> {a,x,y,z})", //
        "{{b,x,1,a*z}}");

    check("ReplaceList({a, b}, {___, x__, ___} -> {x})", //
        "{{a},{a,b},{b}}");

    check("ReplaceList(a + b + c, x_ + y_ -> g(x, y))", //
        "{g(a,b+c)," //
            + "g(b,a+c)," //
            + "g(c,a+b)," //
            + "g(a+b,c)," //
            + "g(a+c,b)," //
            + "g(b+c,a)}");

    check("ReplaceList(x, {x -> a, x -> b, x -> c})", //
        "{a,b,c}");
    check("Replace(x, {x -> a, x -> b, x -> c})", //
        "a");

    // operator form
    check("ReplaceList({x__, y__} -> {{x}, {y}})[{a, b, c, d}]", //
        "{{{a},{b,c,d}}," //
            + "{{a,b},{c,d}}," //
            + "{{a,b,c},{d}}}");

    check("ReplaceList({a, b, c, d, e, f}, {x__, y__} -> {{x}, {y}})", //
        "{{{a},{b,c,d,e,f}}," //
            + "{{a,b},{c,d,e,f}}," //
            + "{{a,b,c},{d,e,f}}," //
            + "{{a,b,c,d},{e,f}}," //
            + "{{a,b,c,d,e},{f}}}");

    check("Replace({a, b, c, d, e, f}, {x__, y__} -> {{x}, {y}})", //
        "{{a},{b,c,d,e,f}}");

    check("ReplaceList(a+b,(x_+y_) :> {{x},{y}})", //
        "{{{a},{b}}," //
            + "{{b},{a}}}");

    check("ReplaceList(a+b+c,(x_+y_) :> {{x},{y}})", //
        "{{{a},{b+c}}," //
            + "{{b},{a+c}}," //
            + "{{c},{a+b}}," //
            + "{{a+b},{c}}," //
            + "{{a+c},{b}}," //
            + "{{b+c},{a}}}");

    check("ReplaceList(a+b+c+d+e+f,(x_+y_+z_) :> {{x},{y},{z}})", //
        "{{{a},{b},{c+d+e+f}}," //
            + "{{a},{c},{b+d+e+f}}," //
            + "{{a},{d},{b+c+e+f}}," //
            + "{{a},{e},{b+c+d+f}}," //
            + "{{a},{f},{b+c+d+e}}," //
            + "{{b},{a},{c+d+e+f}}," //
            + "{{b},{c},{a+d+e+f}}," //
            + "{{b},{d},{a+c+e+f}}," //
            + "{{b},{e},{a+c+d+f}}," //
            + "{{b},{f},{a+c+d+e}}," //
            + "{{c},{a},{b+d+e+f}}," //
            + "{{c},{b},{a+d+e+f}}," //
            + "{{c},{d},{a+b+e+f}}," //
            + "{{c},{e},{a+b+d+f}}," //
            + "{{c},{f},{a+b+d+e}}," //
            + "{{d},{a},{b+c+e+f}}," //
            + "{{d},{b},{a+c+e+f}}," //
            + "{{d},{c},{a+b+e+f}}," //
            + "{{d},{e},{a+b+c+f}}," //
            + "{{d},{f},{a+b+c+e}}," //
            + "{{e},{a},{b+c+d+f}}," //
            + "{{e},{b},{a+c+d+f}}," //
            + "{{e},{c},{a+b+d+f}}," //
            + "{{e},{d},{a+b+c+f}}," //
            + "{{e},{f},{a+b+c+d}}," //
            + "{{f},{a},{b+c+d+e}}," //
            + "{{f},{b},{a+c+d+e}}," //
            + "{{f},{c},{a+b+d+e}}," //
            + "{{f},{d},{a+b+c+e}}," //
            + "{{f},{e},{a+b+c+d}}," //
            + "{{a},{b+c},{d+e+f}}," //
            + "{{a},{b+d},{c+e+f}}," //
            + "{{a},{b+e},{c+d+f}}," //
            + "{{a},{b+f},{c+d+e}}," //
            + "{{a},{c+d},{b+e+f}}," //
            + "{{a},{c+e},{b+d+f}}," //
            + "{{a},{c+f},{b+d+e}}," //
            + "{{a},{d+e},{b+c+f}}," //
            + "{{a},{d+f},{b+c+e}}," //
            + "{{a},{e+f},{b+c+d}}," //
            + "{{b},{a+c},{d+e+f}}," //
            + "{{b},{a+d},{c+e+f}}," //
            + "{{b},{a+e},{c+d+f}}," //
            + "{{b},{a+f},{c+d+e}}," //
            + "{{b},{c+d},{a+e+f}}," //
            + "{{b},{c+e},{a+d+f}}," //
            + "{{b},{c+f},{a+d+e}}," //
            + "{{b},{d+e},{a+c+f}}," //
            + "{{b},{d+f},{a+c+e}}," //
            + "{{b},{e+f},{a+c+d}}," //
            + "{{c},{a+b},{d+e+f}}," //
            + "{{c},{a+d},{b+e+f}}," //
            + "{{c},{a+e},{b+d+f}}," //
            + "{{c},{a+f},{b+d+e}}," //
            + "{{c},{b+d},{a+e+f}}," //
            + "{{c},{b+e},{a+d+f}}," //
            + "{{c},{b+f},{a+d+e}}," //
            + "{{c},{d+e},{a+b+f}}," //
            + "{{c},{d+f},{a+b+e}}," //
            + "{{c},{e+f},{a+b+d}}," //
            + "{{d},{a+b},{c+e+f}}," //
            + "{{d},{a+c},{b+e+f}}," //
            + "{{d},{a+e},{b+c+f}}," //
            + "{{d},{a+f},{b+c+e}}," //
            + "{{d},{b+c},{a+e+f}}," //
            + "{{d},{b+e},{a+c+f}}," //
            + "{{d},{b+f},{a+c+e}}," //
            + "{{d},{c+e},{a+b+f}}," //
            + "{{d},{c+f},{a+b+e}}," //
            + "{{d},{e+f},{a+b+c}}," //
            + "{{e},{a+b},{c+d+f}}," //
            + "{{e},{a+c},{b+d+f}},{{e},{a+d},{b+c+f}},{{e},{a+f},{b+c+d}},{{e},{b+c},{a+d+f}},{{e},{b+d},{a+c+f}},{{e},{b+f},{a+c+d}},{{e},{c+d},{a+b+f}},"
            + "{{e},{c+f},{a+b+d}},{{e},{d+f},{a+b+c}},{{f},{a+b},{c+d+e}}," //
            + "{{f},{a+c},{b+d+e}},{{f},{a+d},{b+c+e}},{{f},{a+e},{b+c+d}},{{f},{b+c},{a+d+e}}," //
            + "{{f},{b+d},{a+c+e}},{{f},{b+e},{a+c+d}},{{f},{c+d},{a+b+e}},{{f},{c+e},{a+b+d}}," //
            + "{{f},{d+e},{a+b+c}},{{a},{b+c+d},{e+f}},{{a},{b+c+e},{d+f}},{{a},{b+c+f},{d+e}}," //
            + "{{a},{b+d+e},{c+f}},{{a},{b+d+f},{c+e}},{{a},{b+e+f},{c+d}},{{a},{c+d+e},{b+f}}," //
            + "{{a},{c+d+f},{b+e}},{{a},{c+e+f},{b+d}},{{a},{d+e+f},{b+c}},{{b},{a+c+d},{e+f}}," //
            + "{{b},{a+c+e},{d+f}},{{b},{a+c+f},{d+e}},{{b},{a+d+e},{c+f}},{{b},{a+d+f},{c+e}}," //
            + "{{b},{a+e+f},{c+d}},{{b},{c+d+e},{a+f}},{{b},{c+d+f},{a+e}},{{b},{c+e+f},{a+d}}," //
            + "{{b},{d+e+f},{a+c}},{{c},{a+b+d},{e+f}},{{c},{a+b+e},{d+f}},{{c},{a+b+f},{d+e}},{{c},{a+d+e},{b+f}}," //
            + "{{c},{a+d+f},{b+e}},{{c},{a+e+f},{b+d}},{{c},{b+d+e},{a+f}},{{c},{b+d+f},{a+e}},{{c},{b+e+f},{a+d}}," //
            + "{{c},{d+e+f},{a+b}},{{d},{a+b+c},{e+f}},{{d},{a+b+e},{c+f}},{{d},{a+b+f},{c+e}},{{d},{a+c+e},{b+f}}," //
            + "{{d},{a+c+f},{b+e}},{{d},{a+e+f},{b+c}},{{d},{b+c+e},{a+f}},{{d},{b+c+f},{a+e}},{{d},{b+e+f},{a+c}}," //
            + "{{d},{c+e+f},{a+b}},{{e},{a+b+c},{d+f}},{{e},{a+b+d},{c+f}},{{e},{a+b+f},{c+d}},{{e},{a+c+d},{b+f}}," //
            + "{{e},{a+c+f},{b+d}},{{e},{a+d+f},{b+c}},{{e},{b+c+d},{a+f}},{{e},{b+c+f},{a+d}},{{e},{b+d+f},{a+c}}," //
            + "{{e},{c+d+f},{a+b}},{{f},{a+b+c},{d+e}},{{f},{a+b+d},{c+e}},{{f},{a+b+e},{c+d}},{{f},{a+c+d},{b+e}}," //
            + "{{f},{a+c+e},{b+d}},{{f},{a+d+e},{b+c}},{{f},{b+c+d},{a+e}},{{f},{b+c+e},{a+d}},{{f},{b+d+e},{a+c}}," //
            + "{{f},{c+d+e},{a+b}},{{a},{b+c+d+e},{f}},{{a},{b+c+d+f},{e}},{{a},{b+c+e+f},{d}},{{a},{b+d+e+f},{c}}," //
            + "{{a},{c+d+e+f},{b}},{{b},{a+c+d+e},{f}},{{b},{a+c+d+f},{e}},{{b},{a+c+e+f},{d}},{{b},{a+d+e+f},{c}}," //
            + "{{b},{c+d+e+f},{a}},{{c},{a+b+d+e},{f}},{{c},{a+b+d+f},{e}},{{c},{a+b+e+f},{d}},{{c},{a+d+e+f},{b}}," //
            + "{{c},{b+d+e+f},{a}},{{d},{a+b+c+e},{f}},{{d},{a+b+c+f},{e}},{{d},{a+b+e+f},{c}},{{d},{a+c+e+f},{b}}," //
            + "{{d},{b+c+e+f},{a}},{{e},{a+b+c+d},{f}},{{e},{a+b+c+f},{d}},{{e},{a+b+d+f},{c}},{{e},{a+c+d+f},{b}}," //
            + "{{e},{b+c+d+f},{a}},{{f},{a+b+c+d},{e}},{{f},{a+b+c+e},{d}},{{f},{a+b+d+e},{c}},{{f},{a+c+d+e},{b}},{{f},{b+c+d+e},{a}},{{a+b},{c},{d+e+f}},{{a+b},{d},{c+e+f}},{{a+b},{e},{c+d+f}},{{a+b},{f},{c+d+e}},{{a+c},{b},{d+e+f}},{{a+c},{d},{b+e+f}},{{a+c},{e},{b+d+f}},{{a+c},{f},{b+d+e}},{{a+d},{b},{c+e+f}},{{a+d},{c},{b+e+f}},{{a+d},{e},{b+c+f}},{{a+d},{f},{b+c+e}},{{a+e},{b},{c+d+f}},{{a+e},{c},{b+d+f}},{{a+e},{d},{b+c+f}},{{a+e},{f},{b+c+d}},{{a+f},{b},{c+d+e}},{{a+f},{c},{b+d+e}},{{a+f},{d},{b+c+e}},{{a+f},{e},{b+c+d}},{{b+c},{a},{d+e+f}},{{b+c},{d},{a+e+f}},{{b+c},{e},{a+d+f}},{{b+c},{f},{a+d+e}},{{b+d},{a},{c+e+f}},{{b+d},{c},{a+e+f}},{{b+d},{e},{a+c+f}},{{b+d},{f},{a+c+e}},{{b+e},{a},{c+d+f}},{{b+e},{c},{a+d+f}},{{b+e},{d},{a+c+f}},{{b+e},{f},{a+c+d}},{{b+f},{a},{c+d+e}},{{b+f},{c},{a+d+e}},{{b+f},{d},{a+c+e}},{{b+f},{e},{a+c+d}},{{c+d},{a},{b+e+f}},{{c+d},{b},{a+e+f}},{{c+d},{e},{a+b+f}},{{c+d},{f},{a+b+e}},{{c+e},{a},{b+d+f}},{{c+e},{b},{a+d+f}},{{c+e},{d},{a+b+f}},{{c+e},{f},{a+b+d}},{{c+f},{a},{b+d+e}},{{c+f},{b},{a+d+e}},{{c+f},{d},{a+b+e}},{{c+f},{e},{a+b+d}},{{d+e},{a},{b+c+f}},{{d+e},{b},{a+c+f}},{{d+e},{c},{a+b+f}},{{d+e},{f},{a+b+c}},{{d+f},{a},{b+c+e}},{{d+f},{b},{a+c+e}},{{d+f},{c},{a+b+e}},{{d+f},{e},{a+b+c}},{{e+f},{a},{b+c+d}},{{e+f},{b},{a+c+d}},{{e+f},{c},{a+b+d}},{{e+f},{d},{a+b+c}},{{a+b},{c+d},{e+f}},{{a+b},{c+e},{d+f}},{{a+b},{c+f},{d+e}},{{a+b},{d+e},{c+f}},{{a+b},{d+f},{c+e}},{{a+b},{e+f},{c+d}},{{a+c},{b+d},{e+f}},{{a+c},{b+e},{d+f}},{{a+c},{b+f},{d+e}},{{a+c},{d+e},{b+f}},{{a+c},{d+f},{b+e}},{{a+c},{e+f},{b+d}},{{a+d},{b+c},{e+f}},{{a+d},{b+e},{c+f}},{{a+d},{b+f},{c+e}},{{a+d},{c+e},{b+f}},{{a+d},{c+f},{b+e}},{{a+d},{e+f},{b+c}},{{a+e},{b+c},{d+f}},{{a+e},{b+d},{c+f}},{{a+e},{b+f},{c+d}},{{a+e},{c+d},{b+f}},{{a+e},{c+f},{b+d}},{{a+e},{d+f},{b+c}},{{a+f},{b+c},{d+e}},{{a+f},{b+d},{c+e}},{{a+f},{b+e},{c+d}},{{a+f},{c+d},{b+e}},{{a+f},{c+e},{b+d}},{{a+f},{d+e},{b+c}},{{b+c},{a+d},{e+f}},{{b+c},{a+e},{d+f}},{{b+c},{a+f},{d+e}},{{b+c},{d+e},{a+f}},{{b+c},{d+f},{a+e}},{{b+c},{e+f},{a+d}},{{b+d},{a+c},{e+f}},{{b+d},{a+e},{c+f}},{{b+d},{a+f},{c+e}},{{b+d},{c+e},{a+f}},{{b+d},{c+f},{a+e}},{{b+d},{e+f},{a+c}},{{b+e},{a+c},{d+f}},{{b+e},{a+d},{c+f}},{{b+e},{a+f},{c+d}},{{b+e},{c+d},{a+f}},{{b+e},{c+f},{a+d}},{{b+e},{d+f},{a+c}},{{b+f},{a+c},{d+e}},{{b+f},{a+d},{c+e}},{{b+f},{a+e},{c+d}},{{b+f},{c+d},{a+e}},{{b+f},{c+e},{a+d}},{{b+f},{d+e},{a+c}},{{c+d},{a+b},{e+f}},{{c+d},{a+e},{b+f}},{{c+d},{a+f},{b+e}},{{c+d},{b+e},{a+f}},{{c+d},{b+f},{a+e}},{{c+d},{e+f},{a+b}},{{c+e},{a+b},{d+f}},{{c+e},{a+d},{b+f}},{{c+e},{a+f},{b+d}},{{c+e},{b+d},{a+f}},{{c+e},{b+f},{a+d}},{{c+e},{d+f},{a+b}},{{c+f},{a+b},{d+e}},{{c+f},{a+d},{b+e}},{{c+f},{a+e},{b+d}},{{c+f},{b+d},{a+e}},{{c+f},{b+e},{a+d}},{{c+f},{d+e},{a+b}},{{d+e},{a+b},{c+f}},{{d+e},{a+c},{b+f}},{{d+e},{a+f},{b+c}},{{d+e},{b+c},{a+f}},{{d+e},{b+f},{a+c}},{{d+e},{c+f},{a+b}},{{d+f},{a+b},{c+e}},{{d+f},{a+c},{b+e}},{{d+f},{a+e},{b+c}},{{d+f},{b+c},{a+e}},{{d+f},{b+e},{a+c}},{{d+f},{c+e},{a+b}},{{e+f},{a+b},{c+d}},{{e+f},{a+c},{b+d}},"
            + "{{e+f},{a+d},{b+c}},{{e+f},{b+c},{a+d}},{{e+f},{b+d},{a+c}},{{e+f},{c+d},{a+b}},{{a+b},{c+d+e},{f}}," //
            + "{{a+b},{c+d+f},{e}},{{a+b},{c+e+f},{d}},{{a+b},{d+e+f},{c}},{{a+c},{b+d+e},{f}},{{a+c},{b+d+f},{e}},{{a+c},{b+e+f},{d}},{{a+c},{d+e+f},{b}},{{a+d},{b+c+e},{f}},{{a+d},{b+c+f},{e}},{{a+d},{b+e+f},{c}},{{a+d},{c+e+f},{b}},{{a+e},{b+c+d},{f}},{{a+e},{b+c+f},{d}},{{a+e},{b+d+f},{c}},{{a+e},{c+d+f},{b}},{{a+f},{b+c+d},{e}},{{a+f},{b+c+e},{d}},{{a+f},{b+d+e},{c}},{{a+f},{c+d+e},{b}},{{b+c},{a+d+e},{f}},{{b+c},{a+d+f},{e}},{{b+c},{a+e+f},{d}},{{b+c},{d+e+f},{a}},{{b+d},{a+c+e},{f}},{{b+d},{a+c+f},{e}},{{b+d},{a+e+f},{c}},{{b+d},{c+e+f},{a}},{{b+e},{a+c+d},{f}},{{b+e},{a+c+f},{d}},{{b+e},{a+d+f},{c}},{{b+e},{c+d+f},{a}},{{b+f},{a+c+d},{e}},{{b+f},{a+c+e},{d}},{{b+f},{a+d+e},{c}},{{b+f},{c+d+e},{a}},{{c+d},{a+b+e},{f}},"
            + "{{c+d},{a+b+f},{e}},{{c+d},{a+e+f},{b}},{{c+d},{b+e+f},{a}},{{c+e},{a+b+d},{f}},{{c+e},{a+b+f},{d}},{{c+e},{a+d+f},{b}},{{c+e},{b+d+f},{a}},{{c+f},{a+b+d},{e}},{{c+f},{a+b+e},{d}},{{c+f},{a+d+e},{b}},{{c+f},{b+d+e},{a}},{{d+e},{a+b+c},{f}},{{d+e},{a+b+f},{c}},{{d+e},{a+c+f},{b}},{{d+e},{b+c+f},{a}},{{d+f},{a+b+c},{e}},{{d+f},{a+b+e},{c}},{{d+f},{a+c+e},{b}},{{d+f},{b+c+e},{a}},{{e+f},{a+b+c},{d}},{{e+f},{a+b+d},{c}},{{e+f},{a+c+d},{b}},{{e+f},{b+c+d},{a}},{{a+b+c},{d},{e+f}},{{a+b+c},{e},{d+f}},{{a+b+c},{f},{d+e}},{{a+b+d},{c},{e+f}},{{a+b+d},{e},{c+f}},{{a+b+d},{f},{c+e}},{{a+b+e},{c},{d+f}},{{a+b+e},{d},{c+f}},{{a+b+e},{f},{c+d}},{{a+b+f},{c},{d+e}},{{a+b+f},{d},{c+e}},{{a+b+f},{e},{c+d}},{{a+c+d},{b},{e+f}},{{a+c+d},{e},{b+f}},{{a+c+d},{f},{b+e}},{{a+c+e},{b},{d+f}},{{a+c+e},{d},{b+f}},{{a+c+e},{f},{b+d}},{{a+c+f},{b},{d+e}},{{a+c+f},{d},{b+e}},{{a+c+f},{e},{b+d}},{{a+d+e},{b},{c+f}},{{a+d+e},{c},{b+f}},{{a+d+e},{f},{b+c}},{{a+d+f},{b},{c+e}},{{a+d+f},{c},{b+e}},{{a+d+f},{e},{b+c}},{{a+e+f},{b},{c+d}},{{a+e+f},{c},{b+d}},{{a+e+f},{d},{b+c}},{{b+c+d},{a},{e+f}},{{b+c+d},{e},{a+f}},{{b+c+d},{f},{a+e}},{{b+c+e},{a},{d+f}},{{b+c+e},{d},{a+f}},{{b+c+e},{f},{a+d}},{{b+c+f},{a},{d+e}},{{b+c+f},{d},{a+e}},{{b+c+f},{e},{a+d}},"
            + "{{b+d+e},{a},{c+f}},{{b+d+e},{c},{a+f}},{{b+d+e},{f},{a+c}},{{b+d+f},{a},{c+e}},{{b+d+f},{c},{a+e}},{{b+d+f},{e},{a+c}},{{b+e+f},{a},{c+d}},{{b+e+f},{c},{a+d}},{{b+e+f},{d},{a+c}},{{c+d+e},{a},{b+f}},{{c+d+e},{b},{a+f}},{{c+d+e},{f},{a+b}},{{c+d+f},{a},{b+e}},{{c+d+f},{b},{a+e}},{{c+d+f},{e},{a+b}},{{c+e+f},{a},{b+d}},{{c+e+f},{b},{a+d}},{{c+e+f},{d},{a+b}},{{d+e+f},{a},{b+c}},{{d+e+f},{b},{a+c}},{{d+e+f},{c},{a+b}},{{a+b+c},{d+e},{f}},{{a+b+c},{d+f},{e}},{{a+b+c},{e+f},{d}},{{a+b+d},{c+e},{f}},{{a+b+d},{c+f},{e}},{{a+b+d},{e+f},{c}},{{a+b+e},{c+d},{f}},{{a+b+e},{c+f},{d}},{{a+b+e},{d+f},{c}},{{a+b+f},{c+d},{e}},{{a+b+f},{c+e},{d}},{{a+b+f},{d+e},{c}},{{a+c+d},{b+e},{f}},{{a+c+d},{b+f},{e}},{{a+c+d},{e+f},{b}},{{a+c+e},{b+d},{f}},{{a+c+e},{b+f},{d}},{{a+c+e},{d+f},{b}},{{a+c+f},{b+d},{e}},{{a+c+f},{b+e},{d}},{{a+c+f},{d+e},{b}},{{a+d+e},{b+c},{f}},{{a+d+e},{b+f},{c}},{{a+d+e},{c+f},{b}},{{a+d+f},{b+c},{e}},{{a+d+f},{b+e},{c}},{{a+d+f},{c+e},{b}},{{a+e+f},{b+c},{d}},{{a+e+f},{b+d},{c}},{{a+e+f},{c+d},{b}},{{b+c+d},{a+e},{f}},{{b+c+d},{a+f},{e}},{{b+c+d},{e+f},{a}},{{b+c+e},{a+d},{f}},{{b+c+e},{a+f},{d}},{{b+c+e},{d+f},{a}},{{b+c+f},{a+d},{e}},{{b+c+f},{a+e},{d}},{{b+c+f},{d+e},{a}},{{b+d+e},{a+c},{f}},{{b+d+e},{a+f},{c}},{{b+d+e},{c+f},{a}},{{b+d+f},{a+c},{e}},{{b+d+f},{a+e},{c}},{{b+d+f},{c+e},{a}},{{b+e+f},{a+c},{d}},{{b+e+f},{a+d},{c}},{{b+e+f},{c+d},{a}},{{c+d+e},{a+b},{f}},{{c+d+e},{a+f},{b}},{{c+d+e},{b+f},{a}},{{c+d+f},{a+b},{e}},{{c+d+f},{a+e},{b}},{{c+d+f},{b+e},{a}},{{c+e+f},{a+b},{d}},{{c+e+f},{a+d},{b}},{{c+e+f},{b+d},{a}},{{d+e+f},{a+b},{c}},"
            + "{{d+e+f},{a+c},{b}},{{d+e+f},{b+c},{a}},{{a+b+c+d},{e},{f}},{{a+b+c+d},{f},{e}},{{a+b+c+e},{d},{f}},{{a+b+c+e},{f},{d}},{{a+b+c+f},{d},{e}},{{a+b+c+f},{e},{d}},{{a+b+d+e},{c},{f}},{{a+b+d+e},{f},{c}},"
            + "{{a+b+d+f},{c},{e}},{{a+b+d+f},{e},{c}},{{a+b+e+f},{c},{d}},{{a+b+e+f},{d},{c}},{{a+c+d+e},{b},{f}}," //
            + "{{a+c+d+e},{f},{b}},{{a+c+d+f},{b},{e}},{{a+c+d+f},{e},{b}},{{a+c+e+f},{b},{d}}," //
            + "{{a+c+e+f},{d},{b}},{{a+d+e+f},{b},{c}},{{a+d+e+f},{c},{b}},{{b+c+d+e},{a},{f}}," //
            + "{{b+c+d+e},{f},{a}},{{b+c+d+f},{a},{e}},{{b+c+d+f},{e},{a}},{{b+c+e+f},{a},{d}}," //
            + "{{b+c+e+f},{d},{a}},{{b+d+e+f},{a},{c}},{{b+d+e+f},{c},{a}},{{c+d+e+f},{a},{b}}," //
            + "{{c+d+e+f},{b},{a}}}");

    check("ReplaceList({a, b, c, d, e, f}, {x__, y__, z__} :> {{x},{y},{z}})", //
        "{{{a},{b},{c,d,e,f}}," //
            + "{{a},{b,c},{d,e,f}}," //
            + "{{a},{b,c,d},{e,f}}," //
            + "{{a},{b,c,d,e},{f}}," //
            + "{{a,b},{c},{d,e,f}}," //
            + "{{a,b},{c,d},{e,f}}," //
            + "{{a,b},{c,d,e},{f}}," //
            + "{{a,b,c},{d},{e,f}}," //
            + "{{a,b,c},{d,e},{f}}," //
            + "{{a,b,c,d},{e},{f}}}"); //

    check("ReplaceList({a, b, c, d, e, f}, {x__, y__} -> {{x}, {y}})", //
        "{{{a},{b,c,d,e,f}},{{a,b},{c,d,e,f}},{{a,b,c},{d,e,f}},{{a,b,c,d},{e,f}},{{a,b,c,d,e},{f}}}"); //
    check("Replace({a, b, c, d, e, f}, {x__, y__} -> {{x}, {y}})", //
        "{{a},{b,c,d,e,f}}");

    check("ReplaceList({x__, y__} -> {{x}, {y}}) [{a, b, c, d}]", //
        "{{{a},{b,c,d}},{{a,b},{c,d}},{{a,b,c},{d}}}");
    check("ReplaceList({a, b, c, d}, {___, x__, ___} -> {x})", //
        "{{a},{a,b},{a,b,c},{a,b,c,d},{b},{b,c},{b,c,d},{c},{c,d},{d}}");
    check("ReplaceList({a, b, b, b, c, c, a}, {___, x_, x_, ___} -> x)", //
        "{b,b,c}");
    check("ReplaceList({a, b, c, a, d, b, d}, {___, x_, y__, x_, ___} -> {x, {y}})", //
        "{{a,{b,c}},{b,{c,a,d}},{d,{b}}}");

    check("ReplaceList(a + b + c, x_ + y_ :> {x, y})", //
        "{{a,b+c},{b,a+c},{c,a+b},{a+b,c},{a+c,b},{b+c,a}}");
    check("Replace(a + b + c, x_ + y_ :> {x, y})", //
        "{a,b+c}");
  }


  @Test
  public void testReplacePartPattern() {

    check("ReplacePart(f(x, y), _ -> g, Heads -> True)", //
        "g(g,g)");

    // github #412
    check("ReplacePart({{f},{a, b, c}, {d, e}}, {_, -1} -> test)", //
        "{{test},{a,b,test},{d,test}}");
    check("ReplacePart({{-2, b, c, d}, {f, g, h}, {i, -2}}, {_, -2} -> xx)", //
        "{{-2,b,xx,d},{f,xx,h},{xx,-2}}");

    check("ReplacePart(f(x, y), _ -> g)", //
        "f(g,g)");
    check("ReplacePart(f(x, y), _ -> g, Heads -> True)", //
        "g(g,g)");

    // check("ReplacePart(<|\"x\" -> 1, \"y\" -> 2|>, {0} -> f)", //
    // "f(1,2)");
    // check("ReplacePart({<|\"x\" -> 1, \"y\" -> 2|>}, {1, 1} -> g)", //
    // "{<|x->g,y->2|>}");
    check("ReplacePart({{a, b, c}, {d, e}, {f}}, i__ -> s(i))", //
        "{s(1),s(2),s(3)}");
    check("ReplacePart({{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, {___, 2, ___} -> x)", //
        "{{0,x,0},x,{0,x,0}}");
    check("ReplacePart({{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}, {i_, i_} -> f(i))", //
        "{{f(1),0,0},{0,f(2),0},{0,0,f(3)}}");
    check("ReplacePart({a, b, c, d, e, f, g}, _?EvenQ -> xxx)", //
        "{a,xxx,c,xxx,e,xxx,g}");
    check("ReplacePart({a, b, c, d, e, f, g}, Except(1 | 3 | 5) -> xxx)", //
        "{a,xxx,c,xxx,e,xxx,xxx}");
    check("ReplacePart({a, b, c, d, e, f, g}, (1 | 3 | 5) -> xxx)", //
        "{xxx,b,xxx,d,xxx,f,g}");
    check("ReplacePart({{1, 2}, {3,4}}, {x_, x_} -> -1)", //
        "{{-1,2},{3,-1}}");


    // check("ReplacePart({a, b, c}, 1 -> t)", //
    // "{t,b,c}");
    // check("ReplacePart({{a, b}, {c, d}}, {2, 1} -> t)", //
    // "{{a,b},{t,d}}");
    // check("ReplacePart({{a, b}, {c, d}}, {{2, 1} -> t, {1, 1} -> t})", //
    // "{{t,b},{t,d}}");
    // check("ReplacePart({a, b, c}, {{1}, {2}} -> t)", //
    // "{t,t,c}");
    //
    // check("n = 1", "1");
    // check("ReplacePart({a, b, c, d}, {{1}, {3}} :> n++)", //
    // "{1,b,2,d}");

    // check("ReplacePart({a, b, c}, 4 -> t)", //
    // "{a,b,c}");
    // check("ReplacePart({a, b, c}, 0 -> Times)", //
    // "a*b*c");
    // check("ReplacePart({a, b, c}, -1 -> t)", //
    // "{a,b,t}");
    //
    // check("ReplacePart({a,b,c^n}, x+y, {{3, 2}, 2})", //
    // "{a,x+y,c^(x+y)}");

    // >>>
    check("ReplacePart({a, b, c, d, e}, {3 -> u, _ -> x})", //
        "{x,x,u,x,x}");


    // check("ReplacePart({a, b, c, d, e}, 3 -> xxx)", //
    // "{a,b,xxx,d,e}");
    // check("ReplacePart({a, b, c, d, e}, {2 -> xx, 5 -> yy})", //
    // "{a,xx,c,d,yy}");
    // check("ReplacePart({{a, b}, {c, d}}, {2, 1} -> xx)", //
    // "{{a,b},{xx,d}}");
    check("ReplacePart({{a, b}, {c, d}}, {i_, i_} -> xx)", //
        "{{xx,b},{c,xx}}");
    // check("ReplacePart({a,b,c^n}, {{3, 2} -> x + y, 2 -> b^100})", //
    // "{a,b^100,c^(x+y)}");
    // check("ReplacePart(3 -> xxx)[{a, b, c, d, e}]", //
    // "{a,b,xxx,d,e}");

    // check("ReplacePart({a, b, c, d, e, f, g}, -3 -> xxx)", //
    // "{a,b,c,d,xxx,f,g}");
    // check("ReplacePart({a, b, c, d, e, f, g}, {{1}, {3}, {5}} -> xxx)", //
    // "{xxx,b,xxx,d,xxx,f,g}");
    check("ReplacePart({a, b, c, d, e, f, g}, (1 | 3 | 5) -> xxx)", //
        "{xxx,b,xxx,d,xxx,f,g}");
    check("ReplacePart({a, b, c, d, e, f, g}, Except[1 | 3 | 5] -> xxx)", //
        "{a,xxx,c,xxx,e,xxx,xxx}");
    check("ReplacePart({a, b, c, d, e, f, g}, _?EvenQ -> xxx)", //
        "{a,xxx,c,xxx,e,xxx,g}");
    //
    check("ReplacePart({{a, b, c}, {d, e}, {f}}, {1, _} -> xx)", //
        "{{xx,xx,xx},{d,e},{f}}");
    // github #412
    check("ReplacePart({{a, b, c}, {d, e}, {f}}, {_, -1} -> xx)", //
        "{{a,b,xx},{d,xx},{xx}}");


    check("ReplacePart({<|\"x\" -> 1, \"y\" -> 2|>, <|\"x\" -> 3, \"y\" -> 4|>}, {_, \"x\"} -> f)", //
        "{<|x->f,y->2|>,<|x->f,y->4|>}");


    //
    // check("ReplacePart({a, b, c, d}, 5 -> x)", //
    // "{a,b,c,d}");
    // check("ReplacePart(ReplacePart(a + b + c, 1 -> x), 3 -> y)", //
    // "b+c+y");

    check("ReplacePart(h(a, b), {} -> x)", //
        "h(a,b)");
    // check("ReplacePart(h(a, b), {{}} -> x)", //
    // "x");
    // check("ReplacePart(<|\"x\" -> 1, \"y\" -> 2|>, {0} -> List)", //
    // "{1,2}");
    // check("Normal(<|\"x\" -> 1, \"y\" -> 2|>)", //
    // "{x->1,y->2}");

  }

  @Test
  public void testReplacePartIntegerPositions() {
    check("ReplacePart({{a, b}, {c, d}}, {{2, 1} -> t, {1, 1} -> t})", //
        "{{t,b},{t,d}}");

    // github #135
    check("ReplacePart(f(x, y), 0 -> g, Heads -> False)", //
        "f(x,y)");
    check("ReplacePart(f(x, y), 0 -> g )", //
        "g(x,y)");
    // check("ReplacePart(f(x, y), _ -> g)", //
    // "f(g,g)");
    // check("ReplacePart(f(x, y), _ -> g, Heads -> True)", //
    // "g(g,g)");

    check("ReplacePart(<|\"x\" -> 1, \"y\" -> 2|>, {0} -> f)", //
        "f(1,2)");
    check("ReplacePart({<|\"x\" -> 1, \"y\" -> 2|>}, {1, 1} -> g)", //
        "{<|x->g,y->2|>}");

    check("ReplacePart({a, b, c}, 1 -> t)", //
        "{t,b,c}");
    check("ReplacePart({{a, b}, {c, d}}, {2, 1} -> t)", //
        "{{a,b},{t,d}}");
    check("ReplacePart({{a, b}, {c, d}}, {{2, 1} -> t, {1, 1} -> t})", //
        "{{t,b},{t,d}}");
    check("ReplacePart({a, b, c}, {{1}, {2}} -> t)", //
        "{t,t,c}");

    check("n = 1", "1");
    check("ReplacePart({a, b, c, d}, {{1}, {3}} :> n++)", //
        "{1,b,2,d}");

    check("ReplacePart({a, b, c}, 4 -> t)", //
        "{a,b,c}");
    check("ReplacePart({a, b, c}, 0 -> Times)", //
        "a*b*c");
    check("ReplacePart({a, b, c}, -1 -> t)", //
        "{a,b,t}");

    check("ReplacePart({a,b,c^n}, x+y, {{3, 2}, 2})", //
        "{a,x+y,c^(x+y)}");

    check("ReplacePart({a, b, c, d, e}, 3 -> xxx)", //
        "{a,b,xxx,d,e}");
    check("ReplacePart({a, b, c, d, e}, {2 -> xx, 5 -> yy})", //
        "{a,xx,c,d,yy}");
    check("ReplacePart({{a, b}, {c, d}}, {2, 1} -> xx)", //
        "{{a,b},{xx,d}}");
    // check("ReplacePart({{a, b}, {c, d}}, {i_, i_} -> xx)", //
    // "{{xx,b},{c,xx}}");
    check("ReplacePart({a,b,c^n}, {{3, 2} -> x + y, 2 -> b^100})", //
        "{a,b^100,c^(x+y)}");
    check("ReplacePart(3 -> xxx)[{a, b, c, d, e}]", //
        "{a,b,xxx,d,e}");

    check("ReplacePart({a, b, c, d, e, f, g}, -3 -> xxx)", //
        "{a,b,c,d,xxx,f,g}");
    check("ReplacePart({a, b, c, d, e, f, g}, {{1}, {3}, {5}} -> xxx)", //
        "{xxx,b,xxx,d,xxx,f,g}");
    // check("ReplacePart({a, b, c, d, e, f, g}, (1 | 3 | 5) -> xxx)", //
    // "{xxx,b,xxx,d,xxx,f,g}");
    // check("ReplacePart({a, b, c, d, e, f, g}, Except[1 | 3 | 5] -> xxx)", //
    // "{a,xxx,c,xxx,e,xxx,xxx}");
    // check("ReplacePart({a, b, c, d, e, f, g}, _?EvenQ -> xxx)", //
    // "{a,xxx,c,xxx,e,xxx,g}");
    // //
    // check("ReplacePart({{a, b, c}, {d, e}, {f}}, {1, _} -> xx)", //
    // "{{xx,xx,xx},{d,e},{f}}");
    // // github #412
    // check("ReplacePart({{a, b, c}, {d, e}, {f}}, {_, -1} -> xx)", //
    // "{{a,b,xx},{d,xx},{xx}}");
    //
    //
    // check("ReplacePart({<|\"x\" -> 1, \"y\" -> 2|>, <|\"x\" -> 3, \"y\" -> 4|>}, {_, \"x\"} ->
    // f)", //
    // "{<|x->f,y->2|>,<|x->f,y->4|>}");


    //
    check("ReplacePart({a, b, c, d}, 5 -> x)", //
        "{a,b,c,d}");
    check("ReplacePart(ReplacePart(a + b + c, 1 -> x), 3 -> y)", //
        "b+c+y");

    check("ReplacePart(h(a, b), {} -> x)", //
        "h(a,b)");
    check("ReplacePart(h(a, b), {{}} -> x)", //
        "x");
    check("ReplacePart(<|\"x\" -> 1, \"y\" -> 2|>, {0} -> List)", //
        "{1,2}");
    check("Normal(<|\"x\" -> 1, \"y\" -> 2|>)", //
        "{x->1,y->2}");

  }

  @Test
  public void testReplaceTransformations() {
    check("f(f(f(1))) //. f(f(x_)) :> g(g(x))", //
        "g(g(f(1)))");

    check("x + y /. x -> 3", //
        "3+y");
    check("x + y /. {x -> a, y -> b}", //
        "a+b");
    check("x + y /. {{x -> 1, y -> 2}, {x -> 4, y -> 2}}", //
        "{3,6}");
    check("Solve(x^3 - 5*x^2 + 2*x + 8 == 0, x)", //
        "{{x->-1},{x->2},{x->4}}");
    check("x^2 + 6 /. {{x->-1},{x->2},{x->4}}", //
        "{7,10,22}");
    check("{x^2, x^3, x^4} /. {x^3 -> u, x^n_ -> p(n)}", //
        "{p(2),u,p(4)}");
    check("h(x + h(y)) /. h(u_) -> u^2", //
        "(x+h(y))^2");
    check("{x^2, y^3} /. {x -> y, y -> x}", //
        "{y^2,x^3}");
    check("x^2 /. x -> (1 + y) /. y -> b", //
        "(1+b)^2");

    check("x^2 + y^6 /. {x -> 2 + a, a -> 3}", //
        "(2+a)^2+y^6");
    check("x^2 + y^6 //. {x -> 2 + a, a -> 3}", //
        "25+y^6");
    check("mylog(a*b*c*d) /. mylog(x_*y_) -> mylog(x) + mylog(y)", //
        "mylog(a)+mylog(b*c*d)");
    check("mylog(a*b*c*d) //. mylog(x_*y_) -> mylog(x) + mylog(y)", //
        "mylog(a)+mylog(b)+mylog(c)+mylog(d)");

    // check("ReplaceList({a, b, c, d}, {x__, y__} -> g({x}, {y}))", "");
    // check("", "");
    // check("", "");
    // check("", "");
  }

  @Test
  public void testExpandedExpressionPattern() {
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      check("f(c_*x_+y_,x_):= {c,x,y}", //
          "");
      check("expanded=ExpandAll((a+b+2*c+x+y+3*z)^24);", //
          "");
      // best time 29.28 s
      check("res=f(expanded,x);Length(res[[3]])", //
          "118754");
    }
  }

  @Test
  public void testPowerPattern() {
    check("foo(1/x_) := {x}", //
        "");
    check("foo(1/a)", //
        "{a}");
  }

  @Override
  public void setUp() {
    super.setUp();
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    }
  }

}
