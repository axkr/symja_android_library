// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.awt;

import java.awt.event.MouseEvent;

@FunctionalInterface
public interface LazyMouseListener {
  /** @param mouseEvent */
  void lazyClicked(MouseEvent mouseEvent);

  /** @param mouseEvent */
  default void lazyDragged(MouseEvent mouseEvent) {
    // empty by default
  }
}
