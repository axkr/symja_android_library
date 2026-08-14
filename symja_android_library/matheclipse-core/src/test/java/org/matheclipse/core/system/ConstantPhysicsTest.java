package org.matheclipse.core.system;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.ISymbol;

/** Tests for SparseArray functions */
public class ConstantPhysicsTest extends ExprEvaluatorTestCase {

  @Test
  public void testAvogadroConstant() {
    // Moles^(-1)
    check("AvogadroConstant", //
        "Quantity(602214076000000000000000,\"Moles\"^(-1))");
  }

  @Test
  public void testBohrRadius() {
    // Nanometers
    check("BohrRadius", //
        "Quantity(0.0529177,\"Nanometers\")");
  }

  @Test
  public void testUniverseAge() {
    // Years
    check("UniverseAge", //
        "Quantity(1.3787*10^10,\"JulianYears\")");
  }


  /** The JUnit setup method */
  @Override
  public void setUp() {
    Config.BUILTIN_PROTECTED = ISymbol.PROTECTED;
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    // // dummy eval
    // try {
    // fScriptEngine.eval("");
    // } catch (ScriptException e) {
    // }
    EvalEngine engine = EvalEngine.get();// (EvalEngine) fScriptEngine.get("EVAL_ENGINE");
    engine.setIterationLimit(50000);
    engine.setRecursionLimit(256);
  }

  @AfterEach
  public void tearDown() throws Exception {
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
