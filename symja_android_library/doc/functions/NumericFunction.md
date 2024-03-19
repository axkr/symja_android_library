## NumericFunction

```
NumericFunction
```

> is an attribute for a symbol `f` to denote that the result of `f(arg1, arg2, ...)` can be treated as a numeric value provided that each `argN` is a numeric value.
          
### Examples

The `NumericFunction` is set for the `LogisticSigmoid` function:
  
```
>> Attributes(LogisticSigmoid)
{Listable,NumericFunction,Protected}

>> LogisticSigmoid(0.5)
0.6224593312018546

>> LogisticSigmoid({-2/10, 1/10, 3/10}) // N
{0.450166,0.524979,0.574443}
```
