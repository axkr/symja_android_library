package org.matheclipse.core.io.link;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The links this process has open.
 *
 * <p>
 * A link is either a process this kernel launched - its standard input and output are the link -
 * or a loopback connection between two kernels which know each other by a name. The name is a file
 * under <code>symja-links</code> in the temporary directory holding the port the listening end
 * chose, which is how <code>LinkConnect["abcd"]</code> finds the <code>LinkCreate["abcd"]</code>
 * that is waiting for it.
 */
public final class LinkRegistry {

  private static final Map<String, LinkEntry> LINKS = new ConcurrentHashMap<String, LinkEntry>();

  private static final AtomicLong COUNTER = new AtomicLong(1);

  /** Where a named link leaves the port it is listening on. */
  private static Path portFile(String name) {
    return Paths.get(System.getProperty("java.io.tmpdir"), "symja-links", name);
  }

  public static LinkEntry get(String uuid) {
    return uuid == null ? null : LINKS.get(uuid);
  }

  public static Collection<LinkEntry> links() {
    return new ArrayList<LinkEntry>(LINKS.values());
  }

  private static String newUuid() {
    return "LINK-" + COUNTER.getAndIncrement();
  }

  /** Register a link over a pair of streams. */
  public static LinkEntry register(String name, InputStream in, OutputStream out,
      Process process) {
    DataInputStream input = LinkCodec.input(in);
    DataOutputStream output = LinkCodec.output(out);
    LinkEntry entry = new LinkEntry(newUuid(), name, input, output, process);
    LINKS.put(entry.uuid(), entry);
    entry.start();
    return entry;
  }

  /**
   * Launch a process and talk to it over its standard input and output.
   *
   * @param command the command and its arguments, already split
   * @return the link, or <code>null</code> if the process would not start
   */
  public static LinkEntry launch(List<String> command) {
    try {
      ProcessBuilder builder = new ProcessBuilder(command);
      // the child's own diagnostics stay on this process's error output, where a person sees them
      builder.redirectError(ProcessBuilder.Redirect.INHERIT);
      Process process = builder.start();
      return register(String.join(" ", command), process.getInputStream(),
          process.getOutputStream(), process);
    } catch (IOException ioe) {
      return null;
    }
  }

  /**
   * Listen for a kernel which will connect by this name. The link is not usable until the other
   * end arrives, which {@link #accept} waits for.
   */
  public static PendingLink create(String name) {
    try {
      ServerSocket server = new ServerSocket();
      server.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
      Path file = portFile(name);
      Files.createDirectories(file.getParent());
      Files.write(file, Integer.toString(server.getLocalPort()).getBytes(StandardCharsets.UTF_8));
      return new PendingLink(name, server, file);
    } catch (IOException ioe) {
      return null;
    }
  }

  /** Connect to a kernel which is listening under this name, waiting up to <code>millis</code>. */
  public static LinkEntry connect(String name, long millis) {
    long deadline = System.currentTimeMillis() + millis;
    Path file = portFile(name);
    while (System.currentTimeMillis() < deadline) {
      try {
        if (Files.exists(file)) {
          int port =
              Integer.parseInt(new String(Files.readAllBytes(file), StandardCharsets.UTF_8).trim());
          Socket socket = new Socket(InetAddress.getLoopbackAddress(), port);
          socket.setTcpNoDelay(true);
          return register(name, socket.getInputStream(), socket.getOutputStream(), null);
        }
      } catch (IOException | NumberFormatException e) {
        // the other end is not listening yet, or is still writing the port: look again
      }
      try {
        Thread.sleep(20);
      } catch (InterruptedException ie) {
        Thread.currentThread().interrupt();
        return null;
      }
    }
    return null;
  }

  /** A link which is listening for the kernel that will use it. */
  public static final class PendingLink {
    private final String name;
    private final ServerSocket server;
    private final Path file;

    PendingLink(String name, ServerSocket server, Path file) {
      this.name = name;
      this.server = server;
      this.file = file;
    }

    public String name() {
      return name;
    }

    /**
     * Wait for the other end and answer the link it opened, or <code>null</code> if nobody came
     * within <code>millis</code>.
     */
    public LinkEntry accept(int millis) {
      try {
        server.setSoTimeout(millis);
        Socket socket = server.accept();
        socket.setTcpNoDelay(true);
        return register(name, socket.getInputStream(), socket.getOutputStream(), null);
      } catch (IOException ioe) {
        return null;
      } finally {
        try {
          server.close();
        } catch (IOException ioe) {
          //
        }
        try {
          Files.deleteIfExists(file);
        } catch (IOException ioe) {
          //
        }
      }
    }
  }

  public static boolean close(String uuid) {
    LinkEntry entry = LINKS.remove(uuid);
    if (entry == null) {
      return false;
    }
    entry.close();
    Process process = entry.process();
    if (process != null) {
      process.destroy();
    }
    return true;
  }

  /** Close every link, which is what leaving the kernel does. */
  public static void closeAll() {
    for (String uuid : new ArrayList<String>(LINKS.keySet())) {
      close(uuid);
    }
  }

  private LinkRegistry() {}
}
