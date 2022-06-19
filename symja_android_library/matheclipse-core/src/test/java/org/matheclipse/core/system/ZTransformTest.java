package org.matheclipse.core.system;

/** Tests forSolve and Roots functions */
public class ZTransformTest extends ExprEvaluatorTestCase {

  public ZTransformTest(String name) {
    super(name);
  }

  public void testInverseZTransform001() {
    check("InverseZTransform(f(z)+ g(z)+h(z),z,n)", //
        "InverseZTransform(f(z),n,z)+InverseZTransform(g(z),n,z)+InverseZTransform(h(z),n,z)");
    check("InverseZTransform(z/(z + f(a)), z, n)", //
        "(-f(a))^n");
    check("InverseZTransform(42, z, n)", //
        "42*DiscreteDelta(z)");
  }


  public void testZTransform001() {
    check("ZTransform(n^2*f(n),n,z)", //
        "z*Derivative(0,0,1)[ZTransform][f(n),n,z]+z^2*Derivative(0,0,2)[ZTransform][f(n),n,z]");
    check("ZTransform(f(1+n),n,z)", //
        "-z*f(0)+z*ZTransform(f(n),n,z)");
    check("ZTransform(f(4+n),n,z)", //
        "-z^4*f(0)-z^3*f(1)-z^2*f(2)-z*f(3)+z^4*ZTransform(f(n),n,z)");
    check("ZTransform(f(5+n),n,z)", //
        "-z^5*f(0)-z^4*f(1)-z^3*f(2)-z^2*f(3)-z*f(4)+z^5*ZTransform(f(n),n,z)");

    check("ZTransform(a*f(n)+ b*g(n), n, z)", //
        "a*ZTransform(f(n),n,z)+b*ZTransform(g(n),n,z)");
    check("ZTransform(f(a)^n, n, z)", //
        "z/(z-f(a))");
    check("ZTransform(f(a)^(f(b)*n), n, z)", //
        "z/(z-f(a)^f(b))");
    check("ZTransform(f[a]/(n+f[b]),n,z)", //
        "f(a)*HurwitzLerchPhi(1/z,1,f(b))");
    check("ZTransform(1/(n+k),n,z)", //
        "HurwitzLerchPhi(1/z,1,k)");
    check("ZTransform(1/(k*n!),n,z)", //
        "E^(1/z)/k");
    check("ZTransform(1/n!,n,z)", //
        "E^(1/z)");
    check("ZTransform(f(a)/(2*n+1)!,n,z)", //
        "Sqrt(z)*f(a)*Sinh(1/Sqrt(z))");
    check("ZTransform(Cos(f(a)*n)/n!,n,z)", //
        "E^(Cos(f(a))/z)*Cos(Sin(f(a))/z)");
    check("ZTransform(Cos(n)/n!,n,z)", //
        "E^(Cos(1)/z)*Cos(Sin(1)/z)");
    check("ZTransform(Sin(f(a)*n)/n!,n,z)", //
        "E^(Cos(f(a))/z)*Sin(Sin(f(a))/z)");
    check("ZTransform(Sin(n)/n!,n,z)", //
        "E^(Cos(1)/z)*Sin(Sin(1)/z)");
    check("ZTransform(Cos(n+1)/(n+1),n,z)", //
        "1/2*z*(-Log((E^I-1/z)/E^I)-Log(1-E^I/z))");
    check("ZTransform(Sin(f*(n+1))/(n+1),n,z)", //
        "-I*1/2*z*(Log((E^(I*f)-1/z)/E^(I*f))-Log(1-E^(I*f)/z))");
  }

  public void testZTransform002() {
    check("ZTransform((-1)^n*n^2,n,z)", //
        "z^2*((2*z)/(1+z)^3-2/(1+z)^2)+z*(-z/(1+z)^2+1/(1+z))");
    check("ZTransform(Cos(n*Omega*t),n,z)", //
        "(z*(z-Cos(omega*t)))/(1+z^2-2*z*Cos(omega*t))");
    check("ZTransform(Cos(n+t),n,z)", //
        "(z*(-Cos(1-t)+z*Cos(t)))/(1+z^2-2*z*Cos(1))");
    check("ZTransform(Cos(n*t+a),n,z)", //
        "(z*(z*Cos(a)-Cos(a-t)))/(1+z^2-2*z*Cos(t))");
    check("ZTransform(Cosh(n*t+a),n,z)", //
        "(z*(z*Cosh(a)-Cosh(a-t)))/(1+z^2-2*z*Cosh(t))");
    check("ZTransform(Sin(n+t),n,z)", //
        "(z*(Sin(1-t)+z*Sin(t)))/(1+z^2-2*z*Cos(1))");
    check("ZTransform(Sin(f*n+t),n,z)", //
        "(z*(Sin(f-t)+z*Sin(t)))/(1+z^2-2*z*Cos(f))");
    check("ZTransform(Sinh(f*n+t),n,z)", //
        "(z*(Sinh(f-t)+z*Sinh(t)))/(1+z^2-2*z*Cosh(f))");
    check("ZTransform(n*Cos(b*n)/(n!),n,z)", //
        "z*((E^(Cos(b)/z)*Cos(b)*Cos(Sin(b)/z))/z^2+(-E^(Cos(b)/z)*Sin(b)*Sin(Sin(b)/z))/z^\n"
            + "2)");
    // check("ZTransform(Cos(b*(n+2))/(n+2),n,z)", //
    // "(z*(Sinh(f-t)+z*Sinh(t)))/(1+z^2-2*z*Cosh(f))");

    check("ZTransform((1+n)^2*f(n),n,z)", //
        "ZTransform(f(n),n,z)-z*Derivative(0,0,1)[ZTransform][f(n),n,z]+z^2*Derivative(0,\n"
            + "0,2)[ZTransform][f(n),n,z]");
    check("ZTransform((1+n)^2*Sin(n),n,z)", //
        "(z*Sin(1))/(1+z^2-2*z*Cos(1))+z^2*((-2*z*(2*z-2*Cos(1))*(-2*z+2*Cos(1))*Sin(1))/(\n"
            + "1+z^2-2*z*Cos(1))^3+(-2*z*Sin(1))/(1+z^2-2*z*Cos(1))^2+(2*(-2*z+2*Cos(1))*Sin(1))/(\n"
            + "1+z^2-2*z*Cos(1))^2)+z*((z*(-2*z+2*Cos(1))*Sin(1))/(1+z^2-2*z*Cos(1))^2+Sin(1)/(\n"
            + "1+z^2-2*z*Cos(1)))-z*((2*z*(-2*z+2*Cos(1))*Sin(1))/(1+z^2-2*z*Cos(1))^2+(2*Sin(1))/(\n"
            + "1+z^2-2*z*Cos(1)))");
  }
}
