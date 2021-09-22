package org.matheclipse.core.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.exception.MemoryLimitExceeded;

@SuppressWarnings("unused")
public class OperationSystem {
  private static final Logger LOGGER = LogManager.getLogger();

  public static int toomCook3Threshold = Integer.MAX_VALUE;

  /** Is running on JVM or not */
  private static boolean jvm = true;
  /** Maximum memory can use by this process */
  private static float maxMemoryUsageFactor = 0.9f;
  /**
   * This field should be set to true if the application receive memory warning, typically in
   * AppDelegate.swift Before calculating, this field should set to true
   */
  private static boolean memoryWarning = false;
  /** Maximum number of bytes the heap can expand to. Negative values mean default JVM value */
  private static long maxMemory = -1;

  /** Set <code>true</code> to cancel all calculation tasks */
  private static boolean abortSystem = false;

  static {
    setJvm(true);
  }

  public static boolean isJvm() {
    return jvm;
  }

  public static void setJvm(boolean jvm) {
    OperationSystem.jvm = jvm;
  }

  public static float getMemoryUsageFactor() {
    return maxMemoryUsageFactor;
  }

  public static void setMemoryUsageFactor(float memoryUsageFactor) {
    OperationSystem.maxMemoryUsageFactor = memoryUsageFactor;
  }

  public static boolean isMemoryWarning() {
    return memoryWarning;
  }

  public static void setMemoryWarning(boolean memoryWarning) {
    OperationSystem.memoryWarning = memoryWarning;
  }

  public static void checkMemory() {
    checkMemory(0);
  }

  public static void checkInterrupt() {
    if (abortSystem) {
      throw new RuntimeException("System is interrupted");
    }
  }

  public static void setInterrupt(boolean interrupt) {
    OperationSystem.abortSystem = interrupt;
  }

  public static long getMaxMemory() {
    return maxMemory;
  }

  public static void setMaxMemory(long maxMemory) {
    OperationSystem.maxMemory = maxMemory;
  }

  /**
   * @param additionalMemoryInBytes approximately memory will be allocate after perform some
   *     operations. Unit of bytes
   */
  public static void checkMemory(long additionalMemoryInBytes) {
    if (isJvm()) {
      Runtime runtime = Runtime.getRuntime();
      if (maxMemory <= 0) {
        maxMemory = runtime.maxMemory();
      }
      long usedMemory = runtime.totalMemory() - runtime.freeMemory() + additionalMemoryInBytes;

      if (LOGGER.isDebugEnabled()) {
        printMemoryUsage(maxMemory, usedMemory);
      }

      // value is valid
      if (maxMemory > 0 && maxMemory < Long.MAX_VALUE && usedMemory > 0) {
        float usageFactor = (float) usedMemory / maxMemory;
        if (usageFactor < 1.0f && usageFactor > maxMemoryUsageFactor) {
          LOGGER.debug("usedMemory = {}; maxMemory = {}", usedMemory, maxMemory);
          throw new OutOfMemoryError("Out of memory");
        }
      }
    } else {
      if (maxMemory <= 0) {
        return;
      }
      Runtime runtime = Runtime.getRuntime();
      long usedMemory = runtime.totalMemory() + additionalMemoryInBytes;
      if (LOGGER.isDebugEnabled()) {
        printMemoryUsage(maxMemory, usedMemory);
      }
      if (usedMemory > maxMemory || memoryWarning) {
        LOGGER.debug("usedMemory = {}; maxMemory = {}", usedMemory, maxMemory);
        throw new OutOfMemoryError("Out of memory");
      }
    }
  }

  private static void printMemoryUsage(long maxMemory, long usageMemory) {
    int max = 50;
    int usage = (int) (((float) usageMemory / maxMemory) * max);
    StringBuilder str = new StringBuilder("[");
    for (int i = 1; i <= max; i++) {
      if (i <= usage) {
        str.append("=");
      } else {
        str.append(" ");
      }
    }
    str.append("] ");
    str.append(" ").append(toMegabytes(usageMemory)).append("/").append(toMegabytes(maxMemory));
    LOGGER.debug(str);
  }

  private static String toMegabytes(long bytes) {
    return (bytes / 1024 / 1024) + " MB";
  }

  public static void checkMultiplicationOperation(int magLength1, long magLength2) {
    LOGGER.debug("magLength1 = {}", magLength1);
    LOGGER.debug("magLength2 = {}", magLength2);

    if (magLength1 > toomCook3Threshold && magLength2 > toomCook3Threshold) {
      throw new MemoryLimitExceeded(
          "toomCook3Threshold "
              + toomCook3Threshold
              + " limit exceeded. "
              + "magLength1 = "
              + magLength1
              + "; magLength2 = "
              + magLength2);
    }
  }
}
