package org.matheclipse.core.io.net;

import java.io.ByteArrayOutputStream;
import java.nio.channels.SelectableChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/** One open socket: the channel, where it points, and what has been read from it but not taken. */
public final class SocketEntry {

  /** What a socket is: something that accepts connections, or one end of one. */
  public enum Role {
    /** Opened with <code>SocketOpen</code>: it listens. */
    SERVER,
    /** Opened with <code>SocketConnect</code>: the near end of an outgoing connection. */
    CLIENT,
    /** A connection a server accepted. */
    ACCEPTED
  }

  private final String uuid;
  private final Role role;
  private final SelectableChannel channel;
  private final String host;
  private final int port;
  private final String parentUuid;

  /** Bytes read by the selector thread and not yet taken by an evaluation. */
  private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

  private final List<String> acceptedUuids = new ArrayList<String>();

  private volatile boolean closed;
  private volatile boolean endOfStream;
  private volatile boolean listening;

  SocketEntry(String uuid, Role role, SelectableChannel channel, String host, int port,
      String parentUuid) {
    this.uuid = uuid;
    this.role = role;
    this.channel = channel;
    this.host = host;
    this.port = port;
    this.parentUuid = parentUuid;
  }

  public String uuid() {
    return uuid;
  }

  public Role role() {
    return role;
  }

  public SelectableChannel channel() {
    return channel;
  }

  public SocketChannel socketChannel() {
    return channel instanceof SocketChannel ? (SocketChannel) channel : null;
  }

  public ServerSocketChannel serverChannel() {
    return channel instanceof ServerSocketChannel ? (ServerSocketChannel) channel : null;
  }

  public String host() {
    return host;
  }

  public int port() {
    return port;
  }

  /** The server this connection was accepted by, or <code>null</code>. */
  public String parentUuid() {
    return parentUuid;
  }

  public List<String> acceptedUuids() {
    return acceptedUuids;
  }

  public boolean isClosed() {
    return closed;
  }

  void markClosed() {
    closed = true;
  }

  public boolean isEndOfStream() {
    return endOfStream;
  }

  void markEndOfStream() {
    endOfStream = true;
  }

  /** Is a handler attached? Without one, what arrives waits to be read instead. */
  public boolean isListening() {
    return listening;
  }

  void setListening(boolean listening) {
    this.listening = listening;
  }

  /** Keep what the selector thread read. */
  synchronized void append(byte[] bytes, int length) {
    buffer.write(bytes, 0, length);
  }

  /** How many bytes are waiting. */
  public synchronized int available() {
    return buffer.size();
  }

  /**
   * Take what is waiting, at most <code>limit</code> bytes.
   *
   * @param limit a negative number means everything
   */
  public synchronized byte[] take(int limit) {
    byte[] all = buffer.toByteArray();
    buffer.reset();
    if (limit < 0 || limit >= all.length) {
      return all;
    }
    byte[] head = new byte[limit];
    System.arraycopy(all, 0, head, 0, limit);
    buffer.write(all, limit, all.length - limit);
    return head;
  }
}
