package org.matheclipse.core.form;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apfloat;

/** Convert a <code>Apfloat</code> value into a string similar to the Mathematica output format. */
public class ApfloatToMMA {
  private static final Logger LOGGER = LogManager.getLogger(ApfloatToMMA.class);

  private static final class FormattingWriter extends FilterWriter {
    private long count;

    private boolean dotWritten;

    private boolean eWritten;

    private StringWriter exponent;

    public FormattingWriter(Writer out) {
      super(out);
      exponent = new StringWriter();
    }

    @Override
    public void write(int c) throws IOException {
      switch (c) {
        case '.':
          out.write(c);
          dotWritten = true;
          break;
        case 'e':
          eWritten = true;
          break;
        default:
          (eWritten ? exponent : out).write(c);
          if (!eWritten) {
            count++;
          }
      }
    }

    @Override
    public void write(char cbuf[], int off, int len) throws IOException {
      while (len-- > 0) {
        write(cbuf[off++]);
      }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
      while (len-- > 0) {
        write(str.charAt(off++));
      }
    }

    public boolean isDotWritten() {
      return dotWritten;
    }

    public long getCount() {
      return count;
    }

    public long getExponent() {
      return exponent.getBuffer().length() > 0 ? Long.parseLong(exponent.toString()) : 0;
    }
  }

  private static void toMMA(Apfloat x, Writer out, long significantFigures, OutputStyle style)
      throws IOException {
    if (style == OutputStyle.MATHML) {
      out.write("<mrow><mn>");
    }
    FormattingWriter formattingWriter = new FormattingWriter(out);
    x.writeTo(formattingWriter);
    long count = formattingWriter.getCount();
    if (count < significantFigures) {
      if (!formattingWriter.isDotWritten()) {
        out.write('.');
      }

      while (count < significantFigures) {
        out.write('0');
        count++;
      }
    }
    if (style == OutputStyle.MATHML) {
      out.write("</mn>");
    }
    long exponent = formattingWriter.getExponent();
    if (exponent != 0) {
      if (style == OutputStyle.MATHML) {
        out.write("<mo>&#0183;</mo><msup><mn>10</mn><mn>");
        out.write(String.valueOf(exponent));
        out.write("</mn></msup>");
      } else if (style == OutputStyle.TEX) {
        out.write("*10^");
        out.write('{');
        out.write(String.valueOf(exponent));
        out.write('}');
      } else {
        out.write("*10^");
        out.write(String.valueOf(exponent));
      }
    }
    if (style == OutputStyle.MATHML) {
      out.write("</mrow>");
    }
  }

  /**
   * Convert an <code>Apfloat</code> value into a string similar to the Mathematica output format.
   *
   * @param buf a string builder where the output should be appended
   * @param value
   * @param exponent use scientific notation for all numbers with exponents outside the range <code>
   *     -exponent</code> to <code>exponent</code>.
   * @param significantFigures
   * @param useSignificantFiguresInApfloat if <code>true</code> shorten an Apfloat to the number of
   *        significantFigures if possible
   * @param style {@link OutputStyle#OUTPUT}, {@link OutputStyle#MATHML} or {@link OutputStyle#TEX}
   */
  public static void apfloatToMMA(Appendable buf, Apfloat value, int exponent,
      long significantFigures, boolean useSignificantFiguresInApfloat, OutputStyle style)
      throws IOException {
    final long scale = value.scale();
    if (exponent > 0 && -exponent <= scale && scale <= exponent) {
      if (style == OutputStyle.MATHML) {
        buf.append("<mn>");
      }
      String str = value.toString(true);
      if (useSignificantFiguresInApfloat) {
        int indx = str.indexOf('.');
        if (indx > 0 && indx + (int) significantFigures <= str.length()) {
          str = str.substring(0, Math.min(str.length(), indx + 1 + (int) significantFigures));
        }
      }
      buf.append(str);
      if (style == OutputStyle.MATHML) {
        buf.append("</mn>");
      }
      return;
    }
    String str = value.toString();
    int index = str.indexOf('e');
    if (index > 0) {
      StringWriter stw = new StringWriter();
      try {
        toMMA(value, stw, significantFigures, style);
        str = stw.toString();
        if (useSignificantFiguresInApfloat) {
          int firstIndex = str.indexOf('.');
          if (firstIndex > 0) {
            int lastIndex = str.indexOf('*', firstIndex + 1);
            if (lastIndex > 0 && (lastIndex - firstIndex) > significantFigures) {
              String preStr = str.substring(0, firstIndex + 1);
              String infixStr =
                  str.substring(firstIndex + 1, firstIndex + 1 + (int) significantFigures);
              String postStr = str.substring(lastIndex, str.length());
              str = preStr + infixStr + postStr;
            }
          }
        }
        buf.append(str);
        return;
      } catch (IOException e) {
        LOGGER.error("ApfloatToMMA.apfloatToMMA() failed", e);
      }
    }
    if (style == OutputStyle.MATHML) {
      buf.append("<mn>");
    }
    buf.append(str);
    if (style == OutputStyle.MATHML) {
      buf.append("</mn>");
    }
  }

  /**
   * Convert a <code>Apfloat</code> value into a string similar to the Mathematica output format.
   *
   * @param buf a string builder where the output should be appended
   * @param value the Apfloat value which should be formatted
   * @param exponent use scientific notation for all numbers with exponents outside the range <code>
   *     -exponent</code> to <code>exponent</code>.
   * @param significantFigures the number of significant figures which should be printed
   * @param useSignificantFiguresInApfloat if <code>true</code> shorten an Apfloat to the number of
   *        significantFigures if possible
   */
  public static void apfloatToMMA(StringBuilder buf, Apfloat value, int exponent,
      long significantFigures, boolean useSignificantFiguresInApfloat) {
    try {
      apfloatToMMA(buf, value, exponent, significantFigures, useSignificantFiguresInApfloat,
          OutputStyle.OUTPUT);
    } catch (IOException ioex) {
      LOGGER.error("ApfloatToMMA.apfloatToMMA() failed", ioex);
    }
  }

  /**
   * Convert a <code>Apfloat</code> value into a TeX string.
   *
   * @param buf
   * @param value
   * @param exponent
   * @param significantFigures
   * @param useSignificantFiguresInApfloat if <code>true</code> shorten an Apfloat to the number of
   *        significantFigures if possible
   */
  public static void apfloatToTeX(StringBuilder buf, Apfloat value, int exponent,
      long significantFigures, boolean useSignificantFiguresInApfloat) {
    try {
      apfloatToMMA(buf, value, exponent, significantFigures, useSignificantFiguresInApfloat,
          OutputStyle.TEX);
    } catch (IOException ioex) {
      LOGGER.error("ApfloatToMMA.apfloatToTeX() failed", ioex);
    }
  }

  /**
   * Convert a <code>Apfloat</code> value into a MathML string.
   *
   * @param buf
   * @param value
   * @param exponent
   * @param significantFigures
   * @param useSignificantFiguresInApfloat if <code>true</code> shorten an Apfloat to the number of
   *        significantFigures if possible
   */
  public static void apfloatToMathML(StringBuilder buf, Apfloat value, int exponent,
      long significantFigures, boolean useSignificantFiguresInApfloat) {
    try {
      apfloatToMMA(buf, value, exponent, significantFigures, useSignificantFiguresInApfloat,
          OutputStyle.MATHML);
    } catch (IOException ioex) {
      LOGGER.error("ApfloatToMMA.apfloatToMathML() failed", ioex);
    }
  }

  public static void apfloatToMMA(StringBuilder buf, double value, int exponent,
      int significantFigures) {
    apfloatToMMA(buf, new Apfloat(value), exponent, significantFigures, false);
  }

  private ApfloatToMMA() {}
}
