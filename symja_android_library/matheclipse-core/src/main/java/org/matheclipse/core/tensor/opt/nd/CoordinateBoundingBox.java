// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.opt.nd;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.sca.Clip;
import org.matheclipse.core.tensor.sca.Clips;

/**
 * n-dimensional axis aligned bounding box
 * 
 */
public class CoordinateBoundingBox {

  /**
   * @param stream of clip instances
   * @return
   */
  public static CoordinateBoundingBox of(Stream<Clip> stream) {
    return new CoordinateBoundingBox(stream.collect(Collectors.toList()));
  }

  /**
   * @param clips
   * @return
   */
  @SafeVarargs
  public static CoordinateBoundingBox of(Clip... clips) {
    return new CoordinateBoundingBox(List.of(clips));
  }

  // ---
  private final List<Clip> list;

  private CoordinateBoundingBox(List<Clip> list) {
    this.list = list;
  }

  /** @return dimensions of bounding box */
  public int dimensions() {
    return list.size();
  }

  /**
   * @param index in the range 0, 1, ... {@link #dimensions()} - 1
   * @return clip in given dimension
   */
  public Clip getClip(int index) {
    return list.get(index);
  }

  /**
   * @param vector of length {@link #dimensions()}
   * @return coordinates of vector clipped to bounds of this box instance
   */
  public IAST mapInside(IAST vector) {
    IASTAppendable result = F.ListAlloc(vector.argSize());
    vector.forEach((x, i) -> result.append(getClip(i).apply(x)));
    return result;
  }

  /**
   * @param vector
   * @return whether given vector is inside this bounding box
   */
  public boolean isInside(IAST vector) {
    IASTAppendable result = F.ListAlloc(vector.argSize());
    vector.forEach((x, i) -> result.append(getClip(i).apply(x)));
    return vector.forAll((x, i) -> getClip(i - 1).isInside(x));
  }

  /**
   * @param vector
   * @return given vector
   * @throws Exception if any coordinate of vector is outside of this bounding box
   */
  public IAST requireInside(IAST vector) {
    if (isInside(vector)) {
      return vector;
    }
    throw new UnsupportedOperationException();
    // throw Throw.of(vector);
  }

  // ---
  /**
   * @param index of dimension
   * @return left, i.e. lower half of this bounding box
   */
  public CoordinateBoundingBox splitLo(int index) {
    List<Clip> copy = new ArrayList<>(list);
    Clip clip = getClip(index);
    copy.set(index, Clips.interval(clip.min(), median(clip)));
    return new CoordinateBoundingBox(copy);
  }

  /**
   * @param index of dimension
   * @return right, i.e. upper half of this bounding box
   */
  public CoordinateBoundingBox splitHi(int index) {
    List<Clip> copy = new ArrayList<>(list);
    Clip clip = getClip(index);
    copy.set(index, Clips.interval(median(clip), clip.max()));
    return new CoordinateBoundingBox(copy);
  }

  /**
   * @param index
   * @return median of bounds in dimension of given index
   */
  public IExpr median(int index) {
    return median(getClip(index));
  }

  private static IExpr median(Clip clip) {
    return clip.min().plus(clip.width().multiply(F.C1D2));
  }

  // ---
  /** @return stream of all clips */
  public Stream<Clip> stream() {
    return list.stream();
  }

  /** @return lower left corner of bounding box */
  public IAST min() {
    return F.ListAlloc(stream().map(Clip::min));
  }

  /** @return upper right corner of bounding box */
  public IAST max() {
    return F.ListAlloc(stream().map(Clip::max));
  }

  // ---
  @Override // from Object
  public int hashCode() {
    return list.hashCode();
  }

  @Override // from Object
  public boolean equals(Object object) {
    if (object instanceof CoordinateBoundingBox) {
      return list.equals(((CoordinateBoundingBox) object).list);
    }
    return false;
  }

  @Override // from Object
  public String toString() {
    return list.toString();
  }
}
