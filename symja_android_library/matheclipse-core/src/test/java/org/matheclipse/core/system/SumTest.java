package org.matheclipse.core.system;

public class SumTest extends ExprEvaluatorTestCase {

  public SumTest(String name) {
    super(name);
  }

  public void testSum001() {
    check("Sum(k^n, {k, 0, m})", //
        "0^n+HarmonicNumber(m,-n)");
    check("Sum(i, {i, 1, 1000000000}) ", //
        "500000000500000000");
    // Leibnitz formula
    check("Sum( ((-1)^k*(2*k + 1))^(-1), {k, 1, Infinity}) ", //
        "1/4*(-4+Pi)");
    check("Sum(b ^ i, {i, 0, k})", //
        "(-1+b^(1+k))/(-1+b)");
    check("Sum(a * b ^ i, {i, 0, k})", //
        "(a*(-1+b^(1+k)))/(-1+b)");
    check("Sum(a * b ^ i, {i, 1, k})", //
        "(a*b*(-1+b^k))/(-1+b)");
    // message Sum: Raw object 0 cannot be used as an iterator.
    check("Sum(101,{0,0,0})", //
        "Sum(101,{0,0,0})");

    check("Sum(z^k/k^n, {k, 1, Infinity})", //
        "PolyLog(n,z)");
    check("Sum(101,{x,0,0})", //
        "101");
    check("Sum(101,{-2})", //
        "0");
  }

  public void testSum002() {
    check("Sum((-1)^i*x^(2*i+1)/(2*i+1)!, {i,0,Infinity})", //
        "Sin(x)");
    check("Sum((-1)^i*x^(2*i)/(2*i)!, {i,0,Infinity})", //
        "Cos(x)");
    check("Sum(1/(i^d),{i,1,Infinity})", //
        "Zeta(d)");
    check("Sum(1/(i^(-d)),{i,1,Infinity})", //
        "Zeta(-d)");
    check("Sum(x^(2*i+1)/(2*i+1)!,{i,0,Infinity})", //
        "Sinh(x)");
    check("Sum(x^(2*i)/(2*i)!,{i,0,Infinity})", //
        "Cosh(x)");
    check("Sum(1/(i!),{i,0,Infinity})", //
        "E");
    check("Sum(x^i/(i!),{i,0,Infinity})", //
        "E^x");
  }

  public void testSum003() {
    check("Sum(1/Binomial(2*i,i), {i,1,Infinity})", //
        "1/27*(9+2*Sqrt(3)*Pi)");
    check("Sum(1/(i*Binomial(2*i,i)), {i,1,Infinity})", //
        "Pi/(3*Sqrt(3))");
    check("Sum(1/(i^2*Binomial(2*i,i)), {i,1,Infinity})", //
        "Pi^2/18");
    check("Sum((-1)^(i-1)/i, {i,1,Infinity})", //
        "Log(2)");

    check("Sum(i^a, {i,0,n})", //
        "0^a+HarmonicNumber(n,-a)");
  }

  public void testSum004() {
    check("Sum(10007,2147483647)", //
        "Hold(Sum(10007,2147483647))");
    check("Sum(1.5708,{i,1,10},{1->0})", //
        "Sum(1.5708,{i,1,10},{1->0})");
    check("Sum(i^2,x)", //
        "i^2*x");
    check("Sum(i^2,Indeterminate)", //
        "Sum(i^2,Indeterminate)");
    check("Sum(f(x), {k,n, n-1})", //
        "0");
    check("Sum(f(x), {k,3, 1/2})", //
        "0");

    check("Sum(a^i,{i,0,n})", //
        "(-1+a^(1+n))/(-1+a)");
    check("Sum((b+i*d)*a^i, {i,0,n})", //
        "((-1+a^(1+n))*b)/(-1+a)+(d*(a+a^(1+n)*(-1-n+a*n)))/(1-a)^2");

    check("Sum(i*a^i, {i,0,n})", //
        "(a+a^(1+n)*(-1-n+a*n))/(1-a)^2");
    check("Sum(i*a^i, {i,1,n})", //
        "(a+a^(1+n)*(-1-n+a*n))/(1-a)^2");

    check("Sum(a^i, {i,0,n})", //
        "(-1+a^(1+n))/(-1+a)");
    check("Sum((3/7)^i, {i,1,n})", //
        "3/4*(1-(3/7)^k)");
    check("Sum((3/7)^i, {i,0,n})", //
        "7/4*(1-(3/7)^(1+n))");

    // prints RecursionLimitExeceeded
    // disabled because github action stack overflows here
    // check(
    // "Sum(f(x), {x, x, x+1})", //
    // "Sum(f(x),{x,x,x+1})");

    check("Sum(f(x), {x, x, x})", //
        "f(x)");
    check("Sum(f(x), {x, a, a+1})", //
        "f(a)+f(1+a)");
  }

  public void testSum005() {
    check("Sum(Boole(x>0), {x,{1,2,3,-2,4,5}})", //
        "5");
    check("Sum(0, {k, a, Infinity})", //
        "0");
    check("Sum(1, {k, a, Infinity})", //
        "Infinity");
    check("Sum(-2, {k, a, Infinity})", //
        "-Infinity");
    check("Sum(42, {k, a, Infinity})", //
        "Infinity");

    // {k,a,n} assumes a<=k<=n
    check("Sum(2, {k, a, n})", //
        "2*(1-a+n)");

    check("Sum(i, {i, 5, 10})", //
        "45");
    check("Sum(i, {i, 0, 30000})", //
        "450015000");
    check("Sum(i, {i, 0, n})", //
        "1/2*n*(1+n)");
    check("Sum(i, {i, 3, n})", //
        "1/2*(-2+n)*(3+n)");
    check("Sum(a + b, {a, 0, 2}, {b, 0, 3})", //
        "30");
    check("Sum(a, {a, {b, c, d, e}})", //
        "b+c+d+e");
    check("Sum(a*f, {a, {b, c, d, e}}, {f, {g, h}})", //
        "b*g+c*g+d*g+e*g+b*h+c*h+d*h+e*h");

    check("Sum(f(k,j),{k,0,-1+2}, {j,0,-1+k})", //
        "f(1,0)");
    check("Sum(k, {k, 1, n})", //
        "1/2*n*(1+n)");
    check("Sum(k, {k, 1, 10})", //
        "55");
    check("Sum(g(i),{i,10,2})", //
        "0");
  }

  public void testSum006() {
    check("Table(f(k,j), {k,0,-1+3},{j,0,-1+k})", "{{},{f(1,0)},{f(2,0),f(2,1)}}");
    check("Sum(f(k,j),{k,0,-1+2}, {j,0,-1+k})", //
        "f(1,0)");
    check("Sum(f(i,j), {i, 1, 2}, {j, 1, 3})", //
        "f(1,1)+f(1,2)+f(1,3)+f(2,1)+f(2,2)+f(2,3)");
    check("Sum(f(k,j), {k,0,-1+2},{j,0,-1+k})", //
        "f(1,0)");
    check(
        "Sum(((-1)^k*Binomial(-1+2,k)*2^((-1)*2*k+2)*Binomial(2*k,j)*Sin(1/2*2*Pi+2*(-j+k)*#1))/((\n"
            + "1+k)*Cos(#1)^(2+2*k)*(-j+k)^(1-2)),{k,0,-1+2},{j,0,-1+k})",
        "1/2*Sec(#1)^4*Sin(2*#1)");
    check("Sum(j+k, {k,0,-1+2},{j,0,-1+k})", "1");
    check("Sum(k, {k, 1, 10})", //
        "55");
    check("Sum(i * j, {i, 1, 10}, {j, 1, 10})", //
        "3025");
    check("Sum(k, {k, 1, n})", //
        "1/2*n*(1+n)");
    check("Sum(k, {k, n, 2*n})", //
        "3/2*n*(1+n)");
    check("Sum(k, {k, 2, 2})", //
        "2");
    check("Sum(k, {k, 2, 3})", //
        "5");
    check("Sum(k, {k, I, I + 1})", //
        "1+I*2");
    check("Sum(1 / k ^ 2, {k, 1, n})", //
        "HarmonicNumber(n,2)");
    check("Sum(x ^ 2, {x, 1, y})", //
        "y/6+y^2/2+y^3/3");
    check("Simplify(Sum(x ^ 2, {x, 1, y}) - y * (y + 1) * (2 * y + 1) / 6)", //
        "0");
    check("Sum( 2 ^ (-i), {i, 1, Infinity})", //
        "1");
    check("Sum( (1/2) ^ i, {i, 1, Infinity})", //
        "1");
    check("Sum(1 / k ^ 2, {k, 1, Infinity}) ", //
        "Pi^2/6");
    check("Sum(i / Log(i), {i, 1, Infinity})", //
        "Sum(i/Log(i),{i,1,Infinity})");
    check("Sum(Cos(Pi*i), {i, 1, Infinity})", //
        "Sum(Cos(i*Pi),{i,1,Infinity})");
    check("Sum(x^k*Sum(y^l,{l,0,4}),{k,0,4})", //
        "1+y+y^2+y^3+y^4+x*(1+y+y^2+y^3+y^4)+x^2*(1+y+y^2+y^3+y^4)+x^3*(1+y+y^2+y^3+y^4)+x^\n"
            + "4*(1+y+y^2+y^3+y^4)");
  }

  public void testSum007() {
    check("Sum(2^(-i), {i, 1, Infinity})", //
        "1");
    check("Sum((-3)^(-i), {i, 1, Infinity})", //
        "-1/4");

    check("Sum(k, {k, Range(5)})", //
        "15");
    check("Sum(i^2 - i + 10 ,{i,1,10})", //
        "430");
    check("Sum(i!,{i,3,n})", //
        "-4-Subfactorial(-1)+(-1)^(1+n)*Gamma(2+n)*Subfactorial(-2-n)");
    check("Sum(i!,{i,1,n})", //
        "-1-Subfactorial(-1)+(-1)^(1+n)*Gamma(2+n)*Subfactorial(-2-n)");

    check("Sum(g(i),{i,10,2})", //
        "0");
    check("Sum(0.5^i,{i,1,Infinity})", //
        "1.0");

    check("Sum((1/2)^i,{i,0,1})", //
        "3/2");
    check("Sum((1/2)^i,{i,0,Infinity})", //
        "2");
    check("Sum((1/2)^i,{i,1,Infinity})", //
        "1");
    check("Sum((1/2)^i,{i,3,Infinity})", //
        "1/4");

    check("Sum(a^i,{i,0,1})", //
        "1+a");
    check("Sum(a^i,{i,0,Infinity})", //
        "1-a/(-1+a)");
    check("Sum(a^i,{i,1,Infinity})", //
        "-a/(-1+a)");
    check("Sum(a^i,{i,3,Infinity})", //
        "-a-a/(-1+a)-a^2");

    check("Sum(0,{i,-4,Infinity})", //
        "0");
    check("Sum((-2)^i,{i,0,Infinity})", //
        "Sum((-2)^i,{i,0,Infinity})");
    check("Sum(42^i,{i,0,Infinity})", //
        "Sum(42^i,{i,0,Infinity})");

    check("Sum(i^k,{i,1,n})", //
        "HarmonicNumber(n,-k)");
    check("Sum(i^5,{i,1,n})", //
        "-n^2/12+5/12*n^4+n^5/2+n^6/6");
    check("Sum(f(i,1),{i,{a,b}})", //
        "f(a,1)+f(b,1)");

    check("Sum(c/(i-j+1), {j,i+1,n}, {i,1,n})", //
        "c*Sum(1/(1+i-j),{j,1+i,n},{i,1,n})");
    check("Sum(-(-c*j+c),{j,i+1,n})", //
        "c*(i-n)+1/2*c*(-i+n)*(1+i+n)");

    check("Sum(c*(i-j+1), {j,i+1,n}, {i,1,n})", //
        "c*n*(-i+n)+1/2*c*(i-n)*n*(1+i+n)+c*(1/2*n*(-i+n)+1/2*(-i+n)*n^2)");
    check("Simplify(c*n*(-i+n)+1/2*c*(i-n)*n*(1+i+n)+c*(1/2*n*(-i+n)+1/2*(-i+n)*n^2))", //
        "1/2*c*(2-i)*n*(-i+n)");

    check("Sum(c*(n-1), {j,i,n-1})", //
        "c*(i-n)+c*n*(-i+n)");
    check("Sum(c, {j,i,n-1}, {i,1,n-1})", //
        "c*(i-n)+c*n*(-i+n)");
    check("Sum(1,{k,j+i,n})", //
        "1-i-j+n");
    check("Sum(k,{k,1,n+1})", //
        "1/2*(1+n)*(2+n)");
    check("Sum(i^(1/2), {i, 1, n} )", //
        "HarmonicNumber(n,-1/2)");
    check("Sum(1/i, {i, 1, n} )", //
        "HarmonicNumber(n)");
    check("Sum(i^(-3), {i, 1, n} )", //
        "HarmonicNumber(n,3)");
  }

  public void testSum008() {
    check("Sum(Ceiling(Log(i)),{i,1,n})", //
        "(-E^Floor(Log(n))+n)*Ceiling(Log(n))+(1+E^(1+Floor(Log(n)))*Floor(Log(n))-E^Floor(Log(n))*(\n"
            + "1+Floor(Log(n))))/(-1+E)");
    check("Sum(Ceiling(Log(a,i)),{i,1,n})", //
        "(-a^Floor(Log(n)/Log(a))+n)*Ceiling(Log(n)/Log(a))+(1+a^(1+Floor(Log(n)/Log(a)))*Floor(Log(n)/Log(a))-a^Floor(Log(n)/Log(a))*(\n"
            + "1+Floor(Log(n)/Log(a))))/(-1+a)");
    check("Sum(i*1/2*i,{i,1,n})", //
        "1/2*(n/6+n^2/2+n^3/3)");
    check("Sum(k * k,{k,1,n+1})", //
        "1+13/6*n+3/2*n^2+n^3/3");
    check("Sum(k,{k,4,2})", //
        "0");
    check("Sum(k,{k,a,b})", //
        "1/2*(1-a+b)*(a+b)");
    check("Sum(c, {k, 1, Infinity} )", //
        "Sum(c,{k,1,Infinity})");
    check("Sum(k,{k,1,n+1})", //
        "1/2*(1+n)*(2+n)");
    check("Sum(f(i,1),{i,{a,b}})", //
        "f(a,1)+f(b,1)");
    check("Sum(f(i, j), {i, {a, b}}, {j, 1, 2})", //
        "f(a,1)+f(a,2)+f(b,1)+f(b,2)");
  }

  public void testSum009() {
    check("Sum(4*i^2, {i, 0, n})", //
        "4*(n/6+n^2/2+n^3/3)");

    check("Sum(c, {i, 1, j}, {j, 1, 2})", //
        "2*c*j");

    check("Sum(c, {k, -Infinity, 10} )", //
        "Sum(c,{k,-Infinity,10})");

    check("Sum(c+k, {k, 1, m} )", //
        "c*m+1/2*m*(1+m)");
    check("Sum(c, {k, l, m} )", //
        "c*(1-l+m)");
    check("Sum(c, {k, 1, m} )", //
        "c*m");
    check("Sum(a, {k, j, n} )", //
        "a*(1-j+n)");
    check("Sum(c, {i0, 1, n0} )", //
        "c*n0");
    check("Sum(c, {i0, 0, n0} )", //
        "c*(1+n0)");
    check("Sum(c*n0, {i0, 1, n0} )", //
        "c*n0^2");
    check("Sum(c*n0, {i0, 0, n0} )", //
        "c*n0*(1+n0)");

    check("Sum(c, {i0, 1, n0}, {j0, 1, n0})", //
        "c*n0^2");
    check("Sum(i0, {i0, 0, n0})", //
        "1/2*n0*(1+n0)");
    check("Sum(i^2, {i, 1, n})", //
        "n/6+n^2/2+n^3/3");
    check("Sum(4*i^2, {i, 0, n})", //
        "4*(n/6+n^2/2+n^3/3)");
    check("Sum(i0^3, {i0, 0, n0})", //
        "n0^2/4+n0^3/2+n0^4/4");
    check("Sum(i0^3+p^2, {i0, 0, n0})", //
        "n0^2/4+n0^3/2+n0^4/4+(1+n0)*p^2");
    check("Sum(Binomial(n0,i0), {i0, 0, n0})", //
        "2^n0");
    check("sum(i0*binomial(n0,i0), {i0, 0, n0})", //
        "n0/2^(1-n0)");
    check("sum(p, {i0, 1, n0})", //
        "n0*p");
    check("sum(p+q, {i0, 1, n0})", //
        "n0*p+n0*q");
    check("sum(p, {i0, 0, n0})", //
        "(1+n0)*p");
    check("sum(4, {i0, 0, n0})", //
        "4*(1+n0)");
    check("sum(lcm(3, k), {k, 100})", //
        "11784");
    check("Sum(sin(x), x)", //
        "Sum(Sin(x),x)");
    check("Sum(x, x)", //
        "1/2*x*(1+x)");
    check("Sum(x^2, x)", //
        "x/6+x^2/2+x^3/3");
    check("Sum(x^3, x)", //
        "x^2/4+x^3/2+x^4/4");
    check("Sum(x^4, x)", //
        "-x/30+x^3/3+x^4/2+x^5/5");
    check("Sum(c, {i, 1, n}, {j, 1, n})", //
        "c*n^2");
    check("Sum(c, {i, 1, j}, {j, 1, n})", //
        "c*j*n");
    check("Sum(c, {j, 1, n}, {i, 1, j})", //
        "1/2*c*n*(1+n)");
    check("Sum((i^2 + i)/2, {i,1,n})", //
        "1/4*n*(1+n)+1/2*(n/6+n^2/2+n^3/3)");
    check("Sum(i*(i + 1)/2, {i,1,n})", //
        "1/4*n*(1+n)+1/2*(n/6+n^2/2+n^3/3)");
  }

  public void testSum010() {
    check("Sum(k^a,{k,j,n})", //
        "HurwitzZeta(-a,j)-HurwitzZeta(-a,1+n)");
    check("-1/4*a^4", //
        "-a^4/4");
    check("Sum(k^3,{k,a,b})", //
        "-a^2/4+a^3/2-a^4/4+b^2/4+b^3/2+b^4/4");
  }

  public void testSum011() {
    check("Sum(x,{x,10})", //
        "55");
    check("Sum(x,{a,10,z})", //
        "x*(-9+z)");
    check("Sum(x,{x,1,1})", //
        "1");
    check("Sum(x,{x,3,2,-1})", //
        "5");
    check("Sum(x,{x,10,3,-4})", //
        "16");
    // use default value "0" for iterator with invalid range
    check("Sum(x,{x,1,0})", //
        "0");
    check("Sum(x,{x,2,3,-1})", //
        "0");
    // 1*1 + 2*1 + 3*1 + 2*2 + 2*3 + 3*3
    check("Sum(x*y, {x, 1, 3}, {y, 1, x})", //
        "25");
    check("Sum(k+a,{k,1,n})", //
        "a*n+1/2*n*(1+n)");
  }
}
