package org.matheclipse.io.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.RuntimeErrorException;
import org.apache.commons.text.StringEscapeUtils;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.output.WolframFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.ParserConfig;
import org.matheclipse.parser.client.SyntaxError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AJAXDocServlet extends HttpServlet {
  private static final Cache<String, String> JSON_DOCS_CACHE =
      Caffeine.newBuilder().maximumSize(100).build();

  private static final long serialVersionUID = 4636252666864898484L;

  public static final String FUNCTIONS_PREFIX1 = "/functions/";
  public static final String FUNCTIONS_PREFIX2 = "functions/";

  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

  /** A TeX node containing text and other inline nodes as children. */
  private static class TeX extends CustomNode implements Delimited {

    private static final String DELIMITER = "$";

    @Override
    public String getOpeningDelimiter() {
      return DELIMITER;
    }

    @Override
    public String getClosingDelimiter() {
      return DELIMITER;
    }
  }

  private static class TeXDelimiterProcessor implements DelimiterProcessor {

    @Override
    public char getOpeningCharacter() {
      return '$';
    }

    @Override
    public char getClosingCharacter() {
      return '$';
    }

    @Override
    public int getMinLength() {
      return 2;
    }

    @Override
    public int process(DelimiterRun opener, DelimiterRun closer) {
      if (opener.length() >= 2 && closer.length() >= 2) {
        // Use exactly two delimiters even if we have more, and don't care about internal
        // openers/closers.
        return 2;
      } else {
        return 0;
      }
    }
  }

  private static class TeXExtension implements Parser.ParserExtension {

    private TeXExtension() {}

    public static Extension create() {
      return new TeXExtension();
    }

    @Override
    public void extend(Parser.Builder parserBuilder) {
      parserBuilder.customDelimiterProcessor(new TeXDelimiterProcessor());
    }
  }

  static class DocNodeRenderer extends CoreHtmlNodeRenderer {
    private final HtmlWriter html;

    // private boolean inHeader=false;
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
      set.add(TeX.class);
      return set;
    }

    @Override
    public void render(Node node) {
      if (node instanceof FencedCodeBlock) {
        fencedCodeBlock((FencedCodeBlock) node);
      } else if (node instanceof Link) {
        link((Link) node);
      } else if (node instanceof TeX) {
        tex((TeX) node);
      } else {
        IndentedCodeBlock codeBlock = (IndentedCodeBlock) node;
        html.line();
        html.tag("pre");
        html.text(codeBlock.getLiteral());
        html.tag("/pre");
        html.line();
      }
    }

    private void fencedCodeBlock(FencedCodeBlock fencedCodeBlock) {

      String literal = fencedCodeBlock.getLiteral();
      WolframFormFactory wolframForm = WolframFormFactory.get();
      final String code = literal.trim();
      if (code.contains(">> ")) {
        // try {
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
              if (!ParserConfig.PARSER_USE_LOWERCASE_SYMBOLS) {
                // Convert documentation examples from Symja to MMA syntax
                try {
                  ExprParser parser = new ExprParser(engine, true);
                  IExpr expr = parser.parse(exampleCommand);
                  if (expr != null) {
                    exampleCommand = wolframForm.toString(expr); // expr.toMMA().trim();
                  }
                } catch (SyntaxError | RuntimeErrorException e) {
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
        // } catch (RuntimeException rex) {
        // rex.printStackTrace();
        // visit(fencedCodeBlock);
        // }
      } else {
        visit(fencedCodeBlock);
        // html.text(code.substring(0, code.length()));
      }
    }

    private void link(Link link) {
      String destination = link.getDestination();
      int index = destination.indexOf(".md");
      if (index > 0) {
        String functionName = destination.substring(0, index);
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        try (InputStream is = classloader.getResourceAsStream("doc/functions/" + destination)) {
          if (is != null) {
            destination = "javascript:loadDoc('/functions/" + functionName + "')";
          } else {
            destination = "javascript:loadDoc('/" + functionName + "')";
          }
          link.setDestination(destination);

          Map<String, String> attrs = new HashMap<>();
          attrs.put("href", destination);
          html.tag("a", attrs);
          // html.text(link.getTitle());
          if (link.getFirstChild() != null) {
            super.render(link.getFirstChild());
          }
          html.tag("/a");
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        visit(link);
      }
    }

    private void tex(TeX teXNode) {
      Text text = (Text) teXNode.getFirstChild();
      html.raw(text.getLiteral());
      // StringBuilder buf = new StringBuilder();
      // buf.append(
      // "<math xmlns=\"http://www.w3.org/1998/Math/MathML\"
      // xmlns=\"http://www.w3.org/1999/xhtml\">\n"
      // + " <mstyle displaystyle=\"true\" mathvariant=\"sans-serif\">\n");
      // buf.append(text.getLiteral());
      // buf.append("</mstyle>\n" + "</math>");
      // html.raw(buf.toString());
    }

    // @Override
    // public void visit(CustomBlock customBlock) {
    // if (customBlock instanceof TableBlock) {
    // TableBlock tableBlock = (TableBlock) customBlock;
    // visitTableBlock(tableBlock);
    // } else {
    // visitChildren(customBlock);
    // }
    // }
    //
    // @Override
    // public void visit(CustomNode customNode) {
    // if (customNode instanceof TableHead) {
    // inHeader = true;
    // visitChildren(customNode);
    //
    // } else if (customNode instanceof TableBody) {
    // inHeader = false;
    // visitChildren(customNode);
    //
    // } else if (customNode instanceof org.commonmark.ext.gfm.tables.TableRow) {
    // visitChildren(customNode);
    // } else if (customNode instanceof TableCell) {
    // TableCell cell = (TableCell) customNode;
    // visitChildren(cell);
    // // if (inHeader) {
    // //
    // // }
    // } else
    // if (customNode instanceof TeX) {
    // visitTeXNode((TeX) customNode);
    // } else {
    // super.visit(customNode);
    // }
    // }
    // private void visitTableBlock(TableBlock tableBlock) {
    // try {
    // visitChildren(tableBlock);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

  }

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

      String jsonStr = JSON_DOCS_CACHE.getIfPresent(value);
      if (jsonStr != null) {
        out.println(jsonStr);
      } else {
        StringBuilder markdownBuf = new StringBuilder(1024);
        printMarkdown(markdownBuf, value);
        String markdownStr = markdownBuf.toString().trim();
        if (markdownStr.length() > 0) {
          String html = generateHTMLString(markdownBuf.toString());
          StringBuilder htmlBuf = new StringBuilder(1024);
          htmlBuf.append("<div id=\"docContent\">\n");
          htmlBuf.append("<div id=\"mjax\">\n");
          htmlBuf.append(html);
          htmlBuf.append("\n</div>");
          // see https://docs.mathjax.org/en/v2.7-latest/advanced/typeset.html -
          // Modifying Math on the Page
          htmlBuf.append(
              "<script type=\"text/javascript\">MathJax.Hub.Queue(['Typeset',MathJax.Hub,'mjax']); </script>");
          htmlBuf.append("\n</div>");

          jsonStr = createJSONDocString(htmlBuf.toString());
          JSON_DOCS_CACHE.put(value, jsonStr);
          out.println(jsonStr);
        } else {
          out.println(createJSONDocString(
              "<p>Insert a keyword and append a '*' to search for keywords. Example: <b>Int*</b>.</p>"));
        }
      }
    } catch (Exception e) {
      // ...
    }
  }

  public static String generateHTMLString(final String markdownStr) {
    List<Extension> EXTENSIONS = Arrays.asList( //
        TeXExtension.create(), TablesExtension.create());
    Parser parser = Parser.builder() //
        .extensions(EXTENSIONS).build();
    Node document = parser.parse(markdownStr);

    HtmlRenderer renderer = HtmlRenderer.builder() //
        .extensions(EXTENSIONS).nodeRendererFactory(new HtmlNodeRendererFactory() {
          @Override
          public NodeRenderer create(HtmlNodeRendererContext context) {
            return new DocNodeRenderer(context);
          }
        }).build();
    return renderer.render(document);
  }

  public static void printMarkdown(Appendable out, String docName) {
    // read markdown file
    String fileName = Documentation.buildDocFilename(docName);

    // Get file from resources folder
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    URL file = classloader.getResource(fileName);
    if (file != null) {
      // jump back to Main documentation page
      try (BufferedReader f =
          new BufferedReader(new InputStreamReader(file.openStream(), StandardCharsets.UTF_8))) {
        out.append("\n\n [&larr; Main](index.md)\n");
        String line;
        while ((line = f.readLine()) != null) {
          out.append(line);
          out.append("\n");
        }
        String functionName = docName;
        if (docName.startsWith(FUNCTIONS_PREFIX1)) {
          functionName = docName.substring(FUNCTIONS_PREFIX1.length());
        } else if (docName.startsWith(FUNCTIONS_PREFIX2)) {
          functionName = docName.substring(FUNCTIONS_PREFIX2.length());
        }
        String identifier = F.symbolNameNormalized(functionName);
        ISymbol symbol = Context.SYSTEM.get(identifier);
        if (symbol != null) {
          // String functionURL = SourceCodeFunctions.functionURL(symbol);
          // if (functionURL != null) {
          //
          // out.append("\n\n### Github");
          // out.append("\n\n* [Implementation of ");
          // out.append(functionName);
          // out.append("](");
          // out.append(functionURL);
          // out.append(") ");
          // }
          out.append("\n\n [&larr; Function reference](99-function-reference.md) ");
        } else {
          if (!docName.equals("index")) {
            // jump back to Main documentation page
            out.append("\n\n [&larr; Main](index.md) ");
          }
        }

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static String createJSONDocString(String str) {
    ObjectNode outJSON = JSON_OBJECT_MAPPER.createObjectNode();
    outJSON.put("content", str);
    return outJSON.toString();
    // JSONObject outJSON = new JSONObject();
    // outJSON.put("content", str);
    // return JSONValue.toJSONString(outJSON);
  }
}
