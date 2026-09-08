package org.matheclipse.core.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Reading and writing ZIP archives.
 *
 * <p>
 * A paclet is shipped as one, and a package manager downloads and unpacks it: that is what
 * <code>ExtractArchive</code> is for, and why this is here rather than only in the modules that do
 * data import.
 */
public final class ZipArchive {

  private ZipArchive() {}

  /** One file inside an archive. */
  public static final class Entry {
    private final String name;
    private final byte[] content;

    public Entry(String name, byte[] content) {
      this.name = name;
      this.content = content;
    }

    public String name() {
      return name;
    }

    public byte[] content() {
      return content;
    }

    public String contentAsString() {
      return new String(content, StandardCharsets.UTF_8);
    }
  }

  /** The names of the files in an archive, directories excluded. */
  public static List<String> names(Path archive) throws IOException {
    List<String> names = new ArrayList<String>();
    for (Entry entry : read(archive, null)) {
      names.add(entry.name());
    }
    return names;
  }

  /**
   * The entries of an archive.
   *
   * @param wanted the names to read, or <code>null</code> for all of them
   */
  public static List<Entry> read(Path archive, List<String> wanted) throws IOException {
    List<Entry> entries = new ArrayList<Entry>();
    try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(archive))) {
      ZipEntry zipEntry;
      while ((zipEntry = zip.getNextEntry()) != null) {
        if (zipEntry.isDirectory()) {
          continue;
        }
        if (wanted != null && !wanted.contains(zipEntry.getName())) {
          continue;
        }
        entries.add(new Entry(zipEntry.getName(), readAll(zip)));
      }
    }
    return entries;
  }

  /**
   * Unpack an archive into <code>directory</code>.
   *
   * <p>
   * An entry whose name would put it outside the directory is refused. Archives are downloaded from
   * the network - a package manager does exactly that - and an entry named
   * <code>../../.bashrc</code> is how an archive writes a file the caller did not ask for.
   *
   * @return the files written
   */
  public static List<Path> extract(Path archive, Path directory) throws IOException {
    List<Path> written = new ArrayList<Path>();
    Path root = directory.toAbsolutePath().normalize();
    Files.createDirectories(root);
    try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(archive))) {
      ZipEntry zipEntry;
      while ((zipEntry = zip.getNextEntry()) != null) {
        Path target = root.resolve(zipEntry.getName()).normalize();
        if (!target.startsWith(root)) {
          throw new IOException("the archive entry " + zipEntry.getName()
              + " would be written outside " + root);
        }
        if (zipEntry.isDirectory()) {
          Files.createDirectories(target);
          continue;
        }
        Path parent = target.getParent();
        if (parent != null) {
          Files.createDirectories(parent);
        }
        Files.write(target, readAll(zip));
        written.add(target);
      }
    }
    return written;
  }

  /** Write <code>entries</code> to a new archive. */
  public static void write(Path archive, List<Entry> entries) throws IOException {
    Path parent = archive.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(archive))) {
      for (Entry entry : entries) {
        zip.putNextEntry(new ZipEntry(entry.name()));
        zip.write(entry.content());
        zip.closeEntry();
      }
    }
  }

  /** Every file under <code>directory</code>, named relative to it. */
  public static List<Entry> entriesOf(Path directory) throws IOException {
    List<Entry> entries = new ArrayList<Entry>();
    Path root = directory.toAbsolutePath().normalize();
    try (java.util.stream.Stream<Path> walk = Files.walk(root)) {
      List<Path> files =
          walk.filter(Files::isRegularFile).sorted().collect(java.util.stream.Collectors.toList());
      for (Path file : files) {
        entries.add(new Entry(root.relativize(file).toString().replace('\\', '/'),
            Files.readAllBytes(file)));
      }
    }
    return entries;
  }

  private static byte[] readAll(InputStream in) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream(8192);
    copy(in, buffer);
    return buffer.toByteArray();
  }

  private static void copy(InputStream in, OutputStream out) throws IOException {
    byte[] chunk = new byte[8192];
    int read;
    while ((read = in.read(chunk)) > 0) {
      out.write(chunk, 0, read);
    }
  }
}
