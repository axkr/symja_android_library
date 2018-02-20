/**
Default implementations of the apfloat Service Provider Interface (SPI).<p>

The <code>org.apfloat.internal</code> package contains four different
implementations of the apfloat SPI, each based on a different primitive
element type:

<ul>
  <li>{@link org.apfloat.internal.IntBuilderFactory}, based on element type
      <code>int</code>: This is the default implementation used by apfloat.
      It works well for 32-bit platforms that perform integer operations fast
      (including integer multiplication), and can multiply <code>double</code>s
      and convert between <code>double</code> and <code>int</code> with adequate
      performance. This applies to most workstations today (Intel x86 processors
      and compatibles, in particular processors with SSE2 support, and most RISC
      architectures). You can do calculations up to roughly 226 million digits
      (in radix 10) with this implementation, which should be enough for most
      purposes.</li>
  <li>{@link org.apfloat.internal.LongBuilderFactory}, based on element type
      <code>long</code>: This implementation uses the 64-bit <code>long</code>
      integer as the elementary type for all data storage and manipulation. It
      usually is faster than the <code>int</code> version on 64-bit architectures
      if you have a JVM that actually uses the 64-bit features of the processor.
      In some places it uses also <code>double</code> arithmetic, so the processor
      should be able to perform double-precision floating point operations as well
      as convert between <code>double</code> and <code>long</code>, for decent
      performance. For example, on x86-64 and SPARC the 64-bit <code>long</code>
      version is faster than the 32-bit <code>int</code> version. You can use the
      <code>long</code> implementation on 32-bit platforms too, however the
      performance per element is less than half of the <code>int</code> version,
      even if roughly twice as much data is processed per element. The upside
      is that this implementation can do much bigger calculations: up to about
      3.5&nbsp;*&nbsp;10<sup>15</sup> digits in radix 10.</li>
  <li>{@link org.apfloat.internal.DoubleBuilderFactory}, based on element type
      <code>double</code>: This implementation exists generally only as a
      curiosity. It will typically perform worse than the <code>long</code>
      version, and it's only able to do calculations with about 1/20 of its
      maximum digit length. The only situation where using the <code>double</code>
      version might make sense is on a platform that performs floating-point
      arithmetic well, but performs integer arithmetic extremely badly. Finding
      such a platform today might be difficult, so generally it's advisable to
      use the <code>long</code> version instead, if you have a 64-bit platform
      or need the most extreme precision.</li>
  <li>{@link org.apfloat.internal.FloatBuilderFactory}, based on element type
      <code>float</code>: This version is also only a curiosity. The main
      downside is that it can only perform calculations up to about 1.3
      million radix-10 digits. The per-digit performance is also typically
      less than that of the <code>int</code> version. Unless you have a
      computer that performs floating-point arithmetic extraordinarily well
      compared to integer arithmetic, it's always advisable to use the
      <code>int</code> version instead.</li>
</ul>

For example, the relative performance of the above implementations on some
CPUs is as follows (bigger percentage means better performance):<p>

<table border="1">
<tr><th>Type</th><th>Pentium 4</th><th>Athlon XP</th><th>Athlon 64 (32-bit)</th><th>Athlon 64 (64-bit)</th><th>UltraSPARC II</th></tr>
<tr><td>Int</td><td>100%</td><td>100%</td><td>100%</td><td>100%</td><td>100%</td></tr>
<tr><td>Long</td><td>40%</td><td>76%</td><td>59%</td><td>95%</td><td>132%</td></tr>
<tr><td>Double</td><td>45%</td><td>63%</td><td>59%</td><td>94%</td><td>120%</td></tr>
<tr><td>Float</td><td>40%</td><td>43%</td><td>46%</td><td>42%</td><td>82%</td></tr>
</table><br>
(Test was done with apfloat 1.1 using Sun's Java 5.0 server VM calculating &pi; to
one million digits with no disk storage.)<p>

Compared to the <code>java.math.BigInteger</code> class with different digit
sizes, the apfloat relative performance with the same CPUs is as follows:<p>

<img src="doc-files/biginteger-comparison.gif"><p>

(Test was done with apfloat 1.1 using Sun's Java 5.0 server VM calculating
3<sup>n</sup> and converting the result to decimal.)<p>

This benchmark suggests that for small numbers &#150; less than roughly 200 decimal
digits in size &#150; the <code>BigInteger</code> / <code>BigDecimal</code> classes
are probably faster, even by an order of magnitude. Using apfloats is only beneficial
for numbers that have at least a couple hundred digits, or of course if some
mathematical functions are needed that are not available for <code>BigInteger</code>s
or <code>BigDecimal</code>s. The results can be easily explained by the smaller overhead
that <code>BigInteger</code>s have due to their simpler implementation. When the size
of the mantissa grows, the O(n log n) complexity of apfloat's FFT-based multiplication
makes apfloat considerably faster than the steady O(n<sup>2</sup>) implementation
of the <code>BigInteger</code> class. For numbers with millions of digits,
multiplication using <code>BigInteger</code>s would be simply unfeasible, whereas for
apfloat it would not be a problem at all.<p>

All of the above apfloat implementations have the following features (some of the links
point to the <code>int</code> version, but all four versions have similar classes):

<ul>
  <li>Depending on the size, numbers can be stored in memory
      ({@link org.apfloat.internal.IntMemoryDataStorage}) or on disk
      ({@link org.apfloat.internal.IntDiskDataStorage}).</li>
  <li>Multiplication can be done in an optimized way if one multiplicand
      has size 1 ({@link org.apfloat.internal.IntShortConvolutionStrategy}),
      using a simple O(n<sup>2</sup>) long multiplication algorithm for small numbers,
      with low overhead ({@link org.apfloat.internal.IntMediumConvolutionStrategy}),
      using the Karatsuba multiplication algorithm for slightly larger numbers,
      with some more overhead ({@link org.apfloat.internal.IntKaratsubaConvolutionStrategy}),
      or using a Number Theoretic Transform (NTT) done using three different moduli,
      and the final result calculated using the Chinese Remainder Theorem
      ({@link org.apfloat.internal.ThreeNTTConvolutionStrategy}), for big numbers.</li>
  <li>Different NTT algorithms for different transform lengths: basic fast NTT
      ({@link org.apfloat.internal.IntTableFNTStrategy}) when the entire transform
      fits in the processor cache, "six-step" NTT when the transform fits in the
      main memory ({@link org.apfloat.internal.SixStepFNTStrategy}),
      and a disk-based "two-pass" NTT strategy when the whole transform doesn't
      fit in the available memory ({@link org.apfloat.internal.TwoPassFNTStrategy}).</li>
</ul>

The apfloat implementation-specific exceptions being thrown by the apfloat library
all extend the base class {@link org.apfloat.internal.ApfloatInternalException}.
This exception, or various subclasses can be thrown in different situations, for
example:

<ul>
  <li>Backing storage failure. For example, if a number is stored on disk,
      an <code>IOException</code> can be thrown in any of the disk operations,
      if e.g. a file can't be created, or written to if the disk is full.</li>
  <li>Operands have different radixes. This is a limitation allowed by the
      specification.</li>
  <li>Other internal limitation, e.g. the maximum transform length
      mathematically possible for the implementation, is exceeded.</li>
</ul>

Note in particular that numbers, which take a lot of space are stored on disk
in temporary files. These files have by default the extension <code>*.ap</code>
and they are by default created in the current working directory. When the objects
are garbage collected, the temporary files are deleted. However, garbage collection
may not work perfectly at all times, and in general there are no guarantees that
it will happen at all. So, depending on the program being executed, it may be
beneficial to explicitly call <code>System.gc()</code> at some point to ensure
that unused temporary files are deleted. However, VM vendors generally warn
against doing this too often, since it may seriously degrade performance. So,
figuring out how to optimally call it may be difficult. If the file deletion fails
for some reason, some temporary files may be left on disk after the program
exits. These files can be safely removed after the program has terminated.<p>

Many parts of the program are parallelized i.e. are processed with multiple threads
in parallel. Parallelization is done where it has been easy to implement and where
it is efficient. E.g. the "six-step" NTT is parallelized, because the data is in
matrix form in memory and it's easy and highly efficient to process the rows of the
matrix in parallel. Other places where parallelization is implemented are the
in-place multiplication of transform results and the carry-CRT operation. However
in both of these algorithms the process is parallelized only if the data is in
memory - if the data was stored on disk then the irregular disk seeking could make
the parallel algorithm highly inefficient.<p>

Many sections of the code are not parallelized, where it's obvious that
parallelization would not bring any benefits. Examples of such cases are
addition, subtraction and matrix transposition. While parallel algorithms for
these operations could certainly be implemented, they would not bring any
performance improvement. The bottleneck in these operations is memory or I/O
bandwidth and not CPU processing time. The CPU processing in addition and
subtraction is highly trivial; in matrix transposition it's outright
nonexistent - the algorithm only moves data from one place to another. Even
if all the data was stored in memory, the memory bandwidth would be the
bottleneck. E.g. in addition, the algorithm only needs a few CPU cycles per
element to be processed. However moving the data from main memory to CPU
registers and back to main memory needs likely significantly more CPU cycles
than the addition operation itself. Parallelization would therefore not
improve efficiency at all - the total CPU load might appear to increase but
when measured in wall-clock time the execution would not be any faster.<p>

Since the core functionality of the apfloat implementation is based on the
original C++ version of apfloat, no significant new algorithms have been
added (although the architecture has been otherwise greatly beautified e.g. by
separating the different implementations behind a SPI, and applying all kinds
of patterns everywhere). Thus, there are no different implementations for e.g.
using a floating-point FFT instead of a NTT, as the SPI ({@link org.apfloat.spi})
might suggest. However the default implementation does implement all the
patterns suggested by the SPI &#150; in fact the SPI was designed for the
default implementation.<p>

The class diagram for an example apfloat that is stored on disk is shown below.
Note that all the aggregate classes can be shared by multiple objects that point
to the same instance. For example, multiple Apfloats can point to the same
ApfloatImpl, multiple ApfloatImpls can point to the same DataStorage etc. This
sharing happens in various situations, e.g. by calling <code>floor()</code>,
multiplying by one etc:<p>

<img src="doc-files/implementation-classes.gif"><p>

The sequence diagram for creating a new apfloat that is stored on disk is as
follows. Note that the FileStorage class is a private inner class of the
DiskDataStorage class:<p>

<img src="doc-files/new-sequence.gif"><p>

The sequence diagram for multiplying two apfloats is as follows. In this case a
NTT based convolution is used, and the resulting apfloat is stored in memory:<p>

<img src="doc-files/multiply-sequence.gif"><p>

Most of the files in the apfloat implementations are generated from templates
where a template tag is replaced by <code>int/long/float/double</code> or
<code>Int/Long/Float/Double</code>. Also the byte size of the element type is
templatized and replaced by 4/8/4/8. The only files that are individually
implemented for each element type are:<p>

<pre>
*BaseMath.java
*CRTMath.java
*ElementaryModMath.java
*ModConstants.java
</pre>

@see org.apfloat.spi
*/

package org.apfloat.internal;
