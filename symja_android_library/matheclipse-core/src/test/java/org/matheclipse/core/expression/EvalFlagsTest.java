package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.interfaces.EvalFlags;
import org.matheclipse.core.interfaces.EvalFlags.Flag;
import org.matheclipse.core.interfaces.EvalFlags.Group;
import org.matheclipse.core.interfaces.EvalFlags.Ternary;
import org.matheclipse.core.interfaces.EvalFlags.Trait;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Structural invariants of the {@link EvalFlags} vocabulary. These guard the bit layout itself, so
 * that renumbering the flags or adding a new one cannot silently break an existing mask.
 */
public class EvalFlagsTest {

  @BeforeEach
  public void setUp() throws Exception {
    F.await();
  }

  @Test
  public void testEveryFlagIsASingleBit() {
    for (Flag flag : Flag.values()) {
      assertEquals(1, Integer.bitCount(flag.mask()),
          () -> String.format("%s is 0x%08X, which is not a single bit", flag, flag.mask()));
    }
  }

  @Test
  public void testFlagsArePairwiseDisjoint() {
    for (Flag left : Flag.values()) {
      for (Flag right : Flag.values()) {
        if (left != right && left.mask() == right.mask()) {
          assertEquals(left, right, String.format("%s and %s share the bit 0x%08X", left, right,
              left.mask()));
        }
      }
    }
  }

  /**
   * {@code Mask.ALL} is spelled out by hand, so check it really is the union of the flags - the
   * serialization masks and the free-bit alarm below both depend on it.
   */
  @Test
  public void testMaskAllIsTheUnionOfAllFlags() {
    int union = EvalFlags.Mask.NONE;
    for (Flag flag : Flag.values()) {
      union |= flag.mask();
    }
    assertEquals(EvalFlags.Mask.ALL, union,
        String.format("expected 0x%08X but was 0x%08X", EvalFlags.Mask.ALL, union));
    assertEquals(Flag.values().length, Integer.bitCount(union), "one flag per bit");
  }

  /**
   * The bit budget alarm. There is exactly one bit left; a new flag needs a design conversation,
   * not a spare bit.
   */
  @Test
  public void testAtLeastOneBitIsStillFree() {
    int free = ~EvalFlags.Mask.ALL;
    assertNotEquals(0, free, "the evaluation flag word is full");
    assertTrue((free & 0x80000000) != 0,
        String.format("expected bit 31 to be free, free bits are 0x%08X", free));
  }

  @Test
  public void testAFlagNeverClearsItself() {
    for (Flag flag : Flag.values()) {
      assertEquals(0, flag.mask() & flag.clears(),
          () -> flag + " clears its own bit, so it could never be set");
      assertEquals(flag.clears(), flag.clears() & EvalFlags.Mask.ALL,
          () -> flag + " clears a bit which is not assigned to any flag");
    }
  }

  /** Exclusion has to be symmetric, otherwise the contradictory state is reachable from one side. */
  @Test
  public void testExclusionIsSymmetric() {
    for (Flag left : Flag.values()) {
      for (Flag right : Flag.values()) {
        if (left != right && (left.clears() & right.mask()) != 0) {
          assertTrue((right.clears() & left.mask()) != 0, String
              .format("%s excludes %s, but %s does not exclude %s", left, right, right, left));
        }
      }
    }
  }

  @Test
  public void testGroupsAreTheUnionOfTheirMembers() {
    assertEquals(
        Flag.CONTAINS_PATTERN.mask() | Flag.CONTAINS_PATTERN_SEQUENCE.mask()
            | Flag.CONTAINS_DEFAULT_PATTERN.mask(),
        Group.PATTERN_EXPR.mask(), "PATTERN_EXPR");
    assertEquals(Flag.IS_MATRIX.mask() | Flag.IS_VECTOR.mask(), Group.MATRIX_OR_VECTOR.mask(),
        "MATRIX_OR_VECTOR");
    assertEquals(Flag.IS_FLATTENED.mask() | Flag.IS_SORTED.mask(),
        Group.FLATTENED_OR_SORTED.mask(), "FLATTENED_OR_SORTED");
    assertEquals(
        Flag.IS_NUMERIC_FUNCTION.mask() | Flag.IS_NOT_NUMERIC_FUNCTION.mask()
            | Flag.IS_NUMERIC_FUNCTION_OR_LIST.mask()
            | Flag.IS_NOT_NUMERIC_FUNCTION_OR_LIST.mask() | Flag.IS_NUMERIC_CONSTANT.mask()
            | Flag.IS_NOT_NUMERIC_CONSTANT.mask(),
        Group.NUMERIC.mask(), "NUMERIC");
    assertEquals(Flag.IS_LISTABLE_THREADED.mask() | Flag.CONTAINS_NO_SPECIAL_ARG.mask(),
        Group.ARGUMENTS_CHANGED.mask(), "ARGUMENTS_CHANGED");

    for (Group group : Group.values()) {
      assertEquals(group.mask(), group.mask() & EvalFlags.Mask.ALL,
          () -> group + " contains a bit which is not assigned to any flag");
    }
  }

  /**
   * The flags are written with {@code writeShort()}, so anything above bit 16 cannot survive a
   * round trip.
   */
  @Test
  public void testPersistentGroupFitsInAShort() {
    assertTrue(Group.PERSISTENT.mask() < 0x8000,
        "PERSISTENT must stay below the sign bit of the short it is written as");
    assertEquals(Group.PERSISTENT.mask(), Group.PERSISTENT.mask() & 0xFFFF,
        "PERSISTENT does not fit into the short it is written as");
    assertTrue(Integer.bitCount(Group.PERSISTENT.mask() + 1) == 1,
        String.format("PERSISTENT should be the contiguous low block, but is 0x%08X",
            Group.PERSISTENT.mask()));

    // the flags which genuinely cannot be recomputed after deserialization
    assertTrue((Group.PERSISTENT.mask() & Group.PATTERN_EXPR.mask()) == Group.PATTERN_EXPR.mask(),
        "the pattern flags must survive - isPatternExpr() never recomputes them");
    assertTrue(
        (Group.PERSISTENT.mask() & Group.FLATTENED_OR_SORTED.mask()) == Group.FLATTENED_OR_SORTED
            .mask(),
        "IS_FLATTENED/IS_SORTED must survive - a rule LHS would be re-evaluated otherwise");
    assertTrue((Group.PERSISTENT.mask() & Flag.IS_DECOMPOSED_PARTIAL_FRACTION.mask()) != 0,
        "IS_DECOMPOSED_PARTIAL_FRACTION must survive - Integrate() reads it as a guard");
    assertTrue((Group.PERSISTENT.mask() & Flag.TIMES_PARSED_IMPLICIT.mask()) != 0,
        "TIMES_PARSED_IMPLICIT must survive - only the parser can know it");
  }

  /**
   * A numeric collision between two namespaces is not a bug by itself, but these two are read in
   * the same method - {@code EvalEngine#threadASTListArgs} memoizes the flag while consulting the
   * attribute - so a collision between them is a trap.
   */
  @Test
  public void testListableThreadedDoesNotCollideWithNonThreadable() {
    assertNotEquals(ISymbol.NONTHREADABLE, Flag.IS_LISTABLE_THREADED.mask(),
        "IS_LISTABLE_THREADED and the NonThreadable attribute must stay distinguishable");
  }



  // ---- behaviour of the new API ----------------------------------------------------------

  @Test
  public void testAddFlagClearsTheContradictingFlag() {
    for (Trait trait : Trait.values()) {
      Flag yes = flagWithMask(trait.yesMask());
      Flag no = flagWithMask(trait.noMask());

      IAST ast = F.List(F.a, F.b);
      ast.addFlag(yes);
      assertEquals(Ternary.TRUE, ast.getTrait(trait), trait.name());
      assertTrue(ast.hasFlag(yes));
      assertFalse(ast.hasFlag(no));

      ast.addFlag(no);
      assertEquals(Ternary.FALSE, ast.getTrait(trait), trait.name());
      assertFalse(ast.hasFlag(yes), trait + ": the contradicting flag was not cleared");
      assertTrue(ast.hasFlag(no));
    }
  }

  @Test
  public void testTraitRoundTrip() {
    for (Trait trait : Trait.values()) {
      IAST ast = F.List(F.a, F.b);
      assertEquals(Ternary.UNKNOWN, ast.getTrait(trait), trait.name());

      ast.setTrait(trait, true);
      assertEquals(Ternary.TRUE, ast.getTrait(trait), trait.name());

      ast.setTrait(trait, false);
      assertEquals(Ternary.FALSE, ast.getTrait(trait), trait.name());
      assertEquals(0, ast.getEvalFlagBits() & trait.yesMask(), trait + ": both bits were set");

      ast.setTrait(trait, Ternary.UNKNOWN);
      assertEquals(Ternary.UNKNOWN, ast.getTrait(trait), trait.name());
      assertEquals(0, ast.getEvalFlagBits() & trait.mask(), trait.name());
    }
  }

  @Test
  public void testContainsNoPatternIsExclusiveWithThePatternFlags() {
    IAST ast = F.List(F.a, F.b);
    ast.addFlag(Flag.CONTAINS_PATTERN).addFlag(Flag.CONTAINS_DEFAULT_PATTERN);
    assertTrue(ast.hasAnyFlag(Group.PATTERN_EXPR));

    ast.addFlag(Flag.CONTAINS_NO_PATTERN);
    assertFalse(ast.hasAnyFlag(Group.PATTERN_EXPR),
        "CONTAINS_NO_PATTERN has to clear the pattern flags");
    assertFalse(ast.hasFlag(Flag.CONTAINS_ALL_DEFAULT_PATTERN));

    ast.addFlag(Flag.CONTAINS_PATTERN);
    assertFalse(ast.hasFlag(Flag.CONTAINS_NO_PATTERN), "and the other way round");
  }

  @Test
  public void testAnyAllAndNoneAreDistinct() {
    IAST ast = F.List(F.a, F.b);
    ast.addFlag(Flag.IS_MATRIX);

    assertTrue(ast.hasAnyFlag(Group.MATRIX_OR_VECTOR));
    assertFalse(ast.hasAllFlags(Group.MATRIX_OR_VECTOR));
    assertFalse(ast.hasNoFlag(Group.MATRIX_OR_VECTOR));

    ast.addFlag(Flag.IS_VECTOR);
    assertTrue(ast.hasAllFlags(Group.MATRIX_OR_VECTOR));

    ast.clearFlags(Group.MATRIX_OR_VECTOR);
    assertTrue(ast.hasNoFlag(Group.MATRIX_OR_VECTOR));
    assertFalse(ast.hasAnyFlag(Group.MATRIX_OR_VECTOR));
  }

  @Test
  public void testCopyFlagsFromAndSameFlags() {
    IAST source = F.List(F.a, F.b).addFlag(Flag.IS_MATRIX).addFlag(Flag.IS_HASH_EVALED);
    IAST target = F.List(F.c, F.d).addFlag(Flag.IS_SORTED);

    target.copyFlagsFrom(source, Group.MATRIX_OR_VECTOR);
    assertTrue(target.hasFlag(Flag.IS_MATRIX), "the group has to be copied");
    assertFalse(target.hasFlag(Flag.IS_HASH_EVALED), "only the group may be copied");
    assertTrue(target.hasFlag(Flag.IS_SORTED), "existing flags have to survive");

    assertTrue(source.sameFlags(target, Group.MATRIX_OR_VECTOR));
    assertFalse(source.sameFlags(target, Group.FLATTENED_OR_SORTED));
  }

  @Test
  public void testClearFlagAndResetFlags() {
    IAST ast = F.List(F.a, F.b).addFlags(Flag.IS_MATRIX, Flag.IS_HASH_EVALED);
    assertTrue(ast.hasFlag(Flag.IS_MATRIX));
    assertTrue(ast.hasFlag(Flag.IS_HASH_EVALED));

    ast.clearFlag(Flag.IS_HASH_EVALED);
    assertFalse(ast.hasFlag(Flag.IS_HASH_EVALED));
    assertTrue(ast.hasFlag(Flag.IS_MATRIX));

    ast.resetFlags();
    assertEquals(EvalFlags.Mask.NONE, ast.getEvalFlagBits());
  }

  /** Atoms have no flags, and the enum API must be a silent no-op for them. */
  @Test
  public void testAtomsAreUnaffected() {
    assertFalse(F.C1.hasFlag(Flag.IS_MATRIX));
    assertFalse(F.C1.hasAnyFlag(Group.PATTERN_EXPR));
    assertTrue(F.C1.hasNoFlag(Group.PATTERN_EXPR));
    assertEquals(F.C1, F.C1.addFlag(Flag.IS_MATRIX));
    assertFalse(F.C1.hasFlag(Flag.IS_MATRIX));
  }

  private static Flag flagWithMask(int mask) {
    for (Flag flag : Flag.values()) {
      if (flag.mask() == mask) {
        return flag;
      }
    }
    throw new AssertionError(String.format("no flag for the bit 0x%08X", mask));
  }
}
