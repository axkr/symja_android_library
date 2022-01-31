package org.matheclipse.core.tensor.io;

import java.io.InputStream;
import java.util.Properties;

/**
 * access to resource data in jar files, for instance, the content included in the tensor library.
 *
 * <p>
 * Tensor resources provided by the tensor library include
 *
 * <pre>
 * /colorscheme/classic.csv
 * /number/primes.vector
 * </pre>
 *
 * <p>
 * Properties provided by the tensor library include
 *
 * <pre>
 * /unit/si.properties
 * </pre>
 */
public enum ResourceData {
  ;

  /**
   * @param string as path to resource
   * @return imported properties, or null if resource could not be loaded
   */
  public static Properties properties(String string) {
    try (InputStream inputStream = ResourceData.class.getResourceAsStream(string)) {
      return ImportHelper.properties(inputStream);
    } catch (Exception exception) {
      // ---
    }
    return null;
  }
}
