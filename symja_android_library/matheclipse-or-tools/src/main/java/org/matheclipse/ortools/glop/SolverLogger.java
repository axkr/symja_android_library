// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: ported from ortools/util/logging.h C++ to
// Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * The search log of the solvers. Disabled by default; when enabled the lines go to
 * <code>System.out</code> or to <code>java.util.logging</code>, and to an optional callback.
 */
public final class SolverLogger {

  private static final Logger LOGGER = Logger.getLogger(SolverLogger.class.getName());

  private boolean enabled;
  private boolean logToStdOut = true;
  private Consumer<String> callback;

  /** Enables or disables the log. */
  public void enableLogging(boolean enable) {
    this.enabled = enable;
  }

  /** Chooses between standard output and <code>java.util.logging</code>. */
  public void setLogToStdOut(boolean logToStdOut) {
    this.logToStdOut = logToStdOut;
  }

  /** Adds a callback receiving each line; <code>null</code> removes it. */
  public void setCallback(Consumer<String> callback) {
    this.callback = callback;
  }

  /** Returns true if logging is enabled. */
  public boolean loggingIsEnabled() {
    return enabled;
  }

  /** Logs the concatenation of the parts. */
  public void log(Object... parts) {
    if (!enabled) {
      return;
    }
    StringBuilder sb = new StringBuilder();
    for (Object p : parts) {
      sb.append(p);
    }
    final String line = sb.toString();
    if (callback != null) {
      callback.accept(line);
    }
    if (logToStdOut) {
      System.out.println(line);
    } else {
      LOGGER.info(line);
    }
  }
}
