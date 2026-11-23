package org.matheclipse.web;

import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

/**
 * The log4j configuration of the notebook kernel in the browser, set in code before anything logs.
 *
 * <p>
 * The server's configuration is matheclipse-logging's <code>log4j2.properties</code>: a console
 * appender for WARN and below, one for ERROR, and the <code>ThreadLocalNotifier</code> appender
 * that <code>org.matheclipse.io.servlet.ServletLogging</code> uses to copy ERROR events
 * of the evaluating thread into the JSON of the cell being answered - that is how, for instance,
 * <code>Import("missing.csv")</code> tells the reader that the file does not exist. In the browser
 * the console is the developer console of the page, and the server's configuration floods it: every
 * input at WARN (<code>In::...</code>) and full stack traces (a FileNotFoundException with the path
 * of the session's directory under <code>/files/</code>).
 *
 * <p>
 * So the kernel keeps log4j-core and the <code>ThreadLocalNotifier</code> appender exactly as they
 * are - the cells get the same messages as from a server - and replaces only what goes to the
 * console: ERROR and above, one line each, without the stack trace. A WARN event is not even
 * created, because the root logger is at ERROR; the cell notifier forwards ERROR and above only, so
 * it misses nothing by that.
 *
 * <p>
 * Swapping log4j-core for log4j-api's <code>SimpleLoggerContextFactory</code> (as the spike's
 * <code>SymjaCheerpJ</code> does) was measured and rejected: the cells then lose those messages,
 * and without log4j-core on the class path every evaluation fails in ServletLogging.
 */
final class BrowserLogging {

  /** The name of the appender {@link org.matheclipse.logging.ThreadLocalNotifyingAppender} is. */
  private static final String THREAD_LOCAL_APPENDER = "THREAD_LOCAL_NOTIFIER";

  private BrowserLogging() {}

  /**
   * Install the configuration. Called first thing in {@link NotebookKernel#start()}: if nothing has
   * logged yet, log4j starts with this configuration and never reads
   * <code>log4j2.properties</code>; if something has, its configuration is replaced.
   */
  static void configure() {
    Configuration configuration = build();
    LoggerContext context = Configurator.initialize(configuration);
    if (context != null && context.getConfiguration() != configuration) {
      // a logger context existed already (something logged before start()): replace its
      // configuration; a configuration object belongs to one context, hence a fresh one
      Configurator.reconfigure(build());
    }
  }

  /** The configuration described in the class comment. */
  private static Configuration build() {
    ConfigurationBuilder<BuiltConfiguration> builder =
        ConfigurationBuilderFactory.newConfigurationBuilder();
    builder.setConfigurationName("SymjaBrowserKernel");
    // log4j's own status messages: only its errors
    builder.setStatusLevel(Level.ERROR);

    // the page's console: one line per ERROR, no stack trace (alwaysWriteExceptions=false and no
    // %ex in the pattern), no timestamps - the browser console has its own
    AppenderComponentBuilder console = builder.newAppender("CONSOLE", "Console")
        .addAttribute("target", ConsoleAppender.Target.SYSTEM_ERR)
        .add(builder.newLayout("PatternLayout").addAttribute("pattern", "%-5p %c{1} - %m%n")
            .addAttribute("alwaysWriteExceptions", false))
        .add(builder.newFilter("ThresholdFilter", Filter.Result.ACCEPT, Filter.Result.DENY)
            .addAttribute("level", Level.ERROR));
    builder.add(console);

    // the cell notifier of matheclipse-logging, found through its Log4j2Plugins.dat (no package
    // scanning, which log4j has deprecated)
    builder.add(builder.newAppender(THREAD_LOCAL_APPENDER, "ThreadLocalNotifier"));

    builder.add(builder.newRootLogger(Level.ERROR).add(builder.newAppenderRef("CONSOLE"))
        .add(builder.newAppenderRef(THREAD_LOCAL_APPENDER)));
    Configuration configuration = builder.build(false);
    // LoggerContext.setConfiguration puts the host name into the context properties of every
    // configuration (for ${hostName}) unless it is there already, and InetAddress.getLocalHost()
    // may block on a name lookup - 5 s on a Mac desktop, most of the kernel's start-up there.
    // Nothing here uses ${hostName}, so a fixed value spares the lookup.
    Map<String, String> contextProperties =
        configuration.getComponent(Configuration.CONTEXT_PROPERTIES);
    if (contextProperties != null) {
      contextProperties.putIfAbsent("hostName", "browser");
    }
    return configuration;
  }
}
