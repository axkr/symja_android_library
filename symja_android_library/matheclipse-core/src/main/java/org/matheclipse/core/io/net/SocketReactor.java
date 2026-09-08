package org.matheclipse.core.io.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * One thread that watches every open socket and turns what happens on them into events.
 *
 * <p>
 * It never touches an expression, a symbol or the evaluation engine - it reads bytes and queues an
 * {@link org.matheclipse.core.eval.tasks.EventLoop.Event}, which the evaluating thread runs when it
 * next pumps. That is the whole of the threading rule here: I/O waits on this thread, Wolfram
 * Language runs on the other one.
 */
final class SocketReactor {

  private static volatile SocketReactor instance;

  static SocketReactor instance() {
    SocketReactor local = instance;
    if (local == null) {
      synchronized (SocketReactor.class) {
        local = instance;
        if (local == null) {
          local = new SocketReactor();
          instance = local;
        }
      }
    }
    return local;
  }

  private final Selector selector;

  /** Work for the selector thread: registering and cancelling channels. */
  private final ConcurrentLinkedQueue<Runnable> pending = new ConcurrentLinkedQueue<Runnable>();

  private final ByteBuffer readBuffer = ByteBuffer.allocateDirect(64 * 1024);

  private SocketReactor() {
    Selector open;
    try {
      open = Selector.open();
    } catch (IOException ex) {
      throw new IllegalStateException("cannot open a selector", ex);
    }
    this.selector = open;
    Thread thread = new Thread(this::loop, "symja-sockets");
    thread.setDaemon(true);
    thread.start();
  }

  /** Watch this socket. */
  void register(SocketEntry entry) {
    pending.add(() -> {
      try {
        int operations = entry.role() == SocketEntry.Role.SERVER ? SelectionKey.OP_ACCEPT
            : SelectionKey.OP_READ;
        entry.channel().register(selector, operations, entry);
      } catch (ClosedChannelException ex) {
        // nothing to watch on a channel that is already gone
      }
    });
    selector.wakeup();
  }

  /** Stop watching it. */
  void unregister(SocketEntry entry) {
    pending.add(() -> {
      SelectionKey key = entry.channel().keyFor(selector);
      if (key != null) {
        key.cancel();
      }
    });
    selector.wakeup();
  }

  private void loop() {
    while (true) {
      try {
        Runnable work;
        while ((work = pending.poll()) != null) {
          work.run();
        }
        selector.select(200);
        java.util.Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
          SelectionKey key = keys.next();
          keys.remove();
          if (!key.isValid()) {
            continue;
          }
          SocketEntry entry = (SocketEntry) key.attachment();
          if (entry == null || entry.isClosed()) {
            key.cancel();
            continue;
          }
          if (key.isAcceptable()) {
            accept(entry);
          } else if (key.isReadable()) {
            read(entry, key);
          }
        }
      } catch (java.nio.channels.ClosedSelectorException ex) {
        return;
      } catch (IOException | RuntimeException ex) {
        // one bad socket must not stop the others being served
      }
    }
  }

  private void accept(SocketEntry server) throws IOException {
    ServerSocketChannel channel = server.serverChannel();
    if (channel == null) {
      return;
    }
    SocketChannel accepted = channel.accept();
    while (accepted != null) {
      SocketEntry client = SocketRegistry.accepted(accepted, server);
      client.setListening(server.isListening());
      register(client);
      SocketEvents.accepted(server, client);
      accepted = channel.accept();
    }
  }

  private void read(SocketEntry entry, SelectionKey key) {
    SocketChannel channel = entry.socketChannel();
    if (channel == null) {
      return;
    }
    try {
      synchronized (readBuffer) {
        readBuffer.clear();
        int read = channel.read(readBuffer);
        if (read < 0) {
          entry.markEndOfStream();
          key.cancel();
          SocketEvents.closed(entry);
          return;
        }
        if (read == 0) {
          return;
        }
        readBuffer.flip();
        byte[] bytes = new byte[read];
        readBuffer.get(bytes);
        // with a handler attached the bytes go to it; without one they wait to be read
        SocketEvents.received(entry, bytes);
      }
    } catch (IOException ex) {
      entry.markEndOfStream();
      key.cancel();
      SocketEvents.error(entry, ex.getMessage());
    }
  }
}
