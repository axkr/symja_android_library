/*
 * Copyright 2005-2008 Axel Kramer (axelclk@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.matheclipse.parser.client.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * A list of <code>ASTNode</code>'s which represents a parsed function.<br/s>
 * The head of the function (i.e. Sin, Cos, Times,...) is stored in the 0-th index of the list.<br/>
 * The arguments of the function are stored in the 1...n-th index of the list.
 */
public class FunctionNode extends ASTNode implements java.util.List<ASTNode> {
	private ArrayList<ASTNode> fNodesList;

	public FunctionNode(final ASTNode head) {
		super(null);
		fNodesList = new ArrayList<ASTNode>(5);
		fNodesList.add(head);
	}

	public FunctionNode(final SymbolNode head, final ASTNode arg0) {
		super(null);
		fNodesList = new ArrayList<ASTNode>(3);
		fNodesList.add(head);
		fNodesList.add(arg0);
	}

	public FunctionNode(final SymbolNode head, final ASTNode arg0, final ASTNode arg1) {
		super(null);
		fNodesList = new ArrayList<ASTNode>(3);
		fNodesList.add(head);
		fNodesList.add(arg0);
		fNodesList.add(arg1);
	}

	public void add(final int index, final ASTNode element) {
		fNodesList.add(index, element);
	}

	public boolean add(final ASTNode e) {
		return fNodesList.add(e);
	}

	public boolean addAll(final Collection<? extends ASTNode> c) {
		return fNodesList.addAll(c);
	}

	public boolean addAll(final int index, final Collection<? extends ASTNode> c) {
		return fNodesList.addAll(index, c);
	}

	public void clear() {
		fNodesList.clear();
	}

	public boolean contains(final Object o) {
		return fNodesList.contains(o);
	}

	public boolean containsAll(final Collection<?> c) {
		return fNodesList.containsAll(c);
	}

	public void ensureCapacity(final int minCapacity) {
		fNodesList.ensureCapacity(minCapacity);
	}

	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof FunctionNode) {
			return fNodesList.equals(((FunctionNode) obj).fNodesList);
		}
		return false;
	}

	public ASTNode get(final int index) {
		return fNodesList.get(index);
	}

	public ASTNode getNode(final int index) {
		return fNodesList.get(index);
	}

	public int hashCode() {
		return fNodesList.hashCode();
	}

	public int indexOf(final Object o) {
		return fNodesList.indexOf(o);
	}

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

	public Iterator<ASTNode> iterator() {
		return fNodesList.iterator();
	}

	public int lastIndexOf(final Object o) {
		return fNodesList.lastIndexOf(o);
	}

	public ListIterator<ASTNode> listIterator() {
		return fNodesList.listIterator();
	}

	public ListIterator<ASTNode> listIterator(final int index) {
		return fNodesList.listIterator(index);
	}

	public ASTNode remove(final int index) {
		return fNodesList.remove(index);
	}

	public boolean remove(final Object o) {
		return fNodesList.remove(o);
	}

	public boolean removeAll(final Collection<?> c) {
		return fNodesList.removeAll(c);
	}

	public boolean retainAll(final Collection<?> c) {
		return fNodesList.retainAll(c);
	}

	public ASTNode set(final int index, final ASTNode element) {
		return fNodesList.set(index, element);
	}

	public int size() {
		return fNodesList.size();
	}

	/**
	 * Because GWT doesn't support the subList() method, we also throw an UnsupportedOperationException
	 */
	public List<ASTNode> subList(final int fromIndex, final int toIndex) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Arraylist#subList() not supported");
	}

	public Object[] toArray() {
		return fNodesList.toArray();
	}

	public Object[] toArray(final Object[] a) {
		return fNodesList.toArray(a);
	}

	public String toString() {
		ASTNode head = fNodesList.get(0);
		final StringBuffer buf = new StringBuffer();
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

	public boolean dependsOn(String variableName) {
		for (int i = 1; i < size(); i++) {
			if (get(i).dependsOn(variableName)) {
				return true;
			}
		}
		return false;
	}

}
