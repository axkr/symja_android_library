package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

public class PatternMatchingTestCase {

  protected static boolean DEBUG = true;

  private Parser fParser;

  protected EvalUtilities util;

  public void check(EvalEngine engine, boolean evaluate, IAST ast, String strResult) {
    try {

      StringWriter buf = new StringWriter();
      if (evaluate) {
        IAST inExpr = ast;
        TimeConstrainedEvaluator utility =
            new TimeConstrainedEvaluator(engine, false, Config.FOREVER);
        utility.constrainedEval(buf, inExpr);
      } else {
        if (ast != null) {
          OutputFormFactory off = OutputFormFactory.get();
          off.setIgnoreNewLine(true);
          OutputFormFactory.get().convert(buf, ast);
        }
      }

      assertEquals(buf.toString(), strResult);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
  }

  //
  public void check(EvalEngine engine, boolean evaluate, String strEval, String strResult) {
    check(engine, evaluate, strEval, strResult, "", false);
  }

  private void check(EvalEngine engine, boolean evaluate, String strEval, String strResult,
      String strException, boolean relaxedSyntax) {
    try {
      if (strEval.length() == 0 && strResult.length() == 0) {
        return;
      }
      IExpr result;
      StringWriter buf = new StringWriter();

      if (evaluate) {
        Parser parser = new Parser(relaxedSyntax);
        ASTNode node = parser.parse(strEval);
        IExpr inExpr = new AST2Expr(false, engine).convert(node);
        TimeConstrainedEvaluator utility =
            new TimeConstrainedEvaluator(engine, false, Config.FOREVER, relaxedSyntax);
        result = utility.constrainedEval(buf, inExpr);
      } else {
        Parser parser = new Parser(relaxedSyntax);
        ASTNode node = parser.parse(strEval);
        IExpr inExpr = new AST2Expr(false, engine).convert(node);
        result = util.evaluate(inExpr);
        if ((result != null) && result != S.Null) {
          OutputFormFactory off = OutputFormFactory.get(relaxedSyntax);
          off.setIgnoreNewLine(true);
          off.convert(buf, result);
        }
      }

      assertEquals(buf.toString(), strResult);
    } catch (Exception e) {
      e.printStackTrace();
      String message = e.getMessage();
      assertEquals(strException, message);
    }
  }

  public void check(IAST ast, String strResult) {
    check(EvalEngine.get(), true, ast, strResult);
  }

  public void check(ScriptEngine scriptEngine, String evalString, String expectedResult) {
    try {
      if (evalString.length() == 0 && expectedResult.length() == 0) {
        return;
      }

      String evaledResult = (String) scriptEngine.eval(evalString);

      assertEquals(evaledResult, expectedResult);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
  }

  public void check(String strEval, String strResult) {
    check(EvalEngine.get(), false, strEval, strResult, "", false);
  }

  public void check(String strEval, String strResult, String strException) {
    check(EvalEngine.get(), false, strEval, strResult, strException, false);
  }

  public void checkPattern(String patternString, String evalString, String resultString) {
    try {
      ASTNode node = fParser.parse(patternString);
      EvalEngine engine = EvalEngine.get();
      IExpr pat = new AST2Expr(false, engine).convert(node);

      node = fParser.parse(evalString);
      IExpr eval = new AST2Expr(false, engine).convert(node);
      PatternMatcher matcher = new PatternMatcher(pat);
      if (matcher.test(eval)) {
        ArrayList<IExpr> resultList = new ArrayList<IExpr>();
        matcher.getPatterns(resultList, pat);
        assertEquals(resultList.toString(), resultString);
        return;
      }
      assertEquals("", resultString);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", resultString);
    }
  }

  public void checkPriority(String patternString, int priority) {
    try {
      EvalEngine engine = EvalEngine.get();
      ASTNode node = fParser.parse(patternString);
      IExpr pat = new AST2Expr(false, engine).convert(node);

      PatternMatcher matcher = new PatternMatcher(pat);
      assertEquals(matcher.getLHSPriority(), priority);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals(0, priority);
    }
  }

  public void comparePriority(String patternString1, String patternString2, int result) {
    try {
      EvalEngine engine = EvalEngine.get();
      ASTNode node = fParser.parse(patternString1);
      IExpr pat1 = new AST2Expr(false, engine).convert(node);
      node = fParser.parse(patternString1);
      IExpr pat2 = new AST2Expr(false, engine).convert(node);

      PatternMatcher matcher1 = new PatternMatcher(pat1);
      PatternMatcher matcher2 = new PatternMatcher(pat2);
      assertEquals(matcher1.equivalentTo(matcher2), result);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals(Integer.MAX_VALUE, result);
    }
  }

  /** The JUnit setup method */
  @BeforeEach
  public void setUp() {
    try {
      // setup the evaluation engine (and bind to current thread)
      // F.initSymbols();
      EvalEngine engine = new EvalEngine(); // EvalEngine.get();
      EvalEngine.set(engine);
      engine.setSessionID("PatternMatchingTestCase");
      engine.setRecursionLimit(256);
      engine.setIterationLimit(1024 * 1024);
      util = new EvalUtilities(engine, false, false);
      // setup a parser for the math expressions
      fParser = new Parser();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testFlatOrderlessSameHeadArgument() {
    // The argument fo[a_Integer, b_Integer] of the pattern has the same head as the Flat-Orderless
    // function itself, so it can only match a multi-element segment fo[1, 2] of the arguments,
    // which the MultisetPartitionsIterator fallback of matchFlatOrderless() finds. The
    // single-argument search must not consume the match attempt (fo[x, y] does not match
    // a_Integer, b_Integer) and must not prune the fallback away.
    check("SetAttributes[fo, {Flat, Orderless}]", "");
    checkPattern("g[fo[fo[a_Integer, b_Integer], c_]]", //
        "g[fo[fo[x, y], 1, 2, w]]", //
        "[1, 2, fo[fo[x,y],w]]");
  }

  @Test
  public void testBlankAreNoTags() {
    // Message UpSetDelayed: F[_,_] does not contain a symbol to attach a rule to.
    // check("ClearAll[F, Q];F[_,_]^:=1", //
    // "$Failed");
    // check("F[_Q,_] // FullForm", //
    // "F(Blank(Q), Blank())");
    check("ClearAll[F, Q];F[_Q,_]^:=1", //
        "");
    check("UpValues[Q]", //
        "{HoldPattern[F[_Q,_]]:>1}");
  }

  // @Test
  // public void testSlotPatternMatching() {
  // checkPattern("b_.* #+c_.*#^2", "#-1*#^2", "");
  // checkPattern("b_.* #+c_.*#^2", "#+#^2", "[1, 1]");
  // checkPattern("a_. + b_.* #+c_.*#^2", "-1+#+#^2", "[-1, 1, 1]");
  // }
  @Test
  public void testHoldPattern() {
    check("HoldPattern[a] := 1", //
        "");
    check("a", //
        "1");
    check("HoldPattern[List] := 1", //
        "$Failed");
  }

  @Test
  public void testKeyValuePattern() {

    check("MatchQ[<|a -> 1, b -> 2|>, KeyValuePattern[{_, _, _}]]", //
        "False");

    check("MatchQ[<|a -> 1, b -> 2|>, KeyValuePattern[{_, _}]]", //
        "True");
    check("MatchQ[<|a -> 1|>, KeyValuePattern[{a :> 1}]]", //
        "False");

    check("MatchQ[<|a -> 1|>, KeyValuePattern[{}]]", //
        "True");
    check("MatchQ[<|a -> 1|>, KeyValuePattern[{x_ -> y_}]]", //
        "True");

    check("MatchQ[<|a -> 1, b -> 2, c -> 3|>, KeyValuePattern[b->2]]", //
        "True");
    check("MatchQ[{a -> 1, b -> 2, c -> 3}, KeyValuePattern[b -> 2]]", //
        "True");
    check("MatchQ[<|a -> 1, b -> 2|>, KeyValuePattern[{a -> _}]]", //
        "True");


    check("MatchQ[<|a -> 1, b -> 2|>, KeyValuePattern[{_ -> 2, _ -> 1}]]", //
        "True");
    check("MatchQ[<|a -> 1, b -> 2|>, <|_ -> 2, _ -> 1|>]", //
        "False");

    check("<|a -> 1, b -> 2, c -> 3|> /. KeyValuePattern[{x_ -> 1}] :> x", //
        "a");
    check("<|a -> 1, b -> 2|> /. KeyValuePattern[{x_ -> y_?EvenQ}] :> (x -> y)", //
        "b->2");
    check("<|a -> 1, b -> 2, c -> 3|> /. KeyValuePattern[{_ -> x_, _ -> y_}] :> {x, y}", //
        "{1,2}");
    check("<|a -> 1, b -> 2, c -> 3|> /.  KeyValuePattern[{a -> x_, c -> y_}] -> {x, y}", //
        "{1,3}");

    check(
        "peopleData = {\n"
            + "  <|\"Name\" -> \"Alice\", \"Age\" -> 30, \"City\" -> \"New York\"|>,\n"//
            + "  <|\"Name\" -> \"Bob\", \"Age\" -> 25, \"City\" -> \"Los Angeles\"|>,\n"//
            + "  <|\"Name\" -> \"Charlie\", \"Age\" -> 35, \"City\" -> \"Chicago\"|>,\n"//
            + "  <|\"Name\" -> \"David\", \"Age\" -> 25, \"City\" -> \"New York\"|>,\n"//
            + "  <|\"Name\" -> \"Eve\", \"Age\" -> 40, \"City\" -> \"Los Angeles\"|>\n"//
            + "};", //
        "");
    check("Cases[peopleData, KeyValuePattern[{\"Age\" -> 25}]]", //
        "{<|Name->Bob,Age->25,City->Los Angeles|>,<|Name->David,Age->25,City->New York|>}");

  }

  @Test
  public void testMathicsPMWithCondition() {
    // see mathics-core #1233: a rule with a condition is not known to be more general than a
    // later rule with a more specific left-hand side, so the two keep the order of their
    // definitions
    check("Bug[n_Integer,_] :={\"Negative\", n} /; (n<0)", //
        "");
    check("Bug[-5,3]", //
        "{Negative,-5}");

    check("Bug[n_Integer,maxpart_Integer] :={\"Integer\", n}", //
        "");
    check("Bug[-5,3]", //
        "{Negative,-5}");
    check("Bug[5,3]", //
        "{Integer,5}");
  }

  @Test
  public void testNamedPattern() {
    // Pattern[expr,f[x_,y_]]:=g[HoldForm[expr]]
    check("expr:f[x_,y_]:=g[HoldForm[expr]]", //
        "");
    check("f[1,2]", //
        "g[f[1,2]]");
  }

  @Test
  public void testNestedCondition() {
    // delayed rule evaluates Condition
    check("f[x]/.(f[u_]:>u^2/; u>3/; u>2)", //
        "f[x]");
    check("f[3]/.(f[u_]:>u^2/; u>3/; u>2)", //
        "f[3]");
    check("f[4]/.(f[u_]:>u^2/; u>3/; u>2)", //
        "16");
    check("f[x]/.(f[u_]->u^2/; u>3/; u>2)", //
        "x^2/;x>3/;x>2");
    // Condition is HoldAll, but it evaluates its first argument
    check("f[3]/.(f[u_]->u^2/; u>3/; u>2)", //
        "9/;3>3/;3>2");
    check("f[4]/.(f[u_]->u^2/; u>3/; u>2)", //
        "16/;4>3/;4>2");
  }

  @Test
  public void testOptionalPattern() {
    check("h[Power[a_.+f[e_.+x*f_.]*b_.,-1/2]] := {a,b,e,f}", //
        "");
    check("h[Power[f[c+Pi/2+d*x],-1/2]]", //
        "{0,1,c+Pi/2,d}");
    check("g[f[e_.+x*f_.]*b_.] := {b,e,f}", //
        "");
    check("g[f[c+Pi/2+d*x]]", //
        "{1,c+Pi/2,d}");

    check("f[e_.+x*f_.]:={e,f}", //
        "");
    check("f[c+Pi/2+d*x]", //
        "{c+Pi/2,d}");
  }

  @Test
  public void testPattern() {}

  @Test
  public void testPatternDefinition() {
    check("f[x_]:=g[x]", //
        "");
    check("Definition[f]", //
        "f[x_]:=g[x]");
    check("h[f[x_]]:= x^2", //
        "");
    check("Definition[h]", //
        "h[g[x_]]:=x^2");
  }


  /**
   * A sequence pattern nested in another head does not make a rule as general as a blank: it still
   * matches one argument with that head, whichever of the two definitions came first.
   */
  @Test
  public void testNestedSequencePatternPriority() {
    check("f[g[n_,i___]] := {n,i}; f[a_] := fb; {f[g[1,2,3]], f[g[1]], f[h[1]]}", //
        "{{1,2,3},{1},fb}");
    check("k[a_] := fb; k[g[n_,i___]] := {n,i}; {k[g[1,2,3]], k[h[1]]}", //
        "{{1,2,3},fb}");
    check("r[0] := True; r[x_Plus] := And @@ (r /@ List @@ x); r[g[i___]] := EvenQ[i]; "
        + "r[_] := False; {r[g[2]], r[g[2]+g[4]], r[g[3]], r[1]}", //
        "{True,True,False,False}");
    // a sequence standing for arguments of the defined function goes after the narrower rule
    check("q[x___] := many; q[x_] := one; {q[1], q[1,2]}", //
        "{one,many}");
    check("q2[x_][y___] := many; q2[x_][y_] := one; {q2[1][2], q2[1][2,3]}", //
        "{one,many}");
  }

  /**
   * A rule with a run of arguments goes before an older rule only if that one is more general; of
   * two rules where neither is, the older one is tried first.
   */
  @Test
  public void testArgumentRunRuleOrder() {
    // neither is more general: several keys against a key list and any head
    check("ar1[r_, k__String, t : _String | _StringExpression] := 1; "
        + "ar1[r_, k : _String | {__String}, t : _Function | _Symbol | _[___]] := 2; "
        + "{ar1[x, \"u\", \"v\" ~~ ___], ar1[x, \"u\", \"v\"], ar1[x, \"u\", h[2]], "
        + "ar1[x, {\"u\", \"w\"}, h[2]]}", //
        "{1,1,2,2}");
    // rules with other numbers of arguments don't decide where the new rule goes
    check("am[a_Association, p_Association] := 0; am[p_Association][a_Association] := am[a, p]; "
        + "am[r_Association, k__String, t : _String | _StringExpression] := 1; "
        + "am[r_Association, k__String, t_Association] := 2; "
        + "am[r_Association, k : _String | {__String}, t : _Function | _Symbol | _[___]] := 3; "
        + "{am[<|\"u\" -> \"v.txt\"|>, \"u\", ___ ~~ \".txt\"], am[<|\"u\" -> 1|>, \"u\", h], "
        + "am[<|\"u\" -> <||>|>, \"u\", <||>]}", //
        "{1,3,2}");
    check("ar2[k__Integer, t_List] := 1; ar2[k_Integer, t_] := 2; {ar2[3, {}], ar2[3, x]}", //
        "{1,2}");
    check("ar3[k_Integer, t_] := 2; ar3[k__Integer, t_List] := 1; {ar3[3, {}], ar3[3, 4, {}]}", //
        "{2,1}");
    check("ar4[k__, t_Real] := 1; ar4[k_, t_] := 2; {ar4[a, 1.5], ar4[a, b]}", //
        "{1,2}");
    check("ar5[y_] := 2; ar5[y__Symbol] := 1; {ar5[a], ar5[a, b]}", //
        "{2,1}");
    // the older rule is more general in every argument: the new one goes first, but after an
    // older rule which is not more general
    check("ar6[k__Symbol, t_] := 1; ar6[k_Symbol, t_] := 2; {ar6[a, 7], ar6[a, b, 7]}", //
        "{2,1}");
    check("ar7[x___] := 0; ar7[x__Integer] := 1; ar7[x_Integer, y_] := 2; "
        + "{ar7[1, a], ar7[1, 2], ar7[a], ar7[]}", //
        "{2,1,0,0}");
    check("DownValues[ar7]", //
        "{HoldPattern[ar7[x__Integer]]:>1,HoldPattern[ar7[x_Integer,y_]]:>2,HoldPattern[ar7[x___]]:>0}");
    // a repeated name matches less than its shape: no reordering
    check("ar8[x__, x__] := 1; ar8[a_, b_] := 2; {ar8[c, c], ar8[c, d]}", //
        "{1,2}");
  }

  /**
   * In a Flat head only a pattern object takes a group of arguments. A power with an optional
   * factor and exponent matches a product on its own, but not the rest of a longer product beside
   * another pattern.
   */
  @Test
  public void testFlatGroupOnlyForPatternObjects() {
    check("ClearAll[fg1]; fg1[(k_.*p[w_])^i_.*(h_.*q[w_])^j_.] := {k, i, h, j}; "
        + "{fg1[p[v]*q[v]*u[v]], fg1[p[v]^3*q[v]*u[v]], fg1[5*p[v]*q[v]], fg1[p[v]*q[v]^2]}", //
        "{fg1[p[v]*q[v]*u[v]],fg1[p[v]^3*q[v]*u[v]],fg1[5*p[v]*q[v]],{1,1,1,2}}");
    check("ClearAll[fg2]; fg2[(h_.*q[w_])^j_.] := {h, j}; fg2[q[v]*u[v]]", //
        "{u[v],1}");
    check("ClearAll[fg3]; fg3[r_*(h_.*q[w_])^j_.] := {r, h, j}; fg3[p[v]*q[v]*u[v]]", //
        "{p[v]*u[v],1,1}");
    check("ReplaceList[p[v]*q[v]*u[v], (k_.*p[w_])^i_.*(h_.*q[w_])^j_. :> {k, i, h, j}]", //
        "{}");
    // the substitution Sec[x] -> u is for the whole integrand, not for Sec[x]*Tan[x] alone
    check("Integrate[Sec[x]*Tan[x]/(4+Sec[x]^2), x]", //
        "-ArcTan[2*Cos[x]]/2");
  }

  /**
   * A rule goes before an older rule which differs from it only by blanks which are optional or
   * have no head test, whatever the attributes of the heads, unless the older rule has a condition
   * the new one hasn't; the older rule stays first when the new one is the more general one.
   */
  @Test
  public void testOptionalVariantRuleOrder() {
    check("ov1[u_^k_.] := optv; ov1[u_^k_] := plainv; {ov1[c^5], ov1[c], DownValues[ov1]}", //
        "{plainv,optv,{HoldPattern[ov1[u_^k_]]:>plainv,HoldPattern[ov1[u_^k_.]]:>optv}}");
    check("ov2[u_^k_] := plainv; ov2[u_^k_.] := optv; {ov2[c^5], ov2[c]}", //
        "{plainv,optv}");
    check("ov3[u_*w_.] := optv; ov3[u_*w_] := plainv; {ov3[c*d], ov3[c]}", //
        "{plainv,optv}");
    check("ov4[u_, k_:7] := optv; ov4[u_, k_] := plainv; {ov4[c, d], ov4[c]}", //
        "{plainv,optv}");
    // a head test only narrows the blank
    check("ov5[u_^k_.] := optv; ov5[u_^k_Integer] := plainv; {ov5[c^5], ov5[c^t], ov5[c]}", //
        "{plainv,optv,optv}");
    check("ov7[u_*k_] := anyv; ov7[u_*k_Integer] := intv; {ov7[3*c], ov7[t*c]}", //
        "{intv,anyv}");
    // a head test and a test are unrelated: definition order
    check("ov17[k_?NonNegative] := nonnegv; ov17[k_Integer] := intv; "
        + "{ov17[4], ov17[-4], DownValues[ov17]}", //
        "{nonnegv,intv,{HoldPattern[ov17[k_?NonNegative]]:>nonnegv,"
            + "HoldPattern[ov17[k_Integer]]:>intv}}");
    check("ov18[u_^k_?OddQ] := oddv; ov18[u_^k_Integer] := intv; {ov18[c^3], ov18[c^4]}", //
        "{oddv,intv}");
    // a rule which goes before an optional blank stops behind an unrelated rule in front of it
    check("ov10[u_^k_.] := optv; ov10[u_^k_?OddQ] := oddv; ov10[u_^k_Integer] := intv; "
        + "{ov10[c^5], ov10[c^4], ov10[c^(1/2)], DownValues[ov10]}", //
        "{oddv,intv,optv,{HoldPattern[ov10[u_^k_?OddQ]]:>oddv,HoldPattern[ov10[u_^k_Integer]]:>intv,"
            + "HoldPattern[ov10[u_^k_.]]:>optv}}");
    // but passes an equally general one
    // the names may differ if they can be renamed one by one
    check("ov9[u_*k_.] := optv; ov9[u_*j_Integer] := plainv; {ov9[3*c], ov9[c]}", //
        "{plainv,optv}");
    check("ov11[s_^l_.] := optv; ov11[t_^m_?IntegerQ] := testv; ov11[u_^n_Integer] := intv; "
        + "DownValues[ov11]", //
        "{HoldPattern[ov11[u_^n_Integer]]:>intv,HoldPattern[ov11[t_^m_?IntegerQ]]:>testv,"
            + "HoldPattern[ov11[s_^l_.]]:>optv}");
    // only in an exponent and only from _Integer to ?IntegerQ
    check("ov19[k_.] := optv; ov19[k_?IntegerQ] := testv; ov19[k_Integer] := intv; "
        + "DownValues[ov19][[All, 2]]", //
        "{testv,intv,optv}");
    check("ov20[s_^l_.] := optv; ov20[t_^m_Integer] := intv; ov20[u_^n_?IntegerQ] := testv; "
        + "DownValues[ov20][[All, 2]]", //
        "{intv,testv,optv}");
    // the same condition text which tests other patterns after renaming: definition order
    check("ov21[u_^k_.] := optv /; k > 2; ov21[k_^u_] := plainv /; k > 2; "
        + "{ov21[Pi^3], DownValues[ov21][[All, 2]]}", //
        "{optv,{optv/;k>2,plainv/;k>2}}");
    // renamed patterns with the same condition
    check("ov22[u_^k_.] := optv /; k > 2; ov22[v_^j_] := plainv /; j > 2; "
        + "{ov22[Pi^3], DownValues[ov22][[All, 2]]}", //
        "{plainv,{plainv/;j>2,optv/;k>2}}");
    // a name used twice can't be renamed into two names: the older rule stays first
    check("ov12[u_, u_^k_.] := optv; ov12[u_, v_^k_] := plainv; DownValues[ov12]", //
        "{HoldPattern[ov12[u_,u_^k_.]]:>optv,HoldPattern[ov12[u_,v_^k_]]:>plainv}");
    // the same condition on both rules
    check("ov6[u_+v_.*z_] := optv /; FreeQ[v, z]; ov6[u_+v_*z_] := plainv /; FreeQ[v, z]; "
        + "{ov6[1+2*t], ov6[1+t]}", //
        "{plainv,optv}");
    // a condition only on the more specific rule
    check("ov14[u_^k_.] := optv; ov14[u_^k_] := plainv /; k > 2; {ov14[c^5], ov14[c^2]}", //
        "{plainv,optv}");
    // a condition on the more general rule which the other one hasn't: definition order
    check("ov15[u_^k_.] := optv /; k > 2; ov15[u_^k_] := plainv; {ov15[c^5], ov15[c^2]}", //
        "{optv,plainv}");
    check("ov16[u_^k_.] := optv /; k < 5; ov16[u_^k_] := plainv /; k > 2; "
        + "{ov16[c^4], DownValues[ov16]}", //
        "{optv,{HoldPattern[ov16[u_^k_.]]:>optv/;k<5,HoldPattern[ov16[u_^k_]]:>plainv/;k>2}}");
  }

  /** <code>/:</code> is right-associative like <code>:=</code>: a definition may make an upvalue. */
  @Test
  public void testTagSetOnTheRightOfSetDelayed() {
    check("FullForm[Hold[a := b /: c := d]]", //
        "Hold(SetDelayed(a, TagSetDelayed(b, c, d)))");
    check("FullForm[Hold[a = b /: c = d]]", //
        "Hold(Set(a, TagSet(b, c, d)))");
    check("tsAttach[e_] := e /: tsShow[e] := shown; tsAttach[tsq]; tsShow[tsq]", //
        "shown");
  }

  /** The pattern of x:p is evaluated when it contains no patterns itself. */
  @Test
  public void testComputedPatternOfANamedPattern() {
    check("cpObj = {cpP, cpE}; "
        + "cpX[e_] := e /. (h:(Alternatives @@ cpObj))[a__] /; h =!= cpE :> h[a, 1]; "
        + "{cpX[cpP[2]], cpX[cpE[2]], cpX[cpQ[2]]}", //
        "{cpp[2,1],cpe[2],cpq[2]}");
  }

  /** In Hold[f][x] the hold is about f; x is evaluated. */
  @Test
  public void testArgumentsOfAHoldHead() {
    check("{Hold[f][1 + 1], HoldForm[g][2 + 3, x]}", //
        "{Hold[f][2],g[5,x]}");
  }

  /** Only the name of x:p is held, and the arguments of a pattern head are evaluated. */
  @Test
  public void testArgumentsOfAPatternHead() {
    check("phSel = phM | phC; {(h:phSel)[x_] -> h[x], z_[Part[{p}, 1]] -> 1, "
        + "Map[Rule[(h:phSel)[#[[1]], di___], h[#[[2]], di]] &, {p -> a}]}", //
        "{(h:phm|phc)[x_]->h[x],z_[p]->1,{(h:phm|phc)[p,di___]->h[a,di]}}");
    check("phM[p, 4] /. (h:phSel)[p, di___] -> h[a, di]", //
        "phm[a,4]");
    check("{phM[p, 4], phM[p, 3]} /. (h:phSel)[p, di_] /; MemberQ[{4}, di] :> h[a, di]", //
        "{phm[a,4],phm[p,3]}");
  }

  @Test
  public void testPatternFlatOneIdentity() {
    check("SetAttributes[r, Flat]", //
        "");
    check("r[a, b, b, c] /. r[x_, x_] -> rp[x]", //
        "r[a,rp[r[b]],c]");
    check("SetAttributes[r, OneIdentity]", //
        "");
    check("Attributes[r]", //
        "{Flat,OneIdentity}");
    check("r[a, b, b, c] /. r[x_, x_] -> rp[x]", //
        "r[a,rp[b],c]");
  }

  @Test
  public void testPatternOrder() {
    // see https://mathematica.stackexchange.com/questions/8619
    check("PatternOrder[x_, 1]", //
        "-1");
    check("PatternOrder[g[{}, _List], g[_, {___}]]", //
        "-1");
    check("PatternOrder[g[a], g[_]]", //
        "1");
    check("PatternOrder[a,b]", //
        "1");
    check("PatternOrder[g[a], g[b]]", //
        "1");
  }

  @Test
  public void testPatternPlus() {
    check("r[s,s] /. r[t_.+x_, x_] -> {t,x}", //
        "{0,s}");
    check("r[a+s,s] /. r[t_+x_, x_] -> {t,x}", //
        "{a,s}");
    check("r[a+b+s,s] /. r[t_+x_, x_] -> {t,x}", //
        "{a+b,s}");
  }

  @Test
  public void testPatternSequence() {
    check("integersQ[__Integer] = True", //
        "True");
    check("integersQ[__] = False", //
        "False");
    check("integersQ[1,2,3]", //
        "True");
    check("integersQ[1,2,a]", //
        "False");
  }

  @Test
  public void testPatternTest() {
    check("MatchQ[{1,8,Pi},{__?Positive}]", //
        "True");

    check("$j[x_, y_:1, z_:2] := jp[x, y, z]; $j[a,b]", //
        "jp[a,b,2]");
    check("$j[x_, y_:1, z_:2] := jp[x, y, z]; $j[a]", //
        "jp[a,1,2]");
    check("$f[x_:2]:={x};$f[]", //
        "{2}");
    check("$f[x_:2]:={x};$f[a]", //
        "{a}");

    check("MatchQ[3, _Integer?(#>0&)]", //
        "True");
    check("MatchQ[-3, _Integer?(#>0&)]", //
        "False");

    check("Cases[{1,2,3,5,x,y,4},_?NumberQ]", //
        "{1,2,3,5,4}");
    check("MatchQ[{1,8,Pi},{__?Positive}]", //
        "True");
    check("MatchQ[{1,I,0},{__?Positive}]", //
        "False");

    check("{3,-5,2,7,-6,3} /. _?Negative:>0", //
        "{3,0,2,7,0,3}");

    check("Cases[Range[0,350],_?(Divisible[#,7]&&Divisible[#,5]&)]", //
        "{0,35,70,105,140,175,210,245,280,315,350}");

    check("$f[n_?NonNegative, p_?PrimeQ]:=n^p; $f[2,3]", //
        "8");
    check("$f[n_?NonNegative, p_?PrimeQ]:=n^p; $f[2,4]", //
        "$f[2,4]");

    check("MatchQ[{{a,b},{c,d}},{_,_}?MatrixQ]", //
        "True");
    check("MatchQ[{a,b},{_,_}?MatrixQ]", //
        "False");

    check("Cases[{{a,b},{1,2,3},{{d,6},{d,10}}}, {_,_}?VectorQ]", //
        "{{a,b}}");
    check("Cases[{{a,b},{1,2,3},{{d,6},{d,10}}}, {x_,y_}/;!ListQ[x]&&!ListQ[y]]", //
        "{{a,b}}");
  }


  @Test
  @Disabled
  public void testFlakyPatternTest() {
    check("f[x_?NumericQ]:= NIntegrate[Sin[t^3], {t, 0, x}]", //
        "");
    // different results between ARM and x86 architecture
    check("f[2]", //
        "0.4519484775376951");
    check("f[(1+Sqrt[2])/5]", //
        "0.0135767506050606");
    check("f[a]", //
        "f[a]");
  }

  @Test
  public void testPatternUpSetDelayed() {
    check("(F[x_]:=s_)^:=Q[x,s]", //
        "");
    check("F[1]:=2", //
        "Q[1,2]");
  }

  @Test
  public void testPatternUpSetDelayedVerbatim() {
    // print message: TagSet: Tag Blank in F[Verbatim[_Q],Verbatim[_]] is Protected.
    check("F[Verbatim[_Q],Verbatim[_]]^:=1", //
        "$Failed");
  }

  @Test
  public void testSetSetraw() {
    // Set: Cannot assign to raw object 42.
    check("42 = 17-3", //
        "14");
    // SetDelayed: Cannot assign to raw object 42.
    check("42 := 17-3*x", //
        "17-3*x");
  }

  @Test
  public void testSetNosym() {
    // Set: aa_ does not contain a symbol to attach a rule to.
    check("aa_=2", //
        "2");
  }

  @Test
  public void testSimplePatternMatching() {
    // the space between "x_" and "." operator is needed:
    checkPattern("test[F_[a_.*x_^m_.]]", "test[g[h*y^2]]", "[g, h, y, 2]");
    checkPattern("x_ . y_", "a.b.c", "[a, b.c]"); // "[a.b, c]");
    checkPattern("x_+y_", "a+b+c", "[a, b+c]");
    checkPattern("f[x_]", "f[a]", "[a]");
    checkPattern("f[x_,y_]", "f[a,b]", "[a, b]");
    checkPattern("g[x_,y_]", "f[a,b]", "");
    checkPattern("g[x_,42, y_]", "g[a,42,b]", "[a, b]");
  }


  @Test
  public void testTagSet() {
    // TagSet: Argument 42 at position 1 is expected to be a symbol.
    check("42 /: g[h[x]] = 3", //
        "42/:g[h[x]]/:3");
    // TagSet: Cannot assign to raw object 42.
    check("zzz /: 42 = 40+2", //
        "42");
    check("x /: g[h[x]] = 3", //
        "3");
    check("f/:format[f]=0", //
        "0");
    // check("f/:format(f)/:0", //
    // "Syntax error in line: 1 - Operator: '/:' not created properly (no grouping defined)\n" //
    // + "f/:format(f)/:0\n" //
    // + " ^");
    check("f/: format[f] = \"TagSet test\"", //
        "TagSet test");
    // Format is a built-in name now, so even in relaxed syntax the lowercase spelling resolves to
    // it; the tag set above is still on f, not on Format
    check("format[]", //
        "Format[]");
  }

  @Test
  public void testTagSetDelayed() {
    // TagSetDelayed: Argument 42 at position 1 is expected to be a symbol.
    check("42 /: f[a,g,z_]/;True := {z}", //
        "TagSetDelayed[42,f[a,g,z_]/;True,{z}]");

    check("g /: f[a,g,z_]/;True := {z}", //
        "");
    check("f[a,g,test]", //
        "{test}");

    check("g /: f[g[x_]] := fg[x]", //
        "");
    check("{f[g[2]] , f[h[2]] }", //
        "{fg[2],f[h[2]]}");
  }

  @Test
  public void testTagSetDelayed02() {
    check("TagSetDelayed[g,0,\"TagSetDelayed test\"]", //
        "$Failed");
    check("g/: Format[a_,g[x]] := \"TagSetDelayed test\"", //
        "");
    // check("Definition(g)", //
    // "TagSet test");
    check("Format[f,g[x]]", //
        "TagSetDelayed test");
  }

  @Test
  public void testBlockPatternMatching() {
    check("g[x_] := Block[{}, Return[a]; x /; x != 0]", //
        "");
    check("g[1]", //
        "a");
    check("g[0]", //
        "a");
    check("g[x_] := Block[{}, Print[a]; x /; x != 0]", //
        "");
    check("g[1]", //
        "1");
    check("g[0]", //
        "g[0]");
    check("f[x_,y_]:=Block[{w = False}, {x,y} /; TrueQ[w]]", //
        "");
    check("f[a,b]", //
        "f[a,b]");
    check("g[x_,y_]:=Block[{w = True}, {x,y} /; TrueQ[w]]", //
        "");
    check("g[a,b]", //
        "{a,b}");
    check("w", //
        "w");
  }

  @Test
  public void testModulePatternMatching() {
    check("g[x_] := Module[{}, Return[a]; x /; x != 0]", //
        "");
    check("g[1]", //
        "a");
    check("g[0]", //
        "a");
    check("g[x_] := Module[{}, Print[a]; x /; x != 0]", //
        "");
    check("g[1]", //
        "1");
    check("g[0]", //
        "g[0]");

    check("f[x_,y_]:=Module[{w = False}, {x,y} /; TrueQ[w]]", //
        "");
    check("f[a,b]", //
        "f[a,b]");
    check("g[x_,y_]:=Module[{w = True}, {x,y} /; TrueQ[w]]", //
        "");
    check("g[a,b]", //
        "{a,b}");
    check("w", //
        "w");
  }

  @Test
  public void testWithPatternMatching() {
    check("g[x_] := With[{}, Return[a]; x /; x != 0]", //
        "");
    check("g[1]", //
        "a");
    check("g[0]", //
        "a");
    check("g[x_] := With[{}, Print[a]; x /; x != 0]", //
        "");
    check("g[1]", //
        "1");
    check("g[0]", //
        "g[0]");
    check("f[x_,y_]:=With[{w = False}, {x,y} /; TrueQ[w]]", //
        "");
    check("f[a,b]", //
        "f[a,b]");
    check("g[x_,y_]:=With[{w = True}, {x,y} /; TrueQ[w]]", //
        "");
    check("g[a,b]", //
        "{a,b}");
    check("w", //
        "w");
  }

}
