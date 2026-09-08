package org.matheclipse.core.io.paclet;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * What a <code>PacletInfo.wl</code> (or <code>PacletInfo.m</code>) says about a paclet: its name and
 * version, where it lives, and which Wolfram Language context each of its files provides.
 *
 * <p>
 * Only the "Kernel" extension is read. That is the one that says where a context's code is, which is
 * what <code>Needs</code> has to know; the other extensions describe documentation and front-end
 * resources that Symja has no use for.
 */
public final class PacletInfo {

  /** One <code>{context, file}</code> entry of a Kernel extension. */
  public static final class ContextFile {
    private final String context;
    private final String fileName;

    public ContextFile(String context, String fileName) {
      this.context = context;
      this.fileName = fileName;
    }

    public String context() {
      return context;
    }

    /** The file the context lives in, or <code>null</code> when the paclet did not name one. */
    public String fileName() {
      return fileName;
    }
  }

  private final String name;
  private final String version;
  private final Path directory;
  private final Path kernelRoot;
  private final List<ContextFile> contexts;

  public PacletInfo(String name, String version, Path directory, Path kernelRoot,
      List<ContextFile> contexts) {
    this.name = name;
    this.version = version;
    this.directory = directory;
    this.kernelRoot = kernelRoot;
    this.contexts = new ArrayList<ContextFile>(contexts);
  }

  public String name() {
    return name;
  }

  public String version() {
    return version;
  }

  /** The paclet's own directory, the one that holds its <code>PacletInfo</code> file. */
  public Path directory() {
    return directory;
  }

  /** The directory the Kernel extension's files are named relative to. */
  public Path kernelRoot() {
    return kernelRoot;
  }

  public List<ContextFile> contexts() {
    return contexts;
  }

  /**
   * The file this paclet provides <code>context</code> in, or <code>null</code> if it does not
   * provide it.
   *
   * <p>
   * A context declared without a file name is looked for by the usual conventions:
   * <code>A`B`</code> lives in <code>A/B.wl</code>, in <code>B.wl</code> beside the paclet, or in an
   * <code>init.m</code> of its own directory.
   */
  public Path fileFor(String context) {
    for (ContextFile contextFile : contexts) {
      if (contextFile.context().equals(context)) {
        String fileName = contextFile.fileName();
        if (fileName != null) {
          return kernelRoot.resolve(fileName);
        }
        return conventionalFile(context);
      }
    }
    return null;
  }

  private Path conventionalFile(String context) {
    String[] segments = context.split("`");
    if (segments.length == 0) {
      return null;
    }
    String last = segments[segments.length - 1];
    Path[] candidates = new Path[] {//
        kernelRoot.resolve(last + ".wl"), //
        kernelRoot.resolve(last + ".m"), //
        kernelRoot.resolve(String.join("/", segments) + ".wl"), //
        kernelRoot.resolve(String.join("/", segments) + ".m"), //
        kernelRoot.resolve(last).resolve("init.wl"), //
        kernelRoot.resolve(last).resolve("init.m"), //
        kernelRoot.resolve("init.wl"), //
        kernelRoot.resolve("init.m")};
    for (Path candidate : candidates) {
      if (java.nio.file.Files.isRegularFile(candidate)) {
        return candidate;
      }
    }
    return null;
  }
}
