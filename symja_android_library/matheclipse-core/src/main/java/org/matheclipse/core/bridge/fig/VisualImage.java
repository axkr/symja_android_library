// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.fig;

import java.awt.image.BufferedImage;
import java.util.Objects;
import org.matheclipse.core.tensor.opt.nd.CoordinateBoundingBox;
import org.matheclipse.core.tensor.sca.Clip;
import org.matheclipse.core.tensor.sca.Clips;

public class VisualImage extends VisualBase {
  private final BufferedImage bufferedImage;

  /**
   * @param clipX non-null
   * @param clipY non-null
   * @param bufferedImage non-null
   */
  public VisualImage(BufferedImage bufferedImage, Clip clipX, Clip clipY) {
    this.bufferedImage = Objects.requireNonNull(bufferedImage);
    getAxisX().setClip(clipX);
    getAxisY().setClip(clipY);
  }

  public VisualImage(BufferedImage bufferedImage, CoordinateBoundingBox coordinateBoundingBox) {
    this(bufferedImage, coordinateBoundingBox.getClip(0), coordinateBoundingBox.getClip(1));
  }

  /** @param bufferedImage */
  public VisualImage(BufferedImage bufferedImage) {
    this(bufferedImage, Clips.positive(bufferedImage.getWidth()),
        Clips.positive(bufferedImage.getHeight()));
  }

  /** @return */
  public BufferedImage getBufferedImage() {
    return bufferedImage;
  }
}
