package org.matheclipse.core.basic;

/**
 * Scales every wall-clock evaluation budget of Symja by one factor.
 *
 * <p>
 * Several algorithms bound themselves in wall-clock time rather than in work: the Rubi rules get
 * 45 seconds inside <code>Integrate()</code>, one step of the symmetry search for
 * <code>DSolve()</code> gets 3 seconds, and so on. Those numbers are not a policy anyone chose,
 * they are an estimate of how long a machine of a particular speed needs, measured on an Apple M5.
 * On a slower machine they cut off evaluations which would have succeeded, and because a budget
 * which runs out looks exactly like a method which does not apply, the result is not a longer wait
 * but a worse answer.
 *
 * <p>
 * The factor here is what makes those numbers portable. It is <code>1.0</code> on the machine the
 * budgets were tuned on, larger on a slower one, and it multiplies every budget at the place where
 * that budget is read. The constants themselves keep their values and their meaning: they are the
 * budget on the baseline machine.
 *
 * <h2>Configuration</h2>
 *
 * The first of these which yields a valid factor wins:
 *
 * <ol>
 * <li>{@link #setScale(double)} or {@link #setPreset(Preset)}, called by the embedding program</li>
 * <li>the system property <code>-Dsymja.timeScale=2.5</code></li>
 * <li>the environment variable <code>SYMJA_TIME_SCALE=2.5</code></li>
 * <li>the system property <code>-Dsymja.machineProfile=fast|normal|slow|auto</code></li>
 * <li>the environment variable <code>SYMJA_MACHINE_PROFILE</code></li>
 * <li><code>1.0</code></li>
 * </ol>
 *
 * A value which cannot be used is reported on <code>System.err</code> and the next source is tried,
 * rather than being silently corrected: a factor is easy to mistype and a wrong one is invisible in
 * the results.
 *
 * <p>
 * The <code>auto</code> profile measures the machine with {@link Config#calibrateTimeScale()}
 * instead of being told the factor. It is never the default, because a measurement taken on a
 * shared or thermally throttled host describes that moment rather than the machine.
 *
 * <h2>What is scaled</h2>
 *
 * <ul>
 * <li>{@link Config#INTEGRATE_RUBI_RULE_TIMELIMIT_SECONDS} - the limit the Rubi rules use
 * internally</li>
 * <li>{@link Config#INTEGRATE_RUBI_TIMELIMIT_MILLIS} - the budget for one run of the Rubi
 * rules</li>
 * <li>{@link Config#INTEGRATE_RATIONAL_TIMELIMIT_MILLIS} - the rational integration stage</li>
 * <li>{@link Config#INTEGRATE_RISCH_NORMAN_TIMELIMIT_MILLIS} - the Risch-Norman stage</li>
 * <li>the deadline and the step limits of the whole <code>DSolve</code> cascade, through
 * <code>DSolveContext</code></li>
 * <li>the bounded <code>Simplify</code> of <code>Wronskian</code>, the bounded
 * <code>Series</code> of <code>AsymptoticRSolveValue</code>, and the denesting check of
 * <code>RadicalCoefficients</code></li>
 * </ul>
 *
 * <h2>What is not scaled</h2>
 *
 * <ul>
 * <li>{@link Config#SERVER_REQUEST_TIMEOUT_SECONDS} and the timeouts of the consoles and the
 * Discord bot: these say how long someone is willing to wait, which does not change with the
 * hardware</li>
 * <li>a <code>TimeConstrained()</code> written by the user, for the same reason</li>
 * <li>{@link Config#INTEGRATE_RUBI_TIMELIMIT_SHARE}, which is a fraction and not a duration</li>
 * <li>{@link Config#TIME_CONSTRAINED_SLEEP_MILLISECONDS} and the grace period of
 * <code>TimeConstrainedExecutor</code>, which are polling intervals</li>
 * </ul>
 */
public final class MachineProfile {

  /** System property holding the factor itself, for example <code>-Dsymja.timeScale=2.5</code>. */
  public static final String TIME_SCALE_PROPERTY = "symja.timeScale";

  /** Environment variable holding the factor itself. */
  public static final String TIME_SCALE_ENV = "SYMJA_TIME_SCALE";

  /**
   * System property naming a {@link Preset}, for example
   * <code>-Dsymja.machineProfile=slow</code>.
   */
  public static final String PROFILE_PROPERTY = "symja.machineProfile";

  /** Environment variable naming a {@link Preset}. */
  public static final String PROFILE_ENV = "SYMJA_MACHINE_PROFILE";

  /**
   * The largest factor which is accepted. A budget multiplied by this stays far away from
   * overflowing the <code>long</code> arithmetic of its callers, and a factor beyond it is far more
   * likely to be a typo than a machine which is a thousand times slower than an Apple M5.
   */
  public static final double MAX_SCALE = 1000.0;

  /** The smallest factor which is accepted. */
  public static final double MIN_SCALE = 0.001;

  /**
   * Named factors, relative to the machine the budgets were tuned on.
   *
   * <p>
   * A preset is a coarse instrument on purpose. Someone who knows what their machine does can pass
   * the factor itself; a preset is for someone who only knows that this one is slower.
   */
  public enum Preset {
    /**
     * A machine faster than the one the budgets were tuned on. Note that shortening a budget can
     * end an evaluation which would have finished, so this is worth choosing only to make a
     * hopeless evaluation give up sooner.
     */
    FAST(0.75),

    /** The machine the budgets were tuned on: the values are used as they are written. */
    NORMAL(1.0),

    /** A machine appreciably slower than the one the budgets were tuned on. */
    SLOW(3.0),

    /** Measure this machine rather than assume it, see {@link Config#calibrateTimeScale()}. */
    AUTO(Double.NaN);

    /** The factor this preset stands for, or {@link Double#NaN} for {@link #AUTO}. */
    public final double scale;

    private Preset(double scale) {
      this.scale = scale;
    }

    /** The preset of that name, ignoring case and surrounding space, or <code>null</code>. */
    public static Preset parse(String name) {
      if (name == null) {
        return null;
      }
      String trimmed = name.trim();
      for (Preset preset : values()) {
        if (preset.name().equalsIgnoreCase(trimmed)) {
          return preset;
        }
      }
      return null;
    }
  }

  /**
   * <code>NaN</code> until the configured sources have been read. Resolving them lazily rather than
   * in the initializer keeps the <code>auto</code> profile - which measures the machine, and so
   * takes a moment - out of the class loading of whichever budget happens to be read first.
   */
  private static volatile double scale = Double.NaN;

  private MachineProfile() {}

  /**
   * The factor every budget is multiplied by. <code>1.0</code> means the budgets are used as they
   * were tuned.
   */
  public static double getScale() {
    double current = scale;
    if (Double.isNaN(current)) {
      synchronized (MachineProfile.class) {
        current = scale;
        if (Double.isNaN(current)) {
          current = parse(property(TIME_SCALE_PROPERTY), environment(TIME_SCALE_ENV),
              property(PROFILE_PROPERTY), environment(PROFILE_ENV));
          scale = current;
        }
      }
    }
    return current;
  }

  /**
   * Sets the factor, overriding whatever the properties and the environment say.
   *
   * <p>
   * Note that the limit the Rubi rules use internally is bound to a symbol when those rules are
   * loaded, which happens on the first call of <code>Integrate()</code>. A factor set before that
   * reaches every budget; a factor set afterwards reaches every budget except that one, which then
   * has to be assigned directly with <code>F.ISet(F.$s("§$timelimit"), F.ZZ(seconds))</code>.
   *
   * @param newScale a finite factor in <code>[{@value #MIN_SCALE}, {@value #MAX_SCALE}]</code>
   * @throws IllegalArgumentException if the factor is not one which can be used
   */
  public static void setScale(double newScale) {
    if (!isUsable(newScale)) {
      throw new IllegalArgumentException("MachineProfile: the time scale factor has to be finite "
          + "and between " + MIN_SCALE + " and " + MAX_SCALE + ", not " + newScale);
    }
    scale = newScale;
  }

  /**
   * Sets the factor to the one the preset stands for. {@link Preset#AUTO} measures this machine
   * with {@link Config#calibrateTimeScale()}.
   */
  public static void setPreset(Preset preset) {
    if (preset == null) {
      throw new IllegalArgumentException("MachineProfile: no preset given");
    }
    setScale(preset == Preset.AUTO ? Config.calibrateTimeScale() : preset.scale);
  }

  /**
   * Forgets a factor which was set programmatically and reads the properties and the environment
   * again. Mainly for tests, which have to leave the factor as they found it because the whole
   * suite shares one virtual machine.
   */
  public static void reset() {
    scale = Double.NaN;
  }

  /**
   * The given number of seconds, on this machine.
   *
   * <p>
   * A budget of zero or less is left alone: every caller reads such a value as "no limit" rather
   * than as a duration. Anything else is at least one second, because
   * <code>TimeConstrained()</code> rounds its limit up to a whole second and rejects a limit which
   * is not positive.
   *
   * @param baseSeconds the budget on the machine the algorithm was tuned on
   */
  public static long seconds(long baseSeconds) {
    if (baseSeconds <= 0L) {
      return baseSeconds;
    }
    double scaled = baseSeconds * getScale();
    if (scaled >= Long.MAX_VALUE) {
      return Long.MAX_VALUE;
    }
    return Math.max(1L, Math.round(scaled));
  }

  /**
   * The given number of seconds, on this machine, as an <code>int</code>.
   *
   * @param baseSeconds the budget on the machine the algorithm was tuned on
   * @see #seconds(long)
   */
  public static int seconds(int baseSeconds) {
    long scaled = seconds((long) baseSeconds);
    if (scaled > Integer.MAX_VALUE) {
      return Integer.MAX_VALUE;
    }
    return (int) scaled;
  }

  /**
   * The given number of milliseconds, on this machine.
   *
   * <p>
   * A budget of zero or less is left alone, because that is how a caller switches its budget off.
   *
   * @param baseMillis the budget on the machine the algorithm was tuned on
   */
  public static long millis(long baseMillis) {
    if (baseMillis <= 0L) {
      return baseMillis;
    }
    double scaled = baseMillis * getScale();
    if (scaled >= Long.MAX_VALUE) {
      return Long.MAX_VALUE;
    }
    return Math.max(1L, Math.round(scaled));
  }

  /**
   * The factor the given configuration asks for, in the documented order of precedence, or
   * <code>1.0</code> if none of the sources says anything which can be used.
   *
   * <p>
   * A source which says something unusable is reported and skipped rather than rounded into range,
   * so that a mistyped factor is noticed instead of quietly becoming a different one.
   */
  static double parse(String scaleProperty, String scaleEnvironment, String profileProperty,
      String profileEnvironment) {
    double fromProperty = parseScale(scaleProperty, TIME_SCALE_PROPERTY);
    if (!Double.isNaN(fromProperty)) {
      return fromProperty;
    }
    double fromEnvironment = parseScale(scaleEnvironment, TIME_SCALE_ENV);
    if (!Double.isNaN(fromEnvironment)) {
      return fromEnvironment;
    }
    double fromProfileProperty = parsePreset(profileProperty, PROFILE_PROPERTY);
    if (!Double.isNaN(fromProfileProperty)) {
      return fromProfileProperty;
    }
    double fromProfileEnvironment = parsePreset(profileEnvironment, PROFILE_ENV);
    if (!Double.isNaN(fromProfileEnvironment)) {
      return fromProfileEnvironment;
    }
    return 1.0;
  }

  /** The factor the given text holds, or <code>NaN</code> if it holds none which can be used. */
  private static double parseScale(String text, String source) {
    if (text == null || text.trim().isEmpty()) {
      return Double.NaN;
    }
    double value;
    try {
      value = Double.parseDouble(text.trim());
    } catch (NumberFormatException nfe) {
      report(source, text, "not a number");
      return Double.NaN;
    }
    if (!isUsable(value)) {
      report(source, text, Double.isNaN(value) || Double.isInfinite(value) //
          ? "not a factor"
          : "outside " + MIN_SCALE + " to " + MAX_SCALE);
      return Double.NaN;
    }
    return value;
  }

  /** The factor the named preset stands for, or <code>NaN</code> if there is no such preset. */
  private static double parsePreset(String text, String source) {
    if (text == null || text.trim().isEmpty()) {
      return Double.NaN;
    }
    Preset preset = Preset.parse(text);
    if (preset == null) {
      report(source, text, "expected one of fast, normal, slow, auto");
      return Double.NaN;
    }
    if (preset == Preset.AUTO) {
      return Config.calibrateTimeScale();
    }
    return preset.scale;
  }

  private static boolean isUsable(double value) {
    return !Double.isNaN(value) && !Double.isInfinite(value) && value >= MIN_SCALE
        && value <= MAX_SCALE;
  }

  /**
   * Says on <code>System.err</code> that a setting was ignored. Deliberately not a logger: this
   * runs while the configuration of the system is still being read.
   */
  private static void report(String source, String value, String reason) {
    System.err.println(
        "MachineProfile: ignoring " + source + "=\"" + value + "\" (" + reason + ")");
  }

  /** The system property, or <code>null</code> if it is not set or cannot be read. */
  private static String property(String name) {
    try {
      return System.getProperty(name);
    } catch (SecurityException se) {
      return null;
    }
  }

  /** The environment variable, or <code>null</code> if it is not set or cannot be read. */
  private static String environment(String name) {
    try {
      return System.getenv(name);
    } catch (SecurityException se) {
      return null;
    }
  }
}
