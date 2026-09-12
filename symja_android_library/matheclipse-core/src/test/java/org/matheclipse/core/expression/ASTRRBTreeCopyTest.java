package org.matheclipse.core.expression;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * {@link ASTRRBTree} copies share their tree spine with the original instead of rebuilding it
 * element by element. Sharing is only correct because the Paguro nodes are persistent, so these
 * tests are what discharges that assumption for this code base - they mutate both sides of a copy
 * and require each to be invisible to the other.
 *
 * <p>
 * The sizes are chosen around Paguro's node width of 32: a list which fits in the focus buffer
 * exercises a different path than one which has been pushed into the tree.
 */
class ASTRRBTreeCopyTest {

  @BeforeAll
  static void initSymja() {
    F.initSymbols();
  }

  /** A {@link ASTRRBTree} with head {@link S#List} and the arguments {@code 1 .. size}. */
  private static ASTRRBTree rrbList(int size) {
    ASTRRBTree result = ASTRRBTree.newInstance(size, S.List);
    for (int i = 1; i <= size; i++) {
      result.append(F.ZZ(i));
    }
    assertEquals(size, result.argSize());
    return result;
  }

  /**
   * Assert that {@code ast} is exactly {@code List(1, 2, .., size)}. Element by element rather than
   * by {@code toString()}, which wraps at the page width.
   */
  private static void assertIsRange(IAST ast, int size, String message) {
    assertEquals(S.List, ast.head(), message + ": head");
    assertEquals(size, ast.argSize(), message + ": size");
    for (int i = 1; i <= size; i++) {
      assertEquals(F.ZZ(i), ast.get(i), message + ": element " + i);
    }
  }

  @Test
  void testAppendToCopyLeavesOriginalAlone() {
    for (int size : new int[] {5, 32, 33, 100, 1000}) {
      ASTRRBTree original = rrbList(size);
      IASTAppendable copy = original.copyAppendable();
      assertNotSame(original, copy);

      copy.append(F.ZZ(size + 1));

      assertEquals(size + 1, copy.argSize(), "copy did not grow for size " + size);
      assertIsRange(original, size, "original after appending to the copy, size " + size);
    }
  }

  @Test
  void testAppendToOriginalLeavesCopyAlone() {
    for (int size : new int[] {5, 32, 33, 100, 1000}) {
      ASTRRBTree original = rrbList(size);
      IASTAppendable copy = original.copyAppendable();

      original.append(F.ZZ(size + 1));

      assertEquals(size + 1, original.argSize());
      assertIsRange(copy, size, "copy after appending to the original, size " + size);
    }
  }

  @Test
  void testSetOnEitherSideIsNotVisibleOnTheOther() {
    for (int size : new int[] {5, 32, 33, 100, 1000}) {
      // touch the front, the middle and the back: only the middle and back reach the shared spine,
      // the front may still sit in the focus buffer
      for (int position : new int[] {1, size / 2 + 1, size}) {
        ASTRRBTree original = rrbList(size);
        IASTAppendable copy = original.copyAppendable();

        copy.set(position, F.CN1);
        assertEquals(F.ZZ(position), original.get(position),
            "set on the copy reached the original at size " + size + " position " + position);
        assertEquals(F.CN1, copy.get(position));

        original.set(position, F.CN2);
        assertEquals(F.CN1, copy.get(position),
            "set on the original reached the copy at size " + size + " position " + position);
        assertEquals(F.CN2, original.get(position));
      }
    }
  }

  /**
   * More than 32 appends after the copy, so both sides push a full focus buffer into the tree and
   * rebuild their spine while sharing the nodes below it.
   */
  @Test
  void testBothSidesGrowPastANodeBoundaryIndependently() {
    ASTRRBTree original = rrbList(100);
    IASTAppendable copy = original.copyAppendable();

    for (int i = 0; i < 40; i++) {
      original.append(F.C0);
      copy.append(F.C1);
    }

    assertEquals(140, original.argSize());
    assertEquals(140, copy.argSize());
    for (int i = 101; i <= 140; i++) {
      assertEquals(F.C0, original.get(i), "original was overwritten at " + i);
      assertEquals(F.C1, copy.get(i), "copy was overwritten at " + i);
    }
    for (int i = 1; i <= 100; i++) {
      assertEquals(F.ZZ(i), original.get(i));
      assertEquals(F.ZZ(i), copy.get(i));
    }
  }

  @Test
  void testRemoveOnTheCopyLeavesOriginalAlone() {
    for (int size : new int[] {33, 100, 1000}) {
      ASTRRBTree original = rrbList(size);
      IASTAppendable copy = original.copyAppendable();

      copy.remove(size / 2 + 1);

      assertEquals(size - 1, copy.argSize());
      assertIsRange(original, size, "original after removing from the copy, size " + size);
    }
  }

  /** A copy of a copy must be independent of both. */
  @Test
  void testChainOfCopiesStaysIndependent() {
    ASTRRBTree original = rrbList(200);
    IASTAppendable first = original.copyAppendable();
    IASTAppendable second = first.copyAppendable();

    first.set(50, F.CN1);
    second.set(50, F.CN2);

    assertEquals(F.ZZ(50), original.get(50));
    assertEquals(F.CN1, first.get(50));
    assertEquals(F.CN2, second.get(50));
  }

  /** The copy must carry the same elements, not merely the same size. */
  @Test
  void testCopyHasTheSameContents() {
    for (int size : new int[] {5, 32, 33, 100, 1000}) {
      ASTRRBTree original = rrbList(size);
      IASTAppendable copy = original.copyAppendable();

      assertEquals(original.head(), copy.head());
      assertEquals(original.argSize(), copy.argSize());
      for (int i = 0; i < original.size(); i++) {
        IExpr expectedElement = original.getRule(i);
        assertEquals(expectedElement, copy.getRule(i), "element " + i + " at size " + size);
      }
      assertEquals(original, copy);
    }
  }
}
