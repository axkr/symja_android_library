package org.matheclipse.logging;

import java.io.Serializable;
import java.util.function.Consumer;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

@Plugin(name = "ThreadLocalNotifier", category = Core.CATEGORY_NAME,
    elementType = Appender.ELEMENT_TYPE, printObject = true)
public class ThreadLocalNotifyingAppender extends AbstractAppender {

  @PluginBuilderFactory
  public static <B extends Builder<B>> B newBuilder() {
    return new Builder<B>().asBuilder();
  }

  public static class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B>
      implements org.apache.logging.log4j.core.util.Builder<ThreadLocalNotifyingAppender> {
    public ThreadLocalNotifyingAppender build() {
      return new ThreadLocalNotifyingAppender(getName(), getFilter(), getLayout(),
          isIgnoreExceptions(), getPropertyArray());
    }
  }

  protected ThreadLocalNotifyingAppender(final String name, final Filter filter,
      final Layout<? extends Serializable> layout, final boolean ignoreExceptions,
      final Property[] properties) {
    super(name, filter, null, true, Property.EMPTY_ARRAY);
  }

  @Override
  public void append(LogEvent event) {
    Consumer<LogEvent> notifier = NOTIFIER.get();
    if (notifier != null) {
      notifier.accept(event);
    }
  }

  private static final ThreadLocal<Consumer<LogEvent>> NOTIFIER = new ThreadLocal<>();

  public static ThreadLocalNotifierClosable addLogEventNotifier(Consumer<LogEvent> notifier) {
    if (NOTIFIER.get() != null) {
      throw new IllegalStateException("LogEvent-notifier already set for this thread");
    }
    NOTIFIER.set(notifier);
    return CLOSER;
  }

  private static final ThreadLocalNotifierClosable CLOSER = NOTIFIER::remove;

  public static interface ThreadLocalNotifierClosable extends AutoCloseable {
    @Override
    public void close();
  }
}
