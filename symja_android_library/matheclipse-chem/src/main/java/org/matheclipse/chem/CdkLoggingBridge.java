package org.matheclipse.chem;

import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Routes CDK's own logging into SLF4J, and through it into the Log4j configuration the rest of
 * Symja uses.
 *
 * <p>
 * Without this, CDK logs to the console behind the application's back. Its
 * {@link LoggingToolFactory} tries <code>Slf4jLoggingTool</code>, then <code>Log4jLoggingTool</code>
 * , and falls back to a <code>StdErrLogger</code> that writes straight to {@link System#err}. Both
 * of the preferred classes live in CDK artifacts this module deliberately does not depend on, so
 * the fallback is what runs - which is why the first <code>MoleculePlot3D</code> of a session used
 * to spray a hundred lines of <code>IteratingSDFReader ERROR: Absolute mass number should be
 * &gt;= 0</code> at the console. Those come from CDK's own bundled ring-template SD file as
 * <code>ModelBuilder3D</code> loads it; they are harmless, unactionable, and not ours to fix.
 *
 * <p>
 * Installing this bridge does not decide whether such messages are shown - it only puts that
 * decision back where every other logging decision in Symja is made, in
 * <code>log4j2.properties</code>.
 */
public final class CdkLoggingBridge implements ILoggingTool {

  private final Logger logger;

  private CdkLoggingBridge(Class<?> sourceClass) {
    this.logger = LoggerFactory.getLogger(sourceClass);
  }

  /** {@link LoggingToolFactory} instantiates an {@link ILoggingTool} through this method. */
  public static ILoggingTool create(Class<?> sourceClass) {
    return new CdkLoggingBridge(sourceClass);
  }

  /** Installs the bridge as CDK's logging implementation. Idempotent. */
  public static void install() {
    if (LoggingToolFactory.getLoggingToolClass() != CdkLoggingBridge.class) {
      LoggingToolFactory.setLoggingToolClass(CdkLoggingBridge.class);
    }
  }

  /** CDK passes a message followed by parts to be concatenated, not SLF4J-style placeholders. */
  private static String join(Object object, Object... objects) {
    StringBuilder buf = new StringBuilder(String.valueOf(object));
    for (Object part : objects) {
      buf.append(part);
    }
    return buf.toString();
  }

  @Override
  public void debug(Object object) {
    logger.debug("{}", object);
  }

  @Override
  public void debug(Object object, Object... objects) {
    if (logger.isDebugEnabled()) {
      logger.debug(join(object, objects));
    }
  }

  @Override
  public void error(Object object) {
    logger.error("{}", object);
  }

  @Override
  public void error(Object object, Object... objects) {
    if (logger.isErrorEnabled()) {
      logger.error(join(object, objects));
    }
  }

  @Override
  public void fatal(Object object) {
    logger.error("{}", object);
  }

  @Override
  public void info(Object object) {
    logger.info("{}", object);
  }

  @Override
  public void info(Object object, Object... objects) {
    if (logger.isInfoEnabled()) {
      logger.info(join(object, objects));
    }
  }

  @Override
  public void warn(Object object) {
    logger.warn("{}", object);
  }

  @Override
  public void warn(Object object, Object... objects) {
    if (logger.isWarnEnabled()) {
      logger.warn(join(object, objects));
    }
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  /**
   * The level is owned by the Log4j configuration, so CDK's own level calls are reported rather
   * than obeyed.
   */
  @Override
  public void setLevel(int level) {
    // intentionally ignored: log4j2.properties decides
  }

  @Override
  public int getLevel() {
    if (logger.isDebugEnabled()) {
      return ILoggingTool.DEBUG;
    }
    if (logger.isInfoEnabled()) {
      return ILoggingTool.INFO;
    }
    if (logger.isWarnEnabled()) {
      return ILoggingTool.WARN;
    }
    return logger.isErrorEnabled() ? ILoggingTool.ERROR : ILoggingTool.OFF;
  }

  /** Dumping the environment to the console is exactly the behaviour this bridge exists to stop. */
  @Override
  public void dumpSystemProperties() {
    // intentionally empty
  }

  @Override
  public void dumpClasspath() {
    // intentionally empty
  }

  @Override
  public void setStackLength(int length) {
    // stack depth is a Log4j layout concern
  }
}
