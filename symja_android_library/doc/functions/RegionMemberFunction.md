## RegionMemberFunction

```
RegionMemberFunction(reg)
```

> represents the membership test of the region `reg` as a function object.

```
RegionMemberFunction(reg)[p]
```

> returns `True` if the point `p` lies in the region `reg`, and `False` if it doesn't.

```
RegionMemberFunction(reg)[{p1, p2, ...}]
```

> returns the list of the results for the points `p1, p2, ...`.

Applied to a point, the function gives the same result as [RegionMember](RegionMember.md)`(reg, p)`: for a point with symbolic coordinates it gives the condition which the coordinates have to satisfy. `RegionMember(reg)` returns the same function object. For an expression which isn't a supported region the test stays unevaluated.

### Examples

```
>> f = RegionMemberFunction(Disk())
RegionMemberFunction(Disk({0,0}))

>> f({1/2, 1/2})
True

>> f({2, 0})
False

>> f({{0, 0}, {1, 1}, {2, 2}})
{True,False,False}

>> f({x, y})
x^2+y^2<=1
```

```
>> RegionMemberFunction(Ball({0,0,0},2))[{{1,1,1},{2,2,2}}]
{True,False}

>> Select({{0,0},{1,1},{1/2,0},{2,0}}, RegionMemberFunction(Disk()))
{{0,0},{1/2,0}}
```

### Related terms
[RegionMember](RegionMember.md), [Select](Select.md)

### Implementation status

* &#x2611; - partially implemented

### Github

* [Implementation of RegionMemberFunction](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/reflection/system/RegionMemberFunction.java)
