package org.matheclipse.core.tensor.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.io.Extension;

/** functionality used in {@link Import} and {@link ResourceData} */
/* package */ enum ImportHelper {
  ;
  /**
   * @param filename
   * @param inputStream
   * @return
   * @throws IOException
   */
  public static IAST of(Filename filename, InputStream inputStream) throws IOException {
    Extension extension = filename.extension();
    if (extension.equals(Extension.GZ))
      try (GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream)) {
        return of(filename.truncate(), gzipInputStream);
      }
    return of(extension, inputStream);
  }

  private static IAST of(Extension extension, InputStream inputStream) throws IOException {
    if (extension == Extension.CSV) {
      return Convert.fromCSV(new InputStreamReader(inputStream));
    }
    throw new UnsupportedOperationException(extension.name());
    // return switch (extension) {
    // case Extension.MATHEMATICA:S.Get.of(inputStream);
    // case CSV -> XsvFormat.CSV.parse(ReadLine.of(inputStream));
    // case TSV -> XsvFormat.TSV.parse(ReadLine.of(inputStream));
    // case VECTOR -> VectorFormat.parse(ReadLine.of(inputStream));
    // case BMP, GIF, JPG, PNG, TIFF -> ImageFormat.from(ImageIO.read(inputStream));
    // default -> throw new UnsupportedOperationException(extension.name());
    // };
  }
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
