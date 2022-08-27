// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.awt;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;

public class WindowClosed {

  /**
   * attaches given runnable to the given window, that will be executed as the window is disposed
   * 
   * @param window
   * @param runnable
   */
  public static void runs(Window window, Runnable runnable) {
    Objects.requireNonNull(runnable);
    window.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        runnable.run();
      }
    });
  }
}
