## Tally

```
Tally(list)
```

or 

```
Tally(list, binary-predicate)
```

> return the elements and their number of occurrences in `list` in a new result list. The `binary-predicate` tests if two elements are equivalent. `SameQ` is used as the default `binary-predicate`.

### Examples

```
>> str="The quick brown fox jumps over the lazy dog";

>> Tally(Characters(str)) // InputForm
{{"T",1},{"h",2},{"e",3},{" ",8},{"q",1},{"u",2},{"i",1},{"c",1},{"k",1},{"b",1},{"r",2},{"o",4},{"w",1},{"n",1},{"f",1},{"x",1},{"j",1},{"m",1},{"p",1},{"s",1},{"v",1},{"t",1},{"l",1},{"a",1},{"z",1},{"y",1},{"d",1},{"g",1}}
```

### Related terms 
[Commonest](Commonest.md), [Counts](Counts.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Tally](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ListFunctions.java#L7216) 
