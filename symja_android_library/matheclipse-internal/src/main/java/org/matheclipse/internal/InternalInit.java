package org.matheclipse.internal;

import org.matheclipse.internal.builtin.InternalFunctions;

/**
 * Initializes the <code>matheclipse-internal</code> module: registers the functions of the
 * <code>Internal`</code> context, such as <code>Internal`Bag</code>,
 * <code>Internal`InheritedBlock</code> or <code>Internal`ExceptionFreeQ</code>, which packages
 * written for other systems call.
 *
 * <p>
 * Call {@link #init()} once after <code>F.initSymja()</code> (or <code>F.await()</code>) and
 * before the first input which uses these names is parsed. An input parsed earlier keeps the
 * ordinary, undefined <code>Internal`</code> symbols it was parsed with.
 */
public class InternalInit {

  /** Register the <code>Internal`</code> functions; calling it again does nothing. */
  public static void init() {
    InternalFunctions.initialize();
  }

  private InternalInit() {}
}
