// code by jph
package org.matheclipse.core.tensor.io;

import java.util.Objects;
import org.matheclipse.core.io.Extension;

/* package */ class Filename {
  private static final char DOT = '.';
  // ---
  /** name of file */
  private final String string;

  /** @param string */
  public Filename(String string) {
    this.string = Objects.requireNonNull(string);
  }

  /** @return
   * @throws Exception if this filename does not contain the character `.` */
  public Filename truncate() {
    return new Filename(string.substring(0, string.lastIndexOf(DOT)));
  }

  /** Example:
   * "title.csv.gz" gives {@link Extension#GZ}
   * 
   * @return ultimate extension of file derived from the characters after the last '.'
   * @throws IllegalArgumentException if extension is not listed in {@link Extension} */
  public Extension extension() {
    return Extension.of(string.substring(string.lastIndexOf(DOT) + 1));
  }
}
