package org.matheclipse.core.io;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A character encoding as the encoding argument of <code>ToCharacterCode</code>,
 * <code>FromCharacterCode</code>, <code>ByteArrayToString</code> and
 * <code>StringToByteArray</code> names it: UTF-8 or one of the encodings with one byte for each
 * character.
 *
 * <p>
 * A byte to which a one-byte encoding gives no character is read as the private-use character
 * <code>U+F200 + byte</code>; that character has no byte in the encoding, so it is not written
 * back.
 */
public final class TextEncoding {

  /** The first of the 256 characters which stand for the bytes an encoding leaves undefined. */
  public static final int UNDEFINED_BYTE_BASE = 0xF200;

  /**
   * The bytes of the Macintosh Roman encoding whose character differs from the one of the
   * <code>x-MacRoman</code> charset, as pairs of byte and character: the Greek letters for the
   * micro and the increment sign, the diamond, the division slash, the currency sign the euro sign
   * replaced, and the logo.
   */
  private static final int[] MACINTOSH_ROMAN_DIFFERENCES = {0xB5, 0x03BC, 0xC6, 0x0394, 0xD7,
      0x25C7, 0xDA, 0x2215, 0xDB, 0x00A4, 0xF0, 0xFDFF};

  private static final TextEncoding UTF8 = new TextEncoding(null);

  private static final Map<String, TextEncoding> ENCODINGS = new HashMap<String, TextEncoding>();

  /** The character of each byte, <code>null</code> for UTF-8. */
  private final int[] characters;

  /** The byte of each character of a one-byte encoding. */
  private final Map<Integer, Integer> bytes;

  private TextEncoding(int[] characters) {
    this.characters = characters;
    this.bytes = new HashMap<Integer, Integer>();
    if (characters != null) {
      for (int b = 0; b < 256; b++) {
        if (characters[b] != UNDEFINED_BYTE_BASE + b) {
          bytes.put(characters[b], b);
        }
      }
    }
  }

  /** The encoding in which the bytes <code>from</code> to <code>to</code> are their characters. */
  private static int[] range(int from, int to) {
    int[] table = new int[256];
    for (int b = 0; b < 256; b++) {
      table[b] = b >= from && b <= to ? b : UNDEFINED_BYTE_BASE + b;
    }
    return table;
  }

  /**
   * The table of a one-byte charset of the Java runtime, or <code>null</code> if the runtime does
   * not have it.
   */
  private static int[] charsetTable(String charsetName) {
    if (!Charset.isSupported(charsetName)) {
      return null;
    }
    Charset charset = Charset.forName(charsetName);
    int[] table = new int[256];
    for (int b = 0; b < 256; b++) {
      CharsetDecoder decoder = charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT);
      try {
        CharBuffer decoded = decoder.decode(ByteBuffer.wrap(new byte[] {(byte) b}));
        table[b] = decoded.length() == 1 ? decoded.charAt(0) : UNDEFINED_BYTE_BASE + b;
      } catch (CharacterCodingException cce) {
        table[b] = UNDEFINED_BYTE_BASE + b;
      }
    }
    return table;
  }

  private static int[] table(String name) {
    switch (name) {
      case "ASCII":
        return range(0, 0x7F);
      case "PrintableASCII":
        int[] printable = range(0x20, 0x7E);
        printable['\n'] = '\n';
        printable['\r'] = '\r';
        return printable;
      case "ISOLatin1":
      case "ISO8859-1":
        return range(0, 0xFF);
      case "WindowsANSI":
        return charsetTable("windows-1252");
      case "MacintoshRoman":
        int[] roman = charsetTable("x-MacRoman");
        if (roman != null) {
          for (int i = 0; i < MACINTOSH_ROMAN_DIFFERENCES.length; i += 2) {
            roman[MACINTOSH_ROMAN_DIFFERENCES[i]] = MACINTOSH_ROMAN_DIFFERENCES[i + 1];
          }
        }
        return roman;
      default:
        return null;
    }
  }

  /** The UTF-8 encoding. */
  public static TextEncoding utf8() {
    return UTF8;
  }

  /**
   * The encoding with this name.
   *
   * @return <code>null</code> if the name is not one of the encodings known here
   */
  public static TextEncoding of(String name) {
    if (name.equals("UTF8") || name.equals("UTF-8")) {
      return UTF8;
    }
    synchronized (ENCODINGS) {
      TextEncoding encoding = ENCODINGS.get(name);
      if (encoding == null) {
        int[] table = table(name);
        if (table == null) {
          return null;
        }
        encoding = new TextEncoding(table);
        ENCODINGS.put(name, encoding);
      }
      return encoding;
    }
  }

  public boolean isUTF8() {
    return characters == null;
  }

  /**
   * The byte of the character <code>codePoint</code> in this one-byte encoding.
   *
   * @return <code>-1</code> if the encoding does not have the character
   */
  public int toByte(int codePoint) {
    Integer b = bytes.get(codePoint);
    return b == null ? -1 : b.intValue();
  }

  /**
   * The first character of <code>text</code> this encoding does not have.
   *
   * @return its code point, <code>-1</code> if the encoding has all the characters
   */
  public int firstUnencodable(String text) {
    if (isUTF8()) {
      return -1;
    }
    return text.codePoints().filter(c -> toByte(c) < 0).findFirst().orElse(-1);
  }

  /**
   * The bytes of <code>text</code> in this encoding.
   *
   * @return <code>null</code> if the encoding lacks one of the characters
   */
  public byte[] encode(String text) {
    if (isUTF8()) {
      return text.getBytes(StandardCharsets.UTF_8);
    }
    final int[] codePoints = text.codePoints().toArray();
    byte[] result = new byte[codePoints.length];
    for (int i = 0; i < codePoints.length; i++) {
      int b = toByte(codePoints[i]);
      if (b < 0) {
        return null;
      }
      result[i] = (byte) b;
    }
    return result;
  }

  /** The text of the <code>bytes</code>, see {@link #decode(int[], Consumer)}. */
  public String decode(byte[] bytes, Consumer<int[]> invalid) {
    int[] codes = new int[bytes.length];
    for (int i = 0; i < bytes.length; i++) {
      codes[i] = bytes[i] & 0xFF;
    }
    return decode(codes, invalid);
  }

  /**
   * The text of character codes: a code below 256 is a byte of this encoding, a larger one is the
   * character it names.
   *
   * <p>
   * In UTF-8 a byte sequence which is no character leaves its first byte as the character of that
   * code, and the bytes after it are read on their own. A sequence with the right shape is a
   * character, even if a shorter sequence encodes the same one.
   *
   * @param codes character codes, none of them negative
   * @param invalid called with the bytes of each UTF-8 sequence which is no character: the first
   *        byte, the bytes which continue it and the one which broke it off; may be
   *        <code>null</code>
   */
  public String decode(int[] codes, Consumer<int[]> invalid) {
    final StringBuilder buffer = new StringBuilder(codes.length);
    if (!isUTF8()) {
      for (int code : codes) {
        buffer.appendCodePoint(code < 256 ? characters[code] : code);
      }
      return buffer.toString();
    }
    int i = 0;
    while (i < codes.length) {
      final int lead = codes[i];
      if (lead < 0x80 || lead > 0xFF) {
        buffer.appendCodePoint(lead);
        i++;
        continue;
      }
      final int length = lead >= 0xF8 ? 0 : lead >= 0xF0 ? 3 : lead >= 0xE0 ? 2 : lead >= 0xC0 ? 1 : 0;
      int tried = 1;
      int codePoint = length == 0 ? -1 : lead & (0x3F >> length);
      for (int k = 1; k <= length && codePoint >= 0; k++) {
        if (i + k >= codes.length) {
          codePoint = -1;
          break;
        }
        final int next = codes[i + k];
        tried++;
        codePoint = next >= 0x80 && next <= 0xBF ? (codePoint << 6) | (next & 0x3F) : -1;
      }
      if (codePoint < 0 || codePoint > Character.MAX_CODE_POINT) {
        if (invalid != null) {
          int[] sequence = new int[tried];
          System.arraycopy(codes, i, sequence, 0, tried);
          invalid.accept(sequence);
        }
        buffer.append((char) lead);
        i++;
        continue;
      }
      buffer.appendCodePoint(codePoint);
      i += length + 1;
    }
    return buffer.toString();
  }

  /**
   * The characters of the UTF-8 bytes of <code>text</code>, one character for each byte: what a
   * text format is exported as into a string.
   */
  public static String toByteCharacters(String text) {
    return new String(text.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
  }

  /**
   * The text of a string of UTF-8 bytes, one character for each byte, as
   * {@link #toByteCharacters(String)} writes it. A string which is not UTF-8 as a whole - it has a
   * character above 255 or a byte sequence which is no character - is returned as it is.
   */
  public static String fromByteCharacters(String byteCharacters) {
    byte[] bytes = new byte[byteCharacters.length()];
    boolean ascii = true;
    for (int i = 0; i < byteCharacters.length(); i++) {
      char c = byteCharacters.charAt(i);
      if (c > 0xFF) {
        return byteCharacters;
      }
      ascii &= c < 0x80;
      bytes[i] = (byte) c;
    }
    if (ascii) {
      return byteCharacters;
    }
    try {
      return StandardCharsets.UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(bytes))
          .toString();
    } catch (CharacterCodingException cce) {
      return byteCharacters;
    }
  }
}
