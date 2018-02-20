/**
The apfloat Application Programming Interface (API).<p>

All application code using apfloat generally needs to only call
the classes in this package.<p>

A sample apfloat program might look like this:

<pre>
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

public class ApfloatTest
{
    public static void main(String[] args)
    {
        Apfloat x = new Apfloat(2, 1000);   // Value 2, precision 1000 digits

        Apfloat y = ApfloatMath.sqrt(x);    // Square root of 2, to 1000 digits

        System.out.println(y);
    }
}
</pre>

As apfloats are immutable, they can be easily passed by reference. Also
the mantissa data of numbers can be efficiently shared in various situations.<p>

An inherent property of an {@link org.apfloat.Apfloat} is the <code>radix</code>.
The radix is specified at the time an apfloat is created. Due to the way the
default implementation works, there is no real performance difference in using
radix 2 or some other radix in the internal calculations. While it's generally
not possible to use numbers in different radixes in operations, it's possible
to convert a number to a different radix using the {@link org.apfloat.Apfloat#toRadix(int)}
method.<p>

The rounding mode for apfloat calculations is undefined. Thus, it's not
guaranteed that rounding happens to an optimal direction and more often
than not it doesn't. This should be carefully considered when designing
numerical algorithms. Round-off errors can accumulate faster than expected,
and loss of precision (as returned by {@link org.apfloat.Apfloat#precision()})
can happen quickly. This bad behaviour is further accelerated by using a radix
bigger than two, e.g. base 10, which is the default. Note that precision is
defined as the number of digits in the number's radix. If numbers need to
be rounded in a specific way then the {@link org.apfloat.ApfloatMath#round(Apfloat,long,RoundingMode)}
method can be invoked explicitly.<p>

Generally, the result of various mathematical operations is accurate to
the second last digit in the resulting number. This means roughly that the
last significant digit of the result can be inaccurate. For example, the
number 12345, with precision 5, should be considered 12345&nbsp;&#177;&nbsp;10.
This should generally not be a problem, as you should typically be using
apfloats for calculations with a precision of thousands or millions of
digits.<p>

There is no concept of an infinity or Not-a-Number with apfloats. Whenever
the result of an operation would be infinite or undefined, an exception is
thrown (usually an <code>ArithmeticException</code>).<p>

All of the apfloat-specific exceptions being thrown by the apfloat library
extend the base class {@link org.apfloat.ApfloatRuntimeException}. This
exception, or various subclasses can be thrown in different situations, for
example:

<ul>
  <li>{@link org.apfloat.InfiniteExpansionException} - The result of an
      operation would have infinite size. For example,
      <code>new Apfloat(2).divide(new Apfloat(3))</code>, in radix 10.</li>
  <li>{@link org.apfloat.OverflowException} - Overflow. If the
      exponent is too large to fit in a <code>long</code>,
      the situation can't be handled. Also, there is no "infinity" apfloat
      value that could be returned as the result.</li>
  <li>{@link org.apfloat.LossOfPrecisionException} - Total loss of precision.
      For example, <code>ApfloatMath.sin(new Apfloat(1e100))</code>.
      If the magnitude (100) is far greater than the precision (1) then
      the value of the <code>sin()</code> function can't be determined
      to any accuracy.</li>
</ul>

The exception is a <code>RuntimeException</code>, because it should "never happen",
and in general the cases where it is thrown are irrecoverable with the current
implementation. Also any of the situations mentioned above may be relaxed in
the future, so this exception handling strategy should be more future-proof
than others, even if it has its limitations currently.<p>

The {@link org.apfloat.Apfloat} class is the basic building block of all the
objects used in the apfloat package. An {@link org.apfloat.Apcomplex} simply
consists of two apfloats, the real part and the imaginary part. An
{@link org.apfloat.Apint} is implemented with an apfloat and all its operations
just guarantee that the number never gets a fractional part. Last, an
{@link org.apfloat.Aprational} is an aggregate of two apints, the numerator
and the denominator. The relations of these classes are shown in a class
diagram format below:<p>

<img src="doc-files/apfloat-classes.gif"><p>
*/

package org.apfloat;

import java.math.RoundingMode;
