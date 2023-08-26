## Permute

```
Permute(list, Cycles({permutation-cycles}))
```

> permutes the `list` from the cycles in `permutation-cycles`.

```
Permute(list, permutation-list)
```

> permutes the `list` from the permutations defined in `permutation-list`.

See 
* [Wikipedia - Permutation](https://en.wikipedia.org/wiki/Permutation)
	 
### Examples

```
>> Permute(CharacterRange("v", "z"), Cycles({{1, 5, 3}}))
{x,w,z,y,v}
```

### Related terms 
[Cycles](Cycles.md), [FindPermutation](FindPermutation.md), [PermutationCycles](PermutationCycles.md), [PermutationCyclesQ](PermutationCyclesQ.md), [PermutationList](PermutationList.md), [PermutationListQ](PermutationListQ.md), [PermutationReplace](PermutationReplace.md), [Permutations](Permutations.md)






### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of Permute](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/Combinatoric.java#L1575) 
