package org.matheclipse.core.system;

import org.matheclipse.core.interfaces.EvalFlags.Flag;
import org.matheclipse.core.interfaces.EvalFlags.Group;
import org.matheclipse.core.interfaces.EvalFlags;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;

/**
 * The evaluation flags of an {@link IAST} are persisted as a <i>short</i> by every
 * {@code readExternal}/{@code writeExternal} pair, so only the low 16 bits survive a round trip.
 * These tests pin that contract down, in particular that a stored {@link IAST#IS_DERIVATIVE_EVALED}
 * (<code>0x8000</code>, the sign bit of a short) is <b>not</b> sign-extended back into a word with
 * every high flag switched on.
 */
public class EvalFlagsSerializationTest {

  /** The set of evaluation flags which survive an externalization round trip. */
  private static final int PERSISTED_MASK = Group.PERSISTENT.mask();

  /** The nine classes which implement their own {@code readExternal}/{@code writeExternal}. */
  private static final String[] EXTERNALIZABLE_AST_CLASSES = {"AST", "AST0", "B1", "B2", "B3",
      "ASTRRBTree", "ASTSeriesData", "ASTRealVector", "ASTRealMatrix"};

  @BeforeEach
  public void setUp() throws Exception {
    // wait for initializing of Integrate() rules:
    F.await();
  }

  /**
   * The flags are written with {@code writeShort()}. Keeping the persisted subset below
   * <code>0x8000</code> means the sign bit of that short is never set, so the sign extension which
   * {@code readShort()} would otherwise apply is structurally impossible - not merely masked away.
   */
  @Test
  public void testThePersistedSubsetCannotReachTheSignBitOfAShort() {
    assertTrue(PERSISTED_MASK > 0 && PERSISTED_MASK < 0x8000,
        String.format("PERSISTENT is 0x%08X, which can set the sign bit of the short it is"
            + " written as", PERSISTED_MASK));
  }

  /** Every flag of the persisted subset has to come back. */
  @Test
  public void testPersistentFlagsSurviveARoundTrip() {
    List<String> problems = new ArrayList<String>();
    for (IAST ast : externalizableASTs()) {
      ast.setEvalFlagBits(PERSISTED_MASK);

      int flags = roundTrip(ast).getEvalFlagBits();
      if (flags != PERSISTED_MASK) {
        problems.add(String.format("%s: expected 0x%08X but was 0x%08X",
            ast.getClass().getSimpleName(), PERSISTED_MASK, flags));
      }
    }
    assertEquals(Collections.emptyList(), problems);
  }

  /**
   * Everything outside the persisted subset is a cache which a deserialized expression recomputes,
   * so none of it may come back. Pins the subset, so that changing it is a deliberate, visible
   * change.
   */
  @Test
  public void testNonPersistentFlagsAreDropped() {
    List<String> problems = new ArrayList<String>();
    for (IAST ast : externalizableASTs()) {
      ast.setEvalFlagBits(0xFFFFFFFF);

      int flags = roundTrip(ast).getEvalFlagBits();
      if (flags != PERSISTED_MASK) {
        problems.add(String.format("%s: expected exactly 0x%08X but was 0x%08X",
            ast.getClass().getSimpleName(), PERSISTED_MASK, flags));
      }
    }
    assertEquals(Collections.emptyList(), problems);
  }

  @Test
  public void testNoFlagsRoundTripsToNoFlags() {
    List<String> problems = new ArrayList<String>();
    for (IAST ast : externalizableASTs()) {
      ast.setEvalFlagBits(EvalFlags.Mask.NONE);

      int flags = roundTrip(ast).getEvalFlagBits();
      if (flags != EvalFlags.Mask.NONE) {
        problems.add(String.format("%s: expected no flags but was 0x%08X",
            ast.getClass().getSimpleName(), flags));
      }
    }
    assertEquals(Collections.emptyList(), problems);
  }

  /**
   * The reason the persisted subset is not empty: these answers cannot be recomputed from the tree
   * after deserialization, so dropping them would change behaviour rather than just cost work.
   */
  @Test
  public void testTheFlagsWhichCannotBeRecomputedSurvive() {
    // isPatternExpr() is a pure memo read - with the pattern flags gone, a deserialized f(x_)
    // would report itself as a non-pattern expression
    IAST pattern = F.unary(F.f, F.$p("x"));
    // isPatternExpr() only reads the memo, so the flags have to be computed once first
    assertFalse(pattern.isFreeOfPatterns(), "precondition: f(x_) contains a pattern");
    assertTrue(pattern.isPatternExpr(), "precondition: the memo is populated");
    assertTrue(roundTrip(pattern).isPatternExpr(), "a deserialized f(x_) is still a pattern expr");

    // PatternMatching#evalLHS reads "neither IS_FLATTENED nor IS_SORTED" as "this left-hand side
    // still needs evaluation", so a rule LHS must keep the pair
    IAST lhs = F.List(F.a, F.b).addFlags(Group.FLATTENED_OR_SORTED);
    assertTrue(roundTrip(lhs).hasAllFlags(Group.FLATTENED_OR_SORTED),
        "a deserialized rule left-hand side keeps its flattened/sorted stamp");

    // only the parser can know this one
    IAST times = F.List(F.a, F.b).addFlag(Flag.TIMES_PARSED_IMPLICIT);
    assertTrue(roundTrip(times).hasFlag(Flag.TIMES_PARSED_IMPLICIT),
        "an implicitly parsed Times keeps its parser stamp");

    // and a pure cache must not survive
    IAST cached = F.List(F.a, F.b).addFlag(Flag.IS_HASH_EVALED);
    assertFalse(roundTrip(cached).hasFlag(Flag.IS_HASH_EVALED),
        "a recomputable cache must not be persisted");
  }

  /** Guards that {@link #externalizableASTs()} really covers all nine implementations. */
  @Test
  public void testAllExternalizableClassesAreCovered() {
    Set<String> covered = new LinkedHashSet<String>();
    for (IAST ast : externalizableASTs()) {
      // B1/B2/B3 are abstract; their subclasses inherit the read/writeExternal pair
      Class<?> clazz = ast.getClass();
      while (clazz.getSuperclass() != null && clazz.getSimpleName().indexOf('.') < 0) {
        covered.add(clazz.getSimpleName());
        clazz = clazz.getSuperclass();
      }
    }
    for (String name : EXTERNALIZABLE_AST_CLASSES) {
      assertTrue(covered.contains(name), "no test expression for " + name + ", covered: " + covered);
    }
  }

  /** One instance of each class which externalizes {@code fEvalFlags} itself. */
  private static List<IAST> externalizableASTs() {
    List<IAST> result = new ArrayList<IAST>();
    result.add(F.Break()); // AST0
    result.add(F.Cos(F.x)); // B1
    result.add(F.List(F.a, F.b)); // B2
    result.add(F.List(F.a, F.b, F.c)); // B3
    result.add(F.List(F.a, F.b, F.c, F.d, F.e)); // AST
    result.add(largeList()); // ASTRRBTree
    result.add(new ASTSeriesData(F.x, F.a, F.List(F.C0, F.C1, F.C3), 0, 10, 1));
    result.add(new ASTRealVector(new double[] {1.0, 1.2, 3.4}, false));
    result.add(new ASTRealMatrix(new double[][] {{1.0, 2.0, 3.0}, {3.3, 4.4, 5.5}}, false));
    return result;
  }

  /** A list above {@code Config.MIN_LIMIT_PERSISTENT_LIST}, so that it is backed by an RRB tree. */
  private static IAST largeList() {
    IASTAppendable list = F.ListAlloc(64);
    for (int i = 0; i < 64; i++) {
      list.append(F.ZZ(i));
    }
    return list.copyAppendable();
  }

  private static IAST roundTrip(IAST original) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(original);
      byte[] bArray = baos.toByteArray();
      baos.close();
      oos.close();

      ByteArrayInputStream bais = new ByteArrayInputStream(bArray);
      ObjectInputStream ois = new ObjectInputStream(bais);
      Object copy = ois.readObject();
      bais.close();
      ois.close();
      return (IAST) copy;
    } catch (ClassNotFoundException | IOException e) {
      throw new AssertionError("serialization of " + original.getClass().getSimpleName()
          + " failed: " + e, e);
    }
  }
}
