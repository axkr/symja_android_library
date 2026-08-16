package org.matheclipse.io.servlet;

import java.util.Set;
import org.apache.commons.text.StringEscapeUtils;
import org.commonmark.Extension;
import org.commonmark.node.Code;
import org.commonmark.node.CustomNode;
import org.commonmark.node.Delimited;
import org.commonmark.node.Emphasis;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Node;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.parser.delimiter.DelimiterProcessor;
import org.commonmark.parser.delimiter.DelimiterRun;
import org.commonmark.renderer.NodeRenderer;
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlNodeRendererFactory;
import org.commonmark.renderer.html.HtmlWriter;

/**
 * The <code>$...$</code> and <code>$$...$$</code> mathematics of a Markdown text, shared by the
 * documentation pages and by the Markdown cells of a notebook.
 *
 * <p>
 * The formula is written out in the delimiters that KaTeX's auto-render is configured for -
 * <code>\(...\)</code> inline and <code>\[...\]</code> in display - rather than being rendered
 * here, so that the browser does the typesetting.
 */
public final class MarkdownTeX {

  private MarkdownTeX() {}

  /** A TeX node holding the LaTeX source between a pair of <code>$</code> delimiters. */
  public static final class TeX extends CustomNode implements Delimited {

    private final String source;
    private final boolean display;

    TeX(String source, boolean display) {
      this.source = source;
      this.display = display;
    }

    public String getSource() {
      return source;
    }

    public boolean isDisplay() {
      return display;
    }

    private String delimiter() {
      return display ? "$$" : "$";
    }

    @Override
    public String getOpeningDelimiter() {
      return delimiter();
    }

    @Override
    public String getClosingDelimiter() {
      return delimiter();
    }
  }

  private static final class TeXDelimiterProcessor implements DelimiterProcessor {

    private final boolean guardDollarSymbols;

    TeXDelimiterProcessor(boolean guardDollarSymbols) {
      this.guardDollarSymbols = guardDollarSymbols;
    }

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
      return 1;
    }

    /**
     * Wrap the nodes between the two delimiters in a {@link TeX} node. <code>$$...$$</code> is
     * display math, a single <code>$...$</code> is inline math.
     *
     * <p>
     * With <code>guardDollarSymbols</code> a single <code>$</code> directly followed by a letter or
     * a digit does not open math. The documentation is full of Symja system symbols -
     * <code>$Assumptions</code>, <code>$Line</code>, <code>$IterationLimit</code> - and without that
     * rule a paragraph mentioning two of them would have everything in between swallowed as a
     * formula. A Markdown cell of a notebook is written the way a Jupyter one is, where
     * <code>$x$</code> is ordinary inline math, so there the guard is off.
     */
    @Override
    public int process(DelimiterRun openingRun, DelimiterRun closingRun) {
      boolean display = openingRun.length() >= 2 && closingRun.length() >= 2;
      Text opener = openingRun.getOpener();
      Node closer = closingRun.getCloser();

      String source = teXSourceBetween(opener, closer);
      if (source.isEmpty()) {
        return 0;
      }
      if (guardDollarSymbols && !display && Character.isLetterOrDigit(source.charAt(0))) {
        return 0;
      }

      TeX tex = new TeX(source, display);
      Node node = opener.getNext();
      while (node != null && node != closer) {
        Node next = node.getNext();
        tex.appendChild(node);
        node = next;
      }
      opener.insertAfter(tex);
      return display ? 2 : 1;
    }
  }

  /**
   * The parser extension for <code>$</code> mathematics.
   *
   * @param guardDollarSymbols if <code>true</code>, a single <code>$</code> in front of a letter or
   *        a digit does not start a formula
   */
  public static Extension extension(final boolean guardDollarSymbols) {
    return new Parser.ParserExtension() {
      @Override
      public void extend(Parser.Builder parserBuilder) {
        parserBuilder.customDelimiterProcessor(new TeXDelimiterProcessor(guardDollarSymbols));
      }
    };
  }

  /**
   * Write the formula in the delimiters that KaTeX's auto-render looks for. The source is escaped,
   * so it cannot bring markup of its own into the page; KaTeX reads the text content of the node,
   * which gives back the original characters.
   */
  public static void render(HtmlWriter html, TeX teXNode) {
    String source = StringEscapeUtils.escapeHtml4(teXNode.getSource());
    if (teXNode.isDisplay()) {
      html.raw("\\[" + source + "\\]");
    } else {
      html.raw("\\(" + source + "\\)");
    }
  }

  /** A renderer for {@link TeX} nodes, for a Markdown renderer that needs nothing else. */
  public static HtmlNodeRendererFactory rendererFactory() {
    return new HtmlNodeRendererFactory() {
      @Override
      public NodeRenderer create(HtmlNodeRendererContext context) {
        return new TeXNodeRenderer(context);
      }
    };
  }

  private static final class TeXNodeRenderer extends CoreHtmlNodeRenderer {
    private final HtmlWriter html;

    TeXNodeRenderer(HtmlNodeRendererContext context) {
      super(context);
      this.html = context.getWriter();
    }

    @Override
    public Set<Class<? extends Node>> getNodeTypes() {
      return java.util.Collections.<Class<? extends Node>>singleton(TeX.class);
    }

    @Override
    public void render(Node node) {
      MarkdownTeX.render(html, (TeX) node);
    }
  }

  /**
   * Recover the LaTeX source between two delimiters. The inline parser has already run over it, so
   * an <code>_</code> or a <code>*</code> in a formula may have been turned into emphasis; the
   * markup those nodes came from is put back here.
   */
  static String teXSourceBetween(Node opener, Node closer) {
    StringBuilder buf = new StringBuilder();
    for (Node node = opener.getNext(); node != null && node != closer; node = node.getNext()) {
      appendTeXSource(buf, node);
    }
    return buf.toString().trim();
  }

  private static void appendTeXSource(StringBuilder buf, Node node) {
    if (node instanceof Text) {
      buf.append(((Text) node).getLiteral());
      return;
    }
    if (node instanceof Code) {
      buf.append('`').append(((Code) node).getLiteral()).append('`');
      return;
    }
    if (node instanceof SoftLineBreak || node instanceof HardLineBreak) {
      buf.append(' ');
      return;
    }
    String delimiter = node instanceof StrongEmphasis ? "**" //
        : node instanceof Emphasis ? "_" //
            : "";
    buf.append(delimiter);
    for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
      appendTeXSource(buf, child);
    }
    buf.append(delimiter);
  }
}
