package org.matheclipse.dataset.expression;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.jsoup.nodes.Element;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.html.HtmlWriteOptions.ElementCreator;

/**
 * The display options a {@link ASTDataset} carries.
 *
 * <p>
 * They split in two. <b>What is shown</b> - <code>MaxItems</code>, <code>HiddenItems</code>,
 * <code>HeaderDisplayFunction</code>, <code>ItemDisplayFunction</code> - changes the table itself
 * and so reaches both renderings, the text one a console prints and the HTML one the servlets
 * send. <b>How it is shown</b> - the colours, alignments, sizes and styles - is CSS, and only the
 * HTML rendering has anywhere to put it; a console prints the same characters either way.
 *
 * <p>
 * With no options set this is {@link #DEFAULT} and the table is handed straight through rather
 * than copied, which is what {@link #isDefault()} is for. The text rendering is then byte for byte
 * what tablesaw prints; the HTML rendering still carries the default grid and header shading of
 * {@link #elementCreator()}, which is an appearance and so has no counterpart in a console.
 */
public final class DatasetOptions {

  /** No options: render the way tablesaw renders. */
  public static final DatasetOptions DEFAULT = new DatasetOptions(F.NIL);

  /**
   * The named themes. A theme supplies defaults; an option given next to it wins, so
   * <code>DatasetTheme -> "Dark", Background -> Red</code> means the dark theme with a red body.
   */
  private static final Map<String, String[]> THEMES = new LinkedHashMap<String, String[]>();
  static {
    // {table, header, item, stripe}
    THEMES.put("basic", new String[] {"border-collapse:collapse", "background:#eeeeee;font-weight:bold",
        "border:1px solid #cccccc", ""});
    THEMES.put("striped", new String[] {"border-collapse:collapse",
        "background:#eeeeee;font-weight:bold", "border:1px solid #cccccc", "background:#f7f7f7"});
    THEMES.put("dark", new String[] {"border-collapse:collapse;background:#222222;color:#eeeeee",
        "background:#111111;font-weight:bold", "border:1px solid #444444", ""});
    THEMES.put("minimal",
        new String[] {"border-collapse:collapse", "border-bottom:2px solid #333333", "", ""});
  }

  private final IAST fRules;

  private int fMaxRows = -1;
  private int fMaxColumns = -1;
  private List<String> fHiddenItems = null;
  private IExpr fHeaderDisplayFunction = F.NIL;
  private IExpr fItemDisplayFunction = F.NIL;
  private boolean fAssociationFormat = false;

  private String fTableCss = "";
  private String fHeaderCss = "";
  private String fItemCss = "";
  private String fStripeCss = "";

  private DatasetOptions(IAST rules) {
    fRules = rules;
    if (rules.isPresent() && rules.isList()) {
      parseTheme(rules);
      for (int i = 1; i < rules.size(); i++) {
        IExpr rule = rules.get(i);
        if (rule.isRuleAST()) {
          parse(rule.first(), rule.second());
        }
      }
    }
  }

  /** @param rules a <code>List(...)</code> of option rules, or {@link F#NIL} */
  public static DatasetOptions of(IAST rules) {
    if (rules == null || rules.isNIL() || !rules.isList() || rules.argSize() == 0) {
      return DEFAULT;
    }
    return new DatasetOptions(rules);
  }

  /** Whether nothing was set, so that the caller can skip the whole apparatus. */
  public boolean isDefault() {
    return this == DEFAULT || (fRules.isNIL() || fRules.argSize() == 0);
  }

  /** The rules as given, so that a derived dataset can carry them on. */
  public IAST rules() {
    return fRules;
  }

  /** Whether <code>DatasetDisplayFormat -> "Associations"</code> was asked for. */
  public boolean isAssociationFormat() {
    return fAssociationFormat;
  }

  private void parseTheme(IAST rules) {
    for (int i = 1; i < rules.size(); i++) {
      IExpr rule = rules.get(i);
      if (rule.isRuleAST() && rule.first() == S.DatasetTheme && rule.second().isString()) {
        String[] theme = THEMES.get(rule.second().toString().toLowerCase(java.util.Locale.US));
        if (theme != null) {
          fTableCss = theme[0];
          fHeaderCss = theme[1];
          fItemCss = theme[2];
          fStripeCss = theme[3];
        }
        return;
      }
    }
  }

  private void parse(IExpr key, IExpr value) {
    if (key == S.MaxItems) {
      if (value.isList() && value.size() == 3) {
        fMaxRows = limit(value.first());
        fMaxColumns = limit(value.second());
      } else {
        fMaxRows = limit(value);
      }
    } else if (key == S.HiddenItems) {
      if (value.isString()) {
        fHiddenItems = new ArrayList<String>();
        fHiddenItems.add(value.toString());
      } else if (value.isList()) {
        fHiddenItems = new ArrayList<String>();
        IAST list = (IAST) value;
        for (int i = 1; i < list.size(); i++) {
          fHiddenItems.add(list.get(i).toString());
        }
      }
    } else if (key == S.HeaderDisplayFunction) {
      fHeaderDisplayFunction = value.isSymbol() || value.isFunction() || value.isAST() ? value : F.NIL;
    } else if (key == S.ItemDisplayFunction) {
      fItemDisplayFunction = value.isSymbol() || value.isFunction() || value.isAST() ? value : F.NIL;
    } else if (key == S.DatasetDisplayFormat) {
      fAssociationFormat = value.isString() && value.toString().equalsIgnoreCase("Associations");
    } else if (key == S.Background) {
      fTableCss = append(fTableCss, "background", css(value));
    } else if (key == S.HeaderBackground) {
      fHeaderCss = append(fHeaderCss, "background", css(value));
    } else if (key == S.Alignment) {
      fItemCss = append(fItemCss, "text-align", alignment(value));
    } else if (key == S.HeaderAlignment) {
      fHeaderCss = append(fHeaderCss, "text-align", alignment(value));
    } else if (key == S.ItemSize) {
      fItemCss = append(fItemCss, "width", size(value));
    } else if (key == S.HeaderSize) {
      fHeaderCss = append(fHeaderCss, "width", size(value));
    } else if (key == S.ItemStyle) {
      fItemCss = appendStyle(fItemCss, value);
    } else if (key == S.HeaderStyle) {
      fHeaderCss = appendStyle(fHeaderCss, value);
    }
    // AllowedDimensions is checked when the dataset is built, not when it is drawn.
    // DatasetTheme was read in parseTheme, before this loop, so that it only supplies defaults.
  }

  /** <code>All</code>, <code>Infinity</code> and <code>Automatic</code> mean no limit. */
  private static int limit(IExpr value) {
    if (value == S.All || value == S.Automatic || value.isInfinity()) {
      return -1;
    }
    int n = value.toIntDefault();
    return n < 0 ? -1 : n;
  }

  private static String alignment(IExpr value) {
    if (value == S.Left) {
      return "left";
    } else if (value == S.Right) {
      return "right";
    } else if (value == S.Center) {
      return "center";
    } else if (value.isString()) {
      return value.toString().toLowerCase(java.util.Locale.US);
    }
    return null;
  }

  private static String size(IExpr value) {
    int n = value.toIntDefault();
    // a plain number is a character count, the same unit the text rendering pads in
    return n > 0 ? n + "ch" : null;
  }

  private static String css(IExpr value) {
    if (value.isString()) {
      return value.toString();
    }
    Color color = ColorUtil.parse(value);
    return color == null ? null : ColorUtil.css(color);
  }

  /** <code>Bold</code>, <code>Italic</code>, <code>Underlined</code>, a colour, or a list of them. */
  private static String appendStyle(String css, IExpr value) {
    if (value.isList()) {
      IAST list = (IAST) value;
      for (int i = 1; i < list.size(); i++) {
        css = appendStyle(css, list.get(i));
      }
      return css;
    }
    if (value == S.Bold) {
      return append(css, "font-weight", "bold");
    } else if (value == S.Plain) {
      return append(css, "font-weight", "normal");
    } else if (value == S.Italic) {
      return append(css, "font-style", "italic");
    } else if (value == S.Underlined) {
      return append(css, "text-decoration", "underline");
    }
    return append(css, "color", css(value));
  }

  /**
   * Set one CSS property, replacing whatever a theme put there. The cascade would pick the later
   * declaration anyway, but emitting <code>background:#eeeeee;background:red</code> reads like a
   * bug in the generator rather than like a theme being overridden.
   */
  private static String append(String css, String property, String value) {
    if (value == null) {
      return css;
    }
    StringBuilder result = new StringBuilder();
    for (String declaration : css.split(";")) {
      if (declaration.isEmpty() || declaration.startsWith(property + ":")) {
        continue;
      }
      result.append(declaration).append(';');
    }
    return result.append(property).append(':').append(value).toString();
  }

  /**
   * The table as it is to be shown: fewer rows, fewer columns, columns dropped, headers and items
   * put through their display functions.
   *
   * @return the same table when nothing applies, so that the common case copies nothing
   */
  public Table apply(Table table) {
    if (isDefault()) {
      return table;
    }
    Table result = table;

    if (fHiddenItems != null && !fHiddenItems.isEmpty()) {
      List<String> keep = new ArrayList<String>(result.columnNames());
      keep.removeAll(fHiddenItems);
      result = keep.isEmpty() ? Table.create(result.name())
          : result.selectColumns(keep.toArray(new String[0]));
    }
    if (fMaxColumns >= 0 && result.columnCount() > fMaxColumns) {
      List<String> keep = result.columnNames().subList(0, fMaxColumns);
      result = result.selectColumns(keep.toArray(new String[0]));
    }
    if (fMaxRows >= 0 && result.rowCount() > fMaxRows) {
      result = result.first(fMaxRows);
    }

    if (fItemDisplayFunction.isPresent() || fHeaderDisplayFunction.isPresent()) {
      result = mapDisplay(result);
    }
    if (result != table) {
      // the printer puts the table name above the header, and selectColumns and first() do not
      // carry it, so a styled table would lose a line the plain one has
      result.setName(table.name());
    }
    return result;
  }

  /**
   * Put every header and every cell through its display function. The result is a table of strings:
   * these functions are about how a value looks, so once one has run there is no number left to
   * align or to sum, only what it printed.
   */
  private Table mapDisplay(Table table) {
    EvalEngine engine = EvalEngine.get();
    Table result = Table.create(table.name());
    for (Column<?> column : table.columns()) {
      String name = column.name();
      if (fHeaderDisplayFunction.isPresent()) {
        name = display(engine, fHeaderDisplayFunction, F.$str(name));
      }
      StringColumn mapped = StringColumn.create(name, table.rowCount());
      for (int row = 0; row < table.rowCount(); row++) {
        String cell = String.valueOf(column.getString(row));
        if (fItemDisplayFunction.isPresent()) {
          cell = display(engine, fItemDisplayFunction, F.$str(cell));
        }
        mapped.set(row, cell);
      }
      result.addColumns(mapped);
    }
    return result;
  }

  private static String display(EvalEngine engine, IExpr function, IExpr argument) {
    try {
      IExpr value = engine.evaluate(F.unaryAST1(function, argument));
      return value.isString() ? value.toString() : value.toString();
    } catch (RuntimeException rex) {
      // a display function that throws must not take the whole table down with it
      return argument.toString();
    }
  }

  /**
   * How a dataset looks when nothing was asked for: a light grey header, and a darker grey rule
   * between every pair of cells. <code>border-collapse</code> is what makes the rules a single
   * line rather than two abutting ones.
   *
   * <p>
   * These are a floor, not a fixed appearance - an option that names one of these properties
   * replaces it, and the rest stay. <code>HeaderBackground -&gt; Red</code> gives a red header
   * with the same grid around it.
   */
  /**
   * What a missing cell shows: a hyphen in the colour of the grid, so that an absent value reads
   * as absent at a glance and no cell has to spell out why.
   */
  public static final String MISSING_CELL = "<span style=\"color:darkgray\">-</span>";

  private static final String DEFAULT_TABLE_CSS = "border-collapse:collapse";
  private static final String DEFAULT_HEADER_CSS =
      "background:lightgray;border:1px solid darkgray;padding:2px 6px";
  private static final String DEFAULT_ITEM_CSS = "border:1px solid darkgray;padding:2px 6px";

  /**
   * The hook tablesaw's HTML writer calls for every element it creates, which is where the CSS
   * goes.
   */
  public ElementCreator elementCreator() {
    final String tableCss = merge(DEFAULT_TABLE_CSS, fTableCss);
    final String headerCss = merge(DEFAULT_HEADER_CSS, fHeaderCss);
    final String itemCss = merge(DEFAULT_ITEM_CSS, fItemCss);
    return (elementName, column, row) -> {
      Element element = new Element(elementName);
      if ("table".equals(elementName)) {
        style(element, tableCss);
      } else if ("th".equals(elementName)) {
        style(element, headerCss);
      } else if ("td".equals(elementName)) {
        style(element, itemCss);
      } else if ("tr".equals(elementName) && row != null && row % 2 == 1) {
        style(element, fStripeCss);
      }
      return element;
    };
  }

  /**
   * <code>css</code> laid over <code>base</code>, property by property, so that what was asked for
   * replaces the default of the same property and leaves the others alone.
   */
  private static String merge(String base, String css) {
    String result = base;
    for (String declaration : css.split(";")) {
      int colon = declaration.indexOf(':');
      if (colon > 0) {
        result = append(result, declaration.substring(0, colon), declaration.substring(colon + 1));
      }
    }
    return result;
  }

  private static void style(Element element, String css) {
    if (!css.isEmpty()) {
      element.attr("style", css);
    }
  }

  /**
   * Whether <code>data</code> matches an <code>AllowedDimensions -> {rows, columns}</code> rule.
   * Checked when the dataset is built rather than when it is drawn, because it is a constraint on
   * the data and not on its appearance.
   *
   * @return the offending rule, or {@link F#NIL} if the dimensions are allowed
   */
  public static IExpr allowedDimensionsViolation(IAST rules, int rowCount, int columnCount) {
    if (rules == null || rules.isNIL() || !rules.isList()) {
      return F.NIL;
    }
    for (int i = 1; i < rules.size(); i++) {
      IExpr rule = rules.get(i);
      if (rule.isRuleAST() && rule.first() == S.AllowedDimensions) {
        IExpr value = rule.second();
        if (value.isList() && value.size() == 3) {
          int rows = value.first().toIntDefault();
          int columns = value.second().toIntDefault();
          boolean rowsOk = rows < 0 || value.first() == S.Automatic || rows == rowCount;
          boolean columnsOk = columns < 0 || value.second() == S.Automatic || columns == columnCount;
          if (!rowsOk || !columnsOk) {
            return rule;
          }
        }
      }
    }
    return F.NIL;
  }

  /** The symbols this class understands, in the order the built-in declares its defaults. */
  public static boolean isOptionSymbol(IExpr symbol) {
    return symbol instanceof ISymbol && (symbol == S.Alignment || symbol == S.AllowedDimensions
        || symbol == S.Background || symbol == S.DatasetDisplayFormat || symbol == S.DatasetTheme
        || symbol == S.HeaderAlignment || symbol == S.HeaderBackground
        || symbol == S.HeaderDisplayFunction || symbol == S.HeaderSize || symbol == S.HeaderStyle
        || symbol == S.HiddenItems || symbol == S.ItemDisplayFunction || symbol == S.ItemSize
        || symbol == S.ItemStyle || symbol == S.MaxItems);
  }
}
