package org.matheclipse.core.tensor.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.stream.Stream;

/** functionality used in {@link Import} and {@link ResourceData} */
/* package */ enum ImportHelper {
  ;

  /**
   * @param inputStream
   * @return lines in given inputStream as stream of strings
   */
  static Stream<String> lines(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream)).lines();
  }

  /**
   * @param inputStream
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   * @throws DataFormatException
   */
  // static <T> T object(InputStream inputStream) throws IOException, ClassNotFoundException,
  // DataFormatException {
  // int length = inputStream.available();
  // byte[] bytes = new byte[length];
  // inputStream.read(bytes);
  // return ObjectFormat.parse(bytes);
  // }

  /**
   * @param inputStream
   * @return
   * @throws IOException
   */
  static Properties properties(InputStream inputStream) throws IOException {
    Properties properties = new Properties();
    properties.load(inputStream);
    return properties;
  }
}
