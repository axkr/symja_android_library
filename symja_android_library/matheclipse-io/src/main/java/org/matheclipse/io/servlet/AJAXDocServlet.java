package org.matheclipse.io.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.management.RuntimeErrorException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
import org.matheclipse.core.builtin.SourceCodeFunctions;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.FEConfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AJAXDocServlet extends HttpServlet {
  public static final String FUNCTIONS_PREFIX1 = "/functions/";
  public static final String FUNCTIONS_PREFIX2 = "functions/";

  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

  static class DocNodeRenderer extends CoreHtmlNodeRenderer {
    private final HtmlWriter html;

    public DocNodeRenderer(HtmlNodeRendererContext context) {
      super(context);
      this.html = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
      // Return the node types we want to use this renderer for.
      Set<Class<? extends Node>> set = new HashSet<Class<? extends Node>>();
      set.add(FencedCodeBlock.class);
      set.add(Link.class);
      return set;
    }

    @Override
    public void render(Node node) {
      if (node instanceof FencedCodeBlock) {
        fencedCodeBlock((FencedCodeBlock) node);
      } else if (node instanceof Link) {
        link((Link) node);
      } else {
        IndentedCodeBlock codeBlock = (IndentedCodeBlock) node;
        html.line();
        html.tag("pre");
        html.text(codeBlock.getLiteral());
        html.tag("/pre");
        html.line();
      }
    }

    public void fencedCodeBlock(FencedCodeBlock fencedCodeBlock) {
      //    	System.out.println(fencedCodeBlock.getLiteral() );
      String literal = fencedCodeBlock.getLiteral();
      final String code = literal.trim();
      if (code.contains(">> ")) {
        int lastIndex = 0;
        int index = 0;
        html.tag("pre");
        EvalEngine engine = new EvalEngine("", 256, 256, System.out, System.err, true);
        while (index >= 0) {
          index = code.indexOf(">> ", index);
          if (index >= 0) {
            if (index == 0 || code.charAt(index - 1) == '\n') {
              int endOfLine = code.indexOf("\n", index);
              if (endOfLine <= index + 3) {
                endOfLine = code.length();
              }
              String exampleCommand = code.substring(index + 3, endOfLine);
              if (!FEConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
                // Convert documentation examples from Symja to MMA syntax
                try {
                  ExprParser parser = new ExprParser(engine, true);
                  IExpr expr = parser.parse(exampleCommand);
                  if (expr != null) {
                    exampleCommand = expr.toMMA().trim();
                  }
                } catch (RuntimeErrorException rex) {
                  //
                }
              }
              final String jsCode = StringEscapeUtils.escapeEcmaScript(exampleCommand);

              html.text(code.substring(lastIndex, index + 3));
              Map<String, String> attrs = new HashMap<>();
              attrs.put("href", "javascript:setQueries(['" + jsCode + "']);");
              html.tag("a", attrs);
              html.text(exampleCommand);
              html.tag("/a");

              lastIndex = endOfLine;
              index = endOfLine + 1;
            } else {
              index++;
            }
          }
        }
        if (lastIndex < code.length()) {
          html.text(code.substring(lastIndex, code.length()));
        }
        html.tag("/pre");
      } else {
        visit(fencedCodeBlock);
        //        html.text(code.substring(0, code.length()));
      }
    }

    public void link(Link link) {
      String destination = link.getDestination();
      int index = destination.indexOf(".md");
      if (index > 0) {
        String functionName = destination.substring(0, index);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try {
          InputStream is = classloader.getResourceAsStream("doc/functions/" + destination);
          if (is != null) {
            destination = "javascript:loadDoc('/functions/" + functionName + "')";
          } else {
            destination = "javascript:loadDoc('/" + functionName + "')";
          }
          link.setDestination(destination);

          Map<String, String> attrs = new HashMap<>();
          attrs.put("href", destination);
          html.tag("a", attrs);
          //        html.text(link.getTitle());
          if (link.getFirstChild() != null) {
            super.render(link.getFirstChild());
          }
          html.tag("/a");
          if (is != null) {
            is.close();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        visit(link);
      }
    }
  }

  /** */
  private static final long serialVersionUID = -7389567393700726482L;

  public void doGet(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    doPost(req, res);
  }

  protected void doPost(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException {
    res.setContentType("text/html; charset=UTF-8");
    res.setCharacterEncoding("UTF-8");
    res.setHeader("Cache-Control", "no-cache");
    PrintWriter out = res.getWriter();
    try {
      String value = "index";
      String pathInfo = req.getPathInfo();
      if (pathInfo != null) {
        int pos = pathInfo.lastIndexOf('/');
        if (pos == 0 && pathInfo.length() > 1) {
          value = pathInfo.substring(pos + 1).trim();
        } else if (pathInfo.startsWith("/functions/")) {
          value = pathInfo.substring(1);
        }
      }

      StringBuilder markdownBuf = new StringBuilder(1024);
      printMarkdown(markdownBuf, value);
      String markdownStr = markdownBuf.toString().trim();
      if (markdownStr.length() > 0) {
        String html = generateHTMLString(markdownBuf.toString());
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
      return;
    } catch (Exception e) {
      // ...
    }
  }

  public static String generateHTMLString(final String markdownStr) {
    Set<Extension> EXTENSIONS = Collections.singleton(TablesExtension.create());
    Parser parser = Parser.builder().extensions(EXTENSIONS).build();
    Node document = parser.parse(markdownStr);

    HtmlRenderer renderer =
        HtmlRenderer.builder()
            .nodeRendererFactory(
                new HtmlNodeRendererFactory() {
                  public NodeRenderer create(HtmlNodeRendererContext context) {
                    return new DocNodeRenderer(context);
                  }
                })
            .build();
    return renderer.render(document);
  }

  public static void printMarkdown(Appendable out, String docName) {
    // read markdown file
    String fileName = Documentation.buildDocFilename(docName);

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
        String functionName = docName;
        if (docName.startsWith(FUNCTIONS_PREFIX1)) {
          functionName = docName.substring(FUNCTIONS_PREFIX1.length());
        } else if (docName.startsWith(FUNCTIONS_PREFIX2)) {
          functionName = docName.substring(FUNCTIONS_PREFIX2.length());
        }
        String identifier = F.symbolNameNormalized(functionName);
        ISymbol symbol = Context.SYSTEM.get(identifier);
        if (symbol != null) {
          String functionURL = SourceCodeFunctions.functionURL(symbol);
          if (functionURL != null) {

            out.append("\n\n### Github");
            out.append("\n\n* [Implementation of ");
            out.append(functionName);
            out.append("](");
            out.append(functionURL);
            out.append(") ");
          }
          out.append("\n\n [&larr; Function reference](99-function-reference.md) ");
        } else {
          if (!docName.equals("index")) {
            // jump back to Main documentation page
            out.append("\n\n [&larr; Main](index.md) ");
          }
        }
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
