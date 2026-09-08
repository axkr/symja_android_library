package org.matheclipse.core.patternmatching;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Regression tests for defects in the pattern matching engine which were found and fixed in one
 * pass; every test names the class which contained the defect.
 */
public class PatternMatchingBugsTest extends ExprEvaluatorTestCase {

  /** {@link RulesData}: a <code>TreeMap</code> keyed by <code>compareTo()</code> mixed up 1 and 1.0 */
  @Test
  public void testIntegerAndRealKeysAreDifferentRules() {
    check("fir(1)=a", "a");
    check("fir(1.0)", "fir(1.0)");
    check("fir(1)", "a");
    check("fir(2.0)=b", "b");
    check("fir(2)", "fir(2)");
    check("fir(2.0)", "b");
    // the printed definition keeps a stable, sorted order
    check("dord(3)=c; dord(1)=a; dord(2)=b; DownValues(dord)",
        "{HoldPattern(dord(1)):>a,HoldPattern(dord(2)):>b,HoldPattern(dord(3)):>c}");
  }

  /** {@link RulesData#putUpRule}: an up-rule with the same left-hand-side was appended, not replaced */
  @Test
  public void testUpRuleWithSameLHSIsReplaced() {
    check("gup/:hup(gup,x_)=1", "1");
    check("gup/:hup(gup,x_)=2", "2");
    check("UpValues(gup)", "{HoldPattern(hup(gup,x_)):>2}");
    check("hup(gup,3)", "2");
  }

  /** {@link PatternMatcher#matchFlat}: a Flat head with a BlankSequence ignored the condition */
  @Test
  public void testFlatBlankSequenceCondition() {
    check("SetAttributes(flc,Flat)", "");
    check("flc(x__)/;False := 1", "");
    check("flc(a,b)", "flc(a,b)");
    check("flc(a)", "flc(a)");
    check("flc(x__) := 2 /; Length({x})>1", "");
    check("flc(a,b)", "2");
    check("flc(a)", "flc(a)");
    // controls without the Flat attribute
    check("fnc(x__)/;False := 1", "");
    check("fnc(a,b)", "fnc(a,b)");
  }

  /** {@link PatternMatcherList}: the substituted <code>Condition()</code> was collected unevaluated */
  @Test
  public void testReplaceListCondition() {
    check("ReplaceList(f(3), f(x_) :> x /; x>5)", "{}");
    check("ReplaceList(f(3), f(x_) :> x /; x>1)", "{3}");
    check("ReplaceList(f(3), f(x_) :> Module({y=x}, y /; y>1))", "{3}");
    check("ReplaceList({1,2,3}, {___, x_, ___} :> x /; x>1)", "{2,3}");
    // the shared matcher must not carry results over to the next expression
    check("ReplaceList({1,2,3}, {___, x_, ___} :> x)", "{1,2,3}");
    check("ReplaceList({4,5}, {___, x_, ___} :> x)", "{4,5}");
  }

  /** {@link IPatternMap.PatternMap#substitutePatterns} and {@link PatternMatcher#matchASTSubset} */
  @Test
  public void testSubsetCasesWithMoreThanSixPatterns() {
    check("SubsetCases({1,2,3,4,5,6,7,8}, {a_,b_,c_,d_,e_,f_,h_})", "{{1,2,3,4,5,6,7}}");
    check("SubsetCases({1,2,3,4,5,6,7,8,9,10,11,12,13,14}, {a_,b_,c_,d_,e_,f_,h_} :> a+b+c+d+e+f+h)",
        "{28,77}");
    check("SubsetCases({1,2,3,4}, {a_,b_})", "{{1,2},{3,4}}");
  }

  /**
   * {@link PatternMatcher#matchAST}: the right-hand-side condition was evaluated although the
   * pattern test of the trailing sequence had already failed
   */
  @Test
  public void testPatternTestBeforeRHSCondition() {
    check("cntpt=0", "0");
    check("ptst(x__?((cntpt++; False)&)) := 1 /; (cntpt+=100; True)", "");
    check("ptst(1,2)", "ptst(1,2)");
    check("cntpt", "1");
    check("ptok(x__?(True&)) := {x} /; (cntpt+=100; True)", "");
    check("ptok(1,2)", "{1,2}");
    check("cntpt", "101");
  }

  /** {@link RulesData#removeRule}: up-rules could not be removed */
  @Test
  public void testTagUnset() {
    check("utu/:ptu(utu,x_)=7", "7");
    check("ptu(utu,1)", "7");
    check("utu/:ptu(utu,x_)=.", "");
    check("UpValues(utu)", "{}");
    check("ptu(utu,1)", "ptu(utu,1)");
    check("utu/:ptu(utu,x_)=8", "8");
    check("TagUnset(utu, ptu(utu,x_))", "");
    check("UpValues(utu)", "{}");
    // an equal (pattern free) up-rule
    check("utu/:qtu(utu)=9", "9");
    check("qtu(utu)", "9");
    check("utu/:qtu(utu)=.", "");
    check("qtu(utu)", "qtu(utu)");
    // the tag as head - TagSet stores this as an up-rule of the tag
    check("utu/:utu(x_)=10", "10");
    check("utu/:utu(x_)=.", "");
    check("UpValues(utu)", "{}");
    // nothing to remove
    check("utu/:ptu(utu,y_)=.", "$Failed");
  }

  /** {@link PatternMatcher#equivalent}: association values were compared with rules */
  @Test
  public void testAssociationRuleIsReplaced() {
    check("asr(<|a->x_|>) := 1", "");
    check("asr(<|a->y_|>) := 2", "");
    check("DownValues(asr)", "{HoldPattern(asr(Association(a->y_))):>2}");
    check("asr(<|a->5|>)", "2");
  }

  /** a rule defined while the rules of the same symbol are scanned must not throw */
  @Test
  public void testRuleInsertedDuringDispatch() {
    // the condition defines a second rule for gdd while the rules of gdd are scanned, and then
    // fails, so the scan continues on the modified rule list
    check("gdd(x_) := x /; (gdd(y_Integer) := 2*y; False)", "");
    check("gdd(a)", "gdd(a)");
    check("gdd(5)", "10");
  }
}
