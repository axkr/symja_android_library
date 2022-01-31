/*
 * Copyright 2005-2008 Axel Kramer (axelclk@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.matheclipse.parser.client.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A list of <code>ASTNode</code>'s which represents a parsed function.<br/s> The head of the
 * function (i.e. Sin, Cos, Times,...) is stored in the 0-th index of the list.<br>
 * The arguments of the function are stored in the 1...n-th index of the list.
 */
public final class FunctionNode extends ASTNode implements java.util.List<ASTNode> {

  private final ArrayList<ASTNode> fNodesList;

  public FunctionNode(final ASTNode head) {
    super(null);
    fNodesList = new ArrayList<ASTNode>(5);
    fNodesList.add(head);
  }

  public FunctionNode(final ASTNode head, final ASTNode arg0) {
    super(null);
    fNodesList = new ArrayList<ASTNode>(2);
    fNodesList.add(head);
    fNodesList.add(arg0);
  }

  public FunctionNode(final ASTNode head, final ASTNode arg0, final ASTNode arg1) {
    super(null);
    fNodesList = new ArrayList<ASTNode>(3);
    fNodesList.add(head);
    fNodesList.add(arg0);
    fNodesList.add(arg1);
  }

  public FunctionNode(final ASTNode head, final ASTNode arg0, final ASTNode arg1,
      final ASTNode arg2) {
    super(null);
    fNodesList = new ArrayList<ASTNode>(4);
    fNodesList.add(head);
    fNodesList.add(arg0);
    fNodesList.add(arg1);
    fNodesList.add(arg2);
  }

  @Override
  public boolean add(final ASTNode e) {
    return fNodesList.add(e);
  }

  @Override
  public void add(final int index, final ASTNode element) {
    fNodesList.add(index, element);
  }

  @Override
  public boolean addAll(final Collection<? extends ASTNode> c) {
    return fNodesList.addAll(c);
  }

  @Override
  public boolean addAll(final int index, final Collection<? extends ASTNode> c) {
    return fNodesList.addAll(index, c);
  }

  @Override
  public void clear() {
    fNodesList.clear();
  }

  @Override
  public boolean contains(final Object o) {
    return fNodesList.contains(o);
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    return fNodesList.containsAll(c);
  }

  @Override
  public boolean dependsOn(String variableName) {
    for (int i = 1; i < size(); i++) {
      if (get(i).dependsOn(variableName)) {
        return true;
      }
    }
    return false;
  }

  public void ensureCapacity(final int minCapacity) {
    fNodesList.ensureCapacity(minCapacity);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof FunctionNode) {
      return fNodesList.equals(((FunctionNode) obj).fNodesList);
    }
    return false;
  }

  @Override
  public ASTNode get(final int index) {
    return fNodesList.get(index);
  }

  public ASTNode getNode(final int index) {
    return fNodesList.get(index);
  }

  @Override
  public int hashCode() {
    return fNodesList.hashCode();
  }

  @Override
  public int indexOf(final Object o) {
    return fNodesList.indexOf(o);
  }

  @Override
  public boolean isEmpty() {
    return fNodesList.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFree(final ASTNode node) {
    if (this.equals(node)) {
      return false;
    }
    for (int i = 0; i < fNodesList.size(); i++) {
      if (!fNodesList.get(i).isFree(node)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Iterator<ASTNode> iterator() {
    return fNodesList.iterator();
  }

  @Override
  public int lastIndexOf(final Object o) {
    return fNodesList.lastIndexOf(o);
  }

  @Override
  public ListIterator<ASTNode> listIterator() {
    return fNodesList.listIterator();
  }

  @Override
  public ListIterator<ASTNode> listIterator(final int index) {
    return fNodesList.listIterator(index);
  }

  @Override
  public ASTNode remove(final int index) {
    return fNodesList.remove(index);
  }

  @Override
  public boolean remove(final Object o) {
    return fNodesList.remove(o);
  }

  @Override
  public boolean removeAll(final Collection<?> c) {
    return fNodesList.removeAll(c);
  }

  @Override
  public boolean retainAll(final Collection<?> c) {
    return fNodesList.retainAll(c);
  }

  @Override
  public ASTNode set(final int index, final ASTNode element) {
    return fNodesList.set(index, element);
  }

  @Override
  public int size() {
    return fNodesList.size();
  }

  /**
   * Because GWT doesn't support the subList() method, we also throw an
   * UnsupportedOperationException
   */
  @Override
  public List<ASTNode> subList(final int fromIndex, final int toIndex)
      throws UnsupportedOperationException {
    throw new UnsupportedOperationException("FunctionNode#subList() not supported");
  }

  @Override
  public Object[] toArray() {
    return fNodesList.toArray();
  }

  @Override
  public Object[] toArray(final Object[] a) {
    return fNodesList.toArray(a);
  }

  @Override
  public String toString() {
    ASTNode head = fNodesList.get(0);
    final StringBuilder buf = new StringBuilder();
    if (head == null) {
      buf.append("<null-tag>");
    } else {
      buf.append(head.toString());
    }
    ASTNode temp;
    if (head instanceof FunctionNode) {
      buf.append('[');
    } else {
      buf.append('(');
    }
    for (int i = 1; i < size(); i++) {
      temp = get(i);
      buf.append(temp == this ? "(this ListNode)" : String.valueOf(temp));
      if (i < size() - 1) {
        buf.append(", ");
      }
    }
    if (head instanceof FunctionNode) {
      buf.append(']');
    } else {
      buf.append(')');
    }
    return buf.toString();
  }

  public void trimToSize() {
    fNodesList.trimToSize();
  }
}
