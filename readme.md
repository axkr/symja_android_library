## Symja Library - Java Symbolic Math System for Android NCalc calculator

**Note**: this repository contains the **Java 8** version of the project. The [NCalc](https://github.com/tranleduy2000/ncalc) Android calculator project 
maintains a **[Java 7 Android branch](https://github.com/tranleduy2000/symja_android_library)** of the [Android *.AAR library](https://github.com/tranleduy2000/symja_android_library/releases).

Try the Android or iOS apps:

<a href="https://play.google.com/store/apps/details?id=com.duy.calculator.free">
	<img src="en_badge_web_generic.png" alt="Google Play" width="200"></a>
<a href="https://itunes.apple.com/us/app/ncalc-scientific-calculator/id1449106995">
	<img src="http://www.lawprose.org/wordpress/wp-content/uploads/App-Store-Badge.png" alt="App Store" width="200"></a>

or help testing the latest [Android BETA version](https://github.com/axkr/symja_android_library/wiki/BETA-tests) or the web demo at [matheclipse.org](http://matheclipse.org/). **Tip**: You can use the [Genymobile/scrcpy](https://github.com/Genymobile/scrcpy) tool for faster testing your Symja script inputs on your android device with BETA version installed.

Read the [Symja Manual](symja_android_library/doc/index.md) for the description of the Symja language or [browse the available functions](symja_android_library/doc/functions/). We encourage everyone to participate in our [Wiki](https://github.com/axkr/symja_android_library/wiki).

[![Join the chat at https://gitter.im/symja_android_library/Lobby](https://badges.gitter.im/symja_android_library/Lobby.svg)](https://gitter.im/symja_android_library/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

- [Features](#features)
- [Applications](#applications)
- [Examples](#examples)
- [Maven Usage](#maven-usage)
- [Getting started](#getting-started)
- [Github GIT](#github-git)
- [Contact](#contact)
- [License](#license)

### Features

Features of the Symja language:

* arbitrary precision integers, rational and complex numbers. Polynomial, list functions and [Association](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Association.md)s
* [differentiation](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/D.md), [integration](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Integrate.md), [equation solving](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Solve.md), [linear algebra](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/linear-algebra.md), [number theory](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/number-theoretic-functions.md), [combinatorial](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/combinatorial.md), [logic](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/logic.md) and polynomial functions...
* a general purpose [Term Rewriting System and Pattern Matching engine](symja_android_library/doc/functions-and-patterns.md)
* use human readable math expression strings or the internal abstract syntax tree (AST) representation to code in Java. See the [Unit test examples](symja_android_library/matheclipse-core/src/test/java/org/matheclipse/core/system/LowercaseTestCase.java)
* two [REPLs](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop) are available in the library. A [Console.java](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/eval/Console.java) for standard math input and a [MMAConsole.java](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/eval/MMAConsole.java) for "Mathematica like syntax" input.
* Symja with "Mathematica like syntax" input can also be used interactively in a [BeakerX/Jupyter Lab](https://github.com/axkr/symja_android_library/wiki/BeakerX-usage) environment
* Symja can also be used interactively in the [Java jshell](https://github.com/axkr/symja_android_library/wiki/JShell-usage)
* the [Rubi symbolic integration rules](https://github.com/axkr/symja_android_library/wiki/Porting-Rubi-Integration-rules-to-Symja) are used to implement the [Integrate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Integrate.md) function, they can be systematically applied to determine the antiderivative of a wide variety of mathematical expressions. 

### Applications

* [Appengine web interface symjaweb.appspot.com](http://symjaweb.appspot.com/) - available as open source in this [Github repository](https://github.com/axkr/symja_web) 

* [Android App Calculator N+ on Google play store](https://play.google.com/store/apps/details?id=com.duy.calculator.free) - available as open source in this [Github repository](https://github.com/tranleduy2000/ncalc) provides an **IDE mode** to calculate arbitrary Symja expressions.

* [Eclipse EASE extension - use Symja as a REPL in Eclipse](https://github.com/axkr/ease_symja)

### Examples

To get an idea of the kinds of expressions Symja handles, see the [tests in this file](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/test/java/org/matheclipse/core/system/LowercaseTestCase.java).

![Console Examples](console.gif)

#### Web Examples

> [Solve({x^2==4,x+y^2==6}, {x,y})](http://matheclipse.org/input?i=Solve({x^2==4,x%2By^2==6},%20{x,y}))

> [FactorInteger(2^15-5)](http://matheclipse.org/input?i=FactorInteger(2^15-5))

> [D(Sin(x^3), x)](http://matheclipse.org/input?i=D(Sin(x^3),%20x))

> [Factor(-1+x^16)](http://matheclipse.org/input?i=Factor(-1%2Bx^16))

> [Manipulate(Plot3D(Sin(a * x * y), {x, -1.5, 1.5}, {y, -1.5, 1.5}), {a,1,5})](http://matheclipse.org/input?i=Manipulate(Plot3D(Sin(a*x*y),%20{x,%20-1.5,%201.5},%20{y,%20-1.5,%201.5}),%20{a,1,5}))
 
>[Plot(Piecewise({{x^2, x < 0}, {x, x >= 0&&x<1},{Cos(x-1), x >= 1}}), {x, -2, 12})](http://matheclipse.org/input?i=Plot(Piecewise({{x^2,%20x%20%3C%200},%20{x,%20x%20%3E=%200%26%26x%3C1},{Cos(x-1),%20x%20%3E=%201}}),%20{x,%20-2,%2012}))

> [Refine(Abs(n*Abs(m)),n<0)](http://matheclipse.org/input?i=Refine(Abs(n*Abs(m)),%20n%3C0))

> [Inverse({{1,2},{3,4}})](http://matheclipse.org/input?i=Inverse({{1,2},{3,4}}))

> [Det({{1,2},{3,4}})](http://matheclipse.org/input?i=Det({{1,2},{3,4}}))

> [Integrate(Cos(x)^5, x)](http://matheclipse.org/input?i=Integrate(Cos(x)^5,%20x))

A [Java usage example](https://github.com/axkr/symja_android_library/wiki/Java-usage):

```java
package org.matheclipse.core.examples;

import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.math.MathException;

public class Example {
	public static void main(String[] args) {
		try {
			ExprEvaluator util = new ExprEvaluator(false, 100);

			// Convert an expression to the internal Java form:
			// Note: single character identifiers are case sensitive
			// (the "D()" function identifier must be written as upper case
			// character)
			String javaForm = util.toJavaForm("D(sin(x)*cos(x),x)");
			// prints: D(Times(Sin(x),Cos(x)),x)
			System.out.println("Out[1]: " + javaForm.toString());

			// Use the Java form to create an expression with F.* static
			// methods:
			ISymbol x = F.Dummy("x");
			IAST function = F.D(F.Times(F.Sin(x), F.Cos(x)), x);
			IExpr result = util.eval(function);
			// print: Cos(x)^2-Sin(x)^2
			System.out.println("Out[2]: " + result.toString());

			// Note "diff" is an alias for the "D" function
			result = util.eval("diff(sin(x)*cos(x),x)");
			// print: Cos(x)^2-Sin(x)^2
			System.out.println("Out[3]: " + result.toString());

			// evaluate the last result (% contains "last answer")
			result = util.eval("%+cos(x)^2");
			// print: 2*Cos(x)^2-Sin(x)^2
			System.out.println("Out[4]: " + result.toString());

			// evaluate an Integrate[] expression
			result = util.eval("integrate(sin(x)^5,x)");
			// print: 2/3*Cos(x)^3-1/5*Cos(x)^5-Cos(x)
			System.out.println("Out[5]: " + result.toString());

			// set the value of a variable "a" to 10
			result = util.eval("a=10");
			// print: 10
			System.out.println("Out[6]: " + result.toString());

			// do a calculation with variable "a"
			result = util.eval("a*3+b");
			// print: 30+b
			System.out.println("Out[7]: " + result.toString());

			// Do a calculation in "numeric mode" with the N() function
			// Note: single character identifiers are case sensistive
			// (the "N()" function identifier must be written as upper case
			// character)
			result = util.eval("N(sinh(5))");
			// print: 74.20321057778875
			System.out.println("Out[8]: " + result.toString());

			// define a function with a recursive factorial function definition.
			// Note: fac(0) is the stop condition.
			result = util.eval("fac(x_Integer):=x*fac(x-1);fac(0)=1");
			// now calculate factorial of 10:
			result = util.eval("fac(10)");
			// print: 3628800
			System.out.println("Out[9]: " + result.toString());

			function = F.Function(F.Divide(F.Gamma(F.Plus(F.C1, F.Slot1)), F.Gamma(F.Plus(F.C1, F.Slot2))));
			// eval function ( Gamma(1+#1)/Gamma(1+#2) ) & [23,20]
			result = util.evalFunction(function, "23", "20");
			// print: 10626
			System.out.println("Out[10]: " + result.toString());
		} catch (SyntaxError e) {
			// catch Symja parser errors here
			System.out.println(e.getMessage());
		} catch (MathException me) {
			// catch Symja math errors here
			System.out.println(me.getMessage());
		} catch (final Exception ex) {
			System.out.println(ex.getMessage());
		} catch (final StackOverflowError soe) {
			System.out.println(soe.getMessage());
		} catch (final OutOfMemoryError oome) {
			System.out.println(oome.getMessage());
		}
	}
}
```


### Maven Usage

Using Maven, add the following to your `pom.xml`

```xml
<dependencies>
  <dependency>
    <groupId>org.matheclipse</groupId>
      <artifactId>matheclipse-core</artifactId>
	  <version>1.0.0-SNAPSHOT</version>
    </dependency>
</dependencies>

<repositories> 
  <repository>
    <id>snapshots-repo</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases>
      <enabled>false</enabled>
    </releases>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```

and run

```
mvn clean install
```

With the following command you can run the Symja console from the command line

```
mvn exec:java -pl matheclipse-core
```
 
With the following command you can run a symja console with a Mathematica-compatible syntax and functions

```
mvn exec:java@mma -pl matheclipse-core 
```

With the following command you can build a fat jar which contains all needed classes

```
mvn assembly:single -pl matheclipse-core
```

### Getting started

First, you'll need a Java Development Kit compatible with Java 8 or later.

You can find JDK installers at:

* http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

If you're unsure how to install the JDK, you can find instructions for
all operating systems here: 

* https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html

Pay careful attention to anything about setting up your `PATH` or `CLASSPATH`.

Install and open the latest version of the Eclipse development IDE for Java Developers:

* http://www.eclipse.org/downloads/packages/

### Github GIT

a) Fork the Symja repository to use as a starting point.

* Navigate to [github.com/axkr/symja_android_library](https://github.com/axkr/symja_android_library)  in your browser.
* Click the "Fork" button in the top-right of the page.
* Once your fork is ready, open the new repository's "Settings" by clicking the link in the menu bar on the left.
* Change the repository name to the name of your Library and save your changes.
  
b) Clone your new repository to your Eclipse workspace.

* Open Eclipse and select the "File -> Import..." menu item.
* Select "Git -> Projects from Git", and click "Next >".
* Select "URI" and click "Next >". 
* Enter your repository's clone URL in the "URI" field. The remaining fields in the "Location" and "Connection" groups will get automatically filled in.
* Enter your Github credentials in the "Authentication" group, and click "Next >".
* Select the `master` branch on the next screen, and click "Next >".
* The default settings on the "Local Configuration" screen should work fine, click "Next >".
* Make sure "Import existing projects" is selected, and click "Next >".
* Eclipse should find and select the `symja_android_library` automatically, click "Finish".

See this [Git version control with Eclipse (EGit) - Tutorial](http://www.vogella.com/tutorials/EclipseGit/article.html) for a general overview.

### Contact

If you have any questions about using or developing for this project, send me an [email][1]!

### License

* the complete Symja system is published under the GNU GENERAL PUBLIC LICENSE Version 3.

If you would like to use parts of the system here are some of the associated licenses:

* the [JAS Java Algebra System](http://krum.rz.uni-mannheim.de/jas/) is published under the (LESSER) GNU GENERAL PUBLIC LICENSE license. The Java bytecode is dual licenced also under the Apache 2.0 license to allow usage in Android projects.  
* the [apfloat project](https://github.com/mtommila/apfloat) is published under the (LESSER) GNU GENERAL PUBLIC LICENSE license. 
* the [LogicNG project](https://github.com/logic-ng/LogicNG) is published under the Apache software license
* the [Hipparchus Mathematics Library](https://www.hipparchus.org/) is published under the Apache software license
* the [JGraphT Library](https://jgrapht.org/)  is published under the Eclipse Public License (EPL) or (LESSER) GNU GENERAL PUBLIC LICENSE license. 
* the Symja parser libraries (org.matheclipse.parser* packages) are published under the APACHE LICENSE Version 2.0.

[1]: mailto:axelclk@gmail.com
[2]: http://www.vogella.com/tutorials/EclipseGit/article.html
