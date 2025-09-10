package org.matheclipse.core.form;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.ICoreFunctionEvaluator;
import org.matheclipse.core.expression.BuiltInSymbol;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.Scanner;
import org.matheclipse.parser.trie.SuggestTree;
import org.matheclipse.parser.trie.SuggestTree.Node;

public class Documentation {

  public static final String GITHUB = "https://github.com/";

  public static final String CORE_POM_PATH =
      "axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/";

  public static final String IO_POM_PATH =
      "axkr/symja_android_library/blob/master/symja_android_library/matheclipse-io/";

  public static final String SRC_PATH = "src/main/java/";

  public static final String RULES_PATH =
      "axkr/symja_android_library/blob/master/symja_android_library/rules/";

  public static final String RULE_SETS_PATH =
      "axkr/symja_android_library/blob/master/symja_android_library/rule_sets/";

  public static String buildDocFilename(String docName) {
    return "doc/" + docName + ".md";
  }

  public static String buildFunctionFilename(String symbolName) {
    return "doc/functions/" + symbolName + ".md";
  }

  public static String buildURL(final Class<?> clazz, int line) {
    String canonicalName = clazz.getCanonicalName();
    String packageName = clazz.getPackage().getName();
    String parentClass = canonicalName.substring(packageName.length() + 1);

    StringBuilder buf = new StringBuilder(512);
    buf.append(Documentation.GITHUB);
    if (packageName.startsWith("org.matheclipse.io")) {
      buf.append(Documentation.IO_POM_PATH);
    } else {
      buf.append(Documentation.CORE_POM_PATH);
    }
    buf.append(Documentation.SRC_PATH);

    int index = parentClass.indexOf('.');
    if (index > 0) {
      parentClass = parentClass.substring(0, index);
    }
    String packagePath = packageName.replace('.', '/');
    buf.append(packagePath);
    buf.append('/');
    buf.append(parentClass);
    buf.append(".java#L");
    buf.append(line);
    return buf.toString();
  }

  public static int extraxtJavadoc(Appendable out, String symbolName) {
    // read markdown file
    String fileName = buildFunctionFilename(symbolName);

    // Get file from resources folder
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    try (BufferedReader f = getResourceReader(classloader, fileName)) {
      if (f != null) {
        String line;
        String shortdesc = null;
        while ((line = f.readLine()) != null) {
          if (line.startsWith("```")) {
            if (shortdesc == null && (line = f.readLine()) != null) {
              shortdesc = line.trim();
            }
            continue;
          }
          if (line.startsWith("### ")) {
            return 0;
          }

          if (line.startsWith("> ")) {
            out.append("\n        /**");
            out.append(" ");
            if (shortdesc != null) {
              out.append(shortdesc);
              out.append(" - ");
            }
            out.append(line.substring(2));
            out.append("\n         * ");
            out.append(
                "@see <a href=\"https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/doc/functions/"
                    + symbolName + ".md\">" + symbolName + " documentation</a>\n");
            out.append("         */");
            return 1;
          }
        }
        return 0;
      }

    } catch (IOException e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
    }
    return -1;
  }

  public static void findDocumentation(Appendable out, String trimmedInput) {
    String name = trimmedInput.substring(1);
    usageDocumentation(out, name);
  }

  public static IExpr findDocumentation(String trimmedInput) {
    StringBuilder out = new StringBuilder();
    String name = trimmedInput.substring(1);
    usageDocumentation(out, name);
    String str = out.toString();
    if (str.length() == 0) {
      return F.Missing(F.stringx("UnknownSymbol"), F.stringx(trimmedInput));
    }
    return F.stringx(str);
  }

  /**
   * Returns the GitHub URL of the <code>built-in-symbol</code> implementation in the
   * <a href="https://github.com/axkr/symja_android_library">Symja GitHub repository</a>.
   *
   * @param builtin the built-in function identifier
   * @return <code>null</code> is no entry was found
   */
  public static String functionURL(ISymbol builtin) {
    int ordinal = builtin.ordinal();
    if (ordinal > 0 && ordinal < ID.LINE_NUMBER_OF_JAVA_CLASS.length) {
      int line = ID.LINE_NUMBER_OF_JAVA_CLASS[ordinal];
      if (line > 0) {
        IEvaluator evaluator = ((IBuiltInSymbol) builtin).getEvaluator();
        if (evaluator != null //
            && evaluator != BuiltInSymbol.DUMMY_EVALUATOR //
            && evaluator != ICoreFunctionEvaluator.ARGS_EVALUATOR) {
          Class<? extends IEvaluator> clazz = evaluator.getClass();
          return buildURL(clazz, line);
        }
      }
    }
    return null;
  }

  public static IAST getAllNames() {
    return F.mapRange(0, AST2Expr.FUNCTION_STRINGS.length, i -> F.$s(AST2Expr.FUNCTION_STRINGS[i]));
  }

  public static List<String> getAutoCompletionList(String namePrefix) {
    List<String> list = new ArrayList<String>();
    if (namePrefix.length() == 0) {
      return list;
    }
    SuggestTree suggestTree = AST2Expr.getSuggestTree();
    namePrefix =
        ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? namePrefix.toLowerCase(Locale.US) : namePrefix;
    Node n = suggestTree.getAutocompleteSuggestions(namePrefix);
    if (n != null) {
      for (int i = 0; i < n.listLength(); i++) {
        list.add(n.getSuggestion(i).getTerm());
      }
    }
    return list;
  }

  public static IAST getDocumentationByPrefix(String name, boolean useAsterisk) {
    if (name.length() == 0) {
      return F.CEmptyList;
    }
    boolean exact = false;
    if (useAsterisk) {
      if (name.charAt(name.length() - 1) == '*') {
        name = name.substring(0, name.length() - 1);
        if (name.length() == 0) {
          return getAllNames();
        }
      } else {
        exact = true;
      }
    } else {
      // if (name.length() == 0) {
      // return getAllNames();
      // }
    }
    return getNamesByPrefix(name, exact);
  }

  /**
   * Get the pure <code>markdown</code> formatted information about the <code>builinFunctionName
   * </code>
   *
   * @param out
   * @param builinFunctionName
   * @return
   */
  public static boolean getMarkdown(Appendable out, String builinFunctionName) {
    ISymbol symbol = F.symbol(builinFunctionName);
    String url = null;
    if (symbol instanceof IBuiltInSymbol) {
      url = Documentation.functionURL(symbol);
    }
    // read markdown file
    String fileName = buildFunctionFilename(builinFunctionName);

    // Get file from resources folder
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    try (BufferedReader f = getResourceReader(classloader, fileName)) {
      if (f != null) {
        String line;
        while ((line = f.readLine()) != null) {
          out.append(line);
          out.append("\n");
        }
        if (url != null) {
          out.append("[Github master](");
          out.append(url);
          out.append(")\n\n");
        }
        return true;
      }
    } catch (IOException e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * Returns a list of {@link IStringX} of the defined symbol names.
   * 
   * @param pattern
   * @param ignoreCase
   * @param ast
   * @param engine
   */
  public static IAST getNamesByPattern(IExpr pattern, boolean ignoreCase, final IAST ast,
      EvalEngine engine) {
    if (pattern.isString()) {
      int indx = pattern.toString().indexOf("`");
      if (indx < 0) {
        pattern = F.$str("System`" + pattern.toString());
      }
    }
    Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
    java.util.regex.Pattern regexPattern =
        IStringX.toRegexPattern(pattern, true, ignoreCase, ast, groups, engine);

    if (regexPattern == null) {
      return F.NIL;
    }

    return Documentation.getNamesByPattern(regexPattern, engine);
  }

  /**
   * Returns a list of {@link IStringX} of the defined symbol names.
   * 
   * @param pattern
   * @param engine
   * @return
   */
  public static IAST getNamesByPattern(java.util.regex.Pattern pattern, EvalEngine engine) {
    ContextPath contextPath = engine.getContextPath();
    IASTAppendable list = F.ListAlloc(31);
    Map<String, Context> contextMap = contextPath.getContextMap();
    for (Map.Entry<String, Context> mapEntry : contextMap.entrySet()) {
      Context context = mapEntry.getValue();
      // avoid java.util.ConcurrentModificationException by creating ArrayList
      for (Map.Entry<String, ISymbol> entry : new ArrayList<>(context.entrySet())) {
        String fullName = context.completeContextName() + entry.getKey();
        java.util.regex.Matcher matcher = pattern.matcher(fullName);
        if (matcher.matches()) {
          if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS && context.equals(Context.SYSTEM)) {
            String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(entry.getValue().getSymbolName());
            if (str != null) {
              list.append(F.$str(str));
              continue;
            }
          }
          ISymbol value = entry.getValue();
          if (context.isGlobal() || context.isSystem()) {
            list.append(F.$str(value.toString()));
          } else {
            list.append(F.$str(fullName));
          }
        }
      }
    }

    for (Context context : contextPath) {
      String completeContextName = context.completeContextName();
      if (!contextMap.containsKey(completeContextName)) {
        for (Map.Entry<String, ISymbol> entry : context.entrySet()) {
          String fullName = completeContextName + entry.getKey();
          java.util.regex.Matcher matcher = pattern.matcher(fullName);
          if (matcher.matches()) {
            if (ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS && context.equals(Context.SYSTEM)) {
              String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(entry.getValue().getSymbolName());
              if (str != null) {
                list.append(F.$str(str));
                continue;
              }
            }
            ISymbol value = entry.getValue();
            if (context.isGlobal() || context.isSystem()) {
              list.append(F.$str(value.toString()));
            } else {
              list.append(F.$str(fullName));
            }
          }
        }
      }
    }
    return list;
  }

  public static IAST getNamesByPrefix(String name, final boolean exact) {
    SuggestTree suggestTree = AST2Expr.getSuggestTree();
    // name = ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS ? name.toLowerCase() : name;
    name = name.toLowerCase(Locale.US);
    final String autocompleteStr = name;
    Node suggestions = suggestTree.getAutocompleteSuggestions(autocompleteStr);
    if (suggestions != null) {
      return F.mapRange(0, suggestions.listLength(), i -> {
        String identifierStr = suggestions.getSuggestion(i).getTerm();
        String str = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(identifierStr);
        if (str != null) {
          identifierStr = str;
        }
        if (exact) {
          if (autocompleteStr.equals(identifierStr.toLowerCase(Locale.US))) {
            return F.$s(identifierStr);
          }
          return F.NIL;
        }
        return F.$s(identifierStr);
      });
    }
    return F.CEmptyList;
  }

  private static BufferedReader getResourceReader(ClassLoader classloader, String resource) {
    InputStream in = classloader.getResourceAsStream(resource);
    return in != null ? new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
        : null;
  }

  /**
   * Returns a list of {@link ISymbol} of the defined symbol names.
   * 
   * @param pattern
   * @param ignoreCase
   * @param ast
   * @param engine
   * @return
   */
  public static IAST getSymbolsByPattern(IExpr pattern, boolean ignoreCase, final IAST ast,
      EvalEngine engine) {
    if (pattern.isString()) {
      int indx = pattern.toString().indexOf("`");
      if (indx < 0) {
        pattern = F.$str("System`" + pattern.toString());
      }
    }
    Map<ISymbol, String> groups = new HashMap<ISymbol, String>();
    java.util.regex.Pattern regexPattern =
        IStringX.toRegexPattern(pattern, true, ignoreCase, ast, groups, engine);

    if (regexPattern == null) {
      return F.NIL;
    }

    return Documentation.getSymbolsByPattern(regexPattern, engine);
  }

  /**
   * Returns a list of {@link ISymbol} of the defined symbol names.
   * 
   * @param pattern
   * @param engine
   * @return
   */
  public static IAST getSymbolsByPattern(java.util.regex.Pattern pattern, EvalEngine engine) {
    ContextPath contextPath = engine.getContextPath();
    IASTAppendable list = F.ListAlloc(31);
    Map<String, Context> contextMap = contextPath.getContextMap();
    for (Map.Entry<String, Context> mapEntry : contextMap.entrySet()) {
      Context context = mapEntry.getValue();
      for (Map.Entry<String, ISymbol> entry : context.entrySet()) {
        String fullName = context.completeContextName() + entry.getKey();
        java.util.regex.Matcher matcher = pattern.matcher(fullName);
        if (matcher.matches()) {
          list.append(entry.getValue());
        }
      }
    }

    for (Context context : contextPath) {
      String completeContextName = context.completeContextName();
      if (!contextMap.containsKey(completeContextName)) {
        for (Map.Entry<String, ISymbol> entry : context.entrySet()) {
          String fullName = completeContextName + entry.getKey();
          java.util.regex.Matcher matcher = pattern.matcher(fullName);
          if (matcher.matches()) {
            list.append(entry.getValue());
          }
        }
      }
    }
    return list;
  }

  /**
   * Load the documentation from resource folder if available and print to output.
   *
   * @param symbolName
   */
  public static boolean printDocumentation(Appendable out, String symbolName) {
    ISymbol symbol = F.symbol(symbolName);
    String url = null;
    if (symbol instanceof IBuiltInSymbol) {
      url = Documentation.functionURL(symbol);
    }
    // read markdown file
    String fileName = buildFunctionFilename(symbolName);

    // Get file from resources folder
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    try (BufferedReader f = getResourceReader(classloader, fileName)) {
      if (f != null) {
        String line;
        boolean emptyLine = false;
        while ((line = f.readLine()) != null) {
          if (line.startsWith("```")) {
            continue;
          }
          if (line.trim().length() == 0) {
            if (emptyLine) {
              continue;
            }
            emptyLine = true;
          } else {
            emptyLine = false;
          }
          out.append(line);
          out.append("\n");
        }

        if (url != null) {
          out.append("[Github master](");
          out.append(url);
          out.append(")\n\n");
        }
        return true;
      }
    } catch (IOException e) {
      if (Config.SHOW_STACKTRACE) {
        e.printStackTrace();
      }
    }
    return false;
  }

  /**
   * Returns the GitHub URL of the <code>built-in-symbol</code> rules definition in the
   * <a href="https://github.com/axkr/symja_android_library">Symja GitHub repository</a>.
   * 
   * @param builtin the built-in function identifier
   * @return <code>null</code> is no entry was found
   */
  public static String rules(ISymbol builtin) {
    int ordinal = builtin.ordinal();
    if (ordinal > 0 && ordinal < ID.LINE_NUMBER_OF_JAVA_CLASS.length) {
      int line = ID.LINE_NUMBER_OF_JAVA_CLASS[ordinal];
      if (line > 0) {
        String symbolName = builtin.toString();
        String userHome = System.getProperty("user.home");
        File ruleFile = new File(userHome
            + "/git/symja_android_library/symja_android_library/rules/" + symbolName + "Rules.m");
        if (ruleFile.exists()) {
          StringBuilder buf = new StringBuilder(512);
          buf.append(GITHUB);
          buf.append(Documentation.RULES_PATH);
          buf.append(symbolName);
          buf.append("Rules.m");
          return buf.toString();
        } else {
          File ruleSetsFile =
              new File(userHome + "/git/symja_android_library/symja_android_library/rule_sets/"
                  + symbolName + "Rules.m");
          if (ruleSetsFile.exists()) {
            StringBuilder buf = new StringBuilder(512);
            buf.append(GITHUB);
            buf.append(Documentation.RULE_SETS_PATH);
            buf.append(symbolName);
            buf.append("Rules.m");
            return buf.toString();
          }
        }
      }
    }
    return null;
  }

  public static void usageDocumentation(Appendable out, String name) {
    try {
      if (Scanner.isIdentifier(name)) {
        ISymbol symbol = F.symbol(name);
        IExpr temp = symbol.evalMessage("usage");
        if (temp.isPresent()) {
          out.append(temp.toString());
        }
      }
      IAST list = Documentation.getDocumentationByPrefix(name, true);

      if (list.size() > 2) {
        for (int i = 1; i < list.size(); i++) {
          out.append(list.get(i).toString());
          if (i != list.argSize()) {
            out.append(", ");
          }
        }
        out.append("\n");
      }
      if (list.size() == 2) {
        Documentation.printDocumentation(out, list.arg1().toString());
      } else if (list.isEmpty() && (name.equals("C") || name.equals("D") || name.equals("E")
          || name.equals("I") || name.equals("N"))) {
        Documentation.printDocumentation(out, name);
      }
    } catch (IOException e) {
    }
  }
}
