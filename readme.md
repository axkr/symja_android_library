## Symja Library - Java Symbolic Math System for Android calculator

[![Join our Discord](https://img.shields.io/discord/869895703718166529?color=7289da&label=Join%20our%20Discord&logo=discord&style=for-the-badge)](https://discord.gg/tYknzr2qam)
[![Symja](https://img.shields.io/docker/pulls/symja/symja-3.3?logo=docker&style=for-the-badge)](https://hub.docker.com/r/symja/symja-3.3)
[![Maven Central](https://img.shields.io/maven-central/v/org.matheclipse/matheclipse?style=for-the-badge)](https://search.maven.org/search?q=g:org.matheclipse)

[![Build Master Snapshot](https://github.com/axkr/symja_android_library/actions/workflows/maven-build-master-and-publish-snapshot.yml/badge.svg)](https://github.com/axkr/symja_android_library/actions/workflows/maven-build-master-and-publish-snapshot.yml)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/org.matheclipse/matheclipse?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/org/matheclipse/)

**Note**: this repository contains the **Java 11** sources of the core modules. A minimal Android app example can be found in the [symja-example repository ](https://github.com/axkr/symja-example):

<img src="https://github.com/axkr/symja-example/blob/main/docs/img_1.png" width="200" height="auto"> <img src="https://github.com/axkr/symja-example/blob/main/docs/img_2.png" width="200" height="auto">

Try the full-blown Android or iOS apps:
<ul>
<li><a href="https://play.google.com/store/apps/details?id=com.duy.calculator.free">
	<img src="https://raw.githubusercontent.com/axkr/symja_android_library/master/screenshots/google_playstore_badge.png" alt="Google Play" width="200"></a></li>
<li><a href="https://itunes.apple.com/us/app/ncalc-scientific-calculator/id1449106995">
    <img src="https://raw.githubusercontent.com/axkr/symja_android_library/master/screenshots/appstore_badge.png" alt="App Store" width="200"></a></li>
</ul>

or help testing the latest [Android BETA version](https://github.com/axkr/symja_android_library/wiki/BETA-tests) or the web demo at [matheclipse.org](https://matheclipse.org/).

Read the [Symja Manual](symja_android_library/doc/index.md) :blue_book: for the description of the Symja language or [browse the available functions](symja_android_library/doc/functions/) :green_book: . We encourage everyone to participate in our [Wiki](https://github.com/axkr/symja_android_library/wiki).

- [Installation](#installation)
- [Features](#features)
- [Applications](#applications)
- [Examples](#examples)
- [Maven Usage](#maven-usage)
- [Getting started](#getting-started)
- [Github GIT](#github-git)
- [Contact](#contact)
- [License](#license)
- [Science and math library dependencies](#science-and-math-library-dependencies-by-maven-module)

### 🔧 Installation <a name="installation"></a>

The different kinds of installations are described in the [Wiki Installation](https://github.com/axkr/symja_android_library/wiki/Installation).

### ✨ Features <a name="features"></a>

Features of the Symja language:

* arbitrary precision integers, rational and complex numbers. Polynomial, list functions and [Association](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Association.md)s
* [differentiation](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/D.md), [integration](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Integrate.md), [equation solving](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Solve.md), [linear algebra](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/98-function-by-category.md#linear-algebra), [number theory](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/98-function-by-category.md#number-theory), [combinatorial](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/98-function-by-category.md#combinatorial), [logic](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/03-comparisons-and-boolean-logic.md) and polynomial functions...
* unified connectivity and interoperability through [Symja functions](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions) for open source libraries like [Hipparchus](https://github.com/Hipparchus-Math/hipparchus), [Tablesaw](https://github.com/jtablesaw/tablesaw), [JGraphT](https://github.com/jgrapht/jgrapht), [LogicNG](https://github.com/logic-ng/LogicNG), [JAS Java Algebra System](https://github.com/kredel/java-algebra-system), [apfloat](https://github.com/mtommila/apfloat)...
* a general purpose [Term Rewriting System and Pattern Matching engine](symja_android_library/doc/07-functions-and-patterns.md)
* use human readable math expression strings or the internal abstract syntax tree (AST) representation to code in Java. See the [Unit test examples](symja_android_library/matheclipse-core/src/test/java/org/matheclipse/core/system/LowercaseTestCase.java)
* two [Java servlet](https://en.wikipedia.org/wiki/Jakarta_Servlet) based notebook interfaces are available in the library. A [Symja server](https://github.com/axkr/symja_android_library/wiki/Symja-browser-usage) for traditional math input and a [MMA server](https://github.com/axkr/symja_android_library/wiki/MMA-browser-usage) for "Mathematica like syntax" input
* two [REPLs](https://en.wikipedia.org/wiki/Read%E2%80%93eval%E2%80%93print_loop) are available in the library. A [Console](https://github.com/axkr/symja_android_library/wiki/Console-usage) for standard math input and a [MMAConsole](https://github.com/axkr/symja_android_library/wiki/MMA-console-usage) for "Mathematica like syntax" input.
* new "script-functions" can be developed as [Packages](https://github.com/axkr/symja_android_library/wiki/context) and loaded into the system
* developers can use Symja interactively in the [Java jshell](https://github.com/axkr/symja_android_library/wiki/JShell-usage) or with a call to to the [JSON Web API Server](https://github.com/axkr/symja_android_library/wiki/API)
* the [Rubi symbolic integration rules](https://github.com/axkr/symja_android_library/wiki/Porting-Rubi-Integration-rules-to-Symja) are used to implement the [Integrate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Integrate.md) function, they can be systematically applied to determine the antiderivative of a wide variety of mathematical expressions
* [compiling numeric functions](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Compile.md) with the [Janino Java™ compiler](https://github.com/janino-compiler/janino)

### 📦 Applications <a name="applications"></a>

* [Symja.org](https://symja.org/) - test the [Symja API](https://github.com/axkr/symja_android_library/wiki/API)

* [Appengine web interface symjaweb.appspot.com](http://symjaweb.appspot.com/) - available as open source in this [Github repository](https://github.com/axkr/symja_web) 

* [Android App Calculator N+ on Google play store](https://play.google.com/store/apps/details?id=com.duy.calculator.free) - available as open source in this [Github repository](https://github.com/tranleduy2000/ncalc) provides an **IDE mode** to calculate arbitrary Symja expressions.

* [Eclipse EASE extension - use Symja as a REPL in Eclipse](https://github.com/axkr/ease_symja) with this [example Github repository](https://github.com/axkr/symja_examples) which contains some Symja example scripts.

### ☕ Examples <a name="examples"></a>
 
#### Console user interface

<img src="https://raw.githubusercontent.com/axkr/symja_android_library/master/screenshots/console.gif" width="100%"></img> 

#### HTML notebook interface 

<img src="https://raw.githubusercontent.com/axkr/symja_android_library/master/screenshots/symja-browser-usage-001.png" width="45%"></img> 
<img src="https://raw.githubusercontent.com/axkr/symja_android_library/master/screenshots/symja-browser-usage-002.png" width="45%"></img> 

<img src="https://raw.githubusercontent.com/axkr/symja_android_library/master/screenshots/Symja-screen1.png" width="30%"></img> 
<img src="https://raw.githubusercontent.com/axkr/symja_android_library/master/screenshots/Symja-screen2.png" width="30%"></img> 
<img src="https://raw.githubusercontent.com/axkr/symja_android_library/master/screenshots/Symja-screen3.png" width="30%"></img> 

To get an idea of the kinds of expressions Symja handles, see the [JUnit tests in this file](https://raw.githubusercontent.com/axkr/symja_android_library/master/symja_android_library/matheclipse-core/src/test/java/org/matheclipse/core/system/LowercaseTestCase.java).

#### Web Examples

> [Solve({x^2==4,x+y^2==6}, {x,y})](https://matheclipse.org/input?i=Solve({x^2==4,x%2By^2==6},%20{x,y}))

> [FactorInteger(2^15-5)](https://matheclipse.org/input?i=FactorInteger(2^15-5))

> [D(Sin(x^3), x)](https://matheclipse.org/input?i=D(Sin(x^3),%20x))

> [Factor(-1+x^16)](https://matheclipse.org/input?i=Factor(-1%2Bx^16))

> [Manipulate(Plot3D(Sin(a * x * y), {x, -1.5, 1.5}, {y, -1.5, 1.5}), {a,1,5})](https://matheclipse.org/input?i=Manipulate(Plot3D(Sin(a*x*y),%20{x,%20-1.5,%201.5},%20{y,%20-1.5,%201.5}),%20{a,1,5}))
 
>[Plot(Piecewise({{x^2, x < 0}, {x, x >= 0&&x<1},{Cos(x-1), x >= 1}}), {x, -2, 12})](https://matheclipse.org/input?i=Plot(Piecewise({{x^2,%20x%20%3C%200},%20{x,%20x%20%3E=%200%26%26x%3C1},{Cos(x-1),%20x%20%3E=%201}}),%20{x,%20-2,%2012}))

> [Refine(Abs(n*Abs(m)),n<0)](https://matheclipse.org/input?i=Refine(Abs(n*Abs(m)),%20n%3C0))

> [Inverse({{1,2},{3,4}})](https://matheclipse.org/input?i=Inverse({{1,2},{3,4}}))

> [Det({{1,2},{3,4}})](https://matheclipse.org/input?i=Det({{1,2},{3,4}}))

> [Integrate(Cos(x)^5, x)](https://matheclipse.org/input?i=Integrate(Cos(x)^5,%20x))

> [JavaForm((x+1)^2+(x+1)^3, Float)](https://matheclipse.org/input?i=JavaForm%28%28x%2B1%29%5E2%2B%28x%2B1%29%5E3%2C%20Float%29)

> [ToExpression("\\\\frac{x}{\\\\sqrt{5}}", TeXForm)](https://matheclipse.org/input?i=ToExpression%28%22%5C%5C%5C%5Cfrac%7Bx%7D%7B%5C%5C%5C%5Csqrt%7B5%7D%7D%22%2C%20TeXForm%29)

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
      ExprEvaluator util = new ExprEvaluator(false, (short) 100);

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

      // evaluate the last result (% or $ans contains "last answer")
      result = util.eval("%+cos(x)^2");
      // print: 2*Cos(x)^2-Sin(x)^2
      System.out.println("Out[4]: " + result.toString());

      // evaluate an Integrate[] expression
      result = util.eval("integrate(sin(x)^5,x)");
      // print: 2/3*Cos(x)^3-1/5*Cos(x)^5-Cos(x)
      System.out.println("Out[5]: " + result.toString());

      // set the value of a variable "a" to 10
      // Note: in server mode the variable name must have a preceding '$'
      // character
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

      function =
          F.Function(F.Divide(F.Gamma(F.Plus(F.C1, F.Slot1)), F.Gamma(F.Plus(F.C1, F.Slot2))));
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

### 🔨 Maven Usage <a name="maven-usage"></a>

How to use Maven is described in the [Maven wiki page](https://github.com/axkr/symja_android_library/wiki/Maven-usage).

### Getting started

First, you'll need a Java Development Kit (JDK) compatible with Java 11 or later.

The Integrated Development Environment (IDE) Eclipse is shipped with a suitable JDK, so you don't have to install a JDK by yourself.
Install and open the latest version of the Eclipse development IDE for Java Developers:

* https://www.eclipse.org/downloads/packages/

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

* the complete Symja system is published under the GNU GENERAL PUBLIC LICENSE Version 3 (GPL) starting with Symja version 2.0.0 parts are published under the Lesser GNU GENERAL PUBLIC LICENSE Version 3 (LGPL).

If you would like to use parts of the system here are some Maven module licenses:
* the maven modules: `parser, external, core` are published under LGPL license.
* the maven modules: `gpl, api, io` are published under GPL license.

Here are some of the associated **JavaScript** licenses:
* the [Paul Masson's Math project](https://github.com/paulmasson/math) is published under the MIT license. 
* the [Paul Masson's MathCell project](https://github.com/paulmasson/mathcell) is published under the MIT license. 
* the [JSXGraph project](https://github.com/jsxgraph/jsxgraph) is published under the GNU LGPL or MIT license.  
* the [KaTeX project](https://katex.org/) is published under the MIT license. 

### Science and math library dependencies by Maven module

The Maven modules below all build on `matheclipse-core`. A module inherits the dependencies of every module it depends on, so the licenses of `matheclipse-core` apply everywhere. `matheclipse-core` itself depends on `matheclipse-parser` and `matheclipse-external`; the third-party code vendored inside `matheclipse-external` is therefore listed together with `core`.

#### matheclipse-core

| Project | Purpose | License |
| --- | --- | --- |
| [Hipparchus](https://www.hipparchus.org/) | linear algebra, ODE solvers, optimization, statistics, FFT, geometry, clustering, curve fitting | Apache License 2.0 |
| [apfloat](https://github.com/mtommila/apfloat) | arbitrary precision arithmetic | MIT License |
| [LogicNG](https://github.com/logic-ng/LogicNG) | boolean logic, SAT solving | Apache License 2.0 |
| [Choco-solver](https://choco-solver.org/) | constraint programming | BSD 3-Clause License |
| [AdaptiveQuadrature](https://github.com/jonathanschilling/AdaptiveQuadrature) | adaptive Gauss-Kronrod numerical integration | Apache License 2.0 |
| [RoaringBitmap](https://github.com/RoaringBitmap/RoaringBitmap) | compressed bitsets | Apache License 2.0 |
| [MFL](https://github.com/HebiRobotics/MFL) | MATLAB `.mat` file import and export | Apache License 2.0 |
| [Paguro](https://github.com/GlenKPeterson/Paguro) | persistent (immutable) collections | Apache License 2.0 or EPL 1.0 |
| [JAS Java Algebra System](http://krum.rz.uni-mannheim.de/jas/) (vendored in `matheclipse-external`) | polynomial arithmetic, factorization, Groebner bases | LGPL; the Java bytecode is dual licensed under Apache 2.0 to allow usage in Android projects |
| [Cream](https://bach.istc.kobe-u.ac.jp/cream/) (vendored in `matheclipse-external`) | finite domain constraint solving | LGPL |
| [Diophantine](https://github.com/Mangara/Diophantine) (vendored in `matheclipse-external`) | solving Diophantine equations | MIT License |
| [SnuggleTeX](https://www2.ph.ed.ac.uk/snuggletex/) (vendored in `matheclipse-external`) | LaTeX to MathML conversion | BSD License |
| [fastutil](https://fastutil.di.unimi.it/) (slim fork vendored in `matheclipse-external`) | primitive type collections | Apache License 2.0 |

#### matheclipse-gpl

| Project | Purpose | License |
| --- | --- | --- |
| [java-math-library](https://github.com/TilmanNeumann/java-math-library) (vendored as `de.tilman_neumann.jml`) | integer factorization, prime numbers, partitions, modular arithmetic, quadratic residues | GNU GPL v3 |

This module is the reason a distribution containing it has to be published under the GPL.

#### matheclipse-graphtheory

| Project | Purpose | License |
| --- | --- | --- |
| [JGraphT](https://jgrapht.org/) | graph data structures and graph algorithms | EPL 2.0 or LGPL 2.1 |

#### matheclipse-image

| Project | Purpose | License |
| --- | --- | --- |
| [BoofCV](https://boofcv.org/) | image processing and computer vision | Apache License 2.0 |
| [Hipparchus](https://www.hipparchus.org/) | numerics and statistics for image operations | Apache License 2.0 |
| [JSVG](https://github.com/weisJ/jsvg) | SVG rendering | MIT License |
| [TwelveMonkeys ImageIO](https://github.com/haraldk/TwelveMonkeys) | additional raster image formats (JPEG, BMP, TIFF, WebP, PNM) | BSD 3-Clause License |

#### matheclipse-astro

| Project | Purpose | License |
| --- | --- | --- |
| [Orekit](https://www.orekit.org/) | astronomy and space flight dynamics | Apache License 2.0 |
| [Hipparchus](https://www.hipparchus.org/) | linear algebra and geometry | Apache License 2.0 |

#### matheclipse-bio

| Project | Purpose | License |
| --- | --- | --- |
| [BioJava](https://biojava.org/) | bioinformatics, sequence alignment, amino acid properties | LGPL 2.1 |

#### matheclipse-chem

| Project | Purpose | License |
| --- | --- | --- |
| [Chemistry Development Kit (CDK)](https://cdk.github.io/) | chemoinformatics, molecular formulas, SMILES/SMARTS, structure diagrams | LGPL 2.1 or later |

#### matheclipse-nlp

| Project | Purpose | License |
| --- | --- | --- |
| [ICU4J](https://icu.unicode.org/) | Unicode, locale, transliteration and unit data | Unicode License v3 |

#### matheclipse-dataset

| Project | Purpose | License |
| --- | --- | --- |
| [Tablesaw](https://github.com/jtablesaw/tablesaw) (vendored fork) | dataframes and table operations | Apache License 2.0 |
| [Apache Arrow](https://arrow.apache.org/) | columnar in-memory data format | Apache License 2.0 |
| [Apache POI](https://poi.apache.org/) | Excel import and export | Apache License 2.0 |
| [fastutil](https://fastutil.di.unimi.it/) | primitive type collections | Apache License 2.0 |
| [RoaringBitmap](https://github.com/RoaringBitmap/RoaringBitmap) | compressed bitsets | Apache License 2.0 |
| [ICU4J](https://icu.unicode.org/) | locale aware parsing and formatting | Unicode License v3 |
| [jsoup](https://jsoup.org/) | HTML table import | MIT License |

#### matheclipse-compile

| Project | Purpose | License |
| --- | --- | --- |
| [Janino](https://janino-compiler.github.io/janino/) | runtime Java compiler | BSD 3-Clause License |
| [JavaPoet](https://github.com/square/javapoet) | Java source code generation | Apache License 2.0 |

#### matheclipse-script

| Project | Purpose | License |
| --- | --- | --- |
| [apfloat](https://github.com/mtommila/apfloat) | arbitrary precision arithmetic | MIT License |

This module also depends on `matheclipse-gpl` and therefore inherits the GPL.

#### matheclipse-md2html

| Project | Purpose | License |
| --- | --- | --- |
| [commonmark-java](https://github.com/commonmark/commonmark-java) | Markdown parsing and HTML rendering | BSD 2-Clause License |

#### matheclipse-discord

| Project | Purpose | License |
| --- | --- | --- |
| [Discord4J](https://discord4j.com/) | Discord bot client | LGPL 3.0 |

#### matheclipse-io

This module aggregates `image, nlp, astro, gpl, bio, chem, graphtheory, compile` and `dataset` and therefore inherits all libraries listed above. In addition it bundles the JavaScript libraries listed in the License section, including KaTeX for math typesetting.

[1]: mailto:axelclk@gmail.com
[2]: http://www.vogella.com/tutorials/EclipseGit/article.html

<a href = https://github.com/axkr/symja_android_library/graphs/contributors>
  <img src = https://contrib.rocks/image?repo=axkr/symja_android_library>
</a>
