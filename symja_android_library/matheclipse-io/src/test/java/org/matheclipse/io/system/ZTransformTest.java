package org.matheclipse.io.system;

/** Tests forSolve and Roots functions */
public class ZTransformTest extends AbstractTestCase {

  public ZTransformTest(String name) {
    super(name);
  }

  public void testInverseZTransform001() {
    check("InverseZTransform((z)+ g(z)+h(z),z,n)", //
        "InverseZTransform(z,n,z)+InverseZTransform(g(z),n,z)+InverseZTransform(h(z),n,z)");
    check("InverseZTransform(z/(z + f(a)), z, n)", //
        "(-f(a))^n");
    check("InverseZTransform(42, z, n)", //
        "42*DiscreteDelta(z)");
  }


  public void testZTransform001() {
      check("ZTransform(a*f(n)+ b*g(n), n, z)", //
          "a*ZTransform(f(n),n,z)+b*ZTransform(g(n),n,z)");
    check("ZTransform(f(a)^n, n, z)", //
        "z/(z-f(a))");
  }

}
