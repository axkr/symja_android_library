package org.matheclipse.core.builtin;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.io.FileSandbox;
import org.matheclipse.core.io.paclet.PacletInfo;
import org.matheclipse.core.io.paclet.PacletRegistry;

/**
 * Paclets: the directories an application says its Wolfram Language code lives in.
 *
 * <p>
 * A notebook or an application starts by loading its own directory -
 * <code>PacletDirectoryLoad[Directory[]]</code> - and everything after that is
 * <code>Needs["Some`Context`"]</code>. Without the registry a context has no file to be read from,
 * so this is what makes a package-based application loadable at all.
 */
public class PacletFunctions {

  private static class Initializer {

    private static void init() {
      if (!Config.FUZZY_PARSER) {
        S.PacletDirectoryLoad.setEvaluator(new PacletDirectoryLoad());
        S.PacletDirectoryUnload.setEvaluator(new PacletDirectoryUnload());
        S.PacletFind.setEvaluator(new PacletFind());
        S.PacletObject.setEvaluator(new PacletObject());
        S.LoadWolframLanguageCode.setEvaluator(new LoadWolframLanguageCode());
      }
    }
  }

  /**
   * <code>PacletDirectoryLoad[dir]</code>, <code>PacletDirectoryLoad[{dir, …}]</code>: read the
   * paclets in those directories. With no argument it answers the directories already loaded, which
   * is how <code>PacletDirectoryUnload /@ PacletDirectoryLoad[]</code> starts from nothing.
   */
  private static class PacletDirectoryLoad extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      if (ast.isAST0()) {
        return directoryList(PacletRegistry.directories());
      }
      IExpr arg1 = ast.arg1();
      IAST names = arg1.isList() ? (IAST) arg1 : F.list(arg1);
      List<Path> loaded = new ArrayList<Path>();
      for (int i = 1; i < names.size(); i++) {
        if (!names.get(i).isString()) {
          continue;
        }
        Path directory =
            FileSandbox.resolveReadPath(S.PacletDirectoryLoad, names.get(i).toString(), engine);
        Path key = directory == null ? null : PacletRegistry.load(directory, engine);
        if (key == null) {
          // `1` is not a directory.
          Errors.printMessage(S.PacletDirectoryLoad, "nodir", F.list(names.get(i)), engine);
        } else {
          loaded.add(key);
        }
      }
      // the whole list of loaded directories, as in the Wolfram Language
      return directoryList(PacletRegistry.directories());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_1;
    }
  }

  private static class PacletDirectoryUnload extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IAST names = arg1.isList() ? (IAST) arg1 : F.list(arg1);
      for (int i = 1; i < names.size(); i++) {
        if (names.get(i).isString()) {
          PacletRegistry.unload(Path.of(names.get(i).toString()));
        }
      }
      return directoryList(PacletRegistry.directories());
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /** <code>PacletFind["Name"]</code>: the loaded paclets of that name. */
  private static class PacletFind extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      String name = ast.isAST0() ? "*" : ast.arg1().toString();
      List<PacletInfo> found = PacletRegistry.find(name);
      IASTAppendable result = F.ListAlloc(found.size());
      for (PacletInfo paclet : found) {
        result.append(F.unaryAST1(S.PacletObject, F.assoc(F.List( //
            F.Rule(F.stringx("Name"), F.stringx(paclet.name() == null ? "" : paclet.name())), //
            F.Rule(F.stringx("Version"),
                F.stringx(paclet.version() == null ? "" : paclet.version())), //
            F.Rule(F.stringx("Location"), F.stringx(paclet.directory().toString()))))));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_0_2;
    }
  }

  /**
   * <code>PacletObject[&lt;|…|&gt;]["Name"]</code>: a paclet answers what it knows about itself.
   *
   * <p>
   * The evaluator is reached for the whole <code>PacletObject[…]["Name"]</code> because dispatch is
   * by the head's head, so this is where the property lookup belongs.
   */
  private static class PacletObject extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr head = ast.head();
      if (head.isAST(S.PacletObject, 2) && ast.isAST1()) {
        IExpr fields = head.first();
        if (fields.isAssociation()) {
          IExpr value =
              ((org.matheclipse.core.interfaces.IAssociation) fields).getValue(ast.arg1());
          return value == null ? F.NIL : value;
        }
      }
      return F.NIL;
    }
  }

  /**
   * <code>PacletManager`Package`loadWolframLanguageCode[name, context, root, file, opts]</code>,
   * which is how a paclet's loader file asks for the paclet's own code.
   *
   * <p>
   * Everything is read at once. The Wolfram Language uses this to arrange for symbols to be loaded
   * when they are first touched; Symja has no autoloading, and reading the file now is the same
   * thing to anyone who then uses those symbols.
   */
  private static class LoadWolframLanguageCode extends AbstractEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (!Config.isFileSystemEnabled(engine)) {
        return F.NIL;
      }
      if (ast.size() < 5) {
        return F.NIL;
      }
      IExpr root = ast.arg3();
      IExpr fileName = ast.arg4();
      if (!root.isString() || !fileName.isString()) {
        return F.NIL;
      }
      return engine.evaluate(F.Get(F.FileNameJoin(F.list(root, fileName))));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }
  }

  private static IAST directoryList(List<Path> directories) {
    IASTAppendable result = F.ListAlloc(directories.size());
    for (Path directory : directories) {
      result.append(F.stringx(directory.toString()));
    }
    return result;
  }

  public static void initialize() {
    Initializer.init();
  }

  private PacletFunctions() {}
}
