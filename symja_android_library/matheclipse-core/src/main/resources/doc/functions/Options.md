## Options

```
Options(symbol)
```

> gives a list of optional arguments to `symbol` and their default values.  

### Examples

You can assign values to 'Options' to specify options.
    
```
>> Options(f) = {n -> 2}
{n->2}

>> Options(f)
{n->2}

>> f(x_, OptionsPattern(f)) := x ^ OptionValue(n)

>> f(x)
x^2

>> f(x, n -> 3)
x^3
```

Delayed option rules are evaluated just when the corresponding 'OptionValue' is called:

```
>> f[a :> Print["value"]] /. f[OptionsPattern[{}]] :> (OptionValue[a]; Print["between"]; OptionValue[a]);
 value
 between
 value
```

In contrast to that, normal option rules are evaluated immediately:

```
>> f[a -> Print["value"]] /. f[OptionsPattern[{}]] :> (OptionValue[a]; Print["between"]; OptionValue[a]);
 value
 between
```

Options must be rules or delayed rules:

```
>> Options[f] = {a}
 {a} is not a valid list of option rules.
{a}
```

A single rule need not be given inside a list:

```
>> Options[f] = a -> b
a -> b

>> Options[f]
{a :> b}
```

Options can only be assigned to symbols:

```
>> Options[a + b] = {a -> b}
 Argument a + b at position 1 is expected to be a symbol.
{a -> b}
```

### Related terms 
[OptionValue](OptionValue.md), [FilterRules](FilterRules.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Options](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1189) 
