package org.matheclipse.astro;

import org.matheclipse.astro.builtin.AstroEclipseFunctions;
import org.matheclipse.astro.builtin.AstroEventFunctions;
import org.matheclipse.astro.builtin.AstroFindEventFunctions;
import org.matheclipse.astro.builtin.AstroGeoFunctions;
import org.matheclipse.astro.builtin.AstroGraphicsFunctions;
import org.matheclipse.astro.builtin.AstroOrbitFunctions;
import org.matheclipse.astro.builtin.AstroPositionFunctions;
import org.matheclipse.astro.builtin.GeoGraphicsFunctions;
import org.matheclipse.astro.builtin.StarDataFunctions;
import org.matheclipse.astro.builtin.AstroTimeFunctions;
import org.matheclipse.astro.data.AstroDataContext;
import org.matheclipse.core.basic.ToggleFeature;

/**
 * Registers the astronomy functions of the <code>matheclipse-astro</code> module with the
 * evaluation engine. Call this after <code>F.initSymja()</code>; <code>org.matheclipse.io.IOInit</code>
 * already does so for the servlets and the consoles.
 *
 * <p>
 * The registration does not depend on the external Orekit data being installed - the functions are
 * always registered and report the <code>orekitdata</code> message at evaluation time when the data
 * is missing. See {@link AstroDataContext}.
 */
public class AstroInit {

  public static void init() {
    if (!ToggleFeature.ASTRO) {
      return;
    }
    AstroDataContext.initialize();
    AstroPositionFunctions.initialize();
    AstroEventFunctions.initialize();
    AstroTimeFunctions.initialize();
    AstroGeoFunctions.initialize();
    AstroOrbitFunctions.initialize();
    AstroFindEventFunctions.initialize();
    AstroEclipseFunctions.initialize();
    AstroGraphicsFunctions.initialize();
    StarDataFunctions.initialize();
    GeoGraphicsFunctions.initialize();
  }

  private AstroInit() {}
}
