package org.matheclipse.core.form;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.SourceCodeFunctions;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.Scanner;

public class Documentation {
  private static final Logger LOGGER = LogManager.getLogger();

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
      url = SourceCodeFunctions.functionURL(symbol);
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
      LOGGER.error("Documentation.getMarkdown() failed", e);
    }
    return false;
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

  public static void usageDocumentation(Appendable out, String name) {
    try {
      if (Scanner.isIdentifier(name)) {
        ISymbol symbol = F.symbol(name);
        IExpr temp = symbol.evalMessage("usage");
        if (temp.isPresent()) {
          out.append(temp.toString());
        }
      }
      IAST list = IOFunctions.getNamesByPrefix(name);

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

  /**
   * Load the documentation from resource folder if available and print to output.
   *
   * @param symbolName
   */
  public static boolean printDocumentation(Appendable out, String symbolName) {
    ISymbol symbol = F.symbol(symbolName);
    String url = null;
    if (symbol instanceof IBuiltInSymbol) {
      url = SourceCodeFunctions.functionURL(symbol);
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
      LOGGER.error("Documentation.printDocumentation() failed", e);
    }
    return false;
  }

  public static String buildFunctionFilename(String symbolName) {
    return "doc/functions/" + symbolName + ".md";
  }

  public static String buildDocFilename(String docName) {
    return "doc/" + docName + ".md";
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
                    + symbolName + ".md\">" + symbolName + " documentation</a>");
            out.append(" */");
            return 1;
          }
        }
        return 0;
      }

    } catch (IOException e) {
      LOGGER.error("Documentation.extraxtJavadoc() failed", e);
    }
    return -1;
  }

  private static BufferedReader getResourceReader(ClassLoader classloader, String resource) {
    InputStream in = classloader.getResourceAsStream(resource);
    return in != null ? new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
        : null;
  }
}
