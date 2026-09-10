package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.io.link.KernelLinkServer;
import org.matheclipse.core.io.link.LinkEntry;
import org.matheclipse.core.io.link.LinkRegistry;

/**
 * A kernel driven over a link, both ends in this process.
 *
 * <p>
 * The second kernel is what <code>symjascript -wstp</code> runs; here it is a thread with a pair
 * of pipes instead of a process with a pair of standard streams, which is the same conversation
 * without the spawn.
 */
public class KernelLinkTest {

  /** One kernel serving a link, and the link to it. */
  private static final class Child implements AutoCloseable {
    final LinkEntry link;
    final Thread thread;

    Child() throws Exception {
      PipedOutputStream toChild = new PipedOutputStream();
      PipedInputStream childIn = new PipedInputStream(toChild, 1 << 16);
      PipedOutputStream fromChild = new PipedOutputStream();
      PipedInputStream childOut = new PipedInputStream(fromChild, 1 << 16);

      thread = new Thread(() -> {
        // the child reads the symbols of this JVM's table, so it has to read them the way that
        // table was built: relaxed names are stored lower-cased, and a strict lookup would find
        // none of them
        EvalEngine engine =
            new EvalEngine(org.matheclipse.parser.client.ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS);
        EvalEngine.set(engine);
        engine.init();
        KernelLinkServer.serve(childIn, fromChild, engine);
      }, "test-child-kernel");
      thread.setDaemon(true);
      thread.start();

      link = LinkRegistry.register("test", childOut, toChild, null);
    }

    /** The next packet, waited for. */
    IExpr read() {
      long deadline = System.currentTimeMillis() + 20_000;
      while (System.currentTimeMillis() < deadline) {
        IExpr packet = link.poll();
        if (packet != null) {
          return packet;
        }
        try {
          Thread.sleep(2);
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          return F.NIL;
        }
      }
      return F.NIL;
    }

    /** Read on until a packet with this head arrives. */
    IExpr readUntil(org.matheclipse.core.interfaces.ISymbol head) {
      for (int i = 0; i < 200; i++) {
        IExpr packet = read();
        if (packet.isAST(head)) {
          return packet;
        }
        if (!packet.isPresent()) {
          return F.NIL;
        }
      }
      return F.NIL;
    }

    @Override
    public void close() {
      LinkRegistry.close(link.uuid());
    }
  }

  @Test
  public void testAnExpressionIsEvaluatedAndAnswered() throws Exception {
    boolean osAccess = Config.OS_ACCESS_ENABLED;
    Config.OS_ACCESS_ENABLED = true;
    try (Child child = new Child()) {
      child.link.write(F.unaryAST1(S.EvaluatePacket, F.Plus(F.C1, F.C1)));
      assertEquals(F.unaryAST1(S.ReturnPacket, F.C2), child.readUntil(S.ReturnPacket));

      // a bare expression, which is what LinkWrite[link, Unevaluated[expr]] sends
      child.link.write(F.Set(F.$s("kernelSideValue"), F.ZZ(41)));
      child.readUntil(S.ReturnPacket);
      child.link.write(F.unaryAST1(S.EvaluatePacket, F.Plus(F.$s("kernelSideValue"), F.C1)));
      assertEquals(F.unaryAST1(S.ReturnPacket, F.ZZ(42)), child.readUntil(S.ReturnPacket));
    } finally {
      Config.OS_ACCESS_ENABLED = osAccess;
    }
  }

  @Test
  public void testTextIsReadAndEvaluated() throws Exception {
    try (Child child = new Child()) {
      child.link.write(F.unaryAST1(S.EnterTextPacket, F.stringx("2^10")));
      assertEquals(F.unaryAST1(S.ReturnTextPacket, F.stringx("1024")),
          child.readUntil(S.ReturnTextPacket));
    }
  }

  @Test
  public void testPrintingBecomesTextPackets() throws Exception {
    try (Child child = new Child()) {
      child.link.write(F.unaryAST1(S.EvaluatePacket, F.unaryAST1(S.Print, F.stringx("hello"))));
      assertEquals(F.unaryAST1(S.TextPacket, F.stringx("hello")),
          child.readUntil(S.TextPacket));
    }
  }

  @Test
  public void testAMessageBecomesAMessagePacket() throws Exception {
    try (Child child = new Child()) {
      // Part[{1, 2}, 5] complains Part::partw
      child.link.write(F.unaryAST1(S.EvaluatePacket, F.Part(F.List(F.C1, F.C2), F.C5)));
      IExpr packet = child.readUntil(S.MessagePacket);
      assertEquals(F.binaryAST2(S.MessagePacket, S.Part, F.stringx("partw")), packet);
    }
  }

  @Test
  public void testAnInterruptStopsAnEvaluationThatIsRunning() throws Exception {
    try (Child child = new Child()) {
      // something long enough to interrupt: a loop which does not finish on its own
      child.link.write(F.unaryAST1(S.EvaluatePacket,
          F.While(S.True, F.unaryAST1(S.Pause, F.num(0.01)))));
      Thread.sleep(200);
      child.link.interrupt();
      IExpr packet = child.readUntil(S.ReturnPacket);
      assertEquals(F.unaryAST1(S.ReturnPacket, S.$Aborted), packet);

      // ...and the kernel is still there afterwards
      child.link.write(F.unaryAST1(S.EvaluatePacket, F.Plus(F.C1, F.C1)));
      assertEquals(F.unaryAST1(S.ReturnPacket, F.C2), child.readUntil(S.ReturnPacket));
    }
  }

  @Test
  public void testTwoKernelsMeetUnderAName() throws Exception {
    boolean osAccess = Config.OS_ACCESS_ENABLED;
    Config.OS_ACCESS_ENABLED = true;
    try {
      String name = "symja-test-" + System.nanoTime();
      LinkRegistry.PendingLink pending = LinkRegistry.create(name);
      assertNotNull(pending);
      LinkEntry[] listening = new LinkEntry[1];
      Thread accept = new Thread(() -> listening[0] = pending.accept(10_000));
      accept.setDaemon(true);
      accept.start();

      LinkEntry connected = LinkRegistry.connect(name, 10_000);
      assertNotNull(connected);
      accept.join(10_000);
      assertNotNull(listening[0]);

      connected.write(F.stringx("over here"));
      long deadline = System.currentTimeMillis() + 10_000;
      IExpr received = null;
      while (received == null && System.currentTimeMillis() < deadline) {
        received = listening[0].poll();
        Thread.sleep(2);
      }
      assertEquals("over here", String.valueOf(received));
      assertTrue(listening[0].sawHello());

      LinkRegistry.close(connected.uuid());
      LinkRegistry.close(listening[0].uuid());
    } finally {
      Config.OS_ACCESS_ENABLED = osAccess;
    }
  }
}
