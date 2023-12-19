 
- [Basic calculations](#basic-calculations)
- [Tutorial](#tutorial) 
- [Reference](#reference) 

## Basic calculations

Symja can be used to calculate basic stuff:

```
>> 1 + 2
3
```

To submit a command to Symja, press Shift+Return in the Web interface or Return in the console interface. The result will be printed in a new line below your query.

Symja understands all basic arithmetic operators and applies the usual operator precedence. Use parentheses when needed:

```
>> 1 - 2 * (3 + 5) / 4
-3
```

The multiplication can be omitted:

```
>> 1 - 2 (3 + 5) / 4
-3
```

But function `f(x)` notation isn't interpreted as `f*(x)`

```
>> f(x)
```

Powers expressions like ${3}^{4}$ can be entered using ^:

```
>> 3 ^ 4
81
```

Integer divisions yield rational numbers like $\frac{6}{4}$ :

```
>> 6 / 4
3/2
```

To convert the result to a floating point number, apply the function `N`:

```
>> N(6 / 4)
1.5
```

For integer number bases other than `10` the `^^` operator can be used. Here's an example for a hexadecimal number:

```
>> 16^^abcdefff
2882400255
```

The number can be converted back to hexadecimal form with the `BaseForm` function:

```
>> BaseForm(2882400255, 16)
Subscript[abcdefff,16]
```

### Symbols with upper and lower case characters

Functions are applied using an identifier and parentheses `(` and `)`. 
In general an identifier is a user-defined or built-in name for a variable, function or constant.

Only the identifiers which consists of only one character are case sensitive. 
For all other identifiers the input parser doesn't distinguish between lower and upper case characters.

For example: the upper-case identifiers [D](functions/D.md), [E](functions/E.md), [I](functions/I.md), [N](functions/N.md), 
are different from the identifiers `d, e, i, n`, whereas the functions like [Factorial](functions/Factorial.md), [Integrate](functions/Integrate.md) can be entered as `factorial(100)` or `integrate(sin(x),x)`. 
If you type `SIN(x)` or `sin(x)`, Symja assumes you always mean the same built-in [Sin](functions/Sin.md) function.  

Symja provides many common mathematical functions and constants.

For example [Log](functions/Log.md) calculates the natural logarithm for base [E](functions/E.md). [Log2](functions/Log2.md) and [Log10](functions/Log10.md) are variants for logarithm to the bases `2` and `10`.

```
>> Log(E)
1

>> Sin(Pi)
0

>> Cos(0.5)
0.877583
```

When entering floating point numbers in your query, Symja will perform a numerical evaluation and present a numerical result, pretty much like if you had applied N.

### Complex numbers

Of course, Symja has complex numbers and uses the equation:

$$\sqrt{-1}=I$$

In Symja the imaginary unit is represented by the uppercase letter [I](functions/I.md):

```
>> Sqrt(-4)
2*I

>> I ^ 2
-1

>> (3 + 2*I) ^ 4
-119+I*120

>> (3 + 2*I) ^ (2.5 - I)
43.663+I*8.28556

>> Tan(I + 0.5)
0.195577+I*0.842966
```

[Abs](functions/Abs.md) calculates absolute values for complex numbers:

```
>> Abs(-3)
3

>> Abs(3 + 4*I)
5
```

Symja can operate with pretty huge numbers:

```
>> 100!
9332621544394415268169923885626670049071596826438162146859296389521759999322991\
5608941463976156518286253697920827223758251185210916864000000000000000000000000
```

(! denotes the factorial function.) The precision of numerical evaluation can be set:

```
>> N(Pi, 100)
3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067
```

Division by zero is forbidden and prints the message `Power: Infinite expression 1/0 encountered.`

```
>> 1 / 0
ComplexInfinity
```

Other expressions involving `Infinity` are evaluated:

```
>> Infinity + 2*Infinity
Infinity
```

In contrast to combinatorial belief, `{0}^{0}` is undefined and prints the message `Power: Indeterminate expression 0^0 encountered.`:

```
>> 0 ^ 0
Indeterminate
```

The result of the previous query to Symja can be accessed by %:

```
>> 3 + 4
7

>> % ^ 2
49
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
