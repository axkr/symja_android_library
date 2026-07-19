package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/** Tests forSolve and Roots functions */
public class ZTransformTest extends ExprEvaluatorTestCase {

  @Test
  public void testInverseZTransform001() {
    check("InverseZTransform(f(z)+ g(z)+h(z),z,n)", //
        "InverseZTransform(f(z),z,n)+InverseZTransform(g(z),z,n)+InverseZTransform(h(z),z,n)");
    check("InverseZTransform(z/(z + f(a)), z, n)", //
        "(-f(a))^n");
    check("InverseZTransform(42, z, n)", //
        "42*DiscreteDelta(n)");
  }

  @Test
  public void testInverseZTransform002() {
    check("InverseZTransform(zsys/(-2+2*zsys),zsys,n)+InverseZTransform(zsys/(2+2*zsys),zsys,n)", //
        "1/2+(-1)^n/2");
    check("InverseZTransform(zsys/(-2+2*zsys),zsys,n)-InverseZTransform(zsys/(2+2*zsys),zsys,n)", //
        "1/2-(-1)^n/2");
  }

  @Test
  public void testZTransform001() {
    check("ZTransform(f(1+n),n,z)", //
        "-z*f(0)+z*ZTransform(f(n),n,z)");
    check("ZTransform(f(4+n),n,z)", //
        "-z^4*f(0)-z^3*f(1)-z^2*f(2)-z*f(3)+z^4*ZTransform(f(n),n,z)");
    check("ZTransform(f(5+n),n,z)", //
        "-z^5*f(0)-z^4*f(1)-z^3*f(2)-z^2*f(3)-z*f(4)+z^5*ZTransform(f(n),n,z)");

    check("ZTransform(f(n),n,z)", //
        "ZTransform(f(n),n,z)");
    check("ZTransform(n,n,z)", //
        "z/(1-z)^2");
    check("ZTransform(a,n,z)", //
        "(a*z)/(-1+z)");
    check("ZTransform(z,n,z)", //
        "z^2/(-1+z)");

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

  @Test
  public void testZTransform002() {
    check("ZTransform((-1)^n*n^2,n,z)", //
        "((1-z)*z)/(1+z)^3");
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
        "(E^(Cos(b)/z)*Cos(b+Sin(b)/z))/z");
    // check("ZTransform(Cos(b*(n+2))/(n+2),n,z)", //
    // "(z*(Sinh(f-t)+z*Sinh(t)))/(1+z^2-2*z*Cosh(f))");


    check("ZTransform((1+n)^2*Sin(n),n,z)", //
        "(2*z*(-1+z^2)*Sin(1))/(1+z^2-2*z*Cos(1))^2+(z*Sin(1))/(1+z^2-2*z*Cos(1))+(-z*(-1+(z*(\n"
            + "6*z-2*z^3-4*Cos(1)))/(1+z^2-2*z*Cos(1))^2+(2*z*(z-Cos(1)))/(1+z^2-2*z*Cos(1)))*Sin(\n"
            + "1))/(1+z^2-2*z*Cos(1))");
  }

  @Test
  public void testZTransform003() {
    check("ZTransform(a+b+c,n,z)", //
        "((a+b+c)*z)/(-1+z)");
  }

  @Test
  public void testZTransform004() {
    check("ZTransform(n^2 + n + 1, n, z)", //
        "(z+z^3)/(-1+z)^3");
  }

  @Test
  public void testZTransformMonomials() {
    check("ZTransform(1, n, z)", //
        "z/(-1+z)");
    check("ZTransform(n, n, z)", //
        "z/(1-z)^2");
    check("ZTransform(n^2, n, z)", //
        "(z*(1+z))/(-1+z)^3");
    check("ZTransform(n^3, n, z)", //
        "(z*(1+4*z+z^2))/(1-z)^4");
    check("ZTransform(n^4, n, z)", //
        "(z*(1+11*z+11*z^2+z^3))/(-1+z)^5");
  }

  @Test
  public void testZTransformConstantMultiples() {
    check("ZTransform(2, n, z)", //
        "(2*z)/(-1+z)");
    check("ZTransform(2*n, n, z)", //
        "(2*z)/(1-z)^2");
    check("ZTransform(2*n^2, n, z)", //
        "(2*z*(1+z))/(-1+z)^3");
    // Symbols free of n act as constants
    check("ZTransform(x, n, z)", //
        "(x*z)/(-1+z)");
  }

  @Test
  public void testZTransformGeometric() {
    check("ZTransform(a^n, n, z)", //
        "z/(-a+z)");
    check("ZTransform(2^n, n, z)", //
        "z/(-2+z)");
    // Rational base: z/(-1/3+z) == (3*z)/(-1+3*z)
    check("ZTransform((1/3)^n, n, z)", //
        "z/(-1/3+z)");
  }

  @Test
  public void testZTransformPolynomialTimesGeometric() {
    check("ZTransform(n*a^n, n, z)", //
        "(a*z)/(-a+z)^2");
    check("ZTransform(n*2^n, n, z)", //
        "(2*z)/(2-z)^2");
    check("ZTransform(n/3^n, n, z)", //
        "(3*z)/(1-3*z)^2");
    check("ZTransform(n^2*a^n, n, z)", //
        "(a*z*(a+z))/(-a+z)^3");
    check("ZTransform(n^2*2^n, n, z)", //
        "(2*z*(2+z))/(-2+z)^3");
    // The documentation example: (z*(2+4*z))/(-1+2*z)^3 == (2*z*(1+2*z))/(-1+2*z)^3
    check("ZTransform(n^2/2^n, n, z)", //
        "(z*(2+4*z))/(-1+2*z)^3");
  }

  @Test
  public void testZTransformInverseFactorial() {
    check("ZTransform(1/n!, n, z)", //
        "E^(1/z)");
    check("ZTransform(3^n/n!, n, z)", //
        "E^(3/z)");
  }

  @Test
  public void testZTransformTrig() {
    check("ZTransform(Sin(n), n, z)", //
        "(z*Sin(1))/(1+z^2-2*z*Cos(1))");
    check("ZTransform(Cos(n), n, z)", //
        "(z*(z-Cos(1)))/(1+z^2-2*z*Cos(1))");
    check("ZTransform(Sin(a*n), n, z)", //
        "(z*Sin(a))/(1+z^2-2*z*Cos(a))");
    check("ZTransform(Cos(a*n), n, z)", //
        "(z*(z-Cos(a)))/(1+z^2-2*z*Cos(a))");
    check("ZTransform(Sin(2*n), n, z)", //
        "(z*Sin(2))/(1+z^2-2*z*Cos(2))");
    check("ZTransform(Sin(n/2), n, z)", //
        "(z*Sin(1/2))/(1+z^2-2*z*Cos(1/2))");
  }

  @Test
  public void testZTransformUnitStep() {
    // UnitStep(n) == 1 for n >= 0, so its Z-transform is z/(z-1)
    check("ZTransform(UnitStep(n), n, z)", //
        "z/(-1+z)");
  }

  @Test
  public void testZTransformUnsupported() {
    // Sin(n^2) is not a linear a*n argument, so there is no closed form
    check("ZTransform(Sin(n^2), n, z)", //
        "ZTransform(Sin(n^2),n,z)");
  }

  @Test
  public void testInverseZTransformGeometric() {
    check("InverseZTransform(z/(z - a), z, n)", //
        "a^n");
    check("InverseZTransform(z/(z - 1), z, n)", //
        "1");
    check("InverseZTransform(z/(z - 2), z, n)", //
        "2^n");
    // (1/3)^n == 3^(-n)
    check("InverseZTransform((3*z)/(-1 + 3*z), z, n)", //
        "(1/3)^n");
    // Sign-wrapped spelling of the same transform
    check("InverseZTransform(-(z/(a - z)), z, n)", //
        "a^n");
  }

  @Test
  public void testInverseZTransformPolynomialSequences() {
    check("InverseZTransform(z/(z - 1)^2, z, n)", //
        "n");
    check("InverseZTransform((z*(1 + z))/(-1 + z)^3, z, n)", //
        "n^2");
  }

  @Test
  public void testInverseZTransformBinomialSequences() {
    // 1/2*(-1+n)*n == ((-1 + n)*n)/2
    check("InverseZTransform(z/(z - 1)^3, z, n)", //
        "1/2*(-1+n)*n");
    check("InverseZTransform(z/(z - 1)^4, z, n)", //
        "1/6*(-2+n)*(-1+n)*n");
  }

  @Test
  public void testInverseZTransformPolynomialTimesGeometric() {
    check("InverseZTransform((a*z)/(a - z)^2, z, n)", //
        "a^n*n");
    check("InverseZTransform((2*z)/(-2 + z)^2, z, n)", //
        "2^n*n");
    check("InverseZTransform((3*z)/(1 - 3*z)^2, z, n)", //
        "n/3^n");
    check("InverseZTransform((2*z*(2 + z))/(-2 + z)^3, z, n)", //
        "2^n*n^2");
    check("InverseZTransform(-((a*z*(a + z))/(a - z)^3), z, n)", //
        "a^n*n^2");
    check("InverseZTransform((2*z*(1 + 2*z))/(-1 + 2*z)^3, z, n)", //
        "n^2/2^n");
  }

  @Test
  public void testInverseZTransformExponentialForms() {
    // Gamma(1+n) == n!
    check("InverseZTransform(E^(1/z), z, n)", //
        "1/Gamma(1+n)");
    check("InverseZTransform(E^(3/z), z, n)", //
        "3^n/Gamma(1+n)");
  }

  @Test
  public void testInverseZTransformConstants() {
    check("InverseZTransform((2*z)/(-1 + z), z, n)", //
        "2");
    check("InverseZTransform((x*z)/(-1 + z), z, n)", //
        "x");
    // z-free input is a DiscreteDelta impulse
    check("InverseZTransform(1, z, n)", //
        "DiscreteDelta(n)");
    check("InverseZTransform(5, z, n)", //
        "5*DiscreteDelta(n)");
  }

  @Test
  public void testZTransformRoundTrip() {
    check("InverseZTransform(ZTransform(n^2/2^n, n, z), z, n)", //
        "n^2/2^n");
    check("ZTransform(InverseZTransform(z/(z - 2), z, n), n, z)", //
        "z/(-2+z)");
  }

  @Test
  public void testInverseZTransformUnsupported() {
    check("InverseZTransform(Sin(z), z, n)", //
        "InverseZTransform(Sin(z),z,n)");
  }

  @Test
  public void testZTransformFractionalShift() {
    // Verifies that ZTransform securely returns unevaluated when encountering
    // sequence functions evaluated at non-integer shifts.
    check("ZTransform(Sqrt(n)*a(Sqrt(n)), n, z)", //
        "ZTransform(Sqrt(n)*a(Sqrt(n)),n,z)");

  }

  @Test
  public void testEGFTransformFractionalShift() {
    // Verifies that EGF securely returns unevaluated for fractional indices.
    check("ExponentialGeneratingFunction(Sqrt(n)*a(Sqrt(n)), n, x)",
        "ExponentialGeneratingFunction(Sqrt(n)*a(Sqrt(n)),n,x)");
  }

  @Test
  public void testInverseZTransformExponentialFractions() {
    // Z^-1 { E^(a/z) }
    check("InverseZTransform(E^(a/z), z, n)", //
        "a^n/Gamma(1+n)");

    // Z^-1 { E^(a/z^2) }
    check("InverseZTransform(E^(a/z^2), z, n)", //
        "((1+(-1)^n)*a^(n/2))/(2*Gamma(1+n/2))");

    // Z^-1 { E^(a/z) / z } -> Tests m = -1 negative shift
    check("InverseZTransform(E^(a/z)/z, z, n)", //
        "1/(a^(1-n)*Gamma(n))");

    // Z^-1 { E^(a/z^2) / z^3 } -> Tests m = -3 negative shift
    check("InverseZTransform(E^(a/z^2)/z^3, z, n)", //
        "((1+(-1)^(-3+n))*a^(1/2*(-3+n)))/(2*Gamma(1+1/2*(-3+n)))");

    // Explicit numeric constants (Extracts 1^x correctly)
    check("InverseZTransform(E^(1/z^2), z, n)", //
        "(1+(-1)^n)/(2*Gamma(1+n/2))");

    // Edge Case (The exact isolated fraction from the RSolve trace)
    check("InverseZTransform(E^(1/z^2)/z, z, n)", //
        "(1+(-1)^(-1+n))/(2*Gamma(1+1/2*(-1+n)))");
  }
}
