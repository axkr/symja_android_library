## Symbolic math scripting with JShell

[JShell](https://docs.oracle.com/javase/10/tools/jshell.htm) is a Read-Evaluate-Print-Loop (REPL), a command line tool that allows you to enter Java statements (simple statements, compound statements, or even full methods and classes), evaluate them, and print the result. We'll go through an example that will show you how to use JShell to explore the [Symja computer algebra library](https://github.com/axkr/symja_android_library).

- [Installation](#installation)
- [Example script](#example-script)  

## Installation

Download the latest [Symja release](https://github.com/axkr/symja_android_library/releases) and unzip the files for example in  a sub-directory named  `/symja`

In this directory you can run the [symja-jshell.bat](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/symja-jshell.bat) batch file under Windows. Please adjust the `JAVA_HOME` path for your environment to a JDK installation greater equal than Java 9.

```
  SET "JAVA_HOME=C:\Program Files\Java\jdk-10.0.1"
  "%JAVA_HOME%\bin\jshell" --class-path "lib/*" --startup start-symja.jsh
```
  
By running the `symja-jshell.bat` file a [start-symja.jsh](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/start-symja.jsh) default scripting file with some additional imports will be executed:

```
...
import org.matheclipse.core.expression.*;
import org.matheclipse.core.eval.*;
import org.matheclipse.core.interfaces.*;
import static org.matheclipse.core.expression.F.*;
 
```

* With the static imports of the [F.class](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/expression/F.java) it's possible to use the formal symbols `a,b,c,...x,y,z` symbolically and to call functions like [Integrate](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Integrate.md), [D](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/D.md) or [FactorInteger](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/FactorInteger.md) and to omit the `F.` prefix. 
* With the predefined `eval` method it's possible to evaluate a math string expression like for example `eval("D(Sin(x),x)")` 

## Example script
 
 
```
C:\temp\symja>symja-jshell
|  Welcome to JShell -- Version 10.0.1
|  For an introduction type: /help intro

jshell> D.of(Sin(x),x)  
$1 ==> Cos(x)

jshell> eval("1+1")
$2 ==> 2

jshell> Integrate.of(Times(Sin(x),Cos(x)),x)
$3 ==> -Cos(x)^2/2

jshell> FactorInteger.of(ZZ(324))
$4 ==> {{2,2},{3,4}}

jshell> HornerForm.of(Expand(Power(Plus(x,y),C3)))
$5 ==> x*(x*(x+3*y)+3*y^2)+y^3

jshell> TeXForm.of(Sum(unary(f,n),List(n,C1,m)))
$6 ==> \sum_{n = 1}^{m} {f(n)}

jshell> Definition.of(ArcTan)
$7 ==> Attributes(ArcTan)={Listable,NumericFunction}
ArcTan(Sqrt(5-2*Sqrt(5)))=Pi/5
...
ArcTan(0)=0
ArcTan(1,1)=Pi/4
ArcTan(Infinity)=Pi/2
...

jshell> ArcTan.of(C1,C1)
$8 ==> Pi/4

jshell> ISymbol xx=Dummy("xx")
xx ==> xx

jshell> Set.of(xx, Plus(x,y))  // assign x+y to variable xx
$10 ==> x+y

jshell> Definition.of(xx)
$11 ==> Attributes(xx)={}
xx=x+y

jshell> eval(xx)
$12 ==> x+y

jshell> usage(Im)  // print documentation for Im() function
$13 ==>
## Im

Im(z)
> returns the imaginary component of the complex number `z`.

### Examples
>> Im(3+4I)
4

>> Im(0.5 + 2.3*I)
2.3


jshell> eval("D(Sin(x),x)")
$14 ==> Cos(x)

jshell> /exit
|  Goodbye 
```
