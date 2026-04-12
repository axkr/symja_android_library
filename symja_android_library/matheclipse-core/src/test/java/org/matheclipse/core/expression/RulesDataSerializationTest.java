package org.matheclipse.core.expression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.RulesData;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class RulesDataSerializationTest {

  private static Kryo kryo;

  @BeforeClass
  public static void setUpClass() throws Exception {
    // Initialize Symja's core system and Kryo
    F.initSymja();
    kryo = KryoUtil.initKryo();
  }

  /**
   * Helper method to serialize RulesData to a byte array.
   */
  private byte[] serializeRules(RulesData rulesData) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try (Output output = new Output(outputStream)) {
      kryo.writeClassAndObject(output, rulesData);
    }
    return outputStream.toByteArray();
  }

  /**
   * Helper method to deserialize a byte array back into RulesData.
   */
  private RulesData deserializeRules(byte[] bytes) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
    try (Input input = new Input(inputStream)) {
      return (RulesData) kryo.readClassAndObject(input);
    }
  }

  @Test
  public void testConstantDownRulesSerialization() {
    EvalEngine engine = new EvalEngine(false);
    ISymbol f = F.symbol("f_const");

    // Define a constant rule: f[1] = 100
    engine.evaluate(F.Set(F.unary(f, F.C1), F.ZZ(100)));

    RulesData originalRules = f.getRulesData();
    Assert.assertNotNull("Original RulesData should not be null", originalRules);

    // Serialize and Deserialize
    byte[] serializedData = serializeRules(originalRules);
    RulesData deserializedRules = deserializeRules(serializedData);

    Assert.assertNotNull("Deserialized RulesData should not be null", deserializedRules);

    // Verify structural equivalence using definition()
    List<IAST> originalDefs = originalRules.definition();
    List<IAST> deserializedDefs = deserializedRules.definition();

    Assert.assertEquals("Definitions size should match", originalDefs.size(),
        deserializedDefs.size());
    Assert.assertEquals("Constant rule AST should match exactly", originalDefs.get(0),
        deserializedDefs.get(0));
  }

  @Test
  public void testPatternDownRulesSerialization() {
    EvalEngine engine = new EvalEngine(false);
    ISymbol f = F.symbol("f_pattern");

    // Define a pattern rule: f[x_] := x ^ 2
    engine.evaluate(F.SetDelayed(F.unary(f, F.x_), F.Sqr(F.x)));

    RulesData originalRules = f.getRulesData();
    byte[] serializedData = serializeRules(originalRules);
    RulesData deserializedRules = deserializeRules(serializedData);

    List<IAST> originalDefs = originalRules.definition();
    List<IAST> deserializedDefs = deserializedRules.definition();

    Assert.assertEquals("Definitions size should match", originalDefs.size(),
        deserializedDefs.size());
    Assert.assertEquals("Pattern rule AST should match exactly", originalDefs.get(0),
        deserializedDefs.get(0));
  }

  @Test
  public void testUpRulesSerialization() {
    EvalEngine engine = new EvalEngine(false);
    ISymbol f = F.symbol("f_up");
    ISymbol g = F.symbol("g_up");

    // Define an UpRule: f[g[x_]] ^:= x
    engine.evaluate(F.UpSetDelayed(F.unary(f, F.unary(g, F.x_)), F.x));

    RulesData originalRules = g.getRulesData(); // UpRules are attached to the inner symbol 'g'
    byte[] serializedData = serializeRules(originalRules);
    RulesData deserializedRules = deserializeRules(serializedData);

    List<IAST> originalDefs = originalRules.definition();
    List<IAST> deserializedDefs = deserializedRules.definition();

    Assert.assertEquals("Definitions size should match", originalDefs.size(),
        deserializedDefs.size());
    Assert.assertEquals("UpRule AST should match exactly", originalDefs.get(0),
        deserializedDefs.get(0));
  }

  @Test
  public void testFunctionalEvaluationAfterDeserialization() {
    EvalEngine engine = new EvalEngine(false);
    ISymbol fOrig = F.symbol("f_func_orig");

    // Define mixed rules
    // f[0] = 1
    engine.evaluate(F.Set(F.unary(fOrig, F.C0), F.C1));
    // f[x_] := x * 10
    engine.evaluate(F.SetDelayed(F.unary(fOrig, F.x_), F.Times(F.C10, F.x)));

    // Serialize and clone the rules
    RulesData originalRules = fOrig.getRulesData();
    byte[] serializedData = serializeRules(originalRules);
    RulesData deserializedRules = deserializeRules(serializedData);

    // Attach the deserialized rules to the cleared symbol
    fOrig.clearAll(engine);
    fOrig.setRulesData(deserializedRules);

    // Evaluate using the new symbol and ensure the pattern matcher works
    // fOrig[0] should evaluate to 1 (Constant Rule)
    Assert.assertEquals(F.C1, engine.evaluate(F.unary(fOrig, F.C0)));

    // fOrig[5] should evaluate to 50 (Pattern Rule)
    Assert.assertEquals(F.ZZ(50), engine.evaluate(F.unary(fOrig, F.ZZ(5))));
  }
}