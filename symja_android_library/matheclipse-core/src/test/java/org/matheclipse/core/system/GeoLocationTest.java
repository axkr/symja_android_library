package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import java.nio.file.Path;
import java.util.TimeZone;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.data.GeoLocations;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.GeoPositionExpr;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>$GeoLocation</code>, <code>$GeoLocationSource</code> and <code>FindGeoLocation</code>: an
 * assigned location wins, then the <code>symja.geolocation</code> setting, then the time zone - and
 * the last two only in a kernel on the user's own machine.
 */
public class GeoLocationTest extends ExprEvaluatorTestCase {

  private final boolean fileSystemEnabled = Config.FILESYSTEM_ENABLED;

  private final TimeZone timeZone = TimeZone.getDefault();

  private final String property = System.getProperty(GeoLocations.PROPERTY);

  /** A setting in the environment of the machine running the tests would change every answer. */
  private static boolean environmentIsClean() {
    String value = System.getenv(GeoLocations.ENVIRONMENT);
    return value == null || value.isBlank();
  }

  @BeforeEach
  public void clearLocation() {
    // a $ symbol keeps its value in the engine, not in the symbol
    evaluator.getEvalEngine().setDollarValue(S.$GeoLocation, null);
    System.clearProperty(GeoLocations.PROPERTY);
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
  }

  @AfterEach
  public void restore() {
    Config.FILESYSTEM_ENABLED = fileSystemEnabled;
    evaluator.getEvalEngine().setFileSandboxRoot(null);
    // a $ symbol keeps its value in the engine, not in the symbol
    evaluator.getEvalEngine().setDollarValue(S.$GeoLocation, null);
    TimeZone.setDefault(timeZone);
    if (property == null) {
      System.clearProperty(GeoLocations.PROPERTY);
    } else {
      System.setProperty(GeoLocations.PROPERTY, property);
    }
  }

  /**
   * Evaluate {@code input} and check it is a position at the given coordinates. Compared
   * numerically, because a GeoPosition normalizes its longitude and can come back a last bit off.
   */
  private void checkPosition(String input, double latitude, double longitude, double altitude) {
    IExpr result = evaluator.eval(input);
    assertTrue(result instanceof GeoPositionExpr, input + " gave " + result);
    GeoPositionExpr position = (GeoPositionExpr) result;
    assertEquals(latitude, position.latitude(), 1e-9, input);
    assertEquals(longitude, position.longitude(), 1e-9, input);
    assertEquals(altitude, position.altitude(), 1e-9, input);
  }

  /** On a server nothing about the host is used, not even its time zone. */
  @Test
  public void testNotHostVisible() {
    Config.FILESYSTEM_ENABLED = false;
    System.setProperty(GeoLocations.PROPERTY, "48.14,11.58");
    check("{$GeoLocation, $GeoLocationSource, FindGeoLocation()}", //
        "{$GeoLocation,None,Missing(NotAvailable)}");
    // a zone given explicitly is no host information; Berlin is at 52 30' N, 13 22' E
    checkPosition("FindGeoLocation(\"Europe/Berlin\")", 52.5, 13 + 22 / 60.0, 0.0);
  }

  @Test
  public void testInsideASandbox(@TempDir Path root) {
    Config.FILESYSTEM_ENABLED = true;
    evaluator.getEvalEngine().setFileSandboxRoot(root);
    check("{$GeoLocation, $GeoLocationSource, FindGeoLocation()}", //
        "{$GeoLocation,None,Missing(NotAvailable)}");
  }

  /** On the user's own machine the time zone gives an estimate, and says so. */
  @Test
  public void testTimeZoneEstimate() {
    assumeTrue(environmentIsClean());
    Config.FILESYSTEM_ENABLED = true;
    // Europe/Paris: 48 52' N, 2 20' E
    checkPosition("$GeoLocation", 48 + 52 / 60.0, 2 + 20 / 60.0, 0.0);
    check("$GeoLocationSource", //
        "TimeZone");
    checkPosition("FindGeoLocation()", 48 + 52 / 60.0, 2 + 20 / 60.0, 0.0);
    // a zone without a place gives nothing
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    check("{$GeoLocation, $GeoLocationSource, FindGeoLocation()}", //
        "{$GeoLocation,None,Missing(NotAvailable)}");
  }

  /** The setting wins over the time zone; Method picks one source. */
  @Test
  public void testConfiguration() {
    Config.FILESYSTEM_ENABLED = true;
    System.setProperty(GeoLocations.PROPERTY, "48.14,11.58,520");
    checkPosition("$GeoLocation", 48.14, 11.58, 520.0);
    check("$GeoLocationSource", //
        "Configuration");
    checkPosition("FindGeoLocation(Method -> \"TimeZone\")", 48 + 52 / 60.0, 2 + 20 / 60.0, 0.0);
    checkPosition("FindGeoLocation(Method -> \"Configuration\")", 48.14, 11.58, 520.0);
    // an unreadable setting is ignored rather than guessed at
    System.setProperty(GeoLocations.PROPERTY, "Munich");
    check("FindGeoLocation(Method -> \"Configuration\")", //
        "Missing(NotAvailable)");
  }

  /** An assigned location always wins. */
  @Test
  public void testAssigned() {
    Config.FILESYSTEM_ENABLED = true;
    System.setProperty(GeoLocations.PROPERTY, "48.14,11.58");
    evaluator.eval("$GeoLocation = GeoPosition({52.52,13.405})");
    checkPosition("$GeoLocation", 52.52, 13.405, 0.0);
    check("$GeoLocationSource", //
        "User");
    // unset, the setting is back
    check("$GeoLocation =.; $GeoLocationSource", //
        "Configuration");
  }

  @Test
  public void testBadArguments() {
    check("FindGeoLocation(\"Middle/Earth\")", //
        "FindGeoLocation(Middle/Earth)");
    check("FindGeoLocation(Method -> \"GeoIP\")", //
        "FindGeoLocation(Method->GeoIP)");
  }
}
