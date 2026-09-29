/**
 * The Meeus algorithms of Night Vision, ported as a headless library.
 *
 * <p>
 * The classes of this package are ported from <a href=
 * "https://sourceforge.net/projects/nightvision/">Night Vision</a> 5.5 by Brian Simpson (package
 * <code>com.nvastro.nvj</code>, commit <code>84b2522</code>), which is licensed under the GNU
 * General Public License version 3 or later. Every ported file keeps its original copyright and
 * licence notice and states how it was modified. Only formulas and numeric tables were taken; the
 * drawing code, the Swing user interface and the preferences of Night Vision were not.
 *
 * <p>
 * The algorithms are from Jean Meeus, <i>Astronomical Algorithms</i>, 2nd edition, Willmann-Bell
 * 1998: abridged VSOP87 series for the planets (Appendix III), the Chapter 47 lunar theory, the
 * Chapter 37 Pluto theory, IAU 1980 nutation, IAU 1976 precession, planet magnitudes and delta T.
 * They are less accurate than the JPL DE 440 ephemerides Orekit uses, but they need no data files
 * and have no date range limit, so {@code matheclipse-astro} uses them for what Orekit does not
 * provide and as a fallback outside the range of the bundled ephemerides.
 *
 * <p>
 * Nothing in this package depends on Orekit, Symja, AWT or Swing, and nothing in it has mutable
 * static state.
 */
package org.matheclipse.astro.meeus;
