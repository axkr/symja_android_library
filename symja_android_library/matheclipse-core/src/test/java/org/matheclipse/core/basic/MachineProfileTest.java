package org.matheclipse.core.basic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the factor which adapts the wall-clock evaluation budgets to the machine they run on.
 *
 * <p>
 * The factor is a global, and the whole suite shares one virtual machine, so every test here has to
 * put it back the way it found it.
 */
public class MachineProfileTest {

  @AfterEach
  public void tearDown() {
    MachineProfile.reset();
  }

  @Test
  public void testNothingConfiguredIsTheTunedValue() {
    assertEquals(1.0, MachineProfile.parse(null, null, null, null), 0.0);
    assertEquals(1.0, MachineProfile.parse("", "  ", "", null), 0.0);
  }

  @Test
  public void testPrecedence() {
    // the factor itself beats the environment, which beats a named profile
    assertEquals(2.5, MachineProfile.parse("2.5", "4", "slow", "fast"), 0.0);
    assertEquals(4.0, MachineProfile.parse(null, "4", "slow", "fast"), 0.0);
    assertEquals(3.0, MachineProfile.parse(null, null, "slow", "fast"), 0.0);
    assertEquals(0.75, MachineProfile.parse(null, null, null, "fast"), 0.0);
  }

  @Test
  public void testPresetNames() {
    assertEquals(0.75, MachineProfile.parse(null, null, "fast", null), 0.0);
    assertEquals(1.0, MachineProfile.parse(null, null, "normal", null), 0.0);
    assertEquals(3.0, MachineProfile.parse(null, null, "slow", null), 0.0);
    assertEquals(3.0, MachineProfile.parse(null, null, "SLOW", null), 0.0);
    assertEquals(3.0, MachineProfile.parse(null, null, "  Slow  ", null), 0.0);
  }

  @Test
  public void testUnusableValueIsSkippedRatherThanCorrected() {
    // a mistyped factor must not quietly become a different one - the next source is asked instead
    assertEquals(2.0, MachineProfile.parse("abc", "2", null, null), 0.0);
    assertEquals(2.0, MachineProfile.parse("NaN", "2", null, null), 0.0);
    assertEquals(2.0, MachineProfile.parse("-1", "2", null, null), 0.0);
    assertEquals(2.0, MachineProfile.parse("0", "2", null, null), 0.0);
    assertEquals(2.0, MachineProfile.parse("Infinity", "2", null, null), 0.0);
    assertEquals(2.0, MachineProfile.parse("1e9", "2", null, null), 0.0);
    assertEquals(1.0, MachineProfile.parse("abc", null, "turbo", null), 0.0);
  }

  @Test
  public void testSecondsAreUnchangedAtTheTunedFactor() {
    MachineProfile.setScale(1.0);
    for (int seconds = 1; seconds <= 60; seconds++) {
      assertEquals(seconds, MachineProfile.seconds(seconds));
      assertEquals((long) seconds, MachineProfile.seconds((long) seconds));
    }
    assertEquals(45000L, MachineProfile.millis(45000L));
    assertEquals(Integer.MAX_VALUE, MachineProfile.seconds(Integer.MAX_VALUE));
  }

  @Test
  public void testSecondsAreScaled() {
    MachineProfile.setScale(2.5);
    assertEquals(20L, MachineProfile.seconds(8L));
    assertEquals(13L, MachineProfile.seconds(5L));
    assertEquals(8L, MachineProfile.seconds(3L)); // 7.5 rounds up

    MachineProfile.setScale(3.0);
    assertEquals(135000L, MachineProfile.millis(45000L));
    assertEquals(6000L, MachineProfile.millis(2000L));
  }

  @Test
  public void testABudgetNeverShrinksBelowASecond() {
    // TimeConstrained() rounds its limit up to a whole second and refuses a limit of zero
    MachineProfile.setScale(0.01);
    assertEquals(1L, MachineProfile.seconds(3L));
    assertEquals(1, MachineProfile.seconds(3));
    assertEquals(1L, MachineProfile.millis(50L));
  }

  @Test
  public void testASwitchedOffBudgetStaysSwitchedOff() {
    // every caller reads zero or less as "no limit", which is not a duration to be scaled
    MachineProfile.setScale(3.0);
    assertEquals(0L, MachineProfile.seconds(0L));
    assertEquals(0L, MachineProfile.millis(0L));
    assertEquals(-1L, MachineProfile.seconds(-1L));
    assertEquals(-5L, MachineProfile.millis(-5L));
  }

  @Test
  public void testSetScale() {
    MachineProfile.setScale(2.0);
    assertEquals(2.0, MachineProfile.getScale(), 0.0);
    MachineProfile.setPreset(MachineProfile.Preset.SLOW);
    assertEquals(3.0, MachineProfile.getScale(), 0.0);
  }

  @Test
  public void testSetScaleRefusesAFactorItCannotUse() {
    MachineProfile.setScale(2.0);
    for (double bad : new double[] {Double.NaN, Double.POSITIVE_INFINITY, 0.0, -1.0, 1e9}) {
      assertThrows(IllegalArgumentException.class, () -> MachineProfile.setScale(bad));
    }
    assertEquals(2.0, MachineProfile.getScale(), 0.0);
  }

  @Test
  public void testResetReadsTheConfigurationAgain() {
    MachineProfile.setScale(7.0);
    assertEquals(7.0, MachineProfile.getScale(), 0.0);
    MachineProfile.reset();
    // also proves that a -Dsymja.timeScale on the command line reaches the forked test machine
    assertEquals(MachineProfile.parse(System.getProperty(MachineProfile.TIME_SCALE_PROPERTY),
        System.getenv(MachineProfile.TIME_SCALE_ENV),
        System.getProperty(MachineProfile.PROFILE_PROPERTY),
        System.getenv(MachineProfile.PROFILE_ENV)), MachineProfile.getScale(), 0.0);
  }

  @Test
  public void testCalibrationMeasuresSomethingUsable() {
    double factor = Config.calibrateTimeScale();
    assertTrue(factor >= 0.25 && factor <= 20.0, "calibrated factor out of range: " + factor);
    // the measurement is taken once and remembered, so the second call has to agree exactly
    assertEquals(factor, Config.calibrateTimeScale(), 0.0);
    // and the factor it produces is one the profile accepts
    MachineProfile.setScale(factor);
    assertEquals(factor, MachineProfile.getScale(), 0.0);
  }

  @Test
  public void testCalibrationWorkloadIsMeasurable() {
    assertTrue(Config.measureCalibrationWorkload() > 0L);
  }
}
