package ch.ethz.idsc.tensor.io.ext;

import java.util.stream.Stream;

public enum WavefrontFormat {
  ;
  /** parse Wavefront .obj file
   * 
   * @param stream
   * @return */
  public static Wavefront parse(Stream<String> stream) {
    return new WavefrontImpl(stream);
  }
}
