package org.matheclipse.core.io.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.tasks.EventLoop;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.expression.data.SocketObjectExpr;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;

/**
 * What a socket does, said in expressions: the handlers <code>SocketListen</code> attached, and the
 * event association each one is called with.
 *
 * <p>
 * The selector thread calls the methods here; they only build a description of what happened and
 * hand it to the {@link EventLoop}, which runs the handler later on the evaluating thread. So a
 * handler is ordinary Wolfram Language code evaluated in the usual single-threaded way, no matter
 * which thread the bytes arrived on.
 */
public final class SocketEvents {

  /** One <code>SocketListen</code>: a socket, and what to call when something happens on it. */
  private static final class Listener {
    final int id;
    final String socketUuid;
    /** An association of event name to function, or a single function for received data. */
    final IExpr handlers;

    Listener(int id, String socketUuid, IExpr handlers) {
      this.id = id;
      this.socketUuid = socketUuid;
      this.handlers = handlers;
    }

    /** The function for this event, or {@link F#NIL}. */
    IExpr handler(String event) {
      if (handlers instanceof IAssociation) {
        IExpr function = ((IAssociation) handlers).getValue(F.stringx(event));
        return function == null ? F.NIL : function;
      }
      // a bare function listens for data
      return "Received".equals(event) ? handlers : F.NIL;
    }
  }

  private static final Map<Integer, Listener> LISTENERS = new ConcurrentHashMap<Integer, Listener>();

  private static final AtomicInteger NEXT_ID = new AtomicInteger();

  private SocketEvents() {}

  /**
   * Attach handlers to a socket.
   *
   * @return the listener's number, which <code>SocketListener[n]</code> carries
   */
  public static int addListener(SocketEntry entry, IExpr handlers) {
    int id = NEXT_ID.incrementAndGet();
    LISTENERS.put(id, new Listener(id, entry.uuid(), handlers));
    entry.setListening(true);
    for (String child : entry.acceptedUuids()) {
      SocketEntry accepted = SocketRegistry.get(child);
      if (accepted != null) {
        accepted.setListening(true);
      }
    }
    return id;
  }

  /** Detach one listener. */
  public static boolean removeListener(int id) {
    Listener listener = LISTENERS.remove(id);
    if (listener == null) {
      return false;
    }
    SocketEntry entry = SocketRegistry.get(listener.socketUuid);
    if (entry != null && listenersOf(entry).isEmpty()) {
      entry.setListening(false);
      for (String child : entry.acceptedUuids()) {
        SocketEntry accepted = SocketRegistry.get(child);
        if (accepted != null) {
          accepted.setListening(false);
        }
      }
    }
    return true;
  }

  /** Is anything listening at all? */
  public static boolean hasListeners() {
    return !LISTENERS.isEmpty();
  }

  public static void clear() {
    LISTENERS.clear();
  }

  /**
   * The listeners an event on this socket reaches: the ones on the socket itself, and - when it is
   * a connection a server accepted - the ones on that server.
   */
  private static List<Listener> listenersOf(SocketEntry entry) {
    List<Listener> found = new ArrayList<Listener>(2);
    for (Listener listener : LISTENERS.values()) {
      if (listener.socketUuid.equals(entry.uuid())
          || listener.socketUuid.equals(entry.parentUuid())) {
        found.add(listener);
      }
    }
    return found;
  }

  static void accepted(SocketEntry server, SocketEntry client) {
    post(client, "Accepted", null);
  }

  static void received(SocketEntry entry, byte[] bytes) {
    post(entry, "Received", bytes);
  }

  static void closed(SocketEntry entry) {
    post(entry, "Closed", null);
  }

  static void error(SocketEntry entry, String message) {
    post(entry, "Error", null);
  }

  /** Wake an evaluation that is waiting for bytes nobody is handling. */
  static void dataAvailable() {
    EventLoop.INSTANCE.post(engine -> {
      // nothing to run: the point is only that a waiting evaluation looks again
    });
  }

  private static void post(SocketEntry entry, String event, byte[] bytes) {
    List<Listener> listeners = listenersOf(entry);
    if (listeners.isEmpty()) {
      if (bytes != null) {
        // no handler wants it, so it waits to be read
        entry.append(bytes, bytes.length);
      }
      dataAvailable();
      return;
    }
    final byte[] data = bytes;
    EventLoop.INSTANCE.post(engine -> {
      for (Listener listener : listeners) {
        IExpr function = listener.handler(event);
        if (function.isPresent() && !function.isNIL()) {
          engine.evaluate(F.unaryAST1(function, association(entry, listener, data, engine)));
        }
      }
    });
  }

  /** The association a handler is called with. */
  private static IExpr association(SocketEntry entry, Listener listener, byte[] data,
      EvalEngine engine) {
    IASTAppendable rules = F.ListAlloc(6);
    rules.append(F.Rule(F.stringx("TimeStamp"), engine.evaluate(S.Now)));
    rules.append(F.Rule(F.stringx("SourceSocket"), SocketObjectExpr.newInstance(entry)));
    SocketEntry listened = SocketRegistry.get(listener.socketUuid);
    rules.append(F.Rule(F.stringx("Socket"),
        listened == null ? SocketObjectExpr.newInstance(entry) : SocketObjectExpr.newInstance(listened)));
    if (data != null) {
      ByteArrayExpr byteArray = ByteArrayExpr.newInstance(data);
      rules.append(F.Rule(F.stringx("DataByteArray"), byteArray));
      rules.append(F.Rule(F.stringx("DataBytes"), F.mapRange(0, data.length,
          i -> F.ZZ(data[i] & 0xFF))));
      rules.append(F.Rule(F.stringx("MultipartComplete"), S.True));
    }
    return F.assoc(rules);
  }
}
