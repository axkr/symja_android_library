## WordBoundary

```
WordBoundary
```

> represents the boundary between words.

### Examples

```
>> StringReplace("apple banana orange artichoke", "e" ~~ WordBoundary -> "E") 
applE banana orangE artichokE
```