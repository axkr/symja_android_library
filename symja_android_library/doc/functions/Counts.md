## Counts

```
Counts({elem1, elem2, elem3, ...})
```

> count the number of each distinct element in the list `{elem1, elem2, elem3, ...}` and return the result as an association `<|elem1->counter1, ...|>`.

### Examples

```
>> Counts({1,2,3,4,5,6,7,8,9,7,5,4,5,6,7,3,2,1,3,4,5,2,2,2,3,3,3,3,3})
<|1->2,2->5,3->8,4->3,5->4,6->2,7->3,8->1,9->1|>
```

### Related terms 
[Commonest](Commonest.md), [Tally](Tally.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Counts](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/AssociationFunctions.java#L534) 
