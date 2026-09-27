## SetSystemOptions

```
SetSystemOptions(name -> value)
```

> resets the internal system option `name` to `value`.

```
SetSystemOptions({name1 -> value1, name2 -> value2, ...})
```

> resets several system options at once.

The system options are the ones [SystemOptions](SystemOptions.md) reports. None of them is stored
here, so a known name is accepted and given back as it was written - the way a notebook uses the
function, as a statement whose result is discarded. A name that is not a system option is a
`SetSystemOptions::sysname` message, and the call stays unevaluated.

See
* [Wolfram Documentation - SetSystemOptions](https://reference.wolfram.com/language/ref/SetSystemOptions.html)

### Examples

```
>> SetSystemOptions("DifferentiationOptions" -> {"ExcludedFunctions" -> {}})
DifferentiationOptions->{ExcludedFunctions->{}}
```

### Related terms
[SystemOptions](SystemOptions.md)

### Implementation status

* &#x1F9EA; - experimental
