package org.matheclipse.core.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import org.matheclipse.core.eval.EvalEngine;

/**
 * Opens a web address that the user of a kernel named.
 *
 * <p>
 * A kernel that is the user's own - see {@link FileSandbox#isHostVisible(EvalEngine)} - reads
 * whatever address it is given, as the machine it runs on is the user's too. Any other kernel
 * answers for somebody who is not on that machine: a browser session, a request to a server. Seen
 * from there, the kernel is a way to make the server send requests, so there it reaches only what
 * anybody could reach without it:
 * <ul>
 * <li><code>http</code> and <code>https</code>, nothing else;
 * <li>hosts on the public internet - not the server itself, not the private network it stands in,
 * not the link local addresses a cloud host keeps its metadata under;
 * <li>redirects are followed by hand, a few of them, each checked the same way;
 * <li>with time limits, and no more than {@link #MAX_BYTES}.
 * </ul>
 *
 * <p>
 * The host is looked up once to be checked and again by the connection, and a name can be made to
 * answer the two differently. Over <code>https</code> that gains nothing: the machine reached has
 * to hold a certificate for the name, which a machine of somebody else's private network does not.
 * Over <code>http</code> the JVM's own cache of addresses is what makes the second lookup give the
 * answer that was checked - so where that cache is switched off, <code>http</code> is not read.
 */
public final class WebFetch {

  /** The most a kernel that is not the user's own reads from one address, in bytes. */
  public static final long MAX_BYTES = Long.getLong("symja.fetch.maxBytes", 16L * 1024 * 1024);

  private static final int CONNECT_TIMEOUT_MILLIS = 10_000;

  private static final int READ_TIMEOUT_MILLIS = 30_000;

  private static final int MAX_REDIRECTS = 5;

  private WebFetch() {}

  /**
   * @param address the address as the user wrote it
   * @throws IOException if the address cannot be read, or may not be read from this kernel
   */
  public static InputStream open(String address, EvalEngine engine) throws IOException {
    URL url = new URL(address);
    if (FileSandbox.isHostVisible(engine)) {
      return url.openStream();
    }
    for (int hop = 0; hop <= MAX_REDIRECTS; hop++) {
      String protocol = url.getProtocol();
      if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
        throw new IOException("Only http and https addresses can be read here.");
      }
      if ("http".equalsIgnoreCase(protocol) && !addressesAreCached()) {
        throw new IOException("Only https addresses can be read here.");
      }
      checkPublic(url.getHost());
      URLConnection opened = url.openConnection();
      if (!(opened instanceof HttpURLConnection)) {
        throw new IOException("Only http and https addresses can be read here.");
      }
      HttpURLConnection connection = (HttpURLConnection) opened;
      connection.setInstanceFollowRedirects(false);
      connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
      connection.setReadTimeout(READ_TIMEOUT_MILLIS);
      int status = connection.getResponseCode();
      if (status == 301 || status == 302 || status == 303 || status == 307 || status == 308) {
        String location = connection.getHeaderField("Location");
        connection.disconnect();
        if (location == null) {
          throw new IOException("A redirect that does not say where to.");
        }
        url = new URL(url, location);
        continue;
      }
      if (status >= 400) {
        connection.disconnect();
        throw new IOException("The server answered " + status + ".");
      }
      return new Limited(connection.getInputStream(), MAX_BYTES);
    }
    throw new IOException("Too many redirects.");
  }

  /**
   * Whether the JVM keeps the addresses a name was looked up to, which is what it does unless it
   * was told not to: <code>networkaddress.cache.ttl</code> in the security properties, or the
   * older system property <code>sun.net.inetaddr.ttl</code>, set to zero.
   */
  static boolean addressesAreCached() {
    return !isZero(java.security.Security.getProperty("networkaddress.cache.ttl"))
        && !isZero(System.getProperty("sun.net.inetaddr.ttl"));
  }

  private static boolean isZero(String setting) {
    try {
      return setting != null && Integer.parseInt(setting.trim()) == 0;
    } catch (NumberFormatException nfe) {
      return false;
    }
  }

  /** @throws IOException if the host is not, with every address it has, on the public internet */
  static void checkPublic(String host) throws IOException {
    if (host == null || host.isEmpty()) {
      throw new IOException("An address without a host.");
    }
    for (InetAddress address : InetAddress.getAllByName(host)) {
      if (!isPublic(address)) {
        throw new IOException("This address is not on the public internet.");
      }
    }
  }

  /** Whether an address belongs to the public internet rather than to a machine or its network. */
  static boolean isPublic(InetAddress address) {
    if (address.isAnyLocalAddress() || address.isLoopbackAddress() || address.isLinkLocalAddress()
        || address.isSiteLocalAddress() || address.isMulticastAddress()) {
      return false;
    }
    byte[] bytes = address.getAddress();
    if (bytes.length == 4) {
      int first = bytes[0] & 0xff;
      int second = bytes[1] & 0xff;
      // "this network", the addresses a provider shares between its customers, and the reserved
      // block at the top, the broadcast address included
      return first != 0 && !(first == 100 && (second & 0xc0) == 64) && first < 240;
    }
    // the unique local addresses of IPv6, its counterpart to the private IPv4 ranges
    return (bytes[0] & 0xfe) != 0xfc;
  }

  /** A stream that fails rather than hand out more than it was told to. */
  private static final class Limited extends FilterInputStream {
    private long remaining;

    Limited(InputStream in, long limit) {
      super(in);
      this.remaining = limit;
    }

    private void count(long n) throws IOException {
      if (n > 0) {
        remaining -= n;
        if (remaining < 0) {
          throw new IOException("More than " + MAX_BYTES + " bytes; the rest is not read.");
        }
      }
    }

    @Override
    public int read() throws IOException {
      int b = super.read();
      count(b < 0 ? 0 : 1);
      return b;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
      int n = super.read(buffer, offset, length);
      count(n);
      return n;
    }
  }
}
