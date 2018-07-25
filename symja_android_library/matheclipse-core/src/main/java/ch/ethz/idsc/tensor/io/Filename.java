package ch.ethz.idsc.tensor.io;

import java.io.File;
import java.io.Serializable;

public class Filename implements Serializable {
  private static final char DOT = '.';
  // ---
  /** name of file in upper case characters */
  private final String string;

  public Filename(File file) {
    this(file.getName());
  }

  public Filename(String string) {
    this.string = string.toUpperCase();
  }

  // strictly private
  private Filename(String string, int until) {
    this.string = string.substring(0, until);
  }

  /** @return
   * @throws Exception if this filename does not contain the character `.` */
  public Filename truncate() {
    return new Filename(string, string.lastIndexOf(DOT));
  }

  /** @return ultimate extension of file derived from the characters after the last '.'
   * @throws Exception if extension is not listed in {@link Extension} */
  public Extension extension() {
    return Extension.valueOf(string.substring(string.lastIndexOf(DOT) + 1));
  }
  
}
