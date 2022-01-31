package org.matheclipse.core.doctools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import org.matheclipse.core.builtin.Arithmetic;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.reflection.system.NIntegrate;

public class Github2 {
  static final String BASE_POM_PATH = "..\\symja_android_library\\matheclipse-core\\";
  static final String BASE_SRC_PATH = "src\\main\\java\\";

  public static void main(final String[] args) {
    String fileName = buildFileNameL(Arithmetic.Times.class);
    File sourceLocation = new File(fileName);
    // File sourceLocation = new File(
    //
    // "..\\symja_android_library\\matheclipse-core\\src\\main\\java\\org\\matheclipse\\core\\builtin\\Arithmetic.java");
    int lineCounter = parseFileToList(sourceLocation, "Times");
    System.out.println(lineCounter);
  }

  public static int parseFileToList(File file, String search) {
    String s1 = "class " + search + " extends";
    String s2 = "class " + search + " implements";
    try {
      final BufferedReader f = new BufferedReader(new FileReader(file));
      String line;
      int lineCounter = 0;
      while ((line = f.readLine()) != null) {
        lineCounter++;
        int index = line.indexOf(search, 0);
        if (index > 0) {
          index = line.indexOf(s1, 0);
          if (index > 0) {
            f.close();
            return lineCounter;
          }
          index = line.indexOf(s2, 0);
          if (index > 0) {
            f.close();
            return lineCounter;
          }
        }
      }
      f.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return -1;
  }

  public static String buildFileNameL(final Class<?> clazz) {
    StringBuilder buf = new StringBuilder(512);
    buf.append(BASE_POM_PATH);
    buf.append(BASE_SRC_PATH);
    String canonicalName = clazz.getCanonicalName();
    String packageName = clazz.getPackage().getName();
    String parentClass = canonicalName.substring(packageName.length() + 1);
    int index = parentClass.indexOf('.');
    if (index > 0) {
      parentClass = parentClass.substring(0, index);
    }
    String packagePath = packageName.replace('.', '\\');
    buf.append(packagePath);
    buf.append('\\');
    buf.append(parentClass);
    buf.append(".java");
    return buf.toString();
  }
}
