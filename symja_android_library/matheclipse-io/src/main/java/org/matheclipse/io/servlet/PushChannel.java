package org.matheclipse.io.servlet;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A way for a server to tell the pages that are open on it something without being asked: a text
 * sent here reaches every browser that is connected at this moment.
 *
 * <p>
 * It is a WebSocket at {@link #path()}, but nothing here says so. The class that makes it one is
 * {@link UndertowLauncher}, the only one that may mention the embedded server - see there for why -
 * so an application hands a channel to {@link ServletServer#runAppServer} and is handed back the
 * same object, now connected.
 *
 * <p>
 * The channel carries what the server has to say and nothing the other way: what a browser sends
 * is not read. A browser is let in only from a page of the server itself.
 */
public final class PushChannel {

  private final String path;

  private volatile Consumer<String> sender = text -> {
  };

  private volatile Supplier<String> greeting = () -> null;

  /**
   * @param path where browsers connect, starting with a slash and outside <code>/ajax</code>, as in
   *        <code>/push</code>
   */
  public PushChannel(String path) {
    this.path = path;
  }

  public String path() {
    return path;
  }

  /** Send a text to every browser connected now. Before the server runs this does nothing. */
  public void send(String text) {
    sender.accept(text);
  }

  /**
   * What a browser is told the moment it connects, or <code>null</code> for nothing - the state it
   * may have missed while it was not connected.
   */
  public void setGreeting(Supplier<String> greeting) {
    this.greeting = greeting;
  }

  String greeting() {
    return greeting.get();
  }

  /** Called by the launcher once there is something to send through. */
  void bind(Consumer<String> sender) {
    this.sender = sender;
  }
}
