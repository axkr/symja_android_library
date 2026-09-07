package org.matheclipse.tools.units;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal dependency-free JSON reader/writer for this generator (ordered maps, lists, strings,
 * longs, booleans, null). The writer pretty-prints with 2-space indentation and stable key order.
 */
public final class Json {

  private Json() {}

  // ---------------------------------------------------------------- writer

  public static String write(Object value) {
    StringBuilder b = new StringBuilder();
    writeValue(b, value, 0);
    b.append('\n');
    return b.toString();
  }

  private static void writeValue(StringBuilder b, Object value, int indent) {
    if (value == null) {
      b.append("null");
    } else if (value instanceof String) {
      writeString(b, (String) value);
    } else if (value instanceof Boolean || value instanceof Number) {
      b.append(value);
    } else if (value instanceof Map) {
      Map<?, ?> map = (Map<?, ?>) value;
      if (map.isEmpty()) {
        b.append("{}");
        return;
      }
      b.append("{\n");
      int i = 0;
      for (Map.Entry<?, ?> e : map.entrySet()) {
        indent(b, indent + 1);
        writeString(b, String.valueOf(e.getKey()));
        b.append(": ");
        writeValue(b, e.getValue(), indent + 1);
        if (++i < map.size()) {
          b.append(',');
        }
        b.append('\n');
      }
      indent(b, indent);
      b.append('}');
    } else if (value instanceof List) {
      List<?> list = (List<?>) value;
      if (list.isEmpty()) {
        b.append("[]");
        return;
      }
      boolean scalars = list.stream()
          .allMatch(v -> v == null || v instanceof String || v instanceof Number
              || v instanceof Boolean);
      if (scalars) {
        b.append('[');
        for (int i = 0; i < list.size(); i++) {
          if (i > 0) {
            b.append(", ");
          }
          writeValue(b, list.get(i), indent);
        }
        b.append(']');
        return;
      }
      b.append("[\n");
      for (int i = 0; i < list.size(); i++) {
        indent(b, indent + 1);
        writeValue(b, list.get(i), indent + 1);
        if (i + 1 < list.size()) {
          b.append(',');
        }
        b.append('\n');
      }
      indent(b, indent);
      b.append(']');
    } else {
      throw new IllegalArgumentException("cannot serialize " + value.getClass());
    }
  }

  private static void indent(StringBuilder b, int level) {
    for (int i = 0; i < level; i++) {
      b.append("  ");
    }
  }

  private static void writeString(StringBuilder b, String s) {
    b.append('"');
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '"':
          b.append("\\\"");
          break;
        case '\\':
          b.append("\\\\");
          break;
        case '\n':
          b.append("\\n");
          break;
        case '\r':
          b.append("\\r");
          break;
        case '\t':
          b.append("\\t");
          break;
        default:
          if (c < 0x20) {
            b.append(String.format("\\u%04x", (int) c));
          } else {
            b.append(c);
          }
      }
    }
    b.append('"');
  }

  // ---------------------------------------------------------------- reader

  public static Object parse(String s) {
    Reader r = new Reader(s);
    Object v = r.readValue();
    r.skipWhitespace();
    if (!r.atEnd()) {
      throw new IllegalArgumentException("trailing JSON content at offset " + r.pos);
    }
    return v;
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> parseObject(String s) {
    Object v = parse(s);
    if (!(v instanceof Map)) {
      throw new IllegalArgumentException("expected a JSON object at top level");
    }
    return (Map<String, Object>) v;
  }

  private static final class Reader {
    final String s;
    int pos;

    Reader(String s) {
      this.s = s;
    }

    boolean atEnd() {
      return pos >= s.length();
    }

    void skipWhitespace() {
      while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
        pos++;
      }
    }

    char peek() {
      if (atEnd()) {
        throw new IllegalArgumentException("unexpected end of JSON");
      }
      return s.charAt(pos);
    }

    void expect(char c) {
      if (atEnd() || s.charAt(pos) != c) {
        throw new IllegalArgumentException(
            "expected '" + c + "' at offset " + pos + (atEnd() ? " (end)" : ""));
      }
      pos++;
    }

    Object readValue() {
      skipWhitespace();
      char c = peek();
      if (c == '{') {
        return readObject();
      }
      if (c == '[') {
        return readArray();
      }
      if (c == '"') {
        return readString();
      }
      if (c == 't') {
        readLiteral("true");
        return Boolean.TRUE;
      }
      if (c == 'f') {
        readLiteral("false");
        return Boolean.FALSE;
      }
      if (c == 'n') {
        readLiteral("null");
        return null;
      }
      return readNumber();
    }

    void readLiteral(String literal) {
      if (!s.startsWith(literal, pos)) {
        throw new IllegalArgumentException("invalid literal at offset " + pos);
      }
      pos += literal.length();
    }

    Map<String, Object> readObject() {
      LinkedHashMap<String, Object> map = new LinkedHashMap<>();
      expect('{');
      skipWhitespace();
      if (peek() == '}') {
        pos++;
        return map;
      }
      while (true) {
        skipWhitespace();
        String key = readString();
        skipWhitespace();
        expect(':');
        map.put(key, readValue());
        skipWhitespace();
        char c = peek();
        if (c == ',') {
          pos++;
        } else if (c == '}') {
          pos++;
          return map;
        } else {
          throw new IllegalArgumentException("expected ',' or '}' at offset " + pos);
        }
      }
    }

    List<Object> readArray() {
      List<Object> list = new ArrayList<>();
      expect('[');
      skipWhitespace();
      if (peek() == ']') {
        pos++;
        return list;
      }
      while (true) {
        list.add(readValue());
        skipWhitespace();
        char c = peek();
        if (c == ',') {
          pos++;
        } else if (c == ']') {
          pos++;
          return list;
        } else {
          throw new IllegalArgumentException("expected ',' or ']' at offset " + pos);
        }
      }
    }

    String readString() {
      expect('"');
      StringBuilder b = new StringBuilder();
      while (true) {
        char c = s.charAt(pos++);
        if (c == '"') {
          return b.toString();
        }
        if (c == '\\') {
          char esc = s.charAt(pos++);
          switch (esc) {
            case '"':
            case '\\':
            case '/':
              b.append(esc);
              break;
            case 'n':
              b.append('\n');
              break;
            case 'r':
              b.append('\r');
              break;
            case 't':
              b.append('\t');
              break;
            case 'b':
              b.append('\b');
              break;
            case 'f':
              b.append('\f');
              break;
            case 'u':
              b.append((char) Integer.parseInt(s.substring(pos, pos + 4), 16));
              pos += 4;
              break;
            default:
              throw new IllegalArgumentException("invalid escape '\\" + esc + "'");
          }
        } else {
          b.append(c);
        }
      }
    }

    Object readNumber() {
      int start = pos;
      while (!atEnd() && "+-.eE0123456789".indexOf(s.charAt(pos)) >= 0) {
        pos++;
      }
      String text = s.substring(start, pos);
      try {
        return Long.parseLong(text);
      } catch (NumberFormatException e) {
        return Double.parseDouble(text);
      }
    }
  }
}
