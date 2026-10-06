package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>TagSet</code>, <code>TagSetDelayed</code>, <code>UpSetDelayed</code> and
 * <code>UpValues</code>: where a rule with a tag is stored, and how it is used.
 */
public class TagSetUpValuesTest extends ExprEvaluatorTestCase {

  @Test
  public void testTagIsTheHead() {
    // the tag is the head of the left-hand-side: a down-value. The rule was lost
    check("fa /: fa(x_) := x^2; {fa(4), DownValues(fa), UpValues(fa)}", //
        "{16,{HoldPattern(fa(x_)):>x^2},{}}");
    // the tag is the left-hand-side: an own-value
    check("xa /: xa = 7; {xa, OwnValues(xa)}", //
        "{7,{HoldPattern(xa):>7}}");
    check("qa /: qa(x_) /; x > 3 := big(x); {qa(5), qa(1)}", //
        "{big(5),qa(1)}");
  }

  @Test
  public void testConditionOnTheRightHandSide() {
    // the condition was returned with the value: yes(4)/;4>0
    check("fb(gb(x_)) ^:= yes(x) /; x > 0; {fb(gb(4)), fb(gb(-3)), UpValues(gb)}", //
        "{yes(4),fb(gb(-3)),{HoldPattern(fb(gb(x_))):>yes(x)/;x>0}}");
    check("gc /: fc(gc(x_)) := pos(x) /; x > 0; {fc(gc(2)), fc(gc(-2))}", //
        "{pos(2),fc(gc(-2))}");
  }

  @Test
  public void testUpValues() {
    check("xd /: xd + y_ /; y > -2 := fpos(y); xd /: xd + y_ /; y < 2 := gpos(y); xd + 1", //
        "fpos(1)");
    // the list which is assigned replaces the up-values, in its order
    check("UpValues(xd) = Reverse(UpValues(xd)); xd + 1", //
        "gpos(1)");
    check("md /: md(a_, p_) + md(b_, p_) := md(Mod(a + b, p), p); md /: i_Integer*md(a_, p_) := md(Mod(i*a, p), p); md(2, 5) + 3*md(3, 5) - md(1, 5)", //
        "md(0,5)");
    // by the name of the symbol
    check("UpValues(\"md\")", //
        "{HoldPattern(md(a_,p_)+md(b_,p_)):>md(Mod(a+b,p),p),HoldPattern(md(a_,p_)*i_Integer):>md(Mod(i*a,p),p)}");
    // an up-value is printed with its tag: lhs ^:= rhs would attach it to every symbol
    check("Definition(md)", //
        "md /: md(a_,p_) + md(b_,p_):=md(Mod(a + b,p),p)\n\nmd /: md(a_,p_)*i_Integer:=md(Mod(i*a,p),p)");
    check("areaD(squareD) ^= s^2; Definition(squareD)", //
        "aread(squared)^=s^2");
  }

  @Test
  public void testTagNotFoundAndUnset() {
    // the tag does not occur: a message, and the value of the assignment
    check("zze /: aae(bbe) = 1", //
        "1");
    check("UpValues(zze)", //
        "{}");
    check("he /: fe(he(x_)) := fhe(x); {fe(he(5)), UpValues(he)}", //
        "{fhe(5),{HoldPattern(fe(he(x_))):>fhe(x)}}");
    check("he /: fe(he(x_)) =.; {fe(he(5)), UpValues(he)}", //
        "{fe(he(5)),{}}");
  }

  @Test
  public void testReviewFindings() {
    // the right-hand-side is evaluated once
    check("nq = 0; fq /: fq(1) = (nq++; nq); {nq, fq(1)}", //
        "{1,1}");
    // the tag is a part of the definition: the rule belongs to gq only
    check("gq /: fw(gq(x_), hq(y_)) := 1; Definition(gq)", //
        "gq /: fw(gq(x_),hq(y_)):=1");
    check("{UpValues(gq), UpValues(hq)}", //
        "{{HoldPattern(fw(gq(x_),hq(y_))):>1},{}}");
    // a list with a rule which cannot be stored changes nothing
    check("UpValues(gq) = {fw(gq(x_)) :> 1, 5 :> 2}", //
        "UpValues(gq)={fw(gq(x_)):>1,5:>2}");
    check("UpValues(gq)", //
        "{HoldPattern(fw(gq(x_),hq(y_))):>1}");
    // no name of a symbol
    check("UpValues(\"f[\")", //
        "UpValues(f[)");
  }
}
