package ch.ethz.idsc.tensor.io.ext;

import java.util.List;

import org.matheclipse.core.interfaces.IAST;

public interface Wavefront {
  /** @return |V| x 3 matrix of vertices */
  IAST vertices();

  /** @return |N| x 3 matrix of normals */
  IAST normals();

  /** @return list of {@link WavefrontObject}s */
  List<WavefrontObject> objects();
}
