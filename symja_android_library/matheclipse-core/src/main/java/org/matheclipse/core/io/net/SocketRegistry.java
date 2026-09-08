package org.matheclipse.core.io.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The sockets a session has open.
 *
 * <p>
 * A socket is named by a string, the way the Wolfram Language names one, so that an expression can
 * carry it around and hand it back. A server's name begins with <code>TCPSERVER-</code>, which is
 * what a caller looks at to tell one from a connection.
 */
public final class SocketRegistry {

  private static final Map<String, SocketEntry> SOCKETS =
      new ConcurrentHashMap<String, SocketEntry>();

  private static final AtomicLong COUNTER = new AtomicLong();

  private SocketRegistry() {}

  private static String newUuid(boolean server) {
    String id = java.util.UUID.randomUUID().toString();
    return server ? "TCPSERVER-" + id : id;
  }

  /** Bind a socket that listens on <code>host:port</code>. */
  public static SocketEntry open(String host, int port) throws IOException {
    ServerSocketChannel channel = ServerSocketChannel.open();
    channel.configureBlocking(false);
    channel.socket().setReuseAddress(true);
    channel.bind(new InetSocketAddress(host, port));
    int boundPort = channel.socket().getLocalPort();
    SocketEntry entry = new SocketEntry(newUuid(true), SocketEntry.Role.SERVER, channel, host,
        boundPort, null);
    SOCKETS.put(entry.uuid(), entry);
    SocketReactor.instance().register(entry);
    COUNTER.incrementAndGet();
    return entry;
  }

  /** Connect to <code>host:port</code>. */
  public static SocketEntry connect(String host, int port) throws IOException {
    SocketChannel channel = SocketChannel.open();
    channel.configureBlocking(true);
    channel.connect(new InetSocketAddress(host, port));
    channel.finishConnect();
    channel.configureBlocking(false);
    channel.socket().setTcpNoDelay(true);
    SocketEntry entry =
        new SocketEntry(newUuid(false), SocketEntry.Role.CLIENT, channel, host, port, null);
    SOCKETS.put(entry.uuid(), entry);
    SocketReactor.instance().register(entry);
    return entry;
  }

  /** Remember a connection a server accepted. */
  static SocketEntry accepted(SocketChannel channel, SocketEntry server) throws IOException {
    channel.configureBlocking(false);
    channel.socket().setTcpNoDelay(true);
    String host = channel.socket().getInetAddress() == null ? server.host()
        : channel.socket().getInetAddress().getHostAddress();
    SocketEntry entry = new SocketEntry(newUuid(false), SocketEntry.Role.ACCEPTED, channel, host,
        channel.socket().getPort(), server.uuid());
    SOCKETS.put(entry.uuid(), entry);
    server.acceptedUuids().add(entry.uuid());
    return entry;
  }

  public static SocketEntry get(String uuid) {
    return uuid == null ? null : SOCKETS.get(uuid);
  }

  /** Every socket still open. */
  public static List<SocketEntry> sockets() {
    List<SocketEntry> open = new ArrayList<SocketEntry>();
    for (SocketEntry entry : SOCKETS.values()) {
      if (!entry.isClosed()) {
        open.add(entry);
      }
    }
    return open;
  }

  /** Close one socket, and the connections a server accepted through it. */
  public static boolean close(String uuid) {
    SocketEntry entry = SOCKETS.get(uuid);
    if (entry == null || entry.isClosed()) {
      return false;
    }
    if (entry.role() == SocketEntry.Role.SERVER) {
      for (String child : new ArrayList<String>(entry.acceptedUuids())) {
        close(child);
      }
    }
    entry.markClosed();
    SocketReactor.instance().unregister(entry);
    try {
      entry.channel().close();
    } catch (IOException ex) {
      // closing something already broken is not a failure
    }
    SOCKETS.remove(uuid);
    return true;
  }

  /** Close everything. For tests, and for a session that ends. */
  public static void closeAll() {
    for (String uuid : new ArrayList<String>(SOCKETS.keySet())) {
      close(uuid);
    }
  }
}
