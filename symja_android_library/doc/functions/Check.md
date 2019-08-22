## Check

```
Check(expr, failure)
```
 
> evaluates `expr`, and returns the result, unless messages were generated, in which case  `failure` will be returned.

### Examples
 
```
>> Check(2^(3), err)
8
```

`0^(-42)` prints message: "Power: Infinite expression 1/0^42 encountered."

```
>> Check(0^(-42), failure)
failure
```