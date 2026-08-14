package org.matheclipse.core.graphics.webgl;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The {@code Lighting} option, turned into a list of lights the renderer can install.
 *
 * <p>
 * The default follows {@code "Automatic"} setup: a dim ambient term plus four coloured directional
 * lights placed around the camera. Colouring the lights is what gives a surface its characteristic
 * warm-to-cool shading across a curve, and a plain white setup looks noticeably flatter, so the
 * colours are kept even though they are unusual for a viewer.
 */
public final class Lighting3D {

  private Lighting3D() {}

  /** One light source, in the form the renderer consumes. */
  public static final class Light {
    final String type;
    final int color;
    double intensity = 1.0;
    double[] position;
    double[] target;
    double angle;
    double decay;
    double distance;
    /** True when the light travels with the camera rather than the scene. */
    boolean fixedToCamera = false;

    Light(String type, int color) {
      this.type = type;
      this.color = color;
    }
  }

  /** Parse a {@code Lighting} value; {@code null} selects the automatic setup. */
  public static List<Light> parse(IExpr value) {
    List<Light> lights = new ArrayList<>();
    if (value == null) {
      return automatic();
    }
    if (value.isNone()) {
      // no lights at all: surfaces are lit only by their own Glow
      return lights;
    }
    if (value.isString()) {
      String name = GraphicsOptions3D.unquote(value.toString());
      if ("Neutral".equalsIgnoreCase(name)) {
        return neutral();
      }
      if ("Accent".equalsIgnoreCase(name)) {
        return accent();
      }
      return automatic();
    }
    if (value.isList()) {
      IAST list = (IAST) value;
      // Lighting -> {{"Ambient", colour}, ...} uses string tagged pairs
      for (int i = 1; i <= list.argSize(); i++) {
        IExpr spec = list.get(i);
        if (spec.isNone()) {
          continue;
        }
        if (spec.isAST()) {
          parseLight((IAST) spec, lights);
        }
      }
      if (lights.isEmpty()) {
        return automatic();
      }
      return lights;
    }
    return automatic();
  }

  private static void parseLight(IAST spec, List<Light> lights) {
    ISymbol head = spec.topHead();
    String type = head.toString();
    if (spec.isList()) {
      parseTaggedLight(spec, lights);
      return;
    }
    Color color = spec.argSize() >= 1 ? ColorUtil.parse(spec.arg1()) : Color.WHITE;
    if (color == null) {
      color = Color.WHITE;
    }
    int rgb = color.getRGB() & 0x00FFFFFF;

    switch (type) {
      case "AmbientLight":
        lights.add(new Light("AmbientLight", rgb));
        break;
      case "DirectionalLight": {
        Light light = new Light("DirectionalLight", rgb);
        double[] position = spec.argSize() >= 2 ? GraphicsOptions3D.vector(spec.arg2()) : null;
        if (position == null && spec.argSize() >= 2 && spec.arg2().isList()) {
          // DirectionalLight[colour, {p1, p2}] gives the light a direction as a segment
          IAST pair = (IAST) spec.arg2();
          double[] from = pair.argSize() >= 1 ? GraphicsOptions3D.vector(pair.arg1()) : null;
          double[] to = pair.argSize() >= 2 ? GraphicsOptions3D.vector(pair.arg2()) : null;
          if (from != null && to != null) {
            position = from;
            light.target = to;
          }
        }
        light.position = position == null ? new double[] {10, 10, 10} : position;
        lights.add(light);
        break;
      }
      case "PointLight": {
        Light light = new Light("PointLight", rgb);
        double[] position = spec.argSize() >= 2 ? GraphicsOptions3D.vector(spec.arg2()) : null;
        light.position = position == null ? new double[] {0, 0, 1} : position;
        if (spec.argSize() >= 3) {
          light.distance = ColorUtil.dbl(spec.arg3(), 0.0);
        }
        lights.add(light);
        break;
      }
      case "SpotLight": {
        Light light = new Light("SpotLight", rgb);
        double[] position = spec.argSize() >= 2 ? GraphicsOptions3D.vector(spec.arg2()) : null;
        light.position = position == null ? new double[] {0, 0, 1} : position;
        double[] direction = spec.argSize() >= 3 ? GraphicsOptions3D.vector(spec.arg3()) : null;
        if (direction == null) {
          direction = new double[] {0, 0, -1};
        }
        light.target = new double[] {light.position[0] + direction[0],
            light.position[1] + direction[1], light.position[2] + direction[2]};
        light.angle = spec.argSize() >= 4 ? ColorUtil.dbl(spec.arg4(), Math.PI / 4) : Math.PI / 4;
        if (spec.argSize() >= 5) {
          light.distance = ColorUtil.dbl(spec.get(5), 0.0);
        }
        if (spec.argSize() >= 6) {
          light.decay = ColorUtil.dbl(spec.get(6), 2.0);
        }
        lights.add(light);
        break;
      }
      default:
        break;
    }
  }

  /** {@code {"Ambient", colour}} and {@code {"Directional", colour, position}}. */
  private static void parseTaggedLight(IAST list, List<Light> lights) {
    if (list.argSize() < 2 || !list.arg1().isString()) {
      return;
    }
    String tag = GraphicsOptions3D.unquote(list.arg1().toString());
    Color color = ColorUtil.parse(list.arg2());
    int rgb = color == null ? 0xFFFFFF : (color.getRGB() & 0x00FFFFFF);
    double[] position = list.argSize() >= 3 ? GraphicsOptions3D.vector(list.arg3()) : null;
    if (position == null && list.argSize() >= 3 && list.arg3().isList()) {
      IAST pair = (IAST) list.arg3();
      if (pair.argSize() >= 1) {
        position = GraphicsOptions3D.vector(pair.arg1());
      }
    }
    switch (tag) {
      case "Ambient":
        lights.add(new Light("AmbientLight", rgb));
        break;
      case "Directional": {
        Light light = new Light("DirectionalLight", rgb);
        light.position = position == null ? new double[] {10, 10, 10} : position;
        lights.add(light);
        break;
      }
      case "Point": {
        Light light = new Light("PointLight", rgb);
        light.position = position == null ? new double[] {0, 0, 1} : position;
        lights.add(light);
        break;
      }
      case "Spot": {
        Light light = new Light("SpotLight", rgb);
        light.position = position == null ? new double[] {0, 0, 1} : position;
        light.angle = Math.PI / 4;
        lights.add(light);
        break;
      }
      default:
        break;
    }
  }

  /**
   * {@code Automatic}: a warm ambient and four strongly coloured directional lights.
   *
   * <p>
   * The colours are not decoration. An unstyled solid is white, and it is these lights that give it
   * its shape: a plain {@code Graphics3D[Cuboid[]]} comes out with a warm top, a magenta face
   * towards the viewer and a blue one to the right, which is how the three visible faces of a white
   * box can be told apart at all. Reproducing that means reproducing these values.
   */
  public static List<Light> automatic() {
    List<Light> lights = new ArrayList<>();
    lights.add(new Light("AmbientLight", rgb(0.4, 0.2, 0.2)));
    addCameraLight(lights, 0.0, 0.18, 0.5, new double[] {2, 0, 2});
    addCameraLight(lights, 0.18, 0.5, 0.18, new double[] {2, 2, 3});
    addCameraLight(lights, 0.5, 0.18, 0.0, new double[] {0, 2, 2});
    addCameraLight(lights, 0.0, 0.0, 0.18, new double[] {0, 0, 2});
    return lights;
  }

  /** White light, which shows a surface's own colour without tinting it. */
  public static List<Light> neutral() {
    List<Light> lights = new ArrayList<>();
    lights.add(new Light("AmbientLight", rgb(0.35, 0.35, 0.35)));
    addCameraLight(lights, 0.37, 0.37, 0.37, new double[] {2, 0, 2});
    addCameraLight(lights, 0.37, 0.37, 0.37, new double[] {2, 2, 2});
    addCameraLight(lights, 0.37, 0.37, 0.37, new double[] {0, 2, 2});
    return lights;
  }

  /** A single white key light over the viewer's shoulder, see {@code "Accent"}. */
  public static List<Light> accent() {
    List<Light> lights = new ArrayList<>();
    addCameraLight(lights, 1.0, 1.0, 1.0, new double[] {1, 1, 4});
    return lights;
  }

  /**
   * Install a light at a position as {@code ImageScaled}.
   *
   * <p>
   * Those coordinates run from 0 to 1 across the displayed box, so the direction the light shines
   * from is the offset of the position from the centre of that box rather than the position itself.
   * The light travels with the camera, which is what keeps a surface lit the same way while it is
   * being turned.
   */
  private static void addCameraLight(List<Light> lights, double r, double g, double b,
      double[] imageScaled) {
    Light light = new Light("DirectionalLight", rgb(r, g, b));
    light.intensity = 1.0;
    light.position =
        new double[] {imageScaled[0] - 0.5, imageScaled[1] - 0.5, imageScaled[2] - 0.5};
    light.fixedToCamera = true;
    lights.add(light);
  }

  /** Pack a colour triple into the integer the renderer takes. */
  private static int rgb(double r, double g, double b) {
    return new Color((float) r, (float) g, (float) b).getRGB() & 0x00FFFFFF;
  }


  /** Write the lights into the scene JSON. */
  public static void write(ArrayNode array, List<Light> lights) {
    for (Light light : lights) {
      ObjectNode node = array.addObject();
      node.put("type", light.type);
      node.put("color", light.color);
      node.put("intensity", light.intensity);
      node.put("fixedToCamera", light.fixedToCamera);
      if (light.position != null) {
        node.putArray("position").add(light.position[0]).add(light.position[1])
            .add(light.position[2]);
      }
      if (light.target != null) {
        node.putArray("target").add(light.target[0]).add(light.target[1]).add(light.target[2]);
      }
      node.put("angle", light.angle);
      node.put("distance", light.distance);
      node.put("decay", light.decay);
    }
  }
}
