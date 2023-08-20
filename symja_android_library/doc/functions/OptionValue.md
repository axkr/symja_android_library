## OptionValue

```
OptionValue(name)
```

> gives the value of the option `name` as specified in a call to a function with `OptionsPattern`.

```
OptionValue(f, name)
```

> recover the value of the option `name` associated to the symbol `f`.

```
OptionValue(f, optvals, name)
```

> recover the value of the option `name` associated to the symbol `f`, extracting the values from `optvals` if available.

```
OptionValue(..., list)
```

> recover the value of the options in `list`.

### Examples

You can assign values to 'Options' to specify options.
    
```
>> f(a->3) /. f(OptionsPattern({})) -> {OptionValue(a)}
{3}

>> f(a->3) /. f(OptionsPattern({})) -> {OptionValue(b)}
{b}

>> f(a->3) /. f(OptionsPattern({})) -> {OptionValue(a+b)} 
{a + b}
```

However, it can be evaluated dynamically:

```
>> f(a->5) /. f(OptionsPattern({})) -> {OptionValue(Symbol("a"))}
{5}
```
     

### Related terms 
[Options](Options.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of OptionValue](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/PatternMatching.java#L1236) 
