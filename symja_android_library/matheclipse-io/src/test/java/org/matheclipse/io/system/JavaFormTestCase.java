package org.matheclipse.io.system;

import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CInfinity;
import static org.matheclipse.core.expression.F.Sinc;
import static org.matheclipse.core.expression.F.Times;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IExpr.SourceCodeProperties;
import org.matheclipse.core.interfaces.IExpr.SourceCodeProperties.Prefix;
import org.matheclipse.core.tensor.qty.IQuantity;
import org.matheclipse.core.tensor.qty.IUnit;
import org.matheclipse.parser.client.ParserConfig;

/** */
public class JavaFormTestCase extends AbstractTestCase {
  public JavaFormTestCase(String name) {
    super(name);
  }

  public void testJavaForm001() {
    // don't distinguish between lower- and uppercase identifiers
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
    EvalUtilities util = new EvalUtilities(false, true);

    IAST function = Sinc(Times(CI, CInfinity));

    IExpr result = EvalEngine.get().evalHoldPattern(function);
    assertEquals("Sinc(DirectedInfinity(CI))", result.internalFormString(true, -1).toString());

    result = util.evaluate(function);
    assertEquals("oo", result.internalFormString(true, -1).toString());
  }

  private static final SourceCodeProperties SYMBOL_FACTORY_PROPERTIES =
      SourceCodeProperties.of(true, false, Prefix.CLASS_NAME, false);
  private static final SourceCodeProperties SYMBOL_FACTORY_PROPERTIES_FULL_NAMES =
      SourceCodeProperties.of(true, false, Prefix.FULLY_QUALIFIED_CLASS_NAME, false);

  public void testJavaForm002() {
    // don't distinguish between lower- and uppercase identifiers
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
    EvalUtilities util = new EvalUtilities(false, true);

    IAST function = Sinc(Times(CI, CInfinity));

    IExpr result = EvalEngine.get().evalHoldPattern(function);
    assertEquals("F.Sinc(F.DirectedInfinity(F.CI))",
        result.internalJavaString(SYMBOL_FACTORY_PROPERTIES, -1, x -> null).toString());

    result = util.evaluate(function);
    assertEquals("F.oo",
        result.internalJavaString(SYMBOL_FACTORY_PROPERTIES, -1, x -> null).toString());
  }

  public void testJavaForm002_fullyQualifiedName() {
    // don't distinguish between lower- and uppercase identifiers
    ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS = true;
    EvalUtilities util = new EvalUtilities(false, true);
    IAST function = Sinc(Times(CI, CInfinity));

    IExpr result = EvalEngine.get().evalHoldPattern(function);
    assertEquals(
        "org.matheclipse.core.expression.F.Sinc(org.matheclipse.core.expression.F.DirectedInfinity(org.matheclipse.core.expression.F.CI))",
        result.internalJavaString(SYMBOL_FACTORY_PROPERTIES_FULL_NAMES, -1, x -> null).toString());

    result = util.evaluate(function);
    assertEquals("org.matheclipse.core.expression.F.oo",
        result.internalJavaString(SYMBOL_FACTORY_PROPERTIES_FULL_NAMES, -1, x -> null).toString());
  }

  private static final SourceCodeProperties NO_SYMBOL_FACTORY_PROPERTIES =
      SourceCodeProperties.of(false, false, Prefix.CLASS_NAME, false);
  private static final SourceCodeProperties NO_SYMBOL_FACTORY_PROPERTIES_FULL_NAMES =
      SourceCodeProperties.of(false, false, Prefix.FULLY_QUALIFIED_CLASS_NAME, false);

  public void testJavaFormQuantity_unitKG() {
    IExpr quantity = IQuantity.of(F.ZZ(43L), IUnit.ofPutIfAbsent("kg"));
    assertEquals("IQuantity.of(F.ZZ(43L),IUnit.ofPutIfAbsent(\"kg\"))",
        quantity.internalJavaString(NO_SYMBOL_FACTORY_PROPERTIES, -1, null).toString());
  }

  public void testJavaFormQuantity_unitKGAndFullyQualifiedName() {
    IExpr quantity =
        org.matheclipse.core.tensor.qty.IQuantity.of(org.matheclipse.core.expression.F.ZZ(43L),
            org.matheclipse.core.tensor.qty.IUnit.ofPutIfAbsent("kg"));
    assertEquals(
        "org.matheclipse.core.tensor.qty.IQuantity.of(org.matheclipse.core.expression.F.ZZ(43L),org.matheclipse.core.tensor.qty.IUnit.ofPutIfAbsent(\"kg\"))",
        quantity.internalJavaString(NO_SYMBOL_FACTORY_PROPERTIES_FULL_NAMES, -1, null).toString());
  }

  public void testJavaFormQuantity_unitOne() {
    IExpr quantity = IQuantity.of(F.ZZ(43L), IUnit.ONE);
    assertEquals("IQuantity.of(F.ZZ(43L),IUnit.ONE)",
        quantity.internalJavaString(NO_SYMBOL_FACTORY_PROPERTIES, -1, null).toString());
  }

  public void testJavaFormQuantity_unitOneAndFullyQualifiedName() {
    IExpr quantity = org.matheclipse.core.tensor.qty.IQuantity
        .of(org.matheclipse.core.expression.F.ZZ(43L), org.matheclipse.core.tensor.qty.IUnit.ONE);
    assertEquals(
        "org.matheclipse.core.tensor.qty.IQuantity.of(org.matheclipse.core.expression.F.ZZ(43L),org.matheclipse.core.tensor.qty.IUnit.ONE)",
        quantity.internalJavaString(NO_SYMBOL_FACTORY_PROPERTIES_FULL_NAMES, -1, null).toString());
  }
}
