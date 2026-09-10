package org.matheclipse.core.io.link;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.WL;
import org.matheclipse.core.interfaces.IExpr;

/**
 * What travels between two kernels on a link.
 *
 * <p>
 * A frame is one byte saying what it is, four bytes saying how long it is, and that many bytes:
 *
 * <ul>
 * <li><b>expression</b> - the expression in WXF, which is what nearly everything is;
 * <li><b>text</b> - the expression written out, for the rare one WXF has no encoding for;
 * <li><b>interrupt</b> - an abort, which the reader acts on rather than queues;
 * <li><b>hello</b> - the first frame each end sends, which is how <code>LinkActivate</code> knows
 * the other end is there;
 * <li><b>close</b> - said once, before the link goes.
 * </ul>
 *
 * <p>
 * Both ends of the link are Symja, so the format only has to agree with itself; it is not WSTP and
 * does not try to be.
 */
public final class LinkCodec {

  public static final byte FRAME_EXPRESSION = 1;
  public static final byte FRAME_TEXT = 2;
  public static final byte FRAME_INTERRUPT = 3;
  public static final byte FRAME_HELLO = 4;
  public static final byte FRAME_CLOSE = 5;

  /** One frame, as it was read. */
  public static final class Frame {
    private final byte type;
    private final byte[] payload;

    Frame(byte type, byte[] payload) {
      this.type = type;
      this.payload = payload;
    }

    public byte type() {
      return type;
    }

    public byte[] payload() {
      return payload;
    }

    /**
     * The expression this frame carries, or {@link F#NIL} when it carries none (an interrupt, a
     * hello, a close) or when what it carries cannot be read back.
     */
    public IExpr expression(EvalEngine engine) {
      try {
        if (type == FRAME_EXPRESSION) {
          IExpr result = WL.deserialize(payload);
          return result == null ? F.NIL : result;
        }
        if (type == FRAME_TEXT) {
          String text = new String(payload, StandardCharsets.UTF_8);
          return engine.parse(text);
        }
      } catch (RuntimeException rex) {
        return F.NIL;
      }
      return F.NIL;
    }
  }

  /** Write one expression. */
  public static synchronized void writeExpression(DataOutputStream out, IExpr expr)
      throws IOException {
    byte[] payload;
    byte type = FRAME_EXPRESSION;
    try {
      payload = WL.serialize(expr);
      if (payload == null) {
        throw new IOException("nothing to write");
      }
    } catch (RuntimeException rex) {
      // an atom WXF has no encoding for: hand it over as text, which the other end reads back
      type = FRAME_TEXT;
      payload = expr.toString().getBytes(StandardCharsets.UTF_8);
    }
    writeFrame(out, type, payload);
  }

  /** Write a frame which carries nothing but its kind. */
  public static synchronized void writeSignal(DataOutputStream out, byte type) throws IOException {
    writeFrame(out, type, new byte[0]);
  }

  private static void writeFrame(DataOutputStream out, byte type, byte[] payload)
      throws IOException {
    synchronized (out) {
      out.writeByte(type);
      out.writeInt(payload.length);
      out.write(payload);
      out.flush();
    }
  }

  /**
   * Read one frame, or <code>null</code> once the other end has gone.
   */
  public static Frame readFrame(DataInputStream in) throws IOException {
    int type;
    try {
      type = in.readByte();
    } catch (EOFException eof) {
      return null;
    }
    int length = in.readInt();
    if (length < 0 || length > MAX_FRAME) {
      throw new IOException("frame of " + length + " bytes");
    }
    byte[] payload = new byte[length];
    in.readFully(payload);
    return new Frame((byte) type, payload);
  }

  /** A frame larger than this is a misread rather than an expression. */
  private static final int MAX_FRAME = 1 << 28;

  public static DataOutputStream output(OutputStream stream) {
    return new DataOutputStream(new java.io.BufferedOutputStream(stream, 8192));
  }

  public static DataInputStream input(InputStream stream) {
    return new DataInputStream(new java.io.BufferedInputStream(stream, 8192));
  }

  private LinkCodec() {}
}
