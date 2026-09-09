package org.matheclipse.core.io.link;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.matheclipse.core.builtin.LinkFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.ExitException;
import org.matheclipse.core.eval.tasks.EventLoop;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The kernel on the other end of a link: what <code>symjascript -wstp</code> runs.
 *
 * <p>
 * It reads expressions from the link, evaluates them, and writes back what they came to. A notebook
 * front end drives a kernel exactly this way - the master kernel serves the browser and hands every
 * cell to this one - so the packets are the ones a front end sends:
 *
 * <ul>
 * <li><code>EvaluatePacket[expr]</code> and a bare expression are evaluated, and the result is
 * answered as <code>ReturnPacket[result]</code>;
 * <li><code>EnterTextPacket["text"]</code> is read and evaluated, and answered as
 * <code>ReturnTextPacket</code>;
 * <li><code>EnterExpressionPacket[expr]</code> is answered as
 * <code>ReturnExpressionPacket</code>.
 * </ul>
 *
 * <p>
 * What the evaluation prints becomes <code>TextPacket</code>s and what it complains about becomes
 * <code>MessagePacket</code>s, because the front end shows those beside the cell rather than in a
 * console nobody is looking at. Between packets the {@link EventLoop} runs, so this kernel's own
 * sockets and scheduled tasks keep working while it waits.
 */
public final class KernelLinkServer {

  /** How long a wait for the next packet lasts before the event loop gets a turn. */
  private static final long POLL_MILLIS = 5L;

  /**
   * Serve one link until the other end closes it or the evaluation asks to leave.
   *
   * @param in the frames coming from the kernel which started this one
   * @param out where the frames going back are written
   * @return the exit code, which is 0 unless an evaluation asked for another
   */
  public static int serve(InputStream in, OutputStream out, EvalEngine engine) {
    LinkEntry parent = LinkRegistry.register("$ParentLink", in, out, null);
    LinkFunctions.setParentLink(parent);
    parent.setInterruptTarget(engine);

    PrintStream printed = new PrintStream(new PacketOutputStream(parent), true,
        StandardCharsets.UTF_8);
    engine.setOutPrintStream(printed);
    engine.setErrorPrintStream(printed);
    engine.setMessageListener((symbol, tag) -> write(parent,
        F.binaryAST2(S.MessagePacket, symbol, F.stringx(tag))));

    try {
      while (!parent.isClosed()) {
        IExpr packet = parent.poll();
        if (packet == null) {
          // nothing from the front end: give this kernel's own work a turn
          EventLoop.INSTANCE.pump(engine);
          EventLoop.INSTANCE.awaitEvent(POLL_MILLIS);
          continue;
        }
        try {
          IExpr answer = answer(packet, engine);
          if (answer.isPresent()) {
            write(parent, answer);
          }
        } catch (ExitException exit) {
          return exit.getExitCode();
        } catch (AbortException abort) {
          engine.clearStopRequest();
          write(parent, F.unaryAST1(S.ReturnPacket, S.$Aborted));
        } catch (RuntimeException rex) {
          write(parent, F.unaryAST1(S.ReturnPacket, S.$Failed));
        }
      }
    } finally {
      LinkFunctions.setParentLink(null);
      LinkRegistry.close(parent.uuid());
    }
    return 0;
  }

  /** What one packet is answered with, or {@link F#NIL} when it is answered with nothing. */
  private static IExpr answer(IExpr packet, EvalEngine engine) {
    if (packet.isAST(S.EvaluatePacket, 2)) {
      return F.unaryAST1(S.ReturnPacket, engine.evaluate(packet.first()));
    }
    if (packet.isAST(S.EnterTextPacket, 2) && packet.first().isString()) {
      IExpr parsed = engine.parse(packet.first().toString());
      return F.unaryAST1(S.ReturnTextPacket, F.stringx(engine.evaluate(parsed).toString()));
    }
    if (packet.isAST(S.EnterExpressionPacket, 2)) {
      return F.unaryAST1(S.ReturnExpressionPacket, engine.evaluate(packet.first()));
    }
    if (packet.isAST(S.ReturnPacket, 2) || packet.isAST(S.TextPacket, 2)) {
      // an answer of ours coming back: nothing to do with it
      return F.NIL;
    }
    // a bare expression, which is what LinkWrite[link, Unevaluated[expr]] sends
    return F.unaryAST1(S.ReturnPacket, engine.evaluate(packet));
  }

  private static void write(LinkEntry link, IExpr expr) {
    try {
      link.write(expr);
    } catch (IOException ioe) {
      link.close();
    }
  }

  /**
   * Everything the kernel prints, sent on as <code>TextPacket</code>s a line at a time.
   *
   * <p>
   * A line at a time rather than a byte at a time because the front end shows each packet as one
   * line of output, and because a packet per character would be a packet per character.
   */
  private static final class PacketOutputStream extends OutputStream {

    private final LinkEntry link;
    private final StringBuilder line = new StringBuilder();

    PacketOutputStream(LinkEntry link) {
      this.link = link;
    }

    @Override
    public synchronized void write(int b) {
      if (b == '\n') {
        flushLine();
        return;
      }
      if (b != '\r') {
        line.append((char) b);
      }
    }

    @Override
    public synchronized void flush() {
      flushLine();
    }

    private void flushLine() {
      if (line.length() == 0) {
        return;
      }
      String text = line.toString();
      line.setLength(0);
      KernelLinkServer.write(link, F.unaryAST1(S.TextPacket, F.stringx(text)));
    }
  }

  /** The packet heads a front end sends and expects, so that a caller can name them. */
  public static IAST returnPacket(IExpr value) {
    return F.unaryAST1(S.ReturnPacket, value);
  }

  private KernelLinkServer() {}
}
