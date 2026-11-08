package org.matheclipse.internal.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.StringWriter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.SyntaxError;

/**
 * The <code>Internal`</code> functions as packages written in Wolfram Language call them, with
 * square brackets and case-sensitive names: the call sites in the WLJS notebook and FeynCalc.
 *
 * <p>
 * Runs in a JVM of its own, see the "wolfram-language-syntax" surefire execution in the pom.
 */
@Tag("wolfram-language-syntax")
public class InternalWolframSyntaxTest {

  static {
    // before anything of Symja is loaded: the built-in symbol table is keyed by name, and with
    // Symja's relaxed syntax those keys are lower-cased
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = false;
  }

  private final ExprEvaluator wolframLanguage;

  public InternalWolframSyntaxTest() throws InterruptedException {
    F.await();
    org.matheclipse.internal.InternalInit.init();
    EvalEngine engine = new EvalEngine(false);
    EvalEngine.set(engine);
    engine.init();
    engine.setRecursionLimit(512);
    engine.setIterationLimit(500);
    engine.setOutListDisabled(false, (short) 10);
    wolframLanguage = new ExprEvaluator(engine, false, (short) 100);
  }

  private void check(String evalString, String expectedResult) {
    EvalEngine previous = EvalEngine.get();
    try {
      EvalEngine.set(wolframLanguage.getEvalEngine());
      IExpr result = wolframLanguage.eval(evalString);
      assertEquals(expectedResult, print(result));
    } catch (SyntaxError e) {
      assertEquals(expectedResult, e.getMessage());
    } finally {
      EvalEngine.set(previous);
    }
  }

  private static String print(IExpr result) {
    if (result == S.Null) {
      return "";
    }
    StringWriter buf = new StringWriter();
    int significantFigures = EvalEngine.get().getSignificantFigures();
    OutputFormFactory off =
        OutputFormFactory.get(false, false, significantFigures - 1, significantFigures + 1);
    return off.convert(buf, result) ? buf.toString() : "ERROR-IN-OUTPUTFORM";
  }

  @Test
  public void testBag() {
    check("bag = Internal`Bag[{1, 2}]; Internal`StuffBag[bag, 3]; "
        + "{bag, Internal`BagLength[bag], Internal`BagPart[bag, All]}", //
        "{Internal`Bag[<3>],3,{1,2,3}}");
    check("bag = Internal`Bag[]; Do[Internal`StuffBag[bag, k^2], {k, 4}]; "
        + "Internal`BagPart[bag, 2] = 0; Internal`BagPart[bag, All]", //
        "{1,0,9,16}");
  }

  @Test
  public void testPrintsTheContext() {
    // Internal`Square is not System`Square
    check("Hold[Internal`Square[x]] // FullForm", //
        "Hold[Internal`Square[x]]");
    check("ToString[Hold[Internal`Square[x]], InputForm]", //
        "Hold[Internal`Square[x]]");
    check("Context[Internal`LocalizedBlock]", //
        "Internal`");
  }

  /** The function a WLJS Manipulate makes of its body, see wljs-manipulate/Kernel.wl. */
  @Test
  public void testManipulateFunction() {
    check("fn = Internal`LocalizedBlock[{a, Subscript[b, 1]}, {a, Subscript[b, 1]} = {##}; "
        + "a + 10 Subscript[b, 1]] &; {fn[1, 2], fn[3, 4]}", //
        "{21,43}");
    check("{a, Subscript[b, 1]}", //
        "{a,Subscript[b,1]}");
  }

  @Test
  public void testWithLocalSettings() {
    check("log = {}; {CheckAbort[Internal`WithLocalSettings[AppendTo[log, \"open\"], "
        + "AppendTo[log, \"work\"]; Abort[]; AppendTo[log, \"never\"], "
        + "AppendTo[log, \"close\"]], $Aborted], log}", //
        "{$Aborted,{open,work,close}}");
  }

  @Test
  public void testInheritedBlock() {
    check("q[1] = 1; {Internal`InheritedBlock[{q}, q[2] = 2; {q[1], q[2]}], q[2]}", //
        "{{1,2},q[2]}");
  }

  /** The tests FeynCalc's PaVeLimitTo4 and FCFeynmanParametrize make. */
  @Test
  public void testFeynCalcPredicates() {
    check("Internal`RationalFunctionQ[#, D] & /@ {(D - 4)/(D - 2)^2, Sqrt[D - 4], Gamma[D]}", //
        "{True,False,False}");
    check("Internal`ExceptionFreeQ /@ {1/(D - 4) /. D -> 4, 2 Pi, Gamma[0]}", //
        "{False,True,False}");
    check("Internal`SyntacticNegativeQ /@ {-x y, x - y}", //
        "{True,False}");
  }
}
