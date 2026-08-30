package org.matheclipse.astro.sky;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The Greek alphabet as it appears in Bayer designations.
 *
 * <p>
 * The catalogue stores the Bayer letter as the Greek character itself, but nobody types
 * <code>"α CMa"</code>. This maps each letter to the spellings a user is likely to write - the
 * full English name and the three letter abbreviation the standard catalogues use - so that
 * <code>"alpha CMa"</code>, <code>"alp CMa"</code> and the Greek form all find the same star.
 */
final class GreekLetters {

  /** Greek letter to {full name, three letter abbreviation}. */
  private static final Map<String, String[]> SPELLINGS = build();

  private GreekLetters() {}

  private static Map<String, String[]> build() {
    Map<String, String[]> map = new LinkedHashMap<String, String[]>();
    map.put("α", new String[] {"alpha", "alp"});
    map.put("β", new String[] {"beta", "bet"});
    map.put("γ", new String[] {"gamma", "gam"});
    map.put("δ", new String[] {"delta", "del"});
    map.put("ε", new String[] {"epsilon", "eps"});
    map.put("ζ", new String[] {"zeta", "zet"});
    map.put("η", new String[] {"eta", "eta"});
    map.put("θ", new String[] {"theta", "the"});
    map.put("ι", new String[] {"iota", "iot"});
    map.put("κ", new String[] {"kappa", "kap"});
    map.put("λ", new String[] {"lambda", "lam"});
    map.put("μ", new String[] {"mu", "mu"});
    map.put("ν", new String[] {"nu", "nu"});
    map.put("ξ", new String[] {"xi", "xi"});
    map.put("ο", new String[] {"omicron", "omi"});
    map.put("π", new String[] {"pi", "pi"});
    map.put("ρ", new String[] {"rho", "rho"});
    map.put("σ", new String[] {"sigma", "sig"});
    map.put("τ", new String[] {"tau", "tau"});
    map.put("υ", new String[] {"upsilon", "ups"});
    map.put("φ", new String[] {"phi", "phi"});
    map.put("χ", new String[] {"chi", "chi"});
    map.put("ψ", new String[] {"psi", "psi"});
    map.put("ω", new String[] {"omega", "ome"});
    return map;
  }

  /**
   * Every way of writing a Bayer letter, the original included.
   *
   * <p>
   * A Bayer designation may carry a superscript index, as in <code>"π 1"</code> for pi-1 Orionis;
   * the index is kept on the end of each spelling so the variants stay distinct.
   *
   * @return the spellings, or a single element list holding {@code bayer} when it is not Greek
   */
  static List<String> spellings(String bayer) {
    List<String> result = new ArrayList<String>();
    if (bayer == null || bayer.isEmpty()) {
      return result;
    }
    result.add(bayer);
    String letter = bayer.substring(0, 1);
    String suffix = bayer.substring(1).trim();
    String[] names = SPELLINGS.get(letter);
    if (names != null) {
      for (String name : names) {
        result.add(name + suffix);
      }
    }
    return result;
  }
}
