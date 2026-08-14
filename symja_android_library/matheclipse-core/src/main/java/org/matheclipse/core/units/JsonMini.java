package org.matheclipse.core.units;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal dependency-free JSON reader used by {@link UnitRegistry} to load <code>units.json</code>
 * (objects as insertion-ordered maps, arrays as lists, strings, longs, doubles, booleans, null).
 * Not a general-purpose JSON parser.
 */
final class JsonMini {

  private final String s;
  private int pos;

  private JsonMini(String s) {
    this.s = s;
  }

  static Object parse(String s) {
    JsonMini r = new JsonMini(s);
    Object v = r.readValue();
    r.skipWhitespace();
    if (r.pos < r.s.length()) {
      throw new IllegalArgumentException("trailing JSON content at offset " + r.pos);
    }
    return v;
  }

  @SuppressWarnings("unchecked")
  static Map<String, Object> parseObject(String s) {
    Object v = parse(s);
    if (!(v instanceof Map)) {
      throw new IllegalArgumentException("expected a JSON object at top level");
    }
    return (Map<String, Object>) v;
  }

  private void skipWhitespace() {
    while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
      pos++;
    }
  }

  private char peek() {
    if (pos >= s.length()) {
      throw new IllegalArgumentException("unexpected end of JSON");
    }
    return s.charAt(pos);
  }

  private void expect(char c) {
    if (pos >= s.length() || s.charAt(pos) != c) {
      throw new IllegalArgumentException("expected '" + c + "' at offset " + pos);
    }
    pos++;
  }

  private Object readValue() {
    skipWhitespace();
    char c = peek();
    switch (c) {
      case '{':
        return readObject();
      case '[':
        return readArray();
      case '"':
        return readString();
      case 't':
        readLiteral("true");
        return Boolean.TRUE;
      case 'f':
        readLiteral("false");
        return Boolean.FALSE;
      case 'n':
        readLiteral("null");
        return null;
      default:
        return readNumber();
    }
  }

  private void readLiteral(String literal) {
    if (!s.startsWith(literal, pos)) {
      throw new IllegalArgumentException("invalid literal at offset " + pos);
    }
    pos += literal.length();
  }

  private Map<String, Object> readObject() {
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

  private List<Object> readArray() {
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

  private String readString() {
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

  private Object readNumber() {
    int start = pos;
    while (pos < s.length() && "+-.eE0123456789".indexOf(s.charAt(pos)) >= 0) {
      pos++;
    }
    String text = s.substring(start, pos);
    try {
      return Long.valueOf(text);
    } catch (NumberFormatException e) {
      return Double.valueOf(text);
    }
  }
}
