import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Writes the class files of the <code>java.*</code> and <code>jdk.*</code> modules of the running
 * Java runtime into one jar: the library classes of the notebook-proguard execution on a runtime
 * without <code>jmods</code>, such as Eclipse's JustJ JRE or a JDK built without them (Temurin 25,
 * GraalVM 25). Every runtime image has the classes in <code>lib/modules</code>, readable through
 * the <code>jrt:</code> file system.
 *
 * <p>
 * Not part of the module's sources: the pom runs it with <code>&lt;java sourcefile=...&gt;</code>,
 * the single-file source launcher.
 *
 * <p>
 * Usage: <code>java JrtLibraryJar.java &lt;output.jar&gt;</code>
 */
public class JrtLibraryJar {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new IllegalArgumentException("usage: java JrtLibraryJar.java <output.jar>");
    }
    Path out = Paths.get(args[0]).toAbsolutePath();
    Files.createDirectories(out.getParent());
    // written next to the target and moved in place, so an interrupted run leaves no partial jar
    Path partial = out.resolveSibling(out.getFileName() + ".partial");
    FileSystem jrt = FileSystems.getFileSystem(URI.create("jrt:/"));
    int count = 0;
    try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(partial));
        DirectoryStream<Path> modules = Files.newDirectoryStream(jrt.getPath("/modules"))) {
      zip.setLevel(1);
      for (Path module : modules) {
        String name = module.getFileName().toString();
        if (!name.startsWith("java.") && !name.startsWith("jdk.")) {
          continue;
        }
        try (Stream<Path> files = Files.walk(module)) {
          Iterator<Path> iterator = files.iterator();
          while (iterator.hasNext()) {
            Path file = iterator.next();
            // a package belongs to one module only, so the entries don't collide
            String entry = module.relativize(file).toString();
            if (!entry.endsWith(".class") || entry.equals("module-info.class")) {
              continue;
            }
            zip.putNextEntry(new ZipEntry(entry));
            Files.copy(file, zip);
            zip.closeEntry();
            count++;
          }
        }
      }
    }
    Files.move(partial, out, StandardCopyOption.REPLACE_EXISTING);
    System.out.println("JrtLibraryJar: " + count + " classes of Java "
        + System.getProperty("java.version") + " written to " + out);
  }
}
