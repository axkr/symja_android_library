// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.gfx;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.time.LocalTime;
import org.matheclipse.core.bridge.awt.RenderQuality;

public enum LocalTimeDisplay {
  INSTANCE;

  private final int wid = 52;
  private final int hourRad = 32;
  private final int hourRadIn = 7;
  private final int minRad = 45;
  private final int minRadAl = 8;
  private final int minRadOut = 48;
  private final int minRadIn = 40;
  private final int secRadOut = 32;
  private final int secRadIn = 15;
  private final int secCirc = 10;

  /** @param graphics
   * @param localTime
   * @param center */
  public void draw(Graphics2D graphics, LocalTime localTime, Point center) {
    RenderQuality.setQuality(graphics);
    graphics.setColor(Color.WHITE);
    graphics.fillArc(center.x - wid, center.y - wid, 2 * wid, 2 * wid, 0, 360);
    int hms_h = localTime.getHour();
    int hms_m = localTime.getMinute();
    int hms_s = localTime.getSecond();
    int hms_n = localTime.getNano();
    final double h = ((hms_h + hms_m / 60.0) / 12.0) * 2 * Math.PI;
    final double m = ((hms_m + hms_s / 60.0) / 60.0) * 2 * Math.PI;
    final double s = (hms_s + hms_n * 1E-9) / 60.0 * 2 * Math.PI;
    // Draw the hands of the clock
    // graphics2d.drawLine(c.x, c.y, c.x + Math.cos(m) * minutesRadius, cy + sin(m) * minutesRadius);
    graphics.setColor(Color.BLACK); // new Color(128, 128, 128, 255)
    Stroke stroke = graphics.getStroke();
    graphics.setStroke(new BasicStroke(4));
    {
      double dx = +Math.sin(h);
      double dy = -Math.cos(h);
      double cx = center.x + dx * hourRad;
      double cy = center.y + dy * hourRad;
      Shape shape = new Line2D.Double(center.x - hourRadIn * dx, center.y - hourRadIn * dy, cx, cy);
      graphics.draw(shape);
    }
    graphics.setStroke(new BasicStroke(3));
    {
      double dx = +Math.sin(m);
      double dy = -Math.cos(m);
      double cx = center.x + dx * minRad;
      double cy = center.y + dy * minRad;
      Shape shape = new Line2D.Double(center.x - minRadAl * dx, center.y - minRadAl * dy, cx, cy);
      graphics.draw(shape);
    }
    // Draw the minute ticks
    graphics.setStroke(new BasicStroke(2));
    for (int a = 0; a < 360; a += 6)
      secAt(graphics, center, a);
    graphics.setStroke(new BasicStroke(3));
    for (int a = 0; a < 360; a += 6 * 5)
      dotAt(graphics, center, a);
    graphics.setColor(new Color(64, 64, 192));
    graphics.setColor(Color.RED);
    graphics.setStroke(new BasicStroke(1.5f));
    {
      double dx = +Math.sin(s);
      double dy = -Math.cos(s);
      double cx = center.x + dx * secRadOut;
      double cy = center.y + dy * secRadOut;
      Shape shape = new Line2D.Double(center.x - secRadIn * dx, center.y - secRadIn * dy, cx, cy);
      graphics.draw(shape);
      graphics.fillArc((int) cx - secCirc / 2, (int) cy - secCirc / 2, secCirc, secCirc, 0, 360);
    }
    graphics.setStroke(stroke);
  }

  private void dotAt(Graphics2D graphics, Point c, int a) {
    double angle = a * Math.PI / 180;
    double x1 = c.x + Math.cos(angle) * minRadOut;
    double y1 = c.y + Math.sin(angle) * minRadOut;
    double x2 = c.x + Math.cos(angle) * minRadIn;
    double y2 = c.y + Math.sin(angle) * minRadIn;
    Shape shape = new Line2D.Double(x1, y1, x2, y2);
    graphics.draw(shape);
  }

  private void secAt(Graphics2D graphics, Point c, int a) {
    double angle = a * Math.PI / 180;
    double x1 = c.x + Math.cos(angle) * minRadOut;
    double y1 = c.y + Math.sin(angle) * minRadOut;
    double x2 = c.x + Math.cos(angle) * (minRadOut - 1);
    double y2 = c.y + Math.sin(angle) * (minRadOut - 1);
    Shape shape = new Line2D.Double(x1, y1, x2, y2);
    graphics.draw(shape);
  }
}