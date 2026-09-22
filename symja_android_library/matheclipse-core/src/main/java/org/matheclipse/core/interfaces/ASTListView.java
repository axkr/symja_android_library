package org.matheclipse.core.interfaces;

import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * A read-only {@link java.util.List} view of an {@link IAST}, returned by {@link IAST#asList()}.
 *
 * <p>
 * Index {@code 0} is the {@link IAST#head() head}, indices {@code 1 .. argSize()} are the
 * arguments as {@link IAST#getRule(int)} returns them - for an {@link IAssociation} this is the
 * stored {@code Rule(key, value)}, not the value {@link IAST#get(int)} would give. The view is
 * backed by the AST: it copies nothing and shows later changes of an {@link IASTAppendable}, but it
 * is not fail-fast. Every mutator throws {@link UnsupportedOperationException}, as the lists from
 * {@link java.util.Collections#unmodifiableList(java.util.List)} do; {@code subList(1, size())}
 * is the arguments alone and {@code new ArrayList<>(view)} a mutable copy including the head.
 */
final class ASTListView extends AbstractList<IExpr> implements RandomAccess {
  private final IAST ast;

  ASTListView(IAST ast) {
    this.ast = ast;
  }

  @Override
  public IExpr get(int index) {
    int size = ast.size();
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    return index == 0 ? ast.head() : ast.getRule(index);
  }

  @Override
  public int size() {
    return ast.size();
  }
}
