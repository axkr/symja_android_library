package org.matheclipse.ortools.sat;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import java.util.function.UnaryOperator;

/**
 * Runtime support of the generated message classes: the protobuf binary wire format (writer and
 * reader), the protobuf text format parser and the modifiable list views of the builders. This
 * replaces the parts of the protobuf runtime used by {@code toByteArray()},
 * {@code parseFrom(byte[])}, {@code mergeFrom(byte[])} and {@code parseTextFormat(String)}.
 *
 * <p>
 * The binary encoding is the canonical one of the C++ serializer: fields in field number order,
 * proto3 scalars only when they differ from their default, packed proto3 repeated scalars,
 * unpacked proto2 repeated scalars, negative {@code int32} values as 10 byte varints. The reader
 * accepts packed and unpacked repeated scalars and skips unknown fields (they are not kept).
 */
final class ProtoWire {
  static final int VARINT = 0;
  static final int FIXED64 = 1;
  static final int LEN = 2;
  static final int START_GROUP = 3;
  static final int END_GROUP = 4;
  static final int FIXED32 = 5;

  /** Maximal nesting depth of messages accepted by the reader, as in the C++ runtime. */
  static final int MAX_DEPTH = 100;

  private ProtoWire() {}

  /** Thrown by the binary reader and the text parser on malformed input. */
  static final class ParseError extends RuntimeException {
    private static final long serialVersionUID = 1L;

    ParseError(String message) {
      super(message);
    }
  }

  // --------------------------------------------------------------------------------------- writer

  /** Growable output buffer of the binary encoding. */
  static final class Writer {
    private byte[] buf = new byte[32];
    private int size;

    private void ensure(int n) {
      if (size + n > buf.length) {
        buf = java.util.Arrays.copyOf(buf, Math.max(buf.length * 2, size + n));
      }
    }

    void rawByte(int b) {
      ensure(1);
      buf[size++] = (byte) b;
    }

    void rawVarint(long v) {
      ensure(10);
      while ((v & ~0x7FL) != 0) {
        buf[size++] = (byte) ((v & 0x7F) | 0x80);
        v >>>= 7;
      }
      buf[size++] = (byte) v;
    }

    void rawFixed64(long v) {
      ensure(8);
      for (int k = 0; k < 8; k++) {
        buf[size++] = (byte) (v >>> (8 * k));
      }
    }

    void rawFixed32(int v) {
      ensure(4);
      for (int k = 0; k < 4; k++) {
        buf[size++] = (byte) (v >>> (8 * k));
      }
    }

    void rawBytes(byte[] b, int off, int len) {
      ensure(len);
      System.arraycopy(b, off, buf, size, len);
      size += len;
    }

    void tag(int field, int wireType) {
      rawVarint(((long) field << 3) | wireType);
    }

    void int32(int field, int v) {
      tag(field, VARINT);
      rawVarint(v); // sign extended, as the C++ encoder does
    }

    void int64(int field, long v) {
      tag(field, VARINT);
      rawVarint(v);
    }

    void bool(int field, boolean v) {
      tag(field, VARINT);
      rawByte(v ? 1 : 0);
    }

    void dbl(int field, double v) {
      tag(field, FIXED64);
      rawFixed64(Double.doubleToRawLongBits(v));
    }

    void flt(int field, float v) {
      tag(field, FIXED32);
      rawFixed32(Float.floatToRawIntBits(v));
    }

    void string(int field, String v) {
      byte[] b = v.getBytes(StandardCharsets.UTF_8);
      tag(field, LEN);
      rawVarint(b.length);
      rawBytes(b, 0, b.length);
    }

    /** Writes {@code sub} as a length delimited field. */
    void bytes(int field, Writer sub) {
      tag(field, LEN);
      rawVarint(sub.size);
      rawBytes(sub.buf, 0, sub.size);
    }

    void packedInt32(int field, java.util.List<Integer> values) {
      Writer sub = new Writer();
      for (Integer v : values) {
        sub.rawVarint(v.intValue());
      }
      bytes(field, sub);
    }

    void packedInt64(int field, java.util.List<Long> values) {
      Writer sub = new Writer();
      for (Long v : values) {
        sub.rawVarint(v.longValue());
      }
      bytes(field, sub);
    }

    void packedBool(int field, java.util.List<Boolean> values) {
      Writer sub = new Writer();
      for (Boolean v : values) {
        sub.rawByte(v.booleanValue() ? 1 : 0);
      }
      bytes(field, sub);
    }

    void packedDouble(int field, java.util.List<Double> values) {
      Writer sub = new Writer();
      for (Double v : values) {
        sub.rawFixed64(Double.doubleToRawLongBits(v.doubleValue()));
      }
      bytes(field, sub);
    }

    void packedFloat(int field, java.util.List<Float> values) {
      Writer sub = new Writer();
      for (Float v : values) {
        sub.rawFixed32(Float.floatToRawIntBits(v.floatValue()));
      }
      bytes(field, sub);
    }

    <E> void packedEnum(int field, java.util.List<E> values, ToIntFunction<E> number) {
      Writer sub = new Writer();
      for (E v : values) {
        sub.rawVarint(number.applyAsInt(v));
      }
      bytes(field, sub);
    }

    byte[] toByteArray() {
      return java.util.Arrays.copyOf(buf, size);
    }
  }

  // --------------------------------------------------------------------------------------- reader

  /** Reader of the binary encoding over a byte array range. */
  static final class Reader {
    private final byte[] buf;
    private int pos;
    private final int limit;
    private final int depth;

    Reader(byte[] buf) {
      this(buf, 0, buf.length, 0);
    }

    private Reader(byte[] buf, int pos, int limit, int depth) {
      this.buf = buf;
      this.pos = pos;
      this.limit = limit;
      this.depth = depth;
    }

    boolean atEnd() {
      return pos >= limit;
    }

    private static ParseError truncated() {
      return new ParseError("truncated message");
    }

    long varint() {
      long result = 0;
      for (int shift = 0; shift < 64; shift += 7) {
        if (pos >= limit) {
          throw truncated();
        }
        int b = buf[pos++];
        result |= (long) (b & 0x7F) << shift;
        if ((b & 0x80) == 0) {
          return result;
        }
      }
      throw new ParseError("malformed varint");
    }

    /** Reads a tag; returns {@code fieldNumber << 3 | wireType}. */
    int tag() {
      long t = varint();
      if ((t >>> 32) != 0 || (t >>> 3) == 0) {
        throw new ParseError("invalid tag");
      }
      return (int) t;
    }

    long fixed64() {
      if (limit - pos < 8) {
        throw truncated();
      }
      long v = 0;
      for (int k = 0; k < 8; k++) {
        v |= (long) (buf[pos++] & 0xFF) << (8 * k);
      }
      return v;
    }

    int fixed32() {
      if (limit - pos < 4) {
        throw truncated();
      }
      int v = 0;
      for (int k = 0; k < 4; k++) {
        v |= (buf[pos++] & 0xFF) << (8 * k);
      }
      return v;
    }

    double dbl() {
      return Double.longBitsToDouble(fixed64());
    }

    float flt() {
      return Float.intBitsToFloat(fixed32());
    }

    private int length() {
      long len = varint();
      if (len < 0 || len > limit - pos) {
        throw truncated();
      }
      return (int) len;
    }

    /** Reads a length delimited nested message. */
    Reader message() {
      int len = length();
      if (depth + 1 > MAX_DEPTH) {
        throw new ParseError("message nesting too deep");
      }
      Reader r = new Reader(buf, pos, pos + len, depth + 1);
      pos += len;
      return r;
    }

    /** Reads the payload of a packed repeated field. */
    Reader packed() {
      int len = length();
      Reader r = new Reader(buf, pos, pos + len, depth);
      pos += len;
      return r;
    }

    /**
     * Reads a string; with {@code strict} (proto3) invalid UTF-8 is a parse error, otherwise it is
     * replaced.
     */
    String string(boolean strict) {
      int len = length();
      int start = pos;
      pos += len;
      if (!strict) {
        return new String(buf, start, len, StandardCharsets.UTF_8);
      }
      CharsetDecoder dec = StandardCharsets.UTF_8.newDecoder()
          .onMalformedInput(CodingErrorAction.REPORT)
          .onUnmappableCharacter(CodingErrorAction.REPORT);
      try {
        CharBuffer cb = dec.decode(ByteBuffer.wrap(buf, start, len));
        return cb.toString();
      } catch (CharacterCodingException e) {
        throw new ParseError("invalid UTF-8 in string field");
      }
    }

    /** Skips the value of a field with the given tag (unknown field or unexpected wire type). */
    void skip(int tag) {
      switch (tag & 7) {
        case VARINT:
          varint();
          return;
        case FIXED64:
          fixed64();
          return;
        case LEN: {
          int len = length(); // not "pos += length()": the old pos would be used
          pos += len;
          return;
        }
        case START_GROUP: {
          int field = tag >>> 3;
          if (depth + 1 > MAX_DEPTH) {
            throw new ParseError("message nesting too deep");
          }
          while (true) {
            if (atEnd()) {
              throw truncated();
            }
            int t = tag();
            if ((t & 7) == END_GROUP) {
              if ((t >>> 3) != field) {
                throw new ParseError("mismatched end group");
              }
              return;
            }
            skip(t);
          }
        }
        case FIXED32:
          fixed32();
          return;
        default:
          throw new ParseError("invalid wire type");
      }
    }
  }

  // ------------------------------------------------------------------------------ text format

  /** Tokenizer and value parser of the protobuf text format. */
  static final class Text {
    private final String s;
    private int pos;
    private int line = 1;
    private int lineStart;
    // current token
    private String tok;
    private int tokLine;
    private int tokCol;
    private char kind; // 'i' identifier, 'n' number, 's' string, 'p' punctuation, 'e' end

    Text(CharSequence text) {
      this.s = text.toString();
      next();
    }

    ParseError error(String message) {
      return new ParseError(tokLine + ":" + tokCol + ": " + message);
    }

    private void skipSpace() {
      while (pos < s.length()) {
        char c = s.charAt(pos);
        if (c == '\n') {
          pos++;
          line++;
          lineStart = pos;
        } else if (c == ' ' || c == '\t' || c == '\r' || c == '\f' || c == 0x0B) {
          pos++;
        } else if (c == '#') {
          while (pos < s.length() && s.charAt(pos) != '\n') {
            pos++;
          }
        } else {
          return;
        }
      }
    }

    private static boolean isIdentStart(char c) {
      return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private static boolean isIdentPart(char c) {
      return isIdentStart(c) || (c >= '0' && c <= '9');
    }

    private void next() {
      skipSpace();
      tokLine = line;
      tokCol = pos - lineStart + 1;
      if (pos >= s.length()) {
        tok = "";
        kind = 'e';
        return;
      }
      int start = pos;
      char c = s.charAt(pos);
      if (isIdentStart(c)) {
        while (pos < s.length() && isIdentPart(s.charAt(pos))) {
          pos++;
        }
        kind = 'i';
      } else if ((c >= '0' && c <= '9')
          || (c == '.' && pos + 1 < s.length() && Character.isDigit(s.charAt(pos + 1)))) {
        scanNumber();
        kind = 'n';
      } else if (c == '"' || c == '\'') {
        pos++;
        while (true) {
          if (pos >= s.length() || s.charAt(pos) == '\n') {
            tokLine = line;
            throw error("String literals cannot cross line boundaries.");
          }
          char d = s.charAt(pos);
          if (d == '\\' && pos + 1 < s.length()) {
            pos += 2;
          } else if (d == c) {
            pos++;
            break;
          } else {
            pos++;
          }
        }
        kind = 's';
      } else {
        pos++;
        kind = 'p';
      }
      tok = s.substring(start, pos);
    }

    private void scanNumber() {
      if (s.charAt(pos) == '0' && pos + 1 < s.length()
          && (s.charAt(pos + 1) == 'x' || s.charAt(pos + 1) == 'X')) {
        pos += 2;
        while (pos < s.length() && Character.digit(s.charAt(pos), 16) >= 0) {
          pos++;
        }
      } else {
        while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
          pos++;
        }
        if (pos < s.length() && s.charAt(pos) == '.') {
          pos++;
          while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
            pos++;
          }
        }
        if (pos < s.length() && (s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {
          int save = pos;
          pos++;
          if (pos < s.length() && (s.charAt(pos) == '+' || s.charAt(pos) == '-')) {
            pos++;
          }
          if (pos < s.length() && Character.isDigit(s.charAt(pos))) {
            while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
              pos++;
            }
          } else {
            pos = save;
          }
        }
        if (pos < s.length() && (s.charAt(pos) == 'f' || s.charAt(pos) == 'F')) {
          pos++;
        }
      }
      if (pos < s.length() && isIdentPart(s.charAt(pos))) {
        throw error("Need space between number and identifier.");
      }
    }

    boolean atEnd() {
      return kind == 'e';
    }

    /** Consumes the punctuation {@code p} if it is the current token. */
    boolean tryConsume(String p) {
      if ((kind == 'p' || kind == 'i') && tok.equals(p)) {
        next();
        return true;
      }
      return false;
    }

    void consume(String p) {
      if (!tryConsume(p)) {
        throw error("Expected \"" + p + "\", found \"" + tok + "\".");
      }
    }

    /** Consumes an optional field separator. */
    void separator() {
      if (!tryConsume(";")) {
        tryConsume(",");
      }
    }

    /** Reads a field name; extensions and Any expansions are not supported. */
    String fieldName() {
      if (kind != 'i') {
        throw error("Expected identifier, got: " + tok);
      }
      String name = tok;
      next();
      return name;
    }

    /** Consumes the start of a message value and returns the expected closing delimiter. */
    String openMessage() {
      if (tryConsume("{")) {
        return "}";
      }
      if (tryConsume("<")) {
        return ">";
      }
      throw error("Expected \"{\", found \"" + tok + "\".");
    }

    /** Consumes the closing delimiter {@code end} if it is the current token. */
    boolean atMessageEnd(String end) {
      if (end == null) {
        return atEnd();
      }
      if (atEnd()) {
        throw error("Expected \"" + end + "\".");
      }
      return tryConsume(end);
    }

    /** Starts a scalar or message list {@code [a, b]}; returns false if the value is not a list. */
    boolean startList() {
      return tryConsume("[");
    }

    /** After a list element: true if another element follows. */
    boolean nextInList() {
      if (tryConsume(",")) {
        return true;
      }
      consume("]");
      return false;
    }

    /** True if the current token closes an empty list (and consumes it). */
    boolean emptyList() {
      return tryConsume("]");
    }

    /** Reads an integer in {@code [min, max]}. */
    long integer(long min, long max) {
      boolean neg = tryConsume("-");
      if (kind != 'n') {
        throw error("Expected integer, got: " + tok);
      }
      String t = tok;
      if (t.indexOf('.') >= 0 || ((t.indexOf('e') >= 0 || t.indexOf('E') >= 0
          || t.endsWith("f") || t.endsWith("F")) && !t.startsWith("0x") && !t.startsWith("0X"))) {
        throw error("Expected integer, got: " + t);
      }
      long magnitude;
      try {
        if (t.startsWith("0x") || t.startsWith("0X")) {
          magnitude = Long.parseUnsignedLong(t.substring(2), 16);
        } else if (t.length() > 1 && t.charAt(0) == '0') {
          magnitude = Long.parseUnsignedLong(t.substring(1), 8);
        } else {
          magnitude = Long.parseUnsignedLong(t);
        }
      } catch (NumberFormatException e) {
        throw error("Integer out of range (" + t + ")");
      }
      long value;
      if (neg) {
        if (Long.compareUnsigned(magnitude, Long.MIN_VALUE) > 0) {
          throw error("Integer out of range (-" + t + ")");
        }
        value = -magnitude;
      } else {
        if (magnitude < 0) {
          throw error("Integer out of range (" + t + ")");
        }
        value = magnitude;
      }
      if (value < min || value > max) {
        throw error("Integer out of range (" + (neg ? "-" : "") + t + ")");
      }
      next();
      return value;
    }

    /** Reads a floating point value (also integers, {@code inf}, {@code infinity}, {@code nan}). */
    double floating() {
      boolean neg = tryConsume("-");
      double v;
      if (kind == 'i') {
        String l = tok.toLowerCase(java.util.Locale.ROOT);
        if (l.equals("inf") || l.equals("infinity")) {
          v = Double.POSITIVE_INFINITY;
        } else if (l.equals("nan")) {
          v = Double.NaN;
        } else {
          throw error("Expected double, got: " + tok);
        }
      } else if (kind == 'n') {
        String t = tok;
        if (t.startsWith("0x") || t.startsWith("0X")) {
          try {
            v = (double) Long.parseUnsignedLong(t.substring(2), 16);
          } catch (NumberFormatException e) {
            throw error("Expected double, got: " + t);
          }
        } else {
          if (t.endsWith("f") || t.endsWith("F")) {
            t = t.substring(0, t.length() - 1);
          }
          try {
            v = Double.parseDouble(t);
          } catch (NumberFormatException e) {
            throw error("Expected double, got: " + tok);
          }
        }
      } else {
        throw error("Expected double, got: " + tok);
      }
      next();
      return neg ? -v : v;
    }

    boolean bool() {
      if (kind == 'i') {
        String t = tok;
        if (t.equals("true") || t.equals("True") || t.equals("t")) {
          next();
          return true;
        }
        if (t.equals("false") || t.equals("False") || t.equals("f")) {
          next();
          return false;
        }
      } else if (kind == 'n') {
        if (tok.equals("1")) {
          next();
          return true;
        }
        if (tok.equals("0")) {
          next();
          return false;
        }
      }
      throw error("Invalid value for boolean field: \"" + tok + "\".");
    }

    /** Reads a string value; adjacent string literals are concatenated. */
    String string() {
      if (kind != 's') {
        throw error("Expected string, got: " + tok);
      }
      java.io.ByteArrayOutputStream bytes = new java.io.ByteArrayOutputStream();
      while (kind == 's') {
        unescape(tok.substring(1, tok.length() - 1), bytes);
        next();
      }
      return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
    }

    private void unescape(String body, java.io.ByteArrayOutputStream out) {
      for (int i = 0; i < body.length(); i++) {
        char c = body.charAt(i);
        if (c != '\\') {
          if (c < 0x80) {
            out.write(c);
          } else {
            int end = i + 1;
            if (Character.isHighSurrogate(c) && end < body.length()) {
              end++;
            }
            byte[] b = body.substring(i, end).getBytes(StandardCharsets.UTF_8);
            out.write(b, 0, b.length);
            i = end - 1;
          }
          continue;
        }
        if (++i >= body.length()) {
          throw error("Invalid escape sequence in string literal.");
        }
        char e = body.charAt(i);
        switch (e) {
          case 'a':
            out.write(7);
            break;
          case 'b':
            out.write(8);
            break;
          case 'f':
            out.write(12);
            break;
          case 'n':
            out.write('\n');
            break;
          case 'r':
            out.write('\r');
            break;
          case 't':
            out.write('\t');
            break;
          case 'v':
            out.write(11);
            break;
          case '\\':
          case '\'':
          case '"':
          case '?':
            out.write(e);
            break;
          case 'x':
          case 'X': {
            int v = 0;
            int n = 0;
            while (n < 2 && i + 1 < body.length() && Character.digit(body.charAt(i + 1), 16) >= 0) {
              v = v * 16 + Character.digit(body.charAt(++i), 16);
              n++;
            }
            if (n == 0) {
              throw error("Invalid escape sequence in string literal.");
            }
            out.write(v);
            break;
          }
          case 'u':
          case 'U': {
            int digits = e == 'u' ? 4 : 8;
            if (i + digits >= body.length()) {
              throw error("Invalid escape sequence in string literal.");
            }
            int cp;
            try {
              cp = Integer.parseInt(body.substring(i + 1, i + 1 + digits), 16);
            } catch (NumberFormatException ex) {
              throw error("Invalid escape sequence in string literal.");
            }
            i += digits;
            if (!Character.isValidCodePoint(cp)) {
              throw error("Invalid escape sequence in string literal.");
            }
            byte[] b = new String(Character.toChars(cp)).getBytes(StandardCharsets.UTF_8);
            out.write(b, 0, b.length);
            break;
          }
          default:
            if (e >= '0' && e <= '7') {
              int v = e - '0';
              int n = 1;
              while (n < 3 && i + 1 < body.length() && body.charAt(i + 1) >= '0'
                  && body.charAt(i + 1) <= '7') {
                v = v * 8 + (body.charAt(++i) - '0');
                n++;
              }
              out.write(v & 0xFF);
            } else {
              throw error("Invalid escape sequence in string literal.");
            }
        }
      }
    }

    /**
     * Reads an enum value given by name or number and returns its number. For a closed enum
     * (proto2) an unknown number is an error; the name {@code UNRECOGNIZED} is never accepted.
     */
    <E extends Enum<E>> int enumValue(Class<E> type, IntFunction<E> forNumber,
        ToIntFunction<E> number, boolean closed, String field) {
      if (kind == 'i') {
        String name = tok;
        E e = null;
        if (!name.equals("UNRECOGNIZED")) {
          try {
            e = Enum.valueOf(type, name);
          } catch (IllegalArgumentException ex) {
            e = null;
          }
        }
        if (e == null) {
          throw error("Unknown enumeration value of \"" + name + "\" for field \"" + field + "\".");
        }
        next();
        return number.applyAsInt(e);
      }
      if (kind == 'n' || (kind == 'p' && tok.equals("-"))) {
        long v = integer(Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (closed && forNumber.apply((int) v) == null) {
          throw error("Unknown enumeration value of \"" + v + "\" for field \"" + field + "\".");
        }
        return (int) v;
      }
      throw error("Expected integer or identifier, got: " + tok);
    }
  }

  /** Text parser error for a field set twice. */
  static ParseError duplicate(Text t, String field) {
    return t.error("Non-repeated field \"" + field + "\" is specified multiple times.");
  }

  /** Text parser error for two members of the same oneof. */
  static ParseError oneofConflict(Text t, String field, String other, String oneof) {
    return t.error("Field \"" + field + "\" is specified along with field \"" + other
        + "\", another member of oneof \"" + oneof + "\".");
  }

  /** Returns {@code e}; a value of an open enum unknown to the Java enum is a parse error. */
  static <E> E known(Text t, E e, String field) {
    if (e == null) {
      throw t.error("Unknown enumeration value for repeated field \"" + field + "\".");
    }
    return e;
  }

  /** Text parser error for an unknown field name. */
  static ParseError unknownField(Text t, String type, String field) {
    return t.error("Message type \"" + type + "\" has no field named \"" + field + "\".");
  }

  // ------------------------------------------------------------------------------- list views

  /**
   * Modifiable view of a repeated field of a builder, like the list returned by the
   * {@code getXxxList()} methods of the OR-Tools 10.0 message wrappers. Null elements are
   * rejected; {@code copy} (if not null) is applied to stored elements, so messages are kept as
   * immutable copies.
   */
  static final class ListView<T> extends AbstractList<T> implements RandomAccess {
    private final ArrayList<T> list;
    private final UnaryOperator<T> copy;

    ListView(ArrayList<T> list, UnaryOperator<T> copy) {
      this.list = list;
      this.copy = copy;
    }

    private T store(T e) {
      Objects.requireNonNull(e);
      return copy == null ? e : copy.apply(e);
    }

    @Override
    public T get(int index) {
      return list.get(index);
    }

    @Override
    public int size() {
      return list.size();
    }

    @Override
    public T set(int index, T element) {
      return list.set(index, store(element));
    }

    @Override
    public void add(int index, T element) {
      list.add(index, store(element));
      modCount++;
    }

    @Override
    public T remove(int index) {
      modCount++;
      return list.remove(index);
    }
  }
}
