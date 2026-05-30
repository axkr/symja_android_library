## BooleanCountingFunction

```
BooleanCountingFunction(spec, vars)
```

> Returns a boolean function (in disjunctive normal form) in the given `vars` which evaluates to `True` exactly when the number of `True` variables matches `spec`.
 
### Examples

``` 
>> BooleanCountingFunction({2, 3}, {a, b, c, d})
(a&&b&&!c)||(a&&!b&&c)||(a&&!b&&d)||(!a&&b&&d)||(!a&&c&&d)||(b&&c&&!d)
```
