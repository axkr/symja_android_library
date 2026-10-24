package org.matheclipse.ortools.sat;

/**
 * Writes {@link ProtoMessage}s in protobuf text format, either one field per line with nested
 * messages indented by two spaces (the format of {@code toString()}), or on a single line (the
 * "short debug string" format). This replaces the text format printer of the protobuf runtime
 * used by OR-Tools.
 */
public final class ProtoPrinter {
  private final StringBuilder out = new StringBuilder();
  private final boolean singleLine;
  private int depth;

  /** Creates a printer; {@code singleLine} selects the short debug string format. */
  public ProtoPrinter(boolean singleLine) {
    this.singleLine = singleLine;
  }

  private void startField(String name) {
    if (singleLine) {
      if (out.length() > 0) {
        out.append(' ');
      }
    } else {
      for (int i = 0; i < depth; i++) {
        out.append("  ");
      }
    }
    out.append(name);
  }

  private void endField() {
    if (!singleLine) {
      out.append('\n');
    }
  }

  /** Prints {@code name: value} for a number, boolean or string value. */
  public void printScalar(String name, Object value) {
    startField(name);
    out.append(": ");
    if (value instanceof String) {
      out.append('"').append(escape((String) value)).append('"');
    } else if (value instanceof Double || value instanceof Float) {
      out.append(formatDouble(((Number) value).doubleValue()));
    } else {
      out.append(value);
    }
    endField();
  }

  /** Prints {@code name: CONSTANT}. */
  public void printEnum(String name, String constant) {
    startField(name);
    out.append(": ").append(constant);
    endField();
  }

  /** Prints {@code name { ... }} with the fields of the nested message. */
  public void printMessage(String name, ProtoMessage message) {
    startField(name);
    out.append(" {");
    if (!singleLine) {
      out.append('\n');
    }
    depth++;
    message.printTo(this);
    depth--;
    if (singleLine) {
      out.append(" }");
    } else {
      for (int i = 0; i < depth; i++) {
        out.append("  ");
      }
      out.append("}\n");
    }
  }

  /** Formats a double like the C++ text format printer (shortest representation). */
  static String formatDouble(double v) {
    if (Double.isNaN(v)) {
      return "nan";
    }
    if (Double.isInfinite(v)) {
      return v > 0 ? "inf" : "-inf";
    }
    if (v == Math.rint(v) && Math.abs(v) < 1e15) {
      return Long.toString((long) v);
    }
    return Double.toString(v);
  }

  /** Escapes a string with the C escape sequences used by the protobuf text format. */
  static String escape(String s) {
    StringBuilder sb = new StringBuilder(s.length());
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      switch (c) {
        case '"':
          sb.append("\\\"");
          break;
        case '\\':
          sb.append("\\\\");
          break;
        case '\n':
          sb.append("\\n");
          break;
        case '\r':
          sb.append("\\r");
          break;
        case '\t':
          sb.append("\\t");
          break;
        case '\'':
          sb.append("\\'");
          break;
        default:
          if (c < 0x20) {
            sb.append(String.format("\\%03o", (int) c));
          } else {
            sb.append(c);
          }
      }
    }
    return sb.toString();
  }

  /** Returns the text printed so far. */
  @Override
  public String toString() {
    return out.toString();
  }
}
