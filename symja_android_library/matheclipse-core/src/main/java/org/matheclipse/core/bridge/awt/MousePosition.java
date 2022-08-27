// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.awt;

import java.awt.MouseInfo;
import java.awt.Point;
import java.util.Optional;

public class MousePosition {

  public static Optional<Point> get() {
    try {
      // can test with GraphicsEnvironment.isHeadless()
      return Optional.of(MouseInfo.getPointerInfo().getLocation());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return Optional.empty();
  }
}
