// code adapted from https://github.com/datahaki/tensor

/**
 * Copyright John E. Lloyd, 2003. All rights reserved. Permission to use, copy, and modify, without
 * fee, is granted for non-commercial and research purposes, provided that this copyright notice
 * appears in all copies.
 *
 * This software is distributed "as is", without any warranty, including any implied warranty of
 * merchantability or fitness for a particular use. The authors assume no responsibility for, and
 * shall not be liable for, any special, indirect, or consequential damages, or any damages
 * whatsoever, arising out of or in connection with the use of this software.
 */
package org.matheclipse.core.tensor.opt.qh3;

/** Maintains a single-linked list of faces for use by QuickHull3D */
class FaceList {
  private Face head;
  private Face tail;

  /** Clears this list. */
  public void clear() {
    head = tail = null;
  }

  /** Adds a vertex to the end of this list. */
  public void add(Face vtx) {
    if (head == null) {
      head = vtx;
    } else {
      tail.next = vtx;
    }
    vtx.next = null;
    tail = vtx;
  }

  public Face first() {
    return head;
  }

  /** Returns true if this list is empty. */
  public boolean isEmpty() {
    return head == null;
  }
}
