package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.File;
import java.io.FileOutputStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.Integrate;
import com.esotericsoftware.kryo.io.Output;

public class IntegrateSerializationTest {

  @BeforeAll
  public static void setUpClass() {
    // Ensure Symja's core is initialized before any tests run
    F.initSymja();
    Integrate.INTEGRATE_RULES_READ.set(true);
    final EvalEngine engine = EvalEngine.get();
    ContextPath path = engine.getContextPath();
    try {
      Integrate.CONST.await();
      // EvalEngine.get().getContextPath().add(org.matheclipse.core.expression.Context.RUBI);
      // UtilityFunctionCtors.getRuleASTRubi45();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      engine.setContextPath(path);
    }
  }

  @Test
  public void testLocalCacheSerializationAndEvaluation() throws Exception {
    // Create a temporary file that will be deleted after the test
    File tempCache = File.createTempFile("symja_test_rubi", ".bin");
    tempCache.deleteOnExit();

    // 1. Force raw initialization to guarantee RulesData is populated
    assertNotNull(S.Integrate.getRulesData(), "RulesData must exist after raw init");

    // 2. Serialize the rules to the temp file
    Integrate.serializeRubiRules(tempCache);
    assertTrue(tempCache.length() > 0, "Cache file should be created and non-empty");

    // 3. Sabotage the memory: Clear the rules from the system
    S.Integrate.setRulesData(null);
    assertNull(S.Integrate.getRulesData(), "RulesData should be null after clearing");

    // 4. Restore the rules via Kryo Deserialization
    boolean success = Integrate.deserializeRubiRules(tempCache);
    assertTrue(success, "Deserialization from valid cache should succeed");
    assertNotNull(S.Integrate.getRulesData(), "RulesData should be fully restored");

    // 5. Functional Proof: Evaluate a basic integral to ensure the pattern matcher works
    EvalEngine engine = new EvalEngine(false);
    IExpr result = engine.evaluate(F.Integrate(F.Sin(F.x), F.x));

    // The integral of Sin(x) is -Cos(x)
    assertEquals(F.Negate(F.Cos(F.x)).toString(), result.toString());
  }

  @Test
  public void testFingerprintMismatchInvalidation() throws Exception {
    File tempCache = File.createTempFile("symja_test_bad_rubi", ".bin");
    tempCache.deleteOnExit();

    // Manually write a fake Kryo file with a mismatched ID fingerprint
    try (Output output = new Output(new FileOutputStream(tempCache))) {
      output.writeString("RUBI_V1_ID_COUNT:9999999"); // Fake, impossible fingerprint
      output.writeInt(0); // Dummy trailing data
    }

    // Attempt to deserialize the tampered file
    boolean success = Integrate.deserializeRubiRules(tempCache);

    // The method should detect the shift and aggressively reject the cache
    assertFalse(success,
        "Deserialization should fail and reject the file due to mismatched fingerprint");
  }

  @Test
  public void testMissingCacheFile() {
    File missingFile = new File("this_file_absolutely_does_not_exist_12345.bin");

    // Attempting to read a non-existent file should gracefully return false, not throw an exception
    boolean success = Integrate.deserializeRubiRules(missingFile);
    assertFalse(success, "Deserialization should fail gracefully if file is missing");
  }

}
