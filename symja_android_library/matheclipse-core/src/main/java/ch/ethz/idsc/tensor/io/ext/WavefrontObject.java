package ch.ethz.idsc.tensor.io.ext;

import org.matheclipse.core.interfaces.IAST;

public interface WavefrontObject {
  /** @return name of object */
  String name();

  /** @return vectors with indices to vertices for each polygon */
  IAST faces();

  /** @return vectors with indices to normals for each polygon */
  IAST normals();
}
