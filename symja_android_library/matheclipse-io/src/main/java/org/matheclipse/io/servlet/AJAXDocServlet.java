package org.matheclipse.io.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.text.StringEscapeUtils;
import org.commonmark.Extension;
import org.commonmark.node.CustomNode;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.html.HtmlWriter;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.output.WolframFormFactory;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.parser.client.ParserConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * AJAX servlet to load Markdown documentation files.
 */
public class AJAXDocServlet extends HttpServlet {
  private static final Cache<String, String> JSON_DOCS_CACHE =
      CacheBuilder.newBuilder().maximumSize(100).build();

  private static final long serialVersionUID = 4636252666864898484L;

  public static final String FUNCTIONS_PREFIX1 = "/functions/";
  public static final String FUNCTIONS_PREFIX2 = "functions/";

  public static final ObjectMapper JSON_OBJECT_MAPPER = new ObjectMapper();

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
      set.add(MarkdownTeX.TeX.class);
      return set;
    }

    @Override
    public void render(Node node) {
      if (node instanceof FencedCodeBlock) {
        fencedCodeBlock((FencedCodeBlock) node);
      } else if (node instanceof Link) {
        link((Link) node);
      } else if (node instanceof MarkdownTeX.TeX) {
        MarkdownTeX.render(html, (MarkdownTeX.TeX) node);
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
                  exampleCommand = toWolframInput(engine, wolframForm, exampleCommand);
                } catch (RuntimeException e) {
                  // especially SyntaxError problems
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
      // an autolinked or written out URL - https://.../README.md - stays a link to that URL
      if (index > 0 && !destination.contains(":")) {
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

  /**
   * Rewrite a documentation example from Symja's relaxed syntax - <code>Sin(3*Pi)</code> - into
   * Wolfram Language input - <code>Sin[3*Pi]</code> - for a kernel which reads names with their
   * case.
   *
   * <p>
   * The relaxed parser reads a name in lower case and looks it up that way. A kernel started with
   * case-sensitive names keeps its built-ins under their own spelling, so the lookup misses and
   * <code>Sin</code> comes back as the global symbol <code>sin</code>. Such a symbol is put back
   * to the built-in its name stands for before the example is printed. A name of one character
   * keeps its case in the relaxed syntax, so <code>e</code> or <code>x</code> stays what it is.
   */
  static String toWolframInput(EvalEngine engine, WolframFormFactory wolframForm,
      String example) {
    IExpr expr = new ExprParser(engine, true).parse(example);
    if (expr == null) {
      return example;
    }
    IExpr builtins = expr.replaceAll(AJAXDocServlet::builtinOfGlobalSymbol).orElse(expr);
    return dropMachinePrecisionMarks(wolframForm.toString(builtins));
  }

  /** A string literal, with its escaped characters. */
  private static final Pattern STRING_LITERAL = Pattern.compile("\"(?:[^\"\\\\]|\\\\.)*\"");

  /**
   * The <code>`</code> which marks a machine real - <code>0.5`</code>,
   * <code>1.0`*^-20</code> - but not one which a precision follows, as in
   * <code>1.000`20</code>.
   */
  private static final Pattern MACHINE_PRECISION_MARK =
      Pattern.compile("(?<![\\w`.])(\\d+\\.\\d*)`(?![\\d`])");

  /**
   * Write machine reals the way the input form of the Wolfram Language does, without the
   * <code>`</code> that marks their precision: <code>Sin[0.5]</code> rather than
   * <code>Sin[0.5`]</code>. String literals are left as they are.
   */
  static String dropMachinePrecisionMarks(String input) {
    if (input.indexOf('`') < 0) {
      return input;
    }
    StringBuilder buf = new StringBuilder(input.length());
    Matcher strings = STRING_LITERAL.matcher(input);
    int start = 0;
    while (strings.find()) {
      buf.append(MACHINE_PRECISION_MARK.matcher(input.substring(start, strings.start()))
          .replaceAll("$1"));
      buf.append(strings.group());
      start = strings.end();
    }
    buf.append(MACHINE_PRECISION_MARK.matcher(input.substring(start)).replaceAll("$1"));
    return buf.toString();
  }

  private static IExpr builtinOfGlobalSymbol(IExpr expr) {
    if (expr instanceof ISymbol) {
      ISymbol symbol = (ISymbol) expr;
      String name = symbol.getSymbolName();
      if (name.length() > 1 && symbol.getContext().isGlobal()) {
        String systemName = AST2Expr.PREDEFINED_SYMBOLS_MAP.get(name);
        if (systemName != null) {
          ISymbol builtin = Context.SYSTEM.get(systemName);
          if (builtin != null) {
            return builtin;
          }
        }
      }
    }
    return F.NIL;
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
        createMarkdownFunctionPage(markdownBuf, value);
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
    List<Extension> EXTENSIONS = GitHubMarkdown.extensions(true);
    Parser parser = Parser.builder() //
        .extensions(EXTENSIONS).build();
    Node document = parser.parse(markdownStr);

    HtmlRenderer renderer = HtmlRenderer.builder() //
        .extensions(EXTENSIONS) //
        .attributeProviderFactory(GitHubMarkdown.taskListAttributes()) //
        .nodeRendererFactory(new HtmlNodeRendererFactory() {
          @Override
          public NodeRenderer create(HtmlNodeRendererContext context) {
            return new DocNodeRenderer(context);
          }
        }).build();
    return renderer.render(document);
  }

  /**
   * Create a documentation page for a function in Markdown format
   * 
   * @param out the output stream for the Markdown documentation
   * @param functionPath the name of the function including path prefixes
   */
  public static void createMarkdownFunctionPage(Appendable out, String functionPath) {
    // read markdown file
    String fileName = Documentation.buildDocFilename(functionPath);

    // Get file from resources folder
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();

    URL file = classloader.getResource(fileName);
    if (file != null) {
      // jump back to Main documentation page
      try (BufferedReader f =
          new BufferedReader(new InputStreamReader(file.openStream(), StandardCharsets.UTF_8))) {
        // insert link on top of page for jumping back to Main documentation page
        out.append("\n\n [&larr; Main](index.md)\n");
        String line;
        while ((line = f.readLine()) != null) {
          out.append(line);
          out.append("\n");
        }
        String functionName = functionPath;
        if (functionPath.startsWith(FUNCTIONS_PREFIX1)) {
          functionName = functionPath.substring(FUNCTIONS_PREFIX1.length());
        } else if (functionPath.startsWith(FUNCTIONS_PREFIX2)) {
          functionName = functionPath.substring(FUNCTIONS_PREFIX2.length());
        }
        String identifier = F.symbolNameNormalized(functionName);
        ISymbol symbol = Context.SYSTEM.get(identifier);
        if (symbol != null) {
          // insert link at the bottom for jumping back to Function reference page
          out.append("\n\n [&larr; Function reference](99-function-reference.md) ");
        } else {
          if (!functionPath.equals("index")) {
            // insert link at the bottom for jumping back to Main documentation page
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
