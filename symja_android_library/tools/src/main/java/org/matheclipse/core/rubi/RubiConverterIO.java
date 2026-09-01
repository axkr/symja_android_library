package org.matheclipse.core.rubi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * File system helper shared by {@link ConvertRubi} and {@link ConvertRubiUtilityFunctions}.
 *
 * <p>
 * All paths are built with {@link Path} instead of hard coded separators, so the converters run
 * unchanged on Windows, Linux and macOS. Input and output are always read and written as UTF-8: the
 * Rubi {@code .m} files contain non ASCII characters (for example {@code U+22C6}) and so do many of
 * the generated {@code IntRules*.java} files, whereas {@code FileReader}/{@code FileWriter} use the
 * platform default charset.
 *
 * <p>
 * The locations can be overridden, in this order of precedence:
 *
 * <ol>
 * <li>program arguments: {@code <input-file> [<output-directory>]}
 * <li>system properties {@code -Drubi.input=...} and {@code -Drubi.outputDir=...}
 * <li>auto detection of the project directory, starting at the current working directory
 * </ol>
 */
public final class RubiConverterIO {

  /** System property which overrides the Rubi {@code .m} input file. */
  public static final String PROP_INPUT = "rubi.input";

  /** System property which overrides the directory the Java sources are written to. */
  public static final String PROP_OUTPUT_DIR = "rubi.outputDir";

  /** System property to keep the previously generated files; {@code -Drubi.clean=false}. */
  public static final String PROP_CLEAN = "rubi.clean";

  /** Directory with the Rubi {@code .m} input files, relative to the project directory. */
  private static final String[] RUBI_INPUT_SEGMENTS = {"Rubi"};

  /** Directory of the generated sources, relative to the project directory. */
  private static final String[] GENERATED_PACKAGE_SEGMENTS = {"matheclipse-core", "src", "main",
      "java", "org", "matheclipse", "core", "integrate", "rubi"};

  /**
   * A file which has to exist in the output directory before any file is deleted there. It is not
   * generated and guards against deleting files in a wrongly resolved directory.
   */
  private static final String OUTPUT_DIR_MARKER = "UtilityFunctionCtors.java";

  private static Path projectDirectory;

  private RubiConverterIO() {}

  /**
   * The {@code symja_android_library} directory which contains both the {@code Rubi} input files and
   * the {@code matheclipse-core} module.
   *
   * @throws IllegalStateException if the directory cannot be located
   */
  public static synchronized Path projectDirectory() {
    if (projectDirectory == null) {
      projectDirectory = findProjectDirectory();
    }
    return projectDirectory;
  }

  /** Directory with the Rubi {@code .m} input files. */
  public static Path rubiDirectory() {
    return resolve(projectDirectory(), RUBI_INPUT_SEGMENTS);
  }

  /** Directory of the generated {@code org.matheclipse.core.integrate.rubi} sources. */
  public static Path defaultOutputDirectory() {
    return resolve(projectDirectory(), GENERATED_PACKAGE_SEGMENTS);
  }

  /**
   * Determine the Rubi {@code .m} file to convert.
   *
   * @param args the program arguments; {@code args[0]} overrides the input file
   * @param defaultFileName the file name to use inside {@link #rubiDirectory()} if nothing was
   *        overridden
   * @throws IllegalStateException if the resolved file doesn't exist
   */
  public static Path resolveInputFile(String[] args, String defaultFileName) {
    Path file = firstNonNull(argument(args, 0), property(PROP_INPUT));
    if (file == null) {
      file = rubiDirectory().resolve(defaultFileName);
    }
    file = file.toAbsolutePath().normalize();
    if (!Files.isRegularFile(file)) {
      throw new IllegalStateException("Rubi input file not found: '" + file
          + "'. Pass the file as first program argument or set -D" + PROP_INPUT + "=<file>.");
    }
    return file;
  }

  /**
   * Determine the directory the generated Java sources are written to. The directory is created if
   * it doesn't exist yet.
   *
   * @param args the program arguments; {@code args[1]} overrides the output directory
   */
  public static Path resolveOutputDirectory(String[] args) throws IOException {
    Path directory = firstNonNull(argument(args, 1), property(PROP_OUTPUT_DIR));
    if (directory == null) {
      directory = defaultOutputDirectory();
    }
    directory = directory.toAbsolutePath().normalize();
    Files.createDirectories(directory);
    return directory;
  }

  /**
   * Delete the previously generated files in {@code directory} before the new ones are written, so
   * that a run which produces fewer files doesn't leave stale rules behind.
   *
   * <p>
   * Only regular files whose name matches {@code fileNamePattern} are deleted; everything else in
   * the package directory is left untouched.
   *
   * @param fileNamePattern must match the complete file name, for example {@code IntRules\d+\.java}
   * @return the number of deleted files
   * @throws IOException if {@code directory} isn't the generated Rubi source directory
   */
  public static int deleteGeneratedFiles(Path directory, Pattern fileNamePattern)
      throws IOException {
    if (!isCleanEnabled()) {
      System.out.println(">>>>> Keeping the existing files (-D" + PROP_CLEAN + "=false)");
      return 0;
    }
    if (!Files.isDirectory(directory)) {
      throw new IOException("Not a directory: '" + directory + "'");
    }
    if (!Files.isRegularFile(directory.resolve(OUTPUT_DIR_MARKER))) {
      throw new IOException("Refusing to delete files in '" + directory + "': the directory doesn't "
          + "contain '" + OUTPUT_DIR_MARKER + "' and therefore isn't the generated Rubi source "
          + "directory.");
    }
    int deleted = 0;
    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
      for (Path entry : stream) {
        if (Files.isRegularFile(entry)
            && fileNamePattern.matcher(entry.getFileName().toString()).matches()) {
          Files.delete(entry);
          deleted++;
        }
      }
    }
    System.out.println(">>>>> Deleted " + deleted + " file(s) matching '" + fileNamePattern
        + "' in " + directory);
    return deleted;
  }

  /** Open the given file for reading as UTF-8. */
  public static BufferedReader newReader(Path file) throws IOException {
    return Files.newBufferedReader(file, StandardCharsets.UTF_8);
  }

  /**
   * Remove the separator after the last rule of a file, so that the following {@code );} is valid
   * Java.
   *
   * <p>
   * The converters flag the last parsed node of a file, but a single node may produce several rules
   * or no rule at all - a {@code CompoundExpression} is unrolled and everything which isn't a
   * {@code SetDelayed} is skipped. The separator therefore cannot reliably be suppressed while the
   * rules are written.
   */
  public static void stripTrailingRuleSeparator(StringBuffer buffer) {
    int end = buffer.length();
    while (end > 0 && Character.isWhitespace(buffer.charAt(end - 1))) {
      end--;
    }
    if (end > 0 && buffer.charAt(end - 1) == ',') {
      buffer.deleteCharAt(end - 1);
    }
  }

  /** Write {@code buffer} to {@code file} as UTF-8, replacing an already existing file. */
  public static void writeFile(Path file, CharSequence buffer) throws IOException {
    try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE)) {
      writer.append(buffer);
    }
  }

  /**
   * Count the {@code ... = org.matheclipse.core.integrate.rubi.<classNamePrefix><n>.RULES;} lines in
   * {@code UtilityFunctionCtors.java}. Commented out lines are not counted.
   *
   * @return the number of registered classes or {@code -1} if the file cannot be read
   */
  public static int countRegisteredClasses(Path outputDirectory, String classNamePrefix) {
    Path file = outputDirectory.resolve(OUTPUT_DIR_MARKER);
    if (!Files.isRegularFile(file)) {
      return -1;
    }
    Pattern pattern = Pattern.compile("^\\s*org\\.matheclipse\\.core\\.integrate\\.rubi\\."
        + classNamePrefix + "\\d+\\.RULES\\s*=\\s*null;");
    try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) {
      return (int) lines.filter(line -> pattern.matcher(line).find()).count();
    } catch (IOException e) {
      return -1;
    }
  }

  /**
   * Collect capturing group 1 of every match of {@code pattern} across the generated
   * {@code <classNamePrefix>NNN.java} files in {@code outputDirectory}.
   *
   * @param outputDirectory the directory the converters write to
   * @param classNamePrefix for example {@code IntRules} or {@code UtilityFunctions}
   * @param pattern a pattern with at least one capturing group
   * @return the distinct captured values, sorted
   */
  public static Set<String> scanGeneratedSources(Path outputDirectory, String classNamePrefix,
      Pattern pattern) throws IOException {
    Set<String> found = new TreeSet<>();
    Pattern fileName = Pattern.compile(Pattern.quote(classNamePrefix) + "\\d+\\.java");
    try (DirectoryStream<Path> files = Files.newDirectoryStream(outputDirectory,
        entry -> fileName.matcher(entry.getFileName().toString()).matches())) {
      for (Path file : files) {
        Matcher matcher = pattern.matcher(Files.readString(file, StandardCharsets.UTF_8));
        while (matcher.find()) {
          found.add(matcher.group(1));
        }
      }
    }
    return found;
  }

  /** Start of the block which {@link #writeRegistration} rewrites, plus the class name prefix. */
  private static final String BEGIN_MARKER = "// <generated registration: ";

  /** End of the block which {@link #writeRegistration} rewrites. */
  private static final String END_MARKER = "// </generated registration>";

  /**
   * Build the assignments of a {@code UtilityFunctionCtors} registration method.
   *
   * <p>
   * Reading {@code RULES} forces the class initializer of one generated class, which registers its
   * rules as a side effect; assigning {@code null} then drops the duplicate reference.
   *
   * @param classNamePrefix for example {@code IntRules}
   * @param fileCount the number of generated classes
   * @param indent the indentation of the surrounding method body
   */
  public static String registrationBody(String classNamePrefix, int fileCount, String indent) {
    StringBuilder buf = new StringBuilder(fileCount * 70);
    for (int i = 0; i < fileCount; i++) {
      if (i > 0 && i % 10 == 0) {
        buf.append('\n');
      }
      buf.append(indent).append("org.matheclipse.core.integrate.rubi.").append(classNamePrefix)
          .append(i).append(".RULES = null;\n");
    }
    return buf.toString();
  }

  /**
   * Rewrite the registration of the generated classes in {@code UtilityFunctionCtors.java} so that
   * converting a new Rubi release needs no hand editing.
   *
   * <p>
   * The block to replace is delimited by {@code // <generated registration: <prefix>>} and
   * {@code // </generated registration>}. When the markers are missing - because someone commented
   * an entry out on purpose, or the file was reorganised - nothing is touched and the replacement
   * is written next to the generated sources for review instead.
   *
   * @param outputDirectory the directory holding {@code UtilityFunctionCtors.java}
   * @param classNamePrefix for example {@code IntRules}
   * @param fileCount the number of generated classes
   * @param methodName the registration method, used for the fall back file name
   */
  public static void writeRegistration(Path outputDirectory, String classNamePrefix, int fileCount,
      String methodName) throws IOException {
    System.out.println(">>>>> Generated " + fileCount + " " + classNamePrefix + "*.java file(s)");
    Path file = outputDirectory.resolve(OUTPUT_DIR_MARKER);
    String source = Files.isRegularFile(file) ? Files.readString(file, StandardCharsets.UTF_8) : null;
    String begin = BEGIN_MARKER + classNamePrefix + ">";

    int from = source == null ? -1 : source.indexOf(begin);
    int to = from < 0 ? -1 : source.indexOf(END_MARKER, from);
    if (from < 0 || to < 0) {
      Path fallback = outputDirectory.resolve(methodName + ".txt");
      writeFile(fallback, "// No '" + begin + "' ... '" + END_MARKER + "' block in "
          + OUTPUT_DIR_MARKER + ", so " + methodName + "() was not rewritten.\n"
          + "// Replace its body with:\n\n" + registrationBody(classNamePrefix, fileCount, "    "));
      System.out.println(">>>>> WARNING: no registration markers for " + classNamePrefix + " in "
          + OUTPUT_DIR_MARKER + "; replacement written to " + fallback);
      return;
    }

    int bodyStart = source.indexOf('\n', from) + 1;
    int lineStart = source.lastIndexOf('\n', to) + 1;
    String indent = source.substring(lineStart, to);
    String updated = source.substring(0, bodyStart)
        + registrationBody(classNamePrefix, fileCount, indent) + source.substring(lineStart);
    if (updated.equals(source)) {
      System.out.println(">>>>> " + methodName + "() already registers " + fileCount + " class(es)");
      return;
    }
    Files.writeString(file, updated, StandardCharsets.UTF_8);
    System.out.println(">>>>> Rewrote " + methodName + "() in " + OUTPUT_DIR_MARKER + " for "
        + fileCount + " " + classNamePrefix + "*.java class(es)");
  }

  private static boolean isCleanEnabled() {
    return !"false".equalsIgnoreCase(System.getProperty(PROP_CLEAN));
  }

  private static Path findProjectDirectory() {
    Path start = Paths.get("").toAbsolutePath().normalize();
    for (Path directory = start; directory != null; directory = directory.getParent()) {
      if (isProjectDirectory(directory)) {
        return directory;
      }
      // the repository root contains the project directory of the same name
      Path nested = directory.resolve("symja_android_library");
      if (isProjectDirectory(nested)) {
        return nested;
      }
    }
    Path fallback = Paths.get(System.getProperty("user.home"), "git", "symja_android_library",
        "symja_android_library");
    if (isProjectDirectory(fallback)) {
      return fallback;
    }
    throw new IllegalStateException("Cannot locate the 'symja_android_library' project directory, "
        + "the search started at '" + start + "'. Pass the input file and the output directory as "
        + "program arguments, or set -D" + PROP_INPUT + " and -D" + PROP_OUTPUT_DIR + ".");
  }

  private static boolean isProjectDirectory(Path directory) {
    return Files.isDirectory(resolve(directory, RUBI_INPUT_SEGMENTS))
        && Files.isDirectory(resolve(directory, GENERATED_PACKAGE_SEGMENTS));
  }

  private static Path resolve(Path directory, String[] segments) {
    Path result = directory;
    for (String segment : segments) {
      result = result.resolve(segment);
    }
    return result;
  }

  private static Path argument(String[] args, int index) {
    return args != null && args.length > index ? toPath(args[index]) : null;
  }

  private static Path property(String key) {
    return toPath(System.getProperty(key));
  }

  private static Path toPath(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    try {
      return Paths.get(value.trim());
    } catch (InvalidPathException e) {
      throw new IllegalStateException("Not a valid path: '" + value + "'", e);
    }
  }

  private static Path firstNonNull(Path first, Path second) {
    return first != null ? first : second;
  }
}
