/**
The apfloat Service Provider Interface (SPI).<p>

The apfloat API is a high-level API that defines algorithms on the level of
e.g. the Newton iteration for the inverse of a number. Behind this high-level
API there is a lot of low-level functionality that makes all the arbitrary
precision arithmetic happen. The digits of a large number are stored in an
array of <code>int</code>s, for example. In fact, an {@link org.apfloat.Apfloat}
is structurally just a pointer to an {@link org.apfloat.spi.ApfloatImpl}, and
most of the functionality of the Apfloat class is simply delegated to the
underlying ApfloatImpl.<p>

The apfloat SPI defines the general interface for the low-level things that
must happen behind the scenes of the high-level API. An actual implementation
of the SPI can be optimized for different things, for example:
<ul>
  <li>Size of numbers: different algorithms are efficient for numbers with
      with 1000 or 1000000 digits. This applies both to the actual storage
      method of the data, and the mathematical algorithms used for e.g.
      multiplying numbers.</li>
  <li>Memory consumption vs. performance: different types of Fast Fourier
      Transform (FFT) based algorithms can be used to find a suitable trade-off
      between memory consumption and performance.</li>
  <li>Hardware architecture: 32-bit and 64-bit systems handle <code>int</code>
      and <code>long</code> type elements correspondingly most efficiently, for
      example. Some systems perform floating-point operations (with
      <code>float</code> or <code>double</code> type elements) faster than
      integer operations (<code>int</code> or <code>long</code>).</li>
  <li>Complexity: a more complex implementation may be optimized for different
      cases, however more code will take more space and use more memory. This
      may be a concern on some systems (e.g. mobile devices).</li>
</ul>

A Service Provider is only required to implement the {@link org.apfloat.spi.BuilderFactory}
interface, and actually only the {@link org.apfloat.spi.BuilderFactory#getApfloatBuilder()}
method in this interface. All apfloat implementations ({@link org.apfloat.spi.ApfloatImpl})
are created through the {@link org.apfloat.spi.ApfloatBuilder} interface's
methods. The rest of the interfaces in the SPI exist only for the convenience
of the default apfloat SPI implementations ({@link org.apfloat.internal}).<p>

The apfloat SPI suggests the usage of various patterns, as encouraged by the
specification of all the interfaces in the SPI. These patterns include:

<ul>
  <li>Abstract factory pattern, for getting instances of the various builders,
      as well as other types of components built by the different builders
      ({@link org.apfloat.spi.ApfloatBuilder}, {@link org.apfloat.spi.DataStorageBuilder},
      {@link org.apfloat.spi.ConvolutionBuilder}, {@link org.apfloat.spi.NTTBuilder}).</li>
  <li>Factory method pattern; obviously the abstract factories use factory
      methods to create instances of objects.</li>
  <li>Builder pattern: an {@link org.apfloat.spi.ApfloatImpl} needs various
      "parts" for its structural construction ({@link org.apfloat.spi.DataStorage})
      as well as its behavior ({@link org.apfloat.spi.ConvolutionStrategy}). Builders
      are used to build the different sub-parts needed, and the ApfloatImpl
      itself only knows the high-level algorithm for how the parts are used and
      related. The construction of the sub-part details is left for the
      builders, and the ApfloatImpl accesses the parts only via an interface.</li>
  <li>Strategy pattern: for multiplying numbers, completely different algorithms
      are optimal, depending on the size of the numbers. The
      {@link org.apfloat.spi.ConvolutionStrategy} defines different convolution
      algorithms to be used in the multiplication. For very large numbers, a
      transform-based convolution can be used, and even a different transform
      strategy can be specified via the {@link org.apfloat.spi.NTTStrategy}
      interface.</li>
  <li>Iterators are used for iterating through {@link org.apfloat.spi.DataStorage}
      elements in a highly flexible manner. The base class is
      {@link org.apfloat.spi.DataStorage.Iterator}. For example, a data storage that
      uses a simple array to store the entire data set in memory can return
      a simple iterator that goes through the array element by element. In
      comparison, a data storage that stores the data in a disk file, can have
      an iterator that reads blocks of data from the file to a memory array,
      and then iterates through the array, one block at a time.</li>
  <li>Singleton pattern, assumed to be used in the {@link org.apfloat.spi.BuilderFactory}
      class, as there should be no need to have more than one instance of each
      builder class. Also the BuilderFactory instance itself is a singleton,
      within an {@link org.apfloat.ApfloatContext}.</li>
  <li>Bridge pattern: the SPI itself is the bridge pattern. An {@link org.apfloat.Apfloat}
      provides a simple high-level programming interface and the complex technical
      implementation details are delegated to an {@link org.apfloat.spi.ApfloatImpl}.
      The Apfloat class can be subclassed for additional functionality, and independent
      of that, different subclasses of an ApfloatImpl can be used to optimize the
      implementation.</li>
</ul>

Associations of the SPI classes are shown in a class diagram format below:<p>

<img src="doc-files/spi-classes.gif"><p>

The class implementing {@link org.apfloat.spi.BuilderFactory} that is used in
creating apfloat implementations is defined in the {@link org.apfloat.ApfloatContext}.
You can set the BuilderFactory instance programmatically by calling
{@link org.apfloat.ApfloatContext#setBuilderFactory(BuilderFactory)},
for example:<p>

<pre>
BuilderFactory builderFactory = new MyBuilderFactory();
ApfloatContext.getContext().setBuilderFactory(builderFactory);
</pre>

It's a lot easier to specify this to happen automatically whenever your program
starts. To do this just specify the BuilderFactory class name in the
<code>apfloat.properties</code> file (or the apfloat ResourceBundle if you use one).
For example, the <code>apfloat.properties</code> file might contain the line:<p>

<pre>
builderFactory=org.mycompany.MyBuilderFactory
</pre>

For more details about configuring the apfloat BuilderFactory, see the
documentation for {@link org.apfloat.ApfloatContext}.

@see org.apfloat.internal
*/

package org.apfloat.spi;
