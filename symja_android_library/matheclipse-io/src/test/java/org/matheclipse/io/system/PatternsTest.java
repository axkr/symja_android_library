package org.matheclipse.io.system;

import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.ast;
import static org.matheclipse.core.expression.F.f;
import static org.matheclipse.core.expression.F.x;
import static org.matheclipse.core.expression.F.x_;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.core.patternmatching.RulesData;

/**
 * Tests for the Java port of the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi - rule-based
 * integrator</a>.
 */
public class PatternsTest extends AbstractTestCase {
  public PatternsTest(String name) {
    super(name);
  }

  @Override
  public void check(String evalString, String expectedResult) {
    check(fScriptEngine, evalString, expectedResult, -1);
  }

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

  public void testComplicatedPatternRule() {
    IExpr expr = F.Integrate(F.unaryAST1(F.unaryAST1(F.Derivative(F.n_), F.f_), F.x_), F.x_Symbol);
    assertEquals("Integrate[Derivative[n_][f_][x_],x_Symbol]", expr.toString());
    boolean isComplicated = RulesData.isComplicatedPatternRule(expr);
    assertTrue(isComplicated);
  }

  public void testDocOptions() {
    // TODO doc/Options
    check(
        "Options(f) = {n -> 2}", //
        "{n->2}");
    check(
        "Options(f)", //
        "{n->2}");
    check(
        "f(x_, OptionsPattern(f)) := x ^ OptionValue(n)", //
        "");
    check(
        "f(x)", //
        "x^2");
    check(
        "f(x, n -> 3)", //
        "x^3");
  }

  public void testOptions() {
    // TODO define options for Plot and other built-ins
    // check("Options(Plot)", //
    // "");
    check(
        "Options(f) = {a -> 1, b -> 2};", //
        "");
    check(
        "Options(f)", //
        "{a->1,b->2}");
    check(
        "f(x_, OptionsPattern()) := {x, OptionValue(a)}", //
        "");
    check(
        "f(7, a -> uuu)", //
        "{7,uuu}");
    check(
        "Options(Plus)", //
        "{}");
    check(
        "Options(Factor)", //
        "{Extension->None,GaussianIntegers->False,Modulus->0}");
  }

  public void testOptional() {
    check(
        "f(a)/.f(a,b_.)->{a,b}", //
        "f(a)");
    check(
        "f(a,b)/.f(a,b_.)->{a,b}", //
        "{a,b}");
    check(
        "f(a)/.f(a,b_:c)->{a,b}", //
        "{a,c}");
    check(
        "f(a,b)/.f(a,b_:c)->{a,b}", //
        "{a,b}");

    check(
        "f(x,y)/.f(a__,b_.)->{{a},{b}}", //
        "{{x},{y}}");
    check(
        "f(x,y)/.f(a___,b_.)->{{a},{b}}", //
        "{{x},{y}}");
    check(
        "f(x,y)/.f(a__,b_:c)->{{a},{b}}", //
        "{{x},{y}}");
    check(
        "f(x,y)/.f(a___,b_:c)->{{a},{b}}", //
        "{{x},{y}}");

    check(
        "f(x,y)/.f(a___,b_:c,d_:e)->{{a},{b},{d}}", //
        "{{},{x},{y}}");
    check(
        "f(x)/.f(a_,b_:y,c_:z)->{{a},{b},{c}}", //
        "{{x},{y},{z}}");
    check(
        "f( )/.f(a_,b_:y,c_:z)->{{a},{b},{c}}", //
        "f()");
    check(
        "f(x,i,j)/.f(a_,b_:y,c_:z)->{{a},{b},{c}}", //
        "{{x},{i},{j}}");
    check(
        "a /.f(a,c_.)->{{c}}", //
        "a");

    check(
        "a /. a + c_.->{{c}}", //
        "{{0}}");

    check(
        "MatchQ(a,f(a,c_.))", //
        "False");
    check(
        "MatchQ(a,a+c_.)", //
        "True");

    check(
        "a/.a+c_.+d_.->{{c},{d}}", //
        "{{0},{0}}");
    check(
        "Cos(x)/.(_+c_.+d_.)->{{c},{d}}", //
        "{{0},{0}}");

    check(
        "5*a/.Optional(c1_?NumberQ)*a_->{{c1},{a}}", //
        "{{5},{a}}");
    check(
        "a/.Optional(c1_?NumberQ)*a_->{{c1},{a}}", //
        "{{1},{a}}");

    check(
        "MatchQ(f(a,b),f(c1__?NumberQ))", //
        "False");
    check(
        "MatchQ(f(1,2),f(c1__?NumberQ))", //
        "True");
    check(
        "MatchQ(f(1,2),f(Optional(c1__?NumberQ)))", //
        "False");
    check(
        "MatchQ(f(1),f(Optional(c1__?NumberQ)))", //
        "True");

    check(
        "ReplaceList({a,b,c},{a_:5,b__}->{{a},{b}})", //
        "{{{a},{b,c}},{{5},{a,b,c}}}");
    // TODO add missing combinations
    check(
        "ReplaceList({a,b,c},{a_:5,b_:6,c___}->{{a},{b},{c}})", //
        "{{{a},{b},{c}},{{5},{6},{a,b,c}}}");

    check(
        "MatchQ(-x,p_.)", //
        "True");
    check(
        "MatchQ(-x*a,p_.*a)", //
        "True");
    check(
        "MatchQ(__, Optional(1)*a_)", //
        "True");
    check(
        "MatchQ(x^x, x^Optional(exp_))", //
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

  public void testDocOptionValue() {
    // TODO doc/OptionValue
    check(
        "f(a->3) /. f(OptionsPattern({})) -> {OptionValue(a)}", //
        "{3}");
    check(
        "f(a->3) /. f(OptionsPattern({})) -> {OptionValue(b)}", //
        "{b}");
    check(
        "f(a->3) /. f(OptionsPattern({})) -> {OptionValue(a+b)}", //
        "{a+b}");
    check(
        "f(a->5) /. f(OptionsPattern({})) -> {OptionValue(Symbol(\"a\"))}", //
        "{5}");
  }

  public void testOptionValue() {
    // TODO
    check(
        "OptionValue({foo`a -> 1}, bar`a)", //
        "1");

    check(
        "Options(tt) = {\"aa\" -> a1, \"bb\" -> b1}", //
        "{aa->a1,bb->b1}");
    check(
        "tt(x_, opts : OptionsPattern()) := {x, opts}", //
        "");
    check(
        "tt(test, \"aa\"->r1)", //
        "{test,aa->r1}");
    check(
        "tt(test,  aa ->r1)", //
        "{test,aa->r1}");
    check(
        "tt(test, \"aa\"->r1, \"a2\"->r2 )", //
        "{test,aa->r1,a2->r2}");
    check(
        "ClearAll(tt)", //
        "");
    check(
        "Options(tt) = {\"aa\" -> a1, \"bb\" -> b1}", //
        "{aa->a1,bb->b1}");
    check(
        "tt(x_,  OptionsPattern()) := {x, OptionValue(aa)}", //
        "");
    check(
        "tt(test,  \"aa\" ->r1)", //
        "{test,r1}");
    check(
        "tt(test,  aa ->r1)", //
        "{test,r1}");
    check(
        "tt(test)", //
        "{test,a1}");

    check(
        "Options(f) = {a -> 1}; "
            + "Options(g) = {a -> 2};"
            + "f(g(OptionsPattern()), OptionsPattern()) := "
            + "{OptionValue(a), OptionValue(f,a), OptionValue(g,a)}", //
        "");

    check(
        "f(g(a->10),a->11)", //
        "{11,11,10}");
    check(
        "f(g( ),a->11)", //
        "{11,11,2}");
    check(
        "f(g())", //
        "{1,1,2}");

    check(
        "Options(f) = {a -> a0, b -> b0}", //
        "{a->a0,b->b0}");
    check(
        "f(x_, OptionsPattern()) := {x, OptionValue(a)}", //
        "");
    check(
        "f(7, a -> test)", //
        "{7,test}");
    check(
        "f(7)", //
        "{7,a0}");

    check(
        "Options(f) = {a -> x, b -> y}", //
        "{a->x,b->y}");
    check(
        "OptionValue(f, {b -> 5}, {a, b})", //
        "{x,5}");
    check(
        "{a, b} /. {b -> 5} /. Options(f)", //
        "{x,5}");
    check(
        "OptionValue(f, {a -> 7, b -> a}, {a, b})", //
        "{7,a}");
    check(
        "{a, b} /. {a -> 7, b -> a} /. Options(f)", //
        "{7,x}");

    check(
        "OptionValue({a -> 1}, {a, b})", //
        "{1,b}");
    check(
        "OptionValue({opt -> 1}, opt)", //
        "1");
    check(
        "OptionValue({\"opt\" -> 1}, \"opt\")", //
        "1");
    check(
        "OptionValue({opt -> 1}, \"opt\")", //
        "1");
    check(
        "OptionValue({\"opt\" -> 1}, opt)", //
        "1");

    check(
        "ClearAll(f,g)", //
        "");
    check(
        "Options(f) = {a -> 1}; "
            + "Options(g) = {a -> 2};"
            + "f(g(OptionsPattern())) := "
            + "{OptionValue(a), OptionValue(f,a), OptionValue(g,a)}", //
        "");
    check(
        "f(g())", //
        "{OptionValue(a),1,2}");

    check(
        "Options(f3):={a->12}", //
        "");
    check(
        "f3(x_,OptionsPattern()):=x^OptionValue(a)", //
        "");
    check(
        "f3(y)", //
        "y^12");

    check(
        "Options(f4):={a->12}", //
        "");
    check(
        "f4(x_,OptionsPattern({a:>4})):=x^OptionValue(a)", //
        "");
    check(
        "f4(y)", //
        "y^4");
  }

  public void testOptionsPattern() {
    check(
        "Options(f)={a->a0, b->b0};", //
        "");
    check(
        "f(x_,OptionsPattern()):={x, OptionValue(a)}", //
        "");
    check(
        "f(11, a->iam)", //
        "{11,iam}");
    check(
        "f(22)", //
        "{22,a0}");
    check(
        "f(x_,OptionsPattern({a -> a0, b -> b0})):={x, OptionValue(a)}", //
        "");
    check(
        "f(11)", //
        "{11,a0}");
    check(
        "opts:OptionsPattern() // FullForm", //
        "Pattern(opts,OptionsPattern())");
    check(
        "OptionsPattern() // FullForm", //
        "OptionsPattern()");
  }
}
