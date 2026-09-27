package org.matheclipse.jsgraphics;

import org.matheclipse.core.form.output.JSPageProvider;
import org.matheclipse.jsgraphics.builtin.JSGraphicsFunctions;
import org.matheclipse.jsgraphics.page.JSPageBuilder;

/**
 * Initializes the <code>matheclipse-jsgraphics</code> module.
 *
 * <p>
 * Registers the evaluators of <code>JSXGraph</code>, <code>ECharts</code> and <code>MathCell</code>,
 * whose symbols are declared in <code>matheclipse-core</code>, and installs the pages that show a
 * <code>JSFormData</code> result as {@link JSPageProvider}. Without this module those symbols stay
 * unevaluated and a <code>JSFormData</code> result has no page to be shown in.
 */
public class JSGraphicsInit {

  private JSGraphicsInit() {}

  public static void init() {
    JSPageProvider.install(JSPageBuilder.INSTANCE);
    JSGraphicsFunctions.initialize();
  }
}
