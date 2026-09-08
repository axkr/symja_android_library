package org.matheclipse.core.builtin;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.tasks.EventLoop;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.expression.data.SocketObjectExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.io.net.SocketEntry;
import org.matheclipse.core.io.net.SocketEvents;
import org.matheclipse.core.io.net.SocketRegistry;

/**
 * TCP sockets.
 *
 * <p>
 * A server is <code>SocketOpen</code>, a connection is <code>SocketConnect</code>, and what arrives
 * either goes to the handlers of a <code>SocketListen</code> or waits for <code>SocketReadMessage
 * </code> to take it. Reading blocks the way the Wolfram Language blocks: the evaluation waits, and
 * while it waits the {@link EventLoop} runs whatever else has come due, so a program written as
 * <code>While[True, Pause[0.01]]</code> still serves its sockets.
 */
public class SocketFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.SocketOpen.setEvaluator(new SocketOpen());
        S.SocketConnect.setEvaluator(new SocketConnect());
        S.SocketListen.setEvaluator(new SocketListen());
        S.SocketReadMessage.setEvaluator(new SocketReadMessage());
        S.SocketReadyQ.setEvaluator(new SocketReadyQ());
        S.SocketWaitNext.setEvaluator(new SocketWaitNext());
        S.SocketWaitAll.setEvaluator(new SocketWaitAll());
        S.Sockets.setEvaluator(new Sockets());
        S.DeleteObject.setEvaluator(new DeleteObject());
        S.WriteLine.setEvaluator(new WriteLine());
        S.BinaryReadList.setEvaluator(new BinaryReadList());
      }
    }
  }

  /** How long one wait inside a blocking socket read lasts before the loop looks again. */
  private static final long POLL_MILLIS = 20L;

  /** A host and a port, however they were written. */
  private static final class Address {
    final String host;
    final int port;

    Address(String host, int port) {
      this.host = host;
      this.port = port;
    }
  }

  /**
   * Read <code>8080</code>, <code>"8080"</code>, <code>"127.0.0.1:8080"</code>,
   * <code>{"127.0.0.1", 8080}</code> and the two-argument <code>host, port</code> form.
   */
  private static Address address(IAST ast, String defaultHost) {
    if (ast.argSize() == 2) {
      int port = ast.arg2().toIntDefault();
      if (port == Integer.MIN_VALUE && ast.arg2().isString()) {
        port = toPort(ast.arg2().toString());
      }
      if (port < 0 || !ast.arg1().isString()) {
        return null;
      }
      return new Address(ast.arg1().toString(), port);
    }
    if (ast.argSize() != 1) {
      return null;
    }
    IExpr arg1 = ast.arg1();
    if (arg1.isList() && ((IAST) arg1).argSize() == 2) {
      IAST list = (IAST) arg1;
      int port = list.arg2().toIntDefault();
      if (port < 0 || !list.arg1().isString()) {
        return null;
      }
      return new Address(list.arg1().toString(), port);
    }
    int port = arg1.toIntDefault();
    if (port >= 0) {
      return new Address(defaultHost, port);
    }
    if (arg1.isString()) {
      String text = arg1.toString();
      int colon = text.lastIndexOf(':');
      if (colon > 0) {
        int p = toPort(text.substring(colon + 1));
        return p < 0 ? null : new Address(text.substring(0, colon), p);
      }
      int p = toPort(text);
      return p < 0 ? null : new Address(defaultHost, p);
    }
    return null;
  }

  private static int toPort(String text) {
    try {
      int port = Integer.parseInt(text.trim());
      return port < 0 || port > 65535 ? -1 : port;
    } catch (NumberFormatException nfe) {
      return -1;
    }
  }

  /** The socket an expression names, or <code>null</code>. */
  public static SocketEntry entryOf(IExpr expr) {
    if (expr instanceof SocketObjectExpr) {
      return ((SocketObjectExpr) expr).entry();
    }
    if (expr.isAST(S.SocketObject, 2) && expr.first().isString()) {
      return SocketRegistry.get(expr.first().toString());
    }
    return null;
  }

  /**
   * <code>SocketOpen[port]</code>, <code>SocketOpen["host:port"]</code>,
   * <code>SocketOpen["host", port]</code>: listen for connections.
   */
  private static final class SocketOpen extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return F.NIL;
      }
      Address address = address(ast, "0.0.0.0");
      if (address == null) {
        return S.$Failed;
      }
      try {
        return SocketObjectExpr.newInstance(SocketRegistry.open(address.host, address.port));
      } catch (IOException | RuntimeException ex) {
        Errors.printMessage(S.SocketOpen, ex);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>SocketConnect[…]</code>: the near end of a connection to somewhere. */
  private static final class SocketConnect extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return F.NIL;
      }
      Address address = address(ast, "127.0.0.1");
      if (address == null) {
        return S.$Failed;
      }
      try {
        return SocketObjectExpr.newInstance(SocketRegistry.connect(address.host, address.port));
      } catch (IOException | RuntimeException ex) {
        Errors.printMessage(S.SocketConnect, ex);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>SocketListen[socket, f]</code>, <code>SocketListen[socket, HandlerFunctions -> &lt;|…|&gt;]
   * </code>: call these when something happens on the socket.
   *
   * @return <code>SocketListener[n]</code>
   */
  private static final class SocketListen extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return F.NIL;
      }
      SocketEntry entry = entryOf(ast.arg1());
      if (entry == null) {
        return S.$Failed;
      }
      IExpr handlers = ast.arg2();
      if (handlers.isRuleAST() && handlers.first().isSymbol()
          && handlers.first() == S.HandlerFunctions) {
        handlers = handlers.second();
      }
      if (!(handlers instanceof IAssociation) && !handlers.isPresent()) {
        return S.$Failed;
      }
      int id = SocketEvents.addListener(entry, handlers);
      return F.unaryAST1(S.SocketListener, F.ZZ(id));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * <code>SocketReadMessage[socket]</code>, <code>SocketReadMessage[socket, n]</code>: wait for
   * bytes and answer them as a <code>ByteArray</code>, or <code>EndOfFile</code> when the other end
   * has gone.
   */
  private static final class SocketReadMessage extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return F.NIL;
      }
      SocketEntry entry = entryOf(ast.arg1());
      if (entry == null) {
        return S.$Failed;
      }
      int limit = ast.isAST2() ? ast.arg2().toIntDefault(-1) : -1;
      while (true) {
        if (entry.available() > 0) {
          return ByteArrayExpr.newInstance(entry.take(limit));
        }
        if (entry.isEndOfStream() || entry.isClosed()) {
          return S.EndOfFile;
        }
        EventLoop.INSTANCE.awaitEvent(POLL_MILLIS);
        EventLoop.INSTANCE.pump(engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>SocketReadyQ[socket]</code>: are there bytes waiting? */
  private static final class SocketReadyQ extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return F.NIL;
      }
      EventLoop.INSTANCE.pump(engine);
      SocketEntry entry = entryOf(ast.arg1());
      if (entry == null) {
        return S.False;
      }
      return F.booleSymbol(entry.available() > 0);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /** <code>SocketWaitNext[{socket, …}]</code>: the first of them with something to read. */
  private static final class SocketWaitNext extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return F.NIL;
      }
      IAST sockets = ast.arg1().isList() ? (IAST) ast.arg1() : F.list(ast.arg1());
      while (true) {
        boolean anyOpen = false;
        for (int i = 1; i < sockets.size(); i++) {
          SocketEntry entry = entryOf(sockets.get(i));
          if (entry == null) {
            continue;
          }
          if (entry.available() > 0) {
            return sockets.get(i);
          }
          if (!entry.isClosed() && !entry.isEndOfStream()) {
            anyOpen = true;
          }
        }
        if (!anyOpen) {
          return S.$Failed;
        }
        EventLoop.INSTANCE.awaitEvent(POLL_MILLIS);
        EventLoop.INSTANCE.pump(engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>SocketWaitAll[{socket, …}]</code>: wait until every one of them has something. */
  private static final class SocketWaitAll extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return F.NIL;
      }
      IAST sockets = ast.arg1().isList() ? (IAST) ast.arg1() : F.list(ast.arg1());
      while (true) {
        boolean allReady = true;
        boolean anyOpen = false;
        for (int i = 1; i < sockets.size(); i++) {
          SocketEntry entry = entryOf(sockets.get(i));
          if (entry == null) {
            continue;
          }
          if (entry.available() == 0) {
            allReady = false;
            if (!entry.isClosed() && !entry.isEndOfStream()) {
              anyOpen = true;
            }
          }
        }
        if (allReady) {
          return sockets;
        }
        if (!anyOpen) {
          return S.$Failed;
        }
        EventLoop.INSTANCE.awaitEvent(POLL_MILLIS);
        EventLoop.INSTANCE.pump(engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>Sockets[]</code>: everything still open. */
  private static final class Sockets extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isOSAccessEnabled(engine)) {
        return F.NIL;
      }
      List<SocketEntry> open = SocketRegistry.sockets();
      IASTAppendable result = F.ListAlloc(open.size());
      for (SocketEntry entry : open) {
        result.append(SocketObjectExpr.newInstance(entry));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /** <code>DeleteObject[obj]</code>: stop a listener, close a socket, remove a task. */
  private static final class DeleteObject extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        return F.mapList((IAST) arg1, x -> evaluate(F.unaryAST1(S.DeleteObject, x), engine));
      }
      if (arg1.isAST(S.SocketListener, 2)) {
        int id = arg1.first().toIntDefault();
        return F.booleSymbol(id != Integer.MIN_VALUE && SocketEvents.removeListener(id));
      }
      SocketEntry entry = entryOf(arg1);
      if (entry != null) {
        return F.booleSymbol(SocketRegistry.close(entry.uuid()));
      }
      if (arg1.isAST(S.TaskObject, 2)) {
        return F.booleSymbol(TaskFunctions.removeTask(arg1.first()));
      }
      return S.$Failed;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>WriteLine[target, string]</code>: the string and a newline. */
  private static final class WriteLine extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!ast.arg2().isString()) {
        return F.NIL;
      }
      SocketEntry entry = entryOf(ast.arg1());
      if (entry != null) {
        return write(entry, (ast.arg2().toString() + "\n").getBytes(StandardCharsets.UTF_8),
            S.WriteLine, engine);
      }
      return engine
          .evaluate(F.binaryAST2(S.WriteString, ast.arg1(), F.stringx(ast.arg2() + "\n")));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /** <code>BinaryReadList[socket]</code>: everything waiting, as a list of bytes. */
  private static final class BinaryReadList extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      SocketEntry entry = entryOf(ast.arg1());
      if (entry == null) {
        return F.NIL;
      }
      byte[] bytes = entry.take(-1);
      return F.mapRange(0, bytes.length, i -> F.ZZ(bytes[i] & 0xFF));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   * Write bytes to a socket.
   *
   * <p>
   * The channel is non-blocking because the selector thread reads from it, so a large write has to
   * be repeated until it has all gone out.
   */
  public static IExpr write(SocketEntry entry, byte[] bytes, org.matheclipse.core.interfaces.ISymbol
      caller, EvalEngine engine) {
    SocketChannel channel = entry.socketChannel();
    if (channel == null || entry.isClosed()) {
      return S.$Failed;
    }
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    try {
      while (buffer.hasRemaining()) {
        int written = channel.write(buffer);
        if (written == 0) {
          // the other end is not taking it yet
          EventLoop.INSTANCE.awaitEvent(POLL_MILLIS);
        }
      }
    } catch (IOException ex) {
      Errors.printMessage(caller, ex);
      return S.$Failed;
    }
    return S.Null;
  }

  /** Close a socket, for the <code>Close</code> built-in. */
  public static IExpr close(SocketEntry entry) {
    return SocketRegistry.close(entry.uuid()) ? SocketObjectExpr.newInstance(entry) : S.$Failed;
  }

  public static void initialize() {
    Initializer.init();
  }

  private SocketFunctions() {}
}
