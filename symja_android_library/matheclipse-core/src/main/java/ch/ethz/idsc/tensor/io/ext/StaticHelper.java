package ch.ethz.idsc.tensor.io.ext;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.stream.Stream;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;


/* package */ enum StaticHelper {
  ;
  /** @param inputStream
   * @return lines in given inputStream as stream of strings */
  static Stream<String> lines(InputStream inputStream) {
    return new BufferedReader(new InputStreamReader(inputStream)).lines();
  }

  static IAST three(String string) {
    StringTokenizer stringTokenizer = new StringTokenizer(string);
    return F.List( //
        F.fromString(stringTokenizer.nextToken()), //
        F.fromString(stringTokenizer.nextToken()), //
        F.fromString(stringTokenizer.nextToken()));
  }
}
