/*
 * Copyright (c) 2008-2014 Nicolai Diethelm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * (MIT License)
 */
package org.matheclipse.core.eval.util;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * An autocomplete data structure that enables you to quickly look up the top k
 * terms with a given prefix in a set of weighted terms. The structure is based
 * on a compressed trie of the terms (implemented as a randomized ternary search
 * tree) in which each node holds a weight-ordered list of the k
 * highest-weighted terms in its subtree.
 *
 * <p>
 * Note that this implementation is not synchronized. If multiple threads access
 * a Suggest Tree concurrently, and at least one of the threads modifies the
 * tree, it must be synchronized externally.
 */
public class SuggestTree {

    private final Random random = new Random();
    private final int k;
    private Node root;
    private int size;

    /**
     * Creates a Suggest Tree with the specified k-value.
     *
     * @param k
     *            the maximum number of auto-complete suggestions to return for a
     *            given prefix
     * @throws IllegalArgumentException
     *             if the specified k-value is less than 1
     */
    public SuggestTree(int k) {
        if (k < 1) {
            throw new IllegalArgumentException();
        }
        this.k = k;
        root = null;
        size = 0;
    }

    /**
     * Returns the k highest-weighted terms in the tree that start with the
     * specified prefix, or null if there is no such term.
     *
     * @param prefix
     *            the prefix for which to return autocomplete suggestions
     * @return the k highest-weighted terms in the tree that start with the
     *         specified prefix, or null if there is no such term
     * @throws IllegalArgumentException
     *             if the specified prefix is an empty string
     * @throws NullPointerException
     *             if the specified prefix is null
     */
    public final Node getAutocompleteSuggestions(final String prefix) {
        return search(prefix);
    }

    /**
     * Returns the tree entry for the specified term, or null if there is no
     * such entry.
     *
     * @param term
     *            the term for which to return the corresponding tree entry
     * @return the tree entry for the specified term, or null if there is no
     *         such entry
     * @throws IllegalArgumentException
     *             if the specified term is an empty string
     * @throws NullPointerException
     *             if the specified term is null
     */
    public Entry getEntry(final String term) {
        Node n = search(term);
        if (n == null || n.charEnd > term.length()) {
            return null;
        } else {
            return n.entry;
        }
    }

    /**
     * Returns an iterator over the terms in the tree.
     *
     * @return an iterator over the terms in the tree
     */
    public final Iterator iterator() {
        return new Iterator();
    }

    /**
     * Returns the number of terms in the tree.
     *
     * @return the number of terms in the tree
     */
    public final int size() {
        return size;
    }

    /**
     * Inserts the specified term with the specified weight into the tree, or
     * reweights the term if it is already present.
     *
     * @param term
     *            the term to be inserted or reweighted
     * @param weight
     *            the weight to be assigned to the term
     * @throws IllegalArgumentException
     *             if the specified term is an empty string or much too long
     *             (longer than 32767 characters)
     * @throws NullPointerException
     *             if the specified term is null
     */
    public void put(String term, int weight) {
        if (term.isEmpty() || term.length() > Short.MAX_VALUE) {
            throw new IllegalArgumentException();
        }
        if (root == null) {
            root = new Node(term, weight, 0, null);
            finishInsertion(root);
            return;
        }
        int i = 0;
        Node n = root;
        while (true) {
            if (term.charAt(i) < n.firstChar) {
                if (n.left == null) {
                    n.left = new Node(term, weight, i, n);
                    finishInsertion(n.left);
                    return;
                } else {
                    n = n.left;
                }
            } else if (term.charAt(i) > n.firstChar) {
                if (n.right == null) {
                    n.right = new Node(term, weight, i, n);
                    finishInsertion(n.right);
                    return;
                } else {
                    n = n.right;
                }
            } else {
                while (++i < n.charEnd) {
                    if (i == term.length() || term.charAt(i) != n.charAt(i)) {
                        n = split(n, i);
                        break;
                    }
                }
                if (i < term.length()) {
                    if (n.mid == null) {
                        n.mid = new Node(term, weight, i, n);
                        finishInsertion(n.mid);
                        return;
                    } else {
                        n = n.mid;
                    }
                } else {
                    if (n.entry == null) {
                        n.entry = new Entry(term, weight);
                        finishInsertion(n);
                    } else if (n.entry.weight < weight) {
                        increaseWeight(n, weight);
                    } else if (n.entry.weight > weight) {
                        reduceWeight(n, weight);
                    }
                    return;
                }
            }
        }
    }

    /**
     * Removes the specified term from the tree.
     *
     * @param term
     *            the term to be removed
     * @throws IllegalArgumentException
     *             if the specified term is an empty string
     * @throws NullPointerException
     *             if the specified term is null
     */
    public void remove(final String term) {
        Node n = search(term);
        if (n == null || n.entry == null || n.charEnd > term.length()) {
            return;
        }
        randomizeDeletion(n);
        Entry e = n.entry;
        n.entry = null;
        if (n.mid == null) {
            Node p = parent(n);
            delete(n);
            n = p;
        }
        if (n != null && n.entry == null && n.mid.left == null
                && n.mid.right == null) {
            Node p = parent(n);
            merge(n, n.mid);
            n = p;
        }
        removeFromLists(e, n);
        size--;
    }

    private Node search(final String s) {
        if (s.isEmpty()) {
            throw new IllegalArgumentException();
        }
        int i = 0;
        Node n = root;
        while (n != null) {
            if (s.charAt(i) < n.firstChar) {
                n = n.left;
            } else if (s.charAt(i) > n.firstChar) {
                n = n.right;
            } else {
                while (++i < n.charEnd) {
                    if (i == s.length()) {
                        return n;
                    } else if (s.charAt(i) != n.charAt(i)) {
                        return null;
                    }
                }
                if (i == s.length()) {
                    return n;
                } else {
                    n = n.mid;
                }
            }
        }
        return null;
    }

    private Node split(Node n, final int position) {
        Node s = new Node(n, position);
        if (n.list.length == k) {
            s.list = Arrays.copyOf(n.list, k);
        } else {
            // the list is copied in insertIntoLists()
            s.list = n.list;
        }
        if (n.left != null) {
            n.left.up = s;
        }
        if (n.right != null) {
            n.right.up = s;
        }
        if (n == root) {
            root = s;
        } else if (n == n.up.left) {
            n.up.left = s;
        } else if (n == n.up.right) {
            n.up.right = s;
        } else {
            n.up.mid = s;
        }
        n.firstChar = n.charAt(position);
        n.left = n.right = null;
        n.up = s;
        return s;
    }

    private void merge(Node n, Node m) {
        m.firstChar = n.firstChar;
        m.left = n.left;
        m.right = n.right;
        m.up = n.up;
        if (n.left != null) {
            n.left.up = m;
        }
        if (n.right != null) {
            n.right.up = m;
        }
        if (n == root) {
            root = m;
        } else if (n == n.up.left) {
            n.up.left = m;
        } else if (n == n.up.right) {
            n.up.right = m;
        } else {
            n.up.mid = m;
        }
    }

    private void delete(Node n) {
        if (n == root) {
            root = null;
        } else if (n == n.up.left) {
            n.up.left = null;
        } else if (n == n.up.right) {
            n.up.right = null;
        } else {
            n.up.mid = null;
        }
    }

    private void finishInsertion(Node n) {
        randomizeInsertion(n);
        insertIntoLists(n);
        size++;
    }

    private void randomizeInsertion(Node n) {
        n.entry.priority = random.nextInt();
        n.priority = higherPriority(n.entry, n.mid);
        while (n != root && n.up.priority < n.priority) {
            if (n == n.up.left) {
                rotateRight(n.up);
            } else if (n == n.up.right) {
                rotateLeft(n.up);
            } else {
                n.up.priority = n.priority;
                n = n.up;
            }
        }
    }

    private void randomizeDeletion(Node n) {
        int p = n.entry.priority;
        n.entry.priority = Integer.MIN_VALUE;
        while (n != null && n.priority == p) {
            n.priority = higherPriority(n.entry, n.mid);
            Node h = higherPriorityNode(n.left, n.right);
            while (h != null && h.priority >= n.priority) {
                if (h == n.left) {
                    rotateRight(n);
                } else {
                    rotateLeft(n);
                }
                h = higherPriorityNode(n.left, n.right);
            }
            n = parent(n);
        }
    }

    private int higherPriority(Entry e, Node n) {
        if (e == null) {
            return n.priority;
        } else if (n == null) {
            return e.priority;
        } else if (e.priority < n.priority) {
            return n.priority;
        } else {
            return e.priority;
        }
    }

    private Node higherPriorityNode(Node n, Node m) {
        if (n == null) {
            return m;
        } else if (m == null) {
            return n;
        } else if (n.priority < m.priority) {
            return m;
        } else {
            return n;
        }
    }

    private void rotateLeft(Node n) {
        Node r = n.right;
        n.right = r.left;
        if (r.left != null) {
            r.left.up = n;
        }
        r.up = n.up;
        if (n == root) {
            root = r;
        } else if (n == n.up.left) {
            n.up.left = r;
        } else if (n == n.up.right) {
            n.up.right = r;
        } else {
            n.up.mid = r;
        }
        r.left = n;
        n.up = r;
    }

    private void rotateRight(Node n) {
        Node l = n.left;
        n.left = l.right;
        if (l.right != null) {
            l.right.up = n;
        }
        l.up = n.up;
        if (n == root) {
            root = l;
        } else if (n == n.up.left) {
            n.up.left = l;
        } else if (n == n.up.right) {
            n.up.right = l;
        } else {
            n.up.mid = l;
        }
        l.right = n;
        n.up = l;
    }

    private void insertIntoLists(Node n) {
        Entry e = n.entry;
        for (; n != null; n = parent(n)) {
            if (n.mid == null) {
                n.list = new Entry[1];
            } else if (n.list.length < k) {
                n.list = Arrays.copyOf(n.list, n.list.length + 1);
            } else if (e.weight <= n.list[k - 1].weight) {
                return;
            }
            int i = n.list.length - 1;
            while (i > 0 && e.weight > n.list[i - 1].weight) {
                n.list[i] = n.list[i - 1];
                i--;
            }
            n.list[i] = e;
        }
    }

    private void increaseWeight(Node n, final int newWeight) {
        Entry e = n.entry;
        e.weight = newWeight;
        for (; n != null; n = parent(n)) {
            int i = n.listIndexOf(e);
            if (i == -1) {
                if (e.weight <= n.list[k - 1].weight) {
                    return;
                } else {
                    i = k - 1;
                }
            }
            while (i > 0 && e.weight > n.list[i - 1].weight) {
                n.list[i] = n.list[i - 1];
                i--;
            }
            n.list[i] = e;
        }
    }

    private void reduceWeight(Node node, int newWeight) {
        Node n = node;
        Entry e = n.entry;
        e.weight = newWeight;
        for (; n != null; n = parent(n)) {
            int i = n.listIndexOf(e);
            if (i == -1) {
                return;
            }
            while (i < n.list.length - 1 && e.weight < n.list[i + 1].weight) {
                n.list[i] = n.list[i + 1];
                i++;
            }
            n.list[i] = e;
            if (i == k - 1) {
                Entry t = topUnlistedTerm(n);
                if (t != null && t.weight > e.weight) {
                    n.list[i] = t;
                }
            }
        }
    }

    private void removeFromLists(Entry e, final Node node) {
        Node n = node;
        for (; n != null; n = parent(n)) {
            int i = n.listIndexOf(e);
            if (i == -1) {
                return;
            }
            while (i < n.list.length - 1) {
                n.list[i] = n.list[i + 1];
                i++;
            }
            n.list[i] = e;
            if (n.list.length < k) {
                n.list = Arrays.copyOf(n.list, n.list.length - 1);
            } else {
                Entry t = topUnlistedTerm(n);
                if (t == null) {
                    n.list = Arrays.copyOf(n.list, k - 1);
                } else {
                    n.list[i] = t;
                }
            }
        }
    }

    private Entry topUnlistedTerm(final Node node) {
        Node n = node;
        Entry t = null;
        if (n.entry != null && n.listIndexOf(n.entry) == -1) {
            t = n.entry;
        }
        for (Node c = leftmostChild(n); c != null; c = rightSibling(c)) {
            for (Entry e : c.list) {
                if (n.listIndexOf(e) == -1) {
                    if (t == null || t.weight < e.weight) {
                        t = e;
                    }
                    break;
                }
            }
        }
        return t;
    }

    private Node leftmostChild(final Node node) {
        Node n = node;
        n = n.mid;
        if (n != null) {
            while (n.left != null) {
                n = n.left;
            }
        }
        return n;
    }

    private Node rightSibling(final Node node) {
        Node n = node;
        if (n.right != null) {
            n = n.right;
            while (n.left != null) {
                n = n.left;
            }
            return n;
        } else {
            while (n == n.up.right) {
                n = n.up;
            }
            if (n == n.up.left) {
                return n.up;
            } else {
                return null;
            }
        }
    }

    private Node parent(final Node node) {
        Node n = node;
        while (n != root && n != n.up.mid) {
            n = n.up;
        }
        return n.up;
    }

    /**
     * An iterator over the terms in the tree. The iterator returns the terms in
     * alphabetical order and allows the caller to remove returned terms from
     * the tree during the iteration.
     */
    public final class Iterator {

        private Node next;

        private Iterator() {
            if (root == null) {
                next = null;
            } else {
                next = firstEntry(root);
            }
        }

        /**
         * Returns true if the iteration has more terms.
         *
         * @return true if the iteration has more terms
         */
        public boolean hasNext() {
            return (next != null);
        }

        /**
         * Returns the next term in the iteration.
         *
         * @return the next term in the iteration
         * @throws NoSuchElementException
         *             if the iteration has no more terms
         */
        public Entry next() throws NoSuchElementException {
            if (next == null) {
                throw new NoSuchElementException();
            }
            Entry e = next.entry;
            next = nextEntry(next);
            return e;
        }

        private Node firstEntry(final Node node) {
            Node n = node;
            while (true) {
                while (n.left != null) {
                    n = n.left;
                }
                if (n.entry == null) {
                    n = n.mid;
                } else {
                    return n;
                }
            }
        }

        private Node nextEntry(final Node node) {
            Node n = node;
            if (n.mid != null) {
                return firstEntry(n.mid);
            } else if (n.right != null) {
                return firstEntry(n.right);
            } else {
                while (n.up != null) {
                    if (n == n.up.left) {
                        if (n.up.entry != null) {
                            return n.up;
                        } else {
                            return firstEntry(n.up.mid);
                        }
                    } else if (n == n.up.mid && n.up.right != null) {
                        return firstEntry(n.up.right);
                    } else {
                        n = n.up;
                    }
                }
                return null;
            }
        }
    }

    /**
     * A list of autocomplete suggestions, ordered from highest weight to lowest
     * weight.
     */
    public static final class Node {

        private Entry[] list;
        private Entry entry;
        private char firstChar;
        private short charEnd;
        private int priority;
        private Node left, mid, right;
        private Node up; // parent in the ternary search tree

        private Node(final String term, final int weight, final int charStart,
                final Node up) {
            entry = new Entry(term, weight);
            firstChar = term.charAt(charStart);
            charEnd = (short) term.length();
            left = mid = right = null;
            this.up = up;
        }

        private Node(final Node n, int charEnd) {
            entry = null;
            firstChar = n.firstChar;
            this.charEnd = (short) charEnd;
            priority = n.priority;
            left = n.left;
            mid = n;
            right = n.right;
            up = n.up;
        }

        /**
         * Returns the suggestion at the specified index in the list. The first
         * suggestion is at index 0, the second at index 1, and so on.
         *
         * @param index
         *            the index of the suggestion to return
         * @return the suggestion at the specified index in the list
         * @throws ArrayIndexOutOfBoundsException
         *             if the specified index is negative or not less than the
         *             list length
         */
        public Entry getSuggestion(final int index) {
            return list[index];
        }

        /**
         * Returns the number of suggestions in the list.
         *
         * @return the number of suggestions in the list
         */
        public int listLength() {
            return list.length;
        }

        private char charAt(final int index) {
            if (entry != null) {
                return entry.term.charAt(index);
            } else {
                return list[0].term.charAt(index);
            }
        }

        private int listIndexOf(final Entry e) {
            for (int i = 0; i < list.length; i++) {
                if (list[i] == e) {
                    return i;
                }
            }
            return -1;
        }
    }

    /**
     * A weighted term.
     */
    public static final class Entry {

        private final String term;
        private int weight;
        private int priority;

        private Entry(final String term, final int weight) {
            this.term = term;
            this.weight = weight;
        }

        /**
         * Returns the term.
         *
         * @return the term
         */
        public String getTerm() {
            return term;
        }

        /**
         * Returns the weight of the term.
         *
         * @return the weight of the term
         */
        public int getWeight() {
            return weight;
        }
    }

    public static void main(String[] args) {
        SuggestTree st = new SuggestTree(10);

        st.put("data", 1);
        st.put("tables", 1);
        st.put("order", 1);
        st.put("ascending", 1);
        st.put("descending", 1);
        st.put("select", 1);
        st.put("select-options", 1);
        st.put("selection", 1);
        st.put("from", 1);
        st.put("endif", 1);
        st.put("endwhile", 1);
        st.put("exit", 1);
        st.put("return", 1);
        st.put("enddo", 1);
        st.put("endcase", 1);

        Node n = st.getAutocompleteSuggestions("sel");
        if (n != null) {
            for (int i = 0; i < n.listLength(); i++) {
                System.out.println(n.getSuggestion(i).getTerm());
            }
        }

    }

}