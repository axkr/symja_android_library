package org.matheclipse.core.interfaces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

/**
 * {@link IAST#asList()} is the only public entry to the {@code java.util.List} view, so these tests
 * go through it alone: index 0 is the head, the arguments come from {@link IAST#getRule(int)}, and
 * nothing can be changed through the view.
 */
class ASTListViewTest {

  @BeforeAll
  static void initSymja() {
    F.initSymbols();
  }

  @Test
  void headAtIndexZero() {
    List<IExpr> view = F.Plus(F.x, F.y).asList();
    assertEquals(3, view.size());
    assertFalse(view.isEmpty());
    assertSame(S.Plus, view.get(0));
    assertSame(F.x, view.get(1));
    assertSame(F.y, view.get(2));
    assertEquals(0, view.indexOf(S.Plus));
    assertEquals(2, view.lastIndexOf(F.y));
    assertTrue(view.contains(F.y));
    assertFalse(view.contains(F.z));
    assertEquals("[Plus, x, y]", view.toString());
    assertEquals(Arrays.asList(S.Plus, F.x, F.y),
        view.stream().collect(Collectors.toList()));
    assertEquals(Arrays.asList(S.Plus, F.x, F.y), new ArrayList<>(view));
    assertEquals(Arrays.asList(F.x, F.y), view.subList(1, view.size()));
  }

  @Test
  void headOnly() {
    List<IExpr> view = F.List().asList();
    assertEquals(1, view.size());
    assertSame(S.List, view.get(0));
    assertTrue(view.subList(1, 1).isEmpty());
  }

  @Test
  void boundsAreChecked() {
    List<IExpr> ast3 = F.List(F.C1, F.C2, F.C3).asList();
    assertThrows(IndexOutOfBoundsException.class, () -> ast3.get(4));
    assertThrows(IndexOutOfBoundsException.class, () -> ast3.get(-1));

    IASTAppendable rrb = F.ListAlloc(8);
    for (int i = 1; i <= 40; i++) {
      rrb.append(F.ZZ(i));
    }
    List<IExpr> tree = rrb.asList();
    assertEquals(41, tree.size());
    assertEquals(F.ZZ(40), tree.get(40));
    assertThrows(IndexOutOfBoundsException.class, () -> tree.get(41));

    // NIL has size 0, so its view is an empty list without a head
    List<IExpr> nil = F.NIL.asList();
    assertTrue(nil.isEmpty());
    assertThrows(IndexOutOfBoundsException.class, () -> nil.get(0));
  }

  @Test
  void associationShowsRules() {
    IAssociation assoc = F.assoc(F.List(F.Rule(F.a, F.C1), F.Rule(F.b, F.C2)));
    List<IExpr> view = assoc.asList();
    assertEquals(3, view.size());
    assertSame(S.Association, view.get(0));
    assertTrue(view.get(1).isRuleAST());
    assertEquals(F.Rule(F.a, F.C1), view.get(1));
    assertEquals(F.Rule(F.b, F.C2), view.get(2));
    // whereas get(int) on the association itself yields the value
    assertEquals(F.C1, assoc.get(1));
  }

  @Test
  void backedNotCopied() {
    IASTAppendable ast = F.ListAlloc(4);
    List<IExpr> view = ast.asList();
    assertEquals(1, view.size());
    ast.append(F.C1);
    assertEquals(2, view.size());
    assertEquals(F.C1, view.get(1));
  }

  @Test
  void readOnly() {
    IASTAppendable ast = F.ListAlloc(4);
    ast.append(F.C1);
    ast.append(F.C2);
    List<IExpr> view = ast.asList();
    assertThrows(UnsupportedOperationException.class, () -> view.set(1, F.C3));
    assertThrows(UnsupportedOperationException.class, () -> view.add(F.C3));
    assertThrows(UnsupportedOperationException.class, () -> view.add(1, F.C3));
    assertThrows(UnsupportedOperationException.class, () -> view.remove(1));
    assertThrows(UnsupportedOperationException.class, () -> view.remove(F.C1));
    assertThrows(UnsupportedOperationException.class, () -> view.clear());
    assertThrows(UnsupportedOperationException.class, () -> view.subList(1, 2).clear());
    assertThrows(UnsupportedOperationException.class, () -> view.sort(null));
    assertThrows(UnsupportedOperationException.class, () -> view.replaceAll(x -> F.C0));
    assertThrows(UnsupportedOperationException.class, () -> view.removeIf(x -> true));
    assertThrows(UnsupportedOperationException.class, () -> {
      java.util.Iterator<IExpr> it = view.iterator();
      it.next();
      it.remove();
    });
    // nothing changed underneath
    assertEquals(F.List(F.C1, F.C2), ast);
  }

  @Test
  void listEquality() {
    List<IExpr> view = F.List(F.C1).asList();
    List<IExpr> plain = Arrays.asList(S.List, F.C1);
    assertEquals(plain, view);
    assertEquals(view, plain);
    assertEquals(plain.hashCode(), view.hashCode());
  }
}
