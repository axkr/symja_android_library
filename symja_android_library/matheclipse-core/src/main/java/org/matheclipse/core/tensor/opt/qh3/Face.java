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

import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.nrm.Vector2NormSquared;

/**
 * Basic triangular face used to form the hull.
 *
 * <p>
 * The information stored for each face consists of a planar normal, a planar offset, and a
 * doubly-linked list of three <a href=HalfEdge>HalfEdges</a> which surround the face in a
 * counter-clockwise direction.
 *
 * @author John E. Lloyd, Fall 2004
 */
class Face {
  HalfEdge he0;
  private Vector3d normal;
  IExpr area;
  private Vector3d centroid;
  IExpr planeOffset;
  int index;
  int numVerts;
  Face next;
  static final int VISIBLE = 1;
  static final int NON_CONVEX = 2;
  static final int DELETED = 3;
  int mark = VISIBLE;
  Vertex outside;

  public void computeCentroid(Vector3d centroid) {
    centroid.setZero();
    HalfEdge he = he0;
    do {
      centroid.add(he.head().pnt);
      he = he.next;
    } while (he != he0);
    centroid.scale(F.num(1 / (double) numVerts));
  }

  public void computeNormal(Vector3d normal, IExpr minArea) {
    computeNormal(normal);
    if ((area.lessThan(minArea).isTrue())) { // area < minArea
      // make the normal more robust by removing
      // components parallel to the longest edge
      HalfEdge hedgeMax = null;
      IExpr lenSqrMax = F.C0;
      HalfEdge hedge = he0;
      do {
        IExpr lenSqr = hedge.lengthSquared();
        if ((lenSqrMax.lessThan(lenSqr).isTrue())) {// lenSqr > lenSqrMax
          hedgeMax = hedge;
          lenSqrMax = lenSqr;
        }
        hedge = hedge.next;
      } while (hedge != he0);
      Vector3d p2 = hedgeMax.head().pnt;
      Vector3d p1 = hedgeMax.tail().pnt;
      IExpr lenMax = F.eval(F.Sqrt(lenSqrMax));
      IExpr ux = p2.x.subtract(p1.x).divide(lenMax);
      IExpr uy = p2.y.subtract(p1.y).divide(lenMax);
      IExpr uz = p2.z.subtract(p1.z).divide(lenMax);
      IExpr dot = normal.x.multiply(ux).add(normal.y.multiply(uy)).add(normal.z.multiply(uz));
      normal.x = normal.x.subtract(ux.multiply(dot));
      normal.y = normal.y.subtract(uy.multiply(dot));
      normal.z = normal.z.subtract(uz.multiply(dot));
      normal.normalize();
    }
  }

  public void computeNormal(Vector3d normal) {
    HalfEdge he1 = he0.next;
    HalfEdge he2 = he1.next;
    Vector3d p0 = he0.head().pnt;
    Vector3d p2 = he1.head().pnt;
    IExpr d2x = p2.x.subtract(p0.x);
    IExpr d2y = p2.y.subtract(p0.y);
    IExpr d2z = p2.z.subtract(p0.z);
    normal.setZero();
    numVerts = 2;
    while (he2 != he0) {
      IExpr d1x = d2x;
      IExpr d1y = d2y;
      IExpr d1z = d2z;
      p2 = he2.head().pnt;
      d2x = p2.x.subtract(p0.x);
      d2y = p2.y.subtract(p0.y);
      d2z = p2.z.subtract(p0.z);
      normal.x = normal.x.add(d1y.multiply(d2z).subtract(d1z.multiply(d2y)));
      normal.y = normal.y.add(d1z.multiply(d2x).subtract(d1x.multiply(d2z)));
      normal.z = normal.z.add(d1x.multiply(d2y).subtract(d1y.multiply(d2x)));
      he1 = he2;
      he2 = he2.next;
      numVerts++;
    }
    area = normal.norm();
    normal.scale(area.reciprocal());
  }

  private void computeNormalAndCentroid() {
    computeNormal(normal);
    computeCentroid(centroid);
    planeOffset = normal.dot(centroid);
    int numv = 0;
    HalfEdge he = he0;
    do {
      numv++;
      he = he.next;
    } while (he != he0);
    if (numv != numVerts) {
      throw new ArgumentTypeException(
          "face " + getVertexString() + " numVerts=" + numVerts + " should be " + numv);
      // throw Throw.of("face " + getVertexString() + " numVerts=" + numVerts + " should be " +
      // numv);
    }
  }

  private void computeNormalAndCentroid(IExpr minArea) {
    computeNormal(normal, minArea);
    computeCentroid(centroid);
    planeOffset = normal.dot(centroid);
  }

  public static Face createTriangle(Vertex v0, Vertex v1, Vertex v2) {
    return createTriangle(v0, v1, v2, F.C0);
  }

  /**
   * Constructs a triangule Face from vertices v0, v1, and v2.
   *
   * @param v0 first vertex
   * @param v1 second vertex
   * @param v2 third vertex
   */
  public static Face createTriangle(Vertex v0, Vertex v1, Vertex v2, IExpr minArea) {
    Face face = new Face();
    HalfEdge he0 = new HalfEdge(v0, face);
    HalfEdge he1 = new HalfEdge(v1, face);
    HalfEdge he2 = new HalfEdge(v2, face);
    he0.prev = he2;
    he0.next = he1;
    he1.prev = he0;
    he1.next = he2;
    he2.prev = he1;
    he2.next = he0;
    face.he0 = he0;
    // compute the normal and offset
    face.computeNormalAndCentroid(minArea);
    return face;
  }

  public static Face create(Vertex[] vtxArray, int[] indices) {
    Face face = new Face();
    HalfEdge hePrev = null;
    for (int i = 0; i < indices.length; i++) {
      HalfEdge he = new HalfEdge(vtxArray[indices[i]], face);
      if (hePrev != null) {
        he.setPrev(hePrev);
        hePrev.setNext(he);
      } else {
        face.he0 = he;
      }
      hePrev = he;
    }
    face.he0.setPrev(hePrev);
    hePrev.setNext(face.he0);
    // compute the normal and offset
    face.computeNormalAndCentroid();
    return face;
  }

  public Face() {
    normal = new Vector3d();
    centroid = new Vector3d();
    mark = VISIBLE;
  }

  /**
   * Gets the i-th half-edge associated with the face.
   * 
   * @param i the half-edge index, in the range 0-2.
   * @return the half-edge
   */
  public HalfEdge getEdge(int i) {
    HalfEdge he = he0;
    while (i > 0) {
      he = he.next;
      i--;
    }
    while (i < 0) {
      he = he.prev;
      i++;
    }
    return he;
  }

  public HalfEdge getFirstEdge() {
    return he0;
  }

  /**
   * Finds the half-edge within this face which has tail <code>vt</code> and head <code>vh</code>.
   *
   * @param vt tail point
   * @param vh head point
   * @return the half-edge, or null if none is found.
   */
  public HalfEdge findEdge(Vertex vt, Vertex vh) {
    HalfEdge he = he0;
    do {
      if (he.head() == vh && he.tail() == vt) {
        return he;
      }
      he = he.next;
    } while (he != he0);
    return null;
  }

  /**
   * Computes the distance from a point p to the plane of this face.
   *
   * @param p the point
   * @return distance from the point to the plane
   */
  public IExpr distanceToPlane(Vector3d p) {
    return F.eval(F.Subtract(F.Dot(normal.toTensor(), p.toTensor()), planeOffset));
    // return normal.x * p.x + normal.y * p.y + normal.z * p.z - planeOffset;
  }

  /**
   * Returns the normal of the plane associated with this face.
   *
   * @return the planar normal
   */
  public Vector3d getNormal() {
    return normal;
  }

  public Vector3d getCentroid() {
    return centroid;
  }

  public int numVertices() {
    return numVerts;
  }

  public String getVertexString() {
    String s = null;
    HalfEdge he = he0;
    do {
      if (s == null) {
        s = "" + he.head().index;
      } else {
        s += " " + he.head().index;
      }
      he = he.next;
    } while (he != he0);
    return s;
  }

  public void getVertexIndices(int[] idxs) {
    HalfEdge he = he0;
    int i = 0;
    do {
      idxs[i++] = he.head().index;
      he = he.next;
    } while (he != he0);
  }

  private Face connectHalfEdges(HalfEdge hedgePrev, HalfEdge hedge) {
    Face discardedFace = null;
    if (hedgePrev.oppositeFace() == hedge.oppositeFace()) { // then there is a redundant edge that
                                                            // we can get rid off
      Face oppFace = hedge.oppositeFace();
      HalfEdge hedgeOpp;
      if (hedgePrev == he0) {
        he0 = hedge;
      }
      if (oppFace.numVertices() == 3) { // then we can get rid of the opposite face altogether
        hedgeOpp = hedge.getOpposite().prev.getOpposite();
        oppFace.mark = DELETED;
        discardedFace = oppFace;
      } else {
        hedgeOpp = hedge.getOpposite().next;
        if (oppFace.he0 == hedgeOpp.prev) {
          oppFace.he0 = hedgeOpp;
        }
        hedgeOpp.prev = hedgeOpp.prev.prev;
        hedgeOpp.prev.next = hedgeOpp;
      }
      hedge.prev = hedgePrev.prev;
      hedge.prev.next = hedge;
      hedge.opposite = hedgeOpp;
      hedgeOpp.opposite = hedge;
      // oppFace was modified, so need to recompute
      oppFace.computeNormalAndCentroid();
    } else {
      hedgePrev.next = hedge;
      hedge.prev = hedgePrev;
    }
    return discardedFace;
  }

  void checkConsistency() {
    // do a sanity check on the face
    HalfEdge hedge = he0;
    IExpr maxd = F.C0;
    int numv = 0;
    if (numVerts < 3) {
      throw new ArgumentTypeException("degenerate face: " + getVertexString());
      // throw Throw.of("degenerate face: " + getVertexString());
    }
    do {
      HalfEdge hedgeOpp = hedge.getOpposite();
      if (hedgeOpp == null) {
        throw new ArgumentTypeException("face " + getVertexString() + ": "
            + "unreflected half edge " + hedge.getVertexString());
        // throw Throw.of("face " + getVertexString() + ": " + "unreflected half edge "
        // + hedge.getVertexString());
      } else if (hedgeOpp.getOpposite() != hedge) {
        throw new ArgumentTypeException(
            "face " + getVertexString() + ": " + "opposite half edge " + hedgeOpp.getVertexString()
                + " has opposite " + hedgeOpp.getOpposite().getVertexString());
        // throw Throw.of(
        // "face " + getVertexString() + ": " + "opposite half edge " + hedgeOpp.getVertexString()
        // + " has opposite " + hedgeOpp.getOpposite().getVertexString());
      }
      if (hedgeOpp.head() != hedge.tail() || hedge.head() != hedgeOpp.tail()) {
        throw new ArgumentTypeException("face " + getVertexString() + ": " + "half edge "
            + hedge.getVertexString() + " reflected by " + hedgeOpp.getVertexString());
        // throw Throw.of("face " + getVertexString() + ": " + "half edge " +
        // hedge.getVertexString()
        // + " reflected by " + hedgeOpp.getVertexString());
      }
      Face oppFace = hedgeOpp.face;
      if (oppFace == null) {
        throw new ArgumentTypeException("face " + getVertexString() + ": " + "no face on half edge "
            + hedgeOpp.getVertexString());
        // throw Throw.of("face " + getVertexString() + ": " + "no face on half edge "
        // + hedgeOpp.getVertexString());
      } else if (oppFace.mark == DELETED) {
        throw new ArgumentTypeException("face " + getVertexString() + ": " + "opposite face "
            + oppFace.getVertexString() + " not on hull");
        // throw Throw.of("face " + getVertexString() + ": " + "opposite face "
        // + oppFace.getVertexString() + " not on hull");
      }
      IExpr d = S.Abs.of(distanceToPlane(hedge.head().pnt));
      if (maxd.lessThan(d).isTrue()) {// d > maxd
        maxd = d;
      }
      numv++;
      hedge = hedge.next;
    } while (hedge != he0);
    if (numv != numVerts) {
      throw new ArgumentTypeException(
          "face " + getVertexString() + " numVerts=" + numVerts + " should be " + numv);
      // throw Throw.of("face " + getVertexString() + " numVerts=" + numVerts + " should be " +
      // numv);
    }
  }

  public int mergeAdjacentFace(HalfEdge hedgeAdj, Face[] discarded) {
    Face oppFace = hedgeAdj.oppositeFace();
    int numDiscarded = 0;
    discarded[numDiscarded++] = oppFace;
    oppFace.mark = DELETED;
    HalfEdge hedgeOpp = hedgeAdj.getOpposite();
    HalfEdge hedgeAdjPrev = hedgeAdj.prev;
    HalfEdge hedgeAdjNext = hedgeAdj.next;
    HalfEdge hedgeOppPrev = hedgeOpp.prev;
    HalfEdge hedgeOppNext = hedgeOpp.next;
    while (hedgeAdjPrev.oppositeFace() == oppFace) {
      hedgeAdjPrev = hedgeAdjPrev.prev;
      hedgeOppNext = hedgeOppNext.next;
    }
    while (hedgeAdjNext.oppositeFace() == oppFace) {
      hedgeOppPrev = hedgeOppPrev.prev;
      hedgeAdjNext = hedgeAdjNext.next;
    }
    HalfEdge hedge;
    for (hedge = hedgeOppNext; hedge != hedgeOppPrev.next; hedge = hedge.next) {
      hedge.face = this;
    }
    if (hedgeAdj == he0) {
      he0 = hedgeAdjNext;
    }
    // handle the half edges at the head
    Face discardedFace;
    discardedFace = connectHalfEdges(hedgeOppPrev, hedgeAdjNext);
    if (discardedFace != null) {
      discarded[numDiscarded++] = discardedFace;
    }
    // handle the half edges at the tail
    discardedFace = connectHalfEdges(hedgeAdjPrev, hedgeOppNext);
    if (discardedFace != null) {
      discarded[numDiscarded++] = discardedFace;
    }
    computeNormalAndCentroid();
    checkConsistency();
    return numDiscarded;
  }

  private IExpr areaSquared(HalfEdge hedge0, HalfEdge hedge1) {
    // return the squared area of the triangle defined
    // by the half edge hedge0 and the point at the
    // head of hedge1.
    Vector3d p0 = hedge0.tail().pnt;
    Vector3d p1 = hedge0.head().pnt;
    Vector3d p2 = hedge1.head().pnt;
    IExpr dx1 = p1.x.subtract(p0.x);
    IExpr dy1 = p1.y.subtract(p0.y);
    IExpr dz1 = p1.z.subtract(p0.z);
    IExpr dx2 = p2.x.subtract(p0.x);
    IExpr dy2 = p2.y.subtract(p0.y);
    IExpr dz2 = p2.z.subtract(p0.z);
    IExpr x = dy1.multiply(dz2).subtract(dz1.multiply(dy2));
    IExpr y = dz1.multiply(dx2).subtract(dx1.multiply(dz2));
    IExpr z = dx1.multiply(dy2).subtract(dy1.multiply(dx2));
    return Vector2NormSquared.of(F.List(x, y, z)); // x * x + y * y + z * z;
  }

  public void triangulate(FaceList newFaces, IExpr minArea) {
    HalfEdge hedge;
    if (numVertices() < 4) {
      return;
    }
    Vertex v0 = he0.head();
    Face prevFace = null;
    hedge = he0.next;
    HalfEdge oppPrev = hedge.opposite;
    Face face0 = null;
    for (hedge = hedge.next; hedge != he0.prev; hedge = hedge.next) {
      Face face = createTriangle(v0, hedge.prev.head(), hedge.head(), minArea);
      face.he0.next.setOpposite(oppPrev);
      face.he0.prev.setOpposite(hedge.opposite);
      oppPrev = face.he0;
      newFaces.add(face);
      if (face0 == null) {
        face0 = face;
      }
    }
    hedge = new HalfEdge(he0.prev.prev.head(), this);
    hedge.setOpposite(oppPrev);
    hedge.prev = he0;
    hedge.prev.next = hedge;
    hedge.next = he0.prev;
    hedge.next.prev = hedge;
    computeNormalAndCentroid(minArea);
    checkConsistency();
    for (Face face = face0; face != null; face = face.next) {
      face.checkConsistency();
    }
  }
}
