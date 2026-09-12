package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.IASTAppendable;

/**
 * The allocation census must be silent when it is switched off and must tell the two kinds of
 * growth apart when it is on.
 */
class AstAllocationStatsTest {

  @BeforeAll
  static void initSymja() {
    F.initSymbols();
  }

  /** Run {@code work} with the counters enabled and reset, restoring the flag afterwards. */
  private static void withStats(Runnable work) {
    final boolean enabled = Config.AST_ALLOCATION_STATISTICS;
    try {
      Config.AST_ALLOCATION_STATISTICS = true;
      AstAllocationStats.reset();
      work.run();
    } finally {
      Config.AST_ALLOCATION_STATISTICS = enabled;
    }
  }

  @Test
  void testNothingIsRecordedWhileDisabled() {
    final boolean enabled = Config.AST_ALLOCATION_STATISTICS;
    try {
      Config.AST_ALLOCATION_STATISTICS = true;
      AstAllocationStats.reset();
      Config.AST_ALLOCATION_STATISTICS = false;

      IASTAppendable list = F.ast(S.List, 4);
      for (int i = 0; i < 100; i++) {
        list.append(F.ZZ(i));
      }

      assertEquals(0, AstAllocationStats.createdArray());
      assertEquals(0, AstAllocationStats.reallocations());
    } finally {
      Config.AST_ALLOCATION_STATISTICS = enabled;
    }
  }

  @Test
  void testAppendingPastTheHintIsRecordedAsAReallocation() {
    withStats(() -> {
      IASTAppendable list = F.ast(S.List, 4);
      assertTrue(AstAllocationStats.createdArray() >= 1, "the creation was not counted");
      long before = AstAllocationStats.reallocations();
      for (int i = 0; i < 200; i++) {
        list.append(F.ZZ(i));
      }
      assertTrue(AstAllocationStats.reallocations() > before,
          "growing far past the hint recorded no reallocation");
      assertTrue(AstAllocationStats.reallocatedElements() > 0);
      assertEquals(201, list.size());
    });
  }

  @Test
  void testStayingInsideTheHintRecordsNoReallocation() {
    withStats(() -> {
      IASTAppendable list = F.ast(S.List, 20);
      for (int i = 0; i < 20; i++) {
        list.append(F.ZZ(i));
      }
      assertEquals(0, AstAllocationStats.reallocations(),
          "a list which stayed inside its hint reallocated");
    });
  }

  /** Above {@link Config#MIN_LIMIT_PERSISTENT_LIST} the hint selects an {@link ASTRRBTree}. */
  @Test
  void testTheRepresentationChoiceIsRecorded() {
    withStats(() -> {
      F.ast(S.List, Config.MIN_LIMIT_PERSISTENT_LIST + 1);
      assertEquals(1, AstAllocationStats.createdRrb());
    });
    withStats(() -> {
      F.ast(S.List, Config.MIN_LIMIT_PERSISTENT_LIST);
      assertEquals(0, AstAllocationStats.createdRrb());
      assertTrue(AstAllocationStats.createdArray() >= 1);
    });
  }

  @Test
  void testBucketsAreTheHalfOpenPowerOfTwoRanges() {
    assertEquals(0, AstAllocationStats.bucket(0));
    assertEquals(0, AstAllocationStats.bucket(-1));
    assertEquals(1, AstAllocationStats.bucket(1));
    assertEquals(2, AstAllocationStats.bucket(2));
    assertEquals(2, AstAllocationStats.bucket(3));
    assertEquals(3, AstAllocationStats.bucket(4));
    assertEquals(3, AstAllocationStats.bucket(7));
    assertEquals(4, AstAllocationStats.bucket(8));
    assertEquals(11, AstAllocationStats.bucket(1024));
    assertTrue(AstAllocationStats.bucket(Integer.MAX_VALUE) < 40);
  }

  @Test
  void testReportRendersWithoutData() {
    withStats(() -> {
      String report = AstAllocationStats.report(5);
      assertTrue(report.contains("AstAllocationStats"), report);
      assertTrue(report.contains("reallocations="), report);
    });
  }
}
