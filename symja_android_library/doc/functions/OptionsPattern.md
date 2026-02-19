## OptionsPattern

```
OptionsPattern(x)
```

> is a pattern that stands for a sequence of options given to a function, with default values taken from `Options(x)`. The options can be of the form `opt->value` or `opt:>value`, and might be in arbitrarily nested lists.

```
OptionsPattern({opt1->value1, ...})
```

> takes explicit default values from the given list. The list may also contain symbols `symbol`, for which `Options(symbol)` is  taken into account; it may be arbitrarily nested. `OptionsPattern({})` does not use any default values.

### Examples

The option values can be accessed using `OptionValue`.

```
>> f(x_, OptionsPattern({n->2})) := x ^ OptionValue(n)

>> f(x)
x ^ 2

>> f(x, n->3)
x ^ 3
```

Delayed rules as options:

```
>> e = f(x, n:>a)
x ^ a

>> a = 5
5

>> e
x ^ 5
```

Options might be given in nested lists:

```
>> f(x, {{{n->4}}})
x ^ 4
```

### Related terms 
[Options](Options.md), [OptionValue](OptionValue.md), [FilterRules](FilterRules.md)




 

### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of OptionsPattern](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1414) 
