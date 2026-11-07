package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * A list of numbers or strings which was evaluated once isn't walked again when a variable holding
 * it is read, when it stands in the body of a pure function or of a <code>Module</code>, and a
 * recursion which runs out of Java stack is reported like an exceeded <code>$RecursionLimit</code>.
 */
public class LargeListReuseTest extends ExprEvaluatorTestCase {

  @Test
  public void testListChangedAfterItWasRead() {
    // the list reads as numbers first, then holds a symbol which gets a value
    check("lq1=Range(5); lq1; lq1[[2]]=y1; y1=7; lq1", //
        "{1,7,3,4,5}");
    check("lq2=Range(4); lq2; AppendTo(lq2,y2); y2=8; lq2", //
        "{1,2,3,4,8}");
    check("lq3=Range(3); Total(lq3); lq3[[1]]=2.5; lq3", //
        "{2.5,2,3}");
    check("lq4={\"u\",\"v\"}; lq4; lq4[[2]]=y4; y4=\"w\"; lq4", //
        "{u,w}");
    check("lq5=Range(3); lq5; y5=1; lq5=lq5+y5; lq5", //
        "{2,3,4}");
  }

  @Test
  public void testPureFunctionWithAListInItsBody() {
    check("With({l=Range(4)}, pf1={l,#}&); pf1(9)", //
        "{{1,2,3,4},9}");
    check("With({l=Range(4)}, Map({l,#}&, {7,8}))", //
        "{{{1,2,3,4},7},{{1,2,3,4},8}}");
    check("pf2={{1,2},{#,3}}&; pf2(5)", //
        "{{1,2},{5,3}}");
    check("lf=Range(3); lf; pf3={lf,#}&; pf3(6)", //
        "{{1,2,3},6}");
    // a head with a slot is filled although the arguments are numbers
    check("pf4=#(1,2,3)&; pf4(gh)", //
        "gh(1,2,3)");
  }

  @Test
  public void testModuleWithAListArgument() {
    check("mw(x_):=Module({i=0}, While(i<3, i++; x[[1]]); x[[i]]); lm=Range(10); lm; mw(lm)", //
        "3");
    check("mv(x_):=Module({i=5}, {i,Total(x)}); lv=Range(10); lv; mv(lv)", //
        "{5,55}");
    // a local name as the head of a list of numbers is renamed
    check("Module({fm}, fm(1,2,3,4,5,6)=7; fm(1,2,3,4,5,6))", //
        "7");
    check("pf5=Module({e=1}, #+e)&; pf5(e+1)", //
        "2+e");
  }

  @Test
  public void testStackOverflowIsARecursionLimit() {
    check("ClearAll(fso); fso(n_) := 1 + fso(n + 1)", //
        "");
    // the Java stack runs out first: the input is held, as for an exceeded $RecursionLimit
    check("Block({$RecursionLimit = Infinity}, fso(0))", //
        "Hold(Block({$RecursionLimit=Infinity},fso(0)))");
    // the evaluation goes on after the overflow
    check("1+1", //
        "2");
    check("TimeConstrained(Block({$RecursionLimit = Infinity}, fso(0)), 30, late)", //
        "Hold(TimeConstrained(Block({$RecursionLimit=Infinity},fso(0)),30,late))");
    check("ClearAll(fsd); fsd(0) = 0; fsd(n_) := 1 + fsd(n - 1); fsd(200)", //
        "200");
    check("ClearAll(fdo); fdo(0) = 0; fdo(n_) := 1 + fdo(n - 1); "
        + "Block({$RecursionLimit = Infinity}, fdo(200))", //
        "200");
  }
}
