// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.awt;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;

public class ScreenInsets {

  /**
   * Remark: on Ubuntu: java.awt.Insets[top=24,left=49,bottom=0,right=0] on Fedora:
   * java.awt.Insets[top=32,left=0,bottom=0,right=0]
   * 
   * @param device
   * @param config
   * @return
   */
  public static Insets of(int device, int config) {
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] graphicsDevice = graphicsEnvironment.getScreenDevices();
    if (device < graphicsDevice.length) {
      GraphicsConfiguration[] graphicsConfiguration = graphicsDevice[device].getConfigurations();
      if (config < graphicsConfiguration.length)
        return Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration[config]);
    }
    return null;
  }

  public static Insets of(int device) {
    return of(device, 0);
  }
}
