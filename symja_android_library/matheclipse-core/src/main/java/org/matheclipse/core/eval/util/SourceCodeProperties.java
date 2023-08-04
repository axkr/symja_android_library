package org.matheclipse.core.eval.util;

/**
 * Define properties how to generate Java, Scala or other source codes from Symja expressions.
 */
public class SourceCodeProperties {

  public enum Prefix {
    NONE, CLASS_NAME, FULLY_QUALIFIED_CLASS_NAME;
  }

  public static final SourceCodeProperties JAVA_FORM_PROPERTIES =
      of(false, false, Prefix.CLASS_NAME, false);

  public static final SourceCodeProperties JAVA_FORM_PROPERTIES_NO_SYMBOL_PREFIX =
      of(false, false, Prefix.CLASS_NAME, true);

  public static final SourceCodeProperties STRING_FORM_SYMBOL_FACTORY =
      of(true, false, Prefix.NONE, false);

  public static final SourceCodeProperties STRING_FORM_NO_SYMBOL_FACTORY =
      of(false, false, Prefix.NONE, false);

  public static final SourceCodeProperties SCALA_FORM_NO_SYMBOL_FACTORY =
      of(false, true, Prefix.NONE, false);

  public static final SourceCodeProperties SCALA_FORM_SYMBOL_FACTORY =
      of(true, true, Prefix.NONE, false);

  /**
   * Returns a {@link SourceCodeProperties} objects with the same values as the given one except
   * that the field {@link #symbolsAsFactoryMethod} of the returned object is always <code>false
   * </code>.
   */
  public static SourceCodeProperties copyWithoutSymbolsAsFactoryMethod(SourceCodeProperties o) {
    return !o.symbolsAsFactoryMethod ? o
        : new SourceCodeProperties(false, o.useOperators, o.prefix, o.noSymbolPrefix);
  }

  public static String getPrefixF(SourceCodeProperties properties) {
    switch (properties.prefix) {
      case FULLY_QUALIFIED_CLASS_NAME:
        return "org.matheclipse.core.expression.F.";
      case CLASS_NAME:
        return "F.";
      case NONE:
      default:
        return "";
    }
  }

  /**
   * Reaturns a {@link SourceCodeProperties} object with the specified parameters.
   *
   * @param symbolsAsFactoryMethod if <code>true</code> use the <code>F.symbol()</code> method,
   *        otherwise print the symbol name.
   * @param useOperators use operators instead of function names for representation of Plus, Times,
   *        Power,...
   * @param prefix if {@link Prefix#CLASS_NAME} use the <code>F....</code> class prefix for
   *        generating Java code, if {@link Prefix#FULLY_QUALIFIED_CLASS_NAME} use the fully
   *        qualified class name, if {@link Prefix#NONE} use no prefix.
   * @param noSymbolPrefix for symbols like <code>x,y,z,...</code> don't use the <code>F....
   *     </code> class prefix for code generation
   */
  public static SourceCodeProperties of(boolean symbolsAsFactoryMethod, boolean useOperators,
      SourceCodeProperties.Prefix prefix, boolean noSymbolPrefix) {
    return new SourceCodeProperties(symbolsAsFactoryMethod, useOperators, prefix, noSymbolPrefix);
  }

  public static SourceCodeProperties scalaFormProperties(boolean symbolsAsFactoryMethod) {
    return symbolsAsFactoryMethod ? SCALA_FORM_SYMBOL_FACTORY : SCALA_FORM_NO_SYMBOL_FACTORY;
  }



  public static SourceCodeProperties stringFormProperties(boolean symbolsAsFactoryMethod) {
    return symbolsAsFactoryMethod ? STRING_FORM_SYMBOL_FACTORY : STRING_FORM_NO_SYMBOL_FACTORY;
  }

  /**
   * If <code>true</code> use the <code>F.symbol()</code> method, otherwise print the symbol name.
   */
  public final boolean symbolsAsFactoryMethod;

  /**
   * If true use operators instead of function names for representation of Plus, Times, Power,...
   */
  public final boolean useOperators;

  /**
   * If {@link Prefix#CLASS_NAME} use the <code>F....</code> class prefix for generating Java code,
   * if {@link Prefix#FULLY_QUALIFIED_CLASS_NAME} use the fully qualified class name, if
   * {@link Prefix#NONE} use no prefix.
   */
  public final Prefix prefix;

  /**
   * If <code>true</code>, for symbols like <code>x,y,z,...</code> don't use the <code>F....
   * </code> class prefix for code generation.
   */
  public final boolean noSymbolPrefix;

  private SourceCodeProperties(boolean symbolsAsFactoryMethod, boolean useOperators,
      SourceCodeProperties.Prefix prefix, boolean noSymbolPrefix) {
    this.symbolsAsFactoryMethod = symbolsAsFactoryMethod;
    this.useOperators = useOperators;
    this.prefix = prefix;
    // this.prefix = Objects.requireNonNull(prefix, "Method prefix must not be null");
    this.noSymbolPrefix = noSymbolPrefix;
  }
}
