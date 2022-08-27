// code adapted from https://github.com/datahaki/bridge
package org.matheclipse.core.bridge.fig;

import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimePeriod;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.tensor.qty.IUnit;
import org.matheclipse.core.tensor.qty.QuantityMagnitude;
import org.matheclipse.core.tensor.qty.UnitConvert;
import org.matheclipse.core.tensor.sca.Clip;
import org.matheclipse.core.tensor.sca.Clips;

/* package */ enum StaticHelper {
  ;
  // TODO BRIDGE ALG the implementation can be improved by implementing TimePeriod
  private static final Calendar CALENDAR = Calendar.getInstance();

  /**
   * @param time
   * @return
   */
  public static TimePeriod timePeriod(ISignedNumber time) {
    long timeL = time.toLongDefault();
    int hours = Math.toIntExact(TimeUnit.SECONDS.toHours(timeL));
    int minutes = Math.toIntExact(TimeUnit.SECONDS.toMinutes(timeL) - 60 * hours);
    int seconds = Math.toIntExact(TimeUnit.SECONDS.toSeconds(timeL) - minutes * 60 - hours * 3600);
    int day = 1;
    int month = CALENDAR.get(Calendar.MONTH) + 1; // Month are 0 based, thus it is necessary to add
                                                  // 1
    int year = CALENDAR.get(Calendar.YEAR);
    return new Second(seconds, minutes, hours, day, month, year); // month and year can not be zero
  }

  /**
   * @param bufferedImage
   * @param visualSet
   * @param domain
   * @param yhi with unit of domain negated
   * @return
   */
  public static VisualImage create(BufferedImage bufferedImage, VisualSet visualSet, IAST domain,
      ISignedNumber yhi) {
    IUnit unitX = visualSet.getAxisX().getUnit();
    UnaryOperator<IExpr> suoX = UnitConvert.SI().to(unitX);
    Clip clipX = Clips.interval(suoX.apply(domain.first()),
        suoX.apply(domain.last()));
    // ---
    IUnit unitY = visualSet.getAxisY().getUnit();
    UnaryOperator<IExpr> suoY = UnitConvert.SI().to(unitY);
    Clip clipY =
        Clips.interval(suoY.apply(yhi.zero()), suoY.apply(yhi));
    // ---
    VisualImage visualImage = new VisualImage(bufferedImage, clipX, clipY);
    visualImage.getAxisX().setLabel(visualSet.getAxisX().getLabel());
    visualImage.getAxisY().setLabel(visualSet.getAxisY().getLabel());
    return visualImage;
  }

  public static void setRange(Axis axis, ValueAxis valueAxis) {
    if (valueAxis instanceof NumberAxis) {
      // Mathematica does not include zero in the y-axes by default
      // whereas jfreechart does so.
      // the code below emulates the behavior of Mathematica
      ((NumberAxis) valueAxis).setAutoRangeIncludesZero(false);
    }
    Optional<Clip> optional = axis.getOptionalClip();
    if (optional.isPresent()) {
      Clip clip = optional.orElseThrow();
      UnaryOperator<IExpr> suo = QuantityMagnitude.SI().in(axis.getUnit());
      valueAxis.setRange( //
          suo.apply(clip.min()).toDoubleDefault(), //
          suo.apply(clip.max()).toDoubleDefault());
    }
  }
}
