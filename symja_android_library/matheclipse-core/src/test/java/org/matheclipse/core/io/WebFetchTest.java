package org.matheclipse.core.io;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.IOException;
import java.net.InetAddress;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

/** Which addresses a kernel that is not the user's own may read. Nothing here opens a connection. */
public class WebFetchTest {

  private static boolean isPublic(String literal) throws IOException {
    // a literal address is not looked up
    return WebFetch.isPublic(InetAddress.getByName(literal));
  }

  @Test
  public void addressesOfTheMachineAndItsNetworkAreNotPublic() throws IOException {
    for (String address : new String[] {"127.0.0.1", "127.8.9.10", "0.0.0.0", "10.1.2.3",
        "172.16.0.1", "172.31.255.254", "192.168.1.1", "169.254.169.254", "100.64.0.1",
        "100.127.255.254", "224.0.0.1", "240.0.0.1", "255.255.255.255", "::1", "::", "fe80::1",
        "fc00::1", "fd12:3456::1", "ff02::1"}) {
      assertFalse(isPublic(address), address);
    }
  }

  @Test
  public void otherAddressesArePublic() throws IOException {
    for (String address : new String[] {"93.184.216.34", "8.8.8.8", "172.32.0.1", "100.63.0.1",
        "100.128.0.1", "2001:4860:4860::8888"}) {
      assertTrue(isPublic(address), address);
    }
  }

  @Test
  public void aConfinedKernelIsRefusedBeforeAnythingIsOpened() {
    boolean fileSystemEnabled = Config.FILESYSTEM_ENABLED;
    Config.FILESYSTEM_ENABLED = false;
    try {
      EvalEngine engine = new EvalEngine(false);
      assertThrows(IOException.class, () -> WebFetch.open("http://127.0.0.1:9/", engine));
      assertThrows(IOException.class, () -> WebFetch.open("http://[::1]:9/", engine));
      assertThrows(IOException.class, () -> WebFetch.open("file:///etc/hosts", engine));
      assertThrows(IOException.class, () -> WebFetch.open("ftp://127.0.0.1/x", engine));
    } finally {
      Config.FILESYSTEM_ENABLED = fileSystemEnabled;
    }
  }

  /**
   * What makes the address that was checked the address that is connected to. A JVM keeps looked
   * up addresses unless it is told not to; with that switched off, plain http is not read.
   */
  @Test
  public void theAddressCacheIsWhatHttpReliesOn() {
    assertTrue(WebFetch.addressesAreCached(), "the default of a JVM");
    String ttl = System.getProperty("sun.net.inetaddr.ttl");
    System.setProperty("sun.net.inetaddr.ttl", "0");
    try {
      assertFalse(WebFetch.addressesAreCached());
    } finally {
      if (ttl == null) {
        System.clearProperty("sun.net.inetaddr.ttl");
      } else {
        System.setProperty("sun.net.inetaddr.ttl", ttl);
      }
    }
  }

  /** An archive unpacks to what its entries say, not to what it weighs: the limit is on that. */
  @Test
  public void anArchiveIsNotUnpackedPastTheLimit(@org.junit.jupiter.api.io.TempDir java.nio.file.Path dir)
      throws IOException {
    java.nio.file.Path archive = dir.resolve("zeros.zip");
    try (java.util.zip.ZipOutputStream zip =
        new java.util.zip.ZipOutputStream(java.nio.file.Files.newOutputStream(archive))) {
      zip.putNextEntry(new java.util.zip.ZipEntry("small.bin"));
      zip.write(new byte[100]);
      zip.closeEntry();
      zip.putNextEntry(new java.util.zip.ZipEntry("large.bin"));
      zip.write(new byte[200_000]);
      zip.closeEntry();
    }
    assertTrue(java.nio.file.Files.size(archive) < 2_000, "200 kB of zeros compress to very little");

    java.nio.file.Path all = dir.resolve("all");
    org.junit.jupiter.api.Assertions.assertEquals(2, ZipArchive.extract(archive, all).size());

    java.nio.file.Path capped = dir.resolve("capped");
    assertThrows(IOException.class, () -> ZipArchive.extract(archive, capped, 10_000));
    assertTrue(java.nio.file.Files.exists(capped.resolve("small.bin")));
    assertFalse(java.nio.file.Files.exists(capped.resolve("large.bin")));
  }
}
