package org.matheclipse.core.expression;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.apfloat.Apfloat;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The Wolfram Language's wire format for <code>Compress</code> and <code>Uncompress</code>.
 *
 * <p>
 * A compressed expression is written <code>"1:" + Base64(zlib("!boR" + tokens))</code>, where each
 * token is one tag byte and its payload, little endian throughout:
 *
 * <table>
 * <tr><td><code>i</code></td><td>machine integer, 4 bytes</td></tr>
 * <tr><td><code>I</code></td><td>big integer: length, then its decimal digits</td></tr>
 * <tr><td><code>r</code></td><td>machine real, 8 bytes</td></tr>
 * <tr><td><code>R</code></td><td>arbitrary-precision real, written out as text</td></tr>
 * <tr><td><code>s</code></td><td>symbol: length, then its full name</td></tr>
 * <tr><td><code>S</code></td><td>string: length, then its characters</td></tr>
 * <tr><td><code>f</code></td><td>an expression: how many arguments, the head, then the arguments</td></tr>
 * <tr><td><code>e</code></td><td>packed reals: rank, dimensions, then the numbers</td></tr>
 * <tr><td><code>n</code></td><td>packed integers: type, rank, dimensions, then the numbers</td></tr>
 * <tr><td><code>b</code></td><td>packed bytes: rank, dimensions, then the bytes</td></tr>
 * </table>
 *
 * <p>
 * Anything outside ASCII is written <code>\:XXXX</code> inside a string or a symbol name.
 *
 * <p>
 * This is what a notebook front end reads: the WLJS Notebook keeps compressed strings in its cells
 * and unpacks them in the browser, so a string Symja writes has to be one Mathematica would have
 * written.
 */
public class WMACompress {

  /** What every compressed string begins with once it is unzipped. */
  private static final byte[] MAGIC = {'!', 'b', 'o', 'R'};

  /** The prefix in front of the Base64 text; the number is the format's version. */
  private static final String PREFIX = "1:";

  /**
   * Write an expression the way <code>Compress</code> does.
   *
   * @return the compressed string, or <code>null</code> if the expression holds something this
   *         format has no token for
   */
  public static String compress(IExpr expr) {
    ByteArrayOutputStream tokens = new ByteArrayOutputStream();
    tokens.write(MAGIC, 0, MAGIC.length);
    if (!write(expr, tokens)) {
      return null;
    }
    return PREFIX + Base64.getEncoder().encodeToString(deflate(tokens.toByteArray()));
  }

  /**
   * Read a string written by <code>Compress</code>.
   *
   * @return the expression, or <code>null</code> if the string is not in this format
   */
  public static IExpr uncompress(String compressed, EvalEngine engine) {
    String text = strip(compressed);
    if (!text.startsWith(PREFIX)) {
      return null;
    }
    byte[] data;
    try {
      data = inflate(Base64.getDecoder().decode(text.substring(PREFIX.length())));
    } catch (IllegalArgumentException | DataFormatException e) {
      return null;
    }
    if (data == null || data.length < MAGIC.length) {
      return null;
    }
    for (int i = 0; i < MAGIC.length; i++) {
      if (data[i] != MAGIC[i]) {
        return null;
      }
    }
    Reader reader = new Reader(data, MAGIC.length, engine);
    IExpr result = reader.read();
    return result;
  }

  /**
   * A compressed string copied out of a notebook carries the quotes, backslashes and line breaks
   * of however it was quoted there. Mathematica's own readers drop them, and so does this one.
   */
  private static String strip(String compressed) {
    StringBuilder buf = new StringBuilder(compressed.length());
    for (int i = 0; i < compressed.length(); i++) {
      char c = compressed.charAt(i);
      if (c != '\\' && c != '\n' && c != '\r' && c != '"' && c != ' ') {
        buf.append(c);
      }
    }
    return buf.toString();
  }

  // --- writing ---------------------------------------------------------------------------------

  private static boolean write(IExpr expr, ByteArrayOutputStream out) {
    if (expr instanceof IStringX) {
      out.write('S');
      writeText(expr.toString(), out);
      return true;
    }
    if (expr instanceof ISymbol) {
      out.write('s');
      writeText(fullName((ISymbol) expr), out);
      return true;
    }
    if (expr instanceof IInteger) {
      BigInteger value = ((IInteger) expr).toBigNumerator();
      if (value.bitLength() < 32) {
        out.write('i');
        writeInt(value.intValue(), out);
      } else {
        out.write('I');
        writeText(value.toString(), out);
      }
      return true;
    }
    if (expr instanceof INum) {
      INum number = (INum) expr;
      if (number instanceof Num) {
        out.write('r');
        writeLong(Double.doubleToLongBits(number.doubleValue()), out);
        return true;
      }
      // a number carrying more digits than a machine real is written out as text: its digits, a
      // backtick and how many of them are precise, which is how Mathematica writes one. Plain
      // notation, never Symja's `1.5*10^3` - the reader on the other side is a Wolfram Language
      // parser, and to it that would be a product.
      out.write('R');
      Apfloat value = number.apfloatValue();
      writeText(value.toString(true) + "`" + value.precision(), out);
      return true;
    }
    if (expr instanceof IFraction) {
      IFraction fraction = (IFraction) expr;
      return writeAST(F.binaryAST2(S.Rational, fraction.numerator(), fraction.denominator()), out);
    }
    if (expr instanceof IComplex) {
      IComplex complex = (IComplex) expr;
      return writeAST(F.binaryAST2(S.Complex, complex.re(), complex.im()), out);
    }
    if (expr instanceof ByteArrayExpr) {
      byte[] bytes = ((ByteArrayExpr) expr).toData();
      out.write('b');
      writeInt(1, out);
      writeInt(bytes.length, out);
      out.write(bytes, 0, bytes.length);
      return true;
    }
    if (expr instanceof IPatternObject) {
      // a pattern is an atom here and an expression in the format: `_` travels as Blank[] and
      // `x_` as Pattern[x, Blank[]], which is how Mathematica writes them too
      IAST fullForm = ((IPatternObject) expr).toFullFormAST();
      return fullForm != null && writeAST(fullForm, out);
    }
    if (expr instanceof IAST) {
      return writeAST((IAST) expr, out);
    }
    return false;
  }

  private static boolean writeAST(IAST ast, ByteArrayOutputStream out) {
    out.write('f');
    writeInt(ast.argSize(), out);
    if (!write(ast.head(), out)) {
      return false;
    }
    for (int i = 1; i < ast.size(); i++) {
      if (!write(ast.get(i), out)) {
        return false;
      }
    }
    return true;
  }

  /**
   * The name a symbol is written under: its context and its name, as Mathematica writes them.
   * <code>System`</code> and <code>Global`</code> are the two a reader puts back by itself.
   */
  private static String fullName(ISymbol symbol) {
    Context context = symbol.getContext();
    if (context == Context.SYSTEM || context.isGlobal()) {
      return symbol.getSymbolName();
    }
    return context.completeContextName() + symbol.getSymbolName();
  }

  private static void writeText(String text, ByteArrayOutputStream out) {
    byte[] bytes = escape(text).getBytes(StandardCharsets.UTF_8);
    writeInt(bytes.length, out);
    out.write(bytes, 0, bytes.length);
  }

  /** Everything outside ASCII is written <code>\:XXXX</code>. */
  private static String escape(String text) {
    StringBuilder buf = null;
    for (int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      if (c > 127) {
        if (buf == null) {
          buf = new StringBuilder(text.length() + 8).append(text, 0, i);
        }
        buf.append(String.format("\\:%04X", (int) c));
      } else if (buf != null) {
        buf.append(c);
      }
    }
    return buf == null ? text : buf.toString();
  }

  private static void writeInt(int value, ByteArrayOutputStream out) {
    out.write(value & 0xFF);
    out.write((value >>> 8) & 0xFF);
    out.write((value >>> 16) & 0xFF);
    out.write((value >>> 24) & 0xFF);
  }

  private static void writeLong(long value, ByteArrayOutputStream out) {
    for (int i = 0; i < 8; i++) {
      out.write((int) ((value >>> (8 * i)) & 0xFF));
    }
  }

  private static byte[] deflate(byte[] data) {
    Deflater deflater = new Deflater();
    try {
      deflater.setInput(data);
      deflater.finish();
      ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
      byte[] buffer = new byte[8192];
      while (!deflater.finished()) {
        out.write(buffer, 0, deflater.deflate(buffer));
      }
      return out.toByteArray();
    } finally {
      deflater.end();
    }
  }

  private static byte[] inflate(byte[] data) throws DataFormatException {
    Inflater inflater = new Inflater();
    try {
      inflater.setInput(data);
      ByteArrayOutputStream out = new ByteArrayOutputStream(data.length * 4);
      byte[] buffer = new byte[8192];
      while (!inflater.finished()) {
        int read = inflater.inflate(buffer);
        if (read == 0 && (inflater.needsInput() || inflater.needsDictionary())) {
          return null;
        }
        out.write(buffer, 0, read);
      }
      return out.toByteArray();
    } finally {
      inflater.end();
    }
  }

  // --- reading ---------------------------------------------------------------------------------

  /** One pass over the token stream. A malformed stream stops it, and the read answers null. */
  private static final class Reader {

    private final byte[] data;
    private int position;
    private final EvalEngine engine;
    private boolean failed = false;

    private Reader(byte[] data, int position, EvalEngine engine) {
      this.data = data;
      this.position = position;
      this.engine = engine;
    }

    private IExpr read() {
      IExpr result = readExpr();
      return failed ? null : result;
    }

    private IExpr fail() {
      failed = true;
      return F.NIL;
    }

    private IExpr readExpr() {
      if (failed || position >= data.length) {
        return fail();
      }
      byte tag = data[position++];
      switch (tag) {
        case 'i':
          return F.ZZ(readInt());
        case 'I': {
          String digits = readText();
          try {
            return digits == null ? fail() : F.ZZ(new BigInteger(digits));
          } catch (NumberFormatException nfe) {
            return fail();
          }
        }
        case 'r':
          return F.num(Double.longBitsToDouble(readLong()));
        case 'R': {
          String text = readText();
          if (text == null) {
            return fail();
          }
          IExpr number = engine.evaluate(F.ToExpression(F.stringx(text)));
          return number.isNumber() ? number : fail();
        }
        case 'S': {
          String text = readText();
          return text == null ? fail() : F.stringx(unescape(text));
        }
        case 's': {
          String name = readText();
          return name == null ? fail() : symbol(unescape(name));
        }
        case 'f': {
          int argSize = readInt();
          if (failed || argSize < 0 || argSize > data.length) {
            return fail();
          }
          IExpr head = readExpr();
          if (failed) {
            return F.NIL;
          }
          IASTAppendable ast = F.ast(head, argSize);
          for (int i = 0; i < argSize; i++) {
            IExpr argument = readExpr();
            if (failed) {
              return F.NIL;
            }
            ast.append(argument);
          }
          return isPatternConstruct(ast) ? engine.evaluate(ast) : ast;
        }
        case 'e':
          return readArray(ARRAY_REAL);
        case 'n':
          return readArray(ARRAY_INTEGER);
        case 'b':
          return readArray(ARRAY_BYTE);
        default:
          return fail();
      }
    }

    private static final int ARRAY_REAL = 0;
    private static final int ARRAY_INTEGER = 1;
    private static final int ARRAY_BYTE = 2;

    /**
     * A packed array: its dimensions, then its elements in row-major order, rebuilt as nested
     * lists. The integer form is preceded by a type field which says nothing this reader needs -
     * the element width follows from how many bytes are left.
     */
    private IExpr readArray(int kind) {
      if (kind == ARRAY_INTEGER) {
        readInt(); // the element type, which the width below tells us anyway
      }
      int rank = readInt();
      if (failed || rank < 0 || rank > 64) {
        return fail();
      }
      int[] dimensions = new int[rank];
      long count = 1;
      for (int i = 0; i < rank; i++) {
        dimensions[i] = readInt();
        if (failed || dimensions[i] < 0) {
          return fail();
        }
        count *= dimensions[i];
        if (count > Integer.MAX_VALUE) {
          return fail();
        }
      }
      int elements = (int) count;
      int width;
      switch (kind) {
        case ARRAY_REAL:
          width = 8;
          break;
        case ARRAY_BYTE:
          width = 1;
          break;
        default:
          int rest = data.length - position;
          if (elements == 0) {
            width = 1;
          } else if (rest % elements != 0) {
            return fail();
          } else {
            width = rest / elements;
            if (width != 1 && width != 2 && width != 4 && width != 8) {
              return fail();
            }
          }
          break;
      }
      IExpr[] values = new IExpr[elements];
      for (int i = 0; i < elements; i++) {
        if (position + width > data.length) {
          return fail();
        }
        if (kind == ARRAY_REAL) {
          values[i] = F.num(Double.longBitsToDouble(readLong()));
        } else if (kind == ARRAY_BYTE) {
          values[i] = F.ZZ(data[position++] & 0xFF);
        } else {
          long raw = 0;
          for (int b = 0; b < width; b++) {
            raw |= ((long) (data[position++] & 0xFF)) << (8 * b);
          }
          int shift = 64 - width * 8;
          values[i] = F.ZZ(shift == 0 ? raw : (raw << shift) >> shift);
        }
      }
      return nest(dimensions, 0, values, new int[] {0});
    }

    /** Fold a flat run of elements into lists of the given dimensions. */
    private IExpr nest(int[] dimensions, int level, IExpr[] values, int[] next) {
      if (level == dimensions.length) {
        return next[0] < values.length ? values[next[0]++] : fail();
      }
      IASTAppendable list = F.ListAlloc(dimensions[level]);
      for (int i = 0; i < dimensions[level]; i++) {
        list.append(nest(dimensions, level + 1, values, next));
      }
      return list;
    }

    /**
     * Is this one of the constructs a pattern is built from?
     *
     * <p>
     * A pattern travels as the expression it is written as - <code>Blank[]</code> for
     * <code>_</code> - and has to become a pattern again when it is read, which is what
     * evaluating <code>Blank[]</code> does.
     */
    private static boolean isPatternConstruct(IAST ast) {
      switch (ast.headID()) {
        case org.matheclipse.core.expression.ID.Blank:
        case org.matheclipse.core.expression.ID.BlankSequence:
        case org.matheclipse.core.expression.ID.BlankNullSequence:
        case org.matheclipse.core.expression.ID.Pattern:
        case org.matheclipse.core.expression.ID.Optional:
        case org.matheclipse.core.expression.ID.OptionsPattern:
        case org.matheclipse.core.expression.ID.Repeated:
        case org.matheclipse.core.expression.ID.RepeatedNull:
        case org.matheclipse.core.expression.ID.PatternTest:
        case org.matheclipse.core.expression.ID.PatternSequence:
          return true;
        default:
          return false;
      }
    }

    /** A name written with its context, or without one when it is System` or Global`. */
    private IExpr symbol(String name) {
      int backtick = name.lastIndexOf('`');
      if (backtick < 0) {
        return F.symbol(name, engine);
      }
      String context = name.substring(0, backtick + 1);
      String shortName = name.substring(backtick + 1);
      if (context.equals("System`") || context.equals("Global`")) {
        return F.symbol(shortName, engine);
      }
      return F.symbol(shortName, context, null, engine);
    }

    private int readInt() {
      if (position + 4 > data.length) {
        failed = true;
        return 0;
      }
      int value = (data[position] & 0xFF) | ((data[position + 1] & 0xFF) << 8)
          | ((data[position + 2] & 0xFF) << 16) | ((data[position + 3] & 0xFF) << 24);
      position += 4;
      return value;
    }

    private long readLong() {
      if (position + 8 > data.length) {
        failed = true;
        return 0L;
      }
      long value = 0L;
      for (int i = 0; i < 8; i++) {
        value |= ((long) (data[position + i] & 0xFF)) << (8 * i);
      }
      position += 8;
      return value;
    }

    private String readText() {
      int length = readInt();
      if (failed || length < 0 || position + length > data.length) {
        failed = true;
        return null;
      }
      String text = new String(data, position, length, StandardCharsets.UTF_8);
      position += length;
      return text;
    }
  }

  /** Put back the characters written as <code>\:XXXX</code>. */
  private static String unescape(String text) {
    int start = text.indexOf("\\:");
    if (start < 0) {
      return text;
    }
    StringBuilder buf = new StringBuilder(text.length());
    int i = 0;
    while (i < text.length()) {
      if (i + 5 < text.length() && text.charAt(i) == '\\' && text.charAt(i + 1) == ':') {
        try {
          buf.append((char) Integer.parseInt(text.substring(i + 2, i + 6), 16));
          i += 6;
          continue;
        } catch (NumberFormatException nfe) {
          // not an escape after all, take the backslash as it stands
        }
      }
      buf.append(text.charAt(i++));
    }
    return buf.toString();
  }

  private WMACompress() {}
}
