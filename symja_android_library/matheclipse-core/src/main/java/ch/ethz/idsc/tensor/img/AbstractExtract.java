package ch.ethz.idsc.tensor.img;

import org.matheclipse.core.interfaces.IAST;

/* package */ abstract class AbstractExtract {
  final IAST tensor;
  final int radius;

  AbstractExtract(IAST tensor, int radius) {
    this.tensor = tensor;
    this.radius = radius;
  }
}
