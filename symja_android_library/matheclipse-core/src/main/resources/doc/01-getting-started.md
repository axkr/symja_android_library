 
- [Basic calculations](#basic-calculations) 
- [Tutorial](#tutorial) 
- [Reference](#reference) 

## Basic calculations

Symja can be used to calculate basic stuff:

```
>> 1 + 2
```

To submit a command to Symja, press Shift+Return in the Web interface or Return in the console interface. The result will be printed in a new line below your query.

Symja understands all basic arithmetic operators and applies the usual operator precedence. Use parentheses when needed:

```
>> 1 - 2 * (3 + 5) / 4
```

The multiplication can be omitted:

```
>> 1 - 2 (3 + 5) / 4
```

But function `f(x)` notation isn't interpreted as `f*(x)`

```
>> f(x)
```

Powers expressions like ${3}^{4}$ can be entered using ^:

```
>> 3 ^ 4
```

Integer divisions yield rational numbers like $\frac{6}{4}$ :

```
>> 6 / 4
```

To convert the result to a floating point number, apply the function `N`:

```
>> N(6 / 4)
```

For integer number bases other than `10` the `^^` operator can be used. Here's an example for a hexadecimal number:

```
>> 16^^abcdefff
2882400255
```

The number can be converted back to hexadecimal form with the `BaseForm` function:

```
>> BaseForm(2882400255, 16)
```

As you can see, functions are applied using the identifier `N` and parentheses `(` and `)`. 
In general an identifier is a user-defined or built-in name for a variable, function or constant. 
Only the identifiers which consists of only one character are case sensitive. 
For all other identifiers the input parser doesn't distinguish between lower and upper case characters.

For example: the upper-case identifiers [D](functions/D.md), [E](functions/E.md), [I](functions/I.md), [N](functions/N.md), 
are different from the identifiers `d, e, i, n`, whereas the 
functions like [Factorial](functions/Factorial.md), [Integrate](functions/Integrate.md) can be entered as 
`factorial(100)` or `integrate(sin(x),x)`. If you type `SIN(x)` or `sin(x)`, 
Symja assumes you always mean the same built-in [Sin](functions/Sin.md) function.  

Symja provides many common mathematical functions and constants, e.g.:

```
>> Log(E)

>> Sin(Pi)

>> Cos(0.5)
```

When entering floating point numbers in your query, Symja will perform a numerical evaluation and present a numerical result, pretty much like if you had applied N.

Of course, Symja has complex numbers and uses the equation:

$$\sqrt{-1}=I$$

In Symja the imaginary unit is represented by the uppercase letter `I`:

```
>> Sqrt(-4)

>> I ^ 2

>> (3 + 2*I) ^ 4

>> (3 + 2*I) ^ (2.5 - I)

>> Tan(I + 0.5)
```

`Abs` calculates absolute values:

```
>> Abs(-3)

>> Abs(3 + 4*I)
```

Symja can operate with pretty huge numbers:

```
>> 100!
```

(! denotes the factorial function.) The precision of numerical evaluation can be set:

```
>> N(Pi, 100)
```

Division by zero is forbidden:

```
>> 1 / 0
```

Other expressions involving `Infinity` are evaluated:

```
>> Infinity + 2 Infinity
```

In contrast to combinatorial belief, ${0}^{0}$ is undefined:

```
>> 0 ^ 0
```

The result of the previous query to Symja can be accessed by %:

```
>> 3 + 4

>> % ^ 2
```

In the console available functions can be determined with the `?` operator

```
>> ?ArcC*
ArcCos, ArcCosh, ArcCot, ArcCoth, ArcCsc, ArcCsch
```

Documentation can be displayed by asking for information for the function name.

```
>> ?Integrate
```

## Tutorial

The following sections are introductions to the basic principles of the Symja language. 
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
* [Plotting graphs and functions](10-plotting.md)
* [Curve sketching](11-curve-sketching.md)
* [Linear algebra](12-linear-algebra.md)
* [Semantic import and Datasets](20-semantic-import.md)

## Reference

* [Expression types](97-expression-types.md) 
* [Function by category](98-function-by-category.md)
* [Reference of functions and built-in symbols](99-function-reference.md)
