## Symja Library - Java Symbolic Math System

**Note**: this repository contains the **Java 8 project**. The **Android library project** can be found in the [SymjaAndroid repository](https://bitbucket.org/axelclk/symjaandroid).

###Features:

* arbitrary precision integers, rational and complex numbers
* differentiation, integration, equation solving, polynomial and linear algebra functions...
* a general purpose Term Rewriting System and Pattern Matching engine
* use human readable math expression strings or the internal abstract syntax tree (AST) representation to code in Java.
   
### Online demo: 

* [Mobile web interface symjaweb.appspot.com](http://symjaweb.appspot.com/)  
* [Notebook interface symjaweb.appspot.com/new.jsp](http://symjaweb.appspot.com/new.jsp)

###Examples

```
>>> 24/60
2/5

>>> N(24/60)
0.4

>>> sin(30*degree)
1/2

>>> sin(pi/2)
1

>>> a+a+4*b^2+3*b^2
2*a+7*b^2

>>> solve({x^2-11==y, x+y==-9}, {x,y})
{{x->-2,y->-7},{x->1,y->-10}}

>>> dsolve({y'(x)==y(x)+2,y(0)==1},y(x), x)
{{y(x)->-2+3*E^x}}

>>> integrate(cos(x)^5, x)
-2/3*Sin(x)^3+Sin(x)^5/5+Sin(x)

>>> D(sin(x^3), x)
3*x^2*Cos(x^3)

>>> factor(-1+x^16)
(-1+x)*(1+x)*(1+x^2)*(1+x^4)*(1+x^8)

>>> factor(5+x^12, Modulus->7)
(2+x^3)*(4+x^6)*(5+x^3)

>>> expand((-1+x)*(1+x)*(1+x^2)*(1+x^4)*(1+x^8))
-1+x^16

>>> det({{1,2},{3,4}})
-2

>>> inverse({{1,2},{3,4}})
{{-2,1},
 {3/2,-1/2}}

>>> factorinteger(2^15-5)
{{3,1},{67,1},{163,1}}
```

### Getting started

First, you'll need a Java Development Kit compatible with Java 8 or later.

You can find JDK installers at:

* http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

If you're unsure how to install the JDK, you can find instructions for
all operating systems here: 

* https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html

Pay careful attention to anything about setting up your `PATH` or `CLASSPATH`.

Install and open the latest version of the Eclipse development IDE:

* http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/mars1
  
### BitBucket GIT

a) Fork the Symja repository to use as a starting point.

* Navigate to https://bitbucket.org/axelclk/symja_android_library/fork in your browser.
* Click the "Fork" button in the top-right of the page.
* Once your fork is ready, open the new repository's "Settings" by clicking the link in the menu bar on the left.
* Change the repository name to the name of your Library and save your changes.
  
b) Clone your new repository to your Eclipse workspace.

* Open Eclipse and select the File → Import... menu item.
* Select Git → Projects from Git, and click "Next >".
* Select "URI" and click "Next >". 
* Enter your repository's clone URL in the "URI" field. The remaining fields in the "Location" and "Connection" groups will get automatically filled in.
* Enter your BitBucket credentials in the "Authentication" group, and click "Next >".
* Select the `master` branch on the next screen, and click "Next >".
* The default settings on the "Local Configuration" screen should work fine, click "Next >".
* Make sure "Import existing projects" is selected, and click "Next >".
* Eclipse should find and select the `symja_android_library` automatically, click "Finish".
  

See also the "Getting started with Symja" document on the BitBucket Wiki pages:

* [Symja wiki pages](https://bitbucket.org/axelclk/symja_android_library/wiki)

###Contact

If you have any questions about using or developing for this project, shoot me
an [email][1]!

###License

* the Symja source code is published under the LESSER GNU GENERAL PUBLIC LICENSE Version 3 

[1]: mailto:axelclk@gmail.com
[2]: http://www.vogella.com/tutorials/EclipseGit/article.html
