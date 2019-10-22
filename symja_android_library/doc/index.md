
- [Installation](#installation)
- [Tutorial](#tutorial)
- [Reference](#reference)

## Installation

Download the latest release from 
* [github.com/axkr/symja_android_library/releases](https://github.com/axkr/symja_android_library/releases)

unzip the download in a separate folder and modify the symja.bat file to use your Java 8 installation path to run the Symja console:
```
"C:\Program Files\Java\jdk1.8.0_25\bin\java" -classpath "lib/*" org.matheclipse.core.eval.Console 
```

## Tutorial

The following sections are introductions to the basic principles of the language of Symja. 
A few examples and functions are presented. Only their most common usages are listed; 
for a full description of their possible arguments, options, etc., see their entry in the "function reference" of built-in symbols.

* [Symbols and assignments](02-symbols-and-assignments.md)
* [Comparisons and Boolean logic](03-comparisons-and-boolean-logic.md)
* [Strings](04-strings.md)
* [Lists](05-lists.md)
* [The structure of things](06-the-structure-of-things.md)
* [Functions and patterns](07-functions-and-patterns.md)
* [Control statements](08-control-statements.md)
* [Scoping](09-scoping.md)

## Reference

* [Expression types](97-expression-types.md) 
* [Function by category](98-function-by-category.md)
* [Reference of functions and built-in symbols](99-function-reference.md)