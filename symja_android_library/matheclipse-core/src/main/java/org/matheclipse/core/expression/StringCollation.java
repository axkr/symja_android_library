package org.matheclipse.core.expression;

import java.text.Normalizer;
import java.util.Comparator;

/**
 * The canonical order of strings, as used by <code>Sort</code>, <code>Order</code>,
 * <code>Union</code> and the arguments of orderless functions. Strings are compared on four levels,
 * each deciding only when the ones before it found the strings equal:
 *
 * <ol>
 * <li>the characters other than ASCII punctuation and white space, without case and accents:
 * digits, then letters, then the other characters such as <code>$</code> or <code>€</code> by
 * their code. <code>"-a"</code>, <code>"a"</code> and <code>"A!"</code> are equal here, and
 * <code>".hid"</code> comes after <code>"b"</code>;</li>
 * <li>the accents: <code>"e"</code> before <code>"é"</code>;</li>
 * <li>the punctuation, character by character, with any other character counting more than
 * punctuation: <code>"-a"</code> before <code>"a"</code> before <code>"a!"</code>, and
 * <code>"a_b"</code> before <code>"ab"</code>;</li>
 * <li>the case of the letters, lower case first: <code>"aB"</code> before
 * <code>"Ab"</code>.</li>
 * </ol>
 *
 * Strings made of punctuation only are ordered by their length before their punctuation.
 * Digit strings are compared character by character, so <code>"10"</code> comes before
 * <code>"2"</code>.
 */
public final class StringCollation implements Comparator<String> {

  public static final StringCollation INSTANCE = new StringCollation();

  /** The order of the punctuation characters; the space comes last. */
  private static final String PUNCTUATION = "!@#%^&*()_-+=~`{[}]|\\<>.,;\"?'/: ";

  /** The weight of white space and control characters other than the space. */
  private static final int OTHER_PUNCTUATION = PUNCTUATION.length();

  /** The weight of a character which isn't punctuation on the punctuation level. */
  private static final int ALPHANUMERIC = Integer.MAX_VALUE;

  private StringCollation() {}

  /** Is the character compared on the first level, i.e. no ASCII punctuation or white space? */
  private static boolean isAlphanumeric(char c) {
    if (Character.isLetterOrDigit(c)) {
      return true;
    }
    return PUNCTUATION.indexOf(c) < 0 && !Character.isWhitespace(c) && !Character.isISOControl(c);
  }

  /** The character without its accents. */
  private static char base(char c) {
    if (c < 128) {
      return c;
    }
    return Normalizer.normalize(String.valueOf(c), Normalizer.Form.NFD).charAt(0);
  }

  /** The accents of the character, or <code>""</code>. */
  private static String accents(char c) {
    if (c < 128) {
      return "";
    }
    return Normalizer.normalize(String.valueOf(c), Normalizer.Form.NFD).substring(1);
  }

  /** The weight of a character on the first level. */
  private static long primary(char c) {
    if (!Character.isLetterOrDigit(c)) {
      return (2L << 32) | c;
    }
    char b = base(c);
    if (Character.isDigit(b)) {
      return b;
    }
    return (1L << 32) | Character.toLowerCase(b);
  }

  private static int punctuationWeight(char c) {
    if (isAlphanumeric(c)) {
      return ALPHANUMERIC;
    }
    int index = PUNCTUATION.indexOf(c);
    return index >= 0 ? index : OTHER_PUNCTUATION;
  }

  @Override
  public int compare(String s1, String s2) {
    // level 1: letters and digits
    int i = 0;
    int j = 0;
    final int n1 = s1.length();
    final int n2 = s2.length();
    boolean alphanumeric1 = false;
    boolean alphanumeric2 = false;
    while (true) {
      while (i < n1 && !isAlphanumeric(s1.charAt(i))) {
        i++;
      }
      while (j < n2 && !isAlphanumeric(s2.charAt(j))) {
        j++;
      }
      if (i >= n1 || j >= n2) {
        alphanumeric1 |= i < n1;
        alphanumeric2 |= j < n2;
        break;
      }
      alphanumeric1 = alphanumeric2 = true;
      int cmp = Long.compare(primary(s1.charAt(i)), primary(s2.charAt(j)));
      if (cmp != 0) {
        return cmp;
      }
      i++;
      j++;
    }
    if (i < n1) {
      return 1;
    }
    if (j < n2) {
      return -1;
    }
    if (!alphanumeric1 && !alphanumeric2 && n1 != n2) {
      return n1 < n2 ? -1 : 1;
    }
    // level 2: accents
    int cmp = compareAlphanumerics(s1, s2, true);
    if (cmp != 0) {
      return cmp;
    }
    // level 3: punctuation
    for (i = 0; i < n1 && i < n2; i++) {
      cmp = Integer.compare(punctuationWeight(s1.charAt(i)), punctuationWeight(s2.charAt(i)));
      if (cmp != 0) {
        return cmp;
      }
    }
    if (n1 != n2) {
      return n1 < n2 ? -1 : 1;
    }
    // level 4: case
    cmp = compareAlphanumerics(s1, s2, false);
    if (cmp != 0) {
      return cmp;
    }
    return s1.compareTo(s2);
  }

  /**
   * Compare the accents (<code>accents == true</code>) or the case of the letters and digits of
   * two strings which are equal on the first level, so they have the same number of them.
   */
  private static int compareAlphanumerics(String s1, String s2, boolean accents) {
    int i = 0;
    int j = 0;
    final int n1 = s1.length();
    final int n2 = s2.length();
    while (true) {
      while (i < n1 && !isAlphanumeric(s1.charAt(i))) {
        i++;
      }
      while (j < n2 && !isAlphanumeric(s2.charAt(j))) {
        j++;
      }
      if (i >= n1 || j >= n2) {
        return 0;
      }
      final char c1 = s1.charAt(i++);
      final char c2 = s2.charAt(j++);
      int cmp;
      if (accents) {
        cmp = accents(c1).compareTo(accents(c2));
      } else {
        cmp = Boolean.compare(Character.isUpperCase(c1), Character.isUpperCase(c2));
      }
      if (cmp != 0) {
        return cmp;
      }
    }
  }
}
