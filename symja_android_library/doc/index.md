## Basic calculations

Symja can be used to calculate basic stuff:

```
1 + 2
```

To submit a command to Symja, press Shift+Return in the Web interface or Return in the console interface. The result will be printed in a new line below your query.

Symja understands all basic arithmetic operators and applies the usual operator precedence. Use parentheses when needed:
```
1 - 2 * (3 + 5) / 4
```

The multiplication can be omitted:
```
1 - 2 (3 + 5) / 4
```

But function `f(x)` notation isn't interpreted as `f*(x)`
```
f(x)
```

Powers can be entered using ^:
```
3 ^ 4
```

Integer divisions yield rational numbers:
```
6 / 4
```

To convert the result to a floating point number, apply the function `N`:
```
N(6 / 4)
```

As you can see, functions are applied using the identifier `N` and braces `(` and `)`. 
In general an identifier is a user-choosen or built-in name for a variable, function or constant. 
Only the identifiers which consists of only one character are case sensitive. 
For all other identifiers the input parser doesn't distinguish between lower and upper case characters.

For example: the upper-case identifiers `[D](functions/D.md),[E](functions/E.md),[I](functions/I.md),[N](functions/N.md)`, 
are different from the identifiers `d, e, i, n`, thereas the 
functions like `[Factorial](functions/Factorial.md),[Integrate](functions/Integrate.md)` can be entered as 
`factorial(100)` or `integrate(sin(x),x)`. If you type `SIN(x)` or `sin(x)`, 
Symja assumes you always mean the same built-in `[Sin](functions/Sin.md)function.  

Symja provides many common mathematical functions and constants, e.g.:
```
Log(E)

Sin(Pi)

Cos(0.5)
```
When entering floating point numbers in your query, Symja will perform a numerical evaluation and present a numerical result, pretty much like if you had applied N.

Of course, Symja has complex numbers:
```
Sqrt(-4)

I ^ 2

(3 + 2*I) ^ 4

(3 + 2*I) ^ (2.5 - I)

Tan(I + 0.5)
```

Abs calculates absolute values:
```
Abs(-3)

Abs(3 + 4*I)
```

Symja can operate with pretty huge numbers:
```
100!
```

(! denotes the factorial function.) The precision of numerical evaluation can be set:

```
N(Pi, 100)
```

Division by zero is forbidden:
```
1 / 0
```

Other expressions involving Infinity are evaluated:
```
Infinity + 2 Infinity
```

In contrast to combinatorial belief, 0^0 is undefined:
```
0 ^ 0
```

The result of the previous query to Symja can be accessed by %:
```
3 + 4
```

## Expression types

| Type          		| Description         																							| Input Example 						|
|:---------------------:|:-------------------------------------------------------------------------------------------------------------:|:-------------------------------------:|
| Integer				| integer numbers   																							| `42`  								|
| Rational				| rational numbers        																						| `13/17` 								|
| Complex				| complex numbers      			 																				| `2+I*1/3`    							|
| Real					| double values  																								| `0.5`  								|
| Complex Real			| complex double values  																						| `0.5-I*0.25`  						|
| Evaluation Precedence	| control precedence with `(...)`  																				| `(a+b)*c`  							|
| Lists					| comma separated list of elements which are sourrounded by `{ ... }`  											| `{a, b, c, d} `  						|
| Vectors				| vectors are like list, but cannot contain sublists `{ ... }`  												| `{1, 2, 3, 4}`  						|
| Matrices				| a matrix contains the rows as sublists 																		| `{{1, 2}, {3, 4}}`  					|
| Predefined Functions	| predefined function names start with an upper case character and the arguments are surrounded by `( ... )`	| `Sin(0), PrimeQ(13)` 					|
| Predefined Constants	| predefined constant names start with an upper case character 													| `Degree, E, Pi, False, True, ... `	|
| Userdefined variables	| identifiers which you would like to assign a value start with a `$` character 								| `$a=42`  								|
| Userdefined rules		| identifiers which you would like to assign a rule start with a `$` character  								| `$f(x_,y_):={x,y}`  					|
| Pattern Symbols		| patterns end with a preceding `_` and could have a constraint 												| `$f(x_IntegerQ):={x}`  				|
| Strings				| character strings are enclosed by double quote characters  													| `"Hello World"`  						|
| Slots					| a `#` character followed by an optional number 																| `#` or `#2`   						|
| Pure Functions		| pure functions can be expressed with the `&` operator															| `(#^3)&[x]`  gives `x^3` 				|
| Parts of an expression| `expr[[index]]`   																							| `{a, b, c, d}[[2]]`  gives `b`		|



