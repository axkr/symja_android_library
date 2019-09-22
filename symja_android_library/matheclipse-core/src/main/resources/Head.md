## Head

```
Head(expr)
```

> returns the head of the expression or atom `expr`.

### Examples

```
>> Head(a * b)
Times

>> Head(6)
Integer

>> Head(6+I)
Complex

>> Head(6.0)
Real

>> Head(6.0+I)
Complex

>> Head(3/4)
Rational

>> Head(x)
Symbol
```