## Symja Library - Java Symbolic Math System

Features:

* arbitrary precision integers, rational and complex numbers
* differentiation, integration, polynomial and linear algebra functions...
* a general purpose Term Rewriting System and Pattern Matching engine
* use human readable math expression strings or the internal abstract syntax tree (AST) representation to code in Java.
* see the "Getting started with Symja" document on the Wiki pages
   
Online demo: 

* [Mobile web interface symjaweb.appspot.com](http://symjaweb.appspot.com/)  
* [Notebook interface symjaweb.appspot.com/new.jsp](http://symjaweb.appspot.com/new.jsp)

See the Wiki pages:

* [Symja wiki pages](https://bitbucket.org/axelclk/symja_android_library/wiki)
	
axelclk_AT_gmail_DOT_com 

License

* the Symja source code is published under the LESSER GNU GENERAL PUBLIC LICENSE Version 3
* the Symja parser (and simple numeric evaluators) are published under the APACHE LICENSE Version 2.0.
* the Apache Commons Mathematics Library is published under Apache software licence
* the JAS Java Algebra System is published under the (LESSER) GNU GENERAL PUBLIC LICENSE licence 

Examples

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

>>> solve(a*x + b == 0, x)
{{x->-b/a}}

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