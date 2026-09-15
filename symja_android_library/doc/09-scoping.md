## Scoping

By default, all symbols are “global” in Symja, i.e. they can be read and written in any part of your program.
However, sometimes “local” variables are needed in order not to disturb the global namespace. 
Symja provides two ways to support this:

* lexical scoping by `Module`, and
* dynamic scoping by `Block`.

```
Module({vars}, expr)
```

> localizes variables by giving them a temporary name of the form `name$number`, where number is the current internal Symja value of "module number". Each time a module is evaluated, "module number" is incremented.

```
Block({vars}, expr)
```

> temporarily stores the definitions of certain variables, evaluates `expr` with reset values and restores the original definitions afterwards.

Both scoping constructs shield inner variables from affecting outer ones:

```
>> t = 3
3

>> Module({t}, t = 2)
2

>> Block({t}, t = 2)
2

>> t
3
```

`Module` creates new variables:

```
>> y = x ^ 3;
>> Module({x = 2}, x * y)
2*x^3
```

`Block` does not:

```
>> Block({x = 2}, x * y)
16
```

Thus, `Block` can be used to temporarily assign a value to a variable:

```
>> expr = x ^ 2 + x;
>> Block({x = 3}, expr)
12

>> x
x
```

It is common to use scoping constructs for function definitions with local variables:

```
>> fac(n_) := Module({k, p}, p = 1; For(k = 1, k <= n, ++k, p *= k); p)
>> fac(10)
3628800

>> 10!
3628800
```

### Formal symbols

The formal symbols `\[FormalA]`, ..., `\[FormalZ]` and `\[FormalCapitalA]`, ..., `\[FormalCapitalZ]` are `Protected` symbols in the `System` context which never hold a value. Symja's built-in rules use them as local variables, so that one evaluation can't see the value of another. They print as their plain letter, and `InputForm` shows the name which reads back as the formal symbol.

Every scoping construct and iterator localizes a formal symbol:

```
>> Sum(\[FormalK], {\[FormalK], 1, 10})
55

>> Block({\[FormalK] = 3}, \[FormalK])
3

>> Function(\[FormalK], \[FormalK]^2)[5]
25
```

A global definition is refused:

```
>> \[FormalK] = 10
Set: Symbol k is Protected.
10

>> \[FormalK]
k

>> {Context(\[FormalK]), \[FormalK] === k}
{System`,False}

>> InputForm(\[FormalK] + 1)
1 + \[FormalK]
```
