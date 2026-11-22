package org.matheclipse.io.servlet;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * The documentation pages and the Markdown cells of a notebook understand the GitHub extensions -
 * tables, strikethrough, autolinks, task lists, alerts and footnotes - next to the
 * <code>$</code> mathematics that the browser typesets with KaTeX.
 */
public class MarkdownRenderTest {

  private static void assertContains(String html, String expected) {
    assertTrue(html.contains(expected), () -> "expected <" + expected + "> in:\n" + html);
  }

  private static String[] both(String markdown) {
    return new String[] {AJAXDocServlet.generateHTMLString(markdown),
        AJAXNotebookServlet.renderMarkdown(markdown)};
  }

  @Test
  public void tableAndStrikethrough() {
    for (String html : both("| x | f |\n|---|---|\n| 1 | 2 |\n\nkeep ~~drop~~ keep\n")) {
      assertContains(html, "<table>");
      assertContains(html, "<td>2</td>");
      assertContains(html, "keep <del>drop</del> keep");
    }
  }

  @Test
  public void bareUrlBecomesALink() {
    for (String html : both("see https://example.org/page for more\n")) {
      // the notebook adds rel="nofollow" to every link
      assertContains(html, "href=\"https://example.org/page\">https://example.org/page</a>");
    }
  }

  @Test
  public void footnote() {
    for (String html : both("A claim.[^src]\n\n[^src]: The source.\n")) {
      assertContains(html, "<sup class=\"footnote-ref\"><a href=\"#fn-src\"");
      assertContains(html, "<section class=\"footnotes\"");
      assertContains(html, "The source.");
    }
  }

  @Test
  public void alert() {
    for (String html : both("> [!WARNING]\n> Mind the branch cut.\n")) {
      assertContains(html, "<div class=\"markdown-alert markdown-alert-warning\"");
      assertContains(html, "<p class=\"markdown-alert-title\">Warning</p>");
      assertContains(html, "Mind the branch cut.");
    }
  }

  @Test
  public void taskList() {
    for (String html : both("- [x] factor\n- [ ] expand\n\nand\n\n- plain\n")) {
      assertContains(html, "<ul class=\"contains-task-list\">");
      assertContains(html, "<li class=\"task-list-item\"><input type=\"checkbox\" disabled=\"\" checked=\"\"> factor</li>");
      assertContains(html, "<li class=\"task-list-item\"><input type=\"checkbox\" disabled=\"\"> expand</li>");
      // an ordinary list keeps its bullets
      assertContains(html, "<ul>\n<li>plain</li>");
    }
  }

  @Test
  public void teXKeepsItsDelimiterCharacters() {
    String html = AJAXNotebookServlet.renderMarkdown("$a *b* c$ and $u~v~w$ and $$p**q**r$$\n");
    assertContains(html, "\\(a *b* c\\)");
    assertContains(html, "\\(u~v~w\\)");
    assertContains(html, "\\[p**q**r\\]");
    assertFalse(html.contains("<del>"), html);
    assertFalse(html.contains("<em>"), html);
  }

  /** <code>\{</code>, <code>\,</code>, <code>\\</code> and <code>\$</code> are Markdown escapes. */
  @Test
  public void teXKeepsItsBackslashes() {
    for (String html : both("Set $\\{y\\mid y>0\\}$ and $\\$5$.\n\n"
        + "$$\\int_0^1 t\\,dt \\\\ \\alpha &lt; \\beta$$\n")) {
      assertContains(html, "\\(\\{y\\mid y&gt;0\\}\\)");
      assertContains(html, "\\(\\$5\\)");
      // an entity reference stays the character it stands for
      assertContains(html, "\\[\\int_0^1 t\\,dt \\\\ \\alpha &lt; \\beta\\]");
    }
    // outside of a formula an escape is still an escape
    assertContains(AJAXNotebookServlet.renderMarkdown("cost \\$5 \\{a\\}\n"), "<p>cost $5 {a}</p>");
  }

  /** A <code>\|</code> in a table is the pipe, also in a formula. */
  @Test
  public void teXInATableCell() {
    String html = AJAXNotebookServlet.renderMarkdown("| f | v |\n|---|---|\n| $\\|x\\|$ | 1 |\n");
    assertContains(html, "<td>\\(|x|\\)</td>");
  }

  @Test
  public void teXInsideTheNewBlocks() {
    for (String html : both("> [!NOTE]\n> The value is $\\sqrt{2}$.\n\n- [ ] solve $\\pi y$\n")) {
      assertContains(html, "markdown-alert-note");
      assertContains(html, "\\(\\sqrt{2}\\)");
      assertContains(html, "<input type=\"checkbox\" disabled=\"\"> solve \\(\\pi y\\)</li>");
    }
  }

  @Test
  public void docPagesKeepSystemSymbols() {
    String html =
        AJAXDocServlet.generateHTMLString("Compare $Line with $Assumptions and $IterationLimit.\n");
    assertContains(html, "$Line with $Assumptions and $IterationLimit.");
  }

  @Test
  public void docLinks() {
    String html = AJAXDocServlet.generateHTMLString(
        "[Plus](Plus.md) and https://example.org/README.md and [x](https://example.org/x.md)\n");
    assertContains(html, "<a href=\"javascript:loadDoc('/functions/Plus')\">Plus</a>");
    assertContains(html, "<a href=\"https://example.org/README.md\">");
    assertContains(html, "<a href=\"https://example.org/x.md\">x</a>");
  }

  @Test
  public void notebookCellsStayEscaped() {
    String html = AJAXNotebookServlet.renderMarkdown(
        "<script>alert(1)</script>\n\n[x](javascript:alert(1)) https://example.org\n");
    assertFalse(html.contains("<script>"), html);
    assertFalse(html.contains("href=\"javascript:"), html);
    assertContains(html, "href=\"https://example.org\">https://example.org</a>");
  }
}
