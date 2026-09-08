package org.matheclipse.core.system;

/**
 * Tag names for the JUnit 5 test tiers.
 *
 * <p>
 * The default build runs the <b>fast</b> tier: every test that carries neither tag. The parent
 * {@code pom.xml} sets {@code symja.test.excludedGroups} to {@code slow,corpus}, and the
 * {@code slow-tests}, {@code all-tests} and {@code rubi-corpus} profiles select the other tiers.
 *
 * <p>
 * These are plain {@code String} constants because {@code @Tag} needs a compile-time constant.
 * {@code matheclipse-io} cannot see this class - {@code matheclipse-core} publishes no test-jar -
 * so the Rubi corpus there spells {@code @Tag("corpus")} out literally.
 */
public final class TestTags {

  /**
   * A single test method that takes more than a second. Excluded from the default run, and from
   * every GitHub Actions workflow, so that the pull-request build stays inside its budget.
   *
   * <p>
   * <b>Adding a test: if one method takes over a second, tag it.</b> This is not a convention -
   * {@code .github/scripts/check-test-budget.py} fails the build over an untagged slow test.
   *
   * <p>
   * Run this tier with {@code mvn test -Pslow-tests}, or together with the fast tier using
   * {@code mvn verify -Pall-tests}.
   */
  public static final String SLOW = "slow";

  /**
   * A scoring corpus whose failures are <b>expected</b>: the expected values are another system's
   * reference output, and the pass count is the result rather than the build status. Used by the
   * Rubi integration corpus in {@code matheclipse-io}.
   *
   * <p>
   * Run this tier with {@code mvn -pl matheclipse-io test -Prubi-corpus -Dsurefire.timeout=5400}.
   */
  public static final String CORPUS = "corpus";

  private TestTags() {}
}
