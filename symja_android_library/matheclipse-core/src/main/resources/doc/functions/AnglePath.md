## AnglePath

```
AnglePath({phi1, phi2, ...})
```

> returns the points formed by a turtle starting at `{0, 0}` and angled at `0` degrees going through the turns given by angles`phi1, phi2, ...` and using distance `1` for each step.

```
AnglePath({{r1, phi1}, {r2, phi2}, ...})
```

> instead of using `1` as distance, use `$r1$, $r2$, ...` as distances for the respective steps.
      
```
AnglePath({{x, y}, phi0}, {phi1, phi2, ...})
```

> specifies initial position `{x, y}` and initial direction `phi0`.
 
### Examples

``` 
>> AnglePath({90*Degree, 90*Degree, 90*Degree, 90*Degree})
{{0, 0}, {0, 1}, {-1, 1}, {-1, 0}, {0, 0}}

>> AnglePath({{1, 1}, 90*Degree}, {{1, 90*Degree}, {2, 90*Degree}, {1, 90*Degree}, {2, 90*Degree}})
{{1, 1}, {0, 1}, {0, -1}, {1, -1}, {1, 1}}

>> AnglePath({a, b})
{{0, 0}, {Cos(a), Sin(a)}, {Cos(a) + Cos(a+b), Sin(a) + Sin(a+b)}}

>> Graphics(Line(AnglePath(Table(1.7, {50}))))
-Graphics-

>> Graphics(Line(AnglePath(RandomReal({-1, 1}, {100}))))
-Graphics-
```


### Implementation status

* &#x2705; - full supported

### Github

* [Implementation of AnglePath](https://github.com/axkr/symja_android_library/blob/master/symja_android_library/matheclipse-core/src/main/java/org/matheclipse/core/builtin/ExpTrigsFunctions.java#L132) 
