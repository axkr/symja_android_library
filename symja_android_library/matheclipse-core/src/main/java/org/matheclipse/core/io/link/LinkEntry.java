package org.matheclipse.core.io.link;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.tasks.EventLoop;
import org.matheclipse.core.interfaces.IExpr;

/**
 * One open link, and the thread which reads it.
 *
 * <p>
 * Reading happens on a thread of its own so that the evaluating thread never blocks on the other
 * kernel: what arrives is put in a queue and the event loop is woken, which is what lets
 * <code>While[True, Pause[0.01]]</code> notice it. An interrupt frame is the exception - it is
 * acted on by the reader thread itself, since the point of it is to stop an evaluation which is
 * running.
 */
public final class LinkEntry {

  private final String uuid;
  private final String name;
  private final DataInputStream in;
  private final DataOutputStream out;
  private final Process process;
  /**
   * The frames which have arrived, still as bytes.
   *
   * <p>
   * Reading them into expressions is left to whoever takes them out, because that has to happen on
   * the thread which evaluates: a symbol is resolved against the contexts of an engine, and the
   * reader thread has none of its own.
   */
  private final BlockingQueue<LinkCodec.Frame> inbox =
      new LinkedBlockingQueue<LinkCodec.Frame>();
  private final AtomicBoolean closed = new AtomicBoolean(false);
  private final AtomicBoolean sawHello = new AtomicBoolean(false);
  private volatile Thread reader;
  private volatile EvalEngine interruptTarget;

  LinkEntry(String uuid, String name, DataInputStream in, DataOutputStream out, Process process) {
    this.uuid = uuid;
    this.name = name;
    this.in = in;
    this.out = out;
    this.process = process;
  }

  public String uuid() {
    return uuid;
  }

  /** The name a link was created or connected under, or the command that launched it. */
  public String name() {
    return name;
  }

  /** The process on the other end, when this link launched one. */
  public Process process() {
    return process;
  }

  public boolean isClosed() {
    return closed.get();
  }

  /** Whether the other end has said hello, which is what {@link #activate} waits for. */
  public boolean sawHello() {
    return sawHello.get();
  }

  /** The engine an interrupt from the other end aborts. */
  public void setInterruptTarget(EvalEngine engine) {
    this.interruptTarget = engine;
  }

  /** Start reading, and say hello so that the other end's <code>LinkActivate</code> returns. */
  void start() {
    try {
      LinkCodec.writeSignal(out, LinkCodec.FRAME_HELLO);
    } catch (IOException ioe) {
      close();
      return;
    }
    Thread thread = new Thread(this::read, "symja-link-" + uuid);
    thread.setDaemon(true);
    reader = thread;
    thread.start();
  }

  private void read() {
    while (!closed.get()) {
      LinkCodec.Frame frame;
      try {
        frame = LinkCodec.readFrame(in);
      } catch (IOException ioe) {
        break;
      }
      if (frame == null) {
        break;
      }
      switch (frame.type()) {
        case LinkCodec.FRAME_HELLO:
          sawHello.set(true);
          break;
        case LinkCodec.FRAME_CLOSE:
          closed.set(true);
          break;
        case LinkCodec.FRAME_INTERRUPT:
          EvalEngine target = interruptTarget;
          if (target != null) {
            target.stopRequest();
          }
          break;
        default:
          inbox.add(frame);
          break;
      }
      // whatever it was, an evaluation which is waiting should look again
      EventLoop.INSTANCE.post(e -> {
      });
    }
    closed.set(true);
    // wake anything waiting for a packet which is never coming
    EventLoop.INSTANCE.post(e -> {
    });
  }

  /** Is there a packet to read? */
  public boolean ready() {
    return !inbox.isEmpty();
  }

  /**
   * The next packet, or <code>null</code> if none has arrived yet. Read here, on the caller's
   * thread, so that the symbols in it are the symbols of the caller's engine.
   */
  public IExpr poll() {
    LinkCodec.Frame frame = inbox.poll();
    if (frame == null) {
      return null;
    }
    IExpr expr = frame.expression(EvalEngine.get());
    return expr.isPresent() ? expr : null;
  }

  public void write(IExpr expr) throws IOException {
    LinkCodec.writeExpression(out, expr);
  }

  public void interrupt() throws IOException {
    LinkCodec.writeSignal(out, LinkCodec.FRAME_INTERRUPT);
  }

  /** Say goodbye, stop reading, and let the process on the other end go. */
  public void close() {
    if (!closed.compareAndSet(false, true)) {
      return;
    }
    try {
      LinkCodec.writeSignal(out, LinkCodec.FRAME_CLOSE);
    } catch (IOException ioe) {
      // the other end is already gone, which is what we were about to tell it
    }
    try {
      out.close();
    } catch (IOException ioe) {
      //
    }
    try {
      in.close();
    } catch (IOException ioe) {
      //
    }
    Thread thread = reader;
    if (thread != null) {
      thread.interrupt();
    }
  }
}
