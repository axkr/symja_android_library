package org.matheclipse.core.preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.matheclipse.core.builtin.SourceCodeFunctions;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.ISymbol;
import com.google.common.base.Charsets;
import com.google.common.io.CharSink;
import com.google.common.io.Files;

/** Add a github link to the Markdown documentation file of a Symja function. */
public class MarkdownGithubLinkPreprocessor {

  public MarkdownGithubLinkPreprocessor() {}

  public static String parseFile(File file) {
    try {
      final BufferedReader f = new BufferedReader(new FileReader(file));
      final StringBuilder buff = new StringBuilder(1024);
      String line;
      while ((line = f.readLine()) != null) {
        buff.append(line);
        buff.append('\n');
      }
      f.close();
      String inputString = buff.toString();
      return inputString;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Add a github link to the implementation of a Symja function.
   *
   * @param sourceLocation the source location of the <code>*.md</code> files
   * @param targetLocation the target location of the <code>*.md</code> files
   */
  public static void generateFunctionStrings(final File sourceLocation, File targetLocation) {
    if (sourceLocation.exists()) {
      // Get the list of the files contained in the source location
      final String[] files = sourceLocation.list();
      if (files != null) {
        for (int i = 0; i < files.length; i++) {
          if (files[i].endsWith(".md")) { // only markdown files
            File sourceFile = new File(sourceLocation, files[i]);

            String functionName = files[i].substring(0, files[i].length() - 3);
            // if (!functionName.equals("Laplacian")) {
            // continue;
            // }
            String identifier = F.symbolNameNormalized(functionName);
            ISymbol symbol = Context.SYSTEM.get(identifier);
            if (symbol != null) {
              String status = SourceCodeFunctions.statusAsString(symbol);
              String functionURL = Documentation.functionURL(symbol);

              if (functionURL != null) {
                String rulesURL = Documentation.rules(symbol);
                try {
                  System.out.println(sourceFile.toString());
                  List<String> result = Files.readLines(sourceFile, Charsets.UTF_8);
                  int index = -1;

                  for (int j = 0; j < result.size(); j++) {
                    if (result.get(j).startsWith("### Implementation status")) {
                      index = j;
                      break;
                    }
                  }
                  if (index >= 0) {
                    // remove the rest of *.md document
                    int end = index - 1;
                    while (end < result.size()) {
                      result.remove(result.size() - 1);
                    }
                  }
                  if (status != null) {
                    result.add("");
                    result.add("### Implementation status");
                    result.add("");
                    result.add(status);
                  }

                  index = -1;
                  for (int j = 0; j < result.size(); j++) {
                    if (result.get(j).startsWith("### Github")) {
                      index = j;
                      break;
                    }
                  }
                  if (index >= 0) {
                    // remove the rest of *.md document
                    int end = index;
                    while (end < result.size()) {
                      result.remove(result.size() - 1);
                    }
                  }
                  result.add("");
                  // lines of new github links
                  result.add("### Github");
                  result.add("");
                  result.add("* [Implementation of " + functionName + "](" + functionURL + ") ");
                  if (rulesURL != null) {
                    result.add("");
                    result.add("* [Rule definitions of " + functionName + "](" + rulesURL + ") ");
                  }

                  // write target file
                  File targetFile = new File(targetLocation, functionName + ".md");
                  CharSink sink = Files.asCharSink(targetFile, Charsets.UTF_8);
                  sink.writeLines(result, "\n");

                } catch (IOException e) {
                  e.printStackTrace();
                }
              }
            }
          }
        }
      }
    }
  }

  /** Add a github link to the implementation of a Symja function. */
  public static void main(final String[] args) {
    F.initSymja();

    System.out.println("Append Github link to the Symja functions markdown file.");

    String userHome = System.getProperty("user.home");
    File sourceLocation =
        new File(userHome + "\\git\\symja_android_library\\symja_android_library\\doc\\functions");
    File targetLocation =
        new File(userHome + "\\git\\symja_android_library\\symja_android_library\\doc\\functions");
    generateFunctionStrings(sourceLocation, targetLocation);
  }
}
