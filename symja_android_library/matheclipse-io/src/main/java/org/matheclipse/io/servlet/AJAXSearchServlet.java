package org.matheclipse.io.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IAST;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AJAXSearchServlet extends HttpServlet {
  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

  /** */
  private static final long serialVersionUID = -7668302968904993646L;

  public static int APPLET_NUMBER = 1;

  public static final String UTF8 = "utf-8";

  public static final String EVAL_ENGINE = EvalEngine.class.getName();

  public static boolean INITIALIZED = false;

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    doPost(req, res);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
    res.setContentType("text/html; charset=UTF-8");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("Cache-Control", "no-cache");
    PrintWriter out = res.getWriter();
    try {
      String name = "query";
      String value = req.getParameter(name);
      if (value == null) {
        out.println(createJSONDocString("No input expression posted!"));
        return;
      }
      StringBuilder markdownBuf = new StringBuilder(1024);
      findDocumentation(markdownBuf, value);
      String markdownStr = markdownBuf.toString().trim();
      if (markdownStr.length() > 0) {
        String html = AJAXDocServlet.generateHTMLString(markdownBuf.toString());
        StringBuilder htmlBuf = new StringBuilder(1024);
        htmlBuf.append("<div id=\"docContent\">\n");
        htmlBuf.append(html);
        htmlBuf.append("\n</div>");
        out.println(createJSONDocString(htmlBuf.toString()));
      } else {
        out.println(
            createJSONDocString(
                "<p>Insert a keyword and append a '*' to search for keywords. Example: <b>Int*</b>.</p>"));
      }
    } catch (Exception e) {
      // ...
    }
  }

  private static void findDocumentation(Appendable out, String trimmedInput) {
    String name = trimmedInput; // .substring(1);
    IAST list = IOFunctions.getNamesByPrefix(name);
    try {
      if (list.size() != 2) {
        for (int i = 1; i < list.size(); i++) {
          final String functionName = list.get(i).toString();

          ClassLoader classloader = Thread.currentThread().getContextClassLoader();
          InputStream is = classloader.getResourceAsStream("doc/functions/" + functionName + ".md");
          if (is != null) {
            out.append("[");
            out.append(functionName);
            out.append("](functions/");
            out.append(functionName);
            out.append(".md)");
            if (i != list.size() - 1) {
              out.append(", ");
            }
            is.close();
          }
        }
      }
      out.append("\n");
      if (list.size() == 2) {
        printMarkdown(out, list.get(1).toString());
      } else if (list.size() == 1 && name.length() == 1) {
        printMarkdown(out, name);
      }
    } catch (IOException e) {
    }
  }

  //  public static String generateHTMLString(final String markdownStr) {
  //    Set<Extension> EXTENSIONS = Collections.singleton(TablesExtension.create());
  //    Parser parser = Parser.builder().extensions(EXTENSIONS).build();
  //    Node document = parser.parse(markdownStr);
  //    HtmlRenderer renderer = HtmlRenderer.builder().extensions(EXTENSIONS).build();
  //    return renderer.render(document);
  //  }

  public static void printMarkdown(Appendable out, String symbolName) {
    // read markdown file
    String fileName = Documentation.buildFunctionFilename(symbolName);

    // Get file from resources folder
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    try {
      InputStream is = classloader.getResourceAsStream(fileName);
      if (is != null) {
        final BufferedReader f = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        String line;
        while ((line = f.readLine()) != null) {
          out.append(line);
          out.append("\n");
        }
        f.close();
        is.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static String createJSONDocString(String str) {
    ObjectNode outJSON = JSON_OBJECT_MAPPER.createObjectNode();
    outJSON.put("content", str);
    return outJSON.toString();
    //    JSONObject outJSON = new JSONObject();
    //    outJSON.put("content", str);
    //    return JSONValue.toJSONString(outJSON);
  }
}
