## All

```
All
```

> is a value for a number of functions indicating to include everything. For example it is a possible value for `Span`, `Part`  and `Quiet`. 

## Examples

In [Part](Part.md) `All`, extracts into a first column vector the first element of each of the list elements:

```
>> {{1, 3}, {5, 7}}[[All, 1]]
{1,5}
```
   
While in [Take](Take.md) `All`, extracts as a column matrix the first element as a list for each of the list elements:

```
>> Take({{1, 3}, {5, 7}}, All, {1})
{{1},{5}}
```

### Related terms 
[Part](Part.md), [Quiet](Quiet.md), [Span](Span.md), [Take](Take.md)