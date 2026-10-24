package org.matheclipse.ortools.sat;

import java.io.IOException;

/**
 * Text format output of the plain Java message classes, and text format input of
 * {@link SatParameters}, replacing the corresponding methods of
 * {@code com.google.protobuf.TextFormat}. The messages of {@code cp_model.proto} parse the text
 * format with their builders' {@code parseTextFormat(String)} method, like the OR-Tools 10.0
 * wrappers.
 */
public final class TextFormat {
  private TextFormat() {}

  /** Thrown by {@link TextFormat#merge(CharSequence, SatParameters.Builder)} on invalid input. */
  public static class ParseException extends IOException {
    private static final long serialVersionUID = 1L;

    /** Creates the exception; the message starts with {@code line:column:}. */
    public ParseException(String message) {
      super(message);
    }
  }

  /** Returns the message in protobuf text format, one field per line. */
  public static String printToString(ProtoMessage message) {
    ProtoPrinter printer = new ProtoPrinter(false);
    message.printTo(printer);
    return printer.toString();
  }

  /** Returns the message in protobuf text format on a single line. */
  public static String shortDebugString(ProtoMessage message) {
    ProtoPrinter printer = new ProtoPrinter(true);
    message.printTo(printer);
    return printer.toString();
  }

  /**
   * Merges the parameters given in protobuf text format into {@code builder}, with the semantics
   * of the protobuf-java parser: a non-repeated field given again overwrites the previous value,
   * repeated fields are appended.
   *
   * @throws ParseException on a syntax error or an unknown field or enum value
   */
  public static void merge(CharSequence input, SatParameters.Builder builder)
      throws ParseException {
    try {
      builder.mergeText(new ProtoWire.Text(input), null, false);
    } catch (ProtoWire.ParseError e) {
      throw new ParseException(e.getMessage());
    }
  }
}
