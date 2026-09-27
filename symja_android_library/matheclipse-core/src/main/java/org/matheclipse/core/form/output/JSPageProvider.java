package org.matheclipse.core.form.output;

/**
 * Builds the stand-alone HTML page that shows the JavaScript of a {@code JSFormData[js, "type"]}
 * result.
 *
 * <p>
 * The pages, and the CDN addresses of the libraries they load, live in the optional
 * <code>matheclipse-jsgraphics</code> module, which <code>matheclipse-core</code> must not depend
 * on. So the dependency is inverted the way {@link org.matheclipse.core.compile.IExprCompiler} does
 * it: <code>org.matheclipse.jsgraphics.JSGraphicsInit#init()</code> installs an implementation here.
 * While none is installed {@link #get()} returns <code>null</code>.
 */
public interface JSPageProvider {

  /** The installed provider, or <code>null</code> when <code>matheclipse-jsgraphics</code> is absent. */
  JSPageProvider[] INSTANCE = new JSPageProvider[1];

  /** Install the provider. Called from <code>org.matheclipse.jsgraphics.JSGraphicsInit</code>. */
  static void install(JSPageProvider provider) {
    INSTANCE[0] = provider;
  }

  /** @return the installed provider, or <code>null</code> */
  static JSPageProvider get() {
    return INSTANCE[0];
  }

  /**
   * A complete HTML page that loads the library for {@code type} from its CDN and runs {@code js}.
   *
   * @param type one of the JavaScript library names in {@link OutputFormats}, e.g.
   *        {@link OutputFormats#JSXGRAPH_STR}
   * @param js the JavaScript to run on the page
   * @return the page, or <code>null</code> for a type the provider does not know
   */
  String page(String type, String js);

  /**
   * {@link #page(String, String)} embedded in a sandboxed <code>&lt;iframe srcdoc="..."&gt;</code>,
   * ready to be put into another HTML page.
   *
   * <p>
   * Every JavaScript rendering is shown this way, never inlined into the host page: the library is
   * loaded from its CDN inside the frame, its globals and styles cannot clash with the host page or
   * with a second result, and the sandbox keeps the generated script away from the host page's
   * cookies and DOM.
   *
   * @return the <code>&lt;iframe&gt;</code> element, or <code>null</code> for a type the provider
   *         does not know
   */
  String iframe(String type, String js);

  /**
   * {@link #iframe(String, String)} of the installed provider.
   *
   * @return <code>null</code> when no provider is installed or it does not know {@code type}
   */
  static String iframeOf(String type, String js) {
    JSPageProvider provider = INSTANCE[0];
    return provider == null ? null : provider.iframe(type, js);
  }

  /**
   * {@link #page(String, String)} of the installed provider.
   *
   * @return <code>null</code> when no provider is installed or it does not know {@code type}
   */
  static String pageOf(String type, String js) {
    JSPageProvider provider = INSTANCE[0];
    return provider == null ? null : provider.page(type, js);
  }
}
