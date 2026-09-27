package org.matheclipse.core.interfaces;

import java.util.AbstractList;
import java.util.RandomAccess;

/**
 * A read-only {@link java.util.List} view of an {@link IAST}, returned by {@link IAST#asList()} and
 * {@link IAST#asArgsList()}.
 *
 * <p>
 * With an {@link #offset} of <code>0</code> the view starts at the {@link IAST#head() head},
 * with an offset of <code>1</code> at the first argument. The arguments are read through
 * {@link IAST#getRule(int)} - for an {@link IAssociation} this is the stored
 * <code>Rule(key, value)</code>, not the value {@link IAST#get(int)} would give. The view is
 * backed by the AST: it copies nothing and shows later changes of an {@link IASTAppendable}, but it
 * is not fail-fast. Every mutator throws {@link UnsupportedOperationException}, as the lists from
 * {@link java.util.Collections#unmodifiableList(java.util.List)} do; <code>new ArrayList&lt;&gt;(view)</code>
 * is a mutable copy.
 */
final class ASTListView extends AbstractList<IExpr> implements RandomAccess {
  private final IAST ast;

  /** <code>0</code>: the head is at index 0; <code>1</code>: the arguments alone. */
  private final int offset;

  ASTListView(IAST ast, int offset) {
    this.ast = ast;
    this.offset = offset;
  }

  @Override
  public IExpr get(int index) {
    int size = size();
    if (index < 0 || index >= size) {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }
    int position = index + offset;
    return position == 0 ? ast.head() : ast.getRule(position);
  }

  @Override
  public int size() {
    // F.NIL has size 0, so the arguments view of it must not report a negative size
    return Math.max(0, ast.size() - offset);
  }
}
