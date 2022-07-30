package org.matheclipse.core.tensor.sca;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;

/**
 * clip to an interval of non-zero width
 * 
 * @implSpec This class is immutable and thread-safe.
 */
/* package */ class ClipInterval implements Clip {
  private final ISignedNumber min;
  private final ISignedNumber max;
  private final ISignedNumber width;
  private final EvalEngine engine;

  public ClipInterval(ISignedNumber min, ISignedNumber max, ISignedNumber width) {
    this.min = min;
    this.max = max;
    this.width = width;
    this.engine = EvalEngine.get();
  }

  @Override
  public final IExpr apply(IExpr scalar) {
    IExpr sca = scalar;
    if (engine.evalTrue(sca.lessThan(min))) {
      return min;
    }
    if (engine.evalTrue(max.lessThan(sca))) {
      return max;
    }
    return sca;
  }

  @Override
  @SuppressWarnings("unchecked")
  public final IAST of(IAST tensor) {
    return tensor.map(this);
  }

  @Override // from Clip
  public final boolean isInside(ISignedNumber scalar) {
    return apply(scalar).equals(scalar);
  }

  @Override // from Clip
  public final boolean isOutside(ISignedNumber scalar) {
    return !isInside(scalar);
  }

  @Override // from Clip
  public final ISignedNumber requireInside(ISignedNumber scalar) {
    if (isInside(scalar)) {
      return scalar;
    }
    return null;
  }

  @Override // from Clip
  public IExpr rescale(ISignedNumber scalar) {
    return apply(scalar).subtract(min).divide(width);
  }

  @Override // from Clip
  public final ISignedNumber min() {
    return min;
  }

  @Override // from Clip
  public final ISignedNumber max() {
    return max;
  }

  @Override // from Clip
  public final ISignedNumber width() {
    return width;
  }

  @Override
  public int hashCode() {
    return min.hashCode() + 31 * max.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (obj instanceof Clip) {
      ClipInterval clip = (ClipInterval) obj;
      return min.equals(clip.min()) && max.equals(clip.max());
    }
    return false;
  }

  @Override // from Object
  public final String toString() {
    return String.format("%s[%s, %s]", Clip.class.getSimpleName(), min(), max());
  }
}
