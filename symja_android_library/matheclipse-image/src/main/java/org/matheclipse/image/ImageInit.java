package org.matheclipse.image;

import javax.imageio.ImageIO;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.io.ImageFormatIO;
import org.matheclipse.image.builtin.ColorFunctions;
import org.matheclipse.image.builtin.ImageAdjustFunctions;
import org.matheclipse.image.builtin.ImageArithmeticFunctions;
import org.matheclipse.image.builtin.ImageGeometryFunctions;
import org.matheclipse.image.builtin.ImageFunctions;
import org.matheclipse.image.builtin.ImagePropertyFunctions;
import org.matheclipse.image.builtin.ImageStructureFunctions;
import org.matheclipse.image.io.ImageIOFormats;

public class ImageInit {
  public static void init() {
    // Import and Export gain every javax.imageio format, and return an Image object rather than a
    // matrix of pixel values. Core declares the interface, this module installs the implementation -
    // see org.matheclipse.core.io.ImageFormatIO.
    ImageIO.scanForPlugins();
    ImageFormatIO.install(new ImageIOFormats());

    ImageFunctions.initialize();
    ImagePropertyFunctions.initialize();
    ImageArithmeticFunctions.initialize();
    ImageStructureFunctions.initialize();
    ColorFunctions.initialize();
    ImageAdjustFunctions.initialize();
    ImageGeometryFunctions.initialize();

    // The plots are not installed from here. This module used to replace ArrayPlot and
    // ListDensityPlot with versions that drew a JFreeChart bitmap, which meant a plot looked
    // different depending on whether this module happened to be on the classpath, and lost every
    // option the core implementations understand. Both now come from matheclipse-core as
    // Graphics, and Image[...] turns any of them into a bitmap for the callers that need one.
    S.ImageCrop.setEvaluator(new org.matheclipse.image.builtin.ImageCrop());
    S.ReliefImage.setEvaluator(new org.matheclipse.image.builtin.ReliefImage());

  }
}
