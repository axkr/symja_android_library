// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.awt;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class LazyMouse implements MouseListener, MouseMotionListener {
  public static final float TOLERANCE_DEFAULT = 3;
  // ---
  private Point pressedC = new Point(); // component
  private Point pressedS = new Point(); // screen
  private boolean isClick;
  private float tolerance = TOLERANCE_DEFAULT;
  private final LazyMouseListener lazyMouseListener;

  public LazyMouse(LazyMouseListener lazyMouseListener) {
    this.lazyMouseListener = lazyMouseListener;
  }

  public void setTolerance(float tolerance) {
    if (tolerance < 0)
      throw new IllegalArgumentException("tolerance is negative");
    this.tolerance = tolerance;
  }

  @Override
  public final void mousePressed(MouseEvent mouseEvent) {
    pressedC = mouseEvent.getPoint();
    pressedS = mouseEvent.getLocationOnScreen();
    isClick = true;
  }

  private boolean shortFromPressed(MouseEvent mouseEvent) {
    Point myPointC = mouseEvent.getPoint();
    Point myPointS = mouseEvent.getLocationOnScreen();
    return Math.hypot(myPointC.x - pressedC.x, myPointC.y - pressedC.y) <= tolerance && //
        Math.hypot(myPointS.x - pressedS.x, myPointS.y - pressedS.y) <= tolerance;
  }

  @Override
  public final void mouseReleased(MouseEvent mouseEvent) {
    isClick &= shortFromPressed(mouseEvent);
    if (isClick)
      lazyMouseListener.lazyClicked(mouseEvent);
  }

  @Override
  public final void mouseClicked(MouseEvent mouseEvent) {
    // this has to be empty!
  }

  @Override
  public final void mouseDragged(MouseEvent mouseEvent) {
    isClick &= shortFromPressed(mouseEvent);
    if (!isClick)
      lazyMouseListener.lazyDragged(mouseEvent);
  }

  @Override
  public void mouseMoved(MouseEvent mouseEvent) {
    // empty by default
  }

  @Override
  public void mouseEntered(MouseEvent mouseEvent) {
    // empty by default
  }

  @Override
  public void mouseExited(MouseEvent mouseEvent) {
    // empty by default
  }

  public void addListenersTo(Component component) {
    component.addMouseListener(this);
    component.addMouseMotionListener(this);
  }
}
