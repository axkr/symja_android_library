package org.matheclipse.core.builtin;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.io.FileSandbox;

/**
 * Working with files and directories: what a script needs to find its own files and what a package
 * manager needs to install them.
 *
 * <p>
 * <code>Directory[]</code> lives on the evaluation engine rather than in the process, because a Java
 * process cannot change its own working directory. Every relative name resolves against it, through
 * {@link FileSandbox}, so <code>SetDirectory</code> means what it says.
 */
public class FileSystemFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.AbsoluteFileName.setEvaluator(new AbsoluteFileName());
        S.CopyDirectory.setEvaluator(new CopyDirectory());
        S.CopyFile.setEvaluator(new CopyFile());
        S.DeleteDirectory.setEvaluator(new DeleteDirectory());
        S.DeleteFile.setEvaluator(new DeleteFile());
        S.Directory.setEvaluator(new Directory());
        S.DirectoryName.setEvaluator(new DirectoryName());
        S.DirectoryQ.setEvaluator(new DirectoryQ());
        S.DynamicLibraryExtension.setEvaluator(new DynamicLibraryExtension());
        S.Environment.setEvaluator(new Environment());
        S.LibraryFunctionLoad.setEvaluator(new LibraryFunctionLoad());
        S.ExpandFileName.setEvaluator(new ExpandFileName());
        S.FileBaseName.setEvaluator(new FileBaseName());
        S.FileByteCount.setEvaluator(new FileByteCount());
        S.FileDate.setEvaluator(new FileDate());
        S.FileExistsQ.setEvaluator(new FileExistsQ());
        S.FileExtension.setEvaluator(new FileExtension());
        S.FileNameDepth.setEvaluator(new FileNameDepth());
        S.FileNames.setEvaluator(new FileNames());
        S.FileNameSplit.setEvaluator(new FileNameSplit());
        S.FileType.setEvaluator(new FileType());
        S.ParentDirectory.setEvaluator(new ParentDirectory());
        S.RenameFile.setEvaluator(new RenameFile());
        S.ResetDirectory.setEvaluator(new ResetDirectory());
        S.SetDirectory.setEvaluator(new SetDirectory());
      }
    }
  }

  /** The path a user-supplied name denotes, or <code>null</code> when the sandbox refuses it. */
  private static Path path(ISymbol symbol, IExpr name, EvalEngine engine) {
    if (!(name instanceof IStringX)) {
      return null;
    }
    return FileSandbox.resolveReadPath(symbol, name.toString(), engine);
  }

  /** Like {@link #path}, for a name that is about to be written to. */
  private static Path writePath(ISymbol symbol, IExpr name, EvalEngine engine) {
    if (!(name instanceof IStringX)) {
      return null;
    }
    return FileSandbox.resolveWritePath(symbol, name.toString(), engine);
  }

  private static class AbsoluteFileName extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path path = path(S.AbsoluteFileName, ast.arg1(), engine);
      if (path == null) {
        return F.NIL;
      }
      if (!Files.exists(path)) {
        // AbsoluteFileName reports a name that does not exist, unlike ExpandFileName
        return Errors.printMessage(S.AbsoluteFileName, "nffil",
            F.list(F.stringx(ast.arg1().toString())), engine);
      }
      try {
        return F.stringx(path.toRealPath().toString());
      } catch (IOException ex) {
        return F.stringx(path.toAbsolutePath().normalize().toString());
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class CopyDirectory extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path from = path(S.CopyDirectory, ast.arg1(), engine);
      Path to = writePath(S.CopyDirectory, ast.arg2(), engine);
      if (from == null || to == null) {
        return F.NIL;
      }
      if (!Files.isDirectory(from)) {
        return Errors.printMessage(S.CopyDirectory, "nodir", F.list(ast.arg1()), engine);
      }
      if (Files.exists(to)) {
        return Errors.printMessage(S.CopyDirectory, "filex", F.list(ast.arg2()), engine);
      }
      try (Stream<Path> walk = Files.walk(from)) {
        for (Path source : walk.collect(java.util.stream.Collectors.toList())) {
          Path target = to.resolve(from.relativize(source).toString());
          if (Files.isDirectory(source)) {
            Files.createDirectories(target);
          } else {
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
          }
        }
        return F.stringx(to.toString());
      } catch (IOException | RuntimeException ex) {
        Errors.printMessage(S.CopyDirectory, ex, engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class CopyFile extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path from = path(S.CopyFile, ast.arg1(), engine);
      Path to = writePath(S.CopyFile, ast.arg2(), engine);
      if (from == null || to == null) {
        return F.NIL;
      }
      boolean overwrite = optionIsTrue(ast, 3, "OverwriteTarget");
      if (Files.exists(to) && !overwrite) {
        return Errors.printMessage(S.CopyFile, "filex", F.list(ast.arg2()), engine);
      }
      try {
        Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        return F.stringx(to.toString());
      } catch (IOException | RuntimeException ex) {
        Errors.printMessage(S.CopyFile, ex, engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }
  }

  private static class DeleteDirectory extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path directory = writePath(S.DeleteDirectory, ast.arg1(), engine);
      if (directory == null) {
        return F.NIL;
      }
      boolean deleteContents = optionIsTrue(ast, 2, "DeleteContents");
      if (!Files.isDirectory(directory)) {
        return Errors.printMessage(S.DeleteDirectory, "nodir", F.list(ast.arg1()), engine);
      }
      try {
        if (deleteContents) {
          try (Stream<Path> walk = Files.walk(directory)) {
            List<Path> paths = walk.sorted(Comparator.reverseOrder())
                .collect(java.util.stream.Collectors.toList());
            for (Path p : paths) {
              Files.deleteIfExists(p);
            }
          }
        } else {
          Files.delete(directory);
        }
        return S.Null;
      } catch (IOException | RuntimeException ex) {
        Errors.printMessage(S.DeleteDirectory, ex, engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class DeleteFile extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        for (int i = 1; i < list.size(); i++) {
          IExpr result = deleteOne(list.get(i), engine);
          if (!result.equals(S.Null)) {
            return result;
          }
        }
        return S.Null;
      }
      return deleteOne(arg1, engine);
    }

    private static IExpr deleteOne(IExpr name, EvalEngine engine) {
      Path file = writePath(S.DeleteFile, name, engine);
      if (file == null) {
        return F.NIL;
      }
      try {
        if (!Files.exists(file)) {
          return Errors.printMessage(S.DeleteFile, "nffil", F.list(name), engine);
        }
        Files.delete(file);
        return S.Null;
      } catch (IOException | RuntimeException ex) {
        Errors.printMessage(S.DeleteFile, ex, engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class Directory extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.stringx(engine.getCurrentDirectory().toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }
  }

  private static class DirectoryName extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      int levels = 1;
      if (ast.isAST2()) {
        levels = ast.arg2().toIntDefault();
        if (levels < 0) {
          return F.NIL;
        }
      }
      Path path = Path.of(ast.arg1().toString());
      for (int i = 0; i < levels; i++) {
        path = path == null ? null : path.getParent();
      }
      return path == null ? F.CEmptyString : F.stringx(path.toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class DirectoryQ extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path path = path(S.DirectoryQ, ast.arg1(), engine);
      return path == null ? F.False : F.booleSymbol(Files.isDirectory(path));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>Internal`DynamicLibraryExtension[]</code>: what a shared library is called on this
   * platform. A package that loads one builds the file name from it.
   */
  private static class DynamicLibraryExtension extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      String operatingSystem =
          System.getProperty("os.name", "").toLowerCase(java.util.Locale.ENGLISH);
      if (operatingSystem.contains("mac") || operatingSystem.contains("darwin")) {
        return F.stringx("dylib");
      }
      if (operatingSystem.contains("win")) {
        return F.stringx("dll");
      }
      return F.stringx("so");
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }
  }

  /**
   * <code>LibraryFunctionLoad[…]</code>: Symja has no LibraryLink, so no shared library ever
   * loads.
   *
   * <p>
   * Answering <code>$Failed</code> rather than staying unevaluated is what lets a package fall
   * back to a Wolfram Language implementation of the same function: the usual shape is
   * <code>If[FailureQ[f = LibraryFunctionLoad[…]], f = Compile[…]]</code>, and an unevaluated
   * <code>LibraryFunctionLoad</code> takes neither branch.
   */
  private static class LibraryFunctionLoad extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr function = ast.size() > 2 ? ast.arg2() : F.CEmptyString;
      // The function `1` was not loaded from the file `2`.
      Errors.printMessage(S.LibraryFunctionLoad, "libload", F.List(function, ast.arg1()), engine);
      return S.$Failed;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }
  }

  private static class Environment extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      String value = System.getenv(ast.arg1().toString());
      return value == null ? S.$Failed : F.stringx(value);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ExpandFileName extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      String name = ast.arg1().toString();
      Path path = Path.of(name);
      if (path.isAbsolute()) {
        return F.stringx(path.normalize().toString());
      }
      return F.stringx(engine.getCurrentDirectory().resolve(path).normalize().toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class FileBaseName extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      String name = fileName(ast.arg1().toString());
      int dot = name.lastIndexOf('.');
      return F.stringx(dot <= 0 ? name : name.substring(0, dot));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class FileByteCount extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path path = path(S.FileByteCount, ast.arg1(), engine);
      if (path == null || !Files.isRegularFile(path)) {
        return Errors.printMessage(S.FileByteCount, "nffil", F.list(ast.arg1()), engine);
      }
      try {
        return F.ZZ(Files.size(path));
      } catch (IOException ex) {
        Errors.printMessage(S.FileByteCount, ex, engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class FileDate extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path path = path(S.FileDate, ast.arg1(), engine);
      if (path == null || !Files.exists(path)) {
        return Errors.printMessage(S.FileDate, "nffil", F.list(ast.arg1()), engine);
      }
      try {
        java.time.LocalDateTime modified = java.time.LocalDateTime.ofInstant(
            Files.getLastModifiedTime(path).toInstant(), java.time.ZoneId.systemDefault());
        return org.matheclipse.core.expression.data.DateObjectExpr.newInstance(modified);
      } catch (IOException ex) {
        Errors.printMessage(S.FileDate, ex, engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class FileExistsQ extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path path = path(S.FileExistsQ, ast.arg1(), engine);
      return path == null ? F.False : F.booleSymbol(Files.exists(path));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class FileExtension extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      String name = fileName(ast.arg1().toString());
      int dot = name.lastIndexOf('.');
      return F.stringx(dot <= 0 ? "" : name.substring(dot + 1));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class FileNameDepth extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      return F.ZZ(splitFileName(ast.arg1().toString()).size());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class FileNameSplit extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!(ast.arg1() instanceof IStringX)) {
        return F.NIL;
      }
      List<String> parts = splitFileName(ast.arg1().toString());
      IASTAppendable result = F.ListAlloc(parts.size());
      for (String part : parts) {
        result.append(F.stringx(part));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>FileNames[pattern]</code>, <code>FileNames[pattern, directories]</code> and
   * <code>FileNames[pattern, directories, depth]</code>: the names a package manager walks a tree
   * with.
   */
  private static class FileNames extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      IExpr pattern = ast.isAST0() ? F.stringx("*") : ast.arg1();
      List<Path> directories = new ArrayList<Path>();
      if (ast.size() > 2) {
        IExpr arg2 = ast.arg2();
        IAST names = arg2.isList() ? (IAST) arg2 : F.list(arg2);
        for (int i = 1; i < names.size(); i++) {
          Path directory = path(S.FileNames, names.get(i), engine);
          if (directory != null) {
            directories.add(directory);
          }
        }
      } else {
        directories.add(FileSandbox.workingDirectory(engine));
      }
      // n means levels 1 to n, {n} exactly level n, {min, max} that range, Infinity the whole tree
      int minDepth = 1;
      int maxDepth = 1;
      if (ast.size() > 3) {
        IExpr arg3 = ast.arg3();
        if (arg3.isList1()) {
          minDepth = maxDepth = levelOf(arg3.first());
        } else if (arg3.isList2()) {
          minDepth = levelOf(arg3.first());
          maxDepth = levelOf(arg3.second());
        } else {
          maxDepth = levelOf(arg3);
        }
        if (minDepth < 1 || maxDepth < minDepth) {
          return F.NIL;
        }
      }

      java.util.regex.Pattern regex;
      try {
        Map<ISymbol, String> groups = new IdentityHashMap<ISymbol, String>();
        regex = IStringX.toRegexPattern(pattern, true, false, ast, groups, engine);
      } catch (ValidateException ve) {
        return Errors.printMessage(S.FileNames, ve, engine);
      }
      if (regex == null) {
        return F.NIL;
      }

      IASTAppendable result = F.ListAlloc();
      for (Path directory : directories) {
        if (!Files.isDirectory(directory)) {
          continue;
        }
        final int from = minDepth;
        try (Stream<Path> walk =
            Files.walk(directory, maxDepth, FileVisitOption.FOLLOW_LINKS)) {
          walk.filter(p -> !p.equals(directory)) //
              .filter(p -> directory.relativize(p).getNameCount() >= from) //
              .filter(p -> {
                Path name = p.getFileName();
                return name != null && regex.matcher(name.toString()).matches();
              }) //
              .sorted() //
              .forEach(p -> result.append(F.stringx(p.toString())));
        } catch (IOException | RuntimeException ex) {
          Errors.printMessage(S.FileNames, ex, engine);
        }
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_3;
    }
  }

  /** One level specification of <code>FileNames</code>, with <code>Infinity</code> for the lot. */
  private static int levelOf(IExpr level) {
    if (level.isInfinity()) {
      return Integer.MAX_VALUE;
    }
    return level.toIntDefault();
  }

  private static class FileType extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path path = path(S.FileType, ast.arg1(), engine);
      if (path == null || !Files.exists(path)) {
        return S.None;
      }
      return Files.isDirectory(path) ? S.Directory : S.File;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class ParentDirectory extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      Path path;
      int levels = 1;
      if (ast.isAST0()) {
        path = engine.getCurrentDirectory();
      } else if (ast.arg1() instanceof IStringX) {
        path = Path.of(ast.arg1().toString());
        if (ast.isAST2()) {
          levels = ast.arg2().toIntDefault();
          if (levels < 0) {
            return F.NIL;
          }
        }
      } else {
        return F.NIL;
      }
      // a relative name has no parent of its own to name, so it is read where it is used
      if (!path.isAbsolute()) {
        path = engine.getCurrentDirectory().resolve(path);
      }
      path = path.normalize();
      for (int i = 0; i < levels; i++) {
        path = path == null ? null : path.getParent();
      }
      return path == null ? F.CEmptyString : F.stringx(path.toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  private static class RenameFile extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path from = path(S.RenameFile, ast.arg1(), engine);
      Path to = writePath(S.RenameFile, ast.arg2(), engine);
      if (from == null || to == null) {
        return F.NIL;
      }
      try {
        Files.move(from, to, StandardCopyOption.REPLACE_EXISTING);
        return F.stringx(to.toString());
      } catch (IOException | RuntimeException ex) {
        Errors.printMessage(S.RenameFile, ex, engine);
        return S.$Failed;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  private static class ResetDirectory extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      java.util.ArrayDeque<Path> stack = engine.getDirectoryStack();
      if (stack.isEmpty()) {
        // ResetDirectory::dtop: Directory stack is empty.
        return Errors.printMessage(S.ResetDirectory, "dtop", F.CEmptyList, engine);
      }
      Path previous = stack.pop();
      engine.setCurrentDirectory(previous);
      return F.stringx(engine.getCurrentDirectory().toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_0;
    }
  }

  private static class SetDirectory extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      Path directory;
      if (ast.isAST0()) {
        String home = System.getProperty("user.home");
        directory = home == null ? engine.getCurrentDirectory() : Path.of(home);
      } else {
        directory = path(S.SetDirectory, ast.arg1(), engine);
      }
      if (directory == null) {
        return F.NIL;
      }
      if (!Files.isDirectory(directory)) {
        // SetDirectory::cdir: Cannot set current directory to `1`.
        return Errors.printMessage(S.SetDirectory, "cdir", F.list(F.stringx(directory.toString())),
            engine);
      }
      engine.getDirectoryStack().push(engine.getCurrentDirectory());
      engine.setCurrentDirectory(directory);
      return F.stringx(engine.getCurrentDirectory().toString());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  /**
   * Is the named option set to <code>True</code> in the arguments from <code>position</code> on?
   *
   * <p>
   * The option is matched by name rather than through {@link OptionArgs}, because
   * <code>DeleteContents</code> and <code>OverwriteTarget</code> are names a caller writes and not
   * symbols the system defines.
   */
  private static boolean optionIsTrue(IAST ast, int position, String name) {
    for (int i = position; i < ast.size(); i++) {
      IExpr argument = ast.get(i);
      if (argument.isRuleAST() && argument.first().toString().equalsIgnoreCase(name)) {
        return argument.second().isTrue();
      }
      if (argument.isList()) {
        IAST list = (IAST) argument;
        for (int j = 1; j < list.size(); j++) {
          IExpr rule = list.get(j);
          if (rule.isRuleAST() && rule.first().toString().equalsIgnoreCase(name)) {
            return rule.second().isTrue();
          }
        }
      }
    }
    return false;
  }

  /** The last segment of a file name, whichever separator was written. */
  private static String fileName(String name) {
    List<String> parts = splitFileName(name);
    return parts.isEmpty() ? "" : parts.get(parts.size() - 1);
  }

  /** A file name split at both separators, so that a Windows name reads on a Unix host too. */
  private static List<String> splitFileName(String name) {
    List<String> parts = new ArrayList<String>();
    StringBuilder part = new StringBuilder();
    for (int i = 0; i < name.length(); i++) {
      char ch = name.charAt(i);
      if (ch == '/' || ch == '\\') {
        if (part.length() > 0 || parts.isEmpty()) {
          parts.add(part.toString());
        }
        part.setLength(0);
      } else {
        part.append(ch);
      }
    }
    if (part.length() > 0) {
      parts.add(part.toString());
    }
    return parts;
  }

  public static void initialize() {
    Initializer.init();
  }

  private FileSystemFunctions() {}
}
