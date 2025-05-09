package org.matheclipse.core.system;

import static org.junit.Assert.assertEquals;
import java.io.StringWriter;
import java.util.ArrayList;
import javax.script.ScriptEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.eval.TimeConstrainedEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.patternmatching.PatternMatcher;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

@RunWith(JUnit4.class)
public class PatternMatchingTestCase {

  private Parser fParser;

  protected EvalUtilities util;

  protected static boolean DEBUG = true;

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
    check(EvalEngine.get(), false, strEval, strResult, false);
  }

  public void check(IAST ast, String strResult) {
    check(EvalEngine.get(), true, ast, strResult);
  }

  //
  public void check(EvalEngine engine, boolean configMode, String strEval, String strResult) {
    check(engine, configMode, strEval, strResult, false);
  }

  public void check(EvalEngine engine, boolean configMode, String strEval, String strResult,
      boolean relaxedSyntax) {
    try {
      if (strEval.length() == 0 && strResult.length() == 0) {
        return;
      }
      IExpr result;
      StringWriter buf = new StringWriter();

      Config.SERVER_MODE = configMode; // configMode;
      if (Config.SERVER_MODE) {
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
        if ((result != null) && !result.equals(F.Null)) {
          OutputFormFactory off = OutputFormFactory.get(relaxedSyntax);
          off.setIgnoreNewLine(true);
          off.convert(buf, result);
        }
      }

      assertEquals(buf.toString(), strResult);
    } catch (Exception e) {
      e.printStackTrace();
      assertEquals("", "1");
    }
  }

  public void check(EvalEngine engine, boolean configMode, IAST ast, String strResult) {
    try {

      StringWriter buf = new StringWriter();
      Config.SERVER_MODE = configMode;
      if (Config.SERVER_MODE) {
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
  public void testWithPatternMatching() {
    check("f[x_,y_]:=With[{w = False}, {x,y} /; TrueQ[w]]", //
        "");
    check("f[a,b]", //
        "f[a,b]");
    check("g[x_,y_]:=With[{w = True}, {x,y} /; TrueQ[w]]", //
        "");
    check("g[a,b]", //
        "{a,b}");
  }

  // @Test
  // public void testSlotPatternMatching() {
  // checkPattern("b_.* #+c_.*#^2", "#-1*#^2", "");
  // checkPattern("b_.* #+c_.*#^2", "#+#^2", "[1, 1]");
  // checkPattern("a_. + b_.* #+c_.*#^2", "-1+#+#^2", "[-1, 1, 1]");
  // }

  @Test
  public void testPattern() {}

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

    check("f[x_?NumericQ]:= NIntegrate[Sin[t^3], {t, 0, x}]", //
        "");
    check("f[2]", //
        "0.4519484771568257");
    check("f[(1+Sqrt[2])/5]", //
        "0.0135767506042311");
    check("f[a]", //
        "f[a]");

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
    // TODO 3^2 -> 9 - but why? Condition has attribute HoldAll
    check("f[3]/.(f[u_]->u^2/; u>3/; u>2)", //
        "3^2/;3>3/;3>2");
    // TODO 4^2 -> 16 but why? Condition has attribute HoldAll
    check("f[4]/.(f[u_]->u^2/; u>3/; u>2)", //
        "4^2/;4>3/;4>2");
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
    // TODO make rule ordering more compatible with WMA
    // see mathics-core #1233
    check("Bug[n_Integer,_] :={\"Negative\", n} /; (n<0)", //
        "");
    check("Bug[-5,3]", //
        "{Negative,-5}");

    check("Bug[n_Integer,maxpart_Integer] :={\"Integer\", n}", //
        "");
    check("Bug[-5,3]", //
        "{Integer,-5}");
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
  // @Test
  // public void testTagSetDelayed1() {
  // check(
  // "Unprotect[ProductLog];
  // ProductLog/:ProductLog[k_,z_]/;NumericQ[k]&&!IntegerQ[k]:=WrightOmega[Log[z]+2*Pi*I*k]", //
  // "");
  // check("ProductLog[1/2,z]", //
  // "wrightomega[I*Pi+Log[z]]");
  // }
  //
  // @Test
  // public void testTagSetDelayed2() {
  // check(
  // "Unprotect[ProductLog];
  // ProductLog[k_,z_]/;NumericQ[k]&&!IntegerQ[k]:=WrightOmega[Log[z]+2*Pi*I*k]", //
  // "");
  // check("ProductLog[1/2,z]", //
  // "wrightomega[I*Pi+Log[z]]");
  // }

  /** The JUnit setup method */
  @Before
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
}
